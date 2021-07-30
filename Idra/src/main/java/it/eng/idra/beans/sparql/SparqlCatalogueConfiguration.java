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

package it.eng.idra.beans.sparql;

import it.eng.idra.beans.odms.OdmsCatalogueAdditionalConfiguration;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "odms_sparql_config")
public class SparqlCatalogueConfiguration extends OdmsCatalogueAdditionalConfiguration {

  private String sparqlDatasetDumpString;
  private String sparqlDatasetFilePath;

  public SparqlCatalogueConfiguration() {
    this.setType("SPARQL");
  }

  /**
   * Instantiates a new sparql catalogue configuration.
   *
   * @param datasets the datasets
   */
  public SparqlCatalogueConfiguration(String datasets) {
    super();
    this.sparqlDatasetDumpString = datasets;
    this.setType("SPARQL");
  }

  /**
   * Instantiates a new sparql catalogue configuration.
   *
   * @param datasets the datasets
   * @param dumpPath the dump path
   */
  public SparqlCatalogueConfiguration(String datasets, String dumpPath) {
    this(datasets);
    this.sparqlDatasetFilePath = dumpPath;
    this.setType("SPARQL");
  }

  @Transient
  public String getSparqlDatasetDumpString() {
    return sparqlDatasetDumpString;
  }

  public void setSparqlDatasetDumpString(String sparqlDatasetDumpString) {
    this.sparqlDatasetDumpString = sparqlDatasetDumpString;
  }

  public String getSparqlDatasetFilePath() {
    return sparqlDatasetFilePath;
  }

  public void setSparqlDatasetFilePath(String sparqlDatasetFilePath) {
    this.sparqlDatasetFilePath = sparqlDatasetFilePath;
  }

}
