package it.eng.idra.statistics;

public class PlatformStatistcs {

  private FacetsStatistics facetsStatistics;
  private CataloguesStatistics cataloguesStatistics;

  public PlatformStatistcs() {
    // TODO Auto-generated constructor stub
  }

  public FacetsStatistics getFacets() {
    return facetsStatistics;
  }

  public void setFacets(FacetsStatistics facets) {
    this.facetsStatistics = facets;
  }

  public CataloguesStatistics getCatalogues() {
    return cataloguesStatistics;
  }

  public void setCatalogues(CataloguesStatistics catalogues) {
    this.cataloguesStatistics = catalogues;
  }

}
