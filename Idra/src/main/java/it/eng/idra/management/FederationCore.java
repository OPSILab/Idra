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

import it.eng.idra.authentication.basic.LoggedUser;
import it.eng.idra.beans.ConfigurationParameter;
import it.eng.idra.beans.DCATThemes;
import it.eng.idra.beans.Log;
import it.eng.idra.beans.User;
import it.eng.idra.beans.exception.DatasetNotFoundException;
import it.eng.idra.beans.exception.InvalidPasswordException;
import it.eng.idra.beans.odms.ODMSAlreadyPresentException;
import it.eng.idra.beans.odms.ODMSManagerException;
import it.eng.idra.beans.odms.ODMSCatalogue;
import it.eng.idra.beans.odms.ODMSCatalogueFederationLevel;
import it.eng.idra.beans.odms.ODMSCatalogueForbiddenException;
import it.eng.idra.beans.odms.ODMSCatalogueMessage;
import it.eng.idra.beans.odms.ODMSCatalogueNotFoundException;
import it.eng.idra.beans.odms.ODMSCatalogueOfflineException;
import it.eng.idra.beans.odms.ODMSCatalogueSSLException;
import it.eng.idra.beans.odms.ODMSCatalogueState;
import it.eng.idra.beans.odms.ODMSCatalogueType;
import it.eng.idra.beans.odms.ODMSSynchLock;
import it.eng.idra.cache.CachePersistenceManager;
import it.eng.idra.cache.MetadataCacheManager;
import it.eng.idra.scheduler.IdraScheduler;
import it.eng.idra.scheduler.exception.SchedulerNotInitialisedException;
import it.eng.idra.search.EuroVocTranslator;
import it.eng.idra.utils.CommonUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import java.util.Random;

import javax.net.ssl.SSLHandshakeException;

import org.apache.solr.client.solrj.SolrServerException;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.quartz.SchedulerException;
import org.apache.logging.log4j.*;

public class FederationCore {

	private FederationCore() {
	}

	private static CachePersistenceManager jpa;
	// private static PersistenceManager manageBeansJpa;

	static Logger logger = LogManager.getLogger(FederationCore.class);

	private static HashMap<String, String> settings = new HashMap<String, String>();
	private static List<DCATThemes> dcatThemes = new ArrayList<DCATThemes>();
	// private static Timer deleteLogsTimer;

	public static void init(boolean loadCacheFromDB, String solrPath) {

		PersistenceManager manageBeansJpa = new PersistenceManager();
		try {
			jpa = new CachePersistenceManager();
			settings = manageBeansJpa.getConfiguration();
			dcatThemes = manageBeansJpa.getDCATThems();

			MetadataCacheManager.init(loadCacheFromDB, solrPath);
			EuroVocTranslator.init();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			manageBeansJpa.jpaClose();
		}
	}

	public static void onFinalize() {

		try {

			MetadataCacheManager.onFinalize();
			PersistenceManager.jpaFinalize();
			DBConnectionManager.closeDbConnection();

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

	public static void setSettings(HashMap<String, String> settingsTmp) throws NumberFormatException, SQLException {
		PersistenceManager manageBeansJpa = new PersistenceManager();
		try {
			List<ConfigurationParameter> configUsed = manageBeansJpa.getConfigurationList();

			for (ConfigurationParameter param : configUsed) {
				param.setParameterValue(settingsTmp.get(param.getParameterName()));
			}

			if (manageBeansJpa.updateConfigurationList(configUsed))
				settings = manageBeansJpa.getConfiguration(); // -> aggiorno le
																// configurazioni
		} finally {
			manageBeansJpa.jpaClose();
		}
	}

	public static List<Log> getLogs(List<String> levelList, ZonedDateTime from, ZonedDateTime to) throws SQLException {

		PersistenceManager manageBeansJpa = new PersistenceManager();
		try {
			return manageBeansJpa.getLogs(levelList, from, to);
		} finally {
			manageBeansJpa.jpaClose();
		}
	}

//	public static boolean validatePassword(String username, String password) throws SQLException {
//
//		PersistenceManager manageBeansJpa = new PersistenceManager();
//		try {
//			User tmp = manageBeansJpa.getUser(username);
//			if (!tmp.getPassword().equals(password)) {
//				return false;
//			}
//
//			return true;
//		} finally {
//			manageBeansJpa.jpaClose();
//		}
//	}

//	public static String login(String username, String password)
//			throws SQLException, NullPointerException, NoSuchAlgorithmException {
//		PersistenceManager manageBeansJpa = new PersistenceManager();
//		try {
//			User tmp = manageBeansJpa.getUser(username);
//			if (!tmp.getPassword().equals(password)) {
//
//				logger.error("Username or password invalid");
//
//				throw new NullPointerException("Username or password invalid!");
//
//			} else {
//				Random random = new SecureRandom();
//				String t = new BigInteger(130, random).toString(32);
//				String token = CommonUtil.encodePassword(tmp.getUsername() + t);
//				LoggedUser u = new LoggedUser(tmp.getUsername(), token);
//				logUser.add(u);
//				logger.info("Login success");
//				return token;
//			}
//		} finally {
//			manageBeansJpa.jpaClose();
//		}
//	}

//	public static boolean updateUserPassword(String username, String newPassword)
//			throws SQLException, NoSuchAlgorithmException, InvalidPasswordException {
//		PersistenceManager manageBeansJpa = new PersistenceManager();
//		try {
//			User u = manageBeansJpa.getUser(username);
//			manageBeansJpa.updateUserPassword(u, newPassword);
//			return validatePassword(u.getUsername(), newPassword);
//		} finally {
//			manageBeansJpa.jpaClose();
//		}
//	}

//	public static void logout(String username) {
//		int remove = -1;
//		for (int i = 0; i < logUser.size(); i++) {
//			if (logUser.get(i).getUsername().equals(username)) {
//				remove = i;
//			}
//		}
//		if (remove >= 0) {
//			logUser.remove(remove);
//		}
//
//		logger.info("Logout success");
//	}

	/*
	 * ODMS MESSAGES DELEGATE METHODS
	 */

	public static HashMap<Integer, Long> getAllODMSMessagesCount() throws ODMSManagerException {
		return ODMSManager.getAllODMSMessagesCount();
	}

	public static List<ODMSCatalogueMessage> getODMSMessages(int nodeID) throws ODMSManagerException {
		return ODMSManager.getODMSMessages(nodeID);
	}

	public static ODMSCatalogueMessage getODMSMessage(int nodeID, int messageID) throws ODMSManagerException {
		return ODMSManager.getODMSMessage(nodeID, messageID);
	}

	public static void deleteAllODMSMessage(int nodeID) throws ODMSManagerException {
		ODMSManager.deleteAllODMSMessage(nodeID);
	}

	public static void deleteODMSMessage(int nodeID, int messageID) throws ODMSManagerException {
		ODMSManager.deleteODMSMessage(nodeID, messageID);
	}

	public static int insertODMSMessage(int nodeID, String message) throws ODMSManagerException {
		return ODMSManager.insertODMSMessage(nodeID, message);
	}

	/**
	 * 
	 * Gets the list of all possible Connector types
	 * 
	 * @return HashMap<ODMSCatalogueType, String>
	 */
	public static HashMap<ODMSCatalogueType, String> getODMSConnectorsList() {
		return ODMSManager.getODMSConnectorsList();
	}

	/**
	 * Gets the List of the federated ODMS Catalogues present in the Federation
	 *
	 * Forwards the request to the underlying JDBC layer
	 *
	 * @param none
	 * @throws ODMSManagerException
	 * @returns ArrayList<ODMSCatalogue> the list of the federated ODMS Catalogues
	 * 
	 */

	public static List<ODMSCatalogue> getODMSCatalogues() {
		return ODMSManager.getODMSCatalogues();
	}

	public static List<ODMSCatalogue> getODMSCatalogues(boolean withImage) throws ODMSManagerException {
		return ODMSManager.getODMSCatalogues(withImage);
	}

	public static List<ODMSCatalogue> getAllInactiveODMSCatalogues(boolean withImage) throws ODMSManagerException {
		return ODMSManager.getAllInactiveODMSCatalogue(withImage);
	}

	/**
	 * Gets a federated ODMS Catalogue present in the Federation
	 *
	 * Forwards the request to the underlying ODMSManager
	 *
	 * @param id
	 *            Id of requested federated node
	 * @throws ODMSCatalogueNotFoundException
	 * @throws ODMSManagerException
	 * @throws SQLException
	 * @returns ODMSCatalogue the resulting federated ODMS Catalogue
	 * 
	 */
	public static ODMSCatalogue getODMSCatalogue(int id) throws ODMSCatalogueNotFoundException {
		return ODMSManager.getODMSCatalogue(id);
	}

	public static ODMSCatalogue getODMSCatalogue(int id, boolean withImage)
			throws ODMSCatalogueNotFoundException, ODMSManagerException {
		return ODMSManager.getODMSCatalogue(id, withImage);
	}

	public static ODMSCatalogue getInactiveODMSCatalogue(int id) throws ODMSCatalogueNotFoundException, ODMSManagerException {
		return ODMSManager.getInactiveODMSCatalogue(id, false);
	}

	/**
	 * Gets the List of federated ODMS Catalogues with a specific federation grade
	 * present in the Federation
	 * 
	 * Forwards the request to the underlying JDBC layer
	 *
	 * @param integrationLevel
	 *            The integration grade
	 * @throws SQLException
	 * @returns ArrayList<ODMSCatalogue> The list of the federated ODMS Catalogues with
	 *          specific federation grade
	 */
	public static List<ODMSCatalogue> getODMSCataloguesbyFederationLevel(ODMSCatalogueFederationLevel level_first,
			ODMSCatalogueFederationLevel level_second) throws SQLException {

		return ODMSManager.getODMSCataloguesbyFederationLevel(level_first, level_second);

	}

	/**
	 * Performs the registration of a new ODMS Catalogue passed by API module
	 *
	 * Depending on Federation Level performs further operations such as
	 * availability and first synchronization
	 *
	 * @param ODMSCatalogue
	 *            The new ODMSCatalogue to add
	 * @throws ODMSManagerException
	 * @throws ODMSAlreadyPresentException
	 * @throws ODMSCatalogueNotFoundException
	 * @throws ODMSCatalogueForbiddenException
	 * @throws ODMSCatalogueSSLException
	 * @throws InvocationTargetException
	 * @throws ODMSCatalogueOfflineException
	 * @throws SchedulerNotInitialisedException 
	 * @throws SQLException 
	 * @returns void
	 * @throws An Exception if the request fails
	 */
	public static void registerODMSCatalogue(final ODMSCatalogue node)
			throws ODMSAlreadyPresentException, ODMSManagerException, ODMSCatalogueNotFoundException,
			ODMSCatalogueForbiddenException, ODMSCatalogueSSLException, InvocationTargetException, ODMSCatalogueOfflineException, SchedulerNotInitialisedException, SQLException {

		/* ************************************************************
		 * The Catalogue is created in the Persistence
		 * the PostCreate is triggered after the line below
		 **************************************************************/
		final int assignedNodeID = ODMSManager.addODMSCatalogue(node);

		/* ************************************************************
		 * If the Catalogue has Federation Level 2 o 3, 
		 * all its datasets are loaded to Persistence and SOLR Cache
		 **************************************************************/
		
		if (node.isCacheable()) {

			node.setId(assignedNodeID);
			node.setSynchLock(ODMSSynchLock.FIRST);
			ODMSManager.updateODMSCatalogue(node, false);

			/* ************************************************************
			 *  1. Gather datasets from the Catalogue, then persist and cache them
			 *  2. Start all the Jobs needed after Catalogue registration
			 *  3. Manage ODMS Statistics and Messages 
			 * ***********************************************************/
			try {
				/* 
				 * 1. Gather datasets from the Catalogue, then persist and cache them
				 */
				MetadataCacheManager.loadCacheFromODMSCatalogue(node);

				node.setSynchLock(ODMSSynchLock.NONE);
				ODMSManager.updateODMSCatalogue(node, false);

				
				/*
				 * 2. Start all the Jobs after Catalogue registration
				 */
				IdraScheduler.getSingletonInstance().startCataloguesSynchJob(node, false);
				// Everything fine reload list of nodes without images
				ODMSManager.updateODMSCatalogueList();

				
				/*
				 * 3. Insert the statistic of the new node
				 */
				StatisticsManager.odmsStatistics(node, node.getDatasetCount(), 0, 0, node.getRdfCount(), 0, 0);
				logger.info("--------- The ODMS Catalogue with host " + node.getHost()
						+ " was successfully registered ----------");
				ODMSManager.insertODMSMessage(node.getId(), "Node successfully registered");

				
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				Throwable target = null;
				if ((target = e.getCause()) != null) {
					if ((target = target.getCause()) != null) {

						Class targetClass = target.getClass();
						if (targetClass.equals(ODMSCatalogueOfflineException.class)) {
							e.printStackTrace();
							logger.error("Problem during registration of Catalogue " + node.getName()
									+ ": Setting state to OFFLINE");
							ODMSManager.insertODMSMessage(node.getId(), "Unreacheble, setting state to OFFLINE");
							node.setNodeState(ODMSCatalogueState.OFFLINE);
							node.setDatasetCount(0);
							ODMSManager.updateODMSCatalogue(node, true);
							node.setSynchLock(ODMSSynchLock.NONE);

							try {
								IdraScheduler.getSingletonInstance().startCataloguesSynchJob(node, false);
							} catch (SchedulerNotInitialisedException e1) {
								e1.printStackTrace();

							}

						} else if (targetClass.equals(ODMSCatalogueNotFoundException.class)) {
							e.printStackTrace();
							logger.error("The node host " + node.getHost() + " was not found");
							ODMSManager.deleteODMSCatalogue(node);
							throw new ODMSCatalogueNotFoundException("The node " + node.getHost() + " host was not found");
						} else if (targetClass.equals(ODMSCatalogueForbiddenException.class)) {
							e.printStackTrace();
							logger.error("The ODMS Catalogue " + node.getHost() + " is forbidden");
							ODMSManager.deleteODMSCatalogue(node);
							throw new ODMSCatalogueForbiddenException("The ODMS Catalogue " + node.getHost() + " is forbidden");
						} else if (targetClass.equals(SSLHandshakeException.class)) {
							e.printStackTrace();
							logger.error("The ODMS Catalogue " + node.getHost()
									+ " requested SSL handshake, import its certificate into java keystore");
							ODMSManager.deleteODMSCatalogue(node);
							throw new ODMSCatalogueSSLException(
									"The ODMS Catalogue " + node.getHost() + " requested SSL handshake and failed");
						} else {
							logger.error("There was an error while registering ODMS Catalogue " + node.getHost()
									+ ": operation deleted " + e.getMessage());
							ODMSManager.deleteODMSCatalogue(node);
							throw e;
						}
					} else {
						logger.error("There was an error while registering ODMS Catalogue " + node.getHost()
								+ ": operation deleted " + e.getMessage());
						ODMSManager.deleteODMSCatalogue(node);
						throw e;
					}
				}

			} catch (SchedulerNotInitialisedException e) {
				
				logger.error("Scheduler not initialised, skipped synchronization thread for " + node.getHost() + ":"
						+ e.getLocalizedMessage());
				throw e;
			} catch (SQLException e) {
				
				logger.error("SqlException while updating node list: " + e.getLocalizedMessage());
				throw e;
			}

		}

	}

	public static void registerInactiveODMSCatalogue(final ODMSCatalogue node) throws Exception {

		try {
			node.setActive(false);
			ODMSManager.addODMSCatalogue(node);
		} catch (ODMSAlreadyPresentException | ODMSCatalogueNotFoundException | ODMSCatalogueOfflineException
				| ODMSCatalogueForbiddenException | ODMSManagerException e) {
			
			//e.printStackTrace(); Exception printed and handled at API level
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
	 * module
	 *
	 * Depending on Federation Level performs further operations such as
	 * availability and first synchronization
	 *
	 * @param ODMSCatalogue
	 *            The ODMSCatalogue to add
	 * @returns void
	 * 
	 * @throws An
	 *             Exception if the request fails
	 */
	public static void unregisterODMSCatalogue(ODMSCatalogue node) throws Exception {

		node.setSynchLock(ODMSSynchLock.PERIODIC);
		ODMSManager.updateODMSCatalogue(node, false);

		// If node has federation level 2 o 3, are deleted all its datasets from
		// Persistence and SOLR Cache
		if (node.isCacheable())
			MetadataCacheManager.deleteAllDatasetsByODMSCatalogue(node);

		ODMSManager.deleteAllODMSMessage(node.getId());

		ODMSManager.deleteODMSCatalogue(node);

		PersistenceManager manageBeansJpa = new PersistenceManager();
		try {

			manageBeansJpa.removeODMSStatistics(node.getId());
			// remove node synch timer from timers list
			// SynchManager.deleteODMSNodeSynchTimer(node.getId());
			IdraScheduler odfScheduler = IdraScheduler.getSingletonInstance();
			if (odfScheduler.isJobRunning(Integer.toString(node.getId()))) {
				odfScheduler.interruptJob(Integer.toString(node.getId()));
			}
			odfScheduler.deleteJob(Integer.toString(node.getId()));

			System.gc();
			logger.info("The ODMS Catalogue with name: " + node.getName() + " and ID: " + node.getId()
					+ " was successfully deleted");

		} finally {

			manageBeansJpa.jpaClose();
		}
	}

	/**
	 * Performs the update operation of a Federated ODMS Catalogue passed by API module
	 *
	 * Depending on previous and current Federation Level performs further
	 * operations such as caching datasets for a node with a new Federation level
	 * equal or greater than 2 and deleting datasets cache of a node with a new
	 * Federation Level equal or smaller than 1
	 *
	 * @param node
	 *            The ODMSCatalogue to update
	 * @throws Exception
	 * 
	 * @returns void
	 * 
	 */
	public static void updateFederatedODMSCatalogue(ODMSCatalogue node, boolean rescheduleJob)
			throws SQLException, NoSuchMethodException, SecurityException, ClassNotFoundException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException,
			SolrServerException, IOException, DatasetNotFoundException, RepositoryException, RDFParseException,
			ODMSCatalogueNotFoundException, ODMSManagerException {

		ODMSCatalogueFederationLevel oldLevel = ODMSManager.getODMSCatalogue(node.getId()).getFederationLevel();
		ODMSCatalogueFederationLevel newLevel = node.getFederationLevel();
		try {
			// If node becomes cacheable
			if ((newLevel.equals(ODMSCatalogueFederationLevel.LEVEL_2) || newLevel.equals(ODMSCatalogueFederationLevel.LEVEL_3))
					&& ((oldLevel.equals(ODMSCatalogueFederationLevel.LEVEL_1)
							|| oldLevel.equals(ODMSCatalogueFederationLevel.LEVEL_0)))) {
				MetadataCacheManager.loadCacheFromODMSCatalogue(node);
				// SynchManager.addODMSNodeSynchTimer(node, false);

				IdraScheduler.getSingletonInstance().startCataloguesSynchJob(node, false);

			}

			if (oldLevel.equals(ODMSCatalogueFederationLevel.LEVEL_2) || oldLevel.equals(ODMSCatalogueFederationLevel.LEVEL_3)) {

				// If node is not cacheable anymore
				if (newLevel.equals(ODMSCatalogueFederationLevel.LEVEL_1)
						|| newLevel.equals(ODMSCatalogueFederationLevel.LEVEL_0)) {
					MetadataCacheManager.deleteAllDatasetsByODMSCatalogue(node);
					// SynchManager.deleteODMSNodeSynchTimer(node.getId());

					IdraScheduler.getSingletonInstance().deleteJob(Integer.toString(node.getId()));
					// this is used to avoid problems on future federation level grown
					node.setDatasetStart(0);
				} else {
					// Rescheduling the job
					if (rescheduleJob)
						IdraScheduler.getSingletonInstance().rescheduleJob(Integer.toString(node.getId()), node);
				}

			}
			
			if(newLevel.equals(ODMSCatalogueFederationLevel.LEVEL_4) && rescheduleJob) {
				MetadataCacheManager.deleteAllDatasetsByODMSCatalogue(node);
				MetadataCacheManager.loadCacheFromODMSCatalogue(node);
			}

		} catch (SchedulerNotInitialisedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ODMSManager.updateODMSCatalogue(node, true);
		ODMSManager.insertODMSMessage(node.getId(), "Node successfully updated");
		logger.info("The ODMS Catalogue with name: " + node.getName() + " and ID: " + node.getId()
				+ " was successfully updated");

		// Everything fine reload list of nodes without images
		try {
			ODMSManager.updateODMSCatalogueList();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Starts a new node synchonization and resets the related timer
	 * 
	 * @param nodeId
	 * @throws ODMSCatalogueNotFoundException
	 * @throws ODMSManagerException
	 * 
	 * @returns void
	 */
	public static void startODMSCatalogueSynch(final int nodeId) throws ODMSCatalogueNotFoundException, ODMSManagerException {

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

	public static void deactivateODMSCatalogue(ODMSCatalogue node, Boolean keepDatasets) throws ODMSCatalogueNotFoundException,
			ODMSManagerException, IOException, SolrServerException, DatasetNotFoundException {
		node.setActive(false);
		ODMSManager.updateODMSCatalogue(node, true);
		// remove node synch timer from timers list
		try {
			IdraScheduler odfScheduler = IdraScheduler.getSingletonInstance();
			if (odfScheduler.isJobRunning(Integer.toString(node.getId()))) {
				logger.info("Interrupting job for catalogue: "+node.getId());
				odfScheduler.interruptJob(Integer.toString(node.getId()));
			}
			logger.info("Deleting job for catalogue: "+node.getId());
			odfScheduler.deleteJob(Integer.toString(node.getId()));
			
		} catch (SchedulerNotInitialisedException | SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (!keepDatasets) {
			node.setSynchLock(ODMSSynchLock.PERIODIC);
			ODMSManager.updateODMSCatalogue(node, false);
			if (node.isCacheable())
				MetadataCacheManager.deleteAllDatasetsByODMSCatalogue(node);

			ODMSManager.deleteAllODMSMessage(node.getId());

			PersistenceManager manageBeansJpa = new PersistenceManager();
			try {
				manageBeansJpa.removeODMSStatistics(node.getId());
			} finally {
				manageBeansJpa.jpaClose();
			}
			node.setDatasetStart(0);
			node.setDatasetCount(0);
			node.setNodeState(ODMSCatalogueState.OFFLINE);
			node.setSynchLock(ODMSSynchLock.NONE);
			ODMSManager.updateODMSCatalogue(node, true);
			System.gc();
		}

		logger.info("The ODMS Catalogue with name: " + node.getName() + " and ID: " + node.getId()
				+ " was successfully deactivated " + ((keepDatasets) ? "keeping datasets" : "deleting datasets"));
	}

	public static void activateODMSCatalogue(ODMSCatalogue node) throws ODMSCatalogueNotFoundException, ODMSManagerException {
		node.setActive(true);
		ODMSManager.updateODMSCatalogue(node, true);

		boolean startNow = false;

		if (node.getDatasetStart() != -1) {
			node.setRegisterDate(ZonedDateTime.now(ZoneOffset.UTC));
			ODMSManager.updateODMSCatalogue(node, true);
			startNow = true;
		}

		if(!node.getFederationLevel().equals(ODMSCatalogueFederationLevel.LEVEL_4)) {
			try {
				IdraScheduler.getSingletonInstance().startCataloguesSynchJob(node, startNow);
			} catch (SchedulerNotInitialisedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			if(startNow)
				try {
					MetadataCacheManager.loadCacheFromODMSCatalogue(node);
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

	}

	public static Integer getODMSCatalogueIDbyName(String nodeName) {
		return ODMSManager.getODMSCatalogueIDbyName(nodeName);
	}

	public static List<Integer> getActiveODMSCataloguesID(List<Integer> ids) {
		logger.info("Original nodeID list: " + ids.toString());
		ids = ids.stream().filter(x -> {
			try {
				return ODMSManager.getODMSCatalogue(x).isActive();
			} catch (ODMSCatalogueNotFoundException e) {
				return false;
			}
		}).collect(Collectors.toList());

		if (ids.isEmpty())
			ids.add(0);
		logger.info("Filtered nodeID list: " + ids.toString());
		return ids;
	}

	public static List<DCATThemes> getDCATThemes() {
		return dcatThemes;
	}

	public static String getDCATThemesFromAbbr(String abbr) {
		return dcatThemes.stream().filter(x -> x.getIdentifier().equalsIgnoreCase(abbr)).findFirst().get().getEn();
	}

	public static String getDCATThemesIdentifier(String val) {
		return dcatThemes.stream().filter(x -> x.getEn().equalsIgnoreCase(val)).findFirst().get().getIdentifier();
	}

	public static boolean isDcatTheme(String value) {
		return dcatThemes.stream()
				.anyMatch(x -> x.getIdentifier().equalsIgnoreCase(value) || x.getEn().equalsIgnoreCase(value));
	}

	// private static void startDeleteLogsDeamon() {
	// deleteLogsTimer = new Timer(true);
	// deleteLogsTimer.scheduleAtFixedRate(new TimerTask() {
	// public void run() {
	// PersistenceManager jpa = new PersistenceManager();
	// try {
	//
	// ZonedDateTime now = ZonedDateTime.now();
	// jpa.deleteLogs(now);
	//
	// } catch (Exception e) {
	//
	// e.printStackTrace();
	// }finally {
	// jpa.jpaClose();
	// }
	// }
	// }, 0 , 604800*1000);
	// }

	// private static void stopDeleteLogsDeamon() {
	// deleteLogsTimer.cancel();
	// }

	
	public static Logger getLogger() {
		return logger;
	}
	
}
