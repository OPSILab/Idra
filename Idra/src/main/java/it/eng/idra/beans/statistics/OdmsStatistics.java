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
  private int nodeID;
  private String name;
  private int day;
  private int month;
  private int year;
  private int added_datasets;
  private int updated_datasets;
  private int deleted_datasets;
  private int added_RDF;
  private int updated_RDF;
  private int deleted_RDF;

  public OdmsStatistics() {

  }

  /**
   * Instantiates a new odms statistics.
   *
   * @param nodeID the node ID
   * @param name the name
   * @param day the day
   * @param month the month
   * @param year the year
   * @param added_datasets the added datasets
   * @param updated_datasets the updated datasets
   * @param deleted_datasets the deleted datasets
   * @param added_RDF the added RDF
   * @param updated_RDF the updated RDF
   * @param deleted_RDF the deleted RDF
   */
  public OdmsStatistics(int nodeID, String name, 
      int day, int month, int year, 
      int added_datasets, int updated_datasets,
      int deleted_datasets, int added_RDF, 
      int updated_RDF, int deleted_RDF) {
    super();
    this.nodeID = nodeID;
    this.name = name;
    this.day = day;
    this.month = month;
    this.year = year;
    this.added_datasets = added_datasets;
    this.updated_datasets = updated_datasets;
    this.deleted_datasets = deleted_datasets;
    this.added_RDF = added_RDF;
    this.updated_RDF = updated_RDF;
    this.deleted_RDF = deleted_RDF;
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
  public int getNodeID() {
    return nodeID;
  }

  public void setNodeID(int nodeID) {
    this.nodeID = nodeID;
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
  public int getAdded_datasets() {
    return added_datasets;
  }

  public void setAdded_datasets(int added_datasets) {
    this.added_datasets = added_datasets;
  }

  public void incAdded_datasets(int added_datasets) {
    this.added_datasets += added_datasets;
  }

  @Column(name = "updated_datasets")
  public int getUpdated_datasets() {
    return updated_datasets;
  }

  public void setUpdated_datasets(int updated_datasets) {
    this.updated_datasets = updated_datasets;
  }

  public void incUpdated_datasets(int updated_datasets) {
    this.updated_datasets += updated_datasets;
  }

  @Column(name = "deleted_datasets")
  public int getDeleted_datasets() {
    return deleted_datasets;
  }

  public void setDeleted_datasets(int deleted_datasets) {
    this.deleted_datasets = deleted_datasets;
  }

  public void incDeleted_datasets(int deleted_datasets) {
    this.deleted_datasets += deleted_datasets;
  }

  @Column(name = "added_RDF")
  public int getAdded_RDF() {
    return added_RDF;
  }

  public void setAdded_RDF(int added_RDF) {
    this.added_RDF = added_RDF;
  }

  @Column(name = "updated_RDF")
  public int getUpdated_RDF() {
    return updated_RDF;
  }

  public void incAdded_RDF(int added_RDF) {
    this.added_RDF += added_RDF;
  }

  public void setUpdated_RDF(int updated_RDF) {
    this.updated_RDF = updated_RDF;
  }

  @Column(name = "deleted_RDF")
  public int getDeleted_RDF() {
    return deleted_RDF;
  }

  public void incUpdated_RDF(int updated_RDF) {
    this.updated_RDF += updated_RDF;
  }

  public void setDeleted_RDF(int deleted_RDF) {
    this.deleted_RDF = deleted_RDF;
  }

  public void incDeleted_RDF(int deleted_RDF) {
    this.deleted_RDF += deleted_RDF;
  }

  @Override
  public String toString() {
    return "ODMSStatistics [id=" + id + ", nodeID=" 
        + nodeID + ", day=" + day + ", month=" + month + ", year=" + year
        + "]";
  }

}
