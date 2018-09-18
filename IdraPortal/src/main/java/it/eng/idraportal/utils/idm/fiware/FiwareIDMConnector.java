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
package it.eng.idraportal.utils.idm.fiware;

import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

import com.google.gson.Gson;
import it.eng.idraportal.utils.PropertyManager;

import it.eng.idraportal.utils.idm.fiware.configuration.IDMProperty;

import it.eng.idraportal.utils.idm.fiware.model.Auth;
import it.eng.idraportal.utils.idm.fiware.model.Domain;
import it.eng.idraportal.utils.idm.fiware.model.Identity;
import it.eng.idraportal.utils.idm.fiware.model.Password;
import it.eng.idraportal.utils.idm.fiware.model.Token;
import it.eng.idraportal.utils.idm.fiware.model.User;
import it.eng.idraportal.utils.idm.fiware.model.UserInfo;
import it.eng.idraportal.utils.idm.fiware.model.UserTokenBean;
import it.eng.idraportal.utils.restclient.RestClient;
import it.eng.idraportal.utils.restclient.RestClientImpl;

/**
 * Integration with Fiware IDM version 6.0.0
 * @author Antonino Sirchia [antonino.sirchia@eng.it]
 */
@Deprecated
public class FiwareIDMConnector {

	private static final String baseurl = PropertyManager.getProperty(IDMProperty.IDM_FIWARE_PROTOCOL)+"://"+PropertyManager.getProperty(IDMProperty.IDM_FIWARE_HOST)+PropertyManager.getProperty(IDMProperty.IDM_FIWARE_PATH_BASE);
	private static final String path_token = PropertyManager.getProperty(IDMProperty.IDM_FIWARE_PATH_TOKEN);
	private static final String path_user = PropertyManager.getProperty(IDMProperty.IDM_FIWARE_PATH_USER);
	private static final String keystone_baseurl = PropertyManager.getProperty(IDMProperty.IDM_FIWARE_PROTOCOL)+"://"+PropertyManager.getProperty(IDMProperty.IDM_FIWARE_KEYSTONE_HOST) + ":" + PropertyManager.getProperty(IDMProperty.IDM_FIWARE_KEYSTONE_PORT);
	private static final String keyston_path_tokens = PropertyManager.getProperty(IDMProperty.IDM_FIWARE_KEYSTONE_PATH_TOKENS);
	
	public void logout(String token) {
		return;
	}

	public Token getToken(String code, String client_id, String client_secret, String redirectUri) 
			throws Exception {
		
		String url = baseurl + path_token;
		String auth = "Basic " + new String(Base64.getEncoder()
				.encode((client_id + ":" + client_secret).getBytes()));
		
		Map<String, String> headers = new HashMap<String, String>();
							headers.put("Authorization", auth);
							
		String reqData = "grant_type=authorization_code"
							+ "&code=" + code 
							+ "&redirect_uri=" + redirectUri;
		
		RestClient client = new RestClientImpl();		
		HttpResponse response = client.sendPostRequest(url, reqData, MediaType.APPLICATION_FORM_URLENCODED_TYPE, headers);
		
		int status = client.getStatus(response);
		if(status!=200 && status!=201 && status!=301)
			 throw new Exception("Unable to retrieve token: "+status+": "+response.getStatusLine().getReasonPhrase());
		
		String returned_json = client.getHttpResponseBody(response);
		
		return new Gson().fromJson(returned_json, Token.class);
		
	}

	public Token getAdminToken(String adminuser, String adminpassword)
			throws Exception {
		
		 Domain domain = new Domain("default");
		 User user = new User(adminuser, domain, adminpassword);
		  
		 Password password = new Password(user);
		 Set<String> methods = new HashSet<String>();
		     methods.add("password");
		  
		 Identity identity = new Identity(methods, password);
		 Auth auth = new Auth(identity);
		  
		 UserTokenBean utb = new UserTokenBean(auth);
		 Gson gson = new Gson();
		 String jsutb = gson.toJson(utb);
		  
		 String idmEndpoint =  keystone_baseurl + keyston_path_tokens;
		 
		 RestClient client = new RestClientImpl();
		 HttpResponse response = client.sendPostRequest(idmEndpoint, jsutb, MediaType.APPLICATION_JSON_TYPE, new HashMap<String, String>());
			
		 int status = client.getStatus(response);
		 if(status!=200 && status!=201 && status!=301)
				 throw new Exception("Unable to retrieve token: "+status);

		 Header respHeaders = response.getFirstHeader("X-Subject-Token");
		 String token = respHeaders.getValue();
		 
		 return new Token(token, null, null, null, null, null);
	}

	public Token refreshToken(String token, String refresh_token, String client_id, String client_secret) throws Exception {
		String url = baseurl + path_token;
		
		String contentType = MediaType.APPLICATION_FORM_URLENCODED;
		String Authorization = "Basic "
				+ new String(Base64.getEncoder().encode((client_id + ":" + client_secret).getBytes()));
		
		Map<String, String> headers = new HashMap<String, String>();
			headers.put("Content-Type", contentType);
			headers.put("Authorization", Authorization);
		
		String reqData = "grant_type=refresh_token"
						+ "&client_id=" + client_id 
						+ "&client_secret=" + client_secret
						+ "&refresh_token=" + refresh_token;
		
		RestClient client = new RestClientImpl();
		HttpResponse response = client.sendPostRequest(url, reqData, MediaType.APPLICATION_JSON_TYPE, headers);
		 
		int status = client.getStatus(response);
		if(status!=200 && status!=201 && status!=301)
			 throw new Exception("Unable to refresh token: "+status);
		
		String returned_json = client.getHttpResponseBody(response);
		
		return new Gson().fromJson(returned_json, Token.class);
	}

	public UserInfo getUserInfo(String token) throws Exception {
		String url = baseurl + path_user + "?access_token="+token;
		
		RestClient client = new RestClientImpl();
		HttpResponse response = client.sendGetRequest(url, new HashMap<String, String>());
		 
		int status = client.getStatus(response);
		if(status!=200 && status!=201 && status!=301)
			 throw new Exception("Unable to get user info: "+status);
		
		String returned_json = client.getHttpResponseBody(response);
		
		return new Gson().fromJson(returned_json, UserInfo.class);
		
	}

	public boolean isValid(String token) throws Exception {
		boolean isvalid = true;
		try{ getUserInfo(token); }
		catch(Exception e){
			isvalid = false;
		}
		
		return isvalid;
	}
	
}