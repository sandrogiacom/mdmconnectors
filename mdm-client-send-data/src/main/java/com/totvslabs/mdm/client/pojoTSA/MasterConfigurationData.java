package com.totvslabs.mdm.client.pojoTSA;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class MasterConfigurationData implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	private MasterConfigurationDataStageEnum configurationStage = MasterConfigurationDataStageEnum.UPDATE_DATA;

	private InstanceTypeEnum instanceTypeEnum;
	private String instanceIpAddress;
	private Integer deviceActiveQuantity;
	private MarkDeviceTypeEnum markDeviceTypeEnum;
	private Date markInitialDate;
	private Date markEndDate;
	private MarkHourTypeEnum markHourTypeEnum;
	private Date markInitialTime;
	private Date markEndTime;

	private List<SlaveIP> technicalInformationSlaves;
	private String technicalInformationThreadsNumber;
	private String technicalInformationSecondsExecution;
	private String technicalInformationSecondsInterval;
	private String technicalInformationTSAServer;
	private String technicalInformationTSAPort;
	private String technicalInformationTSAUserName;
	private String technicalInformationTSAPassword;

	public InstanceTypeEnum getInstanceTypeEnum() {
		return instanceTypeEnum;
	}
	public void setInstanceTypeEnum(InstanceTypeEnum instanceTypeEnum) {
		this.instanceTypeEnum = instanceTypeEnum;
	}
	public Integer getDeviceActiveQuantity() {
		return deviceActiveQuantity;
	}
	public void setDeviceActiveQuantity(Integer deviceActiveQuantity) {
		this.deviceActiveQuantity = deviceActiveQuantity;
	}
	public MarkDeviceTypeEnum getMarkDeviceTypeEnum() {
		return markDeviceTypeEnum;
	}
	public void setMarkDeviceTypeEnum(MarkDeviceTypeEnum markDeviceTypeEnum) {
		this.markDeviceTypeEnum = markDeviceTypeEnum;
	}
	public Date getMarkInitialDate() {
		return markInitialDate;
	}
	public void setMarkInitialDate(Date markInitialDate) {
		this.markInitialDate = markInitialDate;
	}
	public Date getMarkEndDate() {
		return markEndDate;
	}
	public void setMarkEndDate(Date markEndDate) {
		this.markEndDate = markEndDate;
	}
	public MarkHourTypeEnum getMarkHourTypeEnum() {
		return markHourTypeEnum;
	}
	public void setMarkHourTypeEnum(MarkHourTypeEnum markHourTypeEnum) {
		this.markHourTypeEnum = markHourTypeEnum;
	}
	public Date getMarkInitialTime() {
		return markInitialTime;
	}
	public void setMarkInitialTime(Date markInitialTime) {
		this.markInitialTime = markInitialTime;
	}
	public Date getMarkEndTime() {
		return markEndTime;
	}
	public void setMarkEndTime(Date markEndTime) {
		this.markEndTime = markEndTime;
	}
	public List<SlaveIP> getTechnicalInformationSlaves() {
		return technicalInformationSlaves;
	}
	public void setTechnicalInformationSlaves(List<SlaveIP> technicalInformationSlaves) {
		this.technicalInformationSlaves = technicalInformationSlaves;
	}
	public String getTechnicalInformationThreadsNumber() {
		return technicalInformationThreadsNumber;
	}
	public void setTechnicalInformationThreadsNumber(String technicalInformationThreadsNumber) {
		this.technicalInformationThreadsNumber = technicalInformationThreadsNumber;
	}
	public String getTechnicalInformationSecondsExecution() {
		return technicalInformationSecondsExecution;
	}
	public void setTechnicalInformationSecondsExecution(String technicalInformationSecondsExecution) {
		this.technicalInformationSecondsExecution = technicalInformationSecondsExecution;
	}
	public String getTechnicalInformationTSAServer() {
		return technicalInformationTSAServer;
	}
	public void setTechnicalInformationTSAServer(String technicalInformationTSAServer) {
		this.technicalInformationTSAServer = technicalInformationTSAServer;
	}
	public String getTechnicalInformationTSAPort() {
		return technicalInformationTSAPort;
	}
	public void setTechnicalInformationTSAPort(String technicalInformationTSAPort) {
		this.technicalInformationTSAPort = technicalInformationTSAPort;
	}
	public MasterConfigurationDataStageEnum getConfigurationStage() {
		return configurationStage;
	}
	public void setConfigurationStage(MasterConfigurationDataStageEnum configurationStage) {
		this.configurationStage = configurationStage;
	}
	public String getTechnicalInformationTSAUserName() {
		return technicalInformationTSAUserName;
	}
	public void setTechnicalInformationTSAUserName(String technicalInformationTSAUserName) {
		this.technicalInformationTSAUserName = technicalInformationTSAUserName;
	}
	public String getTechnicalInformationTSAPassword() {
		return technicalInformationTSAPassword;
	}
	public void setTechnicalInformationTSAPassword(String technicalInformationTSAPassword) {
		this.technicalInformationTSAPassword = technicalInformationTSAPassword;
	}
	public String getInstanceIpAddress() {
		return instanceIpAddress;
	}
	public void setInstanceIpAddress(String instanceIpAddress) {
		this.instanceIpAddress = instanceIpAddress;
	}
	public String getTechnicalInformationSecondsInterval() {
		return technicalInformationSecondsInterval;
	}
	public void setTechnicalInformationSecondsInterval(String technicalInformationSecondsInterval) {
		this.technicalInformationSecondsInterval = technicalInformationSecondsInterval;
	}
	@Override
	public String toString() {
		return "MasterConfigurationData [configurationStage=" + configurationStage + ", instanceTypeEnum=" + instanceTypeEnum + ", instanceIpAddress=" + instanceIpAddress + ", deviceActiveQuantity="
				+ deviceActiveQuantity + ", markDeviceTypeEnum=" + markDeviceTypeEnum + ", markInitialDate=" + markInitialDate + ", markEndDate=" + markEndDate + ", markHourTypeEnum="
				+ markHourTypeEnum + ", markInitialTime=" + markInitialTime + ", markEndTime=" + markEndTime + ", technicalInformationSlaves=" + technicalInformationSlaves
				+ ", technicalInformationThreadsNumber=" + technicalInformationThreadsNumber + ", technicalInformationSecondsExecution=" + technicalInformationSecondsExecution
				+ ", technicalInformationSecondsInterval=" + technicalInformationSecondsInterval + ", technicalInformationTSAServer=" + technicalInformationTSAServer
				+ ", technicalInformationTSAPort=" + technicalInformationTSAPort + ", technicalInformationTSAUserName=" + technicalInformationTSAUserName + ", technicalInformationTSAPassword="
				+ technicalInformationTSAPassword + "]";
	}
}
