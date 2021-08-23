/*******************************************************************************
 * Idra - Open Data Federation Platform
 * Copyright (C) 2021 Engineering Ingegneria Informatica S.p.A.
 * <p> 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * <p> 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

package it.eng.idra.beans.dcat;

import it.eng.idra.cache.CacheContentType;
import java.util.UUID;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.solr.common.SolrInputDocument;

// TODO: Auto-generated Javadoc
/**
 * The Class DcatProperty.
 */
@Embeddable
public class DcatProperty {

  // private String id;

  /** The uri. */
  private transient String uri;

  /** The property. */
  private transient Property property;

  /** The range. */
  private transient Resource range;

  /** The value. */
  private String value;
  // @LazyCollection(LazyCollectionOption.FALSE)

  // @ElementCollection
  // @CollectionTable(name = "dcat_theme", joinColumns = { @JoinColumn(name =
  // "owner_id") })

  // private List<String> values;

  /**
   * Instantiates a new dcat property.
   */
  public DcatProperty() {
  }

  /**
   * Instantiates a new dcat property.
   *
   * @param uri   the uri
   * @param value the value
   */
  public DcatProperty(String uri, String value) {
    // values = null;
    setUri(uri);
    setValue(value);
  }

  // public DCATProperty(String uri, List<String> values) {
  // value = null;
  // setURI(uri);
  //// setValues(values);
  // }

  /**
   * Instantiates a new dcat property.
   *
   * @param property the property
   * @param range    the range
   * @param value    the value
   */
  public DcatProperty(Property property, Resource range, String value) {
    // values = null;
    setProperty(property);
    setUri(property.getURI());
    setRange(range);
    setValue(value);
  }

  /**
   * Instantiates a new dcat property.
   *
   * @param uri   the uri
   * @param range the range
   * @param value the value
   */
  public DcatProperty(String uri, Resource range, String value) {
    // values = null;
    setUri(uri);
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

  /**
   * Gets the uri.
   *
   * @return the uri
   */
  @Transient
  public String getUri() {
    return uri;
  }

  /**
   * Sets the uri.
   *
   * @param uri the new uri
   */
  public void setUri(String uri) {
    this.uri = uri != null ? uri : "";
  }

  /**
   * Gets the range.
   *
   * @return the range
   */
  @Transient
  public Resource getRange() {
    return range;
  }

  /**
   * Sets the range.
   *
   * @param range the new range
   */
  public void setRange(Resource range) {
    this.range = range != null ? range : ResourceFactory.createResource();
  }

  /**
   * Gets the value.
   *
   * @return the value
   */
  public String getValue() {
    return value;
  }

  /**
   * Sets the value.
   *
   * @param value the new value
   */
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

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "DCATProperty [uri=" + uri + ", property=" + property + ", range=" + range + ", value="
        + value + "]";
  }

  /**
   * Gets the property.
   *
   * @return the property
   */
  @Transient
  public Property getProperty() {
    return property;
  }

  /**
   * Sets the property.
   *
   * @param property the new property
   */
  public void setProperty(Property property) {
    this.property = property;
  }

  /**
   * To doc.
   *
   * @param contentType the content type
   * @param fieldName   the field name
   * @return the solr input document
   */
  public SolrInputDocument toDoc(CacheContentType contentType, String fieldName) {
    SolrInputDocument doc = new SolrInputDocument();
    doc.addField("id", UUID.randomUUID().toString());
    doc.addField("content_type", contentType.toString());
    doc.addField(fieldName, this.value);
    return doc;
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
    result = prime * result + ((value == null) ? 0 : value.hashCode());
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
    DcatProperty other = (DcatProperty) obj;
    if (value == null) {
      if (other.value != null) {
        return false;
      }
    } else if (!value.equals(other.value)) {
      return false;
    }
    return true;
  }

}
