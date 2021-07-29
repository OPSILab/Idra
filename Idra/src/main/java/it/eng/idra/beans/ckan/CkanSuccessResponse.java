package it.eng.idra.beans.ckan;

public class CkanSuccessResponse<T> extends CkanAbstactResponse {

  private T result;

  /**
   * Instantiates a new ckan success response.
   */
  public CkanSuccessResponse() {
    // TODO Auto-generated constructor stub
    super();
    this.setSuccess(true);
  }

  /**
   * Instantiates a new ckan success response.
   *
   * @param help the help
   * @param result the result
   */
  public CkanSuccessResponse(String help, T result) {
    super(help, true);
    // TODO Auto-generated constructor stub
    this.result = result;
  }

  public T getResult() {
    return result;
  }

  public void setResult(T result) {
    this.result = result;
  }

}
