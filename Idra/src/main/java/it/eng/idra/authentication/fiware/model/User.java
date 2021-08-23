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
 * The Class User.
 */
@Deprecated
public class User {

  /** The name. */
  private String name;

  /** The domain. */
  private Domain domain;

  /** The password. */
  private String password;

  /**
   * Instantiates a new user.
   *
   * @param name     the name
   * @param domain   the domain
   * @param password the password
   */
  public User(String name, Domain domain, String password) {
    super();
    this.name = name;
    this.domain = domain;
    this.password = password;
  }

  /**
   * Gets the name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name.
   *
   * @param name the new name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the domain.
   *
   * @return the domain
   */
  public Domain getDomain() {
    return domain;
  }

  /**
   * Sets the domain.
   *
   * @param domain the new domain
   */
  public void setDomain(Domain domain) {
    this.domain = domain;
  }

  /**
   * Gets the password.
   *
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Sets the password.
   *
   * @param password the new password
   */
  public void setPassword(String password) {
    this.password = password;
  }

}
