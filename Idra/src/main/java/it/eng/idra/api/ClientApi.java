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

import it.eng.idra.beans.DataEntity;
import it.eng.idra.beans.Datalet;
import it.eng.idra.beans.ErrorResponse;
import it.eng.idra.beans.EuroVocLanguage;
import it.eng.idra.beans.IdraProperty;
import it.eng.idra.beans.Notification;
import it.eng.idra.beans.OrderBy;
import it.eng.idra.beans.OrderType;
import it.eng.idra.beans.dcat.DcatApFormat;
import it.eng.idra.beans.dcat.DcatApProfile;
import it.eng.idra.beans.dcat.DcatApWriteType;
import it.eng.idra.beans.dcat.DcatDataset;
import it.eng.idra.beans.dcat.DcatDistribution;
import it.eng.idra.beans.dcat.DcatProperty;
import it.eng.idra.beans.dcat.DctStandard;
import it.eng.idra.beans.dcat.FoafAgent;
import it.eng.idra.beans.dcat.SkosConceptTheme;
import it.eng.idra.beans.dcat.SkosPrefLabel;
import it.eng.idra.beans.dcat.VcardOrganization;
import it.eng.idra.beans.exception.DatasetNotFoundException;
import it.eng.idra.beans.exception.DistributionNotFoundException;
import it.eng.idra.beans.exception.EuroVocTranslationNotFoundException;
import it.eng.idra.beans.odms.OdmsCatalogue;
import it.eng.idra.beans.odms.OdmsCatalogueNotFoundException;
import it.eng.idra.beans.odms.OdmsCatalogueType;
import it.eng.idra.beans.odms.OdmsManagerException;
import it.eng.idra.beans.odms.OdmsSynchLock;
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
import it.eng.idra.dcat.dump.DcatApSerializer;
import it.eng.idra.management.FederationCore;
import it.eng.idra.management.StatisticsManager;
import it.eng.idra.scheduler.job.OdmsSynchJob;
import it.eng.idra.search.FederatedSearch;
import it.eng.idra.search.SparqlFederatedSearch;
import it.eng.idra.utils.CommonUtil;
import it.eng.idra.utils.GsonUtil;
import it.eng.idra.utils.GsonUtilException;
import it.eng.idra.utils.NgsiLdCbDcatDeserializer;
import it.eng.idra.utils.PropertyManager;
import it.eng.idra.utils.RedirectFilter;
import it.eng.idra.utils.restclient.RestClient;
import it.eng.idra.utils.restclient.RestClientImpl;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.apache.http.HttpResponse;
import org.apache.jena.query.QueryParseException;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.tika.parser.txt.CharsetDetector;
import org.glassfish.jersey.client.ClientProperties;
import org.json.JSONArray;
import org.json.JSONObject;

// TODO: Auto-generated Javadoc
/**
 * The Class ClientApi.
 */
@Path("/client")
public class ClientApi {

  /** The logger. */
  private static Logger logger = LogManager.getLogger(ClientApi.class);

  /** The client. */
  private static Client client;
  
  /**
   * Receives a Notify from the CB.
   *
   * @param nodeId the id of the catalogue
   * @param apiKey the apiKey of the catalogue
   * @param n the notification
   * @return the response
   * @throws Exception exception 
   */
  @POST
  @Path("/notification/{nodeId}/{apiKey}/push")
  @Consumes({ MediaType.APPLICATION_JSON })
  @Produces("application/json")
  public Response receiveNotify(@PathParam("nodeId") String nodeId, 
      @PathParam("apiKey") String apiKey, 
      final String n) throws Exception {
    
    OdmsCatalogue node = FederationCore.getOdmsCatalogue(Integer.parseInt(nodeId), false);
    logger.info("Catalogue ID about the Notification from the CB: " + nodeId);

    if (node.getNodeType().equals(OdmsCatalogueType.NGSILD_CB) 
        && node.getApiKey().equals(apiKey)) {
      
      Notification notification = GsonUtil.json2Obj(n, GsonUtil.notifcation);
      
      DataEntity[] data = notification.getData();
       
      //Each date field is a dataset that has received a change 
      //and therefore needs to be updated in Idra
      for (int i = 0; i < data.length; i++) {

        String ngsiEntitytId = data[i].getId();
        logger.info("Updated/added entityID in the CB: " + ngsiEntitytId);
        logger.info("Ready to be Updated/added in Idra");
        
        // Call to the CB to obtain the entire Dataset
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        RestClient client = new RestClientImpl();
        
        // CASE 1. The notification concerns the modification/addition of a Dataset
        if (data[i].getType().equals("Dataset")) {

          HttpResponse response = client.sendGetRequest(node.getHost() 
              + "/ngsi-ld/v1/entities?type=Dataset&id=" + ngsiEntitytId, headers);
          
          int status = client.getStatus(response);
          if (status != 200 && status != 207 && status != 204 && status != -1 
              && status != 201 && status != 301) {
            throw new Exception("------------ STATUS search Dataset in Orion"
                + " after a notify: " + status);
          }
          String returnedJson = client.getHttpResponseBody(response); 
          DcatDataset datasetToUpdateFromOrion = NgsiLdCbDcatDeserializer
              .getDatasetFromJson(returnedJson, node);

          try {
            DcatDataset datasetToUpdateInIdra = MetadataCacheManager.getDatasetByIdentifier(Integer
                .parseInt(nodeId), ngsiEntitytId);
            logger.info("Dataset already present with ID: " + datasetToUpdateInIdra.getId()
                + ", to be updated");  
            
            datasetToUpdateFromOrion.setId(datasetToUpdateInIdra.getId());

            MetadataCacheManager.updateDataset(Integer.parseInt(nodeId), 
                datasetToUpdateFromOrion);
            
            logger.info("Dataset " + datasetToUpdateFromOrion.getTitle().getValue() 
                + " updated in Idra");

          } catch (DatasetNotFoundException ex) {
            logger.info(ex.getMessage() + "\n - Adding the new dataset -\n");

            OdmsSynchJob.addDataset(node, datasetToUpdateFromOrion); 
            node.setDatasetCount(node.getDatasetCount() + 1);
            
          }
        }
        
        // CASE 2. The notification concerns the modification/addition of a Distribution
        if (data[i].getType().equals("DistributionDCAT-AP")) {
          
          HttpResponse response = client.sendGetRequest(node.getHost() 
              + "/ngsi-ld/v1/entities?type=DistributionDCAT-AP&id=" + ngsiEntitytId, headers);
          
          int status = client.getStatus(response);
          if (status != 200 && status != 207 && status != 204 && status != -1 
              && status != 201 && status != 301) {
            throw new Exception("STATUS search Distribution in Orion"
                + " after a notify: " + status);
          }
          String returnedJson = client.getHttpResponseBody(response); 
          JSONArray distribArray = new JSONArray(returnedJson);
          
          // Getting the DcatDistribution
          DcatDistribution distributionFromCb = NgsiLdCbDcatDeserializer
              .distributionToDcat(distribArray.get(0), node);
          
          logger.info("Distribution which has been modified in the CB: " 
              + distributionFromCb.getTitle().getValue());
          
          String datasetIdentif = "";
          for (DcatDataset ds : MetadataCacheManager.getAllDatasetsByOdmsCatalogue(node.getId())) {
            for (DcatDistribution d : ds.getDistributions()) {
              if (d.getIdentifier().getValue().equals(ngsiEntitytId)) {
                datasetIdentif = ds.getIdentifier().getValue();
              }
            }
          }
                    
          // N.B. If you ADD in the CB a Distribution whose id is not present in any Dataset,
          // it is not possible to create its DcatDistribution in Idra.
          // As soon as a Dataset will be added/modified in the CB which in its Distribution list
          // presents the Id of the new Distribution, then the DcatDistribution
          // will be created in Idra.
          // You must therefore always create the Distribution in the CB first and then the
          // Dataset that contains it.
          if (datasetIdentif == "") {
            logger.info("The Distribution added in the CB is not present in any Dataset in Idra. "
                + "Exit.");
            return Response.status(Response.Status.OK).build();
          }

          DcatDataset datasetToUpdateInIdra = MetadataCacheManager
              .getDatasetByIdentifier(node.getId(), datasetIdentif);
          
          logger.info("Dataset to which the distribution belongs: " 
              + datasetToUpdateInIdra.getTitle().getValue());
          
          List<DcatDistribution> distributions = datasetToUpdateInIdra.getDistributions();
          List<DcatDistribution> distributionsToUpdate = new ArrayList<DcatDistribution>();

          for (DcatDistribution dis : distributions) {
            if (!(dis.getIdentifier().getValue().equals(ngsiEntitytId))) {
              dis.setId(null);
              distributionsToUpdate.add(dis);
            }
            if ((dis.getIdentifier().getValue().equals(ngsiEntitytId))) {
              
              logger.info("Modified Distribution: " 
                  + dis.getTitle().getValue() + " with: " 
                  + distributionFromCb.getTitle().getValue());

              distributionFromCb.setId(null);
              distributionFromCb.setIdentifier(ngsiEntitytId);
              distributionsToUpdate.add(distributionFromCb);
            }
          }

          List<String> doc = new ArrayList<String>();
          for (DcatProperty prop : datasetToUpdateInIdra.getDocumentation()) {
            doc.add(prop.getValue());
          }
          List<String> hasVersion = new ArrayList<String>();
          for (DcatProperty prop : datasetToUpdateInIdra.getHasVersion()) {
            hasVersion.add(prop.getValue());
          }
          List<String> isVersionOf = new ArrayList<String>();
          for (DcatProperty prop : datasetToUpdateInIdra.getIsVersionOf()) {
            isVersionOf.add(prop.getValue());
          }
          List<String> language = new ArrayList<String>();
          for (DcatProperty prop : datasetToUpdateInIdra.getLanguage()) {
            language.add(prop.getValue());
          }
          List<String> provenance = new ArrayList<String>();
          for (DcatProperty prop : datasetToUpdateInIdra.getProvenance()) {
            provenance.add(prop.getValue());
          }
          List<String> otherIdentifier = new ArrayList<String>();
          for (DcatProperty prop : datasetToUpdateInIdra.getOtherIdentifier()) {
            otherIdentifier.add(prop.getValue());
          }
          List<String> sample = new ArrayList<String>();
          for (DcatProperty prop : datasetToUpdateInIdra.getSample()) {
            sample.add(prop.getValue());
          }
          List<String> source = new ArrayList<String>();
          for (DcatProperty prop : datasetToUpdateInIdra.getSource()) {
            source.add(prop.getValue());
          }
          List<String> versionNotes = new ArrayList<String>();
          for (DcatProperty prop : datasetToUpdateInIdra.getVersionNotes()) {
            versionNotes.add(prop.getValue());
          }
          
          List<VcardOrganization> contactPointList = new ArrayList<VcardOrganization>();
          List<VcardOrganization> contactPointListOld = datasetToUpdateInIdra.getContactPoint();
          for (int k = 0; k < contactPointListOld.size(); k++) {
            contactPointList.add(
                new VcardOrganization(DCAT.contactPoint.getURI(), 
                    null, "", contactPointListOld.get(k).getHasEmail().getValue(),
                    "", "", "", String.valueOf(node.getId())));
          }

          List<SkosConceptTheme> themeList =  new ArrayList<SkosConceptTheme>();
          List<SkosConceptTheme> themeListOld = datasetToUpdateInIdra.getTheme();
          List<String> themes = new ArrayList<String>();
          for (int k = 0; k < themeListOld.size(); k++) {
            for (SkosPrefLabel lab : themeListOld.get(k).getPrefLabel()) {
              themes.add(lab.getValue());
            }
          }
          themeList.addAll(NgsiLdCbDcatDeserializer.extractConceptList(DCAT.theme.getURI(), 
              themes, SkosConceptTheme.class, node));

          List<String> keywords = datasetToUpdateInIdra.getKeywords();

          List<DctStandard> conformsTo = datasetToUpdateInIdra.getConformsTo();
          FoafAgent publisherOld = datasetToUpdateInIdra.getPublisher();
          FoafAgent publisher = new FoafAgent(DCTerms.publisher.getURI(), "",
              publisherOld.getName().getValue(), "", "", null,
              "", String.valueOf(node.getId()));
          FoafAgent creatorOld = datasetToUpdateInIdra.getCreator();
          FoafAgent creator = new FoafAgent(DCTerms.creator.getURI(), "",
              creatorOld.getName().getValue(), "", "", null,
              "", String.valueOf(node.getId()));
          
          DcatDataset datasetUpdated = new DcatDataset(String.valueOf(node.getId()), 
              datasetToUpdateInIdra.getIdentifier().getValue(), 
              datasetToUpdateInIdra.getTitle().getValue(), 
              datasetToUpdateInIdra.getDescription().getValue(), 
              distributionsToUpdate, themeList, publisher, 
              contactPointList, keywords, 
              datasetToUpdateInIdra.getAccessRights().getValue(), conformsTo, 
              doc, datasetToUpdateInIdra.getFrequency().getValue(),
              hasVersion, isVersionOf, 
              datasetToUpdateInIdra.getLandingPage().getValue(), language, 
              provenance, datasetToUpdateInIdra.getReleaseDate().getValue(), 
              datasetToUpdateInIdra.getUpdateDate().getValue(), otherIdentifier, 
              sample, source, 
              datasetToUpdateInIdra.getSpatialCoverage(), 
              datasetToUpdateInIdra.getTemporalCoverage(), 
              datasetToUpdateInIdra.getType().getValue(), 
              datasetToUpdateInIdra.getVersion().getValue(),
              versionNotes, datasetToUpdateInIdra.getRightsHolder(), 
              creator, datasetToUpdateInIdra.getSubject(), null);
          
          logger.info("Updated Dataset, to be inserted in Idra: " + datasetUpdated
              .getTitle().getValue());
          
          MetadataCacheManager.updateDataset(Integer.parseInt(nodeId), 
              datasetUpdated);
          
          //OdmsSynchJob.updateDataset(node, datasetUpdated);
          
          logger.info("Dataset after notification on a Distribution, "
              + " updated in Idra");
        } 
      }
    }
    return Response.status(Response.Status.OK).build();
  }

  /**
   * Search dataset.
   *
   * @param httpRequest the http request
   * @param input       the input
   * @return the response
   */
  @POST
  @Path("/search")
  @Consumes({ MediaType.APPLICATION_JSON })
  @Produces({ MediaType.APPLICATION_JSON, "application/n-triples", "application/rdf+xml",
      "text/turtle", "text/n3" })
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
      if (httpRequest.getHeader("X-FORWARDED-FOR") == null) {
        ipAddress = httpRequest.getRemoteAddr();
      }

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
          if (eurovocFilter.getSourceLanguage() != null) {
            searchParameters.put("sourceLanguage", eurovocFilter.getSourceLanguage().name());
          }
          if (eurovocFilter.getTargetLanguages() != null) {
            searchParameters.put("targetLanguages", eurovocFilter.getTargetLanguages().stream()
                .map(EuroVocLanguage::name).collect(Collectors.joining(",")));
          }
        }

        // Handles issued and modified parameters in input
        if ((issued = request.getReleaseDate()) != null) {
          String[] issuedArray = new String[2];
          issuedArray[0] = CommonUtil.formatDate(issued.getStart());
          issuedArray[1] = CommonUtil.formatDate(issued.getEnd());
          searchParameters.put("releaseDate", issuedArray);
        }
        if ((modified = request.getUpdateDate()) != null) {
          String[] modifiedArray = new String[2];
          modifiedArray[0] = CommonUtil.formatDate(modified.getStart());
          modifiedArray[1] = CommonUtil.formatDate(modified.getEnd());
          searchParameters.put("updateDate", modifiedArray);
        }

        // Adds filters parameters in input to the search HashMap
        filters.stream()
            .forEach(filter -> searchParameters.put(filter.getField(), (Object) filter.getValue()));

        // Adds rows, start, sort parameters
        searchParameters.put("rows", request.getRows());
        searchParameters.put("start", request.getStart());
        searchParameters.put("sort", request.getSort().getField().trim() + ","
            + request.getSort().getMode().toString().trim());

        if (searchParameters.containsKey("datasetThemes")) {
          List<String> tmp = Arrays
              .asList(((String) searchParameters.remove("datasetThemes")).split(",")).stream()
              .distinct().collect(Collectors.toList());
          List<String> themeAbbr = tmp.stream().filter(x -> FederationCore.isDcatTheme(x))
              .collect(Collectors.toList());
          if (!themeAbbr.isEmpty()) {
            searchParameters.put("datasetThemes", String.join(",", themeAbbr));
          } else {
            searchParameters.put("datasetThemes", Arrays.asList());
          }
        }

        // Adds the id of the nodes to search on
        List<Integer> ids = new ArrayList<Integer>();
        if (searchParameters.containsKey("catalogues")) {

          List<String> catalogues = Arrays
              .asList(((String) searchParameters.remove("catalogues")).split(",")).stream()
              .distinct().collect(Collectors.toList());

          ids = catalogues.stream().map(x -> FederationCore.getOdmsCatalogueIdbyName(x)).distinct()
              .collect(Collectors.toList());
        } else {

          ids = request.getNodes();
        }

        // Search only on active nodes
        ids = FederationCore.getActiveOdmsCataloguesId(ids);
        searchParameters.put("nodes", ids);

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
      return handleEuroVocNotFound(e);
    } catch (Exception e) {
      return handleErrorResponse500(e);
    }

  }

  /**
   * Search dataset dcat ap.
   *
   * @param httpRequest the http request
   * @param input       the input
   * @param format      the format
   * @param profile     the profile
   * @return the response
   */
  @POST
  @Path("/search/dcat-ap")
  @Consumes({ MediaType.APPLICATION_JSON })
  @Produces({ MediaType.APPLICATION_JSON, "application/n-triples", "application/rdf+xml",
      "text/turtle", "text/n3" })

  public Response searchDatasetDcatAp(@Context HttpServletRequest httpRequest, final String input,
      @DefaultValue("RDFXML") @QueryParam("format") DcatApFormat format,
      @DefaultValue("DCATAP") @QueryParam("profile") DcatApProfile profile) {

    SearchRequest request = null;
    Boolean liveSearch = null;
    SearchEuroVocFilter eurovocFilter = null;

    SearchDateFilter issued = null;
    SearchDateFilter modified = null;

    try {

      request = GsonUtil.json2Obj(input, GsonUtil.searchRequestType);

      // Gets the source IP address from HTTPRequest
      String ipAddress = null;
      if (httpRequest.getHeader("X-FORWARDED-FOR") == null) {
        ipAddress = httpRequest.getRemoteAddr();
      }

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
          if (eurovocFilter.getSourceLanguage() != null) {
            searchParameters.put("sourceLanguage", eurovocFilter.getSourceLanguage().name());
          }
          if (eurovocFilter.getTargetLanguages() != null) {
            searchParameters.put("targetLanguages", eurovocFilter.getTargetLanguages().stream()
                .map(EuroVocLanguage::name).collect(Collectors.joining(",")));
          }
        }

        // Handles issued and modified parameters in input
        if ((issued = request.getReleaseDate()) != null) {
          String[] issuedArray = new String[2];
          issuedArray[0] = CommonUtil.formatDate(issued.getStart());
          issuedArray[1] = CommonUtil.formatDate(issued.getEnd());
          searchParameters.put("releaseDate", issuedArray);
        }
        if ((modified = request.getUpdateDate()) != null) {
          String[] modifiedArray = new String[2];
          modifiedArray[0] = CommonUtil.formatDate(modified.getStart());
          modifiedArray[1] = CommonUtil.formatDate(modified.getEnd());
          searchParameters.put("updateDate", modifiedArray);
        }

        // Adds filters parameters in input to the search HashMap
        filters.stream()
            .forEach(filter -> searchParameters.put(filter.getField(), (Object) filter.getValue()));

        // Adds rows, start, sort parameters
        searchParameters.put("rows", request.getRows());
        searchParameters.put("start", request.getStart());
        searchParameters.put("sort", request.getSort().getField().trim() + ","
            + request.getSort().getMode().toString().trim());

        // Adds the id of the nodes to search on
        List<Integer> ids = new ArrayList<Integer>();
        if (searchParameters.containsKey("catalogues")) {

          List<String> catalogues = Arrays
              .asList(((String) searchParameters.remove("catalogues")).split(",")).stream()
              .distinct().collect(Collectors.toList());

          ids = catalogues.stream().map(x -> FederationCore.getOdmsCatalogueIdbyName(x)).distinct()
              .collect(Collectors.toList());
        } else {

          ids = request.getNodes();
        }

        // Search only on active nodes
        ids = FederationCore.getActiveOdmsCataloguesId(ids);
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
        String dcatResult = DcatApSerializer.searchResultToDcatAp(result, format,
            profile != null ? profile : DcatApProfile.DCATAP, DcatApWriteType.STRING);
        // Adds search statistics
        StatisticsManager.searchStatistics(ipAddress, liveSearch ? "live" : "cache");

        // try {
        return Response.status(Response.Status.OK).type(format.mediaType()).entity(dcatResult)
            .build();
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
      return handleEuroVocNotFound(e);
    } catch (Exception e) {
      return handleErrorResponse500(e);
    }

  }

  /**
   * Count dataset.
   *
   * @param httpRequest the http request
   * @param input       the input
   * @return the response
   */
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
          if (eurovocFilter.getSourceLanguage() != null) {
            searchParameters.put("sourceLanguage", eurovocFilter.getSourceLanguage().name());
          }
          if (eurovocFilter.getTargetLanguages() != null) {
            searchParameters.put("targetLanguages", eurovocFilter.getTargetLanguages().stream()
                .map(EuroVocLanguage::name).collect(Collectors.joining(",")));
          }
        }

        // Handles issued and modified parameters in input
        if ((issued = request.getReleaseDate()) != null) {
          String[] issuedArray = new String[2];
          issuedArray[0] = CommonUtil.formatDate(issued.getStart());
          issuedArray[1] = CommonUtil.formatDate(issued.getEnd());
          searchParameters.put("releaseDate", issuedArray);
        }
        if ((modified = request.getUpdateDate()) != null) {
          String[] modifiedArray = new String[2];
          modifiedArray[0] = CommonUtil.formatDate(modified.getStart());
          modifiedArray[1] = CommonUtil.formatDate(modified.getEnd());
          searchParameters.put("updateDate", modifiedArray);
        }

        // Adds filters parameters in input to the search HashMap
        filters.stream()
            .forEach(filter -> searchParameters.put(filter.getField(), (Object) filter.getValue()));

        // if ((!j.getString("filter").equals("rows") ||
        // j.getString("filter").equals("start")))
        // searchParameters.put(j.getString("filter"), (Object)
        // j.getString("text"));

        // Adds rows, start, sort parameters
        searchParameters.put("rows", request.getRows());
        searchParameters.put("start", request.getStart());
        searchParameters.put("sort", request.getSort().getField().trim() + ","
            + request.getSort().getMode().toString().trim());

        // Adds the id of the nodes to search on
        List<Integer> ids = new ArrayList<Integer>();
        if (searchParameters.containsKey("catalogues")) {

          List<String> catalogues = Arrays
              .asList(((String) searchParameters.remove("catalogues")).split(",")).stream()
              .distinct().collect(Collectors.toList());

          ids = catalogues.stream().map(x -> FederationCore.getOdmsCatalogueIdbyName(x)).distinct()
              .collect(Collectors.toList());
        } else {

          ids = request.getNodes();
        }

        // Search only on active nodes
        ids = FederationCore.getActiveOdmsCataloguesId(ids);
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

  /**
   * Run sparql query.
   *
   * @param httpRequest the http request
   * @param input       the input
   * @return the response
   */
  @POST
  @Path("/sparql/query")
  @Consumes({ MediaType.APPLICATION_JSON })
  @Produces("application/json")
  public Response runSparqlQuery(@Context HttpServletRequest httpRequest, final String input) {

    try {
      // Gets the source IP address from HTTPRequest
      String ipAddress = null;
      if (httpRequest.getHeader("X-FORWARDED-FOR") == null) {
        ipAddress = httpRequest.getRemoteAddr();
      }

      SparqlSearchRequest request = GsonUtil.json2Obj(input, GsonUtil.sparqlSearchRequestType);

      String queryResult = SparqlFederatedSearch.runQuery(request.getQuery(), request.getFormat());

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

  /**
   * Download from uri.
   *
   * @param httpRequest    the http request
   * @param url            the url
   * @param distributionId the distribution id
   * @param format         the format
   * @param downloadFile   the download file
   * @param isPreview      the is preview
   * @return the response
   */
  @GET
  @Path("/downloadFromUri")
  public Response downloadFromUri(@Context HttpServletRequest httpRequest,
      @QueryParam("url") String url, @QueryParam("id") String distributionId,
      @QueryParam("format") String format,
      @QueryParam("downloadFile") @DefaultValue("true") boolean downloadFile,
      @QueryParam("isPreview") @DefaultValue("false") boolean isPreview) {

    try {
      MetadataCacheManager.getDistribution(distributionId, url);

      logger.info("Download file API: " + downloadFile);
      String compiledUri = url;
      // client = ClientBuilder.newBuilder().readTimeout(10,
      // TimeUnit.SECONDS).build();
      int timeout = Integer.parseInt(PropertyManager.getProperty(IdraProperty.PREVIEW_TIMEOUT))
          * 1000;
      client = ClientBuilder.newClient().register(RedirectFilter.class);
      client.property(ClientProperties.CONNECT_TIMEOUT, timeout);
      client.property(ClientProperties.READ_TIMEOUT, timeout);

      try {
        WebTarget webTarget = client.target(compiledUri);
        Response request = webTarget.request().get();
        logger.info("File uri: " + compiledUri);
        logger.info("File format: " + format);
        ResponseBuilder responseBuilder = Response.status(request.getStatus());
        if (downloadFile) {

          if (StringUtils.isNotBlank(format) && format.toLowerCase().contains("csv")) {
            InputStream stream = new BufferedInputStream((InputStream) request.getEntity());
            CharsetDetector charDetector = new CharsetDetector();
            charDetector.setText(stream);
            responseBuilder.entity(new InputStreamReader(stream, charDetector.detect().getName()));
          } else {
            responseBuilder.entity(new StreamingOutput() {
              @Override
              public void write(OutputStream output) throws IOException, WebApplicationException {
                // TODO Auto-generated method stub
                IOUtils.copy((InputStream) request.getEntity(), output);
                // output.flush();
                output.close();
              }
            });
          }
        }

        MultivaluedMap<String, Object> headers = request.getHeaders();
        Set<String> keys = headers.keySet();
        logger.info("Status: " + request.getStatus());

        logger.debug(compiledUri);

        if (isPreview) {
          try {
            // TO-DO: renderlo configurabile
            long previewLimit = Integer.parseInt(
                PropertyManager.getProperty(IdraProperty.PREVIEW_TIMEOUT)) * 1024
                * 1024; // 10MB
            long dimension = 0L;
            for (String k : keys) {

              if (k.toLowerCase().contains("content-length")) {
                logger.debug("Content-Length");
                logger.debug(headers.get(k).get(0));
                dimension = Long.parseLong((String) headers.get(k).get(0));
                break;
              } else if (k.toLowerCase().contains("content-range")) {
                logger.debug("Content-Range");
                logger.debug(headers.get(k));
                logger.debug(headers.get(k).get(0).toString());
                logger.debug(headers.get(k).get(0).toString().split("/")[1].replaceFirst("]", ""));
                dimension = Long.parseLong(
                    (String) headers.get(k).get(0).toString().split("/")[1].replaceFirst("]", ""));
                break;
              }

            }

            if (dimension > previewLimit) {
              responseBuilder = Response.status(Status.REQUEST_ENTITY_TOO_LARGE);
            }

            // if(dimension==0L || dimension>previewLimit) {
            // responseBuilder = Response.status(Status.REQUEST_ENTITY_TOO_LARGE);
            // }

          } catch (NumberFormatException ex) {
            // System.out.println("Unable to retrieve the dimension of the element");
            logger.error("Unable to retrieve the dimension of the element");
            responseBuilder = Response.status(Status.REQUEST_ENTITY_TOO_LARGE);
          }

        }

        // System.out.println("--------------------------------------------\n");
        // responseBuilder.header("Access-Control-Allow-Origin", "*");
        responseBuilder.header("original-file-format", format);
        responseBuilder.encoding("UTF-8");
        return responseBuilder.build();

      } catch (Exception e) {
        e.printStackTrace();
        return handleErrorResponse500(e);

      } finally {
        // request.close();
        client.close();
      }

    } catch (DistributionNotFoundException | IOException | SolrServerException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
      return handleErrorResponse500(e1);
    }

  }

  /**
   * Creates the datalet from distribution.
   *
   * @param httpRequest            the http request
   * @param input                  the input
   * @param nodeIdentifier         the node identifier
   * @param datasetIdentifier      the dataset identifier
   * @param distributionIdentifier the distribution identifier
   * @return the response
   */
  @POST
  @Path("/catalogues/{nodeID}/dataset/{datasetID}/distribution/{distributionID}/createDatalet")
  @Consumes({ MediaType.APPLICATION_JSON })
  @Produces(MediaType.APPLICATION_JSON)
  public Response createDataletFromDistribution(@Context HttpServletRequest httpRequest,
      final String input, @PathParam("nodeID") String nodeIdentifier,
      @PathParam("datasetID") String datasetIdentifier,
      @PathParam("distributionID") String distributionIdentifier) {

    CachePersistenceManager jpa = new CachePersistenceManager();
    try {

      Datalet datalet = GsonUtil.json2Obj(input, GsonUtil.dataletType);

      datalet.setId(UUID.randomUUID().toString());
      ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
      datalet.setRegisterDate(now);
      datalet.setLastSeenDate(now);
      datalet.setViews(1);
      datalet.setDatasetId(datasetIdentifier);
      datalet.setDistributionId(distributionIdentifier);
      datalet.setNodeId(nodeIdentifier);

      List<Datalet> existingDatalets = null;

      DcatDataset dataset = MetadataCacheManager.getDatasetById(datasetIdentifier);
      boolean updateSolr = false;
      for (DcatDistribution d : dataset.getDistributions()) {
        if (d.getId().equals(distributionIdentifier)) {
          if (!d.isHasDatalets()) {
            d.setHasDatalets(true);
            updateSolr = true;
          } else {
            existingDatalets = jpa.jpaGetDataletByDistributionId(d.getId());
          }
          break;
        }
      }

      if (updateSolr) {
        MetadataCacheManager.updateDatasetInsertDatalet(Integer.parseInt(nodeIdentifier), dataset);
      }

      if (StringUtils.isBlank(datalet.getTitle())) {
        Integer newIdentifier = 1;
        if (existingDatalets != null && existingDatalets.size() != 0) {
          newIdentifier = existingDatalets.stream().filter(x -> x.isCustomTitle())
              .collect(Collectors.toList()).stream()
              .map(x -> Integer.parseInt(x.getTitle().split("_")[1]))
              .collect(Collectors.summarizingInt(Integer::intValue)).getMax() + 1;
        }
        datalet.setTitle("Datalet_" + newIdentifier);
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

  /**
   * Gets the datalet by distribution.
   *
   * @param httpRequest            the http request
   * @param nodeIdentifier         the node identifier
   * @param datasetIdentifier      the dataset identifier
   * @param distributionIdentifier the distribution identifier
   * @return the datalet by distribution
   */
  @GET
  @Path("/catalogues/{nodeID}/dataset/{datasetID}/distribution/{distributionID}/datalets")
  // @Consumes({ MediaType.APPLICATION_JSON })
  @Produces(MediaType.APPLICATION_JSON)
  public Response getDataletByDistribution(@Context HttpServletRequest httpRequest,
      @PathParam("nodeID") String nodeIdentifier, @PathParam("datasetID") String datasetIdentifier,
      @PathParam("distributionID") String distributionIdentifier) {

    CachePersistenceManager jpa = new CachePersistenceManager();
    try {
      List<Datalet> datalets = jpa.jpaGetDataletByTripleId(nodeIdentifier, datasetIdentifier,
          distributionIdentifier);
      return Response.ok(GsonUtil.obj2Json(datalets, GsonUtil.dataletListType)).build();
    } catch (Exception e) {
      return handleErrorResponse500(e);
    } finally {
      jpa.jpaClose();
    }

  }

  /**
   * Update datalet views.
   *
   * @param httpRequest            the http request
   * @param nodeIdentifier         the node identifier
   * @param datasetIdentifier      the dataset identifier
   * @param distributionIdentifier the distribution identifier
   * @param dataletIdentifier      the datalet identifier
   * @return the response
   */
  @PUT
  @Path("/catalogues/{nodeID}/dataset/{datasetID}"
      + "/distribution/{distributionID}/datalet/{dataletID}/updateViews")
  // @Consumes({ MediaType.APPLICATION_JSON })
  @Produces(MediaType.APPLICATION_JSON)
  public Response updateDataletViews(@Context HttpServletRequest httpRequest,
      @PathParam("nodeID") String nodeIdentifier, @PathParam("datasetID") String datasetIdentifier,
      @PathParam("distributionID") String distributionIdentifier,
      @PathParam("dataletID") String dataletIdentifier) {

    CachePersistenceManager jpa = new CachePersistenceManager();
    try {

      Datalet datalet = jpa.jpaGetDataletByIds(nodeIdentifier, datasetIdentifier,
          distributionIdentifier, dataletIdentifier);
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

  /**
   * Gets the catalogues info.
   *
   * @param httpRequest the http request
   * @return the catalogues info
   */
  @GET
  @Path("/cataloguesInfo")
  @Produces("application/json")
  public Response getCataloguesInfo(@Context HttpServletRequest httpRequest) {
    // LocalTime time1 = LocalTime.now();
    try {
      JSONArray result = new JSONArray();
      List<OdmsCatalogue> nodes = FederationCore.getOdmsCatalogues().stream()
          .filter(
              x -> x.isActive() && x.isCacheable() && !x.getSynchLock().equals(OdmsSynchLock.FIRST))
          .collect(Collectors.toList());
      Collections.sort(nodes, CommonUtil.nameOrder);
      for (OdmsCatalogue n : nodes) {
        JSONObject tmp = new JSONObject();
        tmp.put("name", n.getName());
        tmp.put("id", n.getId());
        tmp.put("federationLevel", n.getFederationLevel());
        result.put(tmp);
      }
      // LocalTime time2 = LocalTime.now();
      // logger.info("search_catalogues_list " + Duration.between(time1, time2) + "
      // milliseconds");
      return Response.ok(result.toString()).build();
    } catch (Exception e) {
      return handleErrorResponse500(e);
    }
  }

  /**
   * Gets the odms catalogues.
   *
   * @param withImage the with image
   * @param orderType the order type
   * @param orderBy   the order by
   * @param rows      the rows
   * @param offset    the offset
   * @param name      the name
   * @param country   the country
   * @return the odms catalogues
   */
  @GET
  @Path("/catalogues")
  @Produces("application/json")
  public Response getOdmsCatalogues(
      @QueryParam("withImage") @DefaultValue("true") boolean withImage,
      @QueryParam("orderType") @DefaultValue("asc") String orderType,
      @QueryParam("orderBy") @DefaultValue("id") String orderBy,
      @QueryParam("rows") @DefaultValue("10") String rows,
      @QueryParam("offset") @DefaultValue("0") String offset, @QueryParam("name") String name,
      @QueryParam("country") String country) {

    try {
      List<OdmsCatalogue> nodes = new ArrayList<OdmsCatalogue>(
          FederationCore.getOdmsCatalogues(withImage).stream().filter(x -> x.isActive())
              .collect(Collectors.toList()));

      if (StringUtils.isNotBlank(name) && StringUtils.isBlank(country)) {
        nodes = nodes.stream().filter(x -> x.getName().toLowerCase().contains(name.toLowerCase()))
            .collect(Collectors.toList());
      } else if (StringUtils.isBlank(name) && StringUtils.isNotBlank(country)) {
        nodes = nodes.stream()
            .filter(x -> StringUtils.isNotBlank(x.getCountry())
                && x.getCountry().toLowerCase().equals(country.toLowerCase()))
            .collect(Collectors.toList());
      } else if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(country)) {
        nodes = nodes.stream()
            .filter(x -> x.getName().toLowerCase().contains(name.toLowerCase())
                && (StringUtils.isNotBlank(x.getCountry())
                    && x.getCountry().toLowerCase().equals(country.toLowerCase())))
            .collect(Collectors.toList());
      }

      OrderBy ordBy;
      try {
        ordBy = OrderBy.valueOf(orderBy.toUpperCase());
      } catch (Exception e) {
        ordBy = OrderBy.NAME;
      }

      OrderType ordType;
      try {
        ordType = OrderType.valueOf(orderType.toUpperCase());
      } catch (Exception e) {
        ordType = OrderType.ASC;
      }

      switch (ordBy) {
        case ID:
          Collections.sort(nodes, ordType.equals(OrderType.DESC) 
              ? CommonUtil.idOrder.reverse() : CommonUtil.idOrder);
          break;
        case DATASETCOUNT:
          Collections.sort(nodes,
              ordType.equals(OrderType.DESC) 
              ? CommonUtil.datasetCountOrder.reverse() : CommonUtil.datasetCountOrder);
          break;
        case FEDERATIONLEVEL:
          Collections.sort(nodes, ordType.equals(OrderType.DESC) 
              ? CommonUtil.federationLevelOrder.reverse()
              : CommonUtil.federationLevelOrder);
          break;
        case HOST:
          Collections.sort(nodes, ordType.equals(OrderType.DESC) 
              ? CommonUtil.hostOrder.reverse() : CommonUtil.hostOrder);
          break;
        case LASTUPDATEDATE:
          Collections.sort(nodes,
              ordType.equals(OrderType.DESC) 
              ? CommonUtil.lastUpdateOrder.reverse() : CommonUtil.lastUpdateOrder);
          break;
        case NAME:
          Collections.sort(nodes, ordType.equals(OrderType.DESC) 
              ? CommonUtil.nameOrder.reverse() : CommonUtil.nameOrder);
          break;
        case NODESTATE:
          Collections.sort(nodes,
              ordType.equals(OrderType.DESC) 
              ? CommonUtil.stateOrder.reverse() : CommonUtil.stateOrder);
          break;
        case NODETYPE:
          Collections.sort(nodes, ordType.equals(OrderType.DESC) 
              ? CommonUtil.typeOrder.reverse() : CommonUtil.typeOrder);
          break;
        case REFRESHPERIOD:
          Collections.sort(nodes,
              ordType.equals(OrderType.DESC) 
              ? CommonUtil.refreshPeriodOrder.reverse() : CommonUtil.refreshPeriodOrder);
          break;
        case REGISTERDATE:
          Collections.sort(nodes,
              ordType.equals(OrderType.DESC) 
              ? CommonUtil.registerDateOrder.reverse() : CommonUtil.registerDateOrder);
          break;
        default:
          break;
      }

      // TODO: enable pagination
      int count = nodes.size();
      JSONObject result = new JSONObject();
      result.put("count", count);
      
      int row = CommonUtil.ROWSDEFAULT;
      int off = CommonUtil.OFFSETDEFAULT;

      try {
        row = Integer.parseInt(rows);
      } catch (Exception e) {
        logger.debug(e.getLocalizedMessage());
      }

      try {
        off = Integer.parseInt(offset);
      } catch (Exception e) {
        logger.debug(e.getLocalizedMessage());
      }

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
     
      JSONArray array = new JSONArray(GsonUtil.obj2JsonWithExclude(nodes, GsonUtil.nodeListType));
      result.put("catalogues", array);
      System.gc();
      return Response.status(Response.Status.OK).entity(result.toString()).build();

    } catch (Exception e) {
      logger.error("Exception raised " + e.getLocalizedMessage());
      return handleErrorResponse500(e);
    }

  }

  /**
   * Gets the single catalogue.
   *
   * @param httpRequest    the http request
   * @param nodeIdentifier the node identifier
   * @param withImage      the with image
   * @return the single catalogue
   */
  @GET
  @Path("/catalogues/{nodeID}")
  @Consumes({ MediaType.APPLICATION_JSON })
  @Produces("application/json")
  public Response getSingleCatalogue(@Context HttpServletRequest httpRequest,
      @PathParam("nodeID") String nodeIdentifier,
      @QueryParam("withImage") @DefaultValue("true") boolean withImage) {

    try {
      OdmsCatalogue result = FederationCore.getOdmsCatalogue(Integer.parseInt(nodeIdentifier),
          withImage);
      if (result.isActive()) {
        return Response.status(Response.Status.OK)
            .entity(GsonUtil.obj2JsonWithExclude(result, GsonUtil.nodeType)).build();
      } else {
        ErrorResponse err = new ErrorResponse(
            String.valueOf(Response.Status.NOT_FOUND.getStatusCode()),
            "Catalogues with id: " + nodeIdentifier + " not found",
            String.valueOf(Response.Status.NOT_FOUND.getStatusCode()),
            "Catalogues with id: " + nodeIdentifier + " not found");
        return Response.status(Response.Status.NOT_FOUND).entity(err.toJson()).build();
      }

    } catch (NumberFormatException e) {
      // TODO Auto-generated catch block
      return handleErrorResponse500(e);
    } catch (GsonUtilException e) {
      // TODO Auto-generated catch block
      return handleErrorResponse500(e);
    } catch (OdmsCatalogueNotFoundException e) {
      // TODO Auto-generated catch block
      ErrorResponse err = new ErrorResponse(
          String.valueOf(Response.Status.NOT_FOUND.getStatusCode()),
          "Catalogues with id: " + nodeIdentifier + " not found",
          String.valueOf(Response.Status.NOT_FOUND.getStatusCode()),
          "Catalogues with id: " + nodeIdentifier + " not found");
      return Response.status(Response.Status.NOT_FOUND).entity(err.toJson()).build();
    } catch (OdmsManagerException e) {
      // TODO Auto-generated catch block
      return handleBadRequestErrorResponse(e);
    }
  }

  /**
   * Gets the catalogue datasets.
   *
   * @param httpRequest    the http request
   * @param nodeIdentifier the node identifier
   * @param rows           the rows
   * @param start          the start
   * @return the catalogue datasets
   */
  @GET
  @Path("/catalogues/{nodeID}/datasets")
  @Consumes({ MediaType.APPLICATION_JSON })
  @Produces("application/json")
  public Response getCatalogueDatasets(@Context HttpServletRequest httpRequest,
      @PathParam("nodeID") String nodeIdentifier,
      @QueryParam("rows") @DefaultValue("1000") int rows,
      @QueryParam("start") @DefaultValue("0") int start) {

    try {

      OdmsCatalogue cat = FederationCore.getOdmsCatalogue(Integer.parseInt(nodeIdentifier));

      if (rows > 1000) {
        ErrorResponse err = new ErrorResponse(
            String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()),
            "Rows maximum value is 1000", String.valueOf(Response.Status.NOT_FOUND.getStatusCode()),
            "Rows maximum value is 1000");
        return Response.status(Response.Status.BAD_REQUEST).entity(err.toJson()).build();
      }

      if (cat.isActive()) {
        SearchResult result = MetadataCacheManager
            .getAllDatasetsByOdmsCatalogue(Integer.parseInt(nodeIdentifier), rows, start);
        result.setFacets(null);
        return Response.status(Response.Status.OK)
            .entity(GsonUtil.obj2Json(result, GsonUtil.searchResultType)).build();
      } else {
        ErrorResponse err = new ErrorResponse(
            String.valueOf(Response.Status.NOT_FOUND.getStatusCode()),
            "Catalogues with id: " + nodeIdentifier + " not found",
            String.valueOf(Response.Status.NOT_FOUND.getStatusCode()),
            "Catalogues with id: " + nodeIdentifier + " not found");
        return Response.status(Response.Status.NOT_FOUND).entity(err.toJson()).build();
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
    } catch (OdmsCatalogueNotFoundException e) {
      // TODO Auto-generated catch block
      ErrorResponse err = new ErrorResponse(
          String.valueOf(Response.Status.NOT_FOUND.getStatusCode()),
          "Catalogues with id: " + nodeIdentifier + " not found",
          String.valueOf(Response.Status.NOT_FOUND.getStatusCode()),
          "Catalogues with id: " + nodeIdentifier + " not found");
      return Response.status(Response.Status.NOT_FOUND).entity(err.toJson()).build();
    }
  }

  /**
   * Gets the single dataset.
   *
   * @param httpRequest       the http request
   * @param nodeIdentifier    the node identifier
   * @param datasetIdentifier the dataset identifier
   * @return the single dataset
   */
  @GET
  @Path("/catalogues/{nodeID}/datasets/{datasetID}")
  @Consumes({ MediaType.APPLICATION_JSON })
  @Produces("application/json")
  public Response getSingleDataset(@Context HttpServletRequest httpRequest,
      @PathParam("nodeID") String nodeIdentifier,
      @PathParam("datasetID") String datasetIdentifier) {

    try {

      OdmsCatalogue cat = FederationCore.getOdmsCatalogue(Integer.parseInt(nodeIdentifier));
      if (cat.isActive()) {
        DcatDataset result = MetadataCacheManager.getDatasetById(datasetIdentifier);
        if (result.getNodeId().equals(nodeIdentifier)) {
          return Response.status(Response.Status.OK)
              .entity(GsonUtil.obj2Json(result, GsonUtil.datasetType)).build();
        } else {
          ErrorResponse err = new ErrorResponse(
              String.valueOf(Response.Status.NOT_FOUND.getStatusCode()),
              "Dataset with id: " + datasetIdentifier + " not found for catalogue: "
                  + nodeIdentifier,
              String.valueOf(Response.Status.NOT_FOUND.getStatusCode()),
              "Catalogues with id: " + nodeIdentifier + " not found");
          return Response.status(Response.Status.NOT_FOUND).entity(err.toJson()).build();
        }
      } else {
        ErrorResponse err = new ErrorResponse(
            String.valueOf(Response.Status.NOT_FOUND.getStatusCode()),
            "Catalogues with id: " + nodeIdentifier + " not found",
            String.valueOf(Response.Status.NOT_FOUND.getStatusCode()),
            "Catalogues with id: " + nodeIdentifier + " not found");
        return Response.status(Response.Status.NOT_FOUND).entity(err.toJson()).build();
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
    } catch (OdmsCatalogueNotFoundException e) {
      // TODO Auto-generated catch block
      return handleBadRequestErrorResponse(e);
    }
  }

  /**
   * Gets the dataset distribution.
   *
   * @param httpRequest            the http request
   * @param nodeIdentifier         the node identifier
   * @param datasetIdentifier      the dataset identifier
   * @param distributionIdentifier the distribution identifier
   * @return the dataset distribution
   */
  @GET
  @Path("/catalogues/{nodeID}/datasets/{datasetID}/distributions/{distributionID}")
  @Consumes({ MediaType.APPLICATION_JSON })
  @Produces("application/json")
  public Response getDatasetDistribution(@Context HttpServletRequest httpRequest,
      @PathParam("nodeID") String nodeIdentifier, @PathParam("datasetID") String datasetIdentifier,
      @PathParam("distributionID") String distributionIdentifier) {

    try {
      DcatDataset dataset = MetadataCacheManager.getDatasetById(datasetIdentifier);

      DcatDistribution distribution = MetadataCacheManager
          .getDistributionById(distributionIdentifier);

      if ((distribution.getNodeId().equals(nodeIdentifier))
          && (dataset.getNodeId().equals(nodeIdentifier))) {
        return Response.status(Response.Status.OK)
            .entity(GsonUtil.obj2Json(distribution, GsonUtil.distributionType)).build();
      } else {
        ErrorResponse err = new ErrorResponse(
            String.valueOf(Response.Status.NOT_FOUND.getStatusCode()),
            "Distribution with id: " + distributionIdentifier + " not found for catalogue: "
                + nodeIdentifier,
            String.valueOf(Response.Status.NOT_FOUND.getStatusCode()),
            "Dataset with id: " + datasetIdentifier + " not found");
        return Response.status(Response.Status.NOT_FOUND).entity(err.toJson()).build();
      }

    } catch (NumberFormatException e) {
      // TODO Auto-generated catch block
      return handleErrorResponse500(e);
    } catch (DistributionNotFoundException e) {
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
    }
  }

  /**
   * Gets the dataset distributions.
   *
   * @param httpRequest       the http request
   * @param nodeIdentifier    the node identifier
   * @param datasetIdentifier the dataset identifier
   * @return the dataset distributions
   */
  @GET
  @Path("/catalogues/{nodeID}/datasets/{datasetID}/distributions")
  @Consumes({ MediaType.APPLICATION_JSON })
  @Produces("application/json")
  public Response getDatasetDistributions(@Context HttpServletRequest httpRequest,
      @PathParam("nodeID") String nodeIdentifier,
      @PathParam("datasetID") String datasetIdentifier) {

    try {

      DcatDataset dataset = MetadataCacheManager.getDatasetById(datasetIdentifier);

      List<DcatDistribution> result = dataset.getDistributions();

      return Response.status(Response.Status.OK)
          .entity(GsonUtil.obj2Json(result, GsonUtil.distributionListType)).build();

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
    }
  }

  /**
   * Gets the dataset by id.
   *
   * @param httpRequest the http request
   * @param id          the id
   * @return the dataset by id
   */
  @GET
  @Path("/datasets/{id}")
  @Consumes({ MediaType.APPLICATION_JSON })
  @Produces("application/json")
  public Response getDatasetById(@Context HttpServletRequest httpRequest,
      @PathParam("id") String id) {

    try {
      try {
        DcatDataset result = MetadataCacheManager.getDatasetById(id);
        return Response.status(Response.Status.OK)
            .entity(GsonUtil.obj2Json(result, GsonUtil.datasetType)).build();
      } catch (DatasetNotFoundException e) {
        // TODO Auto-generated catch block
        ErrorResponse err = new ErrorResponse(
            String.valueOf(Response.Status.NOT_FOUND.getStatusCode()),
            "Dataset with id: " + id + " not found",
            String.valueOf(Response.Status.NOT_FOUND.getStatusCode()),
            "Dataset with id: " + id + " not found");
        return Response.status(Response.Status.NOT_FOUND).entity(err.toJson()).build();
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

  /**
   * Execute orion query.
   *
   * @param httpRequest     the http request
   * @param nodeIdentifier  the node identifier
   * @param queryIdentifier the query identifier
   * @return the response
   */
  @GET
  @Path("executeOrionQuery/{cbQueryID}/catalogue/{catalogueID}")
  @Consumes({ MediaType.APPLICATION_JSON })
  @Produces("application/json")
  public Response executeOrionQuery(@Context HttpServletRequest httpRequest,
      @PathParam("catalogueID") String nodeIdentifier,
      @PathParam("cbQueryID") String queryIdentifier) {
    ErrorResponse err = null;
    if (StringUtils.isBlank(nodeIdentifier)) {
      err = new ErrorResponse(String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()),
          "Missing mandatory query parameter: catalogue",
          String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()),
          "Missing mandatory query parameter: catalogue");
    }

    if (StringUtils.isBlank(queryIdentifier)) {
      err = new ErrorResponse(String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()),
          "Missing mandatory query parameter: cbQueryID",
          String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()),
          "Missing mandatory query parameter: cbQueryID");
    }

    try {
      OdmsCatalogue catalogue = FederationCore.getOdmsCatalogue(Integer.parseInt(nodeIdentifier),
          false);
      if (!catalogue.getNodeType().equals(OdmsCatalogueType.ORION)) {
        err = new ErrorResponse(String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()),
            "Catalogue: " + nodeIdentifier + " is not ORION",
            String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()),
            "Catalogue: " + nodeIdentifier + " is not ORION");
      } else {

        OrionCatalogueConfiguration catalogueConfig = (OrionCatalogueConfiguration) catalogue
            .getAdditionalConfig();
        OrionDistributionConfig distributionConfig = MetadataCacheManager
            .getOrionDistributionConfig(queryIdentifier);

        String compiledUri = (!catalogue.getHost().endsWith("/") ? catalogue.getHost()
            : catalogue.getHost().substring(0, catalogue.getHost().length() - 1))
            + (!catalogueConfig.isNgsild() ? "/v2/entities" : "/ngsi-ld/v1/entities") + "?"
            + distributionConfig.getQuery();

        client = ClientBuilder.newClient();

        WebTarget webTarget = client.target(compiledUri);
        Invocation.Builder builder = webTarget.request();
        if (StringUtils.isNotBlank(distributionConfig.getFiwareService())) {
          builder = builder.header("Fiware-Service", distributionConfig.getFiwareService());
        }

        if (StringUtils.isNotBlank(distributionConfig.getFiwareServicePath())) {
          builder = builder.header("Fiware-ServicePath", distributionConfig.getFiwareServicePath());
        }
        
        if (catalogueConfig.isNgsild()) {
          if (StringUtils.isNotBlank(distributionConfig.getContext())) {
            builder = builder.header("Link", "<" + distributionConfig.getContext() + ">; "
                + "rel=\"http://www.w3.org/ns/json-ld#context\"; type=\"application/ld+json\"");
          }
        }

        if (catalogueConfig.isAuthenticated()) {
          builder = builder.header("X-Auth-Token", catalogueConfig.getAuthToken());
        }

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
        // logger.info("Status: " + request.getStatus());

        for (String k : keys) {
          if (!k.toLowerCase().equals("access-control-allow-origin")) {
            responseBuilder.header(k, headers.get(k).get(0));
          }
        }

        return responseBuilder.build();

      }

      return Response.status(Response.Status.OK).build();
    } catch (OdmsCatalogueNotFoundException e) {
      err = new ErrorResponse(String.valueOf(Response.Status.NOT_FOUND.getStatusCode()),
          "Catalogues with id: " + nodeIdentifier + " not found",
          String.valueOf(Response.Status.NOT_FOUND.getStatusCode()),
          "Catalogues with id: " + nodeIdentifier + " not found");
      return Response.status(Response.Status.NOT_FOUND).entity(err.toJson()).build();
    } catch (NumberFormatException | OdmsManagerException e) {
      // TODO Auto-generated catch block
      // e.printStackTrace();
      return handleErrorResponse500(e);
    }

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
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error.toJson()).build();
  }

  /**
   * Handle euro voc not found.
   *
   * @param e the e
   * @return the response
   */
  private static Response handleEuroVocNotFound(EuroVocTranslationNotFoundException e) {

    e.printStackTrace();
    ErrorResponse error = new ErrorResponse(
        String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()), e.getMessage(),
        e.getClass().getSimpleName(), "No results found!");
    return Response.status(Response.Status.BAD_REQUEST).entity(error.toJson()).build();
  }

  /**
   * Handle bad request error response.
   *
   * @param e the e
   * @return the response
   */
  private static Response handleBadRequestErrorResponse(Exception e) {

    e.printStackTrace();
    ErrorResponse error = new ErrorResponse(
        String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()), e.getMessage(),
        e.getClass().getSimpleName(), "The request body is not a valid JSON");
    return Response.status(Response.Status.BAD_REQUEST).entity(error.toJson()).build();
  }

  /**
   * Handle bad query error response.
   *
   * @param e the e
   * @return the response
   */
  private static Response handleBadQueryErrorResponse(Exception e) {

    e.printStackTrace();
    ErrorResponse error = new ErrorResponse(
        String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()), e.getMessage(),
        e.getClass().getSimpleName(), "Malformed SPARQL query");
    return Response.status(Response.Status.BAD_REQUEST).entity(error.toJson()).build();
  }

}
