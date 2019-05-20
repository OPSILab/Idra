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
import it.eng.idra.beans.odms.ODMSCatalogue;
import it.eng.idra.beans.odms.ODMSCatalogueForbiddenException;
import it.eng.idra.beans.odms.ODMSCatalogueNotFoundException;
import it.eng.idra.beans.odms.ODMSCatalogueOfflineException;
import it.eng.idra.beans.odms.ODMSSynchronizationResult;
import it.eng.idra.beans.spod.SPODDataset;
import it.eng.idra.utils.CommonUtil;
import it.eng.idra.utils.GsonUtil;
import it.eng.idra.utils.GsonUtilException;
import it.eng.idra.utils.restclient.RestClient;
import it.eng.idra.utils.restclient.RestClientImpl;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.logging.log4j.*;
import org.ckan.*;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SPODConnector implements IODMSConnector {

	private String nodeID;
	private ODMSCatalogue node;
	private static Logger logger = LogManager.getLogger(SPODConnector.class);

	public SPODConnector(ODMSCatalogue node) {
		this.node = node;
		this.nodeID = String.valueOf(node.getId());
	}

	@Override
	public int countDatasets() throws MalformedURLException, ODMSCatalogueOfflineException,
			ODMSCatalogueNotFoundException, ODMSCatalogueForbiddenException {
		try {
			return getAllIds().size();
		}catch(Exception e) {
			e.printStackTrace();
			throw new ODMSCatalogueOfflineException(e.getMessage());
		}
	}

	@Override
	public List<DCATDataset> findDatasets(HashMap<String, Object> searchParameters) throws MalformedURLException, ODMSCatalogueNotFoundException, ODMSCatalogueForbiddenException, ODMSCatalogueOfflineException {

		ArrayList<DCATDataset> dcatResults = new ArrayList<DCATDataset>();

		return dcatResults;
	}

	@Override
	public DCATDataset getDataset(String datasetId) throws MalformedURLException,
			ODMSCatalogueOfflineException, ODMSCatalogueNotFoundException, ODMSCatalogueForbiddenException {

			try {
				return datasetToDCAT(getCKANDataset(datasetId), node);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ODMSCatalogueOfflineException(e.getMessage());
			}
	}

	@Override
	public List<DCATDataset> getAllDatasets() throws MalformedURLException, ODMSCatalogueOfflineException,
			ODMSCatalogueNotFoundException, ODMSCatalogueForbiddenException {

		ArrayList<DCATDataset> dcatResults = new ArrayList<DCATDataset>();

		try {
			List<String> ids = getAllIds();
		
			ExecutorService executor = Executors.newWorkStealingPool();

			List<Callable<List<DCATDataset>>> callables = new ArrayList<Callable<List<DCATDataset>>>();
			int threadPoolSize = 8;
			int workerSize = (int) Math.ceil((double) ids.size()/threadPoolSize);
			int threadNum = (int) Math.ceil((double) ids.size()/workerSize);
			
			for(int i=0; i<threadNum; i++) {
				int index = i;
				int beg=i*workerSize;
				int end= ((i+1)*workerSize)>=ids.size()? ids.size():((i+1)*workerSize);
				callables.add(()->getSubsetOfDataset(index,beg, end, ids.subList(beg, end)));
			}
			
			LocalDateTime a = LocalDateTime.now();
			logger.info("Start at: "+a.toString());
			try {
				executor.invokeAll(callables)
				    .stream()
				    .map(future -> {
				        try {
				            return future.get();
				        }
				        catch (Exception e) {
				            throw new IllegalStateException(e);
				        }
				    })
				    .forEach(dcatResults::addAll);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			LocalDateTime b = LocalDateTime.now();
			logger.info("Finished at: "+b.toString());
			logger.info("Difference: "+Duration.between(a,b).toMinutes());
			logger.info("Finished: "+dcatResults.size());
			return dcatResults;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ODMSCatalogueOfflineException(e.getMessage());
		}
	}

	@Override
	public ODMSSynchronizationResult getChangedDatasets(List<DCATDataset> oldDatasets, String startingDateString)
			throws ParseException, CKANException, MalformedURLException, ODMSCatalogueOfflineException,
			ODMSCatalogueNotFoundException, ODMSCatalogueForbiddenException {
		List<String> newDatasetsIds=null;
		
		try {
			newDatasetsIds = getAllIds();
		}catch(Exception e) {
			throw new ODMSCatalogueOfflineException(e.getMessage());
		}
		
		List<String> oldDatasetsIds = oldDatasets.stream().map(x->x.getIdentifier().getValue()).collect(Collectors.toList());
		ODMSSynchronizationResult syncrhoResult = new ODMSSynchronizationResult();


		ImmutableSet<String> newSets = ImmutableSet.copyOf(newDatasetsIds);
		ImmutableSet<String> oldSets = ImmutableSet.copyOf(oldDatasetsIds);

		int deleted = 0, added = 0, changed = 0;

		/// Find added datasets
		// difference(current,present)
		SetView<String> diff = Sets.difference(newSets, oldSets);
		logger.info("New Packages: " + diff.size());
		for (String newId : diff) {
			try {
				syncrhoResult.addToAddedList(datasetToDCAT(getCKANDataset(newId), node));
				added++;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Find removed datasets
		// difference(present,current)
		SetView<String> diff1 = Sets.difference(oldSets, newSets);
		logger.info("Deleted Packages: " + diff1.size());
		for (String id : diff1) {
			syncrhoResult.addToDeletedList(oldDatasets.stream().filter(x->x.getIdentifier().getValue().equals(id)).findFirst().get());
			deleted++;
		}

		// Find updated datasets
		// intersection(present,current)
		SetView<String> intersection = Sets.intersection(newSets, oldSets);
		logger.fatal("Common Packages: " + intersection.size());

		GregorianCalendar oldDate = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		oldDate.setLenient(false);
		GregorianCalendar newDate = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		oldDate.setLenient(false);

		SimpleDateFormat DCATDateF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);

		int exception = 0;
		for (String id : intersection) {
			try {
				DCATDataset tmp = datasetToDCAT(getCKANDataset(id), node);
				DCATDataset old = oldDatasets.stream().filter(x->x.getIdentifier().getValue().equals(id)).findFirst().get();
				oldDate.setTime(DCATDateF.parse(old.getUpdateDate().getValue()));
				newDate.setTime(DCATDateF.parse(tmp.getUpdateDate().getValue()));
				if (newDate.after(oldDate)) {
					syncrhoResult.addToChangedList(tmp);
					changed++;
				}
			} catch (Exception ex) {
				exception++;
				if (exception % 1000 == 0) {
					ex.printStackTrace();
				}
			}
		}
		logger.info("Changed " + syncrhoResult.getChangedDatasets().size());
		logger.info("Added " + syncrhoResult.getAddedDatasets().size());
		logger.info("Deleted " + syncrhoResult.getDeletedDatasets().size());
		logger.info("Expected new dataset count: " + (node.getDatasetCount() - deleted + added));

		return syncrhoResult;


	}

	@Override
	public int countSearchDatasets(HashMap<String, Object> searchParameters) throws Exception {

		return 0;
	}

	
	private List<DCATDataset> getSubsetOfDataset(int index,int beg, int end,List<String> ids){
		logger.info("Subset: "+index+" beg: "+beg+" end: "+end+" size: "+ids.size());
		return ids.stream().map(x -> {
			try {
				return datasetToDCAT(getCKANDataset(x), node);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}).filter(x-> x!=null).collect(Collectors.toList());
	}
	
	
	/*
	 * This method will return all of the ids of the catalogue /SpodCkanApi/api/2/rest/dataset
	 * */
	private List<String> getAllIds() throws Exception{
		RestClient client = new RestClientImpl();
		
		HttpResponse response = client.sendGetRequest(node.getHost()+(node.getHost().endsWith("/")?"":"/")+"SpodCkanApi/api/2/rest/dataset", new HashMap<String,String>());
		int status = client.getStatus(response);		
		if(status==200)
			return GsonUtil.json2Obj(client.getHttpResponseBody(response), GsonUtil.stringListType);
		else
			return new ArrayList<String>();
	}

	/*
	 * This method will return the dataset /SpodCkanApi/api/2/rest/dataset/{id}
	 * */
	
	private SPODDataset getCKANDataset(String id) throws Exception{
		RestClient client = new RestClientImpl();
		HttpResponse response = client.sendGetRequest(node.getHost()+(node.getHost().endsWith("/")?"":"/")+"SpodCkanApi/api/2/rest/dataset/"+id, new HashMap<String,String>());
		int status = client.getStatus(response);		
		if(status==200)
			return GsonUtil.json2Obj(client.getHttpResponseBody(response), GsonUtil.spodDatasetType);
		else
			return null;
	}
	
	
	public DCATDataset datasetToDCAT(Object dataset, ODMSCatalogue node) {

		SPODDataset d = (SPODDataset) dataset;
		DCATDataset mapped;
		// Properties to be mapped among different CKAN fallback fields

		if(!d.isIsopen()) {
			System.out.println(d.getName() +" Is not open");
			return null;
		}
		
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
			
//			if(StringUtils.isNotBlank(d.getCkan_url()))
//				landingPage = d.getCkan_url();
//			else 
			if(StringUtils.isNotBlank(d.getUrl()))
				landingPage = d.getUrl();
			else
				landingPage = nodeHost + (nodeHost.endsWith("/") ? "" : "/") + "opendata/" + d.getName();

			// Distributions
			List<Resource> resourceList = d.getResources();
			if (resourceList != null)
				for (Resource r : resourceList) {
					distributionList.add(resourceToDCAT(r, landingPage, license));
				}
		}
		
		if(d.getRelations() !=null)
			relatedResource = d.getRelations().stream().map(x -> x.getUrl()).collect(Collectors.toList());

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
