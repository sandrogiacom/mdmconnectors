package com.totvslabs.mdm.restclient;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.JerseyClientBuilder;

import com.google.gson.Gson;
import com.totvslabs.mdm.restclient.command.AuthenticationRequired;
import com.totvslabs.mdm.restclient.command.CommandPostStagingC;
import com.totvslabs.mdm.restclient.command.ICommand;
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
		
		this.mdmURL = mdmURL;
	}

	public EnvelopeVO executeCommand(ICommand command) {
		if(command instanceof CommandPostStagingC) {
			this.client.register(GZipReaderInterceptor.class);
			this.client.register(GZipWriterInterceptor.class);
		}

		Map<String, String> parametersHeader = command.getParametersHeader();
		Map<String, String> parameterPath = command.getParameterPath();

		Set<String> keySetPath = parameterPath != null ? parameterPath.keySet() : null;
		Set<String> keySetHeader = parametersHeader != null ? parametersHeader.keySet() : null;

		WebTarget webResource = this.client.target(mdmURL + command.getCommandURL());

		if(keySetPath != null) {
			for (String string : keySetPath) {
				webResource = webResource.queryParam(string, parameterPath.get(string));
			}
		}

		Builder request = webResource.request(MediaType.APPLICATION_JSON);

		if(keySetHeader != null) {
			for (String string : keySetHeader) {
				request = request.header(string, parametersHeader.get(string));
			}
		}

		if(command instanceof AuthenticationRequired) {
			request = request.header("Authorization", MDMRestAuthentication.getInstance().getAuthVO().getAccess_token());
		}

		Response response = null;

		switch(command.getType()) {
			case GET:
				long initialTimeGet = System.currentTimeMillis();

				response = request.accept(MediaType.APPLICATION_JSON).get();

				log.info("Time to execute the GET service ('" + command.getCommandURL() + "'): " + (System.currentTimeMillis() - initialTimeGet) );
				break;

			case POST:
				String type = MediaType.APPLICATION_JSON;
				String additionalInformation = "";

				if(command instanceof CommandPostStagingC) {
					request.header(HttpHeaders.ACCEPT_ENCODING, "gzip");
					additionalInformation = " - COMPRESS";
				}

				Map<String, String> formDataCommand = command.getFormData();
				MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();

				if(formDataCommand != null && formDataCommand.size() > 0) {
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

				if (formData != null && formData.size() > 0) {
					response = request.accept(type).post(Entity.form(formData));
				} else {
					response = request.accept(type).post(Entity.entity(string, type));
				}

				log.info("Time to execute the POST service ('" + command.getCommandURL() + "'" + additionalInformation + "): " + (System.currentTimeMillis() - initialTime) );
				break;
		}

		if (response.getStatus() != Response.Status.OK.getStatusCode()) {
			if(command.getData() != null) {
				log.info("Data response:" + command.getData().toString());
			}

			String readEntity = response.readEntity(String.class);

			throw new RuntimeException("Failed : HTTP error code : " + response.getStatus() + " -> " + readEntity);
		}

		Gson gson = new Gson();
		Object resultVO = gson.fromJson(response.readEntity(String.class), command.getResponseType());
		EnvelopeVO envelopeVO = null;

		if (resultVO instanceof EnvelopeVO) {
			envelopeVO = (EnvelopeVO) resultVO;
		} else {
			envelopeVO = new EnvelopeVO();

			List<GenericVO> genericVO = new ArrayList<GenericVO>();
			genericVO.add((GenericVO) resultVO);
			envelopeVO.setHits(genericVO);
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
