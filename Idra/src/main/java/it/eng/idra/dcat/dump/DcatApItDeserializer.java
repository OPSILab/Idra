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

package it.eng.idra.dcat.dump;

import it.eng.idra.beans.dcat.DcatApProfileNotValidException;
import it.eng.idra.beans.dcat.DcatDataset;
import it.eng.idra.beans.dcat.DcatDistribution;
import it.eng.idra.beans.dcat.DcatProperty;
import it.eng.idra.beans.dcat.DctLocation;
import it.eng.idra.beans.dcat.DctPeriodOfTime;
import it.eng.idra.beans.dcat.DctStandard;
import it.eng.idra.beans.dcat.FoafAgent;
import it.eng.idra.beans.dcat.SkosConceptSubject;
import it.eng.idra.beans.dcat.SkosConceptTheme;
import it.eng.idra.beans.dcat.VCardOrganization;
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

public class DcatApItDeserializer extends DcatApDeserializer {

  private static final String DCATAP_IT_BASE_URI = "http://dati.gov.it/onto/dcatapit#";
  private static final Pattern datasetPattern = Pattern
      .compile("\\w*<(dcat|dcatapit):Dataset rdf:about=\\\"(.*)\\\"");
  private static Property startDateProp = ResourceFactory
      .createProperty(DCATAP_IT_BASE_URI + "startDate");
  private static Property endDateProp = ResourceFactory
      .createProperty(DCATAP_IT_BASE_URI + "endDate");

  public DcatApItDeserializer() {
    super();
  }

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
    DcatDataset mapped;
    mapped = new DcatDataset(nodeId, identifier, title, description, distributionList, theme,
        publisher, contactPointList, keywords, accessRights, conformsTo, documentation, frequency,
        hasVersion, isVersionOf, landingPage, language, provenance, releaseDate, updateDate,
        otherIdentifier, sample, source, spatialCoverage, temporalCoverage, type, version,
        versionNotes, rightsHolder, creator, subject, relatedResource);

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

    return mapped;

  }

  /**
   * deserializeTemporalCoverage.
   *
   */
  public DctPeriodOfTime deserializeTemporalCoverage(String nodeId, Resource datasetResource) {
    DcatProperty startDate = null;
    DcatProperty endDate = null;
    Resource temporalResource = datasetResource.getPropertyResourceValue(DCTerms.temporal);

    if (temporalResource != null) {

      if (temporalResource.hasProperty(startDateProp)) {
        startDate = new DcatProperty(startDateProp.getURI(),
            temporalResource.getProperty(startDateProp).getString());
      }

      if (temporalResource.hasProperty(endDateProp)) {
        endDate = new DcatProperty(endDateProp.getURI(),
            temporalResource.getProperty(endDateProp).getString());
      }

      return new DctPeriodOfTime(DCTerms.temporal.getURI(), startDate, endDate, nodeId);

    }

    return null;
  }

  // @Override
  // public Pattern getDatasetPattern() {
  // return datasetPattern;
  // }

}
