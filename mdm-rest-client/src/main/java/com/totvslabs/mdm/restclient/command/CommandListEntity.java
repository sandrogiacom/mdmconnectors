package com.totvslabs.mdm.restclient.command;

import java.util.HashMap;
import java.util.Map;

import com.totvslabs.mdm.restclient.vo.CommandTypeEnum;
import com.totvslabs.mdm.restclient.vo.EntityVO;

/**
 * Command to list entities
 * @author TOTVS Labs
 *
 */
public class CommandListEntity extends AuthenticatedCommand {
	private String tenantId;
	private String dataSourceId;

	/**
	 * Create command based on the tenantId and dataSourceId
	 * @param tenantId
	 * @param dataSourceId
	 */
	public CommandListEntity(String tenantId, String dataSourceId) {
		this.tenantId = tenantId;
		this.dataSourceId = dataSourceId;
	}

	@Override
	public Map<String, String> getParametersHeader() {
		Map<String, String> parameters = new HashMap<>();

		parameters.put("tenant", this.tenantId);
		parameters.put("datasource", this.dataSourceId);

		return parameters;
	}

	@Override
	public void processReturn() {
		//nothing to do
	}

	@Override
	public String getCommandURL() {
		return "api/v1/admin/entities/templates?offset=0&pageSize=10&sortBy=_mdmId&sortOrder=ASC";
	}

	@Override
	public Class<EntityVO> getResponseType() {
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
