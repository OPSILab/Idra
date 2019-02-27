package it.eng.idra.connectors;

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
import it.eng.idra.beans.odms.ODMSCatalogueOfflineException;
import it.eng.idra.beans.odms.ODMSSynchronizationResult;
import it.eng.idra.utils.CommonUtil;
import it.eng.idra.utils.restclient.RestClient;
import it.eng.idra.utils.restclient.RestClientImpl;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

@SuppressWarnings("deprecation")
public class JunarConnector implements IODMSConnector {

	private ODMSCatalogue node;
	private String nodeID;
	public static final MediaType JSON_TYPE = MediaType.APPLICATION_JSON_TYPE;
	private static Logger logger = LogManager.getLogger(JunarConnector.class);
	
	private static final Map<String, String> junarResourcesType = new HashMap<String, String>();
	
	static{
		junarResourcesType.put("csv", "text/csv");
		junarResourcesType.put("xml", "application/xml");
		junarResourcesType.put("ajson", "application/json");
		
		/*
		 * Junar provides also an Excel version of the resource; but it's created on the fly
		 * and expires in few hours so it's not federable.
		 */
	}
	
	public JunarConnector(ODMSCatalogue node) {
		this.node = node;
		this.nodeID = String.valueOf(node.getId());
	}
	
	/**
	 * Live search is not available on current Junar APIs
	 */
	@Override
	public List<DCATDataset> findDatasets(HashMap<String, Object> searchParameters) throws Exception {
		ArrayList<DCATDataset> resultDatasets = new ArrayList<DCATDataset>();
		return resultDatasets;
	}

	/**
	 * Live search is not available on current Junar APIs
	 */
	@Override
	public int countSearchDatasets(HashMap<String, Object> searchParameters) throws Exception {
		return 0;
	}

	@Override
	public int countDatasets() throws Exception {
		return getAllDatasets().size();
	}
	
	@Override
	public List<DCATDataset> getAllDatasets() throws Exception {

		logger.info("-- JUNAR Connector Request sent -- " + node.getHost());
		
		ArrayList<DCATDataset> dcatDatasets = new ArrayList<DCATDataset>();	
		
		Optional<String> returned_json = Optional.ofNullable(sendGetRequest(node.getHost() + "/api/v2/resources?auth_key="+node.getAPIKey()+"&format=json"));
		
		if (!returned_json.isPresent()){
			throw new ODMSCatalogueOfflineException(" The ODMS node is currently unreachable");
		}
		else if(!returned_json.get().startsWith("[")){
			if (returned_json.get().contains("403")){
				throw new ODMSCatalogueForbiddenException("The ODMS node is forbidden");
			}
			else
				throw new ODMSCatalogueOfflineException(" The ODMS node is currently unreachable");
		}

		JSONArray jsonArray = new JSONArray(returned_json.get());
		logger.debug("-- JUNAR Connector Response - Result count:" + jsonArray.length());
		
		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				JSONObject dataset = jsonArray.getJSONObject(i);
				if(dataset.has("type") && "ds".equalsIgnoreCase(dataset.getString("type"))){
					dcatDatasets.add(datasetToDCAT(dataset, node));
				}
				dataset = null;
			} catch (Exception e) {
				logger.warn("There was an error: " + e.getMessage() + " while deserializing Dataset - " + i + " - SKIPPED");
			}
		}

		jsonArray = null;
		System.gc();

		return dcatDatasets;
		
	}

	/**
	 * Performs mapping from Junar JSON Resource object to DCATDataset object
	 *
	 * @param node Node which dataset belongs to
	 * @param d Junar Resource to be mapped
	 * @throws ParseException JSONException
	 * @returns DCATDataset resulting mapped object
	 * @throws Exception if the request fails
	 */
	@Override
	public DCATDataset datasetToDCAT(Object d, ODMSCatalogue node) throws JSONException, ParseException {

		JSONObject dataset = (JSONObject) d;

		// Properties to be mapped to Junar metadata
		String identifier = null;
		String description = null, issued = null, modified = null, title = null, landingPage = null;
		List<VCardOrganization> contactPointList = null;
		FOAFAgent publisher = null;
		List<String> keywords = new ArrayList<String>();
		ArrayList<DCATDistribution> distributionList = new ArrayList<DCATDistribution>();
		List<SKOSConceptTheme> themeList = null;
		String frequency = null;
		
		identifier = dataset.getString("guid");
		landingPage = dataset.optString("link");
		description = dataset.optString("description");

		try {
			issued = CommonUtil.fixBadUTCDate(CommonUtil.fromMillisToUtcDate(dataset.optLong("created_at")));
		} catch (IllegalArgumentException skip) {
		}

		try {
			modified = CommonUtil.fixBadUTCDate(CommonUtil.fromMillisToUtcDate(dataset.optLong("modified_at")));
		} catch (IllegalArgumentException skip) {
		}

		if (dataset.has("tags")) {
			JSONArray keywordArray = dataset.getJSONArray("tags");
			for (int i = 0; i < keywordArray.length(); i++) {
				keywords.add(keywordArray.getString(i));
			}
		}
		if(dataset.has("category_name")){
			keywords.add(dataset.getString("category_name"));
		}

		title = dataset.optString("title");

		if (dataset.has("user"))
			publisher = deserializeFOAFAgent(dataset, "user", DCTerms.publisher, nodeID);

		themeList = deserializeConcept(dataset, "category_name", DCAT.theme, nodeID, SKOSConceptTheme.class);
		
		frequency = dataset.optString("frequency");
		
		distributionList = retrieveDistributionList(dataset, node.getAPIKey());

		return new DCATDataset(nodeID, identifier, title, description, distributionList, themeList, publisher, contactPointList, keywords, null, 
				null, null, frequency, null, null, landingPage, null, null, issued, modified, null, null, null, null, null, 
				"JUNAR", null, null, null, null, null, null);
	}
	
	/**
	 * Junar always provides CSV, XML, JSON and XLS versions of the resources
	 * @param identifier
	 * @param apikey
	 * @return
	 */
	private ArrayList<DCATDistribution> retrieveDistributionList(JSONObject dataset, String apikey) {
		
		ArrayList<DCATDistribution> distributionList = new ArrayList<DCATDistribution>();
		JSONObject distribution = new JSONObject();
		distribution.put("description", dataset.getString("description"));
		distribution.put("title", dataset.getString("title"));
		
		String uriPattern = node.getHost() + "/api/v2/datastreams/%s/data.%s?auth_key="+apikey;
		
		junarResourcesType.entrySet().forEach((e) -> {
			distribution.put("mediaType", e.getValue());
			distribution.put("downloadURL", String.format(uriPattern, dataset.getString("guid"), e.getKey()));
			
			try {
				distributionList.add(distributionToDCAT(distribution, null, nodeID));
			} catch (Exception ex) {
				logger.info("There was an error while deserializing a Distribution: " + ex.getMessage() + " - SKIPPED");
			}
		});
		
		return distributionList;
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
		accessURL = downloadURL = obj.getString("downloadURL");

		return new DCATDistribution(nodeID, accessURL, description, format, license, byteSize, checksum, documentation,
				downloadURL, language, linkedSchemas, mediaType, releaseDate, updateDate, rights, status, title);

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
		
		String concept = obj.optString(fieldName);
		JSONArray conceptArray = new JSONArray().put(concept); 

		if (conceptArray != null) {
			for (int i = 0; i < conceptArray.length(); i++) {

				String label = conceptArray.getString(i);
				if (StringUtils.isNotBlank(label)) {

					List<SKOSPrefLabel> prefLabelList = Arrays.asList(new SKOSPrefLabel(null, label, nodeID));
					try {
						result.add(type.getDeclaredConstructor(SKOSConcept.class).newInstance(new SKOSConcept(property.getURI(), null, prefLabelList, nodeID)));
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException | NoSuchMethodException | SecurityException e) {
						e.printStackTrace();
					}
				}
			}
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
			String publishername = dataset.optString("user");
			
			return new FOAFAgent(property.getURI(), null, publishername, null, null, null, publishername, nodeID);
		} catch (JSONException ignore) {
			logger.info("Agent object not valid! - Skipped");
		}
		return null;
	}

	@Override
	public DCATDataset getDataset(String datasetId) throws Exception {
		return null;
	}

	@Override
	public ODMSSynchronizationResult getChangedDatasets(List<DCATDataset> oldDatasets, String startingDate) throws Exception {
		ArrayList<DCATDataset> newDatasets = (ArrayList<DCATDataset>) getAllDatasets();

		ODMSSynchronizationResult syncrhoResult = new ODMSSynchronizationResult();

		ImmutableSet<DCATDataset> newSets = ImmutableSet.copyOf(newDatasets);
		ImmutableSet<DCATDataset> oldSets = ImmutableSet.copyOf(oldDatasets);

		int deleted = 0, added = 0;//, changed = 0;

		/// Find added datasets
		SetView<DCATDataset> diff = Sets.difference(newSets, oldSets);
		logger.info("New Packages: " + diff.size());
		for (DCATDataset d : diff) {
			syncrhoResult.addToAddedList(d);
			added++;
		}

		// Find removed datasets
		SetView<DCATDataset> diff1 = Sets.difference(oldSets, newSets);
		logger.info("Deleted Packages: " + diff1.size());
		for (DCATDataset d : diff1) {
			syncrhoResult.addToDeletedList(d);
			deleted++;
		}

		// Find updated datasets
		SetView<DCATDataset> intersection = Sets.intersection(newSets, oldSets);
		logger.fatal("Changed Packages: " + intersection.size());

		GregorianCalendar oldDate = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		oldDate.setLenient(false);
		GregorianCalendar newDate = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		oldDate.setLenient(false);
		SimpleDateFormat ISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

		int exception = 0;
		for (DCATDataset d : intersection) {
			try {
				int oldIndex = oldDatasets.indexOf(d);
				int newIndex = newDatasets.indexOf(d);
				oldDate.setTime(ISO.parse(oldDatasets.get(oldIndex).getUpdateDate().getValue()));
				newDate.setTime(ISO.parse(newDatasets.get(newIndex).getUpdateDate().getValue()));

				if (newDate.after(oldDate)) {
					syncrhoResult.addToChangedList(d);
//					changed++;
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
	
	private String sendGetRequest(String urlString) throws Exception {
		try {
			RestClient client = new RestClientImpl();
			HttpResponse response = client.sendGetRequest(urlString, new HashMap<String,String>());
			return client.getHttpResponseBody(response);
		}catch(Exception e) {
			throw e;
		}
	}
	
	private static boolean isSet(String string) {
		return string != null && string.length() > 0;
	}

}
