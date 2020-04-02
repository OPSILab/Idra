/*******************************************************************************
 * Idra - Open Data Federation Platform
 *  Copyright (C) 2020 Engineering Ingegneria Informatica S.p.A.
 *  
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *  
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package it.eng.idra.utils;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import it.eng.idra.beans.odms.ODMSCatalogueImage;

public class ImageSerializer implements JsonSerializer<ODMSCatalogueImage>{

	@Override
	public JsonElement serialize(ODMSCatalogueImage image, Type arg1, JsonSerializationContext arg2) {
//		System.out.println("Image serializer");
//		if(image!=null){
		try{
//			System.out.println("Image not null");
			JsonObject jsonObject = new JsonObject();
		    jsonObject.addProperty("imageId", image.getImageId());
		    jsonObject.addProperty("imageData", image.getImageData());
		    return jsonObject;
		}catch(Exception e){
//			System.out.println("Image null "+e.getMessage());
			return null;
		}
	}

}
