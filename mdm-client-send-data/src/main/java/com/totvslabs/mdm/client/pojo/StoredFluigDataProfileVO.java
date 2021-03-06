package com.totvslabs.mdm.client.pojo;

public class StoredFluigDataProfileVO extends StoredAbstractVO {
	private static final long serialVersionUID = 1L;
	private String profileName;
	private String serverURL = "https://totvslabs.fluigdata.com:8443/";
	private String domain = "totvslabs";
	private String datasourceID = "0b672ec08cbc11e5991b0242ac110002";
	private String username = "admin@totvslabs.com";
	private String password = "Foobar1!";

	@Override
	public void cleanFields() {
		profileName = null;
		serverURL = null;
		domain = null;
		datasourceID = null;
		username = null;
		password = null;
	}
	@Override
	public Boolean validate() {
		return Boolean.TRUE;
	}
	@Override
	public String generateHash() {
		return this.name;
	}
	@Override
	public void setName(String name) {
		this.profileName = name;
		this.name = generateHash();
	}
	public String getProfileName() {
		return profileName;
	}
	public void setProfileName(String profileName) {
		this.profileName = profileName;
		this.name = profileName;
	}
	public String getServerURL() {
		return serverURL;
	}
	public void setServerURL(String serverURL) {
		this.serverURL = serverURL;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getDatasourceID() {
		return datasourceID;
	}
	public void setDatasourceID(String datasourceID) {
		this.datasourceID = datasourceID;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
