package com.totvslabs.mdm.client.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.totvslabs.mdm.client.pojo.StoredAbstractVO;
import com.totvslabs.mdm.client.pojo.StoredFluigDataProfileVO;
import com.totvslabs.mdm.client.ui.events.ChangeTabDispatcher;
import com.totvslabs.mdm.client.ui.events.ChangeTabEvent;
import com.totvslabs.mdm.client.ui.events.MDMConnectionChangedDispatcher;
import com.totvslabs.mdm.client.ui.events.MDMConnectionChangedEvent;
import com.totvslabs.mdm.client.ui.events.MDMConnectionChangedEvent.ConnectionTypeEnum;
import com.totvslabs.mdm.client.util.PersistenceEngine;
import com.totvslabs.mdm.restclient.MDMRestAuthentication;
import com.totvslabs.mdm.restclient.MDMRestConnectionFactory;

public class MDMDatabaseConnection extends PanelAbstract implements ActionListener {
	private static final long serialVersionUID = 1L;

	private JLabel labelMDMConnectionName;
	private JComboBox<String> comboMDMConnectionName;
	private DefaultComboBoxModel<String> comboBoxModelProfiles = new DefaultComboBoxModel<String>();

	private JLabel labelMDMServerURL;
	private JTextField textMDMServerURL;

	private JLabel labelMDMTenantId;
	private JTextField textMDMTenantId;

	private JLabel labelMDMDatasourceId;
	private JTextField textMDMDatasourceId;
	
	private JLabel labelMDMConsumerId;
	private JTextField textMDMConsumerId;

	private JLabel labelMDMUserName;
	private JTextField textMDMUserName;

	private JLabel labelMDMPassword;
	private JTextField textMDMPassword;

	private JButton buttonSaveFDConnection;
	private JButton buttonDeleteFDConnection;
	private JButton buttonConnectDisconnect;
	
	private JLabel labelDataType = new JLabel("Data Type: ");
	private ButtonGroup radioButtonDataType = new ButtonGroup();
	private JRadioButton radioFile = new JRadioButton("File");
	private JRadioButton radioDB = new JRadioButton("Database");

	public MDMDatabaseConnection(){
		super(1, 21, " MDM Database Parameters");

		this.labelMDMConnectionName = new JLabel("Connection Profile: ");
		this.comboMDMConnectionName = new JComboBox<String>(comboBoxModelProfiles);
		this.labelMDMServerURL = new JLabel("Server URL: ");
		this.textMDMServerURL = new JTextField("https://totvslabs.fluigdata.com:8443/", 20);
		this.labelMDMTenantId = new JLabel("Subdomain: ");
		this.textMDMTenantId = new JTextField("totvslabs");
		this.labelMDMDatasourceId = new JLabel("Datasource ID: ");
		this.textMDMDatasourceId = new JTextField("0b672ec08cbc11e5991b0242ac110002");
		this.labelMDMConsumerId = new JLabel("Consumer ID: ");
		this.textMDMConsumerId = new JTextField("819f0980819211e5991b0242ac110002");
		this.labelMDMUserName = new JLabel("User: ");
		this.textMDMUserName = new JTextField("admin@totvslabs.com", 20);
		this.labelMDMPassword = new JLabel("Password: ");
		this.textMDMPassword = new JPasswordField("Foobar1!", 20);
		this.buttonSaveFDConnection = new JButton("Save");
		this.buttonDeleteFDConnection = new JButton("Delete");
		this.buttonConnectDisconnect = new JButton("Connect!");

		this.radioButtonDataType.add(this.radioFile);
		this.radioButtonDataType.add(this.radioDB);

		this.radioFile.setEnabled(false);
		this.radioDB.setEnabled(false);

		this.radioFile.addActionListener(this);
		this.radioDB.addActionListener(this);

		this.comboMDMConnectionName.setEditable(true);

		this.initializeLayout();
		this.loadDefaultData();
	}

	public void initializeLayout() {
		this.add(this.labelMDMConnectionName);
		this.add(this.comboMDMConnectionName);
		this.add(this.labelMDMServerURL);
		this.add(this.textMDMServerURL);
		this.add(this.labelMDMTenantId);
		this.add(this.textMDMTenantId);
		this.add(this.labelMDMDatasourceId);
		this.add(this.textMDMDatasourceId);
		this.add(this.labelMDMConsumerId);
		this.add(this.textMDMConsumerId);
		this.add(this.labelMDMUserName);
		this.add(this.textMDMUserName);
		this.add(this.labelMDMPassword);
		this.add(this.textMDMPassword);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(this.buttonSaveFDConnection, BorderLayout.WEST);
		panel.add(this.buttonConnectDisconnect, BorderLayout.CENTER);
		panel.add(this.buttonDeleteFDConnection, BorderLayout.EAST);
		this.add(new JLabel());
		this.add(panel);

		this.add(new JLabel());
		this.add(this.labelDataType);
		this.add(this.radioFile);
		this.add(this.radioDB);

		this.comboMDMConnectionName.addActionListener(new ComboBoxStateChangeProfile());
		this.buttonSaveFDConnection.addActionListener(new SaveClick());
		this.buttonDeleteFDConnection.addActionListener(new DeleteClick());
		this.buttonConnectDisconnect.addActionListener(new ConnectClick());
	}

	class ConnectClick implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				MDMRestAuthentication.getInstance(textMDMServerURL.getText(), textMDMTenantId.getText(), textMDMDatasourceId.getText(), textMDMUserName.getText(), textMDMPassword.getText());
				MDMRestConnectionFactory.getConnection(textMDMServerURL.getText());

				radioFile.setEnabled(true);
				radioDB.setEnabled(true);

				MDMConnectionChangedEvent event = new MDMConnectionChangedEvent();
				event.setActualConnection((StoredFluigDataProfileVO) getAllData());
				event.setTypeEnum(ConnectionTypeEnum.CONNECTED);
				MDMConnectionChangedDispatcher.getInstance().fireMDMConnectionChangedEvent(event);
			}
			catch(RuntimeException e1) {e1.printStackTrace();
				JOptionPane.showMessageDialog(null, "Happened a problem to establish the connection, please see the log and try again later." + e1.getMessage());
			}
		}
	}

	class ComboBoxStateChangeProfile implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			StoredAbstractVO actualRecord = PersistenceEngine.getInstance().getByName((String) comboBoxModelProfiles.getSelectedItem(), StoredFluigDataProfileVO.class);
			loadAllData(actualRecord);
		}
	}

	public JTextField getTextMDMServerURL() {
		return textMDMServerURL;
	}

	public JTextField getTextMDMUserName() {
		return textMDMUserName;
	}

	public JTextField getTextMDMPassword() {
		return textMDMPassword;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(this.radioDB.getText())) {
			ChangeTabDispatcher.getInstance().fireChangeTabEvent(new ChangeTabEvent(1));			
		}
		else {
			ChangeTabDispatcher.getInstance().fireChangeTabEvent(new ChangeTabEvent(2));
		}
	}

	public boolean isDatabaseData() {
		if(this.radioDB.isSelected()) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public StoredAbstractVO getAllData() {
		StoredFluigDataProfileVO instance = new StoredFluigDataProfileVO();
		instance.setProfileName((String) this.comboMDMConnectionName.getSelectedItem());
		instance.setServerURL(this.textMDMServerURL.getText());
		instance.setDomain(this.textMDMTenantId.getText());
		instance.setDatasourceID(this.textMDMDatasourceId.getText());
		instance.setConsumerID(this.textMDMConsumerId.getText());
		instance.setUsername(this.textMDMUserName.getText());
		instance.setPassword(this.textMDMPassword.getText());

		return instance;
	}

	@Override
	public void loadAllData(StoredAbstractVO originalInstance) {
		if(originalInstance == null) {
			originalInstance = new StoredFluigDataProfileVO();
		}

		StoredFluigDataProfileVO instance = (StoredFluigDataProfileVO) originalInstance;

//		this.comboMDMConnectionName.setSelectedItem(instance.getProfileName());
		this.textMDMServerURL.setText(instance.getServerURL());
		this.textMDMTenantId.setText(instance.getDomain());
		this.textMDMDatasourceId.setText(instance.getDatasourceID());
		this.textMDMConsumerId.setText(instance.getConsumerID());
		this.textMDMUserName.setText(instance.getUsername());
		this.textMDMPassword.setText(instance.getPassword());
	}

	@Override
	public void loadDefaultData() {
		List<StoredAbstractVO> data = PersistenceEngine.getInstance().findAll(StoredFluigDataProfileVO.class);

		this.comboBoxModelProfiles.removeAllElements();

		if(data != null) {
			for (StoredAbstractVO storedAbstractVO : data) {
				this.comboBoxModelProfiles.addElement(storedAbstractVO.getName());
			}
		}
	}
}
