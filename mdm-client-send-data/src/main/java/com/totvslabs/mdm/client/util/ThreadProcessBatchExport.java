package com.totvslabs.mdm.client.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.totvslabs.mdm.client.pojo.FDEntityVO;
import com.totvslabs.mdm.client.pojo.FDFieldVO;
import com.totvslabs.mdm.client.pojo.StoredFluigDataProfileVO;
import com.totvslabs.mdm.restclient.MDMRestAuthentication;
import com.totvslabs.mdm.restclient.MDMRestConnectionFactory;
import com.totvslabs.mdm.restclient.MDMRestConnectionTypeEnum;
import com.totvslabs.mdm.restclient.command.CommandGetDataConsumers;
import com.totvslabs.mdm.restclient.command.CommandGetDataModel;
import com.totvslabs.mdm.restclient.command.CommandGetField;
import com.totvslabs.mdm.restclient.vo.DataConsumerVO;
import com.totvslabs.mdm.restclient.vo.DataModelVO;
import com.totvslabs.mdm.restclient.vo.EnvelopeVO;
import com.totvslabs.mdm.restclient.vo.FieldsVO;
import com.totvslabs.mdm.restclient.vo.GenericVO;

public class ThreadProcessBatchExport implements Runnable {
	private String fluigDataConnection;

	public ThreadProcessBatchExport(String fluigDataConnection) {
		this.fluigDataConnection = fluigDataConnection;
	}

	@Override
	public void run() {
		StoredFluigDataProfileVO fluigProfile = (StoredFluigDataProfileVO) PersistenceEngine.getInstance().getByName(fluigDataConnection, StoredFluigDataProfileVO.class);

		MDMRestAuthentication.getInstance(MDMRestConnectionTypeEnum.NORMAL, fluigProfile.getServerURL(), fluigProfile.getDomain(), fluigProfile.getDatasourceID(), fluigProfile.getUsername(), fluigProfile.getPassword());
		MDMRestConnectionFactory.getConnection(fluigProfile.getServerURL());

		MDMRestAuthentication.getInstance(MDMRestConnectionTypeEnum.CONSUME, fluigProfile.getServerURL(), fluigProfile.getDomain(), fluigProfile.getConsumerID(), fluigProfile.getUsername(), fluigProfile.getPassword());
		MDMRestConnectionFactory.getConnection(fluigProfile.getServerURL());

		CommandGetDataConsumers consumer = new CommandGetDataConsumers(fluigProfile.getConsumerID());
		EnvelopeVO executeCommand = MDMRestConnectionFactory.getConnection(fluigProfile.getServerURL()).executeCommand(consumer);

		List<GenericVO> hits = executeCommand.getHits();
		List<FDEntityVO> entitiesObj = new ArrayList<FDEntityVO>();

		if (hits != null && !hits.isEmpty()) {
			if(hits.get(0) instanceof DataConsumerVO) {
				List<String> entities = ((DataConsumerVO) hits.get(0)).getMdmEntitiesConsumed();

				if(entities != null) {
					for (String string : entities) {
						CommandGetDataModel dataModel = new CommandGetDataModel(string);
						EnvelopeVO executeCommandGetDataModel = MDMRestConnectionFactory.getConnection(fluigProfile.getServerURL()).executeCommand(dataModel);

						String next = ((DataModelVO) executeCommandGetDataModel.getHits().get(0)).getMdmLabel().keySet().iterator().next();

						FDEntityVO entity = new FDEntityVO();
						entity.setDescription(((DataModelVO) executeCommandGetDataModel.getHits().get(0)).getMdmLabel().get(next));
						entity.setName(((DataModelVO) executeCommandGetDataModel.getHits().get(0)).getMdmName());
						entity.setFieldsDetail(((DataConsumerVO) hits.get(0)).getMdmEntityDetails().get(string));
						entity.setEntityId(string);

						if(entity.getName().equalsIgnoreCase("mdmcustomer")) {
							entity.setWsURL("PROTHEUS_MATA030.XML");
						}

						entitiesObj.add(entity);
					}
				}
			}
		}

		for (FDEntityVO fdEntityVO : entitiesObj) {
			List<FDFieldVO> fieldsVO = new ArrayList<FDFieldVO>();
			List<String> getMdmFieldsConsumed = fdEntityVO.getFieldsDetail().getMdmFieldsConsumed();

			if(getMdmFieldsConsumed != null) {
				for (String string : getMdmFieldsConsumed) {
					CommandGetField commandGetField = new CommandGetField(string);
					EnvelopeVO executeCommandGetField = MDMRestConnectionFactory.getConnection(fluigProfile.getServerURL()).executeCommand(commandGetField);
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

//						comboBoxNestedFieldModel.addElement(new FDNestedFieldVO(fieldVO.getMdmName(), fieldVO.getMdmDescription().values().iterator().next(), childrenFields));
						//I am not exporting nested fields for now.
					}
					else {//normal fields
						FDFieldVO fdFieldVO = new FDFieldVO();
						fdFieldVO.setChildren(false);
						fdFieldVO.setDescription(fieldVO.getMdmDescription().values().iterator().next());
						fdFieldVO.setInstance(null);
						fdFieldVO.setName(fieldVO.getMdmName());
						fdFieldVO.setProtheusField(MappingUtil.mappings.get(MappingUtil.TYPE_CUSTOMER).get(MappingUtil.PROD_PROTHEUS).get(MappingUtil.NESTED_INSTANCE_DEFAULT).get(fieldVO.getMdmName()));
						fdFieldVO.setType(fieldVO.getMdmType());
						fdFieldVO.setForeignField((fieldVO.getMdmName().equals("mdmaddressid") && ((fdEntityVO.getName().equals("mdmhcp")) || ((fdEntityVO.getName().equals("mdmhca"))))) || (fieldVO.getMdmName().equals("mdmhealthcareprovidertaxonomycode") && ((fdEntityVO.getName().equals("mdmhcp")) || ((fdEntityVO.getName().equals("mdmhca"))))));//fixme: urgent!!!

						fieldsVO.add(fdFieldVO);
					}
				}
			}

			ThreadExportGoodDataConnector goodDataConnector = new ThreadExportGoodDataConnector(fluigProfile.getServerURL(), fdEntityVO, fieldsVO, null);
			Thread thread = new Thread(goodDataConnector);
			thread.run();
		}
	}
}
