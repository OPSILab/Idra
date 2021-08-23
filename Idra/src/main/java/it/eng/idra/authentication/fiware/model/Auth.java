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

package it.eng.idra.authentication.fiware.model;

// TODO: Auto-generated Javadoc
/**
 * The Class Auth.
 */
@Deprecated
public class Auth {

  /** The identity. */
  private Identity identity;

  /**
   * Instantiates a new auth.
   *
   * @param identity the identity
   */
  public Auth(Identity identity) {
    super();
    this.identity = identity;
  }

  /**
   * Gets the identity.
   *
   * @return the identity
   */
  public Identity getIdentity() {
    return identity;
  }

  /**
   * Sets the identity.
   *
   * @param identity the new identity
   */
  public void setIdentity(Identity identity) {
    this.identity = identity;
  }

}
