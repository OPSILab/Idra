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

import java.util.HashSet;
import java.util.Set;

// TODO: Auto-generated Javadoc
/**
 * The Class UserInfo.
 */
public class UserInfo {

  /** The organizations. */
  private Set<Organization> organizations;
  
  /** The display name. */
  private String displayName;
  
  /** The roles. */
  private Set<Role> roles;
  
  /** The app id. */
  private String app_id;
  
  /** The is gravatar enabled. */
  private boolean isGravatarEnabled;
  
  /** The email. */
  private String email;
  
  /** The id. */
  private String id;
  
  /** The authorization decision. */
  private String authorization_decision;
  
  /** The app azf domain. */
  private String app_azf_domain;
  
  /** The username. */
  private String username;

  /**
   * Instantiates a new user info.
   */
  public UserInfo() {
    organizations = new HashSet<Organization>();
    displayName = new String();
    roles = new HashSet<Role>();
    app_id = new String();
    isGravatarEnabled = false;
    email = new String();
    id = new String();
    authorization_decision = new String();
    app_azf_domain = new String();
    username = new String();
  }

  /**
   * Instantiates a new user info.
   *
   * @param organizations the organizations
   * @param displayName the display name
   * @param role the role
   * @param app_id the app id
   * @param isGravatarEnabled the is gravatar enabled
   * @param email the email
   * @param id the id
   */
  public UserInfo(Set<Organization> organizations, String displayName, Set<Role> role, String app_id,
      boolean isGravatarEnabled, String email, String id) {
    this.organizations = organizations;
    this.displayName = displayName;
    this.roles = role;
    this.app_id = app_id;
    this.isGravatarEnabled = isGravatarEnabled;
    this.email = email;
    this.id = id;
  }

  /**
   * Gets the organizations.
   *
   * @return the organizations
   */
  public Set<Organization> getOrganizations() {
    return organizations;
  }

  /**
   * Sets the organizations.
   *
   * @param organizations the new organizations
   */
  public void setOrganizations(Set<Organization> organizations) {
    this.organizations = organizations;
  }

  /**
   * Gets the display name.
   *
   * @return the display name
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * Sets the display name.
   *
   * @param displayName the new display name
   */
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  /**
   * Gets the app id.
   *
   * @return the app id
   */
  public String getApp_id() {
    return app_id;
  }

  /**
   * Sets the app id.
   *
   * @param app_id the new app id
   */
  public void setApp_id(String app_id) {
    this.app_id = app_id;
  }

  /**
   * Checks if is gravatar enabled.
   *
   * @return true, if is gravatar enabled
   */
  public boolean isGravatarEnabled() {
    return isGravatarEnabled;
  }

  /**
   * Sets the gravatar enabled.
   *
   * @param isGravatarEnabled the new gravatar enabled
   */
  public void setGravatarEnabled(boolean isGravatarEnabled) {
    this.isGravatarEnabled = isGravatarEnabled;
  }

  /**
   * Gets the email.
   *
   * @return the email
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets the email.
   *
   * @param email the new email
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Gets the id.
   *
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the id.
   *
   * @param id the new id
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the roles.
   *
   * @return the roles
   */
  public Set<Role> getRoles() {
    return roles;
  }

  /**
   * Sets the roles.
   *
   * @param role the new roles
   */
  public void setRoles(Set<Role> role) {
    this.roles = role;
  }

  /**
   * Gets the authorization decision.
   *
   * @return the authorization decision
   */
  public String getAuthorization_decision() {
    return authorization_decision;
  }

  /**
   * Sets the authorization decision.
   *
   * @param authorization_decision the new authorization decision
   */
  public void setAuthorization_decision(String authorization_decision) {
    this.authorization_decision = authorization_decision;
  }

  /**
   * Gets the app azf domain.
   *
   * @return the app azf domain
   */
  public String getApp_azf_domain() {
    return app_azf_domain;
  }

  /**
   * Sets the app azf domain.
   *
   * @param app_azf_domain the new app azf domain
   */
  public void setApp_azf_domain(String app_azf_domain) {
    this.app_azf_domain = app_azf_domain;
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

}
