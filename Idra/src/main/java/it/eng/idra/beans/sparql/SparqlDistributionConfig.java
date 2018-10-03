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
package it.eng.idra.beans.sparql;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;

import it.eng.idra.beans.DistributionAdditionalConfiguration;

@Entity
public class SparqlDistributionConfig extends DistributionAdditionalConfiguration{

	/**
	 * 
	 */		
	//Comma Separated Values
	private String formats;
	
	public SparqlDistributionConfig() {
		this.setType("SPARQL");
	}

	public SparqlDistributionConfig(String query,String formats,String nodeID) {
		super();
		this.formats=formats;
		this.setNodeID(nodeID);
		this.setType("SPARQL");
		this.setQuery(query);
	}
	
	public String getFormats() {
		return formats;
	}

	public void setFormats(String formats) {
		this.formats = formats;
	}
	
}
