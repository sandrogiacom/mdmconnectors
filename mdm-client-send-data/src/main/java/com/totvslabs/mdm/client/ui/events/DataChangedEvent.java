package com.totvslabs.mdm.client.ui.events;

import java.awt.Component;

import com.totvslabs.mdm.client.ui.PanelAbstract;

public class DataChangedEvent {
	private Component fieldChanged;
	private PanelAbstract parentPanel;

	public DataChangedEvent(Component fieldChanged, PanelAbstract parentPanel) {
		this.fieldChanged = fieldChanged;
		this.parentPanel = parentPanel;
	}

	public Component getFieldChanged() {
		return fieldChanged;
	}
	public void setFieldChanged(Component fieldChanged) {
		this.fieldChanged = fieldChanged;
	}
	public PanelAbstract getParentPanel() {
		return parentPanel;
	}
	public void setParentPanel(PanelAbstract parentPanel) {
		this.parentPanel = parentPanel;
	}
}
