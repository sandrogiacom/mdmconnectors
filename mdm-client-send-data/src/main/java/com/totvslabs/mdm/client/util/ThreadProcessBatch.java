package com.totvslabs.mdm.client.util;

import java.util.List;

import com.totvslabs.mdm.client.pojo.JDBCTableVO;
import com.totvslabs.mdm.client.pojo.StoredAbstractVO;
import com.totvslabs.mdm.client.pojo.StoredConfigurationVO;
import com.totvslabs.mdm.client.pojo.StoredFluigDataProfileVO;
import com.totvslabs.mdm.client.pojo.StoredJDBCConnectionVO;
import com.totvslabs.mdm.restclient.MDMRestAuthentication;
import com.totvslabs.mdm.restclient.MDMRestConnectionFactory;

public class ThreadProcessBatch implements Runnable {
	@Override
	public void run() {
		List<StoredAbstractVO> batchConfiguration = PersistenceEngine.getInstance().findAll(StoredConfigurationVO.class);

		for (StoredAbstractVO storedAbstractVO : batchConfiguration) {
			long initialTime = System.currentTimeMillis();
			StoredConfigurationVO conf = (StoredConfigurationVO) storedAbstractVO;
			StoredJDBCConnectionVO jdbcConnection = (StoredJDBCConnectionVO) PersistenceEngine.getInstance().getByName(conf.getDatasourceID(), StoredJDBCConnectionVO.class);
			StoredFluigDataProfileVO fluigProfile = (StoredFluigDataProfileVO) PersistenceEngine.getInstance().getByName(conf.getFluigDataName(), StoredFluigDataProfileVO.class);

			MDMRestAuthentication.getInstance(fluigProfile.getServerURL(), fluigProfile.getDomain(), fluigProfile.getDatasourceID(), fluigProfile.getUsername(), fluigProfile.getPassword());
			MDMRestConnectionFactory.getConnection(fluigProfile.getServerURL());

			JDBCTableVO tableVO = new JDBCTableVO(conf.getSourceName());

			Integer totalRecords = JDBCConnectionFactory.getTotalRecords(jdbcConnection, tableVO);
			JDBCConnectionFactory.loadFisicModelFields(jdbcConnection, tableVO);

			tableVO.setTotalRecords(totalRecords);

			/* Running as processor not thread */
			ThreadExportData thread = new ThreadExportData(fluigProfile, tableVO, jdbcConnection);
			thread.run();
			System.out.println("took '" + (System.currentTimeMillis() - initialTime) + "' ms to run the process for " + conf.getName());
		}
	}
}
