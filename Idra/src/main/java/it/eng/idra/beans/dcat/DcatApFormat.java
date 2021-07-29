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

package it.eng.idra.beans.dcat;

import javax.ws.rs.core.MediaType;

public enum DcatApFormat {

  RDFXML("RDF/XML", "application/rdf+xml"), TURTLE("TURTLE", "text/turtle"),
  NTRIPLES("NTRIPLES", "application/n-triples"), N3("N3", "text/n3"), JSONLD("JSON-LD"),
  RDFJSON("RDF/JSON", MediaType.APPLICATION_JSON), ODF("ODF", MediaType.APPLICATION_JSON);

  private String formatName;
  private String mediaType;

  DcatApFormat(String name) {
    this.formatName = name;
  }

  private DcatApFormat(String name, String type) {
    this.formatName = name;
    this.mediaType = type;
  }

  public String mediaType() {
    return mediaType;
  }

  public String formatName() {
    return formatName;
  }

  public static DcatApFormat fromString(String name) {
    return valueOf(name.toUpperCase());
  }

}
