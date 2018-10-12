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
package it.eng.idra.utils;

import java.lang.reflect.Type;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import it.eng.idra.beans.odms.ODMSCatalogueAdditionalConfiguration;
import it.eng.idra.beans.orion.OrionCatalogueConfiguration;
import it.eng.idra.beans.sparql.SparqlCatalogueConfiguration;

public class ODMSCatalogueAdditionalConfigurationDeserializer implements JsonDeserializer<ODMSCatalogueAdditionalConfiguration>,JsonSerializer<ODMSCatalogueAdditionalConfiguration>{

	@Override
	public ODMSCatalogueAdditionalConfiguration deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2)
			throws JsonParseException {
		// TODO Auto-generated method stub
		JSONObject j = new JSONObject(arg0.toString());
		if(j.has("orionDatasetDumpString")) {
			boolean isAuth=false;
			String datasets=j.optString("orionDatasetDumpString","");
			String dumpPath=j.optString("orionDatasetFilePath","");
			String authToken="",oauth2Endpoint="",client_id="",client_secret="";
			try {
				isAuth=j.getBoolean("isAuthenticated");
			}catch(JSONException e) {
				
			}
			if(isAuth) {
				authToken=j.optString("authToken");
				//refreshToken=j.optString("refreshToken");
				oauth2Endpoint=j.optString("oauth2Endpoint");
				client_id=j.optString("clientID");
				client_secret=j.optString("clientSecret");
			}
			//return new OrionCatalogueConfiguration(isAuth, authToken, refreshToken, oauth2Endpoint, client_id, client_secret, datasets,dumpPath);
			return new OrionCatalogueConfiguration(isAuth, authToken, oauth2Endpoint, client_id, client_secret, datasets,dumpPath);
		}else if(j.has("sparqlDatasetDumpString")) {
			String datasets=j.optString("sparqlDatasetDumpString","");
			String dumpPath=j.optString("sparqlDatasetFilePath","");
			return new SparqlCatalogueConfiguration(datasets,dumpPath);
		}
			return null;
	}

	@Override
	public JsonElement serialize(ODMSCatalogueAdditionalConfiguration arg0, Type arg1, JsonSerializationContext arg2) {
		// TODO Auto-generated method stub
		final JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id", arg0.getId());
		jsonObject.addProperty("type", arg0.getType());
		if(arg0.getType().toLowerCase().equals("orion")) {
			OrionCatalogueConfiguration c = (OrionCatalogueConfiguration) arg0;
			jsonObject.addProperty("isAuthenticated", c.isAuthenticated());
			jsonObject.addProperty("authToken", c.getAuthToken());
			//jsonObject.addProperty("refreshToken", c.getRefreshToken());
			jsonObject.addProperty("clientID", c.getClientID());
			jsonObject.addProperty("clientSecret", c.getClientSecret());
			jsonObject.addProperty("oauth2Endpoint", c.getOauth2Endpoint());
			jsonObject.addProperty("orionDatasetDumpString", c.getOrionDatasetDumpString());
			jsonObject.addProperty("orionDatasetFilePath", c.getOrionDatasetFilePath());
		}else if(arg0.getType().toLowerCase().equals("sparql")) {
			SparqlCatalogueConfiguration c = (SparqlCatalogueConfiguration) arg0;
			jsonObject.addProperty("sparqlDatasetDumpString", c.getSparqlDatasetDumpString());
			jsonObject.addProperty("sparqlDatasetFilePath", c.getSparqlDatasetFilePath());
		}
		return jsonObject;
	}

}
