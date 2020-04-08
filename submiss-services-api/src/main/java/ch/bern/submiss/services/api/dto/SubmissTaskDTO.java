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

package ch.bern.submiss.services.api.dto;

import ch.bern.submiss.services.api.constants.TaskTypes;
import ch.bern.submiss.services.api.util.View;
import com.fasterxml.jackson.annotation.JsonView;
import java.sql.Timestamp;
import java.util.Date;

/**
 * The Class SubmissionCancelDTO.
 */
public class SubmissTaskDTO extends AbstractDTO {

  /**
   * The submission.
   */
  @JsonView(View.Public.class)
  private SubmissionDTO submission;

  /**
   * The company.
   */
  @JsonView(View.Public.class)
  private CompanyDTO company;

  /**
   * The description.
   */
  @JsonView(View.Public.class)
  private TaskTypes description;

  /**
   * The created by.
   */
  @JsonView(View.Public.class)
  private String createdBy;

  /**
   * The created on.
   */
  @JsonView(View.Public.class)
  private Date createdOn;

  /**
   * The updated on.
   */
  @JsonView(View.Public.class)
  private Timestamp updatedOn;

  /**
   * The user to edit.
   */
  @JsonView(View.Public.class)
  private String userToEdit;

  /**
   * The user to edit DTO.
   */
  @JsonView(View.Public.class)
  private SubmissUserDTO userToEditDTO;

  /**
   * The user assigned.
   */
  @JsonView(View.Public.class)
  private String userAssigned;

  /**
   * The user assigned.
   */
  @JsonView(View.Public.class)
  private String objectName;

  /**
   * The user assigned.
   */
  @JsonView(View.Public.class)
  private String projectName;

  /**
   * The user assigned.
   */
  @JsonView(View.Public.class)
  private String workType;

  /**
   * The user assigned.
   */
  @JsonView(View.Public.class)
  private String companyName;

  /**
   * The user assigned.
   */
  @JsonView(View.Public.class)
  private String userAutoAssigned;

  /**
   * The type.
   */
  @JsonView(View.Public.class)
  private Integer type;

  /**
   * The first name.
   */
  private String firstName;

  /**
   * The last name.
   */
  private String lastName;

  /**
   * The email.
   */
  private String email;

  /**
   * The main department str.
   */
  private String mainDepartmentStr;

  /**
   * The directorates str.
   */
  private String directoratesStr;

  /**
   * The tenant name.
   */
  private String tenantName;

  /**
   * The role.
   */
  private String role;

  /**
   * The submit date.
   */
  private Date submitDate;

  /**
   * Gets the submission.
   *
   * @return the submission
   */
  public SubmissionDTO getSubmission() {
    return submission;
  }

  /**
   * Sets the submission.
   *
   * @param submission the new submission
   */
  public void setSubmission(SubmissionDTO submission) {
    this.submission = submission;
  }

  /**
   * Gets the created by.
   *
   * @return the created by
   */
  public String getCreatedBy() {
    return createdBy;
  }

  /**
   * Sets the created by.
   *
   * @param createdBy the new created by
   */
  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  /**
   * Gets the created on.
   *
   * @return the created on
   */
  public Date getCreatedOn() {
    return createdOn;
  }

  /**
   * Sets the created on.
   *
   * @param createdOn the new created on
   */
  public void setCreatedOn(Date createdOn) {
    this.createdOn = createdOn;
  }

  /**
   * Gets the company.
   *
   * @return the company
   */
  public CompanyDTO getCompany() {
    return company;
  }

  /**
   * Sets the company.
   *
   * @param company the new company
   */
  public void setCompany(CompanyDTO company) {
    this.company = company;
  }

  /**
   * Gets the description.
   *
   * @return the description
   */
  public TaskTypes getDescription() {
    return description;
  }

  /**
   * Sets the description.
   *
   * @param description the new description
   */
  public void setDescription(TaskTypes description) {
    this.description = description;
  }

  /**
   * Gets the user to edit.
   *
   * @return the user to edit
   */
  public String getUserToEdit() {
    return userToEdit;
  }

  /**
   * Sets the user to edit.
   *
   * @param userToEdit the new user to edit
   */
  public void setUserToEdit(String userToEdit) {
    this.userToEdit = userToEdit;
  }

  /**
   * Gets the user assigned.
   *
   * @return the user assigned
   */
  public String getUserAssigned() {
    return userAssigned;
  }

  /**
   * Sets the user assigned.
   *
   * @param userAssigned the new user assigned
   */
  public void setUserAssigned(String userAssigned) {
    this.userAssigned = userAssigned;
  }

  /**
   * Gets the object name.
   *
   * @return the object name
   */
  public String getObjectName() {
    return objectName;
  }

  /**
   * Sets the object name.
   *
   * @param objectName the new object name
   */
  public void setObjectName(String objectName) {
    this.objectName = objectName;
  }

  /**
   * Gets the project name.
   *
   * @return the project name
   */
  public String getProjectName() {
    return projectName;
  }

  /**
   * Sets the project name.
   *
   * @param projectName the new project name
   */
  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  /**
   * Gets the work type.
   *
   * @return the work type
   */
  public String getWorkType() {
    return workType;
  }

  /**
   * Sets the work type.
   *
   * @param workType the new work type
   */
  public void setWorkType(String workType) {
    this.workType = workType;
  }

  /**
   * Gets the company name.
   *
   * @return the company name
   */
  public String getCompanyName() {
    return companyName;
  }

  /**
   * Sets the company name.
   *
   * @param companyName the new company name
   */
  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  /**
   * Gets the user auto assigned.
   *
   * @return the user auto assigned
   */
  public String getUserAutoAssigned() {
    return userAutoAssigned;
  }

  /**
   * Sets the user auto assigned.
   *
   * @param userAutoAssigned the new user auto assigned
   */
  public void setUserAutoAssigned(String userAutoAssigned) {
    this.userAutoAssigned = userAutoAssigned;
  }

  /**
   * Gets the type.
   *
   * @return the type
   */
  public Integer getType() {
    return type;
  }

  /**
   * Sets the type.
   *
   * @param type the new type
   */
  public void setType(Integer type) {
    this.type = type;
  }

  /**
   * Gets the user to edit DTO.
   *
   * @return the user to edit DTO
   */
  public SubmissUserDTO getUserToEditDTO() {
    return userToEditDTO;
  }

  /**
   * Sets the user to edit DTO.
   *
   * @param userToEditDTO the new user to edit DTO
   */
  public void setUserToEditDTO(SubmissUserDTO userToEditDTO) {
    this.userToEditDTO = userToEditDTO;
  }


  /**
   * Gets the first name.
   *
   * @return the first name
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * Sets the first name.
   *
   * @param firstName the new first name
   */
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   * Gets the last name.
   *
   * @return the last name
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * Sets the last name.
   *
   * @param lastName the new last name
   */
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  /**
   * Gets the email.
   *
   * @return the email
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets the email.
   *
   * @param email the new email
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Gets the main department str.
   *
   * @return the main department str
   */
  public String getMainDepartmentStr() {
    return mainDepartmentStr;
  }

  /**
   * Sets the main department str.
   *
   * @param mainDepartmentStr the new main department str
   */
  public void setMainDepartmentStr(String mainDepartmentStr) {
    this.mainDepartmentStr = mainDepartmentStr;
  }

  /**
   * Gets the directorates str.
   *
   * @return the directorates str
   */
  public String getDirectoratesStr() {
    return directoratesStr;
  }

  /**
   * Sets the directorates str.
   *
   * @param directoratesStr the new directorates str
   */
  public void setDirectoratesStr(String directoratesStr) {
    this.directoratesStr = directoratesStr;
  }

  /**
   * Gets the tenant name.
   *
   * @return the tenant name
   */
  public String getTenantName() {
    return tenantName;
  }

  /**
   * Sets the tenant name.
   *
   * @param tenantName the new tenant name
   */
  public void setTenantName(String tenantName) {
    this.tenantName = tenantName;
  }

  /**
   * Gets the role.
   *
   * @return the role
   */
  public String getRole() {
    return role;
  }

  /**
   * Sets the role.
   *
   * @param role the new role
   */
  public void setRole(String role) {
    this.role = role;
  }

  /**
   * Gets the submit date.
   *
   * @return the submit date
   */
  public Date getSubmitDate() {
    return submitDate;
  }

  /**
   * Sets the submit date.
   *
   * @param submitDate the new submit date
   */
  public void setSubmitDate(Date submitDate) {
    this.submitDate = submitDate;
  }

  /**
   * Gets the updated on.
   *
   * @return the updatedOn
   */
  public Timestamp getUpdatedOn() {
    return updatedOn;
  }

  /**
   * Sets the updated on.
   *
   * @param updatedOn the updatedOn
   */
  public void setUpdatedOn(Timestamp updatedOn) {
    this.updatedOn = updatedOn;
  }

  @Override
  public String toString() {
    return "SubmissTaskDTO [id=" + super.getId() + ", description=" + description + ", createdBy="
      + createdBy
      + ", createdOn=" + createdOn + ", userToEdit=" + userToEdit + ", userAssigned="
      + userAssigned + ", objectName=" + objectName + ", projectName=" + projectName
      + ", workType=" + workType + ", companyName=" + companyName + ", userAutoAssigned="
      + userAutoAssigned + ", type=" + type + ", firstName=" + firstName + ", lastName="
      + lastName + ", email=" + email + ", mainDepartmentStr=" + mainDepartmentStr
      + ", directoratesStr=" + directoratesStr + ", tenantName=" + tenantName + ", role=" + role
      + "]";
  }
}
