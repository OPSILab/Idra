package it.eng.idra.statistics;

// TODO: Auto-generated Javadoc
/**
 * The Class ThemeStatistics.
 */
public class ThemeStatistics {

  /** The theme. */
  private String theme;

  /** The cnt. */
  private int cnt;

  /**
   * Instantiates a new theme statistics.
   */
  public ThemeStatistics() {
    // TODO Auto-generated constructor stub
  }

  /**
   * Instantiates a new theme statistics.
   *
   * @param theme the theme
   * @param cnt   the cnt
   */
  public ThemeStatistics(String theme, int cnt) {
    super();
    this.theme = theme;
    this.cnt = cnt;
  }

  /**
   * Gets the theme.
   *
   * @return the theme
   */
  public String getTheme() {
    return theme;
  }

  /**
   * Sets the theme.
   *
   * @param theme the new theme
   */
  public void setTheme(String theme) {
    this.theme = theme;
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
