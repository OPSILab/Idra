/*******************************************************************************
 * Idra - Open Data Federation Platform
 * Copyright (C) 2021 Engineering Ingegneria Informatica S.p.A.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
package it.eng.idra.api.ckan;

import java.util.List;
import org.ckan.Dataset;

// TODO: Auto-generated Javadoc
/**
 * The Class CkanSearchResult.
 */
public class CkanSearchResult {

  /** The count. */
  private Long count;

  /** The results. */
  private List<Dataset> results;

  /**
   * Instantiates a new ckan search result.
   */
  public CkanSearchResult() {
  }

  /**
   * Instantiates a new ckan search result.
   *
   * @param count  the count
   * @param result the result
   */
  public CkanSearchResult(Long count, List<Dataset> result) {
    super();
    this.count = count;
    this.results = result;
  }

  /**
   * Gets the count.
   *
   * @return the count
   */
  public Long getCount() {
    return count;
  }

  /**
   * Sets the count.
   *
   * @param count the new count
   */
  public void setCount(Long count) {
    this.count = count;
  }

  /**
   * Gets the results.
   *
   * @return the results
   */
  public List<Dataset> getResults() {
    return results;
  }

  /**
   * Sets the results.
   *
   * @param result the new results
   */
  public void setResults(List<Dataset> result) {
    this.results = result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
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
   *
   * @param o the o
   * @return the string
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");

  }
}
