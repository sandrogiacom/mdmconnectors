package com.totvslabs.mdm.client.ui.events;

import java.util.ArrayList;
import java.util.List;

public class JDBCTableSelectedDispatcher {
	private static JDBCTableSelectedDispatcher instance;
	private List<JDBCTableSelectedListener> listeners = new ArrayList<JDBCTableSelectedListener>();

	private JDBCTableSelectedDispatcher() {
	}

	public void addJDBCTableSelectedListener(JDBCTableSelectedListener listener) {
		this.listeners.add(listener);
	}

	public void removeJDBCTableSelectedListener(JDBCTableSelectedListener listener) {
		this.listeners.remove(listener);
	}

	public void fireJDBCTableSelectedEvent(JDBCTableSelectedEvent event) {
		for (JDBCTableSelectedListener jdbcTableSelectedListener : listeners) {
			jdbcTableSelectedListener.onJDBCTableSelectedEvent(event);
		}
	}

	public static JDBCTableSelectedDispatcher getInstance() {
		if(instance == null) {
			instance = new JDBCTableSelectedDispatcher();
		}

		return instance;
	}
}
