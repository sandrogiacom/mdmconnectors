package com.totvslabs.mdm.client.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.totvslabs.mdm.client.pojo.JDBCConnectionParameter;
import com.totvslabs.mdm.client.pojo.JDBCDatabaseVO;
import com.totvslabs.mdm.client.pojo.JDBCFieldVO;
import com.totvslabs.mdm.client.pojo.JDBCTableVO;

public class JDBCConnectionFactory {

	private static Connection getJDBCConnection(String url, String user, String password) {
	    Connection connection = null;
	    Properties connectionProps = new Properties();
	    connectionProps.put("user", user);
	    connectionProps.put("password", password);

        try {
			connection = DriverManager.getConnection(url, connectionProps);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		return connection;
	}

	public static Integer getTotalRecords(JDBCConnectionParameter param, JDBCTableVO tableVO) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT count(*) FROM ");
		sql.append(tableVO.getName());

		Connection connection = JDBCConnectionFactory.getJDBCConnection(param.getUrl(), param.getUser(), param.getPassword());
		Integer totalRecords = 0;

		try {
			Statement st = null;

			try {
				st = connection.createStatement();
				ResultSet rs = st.executeQuery(sql.toString());

				while(rs.next()) {
					totalRecords = rs.getInt(1);
				}

				if(rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			finally {
				if(st != null) {
					try {
						st.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
		finally {
			try {
				if(connection != null) {
				connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return totalRecords;
	}

	public static JsonArray loadData(JDBCConnectionParameter param, JDBCTableVO tableVO, int initialRecord, int quantity) {
		JsonArray jsonRecords = new JsonArray();
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT * FROM ");
		sql.append(tableVO.getName());

		Connection connection = JDBCConnectionFactory.getJDBCConnection(param.getUrl(), param.getUser(), param.getPassword());

		try {
			Statement st = null;

			try {
				st = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
				ResultSet rs = st.executeQuery(sql.toString());
				ResultSetMetaData metaData = rs.getMetaData();

				int columnCount = metaData.getColumnCount();
				int totalRecordsLoaded = 0;

				if(quantity > 0) {
					st.setMaxRows(quantity);
				}

				if(initialRecord > 0) {
					rs.absolute(initialRecord);
				}

				while(rs.next() && (quantity == 0 || (quantity != totalRecordsLoaded))) {
					JsonObject jsonRecord = new JsonObject();

					for(int i=1; i<=columnCount; i++) {
						String columnName = metaData.getColumnName(i);

						jsonRecord.addProperty(columnName, rs.getString(i));
					}

					jsonRecords.add(jsonRecord);
					totalRecordsLoaded++;
				}

				if(rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			finally {
				if(st != null) {
					try {
						st.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
		finally {
			try {
				if(connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return jsonRecords;
	}

	public static JsonArray loadData(JDBCConnectionParameter param, JDBCTableVO tableVO) {
		return loadData(param, tableVO, 0, 0);
	}

	public static void loadFisicModelFields(String url, String user, String password, JDBCTableVO tableVO) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT * FROM ");
		sql.append(tableVO.getName());
		sql.append(" WHERE 1=0 ");
		Connection connection = JDBCConnectionFactory.getJDBCConnection(url, user, password);

		try {
			Statement st = null;

			try {
				st = connection.createStatement();
				ResultSet rs = st.executeQuery(sql.toString());
				ResultSetMetaData metaData = rs.getMetaData();

				int columnCount = metaData.getColumnCount();

				for(int i=1; i<=columnCount; i++) {
					String columnName = metaData.getColumnName(i);
					String columnClassName = metaData.getColumnClassName(i);
					int precision = metaData.getPrecision(i);

					columnName = columnName.toUpperCase();

					JDBCFieldVO fieldVO = new JDBCFieldVO();
					fieldVO.setName(columnName);
					fieldVO.setSize(Double.parseDouble(Integer.toString(precision)));
					fieldVO.setType(columnClassName);

					tableVO.getFields().add(fieldVO);
				}

				if(rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			finally {
				if(st != null) {
					try {
						st.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
		finally {
			try {
				if(connection != null) {
				connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static JDBCDatabaseVO loadFisicModelTables(String url, String user, String password) {
		Connection connection = JDBCConnectionFactory.getJDBCConnection(url, user, password);
		JDBCDatabaseVO databaseByFisicModelVO = new JDBCDatabaseVO();

		if(connection == null) {
			return null;
		}

		try {
			ResultSet tables = connection.getMetaData().getTables(null, "%", "%", new String[] { "TABLE" });

			while (tables.next()) {
				String databaseName = tables.getString("TABLE_CAT");
				String tableName = tables.getString("TABLE_NAME");
				tableName = tableName.toLowerCase();

				databaseByFisicModelVO.setName(databaseName);

				JDBCTableVO tableVO = new JDBCTableVO(tableName);
				databaseByFisicModelVO.addTable(tableVO);
			}

			tables.close();
			connection.close();
		} catch (SQLException e) {
			System.err.println("Error getting all entities from JDBC Database...");
			e.printStackTrace();
		}

		return databaseByFisicModelVO;
	}
}
