/*******************************************************************************
 * Idra - Open Data Federation Platform
 *  Copyright (C) 2018 Engineering Ingegneria Informatica S.p.A.
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

import java.util.Arrays;
import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;
import com.google.gson.annotations.SerializedName;

import it.eng.idra.beans.IdraProperty;
import it.eng.idra.utils.PropertyManager;

@MappedSuperclass
// @Table(name = "odms_sitemap_selector")
public abstract class WebScraperSelector {

	private transient String id;
	protected List<String> parentSelectors;
	private WebScraperSelectorType type;
	private String extractAttribute;
	private Boolean multiple;
    private String regex;
	
	@SerializedName("id")
	private String name;
	private String selector;

	protected List<String> stopValues;
	private static List<String> defaultStopValues = Arrays
			.asList(PropertyManager.getProperty(IdraProperty.WEB_CONNECTOR_DEFAULT_STOP_VALUES).split(","));

	public WebScraperSelector() {
	}

	/**
	 * @param parentSelectors
	 * @param type
	 * @param multiple
	 * @param name
	 * @param selector
	 * @param regex
	 * @param stopValues
	 */
	public WebScraperSelector(List<String> parentSelectors, WebScraperSelectorType type, Boolean multiple, String name,
			String selector, String regex, List<String> stopValues) {
		super();
		this.parentSelectors = parentSelectors;
		this.type = type;
		this.multiple = multiple;
		this.name = name;
		this.selector = selector;
		this.regex = regex;
		this.setStopValues(stopValues);
	}

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	// @LazyCollection(LazyCollectionOption.FALSE)
	// @ElementCollection
	// @CollectionTable(name = "odms_sitemap_selector_parentSelector",
	// joinColumns = { @JoinColumn(name = "selector_id") })
	@Transient
	public List<String> getParentSelectors() {
		return parentSelectors;
	}

	public void setParentSelectors(List<String> parentSelectors) {
		this.parentSelectors = parentSelectors;
	}

	@Enumerated(EnumType.STRING)
	public WebScraperSelectorType getType() {
		return type;
	}

	public void setType(WebScraperSelectorType type) {
		this.type = type;
	}

	public Boolean getMultiple() {
		return multiple;
	}

	public void setMultiple(Boolean multiple) {
		this.multiple = multiple;
	}

	public String getName() {
		return name;
	}

	public void setName(String title) {
		this.name = title;
	}

	public String getSelector() {
		return selector;
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}

	@Transient
	public List<String> getStopValues() {
		return stopValues;
	}

	public void setStopValues(List<String> stopValues) {
		this.stopValues = stopValues;
	}

	public static List<String> getDefaultStopValues() {
		return defaultStopValues;
	}

	public static void setDefaultStopValues(List<String> defaultStopValues) {
		WebScraperSelector.defaultStopValues = defaultStopValues;
	}

	public String getExtractAttribute() {
		return extractAttribute;
	}

	public void setExtractAttribute(String extractAttribute) {
		this.extractAttribute = extractAttribute;
	}


	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
	@Override
	public String toString() {
		return "WebScraperSelector [parentSelectors=" + parentSelectors + ", type=" + type + ", extractAttribute="
				+ extractAttribute + ", multiple=" + multiple + ", name=" + name + ", selector=" + selector
				+ ", stopValues=" + stopValues + "]";
	}

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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WebScraperSelector other = (WebScraperSelector) obj;
		if (name == null && other.getName() != null)
			return false;

		return name.equals(other.getName());
	}


	
	
	
}
