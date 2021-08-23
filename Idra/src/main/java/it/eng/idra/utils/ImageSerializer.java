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

package it.eng.idra.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import it.eng.idra.beans.odms.OdmsCatalogueImage;
import java.lang.reflect.Type;

// TODO: Auto-generated Javadoc
/**
 * The Class ImageSerializer.
 */
public class ImageSerializer implements JsonSerializer<OdmsCatalogueImage> {

  /*
   * (non-Javadoc)
   * 
   * @see com.google.gson.JsonSerializer#serialize(java.lang.Object,
   * java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
   */
  @Override
  public JsonElement serialize(OdmsCatalogueImage image, Type arg1, JsonSerializationContext arg2) {

    try {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("imageId", image.getImageId());
      jsonObject.addProperty("imageData", image.getImageData());
      return jsonObject;
    } catch (Exception e) {
      return null;
    }
  }

}
