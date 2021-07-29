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
import it.eng.idra.beans.dcat.VCardOrganization;
import it.eng.idra.beans.odms.OdmsCatalogue;
import it.eng.idra.beans.odms.OdmsSynchronizationResult;
import it.eng.idra.beans.sparql.SparqlCatalogueConfiguration;
import it.eng.idra.beans.sparql.SparqlDistributionConfig;
import it.eng.idra.management.OdmsManager;
import it.eng.idra.utils.CommonUtil;
import it.eng.idra.utils.GsonUtil;
import it.eng.idra.utils.PropertyManager;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class SparqlConnector implements IodmsConnector {

  private String nodeId;
  private OdmsCatalogue node;
  // The internal API used in case the query must be authenticated or if headers
  // has to be set
  private static String orionFilePath = 
      PropertyManager.getProperty(IdraProperty.ORION_FILE_DUMP_PATH);
  private static Logger logger = LogManager.getLogger(SparqlConnector.class);

  public SparqlConnector() {
  }

  public SparqlConnector(OdmsCatalogue node) {
    this.node = node;
    this.nodeId = String.valueOf(node.getId());
  }

  @Override
  public List<DcatDataset> findDatasets(HashMap<String, Object> searchParameters) throws Exception {
    ArrayList<DcatDataset> resultDatasets = new ArrayList<DcatDataset>();
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
  public DcatDataset datasetToDcat(Object dataset, OdmsCatalogue node) throws Exception {
    JSONObject j = (JSONObject) dataset;

    String title = null, description = null, accessRights = null, frequency = null, landingPage = null,
        releaseDate = null, updateDate = null, identifier = null, type = null, version = null;

    String publisherIdentifier = null, publisherUri = null, publisherName = null, publisherMbox = null,
        publisherHomepage = null, publisherType = null;
    String holderIdentifier = null, holderUri = null, holderName = null, holderMbox = null, holderHomepage = null,
        holderType = null;
    String creatorIdentifier = null, creatorUri = null, creatorName = null, creatorMbox = null, creatorHomepage = null,
        creatorType = null;
    String startDate = null, endDate = null;
    String vCardUri = null, vCardFn = null, vCardHasEmail = null;
    List<DctStandard> conformsTo = new ArrayList<DctStandard>();
    FoafAgent publisher = null, rightsHolder = null, creator = null;
    List<VCardOrganization> contactPointList = new ArrayList<VCardOrganization>();
    DctPeriodOfTime temporalCoverage = null;
    DctLocation spatialCoverage = null;
    DctLicenseDocument license = null;
    String geographicalIdentifier = null, geographicalName = null, geometry = null;
    List<SkosConceptTheme> themeList = new ArrayList<SkosConceptTheme>();
    List<SkosConceptSubject> subjectList = null;
    List<String> keywords = new ArrayList<String>(), documentation = new ArrayList<String>(),
        hasVersion = new ArrayList<String>(), isVersionOf = new ArrayList<String>(), language = new ArrayList<String>(),
        provenance = new ArrayList<String>(), otherIdentifier = new ArrayList<String>(),
        sample = new ArrayList<String>(), source = new ArrayList<String>(), versionNotes = new ArrayList<String>(),
        relatedResource = new ArrayList<String>();

    List<DcatDistribution> distributionList = new ArrayList<DcatDistribution>();

    title = j.optString("title", null);
    description = j.optString("description", null);
    identifier = UUID.randomUUID().toString();

    // Themes
    if (j.has("theme")) {
      List<String> themes = 
          GsonUtil.json2Obj(j.getJSONArray("theme").toString(), GsonUtil.stringListType);
      if (themes.size() > 0) {
        themeList.addAll(extractConceptList(DCAT.theme.getURI(), themes, SkosConceptTheme.class));
      }
    }

    // Subject
    if (j.has("subject")) {
      List<String> subjects = 
          GsonUtil.json2Obj(j.getJSONArray("subject").toString(), GsonUtil.stringListType);
      if (subjects.size() > 0) {
        subjectList.addAll(
            extractConceptList(DCTerms.subject.getURI(), subjects, SkosConceptSubject.class));
      }
    }

    // Publisher
    if (j.has("publisher")) {
      JSONObject pub = j.getJSONObject("publisher");
      if (pub.has("name") || pub.has("mbox") 
          || pub.has("homepage") || pub.has("type") || pub.has("identifier")
          || pub.has("propertyUri")) {
        publisher = new FoafAgent(DCTerms.publisher.getURI(), pub.optString("propertyUri", null),
            pub.optString("name", null), 
            pub.optString("mbox", null), pub.optString("homepage", null),
            pub.optString("type", null), pub.optString("identifier", null), nodeId);
      }
    }

    // Creator
    if (j.has("creator")) {
      JSONObject pub = j.getJSONObject("creator");
      if (pub.has("name") || pub.has("mbox") 
          || pub.has("homepage") || pub.has("type") || pub.has("identifier")
          || pub.has("propertyUri")) {
        rightsHolder = new FoafAgent(DCTerms.rightsHolder.getURI(), 
            pub.optString("propertyUri", null),
            pub.optString("name", null), 
            pub.optString("mbox", null), pub.optString("homepage", null),
            pub.optString("type", null), pub.optString("identifier", null), nodeId);
      }
    }

    // RightsHolder
    if (j.has("rightsHolder")) {
      JSONObject pub = j.getJSONObject("rightsHolder");
      if (pub.has("name") || pub.has("mbox") 
          || pub.has("homepage") || pub.has("type") || pub.has("identifier")
          || pub.has("propertyUri")) {
        creator = new FoafAgent(DCTerms.creator.getURI(), pub.optString("propertyUri", null),
            pub.optString("name", null), 
            pub.optString("mbox", null), pub.optString("homepage", null),
            pub.optString("type", null), pub.optString("identifier", null), nodeId);
      }
    }

    // Keywords
    if (j.has("keywords")) {
      keywords = GsonUtil.json2Obj(j.getJSONArray("keywords").toString(), GsonUtil.stringListType);
    }

    // List<VCardOrganization>
    if (j.has("contactPoint")) {
      JSONArray tmpArr = j.getJSONArray("contactPoint");
      for (int i = 0; i < tmpArr.length(); i++) {
        JSONObject tmp = tmpArr.getJSONObject(i);
        if (tmp.has("resourceUri") || tmp.has("fn") 
            || tmp.has("hasEmail") || tmp.has("hasURL")
            || tmp.has("hasTelephoneValue") || tmp.has("hasTelephoneType")) {
          contactPointList.add(new VCardOrganization(DCAT.contactPoint.getURI(),
              tmp.optString("resourceUri", null),
              tmp.optString("fn", null), tmp.optString("hasEmail", null),
              tmp.optString("hasURL", null),
              tmp.optString("hasTelephoneValue", null),
              tmp.optString("hasTelephoneType", null), nodeId));
        }
      }
    }

    // List<DCATStandard> conformsTo
    if (j.has("conformsTo")) {
      JSONArray tmpArr = j.getJSONArray("conformsTo");
      for (int i = 0; i < tmpArr.length(); i++) {
        JSONObject tmp = tmpArr.getJSONObject(i);
        if (tmp.has("identifier") || tmp.has("title") 
            || tmp.has("description") || tmp.has("referenceDocumentation")) {
          conformsTo.add(new DctStandard(DCTerms.conformsTo.getURI(), 
              tmp.optString("identifier", null),
              tmp.optString("title", null), tmp.optString("description", null),
              GsonUtil.json2Obj(j.getJSONArray("referenceDocumentation").toString(), 
                  GsonUtil.stringListType), nodeId));
        }
      }
    }

    // Documentation
    if (j.has("documentation")) {
      documentation = GsonUtil.json2Obj(
          j.getJSONArray("documentation").toString(), GsonUtil.stringListType);
    }

    if (j.has("relatedResource")) {
      relatedResource = GsonUtil.json2Obj(
          j.getJSONArray("relatedResource").toString(), GsonUtil.stringListType);
    }

    if (j.has("hasVersion")) {
      hasVersion = GsonUtil.json2Obj(
          j.getJSONArray("hasVersion").toString(), GsonUtil.stringListType);
    }

    if (j.has("isVersionOf")) {
      isVersionOf = GsonUtil.json2Obj(
          j.getJSONArray("isVersionOf").toString(), GsonUtil.stringListType);
    }

    if (j.has("language")) {
      language = GsonUtil.json2Obj(j.getJSONArray("language").toString(), GsonUtil.stringListType);
    }

    if (j.has("provenance")) {
      provenance = GsonUtil.json2Obj(
          j.getJSONArray("provenance").toString(), GsonUtil.stringListType);
    }

    if (j.has("otherIdentifier")) {
      otherIdentifier = GsonUtil.json2Obj(
          j.getJSONArray("otherIdentifier").toString(), GsonUtil.stringListType);
    }

    if (j.has("sample")) {
      sample = GsonUtil.json2Obj(j.getJSONArray("sample").toString(), GsonUtil.stringListType);
    }

    if (j.has("source")) {
      source = GsonUtil.json2Obj(j.getJSONArray("source").toString(), GsonUtil.stringListType);
    }

    if (j.has("versionNotes")) {
      versionNotes = GsonUtil.json2Obj(
          j.getJSONArray("versionNotes").toString(), GsonUtil.stringListType);
    }

    accessRights = j.optString("accessRight", null);
    landingPage = j.optString("landingPage", null);
    type = j.optString("type", null);
    version = j.optString("version", null);
    frequency = j.optString("frequency", null);

    if (j.has("releaseDate")) {
      releaseDate = CommonUtil.fixBadUtcDate(j.getString("releaseDate"));
    }
    if (j.has("updateDate")) {
      updateDate = CommonUtil.fixBadUtcDate(j.getString("updateDate"));
    }

    if (j.has("spatialCoverage")) {
      spatialCoverage = new DctLocation(DCTerms.spatial.getURI(),
          j.getJSONObject("spatialCoverage").optString("geographicalIdentifier", null),
          j.getJSONObject("spatialCoverage").optString("geographicalName", null),
          j.getJSONObject("spatialCoverage").optString("geometry", null), nodeId);
    }

    if (StringUtils.isNotBlank(startDate) 
        && StringUtils.isNotBlank(endDate)) {
      temporalCoverage = new DctPeriodOfTime(DCTerms.temporal.getURI(), startDate, endDate, nodeId);
    }

    // Distribution
    if (j.has("distributions")) {
      JSONArray tmpArr = j.getJSONArray("distributions");
      if (tmpArr.length() == 0) {
        throw new Exception("Orion Dataset must contain at least one distribution");
      } else {
        for (int i = 0; i < tmpArr.length(); i++) {
          // TODO: Gestione formati da stringa
          JSONObject tmp = tmpArr.getJSONObject(i);
          if (tmp.has("distributionAdditionalConfig")) {
            JSONObject o = tmp.getJSONObject("distributionAdditionalConfig");
            if (!o.has("query") || StringUtils.isBlank(o.optString("query", null))) {
              throw new Exception(
                  "Each distribution must have the sparqlDistributionConfig with a query");
            }
            SparqlDistributionConfig conf = new SparqlDistributionConfig();
            conf.setQuery(o.getString("query"));
            conf.setNodeID(nodeId);
            conf.setFormats(o.optString("formats", ""));
            // TODO: add validation for query
            List<String> formats = 
                GsonUtil.json2Obj(o.optString("formats", ""), GsonUtil.stringListType);

            if (formats == null || formats.size() == 0) {
              distributionList.add(getSparqlDistribution(tmp, "sparql", conf));
            } else {
              for (String f : formats) {
                distributionList.add(getSparqlDistribution(tmp, f, conf));
              }
            }

          }
        }
      }
    } else {
      throw new Exception("Sparql Dataset must contain at least one distribution");
    }

    return new DcatDataset(nodeId, identifier,
        title, description, distributionList, themeList, publisher,
        contactPointList, keywords, accessRights,
        conformsTo, documentation, frequency, hasVersion, isVersionOf,
        landingPage, language, provenance, 
        releaseDate, updateDate, otherIdentifier, sample, source, spatialCoverage,
        temporalCoverage, type, version, 
        versionNotes, rightsHolder, creator, subjectList, relatedResource);
  }

  /**
   * Gets the sparql distribution.
   *
   * @param tmp the tmp
   * @param format the format
   * @param c the c
   * @return the sparql distribution
   */
  public DcatDistribution getSparqlDistribution(JSONObject tmp,
      String format, SparqlDistributionConfig c) {
    DcatDistribution distro = new DcatDistribution();
    distro.setNodeID(nodeId);
    // downloadURL e accessURL vengono settati dal metadata cache manager
    distro.setFormat(format);

    String query = "";
    boolean isFormat = false;
    if (!format.toLowerCase().equals("sparql")) {
      isFormat = true;
      if (c.getQuery().contains("format=")) {
        Pattern p = Pattern.compile("(format=)([^&]+)(&*)");
        Matcher m = p.matcher(c.getQuery());
        query = m.replaceAll("$1" + format + "$3");
      } else {
        query = c.getQuery() + "&format=" + format;
      }
    } else {
      query = c.getQuery();
    }

    String url = node.getHost() + "?" + query;

    distro.setAccessURL(url);
    distro.setDownloadURL(url);

    distro.setDescription(tmp.optString("description"));
    distro.setTitle(tmp.optString("title"));
    if (tmp.has("byteSize")) {
      distro.setByteSize(tmp.optString("byteSize"));
    }
    if (tmp.has("checksum")) {
      distro.setChecksum(tmp.optString("checksum"));
    }
    if (tmp.has("rights")) {
      distro.setRights(tmp.optString("rights"));
    }
    if (tmp.has("mediaType")) {
      distro.setMediaType(tmp.optString("mediaType"));
    }

    if (isFormat) {
      distro.setMediaType(format);
    }

    if (tmp.has("releaseDate")) {
      distro.setReleaseDate(tmp.optString("releaseDate"));
    }

    if (tmp.has("updateDate")) {
      distro.setUpdateDate(tmp.optString("updateDate"));
    }

    if (tmp.has("language")) {
      logger.info("Distribution language skipped");
    }
    if (tmp.has("linkedSchemas")) {
      logger.info("Distribution linkedSchemas skipped");
    }
    if (tmp.has("documentation")) {
      logger.info("Distribution documentation skipped");
    }
    if (tmp.has("status")) {
      logger.info("Distribution status skipped");
    }

    if (tmp.has("license")) {
      JSONObject l = tmp.getJSONObject("license");
      if (l.has("name") || l.has("uri") || l.has("type") || l.has("versionInfo")) {
        distro.setLicense(new DctLicenseDocument(l.optString("uri"),
            l.optString("name"), l.optString("type"),
            l.optString("versionInfo"), nodeId));
      }
    }

    distro.setDistributionAdditionalConfig(c);
    return distro;
  }

  @Override
  public DcatDataset getDataset(String datasetId) throws Exception {
    return null;
  }

  @Override
  public List<DcatDataset> getAllDatasets() throws Exception {
    SparqlCatalogueConfiguration config = (SparqlCatalogueConfiguration) node.getAdditionalConfig();

    if (StringUtils.isBlank(config.getSparqlDatasetDumpString())) {
      config.setSparqlDatasetDumpString(new String(
          Files.readAllBytes(Paths.get(config.getSparqlDatasetFilePath()))));
    }

    List<DcatDataset> result = new ArrayList<DcatDataset>();
    JSONArray datasetsJson = new JSONArray(config.getSparqlDatasetDumpString());
    for (int i = 0; i < datasetsJson.length(); i++) {
      result.add(datasetToDcat(datasetsJson.get(i), node));
    }

    // if(StringUtils.isBlank(orionConfig.getOrionDatasetFilePath())) {

    try {
      CommonUtil.storeFile(orionFilePath, 
          "sparqlDump_" + nodeId, config.getSparqlDatasetDumpString());
      config.setSparqlDatasetFilePath(orionFilePath + "sparqlDump_" + nodeId);
      config.setSparqlDatasetDumpString(null);
      node.setAdditionalConfig(config);
      OdmsManager.updateOdmsCatalogue(node, true);
    } catch (IOException e) {
      e.printStackTrace();
    }
    // }
    return result;

  }

  @Override
  public OdmsSynchronizationResult getChangedDatasets(
      List<DcatDataset> oldDatasets, String startingDate)
      throws Exception {
    return null;
  }

  private <T extends SkosConcept> List<T> extractConceptList(
      String propertyUri, List<String> concepts, Class<T> type) {
    List<T> result = new ArrayList<T>();

    for (String label : concepts) {
      try {
        result.add(type.getDeclaredConstructor(SkosConcept.class).newInstance(
            new SkosConcept(propertyUri, "", 
                Arrays.asList(new SkosPrefLabel("", label, nodeId)), nodeId)));
      } catch (InstantiationException | IllegalAccessException
          | IllegalArgumentException | InvocationTargetException
          | NoSuchMethodException | SecurityException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    return result;
  }

}
