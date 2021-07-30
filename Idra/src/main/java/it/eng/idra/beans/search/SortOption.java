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

package it.eng.idra.beans.search;

// TODO: Auto-generated Javadoc
/**
 * The Class SortOption.
 */
public class SortOption {

  /** The field. */
  private String field;

  /** The mode. */
  private SortMode mode;

  /**
   * Instantiates a new sort option.
   *
   * @param field the field
   * @param mode  the mode
   */
  public SortOption(String field, SortMode mode) {
    super();
    this.field = field;
    this.mode = mode;
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
   * Gets the mode.
   *
   * @return the mode
   */
  public SortMode getMode() {
    return mode;
  }

  /**
   * Sets the mode.
   *
   * @param mode the new mode
   */
  public void setMode(SortMode mode) {
    this.mode = mode;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "SortOption [field=" + field + ", mode=" + mode + "]";
  }

}
