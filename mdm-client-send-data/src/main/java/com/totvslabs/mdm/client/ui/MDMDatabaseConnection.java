package com.totvslabs.mdm.client.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.totvslabs.mdm.client.pojo.MDMDatasourceBO;
import com.totvslabs.mdm.client.pojo.MDMTenantBO;
import com.totvslabs.mdm.client.pojoTSA.MasterConfigurationData;
import com.totvslabs.mdm.client.ui.events.DatasourceChangedDispatcher;
import com.totvslabs.mdm.client.ui.events.DatasourceChangedEvent;
import com.totvslabs.mdm.client.ui.events.TenantChangedDispatcher;
import com.totvslabs.mdm.client.ui.events.TenantChangedEvent;
import com.totvslabs.mdm.client.ui.events.TenantChangedListener;
import com.totvslabs.mdm.restclient.MDMRestConnection;
import com.totvslabs.mdm.restclient.MDMRestConnectionFactory;
import com.totvslabs.mdm.restclient.command.CommandListDatasource;
import com.totvslabs.mdm.restclient.command.CommandListTenant;
import com.totvslabs.mdm.restclient.vo.EnvelopeVO;
import com.totvslabs.mdm.restclient.vo.GenericVO;

public class MDMDatabaseConnection extends PanelAbstract implements TenantChangedListener {
	private static final long serialVersionUID = 1L;

	private JLabel labelMDMServerURL;
	private JTextField textMDMServerURL;

	private JLabel labelMDMUserName;
	private JTextField textMDMUserName;

	private JLabel labelMDMPassword;
	private JTextField textMDMPassword;

	private JLabel labelTenants;
	private JComboBox<MDMTenantBO> comboTenants;

	private JLabel labelDatasources;
	private JComboBox<MDMDatasourceBO> comboDatasources;

	private JButton buttonConnectDisconnect;

	public MDMDatabaseConnection(){
		super(1, 12, " MDM Database Parameters");

		this.labelMDMServerURL = new JLabel("Server URL: ");
		this.textMDMServerURL = new JTextField("https://app.fluigdata.com:443/mdm/", 20);
		this.labelMDMUserName = new JLabel("User: ");
		this.textMDMUserName = new JTextField("robson.poffo@totvs.com.br", 20);
		this.labelMDMPassword = new JLabel("Password: ");
		this.textMDMPassword = new JPasswordField("Totvs@123", 20);
		this.labelTenants = new JLabel("Tenants: ");
		this.comboTenants = new JComboBox<MDMTenantBO>();
		this.labelDatasources = new JLabel("Datasources: ");
		this.comboDatasources = new JComboBox<MDMDatasourceBO>();
		this.buttonConnectDisconnect = new JButton("Connect!");

		this.initializeLayout();
	}

	public void initializeLayout() {
		this.add(this.labelMDMServerURL);
		this.add(this.textMDMServerURL);
		this.add(this.labelMDMUserName);
		this.add(this.textMDMUserName);
		this.add(this.labelMDMPassword);
		this.add(this.textMDMPassword);

		this.add(new JLabel());
		this.add(this.buttonConnectDisconnect);

		this.add(this.labelTenants);
		this.add(this.comboTenants);

		this.add(this.labelDatasources);
		this.add(this.comboDatasources);

		this.buttonConnectDisconnect.addActionListener(new ConnectClick());

		this.comboDatasources.addItemListener(new ComboBoxStateChangeDatasource());
		this.comboTenants.addItemListener(new ComboBoxStateChangeTenant());

		TenantChangedDispatcher.getInstance().addTenantChangedListener(this);
	}

	class ConnectClick implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			MDMRestConnection connection = MDMRestConnectionFactory.getConnection(textMDMServerURL.getText());
			EnvelopeVO resultTenants = connection.executeCommand(new CommandListTenant());

			comboTenants.removeAllItems();
			comboDatasources.removeAllItems();

			List<GenericVO> hits = resultTenants.getHits();
			for (GenericVO genericVO : hits) {
				comboTenants.addItem(new MDMTenantBO(genericVO.get_mdmId(), genericVO.getName()));
			}
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
			masterConfigurationData.setTechnicalInformationTSAServer(this.textMDMServerURL.getText());
			masterConfigurationData.setTechnicalInformationTSAUserName(this.textMDMUserName.getText());
			masterConfigurationData.setTechnicalInformationTSAPassword(this.textMDMPassword.getText());
		}
	}

	class ComboBoxStateChangeTenant implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent e) {
			TenantChangedEvent event = new TenantChangedEvent();
			event.setActualValue((MDMTenantBO) comboTenants.getSelectedItem());
			
			TenantChangedDispatcher.getInstance().fireTenantChangedEvent(event);
		}
	}

	class ComboBoxStateChangeDatasource implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent e) {
			DatasourceChangedEvent event = new DatasourceChangedEvent((MDMDatasourceBO) comboDatasources.getSelectedItem());

			DatasourceChangedDispatcher.getInstance().fireDatasourceChangedEvent(event);
		}
	}

	@Override
	public void onTenantChangedEvent(TenantChangedEvent event) {
		comboDatasources.removeAllItems();

		if(event.getActualValue() != null) {
			MDMRestConnection connection = MDMRestConnectionFactory.getConnection(textMDMServerURL.getText());
			EnvelopeVO resultTenants = connection.executeCommand(new CommandListDatasource(event.getActualValue().getId()));
			
			List<GenericVO> hits = resultTenants.getHits();
			
			for (GenericVO genericVO : hits) {
				comboDatasources.addItem(new MDMDatasourceBO(genericVO.get_mdmTenantId(), genericVO.get_mdmDataSourceId(), genericVO.getName()));
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
}
