/*******************************************************************************
 * Idra - Open Data Federation Platform
 * Copyright (C) 2021 Engineering Ingegneria Informatica S.p.A.
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
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

package it.eng.idra.scheduler;

import it.eng.idra.beans.IdraProperty;
import it.eng.idra.scheduler.exception.SchedulerCannotBeInitialisedException;
import it.eng.idra.utils.PropertyManager;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving idraScheduler events. The class that is
 * interested in processing a idraScheduler event implements this interface, and
 * the object created with that class is registered with a component using the
 * component's addIdraSchedulerListener method. When the
 * idraScheduler event occurs, that object's appropriate method is invoked.
 *
 * @see IdraSchedulerEvent
 */
public class IdraSchedulerListener implements ServletContextListener {

  /** The logger. */
  private static Logger logger = LogManager.getLogger(IdraSchedulerListener.class);

  /** The Constant synchOnStart. */
  private static final boolean synchOnStart = Boolean
      .parseBoolean(PropertyManager.getProperty(IdraProperty.SYNCH_ON_START));

  /** The Constant dumpOnStart. */
  private static final boolean dumpOnStart = Boolean
      .parseBoolean(PropertyManager.getProperty(IdraProperty.DUMP_ON_START));

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.
   * ServletContextEvent)
   */
  @Override
  public void contextInitialized(ServletContextEvent arg0) {
    logger.info("IdraScheduler listener Context init");
    try {
      IdraScheduler.init(arg0.getServletContext(), synchOnStart, dumpOnStart);
    } catch (SchedulerCannotBeInitialisedException e) {
      logger.error("Error while initialising the IdraScheduler " + e.getMessage());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.
   * ServletContextEvent)
   */
  @Override
  public void contextDestroyed(ServletContextEvent arg0) {
    // TODO Auto-generated method stub
    try {
      logger.info("IdraScheduler listener Context stopped");
    } catch (Exception ex) {
      logger.debug(ex.getLocalizedMessage());
    }
  }

}
