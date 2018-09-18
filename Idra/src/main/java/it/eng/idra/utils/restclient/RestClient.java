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
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.http.HttpResponse;

public interface RestClient {

	public HttpResponse sendPostRequest(String urlString, String data, MediaType type, Map<String, String> headers) throws MalformedURLException;

	public HttpResponse sendGetRequest(String urlString, Map<String, String> headers) throws MalformedURLException;
	
	public HttpResponse sendHeadRequest(String urlString, Map<String, String> headers) throws MalformedURLException;
	
	public HttpResponse sendPutRequest(String urlString, String data, MediaType type, Map<String, String> headers) throws MalformedURLException;

	public HttpResponse sendDeleteRequest(String urlString, Map<String, String> headers) throws MalformedURLException;
	
	public String getHttpResponseBody(HttpResponse httpresponse) throws Exception;

	public int getStatus(HttpResponse httpresponse);

}
