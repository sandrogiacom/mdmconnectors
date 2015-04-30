package com.totvslabs.mdm.client.ui;

import java.awt.BorderLayout;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.totvslabs.mdm.client.ui.events.LogManagerEvent;
import com.totvslabs.mdm.client.ui.events.LogManagerListener;

public class ProcessLog extends JPanel implements LogManagerListener {
	private static final long serialVersionUID = 1L;

	private JScrollPane scrollPaneLog;
	private JTextArea textAreaLog;

	public ProcessLog(){
		this.setLayout(new BorderLayout());

		this.textAreaLog = new JTextArea();
		this.scrollPaneLog = new JScrollPane(this.textAreaLog);

		this.initializeLayout();
	}

	public void initializeLayout() {
		this.add(this.scrollPaneLog, BorderLayout.CENTER);

		this.scrollPaneLog.setAutoscrolls(true);
		this.textAreaLog.setLineWrap(false);

		this.textAreaLog.setEditable(false);
	}

	@Override
	public void onLogAdded(LogManagerEvent event) {
		DateFormat df = new SimpleDateFormat();

		this.textAreaLog.append(df.format(event.getDate()) + " <> " + event.getMessage());
		this.textAreaLog.append("\n");
		this.textAreaLog.setCaretPosition(this.textAreaLog.getDocument().getLength());
	}
}
