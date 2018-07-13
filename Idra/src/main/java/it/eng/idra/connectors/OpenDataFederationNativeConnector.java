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
import it.eng.idra.beans.dcat.SKOSConceptStatus;
import it.eng.idra.beans.dcat.SKOSConceptSubject;
import it.eng.idra.beans.dcat.SKOSConceptTheme;
import it.eng.idra.beans.dcat.SKOSPrefLabel;
import it.eng.idra.beans.dcat.SPDXChecksum;
import it.eng.idra.beans.dcat.VCardOrganization;
import it.eng.idra.beans.odms.ODMSCatalogue;
import it.eng.idra.beans.odms.ODMSCatalogueOfflineException;
import it.eng.idra.beans.odms.ODMSSynchronizationResult;
import it.eng.idra.utils.CommonUtil;
import it.eng.idra.utils.GsonUtil;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import javax.ws.rs.core.MediaType;

import org.apache.jena.iri.IRIFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.logging.log4j.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class OpenDataFederationNativeConnector implements IODMSConnector {

	private ODMSCatalogue node;
	private String nodeID;
	public static final MediaType JSON_TYPE = MediaType.APPLICATION_JSON_TYPE;
	private static Logger logger = LogManager.getLogger(OpenDataFederationNativeConnector.class);

	public OpenDataFederationNativeConnector(ODMSCatalogue node) {
		this.node = node;
		this.nodeID = String.valueOf(node.getId());
	}

	@Override
	public List<DCATDataset> findDatasets(HashMap<String, Object> searchParameters) throws Exception {

		ArrayList<DCATDataset> dcatResults = new ArrayList<DCATDataset>();
		String query = "", sort = "", rows = "", offset = "";

		if (searchParameters.containsKey("sort"))
			sort = (String) searchParameters.get("sort");
		if (searchParameters.containsKey("rows"))
			rows = (String) searchParameters.get("rows");
		if (searchParameters.containsKey("start"))
			offset = (String) searchParameters.get("start");

		query = buildQueryNativeString(searchParameters);

		logger.info("-- NATIVE Connector Request sent --" + "ROWS: " + rows);

		NativeClient c = new NativeClient(node);

		JSONObject resultJSON = c.findDatasets(query, sort, rows, offset);
		JSONArray results = resultJSON.optJSONArray("results");
		logger.info("-- NATIVE Connector Response - Result count:" + resultJSON.optInt("count"));

		if (results != null)
			for (int i = 0; i < results.length(); i++) {
				JSONObject obj = results.getJSONObject(i);
				dcatResults.add(datasetToDCAT(obj, node));
				obj = null;
			}

		c = null;
		System.gc();
		return dcatResults;

	}

	@Override
	public int countDatasets() throws Exception {

		NativeClient c = new NativeClient(node);

		JSONArray datasetsID = c.getAllDatasetsID();
		int count = datasetsID.length();
		datasetsID = null;
		c = null;

		if (count == 0)
			throw new ODMSCatalogueOfflineException(" The ODMS node is currently unreachable");

		return count;

	}

	@Override
	public DCATDataset datasetToDCAT(Object d, ODMSCatalogue node) throws Exception {

		JSONObject dataset = (JSONObject) d;

		String title = null, description = null, accessRights = null, frequency = null, landingPage = null,
				releaseDate = null, updateDate = null, identifier = null, type = null, version = null;

		List<DCTStandard> conformsTo = null;
		FOAFAgent publisher = null, rightsHolder = null, creator = null;
		List<VCardOrganization> contactPointList = new ArrayList<VCardOrganization>();
		DCTPeriodOfTime temporalCoverage = null;
		DCTLocation spatialCoverage = null;
		List<SKOSConceptTheme> themeList = null;
		List<SKOSConceptSubject> subjectList = null;

		List<String> keywords = new ArrayList<String>(), documentation = new ArrayList<String>(),
				hasVersion = new ArrayList<String>(), isVersionOf = new ArrayList<String>(),
				language = new ArrayList<String>(), provenance = new ArrayList<String>(),
				otherIdentifier = new ArrayList<String>(), sample = new ArrayList<String>(),
				source = new ArrayList<String>(), versionNotes = new ArrayList<String>();

		List<DCATDistribution> distributionList = new ArrayList<DCATDistribution>();

		/*
		 * DON'T TOUCH - GetString and not OptString for identifier, because it's
		 * mandatory and eventually throws an exception
		 */
		identifier = dataset.getString("id");

		title = dataset.optString("title");
		description = dataset.optString("description");

		themeList = deserializeConcept(dataset, "theme", DCAT.theme, nodeID,SKOSConceptTheme.class);

		if (dataset.has("publisher"))
			publisher = deserializeFOAFAgent(dataset, "publisher", DCTerms.publisher, nodeID);

		contactPointList = deserializeContactPoint(dataset, DCAT.contactPoint, nodeID);

		if (dataset.has("keywords"))
			keywords = GsonUtil.json2Obj(dataset.getJSONArray("keywords").toString(), GsonUtil.stringListType);

		accessRights = dataset.optString("accessRights");
		conformsTo = deserializeStandard(dataset, "conformsTo", nodeID);

		if (dataset.has("documentation"))
			documentation = GsonUtil.json2Obj(dataset.getJSONArray("documentation").toString(),
					GsonUtil.stringListType);

		if (dataset.has("frequency")) {
			frequency = dataset.optString("frequency");
			if (!IRIFactory.iriImplementation().create(frequency).hasViolation(false))
				frequency = CommonUtil.extractFrequencyFromURI(frequency);
		}

		if (dataset.has("hasVersion"))
			hasVersion = GsonUtil.json2Obj(dataset.getJSONArray("hasVersion").toString(), GsonUtil.stringListType);

		if (dataset.has("isVersionOf"))
			isVersionOf = GsonUtil.json2Obj(dataset.getJSONArray("isVersionOf").toString(), GsonUtil.stringListType);

		landingPage = dataset.optString("landingPage");

		if (dataset.has("language"))
			language = GsonUtil.json2Obj(dataset.getJSONArray("language").toString(), GsonUtil.stringListType);

		if (dataset.has("provenance"))
			provenance = GsonUtil.json2Obj(dataset.getJSONArray("provenance").toString(), GsonUtil.stringListType);

		if (dataset.has("releaseDate"))
			releaseDate = CommonUtil.fixBadUTCDate(dataset.getString("releaseDate"));

		if (dataset.has("updateDate"))
			updateDate = CommonUtil.fixBadUTCDate(dataset.getString("updateDate"));

		if (dataset.has("otherIdentifier"))
			otherIdentifier = GsonUtil.json2Obj(dataset.getJSONArray("otherIdentifier").toString(),
					GsonUtil.stringListType);

		if (dataset.has("sample"))
			sample = GsonUtil.json2Obj(dataset.getJSONArray("sample").toString(), GsonUtil.stringListType);

		if (dataset.has("source"))
			source = GsonUtil.json2Obj(dataset.getJSONArray("source").toString(), GsonUtil.stringListType);

		if (dataset.has("temporal")) {
			temporalCoverage = deserializeTemporal(dataset, nodeID);
		}

		type = dataset.optString("type");
		version = dataset.optString("version");

		if (dataset.has("versionNotes"))
			versionNotes = GsonUtil.json2Obj(dataset.getJSONArray("versionNotes").toString(), GsonUtil.stringListType);

		if (dataset.has("rightsHolder"))
			rightsHolder = deserializeFOAFAgent(dataset, "rightsHolder", DCTerms.rightsHolder, nodeID);

		if (dataset.has("creator"))
			creator = deserializeFOAFAgent(dataset, "creator", DCTerms.creator, nodeID);

		subjectList = deserializeConcept(dataset, "subject", DCTerms.subject, nodeID,SKOSConceptSubject.class);

		spatialCoverage = deserializeSpatial(dataset, nodeID);

		if (dataset.has("distributions")) {
			JSONArray distrArray = dataset.getJSONArray("distributions");
			for (int i = 0; i < distrArray.length(); i++) {
				try {
					distributionList.add(distributionToDCAT(distrArray.getJSONObject(i), nodeID));
				} catch (Exception e) {
					logger.info(
							"There was an error while deserializing a Distribution: " + e.getMessage() + " - SKIPPED");
				}
			}
		}

		return new DCATDataset(nodeID, title, description, distributionList, themeList, publisher, contactPointList,
				keywords, accessRights, conformsTo, documentation, frequency, hasVersion, isVersionOf, landingPage,
				language, provenance, releaseDate, updateDate, identifier, otherIdentifier, sample, source,
				spatialCoverage, temporalCoverage, type, version, versionNotes, rightsHolder, creator, subjectList);

	}

	/**
	 * @param dataset
	 * @param nodeID
	 */
	protected DCTPeriodOfTime deserializeTemporal(JSONObject dataset, String nodeID) {

		try {
			JSONObject temporal = dataset.getJSONObject("temporal");
			return new DCTPeriodOfTime(temporal.optString("uri"), temporal.optString("startDate"),
					temporal.optString("endDate"), nodeID);
		} catch (JSONException ignore) {
			logger.info("Temporal object not valid! - Skipped");
		}
		return null;
	}

	/**
	 * @param dataset
	 * @param fieldName
	 * @param property
	 * @param nodeID
	 * @return FOAFAgent
	 */
	protected FOAFAgent deserializeFOAFAgent(JSONObject dataset, String fieldName, Property property, String nodeID) {

		try {
			JSONObject obj = dataset.getJSONObject(fieldName);
			return new FOAFAgent(property.getURI(), obj.optString("resourceUri"), obj.optString("name"),
					obj.optString("mbox"), obj.optString("homepage"), obj.optString("type"),
					obj.optString("identifier"), nodeID);
		} catch (JSONException ignore) {
			logger.info("Agent object not valid! - Skipped");
		}
		return null;
	}

	/**
	 * @param obj
	 * @param fieldName
	 * @param property
	 * @param nodeID
	 * @return List<SKOSConcept>
	 * @throws JSONException
	 */
	
	protected <T extends SKOSConcept> List<T> deserializeConcept(JSONObject obj, String fieldName, Property property, String nodeID,Class<T> type)
			throws JSONException {

		List<T> result = new ArrayList<T>();

		JSONArray conceptArray = obj.optJSONArray(fieldName);
		if (conceptArray != null) {
			for (int i = 0; i < conceptArray.length(); i++) {
				JSONObject themeObj = conceptArray.optJSONObject(i);

				if (themeObj != null) {

					List<SKOSPrefLabel> prefLabelList = new ArrayList<SKOSPrefLabel>();
					JSONArray labelArray = themeObj.optJSONArray("prefLabel");

					if (labelArray != null) {
						for (int j = 0; j < labelArray.length(); j++) {
							JSONObject labelObj = labelArray.optJSONObject(j);
							prefLabelList.add(new SKOSPrefLabel(labelObj.optString("language"),
									labelObj.optString("value"), nodeID));
						}
					}
					
					try {
						result.add(type.getDeclaredConstructor(SKOSConcept.class).newInstance(new SKOSConcept(property.getURI(), themeObj.optString("resourceUri"), prefLabelList,
								nodeID)));
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException | NoSuchMethodException | SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		return result;
	}

	/**
	 * @param dataset
	 * @param property
	 * @param nodeID
	 * @return List<VCardOrganization>
	 * @throws JSONException
	 */
	protected List<VCardOrganization> deserializeContactPoint(JSONObject dataset, Property property, String nodeID)
			throws JSONException {

		List<VCardOrganization> contactPList = new ArrayList<VCardOrganization>();

		JSONArray contactArray = dataset.optJSONArray("contactPoint");
		if (contactArray != null) {
			for (int i = 0; i < contactArray.length(); i++) {
				JSONObject contactObj = contactArray.optJSONObject(i);
				if (contactObj != null)
					contactPList.add(new VCardOrganization(property.getURI(), contactObj.optString("resourceUri"),
							contactObj.optString("fn"), contactObj.optString("hasEmail"),
							contactObj.optString("hasURL"), contactObj.optString("hasTelephoneValue"),
							contactObj.optString("hasTelephoneType"), nodeID));
			}
		}

		return contactPList;
	}

	/**
	 * @param obj
	 * @param fieldName
	 * @param nodeID
	 * @return List<DCTStandard>
	 * @throws JSONException
	 */
	protected List<DCTStandard> deserializeStandard(JSONObject obj, String fieldName, String nodeID)
			throws JSONException {

		List<DCTStandard> standardList = new ArrayList<DCTStandard>();

		JSONArray standardArray = obj.optJSONArray(fieldName);
		if (standardArray != null) {
			for (int i = 0; i < standardArray.length(); i++) {
				JSONObject standardObj = standardArray.optJSONObject(i);

				if (standardObj != null) {
					List<String> referenceDocumentation = new ArrayList<String>();

					JSONArray referenceArray = standardObj.optJSONArray("prefLabel");
					if (referenceArray != null)
						for (int j = 0; j < referenceArray.length(); j++)
							referenceDocumentation.add(referenceArray.optString(j));

					standardList.add(new DCTStandard(standardObj.optString("uri"), standardObj.optString("identifier"),
							standardObj.optString("title"), standardObj.optString("description"),
							referenceDocumentation, nodeID));
				}
			}
		}

		return standardList;
	}

	/**
	 * @param obj
	 * @param nodeID
	 * @return DCTLicenseDocument
	 */
	protected DCTLicenseDocument deserializeLicense(JSONObject obj, String nodeID) {

		try {
			JSONObject licenseObj = obj.getJSONObject("license");
			return new DCTLicenseDocument(licenseObj.optString("uri"), licenseObj.optString("name"),
					licenseObj.optString("type"), licenseObj.optString("versionInfo"), nodeID);

		} catch (JSONException ignore) {
			logger.info("License object not valid! - Skipped");
		}

		return null;

	}

	/**
	 * @param dataset
	 * @param nodeID
	 * @return DCTLocation
	 */
	protected DCTLocation deserializeSpatial(JSONObject dataset, String nodeID) {

		try {
			JSONObject obj = dataset.getJSONObject("spatialCoverage");
			return new DCTLocation(obj.optString("uri"), obj.optString("geographicalIdentifier"),
					obj.optString("geographicalName"), obj.optString("geometry"), nodeID);
		} catch (JSONException ignore) {
			logger.info("Spatial object not valid! - Skipped");
		}
		return null;
	}

	/**
	 * @param obj
	 * @param nodeID
	 * @return DCATDistribution
	 * @throws Exception
	 */
	protected DCATDistribution distributionToDCAT(JSONObject obj, String nodeID) throws Exception {

		String accessURL = null, description = null, format = null, byteSize = null, downloadURL = null,
				mediaType = null, releaseDate = null, updateDate = null, rights = null, title = null;
		SPDXChecksum checksum = null;
		DCTLicenseDocument license = null;
		List<String> documentation = new ArrayList<String>(), language = new ArrayList<String>();
		List<DCTStandard> linkedSchemas = null;
		SKOSConceptStatus status = null;

		title = obj.optString("title");
		accessURL = obj.getString("accessURL");
		description = obj.optString("description");
		mediaType = obj.optString("mediaType");
		format = obj.optString("format");
		byteSize = obj.optString("byteSize");

		if (obj.has("documentation"))
			documentation = GsonUtil.json2Obj(obj.getJSONArray("documentation").toString(), GsonUtil.stringListType);

		downloadURL = obj.optString("downloadURL");

		if (obj.has("language"))
			language = GsonUtil.json2Obj(obj.getJSONArray("language").toString(), GsonUtil.stringListType);

		license = deserializeLicense(obj, nodeID);

		linkedSchemas = deserializeStandard(obj, "linkedSchemas", nodeID);

		if (obj.has("releaseDate"))
			releaseDate = CommonUtil.fixBadUTCDate(obj.getString("releaseDate"));

		if (obj.has("updateDate"))
			updateDate = CommonUtil.fixBadUTCDate(obj.getString("updateDate"));

		rights = obj.optString("rights");
		try {
			status = deserializeConcept(obj, "status",
					ResourceFactory.createProperty("http://www.w3.org/ns/adms#status"), nodeID,SKOSConceptStatus.class).get(0);
		} catch (IndexOutOfBoundsException e) {
		}

		return new DCATDistribution(nodeID, accessURL, description, format, license, byteSize, checksum, documentation,
				downloadURL, language, linkedSchemas, mediaType, releaseDate, updateDate, rights, status, title);

	}

	@Override
	public DCATDataset getDataset(String datasetId) throws Exception {

		NativeClient c = new NativeClient(node);
		return datasetToDCAT(c.getDataset(datasetId), node);

	}

	@Override
	public List<DCATDataset> getAllDatasets() throws Exception {

		ArrayList<DCATDataset> dcatDatasets = new ArrayList<DCATDataset>();
		/*
		 * HashMap<String, Object> emptyParam = new HashMap <String,Object>();
		 * emptyParam.put("ALL","*:*"); emptyParam.put("rows", "1000000"); return
		 * findDatasets(emptyParam);
		 */
		NativeClient client = new NativeClient(node);

		// DA COMPLETARE E MODIFICARE CON GLI OPPORTUNI PARAMETRI OFFSET E LIMIT
		JSONArray datasetsArray = client.getAllDatasets(0, 0);
		for (int i = 0; i < datasetsArray.length(); i++) {
			try {
				JSONObject dataset = datasetsArray.getJSONObject(i);
				// dcatDatasets.add(getDataset(o.getString("identifier")));
				dcatDatasets.add(datasetToDCAT(dataset, node));
				dataset = null;

			} catch (Exception e) {
				logger.info(
						"There was an error: " + e.getMessage() + " while deserializing Dataset - " + i + " - SKIPPED");
			}
		}

		datasetsArray = null;
		client = null;
		System.gc();

		return dcatDatasets;
	}

	@Override
	public ODMSSynchronizationResult getChangedDatasets(List<DCATDataset> oldDatasets, String startingDateString)
			throws Exception {

		ArrayList<DCATDataset> newDatasets = (ArrayList<DCATDataset>) getAllDatasets();
		ODMSSynchronizationResult syncrhoResult = new ODMSSynchronizationResult();

		ImmutableSet<DCATDataset> newSets = ImmutableSet.copyOf(newDatasets);
		ImmutableSet<DCATDataset> oldSets = ImmutableSet.copyOf(oldDatasets);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		GregorianCalendar startingDate = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		startingDate.setTime(sdf.parse(startingDateString));

		int deleted = 0;
		int added = 0;
		int changed = 0;

		/*
		 * Find added datasets difference(current,present)
		 */
		SetView<DCATDataset> diff = Sets.difference(newSets, oldSets);
		logger.info("New Package " + diff.size());
		for (DCATDataset d : diff) {
			syncrhoResult.addToAddedList(d);
			added++;
		}

		/*
		 * Find removed datasets difference(present,current)
		 */
		SetView<DCATDataset> diff1 = Sets.difference(oldSets, newSets);
		logger.info("Deleted Package " + diff1.size());
		for (DCATDataset d : diff1) {
			syncrhoResult.addToDeletedList(d);
			deleted++;
		}

		/*
		 * Find updated datasets intersection(present,current)
		 */

		SetView<DCATDataset> intersection = Sets.intersection(newSets, oldSets);
		logger.fatal("Intersection Package " + intersection.size());

		GregorianCalendar oldDate = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		oldDate.setLenient(false);
		GregorianCalendar newDate = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		oldDate.setLenient(false);
		SimpleDateFormat socrataDateF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

		SimpleDateFormat DCATDateF = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);

		int exception = 0;
		for (DCATDataset d : intersection) {
			try {
				int oldIndex = oldDatasets.indexOf(d);
				int newIndex = newDatasets.indexOf(d);

				oldDate.setTime(DCATDateF.parse(oldDatasets.get(oldIndex).getUpdateDate().getValue()));
				newDate.setTime(socrataDateF.parse(newDatasets.get(newIndex).getUpdateDate().getValue()));

				if (newDate.after(oldDate)) {
					// result.put(d, "changed package");
					syncrhoResult.addToChangedList(d);
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

	// @Override
	// public HashMap<DCATDataset, String> getChangedDatasets(List<DCATDataset>
	// oldDatasets, String startingDateString)
	// throws Exception {
	//
	// return null;
	// }

	@Override
	public int countSearchDatasets(HashMap<String, Object> searchParameters) throws Exception {

		ArrayList<DCATDataset> dcatResults = new ArrayList<DCATDataset>();
		String query = "";
		String sort = "";
		String rows = "";
		String offset = "";

		if (searchParameters.containsKey("sort"))
			sort = (String) searchParameters.get("sort");
		if (searchParameters.containsKey("rows"))
			rows = (String) searchParameters.get("rows");
		if (searchParameters.containsKey("start"))
			offset = (String) searchParameters.get("start");

		logger.info("-- NATIVE Connector Request sent --" + "ROWS: " + rows);

		query = buildQueryNativeString(searchParameters);

		NativeClient c = new NativeClient(node);

		JSONObject resultJSON = c.findDatasets(query, sort, rows, offset);
		int count = resultJSON.getInt("count");
		logger.info("-- NATIVE Connector Response - Result count:" + resultJSON.optInt("count"));

		// if (results != null)
		// for (int i = 0; i < results.length(); i++) {
		// JSONObject obj = results.getJSONObject(i);
		// dcatResults.add(datasetToDCAT(obj, node));
		// obj = null;
		// }

		c = null;
		return count;

	}

	private String buildQueryNativeString(HashMap<String, Object> searchParameters) {
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
			// query += (query.equals("")?"":" "+defaultOperator+" ")+ "issued"+":[" +
			// issued[0] + " TO " + issued[1] + "] ";
			isFirst = false;
		}

		if (searchParameters.containsKey("modified")) {
			modified = (String[]) searchParameters.remove("modified");
			// query += (query.equals("")?"":" "+defaultOperator+" ")+"modified"+":[" +
			// modified[0] + " TO " + modified[1] + "] ";
			isFirst = false;
		}

		// Creates query string as key-value pairs separated by OR
		for (Map.Entry<String, Object> e : searchParameters.entrySet()) {
			key = e.getKey();
			// System.out.println(key);
			value = ((String) e.getValue()).replaceAll("\"", "").trim();
			if (key.equals("ALL")) {
				if (!((String) value).trim().equals("")) {
					String tmp = ((String) value).replaceAll(",", " " + defaultOperator + " ");
					if (isFirst)
						query += tmp;
					else
						query += " " + defaultOperator + " " + tmp;
				} else
					query += "";

				isFirst = false;
			} else if (key.equals("tags")) {

				if (isFirst)
					query += "tags" + ":" + "(" + ((String) value).replace(",", " " + defaultOperator + " ") + ")";
				else
					query += " OR tags" + ":" + "(" + ((String) value).replace(",", " " + defaultOperator + " ") + ")";
				isFirst = false;

			} else if (!key.equals("sort") && !key.equals("rows") && !key.equals("start")) {

				String tmp = ((String) value).replaceAll(",", " " + defaultOperator + " ");

				if (isFirst)
					query += key + ":( " + tmp + " )";
				else
					query += " " + defaultOperator + " " + key + ":( " + tmp + " )";

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

		System.out.println(query);

		return query;
	}

}
