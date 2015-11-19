package com.totvslabs.mdm.client.ui.events;

import com.totvslabs.mdm.client.pojo.StoredConfigurationVO;

public class StoredConfigurationSelectedEvent {
	private StoredConfigurationVO configurationVO;

	public StoredConfigurationSelectedEvent(StoredConfigurationVO configurationVO) {
		super();
		this.configurationVO = configurationVO;
	}
	public StoredConfigurationVO getConfigurationVO() {
		return configurationVO;
	}
	public void setConfigurationVO(StoredConfigurationVO configurationVO) {
		this.configurationVO = configurationVO;
	}
}
