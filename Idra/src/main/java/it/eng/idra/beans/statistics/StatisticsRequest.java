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

import it.eng.idra.utils.JsonRequired;
import java.time.ZonedDateTime;

public class StatisticsRequest {

  @JsonRequired
  private AggregationLevelEnum aggregationLevel;
  @JsonRequired
  private ZonedDateTime startDate;
  @JsonRequired
  private ZonedDateTime endDate;

  private String[] countries;

  private String[] nodesId;

  /**
   * Instantiates a new statistics request.
   *
   * @param aggregationLevel the aggregation level
   * @param startDate the start date
   * @param endDate the end date
   * @param nodesId the nodes id
   */
  public StatisticsRequest(AggregationLevelEnum aggregationLevel, 
      ZonedDateTime startDate, ZonedDateTime endDate,
      String[] nodesId) {
    super();
    this.aggregationLevel = aggregationLevel;
    this.startDate = startDate;
    this.endDate = endDate;
    this.nodesId = nodesId;
  }

  public AggregationLevelEnum getAggregationLevel() {
    return aggregationLevel;
  }

  public void setAggregationLevel(AggregationLevelEnum aggregationLevel) {
    this.aggregationLevel = aggregationLevel;
  }

  public ZonedDateTime getStartDate() {
    return startDate;
  }

  public void setStartDate(ZonedDateTime startDate) {
    this.startDate = startDate;
  }

  public ZonedDateTime getEndDate() {
    return endDate;
  }

  public void setEndDate(ZonedDateTime endDate) {
    this.endDate = endDate;
  }

  public String[] getNodesId() {
    return nodesId;
  }

  public void setNodesId(String[] nodesId) {
    this.nodesId = nodesId;
  }

  public String[] getCountries() {
    return countries;
  }

  public void setCountries(String[] countries) {
    this.countries = countries;
  }

}
