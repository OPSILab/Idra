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
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.ResourceRequiredException;
import org.apache.jena.riot.system.IRIResolver;
import org.apache.jena.shared.BadURIException;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;
import org.apache.jena.vocabulary.VCARD4;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.formula.ptg.AddPtg;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;

import it.eng.idra.beans.IdraProperty;
import it.eng.idra.beans.dcat.DCATAPFormat;
import it.eng.idra.beans.dcat.DCATAPProfile;
import it.eng.idra.beans.dcat.DCATAPWriteType;
import it.eng.idra.beans.dcat.DCATDataset;
import it.eng.idra.beans.dcat.DCATDistribution;
import it.eng.idra.beans.dcat.DCATProperty;
import it.eng.idra.beans.dcat.DCTFrequency;
import it.eng.idra.beans.dcat.DCTLicenseDocument;
import it.eng.idra.beans.dcat.DCTLocation;
import it.eng.idra.beans.dcat.DCTPeriodOfTime;
import it.eng.idra.beans.dcat.DCTStandard;
import it.eng.idra.beans.dcat.FOAFAgent;
import it.eng.idra.beans.dcat.SKOSConcept;
import it.eng.idra.beans.dcat.SKOSPrefLabel;
import it.eng.idra.beans.dcat.SPDXChecksum;
import it.eng.idra.beans.dcat.VCardOrganization;
import it.eng.idra.beans.odms.ODMSCatalogue;
import it.eng.idra.beans.search.SearchResult;
import it.eng.idra.management.FederationCore;
import it.eng.idra.utils.CommonUtil;
import it.eng.idra.utils.PropertyManager;

/* TODO Pensare ad un approccio con Interfaccia DCAPAPSerializer e
 *  Classi che la implementano a seconda del Profilo
 */
public class DCATAPSerializer {

	static Map<Integer, ODMSCatalogue> nodeResources = null;

	@SuppressWarnings("deprecation")
	protected static final IRIFactory iriFactory = IRIFactory.jenaImplementation();

	public static final String DCATAP_IT_BASE_URI = "http://dati.gov.it/onto/dcatapit#";
	public static final String THEME_BASE_URI = "http://publications.europa.eu/resource/authority/data-theme/";
	public static final String SUBJECT_BASE_URI = "http://eurovoc.europa.eu/";
	public static final String FREQUENCY_BASE_URI = "http://publications.europa.eu/resource/authority/frequency/";
	public static final String LANGUAGE_BASE_URI = "http://publications.europa.eu/mdr/authority/language/";
	public static final String GEO_BASE_URI = "http://publications.europa.eu/mdr/authority/place/";
	public static final String GEO_BASE_URI_ALT = "http://geonames.org/";
	public static final String FORMAT_BASE_URI = "http://publications.europa.eu/mdr/authority/file-type/";
	public static final String LICENSE_TYPE_BASE_URI = "http://purl.org/adms/licencetype/";

	protected static Logger logger = LogManager.getLogger(DCATAPSerializer.class);
	private static String filePath = PropertyManager.getProperty(IdraProperty.DUMP_FILE_PATH);
	private static String fileName = PropertyManager.getProperty(IdraProperty.DUMP_FILE_NAME);

	static {

	}

	public DCATAPSerializer() {
	}

	private static Model datasetToModel(DCATDataset dataset, DCATAPProfile profile) {

		Model model = initializeModel();

		try {
			switch (profile) {

			case DCATAP_IT:
				model.setNsPrefix("dcatapit", "http://dati.gov.it/onto/dcatapit#");
				model = DCATAPITSerializer.addDatasetToModel(dataset, model);
				break;

			/*
			 * ADD HERE MORE CASES FOR FURTHER PROFILES
			 */

			default:
				model = addDatasetToModel(dataset, model);
				break;
			}

		} catch (Exception e) {
			logger.error("Error while converting dataset " + dataset.getId() + " to model, reason: " + e.getMessage());
		}

		return model;
	}

	/**
	 * @return Model
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
		return model;
	}

	private static Model datasetsToModel(List<DCATDataset> datasets, DCATAPProfile profile) {

		Model model = initializeModel();

		for (DCATDataset d : datasets) {
			try {
				switch (profile) {

				case DCATAP_IT:
					model.setNsPrefix("dcatapit", "http://dati.gov.it/onto/dcatapit#");

					model = DCATAPITSerializer.addDatasetToModel(d, model);
					break;

				/*
				 * ADD HERE MORE CASES FOR FURTHER PROFILES
				 */

				default:
					model = addDatasetToModel(d, model);
					break;
				}

			} catch (Exception e) {
				logger.error("Error while converting dataset " + d.getId() + " to model, reason: " + e.getMessage());
			}
		}

		return model;

	}

	private static Model addDatasetToModel(DCATDataset dataset, Model model) {

		String landingPage = dataset.getLandingPage().getValue();
		IRI iri = iriFactory.create(landingPage);
		if (iri.hasViolation(false))
			throw new BadURIException(
					"URI for dataset: " + iri + "is not valid, skipping the dataset in the Jena Model:"
							+ (iri.violations(false).next()).getShortMessage());

		Resource datasetResource = model.createResource(iri.toString(), DCAT.Dataset);

		addDCATPropertyAsLiteral(dataset.getTitle(), datasetResource, model);

		addDCATPropertyAsLiteral(dataset.getDescription(), datasetResource, model);

		serializeConcept(dataset.getTheme(), model, datasetResource);

		serializeContactPoint(dataset.getContactPoint(), model, datasetResource);

		dataset.getKeywords().stream().filter(keyword -> StringUtils.isNotBlank(keyword))
				.forEach(keyword -> datasetResource.addLiteral(DCAT.keyword, keyword));

		addDCATPropertyAsLiteral(dataset.getAccessRights(), datasetResource, model);

		serializeDCTStandard(dataset.getConformsTo(), datasetResource, model);

		List<DCATProperty> documentationList = dataset.getDocumentation();
		if (documentationList != null)
			documentationList.stream().filter(item -> !isValidURI(item.getValue()))
					.forEach(item -> addDCATPropertyAsResource(item, datasetResource, model, true));

		List<DCATProperty> relatedResourceList = dataset.getRelatedResource();
		if (relatedResourceList != null)
			relatedResourceList.stream().filter(item -> !isValidURI(item.getValue()))
					.forEach(item -> addDCATPropertyAsResource(item, datasetResource, model, true));

		serializeFrequency(dataset.getFrequency(), model, datasetResource);

		List<DCATProperty> hasVersion = dataset.getHasVersion();
		if (hasVersion != null)
			hasVersion.stream().filter(item -> !isValidURI(item.getValue()))
					.forEach(item -> addDCATPropertyAsResource(item, datasetResource, model, true));

		List<DCATProperty> isVersionOf = dataset.getIsVersionOf();
		if (isVersionOf != null)
			isVersionOf.stream().filter(item -> !isValidURI(item.getValue()))
					.forEach(item -> addDCATPropertyAsResource(item, datasetResource, model, true));

		addDCATPropertyAsResource(dataset.getLandingPage(), datasetResource, model, false);

		serializeLanguage(dataset.getLanguage(), model, datasetResource);

		List<DCATProperty> provenance = dataset.getProvenance();
		if (provenance != null)
			provenance.stream().forEach(item -> addDCATPropertyAsLiteral(item, datasetResource, model));

		addDCATPropertyAsTypedLiteral(dataset.getReleaseDate(), XSDDateType.XSDdateTime, datasetResource, model);
		addDCATPropertyAsTypedLiteral(dataset.getUpdateDate(), XSDDateType.XSDdateTime, datasetResource, model);

		addDCATPropertyAsLiteral(dataset.getIdentifier(), datasetResource, model);

		List<DCATProperty> otherIdentifier = dataset.getOtherIdentifier();
		if (otherIdentifier != null)
			otherIdentifier.stream().filter(id -> StringUtils.isNotBlank(id.getValue())).forEach(id -> {
				datasetResource.addProperty(model.createProperty(id.getProperty().getURI()),
						model.createResource().addLiteral(SKOS.notation, id.getValue()));
			});

		List<DCATProperty> sample = dataset.getSample();
		if (sample != null)
			sample.stream().filter(item -> isValidURI(item.getValue()))
					.forEach(item -> addDCATPropertyAsResource(item, datasetResource, model, true));

		List<DCATProperty> source = dataset.getSource();
		if (source != null)
			source.stream().filter(item -> isValidURI(item.getValue()))
					.forEach(item -> addDCATPropertyAsResource(item, datasetResource, model, true));

		serializeSpatialCoverage(dataset.getSpatialCoverage(), model, datasetResource);

		serializeTemporalCoverage(dataset.getTemporalCoverage(), model, datasetResource);

		addDCATPropertyAsLiteral(dataset.getType(), datasetResource, model);
		addDCATPropertyAsLiteral(dataset.getVersion(), datasetResource, model);

		List<DCATProperty> versionNotes = dataset.getVersionNotes();
		if (versionNotes != null)
			versionNotes.stream().forEach(item -> addDCATPropertyAsLiteral(item, datasetResource, model));

		for (DCATDistribution distribution : dataset.getDistributions()) {
			addDistributionToModel(model, datasetResource, distribution);
		}

		ODMSCatalogue node = nodeResources.get(new Integer(dataset.getNodeID()));

		/*
		 * Add the Catalogue with the Dataset to the global Model
		 */
		model.createResource(node.getHost(), DCAT.Catalog).addLiteral(DCTerms.title, node.getName())
				.addLiteral(DCTerms.description, node.getDescription())// .addProperty(DCTerms.publisher, publisherNode)
				.addProperty(DCAT.dataset, datasetResource)
				.addProperty(DCTerms.issued, node.getRegisterDate().toString(), XSDDateType.XSDdateTime)
				.addProperty(DCTerms.modified, node.getLastUpdateDate().toString(), XSDDateType.XSDdateTime);

		/*
		 * ** Create the Publisher for the DCATCatalogue as a new FOAFAgent with info
		 * from the federated Catalogue
		 */

		String publisherResourceUri = node.getPublisherUrl();
		if (StringUtils.isBlank(publisherResourceUri) || !isValidURI(publisherResourceUri)) {

			if (!node.getHomepage().equalsIgnoreCase(node.getHost()))
				publisherResourceUri = node.getHomepage();
			else
				publisherResourceUri = node.getHomepage() + "/" + node.getId();
		}

		/*
		 * Check if the publisher is already present in the global Model and if any
		 * create it, either or both for dataset and catalog
		 */

		serializeFOAFAgent(
				new FOAFAgent(DCTerms.publisher.getURI(), publisherResourceUri, node.getPublisherName(),
						node.getPublisherEmail(), node.getPublisherUrl(), "", "", String.valueOf(node.getId())),
				model, model.getResource(node.getHost()));

		FOAFAgent datasetPublisher = dataset.getPublisher();
		if(datasetPublisher!=null) {
			if (StringUtils.isNotBlank(datasetPublisher.getResourceUri()) && isValidURI(datasetPublisher.getResourceUri()))
				serializeFOAFAgent(datasetPublisher, model, datasetResource);
			else {
				// Set blank URI for Dataset Publisher in order to create a blank node
				datasetPublisher.setResourceUri("");
				serializeFOAFAgent(datasetPublisher, model, datasetResource);
			}
		}

		return model;

	}

	/**
	 * @param temporalCoverage
	 * @param model
	 * @param datasetResource
	 */
	protected static void serializeTemporalCoverage(DCTPeriodOfTime temporalCoverage, Model model,
			Resource datasetResource) {
		if (temporalCoverage != null)
			datasetResource.addProperty(model.createProperty(temporalCoverage.getUri()),
					model.createResource(DCTPeriodOfTime.getRDFClass())
							.addProperty(temporalCoverage.getStartDate().getProperty(),
									temporalCoverage.getStartDate().getValue(), XSDDateType.XSDdate)
							.addProperty(temporalCoverage.getEndDate().getProperty(),
									temporalCoverage.getEndDate().getValue(), XSDDateType.XSDdate));
	}

	/**
	 * @param spatialCoverage
	 * @param model
	 * @param parentResource
	 */
	protected static void serializeSpatialCoverage(DCTLocation spatialCoverage, Model model, Resource parentResource) {

		if (spatialCoverage != null) {

			Resource spatialResource = model.createResource(DCTLocation.getRDFClass());
			String geoURI = null;
			// Initialize spatial Resource

			if (StringUtils.isNotBlank(geoURI = spatialCoverage.getGeographicalIdentifier().getValue())) {

				if (IRIFactory.iriImplementation().create(geoURI).hasViolation(false))
					spatialCoverage.getGeographicalIdentifier().setValue(GEO_BASE_URI + geoURI);

				addDCATPropertyAsLiteral(spatialCoverage.getGeographicalIdentifier(), spatialResource, model);
			}

			addDCATPropertyAsLiteral(spatialCoverage.getGeographicalIdentifier(), spatialResource, model);
			addDCATPropertyAsLiteral(spatialCoverage.getGeometry(), spatialResource, model);

			// TODO Geographical Name as SKOS CONCEPT
			addDCATPropertyAsResource(spatialCoverage.getGeographicalName(), spatialResource, model, false);

			parentResource.addProperty(model.createProperty(spatialCoverage.getUri()), spatialResource);

		}

	}

	/**
	 * @param language
	 * @param model
	 * @param datasetResource
	 */
	protected static void serializeLanguage(List<DCATProperty> language, Model model, Resource datasetResource) {

		if (language != null)
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

	/**
	 * @param dataset
	 * @param model
	 * @param datasetResource
	 */
	protected static void serializeFrequency(DCATProperty frequency, Model model, Resource datasetResource) {

		try {
			// Check if the value is a valid Frequency enum value
			DCTFrequency.valueOf(frequency.getValue());
			datasetResource.addProperty(frequency.getProperty(),
					model.createResource(FREQUENCY_BASE_URI + frequency.getValue()));

		} catch (Exception e) {

			datasetResource.addProperty(frequency.getProperty(),
					model.createResource(FREQUENCY_BASE_URI + DCTFrequency.UNKNOWN));
		}
	}

	/**
	 * @param dataset
	 * @param model
	 * @param datasetResource
	 */
	private static void serializeContactPoint(List<VCardOrganization> contactPointList, Model model,
			Resource datasetResource) {

		if (contactPointList != null && !contactPointList.isEmpty())
			for (VCardOrganization contactPoint : contactPointList)
				try {
					Resource contactPointR = null;

					if (StringUtils.isNotBlank(contactPoint.getResourceUri()))
						contactPointR = model.createResource(contactPoint.getResourceUri(),
								VCardOrganization.getRDFClass());
					else
						contactPointR = model.createResource(VCardOrganization.getRDFClass());

					// .addProperty(RDF.type, VCARD4.Kind)
					contactPointR.addLiteral(contactPoint.getFn().getProperty(), contactPoint.getFn().getValue());

					// Fix hasEmail value if needed
					String hasEmailValue = contactPoint.getHasEmail().getValue();
					if (StringUtils.isNotBlank(hasEmailValue) && CommonUtil.checkIfIsEmail(hasEmailValue)) {
						if (!hasEmailValue.startsWith("mailto:"))
							contactPoint.getHasEmail().setValue("mailto:" + hasEmailValue);
						addDCATPropertyAsResource(contactPoint.getHasEmail(), contactPointR, model, false);
					}

					addDCATPropertyAsResource(contactPoint.getHasURL(), contactPointR, model, false);

					// Add contactPoint property to dataset Resource;
					datasetResource.addProperty(model.createProperty(contactPoint.getPropertyUri()), contactPointR);

				} catch (Exception ignore) {
				}

	}

	/**
	 * @param model
	 * @param parentResource
	 * @param agent
	 */
	protected static void serializeFOAFAgent(FOAFAgent agent, Model model, Resource parentResource) {

		if (agent != null) {
			Resource agentResource = null;

			if (StringUtils.isNotBlank(agent.getResourceUri()) && isValidURI(agent.getResourceUri()))
				agentResource = model.createResource(agent.getResourceUri(), FOAFAgent.getRDFClass());
			else
				agentResource = model.createResource(FOAFAgent.getRDFClass());

			addDCATPropertyAsLiteral(agent.getName(), agentResource, model);
			addDCATPropertyAsLiteral(agent.getMbox(), agentResource, model);
			addDCATPropertyAsLiteral(agent.getType(), agentResource, model);
			addDCATPropertyAsLiteral(agent.getIdentifier(), agentResource, model);
			addDCATPropertyAsResource(agent.getHomepage(), agentResource, model, false);

			parentResource.addProperty(model.createProperty(agent.getPropertyUri()), agentResource);

		}
	}

	/**
	 * @param dataset
	 * @param model
	 * @param parentResource
	 */

	protected static <T extends SKOSConcept> void serializeConcept(List<T> conceptList, Model model,
			Resource parentResource) {

		if (conceptList != null && !conceptList.isEmpty()) {

			for (T concept : conceptList) {
				Resource conceptR = null;
				if (concept != null) {
					List<SKOSPrefLabel> labelList = concept.getPrefLabel();

					if (StringUtils.isNotBlank(concept.getResourceUri()))
						conceptR = model.createResource(concept.getResourceUri(), SKOS.Concept);
					else
						conceptR = model.createResource(SKOS.Concept);

					if (labelList != null && !labelList.isEmpty()) {
						for (SKOSPrefLabel l : labelList)
							conceptR.addProperty(SKOS.prefLabel, model.createLiteral(l.getValue(), l.getLanguage()));
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
	 * @param model
	 * @param datasetResource
	 * @param distribution
	 */
	private static void addDistributionToModel(Model model, Resource datasetResource, DCATDistribution distribution) {

		Resource distResource = model.createResource(DCATDistribution.getRDFClass());

		addDCATPropertyAsResource(distribution.getAccessURL(), distResource, model, false);

		addDCATPropertyAsLiteral(distribution.getDescription(), distResource, model);

		serializeFormat(distribution.getFormat(), model, distResource);

		serializeLicense(distribution.getLicense(), model, distResource);

		serializeByteSize(distribution.getByteSize(), model, distResource);

		serializeChecksum(distribution.getChecksum(), model, distResource);

		serializeLanguage(distribution.getLanguage(), model, distResource);

		List<DCATProperty> distDocumentation = distribution.getDocumentation();
		if (distDocumentation != null)
			distDocumentation.stream().filter(item -> StringUtils.isNotBlank(item.getValue()))
					.forEach(item -> distResource.addProperty(item.getProperty(), item.getValue()));

		addDCATPropertyAsResource(distribution.getDownloadURL(), distResource, model, false);

		serializeDCTStandard(distribution.getLinkedSchemas(), distResource, model);

		addDCATPropertyAsLiteral(distribution.getMediaType(), distResource, model);

		distResource.addProperty(distribution.getReleaseDate().getProperty(), distribution.getReleaseDate().getValue(),
				XSDDateType.XSDdateTime);
		distResource.addProperty(distribution.getUpdateDate().getProperty(), distribution.getUpdateDate().getValue(),
				XSDDateType.XSDdateTime);

		addDCATPropertyAsLiteral(distribution.getRights(), distResource, model);

		serializeConcept(Arrays.asList(distribution.getStatus()), model, distResource);

		addDCATPropertyAsLiteral(distribution.getTitle(), distResource, model);

		datasetResource.addProperty(DCAT.distribution, distResource);
	}

	/**
	 * @param distribution
	 * @param model
	 * @param distResource
	 */
	protected static void serializeByteSize(DCATProperty property, Model model, Resource distResource) {

		try {
			distResource.addProperty(property.getProperty(),
					model.createTypedLiteral(new BigDecimal(property.getValue())));
		} catch (NumberFormatException e) {
			logger.debug("The value: " + property.getValue() + " is not a valid byteSize - SKIPPED");
		}
	}

	/**
	 * @param checksum
	 * @param model
	 * @param distResource
	 */
	protected static void serializeChecksum(SPDXChecksum checksum, Model model, Resource distResource) {

		if (checksum != null) {
			distResource.addProperty(model.createProperty(checksum.getUri()),
					model.createResource(SPDXChecksum.getRDFClass())
							.addProperty(checksum.getAlgorithm().getProperty(), checksum.getAlgorithm().getValue())
							.addProperty(checksum.getChecksumValue().getProperty(),
									checksum.getChecksumValue().getValue(), XSDhexBinary.XSDhexBinary));
		}
	}

	/**
	 * @param license
	 * @param model
	 * @param distResource
	 */
	protected static void serializeLicense(DCTLicenseDocument license, Model model, Resource distResource) {

		if (license != null) {

			String licenseTypeURI = null;
			Resource licenseResource = null;

			// Initialize license Resource
			if (StringUtils.isNotBlank(license.getUri()))
				licenseResource = model.createResource(license.getUri(), DCTLicenseDocument.getRDFClass());
			else
				licenseResource = model.createResource(DCTLicenseDocument.getRDFClass());

			try {
				licenseTypeURI = iriFactory.construct(license.getType().getValue()).toString();
			} catch (Exception e) {
				licenseTypeURI = LICENSE_TYPE_BASE_URI + license.getType().getValue();
			}
			license.getType().setValue(licenseTypeURI);

			addDCATPropertyAsLiteral(license.getName(), licenseResource, model);
			addDCATPropertyAsLiteral(license.getVersionInfo(), licenseResource, model);
			addDCATPropertyAsResource(license.getType(), licenseResource, model, false);

			// Add license Resource as property object of the parent Resource
			distResource.addProperty(model.createProperty(DCTerms.license.getURI()), licenseResource);

		}
	}

	/**
	 * @param format
	 * @param model
	 * @param datasetResource
	 * @param distResource
	 */
	protected static void serializeFormat(DCATProperty format, Model model, Resource distResource) {

		if (format != null && StringUtils.isNotBlank(format.getValue())) {
			format.setValue(FORMAT_BASE_URI + format.getValue());
			addDCATPropertyAsResource(format, distResource, model, false);
		}
	}

	protected static void serializeDCTStandard(List<DCTStandard> standardList, Resource parentResource, Model model) {

		if (standardList != null && !standardList.isEmpty())
			standardList.stream().filter(standard -> standard != null).forEach(standard -> {

				Resource standardResource = null;

				if (StringUtils.isNotBlank(standard.getUri()))
					standardResource = model.createResource(standard.getUri(), DCTStandard.getRDFClass());
				else
					standardResource = model.createResource(DCTStandard.getRDFClass());

				standardResource
						.addLiteral(standard.getDescription().getProperty(), standard.getDescription().getValue())
						.addLiteral(standard.getIdentifier().getProperty(), standard.getIdentifier().getValue())
						.addLiteral(standard.getTitle().getProperty(), standard.getTitle().getValue());

				for (DCATProperty p : standard.getReferenceDocumentation())
					standardResource.addProperty(p.getProperty(), p.getValue(), XSDDateType.XSDanyURI);

				parentResource.addProperty(DCTerms.conformsTo, standardResource);

			});

	}

	/*
	 * Helper method used to add a Property, which should have a Resource as its
	 * Object, to a passed Resource e.g. hasUrl, hasEmail or hasTelephone -> type
	 * Optionally use the DCATProperty Range to create a typed Resource
	 */
	protected static void addDCATPropertyAsResource(DCATProperty property, Resource parentResource, Model model,
			boolean useRange) {

		if (StringUtils.isNotBlank(property.getValue())) {
			try {
				if (useRange)

					parentResource.addProperty(property.getProperty(), model.createResource(
							iriFactory.construct(property.getValue()).toURI().toString(), property.getRange()));
				else
					parentResource.addProperty(property.getProperty(),
							model.createResource(iriFactory.construct(property.getValue()).toURI().toString()));
			} catch (ResourceRequiredException | IRIException | URISyntaxException e) {

				// Add anyway the property as string value

				parentResource.addProperty(property.getProperty(), model.createLiteral(property.getValue()));
			}
		}
	}

	/*
	 * Helper method used to add a Property, which should have a Literal as its
	 * Object, to a passed Resource
	 */
	protected static void addDCATPropertyAsLiteral(DCATProperty property, Resource parentResource, Model model) {

		// Add the property as literal value
		if (StringUtils.isNotBlank(property.getValue()))
			parentResource.addProperty(property.getProperty(), model.createLiteral(property.getValue()));
	}

	/*
	 * Helper method used to add a Property, which should have a Typed Literal as
	 * its Object, to a passed Resource
	 */
	protected static void addDCATPropertyAsTypedLiteral(DCATProperty property, RDFDatatype dataType,
			Resource parentResource, Model model) {

		// Add the property as literal value
		if (StringUtils.isNotBlank(property.getValue()))
			parentResource.addProperty(property.getProperty(), property.getValue(), dataType);
	}

	private static String writeModelToString(Model model, DCATAPFormat format) {

		StringWriter outputWriter = new StringWriter();
		model.write(outputWriter, format.formatName());
		return outputWriter.toString();
	}

	public static void writeModelToFileAndZip(Model model, DCATAPFormat format, String filePath, String fileName)
			throws IOException {
		writeModelToFile(model, format, filePath, fileName);
		writeModelToZipFile(model, format, filePath, fileName);
	}

	public static void writeModelToFile(Model model, DCATAPFormat format, String filePath, String fileName)
			throws IOException {
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
			out.close();
			Instant tock = Instant.now();
			logger.info("File writing completed in: " + Duration.between(tick, tock).toString());

		}
	}

	public static void writeModelToZipFile(Model model, DCATAPFormat format, String filePath, String fileName)
			throws IOException {

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

	public static String searchResultToDCATAP(SearchResult result, DCATAPFormat format, DCATAPProfile profile,
			DCATAPWriteType writeType) throws IOException {

		nodeResources = FederationCore.getODMSCatalogues().stream()
				.collect(Collectors.toMap(ODMSCatalogue::getId, node -> node));

		Model model = datasetsToModel(result.getResults(), profile);

		model.setNsPrefix("co", "http://purl.org/ontology/co/core#");
		model.createResource("http://purl.org/ontology/co/core#Counter")
				.addProperty(DCTerms.description, "The total count of matching datasets")
				.addLiteral(model.createProperty("http://purl.org/ontology/co/core#count"), result.getCount());

		if (writeType.equals(DCATAPWriteType.FILE)) {

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

	public static String searchResultToDCATAPByNode(String nodeID, SearchResult result, DCATAPFormat format,
			DCATAPProfile profile, DCATAPWriteType writeType) throws IOException {

		nodeResources = FederationCore.getODMSCatalogues().stream()
				.collect(Collectors.toMap(ODMSCatalogue::getId, node -> node));

		Model model = datasetsToModel(result.getResults(), profile);

		model.setNsPrefix("co", "http://purl.org/ontology/co/core#");
		model.createResource("http://purl.org/ontology/co/core#Counter")
				.addProperty(DCTerms.description, "The total count of matching datasets")
				.addLiteral(model.createProperty("http://purl.org/ontology/co/core#count"), result.getCount());

		if (writeType.equals(DCATAPWriteType.FILE)) {

			writeModelToFileAndZip(model, format, filePath, fileName + "_node_" + nodeID);

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

	protected static boolean isValidURI(String uri) {

		return !iriFactory.create(uri).hasViolation(false);
	}

}
