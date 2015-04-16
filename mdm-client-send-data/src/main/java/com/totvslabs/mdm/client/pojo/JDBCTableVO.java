package com.totvslabs.mdm.client.pojo;

import java.util.ArrayList;
import java.util.List;

public class JDBCTableVO {
	private String name;
	private List<JDBCFieldVO> fields = new ArrayList<JDBCFieldVO>();

	public JDBCTableVO(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<JDBCFieldVO> getFields() {
		return fields;
	}
	public void setFields(List<JDBCFieldVO> fields) {
		this.fields = fields;
	}

	@Override
	public String toString() {
		return name;
	}
}
