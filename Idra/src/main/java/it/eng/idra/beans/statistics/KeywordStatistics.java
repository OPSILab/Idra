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
 * The Class KeywordStatistics.
 */
@SqlResultSetMapping(name = "KeywordStatisticsResult", classes = {
    @ConstructorResult(targetClass = KeywordStatisticsResult.class, columns = {
        @ColumnResult(name = "keyword", type = String.class),
        @ColumnResult(name = "counter", type = Integer.class),
        @ColumnResult(name = "percentage", type = Double.class), }) })

@Entity
@Table(name = "keyword_statistics")
public class KeywordStatistics {

  /** The id. */
  private int id;

  /** The keyword. */
  private String keyword;

  /** The counter. */
  private int counter;

  /**
   * Instantiates a new keyword statistics.
   */
  public KeywordStatistics() {

  }

  /**
   * Instantiates a new keyword statistics.
   *
   * @param keyword the keyword
   * @param counter the counter
   */
  public KeywordStatistics(String keyword, int counter) {
    super();
    this.keyword = keyword;
    this.counter = counter;
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
   * Gets the keyword.
   *
   * @return the keyword
   */
  @Column(name = "keyword")
  public String getKeyword() {
    return keyword;
  }

  /**
   * Sets the keyword.
   *
   * @param keyword the new keyword
   */
  public void setKeyword(String keyword) {
    this.keyword = keyword;
  }

  /**
   * Gets the counter.
   *
   * @return the counter
   */
  @Column(name = "counter")
  public int getCounter() {
    return counter;
  }

  /**
   * Sets the counter.
   *
   * @param counter the new counter
   */
  public void setCounter(int counter) {
    this.counter = counter;
  }

  /**
   * Inc counter.
   */
  public void incCounter() {
    this.counter++;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "KeywordStatistics [id=" + id + ", keyword=" + keyword + ", counter=" + counter + "]";
  }

}
