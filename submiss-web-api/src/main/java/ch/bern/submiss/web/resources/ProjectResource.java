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

import ch.bern.submiss.services.api.administration.ProjectService;
import ch.bern.submiss.services.api.dto.PaginationDTO;
import ch.bern.submiss.services.api.dto.ProjectDTO;
import ch.bern.submiss.services.api.dto.SearchDTO;
import ch.bern.submiss.services.api.dto.TenderDTO;
import ch.bern.submiss.services.api.util.ValidationMessages;
import ch.bern.submiss.web.forms.ProjectForm;
import ch.bern.submiss.web.forms.SearchForm;
import ch.bern.submiss.web.mappers.ProjectMapper;
import ch.bern.submiss.web.mappers.SearchMapper;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.commons.lang3.StringUtils;
import org.ops4j.pax.cdi.api.OsgiService;

/**
 * REST handler for project.
 */
@Path("/project")
@Singleton
public class ProjectResource {


  /** The project service. */
  @OsgiService
  @Inject
  private ProjectService projectService;
  
  private static final String PROJECT_NAME = "projectName";

  /**
   * Find a project based on it's UUID.
   *
   * @param id the project UUID
   * @return
   *         <ul>
   *         <li>200 (OK) - If the project exists, and the project as data.</li>
   *         <li>404 (NotFound) - If the project does not exist.</li>
   *         </ul>
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{id}")
  public Response getProjectById(@PathParam("id") String id) {
    ProjectDTO project = projectService.getProjectById(id);
    if (project != null) {
      return Response.ok(project).build();
    } else {
      return Response.status(Status.NOT_FOUND).build();
    }
  }

  /**
   * Retrieve sorted all projects that satisfy fields from searchForm.
   *
   * @param searchForm the search form
   * @param page the page
   * @param pageItems the page items
   * @param sortColumn the sort column
   * @param sortType the sort type
   * @return The list of projects
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/search/{page}/{pageItems}/{sortColumn}/{sortType}")
  public Response search(SearchForm searchForm, @PathParam("page") int page,
      @PathParam("pageItems") int pageItems, @PathParam("sortColumn") String sortColumn,
      @PathParam("sortType") String sortType) {
    SearchDTO searchDTO = SearchMapper.INSTANCE.toSearchDTO(searchForm);
    List<TenderDTO> tenderDTOList =
        projectService.search(searchDTO, page, pageItems, sortColumn, sortType);
    if (tenderDTOList != null) {
      PaginationDTO paginationDTO =
          new PaginationDTO(projectService.projectCount(searchDTO), tenderDTOList);
      return Response.ok(paginationDTO).build();
    } else {
      return Response.status(Status.NOT_FOUND).build();
    }

  }

  /**
   * Creates the.
   *
   * @param project the project to be created
   * @return the UUID of the created project
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response create(@Valid ProjectForm project) {
    Boolean uniqueName = false;
    if (project.getProjectName() != null && project.getObjectName() != null) {
      uniqueName = projectService.findIfNameUnique(project.getProjectName(),
          project.getObjectName().getId(), project.getId());
    }
    Set<ValidationError> errors = validation(project, uniqueName, null);
    if (!errors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    String id = projectService.createProject(ProjectMapper.INSTANCE.toProjectDTO(project));
    ProjectForm form = new ProjectForm();
    form.setId(id);
    return Response.ok(form).build();

  }

  /**
   * Deletes a project.
   *
   * @param id the UUID of the project to be deleted
   * @return the response
   */
  @DELETE
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/{id}")
  public Response deleteProject(@PathParam("id") String id) {
    Boolean projectHasSubmission = false;
    if (id != null) {
      projectHasSubmission = projectService.findIfProjectHasSubmission(id);
    }
    Set<ValidationError> errors = validation(null, null, projectHasSubmission);
    if (!errors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    projectService.deleteProject(id);
    return Response.ok().build();
  }

  /**
   * Gets the projects by object id.
   *
   * @param objectId the object id
   * @return the projects by object id
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/object/{objectId}")
  public Response getProjectsByObjectId(@PathParam("objectId") String objectId) {
    List<String> projectList = projectService.getProjectsByObjectId(objectId);
    return Response.ok(projectList).build();
  }

  /**
   * Gets the projects by objects IDs.
   *
   * @param objectsIDs the objects IDs
   * @return the projects by objects IDs
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/objects")
  public Response getProjectsByObjectsIDs(List<String> objectsIDs) {
    List<ProjectDTO> projectList = projectService.getProjectsByObjectsIDs(objectsIDs);
    return Response.ok(projectList).build();
  }

  /**
   * Gets all projects names.
   *
   * @return all projects names
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/allNames/{excludedProject}")
  public Response getAllProjectsNames(@PathParam("excludedProject") String excludedProject) {
    List<String> projectNamesList = projectService.getAllProjectsNames(excludedProject);
    return Response.ok(projectNamesList).build();
  }

  /**
   * Gets the all projects credit numbers.
   *
   * @return the all projects credit numbers
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/allCreditNumbers")
  public Response getAllProjectsCreditNos() {
    List<String> projectCreditNoList = projectService.getAllProjectsCreditNos();
    return Response.ok(projectCreditNoList).build();
  }

  /**
   * Gets the all project managers.
   *
   * @return the all project managers
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/allProjectManagers")
  public Response getAllProjectManagers() {
    List<String> projectManagerList = projectService.getAllProjectManagers();
    return Response.ok(projectManagerList).build();
  }

  /**
   * Gets all projects.
   *
   * @return all projects
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/all")
  public Response getAllProjects() {
    List<ProjectDTO> projectsList = projectService.getAllProjects();
    return Response.ok(projectsList).build();
  }

  /**
   * Update a project.
   *
   * @param project the project
   * @return 200 (OK) - If the project is updated.
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/update")
  public Response update(@Valid ProjectForm project) {
    Boolean uniqueName = false;
    if (project.getProjectName() != null && project.getObjectName() != null) {
      uniqueName = projectService.findIfNameUnique(project.getProjectName(),
          project.getObjectName().getId(), project.getId());
    }
    Set<ValidationError> errors = validation(project, uniqueName, null);
    if (!errors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    projectService.updateProject(ProjectMapper.INSTANCE.toProjectDTO(project));
    return Response.ok().build();
  }

  /**
   * Show if at least one of the submissions status of the specific project is offer_opening_closed
   * or greater.
   * 
   * @param projectId The id of the project
   * @return True if the status is (offer_opening_closed) or greater and false if it's not.
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/checkStatus/{projectId}")
  public Response hasStatusAfterOfferOpeningClosed(@PathParam("projectId") String projectId) {
    boolean hasStatusAfterOfferOpeningClosed =
        projectService.hasStatusAfterOfferOpeningClosed(projectId);
    return Response.ok(hasStatusAfterOfferOpeningClosed).build();
  }

  /**
   * Validation.
   *
   * @param project the project
   * @param uniqueName the unique name
   * @param projectHasSubmission the project has submission
   * @return the sets the
   */
  private Set<ValidationError> validation(ProjectForm project, Boolean uniqueName,
      Boolean projectHasSubmission) {
    Set<ValidationError> errors = new HashSet<>();
    if (project != null) {
      if (project.getObjectName() == null || StringUtils.isBlank(project.getProjectName())
        || project.getProcedure() == null || project.getDepartment() == null) {
        errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
          ValidationMessages.MANDATORY_ERROR_MESSAGE));
        if (StringUtils.isBlank(project.getProjectName())) {
          errors.add(new ValidationError(PROJECT_NAME,
            ValidationMessages.MANDATORY_ERROR_MESSAGE));
        }
        if (project.getObjectName() == null) {
          errors.add(new ValidationError("objectName",
            ValidationMessages.MANDATORY_ERROR_MESSAGE));
        }
        if (project.getProcedure() == null) {
          errors.add(new ValidationError("procedure",
            ValidationMessages.MANDATORY_ERROR_MESSAGE));
        }
        if (project.getDepartment() == null) {
          errors.add(new ValidationError("department",
            ValidationMessages.MANDATORY_ERROR_MESSAGE));
        }
        if (project.getDepartment() == null) {
          errors.add(new ValidationError("department",
            ValidationMessages.MANDATORY_ERROR_MESSAGE));
          errors.add(new ValidationError("directorate",
            ValidationMessages.MANDATORY_ERROR_MESSAGE));
        }
      }
      if (project.getProjectNumber() != null && project.getProjectNumber().length() > 20) {
        errors.add(new ValidationError("projectNumberErrorField",
            "project_max_size_projectNumber_error_message"));
        errors.add(
            new ValidationError("projectNumber", "project_max_size_projectNumber_error_message"));
      }
      if (project.getProjectName() != null && project.getProjectName().length() >= 100) {
        errors.add(new ValidationError("projectNameErrorField",
            "project_max_size_projectName_error_message"));
        errors
            .add(new ValidationError(PROJECT_NAME, "project_max_size_projectName_error_message"));
      }
      if (project.getPmDepartmentName() != null && project.getPmDepartmentName().length() >= 100) {
        errors.add(new ValidationError("pmDepartmentNameErrorField",
            "project_max_size_pmDepartmentName_message"));
        errors.add(
            new ValidationError("pmDepartmentName", ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if (StringUtils.isBlank(project.getPmDepartmentName())
          || project.getPmDepartmentName().length() < 3) {
        errors.add(new ValidationError("pmDepartmentNameErrorField",
            "project_min_size_pmDepartmentName_message"));
        errors.add(
            new ValidationError("pmDepartmentName", ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if (uniqueName) {
        errors.add(new ValidationError("uniqueNameErrorField",
            ValidationMessages.UNIQUE_NAME_ERROR_MESSAGE));
        errors
            .add(new ValidationError(PROJECT_NAME, ValidationMessages.UNIQUE_NAME_ERROR_MESSAGE));
        errors.add(new ValidationError("objectName", ValidationMessages.UNIQUE_NAME_ERROR_MESSAGE));
      }
      if (project.getNotes() != null && project.getNotes().length() > 100) {
        errors.add(new ValidationError("projectNotesErrorField", "project_maximum_error_message"));
        errors.add(new ValidationError("projectNotes", "project_maximum_error_message"));
      }
    }
    if (projectHasSubmission != null && projectHasSubmission) {
      errors.add(
          new ValidationError("projectHasSubmissionErrorField", "submission_exists_error_message"));
    }

    return errors;

  }

}
