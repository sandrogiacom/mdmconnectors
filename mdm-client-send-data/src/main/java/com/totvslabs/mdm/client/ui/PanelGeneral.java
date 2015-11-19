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

import com.totvslabs.mdm.client.pojo.DataSourceTypeEnum;
import com.totvslabs.mdm.client.pojo.JDBCTableVO;
import com.totvslabs.mdm.client.pojo.StoredConfigurationVO;
import com.totvslabs.mdm.client.pojo.StoredFluigDataProfileVO;
import com.totvslabs.mdm.client.pojo.StoredJDBCConnectionVO;
import com.totvslabs.mdm.client.ui.events.ChangeTabDispatcher;
import com.totvslabs.mdm.client.ui.events.ChangeTabEvent;
import com.totvslabs.mdm.client.ui.events.ChangeTabListener;
import com.totvslabs.mdm.client.ui.events.JDBCConnectionStabilizedDispatcher;
import com.totvslabs.mdm.client.ui.events.JDBCConnectionStabilizedEvent;
import com.totvslabs.mdm.client.ui.events.JDBCConnectionStabilizedListener;
import com.totvslabs.mdm.client.ui.events.JDBCTableSelectedDispatcher;
import com.totvslabs.mdm.client.ui.events.JDBCTableSelectedEvent;
import com.totvslabs.mdm.client.ui.events.JDBCTableSelectedListener;
import com.totvslabs.mdm.client.ui.events.LogManagerDispatcher;
import com.totvslabs.mdm.client.ui.events.MDMConnectionChangedDispatcher;
import com.totvslabs.mdm.client.ui.events.MDMConnectionChangedEvent;
import com.totvslabs.mdm.client.ui.events.MDMConnectionChangedListener;
import com.totvslabs.mdm.client.ui.events.SendDataFluigDataDoneDispatcher;
import com.totvslabs.mdm.client.ui.events.SendDataFluigDataDoneEvent;
import com.totvslabs.mdm.client.ui.events.SendDataFluigDataDoneListener;
import com.totvslabs.mdm.client.ui.events.SendDataFluigDataUpdateProcessDispatcher;
import com.totvslabs.mdm.client.ui.events.SendDataFluigDataUpdateProcessEvent;
import com.totvslabs.mdm.client.ui.events.SendDataFluigDataUpdateProcessListener;
import com.totvslabs.mdm.client.ui.events.StoredConfigurationChangedDispatcher;
import com.totvslabs.mdm.client.ui.events.StoredConfigurationChangedEvent;
import com.totvslabs.mdm.client.ui.events.StoredConfigurationSelectedDispatcher;
import com.totvslabs.mdm.client.ui.events.StoredConfigurationSelectedEvent;
import com.totvslabs.mdm.client.ui.events.StoredConfigurationSelectedListener;
import com.totvslabs.mdm.client.util.PersistenceEngine;
import com.totvslabs.mdm.client.util.ProcessTypeEnum;
import com.totvslabs.mdm.client.util.ThreadExportData;

public class PanelGeneral extends JFrame implements JDBCTableSelectedListener, ChangeTabListener, JDBCConnectionStabilizedListener, SendDataFluigDataUpdateProcessListener, StoredConfigurationSelectedListener {
	private static final long serialVersionUID = 1L;

	public static final String EXECUTE_OK = "Execute!";
	public static final String EXECUTE_CANCEL = "Cancel";
	public static final String EXECUTE_CANCELING = "Cancelling";

	private JPanel mainPanel = new JPanel();
	private StoredConfigurationPanel storedConfigurationPanel = new StoredConfigurationPanel();
	private MDMDatabaseConnection panelMDMConnection = new MDMDatabaseConnection();
//	private MDMEntities panelMDMEntities = new MDMEntities();
	private SendJDBCDatabaseConnection panelJDBCConnection = new SendJDBCDatabaseConnection();
	private SendJDBCEntities panelJDBCEntities = new SendJDBCEntities();
	private SendFileFluigData panelSendFileFluigData = new SendFileFluigData();
	private MDMConsumer panelMDMConsumer = new MDMConsumer();
	private ProcessLog processLog = new ProcessLog();

	private JTabbedPane tabbedPane;

	private JProgressBar progressBar = new JProgressBar();

	private JButton buttonExecute = new JButton(EXECUTE_OK);
	private JButton buttonExit = new JButton("Exit");

	private StoredJDBCConnectionVO jdbcConnectionVO;
	private StoredConfigurationVO selectedConfigurationVO;
	private JDBCTableVO tableVO;

	public PanelGeneral(){
		this.setTitle("MDM: Generic Client");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().add(this.mainPanel);
		this.setResizable(false);

		this.initializeLayout();

		this.pack();
		this.setSize(714, 900);
		this.setVisible(true);
	}

	public void initializeLayout() {
		JDBCTableSelectedDispatcher.getInstance().addJDBCTableSelectedListener(this);
		SendDataFluigDataUpdateProcessDispatcher.getInstance().addSendDataFluigDataUpdateProcessListener(this);
		this.buttonExit.addActionListener(new ExitApplication());
		this.buttonExecute.addActionListener(new ExecuteSendData());

		this.mainPanel.setLayout(new BorderLayout());

		JPanel southPanel = new JPanel();
		JPanel mainJDBCPanel = new JPanel();
		mainJDBCPanel.setLayout(new BorderLayout());
		southPanel.setLayout(new BorderLayout());

		southPanel.add(this.panelJDBCEntities, BorderLayout.CENTER);

		mainJDBCPanel.add(this.panelJDBCConnection, BorderLayout.NORTH);
		mainJDBCPanel.add(southPanel, BorderLayout.CENTER);
		
		JPanel buttonPanels = new JPanel();
		buttonPanels.setLayout(new BorderLayout());

		JButton saveConfigurationBtn = new JButton("Save Configuration");
		saveConfigurationBtn.addActionListener(new SaveConfiguration());

		JButton deleteConfiguration = new JButton("Delete Configuration");
		deleteConfiguration.addActionListener(new DeleteConfiguration());
		
		buttonPanels.add(saveConfigurationBtn, BorderLayout.WEST);
		buttonPanels.add(deleteConfiguration, BorderLayout.EAST);
		mainJDBCPanel.add(buttonPanels, BorderLayout.SOUTH);

		tabbedPane = new JTabbedPane();

		tabbedPane.addTab("Fluig Data Connection", this.panelMDMConnection);
		tabbedPane.addTab("Send Data: JDBC", mainJDBCPanel);
		tabbedPane.addTab("Send Data: File", panelSendFileFluigData);
		tabbedPane.addTab("Fluig Data Consumer", this.panelMDMConsumer);
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
		JDBCConnectionStabilizedDispatcher.getInstance().addJDBCConnectionStabilizedListener(this);
		StoredConfigurationSelectedDispatcher.getInstance().addStoredConfigurationSelectedListener(this);

		this.buttonExecute.setEnabled(false);

		this.mainPanel.add(this.storedConfigurationPanel, BorderLayout.NORTH);
		this.mainPanel.add(this.tabbedPane, BorderLayout.CENTER);
		this.mainPanel.add(mainButtonPanel, BorderLayout.SOUTH);
	}

	public class DeleteConfiguration implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			PersistenceEngine.getInstance().delete(selectedConfigurationVO);

			StoredConfigurationChangedDispatcher.getInstance().fireStoredConfigurationChangedEvent(new StoredConfigurationChangedEvent());
		}
	}

	public class SaveConfiguration implements ActionListener, JDBCConnectionStabilizedListener, JDBCTableSelectedListener, MDMConnectionChangedListener {
		private StoredFluigDataProfileVO fluigDataProfile;
		private StoredJDBCConnectionVO jdbcConnection;
		private JDBCTableVO tableVO;

		public SaveConfiguration() {
			JDBCConnectionStabilizedDispatcher.getInstance().addJDBCConnectionStabilizedListener(this);
			JDBCTableSelectedDispatcher.getInstance().addJDBCTableSelectedListener(this);
			MDMConnectionChangedDispatcher.getInstance().addMDMConnectionChangedListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			StoredConfigurationVO configuration = new StoredConfigurationVO();
			configuration.setFluigDataName(fluigDataProfile.getName());
			configuration.setTypeEnum(DataSourceTypeEnum.DB);
			configuration.setDatasourceID(jdbcConnection.getName());
			configuration.setSourceName(tableVO.getName());

			PersistenceEngine.getInstance().save(configuration);

			StoredConfigurationChangedDispatcher.getInstance().fireStoredConfigurationChangedEvent(new StoredConfigurationChangedEvent());
		}

		@Override
		public void onMDMConnectionChangedListener(MDMConnectionChangedEvent event) {
			fluigDataProfile = event.getActualConnection();
		}

		@Override
		public void onJDBCTableSelectedEvent(JDBCTableSelectedEvent event) {
			this.tableVO = event.getTableVO();
		}

		@Override
		public void onDataLoadedEvent(JDBCConnectionStabilizedEvent event) {
			this.jdbcConnection = event.getJdbcConnectionVO();
		}
	}

	public class ExecuteSendData implements ActionListener, SendDataFluigDataDoneListener {
		public ExecuteSendData() {
			SendDataFluigDataUpdateProcessDispatcher.getInstance().fireSendDataFluigDataUpdateProcessEvent(new SendDataFluigDataUpdateProcessEvent(0, 0, ProcessTypeEnum.SEND_DATA));
			SendDataFluigDataDoneDispatcher.getInstance().addSendDataFluigDataDoneListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			buttonExecute.setEnabled(false);
			tabbedPane.setSelectedIndex(3);

			if(panelMDMConnection.isDatabaseData()) {
				Thread th = new Thread(new ThreadExportData((StoredFluigDataProfileVO) panelMDMConnection.getAllData(), tableVO, jdbcConnectionVO, panelJDBCEntities));
				th.start();
			}
			else {
				Thread th = new Thread(new ThreadExportData((StoredFluigDataProfileVO) panelMDMConnection.getAllData(), panelSendFileFluigData));
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
		this.tableVO = event.getTableVO();
		this.jdbcConnectionVO = event.getJdbcConnectionVO();
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
	public void onDataLoadedEvent(JDBCConnectionStabilizedEvent event) {
		this.buttonExecute.setEnabled(true);
	}

	@Override
	public void onSendDataFluigDataUpdateProcess(SendDataFluigDataUpdateProcessEvent event) {
		this.progressBar.setMaximum(event.getTotalRecords());
		this.progressBar.setValue(event.getRecordsSent());
	}

	@Override
	public void onStoredConfigurationSelectedEvent(StoredConfigurationSelectedEvent event) {
		this.selectedConfigurationVO = event.getConfigurationVO();
	}
}
