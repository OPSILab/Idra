/*******************************************************************************
 * Idra - Open Data Federation Platform
 *  Copyright (C) 2018 Engineering Ingegneria Informatica S.p.A.
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

import it.eng.idra.beans.dcat.DCATAPProfileNotValidException;
import it.eng.idra.beans.dcat.DCATDataset;
import it.eng.idra.beans.dcat.DCATDistribution;
import it.eng.idra.beans.dcat.DCATProperty;
import it.eng.idra.beans.dcat.DCTLocation;
import it.eng.idra.beans.dcat.DCTPeriodOfTime;
import it.eng.idra.beans.dcat.DCTStandard;
import it.eng.idra.beans.dcat.FOAFAgent;
import it.eng.idra.beans.dcat.SKOSConceptSubject;
import it.eng.idra.beans.dcat.SKOSConceptTheme;
import it.eng.idra.beans.dcat.VCardOrganization;

public class DCATAPITDeserializer extends DCATAPDeserializer {

	private static final String DCATAP_IT_BASE_URI = "http://dati.gov.it/onto/dcatapit#";
	private static final Pattern datasetPattern = Pattern
			.compile("\\w*<(dcat|dcatapit):Dataset rdf:about=\\\"(.*)\\\"");
	private static Property startDateProp = ResourceFactory.createProperty(DCATAP_IT_BASE_URI + "startDate");
	private static Property endDateProp = ResourceFactory.createProperty(DCATAP_IT_BASE_URI + "endDate");

	public DCATAPITDeserializer() {
		super();
	}

	@Override
	public DCATDataset resourceToDataset(String nodeID, Resource datasetResource)
			throws DCATAPProfileNotValidException {

		DCATDataset mapped;
		// Properties to be mapped among different CKAN fallback fields

		String title = null, description = null, accessRights = null, frequency = null, landingPage = null,
				releaseDate = null, updateDate = null, identifier = null, type = null, version = null;

		List<DCTStandard> conformsTo = null;
		List<SKOSConceptTheme> theme = null;
		List<SKOSConceptSubject> subject = null;
		FOAFAgent publisher = null, rightsHolder = null, creator = null;
		List<VCardOrganization> contactPointList = null;
		DCTPeriodOfTime temporalCoverage = null;
		DCTLocation spatialCoverage = null;
		List<String> keywords = new ArrayList<String>(), documentation = new ArrayList<String>(),
				hasVersion = new ArrayList<String>(), isVersionOf = new ArrayList<String>(), language = null,
				provenance = new ArrayList<String>(), otherIdentifier = null, sample = new ArrayList<String>(),
				source = new ArrayList<String>(), versionNotes = new ArrayList<String>();

		List<DCATDistribution> distributionList = new ArrayList<DCATDistribution>();

		if (datasetResource.hasProperty(DCTerms.title))
			title = datasetResource.getRequiredProperty(DCTerms.title).getString();
		if (datasetResource.hasProperty(DCTerms.description))
			description = datasetResource.getRequiredProperty(DCTerms.description).getString();

		// Handle theme concepts
		theme = deserializeConcept(nodeID, datasetResource, DCAT.theme,SKOSConceptTheme.class);

		publisher = deserializeFOAFAgent(nodeID, datasetResource.getProperty(DCTerms.publisher));

		contactPointList = deserializeContactPoint(nodeID, datasetResource);

		// Iterate over keyword properties
		StmtIterator kIt = datasetResource.listProperties(DCAT.keyword);
		while (kIt.hasNext()) {
			keywords.add(kIt.next().getString());
		}

		conformsTo = deserializeDCTStandard(nodeID, datasetResource);

		if (datasetResource.hasProperty(DCTerms.accessRights))
			accessRights = datasetResource.getProperty(DCTerms.accessRights).getString();

		// Iterate over documentation properties
		StmtIterator dIt = datasetResource.listProperties(FOAF.page);
		while (dIt.hasNext()) {
			documentation.add(dIt.next().getString());
		}

		frequency = deserializeFrequency(datasetResource);

		// Iterate over hasVersion properties
		StmtIterator hasVIt = datasetResource.listProperties(DCTerms.hasVersion);
		while (hasVIt.hasNext()) {
			hasVersion.add(hasVIt.next().getString());
		}

		// Iterate over isVersionOf properties
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
		if (datasetResource.hasProperty(DCAT.landingPage)) {
			Resource landingR = datasetResource.getPropertyResourceValue(DCAT.landingPage);
			if (landingR != null && StringUtils.isNotBlank(landingPage = landingR.getURI())) {
			} else
				landingPage = datasetResource.getURI();
		} else
			landingPage = datasetResource.getURI();

		language = deserializeLanguage(datasetResource);

		// Iterate over provenance properties
		StmtIterator provIt = datasetResource.listProperties(DCTerms.provenance);
		while (provIt.hasNext()) {
			provenance.add(provIt.next().getString());
		}

		if (datasetResource.hasProperty(DCTerms.issued))
			releaseDate = extractDate(datasetResource.getProperty(DCTerms.issued));

		if (datasetResource.hasProperty(DCTerms.modified))
			updateDate = extractDate(datasetResource.getProperty(DCTerms.modified));

		if (datasetResource.hasProperty(DCTerms.identifier))
			identifier = datasetResource.getProperty(DCTerms.identifier).getString();

		// Iterate over otherIdentifier properties
		otherIdentifier = deserializeOtherIdentifier(datasetResource);

		// Iterate over sample properties
		StmtIterator sampleIt = datasetResource
				.listProperties(ResourceFactory.createProperty("http://www.w3.org/ns/adms#sample"));
		while (sampleIt.hasNext()) {
			sample.add(sampleIt.next().getString());
		}

		// Iterate over source properties
		StmtIterator sourceIt = datasetResource.listProperties(DCTerms.source);
		while (sourceIt.hasNext()) {
			source.add(sourceIt.next().getString());
		}

		// Handle spatial property
		spatialCoverage = deserializeSpatialCoverage(nodeID, datasetResource);

		// Handle temporal property
		temporalCoverage = deserializeTemporalCoverage(nodeID, datasetResource);

		if (datasetResource.hasProperty(DCTerms.type))
			type = datasetResource.getProperty(DCTerms.type).getString();

		if (datasetResource.hasProperty(OWL.versionInfo))
			version = datasetResource.getProperty(OWL.versionInfo).getString();

		// Iterate over versionNotes properties
		StmtIterator vNotesIt = datasetResource
				.listProperties(ResourceFactory.createProperty("http://www.w3.org/ns/adms#versionNotes"));
		while (vNotesIt.hasNext()) {
			versionNotes.add(vNotesIt.next().getString());
		}

		// Handle subject concepts
		subject = deserializeConcept(nodeID, datasetResource, DCTerms.subject,SKOSConceptSubject.class);

		// Handle RightsHolder
		if (datasetResource.hasProperty(DCTerms.rightsHolder))
			rightsHolder = deserializeFOAFAgent(nodeID, datasetResource.getProperty(DCTerms.rightsHolder));

		// Handle Creator
		if (datasetResource.hasProperty(DCTerms.creator))
			creator = deserializeFOAFAgent(nodeID, datasetResource.getProperty(DCTerms.creator));

		// Handle distributions
		StmtIterator distrIt = datasetResource.listProperties(DCAT.distribution);
		while (distrIt.hasNext()) {
			distributionList.add(resourceToDCATDistribution(distrIt.next().getResource(), nodeID));
		}

		mapped = new DCATDataset(nodeID, title, description, distributionList, theme, publisher, contactPointList,
				keywords, accessRights, conformsTo, documentation, frequency, hasVersion, isVersionOf, landingPage,
				language, provenance, releaseDate, updateDate, identifier, otherIdentifier, sample, source,
				spatialCoverage, temporalCoverage, type, version, versionNotes, rightsHolder, creator, subject);

		distributionList = null;
		contactPointList = null;
		publisher = null;
		conformsTo = null;
		spatialCoverage = null;
		temporalCoverage = null;
		keywords = null;
		theme = null;
		documentation = null;
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
	 * @param nodeID
	 * @param startDate
	 * @param endDate
	 * @param temporalCoverage
	 * @param datasetResource
	 * @return
	 */
	public DCTPeriodOfTime deserializeTemporalCoverage(String nodeID, Resource datasetResource) {
		DCATProperty startDate = null, endDate = null;
		Resource temporalResource = datasetResource.getPropertyResourceValue(DCTerms.temporal);

		if (temporalResource != null) {

			if (temporalResource.hasProperty(startDateProp))
				startDate = new DCATProperty(startDateProp.getURI(),
						temporalResource.getProperty(startDateProp).getString());

			if (temporalResource.hasProperty(endDateProp))
				endDate = new DCATProperty(endDateProp.getURI(), temporalResource.getProperty(endDateProp).getString());

			return new DCTPeriodOfTime(DCTerms.temporal.getURI(), startDate, endDate, nodeID);

		}

		return null;
	}

//	@Override
//	public Pattern getDatasetPattern() {
//		return datasetPattern;
//	}

}
