package it.eng.idra.beans.ckan;

// TODO: Auto-generated Javadoc
/**
 * The Class CkanSuccessResponse.
 *
 * @param <T> the generic type
 */
public class CkanSuccessResponse<T> extends CkanAbstactResponse {

  /** The result. */
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
   * @param help   the help
   * @param result the result
   */
  public CkanSuccessResponse(String help, T result) {
    super(help, true);
    // TODO Auto-generated constructor stub
    this.result = result;
  }

  /**
   * Gets the result.
   *
   * @return the result
   */
  public T getResult() {
    return result;
  }

  /**
   * Sets the result.
   *
   * @param result the new result
   */
  public void setResult(T result) {
    this.result = result;
  }

}
