package com.totvslabs.mdm.restclient.vo;

import java.util.List;
import java.util.Map;

public class DataConsumerDetailsVO {
	private String mdmEntityConsumed;
	private Map<String, String> mdmEntityName;
	private List<String> mdmFieldsConsumed;
	private Integer mdmLastCounterConsumed;
	private Integer mdmNumOfPendingRecords;

	public String getMdmEntityConsumed() {
		return mdmEntityConsumed;
	}
	public void setMdmEntityConsumed(String mdmEntityConsumed) {
		this.mdmEntityConsumed = mdmEntityConsumed;
	}
	public Map<String, String> getMdmEntityName() {
		return mdmEntityName;
	}
	public void setMdmEntityName(Map<String, String> mdmEntityName) {
		this.mdmEntityName = mdmEntityName;
	}
	public List<String> getMdmFieldsConsumed() {
		return mdmFieldsConsumed;
	}
	public void setMdmFieldsConsumed(List<String> mdmFieldsConsumed) {
		this.mdmFieldsConsumed = mdmFieldsConsumed;
	}
	public Integer getMdmLastCounterConsumed() {
		return mdmLastCounterConsumed;
	}
	public void setMdmLastCounterConsumed(Integer mdmLastCounterConsumed) {
		this.mdmLastCounterConsumed = mdmLastCounterConsumed;
	}
	public Integer getMdmNumOfPendingRecords() {
		return mdmNumOfPendingRecords;
	}
	public void setMdmNumOfPendingRecords(Integer mdmNumOfPendingRecords) {
		this.mdmNumOfPendingRecords = mdmNumOfPendingRecords;
	}
}
