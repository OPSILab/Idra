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

import it.eng.idra.beans.ODFProperty;
import it.eng.idra.beans.dcat.DCATAPFormat;
import it.eng.idra.beans.odms.ODMSAlreadyPresentException;
import it.eng.idra.beans.odms.ODMSManagerException;
import it.eng.idra.beans.odms.ODMSCatalogue;
import it.eng.idra.beans.odms.ODMSCatalogueFederationLevel;
import it.eng.idra.beans.odms.ODMSCatalogueForbiddenException;
import it.eng.idra.beans.odms.ODMSCatalogueMessage;
import it.eng.idra.beans.odms.ODMSCatalogueNotFoundException;
import it.eng.idra.beans.odms.ODMSCatalogueOfflineException;
import it.eng.idra.beans.odms.ODMSCatalogueState;
import it.eng.idra.beans.odms.ODMSCatalogueType;
import it.eng.idra.beans.odms.ODMSSynchLock;
import it.eng.idra.beans.orion.OrionCatalogueConfiguration;
import it.eng.idra.connectors.*;
import it.eng.idra.dcat.dump.DCATAPDeserializer;
import it.eng.idra.dcat.dump.DCATAPITDeserializer;
import it.eng.idra.dcat.dump.DCATAPSerializer;
import it.eng.idra.utils.CommonUtil;
import it.eng.idra.utils.PropertyManager;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.logging.log4j.*;

public class ODMSManager {

	private static Logger logger = LogManager.getLogger(ODMSManager.class);

	private static List<ODMSCatalogue> federatedNodes = new ArrayList<ODMSCatalogue>();
	private static HashMap<ODMSCatalogueType, String> ODMSConnectorsList = new HashMap<ODMSCatalogueType, String>();
	private static boolean getNodesLock = false;
	// private static PersistenceManager jpa;

	private ODMSManager() {
	}

	static {
		try {

			federatedNodes = getODMSCataloguesfromDB(false);

			// federatedNodes.stream().forEach(x -> {
			// x.setImage(null);
			// });
			// federatedNodes.stream().filter(x ->
			// StringUtils.isBlank(x.getLocation())).forEach(x ->
			// x.setLocation(""));
			// federatedNodes.stream().filter(x ->
			// StringUtils.isBlank(x.getLocationDescription()))
			// .forEach(x -> x.setLocationDescription(""));

			logger.info("Federated Nodes: " + federatedNodes.size());
			ODMSConnectorsList.put(ODMSCatalogueType.CKAN, "it.eng.idra.connectors.CKanConnector");
			ODMSConnectorsList.put(ODMSCatalogueType.SOCRATA, "it.eng.idra.connectors.SocrataConnector");
			ODMSConnectorsList.put(ODMSCatalogueType.NATIVE, "it.eng.idra.connectors.OpenDataFederationNativeConnector");
			ODMSConnectorsList.put(ODMSCatalogueType.WEB, "it.eng.idra.connectors.WebConnector");
			ODMSConnectorsList.put(ODMSCatalogueType.DCATDUMP, "it.eng.idra.connectors.DCATDumpConnector");
			ODMSConnectorsList.put(ODMSCatalogueType.DKAN, "it.eng.idra.connectors.DkanConnector");
			ODMSConnectorsList.put(ODMSCatalogueType.ORION, "it.eng.idra.connectors.OrionConnector");
			ODMSConnectorsList.put(ODMSCatalogueType.SPARQL, "it.eng.idra.connectors.SparqlConnector");
			ODMSConnectorsList.put(ODMSCatalogueType.SPOD, "it.eng.idra.connectors.SPODConnector");
			ODMSConnectorsList.put(ODMSCatalogueType.OPENDATASOFT, "it.eng.idra.connectors.OpenDataSoftConnector");
			ODMSConnectorsList.put(ODMSCatalogueType.JUNAR, "it.eng.idra.connectors.JunarConnector");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static List<ODMSCatalogue> getODMSCataloguesfromDB(boolean withImage) throws SQLException {
		PersistenceManager jpa = new PersistenceManager();
		List<ODMSCatalogue> resultNodes = null;
		try {
			resultNodes = jpa.jpaGetODMSCatalogues(withImage);

			return resultNodes;
		} finally {
			jpa.jpaClose();
		}
	}

	private static ODMSCatalogue getODMSCataloguefromDB(int id, boolean withImage) throws SQLException {
		PersistenceManager jpa = new PersistenceManager();
		ODMSCatalogue resultNode = null;
		try {
			resultNode = jpa.jpaGetODMSCatalogue(id, withImage);

			return resultNode;
		} finally {
			jpa.jpaClose();
		}
	}

	/**
	 * Gets the ArrayList of the federated ODMS nodes present in the Federation
	 *
	 * Forwards the request to the underlying JDBC layer
	 *
	 * @param none
	 * @throws ODMSManagerException
	 * @returns the list of the federated ODMS nodes
	 * 
	 */
	public static List<ODMSCatalogue> getODMSCatalogues() {

		while (getNodesLock)
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}

		return federatedNodes;

	}

	public static Integer getODMSCatalogueIDbyName(String nodeName) {
		try {
			return federatedNodes.stream().filter(x -> x.getName().equals(nodeName)).findFirst().get().getId();
		} catch (Exception e) {
			return federatedNodes.stream().map(x -> x.getId()).collect(Collectors.toList()).stream()
					.max(Integer::compare).get() + 1;
		}
	}

	public static void updateODMSCatalogueList() throws SQLException {
		federatedNodes = getODMSCataloguesfromDB(false);
	}

	public static List<ODMSCatalogue> getODMSCatalogues(boolean withImage) throws ODMSManagerException {

		while (getNodesLock)
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}

		if (!withImage)
			return federatedNodes;
		else {
			try {
				return getODMSCataloguesfromDB(withImage);

			} catch (SQLException e) {

				throw new ODMSManagerException("There was an error while retrieving ODMS nodes");

			}

		}

	}

	public static ArrayList<Integer> getODMSCataloguesID() {
		ArrayList<Integer> nodesIDList = new ArrayList<Integer>();

		for (ODMSCatalogue node : federatedNodes) {
			nodesIDList.add(node.getId());
		}

		return nodesIDList;
	}

	/**
	 * Gets a federated ODMS node present in the Federation
	 *
	 * Forwards the request to the underlying JDBC layer
	 *
	 * @param id
	 *            Id of requested federated node
	 * @throws ODMSCatalogueNotFoundException
	 * @throws ODMSManagerException
	 * @throws SQLException
	 * @returns the resulting federated ODMS node
	 * 
	 */

	public static ODMSCatalogue getODMSCatalogue(int id) throws ODMSCatalogueNotFoundException {

		try {
			return federatedNodes.get(federatedNodes.indexOf((new ODMSCatalogue(id))));
		} catch (IndexOutOfBoundsException | NullPointerException e) {
			throw new ODMSCatalogueNotFoundException("The ODMS node does not exist in the federation!");
		}

	}

	public static ODMSCatalogue getODMSCatalogue(int id, boolean withImage)
			throws ODMSCatalogueNotFoundException, ODMSManagerException {

		if (!withImage) {
			try {
				return federatedNodes.get(federatedNodes.indexOf((new ODMSCatalogue(id))));
			} catch (IndexOutOfBoundsException | NullPointerException e) {
				throw new ODMSCatalogueNotFoundException("The ODMS node does not exist in the federation!");
			}
		} else {
			try {
				return getODMSCataloguefromDB(id, withImage);
			} catch (SQLException e) {
				throw new ODMSManagerException("There was an error while retrieving ODMS nodes");
			}
		}
	}

	/**
	 * Gets the specific connector instance of a federated ODMS node present in the
	 * Federation by using Java Reflection
	 * 
	 *
	 * @param data.id
	 *            Id of requested federated node
	 * @throws ODMSManagerException
	 * 
	 * @returns the connector instance of federated ODMS node
	 * 
	 */
	public static IODMSConnector getODMSCatalogueConnector(ODMSCatalogue node) throws ODMSManagerException {

		// The connector class is loaded, based on ODMS node type, using Java
		// reflection
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Class<?> cls;
		try {
			cls = loader.loadClass(ODMSConnectorsList.get(node.getNodeType()));

			return (IODMSConnector) cls.getDeclaredConstructor(ODMSCatalogue.class).newInstance(node);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ODMSManagerException("There was an error while retrieving ODMS Connector: " + e.getMessage());
		}

	}

	/**
	 * Gets the List of federated ODMS nodes with a specific federation grade
	 * present in the Federation
	 * 
	 * Forwards the request to the underlying JDBC layer
	 *
	 * @param integrationGrade
	 *            The integration grade
	 * @throws SQLException
	 * @returns the list of the federated ODMS nodes
	 */
	public static List<ODMSCatalogue> getODMSCataloguesbyFederationLevel(ODMSCatalogueFederationLevel integrationLevel_first,
			ODMSCatalogueFederationLevel integrationLevel_second) {

		return federatedNodes.stream().filter(node -> node.getFederationLevel().equals(integrationLevel_first)
				|| node.getFederationLevel().equals(integrationLevel_second)).collect(Collectors.toList());

	}

	/**
	 * Gets the List of federated ODMS nodes with a minimum federation level of 1
	 * present in the Federation
	 * 
	 * Forwards the request to the underlying JDBC layer
	 *
	 * @param none
	 * @throws SQLException
	 * @returns the list of the federated ODMS nodes with minimum Level 1
	 */
	public static List<ODMSCatalogue> getODMSCataloguesbyFederationLevelOne() {

		return federatedNodes.stream()
				.filter(node -> !node.getFederationLevel().equals(ODMSCatalogueFederationLevel.LEVEL_0))
				.collect(Collectors.toList());

	}

	/**
	 * Adds a federated ODMS node to the Federation
	 *
	 * Sends a request to CKAN node to retrieve the datasets count
	 * 
	 * Updates the dataset count of the node Forwards the request to the underlying
	 * JDBC layer
	 *
	 * @param node
	 *            The ODMSCatalogue object to add
	 * 
	 * @throws ODMSAlreadyPresentException
	 * @throws ODMSCatalogueNotFoundException
	 * @throws ODMSManagerException
	 * @returns Node itself with assigned Dataset Count and id
	 * @throws An
	 *             Exception if the request fails
	 */
	public static int addODMSCatalogue(ODMSCatalogue node) throws ODMSAlreadyPresentException, ODMSCatalogueNotFoundException,
			ODMSCatalogueOfflineException, ODMSCatalogueForbiddenException, ODMSManagerException {

		// Sets the lock true, in order to avoid node retrieval until the new
		// node is created
		getNodesLock = true;

		int assignedNodeID;
		int datasetsCount = 0;

		if (!federatedNodes.contains(node)) {
			PersistenceManager jpa = new PersistenceManager();

			try {
				if (node.isActive()) {
					node.setNodeState(ODMSCatalogueState.ONLINE);

					if (node.isCacheable()) {
						node.setSynchLock(ODMSSynchLock.FIRST);
					} else {
						node.setSynchLock(ODMSSynchLock.NONE);
					}

					// Socrata does not support initial datasets count, it is
					// provided during
					// the first synchronization.
					if (!node.getNodeType().equals(ODMSCatalogueType.SOCRATA)
							&& !node.getNodeType().equals(ODMSCatalogueType.DKAN))
						datasetsCount = getODMSCatalogueConnector(node).countDatasets();

					if (!(datasetsCount > 0))
						node.setNodeState(ODMSCatalogueState.OFFLINE);

					node.setDatasetCount(datasetsCount);

					assignedNodeID = jpa.jpaInsertODMSCatalogue(node);
					node.setId(assignedNodeID);

					getNodesLock = false;
					federatedNodes.add(node);

					return assignedNodeID;
				} else {

					node.setNodeState(ODMSCatalogueState.OFFLINE);
					node.setSynchLock(ODMSSynchLock.NONE);
					node.setDatasetCount(0);

					assignedNodeID = jpa.jpaInsertODMSCatalogue(node);
					boolean updateNode=false;
					if (node.getNodeType().equals(ODMSCatalogueType.DCATDUMP)) {
						if (StringUtils.isNotBlank(node.getDumpString())) {
							Model m = null;
							switch (node.getDCATProfile()) {

							case DCATAP_IT:
								m = new DCATAPITDeserializer().dumpToModel(node.getDumpString(), node);
								break;
							default:
								// If no profile was provided, instantiate a base DCATAP Deserializer
								m = new DCATAPDeserializer().dumpToModel(node.getDumpString(), node);
								break;

							}

							String odmsDumpFilePath = PropertyManager.getProperty(ODFProperty.ODMS_DUMP_FILE_PATH);
							try {
								DCATAPSerializer.writeModelToFile(m, DCATAPFormat.RDFXML, odmsDumpFilePath,
										"dumpFileString_" + assignedNodeID);
								node.setDumpFilePath(odmsDumpFilePath + "dumpFileString_" + assignedNodeID);
							} catch (IOException e) {
								e.printStackTrace();
							}

						}
						updateNode=true;
					}else if(node.getNodeType().equals(ODMSCatalogueType.ORION)) {
						
						String orionDumpFilePath=PropertyManager.getProperty(ODFProperty.ORION_FILE_DUMP_PATH);
						try {
							OrionCatalogueConfiguration orionConfig = (OrionCatalogueConfiguration) node.getAdditionalConfig();
							CommonUtil.storeFile(orionDumpFilePath,"orionDump_"+assignedNodeID,orionConfig.getOrionDatasetDumpString());
							orionConfig.setOrionDatasetFilePath(orionDumpFilePath+"orionDump_"+assignedNodeID);
							node.setAdditionalConfig(orionConfig);
							updateNode=true;
						}catch(IOException e) {
							e.printStackTrace();
						}
					}else if(node.getNodeType().equals(ODMSCatalogueType.SPARQL)) {
						
						String dumpFilePath=PropertyManager.getProperty(ODFProperty.ORION_FILE_DUMP_PATH);
						try {
							OrionCatalogueConfiguration orionConfig = (OrionCatalogueConfiguration) node.getAdditionalConfig();
							CommonUtil.storeFile(dumpFilePath,"sparqlDump_"+assignedNodeID,orionConfig.getOrionDatasetDumpString());
							orionConfig.setOrionDatasetFilePath(dumpFilePath+"sparqlDump_"+assignedNodeID);
							node.setAdditionalConfig(orionConfig);
							updateNode=true;
						}catch(IOException e) {
							e.printStackTrace();
						}
					}
					if(updateNode)
						updateInactiveODMSCatalogue(node);
					getNodesLock = false;
					federatedNodes.add(node);

					return assignedNodeID;
				}
			} catch (ODMSCatalogueNotFoundException | ODMSCatalogueOfflineException | ODMSCatalogueForbiddenException e) {
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
				throw new ODMSManagerException("There was an error while adding the ODMS Node: " + e.getMessage());
			} finally {
				getNodesLock = false;
				jpa.jpaClose();
			}

		} else {
			getNodesLock = false;
			throw new ODMSAlreadyPresentException("ODMS Node is already present");
		}

	}

	public static ODMSCatalogue getInactiveODMSCatalogue(int id, boolean withImage) throws ODMSManagerException {
		PersistenceManager jpa = new PersistenceManager();
		try {

			ODMSCatalogue resultNode = null;
			resultNode = jpa.jpaGetInactiveODMSCatalogue(id, withImage);
			return resultNode;

		} finally {
			jpa.jpaClose();
		}

	}

	public static List<ODMSCatalogue> getAllInactiveODMSCatalogue(boolean withImage) throws ODMSManagerException {
		PersistenceManager jpa = new PersistenceManager();
		try {

			List<ODMSCatalogue> resultNode = null;
			resultNode = jpa.jpaGetAllInactiveODMSCatalogue(withImage);
			return resultNode;

		} finally {
			jpa.jpaClose();
		}

	}

	public static int addInactiveODMSCatalogue(ODMSCatalogue node) throws ODMSManagerException {

		PersistenceManager jpa = new PersistenceManager();

		try {

			node.setNodeState(ODMSCatalogueState.OFFLINE);
			node.setSynchLock(ODMSSynchLock.NONE);
			node.setDatasetCount(0);
			int assignedNodeID = jpa.jpaInsertODMSCatalogue(node);
			return assignedNodeID;

		} catch (Exception e) {
			e.printStackTrace();
			throw new ODMSManagerException("There was an error while adding the ODMS Node: " + e.getMessage());
		} finally {
			jpa.jpaClose();
		}

	}

	/**
	 * Removes a federated ODMS node present in the Federation
	 *
	 * Forwards the request to the underlying JDBC layer
	 *
	 * @param node
	 *            The ODMSCatalogue object to remove
	 * @throws ODMSManagerException
	 * @throws ODMSCatalogueNotFoundException
	 * @throws SQLException
	 * @returns void
	 * 
	 */
	public static void deleteODMSCatalogue(ODMSCatalogue node) throws ODMSManagerException, ODMSCatalogueNotFoundException {

		PersistenceManager jpa = new PersistenceManager();
		try {

			jpa.jpaDeleteODMSCatalogue(node.getId());
			federatedNodes.remove(federatedNodes.indexOf(node));

		} catch (IndexOutOfBoundsException e) {
			throw new ODMSCatalogueNotFoundException("ODMSCatalogue is not present");
		} catch (Exception e) {
			throw new ODMSManagerException("There was an error while deleting the ODMS Node: " + e.getMessage());
		} finally {
			jpa.jpaClose();
		}

	}

	/**
	 * Edits a federated ODMS node present in the Federation
	 *
	 * Forwards the request to the underlying JDBC layer for all specified values to
	 * change
	 *
	 * @param node
	 *            ODMSCatalogue to change
	 * @param persist
	 *            Flag to propagate or not the changed node to
	 * @throws SQLException
	 * @throws ODMSCatalogueNotFoundException
	 * @throws ODMSManagerException
	 * @returns void
	 * @throws An
	 *             Exception if the request fails
	 */
	public static void updateODMSCatalogue(ODMSCatalogue node, boolean persist)
			throws ODMSCatalogueNotFoundException, ODMSManagerException {

		if (federatedNodes.remove(node)) {
			if (persist) {
				PersistenceManager jpa = new PersistenceManager();
				try {
					jpa.jpaUpdateODMSCatalogue(node);
				} catch (Exception e) {
					throw new ODMSManagerException(
							"There was an error while updating the ODMS Node: " + e.getMessage());
				} finally {
					jpa.jpaClose();
				}
			}
			federatedNodes.add(node);

		} else
			throw new ODMSCatalogueNotFoundException("The ODMS node does not exist!");

	}

	public static void updateInactiveODMSCatalogue(ODMSCatalogue node) throws ODMSCatalogueNotFoundException, ODMSManagerException {

		PersistenceManager jpa = new PersistenceManager();
		try {
			jpa.jpaUpdateODMSCatalogue(node);
		} catch (Exception e) {
			throw new ODMSManagerException("There was an error while updating the ODMS Node: " + e.getMessage());
		} finally {
			jpa.jpaClose();
		}
	}

	// public static void deleteInactiveODMSNode(ODMSCatalogue node)
	// throws ODMSManagerException {
	//
	// if(!federatedNodes.contains(node)) {
	//
	// PersistenceManager jpa = new PersistenceManager();
	// try {
	// jpa.jpaDeleteODMSNode(node.getId());
	// } catch (Exception e) {
	// throw new ODMSManagerException(
	// "There was an error while updating the ODMS Node: " + e.getMessage());
	// } finally {
	// jpa.jpaClose();
	// }
	// }else {
	// new ODMSManagerException(
	// "Trying to delete a federated node ");
	// }
	// }

	public static void addFederatedODMSCatalogueToList(ODMSCatalogue node) throws ODMSAlreadyPresentException {
		if (!federatedNodes.contains(node)) {
			federatedNodes.add(node);
		} else {
			throw new ODMSAlreadyPresentException("ODMS Node is already present");
		}
	}

	public static void removeFederatedODMSCatalogueFromList(ODMSCatalogue node) throws ODMSCatalogueNotFoundException {
		if (federatedNodes.contains(node)) {
			federatedNodes.remove(node);
		} else {
			throw new ODMSCatalogueNotFoundException("ODMS Node not found");
		}
	}

	/**
	 * Checks if a node is online by requesting its datasets count
	 *
	 * Forwards the request to the appropriate ODMS Connector
	 *
	 * @param node
	 *            the ODMSCatalogue to be verified
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ODMSCatalogueNotFoundException
	 * @throws ODMSManagerException
	 * @returns {@link ODMSCatalogueState}
	 * @throws An
	 *             Exception if the request fails
	 */
	public static ODMSCatalogueState checkODMSCatalogue(ODMSCatalogue node) throws ODMSCatalogueNotFoundException, ODMSManagerException {

		try {
			/*
			 * Check availability through dataset count of the node and then related node
			 * State TODO Provide a specific method "getNodeState" in ODMSNodeConnector
			 * interface?
			 */

			return getODMSCatalogueConnector(node).countDatasets() != 0 ? ODMSCatalogueState.ONLINE : ODMSCatalogueState.OFFLINE;

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getClass().equals(ODMSCatalogueOfflineException.class) || e.getClass().equals(ODMSCatalogueNotFoundException.class)) {

				logger.info("Check node: Setting node state to OFFLINE");
				node.setNodeState(ODMSCatalogueState.OFFLINE);
				ODMSManager.updateODMSCatalogue(node, true);
				return ODMSCatalogueState.OFFLINE;

			} 
			//Since the this method is used during the synchronization, the node is already federated
			//and it must not be removed
			/*else if (e.getClass().equals(ODMSCatalogueNotFoundException.class)) {

				logger.info("Check Node: The node host was not found");
				federatedNodes.remove(federatedNodes.indexOf(node));
				throw new ODMSCatalogueNotFoundException("The node host was not found");

			}*/ 
			else
				throw new ODMSManagerException("There was an error while checking the ODMS Node: " + e.getMessage());
		}
	}

	public static int countODMSCatalogueDatasets(ODMSCatalogue node) throws ODMSCatalogueNotFoundException, ODMSManagerException {

		try {
			/*
			 * Check availability through dataset count of the node and then related node
			 * State TODO Provide a specific method "getNodeState" in ODMSNodeConnector
			 * interface?
			 */

			return getODMSCatalogueConnector(node).countDatasets();

		} catch (Exception e) {

			if (e.getClass().equals(ODMSCatalogueOfflineException.class) || e.getClass().equals(ODMSCatalogueNotFoundException.class)) {

				logger.info("Check node: Setting node state to OFFLINE");
				node.setNodeState(ODMSCatalogueState.OFFLINE);
				ODMSManager.updateODMSCatalogue(node, true);
				return 0;

			} 
			//Since the this method is used during the synchronization, the node is already federated
			//and it must not be removed
			/*else if (e.getClass().equals(ODMSCatalogueNotFoundException.class)) {

				logger.info("Check Node: The node host was not found");
				federatedNodes.remove(federatedNodes.indexOf(node));
				throw new ODMSCatalogueNotFoundException("The node host was not found");

			}*/ 
			else
				throw new ODMSManagerException("There was an error while checking the ODMS Node: " + e.getMessage());
		}
	}

	/**
	 * 
	 * Gets the list of all possible Connector types
	 * 
	 * @return HashMap<ODMSCatalogueType, String>
	 */
	protected static HashMap<ODMSCatalogueType, String> getODMSConnectorsList() {
		return ODMSConnectorsList;
	}

	public static HashMap<Integer, Long> getAllODMSMessagesCount() throws ODMSManagerException {
		PersistenceManager manageBeansJpa = new PersistenceManager();
		try {

			return manageBeansJpa.jpaGetMessagesCount(
					getODMSCatalogues().stream().map(node -> node.getId()).collect(Collectors.toList()));

		} catch (Exception e) {
			throw new ODMSManagerException("There was an error while getting ODMS Nodes messages: " + e.getMessage());
		} finally {
			manageBeansJpa.jpaClose();
		}
	}

	public static List<ODMSCatalogueMessage> getODMSMessages(int nodeID) throws ODMSManagerException {
		PersistenceManager manageBeansJpa = new PersistenceManager();
		try {

			return manageBeansJpa.jpaGetODMSMessagesByNode(nodeID);

		} catch (Exception e) {
			throw new ODMSManagerException("There was an error while getting ODMS Node messages: " + e.getMessage());
		} finally {
			manageBeansJpa.jpaClose();
		}
	}

	public static ODMSCatalogueMessage getODMSMessage(int nodeID, int messageID) throws ODMSManagerException {
		PersistenceManager manageBeansJpa = new PersistenceManager();
		try {
			return manageBeansJpa.jpaGetODMSMessage(messageID, nodeID);
		} catch (Exception e) {
			throw new ODMSManagerException("There was an error while getting ODMS Node message: " + e.getMessage());
		} finally {
			manageBeansJpa.jpaClose();
		}
	}

	public static void deleteAllODMSMessage(int nodeID) throws ODMSManagerException {
		PersistenceManager manageBeansJpa = new PersistenceManager();
		try {
			manageBeansJpa.jpaDeleteAllODMSMessage(nodeID);
		} catch (Exception e) {
			throw new ODMSManagerException("There was an error while deleting ODMS Nodes messages: " + e.getMessage());
		} finally {
			manageBeansJpa.jpaClose();
		}
	}

	public static void deleteODMSMessage(int nodeID, int messageID) throws ODMSManagerException {
		PersistenceManager manageBeansJpa = new PersistenceManager();
		try {
			manageBeansJpa.jpaDeleteODMSMessage(messageID, nodeID);
		} catch (Exception e) {
			throw new ODMSManagerException("There was an error while deleting ODMS Node message: " + e.getMessage());
		} finally {
			manageBeansJpa.jpaClose();
		}
	}

	public static int insertODMSMessage(int nodeID, String message) throws ODMSManagerException {
		PersistenceManager manageBeansJpa = new PersistenceManager();
		try {
			return manageBeansJpa.jpaInsertODMSMessage(message, nodeID);
		} catch (Exception e) {
			throw new ODMSManagerException("There was an error while inserting ODMS Node message: " + e.getMessage());
		} finally {
			manageBeansJpa.jpaClose();
		}
	}

	class getNodesMonitor extends Thread {
		int total;

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
