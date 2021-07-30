package it.eng.idra.beans.spod;

import java.util.List;
import org.ckan.Dataset;

// TODO: Auto-generated Javadoc
/**
 * The Class SpodDataset.
 */
public class SpodDataset extends Dataset {

  /** The relations. */
  private List<SpodRelation> relations;

  /**
   * Instantiates a new spod dataset.
   */
  public SpodDataset() {
    super();
  }

  /**
   * Instantiates a new spod dataset.
   *
   * @param relations the relations
   */
  public SpodDataset(List<SpodRelation> relations) {
    super();
    this.relations = relations;
  }

  /**
   * Gets the relations.
   *
   * @return the relations
   */
  public List<SpodRelation> getRelations() {
    return relations;
  }

  /**
   * Sets the relations.
   *
   * @param relations the new relations
   */
  public void setRelations(List<SpodRelation> relations) {
    this.relations = relations;
  }

}
