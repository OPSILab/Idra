package it.eng.idra.statistics;

import it.eng.idra.beans.odms.ODMSCatalogueType;

public class TechnologiesCountStatistics {

	private ODMSCatalogueType type;
	private int count;
	
	public TechnologiesCountStatistics() {
		// TODO Auto-generated constructor stub
	}

	public TechnologiesCountStatistics(ODMSCatalogueType type, int count) {
		super();
		this.type = type;
		this.count = count;
	}

	public ODMSCatalogueType getType() {
		return type;
	}

	public void setType(ODMSCatalogueType type) {
		this.type = type;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	

}
