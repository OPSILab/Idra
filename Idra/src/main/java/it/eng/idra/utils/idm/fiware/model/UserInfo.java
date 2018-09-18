/*******************************************************************************
 * Idra - Open Data Federation Platform
 *  Copyright (C) 2018 Engineering Ingegneria Informatica S.p.A.
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
package it.eng.idra.utils.idm.fiware.model;

import java.util.HashSet;
import java.util.Set;

public class UserInfo {

	private Set<Organization> organizations;
	private String displayName;
	private Set<Role> roles;
	private String app_id;
	private boolean isGravatarEnabled;
	private String email;
	private String id;
	private String authorization_decision;
	private String app_azf_domain;
	private String username;

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

	public UserInfo(Set<Organization> organizations,String displayName, Set<Role> role, String app_id, boolean isGravatarEnabled, String email, String id ) {
		this.organizations = organizations;
		this.displayName = displayName;
		this.roles = role;
		this.app_id = app_id;
		this.isGravatarEnabled = isGravatarEnabled;
		this.email = email;
		this.id = id;
	}
	
	public Set<Organization> getOrganizations() {
		return organizations;
	}

	public void setOrganizations(Set<Organization> organizations) {
		this.organizations = organizations;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getApp_id() {
		return app_id;
	}

	public void setApp_id(String app_id) {
		this.app_id = app_id;
	}

	public boolean isGravatarEnabled() {
		return isGravatarEnabled;
	}

	public void setGravatarEnabled(boolean isGravatarEnabled) {
		this.isGravatarEnabled = isGravatarEnabled;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> role) {
		this.roles = role;
	}

	public String getAuthorization_decision() {
		return authorization_decision;
	}

	public void setAuthorization_decision(String authorization_decision) {
		this.authorization_decision = authorization_decision;
	}

	public String getApp_azf_domain() {
		return app_azf_domain;
	}

	public void setApp_azf_domain(String app_azf_domain) {
		this.app_azf_domain = app_azf_domain;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
