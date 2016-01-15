package com.totvslabs.mdm.restclient.command;

import java.util.HashMap;
import java.util.Map;

import com.totvslabs.mdm.restclient.vo.CommandTypeEnum;
import com.totvslabs.mdm.restclient.vo.DatasourceVO;

@AuthenticationRequired
public class CommandPostStaging implements ICommand {
	private String tenantId;
	private String datasourceId;
	private String type;
	private Object data;

	public CommandPostStaging(String tenantId, String datasourceId, String type, Object data) {
		this.tenantId = tenantId;
		this.datasourceId = datasourceId;
		this.type = type;
		this.data = data;
	}

	@Override
	public Map<String, String> getParametersHeader() {
		Map<String, String> parameters = new HashMap<String, String>();

		parameters.put("tenant", this.tenantId);
		parameters.put("datasource", datasourceId);

		return parameters;
	}

	@Override
	public void processReturn() {
	}

	@Override
	public String getCommandURL() {
		return "api/v1/staging/entities/types/" + type + "";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Class getResponseType() {
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
		return data;
	}

	@Override
	public Map<String, String> getFormData() {
		return null;
	}
	@Override
	public String toString() {
		return "CommandPostStaging [tenantId=" + tenantId + ", datasourceId="
				+ datasourceId + ", type=" + type + ", data=" + data + "]";
	}
}
