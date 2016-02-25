package com.totvslabs.mdm.restclient.vo;

import java.util.Map;

public class FieldsVO extends GenericVO {
	private String _mdmName;
	private Map<String, String> _mdmLabel;
	private Map<String, String> _mdmDescription;
	private String _mdmIndex;
	private String _mdmAnalyzer;
	private Map<String, FieldsVO> _mdmFieldsFull;
	private Boolean _mdmIsGlobalField;
	private String _mdmFieldType;

	public String getMdmName() {
		return _mdmName;
	}
	public void setMdmName(String _mdmName) {
		this._mdmName = _mdmName;
	}
	public Map<String, String> getMdmLabel() {
		return _mdmLabel;
	}
	public void setMdmLabel(Map<String, String> _mdmLabel) {
		this._mdmLabel = _mdmLabel;
	}
	public Map<String, String> getMdmDescription() {
		return _mdmDescription;
	}
	public void setMdmDescription(Map<String, String> _mdmDescription) {
		this._mdmDescription = _mdmDescription;
	}
	public String getMdmIndex() {
		return _mdmIndex;
	}
	public void setMdmIndex(String _mdmIndex) {
		this._mdmIndex = _mdmIndex;
	}
	public String getMdmAnalyzer() {
		return _mdmAnalyzer;
	}
	public void setMdmAnalyzer(String _mdmAnalyzer) {
		this._mdmAnalyzer = _mdmAnalyzer;
	}
	public Map<String, FieldsVO> getMdmFieldsFull() {
		return _mdmFieldsFull;
	}
	public void setMdmFieldsFull(Map<String, FieldsVO> _mdmFieldsFull) {
		this._mdmFieldsFull = _mdmFieldsFull;
	}
	public Boolean getMdmIsGlobalField() {
		return _mdmIsGlobalField;
	}
	public void setMdmIsGlobalField(Boolean _mdmIsGlobalField) {
		this._mdmIsGlobalField = _mdmIsGlobalField;
	}
	public String getMdmFieldType() {
		return _mdmFieldType;
	}
	public void setMdmFieldType(String _mdmFieldType) {
		this._mdmFieldType = _mdmFieldType;
	}
}
