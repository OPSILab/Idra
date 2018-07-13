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
package it.eng.idra.connectors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import it.eng.idra.beans.ODFProperty;
import it.eng.idra.beans.odms.ODMSCatalogue;
import it.eng.idra.beans.odms.ODMSCatalogueForbiddenException;
import it.eng.idra.beans.odms.ODMSCatalogueNotFoundException;
import it.eng.idra.beans.odms.ODMSCatalogueOfflineException;
import it.eng.idra.utils.PropertyManager;

@SuppressWarnings("deprecation")
public class NativeClient {

	private ODMSCatalogue node;
	private static Logger logger = LogManager.getLogger(NativeClient.class);

	static {
	}

	public NativeClient(ODMSCatalogue node) {
		this.node = node;
	}

	public JSONObject findDatasets(String query, String sort, String rows, String offset)
			throws ODMSCatalogueNotFoundException, ODMSCatalogueForbiddenException, ODMSCatalogueOfflineException {

		String payload = "{";

		if (!(sort.trim().equals("") || sort == null))
			payload += "\"sort\":\"" + sort + "\",";

		if (!(rows.trim().equals("") || rows == null))
			payload += "\"rows\":" + Integer.parseInt(rows) + ",";

		if (!(offset.trim().equals("") || offset == null))
			payload += "\"offset\":" + Integer.parseInt(offset) + ",";

		payload += "\"query\":\"" + query + "\"}";

		String returned_json = sendPostRequest(node.getHost() + "/odf/odms/search", payload);
		System.out.println(returned_json);
		if (!returned_json.startsWith("{")) {

			if (returned_json.matches(".*The requested URL could not be retrieved.*"))
				throw new ODMSCatalogueNotFoundException(" The ODMS host does not exist");
			else if (returned_json.contains("403"))
				throw new ODMSCatalogueForbiddenException(" The ODMS node is forbidden");
			else
				throw new ODMSCatalogueOfflineException(" The ODMS node is currently unreachable");
		}

		return new JSONObject(returned_json);

	}

	public JSONObject getDataset(String id)
			throws ODMSCatalogueNotFoundException, ODMSCatalogueForbiddenException, ODMSCatalogueOfflineException {

		try {
			return new JSONObject(sendGetRequest(node.getHost() + "/odf/odms/datasets/" + id));

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		// if(!returnedJson.startsWith("{")){
		//
		// if(returnedJson.matches(".*The requested URL could not be
		// retrieved.*"))
		// throw new ODMSCatalogueNotFoundException(" The ODMS host does not exist");
		// else if(returnedJson.contains("403"))
		// throw new ODMSCatalogueForbiddenException(" The ODMS node is forbidden");
		// else
		// throw new ODMSCatalogueOfflineException(" The ODMS node is currently
		// unreachable");
		// }

	}

	public JSONArray getAllDatasetsID()
			throws ODMSCatalogueNotFoundException, ODMSCatalogueForbiddenException, ODMSCatalogueOfflineException {

		String returnedJson = sendGetRequest(node.getHost() + "/odf/odms/datasets/info");

		// if(!returnedJson.startsWith("{")){
		// System.out.println("E' UN ARRAY");
		// if(returnedJson.matches(".*The requested URL could not be
		// retrieved.*"))
		// throw new ODMSCatalogueNotFoundException(" The ODMS host does not exist");
		// else if(returnedJson.contains("403"))
		// throw new ODMSCatalogueForbiddenException(" The ODMS node is forbidden");
		// else
		// throw new ODMSCatalogueOfflineException(" The ODMS node is currently
		// unreachable");
		// }

		return new JSONArray(returnedJson);
	}

	// DA COMPLETARE E MODIFICARE CON OFFSET E LIMIT
	public JSONArray getAllDatasets(int offset, int limit)
			throws ODMSCatalogueNotFoundException, ODMSCatalogueForbiddenException, ODMSCatalogueOfflineException {

		try {
			return new JSONArray(sendGetRequest(node.getHost() + "/odf/odms/datasets"));

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		// if(!returnedJson.startsWith("{")){
		//
		// if(returnedJson.matches(".*The requested URL could not be
		// retrieved.*"))
		// throw new ODMSCatalogueNotFoundException(" The ODMS host does not exist");
		// else if(returnedJson.contains("403"))
		// throw new ODMSCatalogueForbiddenException(" The ODMS node is forbidden");
		// else
		// throw new ODMSCatalogueOfflineException(" The ODMS node is currently
		// unreachable");
		// }

	}

	public static String sendGetRequest(String urlString) {
		URL url = null;

		try {
			// url = new URL( this.m_host + ":" + this.m_port + path);
			url = new URL(urlString);
		} catch (MalformedURLException mue) {
			System.err.println(mue);
			return null;
		}

		String body = "";

		final HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 3000000);
		// if(!(data.contains("\"rows\":\"1\"") ||
		// data.contains("\"rows\":\"0\"") || path.contains("package_list")) )
		// HttpConnectionParams.setSoTimeout(httpParams, 6);
		// else
		HttpConnectionParams.setSoTimeout(httpParams, 9000000);

		// apache HttpClient version >4.2 should use
		// BasicClientConnectionManager
		HttpClient httpclient = new DefaultHttpClient(httpParams);

		// HttpClient httpclient = new DefaultHttpClient(httpParams);
		/*
		 * Set an HTTP proxy if it is specified in system properties.
		 * 
		 * http://docs.oracle.com/javase/6/docs/technotes/guides/net/proxies. html
		 * http://hc.apache.org/httpcomponents-client-ga/httpclient/examples/org
		 * /apache/http/examples/client/ClientExecuteProxy.java
		 */
//		if (Boolean.parseBoolean(PropertyManager.getProperty(ODFProperty.HTTP_PROXY_ENABLED).trim())
//				&& StringUtils.isNotBlank(PropertyManager.getProperty(ODFProperty.HTTP_PROXY_HOST).trim())) {
//
//			int port = 80;
//			if (isSet(PropertyManager.getProperty(ODFProperty.HTTP_PROXY_PORT))) {
//				port = Integer.parseInt(PropertyManager.getProperty(ODFProperty.HTTP_PROXY_PORT));
//			}
//			HttpHost proxy = new HttpHost(PropertyManager.getProperty(ODFProperty.HTTP_PROXY_HOST), port, "http");
//			httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
//			if (isSet(PropertyManager.getProperty(ODFProperty.HTTP_PROXY_USER))) {
//				((AbstractHttpClient) httpclient).getCredentialsProvider().setCredentials(
//						new AuthScope(PropertyManager.getProperty(ODFProperty.HTTP_PROXY_HOST), port),
//						(Credentials) new UsernamePasswordCredentials(
//								PropertyManager.getProperty(ODFProperty.HTTP_PROXY_USER),
//								PropertyManager.getProperty(ODFProperty.HTTP_PROXY_PASSWORD)));
//			}
//		}
		try {
			HttpGet getRequest = new HttpGet(url.toString());
			getRequest.addHeader("accept", "application/json");

			HttpResponse response = httpclient.execute(getRequest);

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
			}

			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			body = result.toString();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			logger.info(ioe);
		} finally {
			httpclient.getConnectionManager().shutdown();
		}

		return body;
	}

	private String sendPostRequest(String urlString, String data) {

		URL url = null;

		// url = new URL( this.m_host + ":" + this.m_port + path);
		try {
			url = new URL(urlString);
		} catch (MalformedURLException mue) {
			System.err.println(mue);
			return null;
		}

		String body = "";

		// RequestConfig requestConfig =
		// RequestConfig.custom().setConnectTimeout(300 * 1000).build();

		final HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 300000);
		// if(!(data.contains("\"rows\":\"1\"") ||
		// data.contains("\"rows\":\"0\"") || path.contains("package_list")) )
		// HttpConnectionParams.setSoTimeout(httpParams, 6);
		// else
		HttpConnectionParams.setSoTimeout(httpParams, 900000);

		HttpClient httpclient = new DefaultHttpClient(httpParams);

		/*
		 * Set an HTTP proxy if it is specified in system properties.
		 * 
		 * http://docs.oracle.com/javase/6/docs/technotes/guides/net/proxies. html
		 * http://hc.apache.org/httpcomponents-client-ga/httpclient/examples/org
		 * /apache/http/examples/client/ClientExecuteProxy.java
		 */
		if (Boolean.parseBoolean(PropertyManager.getProperty(ODFProperty.HTTP_PROXY_ENABLED).trim())
				&& StringUtils.isNotBlank(PropertyManager.getProperty(ODFProperty.HTTP_PROXY_HOST).trim())) {

			int port = 80;
			if (isSet(PropertyManager.getProperty(ODFProperty.HTTP_PROXY_PORT))) {
				port = Integer.parseInt(PropertyManager.getProperty(ODFProperty.HTTP_PROXY_PORT));
			}
			HttpHost proxy = new HttpHost(PropertyManager.getProperty(ODFProperty.HTTP_PROXY_HOST), port, "http");
			httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
			if (isSet(PropertyManager.getProperty(ODFProperty.HTTP_PROXY_USER))) {
				((AbstractHttpClient) httpclient).getCredentialsProvider().setCredentials(
						new AuthScope(PropertyManager.getProperty(ODFProperty.HTTP_PROXY_HOST), port),
						(Credentials) new UsernamePasswordCredentials(
								PropertyManager.getProperty(ODFProperty.HTTP_PROXY_USER),
								PropertyManager.getProperty(ODFProperty.HTTP_PROXY_PASSWORD)));
			}
		}
		try {
			HttpPost postRequest = new HttpPost(url.toString());

			// postRequest.setConfig(requestConfig);

			StringEntity input = new StringEntity(data);
			input.setContentType("application/json");
			postRequest.setEntity(input);

			HttpResponse response = httpclient.execute(postRequest);
			int statusCode = response.getStatusLine().getStatusCode();

			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			body = result.toString();
		} catch (IOException ioe) {
			logger.info(ioe);
		} finally {
			httpclient.getConnectionManager().shutdown();
		}

		return body;
	}

	private static boolean isSet(String string) {
		return string != null && string.length() > 0;
	}

}
