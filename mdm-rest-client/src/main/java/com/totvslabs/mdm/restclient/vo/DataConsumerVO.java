package com.totvslabs.mdm.restclient.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataConsumerVO extends GenericVO {
	private String _mdmName;
	private String _mdmDescription;
	private List<String> _mdmEntitiesConsumed = new ArrayList<String>();
	private Map<String, DataConsumerDetailsVO> _mdmEntityDetails;
	private String _mdmElasticsearchMappingType;

	public String getMdmName() {
		return _mdmName;
	}
	public void setMdmName(String _mdmName) {
		this._mdmName = _mdmName;
	}
	public String getMdmDescription() {
		return _mdmDescription;
	}
	public void setMdmDescription(String _mdmDescription) {
		this._mdmDescription = _mdmDescription;
	}
	public List<String> getMdmEntitiesConsumed() {
		return _mdmEntitiesConsumed;
	}
	public void setMdmEntitiesConsumed(List<String> _mdmEntitiesConsumed) {
		this._mdmEntitiesConsumed = _mdmEntitiesConsumed;
	}
	public String getMdmElasticsearchMappingType() {
		return _mdmElasticsearchMappingType;
	}
	public void setMdmElasticsearchMappingType(String _mdmElasticsearchMappingType) {
		this._mdmElasticsearchMappingType = _mdmElasticsearchMappingType;
	}
	public Map<String, DataConsumerDetailsVO> getMdmEntityDetails() {
		return _mdmEntityDetails;
	}
	public void setMdmEntityDetails(
			Map<String, DataConsumerDetailsVO> _mdmEntityDetails) {
		this._mdmEntityDetails = _mdmEntityDetails;
	}
}
