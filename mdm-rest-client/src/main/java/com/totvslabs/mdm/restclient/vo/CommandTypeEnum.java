package com.totvslabs.mdm.restclient.vo;

/**
 * Possible HTTP Methods used in the MDM REST APIs
 * @author TOTVS Labs
 *
 */
public enum CommandTypeEnum {
	/**
	 * Create new data / execute operation
	 */
	POST, 
	
	/**
	 * Update data
	 */
	PUT, 
	
	/**
	 * Get data (read only)
	 */
	GET;
}
