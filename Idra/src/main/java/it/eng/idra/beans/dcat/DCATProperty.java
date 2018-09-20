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
package it.eng.idra.beans.dcat;

import java.util.UUID;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import org.apache.jena.rdf.model.Property;
import org.apache.solr.common.SolrInputDocument;

import it.eng.idra.cache.CacheContentType;

@Embeddable
public class DCATProperty {

	// private String id;

	private transient String uri;
	private transient Property property;
	private transient String range;

	private String value;
	// @LazyCollection(LazyCollectionOption.FALSE)

	// @ElementCollection
	// @CollectionTable(name = "dcat_theme", joinColumns = { @JoinColumn(name =
	// "owner_id") })

	// private List<String> values;

	public DCATProperty() {
	}

	public DCATProperty(String uri, String value) {
		// values = null;
		setURI(uri);
		setValue(value);
	}

	// public DCATProperty(String uri, List<String> values) {
	// value = null;
	// setURI(uri);
	//// setValues(values);
	// }

	public DCATProperty(Property property, String range, String value) {
		// values = null;
		setProperty(property);
		setURI(property.getURI());
		setRange(range);
		setValue(value);
	}

	public DCATProperty(String uri, String range, String value) {
		// values = null;
		setURI(uri);
		setRange(range);
		setValue(value);
	}

	// public DCATProperty(String uri, String range, List<String> values) {
	// value = null;
	// setURI(uri);
	// setRange(range);
	// setValues(values);
	// }

	// public String getId() {
	// return id;
	// }

	// public void setId(String id) {
	// this.id = id;
	// }

	@Transient
	public String getURI() {
		return uri;
	}

	public void setURI(String uri) {
		this.uri = uri != null ? uri : "";
	}

	@Transient
	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range != null ? range : "";
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value != null ? value : "";
	}

	// @Basic

	// public List<String> getValues() {
	//// return Arrays.asList(values.split(";;"));
	// return values;
	//
	// }
	//
	// public void setValues(List<String> values) {
	//// this.values = values.stream().collect(Collectors.joining(";;"));
	// this.values = values;
	// }

	@Override
	public String toString() {
		return "DCATProperty [uri=" + uri + ", property=" + property + ", range=" + range + ", value=" + value + "]";
	}

	
	@Transient
	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}

	public SolrInputDocument toDoc(CacheContentType contentType, String fieldName) {
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", UUID.randomUUID().toString());
		doc.addField("content_type", contentType.toString());
		doc.addField(fieldName, this.value);
		return doc;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		DCATProperty other = (DCATProperty) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	

}
