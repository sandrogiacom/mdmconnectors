package com.totvslabs.mdm.client.ui.events;

import java.util.ArrayList;
import java.util.List;

public class JDBCConnectionStabilizedDispatcher {
	private static JDBCConnectionStabilizedDispatcher instance;
	private List<JDBCConnectionStabilizedListener> listeners = new ArrayList<JDBCConnectionStabilizedListener>();

	private JDBCConnectionStabilizedDispatcher() {
	}

	public void addJDBCConnectionStabilizedListener(JDBCConnectionStabilizedListener listener) {
		this.listeners.add(listener);
	}

	public void removeJDBCConnectionStabilizedListener(JDBCConnectionStabilizedListener listener) {
		this.listeners.remove(listener);
	}

	public void fireJDBCConnectionStabilizedEvent(JDBCConnectionStabilizedEvent event) {
		for (JDBCConnectionStabilizedListener jdbcConnectionStabilizedListener : listeners) {
			jdbcConnectionStabilizedListener.onJDBCConnectionStabilizedEvent(event);
		}
	}

	public static JDBCConnectionStabilizedDispatcher getInstance() {
		if(instance == null) {
			instance = new JDBCConnectionStabilizedDispatcher();
		}

		return instance;
	}
}
