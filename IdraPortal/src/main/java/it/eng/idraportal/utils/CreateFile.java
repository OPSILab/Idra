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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * Servlet implementation class CreateFile
 */
@WebServlet("/CreateFile")
public class CreateFile extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateFile() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		UUID id = UUID.randomUUID();
		
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		
        String sb ="";
        
        try 
        {
          BufferedReader reader = request.getReader();
          String line = null;
          while ((line = reader.readLine()) != null)
          {
            sb+=line;
          }
        } catch (Exception e) { e.printStackTrace(); }
        
        //System.out.println(sb.toString());
        
		JSONObject json = new JSONObject(sb);

		//System.out.println(json.toString());
		
		String content = json.getString("result");
		String format = json.getString("format").toLowerCase();
		
		String path = getServletContext().getRealPath("/temp/");
		
		File dir = new File(path);
		if(!dir.exists()){
			dir.mkdir();
		}
		
//		System.out.println(path);
		if(path.lastIndexOf(File.separator) != path.length())
			path +=File.separator;
		
		FileOutputStream file = new FileOutputStream(path+id+"."+format);
		OutputStreamWriter oos = new OutputStreamWriter(file);
		
		oos.write(content);
		
		oos.close(); 
		file.close();
		
		out.write(id+"."+format);
		
	}

}
