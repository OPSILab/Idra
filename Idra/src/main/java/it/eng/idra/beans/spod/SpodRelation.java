package it.eng.idra.beans.spod;

// TODO: Auto-generated Javadoc
/**
 * The Class SpodRelation.
 */
public class SpodRelation {

  /** The id. */
  private String id;

  /** The url. */
  private String url;

  /** The label. */
  private String label;

  /** The is tab. */
  private boolean isTab;

  /** The is data source. */
  private boolean isDataSource;

  /**
   * Instantiates a new spod relation.
   */
  public SpodRelation() {
    // TODO Auto-generated constructor stub
  }

  /**
   * Instantiates a new spod relation.
   *
   * @param id           the id
   * @param url          the url
   * @param label        the label
   * @param isTab        the is tab
   * @param isDataSource the is data source
   */
  public SpodRelation(String id, String url, String label, boolean isTab, boolean isDataSource) {
    super();
    this.id = id;
    this.url = url;
    this.label = label;
    this.isTab = isTab;
    this.isDataSource = isDataSource;
  }

  /**
   * Gets the id.
   *
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the id.
   *
   * @param id the new id
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the url.
   *
   * @return the url
   */
  public String getUrl() {
    return url;
  }

  /**
   * Sets the url.
   *
   * @param url the new url
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * Gets the label.
   *
   * @return the label
   */
  public String getLabel() {
    return label;
  }

  /**
   * Sets the label.
   *
   * @param label the new label
   */
  public void setLabel(String label) {
    this.label = label;
  }

  /**
   * Checks if is tab.
   *
   * @return true, if is tab
   */
  public boolean isTab() {
    return isTab;
  }

  /**
   * Sets the tab.
   *
   * @param isTab the new tab
   */
  public void setTab(boolean isTab) {
    this.isTab = isTab;
  }

  /**
   * Checks if is data source.
   *
   * @return true, if is data source
   */
  public boolean isDataSource() {
    return isDataSource;
  }

  /**
   * Sets the data source.
   *
   * @param isDataSource the new data source
   */
  public void setDataSource(boolean isDataSource) {
    this.isDataSource = isDataSource;
  }

}
