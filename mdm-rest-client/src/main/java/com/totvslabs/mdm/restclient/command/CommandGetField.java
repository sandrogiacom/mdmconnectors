package com.totvslabs.mdm.restclient.command;

import java.util.Map;

import com.totvslabs.mdm.restclient.MDMRestConnectionTypeEnum;
import com.totvslabs.mdm.restclient.vo.CommandTypeEnum;
import com.totvslabs.mdm.restclient.vo.FieldsVO;

/**
 * Command to get specific field
 * @author TOTVS Labs
 *
 */
public class CommandGetField extends AuthenticatedCommand {
	private String fieldId;

	/**
	 * Create command based on the field id
	 * @param fieldId
	 */
	public CommandGetField(String fieldId) {
		this.fieldId = fieldId;
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
		return "api/v1/fields/" + fieldId;
	}

	@Override
	public Class<FieldsVO> getResponseType() {
		return FieldsVO.class;
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
