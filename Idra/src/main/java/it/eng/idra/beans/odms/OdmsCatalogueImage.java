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

package it.eng.idra.beans.odms;

import com.google.gson.annotations.Expose;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

// TODO: Auto-generated Javadoc
/**
 * The Class OdmsCatalogueImage.
 */
@Entity
@Table(name = "odms_image")
public class OdmsCatalogueImage {

  /** The image id. */
  @Id
  @Column(name = "image_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Expose
  private int imageId;

  /** The image data. */
  @Column(columnDefinition = "LONGTEXT")
  @Expose
  private String imageData;

  /**
   * Instantiates a new odms catalogue image.
   */
  public OdmsCatalogueImage() {
  }

  /**
   * Instantiates a new odms catalogue image.
   *
   * @param imageData the image data
   */
  public OdmsCatalogueImage(String imageData) {
    this.imageData = imageData;
  }

  /**
   * Gets the image id.
   *
   * @return the image id
   */
  public int getImageId() {
    return imageId;
  }

  /**
   * Sets the image id.
   *
   * @param imageId the new image id
   */
  public void setImageId(int imageId) {
    this.imageId = imageId;
  }

  /**
   * Gets the image data.
   *
   * @return the image data
   */
  public String getImageData() {
    return imageData;
  }

  /**
   * Sets the image data.
   *
   * @param imageData the new image data
   */
  public void setImageData(String imageData) {
    this.imageData = imageData;
  }

}
