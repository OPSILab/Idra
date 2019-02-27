package it.eng.idra.statistics;

public class FormatStatistics {
	
	private String format;
	private int cnt;
	
	public FormatStatistics() {
		// TODO Auto-generated constructor stub
	}
	
	public FormatStatistics(String format, int cnt) {
		super();
		this.format = format;
		this.cnt = cnt;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public int getCnt() {
		return cnt;
	}

	public void setCnt(int cnt) {
		this.cnt = cnt;
	}
	
	

}
