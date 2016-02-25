package com.totvslabs.mdm.restclient.vo;

public class GenericVO {
	private String _mdmType;
	private String _mdmLastUpdated;
	private String _mdmCreated;
	private String name;
	private String description;
	private String _mdmId;
	private String _mdmTenantId;
	private String _mdmDataSourceId;

	public String getMdmType() {
		return _mdmType;
	}
	public void setMdmType(String _mdmType) {
		this._mdmType = _mdmType;
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
		return _mdmId;
	}
	public void setMdmId(String _mdmId) {
		this._mdmId = _mdmId;
	}
	public String getMdmTenantId() {
		return _mdmTenantId;
	}
	@Override
	public String toString() {
		return "GenericVO [_mdmType=" + _mdmType + ", _mdmLastUpdated="
				+ _mdmLastUpdated + ", _mdmCreated=" + _mdmCreated + ", name="
				+ name + ", description=" + description + ", _mdmId=" + _mdmId
				+ ", _mdmTenantId=" + _mdmTenantId + ", _mdmDataSourceId="
				+ _mdmDataSourceId + "]";
	}
	public void setMdmTenantId(String _mdmTenantId) {
		this._mdmTenantId = _mdmTenantId;
	}
	public String getMdmDataSourceId() {
		return _mdmDataSourceId;
	}
	public void setMdmDataSourceId(String _mdmDataSourceId) {
		this._mdmDataSourceId = _mdmDataSourceId;
	}
	public String getMdmLastUpdated() {
		return _mdmLastUpdated;
	}
	public void setMdmLastUpdated(String _mdmLastUpdated) {
		this._mdmLastUpdated = _mdmLastUpdated;
	}
	public String getMdmCreated() {
		return _mdmCreated;
	}
	public void setMdmCreated(String _mdmCreated) {
		this._mdmCreated = _mdmCreated;
	}
}
