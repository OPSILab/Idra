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

package it.eng.idra.statistics;

import it.eng.idra.beans.odms.OdmsCatalogue;
import it.eng.idra.beans.odms.OdmsSynchLock;
import it.eng.idra.beans.search.SearchResult;
import it.eng.idra.management.FederationCore;
import it.eng.idra.search.FederatedSearch;
import it.eng.idra.utils.CommonUtil;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class PlatformStatisticsManager.
 */
public class PlatformStatisticsManager {

  /**
   * Instantiates a new platform statistics manager.
   */
  public PlatformStatisticsManager() {
    // TODO Auto-generated constructor stub
  }

  /**
   * Gets the catalogue statistics.
   *
   * @param catalogueId the catalogue id
   * @param startDate   the start date
   * @param endDate     the end date
   * @return the catalogue statistics
   */
  public static PlatformStatistcs getCatalogueStatistics(String catalogueId, String startDate,
      String endDate) {
    PlatformStatistcs result = new PlatformStatistcs();
    // Prendiamo tutti i cataloghi attivi
    try {
      startDate = CommonUtil.fixBadUtcDate(startDate);
      endDate = CommonUtil.fixBadUtcDate(endDate);

      List<OdmsCatalogue> nodes = FederationCore.getOdmsCatalogues().stream()
          .filter(
              x -> x.isActive() && x.isCacheable() && !x.getSynchLock().equals(OdmsSynchLock.FIRST))
          .collect(Collectors.toList());

      if (StringUtils.isNotBlank(catalogueId)) {
        List<Integer> ids = Arrays.asList(catalogueId.split(",")).stream()
            .map(x -> Integer.parseInt(x)).collect(Collectors.toList());
        nodes = nodes.stream().filter(x -> ids.contains(x.getId())).collect(Collectors.toList());
      }

      List<Integer> cataloguesIds = nodes.stream().map(x -> x.getId()).collect(Collectors.toList());

      // Prendo le facets con rows=0;
      HashMap<String, Object> searchParameters = new HashMap<String, Object>();
      searchParameters.put("live", false);
      searchParameters.put("euroVoc", false);
      searchParameters.put("rows", "0");
      searchParameters.put("start", "0");
      searchParameters.put("sort", "id,asc");
      searchParameters.put("nodes", cataloguesIds);

      SearchResult resultForFacets = FederatedSearch.search(searchParameters);
      SearchResult distributionFormats = FederatedSearch.getFormatStatistics(searchParameters);

      /* MANAGE FACETS */
      FacetsStatistics facetsStats = new FacetsStatistics(
          distributionFormats.getFacets().stream()
              .filter(x -> x.getSearchParameter().equals("format")).map(x -> x.getValues())
              .findFirst().get(),

          resultForFacets.getFacets().stream()
              .filter(x -> x.getSearchParameter().equals("distributionLicenses"))
              .map(x -> x.getValues()).findFirst().get(),

          resultForFacets.getFacets().stream()
              .filter(x -> x.getSearchParameter().equals("datasetThemes")).map(x -> x.getValues())
              .findFirst().get());
      result.setFacets(facetsStats);

      searchParameters.put("live", false);
      searchParameters.put("euroVoc", false);
      searchParameters.put("start", "0");
      searchParameters.put("sort", "id,asc");
      searchParameters.put("rows", resultForFacets.getCount().toString());
      String[] dateArray = { startDate, endDate };
      searchParameters.put("releaseDate", dateArray);
      SearchResult resultForRelease = FederatedSearch.search(searchParameters);
      searchParameters.remove("releaseDate");

      searchParameters.put("live", false);
      searchParameters.put("euroVoc", false);
      searchParameters.put("start", "0");
      searchParameters.put("sort", "id,asc");
      searchParameters.put("rows", resultForFacets.getCount().toString());

      searchParameters.put("updateDate", dateArray);
      SearchResult resultForUpdate = FederatedSearch.search(searchParameters);

      ZonedDateTime start = ZonedDateTime.parse(startDate);
      CataloguesStatistics ctlgStat = new CataloguesStatistics(nodes, resultForRelease.getResults(),
          resultForUpdate.getResults().stream()
              .filter(x -> start.isAfter(ZonedDateTime.parse(x.getReleaseDate().getValue())))
              .collect(Collectors.toList()));

      result.setCatalogues(ctlgStat);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return result;
  }

}
