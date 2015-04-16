package com.totvslabs.mdm.client.pojo;

public class JDBCDriverTypeVO {
	private String description;

	public JDBCDriverTypeVO(String description) {
		super();
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return description;
	}
}
