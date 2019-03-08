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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import it.eng.idra.beans.ODFProperty;
import it.eng.idra.beans.dcat.DCATDataset;
import it.eng.idra.beans.dcat.DCATDistribution;
import it.eng.idra.beans.dcat.DCTLicenseDocument;
import it.eng.idra.beans.dcat.DCTLocation;
import it.eng.idra.beans.dcat.DCTPeriodOfTime;
import it.eng.idra.beans.dcat.DCTStandard;
import it.eng.idra.beans.dcat.FOAFAgent;
import it.eng.idra.beans.dcat.SKOSConcept;
import it.eng.idra.beans.dcat.SKOSConceptSubject;
import it.eng.idra.beans.dcat.SKOSConceptTheme;
import it.eng.idra.beans.dcat.SKOSPrefLabel;
import it.eng.idra.beans.dcat.VCardOrganization;
import it.eng.idra.beans.odms.ODMSCatalogue;
import it.eng.idra.beans.odms.ODMSSynchronizationResult;
import it.eng.idra.beans.orion.OrionCatalogueConfiguration;
import it.eng.idra.beans.orion.OrionDistributionConfig;
import it.eng.idra.management.ODMSManager;
import it.eng.idra.utils.CommonUtil;
import it.eng.idra.utils.GsonUtil;
import it.eng.idra.utils.PropertyManager;

public class OrionConnector implements IODMSConnector {

	private String nodeID;
	private ODMSCatalogue node;
	//The internal API used in case the query must be authenticated or if headers has to be set
	private static String orionFilePath = PropertyManager.getProperty(ODFProperty.ORION_FILE_DUMP_PATH);
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
		JSONObject j = (JSONObject) dataset;	
		
		String title = null, description = null, accessRights = null, frequency = null, landingPage = null,
				releaseDate = null, updateDate = null, identifier = null, type = null, version = null;

		String publisherIdentifier = null, publisherUri = null, publisherName = null, publisherMbox = null,
				publisherHomepage = null, publisherType = null;
		String holderIdentifier = null, holderUri = null, holderName = null, holderMbox = null, holderHomepage = null,
				holderType = null;
		String creatorIdentifier = null, creatorUri = null, creatorName = null, creatorMbox = null,
				creatorHomepage = null, creatorType = null;
		String startDate = null, endDate = null;
		String vCardUri = null, vCardFn = null, vCardHasEmail = null;
		List<DCTStandard> conformsTo = new ArrayList<DCTStandard>();
		FOAFAgent publisher = null, rightsHolder = null, creator = null;
		List<VCardOrganization> contactPointList = new ArrayList<VCardOrganization>();
		DCTPeriodOfTime temporalCoverage = null;
		DCTLocation spatialCoverage = null;
		DCTLicenseDocument license = null;
		String geographicalIdentifier = null, geographicalName = null, geometry = null;
		List<SKOSConceptTheme> themeList = new ArrayList<SKOSConceptTheme>();
		List<SKOSConceptSubject> subjectList =  new ArrayList<SKOSConceptSubject>();
		List<String> keywords = new ArrayList<String>(), documentation = new ArrayList<String>(),
				hasVersion = new ArrayList<String>(), isVersionOf = new ArrayList<String>(),
				language = new ArrayList<String>(), provenance = new ArrayList<String>(),
				otherIdentifier = new ArrayList<String>(), sample = new ArrayList<String>(),
				source = new ArrayList<String>(), versionNotes = new ArrayList<String>(), relatedResource = new ArrayList<String>();

		List<DCATDistribution> distributionList = new ArrayList<DCATDistribution>();
		
		title=j.optString("title", null);
		description=j.optString("description", null);
		identifier=UUID.randomUUID().toString();
		
		//Themes
		if(j.has("theme")) {
			List<String> themes = GsonUtil.json2Obj(j.getJSONArray("theme").toString(), GsonUtil.stringListType);
			if(themes.size()>0)
				themeList.addAll(extractConceptList(DCAT.theme.getURI(),themes,SKOSConceptTheme.class));
		}
		
		//Subject
		if(j.has("subject")) {
			List<String> subjects = GsonUtil.json2Obj(j.getJSONArray("subject").toString(), GsonUtil.stringListType);
			if(subjects.size()>0)
				subjectList.addAll(extractConceptList(DCTerms.subject.getURI(),subjects,SKOSConceptSubject.class));
		}
		
		//Publisher
		if(j.has("publisher")) {
			JSONObject pub = j.getJSONObject("publisher");
			if (pub.has("name") || pub.has("mbox") || pub.has("homepage") || pub.has("type") || pub.has("identifier") || pub.has("propertyUri"))
				publisher = new FOAFAgent(DCTerms.publisher.getURI(), pub.optString("propertyUri",null), pub.optString("name",null), pub.optString("mbox",null),
						pub.optString("homepage",null), pub.optString("type",null), pub.optString("identifier",null), nodeID);
		}
		
		//Creator
		if(j.has("creator")) {
			JSONObject pub = j.getJSONObject("creator");
			if (pub.has("name") || pub.has("mbox") || pub.has("homepage") || pub.has("type") || pub.has("identifier") || pub.has("propertyUri"))
				rightsHolder = new FOAFAgent(DCTerms.rightsHolder.getURI(), pub.optString("propertyUri",null), pub.optString("name",null), pub.optString("mbox",null),
						pub.optString("homepage",null), pub.optString("type",null), pub.optString("identifier",null), nodeID);
		}
		
		//RightsHolder
		if(j.has("rightsHolder")) {
			JSONObject pub = j.getJSONObject("rightsHolder");
			if (pub.has("name") || pub.has("mbox") || pub.has("homepage") || pub.has("type") || pub.has("identifier") || pub.has("propertyUri"))
				creator = new FOAFAgent(DCTerms.creator.getURI(), pub.optString("propertyUri",null), pub.optString("name",null), pub.optString("mbox",null),
						pub.optString("homepage",null), pub.optString("type",null), pub.optString("identifier",null), nodeID);
		}
		
		//Keywords
		if(j.has("keywords")) {
			keywords = GsonUtil.json2Obj(j.getJSONArray("keywords").toString(), GsonUtil.stringListType);
		}
		
		//List<VCardOrganization>
		if(j.has("contactPoint")) {
			JSONArray tmp_arr = j.getJSONArray("contactPoint");
			for(int i=0; i<tmp_arr.length(); i++) {
				JSONObject tmp = tmp_arr.getJSONObject(i);
				if(tmp.has("resourceUri")||tmp.has("fn")||tmp.has("hasEmail")||tmp.has("hasURL")||tmp.has("hasTelephoneValue")||tmp.has("hasTelephoneType"))
					contactPointList.add(new VCardOrganization(DCAT.contactPoint.getURI(), tmp.optString("resourceUri",null), tmp.optString("fn",null), tmp.optString("hasEmail",null),
							tmp.optString("hasURL",null), tmp.optString("hasTelephoneValue",null), tmp.optString("hasTelephoneType",null), nodeID));
				}
		}
		
		//List<DCATStandard> conformsTo
		if(j.has("conformsTo")) {
			JSONArray tmp_arr = j.getJSONArray("conformsTo");
			for(int i=0; i<tmp_arr.length();i++) {
				JSONObject tmp = tmp_arr.getJSONObject(i);
				if(tmp.has("identifier")||tmp.has("title")||tmp.has("description")||tmp.has("referenceDocumentation"))
					conformsTo.add(new DCTStandard(DCTerms.conformsTo.getURI(), tmp.optString("identifier",null), tmp.optString("title",null), tmp.optString("description",null), GsonUtil.json2Obj(j.getJSONArray("referenceDocumentation").toString(), GsonUtil.stringListType), nodeID));
			}
		}
		
		//Documentation
		if(j.has("documentation")) {
			documentation = GsonUtil.json2Obj(j.getJSONArray("documentation").toString(), GsonUtil.stringListType);
		}
		
		if(j.has("relatedResource")) {
			relatedResource = GsonUtil.json2Obj(j.getJSONArray("relatedResource").toString(), GsonUtil.stringListType);
		}
		
		if(j.has("hasVersion")) {
			hasVersion = GsonUtil.json2Obj(j.getJSONArray("hasVersion").toString(), GsonUtil.stringListType);
		}
		
		if(j.has("isVersionOf")) {
			isVersionOf = GsonUtil.json2Obj(j.getJSONArray("isVersionOf").toString(), GsonUtil.stringListType);
		}
		
		if(j.has("language")) {
			language = GsonUtil.json2Obj(j.getJSONArray("language").toString(), GsonUtil.stringListType);
		}
		
		if(j.has("provenance")) {
			provenance = GsonUtil.json2Obj(j.getJSONArray("provenance").toString(), GsonUtil.stringListType);
		}
		
		if(j.has("otherIdentifier")) {
			otherIdentifier = GsonUtil.json2Obj(j.getJSONArray("otherIdentifier").toString(), GsonUtil.stringListType);
		}
		
		if(j.has("sample")) {
			sample = GsonUtil.json2Obj(j.getJSONArray("sample").toString(), GsonUtil.stringListType);
		}
		
		if(j.has("source")) {
			source = GsonUtil.json2Obj(j.getJSONArray("source").toString(), GsonUtil.stringListType);
		}
		
		if(j.has("versionNotes")) {
			versionNotes = GsonUtil.json2Obj(j.getJSONArray("versionNotes").toString(), GsonUtil.stringListType);
		}
		
		accessRights=j.optString("accessRight", null);
		landingPage=j.optString("landingPage", null);
		type=j.optString("type", null);
		version=j.optString("version",null);
		frequency=j.optString("frequency",null);
		
		if(j.has("releaseDate"))
			releaseDate=CommonUtil.fixBadUTCDate(j.getString("releaseDate"));
		if(j.has("updateDate"))
			updateDate=CommonUtil.fixBadUTCDate(j.getString("updateDate"));
		
		if(j.has("spatialCoverage"))
			spatialCoverage = new DCTLocation(DCTerms.spatial.getURI(), j.getJSONObject("spatialCoverage").optString("geographicalIdentifier", null),j.getJSONObject("spatialCoverage").optString("geographicalName", null) ,
					j.getJSONObject("spatialCoverage").optString("geometry", null), nodeID);
		
		if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate))
			temporalCoverage = new DCTPeriodOfTime(DCTerms.temporal.getURI(), startDate, endDate, nodeID);
		
		//Distribution
		if(j.has("distributions")) {
			JSONArray tmp_arr = j.getJSONArray("distributions");
			if(tmp_arr.length()==0) {
				throw new Exception("Orion Dataset must contain at least one distribution");
			}else {
				for(int i=0; i<tmp_arr.length(); i++) {
					JSONObject tmp = tmp_arr.getJSONObject(i);
					DCATDistribution distro = new DCATDistribution();
					distro.setNodeID(nodeID);
					//downloadURL e accessURL vengono settati dal metadata cache manager
					//Formato di default per orion
					distro.setFormat("fiware-ngsi");
					
					distro.setDescription(tmp.optString("description"));
					distro.setTitle(tmp.optString("title"));
					if(tmp.has("byteSize"))
						distro.setByteSize(tmp.optString("byteSize"));
					if(tmp.has("checksum"))
						distro.setChecksum(tmp.optString("checksum"));
					
					if(tmp.has("rights"))
						distro.setRights(tmp.optString("rights"));
					
					if(tmp.has("mediaType"))
						distro.setMediaType(tmp.optString("mediaType"));
						
					if(tmp.has("releaseDate"))
						distro.setReleaseDate(tmp.optString("releaseDate"));
					
					if(tmp.has("updateDate"))
						distro.setUpdateDate(tmp.optString("updateDate"));
					
					if(tmp.has("language")) 
						logger.info("Distribution language skipped");
					if(tmp.has("linkedSchemas")) 
						logger.info("Distribution linkedSchemas skipped");
					if(tmp.has("documentation")) 
						logger.info("Distribution documentation skipped");
					if(tmp.has("status")) 
						logger.info("Distribution status skipped");
					
					if(tmp.has("license")) {
						JSONObject l = tmp.getJSONObject("license");
						if(l.has("name")||l.has("uri")||l.has("type")||l.has("versionInfo")) {
							distro.setLicense(new DCTLicenseDocument(l.optString("uri"),l.optString("name"), l.optString("type"), l.optString("versionInfo"), nodeID));
						}
					}
					
					if(tmp.has("distributionAdditionalConfig")) {
						JSONObject o = tmp.getJSONObject("distributionAdditionalConfig");
						if(!o.has("query") || StringUtils.isBlank(o.optString("query",null))){
							throw new Exception("Each distribution must have the orionDistributionConfig with a query");
						}
						OrionDistributionConfig conf = new OrionDistributionConfig();
						conf.setFiwareService(o.optString("fiwareService", null));
						conf.setFiwareServicePath(o.optString("fiwareServicePath", null));
						conf.setQuery(o.getString("query"));
						conf.setNodeID(nodeID);
						//TODO: add validation for query
						distro.setDistributionAdditionalConfig(conf);
					}else {
						throw new Exception("Each distribution must have the orionDistributionConfig field");
					}
					
					distributionList.add(distro);
				}
			}
		}else {
			throw new Exception("Orion Dataset must contain at least one distribution");
		}
		
		return new DCATDataset(nodeID,identifier, title, description, distributionList, themeList, publisher, contactPointList,
				keywords, accessRights, conformsTo, documentation, frequency, hasVersion, isVersionOf, landingPage,
				language, provenance, releaseDate, updateDate, otherIdentifier, sample, source,
				spatialCoverage, temporalCoverage, type, version, versionNotes, rightsHolder, creator, subjectList,null);
	}

	@Override
	public DCATDataset getDataset(String datasetId) throws Exception {
		return null;
	}

	@Override
	public List<DCATDataset> getAllDatasets() throws Exception {
		OrionCatalogueConfiguration orionConfig = (OrionCatalogueConfiguration) node.getAdditionalConfig();
		
		if(StringUtils.isBlank(orionConfig.getOrionDatasetDumpString())){
			orionConfig.setOrionDatasetDumpString(new String(Files.readAllBytes(Paths.get(orionConfig.getOrionDatasetFilePath()))));
		}
				
		List<DCATDataset> result = new ArrayList<DCATDataset>();
		JSONArray datasets_json=new JSONArray(orionConfig.getOrionDatasetDumpString());
		for(int i=0; i<datasets_json.length(); i++)
			result.add(datasetToDCAT(datasets_json.get(i),node ));
		
		//if(StringUtils.isBlank(orionConfig.getOrionDatasetFilePath())) {
			
		try {
			CommonUtil.storeFile(orionFilePath,"orionDump_"+nodeID,orionConfig.getOrionDatasetDumpString());
			orionConfig.setOrionDatasetFilePath(orionFilePath+"orionDump_"+nodeID);
			orionConfig.setOrionDatasetDumpString(null);
			node.setAdditionalConfig(orionConfig);
			ODMSManager.updateODMSCatalogue(node, true);
		}catch(IOException e) {
			e.printStackTrace();
		}
		//}		
		return result;

	}

	@Override
	public ODMSSynchronizationResult getChangedDatasets(List<DCATDataset> oldDatasets, String startingDate)
			throws Exception {
		return null;
	}
	
	private <T extends SKOSConcept> List<T> extractConceptList(String propertyUri, List<String> concepts,Class<T> type) {
		List<T> result = new ArrayList<T>();

		for (String label : concepts) {
				try {
					result.add(type.getDeclaredConstructor(SKOSConcept.class).newInstance(new SKOSConcept(propertyUri, "", Arrays.asList(new SKOSPrefLabel("", label, nodeID)), nodeID)));
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		return result;
	}
	
}
