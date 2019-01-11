package it.eng.idra.statistics;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import it.eng.idra.beans.odms.ODMSCatalogue;
import it.eng.idra.beans.odms.ODMSSynchLock;
import it.eng.idra.beans.search.SearchResult;
import it.eng.idra.management.FederationCore;
import it.eng.idra.search.FederatedSearch;
import it.eng.idra.utils.CommonUtil;

public class PlatformStatisticsManager {

	public PlatformStatisticsManager() {
		// TODO Auto-generated constructor stub
	}

	
	public static PlatformStatistcs getCatalogueStatistics(String catalogueID,String startDate, String endDate) {
		PlatformStatistcs result= new PlatformStatistcs();
		//Prendiamo tutti i cataloghi attivi
		try {
			startDate = CommonUtil.fixBadUTCDate(startDate);
			endDate = CommonUtil.fixBadUTCDate(endDate);
			
			List<ODMSCatalogue> nodes = FederationCore.getODMSCatalogues()
					.stream()
					.filter(x -> x.isActive() && x.isCacheable() && !x.getSynchLock().equals(ODMSSynchLock.FIRST))
					.collect(Collectors.toList());
					
			if(StringUtils.isNotBlank(catalogueID)) {
				List<Integer> ids = Arrays.asList(catalogueID.split(",")).stream().map(x->Integer.parseInt(x)).collect(Collectors.toList());
				nodes =	nodes.stream().filter( x -> ids.contains(x.getId()) ).collect(Collectors.toList());
			}
			
			List<Integer> cataloguesIDS = nodes.stream().map(x -> x.getId()).collect(Collectors.toList());
				
			//Prendo le facets con rows=0;
			HashMap<String, Object> searchParameters = new HashMap<String, Object>();
			searchParameters.put("live", false);
			searchParameters.put("euroVoc",false);
			searchParameters.put("rows", "0");
			searchParameters.put("start", "0");
			searchParameters.put("sort","id,asc");
			searchParameters.put("nodes", cataloguesIDS);

			SearchResult resultForFacets = FederatedSearch.search(searchParameters);
			/*MANAGE FACETS*/
			FacetsStatistics facetsStats = new FacetsStatistics(
					resultForFacets.getFacets().stream().
					filter(x->x.getSearch_parameter().equals("distributionFormats")).
					map(x-> x.getValues()).findFirst().get(),
					resultForFacets.getFacets().stream().
					filter(x->x.getSearch_parameter().equals("distributionLicenses")).
					map(x-> x.getValues()).findFirst().get()
					);
			
			searchParameters.put("live", false);
			searchParameters.put("euroVoc",false);
			searchParameters.put("start", "0");
			searchParameters.put("sort","id,asc");
//			searchParameters.put("nodes", cataloguesIDS);
			searchParameters.put("rows", resultForFacets.getCount().toString());
			String dateArray[]= {startDate,endDate};
			searchParameters.put("releaseDate", dateArray);
			SearchResult resultForRelease = FederatedSearch.search(searchParameters);
			searchParameters.remove("releaseDate");
			
			searchParameters.put("live", false);
			searchParameters.put("euroVoc",false);
			searchParameters.put("start", "0");
			searchParameters.put("sort","id,asc");
//			searchParameters.put("nodes", cataloguesIDS);
			searchParameters.put("rows", resultForFacets.getCount().toString());
			
			searchParameters.put("updateDate", dateArray);
			SearchResult resultForUpdate = FederatedSearch.search(searchParameters);
			
			ZonedDateTime start = ZonedDateTime.parse(startDate);
			CataloguesStatistics ctlgStat = new CataloguesStatistics(nodes, resultForRelease.getResults(), 
					resultForUpdate.getResults().stream().filter(x-> start.isAfter(ZonedDateTime.parse(x.getReleaseDate().getValue())) )
					.collect(Collectors.toList()));
			
			
			result.setCatalogues(ctlgStat);
			result.setFacets(facetsStats);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
}
