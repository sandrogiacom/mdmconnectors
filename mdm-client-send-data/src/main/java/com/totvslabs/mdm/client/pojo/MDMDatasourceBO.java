package com.totvslabs.mdm.client.pojo;

public class MDMDatasourceBO {
	private String tenantId;
	private String datasourceId;
	private String name;

	public MDMDatasourceBO(String tenantId, String datasourceId, String name) {
		this.tenantId = tenantId;
		this.datasourceId = datasourceId;
		this.name = name;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getDatasourceId() {
		return datasourceId;
	}

	public void setDatasourceId(String datasourceId) {
		this.datasourceId = datasourceId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return this.name + "(" + this.datasourceId + ")";
	}
}
