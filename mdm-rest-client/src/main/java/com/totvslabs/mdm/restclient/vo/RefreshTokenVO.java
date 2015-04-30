package com.totvslabs.mdm.restclient.vo;

public class RefreshTokenVO extends GenericVO {
	private String access_token;
	private String client_id;
	private Long timeIssuedInMillis;
	private Long expires_in;
	private String token_type;

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
	public String getToken_type() {
		return token_type;
	}
	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}
}
