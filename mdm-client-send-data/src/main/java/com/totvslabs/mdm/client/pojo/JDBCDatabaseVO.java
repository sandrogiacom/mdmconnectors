package com.totvslabs.mdm.client.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JDBCDatabaseVO {
	private String name;
	private List<JDBCTableVO> tables;
	private Map<String, JDBCTableVO> mapTables;

	public JDBCDatabaseVO() {
		this.tables = new ArrayList<JDBCTableVO>();
		this.mapTables = new HashMap<String, JDBCTableVO>();
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<JDBCTableVO> getTables() {
		return tables;
	}
	public JDBCTableVO getTable(String name) {
		return this.mapTables.get(name);
	}
	public void addTable(JDBCTableVO table) {
		this.tables.add(table);
		this.mapTables.put(table.getName(), table);
	}

	@Override
	public String toString() {
		return "JDBCDatabaseVO [name=" + name + ", tables=" + tables + "]";
	}

}
