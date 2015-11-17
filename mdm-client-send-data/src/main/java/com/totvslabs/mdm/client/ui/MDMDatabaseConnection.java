package com.totvslabs.mdm.client.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.totvslabs.mdm.client.ui.events.ChangeTabDispatcher;
import com.totvslabs.mdm.client.ui.events.ChangeTabEvent;
import com.totvslabs.mdm.restclient.MDMRestAuthentication;
import com.totvslabs.mdm.restclient.MDMRestConnectionFactory;

public class MDMDatabaseConnection extends PanelAbstract implements ActionListener {
	private static final long serialVersionUID = 1L;

	private JLabel labelMDMServerURL;
	private JTextField textMDMServerURL;

	private JLabel labelMDMTenantId;
	private JTextField textMDMTenantId;

	private JLabel labelMDMDatasourceId;
	private JTextField textMDMDatasourceId;

	private JLabel labelMDMUserName;
	private JTextField textMDMUserName;

	private JLabel labelMDMPassword;
	private JTextField textMDMPassword;

	private JButton buttonConnectDisconnect;
	
	private JLabel labelDataType = new JLabel("Data Type: ");
	private ButtonGroup radioButtonDataType = new ButtonGroup();
	private JRadioButton radioFile = new JRadioButton("File");
	private JRadioButton radioDB = new JRadioButton("Database");

	public MDMDatabaseConnection(){
		super(1, 18, " MDM Database Parameters");

		this.labelMDMServerURL = new JLabel("Server URL: ");
		this.textMDMServerURL = new JTextField("https://totvslabs.fluigdata.com:8443/mdm/", 20);
		this.labelMDMTenantId = new JLabel("Subdomain: ");
		this.textMDMTenantId = new JTextField("totvslabs");
		this.labelMDMDatasourceId = new JLabel("Datasource ID: ");
		this.textMDMDatasourceId = new JTextField("def7838081a811e586c52ada15ab4e1c");
		this.labelMDMUserName = new JLabel("User: ");
		this.textMDMUserName = new JTextField("admin@totvslabs.com", 20);
		this.labelMDMPassword = new JLabel("Password: ");
		this.textMDMPassword = new JPasswordField("Foobar1!", 20);
		this.buttonConnectDisconnect = new JButton("Connect!");

		this.radioButtonDataType.add(this.radioFile);
		this.radioButtonDataType.add(this.radioDB);

		this.radioFile.setEnabled(false);
		this.radioDB.setEnabled(false);
		
		this.radioFile.addActionListener(this);
		this.radioDB.addActionListener(this);

		this.initializeLayout();
	}

	public void initializeLayout() {
		this.add(this.labelMDMServerURL);
		this.add(this.textMDMServerURL);
		this.add(this.labelMDMTenantId);
		this.add(this.textMDMTenantId);
		this.add(this.labelMDMDatasourceId);
		this.add(this.textMDMDatasourceId);
		this.add(this.labelMDMUserName);
		this.add(this.textMDMUserName);
		this.add(this.labelMDMPassword);
		this.add(this.textMDMPassword);

		this.add(new JLabel());
		this.add(this.buttonConnectDisconnect);

		this.add(new JLabel());
		this.add(this.labelDataType);
		this.add(this.radioFile);
		this.add(this.radioDB);

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
			}
			catch(RuntimeException e1) {
				JOptionPane.showMessageDialog(null, "Happened a problem to establish the connection, please see the log and try again later." + e1.getMessage());
			}
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
}
