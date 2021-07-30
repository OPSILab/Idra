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

package it.eng.idra.dcat.dump;

import it.eng.idra.beans.dcat.DcatApFormat;
import it.eng.idra.beans.dcat.DcatApProfileNotValidException;
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
import it.eng.idra.beans.dcat.SpdxChecksum;
import it.eng.idra.beans.dcat.VCardOrganization;
import it.eng.idra.beans.odms.OdmsCatalogue;
import it.eng.idra.utils.CommonUtil;
import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.iri.IRIFactory;
import org.apache.jena.rdf.model.LiteralRequiredException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.ResourceRequiredException;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RiotException;
import org.apache.jena.shared.PropertyNotFoundException;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.SKOS;
import org.apache.jena.vocabulary.VCARD4;

// TODO: Auto-generated Javadoc
/**
 * The Class DcatApDeserializer.
 */
public class DcatApDeserializer implements IdcatApDeserialize {

  /** The Constant rdfDatasetPattern. */
  protected static final Pattern rdfDatasetPattern = Pattern
      .compile("\\w*<dcat:Dataset rdf:about=\\\"(.*)\\\"");

  /** The Constant turtleDatasetPattern. */
  protected static final Pattern turtleDatasetPattern = Pattern
      .compile("<(.*)>\\R\\s*a dcat:Dataset");

  /** The Constant THEME_BASE_URI. */
  protected static final String THEME_BASE_URI = "http://publications.europa.eu/resource/authority/data-theme/";

  /** The Constant GEO_BASE_URI. */
  protected static final String GEO_BASE_URI = "http://publications.europa.eu/mdr/authority/place";

  /** The Constant GEO_BASE_URI_ALT. */
  protected static final String GEO_BASE_URI_ALT = "http://www.geonames.org";

  /**
   * Instantiates a new dcat ap deserializer.
   */
  public DcatApDeserializer() {
  }

  /**
   * Instantiates a new dcat ap deserializer.
   *
   * @param modelText the model text
   * @param node      the node
   * @return the model
   * @throws RiotException the riot exception
   */
  public Model dumpToModel(String modelText, OdmsCatalogue node) throws RiotException {

    String nodeBaseUri = node.getHost();
    // create an empty model
    Model model = ModelFactory.createDefaultModel();
    for (DcatApFormat format : DcatApFormat.values()) {
      try {
        model.read(new ByteArrayInputStream(modelText.getBytes(StandardCharsets.UTF_8)),
            nodeBaseUri, format.formatName());
        node.setDcatFormat(format);
        break;
      } catch (RiotException e) {
        if (!e.getMessage().contains("Content is not allowed in prolog") && !e.getMessage()
            .contains("[line: 1, col: 1 ] " + "Expected BNode or IRI: Got: [DIRECTIVE:prefix]")) {
          throw e;
        } else {
          continue;
        }
      }
    }
    return model;
  }

  /**
   * Instantiates a new dcat ap deserializer.
   *
   * @param nodeId          the node id
   * @param datasetResource the dataset resource
   * @return the dcat dataset
   * @throws DcatApProfileNotValidException the dcat ap profile not valid
   *                                        exception
   */
  public DcatDataset resourceToDataset(String nodeId, Resource datasetResource)
      throws DcatApProfileNotValidException {
    // Properties to be mapped among different CKAN fallback fields

    String title = null;
    String description = null;
    String releaseDate = null;
    String updateDate = null;
    String version = null;
    List<SkosConceptTheme> theme = new ArrayList<SkosConceptTheme>();
    List<String> keywords = new ArrayList<String>();
    List<String> documentation = new ArrayList<String>();
    List<String> sample = new ArrayList<String>();
    List<String> versionNotes = new ArrayList<String>();
    List<String> relatedResource = new ArrayList<String>();

    List<DcatDistribution> distributionList = new ArrayList<DcatDistribution>();

    if (datasetResource.hasProperty(DCTerms.title)) {
      title = datasetResource.getRequiredProperty(DCTerms.title).getString();
    }
    if (datasetResource.hasProperty(DCTerms.description)) {
      description = datasetResource.getRequiredProperty(DCTerms.description).getString();
    }

    theme = deserializeConcept(nodeId, datasetResource, DCAT.theme, SkosConceptTheme.class);
    FoafAgent publisher = null;
    publisher = deserializeFoafAgent(nodeId, datasetResource.getProperty(DCTerms.publisher));
    List<VCardOrganization> contactPointList = null;
    contactPointList = deserializeContactPoint(nodeId, datasetResource);

    // Iterate over keyword properties
    StmtIterator kit = datasetResource.listProperties(DCAT.keyword);
    while (kit.hasNext()) {
      keywords.add(kit.next().getString());
    }
    List<DctStandard> conformsTo = null;
    conformsTo = deserializeDctStandard(nodeId, datasetResource);
    String accessRights = null;
    if (datasetResource.hasProperty(DCTerms.accessRights)) {
      accessRights = datasetResource.getProperty(DCTerms.accessRights).getString();
    }

    // Iterate over documentation properties
    StmtIterator dit = datasetResource.listProperties(FOAF.page);
    while (dit.hasNext()) {
      documentation.add(dit.next().getString());
    }

    // Iterate over related properties
    StmtIterator relIt = datasetResource.listProperties(DCTerms.relation);
    while (relIt.hasNext()) {
      relatedResource.add(relIt.next().getString());
    }
    String frequency = null;
    frequency = deserializeFrequency(datasetResource);

    // Iterate over hasVersion properties
    List<String> hasVersion = new ArrayList<String>();
    StmtIterator hasVIt = datasetResource.listProperties(DCTerms.hasVersion);
    while (hasVIt.hasNext()) {
      hasVersion.add(hasVIt.next().getString());
    }

    // Iterate over isVersionOf properties
    List<String> isVersionOf = new ArrayList<String>();
    StmtIterator isVIt = datasetResource.listProperties(DCTerms.isVersionOf);
    while (isVIt.hasNext()) {
      Statement isV = isVIt.next();
      try {
        isVersionOf.add(isV.getString());
      } catch (LiteralRequiredException e) {
        isVersionOf.add(isV.getResource().getURI());
      }
    }

    // Manage required landingPage property
    String landingPage = null;
    if (datasetResource.hasProperty(DCAT.landingPage)) {
      Resource landingR = datasetResource.getPropertyResourceValue(DCAT.landingPage);
      if (landingR != null && StringUtils.isNotBlank(landingPage = landingR.getURI())) {
        System.out.println(landingR.getURI());
      } else {
        landingPage = datasetResource.getURI();
      }
    } else {
      landingPage = datasetResource.getURI();
    }
    List<String> language = null;
    language = deserializeLanguage(datasetResource);

    // Iterate over provenance properties
    List<String> provenance = new ArrayList<String>();
    StmtIterator provIt = datasetResource.listProperties(DCTerms.provenance);
    while (provIt.hasNext()) {
      provenance.add(provIt.next().getString());
    }

    if (datasetResource.hasProperty(DCTerms.issued)) {
      releaseDate = extractDate(datasetResource.getProperty(DCTerms.issued));
    }

    if (datasetResource.hasProperty(DCTerms.modified)) {
      updateDate = extractDate(datasetResource.getProperty(DCTerms.modified));
    }

    String identifier = null;
    if (datasetResource.hasProperty(DCTerms.identifier)) {
      identifier = datasetResource.getProperty(DCTerms.identifier).getString();
    } else {
      identifier = landingPage;
    }

    // Iterate over otherIdentifier properties
    List<String> otherIdentifier = null;
    otherIdentifier = deserializeOtherIdentifier(datasetResource);

    // Iterate over sample properties
    StmtIterator sampleIt = datasetResource
        .listProperties(ResourceFactory.createProperty("http://www.w3.org/ns/adms#sample"));
    while (sampleIt.hasNext()) {
      sample.add(sampleIt.next().getString());
    }

    // Iterate over source properties
    List<String> source = new ArrayList<String>();
    StmtIterator sourceIt = datasetResource.listProperties(DCTerms.source);
    while (sourceIt.hasNext()) {
      Statement sourceStm = sourceIt.next();
      try {
        source.add(sourceStm.getString());
      } catch (LiteralRequiredException e) {
        source.add(sourceStm.getResource().getURI());
      }
    }

    // Handle spatial property
    DctLocation spatialCoverage = null;
    spatialCoverage = deserializeSpatialCoverage(nodeId, datasetResource);

    // Handle temporal property
    DctPeriodOfTime temporalCoverage = null;
    temporalCoverage = deserializeTemporalCoverage(nodeId, datasetResource);
    String type = null;
    if (datasetResource.hasProperty(DCTerms.type)) {
      type = datasetResource.getProperty(DCTerms.type).getString();
    }

    if (datasetResource.hasProperty(OWL.versionInfo)) {
      version = datasetResource.getProperty(OWL.versionInfo).getString();
    }

    // Iterate over versionNotes properties
    StmtIterator vnotesIt = datasetResource
        .listProperties(ResourceFactory.createProperty("http://www.w3.org/ns/adms#versionNotes"));
    while (vnotesIt.hasNext()) {
      versionNotes.add(vnotesIt.next().getString());
    }

    // Handle distributions
    StmtIterator distrIt = datasetResource.listProperties(DCAT.distribution);
    while (distrIt.hasNext()) {
      distributionList.add(resourceToDcatDistribution(distrIt.next().getResource(), nodeId));
    }
    DcatDataset mapped;
    mapped = new DcatDataset(nodeId, identifier, title, description, distributionList, theme,
        publisher, contactPointList, keywords, accessRights, conformsTo, documentation, frequency,
        hasVersion, isVersionOf, landingPage, language, provenance, releaseDate, updateDate,
        otherIdentifier, sample, source, spatialCoverage, temporalCoverage, type, version,
        versionNotes, null, null, new ArrayList<SkosConceptSubject>(), relatedResource);

    distributionList = null;
    contactPointList = null;
    publisher = null;
    conformsTo = null;
    spatialCoverage = null;
    temporalCoverage = null;
    keywords = null;
    theme = null;
    documentation = null;
    relatedResource = null;
    hasVersion = null;
    isVersionOf = null;
    language = null;
    provenance = null;
    otherIdentifier = null;
    sample = null;
    source = null;
    versionNotes = null;

    return mapped;

  }

  /**
   * Extract date.
   *
   * @param dateStatement the date statement
   * @return the string
   */
  protected String extractDate(Statement dateStatement) {
    try {
      return CommonUtil.fromLocalToUtcDate(CommonUtil.fixBadUtcDate(dateStatement.getString()),
          null);
    } catch (IllegalArgumentException ignore) {
      return null;
    }

  }

  /**
   * Deserialize concept.
   *
   * @param                <T> the generic type
   * @param nodeId         the node ID
   * @param parentResource the parent resource
   * @param toExtractP     the to extract P
   * @param type           the type
   * @return the list
   */

  public <T extends SkosConcept> List<T> deserializeConcept(String nodeId, Resource parentResource,
      Property toExtractP, Class<T> type) {
    List<T> conceptList = new ArrayList<T>();

    Resource conceptR = null;

    // Iterate over concept properties
    StmtIterator conceptIt = parentResource.listProperties(toExtractP);
    while (conceptIt.hasNext()) {

      List<SkosPrefLabel> labelList = null;
      String conceptUri = null;

      conceptR = conceptIt.next().getResource();
      if (conceptR != null && StringUtils.isNotBlank(conceptUri = conceptR.getURI())) {

        if (conceptR.hasProperty(SKOS.prefLabel)) {

          labelList = new ArrayList<SkosPrefLabel>();
          StmtIterator labelIt = conceptR.listProperties(SKOS.prefLabel);
          while (labelIt.hasNext()) {
            Statement labelS = labelIt.next();
            labelList.add(new SkosPrefLabel(labelS.getLanguage(), labelS.getString(), nodeId));
          }

          // For theme, the label is the Final label. e.g.
          // http://publications.europa.eu/resource/authority/data-theme/GOVE
        } else if (toExtractP.getURI().equals(DCAT.theme.getURI())) {
          String extractedLabel = extractThemeFromUri(conceptUri);
          labelList = new ArrayList<SkosPrefLabel>();
          labelList.add(new SkosPrefLabel("ENG", extractedLabel, nodeId));

          // For subject, the label is the entire URI. e.g. http://eurovoc.europa.eu/106
        } else if (toExtractP.getURI().equals(DCTerms.subject.getURI())) {
          String extractedLabel = conceptUri;
          labelList = new ArrayList<SkosPrefLabel>();
          labelList.add(new SkosPrefLabel("ENG", extractedLabel, nodeId));
        }

        try {
          conceptList.add(type.getDeclaredConstructor(SkosConcept.class)
              .newInstance(new SkosConcept(toExtractP.getURI(), conceptUri, labelList, nodeId)));
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
            | InvocationTargetException | NoSuchMethodException | SecurityException e) {
          e.printStackTrace();
        }
      }
    }
    return conceptList;
  }

  /**
   * Deserialize language.
   *
   * @param datasetResource the dataset resource
   * @return the list
   */
  public List<String> deserializeLanguage(Resource datasetResource) {

    List<String> language = new ArrayList<String>();

    // Iterate over language properties
    StmtIterator it = datasetResource.listProperties(DCTerms.language);
    while (it.hasNext()) {
      Resource languageR = it.next().getResource();
      String languageUri = null;
      if (languageR != null && StringUtils.isNotBlank(languageUri = languageR.getURI())) {
        if (!IRIFactory.iriImplementation().create(languageUri).hasViolation(false)) {
          language.add(extractLanguageFromUri(languageUri));
        } else {
          language.add(languageUri);
        }
      }
    }
    return language;
  }

  /**
   * Deserialize temporal coverage.
   *
   * @param nodeId          the node ID
   * @param datasetResource the dataset resource
   * @return the dct period of time
   */
  public DctPeriodOfTime deserializeTemporalCoverage(String nodeId, Resource datasetResource) {

    String startDate = null;
    String endDate = null;
    Resource temporalResource = datasetResource.getPropertyResourceValue(DCTerms.temporal);

    if (temporalResource != null) {

      if (temporalResource
          .hasProperty(ResourceFactory.createProperty("http://schema.org#startDate"))) {
        startDate = temporalResource
            .getProperty(ResourceFactory.createProperty("http://schema.org#startDate")).getString();
      }

      if (temporalResource
          .hasProperty(ResourceFactory.createProperty("http://schema.org#endDate"))) {
        endDate = temporalResource
            .getProperty(ResourceFactory.createProperty("http://schema.org#endDate")).getString();
      }

      return new DctPeriodOfTime(DCTerms.temporal.getURI(), startDate, endDate, nodeId);

    }

    return null;
  }

  /**
   * Deserialize spatial coverage.
   *
   * @param nodeId          the node ID
   * @param datasetResource the dataset resource
   * @return the dct location
   */
  public DctLocation deserializeSpatialCoverage(String nodeId, Resource datasetResource) {

    // StmtIterator spatialIt = datasetResource.listProperties(DCTerms.spatial);
    // while (spatialIt.hasNext()) {
    // Resource spatialResource = (Resource) spatialIt.next().getResource();

    String geographicalIdentifier = null;
    String geographicalName = null;
    String geometry = null;
    String spatialResourceUri = null;
    Resource spatialResource = datasetResource.getPropertyResourceValue(DCTerms.spatial);

    if (spatialResource != null) {

      if (spatialResource.hasProperty(ResourceFactory
          .createProperty("http://dati.gov.it/onto/dcatapit#geographicalIdentifier"))) {
        geographicalIdentifier = spatialResource
            .getProperty(ResourceFactory
                .createProperty("http://dati.gov.it/onto/dcatapit#geographicalIdentifier"))
            .getString();
      }
      if (spatialResource
          .hasProperty(ResourceFactory.createProperty("http://www.w3.org/ns/locn#geometry"))) {
        geometry = spatialResource
            .getProperty(ResourceFactory.createProperty("http://www.w3.org/ns/locn#geometry"))
            .getString();
      }

      if (spatialResource.hasProperty(
          ResourceFactory.createProperty("http://www.w3.org/ns/locn#geographicalName"))) {
        geographicalName = spatialResource
            .getPropertyResourceValue(
                ResourceFactory.createProperty("http://www.w3.org/ns/locn#geographicalName"))
            .getURI();
      }

      // Handle geographical Identifier as resource URI
      if (StringUtils.isBlank(geographicalIdentifier)
          && StringUtils.isNotBlank(spatialResourceUri = spatialResource.getURI())) {
        geographicalIdentifier = (spatialResourceUri.startsWith(GEO_BASE_URI)
            || spatialResourceUri.startsWith(GEO_BASE_URI_ALT)) ? spatialResourceUri : "";
      }

      return new DctLocation(DCTerms.spatial.getURI(), geographicalIdentifier, geographicalName,
          geometry, nodeId);
    }

    return null;
  }

  /**
   * Deserialize other identifier.
   *
   * @param datasetResource the dataset resource
   * @return the list
   */
  public List<String> deserializeOtherIdentifier(Resource datasetResource) {

    List<String> otherIdentifier = new ArrayList<String>();
    StmtIterator othIdIt = datasetResource
        .listProperties(ResourceFactory.createProperty("http://www.w3.org/ns/adms#identifier"));
    while (othIdIt.hasNext()) {
      Statement st = othIdIt.next();

      try {
        if (st.getString() != null) {
          otherIdentifier.add(st.getString());
        }

      } catch (LiteralRequiredException e) {
        Resource othIdResource = st.getResource();
        if (othIdResource != null) {
          if (othIdResource.hasProperty(SKOS.notation)) {
            otherIdentifier.add(othIdResource.getProperty(SKOS.notation).getString());
          }
        }
      }

    }
    return otherIdentifier;
  }

  /**
   * Deserialize DCT standard.
   *
   * @param nodeId          the node ID
   * @param datasetResource the dataset resource
   * @return the list
   */
  public List<DctStandard> deserializeDctStandard(String nodeId, Resource datasetResource) {

    List<DctStandard> standardList = new ArrayList<DctStandard>();

    // Iterate over conformsTo/linked Schemas properties
    StmtIterator cit = datasetResource.listProperties(DCTerms.conformsTo);
    Property referenceProperty = ResourceFactory
        .createProperty(DcatApSerializer.DCATAP_IT_BASE_URI + "referenceDocumentation");

    while (cit.hasNext()) {

      String uri = null;
      String identifier = null;
      String toTitle = null;
      String toDescription = null;
      List<String> toReference = new ArrayList<String>();

      Resource standardResource = cit.next().getResource();
      uri = standardResource.getURI();
      if (standardResource.hasProperty(DCTerms.identifier)) {
        identifier = standardResource.getProperty(DCTerms.identifier).getString();
      }
      if (standardResource.hasProperty(DCTerms.title)) {
        toTitle = standardResource.getProperty(DCTerms.title).getString();
      }
      if (standardResource.hasProperty(DCTerms.description)) {
        toDescription = standardResource.getProperty(DCTerms.description).getString();
      }

      StmtIterator referenceIt = standardResource.listProperties(referenceProperty);
      while (referenceIt.hasNext()) {

        toReference.add(referenceIt.next().getString());
      }

      standardList
          .add(new DctStandard(uri, identifier, toTitle, toDescription, toReference, nodeId));
    }
    return standardList;
  }

  /**
   * Deserialize contact point.
   *
   * @param nodeId          the node ID
   * @param datasetResource the dataset resource
   * @return the list
   */
  public List<VCardOrganization> deserializeContactPoint(String nodeId, Resource datasetResource) {

    List<VCardOrganization> contactPointList = new ArrayList<VCardOrganization>();

    // Iterate over contact points
    StmtIterator cit = datasetResource.listProperties(DCAT.contactPoint);
    while (cit.hasNext()) {

      String vcardUri = null;
      String vcardFn = null;
      String vcardHasEmail = null;
      String vcardHasUrl = null;
      String vcardHasTelephoneValue = null;
      String vcardHasTelephoneType = null;

      Resource contactResource = cit.next().getResource();
      if (contactResource != null) {

        vcardUri = contactResource.getURI();
        if (contactResource.hasProperty(VCARD4.fn)) {
          vcardFn = contactResource.getProperty(VCARD4.fn).getString();
        }
        try {
          if (contactResource.hasProperty(VCARD4.hasEmail)) {
            vcardHasEmail = contactResource.getProperty(VCARD4.hasEmail).getResource().getURI();
          }

        } catch (ResourceRequiredException e) {
          vcardHasEmail = contactResource.getProperty(VCARD4.hasEmail).getString();
        }

        if (contactResource.hasProperty(VCARD4.hasURL)) {
          try {
            vcardHasUrl = contactResource.getProperty(VCARD4.hasURL).getString();
          } catch (LiteralRequiredException e) {
            vcardHasUrl = contactResource.getProperty(VCARD4.hasURL).getResource().getURI();
          }
        }

        if (contactResource.hasProperty(VCARD4.hasTelephone)) {
          try {
            vcardHasTelephoneValue = contactResource.getProperty(VCARD4.hasTelephone).getString();
          } catch (LiteralRequiredException e) {
            Resource hasTelephoneR = contactResource.getProperty(VCARD4.hasTelephone).getResource();
            if (hasTelephoneR != null) {
              if (hasTelephoneR.hasProperty(VCARD4.value)) {
                vcardHasTelephoneValue = hasTelephoneR.getProperty(VCARD4.value).getString();
              }
              if (hasTelephoneR.hasProperty(RDF.type)) {
                vcardHasTelephoneType = hasTelephoneR.getPropertyResourceValue(RDF.type).getURI();
              }
            }
          }
        }

        contactPointList.add(new VCardOrganization(DCAT.contactPoint.getURI(), vcardUri, vcardFn,
            vcardHasEmail, vcardHasUrl, vcardHasTelephoneValue, vcardHasTelephoneType, nodeId));
      }
    }
    return contactPointList;
  }

  /**
   * Deserialize FOAF agent.
   *
   * @param nodeId         the node ID
   * @param agentStatement the agent statement
   * @return the foaf agent
   */
  public FoafAgent deserializeFoafAgent(String nodeId, Statement agentStatement) {

    String agentIdentifier = null;
    String agentUri = null;
    String agentName = null;
    String agentMbox = null;
    String agentHomepage = null;
    String agentType = null;
    Resource agentResource = null;

    if (agentStatement != null && (agentResource = agentStatement.getResource()) != null) {

      agentUri = agentResource.getURI();
      if (agentResource.hasProperty(FOAF.name)) {
        agentName = agentResource.getProperty(FOAF.name).getString();
      }
      if (agentResource.hasProperty(FOAF.mbox)) {
        agentMbox = agentResource.getProperty(FOAF.mbox).getString();
      }

      if (agentResource.hasProperty(FOAF.homepage)) {
        Resource homepageR = agentResource.getPropertyResourceValue(FOAF.homepage);
        if (homepageR != null) {
          agentHomepage = homepageR.getURI();
        } else {
          agentHomepage = agentResource.getProperty(FOAF.homepage).getString();
        }
      }
      if (agentResource.hasProperty(DCTerms.type)) {
        agentType = agentResource.getProperty(DCTerms.type).getString();
      }
      if (agentResource.hasProperty(DCTerms.identifier)) {
        agentIdentifier = agentResource.getProperty(DCTerms.identifier).getString();
      }

      return new FoafAgent(agentStatement.getPredicate().getURI(), agentUri, agentName, agentMbox,
          agentHomepage, agentType, agentIdentifier, nodeId);

    }
    return null;
  }

  /**
   * Deserialize frequency.
   *
   * @param datasetResource the dataset resource
   * @return the string
   */
  public String deserializeFrequency(Resource datasetResource) {
    String frequencyUri = null;
    if (datasetResource.hasProperty(DCTerms.accrualPeriodicity)) {
      Resource frequencyR = datasetResource.getPropertyResourceValue(DCTerms.accrualPeriodicity);

      if (frequencyR != null && StringUtils.isNotBlank(frequencyUri = frequencyR.getURI())) {
        if (!IRIFactory.iriImplementation().create(frequencyUri).hasViolation(false)) {
          return CommonUtil.extractFrequencyFromUri(frequencyUri);
        } else {
          return frequencyUri;
        }
      }
    }
    return null;
  }

  /**
   * DcatDistribution.
   *
   * @param r      the r
   * @param nodeId the node id
   * @return the dcat distribution
   */
  public DcatDistribution resourceToDcatDistribution(Resource r, String nodeId) {

    String accessUrl = null;
    String description = null;
    String format = null;
    String documentation = null;
    String downloadUrl = null;
    String releaseDate = null;
    String updateDate = null;
    SpdxChecksum checksum = null;
    String licenseUri = null;
    String licenseName = null;
    String licenseVersion = null;
    String licenseType = null;
    SkosConceptStatus status = null;

    // Manage required accessURL property
    if (r.hasProperty(DCAT.accessURL)) {
      Resource accessR = r.getPropertyResourceValue(DCAT.accessURL);
      if (accessR != null && StringUtils.isNotBlank(accessUrl = accessR.getURI())) {
        System.out.println(accessR.getURI());
      } else {
        throw new PropertyNotFoundException(DCAT.accessURL);
      }
    }

    if (r.hasProperty(DCTerms.description)) {
      description = r.getProperty(DCTerms.description).getString();
    }

    if (r.hasProperty(DCTerms.format)) {
      format = deserializeFormat(r);
    }

    DctLicenseDocument license = null;
    if (r.hasProperty(DCTerms.license)) {
      Resource licenseR = r.getPropertyResourceValue(DCTerms.license);

      licenseUri = licenseR.getURI();
      if (licenseR.hasProperty(FOAF.name)) {
        licenseName = licenseR.getProperty(FOAF.name).getString();
      }
      if (licenseR.hasProperty(DCTerms.type)) {
        licenseType = licenseR.getPropertyResourceValue(DCTerms.type).getURI();
      }
      if (licenseR.hasProperty(OWL.versionInfo)) {
        licenseVersion = licenseR.getProperty(OWL.versionInfo).getString();
      }
      license = new DctLicenseDocument(licenseUri, licenseName, licenseType, licenseVersion,
          nodeId);
    }
    String byteSize = null;
    if (r.hasProperty(DCAT.byteSize)) {
      byteSize = r.getProperty(DCAT.byteSize).getString();
    }

    if (r.hasProperty(ResourceFactory.createProperty("http://spdx.org/rdf/terms#checksum"))) {

      checksum = deserializeChecksum(nodeId, r);
    }

    if (r.hasProperty(FOAF.page)) {
      documentation = r.getProperty(FOAF.page).getString();
    }
    // Manage downloadURL property
    if (r.hasProperty(DCAT.downloadURL)) {
      Resource downloadR = r.getPropertyResourceValue(DCAT.downloadURL);
      if (downloadR != null) {
        downloadUrl = downloadR.getURI();
      }
    }
    String language = null;
    if (r.hasProperty(DCTerms.language)) {
      try {
        language = r.getPropertyResourceValue(DCTerms.language).getURI();
      } catch (Exception ignore) {
        System.out.println(ignore.getLocalizedMessage());
      }
    }
    List<DctStandard> linkedSchemas = null;
    linkedSchemas = deserializeDctStandard(nodeId, r);
    String mediaType = null;
    if (r.hasProperty(DCAT.mediaType)) {
      mediaType = r.getProperty(DCAT.mediaType).getString();
    }

    if (r.hasProperty(DCTerms.issued)) {
      releaseDate = extractDate(r.getProperty(DCTerms.issued));
    }
    if (r.hasProperty(DCTerms.modified)) {
      updateDate = extractDate(r.getProperty(DCTerms.modified));
    }

    String rights = null;
    if (r.hasProperty(DCTerms.rights)) {
      rights = r.getProperty(DCTerms.rights).getString();
    }

    try {
      status = deserializeConcept(nodeId, r,
          ResourceFactory.createProperty("http://www.w3.org/ns/adms#status"),
          SkosConceptStatus.class).get(0);
    } catch (IndexOutOfBoundsException ignore) {
      System.out.println(ignore.getLocalizedMessage());
    }
    String title = null;
    if (r.hasProperty(DCTerms.title)) {
      title = r.getProperty(DCTerms.title).getString();
    }

    if (StringUtils.isBlank(downloadUrl)) {
      downloadUrl = accessUrl;
    }

    return new DcatDistribution(nodeId, accessUrl, description, format, license, byteSize, checksum,
        Arrays.asList(documentation), downloadUrl, Arrays.asList(language), linkedSchemas,
        mediaType, releaseDate, updateDate, rights, status, title);

  }

  /**
   * Deserialize checksum.
   *
   * @param nodeId the node ID
   * @param r      the r
   * @return the spdx checksum
   */
  public SpdxChecksum deserializeChecksum(String nodeId, Resource r) {
    String checksumValue = null;
    String checksumAlgorithm = null;

    Resource checksumR = r.getPropertyResourceValue(
        ResourceFactory.createProperty("http://spdx.org/rdf/terms#checksum"));
    if (checksumR
        .hasProperty(ResourceFactory.createProperty("http://spdx.org/rdf/terms#algorithm"))) {
      checksumAlgorithm = checksumR
          .getProperty(ResourceFactory.createProperty("http://spdx.org/rdf/terms#algorithm"))
          .getString();
    }
    if (checksumR
        .hasProperty(ResourceFactory.createProperty("http://spdx.org/rdf/terms#checksumValue"))) {
      checksumValue = checksumR
          .getProperty(ResourceFactory.createProperty("http://spdx.org/rdf/terms#checksumValue"))
          .getString();
    }

    return new SpdxChecksum("http://spdx.org/rdf/terms#checksum", checksumAlgorithm, checksumValue,
        nodeId);

  }

  /**
   * Deserialize format.
   *
   * @param r the r
   * @return the string
   */
  public String deserializeFormat(Resource r) {

    Resource formatR = r.getPropertyResourceValue(DCTerms.format);
    String formatUri = null;
    String format = null;
    if (formatR != null && StringUtils.isNotBlank(formatUri = formatR.getURI())) {
      if (!IRIFactory.iriImplementation().create(formatUri).hasViolation(false)) {
        format = extractFormatFromUri(formatUri);
      } else {
        format = formatUri;
      }

    }
    return format;
  }

  /**
   * extractFormatFromURI.
   *
   * @param uri the uri
   * @return the string
   */
  public String extractFormatFromUri(String uri) {

    Matcher matcher = Pattern
        .compile(
            "http:\\/\\/publications\\.europa\\.eu\\/resource\\/authority\\/file-type(\\/|#)(\\w*)")
        .matcher(uri);
    String result = null;

    return (matcher.find() && (result = matcher.group(2)) != null) ? result : "";

  }

  /**
   * extractThemeFromURI.
   *
   * @param uri the uri
   * @return the string
   */
  public String extractThemeFromUri(String uri) {

    Matcher matcher = Pattern.compile(
        "http:\\/\\/publications\\.europa\\.eu\\/resource\\/authority\\/data-theme(\\/|#)(\\w*)")
        .matcher(uri);
    String result = null;

    return (matcher.find() && (result = matcher.group(2)) != null) ? result : "";

  }

  /**
   * Extract subject from URI.
   *
   * @param uri the uri
   * @return the string
   */
  public String extractSubjectFromUri(String uri) {

    Matcher matcher = Pattern.compile("http:\\/\\/eurovoc\\.europa\\.eu(\\/|#)(\\w*)").matcher(uri);
    String result = null;

    return (matcher.find() && (result = matcher.group(2)) != null) ? result : "";

  }

  /**
   * extractLanguageFromURI.
   *
   * @param uri the uri
   * @return the string
   */
  public String extractLanguageFromUri(String uri) {

    Matcher matcher = Pattern.compile("http:\\/\\/publications\\.europa\\.eu\\"
        + "/(mdr|resource)\\/authority\\/language(\\/|#)(\\w*)").matcher(uri);
    String result = null;

    return (matcher.find() && (result = matcher.group(3)) != null) ? result : "";

  }

  /**
   * getDatasetPattern.
   *
   * @param format the format
   * @return the dataset pattern
   */
  public Pattern getDatasetPattern(DcatApFormat format) {

    switch (format) {

      case TURTLE:
        return turtleDatasetPattern;
      default:
        return rdfDatasetPattern;
    }
  }

}
