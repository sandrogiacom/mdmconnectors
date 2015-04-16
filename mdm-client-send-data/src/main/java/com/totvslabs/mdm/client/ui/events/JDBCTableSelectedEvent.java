package com.totvslabs.mdm.client.ui.events;

import com.totvslabs.mdm.client.pojo.JDBCConnectionParameter;
import com.totvslabs.mdm.client.pojo.JDBCTableVO;

public class JDBCTableSelectedEvent {
	private JDBCTableVO tableVO;
	private JDBCConnectionParameter param;

	public JDBCTableSelectedEvent(JDBCTableVO tableVO, JDBCConnectionParameter param) {
		super();
		this.tableVO = tableVO;
		this.param = param;
	}

	public JDBCTableVO getTableVO() {
		return tableVO;
	}
	public void setTableVO(JDBCTableVO tableVO) {
		this.tableVO = tableVO;
	}
	public JDBCConnectionParameter getParam() {
		return param;
	}
	public void setParam(JDBCConnectionParameter param) {
		this.param = param;
	}
}
