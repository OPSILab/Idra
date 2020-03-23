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
package it.eng.idra.cache;

import it.eng.idra.beans.IdraProperty;
import it.eng.idra.beans.RdfPrefix;
import it.eng.idra.beans.odms.ODMSCatalogue;
import it.eng.idra.beans.search.SparqlResultFormat;
import it.eng.idra.management.FederationCore;
import it.eng.idra.management.RdfPrefixManager;
import it.eng.idra.utils.PropertyManager;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.URI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.URIImpl;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;

import com.google.common.io.Files;

import org.apache.jena.atlas.web.HttpException;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.JenaException;

import org.apache.logging.log4j.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Java class to manage Linked Open Data in the Open Data Federation. This class
 * allows to insert LOD, that is identified in ODMS of federation, on a RDF4J
 * repository. Class also allows to add new RDF datasets in repository, to
 * delete and update existing datasets, and to execute SPARQL queries on
 * datasets.
 * 
 * @author ENG
 * @version 1.0
 **/
public class LODCacheManager {

	private static Logger logger = LogManager.getLogger(LODCacheManager.class);

	public static Repository repo;
	static int test = 0;

	private LODCacheManager() {
	}

	static {
	}

	/**
	 * 
	 * 
	 * @throws RepositoryException
	 */
	private static Repository getRepository() throws RepositoryException {
		return getRepository(getRepositoryUrl());
	}

	/**
	 * 
	 * @param repoURL
	 *            url of RDF4J repository
	 *            (ex.http://localhost:8080/rdf4j-workbench/repositories/ODF/)
	 * @return repo
	 * @throws RepositoryException
	 */
	private static Repository getRepository(String repoURL) throws RepositoryException {
		if (repo == null) {
			repo = new HTTPRepository(repoURL);

			repo.initialize();
		}
		return repo;
	}

	/**
	 * Public method to add a new RDF dataset into RDF4J repository.
	 * 
	 * @param link
	 *            url of dataset
	 * @throws RepositoryException
	 * @throws IOException
	 * @throws RDFParseException
	 */
	public static int addRDF(String link) throws RepositoryException, IOException {

		int rdfAdded = 0;
		Resource resource = null;
		RepositoryConnection repoConnection = null;

		if (checkContentLength(link) && test < 2) {

			try {
				URL url = new URL(link);

				URLConnection conn = url.openConnection();
				conn.setConnectTimeout(60000);
				conn.setReadTimeout(60000);

				InputStream original = conn.getInputStream();
				byte[] byteArray = IOUtils.toByteArray(original);
				InputStream rdfStream = new ByteArrayInputStream(byteArray);
				InputStream rdfStream1 = new ByteArrayInputStream(byteArray);

				// resource = new URIImpl(link);
				repoConnection = getRepository().getConnection();

				try {
					// repoConnection.add(rdfStream, link, RDFFormat.RDFXML,
					// resource);
					ValueFactory f = repoConnection.getValueFactory();
					IRI context = f.createIRI(link);
					repoConnection.add(rdfStream, link, RDFFormat.RDFXML, context);

					getPrefixes(rdfStream1);
					rdfAdded++;
					logger.info("RDF file: " + link + " loading completed successfully!");

//					test++;
					// original.close();
					// rdfStream.close();
					// rdfStream1.close();

				} catch (RDFParseException | RepositoryException | JenaException | HttpException e) {
					e.printStackTrace();
					logger.error("Exception while adding RDF: " + link + " " + e.getMessage());

				} finally {
					original.close();
					rdfStream.close();
					rdfStream1.close();
				}

			} catch (IOException x) {
				logger.error(link + " " + x.getMessage());
			} finally {
				repoConnection.close();
			}
		}
		return rdfAdded;
	}

	/**
	 * Public method to add a Catalogue's dump file into RDF4J repository.
	 * 
	 * @param node the Catalogue
	 * @param file the byte representation of its dump
	 */
	public static void addCatalogueDump(ODMSCatalogue node,byte[] file) {
		RepositoryConnection repoConnection = getRepository().getConnection();
		try {
			InputStream rdfStream = new ByteArrayInputStream(file);
			InputStream rdfStream1 = new ByteArrayInputStream(file);
			
			ValueFactory f = repoConnection.getValueFactory();
			IRI context = f.createIRI(node.getHost());
			repoConnection.add(rdfStream, node.getHost(), RDFFormat.RDFXML, context);

			getPrefixes(rdfStream1);
			logger.info("RDF file: " + node.getHost() + " loading completed successfully!");
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			repoConnection.close();
		}
	}
	
	
	/**
	 * Public method to add a list of new RDF datasets into RDF4J repository.
	 * 
	 * @param link
	 *            url of dataset
	 * @throws RepositoryException
	 * @throws IOException
	 * @throws RDFParseException
	 */
	public static int addRDFList(List<String> links) throws RepositoryException, IOException {

		int rdfAdded = 0;
		Resource resource = null;
		RepositoryConnection repoConnection = null;

		for (String link : links) {
			if (checkContentLength(link) && test < 2) {

				try {

					URL url = new URL(link);

					URLConnection conn = url.openConnection();
					conn.setConnectTimeout(60000);
					conn.setReadTimeout(60000);

					InputStream original = conn.getInputStream();
					byte[] byteArray = IOUtils.toByteArray(original);
					InputStream rdfStream = new ByteArrayInputStream(byteArray);
					InputStream rdfStream1 = new ByteArrayInputStream(byteArray);

					// resource = new URIImpl(link);
					repoConnection = getRepository().getConnection();

					try {
						// repoConnection.add(rdfStream, link, RDFFormat.RDFXML,
						// resource);
						ValueFactory f = repoConnection.getValueFactory();
						IRI context = f.createIRI(link);
						repoConnection.add(rdfStream, link, RDFFormat.RDFXML, context);

						getPrefixes(rdfStream1);
						rdfAdded++;
						logger.info("RDF file: " + link + " loading completed successfully!");

						test++;
						// original.close();
						// rdfStream.close();
						// rdfStream1.close();

					} catch (RDFParseException | RepositoryException | JenaException | HttpException e) {
						e.printStackTrace();
						logger.error("Exception while adding RDF: " + link + " " + e.getMessage());

					} finally {
						original.close();
						rdfStream.close();
						rdfStream1.close();
					}

				} catch (IOException e) {
					logger.error("Exception while adding RDF: " + link + " " + e.getMessage());

				} finally {
					repoConnection.close();
				}
			}

		}

		return rdfAdded;
	}

	/**
	 * Method private to get RDF prefixes dinamically. This method calls
	 * getNSPrefixMap() of Model class, that returns a list of namespaces. These
	 * namespaces are then converted in RDF prefixes format. Prefixes are stored
	 * into properties file.
	 * 
	 * @param link
	 *            String url of RDF dataset.
	 */

	private static void getPrefixes(InputStream rdfStream) throws JenaException, HttpException {
		try {

			Model model = ModelFactory.createDefaultModel();
			model.read(rdfStream, "");
			Map<String, String> prefixes = model.getNsPrefixMap();

			Set<String> set = prefixes.keySet();
			Iterator<String> it = set.iterator();
			String key = new String();
			// ArrayList<String> prefixList= new ArrayList();
			while (it.hasNext()) {
				key = (String) it.next();
				try {
					// if(!key.equals(""))
					RdfPrefixManager.addPrefix(new RdfPrefix(key, "<" + prefixes.get(key) + ">"));
				} catch (SQLException e) {
					// logger.info(key +" here");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Private method to extract RDF prefixes that are associated to datasets. List
	 * of prefixes is static.
	 * 
	 * @param querylanguage
	 *            can be SPARQL or SERQL. If querylanguage is sparql, out will
	 *            contain prefixes. If querylanguage is SERQL, out will contain
	 *            namespaces.
	 * @return out String that contains all prefixes.
	 */

	/**
	 * Private method to add prefixes to the query.
	 * 
	 * @param querylanguage
	 *            can be SPARQL or SERQL.
	 * @param queryString
	 *            String that contains query.
	 * @return out String that contains prefixes and query.
	 */

	/**
	 * Method to update a RDF dataset if this is modified.
	 * 
	 * @param link
	 *            String url of RDF dataset.
	 * @throws IOException
	 * @throws RDFParseException
	 * @throws RepositoryException
	 * @throws SQLException
	 */
	public static int updateRDF(String link) throws RepositoryException, IOException {

		LODCacheManager.deleteRDF(link);
		return LODCacheManager.addRDF(link);

	}

	/**
	 * Method to delete RDF dataset.
	 * 
	 * @param context
	 *            String. The name of the dataset context in the repository.
	 * @throws RepositoryException
	 */
	public static void deleteRDF(String context) throws RepositoryException {

		// Resource resource = new URIImpl(context);

		RepositoryConnection repoConnection = null;
		try {
			repoConnection = getRepository().getConnection();
			ValueFactory f = repoConnection.getValueFactory();
			IRI contextIRI = f.createIRI(context);
			repoConnection.clear(contextIRI);
		} catch (RepositoryException e) {
			throw e;
		} finally {
			repoConnection.close();
		}
		logger.info("Deleted Context:" + context);
	}

	/**
	 * Method to execute SPARQL queries. A query is created from a string using
	 * QueryFactory. The query and model or RDF dataset to be queried are then
	 * passed to QueryExecutionFactory to produce an instance of a query execution.
	 * Result of query can be formatted in XML/RDF or JSON, according to the user's
	 * choice.
	 * 
	 * @param query
	 *            String
	 * @param formatType
	 *            String format of query result.
	 */
	public static String runQuery(String query, SparqlResultFormat formatType) {

		logger.info("Running SPARQL query");
		// query=addPrefixes(QueryLanguage.SPARQL,query);
		final Query queryS = QueryFactory.create(query);
		final QueryExecution exec = QueryExecutionFactory.createServiceRequest(getRepositoryUrl(), queryS);
		final ResultSet resultSet = exec.execSelect();
		// logger.info(resultSet.getRowNumber());
		String resultsAsString = "";
		switch (formatType.toString()) {
		case "XML":
		case "RDF":
			resultsAsString = ResultSetFormatter.asXMLString(resultSet);
			// logger.info(resultsAsString);
			logger.info("XML result of SPARQL query");
			break;
		case "JSON":

			ByteArrayOutputStream outputWr = new ByteArrayOutputStream();
			ResultSetFormatter.outputAsJSON(outputWr, resultSet);
			logger.info("JSON result of SPARQL query");
			resultsAsString = outputWr.toString();
			// logger.info(resultsAsString);
			break;
		default:
			// logger.info("Error: specified format is invalid");
			logger.error("Error: specified format is invalid");
			break;
		}
		exec.close();
		return resultsAsString;
	}

	private static String getRepositoryUrl() {
		String repoUrl = "";
		repoUrl = PropertyManager.getProperty(IdraProperty.SESAME_SERVER_URI).trim();
		String repoName = PropertyManager.getProperty(IdraProperty.SESAME_REPO_NAME).trim();
		if (!repoUrl.endsWith("/") && !repoName.startsWith("/")) {
			repoUrl += "/";
		}
		repoUrl += repoName;
		return repoUrl;
	}

	public static boolean checkContentLength(String link) throws IOException {

		if (Boolean.parseBoolean(FederationCore.getSettings().get("rdf_undefined_content_length"))) {
			URL url;
			try {

				url = new URL(link);

				HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
				httpcon.setRequestMethod("HEAD");

				if (httpcon
						.getContentLength() >= Integer.parseInt(FederationCore.getSettings().get("rdf_max_dimension"))
								* 1000000
						&& Boolean.parseBoolean(FederationCore.getSettings().get("rdf_undefined_dimension"))) {
					// logger.info("RDF " + link + " dimension exceeds the
					// limit");
					logger.error("RDF " + link + " dimension exceeds the limit");
					httpcon.disconnect();
					return false;
				} else if (httpcon.getContentLength() <= 0) {
					// logger.info("RDF " + link + " content length
					// unspecified");
					logger.error("RDF " + link + " content length unspecified");
					return false;
				} else if (!link.endsWith(".rdf")) {
					// logger.info("RDF " + link + " invalid extension");
					logger.error("RDF " + link + " invalid extension");
					return false;
				}

				httpcon.disconnect();
				return true;

			} catch (ClassCastException e) {
				logger.error(link + " " + e.getMessage());
				return false;
			}

		} else
			return true;
	}

//	public static void main(String[] args) {
//
//		try {
//			System.out.println("Adding RDF TEST");
//			addRDF("http://www.territorio.provincia.tn.it/geodati/812_Aereoporti_ed_interporti_della_PAT__pup__12_12_2011.rdf");
//			System.out.println("SLEEP 5 sec");
//			Thread.sleep(5000);
//			System.out.println("Deleting RDF TEST");
//			deleteRDF(
//					"http://www.territorio.provincia.tn.it/geodati/812_Aereoporti_ed_interporti_della_PAT__pup__12_12_2011.rdf");
//		} catch (RepositoryException | IOException | InterruptedException e) {
//			e.printStackTrace();
//		}
//
//	}

}
