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

	@Override
	public String toString() {
		return "\nRemoteCatalogue [id=" + id + ", catalogueName=" + catalogueName + ", URL=" + URL + "]";
	}

}
