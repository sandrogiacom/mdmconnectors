package com.totvslabs.mdm.client.pojo;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.JOptionPane;

import com.totvslabs.mdm.client.util.PersistenceEngine;

public class StoredConfigurationVO extends StoredAbstractVO {
	private static final long serialVersionUID = 1L;
	private DataSourceTypeEnum typeEnum;
	private String fluigDataName;
	private String datasourceID;
	private String sourceName;
	private Date lastExecution;
	private Integer quantity;
	private Set<String> dataSent = new LinkedHashSet<String>();

	@Override
	public Boolean validate() {
		StoredAbstractVO datasource = PersistenceEngine.getInstance().getByName(this.getDatasourceID(), StoredJDBCConnectionVO.class);
		StoredAbstractVO fluigData = PersistenceEngine.getInstance().getByName(this.getFluigDataName(), StoredFluigDataProfileVO.class);

		if(datasource == null) {
			JOptionPane.showMessageDialog(null, "Please, make sure the Database Connection Properties is saved!");
		}

		if(fluigData == null) {
			JOptionPane.showMessageDialog(null, "Please, make sure the Fluig Data Connection is saved!");
		}

		return !(datasource == null || fluigData == null);
	}
	public DataSourceTypeEnum getTypeEnum() {
		return typeEnum;
	}
	public Set<String> getDataSent() {
		return this.dataSent;
	}
	public void setTypeEnum(DataSourceTypeEnum typeEnum) {
		this.typeEnum = typeEnum;
	}
	public String getFluigDataName() {
		return fluigDataName;
	}
	public void setFluigDataName(String fluigDataName) {
		this.fluigDataName = fluigDataName;
	}
	public String getDatasourceID() {
		return datasourceID;
	}
	public void setDatasourceID(String datasourceID) {
		this.datasourceID = datasourceID;
	}
	public String getSourceName() {
		return sourceName;
	}
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}
	public Date getLastExecution() {
		return lastExecution;
	}
	public void setLastExecution(Date lastExecution) {
		this.lastExecution = lastExecution;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	@Override
	public String getName() {
		return fluigDataName + datasourceID + sourceName;
	}
	@Override
	public String toString() {
		return "StoredConfigurationVO [typeEnum=" + typeEnum
				+ ", fluigDataName=" + fluigDataName + ", datasourceID="
				+ datasourceID + ", sourceName=" + sourceName
				+ ", lastExecution=" + lastExecution + ", quantity=" + quantity
				+ ", dataSent=" + dataSent + "]";
	}
}
