package it.eng.idra.statistics;

public class DatasetCountStatistics {

	private String name;
	private int datasetCount;
	
	public DatasetCountStatistics() {
		// TODO Auto-generated constructor stub
	}
	
	public DatasetCountStatistics(String name, int cnt) {
		// TODO Auto-generated constructor stub
		this.name=name;
		this.datasetCount=cnt;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDatasetCount() {
		return datasetCount;
	}

	public void setDatasetCount(int datasetCount) {
		this.datasetCount = datasetCount;
	}

	@Override
	public String toString() {
		return "DatasetCountStatistics [name=" + name + ", datasetCount=" + datasetCount + "]";
	}

}
