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

package it.eng.idra.authentication.fiware.model;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import it.eng.idra.utils.TokenGsonManager;
import java.util.Set;

// TODO: Auto-generated Javadoc

/**
 * The Class Token.
 */
@JsonAdapter(TokenGsonManager.class)
public class Token {

  /** The access token. */
  @SerializedName(value = "access_token")
  private String accessToken;

  /** The expires in. */
  @SerializedName(value = "expires_in")
  private Integer expiresIn;

  /** The token type. */
  @SerializedName(value = "token_type")
  private String tokenType;

  /** The state. */
  private String state;

  /** The scope. */
  private Set<String> scope;

  /** The refresh token. */
  @SerializedName(value = "refresh_token")
  private String refreshToken;

  /**
   * Instantiates a new token.
   *
   * @param accessToken  the access token
   * @param tokenType    the token type
   * @param expiresIn    the expires in
   * @param refreshToken the refresh token
   * @param scope        the scope
   * @param state        the state
   */
  public Token(String accessToken, String tokenType, Integer expiresIn, String refreshToken,
      Set<String> scope, String state) {
    this.accessToken = accessToken;
    this.tokenType = tokenType;
    this.expiresIn = expiresIn;
    this.refreshToken = refreshToken;
    this.scope = scope;
    this.state = state;
  }

  /**
   * Instantiates a new token.
   *
   * @param accessToken the access token
   */
  public Token(String accessToken) {
    this.accessToken = accessToken;
    this.tokenType = null;
    this.expiresIn = null;
    this.refreshToken = null;
    this.scope = null;
    this.state = null;
  }

  /**
   * Gets the access token.
   *
   * @return the access token
   */
  public String getAccessToken() {
    return accessToken;
  }

  /**
   * Sets the access token.
   *
   * @param accessToken the new access token
   */
  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  /**
   * Gets the token type.
   *
   * @return the token type
   */
  public String getTokenType() {
    return tokenType;
  }

  /**
   * Sets the token type.
   *
   * @param tokenType the new token type
   */
  public void setTokenType(String tokenType) {
    this.tokenType = tokenType;
  }

  /**
   * Gets the expires in.
   *
   * @return the expires in
   */
  public Integer getExpiresIn() {
    return expiresIn;
  }

  /**
   * Sets the expires in.
   *
   * @param expiresIn the new expires in
   */
  public void setExpiresIn(Integer expiresIn) {
    this.expiresIn = expiresIn;
  }

  /**
   * Gets the refresh token.
   *
   * @return the refresh token
   */
  public String getRefreshToken() {
    return refreshToken;
  }

  /**
   * Sets the refresh token.
   *
   * @param refreshToken the new refresh token
   */
  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
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
