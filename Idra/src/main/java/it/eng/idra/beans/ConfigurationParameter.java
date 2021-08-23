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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

// TODO: Auto-generated Javadoc
/**
 * The Class ConfigurationParameter.
 */
@Entity
@Table(name = "configuration")
public class ConfigurationParameter {

  /** The id. */
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  /** The parameter name. */
  @Column(name = "parameterName", unique = false)
  private String parameterName;

  /** The parameter value. */
  @Column(name = "parameterValue", unique = false)
  private String parameterValue;

  /**
   * Instantiates a new configuration parameter.
   */
  public ConfigurationParameter() {

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
   * Gets the parameter name.
   *
   * @return the parameter name
   */
  public String getParameterName() {
    return parameterName;
  }

  /**
   * Sets the parameter name.
   *
   * @param parameterName the new parameter name
   */
  public void setParameterName(String parameterName) {
    this.parameterName = parameterName;
  }

  /**
   * Gets the parameter value.
   *
   * @return the parameter value
   */
  public String getParameterValue() {
    return parameterValue;
  }

  /**
   * Sets the parameter value.
   *
   * @param parameterValue the new parameter value
   */
  public void setParameterValue(String parameterValue) {
    this.parameterValue = parameterValue;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "\nConfigurationParameter [id=" + id + ", parameterName=" + parameterName
        + ", parameterValue=" + parameterValue + "]";
  }

}
