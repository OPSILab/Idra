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
import com.google.gson.Gson;
import it.eng.idra.beans.dcat.DcatDataset;
import it.eng.idra.beans.dcat.DcatDistribution;
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
import it.eng.idra.beans.odms.OdmsCatalogueOfflineException;
import it.eng.idra.beans.odms.OdmsSynchronizationResult;
import it.eng.idra.beans.opendatasoft.Dataset;
import it.eng.idra.beans.opendatasoft.DatasetDto;
import it.eng.idra.beans.opendatasoft.DatasourceDto;
import it.eng.idra.beans.opendatasoft.InnerDatasetMetaDefault;
import it.eng.idra.beans.opendatasoft.Link;
import it.eng.idra.utils.CommonUtil;
import it.eng.idra.utils.restclient.RestClient;
import it.eng.idra.utils.restclient.RestClientImpl;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import org.apache.http.HttpResponse;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

// TODO: Auto-generated Javadoc
/**
 * The Class OpenDataSoftConnector.
 */
public class OpenDataSoftConnector implements IodmsConnector {

  /** The Constant datasetsPath. */
  private static final String datasetsPath = "/api/v2/catalog/datasets";

  /** The node id. */
  private String nodeId;

  /** The node. */
  private OdmsCatalogue node;

  /** The logger. */
  private static Logger logger = LogManager.getLogger(OpenDataSoftConnector.class);

  /**
   * Instantiates a new open data soft connector.
   *
   * @param node the node
   */
  public OpenDataSoftConnector(OdmsCatalogue node) {
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
    String sjson = sendGetRequest(node.getHost().concat(datasetsPath).concat("?rows=0"));
    JSONObject jjson = new JSONObject(sjson);

    int count = jjson.getInt("total_count");
    if (count == 0) {
      throw new OdmsCatalogueOfflineException(" The ODMS node is currently unreachable");
    }
    return count;
  }

  /**
   * Distribution to dcat.
   *
   * @param datasource the datasource
   * @param license    the license
   * @param node       the node
   * @return the dcat distribution
   */
  public DcatDistribution distributionToDcat(Link datasource, String license, OdmsCatalogue node) {
    String format = datasource.getRel();

    if ("self".equalsIgnoreCase(format)) {
      return null;
    }

    String href = datasource.getHref();

    DcatDistribution dcatDistrib = new DcatDistribution(nodeId);
    dcatDistrib.setDownloadUrl(href);
    dcatDistrib.setAccessUrl(href);

    dcatDistrib.setMediaType(format);
    dcatDistrib.setFormat(format);
    dcatDistrib.setTitle(format);

    dcatDistrib.setLicense_name(license);

    return dcatDistrib;
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.connectors.IodmsConnector#datasetToDcat(java.lang.Object,
   * it.eng.idra.beans.odms.OdmsCatalogue)
   */
  @Override
  public DcatDataset datasetToDcat(Object dataset, OdmsCatalogue node) throws Exception {

    Dataset ds = (Dataset) dataset;
    InnerDatasetMetaDefault metadata = ds.getDataset().getMetas().get_default();

    String license = metadata.getLicense();

    List<DcatDistribution> distributionList = new ArrayList<DcatDistribution>();
    for (Link link : ds.getLinks()) {

      if ("exports".equals(link.getRel())) {
        DatasourceDto datasourcesDto = new Gson().fromJson(sendGetRequest(link.getHref()),
            DatasourceDto.class);
        datasourcesDto.getLinks().forEach(l -> {
          Optional<DcatDistribution> optDcatDistrib = Optional
              .ofNullable(distributionToDcat(l, license, node));
          if (optDcatDistrib.isPresent()) {
            distributionList.add(optDcatDistrib.get());
          }
        });

        break;
      }

    }

    String identifier = ds.getDataset().getDatasetId();
    List<String> otherIdentifier = new ArrayList<String>();
    otherIdentifier.add(identifier);

    String landingPage = node.getHost().concat("/explore/dataset/").concat(identifier);

    // Get update Date
    String updateDate = CommonUtil.fixBadUtcDate(metadata.getModified());

    // Extract languages
    List<String> languages = new ArrayList<String>();
    Optional<String> lang = Optional.ofNullable(metadata.getLanguage());
    if (lang.isPresent()) {
      languages.add(lang.get());
    }

    List<VcardOrganization> contactPointList = new ArrayList<VcardOrganization>();
    String accessRights = null;
    List<DctStandard> conformsTo = new ArrayList<DctStandard>();
    List<String> documentation = new ArrayList<String>();
    String frequency = null;
    List<String> hasVersion = new ArrayList<String>();
    List<String> isVersionOf = new ArrayList<String>();

    List<String> provenance = new ArrayList<String>();
    String releaseDate = null;

    List<String> sample = new ArrayList<String>();
    List<String> source = new ArrayList<String>();
    DctLocation spatialCoverage = null;
    DctPeriodOfTime temporalCoverage = null;
    String type = null;
    String version = null;
    List<String> versionNotes = new ArrayList<String>();
    FoafAgent rightsHolder = null;
    List<SkosConceptSubject> subjectList = new ArrayList<SkosConceptSubject>();
    List<String> relatedResources = new ArrayList<String>();

    String title = metadata.getTitle();
    String description = metadata.getDescription();

    List<SkosConceptTheme> datasetTheme = extractConceptList(DCAT.theme.getURI(),
        metadata.getTheme(), SkosConceptTheme.class);
    Optional<String> publisherName = Optional.ofNullable(metadata.getPublisher());
    Optional<String> publisherUri = Optional.empty();
    Optional<String> publisherMbox = Optional.empty();
    Optional<String> publisherHomepage = Optional.empty();
    Optional<String> publisherType = Optional.empty();
    Optional<String> publisherIdentifier = Optional.empty();

    FoafAgent publisher = new FoafAgent(DCTerms.publisher.getURI(), publisherUri.orElse(null),
        publisherName.orElse(null), publisherMbox.orElse(null), publisherHomepage.orElse(null),
        publisherType.orElse(null), publisherIdentifier.orElse(null), nodeId);

    List<String> keywords = metadata.getKeyword();

    DcatDataset mapped = new DcatDataset(nodeId, identifier, title, description, distributionList,
        datasetTheme, publisher, contactPointList, keywords, accessRights, conformsTo,
        documentation, frequency, hasVersion, isVersionOf, landingPage, languages, provenance,
        releaseDate, updateDate, otherIdentifier, sample, source, spatialCoverage, temporalCoverage,
        type, version, versionNotes, rightsHolder, publisher, subjectList, relatedResources);

    return mapped;
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.connectors.IodmsConnector#getDataset(java.lang.String)
   */
  @Override
  public DcatDataset getDataset(String datasetId) throws Exception {
    String sjson = sendGetRequest(node.getHost().concat(datasetsPath).concat(datasetId));
    Dataset dataset = new Gson().fromJson(sjson, Dataset.class);
    return datasetToDcat(dataset, node);
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.connectors.IodmsConnector#getAllDatasets()
   */
  @Override
  public List<DcatDataset> getAllDatasets() throws Exception {

    /*
     *
     * Get all the datasets in the ODMS. The OpenDataSoft portals allows only to
     * retrieve 100 rows per request. So here a pagination mechanism is necessary.
     *
     */

    Integer startIndex = 0;
    Integer totDatasets = 0;
    boolean repeat = false;
    Set<DcatDataset> out = new HashSet<DcatDataset>();

    do {
      String url = node.getHost().concat(datasetsPath).concat("?rows=100&start=")
          .concat(String.valueOf(startIndex));
      logger.debug(url);
      String sjson = sendGetRequest(url);
      DatasetDto datasets = new Gson().fromJson(sjson, DatasetDto.class);

      totDatasets = datasets.getTotalCount();

      if (startIndex == 0) {
        logger.info(totDatasets + " total datasets found");
      }

      Integer currentDatasetNumber = datasets.getDatasets().size();
      logger.debug("Took " + currentDatasetNumber + " datasets");
      datasets.getDatasets().stream().parallel().forEach(d -> {
        try {
          out.add(datasetToDcat(d, node));
        } catch (Exception e) {
          e.printStackTrace();
        }
      });

      startIndex += currentDatasetNumber;
      logger.info(out.size() + " evaluated");

      repeat = totDatasets > startIndex;
      logger.debug("repeat: " + repeat);
    } while (repeat);

    return new ArrayList<DcatDataset>(out);
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

    if (concepts != null) {
      for (String label : concepts) {
        try {
          result.add(type.getDeclaredConstructor(SkosConcept.class).newInstance(new SkosConcept(
              propertyUri, "", Arrays.asList(new SkosPrefLabel("", label, nodeId)), nodeId)));
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
            | InvocationTargetException | NoSuchMethodException | SecurityException e) {
          e.printStackTrace();
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }

    return result;
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

}
