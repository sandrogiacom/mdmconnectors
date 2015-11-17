package com.totvslabs.mdm.client.ui.events;

import java.util.List;

import com.totvslabs.mdm.client.pojo.JDBCConnectionParameter;
import com.totvslabs.mdm.client.pojo.JDBCTableVO;

public class DataLoadedEvent {
	private List<JDBCTableVO> tables;
	private JDBCConnectionParameter param;

	public DataLoadedEvent(JDBCConnectionParameter param, List<JDBCTableVO> tables) {
		super();
		this.tables = tables;
		this.param = param;
	}

	public DataLoadedEvent() {
		super();
	}

	public List<JDBCTableVO> getTables() {
		return tables;
	}

	public void setTables(List<JDBCTableVO> tables) {
		this.tables = tables;
	}

	public JDBCConnectionParameter getParam() {
		return param;
	}

	public void setParam(JDBCConnectionParameter param) {
		this.param = param;
	}
}

