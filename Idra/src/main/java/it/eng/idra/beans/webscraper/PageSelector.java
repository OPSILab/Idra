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
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

// TODO: Auto-generated Javadoc
/**
 * The Class PageSelector.
 */
@Entity
@Table(name = "odms_sitemap_page_selector")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class PageSelector extends WebScraperSelector {

  /**
   * Instantiates a new page selector.
   */
  public PageSelector() {

  }

  /**
   * Instantiates a new page selector.
   *
   * @param parentSelectors the parent selectors
   * @param type            the type
   * @param multiple        the multiple
   * @param title           the title
   * @param selector        the selector
   * @param regex           the regex
   * @param stopValues      the stop values
   */
  public PageSelector(List<String> parentSelectors, WebScraperSelectorType type, Boolean multiple,
      String title, String selector, String regex, List<String> stopValues) {
    super(parentSelectors, type, multiple, title, selector, regex, stopValues);

  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.beans.webscraper.WebScraperSelector#getParentSelectors()
   */
  @Override
  @LazyCollection(LazyCollectionOption.FALSE)
  @ElementCollection
  @CollectionTable(name = "odms_sitemap_page_selector_parentSelector", joinColumns = {
      @JoinColumn(name = "selector_id") })
  public List<String> getParentSelectors() {
    return parentSelectors;
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.beans.webscraper.WebScraperSelector#getStopValues()
   */
  @Override
  @LazyCollection(LazyCollectionOption.FALSE)
  @ElementCollection
  @CollectionTable(name = "odms_sitemap_page_selector_stopValues", joinColumns = {
      @JoinColumn(name = "selector_id") })
  public List<String> getStopValues() {
    return stopValues;
  }
}
