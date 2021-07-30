/*******************************************************************************
 * Idra - Open Data Federation Platform
 *  Copyright (C) 2020 Engineering Ingegneria Informatica S.p.A.
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

package it.eng.idra.scheduler;

import it.eng.idra.beans.odms.OdmsCatalogue;
import it.eng.idra.beans.odms.OdmsCatalogueFederationLevel;
import it.eng.idra.management.OdmsManager;
import it.eng.idra.scheduler.exception.SchedulerCannotBeInitialisedException;
import it.eng.idra.scheduler.exception.SchedulerNotInitialisedException;
import it.eng.idra.scheduler.job.DcatApDumpJob;
import it.eng.idra.scheduler.job.DeleteLogsJob;
import it.eng.idra.scheduler.job.OauthTokenSynchJob;
import it.eng.idra.scheduler.job.OdmsSynchJob;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.DateBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import static org.quartz.JobKey.jobKey;
import static org.quartz.TriggerKey.triggerKey;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.ee.servlet.QuartzInitializerListener.QUARTZ_FACTORY_KEY;

// TODO: Auto-generated Javadoc
/**
 * The Class IdraScheduler.
 */
public class IdraScheduler {

  /** The idra scheduler. */
  private static IdraScheduler idraScheduler = null;

  /** The logger. */
  private static Logger logger = LogManager.getLogger(IdraScheduler.class);

  /** The scheduler. */
  private static Scheduler scheduler = null;

  /** The Constant QUARTZ_FACTORY_KEY_MINE. */
  public static final String QUARTZ_FACTORY_KEY_MINE = "org.quartz.impl.StdSchedulerFactory.KEY";

  /**
   * Instantiates a new idra scheduler.
   *
   * @param sched the sched
   */
  private IdraScheduler(Scheduler sched) {
    scheduler = sched;
  }

  /**
   * Inits the.
   *
   * @param ctx          the ctx
   * @param synchOnStart the synch on start
   * @param dumpOnStart  the dump on start
   * @throws SchedulerCannotBeInitialisedException the scheduler cannot be
   *                                               initialised exception
   */
  public static void init(ServletContext ctx, boolean synchOnStart, boolean dumpOnStart)
      throws SchedulerCannotBeInitialisedException {
    StdSchedulerFactory factory = (StdSchedulerFactory) ctx.getAttribute(QUARTZ_FACTORY_KEY);
    try {
      idraScheduler = new IdraScheduler(factory.getScheduler());
      idraScheduler.startDeleteLogsJob();
      idraScheduler.startCataloguesDumpJob(dumpOnStart);
      idraScheduler.initSynchScheduler(synchOnStart);
    } catch (SchedulerException e) {
      throw new SchedulerCannotBeInitialisedException(
          "The Idra Scheduler could not be initialised.", e);
    }
  }

  /**
   * Gets the singleton instance.
   *
   * @return the singleton instance
   * @throws SchedulerNotInitialisedException the scheduler not initialised
   *                                          exception
   */
  public static IdraScheduler getSingletonInstance() throws SchedulerNotInitialisedException {
    if (idraScheduler == null) {
      throw new SchedulerNotInitialisedException(
          "The Idra Scheduler has not been properly initialised yet.");
    } else {
      return idraScheduler;
    }
  }

  /**
   * Clear all.
   */
  public void clearAll() {
    try {
      scheduler.clear();
    } catch (SchedulerException e) {
      logger.error("Error while clearing: " + e.getMessage());
    }
  }

  /**
   * Gets the trigger detail.
   *
   * @param jobName the job name
   * @return the trigger detail
   */
  public Trigger getTriggerDetail(String jobName) {
    try {
      if (scheduler.checkExists(jobKey(jobName, "jobs"))) {
        Trigger t = scheduler.getTrigger(triggerKey(jobName, "triggers"));
        return t;
      }
    } catch (Exception e) {
      return null;
    }
    return null;
  }

  /**
   * Gets the job detail.
   *
   * @param jobName the job name
   * @return the job detail
   */
  public JobDetail getJobDetail(String jobName) {
    try {
      if (scheduler.checkExists(jobKey(jobName, "jobs"))) {
        JobDetail detail = scheduler.getJobDetail(jobKey(jobName, "jobs"));
        return detail;
      }
    } catch (Exception e) {
      return null;
    }
    return null;
  }

  /**
   * Trigger now.
   *
   * @param jobName the job name
   */
  public void triggerNow(String jobName) {
    try {
      if (scheduler.checkExists(jobKey(jobName, "jobs"))) {
        scheduler.triggerJob(jobKey(jobName, "jobs"));
      }
    } catch (Exception e) {
      logger.debug(e.getLocalizedMessage());
    }
  }

  /**
   * Delete job.
   *
   * @param jobName the job name
   */
  public void deleteJob(String jobName) {
    try {
      if (scheduler.checkExists(jobKey(jobName, "jobs"))) {
        scheduler.deleteJob(jobKey(jobName, "jobs"));
        logger.info("Job " + jobName + " deleted");
      } else {
        logger.info("Job " + jobName + " doesn't exist");
      }
    } catch (SchedulerException e) {

      e.printStackTrace();
    }
  }

  /**
   * Interrupt job.
   *
   * @param jobName the job name
   */
  public void interruptJob(String jobName) {
    try {
      if (scheduler.checkExists(jobKey(jobName, "jobs"))) {
        scheduler.interrupt(jobKey(jobName, "jobs"));
        logger.info("Job " + jobName + " interrupted");
      } else {
        logger.info("Job " + jobName + " doesn't exist");
      }
    } catch (SchedulerException e) {

      e.printStackTrace();
    }
  }

  /**
   * Reschedule job.
   *
   * @param jobName the job name
   * @param node    the node
   */
  public void rescheduleJob(String jobName, OdmsCatalogue node) {
    try {
      if (scheduler.checkExists(jobKey(jobName, "jobs"))) {

        Trigger newTrigger = TriggerBuilder.newTrigger()
            .withIdentity(Integer.toString(node.getId()), "triggers")
            .startAt(
                Date.from(node.getRegisterDate().toInstant().plusSeconds(node.getRefreshPeriod())))
            .withSchedule(
                simpleSchedule().repeatForever().withIntervalInSeconds(node.getRefreshPeriod())
                    .withMisfireHandlingInstructionNextWithExistingCount())
            .build();

        scheduler.rescheduleJob(triggerKey(jobName, "triggers"), newTrigger);
        logger.info("Job for node " + node.getName() + " rescheduled with new refresh period of "
            + node.getRefreshPeriod());
      } else {
        logger.info("Job doesn't exist");
      }
    } catch (SchedulerException e) {

      e.printStackTrace();
    }
  }

  /**
   * Start delete logs job.
   */
  public void startDeleteLogsJob() {
    try {
      JobDetail job = JobBuilder.newJob(DeleteLogsJob.class).withIdentity("delete_logs", "jobs")
          .withDescription("This process deletes logs").build();
      Trigger trigger = TriggerBuilder.newTrigger().withIdentity("delete_logs", "triggers")
          .withSchedule(CronScheduleBuilder.weeklyOnDayAndHourAndMinute(DateBuilder.SUNDAY, 2, 0)
              .withMisfireHandlingInstructionDoNothing())
          .build();
      scheduler.scheduleJob(job, trigger);
      logger.info("Delete Logs Job scheduled");
    } catch (SchedulerException e) {
      logger.error("Error while scheduling Delete Logs job: " + e.getMessage());
    } catch (Exception e) {
      logger.error("Generic Error while scheduling Delete Logs job: " + e.getMessage());
    }
  }

  /**
   * Start catalogues dump job.
   *
   * @param dumpNow the dump now
   */
  public void startCataloguesDumpJob(boolean dumpNow) {
    try {
      JobDetail job = JobBuilder.newJob(DcatApDumpJob.class).withIdentity("dump_catalogues", "jobs")
          .build();
      Trigger trigger = TriggerBuilder.newTrigger().withIdentity("dump_catalogues", "triggers")
          .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(0, 0)
              .withMisfireHandlingInstructionDoNothing())
          .build();
      scheduler.scheduleJob(job, trigger);
      logger.info("Catalogues Dump Job scheduled");
    } catch (SchedulerException e) {
      logger.error("Error while scheduling Catalogues Dump job: " + e.getMessage());
    } catch (Exception e) {
      logger.error("Generic Error while scheduling Catalogues Dump job: " + e.getMessage());
    }

    if (dumpNow) {
      logger.info("Starting Now Catalogues Dump");
      try {
        scheduler.triggerJob(jobKey("dump_catalogues", "jobs"));
      } catch (SchedulerException e) {
        logger.error("Error while Starting Catalogues Dump job: " + e.getMessage());
      }
    }

  }

  /**
   * Inits the synch scheduler.
   *
   * @param startNow the start now
   */
  protected void initSynchScheduler(boolean startNow) {
    for (final OdmsCatalogue node : OdmsManager.getOdmsCataloguesbyFederationLevel(
        OdmsCatalogueFederationLevel.LEVEL_2, OdmsCatalogueFederationLevel.LEVEL_3)) {
      if (node.isActive()) {
        logger.info("Catalogue Name: " + node.getName());
        logger.info("Catalogue Lock: " + node.getSynchLock());
        logger.info("Catalogue Datasets Count: " + node.getDatasetCount());
        logger.info("Catalogue Start offset: " + node.getDatasetStart());
        this.startCataloguesSynchJob(node, startNow);
        // this.getJobDetail(Integer.toString(node.getId()));
      }
    }
  }

  /**
   * Gets the catalogues job detail.
   *
   * @return the catalogues job detail
   */
  protected void getCataloguesJobDetail() {
    for (final OdmsCatalogue node : OdmsManager.getOdmsCataloguesbyFederationLevel(
        OdmsCatalogueFederationLevel.LEVEL_2, OdmsCatalogueFederationLevel.LEVEL_3)) {
      if (node.isActive()) {
        this.getJobDetail(Integer.toString(node.getId()));
      }
    }
  }

  /**
   * Start catalogues synch job.
   *
   * @param node     the node
   * @param startNow the start now
   */
  public void startCataloguesSynchJob(OdmsCatalogue node, boolean startNow) {
    // LEVEL_4 -> non deve sincronizzarsi
    if (!node.getFederationLevel().equals(OdmsCatalogueFederationLevel.LEVEL_4)) {
      try {
        JobDetail job = JobBuilder.newJob(OdmsSynchJob.class)
            .withIdentity(Integer.toString(node.getId()), "jobs")
            .usingJobData("nodeID", node.getId()).build();

        Date d = Date.from(node.getRegisterDate().toInstant().plusSeconds(node.getRefreshPeriod()));

        Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity(Integer.toString(node.getId()), "triggers").startAt(d)
            .withSchedule(
                simpleSchedule().repeatForever().withIntervalInSeconds(node.getRefreshPeriod())
                    .withMisfireHandlingInstructionNextWithExistingCount())
            .build();

        scheduler.scheduleJob(job, trigger);
        logger.info("Synch Job scheduled for catalogue " + node.getName());

      } catch (SchedulerException e) {
        logger.error("Error while scheduling synch job for catalogue " + node.getName() + ": "
            + e.getMessage());
      } catch (Exception e) {
        logger.error("Generic Error while scheduling synch job for catalogue " + node.getName()
            + ": " + e.getMessage());
      }

      if (startNow) {
        logger.info("Starting now synch for catalogue: " + node.getName());
        try {
          scheduler.triggerJob(jobKey(Integer.toString(node.getId()), "jobs"));
        } catch (SchedulerException e) {
          logger.error("Error while Starting synch job for catalogue: " + node.getName() + " "
              + e.getMessage());
        }
      }
    }
  }

  /**
   * Start oauth token synch job.
   *
   * @param node the node
   */
  public void startOauthTokenSynchJob(OdmsCatalogue node) {
    try {
      JobDetail job = JobBuilder.newJob(OauthTokenSynchJob.class)
          .withIdentity("synchToken_" + Integer.toString(node.getId()), "jobs")
          .usingJobData("nodeID", node.getId()).build();

      Trigger trigger = TriggerBuilder.newTrigger()
          .withIdentity("synchToken_" + Integer.toString(node.getId()), "triggers")
          .withSchedule(simpleSchedule().repeatForever().withIntervalInSeconds(3000)
              .withMisfireHandlingInstructionNowWithRemainingCount())
          .startNow().build();

      scheduler.scheduleJob(job, trigger);
      logger.info("Token Synch Job scheduled for catalogue " + node.getName());

    } catch (SchedulerException e) {
      logger.error("Error while scheduling Token synch job for catalogue " + node.getName() + ": "
          + e.getMessage());
    } catch (Exception e) {
      logger.error("Generic Error while scheduling Token synch job for catalogue " + node.getName()
          + ": " + e.getMessage());
    }
  }

  /**
   * Checks if is job running.
   *
   * @param jobName the job name
   * @return true, if is job running
   * @throws SchedulerException the scheduler exception
   */
  public boolean isJobRunning(String jobName) throws SchedulerException {
    List<JobExecutionContext> currentJobs = scheduler.getCurrentlyExecutingJobs();
    for (JobExecutionContext jobCtx : currentJobs) {
      String thisJobName = jobCtx.getJobDetail().getKey().getName();
      if (jobName.equalsIgnoreCase(thisJobName)) {
        return true;
      }
    }
    return false;
  }

}
