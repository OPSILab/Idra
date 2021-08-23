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
package it.eng.idra.dcat.dump;

import it.eng.idra.beans.dcat.DcatDataset;
import it.eng.idra.beans.dcat.DcatDistribution;
import it.eng.idra.beans.dcat.DcatProperty;
import it.eng.idra.beans.dcat.DctPeriodOfTime;
import it.eng.idra.beans.dcat.FoafAgent;
import it.eng.idra.beans.dcat.VcardOrganization;
import it.eng.idra.beans.odms.OdmsCatalogue;
import it.eng.idra.utils.CommonUtil;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.jena.datatypes.xsd.impl.XSDDateType;
import org.apache.jena.iri.IRI;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.ResourceRequiredException;
import org.apache.jena.shared.BadURIException;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.SKOS;
import org.apache.jena.vocabulary.VCARD4;

// TODO: Auto-generated Javadoc
/**
 * The Class DcatApItSerializer.
 */
public class DcatApItSerializer extends DcatApSerializer {

  /** The start date prop. */
  private static Property startDateProp = ResourceFactory
      .createProperty(DCATAP_IT_BASE_URI + "startDate");

  /** The end date prop. */
  private static Property endDateProp = ResourceFactory
      .createProperty(DCATAP_IT_BASE_URI + "endDate");

  /**
   * Instantiates a new dcat ap it serializer.
   */
  private DcatApItSerializer() {
  }

  /**
   * Adds the dataset to model.
   *
   * @param dataset the dataset
   * @param model   the model
   * @return the model
   * @throws BadURIException the bad URI exception
   */
  protected static Model addDatasetToModel(DcatDataset dataset, Model model)
      throws BadURIException {

    String landingPage = dataset.getLandingPage().getValue();
    IRI iri = iriFactory.create(landingPage);
    if (iri.hasViolation(false)) {
      throw new BadURIException(
          "URI for dataset: " + iri + "is not valid, skipping the dataset in the Jena Model:"
              + (iri.violations(false).next()).getShortMessage());
    }

    Resource datasetResource = model.createResource(iri.toString(),
        model.createResource(DCATAP_IT_BASE_URI + "Dataset"));

    datasetResource.addProperty(RDF.type, DCAT.Dataset);

    addDcatPropertyAsLiteral(dataset.getTitle(), datasetResource, model);

    addDcatPropertyAsLiteral(dataset.getDescription(), datasetResource, model);

    serializeConcept(dataset.getTheme(), model, datasetResource);

    serializeContactPoint(dataset.getContactPoint(), model, datasetResource);

    dataset.getKeywords().stream().filter(keyword -> StringUtils.isNotBlank(keyword))
        .forEach(keyword -> datasetResource.addLiteral(DCAT.keyword, keyword));

    serializeDctStandard(dataset.getConformsTo(), datasetResource, model);

    List<DcatProperty> relatedResourceList = dataset.getRelatedResource();
    if (relatedResourceList != null) {
      relatedResourceList.stream().filter(item -> isValidUri(item.getValue()))
          .forEach(item -> addDcatPropertyAsResource(item, datasetResource, model, true));
    }

    serializeFoafAgent(dataset.getRightsHolder(), model, datasetResource);

    serializeFoafAgent(dataset.getCreator(), model, datasetResource);

    serializeFrequency(dataset.getFrequency(), model, datasetResource);

    List<DcatProperty> isVersionOf = dataset.getIsVersionOf();
    if (isVersionOf != null) {
      isVersionOf.stream().filter(item -> isValidUri(item.getValue()))
          .forEach(item -> addDcatPropertyAsResource(item, datasetResource, model, true));
    }

    addDcatPropertyAsResource(dataset.getLandingPage(), datasetResource, model, false);

    serializeLanguage(dataset.getLanguage(), model, datasetResource);

    datasetResource.addProperty(dataset.getReleaseDate().getProperty(),
        dataset.getReleaseDate().getValue(), XSDDateType.XSDdateTime);
    datasetResource.addProperty(dataset.getUpdateDate().getProperty(),
        dataset.getUpdateDate().getValue(), XSDDateType.XSDdateTime);
    datasetResource.addProperty(dataset.getIdentifier().getProperty(),
        dataset.getIdentifier().getValue());

    List<DcatProperty> otherIdentifier = dataset.getOtherIdentifier();
    if (otherIdentifier != null) {
      otherIdentifier.stream().filter(id -> StringUtils.isNotBlank(id.getValue())).forEach(
          id -> datasetResource.addProperty(model.createProperty(id.getProperty().getURI()),
              model.createResource(id.getRange()).addLiteral(SKOS.notation, id.getValue())));
    }

    serializeSpatialCoverage(dataset.getSpatialCoverage(), model, datasetResource);

    serializeTemporalCoverage(dataset.getTemporalCoverage(), model, datasetResource);

    serializeConcept(dataset.getSubject(), model, datasetResource);

    List<DcatDistribution> distributions = dataset.getDistributions();
    for (DcatDistribution distribution : distributions) {
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

    serializeFoafAgent(
        new FoafAgent(DCTerms.publisher.getURI(), publisherResourceUri, node.getPublisherName(),
            node.getPublisherEmail(), node.getPublisherUrl(), "", "", String.valueOf(node.getId())),
        model, model.getResource(node.getHost()));

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
                model.createResource(DCATAP_IT_BASE_URI + "Organization"));
          } else {
            contactPointR = model
                .createResource(model.createResource(DCATAP_IT_BASE_URI + "Organization"));
          }

          // Handle hasTelephone property
          // Resource telephoneResource = model.createResource(object.getURI() +
          // "#hasTelephone").addProperty(
          // contactPoint.getHasTelephoneValue().getProperty(),
          // contactPoint.getHasTelephoneValue().getValue());
          // addDCATPropertyAsResource(contactPoint.getHasTelephoneType(),
          // telephoneResource, model);

          // Add all properties to the Resource, that is the object of the
          // contactPoint property
          contactPointR.addProperty(RDF.type, VCARD4.Kind)
              .addProperty(RDF.type, VcardOrganization.getRdfClass())
              .addLiteral(contactPoint.getFn().getProperty(), contactPoint.getFn().getValue());
          // .addProperty(VCARD4.hasTelephone, telephoneResource);
          if (!contactPointR.hasProperty(VCARD4.hasTelephone)) {

            Resource hasTelephoneR = null;
            try {
              hasTelephoneR = model.createResource();

              addDcatPropertyAsLiteral(contactPoint.getHasTelephoneValue(), hasTelephoneR, model);

              try {
                addDcatPropertyAsResource(contactPoint.getHasTelephoneType(), hasTelephoneR, model,
                    false);
              } catch (ResourceRequiredException e) {
                addDcatPropertyAsLiteral(contactPoint.getHasTelephoneType(), hasTelephoneR, model);

              }

              contactPointR.addProperty(VCARD4.hasTelephone, hasTelephoneR);

            } catch (ResourceRequiredException ignore) {
              logger.debug(ignore.getLocalizedMessage());
            }
          }

          // Fix hasEmail value if needed
          String hasEmailValue = contactPoint.getHasEmail().getValue();
          if (StringUtils.isNotBlank(hasEmailValue) && CommonUtil.checkIfIsEmail(hasEmailValue)) {
            if (!hasEmailValue.startsWith("mailto:")) {
              contactPoint.getHasEmail().setValue("mailto:" + hasEmailValue);
            }
            addDcatPropertyAsResource(contactPoint.getHasEmail(), contactPointR, model, false);
          }

          addDcatPropertyAsResource(contactPoint.getHasUrl(), contactPointR, model, false);

          // Add contactPoint property to the dataset Resource;
          datasetResource.addProperty(model.createProperty(contactPoint.getPropertyUri()),
              contactPointR);

        } catch (Exception ignore) {
          logger.error(ignore);
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

      if (StringUtils.isNotBlank(agent.getResourceUri())) {
        agentResource = model.createResource(agent.getResourceUri(),
            model.createResource(DCATAP_IT_BASE_URI + "Agent"));
      } else {
        agentResource = model.createResource(DCATAP_IT_BASE_URI + "Agent");
      }

      addDcatPropertyAsLiteral(agent.getName(), agentResource, model);
      addDcatPropertyAsLiteral(agent.getMbox(), agentResource, model);
      addDcatPropertyAsLiteral(agent.getType(), agentResource, model);
      addDcatPropertyAsLiteral(agent.getIdentifier(), agentResource, model);
      addDcatPropertyAsResource(agent.getHomepage(), agentResource, model, false);
      addDcatPropertyAsLiteral(new DcatProperty(RDF.type, null, FoafAgent.getRdfClass().getURI()),
          agentResource, model);

      parentResource.addProperty(model.createProperty(agent.getPropertyUri()), agentResource);
    }

  }

  /**
   * Serialize temporal coverage.
   *
   * @param temporalCoverage the temporal coverage
   * @param model            the model
   * @param datasetResource  the dataset resource
   */
  protected static void serializeTemporalCoverage(DctPeriodOfTime temporalCoverage, Model model,
      Resource datasetResource) {

    if (temporalCoverage != null) {
      datasetResource.addProperty(model.createProperty(temporalCoverage.getUri()),
          model.createResource(DctPeriodOfTime.getRdfClass())
              .addProperty(startDateProp, temporalCoverage.getStartDate().getValue(),
                  XSDDateType.XSDdate)
              .addProperty(endDateProp, temporalCoverage.getEndDate().getValue(),
                  XSDDateType.XSDdate));
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

    serializeLicense(distribution.getLicense(), model, distResource);

    addDcatPropertyAsLiteral(distribution.getByteSize(), distResource, model);

    addDcatPropertyAsResource(distribution.getDownloadUrl(), distResource, model, false);

    distResource.addProperty(distribution.getUpdateDate().getProperty(),
        distribution.getUpdateDate().getValue(), XSDDateType.XSDdateTime);

    addDcatPropertyAsLiteral(distribution.getTitle(), distResource, model);

    datasetResource.addProperty(DCAT.distribution, distResource);
  }

}
