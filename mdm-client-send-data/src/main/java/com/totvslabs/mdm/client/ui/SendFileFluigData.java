package com.totvslabs.mdm.client.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.totvslabs.mdm.client.pojo.MDMAbstractData;
import com.totvslabs.mdm.client.pojo.MDMJsonData;
import com.totvslabs.mdm.client.pojo.StoredAbstractVO;
import com.totvslabs.mdm.client.pojo.StoredFluigDataProfileVO;
import com.totvslabs.mdm.client.ui.events.JDBCConnectionStabilizedDispatcher;
import com.totvslabs.mdm.client.ui.events.JDBCConnectionStabilizedEvent;
import com.totvslabs.mdm.client.ui.events.MDMConnectionChangedDispatcher;
import com.totvslabs.mdm.client.ui.events.MDMConnectionChangedEvent;
import com.totvslabs.mdm.client.ui.events.MDMConnectionChangedListener;
import com.totvslabs.mdm.client.util.FileConsume;

public class SendFileFluigData extends PanelAbstract implements MDMConnectionChangedListener {
	private static final long serialVersionUID = 1L;

	private JLabel labelSelectedFile;
	private JLabel labelSelectedFileValue;
	private JLabel labelQuantityRecords;
	private JLabel labelQuantityRecordsValue;

	private JButton buttonSendTxtData;

	private StoredFluigDataProfileVO fluigDataProfile;

	public SendFileFluigData(){
		super(1, 16, " Send Data: File");

		this.labelSelectedFile = new JLabel("File: ");
		this.labelSelectedFileValue = new JLabel();
		this.labelQuantityRecords = new JLabel("Quantity records: ");
		this.labelQuantityRecordsValue = new JLabel();

		this.buttonSendTxtData = new JButton("Load File");

		MDMConnectionChangedDispatcher.getInstance().addMDMConnectionChangedListener(this);

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

		this.buttonSendTxtData.addActionListener(new SendJsonFile());
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
			fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

			int result = fileChooser.showOpenDialog(null);

			if (result == JFileChooser.APPROVE_OPTION) {
				String filename = fileChooser.getSelectedFile().getPath();

				labelSelectedFileValue.setText(filename);

				FileConsume.getInstance(fileChooser.getSelectedFile().getPath()).addEntity(fluigDataProfile, fileChooser.getSelectedFile().getPath());
				NumberFormat nf = new DecimalFormat();

				labelQuantityRecordsValue.setText(FileConsume.getInstance(fileChooser.getSelectedFile().getPath()).getTotalRecordsToSend() + " records in " + nf.format(FileConsume.getInstance(fileChooser.getSelectedFile().getPath()).getFilesToSend().size()) + " file(s).");

				JDBCConnectionStabilizedEvent event = new JDBCConnectionStabilizedEvent();
				JDBCConnectionStabilizedDispatcher.getInstance().fireJDBCConnectionStabilizedEvent(event);
			}
		}

		private List<MDMAbstractData> loadExcelFile(String fileURL) {
			try {
				List<MDMAbstractData> dataList = new ArrayList<MDMAbstractData>();

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

					dataList.add(new MDMJsonData(sheet.getSheetName(), arrayData));
				}

				return dataList;
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		//TODO: fixme
//		public void loadDataExcelFile(String dataFile) {
//			data = loadExcelFile(dataFile);
//			Integer records = 0;
//			for (MDMAbstractData mdmData : data) {
//				records += mdmData.getData().size();
//			}
//
//			labelQuantityRecordsValue.setText(records + " records in " + data.size() + " sheet(s).");
//		}
	}

	public String getFilenamePath() {
		return labelSelectedFileValue.getText();
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
	}
}
