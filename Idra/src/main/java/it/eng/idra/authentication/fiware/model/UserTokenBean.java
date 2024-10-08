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
 * The Class UserTokenBean.
 */
@Deprecated
public class UserTokenBean {

  /** The auth. */
  private Auth auth;

  /**
   * Instantiates a new user token bean.
   *
   * @param auth the auth
   */
  public UserTokenBean(Auth auth) {
    super();
    this.auth = auth;
  }

  /**
   * Gets the auth.
   *
   * @return the auth
   */
  public Auth getAuth() {
    return auth;
  }

  /**
   * Sets the auth.
   *
   * @param auth the new auth
   */
  public void setAuth(Auth auth) {
    this.auth = auth;
  }

}
