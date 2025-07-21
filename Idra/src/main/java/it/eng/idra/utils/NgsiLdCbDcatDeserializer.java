/*******************************************************************************
 * Idra - Open Data Federation Platform
 * Copyright (C) 2025 Engineering Ingegneria Informatica S.p.A.
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

package it.eng.idra.utils;

import it.eng.idra.beans.dcat.DCATAP;
import it.eng.idra.beans.dcat.DcatDataset;
import it.eng.idra.beans.dcat.DcatDatasetSeries;
import it.eng.idra.beans.dcat.DcatDetails;
import it.eng.idra.beans.dcat.DcatDistribution;
import it.eng.idra.beans.dcat.DcatProperty;
import it.eng.idra.beans.dcat.DctLicenseDocument;
import it.eng.idra.beans.dcat.DctLocation;
import it.eng.idra.beans.dcat.DctPeriodOfTime;
import it.eng.idra.beans.dcat.DctStandard;
import it.eng.idra.beans.dcat.FoafAgent;
import it.eng.idra.beans.dcat.Relationship;
import it.eng.idra.beans.dcat.SkosConcept;
import it.eng.idra.beans.dcat.SkosConceptSubject;
import it.eng.idra.beans.dcat.SkosConceptTheme;
import it.eng.idra.beans.dcat.SkosPrefLabel;
import it.eng.idra.beans.dcat.VcardOrganization;
import it.eng.idra.beans.odms.OdmsCatalogue;
import it.eng.idra.connectors.NgsiLdCbDcatConnector;
import it.eng.idra.utils.restclient.RestClient;
import it.eng.idra.utils.restclient.RestClientImpl;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.SKOS;
import org.apache.jena.vocabulary.XSD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

// TODO: Auto-generated Javadoc
/**
 * The Class OrionDcatDeserializer.
 */
public class NgsiLdCbDcatDeserializer {
  private static Logger logger = LogManager.getLogger(NgsiLdCbDcatConnector.class);

  // private static Logger logger =
  // LogManager.getLogger(OrionDcatConnector.class);

  /**
   * Gets Dataset From Json.
   *
   * @return the DcatDataset
   * 
   * @throws Exception exception
   */
  public static DcatDataset getDatasetFromJson(String returnedJson,
      OdmsCatalogue node) throws Exception {

    JSONArray datasetsArray = new JSONArray(returnedJson);
    JSONObject dataset = datasetsArray.getJSONObject(0);
    DcatDataset dcatDataset = datasetToDcat(dataset, node);
    logger.info("fine dataset to dcat");
    return dcatDataset;
  }

  /**
   * Gets DcatDataset From an Object dataset.
   *
   * @return the DcatDataset
   * 
   * @throws Exception exception
   */
  public static DcatDataset datasetToDcat(Object dataset, OdmsCatalogue node) throws Exception {
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
    // List<String> relatedResource = new ArrayList<String>();

    List<DcatDistribution> distributionList = new ArrayList<DcatDistribution>();

    // Initialize new fields
    List<String> applicableLegislation = new ArrayList<String>();
    List<DcatDatasetSeries> inSeries = new ArrayList<DcatDatasetSeries>();
    List<Relationship> qualifiedRelation = new ArrayList<Relationship>();
    String temporalResolution = null;
    List<String> wasGeneratedBy = new ArrayList<String>();
    List<String> HVDCategory = new ArrayList<String>();
    List<DctLocation> geographicalCoverage = new ArrayList<DctLocation>();
    List<DcatDetails> titles = new ArrayList<DcatDetails>();
    List<DcatDetails> descriptions = new ArrayList<DcatDetails>();
    List<DctPeriodOfTime> temporalCoverageList = new ArrayList<DctPeriodOfTime>();
    DcatDetails dcatDetails = new DcatDetails();

    JSONObject titleObject = j.getJSONObject("title");
    title = titleObject.getString("value");
    dcatDetails.setTitle(title);

    identifier = j.optString("id", null);

    if (j.has("description")) {
      JSONObject desObject = j.getJSONObject("description");
      description = desObject.getString("value");
      dcatDetails.setDescription(description);
    }

    // Handle titles
    titles.add(dcatDetails);
    // Handle descriptions
    descriptions.add(dcatDetails);

    // landingPage
    String landingPage = "";
    if (j.has("landingPage")) {
      JSONObject lanPageObject = j.getJSONObject("landingPage");
      landingPage = lanPageObject.getString("value");
    }

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
              SkosConceptTheme.class, node));
        }
      } else {
        themes.add(themeObject.getString("value"));
        themeList.addAll(extractConceptList(DCAT.theme.getURI(), themes,
            SkosConceptTheme.class, node));
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

    // Languages
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
    type = j.optString("type", null); // ?

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
                    "", "", "", String.valueOf(node.getId())));// identifier
          }
        }
      } else {
        contactPointList.add(
            new VcardOrganization(DCAT.contactPoint.getURI(),
                null, "", contObject.getString("value"),
                "", "", "", String.valueOf(node.getId())));// identifier
      }
    }

    FoafAgent publisher = null;
    FoafAgent creator = null;
    // Publisher
    if (j.has("publisher")) {
      JSONObject pub = j.getJSONObject("publisher");
      publisher = new FoafAgent(DCTerms.publisher.getURI(), "",
          Collections.singletonList(pub.getString("value")), "", "", null,
          "", String.valueOf(node.getId()));
    }
    // Creator
    if (j.has("creator")) {
      JSONObject creat = j.getJSONObject("creator");
      creator = new FoafAgent(DCTerms.creator.getURI(), "",
          Collections.singletonList(creat.getString("value")), "", "", null,
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
    String beginning = null;
    String end = null;
    if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate) &&
        StringUtils.isNotBlank(beginning) && StringUtils.isNotBlank(end)) {
      temporalCoverage = new DctPeriodOfTime(DCTerms.temporal.getURI(), startDate, endDate,
          String.valueOf(node.getId()), beginning, end);// identifier
      // Handle temporalCoverage
      temporalCoverageList.add(temporalCoverage);
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
        if (!distributionsId.get(i).equals("")) {
          DcatDistribution distro = distributionToDcat(getJsonDistribution(distributionsId.get(i),
              node),
              node);
          // logger.info(distro);
          distributionList.add(distro);

        }
      }

    }
    FoafAgent rightsHolder = null;
    // spatialCoverage e conformsTo non sono previsti nello smart data model
    // DCATAP/NGSILD
    // if (j.has("sample")) {
    // sample = GsonUtil.json2Obj(j.getJSONArray("sample").toString(),
    // GsonUtil.stringListType);
    // }
    //
    // if (j.has("source")) {
    // source = GsonUtil.json2Obj(j.getJSONArray("source").toString(),
    // GsonUtil.stringListType);
    // }
    // if (j.has("subject")) {
    // List<String> subjects =
    // GsonUtil.json2Obj(j.getJSONArray("subject").toString(),
    // GsonUtil.stringListType);
    // if (subjects.size() > 0) {
    // subjectList.addAll(
    // extractConceptList(DCTerms.subject.getURI(), subjects,
    // SkosConceptSubject.class));
    // }
    // }

    // if (j.has("relatedResource")) {
    // relatedResource =
    // GsonUtil.json2Obj(j.getJSONArray("relatedResource").toString(),
    // GsonUtil.stringListType);
    // }
    //
    // if (j.has("hasVersion")) {
    // hasVersion = GsonUtil.json2Obj(j.getJSONArray("hasVersion").toString(),
    // GsonUtil.stringListType);
    // }
    //
    // if (j.has("isVersionOf")) {
    // isVersionOf = GsonUtil.json2Obj(j.getJSONArray("isVersionOf").toString(),
    // GsonUtil.stringListType);
    // }
    // spatialCoverage e conformsTo non sono previsti nello smart data model
    // DCATAP(NGSILD

    // new
    // Extract "applicableLegislation"
    if (j.has("applicableLegislation")) {
      applicableLegislation = GsonUtil.json2Obj(j.getJSONArray("applicableLegislation").toString(),
          GsonUtil.stringListType);
      /*
       * JSONObject legislationObject = j.getJSONObject("applicableLegislation");
       * if (legislationObject.get("value") instanceof JSONArray) {
       * values = legislationObject.getJSONArray("value");
       * if (values.length() > 0) {
       * applicableLegislation = GsonUtil.json2Obj(values.toString(),
       * GsonUtil.stringListType);
       * }
       * } else {
       * applicableLegislation.add(legislationObject.getString("value"));
       * }
       */
    }

    // Extract "geographicalCoverage", chech if it is ok
    /*
     * if (j.has("geographicalCoverage") || j.has("spatialCoverage")) {
     * JSONObject geoObj = j.getJSONObject("geographicalCoverage");
     * if (geoObj == null)
     * j.getJSONObject("spatialCoverage");
     * 
     * DctLocation location = new DctLocation(geoObj.optString("uri"),
     * geoObj.optString("geographicalIdentifier"),
     * geoObj.optString("geographicalName"), geoObj.optString("geometry"),
     * String.valueOf(node.getId()),
     * geoObj.optString("bbox"), geoObj.optString("centroid"), identifier);
     * geographicalCoverage.add(location);
     * }
     */

    // Extract "geographicalCoverage"
    if (j.has("geographicalCoverage") || j.has("spatialCoverage")) {
      JSONArray geographicalCoverageArray = j.optJSONArray("geographicalCoverage");
      if (geographicalCoverageArray == null)
        geographicalCoverageArray = j.optJSONArray("spatialCoverage");
      if (geographicalCoverageArray != null) {
        for (int i = 0; i < geographicalCoverageArray.length(); i++) {
          JSONObject geoObj = geographicalCoverageArray.getJSONObject(i);
          DctLocation location = new DctLocation(geoObj.optString("uri"), geoObj.optString("geographicalIdentifier"),
              geoObj.optString("geographicalName"), geoObj.optString("geometry"), String.valueOf(node.getId()),
              geoObj.optString("bbox"), geoObj.optString("centroid"));

          geographicalCoverage.add(location);
        }
      }
    }

    // Extract "inSeries"
    /*
     * if (j.has("inSeries")) {
     * JSONArray seriesArray = j.optJSONArray("inSeries");
     * // inSeries = new ArrayList<>();
     * 
     * if (seriesArray != null) {
     * for (int i = 0; i < seriesArray.length(); i++) {
     * // Simplified placeholder logic: you may want to parse real data from JSON
     * inSeries.add(new DcatDatasetSeries(
     * applicableLegislation,
     * contactPointList,
     * descriptions,
     * frequency,
     * geographicalCoverage,
     * updateDate,
     * publisher,
     * releaseDate,
     * temporalCoverageList,
     * titles,
     * String.valueOf(node.getId()),
     * identifier));
     * }
     * } else {
     * // fallback for single object
     * inSeries.add(new DcatDatasetSeries(
     * applicableLegislation,
     * contactPointList,
     * descriptions,
     * frequency,
     * geographicalCoverage,
     * updateDate,
     * publisher,
     * releaseDate,
     * temporalCoverageList,
     * titles,
     * String.valueOf(node.getId()),
     * identifier));
     * }
     * }
     * 
     * // Extract "qualifiedRelation"
     * if (j.has("qualifiedRelation")) {
     * JSONArray relationArray = j.optJSONArray("qualifiedRelation");
     * if (relationArray != null) {
     * for (int i = 0; i < relationArray.length(); i++) {
     * JSONObject relObj = relationArray.getJSONObject(i);
     * String hadRole = relObj.optString("had_role", null);
     * String relation = relObj.optString("relation", null);
     * 
     * Relationship r = new Relationship(hadRole,
     * relation,String.valueOf(node.getId()));
     * qualifiedRelation.add(r);
     * }
     * } else {
     * JSONObject singleRelation = j.getJSONObject("qualifiedRelation");
     * String hadRole = singleRelation.optString("had_role", null);
     * String relation = singleRelation.optString("relation", null);
     * 
     * Relationship r = new Relationship(hadRole,
     * relation,String.valueOf(node.getId()));
     * qualifiedRelation.add(r);
     * }
     * }
     */

    // Extract "temporalResolution"
    if (j.has("temporalResolution")) {
      // JSONObject tempResObject = j.getJSONObject("temporalResolution");
      // temporalResolution = tempResObject.getString("value");
      temporalResolution = j.optString("temporalResolution");
    }

    // Extract "wasGeneratedBy"
    if (j.has("wasGeneratedBy")) {
      wasGeneratedBy = GsonUtil.json2Obj(j.getJSONArray("wasGeneratedBy").toString(),
          GsonUtil.stringListType);
      /*
       * JSONObject generatedByObject = j.getJSONObject("wasGeneratedBy");
       * if (generatedByObject.get("value") instanceof JSONArray) {
       * values = generatedByObject.getJSONArray("value");
       * if (values.length() > 0) {
       * wasGeneratedBy = GsonUtil.json2Obj(values.toString(),
       * GsonUtil.stringListType);
       * }
       * } else {
       * wasGeneratedBy.add(generatedByObject.getString("value"));
       * }
       */
    }

    // Extract "HVDCategory"
    if (j.has("HVDCategory")) {
      HVDCategory = GsonUtil.json2Obj(j.getJSONArray("HVDCategory").toString(),
          GsonUtil.stringListType);
      /*
       * JSONObject hvdObject = j.getJSONObject("HVDCategory");
       * if (hvdObject.get("value") instanceof JSONArray) {
       * values = hvdObject.getJSONArray("value");
       * if (values.length() > 0) {
       * HVDCategory = GsonUtil.json2Obj(values.toString(), GsonUtil.stringListType);
       * }
       * } else {
       * HVDCategory.add(hvdObject.getString("value"));
       * }
       */
    }

    return new DcatDataset(String.valueOf(node.getId()), identifier, title, description,
        distributionList, themeList,
        publisher, contactPointList, keywords, accessRights, conformsTo, documentation, frequency,
        hasVersion, isVersionOf, landingPage, language, provenance, releaseDate, updateDate,
        otherIdentifier, sample, source, geographicalCoverage, temporalCoverageList, type, version,
        versionNotes, rightsHolder, creator, subjectList, null, applicableLegislation,
        inSeries, qualifiedRelation, temporalResolution, wasGeneratedBy, HVDCategory);
  }

  /**
   * Gets the json distributions.
   *
   * @return the json distributions
   * 
   * @throws Exception exception
   */
  private static JSONObject getJsonDistribution(String id, OdmsCatalogue node)
      throws Exception {

    Map<String, String> headers = new HashMap<String, String>();
    headers.put("Content-Type", "application/json");
    RestClient client = new RestClientImpl();
    String idDistr[] = id.split(":");
    String idDistribution = "";
    for (int i = 4; i < idDistr.length; i++) {
      if (i != idDistr.length - 1) {
        idDistribution = idDistribution.concat(idDistr[i]).concat(":");
      } else {
        idDistribution = idDistribution.concat(idDistr[i]);
      }

    }
    // logger.info(idDistribution);
    String distribId = "urn:ngsi-ld:DistributionDCAT-AP:id:" + idDistribution;

    String url = node.getHost() + "/ngsi-ld/v1/entities?type=DistributionDCAT-AP&id=" + distribId;
    // logger.info(url);

    HttpResponse response = client.sendGetRequest(url, headers);
    String returnedJson = client.getHttpResponseBody(response);

    // logger.info(returnedJson);
    JSONArray jsonArr = new JSONArray(returnedJson);
    // logger.info(jsonArr);
    JSONObject jsonObject = jsonArr.getJSONObject(0);
    // logger.info(jsonObject);
    return jsonObject;
  }

  /**
   * Gets a dcat distribution.
   *
   * @return the dcat distribution
   * 
   * @throws Exception exception
   */
  public static DcatDistribution distributionToDcat(Object distribution,
      OdmsCatalogue node) throws Exception {

    JSONObject j = (JSONObject) distribution;
    DcatDistribution distro = new DcatDistribution();

    distro.setIdentifier(j.getString("id"));

    distro.setNodeId(String.valueOf(node.getId()));
    logger.info("id distribution settato");
    String title = null;
    JSONObject titleObject = j.getJSONObject("title");
    title = titleObject.getString("value");
    distro.setTitle(title);

    distro.setDescription("description");

    logger.info("title settato");

    String accessUrl = null;
    if (j.has("accessUrl")) {
      JSONObject accessObject = j.getJSONObject("accessUrl");
      accessUrl = accessObject.getString("value");
      distro.setAccessUrl(accessUrl);
    }

    String downloadUrl = null;
    if (j.has("downloadURL")) {
      JSONObject downloadObject = j.getJSONObject("downloadURL");
      downloadUrl = downloadObject.getString("value");
      distro.setDownloadUrl(downloadUrl);
    }

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
          "", "", String.valueOf(node.getId())));
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

    // add new properties
    if (j.has("accessService")) {
      distro.setAccessService(GsonUtil.json2Obj(j.getJSONArray("accessService").toString(),
          GsonUtil.dataServiceListType));
    }

    if (j.has("applicableLegislation")) {
      distro.setApplicableLegislation(GsonUtil.json2Obj(j.getJSONArray("applicableLegislation").toString(),
          GsonUtil.stringListType));
    }

    if (j.has("availability")) {
      distro.setAvailability(new DcatProperty(DCATAP.availability, SKOS.Concept, j.getString("availability")));
    }
    if (j.has("compressionFormat")) {
      distro.setCompressionFormat(
          new DcatProperty(DCAT.compressFormat, DCTerms.MediaType, j.getString("compressionFormat")));
    }
    if (j.has("hasPolicy")) {
      distro.setHasPolicy(
          new DcatProperty(ResourceFactory.createProperty("https://www.w3.org/ns/odrl/2/"), DCTerms.Policy,
              j.getString("hasPolicy")));
    }
    if (j.has("packagingFormat")) {
      distro.setPackagingFormat(
          new DcatProperty(DCAT.packageFormat, DCTerms.MediaType, j.getString("packagingFormat")));
    }
    if (j.has("spatialResolution")) {
      distro.setSpatialResolution(
          new DcatProperty(DCAT.spatialResolutionInMeters, XSD.decimal, j.getString("spatialResolution")));
    }
    if (j.has("temporalResolution")) {
      distro.setTemporalResolution(
          new DcatProperty(DCAT.temporalResolution, XSD.duration, j.getString("temporalResolution")));
    }

    // if (j.has("status")) {
    // logger.info("Distribution status skipped");
    // }
    // if (j.has("language")) {
    // logger.info("Distribution language skipped");
    // }
    // if (j.has("linkedSchemas")) {
    // logger.info("Distribution linkedSchemas skipped");
    // }
    // if (j.has("documentation")) {
    // logger.info("Distribution documentation skipped");
    // }
    logger.info(distro);
    return distro;
  }

  /**
   * Extract concept list.
   *
   * @param <T>         the generic type
   * @param propertyUri the property uri
   * @param concepts    the concepts
   * @param type        the type
   * @return the list
   */
  public static <T extends SkosConcept> List<T> extractConceptList(String propertyUri,
      List<String> concepts, Class<T> type, OdmsCatalogue node) {
    List<T> result = new ArrayList<T>();

    for (String label : concepts) {
      try {
        result.add(type.getDeclaredConstructor(SkosConcept.class).newInstance(new SkosConcept(
            propertyUri, "", Arrays.asList(new SkosPrefLabel("", label,
                String.valueOf(node.getId()))),
            String.valueOf(node.getId()))));
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
          | InvocationTargetException | NoSuchMethodException | SecurityException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    return result;
  }

}
