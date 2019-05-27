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
package it.eng.idra.management;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

import it.eng.idra.beans.IdraProperty;
import it.eng.idra.scheduler.IdraScheduler;
import it.eng.idra.utils.PropertyManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.*;

public class InitServletListener implements ServletContextListener {
	private static Logger logger = LogManager.getLogger(InitServletListener.class);

	@Override
	public void contextInitialized(ServletContextEvent arg0) {

		logger.info("Welcome to Idra - Open Data Federation Platform! - Init start");

		System.setProperty(IdraProperty.SESAME_REPO_NAME.toString(),
				PropertyManager.getProperty(IdraProperty.SESAME_REPO_NAME).trim());
		System.setProperty(IdraProperty.SESAME_SERVER_URI.toString(),
				PropertyManager.getProperty(IdraProperty.SESAME_SERVER_URI).trim());
		System.setProperty(IdraProperty.SESAME_ENDPOINT.toString(),
				PropertyManager.getProperty(IdraProperty.SESAME_ENDPOINT).trim());
		// System.setProperty("sesameDownloadDirectory",
		// PropertyManager.getProperty("sesameDownloadDirectory").trim());

		// System.setProperty("javax.net.ssl.trustStore","");
		if (Boolean.parseBoolean(PropertyManager.getProperty(IdraProperty.HTTP_PROXY_ENABLED).trim())
				&& StringUtils.isNotBlank(PropertyManager.getProperty(IdraProperty.HTTP_PROXY_HOST).trim())) {

			System.setProperty(IdraProperty.HTTP_PROXY_HOST.toString(),
					PropertyManager.getProperty(IdraProperty.HTTP_PROXY_HOST).trim());
			System.setProperty(IdraProperty.HTTP_PROXY_PORT.toString(),
					PropertyManager.getProperty(IdraProperty.HTTP_PROXY_PORT).trim());
			System.setProperty(IdraProperty.HTTP_PROXY_NONPROXYHOSTS.toString(),
					PropertyManager.getProperty(IdraProperty.HTTP_PROXY_NONPROXYHOSTS).trim());
			String proxyUser = PropertyManager.getProperty(IdraProperty.HTTP_PROXY_USER).trim();
			String proxyPassword = PropertyManager.getProperty(IdraProperty.HTTP_PROXY_PASSWORD).trim();
			if (StringUtils.isNotBlank(proxyUser) && StringUtils.isNotBlank(proxyPassword)) {
				Authenticator.setDefault(new Authenticator() {
					public PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(proxyUser, proxyPassword.toCharArray());
					}
				});

				System.setProperty(IdraProperty.HTTP_PROXY_USER.toString(), proxyUser);
				System.setProperty(IdraProperty.HTTP_PROXY_PASSWORD.toString(), proxyPassword);
			}

			String proxyHttpsUser = PropertyManager.getProperty(IdraProperty.HTTP_PROXY_USER).trim();
			String proxyHttpsPassword = PropertyManager.getProperty(IdraProperty.HTTP_PROXY_PASSWORD).trim();

			if (StringUtils.isNotBlank(proxyHttpsUser) && StringUtils.isNotBlank(proxyHttpsPassword)) {
				// Authenticator.setDefault(new Authenticator() {
				// public PasswordAuthentication getPasswordAuthentication() {
				// return new PasswordAuthentication(proxyHttpsUser,
				// proxyHttpsPassword.toCharArray());
				// }
				// });

				System.setProperty(IdraProperty.HTTPS_PROXY_USER.toString(), proxyHttpsUser);
				System.setProperty(IdraProperty.HTTPS_PROXY_PASSWORD.toString(), proxyHttpsPassword);
			}

			System.setProperty(IdraProperty.HTTPS_PROXY_HOST.toString(),
					PropertyManager.getProperty(IdraProperty.HTTP_PROXY_HOST).trim());
			System.setProperty(IdraProperty.HTTPS_PROXY_PORT.toString(),
					PropertyManager.getProperty(IdraProperty.HTTP_PROXY_PORT).trim());
			System.setProperty(IdraProperty.HTTPS_PROXY_NONPROXYHOSTS.toString(),
					PropertyManager.getProperty(IdraProperty.HTTP_PROXY_NONPROXYHOSTS).trim());

		}
		try {
			
			FederationCore.init((Boolean.parseBoolean(PropertyManager.getProperty(IdraProperty.LOAD_CACHE_FROM_DB))),
					arg0.getServletContext().getRealPath("/WEB-INF/classes/solr"));
			//Moved into a specific listener
//			IdraScheduler.init(arg0.getServletContext(),Boolean.parseBoolean(PropertyManager.getProperty(IdraProperty.SYNCH_ON_START)));
			
			logger.info("Open Data Federation started");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

		try {
			logger.info("Open Data Federation is shutting down!");
			FederationCore.onFinalize();
			logger.info("Finalize end");

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
