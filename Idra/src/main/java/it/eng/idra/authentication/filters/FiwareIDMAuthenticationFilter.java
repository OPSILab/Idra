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
package it.eng.idra.authentication.filters;

import it.eng.idra.authentication.FiwareIDMAuthenticationManager;
import it.eng.idra.authentication.Secured;
import it.eng.idra.authentication.fiware.configuration.IDMProperty;
import it.eng.idra.authentication.fiware.connectors.FiwareIDMConnector;
import it.eng.idra.authentication.fiware.model.FiwareIDMVersion;
import it.eng.idra.authentication.fiware.model.Token;
import it.eng.idra.utils.PropertyManager;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Set;

import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Secured
@Provider
@Priority(1)
public class FiwareIDMAuthenticationFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		if (authorizationHeader == null || authorizationHeader.equals("undefined")
				|| !authorizationHeader.startsWith("Bearer ")) {
			throw new NotAuthorizedException("Authorization header must be provided");
		}
		String token = authorizationHeader.substring("Bearer".length()).trim();
		try {

			if (!FiwareIDMAuthenticationManager.getInstance().validateToken((Object) new Token(token)))
				throw new Exception("Token not valid");

		} catch (Exception e) {
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
		}
	}

	// private void validateToken(String token) throws Exception {
	//
	// UserInfo user = idm.getUserInfo(token);
	// Set<Role> roles = user.getRoles();
	// if (roles != null && !roles.isEmpty()
	// && roles.contains(new
	// Role(PropertyManager.getProperty(IDMProperty.IDM_ADMIN_ROLE_NAME), null))) {
	// // OK
	// } else {
	// throw new Exception("The User has no Admin role");
	// }
	//
	// }
}