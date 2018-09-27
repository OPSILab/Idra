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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "distribution_orion_config")
public class OrionDistributionConfig{

	/**
	 * 
	 */	
	private String id;
	private String nodeID;
	private String query; //The query parameter string to be provided to orion
	private String fiwareService;
	private String fiwareServicePath;
	
	public OrionDistributionConfig() {}

	public OrionDistributionConfig(String query, String fiwareService, String fiwareServicePath,String nodeID) {
		super();
		this.query = query;
		this.fiwareService = fiwareService;
		this.fiwareServicePath = fiwareServicePath;
		this.nodeID=nodeID;
	}

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@Column(name = "orion_id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getNodeID() {
		return nodeID;
	}

	public void setNodeID(String nodeID) {
		this.nodeID = nodeID;
	}
		
}
