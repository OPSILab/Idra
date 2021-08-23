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
package it.eng.idra.utils;

import it.eng.idra.authentication.fiware.configuration.IdmProperty;
import it.eng.idra.beans.IdraProperty;
import it.eng.idra.utils.restclient.configuration.RestProperty;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

// TODO: Auto-generated Javadoc
/**
 * The Class PropertyManager.
 */
public class PropertyManager {

  /** The props. */
  private static Properties props = null;

  static {
    props = new Properties();
    try {
      props.load(
          PropertyManager.class.getClassLoader().getResourceAsStream("configuration.properties"));

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Gets the property.
   *
   * @param propName the prop name
   * @return the property
   */
  public static String getProperty(IdraProperty propName) {
    Optional<String> prop = Optional.ofNullable(System.getenv(propName.toString()));
    return prop.orElse(props.getProperty(propName.toString()));
  }

  /**
   * Gets the property.
   *
   * @param propName the prop name
   * @return the property
   */
  public static String getProperty(IdmProperty propName) {
    Optional<String> prop = Optional.ofNullable(System.getenv(propName.toString()));
    return prop.orElse(props.getProperty(propName.toString()));
  }

  /**
   * Gets the property.
   *
   * @param propName the prop name
   * @return the property
   */
  public static String getProperty(RestProperty propName) {
    Optional<String> prop = Optional.ofNullable(System.getenv(propName.toString()));
    return prop.orElse(props.getProperty(propName.toString()));
  }
}
