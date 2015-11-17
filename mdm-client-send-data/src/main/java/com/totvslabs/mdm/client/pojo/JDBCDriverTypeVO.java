package com.totvslabs.mdm.client.pojo;

public class JDBCDriverTypeVO {
	private String id;
	private String description;
	private String driverClass;
	private String urlSample;
	private String userSample;
	private String passwordSample;

	public JDBCDriverTypeVO(String id, String description, String driverClass,
			String urlSample, String userSample, String passwordSample) {
		super();
		this.id = id;
		this.description = description;
		this.driverClass = driverClass;
		this.urlSample = urlSample;
		this.userSample = userSample;
		this.passwordSample = passwordSample;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserSample() {
		return userSample;
	}

	public void setUserSample(String userSample) {
		this.userSample = userSample;
	}

	public String getPasswordSample() {
		return passwordSample;
	}

	public void setPasswordSample(String passwordSample) {
		this.passwordSample = passwordSample;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public String getUrlSample() {
		return urlSample;
	}

	public void setUrlSample(String urlSample) {
		this.urlSample = urlSample;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JDBCDriverTypeVO other = (JDBCDriverTypeVO) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return description;
	}
}
