package com.totvslabs.mdm.client.ui.events;

import com.totvslabs.mdm.client.pojo.MDMDatasourceBO;

public class DatasourceChangedEvent {
	private MDMDatasourceBO actualValue;

	public DatasourceChangedEvent(MDMDatasourceBO actualValue) {
		super();
		this.actualValue = actualValue;
	}

	public MDMDatasourceBO getActualValue() {
		return actualValue;
	}

	public void setActualValue(MDMDatasourceBO actualValue) {
		this.actualValue = actualValue;
	}
}
