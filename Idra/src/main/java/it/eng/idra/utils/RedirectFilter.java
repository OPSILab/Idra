package it.eng.idra.utils;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.Response;

public class RedirectFilter implements ClientResponseFilter{

	@Override
	public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
		// TODO Auto-generated method stub
		if (responseContext.getStatusInfo().getFamily() != Response.Status.Family.REDIRECTION)
			return;

		Response resp = requestContext.getClient().target(responseContext.getLocation()).request().method(requestContext.getMethod());

		responseContext.setEntityStream((InputStream) resp.getEntity());
		responseContext.setStatusInfo(resp.getStatusInfo());
		responseContext.setStatus(resp.getStatus());

	}
}