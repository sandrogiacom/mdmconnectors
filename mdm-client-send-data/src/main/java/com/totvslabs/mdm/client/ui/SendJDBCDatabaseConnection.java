package com.totvslabs.mdm.client.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.totvslabs.mdm.client.pojo.JDBCConnectionParameter;
import com.totvslabs.mdm.client.pojo.JDBCDatabaseVO;
import com.totvslabs.mdm.client.pojo.JDBCDriverTypeVO;
import com.totvslabs.mdm.client.ui.events.DataLoadedDispatcher;
import com.totvslabs.mdm.client.ui.events.DataLoadedEvent;
import com.totvslabs.mdm.client.util.JDBCConnectionFactory;

public class SendJDBCDatabaseConnection extends PanelAbstract {
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

	private Map<String, JDBCDriverTypeVO> jdbcDrivers = new HashMap<String, JDBCDriverTypeVO>();
	private JDBCDriverTypeVO driver;

	public SendJDBCDatabaseConnection(){
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

		this.jdbcDrivers.put("sqlserver", new JDBCDriverTypeVO("sqlServer", "SQL Server", "", "jdbc:sqlserver://192.168.56.101:1433;DatabaseName=ems2cad1211", "sa", "sa"));
		this.jdbcDrivers.put("progress", new JDBCDriverTypeVO("progress", "Progress", "com.ddtek.jdbc.openedge.OpenEdgeDriver", "jdbc:datadirect:openedge://192.168.56.101:2121;databaseName=marelli;defaultSchema=pub", "sysprogress", "sysprogress"));
//		this.jdbcDrivers.put("oracle", new JDBCDriverTypeVO("oracle", "Oracle", "", "", "", ""));
//		this.jdbcDrivers.put("db2", new JDBCDriverTypeVO("db2", "DB2", "", "", "", ""));
//		this.jdbcDrivers.put("informix", new JDBCDriverTypeVO("informix", "Informix", "", "", "", ""));

		Collection<JDBCDriverTypeVO> values = jdbcDrivers.values();

		for (JDBCDriverTypeVO jdbcDriverTypeVO : values) {
			this.comboDriver.addItem(jdbcDriverTypeVO);
		}

		this.comboDriver.setSelectedIndex(-1);
		this.textJDBCURL.setText("");
		this.textJDBCUserName.setText("");
		this.textJDBCPassword.setText("");

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

        	JDBCConnectionParameter param = new JDBCConnectionParameter(driver.getDriverClass(), textJDBCURL.getText(), textJDBCUserName.getText(), textJDBCPassword.getText());
			DataLoadedEvent event = new DataLoadedEvent(param, database.getTables());
			DataLoadedDispatcher.getInstance().fireJDBCConnectionStabilizedEvent(event);
		}
	}

	class ComboBoxStateChangeDriver implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent e) {
			JDBCDriverTypeVO driverSelected = (JDBCDriverTypeVO) e.getItem();

			textJDBCPassword.setText(driverSelected.getPasswordSample());
			textJDBCURL.setText(driverSelected.getUrlSample());
			textJDBCUserName.setText(driverSelected.getUserSample());

			driver = driverSelected;
		}
	}
}

