package com.totvslabs.mdm.client.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.totvslabs.mdm.client.pojo.FDEntityVO;

public class MDMConsumerFDEntityTableModel extends AbstractTableModel {
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
