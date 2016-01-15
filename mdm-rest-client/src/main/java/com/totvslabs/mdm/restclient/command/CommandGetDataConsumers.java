package com.totvslabs.mdm.restclient.command;

import java.util.Map;

import com.totvslabs.mdm.restclient.vo.CommandTypeEnum;
import com.totvslabs.mdm.restclient.vo.DataConsumerVO;

/**
 * Command to get existent Data Consumers
 * @author TOTVS Labs
 *
 */
public class CommandGetDataConsumers extends AuthenticatedCommand {
	private String dataConsumerId;

	/**
	 * Create command based on the consumer id
	 * @param dataConsumerId
	 */
	public CommandGetDataConsumers(String dataConsumerId) {
		this.dataConsumerId = dataConsumerId;
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
		return "api/v1/dataConsumers/" + dataConsumerId;
	}

	@Override
	public Class<DataConsumerVO> getResponseType() {
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
