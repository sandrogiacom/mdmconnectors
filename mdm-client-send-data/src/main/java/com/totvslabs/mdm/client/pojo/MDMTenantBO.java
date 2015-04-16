package com.totvslabs.mdm.client.pojo;

public class MDMTenantBO {
	private String id;
	private String name;

	public MDMTenantBO(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return name + "(" + this.id + ")";
	}
}
