package it.eng.idra.statistics;

// TODO: Auto-generated Javadoc
/**
 * The Class DatasetUpdatedStatistics.
 */
public class DatasetUpdatedStatistics {

  /** The name. */
  private String name;

  /** The added. */
  private int added;

  /** The updated. */
  private int updated;

  /**
   * Instantiates a new dataset updated statistics.
   */
  public DatasetUpdatedStatistics() {
    // TODO Auto-generated constructor stub
  }

  /**
   * Instantiates a new dataset updated statistics.
   *
   * @param name    the name
   * @param added   the added
   * @param updated the updated
   */
  public DatasetUpdatedStatistics(String name, int added, int updated) {
    super();
    this.name = name;
    this.added = added;
    this.updated = updated;
  }

  /**
   * Gets the name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name.
   *
   * @param name the new name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the added.
   *
   * @return the added
   */
  public int getAdded() {
    return added;
  }

  /**
   * Sets the added.
   *
   * @param added the new added
   */
  public void setAdded(int added) {
    this.added = added;
  }

  /**
   * Gets the updated.
   *
   * @return the updated
   */
  public int getUpdated() {
    return updated;
  }

  /**
   * Sets the updated.
   *
   * @param updated the new updated
   */
  public void setUpdated(int updated) {
    this.updated = updated;
  }

}
