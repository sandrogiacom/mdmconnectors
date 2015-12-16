package com.totvslabs.mdm.client.pojo;

import java.util.StringTokenizer;

public class StoredRecordHashVO extends StoredAbstractVO {
	private static final long serialVersionUID = 1L;
	private String fluigDataProfile;
	private String jdbcConnection;
	private String entityName;
	private String hash;

	public StoredRecordHashVO() {}
	public StoredRecordHashVO(String fluigDataProfile, String jdbcConnection, String entityName, String hash) {
		super();
		this.fluigDataProfile = fluigDataProfile;
		this.jdbcConnection = jdbcConnection;
		this.entityName = entityName;
		this.hash = hash;
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
	public String getHash() {
		return hash;
	}
	public String getJdbcConnection() {
		return jdbcConnection;
	}
	public void setJdbcConnection(String jdbcConnection) {
		this.jdbcConnection = jdbcConnection;
	}
	public void setHash(String hash) {
		this.hash = hash;
		this.updateName();
	}
	@Override
	public String generateHash() {
		return fluigDataProfile + "||" + jdbcConnection + "||" + entityName + "||" + hash;
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
		String hash = null;
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
			else if(counter == 3) {
				hash = string;
			}

			counter++;
		}

		this.fluigDataProfile = fluigDataProfile;
		this.jdbcConnection = jdbcConnection;
		this.entityName = entityName;
		this.hash = hash;
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
