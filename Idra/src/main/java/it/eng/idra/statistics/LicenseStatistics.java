package it.eng.idra.statistics;

public class LicenseStatistics {

  private String license;
  private String licenseUrl;
  private int cnt;

  public LicenseStatistics() {
    // TODO Auto-generated constructor stub
  }

  /**
   * Instantiates a new license statistics.
   *
   * @param format the format
   * @param cnt the cnt
   * @param url the url
   */
  public LicenseStatistics(String format, int cnt, String url) {
    super();
    this.license = format;
    this.cnt = cnt;
    this.licenseUrl = url;
  }

  public String getLicense() {
    return license;
  }

  public void setLicense(String format) {
    this.license = format;
  }

  public int getCnt() {
    return cnt;
  }

  public void setCnt(int cnt) {
    this.cnt = cnt;
  }

  public String getLicenseUrl() {
    return licenseUrl;
  }

  public void setLicenseUrl(String licenseUrl) {
    this.licenseUrl = licenseUrl;
  }

}
