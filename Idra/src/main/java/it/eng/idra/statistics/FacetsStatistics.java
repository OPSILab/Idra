package it.eng.idra.statistics;

import java.util.List;
import java.util.stream.Collectors;

import it.eng.idra.beans.search.SearchFacet;

public class FacetsStatistics {

	private List<FormatStatistics> formatsStatistics;
	private List<LicenseStatistics> licensesStatistics;
	private List<ThemeStatistics> themesStatistics;
	
	public FacetsStatistics() {
		// TODO Auto-generated constructor stub
	}

//	public FacetsStatistics(List<FormatStatistics> formats, List<LicenseStatistics> licenses) {
//		super();
//		this.formats = formats;
//		this.licenses = licenses;
//	}

	public FacetsStatistics(List<SearchFacet> formats, List<SearchFacet> licenses, List<SearchFacet> themes) {
		super();
		this.setFormats(getFormatStatFromFacets(formats));
		this.setLicenses(getLicenseStatFromFacets(licenses));
		this.setThemesStatistics(getThemeStatFromFacets(themes));
	}
	
	public List<FormatStatistics> getFormats() {
		return formatsStatistics;
	}

	public void setFormats(List<FormatStatistics> formats) {
		this.formatsStatistics = formats;
	}

	public List<LicenseStatistics> getLicenses() {
		return licensesStatistics;
	}

	public void setLicenses(List<LicenseStatistics> licenses) {
		this.licensesStatistics = licenses;
	}
		
	public List<ThemeStatistics> getThemesStatistics() {
		return themesStatistics;
	}

	public void setThemesStatistics(List<ThemeStatistics> themesStatistics) {
		this.themesStatistics = themesStatistics;
	}

	private List<FormatStatistics> getFormatStatFromFacets(List<SearchFacet> values){
		return values.stream().map(x->{
			return new FormatStatistics(x.getKeyword(),
					Integer.parseInt(x.getFacet().substring(x.getFacet().lastIndexOf("(")+1, x.getFacet().lastIndexOf(")"))));
		}).collect(Collectors.toList());
	}
	
	private List<LicenseStatistics> getLicenseStatFromFacets(List<SearchFacet> values){
		return values.stream().map(x->{
			return new LicenseStatistics(x.getKeyword(),
					Integer.parseInt(x.getFacet().substring(x.getFacet().lastIndexOf("(")+1, x.getFacet().lastIndexOf(")"))), "" );
		}).collect(Collectors.toList());
	}

	private List<ThemeStatistics> getThemeStatFromFacets(List<SearchFacet> values){
		return values.stream().map(x->{
			return new ThemeStatistics(x.getKeyword(),
					Integer.parseInt(x.getFacet().substring(x.getFacet().lastIndexOf("(")+1, x.getFacet().lastIndexOf(")"))));
		}).collect(Collectors.toList());
	}
}
