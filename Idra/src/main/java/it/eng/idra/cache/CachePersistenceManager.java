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

package it.eng.idra.cache;

import it.eng.idra.beans.Datalet;
import it.eng.idra.beans.dcat.DcatDataset;
import it.eng.idra.beans.dcat.DcatDatasetId;
import it.eng.idra.beans.dcat.DcatDistribution;
import it.eng.idra.beans.orion.OrionDistributionConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class CachePersistenceManager.
 */
public class CachePersistenceManager {

  /** The emf. */
  private static EntityManagerFactory emf;

  /** The em. */
  private EntityManager em;

  /** The logger. */
  private static Logger logger = LogManager.getLogger(CachePersistenceManager.class);

  static {
    // logger.info("Hibernate EntityManagerFactory init");
    try {
      emf = Persistence.createEntityManagerFactory("org.hibernate.jpa");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Instantiates a new cache persistence manager.
   */
  public CachePersistenceManager() {
    // logger.info("Hibernate init");
    try {
      em = emf.createEntityManager();

    } catch (Exception e) {
      e.printStackTrace();
    }
    // logger.info("Hibernate end");
  }

  /**
   * Jpa begin transaction.
   */
  public void jpaBeginTransaction() {
    em.getTransaction().begin();
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
   * Jpa clear.
   */
  public void jpaClear() {
    em.clear();
  }

  /**
   * Jpa persist dataset.
   *
   * @param obj the obj
   * @throws EntityExistsException the entity exists exception
   */
  public void jpaPersistDataset(DcatDataset obj) throws EntityExistsException {
    em.persist(obj);
  }

  /**
   * Jpa merge and commit dataset.
   *
   * @param obj the obj
   * @throws IllegalStateException the illegal state exception
   * @throws RollbackException     the rollback exception
   */
  public void jpaMergeAndCommitDataset(DcatDataset obj)
      throws IllegalStateException, RollbackException {

    try {
      if (!em.getTransaction().isActive()) {
        em.getTransaction().begin();
      }

      em.merge(obj);
      em.getTransaction().commit();

    } catch (Exception e) {
      em.getTransaction().rollback();
      throw e;
    }
  }

  /**
   * Jpa merge and commit distribution.
   *
   * @param obj the obj
   * @throws IllegalStateException the illegal state exception
   * @throws RollbackException     the rollback exception
   */
  public void jpaMergeAndCommitDistribution(DcatDistribution obj)
      throws IllegalStateException, RollbackException {

    try {
      if (!em.getTransaction().isActive()) {
        em.getTransaction().begin();
      }

      em.merge(obj);
      em.getTransaction().commit();

    } catch (Exception e) {
      e.printStackTrace();
      em.getTransaction().rollback();
      throw e;
    }
  }

  /**
   * Jpa persist or merge and commit dataset.
   *
   * @param obj the obj
   * @throws IllegalStateException the illegal state exception
   * @throws RollbackException     the rollback exception
   */
  public void jpaPersistOrMergeAndCommitDataset(DcatDataset obj)
      throws IllegalStateException, RollbackException {

    try {
      if (!em.getTransaction().isActive()) {
        em.getTransaction().begin();
      }

      try {
        em.persist(obj);
      } catch (PersistenceException e) {
        if (em.getTransaction().getRollbackOnly()) {

          em.getTransaction().rollback();
          em.getTransaction().begin();
        }
        em.merge(obj);
      }

      em.getTransaction().commit();

    } catch (RollbackException e) {
      em.getTransaction().rollback();
      throw e;
    }
  }

  /**
   * Jpa persist or merge and commit object.
   *
   * @param obj the obj
   */
  public void jpaPersistOrMergeAndCommitObject(Object obj) {
    /* Rob_mod 16/10/2016 */
    em.getTransaction().begin();
    try {
      em.persist(obj);
    } catch (EntityExistsException e) {
      em.merge(obj);
    }
    em.getTransaction().commit();
  }

  /**
   * Jpa remove dataset.
   *
   * @param obj the obj
   */
  public void jpaRemoveDataset(DcatDataset obj) {
    em.remove(obj);
  }

  /**
   * Jpa commit transanction.
   *
   * @throws IllegalStateException the illegal state exception
   * @throws RollbackException     the rollback exception
   */
  public void jpaCommitTransanction() throws IllegalStateException, RollbackException {
    em.getTransaction().commit();
    em.clear();
  }

  /**
   * Jpa persist and commit dataset.
   *
   * @param obj the obj
   * @throws EntityExistsException the entity exists exception
   */
  public void jpaPersistAndCommitDataset(DcatDataset obj) throws EntityExistsException {
    // logger.info(obj.getId());
    try {
      em.getTransaction().begin();
      em.persist(obj);
      em.getTransaction().commit();
    } catch (IllegalStateException e) {
      obj.toString();
    }
    // em.clear();
  }

  /**
   * Jpa persist datasets.
   *
   * @param objectList the object list
   */
  public void jpaPersistDatasets(List<DcatDataset> objectList) {
    try {
      em.getTransaction().begin();
      for (DcatDataset o : objectList) {
        try {
          em.persist(o);
        } catch (EntityExistsException e) {
          logger.info(o.getId() + "threw Exception");
        }

      }
      em.getTransaction().commit();

    } catch (RollbackException e) {
      if (e.getMessage().contains("Duplicate entry")) {
        logger.info(e.getMessage() + "\n This dataset will be skipped");
      } else {
        // e.printStackTrace();
        logger.info("\n\n\nException " + e.getMessage() + "\n\n\n ");
      }
    }
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

  /**
   * Jpa get datasets.
   *
   * @return the list
   */
  public List<DcatDataset> jpaGetDatasets() {
    TypedQuery<DcatDataset> q = em.createQuery("SELECT d FROM DcatDataset d", DcatDataset.class);
    return q.getResultList();
  }

  /**
   * Jpa get datasets by regex.
   *
   * @param searchParameters the search parameters
   * @return the list
   */
  public List<DcatDataset> jpaGetDatasetsByRegex(HashMap<String, Object> searchParameters) {
    ArrayList<String> nodes = new ArrayList<String>();

    HashMap<String, HashMap<String, String>> filterMap = 
        (HashMap<String, HashMap<String, String>>) searchParameters
        .get("filters");

    nodes.addAll(filterMap.keySet());
    HashMap<String, String> filters = filterMap.get(nodes.get(0));

    String query = "SELECT d FROM DcatDataset d where d.nodeID in (:nodes) and  ( regexp(d.title,'"
        + filters.get("regex") + "') = true " + "or regexp(d.description,'" + filters.get("regex")
        + "') = true ) ";

    // query += "and

    String[] sort = ((String) searchParameters.getOrDefault("sort", "id,asc")).split(",");
    if (!sort[0].equals("id") || !sort[0].equals("title")) {
      sort[0] = "id";
    }

    if (!sort[1].equals("asc") || !sort[1].equals("desc")) {
      sort[1] = "asc";
    }

    query += " ORDER BY " + sort[0] + " " + sort[1];
    // System.out.println(query);
    TypedQuery<DcatDataset> q = em.createQuery(query, DcatDataset.class);
    q.setParameter("nodes", nodes);
    // q.setFirstResult(Integer.parseInt((String)
    // searchParameters.get("start")));
    // q.setMaxResults( Integer.parseInt((String)
    // searchParameters.get("rows"))==0 ? 1 : Integer.parseInt((String)
    // searchParameters.get("rows")));

    return q.getResultList();
  }

  /**
   * Jpa get datasets by location.
   *
   * @param searchParameters the search parameters
   * @return the list
   */
  public List<DcatDataset> jpaGetDatasetsByLocation(HashMap<String, Object> searchParameters) {
    ArrayList<String> nodes = new ArrayList<String>();

    HashMap<String, HashMap<String, String>> filterMap = 
        (HashMap<String, HashMap<String, String>>) searchParameters
        .get("filters");

    nodes.addAll(filterMap.keySet());
    HashMap<String, String> filters = filterMap.get(nodes.get(0));

    String query = "SELECT d FROM DcatDataset d join DctLocation l on d.spatialCoverage=l.id"
        + " where d.nodeID in (:nodes) and l.geometry is not '' "
        + "and ST_Intersects(ST_GeomFromGeoJSON('" + filters.get("geographic_area")
        + "'),ST_GeomFromGeoJSON(l.geometry))=true ";

    if (filters.containsKey("regex")) {
      query += "and ( regexp(d.title,'" + filters.get("regex")
          + "') = true or regexp(d.description,'" + filters.get("regex") + "') = true ) ";
    } else {
      if (filters.containsKey("title")) {
        query += " and d.title like '%" + filters.get("title") + "%' ";
      }

      if (filters.containsKey("description")) {
        query += " and d.description like '%" + filters.get("description") + "%' ";
      }
    }

    String[] sort = ((String) searchParameters.getOrDefault("sort", "id,asc")).split(",");
    if (!sort[0].equals("id") || !sort[0].equals("title")) {
      sort[0] = "d.id";
    }

    if (!sort[1].equals("asc") || !sort[1].equals("desc")) {
      sort[1] = "asc";
    }

    query += " ORDER BY " + sort[0] + " " + sort[1];
    // System.out.println(query);
    TypedQuery<DcatDataset> q = em.createQuery(query, DcatDataset.class);
    q.setParameter("nodes", nodes);
    // q.setFirstResult(Integer.parseInt((String)
    // searchParameters.get("start")));
    // q.setMaxResults( Integer.parseInt((String)
    // searchParameters.get("rows"))==0 ? 1 : Integer.parseInt((String)
    // searchParameters.get("rows")));

    return q.getResultList();
  }

  /**
   * Jpa get count datasets by regex.
   *
   * @param searchParameters the search parameters
   * @return the int
   */
  public int jpaGetCountDatasetsByRegex(HashMap<String, Object> searchParameters) {
    ArrayList<String> nodes = new ArrayList<String>();

    HashMap<String, HashMap<String, String>> filterMap = 
        (HashMap<String, HashMap<String, String>>) searchParameters
        .get("filters");

    nodes.addAll(filterMap.keySet());
    HashMap<String, String> filters = filterMap.get(nodes.get(0));

    String query = "SELECT d FROM DcatDataset d where d.nodeID in (:nodes) and  ( regexp(d.title,'"
        + filters.get("regex") + "') = true " + "or regexp(d.description,'" + filters.get("regex")
        + "') = true ) ";

    String[] sort = ((String) searchParameters.getOrDefault("sort", "id,asc")).split(",");
    if (!sort[0].equals("id") || !sort[0].equals("title")) {
      sort[0] = "d.id";
    }

    if (!sort[1].equals("asc") || !sort[1].equals("desc")) {
      sort[1] = "asc";
    }

    query += " ORDER BY " + sort[0] + " " + sort[1];

    TypedQuery<DcatDataset> q = em.createQuery(query, DcatDataset.class);
    q.setParameter("nodes", nodes);
    // q.setFirstResult(Integer.parseInt((String)
    // searchParameters.get("start")));
    // q.setMaxResults( Integer.parseInt((String)
    // searchParameters.get("rows"))==0 ? 1 : Integer.parseInt((String)
    // searchParameters.get("rows")));

    return q.getResultList().size();
  }

  /**
   * Jpa get count datasets by location.
   *
   * @param searchParameters the search parameters
   * @return the int
   */
  public int jpaGetCountDatasetsByLocation(HashMap<String, Object> searchParameters) {
    ArrayList<String> nodes = new ArrayList<String>();

    HashMap<String, HashMap<String, String>> filterMap = 
        (HashMap<String, HashMap<String, String>>) searchParameters
        .get("filters");

    nodes.addAll(filterMap.keySet());
    HashMap<String, String> filters = filterMap.get(nodes.get(0));

    // String query = "SELECT d FROM DcatDataset d where d.nodeID in (:nodes) and
    // d.spatialCoverage is not '' and ST_Intersects(ST_GeomFromGeoJSON('"
    // + filters.get("geographic_area") +
    // "'),ST_GeomFromGeoJSON(d.spatialCoverage))=true ";

    String query = "SELECT d FROM DcatDataset d join DctLocation l on d.spatialCoverage=l.id "
        + "where d.nodeID in (:nodes) and l.geometry is not '' "
        + "and ST_Intersects(ST_GeomFromGeoJSON('" + filters.get("geographic_area")
        + "'),ST_GeomFromGeoJSON(l.geometry))=true ";

    if (filters.containsKey("regex")) {
      query += "and ( regexp(d.title,'" + filters.get("regex")
          + "') = true or regexp(d.description,'" + filters.get("regex") + "') = true ) ";
    } else {
      if (filters.containsKey("title")) {
        query += " and d.title like '%" + filters.get("title") + "%' ";
      }

      if (filters.containsKey("description")) {
        query += " and d.description like '%" + filters.get("description") + "%' ";
      }
    }

    String[] sort = ((String) searchParameters.getOrDefault("sort", "id,asc")).split(",");
    if (!sort[0].equals("id") || !sort[0].equals("title")) {
      sort[0] = "d.id";
    }

    if (!sort[1].equals("asc") || !sort[1].equals("desc")) {
      sort[1] = "asc";
    }

    query += " ORDER BY " + sort[0] + " " + sort[1];

    TypedQuery<DcatDataset> q = em.createQuery(query, DcatDataset.class);
    q.setParameter("nodes", nodes);
    // q.setFirstResult(Integer.parseInt((String)
    // searchParameters.get("start")));
    // q.setMaxResults( Integer.parseInt((String)
    // searchParameters.get("rows"))==0 ? 1 : Integer.parseInt((String)
    // searchParameters.get("rows")));

    return q.getResultList().size();
  }

  /**
   * Jpa get datasets by ODMS node.
   *
   * @param nodeId the node ID
   * @return the list
   */
  public List<DcatDataset> jpaGetDatasetsByOdmsNode(int nodeId) {
    TypedQuery<DcatDataset> q = em
        .createQuery("SELECT d FROM DcatDataset d where d.nodeID = " + nodeId, DcatDataset.class);
    return q.getResultList();
  }

  /**
   * Jpa get datasets by ID.
   *
   * @param datasetId the dataset ID
   * @return the dcat dataset
   */
  public DcatDataset jpaGetDatasetsById(String datasetId) {
    TypedQuery<DcatDataset> q = em.createQuery(
        "SELECT d FROM DcatDataset d where d.id = '" + datasetId + "'", DcatDataset.class);
    return q.getResultList().get(0);
  }

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
    em.getTransaction().begin();
    em.merge(obj);
    em.getTransaction().commit();

  }

  /**
   * Jpa update dataset.
   *
   * @param obj            the obj
   * @param getTransaction the get transaction
   */
  public void jpaUpdateDataset(DcatDataset obj, boolean getTransaction) {
    if (getTransaction) {
      em.getTransaction().begin();
    }

    em.merge(obj);

    if (getTransaction) {
      em.getTransaction().commit();
    }

  }

  /**
   * Jpa update distribution.
   *
   * @param obj            the obj
   * @param getTransaction the get transaction
   */
  public void jpaUpdateDistribution(DcatDistribution obj, boolean getTransaction) {
    if (getTransaction) {
      em.getTransaction().begin();
    }

    em.merge(obj);

    if (getTransaction) {
      em.getTransaction().commit();
    }

  }

  /**
   * Jpa delete dataset.
   *
   * @param obj the obj
   */
  public void jpaDeleteDataset(Object obj) {
    DcatDataset matching;
    DcatDataset toDelete = (DcatDataset) obj;
    em.getTransaction().begin();
    matching = (DcatDataset) em.find(DcatDataset.class,
        new DcatDatasetId(toDelete.getId(), toDelete.getNodeId()));
    em.remove(matching);
    em.getTransaction().commit();
  }

  // public void jpaDeleteDataset(DcatDataset d){
  // Query q;
  // logger.info("HIBERNATE: Delete Transaction BEGIN");
  // em.getTransaction().begin();
  // q= em.createQuery("DELETE FROM DcatDataset where dataset_id='" +
  // d.getId() + "' AND nodeID='" + d.getNodeID() + "'");
  // q.executeUpdate();
  // logger.info("HIBERNATE: Delete Transaction COMMIT");
  // em.getTransaction().commit();
  //
  // }

  /**
   * Jpa delete datasets.
   *
   * @param objList the obj list
   */
  public void jpaDeleteDatasets(List<DcatDataset> objList) {
    em.getTransaction().begin();
    logger.info("HIBERNATE: Delete Transaction BEGIN");
    for (DcatDataset o : objList) {
      DcatDataset dataset = (DcatDataset) em.find(DcatDataset.class,
          new DcatDatasetId(o.getId(), o.getNodeId()));
      // DcatDataset managed = em.merge(o);
      em.remove(dataset);
    }
    em.getTransaction().commit();

  }

  /**
   * Jpa get all datalets.
   *
   * @return the list
   */
  public List<Datalet> jpaGetAllDatalets() {
    TypedQuery<Datalet> q = em.createQuery("SELECT d FROM Datalet d", Datalet.class);
    return q.getResultList();
  }

  /**
   * Jpa get datalet by distribution ID.
   *
   * @param distributionId the distribution ID
   * @return the list
   */
  public List<Datalet> jpaGetDataletByDistributionId(String distributionId) {
    TypedQuery<Datalet> q = em.createQuery(
        "SELECT d FROM Datalet d where d.distributionID='" + distributionId + "'", Datalet.class);
    return q.getResultList();
  }

  /**
   * Jpa get datalet by triple ID.
   *
   * @param nodeId         the node ID
   * @param datasetId      the dataset ID
   * @param distributionId the distribution ID
   * @return the list
   */
  public List<Datalet> jpaGetDataletByTripleId(String nodeId, String datasetId,
      String distributionId) {
    TypedQuery<Datalet> q = em.createQuery("SELECT d FROM Datalet d where d.nodeID='" + nodeId
        + "' and d.distributionID='" + distributionId + "' and d.datasetID='" + datasetId + "'",
        Datalet.class);
    return q.getResultList();
  }

  /**
   * Jpa get datalet by I ds.
   *
   * @param nodeId         the node ID
   * @param datasetId      the dataset ID
   * @param distributionId the distribution ID
   * @param dataletId      the datalet ID
   * @return the datalet
   */
  public Datalet jpaGetDataletByIds(String nodeId, String datasetId, String distributionId,
      String dataletId) {
    TypedQuery<Datalet> q = em.createQuery(
        "SELECT d FROM Datalet d where d.nodeID='" + nodeId + "' and d.distributionID='"
            + distributionId + "' and d.datasetID='" + datasetId + "' and d.id='" + dataletId + "'",
        Datalet.class);
    if (q.getResultList().isEmpty()) {
      return null;
    } else {
      return q.getResultList().get(0);
    }
  }

  /**
   * Jpa delete datalet.
   *
   * @param datalet the datalet
   */
  public void jpaDeleteDatalet(Datalet datalet) {
    em.getTransaction().begin();
    em.remove(datalet);
    em.getTransaction().commit();
  }

  /**
   * Jpa delete datalet by dstribution ID.
   *
   * @param distributionId the distribution ID
   */
  public void jpaDeleteDataletByDstributionId(String distributionId) {
    em.getTransaction().begin();
    em.createQuery("DELETE FROM Datalet where distributionID = " + distributionId).executeUpdate();
    em.getTransaction().commit();
  }

  /**
   * Jpa persist and commit datalet.
   *
   * @param obj the obj
   * @throws EntityExistsException the entity exists exception
   */
  public void jpaPersistAndCommitDatalet(Datalet obj) throws EntityExistsException {
    // logger.info(obj.getId());
    try {
      em.getTransaction().begin();
      em.persist(obj);
      em.getTransaction().commit();
    } catch (IllegalStateException e) {
      obj.toString();
    }
    // em.clear();
  }

  /**
   * Jpa merge and commit datalet.
   *
   * @param obj the obj
   * @throws EntityExistsException the entity exists exception
   */
  public void jpaMergeAndCommitDatalet(Datalet obj) throws EntityExistsException {
    // logger.info(obj.getId());
    try {
      em.getTransaction().begin();
      em.merge(obj);
      em.getTransaction().commit();
    } catch (IllegalStateException e) {
      obj.toString();
    }
    // em.clear();
  }

  /**
   * Jpa delete datasets by ODMS node.
   *
   * @param nodeId the node ID
   */
  public void jpaDeleteDatasetsByOdmsNode(int nodeId) {
    Query q;
    logger.info("HIBERNATE: Delete Transaction BEGIN");

    em.getTransaction().begin();

    q = em.createNativeQuery("DELETE FROM dcat_keyword where nodeID= " + nodeId);
    q.executeUpdate();

    q = em.createNativeQuery("DELETE FROM dcat_documentation where nodeID= " + nodeId);
    q.executeUpdate();

    q = em.createNativeQuery("DELETE FROM dcat_hasVersion where nodeID= " + nodeId);
    q.executeUpdate();

    q = em.createNativeQuery("DELETE FROM dcat_isVersionOf where nodeID= " + nodeId);
    q.executeUpdate();

    q = em.createNativeQuery("DELETE FROM dcat_provenance where nodeID= " + nodeId);
    q.executeUpdate();

    q = em.createNativeQuery("DELETE FROM dcat_sample where nodeID= " + nodeId);
    q.executeUpdate();

    q = em.createNativeQuery("DELETE FROM dcat_source where nodeID= " + nodeId);
    q.executeUpdate();

    q = em.createNativeQuery("DELETE FROM dcat_otherIdentifier where nodeID= " + nodeId);
    q.executeUpdate();

    q = em.createNativeQuery("DELETE FROM dcat_versionNotes where nodeID= " + nodeId);
    q.executeUpdate();

    q = em.createNativeQuery("DELETE FROM dcat_language where nodeID= " + nodeId);
    q.executeUpdate();

    q = em.createNativeQuery("DELETE FROM dcat_versionNotes where nodeID= " + nodeId);
    q.executeUpdate();

    q = em.createNativeQuery("DELETE FROM dcat_versionNotes where nodeID= " + nodeId);
    q.executeUpdate();

    q = em.createNativeQuery("DELETE FROM dcat_relatedResource where nodeID= " + nodeId);
    q.executeUpdate();

    q = em.createNativeQuery(
        "DELETE FROM dcat_standard_referencedocumentation " + "where nodeID= " + nodeId);
    q.executeUpdate();

    q = em.createNativeQuery(
        "DELETE FROM dcat_concept where " + "nodeID= " + nodeId + " and dataset_id is not null ");
    q.executeUpdate();

    q = em.createNativeQuery("DELETE FROM dcat_concept_prefLabel where nodeID= " + nodeId);
    q.executeUpdate();

    q = em.createQuery("DELETE FROM DctStandard where nodeID= " + nodeId);
    q.executeUpdate();

    q = em.createQuery("DELETE FROM VCardOrganization where nodeID = " + nodeId);
    q.executeUpdate();

    q = em.createNativeQuery("DELETE FROM dcat_distribution_documentation where nodeID= " + nodeId);
    q.executeUpdate();

    q = em.createNativeQuery("DELETE FROM dcat_distribution_language where nodeID= " + nodeId);
    q.executeUpdate();

    q = em.createQuery("DELETE FROM Datalet where nodeID = " + nodeId);
    q.executeUpdate();

    q = em.createQuery("DELETE FROM DcatDistribution where nodeID = " + nodeId);
    q.executeUpdate();

    q = em.createNativeQuery("DELETE FROM dcat_checksum where nodeID= " + nodeId);
    q.executeUpdate();

    q = em.createQuery("DELETE FROM DcatDataset where nodeID = " + nodeId);
    q.executeUpdate();

    q = em.createQuery("DELETE FROM DctLicenseDocument where nodeID= " + nodeId);
    q.executeUpdate();

    q = em.createQuery("DELETE FROM DctLocation where nodeID= " + nodeId);
    q.executeUpdate();

    q = em.createQuery("DELETE FROM DctPeriodOfTime where nodeID= " + nodeId);
    q.executeUpdate();

    q = em.createQuery("DELETE FROM FoafAgent where nodeID = " + nodeId);
    q.executeUpdate();

    q = em.createNativeQuery(
        "DELETE FROM dcat_concept where " + "nodeID= " + nodeId + " and dataset_id is null ");
    q.executeUpdate();

    q = em.createQuery("DELETE FROM OrionDistributionConfig where nodeID= " + nodeId);
    q.executeUpdate();

    em.getTransaction().commit();

    logger.info("HIBERNATE: Delete Transaction COMMIT");
  }

  /**
   * Jpa get orion distribution config.
   *
   * @param id the id
   * @return the orion distribution config
   */
  public OrionDistributionConfig jpaGetOrionDistributionConfig(String id) {
    TypedQuery<OrionDistributionConfig> q = em.createQuery(
        "SELECT d FROM OrionDistributionConfig d " + "where id='" + id + "'",
        OrionDistributionConfig.class);
    if (q.getResultList().isEmpty()) {
      return null;
    } else {
      return q.getResultList().get(0);
    }
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

}
