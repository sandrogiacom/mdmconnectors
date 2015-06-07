package com.totvslabs.mdm.restclient;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.totvslabs.mdm.restclient.command.CommandListDatasource;
import com.totvslabs.mdm.restclient.command.CommandPostStaging;
import com.totvslabs.mdm.restclient.command.ICommand;
import com.totvslabs.mdm.restclient.vo.EnvelopeVO;
import com.totvslabs.mdm.restclient.vo.GenericVO;

/**
 * Executes a MDM Staging using a simple row
 * @author Bruno Candido Volpato da Cunha
 *
 */
public class StagingExecutorTest {

	private static final Logger log = Logger
			.getLogger(StagingExecutorTest.class);

	private MDMRestConnection connection;

	@Before
	public void setUp() {
		String password = System.getProperty("mdm.password");
		if (password == null || password.isEmpty()) {
			throw new IllegalArgumentException(
					"Password should be set using -Dmdm.password");
		}

		log.info("Configuring authentication properties...");
		MDMRestAuthentication.getInstance(MDMTestingConstants.MDM_URL,
				MDMTestingConstants.SUBDOMAIN,
				MDMTestingConstants.DATASOURCE_ID,
				MDMTestingConstants.USERNAME,
				System.getProperty("mdm.password"));

		log.info("Connecting to RESTful Services...");

		connection = MDMRestConnectionFactory
				.getConnection(MDMTestingConstants.MDM_URL);

		
	}

	@Test
	public void execute() {

		log.info("Listing Domain DataSources...");
		ICommand tenantCommand = new CommandListDatasource(
				MDMTestingConstants.SUBDOMAIN);
		EnvelopeVO envelope = connection.executeCommand(tenantCommand);

		assertNotNull("Hits result should not be null", envelope.getHits());
		
		log.info(String.format("Found %s hits.", envelope.getHits().size()));

		for (GenericVO vo : envelope.getHits()) {
			log.info("Row: " + vo);
		}

		log.info("Staging data...");

		JsonArray stagingArray = new JsonArray();
		JsonObject testObject = new JsonObject();
		testObject.addProperty("nome", "Bruno-Java Client");
		stagingArray.add(testObject);

		CommandPostStaging staging = new CommandPostStaging(
				MDMRestAuthentication.getInstance().getAuthVO()
						.get_mdmTenantId(), MDMRestAuthentication.getInstance()
						.getAuthVO().get_mdmDataSourceId(), "mdm_rest_client_test",
				stagingArray);
		EnvelopeVO executeCommand = connection.executeCommand(staging);
		
		log.info(executeCommand);
	}

}
