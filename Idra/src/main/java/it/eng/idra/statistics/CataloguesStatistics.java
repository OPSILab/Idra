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
package it.eng.idra.statistics;

import it.eng.idra.beans.dcat.DcatDataset;
import it.eng.idra.beans.odms.OdmsCatalogue;
import it.eng.idra.beans.odms.OdmsCatalogueType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

// TODO: Auto-generated Javadoc
/**
 * The Class CataloguesStatistics.
 */
public class CataloguesStatistics {

  /** The dataset count statistics. */
  private List<DatasetCountStatistics> datasetCountStatistics;

  /** The technologies stat. */
  private List<TechnologiesCountStatistics> technologiesStat;

  /** The dataset updated stat. */
  private List<DatasetUpdatedStatistics> datasetUpdatedStat;

  /**
   * Instantiates a new catalogues statistics.
   */
  public CataloguesStatistics() {
    // TODO Auto-generated constructor stub
  }

  /**
   * Instantiates a new catalogues statistics.
   *
   * @param catalogues the catalogues
   * @param added      the added
   * @param updated    the updated
   */
  public CataloguesStatistics(List<OdmsCatalogue> catalogues, List<DcatDataset> added,
      List<DcatDataset> updated) {
    super();
    this.setDatasetCountStatistics(getDatasetCntStatFromCatalogues(catalogues));
    this.setTechnologiesStat(getTechStatFromCatalogues(catalogues));
    this.setDatasetUpdatedStat(getUpdatedAddedCatalogues(catalogues, added, updated));
  }

  /**
   * Gets the dataset count statistics.
   *
   * @return the dataset count statistics
   */
  public List<DatasetCountStatistics> getDatasetCountStatistics() {
    return datasetCountStatistics;
  }

  /**
   * Sets the dataset count statistics.
   *
   * @param catalogueStatistics the new dataset count statistics
   */
  public void setDatasetCountStatistics(List<DatasetCountStatistics> catalogueStatistics) {
    this.datasetCountStatistics = catalogueStatistics;
  }

  /**
   * Gets the technologies stat.
   *
   * @return the technologies stat
   */
  public List<TechnologiesCountStatistics> getTechnologiesStat() {
    return technologiesStat;
  }

  /**
   * Sets the technologies stat.
   *
   * @param technologiesStat the new technologies stat
   */
  public void setTechnologiesStat(List<TechnologiesCountStatistics> technologiesStat) {
    this.technologiesStat = technologiesStat;
  }

  /**
   * Gets the dataset updated stat.
   *
   * @return the dataset updated stat
   */
  public List<DatasetUpdatedStatistics> getDatasetUpdatedStat() {
    return datasetUpdatedStat;
  }

  /**
   * Sets the dataset updated stat.
   *
   * @param datasetUpdatedStat the new dataset updated stat
   */
  public void setDatasetUpdatedStat(List<DatasetUpdatedStatistics> datasetUpdatedStat) {
    this.datasetUpdatedStat = datasetUpdatedStat;
  }

  /**
   * Gets the dataset cnt stat from catalogues.
   *
   * @param catalogues the catalogues
   * @return the dataset cnt stat from catalogues
   */
  private List<DatasetCountStatistics> getDatasetCntStatFromCatalogues(
      List<OdmsCatalogue> catalogues) {
    return catalogues.stream().map(x -> {
      return new DatasetCountStatistics(x.getName(), x.getDatasetCount());
    }).collect(Collectors.toList());
  }

  /**
   * Gets the tech stat from catalogues.
   *
   * @param catalogues the catalogues
   * @return the tech stat from catalogues
   */
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

  /**
   * Gets the updated added catalogues.
   *
   * @param catalogues the catalogues
   * @param added      the added
   * @param updated    the updated
   * @return the updated added catalogues
   */
  private List<DatasetUpdatedStatistics> getUpdatedAddedCatalogues(List<OdmsCatalogue> catalogues,
      List<DcatDataset> added, List<DcatDataset> updated) {

    List<DatasetUpdatedStatistics> res = new ArrayList<DatasetUpdatedStatistics>();

    for (OdmsCatalogue c : catalogues) {
      DatasetUpdatedStatistics d = new DatasetUpdatedStatistics();
      d.setName(c.getName());
      d.setAdded(
          (int) added.stream().filter(x -> Integer.parseInt(x.getNodeId()) == c.getId()).count());
      // NB: gli updated mi arrivano filtrati qui
      d.setUpdated(
          (int) updated.stream().filter(x -> Integer.parseInt(x.getNodeId()) == c.getId()).count());
      res.add(d);
    }

    // return res.stream().filter(x->x.getAdded()>0 ||
    // x.getUpdated()>0).collect(Collectors.toList());
    return res.stream().collect(Collectors.toList());
  }

}
