package com.totvslabs.mdm.client.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.Thread.State;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.totvslabs.mdm.client.pojo.FDEntityVO;
import com.totvslabs.mdm.client.pojo.StoredFluigDataProfileVO;
import com.totvslabs.mdm.client.ui.MDMConsumerFDEntityTableModel;
import com.totvslabs.mdm.client.ui.SendJDBCDatabaseConnection;
import com.totvslabs.mdm.restclient.MDMRestAuthentication;
import com.totvslabs.mdm.restclient.MDMRestConnectionFactory;
import com.totvslabs.mdm.restclient.MDMRestConnectionTypeEnum;
import com.totvslabs.mdm.restclient.command.CommandConfirmationConsumption;
import com.totvslabs.mdm.restclient.command.CommandDataConsumption;
import com.totvslabs.mdm.restclient.command.CommandGetSchema;
import com.totvslabs.mdm.restclient.vo.DataConsumptionEntitiesRecordVO;
import com.totvslabs.mdm.restclient.vo.EnvelopeVO;
import com.totvslabs.mdm.restclient.vo.GoldenRecordCrossWalkVO;
import com.totvslabs.mdm.restclient.vo.GoldenRecordVO;
import com.totvslabs.mdm.restclient.vo.MappingVO;
import com.totvslabs.mdm.restclient.vo.StagingSchemaVO;

public class MDMConsumerTop implements ActionListener, Runnable {
	private Thread consomeProcess;
	private Boolean stop = Boolean.FALSE;

	private JButton buttonImportData;
	private MDMConsumerFDEntityTableModel tableModelEntitiesFD;
	private StoredFluigDataProfileVO fluigDataProfile;
	private JTextField textCounter;
	private JLabel labelCounter;
	private JTextField textProtheusWS;
	private JTable tableEntitiesFD;
	private static Map<String, String> ENTITIES_MAP = new HashMap<String, String>();
	static {
		ENTITIES_MAP.put("mdmorderdetail", "titmmov");
	}
	private static Map<String, List<String>> FIELDS_TO_REMOVE = new HashMap<String, List<String>>();
	static {
		List<String> fields = new ArrayList<String>();
		fields.add("idintegracaotmov");
		fields.add("codigoprd");
		

		FIELDS_TO_REMOVE.put("mdmorderdetail", fields);
	}

	public MDMConsumerTop(JButton buttonImportData, MDMConsumerFDEntityTableModel tableModelEntitiesFD, JTable tableEntitiesFD, StoredFluigDataProfileVO fluigDataProfile, JLabel labelCounter, JTextField textCounter, JTextField textProtheusWS) {
		this.buttonImportData = buttonImportData;
		this.tableModelEntitiesFD = tableModelEntitiesFD;
		this.fluigDataProfile = fluigDataProfile;
		this.labelCounter = labelCounter;
		this.textCounter = textCounter;
		this.textProtheusWS = textProtheusWS;
		this.tableEntitiesFD = tableEntitiesFD; 
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

	@Override
	public void run() {
		FDEntityVO entity = tableModelEntitiesFD.getRowObject(tableEntitiesFD.getSelectedRow());

		if(entity == null) {
			return;
		}

		Map<String, String> fieldsType = new HashMap<String, String>();

		CommandGetSchema schema = new CommandGetSchema(ENTITIES_MAP.get(entity.getName()));
		EnvelopeVO executeCommandGetSchema = MDMRestConnectionFactory.getConnection(fluigDataProfile.getServerURL()).executeCommand(schema);
		MappingVO fieldsSchemaMapping = ((StagingSchemaVO) executeCommandGetSchema.getHits().iterator().next()).getmdmStagingMapping();

		Set<String> keySet = fieldsSchemaMapping.getProperties().keySet();
		for (String string : keySet) {
			fieldsType.put(string, fieldsSchemaMapping.getProperties().get(string).getType());
		}

		MDMRestAuthentication.getInstance(MDMRestConnectionTypeEnum.CONSUME, fluigDataProfile.getServerURL(), fluigDataProfile.getDomain(), fluigDataProfile.getDatasourceID(), fluigDataProfile.getUsername(), fluigDataProfile.getPassword());
		MDMRestConnectionFactory.getConnection(fluigDataProfile.getServerURL());

		Integer numOfPendingRecords = 0;

		try {
			do {
				CommandDataConsumption commandDataConsumption = new CommandDataConsumption(entity.getEntityId(), Integer.parseInt(textCounter.getText()), 10);
				commandDataConsumption.setPageSize(500);
				EnvelopeVO executeCommand = MDMRestConnectionFactory.getConnection(fluigDataProfile.getServerURL()).executeCommand(commandDataConsumption);

				DataConsumptionEntitiesRecordVO entitiesRecordVO = (DataConsumptionEntitiesRecordVO) executeCommand.getHits().get(0);

				numOfPendingRecords = entitiesRecordVO.getNumOfPendingRecords();
				List<GoldenRecordVO> goldenRecords = entitiesRecordVO.getGoldenRecords();

				labelCounter.setText("Fluig Data Counter (Processing: " + goldenRecords.size() + ", pending records: " + numOfPendingRecords + "): ");;
				labelCounter.updateUI();

				if(goldenRecords != null) {
					List<String> mdmIdsForConfirmation = new ArrayList<String>();

					for (GoldenRecordVO goldenRecordVO : goldenRecords) {
						Map<String, Map<String, String>> mdmCrossreference = new HashMap<String, Map<String, String>>();
						Set<String> contributors = new HashSet<String>();

						for(GoldenRecordCrossWalkVO crossWalkVO : goldenRecordVO.getMdmCrosswalk()) {
							contributors.add(crossWalkVO.getMdmApplicationId());

							if(crossWalkVO.getMdmApplicationId().equals(fluigDataProfile.getDatasourceID())) {
								for (String key : crossWalkVO.getMdmCrossreference().keySet()) {
									String newKey = key;

									//FIXME: Use the default ETL from Fluig Data to avoid this control
									if(key.contains("_etl")) {
										newKey = key.substring(0, key.indexOf("_etl"));
									}

									mdmCrossreference.put(newKey, crossWalkVO.getMdmCrossreference().get(key));
								}
							}
						}

						if(contributors.size() <= 1) {
							//confirm, record not good enough
							mdmIdsForConfirmation.add(goldenRecordVO.getMdmId());
						}
						else {
							boolean result = consumeRecords(entity, goldenRecordVO.getMdmGoldenFieldAndValues(), mdmCrossreference, fieldsType);

							if(result) {
								mdmIdsForConfirmation.add(goldenRecordVO.getMdmId());
							}
							else {
//								System.err.println("Error processing the record '" + goldenRecordVO + "', please take a look to fix it.");
							}
						}
					}

					CommandConfirmationConsumption consumptionConfirmation = new CommandConfirmationConsumption();

					for (String string : mdmIdsForConfirmation) {
						consumptionConfirmation.add(entity.getEntityId(), string);
					}

					if(mdmIdsForConfirmation != null && mdmIdsForConfirmation.size() > 0) {
						EnvelopeVO executeCommandConfirmation = MDMRestConnectionFactory.getConnection(fluigDataProfile.getServerURL()).executeCommand(consumptionConfirmation);
						System.out.println(executeCommandConfirmation);
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

	enum TypeMapData {
		KEY, VALUE, CONDITIONAL;
	}

	private String returnAsString(FDEntityVO entity, Map<String, String> data, TypeMapData type, Map<String, String> fieldsType) {
		StringBuffer sb = new StringBuffer();

		switch(type) {
			case KEY:
				Iterator<String> iteratorKey = data.keySet().iterator();
				while(iteratorKey.hasNext()) {
					String keyKey = iteratorKey.next();

					if(FIELDS_TO_REMOVE.get(entity.getName()).contains(keyKey)) {
						continue;
					}

					sb.append(keyKey);

					if(iteratorKey.hasNext()) {
						sb.append(",");
					}
				}
				break;

			case VALUE:
				Iterator<String> iteratorValue = data.keySet().iterator();
				while(iteratorValue.hasNext()) {
					String string = iteratorValue.next();

					if(FIELDS_TO_REMOVE.get(entity.getName()).contains(string)) {
						continue;
					}

//					boolean handleAsString = (entity.getMdmFieldsFull().get(string).getMdmType().equals("STRING") || entity.getMdmFieldsFull().get(string).getMdmType().equals("DATE"));
//					boolean handleAsString = true;
					boolean handleAsString = (fieldsType.get(string).equals("string") || fieldsType.get(string).equals("date"));
					
					sb.append((handleAsString ? "'" : "") + data.get(string) + (handleAsString ? "'" : ""));
					
					if(iteratorValue.hasNext()) {
						sb.append(",");
					}
				}
				break;

			case CONDITIONAL:
				Iterator<String> iteratorKeyConditional = data.keySet().iterator();
				while(iteratorKeyConditional.hasNext()) {
					String keyConditional = iteratorKeyConditional.next();

					if(FIELDS_TO_REMOVE.get(entity.getName()).contains(keyConditional)) {
						continue;
					}

					sb.append(keyConditional + "=" + data.get(keyConditional));

					if(iteratorKeyConditional.hasNext()) {
						sb.append(" AND ");
					}
				}
				break;
		}

		return sb.toString();
	}

	private boolean consumeRecords(FDEntityVO entity, Map<String, Object> data, Map<String, Map<String, String>> mdmCrossreference, Map<String, String> fieldsType) {
		LinkedHashMap<String, String> fields = new LinkedHashMap<String, String>();
		StringBuffer keyValue = new StringBuffer();

		Set<String> keySet = data.keySet();
		for (String string : keySet) {
			if(FIELDS_TO_REMOVE.get(entity.getName()).contains(string)) {
				continue;
			}

			String value = (String) data.get(string);
			boolean handleAsString = (fieldsType.get(string).equals("string") || fieldsType.get(string).equals("date"));
//			boolean handleAsString = (entity.getMdmFieldsFull().get(string).getMdmType().equals("STRING") || entity.getMdmFieldsFull().get(string).getMdmType().equals("DATE"));
//			boolean handleAsString = true;

			if(value != null && value.length() > 0) {
				fields.put(string, value);

				if(keyValue.length() != 0) {
					keyValue.append(",");
				}

				keyValue.append(string + " = " + (handleAsString ? "'" : "") +  (String) data.get(string) + (handleAsString ? "'" : ""));
			}
		}

		String sql = null;
		if(mdmCrossreference != null && mdmCrossreference.size() > 0) {
			String entityName = mdmCrossreference.keySet().iterator().next();

			sql = "UPDATE " + entityName + " SET " + keyValue + " WHERE " + returnAsString(entity, mdmCrossreference.get(entityName), TypeMapData.CONDITIONAL, fieldsType);
		}
		else {
			sql = "INSERT INTO " + ENTITIES_MAP.get(entity.getName()) + "(" + returnAsString(entity, fields, TypeMapData.KEY, fieldsType) + ") values (" + returnAsString(entity, fields, TypeMapData.VALUE, fieldsType) + ")";
		}

		System.out.println("sql: " + sql);

		Connection connection = (Connection) DBConnectionFactory.getDb("sqlserver").getConnection(this.textProtheusWS.getText(), SendJDBCDatabaseConnection.DB_SQLSERVER, "sa", "sa");
		try {
			Statement createStatement = connection.createStatement();
			createStatement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
}
