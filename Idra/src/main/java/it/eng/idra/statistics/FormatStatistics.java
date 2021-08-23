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

package it.eng.idra.statistics;

// TODO: Auto-generated Javadoc
/**
 * The Class FormatStatistics.
 */
public class FormatStatistics {

  /** The format. */
  private String format;

  /** The cnt. */
  private int cnt;

  /**
   * Instantiates a new format statistics.
   */
  public FormatStatistics() {
    // TODO Auto-generated constructor stub
  }

  /**
   * Instantiates a new format statistics.
   *
   * @param format the format
   * @param cnt    the cnt
   */
  public FormatStatistics(String format, int cnt) {
    super();
    this.format = format;
    this.cnt = cnt;
  }

  /**
   * Gets the format.
   *
   * @return the format
   */
  public String getFormat() {
    return format;
  }

  /**
   * Sets the format.
   *
   * @param format the new format
   */
  public void setFormat(String format) {
    this.format = format;
  }

  /**
   * Gets the cnt.
   *
   * @return the cnt
   */
  public int getCnt() {
    return cnt;
  }

  /**
   * Sets the cnt.
   *
   * @param cnt the new cnt
   */
  public void setCnt(int cnt) {
    this.cnt = cnt;
  }

}
