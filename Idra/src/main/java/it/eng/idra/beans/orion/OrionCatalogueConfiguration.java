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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "odms_orionconfig")
public class OrionCatalogueConfiguration {

	private String id;
	private boolean isAuthenticated;
	private String authToken;
	private String orionDatasetDumpString;
	private String orionDatasetFilePath;
	
	public OrionCatalogueConfiguration() {}
	
	public OrionCatalogueConfiguration(boolean isAuthenticated, String authToken, String datasets) {
		super();
		this.isAuthenticated = isAuthenticated;
		this.authToken = authToken;
		this.orionDatasetDumpString = datasets;
	}
	
	public OrionCatalogueConfiguration(boolean isAuthenticated, String authToken, String datasets,String dumpPath) {
		this(isAuthenticated, authToken, datasets);
		this.orionDatasetFilePath=dumpPath;
	}
	
	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((authToken == null) ? 0 : authToken.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isAuthenticated ? 1231 : 1237);
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
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isAuthenticated != other.isAuthenticated)
			return false;
		return true;
	}
	
}
