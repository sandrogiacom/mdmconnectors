package com.totvslabs.mdm.client.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.totvslabs.mdm.client.pojo.JDBCConnectionParameter;
import com.totvslabs.mdm.client.pojo.JDBCDatabaseVO;
import com.totvslabs.mdm.client.pojo.JDBCDriverTypeVO;
import com.totvslabs.mdm.client.pojoTSA.MasterConfigurationData;
import com.totvslabs.mdm.client.ui.events.JDBCConnectionStabilizedDispatcher;
import com.totvslabs.mdm.client.ui.events.JDBCConnectionStabilizedEvent;
import com.totvslabs.mdm.client.util.JDBCConnectionFactory;

public class JDBCDatabaseConnection extends PanelAbstract {
	private static final long serialVersionUID = 1L;

	private JLabel labelJDBCURL;
	private JTextField textJDBCURL;

	private JLabel labelJDBCUserName;
	private JTextField textJDBCUserName;

	private JLabel labelJDBCPassword;
	private JTextField textJDBCPassword;

	private JLabel labelDriver;
	private JComboBox<JDBCDriverTypeVO> comboDriver;

	private JButton buttonConnectDisconnect;

	public JDBCDatabaseConnection(){
		super(2, 10, " JDBC Database Parameters");

		this.labelJDBCURL = new JLabel("JDBC URL: ");
		this.textJDBCURL = new JTextField("jdbc:sqlserver://192.168.56.101:1433;DatabaseName=ems2cad1211", 20);
		this.labelJDBCUserName = new JLabel("User: ");
		this.textJDBCUserName = new JTextField("sa", 20);
		this.labelJDBCPassword = new JLabel("Password: ");
		this.textJDBCPassword = new JPasswordField("sa", 20);
		this.labelDriver = new JLabel("Driver: ");
		this.comboDriver = new JComboBox<JDBCDriverTypeVO>();
		this.buttonConnectDisconnect = new JButton("Connect!");

		this.comboDriver.addItem(new JDBCDriverTypeVO("SQL Server"));
		this.comboDriver.addItem(new JDBCDriverTypeVO("Progress"));
		this.comboDriver.addItem(new JDBCDriverTypeVO("Oracle"));
		this.comboDriver.addItem(new JDBCDriverTypeVO("DB2"));
		this.comboDriver.addItem(new JDBCDriverTypeVO("Informix"));

		this.initializeLayout();
	}

	public void initializeLayout() {
		this.add(this.labelDriver);
		this.add(this.comboDriver);

		this.add(this.labelJDBCURL);
		this.add(this.textJDBCURL, 2, true, 1, 2);
		this.add(this.labelJDBCUserName);
		this.add(this.textJDBCUserName);
		this.add(this.labelJDBCPassword);
		this.add(this.textJDBCPassword);

		this.add(new JLabel());
		this.add(this.buttonConnectDisconnect);

		this.buttonConnectDisconnect.addActionListener(new ConnectClick());

		this.comboDriver.addItemListener(new ComboBoxStateChangeDriver());
	}

	class ConnectClick implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
        	JDBCDatabaseVO database = JDBCConnectionFactory.loadFisicModelTables(textJDBCURL.getText(), textJDBCUserName.getText(), textJDBCPassword.getText());

        	if(database == null) {
        		JOptionPane.showMessageDialog(null, "An error occurred while establishing the connection, verify the error message.", "Connection Error", JOptionPane.ERROR_MESSAGE);
        		return;
        	}

        	JDBCConnectionParameter param = new JDBCConnectionParameter(textJDBCURL.getText(), textJDBCUserName.getText(), textJDBCPassword.getText());
			JDBCConnectionStabilizedEvent event = new JDBCConnectionStabilizedEvent(param, database.getTables());
			JDBCConnectionStabilizedDispatcher.getInstance().fireJDBCConnectionStabilizedEvent(event);
		}
	}

	@Override
	public void fillComponents(MasterConfigurationData masterConfigurationData) {
		if(masterConfigurationData != null) {
		}
	}

	@Override
	public void fillData(MasterConfigurationData masterConfigurationData) {
		if(masterConfigurationData != null) {
			masterConfigurationData.setTechnicalInformationTSAServer(this.textJDBCURL.getText());
			masterConfigurationData.setTechnicalInformationTSAUserName(this.textJDBCUserName.getText());
			masterConfigurationData.setTechnicalInformationTSAPassword(this.textJDBCPassword.getText());
		}
	}

	class ComboBoxStateChangeDriver implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent e) {
		}
	}
}
