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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * Servlet implementation class LoadConfigs
 */
@WebServlet("/LoadConfigs")
public class LoadConfigs extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public LoadConfigs() {

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		
		String propFileName = "configuration.properties";

		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

		Properties prop = new Properties();
		JSONObject json;
		try {
			if (inputStream != null) {
				prop.load(inputStream);
				inputStream.close();
			} 
			else {
				throw new FileNotFoundException("Property file '" + propFileName + "' not found in the classpath");
			}
		}catch (IOException e) {
		
			e.printStackTrace();
			json=new JSONObject();
			json.put("message", "Exception");
			out.write(json.toString());
		}
		
		// Override file properties if the ones (if any) set through Environment Variable
		Stream<Entry<Object, Object>> stream = prop.entrySet().stream();
	    Map<String, String> mapOfProperties = stream.collect(Collectors.toMap(
	            e -> String.valueOf(e.getKey()),
	            e -> checkIfEnvProperties(e)));
		
		json=new JSONObject(mapOfProperties);
		
		out.write(json.toString());
		
	}

	private static String checkIfEnvProperties(Entry<Object, Object> entry) {

		String originalValue = entry.getValue().toString();

		return Optional.ofNullable(System.getenv(entry.getKey().toString())).orElse(originalValue);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}

}
