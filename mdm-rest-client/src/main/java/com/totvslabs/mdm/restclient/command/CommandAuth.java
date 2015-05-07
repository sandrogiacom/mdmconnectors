package com.totvslabs.mdm.restclient.command;

import java.util.HashMap;
import java.util.Map;

import com.totvslabs.mdm.restclient.vo.AuthVO;
import com.totvslabs.mdm.restclient.vo.CommandTypeEnum;

public class CommandAuth implements ICommand {
	private final static String GRANT_TYPE_DEFAULT = "password";
	private String username;
	private String password;
	private String grantType = CommandAuth.GRANT_TYPE_DEFAULT;
	private String tenantId;
	private String datasourceId;

	public CommandAuth(String tenantId, String datasourceId, String username, String password) {
		this(tenantId, datasourceId, username, password, "password");
	}

	public CommandAuth(String tenantId, String datasourceId, String username, String password, String grantType) {
		this.tenantId = tenantId;
		this.datasourceId = datasourceId;
		this.username = username;
		this.password = password;
		this.grantType = grantType;
	}

	@Override
	public Map<String, String> getParametersHeader() {
		Map<String, String> parameters = new HashMap<String, String>();

		parameters.put("tenant", this.tenantId);
		parameters.put("datasource", this.datasourceId);

		return parameters;
	}

	@Override
	public void processReturn() {
	}

	@Override
	public String getCommandURL() {
		return "api/v1/oauth2/auth";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Class getResponseType() {
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
		Map<String, String> parameters = new HashMap<String, String>();

		parameters.put("grant_type", this.grantType);
		parameters.put("username", this.username);
		parameters.put("password", this.password);
		parameters.put("subdomain", this.tenantId);
		parameters.put("dataSourceId", datasourceId);

		return parameters;
	}
}

