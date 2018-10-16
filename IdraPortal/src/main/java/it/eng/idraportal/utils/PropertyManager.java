/*******************************************************************************
 * Idra - Open Data Federation Platform
 *  Copyright (C) 2018 Engineering Ingegneria Informatica S.p.A.
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
package it.eng.idraportal.utils;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

import it.eng.idraportal.idm.configuration.IDMProperty;
import it.eng.idraportal.utils.restclient.configuration.RestProperty;

public class PropertyManager {

	private static Properties props = null;

	static {
		props = new Properties();
		try {
			props.load(PropertyManager.class.getClassLoader().getResourceAsStream("configuration.properties"));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getProperty(String propName) {
		Optional<String> prop = Optional.ofNullable(System.getenv(propName));
		return prop.orElse(props.getProperty(propName));
	}

	public static String getProperty(IDMProperty propName) {
		Optional<String> prop = Optional.ofNullable(System.getenv(propName.toString()));
		return prop.orElse(props.getProperty(propName.toString()));
	}

	public static String getProperty(RestProperty propName) {
		Optional<String> prop = Optional.ofNullable(System.getenv(propName.toString()));
		return prop.orElse(props.getProperty(propName.toString()));
	}

	public static Properties getProperties() {
		return props;
	}

}
