/*******************************************************************************
 * Idra - Open Data Federation Platform
 * Copyright (C) 2021 Engineering Ingegneria Informatica S.p.A.
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
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

package it.eng.idra.search;

import it.eng.idra.beans.dcat.DcatDataset;
import it.eng.idra.beans.exception.EuroVocTranslationNotFoundException;
import it.eng.idra.beans.odms.OdmsCatalogue;
import it.eng.idra.beans.odms.OdmsCatalogueFederationLevel;
import it.eng.idra.beans.odms.OdmsCatalogueNotFoundException;
import it.eng.idra.beans.odms.OdmsCatalogueState;
import it.eng.idra.beans.odms.OdmsCatalogueType;
import it.eng.idra.beans.search.SearchResult;
import it.eng.idra.cache.CachePersistenceManager;
import it.eng.idra.cache.MetadataCacheManager;
import it.eng.idra.connectors.IodmsConnector;
import it.eng.idra.management.FederationCore;
import it.eng.idra.management.OdmsManager;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;

// TODO: Auto-generated Javadoc
/**
 * The Class FederatedSearch.
 */
public class FederatedSearch {

  /** The logger. */
  private static Logger logger = LogManager.getLogger(FederatedSearch.class);

  /**
   * Instantiates a new federated search.
   */
  private FederatedSearch() {
  }

  /**
   * Dump datasets.
   *
   * @return the search result
   * @throws IOException         Signals that an I/O exception has occurred.
   * @throws SolrServerException the solr server exception
   */
  public static SearchResult dumpDatasets() throws IOException, SolrServerException {

    return MetadataCacheManager.searchAllDatasets();

  }

  /**
   * Search.
   *
   * @param searchParameters the search parameters
   * @return the search result
   * @throws IOException                         Signals that an I/O exception has
   *                                             occurred.
   * @throws SolrServerException                 the solr server exception
   * @throws SQLException                        the SQL exception
   * @throws OdmsCatalogueNotFoundException      the odms catalogue not found
   *                                             exception
   * @throws EuroVocTranslationNotFoundException the euro voc translation not
   *                                             found exception
   */
  public static SearchResult search(HashMap<String, Object> searchParameters)
      throws IOException, SolrServerException, SQLException, OdmsCatalogueNotFoundException,
      EuroVocTranslationNotFoundException {

    // EuroVoc processing
    if (searchParameters.containsKey("euroVoc") && (boolean) searchParameters.get("euroVoc")) {
      searchParameters = EuroVocTranslator.replaceEuroVocTerms(searchParameters);
    }

    if ((boolean) searchParameters.remove("live")) {
      HashMap<String, Object> searchParameters1 = new HashMap<>(searchParameters);
      SearchResult result = new SearchResult(new Long(countLiveSearch(searchParameters1)),
          liveSearch(searchParameters).getResults());
      return result;
    } else {
      return localSearch(searchParameters);
    }
  }

  /**
   * Search by query.
   *
   * @param query   the query
   * @param sort    the sort
   * @param rows    the rows
   * @param offset  the offset
   * @param nodeIds the node IDS
   * @return the search result
   * @throws IOException                         Signals that an I/O exception has
   *                                             occurred.
   * @throws SolrServerException                 the solr server exception
   * @throws SQLException                        the SQL exception
   * @throws OdmsCatalogueNotFoundException      the odms catalogue not found
   *                                             exception
   * @throws EuroVocTranslationNotFoundException the euro voc translation not
   *                                             found exception
   */
  public static SearchResult searchByQuery(String query, String sort, int rows, int offset,
      List<String> nodeIds) throws IOException, SolrServerException, SQLException,
      OdmsCatalogueNotFoundException, EuroVocTranslationNotFoundException {

    return MetadataCacheManager.searchDatasetsByQuery(query, sort, rows, offset, nodeIds);
  }

  /**
   * Search driver.
   *
   * @param searchParameters the search parameters
   * @return the list
   * @throws IOException         Signals that an I/O exception has occurred.
   * @throws SolrServerException the solr server exception
   * @throws SQLException        the SQL exception
   */
  public static List<DcatDataset> searchDriver(HashMap<String, Object> searchParameters)
      throws IOException, SolrServerException, SQLException {
    logger.info("Search by resourceIDs and filters");
    return MetadataCacheManager.searchDriverDatasets(searchParameters);
  }

  /**
   * Gets the format statistics.
   *
   * @param searchParameters the search parameters
   * @return the format statistics
   * @throws IOException         Signals that an I/O exception has occurred.
   * @throws SolrServerException the solr server exception
   * @throws SQLException        the SQL exception
   */
  public static SearchResult getFormatStatistics(HashMap<String, Object> searchParameters)
      throws IOException, SolrServerException, SQLException {
    logger.info("Get Format Statistics");
    return MetadataCacheManager.searchForDistributionStatistics(searchParameters);
  }

  /**
   * Gets the licenses infos.
   *
   * @param searchParameters the search parameters
   * @return the licenses infos
   * @throws IOException         Signals that an I/O exception has occurred.
   * @throws SolrServerException the solr server exception
   * @throws SQLException        the SQL exception
   */
  public static HashMap<String, String> getLicensesInfos(HashMap<String, Object> searchParameters)
      throws IOException, SolrServerException, SQLException {
    logger.info("Get Licenses Infos");
    return MetadataCacheManager.getAllLicensesInfo(searchParameters);
  }

  /**
   * Regex search.
   *
   * @param searchParameters the search parameters
   * @return the list
   * @throws IOException  Signals that an I/O exception has occurred.
   * @throws SQLException the SQL exception
   */
  public static List<DcatDataset> regexSearch(HashMap<String, Object> searchParameters)
      throws IOException, SQLException {
    logger.info("Search by Location");
    CachePersistenceManager jpa = new CachePersistenceManager();

    List<DcatDataset> result = jpa.jpaGetDatasetsByRegex(searchParameters);
    jpa.jpaClose();
    jpa = null;
    return result;
  }

  /**
   * Spatial search.
   *
   * @param searchParameters the search parameters
   * @return the list
   * @throws IOException  Signals that an I/O exception has occurred.
   * @throws SQLException the SQL exception
   */
  public static List<DcatDataset> spatialSearch(HashMap<String, Object> searchParameters)
      throws IOException, SQLException {
    logger.info("Search by Location");
    CachePersistenceManager jpa = new CachePersistenceManager();

    List<DcatDataset> result = jpa.jpaGetDatasetsByLocation(searchParameters);
    jpa.jpaClose();
    jpa = null;
    return result;
  }

  /**
   * Local search.
   *
   * @param searchParameters the search parameters
   * @return the search result
   * @throws IOException         Signals that an I/O exception has occurred.
   * @throws SolrServerException the solr server exception
   */
  private static SearchResult localSearch(HashMap<String, Object> searchParameters)
      throws IOException, SolrServerException {
    logger.info("Searching on cache");
    return MetadataCacheManager.searchDatasets(searchParameters);
  }

  /**
   * Live search.
   *
   * @param searchParameters the search parameters
   * @return the search result
   * @throws SQLException                   the SQL exception
   * @throws OdmsCatalogueNotFoundException the odms catalogue not found exception
   */
  private static SearchResult liveSearch(HashMap<String, Object> searchParameters)
      throws SQLException, OdmsCatalogueNotFoundException {

    // Gets federated nodes and relative connectors lists
    ArrayList<Integer> nodesToBeSearched = (ArrayList<Integer>) searchParameters.remove("nodes");

    // If the "nodes" parameter is not present, get all federated nodes
    if (nodesToBeSearched == null) {
      nodesToBeSearched = OdmsManager.getOdmsCataloguesId();
    }

    // Divides the total passed rows for each node to be searched
    // Integer nodeRows = Math.round((int)
    // Integer.parseInt(searchParameters.remove("rows").toString())/nodesToBeSearched.size());
    // searchParameters.put("rows", nodeRows.toString());

    HashMap<OdmsCatalogueType, String> odmsConnectorsList = FederationCore.getOdmsConnectorsList();
    logger.info("Live search");
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    List<DcatDataset> globalResult = new ArrayList<DcatDataset>(); // Results
    // from
    // all
    // nodes

    List<DcatDataset> nodeDatasets;
    IodmsConnector currentConnector;
    int allResults = 0;
    boolean flag = false;
    // Iterates on federated nodes list and for each of them performs live
    // search
    for (Integer id : nodesToBeSearched) {
      OdmsCatalogue node = null;
      if ((node = OdmsManager.getOdmsCatalogue(id)) != null
          && !node.getFederationLevel().equals(OdmsCatalogueFederationLevel.LEVEL_0)
          && !node.getFederationLevel().equals(OdmsCatalogueFederationLevel.LEVEL_2)
          && node.getNodeState().equals(OdmsCatalogueState.ONLINE)) {
        try {
          // logger.info("\n\nNODE: "+node.getName());
          // The connector class is loaded, based on ODMS node type,
          // using Java reflection
          Class<?> cls = loader.loadClass(odmsConnectorsList.get(node.getNodeType()));
          currentConnector = (IodmsConnector) cls.getDeclaredConstructor(OdmsCatalogue.class)
              .newInstance(node);

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
          if (result != 0
              && allResults >= Integer.parseInt((String) searchParameters.get("start"))) {

            if (flag) {
              // System.out.println("FLAG==TRUE");
              page -= Math.ceil((allResults - result)
                  / (Integer.parseInt((String) searchParameters.get("rows"))));

              // System.out.println("PAGE: " + page);

              page--;

              // System.out.println("PAGE: " + page);

              int newStart = Integer.parseInt((String) searchParameters.get("rows")) * page
                  + Integer.parseInt((String) searchParameters.get("rows"))
                  - (allResults - result) % Integer.parseInt((String) searchParameters.get("rows"));

              if (newStart < 0) {
                newStart = 0;
              }
              // System.out.println("NEW START: " + newStart);
              // logger.info(newStart);
              // Set new offset
              searchParameters.put("start", Integer.toString(newStart));

            }

            m = cls.getDeclaredMethod("findDatasets", HashMap.class);
            Object currentSearchParameters = searchParameters.clone();
            nodeDatasets = (List<DcatDataset>) m.invoke(currentConnector, currentSearchParameters);

            globalResult.addAll(nodeDatasets);
            if (nodeDatasets.size() < Integer.parseInt((String) searchParameters.get("rows"))) {

              int newRows = Integer.parseInt((String) searchParameters.get("rows"))
                  - nodeDatasets.size();
              searchParameters.put("rows", Integer.toString(newRows));
              searchParameters.put("start", "0");
              flag = false;
            } else {
              break;
            }

          } else {
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
      }

    }

    // Before returning the global result from all nodes, it performs the
    // sort on it
    // Sorting
    String[] params = new String[2];

    if (searchParameters.containsKey("sort")) {
      params = ((String) searchParameters.get("sort")).split(",");
    } else {
      params[0] = "nodeID";
      params[1] = "asc";
    }

    if (params[1].equals("desc")) {
      Collections.sort(globalResult, DatasetComparator.decending(DatasetComparator
          .getComparator(DatasetComparator.valueOf(params[0].toUpperCase() + "_SORT"))));
    } else {
      Collections.sort(globalResult, DatasetComparator
          .getComparator(DatasetComparator.valueOf(params[0].toUpperCase() + "_SORT")));
    }

    return new SearchResult(new Long(globalResult.size()), globalResult);
  }

  /**
   * Count dataset.
   *
   * @param searchParameters the search parameters
   * @return the int
   * @throws IOException                         Signals that an I/O exception has
   *                                             occurred.
   * @throws SolrServerException                 the solr server exception
   * @throws SQLException                        the SQL exception
   * @throws OdmsCatalogueNotFoundException      the odms catalogue not found
   *                                             exception
   * @throws EuroVocTranslationNotFoundException the euro voc translation not
   *                                             found exception
   */
  public static int countDataset(HashMap<String, Object> searchParameters)
      throws IOException, SolrServerException, SQLException, OdmsCatalogueNotFoundException,
      EuroVocTranslationNotFoundException {

    // EuroVoc processing
    if (searchParameters.containsKey("euroVoc") && (boolean) searchParameters.get("euroVoc")) {
      searchParameters = EuroVocTranslator.replaceEuroVocTerms(searchParameters);
    }

    if ((boolean) searchParameters.remove("live")) {
      return countLiveSearch(searchParameters);
    } else {
      return countLocalSearch(searchParameters);
    }
  }

  /**
   * Count driver search.
   *
   * @param searchParameters the search parameters
   * @return the int
   * @throws IOException         Signals that an I/O exception has occurred.
   * @throws SolrServerException the solr server exception
   */
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

  /**
   * Count regex search.
   *
   * @param searchParameters the search parameters
   * @return the int
   * @throws IOException         Signals that an I/O exception has occurred.
   * @throws SolrServerException the solr server exception
   */
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

  /**
   * Count spatial search.
   *
   * @param searchParameters the search parameters
   * @return the int
   * @throws IOException         Signals that an I/O exception has occurred.
   * @throws SolrServerException the solr server exception
   */
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

  /**
   * Count local search.
   *
   * @param searchParameters the search parameters
   * @return the int
   * @throws IOException         Signals that an I/O exception has occurred.
   * @throws SolrServerException the solr server exception
   */
  private static int countLocalSearch(HashMap<String, Object> searchParameters)
      throws IOException, SolrServerException {
    logger.info("Searching on cache");
    return MetadataCacheManager.getDatasetNumber(searchParameters);
  }

  /**
   * Count live search.
   *
   * @param searchParameters the search parameters
   * @return the int
   * @throws SQLException                   the SQL exception
   * @throws OdmsCatalogueNotFoundException the odms catalogue not found exception
   */
  private static int countLiveSearch(HashMap<String, Object> searchParameters)
      throws SQLException, OdmsCatalogueNotFoundException {

    // Gets federated nodes and relative connectors lists
    ArrayList<Integer> nodesToBeSearched = (ArrayList<Integer>) searchParameters.remove("nodes");

    // If the "nodes" parameter is not present, get all federated nodes
    if (nodesToBeSearched == null) {
      nodesToBeSearched = OdmsManager.getOdmsCataloguesId();
    }

    // Divides the total passed rows for each node to be searched
    // Integer nodeRows = Math.round((int)
    // Integer.parseInt(searchParameters.remove("rows").toString())/nodesToBeSearched.size());
    // searchParameters.put("rows", nodeRows.toString());

    HashMap<OdmsCatalogueType, String> odmsConnectorsList = FederationCore.getOdmsConnectorsList();
    logger.info("Live search");
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    int globalResult = 0; // Results from all nodes

    int nodeDatasets;
    IodmsConnector currentConnector;

    // Iterates on federated nodes list and for each of them performs live
    // search
    for (Integer id : nodesToBeSearched) {
      OdmsCatalogue node = null;
      if ((node = OdmsManager.getOdmsCatalogue(id)) != null
          && !node.getFederationLevel().equals(OdmsCatalogueFederationLevel.LEVEL_0)
          && !node.getFederationLevel().equals(OdmsCatalogueFederationLevel.LEVEL_2)
          && node.getNodeState().equals(OdmsCatalogueState.ONLINE)) {
        try {
          // The connector class is loaded, based on ODMS node type,
          // using Java reflection
          Class<?> cls = loader.loadClass(odmsConnectorsList.get(node.getNodeType()));
          currentConnector = (IodmsConnector) cls.getDeclaredConstructor(OdmsCatalogue.class)
              .newInstance(node);
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
    }

    return globalResult;
  }

}
