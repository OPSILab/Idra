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
package it.eng.idra.search;

import it.eng.idra.beans.dcat.DCATDataset;
import it.eng.idra.beans.exception.EuroVocTranslationNotFoundException;
import it.eng.idra.beans.odms.ODMSCatalogue;
import it.eng.idra.beans.odms.ODMSCatalogueFederationLevel;
import it.eng.idra.beans.odms.ODMSCatalogueNotFoundException;
import it.eng.idra.beans.odms.ODMSCatalogueState;
import it.eng.idra.beans.odms.ODMSCatalogueType;
import it.eng.idra.beans.search.SearchResult;
import it.eng.idra.cache.CachePersistenceManager;
import it.eng.idra.cache.MetadataCacheManager;
import it.eng.idra.connectors.IODMSConnector;
import it.eng.idra.management.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.logging.log4j.*;

enum DatasetComparator implements Comparator<DCATDataset> {
	IDENTIFIER_SORT {
		public int compare(DCATDataset o1, DCATDataset o2) {
			return o1.getId().compareTo(o2.getId());
		}
	},
	NODEID_SORT {
		public int compare(DCATDataset o1, DCATDataset o2) {
			return o1.getNodeID().compareTo(o2.getNodeID());
		}
	},
	TITLE_SORT {
		public int compare(DCATDataset o1, DCATDataset o2) {
			return o1.getTitle().getValue().compareTo(o2.getTitle().getValue());
		}
	},
	PUBLISHER_NAME_SORT {
		public int compare(DCATDataset o1, DCATDataset o2) {
			return o1.getPublisher().getName().getValue().compareTo(o2.getPublisher().getName().getValue());
		}
	},
	CONTACTPOINT_FN_SORT {
		public int compare(DCATDataset o1, DCATDataset o2) {
			return o1.getContactPoint().get(0).getFn().getValue()
					.compareTo(o2.getContactPoint().get(0).getFn().getValue());
		}
	},
	CONTACTPOINT_HASEMAIL_SORT {
		public int compare(DCATDataset o1, DCATDataset o2) {
			return o1.getContactPoint().get(0).getHasEmail().getValue()
					.compareTo(o2.getContactPoint().get(0).getHasEmail().getValue());
		}
	},
	// LICENSETITLE_SORT {
	// public int compare(DCATDataset o1, DCATDataset o2) {
	// return o1.getLicense().getValue().compareTo(o2.getLicense().getValue());
	// }
	// },
	ISSUED_SORT {
		public int compare(DCATDataset o1, DCATDataset o2) {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			GregorianCalendar date1 = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
			GregorianCalendar date2 = new GregorianCalendar(TimeZone.getTimeZone("UTC"));

			try {
				date1.setTime(sdf.parse(o1.getReleaseDate().getValue()));
				date2.setTime(sdf.parse(o2.getReleaseDate().getValue()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return date1.compareTo(date2);
		}
	},
	MODIFIED_SORT {
		public int compare(DCATDataset o1, DCATDataset o2) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			GregorianCalendar date1 = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
			GregorianCalendar date2 = new GregorianCalendar(TimeZone.getTimeZone("UTC"));

			try {
				date1.setTime(sdf.parse(o1.getUpdateDate().getValue()));
				date2.setTime(sdf.parse(o2.getUpdateDate().getValue()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return date1.compareTo(date2);
		}
	};

	public static Comparator<DCATDataset> decending(final Comparator<DCATDataset> other) {
		return new Comparator<DCATDataset>() {
			public int compare(DCATDataset o1, DCATDataset o2) {
				return -1 * other.compare(o1, o2);
			}
		};
	}

	public static Comparator<DCATDataset> getComparator(final DatasetComparator... multipleOptions) {
		return new Comparator<DCATDataset>() {
			public int compare(DCATDataset o1, DCATDataset o2) {
				for (DatasetComparator option : multipleOptions) {
					int result = option.compare(o1, o2);
					if (result != 0) {
						return result;
					}
				}
				return 0;
			}
		};
	}
}

public class FederatedSearch {

	private static Logger logger = LogManager.getLogger(FederatedSearch.class);

	private FederatedSearch() {
	};

	// public static List<SearchFacet> getSearchFacets(HashMap<String, Object>
	// searchParameters) throws IOException, SolrServerException,
	// ODMSCatalogueNotFoundException{
	// if((boolean) searchParameters.remove("live")){
	// return MetadataCacheManager.getSearchFacets(searchParameters);
	// }
	// return MetadataCacheManager.getSearchFacets(searchParameters);
	// }

	// public static List<SearchFacet> getLiveSearchFacets(HashMap<String,
	// Object> searchParameters)
	// throws IOException, SolrServerException, ODMSCatalogueNotFoundException {
	// ArrayList<Integer> nodesToBeSearched = (ArrayList<Integer>)
	// searchParameters.remove("nodes");
	//
	// // If the "nodes" parameter is not present, get all federated nodes
	// if (nodesToBeSearched == null)
	// nodesToBeSearched = ODMSManager.getODMSNodesID();
	//
	// // Divides the total passed rows for each node to be searched
	// // Integer nodeRows = Math.round((int)
	// //
	// Integer.parseInt(searchParameters.remove("rows").toString())/nodesToBeSearched.size());
	// // searchParameters.put("rows", nodeRows.toString());
	//
	// HashMap<ODMSCatalogueType, String> ODMSConnectorsList =
	// FederationCore.getODMSConnectorsList();
	// logger.info("Live search");
	// ClassLoader loader = Thread.currentThread().getContextClassLoader();
	// List<DCATDataset> globalResult = new ArrayList<DCATDataset>(); // Results
	// // from
	// // all
	// // nodes
	//
	// List<DCATDataset> nodeDatasets;
	// ODMSConnector currentConnector;
	// int allResults = 0;
	// boolean flag = false;
	// // Iterates on federated nodes list and for each of them performs live
	// // search
	// for (Integer id : nodesToBeSearched) {
	// ODMSCatalogue node = null;
	// if ((node = ODMSManager.getODMSNode(id)) != null
	// && !node.getFederationLevel().equals(ODMSCatalogueFederationLevel.LEVEL_0)
	// && !node.getFederationLevel().equals(ODMSCatalogueFederationLevel.LEVEL_2)
	// && node.getNodeState().equals(ODMSCatalogueState.ONLINE))
	// try {
	// // logger.info("\n\nNODE: "+node.getName());
	// // The connector class is loaded, based on ODMS node type,
	// // using Java reflection
	// Class<?> cls =
	// loader.loadClass(ODMSConnectorsList.get(node.getNodeType()));
	// currentConnector = (ODMSConnector)
	// cls.getDeclaredConstructor(ODMSCatalogue.class).newInstance(node);
	//
	// Method m = cls.getDeclaredMethod("countSearchDatasets", HashMap.class);
	// // Number of results of a single node with specific search
	// // parameters
	// int result = (int) m.invoke(currentConnector, searchParameters);
	//
	// // global variable used to keep track of all results
	// allResults += result;
	//
	// // Page number of this particular search
	// int page = Integer.parseInt((String) searchParameters.get("start"))
	// / Integer.parseInt((String) searchParameters.get("rows"));
	//
	// // System.out.println("------------------------------------------");
	// // System.out.println("RESULT: " + result);
	// // System.out.println("All RESULTS: " + allResults);
	// // System.out.println("START: " + Integer.parseInt((String)
	// // searchParameters.get("start")));
	// if (result != 0 && allResults >= Integer.parseInt((String)
	// searchParameters.get("start"))) {
	//
	// if (flag) {
	// // System.out.println("FLAG==TRUE");
	// page -= Math.ceil(
	// (allResults - result) / (Integer.parseInt((String)
	// searchParameters.get("rows"))));
	//
	// // System.out.println("PAGE: " + page);
	//
	// page--;
	//
	// // System.out.println("PAGE: " + page);
	//
	// int newStart = Integer.parseInt((String) searchParameters.get("rows")) *
	// page
	// + Integer.parseInt((String) searchParameters.get("rows"))
	// - (allResults - result) % Integer.parseInt((String)
	// searchParameters.get("rows"));
	//
	// if (newStart < 0)
	// newStart = 0;
	// // System.out.println("NEW START: " + newStart);
	// // logger.info(newStart);
	// // Set new offset
	// searchParameters.put("start", Integer.toString(newStart));
	//
	// }
	//
	// m = cls.getDeclaredMethod("findDatasets", HashMap.class);
	// Object currentSearchParameters = searchParameters.clone();
	// nodeDatasets = (List<DCATDataset>) m.invoke(currentConnector,
	// currentSearchParameters);
	//
	// globalResult.addAll(nodeDatasets);
	// // System.out.println("ROWS: " +
	// // Integer.parseInt((String)
	// // searchParameters.get("rows")));
	// if (nodeDatasets.size() < Integer.parseInt((String)
	// searchParameters.get("rows"))) {
	//
	// // System.out.println("NODE DATASET SIZE: " +
	// // nodeDatasets.size());
	//
	// int newRows = Integer.parseInt((String) searchParameters.get("rows")) -
	// nodeDatasets.size();
	// // System.out.println("NEW Rows: " + newRows);
	// searchParameters.put("rows", Integer.toString(newRows));
	// searchParameters.put("start", "0");
	// flag = false;
	// } else {
	// // System.out.println("Break");
	// break;
	// }
	//
	// } else {
	// // System.out.println("SET Flag = true");
	// flag = true;
	//
	// }
	//
	// currentConnector = null;
	// System.gc();
	// logger.info("Live search success");
	// logger.info("Results found:" + globalResult.size());
	// } catch (Exception e) {
	// logger.error("There was an error in live search");
	// e.printStackTrace();
	// return null;
	// }
	//
	// // System.out.println("------------------------------------------");
	// }
	//
	// // Before returning the global result from all nodes, it performs the
	// // sort on it
	// // Sorting
	// String[] params = new String[2];
	//
	// if (searchParameters.containsKey("sort"))
	// params = ((String) searchParameters.get("sort")).split(",");
	// else {
	// params[0] = "nodeID";
	// params[1] = "asc";
	// }
	//
	// if (params[1].equals("desc"))
	// Collections.sort(globalResult, DatasetComparator.decending(
	// DatasetComparator.getComparator(DatasetComparator.valueOf(params[0].toUpperCase()
	// + "_SORT"))));
	// else
	// Collections.sort(globalResult,
	// DatasetComparator.getComparator(DatasetComparator.valueOf(params[0].toUpperCase()
	// + "_SORT")));
	//
	// return null;
	// }
	//
	//

	/**
	 * 
	 * Performs the local search for all federated nodes, in order to get a dump
	 * of cached datasets
	 * 
	 * 
	 * @return
	 * @throws IOException
	 * @throws SolrServerException
	 */
	public static SearchResult dumpDatasets() throws IOException, SolrServerException {

		return MetadataCacheManager.searchAllDatasets();

	}

	/**
	 * Performs the fulltext federated search over all federated nodes in the
	 * federation
	 *
	 * Iterates all federated nodes and selects the appropriate connector class
	 * for the current node type to perform fulltext search
	 *
	 * @param searchParameters
	 *            list of key,value pairs relative to fields and keywords to
	 *            search,
	 * @param live_search
	 *            selects search type, live on nodes or on local cache
	 * @throws SolrServerException
	 * @throws IOException
	 * @throws SQLException
	 * @throws ODMSCatalogueNotFoundException
	 * @throws EuroVocTranslationNotFoundException 
	 * @returns List<DCATDataset> The list of matching DCATDataset
	 */
	public static SearchResult search(HashMap<String, Object> searchParameters)
			throws IOException, SolrServerException, SQLException, ODMSCatalogueNotFoundException, EuroVocTranslationNotFoundException {

		// EuroVoc processing
		if (searchParameters.containsKey("euroVoc") && (boolean) searchParameters.get("euroVoc")) {
			searchParameters = EuroVocTranslator.replaceEuroVocTerms(searchParameters);
		}

		if ((boolean) searchParameters.remove("live")) {
			HashMap<String, Object> searchParameters1 = new HashMap<>(searchParameters);
			SearchResult result = new SearchResult(new Long(countLiveSearch(searchParameters1)),
					liveSearch(searchParameters).getResults());
			return result;
		} else
			return localSearch(searchParameters);
	}

	public static SearchResult searchByQuery(String query,String sort,int rows, int offset,List<String> nodeIDS)
			throws IOException, SolrServerException, SQLException, ODMSCatalogueNotFoundException, EuroVocTranslationNotFoundException {

		return MetadataCacheManager.searchDatasetsByQuery(query,sort,rows,offset,nodeIDS);
	}
	
	public static List<DCATDataset> searchDriver(HashMap<String, Object> searchParameters)
			throws IOException, SolrServerException, SQLException {
		logger.info("Search by resourceIDs and filters");
		return MetadataCacheManager.searchDriverDatasets(searchParameters);
	}
	
	public static SearchResult getFormatStatistics(HashMap<String, Object> searchParameters)
			throws IOException, SolrServerException, SQLException {
		logger.info("Get Format Statistics");
		return MetadataCacheManager.searchForDistributionStatistics(searchParameters);
	}

	public static HashMap<String,String> getLicensesInfos(HashMap<String, Object> searchParameters)
			throws IOException, SolrServerException, SQLException {
		logger.info("Get Licenses Infos");
		return MetadataCacheManager.getAllLicensesInfo(searchParameters);
	}
	
	
	// public static List<DCATDataset> regexSearch(HashMap<String, Object>
	// searchParameters)
	// throws IOException, SolrServerException, SQLException {
	// logger.info("Search by Regular expression");
	// return MetadataCacheManager.regexSearch(searchParameters);
	// }

	public static List<DCATDataset> regexSearch(HashMap<String, Object> searchParameters)
			throws IOException, SQLException {
		logger.info("Search by Location");
		CachePersistenceManager jpa = new CachePersistenceManager();

		List<DCATDataset> result = jpa.jpaGetDatasetsByRegex(searchParameters);
		jpa.jpaClose();
		jpa = null;
		return result;
	}

	public static List<DCATDataset> spatialSearch(HashMap<String, Object> searchParameters)
			throws IOException, SQLException {
		logger.info("Search by Location");
		CachePersistenceManager jpa = new CachePersistenceManager();

		List<DCATDataset> result = jpa.jpaGetDatasetsByLocation(searchParameters);
		jpa.jpaClose();
		jpa = null;
		return result;
	}

	private static SearchResult localSearch(HashMap<String, Object> searchParameters)
			throws IOException, SolrServerException {
		logger.info("Searching on cache");
		return MetadataCacheManager.searchDatasets(searchParameters);
	}

	private static SearchResult liveSearch(HashMap<String, Object> searchParameters)
			throws SQLException, ODMSCatalogueNotFoundException {

		// Gets federated nodes and relative connectors lists
		ArrayList<Integer> nodesToBeSearched = (ArrayList<Integer>) searchParameters.remove("nodes");

		// If the "nodes" parameter is not present, get all federated nodes
		if (nodesToBeSearched == null)
			nodesToBeSearched = ODMSManager.getODMSCataloguesID();

		// Divides the total passed rows for each node to be searched
		// Integer nodeRows = Math.round((int)
		// Integer.parseInt(searchParameters.remove("rows").toString())/nodesToBeSearched.size());
		// searchParameters.put("rows", nodeRows.toString());

		HashMap<ODMSCatalogueType, String> ODMSConnectorsList = FederationCore.getODMSConnectorsList();
		logger.info("Live search");
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		List<DCATDataset> globalResult = new ArrayList<DCATDataset>(); // Results
		// from
		// all
		// nodes

		List<DCATDataset> nodeDatasets;
		IODMSConnector currentConnector;
		int allResults = 0;
		boolean flag = false;
		// Iterates on federated nodes list and for each of them performs live
		// search
		for (Integer id : nodesToBeSearched) {
			ODMSCatalogue node = null;
			if ((node = ODMSManager.getODMSCatalogue(id)) != null
					&& !node.getFederationLevel().equals(ODMSCatalogueFederationLevel.LEVEL_0)
					&& !node.getFederationLevel().equals(ODMSCatalogueFederationLevel.LEVEL_2)
					&& node.getNodeState().equals(ODMSCatalogueState.ONLINE))
				try {
					// logger.info("\n\nNODE: "+node.getName());
					// The connector class is loaded, based on ODMS node type,
					// using Java reflection
					Class<?> cls = loader.loadClass(ODMSConnectorsList.get(node.getNodeType()));
					currentConnector = (IODMSConnector) cls.getDeclaredConstructor(ODMSCatalogue.class).newInstance(node);

					Method m = cls.getDeclaredMethod("countSearchDatasets", HashMap.class);
					// Number of results of a single node with specific search
					// parameters
					int result = (int) m.invoke(currentConnector, searchParameters);

					// global variable used to keep track of all results
					allResults += result;

					// Page number of this particular search
					int page = Integer.parseInt((String) searchParameters.get("start"))
							/ Integer.parseInt((String) searchParameters.get("rows"));

					// System.out.println("------------------------------------------");
					// System.out.println("RESULT: " + result);
					// System.out.println("All RESULTS: " + allResults);
					// System.out.println("START: " + Integer.parseInt((String)
					// searchParameters.get("start")));
					if (result != 0 && allResults >= Integer.parseInt((String) searchParameters.get("start"))) {

						if (flag) {
							// System.out.println("FLAG==TRUE");
							page -= Math.ceil(
									(allResults - result) / (Integer.parseInt((String) searchParameters.get("rows"))));

							// System.out.println("PAGE: " + page);

							page--;

							// System.out.println("PAGE: " + page);

							int newStart = Integer.parseInt((String) searchParameters.get("rows")) * page
									+ Integer.parseInt((String) searchParameters.get("rows"))
									- (allResults - result) % Integer.parseInt((String) searchParameters.get("rows"));

							if (newStart < 0)
								newStart = 0;
							// System.out.println("NEW START: " + newStart);
							// logger.info(newStart);
							// Set new offset
							searchParameters.put("start", Integer.toString(newStart));

						}

						m = cls.getDeclaredMethod("findDatasets", HashMap.class);
						Object currentSearchParameters = searchParameters.clone();
						nodeDatasets = (List<DCATDataset>) m.invoke(currentConnector, currentSearchParameters);

						globalResult.addAll(nodeDatasets);
						// System.out.println("ROWS: " +
						// Integer.parseInt((String)
						// searchParameters.get("rows")));
						if (nodeDatasets.size() < Integer.parseInt((String) searchParameters.get("rows"))) {

							// System.out.println("NODE DATASET SIZE: " +
							// nodeDatasets.size());

							int newRows = Integer.parseInt((String) searchParameters.get("rows")) - nodeDatasets.size();
							// System.out.println("NEW Rows: " + newRows);
							searchParameters.put("rows", Integer.toString(newRows));
							searchParameters.put("start", "0");
							flag = false;
						} else {
							// System.out.println("Break");
							break;
						}

					} else {
						// System.out.println("SET Flag = true");
						flag = true;

					}

					currentConnector = null;
					System.gc();
					logger.info("Live search success");
					logger.info("Results found:" + globalResult.size());
				} catch (Exception e) {
					logger.error("There was an error in live search");
					e.printStackTrace();
					return null;
				}

			// System.out.println("------------------------------------------");
		}

		// Before returning the global result from all nodes, it performs the
		// sort on it
		// Sorting
		String[] params = new String[2];

		if (searchParameters.containsKey("sort"))
			params = ((String) searchParameters.get("sort")).split(",");
		else {
			params[0] = "nodeID";
			params[1] = "asc";
		}

		if (params[1].equals("desc"))
			Collections.sort(globalResult, DatasetComparator.decending(
					DatasetComparator.getComparator(DatasetComparator.valueOf(params[0].toUpperCase() + "_SORT"))));
		else
			Collections.sort(globalResult,
					DatasetComparator.getComparator(DatasetComparator.valueOf(params[0].toUpperCase() + "_SORT")));

		return new SearchResult(new Long(globalResult.size()), globalResult);
	}

	public static int countDataset(HashMap<String, Object> searchParameters)
			throws IOException, SolrServerException, SQLException, ODMSCatalogueNotFoundException, EuroVocTranslationNotFoundException {

		// EuroVoc processing
		if (searchParameters.containsKey("euroVoc") && (boolean) searchParameters.get("euroVoc")) {
			searchParameters = EuroVocTranslator.replaceEuroVocTerms(searchParameters);
		}

		if ((boolean) searchParameters.remove("live"))
			return countLiveSearch(searchParameters);
		else
			return countLocalSearch(searchParameters);
	}

	public static int countDriverSearch(HashMap<String, Object> searchParameters)
			throws IOException, SolrServerException {
		logger.info("Searching on cache with resourceIDs and filters");
		return MetadataCacheManager.countDriverSearch(searchParameters);
	}

	// public static int countRegexSearch(HashMap<String, Object>
	// searchParameters)
	// throws IOException, SolrServerException {
	// logger.info("Searching on cache with regex");
	// return MetadataCacheManager.countRegexSearch(searchParameters);
	// }

	public static int countRegexSearch(HashMap<String, Object> searchParameters)
			throws IOException, SolrServerException {
		logger.info("Searching on cache with Location");
		CachePersistenceManager jpa = new CachePersistenceManager();
		try {
			return jpa.jpaGetCountDatasetsByRegex(searchParameters);
		} finally {
			jpa.jpaClose();
		}
	}

	public static int countSpatialSearch(HashMap<String, Object> searchParameters)
			throws IOException, SolrServerException {
		logger.info("Searching on cache with Location");
		CachePersistenceManager jpa = new CachePersistenceManager();
		try {
			return jpa.jpaGetCountDatasetsByLocation(searchParameters);
		} finally {
			jpa.jpaClose();
		}
	}

	private static int countLocalSearch(HashMap<String, Object> searchParameters)
			throws IOException, SolrServerException {
		logger.info("Searching on cache");
		return MetadataCacheManager.getDatasetNumber(searchParameters);
	}

	private static int countLiveSearch(HashMap<String, Object> searchParameters)
			throws SQLException, ODMSCatalogueNotFoundException {

		// Gets federated nodes and relative connectors lists
		ArrayList<Integer> nodesToBeSearched = (ArrayList<Integer>) searchParameters.remove("nodes");

		// If the "nodes" parameter is not present, get all federated nodes
		if (nodesToBeSearched == null)
			nodesToBeSearched = ODMSManager.getODMSCataloguesID();

		// Divides the total passed rows for each node to be searched
		// Integer nodeRows = Math.round((int)
		// Integer.parseInt(searchParameters.remove("rows").toString())/nodesToBeSearched.size());
		// searchParameters.put("rows", nodeRows.toString());

		HashMap<ODMSCatalogueType, String> ODMSConnectorsList = FederationCore.getODMSConnectorsList();
		logger.info("Live search");
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		int globalResult = 0; // Results from all nodes

		int nodeDatasets;
		IODMSConnector currentConnector;

		// Iterates on federated nodes list and for each of them performs live
		// search
		for (Integer id : nodesToBeSearched) {
			ODMSCatalogue node = null;
			if ((node = ODMSManager.getODMSCatalogue(id)) != null
					&& !node.getFederationLevel().equals(ODMSCatalogueFederationLevel.LEVEL_0)
					&& !node.getFederationLevel().equals(ODMSCatalogueFederationLevel.LEVEL_2)
					&& node.getNodeState().equals(ODMSCatalogueState.ONLINE))
				try {
					// The connector class is loaded, based on ODMS node type,
					// using Java reflection
					Class<?> cls = loader.loadClass(ODMSConnectorsList.get(node.getNodeType()));
					currentConnector = (IODMSConnector) cls.getDeclaredConstructor(ODMSCatalogue.class).newInstance(node);
					Method m = cls.getDeclaredMethod("countSearchDatasets", HashMap.class);
					nodeDatasets = (Integer) m.invoke(currentConnector, searchParameters);

					globalResult += nodeDatasets;

					currentConnector = null;
					System.gc();
					logger.info("Live search success");
					// logger.info("Results found:" + globalResult.size());
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("There was an error in live search");
					// return 0;
				}
		}

		return globalResult;
	}

}
