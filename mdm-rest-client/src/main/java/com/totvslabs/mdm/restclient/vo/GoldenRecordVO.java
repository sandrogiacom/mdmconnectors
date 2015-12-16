package com.totvslabs.mdm.restclient.vo;

import java.util.Map;

public class GoldenRecordVO {
	private String _mdmEntityTemplateId;
    private Map<String, Object> _mdmGoldenFieldAndValues;
    private Integer _mdmCounterForEntity;
    private String _mdmProfileTitle;
    private String _mdmId;
    private String _mdmElasticsearchMappingType;

	public String get_mdmEntityTemplateId() {
		return _mdmEntityTemplateId;
	}
	public void set_mdmEntityTemplateId(String _mdmEntityTemplateId) {
		this._mdmEntityTemplateId = _mdmEntityTemplateId;
	}
	public Map<String, Object> get_mdmGoldenFieldAndValues() {
		return _mdmGoldenFieldAndValues;
	}
	public void set_mdmGoldenFieldAndValues(Map<String, Object> _mdmGoldenFieldAndValues) {
		this._mdmGoldenFieldAndValues = _mdmGoldenFieldAndValues;
	}
	public Integer get_mdmCounterForEntity() {
		return _mdmCounterForEntity;
	}
	public void set_mdmCounterForEntity(Integer _mdmCounterForEntity) {
		this._mdmCounterForEntity = _mdmCounterForEntity;
	}
	public String get_mdmProfileTitle() {
		return _mdmProfileTitle;
	}
	public void set_mdmProfileTitle(String _mdmProfileTitle) {
		this._mdmProfileTitle = _mdmProfileTitle;
	}
	public String get_mdmId() {
		return _mdmId;
	}
	public void set_mdmId(String _mdmId) {
		this._mdmId = _mdmId;
	}
	public String get_mdmElasticsearchMappingType() {
		return _mdmElasticsearchMappingType;
	}
	public void set_mdmElasticsearchMappingType(String _mdmElasticsearchMappingType) {
		this._mdmElasticsearchMappingType = _mdmElasticsearchMappingType;
	}    
}
