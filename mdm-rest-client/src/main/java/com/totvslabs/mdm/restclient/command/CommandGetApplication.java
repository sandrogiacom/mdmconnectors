package com.totvslabs.mdm.restclient.command;

import java.util.Map;

import com.totvslabs.mdm.restclient.MDMRestConnectionTypeEnum;
import com.totvslabs.mdm.restclient.vo.ApplicationVO;
import com.totvslabs.mdm.restclient.vo.CommandTypeEnum;

/**
 * Command to get existent Application
 * @author TOTVS Labs
 *
 */
public class CommandGetApplication extends AuthenticatedCommand {
	private String applicationId;

	/**
	 * Create command based on the consumer id
	 * @param dataConsumerId
	 */
	public CommandGetApplication(String applicationId) {
		this.applicationId = applicationId;
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
		return "api/v1/applications/" + applicationId;
	}

	@Override
	public Class<ApplicationVO> getResponseType() {
		return ApplicationVO.class;
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
