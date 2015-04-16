package com.totvslabs.mdm.client.ui.events;

import java.util.ArrayList;
import java.util.List;

public class DatasourceChangedDispatcher {
	private static DatasourceChangedDispatcher instance;
	private List<DatasourceChangedListener> listeners = new ArrayList<DatasourceChangedListener>();

	private DatasourceChangedDispatcher() {
	}

	public void addDatasourceChangedListener(DatasourceChangedListener listener) {
		this.listeners.add(listener);
	}

	public void removeDatasourceChangedListener(DatasourceChangedListener listener) {
		this.listeners.remove(listener);
	}

	public void fireDatasourceChangedEvent(DatasourceChangedEvent event) {
		for (DatasourceChangedListener datasourceChangedListener : listeners) {
			datasourceChangedListener.onDatasourceChangedEvent(event);
		}
	}

	public static DatasourceChangedDispatcher getInstance() {
		if(instance == null) {
			instance = new DatasourceChangedDispatcher();
		}

		return instance;
	}
}
