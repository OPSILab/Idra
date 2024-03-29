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

package it.eng.idra.authentication.fiware.model;

// TODO: Auto-generated Javadoc
/**
 * The Enum FiwareIdmVersion.
 */
public enum FiwareIdmVersion {

  /** The fiware idm version 6. */
  FIWARE_IDM_VERSION_6("6"),
  /** The fiware idm version 7. */
  FIWARE_IDM_VERSION_7("7");

  /** The text. */
  private final String text;

  /**
   * Instantiates a new fiware idm version.
   *
   * @param text the text
   */
  private FiwareIdmVersion(final String text) {
    this.text = text;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Enum#toString()
   */
  @Override
  public String toString() {
    return text;
  }

  /**
   * From string.
   *
   * @param text the text
   * @return the fiware idm version
   */
  public static FiwareIdmVersion fromString(String text) {
    for (FiwareIdmVersion b : FiwareIdmVersion.values()) {
      if (b.text.equalsIgnoreCase(text)) {
        return b;
      }
    }
    return null;
  }

}
