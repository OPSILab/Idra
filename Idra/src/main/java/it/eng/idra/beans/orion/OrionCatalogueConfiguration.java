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
package it.eng.idra.beans.orion;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import it.eng.idra.beans.odms.ODMSCatalogueAdditionalConfiguration;

@Entity
@Table(name = "odms_orionconfig")
public class OrionCatalogueConfiguration extends ODMSCatalogueAdditionalConfiguration {
	
	private boolean isAuthenticated;
	private String authToken;
	private String refreshToken;
	private String oauth2Endpoint;
	private String clientID;
	private String clientSecret;
	private String orionDatasetDumpString;
	private String orionDatasetFilePath;
	
	public OrionCatalogueConfiguration() {
		this.setType("ORION");
	}
	
	public OrionCatalogueConfiguration(boolean isAuthenticated, String authToken,String refreshToken,String oauth2Endpoint,String client_id,String client_secret, String datasets) {
		super();
		this.isAuthenticated = isAuthenticated;
		this.authToken = authToken;
		this.refreshToken=refreshToken;
		this.oauth2Endpoint=oauth2Endpoint;
		this.clientID=client_id;
		this.clientSecret=client_secret;
		this.orionDatasetDumpString = datasets;
		this.setType("ORION");
	}
	
	public OrionCatalogueConfiguration(boolean isAuthenticated, String authToken,String refreshToken,String oauth2Endpoint,String client_id,String client_secret, String datasets,String dumpPath) {
		this(isAuthenticated, authToken, refreshToken,oauth2Endpoint,client_id,client_secret,datasets);
		this.orionDatasetFilePath=dumpPath;
	}
	
	
//	public String getId() {
//		return id;
//	}
//	public void setId(String id) {
//		this.id = id;
//	}
	
	public boolean isAuthenticated() {
		return isAuthenticated;
	}
	public void setAuthenticated(boolean isAuthenticated) {
		this.isAuthenticated = isAuthenticated;
	}
	public String getAuthToken() {
		return authToken;
	}
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	@Transient
	public String getOrionDatasetDumpString() {
		return orionDatasetDumpString;
	}

	public void setOrionDatasetDumpString(String orionDatasetDump) {
		this.orionDatasetDumpString = orionDatasetDump;
	}

	public String getOrionDatasetFilePath() {
		return orionDatasetFilePath;
	}

	public void setOrionDatasetFilePath(String orionDatasetFilePath) {
		this.orionDatasetFilePath = orionDatasetFilePath;
	}
	
	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getOauth2Endpoint() {
		return oauth2Endpoint;
	}

	public void setOauth2Endpoint(String oauth2Endpoint) {
		this.oauth2Endpoint = oauth2Endpoint;
	}

	public String getClientID() {
		return clientID;
	}

	public void setClientID(String client_id) {
		this.clientID = client_id;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String client_secret) {
		this.clientSecret = client_secret;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((authToken == null) ? 0 : authToken.hashCode());
		result = prime * result + ((clientID == null) ? 0 : clientID.hashCode());
		result = prime * result + ((clientSecret == null) ? 0 : clientSecret.hashCode());
		result = prime * result + (isAuthenticated ? 1231 : 1237);
		result = prime * result + ((oauth2Endpoint == null) ? 0 : oauth2Endpoint.hashCode());
		result = prime * result + ((orionDatasetDumpString == null) ? 0 : orionDatasetDumpString.hashCode());
		result = prime * result + ((orionDatasetFilePath == null) ? 0 : orionDatasetFilePath.hashCode());
		result = prime * result + ((refreshToken == null) ? 0 : refreshToken.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrionCatalogueConfiguration other = (OrionCatalogueConfiguration) obj;
		if (authToken == null) {
			if (other.authToken != null)
				return false;
		} else if (!authToken.equals(other.authToken))
			return false;
		if (clientID == null) {
			if (other.clientID != null)
				return false;
		} else if (!clientID.equals(other.clientID))
			return false;
		if (clientSecret == null) {
			if (other.clientSecret != null)
				return false;
		} else if (!clientSecret.equals(other.clientSecret))
			return false;
		if (isAuthenticated != other.isAuthenticated)
			return false;
		if (oauth2Endpoint == null) {
			if (other.oauth2Endpoint != null)
				return false;
		} else if (!oauth2Endpoint.equals(other.oauth2Endpoint))
			return false;
		if (orionDatasetDumpString == null) {
			if (other.orionDatasetDumpString != null)
				return false;
		} else if (!orionDatasetDumpString.equals(other.orionDatasetDumpString))
			return false;
		if (orionDatasetFilePath == null) {
			if (other.orionDatasetFilePath != null)
				return false;
		} else if (!orionDatasetFilePath.equals(other.orionDatasetFilePath))
			return false;
		if (refreshToken == null) {
			if (other.refreshToken != null)
				return false;
		} else if (!refreshToken.equals(other.refreshToken))
			return false;
		return true;
	}
		
}
