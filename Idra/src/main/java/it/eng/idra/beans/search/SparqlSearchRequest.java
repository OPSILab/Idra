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
package it.eng.idra.beans.search;

import it.eng.idra.utils.JsonRequired;

// TODO: Auto-generated Javadoc
/**
 * The Class SparqlSearchRequest.
 */
public class SparqlSearchRequest {

  /** The query. */
  @JsonRequired
  private String query;

  /** The format. */
  @JsonRequired
  private SparqlResultFormat format;

  /**
   * Instantiates a new sparql search request.
   *
   * @param query  the query
   * @param format the format
   */
  public SparqlSearchRequest(String query, SparqlResultFormat format) {
    super();
    this.query = query;
    this.format = format;
  }

  /**
   * Gets the query.
   *
   * @return the query
   */
  public String getQuery() {
    return query;
  }

  /**
   * Sets the query.
   *
   * @param query the new query
   */
  public void setQuery(String query) {
    this.query = query;
  }

  /**
   * Gets the format.
   *
   * @return the format
   */
  public SparqlResultFormat getFormat() {
    return format;
  }

  /**
   * Sets the format.
   *
   * @param format the new format
   */
  public void setFormat(SparqlResultFormat format) {
    this.format = format;
  }

}
