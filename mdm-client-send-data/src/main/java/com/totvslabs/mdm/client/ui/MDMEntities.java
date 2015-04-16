package com.totvslabs.mdm.client.ui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.totvslabs.mdm.client.pojo.MDMEntityBO;
import com.totvslabs.mdm.client.pojo.MDMEntitySyncTypeVO;
import com.totvslabs.mdm.client.pojoTSA.MasterConfigurationData;

public class MDMEntities extends PanelAbstract {
	private static final long serialVersionUID = 1L;

	private JTable tableEntitiesMDM;
	private MDMEntitiesTableModel tableModel;
	private JScrollPane scrollBarEntitiesMDM;

	private JLabel labelSyncType;
	private JComboBox<MDMEntitySyncTypeVO> comboSyncType;

	public MDMEntities(){
		super(2, 13, " MDM Entities");

		this.tableModel = new MDMEntitiesTableModel();
		this.tableEntitiesMDM = new JTable(this.tableModel);
		this.tableEntitiesMDM.setFillsViewportHeight(true);

		this.scrollBarEntitiesMDM = new JScrollPane(this.tableEntitiesMDM);

		this.labelSyncType = new JLabel("Sync Type: ");
		this.comboSyncType = new JComboBox<MDMEntitySyncTypeVO>();

		this.comboSyncType.addItem(new MDMEntitySyncTypeVO(1l,"Database"));

		this.initializeLayout();
	}

	public void initializeLayout() {
		this.add(this.scrollBarEntitiesMDM, 2, true, 8, 2);

		this.add(this.labelSyncType);
		this.add(this.comboSyncType);

		this.initColumnSizes(tableEntitiesMDM);
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
        MDMEntitiesTableModel model = (MDMEntitiesTableModel) table.getModel();
        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;
        Object[] longValues = model.getRow(0);
        TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
 
        for (int i = 0; i < 5; i++) {
            column = table.getColumnModel().getColumn(i);
 
            comp = headerRenderer.getTableCellRendererComponent(null, column.getHeaderValue(), false, false, 0, 0);
            headerWidth = comp.getPreferredSize().width;
            comp = table.getDefaultRenderer(model.getColumnClass(i)).getTableCellRendererComponent(table, longValues[i], false, false, 0, i);
            cellWidth = comp.getPreferredSize().width;
 
            column.setPreferredWidth(Math.max(headerWidth, cellWidth));
        }
    }

	class MDMEntitiesTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		private String[] columnNames = {"Name", "Records", "Datasources", "Inconsistent Data", "Has Sync Conf?"};
		private List<MDMEntityBO> data = new ArrayList<MDMEntityBO>();

		public MDMEntitiesTableModel() {
			MDMEntityBO bo = new MDMEntityBO();

			bo.setDescription("Desc");
			bo.setMdmTenantId(23432424l);
			bo.setMdmDatasourceId(234324l);
			bo.setName("sdfsfsf");

			this.data.add(bo);
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
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
			MDMEntityBO record = this.data.get(rowIndex);

			switch(columnIndex) {
				case 0:
					return record.getName();
				case 1:
					return 0;
				case 2:
					return record.getMdmDatasourceId();
				case 3:
					return new Integer (0);
				case 4:
					return new Boolean(false);
			}

			return null;
		}
	}
}
