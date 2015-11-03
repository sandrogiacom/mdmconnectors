package com.totvslabs.mdm.client.pojo;

import com.google.gson.JsonArray;

public class MDMData {
	private String templateName;
	private JsonArray data;

	public MDMData(String sheetName, JsonArray arrayData) {
		this.templateName = sheetName;
		this.data = arrayData;
	}

	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public JsonArray getData() {
		return data;
	}
	public void setData(JsonArray data) {
		this.data = data;
	}
}
