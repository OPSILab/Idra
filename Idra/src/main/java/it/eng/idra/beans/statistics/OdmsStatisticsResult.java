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

package it.eng.idra.beans.statistics;

import com.google.gson.annotations.SerializedName;

// TODO: Auto-generated Javadoc
/**
 * The Class OdmsStatisticsResult.
 */
public class OdmsStatisticsResult {

  /** The added. */
  private int added;

  /** The deleted. */
  private int deleted;

  /** The updated. */
  private int updated;

  /** The added rdf. */
  @SerializedName(value = "added_RDF")
  private int addedRdf;

  /** The deleted rdf. */
  @SerializedName(value = "deleted_RDF")
  private int deletedRdf;

  /** The updated rdf. */
  @SerializedName(value = "updated_RDF")
  private int updatedRdf;

  /**
   * Instantiates a new odms statistics result.
   */
  public OdmsStatisticsResult() {

  }

  /**
   * Instantiates a new odms statistics result.
   *
   * @param added      the added
   * @param deleted    the deleted
   * @param updated    the updated
   * @param addedRdf   the added RDF
   * @param deletedRdf the deleted RDF
   * @param updatedRdf the updated RDF
   */
  public OdmsStatisticsResult(int added, int deleted, int updated, int addedRdf, int deletedRdf,
      int updatedRdf) {
    this.added = added;
    this.deleted = deleted;
    this.updated = updated;
    this.addedRdf = addedRdf;
    this.deletedRdf = deletedRdf;
    this.updatedRdf = updatedRdf;
  }

  /**
   * Gets the added.
   *
   * @return the added
   */
  public int getAdded() {
    return added;
  }

  /**
   * Sets the added.
   *
   * @param added the new added
   */
  public void setAdded(int added) {
    this.added = added;
  }

  /**
   * Gets the deleted.
   *
   * @return the deleted
   */
  public int getDeleted() {
    return deleted;
  }

  /**
   * Sets the deleted.
   *
   * @param deleted the new deleted
   */
  public void setDeleted(int deleted) {
    this.deleted = deleted;
  }

  /**
   * Gets the updated.
   *
   * @return the updated
   */
  public int getUpdated() {
    return updated;
  }

  /**
   * Sets the updated.
   *
   * @param updated the new updated
   */
  public void setUpdated(int updated) {
    this.updated = updated;
  }

  /**
   * Gets the added rdf.
   *
   * @return the added rdf
   */
  public int getAddedRdf() {
    return addedRdf;
  }

  /**
   * Sets the added rdf.
   *
   * @param addedRdf the new added rdf
   */
  public void setAddedRdf(int addedRdf) {
    this.addedRdf = addedRdf;
  }

  /**
   * Gets the deleted rdf.
   *
   * @return the deleted rdf
   */
  public int getDeletedRdf() {
    return deletedRdf;
  }

  /**
   * Sets the deleted rdf.
   *
   * @param deletedRdf the new deleted rdf
   */
  public void setDeletedRdf(int deletedRdf) {
    this.deletedRdf = deletedRdf;
  }

  /**
   * Gets the updated rdf.
   *
   * @return the updated rdf
   */
  public int getUpdatedRdf() {
    return updatedRdf;
  }

  /**
   * Sets the updated rdf.
   *
   * @param updatedRdf the new updated rdf
   */
  public void setUpdatedRdf(int updatedRdf) {
    this.updatedRdf = updatedRdf;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "ODMSStatisticsResult [added=" + added + ", deleted=" + deleted + ", updated=" + updated
        + ", added_RDF=" + addedRdf + ", deleted_RDF=" + deletedRdf + ", updated_RDF=" + updatedRdf
        + "]";
  }
}
