package com.totvslabs.mdm.client.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.text.DateFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.totvslabs.mdm.client.pojo.StoredAbstractVO;
import com.totvslabs.mdm.client.pojo.StoredConfigurationVO;
import com.totvslabs.mdm.client.ui.events.StoredConfigurationChangedDispatcher;
import com.totvslabs.mdm.client.ui.events.StoredConfigurationChangedEvent;
import com.totvslabs.mdm.client.ui.events.StoredConfigurationChangedListener;
import com.totvslabs.mdm.client.ui.events.StoredConfigurationSelectedDispatcher;
import com.totvslabs.mdm.client.ui.events.StoredConfigurationSelectedEvent;
import com.totvslabs.mdm.client.util.PersistenceEngine;

public class StoredConfigurationPanel extends JPanel implements StoredConfigurationChangedListener {
	private static final long serialVersionUID = 1L;

	private JTable tableFieldsJDBC;
	private StoredConfigurationTableModel tableModel;
	private JScrollPane scrollBarEntitiesMDM;

	public StoredConfigurationPanel() {
		this.setBorder( BorderFactory.createTitledBorder(" Saved Configuration") );

		this.tableModel = new StoredConfigurationTableModel();
		this.tableFieldsJDBC = new JTable(this.tableModel);
		this.tableFieldsJDBC.setFillsViewportHeight(true);

		this.scrollBarEntitiesMDM = new JScrollPane(this.tableFieldsJDBC);

		StoredConfigurationChangedDispatcher.getInstance().addStoredConfigurationChangedListener(this);

		this.initializeLayout();
	}

	public void initializeLayout() {
		this.setLayout(new BorderLayout());
		scrollBarEntitiesMDM.setPreferredSize(new Dimension(100, 140));

		this.add(this.scrollBarEntitiesMDM);

		this.initColumnSizes(this.tableFieldsJDBC);

		tableFieldsJDBC.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.tableFieldsJDBC.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				StoredConfigurationVO object = tableModel.getRowObject(tableFieldsJDBC.getSelectedRow());

				StoredConfigurationSelectedEvent event = new StoredConfigurationSelectedEvent(object);
				StoredConfigurationSelectedDispatcher.getInstance().fireStoredConfigurationSelectedEvent(event);
			}
		});
	}

    private void initColumnSizes(JTable table) {
    	StoredConfigurationTableModel model = (StoredConfigurationTableModel) table.getModel();
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

	class StoredConfigurationTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		private String[] columnNames = {"Fluig Data", "Datasource Type", "Datasource Name", "Source detail", "Last Execution", "Quantity Affected"};
		private List<StoredAbstractVO> data;

		public StoredConfigurationTableModel() {
			data = PersistenceEngine.getInstance().findAll(StoredConfigurationVO.class);
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public Class getColumnClass(int c) {
			Object valueAt = getValueAt(0, c);

			if(valueAt != null) {
				return valueAt.getClass();
			}

			return String.class;
		}

		public void setData(List<StoredAbstractVO> data) {
			this.data = data;
		}

		public void addRows(List<StoredConfigurationVO> vos) {
			this.data.clear();
			this.data.addAll(vos);
		}

		public void addRow(StoredConfigurationVO field) {
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

		public StoredConfigurationVO getRowObject(int row) {
			return (StoredConfigurationVO) data.get(row);
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

			StoredConfigurationVO record = (StoredConfigurationVO) this.data.get(rowIndex);
			DateFormat df = DateFormat.getDateTimeInstance();

			switch(columnIndex) {
				case 0:
					return record.getFluigDataName();
				case 1:
					return record.getTypeEnum();
				case 2:
					return record.getDatasourceID();
				case 3:
					return record.getSourceName();
				case 4:
					return record.getLastExecution() != null ? df.format(record.getLastExecution()) : null;
				case 5:
					return record.getQuantity();
			}

			return null;
		}
	}

	@Override
	public void onStoredConfigurationChanged(StoredConfigurationChangedEvent event) {
		this.tableModel.setData(PersistenceEngine.getInstance().findAll(StoredConfigurationVO.class));
		this.tableFieldsJDBC.updateUI();
	}
}
