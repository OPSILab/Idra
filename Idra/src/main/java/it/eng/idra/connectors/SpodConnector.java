/*******************************************************************************
 * Idra - Open Data Federation Platform
 * Copyright (C) 2021 Engineering Ingegneria Informatica S.p.A.
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
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

package it.eng.idra.connectors;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.eng.idra.beans.dcat.DcatDataset;
import it.eng.idra.beans.dcat.DcatDistribution;
import it.eng.idra.beans.dcat.DctLicenseDocument;
import it.eng.idra.beans.dcat.DctLocation;
import it.eng.idra.beans.dcat.DctPeriodOfTime;
import it.eng.idra.beans.dcat.DctStandard;
import it.eng.idra.beans.dcat.FoafAgent;
import it.eng.idra.beans.dcat.SkosConcept;
import it.eng.idra.beans.dcat.SkosConceptTheme;
import it.eng.idra.beans.dcat.SkosPrefLabel;
import it.eng.idra.beans.dcat.SpdxChecksum;
import it.eng.idra.beans.dcat.VCardOrganization;
import it.eng.idra.beans.odms.OdmsCatalogue;
import it.eng.idra.beans.odms.OdmsCatalogueForbiddenException;
import it.eng.idra.beans.odms.OdmsCatalogueNotFoundException;
import it.eng.idra.beans.odms.OdmsCatalogueOfflineException;
import it.eng.idra.beans.odms.OdmsSynchronizationResult;
import it.eng.idra.beans.spod.SpodDataset;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ckan.CKANException;
import org.ckan.Extra;
import org.ckan.Resource;
import org.ckan.Tag;

// TODO: Auto-generated Javadoc
/**
 * The Class SpodConnector.
 */
public class SpodConnector implements IodmsConnector {

  /** The node id. */
  private String nodeId;

  /** The node. */
  private OdmsCatalogue node;

  /** The logger. */
  private static Logger logger = LogManager.getLogger(SpodConnector.class);

  /**
   * Instantiates a new spod connector.
   *
   * @param node the node
   */
  public SpodConnector(OdmsCatalogue node) {
    this.node = node;
    this.nodeId = String.valueOf(node.getId());
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.connectors.IodmsConnector#countDatasets()
   */
  @Override
  public int countDatasets() throws MalformedURLException, OdmsCatalogueOfflineException,
      OdmsCatalogueNotFoundException, OdmsCatalogueForbiddenException {
    try {
      return getAllIds().size();
    } catch (Exception e) {
      e.printStackTrace();
      throw new OdmsCatalogueOfflineException(e.getMessage());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.connectors.IodmsConnector#findDatasets(java.util.HashMap)
   */
  @Override
  public List<DcatDataset> findDatasets(HashMap<String, Object> searchParameters)
      throws MalformedURLException, OdmsCatalogueNotFoundException, OdmsCatalogueForbiddenException,
      OdmsCatalogueOfflineException {

    ArrayList<DcatDataset> dcatResults = new ArrayList<DcatDataset>();

    return dcatResults;
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.connectors.IodmsConnector#getDataset(java.lang.String)
   */
  @Override
  public DcatDataset getDataset(String datasetId)
      throws MalformedURLException, OdmsCatalogueOfflineException, OdmsCatalogueNotFoundException,
      OdmsCatalogueForbiddenException {

    try {
      return datasetToDcat(getCkanDataset(datasetId), node);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      throw new OdmsCatalogueOfflineException(e.getMessage());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.connectors.IodmsConnector#getAllDatasets()
   */
  @Override
  public List<DcatDataset> getAllDatasets()
      throws MalformedURLException, OdmsCatalogueOfflineException, OdmsCatalogueNotFoundException,
      OdmsCatalogueForbiddenException {

    ArrayList<DcatDataset> dcatResults = new ArrayList<DcatDataset>();

    try {
      List<String> ids = getAllIds();

      ExecutorService executor = Executors.newWorkStealingPool();

      List<Callable<List<DcatDataset>>> callables = new ArrayList<Callable<List<DcatDataset>>>();
      int threadPoolSize = 8;
      int workerSize = (int) Math.ceil((double) ids.size() / threadPoolSize);
      int threadNum = (int) Math.ceil((double) ids.size() / workerSize);

      for (int i = 0; i < threadNum; i++) {
        int index = i;
        int beg = i * workerSize;
        int end = ((i + 1) * workerSize) >= ids.size() ? ids.size() : ((i + 1) * workerSize);
        callables.add(() -> getSubsetOfDataset(index, beg, end, ids.subList(beg, end)));
      }

      LocalDateTime a = LocalDateTime.now();
      logger.info("Start at: " + a.toString());
      try {
        executor.invokeAll(callables).stream().map(future -> {
          try {
            return future.get();
          } catch (Exception e) {
            throw new IllegalStateException(e);
          }
        }).forEach(dcatResults::addAll);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      LocalDateTime b = LocalDateTime.now();
      logger.info("Finished at: " + b.toString());
      logger.info("Difference: " + Duration.between(a, b).toMinutes());
      logger.info("Finished: " + dcatResults.size());
      return dcatResults;
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      throw new OdmsCatalogueOfflineException(e.getMessage());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.connectors.IodmsConnector#getChangedDatasets(java.util.List,
   * java.lang.String)
   */
  @Override
  public OdmsSynchronizationResult getChangedDatasets(List<DcatDataset> oldDatasets,
      String startingDateString)
      throws ParseException, CKANException, MalformedURLException, OdmsCatalogueOfflineException,
      OdmsCatalogueNotFoundException, OdmsCatalogueForbiddenException {
    List<String> newDatasetsIds = null;

    try {
      newDatasetsIds = getAllIds();
    } catch (Exception e) {
      throw new OdmsCatalogueOfflineException(e.getMessage());
    }

    List<String> oldDatasetsIds = oldDatasets.stream().map(x -> x.getIdentifier().getValue())
        .collect(Collectors.toList());
    OdmsSynchronizationResult syncrhoResult = new OdmsSynchronizationResult();

    ImmutableSet<String> newSets = ImmutableSet.copyOf(newDatasetsIds);
    ImmutableSet<String> oldSets = ImmutableSet.copyOf(oldDatasetsIds);

    int deleted = 0;
    int added = 0;
    int changed = 0;

    /// Find added datasets
    // difference(current,present)
    SetView<String> diff = Sets.difference(newSets, oldSets);
    logger.info("New Packages: " + diff.size());
    for (String newId : diff) {
      try {
        syncrhoResult.addToAddedList(datasetToDcat(getCkanDataset(newId), node));
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
      syncrhoResult.addToDeletedList(oldDatasets.stream()
          .filter(x -> x.getIdentifier().getValue().equals(id)).findFirst().get());
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

    SimpleDateFormat dcatDateF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);

    int exception = 0;
    for (String id : intersection) {
      try {
        DcatDataset tmp = datasetToDcat(getCkanDataset(id), node);
        DcatDataset old = oldDatasets.stream().filter(x -> x.getIdentifier().getValue().equals(id))
            .findFirst().get();
        oldDate.setTime(dcatDateF.parse(old.getUpdateDate().getValue()));
        newDate.setTime(dcatDateF.parse(tmp.getUpdateDate().getValue()));
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

  /**
   * Gets the subset of dataset.
   *
   * @param index the index
   * @param beg   the beg
   * @param end   the end
   * @param ids   the ids
   * @return the subset of dataset
   */
  private List<DcatDataset> getSubsetOfDataset(int index, int beg, int end, List<String> ids) {
    logger.info("Subset: " + index + " beg: " + beg + " end: " + end + " size: " + ids.size());
    return ids.stream().map(x -> {
      try {
        return datasetToDcat(getCkanDataset(x), node);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        return null;
      }
    }).filter(x -> x != null).collect(Collectors.toList());
  }

  /**
   * Gets the all ids.
   *
   * @return the all ids
   * @throws Exception the exception
   */
  /*
   * This method will return all of the ids of the catalogue
   * /SpodCkanApi/api/2/rest/dataset
   */
  private List<String> getAllIds() throws Exception {
    RestClient client = new RestClientImpl();

    HttpResponse response = client.sendGetRequest(node.getHost()
        + (node.getHost().endsWith("/") ? "" : "/") + "SpodCkanApi/api/2/rest/dataset",
        new HashMap<String, String>());
    int status = client.getStatus(response);
    if (status == 200) {
      return GsonUtil.json2Obj(client.getHttpResponseBody(response), GsonUtil.stringListType);
    } else {
      return new ArrayList<String>();
    }
  }

  /*
   * This method will return the dataset /SpodCkanApi/api/2/rest/dataset/{id}
   */

  /**
   * Gets the ckan dataset.
   *
   * @param id the id
   * @return the ckan dataset
   * @throws Exception the exception
   */
  private SpodDataset getCkanDataset(String id) throws Exception {
    RestClient client = new RestClientImpl();
    HttpResponse response = client.sendGetRequest(node.getHost()
        + (node.getHost().endsWith("/") ? "" : "/") + "SpodCkanApi/api/2/rest/dataset/" + id,
        new HashMap<String, String>());
    int status = client.getStatus(response);
    if (status == 200) {
      return GsonUtil.json2Obj(client.getHttpResponseBody(response), GsonUtil.spodDatasetType);
    } else {
      return null;
    }
  }

  /**
   * datasetToDcat.
   *
   * @param dataset the dataset
   * @param node    the node
   * @return the dcat dataset
   */
  public DcatDataset datasetToDcat(Object dataset, OdmsCatalogue node) {

    SpodDataset d = (SpodDataset) dataset;
    // Properties to be mapped among different CKAN fallback fields

    if (!d.isIsopen()) {
      System.out.println(d.getName() + " Is not open");
      return null;
    }

    String title = null;
    String description = null;
    String accessRights = null;
    String frequency = null;
    String landingPage = null;
    String releaseDate = null;
    String updateDate = null;
    String identifier = null;
    String type = null;
    String version = null;

    String publisherIdentifier = null;
    String publisherUri = null;
    String publisherName = null;
    String publisherMbox = null;
    String publisherHomepage = null;
    String publisherType = null;
    String holderIdentifier = null;
    String holderUri = null;
    String holderName = null;
    String holderMbox = null;
    String holderHomepage = null;
    String holderType = null;
    String creatorIdentifier = null;
    String creatorUri = null;
    String creatorName = null;
    String creatorMbox = null;
    String creatorHomepage = null;
    String creatorType = null;
    String startDate = null;
    String endDate = null;
    String vcardUri = null;
    String vcardFn = null;
    String vcardHasEmail = null;
    List<DctStandard> conformsTo = new ArrayList<DctStandard>();
    FoafAgent publisher = null;
    FoafAgent rightsHolder = null;
    FoafAgent creator = null;
    List<VCardOrganization> contactPointList = new ArrayList<VCardOrganization>();
    DctPeriodOfTime temporalCoverage = null;
    DctLocation spatialCoverage = null;
    DctLicenseDocument license = null;
    String geographicalIdentifier = null;
    String geographicalName = null;
    String geometry = null;
    List<SkosConceptTheme> themeList = new ArrayList<SkosConceptTheme>();
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

    otherIdentifier.add(d.getName());

    try {
      for (Extra e : d.getExtras()) {

        switch (e.getKey().toLowerCase()) {
          case "alternate_identifier":
            otherIdentifier.addAll(extractValueList(e.getValue()));
            break;
          case "theme":
            themeList
                .addAll(extractConceptList(DCAT.theme.getURI(), 
                    extractValueList(e.getValue()), SkosConceptTheme.class));
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
            if (checkIfJsonObject(input)) {
              geometry = input;
            } else if (input.startsWith("http://")) {
              geographicalIdentifier = input.trim();
            } else {
              geographicalName = input.trim();
            }
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
            if (StringUtils.isBlank(tempVer)) {
              version = e.getValue();
            } else {
              version = tempVer;
            }
            break;
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
            vcardUri = e.getValue();
            break;
          case "contact_name":
            vcardFn = e.getValue();
            break;
          case "contact_email":
            vcardHasEmail = e.getValue();
            break;
          default:
            break;
        }
      }

    } catch (NullPointerException e) {
      logger.debug(e.getLocalizedMessage());
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
      if (StringUtils.isNotBlank(d.getMetadata_created())) {
        releaseDate = CommonUtil.fixBadUtcDate(d.getMetadata_created());
      }
      if (StringUtils.isNotBlank(d.getMetadata_modified())) {
        updateDate = CommonUtil.fixBadUtcDate(d.getMetadata_modified());
      }

      if (StringUtils.isNotBlank(geographicalIdentifier) || StringUtils.isNotBlank(geographicalName)
          || StringUtils.isNotBlank(geometry)) {
        spatialCoverage = new DctLocation(DCTerms.spatial.getURI(), geographicalIdentifier,
            geographicalName, geometry, nodeId);
      }

      if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
        temporalCoverage = new DctPeriodOfTime(DCTerms.temporal.getURI(), startDate, endDate,
            nodeId);
      }

      // Contact Point
      if (StringUtils.isBlank(vcardFn)) {
        if (StringUtils.isBlank((vcardFn = d.getMaintainer()))) {
          vcardFn = d.getAuthor();
        }
      }

      if (StringUtils.isBlank(vcardHasEmail)) {
        if (StringUtils.isBlank((vcardHasEmail = d.getMaintainer_email()))) {
          vcardHasEmail = d.getAuthor_email();
        }
      }

      if (vcardUri != null || vcardFn != null || vcardHasEmail != null) {
        contactPointList.add(new VCardOrganization(DCAT.contactPoint.getURI(), vcardUri, vcardFn,
            vcardHasEmail, "", "", "", nodeId));
      }

      // Publisher
      if (publisherUri != null || publisherName != null || publisherMbox != null
          || publisherHomepage != null || publisherType != null || publisherIdentifier != null) {
        publisher = new FoafAgent(DCTerms.publisher.getURI(), publisherUri, publisherName,
            publisherMbox, publisherHomepage, publisherType, publisherIdentifier, nodeId);
      }
      // Rights Holder
      if (holderUri != null || holderName != null || holderMbox != null || holderHomepage != null
          || holderType != null || holderIdentifier != null) {
        rightsHolder = new FoafAgent(DCTerms.rightsHolder.getURI(), holderUri, holderName,
            holderMbox, holderHomepage, holderType, holderIdentifier, nodeId);
      }
      // Creator
      if (creatorUri != null || creatorName != null || creatorMbox != null
          || creatorHomepage != null || creatorType != null || creatorIdentifier != null) {
        creator = new FoafAgent(DCTerms.creator.getURI(), creatorUri, creatorName, creatorMbox,
            creatorHomepage, creatorType, creatorIdentifier, nodeId);
      }
      // License
      String licenseName = StringUtils.isNotBlank(d.getLicense_id()) ? d.getLicense_id()
          : (StringUtils.isNotBlank(d.getLicense_title()) ? d.getLicense_title() : "unknown");
      license = new DctLicenseDocument(d.getLicense_url(), licenseName, d.getLicense_id(), "",
          nodeId);

      // Keywords
      if (d.getTags() != null) {
        for (Tag t : d.getTags()) {
          keywords.addAll(Arrays.asList(t.getName().split(",")));
        }
      }

      // Dataset url is built from node host and dataset identifier
      // landingPage = d.getUrl();
      String nodeHost = node.getHost();

      if (StringUtils.isNotBlank(d.getUrl())) {
        landingPage = d.getUrl();
      } else {
        landingPage = nodeHost + (nodeHost.endsWith("/") ? "" : "/") + "opendata/" + d.getName();
      }

      // Distributions
      List<Resource> resourceList = d.getResources();
      if (resourceList != null) {
        for (Resource r : resourceList) {
          distributionList.add(resourceToDcat(r, landingPage, license));
        }
      }
    }

    if (d.getRelations() != null) {
      relatedResource = d.getRelations().stream().map(x -> x.getUrl()).collect(Collectors.toList());
    }
    DcatDataset mapped;
    mapped = new DcatDataset(nodeId, identifier, title, description, distributionList, themeList,
        publisher, contactPointList, keywords, accessRights, conformsTo, documentation, frequency,
        hasVersion, isVersionOf, landingPage, language, provenance, releaseDate, updateDate,
        otherIdentifier, sample, source, spatialCoverage, temporalCoverage, type, version,
        versionNotes, rightsHolder, creator, null, relatedResource);

    distributionList = null;
    publisher = null;
    contactPointList = null;

    return mapped;
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
  /*
   * Return a List of SKOSConcept, each of them containing a prefLabel from input
   * String list.
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

  /**
   * Extract value list.
   *
   * @param value the value
   * @return the list
   */
  private List<String> extractValueList(String value) {

    // TODO: regex & groups
    List<String> result = new ArrayList<String>();

    if (StringUtils.isBlank(value)) {
      return result;
    }

    if (value.startsWith("[")) {
      try {
        result.addAll(GsonUtil.json2Obj(value, GsonUtil.stringListType));
      } catch (GsonUtilException ex) {
        if (StringUtils.isNotBlank(value)) {
          for (String s : value.split(",")) {
            result.add(s);
          }
        } else {
          result = null;
        }
      }
    } else if (value.startsWith("{")) {
      for (String s : value.substring(1, value.lastIndexOf("}")).split(",")) {
        result.add(s);
      }
    } else {
      for (String s : value.split(",")) {
        result.add(s);
      }
    }

    return result;

  }

  /**
   * Extract conforms to.
   *
   * @param value the value
   * @return the list
   */
  private List<DctStandard> extractConformsTo(String value) {

    List<DctStandard> result = new ArrayList<DctStandard>();

    if (StringUtils.isBlank(value)) {
      return result;
    }

    if (value.startsWith("[")) {
      try {
        result.addAll(GsonUtil.json2Obj(value, GsonUtil.stringListType));
      } catch (GsonUtilException ex) {
        if (StringUtils.isNotBlank(value)) {
          for (String s : value.substring(1, value.lastIndexOf("}")).split(",")) {
            result.add(new DctStandard(DCTerms.conformsTo.getURI(), s, "", "",
                new ArrayList<String>(), nodeId));
          }
        } else {
          result = null;
        }
      }
    } else if (value.startsWith("{")) {
      for (String s : value.substring(1, value.lastIndexOf("}")).split(",")) {
        result.add(new DctStandard(DCTerms.conformsTo.getURI(), s, "", "", new ArrayList<String>(),
            nodeId));
      }
    } else {
      for (String s : value.split(",")) {
        result.add(new DctStandard(DCTerms.conformsTo.getURI(), s, "", "", new ArrayList<String>(),
            nodeId));
      }
    }
    return result;
  }

  /**
   * Resource to dcat.
   *
   * @param r                  the r
   * @param datasetLandingPage the dataset landing page
   * @param datasetLicense     the dataset license
   * @return the dcat distribution
   */
  private DcatDistribution resourceToDcat(Resource r, String datasetLandingPage,
      DctLicenseDocument datasetLicense) {

    String accessUrl = null;
    String description = null;
    String format = null;
    String downloadUrl = null;

    accessUrl = downloadUrl = StringUtils.isNotBlank(r.getUrl()) ? r.getUrl() : datasetLandingPage;
    description = r.getDescription();
    format = r.getFormat();
    String byteSize = null;
    byteSize = String.valueOf(r.getSize());
    SpdxChecksum checksum = null;
    checksum = new SpdxChecksum("http://spdx.org/rdf/terms#checksum", "checksumAlgorithm_sha1",
        r.getHash(), nodeId);
    // documentation = r.get ?
    // language = r.get ?
    // linkedSchemas = r.get ?
    String mediaType = null;
    mediaType = r.getMimetype();
    String releaseDate = null;
    releaseDate = StringUtils.isNotBlank(r.getCreated()) ? CommonUtil.fixBadUtcDate(r.getCreated())
        : "1970-01-01T00:00:00Z";
    String updateDate = null;
    updateDate = StringUtils.isNotBlank(r.getLast_modified())
        ? CommonUtil.fixBadUtcDate(r.getLast_modified())
        : "1970-01-01T00:00:00Z";
    // rights = r.get ?
    // status = r.get ?
    String title = null;
    title = r.getName();

    return new DcatDistribution(nodeId, accessUrl, description, format, datasetLicense, byteSize,
        checksum, new ArrayList<String>(), downloadUrl, new ArrayList<String>(),
        new ArrayList<DctStandard>(), mediaType, releaseDate, updateDate, null, null, title);
  }

  /**
   * Check if json object.
   *
   * @param input the input
   * @return true, if successful
   */
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

  /**
   * Handle error.
   *
   * @param e the e
   * @throws OdmsCatalogueNotFoundException  the odms catalogue not found
   *                                         exception
   * @throws OdmsCatalogueForbiddenException the odms catalogue forbidden
   *                                         exception
   * @throws OdmsCatalogueOfflineException   the odms catalogue offline exception
   * @throws CKANException                   the CKAN exception
   */
  public void handleError(CKANException e) throws OdmsCatalogueNotFoundException,
      OdmsCatalogueForbiddenException, OdmsCatalogueOfflineException, CKANException {

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
        throw new CKANException(e.getMessage());
      }

    } else {
      throw new CKANException("Unknown CKAN Exception");
    }
  }
}
