package com.totvslabs.mdm.client.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.totvslabs.mdm.client.pojo.MDMData;
import com.totvslabs.mdm.client.pojo.StoredAbstractVO;
import com.totvslabs.mdm.client.ui.events.JDBCConnectionStabilizedDispatcher;
import com.totvslabs.mdm.client.ui.events.JDBCConnectionStabilizedEvent;

public class SendFileFluigData extends PanelAbstract {
	private static final long serialVersionUID = 1L;

	private JLabel labelSelectedFile;
	private JLabel labelSelectedFileValue;
	private JLabel labelQuantityRecords;
	private JLabel labelQuantityRecordsValue;

	private JLabel labelTemplateName;
	private JTextField textTemplateName;

	private JLabel labelCompressOption;
	private JCheckBox checkBoxCompress;

	private JButton buttonSendTxtData;
	private List<MDMData> data;

	public SendFileFluigData(){
		super(1, 16, " Send Data: File");

		this.labelSelectedFile = new JLabel("File: ");
		this.labelSelectedFileValue = new JLabel();
		this.labelQuantityRecords = new JLabel("Quantity records: ");
		this.labelQuantityRecordsValue = new JLabel();

		this.labelTemplateName = new JLabel("Type: ");
		this.textTemplateName = new JTextField(20);

		this.labelCompressOption = new JLabel("Compress: ");
		this.checkBoxCompress = new JCheckBox("Yes!", true);

		this.buttonSendTxtData = new JButton("Load File");

		this.initializeLayout();
	}

	public void initializeLayout() {
		this.add(new JLabel());
		this.add(this.buttonSendTxtData);
		this.add(new JLabel());

		this.add(labelSelectedFile);		
		this.add(labelSelectedFileValue);
		this.add(labelQuantityRecords);
		this.add(labelQuantityRecordsValue);
		this.add(new JLabel());

		this.add(this.labelTemplateName);
		this.add(this.textTemplateName);

		this.add(this.labelCompressOption);
		this.add(this.checkBoxCompress);

		this.textTemplateName.addKeyListener(new KeyListenerType());
		
		this.buttonSendTxtData.addActionListener(new SendJsonFile());
	}

	class KeyListenerType implements KeyListener {
		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyPressed(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if(data != null && data.size() == 1) {
				data.get(0).setTemplateName(((JTextField) e.getSource()).getText());
			}
		}
	}

	class SendJsonFile implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File("C:\\"));
			FileNameExtensionFilter filter1 = new FileNameExtensionFilter("JSon Files", "json", "json");
			FileNameExtensionFilter filter2 = new FileNameExtensionFilter("XSL Files", "xsl", "xsl");
			fileChooser.addChoosableFileFilter(filter1);
			fileChooser.addChoosableFileFilter(filter2);

			int result = fileChooser.showOpenDialog(null);

			if (result == JFileChooser.APPROVE_OPTION) {
				String filename = fileChooser.getSelectedFile().getPath();

				labelSelectedFileValue.setText(filename);
				textTemplateName.setText(fileChooser.getSelectedFile().getName().substring(0, fileChooser.getSelectedFile().getName().indexOf('.')));

				if(filename.contains(".json")) {
					this.loadDataJsonFile(fileChooser.getSelectedFile().getName().substring(0, fileChooser.getSelectedFile().getName().indexOf('.')), filename);
					labelTemplateName.setEnabled(true);
					textTemplateName.setEnabled(true);
				}
				else {
					this.loadDataExcelFile(filename);
					labelTemplateName.setEnabled(false);
					textTemplateName.setEnabled(false);
				}

				JDBCConnectionStabilizedEvent event = new JDBCConnectionStabilizedEvent();
				JDBCConnectionStabilizedDispatcher.getInstance().fireJDBCConnectionStabilizedEvent(event);
			}
		}

		private List<MDMData> loadExcelFile(String fileURL) {
			try {
				List<MDMData> dataList = new ArrayList<MDMData>();

				InputStream inputStream = new FileInputStream(fileURL);
				@SuppressWarnings("resource")
				XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
				Iterator<Sheet> iterator = workbook.iterator();

				while(iterator.hasNext()) {
					Sheet sheet = iterator.next();

					JsonArray arrayData = new JsonArray();
					Map<Integer, String> columns = new HashMap<Integer, String>();

					int numCol = 0;
					Row rowIndex = sheet.getRow(0);
					Cell cellIndex = rowIndex.getCell(numCol);

					while (cellIndex != null) {
						cellIndex = rowIndex.getCell(numCol);

						if(cellIndex != null) {
							columns.put(numCol, cellIndex.toString());
						}

						numCol++;
					}

					Integer row = 1;
					while(true) {
						Row rowData = sheet.getRow(row);

						if(rowData == null) {
							break;
						}

						JsonObject object = new JsonObject();

						for(int i=0; i<numCol; i++) {
							if(rowData.getCell(i) != null) {
								object.addProperty(columns.get(i), rowData.getCell(i).toString());
							}
						}

						arrayData.add(object);
						row++;
					}

					dataList.add(new MDMData(sheet.getSheetName(), arrayData));
				}

				return dataList;
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		private JsonArray loadJsonData(String fileURL) {
			JsonParser parser = new JsonParser();
			JsonArray object = new JsonArray();

			try {
				FileReader fileReader = new FileReader(fileURL);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				String line = null;

				while ((line = bufferedReader.readLine()) != null) {
					object.addAll((JsonArray) parser.parse(line));
				}

				bufferedReader.close(); 
			} catch (JsonIOException e) {
				e.printStackTrace();
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return object;
		}

		public void loadDataExcelFile(String dataFile) {
			data = loadExcelFile(dataFile);
			Integer records = 0;
			for (MDMData mdmData : data) {
				records += mdmData.getData().size();
			}

			labelQuantityRecordsValue.setText(records + " records in " + data.size() + " sheet(s).");
		}

		public void loadDataJsonFile(String typeName, String dataFile) {
			MDMData complexData = new MDMData(typeName, loadJsonData(dataFile));
			data = new ArrayList<MDMData>();
			data.add(complexData);
			labelQuantityRecordsValue.setText(Integer.toString(data.get(0).getData().size()));
		}
	}

	public List<MDMData> getData() {
		return this.data;
	}

	public JTextField getTextTemplateName() {
		return textTemplateName;
	}

	public JCheckBox getCheckBoxCompress() {
		return checkBoxCompress;
	}

	@Override
	public StoredAbstractVO getAllData() {
		// TODO Auto-generated method stub
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
