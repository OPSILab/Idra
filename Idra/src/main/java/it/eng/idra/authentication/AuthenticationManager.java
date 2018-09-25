package it.eng.idra.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import it.eng.idra.beans.ODFAuthenticationMethod;
import it.eng.idra.beans.ODFProperty;
import it.eng.idra.utils.PropertyManager;

public abstract class AuthenticationManager {

	public abstract Object login(String username, String password, String code) throws Exception;

	public abstract Response logout(HttpServletRequest username) throws Exception;

	public abstract Object getToken(String username, String code) throws Exception;

	public abstract Boolean validateToken(Object token) throws Exception;

	public abstract Class<?> getFilterClass() throws ClassNotFoundException;
 
	public static AuthenticationManager getActiveAuthenticationManager() {

		switch (ODFAuthenticationMethod.valueOf(PropertyManager.getProperty(ODFProperty.AUTHENTICATION_METHOD))) {

		case FIWARE:
			return FiwareIDMAuthenticationManager.getInstance();
		default:
			return BasicAuthenticationManager.getInstance();
		}

	}
}
