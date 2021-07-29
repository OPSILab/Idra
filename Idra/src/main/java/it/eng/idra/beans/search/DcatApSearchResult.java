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

import java.util.List;

public class DcatApSearchResult {

  private Long count;
  private List<String> results;
  private List<SearchFacet> facets;

  /**
   * Instantiates a new dcat ap search result.
   *
   * @param count the count
   * @param result the result
   */
  public DcatApSearchResult(Long count, List<String> result) {
    super();
    this.count = count;
    this.results = result;
  }

  /**
   * Instantiates a new dcat ap search result.
   *
   * @param count the count
   * @param result the result
   * @param facets the facets
   */
  public DcatApSearchResult(Long count, List<String> result, List<SearchFacet> facets) {
    super();
    this.count = count;
    this.results = result;
    this.setFacets(facets);
  }

  public Long getCount() {
    return count;
  }

  public void setCount(Long count) {
    this.count = count;
  }

  public List<String> getResults() {
    return results;
  }

  public void setResults(List<String> result) {
    this.results = result;
  }

  public List<SearchFacet> getFacets() {
    return facets;
  }

  public void setFacets(List<SearchFacet> facets) {
    this.facets = facets;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ResourcesResult {\n");

    sb.append("    count: ").append(toIndentedString(count)).append("\n");
    sb.append("    resources: ").append(toIndentedString(results.size())).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}
