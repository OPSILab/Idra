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

package it.eng.idra.management;

import it.eng.idra.beans.ConfigurationParameter;
import it.eng.idra.beans.DcatThemes;
import it.eng.idra.beans.IdraProperty;
import it.eng.idra.beans.Log;
import it.eng.idra.beans.RemoteCatalogue;
import it.eng.idra.beans.dcat.DcatApFormat;
import it.eng.idra.beans.dcat.DcatApProfile;
import it.eng.idra.beans.dcat.DcatApWriteType;
import it.eng.idra.beans.exception.DatasetNotFoundException;
import it.eng.idra.beans.odms.OdmsAlreadyPresentException;
import it.eng.idra.beans.odms.OdmsCatalogue;
import it.eng.idra.beans.odms.OdmsCatalogueFederationLevel;
import it.eng.idra.beans.odms.OdmsCatalogueForbiddenException;
import it.eng.idra.beans.odms.OdmsCatalogueMessage;
import it.eng.idra.beans.odms.OdmsCatalogueNotFoundException;
import it.eng.idra.beans.odms.OdmsCatalogueOfflineException;
import it.eng.idra.beans.odms.OdmsCatalogueSslException;
import it.eng.idra.beans.odms.OdmsCatalogueState;
import it.eng.idra.beans.odms.OdmsCatalogueType;
import it.eng.idra.beans.odms.OdmsManagerException;
import it.eng.idra.beans.odms.OdmsSynchLock;
import it.eng.idra.beans.search.SearchResult;
import it.eng.idra.cache.CachePersistenceManager;
import it.eng.idra.cache.LodCacheManager;
import it.eng.idra.cache.MetadataCacheManager;
import it.eng.idra.dcat.dump.DcatApDumpManager;
import it.eng.idra.dcat.dump.DcatApSerializer;
import it.eng.idra.scheduler.IdraScheduler;
import it.eng.idra.scheduler.exception.SchedulerNotInitialisedException;
import it.eng.idra.search.EuroVocTranslator;
import it.eng.idra.utils.PropertyManager;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.net.ssl.SSLHandshakeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.quartz.SchedulerException;

public class FederationCore {

  private FederationCore() {
  }

  private static CachePersistenceManager jpa;
  // private static PersistenceManager manageBeansJpa;

  static Logger logger = LogManager.getLogger(FederationCore.class);

  private static HashMap<String, String> settings = new HashMap<String, String>();
  // private static HashMap<String, String> remoteCatalogues = new HashMap<String,
  // String>();
  private static List<DcatThemes> dcatThemes = new ArrayList<DcatThemes>();
  // private static Timer deleteLogsTimer;

  /**
   * Inits the.
   *
   * @param loadCacheFromDb the load cache from db
   * @param solrPath the solr path
   */
  public static void init(boolean loadCacheFromDb, String solrPath) {

    PersistenceManager manageBeansJpa = new PersistenceManager();
    try {
      jpa = new CachePersistenceManager();
      settings = manageBeansJpa.getConfiguration();
      // remoteCatalogues = manageBeansJpa.getRemoteCatalogues();
      dcatThemes = manageBeansJpa.getDcatThems();

      MetadataCacheManager.init(loadCacheFromDb, solrPath);
      EuroVocTranslator.init();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      manageBeansJpa.jpaClose();
    }
  }

  /**
   * On finalize.
   */
  public static void onFinalize() {

    try {

      MetadataCacheManager.onFinalize();
      PersistenceManager.jpaFinalize();
      DbConnectionManager.closeDbConnection();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // public static PersistenceManager getManageBeansJpa() {
  // return manageBeansJpa;
  // }
  //
  // public static void setManageBeansJpa(PersistenceManager manageBeans) {
  // FederationCore.manageBeansJpa = manageBeans;
  // }

  public static HashMap<String, String> getSettings() {
    return settings;
  }

  /**
   * Sets the settings.
   *
   * @param settingsTmp the settings tmp
   * @throws NumberFormatException the number format exception
   * @throws SQLException the SQL exception
   */
  public static void setSettings(HashMap<String, String> settingsTmp) 
      throws NumberFormatException, SQLException {
    PersistenceManager manageBeansJpa = new PersistenceManager();
    try {
      List<ConfigurationParameter> configUsed = manageBeansJpa.getConfigurationList();

      for (ConfigurationParameter param : configUsed) {
        param.setParameterValue(settingsTmp.get(param.getParameterName()));
      }

      if (manageBeansJpa.updateConfigurationList(configUsed)) {
        settings = manageBeansJpa.getConfiguration();
      } 
    } finally {
      manageBeansJpa.jpaClose();
    }
  }

  /**
   * Gets the rem cat.
   *
   * @param id the id
   * @return the rem cat
   * @throws SQLException the SQL exception
   */
  public static RemoteCatalogue getRemCat(int id) throws SQLException {
    PersistenceManager manageBeansJpa = new PersistenceManager();
    try {
      return manageBeansJpa.getRemCatId(id);
    } finally {
      manageBeansJpa.jpaClose();
    }
  }

  /**
   * Sets the remote catalogue.
   *
   * @param rem the rem
   * @return true, if successful
   * @throws SQLException the SQL exception
   */
  public static boolean setRemoteCatalogue(RemoteCatalogue rem) throws SQLException {
    PersistenceManager manageBeansJpa = new PersistenceManager();
    try {
      if (!manageBeansJpa.checkRemCatExists(rem)) {
        return manageBeansJpa.addRemoteCat(rem);
      }
    } finally {
      // remoteCatalogues = manageBeansJpa.getRemoteCatalogues();
      manageBeansJpa.jpaClose();
    }

    return false;
  }

  /**
   * Delete rem cat.
   *
   * @param id the id
   * @return true, if successful
   * @throws SQLException the SQL exception
   */
  public static boolean deleteRemCat(int id) throws SQLException {
    PersistenceManager manageBeansJpa = new PersistenceManager();
    try {
      return manageBeansJpa.deleteRemCat(id);
    } finally {
      manageBeansJpa.jpaClose();
    }
  }

  /**
   * Update rem cat.
   *
   * @param rem the rem
   * @return true, if successful
   * @throws SQLException the SQL exception
   */
  public static boolean updateRemCat(RemoteCatalogue rem) throws SQLException {
    PersistenceManager manageBeansJpa = new PersistenceManager();
    try {
      return manageBeansJpa.updateRemCat(rem);
    } finally {
      manageBeansJpa.jpaClose();
    }
  }

  /**
   * Gets the all rem catalogues.
   *
   * @return the all rem catalogues
   * @throws SQLException the SQL exception
   */
  public static List<RemoteCatalogue> getAllRemCatalogues() throws SQLException {
    PersistenceManager manageBeansJpa = new PersistenceManager();
    try {
      return manageBeansJpa.getremoteCataloguesList();
    } finally {
      manageBeansJpa.jpaClose();
    }
  }

  /**
   * Gets the logs.
   *
   * @param levelList the level list
   * @param from the from
   * @param to the to
   * @return the logs
   * @throws SQLException the SQL exception
   */
  public static List<Log> getLogs(List<String> levelList, 
      ZonedDateTime from, ZonedDateTime to) throws SQLException {

    PersistenceManager manageBeansJpa = new PersistenceManager();
    try {
      return manageBeansJpa.getLogs(levelList, from, to);
    } finally {
      manageBeansJpa.jpaClose();
    }
  }

  /*
   * ODMS MESSAGES DELEGATE METHODS
   */

  public static HashMap<Integer, Long> getAllOdmsMessagesCount() throws OdmsManagerException {
    return OdmsManager.getAllOdmsMessagesCount();
  }

  public static List<OdmsCatalogueMessage> getOdmsMessages(int nodeId) throws OdmsManagerException {
    return OdmsManager.getOdmsMessages(nodeId);
  }

  public static OdmsCatalogueMessage getOdmsMessage(int nodeId, int messageId) 
      throws OdmsManagerException {
    return OdmsManager.getOdmsMessage(nodeId, messageId);
  }

  public static void deleteAllOdmsMessage(int nodeId) throws OdmsManagerException {
    OdmsManager.deleteAllOdmsMessage(nodeId);
  }

  public static void deleteOdmsMessage(int nodeId, int messageId) throws OdmsManagerException {
    OdmsManager.deleteOdmsMessage(nodeId, messageId);
  }

  public static int insertOdmsMessage(int nodeId, String message) throws OdmsManagerException {
    return OdmsManager.insertOdmsMessage(nodeId, message);
  }

  /**
   * Gets the list of all possible Connector types.
   * 
   */
  public static HashMap<OdmsCatalogueType, String> getOdmsConnectorsList() {
    return OdmsManager.getOdmsConnectorsList();
  }

  /**
   * Gets the List of the federated ODMS Catalogues present in the Federation.
   * Forwards the request to the underlying JDBC layer
   *
   * @throws OdmsManagerException
   * 
   */

  public static List<OdmsCatalogue> getOdmsCatalogues() {
    return OdmsManager.getOdmsCatalogues();
  }

  public static List<OdmsCatalogue> getOdmsCatalogues(boolean withImage) 
      throws OdmsManagerException {
    return OdmsManager.getOdmsCatalogues(withImage);
  }

  public static List<OdmsCatalogue> getAllInactiveOdmsCatalogues(boolean withImage) 
      throws OdmsManagerException {
    return OdmsManager.getAllInactiveOdmsCatalogue(withImage);
  }

  /**
   * Gets a federated ODMS Catalogue present in the Federation.
   * Forwards the request to the underlying ODMSManager
   *
   * @param id Id of requested federated node
   * @returns ODMSCatalogue the resulting federated ODMS Catalogue
   * 
   */
  public static OdmsCatalogue getOdmsCatalogue(int id) throws OdmsCatalogueNotFoundException {
    return OdmsManager.getOdmsCatalogue(id);
  }

  public static OdmsCatalogue getOdmsCatalogue(int id, boolean withImage)
      throws OdmsCatalogueNotFoundException, OdmsManagerException {
    return OdmsManager.getOdmsCatalogue(id, withImage);
  }

  public static OdmsCatalogue getInactiveOdmsCatalogue(int id)
      throws OdmsCatalogueNotFoundException, OdmsManagerException {
    return OdmsManager.getInactiveOdmsCatalogue(id, false);
  }

  /**
   * Gets the List of federated ODMS Catalogues with a specific federation grade
   * present in the Federation. 
   * Forwards the request to the underlying JDBC layer
   *
   * @throws SQLException
   *          with specific federation grade
   */
  public static List<OdmsCatalogue> getOdmsCataloguesbyFederationLevel(
      OdmsCatalogueFederationLevel levelFirst,
      OdmsCatalogueFederationLevel levelSecond) throws SQLException {

    return OdmsManager.getOdmsCataloguesbyFederationLevel(levelFirst, levelSecond);

  }

  /**
   * Performs the registration of a new ODMS Catalogue passed by API module.
   * Depending on Federation Level performs further operations such as
   * availability and first synchronization
   *
   * @returns void
   */
  public static void registerOdmsCatalogue(final OdmsCatalogue node) 
      throws OdmsAlreadyPresentException, OdmsManagerException,
      OdmsCatalogueNotFoundException, OdmsCatalogueForbiddenException, 
      OdmsCatalogueSslException, InvocationTargetException, 
      OdmsCatalogueOfflineException, SchedulerNotInitialisedException, SQLException {

    /*
     * ************************************************************ The Catalogue is
     * created in the Persistence the PostCreate is triggered after the line below
     **************************************************************/
    final int assignedNodeId = OdmsManager.addOdmsCatalogue(node);

    /*
     * ************************************************************ If the Catalogue
     * has Federation Level 2 o 3, all its datasets are loaded to Persistence and
     * SOLR Cache
     **************************************************************/

    if (node.isCacheable()) {

      node.setId(assignedNodeId);
      node.setSynchLock(OdmsSynchLock.FIRST);
      OdmsManager.updateOdmsCatalogue(node, false);

      /*
       * ************************************************************ 1. Gather
       * datasets from the Catalogue, then persist and cache them 2. Start all the
       * Jobs needed after Catalogue registration 3. Manage ODMS Statistics and
       * Messages
       ***********************************************************/
      try {
        /*
         * 1. Gather datasets from the Catalogue, then persist and cache them
         */
        MetadataCacheManager.loadCacheFromOdmsCatalogue(node, false);

        node.setSynchLock(OdmsSynchLock.NONE);
        OdmsManager.updateOdmsCatalogue(node, false);

        /*
         * 2. Start all the Jobs after Catalogue registration
         */
        IdraScheduler.getSingletonInstance().startCataloguesSynchJob(node, false);
        // Everything fine reload list of nodes without images
        OdmsManager.updateOdmsCatalogueList();

        /*
         * 3. Insert the statistic of the new node
         */
        StatisticsManager.odmsStatistics(node, 
            node.getDatasetCount(), 0, 0, node.getRdfCount(), 0, 0);
        logger.info(
            "--------- The ODMS Catalogue with host " 
            + node.getHost() + " was successfully registered ----------");
        OdmsManager.insertOdmsMessage(node.getId(), "Node successfully registered");

        /*
         * 4. Create Catalogue's dump file
         */

        try {
          SearchResult result = MetadataCacheManager.getAllDatasetsByOdmsCatalogueId(node.getId());
          DcatApSerializer.searchResultToDcatApByNode(Integer.toString(node.getId()), result,
              DcatApFormat.fromString(PropertyManager.getProperty(IdraProperty.DUMP_FORMAT)),
              DcatApProfile.fromString(
                  PropertyManager.getProperty(IdraProperty.DUMP_PROFILE)), DcatApWriteType.FILE);

          // Write Catalogue's DCAT Dump into RDF4J
          DcatApDumpManager.sendDumpToRepository(node);

        } catch (Exception e1) {
          // e1.printStackTrace();
          logger.error("Error: " 
              + e1.getMessage() + " in creation of the dump file for node " + node.getId());
        }

      } catch (InvocationTargetException e) {
        e.printStackTrace();
        Throwable target = null;
        if ((target = e.getCause()) != null) {
          if ((target = target.getCause()) != null) {

            Class targetClass = target.getClass();
            if (targetClass.equals(OdmsCatalogueOfflineException.class)) {
              e.printStackTrace();
              logger.error("Problem during registration of Catalogue " 
                  + node.getName() + ": Setting state to OFFLINE");
              OdmsManager.insertOdmsMessage(node.getId(), "Unreacheble, setting state to OFFLINE");
              node.setNodeState(OdmsCatalogueState.OFFLINE);
              node.setDatasetCount(0);
              OdmsManager.updateOdmsCatalogue(node, true);
              node.setSynchLock(OdmsSynchLock.NONE);

              try {
                IdraScheduler.getSingletonInstance().startCataloguesSynchJob(node, false);
              } catch (SchedulerNotInitialisedException e1) {
                e1.printStackTrace();

              }

            } else if (targetClass.equals(OdmsCatalogueNotFoundException.class)) {
              e.printStackTrace();
              logger.error("The node host " + node.getHost() + " was not found");
              OdmsManager.deleteOdmsCatalogue(node);
              throw new OdmsCatalogueNotFoundException("The node " 
                  + node.getHost() + " host was not found");
            } else if (targetClass.equals(OdmsCatalogueForbiddenException.class)) {
              e.printStackTrace();
              logger.error("The ODMS Catalogue " + node.getHost() + " is forbidden");
              OdmsManager.deleteOdmsCatalogue(node);
              throw new OdmsCatalogueForbiddenException("The ODMS Catalogue " 
                  + node.getHost() + " is forbidden");
            } else if (targetClass.equals(SSLHandshakeException.class)) {
              e.printStackTrace();
              logger.error("The ODMS Catalogue " + node.getHost()
                  + " requested SSL handshake, import its certificate into java keystore");
              OdmsManager.deleteOdmsCatalogue(node);
              throw new OdmsCatalogueSslException(
                  "The ODMS Catalogue " + node.getHost() + " requested SSL handshake and failed");
            } else {
              logger.error("There was an error while registering ODMS Catalogue " + node.getHost()
                  + ": operation deleted " + e.getMessage());
              OdmsManager.deleteOdmsCatalogue(node);
              throw e;
            }
          } else {
            logger.error("There was an error while registering ODMS Catalogue " + node.getHost()
                + ": operation deleted " + e.getMessage());
            OdmsManager.deleteOdmsCatalogue(node);
            throw e;
          }
        }

      } catch (SchedulerNotInitialisedException e) {

        logger.error("Scheduler not initialised, skipped synchronization thread for " 
            + node.getHost() + ":"
            + e.getLocalizedMessage());
        throw e;
      } catch (SQLException e) {

        logger.error("SqlException while updating node list: " + e.getLocalizedMessage());
        throw e;
      }

    }

  }

  /**
   * Register inactive odms catalogue.
   *
   * @param node the node
   * @throws Exception the exception
   */
  public static void registerInactiveOdmsCatalogue(final OdmsCatalogue node) throws Exception {

    try {
      node.setActive(false);
      OdmsManager.addOdmsCatalogue(node);
    } catch (OdmsAlreadyPresentException 
        | OdmsCatalogueNotFoundException | OdmsCatalogueOfflineException
        | OdmsCatalogueForbiddenException | OdmsManagerException e) {

      // e.printStackTrace(); Exception printed and handled at API level
      throw e;
    }

  }

  // public static void deleteInactiveODMSNode(ODMSCatalogue node) {
  // try {
  // ODMSManager.deleteInactiveODMSNode(node);
  // } catch (ODMSManagerException e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // }
  // }

  /**
   * Performs the unregister operation of a Federated ODMS Catalogue passed by API
   * module.
   * Depending on Federation Level performs further operations such as
   * availability and first synchronization
   *
   * @returns void
   */
  public static void unregisterOdmsCatalogue(OdmsCatalogue node) throws Exception {

    node.setSynchLock(OdmsSynchLock.PERIODIC);
    OdmsManager.updateOdmsCatalogue(node, false);

    // If node has federation level 2 o 3, are deleted all its datasets from
    // Persistence and SOLR Cache
    if (node.isCacheable()) {
      MetadataCacheManager.deleteAllDatasetsByOdmsCatalogue(node);
      try {
        LodCacheManager.deleteRdf(node.getHost());
      } catch (Exception e) {
        logger.error("Error while deleting catalogue's " + node.getName() + "rdf");
      }
    }

    OdmsManager.deleteAllOdmsMessage(node.getId());

    OdmsManager.deleteOdmsCatalogue(node);

    PersistenceManager manageBeansJpa = new PersistenceManager();
    try {

      manageBeansJpa.removeOdmsStatistics(node.getId());
      // remove node synch timer from timers list
      // SynchManager.deleteODMSNodeSynchTimer(node.getId());
      IdraScheduler odfScheduler = IdraScheduler.getSingletonInstance();
      if (odfScheduler.isJobRunning(Integer.toString(node.getId()))) {
        odfScheduler.interruptJob(Integer.toString(node.getId()));
      }
      odfScheduler.deleteJob(Integer.toString(node.getId()));

      System.gc();
      logger.info(
          "The ODMS Catalogue with name: " 
           + node.getName() + " and ID: " + node.getId() + " was successfully deleted");

    } finally {

      manageBeansJpa.jpaClose();
    }
  }

  /**
   * Performs the update operation of a Federated ODMS Catalogue passed by API
   * module.
   * Depending on previous and current Federation Level performs further
   * operations such as caching datasets for a node with a new Federation level
   * equal or greater than 2 and deleting datasets cache of a node with a new
   * Federation Level equal or smaller than 1
   *
   * @param node The ODMSCatalogue to update
   * @throws Exception
   * 
   * @returns void
   * 
   */
  public static void updateFederatedOdmsCatalogue(OdmsCatalogue node, boolean rescheduleJob)
      throws SQLException, NoSuchMethodException, SecurityException,
      ClassNotFoundException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, 
      InstantiationException, SolrServerException, IOException,
      DatasetNotFoundException, RepositoryException, 
      RDFParseException, OdmsCatalogueNotFoundException,
      OdmsManagerException {

    OdmsCatalogueFederationLevel oldLevel = 
        OdmsManager.getOdmsCatalogue(node.getId()).getFederationLevel();
    OdmsCatalogueFederationLevel newLevel = node.getFederationLevel();
    try {
      // If node becomes cacheable
      if ((newLevel.equals(OdmsCatalogueFederationLevel.LEVEL_2)
          || newLevel.equals(OdmsCatalogueFederationLevel.LEVEL_3))
          && ((oldLevel.equals(OdmsCatalogueFederationLevel.LEVEL_1)
              || oldLevel.equals(OdmsCatalogueFederationLevel.LEVEL_0)))) {
        MetadataCacheManager.loadCacheFromOdmsCatalogue(node, false);
        // SynchManager.addODMSNodeSynchTimer(node, false);

        IdraScheduler.getSingletonInstance().startCataloguesSynchJob(node, false);

      }

      if (oldLevel.equals(OdmsCatalogueFederationLevel.LEVEL_2)
          || oldLevel.equals(OdmsCatalogueFederationLevel.LEVEL_3)) {

        // If node is not cacheable anymore
        if (newLevel.equals(OdmsCatalogueFederationLevel.LEVEL_1)
            || newLevel.equals(OdmsCatalogueFederationLevel.LEVEL_0)) {
          MetadataCacheManager.deleteAllDatasetsByOdmsCatalogue(node);
          // SynchManager.deleteODMSNodeSynchTimer(node.getId());

          IdraScheduler.getSingletonInstance().deleteJob(Integer.toString(node.getId()));
          // this is used to avoid problems on future federation level grown
          node.setDatasetStart(0);
        } else {
          // Rescheduling the job
          if (rescheduleJob) {
            IdraScheduler.getSingletonInstance()
                .rescheduleJob(Integer.toString(node.getId()), node);
          }
        }

      }

      if (newLevel.equals(OdmsCatalogueFederationLevel.LEVEL_4) && rescheduleJob) {
        MetadataCacheManager.deleteAllDatasetsByOdmsCatalogue(node);
        MetadataCacheManager.loadCacheFromOdmsCatalogue(node, false);
      }

    } catch (SchedulerNotInitialisedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    OdmsManager.updateOdmsCatalogue(node, true);
    OdmsManager.insertOdmsMessage(node.getId(), "Node successfully updated");
    logger.info(
        "The ODMS Catalogue with name: " + node.getName() 
              + " and ID: " + node.getId() + " was successfully updated");

    // Everything fine reload list of nodes without images
    try {
      OdmsManager.updateOdmsCatalogueList();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  /**
   * Starts a new node synchonization and resets the related timer.
   * 
   * @throws OdmsManagerException
   * 
   * @returns void
   */
  public static void startOdmsCatalogueSynch(final int nodeId)
      throws OdmsCatalogueNotFoundException, OdmsManagerException {

    try {
      IdraScheduler.getSingletonInstance().triggerNow(Integer.toString(nodeId));
    } catch (SchedulerNotInitialisedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // final ODMSCatalogue node = ODMSManager.getODMSNode(nodeId);
    //
    // // Remove the old node synch timer
    // SynchManager.deleteODMSNodeSynchTimer(nodeId);
    //
    // // Create a new one starting by now
    // SynchManager.addODMSNodeSynchTimer(node, true);

  }

  // protected static void initODMSSynchScheduler(boolean startNow) {
  // SynchManager.initSynchScheduler(startNow);
  // }

  // protected static void stopODMSSynchScheduler() {
  // SynchManager.stopSynchScheduler();
  // }

  /**
   * Deactivate odms catalogue.
   *
   * @param node the node
   * @param keepDatasets the keep datasets
   * @throws OdmsCatalogueNotFoundException the odms catalogue not found exception
   * @throws OdmsManagerException the odms manager exception
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws SolrServerException the solr server exception
   * @throws DatasetNotFoundException the dataset not found exception
   */
  public static void deactivateOdmsCatalogue(OdmsCatalogue node, Boolean keepDatasets)
      throws OdmsCatalogueNotFoundException, OdmsManagerException, IOException, SolrServerException,
      DatasetNotFoundException {
    node.setActive(false);
    OdmsManager.updateOdmsCatalogue(node, true);
    // remove node synch timer from timers list
    try {
      IdraScheduler odfScheduler = IdraScheduler.getSingletonInstance();
      if (odfScheduler.isJobRunning(Integer.toString(node.getId()))) {
        logger.info("Interrupting job for catalogue: " + node.getId());
        odfScheduler.interruptJob(Integer.toString(node.getId()));
      }
      logger.info("Deleting job for catalogue: " + node.getId());
      odfScheduler.deleteJob(Integer.toString(node.getId()));

    } catch (SchedulerNotInitialisedException | SchedulerException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    if (!keepDatasets) {
      node.setSynchLock(OdmsSynchLock.PERIODIC);
      OdmsManager.updateOdmsCatalogue(node, false);

      if (node.isCacheable()) {
        MetadataCacheManager.deleteAllDatasetsByOdmsCatalogue(node);
        try {
          LodCacheManager.deleteRdf(node.getHost());
        } catch (Exception e) {
          logger.error("Error while deleting catalogue's " + node.getName() + "rdf");
        }
      }

      OdmsManager.deleteAllOdmsMessage(node.getId());

      PersistenceManager manageBeansJpa = new PersistenceManager();
      try {
        manageBeansJpa.removeOdmsStatistics(node.getId());
      } finally {
        manageBeansJpa.jpaClose();
      }
      node.setDatasetStart(0);
      node.setDatasetCount(0);
      node.setNodeState(OdmsCatalogueState.OFFLINE);
      node.setSynchLock(OdmsSynchLock.NONE);
      OdmsManager.updateOdmsCatalogue(node, true);
      System.gc();
    }

    logger.info("The ODMS Catalogue with name: " + node.getName() + " and ID: " + node.getId()
        + " was successfully deactivated " 
        + ((keepDatasets) ? "keeping datasets" : "deleting datasets"));
  }

  /**
   * Activate odms catalogue.
   *
   * @param node the node
   * @throws OdmsCatalogueNotFoundException the odms catalogue not found exception
   * @throws OdmsManagerException the odms manager exception
   */
  public static void activateOdmsCatalogue(OdmsCatalogue node)
      throws OdmsCatalogueNotFoundException, OdmsManagerException {
    node.setActive(true);
    OdmsManager.updateOdmsCatalogue(node, true);

    boolean startNow = false;

    if (node.getDatasetStart() != -1) {
      node.setRegisterDate(ZonedDateTime.now(ZoneOffset.UTC));
      OdmsManager.updateOdmsCatalogue(node, true);
      startNow = true;
    }

    if (!node.getFederationLevel().equals(OdmsCatalogueFederationLevel.LEVEL_4)) {
      try {
        IdraScheduler.getSingletonInstance().startCataloguesSynchJob(node, startNow);
      } catch (SchedulerNotInitialisedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } else {
      if (startNow) {
        try {
          MetadataCacheManager.loadCacheFromOdmsCatalogue(node, false);
        } catch (InvocationTargetException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }

  }

  public static Integer getOdmsCatalogueIdbyName(String nodeName) {
    return OdmsManager.getOdmsCatalogueIdbyName(nodeName);
  }

  /**
   * Gets the active odms catalogues id.
   *
   * @param ids the ids
   * @return the active odms catalogues id
   */
  public static List<Integer> getActiveOdmsCataloguesId(List<Integer> ids) {
    logger.info("Original nodeID list: " + ids.toString());
    ids = ids.stream().filter(x -> {
      try {
        return OdmsManager.getOdmsCatalogue(x).isActive();
      } catch (OdmsCatalogueNotFoundException e) {
        return false;
      }
    }).collect(Collectors.toList());

    if (ids.isEmpty()) {
      ids.add(0);
    }
    logger.info("Filtered nodeID list: " + ids.toString());
    return ids;
  }

  public static List<DcatThemes> getDcatThemes() {
    return dcatThemes;
  }

  public static String getDcatThemesFromAbbr(String abbr) {
    return dcatThemes.stream()
        .filter(x -> x.getIdentifier().equalsIgnoreCase(abbr)).findFirst().get().getEn();
  }

  public static String getDcatThemesIdentifier(String val) {
    return dcatThemes.stream()
        .filter(x -> x.getEn().equalsIgnoreCase(val)).findFirst().get().getIdentifier();
  }

  /**
   * Checks if is dcat theme.
   *
   * @param value the value
   * @return true, if is dcat theme
   */
  public static boolean isDcatTheme(String value) {
    return dcatThemes.stream()
        .anyMatch(x -> x.getIdentifier()
            .equalsIgnoreCase(value) || x.getEn().equalsIgnoreCase(value));
  }

  public static Logger getLogger() {
    return logger;
  }

}
