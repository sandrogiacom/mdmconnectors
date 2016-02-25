package com.totvslabs.mdm.restclient.command;

import java.util.Map;

import com.totvslabs.mdm.restclient.vo.CommandTypeEnum;
import com.totvslabs.mdm.restclient.vo.DataModelVO;

/**
 * Command to get a specific data model
 * @author TOTVS Labs
 *
 */
public class CommandGetDataModelByName extends AuthenticatedCommand {
	private String name;

	/**
	 * Create the command based on the data model name
	 * @param name
	 */
	public CommandGetDataModelByName(String name) {
		this.name = name;
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
		return "api/v1/entities/templates/name/" + name;
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
