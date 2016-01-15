package com.totvslabs.mdm.restclient.command;

import java.util.Map;

import com.totvslabs.mdm.restclient.vo.CommandTypeEnum;
import com.totvslabs.mdm.restclient.vo.DataConsumerVO;

@AuthenticationRequired
public class CommandGetDataConsumers implements ICommand {
	private String dataConsumerID;

	public CommandGetDataConsumers(String dataConsumerID) {
		this.dataConsumerID = dataConsumerID;
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
		return "api/v1/dataConsumers/" + dataConsumerID;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Class getResponseType() {
		return DataConsumerVO.class;
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
