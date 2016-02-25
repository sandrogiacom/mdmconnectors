package com.totvslabs.mdm.restclient.command;

import com.totvslabs.mdm.restclient.vo.CommandTypeEnum;
import com.totvslabs.mdm.restclient.vo.StagingSchemaVO;

import java.util.HashMap;
import java.util.Map;

/**
 * Command to get a specific staging schema
 * @author TOTVS Labs
 *
 */
public class CommandGetSchema extends AuthenticatedCommand {
	private String type;
	private String dataSourceId;
	
	/**
	 * Create the command based on the type
	 * @param type
	 */
	public CommandGetSchema(String type) {
		this.type = type;
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
		return "api/v1/staging/entities/types/" + type + "/schema";
	}

	@Override
	public Class<StagingSchemaVO> getResponseType() {
		return StagingSchemaVO.class;
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
		if (dataSourceId == null || dataSourceId.isEmpty()) {
			return null;
		}
		
		Map<String, String> parameters = new HashMap<>();
		parameters.put("dataSourceId", dataSourceId);
		return parameters;
	}

	/**
	 * @return the dataSourceId
	 */
	public String getDataSourceId() {
		return dataSourceId;
	}

	/**
	 * @param dataSourceId the dataSourceId to set
	 */
	public void setDataSourceId(String dataSourceId) {
		this.dataSourceId = dataSourceId;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	
}
