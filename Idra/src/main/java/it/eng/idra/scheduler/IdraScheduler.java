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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import static org.quartz.JobKey.jobKey;
import static org.quartz.TriggerKey.triggerKey;

import it.eng.idra.beans.odms.ODMSCatalogue;
import it.eng.idra.beans.odms.ODMSCatalogueFederationLevel;
import it.eng.idra.management.ODMSManager;
import it.eng.idra.scheduler.exception.SchedulerCannotBeInitialisedException;
import it.eng.idra.scheduler.exception.SchedulerNotInitialisedException;
import it.eng.idra.scheduler.job.DCATAPDumpJob;
import it.eng.idra.scheduler.job.DeleteLogsJob;
import it.eng.idra.scheduler.job.OAUTHTokenSynchJob;
import it.eng.idra.scheduler.job.ODMSSynchJob;

import static org.quartz.SimpleScheduleBuilder.*;
import javax.servlet.ServletContext;

import static org.quartz.ee.servlet.QuartzInitializerListener.QUARTZ_FACTORY_KEY;

import java.util.Date;
import java.util.List;

public class IdraScheduler {

	private static IdraScheduler idraScheduler = null;
	private static Logger logger = LogManager.getLogger(IdraScheduler.class);
	private static Scheduler scheduler = null;
	public static final String QUARTZ_FACTORY_KEY_MINE = "org.quartz.impl.StdSchedulerFactory.KEY";

	private IdraScheduler(Scheduler sched) {
		scheduler = sched;
	}

	public static void init(ServletContext ctx, boolean synchOnStart, boolean dumpOnStart)
			throws SchedulerCannotBeInitialisedException {
		StdSchedulerFactory factory = (StdSchedulerFactory) ctx.getAttribute(QUARTZ_FACTORY_KEY);
		try {
			idraScheduler = new IdraScheduler(factory.getScheduler());
			idraScheduler.startDeleteLogsJob();
			idraScheduler.startCataloguesDumpJob(dumpOnStart);
			idraScheduler.initSynchScheduler(synchOnStart);
		} catch (SchedulerException e) {
			throw new SchedulerCannotBeInitialisedException("The Idra Scheduler could not be initialised.", e);
		}
	}

	public static IdraScheduler getSingletonInstance() throws SchedulerNotInitialisedException {
		if (idraScheduler == null) {
			throw new SchedulerNotInitialisedException("The Idra Scheduler has not been properly initialised yet.");
		} else {
			return idraScheduler;
		}
	}

	public void clearAll() {
		try {
			scheduler.clear();
		} catch (SchedulerException e) {
			logger.error("Error while clearing: " + e.getMessage());
		}
	}

//    public void getalltriggers() {
//    	for(String group: scheduler.getTriggerGroupNames()) {
//    	    // enumerate each trigger in group
//    	    for(TriggerKey triggerKey : scheduler.getTriggerKeys(groupEquals(group))) {
//    	        System.out.println("Found trigger identified by: " + triggerKey);
//    	    }
//    	}
//
//    }

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

	public void triggerNow(String jobName) {
		try {
			if (scheduler.checkExists(jobKey(jobName, "jobs"))) {
				scheduler.triggerJob(jobKey(jobName, "jobs"));
			}
		} catch (Exception e) {

		}
	}

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

	public void rescheduleJob(String jobName, ODMSCatalogue node) {
		try {
			if (scheduler.checkExists(jobKey(jobName, "jobs"))) {

				Trigger newTrigger = TriggerBuilder.newTrigger()
						.withIdentity(Integer.toString(node.getId()), "triggers")
						.startAt(Date.from(node.getRegisterDate().toInstant().plusSeconds(node.getRefreshPeriod())))
						.withSchedule(simpleSchedule().repeatForever().withIntervalInSeconds(node.getRefreshPeriod())
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

	public void startCataloguesDumpJob(boolean dumpNow) {
		try {
			JobDetail job = JobBuilder.newJob(DCATAPDumpJob.class).withIdentity("dump_catalogues", "jobs").build();
			Trigger trigger = TriggerBuilder.newTrigger().withIdentity("dump_catalogues", "triggers")
					.withSchedule(
							CronScheduleBuilder.dailyAtHourAndMinute(0, 0).withMisfireHandlingInstructionDoNothing())
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

	protected void initSynchScheduler(boolean startNow) {
		for (final ODMSCatalogue node : ODMSManager.getODMSCataloguesbyFederationLevel(
				ODMSCatalogueFederationLevel.LEVEL_2, ODMSCatalogueFederationLevel.LEVEL_3)) {
			if (node.isActive()) {
				logger.info("Catalogue Name: " + node.getName());
				logger.info("Catalogue Lock: " + node.getSynchLock());
				logger.info("Catalogue Datasets Count: " + node.getDatasetCount());
				logger.info("Catalogue Start offset: " + node.getDatasetStart());
				this.startCataloguesSynchJob(node, startNow);
//    			this.getJobDetail(Integer.toString(node.getId()));

			}
		}
	}

	protected void getCataloguesJobDetail() {
		for (final ODMSCatalogue node : ODMSManager.getODMSCataloguesbyFederationLevel(
				ODMSCatalogueFederationLevel.LEVEL_2, ODMSCatalogueFederationLevel.LEVEL_3)) {
			if (node.isActive()) {
				this.getJobDetail(Integer.toString(node.getId()));
			}
		}
	}

	public void startCataloguesSynchJob(ODMSCatalogue node, boolean startNow) {
		// LEVEL_4 -> non deve sincronizzarsi
		if (!node.getFederationLevel().equals(ODMSCatalogueFederationLevel.LEVEL_4)) {
			try {
				JobDetail job = JobBuilder.newJob(ODMSSynchJob.class)
						.withIdentity(Integer.toString(node.getId()), "jobs").usingJobData("nodeID", node.getId())
						.build();

				Date d = Date.from(node.getRegisterDate().toInstant().plusSeconds(node.getRefreshPeriod()));

				Trigger trigger = TriggerBuilder.newTrigger().withIdentity(Integer.toString(node.getId()), "triggers")
						.startAt(d)
						.withSchedule(simpleSchedule().repeatForever().withIntervalInSeconds(node.getRefreshPeriod())
								.withMisfireHandlingInstructionNextWithExistingCount())
						.build();

				scheduler.scheduleJob(job, trigger);
				logger.info("Synch Job scheduled for catalogue " + node.getName());

			} catch (SchedulerException e) {
				logger.error(
						"Error while scheduling synch job for catalogue " + node.getName() + ": " + e.getMessage());
			} catch (Exception e) {
				logger.error("Generic Error while scheduling synch job for catalogue " + node.getName() + ": "
						+ e.getMessage());
			}

			if (startNow) {
				logger.info("Starting now synch for catalogue: " + node.getName());
				try {
					scheduler.triggerJob(jobKey(Integer.toString(node.getId()), "jobs"));
				} catch (SchedulerException e) {
					logger.error(
							"Error while Starting synch job for catalogue: " + node.getName() + " " + e.getMessage());
				}
			}
		}
	}

	public void startOAUTHTokenSynchJob(ODMSCatalogue node) {
		try {
			JobDetail job = JobBuilder.newJob(OAUTHTokenSynchJob.class)
					.withIdentity("synchToken_" + Integer.toString(node.getId()), "jobs")
					.usingJobData("nodeID", node.getId()).build();

			Trigger trigger = TriggerBuilder.newTrigger()
					.withIdentity("synchToken_" + Integer.toString(node.getId()), "triggers")
					.withSchedule(simpleSchedule().repeatForever().withIntervalInSeconds(3000) // Ogni 55 min fa il
																								// check
							.withMisfireHandlingInstructionNowWithRemainingCount() // Per casomai aggiornarlo subito
																					// quando il server riparte
					).startNow().build();

			scheduler.scheduleJob(job, trigger);
			logger.info("Token Synch Job scheduled for catalogue " + node.getName());

		} catch (SchedulerException e) {
			logger.error(
					"Error while scheduling Token synch job for catalogue " + node.getName() + ": " + e.getMessage());
		} catch (Exception e) {
			logger.error("Generic Error while scheduling Token synch job for catalogue " + node.getName() + ": "
					+ e.getMessage());
		}
	}

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
