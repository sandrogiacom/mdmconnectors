package com.totvslabs.mdm.client.ui.events;

import java.util.ArrayList;
import java.util.List;

public class MDMConnectionChangedDispatcher {
	private static MDMConnectionChangedDispatcher instance;
	private List<MDMConnectionChangedListener> listeners = new ArrayList<MDMConnectionChangedListener>();

	private MDMConnectionChangedDispatcher() {
	}

	public void addMDMConnectionChangedListener(MDMConnectionChangedListener listener) {
		this.listeners.add(listener);
	}

	public void removeMDMConnectionChangedListener(MDMConnectionChangedListener listener) {
		this.listeners.remove(listener);
	}

	public void fireMDMConnectionChangedEvent(MDMConnectionChangedEvent event) {
		for (MDMConnectionChangedListener listener : listeners) {
			listener.onMDMConnectionChangedListener(event);
		}
	}

	public static MDMConnectionChangedDispatcher getInstance() {
		if(instance == null) {
			instance = new MDMConnectionChangedDispatcher();
		}

		return instance;
	}
}
