package com.totvslabs.mdm.restclient.command;

import java.util.Map;

import com.totvslabs.mdm.restclient.vo.CommandTypeEnum;
import com.totvslabs.mdm.restclient.vo.DataModelVO;

@AuthenticationRequired
public class CommandGetDataModel implements ICommand {
	private String dataModelID;

	public CommandGetDataModel(String dataModelID) {
		this.dataModelID = dataModelID;
	}

	@Override
	public Map<String, String> getParametersHeader() {
		return null;
	}

	@Override
	public void processReturn() {
	}

	@Override
	public String getCommandURL() {
		return "api/v1/entities/templates/" + dataModelID;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Class getResponseType() {
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
