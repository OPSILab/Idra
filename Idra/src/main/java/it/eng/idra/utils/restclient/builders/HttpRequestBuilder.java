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

import java.util.Map;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import org.apache.http.client.methods.HttpRequestBase;

// TODO: Auto-generated Javadoc
/**
 * The Class HttpRequestBuilder.
 *
 * @param <T> the generic type
 */
public abstract class HttpRequestBuilder<T extends HttpRequestBase> {

  /** The Constant logger. */
  protected static final Logger logger = Logger.getLogger(HttpRequestBuilder.class.getName());

  /** The http request. */
  protected T httpRequest;

  /**
   * Adds the headers.
   *
   * @param headers the headers
   */
  protected void addHeaders(Map<String, String> headers) {
    for (Map.Entry<String, String> h : headers.entrySet()) {
      httpRequest.addHeader(h.getKey(), h.getValue());
    }
  }

  /**
   * Adds the payload.
   *
   * @param type the type
   * @param data the data
   */
  protected abstract void addPayload(MediaType type, String data);

}
