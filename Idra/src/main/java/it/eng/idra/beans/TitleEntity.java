package it.eng.idra.beans;


/**
* The Class TitleEntity.
*/
public class TitleEntity {
  private String type;
  private String value;
  
  public TitleEntity() {
    
  }
  
  /**
  * Instantiates a new TitleEntity.
  */
  public TitleEntity(String type, String value) {
    this.type = type;
    this.value = value;
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
   * Gets the value.
   *
   * @return the value
   */
  public String getValue() {
    return value;
  }
  
  /**
   * Sets the value.
   *
   * @param value the new value
   */
  public void setValue(String value) {
    this.value = value;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "\nTitle [type=" + type + ", value=" + value + "]";
  }
}
