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

package it.eng.idra.scheduler.job;

import it.eng.idra.beans.IdraProperty;
import it.eng.idra.beans.dcat.DcatApFormat;
import it.eng.idra.beans.dcat.DcatApProfile;
import it.eng.idra.beans.dcat.DcatApWriteType;
import it.eng.idra.beans.search.SearchResult;
import it.eng.idra.cache.MetadataCacheManager;
import it.eng.idra.dcat.dump.DcatApSerializer;
import it.eng.idra.utils.PropertyManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

// TODO: Auto-generated Javadoc
/**
 * The Class DcatApDumpJob.
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class DcatApDumpJob implements Job {

  /** The logger. */
  public static Logger logger = LogManager.getLogger(DcatApDumpJob.class);

  /** The Constant dumpFormat. */
  private static final DcatApFormat dumpFormat = DcatApFormat
      .fromString(PropertyManager.getProperty(IdraProperty.DUMP_FORMAT));

  /** The Constant dumpProfile. */
  private static final DcatApProfile dumpProfile = DcatApProfile
      .fromString(PropertyManager.getProperty(IdraProperty.DUMP_PROFILE));

  /**
   * Instantiates a new dcat ap dump job.
   */
  public DcatApDumpJob() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
   */
  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    // TODO Auto-generated method stub
    try {
      logger.info("Executing Dump procedure");
      SearchResult globalResult = MetadataCacheManager.searchAllDatasets();
      DcatApSerializer.searchResultToDcatAp(globalResult, dumpFormat, dumpProfile,
          DcatApWriteType.FILE);
    } catch (Exception e) {
      logger.error("Error during Dump procedure: " + e.getMessage());
    }

  }

}
