package com.totvslabs.mdm.restclient.vo;

import java.util.List;

/**
 * Default Data Wrapper for the MDM Rest Client
 *
 */
public class EnvelopeVO {
	
	private long count;
	private long totalHits;
	private long took;
	private List<GenericVO> hits;

	/**
	 * @return the count
	 */
	public long getCount() {
		return count;
	}


	/**
	 * @param count the count to set
	 */
	public void setCount(long count) {
		this.count = count;
	}


	/**
	 * @return the totalHits
	 */
	public long getTotalHits() {
		return totalHits;
	}


	/**
	 * @param totalHits the totalHits to set
	 */
	public void setTotalHits(long totalHits) {
		this.totalHits = totalHits;
	}


	/**
	 * @return the took
	 */
	public long getTook() {
		return took;
	}


	/**
	 * @param took the took to set
	 */
	public void setTook(long took) {
		this.took = took;
	}


	/**
	 * @return the hits
	 */
	public List<GenericVO> getHits() {
		return hits;
	}

	/**
	 * @return the hits
	 */
	@SuppressWarnings("unchecked")
	public <T> T getHit(@SuppressWarnings("unused") Class<T> hitClass, int index) {
		return (T) hits.get(index);
	}

	/**
	 * @param hits the hits to set
	 */
	public void setHits(List<GenericVO> hits) {
		this.hits = hits;
	}


	@Override
	public String toString() {
		return "EnvelopeVO [count=" + count + ", totalHits=" + totalHits
				+ ", took=" + took + ", hits=" + hits + "]";
	}
}
