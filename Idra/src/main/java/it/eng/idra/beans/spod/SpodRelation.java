package it.eng.idra.beans.spod;

public class SpodRelation {
  private String id;
  private String url;
  private String label;
  private boolean isTab;
  private boolean isDataSource;

  public SpodRelation() {
    // TODO Auto-generated constructor stub
  }

  /**
   * Instantiates a new spod relation.
   *
   * @param id the id
   * @param url the url
   * @param label the label
   * @param isTab the is tab
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

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public boolean isTab() {
    return isTab;
  }

  public void setTab(boolean isTab) {
    this.isTab = isTab;
  }

  public boolean isDataSource() {
    return isDataSource;
  }

  public void setDataSource(boolean isDataSource) {
    this.isDataSource = isDataSource;
  }

}
