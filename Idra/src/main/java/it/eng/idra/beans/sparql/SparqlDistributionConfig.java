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

package it.eng.idra.beans.sparql;

import it.eng.idra.beans.DistributionAdditionalConfiguration;
import javax.persistence.Entity;

// TODO: Auto-generated Javadoc
/**
 * The Class SparqlDistributionConfig.
 */
@Entity
public class SparqlDistributionConfig extends DistributionAdditionalConfiguration {

  /** The formats. */
  private String formats;

  /**
   * Instantiates a new sparql distribution config.
   */
  public SparqlDistributionConfig() {
    this.setType("SPARQL");
  }

  /**
   * Instantiates a new sparql distribution config.
   *
   * @param query   the query
   * @param formats the formats
   * @param nodeId  the node ID
   */
  public SparqlDistributionConfig(String query, String formats, String nodeId) {
    super();
    this.formats = formats;
    this.setNodeId(nodeId);
    this.setType("SPARQL");
    this.setQuery(query);
  }

  /**
   * Gets the formats.
   *
   * @return the formats
   */
  public String getFormats() {
    return formats;
  }

  /**
   * Sets the formats.
   *
   * @param formats the new formats
   */
  public void setFormats(String formats) {
    this.formats = formats;
  }

}
