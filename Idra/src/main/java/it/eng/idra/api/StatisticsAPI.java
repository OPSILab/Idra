package it.eng.idra.api;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import it.eng.idra.statistics.PlatformStatistcs;
import it.eng.idra.statistics.PlatformStatisticsManager;
import it.eng.idra.utils.GsonUtil;
import it.eng.idra.utils.GsonUtilException;

@Path("/statistics")
public class StatisticsAPI {

	//private static Logger logger = LogManager.getLogger(StatisticsAPI.class);


	@GET
	@Path("")
	@Produces("application/json")
	public Response getGlobalStatistics(@Context HttpServletRequest httpRequest,
			@QueryParam("catalogueID") @DefaultValue("") String catalogueID,@QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate) {		
		
		try {
			
			//CatalogueID -> comma separated values of ids
			if(StringUtils.isNotBlank(catalogueID)) {
				
			}
						
			if(StringUtils.isBlank(endDate)) { 
				ZonedDateTime end = ZonedDateTime.now();
				ZonedDateTime start = end.minusDays(7);
				endDate = end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).toString();
				startDate=start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).toString();;
			}
			
			PlatformStatistcs stat= PlatformStatisticsManager.getCatalogueStatistics(catalogueID, startDate, endDate);
			return Response.status(Response.Status.OK).entity(GsonUtil.obj2Json(stat, GsonUtil.platformStatsType)).build();
		} catch (GsonUtilException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
}
