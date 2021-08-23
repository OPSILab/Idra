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

package it.eng.idra.beans.dcat;

import javax.ws.rs.core.MediaType;

// TODO: Auto-generated Javadoc
/**
 * The Enum DcatApFormat.
 */
public enum DcatApFormat {

  /** The rdfxml. */
  RDFXML("RDF/XML", "application/rdf+xml"),
  /** The turtle. */
  TURTLE("TURTLE", "text/turtle"),

  /** The ntriples. */
  NTRIPLES("NTRIPLES", "application/n-triples"),
  /** The n3. */
  N3("N3", "text/n3"),
  /** The jsonld. */
  JSONLD("JSON-LD"),

  /** The rdfjson. */
  RDFJSON("RDF/JSON", MediaType.APPLICATION_JSON),
  /** The odf. */
  ODF("ODF", MediaType.APPLICATION_JSON);

  /** The format name. */
  private String formatName;

  /** The media type. */
  private String mediaType;

  /**
   * Instantiates a new dcat ap format.
   *
   * @param name the name
   */
  DcatApFormat(String name) {
    this.formatName = name;
  }

  /**
   * Instantiates a new dcat ap format.
   *
   * @param name the name
   * @param type the type
   */
  private DcatApFormat(String name, String type) {
    this.formatName = name;
    this.mediaType = type;
  }

  /**
   * Media type.
   *
   * @return the string
   */
  public String mediaType() {
    return mediaType;
  }

  /**
   * Format name.
   *
   * @return the string
   */
  public String formatName() {
    return formatName;
  }

  /**
   * From string.
   *
   * @param name the name
   * @return the dcat ap format
   */
  public static DcatApFormat fromString(String name) {
    return valueOf(name.toUpperCase());
  }

}
