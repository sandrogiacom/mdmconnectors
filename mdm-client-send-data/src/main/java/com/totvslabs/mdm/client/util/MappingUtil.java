package com.totvslabs.mdm.client.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MappingUtil {
	public static final Map<String, Map<String, Map<String, Map<String, String>>>> mappings = new HashMap<String, Map<String, Map<String, Map<String, String>>>>();
	
	public static final String TYPE_CUSTOMER = "mdmcustomer";
	public static final String PROD_PROTHEUS = "protheus";

	public static final String NESTED_INSTANCE_DEFAULT = "DEFAULT";
	public static final String NESTED_INSTANCE_BILLING = "BILLING";
	public static final String NESTED_INSTANCE_DELIVERY = "DELIVERY";

	public static final Map<String, List<String>> NESTED_INSTANCES = new HashMap<String, List<String>>();

	static {
		List<String> NESTED_ADDRESS_INSTANCES = new ArrayList<String>();
		List<String> NESTED_EMAIL_INSTANCES = new ArrayList<String>();
		List<String> NESTED_PHONE_INSTANCES = new ArrayList<String>();

		NESTED_INSTANCES.put("mdmaddress", NESTED_ADDRESS_INSTANCES);
		NESTED_INSTANCES.put("mdmemail", NESTED_EMAIL_INSTANCES);
		NESTED_INSTANCES.put("mdmphone", NESTED_PHONE_INSTANCES);

		NESTED_ADDRESS_INSTANCES.add(NESTED_INSTANCE_DEFAULT);
		NESTED_ADDRESS_INSTANCES.add(NESTED_INSTANCE_BILLING);
		NESTED_ADDRESS_INSTANCES.add(NESTED_INSTANCE_DELIVERY);

		NESTED_EMAIL_INSTANCES.add(NESTED_INSTANCE_DEFAULT);
		NESTED_PHONE_INSTANCES.add(NESTED_INSTANCE_DEFAULT);

		mappings.put(TYPE_CUSTOMER, new HashMap<String, Map<String, Map<String, String>>>());
		mappings.get(TYPE_CUSTOMER).put(PROD_PROTHEUS, new HashMap<String, Map<String, String>>());
		
		mappings.get(TYPE_CUSTOMER).get(PROD_PROTHEUS).put(NESTED_INSTANCE_DEFAULT, new HashMap<String, String>());
		mappings.get(TYPE_CUSTOMER).get(PROD_PROTHEUS).put(NESTED_INSTANCE_BILLING, new HashMap<String, String>());
		mappings.get(TYPE_CUSTOMER).get(PROD_PROTHEUS).put(NESTED_INSTANCE_DELIVERY, new HashMap<String, String>());

		Map<String, String> def = mappings.get(TYPE_CUSTOMER).get(PROD_PROTHEUS).get(NESTED_INSTANCE_DEFAULT);
		Map<String, String> bil = mappings.get(TYPE_CUSTOMER).get(PROD_PROTHEUS).get(NESTED_INSTANCE_BILLING);
		Map<String, String> del = mappings.get(TYPE_CUSTOMER).get(PROD_PROTHEUS).get(NESTED_INSTANCE_DELIVERY);

		def.put("", "A1_FILIAL");
		def.put("", "A1_COD");
		def.put("", "A1_LOJA");
		def.put("mdmname", "A1_NOME");
		def.put("mdmentitytype", "A1_PESSOA");
		def.put("mdmaddress1", "A1_END");
		def.put("mdmdba", "A1_NREDUZ");
		def.put("mdmaddress3", "A1_BAIRRO");
		def.put("", "A1_TIPO");
		def.put("mdmstate", "A1_EST");
		def.put("mdmzipcode", "A1_CEP");
		def.put("mdmcity", "A1_MUN");
		bil.put("mdmaddress1", "A1_ENDCOB");
		def.put("mdmphonenumber", "A1_DDD");
		del.put("mdmaddress1", "A1_ENDENT");
		def.put("mdmphonenumber", "A1_TEL");
		def.put("mdmtaxid", "A1_CGC");
		def.put("mdmstatetaxid", "A1_INSCR");
		def.put("mdmcitytaxid", "A1_INSCRM");
		def.put("mdmcountry", "A1_PAIS");
		def.put("", "A1_VEND");
		def.put("", "A1_TRANSP");
		def.put("mdmhomepage", "A1_HPAGE");
		bil.put("mdmaddress3", "A1_BAIRROC");
		bil.put("mdmzipcode", "A1_CEPC");
		bil.put("mdmcity", "A1_MUNC");
		bil.put("mdmstate", "A1_ESTC");
		del.put("mdmaddress3", "A1_BAIRROE");
		del.put("mdmzipcode", "A1_CEPE");
		del.put("mdmcity", "A1_MUNE");
		del.put("mdmstate", "A1_ESTE");
		def.put("mdmemailaddress", "A1_EMAIL");
		def.put("mdmaddress2", "A1_COMPLEM");		
		def.put("mdmregisterdate", "A1_DTCAD");
		def.put("internalcode", "A1_COD");
	}
}
