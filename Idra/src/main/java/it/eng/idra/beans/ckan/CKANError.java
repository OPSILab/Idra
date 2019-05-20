package it.eng.idra.beans.ckan;

public class CKANError {

	private String message;
	private String type;
	
	public CKANError() {
		// TODO Auto-generated constructor stub
	}

	public CKANError(String message, String type) {
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
