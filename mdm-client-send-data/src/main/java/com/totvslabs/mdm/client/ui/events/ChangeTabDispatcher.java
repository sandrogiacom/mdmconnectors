package com.totvslabs.mdm.client.ui.events;

import java.util.ArrayList;
import java.util.List;

public class ChangeTabDispatcher {
	private static ChangeTabDispatcher instance;
	private List<ChangeTabListener> listeners = new ArrayList<ChangeTabListener>();

	private ChangeTabDispatcher() {
	}

	public void addChangeTabListener(ChangeTabListener listener) {
		this.listeners.add(listener);
	}

	public void removeChangeTabListener(ChangeTabListener listener) {
		this.listeners.remove(listener);
	}

	public void fireChangeTabEvent(ChangeTabEvent event) {
		for (ChangeTabListener changeTabListener : listeners) {
			changeTabListener.onChangeTabListener(event);
		}
	}

	public static ChangeTabDispatcher getInstance() {
		if(instance == null) {
			instance = new ChangeTabDispatcher();
		}

		return instance;
	}
}
