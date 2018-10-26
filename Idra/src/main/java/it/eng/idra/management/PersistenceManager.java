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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;

import org.apache.logging.log4j.*;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.joda.time.DateTime;

import it.eng.idra.beans.ConfigurationParameter;
import it.eng.idra.beans.DCATThemes;
import it.eng.idra.beans.Log;
import it.eng.idra.beans.RdfPrefix;
import it.eng.idra.beans.User;
import it.eng.idra.beans.exception.InvalidPasswordException;
import it.eng.idra.beans.odms.ODMSCatalogue;
import it.eng.idra.beans.odms.ODMSCatalogueMessage;
import it.eng.idra.beans.statistics.KeywordStatistics;
import it.eng.idra.beans.statistics.KeywordStatisticsResult;
import it.eng.idra.beans.statistics.ODMSStatistics;
import it.eng.idra.beans.statistics.ODMSStatisticsResult;
import it.eng.idra.beans.statistics.SearchStatistics;
import it.eng.idra.beans.statistics.SearchStatisticsResult;

import javax.persistence.EntityExistsException;

public class PersistenceManager {

	private static EntityManagerFactory emf;

	private EntityManager em;
	private static Logger logger = LogManager.getLogger(PersistenceManager.class);

	static {
		// logger.info("Hibernate EntityManagerFactory init");
		
		try {
			emf = Persistence.createEntityManagerFactory("org.hibernate.jpa.beans");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public PersistenceManager() {
		// logger.info("Hibernate init");
		try {
			em = emf.createEntityManager();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// logger.info("Hibernate end");
	}

	public void flush() {
		em.flush();
	}

	public void jpaBeginTransaction() {
		if (!em.getTransaction().isActive())
			em.getTransaction().begin();
	}

	public EntityTransaction jpaGetTransaction() {
		return em.getTransaction();
	}

	public void jpaRollbackTransaction() {
		if (em.getTransaction().getRollbackOnly())
			em.getTransaction().rollback();
	}

	/* Rob_mod 16/10/2016 */
	public void jpaPersistOrMergeAndCommitObject(Object obj) {

		if (!em.getTransaction().isActive())
			em.getTransaction().begin();

		try {
			em.persist(obj);
		} catch (EntityExistsException e) {
			em.merge(obj);
		}
		em.getTransaction().commit();
	}

	public void jpaCommitTransanction() {
		em.getTransaction().commit();
		em.clear();
	}

	public Query jpaQuery(String query) {
		Query q = em.createQuery(query);
		return q;
	}

	/* NODES */

	public List<ODMSCatalogue> jpaGetODMSCatalogues(boolean withImage) {

		// TypedQuery<ODMSCatalogue> q = em.createQuery("SELECT d FROM ODMSCatalogue d "
		// + (withImage ? "JOIN FETCH d.image i" : "")+" where d.isActive=true or
		// d.isActive is null",
		TypedQuery<ODMSCatalogue> q = em.createQuery(
				"SELECT d FROM ODMSCatalogue d " + (withImage ? "JOIN FETCH d.image i" : "")+" where d.id not in (74,90,92,93,95,97,100,101,102,106,107,108,109,114,115,116,117,118,119,120,121,122,123,124,125,126,127,129,130,131,132,133,136,208,210,211,214,215)", ODMSCatalogue.class);
		return q.getResultList();
	}

	public int jpaInsertODMSCatalogue(ODMSCatalogue node) {

		if (!em.getTransaction().isActive())
			em.getTransaction().begin();
		// em.getTransaction().begin();
		em.persist(node);
		em.getTransaction().commit();
		// em.flush();

		return node.getId();

	}

	public ODMSCatalogue jpaGetODMSCatalogue(int id) {
		TypedQuery<ODMSCatalogue> q = em.createQuery("SELECT d FROM ODMSCatalogue d where id=" + id,
				ODMSCatalogue.class);
		return q.getResultList().get(0);
	}

	public ODMSCatalogue jpaGetODMSCatalogue(int id, boolean withImage) {
		// TypedQuery<ODMSCatalogue> q = em.createQuery("SELECT d FROM ODMSCatalogue d"
		// + (withImage ? " JOIN FETCH d.image i" : "") + " where id=" + id +" and
		// (d.isActive=true or d.isActive is null)",
		TypedQuery<ODMSCatalogue> q = em.createQuery(
				"SELECT d FROM ODMSCatalogue d" + (withImage ? " JOIN FETCH d.image i" : "") + " where id=" + id,
				ODMSCatalogue.class);
		return q.getResultList().get(0);
	}

	public ODMSCatalogue jpaGetInactiveODMSCatalogue(int id, boolean withImage) {
		TypedQuery<ODMSCatalogue> q = em.createQuery("SELECT d FROM ODMSCatalogue d"
				+ (withImage ? " JOIN FETCH d.image i" : "") + " where id=" + id + " and d.isActive=false",
				ODMSCatalogue.class);
		return q.getResultList().get(0);
	}

	public List<ODMSCatalogue> jpaGetAllInactiveODMSCatalogue(boolean withImage) {
		TypedQuery<ODMSCatalogue> q = em.createQuery("SELECT d FROM ODMSCatalogue d"
				+ (withImage ? " JOIN FETCH d.image i" : "") + " where d.isActive=false", ODMSCatalogue.class);
		return q.getResultList();
	}

	public void jpaUpdateODMSCatalogue(ODMSCatalogue node) {
		if (!em.getTransaction().isActive())
			em.getTransaction().begin();
		em.merge(node);
		em.getTransaction().commit();
	}

	public void jpaDeleteODMSCatalogue(int id) {
		// Query q;
		if (!em.getTransaction().isActive())
			em.getTransaction().begin();
		// q = em.createQuery("DELETE FROM ODMSCatalogue where id = " + id);
		// q.executeUpdate();

		em.remove(em.find(ODMSCatalogue.class, id));
		em.flush();
		em.getTransaction().commit();
	}
	/* END NODES */

	/* MESSAGE */

	public List<ODMSCatalogueMessage> jpaGetODMSMessagesByNode(int nodeID) {
		TypedQuery<ODMSCatalogueMessage> q = em.createQuery(
				"SELECT d FROM ODMSCatalogueMessage d where d.nodeID=" + nodeID + " order by d.date desc",
				ODMSCatalogueMessage.class);
		return q.getResultList();
	}

	public ODMSCatalogueMessage jpaGetODMSMessage(int id, int nodeID) {
		TypedQuery<ODMSCatalogueMessage> q = em.createQuery(
				"SELECT d FROM ODMSCatalogueMessage d where d.nodeID=" + nodeID + "and d.id=" + id,
				ODMSCatalogueMessage.class);
		return q.getSingleResult();
	}

	public void jpaDeleteODMSMessage(int id, int nodeID) {
		Query q;
		if (!em.getTransaction().isActive())
			em.getTransaction().begin();
		q = em.createQuery("DELETE FROM ODMSCatalogueMessage where nodeID = " + nodeID + " and id=" + id);
		q.executeUpdate();
		em.getTransaction().commit();
	}

	public void jpaDeleteAllODMSMessage(int nodeID) {
		Query q;
		if (!em.getTransaction().isActive())
			em.getTransaction().begin();
		q = em.createQuery("DELETE FROM ODMSCatalogueMessage where nodeID = " + nodeID);
		q.executeUpdate();
		em.getTransaction().commit();
	}

	public HashMap<Integer, Long> jpaGetMessagesCount(List<Integer> nodeIDs) {
		HashMap<Integer, Long> res = new HashMap<Integer, Long>();
		for (Integer nodeID : nodeIDs) {
			Query q = em.createQuery("SELECT count(*) FROM ODMSCatalogueMessage d Where d.nodeID=" + nodeID);
			long var = (long) q.getSingleResult();
			res.put(nodeID, var);
		}

		return res;
	}

	public int jpaInsertODMSMessage(String message, int nodeID) {
		try {

			ODMSCatalogueMessage mex = new ODMSCatalogueMessage(nodeID, message, ZonedDateTime.now(ZoneOffset.UTC));

			if (!em.getTransaction().isActive())
				em.getTransaction().begin();

			em.persist(mex);
			em.getTransaction().commit();
			// em.flush();
			return mex.getId();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}

		return 0;
	}

	/* END MESSAGE */

	/* RDF PREFIX */

	public List<RdfPrefix> getAllPrefixes() {
		TypedQuery<RdfPrefix> q = em.createQuery("SELECT d FROM RdfPrefix d", RdfPrefix.class);
		return q.getResultList();
	}

	public RdfPrefix getPrefix(int id) {
		TypedQuery<RdfPrefix> q = em.createQuery("SELECT d FROM RdfPrefix d where d.id=" + id, RdfPrefix.class);
		return q.getResultList().get(0);
	}

	public boolean checkPrefixExists(RdfPrefix prefix) {
		TypedQuery<RdfPrefix> q = em
				.createQuery("SELECT d FROM RdfPrefix d where d.prefix='" + prefix.getPrefix() + "'", RdfPrefix.class);
		return q.getResultList().size() > 0;
	}

	public boolean deletePrefix(int id) {

		try {
			Query q;
			if (!em.getTransaction().isActive())
				em.getTransaction().begin();
			q = em.createQuery("DELETE FROM RdfPrefix where id=" + id);
			q.executeUpdate();
			em.getTransaction().commit();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean updatePrefix(RdfPrefix p) {

		try {
			if (!em.getTransaction().isActive())
				em.getTransaction().begin();
			em.merge(p);
			em.getTransaction().commit();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean addPrefix(RdfPrefix p) {

		try {
			if (!em.getTransaction().isActive())
				em.getTransaction().begin();
			em.persist(p);
			em.getTransaction().commit();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/* END RDF PREFIX */

	/* Configuration */

	public HashMap<String, String> getConfiguration() {
		HashMap<String, String> config = new HashMap<>();
		TypedQuery<ConfigurationParameter> q = em.createQuery("SELECT d FROM ConfigurationParameter d ",
				ConfigurationParameter.class);
		config = (HashMap<String, String>) q.getResultList().stream().collect(
				Collectors.toMap(ConfigurationParameter::getParameterName, ConfigurationParameter::getParameterValue));
		return config;
	}

	public List<DCATThemes> getDCATThems() {
		String query = "FROM DCATThemes";
		TypedQuery<DCATThemes> q = em.createQuery(query, DCATThemes.class);
		return q.getResultList();
	}

	public List<ConfigurationParameter> getConfigurationList() {
		TypedQuery<ConfigurationParameter> q = em.createQuery("SELECT d FROM ConfigurationParameter d ",
				ConfigurationParameter.class);
		return q.getResultList();
	}

	public boolean updateConfigurationList(List<ConfigurationParameter> parameter) {
		try {
			if (!em.getTransaction().isActive())
				em.getTransaction().begin();
			for (ConfigurationParameter tmp : parameter) {
				em.merge(tmp);
			}
			em.getTransaction().commit();
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/* End configuration */

	/* LOGS */

	private String buildLogsQuery(List<String> levelList, ZonedDateTime from, ZonedDateTime to) {

		String query = "SELECT d FROM Log d WHERE ( ";

		if (levelList.size() == 1) {
			query += " d.level = '" + levelList.get(0) + "' ";
		} else {
			for (int i = 0; i < levelList.size(); i++) {
				if (i == 0 && i != (levelList.size() - 1))
					query += " d.level = '" + levelList.get(i) + "' ";
				else
					query += " OR d.level = '" + levelList.get(i) + "' ";
			}
			// query += " d.level = '" + levelList.get(0) + "' " + " OR
			// d.level='" + levelList.get(1) + "' " + " OR d.level='" +
			// levelList.get(2) + "' ";
		}

		query += " ) AND ( d.dated  BETWEEN '" + from.getYear() + "-" + from.getMonthValue() + "-"
				+ from.getDayOfMonth() + " 00:00:00" + "' " + " AND '" + to.getYear() + "-" + to.getMonthValue() + "-"
				+ to.getDayOfMonth() + " 23:59:59" + "' ) order by d.dated asc";

		return query;
	}

	public List<Log> getLogs(List<String> level, ZonedDateTime from, ZonedDateTime to) {
		String query = buildLogsQuery(level, from, to);
		TypedQuery<Log> q = em.createQuery(query, Log.class);
		List<Log> list = q.getResultList();
		return list;
		// JSONObject arr = new JSONObject();
		// String tmp = "";
		//
		// for (Logs l : list) {
		// tmp += "[ " + l.getLevel() + " ] " + l.getDated().toString() + " " +
		// l.getLogger() + " " + l.getMessage()
		// + "\n";
		// }
		// arr.put("logs", tmp);
		// return arr;

	}

	public void deleteLogs(ZonedDateTime now) {
		// System.out.println(now.toEpochSecond());
		em.getTransaction().begin();
		em.createNativeQuery("delete from logs where UNIX_TIMESTAMP(dated) < " + now.toEpochSecond() + "")
				.executeUpdate();
		em.getTransaction().commit();
	}
	/* END Logs */

	/* Statistics */

	public String getCountryFromIp(String ip) {

		String query = "SELECT c.country  FROM ip2nationCountries c,  ip2nation i  WHERE" + " i.ip < INET_ATON('" + ip
				+ "') AND  c.code = i.country ORDER BY  i.ip DESC";

		Query q = em.createNativeQuery(query);
		String res = "";
		if (q.getResultList().size() > 0)
			res = q.getResultList().get(0).toString();

		return res;
	}

	public SearchStatistics getCountryStatistics(String country, String searchType, int day, int month, int year) {

		String query = "Select d From SearchStatistics d Where d.country ='" + country + "' and d.day=" + day
				+ " and d.month=" + month + " and d.year=" + year;
		TypedQuery<SearchStatistics> searchQuery = em.createQuery(query, SearchStatistics.class);
		if (searchQuery.getResultList().size() == 0) {
			return null;
		} else {
			return searchQuery.getSingleResult();
		}

	}

	public void persistCountryStatistic(SearchStatistics stat) {
		try {
			if (!em.getTransaction().isActive())
				em.getTransaction().begin();

			em.persist(stat);
			em.getTransaction().commit();
			// em.flush();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

	public void updateCountryStatistics(SearchStatistics stat) {
		try {
			if (!em.getTransaction().isActive())
				em.getTransaction().begin();

			em.merge(stat);
			em.getTransaction().commit();
			// em.flush();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

	public ODMSStatistics getODMSStatistics(ODMSCatalogue node, int year, int month, int day) {

		String query = "Select d From ODMSStatistics d Where d.nodeID=" + node.getId() + " and d.day=" + day
				+ " and d.month=" + month + " and d.year=" + year;
		TypedQuery<ODMSStatistics> searchQuery = em.createQuery(query, ODMSStatistics.class);
		if (searchQuery.getResultList().size() == 0) {
			return null;
		} else {
			return searchQuery.getSingleResult();
		}

	}

	public void persistODMSStatistic(ODMSStatistics stat) {
		try {
			if (!em.getTransaction().isActive())
				em.getTransaction().begin();

			em.persist(stat);
			em.getTransaction().commit();
			// em.flush();
			// System.out.println(node.getId());
			// return mex.getId();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

	public void updateODMSStatistics(ODMSStatistics stat) {
		try {
			if (!em.getTransaction().isActive())
				em.getTransaction().begin();

			em.merge(stat);
			em.getTransaction().commit();
			// em.flush();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

	public void removeODMSStatistics(int nodeID) {
		try {
			Query q;
			if (!em.getTransaction().isActive())
				em.getTransaction().begin();
			q = em.createQuery("DELETE FROM ODMSStatistics where id=" + nodeID);
			q.executeUpdate();
			em.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public KeywordStatistics getKeywordStatistics(String keyword) {

		String query = "Select d From KeywordStatistics d Where d.keyword ='" + keyword + "'";
		TypedQuery<KeywordStatistics> searchQuery = em.createQuery(query, KeywordStatistics.class);
		if (searchQuery.getResultList().size() == 0) {
			return null;
		} else {
			try {
				return searchQuery.getSingleResult();
			} catch (NonUniqueResultException e) {
				return searchQuery.getResultList().get(0);
			}
		}

	}

	public void persistKeywordStatistics(KeywordStatistics stat) {
		try {
			if (!em.getTransaction().isActive())
				em.getTransaction().begin();

			em.persist(stat);
			em.getTransaction().commit();
			// em.flush();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

	public void updateKeywordStatistics(KeywordStatistics stat) {
		try {
			if (!em.getTransaction().isActive())
				em.getTransaction().begin();

			em.merge(stat);
			em.getTransaction().commit();
			// em.flush();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

	public List<String> getAllCountries() {
		String query = "Select distinct d.country from search_statistics d where d.country <> 'Private' and country <> 'Unknown'";
		Query q = em.createNativeQuery(query);
		return (List<String>) q.getResultList();
	}

	public DateTime getMinDateSearchStatistics() {
		DateTime res = new DateTime();
		String query = "from SearchStatistics d where d.id = (Select min(e.id) from SearchStatistics e)";
		TypedQuery<SearchStatistics> q = em.createQuery(query, SearchStatistics.class);

		if (q.getResultList().size() != 0) {
			SearchStatistics tmp = q.getResultList().get(0);
			res = new DateTime(tmp.getYear(), tmp.getMonth() + 1, tmp.getDay(), 0, 0);
		}
		return res;
	}

	public DateTime getMinDateNodesStatistics() {
		DateTime res = new DateTime();
		String query = "from ODMSStatistics d where d.id = (Select min(e.id) from ODMSStatistics e)";
		TypedQuery<ODMSStatistics> q = em.createQuery(query, ODMSStatistics.class);
		if (q.getResultList().size() != 0) {
			ODMSStatistics tmp = q.getResultList().get(0);
			res = new DateTime(tmp.getYear(), tmp.getMonth() + 1, tmp.getDay(), 0, 0);
		}
		return res;
	}

	public KeywordStatistics getMostUsedKeyword() throws NotFoundException {

		String query = "from KeywordStatistics d Where d.counter = (Select max(e.counter) from KeywordStatistics e)";
		TypedQuery<KeywordStatistics> q = em.createQuery(query, KeywordStatistics.class);
		if (q.getResultList().size() != 0) {
			return q.getResultList().get(0);
		} else {
			throw new NotFoundException();
		}

	}

	public ODMSStatisticsResult getODMSCataloguesStatistics(String query) {

		Query q = em.createNativeQuery(query, "ODMSStatisticsResult");
		ODMSStatisticsResult res = null;

		try {
			res = (ODMSStatisticsResult) q.getSingleResult();
		} catch (NoResultException e) {
			res = new ODMSStatisticsResult(0, 0, 0, 0, 0, 0);
		}

		return res;
	}

	public SearchStatisticsResult getSearchStatistics(String query) {

		Query q = em.createNativeQuery(query, "SearchStatisticsResult");
		SearchStatisticsResult res = null;

		try {
			res = (SearchStatisticsResult) q.getSingleResult();
		} catch (NoResultException e) {
			res = new SearchStatisticsResult(0, 0, 0);
		}

		return res;
	}

	public List<KeywordStatisticsResult> getPercentageKeyword() {
		String query = "Select keyword,counter,100*counter/(select sum(counter) from Keyword_Statistics where 1) as percentage from keyword_statistics Where 1 order by counter desc";
		Query q = em.createNativeQuery(query, "KeywordStatisticsResult");

		return (List<KeywordStatisticsResult>) q.getResultList();

	}

	/* END STATISTICS */

	/* USER */

	public User getUser(String username) throws NullPointerException {
		// FROM user WHERE username=? OR email=? OR id=?
		TypedQuery<User> q = em.createQuery("Select d FROM User d WHERE d.username='" + username + "'", User.class);
		User u = null;
		try {
			u = (User) q.getSingleResult();
		} catch (NoResultException ex) {
			throw new NullPointerException("Username or password invalid!");
		}
		return u;
	}

	public void updateUserPassword(User user, String newPassword) throws InvalidPasswordException {
		try {

			user.setPassword(hashPassword(newPassword));
			if (!em.getTransaction().isActive())
				em.getTransaction().begin();
			em.merge(user);
			em.getTransaction().commit();

		} catch (IllegalStateException | NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		}

	}

	public static String hashPassword(String pwd) throws NoSuchAlgorithmException {

		MessageDigest hash = MessageDigest.getInstance("MD5");
		hash.update(pwd.getBytes());
		byte byteData[] = hash.digest();

		// Conversione in stringa dell'hash
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byteData.length; i++)
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));

		return sb.toString();
	}
	/* END USER */

	public void jpaUpdate(Object obj) {
		em.merge(obj);

	}

	public void jpaUpdateAndCommit(Object obj) {
		if (!em.getTransaction().isActive())
			em.getTransaction().begin();

		em.merge(obj);
		em.getTransaction().commit();

	}

	public static void jpaFinalize() {
		emf.close();
		emf = null;
		System.gc();
	}

	public void jpaClose() {
		if (em.isOpen()) {
			em.clear();
			em.close();
		}
		em = null;

	}

	private static <T> T initializeAndUnproxy(T entity) {
		if (entity == null) {
			throw new NullPointerException("Entity passed for initialization is null");
		}

		Hibernate.initialize(entity);
		if (entity instanceof HibernateProxy) {
			entity = (T) ((HibernateProxy) entity).getHibernateLazyInitializer().getImplementation();
		}
		return entity;
	}

}
