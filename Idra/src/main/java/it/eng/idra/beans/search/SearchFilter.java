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

package it.eng.idra.beans.search;

import it.eng.idra.utils.JsonRequired;

// TODO: Auto-generated Javadoc
/**
 * This class represents a DCATDataset field on which to perform a search.
 */

public class SearchFilter {

  /** The field. */
  @JsonRequired
  private String field;

  /** The value. */
  @JsonRequired
  private String value;

  /**
   * Instantiates a new search filter.
   *
   * @param field the field
   * @param value the value
   */
  public SearchFilter(String field, String value) {
    super();
    this.field = field;
    this.value = value;
  }

  /**
   * Gets the field.
   *
   * @return the field
   */
  public String getField() {
    return field;
  }

  /**
   * Sets the field.
   *
   * @param field the new field
   */
  public void setField(String field) {
    this.field = field;
  }

  /**
   * Gets the value.
   *
   * @return the value
   */
  public String getValue() {
    return value;
  }

  /**
   * Sets the value.
   *
   * @param value the new value
   */
  public void setValue(String value) {
    this.value = value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "SearchFilter [field=" + field + ", value=" + value + "]";
  }

}
