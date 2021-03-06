package com.totvslabs.mdm.client.ui.events;

import com.totvslabs.mdm.client.pojo.JDBCTableVO;
import com.totvslabs.mdm.client.pojo.StoredJDBCConnectionVO;

public class JDBCTableSelectedEvent {
	private JDBCTableVO tableVO;
	private StoredJDBCConnectionVO jdbcConnectionVO;

	public JDBCTableSelectedEvent(JDBCTableVO tableVO, StoredJDBCConnectionVO jdbcConnectionVO) {
		super();
		this.tableVO = tableVO;
		this.jdbcConnectionVO = jdbcConnectionVO;
	}

	public JDBCTableVO getTableVO() {
		return tableVO;
	}
	public void setTableVO(JDBCTableVO tableVO) {
		this.tableVO = tableVO;
	}
	public StoredJDBCConnectionVO getJdbcConnectionVO() {
		return jdbcConnectionVO;
	}
	public void setJdbcConnectionVO(StoredJDBCConnectionVO jdbcConnectionVO) {
		this.jdbcConnectionVO = jdbcConnectionVO;
	}
}
