package it.eng.idra.beans.spod;

public class SPODRelation {
	private String id;
	private String url;
	private String label;
	private boolean isTab;
	private boolean isDataSource;
	
	public SPODRelation() {
		// TODO Auto-generated constructor stub
	}

	public SPODRelation(String id, String url, String label, boolean isTab, boolean isDataSource) {
		super();
		this.id = id;
		this.url = url;
		this.label = label;
		this.isTab = isTab;
		this.isDataSource = isDataSource;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isTab() {
		return isTab;
	}

	public void setTab(boolean isTab) {
		this.isTab = isTab;
	}

	public boolean isDataSource() {
		return isDataSource;
	}

	public void setDataSource(boolean isDataSource) {
		this.isDataSource = isDataSource;
	}
	
	
	
	

}
