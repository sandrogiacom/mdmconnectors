package com.totvslabs.mdm.restclient.command;

import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.totvslabs.mdm.restclient.MDMRestConnectionTypeEnum;
import com.totvslabs.mdm.restclient.vo.CommandTypeEnum;
import com.totvslabs.mdm.restclient.vo.GenericVO;

public class CommandConfirmationConsumption extends AuthenticatedCommand {
	private JsonObject data = new JsonObject();

	public CommandConfirmationConsumption() {
	}

	public CommandConfirmationConsumption(String entityId, String recordId) {
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(recordId));

		data.add(entityId, array);
	}

	public void add(String entityId, String recordId) {
		JsonArray jsonElement = (JsonArray) this.data.get(entityId);

		if(jsonElement == null) {
			JsonArray array = new JsonArray();
			this.data.add(entityId, array);
			jsonElement = array;
		}

		jsonElement.add(new JsonPrimitive(recordId));
	}

	@Override
	public void processReturn() {
	}

	@Override
	public String getCommandURL() {
		return "api/v1/dataConsumption/entities/confirmRecordsConsumed";
	}

	@Override
	public Class<?> getResponseType() {
		return GenericVO.class;
	}

	@Override
	public Map<String, String> getParametersHeader() {
		return null;
	}

	@Override
	public Map<String, String> getParameterPath() {
		return null;
	}

	@Override
	public Map<String, String> getFormData() {
		return null;
	}

	@Override
	public CommandTypeEnum getType() {
		return CommandTypeEnum.POST;
	}

	@Override
	public Object getData() {
		return this.data;
	}

	@Override
	public MDMRestConnectionTypeEnum getAuthenticationType() {
		return MDMRestConnectionTypeEnum.CONSUME;
	}
}
