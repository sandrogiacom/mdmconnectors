package com.totvslabs.mdm.client.ui.events;

import com.totvslabs.mdm.client.pojo.MDMTenantBO;

public class TenantChangedEvent {
	private MDMTenantBO actualValue;

	public MDMTenantBO getActualValue() {
		return actualValue;
	}

	public void setActualValue(MDMTenantBO actualValue) {
		this.actualValue = actualValue;
	}
}
