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

import java.util.List;
import java.util.regex.Pattern;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.RiotException;

import it.eng.idra.beans.dcat.DCATAPFormat;
import it.eng.idra.beans.dcat.DCATAPProfileNotValidException;
import it.eng.idra.beans.dcat.DCATDataset;
import it.eng.idra.beans.dcat.DCATDistribution;
import it.eng.idra.beans.dcat.DCTLocation;
import it.eng.idra.beans.dcat.DCTPeriodOfTime;
import it.eng.idra.beans.dcat.DCTStandard;
import it.eng.idra.beans.dcat.FOAFAgent;
import it.eng.idra.beans.dcat.SKOSConcept;
import it.eng.idra.beans.dcat.SPDXChecksum;
import it.eng.idra.beans.dcat.VCardOrganization;
import it.eng.idra.beans.odms.ODMSCatalogue;

public interface IDCATAPDeserialize {

	public Model dumpToModel(String modelText, ODMSCatalogue node) throws RiotException;

	public DCATDataset resourceToDataset(String nodeID, Resource datasetResource) throws DCATAPProfileNotValidException;

	<T extends SKOSConcept> List<T> deserializeConcept(String nodeID, Resource datasetResource, Property toExtractP,Class<T> type);

	List<String> deserializeLanguage(Resource datasetResource);

	DCTPeriodOfTime deserializeTemporalCoverage(String nodeID, Resource datasetResource);

	DCTLocation deserializeSpatialCoverage(String nodeID, Resource datasetResource);

	List<String> deserializeOtherIdentifier(Resource datasetResource);

	List<DCTStandard> deserializeDCTStandard(String nodeID, Resource datasetResource);

	List<VCardOrganization> deserializeContactPoint(String nodeID, Resource datasetResource);

	FOAFAgent deserializeFOAFAgent(String nodeID, Statement agentStatement);

	String deserializeFrequency(Resource datasetResource);

	DCATDistribution resourceToDCATDistribution(Resource r, String nodeID);

	SPDXChecksum deserializeChecksum(String nodeID, Resource r);

	String deserializeFormat(Resource r);

	String extractFormatFromURI(String uri);

	String extractThemeFromURI(String uri);

	String extractLanguageFromURI(String uri);

	Pattern getDatasetPattern(DCATAPFormat format);

}
