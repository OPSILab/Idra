package it.eng.idra.statistics;

public class DatasetUpdatedStatistics {

	private String name;
	private int added;
	private int updated;
	
	public DatasetUpdatedStatistics() {
		// TODO Auto-generated constructor stub
	}
	
	public DatasetUpdatedStatistics(String name, int added, int updated) {
		super();
		this.name = name;
		this.added = added;
		this.updated = updated;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAdded() {
		return added;
	}

	public void setAdded(int added) {
		this.added = added;
	}

	public int getUpdated() {
		return updated;
	}

	public void setUpdated(int updated) {
		this.updated = updated;
	}
	
	

}
