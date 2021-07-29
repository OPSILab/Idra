package it.eng.idra.beans.opendatasoft;

import com.google.gson.annotations.JsonAdapter;

@JsonAdapter(InnerDatasetFieldDeserializer.class)
public class InnerDatasetField {

  private String label;
  private String type;
  private InnerDatasetFieldAnnotations annotations;
  private String name;
  private String description;

  /**
   * Instantiates a new inner dataset field.
   *
   * @param label the label
   * @param type the type
   * @param annotations the annotations
   * @param name the name
   * @param description the description
   */
  public InnerDatasetField(String label, String type,
      InnerDatasetFieldAnnotations annotations, String name,
      String description) {
    super();
    this.label = label;
    this.type = type;
    this.annotations = annotations;
    this.name = name;
    this.description = description;
  }

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
