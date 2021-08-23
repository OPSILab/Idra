/*******************************************************************************
 * Idra - Open Data Federation Platform
 * Copyright (C) 2021 Engineering Ingegneria Informatica S.p.A.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

package it.eng.idra.beans.opendatasoft;

import com.google.gson.annotations.JsonAdapter;

// TODO: Auto-generated Javadoc
/**
 * The Class InnerDatasetField.
 */
@JsonAdapter(InnerDatasetFieldDeserializer.class)
public class InnerDatasetField {

  /** The label. */
  private String label;

  /** The type. */
  private String type;

  /** The annotations. */
  private InnerDatasetFieldAnnotations annotations;

  /** The name. */
  private String name;

  /** The description. */
  private String description;

  /**
   * Instantiates a new inner dataset field.
   *
   * @param label       the label
   * @param type        the type
   * @param annotations the annotations
   * @param name        the name
   * @param description the description
   */
  public InnerDatasetField(String label, String type, InnerDatasetFieldAnnotations annotations,
      String name, String description) {
    super();
    this.label = label;
    this.type = type;
    this.annotations = annotations;
    this.name = name;
    this.description = description;
  }

  /**
   * Gets the label.
   *
   * @return the label
   */
  public String getLabel() {
    return label;
  }

  /**
   * Sets the label.
   *
   * @param label the new label
   */
  public void setLabel(String label) {
    this.label = label;
  }

  /**
   * Gets the type.
   *
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * Sets the type.
   *
   * @param type the new type
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * Gets the annotations.
   *
   * @return the annotations
   */
  public InnerDatasetFieldAnnotations getAnnotations() {
    return annotations;
  }

  /**
   * Sets the annotations.
   *
   * @param annotations the new annotations
   */
  public void setAnnotations(InnerDatasetFieldAnnotations annotations) {
    this.annotations = annotations;
  }

  /**
   * Gets the name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name.
   *
   * @param name the new name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the description.
   *
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description.
   *
   * @param description the new description
   */
  public void setDescription(String description) {
    this.description = description;
  }

}
