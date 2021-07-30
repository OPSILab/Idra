package it.eng.idra.beans.opendatasoft;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DatasetDto {

  @SerializedName(value = "total_count")
  private Integer totalCount;
  private List<Link> links;
  private List<Dataset> datasets;

  public Integer getTotalCount() {
    return totalCount;
  }

  public void setTotalCount(Integer totalCount) {
    this.totalCount = totalCount;
  }

  public List<Link> getLinks() {
    return links;
  }

  public void setLinks(List<Link> links) {
    this.links = links;
  }

  public List<Dataset> getDatasets() {
    return datasets;
  }

  public void setDatasets(List<Dataset> datasets) {
    this.datasets = datasets;
  }

}
