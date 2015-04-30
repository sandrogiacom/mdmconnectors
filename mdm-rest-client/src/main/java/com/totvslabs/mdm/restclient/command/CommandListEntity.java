package com.totvslabs.mdm.restclient.command;

import java.util.HashMap;
import java.util.Map;

import com.totvslabs.mdm.restclient.vo.CommandTypeEnum;
import com.totvslabs.mdm.restclient.vo.EntityVO;

public class CommandListEntity implements ICommand, AuthenticationRequired {
	private String tenantId;
	private String datasourceId;

	public CommandListEntity(String tenantId, String datasourceId) {
		this.tenantId = tenantId;
		this.datasourceId = datasourceId;
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
		return "api/v1/admin/entities/templates?offset=0&pageSize=10&sortBy=_mdmId&sortOrder=ASC";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Class getResponseType() {
		return EntityVO.class;
	}

	@Override
	public Map<String, String> getParameterPath() {
		return null;
	}

	@Override
	public CommandTypeEnum getType() {
		return CommandTypeEnum.GET;
	}

	@Override
	public Object getData() {
		return null;
	}

	@Override
	public Map<String, String> getFormData() {
		return null;
	}
}
