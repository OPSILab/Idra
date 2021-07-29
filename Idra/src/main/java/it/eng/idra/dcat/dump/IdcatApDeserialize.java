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
import it.eng.idra.beans.dcat.VCardOrganization;
import it.eng.idra.beans.odms.OdmsCatalogue;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.RiotException;

public interface IdcatApDeserialize {

  public Model dumpToModel(String modelText, OdmsCatalogue node) throws RiotException;

  public DcatDataset resourceToDataset(String nodeId, Resource datasetResource)
      throws DcatApProfileNotValidException;

  <T extends SkosConcept> List<T> deserializeConcept(String nodeId, Resource datasetResource,
      Property toExtractP, Class<T> type);

  List<String> deserializeLanguage(Resource datasetResource);

  DctPeriodOfTime deserializeTemporalCoverage(String nodeId, Resource datasetResource);

  DctLocation deserializeSpatialCoverage(String nodeId, Resource datasetResource);

  List<String> deserializeOtherIdentifier(Resource datasetResource);

  List<DctStandard> deserializeDctStandard(String nodeId, Resource datasetResource);

  List<VCardOrganization> deserializeContactPoint(String nodeId, Resource datasetResource);

  FoafAgent deserializeFoafAgent(String nodeId, Statement agentStatement);

  String deserializeFrequency(Resource datasetResource);

  DcatDistribution resourceToDcatDistribution(Resource r, String nodeId);

  SpdxChecksum deserializeChecksum(String nodeId, Resource r);

  String deserializeFormat(Resource r);

  String extractFormatFromUri(String uri);

  String extractThemeFromUri(String uri);

  String extractLanguageFromUri(String uri);

  Pattern getDatasetPattern(DcatApFormat format);

}
