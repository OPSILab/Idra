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
import it.eng.idra.beans.odms.OdmsCatalogueNotFoundException;
import it.eng.idra.management.FederationCore;
import it.eng.idra.management.OdmsManager;
import org.apache.solr.client.solrj.response.FacetField.Count;

// TODO: Auto-generated Javadoc
/**
 * The Class SearchFacet.
 */
public class SearchFacet {

  /** The facet. */
  private String facet;

  /** The keyword. */
  private String keyword;

  /** The search value. */
  @SerializedName(value = "search_value")
  private String searchValue;

  /**
   * Instantiates a new search facet.
   */
  public SearchFacet() {

  }

  /**
   * Instantiates a new search facet.
   *
   * @param facet        the facet
   * @param keywordQuery the keyword query
   * @param searchValue  the search value
   */
  public SearchFacet(String facet, String keywordQuery, String searchValue) {
    super();
    this.facet = facet;
    this.keyword = keywordQuery;
    this.searchValue = searchValue;
  }

  /**
   * Instantiates a new search facet.
   *
   * @param c the c
   */
  public SearchFacet(Count c) {
    super();
    this.facet = c.toString();
    this.keyword = c.getName();
    this.searchValue = c.getName();
  }

  /**
   * Instantiates a new search facet.
   *
   * @param c        the c
   * @param category the category
   */
  public SearchFacet(Count c, String category) {
    super();

    if ("datasetThemes".equals(category)) {
      try {
        this.facet = FederationCore.getDcatThemesFromAbbr(c.getName()) + " (" + c.getCount() + ")";
        this.keyword = FederationCore.getDcatThemesFromAbbr(c.getName());
      } catch (Exception e) {
        this.facet = c.getName() + " (" + c.getCount() + ")";
        this.keyword = c.getName();
      }
      this.searchValue = c.getName();
    } else if ("catalogues".equals(category)) {
      try {
        this.facet = OdmsManager.getOdmsCatalogue(Integer.parseInt(c.getName())).getName() + " ("
            + c.getCount() + ")";
        this.keyword = OdmsManager.getOdmsCatalogue(Integer.parseInt(c.getName())).getName();
        this.searchValue = this.keyword;
      } catch (NumberFormatException | OdmsCatalogueNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } else {
      this.facet = c.toString();
      this.keyword = c.getName();
      this.searchValue = c.getName();
    }
  }

  /**
   * Gets the facet.
   *
   * @return the facet
   */
  public String getFacet() {
    return facet;
  }

  /**
   * Sets the facet.
   *
   * @param facet the new facet
   */
  public void setFacet(String facet) {
    this.facet = facet;
  }

  /**
   * Gets the keyword.
   *
   * @return the keyword
   */
  public String getKeyword() {
    return keyword;
  }

  /**
   * Sets the keyword.
   *
   * @param keywordQuery the new keyword
   */
  public void setKeyword(String keywordQuery) {
    this.keyword = keywordQuery;
  }

  /**
   * Gets the search value.
   *
   * @return the search value
   */
  public String getSearchValue() {
    return searchValue;
  }

  /**
   * Sets the search value.
   *
   * @param searchValue the new search value
   */
  public void setSearchValue(String searchValue) {
    this.searchValue = searchValue;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "SearchFacet [facet=" + facet + ", keyword=" + keyword + ", search_value=" + searchValue
        + "]";
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((facet == null) ? 0 : facet.hashCode());
    result = prime * result + ((keyword == null) ? 0 : keyword.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    SearchFacet other = (SearchFacet) obj;
    if (facet == null) {
      if (other.facet != null) {
        return false;
      }
    } else if (!facet.equals(other.facet)) {
      return false;
    }
    if (keyword == null) {
      if (other.keyword != null) {
        return false;
      }
    } else if (!keyword.equals(other.keyword)) {
      return false;
    }
    return true;
  }

}
