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

package it.eng.idra.beans.dcat;

// TODO: Auto-generated Javadoc
/**
 * The Enum DcatApProfile.
 */
public enum DcatApProfile {

  /** The dcatap. */
  DCATAP("DCATAP"),
  /** The dcatap it. */
  DCATAP_IT("DCATAP_IT");

  /** The profile name. */
  private String profileName;

  /**
   * Instantiates a new dcat ap profile.
   *
   * @param name the name
   */
  DcatApProfile(String name) {
    this.profileName = name;
  }

  /**
   * Profile name.
   *
   * @return the string
   */
  public String profileName() {
    return profileName;
  }

  /**
   * From string.
   *
   * @param name the name
   * @return the dcat ap profile
   */
  public static DcatApProfile fromString(String name) {
    return valueOf(name.toUpperCase());
  }

}
