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
package it.eng.idra.authentication.fiware.connectors;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.core.MediaType;

import org.apache.http.HttpResponse;

import com.google.gson.Gson;

import it.eng.idra.authentication.fiware.model.GrantErrorMessage;
import it.eng.idra.authentication.fiware.model.Token;
import it.eng.idra.authentication.fiware.model.UserInfo;
import it.eng.idra.utils.restclient.RestClient;
import it.eng.idra.utils.restclient.RestClientImpl;

/**
 * Integration with Fiware IDM version 7.0.0
 * 
 * 
 */
public class Keyrock7Connector extends FiwareIDMConnector {

	public Keyrock7Connector(String protocol, String host, int port, String clientId, String clientSecret,
			String redirectUri) {
		super(protocol, host, port, clientId, clientSecret, redirectUri);
	}

	public Token getToken(String code) throws Exception {

		Optional<Token> token = Optional.empty();

		String url = baseUrl + path_token;
		String auth = "Basic " + new String(Base64.getEncoder().encode((clientId + ":" + clientSecret).getBytes()));

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", auth);

		String reqData = "grant_type=authorization_code" + "&code=" + code + "&redirect_uri=" + redirectUri;

		RestClient client = new RestClientImpl();
		HttpResponse response = client.sendPostRequest(url, reqData, MediaType.APPLICATION_FORM_URLENCODED_TYPE,
				headers);

		String returned_json = client.getHttpResponseBody(response);
		switch (client.getStatus(response)) {
		case 500:
			GrantErrorMessage errorBody = new Gson().fromJson(returned_json, GrantErrorMessage.class);
			throw new RuntimeException("[" + errorBody.getStatus() + "] " + errorBody.getMessage());

		default:
			token = Optional.ofNullable(new Gson().fromJson(returned_json, Token.class));
		}

		return token.get();

	}

	public UserInfo getUserInfo(String token) throws Exception {

		Optional<UserInfo> userinfo = Optional.empty();
		String url = baseUrl + path_user + "?access_token=" + token;

		RestClient client = new RestClientImpl();
		HttpResponse response = client.sendGetRequest(url, new HashMap<String, String>());

		String returned_json = client.getHttpResponseBody(response);
		switch (client.getStatus(response)) {

		case 500:
			GrantErrorMessage errorBody = new Gson().fromJson(returned_json, GrantErrorMessage.class);
			throw new RuntimeException("[" + errorBody.getStatus() + "] " + errorBody.getMessage());

		default:
			userinfo = Optional.ofNullable(new Gson().fromJson(returned_json, UserInfo.class));
		}

		return userinfo.get();
	}

//	public static class Builder {
//
//		// Instance specific
//		private String protocol = PropertyManager.getProperty(IDMProperty.IDM_PROTOCOL_DEFAULT);
//		private String host;
//		private int port = Integer.parseInt(PropertyManager.getProperty(IDMProperty.IDM_PORT_DEFAULT));
//
//		public Builder setProtocol(String protocol) {
//			this.protocol = protocol;
//			return this;
//		}
//
//		public Builder setHost(String host) {
//			this.host = host;
//			return this;
//		}
//
//		public Builder setPort(int port) {
//			this.port = port;
//			return this;
//		}
//
//		// Application specific
//		private String clientid;
//		private String clientsecret;
//		private String redirecturi;
//
//		public Builder setClientid(String clientid) {
//			this.clientid = clientid;
//			return this;
//		}
//
//		public Builder setClientsecret(String clientsecret) {
//			this.clientsecret = clientsecret;
//			return this;
//		}
//
//		public Builder setRedirecturi(String redirecturi) {
//			this.redirecturi = redirecturi;
//			return this;
//		}
//
//		public Keyrock7Connector build() {
//			return new Keyrock7Connector(protocol, host, port, clientid, clientsecret, redirecturi);
//		}
//
//	}

}
