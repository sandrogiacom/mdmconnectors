package com.totvslabs.mdm.restclient.command;

/**
 * Command to send staging data
 * @author TOTVS Labs
 *
 */
public class CommandPostStagingC extends CommandPostStaging {

	/**
	 * Create command based on the given information
	 * @param tenantId
	 * @param datasourceId
	 * @param type
	 * @param data
	 */
	public CommandPostStagingC(String tenantId, String datasourceId, String type, Object data) {
		super(tenantId, datasourceId, type, data);
	}
}
