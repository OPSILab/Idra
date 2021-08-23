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
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpRequestBase;

// TODO: Auto-generated Javadoc
/**
 * The Class HttpOptionsBuilder.
 */
public class HttpOptionsBuilder extends HttpRequestBuilder<HttpOptions> {

  /**
   * Instantiates a new http options builder.
   *
   * @param url the url
   */
  private HttpOptionsBuilder(URL url) {
    super.httpRequest = new HttpOptions(url.toString());
  }

  /**
   * Gets the single instance of HttpOptionsBuilder.
   *
   * @param url     the url
   * @param headers the headers
   * @param type    the type
   * @param data    the data
   * @return single instance of HttpOptionsBuilder
   */
  public static HttpRequestBase getInstance(URL url, Map<String, String> headers, MediaType type,
      String data) {
    HttpOptionsBuilder builder = new HttpOptionsBuilder(url);
    builder.addHeaders(headers);

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
    throw new RuntimeException("Payload not allowed in HTTP OPTIONS requests");
  }

}
