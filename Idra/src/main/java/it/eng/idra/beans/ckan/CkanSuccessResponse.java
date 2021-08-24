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
 * The Class CkanSuccessResponse.
 *
 * @param <T> the generic type
 */
public class CkanSuccessResponse<T> extends CkanAbstactResponse {

  /** The result. */
  private T result;

  /**
   * Instantiates a new ckan success response.
   */
  public CkanSuccessResponse() {
    // TODO Auto-generated constructor stub
    super();
    this.setSuccess(true);
  }

  /**
   * Instantiates a new ckan success response.
   *
   * @param help   the help
   * @param result the result
   */
  public CkanSuccessResponse(String help, T result) {
    super(help, true);
    // TODO Auto-generated constructor stub
    this.result = result;
  }

  /**
   * Gets the result.
   *
   * @return the result
   */
  public T getResult() {
    return result;
  }

  /**
   * Sets the result.
   *
   * @param result the new result
   */
  public void setResult(T result) {
    this.result = result;
  }

}
