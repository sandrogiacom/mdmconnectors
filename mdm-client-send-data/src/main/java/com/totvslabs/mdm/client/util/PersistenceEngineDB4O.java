package com.totvslabs.mdm.client.util;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.db4o.DatabaseClosedException;
import com.db4o.Db4o;
import com.db4o.Db4oIOException;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.constraints.UniqueFieldValueConstraint;
import com.db4o.ext.Db4oException;
import com.totvslabs.mdm.client.pojo.StoredAbstractVO;
import com.totvslabs.mdm.client.pojo.StoredConfigurationVO;
import com.totvslabs.mdm.client.pojo.StoredFluigDataProfileVO;
import com.totvslabs.mdm.client.pojo.StoredJDBCConnectionVO;
import com.totvslabs.mdm.client.pojo.StoredRecordHashVO;

public class PersistenceEngineDB4O extends PersistenceEngine {
	private static final Logger LOGGER = Logger.getLogger( PersistenceEngineDB4O.class.getName() );
	private ObjectContainer db;
	private String location;

	protected PersistenceEngineDB4O() {
		this.location = "data/data.yap";
		this.open();
	}

	private synchronized void open() {
		try {
			Configuration configuration = Db4o.newConfiguration();
			configuration.setOut(System.out);
			configuration.messageLevel(1);
			configuration.lockDatabaseFile(false);
			configuration.add(new UniqueFieldValueConstraint(StoredRecordHashVO.class,"name"));
			configuration.add(new UniqueFieldValueConstraint(StoredAbstractVO.class,"name"));
			configuration.objectClass(StoredAbstractVO.class).objectField("name").indexed(true);
			configuration.objectClass(StoredRecordHashVO.class).objectField("name").indexed(true);
			configuration.objectClass(StoredRecordHashVO.class).objectField("fluigDataProfile").indexed(true);
			configuration.objectClass(StoredRecordHashVO.class).objectField("jdbcConnection").indexed(true);
			configuration.objectClass(StoredConfigurationVO.class).objectField("fluigDataName").indexed(true);
			configuration.objectClass(StoredConfigurationVO.class).objectField("datasourceID").indexed(true);

			this.db = Db4o.openFile(configuration, this.location );
		} catch( ArrayIndexOutOfBoundsException e) {
			this.workArround();
		} catch ( Db4oException de ){
			this.workArround();
		} catch ( Exception e ) {
			LOGGER.log(Level.SEVERE, "Erro abrindo banco DB4O -> path: " + this.location, e );
		}
	}

	private void workArround() {
		File directory = new File("data");
		File files[] = directory.listFiles();
		if(files != null){
			for (File file : files) {
				String fileName = file.getName();
				if(fileName.length() > 4 && fileName.substring(fileName.length() - 4, fileName.length()).equalsIgnoreCase(".yap")){
					file.delete();
				}
			}
		}
		else{
			LOGGER.log(Level.SEVERE, "Data file not found: " + this.location);
		}
		LOGGER.log(Level.SEVERE, "Error opening the data file, please try again or try to repair the data file: " + this.location);
		this.open();
	}

	public synchronized void close() {
		try {
			this.db.close();
		} catch ( Exception e ) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e );
		} finally {
			this.db = null;
			instance = null;
		}
	}

	public StoredAbstractVO save(StoredAbstractVO instance) {
		long initialTime = System.currentTimeMillis();

		if(instance == null || instance.getName() == null) {
			return null;
		}

		StoredAbstractVO tempObj = this.getByName(instance.getName(), instance.getClass());
		if(tempObj != null && tempObj.getName().equals(instance.getName())) {
			this.delete(tempObj);
		}

		if(!instance.validate()) {
			return null;
		}

		try {
			this.db.set( instance );
			this.db.commit();
		} catch ( Exception e ) {
			LOGGER.log(Level.SEVERE,"Error when saving records: " + instance.toString(), e );
			this.close();
		}

		LOGGER.log(Level.INFO, "Took '" + (System.currentTimeMillis() - initialTime) + "' to save data.");

		return instance;
	}

	public StoredAbstractVO delete(StoredAbstractVO instance) {
		long initialTime = System.currentTimeMillis();
		if(instance == null) {
			return instance;
		}

		if(instance instanceof StoredFluigDataProfileVO) {
			StoredConfigurationVO trashDataSC = new StoredConfigurationVO();
			trashDataSC.setFluigDataName(instance.getName());
			trashDataSC.setName(null);
			StoredRecordHashVO trashRecord = new StoredRecordHashVO();
			trashRecord.setFluigDataProfile(instance.getName());;
			trashRecord.setName(null);
			
			this.searchAndDelete(trashRecord);
			this.searchAndDelete(trashDataSC);
		}
		else if(instance instanceof StoredJDBCConnectionVO) {
			StoredConfigurationVO trashData = new StoredConfigurationVO();
			trashData.setDatasourceID(instance.getName());
			trashData.setName(null);
			StoredRecordHashVO trashRecord = new StoredRecordHashVO();
			trashRecord.setJdbcConnection(instance.getName());;
			trashRecord.setName(null);

			this.searchAndDelete(trashRecord);
			this.searchAndDelete(trashData);
		}

		this.db.delete(instance);
		this.db.commit();

		LOGGER.log(Level.INFO, "Took '" + (System.currentTimeMillis() - initialTime) + "' to delete data.");

		return instance;
	}

	private void searchAndDelete(StoredAbstractVO instance) {
		ObjectSet<StoredAbstractVO> querySet = this.db.get(instance);

		if(querySet != null && querySet.size() >= 0) {
			for(int i=0; i<querySet.size(); i++) {
				this.delete(querySet.get(i));
			}
		}
	}

	public StoredAbstractVO getByName(final String name, Class<? extends StoredAbstractVO> clasz) {
		long initialTime = System.currentTimeMillis();

		StoredAbstractVO instance = null;
		try {
			instance = clasz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

		instance.cleanFields();
		instance.setName(name);

		ObjectSet<StoredAbstractVO> objectSet = db.get(instance);

        if(objectSet != null && !objectSet.isEmpty()) {
    		LOGGER.log(Level.INFO, "Took '" + (System.currentTimeMillis() - initialTime) + "' to find data '" + objectSet.size() + "'.");

        	return objectSet.get(0);
        }

		LOGGER.log(Level.INFO, "Took '" + (System.currentTimeMillis() - initialTime) + "' to find data (no records).");

		return null;
	}

	@SuppressWarnings("unchecked")
	public List<StoredAbstractVO> findAll(Class<? extends StoredAbstractVO> clasz) {
		long initialTime = System.currentTimeMillis();

		try {
			ObjectSet<StoredAbstractVO> objectSet = (ObjectSet<StoredAbstractVO>) this.db.query(clasz);

			if(objectSet.size() > 0) {
				List<StoredAbstractVO> subList = objectSet.subList(0, objectSet.size());

				if(subList != null) {
					for (StoredAbstractVO storedAbstractVO : subList) {
						System.out.println(storedAbstractVO);
					}
				}

				LOGGER.log(Level.INFO, "Took '" + (System.currentTimeMillis() - initialTime) + "' to findAll records (" + clasz + ") - found '" + objectSet.size() + "' records.");

				return objectSet.subList(0, objectSet.size());
			}
		} catch (Db4oIOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		} catch (DatabaseClosedException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}

		LOGGER.log(Level.INFO, "Took '" + (System.currentTimeMillis() - initialTime) + "' to findAll records (" + clasz + ") - found no records.");

		return null;
	}
}
