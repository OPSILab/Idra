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
 * The Class ThemeStatistics.
 */
public class ThemeStatistics {

  /** The theme. */
  private String theme;

  /** The cnt. */
  private int cnt;

  /**
   * Instantiates a new theme statistics.
   */
  public ThemeStatistics() {
    // TODO Auto-generated constructor stub
  }

  /**
   * Instantiates a new theme statistics.
   *
   * @param theme the theme
   * @param cnt   the cnt
   */
  public ThemeStatistics(String theme, int cnt) {
    super();
    this.theme = theme;
    this.cnt = cnt;
  }

  /**
   * Gets the theme.
   *
   * @return the theme
   */
  public String getTheme() {
    return theme;
  }

  /**
   * Sets the theme.
   *
   * @param theme the new theme
   */
  public void setTheme(String theme) {
    this.theme = theme;
  }

  /**
   * Gets the cnt.
   *
   * @return the cnt
   */
  public int getCnt() {
    return cnt;
  }

  /**
   * Sets the cnt.
   *
   * @param cnt the new cnt
   */
  public void setCnt(int cnt) {
    this.cnt = cnt;
  }

}
