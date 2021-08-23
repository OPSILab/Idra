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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.AttributeConverter;
import org.apache.commons.lang3.StringUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class StringListToStringConverter.
 */
public class StringListToStringConverter implements AttributeConverter<List<String>, String> {

  /*
   * (non-Javadoc)
   * 
   * @see javax.persistence.AttributeConverter#convertToDatabaseColumn(java.lang.
   * Object)
   */
  @Override
  public String convertToDatabaseColumn(List<String> attribute) {
    return attribute == null ? null : String.join(";;", attribute);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.persistence.AttributeConverter#convertToEntityAttribute(java.lang.
   * Object)
   */
  @Override
  public List<String> convertToEntityAttribute(String dbData) {
    if (StringUtils.isBlank(dbData)) {
      return new ArrayList<>();
    }

    return new ArrayList<>(Arrays.asList(dbData.split(",")));
  }

}
