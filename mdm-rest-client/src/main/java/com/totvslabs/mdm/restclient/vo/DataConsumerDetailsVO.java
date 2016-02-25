package com.totvslabs.mdm.restclient.vo;

import java.util.List;
import java.util.Map;

public class DataConsumerDetailsVO {
	private String _mdmEntityConsumed;
	private Map<String, String> _mdmEntityName;
	private List<String> _mdmFieldsConsumed;
	private Integer _mdmLastCounterConsumed;
	private Integer _mdmNumOfPendingRecords;

	public String getMdmEntityConsumed() {
		return _mdmEntityConsumed;
	}
	public void setMdmEntityConsumed(String _mdmEntityConsumed) {
		this._mdmEntityConsumed = _mdmEntityConsumed;
	}
	public Map<String, String> getMdmEntityName() {
		return _mdmEntityName;
	}
	public void setMdmEntityName(Map<String, String> _mdmEntityName) {
		this._mdmEntityName = _mdmEntityName;
	}
	public List<String> getMdmFieldsConsumed() {
		return _mdmFieldsConsumed;
	}
	public void setMdmFieldsConsumed(List<String> _mdmFieldsConsumed) {
		this._mdmFieldsConsumed = _mdmFieldsConsumed;
	}
	public Integer getMdmLastCounterConsumed() {
		return _mdmLastCounterConsumed;
	}
	public void setMdmLastCounterConsumed(Integer _mdmLastCounterConsumed) {
		this._mdmLastCounterConsumed = _mdmLastCounterConsumed;
	}
	public Integer getMdmNumOfPendingRecords() {
		return _mdmNumOfPendingRecords;
	}
	public void setMdmNumOfPendingRecords(Integer _mdmNumOfPendingRecords) {
		this._mdmNumOfPendingRecords = _mdmNumOfPendingRecords;
	}
}
