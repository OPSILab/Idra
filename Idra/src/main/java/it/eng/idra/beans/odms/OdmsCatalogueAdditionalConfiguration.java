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

package it.eng.idra.beans.odms;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

// TODO: Auto-generated Javadoc
/**
 * The Class OdmsCatalogueAdditionalConfiguration.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "odms_additional_config")
public class OdmsCatalogueAdditionalConfiguration {

  /** The id. */
  private String id;

  /** The type. */
  private String type;

  /**
   * Instantiates a new odms catalogue additional configuration.
   */
  public OdmsCatalogueAdditionalConfiguration() {
    super();
  }

  /**
   * Instantiates a new odms catalogue additional configuration.
   *
   * @param id   the id
   * @param type the type
   */
  public OdmsCatalogueAdditionalConfiguration(String id, String type) {
    super();
    this.id = id;
    this.type = type;
  }

  /**
   * Gets the id.
   *
   * @return the id
   */
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  public String getId() {
    return id;
  }

  /**
   * Sets the id.
   *
   * @param id the new id
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the type.
   *
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * Sets the type.
   *
   * @param type the new type
   */
  public void setType(String type) {
    this.type = type;
  }

}
