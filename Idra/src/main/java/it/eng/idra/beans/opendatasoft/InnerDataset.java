/*******************************************************************************
 * Idra - Open Data Federation Platform
 * Copyright (C) 2021 Engineering Ingegneria Informatica S.p.A.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

package it.eng.idra.beans.opendatasoft;

import com.google.gson.annotations.SerializedName;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class InnerDataset.
 */
public class InnerDataset {

  /** The dataset id. */
  @SerializedName(value = "dataset_id")
  private String datasetId;

  /** The dataset uid. */
  @SerializedName(value = "dataset_uid")
  private String datasetUid;

  /** The attachements. */
  private List<Object> attachements;

  /** The has records. */
  @SerializedName(value = "has_records")
  private Boolean hasRecords;

  /** The data visible. */
  @SerializedName(value = "data_visible")
  private Boolean dataVisible;

  /** The fields. */
  private List<InnerDatasetField> fields;

  /** The metas. */
  private DatasetMeta metas;

  /** The features. */
  private List<String> features;

  /**
   * Gets the dataset id.
   *
   * @return the dataset id
   */
  public String getDatasetId() {
    return datasetId;
  }

  /**
   * Sets the dataset id.
   *
   * @param datasetId the new dataset id
   */
  public void setDatasetId(String datasetId) {
    this.datasetId = datasetId;
  }

  /**
   * Gets the dataset uid.
   *
   * @return the dataset uid
   */
  public String getDataset_uid() {
    return datasetUid;
  }

  /**
   * Sets the dataset uid.
   *
   * @param datasetUid the new dataset uid
   */
  public void setDatasetUid(String datasetUid) {
    this.datasetUid = datasetUid;
  }

  /**
   * Gets the attachements.
   *
   * @return the attachements
   */
  public List<Object> getAttachements() {
    return attachements;
  }

  /**
   * Sets the attachements.
   *
   * @param attachements the new attachements
   */
  public void setAttachements(List<Object> attachements) {
    this.attachements = attachements;
  }

  /**
   * Gets the checks for records.
   *
   * @return the checks for records
   */
  public Boolean getHasRecords() {
    return hasRecords;
  }

  /**
   * Sets the checks for records.
   *
   * @param hasRecords the new checks for records
   */
  public void setHasRecords(Boolean hasRecords) {
    this.hasRecords = hasRecords;
  }

  /**
   * Gets the data visible.
   *
   * @return the data visible
   */
  public Boolean getDataVisible() {
    return dataVisible;
  }

  /**
   * Sets the data visible.
   *
   * @param dataVisible the new data visible
   */
  public void setDataVisible(Boolean dataVisible) {
    this.dataVisible = dataVisible;
  }

  /**
   * Gets the fields.
   *
   * @return the fields
   */
  public List<InnerDatasetField> getFields() {
    return fields;
  }

  /**
   * Sets the fields.
   *
   * @param fields the new fields
   */
  public void setFields(List<InnerDatasetField> fields) {
    this.fields = fields;
  }

  /**
   * Gets the metas.
   *
   * @return the metas
   */
  public DatasetMeta getMetas() {
    return metas;
  }

  /**
   * Sets the metas.
   *
   * @param metas the new metas
   */
  public void setMetas(DatasetMeta metas) {
    this.metas = metas;
  }

  /**
   * Gets the features.
   *
   * @return the features
   */
  public List<String> getFeatures() {
    return features;
  }

  /**
   * Sets the features.
   *
   * @param features the new features
   */
  public void setFeatures(List<String> features) {
    this.features = features;
  }

}
