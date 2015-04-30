package com.totvslabs.mdm.restclient.command;

import java.util.Map;

import com.totvslabs.mdm.restclient.vo.CommandTypeEnum;
import com.totvslabs.mdm.restclient.vo.TenantVO;

public class CommandListTenant implements ICommand, AuthenticationRequired {

	@Override
	public void processReturn() {
	}

	@Override
	public String getCommandURL() {
		return "api/v1/admin/tenants?offset=0&pageSize=10&sortBy=_mdmId&sortOrder=ASC";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Class getResponseType() {
		return TenantVO.class;
	}

	@Override
	public Map<String, String> getParametersHeader() {
		return null;
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
