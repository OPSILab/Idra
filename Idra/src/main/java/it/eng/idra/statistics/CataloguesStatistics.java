package it.eng.idra.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import it.eng.idra.beans.dcat.DCATDataset;
import it.eng.idra.beans.odms.ODMSCatalogue;
import it.eng.idra.beans.odms.ODMSCatalogueType;

public class CataloguesStatistics {

	private List<DatasetCountStatistics> datasetCountStatistics;
	private List<TechnologiesCountStatistics> technologiesStat;
	private List<DatasetUpdatedStatistics> datasetUpdatedStat;
	
	public CataloguesStatistics() {
		// TODO Auto-generated constructor stub
	}
	
//	public CataloguesStatistics(List<DatasetCountStatistics> catalogueStatistics,List<TechnologiesCountStatistics> technologiesStat,List<DatasetUpdatedStatistics> datasetUpdatedStat) {
//		super();
//		this.datasetCountStatistics = catalogueStatistics;
//		this.technologiesStat=technologiesStat;
//		this.datasetUpdatedStat=datasetUpdatedStat;
//	}

	public CataloguesStatistics(List<ODMSCatalogue> catalogues,List<DCATDataset> added,List<DCATDataset> updated) {
		super();
		this.setDatasetCountStatistics(getDatasetCntStatFromCatalogues(catalogues));
		this.setTechnologiesStat(getTechStatFromCatalogues(catalogues));
		this.setDatasetUpdatedStat(getUpdatedAddedCatalogues(catalogues,added, updated));
	}
	
	public List<DatasetCountStatistics> getDatasetCountStatistics() {
		return datasetCountStatistics;
	}

	public void setDatasetCountStatistics(List<DatasetCountStatistics> catalogueStatistics) {
		this.datasetCountStatistics = catalogueStatistics;
	}
	
	public List<TechnologiesCountStatistics> getTechnologiesStat() {
		return technologiesStat;
	}

	public void setTechnologiesStat(List<TechnologiesCountStatistics> technologiesStat) {
		this.technologiesStat = technologiesStat;
	}

	public List<DatasetUpdatedStatistics> getDatasetUpdatedStat() {
		return datasetUpdatedStat;
	}

	public void setDatasetUpdatedStat(List<DatasetUpdatedStatistics> datasetUpdatedStat) {
		this.datasetUpdatedStat = datasetUpdatedStat;
	}

	private List<DatasetCountStatistics> getDatasetCntStatFromCatalogues(List<ODMSCatalogue> catalogues){
		return catalogues.stream().map(x -> {
			return new DatasetCountStatistics(x.getName(),x.getDatasetCount());
		}).collect(Collectors.toList());
	}
	
	private List<TechnologiesCountStatistics> getTechStatFromCatalogues(List<ODMSCatalogue> catalogues){
		
		List<TechnologiesCountStatistics> res = new ArrayList<TechnologiesCountStatistics>();
		HashMap<ODMSCatalogueType, Integer> map = new HashMap<ODMSCatalogueType,Integer>();
		for(ODMSCatalogue c : catalogues) {
			if(map.containsKey(c.getNodeType())) {
				int v = map.get(c.getNodeType());
				map.put(c.getNodeType(), ++v);
			}else {
				map.put(c.getNodeType(), 1);
			}
		}
		
		for(ODMSCatalogueType t : map.keySet()) {
			res.add(new TechnologiesCountStatistics(t, map.get(t)));
		}	
		return res;
	}
	
	private List<DatasetUpdatedStatistics> getUpdatedAddedCatalogues(List<ODMSCatalogue> catalogues,List<DCATDataset> added,List<DCATDataset> updated){
		
		List<DatasetUpdatedStatistics> res = new ArrayList<DatasetUpdatedStatistics>();
		
		for(ODMSCatalogue c : catalogues) {
			DatasetUpdatedStatistics d = new DatasetUpdatedStatistics();
			d.setName(c.getName());
			d.setAdded((int) added.stream().filter(x->Integer.parseInt(x.getNodeID())==c.getId()).count());
			//NB: gli updated mi arrivano filtrati qui
			d.setUpdated((int) updated.stream().filter(x->Integer.parseInt(x.getNodeID())==c.getId()).count());
			res.add(d);
		}
		
		//return res.stream().filter(x->x.getAdded()>0 || x.getUpdated()>0).collect(Collectors.toList());
		return res.stream().collect(Collectors.toList());
	}
		
}
