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

import com.google.gson.annotations.JsonAdapter;

import it.eng.idra.utils.TokenGsonManager;

import java.util.Set;

// TODO: Auto-generated Javadoc

/**
 * The Class Token.
 */
@JsonAdapter(TokenGsonManager.class)
public class Token {

  /** The access token. */
  private String access_token;

  /** The expires in. */
  private Integer expires_in;

  /** The token type. */
  private String token_type;

  /** The state. */
  private String state;

  /** The scope. */
  private Set<String> scope;

  /** The refresh token. */
  private String refresh_token;

  /**
   * Instantiates a new token.
   *
   * @param access_token  the access token
   * @param token_type    the token type
   * @param expires_in    the expires in
   * @param refresh_token the refresh token
   * @param scope         the scope
   * @param state         the state
   */
  public Token(String access_token, String token_type, Integer expires_in, String refresh_token, Set<String> scope,
      String state) {
    this.access_token = access_token;
    this.token_type = token_type;
    this.expires_in = expires_in;
    this.refresh_token = refresh_token;
    this.scope = scope;
    this.state = state;
  }

  /**
   * Instantiates a new token.
   *
   * @param access_token the access token
   */
  public Token(String access_token) {
    this.access_token = access_token;
    this.token_type = null;
    this.expires_in = null;
    this.refresh_token = null;
    this.scope = null;
    this.state = null;
  }

  /**
   * Gets the access token.
   *
   * @return the access token
   */
  public String getAccess_token() {
    return access_token;
  }

  /**
   * Sets the access token.
   *
   * @param access_token the new access token
   */
  public void setAccess_token(String access_token) {
    this.access_token = access_token;
  }

  /**
   * Gets the token type.
   *
   * @return the token type
   */
  public String getToken_type() {
    return token_type;
  }

  /**
   * Sets the token type.
   *
   * @param token_type the new token type
   */
  public void setToken_type(String token_type) {
    this.token_type = token_type;
  }

  /**
   * Gets the expires in.
   *
   * @return the expires in
   */
  public Integer getExpires_in() {
    return expires_in;
  }

  /**
   * Sets the expires in.
   *
   * @param expires_in the new expires in
   */
  public void setExpires_in(Integer expires_in) {
    this.expires_in = expires_in;
  }

  /**
   * Gets the refresh token.
   *
   * @return the refresh token
   */
  public String getRefresh_token() {
    return refresh_token;
  }

  /**
   * Sets the refresh token.
   *
   * @param refresh_token the new refresh token
   */
  public void setRefresh_token(String refresh_token) {
    this.refresh_token = refresh_token;
  }

  /**
   * Gets the scope.
   *
   * @return the scope
   */
  public Set<String> getScope() {
    return scope;
  }

  /**
   * Sets the scope.
   *
   * @param scope the new scope
   */
  public void setScope(Set<String> scope) {
    this.scope = scope;
  }

  /**
   * Gets the state.
   *
   * @return the state
   */
  public String getState() {
    return state;
  }

  /**
   * Sets the state.
   *
   * @param state the new state
   */
  public void setState(String state) {
    this.state = state;
  }
}
