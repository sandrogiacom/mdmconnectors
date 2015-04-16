package com.totvslabs.mdm.client.ui.events;

import java.util.ArrayList;
import java.util.List;

public class TenantChangedDispatcher {
	private static TenantChangedDispatcher instance;
	private List<TenantChangedListener> listeners = new ArrayList<TenantChangedListener>();

	private TenantChangedDispatcher() {
	}

	public void addTenantChangedListener(TenantChangedListener listener) {
		this.listeners.add(listener);
	}

	public void removeTenantChangedListener(TenantChangedListener listener) {
		this.listeners.remove(listener);
	}

	public void fireTenantChangedEvent(TenantChangedEvent event) {
		for (TenantChangedListener tenantChangedListener : listeners) {
			tenantChangedListener.onTenantChangedEvent(event);
		}
	}

	public static TenantChangedDispatcher getInstance() {
		if(instance == null) {
			instance = new TenantChangedDispatcher();
		}

		return instance;
	}
}
