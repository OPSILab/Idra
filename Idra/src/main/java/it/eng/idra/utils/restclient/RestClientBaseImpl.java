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
package it.eng.idra.utils.restclient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;

import it.eng.idra.utils.PropertyManager;

import it.eng.idra.utils.restclient.builders.HttpDeleteBuilder;
import it.eng.idra.utils.restclient.builders.HttpGetBuilder;
import it.eng.idra.utils.restclient.builders.HttpHeadBuilder;
import it.eng.idra.utils.restclient.builders.HttpPostBuilder;
import it.eng.idra.utils.restclient.builders.HttpPutBuilder;
import it.eng.idra.utils.restclient.configuration.RestProperty;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.sun.research.ws.wadl.HTTPMethods;

@SuppressWarnings("deprecation")
public abstract class RestClientBaseImpl {
	
	protected static final Logger logger = Logger.getLogger(RestClient.class.getName());
	protected HttpClient httpclient = null;
	
	protected HttpClient buildClient(){
		
		final HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 300000);
		HttpConnectionParams.setSoTimeout(httpParams, 900000);

		httpclient = new DefaultHttpClient(httpParams);

		/* Set an HTTP proxy if it is specified in system properties.
		 * 
		 * http://docs.oracle.com/javase/6/docs/technotes/guides/net/proxies.html
		 * http://hc.apache.org/httpcomponents-client-ga/httpclient/examples/org/apache/http/examples/client/ClientExecuteProxy.java
		 */
		if (Boolean.parseBoolean(PropertyManager.getProperty(RestProperty.HTTP_PROXY_ENABLED).trim())
				&& StringUtils.isNotBlank(PropertyManager.getProperty(RestProperty.HTTP_PROXY_HOST).trim())) {

			int port = 80;
			if (isSet(PropertyManager.getProperty(RestProperty.HTTP_PROXY_PORT))) {
				port = Integer.parseInt(PropertyManager.getProperty(RestProperty.HTTP_PROXY_PORT));
			}
			HttpHost proxy = new HttpHost(PropertyManager.getProperty(RestProperty.HTTP_PROXY_HOST), port, "http");
			httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
			if (isSet(PropertyManager.getProperty(RestProperty.HTTP_PROXY_USER))) {
				((AbstractHttpClient) httpclient).getCredentialsProvider().setCredentials(
						new AuthScope(PropertyManager.getProperty(RestProperty.HTTP_PROXY_HOST), port),
						(Credentials) new UsernamePasswordCredentials(
								PropertyManager.getProperty(RestProperty.HTTP_PROXY_USER),
								PropertyManager.getProperty(RestProperty.HTTP_PROXY_PASSWORD)));
			}
		}
		
		return httpclient;
	}
	
	protected HttpResponse invoke(HTTPMethods method, String urlString, Map<String, String> headers, MediaType type, String data) 
			throws MalformedURLException{
		
		URL url = new URL(urlString);
		
		HttpResponse response = null;
		httpclient = buildClient();
		
		try {
			HttpRequestBase httpRequest = null;
			
			switch(method){
				case DELETE:
					httpRequest = HttpDeleteBuilder.getInstance(url, headers);
					break;
				case GET:
					httpRequest = HttpGetBuilder.getInstance(url, headers);
					break;
				case HEAD:
					httpRequest = HttpHeadBuilder.getInstance(url, headers);
					break;
				case POST:
					httpRequest = HttpPostBuilder.getInstance(url, headers, type, data);
					break;
				case PUT:
					httpRequest = HttpPutBuilder.getInstance(url, headers, type, data);
					break;
				default:
					throw new Exception("Method "+method.toString()+" not supported");
			}
			
			response = httpclient.execute(httpRequest);
			
		} catch (Exception ioe) {
			logger.info(ioe.toString());
		} 
		
		return response;
		
	}
	
	private static boolean isSet(String string) {
		return string != null && string.length() > 0;
	}
}
