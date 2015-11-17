package com.totvslabs.mdm.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;

import com.totvslabs.mdm.client.pojo.JDBCConnectionParameter;
import com.totvslabs.mdm.client.pojo.JDBCTableVO;
import com.totvslabs.mdm.client.ui.events.ChangeTabDispatcher;
import com.totvslabs.mdm.client.ui.events.ChangeTabEvent;
import com.totvslabs.mdm.client.ui.events.ChangeTabListener;
import com.totvslabs.mdm.client.ui.events.DatasourceChangedDispatcher;
import com.totvslabs.mdm.client.ui.events.DatasourceChangedEvent;
import com.totvslabs.mdm.client.ui.events.DatasourceChangedListener;
import com.totvslabs.mdm.client.ui.events.DataLoadedDispatcher;
import com.totvslabs.mdm.client.ui.events.DataLoadedEvent;
import com.totvslabs.mdm.client.ui.events.DataLoadedListener;
import com.totvslabs.mdm.client.ui.events.JDBCTableSelectedDispatcher;
import com.totvslabs.mdm.client.ui.events.JDBCTableSelectedEvent;
import com.totvslabs.mdm.client.ui.events.JDBCTableSelectedListener;
import com.totvslabs.mdm.client.ui.events.LogManagerDispatcher;
import com.totvslabs.mdm.client.ui.events.SendDataFluigDataDoneDispatcher;
import com.totvslabs.mdm.client.ui.events.SendDataFluigDataDoneEvent;
import com.totvslabs.mdm.client.ui.events.SendDataFluigDataDoneListener;
import com.totvslabs.mdm.client.ui.events.SendDataFluigDataUpdateProcessDispatcher;
import com.totvslabs.mdm.client.ui.events.SendDataFluigDataUpdateProcessEvent;
import com.totvslabs.mdm.client.ui.events.SendDataFluigDataUpdateProcessListener;
import com.totvslabs.mdm.client.util.ProcessTypeEnum;
import com.totvslabs.mdm.client.util.ThreadExportData;

public class PanelGeneral extends JFrame implements JDBCTableSelectedListener, DatasourceChangedListener, ChangeTabListener, DataLoadedListener, SendDataFluigDataUpdateProcessListener {
	private static final long serialVersionUID = 1L;

	public static final String EXECUTE_OK = "Execute!";
	public static final String EXECUTE_CANCEL = "Cancel";
	public static final String EXECUTE_CANCELING = "Cancelling";

	private JPanel mainPanel = new JPanel();
	private MDMDatabaseConnection panelMDMConnection = new MDMDatabaseConnection();
	@SuppressWarnings("unused")
	private MDMEntities panelMDMEntities = new MDMEntities();
	private SendJDBCDatabaseConnection panelJDBCConnection = new SendJDBCDatabaseConnection();
	private SendFileFluigData panelSendFileFluigData = new SendFileFluigData();
	private SendJDBCEntities panelJDBCEntities = new SendJDBCEntities();
	private ProcessLog processLog = new ProcessLog();

	private JTabbedPane tabbedPane;

	private JProgressBar progressBar = new JProgressBar();

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
		this.setSize(714, 800);
		this.setVisible(true);
	}

	public void initializeLayout() {
		JDBCTableSelectedDispatcher.getInstance().addJDBCTableSelectedListener(this);
		DatasourceChangedDispatcher.getInstance().addDatasourceChangedListener(this);
		SendDataFluigDataUpdateProcessDispatcher.getInstance().addSendDataFluigDataUpdateProcessListener(this);
		this.buttonExit.addActionListener(new ExitApplication());
		this.buttonExecute.addActionListener(new ExecuteSendData());

		this.mainPanel.setLayout(new BorderLayout());

		JPanel mainJDBCPanel = new JPanel();
		mainJDBCPanel.setLayout(new BorderLayout());

		mainJDBCPanel.add(this.panelJDBCConnection, BorderLayout.NORTH);
		mainJDBCPanel.add(this.panelJDBCEntities, BorderLayout.SOUTH);

		tabbedPane = new JTabbedPane();

		tabbedPane.addTab("MDM Connection", this.panelMDMConnection);
		tabbedPane.addTab("Send Data: JDBC Connection", mainJDBCPanel);
		tabbedPane.addTab("Send Data: File", panelSendFileFluigData);
		tabbedPane.addTab("Process Log", this.processLog);

		this.tabbedPane.setEnabledAt(1, false);
		this.tabbedPane.setEnabledAt(2, false);
		this.tabbedPane.setBackgroundAt(1, Color.GRAY);
		this.tabbedPane.setBackgroundAt(2, Color.GRAY);

		JPanel mainButtonPanel = new JPanel();
		mainButtonPanel.setLayout(new BorderLayout());
		JPanel mainButtonSpecificPanel = new JPanel();
		mainButtonSpecificPanel.add(this.buttonExecute);
		mainButtonSpecificPanel.add(this.buttonExit);

		mainButtonPanel.add(this.progressBar, BorderLayout.NORTH);
		mainButtonPanel.add(mainButtonSpecificPanel, BorderLayout.CENTER);		

		LogManagerDispatcher.getInstance().addLogManagerListener(this.processLog);
		ChangeTabDispatcher.getInstance().addChangeTabListener(this);
		DataLoadedDispatcher.getInstance().addJDBCConnectionStabilizedListener(this);

		this.buttonExecute.setEnabled(false);

		this.mainPanel.add(tabbedPane, BorderLayout.CENTER);
		this.mainPanel.add(mainButtonPanel, BorderLayout.SOUTH);
	}

	public class ExecuteSendData implements ActionListener, SendDataFluigDataDoneListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			buttonExecute.setEnabled(false);
			tabbedPane.setSelectedIndex(3);

			SendDataFluigDataUpdateProcessDispatcher.getInstance().fireSendDataFluigDataUpdateProcessEvent(new SendDataFluigDataUpdateProcessEvent(0, 0, ProcessTypeEnum.SEND_DATA));
			SendDataFluigDataDoneDispatcher.getInstance().addSendDataFluigDataDoneListener(this);

			if(panelMDMConnection.isDatabaseData()) {
				Thread th = new Thread(new ThreadExportData(panelMDMConnection.getTextMDMServerURL().getText(), tenantId, datasourceId, tableVO, param, panelJDBCEntities));
				th.start();
			}
			else {
				Thread th = new Thread(new ThreadExportData(panelMDMConnection.getTextMDMServerURL().getText(), tenantId, datasourceId, panelSendFileFluigData));
				th.start();
			}
		}

		@Override
		public void onSendDataFluigDataDone(SendDataFluigDataDoneEvent event) {
			tabbedPane.setSelectedIndex(0);
			buttonExecute.setEnabled(true);
			JOptionPane.showMessageDialog(null, "Send data process finished sucessfully!");
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
		
		if(event.getTabToSelect() == 1) {
			this.tabbedPane.setEnabledAt(2, false);
			this.tabbedPane.setBackgroundAt(2, Color.GRAY);
			this.tabbedPane.setEnabledAt(1, true);
			this.tabbedPane.setBackgroundAt(1, Color.WHITE);
		}
		else if(event.getTabToSelect() == 2) {
			this.tabbedPane.setEnabledAt(1, false);
			this.tabbedPane.setBackgroundAt(1, Color.GRAY);
			this.tabbedPane.setEnabledAt(2, true);
			this.tabbedPane.setBackgroundAt(2, Color.WHITE);
		}
	}

	@Override
	public void onDataLoadedEvent(DataLoadedEvent event) {
		this.buttonExecute.setEnabled(true);
	}

	@Override
	public void onSendDataFluigDataUpdateProcess(SendDataFluigDataUpdateProcessEvent event) {
		this.progressBar.setMaximum(event.getTotalRecords());
		this.progressBar.setValue(event.getRecordsSent());
	}
}
