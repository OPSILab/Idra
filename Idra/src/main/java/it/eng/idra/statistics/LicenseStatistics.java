package it.eng.idra.statistics;

// TODO: Auto-generated Javadoc
/**
 * The Class LicenseStatistics.
 */
public class LicenseStatistics {

  /** The license. */
  private String license;

  /** The license url. */
  private String licenseUrl;

  /** The cnt. */
  private int cnt;

  /**
   * Instantiates a new license statistics.
   */
  public LicenseStatistics() {
    // TODO Auto-generated constructor stub
  }

  /**
   * Instantiates a new license statistics.
   *
   * @param format the format
   * @param cnt    the cnt
   * @param url    the url
   */
  public LicenseStatistics(String format, int cnt, String url) {
    super();
    this.license = format;
    this.cnt = cnt;
    this.licenseUrl = url;
  }

  /**
   * Gets the license.
   *
   * @return the license
   */
  public String getLicense() {
    return license;
  }

  /**
   * Sets the license.
   *
   * @param format the new license
   */
  public void setLicense(String format) {
    this.license = format;
  }

  /**
   * Gets the cnt.
   *
   * @return the cnt
   */
  public int getCnt() {
    return cnt;
  }

  /**
   * Sets the cnt.
   *
   * @param cnt the new cnt
   */
  public void setCnt(int cnt) {
    this.cnt = cnt;
  }

  /**
   * Gets the license url.
   *
   * @return the license url
   */
  public String getLicenseUrl() {
    return licenseUrl;
  }

  /**
   * Sets the license url.
   *
   * @param licenseUrl the new license url
   */
  public void setLicenseUrl(String licenseUrl) {
    this.licenseUrl = licenseUrl;
  }

}
