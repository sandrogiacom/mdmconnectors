package com.totvslabs.mdm.client.pojo;

import com.google.gson.JsonObject;

public class JDBCFieldVO {
	private String name;
	private Boolean identifier;
	private String type;
	private Double size;
	private JsonObject members;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Boolean getIdentifier() {
		return identifier;
	}
	public void setIdentifier(Boolean identifier) {
		this.identifier = identifier;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Double getSize() {
		return size;
	}
	public void setSize(Double size) {
		this.size = size;
	}
	public JsonObject getMembers() {
		return this.members;
	}
	public void setMembers(JsonObject members) {
		this.members = members;
	}
	@Override
	public String toString() {
		return "JDBCFieldVO [name=" + name + ", identifier=" + identifier
				+ ", type=" + type + ", size=" + size + ", members=" + members
				+ "]";
	}
}
