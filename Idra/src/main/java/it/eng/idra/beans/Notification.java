package it.eng.idra.beans;


/**
* The Class Notification.
*/
public class Notification {
  private String id;
  private String type;
  private String subscriptionId;
  private String notifiedAt;
  DataEntity[] data;
  
  public Notification() { 
  }
  
  /**
  * Instantiates a new Notification.
  */
  public Notification(String id, String type, String subscriptionId, 
      String notifiedAt, DataEntity[] data) {
    this.id = id;
    this.type = type;
    this.subscriptionId = subscriptionId;
    this.notifiedAt = notifiedAt;
    this.data = data;
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
   * Gets the Subscription Id.
   *
   * @return the SubscriptionId
   */
  public String getSubscriptionId() {
    return subscriptionId;
  }
  
  /**
   * Sets the subscriptionId.
   *
   * @param subscriptionId the new subscriptionId
   */
  public void setSubscriptionId(String subscriptionId) {
    this.subscriptionId = subscriptionId;
  }
  
  /**
   * Gets the NotifiedAt.
   *
   * @return the NotifiedAt
   */
  public String getNotifiedAt() {
    return notifiedAt;
  }
  
  /**
   * Sets the notifiedAt.
   *
   * @param notifiedAt the new notifiedAt
   */
  public void setNotifiedAt(String notifiedAt) {
    this.notifiedAt = notifiedAt;
  }
  
  /**
   * Gets the dataEntity.
   *
   * @return the dataEntity
   */
  public DataEntity[] getData() {
    return data;
  }
  
  /**
   * Sets the data.
   *
   * @param data the new data
   */
  public void setData(DataEntity[] data) {
    this.data = data;
  }
}
