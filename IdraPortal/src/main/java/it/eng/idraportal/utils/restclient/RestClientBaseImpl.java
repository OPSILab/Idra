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
package it.eng.idraportal.utils.restclient;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;

import it.eng.idraportal.utils.restclient.builders.HttpDeleteBuilder;
import it.eng.idraportal.utils.restclient.builders.HttpGetBuilder;
import it.eng.idraportal.utils.restclient.builders.HttpHeadBuilder;
import it.eng.idraportal.utils.restclient.builders.HttpPostBuilder;
import it.eng.idraportal.utils.restclient.builders.HttpPutBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

import com.sun.research.ws.wadl.HTTPMethods;

@SuppressWarnings("deprecation")
public abstract class RestClientBaseImpl {
	
	protected static final Logger logger = Logger.getLogger(RestClient.class.getName());
	protected HttpClient httpclient = null;
	
	protected HttpClient buildClient(){
		
		SSLContextBuilder sshbuilder = new SSLContextBuilder();
		try {
			sshbuilder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sshbuilder.build());

			httpclient = HttpClients.custom()
				.setSSLHostnameVerifier(new NoopHostnameVerifier())
			    .setSSLSocketFactory(sslsf)
			    .build();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
