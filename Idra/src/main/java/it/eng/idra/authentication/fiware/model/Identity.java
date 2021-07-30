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

package it.eng.idra.authentication.fiware.model;

import java.util.Set;

// TODO: Auto-generated Javadoc
/**
 * The Class Identity.
 */
@Deprecated
public class Identity {

  /** The methods. */
  private Set<String> methods;

  /** The password. */
  private Password password;

  /**
   * Instantiates a new identity.
   *
   * @param methods  the methods
   * @param password the password
   */
  public Identity(Set<String> methods, Password password) {
    super();
    this.methods = methods;
    this.password = password;
  }

  /**
   * Gets the methods.
   *
   * @return the methods
   */
  public Set<String> getMethods() {
    return methods;
  }

  /**
   * Sets the methods.
   *
   * @param methods the new methods
   */
  public void setMethods(Set<String> methods) {
    this.methods = methods;
  }

  /**
   * Gets the password.
   *
   * @return the password
   */
  public Password getPassword() {
    return password;
  }

  /**
   * Sets the password.
   *
   * @param password the new password
   */
  public void setPassword(Password password) {
    this.password = password;
  }

}
