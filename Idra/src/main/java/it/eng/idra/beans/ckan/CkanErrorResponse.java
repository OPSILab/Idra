package it.eng.idra.beans.ckan;

public class CkanErrorResponse extends CkanAbstactResponse {

  private CkanError error;

  /**
   * Instantiates a new ckan error response.
   */
  public CkanErrorResponse() {
    // TODO Auto-generated constructor stub
    super();
    this.setSuccess(false);
  }

  /**
   * Instantiates a new ckan error response.
   *
   * @param error the error
   */
  public CkanErrorResponse(CkanError error) {
    super();
    this.setSuccess(false);
    this.error = error;
  }

  /**
   * Instantiates a new ckan error response.
   *
   * @param help the help
   * @param er the er
   */
  public CkanErrorResponse(String help, CkanError er) {
    // TODO Auto-generated constructor stub
    super(help, false);
    this.error = er;
  }

  public CkanErrorResponse(String message, String type) {
    super();
    this.error = new CkanError(message, type);
  }

  public CkanErrorResponse(String help, String message, String type) {
    super(help, false);
    this.error = new CkanError(message, type);
  }

  public CkanError getError() {
    return error;
  }

  public void setError(CkanError error) {
    this.error = error;
  }

}
