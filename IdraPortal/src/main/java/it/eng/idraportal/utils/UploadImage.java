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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.oreilly.servlet.MultipartRequest;

/**
 * Servlet implementation class UploadImage
 */
@WebServlet("/UploadImage")
public class UploadImage extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadImage() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {   	
		response.setContentType("text/html");
	    PrintWriter out = response.getWriter();
	    				
		String path = getServletContext().getRealPath("/nodesImages/");
		
		System.out.println(request.getParameterMap().keySet().toArray().toString());
		
		String name2 = request.getParameter("name");
		String image = request.getParameter("image");
//		if(path.lastIndexOf(File.separator) != path.length())
//			path +=File.separator;
	
		System.out.println(name2);
		System.out.println(image);
		System.out.println(path);
		
	    MultipartRequest m = new MultipartRequest(request, path);
	
	    Enumeration files = m.getFileNames();
	    System.out.println(files.hasMoreElements());
	    String name1="";
        while (files.hasMoreElements()) 
         { 
              String name = (String)files.nextElement(); 
              String filename = m.getFilesystemName(name); 
              String type = m.getContentType(name);
              name1=filename;
           } 
	    System.out.println(name1);
	    out.print(name1);
	    
	}

}
