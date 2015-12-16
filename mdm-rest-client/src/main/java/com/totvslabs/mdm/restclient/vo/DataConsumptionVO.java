package com.totvslabs.mdm.restclient.vo;

import java.util.List;

public class DataConsumptionVO extends GenericVO {
	private Integer lastCounterForEntity;
	private Integer numOfPendingRecords;
	private List<GoldenRecordVO> goldenRecords;

	public Integer getLastCounterForEntity() {
		return lastCounterForEntity;
	}
	public void setLastCounterForEntity(Integer lastCounterForEntity) {
		this.lastCounterForEntity = lastCounterForEntity;
	}
	public Integer getNumOfPendingRecords() {
		return numOfPendingRecords;
	}
	public void setNumOfPendingRecords(Integer numOfPendingRecords) {
		this.numOfPendingRecords = numOfPendingRecords;
	}
	public List<GoldenRecordVO> getGoldenRecords() {
		return goldenRecords;
	}
	public void setGoldenRecords(List<GoldenRecordVO> goldenRecords) {
		this.goldenRecords = goldenRecords;
	}
}
