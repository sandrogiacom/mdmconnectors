package com.totvslabs.mdm.restclient;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.JerseyClientBuilder;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.totvslabs.mdm.restclient.command.AuthenticatedCommand;
import com.totvslabs.mdm.restclient.command.CommandDataConsumption;
import com.totvslabs.mdm.restclient.command.CommandPostStagingC;
import com.totvslabs.mdm.restclient.command.ICommand;
import com.totvslabs.mdm.restclient.vo.CommandTypeEnum;
import com.totvslabs.mdm.restclient.vo.DataConsumptionEntitiesRecordVO;
import com.totvslabs.mdm.restclient.vo.DataConsumptionVO;
import com.totvslabs.mdm.restclient.vo.EnvelopeVO;
import com.totvslabs.mdm.restclient.vo.GenericVO;

/**
 * 
 *
 */
public class MDMRestConnection {
	
	private static final Logger log = Logger.getLogger(MDMRestConnection.class);
	
	private Client client;
	private String mdmURL;

	public MDMRestConnection(String mdmURL) {
		this.client = new JerseyClientBuilder()
						.sslContext(getTrustAllSSLContext())
						.hostnameVerifier(new DefaultHostnameVerifier())
						.build(); 

		this.client.register(GZipReaderInterceptor.class);
		this.client.register(GZipWriterInterceptor.class);

		this.mdmURL = mdmURL;
	}

	public EnvelopeVO executeCommand(ICommand command) {
		Map<String, String> parametersHeader = command.getParametersHeader();
		Map<String, String> parameterPath = command.getParameterPath();

		Set<Entry<String, String>> entrySetPath = parameterPath != null ? parameterPath.entrySet() : null;
		Set<Entry<String, String>> entrySetHeader = parametersHeader != null ? parametersHeader.entrySet() : null;

		WebTarget webResource = this.client.target(mdmURL + command.getCommandURL());

		if (entrySetPath != null) {
			for (Entry<String, String> param : entrySetPath) {
				webResource = webResource.queryParam(param.getKey(), param.getValue());
			}
		}

		Builder request = webResource.request(MediaType.APPLICATION_JSON);

		if (entrySetHeader != null) {
			for (Entry<String, String> param : entrySetHeader) {
				request = request.header(param.getKey(), param.getValue());
			}
		}
		
		if (command instanceof AuthenticatedCommand) {
			
			AuthenticatedCommand authCommand = (AuthenticatedCommand) command;
		    log.info("Adding Authorization header...");
			request = request.header("Authorization", authCommand.getAuthentication().getAuthVO().getAccess_token());
		}

		Response response = null;

		switch(command.getType()) {
			case GET:
				long initialTimeGet = System.currentTimeMillis();

				response = request.accept(MediaType.APPLICATION_JSON).get();

				log.info("Time to execute the GET service ('" + command.getCommandURL() + "'): " + (System.currentTimeMillis() - initialTimeGet) );
				break;

			case POST:
			case PUT:
				String type = MediaType.APPLICATION_JSON;
				String additionalInformation = "";

				if(command instanceof CommandPostStagingC) {
					log.info("Adding compression (and forcing)...");
					
					additionalInformation = " - COMPRESS";
				}

				Map<String, String> formDataCommand = command.getFormData();
				MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();

				if (formDataCommand != null && !formDataCommand.isEmpty()) {
					Set<String> keySet = formDataCommand.keySet();

					for (String key : keySet) {
						String value = formDataCommand.get(key);

						formData.add(key, value);
					}
				}

				long initialTimeConvertJson = System.currentTimeMillis();
				String string = command.getData() != null ? command.getData().toString() : "";
				log.info("Time to convert the OBJECT to JSON: " + (System.currentTimeMillis() - initialTimeConvertJson) );
				long initialTime = System.currentTimeMillis();

				request = request.accept(type);
				
				Entity<?> body;
				if (!formData.isEmpty()) {
					body = Entity.form(formData);
				} else {
					body = Entity.entity(string, type);
				}
				
				if (command.getType() == CommandTypeEnum.PUT) {
					response = request.put(body);
				} else {
					response = request.post(body);
				}
				
				log.info("Time to execute the POST service ('" + command.getCommandURL() + "'" + additionalInformation + ": " + formData + "): " + (System.currentTimeMillis() - initialTime) );
				break;
		}

		
		if (response == null) {
			throw new RuntimeException("No response received.");
		}
		
		if (response.getStatus() != Response.Status.OK.getStatusCode()) {
			if(command.getData() != null) {
				log.error("Data response:" + command.getData().toString());
			}

			String readEntity = response.readEntity(String.class);

			throw new RuntimeException("Failed : HTTP error code : " + response.getStatus() + " -> " + readEntity);
		}

		EnvelopeVO envelopeVO = null;
		Object resultVO = null;
		Gson gson = new Gson();
		String responseStr = response.readEntity(String.class);

		if(command.isResultJson()) {
			resultVO = gson.fromJson(responseStr, command.getResponseType());
		}

		if (resultVO != null && resultVO instanceof EnvelopeVO) {
			envelopeVO = (EnvelopeVO) resultVO;
		} else {
			envelopeVO = new EnvelopeVO();

			if(command.getResponseType() != null && command.getResponseType().equals(DataConsumptionVO.class)) {
				resultVO = gson.fromJson(gson.fromJson(responseStr, JsonElement.class).getAsJsonObject().get(((CommandDataConsumption) command).getEntityType()), DataConsumptionEntitiesRecordVO.class);
			}

			if(command.getResponseType() != null) {
				List<GenericVO> genericVO = new ArrayList<>();
				genericVO.add((GenericVO) resultVO);
				envelopeVO.setHits(genericVO);
			}
		}

		return envelopeVO;
	}
	
	


	/**
	 * Instantiates and initializes a trust-all SSL Context
	 * @return
	 */
	protected SSLContext getTrustAllSSLContext() {
		SSLContext sc = null;

		try {
			sc = SSLContext.getInstance("SSL");
		} catch (NoSuchAlgorithmException e) {
			log.error("Error registering SSLContext", e);
		}
		try {
			sc.init(null, new TrustManager[] { new DefaultTrustManager() }, new SecureRandom());
		} catch (KeyManagementException e) {
			log.error("Error initializing SSLContext", e);
		}

		return sc;
	}

	public static class DefaultTrustManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}
		
	}
	
	public static class DefaultHostnameVerifier implements HostnameVerifier {
		
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}

	}
}
