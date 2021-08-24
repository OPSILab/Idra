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

package it.eng.idra.beans.webscraper;

// TODO: Auto-generated Javadoc
/**
 * The Enum WebScraperSelectorType.
 */
public enum WebScraperSelectorType {

  /** The Selector element attribute. */
  SelectorElementAttribute("SelectorElementAttribute"),

  /** The Selector text. */
  SelectorText("SelectorText"),
  /** The Selector link. */
  SelectorLink("SelectorLink");

  /** The type name. */
  private String typeName;

  /**
   * Instantiates a new web scraper selector type.
   *
   * @param name the name
   */
  WebScraperSelectorType(String name) {
    this.typeName = name;
  }

  /**
   * Type name.
   *
   * @return the string
   */
  public String typeName() {
    return typeName;
  }
}
