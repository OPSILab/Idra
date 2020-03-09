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
package it.eng.idra.beans.search;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.response.FacetField;

public class SearchFacetsList {

	private String displayName;
	private String search_parameter;
	private List<SearchFacet> values;
	
	public SearchFacetsList() {
		super();
	};
	
	public SearchFacetsList(String displayName, String search_parameter, List<SearchFacet> values) {
		super();
		this.displayName = displayName;
		this.search_parameter = search_parameter;
		this.values = values;
	}

	public SearchFacetsList(FacetField f) {
		super();
		String category = f.getName();
		switch(category) {
		case "keywords":
			this.search_parameter = "tags";
			this.displayName = "Tags";
			break;
		case "distributionFormats":
			this.search_parameter = category;
			this.displayName = "Formats";
			break;
		case "distributionLicenses":
			this.search_parameter = category;
			this.displayName = "Licenses";
			break;
		case "nodeID":
			this.search_parameter = "catalogues";
			this.displayName = "Catalogues";
			break;
		case "datasetThemes":
			this.search_parameter = category;
			this.displayName = "Categories";
			break;
		default:
			this.search_parameter = category;
			this.displayName = StringUtils.capitalise(category);
			break;
		}
		
		this.values = new ArrayList<SearchFacet>();
		this.values.addAll(f.getValues().stream().map( x-> new SearchFacet(x,this.search_parameter) ).collect(Collectors.toList()));
	}
	
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getSearch_parameter() {
		return search_parameter;
	}

	public void setSearch_parameter(String search_parameter) {
		this.search_parameter = search_parameter;
	}

	public List<SearchFacet> getValues() {
		return values;
	}

	public void setValues(List<SearchFacet> values) {
		this.values = values;
	}
	
	
	
	
}
