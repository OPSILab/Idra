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

package it.eng.idra.statistics;

// TODO: Auto-generated Javadoc
/**
 * The Class DatasetCountStatistics.
 */
public class DatasetCountStatistics {

  /** The name. */
  private String name;

  /** The dataset count. */
  private int datasetCount;

  /**
   * Instantiates a new dataset count statistics.
   */
  public DatasetCountStatistics() {
    // TODO Auto-generated constructor stub
  }

  /**
   * Instantiates a new dataset count statistics.
   *
   * @param name the name
   * @param cnt  the cnt
   */
  public DatasetCountStatistics(String name, int cnt) {
    // TODO Auto-generated constructor stub
    this.name = name;
    this.datasetCount = cnt;
  }

  /**
   * Gets the name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name.
   *
   * @param name the new name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the dataset count.
   *
   * @return the dataset count
   */
  public int getDatasetCount() {
    return datasetCount;
  }

  /**
   * Sets the dataset count.
   *
   * @param datasetCount the new dataset count
   */
  public void setDatasetCount(int datasetCount) {
    this.datasetCount = datasetCount;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "DatasetCountStatistics [name=" + name + ", datasetCount=" + datasetCount + "]";
  }

}
