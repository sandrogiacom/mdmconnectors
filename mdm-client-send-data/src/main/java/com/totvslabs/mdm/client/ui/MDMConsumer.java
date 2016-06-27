package com.totvslabs.mdm.client.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.gson.internal.StringMap;
import com.totvslabs.mdm.client.pojo.FDEntityVO;
import com.totvslabs.mdm.client.pojo.FDFieldVO;
import com.totvslabs.mdm.client.pojo.StoredAbstractVO;
import com.totvslabs.mdm.client.pojo.StoredFluigDataProfileVO;
import com.totvslabs.mdm.client.ui.events.MDMConnectionChangedDispatcher;
import com.totvslabs.mdm.client.ui.events.MDMConnectionChangedEvent;
import com.totvslabs.mdm.client.ui.events.MDMConnectionChangedListener;
import com.totvslabs.mdm.client.util.CustomFileWriter;
import com.totvslabs.mdm.client.util.MappingUtil;
import com.totvslabs.mdm.client.util.ProtheusWebService;
import com.totvslabs.mdm.restclient.MDMRestAuthentication;
import com.totvslabs.mdm.restclient.MDMRestConnectionFactory;
import com.totvslabs.mdm.restclient.MDMRestConnectionTypeEnum;
import com.totvslabs.mdm.restclient.command.CommandConfirmationConsumption;
import com.totvslabs.mdm.restclient.command.CommandDataConsumption;
import com.totvslabs.mdm.restclient.command.CommandGetDataConsumers;
import com.totvslabs.mdm.restclient.command.CommandGetDataModel;
import com.totvslabs.mdm.restclient.command.CommandGetField;
import com.totvslabs.mdm.restclient.command.CommandQueryData;
import com.totvslabs.mdm.restclient.vo.DataConsumerVO;
import com.totvslabs.mdm.restclient.vo.DataConsumptionEntitiesRecordVO;
import com.totvslabs.mdm.restclient.vo.DataConsumptionVO;
import com.totvslabs.mdm.restclient.vo.DataModelVO;
import com.totvslabs.mdm.restclient.vo.DataQueryHitVO;
import com.totvslabs.mdm.restclient.vo.DataQueryVO;
import com.totvslabs.mdm.restclient.vo.EnvelopeVO;
import com.totvslabs.mdm.restclient.vo.FieldsVO;
import com.totvslabs.mdm.restclient.vo.GenericVO;
import com.totvslabs.mdm.restclient.vo.GoldenRecordVO;

public class MDMConsumer extends PanelAbstract implements MDMConnectionChangedListener {
	private static final long serialVersionUID = 1L;

	private JLabel labelProduct;
	private JComboBox<String> comboProduct;

	private JLabel labelProtheusWS;
	private JTextField textProtheusWS;

	private JLabel labelEntitiesFD;
	private JTable tableEntitiesFD;
	private FDEntityTableModel tableModelEntitiesFD;
	private JScrollPane scrollBarEntitiesMDM;
	private FDEntityVO selectedRowMDMEntity;

	private JLabel labelFieldsFD;
	private JTable tableFieldsFD;
	private FDFieldTableModel tableModelFieldsFD;
	private JScrollPane scrollBarFieldsMDM;

	private JLabel labelNestedFields;
	private JComboBox<FDNestedFieldVO> comboNestedFields;
	private DefaultComboBoxModel<FDNestedFieldVO> comboBoxNestedFieldModel;

	private JLabel labelNestedInstance;
	private JComboBox<String> comboNestedInstance;

	private JButton buttonAddNestedInstance;
	private JButton buttonImportData;

	private JLabel labelCounter;
	private JTextField textCounter;

	private StoredFluigDataProfileVO fluigDataProfile;

	public MDMConsumer(boolean initialize) {
		super(2, 28, " Fluig Data: Consumer");
	}

	public MDMConsumer(){
		super(2, 28, " Fluig Data: Consumer");

		this.labelProduct = new JLabel("Product: ");
		this.comboProduct = new JComboBox<String>(new String[]{"Protheus", "Good Data"});
		this.labelProtheusWS = new JLabel("Protheus WS URL: ");
		this.textProtheusWS = new JTextField("http://172.16.103.116/ws/FWWSMODEL.apw");
		this.labelEntitiesFD = new JLabel("Fluig Data Entities: ");
		this.tableModelEntitiesFD = new FDEntityTableModel();
		this.tableEntitiesFD = new JTable(this.tableModelEntitiesFD);
		this.tableEntitiesFD.setFillsViewportHeight(true);
		
		this.labelFieldsFD = new JLabel("Fluig Data Fields: ");
		this.tableModelFieldsFD = new FDFieldTableModel();
		this.tableFieldsFD = new JTable(this.tableModelFieldsFD);
		this.tableFieldsFD.setFillsViewportHeight(true);
		
		this.labelNestedFields = new JLabel("Nested fields:");
		this.comboBoxNestedFieldModel = new DefaultComboBoxModel<FDNestedFieldVO>();
		this.comboNestedFields = new JComboBox<FDNestedFieldVO>(this.comboBoxNestedFieldModel);

		this.labelNestedInstance = new JLabel("Instance Name: ");
		this.comboNestedInstance = new JComboBox<String>();

		this.buttonAddNestedInstance = new JButton("Add Instance for Nested");
		this.buttonImportData = new JButton("Start consume process");

		this.labelCounter = new JLabel("Fluig Data Counter: ");
		this.textCounter = new JTextField("-1");

		this.scrollBarEntitiesMDM = new JScrollPane(this.tableEntitiesFD);
		this.scrollBarFieldsMDM = new JScrollPane(this.tableFieldsFD);

		MDMConnectionChangedDispatcher.getInstance().addMDMConnectionChangedListener(this);

		this.initializeLayout();
	}

	public void initializeLayout() {
		this.add(this.labelProduct);
		this.add(this.comboProduct);
		this.add(this.labelProtheusWS);
		this.add(this.textProtheusWS, 2, true, 1, 2);
		this.add(this.labelEntitiesFD);
		this.add(this.scrollBarEntitiesMDM, 2, true, 6, 2);
		this.add(this.labelFieldsFD);
		this.add(this.scrollBarFieldsMDM, 2, true, 6, 2);
		this.add(this.labelNestedFields);
		this.add(this.comboNestedFields);
		this.add(this.labelNestedInstance);
		this.add(this.comboNestedInstance);
		this.add(this.labelCounter, 2, true, 1, 2);
		this.add(this.textCounter);
		this.add(new JLabel());
		this.add(this.buttonAddNestedInstance);
		this.add(this.buttonImportData);

		this.initColumnSizes(this.tableEntitiesFD);

		this.comboNestedFields.addItemListener(new ComboNestedField());
		this.buttonAddNestedInstance.addActionListener(new AddNestedField());
		this.buttonImportData.addActionListener(new ConsumeGoldenRecordProtheus());
		this.comboProduct.addItemListener(new ProductSelectClick());

		//goodData
		this.comboProduct.setSelectedIndex(1);

		this.tableEntitiesFD.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.tableEntitiesFD.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				FDEntityVO object = tableModelEntitiesFD.getRowObject(tableEntitiesFD.getSelectedRow());

				selectedRowMDMEntity = object;
				tableModelFieldsFD.clearData();
				comboBoxNestedFieldModel.removeAllElements();

				List<String> getMdmFieldsConsumed = selectedRowMDMEntity.getFieldsDetail().getMdmFieldsConsumed();
				if(getMdmFieldsConsumed != null) {
					for (String string : getMdmFieldsConsumed) {
						CommandGetField commandGetField = new CommandGetField(string);
						EnvelopeVO executeCommandGetField = MDMRestConnectionFactory.getConnection(fluigDataProfile.getServerURL()).executeCommand(commandGetField);
						FieldsVO fieldVO = ((FieldsVO) executeCommandGetField.getHits().get(0));

						System.out.println(fieldVO.getMdmName());
						
						if(fieldVO.getMdmType().equals("NESTED")) {//father fields
							Iterator<FieldsVO> iterator = fieldVO.getMdmFieldsFull().values().iterator();
							List<FDFieldVO> childrenFields = new ArrayList<FDFieldVO>();

							while(iterator.hasNext()) {
								FieldsVO next = iterator.next();

								FDFieldVO fdFieldVO = new FDFieldVO();
								fdFieldVO.setChildren(true);
								fdFieldVO.setDescription(next.getMdmDescription().values().iterator().next());
								fdFieldVO.setInstance(null);
								fdFieldVO.setName(next.getMdmName());
								fdFieldVO.setProtheusField(null);
								fdFieldVO.setType(next.getMdmType());

								childrenFields.add(fdFieldVO);
							}

							comboBoxNestedFieldModel.addElement(new FDNestedFieldVO(fieldVO.getMdmName(), fieldVO.getMdmDescription().values().iterator().next(), childrenFields));
						}
						else {//normal fields
							FDFieldVO fdFieldVO = new FDFieldVO();
							fdFieldVO.setChildren(false);
							fdFieldVO.setDescription(fieldVO.getMdmDescription().values().iterator().next());
							fdFieldVO.setInstance(null);
							fdFieldVO.setName(fieldVO.getMdmName());
							fdFieldVO.setProtheusField(MappingUtil.mappings.get(MappingUtil.TYPE_CUSTOMER).get(MappingUtil.PROD_PROTHEUS).get(MappingUtil.NESTED_INSTANCE_DEFAULT).get(fieldVO.getMdmName()));
							fdFieldVO.setType(fieldVO.getMdmType());
							fdFieldVO.setForeignField((fieldVO.getMdmName().equals("mdmaddressid") && ((object.getName().equals("mdmhcp")) || ((object.getName().equals("mdmhca"))))) || (fieldVO.getMdmName().equals("mdmhealthcareprovidertaxonomycode") && ((object.getName().equals("mdmhcp")) || ((object.getName().equals("mdmhca"))))));//fixme: urgent!!!

							tableModelFieldsFD.addRow(fdFieldVO);
						}
					}

					JComboBox<String> comboBox = new JComboBox<String>(MappingUtil.mappings.get(MappingUtil.TYPE_CUSTOMER).get(MappingUtil.PROD_PROTHEUS).get(MappingUtil.NESTED_INSTANCE_DEFAULT).values().toArray(new String[0]));
					TableColumn columnFluigData = tableFieldsFD.getColumnModel().getColumn(3);
					columnFluigData.setCellEditor(new DefaultCellEditor(comboBox));
					columnFluigData.setCellRenderer(new DefaultTableCellRenderer());

					TableColumn columnInstance = tableFieldsFD.getColumnModel().getColumn(4);
					columnInstance.setCellEditor(new DefaultCellEditor(new JTextField()));
					columnInstance.setCellRenderer(new DefaultTableCellRenderer());

					Set<String> fieldsProtheus = MappingUtil.mappings.get(MappingUtil.TYPE_CUSTOMER).get(MappingUtil.PROD_PROTHEUS).keySet();

					for (String string : fieldsProtheus) {
						FDFieldVO fieldVO = new FDFieldVO();
						fieldVO.setProtheusField(string);
					}

					tableFieldsFD.updateUI();

					CommandGetDataConsumers consumer = new CommandGetDataConsumers(fluigDataProfile.getConsumerID());
					EnvelopeVO executeCommand = MDMRestConnectionFactory.getConnection(fluigDataProfile.getServerURL()).executeCommand(consumer);

					List<GenericVO> hits = executeCommand.getHits();
					object.setFieldsDetail(((DataConsumerVO) hits.get(0)).getMdmEntityDetails().get(object.getEntityId()));
					
					labelCounter.setText("Fluig Data Counter (Actual: " + object.getFieldsDetail().getMdmLastCounterConsumed() + ", pending records: " + object.getFieldsDetail().getMdmNumOfPendingRecords() + "): ");;
				}

				//adding nested types for this project (TODO: remove it)
				if(comboProduct.getSelectedItem().equals("Protheus")) {
					for(int i=0; i<comboBoxNestedFieldModel.getSize(); i++) {
						for(int j=0; j<comboNestedInstance.getItemCount(); j++) {
							comboNestedFields.setSelectedIndex(i);
							comboNestedInstance.setSelectedIndex(j);
							buttonAddNestedInstance.doClick();
						}
					}
				}
			}
		});
	}

	class ConsumeGoldenRecordGoodData implements ActionListener, Runnable {
		private Thread consomeProcess;
		private Boolean stop = Boolean.FALSE;

		@Override
		public void run() {
			FDEntityVO entity = tableModelEntitiesFD.getRowObject(tableEntitiesFD.getSelectedRow());

			if(entity == null) {
				return;
			}

			MDMRestAuthentication.getInstance(MDMRestConnectionTypeEnum.CONSUME, fluigDataProfile.getServerURL(), fluigDataProfile.getDomain(), fluigDataProfile.getConsumerID(), fluigDataProfile.getUsername(), fluigDataProfile.getPassword());
			MDMRestConnectionFactory.getConnection(fluigDataProfile.getServerURL());

			Integer numOfPendingRecords = 0;
			Integer actualCounter = Integer.parseInt(textCounter.getText()) == -1 ? -1 : Integer.parseInt(textCounter.getText());
			Integer recordByPage = 500;
			CustomFileWriter writer = null;

			try {
				writer = new CustomFileWriter(entity.getName() + ".csv", true);

				List<FDFieldVO> columns = tableModelFieldsFD.getData();
				int maxColumns = columns.size();
				int actualColumn = 0;
				Map<String, Map<String, String>> foreignNNKeys = new HashMap<String, Map<String, String>>();
				Map<String, List<String>> foreignHeaders = new HashMap<String, List<String>>();
				Map<String, Set<String>> foreignValues = new HashMap<String, Set<String>>();

				writer.append("\"mdmid\",");

				for (FDFieldVO fdFieldVO : columns) {
					if((fdFieldVO.getFatherMDMName() != null && fdFieldVO.getFatherMDMName().equals("mdmaddress")) || (fdFieldVO.getForeignField() == null || !fdFieldVO.getForeignField()) && fdFieldVO.getFatherMDMName() == null) {
						writer.append("\"" + fdFieldVO.getName() + "\"");
						
						if(actualColumn < (maxColumns-1)) {
							writer.append(",");
						}
					}
					else if(fdFieldVO.getFatherMDMName() != null) {
						String foreignEntityHeaderKey = entity.getName() + fdFieldVO.getFatherMDMName();

						List<String> listHeader = foreignHeaders.get(foreignEntityHeaderKey);

						if(listHeader == null) {
							listHeader = new ArrayList<String>();
							foreignHeaders.put(foreignEntityHeaderKey, listHeader);
							listHeader.add(entity.getName() + "Id");
							listHeader.add("counter"+ fdFieldVO.getFatherMDMName());
						}

						listHeader.add(fdFieldVO.getName());
					}
					else if(fdFieldVO.getForeignField() != null && fdFieldVO.getForeignField()) {
						String foreignEntityHeaderKey = entity.getName() + fdFieldVO.getName();

						List<String> listHeader = foreignHeaders.get(foreignEntityHeaderKey);//TODO: Add fields from other side here in this file. Here I can add manually.

						if(listHeader == null) {
							listHeader = new ArrayList<String>();
							foreignHeaders.put(foreignEntityHeaderKey, listHeader);
							listHeader.add(entity.getName() + "Id");
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

					actualColumn++;
				}

				writer.append("\n");
				writer.close();

				//BEGIN: printing the headers for all foreign entities
				for (String entityForeign : foreignHeaders.keySet()) {
					try {
						writer = new CustomFileWriter(entity.getName() + "_" + entityForeign + ".csv", true);
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
						writer = new CustomFileWriter(entity.getName() + ".csv", true);
					}

					CommandDataConsumption commandDataConsumption = new CommandDataConsumption(entity.getEntityId(), actualCounter, recordByPage);
					EnvelopeVO executeCommand = MDMRestConnectionFactory.getConnection(fluigDataProfile.getServerURL()).executeCommand(commandDataConsumption);

					DataConsumptionEntitiesRecordVO dataConsumptionVO = (DataConsumptionEntitiesRecordVO) executeCommand.getHits().get(0);

					lastCounterForEntity = dataConsumptionVO.getLastCounterForEntity();
					numOfPendingRecords = dataConsumptionVO.getNumOfPendingRecords();

					labelCounter.setText("Fluig Data Counter (Actual: " + lastCounterForEntity + ", pending records: " + numOfPendingRecords + "): ");;
					labelCounter.updateUI();

					CommandConfirmationConsumption confirmation = null;

					if(dataConsumptionVO.getGoldenRecords() != null && dataConsumptionVO.getGoldenRecords().size() > 0) {
						for (GoldenRecordVO goldenRecordVO : dataConsumptionVO.getGoldenRecords()) {
							String mdmId = goldenRecordVO.getMdmId();
							maxColumns = columns.size();
							actualColumn = 0;

							writer.append("\"" + goldenRecordVO.getMdmId() + "\",");

							for (FDFieldVO fdFieldVO : columns) {
								if(fdFieldVO.getForeignField() == null || !fdFieldVO.getForeignField()) {
									if(fdFieldVO.getFatherMDMName() != null) {
										Object nested = goldenRecordVO.getMdmGoldenFieldAndValues().get(fdFieldVO.getFatherMDMName());

										if(nested instanceof ArrayList) {
											String foreignEntityKey = entity.getName() + fdFieldVO.getFatherMDMName();
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
									String foreignEntityKey = entity.getName() + fdFieldVO.getName();
//									Map<String, String> map = foreignNNKeys.get(foreignEntityKey);
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

												for (String objectKey : keySet) {
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
							if(textCounter.getText().equals("-1")) {
								if(confirmation == null) {
									confirmation = new CommandConfirmationConsumption();
								}

								confirmation.add(entity.getEntityId(), goldenRecordVO.getMdmId());
							}
						}
						writer.close();

						//Confirming that the golden record was consumed.
						if(textCounter.getText().equals("-1")) {
							MDMRestConnectionFactory.getConnection(fluigDataProfile.getServerURL()).executeCommand(confirmation);
						}

						for (String entityForeign : foreignValues.keySet()) {
							try {
								writer = new CustomFileWriter(entity.getName() + "_" + entityForeign + ".csv", true);
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
								writer = new CustomFileWriter(entity.getName() + "_" + entityForeign + ".csv", true);
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
				while(((textCounter.getText().equals("-1") && numOfPendingRecords != 0) || (!textCounter.getText().equals("-1") && actualCounter <= lastCounterForEntity)) && !this.stop);

				buttonImportData.setText("Start consume process");
				buttonImportData.setEnabled(true);
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

				MDMRestAuthentication.getInstance(MDMRestConnectionTypeEnum.NORMAL, fluigDataProfile.getServerURL(), fluigDataProfile.getDomain(), fluigDataProfile.getDatasourceID(), fluigDataProfile.getUsername(), fluigDataProfile.getPassword());
				MDMRestConnectionFactory.getConnection(fluigDataProfile.getServerURL());
			}
		}

		public Object loadExternalRecord(String foreignName, String primaryKey, boolean returnAllObject) {
			return this.loadExternalRecord(foreignName, primaryKey, "mdmId eq " + primaryKey, returnAllObject);
		}

		public Object loadExternalRecord(String foreignName, String primaryKey, String filter, boolean returnAllObject) {
			CommandQueryData commandQueryData = new CommandQueryData(foreignName, 0, 1);
			commandQueryData.setFilter(filter);
			EnvelopeVO commandQueryResult = MDMRestConnectionFactory.getConnection(fluigDataProfile.getServerURL()).executeCommand(commandQueryData);
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

		@Override
		public void actionPerformed(ActionEvent e) {
			if(this.consomeProcess == null || this.consomeProcess.getState().equals(State.TERMINATED)) {
				this.stop = Boolean.FALSE;
				this.consomeProcess = new Thread(this);
				this.consomeProcess.start();
				buttonImportData.setText("Stop consume process");
				buttonImportData.setEnabled(true);
			}
			else {
				buttonImportData.setText("Stopping consume process");
				buttonImportData.setEnabled(false);
				this.stop = Boolean.TRUE;
			}
		}
		
	}

	class ConsumeGoldenRecordProtheus implements ActionListener, Runnable {
		private Thread consomeProcess;
		private Boolean stop = Boolean.FALSE;

		@Override
		public void actionPerformed(ActionEvent e) {
			if(this.consomeProcess == null || this.consomeProcess.getState().equals(State.TERMINATED)) {
				this.stop = Boolean.FALSE;
				this.consomeProcess = new Thread(this);
				this.consomeProcess.start();
				buttonImportData.setText("Stop consume process");
				buttonImportData.setEnabled(true);
			}
			else {
				buttonImportData.setText("Stopping consume process");
				buttonImportData.setEnabled(false);
				this.stop = Boolean.TRUE;
			}
		}

		@Override
		public void run() {
			FDEntityVO entity = tableModelEntitiesFD.getRowObject(tableEntitiesFD.getSelectedRow());

			List<FDFieldVO> data = tableModelFieldsFD.getData();
			Map<String, FDFieldVO> mappedFields = new HashMap<String, FDFieldVO>();

			for (FDFieldVO fdFieldVO : data) {
				if(fdFieldVO.getProtheusField() != null) {
					mappedFields.put(fdFieldVO.getProtheusField(), fdFieldVO);
				}
			}

			Set<String> protheusColumns = mappedFields.keySet();
			
			if(entity == null) {
				return;
			}

			MDMRestAuthentication.getInstance(MDMRestConnectionTypeEnum.CONSUME, fluigDataProfile.getServerURL(), fluigDataProfile.getDomain(), fluigDataProfile.getConsumerID(), fluigDataProfile.getUsername(), fluigDataProfile.getPassword());
			MDMRestConnectionFactory.getConnection(fluigDataProfile.getServerURL());

			Integer numOfPendingRecords = 0;

			try {
				do {
					CommandDataConsumption commandDataConsumption = new CommandDataConsumption(entity.getEntityId(), Integer.parseInt(textCounter.getText()), 10);
					EnvelopeVO executeCommand = MDMRestConnectionFactory.getConnection(fluigDataProfile.getServerURL()).executeCommand(commandDataConsumption);

					DataConsumptionVO dataConsumptionVO = (DataConsumptionVO) executeCommand.getHits().get(0);
					DataConsumptionEntitiesRecordVO entitiesRecordVO = dataConsumptionVO.getEntitiesRecords().values().iterator().next();

					Integer lastCounterForEntity = entitiesRecordVO.getLastCounterForEntity();
					numOfPendingRecords = entitiesRecordVO.getNumOfPendingRecords();
					List<GoldenRecordVO> goldenRecords = entitiesRecordVO.getGoldenRecords();

					labelCounter.setText("Fluig Data Counter (Actual: " + lastCounterForEntity + ", pending records: " + numOfPendingRecords + "): ");;
					labelCounter.updateUI();

					if(goldenRecords != null) {
						for (GoldenRecordVO goldenRecordVO : goldenRecords) {
							boolean result = consumptionProcess(protheusColumns, mappedFields, entity, goldenRecordVO.getMdmGoldenFieldAndValues());
	
							//TODO: understand why it's generating error saving the new object.
							//It's related with the new index by name, maybe there is other record with same name?
		//					if(result) {
		//						Integer processedlCounter = goldenRecordVO.getMdmCounterForEntity(); //to store locally and show when start again - use the same value on the service above (acima).
		//						StoredDataConsumptionCounterVO actualCounter = new StoredDataConsumptionCounterVO();
		//						actualCounter.setCounter(processedlCounter);
		//						actualCounter.setDatasourceID(fluigDataProfile.getDatasourceID());
		//						actualCounter.setFluigDataName(fluigDataProfile.getName());
		//						actualCounter.setSourceName(entity.getEntityId());
		//
		//						PersistenceEngine.getInstance().save(actualCounter);
		//					}
						}
					}
				}
				while(numOfPendingRecords != 0 && !this.stop);

				buttonImportData.setText("Start consume process");
				buttonImportData.setEnabled(true);
			}
			catch(Exception e1) {
				e1.printStackTrace();
			}
			finally {
				MDMRestAuthentication.getInstance(MDMRestConnectionTypeEnum.NORMAL, fluigDataProfile.getServerURL(), fluigDataProfile.getDomain(), fluigDataProfile.getDatasourceID(), fluigDataProfile.getUsername(), fluigDataProfile.getPassword());
				MDMRestConnectionFactory.getConnection(fluigDataProfile.getServerURL());
			}
		}

		private void assignValueToXMLNode(Node node, FDFieldVO fdFieldVO, Map<String, Object> data) {
			if(node != null) {
				NodeList childNodes = node.getChildNodes();
				for(int j=0; j<childNodes.getLength(); j++) {
					Node nodeChild = childNodes.item(j);

					if("value".equals(nodeChild.getNodeName())) {
						if(fdFieldVO.getChildren() != null && fdFieldVO.getChildren()) {
							Map<String, String> result = new HashMap<String, String>();

							StringMap<String> object = (StringMap<String>) data.get(fdFieldVO.getFatherMDMName());
							Set<String> keySet = object.keySet();

							for (String string : keySet) {
								result.put(string, object.get(string));
							}

							if(result != null) {
								String value = (String) result.get(fdFieldVO.getName());
								
								if(fdFieldVO.getName().equals("mdmcountry") && value != null && value.length() > 3) { //TODO: fixme
									value = value.substring(0, 3);
								}
								if(fdFieldVO.getName().equals("mdmcity") && value != null && value.length() > 15) { //TODO: fixme
									value = value.substring(0, 15);
								}
								if(value != null && value.trim().length() > 0) {
									nodeChild.setTextContent(value);
								}
							}
						}
						else {
							String value = (String) data.get(fdFieldVO.getName());
							if (fdFieldVO.getType().equals("DATE")) {
								if(((String) comboProduct.getSelectedItem()).equals("Protheus")) {
									value = value.replaceAll("-", "");
								}
							}
							if (fdFieldVO.getName().equals("mdmentitytype") && value != null) {// TODO: Fixme
								value = value.trim().toUpperCase();
							}
							if (fdFieldVO.getName().equals("mdmdba") && (value == null || value.trim().length() == 0)) {// TODO: Fixme
								value = (String) data.get("mdmname");
								if(value.length() > 20) {
									value = value.substring(0, 20);
								}
							}
//							if(fdFieldVO.getName().equals("mdmdba") && value != null) {//TODO: Fixme I applied this rule on MDM
//								value = value.trim();
//
//								if(value.length() > 20) {
//									value = value.substring(0, 20);
//								}
//							}
//							if(fdFieldVO.getName().equals("mdmphonenumber") && value != null) {//TODO: Fixme I applied this rule on MDM
//								value = value.trim();
//
//								if(value.length() > 15) {
//									value = value.substring(0, 15);
//								}
//							}

							if(value != null && value.trim().length() > 0) {
								nodeChild.setTextContent(value);
							}
						}
					}
				}
			}
		}

		private void workArround(String fieldProtheus, Map<String, Node> map, Map<String, FDFieldVO> mappedFields, Map<String, Object> data) {
			Node node = map.get(fieldProtheus);
			if (node != null) {
				FDFieldVO fdFieldVO = mappedFields.get(fieldProtheus);
//				fdFieldVO.setChildren(Boolean.FALSE); //workarround!!!
				data.put(fdFieldVO.getName(), ".");
				this.assignValueToXMLNode(node, fdFieldVO, data);
			}
		}
		
		private boolean consumptionProcess(Set<String> protheusColumns, Map<String, FDFieldVO> mappedFields, FDEntityVO entity, Map<String, Object> data) {
			try {
				File file = new File("conf/" + entity.getWsURL());

				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
				Document doc = docBuilder.parse(file);
				
				Node staff = doc.getElementsByTagName("MATA030_SA1").item(0);
				
				NodeList list = staff.getChildNodes();
				Map<String, Node> map = new HashMap<String, Node>();
				
				for (int i = 0; i < list.getLength(); i++) {
					Node node = list.item(i);
					map.put(node.getNodeName(), node);
				}

				for (String protheusColumn : protheusColumns) {
					Node node = map.get(protheusColumn);
					FDFieldVO fdFieldVO = mappedFields.get(protheusColumn);

					this.assignValueToXMLNode(node, fdFieldVO, data);
				}

				//workarround:
				if(data.get("mdmaddress1") == null || ((String) data.get("mdmaddress1")).trim().length() == 0) {
					this.workArround("A1_END", map, mappedFields, data);
				}
				if(data.get("mdmstate") == null || ((String) data.get("mdmstate")).trim().length() == 0) {
					this.workArround("A1_EST", map, mappedFields, data);
				}
				if(data.get("mdmcity") == null || ((String) data.get("mdmcity")).trim().length() == 0) {
					this.workArround("A1_MUN", map, mappedFields, data);
				}

				StringWriter sw = new StringWriter();
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(sw);
				transformer.transform(source, result);

				ProtheusWebService.callWebService(textProtheusWS.getText(), "MATA030", DatatypeConverter.printBase64Binary(sw.toString().getBytes()));
			} catch (ParserConfigurationException pce) {
				pce.printStackTrace();
				return false;
			} catch (TransformerException tfe) {
				tfe.printStackTrace();
				return false;
			} catch (IOException ioe) {
				ioe.printStackTrace();
				return false;
			} catch (SAXException sae) {
				sae.printStackTrace();
				return false;
			}
			
			return true;
		}

	}

	class ComboNestedField implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent e) {
			comboNestedInstance.removeAllItems();
			comboNestedInstance.updateUI();

			FDNestedFieldVO selectedItem = (FDNestedFieldVO) comboNestedFields.getSelectedItem();

			if(selectedItem != null) {
				List<String> list = MappingUtil.NESTED_INSTANCES.get(selectedItem.getInternalName());
				
				if(list != null) {
					for (String string : list) {
						comboNestedInstance.addItem(new String(string));
					}
					comboNestedInstance.updateUI();
				}
			}
		}
	}

	class AddNestedField implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Object selectedItem = comboNestedFields.getSelectedItem();
			
			if(selectedItem != null && selectedItem instanceof FDNestedFieldVO) {
				FDNestedFieldVO fdNestedField = (FDNestedFieldVO) selectedItem;
				List<FDFieldVO> fieldsVO = fdNestedField.getFieldsVO();
				String selectedInstance = null;
				
				if(comboNestedInstance.getSelectedItem() == null) {
					selectedInstance = "default";
				}
				else {
					selectedInstance = new String((String) comboNestedInstance.getSelectedItem());
				}

				if(fieldsVO != null) {
					for (FDFieldVO fdFieldVO : fieldsVO) {
						try {
							FDFieldVO fdFieldNew = (FDFieldVO) fdFieldVO.clone();
							fdFieldNew.setInstance(selectedInstance);
							fdFieldNew.setFatherMDMName(fdNestedField.getInternalName());

							if(MappingUtil.mappings.get(tableModelEntitiesFD.getRowObject(tableEntitiesFD.getSelectedRow()).getName()) != null) {
								fdFieldNew.setProtheusField(MappingUtil.mappings.get(MappingUtil.TYPE_CUSTOMER).get(MappingUtil.PROD_PROTHEUS).get(fdFieldNew.getInstance()).get(fdFieldNew.getName()));
							}
	
							tableModelFieldsFD.addRow(fdFieldNew);
						} catch (CloneNotSupportedException e1) {
						}
					}

					tableFieldsFD.updateUI();
				}
			}
		}
	}

    private void initColumnSizes(JTable table) {
    	FDEntityTableModel model = (FDEntityTableModel) table.getModel();
        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;
        Object[] longValues = model.getRow(0);
        TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
 
        for (int i = 0; i < 2; i++) {
            column = table.getColumnModel().getColumn(i);
 
            comp = headerRenderer.getTableCellRendererComponent(null, column.getHeaderValue(), false, false, 0, 0);
            headerWidth = comp.getPreferredSize().width;
            comp = table.getDefaultRenderer(model.getColumnClass(i)).getTableCellRendererComponent(table, longValues[i], false, false, 0, i);
            cellWidth = comp.getPreferredSize().width;
 
            column.setPreferredWidth(Math.max(headerWidth, cellWidth));
        }
    }

	class ProductSelectClick implements ItemListener {
		ConsumeGoldenRecordGoodData goodData = new ConsumeGoldenRecordGoodData();
		ConsumeGoldenRecordProtheus protheus = new ConsumeGoldenRecordProtheus();

		@Override
		public void itemStateChanged(ItemEvent e) {
			if(e.getItem().equals("Good Data")) {
				//hide Protheus WS URL
				labelProtheusWS.setVisible(false);
				textProtheusWS.setVisible(false);
				//clean fluig data fields
				textCounter.setText("-1");

				this.cleanActionListener(buttonImportData);
				buttonImportData.addActionListener(goodData);
			}
			else if(e.getItem().equals("Protheus")) {
				//hide Protheus WS URL
				labelProtheusWS.setVisible(true);
				textProtheusWS.setVisible(true);
				//clean fluig data fields

				textCounter.setText("-1");

				this.cleanActionListener(buttonImportData);
				buttonImportData.addActionListener(protheus);
			}
		}
		
		private void cleanActionListener(JButton button) {
		    for( ActionListener al : button.getActionListeners() ) {
		        button.removeActionListener( al );
		    }
		}
	}

	class FDNestedFieldVO {
		private String internalName;
		private String name;
		private List<FDFieldVO> fieldsVO;

		public FDNestedFieldVO(String internalName, String name, List<FDFieldVO> fieldsVO) {
			super();
			this.internalName = internalName;
			this.name = name;
			this.fieldsVO = fieldsVO;
		}
		public String getInternalName() {
			return internalName;
		}
		public void setInternalName(String internalName) {
			this.internalName = internalName;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public List<FDFieldVO> getFieldsVO() {
			return fieldsVO;
		}
		public void setFieldsVO(List<FDFieldVO> fieldsVO) {
			this.fieldsVO = fieldsVO;
		}
		public String toString() {
			return this.name;
		}
	}

	class FDDataConsumptionVO {
		private Integer counter;
		private String description;

		public Integer getCounter() {
			return counter;
		}
		public void setCounter(Integer counter) {
			this.counter = counter;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
	}

	class FDDataConsumptionTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		private String[] columnNames = {"Counter", "Description"};
		private List<FDDataConsumptionVO> data = new ArrayList<FDDataConsumptionVO>();

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public Class getColumnClass(int c) {
			Object valueAt = getValueAt(0, c);

			if(valueAt != null) {
				return valueAt.getClass();
			}

			return String.class;
		}

		public void addRows(List<FDDataConsumptionVO> vos) {
			this.data.clear();
			this.data.addAll(vos);
		}

		public void addRow(FDDataConsumptionVO field) {
			this.data.add(field);
		}

		@Override
		public int getRowCount() {
			if(data != null) {
				return data.size();
			}

			return 0;
		}

		public Object[] getRow(int row) {
			Object[] rowData = new Object[this.columnNames.length];

			for(int i=0; i<columnNames.length; i++) {
				rowData[i] = this.getValueAt(row, i);
			}

			return rowData;
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public String getColumnName(int column) {
			return this.columnNames[column];
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if(this.data == null || this.data.isEmpty()) {
				return null;
			}

			FDDataConsumptionVO record = this.data.get(rowIndex);

			switch(columnIndex) {
				case 0:
					return record.getCounter();
				case 1:
					return record.getDescription();
			}

			return null;
		}
	}

	class FDEntityTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		private String[] columnNames = {"Name", "Description"};
		private List<FDEntityVO> data = new ArrayList<FDEntityVO>();

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public Class getColumnClass(int c) {
			Object valueAt = getValueAt(0, c);

			if(valueAt != null) {
				return valueAt.getClass();
			}

			return String.class;
		}

		public void clear() {
			this.data.clear();
		}
		
		public void addRows(List<FDEntityVO> vos) {
			this.data.clear();
			this.data.addAll(vos);
		}

		public void addRow(FDEntityVO field) {
			this.data.add(field);
		}

		@Override
		public int getRowCount() {
			if(data != null) {
				return data.size();
			}

			return 0;
		}

		public FDEntityVO getRowObject(int row) {
			if(row < 0) {
				return null;
			}
			return (FDEntityVO) data.get(row);
		}

		public Object[] getRow(int row) {
			Object[] rowData = new Object[this.columnNames.length];

			for(int i=0; i<columnNames.length; i++) {
				rowData[i] = this.getValueAt(row, i);
			}

			return rowData;
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public String getColumnName(int column) {
			return this.columnNames[column];
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if(this.data == null || this.data.isEmpty()) {
				return null;
			}

			FDEntityVO record = this.data.get(rowIndex);

			switch(columnIndex) {
				case 0:
					return record.getName();
				case 1:
					return record.getDescription();
			}

			return null;
		}
	}

	class FDFieldTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		private String[] columnNames = {"Description", "Type", "Children", "Protheus Field", "Instance", "Delete Children"};
		private List<FDFieldVO> data = new ArrayList<FDFieldVO>();

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public Class getColumnClass(int c) {
			Object valueAt = getValueAt(0, c);

			if(valueAt != null) {
				return valueAt.getClass();
			}

			return String.class;
		}

		public void addRows(List<FDFieldVO> vos) {
			this.data.clear();
			this.data.addAll(vos);
		}

		public void addRow(FDFieldVO field) {
			this.data.add(field);
		}

		@Override
		public int getRowCount() {
			if(data != null) {
				return data.size();
			}

			return 0;
		}

		public void clearData() {
			this.data.clear();
		}

		public List<FDFieldVO> getData() {
			return this.data;
		}

		public Object[] getRow(int row) {
			Object[] rowData = new Object[this.columnNames.length];

			for(int i=0; i<columnNames.length; i++) {
				rowData[i] = this.getValueAt(row, i);
			}

			return rowData;
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public String getColumnName(int column) {
			return this.columnNames[column];
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			FDFieldVO fdFieldVO = this.data.get(rowIndex);

			if(columnIndex==4 && !fdFieldVO.getChildren()) {
				return false;
			}

			return columnIndex > 2;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if(this.data == null || this.data.isEmpty()) {
				return null;
			}

			FDFieldVO record = this.data.get(rowIndex);

			switch(columnIndex) {
				case 0:
					return record.getDescription();
				case 1:
					return record.getType();
				case 2:
					return record.getChildren();
				case 3:
					return record.getProtheusField();
				case 4:
					return record.getInstance();
			}

			return null;
		}

		@Override
        public void setValueAt(Object value, int row, int col) {
            FDFieldVO fdFieldVO = this.data.get(row);

            switch(col) {
				case 3:
					fdFieldVO.setProtheusField((String) value);
					break;
				case 4:
					fdFieldVO.setInstance((String) value);
					break;
            }

            fireTableCellUpdated(row, col);
        }
	}

	@Override
	public StoredAbstractVO getAllData() {
		return null;
	}

	@Override
	public void loadAllData(StoredAbstractVO intance) {
	}

	@Override
	public void loadDefaultData() {
	}

	@Override
	public void onMDMConnectionChangedListener(MDMConnectionChangedEvent event) {
		if(1==1)
			return;
		
		//TODO: fixme fix the new way to work with consumption process.

		this.fluigDataProfile = event.getActualConnection();

		CommandGetDataConsumers consumer = new CommandGetDataConsumers(this.fluigDataProfile.getConsumerID());
		EnvelopeVO executeCommand = MDMRestConnectionFactory.getConnection(fluigDataProfile.getServerURL()).executeCommand(consumer);

		List<GenericVO> hits = executeCommand.getHits();

		if (hits != null && !hits.isEmpty()) {
			if(hits.get(0) instanceof DataConsumerVO) {
				List<String> entities = ((DataConsumerVO) hits.get(0)).getMdmEntitiesConsumed();

				if(entities != null) {
					tableModelEntitiesFD.clear();
					
					for (String string : entities) {
						CommandGetDataModel dataModel = new CommandGetDataModel(string);
						EnvelopeVO executeCommandGetDataModel = MDMRestConnectionFactory.getConnection(fluigDataProfile.getServerURL()).executeCommand(dataModel);

						String next = ((DataModelVO) executeCommandGetDataModel.getHits().get(0)).getMdmLabel().keySet().iterator().next();
						
						FDEntityVO entity = new FDEntityVO();
						entity.setDescription(((DataModelVO) executeCommandGetDataModel.getHits().get(0)).getMdmLabel().get(next));
						entity.setName(((DataModelVO) executeCommandGetDataModel.getHits().get(0)).getMdmName());
						entity.setFieldsDetail(((DataConsumerVO) hits.get(0)).getMdmEntityDetails().get(string));
						entity.setEntityId(string);

						if(entity.getName().equalsIgnoreCase("mdmcustomer")) {
							entity.setWsURL("PROTHEUS_MATA030.XML");
						}

						tableModelEntitiesFD.addRow(entity);
					}
				}
			}
		}
	}

	public JLabel getLabelCounter() {
		return labelCounter;
	}

	public JTextField getTextCounter() {
		return textCounter;
	}

	public JButton getButtonImportData() {
		return buttonImportData;
	}
}
