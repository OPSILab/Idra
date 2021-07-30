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

package it.eng.idra.beans.orion;

import it.eng.idra.beans.DistributionAdditionalConfiguration;
import javax.persistence.Entity;

@Entity
//@Table(name = "distribution_orion_config")
public class OrionDistributionConfig extends DistributionAdditionalConfiguration {

  private String fiwareService;
  private String fiwareServicePath;
  private String linkHeader;

  public OrionDistributionConfig() {
    this.setType("ORION");
  }

  /**
   * Instantiates a new orion distribution config.
   *
   * @param query the query
   * @param fiwareService the fiware service
   * @param fiwareServicePath the fiware service path
   * @param nodeId the node ID
   */
  public OrionDistributionConfig(String query, String fiwareService,
      String fiwareServicePath, String nodeId) {
    super();
    this.fiwareService = fiwareService;
    this.fiwareServicePath = fiwareServicePath;
    this.setNodeId(nodeId);
    this.setType("ORION");
    this.setQuery(query);
  }

  public String getFiwareService() {
    return fiwareService;
  }

  public void setFiwareService(String fiwareService) {
    this.fiwareService = fiwareService;
  }

  public String getFiwareServicePath() {
    return fiwareServicePath;
  }

  public void setFiwareServicePath(String fiwareServicePath) {
    this.fiwareServicePath = fiwareServicePath;
  }

  public String getLinkHeader() {
    return linkHeader;
  }

  public void setLinkHeader(String linkHeader) {
    this.linkHeader = linkHeader;
  }
}
