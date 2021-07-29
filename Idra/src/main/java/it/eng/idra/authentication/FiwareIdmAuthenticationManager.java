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

package it.eng.idra.authentication;

import it.eng.idra.authentication.filters.FiwareIdmAuthenticationFilter;
import it.eng.idra.authentication.fiware.configuration.IdmProperty;
import it.eng.idra.authentication.fiware.connectors.FiwareIdmConnector;
import it.eng.idra.authentication.fiware.connectors.Keyrock6Connector;
import it.eng.idra.authentication.fiware.connectors.Keyrock7Connector;

import it.eng.idra.authentication.fiware.model.FiwareIdmVersion;
import it.eng.idra.authentication.fiware.model.Role;
import it.eng.idra.authentication.fiware.model.Token;
import it.eng.idra.authentication.fiware.model.UserInfo;
import it.eng.idra.utils.PropertyManager;

import java.net.URI;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;

public class FiwareIdmAuthenticationManager extends AuthenticationManager {

  private static FiwareIdmAuthenticationManager instance;
  private static FiwareIdmConnector connector;

  private static final FiwareIdmVersion idmVersion = FiwareIdmVersion
      .fromString(PropertyManager.getProperty(IdmProperty.IDM_VERSION));
  private static final String host = PropertyManager.getProperty(IdmProperty.IDM_HOST);
  private static final String protocol = PropertyManager.getProperty(IdmProperty.IDM_PROTOCOL);

  private static final String clientId = PropertyManager.getProperty(IdmProperty.IDM_CLIENT_ID);
  private static final String clientSecret = PropertyManager
      .getProperty(IdmProperty.IDM_CLIENT_SECRET);
  private static final String redirectUri = PropertyManager
      .getProperty(IdmProperty.IDM_REDIRECT_URI);
  private static final String logoutCallback = PropertyManager
      .getProperty(IdmProperty.IDM_LOGOUT_CALLBACK);

  private FiwareIdmAuthenticationManager() {
  }

  private FiwareIdmAuthenticationManager(FiwareIdmVersion version) throws Exception {
    switch (version) {

      case FIWARE_IDM_VERSION_6:
        connector = new Keyrock6Connector(protocol, host, -1, clientId, clientSecret, redirectUri);
        break;
      case FIWARE_IDM_VERSION_7:
        connector = new Keyrock7Connector(protocol, host, -1, clientId, clientSecret, redirectUri);
        break;
      default:
        throw new Exception("Fiware IdM Version is invalid");
    }

  }

  /**
   * Gets the single instance of FiwareIdmAuthenticationManager.
   *
   * @return single instance of FiwareIdmAuthenticationManager
   */
  public static FiwareIdmAuthenticationManager getInstance() {

    if (instance == null) {
      try {
        instance = new FiwareIdmAuthenticationManager(idmVersion);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return instance;
  }

  @Override
  public Object login(String username, String password, String code) throws Exception {
    return getToken(null, code);
  }

  @Override
  public Response logout(HttpServletRequest request) throws Exception {

    System.out.println("Logging out...");

    HttpSession session = request.getSession();
    session.removeAttribute("loggedin");
    session.removeAttribute("refresh_token");
    session.removeAttribute("username");
    session.invalidate();

    return Response.temporaryRedirect(URI.create(logoutCallback)).build();

  }

  @Override
  public Token getToken(String username, String code) throws Exception {
    return connector.getToken(code);
  }

  @Override
  public Boolean validateToken(Object tokenObj) throws Exception {
    Token token = (Token) tokenObj;

    try {
      validateAdminRole(connector.getUserInfo(token.getAccess_token()));
      return true;
    } catch (Exception e) {
      return false;
    }

  }

  /**
   * Validate admin role.
   *
   * @param user the user
   * @throws Exception the exception
   */
  public void validateAdminRole(UserInfo user) throws Exception {

    Set<Role> roles = user.getRoles();
    if (roles != null && !roles.isEmpty() && roles
        .contains(new Role(PropertyManager.getProperty(IdmProperty.IDM_ADMIN_ROLE_NAME), null))) {
      // OK
    } else {
      throw new Exception("The User has no Admin role");
    }

  }

  public UserInfo getUserInfo(String token) throws Exception {
    return connector.getUserInfo(token);
  }

  @Override
  public Class<FiwareIdmAuthenticationFilter> getFilterClass() throws ClassNotFoundException {

    return FiwareIdmAuthenticationFilter.class;

  }

}
