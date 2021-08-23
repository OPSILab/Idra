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

package it.eng.idra.authentication;

import it.eng.idra.beans.IdraAuthenticationMethod;
import it.eng.idra.beans.IdraProperty;
import it.eng.idra.utils.PropertyManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticationManager.
 */
public abstract class AuthenticationManager {

  /**
   * Login.
   *
   * @param username the username
   * @param password the password
   * @param code     the code
   * @return the object
   * @throws Exception the exception
   */
  public abstract Object login(String username, String password, String code) throws Exception;

  /**
   * Logout.
   *
   * @param username the username
   * @return the response
   * @throws Exception the exception
   */
  public abstract Response logout(HttpServletRequest username) throws Exception;

  /**
   * Gets the token.
   *
   * @param username the username
   * @param code     the code
   * @return the token
   * @throws Exception the exception
   */
  public abstract Object getToken(String username, String code) throws Exception;

  /**
   * Validate token.
   *
   * @param token the token
   * @return the boolean
   * @throws Exception the exception
   */
  public abstract Boolean validateToken(Object token) throws Exception;

  /**
   * Gets the filter class.
   *
   * @return the filter class
   * @throws ClassNotFoundException the class not found exception
   */
  public abstract Class<?> getFilterClass() throws ClassNotFoundException;

  /**
   * Gets the active authentication manager.
   *
   * @return the active authentication manager
   */
  public static AuthenticationManager getActiveAuthenticationManager() {

    switch (IdraAuthenticationMethod
        .valueOf(PropertyManager.getProperty(IdraProperty.AUTHENTICATION_METHOD))) {

      case FIWARE:
        return FiwareIdmAuthenticationManager.getInstance();
      case KEYCLOAK:
        return KeycloakAuthenticationManager.getInstance();
      default:
        return BasicAuthenticationManager.getInstance();
    }

  }
}
