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
package it.eng.idraportal.servlet;

import it.eng.idraportal.utils.idm.fiware.FiwareIDMConnector;
import it.eng.idraportal.utils.idm.fiware.model.Token;
import it.eng.idraportal.utils.idm.fiware.model.UserInfo;
import it.eng.idraportal.utils.PropertyManager;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/PostLogin")
public class PostLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public PostLogin() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String code = request.getParameter("code");
		
		FiwareIDMConnector idm = new FiwareIDMConnector();
		try{
			Token t = idm.getToken(code, PropertyManager.getProperty("idm.client.id"), PropertyManager.getProperty("idm.client.secret"), PropertyManager.getProperty("idm.redirecturi"));
		
			UserInfo info = idm.getUserInfo(t.getAccess_token());
			
			try{
				if(Boolean.parseBoolean(PropertyManager.getProperty("graylog.enabled"))){
//				 GraylogLogger logger = new GraylogLogger(PropertyManager.getProperty("graylog.protocol"), PropertyManager.getProperty("graylog.host"), Integer.parseInt(PropertyManager.getProperty("graylog.port")), PropertyManager.getProperty("graylog.path"));
//				 logger.login("Login to City Data Workspace", "s4c.eng.it/datasources", Tool.CDW);
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			
			String token = t.getAccess_token();
			String refresh_token = t.getRefresh_token();
			
			if(token!=null && token.trim().length()>0){
				HttpSession session = request.getSession();
							session.setAttribute("loggedin", token);
							session.setAttribute("refresh_token", refresh_token);
							session.setAttribute("username", info.getDisplayName());
			}
			
		}
		catch(Exception e){ e.printStackTrace(); }
		
		response.sendRedirect("/IdraPortal");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setStatus(405);
		return;
	}

}
