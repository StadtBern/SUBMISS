/*
 *  Submiss, eProcurement system for managing tenders
 *  Copyright (C) 2019 Stadt Bern
 *  Licensed under the EUPL, Version 1.2 or - as soon as they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at:
 *  https://joinup.ec.europa.eu/collection/eupl
 *  Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 *  distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *  express or implied. See the Licence for the specific language governing permissions and limitations
 *  under the Licence.
 */

package ch.bern.submiss.services.impl.bootstrap;

import ch.bern.submiss.services.api.util.LookupValues;
import com.eurodyn.qlack2.fuse.aaa.api.JSONConfigService;
import com.eurodyn.qlack2.fuse.lexicon.api.BundleUpdateService;
import com.eurodyn.qlack2.fuse.scheduler.api.SchedulerService;
import com.eurodyn.qlack2.fuse.scheduler.api.jobs.SchedulerWrappedJob;
import com.eurodyn.qlack2.fuse.scheduler.api.jobs.triggers.SchedulerWrappedTrigger;
import com.eurodyn.qlack2.fuse.scheduler.api.utils.Constants;
import com.eurodyn.qlack2.fuse.scheduler.api.utils.Constants.TRIGGERS;
import com.eurodyn.qlack2.fuse.scheduler.api.utils.Constants.TRIGGER_MISFIRE;
import com.eurodyn.qlack2.util.liquibase.api.LiquibaseBootMigrationsDoneService;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.ops4j.pax.cdi.api.OsgiService;
import org.osgi.framework.BundleContext;

@Singleton
public class Bootstrap {
  @Inject
  @OsgiService
  BundleUpdateService bundleUpdateService;

  @Inject
  @OsgiService
  JSONConfigService jsonConfigService;

  @Inject
  BundleContext bundleContext;

  @Inject
  @OsgiService
  protected SchedulerService schedulerService;

  @Inject
  @OsgiService
  /**
   * Make sure liquibase migrations are executed before allowing access to this bundle.
   */
  LiquibaseBootMigrationsDoneService liquibaseBootMigrationsDoneService;
  
  private static final Logger LOGGER = Logger.getLogger(Bootstrap.class.getName());

  @PostConstruct
  public void init() {
    /* Update translations */
    bundleUpdateService.processBundle(bundleContext.getBundle(), "custom-qlack-lexicon.yaml");

    /* Update AAA */
    jsonConfigService.processBundle(bundleContext.getBundle());
    
    // create scheduler to check the validity of the company proofs, if it does not exist
    String jobName = LookupValues.COMPANY_PROOFS_SCHEDULER_NAME;
    String jobGroup = LookupValues.APPLICATION_NAME;
    
    if (!schedulerService.checkExistJob(jobName, jobGroup)) {
      // create the job object, where the name is set
      SchedulerWrappedJob job = new SchedulerWrappedJob();
      job.setJobName(jobName);
      job.setJobGroup(jobGroup);
      job.setJobQualifier(jobName);
      // create the trigger object, where the time the scheduler is triggered is set
      // this trigger needs to run daily
      SchedulerWrappedTrigger trigger = new SchedulerWrappedTrigger();
      trigger.setTriggerName(LookupValues.COMPANY_PROOFS_SCHEDULER_TRIGGER_NAME);
      trigger.setTriggerType(TRIGGERS.Daily);
      trigger.setDailyTime(LookupValues.COMPANY_PROOFS_SCHEDULER_DAILY_TIME);
      trigger.setStartOn(new Date());
      trigger.setPriority(6);
      trigger.setTriggerMisfire(TRIGGER_MISFIRE.MISFIRE_INSTRUCTION_DO_NOTHING);
      
      schedulerService.scheduleJob(job, trigger);
      LOGGER.log(Level.INFO, "Scheduler CompanyProofsValidityCheck scheduled.");
    }
    
    // create scheduler to check if the publication award date exists, if it does not exist
    jobName = LookupValues.PUBLICATION_AWARD_DATE_SCHEDULER_NAME;
    
    if (!schedulerService.checkExistJob(jobName, jobGroup)) {
      // create the job object, where the name is set
      SchedulerWrappedJob job = new SchedulerWrappedJob();
      job.setJobName(jobName);
      job.setJobGroup(jobGroup);
      job.setJobQualifier(jobName);
      // create the trigger object, where the time the scheduler is triggered is set
      // this trigger needs to run daily
      SchedulerWrappedTrigger trigger = new SchedulerWrappedTrigger();
      trigger.setTriggerName(LookupValues.PUBLICATION_AWARD_DATE_SCHEDULER_TRIGGER_NAME);
      trigger.setTriggerType(TRIGGERS.Daily);
      trigger.setDailyTime(LookupValues.PUBLICATION_AWARD_DATE_SCHEDULER_DAILY_TIME);
      trigger.setStartOn(new Date());
      trigger.setPriority(5);
      trigger.setTriggerMisfire(TRIGGER_MISFIRE.MISFIRE_INSTRUCTION_DO_NOTHING);
      
      schedulerService.scheduleJob(job, trigger);
      LOGGER.log(Level.INFO, "Scheduler PublicationAwardDateCheck scheduled.");
    }

    // create scheduler to check submissions with no activity for a long time that are not closed, if it does not exist
    jobName = LookupValues.NOT_CLOSED_SUBMISSIONS_SCHEDULER_NAME;
    
    if (!schedulerService.checkExistJob(jobName, jobGroup)) {
      // create the job object, where the name is set
      SchedulerWrappedJob job = new SchedulerWrappedJob();
      job.setJobName(jobName);
      job.setJobGroup(jobGroup);
      job.setJobQualifier(jobName);
      // create the trigger object, where the time the scheduler is triggered is set
      // this trigger needs to run daily
      SchedulerWrappedTrigger trigger = new SchedulerWrappedTrigger();
      trigger.setTriggerName(LookupValues.NOT_CLOSED_SUBMISSIONS_SCHEDULER_TRIGGER_NAME);
      trigger.setTriggerType(TRIGGERS.Daily);
      trigger.setDailyTime(LookupValues.NOT_CLOSED_SUBMISSIONS_SCHEDULER_DAILY_TIME);
      trigger.setStartOn(new Date());
      trigger.setPriority(4);
      trigger.setTriggerMisfire(TRIGGER_MISFIRE.MISFIRE_INSTRUCTION_DO_NOTHING);
      
      schedulerService.scheduleJob(job, trigger);
      LOGGER.log(Level.INFO, "Scheduler NotClosedSubmissionsCheck scheduled.");
    }
    
    // create scheduler to automatically close submissions, if it does not exist
    jobName = LookupValues.AUTO_CLOSE_SUBMISSIONS_SCHEDULER_NAME;
    
    if (!schedulerService.checkExistJob(jobName, jobGroup)) {
      // create the job object, where the name is set
      SchedulerWrappedJob job = new SchedulerWrappedJob();
      job.setJobName(jobName);
      job.setJobGroup(jobGroup);
      job.setJobQualifier(jobName);
      // create the trigger object, where the time the scheduler is triggered is set
      // this trigger needs to run daily
      SchedulerWrappedTrigger trigger = new SchedulerWrappedTrigger();
      trigger.setTriggerName(LookupValues.AUTO_CLOSE_SUBMISSIONS_SCHEDULER_TRIGGER_NAME);
      trigger.setTriggerType(TRIGGERS.Daily);
      trigger.setDailyTime(LookupValues.AUTO_CLOSE_SUBMISSIONS_SCHEDULER_DAILY_TIME);
      trigger.setStartOn(new Date());
      trigger.setPriority(3);
      trigger.setTriggerMisfire(TRIGGER_MISFIRE.MISFIRE_INSTRUCTION_DO_NOTHING);
      
      schedulerService.scheduleJob(job, trigger);
      LOGGER.log(Level.INFO, "Scheduler AutomaticallyCloseSubmissions scheduled.");
    }
    
    jobName = LookupValues.AUTO_DEACTIVATE_USER_SCHEDULER_NAME;
    
    if (!schedulerService.checkExistJob(jobName, jobGroup)) {
      // create the job object, where the name is set
      SchedulerWrappedJob job = new SchedulerWrappedJob();
      job.setJobName(jobName);
      job.setJobGroup(jobGroup);
      job.setJobQualifier(jobName);
      // create the trigger object, where the time the scheduler is triggered is set
      // this trigger needs to run daily
      SchedulerWrappedTrigger trigger = new SchedulerWrappedTrigger();
      trigger.setTriggerName(LookupValues.AUTO_DEACTIVATE_USER_SCHEDULER_TRIGGER_NAME);
      trigger.setTriggerType(TRIGGERS.Daily);
      trigger.setDailyTime(LookupValues.AUTO_DEACTIVATE_USER_SCHEDULER_DAILY_TIME);
      trigger.setStartOn(new Date());
      trigger.setPriority(2);
      trigger.setTriggerMisfire(TRIGGER_MISFIRE.MISFIRE_INSTRUCTION_DO_NOTHING);
      
      schedulerService.scheduleJob(job, trigger);
      LOGGER.log(Level.INFO, "Scheduler AutomaticallyDeactivateUser scheduled.");
    }
    
    jobName = LookupValues.AUTO_UPDATE_TASK_TYPE_SCHEDULER_NAME;
    
    if (!schedulerService.checkExistJob(jobName, jobGroup)) {
      // create the job object, where the name is set
      SchedulerWrappedJob job = new SchedulerWrappedJob();
      job.setJobName(jobName);
      job.setJobGroup(jobGroup);
      job.setJobQualifier(jobName);
      // create the trigger object, where the time the scheduler is triggered is set
      // this trigger needs to run daily
      SchedulerWrappedTrigger trigger = new SchedulerWrappedTrigger();
      trigger.setTriggerName(LookupValues.AUTO_UPDATE_TASK_TYPE_SCHEDULER_TRIGGER_NAME);
      trigger.setTriggerType(TRIGGERS.Daily);
      trigger.setDailyTime(LookupValues.AUTO_UPDATE_TASK_TYPE_SCHEDULER_DAILY_TIME);
      trigger.setStartOn(new Date());
      trigger.setPriority(1);
      trigger.setTriggerMisfire(TRIGGER_MISFIRE.MISFIRE_INSTRUCTION_DO_NOTHING);
      
      schedulerService.scheduleJob(job, trigger);
      LOGGER.log(Level.INFO, "Scheduler AutomaticallyUpdateTaskTypeScheduler scheduled.");
    }
  }
}
