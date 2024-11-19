/*******************************************************************************
 * Idra - Open Data Federation Platform
 * Copyright (C) 2024 Engineering Ingegneria Informatica S.p.A.
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
import it.eng.idra.beans.dcat.SpdxChecksum;
import it.eng.idra.beans.dcat.VcardOrganization;
import it.eng.idra.beans.odms.OdmsCatalogue;
import it.eng.idra.beans.odms.OdmsCatalogueForbiddenException;
import it.eng.idra.beans.odms.OdmsCatalogueNotFoundException;
import it.eng.idra.beans.odms.OdmsCatalogueOfflineException;
import it.eng.idra.beans.odms.OdmsSynchronizationResult;
import it.eng.idra.beans.zenodo.ZenodoClient;
import it.eng.idra.beans.zenodo.ZenodoConnection;
import it.eng.idra.beans.zenodo.ZenodoException;
import it.eng.idra.utils.CommonUtil;
import it.eng.idra.beans.zenodo.ZenodoDataset;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.formula.functions.T;
import java.util.stream.Collectors;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

public class ZenodoConnector implements IodmsConnector {

  /** The node id. */
  private String nodeId;
  /** The node. */
  private OdmsCatalogue node;
  /** The logger. */
  private static Logger logger = LogManager.getLogger(ZenodoConnector.class);

  public ZenodoConnector(OdmsCatalogue node) {
    this.node = node;
    this.nodeId = String.valueOf(node.getId());
  }

  /**
   * Counts the datasets present in the node.
   *
   * @returns int resulting datasets count
   * @throws ZenodoException                 the Zenodo exception
   * @throws MalformedURLException           the malformed URL exception
   * @throws OdmsCatalogueOfflineException   the odms catalogue offline exception
   * @throws OdmsCatalogueNotFoundException  the odms catalogue not found
   *                                         exception
   * @throws OdmsCatalogueForbiddenException the odms catalogue forbidden
   *                                         exception
   * @throws Exception                       Exception
   */
  @Override
  public int countDatasets() throws ZenodoException, MalformedURLException, OdmsCatalogueOfflineException,
      OdmsCatalogueNotFoundException, OdmsCatalogueForbiddenException, Exception {

    // for testing
    logger.info("countDatasets nodeid " + String.valueOf(node.getId()));
    logger.info("countDatasets node name " + node.getName());
    logger.info("countDatasets node host" + node.getHost());
    logger.info("countDatasets node apikey" + node.getApiKey());
    logger.info("countDatasets node dataset count" + node.getDatasetCount());
    logger.info("countDatasets node dataset start" + node.getDatasetStart());
    logger.info("countDatasets node communities" + node.getCommunities());
    //
    logger.info("countDatasets method -- Zenodo Connector Request sent -- First synchronization ");
    ZenodoClient zc = new ZenodoClient(new ZenodoConnection(node.getHost()), node.getApiKey());

    List<ZenodoDataset.Hit> results;
    try {

      results = zc.findRecords("", "", "", null, null, "", node.getCommunities(), "", "", "", "");
      logger.info("Zenodo Connector - countDatasets - Records count: " + results.size());

      if (results.size() == 0) {
        throw new OdmsCatalogueOfflineException(" The ODMS node is currently unreachable");
      }

      zc = null;
      System.gc();

      return results.size();

    } catch (ZenodoException e) {
      e.printStackTrace();
      handleError(e);
      return 0;
    }
  }

  /**
   * Performs dataset query on a federated Zenodo node using Zenodo API Client.
   * https://developers.zenodo.org/#representation35
   *
   * @param searchParameters the search parameters
   * @return the list of DcatDataset, List<DcatDataset>
   * @throws ZenodoException                 the Zenodo exception
   * @throws MalformedURLException           the malformed URL exception
   * @throws OdmsCatalogueNotFoundException  the odms catalogue not found
   *                                         exception
   * @throws OdmsCatalogueForbiddenException the odms catalogue forbidden
   *                                         exception
   * @throws OdmsCatalogueOfflineException   the odms catalogue offline exception
   * @throws Exception                       Exception
   */
  @Override
  public List<DcatDataset> findDatasets(HashMap<String, Object> searchParameters)
      throws ZenodoException, MalformedURLException, OdmsCatalogueNotFoundException,
      OdmsCatalogueForbiddenException, OdmsCatalogueOfflineException, Exception {

    String query = buildLiveQueryString(searchParameters);// check if needed when have data
    logger.info("Live query: " + query);

    String sort = "";
    String communities = "";
    String status = "";
    Integer page = 0;
    Integer size = 0;
    String all_versions = "";
    String type = "";
    String subtype = "";
    String bounds = "";
    String custom = "";

    if (searchParameters.containsKey("sort")) {
      sort = (String) searchParameters.remove("sort");
    }

    if (searchParameters.containsKey("communities")) {
      communities = (String) searchParameters.remove("communities");
    }

    if (searchParameters.containsKey("status")) {
      status = (String) searchParameters.remove("status");
    }
    if (searchParameters.containsKey("page")) {
      page = (Integer) searchParameters.remove("page");
    }
    if (searchParameters.containsKey("size")) {
      size = (Integer) searchParameters.remove("size");
    }
    if (searchParameters.containsKey("all_versions")) {
      all_versions = (String) searchParameters.remove("all_versions");
    }
    if (searchParameters.containsKey("type")) {
      type = (String) searchParameters.remove("type");
    }
    if (searchParameters.containsKey("subtype")) {
      subtype = (String) searchParameters.remove("subtype");
    }
    if (searchParameters.containsKey("bounds")) {
      bounds = (String) searchParameters.remove("bounds");
    }
    if (searchParameters.containsKey("custom")) {
      custom = (String) searchParameters.remove("custom");
    }

    // for testing
    logger.info("findDatasets nodeid " + String.valueOf(node.getId()));
    logger.info("findDatasets node name " + node.getName());
    logger.info("findDatasets node host" + node.getHost());
    logger.info("findDatasets node apikey" + node.getApiKey());
    logger.info("findDatasets node communities" + node.getCommunities());
    logger.info("findDatasets searchParameters communities" + communities);
    //
    logger.info("-- Zenodo Connector Request sent -- First synchronization ");
    ZenodoClient zc = new ZenodoClient(new ZenodoConnection(node.getHost()), node.getApiKey());
    ArrayList<DcatDataset> dcatResults = new ArrayList<DcatDataset>();

    logger.info("\n-----------------------\n");
    logger.info("NODE " + node.getDatasetCount());
    logger.info("START " + node.getDatasetStart());
    logger.info("RESULTS " + dcatResults.size());

    List<ZenodoDataset.Hit> results;
    try {

      results = zc.findRecords(query, status, sort, page, size, all_versions, communities, type, subtype, bounds,
          custom);

      logger.info("Zenodo Connector - findDatasets - Records count: " + results.size());
      for (ZenodoDataset.Hit d : results) {
        dcatResults.add(datasetToDcat(d, node));
      }
      logger.info("\n-----------------------\n");

    } catch (ZenodoException e) {
      e.printStackTrace();
      handleError(e);
    }

    zc = null;
    System.gc();

    return dcatResults;
  }

  /**
   * Count datasets based on search parameters
   * https://developers.zenodo.org/#representation35
   *
   * @param searchParameters the search parameters
   * @return int
   * @throws ZenodoException                 the Zenodo exception
   * @throws MalformedURLException           the malformed URL exception
   * @throws OdmsCatalogueNotFoundException  the odms catalogue not found
   *                                         exception
   * @throws OdmsCatalogueForbiddenException the odms catalogue forbidden
   *                                         exception
   * @throws OdmsCatalogueOfflineException   the odms catalogue offline exception
   * @throws Exception                       Exception
   */
  @Override
  public int countSearchDatasets(HashMap<String, Object> searchParameters)
      throws ZenodoException, MalformedURLException, OdmsCatalogueNotFoundException, OdmsCatalogueForbiddenException,
      OdmsCatalogueOfflineException, Exception {

    String query = buildLiveQueryString(searchParameters);// check if needed when have data
    logger.info("Live query: " + query);

    String sort = "";
    String communities = "";
    String status = "";
    Integer page = 0;
    Integer size = 0;
    String all_versions = "";
    String type = "";
    String subtype = "";
    String bounds = "";
    String custom = "";

    if (searchParameters.containsKey("sort")) {
      sort = (String) searchParameters.remove("sort");
    }

    if (searchParameters.containsKey("communities")) {
      communities = (String) searchParameters.remove("communities");
    }

    if (searchParameters.containsKey("status")) {
      status = (String) searchParameters.remove("status");
    }
    if (searchParameters.containsKey("page")) {
      page = (Integer) searchParameters.remove("page");
    }
    if (searchParameters.containsKey("size")) {
      size = (Integer) searchParameters.remove("size");
    }
    if (searchParameters.containsKey("all_versions")) {
      all_versions = (String) searchParameters.remove("all_versions");
    }
    if (searchParameters.containsKey("type")) {
      type = (String) searchParameters.remove("type");
    }
    if (searchParameters.containsKey("subtype")) {
      subtype = (String) searchParameters.remove("subtype");
    }
    if (searchParameters.containsKey("bounds")) {
      bounds = (String) searchParameters.remove("bounds");
    }
    if (searchParameters.containsKey("custom")) {
      custom = (String) searchParameters.remove("custom");
    }

    // just for test logs
    logger.info("countSearchDatasets nodeid " + String.valueOf(node.getId()));
    logger.info("countSearchDatasets node name " + node.getName());
    logger.info("countSearchDatasets node name " + node.getName());
    logger.info("countSearchDatasets node host" + node.getHost());
    logger.info("countSearchDatasets node apikey" + node.getApiKey());
    logger.info("countSearchDatasets node communities" + node.getCommunities());
    logger.info("countSearchDatasets searchParameters communities" + communities);
    //
    logger.info("-- Zenodo Connector Request sent -- First synchronization ");
    ZenodoClient zc = new ZenodoClient(new ZenodoConnection(node.getHost()), node.getApiKey());
    ArrayList<DcatDataset> dcatResults = new ArrayList<DcatDataset>();

    logger.info("\n-----------------------\n");
    logger.info("NODE " + node.getDatasetCount());
    logger.info("START " + node.getDatasetStart());
    logger.info("RESULTS " + dcatResults.size());

    List<ZenodoDataset.Hit> results;
    try {
      // check, example for ckan c.findDatasets(query, start, "1", sort); //rows=1
      results = zc.findRecords(query, status, sort, page, size, all_versions, communities, type, subtype, bounds,
          custom);
      logger.info("Zenodo Connector - countSearchDatasets - Records count: " + results.size());

      if (results.size() == 0) {
        throw new OdmsCatalogueOfflineException(" The ODMS node is currently unreachable");
      }

      zc = null;
      System.gc();

      return results.size();

    } catch (ZenodoException e) {
      e.printStackTrace();
      handleError(e);
      return 0;
    }
  }

  /**
   * Performs mapping from Zenodo Dataset object to DCATDataset object.
   *
   * (non-Javadoc)
   * 
   * see it.eng.idra.connectors.IodmsConnector#datasetToDcat(java.lang.Object,
   * it.eng.idra.beans.odms.OdmsCatalogue)
   * 
   * @param dataset the Zenodo dataset object
   * @param node    the catalogue node (OdmsCatalogue)
   * @return the mapped DcatDataset object
   * @throws ZenodoException the Zenodo exception
   */
  public DcatDataset datasetToDcat(Object dataset, OdmsCatalogue node) throws ZenodoException {

    // Cast the dataset to ZenodoDataset.Hit
    ZenodoDataset.Hit zd = (ZenodoDataset.Hit) dataset;

    String identifier = StringUtils.isNotBlank(zd.getId().toString()) ? zd.getId().toString() : "";

    List<String> otherIdentifier = new ArrayList<String>();
    otherIdentifier.add("Id: " + identifier);
    if (StringUtils.isNotBlank(zd.getDoi()))
      otherIdentifier.add("Doi: " + zd.getDoi());
    if (StringUtils.isNotBlank(zd.getConceptrecid()))
      otherIdentifier.add("Conceptrec Id: " + zd.getConceptrecid());
    if (StringUtils.isNotBlank(zd.getRecid()))
      otherIdentifier.add("Rec Id: " + zd.getRecid());

    String title = StringUtils.isNotBlank(zd.getMetadata().getTitle()) ? zd.getMetadata().getTitle() : "";
    String description = StringUtils.isNotBlank(zd.getMetadata().getDescription()) ? zd.getMetadata().getDescription()
        : "";

    DctLicenseDocument license = null;
    if (zd.getMetadata().getLicense() != null & StringUtils.isNotBlank(zd.getMetadata().getLicense().getId())) {
      license = new DctLicenseDocument(DCTerms.license.getURI(),
          zd.getMetadata().getLicense().getId(), zd.getMetadata().getLicense().getId(), "", nodeId);
    }

    String version = StringUtils.isNotBlank(zd.getMetadata().getVersion()) ? zd.getMetadata().getVersion() : "";
    List<String> keywords = zd.getMetadata().getKeywords();
    String releaseDate = CommonUtil.fixBadUtcDate(zd.getCreated().toString());
    String updateDate = CommonUtil.fixBadUtcDate(zd.getModified().toString());
    String language = StringUtils.isNotBlank(zd.getMetadata().getLanguage()) ? zd.getMetadata().getLanguage() : "";
    List<String> languages = new ArrayList<String>();
    languages.add(language);

    String accessRights = StringUtils.isNotBlank(zd.getMetadata().getAccess_right())
        ? zd.getMetadata().getAccess_right()
        : "";

    String accessRightsUri = accessRightsURI(accessRights);

    String type = StringUtils.isNotBlank(zd.getMetadata().getResource_type().getType())
        ? zd.getMetadata().getResource_type().getType()
        : "";
    String subtype = "";
    if ("publication".equals(type)) {
      subtype = StringUtils.isNotBlank(zd.getMetadata().getResource_type().getTitle())
          ? zd.getMetadata().getResource_type().getTitle()
          : "";
    }
    String resourceType = "publication".equals(type) ? StringUtils.capitalize(type) + " - " + subtype
        : StringUtils.capitalize(type);

    List<String> zenodoSubjects = new ArrayList<String>();
    zenodoSubjects.add(resourceType);

    List<SkosConceptSubject> subjectList = extractConceptList(DCTerms.subject.getURI(), zenodoSubjects,
        SkosConceptSubject.class);

    List<String> zenodoThemes = new ArrayList<String>();
    zenodoThemes.add(title);
    zenodoThemes.add(description);
    List<String> zenodoThemesMatched = mapZenodoThemeToDcat(zenodoThemes);
    logger.info("Zenodo Connector - datasetToDcat - zenodoThemesMatched size: " + zenodoThemesMatched.size());

    List<SkosConceptTheme> datasetTheme = extractConceptList(DCAT.theme.getURI(), zenodoThemesMatched,
        SkosConceptTheme.class);
    logger.info("Zenodo Connector - datasetToDcat - datasetTheme size: " + datasetTheme.size());

    String publisherName = "Zenodo";// node.getPublisherName()

    FoafAgent publisher = new FoafAgent(DCTerms.publisher.getURI(), null,
        publisherName, null, null, null, null, nodeId);

    Optional<String> creatorName = Optional
        .ofNullable(zd.getMetadata().getCreators().stream().map(c -> String.valueOf(c.getName()))
            .collect(Collectors.joining("; ")));
    Optional<String> creatorIdentifier = Optional
        .ofNullable(zd.getMetadata().getCreators().stream().map(c -> String.valueOf(c.getOrcid()))
            .collect(Collectors.joining("; ")));

    FoafAgent creator = new FoafAgent(DCTerms.creator.getURI(), null, creatorName.orElse(null),
        null, null, null, creatorIdentifier.orElse(null), nodeId);

    String nodeHost = node.getHost();
    // Construct the landing page URL for Zenodo
    String landingPage = nodeHost.contains("https://zenodo.org/api/")
        ? nodeHost + "records/" + identifier
        : nodeHost + (nodeHost.endsWith("/") ? "" : "/") + "api/records/" + identifier;
    /*
     * String landingPage = nodeHost.contains("http://datos.santander.es/catalogo")
     * ? nodeHost.replace("catalogo" + (nodeHost.endsWith("/") ? "/" : ""),
     * "dataset/?id=" + zd.getId())
     * : nodeHost + (nodeHost.endsWith("/") ? "" : "/") + "dataset/" + identifier;
     */// check, this is for ckan

    List<DcatDistribution> distributionList = new ArrayList<DcatDistribution>();
    List<ZenodoDataset.File> resourceList = zd.getFiles();
    if (resourceList != null) {
      for (ZenodoDataset.File f : resourceList) {
        distributionList.add(resourceToDcat(f, landingPage, license));
      }
    }

    DctLocation spatialCoverage = null;
    DctPeriodOfTime temporalCoverage = null;
    FoafAgent rightsHolder = null;
    String frequency = null;
    List<VcardOrganization> contactPointList = new ArrayList<VcardOrganization>();
    List<DctStandard> conformsTo = new ArrayList<DctStandard>();
    List<String> documentation = new ArrayList<String>();
    List<String> versionNotes = new ArrayList<String>();
    List<String> hasVersion = new ArrayList<String>();
    List<String> isVersionOf = new ArrayList<String>();
    List<String> sample = new ArrayList<String>();
    List<String> source = new ArrayList<String>();
    List<String> relatedResources = new ArrayList<String>();
    List<String> provenance = new ArrayList<String>();

    DcatDataset mapped = new DcatDataset(nodeId, identifier, title, description, distributionList,
        datasetTheme, publisher, contactPointList, keywords, accessRightsUri, conformsTo,
        documentation, frequency, hasVersion, isVersionOf, landingPage, languages, provenance,
        releaseDate, updateDate, otherIdentifier, sample, source, spatialCoverage, temporalCoverage,
        type, version, versionNotes, rightsHolder, creator, subjectList, relatedResources);

    return mapped;
  }

  /**
   * Get a specific dataset by ID
   *
   * @param datasetId the id of dataset
   * @return the mapped DcatDataset object
   * @throws ZenodoException                 the Zenodo exception
   * @throws MalformedURLException           the malformed URL exception
   * @throws OdmsCatalogueNotFoundException  the odms catalogue not found
   *                                         exception
   * @throws OdmsCatalogueForbiddenException the odms catalogue forbidden
   *                                         exception
   * @throws OdmsCatalogueOfflineException   the odms catalogue offline exception
   * @throws Exception                       Exception
   */
  @Override
  public DcatDataset getDataset(String datasetId)
      throws ZenodoException, MalformedURLException, OdmsCatalogueOfflineException,
      OdmsCatalogueNotFoundException, OdmsCatalogueForbiddenException, Exception {

    // just for test logs
    logger.info("getDataset nodeid " + String.valueOf(node.getId()));
    logger.info("getDataset node name " + node.getName());
    logger.info("getDataset node host" + node.getHost());
    logger.info("getDataset node apikey" + node.getApiKey());
    logger.info("getDataset node dataset count" + node.getDatasetCount());
    logger.info("getDataset node dataset start" + node.getDatasetStart());
    logger.info("getDataset node communities" + node.getCommunities());
    //
    logger.info("-- Zenodo Connector Request sent -- First synchronization ");
    ZenodoClient zc = new ZenodoClient(new ZenodoConnection(node.getHost()), node.getApiKey());
    ZenodoDataset.Hit dataset;
    DcatDataset mapped = null;

    try {

      dataset = zc.getRecord(Integer.parseInt(datasetId));
      logger.info("Zenodo Connector - getDataset - Dataset title: " + dataset.getMetadata().getTitle());// test

      if (dataset != null) {
        mapped = datasetToDcat(dataset, node);
      }

      zc = null;
      dataset = null;

      return mapped;

    } catch (ZenodoException e) {
      e.printStackTrace();
      handleError(e);
      return null;
    }
  }

  /**
   * Retrieves all datasets belonging to a federated Zenodo node using Zenodo API
   * Client
   *
   * @return the all DcatDataset datasets, List<DcatDataset>
   * @throws ZenodoException                 the Zenodo exception
   * @throws MalformedURLException           the malformed URL exception
   * @throws OdmsCatalogueOfflineException   the odms catalogue offline exception
   * @throws OdmsCatalogueNotFoundException  the odms catalogue not found
   *                                         exception
   * @throws OdmsCatalogueForbiddenException the odms catalogue forbidden
   *                                         exception
   * @throws Exception                       Exception
   */
  @Override
  public List<DcatDataset> getAllDatasets()
      throws ZenodoException, MalformedURLException, OdmsCatalogueOfflineException,
      OdmsCatalogueNotFoundException, OdmsCatalogueForbiddenException, Exception {

    ArrayList<DcatDataset> dcatResults = new ArrayList<DcatDataset>();

    // just for test logs
    logger.info("getAllDatasets nodeid " + String.valueOf(node.getId()));
    logger.info("getAllDatasets node name " + node.getName());
    logger.info("getAllDatasets node host" + node.getHost());
    logger.info("getAllDatasets node apikey" + node.getApiKey());
    logger.info("getAllDatasets node communities" + node.getCommunities());
    //
    logger.info("-- Zenodo Connector Request sent -- First synchronization ");
    ZenodoClient zc = new ZenodoClient(new ZenodoConnection(node.getHost()), node.getApiKey());

    logger.info("\n-----------------------\n");
    logger.info("NODE " + node.getDatasetCount());
    logger.info("START " + node.getDatasetStart());

    List<ZenodoDataset.Hit> results;

    logger.info("\n\n-----------------------------------\n\n");

    int retryNum = 1;
    boolean retry = false;
    do {

      try {
        // check, below example for ckan
        // zc.findDatasets("metadata_modified:[* TO " +
        // CommonUtil.formatDate(node.getRegisterDate()) +"]",
        // Integer.toString(node.getDatasetStart()), "10000000", "metadata_modified
        // asc");
        results = zc.findRecords("", "", "", null, null, "", node.getCommunities(), "", "", "", "");
        retry = false;
        logger.info("Zenodo Connector - getAllDatasets - Records count: " + results.size());
        for (ZenodoDataset.Hit zd : results) {
          dcatResults.add(datasetToDcat(zd, node));
        }

        logger.info("\n-----------------------\n");
        results = null;

      } catch (ZenodoException e) {
        e.printStackTrace();
        if (e.getMessage() != null && e.getMessage().contains("The ODMS host does not exist")) {
          throw new OdmsCatalogueNotFoundException(e.getMessage());
        } else {
          logger.info("Exception! Attempt n: " + retryNum);
          retry = true;
          retryNum++;
          if (retryNum == 5) {
            handleError(e);
          }
        }
      }

    } while (retry);

    return dcatResults;
  }

  /**
   * Retrieves all recent activities on datasets of a node Makes an Hashmap where
   * every key-value pair is a corrispondence between DCATDataset and related
   * activity on it, starting from a passed date string.
   * 
   * (non-Javadoc)
   * 
   * it.eng.idra.connectors.IodmsConnector#getChangedDatasets(java.util.List,
   * java.lang.String)
   *
   * @param oldDatasets  the old datasets
   * @param startingDate the starting date string
   * @return the changed datasets OdmsSynchronizationResult
   * @throws Exception the parse exception
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

  // utils

  // check this method
  /**
   * Builds the live query string for Zenodo records search.
   *
   * @param searchParameters the search parameters
   * @return the constructed query string
   */
  private String buildLiveQueryString(HashMap<String, Object> searchParameters) {
    String query = "";
    boolean isFirst = true;
    String key;
    Object value;

    // Iterate through search parameters and build the query
    for (Map.Entry<String, Object> e : searchParameters.entrySet()) {
      key = e.getKey();
      value = e.getValue();

      if (value instanceof String) {
        value = ((String) value).replaceAll("\"", "").trim();
      }

      switch (key) {
        case "q":
          // Add search query
          if (!((String) value).trim().equals("")) {
            String valueString = "(" + ((String) value).replace("&", " AND ") + ") ";// check "(" and ") "
            query += isFirst ? valueString : " AND " + valueString;
            isFirst = false;
          }
          break;

        case "status":
          // Filter by status (draft or published)
          if (!((String) value).trim().equals("")) {
            query += isFirst ? "status:\"" + value + "\"" : " AND status:\"" + value + "\"";
            isFirst = false;
          }
          break;

        case "sort":
          // Sorting parameter
          if (!((String) value).trim().equals("")) {
            query += isFirst ? "sort:\"" + value + "\"" : " AND sort:\"" + value + "\"";
            isFirst = false;
          }
          break;

        case "page":
          // Pagination page number
          if (value != null) {
            query += isFirst ? "page:" + value : " AND page:" + value;
            isFirst = false;
          }
          break;

        case "size":
          // Number of results per page
          if (value != null) {
            query += isFirst ? "size:" + value : " AND size:" + value;
            isFirst = false;
          }
          break;

        case "all_versions":
          // Show all versions
          if (!((String) value).trim().equals("")) {
            query += isFirst ? "all_versions:" + value : " AND all_versions:" + value;
            isFirst = false;
          }
          break;

        case "communities":
          // Filter by community
          if (!((String) value).trim().equals("")) {
            query += isFirst ? "communities:\"" + value + "\"" : " AND communities:\"" + value + "\"";
            isFirst = false;
          }
          break;

        case "type":
          // Filter by record type (e.g., Publication, Poster)
          if (!((String) value).trim().equals("")) {
            query += isFirst ? "type:\"" + value + "\"" : " AND type:\"" + value + "\"";
            isFirst = false;
          }
          break;

        case "subtype":
          // Filter by subtype (e.g., Journal article, Preprint)
          if (!((String) value).trim().equals("")) {
            query += isFirst ? "subtype:\"" + value + "\"" : " AND subtype:\"" + value + "\"";
            isFirst = false;
          }
          break;

        case "bounds":
          // Filter by geolocation bounding box
          if (!((String) value).trim().equals("")) {
            query += isFirst ? "bounds:" + value : " AND bounds:" + value;
            isFirst = false;
          }
          break;

        case "custom":
          // Filter by custom keywords
          if (!((String) value).trim().equals("")) {
            query += isFirst ? "custom:" + value : " AND custom:" + value;
            isFirst = false;
          }
          break;

        default:
          // For any additional parameters
          if (!((String) value).trim().equals("")) {
            query += isFirst ? key + ":\"" + value + "\"" : " AND " + key + ":\"" + value + "\"";
            isFirst = false;
          }
          break;
      }
    }

    return query.trim();
  }

  /**
   * Resource to dcat.
   *
   * @param f                  the ZenodoDataset.File
   * @param datasetLandingPage the dataset landing page
   * @param datasetLicense     the dataset license
   * @return the dcat distribution
   */
  private DcatDistribution resourceToDcat(ZenodoDataset.File f, String datasetLandingPage,
      DctLicenseDocument datasetLicense) {

    String accessUrl = StringUtils.isNotBlank(f.getLinks().getSelf()) ? f.getLinks().getSelf()
        : datasetLandingPage;
    String downloadUrl = accessUrl;
    String title = f.getFilename();
    String byteSize = String.valueOf(f.getFilesize());
    String format = "";
    if (title.contains(".")) {
      format = title.substring(title.lastIndexOf(".") + 1);
    }

    String checksumComplete = f.getChecksum();
    // Split the checksum into algorithm and value
    String[] checksumParts = checksumComplete.split(":");
    String algorithm = checksumParts[0];
    String checksumValue = checksumParts[1];
    SpdxChecksum checksum = new SpdxChecksum("http://spdx.org/rdf/terms#checksum", algorithm,
        checksumValue, nodeId);

    String mimeType = mimeTypeURI(format);

    return new DcatDistribution(nodeId, accessUrl, null, format, datasetLicense, byteSize,
        checksum, new ArrayList<String>(), downloadUrl, new ArrayList<String>(),
        new ArrayList<DctStandard>(), mimeType, "1970-01-01T00:00:00Z", "1970-01-01T00:00:00Z", null, null, title);
  }

  private String mimeTypeURI(String format) {
    String mimeType;
    switch (format.toLowerCase()) {
      case "csv":
        mimeType = "http://www.iana.org/assignments/media-types/text/csv";
        break;
      case "zip":
        mimeType = "http://www.iana.org/assignments/media-types/application/zip";
        break;
      case "geo json":
      case "geojson":
        mimeType = "http://www.iana.org/assignments/media-types/application/geo+json";
        break;
      case "map_srvc":
        mimeType = "http://www.iana.org/assignments/media-types/application/x-map-server";
        break;
      case "ods":
        mimeType = "http://www.iana.org/assignments/media-types/application/vnd.oasis.opendocument.spreadsheet";
        break;
      case "jsonl":
      case "json":
        mimeType = "http://www.iana.org/assignments/media-types/application/json";
        break;
      case "jsonld":
        mimeType = "http://www.iana.org/assignments/media-types/application/ld+json";
        break;
      case "pdf":
        mimeType = "http://www.iana.org/assignments/media-types/application/pdf";
        break;
      case "htm":
      case "html":
        mimeType = "http://www.iana.org/assignments/media-types/text/html";
        break;
      case "xls":
      case "xlsx":
        mimeType = "http://www.iana.org/assignments/media-types/application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        break;
      case "doc":
      case "docx":
        mimeType = "http://www.iana.org/assignments/media-types/application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        break;
      case "jpg":
      case "jpeg":
        mimeType = "http://www.iana.org/assignments/media-types/image/jpeg";
        break;
      case "ppt":
      case "pptx":
        mimeType = "http://www.iana.org/assignments/media-types/application/vnd.openxmlformats-officedocument.presentationml.presentation";
        break;
      case "kml":
        mimeType = "http://www.iana.org/assignments/media-types/application/vnd.google-earth.kml+xml";
        break;
      case "n3":
        mimeType = "http://www.iana.org/assignments/media-types/text/n3";
        break;
      case "txt":
        mimeType = "http://www.iana.org/assignments/media-types/text/plain";
        break;
      case "xml":
        mimeType = "http://www.iana.org/assignments/media-types/application/xml";
        break;
      case "png":
        mimeType = "http://www.iana.org/assignments/media-types/image/png";
        break;
      case "gif":
        mimeType = "http://www.iana.org/assignments/media-types/image/gif";
        break;
      case "mp4":
        mimeType = "http://www.iana.org/assignments/media-types/video/mp4";
        break;
      case "mp3":
        mimeType = "http://www.iana.org/assignments/media-types/audio/mpeg";
        break;
      case "tar":
        mimeType = "http://www.iana.org/assignments/media-types/application/x-tar";
        break;
      case "rdfxml":
        mimeType = "http://www.iana.org/assignments/media-types/application/rdf+xml";
        break;
      case "turtle":
        mimeType = "http://www.iana.org/assignments/media-types/text/turtle";
        break;
      case "rar":
        mimeType = "http://example.org/media-types/application/vnd.rar"; // Replace with a URI if known
        break;
      case "shp":
        mimeType = "http://example.org/media-types/application/x-shapefile"; // Replace with a URI if known
        break;
      case "wms":
        mimeType = "http://example.org/media-types/application/vnd.ogc.wms_xml"; // Replace with a URI if known
        break;
      case "fgb":// Generic type for FlatGeobuf
      case "parquet":// Commonly used for Apache Parquet
      default:// Default for unknown formats
        mimeType = "http://www.iana.org/assignments/media-types/application/octet-stream";
        break;
    }
    return mimeType;
  }

  private String accessRightsURI(String accessRights) {
    String accessRightsUri;
    switch (accessRights) {
      case "open":
        accessRightsUri = "http://publications.europa.eu/resource/authority/access-right/PUBLIC";
        break;
      case "restricted":
        accessRightsUri = "http://publications.europa.eu/resource/authority/access-right/RESTRICTED";
        break;
      case "embargoed":
        accessRightsUri = "http://publications.europa.eu/resource/authority/access-right/EMBARGOED";
        break;
      case "closed":
        accessRightsUri = "http://publications.europa.eu/resource/authority/access-right/NON_PUBLIC";
        break;
      default:
        accessRightsUri = "http://publications.europa.eu/resource/authority/access-right/PUBLIC";
        break;
    }
    return accessRightsUri;
  }

  // Map Zenodo to DCAT theme using keywords
  private List<String> mapZenodoThemeToDcat(List<String> zenodoThemesList) {
    List<String> themes = new ArrayList<String>();

    for (String zenodoTheme : zenodoThemesList) {
      if (zenodoTheme.contains("agriculture") || zenodoTheme.contains("fisheries")
          || zenodoTheme.contains("forestry") || zenodoTheme.contains("food")) {
        themes.add("AGRI");
      } else if (zenodoTheme.contains("finance") || zenodoTheme.contains("economy")) {
        themes.add("ECON");
      } else if (zenodoTheme.contains("education") || zenodoTheme.contains("culture")
          || zenodoTheme.contains("sport") || zenodoTheme.contains("school")) {
        themes.add("EDUC");
      } else if (zenodoTheme.contains("energy")) {
        themes.add("ENER");
      } else if (zenodoTheme.contains("environment") || zenodoTheme.contains("ecology")) {
        themes.add("ENVI");
      } else if (zenodoTheme.contains("government") || zenodoTheme.contains("public sector")) {
        themes.add("GOVE");
      } else if (zenodoTheme.contains("health") || zenodoTheme.contains("medicine")
          || zenodoTheme.contains("medical")) {
        themes.add("HEAL");
      } else if (zenodoTheme.contains("international") || zenodoTheme.contains("International issues")) {
        themes.add("INTR");
      } else if (zenodoTheme.contains("law") || zenodoTheme.contains("justice")
          || zenodoTheme.contains("safety") || zenodoTheme.contains("legal")
          || zenodoTheme.contains("legal system") || zenodoTheme.contains("public safety")) {
        themes.add("JUST");
      } else if (zenodoTheme.contains("regions") || zenodoTheme.contains("cities")) {
        themes.add("REGI");
      } else if (zenodoTheme.contains("society") || zenodoTheme.contains("population")) {
        themes.add("SOCI");
      } else if (zenodoTheme.contains("science") || zenodoTheme.contains("technology")) {
        themes.add("TECH");
      } else if (zenodoTheme.contains("transport")) {
        themes.add("TRAN");
      }
    }

    return themes;
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
  /*
   * Return a List of SKOSConcept, each of them containing a prefLabel from input
   * String list.
   */
  @SuppressWarnings("hiding")
  private <T extends SkosConcept> List<T> extractConceptList(String propertyUri,
      List<String> concepts, Class<T> type) {

    List<T> result = new ArrayList<T>();

    for (String label : concepts) {
      try {
        result.add(type.getDeclaredConstructor(SkosConcept.class).newInstance(new SkosConcept(
            propertyUri, "", Arrays.asList(new SkosPrefLabel("", label, nodeId)),
            nodeId)));
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
          | InvocationTargetException | NoSuchMethodException | SecurityException e) {
        e.printStackTrace();
      }
    }

    return result;

  }

  /**
   * Handle error.
   *
   * @param e the ZenodoException
   * @throws OdmsCatalogueNotFoundException  the odms catalogue not found
   *                                         exception
   * @throws OdmsCatalogueForbiddenException the odms catalogue forbidden
   *                                         exception
   * @throws OdmsCatalogueOfflineException   the odms catalogue offline exception
   * @throws ZenodoException                 the CKAN exception
   */
  public void handleError(ZenodoException e) throws OdmsCatalogueNotFoundException,
      OdmsCatalogueForbiddenException, OdmsCatalogueOfflineException, ZenodoException {

    String message = e.getMessage();

    if (StringUtils.isBlank(message)) {
      message = e.getErrorMessages().get(0);
    }

    if (StringUtils.isNotBlank(message)) {

      if (message.contains(" The ODMS host does not exist")) {
        throw new OdmsCatalogueNotFoundException(e.getMessage());
      } else if (message.contains(" The ODMS node is forbidden")) {
        throw new OdmsCatalogueForbiddenException(e.getMessage());
      } else if (message.contains(" The ODMS node is currently unreachable")) {
        throw new OdmsCatalogueOfflineException(e.getMessage());
      } else {
        throw new ZenodoException(e.getMessage());
      }

    } else {
      throw new ZenodoException("Unknown Zenodo Exception");
    }
  }
}
