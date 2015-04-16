package com.totvslabs.mdm.client.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.google.gson.JsonArray;
import com.totvslabs.mdm.client.pojo.JDBCConnectionParameter;
import com.totvslabs.mdm.client.pojo.JDBCTableVO;
import com.totvslabs.mdm.client.ui.events.DatasourceChangedDispatcher;
import com.totvslabs.mdm.client.ui.events.DatasourceChangedEvent;
import com.totvslabs.mdm.client.ui.events.DatasourceChangedListener;
import com.totvslabs.mdm.client.ui.events.JDBCTableSelectedDispatcher;
import com.totvslabs.mdm.client.ui.events.JDBCTableSelectedEvent;
import com.totvslabs.mdm.client.ui.events.JDBCTableSelectedListener;
import com.totvslabs.mdm.client.util.JDBCConnectionFactory;
import com.totvslabs.mdm.restclient.MDMRestConnection;
import com.totvslabs.mdm.restclient.MDMRestConnectionFactory;
import com.totvslabs.mdm.restclient.command.CommandPostStaging;
import com.totvslabs.mdm.restclient.command.CommandPostStagingC;

public class PanelGeneral extends JFrame implements JDBCTableSelectedListener, DatasourceChangedListener {
	private static final long serialVersionUID = 1L;

	public static final String EXECUTE_OK = "Execute!";
	public static final String EXECUTE_CANCEL = "Cancel";
	public static final String EXECUTE_CANCELING = "Cancelling";

	private JPanel mainPanel = new JPanel();
	private MDMDatabaseConnection panelMDMConnection = new MDMDatabaseConnection();
	@SuppressWarnings("unused")
	private MDMEntities panelMDMEntities = new MDMEntities();
	private JDBCDatabaseConnection panelJDBCConnection = new JDBCDatabaseConnection();
	private JDBCEntities panelJDBCEntities = new JDBCEntities();

	private JButton buttonExecute = new JButton(EXECUTE_OK);
	private JButton buttonExit = new JButton("Exit");

	private JDBCConnectionParameter param;
	private JDBCTableVO tableVO;
	private String tenantId;
	private String datasourceId;

	public PanelGeneral(){
		this.setTitle("MDM: Generic Client");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().add(this.mainPanel);
		this.setResizable(false);

		this.initializeLayout();

		this.pack();
		this.setSize(714, 780);
		this.setVisible(true);
	}

	public void initializeLayout() {
		JDBCTableSelectedDispatcher.getInstance().addJDBCTableSelectedListener(this);
		DatasourceChangedDispatcher.getInstance().addDatasourceChangedListener(this);
		this.buttonExit.addActionListener(new ExitApplication());
		this.buttonExecute.addActionListener(new ExecuteSendData());

		this.mainPanel.setLayout(new BorderLayout());

		JPanel mainJDBCPanel = new JPanel();
		mainJDBCPanel.setLayout(new BorderLayout());

		mainJDBCPanel.add(this.panelJDBCConnection, BorderLayout.NORTH);
		mainJDBCPanel.add(this.panelJDBCEntities, BorderLayout.SOUTH);

		JTabbedPane tabbedPane = new JTabbedPane();

		tabbedPane.addTab("MDM Connection", this.panelMDMConnection);
		tabbedPane.addTab("JDBC Connection", mainJDBCPanel);

		JPanel mainButtonPanel = new JPanel();
		mainButtonPanel.add(this.buttonExecute);
		mainButtonPanel.add(this.buttonExit);

//		this.mainPanel.add(this.panelMDMEntities);
		this.mainPanel.add(tabbedPane, BorderLayout.CENTER);
		this.mainPanel.add(mainButtonPanel, BorderLayout.SOUTH);
	}

//	@Override
//	public void onChangeInstanceType(InstanceTypeEvent event) {
//		Component[] components = this.mainPanel.getComponents();
//
//		for (Component component : components) {
//			if(component instanceof PanelAbstract) {
//				if(event.getInstanceTypeEnum().equals(InstanceTypeEnum.MASTER)) {
//					((PanelAbstract) component).enableFields();
//					this.buttonPrepareData.setEnabled(Boolean.TRUE);
//					this.buttonExecute.setEnabled(Boolean.FALSE);
//					this.buttonExit.setEnabled(Boolean.TRUE);
//				}
//				else if(event.getInstanceTypeEnum().equals(InstanceTypeEnum.SLAVE)) {
//					((PanelAbstract) component).disableFields();
//					this.buttonPrepareData.setEnabled(Boolean.FALSE);
//					this.buttonExecute.setEnabled(Boolean.FALSE);
//					this.buttonExit.setEnabled(Boolean.FALSE);
//				}
//			}
//		}
//
//		if(event.getInstanceTypeEnum().equals(InstanceTypeEnum.SLAVE)) {
//			this.actualInstanceType = InstanceTypeEnum.SLAVE;
//			this.fillComponents(new MasterConfigurationData());
//		}
//		else {
//			this.actualInstanceType = InstanceTypeEnum.MASTER;
//		}
//	}

//	@Override
//	public void onDataChangedOccur(DataChangedEvent event) {
//		if(this.actualInstanceType != null && this.actualInstanceType.equals(InstanceTypeEnum.MASTER)) {
//			MasterConfigurationData masterConfigurationData = fillData();
//
//			if(this.masterCommunicationManagement != null) {
//				this.masterCommunicationManagement.setMasterConfigurationData(masterConfigurationData);
//			}
//		}
//	}

	public class ExecuteSendData implements ActionListener, Runnable {
		@Override
		public void actionPerformed(ActionEvent event) {
			Thread th = new Thread(this);
			th.start();
		}

		@Override
		public void run() {
			DateFormat df = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");

			System.out.println("Starting the process now: " + df.format(Calendar.getInstance().getTime()));
			JsonArray data = JDBCConnectionFactory.loadData(param, tableVO);
			Integer batchSize = 100;
			long totalDataSend = 0l;

			try {
				batchSize = Integer.parseInt(panelJDBCEntities.getTextBatchSize().getText());
			}
			catch(NumberFormatException e) {
			}

			System.out.println("I will start to send " + data.size() + " records...");

			JsonArray lote = new JsonArray();

			for(int i=0; i<data.size(); i++) {
				lote.add(data.get(i));

				if(i!= 0 && i%(batchSize) == 0 || ((totalDataSend + lote.size()) == data.size())) {
					CommandPostStaging staging = null;

					if(panelJDBCEntities.getCheckBoxCompress().isSelected()) {
						staging = new CommandPostStagingC(tenantId, datasourceId, panelJDBCEntities.getTextTemplateName().getText(), lote);
					}
					else {
						staging = new CommandPostStaging(tenantId, datasourceId, panelJDBCEntities.getTextTemplateName().getText(), lote);
					}

					MDMRestConnection connection = MDMRestConnectionFactory.getConnection(panelMDMConnection.getTextMDMServerURL().getText());

					try {
						long initialTime = System.currentTimeMillis();
						String additionalInformation = "";

						if(staging instanceof CommandPostStagingC) {
							additionalInformation = " - COMPRESS";
						}

						connection.executeCommand(staging);
						System.out.println("Total time execute command: " + (System.currentTimeMillis() - initialTime) + additionalInformation);
						totalDataSend += lote.size();
					}
					catch(Exception e) {
						System.err.println("Error: " + e.getMessage());
						e.printStackTrace();
					}

					double n1 = totalDataSend;
					double n2 = data.size();
					double result = n1 / n2;
					DecimalFormat decF = new DecimalFormat("0.00");

					lote = new JsonArray();
					System.out.println("Total data to be sent: " + data.size() + ", data send until now: " + totalDataSend + ":::::::" + decF.format(result*100) + "%");
				}
			}

			System.out.println("Finishing the process now: " + df.format(Calendar.getInstance().getTime()));
		}
	}

	public class ExitApplication implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			System.exit(JFrame.NORMAL);
		}
	}

	@Override
	public void onJDBCTableSelectedEvent(JDBCTableSelectedEvent event) {
		tableVO = event.getTableVO();
		param = event.getParam();
	}

	@Override
	public void onDatasourceChangedEvent(DatasourceChangedEvent event) {
		if(event.getActualValue() != null) {
			tenantId = event.getActualValue().getTenantId();
			datasourceId = event.getActualValue().getDatasourceId();
		}
	}
}
