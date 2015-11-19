package com.totvslabs.mdm.client.ui.events;

import java.util.List;

import com.totvslabs.mdm.client.pojo.JDBCTableVO;
import com.totvslabs.mdm.client.pojo.StoredJDBCConnectionVO;

public class JDBCConnectionStabilizedEvent {
	private List<JDBCTableVO> tables;
	private StoredJDBCConnectionVO jdbcConnectionVO;

	public JDBCConnectionStabilizedEvent(StoredJDBCConnectionVO jdbcConnectionVO, List<JDBCTableVO> tables) {
		super();
		this.jdbcConnectionVO = jdbcConnectionVO;
		this.tables = tables;
	}

	public JDBCConnectionStabilizedEvent() {
		super();
	}

	public List<JDBCTableVO> getTables() {
		return tables;
	}

	public void setTables(List<JDBCTableVO> tables) {
		this.tables = tables;
	}

	public StoredJDBCConnectionVO getJdbcConnectionVO() {
		return jdbcConnectionVO;
	}

	public void setJdbcConnectionVO(StoredJDBCConnectionVO jdbcConnectionVO) {
		this.jdbcConnectionVO = jdbcConnectionVO;
	}
}

