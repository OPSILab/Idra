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
package it.eng.idra.authentication.fiware.connectors;

import it.eng.idra.authentication.fiware.configuration.IdmProperty;
import it.eng.idra.authentication.fiware.model.Token;
import it.eng.idra.authentication.fiware.model.UserInfo;
import it.eng.idra.utils.PropertyManager;

// TODO: Auto-generated Javadoc
/**
 * The Class FiwareIdmConnector.
 */
public abstract class FiwareIdmConnector {

  /** The client id. */
  protected String clientId;

  /** The client secret. */
  protected String clientSecret;

  /** The redirect uri. */
  protected String redirectUri;

  /** The protocol. */
  protected String protocol;

  /** The host. */
  protected String host;

  /** The port. */
  protected int port;

  /** The base url. */
  protected String baseUrl;

  /** The Constant path_token. */
  protected static final String path_token = PropertyManager
      .getProperty(IdmProperty.IDM_PATH_TOKEN);

  /** The Constant path_user. */
  protected static final String path_user = PropertyManager.getProperty(IdmProperty.IDM_PATH_USER);

  /**
   * Instantiates a new fiware idm connector.
   *
   * @param protocol     the protocol
   * @param host         the host
   * @param port         the port
   * @param clientId     the client id
   * @param clientSecret the client secret
   * @param redirectUri  the redirect uri
   */
  public FiwareIdmConnector(String protocol, String host, int port, String clientId,
      String clientSecret, String redirectUri) {
    super();
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.redirectUri = redirectUri;

    this.protocol = protocol;
    this.host = host;
    this.port = port;
    String hostNew = null;

    boolean needsPort = !(("http".equalsIgnoreCase(this.protocol) && port == 80)
        || ("https".equalsIgnoreCase(this.protocol) && port == 443));

    if (this.port != -1) {
      hostNew = this.host.concat(needsPort ? ":".concat(String.valueOf(this.port)) : "");
    } else {
      hostNew = this.host;
    }

    this.baseUrl = this.protocol.concat("://").concat(hostNew);
  }

  /**
   * Gets the token.
   *
   * @param code the code
   * @return the token
   * @throws Exception the exception
   */
  public abstract Token getToken(String code) throws Exception;

  /**
   * Gets the user info.
   *
   * @param token the token
   * @return the user info
   * @throws Exception the exception
   */
  public abstract UserInfo getUserInfo(String token) throws Exception;

}
