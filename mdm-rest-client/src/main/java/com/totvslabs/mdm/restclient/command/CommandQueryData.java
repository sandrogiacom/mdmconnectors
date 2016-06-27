package com.totvslabs.mdm.restclient.command;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.totvslabs.mdm.restclient.MDMRestConnectionTypeEnum;
import com.totvslabs.mdm.restclient.vo.CommandTypeEnum;
import com.totvslabs.mdm.restclient.vo.DataQueryVO;

public class CommandQueryData extends AuthenticatedCommand {
	private String entityName;
	private Integer startAt;
	private Integer pageSize;
	private String filter; 		//{"type":"mdmaddressGolden", "filter": "mdmId eq 00001d70fec811e599710242ac110003"}

	@Override
	public MDMRestConnectionTypeEnum getAuthenticationType() {
		return MDMRestConnectionTypeEnum.NORMAL;
	}

	public CommandQueryData(String entityName, Integer startAt, Integer pageSize) {
		this.entityName = entityName;
		this.startAt = startAt;
		this.pageSize = pageSize;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}
	@Override
	public void processReturn() {
	}

	@Override
	public String getCommandURL() {
		return "api/v1/queries/freeform";
	}

	@Override
	public Class<?> getResponseType() {
		return DataQueryVO.class;
	}

	@Override
	public Map<String, String> getParametersHeader() {
		return null;
	}

	@Override
	public Map<String, String> getParameterPath() {
		Map<String, String> parameter = new HashMap<String, String>();
		parameter.put("offset", Integer.toString(startAt));
		parameter.put("pageSize", Integer.toString(pageSize));

		return parameter;
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
		JsonObject obj = new JsonObject();
		
		if(this.entityName != null) {
			obj.addProperty("type", this.entityName + "Golden");
		}

		if(filter != null) {
			obj.addProperty("filter", filter);
		}

		JsonArray returnFields = new JsonArray();
		returnFields.add(new JsonPrimitive("mdmGoldenFieldAndValues"));
		obj.add("returnFields", returnFields);

		return obj;
	}

}
