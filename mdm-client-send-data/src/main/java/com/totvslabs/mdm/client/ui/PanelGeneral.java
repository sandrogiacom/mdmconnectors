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
import com.totvslabs.mdm.client.ui.events.ChangeTabDispatcher;
import com.totvslabs.mdm.client.ui.events.ChangeTabEvent;
import com.totvslabs.mdm.client.ui.events.ChangeTabListener;
import com.totvslabs.mdm.client.ui.events.DatasourceChangedDispatcher;
import com.totvslabs.mdm.client.ui.events.DatasourceChangedEvent;
import com.totvslabs.mdm.client.ui.events.DatasourceChangedListener;
import com.totvslabs.mdm.client.ui.events.JDBCConnectionStabilizedDispatcher;
import com.totvslabs.mdm.client.ui.events.JDBCConnectionStabilizedEvent;
import com.totvslabs.mdm.client.ui.events.JDBCConnectionStabilizedListener;
import com.totvslabs.mdm.client.ui.events.JDBCTableSelectedDispatcher;
import com.totvslabs.mdm.client.ui.events.JDBCTableSelectedEvent;
import com.totvslabs.mdm.client.ui.events.JDBCTableSelectedListener;
import com.totvslabs.mdm.client.ui.events.LogManagerDispatcher;
import com.totvslabs.mdm.client.util.JDBCConnectionFactory;
import com.totvslabs.mdm.restclient.MDMRestConnection;
import com.totvslabs.mdm.restclient.MDMRestConnectionFactory;
import com.totvslabs.mdm.restclient.command.CommandPostStaging;
import com.totvslabs.mdm.restclient.command.CommandPostStagingC;

public class PanelGeneral extends JFrame implements JDBCTableSelectedListener, DatasourceChangedListener, ChangeTabListener, JDBCConnectionStabilizedListener {
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
	private ProcessLog processLog = new ProcessLog();

	private JTabbedPane tabbedPane;

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

		tabbedPane = new JTabbedPane();

		tabbedPane.addTab("MDM Connection", this.panelMDMConnection);
		tabbedPane.addTab("JDBC Connection", mainJDBCPanel);
		tabbedPane.addTab("Process Log", this.processLog);

		JPanel mainButtonPanel = new JPanel();
		mainButtonPanel.add(this.buttonExecute);
		mainButtonPanel.add(this.buttonExit);

		LogManagerDispatcher.getInstance().addLogManagerListener(this.processLog);
		ChangeTabDispatcher.getInstance().addChangeTabListener(this);
		JDBCConnectionStabilizedDispatcher.getInstance().addJDBCConnectionStabilizedListener(this);

		this.buttonExecute.setEnabled(false);

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
			buttonExecute.setEnabled(false);
			tabbedPane.setSelectedIndex(2);

			Thread th = new Thread(this);
			th.start();
		}

		@Override
		public void run() {
			DateFormat df = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z");

			LogManagerDispatcher.getInstance().register("Starting the process now: " + df.format(Calendar.getInstance().getTime()));
			Integer batchSize = 100;
			Integer totalRecords = tableVO.getTotalRecords();

			try {
				batchSize = Integer.parseInt(panelJDBCEntities.getTextBatchSize().getText());
			}
			catch(NumberFormatException e) {
			}

			LogManagerDispatcher.getInstance().register("I am going to send " + totalRecords + " records...");

			for(int totalDataSend=0; totalDataSend<totalRecords;) {
				JsonArray lote = JDBCConnectionFactory.loadData(param, tableVO, totalDataSend, batchSize);

				CommandPostStaging staging = null;

				if(panelJDBCEntities.getCheckBoxCompress().isSelected()) {
					staging = new CommandPostStagingC(tenantId, datasourceId, panelJDBCEntities.getTextTemplateName().getText(), lote);
				}
				else {
					staging = new CommandPostStaging(tenantId, datasourceId, panelJDBCEntities.getTextTemplateName().getText(), lote);
				}

				MDMRestConnection connection = MDMRestConnectionFactory.getConnection(panelMDMConnection.getTextMDMServerURL().getText());

				long initialTime = System.currentTimeMillis();
				long endTime = initialTime;

				String additionalInformation = "";

				if(staging instanceof CommandPostStagingC) {
					additionalInformation = " (compressed)";
				}

				try {
					connection.executeCommand(staging);
					endTime = System.currentTimeMillis();
					totalDataSend += lote.size();
				}
				catch(Exception e) {
					System.err.println("Error: " + e.getMessage());
					e.printStackTrace();
				}

				double n1 = totalDataSend;
				double n2 = totalRecords;
				double result = n1 / n2;
				DecimalFormat decF = new DecimalFormat("0.00");

				LogManagerDispatcher.getInstance().register("Sent " + lote.size() + additionalInformation + " records in " + (endTime - initialTime) + "ms, " + decF.format(result*100) + "% completed (" + totalDataSend + " in total).");
			}

			LogManagerDispatcher.getInstance().register("Finished the process now: " + df.format(Calendar.getInstance().getTime()) + "\n\n");

			tabbedPane.setSelectedIndex(2);
			buttonExecute.setEnabled(true);
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

	@Override
	public void onChangeTabListener(ChangeTabEvent event) {
		this.tabbedPane.setSelectedIndex(event.getTabToSelect());
	}

	@Override
	public void onJDBCConnectionStabilizedEvent(JDBCConnectionStabilizedEvent event) {
		this.buttonExecute.setEnabled(true);
	}
}
