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
package it.eng.idra.beans.search;

import org.apache.solr.client.solrj.response.FacetField.Count;

import it.eng.idra.beans.odms.ODMSCatalogueNotFoundException;
import it.eng.idra.management.FederationCore;
import it.eng.idra.management.ODMSManager;

public class SearchFacet {

	private String facet;
	private String keyword;
	private String search_value;
	
	public SearchFacet(){
		
	}

	public SearchFacet(String facet, String keywordQuery,String search_value) {
		super();
		this.facet = facet;
		this.keyword = keywordQuery;
		this.search_value = search_value;
	}

	public SearchFacet(Count c){
		super();
		this.facet = c.toString();
		this.keyword = c.getName();
		this.search_value = c.getName();
	}
	
	public SearchFacet(Count c,String category){
		super();
		
		if("datasetThemes".equals(category)){
			try {
				this.facet = FederationCore.getDCATThemesFromAbbr(c.getName())+" ("+c.getCount()+")";
				this.keyword = FederationCore.getDCATThemesFromAbbr(c.getName());
			}catch(Exception e) {
				this.facet = c.getName()+" ("+c.getCount()+")";
				this.keyword = c.getName();
			}
			this.search_value = c.getName();
		}else if("catalogues".equals(category)) {
			try {
				this.facet = ODMSManager.getODMSCatalogue(Integer.parseInt(c.getName())).getName()+" ("+c.getCount()+")";
				this.keyword = ODMSManager.getODMSCatalogue(Integer.parseInt(c.getName())).getName();
				this.search_value = this.keyword;
			} catch (NumberFormatException | ODMSCatalogueNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			this.facet = c.toString();
			this.keyword = c.getName();
			this.search_value = c.getName();
		}
	}
	
	public String getFacet() {
		return facet;
	}

	public void setFacet(String facet) {
		this.facet = facet;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keywordQuery) {
		this.keyword = keywordQuery;
	}
	
	public String getSearch_value() {
		return search_value;
	}

	public void setSearch_value(String search_value) {
		this.search_value = search_value;
	}

	@Override
	public String toString() {
		return "SearchFacet [facet=" + facet + ", keyword=" + keyword +", search_value="+search_value+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((facet == null) ? 0 : facet.hashCode());
		result = prime * result + ((keyword == null) ? 0 : keyword.hashCode());
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
		SearchFacet other = (SearchFacet) obj;
		if (facet == null) {
			if (other.facet != null)
				return false;
		} else if (!facet.equals(other.facet))
			return false;
		if (keyword == null) {
			if (other.keyword != null)
				return false;
		} else if (!keyword.equals(other.keyword))
			return false;
		return true;
	}
	
	
	
}
