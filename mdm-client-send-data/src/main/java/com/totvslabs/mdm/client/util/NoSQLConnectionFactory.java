package com.totvslabs.mdm.client.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import org.bson.Document;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.util.JSON;
import com.totvslabs.mdm.client.pojo.JDBCDatabaseVO;
import com.totvslabs.mdm.client.pojo.JDBCFieldVO;
import com.totvslabs.mdm.client.pojo.JDBCIndexVO;
import com.totvslabs.mdm.client.pojo.JDBCTableVO;
import com.totvslabs.mdm.client.pojo.MDMJsonData;
import com.totvslabs.mdm.client.pojo.StoredJDBCConnectionVO;

public class NoSQLConnectionFactory extends DBConnectionFactory {
//	private static Logger log = Logger.getLogger(NoSQLConnectionFactory.class.getCanonicalName());
	private static Map<String, MongoDatabase> connections = new HashMap<String, MongoDatabase>();

	@SuppressWarnings("resource")
	public MongoDatabase getConnection(String url, String driver, String user, String password) {
		StringBuffer key = new StringBuffer();
		key.append(url);
		key.append(driver);
		key.append(user);
		key.append(password);

		String host = null;
		Integer port = 27017;
		String databaseName = null;
		int cont = 0;

		StringTokenizer st = new StringTokenizer(url, ":");
		while(st.hasMoreElements()) {
			String value = st.nextToken();

			if(cont == 0) {
				host = value;
			}
			else if(cont == 1) {
				port = Integer.parseInt(value);
			}
			else if(cont == 2) {
				databaseName = value;
			}

			cont++;
		}

		MongoDatabase database = connections.get(key.toString());

		if(database == null) {
			List<MongoCredential> credentials = new ArrayList<MongoCredential>();
			credentials.add(
			    MongoCredential.createMongoCRCredential(
			        user,
			        databaseName,
			        password.toCharArray()
			    )
			);

			MongoClient connection = null;
			
			if((user == null && password == null) || (user != null && user.trim().length() == 0 && password != null && password.trim().length() == 0)) {
				connection = new MongoClient(host, port);
			}
			else {
				connection = new MongoClient(new ServerAddress(host, port), credentials);
			}
			database = connection.getDatabase(databaseName);

			connections.put(key.toString(), database);
		}

		return database;
	}

	public Long getTotalRecords(StoredJDBCConnectionVO jdbcConnectionVO, JDBCTableVO tableVO) {
		MongoDatabase database = this.getConnection(jdbcConnectionVO.getUrl(), jdbcConnectionVO.getDriver(), jdbcConnectionVO.getUsername(), jdbcConnectionVO.getPassword());
		Long totalRecords = 0l;

		totalRecords = database.getCollection(tableVO.getInternalName()).count();

		return totalRecords;
	}

	public MDMJsonData loadData(StoredJDBCConnectionVO jdbcConnectionVO, JDBCTableVO tableVO, int initialRecord, int quantity) {
		long initialTime = System.currentTimeMillis();

		MongoDatabase database = this.getConnection(jdbcConnectionVO.getUrl(), jdbcConnectionVO.getDriver(), jdbcConnectionVO.getUsername(), jdbcConnectionVO.getPassword());
		JsonArray jsonRecords = new JsonArray();
		MDMJsonData returnObj = new MDMJsonData(tableVO.getInternalName(), jsonRecords);

		FindIterable<Document> limit = database.getCollection(tableVO.getInternalName()).find().skip(initialRecord).limit(quantity);

		for (Document document : limit) {
			JsonObject parse = (JsonObject) new JsonParser().parse(JSON.serialize(document));
			String idValue = document.get("_id").toString();
			String classValue = document.get("_class") != null ? document.get("_class").toString() : null;

			if(idValue != null) {
				parse.remove("_id");
				parse.addProperty("idInternalMDB", idValue);
			}

			if(classValue != null) {
				parse.remove("_class");
				parse.addProperty("className", classValue);
			}

			Set<String> keySet = document.keySet();
			for (String string : keySet) {
				String key = string;

				if(key.equals("_id")) {
					key = "idInternalMDB";
				}

				if(key.equals("_class")) {
					key = "className";
				}

				if(document.get(string) != null && document.get(string).getClass() != null && document.get(string).getClass().equals(java.util.Date.class)) {
					DateFormat df = new SimpleDateFormat();
					parse.remove(string);
					if(document.getDate(string) != null) {
						parse.addProperty(key, df.format(document.getDate(string)));
					}
				}

				this.handleDateFieldsAndAvoidIdFields(parse, string);

				if(tableVO.getField(key) == null) {
					//add the field
					JDBCFieldVO fieldVO = this.loadField(key, document);
					tableVO.addField(fieldVO);
					returnObj.getNewFields().add(fieldVO);
				}
				else {
					Object instanceSample = document.get(string);
					Boolean wasModified = Boolean.FALSE;

					if(instanceSample != null && instanceSample.getClass() != null && instanceSample.getClass().equals(Document.class)) {
						Boolean tempWasModified = verifyFieldChanges(tableVO.getField(key).getMembers(), (Document) instanceSample);
						wasModified = (wasModified ? wasModified : tempWasModified);
					}
					else if(instanceSample != null && instanceSample.getClass() != null && instanceSample.getClass().equals(ArrayList.class)) {
						ArrayList array = (ArrayList) instanceSample;

						if(array.size() > 0) {
							Boolean tempWasModified = verifyFieldChanges(tableVO.getField(key).getMembers(), ((Document) array.get(0)));
							wasModified = (wasModified ? wasModified : tempWasModified);
						}
					}

					if(wasModified) {
						returnObj.getNewFields().add(tableVO.getField(key));
					}
				}
			}

			Set<Entry<String, JsonElement>> entrySet = parse.entrySet();
			for (Entry<String, JsonElement> entry : entrySet) {
				if((entry.getValue() instanceof JsonObject) && ((JsonObject) entry.getValue()).get("$date") != null) {
					entry.setValue(((JsonObject) entry.getValue()).get("$date"));
				}
			}

			jsonRecords.add(parse);
		}

		System.out.println("Took: " + (System.currentTimeMillis() - initialTime));

		return returnObj;
	}

	private Boolean verifyFieldChanges(JsonObject modelInstance, Document instanceSample) {
		Set<String> keySet = instanceSample.keySet();
		Boolean wasModified = Boolean.FALSE;

		for (String string : keySet) {
			if(instanceSample.get(string).getClass().equals(Document.class) || instanceSample.get(string).getClass().equals(ArrayList.class)) {
				Document subInstance = null;

				if(instanceSample.get(string).getClass().equals(Document.class)) {
					subInstance = (Document) instanceSample.get(string);
				}
				else if(((ArrayList) instanceSample.get(string)).size() > 0) {
					subInstance = ((Document) ((ArrayList) instanceSample.get(string)).get(0));
				}

				if(subInstance != null && modelInstance.get(string) == null) {
					if(modelInstance.get(string) == null) {
						wasModified = Boolean.TRUE;
						processNestedField(modelInstance, ((Document) instanceSample));
					}
					else {
						Boolean tempWasModified = this.verifyFieldChanges((JsonObject) modelInstance.get(string), subInstance);
						wasModified = (wasModified ? wasModified : tempWasModified);
					}
				}
			}
			else {
				if(modelInstance.get(string) == null) {
					wasModified = Boolean.TRUE;
					modelInstance.addProperty(string, instanceSample.get(string).getClass().getCanonicalName());
				}
			}
		}

		return wasModified;
	}

	private void handleDateFieldsAndAvoidIdFields(JsonObject object, String field) {
		if(object.get(field) instanceof JsonObject) {
			JsonObject child = (JsonObject) object.get(field);

			Set<Entry<String, JsonElement>> entrySet = child.entrySet();
			for (Entry<String, JsonElement> entry : entrySet) {
				if(entry.getValue() instanceof JsonObject) {
					if(((JsonObject) entry.getValue()).get("$date") != null) {
						entry.setValue(((JsonObject) entry.getValue()).get("$date"));
					}

					if(entry.getValue() instanceof JsonObject) {
						this.handleDateFieldsAndAvoidIdFields((JsonObject) entry.getValue(), entry.getKey());
					}
				}

				if(entry.getKey().equals("_id")) {
					child.remove("_id");
					child.add("id_renamedl", entry.getValue());
				}
			}
		}
		else if(object.get(field) instanceof JsonArray) {
			JsonArray arr = (JsonArray) object.get(field);

			for (JsonElement jsonElement : arr) {
				if(jsonElement instanceof JsonObject) {
					Set<Entry<String, JsonElement>> entrySetArr = ((JsonObject) jsonElement).entrySet();

					for (Entry<String, JsonElement> entryArr : entrySetArr) {
						if(entryArr.getValue() instanceof JsonObject) {
							if(((JsonObject) entryArr.getValue()).get("$date") != null) {
								entryArr.setValue(((JsonObject) entryArr.getValue()).get("$date"));
							}
							
							if(entryArr.getValue() instanceof JsonObject) {
								handleDateFieldsAndAvoidIdFields((JsonObject) entryArr.getValue(), entryArr.getKey());
							}
						}
					}
				}
			}
		}
	}

	public MDMJsonData loadData(StoredJDBCConnectionVO jdbcConnectionVO, JDBCTableVO tableVO) {
		return this.loadData(jdbcConnectionVO, tableVO, 0, 0);
	}

	public void loadFisicModelFields(StoredJDBCConnectionVO jdbcConnectionVO, JDBCTableVO tableVO) {
		this.loadFisicModelFields(jdbcConnectionVO.getUrl(), jdbcConnectionVO.getDriver(), jdbcConnectionVO.getUsername(), jdbcConnectionVO.getPassword(), tableVO);
	}

	private void processNestedField(JsonObject modelInstance, Document instanceSample) {
		Set<String> keySet = instanceSample.keySet();

		for (String string : keySet) {
			if(instanceSample.get(string).getClass().equals(Document.class) || instanceSample.get(string).getClass().equals(ArrayList.class)) {
				Document subInstance = null;

				if(instanceSample.get(string).getClass().equals(Document.class)) {
					subInstance = (Document) instanceSample.get(string);
				}
				else if(((ArrayList) instanceSample.get(string)).size() > 0) {
					subInstance = ((Document) ((ArrayList) instanceSample.get(string)).get(0));
				}

				if(subInstance != null) {
					JsonObject subMember = new JsonObject();
					modelInstance.add(string, subMember);
					this.processNestedField(subMember, subInstance);
				}
			}
			else {
				modelInstance.addProperty(string, instanceSample.get(string).getClass().getCanonicalName());
			}
		}
	}

	public void loadFisicModelFields(String url, String driver, String user, String password, JDBCTableVO tableVO) {
		MongoDatabase database = this.getConnection(url, driver, user, password);

		Document document = database.getCollection(tableVO.getInternalName()).find().first();

		Set<String> keySet = document.keySet();

		for(String key : keySet) {
			JDBCFieldVO fieldVO = this.loadField(key, document);

			tableVO.addField(fieldVO);
		}

		Collection<JDBCIndexVO> indexes = new ArrayList<JDBCIndexVO>();
		JDBCIndexVO indexVO = new JDBCIndexVO();
		indexVO.setName("idInternalMDB");
		indexVO.setUnique(true);

		tableVO.getField("idInternalMDB").setIdentifier(true);
		indexVO.addField(tableVO.getField("idInternalMDB"));
		indexes.add(indexVO);

		tableVO.setPrimaryKey(indexes);
	}

	private JDBCFieldVO loadField(String key, Document document) {
		String columnClassName = document.get(key).getClass().getName();
		JsonObject members = new JsonObject();

		if(key.equals("_id")) {
			key = "idInternalMDB";
		}

		if(key.equals("_class")) {
			key = "className";
		}

		if(columnClassName.equals(Document.class.getCanonicalName())) {
			columnClassName = "object";

			processNestedField(members, ((Document) document.get(key)));
		}
		else if(columnClassName.equals(ArrayList.class.getCanonicalName())) {
			columnClassName = "nested";

			processNestedField(members, ((Document) ((ArrayList<Document>) document.get(key)).get(0)));
		}

		JDBCFieldVO fieldVO = new JDBCFieldVO();
		fieldVO.setName(key);
		fieldVO.setMembers(members);
		fieldVO.setType(columnClassName);

		return fieldVO;
	}

	public JDBCDatabaseVO loadFisicModelTables(String url, String driver, String user, String password) {
		MongoDatabase database = this.getConnection(url, driver, user, password);
		JDBCDatabaseVO databaseByFisicModelVO = new JDBCDatabaseVO();

		if(database == null) {
			return null;
		}

		MongoIterable<String> listCollectionNames = database.listCollectionNames();

		for (String string : listCollectionNames) {
			JDBCTableVO tableVO = new JDBCTableVO(string, database.getName());

			databaseByFisicModelVO.addTable(tableVO);
		}

		return databaseByFisicModelVO;
	}
}
