/*******************************************************************************
 * Idra - Open Data Federation Platform
 * Copyright (C) 2021 Engineering Ingegneria Informatica S.p.A.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

package it.eng.idra.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import it.eng.idra.authentication.fiware.model.Token;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;

// TODO: Auto-generated Javadoc
/**
 * The Class TokenGsonManager.
 */
public class TokenGsonManager implements JsonDeserializer<Token> {

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.google.gson.JsonDeserializer#deserialize(com.google.gson.JsonElement,
   * java.lang.reflect.Type, com.google.gson.JsonDeserializationContext)
   */
  @Override
  public Token deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2)
      throws JsonParseException {
    // TODO Auto-generated method stub
    JSONObject j = new JSONObject(arg0.toString());
    String accessToken = j.optString("access_token", "");
    Integer expiresIn = j.optInt("expires_in", 0);
    String tokenType = j.optString("token_type", "");
    String state = j.optString("state", "");
    String refreshToken = j.optString("refresh_token", "");
    List<String> list = new ArrayList<String>();
    if (j.has("scope")) {
      Object s = j.get("scope");
      if (s instanceof JSONArray) {
        try {
          list = GsonUtil.json2Obj(j.optString("scope", ""), GsonUtil.stringListType);
        } catch (GsonUtilException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      } else if (s instanceof String) {
        String tmp = j.optString("scope", "");
        list.add(tmp);
      } else {
        JSONObject jscope = j.optJSONObject("scope");
        if (jscope != null) {
          list.add(jscope.toString());
        }
      }
    }

    Set<String> scope = new HashSet<String>(list);

    return new Token(accessToken, tokenType, expiresIn, refreshToken, scope, state);
  }

}
