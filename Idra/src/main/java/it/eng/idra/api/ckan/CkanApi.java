/*******************************************************************************
 * Idra - Open Data Federation Platform
 * Copyright (C) 2021 Engineering Ingegneria Informatica S.p.A.
 * <p> 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * <p> 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

package it.eng.idra.api.ckan;

import it.eng.idra.beans.ckan.CkanErrorResponse;
import it.eng.idra.beans.ckan.CkanSuccessResponse;
import it.eng.idra.beans.dcat.DcatDataset;
import it.eng.idra.beans.exception.DatasetNotFoundException;
import it.eng.idra.beans.odms.OdmsCatalogue;
import it.eng.idra.beans.odms.OdmsCatalogueNotFoundException;
import it.eng.idra.beans.search.SearchResult;
import it.eng.idra.cache.MetadataCacheManager;
import it.eng.idra.management.FederationCore;
import it.eng.idra.search.FederatedSearch;
import it.eng.idra.utils.GsonUtil;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.ckan.Dataset;
import org.json.JSONObject;

// TODO: Auto-generated Javadoc
/**
 * The Class CkanApi.
 */
@Path("/")
public class CkanApi {

  // private static Logger logger = LogManager.getLogger(CKANApi.class);

  /**
   * All package list.
   *
   * @param httpRequest the http request
   * @param l           the l
   * @param o           the o
   * @return the response
   */
  @GET
  @Path("/api/{var:(3/?)?}action/package_list")
  @Produces("application/json")
  public Response all_package_list(@Context HttpServletRequest httpRequest,
      @QueryParam("limit") String l, @QueryParam("offset") String o) {

    try {

      // TO-DO solo i cataloghi attivi

      int limit = -1;
      int offset = 0;

      if (StringUtils.isNotBlank(l)) {
        limit = Integer.parseInt(l);
      } else {
        // Default limit a 1000
        limit = 1000;
      }

      if (StringUtils.isNotBlank(o)) {
        offset = Integer.parseInt(o);
      }

      CkanSuccessResponse<List<String>> res = new CkanSuccessResponse<>();
      res.setHelp("Return a list of the names of the site's datasets (packages). "
          + ":param limit: if given, the list of datasets will be broken "
          + "into pages of at most ``limit`` datasets per "
          + "page and only one page will be returned at a time "
          + "(optional) :type limit: int :param offset: when ``limit`` is given, "
          + "the offset to start returning packages from :type "
          + "offset: int :rtype: list of strings; Limit default value is 1000 ");
      res.setSuccess(true);
      res.setResult(MetadataCacheManager.getAllDatasetsId(limit, offset));

      return Response.status(Response.Status.OK)
          .entity(GsonUtil.obj2Json(res, GsonUtil.ckanSuccType)).build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

  }

  /**
   * Package list.
   *
   * @param httpRequest         the http request
   * @param catalogueIdentifier the catalogue identifier
   * @param l                   the l
   * @param o                   the o
   * @return the response
   */
  @GET
  @Path("/{catalogueID}/api/{var:(3/?)?}action/package_list")
  @Produces("application/json")
  public Response package_list(@Context HttpServletRequest httpRequest,
      @PathParam("catalogueID") String catalogueIdentifier, @QueryParam("limit") String l,
      @QueryParam("offset") String o) {

    try {
      int limit = -1;
      int offset = 0;

      if (StringUtils.isNotBlank(l)) {
        limit = Integer.parseInt(l);
      }

      if (StringUtils.isNotBlank(o)) {
        offset = Integer.parseInt(o);
      }

      try {
        OdmsCatalogue cat = FederationCore.getOdmsCatalogue(Integer.parseInt(catalogueIdentifier));
        if (cat.isActive()) {
          CkanSuccessResponse<List<String>> res = new CkanSuccessResponse<>();
          res.setHelp("Return a list of the names of the site's datasets (packages). "
              + ":param limit: if given, the list of datasets will be broken "
              + "into pages of at most ``limit`` datasets "
              + "per page and only one page will be returned at a time "
              + "(optional) :type limit: int :param offset: when ``limit`` is given, "
              + "the offset to start returning packages "
              + "from :type offset: int :rtype: list of strings ");
          res.setSuccess(true);
          res.setResult(
              MetadataCacheManager.getAllDatasetsIdByCatalogue(catalogueIdentifier, limit, offset));

          return Response.status(Response.Status.OK)
              .entity(GsonUtil.obj2Json(res, GsonUtil.ckanSuccType)).build();
        } else {
          CkanErrorResponse err = new CkanErrorResponse("",
              "Catalogue " + catalogueIdentifier + " not found", "Not Found");
          return Response.status(Response.Status.NOT_FOUND)
              .entity(GsonUtil.obj2Json(err, GsonUtil.ckanErrType)).build();
        }
      } catch (OdmsCatalogueNotFoundException e) {
        CkanErrorResponse err = new CkanErrorResponse("",
            "Catalogue " + catalogueIdentifier + " not found", "Not Found");
        return Response.status(Response.Status.NOT_FOUND)
            .entity(GsonUtil.obj2Json(err, GsonUtil.ckanErrType)).build();
      }

    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

  }

  /**
   * Package show.
   *
   * @param httpRequest         the http request
   * @param catalogueIdentifier the catalogue identifier
   * @param datasetIdentifier   the dataset identifier
   * @return the response
   */
  @GET
  @Path("/{catalogueID}/api/{var:(3/?)?}action/package_show")
  @Produces("application/json")
  public Response package_show(@Context HttpServletRequest httpRequest,
      @PathParam("catalogueID") String catalogueIdentifier,
      @QueryParam("id") String datasetIdentifier) {

    try {

      if (StringUtils.isBlank(datasetIdentifier)) {
        CkanErrorResponse err = new CkanErrorResponse("", "Missing mandatory parameter id",
            "Validation error");
        return Response.status(Response.Status.CONFLICT)
            .entity(GsonUtil.obj2Json(err, GsonUtil.ckanErrType)).build();
      }

      CkanSuccessResponse<Dataset> res = new CkanSuccessResponse<>();
      res.setHelp("");

      try {
        OdmsCatalogue cat = FederationCore.getOdmsCatalogue(Integer.parseInt(catalogueIdentifier));
        if (cat.isActive()) {
          DcatDataset result = MetadataCacheManager.getDatasetById(datasetIdentifier);
          if (result.getNodeId().equals(catalogueIdentifier)) {
            res.setResult(CkanUtils.toCkanDataset(result));
          } else {
            CkanErrorResponse err = new CkanErrorResponse("",
                "Package not found for catalogue: " + catalogueIdentifier, "Not Found");
            return Response.status(Response.Status.NOT_FOUND)
                .entity(GsonUtil.obj2Json(err, GsonUtil.ckanErrType)).build();
          }
        } else {
          CkanErrorResponse err = new CkanErrorResponse("", "Package not found", "Not Found");
          return Response.status(Response.Status.NOT_FOUND)
              .entity(GsonUtil.obj2Json(err, GsonUtil.ckanErrType)).build();
        }
      } catch (DatasetNotFoundException e) {
        CkanErrorResponse err = new CkanErrorResponse("",
            "Catalogue " + catalogueIdentifier + " not found", "Not Found");
        return Response.status(Response.Status.NOT_FOUND)
            .entity(GsonUtil.obj2Json(err, GsonUtil.ckanErrType)).build();
      } catch (OdmsCatalogueNotFoundException e) {
        CkanErrorResponse err = new CkanErrorResponse("",
            "Catalogue " + catalogueIdentifier + " not found", "Not Found");
        return Response.status(Response.Status.NOT_FOUND)
            .entity(GsonUtil.obj2Json(err, GsonUtil.ckanErrType)).build();
      }

      return Response.status(Response.Status.OK)
          .entity(GsonUtil.obj2Json(res, GsonUtil.ckanSuccType)).build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

  }

  /**
   * All package show.
   *
   * @param httpRequest       the http request
   * @param datasetIdentifier the dataset identifier
   * @return the response
   */
  @GET
  @Path("/api/{var:(3/?)?}action/package_show")
  @Produces("application/json")
  public Response all_package_show(@Context HttpServletRequest httpRequest,
      @QueryParam("id") String datasetIdentifier) {

    try {

      if (StringUtils.isBlank(datasetIdentifier)) {
        CkanErrorResponse err = new CkanErrorResponse("", "Missing mandatory parameter id",
            "Validation error");
        return Response.status(Response.Status.CONFLICT)
            .entity(GsonUtil.obj2Json(err, GsonUtil.ckanErrType)).build();
      }

      CkanSuccessResponse<Dataset> res = new CkanSuccessResponse<>();
      res.setHelp("");

      try {

        DcatDataset result = MetadataCacheManager.getDatasetById(datasetIdentifier);
        res.setResult(CkanUtils.toCkanDataset(result));

      } catch (DatasetNotFoundException e) {
        CkanErrorResponse err = new CkanErrorResponse("", "Package not found", "Not Found");
        return Response.status(Response.Status.NOT_FOUND)
            .entity(GsonUtil.obj2Json(err, GsonUtil.ckanErrType)).build();
      }

      return Response.status(Response.Status.OK)
          .entity(GsonUtil.obj2Json(res, GsonUtil.ckanSuccType)).build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

  }

  /**
   * All package search.
   *
   * @param httpRequest the http request
   * @param query       the query
   * @param start       the start
   * @param rows        the rows
   * @param sort        the sort
   * @return the response
   */
  @GET
  @Path("/api/{var:(3/?)?}action/package_search")
  @Produces("application/json")
  public Response all_package_search(@Context HttpServletRequest httpRequest,
      @QueryParam("q") @DefaultValue("*:*") String query,
      @QueryParam("start") @DefaultValue("0") String start,
      @QueryParam("rows") @DefaultValue("20") String rows,
      @QueryParam("sort") @DefaultValue("metadata_modified desc") String sort) {

    try {

      int limit = -1;
      int offset = 0;

      if (StringUtils.isNotBlank(rows)) {
        limit = Integer.parseInt(rows);
      }

      if (StringUtils.isNotBlank(start)) {
        offset = Integer.parseInt(start);
      }

      String mappedQuery = CkanUtils.manageQuery(query, " ");
      String mappedSort = CkanUtils.manageSort(sort);
      // Adding catalogues ids
      List<String> ids = FederationCore.getOdmsCatalogues(false).stream().filter(x -> x.isActive())
          .map(x -> Integer.toString(x.getId())).collect(Collectors.toList());

      SearchResult result = FederatedSearch.searchByQuery(mappedQuery, mappedSort, limit, offset,
          ids);

      CkanSuccessResponse<CkanSearchResult> res = new CkanSuccessResponse<>();
      res.setHelp("");
      res.setResult(CkanUtils.toCkanSearchResult(result));

      return Response.status(Response.Status.OK)
          .entity(GsonUtil.obj2Json(res, GsonUtil.ckanSuccType)).build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

  }

  /**
   * Single package search.
   *
   * @param httpRequest         the http request
   * @param catalogueIdentifier the catalogue identifier
   * @param query               the query
   * @param start               the start
   * @param rows                the rows
   * @param sort                the sort
   * @return the response
   */
  @GET
  @Path("{catalogueID}/api/{var:(3/?)?}action/package_search")
  @Produces("application/json")
  public Response single_package_search(@Context HttpServletRequest httpRequest,
      @PathParam("catalogueID") String catalogueIdentifier,
      @QueryParam("q") @DefaultValue("*:*") String query,
      @QueryParam("start") @DefaultValue("0") String start,
      @QueryParam("rows") @DefaultValue("20") String rows,
      @QueryParam("sort") @DefaultValue("metadata_modified desc") String sort) {

    try {

      int limit = -1;
      int offset = 0;

      if (StringUtils.isNotBlank(rows)) {
        limit = Integer.parseInt(rows);
      }

      if (StringUtils.isNotBlank(start)) {
        offset = Integer.parseInt(start);
      }

      String mappedQuery = CkanUtils.manageQuery(query, " ");
      String mappedSort = CkanUtils.manageSort(sort);
      // Adding catalogues ids
      try {
        OdmsCatalogue cat = FederationCore.getOdmsCatalogue(Integer.parseInt(catalogueIdentifier));
        if (cat.isActive()) {
          SearchResult result = FederatedSearch.searchByQuery(mappedQuery, mappedSort, limit,
              offset, Arrays.asList(catalogueIdentifier));

          CkanSuccessResponse<CkanSearchResult> res = new CkanSuccessResponse<>();
          res.setHelp("");
          res.setResult(CkanUtils.toCkanSearchResult(result));

          return Response.status(Response.Status.OK)
              .entity(GsonUtil.obj2Json(res, GsonUtil.ckanSuccType)).build();
        } else {
          CkanErrorResponse err = new CkanErrorResponse("",
              "Catalogue " + catalogueIdentifier + " not found", "Not Found");
          return Response.status(Response.Status.NOT_FOUND)
              .entity(GsonUtil.obj2Json(err, GsonUtil.ckanErrType)).build();
        }
      } catch (OdmsCatalogueNotFoundException e) {
        CkanErrorResponse err = new CkanErrorResponse("",
            "Catalogue " + catalogueIdentifier + " not found", "Not Found");
        return Response.status(Response.Status.NOT_FOUND)
            .entity(GsonUtil.obj2Json(err, GsonUtil.ckanErrType)).build();
      }

    } catch (Exception e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

  }

  /**
   * All package search post.
   *
   * @param httpRequest the http request
   * @param input       the input
   * @return the response
   */
  @POST
  @Path("/api/{var:(3/?)?}action/package_search")
  @Produces("application/json")
  public Response all_package_search_post(@Context HttpServletRequest httpRequest,
      final String input) {

    try {

      JSONObject j = new JSONObject(input);

      int limit = -1;
      int offset = 0;

      if (StringUtils.isNotBlank(j.optString("rows", ""))) {
        limit = Integer.parseInt(j.optString("rows", ""));
      } else {
        limit = 20;
      }

      if (StringUtils.isNotBlank(j.optString("start", ""))) {
        offset = Integer.parseInt(j.optString("start", ""));
      } else {
        offset = 0;
      }

      String mappedQuery = CkanUtils.manageQuery(j.optString("q", ""), " ");
      String mappedSort = CkanUtils.manageSort(j.optString("sort", "metadata_modified desc"));
      // Adding catalogues ids
      List<String> ids = FederationCore.getOdmsCatalogues(false).stream().filter(x -> x.isActive())
          .map(x -> Integer.toString(x.getId())).collect(Collectors.toList());

      SearchResult result = FederatedSearch.searchByQuery(mappedQuery, mappedSort, limit, offset,
          ids);

      CkanSuccessResponse<CkanSearchResult> res = new CkanSuccessResponse<>();
      res.setHelp("");
      res.setResult(CkanUtils.toCkanSearchResult(result));

      return Response.status(Response.Status.OK)
          .entity(GsonUtil.obj2Json(res, GsonUtil.ckanSuccType)).build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

  }

  /**
   * Single package search post.
   *
   * @param httpRequest         the http request
   * @param catalogueIdentifier the catalogue identifier
   * @param input               the input
   * @return the response
   */
  @POST
  @Path("{catalogueID}/api/{var:(3/?)?}action/package_search")
  @Produces("application/json")
  public Response single_package_search_post(@Context HttpServletRequest httpRequest,
      @PathParam("catalogueID") String catalogueIdentifier, final String input) {

    try {

      JSONObject j = new JSONObject(input);

      int limit = -1;
      int offset = 0;

      if (StringUtils.isNotBlank(j.optString("rows", ""))) {
        limit = Integer.parseInt(j.optString("rows", ""));
      } else {
        limit = 20;
      }

      if (StringUtils.isNotBlank(j.optString("start", ""))) {
        offset = Integer.parseInt(j.optString("start", ""));
      } else {
        offset = 0;
      }

      String mappedQuery = CkanUtils.manageQuery(j.optString("q", ""), " ");
      String mappedSort = CkanUtils.manageSort(j.optString("sort", "metadata_modified desc"));
      // Adding catalogues ids
      try {
        OdmsCatalogue cat = FederationCore.getOdmsCatalogue(Integer.parseInt(catalogueIdentifier));
        if (cat.isActive()) {
          SearchResult result = FederatedSearch.searchByQuery(mappedQuery, mappedSort, limit,
              offset, Arrays.asList(catalogueIdentifier));

          CkanSuccessResponse<CkanSearchResult> res = new CkanSuccessResponse<>();
          res.setHelp("");
          res.setResult(CkanUtils.toCkanSearchResult(result));

          return Response.status(Response.Status.OK)
              .entity(GsonUtil.obj2Json(res, GsonUtil.ckanSuccType)).build();
        } else {
          CkanErrorResponse err = new CkanErrorResponse("",
              "Catalogue " + catalogueIdentifier + " not found", "Not Found");
          return Response.status(Response.Status.NOT_FOUND)
              .entity(GsonUtil.obj2Json(err, GsonUtil.ckanErrType)).build();
        }
      } catch (OdmsCatalogueNotFoundException e) {
        CkanErrorResponse err = new CkanErrorResponse("",
            "Catalogue " + catalogueIdentifier + " not found", "Not Found");
        return Response.status(Response.Status.NOT_FOUND)
            .entity(GsonUtil.obj2Json(err, GsonUtil.ckanErrType)).build();
      }

    } catch (Exception e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

  }

}
