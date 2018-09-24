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
package it.eng.idra.connectors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.vocabulary.DCAT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import it.eng.idra.beans.dcat.DCATDataset;
import it.eng.idra.beans.dcat.DCATDistribution;
import it.eng.idra.beans.dcat.DCTLicenseDocument;
import it.eng.idra.beans.dcat.SKOSConcept;
import it.eng.idra.beans.dcat.SKOSConceptTheme;
import it.eng.idra.beans.dcat.SKOSPrefLabel;
import it.eng.idra.beans.odms.ODMSCatalogue;
import it.eng.idra.beans.odms.ODMSSynchronizationResult;
import it.eng.idra.beans.orion.OrionConfiguration;
import it.eng.idra.beans.orion.OrionDataset;

public class OrionConnector implements IODMSConnector {

	private String nodeID;
	private ODMSCatalogue node;
	//The internal API used in case the query must be authenticated or if headers has to be set
	private String internalAPI="";
	private static Logger logger = LogManager.getLogger(OrionConnector.class);

	public OrionConnector() {
	}

	public OrionConnector(ODMSCatalogue node) {
		this.node = node;
		this.nodeID = String.valueOf(node.getId());
	}

	@Override
	public List<DCATDataset> findDatasets(HashMap<String, Object> searchParameters) throws Exception {
		ArrayList<DCATDataset> resultDatasets = new ArrayList<DCATDataset>();
		return resultDatasets;
	}

	@Override
	public int countSearchDatasets(HashMap<String, Object> searchParameters) throws Exception {
		return 0;
	}

	@Override
	public int countDatasets() throws Exception {
		return -1;
	}

	@Override
	public DCATDataset datasetToDCAT(Object dataset, ODMSCatalogue node) throws Exception {
		OrionDataset d = (OrionDataset) dataset;
		
		//All Fields
		String title = null, description = null, identifier=null;

		DCTLicenseDocument license = null;
		List<SKOSConceptTheme> themeList = new ArrayList<SKOSConceptTheme>();

		List<DCATDistribution> distributionList = new ArrayList<DCATDistribution>();
		
		title=d.getTitle();
		description=d.getDescription();
		List<String> themesString = Arrays.asList(d.getThemes().split(","));
		for(String t : themesString) {
			themeList.add(new SKOSConceptTheme(new SKOSConcept(DCAT.theme.getURI(), "", Arrays.asList(new SKOSPrefLabel("", t, nodeID)), nodeID)));
		}
		
		try {
			license = new DCTLicenseDocument(d.getLicense(), null, null, null, nodeID);
		} catch (Exception ignore) {
			logger.info("License not valid! - Skipped");
		}
		
		DCATDistribution distribution = new DCATDistribution();
		//Query -> 1 distro per dataset al momento
		distribution.setTitle(title);
		distribution.setFormat("fiware-ngsi");
		distribution.setLicense(license);
		String url="";
		//If the CB doesn't need authentication or if fiware service path are empty or defaults -> set directly the query to the service
		//Otherwise set the url to a internal api that will manage the distribution
		if(!node.getOrionConfig().isAuthenticated() && StringUtils.isBlank(d.getFiwareService()) && (StringUtils.isBlank(d.getFiwareServicePath()) || d.getFiwareServicePath().equals("/"))) {
			url=node.getHost()+"?"+d.getQuery();
		}else {
			url= internalAPI+"?cbQueryID="+d.getId()+"&catalogue="+nodeID; //dovrei mettere l'id della query -> dovrebbe già esserci in quanto la persistenza viene fatta con il nodo,
																		 //Serve anche l'id del nodo? per recuperare il token
		}
		
		distribution.setDownloadURL(url);
		distribution.setAccessURL(url);
		
		distributionList.add(distribution);
		
		//Setting the identifier as the OrionDataset id
		identifier = d.getId();
		
		//TODO: add keywords, release date, update date (if present)
		
		DCATDataset res = new DCATDataset(url, identifier, title, description, distributionList, themeList, null,
										null, null, null, 
										null, null, null, null, 
										null, null, null, null, 
										null, null, null, 
										null, null, null, null, 
										null, null, null, null, null, null);
	
		
		
		return res;
	}

	@Override
	public DCATDataset getDataset(String datasetId) throws Exception {
		return null;
	}

	@Override
	public List<DCATDataset> getAllDatasets() throws Exception {
		OrionConfiguration orionConfig = node.getOrionConfig();
		List<DCATDataset> datasetsList = new ArrayList<DCATDataset>();
		
		for(OrionDataset orionD : orionConfig.getOrionDataset()) {
			datasetsList.add(datasetToDCAT(orionD, node));
		}
		
		return datasetsList;

	}

	@Override
	public ODMSSynchronizationResult getChangedDatasets(List<DCATDataset> oldDatasets, String startingDate)
			throws Exception {
		return null;
	}

}
