package it.eng.idra.management;

import static org.junit.Assert.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;
import it.eng.idra.beans.IdraProperty;
import it.eng.idra.beans.RemoteCatalogue;
import it.eng.idra.beans.exception.DatasetNotFoundException;
import it.eng.idra.beans.odms.ODMSAlreadyPresentException;
import it.eng.idra.beans.odms.ODMSCatalogue;
import it.eng.idra.beans.odms.ODMSCatalogueForbiddenException;
import it.eng.idra.beans.odms.ODMSCatalogueNotFoundException;
import it.eng.idra.beans.odms.ODMSCatalogueOfflineException;
import it.eng.idra.beans.odms.ODMSCatalogueSSLException;
import it.eng.idra.utils.PropertyManager;
import it.eng.idra.beans.odms.ODMSCatalogueType;
import it.eng.idra.beans.odms.ODMSManagerException;
import it.eng.idra.scheduler.exception.SchedulerNotInitialisedException;

public class FederationCoreTest { 

	public static void init() {
		FederationCore.init((Boolean.parseBoolean(PropertyManager.getProperty(IdraProperty.LOAD_CACHE_FROM_DB))), null);
	}
	
	static RemoteCatalogue remCatalogueTest = new RemoteCatalogue();
	static ODMSCatalogue catalogueTest = new ODMSCatalogue();
	
	@Test
	public void testSetRemoteCatalogue() throws SQLException {
	
		remCatalogueTest.setCatalogueName("json remote catalogue");
		remCatalogueTest.setURL("http://localhost/catalogue.json");
		
		assertTrue(FederationCore.setRemoteCatalogue(remCatalogueTest));
		
		assertEquals(1, FederationCore.getAllRemCatalogues().size());
		
	}
	
	@Test
	public void testGetRemCatalogue() throws SQLException {
			
			assertNotNull(FederationCore.getAllRemCatalogues());
			
			List<RemoteCatalogue> remCatalogues = FederationCore.getAllRemCatalogues();
			
			if(remCatalogues.size()> 0) {
				for(RemoteCatalogue cat: remCatalogues) {
					int id = cat.getId();
					assertEquals(FederationCore.getRemCat(id), cat);
				}
			}
	}
	
	@Test
	public void testUpdateRemoteCatalogue() throws SQLException {
		int id ;
		String newName = "json updated remote catalogue";
		
		remCatalogueTest.setCatalogueName(newName);
		
		assertTrue(FederationCore.updateRemCat(remCatalogueTest));
		
		id = FederationCore.getAllRemCatalogues().get(0).getId();
		assertEquals(newName, FederationCore.getRemCat(id).getCatalogueName());
		
	}
	
		
	@Test
	public void testDeleteRemCat() throws SQLException {
		int id = FederationCore.getAllRemCatalogues().get(0).getId();
		
		assertTrue(FederationCore.deleteRemCat(id));
		
		assertNull(FederationCore.getRemCat(id));
		
	}
	
	
	@Test
	public void testRegisterODMSCatalogue() throws SQLException, InvocationTargetException, ODMSAlreadyPresentException, ODMSManagerException, ODMSCatalogueNotFoundException, ODMSCatalogueForbiddenException, ODMSCatalogueSSLException, ODMSCatalogueOfflineException, SchedulerNotInitialisedException {
		int id;
		
		catalogueTest.setHost("https://demo.ckan.org/");
		catalogueTest.setName("Test Catalogue");
		catalogueTest.setNodeType(ODMSCatalogueType.CKAN);
		
		FederationCore.registerODMSCatalogue(catalogueTest);
		
		id = FederationCore.getODMSCatalogueIDbyName(catalogueTest.getName());
		assertNotNull(id);
		
		assertEquals(1, FederationCore.getODMSCatalogues().size());
		
	}
	
	@Test
	public void testDeactivateODMSCatalogue() throws ODMSCatalogueNotFoundException, ODMSManagerException, IOException, SolrServerException, DatasetNotFoundException {
		
		FederationCore.deactivateODMSCatalogue(catalogueTest, true);
		
		assertEquals(1, FederationCore.getAllInactiveODMSCatalogues(false).size());
		
		assertEquals(catalogueTest, FederationCore.getInactiveODMSCatalogue(FederationCore.getAllInactiveODMSCatalogues(false).get(0).getId()));
	}
	
	@Test
	public void unregisterODMSCatalogue() throws Exception {
		
		FederationCore.unregisterODMSCatalogue(catalogueTest);
		
		assertNull(FederationCore.getODMSCatalogues());
		
		assertNull(FederationCore.getODMSCatalogue(catalogueTest.getId()));
		
	}

}
