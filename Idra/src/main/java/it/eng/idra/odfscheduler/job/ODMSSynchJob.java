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
package it.eng.idra.odfscheduler.job;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityExistsException;

import org.apache.commons.lang.StringUtils;
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

import it.eng.idra.beans.ODFProperty;
import it.eng.idra.beans.dcat.DCATAPFormat;
import it.eng.idra.beans.dcat.DCATAPProfile;
import it.eng.idra.beans.dcat.DCATAPWriteType;
import it.eng.idra.beans.dcat.DCATDataset;
import it.eng.idra.beans.dcat.DCATDistribution;
import it.eng.idra.beans.exception.DatasetNotFoundException;
import it.eng.idra.beans.odms.ODMSManagerException;
import it.eng.idra.beans.odms.ODMSCatalogue;
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
import it.eng.idra.management.ODMSManager;
import it.eng.idra.management.StatisticsManager;
import it.eng.idra.utils.CommonUtil;
import it.eng.idra.utils.PropertyManager;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class ODMSSynchJob implements InterruptableJob{

	private static Logger logger = LogManager.getLogger(ODMSSynchJob.class);
	private static Boolean enableRdf = Boolean.parseBoolean(PropertyManager.getProperty(ODFProperty.ENABLE_RDF));

	public ODMSSynchJob() {}
	
	@Override
	public void interrupt() throws UnableToInterruptJobException {
		// TODO Auto-generated method stub
		logger.info("Interrupting job");
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub
		try {
			logger.info("Starting synch job for catalogue: "+context.getJobDetail().getJobDataMap().get("nodeID"));
			ODMSCatalogue node = ODMSManager.getODMSCatalogue((int) context.getJobDetail().getJobDataMap().get("nodeID"));
			if (!node.isFederating() && node.isUnlocked() && node.isActive()) {
				node.setSynchLock(ODMSSynchLock.PERIODIC);
				ODMSManager.updateODMSCatalogue(node, false);
				try {
					synchODMSNode(node);
				} catch (ODMSCatalogueNotFoundException e) {
					e.printStackTrace();
				} catch (ODMSCatalogueForbiddenException e) {
					logger.info(e.getMessage());
//					node.setSynchLock(ODMSSynchLock.NONE);
				} finally{
					node.setSynchLock(ODMSSynchLock.NONE);
					ODMSManager.updateODMSCatalogue(node, false);
				}
			}
		}catch(ODMSCatalogueNotFoundException e){
			e.printStackTrace();
//			JobExecutionException e2 =
//	        		new JobExecutionException(e);
//			e2.setUnscheduleFiringTrigger(true);
		} catch (Exception e) {

			e.printStackTrace();
		}
	}
	
	static ODMSSynchronizationResult getChangedDatasets(ODMSCatalogue node, List<DCATDataset> oldDatasets,
			String startingDate) throws Exception {

		ODMSSynchronizationResult nodeDatasets = new ODMSSynchronizationResult();

		// The connector class is loaded, based on ODMS node type, using Java
		// reflection
		nodeDatasets = ODMSManager.getODMSCatalogueConnector(node).getChangedDatasets(oldDatasets, startingDate);
		System.gc();

		return nodeDatasets;
	}

	private static void synchWebODMSNode(ODMSCatalogue node)
			throws InvocationTargetException, IOException, SolrServerException, DatasetNotFoundException {

		MetadataCacheManager.deleteAllDatasetsByODMSCatalogue(node);
		MetadataCacheManager.loadCacheFromODMSCatalogue(node);
		StatisticsManager.odmsStatistics(node, node.getDatasetCount(), 0, 0, node.getRdfCount(), 0, 0);

	}

	private static void synchDUMPODMSNode(ODMSCatalogue node)
			throws InvocationTargetException, IOException, SolrServerException, DatasetNotFoundException {

		MetadataCacheManager.deleteAllDatasetsByODMSCatalogue(node);

		if (StringUtils.isBlank(node.getDumpURL()) && StringUtils.isBlank(node.getDumpString())
				&& StringUtils.isNotBlank(node.getDumpFilePath())) {

			// Read the content of the file from the file system
			String dumpString = new String(Files.readAllBytes(Paths.get(node.getDumpFilePath())));
			node.setDumpString(dumpString);

		}

		MetadataCacheManager.loadCacheFromODMSCatalogue(node);
		StatisticsManager.odmsStatistics(node, node.getDatasetCount(), 0, 0, node.getRdfCount(), 0, 0);

	}

	protected static boolean synchODMSNode(ODMSCatalogue node) throws SQLException, IOException, SolrServerException,
			ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			NoSuchMethodException, SecurityException, DatasetNotFoundException, RepositoryException, RDFParseException,
			ODMSCatalogueNotFoundException, ODMSCatalogueForbiddenException, ODMSManagerException {

		if (!node.isFederating()) {
			logger.info("Starting synchronization for node: " + node.getName() + " ID: " + node.getId());

			ODMSSynchronizationResult synchroResult = new ODMSSynchronizationResult();
			List<DCATDataset> presentDatasets = new ArrayList<DCATDataset>();
			int addedDatasets = 0, deletedDatasets = 0, updatedDatasets = 0, addedRDF = 0, deletedRDF = 0,
					updatedRDF = 0;

			boolean synchCompleted = true;
			
			boolean first = false;
			if (node.getDatasetStart() != -1)
				first = true;

			// Check first the current node state
			node.setNodeState(ODMSManager.checkODMSCatalogue(node));

			logger.info("The ODMS Node with name: " + node.getName() + " and ID: " + node.getId() + " is "
					+ node.getNodeState());

			/*
			 * If the node is Online is possible to retrieve only changed datasets
			 * ********************
			 */
			ZonedDateTime lastUpdate = node.getLastUpdateDate();

			if (node.isOnline() && !first) {

				try {

//					if (!node.getNodeType().equals(ODMSCatalogueType.DCATDUMP)) {

						if (!node.getNodeType().equals(ODMSCatalogueType.CKAN))
							presentDatasets = MetadataCacheManager.getAllDatasetsByODMSCatalogue(node.getId());

						synchroResult = getChangedDatasets(node, presentDatasets, CommonUtil.formatDate(lastUpdate));

						for (DCATDataset dataset : synchroResult.getDeletedDatasets()) {
							deletedRDF += ODMSSynchJob.deleteDataset(node, dataset);
						}

						for (DCATDataset dataset : synchroResult.getAddedDatasets()) {
							addedRDF += ODMSSynchJob.addDataset(node, dataset);
						}

						for (DCATDataset dataset : synchroResult.getChangedDatasets()) {
							updatedRDF += ODMSSynchJob.updateDataset(node, dataset);
						}

//					} else if (node.getNodeType().equals(ODMSCatalogueType.DCATDUMP)) {
//						// Do nothing for node type DUMP
//						synchDUMPODMSNode(node);
//					}

				} catch (Exception e) {
					synchCompleted = false;
					e.printStackTrace();
					node.setLastUpdateDate(lastUpdate);
					ODMSManager.insertODMSMessage(node.getId(), "Node OFFLINE "+e.getLocalizedMessage());
					logger.error("Setting node state to OFFLINE "+e.getLocalizedMessage());
					node.setNodeState(ODMSCatalogueState.OFFLINE);
					node.setSynchLock(ODMSSynchLock.NONE);
					ODMSManager.updateODMSCatalogue(node, true);
				}

				/*
				 * Otherwise if the node was offline during first synchronization, it is
				 * necessary to load all datasets from node ****
				 */

			} else if (node.isOnline() && first)
				try {
					logger.info("LOAD CACHE FROM ODMS");
					node.setDatasetCount(ODMSManager.countODMSCatalogueDatasets(node));
					logger.info(node.getDatasetCount());
					MetadataCacheManager.loadCacheFromODMSCatalogue(node);
					logger.info(node.getDatasetCount());
					node.setNodeState(ODMSCatalogueState.ONLINE);
					
				} catch (InvocationTargetException e) {
					synchCompleted = false;
					logger.info("Setting node state to OFFLINE "+e.getLocalizedMessage());
					ODMSManager.insertODMSMessage(node.getId(), "Node OFFLINE "+e.getLocalizedMessage());
					node.setNodeState(ODMSCatalogueState.OFFLINE);
					node.setDatasetCount(0);
//					ODMSManager.updateODMSNode(node, true);
				}finally{
					node.setSynchLock(ODMSSynchLock.NONE);
					ODMSManager.updateODMSCatalogue(node, true);
				}

			if(synchCompleted) {
				
				addedDatasets = synchroResult.getAddedDatasets().size();
				updatedDatasets = synchroResult.getChangedDatasets().size();
				deletedDatasets = synchroResult.getDeletedDatasets().size();

				// Update the dataset count of the federated Node
				node.setDatasetCount(node.getDatasetCount() + addedDatasets - deletedDatasets);
				node.setRdfCount(node.getRdfCount() + addedRDF - deletedRDF);
				ODMSManager.updateODMSCatalogue(node, true);

				// Set the new last update date to the node
				node.setLastUpdateDate(ZonedDateTime.now(ZoneOffset.UTC));
				ODMSManager.updateODMSCatalogue(node, true);

				// LOG Operations
				logger.info("Synchronization complete for node: " + node.getName() + " ID: " + node.getId());
				logger.info("Added RDF = " + addedRDF);
				logger.info("Updated RDF = " + updatedRDF);
				logger.info("Deleted RDF = " + deletedRDF);
				logger.info("Added Datasets : " + addedDatasets);
				logger.info("Updated Datasets : " + updatedDatasets);
				logger.info("Deleted Datasets : " + deletedDatasets);

				// Adds dataset counters to the statistics on DB
				StatisticsManager.odmsStatistics(node, addedDatasets, deletedDatasets, updatedDatasets, addedRDF,
						updatedRDF, deletedRDF);
				ODMSManager.insertODMSMessage(node.getId(), "Node successfully synchronized");			

				// Creating the dump file for the node after the synchronization
				try {
					SearchResult result = MetadataCacheManager.getAllDatasetsByODMSCatalogueID(node.getId());
					DCATAPSerializer.searchResultToDCATAPByNode(Integer.toString(node.getId()), result,
							DCATAPFormat.fromString(PropertyManager.getProperty(ODFProperty.DUMP_FORMAT)),
							DCATAPProfile.fromString(PropertyManager.getProperty(ODFProperty.DUMP_PROFILE)),
							DCATAPWriteType.FILE);
				} catch (Exception e1) {
					e1.printStackTrace();
					logger.error("Error: " + e1.getMessage() + " in creation of the dump file for node " + node.getId());
				}
			}
			return true;
		} else {
			return false;
		}

	}
	
	static int deleteDataset(ODMSCatalogue node, DCATDataset dataset) {
		int deletedRDF = 0;
		CachePersistenceManager jpa = new CachePersistenceManager();
		try {
			List<DCATDistribution> distributionList = dataset.getDistributions();
			MetadataCacheManager.deleteDataset(node.getId(), dataset);

			if (distributionList != null && !distributionList.isEmpty())
				for (DCATDistribution d : distributionList) {
					if (d.isRDF() && enableRdf) {

						logger.info("Deleting RDF - " + d.getAccessURL().getValue());
						try {
							LODCacheManager.deleteRDF(d.getAccessURL().getValue());
							logger.info("Deleting RDF - " + d.getAccessURL().getValue() + "successfully");
							deletedRDF++;
						} catch (RepositoryException e) {
							logger.error("Deleting RDF - " + d.getAccessURL().getValue()
									+ "unable to complete the deletion: " + e.getLocalizedMessage());
						}
					}

					if (d.isHasDatalets()) {
						jpa.jpaDeleteDataletByDstributionID(d.getId());
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

		return deletedRDF;
	}

	static int addDataset(ODMSCatalogue node, DCATDataset dataset) {
		int addedRDF = 0;

		logger.info("\n--- Creating dataset ---" + dataset.getId() + " " + dataset.getTitle().getValue() + "\n");
		try {
			// MetadataCacheManager.getDataset(dataset.getId(),false);
			MetadataCacheManager.getDatasetByIdentifier(node.getId(), dataset.getIdentifier().getValue());
			logger.info("Dataset is already present");
		} catch (DatasetNotFoundException ex) {
			// If a dataset is not found, create one
			logger.info(ex.getMessage() + "\n - Adding new dataset -\n");

			try {
				MetadataCacheManager.addDataset(dataset);
				List<DCATDistribution> distributionList = dataset.getDistributions();
				// Add all RDF distributions on LOD
				// Repository
				if (distributionList != null && !distributionList.isEmpty())
					for (DCATDistribution d : distributionList) {
						if (d.isRDF() && enableRdf) {

							logger.info("Adding new RDF - " + d.getAccessURL().getValue());

							try {
								addedRDF += LODCacheManager.addRDF(d.getAccessURL().getValue());
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

				if (addedRDF != 0)
					logger.info("Adding RDF completed successfully");

			} catch (EntityExistsException | SolrServerException | IOException e) {
				logger.error("--- The dataset" + dataset.getId() + "is already present and then skipped ---");
			}
		} catch (SolrServerException | IOException eex) {

		}

		return addedRDF;
	}

	static int updateDataset(ODMSCatalogue node, DCATDataset dataset) {
		int updatedRDF = 0;
		try {
			MetadataCacheManager.updateDataset(node.getId(), dataset);
			List<DCATDistribution> distributionList = dataset.getDistributions();

			// Update all RDF distributions on LOD
			// Repository
			if (distributionList != null && !distributionList.isEmpty())
				for (DCATDistribution d : distributionList) {
					if (d.isRDF()) {

						logger.info("Updating RDF - " + d.getAccessURL().getValue());
						if (node.getNodeType().equals(ODMSCatalogueType.SOCRATA) || node.getNodeType().equals(ODMSCatalogueType.DKAN))
							d.getAccessURL().setValue(d.getAccessURL().getValue().split("\\?")[0]);
						if(enableRdf) {
							try {

								updatedRDF += LODCacheManager.updateRDF(d.getAccessURL().getValue());
							} catch (IOException | RepositoryException e) {
								logger.error("Unable to update rdf: " + d.getAccessURL().getValue() + " "
										+ e.getLocalizedMessage());
							}
						}
					}
				}
			if (updatedRDF != 0) {
				logger.info("Updating RDF...");
				logger.info("updatedRDF: " + updatedRDF);
			}
		} catch (DatasetNotFoundException e) {
			return addDataset(node, dataset);
		} catch (SolrServerException | IOException e) {
			logger.error("Unable to update dataset: " + dataset.getId() + " " + e.getLocalizedMessage());
		}
		return updatedRDF;
	}
	
}
