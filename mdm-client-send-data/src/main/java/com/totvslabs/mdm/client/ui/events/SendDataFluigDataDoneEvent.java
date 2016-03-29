package com.totvslabs.mdm.client.ui.events;

import java.util.Date;

import com.totvslabs.mdm.client.util.ProcessTypeEnum;

public class SendDataFluigDataDoneEvent {
	private Date when;
	private String jsonData;
	private ProcessTypeEnum processTypeEnum;
	private ProcessStatusEnum processStatusEnum;

	public SendDataFluigDataDoneEvent(ProcessTypeEnum processTypeEnum, ProcessStatusEnum status, String string) {
		super();
		this.when = new Date();
		this.jsonData = string;
		this.processTypeEnum = processTypeEnum;
		this.processStatusEnum = status;
	}

	public ProcessStatusEnum getProcessStatusEnum() {
		return processStatusEnum;
	}

	public void setProcessStatusEnum(ProcessStatusEnum processStatusEnum) {
		this.processStatusEnum = processStatusEnum;
	}

	public ProcessTypeEnum getProcessTypeEnum() {
		return processTypeEnum;
	}

	public void setProcessTypeEnum(ProcessTypeEnum processTypeEnum) {
		this.processTypeEnum = processTypeEnum;
	}

	public Date getWhen() {
		return when;
	}

	public void setWhen(Date when) {
		this.when = when;
	}

	public String getJsonData() {
		return jsonData;
	}

	public void setJsonData(String jsonData) {
		this.jsonData = jsonData;
	}
}
