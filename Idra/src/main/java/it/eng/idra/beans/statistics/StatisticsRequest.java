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

import it.eng.idra.utils.JsonRequired;
import java.time.ZonedDateTime;

// TODO: Auto-generated Javadoc
/**
 * The Class StatisticsRequest.
 */
public class StatisticsRequest {

  /** The aggregation level. */
  @JsonRequired
  private AggregationLevelEnum aggregationLevel;

  /** The start date. */
  @JsonRequired
  private ZonedDateTime startDate;

  /** The end date. */
  @JsonRequired
  private ZonedDateTime endDate;

  /** The countries. */
  private String[] countries;

  /** The nodes id. */
  private String[] nodesId;

  /**
   * Instantiates a new statistics request.
   *
   * @param aggregationLevel the aggregation level
   * @param startDate        the start date
   * @param endDate          the end date
   * @param nodesId          the nodes id
   */
  public StatisticsRequest(AggregationLevelEnum aggregationLevel, ZonedDateTime startDate,
      ZonedDateTime endDate, String[] nodesId) {
    super();
    this.aggregationLevel = aggregationLevel;
    this.startDate = startDate;
    this.endDate = endDate;
    this.nodesId = nodesId;
  }

  /**
   * Gets the aggregation level.
   *
   * @return the aggregation level
   */
  public AggregationLevelEnum getAggregationLevel() {
    return aggregationLevel;
  }

  /**
   * Sets the aggregation level.
   *
   * @param aggregationLevel the new aggregation level
   */
  public void setAggregationLevel(AggregationLevelEnum aggregationLevel) {
    this.aggregationLevel = aggregationLevel;
  }

  /**
   * Gets the start date.
   *
   * @return the start date
   */
  public ZonedDateTime getStartDate() {
    return startDate;
  }

  /**
   * Sets the start date.
   *
   * @param startDate the new start date
   */
  public void setStartDate(ZonedDateTime startDate) {
    this.startDate = startDate;
  }

  /**
   * Gets the end date.
   *
   * @return the end date
   */
  public ZonedDateTime getEndDate() {
    return endDate;
  }

  /**
   * Sets the end date.
   *
   * @param endDate the new end date
   */
  public void setEndDate(ZonedDateTime endDate) {
    this.endDate = endDate;
  }

  /**
   * Gets the nodes id.
   *
   * @return the nodes id
   */
  public String[] getNodesId() {
    return nodesId;
  }

  /**
   * Sets the nodes id.
   *
   * @param nodesId the new nodes id
   */
  public void setNodesId(String[] nodesId) {
    this.nodesId = nodesId;
  }

  /**
   * Gets the countries.
   *
   * @return the countries
   */
  public String[] getCountries() {
    return countries;
  }

  /**
   * Sets the countries.
   *
   * @param countries the new countries
   */
  public void setCountries(String[] countries) {
    this.countries = countries;
  }

}
