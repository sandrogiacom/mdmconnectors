package com.totvslabs.mdm.client.pojo;

import com.totvslabs.mdm.restclient.vo.DataConsumerDetailsVO;

public class FDEntityVO {
	private String name;
	private String description;
	private String wsURL;
	private DataConsumerDetailsVO fieldsDetail;
	private String entityId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getWsURL() {
		return wsURL;
	}

	public void setWsURL(String wsURL) {
		this.wsURL = wsURL;
	}

	public DataConsumerDetailsVO getFieldsDetail() {
		return fieldsDetail;
	}

	public void setFieldsDetail(DataConsumerDetailsVO fieldsDetail) {
		this.fieldsDetail = fieldsDetail;
	}

	public String getEntityId() {
		return this.entityId;
	}

	public void setEntityId(String string) {
		this.entityId = string;
	}
}
