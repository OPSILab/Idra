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

import it.eng.idra.utils.GsonUtil;
import it.eng.idra.utils.GsonUtilException;

// TODO: Auto-generated Javadoc
/**
 * The Class ErrorResponse.
 */
public class ErrorResponse {

  /** The status code. */
  private String statusCode;

  /** The technical message. */
  private String technicalMessage;

  /** The error code. */
  private String errorCode;

  /** The user message. */
  private String userMessage;

  /**
   * Instantiates a new error response.
   *
   * @param statusCode       the status code
   * @param technicalMessage the technical message
   * @param errorCode        the error code
   * @param userMessage      the user message
   */
  public ErrorResponse(String statusCode, String technicalMessage, String errorCode,
      String userMessage) {
    super();
    this.statusCode = statusCode;
    this.technicalMessage = technicalMessage;
    this.errorCode = errorCode;
    this.userMessage = userMessage;
  }

  /**
   * Gets the status code.
   *
   * @return the status code
   */
  public String getStatusCode() {
    return statusCode;
  }

  /**
   * Sets the status code.
   *
   * @param statusCode the new status code
   */
  public void setStatusCode(String statusCode) {
    this.statusCode = statusCode;
  }

  /**
   * Gets the technical message.
   *
   * @return the technical message
   */
  public String getTechnicalMessage() {
    return technicalMessage;
  }

  /**
   * Sets the technical message.
   *
   * @param technicalMessage the new technical message
   */
  public void setTechnicalMessage(String technicalMessage) {
    this.technicalMessage = technicalMessage;
  }

  /**
   * Gets the error code.
   *
   * @return the error code
   */
  public String getErrorCode() {
    return errorCode;
  }

  /**
   * Sets the error code.
   *
   * @param errorCode the new error code
   */
  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  /**
   * Gets the user message.
   *
   * @return the user message
   */
  public String getUserMessage() {
    return userMessage;
  }

  /**
   * Sets the user message.
   *
   * @param userMessage the new user message
   */
  public void setUserMessage(String userMessage) {
    this.userMessage = userMessage;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "\nErrorResponse [statusCode=" + statusCode + ", technicalMessage=" + technicalMessage
        + ", errorCode=" + errorCode + "userMessage=" + userMessage + "]\n";
  }

  /**
   * To json.
   *
   * @return the string
   */
  public String toJson() {
    try {
      return GsonUtil.obj2Json(this, ErrorResponse.class);
    } catch (GsonUtilException e) {
      e.printStackTrace();
      return null;
    }

  }

}
