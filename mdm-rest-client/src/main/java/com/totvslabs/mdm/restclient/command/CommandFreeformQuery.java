package com.totvslabs.mdm.restclient.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.totvslabs.mdm.restclient.MDMRestConnectionTypeEnum;
import com.totvslabs.mdm.restclient.vo.CommandTypeEnum;
import com.totvslabs.mdm.restclient.vo.DatasourceVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Command to execute freeform query
 * @author TOTVS Labs
 *
 */
public class CommandFreeformQuery extends AuthenticatedCommand {
	private String type;
	private String filter;
	private List<String> fields;

	/**
	 * Create command based on given information
	 * @param type
	 * @param filter
	 * @param fields
	 */
	public CommandFreeformQuery(String type, String filter, List<String> fields) {
		this.type = type;
		this.filter = filter;
		this.fields = fields;
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
		return "api/v1/queries/freeform";
	}

	@Override
	public Class<DatasourceVO> getResponseType() {
		return DatasourceVO.class;
	}

	@Override
	public Map<String, String> getParameterPath() {
		return null;
	}

	@Override
	public CommandTypeEnum getType() {
		return CommandTypeEnum.POST;
	}

	@Override
	public Object getData() {
		JsonObject object = new JsonObject();
		if (type != null) {
			object.addProperty("type", type);
		}
		
		if (filter != null) {
			object.addProperty("filter", filter);
		}
		
		if (fields != null) {
			JsonArray fieldsArray = new JsonArray();
			for (String field : fields) {
				fieldsArray.add(new JsonPrimitive(field));
			}
			object.add("fields", fieldsArray);
		}
		
		return object;
	}

	@Override
	public Map<String, String> getFormData() {
		return null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format(
				"CommandFreeformQuery [type=%s, filter=%s, fields=%s]", type,
				filter, fields);
	}
	
	
}
