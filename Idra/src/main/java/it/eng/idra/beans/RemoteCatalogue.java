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

package it.eng.idra.beans;

import com.google.gson.annotations.SerializedName;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

// TODO: Auto-generated Javadoc
/**
 * The Class RemoteCatalogue.
 */
@Entity
@Table(name = "remoteCatalogue")
public class RemoteCatalogue {

  /** The id. */
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  /** The catalogue name. */
  @Column(name = "catalogueName", unique = false)
  private String catalogueName;

  /** The url. */
  @Column(name = "URL", unique = false)
  @SerializedName(value = "URL")
  private String url;

  /** The editable. */
  @Column(name = "editable", unique = false)
  private boolean editable;

  /** The username. */
  @Column(name = "username", unique = false)
  private String username;

  /** The password. */
  @Column(name = "password", unique = false)
  private String password;

  /** The client id. */
  @Column(name = "clientID", unique = false)
  @SerializedName(value = "clientID")
  private String clientId;

  /** The client secret. */
  @Column(name = "clientSecret", unique = false)
  private String clientSecret;

  /** The portal. */
  @Column(name = "portal", unique = false)
  private String portal;

  /** The is idra. */
  @Column(name = "isIdra", unique = false)
  private boolean isIdra;

  /**
   * Instantiates a new remote catalogue.
   */
  public RemoteCatalogue() {

  }

  /**
   * Gets the id.
   *
   * @return the id
   */
  public int getId() {
    return id;
  }

  /**
   * Sets the id.
   *
   * @param id the new id
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Gets the catalogue name.
   *
   * @return the catalogue name
   */
  public String getCatalogueName() {
    return catalogueName;
  }

  /**
   * Sets the catalogue name.
   *
   * @param catName the new catalogue name
   */
  public void setCatalogueName(String catName) {
    this.catalogueName = catName;
  }

  /**
   * Gets the url.
   *
   * @return the url
   */
  public String getUrl() {
    return url;
  }

  /**
   * Sets the url.
   *
   * @param newUrl the new url
   */
  public void setUrl(String newUrl) {
    this.url = newUrl;
  }

  /**
   * Gets the editable.
   *
   * @return the editable
   */
  public boolean getEditable() {
    return editable;
  }

  /**
   * Sets the editable.
   *
   * @param editable the new editable
   */
  public void setEditable(boolean editable) {
    this.editable = editable;
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
   * Gets the password.
   *
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Sets the password.
   *
   * @param password the new password
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Gets the client id.
   *
   * @return the client id
   */
  public String getClientId() {
    return clientId;
  }

  /**
   * Sets the client id.
   *
   * @param clientId the new client id
   */
  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  /**
   * Gets the client secret.
   *
   * @return the client secret
   */
  public String getClientSecret() {
    return clientSecret;
  }

  /**
   * Sets the client secret.
   *
   * @param clientSecret the new client secret
   */
  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }

  /**
   * Gets the portal.
   *
   * @return the portal
   */
  public String getPortal() {
    return portal;
  }

  /**
   * Sets the portal.
   *
   * @param portal the new portal
   */
  public void setPortal(String portal) {
    this.portal = portal;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "\nRemoteCatalogue [id=" + id + ", catalogueName=" + catalogueName + ", URL=" + url
        + ", editable=" + editable + ", isIdra=" + isIdra + ", username=" + username + ", password="
        + password + ", clientID=" + clientId + ", clientSecret=" + clientSecret + ", portal="
        + portal + "]";
  }

}
