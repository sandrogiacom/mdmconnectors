package com.totvslabs.mdm.restclient.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataConsumerVO extends GenericVO {
	private String mdmName;
	private Map<String, String> mdmDescription;
	private List<String> mdmEntitiesConsumed = new ArrayList<String>();
	private Map<String, DataConsumerDetailsVO> mdmEntityDetails;
	private String mdmElasticsearchMappingType;

	public String getMdmName() {
		return mdmName;
	}
	public void setMdmName(String mdmName) {
		this.mdmName = mdmName;
	}
	public Map<String, String> getmdmDescription() {
		return mdmDescription;
	}
	public List<String> getMdmEntitiesConsumed() {
		return mdmEntitiesConsumed;
	}
	public void setMdmEntitiesConsumed(List<String> mdmEntitiesConsumed) {
		this.mdmEntitiesConsumed = mdmEntitiesConsumed;
	}
	public String getMdmElasticsearchMappingType() {
		return mdmElasticsearchMappingType;
	}
	public void setMdmElasticsearchMappingType(String mdmElasticsearchMappingType) {
		this.mdmElasticsearchMappingType = mdmElasticsearchMappingType;
	}
	public Map<String, DataConsumerDetailsVO> getMdmEntityDetails() {
		return mdmEntityDetails;
	}
	public void setMdmEntityDetails(
			Map<String, DataConsumerDetailsVO> mdmEntityDetails) {
		this.mdmEntityDetails = mdmEntityDetails;
	}
}
