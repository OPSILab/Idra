/*******************************************************************************
 * Idra - Open Data Federation Platform
 * Copyright (C) 2021 Engineering Ingegneria Informatica S.p.A.
 * <p> 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * <p> 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

package it.eng.idra.connectors;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import it.eng.idra.beans.dcat.DcatDataset;
import it.eng.idra.beans.dcat.DcatDistribution;
import it.eng.idra.beans.dcat.DctLicenseDocument;
import it.eng.idra.beans.dcat.DctStandard;
import it.eng.idra.beans.dcat.FoafAgent;
import it.eng.idra.beans.dcat.SkosConcept;
import it.eng.idra.beans.dcat.SkosConceptStatus;
import it.eng.idra.beans.dcat.SkosConceptTheme;
import it.eng.idra.beans.dcat.SkosPrefLabel;
import it.eng.idra.beans.dcat.SpdxChecksum;
import it.eng.idra.beans.dcat.VcardOrganization;
import it.eng.idra.beans.odms.OdmsCatalogue;
import it.eng.idra.beans.odms.OdmsCatalogueForbiddenException;
import it.eng.idra.beans.odms.OdmsCatalogueOfflineException;
import it.eng.idra.beans.odms.OdmsSynchronizationResult;
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

// TODO: Auto-generated Javadoc
/**
 * The Class JunarConnector.
 */
@SuppressWarnings("deprecation")
public class JunarConnector implements IodmsConnector {

  /** The node. */
  private OdmsCatalogue node;

  /** The node id. */
  private String nodeId;

  /** The Constant JSON_TYPE. */
  public static final MediaType JSON_TYPE = MediaType.APPLICATION_JSON_TYPE;

  /** The logger. */
  private static Logger logger = LogManager.getLogger(JunarConnector.class);

  /** The Constant junarResourcesType. */
  private static final Map<String, String> junarResourcesType = new HashMap<String, String>();

  static {
    junarResourcesType.put("csv", "text/csv");
    junarResourcesType.put("xml", "application/xml");
    junarResourcesType.put("ajson", "application/json");

    /*
     * Junar provides also an Excel version of the resource; but it's created on the
     * fly and expires in few hours so it's not federable.
     */
  }

  /**
   * Instantiates a new junar connector.
   *
   * @param node the node
   */
  public JunarConnector(OdmsCatalogue node) {
    this.node = node;
    this.nodeId = String.valueOf(node.getId());
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.connectors.IodmsConnector#findDatasets(java.util.HashMap)
   */
  @Override
  public List<DcatDataset> findDatasets(HashMap<String, Object> searchParameters) throws Exception {
    ArrayList<DcatDataset> resultDatasets = new ArrayList<DcatDataset>();
    return resultDatasets;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * it.eng.idra.connectors.IodmsConnector#countSearchDatasets(java.util.HashMap)
   */
  @Override
  public int countSearchDatasets(HashMap<String, Object> searchParameters) throws Exception {
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.connectors.IodmsConnector#countDatasets()
   */
  @Override
  public int countDatasets() throws Exception {
    return getAllDatasets().size();
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.connectors.IodmsConnector#getAllDatasets()
   */
  @Override
  public List<DcatDataset> getAllDatasets() throws Exception {

    logger.info("-- JUNAR Connector Request sent -- " + node.getHost());

    ArrayList<DcatDataset> dcatDatasets = new ArrayList<DcatDataset>();

    Optional<String> returnedJson = Optional.ofNullable(sendGetRequest(
        node.getHost() + "/api/v2/resources?auth_key=" + node.getApiKey() + "&format=json"));

    if (!returnedJson.isPresent()) {
      throw new OdmsCatalogueOfflineException(" The ODMS node is currently unreachable");
    } else if (!returnedJson.get().startsWith("[")) {
      if (returnedJson.get().contains("403")) {
        throw new OdmsCatalogueForbiddenException("The ODMS node is forbidden");
      } else {
        throw new OdmsCatalogueOfflineException(" The ODMS node is currently unreachable");
      }
    }

    JSONArray jsonArray = new JSONArray(returnedJson.get());
    logger.debug("-- JUNAR Connector Response - Result count:" + jsonArray.length());

    for (int i = 0; i < jsonArray.length(); i++) {
      try {
        JSONObject dataset = jsonArray.getJSONObject(i);
        if (dataset.has("type") && "ds".equalsIgnoreCase(dataset.getString("type"))) {
          dcatDatasets.add(datasetToDcat(dataset, node));
        }
        dataset = null;
      } catch (Exception e) {
        logger.warn("There was an error: " + e.getMessage() + " while deserializing Dataset - " + i
            + " - SKIPPED");
      }
    }

    jsonArray = null;
    System.gc();

    return dcatDatasets;

  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.connectors.IodmsConnector#datasetToDcat(java.lang.Object,
   * it.eng.idra.beans.odms.OdmsCatalogue)
   */
  @Override
  public DcatDataset datasetToDcat(Object d, OdmsCatalogue node)
      throws JSONException, ParseException {

    JSONObject dataset = (JSONObject) d;

    // Properties to be mapped to Junar metadata
    String identifier = null;
    String description = null;
    String issued = null;
    String modified = null;
    String landingPage = null;
    FoafAgent publisher = null;
    List<String> keywords = new ArrayList<String>();

    identifier = dataset.getString("guid");
    landingPage = dataset.optString("link");
    description = dataset.optString("description");

    try {
      issued = CommonUtil
          .fixBadUtcDate(CommonUtil.fromMillisToUtcDate(dataset.optLong("created_at")));
    } catch (IllegalArgumentException skip) {
      logger.debug(skip.getLocalizedMessage());
    }

    try {
      modified = CommonUtil
          .fixBadUtcDate(CommonUtil.fromMillisToUtcDate(dataset.optLong("modified_at")));
    } catch (IllegalArgumentException skip) {
      logger.debug(skip.getLocalizedMessage());
    }

    if (dataset.has("tags")) {
      JSONArray keywordArray = dataset.getJSONArray("tags");
      for (int i = 0; i < keywordArray.length(); i++) {
        keywords.add(keywordArray.getString(i));
      }
    }
    if (dataset.has("category_name")) {
      keywords.add(dataset.getString("category_name"));
    }
    String title = null;
    title = dataset.optString("title");

    if (dataset.has("user")) {
      publisher = deserializeFoafAgent(dataset, "user", DCTerms.publisher, nodeId);
    }
    List<SkosConceptTheme> themeList = null;
    themeList = deserializeConcept(dataset, "category_name", DCAT.theme, nodeId,
        SkosConceptTheme.class);

    String frequency = null;
    frequency = dataset.optString("frequency");
    ArrayList<DcatDistribution> distributionList = new ArrayList<DcatDistribution>();
    distributionList = retrieveDistributionList(dataset, node.getApiKey());

    return new DcatDataset(nodeId, identifier, title, description, distributionList, themeList,
        publisher, new ArrayList<VcardOrganization>(), keywords, null, null, null, frequency, null,
        null, landingPage, null, null, issued, modified, null, null, null, null, null, "JUNAR",
        null, null, null, null, null, null);
  }

  /**
   * Retrieve distribution list.
   *
   * @param dataset the dataset
   * @param apikey  the apikey
   * @return the array list
   */
  private ArrayList<DcatDistribution> retrieveDistributionList(JSONObject dataset, String apikey) {

    ArrayList<DcatDistribution> distributionList = new ArrayList<DcatDistribution>();
    JSONObject distribution = new JSONObject();
    distribution.put("description", dataset.getString("description"));
    distribution.put("title", dataset.getString("title"));

    String uriPattern = node.getHost() + "/api/v2/datastreams/%s/data.%s?auth_key=" + apikey;

    junarResourcesType.entrySet().forEach((e) -> {
      distribution.put("mediaType", e.getValue());
      distribution.put("downloadURL",
          String.format(uriPattern, dataset.getString("guid"), e.getKey()));

      try {
        distributionList.add(distributionToDcat(distribution, null, nodeId));
      } catch (Exception ex) {
        logger.info("There was an error while deserializing a Distribution: " + ex.getMessage()
            + " - SKIPPED");
      }
    });

    return distributionList;
  }

  /**
   * Distribution to dcat.
   *
   * @param obj     the obj
   * @param license the license
   * @param nodeId  the node id
   * @return the dcat distribution
   * @throws Exception the exception
   */
  protected DcatDistribution distributionToDcat(JSONObject obj, DctLicenseDocument license,
      String nodeId) throws Exception {

    String accessUrl = null;
    String description = null;
    String format = null;
    String byteSize = null;
    String downloadUrl = null;
    String mediaType = null;
    String releaseDate = null;
    String updateDate = null;
    String rights = null;
    String title = null;
    SpdxChecksum checksum = null;
    List<String> documentation = new ArrayList<String>();
    List<String> language = new ArrayList<String>();
    List<DctStandard> linkedSchemas = null;
    SkosConceptStatus status = null;

    mediaType = obj.optString("mediaType");
    accessUrl = downloadUrl = obj.getString("downloadURL");

    return new DcatDistribution(nodeId, accessUrl, description, format, license, byteSize, checksum,
        documentation, downloadUrl, language, linkedSchemas, mediaType, releaseDate, updateDate,
        rights, status, title);

  }

  /**
   * Deserialize concept.
   *
   * @param           <T> the generic type
   * @param obj       the obj
   * @param fieldName the field name
   * @param property  the property
   * @param nodeId    the node id
   * @param type      the type
   * @return the list
   * @throws JSONException the JSON exception
   */
  protected <T extends SkosConcept> List<T> deserializeConcept(JSONObject obj, String fieldName,
      Property property, String nodeId, Class<T> type) throws JSONException {

    List<T> result = new ArrayList<T>();

    String concept = obj.optString(fieldName);
    JSONArray conceptArray = new JSONArray().put(concept);

    if (conceptArray != null) {
      for (int i = 0; i < conceptArray.length(); i++) {

        String label = conceptArray.getString(i);
        if (StringUtils.isNotBlank(label)) {

          List<SkosPrefLabel> prefLabelList = Arrays.asList(new SkosPrefLabel(null, label, nodeId));
          try {
            result.add(type.getDeclaredConstructor(SkosConcept.class)
                .newInstance(new SkosConcept(property.getURI(), null, prefLabelList, nodeId)));
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
   * Deserialize foaf agent.
   *
   * @param dataset   the dataset
   * @param fieldName the field name
   * @param property  the property
   * @param nodeId    the node id
   * @return the foaf agent
   */
  protected FoafAgent deserializeFoafAgent(JSONObject dataset, String fieldName, Property property,
      String nodeId) {
    try {
      String publishername = dataset.optString("user");

      return new FoafAgent(property.getURI(), null, publishername, null, null, null, publishername,
          nodeId);
    } catch (JSONException ignore) {
      logger.info("Agent object not valid! - Skipped");
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.connectors.IodmsConnector#getDataset(java.lang.String)
   */
  @Override
  public DcatDataset getDataset(String datasetId) throws Exception {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.connectors.IodmsConnector#getChangedDatasets(java.util.List,
   * java.lang.String)
   */
  @Override
  public OdmsSynchronizationResult getChangedDatasets(List<DcatDataset> oldDatasets,
      String startingDate) throws Exception {
    ArrayList<DcatDataset> newDatasets = (ArrayList<DcatDataset>) getAllDatasets();

    OdmsSynchronizationResult syncrhoResult = new OdmsSynchronizationResult();

    ImmutableSet<DcatDataset> newSets = ImmutableSet.copyOf(newDatasets);
    ImmutableSet<DcatDataset> oldSets = ImmutableSet.copyOf(oldDatasets);

    int deleted = 0;
    int added = 0;
    // , changed = 0;

    /// Find added datasets
    SetView<DcatDataset> diff = Sets.difference(newSets, oldSets);
    logger.info("New Packages: " + diff.size());
    for (DcatDataset d : diff) {
      syncrhoResult.addToAddedList(d);
      added++;
    }

    // Find removed datasets
    SetView<DcatDataset> diff1 = Sets.difference(oldSets, newSets);
    logger.info("Deleted Packages: " + diff1.size());
    for (DcatDataset d : diff1) {
      syncrhoResult.addToDeletedList(d);
      deleted++;
    }

    // Find updated datasets
    SetView<DcatDataset> intersection = Sets.intersection(newSets, oldSets);
    logger.fatal("Changed Packages: " + intersection.size());

    GregorianCalendar oldDate = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
    oldDate.setLenient(false);
    GregorianCalendar newDate = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
    oldDate.setLenient(false);
    SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    int exception = 0;
    for (DcatDataset d : intersection) {
      try {
        int oldIndex = oldDatasets.indexOf(d);
        int newIndex = newDatasets.indexOf(d);
        oldDate.setTime(iso.parse(oldDatasets.get(oldIndex).getUpdateDate().getValue()));
        newDate.setTime(iso.parse(newDatasets.get(newIndex).getUpdateDate().getValue()));

        if (newDate.after(oldDate)) {
          syncrhoResult.addToChangedList(d);
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

  /**
   * Send get request.
   *
   * @param urlString the url string
   * @return the string
   * @throws Exception the exception
   */
  private String sendGetRequest(String urlString) throws Exception {
    try {
      RestClient client = new RestClientImpl();
      HttpResponse response = client.sendGetRequest(urlString, new HashMap<String, String>());
      return client.getHttpResponseBody(response);
    } catch (Exception e) {
      throw e;
    }
  }

}
