package com.totvslabs.mdm.restclient.command;

import java.util.Map;

import com.totvslabs.mdm.restclient.MDMRestConnectionTypeEnum;
import com.totvslabs.mdm.restclient.vo.CommandTypeEnum;
import com.totvslabs.mdm.restclient.vo.DataModelVO;

/**
 * Command to get a specific data model
 * @author TOTVS Labs
 *
 */
public class CommandGetDataModel extends AuthenticatedCommand {
	private String dataModelId;

	/**
	 * Create the command based on the data model id
	 * @param dataModelId
	 */
	public CommandGetDataModel(String dataModelId) {
		this.dataModelId = dataModelId;
	}

	@Override
	public MDMRestConnectionTypeEnum getAuthenticationType() {
		return MDMRestConnectionTypeEnum.NORMAL;
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
		return "api/v1/entities/templates/" + dataModelId;
	}

	@Override
	public Class<DataModelVO> getResponseType() {
		return DataModelVO.class;
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
