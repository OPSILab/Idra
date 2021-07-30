package it.eng.idra.statistics;

// TODO: Auto-generated Javadoc
/**
 * The Class DatasetCountStatistics.
 */
public class DatasetCountStatistics {

  /** The name. */
  private String name;

  /** The dataset count. */
  private int datasetCount;

  /**
   * Instantiates a new dataset count statistics.
   */
  public DatasetCountStatistics() {
    // TODO Auto-generated constructor stub
  }

  /**
   * Instantiates a new dataset count statistics.
   *
   * @param name the name
   * @param cnt  the cnt
   */
  public DatasetCountStatistics(String name, int cnt) {
    // TODO Auto-generated constructor stub
    this.name = name;
    this.datasetCount = cnt;
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
   * Gets the dataset count.
   *
   * @return the dataset count
   */
  public int getDatasetCount() {
    return datasetCount;
  }

  /**
   * Sets the dataset count.
   *
   * @param datasetCount the new dataset count
   */
  public void setDatasetCount(int datasetCount) {
    this.datasetCount = datasetCount;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "DatasetCountStatistics [name=" + name + ", datasetCount=" + datasetCount + "]";
  }

}
