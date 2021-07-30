package it.eng.idra.statistics;

// TODO: Auto-generated Javadoc
/**
 * The Class FormatStatistics.
 */
public class FormatStatistics {

  /** The format. */
  private String format;

  /** The cnt. */
  private int cnt;

  /**
   * Instantiates a new format statistics.
   */
  public FormatStatistics() {
    // TODO Auto-generated constructor stub
  }

  /**
   * Instantiates a new format statistics.
   *
   * @param format the format
   * @param cnt    the cnt
   */
  public FormatStatistics(String format, int cnt) {
    super();
    this.format = format;
    this.cnt = cnt;
  }

  /**
   * Gets the format.
   *
   * @return the format
   */
  public String getFormat() {
    return format;
  }

  /**
   * Sets the format.
   *
   * @param format the new format
   */
  public void setFormat(String format) {
    this.format = format;
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

}
