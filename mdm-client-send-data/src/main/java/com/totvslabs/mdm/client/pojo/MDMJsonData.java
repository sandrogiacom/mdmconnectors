package com.totvslabs.mdm.client.pojo;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;

public class MDMJsonData extends MDMAbstractData {
	private JsonArray data;
	private List<JDBCFieldVO> newFields = new ArrayList<JDBCFieldVO>();

	public MDMJsonData(String typeName, JsonArray arrayData) {
		super(typeName);
		this.data = arrayData;
	}

	public JsonArray getData() {
		return this.data;
	}

	public List<JDBCFieldVO> getNewFields() {
		return this.newFields;
	}
}
