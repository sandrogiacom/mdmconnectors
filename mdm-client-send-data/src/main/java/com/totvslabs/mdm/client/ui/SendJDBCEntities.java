package com.totvslabs.mdm.client.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.totvslabs.mdm.client.pojo.JDBCFieldVO;
import com.totvslabs.mdm.client.pojo.JDBCTableVO;
import com.totvslabs.mdm.client.pojo.StoredAbstractVO;
import com.totvslabs.mdm.client.pojo.StoredJDBCConnectionVO;
import com.totvslabs.mdm.client.ui.events.JDBCConnectionStabilizedDispatcher;
import com.totvslabs.mdm.client.ui.events.JDBCConnectionStabilizedEvent;
import com.totvslabs.mdm.client.ui.events.JDBCConnectionStabilizedListener;
import com.totvslabs.mdm.client.ui.events.JDBCTableSelectedDispatcher;
import com.totvslabs.mdm.client.ui.events.JDBCTableSelectedEvent;
import com.totvslabs.mdm.client.ui.events.JDBCTableSelectedListener;
import com.totvslabs.mdm.client.ui.events.SendDataFluigDataDoneDispatcher;
import com.totvslabs.mdm.client.ui.events.SendDataFluigDataDoneEvent;
import com.totvslabs.mdm.client.ui.events.SendDataFluigDataDoneListener;
import com.totvslabs.mdm.client.util.JDBCConnectionFactory;
import com.totvslabs.mdm.client.util.ProcessTypeEnum;
import com.totvslabs.mdm.client.util.ThreadExportData;

public class SendJDBCEntities extends PanelAbstract implements JDBCConnectionStabilizedListener {
	private static final long serialVersionUID = 1L;

	private JLabel labelTable;
	private JComboBox<JDBCTableVO> comboTable;

	private JLabel labelFields;
	private JTable tableFieldsJDBC;
	private JDBCFieldsTableModel tableModel;
	private JScrollPane scrollBarEntitiesMDM;

	private JLabel labelTemplateName;
	private JTextField textTemplateName;

	private JLabel labelBatchSize;
	private JTextField textBatchSize;

	private JLabel labelCompressOption;
	private JCheckBox checkBoxCompress;

	private JLabel labelIgnoreLocalCache;
	private JCheckBox checkBoxIgnoreLocalCache;

	private JDBCConnectionStabilizedEvent jdbcConnectionStabilizedEvent;

	private JButton buttonGenerateJsonFile;

	public SendJDBCEntities(){
		super(2, 12, " JDBC Entities");

		this.labelTable = new JLabel("Table: ");
		this.comboTable = new JComboBox<JDBCTableVO>();

		this.labelFields = new JLabel("Fields: ");
		this.tableModel = new JDBCFieldsTableModel();
		this.tableFieldsJDBC = new JTable(this.tableModel);
		this.tableFieldsJDBC.setFillsViewportHeight(true);

		this.scrollBarEntitiesMDM = new JScrollPane(this.tableFieldsJDBC);

		this.labelTemplateName = new JLabel("Type: ");
		this.textTemplateName = new JTextField(20);

		this.labelCompressOption = new JLabel("Compress: ");
		this.checkBoxCompress = new JCheckBox("Yes!", true);

		this.labelIgnoreLocalCache = new JLabel("Ignore Local Cache: ");
		this.checkBoxIgnoreLocalCache = new JCheckBox("Yes!", true);

		this.labelBatchSize = new JLabel("Batch Size (records): ");
		this.textBatchSize = new JTextField("500", 20);

		this.buttonGenerateJsonFile = new JButton("Export Entity as Json File");
		this.buttonGenerateJsonFile.setEnabled(false);

		this.initializeLayout();
	}

	public void initializeLayout() {
		this.add(this.labelTable);
		this.add(this.comboTable);

//		this.add(this.labelTemplateName);
//		this.add(this.textTemplateName);
//
//		this.add(this.labelBatchSize);
//		this.add(this.textBatchSize);
//
//		this.add(this.labelCompressOption);
//		this.add(this.checkBoxCompress);

		this.add(this.labelIgnoreLocalCache);
		this.add(this.checkBoxIgnoreLocalCache);

		this.add(this.labelFields);
		this.add(this.scrollBarEntitiesMDM, 2, true, 7, 2);

//		this.add(new JLabel());
//		this.add(this.buttonGenerateJsonFile);

		this.initColumnSizes(this.tableFieldsJDBC);

		this.comboTable.addItemListener(new JDBCTableSelectClick());
		this.buttonGenerateJsonFile.addActionListener(new GenerateJSonFileClick(this));

		JDBCConnectionStabilizedDispatcher.getInstance().addJDBCConnectionStabilizedListener(this);
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

	class GenerateJSonFileClick implements ActionListener, JDBCTableSelectedListener, JDBCConnectionStabilizedListener, SendDataFluigDataDoneListener {
		private StoredJDBCConnectionVO jdbcConnectionVO;
		private JDBCTableVO tableVO;
		private SendJDBCEntities panelJDBCEntities;
		private File file;

		public GenerateJSonFileClick(SendJDBCEntities panelJDBCEntities) {
			this.panelJDBCEntities = panelJDBCEntities;
			JDBCTableSelectedDispatcher.getInstance().addJDBCTableSelectedListener(this);
			JDBCConnectionStabilizedDispatcher.getInstance().addJDBCConnectionStabilizedListener(this);
			SendDataFluigDataDoneDispatcher.getInstance().addSendDataFluigDataDoneListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			buttonGenerateJsonFile.setEnabled(false);
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setSelectedFile(new File(textTemplateName.getText() + ".json"));
			fileChooser.setFileFilter(new FileNameExtensionFilter("json", "json"));

			int returnValue = fileChooser.showSaveDialog(null);

			if(returnValue == JFileChooser.APPROVE_OPTION) {
	            File file = fileChooser.getSelectedFile();

	            if(!file.exists()) {
	            	try {
						file.createNewFile();
					} catch (IOException e1) {
					}
	            }

	            this.file = file;

	            Thread threadExportData = new Thread(new ThreadExportData(this.tableVO, this.jdbcConnectionVO, this.panelJDBCEntities));
	            threadExportData.start();
			}
			else {
				buttonGenerateJsonFile.setEnabled(true);	
			}
		}

		@Override
		public void onDataLoadedEvent(JDBCConnectionStabilizedEvent event) {
			this.jdbcConnectionVO = event.getJdbcConnectionVO();
		}

		@Override
		public void onJDBCTableSelectedEvent(JDBCTableSelectedEvent event) {
			this.jdbcConnectionVO = event.getJdbcConnectionVO();
			this.tableVO = event.getTableVO();
		}

		@Override
		public void onSendDataFluigDataDone(SendDataFluigDataDoneEvent event) {
			try {
				String jsonData = event.getJsonData();
				
				if(event.getProcessTypeEnum().equals(ProcessTypeEnum.EXPORT_DATA)) {
					PrintWriter pw = new PrintWriter(file);
					pw.write(jsonData);
					pw.close();
					JOptionPane.showMessageDialog(null, "The json file was generated successfully at: " + file.getAbsolutePath());
				}

				buttonGenerateJsonFile.setEnabled(true);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	class JDBCTableSelectClick implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent e) {
			JDBCTableVO vo = (JDBCTableVO) e.getItem();

			textTemplateName.setText(vo.getName());

			JDBCConnectionStabilizedEvent event = jdbcConnectionStabilizedEvent;

			JDBCConnectionFactory.loadFisicModelFields(event.getJdbcConnectionVO().getUrl(), event.getJdbcConnectionVO().getUsername(), event.getJdbcConnectionVO().getPassword(), vo);
			List<JDBCFieldVO> fields = vo.getFields();

			tableModel.addRows(fields);
			tableFieldsJDBC.updateUI();

			JDBCTableSelectedEvent eventTableSelected = new JDBCTableSelectedEvent(vo, event.getJdbcConnectionVO());
			JDBCTableSelectedDispatcher.getInstance().fireJDBCTableSelectedEvent(eventTableSelected);

			Integer totalRecords = JDBCConnectionFactory.getTotalRecords(event.getJdbcConnectionVO(), vo);
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
	public void onDataLoadedEvent(JDBCConnectionStabilizedEvent event) {
		if(event.getTables() != null) {
			this.comboTable.removeAllItems();
			this.textTemplateName.setText("");
			this.textBatchSize.setText("500");
			
			this.jdbcConnectionStabilizedEvent = event;
			
			this.comboTable.removeAllItems();
			
			for (JDBCTableVO tables : event.getTables()) {
				this.comboTable.addItem(tables);
			}
			
			this.buttonGenerateJsonFile.setEnabled(true);
		}
	}

	public JTextField getTextTemplateName() {
		return textTemplateName;
	}

	public JTextField getTextBatchSize() {
		return textBatchSize;
	}

	public Boolean getCheckBoxCompress() {
		return checkBoxCompress.isSelected();
	}

	public Boolean getIgnoreLocalCache() {
		return this.checkBoxIgnoreLocalCache.isSelected();
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
}
