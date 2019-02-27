package it.eng.idra.beans.opendatasoft;

public class InnerDatasetField {

	private String label;
	private String type;
	private InnerDatasetFieldAnnotations annotations;
	private String name;
	private String description;
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public InnerDatasetFieldAnnotations getAnnotations() {
		return annotations;
	}
	public void setAnnotations(InnerDatasetFieldAnnotations annotations) {
		this.annotations = annotations;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
