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
//package it.eng.idra.authentication.fiware;
//
//import it.eng.idra.authentication.fiware.configuration.IDMProperty;
//import it.eng.idra.utils.PropertyManager;
//
//import java.net.URI;
//
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//import javax.ws.rs.core.Response;
//
///**
// * Servlet implementation class Logout
// */
//@WebServlet("/logout")
//public class Logout extends HttpServlet {
//
//private static final long serialVersionUID = 693724303976303888L;
//private static final String logoutallback = 
//PropertyManager.getProperty(IDMProperty.IDM_LOGOUT_CALLBACK);
//protected void doGet(HttpServletRequest request, HttpServletResponse response) {
//
//System.out.println("Logging out...");
//
//HttpSession session = request.getSession();
//session.removeAttribute("loggedin");
//session.removeAttribute("refresh_token");
//session.removeAttribute("username");
//session.invalidate();
//try{ Response.temporaryRedirect(URI.create(httpRequest.getContextPath() 
//+ "/IdraPortal")).build();(PropertyManager.getProperty("idm.logout.callback")); }
//catch(Exception e){
//e.printStackTrace();
//}
//}
//
//}
