package com.totvslabs.mdm.restclient.command;

import java.util.Map;

import com.totvslabs.mdm.restclient.vo.CommandTypeEnum;
import com.totvslabs.mdm.restclient.vo.GenericVO;

public interface ICommand {
	public void processReturn();
	public String getCommandURL();
	public Class<GenericVO> getResponseType();
	public Map<String, String> getParametersHeader();
	public Map<String, String> getParameterPath();
	public Map<String, String> getFormData();
	public CommandTypeEnum getType();
	public Object getData();
}
