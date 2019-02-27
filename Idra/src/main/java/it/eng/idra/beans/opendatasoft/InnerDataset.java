package it.eng.idra.beans.opendatasoft;

import java.util.List;

public class InnerDataset {

	private String dataset_id;
	private String dataset_uid;
	private List<Object> attachements;
	private Boolean has_records;
	private Boolean data_visible;
	
	private List<InnerDatasetField> fields;
	private DatasetMeta metas;
	private List<String> features;
	
	
	public String getDataset_id() {
		return dataset_id;
	}
	public void setDataset_id(String dataset_id) {
		this.dataset_id = dataset_id;
	}
	public String getDataset_uid() {
		return dataset_uid;
	}
	public void setDataset_uid(String dataset_uid) {
		this.dataset_uid = dataset_uid;
	}
	public List<Object> getAttachements() {
		return attachements;
	}
	public void setAttachements(List<Object> attachements) {
		this.attachements = attachements;
	}
	public Boolean getHas_records() {
		return has_records;
	}
	public void setHas_records(Boolean has_records) {
		this.has_records = has_records;
	}
	public Boolean getData_visible() {
		return data_visible;
	}
	public void setData_visible(Boolean data_visible) {
		this.data_visible = data_visible;
	}
	public List<InnerDatasetField> getFields() {
		return fields;
	}
	public void setFields(List<InnerDatasetField> fields) {
		this.fields = fields;
	}

	public DatasetMeta getMetas() {
		return metas;
	}
 
	public void setMetas(DatasetMeta metas) {
		this.metas = metas;
	}
	public List<String> getFeatures() {
		return features;
	}
	public void setFeatures(List<String> features) {
		this.features = features;
	}
	
}
