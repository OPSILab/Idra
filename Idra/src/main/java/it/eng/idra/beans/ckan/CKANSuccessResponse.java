package it.eng.idra.beans.ckan;

public class CKANSuccessResponse<T> extends CKANAbstactResponse{

	private T result;
	
	public CKANSuccessResponse() {
		// TODO Auto-generated constructor stub
		super();
		this.setSuccess(true);
	}
	
	public CKANSuccessResponse(String help, T result) {
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
