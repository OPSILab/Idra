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
package it.eng.idra.api;

import it.eng.idra.beans.Datalet;
import it.eng.idra.beans.ErrorResponse;
import it.eng.idra.beans.EuroVocLanguage;
import it.eng.idra.beans.ODFProperty;
import it.eng.idra.beans.OrderBy;
import it.eng.idra.beans.OrderType;
import it.eng.idra.beans.dcat.DCATAPFormat;
import it.eng.idra.beans.dcat.DCATAPProfile;
import it.eng.idra.beans.dcat.DCATAPWriteType;
import it.eng.idra.beans.dcat.DCATDataset;
import it.eng.idra.beans.dcat.DCATDistribution;
import it.eng.idra.beans.exception.DatasetNotFoundException;
import it.eng.idra.beans.exception.EuroVocTranslationNotFoundException;
import it.eng.idra.beans.odms.ODMSCatalogue;
import it.eng.idra.beans.odms.ODMSCatalogueNotFoundException;
import it.eng.idra.beans.odms.ODMSCatalogueType;
import it.eng.idra.beans.odms.ODMSManagerException;
import it.eng.idra.beans.odms.ODMSSynchLock;
import it.eng.idra.beans.orion.OrionCatalogueConfiguration;
import it.eng.idra.beans.orion.OrionDistributionConfig;
import it.eng.idra.beans.search.SearchDateFilter;
import it.eng.idra.beans.search.SearchEuroVocFilter;
import it.eng.idra.beans.search.SearchFilter;
import it.eng.idra.beans.search.SearchRequest;
import it.eng.idra.beans.search.SearchResult;
import it.eng.idra.beans.search.SparqlSearchRequest;
import it.eng.idra.cache.CachePersistenceManager;
import it.eng.idra.cache.MetadataCacheManager;
import it.eng.idra.dcat.dump.DCATAPSerializer;
import it.eng.idra.management.FederationCore;
import it.eng.idra.management.StatisticsManager;
import it.eng.idra.search.FederatedSearch;
import it.eng.idra.search.SPARQLFederatedSearch;
import it.eng.idra.utils.CommonUtil;
import it.eng.idra.utils.GsonUtil;
import it.eng.idra.utils.GsonUtilException;
import it.eng.idra.utils.PropertyManager;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.query.QueryParseException;
import org.json.JSONArray;
import org.json.JSONObject;

import org.apache.logging.log4j.*;
import org.apache.solr.client.solrj.SolrServerException;
import org.glassfish.jersey.client.ClientProperties;

@Path("/client")
public class ClientAPI {

	private static Logger logger = LogManager.getLogger(ClientAPI.class);
	private static Client client;

	@POST
	@Path("/search")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, "application/n-triples", "application/rdf+xml", "text/turtle", "text/n3" })
	public Response searchDataset(@Context HttpServletRequest httpRequest, final String input) {

		SearchRequest request = null;
		Boolean liveSearch = null;
		SearchEuroVocFilter eurovocFilter = null;

		SearchDateFilter issued = null;
		SearchDateFilter modified = null;

		try {

			request = GsonUtil.json2Obj(input, GsonUtil.searchRequestType);

			// Gets the source IP address from HTTPRequest
			String ipAddress = null;
			if (httpRequest.getHeader("X-FORWARDED-FOR") == null)
				ipAddress = httpRequest.getRemoteAddr();

			// If the filters list is not empty, start to build the parameters
			// HashMap, to be passed to FederatesSearch

			List<SearchFilter> filters = request.getFilters();
			if (!filters.isEmpty()) {
				HashMap<String, Object> searchParameters = new HashMap<String, Object>();

				liveSearch = request.isLive();
				eurovocFilter = request.getEuroVocFilter();
				// Add live/cache, euroVoc flags
				searchParameters.put("live", liveSearch);

				// Flag for Multilingual EuroVoc search
				searchParameters.put("euroVoc", eurovocFilter.isEuroVoc());

				if (eurovocFilter.isEuroVoc()) {
					if (eurovocFilter.getSourceLanguage() != null)
						searchParameters.put("sourceLanguage", eurovocFilter.getSourceLanguage().name());
					if (eurovocFilter.getTargetLanguages() != null)
						searchParameters.put("targetLanguages", eurovocFilter.getTargetLanguages().stream()
								.map(EuroVocLanguage::name).collect(Collectors.joining(",")));
				}

				// Handles issued and modified parameters in input
				if ((issued = request.getReleaseDate()) != null) {
					String issuedArray[] = new String[2];
					issuedArray[0] = CommonUtil.formatDate(issued.getStart());
					issuedArray[1] = CommonUtil.formatDate(issued.getEnd());
					searchParameters.put("releaseDate", issuedArray);
				}
				if ((modified = request.getUpdateDate()) != null) {
					String modifiedArray[] = new String[2];
					modifiedArray[0] = CommonUtil.formatDate(modified.getStart());
					modifiedArray[1] = CommonUtil.formatDate(modified.getEnd());
					searchParameters.put("updateDate", modifiedArray);
				}

				// Adds filters parameters in input to the search HashMap
				filters.stream().forEach(filter -> searchParameters.put(filter.getField(), (Object) filter.getValue()));

				// Adds rows, start, sort parameters
				searchParameters.put("rows", request.getRows());
				searchParameters.put("start", request.getStart());
				searchParameters.put("sort",
						request.getSort().getField().trim() + "," + request.getSort().getMode().toString().trim());

				if(searchParameters.containsKey("datasetThemes")) {					
					List<String> tmp = Arrays.asList(((String) searchParameters.remove("datasetThemes")).split(","))
							.stream().distinct().collect(Collectors.toList());
					List<String> themeAbbr = tmp.stream().filter(x-> FederationCore.isDcatTheme(x))
//							.map(x -> FederationCore.getDCATThemesIdentifier(x))
							.collect(Collectors.toList());
					if(!themeAbbr.isEmpty()) {
						searchParameters.put("datasetThemes", String.join(",", themeAbbr));
					}else {
						searchParameters.put("datasetThemes", Arrays.asList());
					}
				}
				
				// Adds the id of the nodes to search on
				List<Integer> ids = new ArrayList<Integer>();
				if (searchParameters.containsKey("catalogues")) {
					
					List<String> catalogues = Arrays.asList(((String) searchParameters.remove("catalogues")).split(","))
							.stream().distinct().collect(Collectors.toList());
					
					ids = catalogues.stream().map(x -> FederationCore.getODMSCatalogueIDbyName(x)).distinct()
							.collect(Collectors.toList());					
				} else {
					
					ids = request.getNodes();
				}
				
				//Search only on active nodes
				ids = FederationCore.getActiveODMSCataloguesID(ids);
				searchParameters.put("nodes", ids);
//				if(ids.size()!=0) {
					//Search only on active nodes
//					ids = FederationCore.getActiveNodesID(ids);
//					searchParameters.put("nodes", ids);
//				}
				
				logger.info("Searching" + (liveSearch ? "live " : " in cache ")
						+ (eurovocFilter.isEuroVoc() ? "with EuroVoc search" : ""));
				logger.info("Search on nodes: " + request.getNodes());
				logger.info("Filters: " + searchParameters.toString());
				logger.info("Sort :" + request.getSort());
				logger.info("Rows :" + request.getRows());
				logger.info("Start :" + request.getStart());

				// Call FederatedSearch method in order to perform the actual
				// search
				SearchResult result = FederatedSearch.search(searchParameters);

				// Adds search statistics
				StatisticsManager.searchStatistics(ipAddress, liveSearch ? "live" : "cache");

				try {
					return Response.status(Response.Status.OK)
							.entity(GsonUtil.obj2Json(result, GsonUtil.searchResultType)).build();
				} catch (GsonUtilException e) {
					return handleErrorResponse500(e);
				}

			} else {
				return handleBadRequestErrorResponse(
						new Exception("The filters array must contain at least one element"));
			}

		} catch (GsonUtilException e) {
			return handleBadRequestErrorResponse(e);

		} catch (EuroVocTranslationNotFoundException e) {
			return handleEuroVocTranslationNotFoundErrorResponse(e);
		} catch (Exception e) {
			return handleErrorResponse500(e);
		}

	}

	@POST
	@Path("/search/dcat-ap")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, "application/n-triples", "application/rdf+xml", "text/turtle", "text/n3" })

	public Response searchDatasetDCATAP(@Context HttpServletRequest httpRequest, final String input,
			@DefaultValue("RDFXML") @QueryParam("format") DCATAPFormat format,
			@DefaultValue("DCATAP") @QueryParam("profile") DCATAPProfile profile) {

		SearchRequest request = null;
		Boolean liveSearch = null;
		SearchEuroVocFilter eurovocFilter = null;

		SearchDateFilter issued = null;
		SearchDateFilter modified = null;

		try {

			request = GsonUtil.json2Obj(input, GsonUtil.searchRequestType);

			// Gets the source IP address from HTTPRequest
			String ipAddress = null;
			if (httpRequest.getHeader("X-FORWARDED-FOR") == null)
				ipAddress = httpRequest.getRemoteAddr();

			// If the filters list is not empty, start to build the parameters
			// HashMap, to be passed to FederatesSearch

			List<SearchFilter> filters = request.getFilters();
			if (!filters.isEmpty()) {
				HashMap<String, Object> searchParameters = new HashMap<String, Object>();

				liveSearch = request.isLive();
				eurovocFilter = request.getEuroVocFilter();
				// Add live/cache, euroVoc flags
				searchParameters.put("live", liveSearch);

				// Flag for Multilingual EuroVoc search
				searchParameters.put("euroVoc", eurovocFilter.isEuroVoc());

				if (eurovocFilter.isEuroVoc()) {
					if (eurovocFilter.getSourceLanguage() != null)
						searchParameters.put("sourceLanguage", eurovocFilter.getSourceLanguage().name());
					if (eurovocFilter.getTargetLanguages() != null)
						searchParameters.put("targetLanguages", eurovocFilter.getTargetLanguages().stream()
								.map(EuroVocLanguage::name).collect(Collectors.joining(",")));
				}

				// Handles issued and modified parameters in input
				if ((issued = request.getReleaseDate()) != null) {
					String issuedArray[] = new String[2];
					issuedArray[0] = CommonUtil.formatDate(issued.getStart());
					issuedArray[1] = CommonUtil.formatDate(issued.getEnd());
					searchParameters.put("releaseDate", issuedArray);
				}
				if ((modified = request.getUpdateDate()) != null) {
					String modifiedArray[] = new String[2];
					modifiedArray[0] = CommonUtil.formatDate(modified.getStart());
					modifiedArray[1] = CommonUtil.formatDate(modified.getEnd());
					searchParameters.put("updateDate", modifiedArray);
				}

				// Adds filters parameters in input to the search HashMap
				filters.stream().forEach(filter -> searchParameters.put(filter.getField(), (Object) filter.getValue()));

				// Adds rows, start, sort parameters
				searchParameters.put("rows", request.getRows());
				searchParameters.put("start", request.getStart());
				searchParameters.put("sort",
						request.getSort().getField().trim() + "," + request.getSort().getMode().toString().trim());

				// Adds the id of the nodes to search on
				List<Integer> ids = new ArrayList<Integer>();
				if (searchParameters.containsKey("catalogues")) {
					
					List<String> catalogues = Arrays.asList(((String) searchParameters.remove("catalogues")).split(","))
							.stream().distinct().collect(Collectors.toList());
					
					ids = catalogues.stream().map(x -> FederationCore.getODMSCatalogueIDbyName(x)).distinct()
							.collect(Collectors.toList());					
				} else {
					
					ids = request.getNodes();
				}
				
				//Search only on active nodes
				ids = FederationCore.getActiveODMSCataloguesID(ids);
				searchParameters.put("nodes", ids);


				logger.info("Searching" + (liveSearch ? "live " : " in cache ")
						+ (eurovocFilter.isEuroVoc() ? "with EuroVoc search" : ""));
				logger.info("Search on nodes: " + request.getNodes());
				logger.info("Filters: " + searchParameters.toString());
				logger.info("Sort: " + request.getSort());
				logger.info("Rows: " + request.getRows());
				logger.info("Start: " + request.getStart());

				// Call FederatedSearch method in order to perform the actual
				// search
				SearchResult result = FederatedSearch.search(searchParameters);
				String dcatResult = DCATAPSerializer.searchResultToDCATAP(result, format,
						profile != null ? profile : DCATAPProfile.DCATAP, DCATAPWriteType.STRING);
				// Adds search statistics
				StatisticsManager.searchStatistics(ipAddress, liveSearch ? "live" : "cache");

				// try {
				return Response.status(Response.Status.OK).type(format.mediaType()).entity(dcatResult).build();
				// } catch (GsonUtilException e) {
				// return handleErrorResponse500(e);
				// }

			} else {
				return handleBadRequestErrorResponse(
						new Exception("The filters array must contain at least one element"));
			}

		} catch (GsonUtilException e) {
			return handleBadRequestErrorResponse(e);

		} catch (EuroVocTranslationNotFoundException e) {
			return handleEuroVocTranslationNotFoundErrorResponse(e);
		} catch (Exception e) {
			return handleErrorResponse500(e);
		}

	}

	@POST
	@Path("/countDataset")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces("application/json")
	public Response countDataset(@Context HttpServletRequest httpRequest, final String input) {

		SearchRequest request = null;
		Boolean liveSearch = null;
		SearchEuroVocFilter eurovocFilter = null;

		SearchDateFilter issued = null;
		SearchDateFilter modified = null;

		try {

			request = GsonUtil.json2Obj(input, GsonUtil.searchRequestType);

			// Gets the source IP address from HTTPRequest
			String ipAddress = null;
			if (httpRequest.getHeader("X-FORWARDED-FOR") == null)
				ipAddress = httpRequest.getRemoteAddr();

			// If the filters list is not empty, start to build the parameters
			// HashMap, to be passed to FederatesSearch

			List<SearchFilter> filters = request.getFilters();
			if (!filters.isEmpty()) {
				HashMap<String, Object> searchParameters = new HashMap<String, Object>();

				liveSearch = request.isLive();

				// Add live/cache
				searchParameters.put("live", liveSearch);

				// Flag for Multilingual EuroVoc search
				eurovocFilter = request.getEuroVocFilter();

				searchParameters.put("euroVoc", eurovocFilter.isEuroVoc());

				if (eurovocFilter.isEuroVoc()) {
					if (eurovocFilter.getSourceLanguage() != null)
						searchParameters.put("sourceLanguage", eurovocFilter.getSourceLanguage().name());
					if (eurovocFilter.getTargetLanguages() != null)
						searchParameters.put("targetLanguages", eurovocFilter.getTargetLanguages().stream()
								.map(EuroVocLanguage::name).collect(Collectors.joining(",")));
				}

				// Handles issued and modified parameters in input
				if ((issued = request.getReleaseDate()) != null) {
					String issuedArray[] = new String[2];
					issuedArray[0] = CommonUtil.formatDate(issued.getStart());
					issuedArray[1] = CommonUtil.formatDate(issued.getEnd());
					searchParameters.put("releaseDate", issuedArray);
				}
				if ((modified = request.getUpdateDate()) != null) {
					String modifiedArray[] = new String[2];
					modifiedArray[0] = CommonUtil.formatDate(modified.getStart());
					modifiedArray[1] = CommonUtil.formatDate(modified.getEnd());
					searchParameters.put("updateDate", modifiedArray);
				}

				// Adds filters parameters in input to the search HashMap
				filters.stream().forEach(filter -> searchParameters.put(filter.getField(), (Object) filter.getValue()));

				// if ((!j.getString("filter").equals("rows") ||
				// j.getString("filter").equals("start")))
				// searchParameters.put(j.getString("filter"), (Object)
				// j.getString("text"));

				// Adds rows, start, sort parameters
				searchParameters.put("rows", request.getRows());
				searchParameters.put("start", request.getStart());
				searchParameters.put("sort",
						request.getSort().getField().trim() + "," + request.getSort().getMode().toString().trim());

				// Adds the id of the nodes to search on
				List<Integer> ids = new ArrayList<Integer>();
				if (searchParameters.containsKey("catalogues")) {
					
					List<String> catalogues = Arrays.asList(((String) searchParameters.remove("catalogues")).split(","))
							.stream().distinct().collect(Collectors.toList());
					
					ids = catalogues.stream().map(x -> FederationCore.getODMSCatalogueIDbyName(x)).distinct()
							.collect(Collectors.toList());					
				} else {
					
					ids = request.getNodes();
				}
				
				//Search only on active nodes
				ids = FederationCore.getActiveODMSCataloguesID(ids);
				searchParameters.put("nodes", ids);


				if (liveSearch) {
					searchParameters.put("rows", "1");
					// FederationCore.searchStatistics(ipAddress, "live");
				} else {
					searchParameters.put("rows", "1000000");
					// FederationCore.searchStatistics(ipAddress, "cache");
				}

				logger.info("Counting" + (liveSearch ? "live " : "in cache ")
						+ (eurovocFilter.isEuroVoc() ? "with EuroVoc search" : ""));
				logger.info("Count on nodes: " + request.getNodes());
				logger.info("Filters: " + searchParameters.toString());
				logger.info("Sort :" + request.getSort());
				logger.info("Rows :" + request.getRows());
				logger.info("Start :" + request.getStart());

				int result = FederatedSearch.countDataset(searchParameters);

				JSONObject res = new JSONObject();
				res.put("count", result);
				return Response.status(Response.Status.OK).entity(res.toString()).build();

			} else {
				return handleBadRequestErrorResponse(
						new Exception("The filters array must contain at least one element"));
			}

		} catch (GsonUtilException e) {
			return handleBadRequestErrorResponse(e);

		} catch (Exception e) {
			return handleErrorResponse500(e);
		}

	}

	@POST
	@Path("/sparql/query")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces("application/json")
	public Response runSparqlQuery(@Context HttpServletRequest httpRequest, final String input) {

		try {
			// Gets the source IP address from HTTPRequest
			String ipAddress = null;
			if (httpRequest.getHeader("X-FORWARDED-FOR") == null)
				ipAddress = httpRequest.getRemoteAddr();

			SparqlSearchRequest request = GsonUtil.json2Obj(input, GsonUtil.sparqlSearchRequestType);

			String queryResult = SPARQLFederatedSearch.runQuery(request.getQuery(), request.getFormat());

			// TODO Spostando il content type, non c'è bisogno di un JSON Object
			// con il campo "result"
			JSONObject jsonResult = new JSONObject();
			jsonResult.put("result", queryResult);
			// TODO Spostare il Content type nell'header
			jsonResult.put("contentType", request.getFormat());

			// Adds search statistics
			StatisticsManager.searchStatistics(ipAddress, "sparql");

			return Response.status(Response.Status.OK).entity(jsonResult.toString()).build();

		} catch (GsonUtilException e) {
			return handleBadRequestErrorResponse(e);

		} catch (QueryParseException e) {
			return handleBadQueryErrorResponse(e);

		} catch (Exception e) {
			return handleErrorResponse500(e);

		}

	}

	@GET
	@Path("/downloadFromUri")
	public Response downloadFromUri(@Context HttpServletRequest httpRequest, @QueryParam("url") String url,
			@QueryParam("format") String format,@QueryParam("downloadFile") @DefaultValue("true") boolean downloadFile,@QueryParam("isPreview") @DefaultValue("false") boolean isPreview) {
		
		logger.info("Download file API: "+downloadFile);
		String compiledUri = url;
		//client = ClientBuilder.newBuilder().readTimeout(10, TimeUnit.SECONDS).build();
		int timeout = Integer.parseInt(PropertyManager.getProperty(ODFProperty.PREVIEW_TIMEOUT))*1000;
		client = ClientBuilder.newClient();
		client.property(ClientProperties.CONNECT_TIMEOUT, timeout);
	    client.property(ClientProperties.READ_TIMEOUT,    timeout);
	    
		try {
			WebTarget webTarget = client.target(compiledUri);
			Response request = webTarget.request().get();
			logger.info("File uri: " + compiledUri);
			logger.info("File format: " + format);
			ResponseBuilder responseBuilder = Response.status(request.getStatus());
			if(downloadFile) {
				
				if(StringUtils.isNotBlank(format) && format.toLowerCase().contains("csv")) {
					InputStream stream = new BufferedInputStream((InputStream) request.getEntity());
//					CharsetDetector charDetector = new CharsetDetector();
//					charDetector.setText(stream);
					responseBuilder.entity(new InputStreamReader(stream,StandardCharsets.ISO_8859_1));
				}else {
					responseBuilder.entity(new StreamingOutput() {
						@Override
						public void write(OutputStream output) throws IOException, WebApplicationException {
							// TODO Auto-generated method stub
							IOUtils.copy((InputStream) request.getEntity(), output);
//							output.flush();
							output.close();
						}
					});
				}
			}
			
			MultivaluedMap<String, Object> headers = request.getHeaders();
			Set<String> keys = headers.keySet();
			logger.info("Status: " + request.getStatus());
			
			logger.debug(compiledUri);
			
			if(isPreview) {
				try {
					//TO-DO: renderlo configurabile
					long previewLimit = Integer.parseInt(PropertyManager.getProperty(ODFProperty.PREVIEW_TIMEOUT))*1024*1024; //10MB
					long dimension=0L;
					for (String k : keys) {

						if(k.toLowerCase().contains("content-length")) {
							logger.debug("Content-Length");
							logger.debug(headers.get(k).get(0));
							dimension = Long.parseLong((String) headers.get(k).get(0));
							break;
						}
						else if(k.toLowerCase().contains("content-range")) {
							logger.debug("Content-Range");
							logger.debug(headers.get(k));
							logger.debug(headers.get(k).get(0).toString());
							logger.debug(headers.get(k).get(0).toString().split("/")[1].replaceFirst("]", ""));
							dimension = Long.parseLong((String) headers.get(k).get(0).toString().split("/")[1].replaceFirst("]", ""));
							break;
						}

					}
					
					
					if(dimension>previewLimit) {
						responseBuilder = Response.status(Status.REQUEST_ENTITY_TOO_LARGE);
					}
					
//					if(dimension==0L || dimension>previewLimit) {
//						responseBuilder = Response.status(Status.REQUEST_ENTITY_TOO_LARGE);
//					}
					
				}catch(NumberFormatException ex) {
//					System.out.println("Unable to retrieve the dimension of the element");
					logger.error("Unable to retrieve the dimension of the element");
					responseBuilder = Response.status(Status.REQUEST_ENTITY_TOO_LARGE);
				}
				
				
			}
			
//			System.out.println("--------------------------------------------\n");
//			responseBuilder.header("Access-Control-Allow-Origin", "*");
			responseBuilder.header("original-file-format", format);
			responseBuilder.encoding("UTF-8");
			return responseBuilder.build();

		} catch (Exception e) {
			e.printStackTrace();
			return handleErrorResponse500(e);

		}finally {
//			request.close();
			client.close();
		}

	}

	@POST
	@Path("/catalogues/{nodeID}/dataset/{datasetID}/distribution/{distributionID}/createDatalet")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces(MediaType.APPLICATION_JSON)
	public Response createDataletFromDistribution(@Context HttpServletRequest httpRequest, final String input,
			@PathParam("nodeID") String nodeID, @PathParam("datasetID") String datasetID,
			@PathParam("distributionID") String distributionID) {

		CachePersistenceManager jpa = new CachePersistenceManager();
		try {

			Datalet datalet = GsonUtil.json2Obj(input, GsonUtil.dataletType);

			datalet.setId(UUID.randomUUID().toString());
			ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
			datalet.setRegisterDate(now);
			datalet.setLastSeenDate(now);
			datalet.setViews(1);
			datalet.setDatasetID(datasetID);
			datalet.setDistributionID(distributionID);
			datalet.setNodeID(nodeID);

			List<Datalet> existingDatalets = null;

			DCATDataset dataset = MetadataCacheManager.getDatasetByID(datasetID);
			boolean updateSolr = false;
			for (DCATDistribution d : dataset.getDistributions()) {
				if (d.getId().equals(distributionID)) {
					if (!d.isHasDatalets()) {
						d.setHasDatalets(true);
						updateSolr = true;
					} else {
						existingDatalets = jpa.jpaGetDataletByDistributionID(d.getId());
					}
					break;
				}
			}

			if (updateSolr) {
				MetadataCacheManager.updateDatasetInsertDatalet(Integer.parseInt(nodeID), dataset);
			}

			if (StringUtils.isBlank(datalet.getTitle())) {
				Integer newID = 1;
				if (existingDatalets != null && existingDatalets.size() != 0) {
					newID = existingDatalets.stream().filter(x -> x.isCustomTitle()).collect(Collectors.toList())
							.stream().map(x -> Integer.parseInt(x.getTitle().split("_")[1]))
							.collect(Collectors.summarizingInt(Integer::intValue)).getMax() + 1;
				}
				datalet.setTitle("Datalet_" + newID);
				datalet.setCustomTitle(true);
			} else {
				datalet.setCustomTitle(false);
			}

			jpa.jpaPersistAndCommitDatalet(datalet);

			return Response.ok().build();
		} catch (Exception e) {
			return handleErrorResponse500(e);
		} finally {
			jpa.jpaClose();
		}

	}

	@GET
	@Path("/catalogues/{nodeID}/dataset/{datasetID}/distribution/{distributionID}/datalets")
	// @Consumes({ MediaType.APPLICATION_JSON })
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDataletByDistribution(@Context HttpServletRequest httpRequest,
			@PathParam("nodeID") String nodeID, @PathParam("datasetID") String datasetID,
			@PathParam("distributionID") String distributionID) {

		CachePersistenceManager jpa = new CachePersistenceManager();
		try {
			List<Datalet> datalets = jpa.jpaGetDataletByTripleID(nodeID, datasetID, distributionID);
			return Response.ok(GsonUtil.obj2Json(datalets, GsonUtil.dataletListType)).build();
		} catch (Exception e) {
			return handleErrorResponse500(e);
		} finally {
			jpa.jpaClose();
		}

	}

	@PUT
	@Path("/catalogues/{nodeID}/dataset/{datasetID}/distribution/{distributionID}/datalet/{dataletID}/updateViews")
	// @Consumes({ MediaType.APPLICATION_JSON })
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateDataletViews(@Context HttpServletRequest httpRequest, @PathParam("nodeID") String nodeID,
			@PathParam("datasetID") String datasetID, @PathParam("distributionID") String distributionID,
			@PathParam("dataletID") String dataletID) {

		CachePersistenceManager jpa = new CachePersistenceManager();
		try {

			Datalet datalet = jpa.jpaGetDataletByIDs(nodeID, datasetID, distributionID, dataletID);
			datalet.setLastSeenDate(ZonedDateTime.now());
			datalet.setViews(datalet.getViews() + 1);

			jpa.jpaMergeAndCommitDatalet(datalet);

			return Response.ok().build();
		} catch (Exception e) {
			return handleErrorResponse500(e);
		} finally {
			jpa.jpaClose();
		}

	}
	
	@GET
	@Path("/cataloguesInfo")
	@Produces("application/json")
	public Response getCataloguesInfo(@Context HttpServletRequest httpRequest) {
		//LocalTime time1 = LocalTime.now();
		try {
			JSONArray result = new JSONArray();
			List<ODMSCatalogue> nodes = FederationCore.getODMSCatalogues()
					.stream()
					.filter(x -> x.isActive() && x.isCacheable() && !x.getSynchLock().equals(ODMSSynchLock.FIRST))
					.collect(Collectors.toList());
			Collections.sort(nodes, CommonUtil.nameOrder);
			for(ODMSCatalogue n : nodes) {
				JSONObject tmp = new JSONObject();
				tmp.put("name", n.getName());
				tmp.put("id", n.getId());
				tmp.put("federationLevel", n.getFederationLevel());
				result.put(tmp);
			}
			//LocalTime time2 = LocalTime.now();
			//logger.info("search_catalogues_list " + Duration.between(time1, time2) + " milliseconds");
			return Response.ok(result.toString()).build();
		}catch(Exception e) {
			return handleErrorResponse500(e);
		}	
	}
	
	@GET
	@Path("/catalogues")
	@Produces("application/json")
	public Response getODMSCatalogues(
			@QueryParam("withImage") @DefaultValue("true") boolean withImage,
			@QueryParam("orderType") @DefaultValue("asc") String orderType,
			@QueryParam("orderBy") @DefaultValue("id") String orderBy,
			@QueryParam("rows") @DefaultValue("10") String rows,
			@QueryParam("offset") @DefaultValue("0") String offset,
			@QueryParam("name") String name,
			@QueryParam("country") String country) {

		try {
			List<ODMSCatalogue> nodes = new ArrayList<ODMSCatalogue>(FederationCore.getODMSCatalogues(withImage).stream().filter(x -> x.isActive()).collect(Collectors.toList()));
			
			if(StringUtils.isNotBlank(name) && StringUtils.isBlank(country)) {
				nodes=nodes.stream().filter(x -> x.getName().toLowerCase().contains(name.toLowerCase())).collect(Collectors.toList());
			}else if(StringUtils.isBlank(name) && StringUtils.isNotBlank(country)) {
				nodes=nodes.stream().filter(x -> StringUtils.isNotBlank(x.getCountry()) && x.getCountry().toLowerCase().equals(country.toLowerCase())).collect(Collectors.toList());
			}else if(StringUtils.isNotBlank(name) && StringUtils.isNotBlank(country)) {
				nodes=nodes.stream().filter(x -> x.getName().toLowerCase().contains(name.toLowerCase()) 
						&& (StringUtils.isNotBlank(x.getCountry()) && x.getCountry().toLowerCase().equals(country.toLowerCase()))).collect(Collectors.toList());
			}
			
			int count = nodes.size();
			
			OrderBy ordBy;
			try {
				ordBy= OrderBy.valueOf(orderBy.toUpperCase());
			}catch(Exception e) {
				ordBy = OrderBy.NAME;
			}
			
			OrderType ordType;
			try {
				ordType= OrderType.valueOf(orderType.toUpperCase());
			}catch(Exception e) {
				ordType = OrderType.ASC;
			}
			
			switch(ordBy) {
			case ID:
				Collections.sort(nodes, ordType.equals(OrderType.DESC) ? CommonUtil.idOrder.reverse() : CommonUtil.idOrder);
				break;
			case DATASETCOUNT:
				Collections.sort(nodes, ordType.equals(OrderType.DESC) ? CommonUtil.datasetCountOrder.reverse() : CommonUtil.datasetCountOrder);
				break;
			case FEDERATIONLEVEL:
				Collections.sort(nodes, ordType.equals(OrderType.DESC) ? CommonUtil.federationLevelOrder.reverse() : CommonUtil.federationLevelOrder);
				break;
			case HOST:
				Collections.sort(nodes, ordType.equals(OrderType.DESC) ? CommonUtil.hostOrder.reverse() : CommonUtil.hostOrder);
				break;
			case LASTUPDATEDATE:
				Collections.sort(nodes, ordType.equals(OrderType.DESC) ? CommonUtil.lastUpdateOrder.reverse() : CommonUtil.lastUpdateOrder);
				break;
			case NAME:
				Collections.sort(nodes, ordType.equals(OrderType.DESC) ? CommonUtil.nameOrder.reverse() : CommonUtil.nameOrder);
				break;
			case NODESTATE:
				Collections.sort(nodes, ordType.equals(OrderType.DESC) ? CommonUtil.stateOrder.reverse() : CommonUtil.stateOrder);
				break;
			case NODETYPE:
				Collections.sort(nodes, ordType.equals(OrderType.DESC) ? CommonUtil.typeOrder.reverse() : CommonUtil.typeOrder);
				break;
			case REFRESHPERIOD:
				Collections.sort(nodes, ordType.equals(OrderType.DESC) ? CommonUtil.refreshPeriodOrder.reverse() : CommonUtil.refreshPeriodOrder);
				break;
			case REGISTERDATE:
				Collections.sort(nodes, ordType.equals(OrderType.DESC) ? CommonUtil.registerDateOrder.reverse() : CommonUtil.registerDateOrder);
				break;
			default:
				break;
			}

			//TODO: enable pagination
			/* 
			int row=CommonUtil.ROWSDEFAULT;
			int off=CommonUtil.OFFSETDEFAULT;
			
			try {
			row = Integer.parseInt(rows);
			}catch(Exception e) {}
			
			try {
				off = Integer.parseInt(offset);
			}catch(Exception e) {}

			if (off >= nodes.size()) {
				nodes.clear();
			} else if (off < nodes.size()) {
				if (row >= nodes.size() || (row + off) >= nodes.size()) {
					row = nodes.size();
				} else {
					row += off;
				}
				nodes = nodes.subList(off, row);
			}
			*/
			JSONArray array = new JSONArray(GsonUtil.obj2JsonWithExclude(nodes, GsonUtil.nodeListType));
			JSONObject result = new JSONObject();
			result.put("count", count);
			result.put("catalogues", array);
			System.gc();
			return Response.status(Response.Status.OK).entity(result.toString()).build();


		} catch (Exception e) {
			logger.error("Exception raised "+e.getLocalizedMessage());
			return handleErrorResponse500(e);
		}

	}
	
	
	@GET
	@Path("/catalogues/{nodeID}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces("application/json")
	public Response getSingleCatalogue(@Context HttpServletRequest httpRequest,@PathParam("nodeID") String nodeID,@QueryParam("withImage") @DefaultValue("true") boolean withImage) {

		try {
			ODMSCatalogue result = FederationCore.getODMSCatalogue(Integer.parseInt(nodeID), withImage);
			if(result.isActive())
				return Response.status(Response.Status.OK).entity(GsonUtil.obj2JsonWithExclude(result, GsonUtil.nodeType)).build();
			else {
				ErrorResponse err = new ErrorResponse(String.valueOf(Response.Status.NOT_FOUND.getStatusCode()), "Catalogues with id: "+nodeID+" not found", String.valueOf(Response.Status.NOT_FOUND.getStatusCode()), "Catalogues with id: "+nodeID+" not found");
				return Response.status(Response.Status.NOT_FOUND).entity(GsonUtil.obj2Json(err, GsonUtil.errorResponseSetType)).build();
			}

		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			return handleErrorResponse500(e);
		} catch (GsonUtilException e) {
			// TODO Auto-generated catch block
			return handleErrorResponse500(e);
		} catch (ODMSCatalogueNotFoundException e) {
			// TODO Auto-generated catch block
			return handleBadRequestErrorResponse(e);
		} catch (ODMSManagerException e) {
			// TODO Auto-generated catch block
			return handleBadRequestErrorResponse(e);
		}
	}
	
	/*
	@GET
	@Path("/catalogues/{nodeID}/datasets")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces("application/json")
	public Response getCatalogueDatasets(@Context HttpServletRequest httpRequest,@PathParam("nodeID") String nodeID) {

		try {
			ODMSCatalogue cat= FederationCore.getODMSCatalogue(Integer.parseInt(nodeID));
			if(cat.isActive()) {
				List<DCATDataset> result = MetadataCacheManager.getAllDatasetsByODMSCatalogue(Integer.parseInt(nodeID));
				return Response.status(Response.Status.OK).entity(GsonUtil.obj2Json(result, GsonUtil.datasetListType)).build();
			}else {
				ErrorResponse err = new ErrorResponse(String.valueOf(Response.Status.NOT_FOUND.getStatusCode()), "Catalogues with id: "+nodeID+" not found", String.valueOf(Response.Status.NOT_FOUND.getStatusCode()), "Catalogues with id: "+nodeID+" not found");
				return Response.status(Response.Status.NOT_FOUND).entity(GsonUtil.obj2Json(err, GsonUtil.errorResponseSetType)).build();
			}

		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			return handleErrorResponse500(e);
		} catch (DatasetNotFoundException e) {
			// TODO Auto-generated catch block
			return handleErrorResponse500(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return handleErrorResponse500(e);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			return handleErrorResponse500(e);
		} catch (GsonUtilException e) {
			// TODO Auto-generated catch block
			return handleErrorResponse500(e);
		} catch (ODMSCatalogueNotFoundException e) {
			// TODO Auto-generated catch block
			return handleBadRequestErrorResponse(e);
		}
	}


	@GET
	@Path("/catalogues/{nodeID}/datasets/{datasetID}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces("application/json")
	public Response getSingleDataset(@Context HttpServletRequest httpRequest,@PathParam("nodeID") String nodeID,@PathParam("datasetID") String	 datasetID) {

		try {
			
			ODMSCatalogue cat= FederationCore.getODMSCatalogue(Integer.parseInt(nodeID));
			if(cat.isActive()) {
				DCATDataset result = MetadataCacheManager.getDatasetByID(datasetID);
				return Response.status(Response.Status.OK).entity(GsonUtil.obj2Json(result, GsonUtil.datasetType)).build();
			}else {
				ErrorResponse err = new ErrorResponse(String.valueOf(Response.Status.NOT_FOUND.getStatusCode()), "Catalogues with id: "+nodeID+" not found", String.valueOf(Response.Status.NOT_FOUND.getStatusCode()), "Catalogues with id: "+nodeID+" not found");
				return Response.status(Response.Status.NOT_FOUND).entity(GsonUtil.obj2Json(err, GsonUtil.errorResponseSetType)).build();
			}
	
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			return handleErrorResponse500(e);
		} catch (DatasetNotFoundException e) {
			// TODO Auto-generated catch block
			return handleErrorResponse500(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return handleErrorResponse500(e);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			return handleErrorResponse500(e);
		} catch (GsonUtilException e) {
			// TODO Auto-generated catch block
			return handleErrorResponse500(e);
		}catch (ODMSCatalogueNotFoundException e) {
			// TODO Auto-generated catch block
			return handleBadRequestErrorResponse(e);
		}
	}*/
	
	@GET
	@Path("/datasets/{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces("application/json")
	public Response getDatasetByID(@Context HttpServletRequest httpRequest,@PathParam("id") String id) {

		try {
			try {
				DCATDataset result = MetadataCacheManager.getDatasetByID(id);
				return Response.status(Response.Status.OK).entity(GsonUtil.obj2Json(result, GsonUtil.datasetType)).build();
			}catch (DatasetNotFoundException e) {
				// TODO Auto-generated catch block
				ErrorResponse err = new ErrorResponse(String.valueOf(Response.Status.NOT_FOUND.getStatusCode()), "Dataset with id: "+id+" not found", String.valueOf(Response.Status.NOT_FOUND.getStatusCode()), "Dataset with id: "+id+" not found");
				return Response.status(Response.Status.NOT_FOUND).entity(GsonUtil.obj2Json(err, GsonUtil.errorResponseSetType)).build();
			} 
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			return handleErrorResponse500(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return handleErrorResponse500(e);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			return handleErrorResponse500(e);
		} catch (GsonUtilException e) {
			// TODO Auto-generated catch block
			return handleErrorResponse500(e);
		}
	}
	
	@GET
	@Path("executeOrionQuery/{cbQueryID}/catalogue/{catalogueID}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces("application/json")
	public Response executeOrionQuery(@Context HttpServletRequest httpRequest,
			@PathParam("catalogueID") String nodeID,@PathParam("cbQueryID") String queryID) {
		ErrorResponse err=null;
		if(StringUtils.isBlank(nodeID)) {
			err = new ErrorResponse(String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()), "Missing mandatory query parameter: catalogue", String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()), "Missing mandatory query parameter: catalogue");
		}

		if(StringUtils.isBlank(queryID)) {
			err = new ErrorResponse(String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()), "Missing mandatory query parameter: cbQueryID", String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()), "Missing mandatory query parameter: cbQueryID");	
		}
		
		try {
			ODMSCatalogue catalogue = FederationCore.getODMSCatalogue(Integer.parseInt(nodeID), false);
			if(!catalogue.getNodeType().equals(ODMSCatalogueType.ORION)) {
				err = new ErrorResponse(String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()), "Catalogue: "+nodeID+" is not ORION", String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()), "Catalogue: "+nodeID+" is not ORION");
			}else {
				
				OrionCatalogueConfiguration catalogueConfig = (OrionCatalogueConfiguration) catalogue.getAdditionalConfig();
				OrionDistributionConfig distributionConfig = MetadataCacheManager.getOrionDistributionConfig(queryID);
				
				String compiledUri=catalogue.getHost()+"?"+distributionConfig.getQuery();
				
				client = ClientBuilder.newClient();

				WebTarget webTarget = client.target(compiledUri);
				Invocation.Builder builder = webTarget.request();
				if(StringUtils.isNotBlank(distributionConfig.getFiwareService())) {
					builder = builder.header("Fiware-Service",distributionConfig.getFiwareService());
				}
				
				if(StringUtils.isNotBlank(distributionConfig.getFiwareServicePath())) {
					builder = builder.header("Fiware-ServicePath",distributionConfig.getFiwareServicePath());
				}
				
				if(catalogueConfig.isAuthenticated())
					builder = builder.header("X-Auth-Token",catalogueConfig.getAuthToken());
				
				Response request = builder.get();
				ResponseBuilder responseBuilder = Response.status(request.getStatus());
				final InputStream responseStream = (InputStream) request.getEntity();
				StreamingOutput output = new StreamingOutput() {
					@Override
					public void write(OutputStream out) throws IOException, WebApplicationException {
						int length;
						byte[] buffer = new byte[1024];
						while ((length = responseStream.read(buffer)) != -1) {
							out.write(buffer, 0, length);
						}
						out.flush();
						responseStream.close();
					}
				};

				responseBuilder.entity(output);
				
				MultivaluedMap<String, Object> headers = request.getHeaders();
				Set<String> keys = headers.keySet();
				//logger.info("Status: " + request.getStatus());
				

				for (String k : keys) {
					if (!k.toLowerCase().equals("access-control-allow-origin"))
						responseBuilder.header(k, headers.get(k).get(0));
				}
				
				return responseBuilder.build();
				
			}
			
			return Response.status(Response.Status.OK).build();
		} catch(ODMSCatalogueNotFoundException e) {
			err = new ErrorResponse(String.valueOf(Response.Status.NOT_FOUND.getStatusCode()), "Catalogues with id: "+nodeID+" not found", String.valueOf(Response.Status.NOT_FOUND.getStatusCode()), "Catalogues with id: "+nodeID+" not found");
			return Response.status(Response.Status.NOT_FOUND).build();	
		} catch (NumberFormatException | ODMSManagerException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return handleErrorResponse500(e);
		}
		
				
	}
	
	
	private static Response handleErrorResponse500(Exception e) {

		e.printStackTrace();
		ErrorResponse error = new ErrorResponse(String.valueOf(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()),
				e.getMessage(), e.getClass().getSimpleName(), "An error occurred, please contact the administrator!");
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error.toJson()).build();
	}

	private static Response handleEuroVocTranslationNotFoundErrorResponse(EuroVocTranslationNotFoundException e) {

		e.printStackTrace();
		ErrorResponse error = new ErrorResponse(String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()),
				e.getMessage(), e.getClass().getSimpleName(), "No results found!");
		return Response.status(Response.Status.BAD_REQUEST).entity(error.toJson()).build();
	}

	private static Response handleBadRequestErrorResponse(Exception e) {

		e.printStackTrace();
		ErrorResponse error = new ErrorResponse(String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()),
				e.getMessage(), e.getClass().getSimpleName(), "The request body is not a valid JSON");
		return Response.status(Response.Status.BAD_REQUEST).entity(error.toJson()).build();
	}

	private static Response handleBadQueryErrorResponse(Exception e) {

		e.printStackTrace();
		ErrorResponse error = new ErrorResponse(String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()),
				e.getMessage(), e.getClass().getSimpleName(), "Malformed SPARQL query");
		return Response.status(Response.Status.BAD_REQUEST).entity(error.toJson()).build();
	}

}
