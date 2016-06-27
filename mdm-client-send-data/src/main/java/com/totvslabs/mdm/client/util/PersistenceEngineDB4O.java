package com.totvslabs.mdm.client.util;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.db4o.DatabaseClosedException;
import com.db4o.Db4o;
import com.db4o.Db4oIOException;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.defragment.AvailableClassFilter;
import com.db4o.defragment.Defragment;
import com.db4o.defragment.DefragmentConfig;
import com.db4o.ext.Db4oException;
import com.totvslabs.mdm.client.pojo.StoredAbstractVO;
import com.totvslabs.mdm.client.pojo.StoredConfigurationVO;
import com.totvslabs.mdm.client.pojo.StoredDataConsumptionCounterVO;
import com.totvslabs.mdm.client.pojo.StoredFluigDataProfileVO;
import com.totvslabs.mdm.client.pojo.StoredJDBCConnectionVO;
import com.totvslabs.mdm.client.pojo.StoredRecordHashVO;

public class PersistenceEngineDB4O extends PersistenceEngine implements Runnable {
	private static final Logger LOGGER = Logger.getLogger( PersistenceEngineDB4O.class.getName() );
	private ObjectContainer db;
	private String location;
	private Boolean isClose = Boolean.TRUE;
	private Boolean wasChanged = Boolean.FALSE;
	private Thread threadCommit;

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

			configuration.objectClass(StoredRecordHashVO.class).updateDepth(2);

			configuration.objectClass(StoredAbstractVO.class).objectField("name").indexed(true);
			configuration.objectClass(StoredJDBCConnectionVO.class).objectField("name").indexed(true);
			configuration.objectClass(StoredFluigDataProfileVO.class).objectField("name").indexed(true);
			configuration.objectClass(StoredDataConsumptionCounterVO.class).objectField("name").indexed(true);
			configuration.objectClass(StoredRecordHashVO.class).objectField("name").indexed(true);
			configuration.objectClass(StoredRecordHashVO.class).objectField("fluigDataProfile").indexed(true);
			configuration.objectClass(StoredRecordHashVO.class).objectField("jdbcConnection").indexed(true);
			configuration.objectClass(StoredConfigurationVO.class).objectField("fluigDataName").indexed(true);
			configuration.objectClass(StoredConfigurationVO.class).objectField("datasourceID").indexed(true);

			this.db = Db4o.openFile(configuration, this.location);

			this.threadCommit = new Thread(this);
			this.threadCommit.start();
		} catch( ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		} catch (Db4oException e) {
			e.printStackTrace();
		} catch ( Exception e ) {
			LOGGER.log(Level.SEVERE, "Error when oppening the db4o file: path ---> " + this.location, e );
		}
	}

	public synchronized void close() {
		try {
			this.db.close();
			this.isClose = Boolean.TRUE;
		} catch ( Exception e ) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e );
		} finally {
			this.db = null;
			instance = null;
		}
	}

	@Override
	public void run() {
		while(!this.isClose) {
			try {
				if(this.wasChanged) {
					long initialTime = System.currentTimeMillis();
					this.db.commit();
					this.wasChanged = Boolean.FALSE;					
					LOGGER.log(Level.INFO, "--> |COMMIT THREAD| It took '" + (System.currentTimeMillis() - initialTime) + "' ms to commit the session.");
				}

				Thread.sleep(50000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public StoredAbstractVO save(StoredAbstractVO instance) {
		long initialTime = System.currentTimeMillis();

		if(instance == null || instance.getName() == null) {
			return null;
		}

		long partialTime = System.currentTimeMillis();
		StoredAbstractVO tempObj = this.getByName(instance.getName(), instance.getClass());

		LOGGER.log(Level.INFO, "--> |SAVE| Partial time to find the record: " + (System.currentTimeMillis()-partialTime));

		if(tempObj != null && tempObj.getName().equals(instance.getName())) {
			partialTime = System.currentTimeMillis();
			this.delete(tempObj);

			LOGGER.log(Level.INFO, "--> |SAVE| Partial time to delete the record (deleting because it exists before save the actual instance): " + (System.currentTimeMillis()-partialTime));
		}

		if(!instance.validate()) {
			return null;
		}

		try {
			partialTime = System.currentTimeMillis();
			this.db.set( instance );
			this.wasChanged = Boolean.TRUE;

			LOGGER.log(Level.INFO, "--> |SAVE| Partial time to set the record in Db4o: " + (System.currentTimeMillis()-partialTime));
		} catch ( Exception e ) {
			LOGGER.log(Level.SEVERE,"|SAVE| Error when saving record: " + instance.toString(), e );
			this.close();
		}

		LOGGER.log(Level.INFO, "--> |SAVE| It took '" + (System.currentTimeMillis() - initialTime) + "' ms to save the record.");

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
		this.wasChanged = Boolean.TRUE;

		LOGGER.log(Level.INFO, "--> |DELETE| It took '" + (System.currentTimeMillis() - initialTime) + "' ms to delete the record.");

		return instance;
	}

	private void searchAndDelete(StoredAbstractVO instance) {
		ObjectSet<StoredAbstractVO> querySet = this.db.get(instance);

		while(querySet.hasNext()) {
			this.delete(querySet.next());
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
    		LOGGER.log(Level.INFO, "--> |FIND_NAME| It took '" + (System.currentTimeMillis() - initialTime) + "' ms to find '" + objectSet.size() + "' record(s).");

        	return objectSet.get(0);
        }

		LOGGER.log(Level.INFO, "--> |FIND_NAME| It took '" + (System.currentTimeMillis() - initialTime) + "' ms to find no records.");

		return null;
	}

	@SuppressWarnings("unchecked")
	public List<StoredAbstractVO> findAll(Class<? extends StoredAbstractVO> clasz) {
		long initialTime = System.currentTimeMillis();

		try {
			ObjectSet<StoredAbstractVO> objectSet = (ObjectSet<StoredAbstractVO>) this.db.query(clasz);

			if (!objectSet.isEmpty()) {
				List<StoredAbstractVO> subList = objectSet.subList(0, objectSet.size());

				LOGGER.log(Level.INFO, "--> |FIND_ALL| It took '" + (System.currentTimeMillis() - initialTime) + "' ms to find all records of '" + clasz + "'. It found '" + objectSet.size() + "' records.");

				return subList;
			}
		} catch (Db4oIOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		} catch (DatabaseClosedException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}

		LOGGER.log(Level.INFO, "--> |FIND_ALL| It took '" + (System.currentTimeMillis() - initialTime) + "' ms to find no records of '" + clasz + "'.");

		return null;
	}

	@Override
	public void defragDatabase() {
	    try {
	        DefragmentConfig config = new DefragmentConfig(this.location, this.location + ".bak");
	        config.storedClassFilter(new AvailableClassFilter());
	        config.forceBackupDelete(false);
	        config.objectCommitFrequency(1000);
	        Defragment.defrag(config);
	        System.out.println("Defrag completed");
	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }
	}
}
