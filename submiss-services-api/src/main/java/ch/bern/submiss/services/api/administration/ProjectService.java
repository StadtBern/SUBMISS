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

import java.util.List;
import ch.bern.submiss.services.api.dto.ProjectDTO;
import ch.bern.submiss.services.api.dto.SearchDTO;
import ch.bern.submiss.services.api.dto.TenderDTO;

/**
 * ProjectService is the interface for all classes that implement Project related behaviour.
 */
public interface ProjectService {

  /**
   * Finds a project based on it's UUID.
   *
   * @param id the project UUID
   * @return the project
   */
  ProjectDTO getProjectById(String id);

  /**
   * Creates a project.
   *
   * @param project the project to be created
   * @return the UUID of the created project
   */
  String createProject(ProjectDTO project);

  /**
   * Deletes a project.
   *
   * @param id the UUID of the project to be deleted
   */
  void deleteProject(String id);

  /**
   * Finds all project names that belong to an object.
   *
   * @param objectId the uuid of the object
   * @return a list of the project names
   */
  List<String> getProjectsByObjectId(String objectId);

  /**
   * Returns all projects.
   *
   * @return a list of Projects
   */
  List<ProjectDTO> getAllProjects();

  /**
   * Returns all projects names.
   *
   * @param excludedProject the excluded project
   * @return a list of ProjectNames
   */
  List<String> getAllProjectsNames(String excludedProject);

  /**
   * Updates a project.
   *
   * @param project the project with the updated values
   */
  void updateProject(ProjectDTO project);

  /**
   * Retrieve sorted all projects that satisfy fields from searchForm.
   *
   * @param searchDTO the search DTO
   * @param page the page
   * @param pageItems the page items
   * @param sortColumn the sort column
   * @param sortType the sort type
   * @return The list of projects
   */
  List<TenderDTO> search(SearchDTO searchDTO, int page, int pageItems, String sortColumn,
      String sortType);

  /**
   * Find the count of projects.
   *
   * @param searchDTO the search DTO
   * @return The count of projects
   */
  long projectCount(SearchDTO searchDTO);

  /**
   * Find if name unique.
   *
   * @param projectName the project name
   * @param objectNameId the object name id
   * @param projectId the project id
   * @return the boolean
   */
  Boolean findIfNameUnique(String projectName, String objectNameId, String projectId);

  /**
   * Find if project has submission.
   *
   * @param id the id
   * @return the boolean
   */
  Boolean findIfProjectHasSubmission(String id);

  /**
   * Show if at least one of the submissions status of the specific project is offer_opening_closed
   * or greater.
   * 
   * @param projectId The id of the project
   * @return True if the status is (offer_opening_closed) or greater and false if it's not.
   */
  boolean hasStatusAfterOfferOpeningClosed(String projectId);

  /**
   * Gets the projects by objects IDs.
   *
   * @param objectsIDs the objects IDs
   * @return the projects by objects IDs
   */
  List<ProjectDTO> getProjectsByObjectsIDs(List<String> objectsIDs);

  /**
   * Gets the all projects credit numbers.
   *
   * @return the all projects credit numbers
   */
  List<String> getAllProjectsCreditNos();

  /**
   * Gets the all project managers.
   *
   * @return the all project managers
   */
  List<String> getAllProjectManagers();

}
