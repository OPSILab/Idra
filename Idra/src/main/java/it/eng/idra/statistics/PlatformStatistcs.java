package it.eng.idra.statistics;

// TODO: Auto-generated Javadoc
/**
 * The Class PlatformStatistcs.
 */
public class PlatformStatistcs {

  /** The facets statistics. */
  private FacetsStatistics facetsStatistics;

  /** The catalogues statistics. */
  private CataloguesStatistics cataloguesStatistics;

  /**
   * Instantiates a new platform statistcs.
   */
  public PlatformStatistcs() {
    // TODO Auto-generated constructor stub
  }

  /**
   * Gets the facets.
   *
   * @return the facets
   */
  public FacetsStatistics getFacets() {
    return facetsStatistics;
  }

  /**
   * Sets the facets.
   *
   * @param facets the new facets
   */
  public void setFacets(FacetsStatistics facets) {
    this.facetsStatistics = facets;
  }

  /**
   * Gets the catalogues.
   *
   * @return the catalogues
   */
  public CataloguesStatistics getCatalogues() {
    return cataloguesStatistics;
  }

  /**
   * Sets the catalogues.
   *
   * @param catalogues the new catalogues
   */
  public void setCatalogues(CataloguesStatistics catalogues) {
    this.cataloguesStatistics = catalogues;
  }

}
