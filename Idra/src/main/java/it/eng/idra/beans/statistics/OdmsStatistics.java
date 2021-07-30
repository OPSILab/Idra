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
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

@SqlResultSetMapping(name = "ODMSStatisticsResult", classes = {
    @ConstructorResult(targetClass = OdmsStatisticsResult.class, columns = {
        @ColumnResult(name = "added", type = Integer.class), 
        @ColumnResult(name = "deleted", type = Integer.class),
        @ColumnResult(name = "updated", type = Integer.class), 
        @ColumnResult(name = "added_RDF", type = Integer.class),
        @ColumnResult(name = "deleted_RDF", type = Integer.class),
        @ColumnResult(name = "updated_RDF", type = Integer.class) }) })

@Entity
@Table(name = "odms_statistics")
public class OdmsStatistics {

  private int id;
  @SerializedName(value = "nodeID")
  private int nodeId;
  private String name;
  private int day;
  private int month;
  private int year;
  @SerializedName(value = "added_datasets")
  private int addedDatasets;
  @SerializedName(value = "updated_datasets")
  private int updatedDatasets;
  @SerializedName(value = "deleted_datasets")
  private int deletedDatasets;
  @SerializedName(value = "added_RDF")
  private int addedRdf;
  @SerializedName(value = "updated_RDF")
  private int updatedRdf;
  @SerializedName(value = "deleted_RDF")
  private int deletedRdf;

  public OdmsStatistics() {

  }

  /**
   * Instantiates a new odms statistics.
   *
   * @param nodeId the node ID
   * @param name the name
   * @param day the day
   * @param month the month
   * @param year the year
   * @param addedDatasets the added datasets
   * @param updatedDatasets the updated datasets
   * @param deletedDatasets the deleted datasets
   * @param addedRdf the added RDF
   * @param updatedRdf the updated RDF
   * @param deletedRdf the deleted RDF
   */
  public OdmsStatistics(int nodeId, String name, 
      int day, int month, int year, 
      int addedDatasets, int updatedDatasets,
      int deletedDatasets, int addedRdf, 
      int updatedRdf, int deletedRdf) {
    super();
    this.nodeId = nodeId;
    this.name = name;
    this.day = day;
    this.month = month;
    this.year = year;
    this.addedDatasets = addedDatasets;
    this.updatedDatasets = updatedDatasets;
    this.deletedDatasets = deletedDatasets;
    this.addedRdf = addedRdf;
    this.updatedRdf = updatedRdf;
    this.deletedRdf = deletedRdf;
  }

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @Column(name = "nodeID")
  public int getNodeId() {
    return nodeId;
  }

  public void setNodeId(int nodeId) {
    this.nodeId = nodeId;
  }

  @Column(name = "name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "day")
  public int getDay() {
    return day;
  }

  public void setDay(int day) {
    this.day = day;
  }

  @Column(name = "month")
  public int getMonth() {
    return month;
  }

  public void setMonth(int month) {
    this.month = month;
  }

  @Column(name = "year")
  public int getYear() {
    return year;
  }

  public void setYear(int year) {
    this.year = year;
  }

  @Column(name = "added_datasets")
  public int getAddedDatasets() {
    return addedDatasets;
  }

  public void setAddedDatasets(int addedDatasets) {
    this.addedDatasets = addedDatasets;
  }

  public void incAddedDatasets(int addedDatasets) {
    this.addedDatasets += addedDatasets;
  }

  @Column(name = "updated_datasets")
  public int getUpdatedDatasets() {
    return updatedDatasets;
  }

  public void setUpdatedDatasets(int updatedDatasets) {
    this.updatedDatasets = updatedDatasets;
  }

  public void incUpdatedDatasets(int updatedDatasets) {
    this.updatedDatasets += updatedDatasets;
  }

  @Column(name = "deleted_datasets")
  public int getDeletedDatasets() {
    return deletedDatasets;
  }

  public void setDeletedDatasets(int deletedDatasets) {
    this.deletedDatasets = deletedDatasets;
  }

  public void incDeletedDatasets(int deletedDatasets) {
    this.deletedDatasets += deletedDatasets;
  }

  @Column(name = "added_RDF")
  public int getAddedRdf() {
    return addedRdf;
  }

  public void setAddedRdf(int addedRdf) {
    this.addedRdf = addedRdf;
  }

  @Column(name = "updated_RDF")
  public int getUpdatedRdf() {
    return updatedRdf;
  }

  public void incAddedRdf(int addedRdf) {
    this.addedRdf += addedRdf;
  }

  public void setUpdatedRdf(int updatedRdf) {
    this.updatedRdf = updatedRdf;
  }

  @Column(name = "deleted_RDF")
  public int getDeletedRdf() {
    return deletedRdf;
  }

  public void incUpdatedRdf(int updatedRdf) {
    this.updatedRdf += updatedRdf;
  }

  public void setDeletedRdf(int deletedRdf) {
    this.deletedRdf = deletedRdf;
  }

  public void incDeletedRdf(int deletedRdf) {
    this.deletedRdf += deletedRdf;
  }

  @Override
  public String toString() {
    return "ODMSStatistics [id=" + id + ", nodeID=" 
        + nodeId + ", day=" + day + ", month=" + month + ", year=" + year
        + "]";
  }

}
