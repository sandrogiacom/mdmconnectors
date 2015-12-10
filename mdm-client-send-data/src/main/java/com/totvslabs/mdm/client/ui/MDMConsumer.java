package com.totvslabs.mdm.client.ui;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.totvslabs.mdm.client.pojo.StoredAbstractVO;
import com.totvslabs.mdm.client.pojo.StoredFluigDataProfileVO;
import com.totvslabs.mdm.client.ui.events.MDMConnectionChangedDispatcher;
import com.totvslabs.mdm.client.ui.events.MDMConnectionChangedEvent;
import com.totvslabs.mdm.client.ui.events.MDMConnectionChangedListener;
import com.totvslabs.mdm.restclient.MDMRestConnectionFactory;
import com.totvslabs.mdm.restclient.command.CommandGetDataConsumers;
import com.totvslabs.mdm.restclient.command.CommandGetDataModel;
import com.totvslabs.mdm.restclient.vo.DataConsumerVO;
import com.totvslabs.mdm.restclient.vo.DataModelVO;
import com.totvslabs.mdm.restclient.vo.EnvelopeVO;
import com.totvslabs.mdm.restclient.vo.GenericVO;

public class MDMConsumer extends PanelAbstract implements MDMConnectionChangedListener {
	private static final long serialVersionUID = 1L;

	private JLabel labelProduct;
	private JComboBox<String> comboProduct;

	private JLabel labelEntity;
	private JTextField textEntity;

	private JLabel labelEntitiesFD;
	private JTable tableEntitiesFD;
	private FDEntityTableModel tableModelEntitiesFD;
	private JScrollPane scrollBarEntitiesMDM;

	private JLabel labelCounter;
	private JTextField textCounter;

	private JLabel labelDataConsumptionFD;
	private JTable tableDataConsumptionFD;
	private FDDataConsumptionTableModel tableModelDataConsumptionFD;
	private JScrollPane scrollBarDataConsumptionFD;

	private StoredFluigDataProfileVO fluigDataProfile;

	public MDMConsumer(){
		super(2, 21, " Fluig Data: Consumer");

		this.labelProduct = new JLabel("Product: ");
		this.comboProduct = new JComboBox<String>(new String[]{"Protheus"});
		this.labelEntity = new JLabel("Entity: ");
		this.textEntity = new JTextField("");
		this.labelEntitiesFD = new JLabel("Entities: ");
		this.tableModelEntitiesFD = new FDEntityTableModel();
		this.tableEntitiesFD = new JTable(this.tableModelEntitiesFD);
		this.tableEntitiesFD.setFillsViewportHeight(true);
		this.labelDataConsumptionFD = new JLabel("Consumption Queue: ");
		this.tableModelDataConsumptionFD = new FDDataConsumptionTableModel();
		this.tableDataConsumptionFD = new JTable(this.tableModelDataConsumptionFD);
		this.tableDataConsumptionFD.setFillsViewportHeight(true);

		this.labelCounter = new JLabel("Counter: ");
		this.textCounter = new JTextField("");

		this.scrollBarEntitiesMDM = new JScrollPane(this.tableEntitiesFD);
		this.scrollBarDataConsumptionFD = new JScrollPane(this.tableDataConsumptionFD);

		MDMConnectionChangedDispatcher.getInstance().addMDMConnectionChangedListener(this);

		this.initializeLayout();
	}

	public void initializeLayout() {
		this.add(this.labelProduct);
		this.add(this.comboProduct);
		this.add(this.labelEntity);
		this.add(this.textEntity);
		this.add(this.labelEntitiesFD);
		this.add(this.scrollBarEntitiesMDM, 2, true, 7, 2);
		this.add(this.labelCounter);
		this.add(this.textCounter);
		this.add(this.labelDataConsumptionFD);
		this.add(this.scrollBarDataConsumptionFD, 2, true, 7, 2);

		this.initColumnSizes(this.tableEntitiesFD);

		this.comboProduct.addItemListener(new ProductSelectClick());
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
			if(this.data == null || this.data.size() == 0) {
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
			if(this.data == null || this.data.size() == 0) {
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

	@Override
	public StoredAbstractVO getAllData() {
		return null;
	}

	@Override
	public void loadAllData(StoredAbstractVO intance) {
		// TODO Auto-generated method stub
	}

	@Override
	public void loadDefaultData() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onMDMConnectionChangedListener(MDMConnectionChangedEvent event) {
		this.fluigDataProfile = event.getActualConnection();

		CommandGetDataConsumers consumer = new CommandGetDataConsumers(this.fluigDataProfile.getConsumerID());
		EnvelopeVO executeCommand = MDMRestConnectionFactory.getConnection(fluigDataProfile.getServerURL()).executeCommand(consumer);

		List<GenericVO> hits = executeCommand.getHits();

		if(hits != null && hits.size() > 0) {
			if(hits.get(0) instanceof DataConsumerVO) {
				List<String> entities = ((DataConsumerVO) hits.get(0)).get_mdmEntitiesConsumed();

				if(entities != null) {
					tableModelEntitiesFD.clear();
					
					for (String string : entities) {
						CommandGetDataModel dataModel = new CommandGetDataModel(string);
						EnvelopeVO executeCommandGetDataModel = MDMRestConnectionFactory.getConnection(fluigDataProfile.getServerURL()).executeCommand(dataModel);

						System.out.println(executeCommandGetDataModel);
						String next = ((DataModelVO) executeCommandGetDataModel.getHits().get(0)).get_mdmLabel().keySet().iterator().next();
						
						FDEntityVO entity = new FDEntityVO();
						entity.setDescription(((DataModelVO) executeCommandGetDataModel.getHits().get(0)).get_mdmLabel().get(next));
						entity.setName(((DataModelVO) executeCommandGetDataModel.getHits().get(0)).get_mdmName());

						tableModelEntitiesFD.addRow(entity);
					}
				}
			}
		}
	}
}
