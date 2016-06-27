package com.totvslabs.mdm.client.pojo;

import com.google.gson.JsonArray;

public class MDMCSVData extends MDMAbstractData {
	StringBuffer data = new StringBuffer();

	public MDMCSVData(String typeName) {
		super(typeName);
	}

	@Override
	public JsonArray getData() {
		return null;
	}
}
