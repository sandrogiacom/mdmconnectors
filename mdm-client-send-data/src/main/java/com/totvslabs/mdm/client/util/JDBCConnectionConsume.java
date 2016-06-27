package com.totvslabs.mdm.client.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.totvslabs.mdm.client.pojo.JDBCFieldVO;
import com.totvslabs.mdm.client.pojo.JDBCIndexVO;
import com.totvslabs.mdm.client.pojo.JDBCTableVO;
import com.totvslabs.mdm.client.pojo.MDMJsonData;
import com.totvslabs.mdm.client.pojo.StoredAbstractVO;
import com.totvslabs.mdm.client.pojo.StoredFluigDataProfileVO;
import com.totvslabs.mdm.client.pojo.StoredJDBCConnectionVO;
import com.totvslabs.mdm.client.pojo.StoredRecordHashVO;

public class JDBCConnectionConsume {
	private static final Logger LOGGER = Logger.getLogger(JDBCConnectionConsume.class.getName());

	private static Integer BATCH_SIZE = 1000;
	public static Boolean IGNORE_LOCAL_CACHE = Boolean.TRUE;

	private List<String> entitiesToSend;
	private Map<String, EntityDetails> entitiesDetails;
	private List<String> entitiesSent;
	private static Map<String, JDBCConnectionConsume> instances = new HashMap<String, JDBCConnectionConsume>();

	private JDBCConnectionConsume() {
		this.entitiesToSend = new ArrayList<String>();
		this.entitiesSent = new ArrayList<String>();
		this.entitiesDetails = new HashMap<String, EntityDetails>();
	}

	public synchronized static JDBCConnectionConsume getInstance(String connectionName) {
		if(JDBCConnectionConsume.instances.get(connectionName) == null) {
			JDBCConnectionConsume.instances.put(connectionName, new JDBCConnectionConsume());
		}

		return JDBCConnectionConsume.instances.get(connectionName);
	}

	public List<String> getEntitiesToSend() {
		return this.entitiesToSend;
	}

	public List<String> getEntitiesSent() {
		return this.entitiesSent;
	}

	public String getLastIdByEntity(String entity) {
		return this.entitiesDetails.get(entity.toLowerCase()).getActualPK();
	}

	public Integer getTotalRecordsSent(String entity) {
		return this.entitiesDetails.get(entity.toLowerCase()).getRecordsSent();
	}

	public MDMJsonData getRecords(String entityName) {
		EntityDetails entityDetails = this.entitiesDetails.get(entityName.toLowerCase());

		Boolean wasChanged = Boolean.FALSE;
		JsonArray lote = new JsonArray();
		MDMJsonData returnObj = new MDMJsonData(entityName, lote);
		StoredRecordHashVO vo = null;
		StoredAbstractVO hash = null;

		if(entityDetails.getFluigDataProfileVO() != null) {
			vo = new StoredRecordHashVO(entityDetails.getFluigDataProfileVO().getName(), entityDetails.getJdbcConnectionVO().getName(), entityName);
		}

		if(vo != null) {
			hash = PersistenceEngine.getInstance().getByName(vo.getName(), StoredRecordHashVO.class);
		}

		if(hash != null && hash instanceof StoredRecordHashVO) {
			vo = (StoredRecordHashVO) hash;
		}

		Collection<JDBCIndexVO> primaryKey = entityDetails.getTableVO().getPrimaryKey();
		String lastPk = null;

		do {
			MDMJsonData loteInitial = DBConnectionFactory.getDb(entityDetails.getJdbcConnectionVO().getDriver()).loadData(entityDetails.getJdbcConnectionVO(), entityDetails.getTableVO(), entityDetails.getRecordsSent(), (JDBCConnectionConsume.BATCH_SIZE - lote.size()));
			returnObj.getNewFields().addAll(loteInitial.getNewFields());
			long initialTimeMD5 = System.currentTimeMillis();

			for(int i=0; i<loteInitial.getData().size(); i++) {
				JsonElement jsonElement = loteInitial.getData().get(i);

				try {
					MessageDigest m = MessageDigest.getInstance("MD5");
					m.reset();
					m.update(jsonElement.toString().getBytes());
					byte[] digest = m.digest();
					BigInteger bigInt = new BigInteger(1,digest);
					String hashtext = bigInt.toString(16);

					StringBuffer lastPkBuffer = new StringBuffer();

					for(JDBCIndexVO indexVO : primaryKey) {
						List<JDBCFieldVO> fields = indexVO.getFields();

						for(JDBCFieldVO field : fields) {
							if(lastPkBuffer.length() > 0) {
								lastPkBuffer.append("|");
							}

							lastPkBuffer.append(jsonElement.getAsJsonObject().get(field.getName()).getAsString());
						}
					}

					lastPk = lastPkBuffer.toString();

					if(JDBCConnectionConsume.IGNORE_LOCAL_CACHE) {
						lote.add(jsonElement);

						if(vo != null) {
							vo.getRecordsHash().add(hashtext);
							wasChanged = Boolean.TRUE;
						}
					}
					else {
						if(vo.getRecordsHash().contains(hashtext)) {
							continue;
						}
						else {
							vo.getRecordsHash().add(hashtext);
							wasChanged = Boolean.TRUE;
							lote.add(jsonElement);
						}
					}
				} catch (NoSuchAlgorithmException e) {
				}
			}

			LOGGER.info("--> (" + entityName + ") Time to generate the MD5: " + (System.currentTimeMillis() - initialTimeMD5));

			if(wasChanged) {
				PersistenceEngine.getInstance().save(vo);
			}

			entityDetails.setRecordsSent(entityDetails.getRecordsSent() + loteInitial.getData().size());
		}
		while(lote.size() < JDBCConnectionConsume.BATCH_SIZE && (lote.size() + entityDetails.getRecordsSent()) < entityDetails.getTotalRecordsToSend());

		entityDetails.setActualPK(lastPk);

		return returnObj;
	}

	public Boolean hasRecords(String entity) {
		EntityDetails entityDetails = this.entitiesDetails.get(entity.toLowerCase());

		return (entityDetails.getRecordsSent() < entityDetails.getTotalRecordsToSend());
	}

	public void addEntity(StoredJDBCConnectionVO connectionVO, StoredFluigDataProfileVO fluigDataProfileVO, JDBCTableVO tableVO) {
		Long totalRecords = DBConnectionFactory.getDb(connectionVO.getDriver()).getTotalRecords(connectionVO, tableVO);
		EntityDetails entityDetails = new EntityDetails(connectionVO, fluigDataProfileVO, tableVO, null, 0, totalRecords);

		this.entitiesDetails.put(tableVO.getName().toLowerCase(), entityDetails);
	}

	class EntityDetails {
		private String actualPK;
		private Integer recordsSent;
		private Long totalRecordsToSend;
		private StoredJDBCConnectionVO jdbcConnectionVO;
		private StoredFluigDataProfileVO fluigDataProfileVO;
		private JDBCTableVO tableVO;

		public EntityDetails(StoredJDBCConnectionVO connectionVO, StoredFluigDataProfileVO fluigDataProfileVO, JDBCTableVO tableVO, String actualPK, Integer recordsSent, Long totalRecordsToSend) {
			this.jdbcConnectionVO = connectionVO;
			this.fluigDataProfileVO = fluigDataProfileVO;
			this.tableVO = tableVO;
			this.actualPK = actualPK;
			this.recordsSent = recordsSent;
			this.totalRecordsToSend = totalRecordsToSend;
		}

		public void setActualPK(String actualPK) {
			this.actualPK = actualPK;
		}

		public String getActualPK() {
			return this.actualPK;
		}

		public Integer getRecordsSent() {
			return recordsSent;
		}

		public void setRecordsSent(Integer recordsSent) {
			this.recordsSent = recordsSent;
		}

		public Long getTotalRecordsToSend() {
			return totalRecordsToSend;
		}

		public void setTotalRecordsToSend(Long totalRecordsToSend) {
			this.totalRecordsToSend = totalRecordsToSend;
		}

		public StoredJDBCConnectionVO getJdbcConnectionVO() {
			return this.jdbcConnectionVO;
		}

		public JDBCTableVO getTableVO() {
			return this.tableVO;
		}

		public StoredFluigDataProfileVO getFluigDataProfileVO() {
			return this.fluigDataProfileVO;
		}
	}
}
