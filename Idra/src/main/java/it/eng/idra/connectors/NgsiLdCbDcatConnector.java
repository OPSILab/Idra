/*******************************************************************************
 * Idra - Open Data Federation Platform
 * Copyright (C) 2021 Engineering Ingegneria Informatica S.p.A.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

package it.eng.idra.connectors;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import it.eng.idra.beans.IdraProperty;
import it.eng.idra.beans.dcat.DcatDataset;
import it.eng.idra.beans.dcat.DcatDistribution;
import it.eng.idra.beans.dcat.DctLicenseDocument;
import it.eng.idra.beans.dcat.DctLocation;
import it.eng.idra.beans.dcat.DctPeriodOfTime;
import it.eng.idra.beans.dcat.DctStandard;
import it.eng.idra.beans.dcat.FoafAgent;
import it.eng.idra.beans.dcat.SkosConcept;
import it.eng.idra.beans.dcat.SkosConceptSubject;
import it.eng.idra.beans.dcat.SkosConceptTheme;
import it.eng.idra.beans.dcat.SkosPrefLabel;
import it.eng.idra.beans.dcat.VcardOrganization;
import it.eng.idra.beans.odms.OdmsCatalogue;
import it.eng.idra.beans.odms.OdmsSynchronizationResult;
import it.eng.idra.utils.CommonUtil;
import it.eng.idra.utils.GsonUtil;
import it.eng.idra.utils.PropertyManager;
import it.eng.idra.utils.restclient.RestClient;
import it.eng.idra.utils.restclient.RestClientImpl;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

// TODO: Auto-generated Javadoc
/**
 * The Class OrionConnector.
 */
public class NgsiLdCbDcatConnector implements IodmsConnector {


  /** The subscription base callback url. */
  private static String subscriptionBaseUrl = 
       PropertyManager.getProperty(IdraProperty.IDRA_SERVER_BASEURL);
  

  /** The node id. */
  private String nodeId;
  
  /** The datasets array. */
  private JSONArray datasetsArray;

  /** The node. */
  private OdmsCatalogue node;

  /** The logger. */
  private static Logger logger = LogManager.getLogger(NgsiLdCbDcatConnector.class);

  /**
   * Instantiates a new orion connector.
   */
  public NgsiLdCbDcatConnector() {
  }

  /**
   * Instantiates a new orion connector.
   *
   * @param node the node
   */
  public NgsiLdCbDcatConnector(OdmsCatalogue node) {      
    if (node.getApiKey() == null || node.getApiKey().equals("")) {
      node.setApiKey(UUID.randomUUID().toString());
    }
    //logger.info("Api settata: " + node.getApiKey());
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
    //return -1;
  }

  
  /**
   * Gets a dcat distribution.
   *
   * @return the dcat distribution
   * 
   * @throws Exception exception
   */
  public DcatDistribution distributionToDcat(Object distribution, 
      OdmsCatalogue node) throws Exception {

    JSONObject j = (JSONObject) distribution;
    DcatDistribution distro = new DcatDistribution();
   
    distro.setIdentifier(j.getString("id"));
    
    distro.setNodeId(nodeId); 

    String title = null;
    JSONObject titleObject = j.getJSONObject("title");
    title = titleObject.getString("value");
    distro.setTitle(title);
    
    distro.setDescription("description");
    
    String accessUrl = null;
    JSONObject accessObject = j.getJSONObject("accessUrl");
    accessUrl = accessObject.getString("value");
    distro.setAccessUrl(accessUrl);
    
    String downloadUrl = null;
    JSONObject downloadObject = j.getJSONObject("downloadURL");
    downloadUrl = downloadObject.getString("value");
    distro.setDownloadUrl(downloadUrl);
    
    if (j.has("format")) {
      JSONObject formatObject = j.getJSONObject("format");
      distro.setFormat(formatObject.getString("value"));
    }
    JSONObject attributeObject = new JSONObject();
    if (j.has("byteSize")) {
      attributeObject = j.getJSONObject("byteSize");
      distro.setByteSize(attributeObject.getString("value"));
    }
    if (j.has("checksum")) {
      attributeObject = j.getJSONObject("checksum");
      distro.setChecksum(attributeObject.getString("value"));
    }
  
    if (j.has("rights")) {
      attributeObject = j.getJSONObject("rights");
      distro.setRights(attributeObject.getString("value"));
    }
    
    if (j.has("mediaType")) {
      attributeObject = j.getJSONObject("mediaType");
      distro.setMediaType(attributeObject.getString("value"));
    }
    if (j.has("description")) {
      attributeObject = j.getJSONObject("description");
      distro.setDescription(attributeObject.getString("value"));
    }
    if (j.has("license")) {
      attributeObject = j.getJSONObject("license");
      distro.setLicense(new DctLicenseDocument("", attributeObject.getString("value"),
          "", "", nodeId));
    }

    String releaseDate = null;
    if (j.has("releaseDate")) {
      JSONObject relDateObject = j.getJSONObject("releaseDate");
      JSONObject valueObj = relDateObject.getJSONObject("value");
      String date = valueObj.getString("@value");    
      releaseDate = CommonUtil.fixBadUtcDate(date);
      distro.setReleaseDate(releaseDate);
    }
     
    String updateDate = null;
    if (j.has("modifiedDate")) {
      JSONObject updDateObject = j.getJSONObject("modifiedDate");
      JSONObject valueObj = updDateObject.getJSONObject("value");
      String date = valueObj.getString("@value");    
      updateDate = CommonUtil.fixBadUtcDate(date);
      distro.setUpdateDate(updateDate);
    }
    //    if (j.has("status")) {
    //    logger.info("Distribution status skipped");
    //    }
    //    if (j.has("language")) {
    //      logger.info("Distribution language skipped");
    //    }
    //    if (j.has("linkedSchemas")) {
    //      logger.info("Distribution linkedSchemas skipped");
    //    }
    //    if (j.has("documentation")) {
    //      logger.info("Distribution documentation skipped");
    //    }
    
    return distro;
  }
  
  
  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.connectors.IodmsConnector#datasetToDcat(java.lang.Object,
   * it.eng.idra.beans.odms.OdmsCatalogue)
   */
  @Override
  public DcatDataset datasetToDcat(Object dataset, OdmsCatalogue node) throws Exception {
    JSONObject j = (JSONObject) dataset;

    String title = null;
    String description = null;
    String identifier = null;
    DctPeriodOfTime temporalCoverage = null;
    List<String> keywords = new ArrayList<String>();
    List<String> documentation = new ArrayList<String>();
    List<String> provenance = new ArrayList<String>();
    List<String> otherIdentifier = new ArrayList<String>();
    List<String> versionNotes = new ArrayList<String>();
    //List<String> relatedResource = new ArrayList<String>();

    List<DcatDistribution> distributionList = new ArrayList<DcatDistribution>();

    JSONObject titleObject = j.getJSONObject("title");
    title = titleObject.getString("value");

    identifier = j.optString("id", null);
    
    if (j.has("description")) {
      JSONObject desObject = j.getJSONObject("description");
      description = desObject.getString("value");
    }
    
    // landingPage
    String landingPage = "";
    JSONObject lanPageObject = j.getJSONObject("landingPage");
    landingPage = lanPageObject.getString("value");
    
    // frequency
    String frequency = "";
    if (j.has("frequency")) {
      JSONObject freqObject = j.getJSONObject("frequency");
      frequency = freqObject.getString("value");
    }

    JSONArray values = new JSONArray();
    
    // Themes
    List<SkosConceptTheme> themeList = new ArrayList<SkosConceptTheme>();
    List<String> themes = new ArrayList<String>();
    if (j.has("theme")) {
      JSONObject themeObject = j.getJSONObject("theme");
      if (themeObject.get("value") instanceof JSONArray) {
        values = themeObject.getJSONArray("value");
        if (values.length() > 0) {
          themes = GsonUtil.json2Obj(values.toString(), 
              GsonUtil.stringListType);
          themeList.addAll(extractConceptList(DCAT.theme.getURI(), themes, 
              SkosConceptTheme.class));
        }
      } else {
        themes.add(themeObject.getString("value")); 
        themeList.addAll(extractConceptList(DCAT.theme.getURI(), themes, 
            SkosConceptTheme.class));
      }
    }
    
    // Keywords
    if (j.has("keyword")) {
      JSONObject keyObject = j.getJSONObject("keyword");
      if (keyObject.get("value") instanceof JSONArray) {
        values = keyObject.getJSONArray("value");
        if (values.length() > 0) {
          keywords = GsonUtil.json2Obj(values.toString(), 
              GsonUtil.stringListType);
        } 
      } else {
        keywords.add(keyObject.getString("value"));     
      }                                                
    }
    
    //Languages
    List<String> language = new ArrayList<String>();
    if (j.has("language")) {
      JSONObject lanObject = j.getJSONObject("language");
      if (lanObject.get("value") instanceof JSONArray) {
        values = lanObject.getJSONArray("value");
        if (values.length() > 0) {
          language = GsonUtil.json2Obj(values.toString(), 
              GsonUtil.stringListType);
        } 
      } else {
        language.add(lanObject.getString("value"));      
      }
    }

    // Documentation
    if (j.has("documentation")) {    
      JSONObject docObject = j.getJSONObject("documentation");
      if (docObject.get("value") instanceof JSONArray) {
        values = docObject.getJSONArray("value");
        if (values.length() > 0) {
          documentation = GsonUtil.json2Obj(values.toString(), 
              GsonUtil.stringListType);
        } 
      } else {
        documentation.add(docObject.getString("value"));      
      }
    }
    
    // Provenance
    if (j.has("provenance")) {    
      JSONObject provObject = j.getJSONObject("provenance");
      if (provObject.get("value") instanceof JSONArray) {
        values = provObject.getJSONArray("value");
        if (values.length() > 0) {
          provenance = GsonUtil.json2Obj(values.toString(), 
              GsonUtil.stringListType);
        } 
      } else {
        provenance.add(provObject.getString("value"));     
      }
    }
    // Other Identifiers
    if (j.has("otherIdentifier")) {    
      JSONObject othIdObject = j.getJSONObject("otherIdentifier");
      if (othIdObject.get("value") instanceof JSONArray) {
        values = othIdObject.getJSONArray("value");
        if (values.length() > 0) {
          otherIdentifier = GsonUtil.json2Obj(values.toString(), 
              GsonUtil.stringListType);
        } 
      } else {
        otherIdentifier.add(othIdObject.getString("value"));     
      }
    }
    // Version
    String version = "";
    if (j.has("version")) {
      JSONObject verObject = j.getJSONObject("version");
      version = verObject.getString("value");
    }
    // Version Notes
    if (j.has("versionNotes")) {    
      JSONObject verNotesObject = j.getJSONObject("versionNotes");
      if (verNotesObject.get("value") instanceof JSONArray) {
        values = verNotesObject.getJSONArray("value");
        if (values.length() > 0) {
          versionNotes = GsonUtil.json2Obj(values.toString(), 
              GsonUtil.stringListType);
        } 
      } else {
        versionNotes.add(verNotesObject.getString("value"));     
      }
    }
    // AccessRights
    String accessRights = "";
    if (j.has("accessRights")) {
      JSONObject accRightsObject = j.getJSONObject("accessRights");
      accessRights = accRightsObject.getString("value");
    }

    String type = null;
    type = j.optString("type", null);       // ?
    
    // Release Date
    String releaseDate = null;
    if (j.has("releaseDate")) {
      JSONObject relDateObject = j.getJSONObject("releaseDate");
      JSONObject valueObj = relDateObject.getJSONObject("value");
      String date = valueObj.getString("@value");    
      releaseDate = CommonUtil.fixBadUtcDate(date);
    }
    // Update Date 
    String updateDate = null;
    if (j.has("updateDate")) {
      JSONObject updDateObject = j.getJSONObject("updateDate");
      JSONObject valueObj = updDateObject.getJSONObject("value");
      String date = valueObj.getString("@value");    
      updateDate = CommonUtil.fixBadUtcDate(date);
    }
    
    List<VcardOrganization> contactPointList = new ArrayList<VcardOrganization>();  
    if (j.has("contactPoint")) {    
      JSONObject contObject = j.getJSONObject("contactPoint");
      if (contObject.get("value") instanceof JSONArray) {
        values = contObject.getJSONArray("value");
        if (values.length() > 0) {
          for (int i = 0; i < values.length(); i++) {
            contactPointList.add(
                new VcardOrganization(DCAT.contactPoint.getURI(), 
                    null, "", values.getString(i),
                    "", "", "", nodeId));
          }
        } 
      } else {
        contactPointList.add(
            new VcardOrganization(DCAT.contactPoint.getURI(), 
                null, "", contObject.getString("value"),
                "", "", "", nodeId));  
      }
    }

    FoafAgent publisher = null;
    FoafAgent creator = null;
    // Publisher
    if (j.has("publisher")) {
      JSONObject pub = j.getJSONObject("publisher");
      publisher = new FoafAgent(DCTerms.publisher.getURI(), "",
          pub.getString("value"), "", "", null,
          "", String.valueOf(node.getId()));
    }
    // Creator
    if (j.has("creator")) {
      JSONObject creat = j.getJSONObject("creator");
      creator = new FoafAgent(DCTerms.creator.getURI(), "",
          creat.getString("value"), "", "", null,
          "", String.valueOf(node.getId()));
    }
    
    List<String> sample = new ArrayList<String>();
    List<String> source = new ArrayList<String>();
    List<String> hasVersion = new ArrayList<String>();
    List<String> isVersionOf = new ArrayList<String>();
    
    DctLocation spatialCoverage = null;
    List<SkosConceptSubject> subjectList = new ArrayList<SkosConceptSubject>();
    List<DctStandard> conformsTo = new ArrayList<DctStandard>();      

    String startDate = null;
    String endDate = null;
    if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
      temporalCoverage = new DctPeriodOfTime(DCTerms.temporal.getURI(), startDate, endDate, nodeId);
    }

    // DISTRIBUTIONS
    if (j.has("datasetDistribution")) {
      List<String> distributionsId = new ArrayList<String>();
      JSONObject distribObject = j.getJSONObject("datasetDistribution");

      if (distribObject.get("value") instanceof JSONArray) {
        JSONArray distrib = distribObject.getJSONArray("value");
        for (int i = 0; i < distrib.length(); i++) {
          distributionsId.add(distrib.getString(i));
        }
        
      } else {
        distributionsId.add(distribObject.getString("value"));
      }
      
      for (int i = 0; i < distributionsId.size(); i++) {
        DcatDistribution distro = distributionToDcat(getJsonDistribution(distributionsId.get(i)), 
            node);
        distributionList.add(distro);
      }
      
    }
    FoafAgent rightsHolder = null;
    //  spatialCoverage e conformsTo non sono previsti nello smart data model DCATAP/NGSILD   
    //  if (j.has("sample")) {
    //  sample = GsonUtil.json2Obj(j.getJSONArray("sample").toString(), GsonUtil.stringListType);
    //}
    //
    //if (j.has("source")) {
    //  source = GsonUtil.json2Obj(j.getJSONArray("source").toString(), GsonUtil.stringListType);
    //}
    // Subject
    //    if (j.has("subject")) {
    //      List<String> subjects = GsonUtil.json2Obj(j.getJSONArray("subject").toString(),
    //          GsonUtil.stringListType);
    //      if (subjects.size() > 0) {
    //        subjectList.addAll(
    //            extractConceptList(DCTerms.subject.getURI(), subjects, SkosConceptSubject.class));
    //      }
    //    }
    
    //    if (j.has("relatedResource")) {
    //      relatedResource = GsonUtil.json2Obj(j.getJSONArray("relatedResource").toString(),
    //          GsonUtil.stringListType);
    //    }
    //
    //    if (j.has("hasVersion")) {
    //      hasVersion = GsonUtil.json2Obj(j.getJSONArray("hasVersion").toString(),
    //          GsonUtil.stringListType);
    //    }
    //
    //    if (j.has("isVersionOf")) {
    //      isVersionOf = GsonUtil.json2Obj(j.getJSONArray("isVersionOf").toString(),
    //          GsonUtil.stringListType);
    //    }
    
    return new DcatDataset(nodeId, identifier, title, description, distributionList, themeList,
        publisher, contactPointList, keywords, accessRights, conformsTo, documentation, frequency,
        hasVersion, isVersionOf, landingPage, language, provenance, releaseDate, updateDate,
        otherIdentifier, sample, source, spatialCoverage, temporalCoverage, type, version,
        versionNotes, rightsHolder, creator, subjectList, null);
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
  
  
  /**
   * Gets the json datasets.
   *
   * @return the json datasets
   * 
   * @throws Exception exception
   */
  private JSONArray getJsonDatasets()
      throws Exception {

    logger.info("-- ORION DCAT Connector Request for Datasets, sent -- " + node.getHost());
    
    Map<String, String> headers = new HashMap<String, String>();
    headers.put("Content-Type", "application/json");
    RestClient client = new RestClientImpl();

    String url = node.getHost() + "/ngsi-ld/v1/entities?type=Dataset";
    
    HttpResponse response;
    String returnedJson = "";

    response = client.sendGetRequest(url, headers);
    returnedJson = client.getHttpResponseBody(response);

    JSONArray jsonArray = new JSONArray(returnedJson);
    System.out.println("JSONDATASET: " + jsonArray);
    return jsonArray;
  }
  

  /**
   * Allows to subscribe to the CB.
   *
   * @throws Exception exception 
   */
  public void subscribeToTheContextBroker() throws Exception {
    
    logger.info("Request for subscription from Idra to CB");
    logger.info("Catalogue ID: " + node.getId());
    
    Map<String, String> headers = new HashMap<String, String>();
    headers.put("Content-Type", "application/json");
    RestClient client = new RestClientImpl();
    
    HttpResponse response = client.sendGetRequest(node.getHost() 
        + "/ngsi-ld/v1/subscriptions/urn:ngsi-ld:Subscription:" + node.getId(), headers);
    int status = client.getStatus(response);
    if (status != 200) {
      logger.info("Subscription NOT present, creation");
      

      String endpoint = (subscriptionBaseUrl.endsWith("/")
          ? subscriptionBaseUrl : subscriptionBaseUrl + "/")
          + "Idra/api/v1/client" + "/notification/" + node.getId() 
          + "/" + node.getApiKey() + "/push";
      
      String req = "{"
          + "\"description\": \"Notify me on the creation or modification of a Dataset\","
          + "\"type\": \"Subscription\","
          + "\"id\": \"urn:ngsi-ld:Subscription:" + node.getId() + "\","
          + "\"entities\": [{\"type\": \"Dataset\"}, {\"type\": \"DistributionDCAT-AP\"}],"
          + "\"notification\": {"
          + "  \"attributes\": [\"title\"],"
          + "  \"format\": \"normalized\","
          + "  \"endpoint\": {"
          + "  \"uri\": \"" + endpoint + "\","
          + "  \"accept\": \"application/json\""
          + "  }"
          + "}"
          + "}";
      
      logger.info("REQUEST NOTIFY: "  + req);
      
      String api = node.getHost() + "/ngsi-ld/v1/subscriptions/";
      response = client.sendPostRequest(api, req,
          MediaType.APPLICATION_JSON_TYPE, headers); 
      status = client.getStatus(response);
      
      if (status != 200 && status != 207 && status != 204 && status != -1 
          && status != 201 && status != 301) {
        throw new Exception("-- STATUS POST SUBSCRIPTION "
            + "FROM IDRA: " + status);
      }  
    }
    logger.info("Subscription already present, does not need to be created");
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.connectors.IodmsConnector#getAllDatasets()
   */
  @Override
  public List<DcatDataset> getAllDatasets() throws Exception {
    
    ArrayList<DcatDataset> dcatDatasets = new ArrayList<DcatDataset>();
    
    List<String> datasetsNgsiId = new ArrayList<String>();
    datasetsArray = getJsonDatasets();
    
    //System.out.println("NUM DATASET: " + datasetsArray.length());
    
    for (int i = 0; i < datasetsArray.length(); i++) {
      try {
        JSONObject dataset = datasetsArray.getJSONObject(i);

        DcatDataset dcatDataset = datasetToDcat(dataset, node);
        dcatDatasets.add(dcatDataset);
        
        datasetsNgsiId.add(dcatDataset.getIdentifier().getValue());
        
        dataset = null;

      } catch (Exception e) {
        logger.info("There was an error: " + e.getMessage() + " while deserializing "
            + "Dataset for Orion DCAT - " + i + " - SKIPPED");
      }
    }
    //System.out.println("Num Id dei datasets recuperati: " + datasetsNgsiId.size());
    logger.info("AUTOUPDATE of the node: " + node.getAutoUpdate());
    if (node.getAutoUpdate() == 1) {
      subscribeToTheContextBroker();
    }
    
    datasetsArray = null;
    System.gc();

    return dcatDatasets;

  }
  
  /**
   * Gets the json distributions.
   *
   * @return the json distributions
   * 
   * @throws Exception exception 
   */
  private JSONObject getJsonDistribution(String id)
      throws Exception {

    //logger.info("-- ORION DCAT Connector Request for DIstrib, sent -- " + node.getHost());
    
    Map<String, String> headers = new HashMap<String, String>();
    headers.put("Content-Type", "application/json");
    RestClient client = new RestClientImpl();
    
    // L'Id della distrib che arriva è di tipo 
    //urn:ngsi-ld:Dataset:items:330d6f03-9e95-4335-af06-9b8437f5e084
    // però l'entity salvata è di tipo 
    //urn:ngsi-ld:DistributionDCAT-AP:id:330d6f03-9e95-4335-af06-9b8437f5e084
    String distribId = "urn:ngsi-ld:DistributionDCAT-AP:id:" + id.split(":")[4];

    String url = node.getHost() + "/ngsi-ld/v1/entities?type=DistributionDCAT-AP&id=" + distribId;

    HttpResponse response = client.sendGetRequest(url, headers);
    String returnedJson = client.getHttpResponseBody(response);
    
    JSONArray jsonArr = new JSONArray(returnedJson);
    JSONObject jsonObject = jsonArr.getJSONObject(0);
    return jsonObject;
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
    //return null;
    
    ArrayList<DcatDataset> newDatasets = (ArrayList<DcatDataset>) getAllDatasets();

    OdmsSynchronizationResult syncrhoResult = new OdmsSynchronizationResult();

    ImmutableSet<DcatDataset> newSets = ImmutableSet.copyOf(newDatasets);
    ImmutableSet<DcatDataset> oldSets = ImmutableSet.copyOf(oldDatasets);

    int deleted = 0;
    int added = 0;
    int changed = 0;

    /// Find added datasets
    // difference(current,present)
    SetView<DcatDataset> diff = Sets.difference(newSets, oldSets);
    logger.info("DIFF if added: " + diff.size());
    logger.info("New Packages: " + diff.size());
    for (DcatDataset d : diff) {
      syncrhoResult.addToAddedList(d);
      added++;
    }

    // Find removed datasets
    // difference(present,current)
    SetView<DcatDataset> diff1 = Sets.difference(oldSets, newSets);
    logger.info("DIFF if removed: " + diff.size());
    logger.info("Deleted Packages: " + diff1.size());
    for (DcatDataset d : diff1) {
      syncrhoResult.addToDeletedList(d);
      deleted++;
    }

    // Find updated datasets
    // intersection(present,current)
    SetView<DcatDataset> intersection = Sets.intersection(newSets, oldSets);
    logger.fatal("Changed Packages: " + intersection.size());

    GregorianCalendar oldDate = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
    oldDate.setLenient(false);
    GregorianCalendar newDate = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
    oldDate.setLenient(false);
    SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    // SimpleDateFormat DCATDateF = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz
    // yyyy", Locale.US);

    int exception = 0;
    for (DcatDataset d : intersection) {
      try {
        int oldIndex = oldDatasets.indexOf(d);
        int newIndex = newDatasets.indexOf(d);
        oldDate.setTime(iso.parse(oldDatasets.get(oldIndex).getUpdateDate().getValue()));
        newDate.setTime(iso.parse(newDatasets.get(newIndex).getUpdateDate().getValue()));

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

  /**
   * Extract concept list.
   *
   * @param             <T> the generic type
   * @param propertyUri the property uri
   * @param concepts    the concepts
   * @param type        the type
   * @return the list
   */
  private <T extends SkosConcept> List<T> extractConceptList(String propertyUri,
      List<String> concepts, Class<T> type) {
    List<T> result = new ArrayList<T>();

    for (String label : concepts) {
      try {
        result.add(type.getDeclaredConstructor(SkosConcept.class).newInstance(new SkosConcept(
            propertyUri, "", Arrays.asList(new SkosPrefLabel("", label, nodeId)), nodeId)));
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
          | InvocationTargetException | NoSuchMethodException | SecurityException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    return result;
  }

}
