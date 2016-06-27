package com.totvslabs.mdm.client.pojo;

import com.google.gson.JsonArray;


public abstract class MDMAbstractData {
	private String templateName;

	public MDMAbstractData(String typeName) {
		this.templateName = typeName;
	}

	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public abstract JsonArray getData();
}
