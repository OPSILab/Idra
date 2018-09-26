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
package it.eng.idra.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;

import org.apache.logging.log4j.*;
import org.apache.logging.log4j.core.impl.ThrowableFormatOptions;

import it.eng.idra.beans.Datalet;
import it.eng.idra.beans.dcat.DCATDataset;
import it.eng.idra.beans.dcat.DCATDatasetId;
import it.eng.idra.beans.dcat.DCATDistribution;
import it.eng.idra.beans.orion.OrionDistributionConfig;

import javax.persistence.EntityExistsException;

public class CachePersistenceManager {

	private static EntityManagerFactory emf;

	private EntityManager em;
	private static Logger logger = LogManager.getLogger(CachePersistenceManager.class);

	static {
		// logger.info("Hibernate EntityManagerFactory init");
		try {
			emf = Persistence.createEntityManagerFactory("org.hibernate.jpa");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CachePersistenceManager() {
		// logger.info("Hibernate init");
		try {
			em = emf.createEntityManager();

		} catch (Exception e) {
			e.printStackTrace();
		}
		// logger.info("Hibernate end");
	}

	public void jpaBeginTransaction() {
		em.getTransaction().begin();
	}

	public EntityTransaction jpaGetTransaction() {
		return em.getTransaction();
	}

	public void jpaRollbackTransaction() {
		if (em.getTransaction().getRollbackOnly())
			em.getTransaction().rollback();
	}

	public void jpaClear() {
		em.clear();
	}

	public void jpaPersistDataset(DCATDataset obj) throws EntityExistsException {
		em.persist(obj);
	}

	public void jpaMergeAndCommitDataset(DCATDataset obj) throws IllegalStateException, RollbackException {

		try {
			if (!em.getTransaction().isActive())
				em.getTransaction().begin();

			em.merge(obj);
			em.getTransaction().commit();

		} catch (Exception e) {
			em.getTransaction().rollback();
			throw e;
		}
	}

	public void jpaMergeAndCommitDistribution(DCATDistribution obj) throws IllegalStateException, RollbackException {

		try {
			if (!em.getTransaction().isActive())
				em.getTransaction().begin();

			em.merge(obj);
			em.getTransaction().commit();

		} catch (Exception e) {
			e.printStackTrace();
			em.getTransaction().rollback();
			throw e;
		}
	}

	public void jpaPersistOrMergeAndCommitDataset(DCATDataset obj) throws IllegalStateException, RollbackException {

		try {
			if (!em.getTransaction().isActive())
				em.getTransaction().begin();

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

	/* Rob_mod 16/10/2016 */
	public void jpaPersistOrMergeAndCommitObject(Object obj) {
		em.getTransaction().begin();
		try {
			em.persist(obj);
		} catch (EntityExistsException e) {
			em.merge(obj);
		}
		em.getTransaction().commit();
	}

	public void jpaRemoveDataset(DCATDataset obj) {
		em.remove(obj);
	}

	public void jpaCommitTransanction() throws IllegalStateException, RollbackException {
		em.getTransaction().commit();
		em.clear();
	}

	public void jpaPersistAndCommitDataset(DCATDataset obj) throws EntityExistsException {
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

	public void jpaPersistDatasets(List<DCATDataset> objectList) {
		try {
			em.getTransaction().begin();
			for (DCATDataset o : objectList) {
				try {
					em.persist(o);
				} catch (EntityExistsException e) {
					logger.info(o.getId() + "threw Exception");
				}

			}
			em.getTransaction().commit();

		} catch (RollbackException e) {
			if (e.getMessage().contains("Duplicate entry"))
				logger.info(e.getMessage() + "\n This dataset will be skipped");
			else {
				// e.printStackTrace();
				logger.info("\n\n\nException " + e.getMessage() + "\n\n\n ");
			}
		}
	}

	public Query jpaQuery(String query) {
		Query q = em.createQuery(query);
		return q;
	}

	public List<DCATDataset> jpaGetDatasets() {
		TypedQuery<DCATDataset> q = em.createQuery("SELECT d FROM DCATDataset d", DCATDataset.class);
		return q.getResultList();
	}

	public List<DCATDataset> jpaGetDatasetsByRegex(HashMap<String, Object> searchParameters) {
		ArrayList<String> nodes = new ArrayList<String>();

		HashMap<String, HashMap<String, String>> filterMap = (HashMap<String, HashMap<String, String>>) searchParameters
				.get("filters");

		nodes.addAll(filterMap.keySet());
		HashMap<String, String> filters = filterMap.get(nodes.get(0));

		String query = "SELECT d FROM DCATDataset d where d.nodeID in (:nodes) and  ( regexp(d.title,'"
				+ filters.get("regex") + "') = true or regexp(d.description,'" + filters.get("regex") + "') = true ) ";

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
		TypedQuery<DCATDataset> q = em.createQuery(query, DCATDataset.class);
		q.setParameter("nodes", nodes);
		// q.setFirstResult(Integer.parseInt((String)
		// searchParameters.get("start")));
		// q.setMaxResults( Integer.parseInt((String)
		// searchParameters.get("rows"))==0 ? 1 : Integer.parseInt((String)
		// searchParameters.get("rows")));

		return q.getResultList();
	}

	public List<DCATDataset> jpaGetDatasetsByLocation(HashMap<String, Object> searchParameters) {
		ArrayList<String> nodes = new ArrayList<String>();

		HashMap<String, HashMap<String, String>> filterMap = (HashMap<String, HashMap<String, String>>) searchParameters
				.get("filters");

		nodes.addAll(filterMap.keySet());
		HashMap<String, String> filters = filterMap.get(nodes.get(0));

		String query = "SELECT d FROM DCATDataset d join DCTLocation l on d.spatialCoverage=l.id where d.nodeID in (:nodes) and l.geometry is not '' and ST_Intersects(ST_GeomFromGeoJSON('"
				+ filters.get("geographic_area") + "'),ST_GeomFromGeoJSON(l.geometry))=true ";

		if (filters.containsKey("regex")) {
			query += "and ( regexp(d.title,'" + filters.get("regex") + "') = true or regexp(d.description,'"
					+ filters.get("regex") + "') = true ) ";
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
		TypedQuery<DCATDataset> q = em.createQuery(query, DCATDataset.class);
		q.setParameter("nodes", nodes);
		// q.setFirstResult(Integer.parseInt((String)
		// searchParameters.get("start")));
		// q.setMaxResults( Integer.parseInt((String)
		// searchParameters.get("rows"))==0 ? 1 : Integer.parseInt((String)
		// searchParameters.get("rows")));

		return q.getResultList();
	}

	public int jpaGetCountDatasetsByRegex(HashMap<String, Object> searchParameters) {
		ArrayList<String> nodes = new ArrayList<String>();

		HashMap<String, HashMap<String, String>> filterMap = (HashMap<String, HashMap<String, String>>) searchParameters
				.get("filters");

		nodes.addAll(filterMap.keySet());
		HashMap<String, String> filters = filterMap.get(nodes.get(0));

		String query = "SELECT d FROM DCATDataset d where d.nodeID in (:nodes) and  ( regexp(d.title,'"
				+ filters.get("regex") + "') = true or regexp(d.description,'" + filters.get("regex") + "') = true ) ";

		String[] sort = ((String) searchParameters.getOrDefault("sort", "id,asc")).split(",");
		if (!sort[0].equals("id") || !sort[0].equals("title")) {
			sort[0] = "d.id";
		}

		if (!sort[1].equals("asc") || !sort[1].equals("desc")) {
			sort[1] = "asc";
		}

		query += " ORDER BY " + sort[0] + " " + sort[1];

		TypedQuery<DCATDataset> q = em.createQuery(query, DCATDataset.class);
		q.setParameter("nodes", nodes);
		// q.setFirstResult(Integer.parseInt((String)
		// searchParameters.get("start")));
		// q.setMaxResults( Integer.parseInt((String)
		// searchParameters.get("rows"))==0 ? 1 : Integer.parseInt((String)
		// searchParameters.get("rows")));

		return q.getResultList().size();
	}

	public int jpaGetCountDatasetsByLocation(HashMap<String, Object> searchParameters) {
		ArrayList<String> nodes = new ArrayList<String>();

		HashMap<String, HashMap<String, String>> filterMap = (HashMap<String, HashMap<String, String>>) searchParameters
				.get("filters");

		nodes.addAll(filterMap.keySet());
		HashMap<String, String> filters = filterMap.get(nodes.get(0));

		// String query = "SELECT d FROM DCATDataset d where d.nodeID in (:nodes) and
		// d.spatialCoverage is not '' and ST_Intersects(ST_GeomFromGeoJSON('"
		// + filters.get("geographic_area") +
		// "'),ST_GeomFromGeoJSON(d.spatialCoverage))=true ";

		String query = "SELECT d FROM DCATDataset d join DCTLocation l on d.spatialCoverage=l.id where d.nodeID in (:nodes) and l.geometry is not '' and ST_Intersects(ST_GeomFromGeoJSON('"
				+ filters.get("geographic_area") + "'),ST_GeomFromGeoJSON(l.geometry))=true ";

		if (filters.containsKey("regex")) {
			query += "and ( regexp(d.title,'" + filters.get("regex") + "') = true or regexp(d.description,'"
					+ filters.get("regex") + "') = true ) ";
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

		TypedQuery<DCATDataset> q = em.createQuery(query, DCATDataset.class);
		q.setParameter("nodes", nodes);
		// q.setFirstResult(Integer.parseInt((String)
		// searchParameters.get("start")));
		// q.setMaxResults( Integer.parseInt((String)
		// searchParameters.get("rows"))==0 ? 1 : Integer.parseInt((String)
		// searchParameters.get("rows")));

		return q.getResultList().size();
	}

	public List<DCATDataset> jpaGetDatasetsByODMSNode(int nodeID) {
		TypedQuery<DCATDataset> q = em.createQuery("SELECT d FROM DCATDataset d where d.nodeID = " + nodeID,
				DCATDataset.class);
		return q.getResultList();
	}

	public DCATDataset jpaGetDatasetsByID(String datasetID) {
		TypedQuery<DCATDataset> q = em.createQuery("SELECT d FROM DCATDataset d where d.id = '" + datasetID + "'",
				DCATDataset.class);
		return q.getResultList().get(0);
	}

	public void jpaUpdate(Object obj) {
		em.merge(obj);

	}

	public void jpaUpdateAndCommit(Object obj) {
		em.getTransaction().begin();
		em.merge(obj);
		em.getTransaction().commit();

	}

	public void jpaUpdateDataset(DCATDataset obj, boolean getTransaction) {
		if (getTransaction)
			em.getTransaction().begin();

		em.merge(obj);

		if (getTransaction)
			em.getTransaction().commit();

	}

	public void jpaUpdateDistribution(DCATDistribution obj, boolean getTransaction) {
		if (getTransaction)
			em.getTransaction().begin();

		em.merge(obj);

		if (getTransaction)
			em.getTransaction().commit();

	}

	public void jpaDeleteDataset(Object obj) {
		DCATDataset matching;
		DCATDataset toDelete = (DCATDataset) obj;
		em.getTransaction().begin();
		matching = (DCATDataset) em.find(DCATDataset.class, new DCATDatasetId(toDelete.getId(), toDelete.getNodeID()));
		em.remove(matching);
		em.getTransaction().commit();
	}

	// public void jpaDeleteDataset(DCATDataset d){
	// Query q;
	// logger.info("HIBERNATE: Delete Transaction BEGIN");
	// em.getTransaction().begin();
	// q= em.createQuery("DELETE FROM DCATDataset where dataset_id='" +
	// d.getId() + "' AND nodeID='" + d.getNodeID() + "'");
	// q.executeUpdate();
	// logger.info("HIBERNATE: Delete Transaction COMMIT");
	// em.getTransaction().commit();
	//
	// }

	public void jpaDeleteDatasets(List<DCATDataset> objList) {
		em.getTransaction().begin();
		logger.info("HIBERNATE: Delete Transaction BEGIN");
		for (DCATDataset o : objList) {
			DCATDataset dataset = (DCATDataset) em.find(DCATDataset.class, new DCATDatasetId(o.getId(), o.getNodeID()));
			// DCATDataset managed = em.merge(o);
			em.remove(dataset);
		}
		em.getTransaction().commit();

	}

	public List<Datalet> jpaGetAllDatalets() {
		TypedQuery<Datalet> q = em.createQuery("SELECT d FROM Datalet d", Datalet.class);
		return q.getResultList();
	}

	public List<Datalet> jpaGetDataletByDistributionID(String distributionID) {
		TypedQuery<Datalet> q = em
				.createQuery("SELECT d FROM Datalet d where d.distributionID='" + distributionID + "'", Datalet.class);
		return q.getResultList();
	}

	public List<Datalet> jpaGetDataletByTripleID(String nodeID, String datasetID, String distributionID) {
		TypedQuery<Datalet> q = em.createQuery("SELECT d FROM Datalet d where d.nodeID='" + nodeID
				+ "' and d.distributionID='" + distributionID + "' and d.datasetID='" + datasetID + "'", Datalet.class);
		return q.getResultList();
	}

	public Datalet jpaGetDataletByIDs(String nodeID, String datasetID, String distributionID, String dataletID) {
		TypedQuery<Datalet> q = em
				.createQuery(
						"SELECT d FROM Datalet d where d.nodeID='" + nodeID + "' and d.distributionID='"
								+ distributionID + "' and d.datasetID='" + datasetID + "' and d.id='" + dataletID + "'",
						Datalet.class);
		if (q.getResultList().isEmpty()) {
			return null;
		} else {
			return q.getResultList().get(0);
		}
	}

	public void jpaDeleteDatalet(Datalet datalet) {
		em.getTransaction().begin();
		em.remove(datalet);
		em.getTransaction().commit();
	}

	public void jpaDeleteDataletByDstributionID(String distributionID) {
		em.getTransaction().begin();
		em.createQuery("DELETE FROM Datalet where distributionID = " + distributionID).executeUpdate();
		em.getTransaction().commit();
	}

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

	public void jpaDeleteDatasetsByODMSNode(int nodeID) {
		Query q;
		logger.info("HIBERNATE: Delete Transaction BEGIN");

		em.getTransaction().begin();

		q = em.createNativeQuery("DELETE FROM dcat_keyword where nodeID= " + nodeID);
		q.executeUpdate();

		q = em.createNativeQuery("DELETE FROM dcat_documentation where nodeID= " + nodeID);
		q.executeUpdate();

		q = em.createNativeQuery("DELETE FROM dcat_hasVersion where nodeID= " + nodeID);
		q.executeUpdate();

		q = em.createNativeQuery("DELETE FROM dcat_isVersionOf where nodeID= " + nodeID);
		q.executeUpdate();

		q = em.createNativeQuery("DELETE FROM dcat_provenance where nodeID= " + nodeID);
		q.executeUpdate();

		q = em.createNativeQuery("DELETE FROM dcat_sample where nodeID= " + nodeID);
		q.executeUpdate();

		q = em.createNativeQuery("DELETE FROM dcat_source where nodeID= " + nodeID);
		q.executeUpdate();

		q = em.createNativeQuery("DELETE FROM dcat_otherIdentifier where nodeID= " + nodeID);
		q.executeUpdate();

		q = em.createNativeQuery("DELETE FROM dcat_versionNotes where nodeID= " + nodeID);
		q.executeUpdate();

		q = em.createNativeQuery("DELETE FROM dcat_language where nodeID= " + nodeID);
		q.executeUpdate();

		q = em.createNativeQuery("DELETE FROM dcat_versionNotes where nodeID= " + nodeID);
		q.executeUpdate();

		q = em.createNativeQuery("DELETE FROM dcat_versionNotes where nodeID= " + nodeID);
		q.executeUpdate();

		q = em.createNativeQuery("DELETE FROM dcat_standard_referencedocumentation where nodeID= " + nodeID);
		q.executeUpdate();

		q = em.createNativeQuery("DELETE FROM dcat_concept where nodeID= " + nodeID + " and dataset_id is not null ");
		q.executeUpdate();

		q = em.createNativeQuery("DELETE FROM dcat_concept_prefLabel where nodeID= " + nodeID);
		q.executeUpdate();

		q = em.createQuery("DELETE FROM DCTStandard where nodeID= " + nodeID);
		q.executeUpdate();

		q = em.createQuery("DELETE FROM VCardOrganization where nodeID = " + nodeID);
		q.executeUpdate();

		q = em.createNativeQuery("DELETE FROM dcat_distribution_documentation where nodeID= " + nodeID);
		q.executeUpdate();

		q = em.createNativeQuery("DELETE FROM dcat_distribution_language where nodeID= " + nodeID);
		q.executeUpdate();

		q = em.createQuery("DELETE FROM Datalet where nodeID = " + nodeID);
		q.executeUpdate();

		q = em.createQuery("DELETE FROM DCATDistribution where nodeID = " + nodeID);
		q.executeUpdate();

		q = em.createNativeQuery("DELETE FROM dcat_checksum where nodeID= " + nodeID);
		q.executeUpdate();

		q = em.createQuery("DELETE FROM DCATDataset where nodeID = " + nodeID);
		q.executeUpdate();

		q = em.createQuery("DELETE FROM DCTLicenseDocument where nodeID= " + nodeID);
		q.executeUpdate();

		q = em.createQuery("DELETE FROM DCTLocation where nodeID= " + nodeID);
		q.executeUpdate();

		q = em.createQuery("DELETE FROM DCTPeriodOfTime where nodeID= " + nodeID);
		q.executeUpdate();

		q = em.createQuery("DELETE FROM FOAFAgent where nodeID = " + nodeID);
		q.executeUpdate();

		q = em.createNativeQuery("DELETE FROM dcat_concept where nodeID= " + nodeID + " and dataset_id is null ");
		q.executeUpdate();
		
		em.getTransaction().commit();

		logger.info("HIBERNATE: Delete Transaction COMMIT");
	}

	
	public OrionDistributionConfig jpaGetOrionDistributionConfig(String id) {
		TypedQuery<OrionDistributionConfig> q = em
				.createQuery(
						"SELECT d FROM OrionDistributionConfig d where id='" + id + "'",
						OrionDistributionConfig.class);
		if (q.getResultList().isEmpty()) {
			return null;
		} else {
			return q.getResultList().get(0);
		}
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

}
