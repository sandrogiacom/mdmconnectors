package com.totvslabs.mdm.client.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MappingUtil {
	public static final Map<String, Map<String, Map<String, Map<String, String>>>> mappings = new HashMap<String, Map<String, Map<String, Map<String, String>>>>();
	
	public static final String TYPE_CUSTOMER = "customer";
	public static final String PROD_PROTHEUS = "protheus";

	public static final String NESTED_INSTANCE_DEFAULT = "DEFAULT";
	public static final String NESTED_INSTANCE_BILLING = "BILLING";
	public static final String NESTED_INSTANCE_DELIVERY = "DELIVERY";

	public static final Map<String, List<String>> NESTED_INSTANCES = new HashMap<String, List<String>>();

	static {
		List<String> NESTED_ADDRESS_INSTANCES = new ArrayList<String>();
		List<String> NESTED_EMAIL_INSTANCES = new ArrayList<String>();
		List<String> NESTED_PHONE_INSTANCES = new ArrayList<String>();

		NESTED_INSTANCES.put("_mdmaddress", NESTED_ADDRESS_INSTANCES);
		NESTED_INSTANCES.put("_mdmemail", NESTED_EMAIL_INSTANCES);
		NESTED_INSTANCES.put("_mdmphone", NESTED_PHONE_INSTANCES);

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
		def.put("_mdmname", "A1_NOME");
		def.put("_mdmentitytype", "A1_PESSOA");
		def.put("_mdmaddress1", "A1_END");
		def.put("_mdmdba", "A1_NREDUZ");
		def.put("_mdmaddress3", "A1_BAIRRO");
		def.put("", "A1_TIPO");
		def.put("_mdmstate", "A1_EST");
		def.put("_mdmzipcode", "A1_CEP");
		def.put("_mdmcity", "A1_MUN");
		bil.put("_mdmaddress1", "A1_ENDCOB");
		def.put("_mdmphonenumber", "A1_DDD");
		del.put("_mdmaddress1", "A1_ENDENT");
		def.put("_mdmphonenumber", "A1_TEL");
		def.put("_mdmtaxid", "A1_CGC");
		def.put("_mdmstatetaxid", "A1_INSCR");
		def.put("_mdmcitytaxid", "A1_INSCRM");
		def.put("_mdmcountry", "A1_PAIS");
		def.put("", "A1_VEND");
		def.put("", "A1_TRANSP");
		def.put("_mdmhomepage", "A1_HPAGE");
		bil.put("_mdmaddress3", "A1_BAIRROC");
		bil.put("_mdmzipcode", "A1_CEPC");
		bil.put("_mdmcity", "A1_MUNC");
		bil.put("_mdmstate", "A1_ESTC");
		del.put("_mdmaddress3", "A1_BAIRROE");
		del.put("_mdmzipcode", "A1_CEPE");
		del.put("_mdmcity", "A1_MUNE");
		del.put("_mdmstate", "A1_ESTE");
		def.put("_mdmemailaddress", "A1_EMAIL");
		def.put("_mdmaddress2", "A1_COMPLEM");		
		def.put("_mdmregisterdate", "A1_DTCAD");
		def.put("internalcode", "A1_COD");
	}
}
