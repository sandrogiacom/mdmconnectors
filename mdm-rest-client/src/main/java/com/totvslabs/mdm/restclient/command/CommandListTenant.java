package com.totvslabs.mdm.restclient.command;

import java.util.Map;

import com.totvslabs.mdm.restclient.MDMRestConnectionTypeEnum;
import com.totvslabs.mdm.restclient.vo.CommandTypeEnum;
import com.totvslabs.mdm.restclient.vo.TenantVO;

/**
 * Command to list tenants
 * @author TOTVS Labs
 *
 */
public class CommandListTenant extends AuthenticatedCommand {

	/**
	 * Default constructor, no information needed
	 */
	public CommandListTenant() {
	}
	
	@Override
	public MDMRestConnectionTypeEnum getAuthenticationType() {
		return MDMRestConnectionTypeEnum.NORMAL;
	}

	@Override
	public void processReturn() {
		//nothing to do
	}

	@Override
	public String getCommandURL() {
		return "api/v1/admin/tenants?offset=0&pageSize=10&sortBy=mdmId&sortOrder=ASC";
	}

	@Override
	public Class<TenantVO> getResponseType() {
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
