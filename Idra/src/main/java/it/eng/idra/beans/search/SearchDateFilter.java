/*******************************************************************************
 * Idra - Open Data Federation Platform
 * Copyright (C) 2021 Engineering Ingegneria Informatica S.p.A.
 * <p> 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * <p> 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

package it.eng.idra.beans.search;

import java.time.ZonedDateTime;

// TODO: Auto-generated Javadoc
/**
 * The Class SearchDateFilter.
 */
public class SearchDateFilter {

  /** The start. */
  private ZonedDateTime start;

  /** The end. */
  private ZonedDateTime end;

  /**
   * Instantiates a new search date filter.
   *
   * @param start the start
   * @param end   the end
   */
  public SearchDateFilter(ZonedDateTime start, ZonedDateTime end) {
    super();
    this.start = start;
    this.end = end;
  }

  /**
   * Gets the start.
   *
   * @return the start
   */
  public ZonedDateTime getStart() {
    return start;
  }

  /**
   * Sets the start.
   *
   * @param start the new start
   */
  public void setStart(ZonedDateTime start) {
    this.start = start;
  }

  /**
   * Gets the end.
   *
   * @return the end
   */
  public ZonedDateTime getEnd() {
    return end;
  }

  /**
   * Sets the end.
   *
   * @param end the new end
   */
  public void setEnd(ZonedDateTime end) {
    this.end = end;
  }

}
