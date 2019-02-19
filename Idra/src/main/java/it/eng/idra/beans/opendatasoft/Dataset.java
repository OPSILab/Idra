package it.eng.idra.beans.opendatasoft;

import java.util.List;

public class Dataset {

	private List<Link> links;
	private InnerDataset dataset;

	public List<Link> getLinks() {
		return links;
	}
	public void setLinks(List<Link> links) {
		this.links = links;
	}
	public InnerDataset getDataset() {
		return dataset;
	}
	public void setDataset(InnerDataset dataset) {
		this.dataset = dataset;
	}
	
}
