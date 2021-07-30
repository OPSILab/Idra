/*******************************************************************************
 * Idra - Open Data Federation Platform
 *  Copyright (C) 2020 Engineering Ingegneria Informatica S.p.A.
 *  
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *  
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package it.eng.idra.beans.statistics;

import com.google.gson.annotations.SerializedName;

public class OdmsStatisticsResult {

  private int added;
  private int deleted;
  private int updated;
  @SerializedName(value = "added_RDF")
  private int addedRdf;
  @SerializedName(value = "deleted_RDF")
  private int deletedRdf;
  @SerializedName(value = "updated_RDF")
  private int updatedRdf;

  public OdmsStatisticsResult() {

  }

  /**
   * Instantiates a new odms statistics result.
   *
   * @param added the added
   * @param deleted the deleted
   * @param updated the updated
   * @param addedRdf the added RDF
   * @param deletedRdf the deleted RDF
   * @param updatedRdf the updated RDF
   */
  public OdmsStatisticsResult(int added, int deleted, 
      int updated, int addedRdf, int deletedRdf, int updatedRdf) {
    this.added = added;
    this.deleted = deleted;
    this.updated = updated;
    this.addedRdf = addedRdf;
    this.deletedRdf = deletedRdf;
    this.updatedRdf = updatedRdf;
  }

  public int getAdded() {
    return added;
  }

  public void setAdded(int added) {
    this.added = added;
  }

  public int getDeleted() {
    return deleted;
  }

  public void setDeleted(int deleted) {
    this.deleted = deleted;
  }

  public int getUpdated() {
    return updated;
  }

  public void setUpdated(int updated) {
    this.updated = updated;
  }

  public int getAddedRdf() {
    return addedRdf;
  }

  public void setAddedRdf(int addedRdf) {
    this.addedRdf = addedRdf;
  }

  public int getDeletedRdf() {
    return deletedRdf;
  }

  public void setDeletedRdf(int deletedRdf) {
    this.deletedRdf = deletedRdf;
  }

  public int getUpdatedRdf() {
    return updatedRdf;
  }

  public void setUpdatedRdf(int updatedRdf) {
    this.updatedRdf = updatedRdf;
  }

  @Override
  public String toString() {
    return "ODMSStatisticsResult [added=" + added 
        + ", deleted=" + deleted + ", updated=" + updated + ", added_RDF="
        + addedRdf + ", deleted_RDF=" + deletedRdf + ", updated_RDF=" + updatedRdf + "]";
  }
}
