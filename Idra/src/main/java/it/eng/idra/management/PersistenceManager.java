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

package it.eng.idra.management;

import it.eng.idra.beans.ConfigurationParameter;
import it.eng.idra.beans.DcatThemes;
import it.eng.idra.beans.Log;
import it.eng.idra.beans.RdfPrefix;
import it.eng.idra.beans.RemoteCatalogue;
import it.eng.idra.beans.User;
import it.eng.idra.beans.exception.InvalidPasswordException;
import it.eng.idra.beans.odms.OdmsCatalogue;
import it.eng.idra.beans.odms.OdmsCatalogueMessage;
import it.eng.idra.beans.statistics.KeywordStatistics;
import it.eng.idra.beans.statistics.KeywordStatisticsResult;
import it.eng.idra.beans.statistics.OdmsStatistics;
import it.eng.idra.beans.statistics.OdmsStatisticsResult;
import it.eng.idra.beans.statistics.SearchStatistics;
import it.eng.idra.beans.statistics.SearchStatisticsResult;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.joda.time.DateTime;

// TODO: Auto-generated Javadoc
/**
 * The Class PersistenceManager.
 */
public class PersistenceManager {

  /** The emf. */
  private static EntityManagerFactory emf;

  /** The em. */
  private EntityManager em;

  /** The logger. */
  private static Logger logger = LogManager.getLogger(PersistenceManager.class);

  static {
    // logger.info("Hibernate EntityManagerFactory init");

    try {
      emf = Persistence.createEntityManagerFactory("org.hibernate.jpa.beans");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Instantiates a new persistence manager.
   */
  public PersistenceManager() {
    // logger.info("Hibernate init");
    try {
      em = emf.createEntityManager();
    } catch (Exception e) {
      e.printStackTrace();
    }
    // logger.info("Hibernate end");
  }

  /**
   * Flush.
   */
  public void flush() {
    em.flush();
  }

  /**
   * Jpa begin transaction.
   */
  public void jpaBeginTransaction() {
    if (!em.getTransaction().isActive()) {
      em.getTransaction().begin();
    }
  }

  /**
   * Jpa get transaction.
   *
   * @return the entity transaction
   */
  public EntityTransaction jpaGetTransaction() {
    return em.getTransaction();
  }

  /**
   * Jpa rollback transaction.
   */
  public void jpaRollbackTransaction() {
    if (em.getTransaction().getRollbackOnly()) {
      em.getTransaction().rollback();
    }
  }

  /**
   * Jpa persist or merge and commit object.
   *
   * @param obj the obj
   */
  public void jpaPersistOrMergeAndCommitObject(Object obj) {

    if (!em.getTransaction().isActive()) {
      em.getTransaction().begin();
    }

    try {
      em.persist(obj);
    } catch (EntityExistsException e) {
      em.merge(obj);
    }
    em.getTransaction().commit();
  }

  /**
   * Jpa commit transanction.
   */
  public void jpaCommitTransanction() {
    em.getTransaction().commit();
    em.clear();
  }

  /**
   * Jpa query.
   *
   * @param query the query
   * @return the query
   */
  public Query jpaQuery(String query) {
    Query q = em.createQuery(query);
    return q;
  }

  /* NODES */

  /**
   * Jpa get odms catalogues.
   *
   * @param withImage the with image
   * @return the list
   */
  public List<OdmsCatalogue> jpaGetOdmsCatalogues(boolean withImage) {

    // TypedQuery<OdmsCatalogue> q = em.createQuery("SELECT d FROM OdmsCatalogue d "
    // + (withImage ? "JOIN FETCH d.image i" : "")+" where d.isActive=true or
    // d.isActive is null",
    TypedQuery<OdmsCatalogue> q = em.createQuery(
        "SELECT d FROM OdmsCatalogue d " + (withImage ? "JOIN FETCH d.image i" : ""),
        OdmsCatalogue.class);
    return q.getResultList();
  }

  /**
   * Jpa insert odms catalogue.
   *
   * @param node the node
   * @return the int
   */
  public int jpaInsertOdmsCatalogue(OdmsCatalogue node) {

    if (!em.getTransaction().isActive()) {
      em.getTransaction().begin();
    }
    // em.getTransaction().begin();
    em.persist(node);
    em.getTransaction().commit();
    // em.flush();

    return node.getId();

  }

  /**
   * Jpa get odms catalogue.
   *
   * @param id the id
   * @return the odms catalogue
   */
  public OdmsCatalogue jpaGetOdmsCatalogue(int id) {
    TypedQuery<OdmsCatalogue> q = em.createQuery("SELECT d FROM OdmsCatalogue d where id=" + id,
        OdmsCatalogue.class);
    return q.getResultList().get(0);
  }

  /**
   * Jpa get odms catalogue.
   *
   * @param id        the id
   * @param withImage the with image
   * @return the odms catalogue
   */
  public OdmsCatalogue jpaGetOdmsCatalogue(int id, boolean withImage) {
    // TypedQuery<OdmsCatalogue> q = em.createQuery("SELECT d FROM OdmsCatalogue d"
    // + (withImage ? " JOIN FETCH d.image i" : "") + " where id=" + id +" and
    // (d.isActive=true or d.isActive is null)",
    TypedQuery<OdmsCatalogue> q = em.createQuery("SELECT d FROM OdmsCatalogue d"
        + (withImage ? " JOIN FETCH d.image i" : "") + " where id=" + id, OdmsCatalogue.class);
    return q.getResultList().get(0);
  }

  /**
   * Jpa get inactive odms catalogue.
   *
   * @param id        the id
   * @param withImage the with image
   * @return the odms catalogue
   */
  public OdmsCatalogue jpaGetInactiveOdmsCatalogue(int id, boolean withImage) {
    TypedQuery<OdmsCatalogue> q = em.createQuery("SELECT d FROM OdmsCatalogue d"
        + (withImage ? " JOIN FETCH d.image i" : "") + " where id=" + id + " and d.isActive=false",
        OdmsCatalogue.class);
    return q.getResultList().get(0);
  }

  /**
   * Jpa get all inactive odms catalogue.
   *
   * @param withImage the with image
   * @return the list
   */
  public List<OdmsCatalogue> jpaGetAllInactiveOdmsCatalogue(boolean withImage) {
    TypedQuery<OdmsCatalogue> q = em.createQuery("SELECT d FROM OdmsCatalogue d"
        + (withImage ? " JOIN FETCH d.image i" : "") + " where d.isActive=false",
        OdmsCatalogue.class);
    return q.getResultList();
  }

  /**
   * Jpa update odms catalogue.
   *
   * @param node the node
   */
  public void jpaUpdateOdmsCatalogue(OdmsCatalogue node) {
    if (!em.getTransaction().isActive()) {
      em.getTransaction().begin();
    }
    em.merge(node);
    em.getTransaction().commit();
  }

  /**
   * Jpa delete odms catalogue.
   *
   * @param id the id
   */
  public void jpaDeleteOdmsCatalogue(int id) {
    // Query q;
    if (!em.getTransaction().isActive()) {
      em.getTransaction().begin();
    }

    em.remove(em.find(OdmsCatalogue.class, id));
    em.flush();
    em.getTransaction().commit();
  }
  /* END NODES */

  /* MESSAGE */

  /**
   * Jpa get odms messages by node.
   *
   * @param nodeId the node ID
   * @return the list
   */
  public List<OdmsCatalogueMessage> jpaGetOdmsMessagesByNode(int nodeId) {
    TypedQuery<OdmsCatalogueMessage> q = em.createQuery(
        "SELECT d FROM OdmsCatalogueMessage d where d.nodeID=" + nodeId + " order by d.date desc",
        OdmsCatalogueMessage.class);
    return q.getResultList();
  }

  /**
   * Jpa get odms message.
   *
   * @param id     the id
   * @param nodeId the node id
   * @return the odms catalogue message
   */
  public OdmsCatalogueMessage jpaGetOdmsMessage(int id, int nodeId) {
    TypedQuery<OdmsCatalogueMessage> q = em.createQuery(
        "SELECT d FROM OdmsCatalogueMessage d where d.nodeID=" + nodeId + "and d.id=" + id,
        OdmsCatalogueMessage.class);
    return q.getSingleResult();
  }

  /**
   * Jpa delete odms message.
   *
   * @param id     the id
   * @param nodeId the node id
   */
  public void jpaDeleteOdmsMessage(int id, int nodeId) {
    Query q;
    if (!em.getTransaction().isActive()) {
      em.getTransaction().begin();
    }
    q = em
        .createQuery("DELETE FROM OdmsCatalogueMessage where nodeID = " + nodeId + " and id=" + id);
    q.executeUpdate();
    em.getTransaction().commit();
  }

  /**
   * Jpa delete all odms message.
   *
   * @param nodeId the node id
   */
  public void jpaDeleteAllOdmsMessage(int nodeId) {
    Query q;
    if (!em.getTransaction().isActive()) {
      em.getTransaction().begin();
    }
    q = em.createQuery("DELETE FROM OdmsCatalogueMessage where nodeID = " + nodeId);
    q.executeUpdate();
    em.getTransaction().commit();
  }

  /**
   * Jpa get messages count.
   *
   * @param nodeIds the node ids
   * @return the hash map
   */
  public HashMap<Integer, Long> jpaGetMessagesCount(List<Integer> nodeIds) {
    HashMap<Integer, Long> res = new HashMap<Integer, Long>();
    for (Integer nodeId : nodeIds) {
      Query q = em
          .createQuery("SELECT count(*) FROM OdmsCatalogueMessage d Where d.nodeID=" + nodeId);
      long var = (long) q.getSingleResult();
      res.put(nodeId, var);
    }

    return res;
  }

  /**
   * Jpa insert odms message.
   *
   * @param message the message
   * @param nodeId  the node id
   * @return the int
   */
  public int jpaInsertOdmsMessage(String message, int nodeId) {
    try {

      OdmsCatalogueMessage mex = new OdmsCatalogueMessage(nodeId, message,
          ZonedDateTime.now(ZoneOffset.UTC));

      if (!em.getTransaction().isActive()) {
        em.getTransaction().begin();
      }

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

  /**
   * Gets the all prefixes.
   *
   * @return the all prefixes
   */
  public List<RdfPrefix> getAllPrefixes() {
    TypedQuery<RdfPrefix> q = em.createQuery("SELECT d FROM RdfPrefix d", RdfPrefix.class);
    return q.getResultList();
  }

  /**
   * Gets the prefix.
   *
   * @param id the id
   * @return the prefix
   */
  public RdfPrefix getPrefix(int id) {
    TypedQuery<RdfPrefix> q = em.createQuery("SELECT d FROM RdfPrefix d where d.id=" + id,
        RdfPrefix.class);
    return q.getResultList().get(0);
  }

  /**
   * Check prefix exists.
   *
   * @param prefix the prefix
   * @return true, if successful
   */
  public boolean checkPrefixExists(RdfPrefix prefix) {
    TypedQuery<RdfPrefix> q = em.createQuery(
        "SELECT d FROM RdfPrefix d where d.prefix='" + prefix.getPrefix() + "'", RdfPrefix.class);
    return q.getResultList().size() > 0;
  }

  /**
   * Delete prefix.
   *
   * @param id the id
   * @return true, if successful
   */
  public boolean deletePrefix(int id) {

    try {
      Query q;
      if (!em.getTransaction().isActive()) {
        em.getTransaction().begin();
      }
      q = em.createQuery("DELETE FROM RdfPrefix where id=" + id);
      q.executeUpdate();
      em.getTransaction().commit();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Update prefix.
   *
   * @param p the p
   * @return true, if successful
   */
  public boolean updatePrefix(RdfPrefix p) {

    try {
      if (!em.getTransaction().isActive()) {
        em.getTransaction().begin();
      }
      em.merge(p);
      em.getTransaction().commit();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Adds the prefix.
   *
   * @param p the p
   * @return true, if successful
   */
  public boolean addPrefix(RdfPrefix p) {

    try {
      if (!em.getTransaction().isActive()) {
        em.getTransaction().begin();
      }
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

  /**
   * Gets the configuration.
   *
   * @return the configuration
   */
  public HashMap<String, String> getConfiguration() {
    HashMap<String, String> config = new HashMap<>();
    TypedQuery<ConfigurationParameter> q = em.createQuery("SELECT d FROM ConfigurationParameter d ",
        ConfigurationParameter.class);
    config = (HashMap<String, String>) q.getResultList().stream().collect(Collectors.toMap(
        ConfigurationParameter::getParameterName, ConfigurationParameter::getParameterValue));
    return config;
  }

  /**
   * Gets the dcat thems.
   *
   * @return the dcat thems
   */
  public List<DcatThemes> getDcatThems() {
    String query = "FROM DcatThemes";
    TypedQuery<DcatThemes> q = em.createQuery(query, DcatThemes.class);
    return q.getResultList();
  }

  /**
   * Gets the configuration list.
   *
   * @return the configuration list
   */
  public List<ConfigurationParameter> getConfigurationList() {
    TypedQuery<ConfigurationParameter> q = em.createQuery("SELECT d FROM ConfigurationParameter d ",
        ConfigurationParameter.class);
    return q.getResultList();
  }

  /**
   * Update configuration list.
   *
   * @param parameter the parameter
   * @return true, if successful
   */
  public boolean updateConfigurationList(List<ConfigurationParameter> parameter) {
    try {
      if (!em.getTransaction().isActive()) {
        em.getTransaction().begin();
      }
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

  /* Remote Catalogues */

  /**
   * Gets the remote catalogues.
   *
   * @return the remote catalogues
   */
  public HashMap<String, String> getRemoteCatalogues() {
    HashMap<String, String> catalogues = new HashMap<>();
    TypedQuery<RemoteCatalogue> q = em.createQuery("SELECT d FROM RemoteCatalogue d ",
        RemoteCatalogue.class);
    catalogues = (HashMap<String, String>) q.getResultList().stream()
        .collect(Collectors.toMap(RemoteCatalogue::getCatalogueName, RemoteCatalogue::getUrl));
    return catalogues;
  }

  /**
   * Gets the rem cat id.
   *
   * @param id the id
   * @return the rem cat id
   */
  public RemoteCatalogue getRemCatId(int id) {
    TypedQuery<RemoteCatalogue> q = em
        .createQuery("SELECT d FROM RemoteCatalogue d where d.id=" + id, RemoteCatalogue.class);
    return q.getResultList().get(0);
  }

  /**
   * Check rem cat exists.
   *
   * @param rm the rm
   * @return true, if successful
   */
  public boolean checkRemCatExists(RemoteCatalogue rm) {
    TypedQuery<RemoteCatalogue> q = em.createQuery(
        "SELECT d FROM RemoteCatalogue d where d.catalogueName='" + rm.getCatalogueName() + "'",
        RemoteCatalogue.class);
    return q.getResultList().size() > 0;
  }

  /**
   * Update remote catalogues.
   *
   * @param catalogues the catalogues
   * @return true, if successful
   */
  public boolean updateRemoteCatalogues(List<RemoteCatalogue> catalogues) {
    try {
      if (!em.getTransaction().isActive()) {
        em.getTransaction().begin();
      }
      for (RemoteCatalogue tmp : catalogues) {
        em.merge(tmp);
      }
      em.getTransaction().commit();
      return true;
    } catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }
  }

  /**
   * Adds the remote cat.
   *
   * @param r the r
   * @return true, if successful
   */
  public boolean addRemoteCat(RemoteCatalogue r) {

    try {
      if (!em.getTransaction().isActive()) {
        em.getTransaction().begin();
      }
      em.persist(r);
      em.getTransaction().commit();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Delete rem cat.
   *
   * @param id the id
   * @return true, if successful
   */
  public boolean deleteRemCat(int id) {

    try {
      Query q;
      if (!em.getTransaction().isActive()) {
        em.getTransaction().begin();
      }
      q = em.createQuery("DELETE FROM RemoteCatalogue where id=" + id);
      q.executeUpdate();
      em.getTransaction().commit();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Update rem cat.
   *
   * @param rm the rm
   * @return true, if successful
   */
  public boolean updateRemCat(RemoteCatalogue rm) {
    try {
      if (!em.getTransaction().isActive()) {
        em.getTransaction().begin();
      }
      em.merge(rm);
      em.getTransaction().commit();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Gets the remote catalogues list.
   *
   * @return the remote catalogues list
   */
  public List<RemoteCatalogue> getremoteCataloguesList() {
    TypedQuery<RemoteCatalogue> q = em.createQuery("SELECT d FROM RemoteCatalogue d ",
        RemoteCatalogue.class);
    return q.getResultList();
  }
  /* End Remote Catalogues */

  /* LOGS */

  /**
   * Builds the logs query.
   *
   * @param levelList the level list
   * @param from      the from
   * @param to        the to
   * @return the string
   */
  private String buildLogsQuery(List<String> levelList, ZonedDateTime from, ZonedDateTime to) {

    String query = "SELECT d FROM Log d WHERE ( ";

    if (levelList.size() == 1) {
      query += " d.level = '" + levelList.get(0) + "' ";
    } else {
      for (int i = 0; i < levelList.size(); i++) {
        if (i == 0 && i != (levelList.size() - 1)) {
          query += " d.level = '" + levelList.get(i) + "' ";
        } else {
          query += " OR d.level = '" + levelList.get(i) + "' ";
        }
      }
      // query += " d.level = '" + levelList.get(0) + "' " + " OR
      // d.level='" + levelList.get(1) + "' " + " OR d.level='" +
      // levelList.get(2) + "' ";
    }

    query += " ) AND ( d.dated  BETWEEN '" + from.getYear() + "-" + from.getMonthValue() + "-"
        + from.getDayOfMonth() + " 00:00:00" + "' " + " AND '" + to.getYear() + "-"
        + to.getMonthValue() + "-" + to.getDayOfMonth() + " 23:59:59" + "' ) order by d.dated asc";

    return query;
  }

  /**
   * Gets the logs.
   *
   * @param level the level
   * @param from  the from
   * @param to    the to
   * @return the logs
   */
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

  /**
   * Delete logs.
   *
   * @param now the now
   */
  public void deleteLogs(ZonedDateTime now) {
    // System.out.println(now.toEpochSecond());
    em.getTransaction().begin();
    em.createNativeQuery(
        "delete from logs where UNIX_TIMESTAMP(dated) < " + now.toEpochSecond() + "")
        .executeUpdate();
    em.getTransaction().commit();
  }
  /* END Logs */

  /* Statistics */

  /**
   * Gets the country from ip.
   *
   * @param ip the ip
   * @return the country from ip
   */
  public String getCountryFromIp(String ip) {

    String query = "SELECT c.country  FROM ip2nationCountries c,  ip2nation i  WHERE"
        + " i.ip < INET_ATON('" + ip + "') AND  c.code = i.country ORDER BY  i.ip DESC";

    Query q = em.createNativeQuery(query);
    String res = "";
    if (q.getResultList().size() > 0) {
      res = q.getResultList().get(0).toString();
    }

    return res;
  }

  /**
   * Gets the country statistics.
   *
   * @param country    the country
   * @param searchType the search type
   * @param day        the day
   * @param month      the month
   * @param year       the year
   * @return the country statistics
   */
  public SearchStatistics getCountryStatistics(String country, String searchType, int day,
      int month, int year) {

    String query = "Select d From SearchStatistics d Where d.country ='" + country + "' and d.day="
        + day + " and d.month=" + month + " and d.year=" + year;
    TypedQuery<SearchStatistics> searchQuery = em.createQuery(query, SearchStatistics.class);
    if (searchQuery.getResultList().size() == 0) {
      return null;
    } else {
      return searchQuery.getSingleResult();
    }

  }

  /**
   * Persist country statistic.
   *
   * @param stat the stat
   */
  public void persistCountryStatistic(SearchStatistics stat) {
    try {
      if (!em.getTransaction().isActive()) {
        em.getTransaction().begin();
      }

      em.persist(stat);
      em.getTransaction().commit();
      // em.flush();
    } catch (IllegalStateException e) {
      e.printStackTrace();
    }
  }

  /**
   * Update country statistics.
   *
   * @param stat the stat
   */
  public void updateCountryStatistics(SearchStatistics stat) {
    try {
      if (!em.getTransaction().isActive()) {
        em.getTransaction().begin();
      }

      em.merge(stat);
      em.getTransaction().commit();
      // em.flush();
    } catch (IllegalStateException e) {
      e.printStackTrace();
    }
  }

  /**
   * Gets the ODMS statistics.
   *
   * @param node  the node
   * @param year  the year
   * @param month the month
   * @param day   the day
   * @return the ODMS statistics
   */
  public OdmsStatistics getOdmsStatistics(OdmsCatalogue node, int year, int month, int day) {

    String query = "Select d From OdmsStatistics d Where d.nodeID=" + node.getId() + " and d.day="
        + day + " and d.month=" + month + " and d.year=" + year;
    TypedQuery<OdmsStatistics> searchQuery = em.createQuery(query, OdmsStatistics.class);
    if (searchQuery.getResultList().size() == 0) {
      return null;
    } else {
      return searchQuery.getSingleResult();
    }

  }

  /**
   * Persist ODMS statistic.
   *
   * @param stat the stat
   */
  public void persistOdmsStatistic(OdmsStatistics stat) {
    try {
      if (!em.getTransaction().isActive()) {
        em.getTransaction().begin();
      }

      em.persist(stat);
      em.getTransaction().commit();
      // em.flush();
      // System.out.println(node.getId());
      // return mex.getId();
    } catch (IllegalStateException e) {
      e.printStackTrace();
    }
  }

  /**
   * Update ODMS statistics.
   *
   * @param stat the stat
   */
  public void updateOdmsStatistics(OdmsStatistics stat) {
    try {
      if (!em.getTransaction().isActive()) {
        em.getTransaction().begin();
      }

      em.merge(stat);
      em.getTransaction().commit();
      // em.flush();
    } catch (IllegalStateException e) {
      e.printStackTrace();
    }
  }

  /**
   * Removes the ODMS statistics.
   *
   * @param nodeId the node ID
   */
  public void removeOdmsStatistics(int nodeId) {
    try {
      Query q;
      if (!em.getTransaction().isActive()) {
        em.getTransaction().begin();
      }
      q = em.createQuery("DELETE FROM OdmsStatistics where id=" + nodeId);
      q.executeUpdate();
      em.getTransaction().commit();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Gets the keyword statistics.
   *
   * @param keyword the keyword
   * @return the keyword statistics
   */
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

  /**
   * Persist keyword statistics.
   *
   * @param stat the stat
   */
  public void persistKeywordStatistics(KeywordStatistics stat) {
    try {
      if (!em.getTransaction().isActive()) {
        em.getTransaction().begin();
      }

      em.persist(stat);
      em.getTransaction().commit();
      // em.flush();
    } catch (IllegalStateException e) {
      e.printStackTrace();
    }
  }

  /**
   * Update keyword statistics.
   *
   * @param stat the stat
   */
  public void updateKeywordStatistics(KeywordStatistics stat) {
    try {
      if (!em.getTransaction().isActive()) {
        em.getTransaction().begin();
      }

      em.merge(stat);
      em.getTransaction().commit();
      // em.flush();
    } catch (IllegalStateException e) {
      e.printStackTrace();
    }
  }

  /**
   * Gets the all countries.
   *
   * @return the all countries
   */
  public List<String> getAllCountries() {
    String query = "Select distinct d.country from search_statistics d "
        + "where d.country <> 'Private' and country <> 'Unknown'";
    Query q = em.createNativeQuery(query);
    return (List<String>) q.getResultList();
  }

  /**
   * Gets the min date search statistics.
   *
   * @return the min date search statistics
   */
  public DateTime getMinDateSearchStatistics() {
    DateTime res = new DateTime();
    String query = "from SearchStatistics d where "
        + "d.id = (Select min(e.id) from SearchStatistics e)";
    TypedQuery<SearchStatistics> q = em.createQuery(query, SearchStatistics.class);

    if (q.getResultList().size() != 0) {
      SearchStatistics tmp = q.getResultList().get(0);
      res = new DateTime(tmp.getYear(), tmp.getMonth() + 1, tmp.getDay(), 0, 0);
    }
    return res;
  }

  /**
   * Gets the min date nodes statistics.
   *
   * @return the min date nodes statistics
   */
  public DateTime getMinDateNodesStatistics() {
    DateTime res = new DateTime();
    String query = "from OdmsStatistics d where d.id = (Select min(e.id) from ODMSStatistics e)";
    TypedQuery<OdmsStatistics> q = em.createQuery(query, OdmsStatistics.class);
    if (q.getResultList().size() != 0) {
      OdmsStatistics tmp = q.getResultList().get(0);
      res = new DateTime(tmp.getYear(), tmp.getMonth() + 1, tmp.getDay(), 0, 0);
    }
    return res;
  }

  /**
   * Gets the most used keyword.
   *
   * @return the most used keyword
   * @throws NotFoundException the not found exception
   */
  public KeywordStatistics getMostUsedKeyword() throws NotFoundException {

    String query = "from KeywordStatistics d "
        + "Where d.counter = (Select max(e.counter) from KeywordStatistics e)";
    TypedQuery<KeywordStatistics> q = em.createQuery(query, KeywordStatistics.class);
    if (q.getResultList().size() != 0) {
      return q.getResultList().get(0);
    } else {
      throw new NotFoundException();
    }

  }

  /**
   * Gets the ODMS catalogues statistics.
   *
   * @param query the query
   * @return the ODMS catalogues statistics
   */
  public OdmsStatisticsResult getOdmsCataloguesStatistics(String query) {

    Query q = em.createNativeQuery(query, "OdmsStatisticsResult");
    OdmsStatisticsResult res = null;

    try {
      res = (OdmsStatisticsResult) q.getSingleResult();
    } catch (NoResultException e) {
      res = new OdmsStatisticsResult(0, 0, 0, 0, 0, 0);
    }

    return res;
  }

  /**
   * Gets the search statistics.
   *
   * @param query the query
   * @return the search statistics
   */
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

  /**
   * Gets the percentage keyword.
   *
   * @return the percentage keyword
   */
  public List<KeywordStatisticsResult> getPercentageKeyword() {
    String query = "Select keyword,counter,100*counter/(select sum(counter) "
        + "from Keyword_Statistics "
        + "where 1) as percentage from keyword_statistics Where 1 order by counter desc";
    Query q = em.createNativeQuery(query, "KeywordStatisticsResult");

    return (List<KeywordStatisticsResult>) q.getResultList();

  }

  /* END STATISTICS */

  /* USER */

  /**
   * Gets the user.
   *
   * @param username the username
   * @return the user
   * @throws NullPointerException the null pointer exception
   */
  public User getUser(String username) throws NullPointerException {
    // FROM user WHERE username=? OR email=? OR id=?
    TypedQuery<User> q = em.createQuery("Select d FROM User d WHERE d.username='" + username + "'",
        User.class);
    User u = null;
    try {
      u = (User) q.getSingleResult();
    } catch (NoResultException ex) {
      throw new NullPointerException("Username or password invalid!");
    }
    return u;
  }

  /**
   * Update user password.
   *
   * @param user        the user
   * @param newPassword the new password
   * @throws InvalidPasswordException the invalid password exception
   */
  public void updateUserPassword(User user, String newPassword) throws InvalidPasswordException {
    try {

      user.setPassword(hashPassword(newPassword));
      if (!em.getTransaction().isActive()) {
        em.getTransaction().begin();
      }
      em.merge(user);
      em.getTransaction().commit();

    } catch (IllegalStateException | NoSuchAlgorithmException ex) {
      ex.printStackTrace();
    }

  }

  /**
   * Hash password.
   *
   * @param pwd the pwd
   * @return the string
   * @throws NoSuchAlgorithmException the no such algorithm exception
   */
  public static String hashPassword(String pwd) throws NoSuchAlgorithmException {

    MessageDigest hash = MessageDigest.getInstance("MD5");
    hash.update(pwd.getBytes());
    byte[] byteData = hash.digest();

    // Conversione in stringa dell'hash
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < byteData.length; i++) {
      sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
    }

    return sb.toString();
    // return "";
  }

  /* END USER */

  /**
   * Jpa update.
   *
   * @param obj the obj
   */
  public void jpaUpdate(Object obj) {
    em.merge(obj);

  }

  /**
   * Jpa update and commit.
   *
   * @param obj the obj
   */
  public void jpaUpdateAndCommit(Object obj) {
    if (!em.getTransaction().isActive()) {
      em.getTransaction().begin();
    }

    em.merge(obj);
    em.getTransaction().commit();

  }

  /**
   * Jpa finalize.
   */
  public static void jpaFinalize() {
    emf.close();
    emf = null;
    System.gc();
  }

  /**
   * Jpa close.
   */
  public void jpaClose() {
    if (em.isOpen()) {
      em.clear();
      em.close();
    }
    em = null;

  }

  /**
   * Initialize and unproxy.
   *
   * @param        <T> the generic type
   * @param entity the entity
   * @return the t
   */
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
