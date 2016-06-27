package com.totvslabs.mdm.restclient.vo;

public class GenericVO {
	private String mdmType;
	private String mdmLastUpdated;
	private String mdmCreated;
	private String name;
	private String description;
	private String mdmId;
	private String mdmTenantId;
	private String mdmDataSourceId;

	public String getMdmType() {
		return mdmType;
	}
	public void setMdmType(String mdmType) {
		this.mdmType = mdmType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getMdmId() {
		return mdmId;
	}
	public void setMdmId(String mdmId) {
		this.mdmId = mdmId;
	}
	public String getMdmTenantId() {
		return mdmTenantId;
	}
	@Override
	public String toString() {
		return "GenericVO [mdmType=" + mdmType + ", mdmLastUpdated="
				+ mdmLastUpdated + ", mdmCreated=" + mdmCreated + ", name="
				+ name + ", description=" + description + ", mdmId=" + mdmId
				+ ", mdmTenantId=" + mdmTenantId + ", mdmDataSourceId="
				+ mdmDataSourceId + "]";
	}
	public void setMdmTenantId(String mdmTenantId) {
		this.mdmTenantId = mdmTenantId;
	}
	public String getMdmDataSourceId() {
		return mdmDataSourceId;
	}
	public void setMdmDataSourceId(String mdmDataSourceId) {
		this.mdmDataSourceId = mdmDataSourceId;
	}
	public String getMdmLastUpdated() {
		return mdmLastUpdated;
	}
	public void setMdmLastUpdated(String mdmLastUpdated) {
		this.mdmLastUpdated = mdmLastUpdated;
	}
	public String getMdmCreated() {
		return mdmCreated;
	}
	public void setMdmCreated(String mdmCreated) {
		this.mdmCreated = mdmCreated;
	}
}
