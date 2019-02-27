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
package it.eng.idra.connectors;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RiotException;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.RDF;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

import it.eng.idra.beans.ODFProperty;
import it.eng.idra.beans.dcat.DCATAPFormat;
import it.eng.idra.beans.dcat.DCATAPProfile;
import it.eng.idra.beans.dcat.DCATDataset;
import it.eng.idra.beans.odms.ODMSCatalogue;
import it.eng.idra.beans.odms.ODMSSynchronizationResult;
import it.eng.idra.dcat.dump.DCATAPDeserializer;
import it.eng.idra.dcat.dump.DCATAPITDeserializer;
import it.eng.idra.dcat.dump.DCATAPSerializer;
import it.eng.idra.management.ODMSManager;
import it.eng.idra.utils.PropertyManager;

public class DCATDumpConnector implements IODMSConnector {

	private String nodeID;
	private ODMSCatalogue node;
	// private DCATAPProfile profile;
	private DCATAPDeserializer deserializer;
	private static Logger logger = LogManager.getLogger(DCATDumpConnector.class);

	private static String odmsDumpFilePath = PropertyManager.getProperty(ODFProperty.ODMS_DUMP_FILE_PATH);

	public DCATDumpConnector() {
	}

	public DCATDumpConnector(ODMSCatalogue node) {
		this.node = node;
		this.nodeID = String.valueOf(node.getId());

		if (node.getDCATProfile() != null) {
			switch (node.getDCATProfile()) {

			case DCATAP_IT:
				deserializer = new DCATAPITDeserializer();
				break;
			default:
				// If no valid profile was provided, instantiate a base DCATAP Deserializer
				deserializer = new DCATAPDeserializer();
				break;
			}
			
		} else {
			// If no profile was provided, instantiate a base DCATAP Deserializer and set the profile
			node.setDCATProfile(DCATAPProfile.DCATAP);
			deserializer = new DCATAPDeserializer();
		}

	}

	@Override
	public List<DCATDataset> findDatasets(HashMap<String, Object> searchParameters) throws Exception {
		ArrayList<DCATDataset> resultDatasets = new ArrayList<DCATDataset>();
		return resultDatasets;
	}

	@Override
	public int countSearchDatasets(HashMap<String, Object> searchParameters) throws Exception {
		return 0;
	}

	@Override
	public int countDatasets() throws Exception {
		// try {
		// Document doc = Jsoup.connect(node.getHost()).get();
		// return (doc != null) ? -1 : 0;
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// return 0;
		// }
		return -1;
		// try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
		// HttpGet httpget = new HttpGet(node.getHost());
		// System.out.println("Executing request " + httpget.getRequestLine());
		//
		// // Create a custom response handler
		// ResponseHandler<Integer> responseHandler = response -> {
		// int status = response.getStatusLine().getStatusCode();
		// if (status >= 200 && status < 300) {
		// return -1;
		// } else {
		// return 0;
		// }
		// };
		// return httpclient.execute(httpget, responseHandler);
		//
		// }

	}

	@Override
	public DCATDataset datasetToDCAT(Object dataset, ODMSCatalogue node) throws Exception {
		return null;
	}

	@Override
	public DCATDataset getDataset(String datasetId) throws Exception {
		return null;
	}

	@Override
	public List<DCATDataset> getAllDatasets() throws Exception {

		String dumpURL = null, dumpString = null;

		try {
			if (StringUtils.isNotBlank(dumpURL = node.getDumpURL())) {
				node.setDumpFilePath(null);
				return getDatasetsFromDumpURL(dumpURL);
			} else if (StringUtils.isNotBlank(dumpString = node.getDumpString())) {
				return getDatasetsFromDumpString(dumpString);
			} else
				throw new Exception("The node must have either the dumpURL or dumpString");

		} catch (IOException | RiotException e) {
			throw new Exception(e);
		}

	}

	private List<DCATDataset> getDatasetsFromDumpString(String dumpString) throws Exception {

		List<DCATDataset> datasetsList = new ArrayList<DCATDataset>();

		// Pass the Node Host as base URI for the model
		Model m = deserializer.dumpToModel(dumpString, node);
		StmtIterator sIt = m.listStatements(null, RDF.type, DCAT.Dataset);
		// Matcher matcher =
		// deserializer.getDatasetPattern(node.getDcatFormat()).matcher(dumpString);
		String datasetURI = null;
		while (sIt.hasNext()) {
			datasetURI = null;
			try {
				//
				// switch (node.getDcatFormat()) {
				//
				// case TURTLE:
				// datasetURI = matcher.group(1);
				// break;
				//
				// // RDFXML is the default
				// default:
				// datasetURI = matcher.group(1);
				// break;
				//
				// }
				//
				// if (StringUtils.isNotBlank(datasetURI)) {
				//
				// Resource r = m.getResource(datasetURI);
				// System.out.println(r.getLocalName());
				// datasetsList.add(deserializer.resourceToDataset(nodeID, r));
				//
				Statement s = sIt.nextStatement();
				Resource datasetResource = m.getResource(s.getSubject().getURI());
				datasetsList.add(deserializer.resourceToDataset(nodeID, datasetResource));

			} catch (Exception e) {
				logger.info("Skipped dataset - There was an error: " + e.getMessage() + " while deserializing dataset: "
						+ datasetURI);

			}
		}

		if (datasetsList.size() != 0) {
			DCATAPSerializer.writeModelToFile(m, DCATAPFormat.RDFXML, odmsDumpFilePath, "dumpFileString_" + nodeID);
			node.setDumpFilePath(odmsDumpFilePath + "dumpFileString_" + nodeID);
			// Since the registration is finished we don't need the file in memory
			node.setDumpString(null);
			ODMSManager.updateODMSCatalogue(node, true);
			return datasetsList;
		} else
			throw new Exception("No Datasets retrieved from the provided DUMP!");
	}

	private List<DCATDataset> getDatasetsFromDumpURL(String dumpURL) throws Exception {

		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
			HttpGet httpget = new HttpGet(dumpURL);
			logger.info("Executing request " + httpget.getRequestLine());

			// Create a custom response handler
			ResponseHandler<String> responseHandler = response -> {
				int status = response.getStatusLine().getStatusCode();
				if (status >= 200 && status < 300) {
					HttpEntity entity = response.getEntity();
					return entity != null ? EntityUtils.toString(entity,"UTF-8") : null;
				} else {
					throw new ClientProtocolException("Unexpected response status: " + status);
				}
			};
			String responseBody = httpclient.execute(httpget, responseHandler);
			return getDatasetsFromDumpString(responseBody);

		}
	}

	@Override
	public ODMSSynchronizationResult getChangedDatasets(List<DCATDataset> oldDatasets, String startingDateString)
			throws Exception {

		ArrayList<DCATDataset> newDatasets = (ArrayList<DCATDataset>) getAllDatasets();

		ODMSSynchronizationResult syncrhoResult = new ODMSSynchronizationResult();

		ImmutableSet<DCATDataset> newSets = ImmutableSet.copyOf(newDatasets);
		ImmutableSet<DCATDataset> oldSets = ImmutableSet.copyOf(oldDatasets);

		int deleted = 0, added = 0, changed = 0;

		/// Find added datasets
		// difference(current,present)
		SetView<DCATDataset> diff = Sets.difference(newSets, oldSets);
		logger.info("New Packages: " + diff.size());
		for (DCATDataset d : diff) {
			syncrhoResult.addToAddedList(d);
			added++;
		}

		// Find removed datasets
		// difference(present,current)
		SetView<DCATDataset> diff1 = Sets.difference(oldSets, newSets);
		logger.info("Deleted Packages: " + diff1.size());
		for (DCATDataset d : diff1) {
			syncrhoResult.addToDeletedList(d);
			deleted++;
		}

		// Find updated datasets
		// intersection(present,current)
		SetView<DCATDataset> intersection = Sets.intersection(newSets, oldSets);
		logger.fatal("Changed Packages: " + intersection.size());

		GregorianCalendar oldDate = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		oldDate.setLenient(false);
		GregorianCalendar newDate = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		oldDate.setLenient(false);
		SimpleDateFormat ISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

		int exception = 0;
		for (DCATDataset d : intersection) {
			try {
				int oldIndex = oldDatasets.indexOf(d);
				int newIndex = newDatasets.indexOf(d);
				oldDate.setTime(ISO.parse(oldDatasets.get(oldIndex).getUpdateDate().getValue()));
				newDate.setTime(ISO.parse(newDatasets.get(newIndex).getUpdateDate().getValue()));

				if (newDate.after(oldDate)) {
					syncrhoResult.addToChangedList(d);
					changed++;
				}
			} catch (Exception ex) {
				exception++;
				if (exception % 1000 == 0) {
					ex.printStackTrace();
				}
			}
		}
		logger.info("Changed " + syncrhoResult.getChangedDatasets().size());
		logger.info("Added " + syncrhoResult.getAddedDatasets().size());
		logger.info("Deleted " + syncrhoResult.getDeletedDatasets().size());
		logger.info("Expected new dataset count: " + (node.getDatasetCount() - deleted + added));

		return syncrhoResult;
	}

}
