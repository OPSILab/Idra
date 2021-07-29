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



@SqlResultSetMapping(name = "SearchStatisticsResult", classes = {
    @ConstructorResult(targetClass = SearchStatisticsResult.class, columns = {
        @ColumnResult(name = "live", type = Integer.class), 
        @ColumnResult(name = "cache", type = Integer.class),
        @ColumnResult(name = "sparql", type = Integer.class) }) })

@Entity
@Table(name = "search_statistics")
public class SearchStatistics {

  private int id;
  private String country;
  private int live;
  private int cache;
  private int sparql;
  private int day;
  private int week;
  private int month;
  private int year;

  public SearchStatistics() {

  }

  /**
   * Instantiates a new search statistics.
   *
   * @param country the country
   * @param live the live
   * @param cache the cache
   * @param sparql the sparql
   * @param day the day
   * @param week the week
   * @param month the month
   * @param year the year
   */
  public SearchStatistics(String country, int live, 
      int cache, int sparql, int day, int week, int month, int year) {
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

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @Column(name = "country")
  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  @Column(name = "live")
  public int getLive() {
    return live;
  }

  public void setLive(int live) {
    this.live = live;
  }

  public void incLive(int live) {
    this.live += live;
  }

  @Column(name = "cache")
  public int getCache() {
    return cache;
  }

  public void setCache(int cache) {
    this.cache = cache;
  }

  public void incCache(int cache) {
    this.cache += cache;
  }

  @Column(name = "sparql")
  public int getSparql() {
    return sparql;
  }

  public void setSparql(int sparql) {
    this.sparql = sparql;
  }

  public void incSparql(int sparql) {
    this.sparql = sparql;
  }

  @Column(name = "day")
  public int getDay() {
    return day;
  }

  public void setDay(int day) {
    this.day = day;
  }

  @Column(name = "week")
  public int getWeek() {
    return week;
  }

  public void setWeek(int week) {
    this.week = week;
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

  @Override
  public String toString() {
    return "SearchStatistics [id=" + id + ", country=" 
       + country + ", values: {cache: " + cache + ", live: " + live
        + ", sparql: " + sparql + "}]";
  }

}
