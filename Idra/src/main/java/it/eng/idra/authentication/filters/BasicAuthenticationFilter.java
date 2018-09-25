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
package it.eng.idra.authentication.filters;

import java.io.IOException;
import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import it.eng.idra.authentication.BasicAuthenticationManager;
import it.eng.idra.authentication.Secured;

@Secured
@Provider
@Priority(2)
public class BasicAuthenticationFilter implements ContainerRequestFilter {

	@Context
	private ResourceInfo resourceInfo;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		// Method jerseyMethod = resourceInfo.getResourceMethod();
		// if(!jerseyMethod.isAnnotationPresent(Secured.class)){
		// return;
		// }

		// Get the HTTP Authorization header from the request
		String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

		// Check if the HTTP Authorization header is present and formatted correctly
		if (authorizationHeader == null || authorizationHeader.equals("undefined")
				|| !authorizationHeader.startsWith("Bearer ")) {
			throw new NotAuthorizedException("Authorization header must be provided");
		}

		// Extract the token from the HTTP Authorization header
		String token = authorizationHeader.substring("Bearer".length()).trim();
		// logger.info(token);

		try {

			// Validate the token
			if (!BasicAuthenticationManager.getInstance().validateToken(token))
				throw new Exception("Token not valid");

		} catch (Exception e) {
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
		}
	}

	// private void validateToken(String token) throws Exception {
	// // Check if it was issued by the server and if it's not expired
	// // Throw an Exception if the token is invalid
	//
	//// int defaultValidationPeriod = Integer.parseInt(
	// FederationCore.getSettings().get("token_validation") );
	// int defaultValidationPeriod = 3600000;
	//
	// ArrayList<LoggedUser> list = FederationCore.getLogUser();
	//
	// if(list.size()==0){
	// throw new Exception();
	// }
	//
	// for(int i=0; i<list.size(); i++){
	// LoggedUser tmp = list.get(i);
	// if(tmp.getToken().equals(token)){
	// Date n = new Date();
	// if((n.getTime() - tmp.getCreationDate().getTime()) >
	// defaultValidationPeriod){ //da prendere il default
	// list.remove(i);
	// throw new Exception();
	// }else{
	// tmp.setCreationDate(n);
	// break;
	// }
	// }else if(i==list.size()-1 && !tmp.getToken().equals(token)){
	// throw new Exception();
	// }
	// }
	// }
}
