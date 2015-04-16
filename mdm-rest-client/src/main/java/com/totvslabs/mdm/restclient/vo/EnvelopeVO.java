package com.totvslabs.mdm.restclient.vo;

import java.util.List;

public class EnvelopeVO {
	private Long count;
	private Long totalHits;
	private Long took;
	private List<GenericVO> hits;

	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
	public Long getTotalHits() {
		return totalHits;
	}
	public void setTotalHits(Long totalHits) {
		this.totalHits = totalHits;
	}
	public Long getTook() {
		return took;
	}
	public void setTook(Long took) {
		this.took = took;
	}
	public List<GenericVO> getHits() {
		return hits;
	}
	public void setHits(List<GenericVO> hits) {
		this.hits = hits;
	}
	@Override
	public String toString() {
		return "EnvelopeVO [count=" + count + ", totalHits=" + totalHits
				+ ", took=" + took + ", hits=" + hits + "]";
	}
}
