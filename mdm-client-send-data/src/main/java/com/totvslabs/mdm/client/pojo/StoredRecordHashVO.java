package com.totvslabs.mdm.client.pojo;

public class StoredRecordHashVO extends StoredAbstractVO {
	private static final long serialVersionUID = 1L;
	private String fluigDataProfile;
	private String entityName;
	private String hash;

	public StoredRecordHashVO(String fluigDataProfile, String entityName, String hash) {
		super();
		this.fluigDataProfile = fluigDataProfile;
		this.entityName = entityName;
		this.hash = hash;
	}

	public String getFluigDataProfile() {
		return fluigDataProfile;
	}
	public void setFluigDataProfile(String fluigDataProfile) {
		this.fluigDataProfile = fluigDataProfile;
	}
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	@Override
	public String getName() {
		return fluigDataProfile + entityName + hash;
	}
	@Override
	public Boolean validate() {
		return Boolean.TRUE;
	}
}
