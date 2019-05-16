package it.eng.idra.api.ckan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ckan.Dataset;
import org.json.JSONObject;

import it.eng.idra.beans.ErrorResponse;
import it.eng.idra.beans.ckan.CKANErrorResponse;
import it.eng.idra.beans.ckan.CKANSuccessResponse;
import it.eng.idra.beans.dcat.DCATDataset;
import it.eng.idra.beans.exception.DatasetNotFoundException;
import it.eng.idra.beans.odms.ODMSCatalogue;
import it.eng.idra.beans.odms.ODMSCatalogueNotFoundException;
import it.eng.idra.beans.search.SearchResult;
import it.eng.idra.cache.MetadataCacheManager;
import it.eng.idra.management.FederationCore;
import it.eng.idra.search.FederatedSearch;
import it.eng.idra.utils.GsonUtil;

@Path("/")
public class CKANApi {

	private static Logger logger = LogManager.getLogger(CKANApi.class);

	@GET
	@Path("/api/action/package_list")
	@Produces("application/json")
	public Response all_package_list(@Context HttpServletRequest httpRequest,
			@QueryParam("limit") String l, @QueryParam("offset") String o) {		

		try {

			//TO-DO solo i cataloghi attivi

			int limit=-1;
			int offset=0;

			if(StringUtils.isNotBlank(l)) {
				limit = Integer.parseInt(l);
			}else {
				//Default limit a 1000
				limit=1000;
			}

			if(StringUtils.isNotBlank(o)) {
				offset=Integer.parseInt(o);
			}

			CKANSuccessResponse<List<String>> res = new CKANSuccessResponse<>();
			res.setHelp("Return a list of the names of the site's datasets (packages). "
					+ ":param limit: if given, the list of datasets will be broken into pages of at most ``limit`` datasets per page and only one page will be returned at a time "
					+ "(optional) :type limit: int :param offset: when ``limit`` is given, "
					+ "the offset to start returning packages from :type offset: int :rtype: list of strings; Limit default value is 1000 ");
			res.setSuccess(true);
			res.setResult(MetadataCacheManager.getAllDatasetsID(limit,offset));

			return Response.status(Response.Status.OK).entity(GsonUtil.obj2Json(res, GsonUtil.ckanSuccType)).build();
		}catch(Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}

	}

	@GET
	@Path("/{catalogueID}/api/action/package_list")
	@Produces("application/json")
	public Response package_list(@Context HttpServletRequest httpRequest,
			@PathParam("catalogueID") String catalogueID,@QueryParam("limit") String l, @QueryParam("offset") String o) {		

		try {
			int limit=-1;
			int offset=0;

			if(StringUtils.isNotBlank(l)) {
				limit = Integer.parseInt(l);
			}

			if(StringUtils.isNotBlank(o)) {
				offset=Integer.parseInt(o);
			}

			try {
				ODMSCatalogue cat = FederationCore.getODMSCatalogue(Integer.parseInt(catalogueID));
				if(cat.isActive()) {
					CKANSuccessResponse<List<String>> res = new CKANSuccessResponse<>();
					res.setHelp("Return a list of the names of the site's datasets (packages). "
							+ ":param limit: if given, the list of datasets will be broken into pages of at most ``limit`` datasets per page and only one page will be returned at a time "
							+ "(optional) :type limit: int :param offset: when ``limit`` is given, "
							+ "the offset to start returning packages from :type offset: int :rtype: list of strings ");
					res.setSuccess(true);
					res.setResult(MetadataCacheManager.getAllDatasetsIDByCatalogue(catalogueID,limit,offset));

					return Response.status(Response.Status.OK).entity(GsonUtil.obj2Json(res, GsonUtil.ckanSuccType)).build();
				}else {
					CKANErrorResponse err = new CKANErrorResponse("", "Catalogue "+catalogueID+" not found", "Not Found");
					return Response.status(Response.Status.NOT_FOUND).entity(GsonUtil.obj2Json(err, GsonUtil.ckanErrType)).build();
				}
			}catch(ODMSCatalogueNotFoundException e) {
				CKANErrorResponse err = new CKANErrorResponse("", "Catalogue "+catalogueID+" not found", "Not Found");
				return Response.status(Response.Status.NOT_FOUND).entity(GsonUtil.obj2Json(err, GsonUtil.ckanErrType)).build();
			}
			
		}catch(Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}

	}

	@GET
	@Path("/{catalogueID}/api/action/package_show")
	@Produces("application/json")
	public Response package_show(@Context HttpServletRequest httpRequest,
			@PathParam("catalogueID") String catalogueID,@QueryParam("id") String datasetID) {		

		try {

			if(StringUtils.isBlank(datasetID)) {
				CKANErrorResponse err = new CKANErrorResponse("", "Missing mandatory parameter id", "Validation error");
				return Response.status(Response.Status.CONFLICT).entity(GsonUtil.obj2Json(err, GsonUtil.ckanErrType)).build();
			}

			CKANSuccessResponse<Dataset> res = new CKANSuccessResponse<>();
			res.setHelp("");

			try {
				ODMSCatalogue cat = FederationCore.getODMSCatalogue(Integer.parseInt(catalogueID));
				if(cat.isActive()) {
					DCATDataset result = MetadataCacheManager.getDatasetByID(datasetID);
					if(result.getNodeID().equals(catalogueID)) {
						res.setResult(CKANUtils.toCkanDataset(result));
					}else {
						CKANErrorResponse err = new CKANErrorResponse("", "Package not found for catalogue: "+catalogueID, "Not Found");
						return Response.status(Response.Status.NOT_FOUND).entity(GsonUtil.obj2Json(err, GsonUtil.ckanErrType)).build();
					}
				}else {
					CKANErrorResponse err = new CKANErrorResponse("", "Package not found", "Not Found");
					return Response.status(Response.Status.NOT_FOUND).entity(GsonUtil.obj2Json(err, GsonUtil.ckanErrType)).build();
				}
			}catch(DatasetNotFoundException e) {
				CKANErrorResponse err = new CKANErrorResponse("", "Catalogue "+catalogueID+" not found", "Not Found");
				return Response.status(Response.Status.NOT_FOUND).entity(GsonUtil.obj2Json(err, GsonUtil.ckanErrType)).build();
			}catch(ODMSCatalogueNotFoundException e) {
				CKANErrorResponse err = new CKANErrorResponse("", "Catalogue "+catalogueID+" not found", "Not Found");
				return Response.status(Response.Status.NOT_FOUND).entity(GsonUtil.obj2Json(err, GsonUtil.ckanErrType)).build();
			}

			return Response.status(Response.Status.OK).entity(GsonUtil.obj2Json(res, GsonUtil.ckanSuccType)).build();
		}catch(Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}

	}

	@GET
	@Path("/api/action/package_show")
	@Produces("application/json")
	public Response all_package_show(@Context HttpServletRequest httpRequest,
			@QueryParam("id") String datasetID) {		

		try {

			if(StringUtils.isBlank(datasetID)) {
				CKANErrorResponse err = new CKANErrorResponse("", "Missing mandatory parameter id", "Validation error");
				return Response.status(Response.Status.CONFLICT).entity(GsonUtil.obj2Json(err, GsonUtil.ckanErrType)).build();
			}

			CKANSuccessResponse<Dataset> res = new CKANSuccessResponse<>();
			res.setHelp("");

			try {

				DCATDataset result = MetadataCacheManager.getDatasetByID(datasetID);
				res.setResult(CKANUtils.toCkanDataset(result));

			}catch(DatasetNotFoundException e) {
				CKANErrorResponse err = new CKANErrorResponse("", "Package not found", "Not Found");
				return Response.status(Response.Status.NOT_FOUND).entity(GsonUtil.obj2Json(err, GsonUtil.ckanErrType)).build();
			}

			return Response.status(Response.Status.OK).entity(GsonUtil.obj2Json(res, GsonUtil.ckanSuccType)).build();
		}catch(Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}

	}
	
	@GET
	@Path("/api/action/package_search")
	@Produces("application/json")
	public Response all_package_search(@Context HttpServletRequest httpRequest,
			@QueryParam("q") @DefaultValue("*:*") String query,@QueryParam("start") @DefaultValue("0") String start, @QueryParam("rows") @DefaultValue("20") String rows, @QueryParam("sort") @DefaultValue("metadata_modified desc") String sort ) {		

		try {
			
			int limit=-1;
			int offset=0;

			if(StringUtils.isNotBlank(rows)) {
				limit = Integer.parseInt(rows);
			}

			if(StringUtils.isNotBlank(start)) {
				offset=Integer.parseInt(start);
			}
			
			String mappedQuery = CKANUtils.manageQuery(query, " ");
			String mappedSort = CKANUtils.manageSort(sort);
			//Adding catalogues ids
			List<String> ids = FederationCore.getODMSCatalogues(false).stream().filter(x -> x.isActive()).map(x -> Integer.toString(x.getId())).collect(Collectors.toList());

			SearchResult result = FederatedSearch.searchByQuery(mappedQuery, mappedSort, limit, offset, ids);
			
			CKANSuccessResponse<CKANSearchResult> res = new CKANSuccessResponse<>();
			res.setHelp("");
			res.setResult(CKANUtils.toCkanSearchResult(result));
			
			return Response.status(Response.Status.OK).entity(GsonUtil.obj2Json(res, GsonUtil.ckanSuccType)).build();
		}catch(Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}

	}

	@GET
	@Path("{catalogueID}/api/action/package_search")
	@Produces("application/json")
	public Response single_package_search(@Context HttpServletRequest httpRequest,@PathParam("catalogueID") String catalogueID,
			@QueryParam("q") @DefaultValue("*:*") String query,@QueryParam("start") @DefaultValue("0") String start, @QueryParam("rows") @DefaultValue("20") String rows, @QueryParam("sort") @DefaultValue("metadata_modified desc") String sort ) {		

		try {
			
			int limit=-1;
			int offset=0;

			if(StringUtils.isNotBlank(rows)) {
				limit = Integer.parseInt(rows);
			}

			if(StringUtils.isNotBlank(start)) {
				offset=Integer.parseInt(start);
			}
			
			String mappedQuery = CKANUtils.manageQuery(query, " ");
			String mappedSort = CKANUtils.manageSort(sort);
			//Adding catalogues ids
			try {
				ODMSCatalogue cat = FederationCore.getODMSCatalogue(Integer.parseInt(catalogueID));
				if(cat.isActive()) {
					SearchResult result = FederatedSearch.searchByQuery(mappedQuery, mappedSort, limit, offset, Arrays.asList(catalogueID));
					
					CKANSuccessResponse<CKANSearchResult> res = new CKANSuccessResponse<>();
					res.setHelp("");
					res.setResult(CKANUtils.toCkanSearchResult(result));
					
					return Response.status(Response.Status.OK).entity(GsonUtil.obj2Json(res, GsonUtil.ckanSuccType)).build();
				}else {
					CKANErrorResponse err = new CKANErrorResponse("", "Catalogue "+catalogueID+" not found", "Not Found");
					return Response.status(Response.Status.NOT_FOUND).entity(GsonUtil.obj2Json(err, GsonUtil.ckanErrType)).build();
				}
			}catch(ODMSCatalogueNotFoundException e) {
				CKANErrorResponse err = new CKANErrorResponse("", "Catalogue "+catalogueID+" not found", "Not Found");
				return Response.status(Response.Status.NOT_FOUND).entity(GsonUtil.obj2Json(err, GsonUtil.ckanErrType)).build();
			}

		}catch(Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}

	}

	@POST
	@Path("/api/action/package_search")
	@Produces("application/json")
	public Response all_package_search_post(@Context HttpServletRequest httpRequest,final String input) {		

		try {
			
			JSONObject j = new JSONObject(input);
					
			int limit=-1;
			int offset=0;

			if(StringUtils.isNotBlank(j.optString("rows", ""))) {
				limit = Integer.parseInt(j.optString("rows", ""));
			}else {
				limit=20;
			}

			if(StringUtils.isNotBlank(j.optString("start", ""))) {
				offset=Integer.parseInt(j.optString("start", ""));
			}else {
				offset=0;
			}
			
			String mappedQuery = CKANUtils.manageQuery(j.optString("q", ""), " ");
			String mappedSort = CKANUtils.manageSort(j.optString("sort", "metadata_modified desc"));
			//Adding catalogues ids
			List<String> ids = FederationCore.getODMSCatalogues(false).stream().filter(x -> x.isActive()).map(x -> Integer.toString(x.getId())).collect(Collectors.toList());

			SearchResult result = FederatedSearch.searchByQuery(mappedQuery, mappedSort, limit, offset, ids);
			
			CKANSuccessResponse<CKANSearchResult> res = new CKANSuccessResponse<>();
			res.setHelp("");
			res.setResult(CKANUtils.toCkanSearchResult(result));
			
			return Response.status(Response.Status.OK).entity(GsonUtil.obj2Json(res, GsonUtil.ckanSuccType)).build();
		}catch(Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}

	}

	@POST
	@Path("{catalogueID}/api/action/package_search")
	@Produces("application/json")
	public Response single_package_search_post(@Context HttpServletRequest httpRequest,@PathParam("catalogueID") String catalogueID,final String input) {		

		try {
			
			JSONObject j = new JSONObject(input);
			
			int limit=-1;
			int offset=0;

			if(StringUtils.isNotBlank(j.optString("rows", ""))) {
				limit = Integer.parseInt(j.optString("rows", ""));
			}else {
				limit=20;
			}

			if(StringUtils.isNotBlank(j.optString("start", ""))) {
				offset=Integer.parseInt(j.optString("start", ""));
			}else {
				offset=0;
			}
			
			String mappedQuery = CKANUtils.manageQuery(j.optString("q", ""), " ");
			String mappedSort = CKANUtils.manageSort(j.optString("sort", "metadata_modified desc"));
			//Adding catalogues ids
			try {
				ODMSCatalogue cat = FederationCore.getODMSCatalogue(Integer.parseInt(catalogueID));
				if(cat.isActive()) {
					SearchResult result = FederatedSearch.searchByQuery(mappedQuery, mappedSort, limit, offset, Arrays.asList(catalogueID));
					
					CKANSuccessResponse<CKANSearchResult> res = new CKANSuccessResponse<>();
					res.setHelp("");
					res.setResult(CKANUtils.toCkanSearchResult(result));
					
					return Response.status(Response.Status.OK).entity(GsonUtil.obj2Json(res, GsonUtil.ckanSuccType)).build();
				}else {
					CKANErrorResponse err = new CKANErrorResponse("", "Catalogue "+catalogueID+" not found", "Not Found");
					return Response.status(Response.Status.NOT_FOUND).entity(GsonUtil.obj2Json(err, GsonUtil.ckanErrType)).build();
				}
			}catch(ODMSCatalogueNotFoundException e) {
				CKANErrorResponse err = new CKANErrorResponse("", "Catalogue "+catalogueID+" not found", "Not Found");
				return Response.status(Response.Status.NOT_FOUND).entity(GsonUtil.obj2Json(err, GsonUtil.ckanErrType)).build();
			}

		}catch(Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}

	}
	
}
