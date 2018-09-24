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

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "orion_dataset")
public class OrionDataset {

	private String id;
	private String title; //The title of the dataset and the distribution
	private String description;
	private String query; //The query parameter string to be provided to orion
	private String fiwareService;
	private String fiwareServicePath;
	private String themes; //Comma separated values to be mapped with DCAT Themes
	private String license;
	
	public OrionDataset() {}

	public OrionDataset(String title, String description, String query, String fiwareService, String fiwareServicePath,
			String themes,String license) {
		super();
		this.title = title;
		this.description = description;
		this.query = query;
		this.fiwareService = fiwareService;
		this.fiwareServicePath = fiwareServicePath;
		this.themes = themes;
		this.license = license;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getFiwareService() {
		return fiwareService;
	}

	public void setFiwareService(String fiwareService) {
		this.fiwareService = fiwareService;
	}

	public String getFiwareServicePath() {
		return fiwareServicePath;
	}

	public void setFiwareServicePath(String fiwareServicePath) {
		this.fiwareServicePath = fiwareServicePath;
	}

	public String getThemes() {
		return themes;
	}

	public void setThemes(String themes) {
		this.themes = themes;
	}
	
	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((fiwareService == null) ? 0 : fiwareService.hashCode());
		result = prime * result + ((fiwareServicePath == null) ? 0 : fiwareServicePath.hashCode());
		result = prime * result + ((query == null) ? 0 : query.hashCode());
		result = prime * result + ((themes == null) ? 0 : themes.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
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
		OrionDataset other = (OrionDataset) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (fiwareService == null) {
			if (other.fiwareService != null)
				return false;
		} else if (!fiwareService.equals(other.fiwareService))
			return false;
		if (fiwareServicePath == null) {
			if (other.fiwareServicePath != null)
				return false;
		} else if (!fiwareServicePath.equals(other.fiwareServicePath))
			return false;
		if (query == null) {
			if (other.query != null)
				return false;
		} else if (!query.equals(other.query))
			return false;
		if (themes == null) {
			if (other.themes != null)
				return false;
		} else if (!themes.equals(other.themes))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}
		
}
