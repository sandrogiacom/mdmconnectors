package com.totvslabs.mdm.client.pojo;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class StoredRecordHashVO extends StoredAbstractVO {
	private static final long serialVersionUID = 1L;
	private String fluigDataProfile;
	private String jdbcConnection;
	private String entityName;
	private Set<String> recordsHash;

	public StoredRecordHashVO() {}
	public StoredRecordHashVO(String fluigDataProfile, String jdbcConnection, String entityName) {
		super();
		this.fluigDataProfile = fluigDataProfile;
		this.jdbcConnection = jdbcConnection;
		this.entityName = entityName;
		this.recordsHash = new LinkedHashSet<String>();
		this.updateName();
	}

	@Override
	public void cleanFields() {
	}
	public String getFluigDataProfile() {
		return fluigDataProfile;
	}
	public void setFluigDataProfile(String fluigDataProfile) {
		this.fluigDataProfile = fluigDataProfile;
		this.updateName();
	}
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
		this.updateName();
	}
	public Set<String> getRecordsHash() {
		return recordsHash;
	}
	public String getJdbcConnection() {
		return jdbcConnection;
	}
	public void setJdbcConnection(String jdbcConnection) {
		this.jdbcConnection = jdbcConnection;
	}
	public void setHash(Set<String> recordsHash) {
		this.recordsHash = recordsHash;
	}
	@Override
	public String generateHash() {
		return fluigDataProfile + "||" + jdbcConnection + "||" + entityName;
	}
	@Override
	public void setName(String name) {
		if(name == null) {
			this.name = null;
			return;
		}

		StringTokenizer st = new StringTokenizer(name, "||");
		String fluigDataProfile = null;
		String jdbcConnection = null;
		String entityName = null;
		int counter = 0;

		while(st.hasMoreElements()) {
			String string = (String) st.nextElement();

			if(counter == 0) {
				fluigDataProfile = string;
			}
			else if(counter == 1) {
				jdbcConnection = string;
			}
			else if(counter == 2) {
				entityName = string;
			}

			counter++;
		}

		this.fluigDataProfile = fluigDataProfile;
		this.jdbcConnection = jdbcConnection;
		this.entityName = entityName;
		this.name = generateHash();
	}
	private void updateName() {
		this.name = generateHash();
	}
	@Override
	public Boolean validate() {
		return Boolean.TRUE;
	}
}
