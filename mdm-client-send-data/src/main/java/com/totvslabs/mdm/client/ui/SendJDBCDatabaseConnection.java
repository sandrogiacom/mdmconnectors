package com.totvslabs.mdm.client.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.totvslabs.mdm.client.pojo.JDBCDatabaseVO;
import com.totvslabs.mdm.client.pojo.JDBCDriverTypeVO;
import com.totvslabs.mdm.client.pojo.StoredAbstractVO;
import com.totvslabs.mdm.client.pojo.StoredJDBCConnectionVO;
import com.totvslabs.mdm.client.ui.events.JDBCConnectionStabilizedDispatcher;
import com.totvslabs.mdm.client.ui.events.JDBCConnectionStabilizedEvent;
import com.totvslabs.mdm.client.util.DBConnectionFactory;
import com.totvslabs.mdm.client.util.PersistenceEngine;

public class SendJDBCDatabaseConnection extends PanelAbstract {
	private static final long serialVersionUID = 1L;

	private JLabel labelConnectionProfile;
	private JComboBox<String> comboConnectionProfile;
	private DefaultComboBoxModel<String> comboBoxModelProfiles = new DefaultComboBoxModel<String>();

	private JLabel labelJDBCURL;
	private JTextField textJDBCURL;

	private JLabel labelJDBCUserName;
	private JTextField textJDBCUserName;

	private JLabel labelJDBCPassword;
	private JTextField textJDBCPassword;

	private JLabel labelDriver;
	private JComboBox<JDBCDriverTypeVO> comboDriver;

	private JButton buttonSaveJDBCConnection;
	private JButton buttonDeleteJDBCConnection;
	private JButton buttonConnectDisconnect;

	public static Map<String, JDBCDriverTypeVO> jdbcDrivers = new HashMap<String, JDBCDriverTypeVO>();
	
	public static final String DB_PROGRESS = "com.ddtek.jdbc.openedge.OpenEdgeDriver";
	public static final String DB_SQLSERVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	public static final String DB_ORACLE = "oracle.jdbc.driver.OracleDriver";
	public static final String DB_MONGO = "mongo";

	public SendJDBCDatabaseConnection(){
		super(2, 12, " JDBC Connection Parameters");

		this.labelConnectionProfile = new JLabel("Connection Profile: ");
		this.comboConnectionProfile = new JComboBox<String>(comboBoxModelProfiles);
		this.labelJDBCURL = new JLabel("JDBC URL: ");
		this.textJDBCURL = new JTextField("jdbc:sqlserver://192.168.56.101:1433;DatabaseName=ems2cad1211", 20);
		this.labelJDBCUserName = new JLabel("User: ");
		this.textJDBCUserName = new JTextField("sa", 20);
		this.labelJDBCPassword = new JLabel("Password: ");
		this.textJDBCPassword = new JPasswordField("sa", 20);
		this.labelDriver = new JLabel("Driver: ");
		this.comboDriver = new JComboBox<JDBCDriverTypeVO>();
		this.buttonSaveJDBCConnection = new JButton("Save");
		this.buttonDeleteJDBCConnection = new JButton("Delete");
		this.buttonConnectDisconnect = new JButton("Connect!");

		SendJDBCDatabaseConnection.jdbcDrivers.put(SendJDBCDatabaseConnection.DB_SQLSERVER, new JDBCDriverTypeVO(DB_SQLSERVER, "SQL Server", DB_SQLSERVER, "jdbc:sqlserver://192.168.56.101:1433;DatabaseName=ems2cad1211", "sa", "sa"));
		SendJDBCDatabaseConnection.jdbcDrivers.put(SendJDBCDatabaseConnection.DB_PROGRESS, new JDBCDriverTypeVO(DB_PROGRESS, "Progress", DB_PROGRESS, "jdbc:datadirect:openedge://192.168.56.101:2121;databaseName=marelli;defaultSchema=pub", "sysprogress", "sysprogress"));
		SendJDBCDatabaseConnection.jdbcDrivers.put(SendJDBCDatabaseConnection.DB_ORACLE, new JDBCDriverTypeVO(DB_ORACLE, "Oracle", DB_ORACLE, "jdbc:oracle:thin:@cordas:1521:cordas", "ems2cad1154", "ems2cad1154"));
		SendJDBCDatabaseConnection.jdbcDrivers.put(SendJDBCDatabaseConnection.DB_MONGO, new JDBCDriverTypeVO(DB_MONGO, "MongoDB", DB_MONGO, "localhost:27017:fiscalcloud", "", ""));
//		SendJDBCDatabaseConnection.jdbcDrivers.put("db2", new JDBCDriverTypeVO("db2", "DB2", "", "", "", ""));
//		SendJDBCDatabaseConnection.jdbcDrivers.put("informix", new JDBCDriverTypeVO("informix", "Informix", "", "", "", ""));

		this.comboConnectionProfile.setEditable(true);

		Collection<JDBCDriverTypeVO> values = jdbcDrivers.values();

		for (JDBCDriverTypeVO jdbcDriverTypeVO : values) {
			this.comboDriver.addItem(jdbcDriverTypeVO);
		}

		this.comboDriver.setSelectedIndex(-1);
		this.textJDBCURL.setText("");
		this.textJDBCUserName.setText("");
		this.textJDBCPassword.setText("");

		this.initializeLayout();
		this.loadDefaultData();
	}

	public void initializeLayout() {
		this.add(this.labelConnectionProfile);
		this.add(this.comboConnectionProfile);
		this.add(this.labelDriver);
		this.add(this.comboDriver);
		this.add(this.labelJDBCURL);
		this.add(this.textJDBCURL, 2, true, 1, 2);
		this.add(this.labelJDBCUserName);
		this.add(this.textJDBCUserName);
		this.add(this.labelJDBCPassword);
		this.add(this.textJDBCPassword);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(this.buttonSaveJDBCConnection, BorderLayout.WEST);
		panel.add(this.buttonConnectDisconnect, BorderLayout.CENTER);
		panel.add(this.buttonDeleteJDBCConnection, BorderLayout.EAST);
		this.add(new JLabel());
		this.add(panel);

		this.buttonSaveJDBCConnection.addActionListener(new SaveClick());
		this.buttonDeleteJDBCConnection.addActionListener(new DeleteClick());
		this.buttonConnectDisconnect.addActionListener(new ConnectClick(this));
		this.comboDriver.addItemListener(new ComboBoxStateChangeDriver());
		this.comboConnectionProfile.addActionListener(new ComboBoxStateChangeProfile());
	}

	class ConnectClick implements ActionListener {
		private JPanel parent;

		public ConnectClick(JPanel parent) {
			this.parent = parent;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if(comboDriver.getSelectedIndex() < 0) {
				JOptionPane.showMessageDialog(this.parent, "Please inform the conection parameters before perform the connection.");
				return;
			}

        	JDBCDatabaseVO database = DBConnectionFactory.getDb(((JDBCDriverTypeVO) comboDriver.getSelectedItem()).getDriverClass()).loadFisicModelTables(textJDBCURL.getText(), ((JDBCDriverTypeVO) comboDriver.getSelectedItem()).getDriverClass(), textJDBCUserName.getText(), textJDBCPassword.getText());

        	if(database == null) {
        		JOptionPane.showMessageDialog(this.parent, "An error occurred while establishing the connection, verify the error message.", "Connection Error", JOptionPane.ERROR_MESSAGE);
        		return;
        	}

			JDBCConnectionStabilizedEvent event = new JDBCConnectionStabilizedEvent((StoredJDBCConnectionVO) getAllData(), database.getTables());
			JDBCConnectionStabilizedDispatcher.getInstance().fireJDBCConnectionStabilizedEvent(event);
		}
	}

	class ComboBoxStateChangeDriver implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent e) {
			JDBCDriverTypeVO driverSelected = (JDBCDriverTypeVO) e.getItem();

			textJDBCPassword.setText(driverSelected.getPasswordSample());
			textJDBCURL.setText(driverSelected.getUrlSample());
			textJDBCUserName.setText(driverSelected.getUserSample());
		}
	}

	class ComboBoxStateChangeProfile implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			StoredAbstractVO actualRecord = PersistenceEngine.getInstance().getByName((String) comboBoxModelProfiles.getSelectedItem(), StoredJDBCConnectionVO.class);
			loadAllData(actualRecord);
		}
	}

	@Override
	public StoredAbstractVO getAllData() {
		StoredJDBCConnectionVO instance = new StoredJDBCConnectionVO();
		instance.setProfileName((String) this.comboConnectionProfile.getSelectedItem());
		instance.setDriver(this.comboDriver.getSelectedItem() != null ? ((JDBCDriverTypeVO) this.comboDriver.getSelectedItem()).getDriverClass() : null);
		instance.setUrl(this.textJDBCURL.getText());
		instance.setUsername(this.textJDBCUserName.getText());
		instance.setPassword(this.textJDBCPassword.getText());

		return instance;
	}

	@Override
	public void loadAllData(StoredAbstractVO instanceGeneric) {
		if(instanceGeneric == null) {
			instanceGeneric = new StoredJDBCConnectionVO();
		}
		if(!(instanceGeneric instanceof StoredJDBCConnectionVO)) {
			return;
		}

		StoredJDBCConnectionVO instance = (StoredJDBCConnectionVO) instanceGeneric;
//		this.comboConnectionProfile.setSelectedItem(instance.getProfileName());
		this.comboDriver.setSelectedItem(jdbcDrivers.get(instance.getDriver()));
		this.textJDBCURL.setText(instance.getUrl());
		this.textJDBCUserName.setText(instance.getUsername());
		this.textJDBCPassword.setText(instance.getPassword());		
	}

	@Override
	public void loadDefaultData() {
		List<StoredAbstractVO> data = PersistenceEngine.getInstance().findAll(StoredJDBCConnectionVO.class);

		this.comboBoxModelProfiles.removeAllElements();

		if(data != null) {
			for (StoredAbstractVO storedAbstractVO : data) {
				this.comboBoxModelProfiles.addElement(storedAbstractVO.getName());
				System.out.println(storedAbstractVO);
			}
		}
	}
}

