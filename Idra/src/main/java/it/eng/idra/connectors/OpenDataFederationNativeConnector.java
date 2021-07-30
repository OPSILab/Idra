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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import it.eng.idra.beans.dcat.DcatDataset;
import it.eng.idra.beans.dcat.DcatDistribution;
import it.eng.idra.beans.dcat.DctLicenseDocument;
import it.eng.idra.beans.dcat.DctLocation;
import it.eng.idra.beans.dcat.DctPeriodOfTime;
import it.eng.idra.beans.dcat.DctStandard;
import it.eng.idra.beans.dcat.FoafAgent;
import it.eng.idra.beans.dcat.SkosConcept;
import it.eng.idra.beans.dcat.SkosConceptStatus;
import it.eng.idra.beans.dcat.SkosConceptSubject;
import it.eng.idra.beans.dcat.SkosConceptTheme;
import it.eng.idra.beans.dcat.SkosPrefLabel;
import it.eng.idra.beans.dcat.VCardOrganization;
import it.eng.idra.beans.odms.OdmsCatalogue;
import it.eng.idra.beans.odms.OdmsCatalogueOfflineException;
import it.eng.idra.beans.odms.OdmsSynchronizationResult;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// TODO: Auto-generated Javadoc
/**
 * The Class OpenDataFederationNativeConnector.
 */
public class OpenDataFederationNativeConnector implements IodmsConnector {

  /** The node. */
  private OdmsCatalogue node;

  /** The node id. */
  private String nodeId;

  /** The Constant JSON_TYPE. */
  public static final MediaType JSON_TYPE = MediaType.APPLICATION_JSON_TYPE;

  /** The logger. */
  private static Logger logger = LogManager.getLogger(OpenDataFederationNativeConnector.class);

  /**
   * Instantiates a new open data federation native connector.
   *
   * @param node the node
   */
  public OpenDataFederationNativeConnector(OdmsCatalogue node) {
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

    ArrayList<DcatDataset> dcatResults = new ArrayList<DcatDataset>();
    String sort = "";
    String rows = "";
    String offset = "";

    if (searchParameters.containsKey("sort")) {
      sort = (String) searchParameters.get("sort");
    }
    if (searchParameters.containsKey("rows")) {
      rows = (String) searchParameters.get("rows");
    }
    if (searchParameters.containsKey("start")) {
      offset = (String) searchParameters.get("start");
    }
    String query = "";
    query = buildQueryNativeString(searchParameters);

    logger.info("-- NATIVE Connector Request sent --" + "ROWS: " + rows);

    NativeClient c = new NativeClient(node);

    JSONObject resultJson = c.findDatasets(query, sort, rows, offset);
    JSONArray results = resultJson.optJSONArray("results");
    logger.info("-- NATIVE Connector Response - Result count:" + resultJson.optInt("count"));

    if (results != null) {
      for (int i = 0; i < results.length(); i++) {
        JSONObject obj = results.getJSONObject(i);
        dcatResults.add(datasetToDcat(obj, node));
        obj = null;
      }
    }

    c = null;
    System.gc();
    return dcatResults;

  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.connectors.IodmsConnector#countDatasets()
   */
  @Override
  public int countDatasets() throws Exception {

    NativeClient c = new NativeClient(node);

    JSONArray datasetsId = c.getAllDatasetsId();
    int count = datasetsId.length();
    datasetsId = null;
    c = null;

    if (count == 0) {
      throw new OdmsCatalogueOfflineException(" The ODMS node is currently unreachable");
    }

    return count;

  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.connectors.IodmsConnector#datasetToDcat(java.lang.Object,
   * it.eng.idra.beans.odms.OdmsCatalogue)
   */
  @Override
  public DcatDataset datasetToDcat(Object d, OdmsCatalogue node) throws Exception {

    JSONObject dataset = (JSONObject) d;

    String title = null;
    String description = null;
    String frequency = null;
    String releaseDate = null;
    String updateDate = null;
    String identifier = null;
    FoafAgent publisher = null;
    FoafAgent rightsHolder = null;
    FoafAgent creator = null;
    DctPeriodOfTime temporalCoverage = null;

    List<String> keywords = new ArrayList<String>();
    List<String> documentation = new ArrayList<String>();
    List<String> hasVersion = new ArrayList<String>();
    List<String> isVersionOf = new ArrayList<String>();
    List<String> language = new ArrayList<String>();
    List<String> provenance = new ArrayList<String>();
    List<String> otherIdentifier = new ArrayList<String>();
    List<String> sample = new ArrayList<String>();
    List<String> source = new ArrayList<String>();
    List<String> versionNotes = new ArrayList<String>();
    List<String> relatedResource = new ArrayList<String>();

    List<DcatDistribution> distributionList = new ArrayList<DcatDistribution>();

    /*
     * DON'T TOUCH - GetString and not OptString for identifier, because it's
     * mandatory and eventually throws an exception
     */
    identifier = dataset.getString("id");

    title = dataset.optString("title");
    description = dataset.optString("description");
    List<SkosConceptTheme> themeList = null;
    themeList = deserializeConcept(dataset, "theme", DCAT.theme, nodeId, SkosConceptTheme.class);

    if (dataset.has("publisher")) {
      publisher = deserializeFoafAgent(dataset, "publisher", DCTerms.publisher, nodeId);
    }
    List<VCardOrganization> contactPointList = new ArrayList<VCardOrganization>();
    contactPointList = deserializeContactPoint(dataset, DCAT.contactPoint, nodeId);

    if (dataset.has("keywords")) {
      keywords = GsonUtil.json2Obj(dataset.getJSONArray("keywords").toString(),
          GsonUtil.stringListType);
    }
    String accessRights = null;
    accessRights = dataset.optString("accessRights");
    List<DctStandard> conformsTo = null;
    conformsTo = deserializeStandard(dataset, "conformsTo", nodeId);

    if (dataset.has("documentation")) {
      documentation = GsonUtil.json2Obj(dataset.getJSONArray("documentation").toString(),
          GsonUtil.stringListType);
    }

    if (dataset.has("relatedResource")) {
      relatedResource = GsonUtil.json2Obj(dataset.getJSONArray("relatedResource").toString(),
          GsonUtil.stringListType);
    }

    if (dataset.has("frequency")) {
      frequency = dataset.optString("frequency");
      if (!IRIFactory.iriImplementation().create(frequency).hasViolation(false)) {
        frequency = CommonUtil.extractFrequencyFromUri(frequency);
      }
    }

    if (dataset.has("hasVersion")) {
      hasVersion = GsonUtil.json2Obj(dataset.getJSONArray("hasVersion").toString(),
          GsonUtil.stringListType);
    }

    if (dataset.has("isVersionOf")) {
      isVersionOf = GsonUtil.json2Obj(dataset.getJSONArray("isVersionOf").toString(),
          GsonUtil.stringListType);
    }

    String landingPage = null;
    landingPage = dataset.optString("landingPage");

    if (dataset.has("language")) {
      language = GsonUtil.json2Obj(dataset.getJSONArray("language").toString(),
          GsonUtil.stringListType);
    }

    if (dataset.has("provenance")) {
      provenance = GsonUtil.json2Obj(dataset.getJSONArray("provenance").toString(),
          GsonUtil.stringListType);
    }

    if (dataset.has("releaseDate")) {
      releaseDate = CommonUtil.fixBadUtcDate(dataset.getString("releaseDate"));
    }

    if (dataset.has("updateDate")) {
      updateDate = CommonUtil.fixBadUtcDate(dataset.getString("updateDate"));
    }

    if (dataset.has("otherIdentifier")) {
      otherIdentifier = GsonUtil.json2Obj(dataset.getJSONArray("otherIdentifier").toString(),
          GsonUtil.stringListType);
    }

    if (dataset.has("sample")) {
      sample = GsonUtil.json2Obj(dataset.getJSONArray("sample").toString(),
          GsonUtil.stringListType);
    }

    if (dataset.has("source")) {
      source = GsonUtil.json2Obj(dataset.getJSONArray("source").toString(),
          GsonUtil.stringListType);
    }

    if (dataset.has("temporal")) {
      temporalCoverage = deserializeTemporal(dataset, nodeId);
    }
    String type = null;
    type = dataset.optString("type");

    String version = null;
    version = dataset.optString("version");

    if (dataset.has("versionNotes")) {
      versionNotes = GsonUtil.json2Obj(dataset.getJSONArray("versionNotes").toString(),
          GsonUtil.stringListType);
    }

    if (dataset.has("rightsHolder")) {
      rightsHolder = deserializeFoafAgent(dataset, "rightsHolder", DCTerms.rightsHolder, nodeId);
    }

    if (dataset.has("creator")) {
      creator = deserializeFoafAgent(dataset, "creator", DCTerms.creator, nodeId);
    }
    List<SkosConceptSubject> subjectList = null;
    subjectList = deserializeConcept(dataset, "subject", DCTerms.subject, nodeId,
        SkosConceptSubject.class);
    DctLocation spatialCoverage = null;
    spatialCoverage = deserializeSpatial(dataset, nodeId);

    if (dataset.has("distributions")) {
      JSONArray distrArray = dataset.getJSONArray("distributions");
      for (int i = 0; i < distrArray.length(); i++) {
        try {
          distributionList.add(distributionToDcat(distrArray.getJSONObject(i), nodeId));
        } catch (Exception e) {
          logger.info("There was an error while deserializing a Distribution: " + e.getMessage()
              + " - SKIPPED");
        }
      }
    }

    return new DcatDataset(nodeId, identifier, title, description, distributionList, themeList,
        publisher, contactPointList, keywords, accessRights, conformsTo, documentation, frequency,
        hasVersion, isVersionOf, landingPage, language, provenance, releaseDate, updateDate,
        otherIdentifier, sample, source, spatialCoverage, temporalCoverage, type, version,
        versionNotes, rightsHolder, creator, subjectList, relatedResource);

  }

  /**
   * Deserialize temporal.
   *
   * @param dataset the dataset
   * @param nodeId  the node id
   * @return the dct period of time
   */
  protected DctPeriodOfTime deserializeTemporal(JSONObject dataset, String nodeId) {

    try {
      JSONObject temporal = dataset.getJSONObject("temporal");
      return new DctPeriodOfTime(temporal.optString("uri"), temporal.optString("startDate"),
          temporal.optString("endDate"), nodeId);
    } catch (JSONException ignore) {
      logger.info("Temporal object not valid! - Skipped");
    }
    return null;
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
      JSONObject obj = dataset.getJSONObject(fieldName);
      return new FoafAgent(property.getURI(), obj.optString("resourceUri"), obj.optString("name"),
          obj.optString("mbox"), obj.optString("homepage"), obj.optString("type"),
          obj.optString("identifier"), nodeId);
    } catch (JSONException ignore) {
      logger.info("Agent object not valid! - Skipped");
    }
    return null;
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

    JSONArray conceptArray = obj.optJSONArray(fieldName);
    if (conceptArray != null) {
      for (int i = 0; i < conceptArray.length(); i++) {
        JSONObject themeObj = conceptArray.optJSONObject(i);

        if (themeObj != null) {

          List<SkosPrefLabel> prefLabelList = new ArrayList<SkosPrefLabel>();
          JSONArray labelArray = themeObj.optJSONArray("prefLabel");

          if (labelArray != null) {
            for (int j = 0; j < labelArray.length(); j++) {
              JSONObject labelObj = labelArray.optJSONObject(j);
              prefLabelList.add(new SkosPrefLabel(labelObj.optString("language"),
                  labelObj.optString("value"), nodeId));
            }
          }

          try {
            result.add(type.getDeclaredConstructor(SkosConcept.class).newInstance(new SkosConcept(
                property.getURI(), themeObj.optString("resourceUri"), prefLabelList, nodeId)));
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
   * Deserialize contact point.
   *
   * @param dataset  the dataset
   * @param property the property
   * @param nodeId   the node id
   * @return the list
   * @throws JSONException the JSON exception
   */
  protected List<VCardOrganization> deserializeContactPoint(JSONObject dataset, Property property,
      String nodeId) throws JSONException {

    List<VCardOrganization> contactPList = new ArrayList<VCardOrganization>();

    JSONArray contactArray = dataset.optJSONArray("contactPoint");
    if (contactArray != null) {
      for (int i = 0; i < contactArray.length(); i++) {
        JSONObject contactObj = contactArray.optJSONObject(i);
        if (contactObj != null) {
          contactPList
              .add(new VCardOrganization(property.getURI(), contactObj.optString("resourceUri"),
                  contactObj.optString("fn"), contactObj.optString("hasEmail"),
                  contactObj.optString("hasURL"), contactObj.optString("hasTelephoneValue"),
                  contactObj.optString("hasTelephoneType"), nodeId));
        }
      }
    }

    return contactPList;
  }

  /**
   * Deserialize standard.
   *
   * @param obj       the obj
   * @param fieldName the field name
   * @param nodeId    the node id
   * @return the list
   * @throws JSONException the JSON exception
   */
  protected List<DctStandard> deserializeStandard(JSONObject obj, String fieldName, String nodeId)
      throws JSONException {

    List<DctStandard> standardList = new ArrayList<DctStandard>();

    JSONArray standardArray = obj.optJSONArray(fieldName);
    if (standardArray != null) {
      for (int i = 0; i < standardArray.length(); i++) {
        JSONObject standardObj = standardArray.optJSONObject(i);

        if (standardObj != null) {
          List<String> referenceDocumentation = new ArrayList<String>();

          JSONArray referenceArray = standardObj.optJSONArray("prefLabel");
          if (referenceArray != null) {
            for (int j = 0; j < referenceArray.length(); j++) {
              referenceDocumentation.add(referenceArray.optString(j));
            }
          }

          standardList.add(new DctStandard(standardObj.optString("uri"),
              standardObj.optString("identifier"), standardObj.optString("title"),
              standardObj.optString("description"), referenceDocumentation, nodeId));
        }
      }
    }

    return standardList;
  }

  /**
   * Deserialize license.
   *
   * @param obj    the obj
   * @param nodeId the node id
   * @return the dct license document
   */
  protected DctLicenseDocument deserializeLicense(JSONObject obj, String nodeId) {

    try {
      JSONObject licenseObj = obj.getJSONObject("license");
      return new DctLicenseDocument(licenseObj.optString("uri"), licenseObj.optString("name"),
          licenseObj.optString("type"), licenseObj.optString("versionInfo"), nodeId);

    } catch (JSONException ignore) {
      logger.info("License object not valid! - Skipped");
    }

    return null;

  }

  /**
   * Deserialize spatial.
   *
   * @param dataset the dataset
   * @param nodeId  the node id
   * @return the dct location
   */
  protected DctLocation deserializeSpatial(JSONObject dataset, String nodeId) {

    try {
      JSONObject obj = dataset.getJSONObject("spatialCoverage");
      return new DctLocation(obj.optString("uri"), obj.optString("geographicalIdentifier"),
          obj.optString("geographicalName"), obj.optString("geometry"), nodeId);
    } catch (JSONException ignore) {
      logger.info("Spatial object not valid! - Skipped");
    }
    return null;
  }

  /**
   * Distribution to dcat.
   *
   * @param obj    the obj
   * @param nodeId the node id
   * @return the dcat distribution
   * @throws Exception the exception
   */
  protected DcatDistribution distributionToDcat(JSONObject obj, String nodeId) throws Exception {

    String accessUrl = null;
    String description = null;
    String releaseDate = null;
    String updateDate = null;
    String title = null;
    List<String> documentation = new ArrayList<String>();
    List<String> language = new ArrayList<String>();
    SkosConceptStatus status = null;

    title = obj.optString("title");
    accessUrl = obj.getString("accessURL");
    description = obj.optString("description");
    String mediaType = null;
    mediaType = obj.optString("mediaType");
    String format = null;
    format = obj.optString("format");
    String byteSize = null;
    byteSize = obj.optString("byteSize");

    if (obj.has("documentation")) {
      documentation = GsonUtil.json2Obj(obj.getJSONArray("documentation").toString(),
          GsonUtil.stringListType);
    }

    String downloadUrl = null;
    downloadUrl = obj.optString("downloadURL");

    if (obj.has("language")) {
      language = GsonUtil.json2Obj(obj.getJSONArray("language").toString(),
          GsonUtil.stringListType);
    }
    DctLicenseDocument license = null;
    license = deserializeLicense(obj, nodeId);

    List<DctStandard> linkedSchemas = null;
    linkedSchemas = deserializeStandard(obj, "linkedSchemas", nodeId);

    if (obj.has("releaseDate")) {
      releaseDate = CommonUtil.fixBadUtcDate(obj.getString("releaseDate"));
    }

    if (obj.has("updateDate")) {
      updateDate = CommonUtil.fixBadUtcDate(obj.getString("updateDate"));
    }
    String rights = null;
    rights = obj.optString("rights");
    try {
      status = deserializeConcept(obj, "status",
          ResourceFactory.createProperty("http://www.w3.org/ns/adms#status"), nodeId,
          SkosConceptStatus.class).get(0);
    } catch (IndexOutOfBoundsException e) {
      e.printStackTrace();
    }

    return new DcatDistribution(nodeId, accessUrl, description, format, license, byteSize, null,
        documentation, downloadUrl, language, linkedSchemas, mediaType, releaseDate, updateDate,
        rights, status, title);

  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.connectors.IodmsConnector#getDataset(java.lang.String)
   */
  @Override
  public DcatDataset getDataset(String datasetId) throws Exception {

    NativeClient c = new NativeClient(node);
    return datasetToDcat(c.getDataset(datasetId), node);

  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.connectors.IodmsConnector#getAllDatasets()
   */
  @Override
  public List<DcatDataset> getAllDatasets() throws Exception {

    ArrayList<DcatDataset> dcatDatasets = new ArrayList<DcatDataset>();
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
        dcatDatasets.add(datasetToDcat(dataset, node));
        dataset = null;

      } catch (Exception e) {
        logger.info("There was an error: " + e.getMessage() + " while deserializing Dataset - " + i
            + " - SKIPPED");
      }
    }

    datasetsArray = null;
    client = null;
    System.gc();

    return dcatDatasets;
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.connectors.IodmsConnector#getChangedDatasets(java.util.List,
   * java.lang.String)
   */
  @Override
  public OdmsSynchronizationResult getChangedDatasets(List<DcatDataset> oldDatasets,
      String startingDateString) throws Exception {

    ArrayList<DcatDataset> newDatasets = (ArrayList<DcatDataset>) getAllDatasets();
    OdmsSynchronizationResult syncrhoResult = new OdmsSynchronizationResult();

    ImmutableSet<DcatDataset> newSets = ImmutableSet.copyOf(newDatasets);
    ImmutableSet<DcatDataset> oldSets = ImmutableSet.copyOf(oldDatasets);

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
    SetView<DcatDataset> diff = Sets.difference(newSets, oldSets);
    logger.info("New Package " + diff.size());
    for (DcatDataset d : diff) {
      syncrhoResult.addToAddedList(d);
      added++;
    }

    /*
     * Find removed datasets difference(present,current)
     */
    SetView<DcatDataset> diff1 = Sets.difference(oldSets, newSets);
    logger.info("Deleted Package " + diff1.size());
    for (DcatDataset d : diff1) {
      syncrhoResult.addToDeletedList(d);
      deleted++;
    }

    /*
     * Find updated datasets intersection(present,current)
     */

    SetView<DcatDataset> intersection = Sets.intersection(newSets, oldSets);
    logger.fatal("Intersection Package " + intersection.size());

    GregorianCalendar oldDate = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
    oldDate.setLenient(false);
    GregorianCalendar newDate = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
    oldDate.setLenient(false);
    SimpleDateFormat socrataDateF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    SimpleDateFormat dcatDateF = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);

    int exception = 0;
    for (DcatDataset d : intersection) {
      try {
        int oldIndex = oldDatasets.indexOf(d);
        int newIndex = newDatasets.indexOf(d);

        oldDate.setTime(dcatDateF.parse(oldDatasets.get(oldIndex).getUpdateDate().getValue()));
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

  /*
   * (non-Javadoc)
   * 
   * @see
   * it.eng.idra.connectors.IodmsConnector#countSearchDatasets(java.util.HashMap)
   */
  @Override
  public int countSearchDatasets(HashMap<String, Object> searchParameters) throws Exception {

    ArrayList<DcatDataset> dcatResults = new ArrayList<DcatDataset>();
    String sort = "";
    String rows = "";
    String offset = "";

    if (searchParameters.containsKey("sort")) {
      sort = (String) searchParameters.get("sort");
    }
    if (searchParameters.containsKey("rows")) {
      rows = (String) searchParameters.get("rows");
    }
    if (searchParameters.containsKey("start")) {
      offset = (String) searchParameters.get("start");
    }

    logger.info("-- NATIVE Connector Request sent --" + "ROWS: " + rows);
    String query = "";
    query = buildQueryNativeString(searchParameters);

    NativeClient c = new NativeClient(node);

    JSONObject resultJson = c.findDatasets(query, sort, rows, offset);
    int count = resultJson.getInt("count");
    logger.info("-- NATIVE Connector Response - Result count:" + resultJson.optInt("count"));

    // if (results != null)
    // for (int i = 0; i < results.length(); i++) {
    // JSONObject obj = results.getJSONObject(i);
    // dcatResults.add(datasetToDCAT(obj, node));
    // obj = null;
    // }

    c = null;
    return count;

  }

  /**
   * Builds the query native string.
   *
   * @param searchParameters the search parameters
   * @return the string
   */
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
          if (isFirst) {
            query += tmp;
          } else {
            query += " " + defaultOperator + " " + tmp;
          }
        } else {
          query += "";
        }

        isFirst = false;
      } else if (key.equals("tags")) {

        if (isFirst) {
          query += "tags" + ":" + "(" + ((String) value).replace(",", " " + defaultOperator + " ")
              + ")";
        } else {
          query += " OR tags" + ":" + "("
              + ((String) value).replace(",", " " + defaultOperator + " ") + ")";
        }
        isFirst = false;

      } else if (!key.equals("sort") && !key.equals("rows") && !key.equals("start")) {

        String tmp = ((String) value).replaceAll(",", " " + defaultOperator + " ");

        if (isFirst) {
          query += key + ":( " + tmp + " )";
        } else {
          query += " " + defaultOperator + " " + key + ":( " + tmp + " )";
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

    System.out.println(query);

    return query;
  }

}
