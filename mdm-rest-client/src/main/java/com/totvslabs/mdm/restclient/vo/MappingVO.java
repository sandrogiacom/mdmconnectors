package com.totvslabs.mdm.restclient.vo;

import java.util.Map;

public class MappingVO {
	private String type;
	private String index;
	private String analyzer;
	private Integer ignore_above;
	private String format;
	private Boolean omit_norms;
	private Map<String, MappingVO> properties;
	private Map<String, MappingVO> fields;

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the index
	 */
	public String getIndex() {
		return index;
	}

	/**
	 * @param index
	 *            the index to set
	 */
	public void setIndex(String index) {
		this.index = index;
	}

	/**
	 * @return the analyzer
	 */
	public String getAnalyzer() {
		return analyzer;
	}

	/**
	 * @param analyzer
	 *            the analyzer to set
	 */
	public void setAnalyzer(String analyzer) {
		this.analyzer = analyzer;
	}

	/**
	 * @return the ignore_above
	 */
	public Integer getIgnore_above() {
		return ignore_above;
	}

	/**
	 * @param ignore_above
	 *            the ignore_above to set
	 */
	public void setIgnore_above(Integer ignore_above) {
		this.ignore_above = ignore_above;
	}

	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @param format
	 *            the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * @return the omit_norms
	 */
	public Boolean getOmit_norms() {
		return omit_norms;
	}

	/**
	 * @param omit_norms
	 *            the omit_norms to set
	 */
	public void setOmit_norms(Boolean omit_norms) {
		this.omit_norms = omit_norms;
	}

	/**
	 * @return the properties
	 */
	public Map<String, MappingVO> getProperties() {
		return properties;
	}

	/**
	 * @param properties
	 *            the properties to set
	 */
	public void setProperties(Map<String, MappingVO> properties) {
		this.properties = properties;
	}

	/**
	 * @return the fields
	 */
	public Map<String, MappingVO> getFields() {
		return fields;
	}

	/**
	 * @param fields
	 *            the fields to set
	 */
	public void setFields(Map<String, MappingVO> fields) {
		this.fields = fields;
	}

}
