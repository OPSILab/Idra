package it.eng.idra.statistics;

import it.eng.idra.beans.odms.OdmsCatalogueType;

public class TechnologiesCountStatistics {

  private OdmsCatalogueType type;
  private int count;

  public TechnologiesCountStatistics() {
    // TODO Auto-generated constructor stub
  }

  /**
   * Instantiates a new technologies count statistics.
   *
   * @param type the type
   * @param count the count
   */
  public TechnologiesCountStatistics(OdmsCatalogueType type, int count) {
    super();
    this.type = type;
    this.count = count;
  }

  public OdmsCatalogueType getType() {
    return type;
  }

  public void setType(OdmsCatalogueType type) {
    this.type = type;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

}
