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

package it.eng.idra.utils.restclient.configuration;

// TODO: Auto-generated Javadoc
/**
 * The Enum RestProperty.
 */
public enum RestProperty {

  /** The http proxy enabled. */
  HTTP_PROXY_ENABLED("http.proxyEnabled"),

  /** The http proxy host. */
  HTTP_PROXY_HOST("http.proxyHost"),
  /** The http proxy user. */
  HTTP_PROXY_USER("http.proxyUser"),

  /** The http proxy port. */
  HTTP_PROXY_PORT("http.proxyPort"),
  /** The http proxy password. */
  HTTP_PROXY_PASSWORD("http.proxyPassword"),

  /** The http proxy nonproxyhosts. */
  HTTP_PROXY_NONPROXYHOSTS("http.nonProxyHosts");

  /** The text. */
  private final String text;

  /**
   * Instantiates a new rest property.
   *
   * @param text the text
   */
  private RestProperty(final String text) {
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
}