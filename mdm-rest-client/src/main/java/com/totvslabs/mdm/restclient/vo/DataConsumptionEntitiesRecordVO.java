package com.totvslabs.mdm.restclient.vo;

import java.util.List;

public class DataConsumptionEntitiesRecordVO extends GenericVO {
	private Integer lastCounterForEntity;
	private Integer numOfPendingRecords;
	private Integer lastCounterConsumed;
	private List<GoldenRecordVO> goldenRecords;
	/**
	 * @return the lastCounterForEntity
	 */
	public Integer getLastCounterForEntity() {
		return lastCounterForEntity;
	}
	/**
	 * @param lastCounterForEntity the lastCounterForEntity to set
	 */
	public void setLastCounterForEntity(Integer lastCounterForEntity) {
		this.lastCounterForEntity = lastCounterForEntity;
	}
	/**
	 * @return the numOfPendingRecords
	 */
	public Integer getNumOfPendingRecords() {
		return numOfPendingRecords;
	}
	/**
	 * @param numOfPendingRecords the numOfPendingRecords to set
	 */
	public void setNumOfPendingRecords(Integer numOfPendingRecords) {
		this.numOfPendingRecords = numOfPendingRecords;
	}
	/**
	 * @return the goldenRecords
	 */
	public List<GoldenRecordVO> getGoldenRecords() {
		return goldenRecords;
	}
	/**
	 * @param goldenRecords the goldenRecords to set
	 */
	public void setGoldenRecords(List<GoldenRecordVO> goldenRecords) {
		this.goldenRecords = goldenRecords;
	}
	public Integer getLastCounterConsumed() {
		return lastCounterConsumed;
	}
	public void setLastCounterConsumed(Integer lastCounterConsumed) {
		this.lastCounterConsumed = lastCounterConsumed;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String
				.format("DataConsumptionVO [lastCounterForEntity=%s, numOfPendingRecords=%s, goldenRecords=%s]",
						lastCounterForEntity, numOfPendingRecords,
						goldenRecords);
	}

}
