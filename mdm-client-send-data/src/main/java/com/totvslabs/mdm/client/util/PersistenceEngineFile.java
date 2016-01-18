package com.totvslabs.mdm.client.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.totvslabs.mdm.client.pojo.StoredAbstractVO;
import com.totvslabs.mdm.client.pojo.StoredConfigurationVO;
import com.totvslabs.mdm.client.pojo.StoredFluigDataProfileVO;
import com.totvslabs.mdm.client.pojo.StoredJDBCConnectionVO;

public class PersistenceEngineFile {
	static PersistenceEngineFile instance;
	private Map<String, Map<String, StoredAbstractVO>> data = new HashMap<String, Map<String, StoredAbstractVO>>();
	private Map<String, List<StoredConfigurationVO>> storedConfigurationVOByDatasource = new HashMap<String, List<StoredConfigurationVO>>();
	private Map<String, List<StoredConfigurationVO>> storedConfigurationVOByFluigInstance = new HashMap<String, List<StoredConfigurationVO>>();

	@SuppressWarnings("unchecked")
	private PersistenceEngineFile() {
		try {
			FileInputStream fis = new FileInputStream("data/data.ser");
			ObjectInputStream ois = new ObjectInputStream(fis);
			this.data = (HashMap<String, Map<String, StoredAbstractVO>>) ois.readObject();
			ois.close();
			fis.close();

			for(Map<String, StoredAbstractVO> records : this.data.values()) {
				for (StoredAbstractVO record : records.values()) {
					if(record instanceof StoredConfigurationVO) {
						this.generateCache((StoredConfigurationVO) record);
					}
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void generateCache(StoredConfigurationVO record) {
		String keyFluigData = record.getFluigDataName();
		String keyDatasource = record.getDatasourceID();

		List<StoredConfigurationVO> list = storedConfigurationVOByFluigInstance.get(keyFluigData);

		if(list == null) {
			list = new ArrayList<StoredConfigurationVO>();
			storedConfigurationVOByFluigInstance.put(keyFluigData, list);
		}

		list.add((StoredConfigurationVO) record);

		/*###*/

		List<StoredConfigurationVO> listDatasource = storedConfigurationVOByDatasource.get(keyDatasource);

		if(listDatasource == null) {
			listDatasource = new ArrayList<StoredConfigurationVO>();
			storedConfigurationVOByDatasource.put(keyDatasource, listDatasource);
		}

		listDatasource.add((StoredConfigurationVO) record);
	}

	private Map<String, StoredAbstractVO> getSpecificTypeData(Class<? extends StoredAbstractVO> clasz) {
		String key = clasz.getCanonicalName();
		Map<String, StoredAbstractVO> map = this.data.get(key);

		if(map == null) {
			map = new HashMap<String, StoredAbstractVO>();
			this.data.put(key, map);
		}

		return map;
	}

	public StoredAbstractVO save(StoredAbstractVO instance) {
		return this.save(instance, Boolean.TRUE);
	}

	public StoredAbstractVO save(StoredAbstractVO instance, Boolean persist) {
		if(instance == null) {
			return null;
		}

		if(!instance.validate()) {
			return null;
		}

		if(instance instanceof StoredConfigurationVO) {
			this.generateCache((StoredConfigurationVO) instance);
		}

		Map<String, StoredAbstractVO> dataType = this.getSpecificTypeData(instance.getClass());

		dataType.put(instance.getName(), instance);

		if(persist)
			this.persist();

		return instance;
	}

	public void persist() {
		long initialTime = System.currentTimeMillis();
		try {
			FileOutputStream fos = new FileOutputStream("data/data.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(data);
			oos.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("took '" + (System.currentTimeMillis() - initialTime) + "' to save the local file.");
	}

	public StoredAbstractVO delete(StoredAbstractVO instance) {
		if(instance == null) {
			return instance;
		}

		Map<String, StoredAbstractVO> dataType = this.getSpecificTypeData(instance.getClass());

		dataType.remove(instance.getName());

		if(instance instanceof StoredFluigDataProfileVO) {
			List<StoredConfigurationVO> list = this.storedConfigurationVOByFluigInstance.get(((StoredFluigDataProfileVO) instance).getProfileName());

			if (list != null && !list.isEmpty()) {
				for (StoredConfigurationVO storedConfigurationVO : list) {
					this.delete(storedConfigurationVO);
				}
			}
		}
		else if(instance instanceof StoredJDBCConnectionVO) {
			List<StoredConfigurationVO> list = this.storedConfigurationVOByDatasource.get(((StoredJDBCConnectionVO) instance).getProfileName());

			if (list != null && !list.isEmpty()) {
				for (StoredConfigurationVO storedConfigurationVO : list) {
					this.delete(storedConfigurationVO);
				}
			}			
		}

		this.persist();

		return instance;
	}

	public StoredAbstractVO getByName(String name, Class<? extends StoredAbstractVO> clasz) {
		Map<String, StoredAbstractVO> dataType = this.getSpecificTypeData(clasz);
		
		StoredAbstractVO storedAbstractVO = dataType.get(name);
		
		return storedAbstractVO;
	}

	public List<StoredAbstractVO> findAll(Class<? extends StoredAbstractVO> clasz) {
		Map<String, StoredAbstractVO> dataType = this.getSpecificTypeData(clasz);

		return new ArrayList<StoredAbstractVO>(dataType.values());
	}
}
