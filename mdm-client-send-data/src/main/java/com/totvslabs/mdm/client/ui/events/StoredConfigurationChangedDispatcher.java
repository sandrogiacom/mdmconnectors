package com.totvslabs.mdm.client.ui.events;

import java.util.ArrayList;
import java.util.List;

public class StoredConfigurationChangedDispatcher {
	private static StoredConfigurationChangedDispatcher instance;
	private List<StoredConfigurationChangedListener> listeners = new ArrayList<StoredConfigurationChangedListener>();

	private StoredConfigurationChangedDispatcher() {
	}

	public void addStoredConfigurationChangedListener(StoredConfigurationChangedListener listener) {
		this.listeners.add(listener);
	}

	public void removeStoredConfigurationChangedListener(StoredConfigurationChangedListener listener) {
		this.listeners.remove(listener);
	}

	public void fireStoredConfigurationChangedEvent(StoredConfigurationChangedEvent event) {
		for (StoredConfigurationChangedListener listener : listeners) {
			listener.onStoredConfigurationChanged(event);
		}
	}

	public static StoredConfigurationChangedDispatcher getInstance() {
		if(instance == null) {
			instance = new StoredConfigurationChangedDispatcher();
		}

		return instance;
	}
}
