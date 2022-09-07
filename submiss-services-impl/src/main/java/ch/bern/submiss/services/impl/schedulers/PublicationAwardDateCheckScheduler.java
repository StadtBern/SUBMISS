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

import java.util.List;
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
import ch.bern.submiss.services.api.administration.SubmissTaskService;
import ch.bern.submiss.services.api.administration.SubmissionCloseService;
import ch.bern.submiss.services.api.constants.TaskTypes;
import ch.bern.submiss.services.api.dto.SubmissTaskDTO;
import ch.bern.submiss.services.api.dto.SubmissionDTO;
import ch.bern.submiss.services.api.util.LookupValues;

/**
 * The Class PublicationAwardDateCheckScheduler. This class implements a scheduler that runs every
 * night and checks for every submission with status AWARD_NOTICES_CREATED set more than 1 day ago if
 * the field publicationDateAward has been set . If not then
 * a to do-task is created.
 */
@Transactional
@OsgiServiceProvider(classes = {SchedulerJob.class})
@Properties(@Property(name = "qlack2.job.qualifier", value = "PublicationAwardDateCheckScheduler"))
@Singleton
public class PublicationAwardDateCheckScheduler implements SchedulerJob {

  /** The Constant LOGGER. */
  private static final Logger LOGGER =
      Logger.getLogger(PublicationAwardDateCheckScheduler.class.getName());

  /** The submission close service. */
  @Inject
  private SubmissionCloseService submissionCloseService;

  /** The task service. */  
  @Inject
  private SubmissTaskService taskService;


  /*
   * (non-Javadoc)
   * 
   * @see com.eurodyn.qlack2.fuse.scheduler.api.jobs.SchedulerJob#execute(java.util.Map)
   */
  @Override
  public void execute(Map<String, Object> dataMap) {

    LOGGER.log(Level.INFO, "Job PublicationAwardDateCheck started.");

    // find submissions that meet the conditions
    // current status is AWARD_NOTICES_CREATED or CONTRACT_CREATED
    // status AWARD_NOTICES_CREATED set more than 1 day ago
    // field publicationDateAward is null
    List<SubmissionDTO> submissionDTOList = submissionCloseService.getSubmissionsNoPublicationAwardDate();
    
    // for every one of them a to do-task needs to be created, if not already exists
    for (SubmissionDTO submissionDTO : submissionDTOList) {
      SubmissTaskDTO taskDTO = new SubmissTaskDTO();
      taskDTO.setSubmission(submissionDTO);
      taskDTO.setDescription(TaskTypes.SET_SURCHARGE_PDATE);
      // Set created by the system as it is only when scheduler runs.
      taskDTO.setCreatedBy(LookupValues.SYSTEM);
      taskService.createTask(taskDTO);
    }
    
    LOGGER.log(Level.INFO, "Job PublicationAwardDateCheck ended successfully.");
  }
}
