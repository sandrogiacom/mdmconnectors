package com.totvslabs.mdm.restclient.command;

import java.util.HashMap;
import java.util.Map;

import com.totvslabs.mdm.restclient.vo.CommandTypeEnum;
import com.totvslabs.mdm.restclient.vo.EnvelopeVO;

public class CommandListDatasource implements ICommand, AuthenticationRequired {
	private String tenantId;

	public CommandListDatasource(String domain) {
		this.tenantId = domain;
	}

	@Override
	public Map<String, String> getParametersHeader() {
		Map<String, String> parameters = new HashMap<String, String>();

		parameters.put("tenant", this.tenantId);

		return parameters;
	}

	@Override
	public void processReturn() {
	}

	@Override
	public String getCommandURL() {
		return "api/v1/dataSources?offset=0&pageSize=10&sortBy=_mdmId&sortOrder=ASC";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Class getResponseType() {
		return EnvelopeVO.class;
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
