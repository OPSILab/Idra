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

import it.eng.idra.statistics.PlatformStatistcs;
import it.eng.idra.statistics.PlatformStatisticsManager;
import it.eng.idra.utils.GsonUtil;
import it.eng.idra.utils.GsonUtilException;
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

// TODO: Auto-generated Javadoc
/**
 * The Class StatisticsApi.
 */
@Path("/statistics")
public class StatisticsApi {

  // private static Logger logger = LogManager.getLogger(StatisticsAPI.class);

  /**
   * getGlobalStatistics.
   *
   * @param httpRequest parameter
   * @param catalogueId parameter
   * @param startDate   parameter
   * @param endDate     parameter
   * @return the global statistics
   */
  @GET
  @Path("")
  @Produces("application/json")
  public Response getGlobalStatistics(@Context HttpServletRequest httpRequest,
      @QueryParam("catalogueID") @DefaultValue("") String catalogueId,
      @QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate) {

    try {

      // CatalogueID -> comma separated values of ids
      // if (StringUtils.isNotBlank(catalogueId)){}

      if (StringUtils.isBlank(endDate)) {
        ZonedDateTime end = ZonedDateTime.now();
        ZonedDateTime start = end.minusDays(7);
        endDate = end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).toString();
        startDate = start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).toString();
        ;
      }

      PlatformStatistcs stat = PlatformStatisticsManager.getCatalogueStatistics(catalogueId,
          startDate, endDate);
      return Response.status(Response.Status.OK)
          .entity(GsonUtil.obj2Json(stat, GsonUtil.platformStatsType)).build();
    } catch (GsonUtilException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

}
