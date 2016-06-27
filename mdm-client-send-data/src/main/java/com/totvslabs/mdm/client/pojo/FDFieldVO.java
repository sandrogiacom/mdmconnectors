package com.totvslabs.mdm.client.pojo;

public class FDFieldVO implements Cloneable {
	private String name;
	private String description;
	private String type;
	private String protheusField;
	private String instance;
	private Boolean children;
	private String fatherMDMName;
	private Boolean foreignField;

	public void setForeignField(Boolean foreignField) {
		this.foreignField  = foreignField;
	}
	public Boolean getForeignField() {
		return this.foreignField;
	}
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getProtheusField() {
		return protheusField;
	}
	public String getFatherMDMName() {
		return fatherMDMName;
	}
	public void setFatherMDMName(String fatherMDMName) {
		this.fatherMDMName = fatherMDMName;
	}
	public void setProtheusField(String protheusField) {
		this.protheusField = protheusField;
	}
	public String getInstance() {
		return instance;
	}
	public void setInstance(String instance) {
		this.instance = instance;
	}
	public Boolean getChildren() {
		return children;
	}
	public void setChildren(Boolean children) {
		this.children = children;
	}
}