package com.totvslabs.mdm.client.pojo;

import java.util.Date;

public abstract class GenericMDMBO {
	private String mdmType;
	private Date mdmLastUpdated;
	private Date mdmCreated;

	private Long mdmId;
	private Long mdmTenantId;
	private Long mdmDatasourceId;

	private String name;
	private String description;
	public String getMdmType() {
		return mdmType;
	}
	public void setMdmType(String mdmType) {
		this.mdmType = mdmType;
	}
	public Date getMdmLastUpdated() {
		return mdmLastUpdated;
	}
	public void setMdmLastUpdated(Date mdmLastUpdated) {
		this.mdmLastUpdated = mdmLastUpdated;
	}
	public Date getMdmCreated() {
		return mdmCreated;
	}
	public void setMdmCreated(Date mdmCreated) {
		this.mdmCreated = mdmCreated;
	}
	public Long getMdmId() {
		return mdmId;
	}
	public void setMdmId(Long mdmId) {
		this.mdmId = mdmId;
	}
	public Long getMdmTenantId() {
		return mdmTenantId;
	}
	public void setMdmTenantId(Long mdmTenantId) {
		this.mdmTenantId = mdmTenantId;
	}
	public Long getMdmDatasourceId() {
		return mdmDatasourceId;
	}
	public void setMdmDatasourceId(Long mdmDatasourceId) {
		this.mdmDatasourceId = mdmDatasourceId;
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
}
