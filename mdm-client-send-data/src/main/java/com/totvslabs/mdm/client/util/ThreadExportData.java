package com.totvslabs.mdm.client.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.totvslabs.mdm.client.pojo.JDBCFieldVO;
import com.totvslabs.mdm.client.pojo.JDBCTableVO;
import com.totvslabs.mdm.client.pojo.MDMData;
import com.totvslabs.mdm.client.pojo.StoredAbstractVO;
import com.totvslabs.mdm.client.pojo.StoredFluigDataProfileVO;
import com.totvslabs.mdm.client.pojo.StoredJDBCConnectionVO;
import com.totvslabs.mdm.client.pojo.StoredRecordHashVO;
import com.totvslabs.mdm.client.ui.SendFileFluigData;
import com.totvslabs.mdm.client.ui.SendJDBCEntities;
import com.totvslabs.mdm.client.ui.events.LogManagerDispatcher;
import com.totvslabs.mdm.client.ui.events.SendDataFluigDataDoneDispatcher;
import com.totvslabs.mdm.client.ui.events.SendDataFluigDataDoneEvent;
import com.totvslabs.mdm.client.ui.events.SendDataFluigDataUpdateProcessDispatcher;
import com.totvslabs.mdm.client.ui.events.SendDataFluigDataUpdateProcessEvent;
import com.totvslabs.mdm.restclient.MDMRestConnection;
import com.totvslabs.mdm.restclient.MDMRestConnectionFactory;
import com.totvslabs.mdm.restclient.command.CommandPostSchema;
import com.totvslabs.mdm.restclient.command.CommandPostStaging;
import com.totvslabs.mdm.restclient.command.CommandPostStagingC;

public class ThreadExportData implements Runnable {
	private SendJDBCEntities panelJDBCEntities;
	private SendFileFluigData panelSendFileFluigData;
	private JDBCTableVO tableVO;
	private StoredJDBCConnectionVO jdbcConnectionVO;
	private StoredFluigDataProfileVO fluigDataProfileVO;
	private String mdmServerURL;
	private String tenantId;
	private String datasourceId;
	private boolean justExportJSonData = Boolean.FALSE;
	private boolean backgroundProcess = Boolean.FALSE;
	private Integer quantityRecords = 0;

	public ThreadExportData(JDBCTableVO tableVO, StoredJDBCConnectionVO jdbcConnectionVO, SendJDBCEntities panelJDBCEntities) {
		this(null, tableVO, jdbcConnectionVO, panelJDBCEntities);
		this.justExportJSonData = Boolean.TRUE;
		this.backgroundProcess = Boolean.FALSE;
	}

	public ThreadExportData(StoredFluigDataProfileVO mdmProfile, JDBCTableVO tableVO, StoredJDBCConnectionVO jdbcConnectionVO) {
		this(mdmProfile, tableVO, jdbcConnectionVO, null);
		this.backgroundProcess = Boolean.TRUE;
	}

	public ThreadExportData(StoredFluigDataProfileVO mdmProfile, JDBCTableVO tableVO, StoredJDBCConnectionVO jdbcConnectionVO, SendJDBCEntities panelJDBCEntities) {
		this.panelJDBCEntities = panelJDBCEntities;
		this.jdbcConnectionVO = jdbcConnectionVO;
		this.fluigDataProfileVO = mdmProfile;
		this.mdmServerURL = mdmProfile != null ? mdmProfile.getServerURL() : null;
		this.tenantId = mdmProfile != null ? mdmProfile.getDomain() : null;
		this.datasourceId = mdmProfile != null ? mdmProfile.getDatasourceID() : null;
		this.tableVO = tableVO;
		this.backgroundProcess = Boolean.FALSE;
	}

	public ThreadExportData(StoredFluigDataProfileVO mdmProfile, SendFileFluigData panelSendFileFluigData) {
		this.panelSendFileFluigData = panelSendFileFluigData;
		this.fluigDataProfileVO = mdmProfile;
		this.mdmServerURL = mdmProfile != null ? mdmProfile.getServerURL() : null;
		this.tenantId = mdmProfile != null ? mdmProfile.getDomain() : null;
		this.datasourceId = mdmProfile != null ? mdmProfile.getDatasourceID() : null;
		this.backgroundProcess = Boolean.FALSE;
	}

	@Override
	public void run() {
		if(panelSendFileFluigData != null) {
			sendDataFile();
		}
		else if(panelJDBCEntities != null || this.backgroundProcess) {
			sendDataJDBC();
		}
	}

	private void sendDataFile() {
		DateFormat df = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z");

		LogManagerDispatcher.getInstance().register("Starting the process now: " + df.format(Calendar.getInstance().getTime()));
		List<MDMData> mdmData = panelSendFileFluigData.getData();
		MDMRestConnection connection = MDMRestConnectionFactory.getConnection(mdmServerURL);

		JsonObject completeSchema = new JsonObject();
		JsonObject schema = new JsonObject();
		JsonObject schemaCross = new JsonObject();
		JsonObject schemas = new JsonObject();
		JsonObject schemasCross = new JsonObject();
		schema.add("properties", schemas);
		schemaCross.add("_mdmCrossreference", schemasCross);
		completeSchema.add("_mdmStagingMapping", schema);
		completeSchema.add("_mdmCrosswalkTemplate", schemaCross);

		for (int i = 0; i < mdmData.size(); i++) {
			MDMData data = mdmData.get(i);
			CommandPostStaging staging = null;

			if(panelSendFileFluigData.getCheckBoxCompress().isSelected()) {
				staging = new CommandPostStagingC(tenantId, datasourceId, data.getTemplateName().replaceAll(" ", ""), data.getData());
			}
			else {
				staging = new CommandPostStaging(tenantId, datasourceId, data.getTemplateName().replaceAll(" ", ""), data.getData());
			}

			long initialTime = System.currentTimeMillis();
			long endTime = initialTime;

			String additionalInformation = "";

			if(staging instanceof CommandPostStagingC) {
				additionalInformation = " (compressed)";
			}

			try {
				connection.executeCommand(staging);
				endTime = System.currentTimeMillis();
			}
			catch(Exception e) {
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace();
			}

			LogManagerDispatcher.getInstance().register("Sent " + data.getData().size() + additionalInformation + " records in " + (endTime - initialTime) + "ms (" + data.getData().size() + " in total).");

			SendDataFluigDataUpdateProcessDispatcher.getInstance().fireSendDataFluigDataUpdateProcessEvent(new SendDataFluigDataUpdateProcessEvent(data.getData().size(), data.getData().size(), ProcessTypeEnum.SEND_DATA));

			LogManagerDispatcher.getInstance().register("Finished the process now: " + df.format(Calendar.getInstance().getTime()) + "\n\n");

			SendDataFluigDataDoneDispatcher.getInstance().fireSendDataFluigDataDoneEvent(new SendDataFluigDataDoneEvent(ProcessTypeEnum.SEND_DATA, data.getData().toString()));
		}
	}

	private void sendDataJDBC() {
		DateFormat df = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z");

		LogManagerDispatcher.getInstance().register("Starting the process now: " + df.format(Calendar.getInstance().getTime()));
		StringBuffer totalDataJSon = new StringBuffer();
		Integer batchSize = 500;
		Integer totalRecords = tableVO.getTotalRecords();

		if(panelJDBCEntities != null) {
			try {
				batchSize = Integer.parseInt(panelJDBCEntities.getTextBatchSize().getText());
			}
			catch(NumberFormatException e) {
			}
		}

		MDMRestConnection connection = MDMRestConnectionFactory.getConnection(mdmServerURL);

		Long totalTimeSchema = System.currentTimeMillis();
		JsonObject completeSchema = new JsonObject();
		JsonObject schema = new JsonObject();
		JsonObject schemaCross = new JsonObject();
		JsonObject schemas = new JsonObject();
		JsonObject schemasCross = new JsonObject();
		schema.add("properties", schemas);
		schemaCross.add("_mdmCrossreference", schemasCross);
		completeSchema.add("_mdmStagingMapping", schema);
		completeSchema.add("_mdmCrosswalkTemplate", schemaCross);

		List<JDBCFieldVO> fields = this.tableVO.getFields();

		for (JDBCFieldVO jdbcFieldVO : fields) {
			JsonObject fieldDetail = new JsonObject();

			String type = jdbcFieldVO.getType();

			if(type.equals(String.class.getCanonicalName())) {
				type = "string";
			}
			else if(type.equals(Boolean.class.getCanonicalName())) {
				type = "boolean";
			}
			else if(type.equals(Short.class.getCanonicalName())) {
				type = "short";
			}
			else if(type.equals(Integer.class.getCanonicalName())) {
				type = "integer";
			}
			else if(type.equals(Float.class.getCanonicalName())) {
				type = "float";
			}
			else if(type.equals(Double.class.getCanonicalName())) {
				type = "double";
			}
			else if(type.equals(Byte.class.getCanonicalName())) {
				type = "byte";
			}
			else if(type.equals(java.sql.Date.class.getCanonicalName())) {
				type = "date";
			}
			else if(type.equals(BigDecimal.class.getCanonicalName())) {
				type = "double";
			}
			else if(type.equals(Long.class.getCanonicalName())) {
				type = "integer";
			}

			fieldDetail.addProperty("type", type);

			schemas.add(jdbcFieldVO.getName(), fieldDetail);

			if(jdbcFieldVO.getIdentifier() != null && jdbcFieldVO.getIdentifier()) {
				JsonArray fieldIndex = schemasCross.getAsJsonArray(tableVO.getName());

				if(fieldIndex == null) {
					fieldIndex = new JsonArray();
				}

				fieldIndex.add(new JsonPrimitive(jdbcFieldVO.getName()));
				
				schemasCross.add(tableVO.getName(), fieldIndex);
			}
		}

		if(!this.justExportJSonData) {
			CommandPostSchema schemaCommand = new CommandPostSchema(tenantId, datasourceId, (panelJDBCEntities != null ? panelJDBCEntities.getTextTemplateName().getText() : tableVO.getName()), completeSchema.toString());

			try {
				connection.executeCommand(schemaCommand);
				LogManagerDispatcher.getInstance().register("Sent the schema for the type '" + (panelJDBCEntities != null ? panelJDBCEntities.getTextTemplateName().getText() : tableVO.getName()) + "' in " + (totalTimeSchema - System.currentTimeMillis()) + "ms.");
			}
			catch(Exception e) {
				System.err.println("Error sending schema: " + e.getMessage());
				e.printStackTrace();
			}

			LogManagerDispatcher.getInstance().register("I am going to send " + totalRecords + " records...");
		}

		for(int totalDataSend=0; totalDataSend<totalRecords;) {
			JsonArray loteInitial = JDBCConnectionFactory.loadData(jdbcConnectionVO, tableVO, totalDataSend, batchSize);
			JsonArray lote = new JsonArray();

			if(this.justExportJSonData) {
				totalDataJSon.append(loteInitial.toString() + "\n");
				totalDataSend += loteInitial.size();
			}
			else {
				long initialTimeMD5 = System.currentTimeMillis();

				for(int i=0; i<loteInitial.size(); i++) {
					JsonElement jsonElement = loteInitial.get(i);

					try {
						MessageDigest m = MessageDigest.getInstance("MD5");
						m.reset();
						m.update(jsonElement.toString().getBytes());
						byte[] digest = m.digest();
						BigInteger bigInt = new BigInteger(1,digest);
						String hashtext = bigInt.toString(16);

						if(panelJDBCEntities.getIgnoreLocalCache()) {
							lote.add(jsonElement);
						}
						else {
							StoredRecordHashVO vo = new StoredRecordHashVO(fluigDataProfileVO.getName(), jdbcConnectionVO.getName(), tableVO.getName(), hashtext);
							StoredAbstractVO hash = PersistenceEngine.getInstance().getByName(vo.getName(), StoredRecordHashVO.class);
	
							if(hash != null) {
								continue;
							}
							else {
								lote.add(jsonElement);
								PersistenceEngine.getInstance().save(vo);
							}
						}
					} catch (NoSuchAlgorithmException e) {
					}
				}

				System.out.println("Took '" + (System.currentTimeMillis() - initialTimeMD5) + "' for hash operatins..");

				if (lote.size() > 0) {
					CommandPostStaging staging = null;

					if(panelJDBCEntities == null || panelJDBCEntities.getCheckBoxCompress()) {
						staging = new CommandPostStagingC(tenantId, datasourceId, (panelJDBCEntities != null ? panelJDBCEntities.getTextTemplateName().getText() : tableVO.getName()), lote);
					}
					else {
						staging = new CommandPostStaging(tenantId, datasourceId, (panelJDBCEntities != null ? panelJDBCEntities.getTextTemplateName().getText() : tableVO.getName()), lote);
					}

					long initialTime = System.currentTimeMillis();
					long endTime = initialTime;

					String additionalInformation = "";

					if(staging instanceof CommandPostStagingC) {
						additionalInformation = " (compressed)";
					}

					try {
						connection.executeCommand(staging);
						endTime = System.currentTimeMillis();
						totalDataSend += loteInitial.size();
						this.quantityRecords += lote.size();
					}
					catch(Exception e) {
						System.err.println("Error: " + e.getMessage());
						e.printStackTrace();
					}
					
					double n1 = totalDataSend;
					double n2 = totalRecords;
					double result = n1 / n2;
					DecimalFormat decF = new DecimalFormat("0.00");
					
					LogManagerDispatcher.getInstance().register("Sent " + lote.size() + additionalInformation + " records in " + (endTime - initialTime) + "ms, " + decF.format(result*100) + "% completed (" + totalDataSend + " in total).");
				}
				else {
					totalDataSend += loteInitial.size();
				}
			}

			ProcessTypeEnum processType = ProcessTypeEnum.SEND_DATA;

			if(this.justExportJSonData) {
				processType = ProcessTypeEnum.EXPORT_DATA;
			}

			SendDataFluigDataUpdateProcessDispatcher.getInstance().fireSendDataFluigDataUpdateProcessEvent(new SendDataFluigDataUpdateProcessEvent(totalDataSend, totalRecords, processType));
		}

		LogManagerDispatcher.getInstance().register("Finished the process now: " + df.format(Calendar.getInstance().getTime()) + "\n\n");

		ProcessTypeEnum processTypeEnum = ProcessTypeEnum.SEND_DATA;

		if(justExportJSonData) {
			processTypeEnum = ProcessTypeEnum.EXPORT_DATA;
		}

		SendDataFluigDataDoneDispatcher.getInstance().fireSendDataFluigDataDoneEvent(new SendDataFluigDataDoneEvent(processTypeEnum, totalDataJSon.toString()));
	}

	public Integer getQuantityRecords() {
		return quantityRecords;
	}
}
