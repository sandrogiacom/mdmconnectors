package com.totvslabs.mdm.restclient.vo;

import java.util.Map;

public class GoldenRecordCrossWalkVO {
	private String mdmApplicationId;
	private Map<String, Map<String, String>> mdmCrossreference;

	public String getMdmApplicationId() {
		return mdmApplicationId;
	}
	public void setMdmApplicationId(String mdmApplicationId) {
		this.mdmApplicationId = mdmApplicationId;
	}
	public Map<String, Map<String, String>> getMdmCrossreference() {
		return mdmCrossreference;
	}
	public void setMdmCrossreference(Map<String, Map<String, String>> mdmCrossreference) {
		this.mdmCrossreference = mdmCrossreference;
	}
}
