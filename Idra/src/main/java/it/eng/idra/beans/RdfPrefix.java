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

package it.eng.idra.beans;

import it.eng.idra.utils.JsonRequired;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

// TODO: Auto-generated Javadoc
/**
 * The Class RdfPrefix.
 */
@Entity
@Table(name = "prefix")
public class RdfPrefix {

  /** The id. */
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  /** The prefix. */
  @JsonRequired
  @Column(name = "prefix")
  private String prefix;

  /** The namespace. */
  @JsonRequired
  @Column(name = "namespace")
  private String namespace;

  /**
   * Instantiates a new rdf prefix.
   */
  public RdfPrefix() {
  }

  /**
   * Instantiates a new rdf prefix.
   *
   * @param id        the id
   * @param prefix    the prefix
   * @param namespace the namespace
   */
  public RdfPrefix(int id, String prefix, String namespace) {
    super();
    this.id = id;
    this.prefix = prefix;
    this.namespace = namespace;
  }

  /**
   * Instantiates a new rdf prefix.
   *
   * @param prefix    the prefix
   * @param namespace the namespace
   */
  public RdfPrefix(String prefix, String namespace) {
    super();
    this.prefix = prefix;
    this.namespace = namespace;
  }

  /**
   * Gets the id.
   *
   * @return the id
   */
  public int getId() {
    return id;
  }

  /**
   * Sets the id.
   *
   * @param id the new id
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Gets the prefix.
   *
   * @return the prefix
   */
  public String getPrefix() {
    return prefix;
  }

  /**
   * Sets the prefix.
   *
   * @param prefix the new prefix
   */
  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  /**
   * Gets the namespace.
   *
   * @return the namespace
   */
  public String getNamespace() {
    return namespace;
  }

  /**
   * Sets the namespace.
   *
   * @param namespace the new namespace
   */
  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

}
