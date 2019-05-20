package it.eng.idra.beans.ckan;

public abstract class CKANAbstactResponse {

	private String help;
	private boolean success;
	
	public CKANAbstactResponse() {
		// TODO Auto-generated constructor stub
	}
	
	public CKANAbstactResponse(String help, boolean success) {
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
