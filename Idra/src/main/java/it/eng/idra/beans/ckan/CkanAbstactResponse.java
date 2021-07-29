package it.eng.idra.beans.ckan;

public abstract class CkanAbstactResponse {

  private String help;
  private boolean success;

  public CkanAbstactResponse() {
    // TODO Auto-generated constructor stub
  }

  /**
   * Instantiates a new ckan abstact response.
   *
   * @param help the help
   * @param success the success
   */
  public CkanAbstactResponse(String help, boolean success) {
    super();
    this.help = help;
    this.success = success;
  }

  public String getHelp() {
    return help;
  }

  public void setHelp(String help) {
    this.help = help;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

}
