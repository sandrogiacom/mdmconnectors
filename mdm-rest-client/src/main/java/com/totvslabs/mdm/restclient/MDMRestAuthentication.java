package com.totvslabs.mdm.restclient;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private static Map<MDMRestConnectionTypeEnum, MDMRestAuthentication> cache = new HashMap<MDMRestConnectionTypeEnum, MDMRestAuthentication>();
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

	private void updateIssueCalend(Long timeIssuedMs, Long expiresInSeconds) {//experiedCalend.getTime()
		experiedCalend.setTimeInMillis(timeIssuedMs);
		experiedCalend.add(Calendar.SECOND, expiresInSeconds.intValue());
	}

	public static MDMRestAuthentication getInstance(MDMRestConnectionTypeEnum type, String mdmURL, String subdomain, String datasourceId, String username, String password) {
		MDMRestAuthentication mdmRestAuthentication = cache.get(type);

		if((mdmRestAuthentication == null) || (mdmRestAuthentication != null && !mdmRestAuthentication.getAuthVO().getClient_id().equals(datasourceId))) {
			AuthVO authVO = MDMRestAuthentication.authorization(mdmURL, subdomain, datasourceId, username, password);
			mdmRestAuthentication = new MDMRestAuthentication(mdmURL, authVO.getRefresh_token(), authVO.getAccess_token(), authVO.getClient_id(), Calendar.getInstance().getTimeInMillis(), authVO.getExpires_in());
			cache.put(type, mdmRestAuthentication);
		}
		else {
			mdmRestAuthentication.refreshToken();
		}

		return mdmRestAuthentication;
	}

	public static MDMRestAuthentication getInstance(MDMRestConnectionTypeEnum type, String mdmURL, String clientId, String accessToken, String refreshToken, Long timeIssuedInMillis, Long experiesIn) {
		MDMRestAuthentication mdmRestAuthentication = cache.get(type);

		if((mdmRestAuthentication == null) || (mdmRestAuthentication != null && !mdmRestAuthentication.getAuthVO().getClient_id().equals(clientId))) {
			mdmRestAuthentication = new MDMRestAuthentication(mdmURL, refreshToken, accessToken, clientId, timeIssuedInMillis, experiesIn);
			cache.put(type, mdmRestAuthentication);
		}

		return mdmRestAuthentication;
	}

	public static MDMRestAuthentication getInstance(MDMRestConnectionTypeEnum type) {
		MDMRestAuthentication mdmRestAuthentication = cache.get(type);

		if(mdmRestAuthentication == null) {
			throw new RuntimeException("Authentication is required to use this operation.");
		}

		mdmRestAuthentication.refreshToken();

		return mdmRestAuthentication;
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
