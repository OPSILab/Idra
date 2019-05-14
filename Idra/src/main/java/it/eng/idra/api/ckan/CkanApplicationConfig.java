package it.eng.idra.api.ckan;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/ckan")
public class CkanApplicationConfig extends Application{

	@Override
	public Set<Class<?>> getClasses() {

		Set<Class<?>> resources = new HashSet<Class<?>>();
		resources.add(org.glassfish.jersey.media.multipart.MultiPartFeature.class);
		resources.add(it.eng.idra.api.CORSResponseFilter.class);
		resources.add(it.eng.idra.api.ckan.CKANApi.class);
		
		return resources;
	}
}
