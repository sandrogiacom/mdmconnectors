package com.totvslabs.mdm.client.pojo;

public class StoredJDBCConnectionVO extends StoredAbstractVO {
	private static final long serialVersionUID = 1L;
	private String profileName;
	private String driver;
	private String url;
	private String username;
	private String password;

	@Override
	public void cleanFields() {
	}
	@Override
	public Boolean validate() {
		return Boolean.TRUE;
	}
	public String getProfileName() {
		return profileName;
	}
	public void setProfileName(String profileName) {
		this.profileName = profileName;
		this.name = profileName;
	}
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
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
	@Override 
	public void setName(String name) {
		this.profileName = name;
		this.name = this.generateHash();
	}
	@Override
	public String generateHash() {
		return profileName;
	}
	@Override
	public String toString() {
		return "StoredJDBCConnectionVO [profileName=" + profileName
				+ ", driver=" + driver + ", url=" + url + ", username="
				+ username + ", password=" + password + "]";
	}
}
