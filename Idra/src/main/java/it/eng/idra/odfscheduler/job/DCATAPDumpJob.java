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
package it.eng.idra.odfscheduler.job;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import it.eng.idra.beans.ODFProperty;
import it.eng.idra.beans.dcat.DCATAPFormat;
import it.eng.idra.beans.dcat.DCATAPProfile;
import it.eng.idra.beans.dcat.DCATAPWriteType;
import it.eng.idra.beans.search.SearchResult;
import it.eng.idra.cache.MetadataCacheManager;
import it.eng.idra.dcat.dump.DCATAPSerializer;
import it.eng.idra.utils.PropertyManager;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class DCATAPDumpJob implements Job{

	public static Logger logger = LogManager.getLogger(DCATAPDumpJob.class);
	
	private static DCATAPFormat dumpFormat = DCATAPFormat
			.fromString(PropertyManager.getProperty(ODFProperty.DUMP_FORMAT));
	private static DCATAPProfile dumpProfile = DCATAPProfile
			.fromString(PropertyManager.getProperty(ODFProperty.DUMP_PROFILE));
	
	public DCATAPDumpJob() {}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub
		try {
			logger.info("Executing Dump procedure");
			SearchResult globalResult = MetadataCacheManager.searchAllDatasets();
			DCATAPSerializer.searchResultToDCATAP(globalResult, dumpFormat, dumpProfile, DCATAPWriteType.FILE);
		}catch(Exception e) {
			logger.error("Error during Dump procedure: "+e.getMessage());
		}

	}

}
