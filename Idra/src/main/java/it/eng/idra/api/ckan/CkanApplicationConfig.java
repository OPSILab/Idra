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

package it.eng.idra.api.ckan;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

// TODO: Auto-generated Javadoc
/**
 * The Class CkanApplicationConfig.
 */
@ApplicationPath("/ckan")
public class CkanApplicationConfig extends Application {

  /*
   * (non-Javadoc)
   * 
   * @see javax.ws.rs.core.Application#getClasses()
   */
  @Override
  public Set<Class<?>> getClasses() {

    Set<Class<?>> resources = new HashSet<Class<?>>();
    resources.add(org.glassfish.jersey.media.multipart.MultiPartFeature.class);
    resources.add(it.eng.idra.api.CorsResponseFilter.class);
    resources.add(it.eng.idra.api.ckan.CkanApi.class);

    return resources;
  }
}
