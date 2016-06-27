package com.totvslabs.mdm.client.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.gson.internal.StringMap;
import com.totvslabs.mdm.client.pojo.FDEntityVO;
import com.totvslabs.mdm.client.pojo.FDFieldVO;
import com.totvslabs.mdm.client.ui.MDMConsumer;
import com.totvslabs.mdm.restclient.MDMRestConnectionFactory;
import com.totvslabs.mdm.restclient.command.CommandConfirmationConsumption;
import com.totvslabs.mdm.restclient.command.CommandDataConsumption;
import com.totvslabs.mdm.restclient.command.CommandQueryData;
import com.totvslabs.mdm.restclient.vo.DataConsumptionEntitiesRecordVO;
import com.totvslabs.mdm.restclient.vo.DataQueryHitVO;
import com.totvslabs.mdm.restclient.vo.DataQueryVO;
import com.totvslabs.mdm.restclient.vo.EnvelopeVO;
import com.totvslabs.mdm.restclient.vo.GoldenRecordVO;

public class ThreadExportGoodDataConnector implements Runnable {
	private FDEntityVO fdEntityVO;
	private MDMConsumer mdmConsumer;
	private List<FDFieldVO> fdFieldsVO;
	private String fluigDataURL;
	private boolean stop;

	public ThreadExportGoodDataConnector(String fluigDataURL, FDEntityVO fdEntityVO, List<FDFieldVO> fields, MDMConsumer mdmConsumer) {
		this.fdEntityVO = fdEntityVO;
		this.fdFieldsVO = fields;
		this.fluigDataURL = fluigDataURL;
		this.mdmConsumer = mdmConsumer;
	}

	@Override
	public void run() {
		if(fdEntityVO == null) {
			return;
		}

		Integer numOfPendingRecords = 0;
		Integer originalCounter = -1;
		Integer actualCounter = -1;
		
		if(mdmConsumer != null) {
			actualCounter = Integer.parseInt(mdmConsumer.getTextCounter().getText()) == -1 ? -1 : Integer.parseInt(mdmConsumer.getTextCounter().getText());
			originalCounter = actualCounter;
		}

		Integer recordByPage = 10;
		CustomFileWriter writer = null;

		try {
			writer = new CustomFileWriter(fdEntityVO.getName() + ".csv", true);

			int maxColumns = fdFieldsVO.size();
			int actualColumn = 0;
			Map<String, Map<String, String>> foreignNNKeys = new HashMap<String, Map<String, String>>();
			Map<String, List<String>> foreignHeaders = new HashMap<String, List<String>>();
			Map<String, Set<String>> foreignValues = new HashMap<String, Set<String>>();
			List<String> taxonomyFields = new ArrayList<String>();
			taxonomyFields.add("mdmmedicarespecialtycode");
			taxonomyFields.add("mdmhealthcareprovidertaxonomydescription");
			taxonomyFields.add("mdmhealthcareprovidertaxonomycode");
			taxonomyFields.add("mdmmedicareproviderdescription");

			writer.append("\"mdmid\",");

			for (FDFieldVO fdFieldVO : fdFieldsVO) {
				if((fdFieldVO.getFatherMDMName() != null && fdFieldVO.getFatherMDMName().equals("mdmaddress")) || (fdFieldVO.getForeignField() == null || !fdFieldVO.getForeignField()) && fdFieldVO.getFatherMDMName() == null) {
					writer.append("\"" + fdFieldVO.getName() + "\"");
					
					if(actualColumn < (maxColumns-1)) {
						writer.append(",");
					}
				}
				else if(fdFieldVO.getFatherMDMName() != null) {
					String foreignEntityHeaderKey = fdEntityVO.getName() + fdFieldVO.getFatherMDMName();

					List<String> listHeader = foreignHeaders.get(foreignEntityHeaderKey);

					if(listHeader == null) {
						listHeader = new ArrayList<String>();
						foreignHeaders.put(foreignEntityHeaderKey, listHeader);
						listHeader.add(fdEntityVO.getName() + "Id");
						listHeader.add("counter"+ fdFieldVO.getFatherMDMName());
					}

					listHeader.add(fdFieldVO.getName());
				}
				else if(fdFieldVO.getForeignField() != null && fdFieldVO.getForeignField()) {
					if(fdFieldVO.getName().equals("mdmaddressid")) {
						String foreignEntityHeaderKey = fdEntityVO.getName() + fdFieldVO.getName();

						List<String> listHeader = foreignHeaders.get(foreignEntityHeaderKey);//TODO: Add fields from other side here in this file. Here I can add manually.

						if(listHeader == null) {
							listHeader = new ArrayList<String>();
							foreignHeaders.put(foreignEntityHeaderKey, listHeader);
							listHeader.add(fdEntityVO.getName() + "Id");
							listHeader.add(fdFieldVO.getName() + "Id");
							listHeader.add("mdmstate");
							listHeader.add("mdmzipcode");
							listHeader.add("mdmcountry");
							listHeader.add("mdmcity");
							listHeader.add("mdmaddress2");
							listHeader.add("mdmaddress1");
							listHeader.add("countAddress");
						}						
					}
					else if(fdFieldVO.getName().equals("mdmhealthcareprovidertaxonomycode")) {
						Integer actualColumnTaxonomy = 0;
						writer.append(",");
						for (String string : taxonomyFields) {
							writer.append("\"" + string + "\"");

							if(actualColumnTaxonomy < (taxonomyFields.size()-1)) {
								writer.append(",");
							}
							actualColumnTaxonomy++;
						}
					}
				}

				actualColumn++;
			}

			writer.append("\n");
			writer.close();

			//BEGIN: printing the headers for all foreign entities
			for (String entityForeign : foreignHeaders.keySet()) {
				try {
					writer = new CustomFileWriter(fdEntityVO.getName() + "_" + entityForeign + ".csv", true);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				List<String> listHeader = foreignHeaders.get(entityForeign);
				Integer actualCounterNested = 0;

				for (String header : listHeader) {
					writer.append("\"" + header + "\"");

					if(actualCounterNested < (listHeader.size()-1)) {
						writer.append(",");
					}
					actualCounterNested++;
				}
				writer.append("\n");
				writer.close();
			}
			//FINISH: printing the headers for all foreign entities.

			Map<String, Object> cacheAddress = new HashMap<String, Object>();
			Map<String, Object> cacheTaxonomy = new HashMap<String, Object>();
			Integer lastCounterForEntity = 0;

			do {
				if(writer.getIsClosed()) {
					writer = new CustomFileWriter(fdEntityVO.getName() + ".csv", true);
				}

				CommandDataConsumption commandDataConsumption = new CommandDataConsumption(fdEntityVO.getEntityId(), actualCounter, recordByPage);
				EnvelopeVO executeCommand = MDMRestConnectionFactory.getConnection(fluigDataURL).executeCommand(commandDataConsumption);

				DataConsumptionEntitiesRecordVO dataConsumptionVO = (DataConsumptionEntitiesRecordVO) executeCommand.getHits().get(0);

				lastCounterForEntity = dataConsumptionVO.getLastCounterForEntity();
				numOfPendingRecords = dataConsumptionVO.getNumOfPendingRecords();

				if(this.mdmConsumer != null) {
					this.mdmConsumer.getLabelCounter().setText("Fluig Data Counter (Actual: " + lastCounterForEntity + ", pending records: " + numOfPendingRecords + "): ");;
					this.mdmConsumer.getLabelCounter().updateUI();
				}

				CommandConfirmationConsumption confirmation = null;

				if(dataConsumptionVO.getGoldenRecords() != null && dataConsumptionVO.getGoldenRecords().size() > 0) {
					for (GoldenRecordVO goldenRecordVO : dataConsumptionVO.getGoldenRecords()) {
						String mdmId = goldenRecordVO.getMdmId();
						maxColumns = fdFieldsVO.size();
						actualColumn = 0;

						writer.append("\"" + goldenRecordVO.getMdmId() + "\",");

						for (FDFieldVO fdFieldVO : fdFieldsVO) {
							if(fdFieldVO.getForeignField() == null || !fdFieldVO.getForeignField()) {
								if(fdFieldVO.getFatherMDMName() != null) {
									Object nested = goldenRecordVO.getMdmGoldenFieldAndValues().get(fdFieldVO.getFatherMDMName());

									if(nested instanceof ArrayList) {
										String foreignEntityKey = fdEntityVO.getName() + fdFieldVO.getFatherMDMName();
										Set<String> list = foreignValues.get(foreignEntityKey);

										if(list == null) {
											list = new TreeSet<String>();
											foreignValues.put(foreignEntityKey, list);
										}

										ArrayList<StringMap<String>> nestedArray = (ArrayList<StringMap<String>>) nested;

										for (StringMap<String> value : nestedArray) {
											Set<String> keySet = value.keySet();
											StringBuffer sb = new StringBuffer();
											sb.append("\""+mdmId+"\",");
											sb.append("\"1\",");
											Integer actualColumnN1 = 0;

											for (String string : keySet) {
												sb.append("\"" + value.get(string) + "\"");

												if(actualColumnN1 < (keySet.size()-1)) {
													sb.append(",");
												}

												actualColumnN1++;
											}

											list.add(sb.toString());//adding values for all columns.
										}
									}
									else if(nested instanceof StringMap) {
										StringMap<String> object = (StringMap) nested;
										writer.append("\"" + (object.get(fdFieldVO.getName()) == null ? "": object.get(fdFieldVO.getName())) + "\"");
									}
								}
								else {
									writer.append("\"" + (goldenRecordVO.getMdmGoldenFieldAndValues().get(fdFieldVO.getName()) == null ? "": goldenRecordVO.getMdmGoldenFieldAndValues().get(fdFieldVO.getName())) + "\"");
								}

								if(actualColumn < (maxColumns-1)) {
									writer.append(",");
								}
							}
							else {//search the foreign value to add all values here:
								String foreignEntityKey = fdEntityVO.getName() + fdFieldVO.getName();
//								Map<String, String> map = foreignNNKeys.get(foreignEntityKey);
								Set<String> list = foreignValues.get(foreignEntityKey);
								String foreignName = null;

								switch(fdFieldVO.getName()) {
									case "mdmaddressid":
										foreignName = "mdmaddress";

										if(list == null) {
											list = new TreeSet<String>();
											foreignValues.put(foreignEntityKey, list);
										}

										ArrayList<String> values = (ArrayList<String>) goldenRecordVO.getMdmGoldenFieldAndValues().get(fdFieldVO.getName());

										for (String value : values) {
											StringBuffer sb = new StringBuffer();
											sb.append("\"" + mdmId + "\",\"" + value + "\"");

											Object cachedValue = cacheAddress.get(value);

											if(cachedValue == null) {
												Object object = this.loadExternalRecord(foreignName, value, false);

												cacheAddress.put(value, object);
												cachedValue = object;
											}

											if(cachedValue instanceof StringMap) {
												StringMap<String> objectValues = (StringMap) cachedValue;
												Set<String> keySet = objectValues.keySet();
												Integer actualField = 0;
												sb.append(",");

												for (String objectKey : keySet) {
													sb.append("\"" + (objectValues.get(objectKey) == null ? "": objectValues.get(objectKey)) + "\"");
													if(actualField < (keySet.size()-1)) {
														sb.append(",");
													}
													actualField++;
												}
												sb.append(",1");//counter
											}

											list.add(sb.toString());//adding values for all columns.
											break;//FIXME: adding just first address....
										}
										break;
									case "mdmhealthcareprovidertaxonomycode":
										foreignName = "mdmhcpataxonomy";

										String value = (String) goldenRecordVO.getMdmGoldenFieldAndValues().get(fdFieldVO.getName());

										Object cachedValue = cacheTaxonomy.get(value);

										if(cachedValue == null) {
											Object object = this.loadExternalRecord(foreignName, value, "nested mdmGoldenFieldAndValues (mdmGoldenFieldAndValues.mdmhealthcareprovidertaxonomycode.raw eq \"" + value + "\")", true);

											cacheAddress.put(value, object);
											cachedValue = object;
										}

										if(cachedValue instanceof LinkedHashMap) {
											LinkedHashMap<String, String> objectValues = (LinkedHashMap) cachedValue;
											Set<String> keySet = objectValues.keySet();
											Integer actualField = 0;
											writer.append(",");

											for (String objectKey : taxonomyFields) {
												writer.append("\"" + (objectValues.get(objectKey) == null ? "": objectValues.get(objectKey)) + "\"");
												if(actualField < (keySet.size()-1)) {
													writer.append(",");
												}
												actualField++;
											}
										}

										break;
									default:
										System.out.println("nothing....");
										break;
								}
							}

							actualColumn++;
						}

						writer.append("\n");

						//Confirming that the golden record was consumed.
						if(originalCounter.equals(-1)) {
							if(confirmation == null) {
								confirmation = new CommandConfirmationConsumption();
							}

							confirmation.add(fdEntityVO.getEntityId(), goldenRecordVO.getMdmId());
						}
					}
					writer.close();

					//Confirming that the golden record was consumed.
					if(originalCounter.equals(-1)) {
						MDMRestConnectionFactory.getConnection(fluigDataURL).executeCommand(confirmation);
					}

					for (String entityForeign : foreignValues.keySet()) {
						try {
							writer = new CustomFileWriter(fdEntityVO.getName() + "_" + entityForeign + ".csv", true);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}

						Set<String> set = foreignValues.get(entityForeign);

						for (String foreignValue : set) {
							writer.append(foreignValue + "\n");
						}

						writer.close();
					}

					////////////////

					Set<String> entitiesForeign = foreignNNKeys.keySet();

					for (String entityForeign : entitiesForeign) {
						try {
							writer = new CustomFileWriter(fdEntityVO.getName() + "_" + entityForeign + ".csv", true);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}

						Map<String, String> map = foreignNNKeys.get(entityForeign);
						Set<String> keySet = map.keySet();

						for (String foreignValue : keySet) {
							String primaryKey = map.get(foreignValue);

							writer.append("\"" + primaryKey + "\",\"" + foreignValue + "\",\"1\"\n");
						}

						writer.close();
					}
				}
				else {
					break;
				}

				actualCounter += recordByPage;
			}
			while(((originalCounter.equals(-1) && numOfPendingRecords != 0) || (!originalCounter.equals(-1) && actualCounter <= lastCounterForEntity)) && !this.stop);

			if(this.mdmConsumer != null) {
				mdmConsumer.getButtonImportData().setText("Start consume process");
				mdmConsumer.getButtonImportData().setEnabled(true);
			}
		}
		catch(Exception e1) {
			e1.printStackTrace();
		}
		finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public Object loadExternalRecord(String foreignName, String primaryKey, boolean returnAllObject) {
		return this.loadExternalRecord(foreignName, primaryKey, "mdmId eq " + primaryKey, returnAllObject);
	}

	public Object loadExternalRecord(String foreignName, String primaryKey, String filter, boolean returnAllObject) {
		CommandQueryData commandQueryData = new CommandQueryData(foreignName, 0, 1);
		commandQueryData.setFilter(filter);
		EnvelopeVO commandQueryResult = MDMRestConnectionFactory.getConnection(fluigDataURL).executeCommand(commandQueryData);
		DataQueryVO dataQueryVO = (DataQueryVO) commandQueryResult.getHits().get(0);
		List<DataQueryHitVO> hits = dataQueryVO.getHits();

		if(hits != null) {
			Iterator<DataQueryHitVO> iterator = hits.iterator();
			if(iterator != null && iterator.hasNext()) {
				DataQueryHitVO next = iterator.next();
				Object object = null;

				if(returnAllObject) {
					object = next.getSource().getMdmGoldenFieldAndValues();
				}
				else {
					object = next.getSource().getMdmGoldenFieldAndValues().get(foreignName);
				}

				return object;
			}
		}
		return null;
	}
}
