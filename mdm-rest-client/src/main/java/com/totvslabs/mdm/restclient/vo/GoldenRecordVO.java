package com.totvslabs.mdm.restclient.vo;

import java.util.List;
import java.util.Map;

public class GoldenRecordVO {
	private String mdmEntityTemplateId;
    private Map<String, Object> mdmGoldenFieldAndValues;
    private List<GoldenRecordCrossWalkVO> mdmCrosswalk;
    private Integer mdmCounterForEntity;
    private String mdmProfileTitle;
    private String mdmId;
    private String mdmElasticsearchMappingType;

	public List<GoldenRecordCrossWalkVO> getMdmCrosswalk() {
		return mdmCrosswalk;
	}
	public void setMdmCrosswalk(List<GoldenRecordCrossWalkVO> mdmCrosswalk) {
		this.mdmCrosswalk = mdmCrosswalk;
	}
	public String getMdmEntityTemplateId() {
		return mdmEntityTemplateId;
	}
	public void setMdmEntityTemplateId(String mdmEntityTemplateId) {
		this.mdmEntityTemplateId = mdmEntityTemplateId;
	}
	public Map<String, Object> getMdmGoldenFieldAndValues() {
		return mdmGoldenFieldAndValues;
	}
	public void setMdmGoldenFieldAndValues(Map<String, Object> mdmGoldenFieldAndValues) {
		this.mdmGoldenFieldAndValues = mdmGoldenFieldAndValues;
	}
	public Integer getMdmCounterForEntity() {
		return mdmCounterForEntity;
	}
	public void setMdmCounterForEntity(Integer mdmCounterForEntity) {
		this.mdmCounterForEntity = mdmCounterForEntity;
	}
	public String getMdmProfileTitle() {
		return mdmProfileTitle;
	}
	public void setMdmProfileTitle(String mdmProfileTitle) {
		this.mdmProfileTitle = mdmProfileTitle;
	}
	public String getMdmId() {
		return mdmId;
	}
	public void setMdmId(String mdmId) {
		this.mdmId = mdmId;
	}
	public String getMdmElasticsearchMappingType() {
		return mdmElasticsearchMappingType;
	}
	public void setMdmElasticsearchMappingType(String mdmElasticsearchMappingType) {
		this.mdmElasticsearchMappingType = mdmElasticsearchMappingType;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String
				.format("GoldenRecordVO [mdmEntityTemplateId=%s, mdmGoldenFieldAndValues=%s, mdmCounterForEntity=%s, mdmProfileTitle=%s, mdmId=%s, mdmElasticsearchMappingType=%s]",
						mdmEntityTemplateId, mdmGoldenFieldAndValues,
						mdmCounterForEntity, mdmProfileTitle, mdmId,
						mdmElasticsearchMappingType);
	}    
	
	
}
