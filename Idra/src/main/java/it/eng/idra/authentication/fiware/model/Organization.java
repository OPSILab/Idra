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

import com.google.gson.annotations.SerializedName;
import java.util.HashSet;
import java.util.Set;

// TODO: Auto-generated Javadoc
/**
 * The Class Organization.
 */
public class Organization {

  /** The website. */
  private String website;
  
  /** The description. */
  private String description;
  
  /** The roles. */
  private Set<Role> roles;
  
  /** The enabled. */
  private boolean enabled;
  
  /** The id. */
  private String id;
  
  /** The domain id. */
  @SerializedName(value = "domain_id")
  private String domainId;
  
  /** The name. */
  private String name;

  /**
   * Gets the website.
   *
   * @return the website
   */
  public String getWebsite() {
    return website;
  }

  /**
   * Sets the website.
   *
   * @param website the new website
   */
  public void setWebsite(String website) {
    this.website = website;
  }

  /**
   * Gets the description.
   *
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description.
   *
   * @param description the new description
   */
  public void setDescription(String description) {
    this.description = description;
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
   * @param roles the new roles
   */
  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }

  /**
   * Checks if is enabled.
   *
   * @return true, if is enabled
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Sets the enabled.
   *
   * @param enabled the new enabled
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
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
   * Gets the domain id.
   *
   * @return the domain id
   */
  public String getDomainId() {
    return domainId;
  }

  /**
   * Sets the domain id.
   *
   * @param domainId the new domain id
   */
  public void setDomainId(String domainId) {
    this.domainId = domainId;
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
   * Instantiates a new organization.
   *
   * @param website the website
   * @param description the description
   * @param roles the roles
   * @param enabled the enabled
   * @param id the id
   * @param domainId the domain id
   * @param name the name
   */
  public Organization(String website, String description, 
      Set<Role> roles, boolean enabled, String id, 
      String domainId,
      String name) {
    this.website = website;
    this.description = description;
    this.roles = roles;
    this.enabled = enabled;
    this.id = id;
    this.domainId = domainId;
    this.name = name;
  }

  /**
   * Instantiates a new organization.
   */
  public Organization() {
    this.description = "";
    this.roles = new HashSet<Role>();
    this.enabled = true;
    this.id = "";
    this.domainId = "";
    this.name = "";
  }

}
