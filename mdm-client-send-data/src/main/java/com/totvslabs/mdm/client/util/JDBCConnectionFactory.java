package com.totvslabs.mdm.client.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.totvslabs.mdm.client.pojo.JDBCDatabaseVO;
import com.totvslabs.mdm.client.pojo.JDBCFieldVO;
import com.totvslabs.mdm.client.pojo.JDBCIndexVO;
import com.totvslabs.mdm.client.pojo.JDBCTableVO;
import com.totvslabs.mdm.client.pojo.StoredJDBCConnectionVO;
import com.totvslabs.mdm.client.ui.SendJDBCDatabaseConnection;

public class JDBCConnectionFactory {
	private static Logger log = Logger.getLogger(JDBCConnectionFactory.class.getCanonicalName());

	private static Connection getJDBCConnection(String url, String driver, String user, String password) {
	    Connection connection = null;
	    Properties connectionProps = new Properties();
	    connectionProps.put("user", user);
	    connectionProps.put("password", password);

	    if(driver != null && driver.trim().length() > 0) {
	    	try {
	    		Class.forName(driver);
	    	} catch (ClassNotFoundException e) {
	    		e.printStackTrace();
	    	}
	    }
	    
        try {
			connection = DriverManager.getConnection(url, connectionProps);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		return connection;
	}

	public static Integer getTotalRecords(StoredJDBCConnectionVO jdbcConnectionVO, JDBCTableVO tableVO) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT count(*) FROM ");
		if(tableVO.getDatabaseName() != null && !jdbcConnectionVO.getDriver().equals(SendJDBCDatabaseConnection.DB_SQLSERVER) && !jdbcConnectionVO.getDriver().equals(SendJDBCDatabaseConnection.DB_PROGRESS)) {
			sql.append(tableVO.getDatabaseName() + ".");
		}
		sql.append(tableVO.getInternalName());

		Connection connection = JDBCConnectionFactory.getJDBCConnection(jdbcConnectionVO.getUrl(), jdbcConnectionVO.getDriver(), jdbcConnectionVO.getUsername(), jdbcConnectionVO.getPassword());
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
				System.err.println("query: " + sql.toString());
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

	public static JsonArray loadData(StoredJDBCConnectionVO jdbcConnectionVO, JDBCTableVO tableVO, int initialRecord, int quantity) {
		JsonArray jsonRecords = new JsonArray();
		StringBuffer sql = new StringBuffer();

		if(jdbcConnectionVO.getDriver().equals(SendJDBCDatabaseConnection.DB_PROGRESS)) {
			StringBuffer fields = new StringBuffer();

			for (int i = 0; i < tableVO.getFields().size(); i++) {
				JDBCFieldVO jdbcFieldVO = tableVO.getFields().get(i);
				
				DecimalFormat df = new DecimalFormat("####");
				Double size = jdbcFieldVO.getSize();
				String type = jdbcFieldVO.getType();
				String name = jdbcFieldVO.getName();

				if(size != null && type.contains("String")) {
					fields.append("substr(\"" + name + "\", 1, " + df.format(size) + ") '" + name + "'");
				}
				else {
					fields.append("\"" + name + "\"");
				}
				
				if(tableVO.getFields().size() > (i+1)) {
					fields.append(", ");
				}
			}

			sql.append("SELECT " + fields.toString() + " FROM ");
		}
		else {
			if(jdbcConnectionVO.getDriver().equals(SendJDBCDatabaseConnection.DB_ORACLE)) {
				sql.append("SELECT rownum as rnum, e.* FROM ");
			}
			else {
				sql.append("SELECT * FROM ");
			}
		}

		if(tableVO.getDatabaseName() != null && !jdbcConnectionVO.getDriver().equals(SendJDBCDatabaseConnection.DB_SQLSERVER) && !jdbcConnectionVO.getDriver().equals(SendJDBCDatabaseConnection.DB_PROGRESS)) {
			sql.append(tableVO.getDatabaseName() + ".");
		}

		sql.append(tableVO.getInternalName() + " e ");

		if(jdbcConnectionVO.getDriver().equals(SendJDBCDatabaseConnection.DB_ORACLE)) {
			sql.append(" where rownum <= " + (initialRecord + quantity));

			StringBuffer sqlNew = new StringBuffer();
			sqlNew.append("SELECT * FROM (");
			sqlNew.append(sql.toString());
			sqlNew.append(") where rnum > " + initialRecord);
			
			sql = sqlNew;
		}

		Connection connection = JDBCConnectionFactory.getJDBCConnection(jdbcConnectionVO.getUrl(), jdbcConnectionVO.getDriver(), jdbcConnectionVO.getUsername(), jdbcConnectionVO.getPassword());

		try {
			Statement st = null;

			try {
				st = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);

				ResultSet rs = st.executeQuery(sql.toString());
				ResultSetMetaData metaData = rs.getMetaData();

				int columnCount = metaData.getColumnCount();
				int totalRecordsLoaded = 0;

				if(!jdbcConnectionVO.getDriver().equals(SendJDBCDatabaseConnection.DB_ORACLE)) {
					if(quantity > 0) {
						st.setMaxRows(quantity);
					}
					
					if(initialRecord > 0) {
						rs.absolute(initialRecord);
					}
				}

				System.out.println("Initial record: " + initialRecord);

				try {
					while(rs.next() && (quantity == 0 || (quantity != totalRecordsLoaded))) {
						JsonObject jsonRecord = new JsonObject();

						for(int i=1; i<=columnCount; i++) {
							String columnName = metaData.getColumnName(i);

							if(tableVO.getField(columnName) != null && tableVO.getField(columnName).getType().equals("oracle.sql.CLOB")) {
								jsonRecord.addProperty(columnName, clobToString(rs.getClob(i)));
							}
							else if(tableVO.getField(columnName) != null && tableVO.getField(columnName).getType().equals("oracle.sql.BLOB")) {
								jsonRecord.addProperty(columnName, blobToString(rs.getBlob(i)));
							}
							else {
								try {
									jsonRecord.addProperty(columnName, rs.getString(i));
								}
								catch(Exception e) {
									e.printStackTrace();
								}
							}
						}

						jsonRecords.add(jsonRecord);
						totalRecordsLoaded++;
					}
				}
				catch(SQLException e) {
					System.err.println("Error loading that record: " + (initialRecord + totalRecordsLoaded));
					e.printStackTrace();
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

	private static String blobToString(Blob blob) {
		if(blob == null) {
			return null;
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];

		InputStream in = null;

		try {
			in = blob.getBinaryStream();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if(in != null) {
			int n = 0;
			try {
				while ((n = in.read(buf)) >= 0) {
					baos.write(buf, 0, n);
				}

				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		byte[] bytes = baos.toByteArray();
		String blobString = new String(bytes);

		return blobString;
	}

	private static String clobToString(Clob data) {
		if(data == null)
			return null;

		final StringBuilder sb = new StringBuilder();

		try {
			final Reader reader = data.getCharacterStream();
			final BufferedReader br = new BufferedReader(reader);

			int b;
			while (-1 != (b = br.read())) {
				sb.append((char) b);
			}

			br.close();
		} catch (SQLException e) {
			log.log(Level.SEVERE, "SQL. Could not convert CLOB to string", e);
			return e.toString();
		} catch (IOException e) {
			log.log(Level.SEVERE, "IO. Could not convert CLOB to string", e);
			return e.toString();
		}

		return sb.toString();
	}

	public static JsonArray loadData(StoredJDBCConnectionVO jdbcConnectionVO, JDBCTableVO tableVO) {
		return loadData(jdbcConnectionVO, tableVO, 0, 0);
	}

	public static void loadFisicModelFields(StoredJDBCConnectionVO jdbcConnectionVO, JDBCTableVO tableVO) {
		JDBCConnectionFactory.loadFisicModelFields(jdbcConnectionVO.getUrl(), jdbcConnectionVO.getDriver(), jdbcConnectionVO.getUsername(), jdbcConnectionVO.getPassword(), tableVO);
	}

	public static void loadFisicModelFields(String url, String driver, String user, String password, JDBCTableVO tableVO) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT * FROM ");
		if(tableVO.getDatabaseName() != null && !driver.equals(SendJDBCDatabaseConnection.DB_SQLSERVER) && !driver.equals(SendJDBCDatabaseConnection.DB_PROGRESS)) {
			sql.append(tableVO.getDatabaseName() + ".");
		}
		sql.append(tableVO.getInternalName());
		sql.append(" WHERE 1=0 ");
		Connection connection = JDBCConnectionFactory.getJDBCConnection(url, driver, user, password);

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
					int precision = Integer.MAX_VALUE;

					if(!columnClassName.equals("oracle.sql.CLOB") && !columnClassName.equals("oracle.sql.BLOB")) {
						try {
							precision = metaData.getPrecision(i);
						}
						catch(Exception e) {
							e.printStackTrace(); 
						}
						finally {}
					}

					JDBCFieldVO fieldVO = new JDBCFieldVO();
					fieldVO.setName(columnName);
					fieldVO.setSize(Double.parseDouble(Integer.toString(precision)));
					fieldVO.setType(columnClassName);

					tableVO.addField(fieldVO);
				}

				if(rs != null) {
					rs.close();
				}

//				ResultSet indexInformation = connection.getMetaData().getIndexInfo(connection.getCatalog(), null, tableVO.getInternalName(), false, true);
				ResultSet primaryKey = connection.getMetaData().getPrimaryKeys(null, null, tableVO.getInternalName());
				Map<String, JDBCIndexVO> mapIndex = new HashMap<String, JDBCIndexVO>();

				try {
					while (primaryKey.next()) {
//						String dbIndexName = indexInformation.getString("INDEX_NAME");
//						String dbColumnName = indexInformation.getString("COLUMN_NAME");
//						Boolean dbUnique = indexInformation.getBoolean("NON_UNIQUE");
						String primaryKeyFeild = primaryKey.getString("COLUMN_NAME");
						String dbIndexName = "PK";

						if(primaryKeyFeild != null) {
							JDBCIndexVO indexVO = mapIndex.get(dbIndexName);

							if(indexVO == null) {
								indexVO = new JDBCIndexVO();
								indexVO.setName(dbIndexName);
								indexVO.setUnique(true);
								mapIndex.put(dbIndexName, indexVO);
							}

							tableVO.getField(primaryKeyFeild).setIdentifier(true);
							indexVO.addField(tableVO.getField(primaryKeyFeild));
						}
					}

					tableVO.setPrimaryKey(mapIndex.values());

					primaryKey.close();
				}
				catch(Exception e) {
					JOptionPane.showMessageDialog(null, "Erro ao carregar dados do banco de dados, por favor, verifique os logs e tente novamente.");
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

	public static JDBCDatabaseVO loadFisicModelTables(String url, String driver, String user, String password) {
		Connection connection = JDBCConnectionFactory.getJDBCConnection(url, driver, user, password);
		JDBCDatabaseVO databaseByFisicModelVO = new JDBCDatabaseVO();

		if(connection == null) {
			return null;
		}

		try {
			ResultSet tables = connection.getMetaData().getTables(null, "%", "%", new String[] { "TABLE" });

			while (tables.next()) {
				String databaseName = tables.getString("TABLE_CAT");
				String tableName = tables.getString("TABLE_NAME");

				//normally for Oracle
				if(databaseName == null) {
					databaseName = tables.getString("TABLE_SCHEM");
				}

				databaseByFisicModelVO.setName(databaseName);

				JDBCTableVO tableVO = new JDBCTableVO(tableName.toLowerCase(), databaseName);
				if(tableName.contains("-")) {
					tableVO.setInternalName("\"" + tableName + "\"");
				}
				else {
					tableVO.setInternalName(tableName);
				}

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
