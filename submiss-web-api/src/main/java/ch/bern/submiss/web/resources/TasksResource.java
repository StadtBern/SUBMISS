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

package ch.bern.submiss.web.resources;

import ch.bern.submiss.services.api.administration.CompanyService;
import ch.bern.submiss.services.api.administration.SubmissTaskService;
import ch.bern.submiss.services.api.administration.SubmissionService;
import ch.bern.submiss.services.api.constants.TaskTypes;
import ch.bern.submiss.services.api.dto.SubmissTaskDTO;
import ch.bern.submiss.services.api.dto.SubmissionDTO;
import ch.bern.submiss.services.api.util.View;
import ch.bern.submiss.web.forms.TaskCreateForm;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import com.fasterxml.jackson.annotation.JsonView;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.ops4j.pax.cdi.api.OsgiService;

@Path("/tasks")
@Singleton
public class TasksResource {
  
  @OsgiService
  @Inject
  SubmissTaskService taskService;
  
  @OsgiService
  @Inject
  SubmissionService submissionService;
  
  @OsgiService
  @Inject
  CompanyService companyService;

  @GET
  @Path("/allTasks/{showUserTasks}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @JsonView(View.Public.class)
  public Response getAllTasks(@PathParam("showUserTasks") boolean showUserTasks) {
    return Response.ok(taskService.getAllTasks(showUserTasks)).build();
  }

  @GET
  @Path("/companyTask/{companyId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @JsonView(View.Public.class)
  public Response getCompanyTask(@PathParam("companyId") String companyId) {
    return Response.ok(taskService.getCompanyTask(companyId)).build();
  }
  
  @DELETE
  @Path("/settleTask/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response settleTask(@PathParam("id") String taskId) {

    Set<ValidationError> optimisticLockErrors = taskService.settleTaskOptimisticLock(taskId);
    if (!optimisticLockErrors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(optimisticLockErrors).build();
    }

    if (!taskService.settleControllingTask(taskId)) {
      return Response.status(Response.Status.BAD_REQUEST).build();
    }
    
    return Response.ok().build();
  }
  
  @POST
  @Path("/undertakeTask/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response undertakeTask(@PathParam("id") String taskId) {

    taskService.undertakeTask(taskId);
    
    return Response.ok().build();
    
  }
  
  
  @POST
  @Path("/createTask")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createTask(@Valid TaskCreateForm taskForm) {

    SubmissTaskDTO taskDTO = new SubmissTaskDTO();
    if (StringUtils.isNotBlank(taskForm.getSubmissionID())) {
      // Variable to indicate whether the project part task has been created.
      boolean projectTaskCreated = false;
      if (taskForm.getSubmittentIDs().isEmpty()) {
        taskDTO.setDescription(TaskTypes.valueOf(taskForm.getTaskType()));
        taskDTO.setSubmission(submissionService.getSubmissionById(taskForm.getSubmissionID()));
        projectTaskCreated = taskService.createTask(taskDTO); 
      } else {
        SubmissionDTO submissionDTO = submissionService.getSubmissionById(taskForm.getSubmissionID());
        for (String submittentID : taskForm.getSubmittentIDs()) {
          taskDTO.setDescription(TaskTypes.valueOf(taskForm.getTaskType()));
          taskDTO.setSubmission(submissionDTO);
          taskDTO.setCompany(companyService.getCompanyById(submittentID));
          if (!projectTaskCreated) {
            // If no project part task has been created, get the projectTaskCreated value from the
            // createTask function.
            projectTaskCreated = taskService.createTask(taskDTO);
          } else {
            // If a project part task has already been created, just call the createTask function
            // without assigning its returned value.
            taskService.createTask(taskDTO);
          }
        }
      }
      if (projectTaskCreated) {
        // Create a proof request audit log, only if a task has been created.
        taskService.createProofRequestLogForProjectPart(taskDTO);
      }
    } else if (StringUtils.isBlank(taskForm.getSubmissionID())
        && !taskForm.getSubmittentIDs().isEmpty()) {
        for (String submittentID : taskForm.getSubmittentIDs()) {
          taskDTO.setDescription(TaskTypes.valueOf(taskForm.getTaskType()));
          taskDTO.setCompany(companyService.getCompanyById(submittentID));
          taskService.createTask(taskDTO);
        }
    }

    return Response.ok().build();

  }
  
}
