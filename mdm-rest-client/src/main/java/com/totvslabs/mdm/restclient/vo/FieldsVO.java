package com.totvslabs.mdm.restclient.vo;

import java.util.Map;

public class FieldsVO extends GenericVO {
	private String mdmName;
	private Map<String, String> mdmLabel;
	private Map<String, String> mdmDescription;
	private String mdmIndex;
	private String mdmAnalyzer;
	private String mdmForeignKeyField;
	private Map<String, FieldsVO> mdmFieldsFull;
	private Boolean mdmIsGlobalField;
	private String mdmFieldType;

	public void setMdmForeignKeyField(String mdmForeignKeyField) {
		this.mdmForeignKeyField=mdmForeignKeyField;
	}
	public String getMdmForeignKeyField() {
		return this.mdmForeignKeyField;
	}
	public String getMdmName() {
		return mdmName;
	}
	public void setMdmName(String mdmName) {
		this.mdmName = mdmName;
	}
	public Map<String, String> getMdmLabel() {
		return mdmLabel;
	}
	public void setMdmLabel(Map<String, String> mdmLabel) {
		this.mdmLabel = mdmLabel;
	}
	public Map<String, String> getMdmDescription() {
		return mdmDescription;
	}
	public void setMdmDescription(Map<String, String> mdmDescription) {
		this.mdmDescription = mdmDescription;
	}
	public String getMdmIndex() {
		return mdmIndex;
	}
	public void setMdmIndex(String mdmIndex) {
		this.mdmIndex = mdmIndex;
	}
	public String getMdmAnalyzer() {
		return mdmAnalyzer;
	}
	public void setMdmAnalyzer(String mdmAnalyzer) {
		this.mdmAnalyzer = mdmAnalyzer;
	}
	public Map<String, FieldsVO> getMdmFieldsFull() {
		return mdmFieldsFull;
	}
	public void setMdmFieldsFull(Map<String, FieldsVO> mdmFieldsFull) {
		this.mdmFieldsFull = mdmFieldsFull;
	}
	public Boolean getMdmIsGlobalField() {
		return mdmIsGlobalField;
	}
	public void setMdmIsGlobalField(Boolean mdmIsGlobalField) {
		this.mdmIsGlobalField = mdmIsGlobalField;
	}
	public String getMdmFieldType() {
		return mdmFieldType;
	}
	public void setMdmFieldType(String mdmFieldType) {
		this.mdmFieldType = mdmFieldType;
	}
}
