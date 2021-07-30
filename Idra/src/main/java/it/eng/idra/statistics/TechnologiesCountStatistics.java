package it.eng.idra.statistics;

import it.eng.idra.beans.odms.OdmsCatalogueType;

// TODO: Auto-generated Javadoc
/**
 * The Class TechnologiesCountStatistics.
 */
public class TechnologiesCountStatistics {

  /** The type. */
  private OdmsCatalogueType type;

  /** The count. */
  private int count;

  /**
   * Instantiates a new technologies count statistics.
   */
  public TechnologiesCountStatistics() {
    // TODO Auto-generated constructor stub
  }

  /**
   * Instantiates a new technologies count statistics.
   *
   * @param type  the type
   * @param count the count
   */
  public TechnologiesCountStatistics(OdmsCatalogueType type, int count) {
    super();
    this.type = type;
    this.count = count;
  }

  /**
   * Gets the type.
   *
   * @return the type
   */
  public OdmsCatalogueType getType() {
    return type;
  }

  /**
   * Sets the type.
   *
   * @param type the new type
   */
  public void setType(OdmsCatalogueType type) {
    this.type = type;
  }

  /**
   * Gets the count.
   *
   * @return the count
   */
  public int getCount() {
    return count;
  }

  /**
   * Sets the count.
   *
   * @param count the new count
   */
  public void setCount(int count) {
    this.count = count;
  }

}
