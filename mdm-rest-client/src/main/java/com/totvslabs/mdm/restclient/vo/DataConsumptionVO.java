package com.totvslabs.mdm.restclient.vo;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

/**
 * Data Consumption POJO
 *
 */
public class DataConsumptionVO extends GenericVO {
	public DataConsumptionVO() {}
	public DataConsumptionVO(String object) {
		Gson gson = new Gson(); 
		Map<String,DataConsumptionEntitiesRecordVO> map = new HashMap<String,DataConsumptionEntitiesRecordVO>();
		map = (Map<String,DataConsumptionEntitiesRecordVO>) gson.fromJson(object, map.getClass());

		this.entitiesRecords = map;
	}
	private Map<String, DataConsumptionEntitiesRecordVO> entitiesRecords;

	public Map<String, DataConsumptionEntitiesRecordVO> getEntitiesRecords() {
		return entitiesRecords;
	}
	public void setEntitiesRecords(Map<String, DataConsumptionEntitiesRecordVO> entitiesRecords) {
		this.entitiesRecords = entitiesRecords;
	}
}
