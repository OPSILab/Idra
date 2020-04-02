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
package it.eng.idra.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import it.eng.idra.beans.IdraAuthenticationMethod;
import it.eng.idra.beans.IdraProperty;
import it.eng.idra.utils.PropertyManager;

public abstract class AuthenticationManager {

	public abstract Object login(String username, String password, String code) throws Exception;

	public abstract Response logout(HttpServletRequest username) throws Exception;

	public abstract Object getToken(String username, String code) throws Exception;

	public abstract Boolean validateToken(Object token) throws Exception;

	public abstract Class<?> getFilterClass() throws ClassNotFoundException;
 
	public static AuthenticationManager getActiveAuthenticationManager() {

		switch (IdraAuthenticationMethod.valueOf(PropertyManager.getProperty(IdraProperty.AUTHENTICATION_METHOD))) {

		case FIWARE:
			return FiwareIDMAuthenticationManager.getInstance();
		default:
			return BasicAuthenticationManager.getInstance();
		}

	}
}
