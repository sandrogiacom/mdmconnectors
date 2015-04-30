package com.totvslabs.mdm.restclient.vo;

public class AuthVO extends GenericVO {
	private String refresh_token;
	private String access_token;
	private String client_id;
	private Long timeIssuedInMillis;
	private Long expires_in;

	public String getRefresh_token() {
		return refresh_token;
	}
	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	public String getClient_id() {
		return client_id;
	}
	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}
	public Long getTimeIssuedInMillis() {
		return timeIssuedInMillis;
	}
	public void setTimeIssuedInMillis(Long timeIssuedInMillis) {
		this.timeIssuedInMillis = timeIssuedInMillis;
	}
	public Long getExpires_in() {
		return expires_in;
	}
	public void setExpires_in(Long expires_in) {
		this.expires_in = expires_in;
	}
}
