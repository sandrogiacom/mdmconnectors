package com.totvslabs.mdm.restclient.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.totvslabs.mdm.restclient.MDMRestConnectionTypeEnum;
import com.totvslabs.mdm.restclient.vo.CommandTypeEnum;
import com.totvslabs.mdm.restclient.vo.DataConsumptionVO;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
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
	public MDMRestConnectionTypeEnum getAuthenticationType() {
		return MDMRestConnectionTypeEnum.CONSUME;
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
		return "api/v1/dataConsumption/entities/records";
	}

	@Override
	public Class<DataConsumptionVO> getResponseType() {
		return DataConsumptionVO.class;
	}

	@Override
	public Map<String, String> getParameterPath() {
		Map<String, String> param = new HashMap<String, String>();
		//[{"entityId": "d2551b10fc0f11e59e240242ac110003", "startCounter": -1}]

		JsonObject entityList = new JsonObject();
		JsonArray entityArray = new JsonArray();
		entityList.addProperty("entityId", entityType);
		entityList.addProperty("startCounter", this.counter);
		entityArray.add(entityList);

		try {
			param.put("entityList", URLEncoder.encode(entityArray.toString(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		param.put("pageSize", Integer.toString(this.pageSize));

		return param;
	}

	@Override
	public CommandTypeEnum getType() {
		return CommandTypeEnum.GET;
	}

	@Override
	public Object getData() {
		return null;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getEntityType() {
		return this.entityType;
	}

	@Override
	public Map<String, String> getFormData() {
		return null;
	}
}
