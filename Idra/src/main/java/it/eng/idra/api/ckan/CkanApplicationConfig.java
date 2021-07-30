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
