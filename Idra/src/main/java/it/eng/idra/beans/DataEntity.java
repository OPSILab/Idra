package it.eng.idra.beans;

/**
* The Class DataEntity.
*/
public class DataEntity {
  private String id;
  private String type;
  TitleEntity title;
  
  public DataEntity() {
  }
  
  /**
  * Instantiates a new DataEntity.
  */
  public DataEntity(String id, String type, TitleEntity title) {
    this.id = id;
    this.type = type;
    this.title = title;
  }

  /**
   * Gets the id.
   *
   * @return the id
   */
  public String getId() {
    return id;
  }
  
  /**
   * Sets the id.
   *
   * @param id the new id
   */
  public void setId(String id) {
    this.id = id;
  }
  
  /**
   * Gets the type.
   *
   * @return the type
   */
  public String getType() {
    return type;
  }
  
  /**
   * Sets the type.
   *
   * @param type the new type
   */
  public void setType(String type) {
    this.type = type;
  }
  
  /**
   * Gets the title.
   *
   * @return the title
   */
  public TitleEntity getTitle() {
    return title;
  }
  
  /**
   * Sets the title.
   *
   * @param title the new title
   */
  public void setData(TitleEntity title) {
    this.title = title;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "\nDataEntity [id=" + id + ", type=" + type + ", title=" + title.toString() + "]";
  }
}
