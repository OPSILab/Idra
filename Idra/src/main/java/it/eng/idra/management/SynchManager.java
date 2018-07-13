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
package it.eng.idra.management;

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
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import javax.persistence.EntityExistsException;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFParseException;

import it.eng.idra.beans.ODFProperty;
import it.eng.idra.beans.dcat.DCATAPFormat;
import it.eng.idra.beans.dcat.DCATAPProfile;
import it.eng.idra.beans.dcat.DCATAPWriteType;
import it.eng.idra.beans.dcat.DCATDataset;
import it.eng.idra.beans.dcat.DCATDistribution;
import it.eng.idra.beans.exception.DatasetNotFoundException;
import it.eng.idra.beans.odms.ODMSManagerException;
import it.eng.idra.beans.odms.ODMSCatalogue;
import it.eng.idra.beans.odms.ODMSCatalogueFederationLevel;
import it.eng.idra.beans.odms.ODMSCatalogueForbiddenException;
import it.eng.idra.beans.odms.ODMSCatalogueNotFoundException;
import it.eng.idra.beans.odms.ODMSCatalogueState;
import it.eng.idra.beans.odms.ODMSCatalogueType;
import it.eng.idra.beans.odms.ODMSSynchLock;
import it.eng.idra.beans.odms.ODMSSynchronizationResult;
import it.eng.idra.beans.search.SearchResult;
import it.eng.idra.cache.CachePersistenceManager;
import it.eng.idra.cache.LODCacheManager;
import it.eng.idra.cache.MetadataCacheManager;
import it.eng.idra.dcat.dump.DCATAPSerializer;
import it.eng.idra.odfscheduler.ODFScheduler;
import it.eng.idra.odfscheduler.SchedulerNotInitialisedException;
import it.eng.idra.utils.CommonUtil;
import it.eng.idra.utils.PropertyManager;

public class SynchManager {

	// Map between node id and its synch timer
//	private static HashMap<Integer, Timer> synchODMSNodeScheduler = new HashMap<Integer, Timer>();
//	private static Logger logger = LogManager.getLogger(SynchManager.class);

//	static ODMSSynchronizationResult getChangedDatasets(ODMSCatalogue node, List<DCATDataset> oldDatasets,
//			String startingDate) throws Exception {
//
//		ODMSSynchronizationResult nodeDatasets = new ODMSSynchronizationResult();
//
//		// The connector class is loaded, based on ODMS node type, using Java
//		// reflection
//		nodeDatasets = ODMSManager.getODMSNodeConnector(node).getChangedDatasets(oldDatasets, startingDate);
//		System.gc();
//
//		return nodeDatasets;
//	}
//
//	private static void synchWebODMSNode(ODMSCatalogue node)
//			throws InvocationTargetException, IOException, SolrServerException, DatasetNotFoundException {
//
//		MetadataCacheManager.deleteAllDatasetsByODMSNode(node);
//		MetadataCacheManager.loadCacheFromODMSNode(node);
//		StatisticsManager.odmsStatistics(node, node.getDatasetCount(), 0, 0, node.getRdfCount(), 0, 0);
//
//	}
//
//	private static void synchDUMPODMSNode(ODMSCatalogue node)
//			throws InvocationTargetException, IOException, SolrServerException, DatasetNotFoundException {
//
//		MetadataCacheManager.deleteAllDatasetsByODMSNode(node);
//
//		if (StringUtils.isBlank(node.getDumpURL()) && StringUtils.isBlank(node.getDumpString())
//				&& StringUtils.isNotBlank(node.getDumpFilePath())) {
//
//			// Read the content of the file from the file system
//			String dumpString = new String(Files.readAllBytes(Paths.get(node.getDumpFilePath())));
//			node.setDumpString(dumpString);
//
//		}
//
//		MetadataCacheManager.loadCacheFromODMSNode(node);
//		StatisticsManager.odmsStatistics(node, node.getDatasetCount(), 0, 0, node.getRdfCount(), 0, 0);
//
//	}
//
//	protected static boolean synchODMSNode(ODMSCatalogue node) throws SQLException, IOException, SolrServerException,
//			ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
//			NoSuchMethodException, SecurityException, DatasetNotFoundException, RepositoryException, RDFParseException,
//			ODMSCatalogueNotFoundException, ODMSCatalogueForbiddenException, ODMSManagerException {
//
//		if (!node.isFederating()) {
//			logger.info("Starting synchronization for node: " + node.getName() + " ID: " + node.getId());
//
//			ODMSSynchronizationResult synchroResult = new ODMSSynchronizationResult();
//			List<DCATDataset> presentDatasets = new ArrayList<DCATDataset>();
//			int addedDatasets = 0, deletedDatasets = 0, updatedDatasets = 0, addedRDF = 0, deletedRDF = 0,
//					updatedRDF = 0;
//
//			boolean first = false;
//			if (node.getDatasetStart() != -1)
//				first = true;
//
//			// Check first the current node state
//			node.setNodeState(ODMSManager.checkODMSNode(node));
//
//			logger.info("The ODMS Node with name: " + node.getName() + " and ID: " + node.getId() + " is "
//					+ node.getNodeState());
//
//			/*
//			 * If the node is Online is possible to retrieve only changed datasets
//			 * ********************
//			 */
//			ZonedDateTime lastUpdate = node.getLastUpdateDate();
//
//			if (node.isOnline() && !first) {
//
//				try {
//
//					if (!node.getNodeType().equals(ODMSCatalogueType.WEB)
//							&& !node.getNodeType().equals(ODMSCatalogueType.DCATDUMP)) {
//
//						if (!node.getNodeType().equals(ODMSCatalogueType.CKAN))
//							presentDatasets = MetadataCacheManager.getAllDatasetsByODMSNode(node.getId());
//
//						synchroResult = getChangedDatasets(node, presentDatasets, CommonUtil.formatDate(lastUpdate));
//
//						for (DCATDataset dataset : synchroResult.getDeletedDatasets()) {
//							deletedRDF += SynchManager.deleteDataset(node, dataset);
//						}
//
//						for (DCATDataset dataset : synchroResult.getAddedDatasets()) {
//							addedRDF += SynchManager.addDataset(node, dataset);
//						}
//
//						for (DCATDataset dataset : synchroResult.getChangedDatasets()) {
//							updatedRDF += SynchManager.updateDataset(node, dataset);
//						}
//
//					} else if (node.getNodeType().equals(ODMSCatalogueType.WEB)) {
//						synchWebODMSNode(node);
//					} else if (node.getNodeType().equals(ODMSCatalogueType.DCATDUMP)) {
//						synchDUMPODMSNode(node);
//					}
//
//				} catch (Exception e) {
//					e.printStackTrace();
//					node.setLastUpdateDate(lastUpdate);
//					ODMSManager.insertODMSMessage(node.getId(), "Node OFFLINE");
//					logger.info("Setting node state to OFFLINE");
//					node.setNodeState(ODMSCatalogueState.OFFLINE);
//					ODMSManager.updateODMSNode(node, true);
//
//				}
//
//				/*
//				 * Otherwise if the node was offline during first synchronization, it is
//				 * necessary to load all datasets from node ****
//				 */
//
//			} else if (node.isOnline() && first)
//				try {
//					logger.info("LOAD CACHE FROM ODMS");
//					node.setDatasetCount(ODMSManager.countODMSNodeDatasets(node));
//					logger.info(node.getDatasetCount());
//					MetadataCacheManager.loadCacheFromODMSNode(node);
//					logger.info(node.getDatasetCount());
//					node.setNodeState(ODMSCatalogueState.ONLINE);
//					ODMSManager.updateODMSNode(node, true);
//				} catch (InvocationTargetException e) {
//					logger.info("Setting node state to OFFLINE");
//					node.setNodeState(ODMSCatalogueState.OFFLINE);
//					node.setDatasetCount(0);
//					ODMSManager.updateODMSNode(node, true);
//					node.setSynchLock(ODMSSynchLock.NONE);
//				}
//
//			addedDatasets = synchroResult.getAddedDatasets().size();
//			updatedDatasets = synchroResult.getChangedDatasets().size();
//			deletedDatasets = synchroResult.getDeletedDatasets().size();
//
//			// Update the dataset count of the federated Node
//			node.setDatasetCount(node.getDatasetCount() + addedDatasets - deletedDatasets);
//			node.setRdfCount(node.getRdfCount() + addedRDF - deletedRDF);
//			ODMSManager.updateODMSNode(node, true);
//
//			// Set the new last update date to the node
//			node.setLastUpdateDate(ZonedDateTime.now(ZoneOffset.UTC));
//			ODMSManager.updateODMSNode(node, true);
//
//			// LOG Operations
//			logger.info("Synchronization complete for node: " + node.getName() + " ID: " + node.getId());
//			logger.info("Added RDF = " + addedRDF);
//			logger.info("Updated RDF = " + updatedRDF);
//			logger.info("Deleted RDF = " + deletedRDF);
//			logger.info("Added Datasets : " + addedDatasets);
//			logger.info("Updated Datasets : " + updatedDatasets);
//			logger.info("Deleted Datasets : " + deletedDatasets);
//
//			// Adds dataset counters to the statistics on DB
//			StatisticsManager.odmsStatistics(node, addedDatasets, deletedDatasets, updatedDatasets, addedRDF,
//					updatedRDF, deletedRDF);
//			ODMSManager.insertODMSMessage(node.getId(), "Node successfully synchronized");			
//
//			// Creating the dump file for the node after the synchronization
//			try {
//				SearchResult result = MetadataCacheManager.getAllDatasetsByODMSNodeID(node.getId());
//				DCATAPSerializer.searchResultToDCATAPByNode(Integer.toString(node.getId()), result,
//						DCATAPFormat.fromString(PropertyManager.getProperty(ODFProperty.DUMP_FORMAT)),
//						DCATAPProfile.fromString(PropertyManager.getProperty(ODFProperty.DUMP_PROFILE)),
//						DCATAPWriteType.FILE);
//			} catch (Exception e1) {
//				e1.printStackTrace();
//				logger.error("Error: " + e1.getMessage() + " in creation of the dump file for node " + node.getId());
//			}
//
//			return true;
//		} else {
//			return false;
//		}
//
//	}

//	protected static void stopSynchScheduler() {
//		for (Entry<Integer, Timer> e : synchODMSNodeScheduler.entrySet()) {
//			e.getValue().cancel();
//		}
//	}

//	static void addODMSNodeSynchTimer(ODMSCatalogue node, boolean startNow) {
//
//		// Set the new timer for node synchronization and add it to the
//		// timers list
////		Timer timer = new Timer(true);
////		timer.scheduleAtFixedRate(new TimerTask() {
////			public void run() {
////				try {
////					// if(!node.getNodeType().equals(ODMSCatalogueType.SOCRATA)
////
////					if (!node.isFederating()) {
////						node.setSynchLock(ODMSSynchLock.PERIODIC);
////						ODMSManager.updateODMSNode(node, false);
////						try {
////							synchODMSNode(node);
////						} catch (ODMSCatalogueNotFoundException | ODMSCatalogueForbiddenException e) {
////							e.printStackTrace();
////						}
////						node.setSynchLock(ODMSSynchLock.NONE);
////						ODMSManager.updateODMSNode(node, false);
////					}
////				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
////						| IllegalArgumentException | NoSuchMethodException | SecurityException | SQLException
////						| IOException | SolrServerException | DatasetNotFoundException | RepositoryException
////						| RDFParseException | ODMSCatalogueNotFoundException | ODMSManagerException e) {
////					node.setSynchLock(ODMSSynchLock.NONE);
////					e.printStackTrace();
////				}
////			}
////		}, startNow ? 0 : node.getRefreshPeriod() * 1000, node.getRefreshPeriod() * 1000);
////
////		synchODMSNodeScheduler.put(node.getId(), timer);
//
//	}

//	static void deleteODMSNodeSynchTimer(int nodeId) {
//
////		Timer timer = synchODMSNodeScheduler.remove(nodeId);
////		if (timer != null)
////			timer.cancel();
////
////		timer = null;
//
//	}

//	static int deleteDataset(ODMSCatalogue node, DCATDataset dataset) {
//		int deletedRDF = 0;
//		CachePersistenceManager jpa = new CachePersistenceManager();
//		try {
//			List<DCATDistribution> distributionList = dataset.getDistributions();
//			MetadataCacheManager.deleteDataset(node.getId(), dataset);
//
//			if (distributionList != null && !distributionList.isEmpty())
//				for (DCATDistribution d : distributionList) {
//					if (d.isRDF()) {
//
//						logger.info("Deleting RDF - " + d.getAccessURL().getValue());
//						try {
//							LODCacheManager.deleteRDF(d.getAccessURL().getValue());
//							logger.info("Deleting RDF - " + d.getAccessURL().getValue() + "successfully");
//							deletedRDF++;
//						} catch (RepositoryException e) {
//							logger.error("Deleting RDF - " + d.getAccessURL().getValue()
//									+ "unable to complete the deletion: " + e.getLocalizedMessage());
//						}
//					}
//
//					if (d.isHasDatalets()) {
//						jpa.jpaDeleteDataletByDstributionID(d.getId());
//					}
//				}
//
//			// if (deletedRDF != 0)
//			// logger.info("deletedRDF: " + deletedRDF);
//		} catch (DatasetNotFoundException | SolrServerException | IOException ex) {
//			ex.printStackTrace();
//			logger.info("\nDataset is already deleted\n");
//			logger.error("Dataset is already deleted");
//		} finally {
//			jpa.jpaClose();
//		}
//
//		return deletedRDF;
//	}
//
//	static int addDataset(ODMSCatalogue node, DCATDataset dataset) {
//		int addedRDF = 0;
//
//		logger.info("\n--- Creating dataset ---" + dataset.getId() + " " + dataset.getTitle().getValue() + "\n");
//		try {
//			// MetadataCacheManager.getDataset(dataset.getId(),false);
//			MetadataCacheManager.getDataset(node.getId(), dataset.getOtherIdentifier().get(0).getValue());
//			logger.info("Dataset is already present");
//		} catch (DatasetNotFoundException ex) {
//			// If a dataset is not found, create one
//			logger.info(ex.getMessage() + "\n - Adding new dataset -\n");
//
//			try {
//				MetadataCacheManager.addDataset(dataset);
//				List<DCATDistribution> distributionList = dataset.getDistributions();
//				// Add all RDF distributions on LOD
//				// Repository
//				if (distributionList != null && !distributionList.isEmpty())
//					for (DCATDistribution d : distributionList) {
//						if (d.isRDF()) {
//
//							logger.info("Adding new RDF - " + d.getAccessURL().getValue());
//
//							try {
//								addedRDF += LODCacheManager.addRDF(d.getAccessURL().getValue());
//								// logger.info("Adding
//								// RDF completed
//								// successfully");
//								// addedRDF++;
//							} catch (RepositoryException | IOException e1) {
//								logger.info("There was an error while adding the RDF:   " + e1.getMessage());
//								logger.error("There was an error while adding the RDF");
//							}
//
//							// logger.info("addedRDF: "
//							// + addedRDF);
//						}
//					}
//
//				if (addedRDF != 0)
//					logger.info("Adding RDF completed successfully");
//
//			} catch (EntityExistsException | SolrServerException | IOException e) {
//				logger.error("--- The dataset" + dataset.getId() + "is already present and then skipped ---");
//			}
//		} catch (SolrServerException | IOException eex) {
//
//		}
//
//		return addedRDF;
//	}
//
//	static int updateDataset(ODMSCatalogue node, DCATDataset dataset) {
//		int updatedRDF = 0;
//		try {
//			MetadataCacheManager.updateDataset(node.getId(), dataset);
//			List<DCATDistribution> distributionList = dataset.getDistributions();
//
//			// Update all RDF distributions on LOD
//			// Repository
//			if (distributionList != null && !distributionList.isEmpty())
//				for (DCATDistribution d : distributionList) {
//					if (d.isRDF()) {
//
//						logger.info("Updating RDF - " + d.getAccessURL().getValue());
//						if (node.getNodeType().equals(ODMSCatalogueType.SOCRATA))
//							d.getAccessURL().setValue(d.getAccessURL().getValue().split("\\?")[0]);
//						try {
//							updatedRDF += LODCacheManager.updateRDF(d.getAccessURL().getValue());
//						} catch (IOException | RepositoryException e) {
//							logger.error("Unable to update rdf: " + d.getAccessURL().getValue() + " "
//									+ e.getLocalizedMessage());
//						}
//
//					}
//				}
//			if (updatedRDF != 0) {
//				logger.info("Updating RDF...");
//				logger.info("updatedRDF: " + updatedRDF);
//			}
//		} catch (DatasetNotFoundException e) {
//			return addDataset(node, dataset);
//		} catch (SolrServerException | IOException e) {
//			logger.error("Unable to update dataset: " + dataset.getId() + " " + e.getLocalizedMessage());
//		}
//		return updatedRDF;
//	}

//	protected static void initSynchScheduler(boolean startNow) {
//
//		for (final ODMSCatalogue node : ODMSManager.getODMSNodesbyFederationLevel(ODMSCatalogueFederationLevel.LEVEL_2,
//				ODMSCatalogueFederationLevel.LEVEL_3)) {
//			if(node.isActive()) {
//				logger.info("Node Name: " + node.getName());
//				logger.info("Node Lock: " + node.getSynchLock());
//				logger.info("Node Datasets Count: " + node.getDatasetCount());
//				logger.info("Node Start offset: " + node.getDatasetStart());
//								
////				Timer timer = new Timer(true);
////				timer.scheduleAtFixedRate(new TimerTask() {
////					public void run() {
////						try {
////
////							if (!node.isFederating() && node.isUnlocked()) {
////								node.setSynchLock(ODMSSynchLock.PERIODIC);
////								ODMSManager.updateODMSNode(node, false);
////								try {
////									synchODMSNode(node);
////								} catch (ODMSCatalogueNotFoundException e) {
////
////									e.printStackTrace();
////								} catch (ODMSCatalogueForbiddenException e) {
////									logger.info(e.getMessage());
////									node.setSynchLock(ODMSSynchLock.NONE);
////
////								}
////								node.setSynchLock(ODMSSynchLock.NONE);
////								ODMSManager.updateODMSNode(node, false);
////							}
////						} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
////								| IllegalArgumentException | NoSuchMethodException | SecurityException | SQLException
////								| IOException | SolrServerException | DatasetNotFoundException | RepositoryException
////								| RDFParseException | ODMSCatalogueNotFoundException | ODMSManagerException e) {
////
////							e.printStackTrace();
////						}
////					}
////				}, startNow ? 0 : node.getRefreshPeriod() * 1000, node.getRefreshPeriod() * 1000);
////
////				synchODMSNodeScheduler.put(node.getId(), timer);
//
//			}
//		}
//	}

	// protected static void synchODMSNodes() throws SQLException {
	//
	// for (final ODMSCatalogue node :
	// ODMSManager.getODMSNodesbyFederationLevel(ODMSCatalogueFederationLevel.LEVEL_2,
	// ODMSCatalogueFederationLevel.LEVEL_3)) {
	// logger.info("Node Name: " + node.getName());
	// logger.info("Node Lock: " + node.getSynchLock());
	// logger.info("Node Datasets Count: " + node.getDatasetCount());
	// logger.info("Node Start offset: " + node.getDatasetStart());
	// Timer timer = new Timer(true);
	// timer.scheduleAtFixedRate(new TimerTask() {
	// public void run() {
	// try {
	//
	// if (!node.isFederating() &&
	// ODMSManager.getODMSNode(node.getId()).isUnlocked()) {
	// node.setSynchLock(ODMSSynchLock.PERIODIC);
	// ODMSManager.updateODMSNode(node, false);
	// try {
	// FederationCore.synchODMSNode(node);
	// } catch (ODMSCatalogueNotFoundException e) {
	//
	// e.printStackTrace();
	// } catch (ODMSCatalogueForbiddenException e) {
	// logger.info(e.getMessage());
	// node.setSynchLock(ODMSSynchLock.NONE);
	//
	// }
	// node.setSynchLock(ODMSSynchLock.NONE);
	// ODMSManager.updateODMSNode(node, false);
	// }
	// } catch (ClassNotFoundException | InstantiationException |
	// IllegalAccessException
	// | IllegalArgumentException | NoSuchMethodException | SecurityException |
	// SQLException
	// | IOException | SolrServerException | DatasetNotFoundException |
	// RepositoryException
	// | RDFParseException | ODMSCatalogueNotFoundException e) {
	//
	// e.printStackTrace();
	// }
	// }
	// }, 0, node.getRefreshPeriod() * 1000);
	//
	// synchODMSNodeScheduler.put(node.getId(), timer);
	//
	// }
	// }
	//
	// protected static void synchODMSNodesDelayed() throws SQLException {
	//
	// for (final ODMSCatalogue node :
	// ODMSManager.getODMSNodesbyFederationLevel(ODMSCatalogueFederationLevel.LEVEL_2,
	// ODMSCatalogueFederationLevel.LEVEL_3)) {
	// logger.info("Node Name: " + node.getName());
	// logger.info("Node Lock: " + node.getSynchLock());
	// logger.info("Node Datasets Count: " + node.getDatasetCount());
	// logger.info("Node Start offset: " + node.getDatasetStart());
	// logger.info("Delayed Start");
	// Timer timer = new Timer(true);
	// timer.scheduleAtFixedRate(new TimerTask() {
	// public void run() {
	// try {
	//
	// if (!node.isFederating() &&
	// ODMSManager.getODMSNode(node.getId()).isUnlocked()) {
	// node.setSynchLock(ODMSSynchLock.PERIODIC);
	// ODMSManager.updateODMSNode(node, false);
	// try {
	// FederationCore.synchODMSNode(node);
	// } catch (ODMSCatalogueNotFoundException e) {
	//
	// e.printStackTrace();
	// } catch (ODMSCatalogueForbiddenException e) {
	// logger.info(e.getMessage());
	// node.setSynchLock(ODMSSynchLock.NONE);
	//
	// }
	// node.setSynchLock(ODMSSynchLock.NONE);
	// ODMSManager.updateODMSNode(node, false);
	// }
	// } catch (ClassNotFoundException | InstantiationException |
	// IllegalAccessException
	// | IllegalArgumentException | NoSuchMethodException | SecurityException |
	// SQLException
	// | IOException | SolrServerException | DatasetNotFoundException |
	// RepositoryException
	// | RDFParseException | ODMSCatalogueNotFoundException e) {
	//
	// e.printStackTrace();
	// }
	// }
	// }, node.getRefreshPeriod() * 1000, node.getRefreshPeriod() * 1000);
	//
	// synchODMSNodeScheduler.put(node.getId(), timer);
	//
	// }
	// }

	// private static String[] getNodeDatasetsID(ODMSCatalogue node) throws
	// Exception, RepositoryException {
	//
	// HashMap<ODMSCatalogueType, String> ODMSConnectorsList =
	// FederationCore.getODMSConnectorsList();
	// ClassLoader loader = Thread.currentThread().getContextClassLoader();
	// ODMSConnector currentConnector;
	//
	// // Retrieves all datasets of the passed ODMS node
	//
	// // The connector class is loaded, based on ODMS node type, using Java
	// // reflection
	// Class<?> cls =
	// loader.loadClass(ODMSConnectorsList.get(node.getNodeType()));
	// currentConnector = (ODMSConnector)
	// cls.getDeclaredConstructor(ODMSCatalogue.class).newInstance(node);
	// Method m = cls.getDeclaredMethod("getAllDatasetsID");
	// String[] nodeDatasetsID = (String[]) m.invoke(currentConnector);
	//
	// currentConnector = null;
	// System.gc();
	// return nodeDatasetsID;
	// }
//
//	class Job extends Thread {
//
//		private Long sleepTime;
//
//		public boolean problem = false;
//
//		public Job(Long sleepTime) {
//			this.sleepTime = sleepTime;
//		}
//
//		@Override
//		public void run() {
//			while (true) {
//				synchronized (this) {
//					try {
//						Thread.sleep(sleepTime);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//
//			}
//
//		}
//	}

}
