package com.totvslabs.mdm.restclient.command;

import java.util.HashMap;
import java.util.Map;

import com.totvslabs.mdm.restclient.MDMRestConnectionTypeEnum;
import com.totvslabs.mdm.restclient.vo.CommandTypeEnum;
import com.totvslabs.mdm.restclient.vo.EnvelopeVO;

/**
 * Command to list all the datasources
 * @author TOTVS Labs
 *
 */
public class CommandListDatasource extends AuthenticatedCommand {
	private String tenantId;

	/**
	 * Create command based on the tenantId
	 * @param tenantId
	 */
	public CommandListDatasource(String tenantId) {
		this.tenantId = tenantId;
	}

	@Override
	public MDMRestConnectionTypeEnum getAuthenticationType() {
		return MDMRestConnectionTypeEnum.NORMAL;
	}

	@Override
	public Map<String, String> getParametersHeader() {
		Map<String, String> parameters = new HashMap<>();

		parameters.put("tenant", this.tenantId);

		return parameters;
	}

	@Override
	public void processReturn() {
		//nothing to do
	}

	@Override
	public String getCommandURL() {
		return "api/v1/dataSources?offset=0&pageSize=10&sortBy=mdmId&sortOrder=ASC";
	}

	@Override
	public Class<EnvelopeVO> getResponseType() {
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
