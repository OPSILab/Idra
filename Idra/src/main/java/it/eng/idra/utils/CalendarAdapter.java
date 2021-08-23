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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

// TODO: Auto-generated Javadoc
/**
 * The Class CalendarAdapter.
 */
public class CalendarAdapter implements JsonSerializer<Calendar>, JsonDeserializer<Calendar> {

  /**
   * Instantiates a new calendar adapter.
   */
  public CalendarAdapter() {

  }

  /*
   * (non-Javadoc)
   * 
   * @see com.google.gson.JsonSerializer#serialize(java.lang.Object,
   * java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
   */
  @Override
  public JsonElement serialize(Calendar calendar, Type type, JsonSerializationContext context) {

    String str = null;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    if (calendar != null) {
      str = sdf.format(calendar.getTime());
    }

    return new JsonPrimitive(str);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.google.gson.JsonDeserializer#deserialize(com.google.gson.JsonElement,
   * java.lang.reflect.Type, com.google.gson.JsonDeserializationContext)
   */
  @Override
  public Calendar deserialize(JsonElement json, Type type, JsonDeserializationContext context)
      throws JsonParseException {

    String strDate = json.getAsString();
    GregorianCalendar calendar = null;
    try {
      calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
      sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
      calendar.setTimeInMillis(sdf.parse(strDate).getTime());
    } catch (ParseException e) {
      throw new JsonParseException("calendar parsing error");
    }
    return calendar;
  }
}
