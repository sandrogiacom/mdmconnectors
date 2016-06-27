package com.totvslabs.mdm.client.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.totvslabs.mdm.client.pojo.JDBCFieldVO;
import com.totvslabs.mdm.client.pojo.JDBCTableVO;
import com.totvslabs.mdm.client.pojo.MDMJsonData;
import com.totvslabs.mdm.client.pojo.StoredConfigurationVO;
import com.totvslabs.mdm.client.pojo.StoredFluigDataProfileVO;
import com.totvslabs.mdm.client.pojo.StoredJDBCConnectionVO;
import com.totvslabs.mdm.client.ui.events.LogManagerDispatcher;
import com.totvslabs.mdm.client.ui.events.ProcessStatusEnum;
import com.totvslabs.mdm.client.ui.events.SendDataFluigDataDoneDispatcher;
import com.totvslabs.mdm.client.ui.events.SendDataFluigDataDoneEvent;
import com.totvslabs.mdm.client.ui.events.SendDataFluigDataUpdateProcessDispatcher;
import com.totvslabs.mdm.client.ui.events.SendDataFluigDataUpdateProcessEvent;
import com.totvslabs.mdm.restclient.MDMRestConnection;
import com.totvslabs.mdm.restclient.MDMRestConnectionFactory;
import com.totvslabs.mdm.restclient.command.CommandPostSchema;
import com.totvslabs.mdm.restclient.command.CommandPostStaging;
import com.totvslabs.mdm.restclient.command.CommandPostStagingC;
import com.totvslabs.mdm.restclient.command.CommandUpdateSchema;

public class ThreadExportData implements Runnable {
	private JDBCTableVO tableVO;
	private StoredJDBCConnectionVO jdbcConnectionVO;
	private StoredFluigDataProfileVO fluigDataProfileVO;
	private StoredConfigurationVO configurationVO;
	private String mdmServerURL;
	private String tenantId;
	private String datasourceId;
	private boolean justExportJSonData = Boolean.FALSE;
	private Integer quantityRecords = 0;

	//file process
	private String fileConnectionName;
	private String file;

	public ThreadExportData(JDBCTableVO tableVO, StoredJDBCConnectionVO jdbcConnectionVO) {
		this(null, tableVO, jdbcConnectionVO);
		this.justExportJSonData = Boolean.TRUE;
	}

	public ThreadExportData(StoredConfigurationVO configurationVO, StoredFluigDataProfileVO mdmProfile, JDBCTableVO tableVO, StoredJDBCConnectionVO jdbcConnectionVO) {
		this(mdmProfile, tableVO, jdbcConnectionVO);
		this.configurationVO = configurationVO;
	}

	public ThreadExportData(StoredFluigDataProfileVO mdmProfile, JDBCTableVO tableVO, StoredJDBCConnectionVO jdbcConnectionVO) {
		this.jdbcConnectionVO = jdbcConnectionVO;
		this.fluigDataProfileVO = mdmProfile;
		this.mdmServerURL = mdmProfile != null ? mdmProfile.getServerURL() : null;
		this.tenantId = mdmProfile != null ? mdmProfile.getDomain() : null;
		this.datasourceId = mdmProfile != null ? mdmProfile.getDatasourceID() : null;
		this.tableVO = tableVO;
	}

	public ThreadExportData(StoredFluigDataProfileVO mdmProfile, String fileConnectionName, String file) {
		this.fluigDataProfileVO = mdmProfile;
		this.mdmServerURL = mdmProfile != null ? mdmProfile.getServerURL() : null;
		this.tenantId = mdmProfile != null ? mdmProfile.getDomain() : null;
		this.datasourceId = mdmProfile != null ? mdmProfile.getDatasourceID() : null;
		this.fileConnectionName = fileConnectionName;
		this.file = file;
	}

	@Override
	public void run() {
		if(file != null) {
			sendDataFile(this.fileConnectionName, this.file);
		}
		else {
			sendDataJDBC();
		}
	}

	private void sendDataFile(String connectionName, String entity) {
		DateFormat df = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z");
		LogManagerDispatcher.getInstance().register("Starting the process now: " + df.format(Calendar.getInstance().getTime()));
		MDMRestConnection connection = MDMRestConnectionFactory.getConnection(mdmServerURL);

		List<String> header = FileConsume.getInstance(connectionName).getHeader(entity);
		List<String> pks = FileConsume.getInstance(connectionName).getPks(entity);
		String typeName = FileConsume.getInstance(connectionName).getTypeName(entity);

		Long totalTimeSchema = System.currentTimeMillis();
		JsonObject completeSchema = new JsonObject();
		JsonObject schema = new JsonObject();
		JsonObject schemaCross = new JsonObject();
		JsonObject schemas = new JsonObject();
		JsonObject schemasCross = new JsonObject();
		schema.add("properties", schemas);
		schemaCross.add("mdmCrossreference", schemasCross);
		completeSchema.add("mdmStagingMapping", schema);
		completeSchema.add("mdmCrosswalkTemplate", schemaCross);

		int fieldCount = 0;

		for (String field : header) {
			JsonObject fieldDetail = new JsonObject();
			fieldDetail.addProperty("type", "string");

			schemas.add(field, fieldDetail);

			if(fieldCount == 0 || pks.contains(field)) {
				JsonArray fieldIndex = schemasCross.getAsJsonArray(typeName);

				if(fieldIndex == null) {
					fieldIndex = new JsonArray();
				}

				fieldIndex.add(new JsonPrimitive(field));
				
				schemasCross.add(typeName, fieldIndex);
			}

			fieldCount++;
		}

		if(header.size() > 0) {
			CommandPostSchema schemaCommand = new CommandPostSchema(tenantId, datasourceId, typeName, completeSchema.toString());

			try {
				connection.executeCommand(schemaCommand);
				LogManagerDispatcher.getInstance().register("Sent the schema for the type '" + (typeName) + "' in " + (totalTimeSchema - System.currentTimeMillis()) + "ms.");
			}
			catch(Exception e) {
				System.err.println("Error sending schema: " + e.getMessage());
				e.printStackTrace();
			}
		}

		while(FileConsume.getInstance(connectionName).hasRecords(entity)) {
			JsonArray lote = FileConsume.getInstance(connectionName).getRecords(entity);

			LogManagerDispatcher.getInstance().register("I am going to send " + lote.size() + " records...");
			CommandPostStaging staging = new CommandPostStagingC(tenantId, datasourceId, typeName, lote);

			long initialTime = System.currentTimeMillis();
			long endTime = initialTime;

			try {
				connection.executeCommand(staging);
				endTime = System.currentTimeMillis();
			}
			catch(Exception e) {
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace();
			}

			LogManagerDispatcher.getInstance().register("Sent " + FileConsume.getInstance(connectionName).getRecordsSent() + " records in " + (endTime - initialTime) + "ms (" + FileConsume.getInstance(connectionName).getTotalRecordsToSend().intValue() + " in total).");
			SendDataFluigDataUpdateProcessDispatcher.getInstance().fireSendDataFluigDataUpdateProcessEvent(new SendDataFluigDataUpdateProcessEvent(FileConsume.getInstance(connectionName).getRecordsSent().intValue(), FileConsume.getInstance(connectionName).getTotalRecordsToSend().intValue(), ProcessTypeEnum.SEND_DATA));
		}

		LogManagerDispatcher.getInstance().register("Finished the process now: " + df.format(Calendar.getInstance().getTime()) + "\n\n");
		SendDataFluigDataDoneDispatcher.getInstance().fireSendDataFluigDataDoneEvent(new SendDataFluigDataDoneEvent(ProcessTypeEnum.SEND_DATA, ProcessStatusEnum.DONE, FileConsume.getInstance(connectionName).getTotalRecordsToSend(entity).toString()));
	}

	private void processNestedField(JsonObject jsonField, JsonObject finalSchema) {
		Set<Entry<String, JsonElement>> entrySet = jsonField.entrySet();

		for (Entry<String, JsonElement> entry : entrySet) {
			finalSchema.addProperty("type", "nested");

			if(entry.getValue().isJsonObject()) {
				JsonObject fields = new JsonObject();
				JsonObject fieldProperties = new JsonObject();
				fields.add(entry.getKey(), fieldProperties);

				if(finalSchema.has("properties")) {
					finalSchema.get("properties").getAsJsonObject().add(entry.getKey(), fieldProperties);
				}
				else {
					finalSchema.add("properties", fields);
				}

				this.processNestedField(entry.getValue().getAsJsonObject(), fieldProperties);
			}
			else {
				JsonObject fields = new JsonObject();
				JsonObject fieldType = new JsonObject();
				fieldType.addProperty("type", parseType(entry.getValue().getAsString()));
				fields.add(entry.getKey(), fieldType);
				
				if(finalSchema.has("properties")) {
					finalSchema.get("properties").getAsJsonObject().add(entry.getKey(), fieldType);
				}
				else {
					finalSchema.add("properties", fields);
				}
			}
		}
	}

	private String parseType(String type) {
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
			type = "string";
		}
		else if(type.equals(java.util.Date.class.getCanonicalName())) {
			type = "string";
		}
		else if(type.equals(BigDecimal.class.getCanonicalName())) {
			type = "double";
		}
		else if(type.equals(Long.class.getCanonicalName())) {
			type = "long";
		}
		else if(type.equals(Timestamp.class.getCanonicalName())) {
			type = "string";
		}

		return type;
	}

	private JsonObject loadSchema() {
		JsonObject completeSchema = new JsonObject();
		JsonObject schema = new JsonObject();
		JsonObject schemaCross = new JsonObject();
		JsonObject schemas = new JsonObject();
		JsonObject schemasCross = new JsonObject();
		schema.add("properties", schemas);
		schemaCross.add("mdmCrossreference", schemasCross);
		completeSchema.add("mdmStagingMapping", schema);
		completeSchema.add("mdmCrosswalkTemplate", schemaCross);

		List<JDBCFieldVO> fields = this.tableVO.getFields();
		String firstField = null;

		for (JDBCFieldVO jdbcFieldVO : fields) {
			JsonObject fieldDetail = new JsonObject();

			String type = parseType(jdbcFieldVO.getType());

			if(type.equals("nested") || type.equals("object")) {
				JsonObject objectSchema = new JsonObject();
				schemas.add(jdbcFieldVO.getName(), objectSchema);
				this.processNestedField(jdbcFieldVO.getMembers(), objectSchema);
			}
			else {
				fieldDetail.addProperty("type", type);
				schemas.add(jdbcFieldVO.getName(), fieldDetail);
			}

			if(firstField == null) {
				firstField = jdbcFieldVO.getName();
			}

			if(jdbcFieldVO.getIdentifier() != null && jdbcFieldVO.getIdentifier()) {
				JsonArray fieldIndex = schemasCross.getAsJsonArray(tableVO.getInternalName());

				if(fieldIndex == null) {
					fieldIndex = new JsonArray();
				}

				fieldIndex.add(new JsonPrimitive(jdbcFieldVO.getName()));

				schemasCross.add(tableVO.getInternalName(), fieldIndex);
			}
		}

		if(schemasCross.getAsJsonArray(tableVO.getInternalName()) == null) {
			JsonArray fieldIndex = new JsonArray();
			fieldIndex.add(new JsonPrimitive(firstField));
			schemasCross.add(tableVO.getInternalName(), fieldIndex);
		}

		return completeSchema;
	}

	private void sendSchema() {
		Long totalTimeSchema = System.currentTimeMillis();
		JsonObject completeSchema = this.loadSchema();

		MDMRestConnection connection = MDMRestConnectionFactory.getConnection(mdmServerURL);

		CommandPostSchema schemaCommand = new CommandPostSchema(tenantId, datasourceId, tableVO.getInternalName(), completeSchema.toString());

		try {
			connection.executeCommand(schemaCommand);
			LogManagerDispatcher.getInstance().register("Sent the schema for the type '" + tableVO.getInternalName() + "' in " + (totalTimeSchema - System.currentTimeMillis()) + "ms.");
		}
		catch(Exception e) {
			System.err.println("Error sending schema: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void updateSchema() {
		Long totalTimeSchema = System.currentTimeMillis();
		JsonObject completeSchema = this.loadSchema();

		MDMRestConnection connection = MDMRestConnectionFactory.getConnection(mdmServerURL);

		CommandUpdateSchema schemaCommand = new CommandUpdateSchema(tenantId, datasourceId, tableVO.getInternalName(), completeSchema.toString());
		System.out.println("schema: " + completeSchema.toString());
		try {
			connection.executeCommand(schemaCommand);
			LogManagerDispatcher.getInstance().register("Sent the schema for the type '" + tableVO.getInternalName() + "' in " + (totalTimeSchema - System.currentTimeMillis()) + "ms.");
		}
		catch(Exception e) {
			System.err.println("Error sending schema: " + e.getMessage());
			e.printStackTrace();
		}
	}



	private void sendDataJDBC() {
		DateFormat df = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z");

		LogManagerDispatcher.getInstance().register("Starting the process now: " + df.format(Calendar.getInstance().getTime()));
		Long totalRecords = tableVO.getTotalRecords();

		MDMRestConnection connection = MDMRestConnectionFactory.getConnection(mdmServerURL);

		System.out.println("tableVO: " + tableVO.getFields());

		if(!this.justExportJSonData) {
			this.sendSchema();

			LogManagerDispatcher.getInstance().register("I am going to send " + totalRecords + " records...");
		}

		//adding the entity to be sent
		JDBCConnectionConsume.getInstance(jdbcConnectionVO.getName()).addEntity(jdbcConnectionVO, fluigDataProfileVO, tableVO);
		Integer affectedRecords = 0;

		while(JDBCConnectionConsume.getInstance(jdbcConnectionVO.getName()).hasRecords(tableVO.getInternalName())) {
			MDMJsonData lote = JDBCConnectionConsume.getInstance(jdbcConnectionVO.getName()).getRecords(tableVO.getInternalName());
			long initialTime = System.currentTimeMillis();
			long endTime = initialTime;

			if(lote.getNewFields() != null && lote.getNewFields().size() > 0) {
				//validate the schema
//				CommandGetSchema getSchema = new CommandGetSchema(tableVO.getInternalName());
//				EnvelopeVO executeCommand = connection.executeCommand(getSchema);

				this.updateSchema();

				//convert the actual tableVO to schema
				//compare 2 Json structure
				//send the schema update (map this new command)
			}

			if(this.justExportJSonData) {
				SendDataFluigDataDoneDispatcher.getInstance().fireSendDataFluigDataDoneEvent(new SendDataFluigDataDoneEvent(ProcessTypeEnum.EXPORT_DATA, ProcessStatusEnum.WORKING, lote.getData().toString()));
				endTime = System.currentTimeMillis();
			}
			else {
				CommandPostStaging staging = new CommandPostStagingC(tenantId, datasourceId, tableVO.getInternalName(), lote.getData());

				JsonArray arr = new JsonArray();
				for(int i=0; i<100; i++) {
					arr.add(lote.getData().get(i));
				}

				System.out.println(arr);

				try {
					connection.executeCommand(staging);
					endTime = System.currentTimeMillis();
					this.quantityRecords += lote.getData().size();
				}
				catch(Exception e) {
					System.err.println("Error: " + e.getMessage());
					e.printStackTrace();
				}
				
				affectedRecords += lote.getData().size();
			}

			double n1 = JDBCConnectionConsume.getInstance(jdbcConnectionVO.getName()).getTotalRecordsSent(tableVO.getInternalName());
			double n2 = totalRecords;
			double result = n1 / n2;

			DecimalFormat decF = new DecimalFormat("0.00");
			LogManagerDispatcher.getInstance().register("Sent " + lote.getData().size() + " records in " + (endTime - initialTime) + "ms, " + decF.format(result*100) + "% completed (" + n1 + " in total).");
		}

		if(configurationVO != null) {
			this.configurationVO.setLastExecution(new Date());
			this.configurationVO.setQuantity(affectedRecords);
		}

		LogManagerDispatcher.getInstance().register("Finished the process now: " + df.format(Calendar.getInstance().getTime()) + "\n\n");

		ProcessTypeEnum processTypeEnum = ProcessTypeEnum.SEND_DATA;

		if(justExportJSonData) {
			processTypeEnum = ProcessTypeEnum.EXPORT_DATA;
		}

		SendDataFluigDataDoneDispatcher.getInstance().fireSendDataFluigDataDoneEvent(new SendDataFluigDataDoneEvent(processTypeEnum, ProcessStatusEnum.DONE, ""));
	}

	public Integer getQuantityRecords() {
		return quantityRecords;
	}
}
