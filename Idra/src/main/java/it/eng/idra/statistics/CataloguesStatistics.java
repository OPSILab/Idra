package it.eng.idra.statistics;

import it.eng.idra.beans.dcat.DcatDataset;
import it.eng.idra.beans.odms.OdmsCatalogue;
import it.eng.idra.beans.odms.OdmsCatalogueType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CataloguesStatistics {

  private List<DatasetCountStatistics> datasetCountStatistics;
  private List<TechnologiesCountStatistics> technologiesStat;
  private List<DatasetUpdatedStatistics> datasetUpdatedStat;

  public CataloguesStatistics() {
    // TODO Auto-generated constructor stub
  }

  /**
   * Instantiates a new catalogues statistics.
   *
   * @param catalogues the catalogues
   * @param added the added
   * @param updated the updated
   */
  public CataloguesStatistics(List<OdmsCatalogue> catalogues, 
      List<DcatDataset> added, List<DcatDataset> updated) {
    super();
    this.setDatasetCountStatistics(getDatasetCntStatFromCatalogues(catalogues));
    this.setTechnologiesStat(getTechStatFromCatalogues(catalogues));
    this.setDatasetUpdatedStat(getUpdatedAddedCatalogues(catalogues, added, updated));
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

  private List<DatasetCountStatistics> getDatasetCntStatFromCatalogues(
      List<OdmsCatalogue> catalogues) {
    return catalogues.stream().map(x -> {
      return new DatasetCountStatistics(x.getName(), x.getDatasetCount());
    }).collect(Collectors.toList());
  }

  private List<TechnologiesCountStatistics> getTechStatFromCatalogues(
      List<OdmsCatalogue> catalogues) {

    List<TechnologiesCountStatistics> res = new ArrayList<TechnologiesCountStatistics>();
    HashMap<OdmsCatalogueType, Integer> map = new HashMap<OdmsCatalogueType, Integer>();
    for (OdmsCatalogue c : catalogues) {
      if (map.containsKey(c.getNodeType())) {
        int v = map.get(c.getNodeType());
        map.put(c.getNodeType(), ++v);
      } else {
        map.put(c.getNodeType(), 1);
      }
    }

    for (OdmsCatalogueType t : map.keySet()) {
      res.add(new TechnologiesCountStatistics(t, map.get(t)));
    }
    return res;
  }

  private List<DatasetUpdatedStatistics> getUpdatedAddedCatalogues(List<OdmsCatalogue> catalogues,
      List<DcatDataset> added, List<DcatDataset> updated) {

    List<DatasetUpdatedStatistics> res = new ArrayList<DatasetUpdatedStatistics>();

    for (OdmsCatalogue c : catalogues) {
      DatasetUpdatedStatistics d = new DatasetUpdatedStatistics();
      d.setName(c.getName());
      d.setAdded((int) added.stream()
          .filter(x -> Integer.parseInt(x.getNodeID()) == c.getId()).count());
      // NB: gli updated mi arrivano filtrati qui
      d.setUpdated((int) updated.stream()
          .filter(x -> Integer.parseInt(x.getNodeID()) == c.getId()).count());
      res.add(d);
    }

    // return res.stream().filter(x->x.getAdded()>0 ||
    // x.getUpdated()>0).collect(Collectors.toList());
    return res.stream().collect(Collectors.toList());
  }

}
