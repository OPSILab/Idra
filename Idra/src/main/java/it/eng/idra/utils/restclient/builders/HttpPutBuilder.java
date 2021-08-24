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

package it.eng.idra.utils.restclient.builders;

import java.net.URL;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;

// TODO: Auto-generated Javadoc
/**
 * The Class HttpPutBuilder.
 */
public class HttpPutBuilder extends HttpRequestBuilder<HttpPut> {

  /**
   * Instantiates a new http put builder.
   *
   * @param url the url
   */
  private HttpPutBuilder(URL url) {
    super.httpRequest = new HttpPut(url.toString());
  }

  /**
   * Gets the single instance of HttpPutBuilder.
   *
   * @param url     the url
   * @param headers the headers
   * @param type    the type
   * @param data    the data
   * @return single instance of HttpPutBuilder
   */
  public static HttpRequestBase getInstance(URL url, Map<String, String> headers, MediaType type,
      String data) {
    HttpPutBuilder builder = new HttpPutBuilder(url);
    builder.addHeaders(headers);
    builder.addPayload(type, data);

    return builder.httpRequest;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * it.eng.idra.utils.restclient.builders.HttpRequestBuilder#addPayload(javax.ws.
   * rs.core.MediaType, java.lang.String)
   */
  @Override
  protected void addPayload(MediaType type, String data) {
    try {
      StringEntity input = new StringEntity(data);
      input.setContentType(type.toString());
      super.httpRequest.setEntity(input);
    } catch (Exception e) {
      logger.warning(e.toString());
    }
  }

}
