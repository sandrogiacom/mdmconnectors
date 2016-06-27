package com.totvslabs.mdm.client.util;

import java.util.List;

import com.totvslabs.mdm.client.pojo.JDBCDatabaseVO;
import com.totvslabs.mdm.client.pojo.JDBCTableVO;
import com.totvslabs.mdm.client.pojo.StoredAbstractVO;
import com.totvslabs.mdm.client.pojo.StoredConfigurationVO;
import com.totvslabs.mdm.client.pojo.StoredFluigDataProfileVO;
import com.totvslabs.mdm.client.pojo.StoredJDBCConnectionVO;
import com.totvslabs.mdm.restclient.MDMRestAuthentication;
import com.totvslabs.mdm.restclient.MDMRestConnectionFactory;
import com.totvslabs.mdm.restclient.MDMRestConnectionTypeEnum;

public class ThreadProcessBatch implements Runnable {
	@Override
	public void run() {
		List<StoredAbstractVO> batchConfiguration = PersistenceEngine.getInstance().findAll(StoredConfigurationVO.class);

		for (StoredAbstractVO storedAbstractVO : batchConfiguration) {
			StoredConfigurationVO conf = (StoredConfigurationVO) storedAbstractVO;
			StoredJDBCConnectionVO jdbcConnection = (StoredJDBCConnectionVO) PersistenceEngine.getInstance().getByName(conf.getDatasourceID(), StoredJDBCConnectionVO.class);
			StoredFluigDataProfileVO fluigProfile = (StoredFluigDataProfileVO) PersistenceEngine.getInstance().getByName(conf.getFluigDataName(), StoredFluigDataProfileVO.class);

			JDBCDatabaseVO database = DBConnectionFactory.getDb(jdbcConnection.getDriver()).loadFisicModelTables(jdbcConnection.getUrl(), jdbcConnection.getDriver(), jdbcConnection.getUsername(), jdbcConnection.getPassword());

			MDMRestAuthentication.getInstance(MDMRestConnectionTypeEnum.NORMAL, fluigProfile.getServerURL(), fluigProfile.getDomain(), fluigProfile.getDatasourceID(), fluigProfile.getUsername(), fluigProfile.getPassword());
			MDMRestConnectionFactory.getConnection(fluigProfile.getServerURL());

			JDBCTableVO tableVO = database.getTable(conf.getSourceName());

			Long totalRecords = DBConnectionFactory.getDb(jdbcConnection.getDriver()).getTotalRecords(jdbcConnection, tableVO);
			DBConnectionFactory.getDb(jdbcConnection.getDriver()).loadFisicModelFields(jdbcConnection, tableVO);

			tableVO.setTotalRecords(totalRecords);

			/* Running as process not thread */
			Thread thread = new Thread(new ThreadExportData(conf, fluigProfile, tableVO, jdbcConnection));
			thread.start();
		}
	}
}
