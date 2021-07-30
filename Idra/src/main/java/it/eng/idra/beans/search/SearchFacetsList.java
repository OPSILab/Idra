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

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.response.FacetField;

public class SearchFacetsList {

  private String displayName;
  @SerializedName(value = "search_parameter")
  private String searchParameter;
  private List<SearchFacet> values;

  public SearchFacetsList() {
    super();
  }

  /**
   * Instantiates a new search facets list.
   *
   * @param displayName the display name
   * @param searchParameter the search parameter
   * @param values the values
   */
  public SearchFacetsList(String displayName, String searchParameter, List<SearchFacet> values) {
    super();
    this.displayName = displayName;
    this.searchParameter = searchParameter;
    this.values = values;
  }

  /**
   * Instantiates a new search facets list.
   *
   * @param f the f
   */
  public SearchFacetsList(FacetField f) {
    super();
    String category = f.getName();
    switch (category) {
      case "keywords":
        this.searchParameter = "tags";
        this.displayName = "Tags";
        break;
      case "distributionFormats":
        this.searchParameter = category;
        this.displayName = "Formats";
        break;
      case "distributionLicenses":
        this.searchParameter = category;
        this.displayName = "Licenses";
        break;
      case "nodeID":
        this.searchParameter = "catalogues";
        this.displayName = "Catalogues";
        break;
      case "datasetThemes":
        this.searchParameter = category;
        this.displayName = "Categories";
        break;
      default:
        this.searchParameter = category;
        this.displayName = StringUtils.capitalize(category);
        break;
    }

    this.values = new ArrayList<SearchFacet>();
    this.values.addAll(
        f.getValues().stream().map(x -> 
        new SearchFacet(x, this.searchParameter)).collect(Collectors.toList()));
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getSearchParameter() {
    return searchParameter;
  }

  public void setSearchParameter(String searchParameter) {
    this.searchParameter = searchParameter;
  }

  public List<SearchFacet> getValues() {
    return values;
  }

  public void setValues(List<SearchFacet> values) {
    this.values = values;
  }

}
