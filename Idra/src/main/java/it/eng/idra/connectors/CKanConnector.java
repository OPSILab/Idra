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
import it.eng.idra.beans.dcat.SPDXChecksum;
import it.eng.idra.beans.dcat.VCardOrganization;
import it.eng.idra.beans.exception.DatasetNotFoundException;
import it.eng.idra.beans.odms.ODMSCatalogue;
import it.eng.idra.beans.odms.ODMSCatalogueForbiddenException;
import it.eng.idra.beans.odms.ODMSCatalogueNotFoundException;
import it.eng.idra.beans.odms.ODMSCatalogueOfflineException;
import it.eng.idra.beans.odms.ODMSSynchronizationResult;
import it.eng.idra.cache.MetadataCacheManager;
import it.eng.idra.management.StatisticsManager;
import it.eng.idra.utils.CommonUtil;
import it.eng.idra.utils.GsonUtil;
import it.eng.idra.utils.GsonUtilException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.logging.log4j.*;
import org.apache.solr.client.solrj.SolrServerException;
import org.ckan.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CKanConnector implements IODMSConnector {

	private String nodeID;
	private ODMSCatalogue node;
	static private HashMap<String, String> DCATtoCKANmap = new HashMap<String, String>();
	private static Logger logger = LogManager.getLogger(CKanConnector.class);

	private static final String GEO_BASE_URI = "http://publications.europa.eu/resource/authority/place/";
	private static final String GEOJSON_IMT = "https://www.iana.org/assignments/media-types/application/vnd.geo+json";

	public CKanConnector(ODMSCatalogue node) {
		this.node = node;
		this.nodeID = String.valueOf(node.getId());
		// DCAT to CKAN corrispondence for live search starting from DCAT fields
		// as search parameters
		// DCATtoCKANmap.put("title", "name");
		// DCATtoCKANmap.put("identifier", "id");
		DCATtoCKANmap.put("title", "title");
		DCATtoCKANmap.put("identifier", "name");
		DCATtoCKANmap.put("releaseDate", "metadata_created");
		DCATtoCKANmap.put("updateDate", "metadata_modified");
		DCATtoCKANmap.put("description", "notes");
		DCATtoCKANmap.put("landingPage", "url");
		DCATtoCKANmap.put("contactPoint_fn", "contact_name");
		DCATtoCKANmap.put("contactPoint_hasEmail", "contact_email");
		DCATtoCKANmap.put("publisher_name", "publisher_name");
	}

	/**
	 * Counts the datasets present in the node
	 * 
	 *
	 * 
	 * @param none
	 * 
	 * @throws MalformedURLException
	 * @throws CKANException
	 * @throws ODMSCatalogueOfflineException
	 * @throws ODMSCatalogueNotFoundException
	 * @throws ODMSCatalogueForbiddenException
	 * @returns int resulting datasets count
	 *
	 */
	@Override
	public int countDatasets() throws CKANException, MalformedURLException, ODMSCatalogueOfflineException,
			ODMSCatalogueNotFoundException, ODMSCatalogueForbiddenException {

		Client c = new Client(new Connection(node.getHost()), node.getAPIKey());

		Dataset.SearchResults result;
		try {

			result = c.findDatasets("", "", "0", "");

			c = null;
			if (result.count == 0)
				throw new ODMSCatalogueOfflineException(" The ODMS node is currently unreachable");

			return result.count;

		} catch (CKANException e) {
			e.printStackTrace();
			handleError(e);
			return 0;
		}
	}

	/**
	 * Performs dataset query on a federated CKAN node using CKAN API Client
	 * 
	 *
	 * 
	 * @param host
	 *            The host of CKAN node
	 * @param APIKey
	 *            The token required by CKAN API
	 * @param query
	 *            The query string to be searched for a match
	 * @throws CKANException
	 * @throws MalformedURLException
	 * @throws ODMSCatalogueOfflineException
	 * @throws ODMSCatalogueNotFoundException
	 * @throws ODMSCatalogueForbiddenException
	 * @returns List<DCATDataset> results converted to a List of DCATDataset
	 * @throws An
	 *             Exception if the request fails
	 */
	@Override
	public List<DCATDataset> findDatasets(HashMap<String, Object> searchParameters) throws CKANException,
			MalformedURLException, ODMSCatalogueNotFoundException, ODMSCatalogueForbiddenException, ODMSCatalogueOfflineException {

		ArrayList<DCATDataset> dcatResults = new ArrayList<DCATDataset>();

		String query = buildLiveQueryString(searchParameters);

		String sort = "", rows = "", start = "";

		if (searchParameters.containsKey("sort")) {
			sort = (String) searchParameters.remove("sort");
			String tmp1[] = sort.split(",");
			if (DCATtoCKANmap.containsKey(tmp1[0])) {
				tmp1[0] = DCATtoCKANmap.get(tmp1[0]);
			}
			sort = tmp1[0] + " " + tmp1[1];
			// if(DCATtoCKANmap.containsKey(sort.split(" ")));
		}

		if (searchParameters.containsKey("rows"))
			rows = (String) searchParameters.remove("rows");
		if (searchParameters.containsKey("start"))
			start = (String) searchParameters.remove("start");

		logger.info("-- CKAN Connector Request sent -- " + "ROWS: " + rows);
		Client c = new Client(new Connection(node.getHost()), node.getAPIKey());
		// si pu� fare qui il trick
		logger.info(dcatResults.size());
		// int start=0;
		// boolean stop=false;
		// int check=0;
		// while(!stop){
		logger.info("\n-----------------------\n");
		logger.info("NODE " + node.getDatasetCount());
		logger.info("START " + start);
		logger.info("RESULTS " + dcatResults.size());
		Dataset.SearchResults result;
		try {
			result = c.findDatasets(query, start, rows, sort);

			logger.info("-- CKAN Connector Response - Result count:" + result.results.size());
			for (Dataset d : result.results) {
				dcatResults.add(datasetToDCAT(d, node));
			}
			logger.info("\n-----------------------\n");
			start += result.results.size();
			// if(start==node.getDatasetCount()) stop=true;
			//
			// if(result.results.size() ==0 ){
			// check++;
			// start+=100;
			// if(check==5) stop=true;
			// }else{
			// check=0;
			// }
			// result = null;
		} catch (CKANException e) {
			handleError(e);
		}

		// }
		c = null;
		System.gc();

		// if(isEurovoc){
		// searchParameters.put("euroVoc", isEurovoc);
		// }

		return dcatResults;
	}

	/**
	 * Performs mapping from CKAN Dataset object to DCATDataset object
	 *
	 * In addition iterates over extras and tags object of a CKAN Dataset
	 *
	 * @param d
	 *            CKAN Dataset to be mapped
	 * @param nodeUrl
	 *            Node url which dataset belongs to
	 * @returns DCATDataset resulting mapped object
	 * @throws An
	 *             Exception if the request fails
	 */
	public DCATDataset datasetToDCAT(Object dataset, ODMSCatalogue node) {

		Dataset d = (Dataset) dataset;
		DCATDataset mapped;
		// Properties to be mapped among different CKAN fallback fields

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
		List<SKOSConceptSubject> subjectList = null;
		List<String> keywords = new ArrayList<String>(), documentation = new ArrayList<String>(),
				hasVersion = new ArrayList<String>(), isVersionOf = new ArrayList<String>(),
				language = new ArrayList<String>(), provenance = new ArrayList<String>(),
				otherIdentifier = new ArrayList<String>(), sample = new ArrayList<String>(),
				source = new ArrayList<String>(), versionNotes = new ArrayList<String>(), relatedResource = new ArrayList<String>();

		List<DCATDistribution> distributionList = new ArrayList<DCATDistribution>();

		otherIdentifier.add(d.getName());

		try {
			for (Extra e : d.getExtras()) {

				switch (e.getKey().toLowerCase()) {
				case "alternate_identifier":
					otherIdentifier.addAll(extractValueList(e.getValue()));
					break;
				case "theme":
					themeList.addAll(extractConceptList(DCAT.theme.getURI(), extractValueList(e.getValue()),SKOSConceptTheme.class));
					break;
				case "access_rights":
					accessRights = e.getValue();
					break;
				case "conforms_to":
					conformsTo = extractConformsTo(e.getValue());
					break;
				case "documentation":
					documentation = extractValueList(e.getValue());
					break;
				case "frequency":
					frequency = e.getValue();
					break;
				case "has_version":
					hasVersion = extractValueList(e.getValue());
					break;
				case "is_version_of":
					isVersionOf = extractValueList(e.getValue());
					break;
				case "language":
					language = extractValueList(e.getValue());
					break;
				case "provenance":
					provenance = extractValueList(e.getValue());
					break;
				case "source":
					source = extractValueList(e.getValue());
					break;
				case "sample":
					sample = extractValueList(e.getValue());
					break;
				case "spatial":
				case "spatial_uri":
				case "spatial_text":
				case "geographical_name":
				case "geographical_geonames_url":
				case "spatial_coverage":
				case "Copertura Geografica URI":
				case "geometry":
				case "spatial_geometry":
					String input = e.getValue();
					if (checkIfJsonObject(input))
						geometry = input;
					else if (input.startsWith("http://"))
						geographicalIdentifier = input.trim();
					else
						geographicalName = input.trim();

					break;
				case "temporal_start":
					startDate = e.getValue();
					break;
				case "temporal_end":
					endDate = e.getValue();
					break;
				case "dcat_type":
					type = e.getValue();
					break;
				case "dcat_version":
					String tempVer = d.getVersion();
					if (StringUtils.isBlank(tempVer))
						version = e.getValue();
					else
						version = tempVer;
				case "version_notes":
					versionNotes = extractValueList(e.getValue());
					break;

				case "publisher_identifier":
					publisherIdentifier = e.getValue();
					break;
				case "publisher_uri":
					publisherUri = e.getValue();
					break;
				case "publisher_name":
					publisherName = e.getValue();
					break;
				case "publisher_email":
					publisherMbox = e.getValue();
					break;
				case "publisher_url":
					publisherHomepage = e.getValue();
					break;
				case "publisher_type":
					publisherType = e.getValue();
					break;
				case "holder_identifier":
					holderIdentifier = e.getValue();
					break;
				case "holder_uri":
					holderUri = e.getValue();
					break;
				case "holder_name":
					holderName = e.getValue();
					break;
				case "holder_email":
					holderMbox = e.getValue();
					break;
				case "holder_url":
					holderHomepage = e.getValue();
					break;
				case "holder_type":
					holderType = e.getValue();
					break;
				case "creator_identifier":
					creatorIdentifier = e.getValue();
					break;
				case "creator_uri":
					creatorUri = e.getValue();
					break;
				case "creator_name":
					creatorName = e.getValue();
					break;
				case "creator_email":
					creatorMbox = e.getValue();
					break;
				case "creator_url":
					creatorHomepage = e.getValue();
					break;
				case "creator_type":
					creatorType = e.getValue();
					break;
				case "contact_uri":
					vCardUri = e.getValue();
					break;
				case "contact_name":
					vCardFn = e.getValue();
					break;
				case "contact_email":
					vCardHasEmail = e.getValue();
					break;
				default:
					break;
				}
			}

		} catch (NullPointerException e) {
		} finally {

			title = d.getTitle();
			description = StringUtils.isNotBlank(d.getNotes()) ? d.getNotes() : "";

			/*
			 * 07/10/16 robcalla_mod: prima utilizzavamo, se presenti, i campi Extras
			 * "identifier" o "guid" come identificatori di un dataset ed in alternativa
			 * l'id. adesso viene inserito solo ed esclusivamente l'id del dataset presente
			 * nel nodo ckan NOTA: se nel nodo sono presenti due dataset identici, ma con id
			 * diverso, questi vengono federati
			 */
			identifier = d.getId();

			// Convert date fields into ISO 8601 format with UTC time zone
			if(StringUtils.isNotBlank(d.getMetadata_created()))
				releaseDate = CommonUtil.fixBadUTCDate(d.getMetadata_created());
			if(StringUtils.isNotBlank(d.getMetadata_modified()))
				updateDate = CommonUtil.fixBadUTCDate(d.getMetadata_modified());

			if (StringUtils.isNotBlank(geographicalIdentifier) || StringUtils.isNotBlank(geographicalName)
					|| StringUtils.isNotBlank(geometry))
				spatialCoverage = new DCTLocation(DCTerms.spatial.getURI(), geographicalIdentifier, geographicalName,
						geometry, nodeID);

			if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate))
				temporalCoverage = new DCTPeriodOfTime(DCTerms.temporal.getURI(), startDate, endDate, nodeID);

			// Contact Point
			if (StringUtils.isBlank(vCardFn))
				if (StringUtils.isBlank((vCardFn = d.getMaintainer())))
					vCardFn = d.getAuthor();

			if (StringUtils.isBlank(vCardHasEmail))
				if (StringUtils.isBlank((vCardHasEmail = d.getMaintainer_email())))
					vCardHasEmail = d.getAuthor_email();

			if (vCardUri != null || vCardFn != null || vCardHasEmail != null)
				contactPointList.add(new VCardOrganization(DCAT.contactPoint.getURI(), vCardUri, vCardFn, vCardHasEmail,
						"", "", "", nodeID));

			// Publisher
			if (publisherUri != null || publisherName != null || publisherMbox != null || publisherHomepage != null
					|| publisherType != null || publisherIdentifier != null)
				publisher = new FOAFAgent(DCTerms.publisher.getURI(), publisherUri, publisherName, publisherMbox,
						publisherHomepage, publisherType, publisherIdentifier, nodeID);
			// Rights Holder
			if (holderUri != null || holderName != null || holderMbox != null || holderHomepage != null
					|| holderType != null || holderIdentifier != null)
				rightsHolder = new FOAFAgent(DCTerms.rightsHolder.getURI(), holderUri, holderName, holderMbox,
						holderHomepage, holderType, holderIdentifier, nodeID);
			// Creator
			if (creatorUri != null || creatorName != null || creatorMbox != null || creatorHomepage != null
					|| creatorType != null || creatorIdentifier != null)
				creator = new FOAFAgent(DCTerms.creator.getURI(), creatorUri, creatorName, creatorMbox, creatorHomepage,
						creatorType, creatorIdentifier, nodeID);
			// License
			String license_name = StringUtils.isNotBlank(d.getLicense_id()) ? d.getLicense_id()
					: (StringUtils.isNotBlank(d.getLicense_title()) ? d.getLicense_title() : "unknown");
			license = new DCTLicenseDocument(d.getLicense_url(), license_name, d.getLicense_id(), "", nodeID);

			// Keywords
			if(d.getTags()!=null)
				for (Tag t : d.getTags()) {
					keywords.addAll(Arrays.asList(t.getName().split(",")));
				}

			// Dataset url is built from node host and dataset identifier
			// landingPage = d.getUrl();
			String nodeHost = node.getHost();
			if (nodeHost.contains("http://datos.santander.es/catalogo"))
				landingPage = nodeHost.replace("catalogo" + (nodeHost.endsWith("/") ? "/" : ""),
						"dataset/?id=" + d.getName());
			else
				landingPage = nodeHost + (nodeHost.endsWith("/") ? "" : "/") + "dataset/" + identifier;

			// Distributions
			List<Resource> resourceList = d.getResources();
			if (resourceList != null)
				for (Resource r : resourceList) {
					distributionList.add(resourceToDCAT(r, landingPage, license));
				}
		}

		mapped = new DCATDataset(nodeID,identifier, title, description, distributionList, themeList, publisher, contactPointList,
				keywords, accessRights, conformsTo, documentation, frequency, hasVersion, isVersionOf, landingPage,
				language, provenance, releaseDate, updateDate, otherIdentifier, sample, source,
				spatialCoverage, temporalCoverage, type, version, versionNotes, rightsHolder, creator, subjectList,relatedResource);

		distributionList = null;
		publisher = null;
		contactPointList = null;

		return mapped;
	}

	/*
	 * Return a List of SKOSConcept, each of them containing a prefLabel from input
	 * String list.
	 */
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
	

	
	private List<String> extractValueList(String value) {
		
//		if(StringUtils.isBlank(value)) return null;
		
		//TODO: regex & groups
		List<String> result = new ArrayList<String>();
		
		if(StringUtils.isBlank(value)) return result;
		
		if (value.startsWith("["))
			try {
				result.addAll(GsonUtil.json2Obj(value, GsonUtil.stringListType));
			} catch (GsonUtilException ex) {
				if(StringUtils.isNotBlank(value)) {
					for(String s:value.split(",")) {
						result.add(s);
					}
				}else {
					result=null;
				}
			}
		else if (value.startsWith("{"))
			for(String s:value.substring(1, value.lastIndexOf("}")).split(",")) {
				result.add(s);
			}
		else
			for(String s:value.split(",")) {
				result.add(s);
			}
		
		return result;

	}
	
//	private List<String> extractValueList(String value) {
//
//		if (value.startsWith("["))
//			try {
//				return Arrays.asList(GsonUtil.json2Obj(value, GsonUtil.stringListType));
//			} catch (GsonUtilException ex) {
//				return StringUtils.isNotBlank(value) ? Arrays.asList(value.split(",")) : null;
//			}
//		else if (value.startsWith("{"))
//			return Arrays.asList(value.substring(1, value.lastIndexOf("}")).split(","));
//		else
//			return Arrays.asList(value.split(","));
//
//	}

	private List<DCTStandard> extractConformsTo(String value) {
		
//		if(StringUtils.isBlank(value)) return null;
		
		List<DCTStandard> result=new ArrayList<DCTStandard>();
		
		if(StringUtils.isBlank(value)) return result;
		
		if (value.startsWith("["))
			try {
				result.addAll(GsonUtil.json2Obj(value, GsonUtil.stringListType));
			} catch (GsonUtilException ex) {
				if(StringUtils.isNotBlank(value)) {
					for(String s:value.substring(1, value.lastIndexOf("}")).split(",")) {
						result.add(new DCTStandard(DCTerms.conformsTo.getURI(), s, "", "", new ArrayList<String>(), nodeID));
					}
				}else {
					result=null;
				}
			}
		else if (value.startsWith("{")) {
			for(String s:value.substring(1, value.lastIndexOf("}")).split(",")) {
				result.add(new DCTStandard(DCTerms.conformsTo.getURI(), s, "", "", new ArrayList<String>(), nodeID));
			}
		}else {
			for(String s:value.split(",")) {
				result.add(new DCTStandard(DCTerms.conformsTo.getURI(), s, "", "", new ArrayList<String>(), nodeID));
			}
		}
		return result;
	}
	
//	private List<DCTStandard> extractConformsTo(String value) {
//		
//		if(StringUtils.isNoneBlank(value)) return null;
//		
//		if (value.startsWith("["))
//			try {
//				return Arrays.asList(GsonUtil.json2Obj(value, GsonUtil.stringListType));
//			} catch (GsonUtilException ex) {
//				return StringUtils.isNotBlank(value) ? Arrays.asList(value.split(",")).stream().map(
//						item -> new DCTStandard(DCTerms.conformsTo.getURI(), item, "", "", Arrays.asList(""), nodeID))
//						.collect(Collectors.toList()) : null;
//			}
//		else if (value.startsWith("{"))
//			return Arrays.asList(value.substring(1, value.lastIndexOf("}")).split(",")).stream()
//					.map(item -> new DCTStandard(DCTerms.conformsTo.getURI(), item, "", "", Arrays.asList(""), nodeID))
//					.collect(Collectors.toList());
//		else
//			return Arrays.asList(value.split(",")).stream()
//					.map(item -> new DCTStandard(DCTerms.conformsTo.getURI(), item, "", "", Arrays.asList(""), nodeID))
//					.collect(Collectors.toList());
//
//	}

	private DCATDistribution resourceToDCAT(Resource r, String datasetLandingPage, DCTLicenseDocument datasetLicense) {

		String accessURL = null, description = null, format = null, byteSize = null, downloadURL = null,
				mediaType = null, releaseDate = null, updateDate = null, rights = null, title = null;
		SPDXChecksum checksum = null;
		List<String> documentation = new ArrayList<String>();
		List<String> language = new ArrayList<String>();

		List<DCTStandard> linkedSchemas = new ArrayList<DCTStandard>();
		
		accessURL = downloadURL = StringUtils.isNotBlank(r.getUrl()) ? r.getUrl() : datasetLandingPage;
		description = r.getDescription();
		format = r.getFormat();
		byteSize = String.valueOf(r.getSize());
		checksum = new SPDXChecksum("http://spdx.org/rdf/terms#checksum", "checksumAlgorithm_sha1", r.getHash(),
				nodeID);
		// documentation = r.get ?
		// language = r.get ?
		// linkedSchemas = r.get ?
		mediaType = r.getMimetype();
		releaseDate = StringUtils.isNotBlank(r.getCreated()) ? CommonUtil.fixBadUTCDate(r.getCreated())
				: "1970-01-01T00:00:00Z";
		updateDate = StringUtils.isNotBlank(r.getLast_modified()) ? CommonUtil.fixBadUTCDate(r.getLast_modified())
				: "1970-01-01T00:00:00Z";
		// rights = r.get ?
		// status = r.get ?
		title = r.getName();

		return new DCATDistribution(nodeID, accessURL, description, format, datasetLicense, byteSize, checksum,
				documentation, downloadURL, language, linkedSchemas, mediaType, releaseDate, updateDate, rights, null,
				title);
	}

	@Override
	public DCATDataset getDataset(String datasetId) throws CKANException, MalformedURLException,
			ODMSCatalogueOfflineException, ODMSCatalogueNotFoundException, ODMSCatalogueForbiddenException {

		Client c = new Client(new Connection(node.getHost()), node.getAPIKey());
		Dataset dataset;
		try {
			dataset = c.getDataset(datasetId);

			DCATDataset mapped = null;
			if (dataset != null)
				mapped = datasetToDCAT(dataset, node);
			c = null;
			dataset = null;

			return mapped;

		} catch (CKANException e) {
			handleError(e);
			return null;
		}
	}

	/**
	 * Retrieves all datasets belonging to a federated CKAN node using CKAN API
	 * Client
	 * 
	 * @param none
	 * @throws CKANException
	 * @throws MalformedURLException
	 * @throws ODMSCatalogueNotFoundException
	 * @throws ODMSCatalogueOfflineException
	 * @throws ODMSCatalogueForbiddenException
	 * 
	 * @returns List<DCATDataset> results converted to a List of DCATDataset
	 * @throws An
	 *             Exception if the request fails
	 */
	@Override
	public List<DCATDataset> getAllDatasets() throws CKANException, MalformedURLException, ODMSCatalogueOfflineException,
			ODMSCatalogueNotFoundException, ODMSCatalogueForbiddenException {

		ArrayList<DCATDataset> dcatResults = new ArrayList<DCATDataset>();

		logger.info("-- CKAN Connector Request sent -- First synchronization ");
		Client c = new Client(new Connection(node.getHost()), node.getAPIKey());

		logger.info("\n-----------------------\n");
		logger.info("NODE - Dataset count: " + node.getDatasetCount());
		logger.info("START " + node.getDatasetStart());
		Dataset.SearchResults result;

		logger.info("\n\n-----------------------------------\n\n");

		int retryNum = 1;
		boolean retry = false;
		do {

			try {

				result = c.findDatasets(
						"metadata_modified:[* TO " + CommonUtil.formatDate(node.getRegisterDate()) + "]",
						Integer.toString(node.getDatasetStart()), "10000000", "metadata_modified asc");
				retry = false;
				logger.info("-- CKAN Connector Response - Result count:" + result.results.size());
				for (Dataset d : result.results) {
					dcatResults.add(datasetToDCAT(d, node));
				}

				logger.info("\n-----------------------\n");
				result = null;

			} catch (CKANException e) {
				e.printStackTrace();
				if (e.getMessage() != null && e.getMessage().contains("The ODMS host does not exist")) {
					throw new ODMSCatalogueNotFoundException(e.getMessage());
				} else {
					logger.info("Exception! Attempt n: " + retryNum);
					retry = true;
					retryNum++;
					if (retryNum == 5)
						handleError(e);
				}
			}

		} while (retry);

		return dcatResults;

	}

	/**
	 * Retrieves all recent activities on datasets of a node
	 *
	 * Makes an Hashmap where every key-value pair is a corrispondence between
	 * DCATDataset and related activity on it, starting from a passed date string.
	 * 
	 * @param startingDate
	 *            The string representing the starting date compliant to ISO 8601
	 *            standard
	 * @throws ParseException
	 * @throws MalformedURLException
	 * @throws ODMSCatalogueOfflineException
	 * @throws ODMSCatalogueForbiddenException
	 * @throws ODMSCatalogueNotFoundException
	 * @returns HashMap<String,String> a Map of ID,activity_type pairs
	 * @throws A
	 *             CKANException if the request fails
	 */
	@Override
	public ODMSSynchronizationResult getChangedDatasets(List<DCATDataset> oldDatasets, String startingDateString)
			throws ParseException, CKANException, MalformedURLException, ODMSCatalogueOfflineException,
			ODMSCatalogueNotFoundException, ODMSCatalogueForbiddenException {

		// The old datasets list is not used in CKAN Connector, The list of
		// Datasets IDs is used instead

		Client c = new Client(new Connection(node.getHost()), node.getAPIKey());

		ODMSSynchronizationResult syncrhoResult = new ODMSSynchronizationResult();

		/*
		 * FOR DELETED DATASETS, RETRIEVES THE CURRENT ID LIST ON THE NODE AND COMPARES
		 * IT WITH THE LOCAL ONE
		 * 
		 * If dataset was deleted, inserts a DCATDataset only with id and nodeID,
		 * because this dataset from node is not longer available, but its id e node id
		 * are needed in order to forward delete request to Metadata Cache Manager
		 * 
		 * 
		 */

		HashMap<String,ArrayList<String>> idMap=null;
		List<String> oldDatasetsID = new ArrayList<String>();
		List<String> newDatasetsNames = new ArrayList<String>();
		try {
			logger.info("Starting to retrieve present datasets of the node from cache");
			//oldDatasetsID = MetadataCacheManager.getAllDatasetsIDByODMSCatalogue(node.getId(), true);
			idMap = MetadataCacheManager.getCKANDatasetNamesIdentifiers(node.getId());
			oldDatasetsID=new ArrayList<String>(idMap.keySet());
		} catch (DatasetNotFoundException | IOException | SolrServerException e) {
			logger.info(e.getMessage());
		}

	try {
			newDatasetsNames = Arrays.asList(c.getAllDatasetsID());
			
		} catch (CKANException | MalformedURLException e) {
			e.printStackTrace();
			logger.info(e.getMessage());
			if (e.getClass().equals(CKANException.class))
				handleError((CKANException) e);
			else
				throw e;
		}

		
		//k -> identifier 
		//elements ->arrayList of name and other identifiers
		int deleted = 0;
		for(String k : idMap.keySet()) {
			List<String> names = idMap.get(k);
			boolean isPresent=false;
			for(String n:names) {
				if(newDatasetsNames.contains(n)) {
					isPresent=true;
					break;
				}
			}
			if(!isPresent) {
				//No match of identifiers in the new Array -> deleted
				/* -> No perché in questo modo non funzionano cancella gli RDF dato che il dataset non ha distribution -> 
				 * dobbiamo andare a prendere quelli della cache
				DCATDataset deletedDataset = new DCATDataset();
				deletedDataset.setNodeID(new Integer(node.getId()).toString());
				deletedDataset.setIdentifier(new DCATProperty(DCTerms.identifier, RDFS.Literal.getURI(), k));
				syncrhoResult.addToDeletedList(deletedDataset);
				deleted++;*/
				
				try {
					//In questo modo quando deve cancellare ha il dataset con tutte le info 
					syncrhoResult.addToDeletedList(MetadataCacheManager.getDatasetByIdentifier(Integer.parseInt(nodeID), k));
					deleted++;
				} catch (NumberFormatException | DatasetNotFoundException | IOException | SolrServerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
				
			}
			
		}
		logger.info("Deleted Packages: " + deleted);
		/*
		 * OLD -> NO
		 * 
		 * ImmutableSet<String> newSets = ImmutableSet.copyOf(newDatasetsIdentifiers);
		ImmutableSet<String> oldSets = ImmutableSet.copyOf(oldDatasetsIdentifiers);
		logger.info(" Start to compare the new and old dataset IDs lists: " + " New size: " + newSets.size()
				+ " - Old size: " + oldSets.size());
		SetView<String> diff = Sets.difference(oldSets, newSets);

		int deleted = 0;
		logger.info("Deleted Packages: " + diff.size());
		for (String identifier : diff) {
			DCATDataset deletedDataset = new DCATDataset();
			//deletedDataset.setId(id);
			deletedDataset.setNodeID(new Integer(node.getId()).toString());
			//This line is the cause of the nullpointerexception
			deletedDataset.setOtherIdentifier(Arrays.asList(new DCATProperty("dcat:altIdentifier", id)));
			deleted++;
			syncrhoResult.addToDeletedList(deletedDataset);
		}*/

		
		
		/**********************************************************************************************/
		/*
		 * FOR ADDED AND UPDATED DATASETS, RETRIEVES ALL DATASETS WITH METADATA CREATED
		 * AND MODIFIED AFTER THE LAST UPDATE DATE
		 */
		
		int changed = 0, added = 0, offset = 0;
		Dataset.SearchResults result = null;

		logger.info("Last update date from which to start:" + startingDateString);

		// Get the count of changed datasets, in order to calculate the number
		// of successive calls (datasets pages)
		int count = 0;
		count = c.findDatasets("metadata_created:[" + startingDateString + " TO *] OR metadata_modified:["
				+ startingDateString + " TO * ]", "0", "0", "metadata_created desc").count;

		logger.info("-- CKAN Connector Response - Result TOTAL count:" + count);

		while (offset < count) {

			boolean retry;
			int retryNum = 0;
			do {

				try {
					result = c.findDatasets(
							"metadata_created:[" + startingDateString + " TO *] OR metadata_modified:["
									+ startingDateString + " TO * ]",
							String.valueOf(offset), "1000", "metadata_created desc");
					retry = false;

				} catch (CKANException e) {
					e.printStackTrace();
					logger.info("Exception! Attempt n: " + retryNum);
					retry = true;
					retryNum++;
					if (retryNum == 5)
						throw new ODMSCatalogueOfflineException("The Node is currently OFFLINE");
				}
			} while (retry);

			logger.info("-- CKAN Connector Response - Result PARTIAL count:" + result.results.size());

			for (Dataset d : result.results) {

				if (oldDatasetsID.contains(d.getId())) {
					syncrhoResult.addToChangedList(datasetToDCAT(d, node));
					changed++;
				} else if (newDatasetsNames.contains(d.getName())) {
					syncrhoResult.addToAddedList(datasetToDCAT(d, node));
					added++;
				}

			}
			
			logger.info("NodeID: " + nodeID + " Changed " + syncrhoResult.getChangedDatasets().size());
			logger.info("NodeID: " + nodeID + " Added " + syncrhoResult.getAddedDatasets().size());
			logger.info("NodeID: " + nodeID + " Deleted " + syncrhoResult.getDeletedDatasets().size());
			logger.info(
					"NodeID: " + nodeID + " Expected new dataset count: " + (node.getDatasetCount() - deleted + added));
			offset += result.results.size();
			logger.info("\n\n Collected \n\n" + result.results.size() + "new/changed datasets\n");

		}

		c = null;
		result = null;
		System.gc();
		return syncrhoResult;

	}

	// @Override
	// public HashMap<DCATDataset, String> getChangedDatasets(List<DCATDataset>
	// oldDatasets, String startingDateString)
	// throws JSONException, ParseException, CKANException,
	// MalformedURLException, ODMSCatalogueOfflineException,
	// ODMSCatalogueNotFoundException, ODMSCatalogueForbiddenException {
	//
	// // The old datasets list is not used in CKAN Connector, due to the
	// // presence of appropriate
	// // API directly reporting the recently changed datasets (TEST CON
	// // PACKAGE SEARCH
	// // e differenza tra le liste di ID, in ogni caso la lista dei
	// // DCATDataset qui non serve)
	//
	// Client c = new Client(new Connection(node.getHost()), node.getAPIKey());
	// // HashMap<String, String> res;
	// // res = c.getChangedDatasetsID(startingDate);
	//
	// HashMap<DCATDataset, String> changedDatasets = new HashMap<DCATDataset,
	// String>();
	//
	// /*
	// * OLD APPROACH WITH RECENTLY CHANGED PACKAGE ACTIVITY LIST
	// *
	// * for(Entry<String,String> e: res.entrySet()){
	// *
	// *
	// * if ( e.getValue().equals("deleted package") ){ DCATDataset
	// * deletedDataset= new DCATDataset();
	// * deletedDataset.setId(e.getKey()+"-"+node.getId());
	// * deletedDataset.setNodeID(new Integer(node.getId()).toString());
	// * changedDatasets.put( deletedDataset, e.getValue() ); }else{
	// * DCATDataset dataset = getDataset(e.getKey()); if (dataset != null)
	// * changedDatasets.put( dataset,e.getValue() ); }
	// *
	// * }
	// */
	//
	// /*
	// * FOR DELETED DATASETS, RETRIEVES THE CURRENT ID LIST ON THE NODE AND
	// * COMPARES IT WITH THE LOCAL ONE
	// *
	// * If dataset was deleted, inserts in the hashmap a DCATDataset only
	// * with id and nodeID, // because this dataset from node is not longer
	// * available, but its id e node id are needed to // forward delete
	// * request to Metadata Cache Manager
	// *
	// *
	// */
	// logger.info("Starting to retrieve present datasets of the node from
	// cache");
	// List<String> oldDatasetsID = null;
	// String[] newDatasetsID = null;
	// try {
	//
	// oldDatasetsID =
	// MetadataCacheManager.getAllDatasetsIDByODMSNode(node.getId(), true);
	//// System.out.println("old size: "+oldDatasetsID.size());
	//// for(String a : oldDatasetsID){
	//// System.out.println(a);
	//// }
	//
	// } catch (DatasetNotFoundException | IOException | SolrServerException e)
	// {
	// logger.info(e.getMessage());
	// }
	//
	// try {
	// newDatasetsID = c.getAllDatasetsID();
	// } catch (SocketTimeoutException /* | ODMSCatalogueOfflineException */ |
	// CKANException | MalformedURLException e) {
	// e.printStackTrace();
	// logger.info(e.getMessage());
	// if (e.getClass().equals(CKANException.class))
	// handleError((CKANException) e);
	//
	// }
	//
	//// System.out.println("New dataset size: "+newDatasetsID.length);
	//
	// ImmutableSet<String> newSets = ImmutableSet.copyOf(newDatasetsID);
	// ImmutableSet<String> oldSets = ImmutableSet.copyOf(oldDatasetsID);
	// logger.info(" Start to compare the new and the old lists
	// "+newSets.size()+" "+oldSets.size());
	// SetView<String> diff1 = Sets.difference(oldSets, newSets);
	//// System.out.println("HERE__________________________________");
	//// for(int i=0; i< newDatasetsID.length; i++){
	//// System.out.println(i+": "+(newDatasetsID[i].equals("")?"Non
	// c'è":newDatasetsID[i]));
	//// }
	//// System.out.println("\n");
	// int deleted=0;
	// logger.info("Deleted Package " + diff1.size());
	// for (String d : diff1) {
	// //System.out.println("To be deleted dataset: "+d);
	// DCATDataset deletedDataset = new DCATDataset();
	// deletedDataset.setId(d);
	// deletedDataset.setNodeID(new Integer(node.getId()).toString());
	// DCATProperty dcat_altIdentifier = new DCATProperty();
	// dcat_altIdentifier.setLabel("dcat:altIdentifier");
	// dcat_altIdentifier.setValue(d);
	// deletedDataset.setDcat_altIdentifier(dcat_altIdentifier);
	// deleted++;
	// changedDatasets.put(deletedDataset, "deleted package");
	// }
	//
	// /**********************************************************************************************/
	// /*
	// * FOR ADDED AND UPDATED DATASETS, RETRIEVES ALL DATASETS WITH METADATA
	// * CREATED AND MODIFIED AFTER THE LAST UPDATE DATE
	// */
	//
	// int changed = 0;
	// int added =0;
	//// int elses =0;
	//
	// int offset = 0;
	// Dataset.SearchResults result = null;
	// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	// sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
	// GregorianCalendar startingDate = new GregorianCalendar();
	//// startingDate.setTimeInMillis(node.getLastUpdateDate().getTimeInMillis());
	// startingDate.setTime(sdf.parse(startingDateString));
	// logger.info("Last update date from which to start:" +
	// startingDateString);
	// // GETS THE COUNT OF THE MATCHING DATASETS
	// int count = 0;
	//
	// try {
	// count = c.findDatasets("metadata_created:[" + startingDateString + " TO
	// *] OR metadata_modified:["
	// + startingDateString + " TO * ]", "0", "0", "metadata_created
	// desc").count;
	// } catch (SocketTimeoutException e) {
	// e.printStackTrace();
	// logger.info("The node is currently OFFLINE");
	// throw new ODMSCatalogueOfflineException("The Node is currently OFFLINE");
	// }
	// logger.info("-- CKAN Connector Response - Result TOTAL count:" + count);
	//
	// while (offset < count) {
	//
	// boolean retry;
	// int retryNum = 0;
	// do {
	//
	// // Send the API request
	// try {
	// result = c.findDatasets(
	// "metadata_created:[" + startingDateString + " TO *] OR
	// metadata_modified:["
	// + startingDateString + " TO * ]",
	// new Integer(offset).toString(), "1000", "metadata_created desc");
	// retry = false;
	// } catch (SocketTimeoutException e) {
	// e.printStackTrace();
	// logger.info("Exception attempt n: " + retryNum);
	// retry = true;
	// retryNum++;
	// if (retryNum == 5)
	// throw new ODMSCatalogueOfflineException("The Node is currently OFFLINE");
	// }
	//
	// } while (retry);
	//
	//// System.out.println("\n\n-----------------------------------\n\n");
	// logger.info("-- CKAN Connector Response - Result PARTIAL count:" +
	// result.results.size());
	//
	// GregorianCalendar issuedDate = new
	// GregorianCalendar(TimeZone.getTimeZone("UTC"));
	// issuedDate.setLenient(false);
	// GregorianCalendar modifiedDate = new
	// GregorianCalendar(TimeZone.getTimeZone("UTC"));
	// modifiedDate.setLenient(false);
	//
	//
	// for (Dataset d : result.results) {
	// issuedDate.setTimeInMillis(sdf.parse(parseCKANDate(d.getMetadata_created())).getTime());
	// modifiedDate.setTimeInMillis(sdf.parse(parseCKANDate(d.getMetadata_modified())).getTime());
	//
	// if(oldDatasetsID.contains(d.getName())){
	//// System.out.print(" CHANGED:\n");
	// changedDatasets.put(datasetToDCAT(d, node), "changed package");
	// changed++;
	// }else if(newSets.contains(d.getName())){
	// changedDatasets.put(datasetToDCAT(d, node), "new package");
	// added++;
	// }
	//
	//// if (issuedDate.after(startingDate)) {
	////// System.out.println("New Package: "+issuedDate.after(startingDate));
	//// changedDatasets.put(datasetToDCAT(d, node), "new package");
	//// // logger.info(sdf.format(issuedDate.getTime()) + " >" +
	//// // sdf.format(startingDate.getTime()));
	//// added++;
	//// } else if (modifiedDate.after(startingDate)) {
	////// System.out.println("Changed Package:
	// "+modifiedDate.after(startingDate));
	//// changedDatasets.put(datasetToDCAT(d, node), "changed package");
	////
	//// // logger.info(sdf.format(modifiedDate.getTime()) + " >" +
	//// // sdf.format(startingDate.getTime()));
	//// }
	//
	// }
	//
	// logger.info("NodeID: "+nodeID+" Changed "+changed);
	// logger.info("NodeID: "+nodeID+" Added "+added);
	// logger.info("NodeID: "+nodeID+" Deleted "+deleted);
	// logger.info("NodeID: "+nodeID+" Expected new dataset count:
	// "+(node.getDatasetCount()-deleted+added));
	// offset += result.results.size();
	// logger.info("\n\n Collected \n\n" + result.results.size() + "new/changed
	// datasets\n");
	//
	// }
	//
	//
	// c = null;
	// result = null;
	// System.gc();
	// return changedDatasets;
	//
	// }

	public ODMSCatalogue getNode() {
		return node;
	}

	public void setNode(ODMSCatalogue node) {
		this.node = node;
	}

	@Override
	public int countSearchDatasets(HashMap<String, Object> searchParameters) throws Exception {

		ArrayList<DCATDataset> dcatResults = new ArrayList<DCATDataset>();

		String query = buildLiveQueryString(searchParameters);

		String sort = "";
		String rows = "";
		String start = "";

		if (searchParameters.containsKey("sort")) {
			sort = (String) searchParameters.get("sort");
			String tmp1[] = sort.split(",");
			if (DCATtoCKANmap.containsKey(tmp1[0])) {
				tmp1[0] = DCATtoCKANmap.get(tmp1[0]);
			}
			sort = tmp1[0] + " " + tmp1[1];
		}

		if (searchParameters.containsKey("rows"))
			rows = (String) searchParameters.get("rows");
		if (searchParameters.containsKey("start"))
			start = (String) searchParameters.get("start");

		logger.info("Live query: " + query);

		logger.info("-- CKAN Connector Request sent --" + "ROWS: " + rows);
		Client c = new Client(new Connection(node.getHost()), node.getAPIKey());
		logger.info(dcatResults.size());
		Dataset.SearchResults result;
		try {
			result = c.findDatasets(query, start, "1", sort);
			// if(isEurovoc){
			// searchParameters.put("euroVoc", isEurovoc);
			// }
			return result.count;
		} catch (CKANException e) {
			handleError(e);
			return 0;
		}
	}

	private static boolean checkIfJsonObject(String input) {

		try {
			JsonElement jelement = new JsonParser().parse(input);
			JsonObject jobject = jelement.getAsJsonObject();
			return true;
		} catch (Exception e) {
			logger.debug("Spatial string is not a valid GeoJson: " + e.getMessage());
			return false;
		}
	}

	private String buildLiveQueryString(HashMap<String, Object> searchParameters) {

		String query = "";
		String[] issued = {};
		String[] modified = {};
		boolean isFirst = true;
		String key;
		Object value;

		String defaultOperator = "AND";
		boolean isEurovoc = false;
		if (searchParameters.containsKey("euroVoc")) {
			isEurovoc = (boolean) searchParameters.remove("euroVoc");
			if (isEurovoc) {
				defaultOperator = "OR";
			}
		}

		if (searchParameters.containsKey("issued")) {
			issued = (String[]) searchParameters.remove("issued");
			query += DCATtoCKANmap.get("issued") + ":[" + issued[0] + " TO " + issued[1] + "] ";
			isFirst = false;
		}

		if (searchParameters.containsKey("modified")) {
			modified = (String[]) searchParameters.remove("modified");
			query += isFirst ? ""
					: " AND " + DCATtoCKANmap.get("modified") + ":[" + modified[0] + " TO " + modified[1] + "] ";
			isFirst = false;
		}

		// Creates query string as key-value pairs separated by OR
		for (Map.Entry<String, Object> e : searchParameters.entrySet()) {
			key = e.getKey();
			value = ((String) e.getValue()).replaceAll("\"", "").trim();

			if (key.equals("ALL")) {
				if (!((String) value).trim().equals("")) {

					// Add passed keywords to statistics DB
					StatisticsManager.storeKeywordsStatistic((String) value);

					// String valueString = "(" + ((String) value).replace(",",
					// " OR ") + ") ";
					// String valueString = "(\\\"" + ((String)
					// value).replace(",", "\\\" "+defaultOperator+" \\\"") +
					// "\\\") ";
					// String valueString = "(" + ((String) value).replace(",",
					// " "+defaultOperator+" ") + ") ";
					String valueString = "(" + ((String) value).replace(",", "+") + ") ";
					// String valueString = "(" + ((String) value).replace(",",
					// " ") + ") ";

					if (isFirst)
						query += valueString;
					else {
						// query += " OR " + valueString + " ";
						query += " AND " + valueString + " ";
					}
				} else
					query += "*:*";

				isFirst = false;
			} else if (key.equals("tags")) {

				if (isFirst) {
					// query += "tags" + ":" + "(" + ((String)
					// value).replace(",", " OR ") + ")";
					query += "tags" + ":" + "(" + ((String) value).replace(",", " AND ") + ")";
				} else {
					// query += " OR tags" + ":" + "(" + ((String)
					// value).replace(",", " OR ") + ")";
					query += " AND tags" + ":" + "(" + ((String) value).replace(",", " AND ") + ")";
				}
				isFirst = false;

			} else if (!key.equals("sort") && !key.equals("rows") && !key.equals("start")) {

				// Add passed keywords to statistics DB
				if (DCATtoCKANmap.containsKey(key)) {
					// System.out.println(key);
					if (isFirst) {
						// query += DCATtoCKANmap.get(key) + ":" + "(\\\"" +
						// ((String) value).trim().replace(",", "\\\" \\\"")
						// + "\\\")";
						query += DCATtoCKANmap.get(key) + ":" + "(\\\""
								+ ((String) value).trim().replace(",", "\\\" " + defaultOperator + " \\\"") + "\\\")";
						// query += DCATtoCKANmap.get(key) + ":" + "(" +
						// ((String) value).trim().replace(",", " OR ")
						// + ")";
					} else {
						// query += " AND " + DCATtoCKANmap.get(key) + ":" +
						// "(\\\""
						// + ((String) value).trim().replace(",", "\\\" \\\"") +
						// "\\\")";
						query += " AND " + DCATtoCKANmap.get(key) + ":" + "(\\\""
								+ ((String) value).trim().replace(",", "\\\" " + defaultOperator + " \\\"") + "\\\")";
						// query += " OR " + DCATtoCKANmap.get(key) + ":" + "("
						// + ((String) value).trim().replace(",", " OR ") + ")";

					}
				}

				isFirst = false;
			}
		}

		if (isEurovoc) {
			searchParameters.put("euroVoc", isEurovoc);
		}

		if (issued.length != 0) {
			searchParameters.put("issued", issued);
		}

		if (modified.length != 0) {
			searchParameters.put("modified", modified);
		}

		return query;

	}

	public void handleError(CKANException e)
			throws ODMSCatalogueNotFoundException, ODMSCatalogueForbiddenException, ODMSCatalogueOfflineException, CKANException {

		String message = e.getMessage();

		if (StringUtils.isBlank(message))
			message = e.getErrorMessages().get(0);

		if (StringUtils.isNotBlank(message)) {

			if (message.contains(" The ODMS host does not exist"))
				throw new ODMSCatalogueNotFoundException(e.getMessage());

			else if (message.contains(" The ODMS node is forbidden"))
				throw new ODMSCatalogueForbiddenException(e.getMessage());
			else if (message.contains(" The ODMS node is currently unreachable"))
				throw new ODMSCatalogueOfflineException(e.getMessage());
			else
				throw new CKANException(e.getMessage());

		} else {
			throw new CKANException("Unknown CKAN Exception");
		}
	}
}
