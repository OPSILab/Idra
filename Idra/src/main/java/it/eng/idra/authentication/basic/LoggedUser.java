/*******************************************************************************
 * Idra - Open Data Federation Platform
 * Copyright (C) 2021 Engineering Ingegneria Informatica S.p.A.
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
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

package it.eng.idra.authentication.basic;

import it.eng.idra.utils.JsonRequired;
import java.util.Date;

// TODO: Auto-generated Javadoc
/**
 * The Class LoggedUser.
 */
public class LoggedUser {

  /** The username. */
  @JsonRequired
  private String username;

  /** The token. */
  @JsonRequired
  private String token;

  /** The creation date. */
  private Date creationDate;

  /**
   * Instantiates a new logged user.
   *
   * @param us  the us
   * @param tok the tok
   */
  public LoggedUser(String us, String tok) {
    this.username = us;
    this.token = tok;
    this.creationDate = new Date();
  }

  /**
   * Gets the username.
   *
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets the username.
   *
   * @param username the new username
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Gets the token.
   *
   * @return the token
   */
  public String getToken() {
    return token;
  }

  /**
   * Sets the token.
   *
   * @param token the new token
   */
  public void setToken(String token) {
    this.token = token;
  }

  /**
   * Gets the creation date.
   *
   * @return the creation date
   */
  public Date getCreationDate() {
    return creationDate;
  }

  /**
   * Sets the creation date.
   *
   * @param creationDate the new creation date
   */
  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }
}
