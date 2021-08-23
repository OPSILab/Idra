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

import it.eng.idra.beans.odms.OdmsCatalogueType;

// TODO: Auto-generated Javadoc
/**
 * The Class TechnologiesCountStatistics.
 */
public class TechnologiesCountStatistics {

  /** The type. */
  private OdmsCatalogueType type;

  /** The count. */
  private int count;

  /**
   * Instantiates a new technologies count statistics.
   */
  public TechnologiesCountStatistics() {
    // TODO Auto-generated constructor stub
  }

  /**
   * Instantiates a new technologies count statistics.
   *
   * @param type  the type
   * @param count the count
   */
  public TechnologiesCountStatistics(OdmsCatalogueType type, int count) {
    super();
    this.type = type;
    this.count = count;
  }

  /**
   * Gets the type.
   *
   * @return the type
   */
  public OdmsCatalogueType getType() {
    return type;
  }

  /**
   * Sets the type.
   *
   * @param type the new type
   */
  public void setType(OdmsCatalogueType type) {
    this.type = type;
  }

  /**
   * Gets the count.
   *
   * @return the count
   */
  public int getCount() {
    return count;
  }

  /**
   * Sets the count.
   *
   * @param count the new count
   */
  public void setCount(int count) {
    this.count = count;
  }

}
