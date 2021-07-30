package it.eng.idra.beans.ckan;

// TODO: Auto-generated Javadoc
/**
 * The Class CkanError.
 */
public class CkanError {

  /** The message. */
  private String message;

  /** The type. */
  private String type;

  /**
   * Instantiates a new ckan error.
   */
  public CkanError() {
    // TODO Auto-generated constructor stub
  }

  /**
   * Instantiates a new ckan error.
   *
   * @param message the message
   * @param type    the type
   */
  public CkanError(String message, String type) {
    super();
    this.message = message;
    this.type = type;
  }

  /**
   * Gets the message.
   *
   * @return the message
   */
  public String getMessage() {
    return message;
  }

  /**
   * Sets the message.
   *
   * @param message the new message
   */
  public void setMessage(String message) {
    this.message = message;
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

}
