package it.eng.idra.api.ckan;

import java.util.List;

import org.ckan.Dataset;

public class CKANSearchResult {

	private Long count;
	private List<Dataset> results;

	public CKANSearchResult() {}
	
	public CKANSearchResult(Long count, List<Dataset> result) {
		super();
		this.count = count;
		this.results = result;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public List<Dataset> getResults() {
		return results;
	}

	public void setResults(List<Dataset> result) {
		this.results = result;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class ResourcesResult {\n");

		sb.append("    count: ").append(toIndentedString(count)).append("\n");
		sb.append("    resources: ").append(toIndentedString(results.size())).append("\n");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Convert the given object to string with each line indented by 4 spaces
	 * (except the first line).
	 */
	private String toIndentedString(java.lang.Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");

	}
}
