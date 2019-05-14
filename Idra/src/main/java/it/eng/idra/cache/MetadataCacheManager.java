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
package it.eng.idra.cache;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import java.util.stream.Collectors;
import javax.persistence.EntityExistsException;
import javax.persistence.RollbackException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrException;
import org.apache.solr.core.CoreContainer;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFParseException;

import org.apache.logging.log4j.*;
import org.apache.commons.lang3.StringUtils;

import it.eng.idra.beans.ODFProperty;
import it.eng.idra.beans.dcat.DCATDataset;
import it.eng.idra.beans.dcat.DCATDistribution;
import it.eng.idra.beans.dcat.DCTLicenseDocument;
import it.eng.idra.beans.exception.DatasetNotFoundException;
import it.eng.idra.beans.odms.ODMSCatalogue;
import it.eng.idra.beans.odms.ODMSCatalogueState;
import it.eng.idra.beans.odms.ODMSCatalogueType;
import it.eng.idra.beans.orion.OrionCatalogueConfiguration;
import it.eng.idra.beans.orion.OrionDistributionConfig;
import it.eng.idra.beans.search.SearchFacetsList;
import it.eng.idra.beans.search.SearchResult;
import it.eng.idra.management.ODMSManager;
import it.eng.idra.management.StatisticsManager;
import it.eng.idra.search.EuroVocTranslator;
import it.eng.idra.utils.PropertyManager;

public class MetadataCacheManager {

	public static Logger logger = LogManager.getLogger(MetadataCacheManager.class);

	private static Boolean enableRdf = Boolean.parseBoolean(PropertyManager.getProperty(ODFProperty.ENABLE_RDF));
	private static SolrClient server;

	private MetadataCacheManager() {

	}

	/**
	 * Retrieve a dataset from SOLR Cache matching passed id
	 * 
	 * @param id
	 *            DCATDataset id to be retrieved
	 * @param boolean
	 *            altIdentifier Flag to use the id or altIdentifier to get the
	 *            dataset
	 * @throws IOException
	 * @throws SolrServerException
	 * @throws DatasetNotFoundException
	 * @returns DCatDataset The dataset matching id
	 */
	public static DCATDataset getDatasetByIdentifier(int nodeID, String id)
			throws DatasetNotFoundException, IOException, SolrServerException {

		SolrQuery query = new SolrQuery();
		QueryResponse rsp;

		// Don't touch
		//query.setQuery("(id:\"" + id + "\" or legacyIdentifier:\"" + id + "\") and nodeID:" + nodeID);
		query.setQuery("(identifier:\"" + id + "\") and nodeID:" + nodeID);

		query.set("parent_filter", "content_type:" + CacheContentType.dataset);
		query.set("defType", "edismax");
		query.addFilterQuery("{!parent which=$parent_filter}");
		query.setParam("fl", "*,[child parentFilter=$parent_filter limit=1000]");

		rsp = server.query(query);
		SolrDocumentList docs = rsp.getResults();

		DCATDataset tmp = null;
		for (SolrDocument doc_tmp : docs) {
			tmp = DCATDataset.docToDataset(doc_tmp);
			if (tmp.getIdentifier().getValue().equals(id)) {
				return tmp;
			}
		}
		throw new DatasetNotFoundException("Dataset not found in cache for id:" + id);

	}
	
	
	public static DCATDataset getDatasetByID(String id)
			throws DatasetNotFoundException, IOException, SolrServerException {

		SolrQuery query = new SolrQuery();
		QueryResponse rsp;

		// Don't touch
		//query.setQuery("(id:\"" + id + "\" or seoIdentifier:\"" + id + "\")");
		query.setQuery("(id:\"" + id + "\")");

		query.set("parent_filter", "content_type:" + CacheContentType.dataset);
		query.set("defType", "edismax");
		query.addFilterQuery("{!parent which=$parent_filter}");
		query.setParam("fl", "*,[child parentFilter=$parent_filter limit=1000]");

		rsp = server.query(query);
		SolrDocumentList docs = rsp.getResults();

		DCATDataset tmp = null;
		for (SolrDocument doc_tmp : docs) {
			tmp = DCATDataset.docToDataset(doc_tmp);
			if (tmp.getId().equals(id)) {
				return tmp;
			}
		}
		throw new DatasetNotFoundException("Dataset not found in cache for seoIdentifier:" + id);

	}

	/**
	 * Searches all Dataset belonging to the passed nodeID on local SOLR cache
	 * 
	 * @param data.nodeID
	 *            ID of the node which datasets belong to
	 * @throws IOException
	 * @throws SolrServerException
	 * @throws DatasetNotFoundException
	 * @returns List<DCATDataset> the list of the DCAT Datasets of the node
	 */
	public static List<DCATDataset> getAllDatasetsByODMSCatalogue(int nodeId)
			throws DatasetNotFoundException, IOException, SolrServerException {
		HashMap<String, Object> idParam = new HashMap<String, Object>();
		idParam.put("nodeID", new Integer(nodeId).toString());
		idParam.put("rows", "10000000");

		return searchDatasets(idParam).getResults();
	}
	
	public static SearchResult getAllDatasetsByODMSCatalogue(int nodeId,int rows, int start)
			throws DatasetNotFoundException, IOException, SolrServerException {
		HashMap<String, Object> idParam = new HashMap<String, Object>();
		idParam.put("nodeID", new Integer(nodeId).toString());
		idParam.put("rows", Integer.toString(rows));
		idParam.put("start", Integer.toString(start));

		return searchDatasets(idParam);
	}

	public static SearchResult getAllDatasetsByODMSCatalogueID(int nodeId)
			throws DatasetNotFoundException, IOException, SolrServerException {
		HashMap<String, Object> idParam = new HashMap<String, Object>();
		idParam.put("nodeID", new Integer(nodeId).toString());
		idParam.put("rows", "10000000");

		return searchDatasets(idParam);
	}

	/**
	 * 
	 * Gets ID and DCAT identifier of all datasets in the SOLR cache
	 * 
	 * 
	 * @return HashMap<String,String> Map of id-Dcat Identifier pairs for every
	 *         present dataset
	 * @throws SolrServerException
	 * @throws IOException
	 */
	public static List<String> getAllDatasetsID(int limit,int offset) throws SolrServerException, IOException {
		SolrQuery query = new SolrQuery();
		QueryResponse rsp;
		List<String> idList = new ArrayList<String>();
		// Set the filters in order to match parent and childs
		query.set("parent_filter", "content_type:" + CacheContentType.dataset);
		query.set("defType", "edismax");
		query.addFilterQuery("{!parent which=$parent_filter}");
		query.setParam("fl", "id");
		if(limit<0) {
			query.setRows(1000000);
		}else {
			query.setRows(limit);
			query.setStart(offset);
		}
		rsp = server.query(query);

		for (SolrDocument doc : rsp.getResults()) {
			idList.add((String) doc.getFieldValue("id"));
		}
		return idList;
	}

	
	/**
	 * 
	 * Gets ID of all datasets in the SOLR cache belonging to a catalogue
	 * 
	 * @param catalogueID, the identifier of the catalogue
	 * @return List<String> of datasets id 
	 * @throws SolrServerException
	 * @throws IOException
	 */
	public static List<String> getAllDatasetsIDByCatalogue(String catalogueID,int limit,int offset) throws SolrServerException, IOException {
		SolrQuery query = new SolrQuery();
		QueryResponse rsp;
		List<String> idList = new ArrayList<String>();
		// Set the filters in order to match parent and childs
		query.setQuery("nodeID:"+catalogueID);
		query.set("parent_filter", "content_type:" + CacheContentType.dataset);
		query.set("defType", "edismax");
		query.addFilterQuery("{!parent which=$parent_filter}");
		query.setParam("fl", "id");
		if(limit<0) {
			query.setRows(1000000);
		}else {
			query.setRows(limit);
			query.setStart(offset);
		}
		rsp = server.query(query);

		for (SolrDocument doc : rsp.getResults()) {
			idList.add((String) doc.getFieldValue("id"));
		}
		System.out.println(idList.size());
		return idList;
	}
	
	/**
	 * Searches all Dataset ID belonging to the passed nodeID on local SOLR cache
	 * 
	 * 
	 * @param data.nodeID
	 *            ID of the node which datasets belong to
	 * @param nativeID
	 *            boolean to extract the native id from the original node or the one
	 *            with the concatenated nodeID
	 * @throws IOException
	 * @throws SolrServerException
	 * @throws DatasetNotFoundException
	 * @returns List<String> the list of the Datasets ID of the node
	 */
	public static HashMap<String,ArrayList<String>> getCKANDatasetNamesIdentifiers(int nodeId)
			throws DatasetNotFoundException, IOException, SolrServerException {
		SolrQuery query = new SolrQuery();
		QueryResponse rsp;
		HashMap<String,ArrayList<String>> idMap = new HashMap<String,ArrayList<String>>();

		query.setQuery("nodeID:" + nodeId);

		query.set("parent_filter", "content_type:" + "dataset");
		query.set("defType", "edismax");
		query.addFilterQuery("{!parent which=$parent_filter}");
		//query.setParam("fl", (nativeID ? "identifier" : "id") + ",[child parentFilter=$parent_filter limit=1000]");
		query.setParam("fl", "otherIdentifier,identifier"+ ",[child parentFilter=$parent_filter limit=1000]");
		query.set("rows", "1000000");
		// query.set("fl", nativeID ? "otherIdentifier" : "id");

		rsp = server.query(query);

		for (SolrDocument doc : rsp.getResults()) {
				idMap.put((String) doc.getFieldValue("identifier"),(ArrayList<String>) doc.getFieldValue("otherIdentifier"));
		}
		return idMap;
	}

	/**
	 * Searches Dataset matching the passed id on local cache to forward the
	 * operation to persistence Manager to propagate operation to DB, in addition to
	 * SOLR cache
	 * 
	 * @param data.id
	 *            DCATDataset id to be deleted
	 * @param boolean
	 *            Flag to use deletion based on identifier or altIdentifier (CKAN)
	 * 
	 * @throws IOException
	 * @throws SolrServerException
	 * @throws DatasetNotFoundException
	 * @returns void
	 */
	public static void deleteDataset(int nodeID, DCATDataset dataset)
			throws SolrServerException, IOException, DatasetNotFoundException {
		CachePersistenceManager jpaInstance;
		jpaInstance = new CachePersistenceManager();

		// Deletes dataset from DB
		//DCATDataset matchingDataset = getDataset(nodeID, dataset.getLegacyIdentifier());
		DCATDataset matchingDataset = getDatasetByIdentifier(nodeID, dataset.getIdentifier().getValue());
		jpaInstance.jpaDeleteDataset(matchingDataset);

		// Deletes dataset from SOLR server
		// System.out.println(dataset.getId()+" "+dataset.getNodeID());
		server.deleteByQuery(
				"_root_:" + "\"" + matchingDataset.getId() + "\"" + " AND nodeID:" + matchingDataset.getNodeID());
		server.commit();

		jpaInstance.jpaClose();
		jpaInstance = null;
		matchingDataset = null;
		dataset = null;
	}

	/**
	 * Searches Dataset that has been retrieved from passed ODMSCatalogue with passed id
	 * on local cache to forward the operation to persistence Manager, to propagate
	 * operation to DB, in addition to SOLR cache
	 * 
	 * @param data.id
	 *            ODMSCatalogue id of datasets id to be deleted
	 * @throws IOException
	 * @throws SolrServerException
	 * @throws DatasetNotFoundException
	 * @throws RepositoryException
	 * @returns void
	 */
	public static void deleteAllDatasetsByODMSCatalogue(ODMSCatalogue node)
			throws IOException, SolrServerException, DatasetNotFoundException {

		CachePersistenceManager jpaInstance;
		jpaInstance = new CachePersistenceManager();

		// List<DCATDataset> matchingDatasets =
		// getAllDatasetsByODMSNode(node.getId());

		logger.info("Deleting all datasets of ODMS Node " + node.getName() + " with Id: " + node.getId());
		// Deletes dataset from DB
		// if (matchingDatasets != null && matchingDatasets.size() != 0)
		// jpaInstance.jpaDeleteDatasets(matchingDatasets);
		jpaInstance.jpaDeleteDatasetsByODMSNode(node.getId());

		// Deletes dataset by its id from SOLR Cache
		logger.info("Datasets delete from HIBERNATE complete - Start deleting from SOLR Cache");
		// UpdateResponse resp = server.deleteByQuery("nodeID:" + node.getId() +
		// " AND content_type:" + "dataset");
		UpdateResponse resp = server.deleteByQuery("nodeID:" + node.getId());

		// Deletes distributions related to dataset by their OwnerID

		// for (DCATDataset dataset : matchingDatasets) {
		// // jpaInstance.jpaDelete(dataset);
		// List<DCATDistribution> matchingDistr =
		// dataset.getDcat_distributions();
		// if (matchingDistr != null && !matchingDistr.isEmpty())
		// for (DCATDistribution d : matchingDistr) {
		// UpdateResponse distrResp = server.deleteByQuery(
		// "nodeID:" + "\"" + node.getId() + "\"" + " AND content_type:" +
		// "distribution");
		// if (d.isRDF()) {
		// if (node.getNodeType().equals(ODMSCatalogueType.SOCRATA))
		// d.getDcat_accessUrl().setValue(d.getDcat_accessUrl().getValue().split("\\?")[0]);
		//
		// try {
		// if (d.getStoredRDF()) {
		// logger.info("Deleting RDF - " + d.getDcat_accessUrl().getValue());
		// LODCacheManager.deleteRDF(d.getDcat_accessUrl().getValue());
		// }
		// } catch (RepositoryException e) {
		// logger.error("There was an error while deleting the RDF: " +
		// e.getMessage());
		// }
		// }
		//
		// }
		// matchingDistr = null;
		// }

		server.commit();
		jpaInstance.jpaClose();
		jpaInstance = null;
		// matchingDatasets = null;
		System.gc();

		logger.info("Deleting datasets completed successfully");
	}

	/**
	 * Adds a new Dataset to Persistence Manager and cache SOLR server
	 * 
	 * @param data.id
	 *            DCATDataset id to be deleted
	 * @throws SolrServerException
	 * @throws IOException
	 * @returns void
	 */
	public static void addDataset(DCATDataset dataset) throws IOException, SolrServerException, EntityExistsException {

		CachePersistenceManager jpaInstance;
		jpaInstance = new CachePersistenceManager();

		// Persists dataset to DB
		jpaInstance.jpaPersistAndCommitDataset(dataset);

		/*
		 * Load dataset and related distributions in SOLR Cache
		 * 
		 */

		server.add(dataset.toDoc());
		server.commit();
		jpaInstance.jpaClose();
		jpaInstance = null;

	}

	/**
	 * Searches Dataset matching the passed id on local cache to forward the
	 * operation to persistence Manager in order to propagate operation to DB
	 * 
	 * @param data.id
	 *            DCATDataset id to be updated
	 * @throws IOException
	 * @throws SolrServerException
	 * @throws DatasetNotFoundException
	 * @returns void
	 */
	public static void updateDataset(int nodeID, DCATDataset dataset)
			throws SolrServerException, IOException, DatasetNotFoundException {
		CachePersistenceManager jpaInstance;
		jpaInstance = new CachePersistenceManager();

		// Check if dataset is present, otherwise throw a
		// DatasetNotFoundException
		// DCATDataset matchingDataset = getDataset(dataset.getId(),false);
		//DCATDataset matchingDataset = getDataset(nodeID, dataset.getOtherIdentifier().get(0).getValue());
		//DCATDataset matchingDataset = getDataset(nodeID, dataset.getLegacyIdentifier());
		DCATDataset matchingDataset = getDatasetByIdentifier(nodeID, dataset.getIdentifier().getValue());

		//Settiamo i vecchi id e seoid
		dataset.setId(matchingDataset.getId());
		//dataset.setSeoIdentifier(matchingDataset.getSeoIdentifier());
		
		//TODO: gestire anche le datalets
		
		// Update dataset from persistence
		// jpaInstance.jpaDelete(dataset);
		jpaInstance.jpaDeleteDataset(matchingDataset);
		jpaInstance.jpaPersistAndCommitDataset(dataset);

		// Delete and add updated dataset into SOLR cache
		// server.deleteByQuery("_root_:" + "\"" + dataset.getId() + "\"" + "
		// AND nodeID:" + dataset.getNodeID());
		server.deleteByQuery("_root_:" + "\"" + matchingDataset.getId() + "\"");// + " AND nodeID:" +
																				// matchingDataset.getNodeID());
		// server.commit();
		server.add(dataset.toDoc());
		server.commit();

		jpaInstance.jpaClose();
		jpaInstance = null;
	}

	public static synchronized void updateDatasetInsertDatalet(int nodeID, DCATDataset dataset)
			throws SolrServerException, IOException, DatasetNotFoundException {
		CachePersistenceManager jpaInstance;
		jpaInstance = new CachePersistenceManager();

		jpaInstance.jpaMergeAndCommitDataset(dataset);
		// Delete and add updated dataset into SOLR cache
		// server.deleteByQuery("_root_:" + "\"" + dataset.getId() + "\"" + "
		// AND nodeID:" + dataset.getNodeID());
		server.deleteByQuery("_root_:" + "\"" + dataset.getId() + "\"");// + " AND nodeID:" + dataset.getNodeID());
		server.add(dataset.toDoc());
		server.commit();

		jpaInstance.jpaClose();
		jpaInstance = null;
	}

	public static int getDatasetNumber(HashMap<String, Object> searchParameters)
			throws SolrServerException, IOException {
		SolrQuery query = new SolrQuery();

		/*
		 * There will be a query to retrieve documents representing Datasets and one to
		 * retrieve documents representing Distributions. Then every resulting
		 * DCATDistribution bean will be added to associated DCATDataset, getting its ID
		 * (ownerId).
		 */

		// DATASETS QUERY
		// Risparmiamo cicli inutili nella buildGenericQuery
		if (searchParameters.containsKey("sort")) {
			String[] sort = ((String) searchParameters.remove("sort")).split(",");
			query.set("sort", sort[0] + " " + sort[1]);
		}

		// System.out.println(buildGenericQuery(searchParameters).toString());

		query.setQuery(buildGenericQuery(searchParameters));

		// Set the filters in order to match parent and childs
		query.set("parent_filter", "content_type:" + "dataset");
		query.set("defType", "edismax");
		query.addFilterQuery("{!parent which=$parent_filter}");
		query.setParam("fl", "*,[child parentFilter=$parent_filter limit=1000]");
		query.set("rows", "0");
		query.set("facet", "true");

		QueryResponse rsp = server.query(query);
		long num = rsp.getResults().getNumFound();
		logger.info("-- Search-- Matched Datasets in cache: " + num);
		rsp = null;

		return (int) num;

	}

	/**
	 * Performs the fulltext federated search on local cache created by nodes
	 * synchronization
	 *
	 * Builds a SOLR query to run on Embedded SOLR Server, which holds datasets
	 * metadata cache
	 *
	 * @param searchParameters
	 *            list of key,value pairs relative to keywords and fields to search
	 *            in
	 * @throws IOException
	 * @throws SolrServerException
	 * @returns SearchResult
	 *
	 *          TODO: Sostituire l'hashmap in input con l'oggetto di tipo
	 *          SearchRequest
	 */
	public static SearchResult searchDatasets(HashMap<String, Object> searchParameters)
			throws IOException, SolrServerException {
		SolrQuery query = new SolrQuery();

		QueryResponse rsp;
		List<DCATDataset> resultDatasets = new ArrayList<DCATDataset>();
		List<SearchFacetsList> facets = new ArrayList<SearchFacetsList>();

		// Risparmiamo cicli inutili nella buildGenericQuery
		if (searchParameters.containsKey("rows"))
			query.set("rows", (String) searchParameters.remove("rows"));
		if (searchParameters.containsKey("start"))
			query.set("start", (String) searchParameters.remove("start"));
		if (searchParameters.containsKey("sort")) {
			String[] sort = ((String) searchParameters.remove("sort")).split(",");
			query.set("sort", sort[0] + " " + sort[1]);
		}

		// DATASETS QUERY
		query.setQuery(buildGenericQuery(searchParameters));

		// Facets
		query.addFacetField("keywords");
		query.addFacetField("distributionFormats");
		query.addFacetField("distributionLicenses");
		query.addFacetField("nodeID");
//		query.addFacetField("theme");
		query.addFacetField("datasetThemes");

		// query.setFacetLimit(40);
		query.setFacetMinCount(1);

		// Set the filters in order to match parent and childs
		query.set("parent_filter", "content_type:" + CacheContentType.dataset);

		query.set("defType", "edismax");
		query.addFilterQuery("{!parent which=$parent_filter}");

		query.setParam("fl", "*,[child parentFilter=$parent_filter limit=1000]");

		rsp = server.query(query);

		SolrDocumentList docs = rsp.getResults();
		Long count = docs.getNumFound();

		// Collect resulting datasets
		for (SolrDocument doc : docs) {
			DCATDataset d = DCATDataset.docToDataset(doc);
			resultDatasets.add(d);
		}

		// Collect resulting facets

		for (FacetField f : rsp.getFacetFields()) {
			facets.add(new SearchFacetsList(f));
		}

		logger.info("-- Search-- Matched Datasets in cache: " + docs.getNumFound());

		docs = null;
		rsp = null;

		// TEST
		// resultDatasets.forEach(i -> {
		// if (i.getDistributions().size() > 1) {
		// i.getDistributions().forEach(d -> System.out.println(d.getDownloadURL()));
		// }
		// });

		return new SearchResult(count, resultDatasets, facets);

	}
	
	public static SearchResult searchDatasetsByQuery(String q,String sort,int rows, int offset,List<String> nodeIDS)
			throws IOException, SolrServerException {
		SolrQuery query = new SolrQuery();
		QueryResponse rsp;
		List<DCATDataset> resultDatasets = new ArrayList<DCATDataset>();
		// Set the filters in order to match parent and childs
		logger.info(q+" AND nodeID:("+String.join(" OR ", nodeIDS)+")");
		if(StringUtils.isNotBlank(q)) {
			query.setQuery("nodeID:("+String.join(" OR ", nodeIDS)+") AND "+q);
		}else {
			query.setQuery("nodeID:("+String.join(" OR ", nodeIDS)+")");
		}
		List<SortClause> sorts = Arrays.asList(sort.split(",")).stream().map(x -> new SortClause(x.split(" ")[0],x.split(" ")[1])).collect(Collectors.toList());
		query.setSorts(sorts);
		query.set("parent_filter", "content_type:" + CacheContentType.dataset);
		query.set("defType", "edismax");
		query.addFilterQuery("{!parent which=$parent_filter}");
		query.setParam("fl", "*,[child parentFilter=$parent_filter limit=1000]");
		if(rows<0) {
			query.setRows(100);
		}else {
			query.setRows(rows);
			query.setStart(offset);
		}
		rsp = server.query(query);

		SolrDocumentList docs = rsp.getResults();
		Long count = docs.getNumFound();

		// Collect resulting datasets
		for (SolrDocument doc : docs) {
			DCATDataset d = DCATDataset.docToDataset(doc);
			resultDatasets.add(d);
		}
		
		logger.info("-- Search-- Matched Datasets in cache: " + docs.getNumFound());

		docs = null;
		rsp = null;

		return new SearchResult(count, resultDatasets, null);

	}

	
	public static SearchResult searchForDistributionStatistics(HashMap<String, Object> searchParameters)
			throws IOException, SolrServerException {
		SolrQuery query = new SolrQuery();

		QueryResponse rsp;
		List<SearchFacetsList> facets = new ArrayList<SearchFacetsList>();

		if (searchParameters.containsKey("nodes") && !searchParameters.containsKey("nodeID")) {
			ArrayList<Integer> nodes = (ArrayList<Integer>) searchParameters.remove("nodes");
			String nodeIDString = nodes.stream().map(i -> i.toString()).collect(Collectors.joining(" OR ", "(", ")"));
			if (!"()".equals(nodeIDString))
				query.setQuery("nodeID:"+searchParameters.get("nodeID").toString());
		}else if(searchParameters.containsKey("nodeID")) {
			query.setQuery("nodeID:"+searchParameters.get("nodeID").toString());
		}
		
		// Risparmiamo cicli inutili nella buildGenericQuery
		query.set("rows", "0");

		// Facets
		query.addFacetField("format");
//		query.addFacetField("license");
		// query.setFacetLimit(40);
		query.setFacetMinCount(1);

		// Set the filters in order to match parent and childs
		query.set("parent_filter", "content_type:" + CacheContentType.distribution);

		query.set("defType", "edismax");
		query.addFilterQuery("{!parent which=$parent_filter}");

		query.setParam("fl", "*,[child parentFilter=$parent_filter limit=1000]");

		rsp = server.query(query);

		SolrDocumentList docs = rsp.getResults();
		Long count = docs.getNumFound();

		for (FacetField f : rsp.getFacetFields()) {
			facets.add(new SearchFacetsList(f));
		}

		logger.info("-- Search-- Matched Distribution in cache: " + docs.getNumFound());

		docs = null;
		rsp = null;

		return new SearchResult(count,null,facets);

	}
	
	public static HashMap<String,String> getAllLicensesInfo(HashMap<String, Object> searchParameters)
			throws IOException, SolrServerException {
		SolrQuery query = new SolrQuery();

		QueryResponse rsp;
		HashMap<String,String> map = new HashMap<String,String>();

		if (searchParameters.containsKey("nodes") && !searchParameters.containsKey("nodeID")) {
			ArrayList<Integer> nodes = (ArrayList<Integer>) searchParameters.remove("nodes");
			String nodeIDString = nodes.stream().map(i -> i.toString()).collect(Collectors.joining(" OR ", "(", ")"));
			if (!"()".equals(nodeIDString))
				query.setQuery("nodeID:"+searchParameters.get("nodeID").toString());
		}else if(searchParameters.containsKey("nodeID")) {
			query.setQuery("nodeID:"+searchParameters.get("nodeID").toString());
		}
		query.setRows(Integer.parseInt(searchParameters.get("rows").toString()));

		// Set the filters in order to match parent and childs
		query.set("parent_filter", "content_type:" + CacheContentType.distribution);
//		query.set("defType", "edismax");
		query.addFilterQuery("{!parent which=$parent_filter}");

		query.setParam("fl", "license");

		rsp = server.query(query);

		SolrDocumentList docs = rsp.getResults();
		Long count = docs.getNumFound();

		// Collect resulting datasets
		for (SolrDocument doc : docs) {
			DCTLicenseDocument l = DCTLicenseDocument.docToDCTLicenseDocument(doc, "");
			if(!map.containsKey(l.getName().getValue())) {
				map.put(l.getName().getValue(), l.getUri());
			}
		}

		logger.info("-- Search-- Matched Distribution in cache: " + docs.getNumFound());

		docs = null;
		rsp = null;
		
		return map;

	}
	
	
	public static SearchResult searchAllDatasets() throws IOException, SolrServerException {

		List<DCATDataset> totalDatasets = new ArrayList<DCATDataset>();
		int start = 0;
		HashMap<String, Object> searchParameters = new HashMap<String, Object>();
		searchParameters.put("ALL", "");
		searchParameters.put("rows", "1000");
		searchParameters.put("start", "0");
		searchParameters.put("sort", "title,asc");
		// searchParameters.put("nodes", new ArrayList<Integer>());

		// Initial search to get total count and first 1000 datasets
		SearchResult currentResult = searchDatasets(searchParameters);
		Long totalCount = currentResult.getCount();
		
		if (totalCount > 0)
			do {

				totalDatasets.addAll(currentResult.getResults());
				searchParameters.put("start", String.valueOf(start));
				searchParameters.put("rows", "1000");
				currentResult = searchDatasets(searchParameters);
				start += currentResult.getResults().size();

			} while (start < totalCount && currentResult.getResults().size()!=0);

		return new SearchResult(totalCount, totalDatasets);

	}

	/**
	 * Searches all Dataset belonging to the passed nodeID on local SOLR cache
	 * 
	 * @param data.nodeID
	 *            ID of the node which datasets belong to
	 * @throws IOException
	 * @throws SolrServerException
	 * @throws DatasetNotFoundException
	 * @returns SearchResult containing the list of DCAT Datasets of the node
	 */
	public static SearchResult searchAllDatasetsByODMSNode(int nodeId)
			throws DatasetNotFoundException, IOException, SolrServerException {
		HashMap<String, Object> idParam = new HashMap<String, Object>();
		idParam.put("nodeID", new Integer(nodeId).toString());
		idParam.put("rows", "10000000");

		return searchDatasets(idParam);
	}

	// public static List<SearchFacet> getSearchFacets(HashMap<String, Object>
	// searchParameters)
	// throws IOException, SolrServerException {
	// SolrQuery query = new SolrQuery();
	//
	// QueryResponse rsp;
	//
	// List<SearchFacet> facets = new ArrayList<SearchFacet>();
	//
	// // Risparmiamo cicli inutili nella buildGenericQuery
	// if (searchParameters.containsKey("rows"))
	// query.set("rows", (String) searchParameters.remove("rows"));
	// if (searchParameters.containsKey("start"))
	// query.set("start", (String) searchParameters.remove("start"));
	// if (searchParameters.containsKey("sort")) {
	// String[] sort = ((String) searchParameters.remove("sort")).split(",");
	// query.set("sort", sort[0] + " " + sort[1]);
	// }
	//
	// // DATASETS QUERY
	// query.setQuery(buildGenericQuery(searchParameters));
	//
	// // Set the filters in order to match parent and childs
	// query.setRows(1);
	// query.addFacetField("keywords");
	// query.setFacetLimit(30);
	// query.setFacetMinCount(1);
	// query.set("parent_filter", "content_type:" + "dataset");
	// query.set("defType", "edismax");
	// query.addFilterQuery("{!parent which=$parent_filter}");
	// query.setParam("fl", "*,[child parentFilter=$parent_filter limit=1000]");
	//
	// rsp = server.query(query);
	//
	// List<Count> response = rsp.getFacetFields().get(0).getValues();
	//
	// for (Count c : response) {
	// facets.add(new SearchFacet(c));
	// }
	//
	// logger.info("-- Search-- Facets found in cache: " + facets.size());
	//
	// // facets = null;
	// rsp = null;
	//
	// return facets;
	//
	// }

	/**
	 * Builds the SOLR query string starting from passed Key-value pairs
	 **/
	private static String buildGenericQuery(HashMap<String, Object> searchParameters) {

		String key, value, queryString = "";
		boolean isFirst = true;

		if (searchParameters.containsKey("nodes") && !searchParameters.containsKey("nodeID")) {
			ArrayList<Integer> nodes = (ArrayList<Integer>) searchParameters.remove("nodes");
			String nodeIDString = nodes.stream().map(i -> i.toString()).collect(Collectors.joining(" OR ", "(", ")"));
			if (!"()".equals(nodeIDString))
				searchParameters.put("nodeID", nodeIDString);
		}

		String defaultOperator = "AND";
		boolean isEurovoc = false;
		logger.info(searchParameters.toString());
		if (searchParameters.containsKey("euroVoc")) {
			isEurovoc = (boolean) searchParameters.remove("euroVoc");
			logger.info("IS EUROVOC: " + isEurovoc);
			if (isEurovoc) {
				defaultOperator = "OR";
			}
		}

		for (Entry<String, Object> e : searchParameters.entrySet()) {
			key = e.getKey();
			if (key.equals("releaseDate") || key.equals("updateDate"))
				continue;
			value = ((String) e.getValue()).replaceAll("\"", "").trim();

			if (key.equals("ALL")) {

				if (StringUtils.isNotBlank((String) value)) {

					// Add passed keywords to statistics DB
					StatisticsManager.storeKeywordsStatistic((String) value);

					// Build the SOLR query String
					// queryString += "(" + (isFirst ? "" : " OR ") + "*" +
					// ((String) value).replaceAll("\\s", "*") + "*" + ")";

					queryString += (isFirst ? "" : " AND ");

					// queryString += Arrays.asList(((String)
					// value).split(",")).stream()
					// .map(x -> x.replaceAll("\\s",
					// "*")).collect(Collectors.joining("* OR *", "(*", "*)"));

					queryString += "*:" + Arrays.asList(((String) value).split(",")).stream()
							.collect(Collectors.joining("\" " + defaultOperator + " \"", "(\"", "\")"));

				} else if (isFirst && queryString.trim().equals(""))
					queryString += "*:*";

				isFirst = false;

			} else if (key.equals("nodeID")) {

				if (StringUtils.isNotBlank((String) value))
					queryString += (isFirst ? "" : " AND ") + "nodeID:" + ((String) value);

				isFirst = false;

			} else if (key.equals("tags")) {

				queryString += (isFirst ? "" : " AND ") + "keywords" + ":" + "(\"" + value.replace(",", "\" AND \"")
						+ "\")";
				isFirst = false;

			} else if (!key.equals("sort") && !key.equals("rows") && !key.equals("start") && !key.equals("releaseDate")
					&& !key.equals("updateDate")) {

				// Add passed keywords to statistics DB
				StatisticsManager.storeKeywordsStatistic((String) value);

				// Build the SOLR query String
				// queryString += (isFirst ? "" : " AND ") + key.trim() + ":(*"
				// + ((String) value).replaceAll("\\s", "*").replaceAll(",", "*
				// OR *") + "*)";
				queryString += (isFirst ? "" : " AND ") + key.trim() + ":(\""
						+ ((String) value).replaceAll(",", "\" " + defaultOperator + " \"") + "\")";
				isFirst = false;
			}

			key = null;
			value = null;
		}

		if (searchParameters.containsKey("releaseDate")) {
			String[] startEnd = (String[]) searchParameters.remove("releaseDate");
			queryString += (isFirst ? "" : " AND ") + "releaseDate:[" + startEnd[0] + " TO " + startEnd[1] + "]";
			isFirst = false;
		}

		if (searchParameters.containsKey("updateDate")) {
			String[] startEnd = (String[]) searchParameters.remove("updateDate");
			queryString += ((isFirst ? "" : " AND ") + "updateDate:[" + startEnd[0] + " TO " + startEnd[1] + "]");
			isFirst = false;
		}
		logger.info(queryString);
		return queryString;
	}

	/*
	 * private String buildGenericDistributionQuery(HashMap<String,Object>
	 * searchParameters ) {
	 * 
	 * 
	 * For future purpose: search on distribution params Builds the query string,
	 * getting key-value pairs from an Hashmap contained in distributionFields value
	 * 
	 * 
	 * String distrQueryString = null; if
	 * (searchParameters.containsKey("distributionParams")) { HashMap<String,
	 * String> distrSearchParameters = (HashMap<String, String>) searchParameters
	 * .get("distributionParams"); boolean isFirst = true; String distrKey,
	 * distrValue; isFirst = true; for (Entry<String, String> e :
	 * distrSearchParameters.entrySet()) { distrKey = e.getKey(); distrValue =
	 * e.getValue(); if (!distrKey.equals("sort") && !distrKey.equals("rows")) { if
	 * (isFirst) distrQueryString += distrKey.trim() + "_solr" + ":" + "*" +
	 * ((String) distrValue).trim().replaceAll("\\s", "*") + "*"; else
	 * distrQueryString += " OR " + distrKey.trim() + "_solr" + ":" + "*" +
	 * ((String) distrValue).trim().replaceAll("\\s", "*") + "*"; isFirst = false; }
	 * } }
	 * 
	 * return distrQueryString; }
	 * 
	 */

	/**
	 * Performs the fulltext federated search on local cache created by nodes
	 * synchronization
	 *
	 * Builds a SOLR query to run on Embedded SOLR Server, which holds datasets
	 * metadata cache
	 *
	 * @param searchParameters
	 *            list of key,value pairs relative to keywords and fields to search
	 *            in
	 * @throws IOException
	 * @throws SolrServerException
	 * @returns void
	 *
	 */
	public static List<DCATDataset> searchDriverDatasets(HashMap<String, Object> searchParameters)
			throws IOException, SolrServerException {

		SolrQuery query = new SolrQuery();
		List<DCATDataset> resultDatasets = new ArrayList<DCATDataset>();
		/*
		 * There will be a query to retrieve documents representing Datasets and one to
		 * retrieve documents representing Distributions. Then every resulting
		 * DCATDistribution bean will be added to associated DCATDataset, getting its ID
		 * (ownerId).
		 */

		// DATASETS QUERY
		query.setQuery(buildDriverQuery(searchParameters));

		if (searchParameters.containsKey("rows"))
			query.set("rows", (String) searchParameters.get("rows"));
		if (searchParameters.containsKey("start"))
			query.set("start", (String) searchParameters.get("start"));
		if (searchParameters.containsKey("sort")) {
			String[] sort = ((String) searchParameters.get("sort")).split(",");
			query.set("sort", sort[0] + " " + sort[1]);
		}

		// Set the filters in order to match parent and childs
		query.set("parent_filter", "content_type:" + "dataset");
		query.set("defType", "edismax");
		query.addFilterQuery("{!parent which=$parent_filter}");
		query.setParam("fl", "*,[child parentFilter=$parent_filter limit=1000]");

		QueryResponse rsp = server.query(query);

		SolrDocumentList docs = rsp.getResults();

		for (SolrDocument doc : docs) {
			DCATDataset d = DCATDataset.docToDataset(doc);
			resultDatasets.add(d);
		}

		logger.info("-- Search-- Matched Datasets in cache: " + docs.getNumFound());
		rsp = null;

		return resultDatasets;

	}

	public static int countDriverSearch(HashMap<String, Object> searchParameters)
			throws SolrServerException, IOException {
		SolrQuery query = new SolrQuery();

		/*
		 * There will be a query to retrieve documents representing Datasets and one to
		 * retrieve documents representing Distributions. Then every resulting
		 * DCATDistribution bean will be added to associated DCATDataset, getting its ID
		 * (ownerId).
		 */

		// DATASETS QUERY

		query.setQuery(buildDriverQuery(searchParameters));

		query.set("parent_filter", "content_type:" + "dataset");
		query.set("defType", "edismax");
		query.addFilterQuery("{!parent which=$parent_filter}");
		query.setParam("fl", "*,[child parentFilter=$parent_filter limit=1000]");
		query.set("rows", "0");
		query.set("facet", "true");

		QueryResponse rsp = server.query(query);
		long num = rsp.getResults().getNumFound();
		logger.info("-- Search-- Matched Datasets in cache: " + num);
		rsp = null;

		return (int) num;

	}

	/**
	 * Build the SOLR query string, according to association between
	 * nodeID-resourceIDs and nodeID-filter, coming from Festival Driver
	 * 
	 **/
	private static String buildDriverQuery(HashMap<String, Object> searchParameters) {

		String punctualQuery = null, filterQuery = null;
		/*
		 * Process resourceIDs parameters specific for Festival Driver TODO To be moved
		 * on dedicated methods
		 */
		if (searchParameters.containsKey("nodeID-datasetIDs")) {

			HashMap<String, List<String>> resourceIDsMap = (HashMap<String, List<String>>) searchParameters
					.get("nodeID-datasetIDs");

			punctualQuery = "(";
			Iterator<Entry<String, List<String>>> entryIt = resourceIDsMap.entrySet().iterator();

			while (entryIt.hasNext()) {

				Map.Entry<String, List<String>> currEntry = entryIt.next();
				String nodeQuery = "(nodeID:" + currEntry.getKey();
				List<String> resourceIDs = currEntry.getValue();
				nodeQuery += resourceIDs.stream().collect(Collectors.joining("\" OR \"", " AND id:(\"", "\"))"));

				// TODO TO BE DELETED
				// String nodeQuery = "(nodeID:" + currEntry.getKey() + " AND
				// id:( ";
				// Iterator<String> it = resourceIDs.iterator();
				// while (it.hasNext()) {
				// nodeQuery += (String) it.next().toString() + (it.hasNext() ?
				// " OR " : " ");
				// }
				// nodeQuery += "))";

				punctualQuery += nodeQuery + (entryIt.hasNext() ? " OR " : " ");

			}

			punctualQuery += ")";

		}

		/*
		 * Process filter parameters specific for Festival Driver
		 *
		 */
		if (searchParameters.containsKey("filters")) {
			HashMap<String, HashMap<String, String>> filterMap = (HashMap<String, HashMap<String, String>>) searchParameters
					.get("filters");

			filterQuery = "(";
			Iterator<Entry<String, HashMap<String, String>>> entryIt = filterMap.entrySet().iterator();

			while (entryIt.hasNext()) {

				Map.Entry<String, HashMap<String, String>> currEntry = entryIt.next();
				// String nodeQuery = "(nodeID:" + currEntry.getKey() + " AND ";
				String nodeQuery = "(nodeID:" + currEntry.getKey() + " ";

				HashMap<String, String> filterKeyValue = currEntry.getValue();

				if (filterKeyValue.containsKey("regex")) {
					nodeQuery += " AND ";
					String regex = (String) filterKeyValue.get("regex");

					if (regex.startsWith("^")) {
						regex = regex.replaceFirst("\\^", "");
					} else if (!regex.startsWith(".*")) {
						regex = ".*" + regex;
					}

					if (!regex.endsWith("$") && !regex.endsWith(".*")) {
						regex += ".*";
					} else if (regex.endsWith("$")) {
						regex = regex.replace(regex.substring(regex.length() - 1), "");
					}

					nodeQuery += "( title:/" + regex + "/ OR description:/" + regex + "/ ) ";

				} else {
					// boolean isTitle = false;
					if (filterKeyValue.containsKey("title")
							&& StringUtils.isNoneBlank(filterKeyValue.getOrDefault("title", ""))) {
						nodeQuery += " AND ";
						// nodeQuery += "title:*" +
						// filterKeyValue.getOrDefault("title", "
						// ").replaceAll("\\s", "*") + "* ";
						nodeQuery += "title:\"" + filterKeyValue.getOrDefault("title", "") + "\" ";
						// isTitle = true;
					}

					if (filterKeyValue.containsKey("description")
							&& StringUtils.isNoneBlank(filterKeyValue.getOrDefault("description", ""))) {
						nodeQuery += " AND ";
						// nodeQuery += "description:*" +
						// filterKeyValue.getOrDefault("description",
						// "").replaceAll("\\s", "")
						// + "* ";
						// nodeQuery += "description:*" +
						// filterKeyValue.getOrDefault("description", "")+ "* ";
						nodeQuery += "description:\"" + filterKeyValue.getOrDefault("description", "") + "\" ";
					}
				}

				filterQuery += nodeQuery + ")" + (entryIt.hasNext() ? " OR " : " ");

			}

			filterQuery += ")";
		}

		if (punctualQuery != null)
			return "(" + punctualQuery + ")";
		else
			return "(" + filterQuery + ")";

		// If resource(s) id are provided you shouldn't search by filter but
		// only with ids
		// if (filterQuery != null && punctualQuery != null)
		// return "(" + filterQuery + " OR " + punctualQuery + ")";
		// else if (punctualQuery != null)
		// return "(" + punctualQuery + ")";
		// else
		// return "(" + filterQuery + ")";

	}

	/**
	 * Initializes the cache retrieving datasets from DB and then loading them in
	 * SOLR Server
	 * 
	 **/
	public static void init(boolean createCache, String configPath) {

		logger.info("LOAD CACHE init");
		List<DCATDataset> toLoad;

		// *************** Initializes SOLR Embedded Server
		// ***********************/
		logger.info("SOLR SERVER - init - start");
		CoreContainer container = new CoreContainer(configPath);
		container.load();
		server = new EmbeddedSolrServer(container, "core");
//		String urlString = "http://localhost:8983/solr/opendatafederation";
//		server = new HttpSolrClient.Builder(urlString).build();
		logger.info("SOLR SERVER - init - end");

		// ******************************************************/

		// Loads datasets from DB to Metadata SOLR Cache
		if (createCache) {
			logger.info("LOAD CACHE from DB - init - start");
			CachePersistenceManager jpaInstance = new CachePersistenceManager();

			LocalTime time1 = LocalTime.now();
			toLoad = jpaInstance.jpaGetDatasets();
			LocalTime time2 = LocalTime.now();
			logger.info("Dataset Loaded "+toLoad.size());
			logger.info("LOAD CACHE from DB - init - end in " + Duration.between(time1, time2) + " milliseconds");
			logger.info("LOAD DB CACHE to SOLR - start");

			try {
				// Clean previous index data
				server.deleteByQuery("*:*");
				server.commit();

				/*
				 *
				 */
				double rounds = Math.ceil(toLoad.size() / 10000.00);
				logger.debug("Loading Datasets to SOLR with Rounds: " + rounds);
				for (int i = 0; i < rounds; i++) {

					int beg = i * 10000;
					int end = (((i + 1) * 10000 - 1) > toLoad.size()) ? toLoad.size() : ((i + 1) * 10000 - 1);

					logger.debug("TOTAL: " + toLoad.size() + " FROM: " + beg + " TO: " + end);

					List<DCATDataset> datasetsToAdd = toLoad.subList(beg, end);
					if (!datasetsToAdd.isEmpty()) {
						for (DCATDataset d : datasetsToAdd)
							server.add(d.toDoc());
						server.commit();
					}
				}

				jpaInstance.jpaClose();
				jpaInstance = null;
				toLoad = null;

				logger.info("LOAD DB CACHE to SOLR - end");
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		/*
		 * Init DCAT-AP datasets dump scheduler
		 */
//		logger.info("Starting Dump Scheduler - init");
//		DCATAPDumpManager.initDumpScheduler();
//		logger.info("Starting Dump Scheduler - completed");

		System.gc();
		logger.info("LOAD CACHE end");
	}

	/**
	 * Loads all datasets of passed node into the Persistence Manager, to persist
	 * them to DB, in the SOLR cache and LOD RDF Cache
	 * 
	 * @param node
	 *            ODMSCatalogue which datasets are to be loaded
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IOException
	 * @throws SolrServerException
	 * @throws ClassNotFoundException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws RDFParseException
	 * @throws RepositoryException
	 * @throws SQLException
	 * @throws ProxoolException
	 * @returns void
	 */
	public static void loadCacheFromODMSCatalogue(ODMSCatalogue node) throws InvocationTargetException {

		LocalTime startTime = LocalTime.now();

		// Total (node) variables
		CachePersistenceManager cachePersistence = new CachePersistenceManager();
		int rdfCount = 0, skipped = 0;
		boolean stop = false;

		/*
		 * datasetCount is total node datasets count, calculated by this method
		 * 
		 * IMPORTANT datasetCount is different from the value node.getDatasetCount(),
		 * which is the one retrieved from "count" fields, returned directly by node API
		 *
		 * 
		 */
		int datasetCount = 0;

		// Iteration variables
		List<DCATDataset> currentDatasets = null;
		int currentDatasetCount = 0, currentSkipped = 0;

		/*
		 * Retrieve all datasets from the ODMS node, in a paginated way (1000 per page)
		 * 
		 */
		logger.info("Loading all datasets of the new ODMS Node " + node.getName());
		try {

			/*
			 * Retrieve all node datasets, page by page, until all the datasets pages has
			 * been retrieved
			 * 
			 */
			// while(node.getDatasetStart() < node.getDatasetCount() ||
			// node.getNodeType().equals(ODMSCatalogueType.SOCRATA ) ){

			while (!stop) {

				// Get current datasets page from ODMS Node
				currentDatasets = ODMSManager.getODMSCatalogueConnector(node).getAllDatasets();
				currentDatasetCount = currentDatasets.size();
				currentSkipped = 0;

				if (!node.getNodeType().equals(ODMSCatalogueType.CKAN) && currentDatasets.size() != 0) {

					node.setDatasetCount(currentDatasetCount);
					node.setNodeState(ODMSCatalogueState.ONLINE);
					ODMSManager.updateODMSCatalogue(node, true);
				}

				int i = 0;

				logger.info("Starting to persist current datasets (ALL in one transaction");
				cachePersistence.jpaBeginTransaction();

				for (DCATDataset dataset : currentDatasets) {

					try {

						i++;
						logger.debug("Persisting " + i);

						/*
						 * Add distribution RDFs (if any) to the LODCache
						 */
						if (enableRdf)
							dataset=handleRDFDistributions(dataset);

						cachePersistence.jpaPersistDataset(dataset);
						
						//Se è orion setting sulle downloadURL e accessURL delle distribution
						if(node.getNodeType().equals(ODMSCatalogueType.ORION)) {
							handleORIONDistribution(cachePersistence,node,dataset);
						}
						
						server.add(dataset.toDoc());
						
					} catch (EntityExistsException e) {
						logger.info("Dataset with Id: " + dataset.getId() + " is already present, then skipped");
						currentSkipped++;

					} catch (SolrServerException | IOException | SolrException e) {
						logger.info("Problem during SOLR adding of dataset with Id: " + dataset.getId()
								+ " , then skipped:" + e.getClass() + " - " + e.getMessage());
						currentSkipped++;
						cachePersistence.jpaRemoveDataset(dataset);

					}

				}

				/*
				 * Commit the current datasets page to Hibernate and then to SOLR Cache
				 * 
				 */
				try {

					logger.info("Starting to commit the current datasets page");
					logger.info("Hibernate Commit Transaction");
					cachePersistence.jpaCommitTransanction();
					logger.info("SOLR Commit");
					server.commit();
					currentDatasets = null;
					skipped += currentSkipped;
					logger.info("Current datasets page was successfully committed and persisted");

					/*
					 * If there was an error while committing the whole transaction, start to
					 * persist datasets one by one
					 */
				} catch (IllegalStateException | RollbackException | SolrServerException | IOException
						| SolrException e) {

					logger.info("There was an error while committing the current datasets page: " + e.getClass() + " - "
							+ e.getMessage());
					logger.info("Starting to persist datasets one by one");
					e.printStackTrace();

					// cachePersistence.jpaClear();
					server.rollback();
					// cachePersistence.jpaRollbackTransaction();

					i = 0;
					for (DCATDataset dataset : currentDatasets) {

						try {
							i++;
							logger.info("Persisting dataset: " + i);
							
							if (enableRdf)
								dataset=handleRDFDistributions(dataset);
						
							cachePersistence.jpaPersistOrMergeAndCommitDataset(dataset);
							//Se è orion setting sulle downloadURL e accessURL delle distribution
							if(node.getNodeType().equals(ODMSCatalogueType.ORION)) {
								handleORIONDistribution(cachePersistence,node,dataset);
							}
							server.add(dataset.toDoc());
							server.commit();

						} catch (RollbackException | IllegalStateException ex) {
							logger.info("Transaction Failed while committing dataset with Id: " + dataset.getId()
									+ " - " + e.getClass() + " - " + e.getMessage());
							skipped++;
						} catch (Exception ex) {

							logger.info("Problem during committing of dataset with Id: " + dataset.getId()
									+ " , then skipped: " + ex.getClass() + " - " + ex.getMessage());
							skipped++;
							if (ex.getClass().equals(IOException.class)
									|| ex.getClass().equals(SolrServerException.class))
								cachePersistence.jpaRemoveDataset(dataset);

						}

					}

					currentDatasets = null;

				}

				/*
				 * Update all node counts (RDF and datasets) for this page iteration and //
				 * "Start" number from which to start the next datasets // retrieval (page)
				 * 
				 */

				/*
				 * In any case (whole page or one by one commit), add the current datasets count
				 * to the global one (node)
				 */

				datasetCount += currentDatasetCount;

				node.setRdfCount(node.getRdfCount() + rdfCount);
				node.setDatasetStart(node.getDatasetStart() + currentDatasetCount);
				ODMSManager.updateODMSCatalogue(node, true);

				if (node.getNodeType().equals(ODMSCatalogueType.CKAN) || node.getNodeType().equals(ODMSCatalogueType.NATIVE)) {
					if (node.getDatasetCount() == node.getDatasetStart()
							|| node.getDatasetStart() > node.getDatasetCount() || currentDatasetCount == 0) {
						stop = true;
					}
				} else {
					stop = true;
				}

			}

			// Check if the node datasets Count, initially retrieved by node
			// API, mismatches with
			// the one calculated here
			// if (node.getDatasetStart() > node.getDatasetCount()) {
			// node.setDatasetCount(node.getDatasetCount() +
			// currentDatasetCount);
			// } else
			if (node.getDatasetCount() > node.getDatasetStart()) {
				node.setDatasetCount(datasetCount);
			}

			// Updates the node dataset Count
			if (skipped != 0) {
				node.setDatasetCount(node.getDatasetCount() - skipped);

				// Decrementando lo start, come si fa a sapere quali dataset
				// sono stati saltati?
				// TODO Valutare se salvare e trattare separatamente i dataset
				// saltati
				// node.setDatasetStart(node.getDatasetStart() - skipped);

				ODMSManager.updateODMSCatalogue(node, true);
			}

			if (stop) {
				node.setDatasetStart(-1);
				ODMSManager.updateODMSCatalogue(node, true);
			}

			logger.info("Adding datasets for ODMS node:" + node.getId() + " completed successfully");
			// node.setRdfCount(rdfCount);
			// logger.info("rdfCount: " + node.getRdfCount());
			logger.info("NODE: " + node.getHost());
			logger.info("ADDED DATASET: " + node.getDatasetCount());
			logger.info("ADDED RDF: " + node.getRdfCount());
			StatisticsManager.odmsStatistics(node, node.getDatasetCount(), 0, 0, node.getRdfCount(), 0, 0);

			System.gc();

		} catch (Exception e) {
			e.printStackTrace();
			throw new InvocationTargetException(e, e.getMessage());
		} finally {
			cachePersistence.jpaClose();
			cachePersistence = null;
		}

		LocalTime endTime = LocalTime.now();
		logger.info("Elapsed time to federate: " + Duration.between(startTime, endTime));

	}

	private static DCATDataset handleRDFDistributions(DCATDataset dataset) {

		boolean hasStoredRDF = false;
		List<DCATDistribution> distributionsToAdd = dataset.getDistributions().stream().filter(x -> x.isRDF()).collect(Collectors.toList());

		if (distributionsToAdd != null && !distributionsToAdd.isEmpty()) {

			for (DCATDistribution dist : distributionsToAdd) {

				//if (dist.isRDF()) {

					logger.info("Adding new RDF to LODCache - " + dist.getAccessURL().getValue());
					try {

						int rdfAdded;
						if (dist.getAccessURL().getValue().contains("'"))
							rdfAdded = LODCacheManager.addRDF(dist.getAccessURL().getValue().split("'")[1]);
						else
							rdfAdded = LODCacheManager.addRDF(dist.getAccessURL().getValue());

						// Set to true the "storedRDF" flag if
						// adding RDF was successful
						if (rdfAdded != 0) {
							hasStoredRDF = true;
							dist.setStoredRDF(true);

							//cachePersistence.jpaUpdateDistribution(dist, getTransaction);
							logger.info(
									"Adding new RDF to LODCache - " + dist.getAccessURL().getValue() + " -Successful");
						} else {
							logger.info(
									"Adding new RDF to LODCache - " + dist.getAccessURL().getValue() + " - Skipped");

						}

					} catch (Exception e) {

						logger.error("Exception while adding rdf: " + dist.getAccessURL().getValue() + " "
								+ e.getMessage() + "\n Then this RDF was skipped and not stored on LODCache");

					}
			}

		}

		/*
		 * Update the hasStored flag of the dataset, if there was at least one
		 * distribution with RDF stored in LODCache
		 */
		if (hasStoredRDF) 
			dataset.setHasStoredRDF(hasStoredRDF);
			//cachePersistence.jpaUpdateDataset(dataset, getTransaction);
//		}
		distributionsToAdd = null;
		return dataset;

	}

	private static void handleORIONDistribution(CachePersistenceManager cachePersistence,ODMSCatalogue node,DCATDataset dataset) {
		String internalAPI=PropertyManager.getProperty(ODFProperty.ORION_INTERNAL_API);
		OrionCatalogueConfiguration nodeConf=(OrionCatalogueConfiguration)node.getAdditionalConfig();
		for(DCATDistribution distribution : dataset.getDistributions()) {
			String url="";
			OrionDistributionConfig distroConf = (OrionDistributionConfig) distribution.getDistributionAdditionalConfig();
			if(!nodeConf.isAuthenticated() && StringUtils.isBlank(distroConf.getFiwareService()) && (StringUtils.isBlank(distroConf.getFiwareServicePath()) || distroConf.getFiwareServicePath().equals("/"))) {
				url=node.getHost()+"?"+distroConf.getQuery();
			}else {
				url= internalAPI+"/"+distroConf.getId()+"/catalogue/"+node.getId(); //dovrei mettere l'id della query -> dovrebbe già esserci in quanto la persistenza viene fatta con il nodo,
			}
			distribution.setDownloadURL(url);
			distribution.setAccessURL(url);
			cachePersistence.jpaUpdateDistribution(distribution,false);
		}
	}
	
//	private static void handleSparqlDistribution(CachePersistenceManager cachePersistence,ODMSCatalogue node,DCATDataset dataset) {
//		String internalAPI=PropertyManager.getProperty(ODFProperty.ORION_INTERNAL_API);
//		SparqlCatalogueConfiguration nodeConf= (SparqlCatalogueConfiguration) node.getAdditionalConfig();
//		for(DCATDistribution distribution : dataset.getDistributions()) {
//			String url="";
//			SparqlDistributionConfig distroConf = (SparqlDistributionConfig) distribution.getDistributionAdditionalConfig();
//			if(StringUtils.isBlank(distroConf.getFiwareService()) && (StringUtils.isBlank(distroConf.getFiwareServicePath()) || distroConf.getFiwareServicePath().equals("/"))) {
//				url=node.getHost()+"?"+distroConf.getQuery();
//			}else {
//				url= internalAPI+"/"+distroConf.getId()+"/catalogue/"+node.getId(); //dovrei mettere l'id della query -> dovrebbe già esserci in quanto la persistenza viene fatta con il nodo,
//			}
//			distribution.setDownloadURL(url);
//			distribution.setAccessURL(url);
//			cachePersistence.jpaUpdateDistribution(distribution,false);
//		}
//	}
	
	public static OrionDistributionConfig getOrionDistributionConfig(String orionDistrbutionConfig){
		CachePersistenceManager jpaInstance;
		jpaInstance = new CachePersistenceManager();
		OrionDistributionConfig res = jpaInstance.jpaGetOrionDistributionConfig(orionDistrbutionConfig);
		jpaInstance.jpaClose();
		jpaInstance = null;
		return res;
	}
	
	public static void onFinalize() {
		try {
			server.close();
			CachePersistenceManager.jpaFinalize();
			EuroVocTranslator.jpaFinalize();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
