package com.totvslabs.mdm.client.pojoTSA;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SlaveIP implements Serializable {
	private static final long serialVersionUID = 1L;

	private String ipAddress;
	private Boolean connected;
	private List<DeviceSimple> devices;
	private Boolean masterInstance;

	public SlaveIP(String ipAddress, Boolean masterInstance, Boolean connected) {
		this.ipAddress = ipAddress;
		this.connected = Boolean.FALSE;
		this.devices = new ArrayList<DeviceSimple>();
		this.masterInstance = masterInstance;
		this.connected = connected;
	}

	public SlaveIP(String ipAddress, Boolean masterInstance) {
		this(ipAddress, masterInstance, false);
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Boolean getConnected() {
		return connected;
	}

	public List<DeviceSimple> getDevices() {
		return devices;
	}

	public void setConnected(Boolean connected) {
		this.connected = connected;
	}

	public Boolean getMasterInstance() {
		return masterInstance;
	}

	public void setMasterInstance(Boolean masterInstance) {
		this.masterInstance = masterInstance;
	}

	@Override
	public String toString() {
		int lengthString = ipAddress.length();
		String tempIpAddress = ipAddress;

		if(lengthString < 15) {
			char[] c = new char[15 - lengthString];

			String space = new String(c).replace('\0', ' ');
			tempIpAddress = tempIpAddress + space;
		}

		return tempIpAddress + " " + (this.connected ? "ON" : "OFF");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((connected == null) ? 0 : connected.hashCode());
		result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SlaveIP other = (SlaveIP) obj;
		if (connected == null) {
			if (other.connected != null)
				return false;
		} else if (!connected.equals(other.connected))
			return false;
		if (ipAddress == null) {
			if (other.ipAddress != null)
				return false;
		} else if (!ipAddress.equals(other.ipAddress))
			return false;
		return true;
	}
}