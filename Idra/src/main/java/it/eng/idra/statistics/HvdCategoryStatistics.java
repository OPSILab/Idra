package it.eng.idra.statistics;

public class HvdCategoryStatistics {
  private String hvdCategory;
  private int cnt;

  public HvdCategoryStatistics() {}

  public HvdCategoryStatistics(String hvdCategory, int cnt) {
    this.hvdCategory = hvdCategory;
    this.cnt = cnt;
  }

  public String getHvdCategory() {
    return hvdCategory;
  }

  public void setHvdCategory(String hvdCategory) {
    this.hvdCategory = hvdCategory;
  }

  public int getCnt() {
    return cnt;
  }

  public void setCnt(int cnt) {
    this.cnt = cnt;
  }
}