package com.totvslabs.mdm.client.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.Thread.State;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
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
import com.totvslabs.mdm.client.pojo.StoredFluigDataProfileVO;
import com.totvslabs.mdm.client.ui.MDMConsumerFDEntityTableModel;
import com.totvslabs.mdm.restclient.MDMRestAuthentication;
import com.totvslabs.mdm.restclient.MDMRestConnectionFactory;
import com.totvslabs.mdm.restclient.MDMRestConnectionTypeEnum;
import com.totvslabs.mdm.restclient.command.CommandDataConsumption;
import com.totvslabs.mdm.restclient.vo.DataConsumptionEntitiesRecordVO;
import com.totvslabs.mdm.restclient.vo.DataConsumptionVO;
import com.totvslabs.mdm.restclient.vo.EnvelopeVO;
import com.totvslabs.mdm.restclient.vo.GoldenRecordVO;

public class MDMConsumerProtheus implements ActionListener, Runnable {
	private Thread consomeProcess;
	private Boolean stop = Boolean.FALSE;

	private JButton buttonImportData;
	private MDMConsumerFDEntityTableModel tableModelEntitiesFD;
	private FDEntityVO entity;
	private List<FDFieldVO> fields;
	private StoredFluigDataProfileVO fluigDataProfile;
	private JTextField textCounter;
	private JLabel labelCounter;
	private JTextField textProtheusWS;

	public MDMConsumerProtheus(JButton buttonImportData, MDMConsumerFDEntityTableModel tableModelEntitiesFD, FDEntityVO entity, List<FDFieldVO> fields, StoredFluigDataProfileVO fluigDataProfile, JLabel labelCounter, JTextField textCounter, JTextField textProtheusWS) {
		this.buttonImportData = buttonImportData;
		this.tableModelEntitiesFD = tableModelEntitiesFD;
		this.entity = entity;
		this.fields = fields;
		this.fluigDataProfile = fluigDataProfile;
		this.labelCounter = labelCounter;
		this.textCounter = textCounter;
		this.textProtheusWS = textProtheusWS;
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
		Map<String, FDFieldVO> mappedFields = new HashMap<String, FDFieldVO>();

		for (FDFieldVO fdFieldVO : fields) {
			if(fdFieldVO.getProtheusField() != null) {
				mappedFields.put(fdFieldVO.getProtheusField(), fdFieldVO);
			}
		}

		Set<String> protheusColumns = mappedFields.keySet();
		
		if(entity == null) {
			return;
		}

		MDMRestAuthentication.getInstance(MDMRestConnectionTypeEnum.CONSUME, fluigDataProfile.getServerURL(), fluigDataProfile.getDomain(), fluigDataProfile.getDatasourceID(), fluigDataProfile.getUsername(), fluigDataProfile.getPassword());
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
							value = value.replaceAll("-", "");
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
//						if(fdFieldVO.getName().equals("mdmdba") && value != null) {//TODO: Fixme I applied this rule on MDM
//							value = value.trim();
//
//							if(value.length() > 20) {
//								value = value.substring(0, 20);
//							}
//						}
//						if(fdFieldVO.getName().equals("mdmphonenumber") && value != null) {//TODO: Fixme I applied this rule on MDM
//							value = value.trim();
//
//							if(value.length() > 15) {
//								value = value.substring(0, 15);
//							}
//						}

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
//			fdFieldVO.setChildren(Boolean.FALSE); //workarround!!!
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
