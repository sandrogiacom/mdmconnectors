package com.totvslabs.mdm.restclient.vo;

import java.util.Map;


public class DataModelVO extends GenericVO {
	private String _mdmName;
	private Map<String, String> _mdmLabel;
	private Map<String, String> _mdmDescription;

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
}
