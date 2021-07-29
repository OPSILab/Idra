package it.eng.idra.beans.ckan;

public class CkanError {

  private String message;
  private String type;

  public CkanError() {
    // TODO Auto-generated constructor stub
  }

  /**
   * Instantiates a new ckan error.
   *
   * @param message the message
   * @param type the type
   */
  public CkanError(String message, String type) {
    super();
    this.message = message;
    this.type = type;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

}
