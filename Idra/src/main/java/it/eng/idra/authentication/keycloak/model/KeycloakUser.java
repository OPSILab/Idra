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

package it.eng.idra.authentication.keycloak.model;

import com.google.gson.annotations.SerializedName;
import java.util.Set;

// TODO: Auto-generated Javadoc
/**
 * The Class KeycloakUser.
 */
public class KeycloakUser {

  /** The sub. */
  private String sub;

  /** The email verified. */
  @SerializedName(value = "email_verified")
  private boolean emailVerified;

  /** The roles. */
  private Set<String> roles;
  
  /** Realm Roles. */
  @SerializedName(value = "realm_access")
  private RealmAccess realmAccess;

  /** The name. */
  private String name;

  /** The preferred username. */
  @SerializedName(value = "preferred_username")
  private String preferredUsername;

  /** The given name. */
  @SerializedName(value = "given_name")
  private String givenName;

  /** The family name. */
  @SerializedName(value = "family_name")
  private String familyName;

  /** The email. */
  private String email;

  /**
   * Instantiates a new keycloak user.
   */
  public KeycloakUser() {
    // TODO Auto-generated constructor stub
  }

  /**
   * Instantiates a new keycloak user.
   *
   * @param sub               the sub
   * @param emailVerified     the email verified
   * @param roles             the roles
   * @param name              the name
   * @param preferredUsername the preferred username
   * @param givenName         the given name
   * @param familyName        the family name
   * @param email             the email
   */
  public KeycloakUser(String sub, boolean emailVerified, Set<String> roles, String name,
      String preferredUsername, String givenName, String familyName, String email) {
    super();
    this.sub = sub;
    this.emailVerified = emailVerified;
    this.roles = roles;
    this.name = name;
    this.preferredUsername = preferredUsername;
    this.givenName = givenName;
    this.familyName = familyName;
    this.email = email;
  }

  /**
   * Gets the sub.
   *
   * @return the sub
   */
  public String getSub() {
    return sub;
  }

  /**
   * Sets the sub.
   *
   * @param sub the new sub
   */
  public void setSub(String sub) {
    this.sub = sub;
  }

  /**
   * Checks if is email verified.
   *
   * @return true, if is email verified
   */
  public boolean isEmailVerified() {
    return emailVerified;
  }

  /**
   * Sets the email verified.
   *
   * @param emailVerified the new email verified
   */
  public void setEmailVerified(boolean emailVerified) {
    this.emailVerified = emailVerified;
  }

  /**
   * Gets the roles.
   *
   * @return the roles
   */
  public Set<String> getRoles() {
    return roles;
  }

  /**
   * Sets the roles.
   *
   * @param roles the new roles
   */
  public void setRoles(Set<String> roles) {
    this.roles = roles;
  }

  /**
   * Gets the name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name.
   *
   * @param name the new name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the preferred username.
   *
   * @return the preferred username
   */
  public String getPreferredUsername() {
    return preferredUsername;
  }

  /**
   * Sets the preferred username.
   *
   * @param preferredUsername the new preferred username
   */
  public void setPreferredUsername(String preferredUsername) {
    this.preferredUsername = preferredUsername;
  }

  /**
   * Gets the given name.
   *
   * @return the given name
   */
  public String getGivenName() {
    return givenName;
  }

  /**
   * Sets the given name.
   *
   * @param givenName the new given name
   */
  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }

  /**
   * Gets the family name.
   *
   * @return the family name
   */
  public String getFamilyName() {
    return familyName;
  }

  /**
   * Sets the family name.
   *
   * @param familyName the new family name
   */
  public void setFamilyName(String familyName) {
    this.familyName = familyName;
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
   * Gets the realmAccess.
   *
   * @return the realmAccess
   */
  public RealmAccess getRealmAccess() {
    return realmAccess;
  }

  /**
   * Sets the realmAccess.
   *
   * @param realmAccess the new RealmAccess
   */
  public void setRealmAccess(RealmAccess realmAccess) {
    this.realmAccess = realmAccess;
  }


  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "KeycloakUser [sub=" + sub + ", " + "email_verified=" + emailVerified + ", roles="
        + roles + ", name=" + name + ", preferred_username=" + preferredUsername + ", "
        + "given_name=" + givenName + ", family_name=" + familyName + ", email=" + email + "]";
  }

  /**
   * RealmAccess class.
   *
   */
  public class RealmAccess {
    private Set<String> roles;

    public Set<String> getRoles() {
      return roles;
    }

    public void setRoles(Set<String> roles) {
      this.roles = roles;
    }
  }

  
}
