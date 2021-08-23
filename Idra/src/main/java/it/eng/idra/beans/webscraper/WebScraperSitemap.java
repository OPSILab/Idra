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

package it.eng.idra.beans.webscraper;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

// TODO: Auto-generated Javadoc
/**
 * The Class WebScraperSitemap.
 */
@Entity
@Table(name = "odms_sitemap")
public class WebScraperSitemap {

  /** The id. */
  private String id;

  /** The start url. */
  private String startUrl;

  /** The dataset selectors. */
  private List<DatasetSelector> datasetSelectors;

  /** The navigation parameter. */
  private NavigationParameter navigationParameter;

  /**
   * Instantiates a new web scraper sitemap.
   */
  public WebScraperSitemap() {
  }

  /**
   * Instantiates a new web scraper sitemap.
   *
   * @param startUrl            the start url
   * @param selectors           the selectors
   * @param navigationParameter the navigation parameter
   */
  public WebScraperSitemap(String startUrl, List<DatasetSelector> selectors,
      NavigationParameter navigationParameter) {
    super();
    this.startUrl = startUrl;
    this.datasetSelectors = selectors;
    this.navigationParameter = navigationParameter;
  }

  /**
   * Gets the id.
   *
   * @return the id
   */
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  public String getId() {
    return id;
  }

  /**
   * Sets the id.
   *
   * @param id the new id
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the start url.
   *
   * @return the start url
   */
  public String getStartUrl() {
    return startUrl;
  }

  /**
   * Sets the start url.
   *
   * @param startUrl the new start url
   */
  public void setStartUrl(String startUrl) {
    this.startUrl = startUrl;
  }

  /**
   * Gets the dataset selectors.
   *
   * @return the dataset selectors
   */
  @LazyCollection(LazyCollectionOption.FALSE)
  @OneToMany(cascade = { CascadeType.ALL })
  // @Fetch(FetchMode.SELECT)
  @JoinColumns({ @JoinColumn(name = "sitemap_id", referencedColumnName = "id") })
  public List<DatasetSelector> getDatasetSelectors() {
    return datasetSelectors;
  }

  /**
   * Sets the dataset selectors.
   *
   * @param datasetSelector the new dataset selectors
   */
  public void setDatasetSelectors(List<DatasetSelector> datasetSelector) {
    this.datasetSelectors = datasetSelector;
  }

  /**
   * Gets the navigation parameter.
   *
   * @return the navigation parameter
   */
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "navigationParameter_id")
  public NavigationParameter getNavigationParameter() {
    return navigationParameter;
  }

  /**
   * Sets the navigation parameter.
   *
   * @param navigationParameter the new navigation parameter
   */
  public void setNavigationParameter(NavigationParameter navigationParameter) {
    this.navigationParameter = navigationParameter;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "WebScraperSitemap [id=" + id + ", startUrl=" + startUrl + ", datasetSelectors="
        + datasetSelectors + ", navigationParameter=" + navigationParameter + "]";
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
    result = prime * result + ((datasetSelectors == null) ? 0 : datasetSelectors.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((navigationParameter == null) ? 0 : navigationParameter.hashCode());
    result = prime * result + ((startUrl == null) ? 0 : startUrl.hashCode());
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
    WebScraperSitemap other = (WebScraperSitemap) obj;
    if (datasetSelectors == null) {
      if (other.datasetSelectors != null) {
        return false;
      }
    } else if (!datasetSelectors.equals(other.datasetSelectors)) {
      return false;
    }
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    if (navigationParameter == null) {
      if (other.navigationParameter != null) {
        return false;
      }
    } else if (!navigationParameter.equals(other.navigationParameter)) {
      return false;
    }
    if (startUrl == null) {
      if (other.startUrl != null) {
        return false;
      }
    } else if (!startUrl.equals(other.startUrl)) {
      return false;
    }
    return true;
  }

}
