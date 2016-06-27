package com.totvslabs.mdm.restclient.vo;

import java.util.Map;


public class DataModelVO extends GenericVO {
	private String mdmName;
	private Map<String, String> mdmLabel;
	private Map<String, String> mdmDescription;

	public String getMdmName() {
		return mdmName;
	}
	public void setMdmName(String mdmName) {
		this.mdmName = mdmName;
	}
	public Map<String, String> getMdmLabel() {
		return mdmLabel;
	}
	public void setMdmLabel(Map<String, String> mdmLabel) {
		this.mdmLabel = mdmLabel;
	}
	public Map<String, String> getMdmDescription() {
		return mdmDescription;
	}
	public void setMdmDescription(Map<String, String> mdmDescription) {
		this.mdmDescription = mdmDescription;
	}
}
