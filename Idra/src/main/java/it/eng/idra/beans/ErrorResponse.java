/*******************************************************************************
 * Idra - Open Data Federation Platform
 *  Copyright (C) 2018 Engineering Ingegneria Informatica S.p.A.
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
package it.eng.idra.beans;

import it.eng.idra.utils.GsonUtil;
import it.eng.idra.utils.GsonUtilException;

public class ErrorResponse {

	private String statusCode;
	private String technicalMessage;
	private String errorCode;
	private String userMessage;

	public ErrorResponse(String statusCode, String technicalMessage, String errorCode, String userMessage) {
		super();
		this.statusCode = statusCode;
		this.technicalMessage = technicalMessage;
		this.errorCode = errorCode;
		this.userMessage = userMessage;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getTechnicalMessage() {
		return technicalMessage;
	}

	public void setTechnicalMessage(String technicalMessage) {
		this.technicalMessage = technicalMessage;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getUserMessage() {
		return userMessage;
	}

	public void setUserMessage(String userMessage) {
		this.userMessage = userMessage;
	}

	@Override
	public String toString() {
		return "\nErrorResponse [statusCode=" + statusCode + ", technicalMessage=" + technicalMessage + ", errorCode="
				+ errorCode + "userMessage=" + userMessage + "]\n";
	}

	public String toJson() {
		try {
			return GsonUtil.obj2Json(this, ErrorResponse.class);
		} catch (GsonUtilException e) {
			e.printStackTrace();
			return null;
		}

	}

}
