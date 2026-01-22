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

package it.eng.idra.connectors;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.eng.idra.beans.dcat.DcatDataset;
import it.eng.idra.beans.dcat.DcatDatasetSeries;
import it.eng.idra.beans.dcat.DcatDetails;
import it.eng.idra.beans.dcat.DcatDistribution;
import it.eng.idra.beans.dcat.DctLocation;
import it.eng.idra.beans.dcat.DctPeriodOfTime;
import it.eng.idra.beans.dcat.DctStandard;
import it.eng.idra.beans.dcat.FoafAgent;
import it.eng.idra.beans.dcat.Relationship;
import it.eng.idra.beans.dcat.SkosConcept;
import it.eng.idra.beans.dcat.SkosConceptTheme;
import it.eng.idra.beans.dcat.SkosPrefLabel;
import it.eng.idra.beans.dcat.VcardOrganization;
import it.eng.idra.beans.exception.DatasetNotValidException;
import it.eng.idra.beans.odms.OdmsCatalogue;
import it.eng.idra.beans.odms.OdmsSynchronizationResult;
import it.eng.idra.beans.webscraper.DatasetSelector;
import it.eng.idra.beans.webscraper.WebScraperSelector;
import it.eng.idra.beans.webscraper.WebScraperSelectorType;
import it.eng.idra.connectors.webscraper.WebScraper;
import it.eng.idra.management.FederationCore;
import it.eng.idra.utils.CommonUtil;
import it.eng.idra.utils.GsonUtil;
import it.eng.idra.utils.GsonUtilException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

// TODO: Auto-generated Javadoc
/**
 * The Class WebConnector.
 */
public class WebConnector implements IodmsConnector {

  /** The node id. */
  private String nodeId;

  /** The node. */
  private OdmsCatalogue node;

  /** The logger. */
  private static Logger logger = LogManager.getLogger(WebConnector.class);

  /** The static pattern. */
  private static Pattern staticPattern = Pattern.compile("(distribution_)(\\d)_(\\w+)");

  /** The dinamic pattern. */
  private static Pattern dinamicPattern = Pattern.compile("(distribution_)((?!((\\d+)_)).*)$");

  /** The shift pattern. */
  private static Pattern shiftPattern = Pattern.compile("div:nth-of-type\\((\\d+)\\)");

  /** The download url pattern. */
  private static Pattern downloadUrlPattern = Pattern.compile("\\w*\\(([^)]+)\\);*");

  /**
   * Instantiates a new web connector.
   */
  public WebConnector() {
  }

  /**
   * Instantiates a new web connector.
   *
   * @param node the node
   */
  public WebConnector(OdmsCatalogue node) {
    this.node = node;
    this.nodeId = String.valueOf(node.getId());
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.connectors.IodmsConnector#findDatasets(java.util.HashMap)
   */
  // Live search is not available
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
    // Return -1 in order to keep the node as ONLINE, but is not possible to
    // retrieve actual Datasets count from a WEB node
    try {
      Document doc = Jsoup.connect(node.getHost()).get();
      return (doc != null) ? -1 : 0;

    } catch (Exception e) {
      e.printStackTrace();
      return 0;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.eng.idra.connectors.IodmsConnector#datasetToDcat(java.lang.Object,
   * it.eng.idra.beans.odms.OdmsCatalogue)
   */
  @Override
  public DcatDataset datasetToDcat(Object dataset, OdmsCatalogue node)
      throws DatasetNotValidException {

    List<DatasetSelector> selectors = new ArrayList<DatasetSelector>();
    List<DatasetSelector> distrSelectors = new ArrayList<DatasetSelector>();

    String identifier = null;
    String title = null;
    String description = null;
    String accessRights = null;
    String frequency = null;
    String landingPage = null;
    String releaseDate = null;
    String updateDate = null;
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
    String holderType = null;
    String creatorIdentifier = null;
    String creatorUri = null;
    String creatorName = null;
    String creatorMbox = null;
    String creatorHomepage = null;
    String creatorType = null;
    String startDate = null;
    String endDate = null;
    String vcardFn = null;
    String vcardHasEmail = null;
    String vcardHasTelephone = null;
    String vcardHasUrl = null;
    String conformsToIdentifier = null;
    String conformsToTitle = null;
    String conformsToDescription = null;
    String conformsToReferenceDocumentation = null;
    FoafAgent publisher = null;
    FoafAgent rightsHolder = null;
    FoafAgent creator = null;
    List<VcardOrganization> contactPointList = new ArrayList<VcardOrganization>();
    DctPeriodOfTime temporalCoverage = null;
    DctLocation spatialCoverage = null;
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
    List<String> sample = new ArrayList<String>();
    List<String> source = new ArrayList<String>();
    List<String> versionNotes = new ArrayList<String>();
    List<String> subject = new ArrayList<String>();
    List<DcatDistribution> distributionList = new ArrayList<DcatDistribution>();

    // new
    String bbox = null;
    String centroid = null;
    String beginning = null;
    String end = null;
    List<String> applicableLegislation = new ArrayList<String>();
    List<DctLocation> geographicalCoverage = new ArrayList<DctLocation>();
    List<DcatDetails> descriptions = new ArrayList<DcatDetails>();
    List<DcatDetails> titles = new ArrayList<DcatDetails>();
    List<DctPeriodOfTime> temporalCoverageList = new ArrayList<DctPeriodOfTime>();
    List<DcatDatasetSeries> inSeries = new ArrayList<DcatDatasetSeries>();
    List<Relationship> qualifiedRelation = new ArrayList<Relationship>();
    String temporalResolution = null;
    List<String> wasGeneratedBy = new ArrayList<String>();
    List<String> HVDCategory = new ArrayList<String>();
    List<String> names = new ArrayList<String>();
    DcatDatasetSeries datasetSeries = null;

    Document doc = (Document) dataset;

    if (StringUtils.isBlank(landingPage)) {
      landingPage = doc.baseUri();
    }

    identifier = landingPage;
    /*
     * Divide Dataset selectors from the ones related to the Distribution. Scrape
     * first Distributions -> If they are > 1 -> Increments by one the N value of
     * all the Dataset selectors of type
     * "div:nth-of-type(N). In order to manage the DIV shifting caused by the Distribution divs"
     */

    Map<Boolean, List<DatasetSelector>> partitions = node.getSitemap().getDatasetSelectors()
        .stream().collect(Collectors.partitioningBy(d -> d.getName().startsWith("distribution_")));
    distrSelectors.addAll(partitions.get(true));
    selectors.addAll(partitions.get(false));

    /*
     * ** First fetch all the Distributions. Its number is used to manage a possible
     * DIV shifting
     */
    // DIV shifting
    distributionList.addAll(manageDistributionSelectors(distrSelectors, null, doc));
    int distrNumber = distributionList.size();
    int shift = distrNumber > 1 ? distrNumber - 1 : distrNumber == 0 ? -1 : 0;

    for (DatasetSelector selector : selectors) {

      /*
       * ** Apply the shift if needed
       */
      String cssSelector = selector.getSelector().replaceAll("'", "");
      if (shift != 0) {
        Matcher shiftMatcher = shiftPattern.matcher(cssSelector);
        String argument = null;
        if (shiftMatcher.find() && (argument = shiftMatcher.group(1)) != null) {
          int argValue = Integer.parseInt(argument);
          cssSelector = cssSelector.replace("(" + argValue + ")", "(" + (argValue + shift) + ")");
        }

      }

      /*
       * ** Extract the values from the HTML document using the Dataset Selector
       */

      List<String> extractedValues = fetchMultipleValuesBySelector(doc, selector);

      /*
       * If there are no extracted values, skip most selectors but still allow
       * fallbacks for title/description to populate datasets.
       */
      if (extractedValues.size() == 0
          && !"title".equalsIgnoreCase(selector.getName())
          && !"description".equalsIgnoreCase(selector.getName())) {
        continue;
      }

      switch (selector.getName()) {

        case "title":
          // Prefer extracted value when present, else robust fallbacks
          title = !extractedValues.isEmpty() ? extractedValues.get(0) : null;
          if (StringUtils.isBlank(title)) {
            String h1 = doc.select("h1").stream().map(Element::text)
                .filter(StringUtils::isNotBlank).findFirst().orElse(null);
            if (StringUtils.isNotBlank(h1)) { title = h1; }
          }
          if (StringUtils.isBlank(title)) {
            String h2 = doc.select("h2").stream().map(Element::text)
                .filter(StringUtils::isNotBlank).findFirst().orElse(null);
            if (StringUtils.isNotBlank(h2)) { title = h2; }
          }
          if (StringUtils.isBlank(title)) {
            title = StringUtils.defaultIfBlank(doc.title(), null);
          }
          if (StringUtils.isBlank(title)
              || WebScraperSelector.getDefaultStopValues().contains(title)
              || (selector.getStopValues() != null && selector.getStopValues().contains(title))
              || "Referente :".equals(title)) {
            throw new DatasetNotValidException("The value "
                + title + " for the selector: " + selector.getName()
                + " is a stopValue or is empty then not valid; dataset with URL: "
                + doc.baseUri() + " was skipped");
          }
          break;
        case "description":
          description = extractedValues.size() > 0 ? extractedValues.get(0) : null;
          if (StringUtils.isBlank(description)) {
            String metaDesc = doc.select("meta[name=description]").attr("content");
            if (StringUtils.isNotBlank(metaDesc)) {
              description = metaDesc;
            } else {
              String pText = doc.select("p").stream().map(Element::text)
                  .filter(StringUtils::isNotBlank).findFirst().orElse("");
              description = pText;
            }
          }
          break;
        case "publisher_name":
          publisherName = extractedValues.size() > 0 ? extractedValues.get(0) : null;
          break;
        case "publisher_mbox":
          publisherMbox = extractedValues.size() > 0 ? extractedValues.get(0) : null;
          break;
        case "publisher_homepage":
          publisherHomepage = extractedValues.size() > 0 ? extractedValues.get(0) : null;
          break;
        case "publisher_type":
          publisherType = extractedValues.size() > 0 ? extractedValues.get(0) : null;
          break;
        case "publisher_identifier":
          publisherIdentifier = extractedValues.size() > 0 ? extractedValues.get(0) : null;
          break;
        case "publisher_uri":
          publisherUri = extractedValues.size() > 0 ? extractedValues.get(0) : null;
          break;
        case "contact_fn":
          vcardFn = extractedValues.size() > 0 ? extractedValues.get(0) : null;
          break;
        case "contact_email":
          vcardHasEmail = extractedValues.size() > 0 ? extractedValues.get(0) : null;
          break;
        case "contact_telephone":
          vcardHasTelephone = extractedValues.size() > 0 ? extractedValues.get(0) : null;
          break;
        case "contact_url":
          vcardHasUrl = extractedValues.size() > 0 ? extractedValues.get(0) : null;
          break;
        case "keywords":
          extractedValues.stream().filter(StringUtils::isNoneBlank)
              .forEach(x -> Arrays.stream(x.trim().split(",")).forEach(k -> keywords.add(k)));
          break;
        case "accessRights":
          accessRights = extractedValues.size() > 0 ? extractedValues.get(0) : null;
          break;
        case "conformsTo_identifier":
          conformsToIdentifier = extractedValues.size() > 0 ? extractedValues.get(0) : null;
          break;
        case "conformsTo_title":
          conformsToTitle = extractedValues.size() > 0 ? extractedValues.get(0) : null;
          break;
        case "conformsTo_description":
          conformsToDescription = extractedValues.size() > 0 ? extractedValues.get(0) : null;
          break;
        case "conformsTo_referenceDocumentation":
          conformsToReferenceDocumentation = extractedValues.size() > 0 ? extractedValues.get(0) : null;
          break;
        case "documentation":
          documentation.addAll(extractedValues);
          break;
        case "frequency":
          frequency = extractedValues.size() > 0 ? extractedValues.get(0) : null;
          break;
        case "hasVersion":
          if (!extractedValues.isEmpty()) { hasVersion.add(extractedValues.get(0)); }
          break;
        case "isVersionOf":
          if (!extractedValues.isEmpty()) { isVersionOf.add(extractedValues.get(0)); }
          break;
        case "landingPage":
          landingPage = extractedValues.size() > 0 ? extractedValues.get(0) : landingPage;
          break;
        case "language":
          if (!extractedValues.isEmpty()) { language.add(extractedValues.get(0)); }
          break;
        case "provenance":
          if (!extractedValues.isEmpty()) { provenance.add(extractedValues.get(0)); }
          break;
        case "releaseDate":
          try {
            if (!extractedValues.isEmpty()) {
              releaseDate = CommonUtil.fromLocalToUtcDate(extractedValues.get(0), null);
            }
          } catch (IllegalArgumentException ignore) {
            logger.debug(ignore.getLocalizedMessage());
          }
          break;
        case "updateDate":
          try {
            if (!extractedValues.isEmpty()) {
              updateDate = CommonUtil.fromLocalToUtcDate(extractedValues.get(0), null);
            }
          } catch (IllegalArgumentException ignore) {
            logger.debug(ignore.getLocalizedMessage());
          }
          break;
        case "source":
          if (!extractedValues.isEmpty()) { source.add(extractedValues.get(0)); }
          break;
        case "sample":
          if (!extractedValues.isEmpty()) { sample.add(extractedValues.get(0)); }
          break;
        case "spatialCoverage_geographicalIdentifier":
        case "geographicalCoverage_geographicalIdentifier":
          geographicalIdentifier = extractedValues.size() > 0 ? extractedValues.get(0) : geographicalIdentifier;
          break;
        case "spatialCoverage_geographicalName":
        case "geographicalCoverage_geographicalName":
          geographicalName = extractedValues.size() > 0 ? extractedValues.get(0) : geographicalName;
          break;
        case "spatialCoverage_geometry":
        case "geographicalCoverage_geometry":
          geometry = extractedValues.size() > 0 ? extractedValues.get(0) : geometry;
          break;
        case "temporalCoverage_startDate":
          try {
            if (!extractedValues.isEmpty()) {
              startDate = CommonUtil.fromLocalToUtcDate(extractedValues.get(0), null);
            }
          } catch (IllegalArgumentException ignore) {
            logger.debug(ignore.getLocalizedMessage());
          }
          break;
        case "temporalCoverage_endDate":
          try {
            if (!extractedValues.isEmpty()) {
              endDate = CommonUtil.fromLocalToUtcDate(extractedValues.get(0), null);
            }
          } catch (IllegalArgumentException ignore) {
            logger.debug(ignore.getLocalizedMessage());
          }
          break;
        case "type":
          type = extractedValues.size() > 0 ? extractedValues.get(0) : type;
          break;
        case "version":
          version = extractedValues.size() > 0 ? extractedValues.get(0) : version;
          break;
        case "versionNotes":
          if (!extractedValues.isEmpty()) { versionNotes.add(extractedValues.get(0)); }
          break;
        case "rightsHolder_name":
          holderName = extractedValues.size() > 0 ? extractedValues.get(0) : holderName;
          break;
        case "rightsHolder_mbox":
          holderMbox = extractedValues.size() > 0 ? extractedValues.get(0) : holderMbox;
          break;
        case "rightsHolder_homepage":
          holderName = extractedValues.size() > 0 ? extractedValues.get(0) : holderName;
          break;
        case "rightsHolder_type":
          holderType = extractedValues.size() > 0 ? extractedValues.get(0) : holderType;
          break;
        case "rightsHolder_uri":
          holderUri = extractedValues.size() > 0 ? extractedValues.get(0) : holderUri;
          break;
        case "rightsHolder_identifier":
          holderIdentifier = extractedValues.size() > 0 ? extractedValues.get(0) : holderIdentifier;
          break;
        case "creator_name":
          creatorName = extractedValues.size() > 0 ? extractedValues.get(0) : creatorName;
          break;
        case "creator_mbox":
          creatorMbox = extractedValues.size() > 0 ? extractedValues.get(0) : creatorMbox;
          break;
        case "creator_homepage":
          creatorHomepage = extractedValues.size() > 0 ? extractedValues.get(0) : creatorHomepage;
          break;
        case "creator_type":
          creatorType = extractedValues.size() > 0 ? extractedValues.get(0) : creatorType;
          break;
        case "creator_uri":
          creatorUri = extractedValues.size() > 0 ? extractedValues.get(0) : creatorUri;
          break;
        case "creator_identifier":
          creatorIdentifier = extractedValues.size() > 0 ? extractedValues.get(0) : creatorIdentifier;
          break;
        case "subject":
          if (!extractedValues.isEmpty()) { subject.add(extractedValues.get(0)); }
          break;
        case "theme":
          if (!extractedValues.isEmpty()) {
            themeList.addAll(extractConceptList(DCAT.theme.getURI(),
                extractValueList(extractedValues.get(0)), SkosConceptTheme.class));
          }
          break;
        case "bbox":
          bbox = extractedValues.size() > 0 ? extractedValues.get(0) : bbox;
          break;
        case "centroid":
          centroid = extractedValues.size() > 0 ? extractedValues.get(0) : centroid;
          break;
        case "has_beginning":
        case "beginning":
          beginning = extractedValues.size() > 0 ? extractedValues.get(0) : beginning;
          break;
        case "has_end":
        case "end":
          end = extractedValues.size() > 0 ? extractedValues.get(0) : end;
          break;
        case "applicable_legislation":
        case "applicableLegislation":
          if (!extractedValues.isEmpty()) { applicableLegislation.addAll(extractValueList(extractedValues.get(0))); }
          break;
        /*
         * case "in_series":
         * case "inSeries":
         * if (checkIfJsonArray(extractedValues.get(0))) {
         * JSONArray array = new JSONArray(extractedValues.get(0));
         * for (int i = 0; i < array.length(); i++) {
         * JSONObject seriesObj = array.getJSONObject(i);
         * 
         * // DcatDetails dcatDetails = new DcatDetails();
         * // dcatDetails.setTitle(title);
         * // dcatDetails.setDescription(description);
         * // Extracting properties from JSON
         * applicableLegislation.addAll(extractValueList(seriesObj.optString(
         * "applicableLegislation")));//
         * .addAll(extractValueList(extractedValues.get(0)));
         * // descriptions.add(dcatDetails); //
         * // extractValueList(seriesObj.optString("description", "[]"));
         * frequency = seriesObj.optString("frequency", null);
         * // geographicalCoverage.add(spatialCoverage); //
         * // extractValueList(seriesObj.optString("geographicalCoverage",
         * // "[]"));
         * updateDate = seriesObj.optString("modificationDate", null);
         * // publisher = new FoafAgent(DCTerms.publisher.getURI(), publisherUri,
         * // publisherName != null
         * // ? Collections.singletonList(publisherName)
         * // : Collections.emptyList(),
         * // publisherMbox, publisherHomepage, publisherType, publisherIdentifier,
         * // nodeId);
         * releaseDate = seriesObj.optString("releaseDate", null);
         * // temporalCoverage = new DctPeriodOfTime(DCTerms.temporal.getURI(),
         * startDate,
         * // endDate,
         * // nodeId, beginning, end, identifier);
         * // temporalCoverageList.add(temporalCoverage);
         * // titles.add(dcatDetails); // extractValueList(seriesObj.optString("title",
         * // "[]"));
         * 
         * // Create part of the DcatDatasetSeries object DcatDatasetSeries series
         * datasetSeries = new DcatDatasetSeries(
         * applicableLegislation,
         * null, // contactPointList,
         * null, // descriptions,
         * frequency,
         * null, // geographicalCoverage,
         * updateDate,
         * null, // publisher,
         * releaseDate,
         * null, // temporalCoverageList,
         * null, // titles,
         * String.valueOf(node.getId()),
         * identifier);
         * 
         * // Add to the list
         * // inSeries.add(series);
         * }
         * }
         * break;
         */
        case "qualified_relation":
        case "qualifiedRelation":
        case "relationship":
          if (extractedValues.isEmpty()) { break; }
          JsonElement jelement = JsonParser.parseString(extractedValues.get(0));
          JsonObject jobject = jelement.getAsJsonObject();
          Relationship relationship = new Relationship(jobject.get("had_role").getAsString(),
              jobject.get("relation").getAsString(), nodeId);
          qualifiedRelation.add(relationship); // addAll(extractValueList(e.getValue()));
          break;
        case "temporal_resolution":
        case "temporalResolution":
          temporalResolution = extractedValues.size() > 0 ? extractedValues.get(0) : temporalResolution;
          break;
        case "was_generated_by":
        case "wasGeneratedBy":
          if (!extractedValues.isEmpty()) { wasGeneratedBy.addAll(extractValueList(extractedValues.get(0))); }
          break;
        case "hvd_category":
        case "HVDCategory":
          if (!extractedValues.isEmpty()) { HVDCategory.addAll(extractValueList(extractedValues.get(0))); }
          break;
        case "name":
          if (!extractedValues.isEmpty()) { names.add(extractedValues.get(0)); }
          break;
        default:
          break;
      }

    }
    DcatDetails dcatDetails = new DcatDetails();
    dcatDetails.setTitle(title);
    dcatDetails.setDescription(description);
    descriptions.add(dcatDetails);
    titles.add(dcatDetails);

    if (StringUtils.isNotBlank(geographicalIdentifier) || StringUtils.isNotBlank(geographicalName)
        || StringUtils.isNotBlank(geometry)) {
      spatialCoverage = new DctLocation(DCTerms.spatial.getURI(), geographicalIdentifier,
          geographicalName, geometry, nodeId, bbox, centroid);// identifier
      geographicalCoverage.add(spatialCoverage);
    }

    if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
      temporalCoverage = new DctPeriodOfTime(DCTerms.temporal.getURI(), startDate, endDate, nodeId, beginning, end);// identifier
      temporalCoverageList.add(temporalCoverage);
    }

    // Contact Point
    String vcardUri = null;
    if (vcardUri != null || vcardFn != null || vcardHasEmail != null) {
      contactPointList.add(new VcardOrganization(DCAT.contactPoint.getURI(), vcardUri, vcardFn,
          vcardHasEmail, vcardHasUrl, vcardHasTelephone, "", nodeId));// identifier
    }

    // Publisher
    if (publisherUri != null || publisherName != null || publisherMbox != null
        || publisherHomepage != null || publisherType != null || publisherIdentifier != null) {
      publisher = new FoafAgent(DCTerms.publisher.getURI(), publisherUri, publisherName != null
          ? Collections.singletonList(publisherName)
          : Collections.emptyList(),
          publisherMbox, publisherHomepage, publisherType, publisherIdentifier, nodeId);
    }
    // Rights Holder
    String holderHomepage = null;
    if (holderUri != null || holderName != null || holderMbox != null || holderHomepage != null
        || holderType != null || holderIdentifier != null) {
      rightsHolder = new FoafAgent(DCTerms.rightsHolder.getURI(), holderUri, holderName != null
          ? Collections.singletonList(holderName)
          : Collections.emptyList(), holderMbox,
          holderHomepage, holderType, holderIdentifier, nodeId);
    }
    // Creator
    if (creatorUri != null || creatorName != null || creatorMbox != null || creatorHomepage != null
        || creatorType != null || creatorIdentifier != null) {
      creator = new FoafAgent(DCTerms.creator.getURI(), creatorUri, creatorName != null
          ? Collections.singletonList(creatorName)
          : Collections.emptyList(), creatorMbox,
          creatorHomepage, creatorType, creatorIdentifier, nodeId);
    }

    if (StringUtils.isBlank(releaseDate)) {
      releaseDate = "";
    }
    if (StringUtils.isBlank(updateDate)) {
      updateDate = releaseDate;
    }

    /*
     * if (datasetSeries != null) {
     * datasetSeries.setContactPoint(contactPointList);
     * datasetSeries.setDescription(descriptions);
     * datasetSeries.setTitle(titles);
     * datasetSeries.setGeographicalCoverage(geographicalCoverage);
     * datasetSeries.setTemporalCoverage(temporalCoverageList);
     * datasetSeries.setPublisher(publisher);
     * // Add to the list
     * inSeries.add(datasetSeries);
     * }
     */

    DcatDataset mapped;
    mapped = new DcatDataset(nodeId, identifier, title, description, distributionList, themeList,
        publisher, contactPointList, keywords, accessRights, new ArrayList<DctStandard>(),
        documentation, frequency, hasVersion, isVersionOf, landingPage, language, provenance,
        releaseDate, updateDate, new ArrayList<String>(), sample, source, geographicalCoverage,
        temporalCoverageList, type, version, versionNotes, rightsHolder, creator, null,
        new ArrayList<String>(), applicableLegislation, inSeries, qualifiedRelation, temporalResolution,
        wasGeneratedBy, HVDCategory);

    distributionList = null;
    publisher = null;
    contactPointList = null;

    return mapped;
  }

  /**
   * Extract one or more values from the HTML document, using the input Selector
   * and depending on its type (text, Link, ecc).
   *
   * @param selectors   the selectors
   * @param elementList the element list
   * @param document    the document
   * @return the list
   */

  /*
   * Extract the distribution from dataset page either through a list of
   * Underscored selectors (e.g. distribution_0_title) or directly from the list
   * of extracted multiple elements
   */

  private List<DcatDistribution> manageDistributionSelectors(List<DatasetSelector> selectors,
      Elements elementList, Document document) {

    Map<String, DcatDistribution> staticMap = new HashMap<String, DcatDistribution>();
    Map<String, DcatDistribution> dinamicMap = new HashMap<String, DcatDistribution>();

    List<DatasetSelector> dinamicSelectors = new ArrayList<DatasetSelector>();

    /*
     * **************STATIC DISTRIBUTION FETCH ***********************************
     * Retrieve distributions by using statically defined selectors (e.g
     * distribution_0_title)
     */
    for (DatasetSelector sel : selectors) {
      String selName = sel.getName().trim();
      Matcher staticMatcher = staticPattern.matcher(selName);
      Matcher dinamicMatcher = dinamicPattern.matcher(selName);

      if (staticMatcher.find()) {

        // Extract index from selector name and check if the distribution is already in
        // the Map

        String index = null;

        // Get selector name and call the relative Distribution setter method, in order
        // to either complete or create a new Distribution
        if ((index = staticMatcher.group(2)) != null) {
          DcatDistribution distr = staticMap.getOrDefault(index, new DcatDistribution(nodeId));

          try {
            Method method = distr.getClass()
                .getMethod("set" + WordUtils.capitalize(staticMatcher.group(3)), String.class);

            method.invoke(distr, fetchValueBySelector(document, sel));
            staticMap.put(index, distr);

          } catch (Exception e) {
            logger.info("Error while retrieving Distribution " + "setter method from Selector Name:"
                + selName + " - " + e.getMessage());
          }
        }

      } else if (dinamicMatcher.find()) {

        /*
         * ************* DINAMIC DISTRIBUTIONS FETCH ****************************
         * Retrieve distributions by using selectors for a page with a list of multiple
         * similar items (e.g. distribution_title )
         */
        // TODO Se il selettore è diverso da downloadURL, se matcha più di un elemento,
        // controllare che siano in numero uguali a quelli matchati da downloadURL.
        // Se matcha un elemento, si considera che i campi diversi da downloadURL siano
        // unici per tutte le distribution, perchè sono relativi al dataset intero

        String field = null;

        if ((field = dinamicMatcher.group(2)) != null) {

          if (field.equals("downloadURL")) {

            for (String downloadUrlValue : fetchMultipleValuesBySelector(document, sel)) {
              DcatDistribution dist = new DcatDistribution(nodeId);
              dist.setDownloadUrl(downloadUrlValue);
              dist.setAccessUrl(downloadUrlValue);
              dinamicMap.put(downloadUrlValue, dist);
            }

          } else {
            // Collect other dinamic selectors to be managed out of the iteration and after
            // downloadURLs have been fetched
            dinamicSelectors.add(sel);
          }

        }
      }
    }

    // If dinamicMap has been populated and the other dinamic selectors have been
    // collected,
    // tries to complete with other selectors the distributions created for each
    // downloadURL

    if (!dinamicMap.isEmpty() && !dinamicSelectors.isEmpty()) {

      for (DatasetSelector sel : dinamicSelectors) {
        String selName = sel.getName().trim();
        Matcher dinamicMatcher = dinamicPattern.matcher(selName);

        // Fetch Values from multiple Elements from the dataset page, by using current
        // selector (if it is a dinamic Selector: e.g. distribution_title)
        if (dinamicMatcher.find()) {
          List<String> values = fetchMultipleValuesBySelector(document, sel);

          if (!values.isEmpty()) {

            // Apply the single fetched value to each distribution
            if (values.size() == 1) {

              for (Map.Entry<String, DcatDistribution> entry : dinamicMap.entrySet()) {
                try {
                  DcatDistribution distr = entry.getValue();
                  Method method = distr.getClass().getMethod(
                      "set" + WordUtils.capitalize(dinamicMatcher.group(2)), String.class);

                  method.invoke(distr, values.get(0));
                  dinamicMap.put(entry.getKey(), distr);
                } catch (Exception e) {
                  logger.info("Error while retrieving Distribution "
                      + "setter method from Selector Name:" + selName + " - " + e.getMessage());
                }
              }

            } else if (values.size() == dinamicMap.size()) {
              ListIterator<String> valuesIt = values.listIterator();
              Iterator<Entry<String, DcatDistribution>> entriesIt = dinamicMap.entrySet()
                  .iterator();

              while (valuesIt.hasNext()) {
                try {
                  Entry<String, DcatDistribution> entry = entriesIt.next();
                  DcatDistribution distr = entry.getValue();
                  Method method = distr.getClass().getMethod(
                      "set" + WordUtils.capitalize(dinamicMatcher.group(2)), String.class);

                  method.invoke(distr, valuesIt.next());
                  dinamicMap.put(entry.getKey(), distr);
                } catch (Exception e) {
                  logger.info("Error while retrieving Distribution "
                      + "setter method from Selector Name:" + selName + " - " + e.getMessage());
                }
              }
            }
          }
        }
      }

    }

    // Return the Distribution list coming from relative Map, also try to extract
    // format from file extension, if format field is empty
    if (dinamicMap.isEmpty()) {
      return staticMap.values().stream().map(distr -> {
        return StringUtils.isNotBlank(distr.getFormat().getValue()) ? distr
            : distr.setFormat(
                CommonUtil.extractFormatFromFileExtension(distr.getDownloadUrl().getValue()));
      }).collect(Collectors.toList());
    } else {
      return dinamicMap.values().stream().map(distr -> {
        return StringUtils.isNotBlank(distr.getFormat().getValue()) ? distr
            : distr.setFormat(
                CommonUtil.extractFormatFromFileExtension(distr.getDownloadUrl().getValue()));
      }).collect(Collectors.toList());
    }

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
   * @see it.eng.idra.connectors.IodmsConnector#getAllDatasets()
   */
  @Override
  public List<DcatDataset> getAllDatasets() throws Exception {

    /*
     * Call the WebScraper to get the Dataset Documents to be mapped
     */
    List<Document> docs = WebScraper.getDatasetsDocument(node.getSitemap());

    AtomicInteger counter = new AtomicInteger(0);
    List<DcatDataset> totalDatasets = docs.stream().map(doc -> {
      try {
        return datasetToDcat(doc, node);
      } catch (DatasetNotValidException e) {
        logger.info(e.getMessage());
        counter.getAndIncrement();
        return null;
      }
      // }).filter(item -> item != null).filter(distinctByKey(p ->
      // p.getTitle())).collect(Collectors.toList());
    }).filter(item -> item != null).collect(Collectors.toList());

    logger.info("Skipped Web datasets when mapping: " + counter.get() + "/" + docs.size());
    logger.info("Final mapped and returned datasets: " + totalDatasets.size() + "/" + docs.size());

    return totalDatasets;

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
    int changed = 0;

    /// Find added datasets
    // difference(current,present)
    SetView<DcatDataset> diff = Sets.difference(newSets, oldSets);
    logger.info("New Packages: " + diff.size());
    for (DcatDataset d : diff) {
      syncrhoResult.addToAddedList(d);
      added++;
    }

    // Find removed datasets
    // difference(present,current)
    SetView<DcatDataset> diff1 = Sets.difference(oldSets, newSets);
    logger.info("Deleted Packages: " + diff1.size());
    for (DcatDataset d : diff1) {
      syncrhoResult.addToDeletedList(d);
      deleted++;
    }

    // Find updated datasets
    // intersection(present,current)
    SetView<DcatDataset> intersection = Sets.intersection(newSets, oldSets);
    logger.fatal("Changed Packages: " + intersection.size());

    for (DcatDataset d : intersection) {
      syncrhoResult.addToChangedList(d);
      changed++;
    }

    logger.info("Changed " + syncrhoResult.getChangedDatasets().size());
    logger.info("Added " + syncrhoResult.getAddedDatasets().size());
    logger.info("Deleted " + syncrhoResult.getDeletedDatasets().size());
    logger.info("Expected new dataset count: " + (node.getDatasetCount() - deleted + added));

    return syncrhoResult;
  }

  /**
   * Distinct by key.
   *
   * @param <T>          the generic type
   * @param keyExtractor the key extractor
   * @return the predicate
   */
  private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
    Map<Object, Boolean> map = new ConcurrentHashMap<>();
    return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
  }

  /**
   * Fetch multiple values by selector.
   *
   * @param document the document
   * @param sel      the sel
   * @return the list
   */
  private static List<String> fetchMultipleValuesBySelector(Document document,
      DatasetSelector sel) {
    Elements extractedElements = document.select(sel.getSelector().replaceAll("'", ""));
    List<String> extractedValues = new ArrayList<>();

    switch (sel.getType()) {

      case SelectorElementAttribute:
        extractedValues = extractedElements.stream().map(e -> {

          String extractedAttr = e.attr(sel.getExtractAttribute());
          /*
           * If the selector is downloadURL, extract the url
           */
          if (sel.getName().contains("downloadURL")) {
            extractedAttr = extractDownloadUrl(extractedAttr);
          }

          return extractedAttr;
        }).collect(Collectors.toList());

        break;
      case SelectorText:
        extractedValues = extractedElements.stream().map(e -> {

          /*
           * ** Extract the text from the HTML element with Regex if any in the selector
           */
          String extractedText = e.text();
          String regex = sel.getRegex();
          if (StringUtils.isNotBlank(regex)) {
            Matcher regexMatcher = Pattern.compile(regex).matcher(extractedText);
            if (regexMatcher.find()) {
              extractedText = regexMatcher.group();
            }
          }

          /*
           * If the selector is downloadURL, extract the url
           */
          if (sel.getName().contains("downloadURL")) {
            extractedText = extractDownloadUrl(extractedText);
          }

          return extractedText;
        }).collect(Collectors.toList());

        break;
      case SelectorLink:
        // Return hrefs of anchor elements or text when no href present
        extractedValues = extractedElements.stream().map(e -> {
          String href = e.attr("href");
          return StringUtils.isNotBlank(href) ? href : e.text();
        }).collect(Collectors.toList());
        break;

      default:
        // leave extractedValues as empty list
        break;

    }

    return extractedValues;
  }

  /**
   * Extract download url.
   *
   * @param extractedText the extracted text
   * @return the string
   */
  private static String extractDownloadUrl(String extractedText) {
    Matcher downloadMatcher = downloadUrlPattern.matcher(extractedText);
    if (downloadMatcher.find()) {
      String argument = null;
      if ((argument = downloadMatcher.group(1)) != null) {
        extractedText = argument.replaceAll("'", "");
      }
    }
    return extractedText;
  }

  /**
   * Fetch value by selector.
   *
   * @param document the document
   * @param sel      the sel
   * @return the string
   */
  private static String fetchValueBySelector(Document document, DatasetSelector sel) {

    String fetchValue = null;
    if (sel.getType().equals(WebScraperSelectorType.SelectorElementAttribute)) {
      fetchValue = document.select(sel.getSelector()).attr(sel.getExtractAttribute());
    } else {
      fetchValue = document.select(sel.getSelector()).text();
    }

    if (sel.getName().contains("downloadURL")) {

      Matcher downloadMatcher = Pattern.compile("\\w*\\(([^)]+)\\);*").matcher(fetchValue);
      if (downloadMatcher.find()) {
        String argument = null;
        if ((argument = downloadMatcher.group(1)) != null) {
          fetchValue = argument.replaceAll("'", "");
        }
      }
    }
    return fetchValue;
  }

  /**
   * Check if input is a valid JSON array.
   *
   * @param input the input string
   * @return true if valid JSON array, false otherwise
   */
  public static boolean checkIfJsonArray(String input) {
    try {
      JsonElement element = JsonParser.parseString(input);
      return element != null && element.isJsonArray();
    } catch (Exception e) {
      logger.debug("Input is not a valid JSON Array: {}", e.getMessage());
      return false;
    }
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

  /*
   * Return a List of SKOSConcept, each of them containing a prefLabel from input
   * String list.
   */

  /**
   * Extract concept list.
   *
   * @param <T>         the generic type
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
            propertyUri, "", Arrays.asList(new SkosPrefLabel("", FederationCore.getEnglishDcatTheme(label), nodeId)),
            nodeId)));
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
          | InvocationTargetException | NoSuchMethodException | SecurityException e) {
        e.printStackTrace();
      }
    }
    return result;
  }

}
