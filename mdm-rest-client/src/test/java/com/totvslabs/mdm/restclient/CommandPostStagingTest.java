package com.totvslabs.mdm.restclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.totvslabs.mdm.restclient.command.CommandPostStaging;
import com.totvslabs.mdm.restclient.vo.GenericVO;

/**
 * Some testing with Staging Command
 * @author Bruno Candido Volpato da Cunha
 *
 */
public class CommandPostStagingTest {
    
	private MDMRestConnection connection;
	
	@Before
	public void setup() {
		//Not using SSL for possible troubleshoot using network sniffers
		connection = MDMRestConnectionFactory.getConnection(MDMTestingConstants.MDM_URL);
	}
	
	@Test
	public void testEmpty() {
		CommandPostStaging staging = new CommandPostStaging("a", "b", "test", new ArrayList<GenericVO>());
		assertNotNull(staging);
		
		assertEquals("Staging URL", "api/v1/staging/entities/types/test", staging.getCommandURL());
		assertEquals("Blank data", "[]", staging.getData().toString());
		
	}
	
	@Test(expected=RuntimeException.class)
	public void testSendEmptyInvalidDS() {
		CommandPostStaging staging = new CommandPostStaging("a", "b" ,"test1", new ArrayList<GenericVO>());
		connection.executeCommand(staging);
		
		//EnvelopeVO vo = connection.executeCommand(staging);
		//assertEquals("0-sized request", 0L, (long) vo.getCount());
	}
}
