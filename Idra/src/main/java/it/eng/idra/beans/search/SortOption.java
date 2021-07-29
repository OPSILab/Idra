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

public class SortOption {

  private String field;
  private SortMode mode;

  /**
   * Instantiates a new sort option.
   *
   * @param field the field
   * @param mode the mode
   */
  public SortOption(String field, SortMode mode) {
    super();
    this.field = field;
    this.mode = mode;
  }

  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  public SortMode getMode() {
    return mode;
  }

  public void setMode(SortMode mode) {
    this.mode = mode;
  }

  @Override
  public String toString() {
    return "SortOption [field=" + field + ", mode=" + mode + "]";
  }

}
