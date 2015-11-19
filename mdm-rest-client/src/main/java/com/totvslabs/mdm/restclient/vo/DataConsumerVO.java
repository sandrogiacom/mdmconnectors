package com.totvslabs.mdm.restclient.vo;

import java.util.ArrayList;
import java.util.List;

public class DataConsumerVO extends GenericVO {
	private String _mdmName;
	private String _mdmDescription;
	private List<String> _mdmEntitiesConsumed = new ArrayList<String>();
	private String _mdmElasticsearchMappingType;

	public String get_mdmName() {
		return _mdmName;
	}
	public void set_mdmName(String _mdmName) {
		this._mdmName = _mdmName;
	}
	public String get_mdmDescription() {
		return _mdmDescription;
	}
	public void set_mdmDescription(String _mdmDescription) {
		this._mdmDescription = _mdmDescription;
	}
	public List<String> get_mdmEntitiesConsumed() {
		return _mdmEntitiesConsumed;
	}
	public void set_mdmEntitiesConsumed(List<String> _mdmEntitiesConsumed) {
		this._mdmEntitiesConsumed = _mdmEntitiesConsumed;
	}
	public String get_mdmElasticsearchMappingType() {
		return _mdmElasticsearchMappingType;
	}
	public void set_mdmElasticsearchMappingType(String _mdmElasticsearchMappingType) {
		this._mdmElasticsearchMappingType = _mdmElasticsearchMappingType;
	}
}
