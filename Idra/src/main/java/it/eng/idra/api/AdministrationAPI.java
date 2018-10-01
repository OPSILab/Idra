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

import it.eng.idra.authentication.LoggedUser;
import it.eng.idra.authentication.Secured;
import it.eng.idra.beans.Datalet;
import it.eng.idra.beans.ErrorResponse;
import it.eng.idra.beans.Log;
import it.eng.idra.beans.LogsRequest;
import it.eng.idra.beans.ODFProperty;
import it.eng.idra.beans.PasswordChange;
import it.eng.idra.beans.RdfPrefix;
import it.eng.idra.beans.User;
import it.eng.idra.beans.dcat.DCATDataset;
import it.eng.idra.beans.odms.ODMSAlreadyPresentException;
import it.eng.idra.beans.odms.ODMSManagerException;
import it.eng.idra.beans.orion.OrionCatalogueConfiguration;
import it.eng.idra.beans.odms.ODMSCatalogue;
import it.eng.idra.beans.odms.ODMSCatalogueChangeActiveStateException;
import it.eng.idra.beans.odms.ODMSCatalogueForbiddenException;
import it.eng.idra.beans.odms.ODMSCatalogueMessage;
import it.eng.idra.beans.odms.ODMSCatalogueNotFoundException;
import it.eng.idra.beans.odms.ODMSCatalogueOfflineException;
import it.eng.idra.beans.odms.ODMSCatalogueType;
import it.eng.idra.beans.statistics.StatisticsRequest;
import it.eng.idra.cache.CachePersistenceManager;
import it.eng.idra.cache.MetadataCacheManager;
import it.eng.idra.dcat.dump.DCATAPDumpManager;
import it.eng.idra.management.FederationCore;
import it.eng.idra.management.ODMSManager;
import it.eng.idra.management.RdfPrefixManager;
import it.eng.idra.management.StatisticsManager;
import it.eng.idra.utils.GsonUtil;
import it.eng.idra.utils.GsonUtilException;
import it.eng.idra.utils.PropertyManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.*;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.JsonObject;

@Path("/administration")
public class AdministrationAPI {

	private static Logger logger = LogManager.getLogger(AdministrationAPI.class);

	@GET
	@Path("/version")
	@Produces("application/json")
	public Response getVersion() {

		JSONObject out = new JSONObject();
		out.put("idra_version", PropertyManager.getProperty(ODFProperty.IDRA_VERSION));
		out.put("idra_release_timestamp", PropertyManager.getProperty(ODFProperty.IDRA_RELEASE_TIMESTAMP));

		return Response.status(Response.Status.OK).entity(out.toString()).build();
	}

	@POST
	@Secured
	@Path("/catalogues")
	@Consumes({ MediaType.MULTIPART_FORM_DATA })
	@Produces("application/json")
	public Response registerODMSCatalogue(@FormDataParam("dump") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition cdh, @FormDataParam("node") String nodeString) {
		ODMSCatalogue node = null;
		try {
			
			node = GsonUtil.json2Obj(nodeString, GsonUtil.nodeType);

			// If the node type is DCATDUMP, if the dump URL is blank, try to get the
			// dump from the uploaded file
			if (node.getNodeType().equals(ODMSCatalogueType.DCATDUMP)) {
				if (StringUtils.isBlank(node.getDumpURL())) {
					if (fileInputStream == null)
						throw new IOException("The dump part of the request is empty");

					String dumpString = IOUtils.toString(fileInputStream, StandardCharsets.UTF_8);
					if (StringUtils.isNotBlank(dumpString))
						node.setDumpString(dumpString);
					else
						throw new IOException(
								"The node must have either the dumpURL or dump file in the \" dump \" part of the multipart request");
				}
			} else {
				node.setDumpURL(null);
				node.setDumpFilePath(null);
			}

			if (!node.getNodeType().equals(ODMSCatalogueType.WEB))
				node.setSitemap(null);

			if(!node.getNodeType().equals(ODMSCatalogueType.ORION) || node.getNodeType().equals(ODMSCatalogueType.SPARQL)) {
				node.setAdditionalConfig(null);
			}else if(node.getNodeType().equals(ODMSCatalogueType.ORION)){
				if(node.getAdditionalConfig()==null) {
					ErrorResponse error = new ErrorResponse(String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()),
							"Orion Catalogue must have its configuration parameters!", "400", "Orion Catalogue must have its configuration parameters!");
					return Response.status(Response.Status.BAD_REQUEST).entity(error.toJson()).build();
				}
				OrionCatalogueConfiguration orionConfig = (OrionCatalogueConfiguration) node.getAdditionalConfig();
				if(orionConfig.isAuthenticated()) {
					if(StringUtils.isBlank(orionConfig.getAuthToken()) || StringUtils.isBlank(orionConfig.getRefreshToken()) || StringUtils.isBlank(orionConfig.getOauth2Endpoint())
							|| StringUtils.isBlank(orionConfig.getClientID()) || StringUtils.isBlank(orionConfig.getClientSecret())) {
						ErrorResponse error = new ErrorResponse(String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()),
								"Please provide all of the authentication configuration parameters", "400", "Please provide all of the authentication configuration parameters");
						return Response.status(Response.Status.BAD_REQUEST).entity(error.toJson()).build();
					}
				}
				
				if(StringUtils.isBlank(orionConfig.getOrionDatasetDumpString()) && fileInputStream==null)
						throw new IOException("Orion Catalogue must have a dump string or a dump file");
				
				if(StringUtils.isBlank(orionConfig.getOrionDatasetDumpString())) {
					String dumpString = IOUtils.toString(fileInputStream, StandardCharsets.UTF_8);
					if (StringUtils.isNotBlank(dumpString)) {
						orionConfig.setOrionDatasetDumpString(dumpString);
						node.setAdditionalConfig(orionConfig);
					}
				}
			}
			
			if (node.isActive() == null) {
				node.setActive(false);
			}
			
			if (node.isActive()) {
				FederationCore.registerODMSCatalogue(node);
			} else {
				FederationCore.registerInactiveODMSCatalogue(node);
			}

			return Response.status(Response.Status.OK).build();

		} catch (GsonUtilException | IOException e) {

			return handleBadRequestErrorResponse(e);

		} catch (ODMSAlreadyPresentException e) {

			logger.info(e.getMessage());
			ErrorResponse error = new ErrorResponse(String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()),
					e.getMessage(), e.getClass().getSimpleName(), "The node is already present in the federation!");
			return Response.status(Response.Status.BAD_REQUEST).entity(error.toJson()).build();

		} catch (ODMSCatalogueNotFoundException e) {
			return handleNodeHostNotFoundErrorResponse(e, node.getHost());
		} catch (ODMSCatalogueForbiddenException e) {
			return handleNodeForbiddenErrorResponse(e, node.getHost());
		} catch (ODMSCatalogueOfflineException e) {
			return handleNodeOfflineErrorResponse(e, node.getHost());
		} catch (Exception e) {
			return handleErrorResponse500(e);
		}
	}

	@GET
	@Secured
	@Path("/catalogues")
	@Produces("application/json")
	public Response getODMSCatalogues(@QueryParam("withImage") boolean withImage) {

		try {
			List<ODMSCatalogue> nodes = new ArrayList<ODMSCatalogue>(FederationCore.getODMSCatalogues(withImage));

			try {
				HashMap<Integer, Long> messages = FederationCore.getAllODMSMessagesCount();
				nodes.stream().forEach(node -> node.setMessageCount(messages.get(node.getId())));
			} catch (Exception e) {
				e.printStackTrace();
				nodes.stream().forEach(node -> node.setMessageCount(0L));
			}
			
			nodes.sort((n1, n2) -> n1.getId()-n2.getId());

			return Response.status(Response.Status.OK).entity(GsonUtil.obj2Json(nodes, GsonUtil.nodeListType)).build();

		} catch (Exception e) {
			logger.error("Exception raised "+e.getLocalizedMessage());
			return handleErrorResponse500(e);
		}

	}

	@PUT
	@Secured
	@Path("/catalogues/{id}/activate")
	@Produces("application/json")
	public Response activateODMSCatalogue(@PathParam("id") String id) {
		ODMSCatalogue node = null;
		try {

			node = FederationCore.getODMSCatalogue(Integer.parseInt(id));

			if (node.isActive()) {
				logger.error("Node "+node.getHost()+" already active");
				throw new ODMSCatalogueChangeActiveStateException("Node "+node.getHost()+" already active");
			}
			
			if(node.getNodeType().equals(ODMSCatalogueType.DCATDUMP)) {
				if (StringUtils.isBlank(node.getDumpURL()) && StringUtils.isBlank(node.getDumpString())
						&& StringUtils.isNotBlank(node.getDumpFilePath())) {
					
					// Read the content of the file from the file system
					String dumpString = new String(Files.readAllBytes(Paths.get(node.getDumpFilePath())));
					node.setDumpString(dumpString);
				}
			}
			
			if(node.getNodeType().equals(ODMSCatalogueType.ORION)) {
				OrionCatalogueConfiguration conf = (OrionCatalogueConfiguration) node.getAdditionalConfig();
				if (StringUtils.isBlank(conf.getOrionDatasetDumpString()) && StringUtils.isNotBlank(conf.getOrionDatasetFilePath())) {
					// Read the content of the file from the file system
					String dumpOrion = new String(Files.readAllBytes(Paths.get(conf.getOrionDatasetFilePath())));
					conf.setOrionDatasetDumpString(dumpOrion);
					node.setAdditionalConfig(conf);
				}
			}
			
			FederationCore.activateODMSCatalogue(node);

			return Response.status(Response.Status.OK).build();

		} catch (ODMSCatalogueChangeActiveStateException e) {
			logger.error("Node "+node.getHost()+" raised: "+e.getLocalizedMessage());
			return handleBadRequestErrorResponse(e);
		} catch (Exception e) {
			logger.error("Node "+node.getHost()+" raised: "+e.getLocalizedMessage());
			return handleErrorResponse500(e);
		}
	}

	@PUT
	@Secured
	@Path("/catalogues/{id}/deactivate")
	@Produces("application/json")
	public Response deactivateODMSCatalogue(@PathParam("id") String id,
			@QueryParam("keepDatasets") @DefaultValue("false") Boolean keepDatasets) {
		ODMSCatalogue node = null;
		try {

			node = FederationCore.getODMSCatalogue(Integer.parseInt(id));
			if (!node.isActive()) {
				logger.error("Node "+node.getHost()+" already inactive");
				throw new ODMSCatalogueChangeActiveStateException("Node "+node.getHost()+" already inactive");
			}

			FederationCore.deactivateODMSCatalogue(node, keepDatasets);

			return Response.status(Response.Status.OK).build();

		} catch (ODMSCatalogueChangeActiveStateException e) {
			logger.error("Node "+node.getHost()+" raised: "+e.getLocalizedMessage());
			return handleBadRequestErrorResponse(e);
		} catch (Exception e) {
			logger.error("Node "+node.getHost()+" raised: "+e.getLocalizedMessage());
			return handleErrorResponse500(e);
		}
	}
	
	@GET
	@Secured
	@Path("/catalogues/{nodeId}")
	@Produces("application/json")
	public Response getODMSCatalogue(@PathParam("nodeId") String nodeId, @QueryParam("withImage") boolean withImage) {

		try {

			ODMSCatalogue node = FederationCore.getODMSCatalogue(Integer.parseInt(nodeId), withImage);

			return Response.status(Response.Status.OK).entity(GsonUtil.obj2Json(node, GsonUtil.nodeType).toString())
					.build();

		} catch (NumberFormatException e) {
			logger.error("NumberFormatException with parameter "+nodeId);
			return handleBadRequestErrorResponse(e);

		} catch (ODMSCatalogueNotFoundException | NullPointerException e) {
			logger.error("Exception "+e.getLocalizedMessage());
			return handleNodeNotFoundErrorResponse(e, nodeId);

		} catch (Exception e) {
			logger.error("Exception "+e.getLocalizedMessage());
			return handleErrorResponse500(e);

		}
	}

	@PUT
	@Secured
	@Path("/catalogues/{nodeId}")
	@Consumes({ MediaType.MULTIPART_FORM_DATA })
	@Produces("application/json")
	public Response updateODMSCatalogue(@PathParam("nodeId") String nodeId,
			@FormDataParam("dump") InputStream fileInputStream, @FormDataParam("file") FormDataContentDisposition cdh,
			@FormDataParam("node") String nodeString) throws JSONException, ParseException {

		try {

			ODMSCatalogue requestNode = GsonUtil.json2Obj(nodeString, GsonUtil.nodeType);
			ODMSCatalogue currentNode = ODMSManager.getODMSCatalogue(Integer.parseInt(nodeId));

			if (!requestNode.getNodeType().equals(currentNode.getNodeType())) {
				logger.error("Update node "+currentNode.getHost()+" type is not allowed");
				throw new Exception("Update node "+currentNode.getHost()+" type is not allowed");
			}

			if (!requestNode.isActive().equals(currentNode.isActive())) {
				logger.error("Update Active State for node " +currentNode.getHost()+ " is not allowed");
				throw new ODMSCatalogueChangeActiveStateException("Update Active State for node " +currentNode.getHost()+ " is not allowed");
			}

			if (requestNode.getNodeType().equals(ODMSCatalogueType.DCATDUMP)) {
				if ((StringUtils.isBlank(currentNode.getDumpURL()) && StringUtils.isNotBlank(requestNode.getDumpURL()))
						&& (!requestNode.getDumpURL().equals(currentNode.getDumpURL()))) {
					logger.info("Updating the DUMP Url for node: "+currentNode.getHost());
				} else {
					String dumpString = IOUtils.toString(fileInputStream, StandardCharsets.UTF_8);
					if (StringUtils.isNotBlank(dumpString)) {
						logger.info("Updating dump file for node "+currentNode.getHost());
					} else if (StringUtils.isBlank(requestNode.getDumpFilePath())
							&& StringUtils.isNotBlank(currentNode.getDumpFilePath())) {
						logger.info("Dump file path was empty for "+currentNode.getHost()+" , setting the previous");
						requestNode.setDumpFilePath(currentNode.getDumpFilePath());
					}
				}
			}

			if (requestNode.getNodeType().equals(ODMSCatalogueType.WEB)) {
				if (requestNode.getSitemap() == null) {
					logger.error("Sitemap was null, setting the previous for node "+currentNode.getHost());
					requestNode.setSitemap(currentNode.getSitemap());
				}
			} else {
				requestNode.setSitemap(null);
			}

			boolean rescheduleJob = false;
			if(requestNode.getRefreshPeriod()!=currentNode.getRefreshPeriod()) {
				rescheduleJob=true;
			}
			
			if (requestNode != null) {
				requestNode.setId(Integer.parseInt(nodeId));

				FederationCore.updateFederatedODMSCatalogue(requestNode,rescheduleJob);

				return Response.status(Response.Status.OK).build();
			} else {
				logger.error("The request body is empty");
				throw new GsonUtilException("The request body is empty");
			}

		} catch (GsonUtilException | NumberFormatException | ODMSCatalogueChangeActiveStateException e) {
			return handleBadRequestErrorResponse(e);

		} catch (ODMSCatalogueNotFoundException e) {
			return handleNodeNotFoundErrorResponse(e, nodeId);
		} catch (Exception e) {
			return handleErrorResponse500(e);
		}

	}

	@DELETE
	@Secured
	@Path("/catalogues/{nodeId}")
	@Produces("application/json")
	public Response unregisterODMSCatalogue(@PathParam("nodeId") String nodeId) {

		ODMSCatalogue node = null;
		try {

			node = FederationCore.getODMSCatalogue(Integer.parseInt(nodeId));
			logger.info("Deleting ODMS catalogue with host: " + node.getHost() + " and id "+nodeId+" - START");
			FederationCore.unregisterODMSCatalogue(node);
			logger.info("Deleting ODMS node with id: " + node.getHost() + " and id "+nodeId+" - COMPLETE");
			return Response.status(Response.Status.OK).build();

		} catch (NumberFormatException e) {
			return handleBadRequestErrorResponse(e);

		} catch (NullPointerException | ODMSCatalogueNotFoundException e) {
			return handleNodeNotFoundErrorResponse(e, nodeId);

		} catch (Exception e) {
			return handleErrorResponse500(e);
		}

	}

	@POST
	@Secured
	@Path("/catalogues/{nodeId}/synchronize")
	@Produces("application/json")
	public Response startODMSCatalogueSynch(@PathParam("nodeId") String nodeId) {

		int nodeID = Integer.parseInt(nodeId);

		try {
			logger.info("Forcing the synchronization for node "+nodeId);
			FederationCore.startODMSCatalogueSynch(nodeID);
			return Response.status(Response.Status.OK).build();

		} catch (ODMSCatalogueNotFoundException e) {
			return handleNodeNotFoundErrorResponse(e, nodeId);
		} catch (ODMSManagerException e) {
			return handleErrorResponse500(e);
		}

	}

	@POST
	@Secured
	@Path("/configuration")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces("application/json")
	public Response setSettings(final String input) {

		HashMap<String, String> map = null;

		try {
			map = GsonUtil.json2Obj(input, GsonUtil.configurationType);
			FederationCore.setSettings(map);

			return Response.ok().build();

		} catch (GsonUtilException e) {
			return handleBadRequestErrorResponse(e);

		} catch (Exception e) {
			return handleErrorResponse500(e);
		}

	}

	@GET
	@Path("/configuration")
	@Produces("application/json")
	public Response getSettings() {

		try {

			HashMap<String, String> conf = FederationCore.getSettings();

			return Response.status(Response.Status.OK).entity(GsonUtil.obj2Json(conf, GsonUtil.configurationType))
					.build();

		} catch (Exception e) {
			return handleErrorResponse500(e);
		}

	}

	@GET
	@Path("/prefixes")
	@Produces("application/json")
	public Response getPrefixes() {

		try {
			List<RdfPrefix> prefixes = RdfPrefixManager.getAllPrefixes();
			return Response.status(Response.Status.OK).entity(GsonUtil.obj2Json(prefixes, GsonUtil.prefixListType))
					.build();
		} catch (Exception e) {
			return handleErrorResponse500(e);
		}
	}

	@GET
	@Secured
	@Path("/prefixes/{prefixId}")
	@Produces("application/json")
	public Response getPrefix(@PathParam("prefixId") String prefixId) {

		try {

			RdfPrefix prefix = RdfPrefixManager.getPrefix(Integer.parseInt(prefixId));

			return Response.status(Response.Status.OK).entity(GsonUtil.obj2Json(prefix, GsonUtil.prefixType)).build();

		} catch (NumberFormatException e) {
			return handleBadRequestErrorResponse(e);

		} catch (NullPointerException e) {
			return handlePrefixNotFoundErrorResponse(e, prefixId);

		} catch (Exception e) {
			return handleErrorResponse500(e);
		}
	}

	@DELETE
	@Secured
	@Path("/prefixes/{prefixId}")
	// @Produces("application/json")
	public Response deletePrefix(@PathParam("prefixId") String prefixId) {

		// logger.info("DELETE " + prefixId);

		try {

			RdfPrefixManager.deletePrefix(Integer.parseInt(prefixId));

			return Response.status(Response.Status.OK).build();

		} catch (NumberFormatException e) {
			return handleBadRequestErrorResponse(e);

		} catch (NullPointerException e) {
			return handlePrefixNotFoundErrorResponse(e, prefixId);

		} catch (Exception e) {
			return handleErrorResponse500(e);

		}

	}

	@PUT
	@Secured
	@Path("/prefixes/{prefixId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces("application/json")
	public Response updatePrefix(@PathParam("prefixId") String prefixId, final String input) {

		try {

			RdfPrefix prefix = GsonUtil.json2Obj(input, GsonUtil.prefixType);
			prefix.setId(Integer.parseInt(prefixId));

			RdfPrefixManager.updatePrefix(prefix);

			return Response.status(Response.Status.OK).build();

		} catch (NumberFormatException e) {
			return handleBadRequestErrorResponse(e);

		} catch (NullPointerException e) {
			return handlePrefixNotFoundErrorResponse(e, prefixId);

		} catch (Exception e) {
			return handleErrorResponse500(e);
		}

	}

	@POST
	@Secured
	@Path("/prefixes")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces("application/json")
	public Response addPrefix(final String input) {

		try {

			RdfPrefix prefix = GsonUtil.json2Obj(input, GsonUtil.prefixType);
			RdfPrefixManager.addPrefix(prefix);

			return Response.status(Response.Status.OK).build();

		} catch (GsonUtilException e) {
			return handleBadRequestErrorResponse(e);
		} catch (Exception e) {
			return handleErrorResponse500(e);
		}

	}

	@POST
	@Path("/login")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces("text/plain")
	public Response login(final String input) {

		try {

			User user = GsonUtil.json2Obj(input, GsonUtil.userType);

			String token = FederationCore.login(user.getUsername(), user.getPassword());

			return Response.status(Response.Status.OK).entity(token).build();

		} catch (GsonUtilException e) {
			return handleBadRequestErrorResponse(e);
		} catch (NullPointerException e) {
			return handleErrorResponseLogin(e);
		} catch (Exception e) {
			return handleErrorResponse500(e);
		}

	}

	@PUT
	@Path("/updatePassword")
	@Secured
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces("application/json")
	public Response updatePassword(final String input) {

		try {

			PasswordChange passChange = GsonUtil.json2Obj(input, PasswordChange.class);

			if (!passChange.getNewPassword().equals(passChange.getNewPasswordConfirm())) {
				ErrorResponse error = new ErrorResponse(String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()),
						"Password and password confirm are not equal", "PasswordsNotEqual",
						"Password and password confirm are not equal");
				return Response.status(Response.Status.BAD_REQUEST).entity(error.toJson()).build();
			}

			if (FederationCore.validatePassword(passChange.getUsername(), passChange.getOldPassword())) {

				FederationCore.updateUserPassword(passChange.getUsername(), passChange.getNewPassword());

				JsonObject out = new JsonObject();
				out.addProperty("message", "Password successfully updated!");
				return Response.status(Response.Status.OK).entity(out.toString()).build();

			} else {

				ErrorResponse error = new ErrorResponse(String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()),
						"Wrong Old Password!", "WrongOldPassword", "Wrong Old Password!");
				return Response.status(Response.Status.BAD_REQUEST).entity(error.toJson()).build();
			}
		} catch (Exception e) {
			return handleErrorResponse500(e);
		}
	}

	@POST
	@Path("/logout")
	@Secured
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces("application/json")
	public Response logout(final String input) {

		try {

			LoggedUser user = GsonUtil.json2Obj(input, GsonUtil.loggedUserType);

			FederationCore.logout(user.getUsername());

			return Response.status(Response.Status.OK).build();

		} catch (GsonUtilException e) {
			return handleBadRequestErrorResponse(e);
		} catch (Exception e) {
			return handleErrorResponse500(e);
		}

	}

	@GET
	@Secured
	@Path("/verifyToken")
	@Produces("application/json")
	public Response verifyToken() {

		return Response.status(Response.Status.OK).build();

	}

	@GET
	@Secured
	@Path("/countries")
	@Produces("application/json")
	public Response getAllCountries() {

		// TODO Sostituire DateTime con ZonedDateTime e JsonObject di gson e non
		// JSONObject
		try {
			List<String> countries = StatisticsManager.getAllCountries();
			JSONObject j = new JSONObject();
			j.put("countries", GsonUtil.obj2Json(countries, GsonUtil.stringListType));
			j.put("minDate", StatisticsManager.getMinDateSearchStatistics());
			return Response.status(Response.Status.OK).entity(j.toString()).build();

		} catch (Exception e) {
			return handleErrorResponse500(e);
		}

	}

	@GET
	@Secured
	@Path("/cataloguesStatMinDate")
	@Produces("application/json")
	public Response getMinDateCataloguesStat() {

		// TODO Sostituire DateTime con ZonedDateTime e JsonObject di gson e non
		// JSONObject

		try {
			JSONObject j = new JSONObject();
			j.put("minDate", StatisticsManager.getMinDateNodesStatistics());
			return Response.status(Response.Status.OK).entity(j.toString()).build();
		} catch (Exception e) {
			return handleErrorResponse500(e);
		}

	}

	@POST
	@Secured
	@Path("/statistics/catalogues")
	@Produces("application/json")
	public Response getODMSCataloguesStatistics(final String input) {

		try {

			StatisticsRequest request = GsonUtil.json2Obj(input, GsonUtil.statisticsRequestType);

			// TODO sostituire JSONObject con bean per le statistiche aggregate
			// per più nodi
			JSONObject res = StatisticsManager.getNodesStatistics(request.getNodesId(), request.getAggregationLevel(),
					request.getStartDate(), request.getEndDate());
			return Response.status(Response.Status.OK).entity(res.toString()).build();

		} catch (GsonUtilException e) {
			return handleBadRequestErrorResponse(e);
		} catch (Exception e) {
			return handleErrorResponse500(e);
		}
	}

	@POST
	@Secured
	@Path("/statistics/search")
	@Produces("application/json")
	public Response getSearchStatistics(final String input) {

		try {

			StatisticsRequest request = GsonUtil.json2Obj(input, GsonUtil.statisticsRequestType);

			// TODO sostituire JSONObject con bean per le statistiche aggregate
			// per più nodi
			JSONObject res = StatisticsManager.getSearchStatistics(request.getCountries(),
					request.getAggregationLevel(), request.getStartDate(), request.getEndDate());
			return Response.status(Response.Status.OK).entity(res.toString()).build();

		} catch (GsonUtilException e) {
			return handleBadRequestErrorResponse(e);
		} catch (Exception e) {
			return handleErrorResponse500(e);
		}

	}

	@POST
	@Secured
	@Path("/statistics/keyword")
	@Produces("application/json")
	public Response getKeywordStatistics(final String input) {

		try {

			String out = GsonUtil.obj2Json(StatisticsManager.getKeywordStatistics(),
					GsonUtil.keywordStatisticsResultListType);

			return Response.status(Response.Status.OK).entity(out).build();

		} catch (Exception e) {
			return handleErrorResponse500(e);
		}

	}

	@POST
	@Secured
	@Path("/statistics/catalogues/details")
	@Produces("application/json")
	public Response getODMSCataloguesStatisticsDetails(final String input) {
		try {

			StatisticsRequest request = GsonUtil.json2Obj(input, GsonUtil.statisticsRequestType);

			// TODO sostituire JSONObject con bean
			JSONObject res = StatisticsManager.getNodeStatisticsDetails(request.getNodesId(),
					request.getAggregationLevel(), request.getStartDate());

			return Response.status(Response.Status.OK).entity(res.toString()).build();

		} catch (GsonUtilException e) {
			return handleBadRequestErrorResponse(e);
		} catch (Exception e) {
			return handleErrorResponse500(e);
		}
	}

	@POST
	@Secured
	@Path("/statistics/search/details")
	@Produces("application/json")
	public Response getStatisticsDetails(final String input) {

		try {

			StatisticsRequest request = GsonUtil.json2Obj(input, GsonUtil.statisticsRequestType);

			// TODO sostituire JSONObject con bean
			JSONObject res = StatisticsManager.getSearchStatisticsDetails(request.getCountries(),
					request.getAggregationLevel(), request.getStartDate());
			return Response.status(Response.Status.OK).entity(res.toString()).build();

		} catch (GsonUtilException e) {
			return handleBadRequestErrorResponse(e);
		} catch (Exception e) {
			return handleErrorResponse500(e);
		}
	}

	@GET
	@Secured
	@Path("/catalogues/{nodeId}/messages")
	@Produces("application/json")
	public Response getODMSCatalogueMessages(@PathParam("nodeId") String nodeId) {

		int nodeID = Integer.parseInt(nodeId);
		try {
			ODMSManager.getODMSCatalogue(nodeID);
			List<ODMSCatalogueMessage> messageList = FederationCore.getODMSMessages(nodeID);

			return Response.status(Response.Status.OK)
					.entity(GsonUtil.obj2Json(messageList, GsonUtil.messageListType).toString()).build();
		} catch(ODMSCatalogueNotFoundException e) {
			return handleNodeNotFoundErrorResponse(e, nodeId);
		} catch (Exception e) {
			return handleErrorResponse500(e);
		}
	}

	@GET
	@Secured
	@Path("/catalogues/{nodeId}/messages/{messageID}")
	@Produces("application/json")
	public Response getODMSCatalogueMessage(@PathParam("nodeId") String nodeId, @PathParam("messageID") String messageID) {

		try {
			int nodeID = Integer.parseInt(nodeId);
			int message_id = Integer.parseInt(messageID);
			ODMSManager.getODMSCatalogue(nodeID);
			ODMSCatalogueMessage message = FederationCore.getODMSMessage(nodeID, message_id);

			return Response.status(Response.Status.OK)
					.entity(GsonUtil.obj2Json(message, GsonUtil.messageType).toString()).build();

		} catch(ODMSCatalogueNotFoundException e) {
			return handleNodeNotFoundErrorResponse(e, nodeId);
		} catch (NumberFormatException e) {
			return handleBadRequestErrorResponse(e);
		} catch (Exception e) {
			return handleErrorResponse500(e);
		}
	}

	@DELETE
	@Secured
	@Path("/catalogues/{nodeId}/messages/{messageID}")
	@Produces("application/json")
	public Response deleteODMSCatalogueMessage(@PathParam("nodeId") String nodeId,
			@PathParam("messageID") String messageID) {

		try {
			int nodeID = Integer.parseInt(nodeId);
			int message_id = Integer.parseInt(messageID);
			ODMSManager.getODMSCatalogue(nodeID);
			FederationCore.deleteODMSMessage(nodeID, message_id);
			return Response.status(Response.Status.OK).build();

		}catch(ODMSCatalogueNotFoundException e) {
			return handleNodeNotFoundErrorResponse(e, nodeId);
		} catch (NumberFormatException e) {
			return handleBadRequestErrorResponse(e);
		} catch (Exception e) {
			return handleErrorResponse500(e);
		}
	}

	@DELETE
	@Secured
	@Path("/catalogues/{nodeId}/messages")
	@Produces("application/json")
	public Response deleteODMSCatalogueMessages(@PathParam("nodeId") String nodeId) {

		int nodeID = Integer.parseInt(nodeId);
		try {
			
			ODMSManager.getODMSCatalogue(nodeID);
			FederationCore.deleteAllODMSMessage(nodeID);
			return Response.status(Response.Status.OK).build();
		
		}catch(ODMSCatalogueNotFoundException e) {
			return handleNodeNotFoundErrorResponse(e, nodeId);
		} catch (Exception e) {
			return handleErrorResponse500(e);
		}
	}

	@POST
	@Secured
	@Path("/logs")
	@Produces("application/json")
	public Response getLogs(final String input) {

		try {

			LogsRequest request = GsonUtil.json2Obj(input, GsonUtil.logRequestType);
			List<Log> logs = FederationCore.getLogs(request.getLevelList(), request.getStartDate(),
					request.getEndDate());

			return Response.status(Response.Status.OK).entity(GsonUtil.obj2Json(logs, GsonUtil.logsListType)).build();

		} catch (GsonUtilException e) {
			return handleBadRequestErrorResponse(e);
		} catch (Exception e) {
			return handleErrorResponse500(e);
		}

	}

	@GET
	@Secured
	@Path("/dcat-ap/dump/download")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response downloadDatasetDump(@Context HttpServletRequest httpRequest,
			@DefaultValue("false") @QueryParam("forceDump") Boolean forceDump,
			@DefaultValue("false") @QueryParam("zip") Boolean returnZip) {

		try {

			return Response
					.ok(DCATAPDumpManager.getDatasetDumpFromFile(null, forceDump, returnZip),
							MediaType.APPLICATION_OCTET_STREAM)
					.header("content-disposition",
							"attachment; filename = " + DCATAPDumpManager.dumpFileName + (returnZip ? ".zip" : ""))
					.build();

		} catch (Exception e) {
			return handleErrorResponse500(e);
		}

	}

	@GET
	@Secured
	@Path("/dcat-ap/dump")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getDatasetDump(@Context HttpServletRequest httpRequest,
			@DefaultValue("false") @QueryParam("forceDump") Boolean forceDump) {

		try {

			return Response.ok(DCATAPDumpManager.getDatasetDumpFromFile(null, forceDump, false)).build();

		} catch (Exception e) {
			return handleErrorResponse500(e);
		}

	}

	@GET
	@Secured
	@Path("/dcat-ap/dump/download/{nodeID}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response downloadDatasetDumpByODMSCatalogue(@Context HttpServletRequest httpRequest,
			@PathParam("nodeID") String nodeID, @DefaultValue("false") @QueryParam("forceDump") Boolean forceDump,
			@DefaultValue("false") @QueryParam("zip") Boolean returnZip) {

		try {

			return Response
					.ok(DCATAPDumpManager.getDatasetDumpFromFile(nodeID, forceDump, returnZip),
							MediaType.APPLICATION_OCTET_STREAM)
					.header("content-disposition",
							"attachment; filename = " + DCATAPDumpManager.dumpFileName
									+ (StringUtils.isBlank(nodeID) ? "" : new String("_node_" + nodeID))
									+ (returnZip ? ".zip" : ""))
					.build();

		} catch (Exception e) {
			return handleErrorResponse500(e);
		}

	}

	@GET
	@Secured
	@Path("/dcat-ap/dump/{nodeID}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getDatasetDumpByODMSCatalogue(@Context HttpServletRequest httpRequest,
			@DefaultValue("false") @QueryParam("forceDump") Boolean forceDump, @PathParam("nodeID") String nodeID) {

		try {

			return Response.ok(DCATAPDumpManager.getDatasetDumpFromFile(nodeID, forceDump, false)).build();

		} catch (Exception e) {
			return handleErrorResponse500(e);
		}

	}

	@DELETE
	@Path("/catalogues/{nodeID}/dataset/{datasetID}/distribution/{distributionID}/deleteDatalet/{dataletID}")
	@Secured
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteDataletFromDistribution(@Context HttpServletRequest httpRequest,
			@PathParam("nodeID") String nodeID, @PathParam("datasetID") String datasetID,
			@PathParam("distributionID") String distributionID, @PathParam("dataletID") String dataletID) {

		CachePersistenceManager jpa = new CachePersistenceManager();
		try {

			Datalet toRemove = jpa.jpaGetDataletByIDs(nodeID, datasetID, distributionID, dataletID);
			if (toRemove != null) {
				jpa.jpaDeleteDatalet(toRemove);
			}

			List<Datalet> remainingDatalet = jpa.jpaGetDataletByDistributionID(distributionID);
			if (remainingDatalet.size() == 0) {
				DCATDataset dataset = MetadataCacheManager.getDatasetByID(datasetID);
				dataset.getDistributions().stream().filter(x -> x.getId().equals(distributionID)).findFirst().get()
						.setHasDatalets(false);
				MetadataCacheManager.updateDatasetInsertDatalet(Integer.parseInt(nodeID), dataset);
			}

			return Response.ok().build();
		} catch (Exception e) {
			return handleErrorResponse500(e);
		} finally {
			jpa.jpaClose();
		}

	}

	@GET
	@Path("/datalets")
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllDatalet(@Context HttpServletRequest httpRequest) {

		try {
			CachePersistenceManager jpa = new CachePersistenceManager();
			List<Datalet> datalets = jpa.jpaGetAllDatalets();
			datalets.sort((n1, n2) -> n1.getId().compareTo(n2.getId()));
			return Response.ok(GsonUtil.obj2Json(datalets, GsonUtil.dataletListType)).build();
		} catch (Exception e) {
			return handleErrorResponse500(e);
		}

	}

	private static Response handleErrorResponseLogin(Exception e) {

		e.printStackTrace();
		ErrorResponse error = new ErrorResponse(String.valueOf(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()),
				e.getMessage(), e.getClass().getSimpleName(), e.getMessage());
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON)
				.entity(error.toJson()).build();
	}

	private static Response handleErrorResponse500(Exception e) {

		e.printStackTrace();
		ErrorResponse error = new ErrorResponse(String.valueOf(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()),
				e.getMessage(), e.getClass().getSimpleName(), "An error occurred, please contact the administrator!");
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON)
				.entity(error.toJson()).build();
	}

	private static Response handleBadRequestErrorResponse(Exception e) {

		e.printStackTrace();
		logger.error("Exception "+e.getLocalizedMessage());
		ErrorResponse error = new ErrorResponse(String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()),
				e.getMessage(), e.getClass().getSimpleName(), "The request body is not a valid JSON");
		return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(error.toJson())
				.build();
	}

	private static Response handlePrefixNotFoundErrorResponse(Exception e, String prefixId) {

		e.printStackTrace();
		logger.error("Prefix "+prefixId+" raised exception "+e.getLocalizedMessage());
		ErrorResponse error = new ErrorResponse(String.valueOf(Response.Status.NOT_FOUND.getStatusCode()),
				e.getMessage(), e.getClass().getSimpleName(), "No prefix found with id: " + prefixId);
		return Response.status(Response.Status.NOT_FOUND).type(MediaType.APPLICATION_JSON).entity(error.toJson())
				.build();
	}

	private static Response handleNodeNotFoundErrorResponse(Exception e, String nodeId) {

		logger.error("NodeID "+nodeId+" not found: "+e.getLocalizedMessage());
		ErrorResponse error = new ErrorResponse(String.valueOf(Response.Status.NOT_FOUND.getStatusCode()),
				e.getMessage(), e.getClass().getSimpleName(),
				"The ODMS node does not exist in the federation: " + nodeId);
		return Response.status(Response.Status.NOT_FOUND).type(MediaType.APPLICATION_JSON).entity(error.toJson())
				.build();
	}

	private static Response handleNodeHostNotFoundErrorResponse(Exception e, String nodeHost) {

		e.printStackTrace();
		logger.error("NodeHost "+nodeHost+" not found: "+e.getLocalizedMessage());
		ErrorResponse error = new ErrorResponse(String.valueOf(Response.Status.NOT_FOUND.getStatusCode()),
				e.getMessage(), e.getClass().getSimpleName(),
				"The ODMS node with host URL: " + nodeHost + " does not exist");
		return Response.status(Response.Status.NOT_FOUND).type(MediaType.APPLICATION_JSON).entity(error.toJson())
				.build();
	}

	private static Response handleNodeForbiddenErrorResponse(Exception e, String nodeHost) {

		e.printStackTrace();
		logger.error("NodeHost "+nodeHost+" forbidden: "+e.getLocalizedMessage());
		ErrorResponse error = new ErrorResponse(String.valueOf(Response.Status.FORBIDDEN.getStatusCode()),
				e.getMessage(), e.getClass().getSimpleName(),
				"The ODMS node with host URL: " + nodeHost + " is forbidden!");
		return Response.status(Response.Status.FORBIDDEN).type(MediaType.APPLICATION_JSON).entity(error.toJson())
				.build();
	}

	private static Response handleNodeOfflineErrorResponse(Exception e, String nodeHost) {

		e.printStackTrace();
		logger.error("NodeHost "+nodeHost+" offline: "+e.getLocalizedMessage());
		ErrorResponse error = new ErrorResponse(String.valueOf(Response.Status.FORBIDDEN.getStatusCode()),
				e.getMessage(), e.getClass().getSimpleName(),
				"The ODMS node with host URL: " + nodeHost + " is offline!");
		return Response.status(Response.Status.FORBIDDEN).type(MediaType.APPLICATION_JSON).entity(error.toJson())
				.build();
	}

}
