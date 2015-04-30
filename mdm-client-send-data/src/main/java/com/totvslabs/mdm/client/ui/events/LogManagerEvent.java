package com.totvslabs.mdm.client.ui.events;

import java.util.Date;

public class LogManagerEvent {
	private String message;
	private Date date;

	public LogManagerEvent(String message) {
		this(message, new Date());
	}

	public LogManagerEvent(String message, Date date) {
		this.message = message;
		this.date = date;
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}
