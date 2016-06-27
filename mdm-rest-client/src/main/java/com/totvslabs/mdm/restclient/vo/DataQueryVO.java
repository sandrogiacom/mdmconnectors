package com.totvslabs.mdm.restclient.vo;

import java.util.ArrayList;
import java.util.List;

public class DataQueryVO extends GenericVO {
	private Integer count;
	private Integer totalHits;
	private Integer took;
	private List<DataQueryHitVO> hits = new ArrayList<DataQueryHitVO>();

	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public Integer getTotalHits() {
		return totalHits;
	}
	public void setTotalHits(Integer totalHits) {
		this.totalHits = totalHits;
	}
	public Integer getTook() {
		return took;
	}
	public void setTook(Integer took) {
		this.took = took;
	}
	public List<DataQueryHitVO> getHits() {
		return hits;
	}
	public void setHits(List<DataQueryHitVO> hits) {
		this.hits = hits;
	}
}
