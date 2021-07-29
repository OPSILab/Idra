package it.eng.idra.statistics;

public class ThemeStatistics {

  private String theme;
  private int cnt;

  public ThemeStatistics() {
    // TODO Auto-generated constructor stub
  }

  /**
   * Instantiates a new theme statistics.
   *
   * @param theme the theme
   * @param cnt the cnt
   */
  public ThemeStatistics(String theme, int cnt) {
    super();
    this.theme = theme;
    this.cnt = cnt;
  }

  public String getTheme() {
    return theme;
  }

  public void setTheme(String theme) {
    this.theme = theme;
  }

  public int getCnt() {
    return cnt;
  }

  public void setCnt(int cnt) {
    this.cnt = cnt;
  }

}
