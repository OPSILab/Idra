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
package it.eng.idra.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "remoteCatalogue")
public class RemoteCatalogue {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "catalogueName", unique = false)
	private String catalogueName;

	@Column(name = "URL", unique = false)
	private String URL;
	
	
	@Column(name = "editable", unique = false)
	private boolean editable;
	
	@Column(name = "username", unique = false)
	private String username;
	
	@Column(name = "password", unique = false)
	private String password;
	
	@Column(name = "clientID", unique = false)
	private String clientID;
	
	@Column(name = "clientSecret", unique = false)
	private String clientSecret;
	
	@Column(name = "portal", unique = false)
	private String portal;
	
	@Column(name = "isIdra", unique = false)
	private boolean isIdra;
    
	public RemoteCatalogue() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCatalogueName() {
		return catalogueName;
	}

	public void setCatalogueName(String catName) {
		this.catalogueName = catName;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String newURL) {
		this.URL = newURL;
	}
	
	public boolean getEditable() {
		return editable;
	}
	
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getClientID() {
		return clientID;
	}
	
	public void setClientID(String clientID) {
		this.clientID = clientID;
	}
	
	public String getClientSecret() {
		return clientSecret;
	}
	
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}
	
	public String getPortal() {
		return portal;
	}
	
	public void setPortal(String portal) {
		this.portal = portal;
	}
	
	@Override
	public String toString() {
		return "\nRemoteCatalogue [id=" + id + ", catalogueName=" + catalogueName + ", URL=" + URL + ", editable=" + editable  
				+ ", isIdra=" + isIdra + ", username=" + username +  ", password=" + password + ", clientID=" + clientID + ", clientSecret=" + clientSecret 
				+ ", portal=" + portal +"]";
	}

}
