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

package it.eng.idra.statistics;

// TODO: Auto-generated Javadoc
/**
 * The Class PlatformStatistcs.
 */
public class PlatformStatistcs {

  /** The facets statistics. */
  private FacetsStatistics facetsStatistics;

  /** The catalogues statistics. */
  private CataloguesStatistics cataloguesStatistics;

  /**
   * Instantiates a new platform statistcs.
   */
  public PlatformStatistcs() {
    // TODO Auto-generated constructor stub
  }

  /**
   * Gets the facets.
   *
   * @return the facets
   */
  public FacetsStatistics getFacets() {
    return facetsStatistics;
  }

  /**
   * Sets the facets.
   *
   * @param facets the new facets
   */
  public void setFacets(FacetsStatistics facets) {
    this.facetsStatistics = facets;
  }

  /**
   * Gets the catalogues.
   *
   * @return the catalogues
   */
  public CataloguesStatistics getCatalogues() {
    return cataloguesStatistics;
  }

  /**
   * Sets the catalogues.
   *
   * @param catalogues the new catalogues
   */
  public void setCatalogues(CataloguesStatistics catalogues) {
    this.cataloguesStatistics = catalogues;
  }

}
