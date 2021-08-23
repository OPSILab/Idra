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

// TODO: Auto-generated Javadoc
/**
 * The Class SearchStatisticsResult.
 */
public class SearchStatisticsResult {

  /** The live. */
  private int live;

  /** The sparql. */
  private int sparql;

  /** The cache. */
  private int cache;
  // private int day;
  // private int month;
  // private int week;
  // private int year;

  /** The start label. */
  private String startLabel;

  /** The end label. */
  private String endLabel;

  /**
   * Instantiates a new search statistics result.
   */
  public SearchStatisticsResult() {

  }

  /**
   * Instantiates a new search statistics result.
   *
   * @param live   the live
   * @param cache  the cache
   * @param sparql the sparql
   */
  public SearchStatisticsResult(int live, int cache, int sparql) {
    this.live = live;
    this.cache = cache;
    this.sparql = sparql;
  }

  /**
   * Gets the live.
   *
   * @return the live
   */
  public int getLive() {
    return live;
  }

  /**
   * Sets the live.
   *
   * @param live the new live
   */
  public void setLive(int live) {
    this.live = live;
  }

  /**
   * Gets the sparql.
   *
   * @return the sparql
   */
  public int getSparql() {
    return sparql;
  }

  /**
   * Sets the sparql.
   *
   * @param sparql the new sparql
   */
  public void setSparql(int sparql) {
    this.sparql = sparql;
  }

  /**
   * Gets the cache.
   *
   * @return the cache
   */
  public int getCache() {
    return cache;
  }

  /**
   * Sets the cache.
   *
   * @param cache the new cache
   */
  public void setCache(int cache) {
    this.cache = cache;
  }

  // public int getDay() {
  // return day;
  // }
  //
  // public void setDay(int day) {
  // this.day = day;
  // }
  //
  // public int getMonth() {
  // return month;
  // }
  //
  // public void setMonth(int month) {
  // this.month = month;
  // }
  //
  // public int getWeek() {
  // return week;
  // }
  //
  // public void setWeek(int week) {
  // this.week = week;
  // }
  //
  // public int getYear() {
  // return year;
  // }
  //
  // public void setYear(int year) {
  // this.year = year;
  // }

  /**
   * Gets the start label.
   *
   * @return the start label
   */
  public String getStartLabel() {
    return startLabel;
  }

  /**
   * Sets the start label.
   *
   * @param startLabel the new start label
   */
  public void setStartLabel(String startLabel) {
    this.startLabel = startLabel;
  }

  /**
   * Gets the end label.
   *
   * @return the end label
   */
  public String getEndLabel() {
    return endLabel;
  }

  /**
   * Sets the end label.
   *
   * @param endLabel the new end label
   */
  public void setEndLabel(String endLabel) {
    this.endLabel = endLabel;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "SearchStatisticsResult [live=" + live + ", sparql=" + sparql + ", cache=" + cache + "]";
  }

}
