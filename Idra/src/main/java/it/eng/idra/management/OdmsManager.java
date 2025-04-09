/*******************************************************************************
 * Idra - Open Data Federation Platform
 * Copyright (C) 2024 Engineering Ingegneria Informatica S.p.A.
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

package it.eng.idra.management;

import it.eng.idra.beans.IdraProperty;
import it.eng.idra.beans.dcat.DcatApFormat;
import it.eng.idra.beans.odms.OdmsAlreadyPresentException;
import it.eng.idra.beans.odms.OdmsCatalogue;
import it.eng.idra.beans.odms.OdmsCatalogueFederationLevel;
import it.eng.idra.beans.odms.OdmsCatalogueForbiddenException;
import it.eng.idra.beans.odms.OdmsCatalogueImage;
import it.eng.idra.beans.odms.OdmsCatalogueMessage;
import it.eng.idra.beans.odms.OdmsCatalogueNotFoundException;
import it.eng.idra.beans.odms.OdmsCatalogueOfflineException;
import it.eng.idra.beans.odms.OdmsCatalogueState;
import it.eng.idra.beans.odms.OdmsCatalogueType;
import it.eng.idra.beans.odms.OdmsManagerException;
import it.eng.idra.beans.odms.OdmsSynchLock;
import it.eng.idra.beans.orion.OrionCatalogueConfiguration;
import it.eng.idra.connectors.IodmsConnector;
import it.eng.idra.dcat.dump.DcatApDeserializer;
import it.eng.idra.dcat.dump.DcatApItDeserializer;
import it.eng.idra.dcat.dump.DcatApSerializer;
import it.eng.idra.utils.CommonUtil;
import it.eng.idra.utils.PropertyManager;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class OdmsManager.
 */
public class OdmsManager {

  /** The logger. */
  private static Logger logger = LogManager.getLogger(OdmsManager.class);

  /** The federated nodes. */
  private static List<OdmsCatalogue> federatedNodes = new ArrayList<OdmsCatalogue>();

  /** The ODMS connectors list. */
  private static HashMap<OdmsCatalogueType, String> ODMSConnectorsList = 
      new HashMap<OdmsCatalogueType, String>();
  
  /** The get nodes lock. */
  private static boolean getNodesLock = false;
  // private static PersistenceManager jpa;

  /**
   * Instantiates a new odms manager.
   */
  private OdmsManager() {
  }

  static {
    try {

      federatedNodes = getOdmsCataloguesfromDb(false);

      logger.info("Federated Nodes: " + federatedNodes.size());
      ODMSConnectorsList.put(OdmsCatalogueType.CKAN, "it.eng.idra.connectors.CkanConnector");
      ODMSConnectorsList.put(OdmsCatalogueType.SOCRATA, "it.eng.idra.connectors.SocrataConnector");
      ODMSConnectorsList.put(OdmsCatalogueType.NATIVE,
          "it.eng.idra.connectors.OpenDataFederationNativeConnector");
      ODMSConnectorsList.put(OdmsCatalogueType.WEB, "it.eng.idra.connectors.WebConnector");
      ODMSConnectorsList.put(OdmsCatalogueType.DCATDUMP,
          "it.eng.idra.connectors.DcatDumpConnector");
      ODMSConnectorsList.put(OdmsCatalogueType.DKAN, "it.eng.idra.connectors.DkanConnector");
      ODMSConnectorsList.put(OdmsCatalogueType.ORION, "it.eng.idra.connectors.OrionConnector");
      ODMSConnectorsList.put(OdmsCatalogueType.NGSILD_CB, 
          "it.eng.idra.connectors.NgsiLdCbDcatConnector");
      ODMSConnectorsList.put(OdmsCatalogueType.SPARQL, "it.eng.idra.connectors.SparqlConnector");
      ODMSConnectorsList.put(OdmsCatalogueType.SPOD, "it.eng.idra.connectors.SpodConnector");
      ODMSConnectorsList.put(OdmsCatalogueType.OPENDATASOFT,
          "it.eng.idra.connectors.OpenDataSoftConnector");
      ODMSConnectorsList.put(OdmsCatalogueType.JUNAR, "it.eng.idra.connectors.JunarConnector");
      ODMSConnectorsList.put(OdmsCatalogueType.ZENODO, "it.eng.idra.connectors.ZenodoConnector");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Gets the odms cataloguesfrom db.
   *
   * @param withImage the with image
   * @return the odms cataloguesfrom db
   * @throws SQLException the SQL exception
   */
  private static List<OdmsCatalogue> getOdmsCataloguesfromDb(boolean withImage)
      throws SQLException {
    PersistenceManager jpa = new PersistenceManager();
    List<OdmsCatalogue> resultNodes = null;
    try {
      resultNodes = jpa.jpaGetOdmsCatalogues(withImage);

      return resultNodes;
    } finally {
      jpa.jpaClose();
    }
  }

  /**
   * Gets the odms cataloguefrom db.
   *
   * @param id        the id
   * @param withImage the with image
   * @return the odms cataloguefrom db
   * @throws SQLException the SQL exception
   */
  private static OdmsCatalogue getOdmsCataloguefromDb(int id, boolean withImage)
      throws SQLException {
    PersistenceManager jpa = new PersistenceManager();
    OdmsCatalogue resultNode = null;
    try {
      resultNode = jpa.jpaGetOdmsCatalogue(id, withImage);

      return resultNode;
    } finally {
      jpa.jpaClose();
    }
  }

  /**
   * Gets the odms catalogues.
   *
   * @return the odms catalogues
   */
  public static List<OdmsCatalogue> getOdmsCataloguesList() {

    while (getNodesLock) {
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {

        e.printStackTrace();
      }
    }

    return federatedNodes;

  }

  /**
   * Gets the odms catalogue idby name.
   *
   * @param nodeName the node name
   * @return the odms catalogue idby name
   */
  public static Integer getOdmsCatalogueIdbyName(String nodeName) {
    try {
      return federatedNodes.stream().filter(x -> x.getName().equals(nodeName)).findFirst().get()
          .getId();
    } catch (Exception e) {
      return federatedNodes.stream().map(x -> x.getId()).collect(Collectors.toList()).stream()
          .max(Integer::compare).get() + 1;
    }
  }

  /**
   * Update odms catalogue list.
   *
   * @throws SQLException the SQL exception
   */
  public static void updateOdmsCatalogueList() throws SQLException {
    federatedNodes = getOdmsCataloguesfromDb(false);
  }

  /**
   * Gets the odms catalogues.
   *
   * @param withImage the with image
   * @return the odms catalogues
   * @throws OdmsManagerException the odms manager exception
   */
  public static List<OdmsCatalogue> getOdmsCatalogues(boolean withImage)
      throws OdmsManagerException {

    while (getNodesLock) {
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {

        e.printStackTrace();
      }
    }

    if (!withImage) {
      return federatedNodes;
    } else {
      try {
        return getOdmsCataloguesfromDb(withImage);

      } catch (SQLException e) {

        throw new OdmsManagerException("There was an error while retrieving ODMS nodes");

      }

    }

  }

  /**
   * Gets the odms catalogues id.
   *
   * @return the odms catalogues id
   */
  public static ArrayList<Integer> getOdmsCataloguesId() {
    ArrayList<Integer> nodesIdList = new ArrayList<Integer>();

    for (OdmsCatalogue node : federatedNodes) {
      nodesIdList.add(node.getId());
    }

    return nodesIdList;
  }

  /**
   * Gets a federated ODMS node present in the Federation Forwards the request to
   * the underlying JDBC layer.
   *
   * @param id Id of requested federated node
   * @return the ODMS catalogue
   * @throws OdmsCatalogueNotFoundException the odms catalogue not found exception
   * @returns the resulting federated ODMS node
   */

  public static OdmsCatalogue getOdmsCatalogue(int id) throws OdmsCatalogueNotFoundException {

    try {
      return federatedNodes.get(federatedNodes.indexOf((new OdmsCatalogue(id))));
    } catch (IndexOutOfBoundsException | NullPointerException e) {
      throw new OdmsCatalogueNotFoundException("The ODMS node does not exist in the federation!");
    }

  }

  /**
   * Gets the ODMS catalogue.
   *
   * @param id        the id
   * @param withImage the with image
   * @return the ODMS catalogue
   * @throws OdmsCatalogueNotFoundException the odms catalogue not found exception
   * @throws OdmsManagerException           the odms manager exception
   */
  public static OdmsCatalogue getOdmsCatalogue(int id, boolean withImage)
      throws OdmsCatalogueNotFoundException, OdmsManagerException {

    if (!withImage) {
      try {
        return federatedNodes.get(federatedNodes.indexOf((new OdmsCatalogue(id))));
      } catch (IndexOutOfBoundsException | NullPointerException e) {
        throw new OdmsCatalogueNotFoundException("The ODMS node does not exist in the federation!");
      }
    } else {
      try {
        return getOdmsCataloguefromDb(id, withImage);
      } catch (SQLException e) {
        throw new OdmsManagerException("There was an error while retrieving ODMS nodes");
      }
    }
  }

  /**
   * Gets the specific connector instance of a federated ODMS node present in the
   * Federation by using Java Reflection.
   *
   * @param node the node
   * @return the ODMS catalogue connector
   * @throws OdmsManagerException the odms manager exception
   * @returns the connector instance of federated ODMS node
   */
  public static IodmsConnector getOdmsCatalogueConnector(OdmsCatalogue node)
      throws OdmsManagerException {

    // The connector class is loaded, based on ODMS node type, using Java
    // reflection
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    Class<?> cls;
    try {
      cls = loader.loadClass(ODMSConnectorsList.get(node.getNodeType()));

      return (IodmsConnector) cls.getDeclaredConstructor(OdmsCatalogue.class).newInstance(node);
    } catch (Exception e) {
      e.printStackTrace();
      throw new OdmsManagerException(
          "There was an error while " + "retrieving ODMS Connector: " + e.getMessage());
    }

  }

  /**
   * Gets the List of federated ODMS nodes with a specific federation grade
   * present in the Federation Forwards the request to the underlying JDBC layer.
   *
   * @param integrationLevelFirst  the integration level first
   * @param integrationLevelSecond the integration level second
   * @return the ODMS cataloguesby federation level
   * @returns the list of the federated ODMS nodes
   */
  public static List<OdmsCatalogue> getOdmsCataloguesbyFederationLevel(
      OdmsCatalogueFederationLevel integrationLevelFirst,
      OdmsCatalogueFederationLevel integrationLevelSecond) {

    return federatedNodes.stream()
        .filter(node -> node.getFederationLevel().equals(integrationLevelFirst)
            || node.getFederationLevel().equals(integrationLevelSecond))
        .collect(Collectors.toList());

  }

  /**
   * Gets the List of federated ODMS nodes with a minimum federation level of 1
   * present in the Federation Forwards the request to the underlying JDBC layer.
   *
   * @return the ODMS cataloguesby federation level one
   * @returns the list of the federated ODMS nodes with minimum Level 1
   */
  public static List<OdmsCatalogue> getOdmsCataloguesbyFederationLevelOne() {

    return federatedNodes.stream()
        .filter(node -> !node.getFederationLevel().equals(OdmsCatalogueFederationLevel.LEVEL_0))
        .collect(Collectors.toList());

  }

  /**
   * Adds a federated ODMS node to the Federation Sends a request to CKAN node to
   * retrieve the datasets count Updates the dataset count of the node Forwards
   * the request to the underlying JDBC layer.
   *
   * @param node The ODMSCatalogue object to add
   * @return the int
   * @throws OdmsAlreadyPresentException     the odms already present exception
   * @throws OdmsCatalogueNotFoundException  the odms catalogue not found
   *                                         exception
   * @throws OdmsCatalogueOfflineException   the odms catalogue offline exception
   * @throws OdmsCatalogueForbiddenException the odms catalogue forbidden
   *                                         exception
   * @throws OdmsManagerException            the odms manager exception
   * @returns Node itself with assigned Dataset Count and id
   */
  public static int addOdmsCatalogue(OdmsCatalogue node)
      throws OdmsAlreadyPresentException, OdmsCatalogueNotFoundException,
      OdmsCatalogueOfflineException, OdmsCatalogueForbiddenException, OdmsManagerException {

    // Sets the lock true, in order to avoid node retrieval until the new
    // node is created
    getNodesLock = true;

    int assignedNodeId;
    int datasetsCount = 0;

    if (!federatedNodes.contains(node)) {
      PersistenceManager jpa = new PersistenceManager();

      try {
        if (node.isActive()) {
          node.setNodeState(OdmsCatalogueState.ONLINE);

          if (node.isCacheable()) {
            node.setSynchLock(OdmsSynchLock.FIRST);
          } else {
            node.setSynchLock(OdmsSynchLock.NONE);
          }

          /*
           * Socrata and DKAN do not support initial datasets count, it is provided during
           * the first synchronization.
           */

          // if (!node.getNodeType().equals(ODMSCatalogueType.SOCRATA)
          // && !node.getNodeType().equals(ODMSCatalogueType.DKAN))
          // datasetsCount = getODMSCatalogueConnector(node).countDatasets();
          //
          // if (!(datasetsCount > 0))
          // node.setNodeState(ODMSCatalogueState.OFFLINE);

          node.setDatasetCount(datasetsCount);

          /*
           * If there is no Catalogue image fill the Catalogue with an empty one
           */
          if (node.getImage() == null || StringUtils.isBlank(node.getImage().getImageData())) {
            node.setImage(new OdmsCatalogueImage("data:image/png;base64,"));
          }

          /*
           * Persist the node and return the ID assigned by the Entity Manager
           */
          assignedNodeId = jpa.jpaInsertOdmsCatalogue(node);
          node.setId(assignedNodeId);

          /*
           * Unlock the Get nodes and add the persisted Node in the global Federated Nodes
           * list
           */
          getNodesLock = false;
          federatedNodes.add(node);

          return assignedNodeId;

        } else {

          node.setNodeState(OdmsCatalogueState.OFFLINE);
          node.setSynchLock(OdmsSynchLock.NONE);
          node.setDatasetCount(0);

          assignedNodeId = jpa.jpaInsertOdmsCatalogue(node);
          boolean updateNode = false;
          if (node.getNodeType().equals(OdmsCatalogueType.DCATDUMP)) {
            if (StringUtils.isNotBlank(node.getDumpString())) {
              Model m = null;
              switch (node.getDcatProfile()) {
                case DCATAP_IT:
                  m = new DcatApItDeserializer().dumpToModel(node.getDumpString(), node);
                  break;
                default:
                  // If no profile was provided, instantiate a base DCATAP Deserializer
                  m = new DcatApDeserializer().dumpToModel(node.getDumpString(), node);
                  break;
              }

              String odmsDumpFilePath = PropertyManager
                  .getProperty(IdraProperty.ODMS_DUMP_FILE_PATH);
              try {
                DcatApSerializer.writeModelToFile(m, DcatApFormat.RDFXML, odmsDumpFilePath,
                    "dumpFileString_" + assignedNodeId);
                node.setDumpFilePath(odmsDumpFilePath + "dumpFileString_" + assignedNodeId);
              } catch (IOException e) {
                e.printStackTrace();
              }

            }
            
            updateNode = true;
          } else if (node.getNodeType().equals(OdmsCatalogueType.ORION)) {

            String orionDumpFilePath = PropertyManager
                .getProperty(IdraProperty.ORION_FILE_DUMP_PATH);

            try {
              OrionCatalogueConfiguration orionConfig = (OrionCatalogueConfiguration) node
                  .getAdditionalConfig();
              CommonUtil.storeFile(orionDumpFilePath, "orionDump_" + assignedNodeId,
                  orionConfig.getOrionDatasetDumpString());
              orionConfig
                  .setOrionDatasetFilePath(orionDumpFilePath + "orionDump_" + assignedNodeId);
              node.setAdditionalConfig(orionConfig);
              updateNode = true;
            } catch (IOException e) {
              e.printStackTrace();
            }

          } else if (node.getNodeType().equals(OdmsCatalogueType.SPARQL)) {

            String dumpFilePath = PropertyManager.getProperty(IdraProperty.ORION_FILE_DUMP_PATH);

            try {
              OrionCatalogueConfiguration orionConfig = (OrionCatalogueConfiguration) node
                  .getAdditionalConfig();
              CommonUtil.storeFile(dumpFilePath, "sparqlDump_" + assignedNodeId,
                  orionConfig.getOrionDatasetDumpString());
              orionConfig.setOrionDatasetFilePath(dumpFilePath + "sparqlDump_" + assignedNodeId);
              node.setAdditionalConfig(orionConfig);
              updateNode = true;
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
          if (updateNode) {
            updateInactiveOdmsCatalogue(node);
          }
          getNodesLock = false;
          federatedNodes.add(node);

          return assignedNodeId;
        }
      } catch (OdmsCatalogueNotFoundException e) {
        throw e;
      } catch (Exception e) {
        e.printStackTrace();
        throw new OdmsManagerException(
            "There was an error " + "while adding the ODMS Node: " + e.getMessage());
      } finally {
        getNodesLock = false;
        jpa.jpaClose();
      }

    } else {
      getNodesLock = false;
      throw new OdmsAlreadyPresentException("ODMS Node is already present");
    }

  }

  /**
   * Gets the inactive ODMS catalogue.
   *
   * @param id        the id
   * @param withImage the with image
   * @return the inactive ODMS catalogue
   * @throws OdmsManagerException the odms manager exception
   */
  public static OdmsCatalogue getInactiveOdmsCatalogue(int id, boolean withImage)
      throws OdmsManagerException {
    PersistenceManager jpa = new PersistenceManager();
    try {

      OdmsCatalogue resultNode = null;
      resultNode = jpa.jpaGetInactiveOdmsCatalogue(id, withImage);
      return resultNode;

    } finally {
      jpa.jpaClose();
    }

  }

  /**
   * Gets the all inactive ODMS catalogue.
   *
   * @param withImage the with image
   * @return the all inactive ODMS catalogue
   * @throws OdmsManagerException the odms manager exception
   */
  public static List<OdmsCatalogue> getAllInactiveOdmsCatalogue(boolean withImage)
      throws OdmsManagerException {
    PersistenceManager jpa = new PersistenceManager();
    try {

      List<OdmsCatalogue> resultNode = null;
      resultNode = jpa.jpaGetAllInactiveOdmsCatalogue(withImage);
      return resultNode;

    } finally {
      jpa.jpaClose();
    }

  }

  /**
   * Adds the inactive ODMS catalogue.
   *
   * @param node the node
   * @return the int
   * @throws OdmsManagerException the odms manager exception
   */
  public static int addInactiveOdmsCatalogue(OdmsCatalogue node) throws OdmsManagerException {

    PersistenceManager jpa = new PersistenceManager();

    try {

      node.setNodeState(OdmsCatalogueState.OFFLINE);
      node.setSynchLock(OdmsSynchLock.NONE);
      node.setDatasetCount(0);
      int assignedNodeId = jpa.jpaInsertOdmsCatalogue(node);
      return assignedNodeId;

    } catch (Exception e) {
      e.printStackTrace();
      throw new OdmsManagerException(
          "There was an error" + " while adding the ODMS Node: " + e.getMessage());
    } finally {
      jpa.jpaClose();
    }

  }

  /**
   * Removes a federated ODMS node present in the Federation Forwards the request
   * to the underlying JDBC layer.
   *
   * @param node The ODMSCatalogue object to remove
   * @throws OdmsManagerException           the odms manager exception
   * @throws OdmsCatalogueNotFoundException the odms catalogue not found exception
   * @returns void
   */
  public static void deleteOdmsCatalogue(OdmsCatalogue node)
      throws OdmsManagerException, OdmsCatalogueNotFoundException {

    PersistenceManager jpa = new PersistenceManager();
    try {

      jpa.jpaDeleteOdmsCatalogue(node.getId());
      federatedNodes.remove(federatedNodes.indexOf(node));

    } catch (IndexOutOfBoundsException e) {
      throw new OdmsCatalogueNotFoundException("ODMSCatalogue is not present");
    } catch (Exception e) {
      throw new OdmsManagerException(
          "There was an " + "error while deleting the ODMS Node: " + e.getMessage());
    } finally {
      jpa.jpaClose();
    }

  }

  /**
   * Edits a federated ODMS node present in the Federation Forwards the request to
   * the underlying JDBC layer for all specified values to change.
   *
   * @param node    ODMSCatalogue to change
   * @param persist Flag to propagate or not the changed node to
   * @throws OdmsCatalogueNotFoundException the odms catalogue not found exception
   * @throws OdmsManagerException           the odms manager exception
   * @returns void
   */
  public static void updateOdmsCatalogue(OdmsCatalogue node, boolean persist)

    throws OdmsCatalogueNotFoundException, OdmsManagerException {
      try {
        if (federatedNodes.remove(node)) {
          if (persist) {
            PersistenceManager jpa = new PersistenceManager();
            try {
              jpa.jpaUpdateOdmsCatalogue(node);
            } catch (Exception e) {
              throw new OdmsManagerException(
                  "There " + "was an error while updating the ODMS Node: " + e.getMessage());
            } finally {
              jpa.jpaClose();
            }
          }
          federatedNodes.add(node);

        } else {
          throw new OdmsCatalogueNotFoundException("The ODMS node does not exist!");
        }
      } catch (Exception e) {
        throw new OdmsManagerException(
            "There " + "was an error while updating the ODMS Node: " + e.getMessage());
      } 
  }

  /**
   * Update inactive ODMS catalogue.
   *
   * @param node the node
   * @throws OdmsCatalogueNotFoundException the odms catalogue not found exception
   * @throws OdmsManagerException           the odms manager exception
   */
  public static void updateInactiveOdmsCatalogue(OdmsCatalogue node)
      throws OdmsCatalogueNotFoundException, OdmsManagerException {

    PersistenceManager jpa = new PersistenceManager();
    try {
      jpa.jpaUpdateOdmsCatalogue(node);
    } catch (Exception e) {
      throw new OdmsManagerException(
          "There was an error " + "while updating the ODMS Node: " + e.getMessage());
    } finally {
      jpa.jpaClose();
    }
  }

  /**
   * Adds the federated ODMS catalogue to list.
   *
   * @param node the node
   * @throws OdmsAlreadyPresentException the odms already present exception
   */
  public static void addFederatedOdmsCatalogueToList(OdmsCatalogue node)
      throws OdmsAlreadyPresentException {
    if (!federatedNodes.contains(node)) {
      federatedNodes.add(node);
    } else {
      throw new OdmsAlreadyPresentException("ODMS Node is already present");
    }
  }

  /**
   * Removes the federated ODMS catalogue from list.
   *
   * @param node the node
   * @throws OdmsCatalogueNotFoundException the odms catalogue not found exception
   */
  public static void removeFederatedOdmsCatalogueFromList(OdmsCatalogue node)
      throws OdmsCatalogueNotFoundException {
    if (federatedNodes.contains(node)) {
      federatedNodes.remove(node);
    } else {
      throw new OdmsCatalogueNotFoundException("ODMS Node not found");
    }
  }

  /**
   * Checks if a node is online by requesting its datasets count Forwards the
   * request to the appropriate ODMS Connector.
   *
   * @param node the ODMSCatalogue to be verified
   * @return the odms catalogue state
   * @throws OdmsCatalogueNotFoundException the odms catalogue not found exception
   * @throws OdmsManagerException           the odms manager exception
   * @throws SecurityException              the security exception
   * @throws IllegalArgumentException       the illegal argument exception
   * @returns {@link OdmsCatalogueState}
   */
  public static OdmsCatalogueState checkOdmsCatalogue(OdmsCatalogue node)
      throws OdmsCatalogueNotFoundException, OdmsManagerException {

    try {
      /*
       * Check availability through dataset count of the node and then related node
       * State TODO Provide a specific method "getNodeState" in ODMSNodeConnector
       * interface?
       */

      return getOdmsCatalogueConnector(node).countDatasets() != 0 ? OdmsCatalogueState.ONLINE
          : OdmsCatalogueState.OFFLINE;

    } catch (Exception e) {
      // e.printStackTrace();
      logger.error("Check catalogue raiser: " + e.getLocalizedMessage());
      logger.info("Check node: Setting node state to OFFLINE");
      node.setNodeState(OdmsCatalogueState.OFFLINE);
      OdmsManager.updateOdmsCatalogue(node, true);
      return OdmsCatalogueState.OFFLINE;
      // Since the this method is used during the synchronization, the node is already
      // federated
      // and it must not be removed
      /*
       * else if (e.getClass().equals(ODMSCatalogueNotFoundException.class)) {
       * 
       * logger.info("Check Node: The node host was not found");
       * federatedNodes.remove(federatedNodes.indexOf(node)); throw new
       * ODMSCatalogueNotFoundException("The node host was not found");
       * 
       * }
       */
    }
  }

  /**
   * Count ODMS catalogue datasets.
   *
   * @param node the node
   * @return the int
   * @throws OdmsCatalogueNotFoundException the odms catalogue not found exception
   * @throws OdmsManagerException           the odms manager exception
   */
  public static int countOdmsCatalogueDatasets(OdmsCatalogue node)
      throws OdmsCatalogueNotFoundException, OdmsManagerException {

    try {
      /*
       * Check availability through dataset count of the node and then related node
       * State TODO Provide a specific method "getNodeState" in ODMSNodeConnector
       * interface?
       */

      return getOdmsCatalogueConnector(node).countDatasets();

    } catch (Exception e) {

      if (e.getClass().equals(OdmsCatalogueOfflineException.class)
          || e.getClass().equals(OdmsCatalogueNotFoundException.class)) {

        logger.info("Check node: Setting node state to OFFLINE");
        node.setNodeState(OdmsCatalogueState.OFFLINE);
        OdmsManager.updateOdmsCatalogue(node, true);
        return 0;

      } else {
        throw new OdmsManagerException(
            "There was " + "an error while checking the ODMS Node: " + e.getMessage());
      }
    }
  }

  /**
   * Gets the list of all possible Connector types.
   *
   * @return the odms connectors list
   */
  protected static HashMap<OdmsCatalogueType, String> getOdmsConnectorsList() {
    return ODMSConnectorsList;
  }

  /**
   * Gets the all ODMS messages count.
   *
   * @return the all ODMS messages count
   * @throws OdmsManagerException the odms manager exception
   */
  public static HashMap<Integer, Long> getAllOdmsMessagesCount() throws OdmsManagerException {
    PersistenceManager manageBeansJpa = new PersistenceManager();
    try {

      return manageBeansJpa.jpaGetMessagesCount(
          getOdmsCataloguesList().stream().map(node -> node.getId()).collect(Collectors.toList()));

    } catch (Exception e) {
      throw new OdmsManagerException(
          "There was an error " + "while getting ODMS Nodes messages: " + e.getMessage());
    } finally {
      manageBeansJpa.jpaClose();
    }
  }

  /**
   * Gets the ODMS messages.
   *
   * @param nodeId the node ID
   * @return the ODMS messages
   * @throws OdmsManagerException the odms manager exception
   */
  public static List<OdmsCatalogueMessage> getOdmsMessages(int nodeId) throws OdmsManagerException {
    PersistenceManager manageBeansJpa = new PersistenceManager();
    try {

      return manageBeansJpa.jpaGetOdmsMessagesByNode(nodeId);

    } catch (Exception e) {
      throw new OdmsManagerException(
          "There was " + "an error while getting ODMS Node messages: " + e.getMessage());
    } finally {
      manageBeansJpa.jpaClose();
    }
  }

  /**
   * Gets the ODMS message.
   *
   * @param nodeId    the node ID
   * @param messageId the message ID
   * @return the ODMS message
   * @throws OdmsManagerException the odms manager exception
   */
  public static OdmsCatalogueMessage getOdmsMessage(int nodeId, int messageId)
      throws OdmsManagerException {
    PersistenceManager manageBeansJpa = new PersistenceManager();
    try {
      return manageBeansJpa.jpaGetOdmsMessage(messageId, nodeId);
    } catch (Exception e) {
      throw new OdmsManagerException(
          "There was an " + "error while getting ODMS Node message: " + e.getMessage());
    } finally {
      manageBeansJpa.jpaClose();
    }
  }

  /**
   * Delete all ODMS message.
   *
   * @param nodeId the node ID
   * @throws OdmsManagerException the odms manager exception
   */
  public static void deleteAllOdmsMessage(int nodeId) throws OdmsManagerException {
    PersistenceManager manageBeansJpa = new PersistenceManager();
    try {
      manageBeansJpa.jpaDeleteAllOdmsMessage(nodeId);
    } catch (Exception e) {
      throw new OdmsManagerException(
          "There was an error " + "while deleting ODMS Nodes messages: " + e.getMessage());
    } finally {
      manageBeansJpa.jpaClose();
    }
  }

  /**
   * Delete ODMS message.
   *
   * @param nodeId    the node ID
   * @param messageId the message ID
   * @throws OdmsManagerException the odms manager exception
   */
  public static void deleteOdmsMessage(int nodeId, int messageId) throws OdmsManagerException {
    PersistenceManager manageBeansJpa = new PersistenceManager();
    try {
      manageBeansJpa.jpaDeleteOdmsMessage(messageId, nodeId);
    } catch (Exception e) {
      throw new OdmsManagerException(
          "There was an error" + " while deleting ODMS Node message: " + e.getMessage());
    } finally {
      manageBeansJpa.jpaClose();
    }
  }

  /**
   * Insert ODMS message.
   *
   * @param nodeId  the node ID
   * @param message the message
   * @return the int
   * @throws OdmsManagerException the odms manager exception
   */
  public static int insertOdmsMessage(int nodeId, String message) throws OdmsManagerException {
    PersistenceManager manageBeansJpa = new PersistenceManager();
    try {
      return manageBeansJpa.jpaInsertOdmsMessage(message, nodeId);
    } catch (Exception e) {
      throw new OdmsManagerException(
          "There was " + "an error while inserting ODMS Node message: " + e.getMessage());
    } finally {
      manageBeansJpa.jpaClose();
    }
  }

  /**
   * Return changed protocol.
   *
   * @param node the node
   * @return the odms catalogue
   */
  public static OdmsCatalogue returnChangedProtocol(OdmsCatalogue node) {

    String host = node.getHost();
    String[] protocol = host.split("://");
    if ("http".equals(protocol[0])) {
      protocol[0] = "https";
      node.setHost(StringUtils.join(protocol, "://"));
    } else if ("https".equals(protocol[0])) {
      protocol[0] = "http";
      node.setHost(StringUtils.join(protocol, "://"));
    }
    return node;
  }

  /**
   * The Class getNodesMonitor.
   */
  class GetNodesMonitor extends Thread {

    /** The total. */
    int total;

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
      synchronized (this) {
        for (int i = 0; i < 100; i++) {
          total += i;
        }
        notify();
      }
    }
  }

}
