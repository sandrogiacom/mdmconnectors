package com.totvslabs.mdm.restclient.command;

import java.util.HashMap;
import java.util.Map;

import com.totvslabs.mdm.restclient.vo.CommandTypeEnum;
import com.totvslabs.mdm.restclient.vo.RefreshTokenVO;

public class CommandRefreshToken implements ICommand {
	private String refreshToken;

	public CommandRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	@Override
	public Map<String, String> getParametersHeader() {
		return null;
	}

	@Override
	public void processReturn() {
	}

	@Override
	public String getCommandURL() {
		return "api/v1/oauth2/token";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Class getResponseType() {
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
		Map<String, String> parameters = new HashMap<String, String>();

		parameters.put("refresh_token", this.refreshToken);

		return parameters;
	}
}

