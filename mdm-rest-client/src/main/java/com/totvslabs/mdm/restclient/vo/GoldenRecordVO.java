package com.totvslabs.mdm.restclient.vo;

import java.util.Map;

public class GoldenRecordVO {
	private String _mdmEntityTemplateId;
    private Map<String, Object> _mdmGoldenFieldAndValues;
    private Integer _mdmCounterForEntity;
    private String _mdmProfileTitle;
    private String _mdmId;
    private String _mdmElasticsearchMappingType;

	public String getMdmEntityTemplateId() {
		return _mdmEntityTemplateId;
	}
	public void setMdmEntityTemplateId(String _mdmEntityTemplateId) {
		this._mdmEntityTemplateId = _mdmEntityTemplateId;
	}
	public Map<String, Object> getMdmGoldenFieldAndValues() {
		return _mdmGoldenFieldAndValues;
	}
	public void setMdmGoldenFieldAndValues(Map<String, Object> _mdmGoldenFieldAndValues) {
		this._mdmGoldenFieldAndValues = _mdmGoldenFieldAndValues;
	}
	public Integer getMdmCounterForEntity() {
		return _mdmCounterForEntity;
	}
	public void setMdmCounterForEntity(Integer _mdmCounterForEntity) {
		this._mdmCounterForEntity = _mdmCounterForEntity;
	}
	public String getMdmProfileTitle() {
		return _mdmProfileTitle;
	}
	public void setMdmProfileTitle(String _mdmProfileTitle) {
		this._mdmProfileTitle = _mdmProfileTitle;
	}
	public String getMdmId() {
		return _mdmId;
	}
	public void setMdmId(String _mdmId) {
		this._mdmId = _mdmId;
	}
	public String getMdmElasticsearchMappingType() {
		return _mdmElasticsearchMappingType;
	}
	public void setMdmElasticsearchMappingType(String _mdmElasticsearchMappingType) {
		this._mdmElasticsearchMappingType = _mdmElasticsearchMappingType;
	}    
}
