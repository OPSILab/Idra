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

@Entity
@Table(name = "odms_sitemap_dataset_selector")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class DatasetSelector extends WebScraperSelector {

  public DatasetSelector() {

  }

  public DatasetSelector(List<String> parentSelectors,
      WebScraperSelectorType type, Boolean multiple, String name,
      String selector, String regex, List<String> stopValues) {
    super(parentSelectors, type, multiple, name, selector, regex, stopValues);

  }

  @Override
  @LazyCollection(LazyCollectionOption.FALSE)
  @ElementCollection
  @CollectionTable(name = "odms_sitemap_dataset_selector_parentSelector", joinColumns = {
      @JoinColumn(name = "selector_id") })
  public List<String> getParentSelectors() {
    return parentSelectors;
  }

  @Override
  @LazyCollection(LazyCollectionOption.FALSE)
  @ElementCollection
  @CollectionTable(name = "odms_sitemap_dataset_selector_stopValues", joinColumns = {
      @JoinColumn(name = "selector_id") })
  public List<String> getStopValues() {
    return stopValues;
  }

}
