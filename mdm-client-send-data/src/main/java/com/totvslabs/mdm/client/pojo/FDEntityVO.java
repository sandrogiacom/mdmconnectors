package com.totvslabs.mdm.client.pojo;

import java.util.Map;

import com.totvslabs.mdm.restclient.vo.EntityDetailsVO;
import com.totvslabs.mdm.restclient.vo.FieldsVO;

public class FDEntityVO {
	private String name;
	private String description;
	private String wsURL;
	private EntityDetailsVO fieldsDetail;
	private String entityId;
	private Map<String, FieldsVO> mdmFieldsFull;

	public Map<String, FieldsVO> getMdmFieldsFull() {
		return mdmFieldsFull;
	}

	public void setMdmFieldsFull(Map<String, FieldsVO> mdmFieldsFull) {
		this.mdmFieldsFull = mdmFieldsFull;
	}

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

	public EntityDetailsVO getFieldsDetail() {
		return fieldsDetail;
	}

	public void setFieldsDetail(EntityDetailsVO fieldsDetail) {
		this.fieldsDetail = fieldsDetail;
	}

	public String getEntityId() {
		return this.entityId;
	}

	public void setEntityId(String string) {
		this.entityId = string;
	}
}
