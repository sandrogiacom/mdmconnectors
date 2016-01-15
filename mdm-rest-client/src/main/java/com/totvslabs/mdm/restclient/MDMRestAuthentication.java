package com.totvslabs.mdm.restclient;

import java.util.Calendar;
import java.util.List;

import com.totvslabs.mdm.restclient.command.CommandAuth;
import com.totvslabs.mdm.restclient.command.CommandRefreshToken;
import com.totvslabs.mdm.restclient.vo.AuthVO;
import com.totvslabs.mdm.restclient.vo.EnvelopeVO;
import com.totvslabs.mdm.restclient.vo.GenericVO;
import com.totvslabs.mdm.restclient.vo.RefreshTokenVO;

/**
 * Authentication object to consume MDM REST APIs
 */
public class MDMRestAuthentication {
	private static MDMRestAuthentication instance;
	private String mdmURL;
	private AuthVO authVO;
	private Calendar experiedCalend = Calendar.getInstance();

	private MDMRestAuthentication(String mdmUrl, String refreshToken, String accessToken, String clientId, Long timeIssuedMs, Long expiresInSeconds) {
		this.authVO = new AuthVO();

		this.mdmURL = mdmUrl;
		this.authVO.setRefresh_token(refreshToken);
		this.authVO.setAccess_token(accessToken);
		this.authVO.setClient_id(clientId);
		this.authVO.setTimeIssuedInMillis(timeIssuedMs);
		this.authVO.setExpires_in(expiresInSeconds);

		this.updateIssueCalend(timeIssuedMs, expiresInSeconds);
	}

	private void updateIssueCalend(Long timeIssuedMs, Long expiresInSeconds) {
		experiedCalend.setTimeInMillis(timeIssuedMs);
		experiedCalend.add(Calendar.SECOND, expiresInSeconds.intValue());
	}

	public static MDMRestAuthentication getInstance(String mdmURL, String subdomain, String datasourceId, String username, String password) {
		return getInstance(mdmURL, subdomain, datasourceId, username, password, false);
	}

	public static MDMRestAuthentication getInstance(String mdmURL, String subdomain, String datasourceId, String username, String password, boolean forceAuth) {
		if (forceAuth) {
			instance = null;
		}

		if(instance == null) {
			AuthVO authVO = MDMRestAuthentication.authorization(mdmURL, subdomain, datasourceId, username, password);
			instance = new MDMRestAuthentication(mdmURL, authVO.getRefresh_token(), authVO.getAccess_token(), authVO.getClient_id(), authVO.getTimeIssuedInMillis(), authVO.getExpires_in());
		}
		else {
			instance.refreshToken();
		}

		return instance;
	}

	public static MDMRestAuthentication getInstance(String mdmURL, String clientId, String accessToken, String refreshToken, Long timeIssuedInMillis, Long experiesIn) {
		instance = new MDMRestAuthentication(mdmURL, refreshToken, accessToken, clientId, timeIssuedInMillis, experiesIn);

		return instance;
	}

	public static MDMRestAuthentication getInstance() {
		if (instance == null) {
			throw new RuntimeException("Authentication is required to use this operation.");
		}
		
		instance.refreshToken();

		return instance;
	}

	private static AuthVO authorization(String mdmURL, String subdomain, String datasourceId, String username, String password) {
		MDMRestConnection connection = MDMRestConnectionFactory.getConnection(mdmURL);
		EnvelopeVO authResult = connection.executeCommand(new CommandAuth(subdomain, datasourceId, username, password));
		List<GenericVO> authsVO = authResult.getHits();
		AuthVO authVO = (AuthVO) authsVO.get(0);

		return authVO;
	}

	private void refreshToken() {
		if(Calendar.getInstance().after(this.experiedCalend)) {
			MDMRestConnection connection = MDMRestConnectionFactory.getConnection(mdmURL);
			EnvelopeVO authResult = connection.executeCommand(new CommandRefreshToken(authVO.getRefresh_token()));
			List<GenericVO> authsVO = authResult.getHits();
			RefreshTokenVO refreshTokenVO = (RefreshTokenVO) authsVO.get(0);

			this.authVO.setAccess_token(refreshTokenVO.getAccess_token());
			this.authVO.setClient_id(refreshTokenVO.getClient_id());
			this.authVO.setTimeIssuedInMillis(refreshTokenVO.getTimeIssuedInMillis());
			this.authVO.setExpires_in(refreshTokenVO.getExpires_in());

			this.updateIssueCalend(refreshTokenVO.getTimeIssuedInMillis(), refreshTokenVO.getExpires_in());
		}
	}


	public void invalidateAuthVO() {
		this.authVO = null;
	}

	public AuthVO getAuthVO() {
		return authVO;
	}
}
