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

	public String get_mdmName() {
		return _mdmName;
	}
	public void set_mdmName(String _mdmName) {
		this._mdmName = _mdmName;
	}
	public Map<String, String> get_mdmLabel() {
		return _mdmLabel;
	}
	public void set_mdmLabel(Map<String, String> _mdmLabel) {
		this._mdmLabel = _mdmLabel;
	}
	public Map<String, String> get_mdmDescription() {
		return _mdmDescription;
	}
	public void set_mdmDescription(Map<String, String> _mdmDescription) {
		this._mdmDescription = _mdmDescription;
	}
	public String get_mdmIndex() {
		return _mdmIndex;
	}
	public void set_mdmIndex(String _mdmIndex) {
		this._mdmIndex = _mdmIndex;
	}
	public String get_mdmAnalyzer() {
		return _mdmAnalyzer;
	}
	public void set_mdmAnalyzer(String _mdmAnalyzer) {
		this._mdmAnalyzer = _mdmAnalyzer;
	}
	public Map<String, FieldsVO> get_mdmFieldsFull() {
		return _mdmFieldsFull;
	}
	public void set_mdmFieldsFull(Map<String, FieldsVO> _mdmFieldsFull) {
		this._mdmFieldsFull = _mdmFieldsFull;
	}
	public Boolean get_mdmIsGlobalField() {
		return _mdmIsGlobalField;
	}
	public void set_mdmIsGlobalField(Boolean _mdmIsGlobalField) {
		this._mdmIsGlobalField = _mdmIsGlobalField;
	}
	public String get_mdmFieldType() {
		return _mdmFieldType;
	}
	public void set_mdmFieldType(String _mdmFieldType) {
		this._mdmFieldType = _mdmFieldType;
	}
}
