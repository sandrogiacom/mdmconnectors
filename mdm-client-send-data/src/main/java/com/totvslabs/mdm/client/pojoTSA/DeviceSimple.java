package com.totvslabs.mdm.client.pojoTSA;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DeviceSimple implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer deviceCode;
	private Boolean rep;
	private List<UserSimple> usersAvailable;
	private List<ResultSimulationData> resultSimulationData;

	public DeviceSimple(Integer deviceCode) {
		this(deviceCode, Boolean.FALSE);
	}

	public DeviceSimple(Integer deviceCode, Boolean rep) {
		super();
		this.deviceCode = deviceCode;
		this.usersAvailable = new ArrayList<UserSimple>();
		this.resultSimulationData = new ArrayList<ResultSimulationData>();
		this.rep = (rep == null ? Boolean.FALSE : rep);
	}

	public Integer getDeviceCode() {
		return deviceCode;
	}

	public void setDeviceCode(Integer deviceCode) {
		this.deviceCode = deviceCode;
	}

	public List<UserSimple> getUsersAvailable() {
		return usersAvailable;
	}

	public List<ResultSimulationData> getResultSimulationData() {
		return resultSimulationData;
	}

	public Boolean getRep() {
		return rep;
	}

	public void setRep(Boolean rep) {
		this.rep = rep;
	}
}
