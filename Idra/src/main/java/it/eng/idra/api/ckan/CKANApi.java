package it.eng.idra.api.ckan;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.eng.idra.beans.ErrorResponse;
import it.eng.idra.beans.ckan.CKANErrorResponse;
import it.eng.idra.beans.ckan.CKANSuccessResponse;
import it.eng.idra.beans.dcat.DCATDataset;
import it.eng.idra.beans.exception.DatasetNotFoundException;
import it.eng.idra.beans.odms.ODMSCatalogue;
import it.eng.idra.beans.odms.ODMSCatalogueNotFoundException;
import it.eng.idra.cache.MetadataCacheManager;
import it.eng.idra.management.FederationCore;
import it.eng.idra.utils.GsonUtil;

@Path("/ckan")
public class CKANApi {

	private static Logger logger = LogManager.getLogger(CKANApi.class);

	@GET
	@Path("/package_list")
	@Produces("application/json")
	public Response all_package_list(@Context HttpServletRequest httpRequest,
			@QueryParam("limit") String l, @QueryParam("offset") String o) {		

		try {

			//TO-DO solo i cataloghi attivi

			int limit=-1;
			int offset=0;

			if(StringUtils.isNotBlank(l)) {
				limit = Integer.parseInt(l);
			}

			if(StringUtils.isNotBlank(o)) {
				offset=Integer.parseInt(o);
			}

			CKANSuccessResponse<List<String>> res = new CKANSuccessResponse<>();
			res.setHelp("Return a list of the names of the site's datasets (packages). "
					+ ":param limit: if given, the list of datasets will be broken into pages of at most ``limit`` datasets per page and only one page will be returned at a time "
					+ "(optional) :type limit: int :param offset: when ``limit`` is given, "
					+ "the offset to start returning packages from :type offset: int :rtype: list of strings ");
			res.setSuccess(true);
			res.setResult(MetadataCacheManager.getAllDatasetsID(limit,offset));

			return Response.status(Response.Status.OK).entity(GsonUtil.obj2Json(res, GsonUtil.ckanSuccType)).build();
		}catch(Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}

	}

	@GET
	@Path("/{catalogueID}/package_list")
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

			CKANSuccessResponse<List<String>> res = new CKANSuccessResponse<>();
			res.setHelp("Return a list of the names of the site's datasets (packages). "
					+ ":param limit: if given, the list of datasets will be broken into pages of at most ``limit`` datasets per page and only one page will be returned at a time "
					+ "(optional) :type limit: int :param offset: when ``limit`` is given, "
					+ "the offset to start returning packages from :type offset: int :rtype: list of strings ");
			res.setSuccess(true);
			res.setResult(MetadataCacheManager.getAllDatasetsIDByCatalogue(catalogueID,limit,offset));

			return Response.status(Response.Status.OK).entity(GsonUtil.obj2Json(res, GsonUtil.ckanSuccType)).build();
		}catch(Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}

	}

	@GET
	@Path("/{catalogueID}/package_show")
	@Produces("application/json")
	public Response package_show(@Context HttpServletRequest httpRequest,
			@PathParam("catalogueID") String catalogueID,@QueryParam("id") String datasetID) {		

		try {

			if(StringUtils.isBlank(datasetID)) {
				CKANErrorResponse err = new CKANErrorResponse("", "Missing mandatory parameter id", "Validation error");
				return Response.status(Response.Status.CONFLICT).entity(GsonUtil.obj2Json(err, GsonUtil.ckanErrType)).build();
			}

			CKANSuccessResponse<DCATDataset> res = new CKANSuccessResponse<>();
			res.setHelp("");

			try {
				ODMSCatalogue cat = FederationCore.getODMSCatalogue(Integer.parseInt(catalogueID));
				if(cat.isActive()) {
					DCATDataset result = MetadataCacheManager.getDatasetByID(datasetID);
					if(result.getNodeID().equals(catalogueID)) {
						res.setResult(result);
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
	@Path("/package_show")
	@Produces("application/json")
	public Response all_package_show(@Context HttpServletRequest httpRequest,
			@QueryParam("id") String datasetID) {		

		try {

			if(StringUtils.isBlank(datasetID)) {
				CKANErrorResponse err = new CKANErrorResponse("", "Missing mandatory parameter id", "Validation error");
				return Response.status(Response.Status.CONFLICT).entity(GsonUtil.obj2Json(err, GsonUtil.ckanErrType)).build();
			}

			CKANSuccessResponse<DCATDataset> res = new CKANSuccessResponse<>();
			res.setHelp("");

			try {

				DCATDataset result = MetadataCacheManager.getDatasetByID(datasetID);
				res.setResult(result);

			}catch(DatasetNotFoundException e) {
				CKANErrorResponse err = new CKANErrorResponse("", "Package not found", "Not Found");
				return Response.status(Response.Status.NOT_FOUND).entity(GsonUtil.obj2Json(err, GsonUtil.ckanErrType)).build();
			}

			return Response.status(Response.Status.OK).entity(GsonUtil.obj2Json(res, GsonUtil.ckanSuccType)).build();
		}catch(Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}


	}

}
