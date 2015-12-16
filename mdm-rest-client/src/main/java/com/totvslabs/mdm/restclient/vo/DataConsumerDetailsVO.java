package com.totvslabs.mdm.restclient.vo;

import java.util.List;
import java.util.Map;

public class DataConsumerDetailsVO {
	private String _mdmEntityConsumed;
	private Map<String, String> _mdmEntityName;
	private List<String> _mdmFieldsConsumed;
	private Integer _mdmLastCounterConsumed;
	private Integer _mdmNumOfPendingRecords;

	public String get_mdmEntityConsumed() {
		return _mdmEntityConsumed;
	}
	public void set_mdmEntityConsumed(String _mdmEntityConsumed) {
		this._mdmEntityConsumed = _mdmEntityConsumed;
	}
	public Map<String, String> get_mdmEntityName() {
		return _mdmEntityName;
	}
	public void set_mdmEntityName(Map<String, String> _mdmEntityName) {
		this._mdmEntityName = _mdmEntityName;
	}
	public List<String> get_mdmFieldsConsumed() {
		return _mdmFieldsConsumed;
	}
	public void set_mdmFieldsConsumed(List<String> _mdmFieldsConsumed) {
		this._mdmFieldsConsumed = _mdmFieldsConsumed;
	}
	public Integer get_mdmLastCounterConsumed() {
		return _mdmLastCounterConsumed;
	}
	public void set_mdmLastCounterConsumed(Integer _mdmLastCounterConsumed) {
		this._mdmLastCounterConsumed = _mdmLastCounterConsumed;
	}
	public Integer get_mdmNumOfPendingRecords() {
		return _mdmNumOfPendingRecords;
	}
	public void set_mdmNumOfPendingRecords(Integer _mdmNumOfPendingRecords) {
		this._mdmNumOfPendingRecords = _mdmNumOfPendingRecords;
	}
}
