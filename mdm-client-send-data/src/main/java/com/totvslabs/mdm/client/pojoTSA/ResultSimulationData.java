package com.totvslabs.mdm.client.pojoTSA;

import java.io.Serializable;
import java.util.Date;

public class ResultSimulationData implements Serializable {
	private static final long serialVersionUID = 1L;
	private Date ocorrenceDate;
	private String cardCode;
	private Date dateMark;
	private String message;
	private Long totalTime;
	private Integer deviceCode;
	private String slaveIp;
	private ResultSimulationType type;

	public ResultSimulationData(Date ocorrenceDate, String cardCode, Date dateMark, String message, Long totalTime, String slaveIp, Integer deviceCode, ResultSimulationType type) {
		super();
		this.ocorrenceDate = ocorrenceDate;
		this.cardCode = cardCode;
		this.dateMark = dateMark;
		this.message = message;
		this.totalTime = totalTime;
		this.slaveIp = slaveIp;
		this.deviceCode = deviceCode;
		this.type = type;
	}

	public Date getOcorrenceDate() {
		return ocorrenceDate;
	}

	public void setOcorrenceDate(Date ocorrenceDate) {
		this.ocorrenceDate = ocorrenceDate;
	}

	public String getCardCode() {
		return cardCode;
	}

	public void setCardCode(String cardCode) {
		this.cardCode = cardCode;
	}

	public Date getDateMark() {
		return dateMark;
	}

	public void setDateMark(Date dateMark) {
		this.dateMark = dateMark;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Long getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(Long totalTime) {
		this.totalTime = totalTime;
	}

	public Integer getDeviceCode() {
		return deviceCode;
	}

	public void setDeviceCode(Integer deviceCode) {
		this.deviceCode = deviceCode;
	}

	public String getSlaveIp() {
		return slaveIp;
	}

	public void setSlaveIp(String slaveIp) {
		this.slaveIp = slaveIp;
	}

	public ResultSimulationType getType() {
		return type;
	}

	public void setType(ResultSimulationType type) {
		this.type = type;
	}

	public enum ResultSimulationType {
		ERROR, MESSAGE, MARK_OK, END_PROCESS, BEGIN_PROCESS, INTERRUPTED_PROCESS;
	}
}
