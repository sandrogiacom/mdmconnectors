package com.totvslabs.mdm.client.pojo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JDBCTableVO {
	private String name;
	private String databaseName;
	private String internalName;
	private List<JDBCFieldVO> fields = new ArrayList<JDBCFieldVO>();
	private Map<String, JDBCFieldVO> fieldsMap = new HashMap<String, JDBCFieldVO>();
	private Collection<JDBCIndexVO> primaryKey = new ArrayList<JDBCIndexVO>();
	private Long totalRecords;

	public JDBCTableVO(String name) {
		this(name, null);
	}

	public JDBCTableVO(String name, String databaseName) {
		super();
		this.name = name;
		this.databaseName = databaseName;

		if(name.contains("-")) {
			internalName = "\"" + name + "\"";
		}
		else {
			internalName = name;
		}
	}

	public String getDatabaseName() {
		return databaseName;
	}
	public String getInternalName() {
		return internalName;
	}
	public void setInternalName(String internalName) {
		this.internalName = internalName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public JDBCFieldVO getField(String fieldName) {
		return this.fieldsMap.get(fieldName);
	}
	public void addField(JDBCFieldVO fieldVO) {
		fields.add(fieldVO);
		fieldsMap.put(fieldVO.getName(), fieldVO);
	}
	public List<JDBCFieldVO> getFields() {
		return fields;
	}
	public void setFields(List<JDBCFieldVO> fields) {
		this.fields = fields;
	}
	public Long getTotalRecords() {
		return totalRecords;
	}
	public void setTotalRecords(Long totalRecords) {
		this.totalRecords = totalRecords;
	}
	public Collection<JDBCIndexVO> getPrimaryKey() {
		return primaryKey;
	}
	public void setPrimaryKey(Collection<JDBCIndexVO> primaryKey) {
		this.primaryKey = primaryKey;
	}
	@Override
	public String toString() {
		return (name + (totalRecords != null && totalRecords != 0 ? " (" + totalRecords +  " records)" : ""));
	}
}
