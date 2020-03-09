/*******************************************************************************
 * Idra - Open Data Federation Platform
 *  Copyright (C) 2020 Engineering Ingegneria Informatica S.p.A.
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

import it.eng.idra.beans.IdraProperty;
import it.eng.idra.beans.dcat.DCATDataset;
import it.eng.idra.beans.dcat.DCATDistribution;
import it.eng.idra.beans.dcat.DCTLicenseDocument;
import it.eng.idra.beans.dcat.DCTStandard;
import it.eng.idra.beans.dcat.FOAFAgent;
import it.eng.idra.beans.dcat.SKOSConcept;
import it.eng.idra.beans.dcat.SKOSConceptStatus;
import it.eng.idra.beans.dcat.SKOSConceptTheme;
import it.eng.idra.beans.dcat.SKOSPrefLabel;
import it.eng.idra.beans.dcat.SPDXChecksum;
import it.eng.idra.beans.dcat.VCardOrganization;
import it.eng.idra.beans.odms.ODMSCatalogue;
import it.eng.idra.beans.odms.ODMSCatalogueForbiddenException;
import it.eng.idra.beans.odms.ODMSCatalogueNotFoundException;
import it.eng.idra.beans.odms.ODMSCatalogueOfflineException;
import it.eng.idra.beans.odms.ODMSSynchronizationResult;
import it.eng.idra.utils.CommonUtil;
import it.eng.idra.utils.PropertyManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.logging.log4j.*;
import org.ckan.CKANException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.*;
import com.google.common.collect.Sets.SetView;

public class DkanConnector implements IODMSConnector {

	private ODMSCatalogue node;
	private String nodeID;
	private JSONArray datasetsArray;
	public static final MediaType JSON_TYPE = MediaType.APPLICATION_JSON_TYPE;
	private static Logger logger = LogManager.getLogger(DkanConnector.class);

	public DkanConnector(ODMSCatalogue node) {
		this.node = node;
		this.nodeID = String.valueOf(node.getId());
	}

	private JSONArray getJSONDatasets() throws JSONException, URISyntaxException, ODMSCatalogueOfflineException,
			ODMSCatalogueForbiddenException, ODMSCatalogueNotFoundException, IOException {

		logger.info("-- DKAN Connector Request sent -- " + node.getHost());

		String returned_json = sendGetRequest1(node.getHost() + "/data.json");

		if (!returned_json.startsWith("{")) {
			if (returned_json.matches(".*The requested URL could not be retrieved.*")
					|| returned_json.matches(".*does not exist.*"))
				throw new ODMSCatalogueNotFoundException(" The ODMS host does not exist");
			else if (returned_json.contains("403"))
				throw new ODMSCatalogueForbiddenException(" The ODMS node is forbidden");
			else
				throw new ODMSCatalogueOfflineException(" The ODMS node is currently unreachable");
		}

		JSONObject jsonObject = new JSONObject(returned_json);
		JSONArray jsonArray = jsonObject.getJSONArray("dataset");
		logger.info("-- DKAN Connector Response - Result count:" + jsonArray.length());

		return jsonArray;
	}

	/**
	 * Performs mapping from DKAN JSON Dataset object to DCATDataset object
	 *
	 *
	 * @param node
	 *            Node which dataset belongs to
	 * @param d
	 *            DKAN Dataset to be mapped
	 * @throws ParseException
	 *             JSONException
	 * @returns DCATDataset resulting mapped object
	 * @throws An
	 *             Exception if the request fails
	 */
	@Override
	public DCATDataset datasetToDCAT(Object d, ODMSCatalogue node) throws JSONException, ParseException {

		JSONObject dataset = (JSONObject) d;

		// Properties to be mapped to DKAN metadata
		String identifier = null;
		String description = null, issued = null, modified = null, title = null, landingPage = null;
		List<VCardOrganization> contactPointList = null;
		FOAFAgent publisher = null;
		List<String> keywords = new ArrayList<String>();
		DCTLicenseDocument license = null;
		ArrayList<DCATDistribution> distributionList = new ArrayList<DCATDistribution>();
		List<SKOSConceptTheme> themeList = null;
		/*
		 * DON'T TOUCH - GetString and not OptString for identifier, because it's
		 * mandatory and eventually throws an exception
		 */
		identifier = dataset.getString("identifier");

		landingPage = dataset.optString("landingPage");
		description = dataset.optString("description");

		try {
			issued = CommonUtil.fromLocalToUtcDate(dataset.optString("issued"), null);
		} catch (IllegalArgumentException skip) {
		}

		try {
			modified = CommonUtil.fromLocalToUtcDate(dataset.optString("modified"), null);
		} catch (IllegalArgumentException skip) {
		}

		if (dataset.has("keyword")) {
			JSONArray keywordArray = dataset.getJSONArray("keyword");
			for (int i = 0; i < keywordArray.length(); i++) {
				keywords.add(keywordArray.getString(i));
			}
		}

		contactPointList = deserializeContactPoint(dataset);
		title = dataset.optString("title");

		if (dataset.has("publisher"))
			publisher = deserializeFOAFAgent(dataset, "publisher", DCTerms.publisher, nodeID);

		if (dataset.has("license"))
			license = deserializeLicense(dataset, nodeID);

		themeList = deserializeConcept(dataset, "theme", DCAT.theme, nodeID,SKOSConceptTheme.class);

		if (dataset.has("distribution")) {
			JSONArray distrArray = dataset.getJSONArray("distribution");
			for (int i = 0; i < distrArray.length(); i++) {
				try {
					distributionList.add(distributionToDCAT(distrArray.getJSONObject(i), license, nodeID));
				} catch (Exception e) {
					logger.info(
							"There was an error while deserializing a Distribution: " + e.getMessage() + " - SKIPPED");
				}
			}
		}

		return new DCATDataset(nodeID,identifier, title, description, distributionList, themeList, publisher, contactPointList,
				keywords, null, null, null, null, null, null, landingPage, null, null, issued, modified,
				null, null, null, null, null, null, null, null, null, null, null,null);
	}

	/**
	 * @param obj
	 * @param nodeID
	 * @param DCTLicenseDocument
	 *            license
	 * @return DCATDistribution
	 * @throws Exception
	 */
	protected DCATDistribution distributionToDCAT(JSONObject obj, DCTLicenseDocument license, String nodeID)
			throws Exception {

		String accessURL = null, description = null, format = null, byteSize = null, downloadURL = null,
				mediaType = null, releaseDate = null, updateDate = null, rights = null, title = null;
		SPDXChecksum checksum = null;
		List<String> documentation = new ArrayList<String>(), language = new ArrayList<String>();
		List<DCTStandard> linkedSchemas = null;
		SKOSConceptStatus status = null;

		mediaType = obj.optString("mediaType");
//		accessURL = downloadURL = obj.getString("downloadURL");
		if(obj.has("format")) {
			format = obj.getString("format");
		}

		if(obj.has("title")) {
			title = obj.getString("title");
		}
		
		if(obj.has("downloadURL")) {
			accessURL = downloadURL = obj.getString("downloadURL");
			if(obj.has("accessURL")) {
				accessURL = obj.getString("accessURL");
			}
		}else if(obj.has("accessURL")) {
			accessURL = downloadURL = obj.getString("accessURL");
		}
		
		return new DCATDistribution(nodeID, accessURL, description, format, license, byteSize, checksum, documentation,
				downloadURL, language, linkedSchemas, mediaType, releaseDate, updateDate, rights, status, title);

	}

	/**
	 * @param dataset
	 * @return List<VCardOrganization>
	 */
	protected List<VCardOrganization> deserializeContactPoint(JSONObject dataset) {

		List<VCardOrganization> result = new ArrayList<VCardOrganization>();

		if (dataset.has("contactPoint")) {

			JSONObject contactObj = dataset.optJSONObject("contactPoint");
			if (contactObj != null)
				result.add(new VCardOrganization(DCAT.contactPoint.getURI(), null, contactObj.optString("fn"),
						contactObj.optString("hasEmail"), contactObj.optString("hasURL"),
						contactObj.optString("hasTelephoneValue"), contactObj.optString("hasTelephoneType"), nodeID));
		}
		return result;
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
	 * @param nodeID
	 * @return DCTLicenseDocument
	 */
	protected DCTLicenseDocument deserializeLicense(JSONObject obj, String nodeID) {

		try {

			return new DCTLicenseDocument(obj.getString("license"), null, null, null, nodeID);

		} catch (JSONException ignore) {
			logger.info("License not valid! - Skipped");
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

				String label = conceptArray.getString(i);
				if (StringUtils.isNotBlank(label)) {

					List<SKOSPrefLabel> prefLabelList = Arrays.asList(new SKOSPrefLabel(null, label, nodeID));
					try {
						result.add(type.getDeclaredConstructor(SKOSConcept.class).newInstance(new SKOSConcept(property.getURI(), null, prefLabelList, nodeID)));
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
	 * Counts the datasets present in the node
	 * 
	 *
	 * 
	 * @param none
	 * @throws URISyntaxException
	 * @throws ParseException
	 * @throws ODMSCatalogueNotFoundException
	 * @throws ODMSCatalogueForbiddenException
	 * @throws ODMSCatalogueOfflineException
	 * @throws IOException
	 * 
	 * @throws MalformedURLException
	 * @throws CKANException
	 * @returns int resulting datasets count
	 *
	 */
	@Override
	public int countDatasets() throws ParseException, URISyntaxException, ODMSCatalogueOfflineException,
			ODMSCatalogueForbiddenException, ODMSCatalogueNotFoundException, IOException {

		return getAllDatasets().size();

	}

	// Live search is not available on current SODA API
	@Override
	public List<DCATDataset> findDatasets(HashMap<String, Object> searchParameters) {
		ArrayList<DCATDataset> resultDatasets = new ArrayList<DCATDataset>();
		return resultDatasets;
	}

	// Individual get of the datasets is not available on current SODA API
	@Override
	public DCATDataset getDataset(String datasetId) {
		return null;
	}

	/**
	 * Retrieves all datasets belonging to a federated DKAN node using DKAN
	 * API Client
	 * 
	 * 
	 * @param none
	 * @throws ParseException
	 * @throws URISyntaxException
	 * @throws ODMSCatalogueNotFoundException
	 * @throws ODMSCatalogueForbiddenException
	 * @throws ODMSCatalogueOfflineException
	 * @throws IOException
	 * @returns List<DCATDataset> results converted to a List of DCATDataset
	 * @throws An
	 *             Exception if the request fails
	 */
	@Override
	public List<DCATDataset> getAllDatasets() throws ParseException, URISyntaxException, ODMSCatalogueOfflineException,
			ODMSCatalogueForbiddenException, ODMSCatalogueNotFoundException, IOException {

		ArrayList<DCATDataset> dcatDatasets = new ArrayList<DCATDataset>();

		datasetsArray = getJSONDatasets();
		for (int i = 0; i < datasetsArray.length(); i++) {
			try {
				JSONObject dataset = datasetsArray.getJSONObject(i);
				dcatDatasets.add(datasetToDCAT(dataset, node));
				dataset = null;

			} catch (Exception e) {
				logger.info(
						"There was an error: " + e.getMessage() + " while deserializing Dataset - " + i + " - SKIPPED");
			}
		}

		datasetsArray = null;
		System.gc();

		return dcatDatasets;
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
	 * @throws ODMSCatalogueNotFoundException
	 * @throws ODMSCatalogueForbiddenException
	 * @throws ODMSCatalogueOfflineException
	 * @throws IOException
	 * @throws JSONException
	 * @throws MalformedURLException
	 * @returns HashMap<String,String> a Map of ID,activity_type pairs
	 * @throws Exception
	 *             if the request fails
	 */
	@Override
	public ODMSSynchronizationResult getChangedDatasets(List<DCATDataset> oldDatasets, String startingDateString)
			throws ParseException, URISyntaxException, ODMSCatalogueOfflineException, ODMSCatalogueForbiddenException,
			ODMSCatalogueNotFoundException, IOException {

		ArrayList<DCATDataset> newDatasets = (ArrayList<DCATDataset>) getAllDatasets();
		// ArrayList<DCATDataset> newDatasets = new ArrayList<DCATDataset>();
		// newDatasets.add(new
		// DCATDataset(node,"","",null,"1","","2015-25-11T00:00:00Z","2015-25-11T00:00:00Z","","","","","","","","",null,null,null));
		// newDatasets.add(new

		ODMSSynchronizationResult syncrhoResult = new ODMSSynchronizationResult();

		ImmutableSet<DCATDataset> newSets = ImmutableSet.copyOf(newDatasets);
		ImmutableSet<DCATDataset> oldSets = ImmutableSet.copyOf(oldDatasets);

		int deleted = 0, added = 0, changed = 0;

		/// Find added datasets
		// difference(current,present)
		SetView<DCATDataset> diff = Sets.difference(newSets, oldSets);
		logger.info("New Packages: " + diff.size());
		for (DCATDataset d : diff) {
			syncrhoResult.addToAddedList(d);
			added++;
		}

		// Find removed datasets
		// difference(present,current)
		SetView<DCATDataset> diff1 = Sets.difference(oldSets, newSets);
		logger.info("Deleted Packages: " + diff1.size());
		for (DCATDataset d : diff1) {
			syncrhoResult.addToDeletedList(d);
			deleted++;
		}

		// Find updated datasets
		// intersection(present,current)
		SetView<DCATDataset> intersection = Sets.intersection(newSets, oldSets);
		logger.fatal("Changed Packages: " + intersection.size());

		GregorianCalendar oldDate = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		oldDate.setLenient(false);
		GregorianCalendar newDate = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		oldDate.setLenient(false);
		SimpleDateFormat ISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

		//SimpleDateFormat DCATDateF = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);

		int exception = 0;
		for (DCATDataset d : intersection) {
			try {
				int oldIndex = oldDatasets.indexOf(d);
				int newIndex = newDatasets.indexOf(d);
				oldDate.setTime(ISO.parse(oldDatasets.get(oldIndex).getUpdateDate().getValue()));
				newDate.setTime(ISO.parse(newDatasets.get(newIndex).getUpdateDate().getValue()));

				if (newDate.after(oldDate)) {
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
	// oldDatasets, String startingDateString) throws ParseException,
	// URISyntaxException, ODMSCatalogueOfflineException, ODMSCatalogueForbiddenException,
	// ODMSCatalogueNotFoundException{
	// HashMap<DCATDataset,String> result = new HashMap<DCATDataset, String>();
	// ArrayList<DCATDataset> newDatasets = (ArrayList <DCATDataset>)
	// getAllDatasets();
	//// ArrayList<DCATDataset> newDatasets = new ArrayList<DCATDataset>();
	////
	//// newDatasets.add(new
	// DCATDataset(node,"","",null,"1","","2015-25-11T00:00:00Z","2015-25-11T00:00:00Z","","","","","","","","",null,null,null));
	//// newDatasets.add(new
	// DCATDataset(node,"","",null,"2","","2015-25-11T00:00:00Z","2015-25-11T00:00:00Z","","","","","","","","",null,null,null));
	//// newDatasets.add(new
	// DCATDataset(node,"","",null,"3","","2015-25-11T00:00:00Z","2015-25-11T00:00:00Z","","","","","","","","",null,null,null));
	//
	//
	//
	// ImmutableSet<DCATDataset> newSets = ImmutableSet.copyOf(newDatasets);
	// ImmutableSet<DCATDataset> oldSets = ImmutableSet.copyOf(oldDatasets);
	//
	// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	// sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
	// GregorianCalendar startingDate = new
	// GregorianCalendar(TimeZone.getTimeZone("UTC"));
	// startingDate.setTime(sdf.parse(startingDateString));
	//
	// int deleted = 0;
	// int added = 0;
	// int changed = 0;
	//
	//
	// /// Find added datasets
	// //difference(current,present)
	// SetView<DCATDataset> diff = Sets.difference(newSets,oldSets);
	// logger.info("New Package "+diff.size());
	// for( DCATDataset d: diff ) {
	// result.put(d, "new package");
	// added++;
	// }
	//
	// // Find removed datasets
	// //difference(present,current)
	// SetView<DCATDataset> diff1 = Sets.difference(oldSets,newSets);
	// logger.info("Deleted Package "+diff1.size());
	// for( DCATDataset d: diff1 ) {
	// result.put(d, "deleted package");
	// deleted++;
	// }
	//
	// //Find updated datasets
	// //intersection(present,current)
	//// SimpleDateFormat sdf1 = new SimpleDateFormat( "EEE MMM dd HH:mm:ss z
	// yyyy", Locale.US);
	//// sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
	//// GregorianCalendar startingDate = new
	// GregorianCalendar(TimeZone.getTimeZone("UTC"));
	// SetView<DCATDataset> intersection = Sets.intersection(newSets, oldSets);
	// logger.fatal("Intersection Package " + intersection.size());
	//
	// GregorianCalendar oldDate = new
	// GregorianCalendar(TimeZone.getTimeZone("UTC"));
	// oldDate.setLenient(false);
	// GregorianCalendar newDate = new
	// GregorianCalendar(TimeZone.getTimeZone("UTC"));
	// oldDate.setLenient(false);
	// SimpleDateFormat DKANDateF = new
	// SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	//
	// SimpleDateFormat DCATDateF = new SimpleDateFormat("EEE MMM dd HH:mm:ss
	// zzz yyyy", Locale.US);
	//
	// int exception = 0;
	// for (DCATDataset d: intersection ){
	// try{
	// int oldIndex = oldDatasets.indexOf(d);
	// int newIndex = newDatasets.indexOf(d);
	// System.out.println("-----------------------\n");
	// System.out.println(oldDatasets.get(oldIndex).getDcat_modified().getValue());
	// System.out.println(newDatasets.get(newIndex).getDcat_modified().getValue());
	// System.out.println("-----------------------\n\n");
	// oldDate.setTime(DCATDateF.parse(oldDatasets.get(oldIndex).getDcat_modified().getValue()));
	// newDate.setTime(DKANDateF.parse(newDatasets.get(newIndex).getDcat_modified().getValue()));
	//// oldDate.setTime(sdf.parse(oldDatasets.get(oldIndex).getDcat_modified().getValue()));
	//// oldDate.setTimeInMillis(sdf.parse(parseDate(oldDatasets.get(oldIndex).getDcat_modified().getValue())).getTime());
	////
	////// newDate.setTime(sdf.parse(newDatasets.get(newIndex).getDcat_modified().getValue()));
	//// newDate.setTimeInMillis(sdf.parse(parseDate(newDatasets.get(newIndex).getDcat_modified().getValue())).getTime());
	//
	//
	//
	// if(newDate.after(oldDate)){
	// result.put(d, "changed package");
	// changed++;
	// }
	// }catch(Exception ex){
	// exception++;
	// if(exception%1000 == 0){
	// ex.printStackTrace();
	// }
	// }
	// }
	// System.out.println("Exceptions: "+exception);
	// System.out.println("Deleted "+deleted);
	// System.out.println("Changed "+changed);
	// System.out.println("Added "+added);
	//
	// return result;
	// }

	private String sendGetRequest1(String urlString) {


//		logger.info("Requesting a new token for the notification server");
		try{
			TrustManager[] certs = new TrustManager[]{new X509TrustManager() {
	            @Override
	            public X509Certificate[] getAcceptedIssuers() {
	                return null;
	            }

	            @Override
	            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	            }

	            @Override
	            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	            }
	        }};

	        SSLContext ctx = null;
	        try {
	            ctx = SSLContext.getInstance("TLS");
	            ctx.init(null, certs, new SecureRandom());
	        } catch (java.security.GeneralSecurityException e) {
	            logger.error("", e);
	            //throw OurExceptionUtils.wrapInRuntimeExceptionIfNecessary(e);
	        }

	        HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());

	        ClientBuilder clientBuilder = ClientBuilder.newBuilder();
	        try {
	            clientBuilder.sslContext(ctx);
	            clientBuilder.hostnameVerifier(new HostnameVerifier() {
	                @Override
	                public boolean verify(String hostname, SSLSession session) {
	                    return true;
	                }
	            });
	        } catch (Exception e) {
	        	e.printStackTrace();
	        	logger.error("", e);
	            //throw OurExceptionUtils.wrapInRuntimeExceptionIfNecessary(e);
	        }
			
	        Client client = ClientBuilder.newBuilder().build();
	        
			WebTarget webTarget = client.target(urlString);

			Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
			Response response = invocationBuilder.get();

			StatusType status = response.getStatusInfo();
			String res = response.readEntity(String.class);
			return res;
//			if(status.getStatusCode() == 200){
//				logger.info("Token retrieved");
//				JSONObject json = new JSONObject(res);
//				return json.getString("access_token");
//			}else{
//				logger.info("Problem during getting the token");
//				logger.info("res");
//				return res;
//			}
		}catch(Exception e){
			e.printStackTrace();
			return "";
		}

	
	}
	
	private String sendGetRequest(String urlString) throws IOException {
		URL url = null;

		try {
			// url = new URL( this.m_host + ":" + this.m_port + path);
			url = new URL(urlString);
		} catch (MalformedURLException mue) {
			System.err.println(mue);
			return null;
		}

		String body = "";

		final HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 3000000);
		// if(!(data.contains("\"rows\":\"1\"") ||
		// data.contains("\"rows\":\"0\"") || path.contains("package_list")) )
		// HttpConnectionParams.setSoTimeout(httpParams, 6);
		// else
		HttpConnectionParams.setSoTimeout(httpParams, 9000000);

		// apache HttpClient version >4.2 should use BasicClientConnectionManager
		HttpClient httpclient = new DefaultHttpClient(httpParams);

		// HttpClient httpclient = new DefaultHttpClient(httpParams);
		/*
		 * Set an HTTP proxy if it is specified in system properties.
		 * 
		 * http://docs.oracle.com/javase/6/docs/technotes/guides/net/proxies.html
		 * http://hc.apache.org/httpcomponents-client-ga/httpclient/examples/org/apache/
		 * http/examples/client/ClientExecuteProxy.java
		 */
		if (Boolean.parseBoolean(PropertyManager.getProperty(IdraProperty.HTTP_PROXY_ENABLED).trim())
				&& StringUtils.isNotBlank(PropertyManager.getProperty(IdraProperty.HTTP_PROXY_HOST).trim())) {

			int port = 80;
			if (isSet(PropertyManager.getProperty(IdraProperty.HTTP_PROXY_PORT))) {
				port = Integer.parseInt(PropertyManager.getProperty(IdraProperty.HTTP_PROXY_PORT));
			}
			HttpHost proxy = new HttpHost(PropertyManager.getProperty(IdraProperty.HTTP_PROXY_HOST), port, "http");
			httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
			if (isSet(PropertyManager.getProperty(IdraProperty.HTTP_PROXY_USER))) {
				((AbstractHttpClient) httpclient).getCredentialsProvider().setCredentials(
						new AuthScope(PropertyManager.getProperty(IdraProperty.HTTP_PROXY_HOST), port),
						(Credentials) new UsernamePasswordCredentials(
								PropertyManager.getProperty(IdraProperty.HTTP_PROXY_USER),
								PropertyManager.getProperty(IdraProperty.HTTP_PROXY_PASSWORD)));
			}
		}
		try {
			
			HttpGet getRequest = new HttpGet(url.toString());
			getRequest.addHeader("accept", "application/json");

			HttpResponse response = httpclient.execute(getRequest);

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
			}

			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			body = result.toString();

		} finally {
			httpclient.getConnectionManager().shutdown();
		}

		return body;
	}

	private static boolean isSet(String string) {
		return string != null && string.length() > 0;
	}

	@Override
	public int countSearchDatasets(HashMap<String, Object> searchParameters) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

}
