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

package it.eng.idra.authentication;

import it.eng.idra.authentication.filters.KeycloakAuthenticationFilter;
import it.eng.idra.authentication.fiware.configuration.IdmProperty;
import it.eng.idra.authentication.fiware.model.Token;
import it.eng.idra.authentication.keycloak.connector.KeycloakConnectorImpl;
import it.eng.idra.authentication.keycloak.model.KeycloakUser;
import it.eng.idra.utils.PropertyManager;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;
import org.apache.commons.collections4.CollectionUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class KeycloakAuthenticationManager.
 */
public class KeycloakAuthenticationManager extends AuthenticationManager {

  /** The instance. */
  private static KeycloakAuthenticationManager instance;

  /** The connector. */
  private static KeycloakConnectorImpl connector;

  /** The Constant host. */
  private static final String host = PropertyManager.getProperty(IdmProperty.IDM_HOST);

  /** The Constant protocol. */
  private static final String protocol = PropertyManager.getProperty(IdmProperty.IDM_PROTOCOL);

  /** The Constant clientId. */
  private static final String clientId = PropertyManager.getProperty(IdmProperty.IDM_CLIENT_ID);

  /** The Constant clientSecret. */
  private static final String clientSecret = PropertyManager
      .getProperty(IdmProperty.IDM_CLIENT_SECRET);

  /** The Constant redirectUri. */
  private static final String redirectUri = PropertyManager
      .getProperty(IdmProperty.IDM_REDIRECT_URI);

  /** The Constant logoutCallback. */
  private static final String logoutCallback = PropertyManager
      .getProperty(IdmProperty.IDM_LOGOUT_CALLBACK);

  /**
   * Instantiates a new keycloak authentication manager.
   */
  private KeycloakAuthenticationManager() {
    connector = new KeycloakConnectorImpl(protocol, host, -1, clientId, clientSecret, redirectUri);
  }

  /**
   * Gets the single instance of KeycloakAuthenticationManager.
   *
   * @return single instance of KeycloakAuthenticationManager
   */
  public static KeycloakAuthenticationManager getInstance() {

    if (instance == null) {
      try {
        instance = new KeycloakAuthenticationManager();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return instance;
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.authentication.AuthenticationManager
   * #login(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public Object login(String username, String password, String code) throws Exception {
    return getToken(null, code);
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.authentication.AuthenticationManager
   * #logout(javax.servlet.http.HttpServletRequest)
   */
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

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.authentication.AuthenticationManager
   * #getToken(java.lang.String, java.lang.String)
   */
  @Override
  public Token getToken(String username, String code) throws Exception {
    return connector.getToken(code);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * it.eng.idra.authentication.AuthenticationManager#validateToken(java.lang.
   * Object)
   */
  @Override
  public Boolean validateToken(Object tokenObj) throws Exception {
    Token token = (Token) tokenObj;

    try {
      validateAdminRole(connector.getUserInfo(token.getAccessToken()));
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
  public void validateAdminRole(KeycloakUser user) throws Exception {

    List<String> roles = new ArrayList<String>();
    
    if (CollectionUtils.isNotEmpty(user.getRealmAccess().getRoles())) {
      roles.addAll(user.getRealmAccess().getRoles().stream()
          .map(x -> x.toUpperCase()).collect(Collectors.toList()));
    }
    
    if (CollectionUtils.isNotEmpty(user.getRoles())) {
      roles.addAll(user.getRoles().stream()
          .map(x -> x.toUpperCase()).collect(Collectors.toList()));
    }
        
    if (roles != null && !roles.isEmpty() && roles
        .contains(PropertyManager.getProperty(IdmProperty.IDM_ADMIN_ROLE_NAME).toUpperCase())) {
      // OK
    } else {
      throw new Exception("The User has no Admin role");
    }

  }

  /**
   * Gets the user info.
   *
   * @param token the token
   * @return the user info
   * @throws Exception the exception
   */
  public KeycloakUser getUserInfo(String token) throws Exception {
    return connector.getUserInfo(token);
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.authentication.AuthenticationManager#getFilterClass()
   */
  @Override
  public Class<KeycloakAuthenticationFilter> getFilterClass() throws ClassNotFoundException {

    return KeycloakAuthenticationFilter.class;

  }

}
