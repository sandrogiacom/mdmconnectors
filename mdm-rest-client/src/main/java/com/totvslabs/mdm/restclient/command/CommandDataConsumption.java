package com.totvslabs.mdm.restclient.command;

import java.util.Map;

import com.totvslabs.mdm.restclient.vo.CommandTypeEnum;
import com.totvslabs.mdm.restclient.vo.DataConsumptionVO;

//https://totvslabs.fluigdata.com:8443/mdm/api/v1/dataConsumption/entities/0423ece09c7811e5ab44ca46fae0c14f/records?startCounter=-1&pageSize=10

public class CommandDataConsumption implements ICommand, AuthenticationRequired {
	private String entityType;
	private Integer pageSize;
	private Integer counter;

	public CommandDataConsumption(String entityType, Integer counter, Integer pageSize) {
		this.entityType = entityType;
		this.counter = counter;
		this.pageSize = pageSize;
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
		return "api/v1/dataConsumption/entities/" + this.entityType + "/records?startCounter=" + this.counter + "&pageSize=" + this.pageSize;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Class getResponseType() {
		return DataConsumptionVO.class;
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
