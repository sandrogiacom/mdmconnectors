package com.totvslabs.mdm.client.util;

import java.util.List;

import com.totvslabs.mdm.client.pojo.StoredAbstractVO;

public abstract class PersistenceEngine {
	protected static PersistenceEngine instance;

	public static final PersistenceEngine getInstance() {
		if(PersistenceEngine.instance == null) {
			PersistenceEngine.instance = new PersistenceEngineDB4O();
		}

		return PersistenceEngine.instance;
	}

	public abstract StoredAbstractVO save(StoredAbstractVO instance);

	public abstract StoredAbstractVO delete(StoredAbstractVO instance);

	public abstract StoredAbstractVO getByName(String name, Class<? extends StoredAbstractVO> clasz);

	public abstract List<StoredAbstractVO> findAll(Class<? extends StoredAbstractVO> clasz);

	public abstract void defragDatabase();
}
