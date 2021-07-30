package it.eng.idra.beans.ckan;

// TODO: Auto-generated Javadoc
/**
 * The Class CkanAbstactResponse.
 */
public abstract class CkanAbstactResponse {

  /** The help. */
  private String help;

  /** The success. */
  private boolean success;

  /**
   * Instantiates a new ckan abstact response.
   */
  public CkanAbstactResponse() {
    // TODO Auto-generated constructor stub
  }

  /**
   * Instantiates a new ckan abstact response.
   *
   * @param help    the help
   * @param success the success
   */
  public CkanAbstactResponse(String help, boolean success) {
    super();
    this.help = help;
    this.success = success;
  }

  /**
   * Gets the help.
   *
   * @return the help
   */
  public String getHelp() {
    return help;
  }

  /**
   * Sets the help.
   *
   * @param help the new help
   */
  public void setHelp(String help) {
    this.help = help;
  }

  /**
   * Checks if is success.
   *
   * @return true, if is success
   */
  public boolean isSuccess() {
    return success;
  }

  /**
   * Sets the success.
   *
   * @param success the new success
   */
  public void setSuccess(boolean success) {
    this.success = success;
  }

}
