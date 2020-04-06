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

package ch.bern.submiss.services.api.administration;

import java.util.Date;
import java.util.List;
import ch.bern.submiss.services.api.constants.TaskTypes;
import ch.bern.submiss.services.api.dto.SubmissTaskDTO;
import ch.bern.submiss.services.api.dto.SubmissionDTO;

/**
 * The Interface SubmissTaskService.
 */
public interface SubmissTaskService {

  /**
   * Get only Tasks for the tenant of the logged in user
   * 1. For the company part get a list of users assigned to this TENANT
   * 2. For the submission part filter the project by TENANT
   *
   * @return the all tasks
   */
  List<SubmissTaskDTO> getAllTasks(boolean showUserTasks);
  
  /**
   * Deletes a specific task and creates an audit entry 
   * 
   * @param taskId
   * @param isControllingTask
   */
  void settleTask(String taskId);
  
  /**
   * Checks if a task is a Controlling Task and then deletes this task
   * 
   * @param taskId
   */
  boolean settleControllingTask(String taskId);

  /**
   * @param taskId
   */
  void undertakeTask(String taskId);

  /**
   * Creates the task.
   *
   * @param taskDTO the task DTO
   * @return true, if project part task created
   */
  boolean createTask(SubmissTaskDTO taskDTO);
  
  /**
   * @param submissionId
   * @param companyId
   * @param taskType
   * @return a boolean indicating whether a task was found or not
   */
  boolean settleTask(String submissionId, String companyId, TaskTypes taskType, String userId, Date submitDate);
  
  /**
   * Gets the task by submission id and description.
   *
   * @param submissionId the submission id
   * @param description the description
   * @param companyId the company id
   * @return the task by submission id and description
   */
  String getTaskBySubmissionIdAndDescription(String submissionId, TaskTypes description, String companyId);

  /**
   * Update hybrid task.
   *
   * @param submissionId the submission id
   * @param companyId the company id
   * @param taskDescription the task description
   */
  void updateHybridTask(String submissionId, String companyId, TaskTypes taskDescription);

  /**
   * Automatically update task type.
   */
  void automaticallyUpdateTaskType();

  /**
   * Creates a proof request audit log for the project part.
   *
   * @param taskDTO the task DTO
   */
  void createProofRequestLogForProjectPart(SubmissTaskDTO taskDTO);

  /**
   * Creates a log for the settled proof request tasks in the project part.
   *
   * @param submissionDTO the submission DTO
   */
  void createLogForSettledProofRequestTasks(SubmissionDTO submissionDTO);
}
