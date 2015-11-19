package com.totvslabs.mdm.restclient.vo;

import java.util.Map;


public class DataModelVO extends GenericVO {
	private String _mdmName;
	private Map<String, String> _mdmLabel;
	private Map<String, String> _mdmDescription;

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
}
