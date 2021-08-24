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
 * The Class CkanErrorResponse.
 */
public class CkanErrorResponse extends CkanAbstactResponse {

  /** The error. */
  private CkanError error;

  /**
   * Instantiates a new ckan error response.
   */
  public CkanErrorResponse() {
    // TODO Auto-generated constructor stub
    super();
    this.setSuccess(false);
  }

  /**
   * Instantiates a new ckan error response.
   *
   * @param error the error
   */
  public CkanErrorResponse(CkanError error) {
    super();
    this.setSuccess(false);
    this.error = error;
  }

  /**
   * Instantiates a new ckan error response.
   *
   * @param help the help
   * @param er   the er
   */
  public CkanErrorResponse(String help, CkanError er) {
    // TODO Auto-generated constructor stub
    super(help, false);
    this.error = er;
  }

  /**
   * Instantiates a new ckan error response.
   *
   * @param message the message
   * @param type    the type
   */
  public CkanErrorResponse(String message, String type) {
    super();
    this.error = new CkanError(message, type);
  }

  /**
   * Instantiates a new ckan error response.
   *
   * @param help    the help
   * @param message the message
   * @param type    the type
   */
  public CkanErrorResponse(String help, String message, String type) {
    super(help, false);
    this.error = new CkanError(message, type);
  }

  /**
   * Gets the error.
   *
   * @return the error
   */
  public CkanError getError() {
    return error;
  }

  /**
   * Sets the error.
   *
   * @param error the new error
   */
  public void setError(CkanError error) {
    this.error = error;
  }

}
