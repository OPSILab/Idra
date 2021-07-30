package it.eng.idra.beans.spod;

import java.util.List;
import org.ckan.Dataset;

public class SpodDataset extends Dataset {

  private List<SpodRelation> relations;

  public SpodDataset() {
    super();
  }

  public SpodDataset(List<SpodRelation> relations) {
    super();
    this.relations = relations;
  }

  public List<SpodRelation> getRelations() {
    return relations;
  }

  public void setRelations(List<SpodRelation> relations) {
    this.relations = relations;
  }

}
