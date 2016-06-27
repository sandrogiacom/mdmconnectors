package com.totvslabs.mdm.restclient.command;

import java.util.HashMap;
import java.util.Map;

import com.totvslabs.mdm.restclient.vo.AuthVO;
import com.totvslabs.mdm.restclient.vo.CommandTypeEnum;

/**
 * Authentication Command
 * @author TOTVS Labs
 *
 */
public class CommandAuth implements ICommand {
	private final static String GRANT_TYPE_DEFAULT = "password";
	private String username;
	private String password;
	private String grantType = CommandAuth.GRANT_TYPE_DEFAULT;
	private String tenantId;
	private String applicationId;

	/**
	 * Create an authentication command with login values
	 * @param tenantId
	 * @param datasourceId
	 * @param username
	 * @param password
	 */
	public CommandAuth(String tenantId, String datasourceId, String username, String password) {
		this(tenantId, datasourceId, username, password, "password");
	}

	/**
	 * Create an authentication command with login values
	 * @param tenantId
	 * @param datasourceId
	 * @param username
	 * @param password
	 * @param grantType
	 */
	public CommandAuth(String tenantId, String applicationId, String username, String password, String grantType) {
		this.tenantId = tenantId;
		this.applicationId = applicationId;
		this.username = username;
		this.password = password;
		this.grantType = grantType;
	}

	@Override
	public Map<String, String> getParametersHeader() {
		Map<String, String> parameters = new HashMap<>();

		parameters.put("tenant", this.tenantId);
		parameters.put("applicationId", this.applicationId);

		return parameters;
	}

	@Override
	public void processReturn() {
		//nothing
	}

	@Override
	public String getCommandURL() {
		return "api/v1/oauth2/token";
	}

	@Override
	public Class<AuthVO> getResponseType() {
		return AuthVO.class;
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

		parameters.put("grant_type", this.grantType);
		parameters.put("username", this.username);
		parameters.put("password", this.password);
		parameters.put("subdomain", this.tenantId);
		parameters.put("applicationId", applicationId);

		return parameters;
	}

	@Override
	public Boolean isResultJson() {
		return Boolean.TRUE;
	}
}

