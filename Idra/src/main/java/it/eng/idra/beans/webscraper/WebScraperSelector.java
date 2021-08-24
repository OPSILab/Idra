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

import com.google.gson.annotations.SerializedName;
import it.eng.idra.beans.IdraProperty;
import it.eng.idra.utils.PropertyManager;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;

// TODO: Auto-generated Javadoc
/**
 * The Class WebScraperSelector.
 */
@MappedSuperclass
// @Table(name = "odms_sitemap_selector")
public abstract class WebScraperSelector {

  /** The id. */
  private transient String id;

  /** The parent selectors. */
  protected List<String> parentSelectors;

  /** The type. */
  private WebScraperSelectorType type;

  /** The extract attribute. */
  private String extractAttribute;

  /** The multiple. */
  private Boolean multiple;

  /** The regex. */
  private String regex;

  /** The name. */
  @SerializedName("id")
  private String name;

  /** The selector. */
  private String selector;

  /** The stop values. */
  protected List<String> stopValues;

  /** The default stop values. */
  private static List<String> defaultStopValues = Arrays.asList(
      PropertyManager.getProperty(IdraProperty.WEB_CONNECTOR_DEFAULT_STOP_VALUES).split(","));

  /**
   * Instantiates a new web scraper selector.
   */
  public WebScraperSelector() {
  }

  /**
   * Instantiates a new web scraper selector.
   *
   * @param parentSelectors the parent selectors
   * @param type            the type
   * @param multiple        the multiple
   * @param name            the name
   * @param selector        the selector
   * @param regex           the regex
   * @param stopValues      the stop values
   */
  public WebScraperSelector(List<String> parentSelectors, WebScraperSelectorType type,
      Boolean multiple, String name, String selector, String regex, List<String> stopValues) {
    super();
    this.parentSelectors = parentSelectors;
    this.type = type;
    this.multiple = multiple;
    this.name = name;
    this.selector = selector;
    this.regex = regex;
    this.setStopValues(stopValues);
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

  // @LazyCollection(LazyCollectionOption.FALSE)
  // @ElementCollection
  // @CollectionTable(name = "odms_sitemap_selector_parentSelector",
  /**
   * Gets the parent selectors.
   *
   * @return the parent selectors
   */
  // joinColumns = { @JoinColumn(name = "selector_id") })
  @Transient
  public List<String> getParentSelectors() {
    return parentSelectors;
  }

  /**
   * Sets the parent selectors.
   *
   * @param parentSelectors the new parent selectors
   */
  public void setParentSelectors(List<String> parentSelectors) {
    this.parentSelectors = parentSelectors;
  }

  /**
   * Gets the type.
   *
   * @return the type
   */
  @Enumerated(EnumType.STRING)
  public WebScraperSelectorType getType() {
    return type;
  }

  /**
   * Sets the type.
   *
   * @param type the new type
   */
  public void setType(WebScraperSelectorType type) {
    this.type = type;
  }

  /**
   * Gets the multiple.
   *
   * @return the multiple
   */
  public Boolean getMultiple() {
    return multiple;
  }

  /**
   * Sets the multiple.
   *
   * @param multiple the new multiple
   */
  public void setMultiple(Boolean multiple) {
    this.multiple = multiple;
  }

  /**
   * Gets the name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name.
   *
   * @param title the new name
   */
  public void setName(String title) {
    this.name = title;
  }

  /**
   * Gets the selector.
   *
   * @return the selector
   */
  public String getSelector() {
    return selector;
  }

  /**
   * Sets the selector.
   *
   * @param selector the new selector
   */
  public void setSelector(String selector) {
    this.selector = selector;
  }

  /**
   * Gets the stop values.
   *
   * @return the stop values
   */
  @Transient
  public List<String> getStopValues() {
    return stopValues;
  }

  /**
   * Sets the stop values.
   *
   * @param stopValues the new stop values
   */
  public void setStopValues(List<String> stopValues) {
    this.stopValues = stopValues;
  }

  /**
   * Gets the default stop values.
   *
   * @return the default stop values
   */
  public static List<String> getDefaultStopValues() {
    return defaultStopValues;
  }

  /**
   * Sets the default stop values.
   *
   * @param defaultStopValues the new default stop values
   */
  public static void setDefaultStopValues(List<String> defaultStopValues) {
    WebScraperSelector.defaultStopValues = defaultStopValues;
  }

  /**
   * Gets the extract attribute.
   *
   * @return the extract attribute
   */
  public String getExtractAttribute() {
    return extractAttribute;
  }

  /**
   * Sets the extract attribute.
   *
   * @param extractAttribute the new extract attribute
   */
  public void setExtractAttribute(String extractAttribute) {
    this.extractAttribute = extractAttribute;
  }

  /**
   * Gets the regex.
   *
   * @return the regex
   */
  public String getRegex() {
    return regex;
  }

  /**
   * Sets the regex.
   *
   * @param regex the new regex
   */
  public void setRegex(String regex) {
    this.regex = regex;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "WebScraperSelector [parentSelectors=" + parentSelectors + ", type=" + type
        + ", extractAttribute=" + extractAttribute + ", multiple=" + multiple + ", name=" + name
        + ", selector=" + selector + ", stopValues=" + stopValues + "]";
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
    result = prime * result + ((multiple == null) ? 0 : multiple.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((parentSelectors == null) ? 0 : parentSelectors.hashCode());
    result = prime * result + ((selector == null) ? 0 : selector.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
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
    WebScraperSelector other = (WebScraperSelector) obj;
    if (name == null && other.getName() != null) {
      return false;
    }

    return name.equals(other.getName());
  }

}
