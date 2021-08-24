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

package it.eng.idra.beans;

import it.eng.idra.utils.JsonRequired;
import java.time.ZonedDateTime;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class LogsRequest.
 */
public class LogsRequest {

  /** The level list. */
  @JsonRequired
  private List<String> levelList;

  /** The start date. */
  @JsonRequired
  private ZonedDateTime startDate;

  /** The end date. */
  @JsonRequired
  private ZonedDateTime endDate;

  /**
   * Instantiates a new logs request.
   *
   * @param levelList the level list
   * @param startDate the start date
   * @param endDate   the end date
   */
  public LogsRequest(List<String> levelList, ZonedDateTime startDate, ZonedDateTime endDate) {
    super();
    this.levelList = levelList;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  /**
   * Gets the level list.
   *
   * @return the level list
   */
  public List<String> getLevelList() {
    return levelList;
  }

  /**
   * Sets the level list.
   *
   * @param level the new level list
   */
  public void setLevelList(List<String> level) {
    this.levelList = level;
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

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "LogsRequest [levelList=" + levelList + ", startDate=" + startDate + ", endDate="
        + endDate + "]";
  }

}
