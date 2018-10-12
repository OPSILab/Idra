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
package it.eng.idra.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;


import it.eng.idra.authentication.fiware.configuration.IDMProperty;
import it.eng.idra.beans.ODFProperty;
import it.eng.idra.utils.restclient.configuration.RestProperty;

public class PropertyManager {

	private static Properties props = null;

	static {
		props = new Properties();
		try {
			props.load(PropertyManager.class.getClassLoader().getResourceAsStream("configuration.properties"));

		} catch (IOException e) {
			try {
				props = new Properties();
				System.out.println("Prop MAnager: " + System.getenv("CONF_FOLDER"));
				props.load(Files.newBufferedReader(Paths.get(System.getenv("CONF_FOLDER")),Charset.forName("UTF-8")));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public static String getProperty(ODFProperty propName) {
		return props.getProperty(propName.toString());
	}

	public static String getProperty(IDMProperty propName) {
		return props.getProperty(propName.toString());
	}

	public static String getProperty(RestProperty propName) {
		Optional<String> prop = Optional.ofNullable(System.getenv(propName.toString()));
		return prop.orElse(props.getProperty(propName.toString()));
	}
}
