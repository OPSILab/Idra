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

// TODO: Auto-generated Javadoc
/**
 * The Enum OrderBy.
 */
public enum OrderBy {

  /** The id. */
  ID("ID"),
  /** The name. */
  NAME("NAME"),
  /** The host. */
  HOST("HOST"),
  /** The nodetype. */
  NODETYPE("NODETYPE"),
  /** The federationlevel. */
  FEDERATIONLEVEL("FEDERATIONLEVEL"),

  /** The datasetcount. */
  DATASETCOUNT("DATASETCOUNT"),
  /** The nodestate. */
  NODESTATE("NODESTATE"),

  /** The registerdate. */
  REGISTERDATE("REGISTERDATE"),
  /** The lastupdatedate. */
  LASTUPDATEDATE("LASTUPDATEDATE"),

  /** The refreshperiod. */
  REFRESHPERIOD("REFRESHPERIOD");

  /** The value. */
  private String value;

  /**
   * Instantiates a new order by.
   *
   * @param value the value
   */
  OrderBy(String value) {
    this.value = value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Enum#toString()
   */
  @Override
  public String toString() {
    return String.valueOf(value);
  }

}
