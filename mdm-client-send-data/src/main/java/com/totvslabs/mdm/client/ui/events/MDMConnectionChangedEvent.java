package com.totvslabs.mdm.client.ui.events;

import com.totvslabs.mdm.client.pojo.StoredFluigDataProfileVO;

public class MDMConnectionChangedEvent {
	private ConnectionTypeEnum typeEnum;
	private StoredFluigDataProfileVO actualConnection;

	public ConnectionTypeEnum getTypeEnum() {
		return typeEnum;
	}
	public void setTypeEnum(ConnectionTypeEnum typeEnum) {
		this.typeEnum = typeEnum;
	}
	public StoredFluigDataProfileVO getActualConnection() {
		return actualConnection;
	}
	public void setActualConnection(StoredFluigDataProfileVO actualConnection) {
		this.actualConnection = actualConnection;
	}

	public enum ConnectionTypeEnum {
		CONNECTED, DISCONNECTED;
	}
}
