package com.totvslabs.mdm.client.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JDBCIndexVO {
	private String name;
	private String nameAlternative;
	private Boolean unique;
	private List<JDBCFieldVO> fields;
	private Map<String, JDBCFieldVO> mapFields;

	public JDBCIndexVO() {
		this.fields = new ArrayList<JDBCFieldVO>();
		this.mapFields = new HashMap<String, JDBCFieldVO>();
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
	public JDBCFieldVO getField(String name) {
		return this.mapFields.get(name.toUpperCase());
	}
	public void addField(JDBCFieldVO field) {
		this.fields.add(field);
		this.mapFields.put(field.getName().toUpperCase(), field);

		if(this.fields != null) {
			StringBuffer sb = new StringBuffer();

			for (JDBCFieldVO fieldItemVO : this.fields) {
				if(sb.length() > 0) {
					sb.append("_");
				}

				if(fieldItemVO != null) {
					sb.append(fieldItemVO.getName());
				}
			}

			this.nameAlternative = sb.toString();
		}
	}
	public Boolean getUnique() {
		return unique;
	}
	public void setUnique(Boolean unique) {
		this.unique = unique;
	}
	public String getNameAlternative() {
		return nameAlternative;
	}
	public void setNameAlternative(String nameAlternative) {
		this.nameAlternative = nameAlternative;
	}
	public void addFields(List<JDBCFieldVO> fieldsVO) {
		if(fieldsVO != null) {
			for (JDBCFieldVO fieldVO : fieldsVO) {
				this.addField(fieldVO);
			}
		}
	}

	@Override
	public String toString() {
		return "IndexVO [name=" + name + ", fields=" + fields + ", mapFields="
				+ mapFields + "]";
	}
}
