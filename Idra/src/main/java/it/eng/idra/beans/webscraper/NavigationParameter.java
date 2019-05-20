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
package it.eng.idra.beans.webscraper;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Table(name = "odms_sitemap_navigationParameter")
public class NavigationParameter {

	private String id;
	private String name;
	private NavigationType type;
	private String startValue;
	private String endValue;
	private Integer pagesNumber;
	private Integer datasetsPerPage;
	private List<String> enumValues;
	private List<PageSelector> pageSelectors;

	public NavigationParameter() {
	}

	/**
	 * @param name
	 * @param type
	 * @param startValue
	 * @param endValue
	 * @param enumValues
	 * @param pageSelectors
	 */
	public NavigationParameter(String name, NavigationType type, String startValue, String endValue,
			List<String> enumValues, List<PageSelector> navigationSelectors) {
		super();
		this.name = name;
		this.type = type;
		this.startValue = startValue;
		this.endValue = endValue;
		this.enumValues = enumValues;
		this.pageSelectors = navigationSelectors;
	}

	/**
	 * @param name
	 * @param type
	 * @param startValue
	 * @param endValue
	 */
	public NavigationParameter(String name, NavigationType type, String startValue, String endValue) {
		super();
		this.name = name;
		this.type = type;
		this.startValue = startValue;
		this.endValue = endValue;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public NavigationType getType() {
		return type;
	}

	public void setType(NavigationType type) {
		this.type = type;
	}

	public String getStartValue() {
		return startValue;
	}

	public void setStartValue(String startValue) {
		this.startValue = startValue;
	}

	public String getEndValue() {
		return endValue;
	}

	public void setEndValue(String endValue) {
		this.endValue = endValue;
	}

	public Integer getDatasetsPerPage() {
		return datasetsPerPage;
	}

	public void setDatasetsPerPage(Integer datasetsPerPage) {
		this.datasetsPerPage = datasetsPerPage;
	}

	@LazyCollection(LazyCollectionOption.FALSE)
	@ElementCollection
	@CollectionTable(name = "odms_sitemap_navigationParameter_enumValues", joinColumns = {
			@JoinColumn(referencedColumnName = "id", name = "navigationParameter_id") })
	public List<String> getEnumValues() {
		return enumValues;
	}

	public void setEnumValues(List<String> enumValues) {
		this.enumValues = enumValues;
	}

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = { CascadeType.ALL })
	// @Fetch(FetchMode.SELECT)
	@JoinColumns({ @JoinColumn(name = "navigationParameter_id", referencedColumnName = "id") })
	public List<PageSelector> getPageSelectors() {
		return pageSelectors;
	}

	public void setPageSelectors(List<PageSelector> pageSelectors) {
		this.pageSelectors = pageSelectors;
	}

	public Integer getPagesNumber() {
		return pagesNumber;
	}

	public void setPagesNumber(Integer pagesNumber) {
		this.pagesNumber = pagesNumber;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((datasetsPerPage == null) ? 0 : datasetsPerPage.hashCode());
		result = prime * result + ((endValue == null) ? 0 : endValue.hashCode());
		result = prime * result + ((enumValues == null) ? 0 : enumValues.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((pageSelectors == null) ? 0 : pageSelectors.hashCode());
		result = prime * result + ((startValue == null) ? 0 : startValue.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		NavigationParameter other = (NavigationParameter) obj;
		if (datasetsPerPage == null) {
			if (other.datasetsPerPage != null)
				return false;
		} else if (!datasetsPerPage.equals(other.datasetsPerPage))
			return false;
		if (endValue == null) {
			if (other.endValue != null)
				return false;
		} else if (!endValue.equals(other.endValue))
			return false;
		if (enumValues == null) {
			if (other.enumValues != null)
				return false;
		} else if (!enumValues.equals(other.enumValues))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (pageSelectors == null) {
			if (other.pageSelectors != null)
				return false;
		} else if (!pageSelectors.equals(other.pageSelectors))
			return false;
		if (startValue == null) {
			if (other.startValue != null)
				return false;
		} else if (!startValue.equals(other.startValue))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "NavigationParameter [id=" + id + ", name=" + name + ", type=" + type + ", startValue=" + startValue
				+ ", endValue=" + endValue + ", datasetsPerPage=" + datasetsPerPage + ", enumValues=" + enumValues
				+ ", pageSelectors=" + pageSelectors + "]";
	}

}
