package com.totvslabs.mdm.restclient.command;

import com.totvslabs.mdm.restclient.vo.CommandTypeEnum;
import com.totvslabs.mdm.restclient.vo.DataConsumptionVO;

import java.util.Map;

/**
 * Command to consume data from golden records.
 * Sample request: /api/v1/dataConsumption/entities/0423ece09c7811e5ab44ca46fae0c14f/records?startCounter=-1&pageSize=10
 * 
 * @author TOTVS Labs
 *
 */
public class CommandDataConsumption extends AuthenticatedCommand {
	private String entityType;
	private int pageSize;
	private int counter;

	/**
	 * Creation of the command 
	 * @param entityType
	 * @param counter
	 * @param pageSize
	 */
	public CommandDataConsumption(String entityType, int counter, int pageSize) {
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
		//nothing to do
	}

	@Override
	public String getCommandURL() {
		return "api/v1/dataConsumption/entities/" + this.entityType + "/records?startCounter=" + this.counter + "&pageSize=" + this.pageSize;
	}

	@Override
	public Class<DataConsumptionVO> getResponseType() {
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
