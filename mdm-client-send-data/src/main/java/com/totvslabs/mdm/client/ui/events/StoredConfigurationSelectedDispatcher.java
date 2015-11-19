package com.totvslabs.mdm.client.ui.events;

import java.util.ArrayList;
import java.util.List;

public class StoredConfigurationSelectedDispatcher {
	private static StoredConfigurationSelectedDispatcher instance;
	private List<StoredConfigurationSelectedListener> listeners = new ArrayList<StoredConfigurationSelectedListener>();

	private StoredConfigurationSelectedDispatcher() {
	}

	public void addStoredConfigurationSelectedListener(StoredConfigurationSelectedListener listener) {
		this.listeners.add(listener);
	}

	public void removeStoredConfigurationSelectedListener(StoredConfigurationSelectedListener listener) {
		this.listeners.remove(listener);
	}

	public void fireStoredConfigurationSelectedEvent(StoredConfigurationSelectedEvent event) {
		for (StoredConfigurationSelectedListener listener : listeners) {
			listener.onStoredConfigurationSelectedEvent(event);
		}
	}

	public static StoredConfigurationSelectedDispatcher getInstance() {
		if(instance == null) {
			instance = new StoredConfigurationSelectedDispatcher();
		}

		return instance;
	}
}
