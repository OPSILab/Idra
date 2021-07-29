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

public class KeywordStatisticsResult {

  private String keyword;
  private int counter;
  private double percentage;

  public KeywordStatisticsResult() {

  }

  /**
   * Instantiates a new keyword statistics result.
   *
   * @param keyword the keyword
   * @param counter the counter
   * @param percentage the percentage
   */
  public KeywordStatisticsResult(String keyword, int counter, double percentage) {
    this.keyword = keyword;
    this.counter = counter;
    this.percentage = percentage;
  }

  public String getKeyword() {
    return keyword;
  }

  public void setKeyword(String keyword) {
    this.keyword = keyword;
  }

  public int getCounter() {
    return counter;
  }

  public void setCounter(int counter) {
    this.counter = counter;
  }

  public double getPercentage() {
    return percentage;
  }

  public void setPercentage(double percentage) {
    this.percentage = percentage;
  }

  @Override
  public String toString() {
    return "KeywordStatisticsResult [keyword=" + keyword 
        + ", counter=" + counter + ", percentage=" + percentage + "]";
  }

}
