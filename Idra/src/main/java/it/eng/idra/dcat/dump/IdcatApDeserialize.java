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

import it.eng.idra.beans.dcat.DcatApFormat;
import it.eng.idra.beans.dcat.DcatApProfileNotValidException;
import it.eng.idra.beans.dcat.DcatDataset;
import it.eng.idra.beans.dcat.DcatDistribution;
import it.eng.idra.beans.dcat.DctLocation;
import it.eng.idra.beans.dcat.DctPeriodOfTime;
import it.eng.idra.beans.dcat.DctStandard;
import it.eng.idra.beans.dcat.FoafAgent;
import it.eng.idra.beans.dcat.SkosConcept;
import it.eng.idra.beans.dcat.SpdxChecksum;
import it.eng.idra.beans.dcat.VcardOrganization;
import it.eng.idra.beans.odms.OdmsCatalogue;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.RiotException;

// TODO: Auto-generated Javadoc
/**
 * The Interface IdcatApDeserialize.
 */
public interface IdcatApDeserialize {

  /**
   * Dump to model.
   *
   * @param modelText the model text
   * @param node      the node
   * @return the model
   * @throws RiotException the riot exception
   */
  public Model dumpToModel(String modelText, OdmsCatalogue node) throws RiotException;

  /**
   * Resource to dataset.
   *
   * @param nodeId          the node id
   * @param datasetResource the dataset resource
   * @return the dcat dataset
   * @throws DcatApProfileNotValidException the dcat ap profile not valid
   *                                        exception
   */
  public DcatDataset resourceToDataset(String nodeId, Resource datasetResource)
      throws DcatApProfileNotValidException;

  /**
   * Deserialize concept.
   *
   * @param                 <T> the generic type
   * @param nodeId          the node id
   * @param datasetResource the dataset resource
   * @param toExtractP      the to extract P
   * @param type            the type
   * @return the list
   */
  <T extends SkosConcept> List<T> deserializeConcept(String nodeId, Resource datasetResource,
      Property toExtractP, Class<T> type);

  /**
   * Deserialize language.
   *
   * @param datasetResource the dataset resource
   * @return the list
   */
  List<String> deserializeLanguage(Resource datasetResource);

  /**
   * Deserialize temporal coverage.
   *
   * @param nodeId          the node id
   * @param datasetResource the dataset resource
   * @return the dct period of time
   */
  DctPeriodOfTime deserializeTemporalCoverage(String nodeId, Resource datasetResource);

  /**
   * Deserialize spatial coverage.
   *
   * @param nodeId          the node id
   * @param datasetResource the dataset resource
   * @return the dct location
   */
  DctLocation deserializeSpatialCoverage(String nodeId, Resource datasetResource);

  /**
   * Deserialize other identifier.
   *
   * @param datasetResource the dataset resource
   * @return the list
   */
  List<String> deserializeOtherIdentifier(Resource datasetResource);

  /**
   * Deserialize dct standard.
   *
   * @param nodeId          the node id
   * @param datasetResource the dataset resource
   * @return the list
   */
  List<DctStandard> deserializeDctStandard(String nodeId, Resource datasetResource);

  /**
   * Deserialize contact point.
   *
   * @param nodeId          the node id
   * @param datasetResource the dataset resource
   * @return the list
   */
  List<VcardOrganization> deserializeContactPoint(String nodeId, Resource datasetResource);

  /**
   * Deserialize foaf agent.
   *
   * @param nodeId         the node id
   * @param agentStatement the agent statement
   * @return the foaf agent
   */
  FoafAgent deserializeFoafAgent(String nodeId, Statement agentStatement);

  /**
   * Deserialize frequency.
   *
   * @param datasetResource the dataset resource
   * @return the string
   */
  String deserializeFrequency(Resource datasetResource);

  /**
   * Resource to dcat distribution.
   *
   * @param r      the r
   * @param nodeId the node id
   * @return the dcat distribution
   */
  DcatDistribution resourceToDcatDistribution(Resource r, String nodeId);

  /**
   * Deserialize checksum.
   *
   * @param nodeId the node id
   * @param r      the r
   * @return the spdx checksum
   */
  SpdxChecksum deserializeChecksum(String nodeId, Resource r);

  /**
   * Deserialize format.
   *
   * @param r the r
   * @return the string
   */
  String deserializeFormat(Resource r);

  /**
   * Deserialize format.
   *
   * @param r the r
   * @return the string
   */
  String deserializeMediaType(Resource r);

  /**
   * Extract format from uri.
   *
   * @param uri the uri
   * @return the string
   */
  String extractFormatFromUri(String uri);

  /**
   * Extract theme from uri.
   *
   * @param uri the uri
   * @return the string
   */
  String extractThemeFromUri(String uri);

  /**
   * Extract language from uri.
   *
   * @param uri the uri
   * @return the string
   */
  String extractLanguageFromUri(String uri);

  /**
   * Gets the dataset pattern.
   *
   * @param format the format
   * @return the dataset pattern
   */
  Pattern getDatasetPattern(DcatApFormat format);

}
