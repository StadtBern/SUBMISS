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

package ch.bern.submiss.services.impl.schedulers;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import org.ops4j.pax.cdi.api.Properties;
import org.ops4j.pax.cdi.api.Property;
import com.eurodyn.qlack2.fuse.scheduler.api.jobs.SchedulerJob;
import ch.bern.submiss.services.api.administration.SubmissionService;

/**
 * The Class AutomaticallyCloseSubmissionsScheduler. This class implements a scheduler that runs
 * every night and finds submissions that meet the conditions to be closed automatically and closes
 * them.
 */
@Transactional
@OsgiServiceProvider(classes = {SchedulerJob.class})
@Properties(@Property(name = "qlack2.job.qualifier",
    value = "AutomaticallyCloseSubmissionsScheduler"))
@Singleton
public class AutomaticallyCloseSubmissionsScheduler implements SchedulerJob {

  /** The Constant LOGGER. */
  private static final Logger LOGGER =
      Logger.getLogger(AutomaticallyCloseSubmissionsScheduler.class.getName());

  /** The submission service. */
  @Inject
  private SubmissionService submissionService;

  /*
   * (non-Javadoc)
   * 
   * @see com.eurodyn.qlack2.fuse.scheduler.api.jobs.SchedulerJob#execute(java.util.Map)
   */
  @Override
  public void execute(Map<String, Object> dataMap) {

    LOGGER.log(Level.INFO, "Job AutomaticallyCloseSubmissions started.");

    submissionService.automaticallyCloseSubmissions();

    LOGGER.log(Level.INFO, "Job AutomaticallyCloseSubmissions ended successfully.");
  }
}
