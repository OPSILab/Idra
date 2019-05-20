package it.eng.idra.beans.ckan;

public class CKANErrorResponse extends CKANAbstactResponse{

	private CKANError error;
	
	public CKANErrorResponse() {
		// TODO Auto-generated constructor stub
		super();
		this.setSuccess(false);
	}
	
	public CKANErrorResponse(CKANError error) {
		super();
		this.setSuccess(false);
		this.error = error;
	}
	
	public CKANErrorResponse(String help, CKANError er) {
		// TODO Auto-generated constructor stub
		super(help, false);
		this.error = er;
	}

	public CKANErrorResponse(String message, String type) {
		super();
		this.error = new CKANError(message, type);
	}
	
	public CKANErrorResponse(String help,String message, String type) {
		super(help,false);
		this.error = new CKANError(message, type);
	}

	public CKANError getError() {
		return error;
	}

	public void setError(CKANError error) {
		this.error = error;
	}
	
}
