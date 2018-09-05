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
import org.apache.jena.rdf.model.RDFNode;
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

import it.eng.idra.beans.dcat.DCATAPFormat;
import it.eng.idra.beans.dcat.DCATAPProfileNotValidException;
import it.eng.idra.beans.dcat.DCATDataset;
import it.eng.idra.beans.dcat.DCATDistribution;
import it.eng.idra.beans.dcat.DCTLicenseDocument;
import it.eng.idra.beans.dcat.DCTLocation;
import it.eng.idra.beans.dcat.DCTPeriodOfTime;
import it.eng.idra.beans.dcat.DCTStandard;
import it.eng.idra.beans.dcat.FOAFAgent;
import it.eng.idra.beans.dcat.SKOSConcept;
import it.eng.idra.beans.dcat.SKOSConceptStatus;
import it.eng.idra.beans.dcat.SKOSConceptSubject;
import it.eng.idra.beans.dcat.SKOSConceptTheme;
import it.eng.idra.beans.dcat.SKOSPrefLabel;
import it.eng.idra.beans.dcat.SPDXChecksum;
import it.eng.idra.beans.dcat.VCardOrganization;
import it.eng.idra.beans.odms.ODMSCatalogue;
import it.eng.idra.utils.CommonUtil;

public class DCATAPDeserializer implements IDCATAPDeserialize {

	private static final Pattern rdfDatasetPattern = Pattern.compile("\\w*<dcat:Dataset rdf:about=\\\"(.*)\\\"");
	private static final Pattern turtleDatasetPattern = Pattern.compile("<(.*)>\\R\\s*a dcat:Dataset");

	private static final String GEO_BASE_URI = "http://publications.europa.eu/mdr/authority/place";
	private static final String GEO_BASE_URI_ALT = "http://www.geonames.org";

	public DCATAPDeserializer() {
	}

	public Model dumpToModel(String modelText, ODMSCatalogue node) throws RiotException {

		String nodeBaseURI = node.getHost();
		// create an empty model
		Model model = ModelFactory.createDefaultModel();
		for (DCATAPFormat format : DCATAPFormat.values()) {
			try {
				model.read(new ByteArrayInputStream(modelText.getBytes(StandardCharsets.UTF_8)), nodeBaseURI,
						format.formatName());
				node.setDcatFormat(format);
				break;
			} catch (RiotException e) {
				if (!e.getMessage().contains("Content is not allowed in prolog") && !e.getMessage()
						.contains("[line: 1, col: 1 ] Expected BNode or IRI: Got: [DIRECTIVE:prefix]"))
					throw e;
				else
					continue;
			}
		}
		return model;
	}

	public DCATDataset resourceToDataset(String nodeID, Resource datasetResource)
			throws DCATAPProfileNotValidException {

		DCATDataset mapped;
		// Properties to be mapped among different CKAN fallback fields

		String title = null, description = null, accessRights = null, frequency = null, landingPage = null,
				releaseDate = null, updateDate = null, identifier = null, type = null, version = null;

		List<DCTStandard> conformsTo = null;
		List<SKOSConceptTheme> theme = new ArrayList<SKOSConceptTheme>();
		List<SKOSConceptSubject> subject = new ArrayList<SKOSConceptSubject>();
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

		theme = deserializeConcept(nodeID, datasetResource, DCAT.theme, SKOSConceptTheme.class);

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
			Statement sourceStm = sourceIt.next();
			try {
				source.add(sourceStm.getString());
			} catch (LiteralRequiredException e) {
				source.add(sourceStm.getResource().getURI());
			}
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
	 * @param datasetResource
	 * @return
	 */
	protected String extractDate(Statement dateStatement) {
		try {
			return CommonUtil.fromLocalToUtcDate(CommonUtil.fixBadUTCDate(dateStatement.getString()), null);
		} catch (IllegalArgumentException ignore) {
			return null;
		}

	}

	/**
	 *
	 * @param parentResource
	 * @return List<SKOSConcept>
	 */

	public <T extends SKOSConcept> List<T> deserializeConcept(String nodeID, Resource parentResource,
			Property toExtractP, Class<T> type) {
		List<T> conceptList = new ArrayList<T>();

		Resource conceptR = null;

		// Iterate over concept properties
		StmtIterator conceptIt = parentResource.listProperties(toExtractP);
		while (conceptIt.hasNext()) {

			List<SKOSPrefLabel> labelList = null;
			String conceptURI = null;

			conceptR = conceptIt.next().getResource();
			if (conceptR != null && StringUtils.isNotBlank(conceptURI = conceptR.getURI())) {

				if (conceptR.hasProperty(SKOS.prefLabel)) {

					labelList = new ArrayList<SKOSPrefLabel>();
					StmtIterator labelIt = conceptR.listProperties(SKOS.prefLabel);
					while (labelIt.hasNext()) {
						Statement labelS = labelIt.next();
						labelList.add(new SKOSPrefLabel(labelS.getLanguage(), labelS.getString(), nodeID));
					}
				}

				try {
					conceptList.add(type.getDeclaredConstructor(SKOSConcept.class)
							.newInstance(new SKOSConcept(toExtractP.getURI(), conceptURI, labelList, nodeID)));
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// if (!IRIFactory.iriImplementation().create(conceptURI).hasViolation(false))
				// conceptList.add(extractThemeFromURI(conceptURI));
				// else
				// conceptList.add(conceptURI);
			}
		}
		return conceptList;
	}

	/**
	 *
	 * @param datasetResource
	 * @return
	 */
	public List<String> deserializeLanguage(Resource datasetResource) {

		List<String> language = new ArrayList<String>();

		// Iterate over language properties
		StmtIterator it = datasetResource.listProperties(DCTerms.language);
		while (it.hasNext()) {
			Resource languageR = it.next().getResource();
			String languageURI = null;
			if (languageR != null && StringUtils.isNotBlank(languageURI = languageR.getURI())) {
				if (!IRIFactory.iriImplementation().create(languageURI).hasViolation(false))
					language.add(extractLanguageFromURI(languageURI));
				else
					language.add(languageURI);
			}
		}
		return language;
	}

	/**
	 * @param nodeID
	 * @param datasetResource
	 * @return
	 */
	public DCTPeriodOfTime deserializeTemporalCoverage(String nodeID, Resource datasetResource) {

		String startDate = null, endDate = null;
		Resource temporalResource = datasetResource.getPropertyResourceValue(DCTerms.temporal);

		if (temporalResource != null) {

			if (temporalResource.hasProperty(ResourceFactory.createProperty("http://schema.org#startDate")))
				startDate = temporalResource.getProperty(ResourceFactory.createProperty("http://schema.org#startDate"))
						.getString();

			if (temporalResource.hasProperty(ResourceFactory.createProperty("http://schema.org#endDate")))
				endDate = temporalResource.getProperty(ResourceFactory.createProperty("http://schema.org#endDate"))
						.getString();

			return new DCTPeriodOfTime(DCTerms.temporal.getURI(), startDate, endDate, nodeID);

		}

		return null;
	}

	/**
	 * @param nodeID
	 * @param geographicalIdentifier
	 * @param geographicalName
	 * @param geometry
	 * @param datasetResource
	 * @return
	 */
	public DCTLocation deserializeSpatialCoverage(String nodeID, Resource datasetResource) {

		// StmtIterator spatialIt = datasetResource.listProperties(DCTerms.spatial);
		// while (spatialIt.hasNext()) {
		// Resource spatialResource = (Resource) spatialIt.next().getResource();

		String geographicalIdentifier = null, geographicalName = null, geometry = null, spatialResourceURI = null;
		Resource spatialResource = datasetResource.getPropertyResourceValue(DCTerms.spatial);

		if (spatialResource != null) {

			if (spatialResource.hasProperty(
					ResourceFactory.createProperty("http://dati.gov.it/onto/dcatapit#geographicalIdentifier")))
				geographicalIdentifier = spatialResource.getProperty(
						ResourceFactory.createProperty("http://dati.gov.it/onto/dcatapit#geographicalIdentifier"))
						.getString();
			if (spatialResource.hasProperty(ResourceFactory.createProperty("http://www.w3.org/ns/locn#geometry")))
				geometry = spatialResource
						.getProperty(ResourceFactory.createProperty("http://www.w3.org/ns/locn#geometry")).getString();

			if (spatialResource
					.hasProperty(ResourceFactory.createProperty("http://www.w3.org/ns/locn#geographicalName")))
				geographicalName = spatialResource.getPropertyResourceValue(
						ResourceFactory.createProperty("http://www.w3.org/ns/locn#geographicalName")).getURI();

			// Handle geographical Identifier as resource URI
			if (StringUtils.isBlank(geographicalIdentifier)
					&& StringUtils.isNotBlank(spatialResourceURI = spatialResource.getURI()))
				geographicalIdentifier = (spatialResourceURI.startsWith(GEO_BASE_URI)
						|| spatialResourceURI.startsWith(GEO_BASE_URI_ALT)) ? spatialResourceURI : "";

			return new DCTLocation(DCTerms.spatial.getURI(), geographicalIdentifier, geographicalName, geometry,
					nodeID);
		}

		return null;
	}

	/**
	 * @param otherIdentifier
	 * @param datasetResource
	 * @return
	 */
	public List<String> deserializeOtherIdentifier(Resource datasetResource) {

		List<String> otherIdentifier = new ArrayList<String>();
		StmtIterator othIdIt = datasetResource
				.listProperties(ResourceFactory.createProperty("http://www.w3.org/ns/adms#identifier"));
		while (othIdIt.hasNext()) {
			Statement st = othIdIt.next();

			try {
				if (st.getString() != null)
					otherIdentifier.add(st.getString());

			} catch (LiteralRequiredException e) {
				Resource othIdResource = st.getResource();
				if (othIdResource != null) {
					if (othIdResource.hasProperty(SKOS.notation))
						otherIdentifier.add(othIdResource.getProperty(SKOS.notation).getString());
				}
			}

		}
		return otherIdentifier;
	}

	/**
	 * @param nodeID
	 * @param datasetResource
	 * @return List<DCTStandard>
	 */
	public List<DCTStandard> deserializeDCTStandard(String nodeID, Resource datasetResource) {

		List<DCTStandard> standardList = new ArrayList<DCTStandard>();

		// Iterate over conformsTo/linked Schemas properties
		StmtIterator cit = datasetResource.listProperties(DCTerms.conformsTo);
		Property referenceProperty = ResourceFactory
				.createProperty(DCATAPSerializer.DCATAP_IT_BASE_URI + "referenceDocumentation");

		while (cit.hasNext()) {

			String uri = null, identifier = null, toTitle = null, toDescription = null;
			List<String> toReference = new ArrayList<String>();

			Resource standardResource = cit.next().getResource();
			uri = standardResource.getURI();
			if (standardResource.hasProperty(DCTerms.identifier))
				identifier = standardResource.getProperty(DCTerms.identifier).getString();
			if (standardResource.hasProperty(DCTerms.title))
				toTitle = standardResource.getProperty(DCTerms.title).getString();
			if (standardResource.hasProperty(DCTerms.description))
				toDescription = standardResource.getProperty(DCTerms.description).getString();

			StmtIterator referenceIt = standardResource.listProperties(referenceProperty);
			while (referenceIt.hasNext()) {

				toReference.add(referenceIt.next().getString());
			}

			standardList.add(new DCTStandard(uri, identifier, toTitle, toDescription, toReference, nodeID));
		}
		return standardList;
	}

	/**
	 * @param nodeID
	 * @param vCardFn
	 * @param vCardHasEmail
	 * @param vCardHasURL
	 * @param vCardHasTelephone
	 * @param contactPointList
	 * @param datasetResource
	 * @return
	 */
	public List<VCardOrganization> deserializeContactPoint(String nodeID, Resource datasetResource) {

		List<VCardOrganization> contactPointList = new ArrayList<VCardOrganization>();

		// Iterate over contact points
		StmtIterator cIt = datasetResource.listProperties(DCAT.contactPoint);
		while (cIt.hasNext()) {

			String vCardUri = null, vCardFn = null, vCardHasEmail = null, vCardHasURL = null,
					vCardHasTelephoneValue = null, vCardHasTelephoneType = null;

			Resource contactResource = cIt.next().getResource();
			if (contactResource != null) {

				vCardUri = contactResource.getURI();
				if (contactResource.hasProperty(VCARD4.fn))
					vCardFn = contactResource.getProperty(VCARD4.fn).getString();
				try {
					if (contactResource.hasProperty(VCARD4.hasEmail)) {
						vCardHasEmail = contactResource.getProperty(VCARD4.hasEmail).getResource().getURI();
					}

				} catch (ResourceRequiredException e) {
					vCardHasEmail = contactResource.getProperty(VCARD4.hasEmail).getString();
				}

				if (contactResource.hasProperty(VCARD4.hasURL))
					try {
						vCardHasURL = contactResource.getProperty(VCARD4.hasURL).getString();
					} catch (LiteralRequiredException e) {
						vCardHasURL = contactResource.getProperty(VCARD4.hasURL).getResource().getURI();
					}

				if (contactResource.hasProperty(VCARD4.hasTelephone))
					try {
						vCardHasTelephoneValue = contactResource.getProperty(VCARD4.hasTelephone).getString();
					} catch (LiteralRequiredException e) {
						Resource hasTelephoneR = contactResource.getProperty(VCARD4.hasTelephone).getResource();
						if (hasTelephoneR != null) {
							if (hasTelephoneR.hasProperty(VCARD4.value))
								vCardHasTelephoneValue = hasTelephoneR.getProperty(VCARD4.value).getString();
							if (hasTelephoneR.hasProperty(RDF.type))
								vCardHasTelephoneType = hasTelephoneR.getPropertyResourceValue(RDF.type).getURI();
						}
					}

				contactPointList.add(new VCardOrganization(DCAT.contactPoint.getURI(), vCardUri, vCardFn, vCardHasEmail,
						vCardHasURL, vCardHasTelephoneValue, vCardHasTelephoneType, nodeID));
			}
		}
		return contactPointList;
	}

	/**
	 * @param nodeID
	 * @param agentIdentifier
	 * @param agentName
	 * @param agentMbox
	 * @param agentHomepage
	 * @param agentType
	 * @param agent
	 * @param agentResource
	 * @return
	 */
	public FOAFAgent deserializeFOAFAgent(String nodeID, Statement agentStatement) {

		String agentIdentifier = null, agentUri = null, agentName = null, agentMbox = null, agentHomepage = null,
				agentType = null;
		Resource agentResource = null;

		if (agentStatement != null && (agentResource = agentStatement.getResource()) != null) {

			agentUri = agentResource.getURI();
			if (agentResource.hasProperty(FOAF.name))
				agentName = agentResource.getProperty(FOAF.name).getString();
			if (agentResource.hasProperty(FOAF.mbox))
				agentMbox = agentResource.getProperty(FOAF.mbox).getString();

			if (agentResource.hasProperty(FOAF.homepage)) {
				Resource homepageR = agentResource.getPropertyResourceValue(FOAF.homepage);
				if (homepageR != null)
					agentHomepage = homepageR.getURI();
				else
					agentHomepage = agentResource.getProperty(FOAF.homepage).getString();
			}
			if (agentResource.hasProperty(DCTerms.type))
				agentType = agentResource.getProperty(DCTerms.type).getString();
			if (agentResource.hasProperty(DCTerms.identifier))
				agentIdentifier = agentResource.getProperty(DCTerms.identifier).getString();

			return new FOAFAgent(agentStatement.getPredicate().getURI(), agentUri, agentName, agentMbox, agentHomepage,
					agentType, agentIdentifier, nodeID);

		}
		return null;
	}

	/**
	 * @param frequency
	 * @param datasetResource
	 * @return
	 */
	public String deserializeFrequency(Resource datasetResource) {
		String frequencyURI = null;
		if (datasetResource.hasProperty(DCTerms.accrualPeriodicity)) {
			Resource frequencyR = datasetResource.getPropertyResourceValue(DCTerms.accrualPeriodicity);

			if (frequencyR != null && StringUtils.isNotBlank(frequencyURI = frequencyR.getURI())) {
				if (!IRIFactory.iriImplementation().create(frequencyURI).hasViolation(false))
					return CommonUtil.extractFrequencyFromURI(frequencyURI);
				else
					return frequencyURI;
			}
		}
		return null;
	}

	public DCATDistribution resourceToDCATDistribution(Resource r, String nodeID) {

		String accessURL = null, description = null, format = null, byteSize = null, documentation = null,
				downloadURL = null, language = null, mediaType = null, releaseDate = null, updateDate = null,
				rights = null, title = null;

		SPDXChecksum checksum = null;
		DCTLicenseDocument license = null;
		String licenseURI = null, licenseName = null, licenseVersion = null, licenseType = null;
		SKOSConceptStatus status = null;
		List<DCTStandard> linkedSchemas = null;

		// Manage required accessURL property
		if (r.hasProperty(DCAT.accessURL)) {
			Resource accessR = r.getPropertyResourceValue(DCAT.accessURL);
			if (accessR != null && StringUtils.isNotBlank(accessURL = accessR.getURI())) {
			} else
				throw new PropertyNotFoundException(DCAT.accessURL);
		}

		if (r.hasProperty(DCTerms.description))
			description = r.getProperty(DCTerms.description).getString();

		if (r.hasProperty(DCTerms.format)) {
			format = deserializeFormat(r);
		}

		if (r.hasProperty(DCTerms.license)) {
			Resource licenseR = r.getPropertyResourceValue(DCTerms.license);

			licenseURI = licenseR.getURI();
			if (licenseR.hasProperty(FOAF.name))
				licenseName = licenseR.getProperty(FOAF.name).getString();
			if (licenseR.hasProperty(DCTerms.type))
				licenseType = licenseR.getPropertyResourceValue(DCTerms.type).getURI();
			if (licenseR.hasProperty(OWL.versionInfo))
				licenseVersion = licenseR.getProperty(OWL.versionInfo).getString();

			license = new DCTLicenseDocument(licenseURI, licenseName, licenseType, licenseVersion, nodeID);
		}

		if (r.hasProperty(DCAT.byteSize))
			byteSize = r.getProperty(DCAT.byteSize).getString();

		if (r.hasProperty(ResourceFactory.createProperty("http://spdx.org/rdf/terms#checksum"))) {

			checksum = deserializeChecksum(nodeID, r);
		}

		if (r.hasProperty(FOAF.page))
			documentation = r.getProperty(FOAF.page).getString();
		// Manage downloadURL property
		if (r.hasProperty(DCAT.downloadURL)) {
			Resource downloadR = r.getPropertyResourceValue(DCAT.downloadURL);
			if (downloadR != null)
				downloadURL = downloadR.getURI();
		}

		if (r.hasProperty(DCTerms.language))
			try {
				language = r.getPropertyResourceValue(DCTerms.language).getURI();
			} catch (Exception ignore) {
			}

		linkedSchemas = deserializeDCTStandard(nodeID, r);

		if (r.hasProperty(DCAT.mediaType))
			mediaType = r.getProperty(DCAT.mediaType).getString();

		if (r.hasProperty(DCTerms.issued))
			releaseDate = extractDate(r.getProperty(DCTerms.issued));
		if (r.hasProperty(DCTerms.modified))
			updateDate = extractDate(r.getProperty(DCTerms.modified));
		if (r.hasProperty(DCTerms.rights))
			rights = r.getProperty(DCTerms.rights).getString();

		try {
			status = deserializeConcept(nodeID, r, ResourceFactory.createProperty("http://www.w3.org/ns/adms#status"),
					SKOSConceptStatus.class).get(0);
		} catch (IndexOutOfBoundsException ignore) {
		}

		if (r.hasProperty(DCTerms.title))
			title = r.getProperty(DCTerms.title).getString();

		if (StringUtils.isBlank(downloadURL))
			downloadURL = accessURL;

		return new DCATDistribution(nodeID, accessURL, description, format, license, byteSize, checksum,
				Arrays.asList(documentation), downloadURL, Arrays.asList(language), linkedSchemas, mediaType,
				releaseDate, updateDate, rights, status, title);

	}

	/**
	 * @param r
	 * @param nodeID
	 * @param checksumValue
	 * @param checksumAlgorithm
	 * @return
	 */
	public SPDXChecksum deserializeChecksum(String nodeID, Resource r) {
		String checksumValue = null, checksumAlgorithm = null;

		Resource checksumR = r
				.getPropertyResourceValue(ResourceFactory.createProperty("http://spdx.org/rdf/terms#checksum"));
		if (checksumR.hasProperty(ResourceFactory.createProperty("http://spdx.org/rdf/terms#algorithm")))
			checksumAlgorithm = checksumR
					.getProperty(ResourceFactory.createProperty("http://spdx.org/rdf/terms#algorithm")).getString();
		if (checksumR.hasProperty(ResourceFactory.createProperty("http://spdx.org/rdf/terms#checksumValue")))
			checksumValue = checksumR
					.getProperty(ResourceFactory.createProperty("http://spdx.org/rdf/terms#checksumValue")).getString();

		return new SPDXChecksum("http://spdx.org/rdf/terms#checksum", checksumAlgorithm, checksumValue, nodeID);

	}

	/**
	 * @param r
	 * @param format
	 * @return
	 */
	public String deserializeFormat(Resource r) {

		Resource formatR = r.getPropertyResourceValue(DCTerms.format);
		String formatURI = null, format = null;
		if (formatR != null && StringUtils.isNotBlank(formatURI = formatR.getURI())) {
			if (!IRIFactory.iriImplementation().create(formatURI).hasViolation(false))
				format = extractFormatFromURI(formatURI);
			else
				format = formatURI;

		}
		return format;
	}

	public String extractFormatFromURI(String uri) {

		Matcher matcher = Pattern
				.compile("http:\\/\\/publications\\.europa\\.eu\\/resource\\/authority\\/file-type(\\/|#)(\\w*)")
				.matcher(uri);
		String result = null;

		return (matcher.find() && (result = matcher.group(2)) != null) ? result : "";

	}

	public String extractThemeFromURI(String uri) {

		Matcher matcher = Pattern
				.compile("http:\\/\\/publications\\.europa\\.eu\\/resource\\/authority\\/data-theme(\\/|#)(\\w*)")
				.matcher(uri);
		String result = null;

		return (matcher.find() && (result = matcher.group(2)) != null) ? result : "";

	}

	public String extractLanguageFromURI(String uri) {

		Matcher matcher = Pattern
				.compile("http:\\/\\/publications\\.europa\\.eu\\/(mdr|resource)\\/authority\\/language(\\/|#)(\\w*)")
				.matcher(uri);
		String result = null;

		return (matcher.find() && (result = matcher.group(3)) != null) ? result : "";

	}

	public Pattern getDatasetPattern(DCATAPFormat format) {

		switch (format) {

		case TURTLE:
			return turtleDatasetPattern;
		default:
			return rdfDatasetPattern;
		}
	}

}
