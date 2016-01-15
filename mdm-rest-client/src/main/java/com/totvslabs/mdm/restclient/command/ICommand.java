package com.totvslabs.mdm.restclient.command;

import java.util.Map;

import com.totvslabs.mdm.restclient.vo.CommandTypeEnum;
import com.totvslabs.mdm.restclient.vo.GenericVO;

/**
 * Common Interface for every possible command/API call in MDM
 * 
 * @author TOTVS Labs
 *
 */
public interface ICommand {

	/**
	 * Process return of command
	 */
	public void processReturn();

	/**
	 * @return command API URL
	 */
	public String getCommandURL();

	/**
	 * @return get the type of the response
	 */
	public Class<?> getResponseType();

	/**
	 * @return get header parameters
	 */
	public Map<String, String> getParametersHeader();

	/**
	 * @return get path parameters
	 */
	public Map<String, String> getParameterPath();

	/**
	 * @return get form (payload) parameters
	 */
	public Map<String, String> getFormData();

	/**
	 * @return get the command type / method
	 */
	public CommandTypeEnum getType();

	/**
	 * @return get data of the command
	 */
	public Object getData();
}
