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

package it.eng.idra.dcat.dump;

import it.eng.idra.beans.IdraProperty;
import it.eng.idra.beans.dcat.DCATAP;
import it.eng.idra.beans.dcat.DcatApFormat;
import it.eng.idra.beans.dcat.DcatApProfile;
import it.eng.idra.beans.dcat.DcatApWriteType;
import it.eng.idra.beans.dcat.DcatDataService;
import it.eng.idra.beans.dcat.DcatDataset;
import it.eng.idra.beans.dcat.DcatDistribution;
import it.eng.idra.beans.dcat.DcatProperty;
import it.eng.idra.beans.dcat.DctFrequency;
import it.eng.idra.beans.dcat.DctLicenseDocument;
import it.eng.idra.beans.dcat.DctLocation;
import it.eng.idra.beans.dcat.DctPeriodOfTime;
import it.eng.idra.beans.dcat.DctStandard;
import it.eng.idra.beans.dcat.ELI;
import it.eng.idra.beans.dcat.FoafAgent;
import it.eng.idra.beans.dcat.Relationship;
import it.eng.idra.beans.dcat.SkosConcept;
import it.eng.idra.beans.dcat.SkosPrefLabel;
import it.eng.idra.beans.dcat.SpdxChecksum;
import it.eng.idra.beans.dcat.VcardOrganization;
import it.eng.idra.beans.odms.OdmsCatalogue;
import it.eng.idra.beans.search.SearchResult;
import it.eng.idra.management.FederationCore;
import it.eng.idra.utils.PropertyManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDhexBinary;
import org.apache.jena.datatypes.xsd.impl.XSDDateType;
import org.apache.jena.iri.IRI;
import org.apache.jena.iri.IRIException;
import org.apache.jena.iri.IRIFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceRequiredException;
//import org.apache.jena.shared.BadURIException;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;
import org.apache.jena.vocabulary.VCARD4;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.apache.jena.rdf.model.ResourceFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class DcatApSerializer.
 */
public class DcatApSerializer {

  /** The node resources. */
  static Map<Integer, OdmsCatalogue> nodeResources = null;

  /** The Constant iriFactory. */
  @SuppressWarnings("deprecation")
  protected static final IRIFactory iriFactory = IRIFactory.jenaImplementation();

  /** The Constant DCATAP_IT_BASE_URI. */
  public static final String DCATAP_IT_BASE_URI = "http://dati.gov.it/onto/dcatapit#";

  /** The Constant THEME_BASE_URI. */
  public static final String THEME_BASE_URI = "http://publications.europa.eu/resource/authority/data-theme/";

  /** The Constant SUBJECT_BASE_URI. */
  public static final String SUBJECT_BASE_URI = "http://eurovoc.europa.eu/";

  /** The Constant FREQUENCY_BASE_URI. */
  public static final String FREQUENCY_BASE_URI = "http://publications.europa.eu/resource/authority/frequency/";

  /** The Constant LANGUAGE_BASE_URI. */
  public static final String LANGUAGE_BASE_URI = "http://publications.europa.eu/mdr/authority/language/";

  /** The Constant GEO_BASE_URI. */
  public static final String GEO_BASE_URI = "http://publications.europa.eu/mdr/authority/place/";

  /** The Constant GEO_BASE_URI_ALT. */
  public static final String GEO_BASE_URI_ALT = "http://geonames.org/";

  /** The Constant FORMAT_BASE_URI. */
  public static final String FORMAT_BASE_URI = "http://publications.europa.eu/mdr/authority/file-type/";

  /** The Constant FORMAT_BASE_URI. */
  public static final String MEDIATYPE_BASE_URI = "https://www.iana.org/assignments/media-types/";

  /** The Constant LICENSE_TYPE_BASE_URI. */
  public static final String LICENSE_TYPE_BASE_URI = "http://purl.org/adms/licencetype/";

  /** The logger. */
  protected static Logger logger = LogManager.getLogger(DcatApSerializer.class);

  /** The file path. */
  private static String filePath = PropertyManager.getProperty(IdraProperty.DUMP_FILE_PATH);

  /** The file name. */
  private static String fileName = PropertyManager.getProperty(IdraProperty.DUMP_FILE_NAME);

  static {

  }

  /**
   * Instantiates a new dcat ap serializer.
   */
  public DcatApSerializer() {
  }

  /**
   * Dataset to model.
   *
   * @param dataset the dataset
   * @param profile the profile
   * @return the model
   */
  private static Model datasetToModel(DcatDataset dataset, DcatApProfile profile) {

    Model model = initializeModel();

    try {
      switch (profile) {

        case DCATAP_IT:
          model.setNsPrefix("dcatapit", "http://dati.gov.it/onto/dcatapit#");
          model = DcatApItSerializer.addDatasetToModel(dataset, model);
          break;

        /*
         * ADD HERE MORE CASES FOR FURTHER PROFILES
         */

        default:
          model = addDatasetToModel(dataset, model);
          break;
      }

    } catch (Exception e) {
      logger.error("Error while converting dataset " + dataset.getId() + " to model, reason: "
          + e.getMessage());
    }

    return model;
  }

  /**
   * initializeModel.
   *
   * @return the model
   */
  private static Model initializeModel() {
    Model model = ModelFactory.createDefaultModel();
    model.setNsPrefix("dct", DCTerms.getURI());
    model.setNsPrefix("dcat", DCAT.getURI());
    model.setNsPrefix("adms", "http://www.w3.org/ns/adms#");
    model.setNsPrefix("foaf", FOAF.getURI());
    model.setNsPrefix("owl", OWL.getURI());
    model.setNsPrefix("rdfs", RDFS.getURI());
    model.setNsPrefix("schema", "http://schema.org#");
    model.setNsPrefix("skos", SKOS.getURI());
    model.setNsPrefix("spdx", "http://spdx.org/rdf/terms#");
    model.setNsPrefix("xsd", XMLSchema.NAMESPACE);
    model.setNsPrefix("vcard", VCARD4.getURI());
    model.setNsPrefix("locn", "http://www.w3.org/ns/locn#");
    model.setNsPrefix("dcatap", "http://data.europa.eu/r5r/");
    model.setNsPrefix("rdf", RDF.getURI());
    model.setNsPrefix("eli", "http://data.europa.eu/eli/ontology#");// ns1
    return model;
  }

  /**
   * Datasets to model.
   *
   * @param datasets the datasets
   * @param profile  the profile
   * @return the model
   */
  private static Model datasetsToModel(List<DcatDataset> datasets, DcatApProfile profile) {

    Model model = initializeModel();

    for (DcatDataset d : datasets) {
      try {
        switch (profile) {

          case DCATAP_IT:
            model.setNsPrefix("dcatapit", "http://dati.gov.it/onto/dcatapit#");
            model = DcatApItSerializer.addDatasetToModel(d, model);
            break;

          /*
           * ADD HERE MORE CASES FOR FURTHER PROFILES
           */

          default:
            model = addDatasetToModel(d, model);
            break;
        }

      } catch (Exception e) {
        logger.error(
            "Error while converting dataset " + d.getId() + " to model, reason: " + e.getMessage());
      }
    }

    return model;

  }

  /**
   * Adds the dataset to model.
   *
   * @param dataset the dataset
   * @param model   the model
   * @return the model
   * @throws IRIException the IRI exception
   */
  protected static Model addDatasetToModel(DcatDataset dataset, Model model) throws IRIException {

    String landingPage = dataset.getLandingPage().getValue();
    IRI iri = iriFactory.create(landingPage);
    if (iri.hasViolation(false)) {
      throw new IllegalArgumentException(
          "URI for dataset: " + iri + "is not valid, skipping the dataset in the Jena Model:"
              + (iri.violations(false).next()).getShortMessage());
    }

    Resource datasetResource = model.createResource(iri.toString(), DCAT.Dataset);

    addDcatPropertyAsLiteral(dataset.getTitle(), datasetResource, model);

    addDcatPropertyAsLiteral(dataset.getDescription(), datasetResource, model);

    serializeConcept(dataset.getTheme(), model, datasetResource);

    serializeContactPoint(dataset.getContactPoint(), model, datasetResource);

    dataset.getKeywords().stream().filter(keyword -> StringUtils.isNotBlank(keyword))
        .forEach(keyword -> datasetResource.addLiteral(DCAT.keyword, keyword));

    // addDcatPropertyAsLiteral(dataset.getAccessRights(), datasetResource, model);

    if (isValidUri(dataset.getAccessRights().getValue())) {
      addDcatPropertyAsResource(dataset.getAccessRights(), datasetResource, model, true);
    } else {
      addDcatPropertyAsLiteral(dataset.getAccessRights(), datasetResource, model);
    }

    serializeDctStandard(dataset.getConformsTo(), datasetResource, model);

    List<DcatProperty> documentationList = dataset.getDocumentation();
    if (documentationList != null) {
      documentationList.stream().filter(item -> !isValidUri(item.getValue()))
          .forEach(item -> addDcatPropertyAsResource(item, datasetResource, model, true));
    }

    List<DcatProperty> relatedResourceList = dataset.getRelatedResource();
    if (relatedResourceList != null) {
      relatedResourceList.stream().filter(item -> !isValidUri(item.getValue()))
          .forEach(item -> addDcatPropertyAsResource(item, datasetResource, model, true));
    }

    serializeFrequency(dataset.getFrequency(), model, datasetResource);

    List<DcatProperty> hasVersion = dataset.getHasVersion();
    if (hasVersion != null) {
      hasVersion.stream().filter(item -> !isValidUri(item.getValue()))
          .forEach(item -> addDcatPropertyAsResource(item, datasetResource, model, true));
    }

    List<DcatProperty> isVersionOf = dataset.getIsVersionOf();
    if (isVersionOf != null) {
      isVersionOf.stream().filter(item -> !isValidUri(item.getValue()))
          .forEach(item -> addDcatPropertyAsResource(item, datasetResource, model, true));
    }

    addDcatPropertyAsResource(dataset.getLandingPage(), datasetResource, model, false);

    // datasetResource.addProperty(model.createProperty(dataset.getLandingPage().getUri()),
    // model.createResource(dataset.getLandingPage().getRange())
    // .addProperty(dataset.getLandingPage().getProperty(),
    // dataset.getLandingPage().getRange()));

    // Property p = model.createProperty(dataset.getLandingPage().getUri());
    // dataset.getLandingPage().getProperty().addProperty(p,
    // model.createResource(dataset.getLandingPage().getValue(),
    // dataset.getLandingPage().getRange()));

    serializeLanguage(dataset.getLanguage(), model, datasetResource);

    List<DcatProperty> provenance = dataset.getProvenance();
    if (provenance != null) {
      provenance.stream().forEach(item -> addDcatPropertyAsLiteral(item, datasetResource, model));
    }

    addDcatPropertyAsTypedLiteral(dataset.getReleaseDate(), XSDDateType.XSDdateTime,
        datasetResource, model);
    addDcatPropertyAsTypedLiteral(dataset.getUpdateDate(), XSDDateType.XSDdateTime, datasetResource,
        model);

    addDcatPropertyAsLiteral(dataset.getIdentifier(), datasetResource, model);

    List<DcatProperty> otherIdentifier = dataset.getOtherIdentifier();
    if (otherIdentifier != null) {
      otherIdentifier.stream().filter(id -> StringUtils.isNotBlank(id.getValue())).forEach(id -> {
        datasetResource.addProperty(model.createProperty(id.getProperty().getURI()),
            model.createResource().addLiteral(SKOS.notation, id.getValue()));
      });
    }

    List<DcatProperty> sample = dataset.getSample();
    if (sample != null) {
      sample.stream().filter(item -> isValidUri(item.getValue()))
          .forEach(item -> addDcatPropertyAsResource(item, datasetResource, model, true));
    }

    List<DcatProperty> source = dataset.getSource();
    if (source != null) {
      source.stream().filter(item -> isValidUri(item.getValue()))
          .forEach(item -> addDcatPropertyAsResource(item, datasetResource, model, true));
    }

    serializeSpatialCoverage(dataset.getSpatialCoverage(), model, datasetResource);

    serializeTemporalCoverage(dataset.getTemporalCoverage(), model, datasetResource);

    addDcatPropertyAsLiteral(dataset.getType(), datasetResource, model);
    addDcatPropertyAsLiteral(dataset.getVersion(), datasetResource, model);

    List<DcatProperty> versionNotes = dataset.getVersionNotes();
    if (versionNotes != null) {
      versionNotes.stream().forEach(item -> addDcatPropertyAsLiteral(item, datasetResource, model));
    }

    // new, add also List<DcatDatasetSeries> inSeries;
    /*
     * logger.info(
     * "applicableLegislation size: " +
     * (applicableLegislation != null && !applicableLegislation.isEmpty() ?
     * applicableLegislation.size() : null));
     */
    List<DcatProperty> applicableLegislation = dataset.getApplicableLegislation();
    if (applicableLegislation != null) {
      applicableLegislation.stream()
          .filter(item -> StringUtils.isNotBlank(item.getValue()))
          .forEach(item -> {
            if (isValidUri(item.getValue())) {
              Resource legalResource = model.createResource(item.getValue());
              legalResource.addProperty(RDF.type, ELI.LegalResource);
              datasetResource.addProperty(DCATAP.applicableLegislation, legalResource);
            } else {
              datasetResource.addProperty(DCATAP.applicableLegislation, model.createLiteral(item.getValue()));
            }
          });
    }

    // For wasGeneratedBy
    List<DcatProperty> wasGeneratedBy = dataset.getWasGeneratedBy();
    if (wasGeneratedBy != null) {
      wasGeneratedBy.stream()
          .filter(item -> StringUtils.isNotBlank(item.getValue()))
          .forEach(item -> {
            if (isValidUri(item.getValue())) {
              Resource resource = model.createResource(item.getValue());
              datasetResource.addProperty(ResourceFactory.createProperty("https://www.w3.org/ns/prov#wasGeneratedBy"),
                  resource);
            } else {
              datasetResource.addProperty(ResourceFactory.createProperty("https://www.w3.org/ns/prov#wasGeneratedBy"),
                  model.createLiteral(item.getValue()));
            }
          });
    }

    // For HVDCategory
    List<DcatProperty> HVDCategory = dataset.getHVDCategory();
    if (HVDCategory != null) {
      HVDCategory.stream()
          .filter(item -> StringUtils.isNotBlank(item.getValue()))
          .forEach(item -> {
            if (isValidUri(item.getValue())) {
              Resource resource = model.createResource(item.getValue());
              datasetResource.addProperty(DCATAP.hvdCategory, resource);
            } else {
              datasetResource.addProperty(DCATAP.hvdCategory, model.createLiteral(item.getValue()));
            }
          });
    }

    if (StringUtils.isNotBlank(dataset.getTemporalResolution().getValue())) {
      if (isValidUri(dataset.getTemporalResolution().getValue())) {
        datasetResource.addProperty(DCAT.temporalResolution,
            model.createResource(dataset.getTemporalResolution().getValue()));
      } else {
        datasetResource.addProperty(DCAT.temporalResolution,
            model.createLiteral(dataset.getTemporalResolution().getValue()));
      }
    }

    // Serialize qualifiedRelation
    List<Relationship> qualifiedRelation = dataset.getQualifiedRelation();
    if (qualifiedRelation != null && !qualifiedRelation.isEmpty()) {
      for (Relationship relationship : qualifiedRelation) {

        Resource relationshipResource = model.createResource(Relationship.getRdfClass());

        if (StringUtils.isNotBlank(relationship.getHad_role().getValue())) {
          if (isValidUri(relationship.getHad_role().getValue())) {
            relationshipResource.addProperty(DCAT.hadRole,
                model.createResource(relationship.getHad_role().getValue()));
          } else {
            relationshipResource.addProperty(DCAT.hadRole,
                model.createLiteral(relationship.getHad_role().getValue()));
          }
        }

        if (StringUtils.isNotBlank(relationship.getRelation().getValue())) {
          if (isValidUri(relationship.getRelation().getValue())) {
            relationshipResource.addProperty(DCTerms.relation,
                model.createResource(relationship.getRelation().getValue()));
          } else {
            relationshipResource.addProperty(DCTerms.relation,
                model.createLiteral(relationship.getRelation().getValue()));
          }
        }

        datasetResource.addProperty(DCAT.qualifiedRelation, relationshipResource);
      }
    }

    for (DcatDistribution distribution : dataset.getDistributions()) {
      addDistributionToModel(model, datasetResource, distribution);
    }

    OdmsCatalogue node = nodeResources.get(new Integer(dataset.getNodeId()));

    /*
     * Add the Catalogue with the Dataset to the global Model
     */
    model.createResource(node.getHost(), DCAT.Catalog).addLiteral(DCTerms.title, node.getName())
        .addLiteral(DCTerms.description, node.getDescription())
        .addProperty(DCAT.dataset, datasetResource)
        .addProperty(DCTerms.issued, node.getRegisterDate().toString(), XSDDateType.XSDdateTime)
        .addProperty(DCTerms.modified, node.getLastUpdateDate().toString(),
            XSDDateType.XSDdateTime);

    /*
     * ** Create the Publisher for the DCATCatalogue as a new FOAFAgent with info
     * from the federated Catalogue
     */

    String publisherResourceUri = node.getPublisherUrl();
    if (StringUtils.isBlank(publisherResourceUri) || !isValidUri(publisherResourceUri)) {

      if (!node.getHomepage().equalsIgnoreCase(node.getHost())) {
        publisherResourceUri = node.getHomepage();
      } else {
        publisherResourceUri = node.getHomepage() + "/" + node.getId();
      }
    }

    /*
     * Check if the publisher is already present in the global Model and if any
     * create it, either or both for dataset and catalog
     */

    // serializeFoafAgent(
    // new FoafAgent(DCTerms.publisher.getURI(), publisherResourceUri,
    // node.getPublisherName(),
    // node.getPublisherEmail(), node.getPublisherUrl(), "", "",
    // String.valueOf(node.getId())),
    // model, model.getResource(node.getHost()));

    FoafAgent datasetPublisher = dataset.getPublisher();
    if (datasetPublisher != null) {
      if (StringUtils.isNotBlank(datasetPublisher.getResourceUri())
          && isValidUri(datasetPublisher.getResourceUri())) {
        serializeFoafAgent(datasetPublisher, model, datasetResource);
      } else {
        // Set blank URI for Dataset Publisher in order to create a blank node
        datasetPublisher.setResourceUri("");
        serializeFoafAgent(datasetPublisher, model, datasetResource);
      }
    }

    return model;

  }

  /**
   * Serialize temporal coverage.
   *
   * @param temporalCoverage the temporal coverage
   * @param model            the model
   * @param datasetResource  the dataset resource
   */
  protected static void serializeTemporalCoverage(List<DctPeriodOfTime> temporalCoverage, Model model,
      Resource datasetResource) {
    if (temporalCoverage != null) {
      for (DctPeriodOfTime period : temporalCoverage) {
        datasetResource.addProperty(model.createProperty(period.getUri()),
            model.createResource(DctPeriodOfTime.getRdfClass())
                .addProperty(period.getStartDate().getProperty(),
                    period.getStartDate().getValue(), XSDDateType.XSDdate)
                .addProperty(period.getEndDate().getProperty(),
                    period.getEndDate().getValue(), XSDDateType.XSDdate)
                .addProperty(period.getBeginning().getProperty(), period.getBeginning().getValue(),
                    XSDDateType.XSDdate)
                .addProperty(period.getEnd().getProperty(), period.getEnd().getValue(),
                    XSDDateType.XSDdate));
      }
    }
  }

  /**
   * Serialize spatial coverage.
   *
   * @param spatialCoverage the spatial coverage
   * @param model           the model
   * @param parentResource  the parent resource
   */
  protected static void serializeSpatialCoverage(List<DctLocation> spatialCoverage, Model model,
      Resource parentResource) {

    if (spatialCoverage != null) {

      for (DctLocation location : spatialCoverage) {
        Resource spatialResource = model.createResource(DctLocation.getRdfClass());
        String geoUri = null;
        // Initialize spatial Resource

        if (StringUtils.isNotBlank(geoUri = location.getGeographicalIdentifier().getValue())) {

          if (IRIFactory.iriImplementation().create(geoUri).hasViolation(false)) {
            location.getGeographicalIdentifier().setValue(GEO_BASE_URI + geoUri);
          }

          addDcatPropertyAsLiteral(location.getGeographicalIdentifier(), spatialResource,
              model);
        }

        addDcatPropertyAsLiteral(location.getGeographicalIdentifier(), spatialResource, model);
        addDcatPropertyAsLiteral(location.getGeometry(), spatialResource, model);

        // TODO Geographical Name as SKOS CONCEPT
        addDcatPropertyAsResource(location.getGeographicalName(), spatialResource, model,
            false);

        addDcatPropertyAsLiteral(location.getBbox(), spatialResource, model);
        addDcatPropertyAsLiteral(location.getCentroid(), spatialResource, model);

        parentResource.addProperty(model.createProperty(location.getUri()), spatialResource);

      }
    }

  }

  /**
   * Serialize language.
   *
   * @param language        the language
   * @param model           the model
   * @param datasetResource the dataset resource
   */
  protected static void serializeLanguage(List<DcatProperty> language, Model model,
      Resource datasetResource) {

    if (language != null) {
      language.stream().filter(lang -> StringUtils.isNotBlank(lang.getValue())).forEach(lang -> {
        try {
          datasetResource.addProperty(lang.getProperty(),
              model.createResource(iriFactory.construct(lang.getValue()).toURI().toString()));
        } catch (IRIException | URISyntaxException e) {
          datasetResource.addProperty(lang.getProperty(),
              model.createResource(LANGUAGE_BASE_URI + lang.getValue()));
        }
      });
    }
  }

  /**
   * Serialize frequency.
   *
   * @param frequency       the frequency
   * @param model           the model
   * @param datasetResource the dataset resource
   */
  protected static void serializeFrequency(DcatProperty frequency, Model model,
      Resource datasetResource) {

    try {
      // Check if the value is a valid Frequency enum value
      DctFrequency.valueOf(frequency.getValue());
      datasetResource.addProperty(frequency.getProperty(),
          model.createResource(FREQUENCY_BASE_URI + frequency.getValue()));

    } catch (Exception e) {

      datasetResource.addProperty(frequency.getProperty(),
          model.createResource(FREQUENCY_BASE_URI + DctFrequency.UNKNOWN));
    }
  }

  /**
   * Serialize contact point.
   *
   * @param contactPointList the contact point list
   * @param model            the model
   * @param datasetResource  the dataset resource
   */
  private static void serializeContactPoint(List<VcardOrganization> contactPointList, Model model,
      Resource datasetResource) {

    if (contactPointList != null && !contactPointList.isEmpty()) {
      for (VcardOrganization contactPoint : contactPointList) {
        try {
          Resource contactPointR = null;

          if (StringUtils.isNotBlank(contactPoint.getResourceUri())) {
            contactPointR = model.createResource(contactPoint.getResourceUri(),
                VcardOrganization.getRdfClass());
          } else {
            contactPointR = model.createResource(VcardOrganization.getRdfClass());
          }

          // .addProperty(RDF.type, VCARD4.Kind)
          contactPointR.addLiteral(contactPoint.getFn().getProperty(),
              contactPoint.getFn().getValue());

          // Fix hasEmail value if needed
          String hasEmailValue = contactPoint.getHasEmail().getValue();
          // && CommonUtil.checkIfIsEmail(hasEmailValue)
          if (StringUtils.isNotBlank(hasEmailValue)) {
            if (!hasEmailValue.startsWith("mailto:")) {
              contactPoint.getHasEmail().setValue("mailto:" + hasEmailValue);
            }
            addDcatPropertyAsResource(contactPoint.getHasEmail(), contactPointR, model, false);
          }

          addDcatPropertyAsResource(contactPoint.getHasUrl(), contactPointR, model, false);

          // Add contactPoint property to dataset Resource;
          datasetResource.addProperty(model.createProperty(contactPoint.getPropertyUri()),
              contactPointR);

        } catch (Exception ignore) {
          logger.debug(ignore.getLocalizedMessage());
        }
      }
    }

  }

  /**
   * Serialize foaf agent.
   *
   * @param agent          the agent
   * @param model          the model
   * @param parentResource the parent resource
   */
  protected static void serializeFoafAgent(FoafAgent agent, Model model, Resource parentResource) {

    if (agent != null) {
      Resource agentResource = null;

      if (StringUtils.isNotBlank(agent.getResourceUri()) && isValidUri(agent.getResourceUri())) {
        agentResource = model.createResource(agent.getResourceUri(), FoafAgent.getRdfClass());
      } else {
        agentResource = model.createResource(FoafAgent.getRdfClass());
      }

      // addDcatPropertyAsLiteral(agent.getName(), agentResource, model);
      if (agent.getName() != null) {
        for (DcatProperty name : agent.getName()) {
          addDcatPropertyAsLiteral(name, agentResource, model);
        }
      }
      addDcatPropertyAsLiteral(agent.getMbox(), agentResource, model);
      addDcatPropertyAsLiteral(agent.getType(), agentResource, model);
      addDcatPropertyAsLiteral(agent.getIdentifier(), agentResource, model);
      addDcatPropertyAsResource(agent.getHomepage(), agentResource, model, false);

      parentResource.addProperty(model.createProperty(agent.getPropertyUri()), agentResource);

    }
  }

  /**
   * Serialize concept.
   *
   * @param <T>            the generic type
   * @param conceptList    the concept list
   * @param model          the model
   * @param parentResource the parent resource
   */
  protected static <T extends SkosConcept> void serializeConcept(List<T> conceptList, Model model,
      Resource parentResource) {

    if (conceptList != null && !conceptList.isEmpty()) {

      for (T concept : conceptList) {
        Resource conceptR = null;
        if (concept != null) {
          List<SkosPrefLabel> labelList = concept.getPrefLabel();

          if (StringUtils.isNotBlank(concept.getResourceUri())) {
            conceptR = model.createResource(concept.getResourceUri(), SKOS.Concept);
          } else {
            conceptR = model.createResource(SKOS.Concept);
          }

          if (labelList != null && !labelList.isEmpty()) {
            for (SkosPrefLabel l : labelList) {
              conceptR.addProperty(SKOS.prefLabel,
                  model.createLiteral(l.getValue(), l.getLanguage()));
            }
          }

          parentResource.addProperty(model.createProperty(concept.getPropertyUri()), conceptR);

        }

        // if (StringUtils.isNotBlank(concept.getValue()))
        // datasetResource.addProperty(concept.getProperty(),
        // model.createResource(
        // IRIFactory.jenaImplementation().construct(concept.getValue()).toURI().toString(),
        // SKOS.Concept));

        // } catch (IRIException | URISyntaxException e) {
        // datasetResource.addProperty(concept.getProperty(),
        // model.createResource(THEME_BASE_URI + concept.getValue(), SKOS.Concept));
        // }
      }
    }
  }

  /**
   * Adds the distribution to model.
   *
   * @param model           the model
   * @param datasetResource the dataset resource
   * @param distribution    the distribution
   */
  private static void addDistributionToModel(Model model, Resource datasetResource,
      DcatDistribution distribution) {

    Resource distResource = model.createResource(DcatDistribution.getRdfClass());

    addDcatPropertyAsResource(distribution.getAccessUrl(), distResource, model, false);

    addDcatPropertyAsLiteral(distribution.getDescription(), distResource, model);

    serializeFormat(distribution.getFormat(), model, distResource);

    serializeMediaType(distribution.getMediaType(), model, distResource);

    serializeLicense(distribution.getLicense(), model, distResource);

    serializeByteSize(distribution.getByteSize(), model, distResource);

    serializeChecksum(distribution.getChecksum(), model, distResource);

    serializeLanguage(distribution.getLanguage(), model, distResource);

    List<DcatProperty> distDocumentation = distribution.getDocumentation();
    if (distDocumentation != null) {
      distDocumentation.stream().filter(item -> StringUtils.isNotBlank(item.getValue()))
          .forEach(item -> distResource.addProperty(item.getProperty(), item.getValue()));
    }

    addDcatPropertyAsResource(distribution.getDownloadUrl(), distResource, model, false);

    serializeDctStandard(distribution.getLinkedSchemas(), distResource, model);

    // addDcatPropertyAsLiteral(distribution.getMediaType(), distResource, model);
    addDcatPropertyAsResource(distribution.getMediaType(), distResource, model, true);

    distResource.addProperty(distribution.getReleaseDate().getProperty(),
        distribution.getReleaseDate().getValue(), XSDDateType.XSDdateTime);
    distResource.addProperty(distribution.getUpdateDate().getProperty(),
        distribution.getUpdateDate().getValue(), XSDDateType.XSDdateTime);

    // addDcatPropertyAsLiteral(distribution.getRights(), distResource, model);

    if (isValidUri(distribution.getRights().getValue())) {
      addDcatPropertyAsResource(distribution.getRights(), distResource, model, true);
    } else {
      addDcatPropertyAsLiteral(distribution.getRights(), distResource, model);
    }

    // Property p = model.createProperty(distribution.getRights().getUri());
    // distResource.addProperty(p,
    // model.createResource(distribution.getRights().getValue(),
    // distribution.getRights().getRange()));

    serializeConcept(Arrays.asList(distribution.getStatus()), model, distResource);

    addDcatPropertyAsLiteral(distribution.getTitle(), distResource, model);

    // new
    // Serialize accessService(s)
    List<DcatDataService> accessServices = distribution.getAccessService();
    if (accessServices != null && !accessServices.isEmpty()) {
      for (DcatDataService service : accessServices) {

        Resource serviceResource = model.createResource(DcatDataService.getRdfClass());

        if (service.getEndpointURL() != null) {
          service.getEndpointURL().stream()
              .filter(item -> StringUtils.isNotBlank(item.getValue()))
              .forEach(item -> {
                if (isValidUri(item.getValue())) {
                  serviceResource.addProperty(DCAT.endpointURL, model.createResource(item.getValue()));
                } else {
                  serviceResource.addProperty(DCAT.endpointURL, model.createLiteral(item.getValue()));
                }
              });
        }

        if (service.getEndpointDescription() != null) {
          service.getEndpointDescription().stream()
              .filter(item -> StringUtils.isNotBlank(item.getValue()))
              .forEach(item -> {
                if (isValidUri(item.getValue())) {
                  serviceResource.addProperty(DCAT.endpointDescription, model.createResource(item.getValue()));
                } else {
                  serviceResource.addProperty(DCAT.endpointDescription, model.createLiteral(item.getValue()));
                }
              });
        }

        /*
         * if (service.getApplicableLegislation() != null) {
         * service.getApplicableLegislation().stream()
         * .filter(item -> StringUtils.isNotBlank(item.getValue()))
         * .forEach(item -> {
         * if (isValidUri(item.getValue())) {
         * Resource legalResource = model.createResource(item.getValue());
         * legalResource.addProperty(RDF.type, ELI.LegalResource);
         * serviceResource.addProperty(DCATAP.applicableLegislation, legalResource);
         * } else {
         * serviceResource.addProperty(DCATAP.applicableLegislation,
         * model.createLiteral(item.getValue()));
         * }
         * });
         * }
         */

        /*
         * if (service.getContactPoint() != null &&
         * !service.getContactPoint().isEmpty()) {
         * serializeContactPoint(service.getContactPoint(), model, serviceResource);
         * }
         */

        /*
         * if (StringUtils.isNotBlank(service.getLicence().getValue())) {
         * if (isValidUri(service.getLicence().getValue())) {
         * serviceResource.addProperty(DCTerms.license,
         * model.createResource(service.getLicence().getValue()));
         * } else {
         * serviceResource.addProperty(DCTerms.license,
         * model.createLiteral(service.getLicence().getValue()));
         * }
         * }
         */

        if (service.getRights() != null) {
          service.getRights().stream()
              .filter(item -> StringUtils.isNotBlank(item.getValue()))
              .forEach(item -> {
                  // serviceResource.addProperty(DCTerms.rights, model.createLiteral(item.getValue()));
                if (isValidUri(item.getValue())) {
                  serviceResource.addProperty(DCTerms.rights, model.createResource(item.getValue()));
                } else {
                  serviceResource.addProperty(DCTerms.rights, model.createLiteral(item.getValue()));
                }
              });
        }

        if (service.getServesDataset() != null) {
          service.getServesDataset().stream()
              .filter(item -> StringUtils.isNotBlank(item.getValue()))
              .forEach(item -> {
                if (isValidUri(item.getValue())) {
                  serviceResource.addProperty(DCAT.servesDataset, model.createResource(item.getValue()));
                } else {
                  serviceResource.addProperty(DCAT.servesDataset, model.createLiteral(item.getValue()));
                }
              });
        }

        addDcatPropertyAsLiteral(service.getTitle(), serviceResource, model);

        // Link the distribution to the DataService
        distResource.addProperty(DCAT.accessService, serviceResource);
      }
    }

    /*
     * logger.info(
     * "applicableLegislation size: " +
     * (applicableLegislation != null && !applicableLegislation.isEmpty() ?
     * applicableLegislation.size() : null));
     */
    List<DcatProperty> applicableLegislation = distribution.getApplicableLegislation();
    if (applicableLegislation != null) {
      applicableLegislation.stream()
          .filter(item -> StringUtils.isNotBlank(item.getValue()))
          .forEach(item -> {
            if (isValidUri(item.getValue())) {
              Resource legalResource = model.createResource(item.getValue());
              legalResource.addProperty(RDF.type, ELI.LegalResource);
              distResource.addProperty(DCATAP.applicableLegislation, legalResource);
            } else {
              distResource.addProperty(DCATAP.applicableLegislation, model.createLiteral(item.getValue()));
            }
          });
    }

    // addDcatPropertyAsResource(distribution.getHasPolicy(), distResource, model,
    // false);

    if (StringUtils.isNotBlank(distribution.getHasPolicy().getValue())) {
      if (isValidUri(distribution.getHasPolicy().getValue())) {
        distResource.addProperty(model.createProperty("https://www.w3.org/ns/odrl/2/"),
            model.createResource(distribution.getHasPolicy().getValue()));
      } else {
        distResource.addProperty(model.createProperty("https://www.w3.org/ns/odrl/2/"),
            model.createLiteral(distribution.getHasPolicy().getValue()));
      }
    }

    if (StringUtils.isNotBlank(distribution.getAvailability().getValue())) {
      if (isValidUri(distribution.getAvailability().getValue())) {
        distResource.addProperty(DCATAP.availability,
            model.createResource(distribution.getAvailability().getValue()));
      } else {
        distResource.addProperty(DCATAP.availability,
            model.createLiteral(distribution.getAvailability().getValue()));
      }
    }
    if (StringUtils.isNotBlank(distribution.getCompressionFormat().getValue())) {
      if (isValidUri(distribution.getCompressionFormat().getValue())) {
        distResource.addProperty(DCAT.compressFormat,
            model.createResource(distribution.getCompressionFormat().getValue()));
      } else {
        distResource.addProperty(DCAT.compressFormat,
            model.createLiteral(distribution.getCompressionFormat().getValue()));
      }
    }
    if (StringUtils.isNotBlank(distribution.getPackagingFormat().getValue())) {
      if (isValidUri(distribution.getPackagingFormat().getValue())) {
        distResource.addProperty(DCAT.packageFormat,
            model.createResource(distribution.getPackagingFormat().getValue()));
      } else {
        distResource.addProperty(DCAT.packageFormat,
            model.createLiteral(distribution.getPackagingFormat().getValue()));
      }
    }

    if (StringUtils.isNotBlank(distribution.getTemporalResolution().getValue())) {
      if (isValidUri(distribution.getTemporalResolution().getValue())) {
        distResource.addProperty(DCAT.temporalResolution,
            model.createResource(distribution.getTemporalResolution().getValue()));
      } else {
        distResource.addProperty(DCAT.temporalResolution,
            model.createLiteral(distribution.getTemporalResolution().getValue()));
      }
    }

    if (StringUtils.isNotBlank(distribution.getSpatialResolution().getValue())) {
      if (isValidUri(distribution.getSpatialResolution().getValue())) {
        distResource.addProperty(DCAT.spatialResolutionInMeters,
            model.createResource(distribution.getSpatialResolution().getValue()));
      } else {
        distResource.addProperty(DCAT.spatialResolutionInMeters,
            model.createLiteral(distribution.getSpatialResolution().getValue()));
      }
    }

    datasetResource.addProperty(DCAT.distribution, distResource);
  }

  /**
   * Serialize byte size.
   *
   * @param property     the property
   * @param model        the model
   * @param distResource the dist resource
   */
  protected static void serializeByteSize(DcatProperty property, Model model,
      Resource distResource) {

    try {
      distResource.addProperty(property.getProperty(),
          model.createTypedLiteral(new BigDecimal(property.getValue())));
    } catch (NumberFormatException e) {
      logger.debug("The value: " + property.getValue() + " is not a valid byteSize - SKIPPED");
    }
  }

  /**
   * Serialize checksum.
   *
   * @param checksum     the checksum
   * @param model        the model
   * @param distResource the dist resource
   */
  protected static void serializeChecksum(SpdxChecksum checksum, Model model,
      Resource distResource) {

    if (checksum != null) {
      distResource.addProperty(model.createProperty(checksum.getUri()),
          model.createResource(SpdxChecksum.getRdfClass())
              .addProperty(checksum.getAlgorithm().getProperty(),
                  checksum.getAlgorithm().getValue())
              .addProperty(checksum.getChecksumValue().getProperty(),
                  checksum.getChecksumValue().getValue(), XSDhexBinary.XSDhexBinary));
    }
  }

  /**
   * Serialize license.
   *
   * @param license      the license
   * @param model        the model
   * @param distResource the dist resource
   */
  protected static void serializeLicense(DctLicenseDocument license, Model model,
      Resource distResource) {

    if (license != null) {

      String licenseTypeUri = null;
      Resource licenseResource = null;

      // Initialize license Resource
      if (StringUtils.isNotBlank(license.getUri())) {
        licenseResource = model.createResource(license.getUri(), DctLicenseDocument.getRdfClass());
      } else {
        licenseResource = model.createResource(DctLicenseDocument.getRdfClass());
      }

      try {
        licenseTypeUri = iriFactory.construct(license.getType().getValue()).toString();
      } catch (Exception e) {
        licenseTypeUri = LICENSE_TYPE_BASE_URI + license.getType().getValue();
      }
      license.getType().setValue(licenseTypeUri);

      addDcatPropertyAsLiteral(license.getName(), licenseResource, model);
      addDcatPropertyAsLiteral(license.getVersionInfo(), licenseResource, model);
      addDcatPropertyAsResource(license.getType(), licenseResource, model, false);

      // Add license Resource as property object of the parent Resource
      distResource.addProperty(model.createProperty(DCTerms.license.getURI()), licenseResource);

    }
  }

  /**
   * Serialize format.
   *
   * @param format       the format
   * @param model        the model
   * @param distResource the dist resource
   */
  protected static void serializeFormat(DcatProperty format, Model model, Resource distResource) {

    if (format != null && StringUtils.isNotBlank(format.getValue())) {
      format.setValue(FORMAT_BASE_URI + format.getValue());
      // addDcatPropertyAsResource(format, distResource, model, false);
      addDcatPropertyAsResource(format, distResource, model, true);
    }
  }

  /**
   * Serialize mediaType.
   *
   * @param mediatype    the format
   * @param model        the model
   * @param distResource the dist resource
   */
  protected static void serializeMediaType(DcatProperty mediatype, Model model, Resource distResource) {

    if (mediatype != null && StringUtils.isNotBlank(mediatype.getValue())) {
      mediatype.setValue(MEDIATYPE_BASE_URI + mediatype.getValue());
      // addDcatPropertyAsResource(format, distResource, model, false);
      addDcatPropertyAsResource(mediatype, distResource, model, true);
    }
  }

  /**
   * Serialize dct standard.
   *
   * @param standardList   the standard list
   * @param parentResource the parent resource
   * @param model          the model
   */
  protected static void serializeDctStandard(List<DctStandard> standardList,
      Resource parentResource, Model model) {

    if (standardList != null && !standardList.isEmpty()) {
      standardList.stream().filter(standard -> standard != null).forEach(standard -> {

        Resource standardResource = null;

        if (StringUtils.isNotBlank(standard.getUri())) {
          standardResource = model.createResource(standard.getUri(), DctStandard.getRdfClass());
        } else {
          standardResource = model.createResource(DctStandard.getRdfClass());
        }

        standardResource
            .addLiteral(standard.getDescription().getProperty(),
                standard.getDescription().getValue())
            .addLiteral(standard.getIdentifier().getProperty(), standard.getIdentifier().getValue())
            .addLiteral(standard.getTitle().getProperty(), standard.getTitle().getValue());

        for (DcatProperty p : standard.getReferenceDocumentation()) {
          standardResource.addProperty(p.getProperty(), p.getValue(), XSDDateType.XSDanyURI);
        }

        parentResource.addProperty(DCTerms.conformsTo, standardResource);

      });
    }

  }

  /**
   * Adds the dcat property as resource.
   *
   * @param property       the property
   * @param parentResource the parent resource
   * @param model          the model
   * @param useRange       the use range
   */
  /*
   * Helper method used to add a Property, which should have a Resource as its
   * Object, to a passed Resource e.g. hasUrl, hasEmail or hasTelephone -> type
   * Optionally use the DCATProperty Range to create a typed Resource
   */
  protected static void addDcatPropertyAsResource(DcatProperty property, Resource parentResource,
      Model model, boolean useRange) {

    if (StringUtils.isNotBlank(property.getValue())) {
      try {
        if (useRange) {
          parentResource.addProperty(property.getProperty(), model.createResource(
              iriFactory.construct(property.getValue()).toURI().toString(), property.getRange()));
        } else {
          parentResource.addProperty(property.getProperty(),
              model.createResource(iriFactory.construct(property.getValue()).toURI().toString()));
        }
      } catch (ResourceRequiredException | IRIException | URISyntaxException e) {

        // Add anyway the property as string value
        System.out.println("ERROR in addDcatPropertyAsResource for " + property.getProperty() + ": "
            + e.getMessage());
        parentResource.addProperty(property.getProperty(),
            model.createLiteral(property.getValue()));
      }
    }
  }

  /**
   * Adds the dcat property as literal.
   *
   * @param property       the property
   * @param parentResource the parent resource
   * @param model          the model
   */
  /*
   * Helper method used to add a Property, which should have a Literal as its
   * Object, to a passed Resource
   */
  protected static void addDcatPropertyAsLiteral(DcatProperty property, Resource parentResource,
      Model model) {

    // Add the property as literal value
    if (StringUtils.isNotBlank(property.getValue())) {
      String value = property.getValue();

      logger.info("Value: " + value);
      logger.info("Property: " + property.getProperty());
      logger.info("Property Value: " + property.getValue());
      parentResource.addProperty(property.getProperty(), model.createLiteral(property.getValue()));
    }
  }

  /**
   * Adds the dcat property as typed literal.
   *
   * @param property       the property
   * @param dataType       the data type
   * @param parentResource the parent resource
   * @param model          the model
   */
  /*
   * Helper method used to add a Property, which should have a Typed Literal as
   * its Object, to a passed Resource
   */
  protected static void addDcatPropertyAsTypedLiteral(DcatProperty property, RDFDatatype dataType,
      Resource parentResource, Model model) {

    // Add the property as literal value
    if (StringUtils.isNotBlank(property.getValue())) {
      parentResource.addProperty(property.getProperty(), property.getValue(), dataType);
    }
  }

  /**
   * Write model to string.
   *
   * @param model  the model
   * @param format the format
   * @return the string
   */
  private static String writeModelToString(Model model, DcatApFormat format) {

    StringWriter outputWriter = new StringWriter();
    model.write(outputWriter, format.formatName());
    return outputWriter.toString();
  }

  /**
   * Write model to file and zip.
   *
   * @param model    the model
   * @param format   the format
   * @param filePath the file path
   * @param fileName the file name
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static void writeModelToFileAndZip(Model model, DcatApFormat format, String filePath,
      String fileName) throws IOException {
    writeModelToFile(model, format, filePath, fileName);
    writeModelToZipFile(model, format, filePath, fileName);
  }

  /**
   * Write model to file.
   *
   * @param model    the model
   * @param format   the format
   * @param filePath the file path
   * @param fileName the file name
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static void writeModelToFile(Model model, DcatApFormat format, String filePath,
      String fileName) throws IOException {
    /* This method is used to serialize the Jena-model to file. */

    FileWriter out = null;
    logger.info("Writing model to file: " + filePath + fileName);

    Instant tick = Instant.now();
    try {
      out = new FileWriter(filePath + fileName);
      model.write(out, format.name());
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (out != null) {
        out.close();
      }

      Instant tock = Instant.now();
      logger.info("File writing completed in: " + Duration.between(tick, tock).toString());

    }
  }

  /**
   * Write model to zip file.
   *
   * @param model    the model
   * @param format   the format
   * @param filePath the file path
   * @param fileName the file name
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static void writeModelToZipFile(Model model, DcatApFormat format, String filePath,
      String fileName) throws IOException {

    logger.info("Writing model to file: " + filePath + fileName + ".zip");
    Instant tick = Instant.now();
    try {
      File f = new File(filePath + fileName + ".zip");
      ZipOutputStream out = new ZipOutputStream(new FileOutputStream(f));
      ZipEntry e = new ZipEntry(fileName);
      out.putNextEntry(e);

      byte[] data = writeModelToString(model, format).getBytes();
      out.write(data, 0, data.length);
      out.closeEntry();
      out.close();

    } finally {
      Instant tock = Instant.now();
      logger.info("File writing completed in: " + Duration.between(tick, tock).toString());

    }
  }

  // public static String datasetToDCATAP(DCATDataset dataset, DCATAPFormat
  // format) {
  // return (writeModelToString(datasetToModel(dataset), format));
  // }

  // public static List<String> datasetsToDCATAPList(List<DCATDataset> datasets,
  // DCATAPFormat format) {
  // return datasets.stream().map(item -> writeModelToString(datasetToModel(item),
  // format))
  // .collect(Collectors.toList());
  // }

  /**
   * Search result to dcat ap.
   *
   * @param result    the result
   * @param format    the format
   * @param profile   the profile
   * @param writeType the write type
   * @return the string
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static String searchResultToDcatAp(SearchResult result, DcatApFormat format,
      DcatApProfile profile, DcatApWriteType writeType) throws IOException {

    nodeResources = FederationCore.getOdmsCatalogues().stream()
        .collect(Collectors.toMap(OdmsCatalogue::getId, node -> node));

    Model model = datasetsToModel(result.getResults(), profile);

    model.setNsPrefix("co", "http://purl.org/ontology/co/core#");
    model.createResource("http://purl.org/ontology/co/core#Counter")
        .addProperty(DCTerms.description, "The total count of matching datasets").addLiteral(
            model.createProperty("http://purl.org/ontology/co/core#count"), result.getCount());

    if (writeType.equals(DcatApWriteType.FILE)) {

      writeModelToFileAndZip(model, format, filePath, fileName);
      return writeModelToString(model, format);
      // } else if (writeType.equals(DCATAPWriteType.ZIPFILE)) {
      //
      // writeModelToZipFile(model, format,
      // PropertyManager.getProperty(IdraProperty.DUMP_FILE_PATH),
      // "datasetDump");
      // return "";
    } else {

      return writeModelToString(model, format);
    }

  }

  /**
   * Search result to dcat ap by node.
   *
   * @param nodeId    the node id
   * @param result    the result
   * @param format    the format
   * @param profile   the profile
   * @param writeType the write type
   * @return the string
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static String searchResultToDcatApByNode(String nodeId, SearchResult result,
      DcatApFormat format, DcatApProfile profile, DcatApWriteType writeType) throws IOException {

    nodeResources = FederationCore.getOdmsCatalogues().stream()
        .collect(Collectors.toMap(OdmsCatalogue::getId, node -> node));

    Model model = datasetsToModel(result.getResults(), profile);

    model.setNsPrefix("co", "http://purl.org/ontology/co/core#");
    model.createResource("http://purl.org/ontology/co/core#Counter")
        .addProperty(DCTerms.description, "The total count of matching datasets").addLiteral(
            model.createProperty("http://purl.org/ontology/co/core#count"), result.getCount());

    if (writeType.equals(DcatApWriteType.FILE)) {

      writeModelToFileAndZip(model, format, filePath, fileName + "_node_" + nodeId);

      return writeModelToString(model, format);
      // } else if (writeType.equals(DCATAPWriteType.ZIPFILE)) {
      //
      // writeModelToZipFile(model, format,
      // PropertyManager.getProperty(IdraProperty.DUMP_FILE_PATH),
      // "datasetDump");
      // return "";
    } else {

      return writeModelToString(model, format);
    }

  }

  /**
   * Checks if is valid uri.
   *
   * @param uri the uri
   * @return true, if is valid uri
   */
  protected static boolean isValidUri(String uri) {
    return !iriFactory.create(uri).hasViolation(false);
  }

}
