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

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

// TODO: Auto-generated Javadoc
/**
 * The Class SearchStatistics.
 */
@SqlResultSetMapping(name = "SearchStatisticsResult", classes = {
    @ConstructorResult(targetClass = SearchStatisticsResult.class, columns = {
        @ColumnResult(name = "live", type = Integer.class),
        @ColumnResult(name = "cache", type = Integer.class),
        @ColumnResult(name = "sparql", type = Integer.class) }) })

@Entity
@Table(name = "search_statistics")
public class SearchStatistics {

  /** The id. */
  private int id;

  /** The country. */
  private String country;

  /** The live. */
  private int live;

  /** The cache. */
  private int cache;

  /** The sparql. */
  private int sparql;

  /** The day. */
  private int day;

  /** The week. */
  private int week;

  /** The month. */
  private int month;

  /** The year. */
  private int year;

  /**
   * Instantiates a new search statistics.
   */
  public SearchStatistics() {

  }

  /**
   * Instantiates a new search statistics.
   *
   * @param country the country
   * @param live    the live
   * @param cache   the cache
   * @param sparql  the sparql
   * @param day     the day
   * @param week    the week
   * @param month   the month
   * @param year    the year
   */
  public SearchStatistics(String country, int live, int cache, int sparql, int day, int week,
      int month, int year) {
    super();
    this.country = country;
    this.live = live;
    this.cache = cache;
    this.sparql = sparql;
    this.day = day;
    this.week = week;
    this.month = month;
    this.year = year;
  }

  /**
   * Gets the id.
   *
   * @return the id
   */
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public int getId() {
    return id;
  }

  /**
   * Sets the id.
   *
   * @param id the new id
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Gets the country.
   *
   * @return the country
   */
  @Column(name = "country")
  public String getCountry() {
    return country;
  }

  /**
   * Sets the country.
   *
   * @param country the new country
   */
  public void setCountry(String country) {
    this.country = country;
  }

  /**
   * Gets the live.
   *
   * @return the live
   */
  @Column(name = "live")
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
   * Inc live.
   *
   * @param live the live
   */
  public void incLive(int live) {
    this.live += live;
  }

  /**
   * Gets the cache.
   *
   * @return the cache
   */
  @Column(name = "cache")
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

  /**
   * Inc cache.
   *
   * @param cache the cache
   */
  public void incCache(int cache) {
    this.cache += cache;
  }

  /**
   * Gets the sparql.
   *
   * @return the sparql
   */
  @Column(name = "sparql")
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
   * Inc sparql.
   *
   * @param sparql the sparql
   */
  public void incSparql(int sparql) {
    this.sparql = sparql;
  }

  /**
   * Gets the day.
   *
   * @return the day
   */
  @Column(name = "day")
  public int getDay() {
    return day;
  }

  /**
   * Sets the day.
   *
   * @param day the new day
   */
  public void setDay(int day) {
    this.day = day;
  }

  /**
   * Gets the week.
   *
   * @return the week
   */
  @Column(name = "week")
  public int getWeek() {
    return week;
  }

  /**
   * Sets the week.
   *
   * @param week the new week
   */
  public void setWeek(int week) {
    this.week = week;
  }

  /**
   * Gets the month.
   *
   * @return the month
   */
  @Column(name = "month")
  public int getMonth() {
    return month;
  }

  /**
   * Sets the month.
   *
   * @param month the new month
   */
  public void setMonth(int month) {
    this.month = month;
  }

  /**
   * Gets the year.
   *
   * @return the year
   */
  @Column(name = "year")
  public int getYear() {
    return year;
  }

  /**
   * Sets the year.
   *
   * @param year the new year
   */
  public void setYear(int year) {
    this.year = year;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "SearchStatistics [id=" + id + ", country=" + country + ", values: {cache: " + cache
        + ", live: " + live + ", sparql: " + sparql + "}]";
  }

}
