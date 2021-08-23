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

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class Dataset.
 */
public class Dataset {

  /** The links. */
  private List<Link> links;

  /** The dataset. */
  private InnerDataset dataset;

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
   * Gets the dataset.
   *
   * @return the dataset
   */
  public InnerDataset getDataset() {
    return dataset;
  }

  /**
   * Sets the dataset.
   *
   * @param dataset the new dataset
   */
  public void setDataset(InnerDataset dataset) {
    this.dataset = dataset;
  }

}
