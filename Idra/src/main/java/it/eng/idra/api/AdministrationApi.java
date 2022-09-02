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

package it.eng.idra.api;

import it.eng.idra.authentication.AuthenticationManager;
import it.eng.idra.authentication.BasicAuthenticationManager;
import it.eng.idra.authentication.FiwareIdmAuthenticationManager;
import it.eng.idra.authentication.KeycloakAuthenticationManager;
import it.eng.idra.authentication.Secured;
import it.eng.idra.authentication.fiware.model.Token;
import it.eng.idra.authentication.fiware.model.UserInfo;
import it.eng.idra.authentication.keycloak.model.KeycloakUser;
import it.eng.idra.beans.Datalet;
import it.eng.idra.beans.ErrorResponse;
import it.eng.idra.beans.IdraAuthenticationMethod;
import it.eng.idra.beans.IdraProperty;
import it.eng.idra.beans.Log;
import it.eng.idra.beans.LogsRequest;
import it.eng.idra.beans.PasswordChange;
import it.eng.idra.beans.RdfPrefix;
import it.eng.idra.beans.RemoteCatalogue;
import it.eng.idra.beans.User;
import it.eng.idra.beans.dcat.DcatDataset;
import it.eng.idra.beans.odms.OdmsAlreadyPresentException;
import it.eng.idra.beans.odms.OdmsCatalogue;
import it.eng.idra.beans.odms.OdmsCatalogueChangeActiveStateException;
import it.eng.idra.beans.odms.OdmsCatalogueFederationLevel;
import it.eng.idra.beans.odms.OdmsCatalogueForbiddenException;
import it.eng.idra.beans.odms.OdmsCatalogueMessage;
import it.eng.idra.beans.odms.OdmsCatalogueNotFoundException;
import it.eng.idra.beans.odms.OdmsCatalogueOfflineException;
import it.eng.idra.beans.odms.OdmsCatalogueType;
import it.eng.idra.beans.odms.OdmsManagerException;
import it.eng.idra.beans.orion.OrionCatalogueConfiguration;
import it.eng.idra.beans.sparql.SparqlCatalogueConfiguration;
import it.eng.idra.beans.statistics.StatisticsRequest;
import it.eng.idra.cache.CachePersistenceManager;
import it.eng.idra.cache.MetadataCacheManager;
import it.eng.idra.dcat.dump.DcatApDumpManager;
import it.eng.idra.management.FederationCore;
import it.eng.idra.management.OdmsManager;
import it.eng.idra.management.RdfPrefixManager;
import it.eng.idra.management.StatisticsManager;
import it.eng.idra.utils.CommonUtil;
import it.eng.idra.utils.GsonUtil;
import it.eng.idra.utils.GsonUtilException;
import it.eng.idra.utils.PropertyManager;
import it.eng.idra.utils.restclient.RestClient;
import it.eng.idra.utils.restclient.RestClientImpl;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.core.StreamingOutput;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONException;
import org.json.JSONObject;

// TODO: Auto-generated Javadoc
/**
 * The Class AdministrationApi.
 */
@Path("/administration")
public class AdministrationApi {

  /** The logger. */
  private static Logger logger = LogManager.getLogger(AdministrationApi.class);

  /** The client. */
  private static Client client;
  
  private static String urlOrionmanager = 
      PropertyManager.getProperty(IdraProperty.ORION_MANAGER_URL);
  
  /**
   * getVersion.
   *
   * @return the version
   */
  @GET
  @Path("/version")
  @Produces("application/json")
  public Response getVersion() {

    JSONObject out = new JSONObject();
    out.put("idra_version", PropertyManager.getProperty(IdraProperty.IDRA_VERSION));
    out.put("idra_release_timestamp",
        PropertyManager.getProperty(IdraProperty.IDRA_RELEASE_TIMESTAMP));

    return Response.status(Response.Status.OK).entity(out.toString()).build();
  }

  /**
   * registerOdmsCatalogue.
   *
   * @param fileInputStream parameter
   * @param cdh             parameter
   * @param nodeString      parameter
   * @return the response
   */
  @POST
  @Secured
  @Path("/catalogues")
  @Consumes({ MediaType.MULTIPART_FORM_DATA })
  @Produces("application/json")
  public Response registerOdmsCatalogue(@FormDataParam("dump") InputStream fileInputStream,
      @FormDataParam("file") FormDataContentDisposition cdh,
      @FormDataParam("node") String nodeString) {
    OdmsCatalogue node = null;
    try {

      node = GsonUtil.json2Obj(nodeString, GsonUtil.nodeType);

      // If the node type is DCATDUMP, if the dump URL is blank, try to get the
      // dump from the uploaded file
      if (node.getNodeType().equals(OdmsCatalogueType.DCATDUMP)) {
        if (StringUtils.isBlank(node.getDumpUrl()) && StringUtils.isBlank(node.getDumpString())) {
          if (fileInputStream == null) {
            throw new IOException("The dump part of the request is empty");
          }

          String dumpString = IOUtils.toString(fileInputStream, StandardCharsets.UTF_8);
          if (StringUtils.isNotBlank(dumpString)) {
            node.setDumpString(dumpString);
          } else {
            throw new IOException(
                "The node must have either the dumpURL or dump file in the \" dump \" "
                    + "part of the multipart request");
          }
        } else if (StringUtils.isBlank(node.getDumpUrl())
            && StringUtils.isNotBlank(node.getDumpString())) {
          logger.info("Dump catalogue with dumpString");
        }
      } else {

        node.setDumpUrl(null);
        node.setDumpFilePath(null);
      }

      if (!node.getNodeType().equals(OdmsCatalogueType.WEB)) {
        node.setSitemap(null);
      }

      if (!node.getNodeType().equals(OdmsCatalogueType.ORION)
          && !node.getNodeType().equals(OdmsCatalogueType.SPARQL)) {
        node.setAdditionalConfig(null);
      } else if (node.getNodeType().equals(OdmsCatalogueType.ORION)) {

        if (!node.getFederationLevel().equals(OdmsCatalogueFederationLevel.LEVEL_4)) {
          ErrorResponse error = new ErrorResponse(
              String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()),
              "Orion Catalogue cannot synchronize its datasets, please set Federation Level 4!",
              "400",
              "Orion Catalogue cannot synchronize its datasets, please set Federation Level 4!");
          return Response.status(Response.Status.BAD_REQUEST).entity(error.toJson()).build();
        }

        if (node.getAdditionalConfig() == null) {
          ErrorResponse error = new ErrorResponse(
              String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()),
              "Orion Catalogue must have its configuration parameters!", "400",
              "Orion Catalogue must have its configuration parameters!");
          return Response.status(Response.Status.BAD_REQUEST).entity(error.toJson()).build();
        }
        OrionCatalogueConfiguration orionConfig = (OrionCatalogueConfiguration) node
            .getAdditionalConfig();
        if (orionConfig.isAuthenticated()) {
          if (StringUtils.isBlank(orionConfig.getOauth2Endpoint())
              || StringUtils.isBlank(orionConfig.getClientId())
              || StringUtils.isBlank(orionConfig.getClientSecret())) {
            ErrorResponse error = new ErrorResponse(
                String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()),
                "Please provide all of the authentication configuration parameters", "400",
                "Please provide all of the authentication configuration parameters");
            return Response.status(Response.Status.BAD_REQUEST).entity(error.toJson()).build();
          }
        }

        if (StringUtils.isBlank(orionConfig.getOrionDatasetDumpString())
            && fileInputStream == null) {
          throw new IOException("Orion Catalogue must have a dump string or a dump file");
        }

        if (StringUtils.isBlank(orionConfig.getOrionDatasetDumpString())) {
          String dumpString = IOUtils.toString(fileInputStream, StandardCharsets.UTF_8);
          if (StringUtils.isNotBlank(dumpString)) {
            orionConfig.setOrionDatasetDumpString(dumpString);
            node.setAdditionalConfig(orionConfig);
          }
        }
      } else if (node.getNodeType().equals(OdmsCatalogueType.SPARQL)) {

        if (!node.getFederationLevel().equals(OdmsCatalogueFederationLevel.LEVEL_4)) {
          ErrorResponse error = new ErrorResponse(
              String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()),
              "Sparql Catalogue cannot synchronize its datasets, please set Federation Level 4!",
              "400",
              "Sparql Catalogue cannot synchronize its datasets, please set Federation Level 4!");
          return Response.status(Response.Status.BAD_REQUEST).entity(error.toJson()).build();
        }

        SparqlCatalogueConfiguration sparqlConfig = (SparqlCatalogueConfiguration) node
            .getAdditionalConfig();

        if (sparqlConfig == null && fileInputStream == null) {
          throw new IOException("Sparql Catalogue must have a dump string or a dump file");
        }

        if (sparqlConfig == null) {
          sparqlConfig = new SparqlCatalogueConfiguration();
        }

        if (StringUtils.isBlank(sparqlConfig.getSparqlDatasetDumpString())) {
          String dumpString = IOUtils.toString(fileInputStream, StandardCharsets.UTF_8);
          if (StringUtils.isNotBlank(dumpString)) {
            sparqlConfig.setSparqlDatasetDumpString(dumpString);
            node.setAdditionalConfig(sparqlConfig);
          }
        }
      }

      if (node.isActive() == null) {
        node.setActive(false);
      }
      
      if (node.isFederatedInCb() == null) {
        node.setFederatedInCb(false);
      }
      

      if (node.isActive()) {
        FederationCore.registerOdmsCatalogue(node);
      } else {
        FederationCore.registerInactiveOdmsCatalogue(node);
      }

      return Response.status(Response.Status.OK).build();

    } catch (GsonUtilException | IOException e) {

      return handleBadRequestErrorResponse(e);

    } catch (OdmsAlreadyPresentException e) {

      logger.info(e.getMessage());
      ErrorResponse error = new ErrorResponse(
          String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()), e.getMessage(),
          e.getClass().getSimpleName(), "The node is already present in the federation!");
      return Response.status(Response.Status.BAD_REQUEST).entity(error.toJson()).build();

    } catch (OdmsCatalogueNotFoundException e) {
      return handleNodeHostNotFoundErrorResponse(e, node.getHost());
    } catch (OdmsCatalogueForbiddenException e) {
      return handleNodeForbiddenErrorResponse(e, node.getHost());
    } catch (OdmsCatalogueOfflineException e) {
      return handleNodeOfflineErrorResponse(e, node.getHost());
    } catch (Exception e) {
      return handleErrorResponse500(e);
    }
  }

  /**
   * getOdmsCatalogues.
   *
   * @param withImage parameter
   * @return the odms catalogues
   */
  @GET
  @Secured
  @Path("/catalogues")
  @Produces("application/json")
  public Response getOdmsCatalogues(@QueryParam("withImage") boolean withImage) {

    try {
      List<OdmsCatalogue> nodes = new ArrayList<OdmsCatalogue>(
          FederationCore.getOdmsCatalogues(withImage));

      try {
        HashMap<Integer, Long> messages = FederationCore.getAllOdmsMessagesCount();
        nodes.stream().forEach(node -> node.setMessageCount(messages.get(node.getId())));
      } catch (Exception e) {
        e.printStackTrace();
        nodes.stream().forEach(node -> node.setMessageCount(0L));
      }

      nodes.sort((n1, n2) -> n1.getId() - n2.getId());

      return Response.status(Response.Status.OK)
          .entity(GsonUtil.obj2Json(nodes, GsonUtil.nodeListType)).build();

    } catch (Exception e) {
      logger.error("Exception raised " + e.getLocalizedMessage());
      return handleErrorResponse500(e);
    }

  }

  /**
   * activateOdmsCatalogue.
   *
   * @param id parameter
   * @return the response
   */
  @PUT
  @Secured
  @Path("/catalogues/{id}/activate")
  @Produces("application/json")
  public Response activateOdmsCatalogue(@PathParam("id") String id) {
    OdmsCatalogue node = null;
    try {

      node = FederationCore.getOdmsCatalogue(Integer.parseInt(id));

      if (node.isActive()) {
        logger.error("Node " + node.getHost() + " already active");
        throw new OdmsCatalogueChangeActiveStateException(
            "Node " + node.getHost() + " already active");
      }

      if (node.getNodeType().equals(OdmsCatalogueType.DCATDUMP)) {
        if (StringUtils.isBlank(node.getDumpUrl()) && StringUtils.isBlank(node.getDumpString())
            && StringUtils.isNotBlank(node.getDumpFilePath())) {

          // Read the content of the file from the file system
          String dumpString = new String(Files.readAllBytes(Paths.get(node.getDumpFilePath())));
          node.setDumpString(dumpString);
        }
      }

      if (node.getNodeType().equals(OdmsCatalogueType.ORION)) {
        OrionCatalogueConfiguration conf = (OrionCatalogueConfiguration) node.getAdditionalConfig();
        if (StringUtils.isBlank(conf.getOrionDatasetDumpString())
            && StringUtils.isNotBlank(conf.getOrionDatasetFilePath())) {
          // Read the content of the file from the file system
          String dumpOrion = new String(
              Files.readAllBytes(Paths.get(conf.getOrionDatasetFilePath())));
          conf.setOrionDatasetDumpString(dumpOrion);
          node.setAdditionalConfig(conf);
        }
      }

      if (node.getNodeType().equals(OdmsCatalogueType.SPARQL)) {
        SparqlCatalogueConfiguration conf = (SparqlCatalogueConfiguration) node
            .getAdditionalConfig();
        if (StringUtils.isBlank(conf.getSparqlDatasetDumpString())
            && StringUtils.isNotBlank(conf.getSparqlDatasetFilePath())) {
          // Read the content of the file from the file system
          String dumpOrion = new String(
              Files.readAllBytes(Paths.get(conf.getSparqlDatasetFilePath())));
          conf.setSparqlDatasetDumpString(dumpOrion);
          node.setAdditionalConfig(conf);
        }
      }

      FederationCore.activateOdmsCatalogue(node);

      return Response.status(Response.Status.OK).build();

    } catch (OdmsCatalogueChangeActiveStateException e) {
      logger.error("Node " + node.getHost() + " raised: " + e.getLocalizedMessage());
      return handleBadRequestErrorResponse(e);
    } catch (Exception e) {
      logger.error("Node " + node.getHost() + " raised: " + e.getLocalizedMessage());
      return handleErrorResponse500(e);
    }
  }

  /**
   * deactivateOdmsCatalogue.
   *
   * @param id           parameter
   * @param keepDatasets parameter
   * @return the response
   */
  @PUT
  @Secured
  @Path("/catalogues/{id}/deactivate")
  @Produces("application/json")
  public Response deactivateOdmsCatalogue(@PathParam("id") String id,
      @QueryParam("keepDatasets") @DefaultValue("false") Boolean keepDatasets) {
    OdmsCatalogue node = null;
    try {

      node = FederationCore.getOdmsCatalogue(Integer.parseInt(id));
      if (!node.isActive()) {
        logger.error("Node " + node.getHost() + " already inactive");
        throw new OdmsCatalogueChangeActiveStateException(
            "Node " + node.getHost() + " already inactive");
      }

      FederationCore.deactivateOdmsCatalogue(node, keepDatasets);

      return Response.status(Response.Status.OK).build();

    } catch (OdmsCatalogueChangeActiveStateException e) {
      logger.error("Node " + node.getHost() + " raised: " + e.getLocalizedMessage());
      return handleBadRequestErrorResponse(e);
    } catch (Exception e) {
      logger.error("Node " + node.getHost() + " raised: " + e.getLocalizedMessage());
      return handleErrorResponse500(e);
    }
  }

  /**
   * getOdmsCatalogue.
   *
   * @param nodeId    parameter
   * @param withImage parameter
   * @return the odms catalogue
   */
  @GET
  @Secured
  @Path("/catalogues/{nodeId}")
  @Produces("application/json")
  public Response getOdmsCatalogue(@PathParam("nodeId") String nodeId,
      @QueryParam("withImage") boolean withImage) {

    try {

      OdmsCatalogue node = FederationCore.getOdmsCatalogue(Integer.parseInt(nodeId), withImage);

      if (node.getNodeType().equals(OdmsCatalogueType.DCATDUMP)) {
        if (StringUtils.isBlank(node.getDumpString())) {
          // Read the content of the file from the file system
          String dump = new String(Files.readAllBytes(Paths.get(node.getDumpFilePath())));
          node.setDumpString(dump);
        }
      }

      if (node.getNodeType().equals(OdmsCatalogueType.ORION)) {
        OrionCatalogueConfiguration conf = (OrionCatalogueConfiguration) node.getAdditionalConfig();
        if (StringUtils.isBlank(conf.getOrionDatasetDumpString())
            && StringUtils.isNotBlank(conf.getOrionDatasetFilePath())) {
          // Read the content of the file from the file system
          String dumpOrion = new String(
              Files.readAllBytes(Paths.get(conf.getOrionDatasetFilePath())));
          conf.setOrionDatasetDumpString(dumpOrion);
          node.setAdditionalConfig(conf);
        }
      }

      if (node.getNodeType().equals(OdmsCatalogueType.SPARQL)) {
        SparqlCatalogueConfiguration conf = (SparqlCatalogueConfiguration) node
            .getAdditionalConfig();
        if (StringUtils.isBlank(conf.getSparqlDatasetDumpString())
            && StringUtils.isNotBlank(conf.getSparqlDatasetFilePath())) {
          // Read the content of the file from the file system
          String dumpOrion = new String(
              Files.readAllBytes(Paths.get(conf.getSparqlDatasetFilePath())));
          conf.setSparqlDatasetDumpString(dumpOrion);
          node.setAdditionalConfig(conf);
        }
      }

      return Response.status(Response.Status.OK)
          .entity(GsonUtil.obj2Json(node, GsonUtil.nodeType).toString()).build();

    } catch (NumberFormatException e) {
      logger.error("NumberFormatException with parameter " + nodeId);
      return handleBadRequestErrorResponse(e);

    } catch (OdmsCatalogueNotFoundException | NullPointerException e) {
      logger.error("Exception " + e.getLocalizedMessage());
      return handleNodeNotFoundErrorResponse(e, nodeId);

    } catch (Exception e) {
      logger.error("Exception " + e.getLocalizedMessage());
      return handleErrorResponse500(e);

    }
  }

  /**
   * updateOdmsCatalogue.
   *
   * @param nodeId          parameter
   * @param fileInputStream parameter
   * @param cdh             parameter
   * @param nodeString      parameter
   * @return the response
   * @throws JSONException  the JSON exception
   * @throws ParseException the parse exception
   */
  @PUT
  @Secured
  @Path("/catalogues/{nodeId}")
  @Consumes({ MediaType.MULTIPART_FORM_DATA })
  @Produces("application/json")
  public Response updateOdmsCatalogue(@PathParam("nodeId") String nodeId,
      @FormDataParam("dump") InputStream fileInputStream,
      @FormDataParam("file") FormDataContentDisposition cdh,
      @FormDataParam("node") String nodeString) throws JSONException, ParseException {

    try {

      OdmsCatalogue requestNode = GsonUtil.json2Obj(nodeString, GsonUtil.nodeType);
      OdmsCatalogue currentNode = OdmsManager.getOdmsCatalogue(Integer.parseInt(nodeId));

      if (!requestNode.getNodeType().equals(currentNode.getNodeType())) {
        logger.error("Update node " + currentNode.getHost() + " type is not allowed");
        throw new Exception("Update node " + currentNode.getHost() + " type is not allowed");
      }

      if (!requestNode.isActive().equals(currentNode.isActive())) {
        logger.error("Update Active State for node " + currentNode.getHost() + " is not allowed");
        throw new OdmsCatalogueChangeActiveStateException(
            "Update Active State for node " + currentNode.getHost() + " is not allowed");
      }

      // TODO: Manage update of DCATDUMP catalogue dumpstring
      if (requestNode.getNodeType().equals(OdmsCatalogueType.DCATDUMP)) {
        if ((StringUtils.isBlank(currentNode.getDumpUrl())
            && StringUtils.isNotBlank(requestNode.getDumpUrl()))
            && (!requestNode.getDumpUrl().equals(currentNode.getDumpUrl()))) {
          logger.info("Updating the DUMP Url for node: " + currentNode.getHost());
        } else {
          String dumpString = IOUtils.toString(fileInputStream, StandardCharsets.UTF_8);
          if (StringUtils.isNotBlank(dumpString)) {
            logger.info("Updating dump file for node " + currentNode.getHost());
          } else if (StringUtils.isBlank(requestNode.getDumpFilePath())
              && StringUtils.isNotBlank(currentNode.getDumpFilePath())) {
            logger.info("Dump file path was empty for " + currentNode.getHost()
                + " , setting the previous");
            requestNode.setDumpFilePath(currentNode.getDumpFilePath());
          }
        }
      }

      if (requestNode.getNodeType().equals(OdmsCatalogueType.WEB)) {
        if (requestNode.getSitemap() == null) {
          logger.error("Sitemap was null, setting the previous for node " + currentNode.getHost());
          requestNode.setSitemap(currentNode.getSitemap());
        }
      } else {
        requestNode.setSitemap(null);
      }

      boolean rescheduleJob = false;
      if (requestNode.getNodeType().equals(OdmsCatalogueType.ORION)) {
        OrionCatalogueConfiguration c = (OrionCatalogueConfiguration) requestNode
            .getAdditionalConfig();
        String oldDump = new String(Files.readAllBytes(Paths.get(c.getOrionDatasetFilePath())));
        if (StringUtils.isBlank(c.getOrionDatasetDumpString())) {
          c.setOrionDatasetDumpString(oldDump);
          requestNode.setAdditionalConfig(c);
        } else {
          rescheduleJob = true;
        }
      }

      if (requestNode.getNodeType().equals(OdmsCatalogueType.SPARQL)) {
        SparqlCatalogueConfiguration c = (SparqlCatalogueConfiguration) requestNode
            .getAdditionalConfig();
        String oldDump = new String(Files.readAllBytes(Paths.get(c.getSparqlDatasetFilePath())));
        if (StringUtils.isBlank(c.getSparqlDatasetDumpString())) {
          c.setSparqlDatasetDumpString(oldDump);
          requestNode.setAdditionalConfig(c);
        } else {
          rescheduleJob = true;
        }
      }

      if (requestNode.getRefreshPeriod() != currentNode.getRefreshPeriod()) {
        rescheduleJob = true;
      }

      if (requestNode != null) {
        requestNode.setId(Integer.parseInt(nodeId));

        FederationCore.updateFederatedOdmsCatalogue(requestNode, rescheduleJob);

        return Response.status(Response.Status.OK).build();
      } else {
        logger.error("The request body is empty");
        throw new GsonUtilException("The request body is empty");
      }

    } catch (GsonUtilException | NumberFormatException
        | OdmsCatalogueChangeActiveStateException e) {
      return handleBadRequestErrorResponse(e);

    } catch (OdmsCatalogueNotFoundException e) {
      return handleNodeNotFoundErrorResponse(e, nodeId);
    } catch (Exception e) {
      return handleErrorResponse500(e);
    }

  }

  /**
   * unregisterOdmsCatalogue.
   *
   * @param nodeId parameter
   * @return the response
   */
  @DELETE
  @Secured
  @Path("/catalogues/{nodeId}")
  @Produces("application/json")
  public Response unregisterOdmsCatalogue(@PathParam("nodeId") String nodeId) {

    OdmsCatalogue node = null;
    try {

      node = FederationCore.getOdmsCatalogue(Integer.parseInt(nodeId));
      logger.info("Deleting ODMS catalogue with host: " + node.getHost() + " and id " + nodeId
          + " - START");

      logger.info("Deletion of the Catalogue also in the CB. "
          + "Calling the Broker Manager component.");
      HashMap<String, String> conf = FederationCore.getSettings();
      if (!conf.get("orionUrl").equals("")) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        RestClient client = new RestClientImpl();
        
        String api = urlOrionmanager + "deleteCatalogue";
        String data = "{ \"catalogueId\": \"" + node.getId() + "\", \"contextBrokerUrl\": \"" 
            + conf.get("orionUrl") + "\"  }";
        logger.info("Context Broker enabled, deleting NODEID: "  + data);
        
        HttpResponse response = client.sendPostRequest(api, data,
            MediaType.APPLICATION_JSON_TYPE, headers); 
        //        int status = client.getStatus(response);
        
        if (response == null) {
          logger.info("STATUS POST Delete in the CB, from BROKER MANAGER: -1" 
              + ". The NGSI-LD Broker Manager is not running.");
        } else {
          String body = client.getHttpResponseBody(response);
          JSONObject objResponse = new JSONObject(body);
          int status = objResponse.getInt("status");
          
          if (status != 200 && status != 207 && status != 204 
              && status != 201 && status != 301) {
            // the deletion was not successful, still federated in the CB
            node.setFederatedInCb(true);  
            if (status == 400) {
              logger.info("STATUS POST Delete in the CB, from BROKER MANAGER: " + status
                  + ". Bad Request. Deletion from the CB failed.");
            } else {
              logger.info("STATUS POST Delete in the CB, from BROKER MANAGER: " + status
                  + ". Deletion from the CB failed.");
            }
            //          throw new Exception("------------ STATUS POST DELETE "
            //              + "CATALOGUE ID - BROKER MANAGER: " + status);
          } else {
            node.setFederatedInCb(false); 
            logger.info("Catalogue deleted from the CB.");
          }  
        }
        
       
      } else {
        node.setFederatedInCb(false); 
        logger.info("Context Broker NOT enabled");
      }  
      
      
      FederationCore.unregisterOdmsCatalogue(node);
      logger.info(
          "Deleting ODMS node with id: " + node.getHost() + " and id " + nodeId + " - COMPLETE");
      
      if (node.getNodeType().equals(OdmsCatalogueType.NGSILD_CB)) {
        logger.info("Deletion of the Subscription of the Catalogue, if present");
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        RestClient client = new RestClientImpl();
 
        HttpResponse response = client.sendGetRequest(node.getHost() 
            + "/ngsi-ld/v1/subscriptions/urn:ngsi-ld:Subscription:" + node.getId(), headers);
        int status = client.getStatus(response);
        if (status == 200) {
          logger.info("The NGSI_LD Catalogue had a Subscription, deleting");
          response = client.sendDeleteRequest(node.getHost() 
              + "/ngsi-ld/v1/subscriptions/urn:ngsi-ld:Subscription:" + node.getId(), headers); 
         
          status = client.getStatus(response);
          if (status != 200 && status != 207 && status != 204 && status != -1 
              && status != 201 && status != 301) {
            throw new Exception("------------ STATUS POST DELETE "
                + "SUBSCRIPTION: " + status);
          }
        }


      }
      return Response.status(Response.Status.OK).build();

    } catch (NumberFormatException e) {
      return handleBadRequestErrorResponse(e);

    } catch (NullPointerException | OdmsCatalogueNotFoundException e) {
      return handleNodeNotFoundErrorResponse(e, nodeId);

    } catch (Exception e) {
      return handleErrorResponse500(e);
    }

  }

  /**
   * startOdmsCatalogueSynch.
   *
   * @param nodeId parameter
   * @return the response
   */
  @POST
  @Secured
  @Path("/catalogues/{nodeId}/synchronize")
  @Produces("application/json")
  public Response startOdmsCatalogueSynch(@PathParam("nodeId") String nodeId) {

    int nodeIdentifier = Integer.parseInt(nodeId);

    try {
      logger.info("Forcing the synchronization for node " + nodeId);
      FederationCore.startOdmsCatalogueSynch(nodeIdentifier);
      
      logger.info("Fine funzione sync");
      return Response.status(Response.Status.OK).build();

    } catch (OdmsCatalogueNotFoundException e) {
      return handleNodeNotFoundErrorResponse(e, nodeId);
    } catch (OdmsManagerException e) {
      return handleErrorResponse500(e);
    }

  }

  /**
   * setSettings.
   *
   * @param input parameter
   * @return the response
   */
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

  /**
   * getSettings.
   *
   * @return the settings
   */
  @GET
  @Path("/configuration")
  @Produces("application/json")
  public Response getSettings() {

    try {

      HashMap<String, String> conf = FederationCore.getSettings();

      return Response.status(Response.Status.OK)
          .entity(GsonUtil.obj2Json(conf, GsonUtil.configurationType)).build();

    } catch (Exception e) {
      return handleErrorResponse500(e);
    }

  }

  /**
   * setRemoteCatalogues.
   *
   * @param input parameter
   * @return the response
   */
  @POST
  @Secured
  @Path("/remoteCatalogue")
  @Consumes({ MediaType.APPLICATION_JSON })
  @Produces("application/json")
  public Response setRemoteCatalogues(final String input) {

    try {
      RemoteCatalogue remCat = GsonUtil.json2Obj(input, GsonUtil.remCatType);
      if (remCat.getPassword() != null) {
        // encrypt password
        String ecrPassword = CommonUtil.encrypt(remCat.getPassword());
        remCat.setPassword(ecrPassword);
      }

      FederationCore.setRemoteCatalogue(remCat);

      return Response.status(Response.Status.OK).build();

    } catch (GsonUtilException e) {
      return handleBadRequestErrorResponse(e);
    } catch (Exception e) {
      return handleErrorResponse500(e);
    }

  }

  /**
   * getRemoteCatalogue.
   *
   * @return the remote catalogue
   */
  @GET
  @Secured
  @Path("/remoteCatalogue")
  @Produces("application/json")
  public Response getRemoteCatalogue() {
    try {

      List<RemoteCatalogue> conf = FederationCore.getAllRemCatalogues();
      return Response.status(Response.Status.OK)
          .entity(GsonUtil.obj2Json(conf, GsonUtil.remCatListType)).build();

    } catch (Exception e) {
      return handleErrorResponse500(e);
    }

  }

  /**
   * deleteRemCat.
   *
   * @param rmId parameter
   * @return the response
   */
  @DELETE
  @Secured
  @Path("/remoteCatalogue/{rmId}")
  public Response deleteRemCat(@PathParam("rmId") String rmId) {

    try {

      FederationCore.deleteRemCat(Integer.parseInt(rmId));

      return Response.status(Response.Status.OK).build();

    } catch (NumberFormatException e) {
      return handleBadRequestErrorResponse(e);

    } catch (NullPointerException e) {
      return handlePrefixNotFoundErrorResponse(e, rmId);

    } catch (Exception e) {
      return handleErrorResponse500(e);

    }

  }

  /**
   * updateRemoteCat.
   *
   * @param rmId  parameter
   * @param input parameter
   * @return the response
   */
  @PUT
  @Secured
  @Path("/remoteCatalogue/{rmId}")
  @Consumes({ MediaType.APPLICATION_JSON })
  @Produces("application/json")
  public Response updateRemoteCat(@PathParam("rmId") String rmId, final String input) {

    try {

      RemoteCatalogue rm = GsonUtil.json2Obj(input, GsonUtil.remCatType);

      RemoteCatalogue oldRem = FederationCore.getRemCat(Integer.parseInt(rmId));
      logger.debug("passw old: " + oldRem.getPassword() + " pssw dopo la update del nome: "
          + rm.getPassword());
      if (((oldRem.getPassword() != null && rm.getPassword() != null)
          && (!(oldRem.getPassword().equals(rm.getPassword()))))
          || (oldRem.getPassword() == null && rm.getPassword() != null)) {
        // encrypt pssw
        logger.debug("encrypt password");
        String ecrPassword = CommonUtil.encrypt(rm.getPassword());
        rm.setPassword(ecrPassword);
      }

      rm.setId(Integer.parseInt(rmId));

      FederationCore.updateRemCat(rm);

      return Response.status(Response.Status.OK).build();

    } catch (NumberFormatException e) {
      return handleBadRequestErrorResponse(e);

    } catch (NullPointerException e) {
      return handlePrefixNotFoundErrorResponse(e, rmId);

    } catch (Exception e) {
      return handleErrorResponse500(e);
    }

  }

  /**
   * authRemoteCatalogue.
   *
   * @param id parameter
   * @return the response
   */
  @GET
  @Path("/remoteCatalogue/auth/{id}")
  @Consumes({ MediaType.APPLICATION_JSON })
  @Produces("text/plain")
  public Response authRemoteCatalogue(@PathParam("id") String id) {

    try {
      // -------------------------- Utenze in Idra
      RemoteCatalogue remCatalogue = FederationCore.getRemCat(Integer.parseInt(id));
      String username = remCatalogue.getUsername();
      // decrypt password
      String password = CommonUtil.decrypt(remCatalogue.getPassword());
      String basePath = remCatalogue.getUrl();

      client = ClientBuilder.newClient();
      String compiledUri = basePath + "Idra/api/v1/administration/login";
      WebTarget webTarget = client.target(compiledUri);
      Invocation.Builder builderLogin = webTarget.request();

      builderLogin = builderLogin.header("Content-Type", "application/json");

      Response responseLogin = builderLogin
          .post(Entity.entity("{username: " + username + ", password: " + password + "}",
              MediaType.APPLICATION_JSON_TYPE));

      StatusType statusLogin = responseLogin.getStatusInfo();
      if (statusLogin.getStatusCode() == 200) {
        logger.debug("Status POST LOGIN: 200 OK");
      } else {
        throw new Exception("Status code POST LOGIN: " + statusLogin.getStatusCode());
      }

      logger.debug(responseLogin);
      ResponseBuilder responseBuilder = Response.status(responseLogin.getStatus());
      final InputStream responseStream = (InputStream) responseLogin.getEntity();
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

      logger.debug("Response Login: " + responseBuilder.entity(output).build());

      // --------------------------------------------------------------------
      client = ClientBuilder.newClient();
      compiledUri = basePath + "Idra/api/v1/administration/catalogues?withImage=true";
      webTarget = client.target(compiledUri);
      Invocation.Builder builder = webTarget.request();
      // builder = builder.header("Authorization"," Bearer "+token);

      Response response = builder.get();
      final InputStream responseStream2 = (InputStream) response.getEntity();
      ResponseBuilder responseBuilder2 = Response.status(response.getStatus());

      StatusType status = response.getStatusInfo();
      if (status.getStatusCode() == 200) {
        logger.debug("200 OK");
      } else {
        throw new Exception("Status code: " + status.getStatusCode());
      }

      StreamingOutput output2 = new StreamingOutput() {
        @Override
        public void write(OutputStream out) throws IOException, WebApplicationException {
          int length;
          byte[] buffer = new byte[1024];
          while ((length = responseStream2.read(buffer)) != -1) {
            out.write(buffer, 0, length);
          }
          out.flush();
          responseStream.close();
        }
      };

      return responseBuilder2.entity(output2).build();

    } catch (GsonUtilException e) {
      return handleBadRequestErrorResponse(e);
    } catch (NullPointerException e) {
      return handleErrorResponseLogin(e);
    } catch (Exception e) {
      return handleErrorResponse500(e);
    }

  }

  /**
   * authRemoteCatalogueIdm.
   *
   * @param id parameter
   * @return the response
   */
  @GET
  @Path("/remoteCatalogue/authIDM/{id}")
  @Consumes({ MediaType.APPLICATION_JSON })
  @Produces("text/plain")
  public Response authRemoteCatalogueIdm(@PathParam("id") String id) {
    try {
      logger.debug(" -------------------------- Caso Login IDM FIWARE");
      RemoteCatalogue remCatalogue = FederationCore.getRemCat(Integer.parseInt(id));
      String clientId = remCatalogue.getClientId();
      String clientSecret = remCatalogue.getClientSecret();
      // decrypt password
      String portalUrl = remCatalogue.getPortal();

      client = ClientBuilder.newClient();
      String compiledUri = portalUrl + "oauth2/token";
      WebTarget webTarget = client.target(compiledUri);

      String auth = "Basic "
          + new String(Base64.getEncoder().encode((clientId + ":" + clientSecret).getBytes()));
      Invocation.Builder builder = webTarget.request();

      MultivaluedMap<String, Object> head = new MultivaluedHashMap<String, Object>();
      head.add("Content-Type", "application/x-www-form-urlencoded");
      head.add("Authorization", auth);
      builder.headers(head);

      String username = remCatalogue.getUsername();
      String password = CommonUtil.decrypt(remCatalogue.getPassword());
      MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
      formData.add("grant_type", "password");
      formData.add("username", username);
      formData.add("password", password);

      Response response = builder.post(Entity.form(formData));

      String entity = response.readEntity(String.class);
      JSONObject resJson = new JSONObject(entity);
      String accessToken = resJson.get("access_token").toString();

      StatusType status = response.getStatusInfo();
      if (status.getStatusCode() == 200) {
        logger.debug("Status POST: 200 OK");
      } else {
        throw new Exception("Status code POST: " + status.getStatusCode());
      }

      // ------------------------------------------------------------------
      String basePath = remCatalogue.getUrl();
      client = ClientBuilder.newClient();
      String compiledUri2 = basePath + "Idra/api/v1/administration/catalogues?withImage=true";
      WebTarget webTarget2 = client.target(compiledUri2);

      Invocation.Builder builder2 = webTarget2.request();
      builder2 = builder2.header("Authorization", " Bearer " + accessToken);

      Response response2 = builder2.get();

      final InputStream responseStream = (InputStream) response2.getEntity();
      ResponseBuilder responseBuilder = Response.status(response2.getStatus());

      StatusType status2 = response2.getStatusInfo();
      if (status2.getStatusCode() == 200) {
        logger.debug("Status GET: 200 OK");
      } else {
        throw new Exception("Status code GET: " + status2.getStatusCode());
      }

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
      return responseBuilder.build();

    } catch (GsonUtilException e) {
      return handleBadRequestErrorResponse(e);
    } catch (NullPointerException e) {
      return handleErrorResponseLogin(e);
    } catch (Exception e) {
      return handleErrorResponse500(e);
    }

  }

  /**
   * getPrefixes.
   *
   * @return the prefixes
   */
  @GET
  @Path("/prefixes")
  @Produces("application/json")
  public Response getPrefixes() {

    try {
      List<RdfPrefix> prefixes = RdfPrefixManager.getAllPrefixes();
      return Response.status(Response.Status.OK)
          .entity(GsonUtil.obj2Json(prefixes, GsonUtil.prefixListType)).build();
    } catch (Exception e) {
      return handleErrorResponse500(e);
    }
  }

  /**
   * getPrefix.
   *
   * @param prefixId parameter
   * @return the prefix
   */
  @GET
  @Secured
  @Path("/prefixes/{prefixId}")
  @Produces("application/json")
  public Response getPrefix(@PathParam("prefixId") String prefixId) {

    try {

      RdfPrefix prefix = RdfPrefixManager.getPrefix(Integer.parseInt(prefixId));

      return Response.status(Response.Status.OK)
          .entity(GsonUtil.obj2Json(prefix, GsonUtil.prefixType)).build();

    } catch (NumberFormatException e) {
      return handleBadRequestErrorResponse(e);

    } catch (NullPointerException e) {
      return handlePrefixNotFoundErrorResponse(e, prefixId);

    } catch (Exception e) {
      return handleErrorResponse500(e);
    }
  }

  /**
   * deletePrefix.
   *
   * @param prefixId parameter
   * @return the response
   */
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

  /**
   * updatePrefix.
   *
   * @param prefixId parameter
   * @param input    parameter
   * @return the response
   */
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

  /**
   * addPrefix.
   *
   * @param input parameter
   * @return the response
   */
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

  /**
   * loginGet.
   *
   * @param code        parameter
   * @param httpRequest parameter
   * @return the response
   */
  @GET
  @Path("/login")
  @Consumes({ MediaType.APPLICATION_JSON })
  @Produces("text/plain")
  public Response loginGet(@DefaultValue("") @QueryParam("code") String code,
      @Context HttpServletRequest httpRequest) {

    try {
      Object token = null;
      AuthenticationManager authInstance = AuthenticationManager.getActiveAuthenticationManager();

      switch (IdraAuthenticationMethod.valueOf(
          PropertyManager.getProperty(IdraProperty.AUTHENTICATION_METHOD))) {

        case FIWARE:

          if (StringUtils.isBlank(code)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
          }

          Token t = (Token) authInstance.login(null, null, code);
          UserInfo info = FiwareIdmAuthenticationManager.getInstance()
              .getUserInfo(t.getAccessToken());

          token = t.getAccessToken();

          String refreshToken = t.getRefreshToken();

          if (token != null && ((String) token).trim().length() > 0) {
            return Response.seeOther(URI.create(
                  PropertyManager.getProperty(IdraProperty.IDRA_CATALOGUE_BASEPATH)))
                  .cookie(new NewCookie("loggedin", (String) token, "/", "", "comment", 100, true))
                  .cookie(new NewCookie("refresh_token", refreshToken,
                      "/", "", "comment", 100, true))
                  .cookie(new NewCookie("username", info.getDisplayName(),
                      "/", "", "comment", 100, true)).build();
          } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
          }

        case KEYCLOAK:
          if (StringUtils.isBlank(code)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
          }

          Token k = (Token) authInstance.login(null, null, code);
          KeycloakUser keycloakUser = KeycloakAuthenticationManager.getInstance()
                .getUserInfo(k.getAccessToken());

          token = k.getAccessToken();

          refreshToken = k.getRefreshToken();

          if (token != null && ((String) token).trim().length() > 0) {
            return Response.seeOther(URI.create(
                  PropertyManager.getProperty(IdraProperty.IDRA_CATALOGUE_BASEPATH)))
                  .cookie(new NewCookie("loggedin", (String) token, "/", "", "comment", 100, true))
                  .cookie(new NewCookie("refresh_token", refreshToken,
                      "/", "", "comment", 100, true))
                  .cookie(new NewCookie("username", keycloakUser.getPreferredUsername(),
                      "/", "", "comment", 100, true)).build();
          } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
          }

        default:
          String input = IOUtils.toString(httpRequest.getInputStream(), Charset.defaultCharset());
          User user = GsonUtil.json2Obj(input, GsonUtil.userType);
          token = (String) authInstance.login(user.getUsername(), user.getPassword(), null);
          return Response.status(Response.Status.OK).entity(token).build();
      }

    } catch (GsonUtilException e) {
      return handleBadRequestErrorResponse(e);
    } catch (NullPointerException e) {
      return handleErrorResponseLogin(e);
    } catch (Exception e) {
      return handleErrorResponse500(e);
    }

  }

  /**
   * loginPost.
   *
   * @param httpRequest parameter
   * @return the response
   */
  @POST
  @Path("/login")
  @Consumes({ MediaType.APPLICATION_JSON })
  @Produces("text/plain")
  public Response loginPost(@Context HttpServletRequest httpRequest) {

    try {
      Object token = null;
      AuthenticationManager authInstance = AuthenticationManager.getActiveAuthenticationManager();

      switch (IdraAuthenticationMethod.valueOf(
          PropertyManager.getProperty(IdraProperty.AUTHENTICATION_METHOD))) {

        case FIWARE:
          String code = httpRequest.getParameter("code");
          if (StringUtils.isBlank(code)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
          }

          Token t = (Token) authInstance.login(null, null, code);
          UserInfo info = FiwareIdmAuthenticationManager.getInstance()
              .getUserInfo(t.getAccessToken());

          token = t.getAccessToken();
          token = (String) token;

          String refreshToken = t.getRefreshToken();

          if (token != null && ((String) token).trim().length() > 0) {
            HttpSession session = httpRequest.getSession();
            session.setAttribute("loggedin", token);
            session.setAttribute("refresh_token", refreshToken);
            session.setAttribute("username", info.getDisplayName());
          }

          return Response
            .temporaryRedirect(URI.create(
                httpRequest.getContextPath() 
                + PropertyManager.getProperty(IdraProperty.IDRA_CATALOGUE_BASEPATH)))
            .build();

        case KEYCLOAK:
          code = httpRequest.getParameter("code");
          if (StringUtils.isBlank(code)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
          }

          Token k = (Token) authInstance.login(null, null, code);
          KeycloakUser keycloakUser = KeycloakAuthenticationManager.getInstance()
              .getUserInfo(k.getAccessToken());

          token = k.getAccessToken();

          refreshToken = k.getRefreshToken();

          if (token != null && ((String) token).trim().length() > 0) {
            return Response.seeOther(URI.create(
                PropertyManager.getProperty(IdraProperty.IDRA_CATALOGUE_BASEPATH)))
              .cookie(new NewCookie("loggedin", (String) token, "/", "", "comment", 100, true))
              .cookie(new NewCookie("refresh_token", refreshToken, "/", "", "comment", 100, true))
              .cookie(new NewCookie("username", keycloakUser.getPreferredUsername(),
                  "/", "", "comment", 100, true)).build();
          } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
          }
      
        default:
          String input = IOUtils.toString(httpRequest.getInputStream(), Charset.defaultCharset());
          User user = GsonUtil.json2Obj(input, GsonUtil.userType);
          token = (String) authInstance.login(user.getUsername(), user.getPassword(), null);
          break;
      }

      return Response.status(Response.Status.OK).entity(token).build();

    } catch (GsonUtilException e) {
      return handleBadRequestErrorResponse(e);
    } catch (NullPointerException e) {
      return handleErrorResponseLogin(e);
    } catch (Exception e) {
      return handleErrorResponse500(e);
    }

  }

  /**
   * updatePassword.
   *
   * @param input parameter
   * @return the response
   */
  @PUT
  @Path("/updatePassword")
  @Secured
  @Consumes({ MediaType.APPLICATION_JSON })
  @Produces("application/json")
  public Response updatePassword(final String input) {

    try {

      PasswordChange passChange = GsonUtil.json2Obj(input, PasswordChange.class);

      if (!passChange.getNewPassword().equals(passChange.getNewPasswordConfirm())) {
        ErrorResponse error = new ErrorResponse(
            String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()),
            "Password and password confirm are not equal", "PasswordsNotEqual",
            "Password and password confirm are not equal");
        return Response.status(Response.Status.BAD_REQUEST).entity(error.toJson()).build();
      }

      if (BasicAuthenticationManager.validatePassword(passChange.getUsername(),
          passChange.getOldPassword())) {

        BasicAuthenticationManager.updateUserPassword(passChange.getUsername(),
            passChange.getNewPassword());

        JSONObject out = new JSONObject();
        out.append("message", "Password successfully updated!");
        return Response.status(Response.Status.OK).entity(out.toString()).build();

      } else {

        ErrorResponse error = new ErrorResponse(
            String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()), "Wrong Old Password!",
            "WrongOldPassword", "Wrong Old Password!");
        return Response.status(Response.Status.BAD_REQUEST).entity(error.toJson()).build();
      }
    } catch (Exception e) {
      return handleErrorResponse500(e);
    }
  }

  /**
   * logout.
   *
   * @param httpRequest parameter
   * @return the response
   */
  @POST
  @Path("/logout")
  @Secured
  @Consumes({ MediaType.APPLICATION_JSON })
  @Produces("application/json")
  public Response logout(@Context HttpServletRequest httpRequest) {

    try {

      // switch
      // (IdraAuthenticationMethod.valueOf(PropertyManager
      // .getProperty(IdraProperty.AUTHENTICATION_METHOD)))
      // {

      // case FIWARE:
      // return authInstance.logout(httpRequest);
      // default:
      AuthenticationManager authInstance = AuthenticationManager.getActiveAuthenticationManager();

      return authInstance.logout(httpRequest);
      // }

      // return Response.status(Response.Status.OK).build();

    } catch (GsonUtilException e) {
      return handleBadRequestErrorResponse(e);
    } catch (Exception e) {
      return handleErrorResponse500(e);
    }

  }

  /**
   * verifyToken.
   *
   * @return the response
   */
  @GET
  @Secured
  @Path("/verifyToken")
  @Produces("application/json")
  public Response verifyToken() {

    return Response.status(Response.Status.OK).build();

  }

  /**
   * getAllCountries.
   *
   * @return the all countries
   */
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

  /**
   * getMinDateCataloguesStat.
   *
   * @return the min date catalogues stat
   */
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

  /**
   * getOdmsCataloguesStatistics.
   *
   * @param input parameter
   * @return the odms catalogues statistics
   */
  @POST
  @Secured
  @Path("/statistics/catalogues")
  @Produces("application/json")
  public Response getOdmsCataloguesStatistics(final String input) {

    try {

      StatisticsRequest request = GsonUtil.json2Obj(input, GsonUtil.statisticsRequestType);

      // TODO sostituire JSONObject con bean per le statistiche aggregate
      // per più nodi
      JSONObject res = StatisticsManager.getNodesStatistics(request.getNodesId(),
          request.getAggregationLevel(), request.getStartDate(), request.getEndDate());
      return Response.status(Response.Status.OK).entity(res.toString()).build();

    } catch (GsonUtilException e) {
      return handleBadRequestErrorResponse(e);
    } catch (Exception e) {
      return handleErrorResponse500(e);
    }
  }

  /**
   * Gets the search statistics.
   *
   * @param input the input
   * @return the search statistics
   */
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

  /**
   * Gets the keyword statistics.
   *
   * @param input the input
   * @return the keyword statistics
   */
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

  /**
   * Gets the odms catalogues statistics details.
   *
   * @param input the input
   * @return the odms catalogues statistics details
   */
  @POST
  @Secured
  @Path("/statistics/catalogues/details")
  @Produces("application/json")
  public Response getOdmsCataloguesStatisticsDetails(final String input) {
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

  /**
   * Gets the statistics details.
   *
   * @param input the input
   * @return the statistics details
   */
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

  /**
   * Gets the odms catalogue messages.
   *
   * @param nodeId the node id
   * @return the odms catalogue messages
   */
  @GET
  @Secured
  @Path("/catalogues/{nodeId}/messages")
  @Produces("application/json")
  public Response getOdmsCatalogueMessages(@PathParam("nodeId") String nodeId) {

    int nodeIdentifier = Integer.parseInt(nodeId);
    try {
      OdmsManager.getOdmsCatalogue(nodeIdentifier);
      List<OdmsCatalogueMessage> messageList = FederationCore.getOdmsMessages(nodeIdentifier);

      return Response.status(Response.Status.OK)
          .entity(GsonUtil.obj2Json(messageList, GsonUtil.messageListType).toString()).build();
    } catch (OdmsCatalogueNotFoundException e) {
      return handleNodeNotFoundErrorResponse(e, nodeId);
    } catch (Exception e) {
      return handleErrorResponse500(e);
    }
  }

  /**
   * Gets the odms catalogue message.
   *
   * @param nodeId    the node id
   * @param messageId the message id
   * @return the odms catalogue message
   */
  @GET
  @Secured
  @Path("/catalogues/{nodeId}/messages/{messageID}")
  @Produces("application/json")
  public Response getOdmsCatalogueMessage(@PathParam("nodeId") String nodeId,
      @PathParam("messageID") String messageId) {

    try {
      int nodeIdentifier = Integer.parseInt(nodeId);
      int messageIdentifier = Integer.parseInt(messageId);
      OdmsManager.getOdmsCatalogue(nodeIdentifier);
      OdmsCatalogueMessage message = FederationCore.getOdmsMessage(nodeIdentifier,
          messageIdentifier);

      return Response.status(Response.Status.OK)
          .entity(GsonUtil.obj2Json(message, GsonUtil.messageType).toString()).build();

    } catch (OdmsCatalogueNotFoundException e) {
      return handleNodeNotFoundErrorResponse(e, nodeId);
    } catch (NumberFormatException e) {
      return handleBadRequestErrorResponse(e);
    } catch (Exception e) {
      return handleErrorResponse500(e);
    }
  }

  /**
   * Delete odms catalogue message.
   *
   * @param nodeId    the node id
   * @param messageId the message id
   * @return the response
   */
  @DELETE
  @Secured
  @Path("/catalogues/{nodeId}/messages/{messageID}")
  @Produces("application/json")
  public Response deleteOdmsCatalogueMessage(@PathParam("nodeId") String nodeId,
      @PathParam("messageID") String messageId) {

    try {
      int nodeIdentifier = Integer.parseInt(nodeId);
      int messageIdentifier = Integer.parseInt(messageId);
      OdmsManager.getOdmsCatalogue(nodeIdentifier);
      FederationCore.deleteOdmsMessage(nodeIdentifier, messageIdentifier);
      return Response.status(Response.Status.OK).build();

    } catch (OdmsCatalogueNotFoundException e) {
      return handleNodeNotFoundErrorResponse(e, nodeId);
    } catch (NumberFormatException e) {
      return handleBadRequestErrorResponse(e);
    } catch (Exception e) {
      return handleErrorResponse500(e);
    }
  }

  /**
   * Delete odms catalogue messages.
   *
   * @param nodeId the node id
   * @return the response
   */
  @DELETE
  @Secured
  @Path("/catalogues/{nodeId}/messages")
  @Produces("application/json")
  public Response deleteOdmsCatalogueMessages(@PathParam("nodeId") String nodeId) {

    int nodeIdentifier = Integer.parseInt(nodeId);
    try {

      OdmsManager.getOdmsCatalogue(nodeIdentifier);
      FederationCore.deleteAllOdmsMessage(nodeIdentifier);
      return Response.status(Response.Status.OK).build();

    } catch (OdmsCatalogueNotFoundException e) {
      return handleNodeNotFoundErrorResponse(e, nodeId);
    } catch (Exception e) {
      return handleErrorResponse500(e);
    }
  }

  /**
   * Gets the logs.
   *
   * @param input the input
   * @return the logs
   */
  @POST
  @Secured
  @Path("/logs")
  @Produces("application/json")
  public Response getLogs(final String input) {

    try {

      LogsRequest request = GsonUtil.json2Obj(input, GsonUtil.logRequestType);
      List<Log> logs = FederationCore.getLogs(request.getLevelList(), request.getStartDate(),
          request.getEndDate());

      return Response.status(Response.Status.OK)
          .entity(GsonUtil.obj2Json(logs, GsonUtil.logsListType)).build();

    } catch (GsonUtilException e) {
      return handleBadRequestErrorResponse(e);
    } catch (Exception e) {
      return handleErrorResponse500(e);
    }

  }

  /**
   * Download global dcat ap dump.
   *
   * @param httpRequest the http request
   * @param forceDump   the force dump
   * @param returnZip   the return zip
   * @return the response
   */
  @GET
  @Secured
  @Path("/dcat-ap/dump/download")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  public Response downloadGlobalDcatApDump(@Context HttpServletRequest httpRequest,
      @DefaultValue("false") @QueryParam("forceDump") Boolean forceDump,
      @DefaultValue("false") @QueryParam("zip") Boolean returnZip) {

    try {

      return Response
          .ok(DcatApDumpManager.getDatasetDumpFromFile(null, forceDump, returnZip),
              MediaType.APPLICATION_OCTET_STREAM)
          .header("content-disposition", "attachment; filename = "
              + DcatApDumpManager.globalDumpFileName + (returnZip ? ".zip" : ""))
          .build();

    } catch (Exception e) {
      return handleErrorResponse500(e);
    }

  }

  /**
   * Gets the global dcat ap dump.
   *
   * @param httpRequest the http request
   * @param forceDump   the force dump
   * @return the global dcat ap dump
   */
  @GET
  @Secured
  @Path("/dcat-ap/dump")
  @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Response getGlobalDcatApDump(@Context HttpServletRequest httpRequest,
      @DefaultValue("false") @QueryParam("forceDump") Boolean forceDump) {

    try {

      return Response.ok(DcatApDumpManager.getDatasetDumpFromFile(null, forceDump, false)).build();

    } catch (Exception e) {
      return handleErrorResponse500(e);
    }

  }

  /**
   * Download catalogue dcat ap dump.
   *
   * @param httpRequest    the http request
   * @param nodeIdentifier the node identifier
   * @param forceDump      the force dump
   * @param returnZip      the return zip
   * @return the response
   */
  @GET
  @Secured
  @Path("/dcat-ap/dump/download/{nodeID}")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  public Response downloadCatalogueDcatApDump(@Context HttpServletRequest httpRequest,
      @PathParam("nodeID") String nodeIdentifier,
      @DefaultValue("false") @QueryParam("forceDump") Boolean forceDump,
      @DefaultValue("false") @QueryParam("zip") Boolean returnZip) {

    try {

      return Response
          .ok(DcatApDumpManager.getDatasetDumpFromFile(nodeIdentifier, forceDump, returnZip),
              MediaType.APPLICATION_OCTET_STREAM)
          .header("content-disposition", "attachment; filename = "
              + DcatApDumpManager.globalDumpFileName
              + (StringUtils.isBlank(nodeIdentifier) ? "" : new String("_node_" + nodeIdentifier))
              + (returnZip ? ".zip" : ""))
          .build();

    } catch (Exception e) {
      return handleErrorResponse500(e);
    }

  }

  /**
   * Gets the catalogue dcat ap dump.
   *
   * @param httpRequest    the http request
   * @param forceDump      the force dump
   * @param nodeIdentifier the node identifier
   * @return the catalogue dcat ap dump
   */
  @GET
  @Secured
  @Path("/dcat-ap/dump/{nodeID}")
  @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Response getCatalogueDcatApDump(@Context HttpServletRequest httpRequest,
      @DefaultValue("false") @QueryParam("forceDump") Boolean forceDump,
      @PathParam("nodeID") String nodeIdentifier) {

    try {

      return Response.ok(DcatApDumpManager.getDatasetDumpFromFile(nodeIdentifier, forceDump, false))
          .build();

    } catch (Exception e) {
      return handleErrorResponse500(e);
    }

  }

  /**
   * Delete datalet from distribution.
   *
   * @param httpRequest            the http request
   * @param nodeIdentifier         the node identifier
   * @param datasetIdentifier      the dataset identifier
   * @param distributionIdentifier the distribution identifier
   * @param dataletIdentifier      the datalet identifier
   * @return the response
   */
  @DELETE
  @Path("/catalogues/{nodeID}/dataset/{datasetID}"
      + "/distribution/{distributionID}/deleteDatalet/{dataletID}")
  @Secured
  @Consumes({ MediaType.APPLICATION_JSON })
  @Produces(MediaType.APPLICATION_JSON)
  public Response deleteDataletFromDistribution(@Context HttpServletRequest httpRequest,
      @PathParam("nodeID") String nodeIdentifier, @PathParam("datasetID") String datasetIdentifier,
      @PathParam("distributionID") String distributionIdentifier,
      @PathParam("dataletID") String dataletIdentifier) {

    CachePersistenceManager jpa = new CachePersistenceManager();
    try {

      Datalet toRemove = jpa.jpaGetDataletByIds(nodeIdentifier, datasetIdentifier,
          distributionIdentifier, dataletIdentifier);
      if (toRemove != null) {
        jpa.jpaDeleteDatalet(toRemove);
      }

      List<Datalet> remainingDatalet = jpa.jpaGetDataletByDistributionId(distributionIdentifier);
      if (remainingDatalet.size() == 0) {
        DcatDataset dataset = MetadataCacheManager.getDatasetById(datasetIdentifier);
        dataset.getDistributions().stream().filter(x -> x.getId().equals(distributionIdentifier))
            .findFirst().get().setHasDatalets(false);
        MetadataCacheManager.updateDatasetInsertDatalet(Integer.parseInt(nodeIdentifier), dataset);
      }

      return Response.ok().build();
    } catch (Exception e) {
      return handleErrorResponse500(e);
    } finally {
      jpa.jpaClose();
    }

  }

  /**
   * Gets the all datalet.
   *
   * @param httpRequest the http request
   * @return the all datalet
   */
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

  /**
   * Handle error response login.
   *
   * @param e the e
   * @return the response
   */
  private static Response handleErrorResponseLogin(Exception e) {

    e.printStackTrace();
    ErrorResponse error = new ErrorResponse(
        String.valueOf(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()), e.getMessage(),
        e.getClass().getSimpleName(), e.getMessage());
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON)
        .entity(error.toJson()).build();
  }

  /**
   * Handle error response 500.
   *
   * @param e the e
   * @return the response
   */
  private static Response handleErrorResponse500(Exception e) {

    e.printStackTrace();
    ErrorResponse error = new ErrorResponse(
        String.valueOf(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()), e.getMessage(),
        e.getClass().getSimpleName(), "An error occurred, please contact the administrator!");
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON)
        .entity(error.toJson()).build();
  }

  /**
   * Handle bad request error response.
   *
   * @param e the e
   * @return the response
   */
  private static Response handleBadRequestErrorResponse(Exception e) {

    e.printStackTrace();
    logger.error("Exception " + e.getLocalizedMessage());
    ErrorResponse error = new ErrorResponse(
        String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()), e.getMessage(),
        e.getClass().getSimpleName(), "The request body is not a valid JSON");
    return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON)
        .entity(error.toJson()).build();
  }

  /**
   * Handle prefix not found error response.
   *
   * @param e        the e
   * @param prefixId the prefix id
   * @return the response
   */
  private static Response handlePrefixNotFoundErrorResponse(Exception e, String prefixId) {

    e.printStackTrace();
    logger.error("Prefix " + prefixId + " raised exception " + e.getLocalizedMessage());
    ErrorResponse error = new ErrorResponse(
        String.valueOf(Response.Status.NOT_FOUND.getStatusCode()), e.getMessage(),
        e.getClass().getSimpleName(), "No prefix found with id: " + prefixId);
    return Response.status(Response.Status.NOT_FOUND).type(MediaType.APPLICATION_JSON)
        .entity(error.toJson()).build();
  }

  /**
   * Handle node not found error response.
   *
   * @param e      the e
   * @param nodeId the node id
   * @return the response
   */
  private static Response handleNodeNotFoundErrorResponse(Exception e, String nodeId) {

    logger.error("NodeID " + nodeId + " not found: " + e.getLocalizedMessage());
    ErrorResponse error = new ErrorResponse(
        String.valueOf(Response.Status.NOT_FOUND.getStatusCode()), e.getMessage(),
        e.getClass().getSimpleName(), "The ODMS node does not exist in the federation: " + nodeId);
    return Response.status(Response.Status.NOT_FOUND).type(MediaType.APPLICATION_JSON)
        .entity(error.toJson()).build();
  }

  /**
   * Handle node host not found error response.
   *
   * @param e        the e
   * @param nodeHost the node host
   * @return the response
   */
  private static Response handleNodeHostNotFoundErrorResponse(Exception e, String nodeHost) {

    e.printStackTrace();
    logger.error("NodeHost " + nodeHost + " not found: " + e.getLocalizedMessage());
    ErrorResponse error = new ErrorResponse(
        String.valueOf(Response.Status.NOT_FOUND.getStatusCode()), e.getMessage(),
        e.getClass().getSimpleName(),
        "The ODMS node with host URL: " + nodeHost + " does not exist");
    return Response.status(Response.Status.NOT_FOUND).type(MediaType.APPLICATION_JSON)
        .entity(error.toJson()).build();
  }

  /**
   * Handle node forbidden error response.
   *
   * @param e        the e
   * @param nodeHost the node host
   * @return the response
   */
  private static Response handleNodeForbiddenErrorResponse(Exception e, String nodeHost) {

    e.printStackTrace();
    logger.error("NodeHost " + nodeHost + " forbidden: " + e.getLocalizedMessage());
    ErrorResponse error = new ErrorResponse(
        String.valueOf(Response.Status.FORBIDDEN.getStatusCode()), e.getMessage(),
        e.getClass().getSimpleName(),
        "The ODMS node with host URL: " + nodeHost + " is forbidden!");
    return Response.status(Response.Status.FORBIDDEN).type(MediaType.APPLICATION_JSON)
        .entity(error.toJson()).build();
  }

  /**
   * Handle node offline error response.
   *
   * @param e        the e
   * @param nodeHost the node host
   * @return the response
   */
  private static Response handleNodeOfflineErrorResponse(Exception e, String nodeHost) {

    e.printStackTrace();
    logger.error("NodeHost " + nodeHost + " offline: " + e.getLocalizedMessage());
    ErrorResponse error = new ErrorResponse(
        String.valueOf(Response.Status.FORBIDDEN.getStatusCode()), e.getMessage(),
        e.getClass().getSimpleName(), "The ODMS node with host URL: " + nodeHost + " is offline!");
    return Response.status(Response.Status.FORBIDDEN).type(MediaType.APPLICATION_JSON)
        .entity(error.toJson()).build();
  }

}
