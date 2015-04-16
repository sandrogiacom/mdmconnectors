package com.totvslabs.mdm.restclient;

public class MDMRestConnectionFactory {
	public static MDMRestConnection getConnection(String mdmURL) {
		return new MDMRestConnection(mdmURL);
	}
}
