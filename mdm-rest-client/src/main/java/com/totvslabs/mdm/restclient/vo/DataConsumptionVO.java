package com.totvslabs.mdm.restclient.vo;

import java.util.List;

/**
 * Data Consumption POJO
 *
 */
public class DataConsumptionVO extends GenericVO {
	private Integer lastCounterForEntity;
	private Integer numOfPendingRecords;
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
