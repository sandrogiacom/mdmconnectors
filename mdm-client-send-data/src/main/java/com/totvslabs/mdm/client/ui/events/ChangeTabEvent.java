package com.totvslabs.mdm.client.ui.events;

public class ChangeTabEvent {
	private Integer tabToSelect;

	public ChangeTabEvent(Integer tabToSelect) {
		super();
		this.tabToSelect = tabToSelect;
	}

	public Integer getTabToSelect() {
		return tabToSelect;
	}

	public void setTabToSelect(Integer tabToSelect) {
		this.tabToSelect = tabToSelect;
	}
}
