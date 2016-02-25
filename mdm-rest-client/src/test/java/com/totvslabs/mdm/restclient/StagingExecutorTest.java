package com.totvslabs.mdm.restclient;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.totvslabs.mdm.restclient.command.AuthenticatedCommand;
import com.totvslabs.mdm.restclient.command.CommandListDatasource;
import com.totvslabs.mdm.restclient.command.CommandPostSchema;
import com.totvslabs.mdm.restclient.command.CommandPostStaging;
import com.totvslabs.mdm.restclient.command.ICommand;
import com.totvslabs.mdm.restclient.vo.EnvelopeVO;
import com.totvslabs.mdm.restclient.vo.GenericVO;

import java.util.Random;

/**
 * Executes a MDM Staging using a simple row
 * 
 * @author Bruno Candido Volpato da Cunha
 *
 */
public class StagingExecutorTest {

	private static final Logger log = Logger.getLogger(StagingExecutorTest.class);

	private MDMRestConnection connection;

	private MDMRestAuthentication authentication;
	
	@Before
	public void setUp() {
		String password = System.getProperty("mdm.password");
		if (password == null || password.isEmpty()) {
			throw new IllegalArgumentException("Password should be set using -Dmdm.password");
		}

		log.info("Configuring authentication properties...");
		authentication = MDMRestAuthentication.getInstance(MDMTestingConstants.MDM_URL, 
				MDMTestingConstants.SUBDOMAIN,
				MDMTestingConstants.DATASOURCE_ID, 
				MDMTestingConstants.USERNAME, 
				System.getProperty("mdm.password"),
				true);

		

		log.info("Connecting to RESTful Services...");

		connection = MDMRestConnectionFactory.getConnection(MDMTestingConstants.MDM_URL);

	}

	@Test
	public void execute() {

		log.info("Listing Domain DataSources...");
		AuthenticatedCommand tenantCommand = new CommandListDatasource(MDMTestingConstants.SUBDOMAIN);
		tenantCommand.setAuthentication(authentication);
		
		EnvelopeVO envelope = connection.executeCommand(tenantCommand);

		assertNotNull("Hits result should not be null", envelope.getHits());

		log.info(String.format("Found %s hits.", envelope.getHits().size()));

		for (GenericVO vo : envelope.getHits()) {
			log.info("Row: " + vo);
		}

		log.info("Staging data...");

		String type = "mdm_rest_client_test_" + randomIntBetween(1,10000);

		try {
			JsonObject schema = new JsonObject();
			schema.addProperty("_mdmStagingDataSourceId", MDMTestingConstants.DATASOURCE_ID);
			schema.addProperty("_mdmStagingType", type);

			JsonArray crosswalkType = new JsonArray();
			crosswalkType.add(new JsonPrimitive("name"));
			
			JsonObject crosswalk = new JsonObject();
			crosswalk.add(type, crosswalkType);
			
			schema.add("_mdmCrosswalkTemplate", crosswalk);
			
			
			JsonObject mapping = new JsonObject();
			
			JsonObject mappingProperties = new JsonObject();
			JsonObject nameProperties = new JsonObject();
			nameProperties.addProperty("type", "string");
			mappingProperties.add("name", nameProperties);
			
			mapping.add("properties", mappingProperties);
			
			schema.add("_mdmStagingMapping", mapping);
			
			AuthenticatedCommand schemaCommand = new CommandPostSchema(MDMRestAuthentication.getInstance().getAuthVO()
					.getMdmTenantId(), MDMTestingConstants.DATASOURCE_ID, type,
					schema);
			schemaCommand.setAuthentication(authentication);

			EnvelopeVO schemaCommandResponse = connection.executeCommand(schemaCommand);

			log.info("Schema Command Response: " + schemaCommandResponse);

		} catch (Exception e) {
			log.error("Error happened when creating schema", e);
		}

		{
			JsonArray stagingArray = new JsonArray();
			JsonObject testObject = new JsonObject();
			testObject.addProperty("name", "Bruno-Java Client");
			stagingArray.add(testObject);

			AuthenticatedCommand stagingCommand = new CommandPostStaging(MDMRestAuthentication.getInstance().getAuthVO()
					.getMdmTenantId(), MDMTestingConstants.DATASOURCE_ID, type,
					stagingArray);
			stagingCommand.setAuthentication(authentication);

			EnvelopeVO stagingCommandResponse = connection.executeCommand(stagingCommand);

			log.info("Staging Command Response: " + stagingCommandResponse);
		}

	}

	private static int randomIntBetween(int min, int max) {
		Random rand = new Random();
		return rand.nextInt((max - min) + 1) + min;
	}

}
