package com.totvslabs.mdm.client.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Stream;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.totvslabs.mdm.client.pojo.StoredAbstractVO;
import com.totvslabs.mdm.client.pojo.StoredFluigDataProfileVO;
import com.totvslabs.mdm.client.pojo.StoredRecordHashVO;

public class FileConsume {
	private static final Logger LOGGER = Logger.getLogger(JDBCConnectionConsume.class.getName());

	private static Integer BATCH_SIZE = 3000;
	public static Boolean IGNORE_LOCAL_CACHE = Boolean.TRUE;

	private List<String> filesToSend;
	private Map<String, FilesDetails> filesDetails;
	private List<String> filesSent;
	private static Map<String, FileConsume> instances = new HashMap<String, FileConsume>();

	private FileConsume() {
		this.filesToSend = new ArrayList<String>();
		this.filesSent = new ArrayList<String>();
		this.filesDetails = new HashMap<String, FilesDetails>();
	}

	public synchronized static FileConsume getInstance(String connectionName) {
		if(FileConsume.instances.get(connectionName) == null) {
			FileConsume.instances.put(connectionName, new FileConsume());
		}

		return FileConsume.instances.get(connectionName);
	}

	public List<String> getFilesToSend() {
		return this.filesToSend;
	}

	public List<String> getFilesSent() {
		return this.filesSent;
	}

	private void processLine(FilesDetails file, String line, JsonArray jsonArray) {
		switch(file.getType()) {
			case CSV:
				if(file.getActualLineNumber() == 0l) {
					if(file.getHeader().size() == 0) {
						String[] headsArr = line.split(";(?=([^\']*\'[^\']*\')*[^\']*$)");
						
						for(String value : headsArr) {
							if(value.contains("#")) {
								file.getPks().add(value.replaceAll("#", ""));
							}
							
							file.getHeader().add(value.replaceAll("#", ""));
						}
					}
				}
				else {
					String[] headsArr = line.split(";(?=([^\']*\'[^\']*\')*[^\']*$)");
					JsonObject object = new JsonObject();
					int headerCount = 0;

					for(String value : headsArr) {
						try {
							object.addProperty(file.getHeader().get(headerCount), value.replaceAll("'", ""));
							headerCount++;
						}
						catch(Exception e) {
							System.err.println(file.getFileName() + "::::" + line);
						}
					}

					if(jsonArray != null) {
						jsonArray.add(object);
					}
				}

				break;
			case JSON:
				JsonParser parser = new JsonParser();
				if(jsonArray != null) {
					jsonArray.addAll((JsonArray) parser.parse(line));
				}
				break;
			case EXCEL:
				//TODO fixme!!!
				break;
		}
	}

	public JsonArray getRecords(String fileName) {
		FilesDetails entityDetails = this.filesDetails.get(fileName);

		Boolean wasChanged = Boolean.FALSE;
		StoredRecordHashVO vo = new StoredRecordHashVO(entityDetails.getFluigDataProfileVO().getName(), entityDetails.getFileName(), entityDetails.getFileName());
		StoredAbstractVO hash = null;//PersistenceEngine.getInstance().getByName(vo.getName(), StoredRecordHashVO.class); //TODO fixme
		JsonArray lote = new JsonArray();

		if(hash != null && hash instanceof StoredRecordHashVO) {
			vo = (StoredRecordHashVO) hash;
		}

		do {
			JsonArray loteInitial = new JsonArray();

			try {
				synchronized(this) {
					try (Stream<String> lines = Files.lines(Paths.get(entityDetails.getFileName()), Charset.forName("ISO-8859-1"))) {
						lines.skip(this.filesDetails.get(fileName).getActualLineNumber()).limit(BATCH_SIZE).forEach(line -> processLine(entityDetails, line, loteInitial));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					entityDetails.setActualLineNumber(this.filesDetails.get(fileName).getActualLineNumber()+1);
				}
			}
			catch(Exception e) {
				System.err.println("Something wrong when running synchronized method!!!");
				e.printStackTrace();
			}

			long initialTimeMD5 = System.currentTimeMillis();
			MessageDigest m = null;

			try {
				m = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
			}

			for(int i=0; i<loteInitial.size(); i++) {
				JsonElement jsonElement = loteInitial.get(i);
				String hashtext = null;

				if(m != null) {
					m.reset();
					m.update(jsonElement.toString().getBytes());
					byte[] digest = m.digest();
					BigInteger bigInt = new BigInteger(1,digest);
					hashtext = bigInt.toString(16);
				}

				if(FileConsume.IGNORE_LOCAL_CACHE) {
					lote.add(jsonElement);
					vo.getRecordsHash().add(hashtext);
					wasChanged = Boolean.TRUE;
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
			}

			LOGGER.info("--> (" + entityDetails.getFileName() + ") Time to generate the MD5: " + (System.currentTimeMillis() - initialTimeMD5));

			if(wasChanged) {
//				PersistenceEngine.getInstance().save(vo);//TODO fixme
			}

			entityDetails.setActualLineNumber(entityDetails.getActualLineNumber() + ((long) loteInitial.size()));
		}
		while(lote.size() < FileConsume.BATCH_SIZE && (lote.size() + entityDetails.getActualLineNumber() + (entityDetails.getHeader().size() > 0 ? 1 : 0)) < entityDetails.getTotalRecordsToSend());

		return lote;
	}

	public Boolean hasRecords(String entity) {
		FilesDetails entityDetails = this.filesDetails.get(entity);

		if(entityDetails == null) return false;

		return (entityDetails.getActualLineNumber() < entityDetails.getTotalRecordsToSend());
	}

	public Long getTotalRecordsToSend(String entity) {
		FilesDetails entityDetails = this.filesDetails.get(entity);

		if(entityDetails == null) return 0l;

		return entityDetails.getTotalRecordsToSend();
	}

	public List<String> getHeader(String fileName) {
		FilesDetails entityDetails = this.filesDetails.get(fileName);

		if(entityDetails == null) return null;

		return entityDetails.getHeader();
	}

	public List<String> getPks(String fileName) {
		FilesDetails entityDetails = this.filesDetails.get(fileName);

		if(entityDetails == null) return null;

		return entityDetails.getPks();
	}

	public Long getRecordsSent() {
		Long recordsSent = 0l;
		Collection<FilesDetails> values = this.filesDetails.values();

		for (FilesDetails filesDetails : values) {
			recordsSent += filesDetails.getActualLineNumber();
		}

		return recordsSent;
	}

	public Long getTotalRecordsToSend() {
		Long totalRecords = 0l;
		Collection<FilesDetails> values = this.filesDetails.values();

		for (FilesDetails filesDetails : values) {
			totalRecords += filesDetails.getTotalRecordsToSend();
		}

		return totalRecords;
	}

	public String getTypeName(String fileName) {
		FilesDetails entityDetails = this.filesDetails.get(fileName);

		if(entityDetails == null) return null;

		return entityDetails.getTypeName();
	}

	public void addEntity(StoredFluigDataProfileVO fluigDataProfileVO, String fileName) {
		try {
			File location = new File(fileName);

			if(location.isFile()) {
				addPathAsFile(fluigDataProfileVO, location);
			}
			else {
				File[] listFiles = location.listFiles();

				for (File file : listFiles) {
					addPathAsFile(fluigDataProfileVO, file);
				}
			}

			for (String entity : this.filesToSend) {
				FilesDetails entityDetails = filesDetails.get(entity);

				try (Stream<String> lines = Files.lines(Paths.get(entityDetails.getFileName()), Charset.forName("ISO-8859-1"))) {
					lines.skip(0).limit(1).forEach(line -> processLine(entityDetails, line, null));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addPathAsFile(StoredFluigDataProfileVO fluigDataProfileVO, File file) throws IOException {
		Long readFileLineNumber = readFileLineNumber(file);
		FileType fileType = null;

		if(file.getName().toLowerCase().contains(".csv")) {
			fileType = FileType.CSV;
		} else if(file.getName().toLowerCase().contains(".xlsx")) {
			fileType = FileType.EXCEL;
		} else if(file.getName().toLowerCase().contains(".json")) {
			fileType = FileType.JSON;
		}

		if(fileType != null) {
			FilesDetails entityDetails = new FilesDetails(fluigDataProfileVO, file.getPath(), file.getName().substring(0, file.getName().indexOf('.')), fileType, 0l, readFileLineNumber);

			this.filesDetails.put(file.getName(), entityDetails);
			this.filesToSend.add(file.getName());
		}
	}

	private Long readFileLineNumber(File file) throws IOException {
		Long lineNumber = 0l;

		LineNumberReader lnr = new LineNumberReader(new FileReader(file));
		lnr.skip(Long.MAX_VALUE);
		lineNumber = (lnr.getLineNumber() + 1l);
		lnr.close();

		return lineNumber;
	}

	enum FileType {
		CSV,
		EXCEL,
		JSON
	}

	class FilesDetails {
		private Long actualLineNumber;
		private Long totalRecordsToSend;
		private StoredFluigDataProfileVO fluigDataProfileVO;
		private String fileName;
		private String typeName;
		private FileType type;
		private List<String> header;
		private List<String> pks;

		public FilesDetails(StoredFluigDataProfileVO fluigDataProfileVO, String fileName, String typeName, FileType type, Long actualLineNumber, Long totalRecordsToSend) {
			this.fluigDataProfileVO = fluigDataProfileVO;
			this.fileName = fileName;
			this.typeName = typeName;
			this.actualLineNumber = actualLineNumber;
			this.totalRecordsToSend = totalRecordsToSend;
			this.type = type;
			this.header = new ArrayList<String>();
			this.pks = new ArrayList<String>();
		}

		public String getTypeName() {
			return this.typeName;
		}

		public List<String> getPks() {
			return this.pks;
		}

		public List<String> getHeader() {
			return this.header;
		}

		public FileType getType() {
			return type;
		}

		public void setType(FileType type) {
			this.type = type;
		}

		public void setActualLineNumber(Long actualLineNumber) {
			this.actualLineNumber = actualLineNumber;
		}

		public Long getActualLineNumber() {
			return this.actualLineNumber;
		}

		public Long getTotalRecordsToSend() {
			return totalRecordsToSend;
		}

		public void setTotalRecordsToSend(Long totalRecordsToSend) {
			this.totalRecordsToSend = totalRecordsToSend;
		}

		public String getFileName() {
			return this.fileName;
		}

		public StoredFluigDataProfileVO getFluigDataProfileVO() {
			return this.fluigDataProfileVO;
		}
	}
}
