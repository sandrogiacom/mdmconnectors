package com.totvslabs.mdm.restclient.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ApplicationVO extends GenericVO {
	private String mdmName;
	private Map<String, String> mdmLabel;
	private Map<String, String> mdmDescription;
	private List<String> mdmEntitiesConsumed = new ArrayList<String>();
	private Map<String, EntityDetailsVO> mdmEntityDetails;
	private String mdmElasticsearchMappingType;

	public Map<String, String> getMdmLabel() {
		return this.mdmLabel;
	}
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
	public Map<String, EntityDetailsVO> getMdmEntityDetails() {
		return mdmEntityDetails;
	}
	public void setMdmEntityDetails(
			Map<String, EntityDetailsVO> mdmEntityDetails) {
		this.mdmEntityDetails = mdmEntityDetails;
	}
}
