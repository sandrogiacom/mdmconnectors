package com.totvslabs.mdm.restclient.command;

import com.totvslabs.mdm.restclient.MDMRestAuthentication;
import com.totvslabs.mdm.restclient.MDMRestConnectionTypeEnum;

/**
 * Command that stores authentication data
 * 
 * @author TOTVS Labs
 *
 */
public abstract class AuthenticatedCommand implements ICommand {

	protected MDMRestAuthentication authentication;

	/**
	 * @return the authentication
	 */
	public MDMRestAuthentication getAuthentication() {
		//if authentication isn't defined, grab the singleton
		if (authentication == null) {
			return MDMRestAuthentication.getInstance(getAuthenticationType());
		}
		
		return authentication;
	}

	@Override
	public Boolean isResultJson() {
		return Boolean.TRUE;
	}

	public abstract MDMRestConnectionTypeEnum getAuthenticationType();

	/**
	 * @param authentication
	 *            the authentication to set
	 */
	public void setAuthentication(MDMRestAuthentication authentication) {
		this.authentication = authentication;
	}

}
