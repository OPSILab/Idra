package it.eng.idra.beans.opendatasoft;

import java.util.List;

public class DatasetDTO {

	private Integer total_count;
	private List<Link> links;
	private List<Dataset> datasets;
	
	public Integer getTotal_count() {
		return total_count;
	}
	public void setTotal_count(Integer total_count) {
		this.total_count = total_count;
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
