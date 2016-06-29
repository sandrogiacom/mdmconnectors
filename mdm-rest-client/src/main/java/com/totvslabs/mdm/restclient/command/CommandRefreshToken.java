package com.totvslabs.mdm.restclient.command;

import java.util.HashMap;
import java.util.Map;

import com.totvslabs.mdm.restclient.vo.CommandTypeEnum;
import com.totvslabs.mdm.restclient.vo.RefreshTokenVO;

/**
 * Command to refresh an access token
 * @author TOTVS Labs
 *
 */
public class CommandRefreshToken implements ICommand {
	private String refreshToken;

	/**
	 * Create command based on the given refreshToken
	 * @param refreshToken
	 */
	public CommandRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	@Override
	public Map<String, String> getParametersHeader() {
		return null;
	}

	@Override
	public void processReturn() {
		//nothing to do
	}

	@Override
	public String getCommandURL() {
		return "api/v1/oauth2/token";
	}

	@Override
	public Class<RefreshTokenVO> getResponseType() {
		return RefreshTokenVO.class;
	}

	@Override
	public Map<String, String> getParameterPath() {
		return null;
	}

	@Override
	public CommandTypeEnum getType() {
		return CommandTypeEnum.POST;
	}

	@Override
	public Object getData() {
		return null;
	}

	@Override
	public Map<String, String> getFormData() {
		Map<String, String> parameters = new HashMap<>();

		parameters.put("grant_type", "refresh_token");
		parameters.put("refresh_token", this.refreshToken);

		return parameters;
	}

	@Override
	public Boolean isResultJson() {
		return Boolean.TRUE;
	}
}

