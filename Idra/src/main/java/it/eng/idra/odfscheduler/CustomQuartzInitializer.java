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

import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.SchedulerException;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.quartz.impl.StdSchedulerFactory;

public class CustomQuartzInitializer extends QuartzInitializerListener {
	
	private static Logger logger = LogManager.getLogger(CustomQuartzInitializer.class);
	
	private Properties getQuartzProperties(String file) throws IOException{
		
		Properties props = new Properties();
		props.load(CustomQuartzInitializer.class.getClassLoader().getResourceAsStream(file));
		
		System.getenv().entrySet().stream()
			.filter(e -> {
				return e.getKey().startsWith("org.quartz.");
			})
			.forEach(e->{
				logger.debug("Overriding Quartz property " + e.getKey() + ": "+e.getValue());
				props.setProperty(e.getKey(), e.getValue());
			});
		
		return props;
	}
	
	@Override
	protected StdSchedulerFactory getSchedulerFactory(String configFile) throws SchedulerException {
		StdSchedulerFactory schedFactory = new StdSchedulerFactory();
		try{ schedFactory.initialize(getQuartzProperties(configFile)); }
		catch(Exception e){
			e.printStackTrace();
		}
		return schedFactory;
		
	}
	
}
