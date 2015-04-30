package com.totvslabs.mdm.client.ui.events;

import java.util.ArrayList;
import java.util.List;

public class LogManagerDispatcher {
	private static LogManagerDispatcher instance;
	private List<LogManagerListener> listeners = new ArrayList<LogManagerListener>();

	private LogManagerDispatcher() {
	}

	public void addLogManagerListener(LogManagerListener listener) {
		this.listeners.add(listener);
	}

	public void removeLogManagerListener(LogManagerListener listener) {
		this.listeners.remove(listener);
	}

	public void fireLogManagerEvent(LogManagerEvent event) {
		for (LogManagerListener logManagerListener : listeners) {
			logManagerListener.onLogAdded(event);
		}
	}

	public static LogManagerDispatcher getInstance() {
		if(instance == null) {
			instance = new LogManagerDispatcher();
		}

		return instance;
	}

	public void register(String string) {
		LogManagerEvent event = new LogManagerEvent(string);

		LogManagerDispatcher.getInstance().fireLogManagerEvent(event);
	}
}
