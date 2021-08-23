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

package it.eng.idra.beans.opendatasoft;

import com.google.gson.annotations.SerializedName;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class DatasetDto.
 */
public class DatasetDto {

  /** The total count. */
  @SerializedName(value = "total_count")
  private Integer totalCount;

  /** The links. */
  private List<Link> links;

  /** The datasets. */
  private List<Dataset> datasets;

  /**
   * Gets the total count.
   *
   * @return the total count
   */
  public Integer getTotalCount() {
    return totalCount;
  }

  /**
   * Sets the total count.
   *
   * @param totalCount the new total count
   */
  public void setTotalCount(Integer totalCount) {
    this.totalCount = totalCount;
  }

  /**
   * Gets the links.
   *
   * @return the links
   */
  public List<Link> getLinks() {
    return links;
  }

  /**
   * Sets the links.
   *
   * @param links the new links
   */
  public void setLinks(List<Link> links) {
    this.links = links;
  }

  /**
   * Gets the datasets.
   *
   * @return the datasets
   */
  public List<Dataset> getDatasets() {
    return datasets;
  }

  /**
   * Sets the datasets.
   *
   * @param datasets the new datasets
   */
  public void setDatasets(List<Dataset> datasets) {
    this.datasets = datasets;
  }

}
