package com.totvslabs.mdm.client.util;

import java.util.List;

import com.google.gson.JsonArray;
import com.totvslabs.mdm.client.pojo.StoredAbstractVO;
import com.totvslabs.mdm.client.pojo.StoredFluigDataProfileVO;

public class TestFile {

	public static void main(String[] args) {
		//TODO: implement these items:
		//initialize the default configuration for my application :)
		//environments?
		//local databases?
		//what more???

		List<StoredAbstractVO> findAll = PersistenceEngine.getInstance().findAll(StoredFluigDataProfileVO.class);
		StoredFluigDataProfileVO fluigData = null;
		FileConsume.IGNORE_LOCAL_CACHE = Boolean.TRUE;

		for (StoredAbstractVO storedAbstractVO : findAll) {
			if(storedAbstractVO instanceof StoredFluigDataProfileVO) {
				if(((StoredFluigDataProfileVO) storedAbstractVO).getProfileName().equalsIgnoreCase("local")) {
					fluigData = (StoredFluigDataProfileVO) storedAbstractVO;
				}
			}
		}

		FileConsume.getInstance("testing").addEntity(fluigData, "/Users/poffo/Downloads/try/FF_AGENDA_ATENDIMENTO.csv");
		System.out.println("total records: " + FileConsume.getInstance("testing").getTotalRecordsToSend());

		for(int i=0; i<5; i++) {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					do {
						JsonArray records = FileConsume.getInstance("testing").getRecords("FF_AGENDA_ATENDIMENTO.csv");
						System.out.println("" + records.size());
					} while(FileConsume.getInstance("testing").hasRecords("FF_AGENDA_ATENDIMENTO.csv"));
				}
			});
			thread.start();
		}
	}
}
