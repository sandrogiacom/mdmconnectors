package com.totvslabs.mdm.client.pojo;

import java.util.Arrays;
import java.util.Iterator;

public class StoredDataConsumptionCounterVO extends StoredAbstractVO {
	private static final long serialVersionUID = 1L;
	private String fluigDataName;
	private String datasourceID;
	private String sourceName;//entity
	private Integer counter;

	@Override
	public void cleanFields() {
	}
	@Override
	public Boolean validate() {
		return Boolean.TRUE;
	}
	public String getFluigDataName() {
		return fluigDataName;
	}
	public void setFluigDataName(String fluigDataName) {
		this.fluigDataName = fluigDataName;
		this.updateName();
	}
	public String getDatasourceID() {
		return datasourceID;
	}
	public void setDatasourceID(String datasourceID) {
		this.datasourceID = datasourceID;
		this.updateName();
	}
	public String getSourceName() {
		return sourceName;
	}
	public Integer getCounter() {
		return counter;
	}
	public void setCounter(Integer counter) {
		this.counter = counter;
	}
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
		this.updateName();
	}
	@Override
	public String generateHash() {
		return fluigDataName + "||" + datasourceID + "||" + sourceName;
	}
	private void updateName() {
		this.name = generateHash();
	}
	@Override
	public void setName(String name) {
		if(name == null) {
			this.name = null;
			return;
		}

		Iterator<String> st = Arrays.asList(name.split("\\|\\|")).iterator();
		String datasourceID = null;
		String fluigDataName = null;
		String sourceName = null;
		int counter = 0;

		while(st.hasNext()) {
			String object = st.next();
			String string = (String) object;
			
			if(counter == 0) {
				fluigDataName = string;
			}
			else if(counter == 1) {
				datasourceID = string;
			}
			else if(counter == 2) {
				sourceName = string;
			}

			counter++;
		}

		this.datasourceID = datasourceID;
		this.fluigDataName = fluigDataName;
		this.sourceName = sourceName;
		this.name = generateHash();
	}
}
