package com.totvslabs.mdm.client.ui.events;

import java.util.ArrayList;
import java.util.List;

public class DataLoadedDispatcher {
	private static DataLoadedDispatcher instance;
	private List<DataLoadedListener> listeners = new ArrayList<DataLoadedListener>();

	private DataLoadedDispatcher() {
	}

	public void addJDBCConnectionStabilizedListener(DataLoadedListener listener) {
		this.listeners.add(listener);
	}

	public void removeJDBCConnectionStabilizedListener(DataLoadedListener listener) {
		this.listeners.remove(listener);
	}

	public void fireJDBCConnectionStabilizedEvent(DataLoadedEvent event) {
		for (DataLoadedListener jdbcConnectionStabilizedListener : listeners) {
			jdbcConnectionStabilizedListener.onDataLoadedEvent(event);
		}
	}

	public static DataLoadedDispatcher getInstance() {
		if(instance == null) {
			instance = new DataLoadedDispatcher();
		}

		return instance;
	}
}

