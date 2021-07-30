package it.eng.idra.beans.opendatasoft;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class Dataset.
 */
public class Dataset {

  /** The links. */
  private List<Link> links;

  /** The dataset. */
  private InnerDataset dataset;

  /**
   * Gets the links.
   *
   * @return the links
   */
  public List<Link> getLinks() {
    return links;
  }

  /**
   * Sets the links.
   *
   * @param links the new links
   */
  public void setLinks(List<Link> links) {
    this.links = links;
  }

  /**
   * Gets the dataset.
   *
   * @return the dataset
   */
  public InnerDataset getDataset() {
    return dataset;
  }

  /**
   * Sets the dataset.
   *
   * @param dataset the new dataset
   */
  public void setDataset(InnerDataset dataset) {
    this.dataset = dataset;
  }

}
