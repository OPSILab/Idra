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

package it.eng.idra.scheduler.job;

import it.eng.idra.beans.IdraProperty;
import it.eng.idra.beans.dcat.DcatApFormat;
import it.eng.idra.beans.dcat.DcatApProfile;
import it.eng.idra.beans.dcat.DcatApWriteType;
import it.eng.idra.beans.dcat.DcatDataset;
import it.eng.idra.beans.dcat.DcatDistribution;
import it.eng.idra.beans.exception.DatasetNotFoundException;
import it.eng.idra.beans.odms.OdmsCatalogue;
import it.eng.idra.beans.odms.OdmsCatalogueForbiddenException;
import it.eng.idra.beans.odms.OdmsCatalogueNotFoundException;
import it.eng.idra.beans.odms.OdmsCatalogueState;
import it.eng.idra.beans.odms.OdmsCatalogueType;
import it.eng.idra.beans.odms.OdmsManagerException;
import it.eng.idra.beans.odms.OdmsSynchLock;
import it.eng.idra.beans.odms.OdmsSynchronizationResult;
import it.eng.idra.beans.search.SearchResult;
import it.eng.idra.cache.CachePersistenceManager;
import it.eng.idra.cache.LodCacheManager;
import it.eng.idra.cache.MetadataCacheManager;
import it.eng.idra.dcat.dump.DcatApDumpManager;
import it.eng.idra.dcat.dump.DcatApSerializer;
import it.eng.idra.management.FederationCore;
import it.eng.idra.management.OdmsManager;
import it.eng.idra.management.StatisticsManager;
import it.eng.idra.utils.CommonUtil;
import it.eng.idra.utils.PropertyManager;
import it.eng.idra.utils.restclient.RestClient;
import it.eng.idra.utils.restclient.RestClientImpl;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityExistsException;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.UnableToInterruptJobException;

// TODO: Auto-generated Javadoc
/**
 * The Class OdmsSynchJob.
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class OdmsSynchJob implements InterruptableJob {

  /** The logger. */
  private static Logger logger = LogManager.getLogger(OdmsSynchJob.class);
  
  /** The Context Broker Manager URL. */
  private static String urlOrionmanager = 
      PropertyManager.getProperty(IdraProperty.ORION_MANAGER_URL);

  /** The enable rdf. */
  private static Boolean enableRdf = Boolean
      .parseBoolean(PropertyManager.getProperty(IdraProperty.ENABLE_RDF));

  /**
   * Instantiates a new odms synch job.
   */
  public OdmsSynchJob() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.quartz.InterruptableJob#interrupt()
   */
  @Override
  public void interrupt() throws UnableToInterruptJobException {
    // TODO Auto-generated method stub
    logger.info("Interrupting job");
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
   */
  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    // TODO Auto-generated method stub
    logger.info("\nIn Execute");
    try {
      logger.info("Starting synch job for catalogue: "
          + context.getJobDetail().getJobDataMap().get("nodeID"));
      OdmsCatalogue node = OdmsManager
          .getOdmsCatalogue((int) context.getJobDetail().getJobDataMap().get("nodeID"));
      if (!node.isFederating() && node.isUnlocked() && node.isActive()) {
        node.setSynchLock(OdmsSynchLock.PERIODIC);
        OdmsManager.updateOdmsCatalogue(node, false);
        try {
          synchOdmsNode(node, false);
        } catch (OdmsCatalogueNotFoundException e) {
          e.printStackTrace();
        } catch (OdmsCatalogueForbiddenException e) {
          logger.info(e.getMessage());
          // node.setSynchLock(ODMSSynchLock.NONE);
        } finally {
          node.setSynchLock(OdmsSynchLock.NONE);
          OdmsManager.updateOdmsCatalogue(node, false);
        }
      }
    } catch (OdmsCatalogueNotFoundException e) {
      e.printStackTrace();
      // JobExecutionException e2 =
      // new JobExecutionException(e);
      // e2.setUnscheduleFiringTrigger(true);
    } catch (Exception e) {

      e.printStackTrace();
    }
  }

  /**
   * Gets the changed datasets.
   *
   * @param node         the node
   * @param oldDatasets  the old datasets
   * @param startingDate the starting date
   * @return the changed datasets
   * @throws Exception the exception
   */
  static OdmsSynchronizationResult getChangedDatasets(OdmsCatalogue node,
      List<DcatDataset> oldDatasets, String startingDate) throws Exception {

    OdmsSynchronizationResult nodeDatasets = new OdmsSynchronizationResult();

    // The connector class is loaded, based on ODMS node type, using Java
    // reflection
    nodeDatasets = OdmsManager.getOdmsCatalogueConnector(node).getChangedDatasets(oldDatasets,
        startingDate);
    System.gc();

    return nodeDatasets;
  }

  /**
   * Synch web odms node.
   *
   * @param node the node
   * @throws InvocationTargetException the invocation target exception
   * @throws IOException               Signals that an I/O exception has occurred.
   * @throws SolrServerException       the solr server exception
   * @throws DatasetNotFoundException  the dataset not found exception
   */
  private static void synchWebOdmsNode(OdmsCatalogue node)
      throws InvocationTargetException, IOException, SolrServerException, DatasetNotFoundException {

    MetadataCacheManager.deleteAllDatasetsByOdmsCatalogue(node);
    MetadataCacheManager.loadCacheFromOdmsCatalogue(node, false);
    StatisticsManager.odmsStatistics(node, node.getDatasetCount(), 0, 0, node.getRdfCount(), 0, 0);

  }

  /**
   * Synch dump odms node.
   *
   * @param node the node
   * @throws InvocationTargetException the invocation target exception
   * @throws IOException               Signals that an I/O exception has occurred.
   * @throws SolrServerException       the solr server exception
   * @throws DatasetNotFoundException  the dataset not found exception
   */
  private static void synchDumpOdmsNode(OdmsCatalogue node)
      throws InvocationTargetException, IOException, SolrServerException, DatasetNotFoundException {

    MetadataCacheManager.deleteAllDatasetsByOdmsCatalogue(node);

    if (StringUtils.isBlank(node.getDumpUrl()) && StringUtils.isBlank(node.getDumpString())
        && StringUtils.isNotBlank(node.getDumpFilePath())) {

      // Read the content of the file from the file system
      String dumpString = new String(Files.readAllBytes(Paths.get(node.getDumpFilePath())));
      node.setDumpString(dumpString);

    }

    MetadataCacheManager.loadCacheFromOdmsCatalogue(node, false);
    StatisticsManager.odmsStatistics(node, node.getDatasetCount(), 0, 0, node.getRdfCount(), 0, 0);

  }

  /**
   * Synch odms node.
   *
   * @param node              the node
   * @param isChangedProtocol the is changed protocol
   * @return true, if successful
   * @throws SQLException                    the SQL exception
   * @throws IOException                     Signals that an I/O exception has
   *                                         occurred.
   * @throws SolrServerException             the solr server exception
   * @throws ClassNotFoundException          the class not found exception
   * @throws InstantiationException          the instantiation exception
   * @throws IllegalAccessException          the illegal access exception
   * @throws IllegalArgumentException        the illegal argument exception
   * @throws NoSuchMethodException           the no such method exception
   * @throws SecurityException               the security exception
   * @throws DatasetNotFoundException        the dataset not found exception
   * @throws RepositoryException             the repository exception
   * @throws RDFParseException               the RDF parse exception
   * @throws OdmsCatalogueNotFoundException  the odms catalogue not found
   *                                         exception
   * @throws OdmsCatalogueForbiddenException the odms catalogue forbidden
   *                                         exception
   * @throws OdmsManagerException            the odms manager exception
   */
  protected static boolean synchOdmsNode(OdmsCatalogue node, boolean isChangedProtocol)
      throws SQLException, IOException, SolrServerException, ClassNotFoundException,
      InstantiationException, IllegalAccessException, IllegalArgumentException,
      NoSuchMethodException, SecurityException, DatasetNotFoundException, RepositoryException,
      RDFParseException, OdmsCatalogueNotFoundException, OdmsCatalogueForbiddenException,
      OdmsManagerException {
    logger.info("\nIn Sync");
    if (!node.isFederating()) {
      logger.info("Starting synchronization for node: " + node.getName() + " ID: " + node.getId());

      OdmsSynchronizationResult synchroResult = new OdmsSynchronizationResult();
      List<DcatDataset> presentDatasets = new ArrayList<DcatDataset>();
      int addedDatasets = 0;
      int deletedDatasets = 0;
      int updatedDatasets = 0;
      int addedRdf = 0;
      int deletedRdf = 0;
      int updatedRdf = 0;

      boolean synchCompleted = true;

      boolean first = false;
      if (node.getDatasetStart() != -1) {
        first = true;
      }

      // Check first the current node state
      node.setNodeState(OdmsManager.checkOdmsCatalogue(node));

      logger.info("The ODMS Node with name: " + node.getName() + " and ID: " + node.getId() + " is "
          + node.getNodeState());

      /*
       * If the node is Online is possible to retrieve only changed datasets
       * ********************
       */
      ZonedDateTime lastUpdate = node.getLastUpdateDate();

      if (node.isOnline()) {
        if (!first) {

          try {

            // if (!node.getNodeType().equals(ODMSCatalogueType.DCATDUMP)) {

            if (!node.getNodeType().equals(OdmsCatalogueType.CKAN)) {
              presentDatasets = MetadataCacheManager.getAllDatasetsByOdmsCatalogue(node.getId());
            }

            synchroResult = getChangedDatasets(node, presentDatasets,
                CommonUtil.formatDate(lastUpdate));

            for (DcatDataset dataset : synchroResult.getDeletedDatasets()) {
              deletedRdf += OdmsSynchJob.deleteDataset(node, dataset);
            }

            for (DcatDataset dataset : synchroResult.getAddedDatasets()) {
              addedRdf += OdmsSynchJob.addDataset(node, dataset);
            }

            for (DcatDataset dataset : synchroResult.getChangedDatasets()) {
              updatedRdf += OdmsSynchJob.updateDataset(node, dataset);
            }

            // } else if (node.getNodeType().equals(ODMSCatalogueType.DCATDUMP)) {
            //// Do nothing for node type DUMP
            // synchDUMPODMSNode(node);
            // }

          } catch (Exception e) {
            synchCompleted = false;
            e.printStackTrace();
            node.setLastUpdateDate(lastUpdate);
            OdmsManager.insertOdmsMessage(node.getId(), "Node OFFLINE " + e.getLocalizedMessage());
            logger.error("Setting node state to OFFLINE " + e.getLocalizedMessage());
            node.setNodeState(OdmsCatalogueState.OFFLINE);
            node.setSynchLock(OdmsSynchLock.NONE);
            OdmsManager.updateOdmsCatalogue(node, true);
          }

          /*
           * Otherwise if the node was offline during first synchronization, it is
           * necessary to load all datasets from node ****
           */

        } else {
          try {
            logger.info("LOAD CACHE FROM ODMS");
            node.setDatasetCount(OdmsManager.countOdmsCatalogueDatasets(node));
            logger.info(node.getDatasetCount());
            MetadataCacheManager.loadCacheFromOdmsCatalogue(node, false);
            logger.info(node.getDatasetCount());
            node.setNodeState(OdmsCatalogueState.ONLINE);

          } catch (InvocationTargetException e) {
            synchCompleted = false;
            logger.info("Setting node state to OFFLINE " + e.getLocalizedMessage());
            OdmsManager.insertOdmsMessage(node.getId(), "Node OFFLINE " + e.getLocalizedMessage());
            node.setNodeState(OdmsCatalogueState.OFFLINE);
            node.setDatasetCount(0);
            // ODMSManager.updateODMSNode(node, true);
          } finally {
            node.setSynchLock(OdmsSynchLock.NONE);
            OdmsManager.updateOdmsCatalogue(node, true);
          }
        }
      } else {
        if (!isChangedProtocol) {
          node = OdmsManager.returnChangedProtocol(node);
          return synchOdmsNode(node, true);
        } else {
          return false;
        }
      }

      try {
        logger.info("ADDING the Catalogue in the Context Broker calling the BROKER MANAGER "
            + "component");
        addCatalogueInCb(node);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        logger.error("Error: " + e.getMessage() + " in creation of the Catalogue node "
            + node.getId() + " in the Context Broker");
      }

      if (synchCompleted) {

        addedDatasets = synchroResult.getAddedDatasets().size();
        updatedDatasets = synchroResult.getChangedDatasets().size();
        deletedDatasets = synchroResult.getDeletedDatasets().size();

        // Update the dataset count of the federated Node
        node.setDatasetCount(node.getDatasetCount() + addedDatasets - deletedDatasets);
        node.setRdfCount(node.getRdfCount() + addedRdf - deletedRdf);
        OdmsManager.updateOdmsCatalogue(node, true);

        // Set the new last update date to the node
        node.setLastUpdateDate(ZonedDateTime.now(ZoneOffset.UTC));
        OdmsManager.updateOdmsCatalogue(node, true);

        // LOG Operations
        logger
            .info("Synchronization complete for node: " + node.getName() + " ID: " + node.getId());
        logger.info("Added RDF = " + addedRdf);
        logger.info("Updated RDF = " + updatedRdf);
        logger.info("Deleted RDF = " + deletedRdf);
        logger.info("Added Datasets : " + addedDatasets);
        logger.info("Updated Datasets : " + updatedDatasets);
        logger.info("Deleted Datasets : " + deletedDatasets);

        // Adds dataset counters to the statistics on DB
        StatisticsManager.odmsStatistics(node, addedDatasets, deletedDatasets, updatedDatasets,
            addedRdf, updatedRdf, deletedRdf);
        OdmsManager.insertOdmsMessage(node.getId(), "Node successfully synchronized");

        // Creating the dump file for the node after the synchronization
        try {
          SearchResult result = MetadataCacheManager.getAllDatasetsByOdmsCatalogueId(node.getId());
          DcatApSerializer.searchResultToDcatApByNode(Integer.toString(node.getId()), result,
              DcatApFormat.fromString(PropertyManager.getProperty(IdraProperty.DUMP_FORMAT)),
              DcatApProfile.fromString(PropertyManager.getProperty(IdraProperty.DUMP_PROFILE)),
              DcatApWriteType.FILE);

          // Write Catalogue's DCAT Dump into RDF4J
          DcatApDumpManager.sendDumpToRepository(node);      

        } catch (Exception e1) {
          e1.printStackTrace();
          logger.error("Error: " + e1.getMessage() + " in creation of the dump file for node "
              + node.getId());
        }
      }
      return true;
    } else {
      return false;
    }

  }
  
  /**
   * Adding the node in the CB.
   *
   * @param node    the node
   * @throws Exception exception
   */
  private static void addCatalogueInCb(OdmsCatalogue node) throws Exception {
    HashMap<String, String> conf = FederationCore.getSettings();
    if (!conf.get("orionUrl").equals("")) {
      logger.info(" -- Context Broker URL: " + conf.get("orionUrl"));
    
      logger.info("Started CB Federation");
      node.setSynchLockOrion(OdmsSynchLock.PERIODIC);
      Map<String, String> headers = new HashMap<String, String>();
      headers.put("Content-Type", "application/json");
      RestClient client = new RestClientImpl();
      
      String api = urlOrionmanager + "startProcess";
      String data = "{ \"catalogueId\": \"" + node.getId() + "\", \"contextBrokerUrl\": \"" 
          + conf.get("orionUrl") + "\"  }";
      logger.info("Sending configurations: "  + data);
      
      HttpResponse response = client.sendPostRequest(api, data,
          MediaType.APPLICATION_JSON_TYPE, headers); 
      int status = client.getStatus(response);
   
      if (status != 200 && status != 207 && status != 204 && status != -1 
          && status != 201 && status != 301) {
        throw new Exception("STATUS POST Add catalogue in the BROKER MANAGER: " + status);
      
      } else {
        if (status == -1) {     // Case in which the Broker Manager is turned off
          node.setFederatedInCb(false);
        } else {
          node.setFederatedInCb(true);
        }
        logger.info("Catalogue FEDERATED in the CB: " + node.isFederatedInCb());
        node.setSynchLockOrion(OdmsSynchLock.NONE);
      }
    } else {
      logger.info("Context Broker NOT enabled.");
    }  
  }

  /**
   * Delete dataset.
   *
   * @param node    the node
   * @param dataset the dataset
   * @return the int
   */
  static int deleteDataset(OdmsCatalogue node, DcatDataset dataset) {
    int deletedRdf = 0;
    CachePersistenceManager jpa = new CachePersistenceManager();
    try {
      List<DcatDistribution> distributionList = dataset.getDistributions();
      MetadataCacheManager.deleteDataset(node.getId(), dataset);

      if (distributionList != null && !distributionList.isEmpty()) {
        for (DcatDistribution d : distributionList) {
          if (d.isRdf() && enableRdf) {

            logger.info("Deleting RDF - " + d.getAccessUrl().getValue());
            try {
              LodCacheManager.deleteRdf(d.getAccessUrl().getValue());
              logger.info("Deleting RDF - " + d.getAccessUrl().getValue() + "successfully");
              deletedRdf++;
            } catch (RepositoryException e) {
              logger.error("Deleting RDF - " + d.getAccessUrl().getValue()
                  + "unable to complete the deletion: " + e.getLocalizedMessage());
            }
          }

          if (d.isHasDatalets()) {
            jpa.jpaDeleteDataletByDstributionId(d.getId());
          }
        }
      }

      // if (deletedRDF != 0)
      // logger.info("deletedRDF: " + deletedRDF);
    } catch (DatasetNotFoundException | SolrServerException | IOException ex) {
      ex.printStackTrace();
      logger.info("\nDataset is already deleted\n");
      logger.error("Dataset is already deleted");
    } finally {
      jpa.jpaClose();
    }

    return deletedRdf;
  }

  /**
   * Adds the dataset.
   *
   * @param node    the node
   * @param dataset the dataset
   * @return the int
   */
  public static int addDataset(OdmsCatalogue node, DcatDataset dataset) {
    int addedRdf = 0;

    logger.info("\n--- Creating dataset ---" + dataset.getId() + " " + dataset.getTitle().getValue()
        + "\n");
    try {
      // MetadataCacheManager.getDataset(dataset.getId(),false);
      MetadataCacheManager.getDatasetByIdentifier(node.getId(), dataset.getIdentifier().getValue());
      logger.info("Dataset is already present");
    } catch (DatasetNotFoundException ex) {
      // If a dataset is not found, create one
      logger.info(ex.getMessage() + "\n - Adding new dataset -\n");

      try {
        MetadataCacheManager.addDataset(dataset);
        List<DcatDistribution> distributionList = dataset.getDistributions();
        // Add all RDF distributions on LOD
        // Repository
        if (distributionList != null && !distributionList.isEmpty()) {
          for (DcatDistribution d : distributionList) {
            if (d.isRdf() && enableRdf) {

              logger.info("Adding new RDF - " + d.getAccessUrl().getValue());

              try {
                addedRdf += LodCacheManager.addRdf(d.getAccessUrl().getValue());
                // logger.info("Adding
                // RDF completed
                // successfully");
                // addedRDF++;
              } catch (RepositoryException | IOException e1) {
                logger.info("There was an error while adding the RDF:   " + e1.getMessage());
                logger.error("There was an error while adding the RDF");
              }

              // logger.info("addedRDF: "
              // + addedRDF);
            }
          }
        }

        if (addedRdf != 0) {
          logger.info("Adding RDF completed successfully");
        }

      } catch (EntityExistsException | SolrServerException | IOException e) {
        logger
            .error("--- The dataset" + dataset.getId() + "is already present and then skipped ---");
      }
    } catch (SolrServerException | IOException eex) {
      logger.debug(eex.getLocalizedMessage());
    }

    return addedRdf;
  }

  /**
   * Update dataset.
   *
   * @param node    the node
   * @param dataset the dataset
   * @return the int
   */
  public static int updateDataset(OdmsCatalogue node, DcatDataset dataset) {
    int updatedRdf = 0;
    try {
      MetadataCacheManager.updateDataset(node.getId(), dataset);
      List<DcatDistribution> distributionList = dataset.getDistributions();

      // Update all RDF distributions on LOD
      // Repository
      if (distributionList != null && !distributionList.isEmpty()) {
        for (DcatDistribution d : distributionList) {
          if (d.isRdf()) {

            logger.info("Updating RDF - " + d.getAccessUrl().getValue());
            if (node.getNodeType().equals(OdmsCatalogueType.SOCRATA)
                || node.getNodeType().equals(OdmsCatalogueType.DKAN)) {
              d.getAccessUrl().setValue(d.getAccessUrl().getValue().split("\\?")[0]);
            }
            if (enableRdf) {
              try {

                updatedRdf += LodCacheManager.updateRdf(d.getAccessUrl().getValue());
              } catch (IOException | RepositoryException e) {
                logger.error("Unable to update rdf: " + d.getAccessUrl().getValue() + " "
                    + e.getLocalizedMessage());
              }
            }
          }
        }
      }
      if (updatedRdf != 0) {
        logger.info("Updating RDF...");
        logger.info("updatedRDF: " + updatedRdf);
      }
    } catch (DatasetNotFoundException e) {
      return addDataset(node, dataset);
    } catch (SolrServerException | IOException e) {
      logger.error("Unable to update dataset: " + dataset.getId() + " " + e.getLocalizedMessage());
    }
    return updatedRdf;
  }

}
