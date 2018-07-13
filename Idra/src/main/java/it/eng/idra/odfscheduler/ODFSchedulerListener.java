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
package it.eng.idra.odfscheduler;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.eng.idra.beans.ODFProperty;
import it.eng.idra.utils.PropertyManager;


public class ODFSchedulerListener implements ServletContextListener {

    private static Logger logger = LogManager.getLogger(ODFSchedulerListener.class);

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        logger.info("ODFScheduler listener Context init");
        try {
        	ODFScheduler.init(arg0.getServletContext(),Boolean.parseBoolean(PropertyManager.getProperty(ODFProperty.SYNCH_ON_START)));
        } catch (SchedulerCannotBeInitialisedException e) {
            logger.error("Error while initialising the scheduler "+ e.getMessage());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub
        try {
            logger.info("ODFScheduler listener Context stopped");
        } catch (Exception ex) {
        }
    }

}
