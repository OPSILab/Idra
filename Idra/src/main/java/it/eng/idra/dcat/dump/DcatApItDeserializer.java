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

import it.eng.idra.beans.dcat.DCATAP;
import it.eng.idra.beans.dcat.DcatApProfileNotValidException;
import it.eng.idra.beans.dcat.DcatDataset;
import it.eng.idra.beans.dcat.DcatDatasetSeries;
import it.eng.idra.beans.dcat.DcatDetails;
import it.eng.idra.beans.dcat.DcatDistribution;
import it.eng.idra.beans.dcat.DcatProperty;
import it.eng.idra.beans.dcat.DctLocation;
import it.eng.idra.beans.dcat.DctPeriodOfTime;
import it.eng.idra.beans.dcat.DctStandard;
import it.eng.idra.beans.dcat.FoafAgent;
import it.eng.idra.beans.dcat.Relationship;
import it.eng.idra.beans.dcat.SkosConceptSubject;
import it.eng.idra.beans.dcat.SkosConceptTheme;
import it.eng.idra.beans.dcat.VcardOrganization;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.LiteralRequiredException;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.OWL;

// TODO: Auto-generated Javadoc
/**
 * The Class DcatApItDeserializer.
 */
public class DcatApItDeserializer extends DcatApDeserializer {

  /** The Constant DCATAP_IT_BASE_URI. */
  private static final String DCATAP_IT_BASE_URI = "http://dati.gov.it/onto/dcatapit#";

  /** The Constant datasetPattern. */
  private static final Pattern datasetPattern = Pattern
      .compile("\\w*<(dcat|dcatapit):Dataset rdf:about=\\\"(.*)\\\"");

  /** The start date prop. */
  private static Property startDateProp = ResourceFactory
      .createProperty(DCATAP_IT_BASE_URI + "startDate");

  /** The end date prop. */
  private static Property endDateProp = ResourceFactory
      .createProperty(DCATAP_IT_BASE_URI + "endDate");

  /** The beginning prop. */ // check if this is ok
  private static Property beginningProp = ResourceFactory
      .createProperty(DCATAP_IT_BASE_URI + "beginning");

  /** The end prop. */ // check if this is ok
  private static Property endProp = ResourceFactory
      .createProperty(DCATAP_IT_BASE_URI + "end");

  /**
   * Instantiates a new dcat ap it deserializer.
   */
  public DcatApItDeserializer() {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * it.eng.idra.dcat.dump.DcatApDeserializer#resourceToDataset(java.lang.String,
   * org.apache.jena.rdf.model.Resource)
   */
  @Override
  public DcatDataset resourceToDataset(String nodeId, Resource datasetResource)
      throws DcatApProfileNotValidException {
    // Properties to be mapped among different CKAN fallback fields

    String title = null;
    String description = null;
    String releaseDate = null;
    String updateDate = null;
    String version = null;
    List<SkosConceptTheme> theme = null;
    List<String> keywords = new ArrayList<String>();
    List<String> documentation = new ArrayList<String>();
    List<String> sample = new ArrayList<String>();
    List<String> versionNotes = new ArrayList<String>();
    List<String> relatedResource = new ArrayList<String>();

    List<DcatDistribution> distributionList = new ArrayList<DcatDistribution>();

    // new
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

    if (datasetResource.hasProperty(DCTerms.title)) {
      title = datasetResource.getRequiredProperty(DCTerms.title).getString();
    }
    if (datasetResource.hasProperty(DCTerms.description)) {
      description = datasetResource.getRequiredProperty(DCTerms.description).getString();
    }

    // Handle theme concepts
    theme = deserializeConcept(nodeId, datasetResource, DCAT.theme, SkosConceptTheme.class);
    FoafAgent publisher = null;
    publisher = deserializeFoafAgent(nodeId, datasetResource.getProperty(DCTerms.publisher));
    List<VcardOrganization> contactPointList = null;
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
    String frequency = null;
    frequency = deserializeFrequency(datasetResource);

    // Iterate over hasVersion properties
    List<String> hasVersion = new ArrayList<String>();
    StmtIterator hasVit = datasetResource.listProperties(DCTerms.hasVersion);
    while (hasVit.hasNext()) {
      hasVersion.add(hasVit.next().getString());
    }

    // Iterate over isVersionOf properties
    List<String> isVersionOf = new ArrayList<String>();
    StmtIterator isVit = datasetResource.listProperties(DCTerms.isVersionOf);
    while (isVit.hasNext()) {
      Statement isV = isVit.next();
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

    // Handle subject concepts
    List<SkosConceptSubject> subject = null;
    subject = deserializeConcept(nodeId, datasetResource, DCTerms.subject,
        SkosConceptSubject.class);

    // Handle RightsHolder
    FoafAgent rightsHolder = null;
    if (datasetResource.hasProperty(DCTerms.rightsHolder)) {
      rightsHolder = deserializeFoafAgent(nodeId,
          datasetResource.getProperty(DCTerms.rightsHolder));
    }

    // Handle Creator
    FoafAgent creator = null;
    if (datasetResource.hasProperty(DCTerms.creator)) {
      creator = deserializeFoafAgent(nodeId, datasetResource.getProperty(DCTerms.creator));
    }

    // Handle distributions
    StmtIterator distrIt = datasetResource.listProperties(DCAT.distribution);
    while (distrIt.hasNext()) {
      distributionList.add(resourceToDcatDistribution(distrIt.next().getResource(), nodeId));
    }

    // Iterate over related properties
    StmtIterator relIt = datasetResource.listProperties(DCTerms.relation);
    while (relIt.hasNext()) {
      relatedResource.add(relIt.next().getString());
    }

    // Iterate over applicableLegislation properties
    StmtIterator legIt = datasetResource.listProperties(DCATAP.applicableLegislation);
    while (legIt.hasNext()) {
      Statement stmt = legIt.next();
      try {
        applicableLegislation.add(stmt.getString());
        // logger.info("applicableLegislation: " + stmt.getString());
      } catch (LiteralRequiredException e) {
        applicableLegislation.add(stmt.getResource().getURI());
        // logger.info("applicableLegislation: " + stmt.getResource().getURI());
      }
    }
    // geographicalCoverage properties
    geographicalCoverage.add(spatialCoverage);

    /*
     * DcatDetails dcatDetails = new DcatDetails();
     * dcatDetails.setTitle(title);
     * dcatDetails.setDescription(description);
     * 
     * // Handle titles
     * titles.add(dcatDetails);
     * // Handle descriptions
     * descriptions.add(dcatDetails);
     */

    // Handle temporalCoverage
    temporalCoverageList.add(temporalCoverage);

    // Iterate over inSeries properties
    /*
     * StmtIterator inSeriesIt = datasetResource
     * .listProperties(ResourceFactory.createProperty(
     * "http://www.w3.org/ns/dcat#inSeries"));
     * while (inSeriesIt.hasNext()) {
     * Statement stmt = inSeriesIt.next();
     * if (stmt.getObject().isResource()) {
     * inSeries.add(new DcatDatasetSeries(applicableLegislation, contactPointList,
     * descriptions,
     * frequency, geographicalCoverage, updateDate, publisher, releaseDate,
     * temporalCoverageList, titles, nodeId,
     * identifier));
     * }
     * }
     * 
     * // Handle qualifiedRelation
     * Relationship relationship = new
     * Relationship(datasetResource.getProperty(DCAT.hadRole).getString(),
     * datasetResource.getProperty(DCTerms.relation).getString(),nodeId);
     * qualifiedRelation.add(relationship);
     */

    // Iterate over qualifiedRelation properties
    // StmtIterator qrelIt = datasetResource
    // .listProperties(ResourceFactory.createProperty("http://www.w3.org/ns/dcat#qualifiedRelation"));
    // while (qrelIt.hasNext()) {
    // qualifiedRelation.add(relationship);// qrelIt.next().getString()
    // }

    // Extract temporalResolution property
    if (datasetResource.hasProperty(DCAT.temporalResolution)) {
      try {
        temporalResolution = datasetResource.getProperty(DCAT.temporalResolution).getString();
      } catch (LiteralRequiredException e) {
        temporalResolution = datasetResource.getProperty(DCAT.temporalResolution).getResource().getURI();

      }
    }

    // Iterate over wasGeneratedBy properties
    StmtIterator wasGeneratedByIt = datasetResource
        .listProperties(ResourceFactory.createProperty("http://www.w3.org/ns/prov#wasGeneratedBy"));
    while (wasGeneratedByIt.hasNext()) {
      Statement stmt = wasGeneratedByIt.next();
      try {
        wasGeneratedBy.add(stmt.getString());
      } catch (LiteralRequiredException e) {
        wasGeneratedBy.add(stmt.getResource().getURI());
      }
    }

    // Iterate over HVDCategory properties
    StmtIterator HVDCategoryIt = datasetResource.listProperties(DCATAP.hvdCategory);
    while (HVDCategoryIt.hasNext()) {
      Statement stmt = HVDCategoryIt.next();
      try {
        HVDCategory.add(stmt.getString());
      } catch (LiteralRequiredException e) {
        HVDCategory.add(stmt.getResource().getURI());
      }
    }

    // Iterate over qualifiedRelation properties
    if (datasetResource.hasProperty(DCAT.qualifiedRelation)) {
      StmtIterator qualifiedRelationIt = datasetResource.listProperties(DCAT.qualifiedRelation);
      while (qualifiedRelationIt.hasNext()) {
        Statement stmt = qualifiedRelationIt.next();
        if (stmt.getObject().isResource()) {
          Resource qualifiedRelationRes = stmt.getResource();

          String hadRole = null;
          if (qualifiedRelationRes.hasProperty(DCAT.hadRole)) {
            try {
              hadRole = qualifiedRelationRes.getProperty(DCAT.hadRole).getString();
            } catch (LiteralRequiredException e) {
              hadRole = qualifiedRelationRes.getProperty(DCAT.hadRole).getResource().getURI();
            }
          }

          String relation = null;
          if (qualifiedRelationRes.hasProperty(DCTerms.relation)) {
            try {
              relation = qualifiedRelationRes.getProperty(DCTerms.relation).getString();
            } catch (LiteralRequiredException e) {
              relation = qualifiedRelationRes.getProperty(DCTerms.relation).getResource().getURI();
            }
          }

          Relationship relationship = new Relationship(hadRole, relation, nodeId);
          qualifiedRelation.add(relationship);
        }
      }
    }

    DcatDataset mapped;
    mapped = new DcatDataset(nodeId, identifier, title, description, distributionList, theme,
        publisher, contactPointList, keywords, accessRights, conformsTo, documentation, frequency,
        hasVersion, isVersionOf, landingPage, language, provenance, releaseDate, updateDate,
        otherIdentifier, sample, source, geographicalCoverage, temporalCoverageList, type, version,
        versionNotes, rightsHolder, creator, subject, relatedResource, applicableLegislation,
        inSeries, qualifiedRelation, temporalResolution, wasGeneratedBy, HVDCategory);

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
    subject = null;
    applicableLegislation = null;
    inSeries = null;
    qualifiedRelation = null;
    temporalResolution = null;
    wasGeneratedBy = null;
    HVDCategory = null;

    return mapped;

  }

  /**
   * deserializeTemporalCoverage.
   *
   * @param nodeId          the node id
   * @param datasetResource the dataset resource
   * @return the dct period of time
   */
  public DctPeriodOfTime deserializeTemporalCoverage(String nodeId, Resource datasetResource) {
    DcatProperty startDate = null; // datasetResource.getProperty(DCAT.startDate);
    DcatProperty endDate = null; // datasetResource.getProperty(DCAT.endDate);
    Resource temporalResource = datasetResource.getPropertyResourceValue(DCTerms.temporal);
    DcatProperty beginning = null;
    DcatProperty end = null;

    if (temporalResource != null) {

      if (temporalResource.hasProperty(startDateProp)) {
        startDate = new DcatProperty(startDateProp.getURI(),
            temporalResource.getProperty(startDateProp).getString());
      }

      if (temporalResource.hasProperty(endDateProp)) {
        endDate = new DcatProperty(endDateProp.getURI(),
            temporalResource.getProperty(endDateProp).getString());
      }

      if (temporalResource.hasProperty(beginningProp)) {
        beginning = new DcatProperty(beginningProp.getURI(),
            temporalResource.getProperty(beginningProp).getString());
      }

      if (temporalResource.hasProperty(endProp)) {
        end = new DcatProperty(endProp.getURI(),
            temporalResource.getProperty(endProp).getString());
      }

      return new DctPeriodOfTime(DCTerms.temporal.getURI(), startDate, endDate, nodeId, beginning, end);

    }

    return null;
  }

  // @Override
  // public Pattern getDatasetPattern() {
  // return datasetPattern;
  // }

}
