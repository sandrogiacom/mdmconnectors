package com.totvslabs.mdm.client.util;

import com.totvslabs.mdm.client.pojo.JDBCDatabaseVO;
import com.totvslabs.mdm.client.pojo.JDBCTableVO;
import com.totvslabs.mdm.client.pojo.MDMJsonData;
import com.totvslabs.mdm.client.pojo.StoredJDBCConnectionVO;

public abstract class DBConnectionFactory {
	public static DBConnectionFactory getDb() {
		return DBConnectionFactory.getDb("");
	}
	public static DBConnectionFactory getDb(String db) {
		if(db.equalsIgnoreCase("mongo")) {
			return new NoSQLConnectionFactory();
		}
		else {
			return new SQLConnectionFactory();
		}
	}
	public abstract Object getConnection(String url, String driver, String user, String password);
	public abstract Long getTotalRecords(StoredJDBCConnectionVO jdbcConnectionVO, JDBCTableVO tableVO);
	public abstract MDMJsonData loadData(StoredJDBCConnectionVO jdbcConnectionVO, JDBCTableVO tableVO, int initialRecord, int quantity);
	public abstract MDMJsonData loadData(StoredJDBCConnectionVO jdbcConnectionVO, JDBCTableVO tableVO);
	public abstract void loadFisicModelFields(StoredJDBCConnectionVO jdbcConnectionVO, JDBCTableVO tableVO);
	public abstract void loadFisicModelFields(String url, String driver, String user, String password, JDBCTableVO tableVO);
	public abstract JDBCDatabaseVO loadFisicModelTables(String url, String driver, String user, String password);
}
