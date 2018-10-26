package it.eng.idra.beans.spod;

import java.util.List;

import org.ckan.Dataset;

public class SPODDataset extends Dataset{

	private List<SPODRelation> relations;
	
	public SPODDataset() {
		super();
	}

	public SPODDataset(List<SPODRelation> relations) {
		super();
		this.relations = relations;
	}

	public List<SPODRelation> getRelations() {
		return relations;
	}

	public void setRelations(List<SPODRelation> relations) {
		this.relations = relations;
	}	

}
