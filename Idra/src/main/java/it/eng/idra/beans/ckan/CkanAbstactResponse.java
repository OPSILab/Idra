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

package it.eng.idra.beans.ckan;

// TODO: Auto-generated Javadoc
/**
 * The Class CkanAbstactResponse.
 */
public abstract class CkanAbstactResponse {

  /** The help. */
  private String help;

  /** The success. */
  private boolean success;

  /**
   * Instantiates a new ckan abstact response.
   */
  public CkanAbstactResponse() {
    // TODO Auto-generated constructor stub
  }

  /**
   * Instantiates a new ckan abstact response.
   *
   * @param help    the help
   * @param success the success
   */
  public CkanAbstactResponse(String help, boolean success) {
    super();
    this.help = help;
    this.success = success;
  }

  /**
   * Gets the help.
   *
   * @return the help
   */
  public String getHelp() {
    return help;
  }

  /**
   * Sets the help.
   *
   * @param help the new help
   */
  public void setHelp(String help) {
    this.help = help;
  }

  /**
   * Checks if is success.
   *
   * @return true, if is success
   */
  public boolean isSuccess() {
    return success;
  }

  /**
   * Sets the success.
   *
   * @param success the new success
   */
  public void setSuccess(boolean success) {
    this.success = success;
  }

}
