package com.totvslabs.mdm.client.ui;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.totvslabs.mdm.client.pojo.JDBCFieldVO;
import com.totvslabs.mdm.client.pojo.JDBCTableVO;
import com.totvslabs.mdm.client.pojoTSA.MasterConfigurationData;
import com.totvslabs.mdm.client.ui.events.JDBCConnectionStabilizedDispatcher;
import com.totvslabs.mdm.client.ui.events.JDBCConnectionStabilizedEvent;
import com.totvslabs.mdm.client.ui.events.JDBCConnectionStabilizedListener;
import com.totvslabs.mdm.client.ui.events.JDBCTableSelectedDispatcher;
import com.totvslabs.mdm.client.ui.events.JDBCTableSelectedEvent;
import com.totvslabs.mdm.client.util.JDBCConnectionFactory;

public class JDBCEntities extends PanelAbstract implements JDBCConnectionStabilizedListener {
	private static final long serialVersionUID = 1L;

	private JLabel labelTable;
	private JComboBox<JDBCTableVO> comboTable;

	private JTable tableFieldsJDBC;
	private JDBCFieldsTableModel tableModel;
	private JScrollPane scrollBarEntitiesMDM;

	private JLabel labelTemplateName;
	private JTextField textTemplateName;

	private JLabel labelBatchSize;
	private JTextField textBatchSize;

	private JLabel labelCompressOption;
	private JCheckBox checkBoxCompress;

	private JDBCConnectionStabilizedEvent jdbcConnectionStabilizedEvent;

	public JDBCEntities(){
		super(2, 17, " MDM Entities");

		this.labelTable = new JLabel("Table: ");
		this.comboTable = new JComboBox<JDBCTableVO>();

		this.tableModel = new JDBCFieldsTableModel();
		this.tableFieldsJDBC = new JTable(this.tableModel);
		this.tableFieldsJDBC.setFillsViewportHeight(true);

		this.scrollBarEntitiesMDM = new JScrollPane(this.tableFieldsJDBC);

		this.labelTemplateName = new JLabel("Type: ");
		this.textTemplateName = new JTextField(20);

		this.labelCompressOption = new JLabel("Compress: ");
		this.checkBoxCompress = new JCheckBox("Yes!", true);

		this.labelBatchSize = new JLabel("Batch Size (records): ");
		this.textBatchSize = new JTextField("500", 20);

		this.initializeLayout();
	}

	public void initializeLayout() {
		this.add(this.labelTable);
		this.add(this.comboTable);

		this.add(this.labelTemplateName);
		this.add(this.textTemplateName);

		this.add(this.labelBatchSize);
		this.add(this.textBatchSize);

		this.add(this.labelCompressOption);
		this.add(this.checkBoxCompress);

		this.add(new JLabel());
		this.add(this.scrollBarEntitiesMDM, 2, true, 8, 2);

		this.initColumnSizes(this.tableFieldsJDBC);

		this.comboTable.addItemListener(new JDBCTableSelectClick());

		JDBCConnectionStabilizedDispatcher.getInstance().addJDBCConnectionStabilizedListener(this);
	}

	@Override
	public void fillComponents(MasterConfigurationData masterConfigurationData) {
		if(masterConfigurationData != null) {
		}
	}

	@Override
	public void fillData(MasterConfigurationData masterConfigurationData) {
		if(masterConfigurationData != null) {
		}
	}

    private void initColumnSizes(JTable table) {
        JDBCFieldsTableModel model = (JDBCFieldsTableModel) table.getModel();
        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;
        Object[] longValues = model.getRow(0);
        TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
 
        for (int i = 0; i < 4; i++) {
            column = table.getColumnModel().getColumn(i);
 
            comp = headerRenderer.getTableCellRendererComponent(null, column.getHeaderValue(), false, false, 0, 0);
            headerWidth = comp.getPreferredSize().width;
            comp = table.getDefaultRenderer(model.getColumnClass(i)).getTableCellRendererComponent(table, longValues[i], false, false, 0, i);
            cellWidth = comp.getPreferredSize().width;
 
            column.setPreferredWidth(Math.max(headerWidth, cellWidth));
        }
    }

	class JDBCTableSelectClick implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent e) {
			JDBCTableVO vo = (JDBCTableVO) e.getItem();

			textTemplateName.setText(vo.getName());

			JDBCConnectionStabilizedEvent event = jdbcConnectionStabilizedEvent;

			JDBCConnectionFactory.loadFisicModelFields(event.getParam().getUrl(), event.getParam().getUser(), event.getParam().getPassword(), vo);
			List<JDBCFieldVO> fields = vo.getFields();

			tableModel.addRows(fields);
			tableFieldsJDBC.updateUI();

			JDBCTableSelectedEvent eventTableSelected = new JDBCTableSelectedEvent(vo, event.getParam());
			JDBCTableSelectedDispatcher.getInstance().fireJDBCTableSelectedEvent(eventTableSelected);

			Integer totalRecords = JDBCConnectionFactory.getTotalRecords(event.getParam(), vo);
			vo.setTotalRecords(totalRecords);
		}
	}

	class JDBCFieldsTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		private String[] columnNames = {"Field", "Identifier", "Type", "Size"};
		private List<JDBCFieldVO> data = new ArrayList<JDBCFieldVO>();

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public Class getColumnClass(int c) {
			Object valueAt = getValueAt(0, c);

			if(valueAt != null) {
				return valueAt.getClass();
			}

			return String.class;
		}

		public void addRows(List<JDBCFieldVO> vos) {
			this.data.clear();
			this.data.addAll(vos);
		}

		public void addRow(JDBCFieldVO field) {
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

			JDBCFieldVO record = this.data.get(rowIndex);

			switch(columnIndex) {
				case 0:
					return record.getName();
				case 1:
					return record.getIdentifier();
				case 2:
					return record.getType();
				case 3:
					return record.getSize();
			}

			return null;
		}
	}

	@Override
	public void onJDBCConnectionStabilizedEvent(JDBCConnectionStabilizedEvent event) {
		this.comboTable.removeAllItems();
		this.textTemplateName.setText("");
		this.textBatchSize.setText("500");

		this.jdbcConnectionStabilizedEvent = event;

		this.comboTable.removeAllItems();

		for (JDBCTableVO tables : event.getTables()) {
			this.comboTable.addItem(tables);
		}
	}

	public JTextField getTextTemplateName() {
		return textTemplateName;
	}

	public JTextField getTextBatchSize() {
		return textBatchSize;
	}

	public JCheckBox getCheckBoxCompress() {
		return checkBoxCompress;
	}
}
