package it.eng.idra.beans.opendatasoft;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class InnerDataset {

  @SerializedName(value = "dataset_id")
  private String datasetId;
  @SerializedName(value = "dataset_uid")
  private String datasetUid;
  private List<Object> attachements;
  @SerializedName(value = "has_records")
  private Boolean hasRecords;
  @SerializedName(value = "data_visible")
  private Boolean dataVisible;

  private List<InnerDatasetField> fields;
  private DatasetMeta metas;
  private List<String> features;

  public String getDatasetId() {
    return datasetId;
  }

  public void setDatasetId(String datasetId) {
    this.datasetId = datasetId;
  }

  public String getDataset_uid() {
    return datasetUid;
  }

  public void setDatasetUid(String datasetUid) {
    this.datasetUid = datasetUid;
  }

  public List<Object> getAttachements() {
    return attachements;
  }

  public void setAttachements(List<Object> attachements) {
    this.attachements = attachements;
  }

  public Boolean getHasRecords() {
    return hasRecords;
  }

  public void setHasRecords(Boolean hasRecords) {
    this.hasRecords = hasRecords;
  }

  public Boolean getDataVisible() {
    return dataVisible;
  }

  public void setDataVisible(Boolean dataVisible) {
    this.dataVisible = dataVisible;
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
