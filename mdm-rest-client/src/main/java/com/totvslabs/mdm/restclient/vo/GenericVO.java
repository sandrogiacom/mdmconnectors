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

	public String get_mdmType() {
		return _mdmType;
	}
	public void set_mdmType(String _mdmType) {
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
	public String get_mdmId() {
		return _mdmId;
	}
	public void set_mdmId(String _mdmId) {
		this._mdmId = _mdmId;
	}
	public String get_mdmTenantId() {
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
	public void set_mdmTenantId(String _mdmTenantId) {
		this._mdmTenantId = _mdmTenantId;
	}
	public String get_mdmDataSourceId() {
		return _mdmDataSourceId;
	}
	public void set_mdmDataSourceId(String _mdmDataSourceId) {
		this._mdmDataSourceId = _mdmDataSourceId;
	}
	public String get_mdmLastUpdated() {
		return _mdmLastUpdated;
	}
	public void set_mdmLastUpdated(String _mdmLastUpdated) {
		this._mdmLastUpdated = _mdmLastUpdated;
	}
	public String get_mdmCreated() {
		return _mdmCreated;
	}
	public void set_mdmCreated(String _mdmCreated) {
		this._mdmCreated = _mdmCreated;
	}
}
