package com.totvslabs.mdm.client.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.totvslabs.mdm.client.pojo.StoredAbstractVO;
import com.totvslabs.mdm.client.pojo.StoredFluigDataProfileVO;
import com.totvslabs.mdm.client.ui.events.MDMConnectionChangedDispatcher;
import com.totvslabs.mdm.client.ui.events.MDMConnectionChangedEvent;
import com.totvslabs.mdm.client.ui.events.MDMConnectionChangedListener;
import com.totvslabs.mdm.client.util.MappingUtil;
import com.totvslabs.mdm.client.util.ProtheusWebService;
import com.totvslabs.mdm.restclient.MDMRestAuthentication;
import com.totvslabs.mdm.restclient.MDMRestConnectionFactory;
import com.totvslabs.mdm.restclient.command.CommandDataConsumption;
import com.totvslabs.mdm.restclient.command.CommandGetDataConsumers;
import com.totvslabs.mdm.restclient.command.CommandGetDataModel;
import com.totvslabs.mdm.restclient.command.CommandGetField;
import com.totvslabs.mdm.restclient.vo.DataConsumerDetailsVO;
import com.totvslabs.mdm.restclient.vo.DataConsumerVO;
import com.totvslabs.mdm.restclient.vo.DataConsumptionVO;
import com.totvslabs.mdm.restclient.vo.DataModelVO;
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
	private JComboBox<String> textNestedInstnace;

	private JButton buttonAddNestedInstance;
	private JButton buttonImportData;

	private JLabel labelCounter;
	private JTextField textCounter;

	private StoredFluigDataProfileVO fluigDataProfile;

	public MDMConsumer(){
		super(2, 28, " Fluig Data: Consumer");

		this.labelProduct = new JLabel("Product: ");
		this.comboProduct = new JComboBox<String>(new String[]{"Protheus"});
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
		this.textNestedInstnace = new JComboBox<String>();

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
		this.add(this.textNestedInstnace);
		this.add(this.labelCounter, 2, true, 1, 2);
		this.add(this.textCounter);
		this.add(new JLabel());
		this.add(this.buttonAddNestedInstance);
		this.add(this.buttonImportData);

		this.initColumnSizes(this.tableEntitiesFD);

		this.comboNestedFields.addItemListener(new ComboNestedField());
		this.buttonAddNestedInstance.addActionListener(new AddNestedField());
		this.buttonImportData.addActionListener(new ActionImportDataButton());
		this.comboProduct.addItemListener(new ProductSelectClick());

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
				for(int i=0; i<comboBoxNestedFieldModel.getSize(); i++) {
					for(int j=0; j<textNestedInstnace.getItemCount(); j++) {
						comboNestedFields.setSelectedIndex(i);
						textNestedInstnace.setSelectedIndex(j);
						buttonAddNestedInstance.doClick();
					}
				}
			}
		});
	}

	class ActionImportDataButton implements ActionListener, Runnable {
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
			
			MDMRestAuthentication.getInstance(fluigDataProfile.getServerURL(), fluigDataProfile.getDomain(), fluigDataProfile.getConsumerID(), fluigDataProfile.getUsername(), fluigDataProfile.getPassword(), true);
			MDMRestConnectionFactory.getConnection(fluigDataProfile.getServerURL());

			Integer numOfPendingRecords = 0;

			try {
				do {
					CommandDataConsumption commandDataConsumption = new CommandDataConsumption(entity.getEntityId(), Integer.parseInt(textCounter.getText()), 10);
					EnvelopeVO executeCommand = MDMRestConnectionFactory.getConnection(fluigDataProfile.getServerURL()).executeCommand(commandDataConsumption);

					DataConsumptionVO dataConsumptionVO = (DataConsumptionVO) executeCommand.getHits().get(0);
					List<GoldenRecordVO> goldenRecords = dataConsumptionVO.getGoldenRecords();

					Integer lastCounterForEntity = dataConsumptionVO.getLastCounterForEntity();
					numOfPendingRecords = dataConsumptionVO.getNumOfPendingRecords();

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
				MDMRestAuthentication.getInstance(fluigDataProfile.getServerURL(), fluigDataProfile.getDomain(), fluigDataProfile.getDatasourceID(), fluigDataProfile.getUsername(), fluigDataProfile.getPassword(), true);
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
								
								if(fdFieldVO.getName().equals("_mdmcountry") && value != null && value.length() > 3) { //TODO: fixme
									value = value.substring(0, 3);
								}
								if(fdFieldVO.getName().equals("_mdmcity") && value != null && value.length() > 15) { //TODO: fixme
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
							if (fdFieldVO.getName().equals("_mdmentitytype") && value != null) {// TODO: Fixme
								value = value.trim().toUpperCase();
							}
							if (fdFieldVO.getName().equals("_mdmdba") && (value == null || value.trim().length() == 0)) {// TODO: Fixme
								value = (String) data.get("_mdmname");
								if(value.length() > 20) {
									value = value.substring(0, 20);
								}
							}
//							if(fdFieldVO.getName().equals("_mdmdba") && value != null) {//TODO: Fixme I applied this rule on MDM
//								value = value.trim();
//
//								if(value.length() > 20) {
//									value = value.substring(0, 20);
//								}
//							}
//							if(fdFieldVO.getName().equals("_mdmphonenumber") && value != null) {//TODO: Fixme I applied this rule on MDM
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
				if(data.get("_mdmaddress1") == null || ((String) data.get("_mdmaddress1")).trim().length() == 0) {
					this.workArround("A1_END", map, mappedFields, data);
				}
				if(data.get("_mdmstate") == null || ((String) data.get("_mdmstate")).trim().length() == 0) {
					this.workArround("A1_EST", map, mappedFields, data);
				}
				if(data.get("_mdmcity") == null || ((String) data.get("_mdmcity")).trim().length() == 0) {
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
			textNestedInstnace.removeAllItems();
			textNestedInstnace.updateUI();

			FDNestedFieldVO selectedItem = (FDNestedFieldVO) comboNestedFields.getSelectedItem();

			if(selectedItem != null) {
				List<String> list = MappingUtil.NESTED_INSTANCES.get(selectedItem.getInternalName());
				
				if(list != null) {
					for (String string : list) {
						textNestedInstnace.addItem(new String(string));
					}
					textNestedInstnace.updateUI();
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
				String selectedInstance = new String((String) textNestedInstnace.getSelectedItem());

				if(fieldsVO != null) {
					for (FDFieldVO fdFieldVO : fieldsVO) {
						try {
							FDFieldVO fdFieldNew = (FDFieldVO) fdFieldVO.clone();
							fdFieldNew.setInstance(selectedInstance);
							fdFieldNew.setProtheusField(MappingUtil.mappings.get(MappingUtil.TYPE_CUSTOMER).get(MappingUtil.PROD_PROTHEUS).get(fdFieldNew.getInstance()).get(fdFieldNew.getName()));
							fdFieldNew.setFatherMDMName(fdNestedField.getInternalName());
	
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
		@Override
		public void itemStateChanged(ItemEvent e) {
		}
	}

	class FDEntityVO {
		private String name;
		private String description;
		private String wsURL;
		private DataConsumerDetailsVO fieldsDetail;
		private String entityId;

		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getWsURL() {
			return wsURL;
		}
		public void setWsURL(String wsURL) {
			this.wsURL = wsURL;
		}
		public DataConsumerDetailsVO getFieldsDetail() {
			return fieldsDetail;
		}
		public void setFieldsDetail(DataConsumerDetailsVO fieldsDetail) {
			this.fieldsDetail = fieldsDetail;
		}
		public String getEntityId() {
			return this.entityId;
		}
		public void setEntityId(String string) {
			this.entityId = string;
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

	class FDFieldVO implements Cloneable {
		private String name;
		private String description;
		private String type;
		private String protheusField;
		private String instance;
		private Boolean children;
		private String fatherMDMName;

	    protected Object clone() throws CloneNotSupportedException {
	        return super.clone();
	    }
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getProtheusField() {
			return protheusField;
		}
		public String getFatherMDMName() {
			return fatherMDMName;
		}
		public void setFatherMDMName(String fatherMDMName) {
			this.fatherMDMName = fatherMDMName;
		}
		public void setProtheusField(String protheusField) {
			this.protheusField = protheusField;
		}
		public String getInstance() {
			return instance;
		}
		public void setInstance(String instance) {
			this.instance = instance;
		}
		public Boolean getChildren() {
			return children;
		}
		public void setChildren(Boolean children) {
			this.children = children;
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
}
