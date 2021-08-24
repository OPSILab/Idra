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

package it.eng.idra.beans.spod;

import java.util.List;
import org.ckan.Dataset;

// TODO: Auto-generated Javadoc
/**
 * The Class SpodDataset.
 */
public class SpodDataset extends Dataset {

  /** The relations. */
  private List<SpodRelation> relations;

  /**
   * Instantiates a new spod dataset.
   */
  public SpodDataset() {
    super();
  }

  /**
   * Instantiates a new spod dataset.
   *
   * @param relations the relations
   */
  public SpodDataset(List<SpodRelation> relations) {
    super();
    this.relations = relations;
  }

  /**
   * Gets the relations.
   *
   * @return the relations
   */
  public List<SpodRelation> getRelations() {
    return relations;
  }

  /**
   * Sets the relations.
   *
   * @param relations the new relations
   */
  public void setRelations(List<SpodRelation> relations) {
    this.relations = relations;
  }

}
