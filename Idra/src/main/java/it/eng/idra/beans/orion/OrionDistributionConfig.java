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

package it.eng.idra.beans.orion;

import it.eng.idra.beans.DistributionAdditionalConfiguration;
import javax.persistence.Entity;

// TODO: Auto-generated Javadoc
/**
 * The Class OrionDistributionConfig.
 */
@Entity
//@Table(name = "distribution_orion_config")
public class OrionDistributionConfig extends DistributionAdditionalConfiguration {

  /** The fiware service. */
  private String fiwareService;

  /** The fiware service path. */
  private String fiwareServicePath;

  /** The link header. */
  private String linkHeader;

  /**
   * Instantiates a new orion distribution config.
   */
  public OrionDistributionConfig() {
    this.setType("ORION");
  }

  /**
   * Instantiates a new orion distribution config.
   *
   * @param query             the query
   * @param fiwareService     the fiware service
   * @param fiwareServicePath the fiware service path
   * @param nodeId            the node ID
   */
  public OrionDistributionConfig(String query, String fiwareService, String fiwareServicePath,
      String nodeId) {
    super();
    this.fiwareService = fiwareService;
    this.fiwareServicePath = fiwareServicePath;
    this.setNodeId(nodeId);
    this.setType("ORION");
    this.setQuery(query);
  }

  /**
   * Gets the fiware service.
   *
   * @return the fiware service
   */
  public String getFiwareService() {
    return fiwareService;
  }

  /**
   * Sets the fiware service.
   *
   * @param fiwareService the new fiware service
   */
  public void setFiwareService(String fiwareService) {
    this.fiwareService = fiwareService;
  }

  /**
   * Gets the fiware service path.
   *
   * @return the fiware service path
   */
  public String getFiwareServicePath() {
    return fiwareServicePath;
  }

  /**
   * Sets the fiware service path.
   *
   * @param fiwareServicePath the new fiware service path
   */
  public void setFiwareServicePath(String fiwareServicePath) {
    this.fiwareServicePath = fiwareServicePath;
  }

  /**
   * Gets the link header.
   *
   * @return the link header
   */
  public String getLinkHeader() {
    return linkHeader;
  }

  /**
   * Sets the link header.
   *
   * @param linkHeader the new link header
   */
  public void setLinkHeader(String linkHeader) {
    this.linkHeader = linkHeader;
  }
}
