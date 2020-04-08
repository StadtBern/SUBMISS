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

import ch.bern.submiss.services.api.constants.Process;
import java.util.Date;

/**
 * The Class TenderDTO.
 */
public class TenderDTO extends AbstractStammdatenDTO {

  /**
   * The object name.
   */
  private String objectName;

  /**
   * The project id.
   */
  private String projectId;

  /**
   * The project version.
   */
  private Long projectVersion;

  /**
   * The project name.
   */
  private String projectName;

  /**
   * The description.
   */
  private String description;

  /**
   * The work type.
   */
  private String workType;

  /**
   * The proccess.
   */
  private Process proccess;

  /**
   * The proccess type.
   */
  private String proccessType;

  /**
   * The submission deadline.
   */
  private Date submissionDeadline;

  /**
   * The man dep.
   */
  private String manDep;

  /**
   * The project manager of dep.
   */
  private String projectManagerOfDep;

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
   * Gets the project id.
   *
   * @return the project id
   */
  public String getProjectId() {
    return projectId;
  }

  /**
   * Sets the project id.
   *
   * @param projectId the new project id
   */
  public void setProjectId(String projectId) {
    this.projectId = projectId;
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
   * Gets the description.
   *
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description.
   *
   * @param description the new description
   */
  public void setDescription(String description) {
    this.description = description;
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
   * Gets the proccess.
   *
   * @return the proccess
   */
  public Process getProccess() {
    return proccess;
  }

  /**
   * Sets the proccess.
   *
   * @param proccess the new proccess
   */
  public void setProccess(Process proccess) {
    this.proccess = proccess;
  }

  /**
   * Gets the proccess type.
   *
   * @return the proccess type
   */
  public String getProccessType() {
    return proccessType;
  }

  /**
   * Sets the proccess type.
   *
   * @param proccessType the new proccess type
   */
  public void setProccessType(String proccessType) {
    this.proccessType = proccessType;
  }

  /**
   * Gets the submission deadline.
   *
   * @return the submission deadline
   */
  public Date getSubmissionDeadline() {
    return submissionDeadline;
  }

  /**
   * Sets the submission deadline.
   *
   * @param date the new submission deadline
   */
  public void setSubmissionDeadline(Date date) {
    this.submissionDeadline = date;
  }

  /**
   * Gets the man dep.
   *
   * @return the man dep
   */
  public String getManDep() {
    return manDep;
  }

  /**
   * Sets the man dep.
   *
   * @param manDep the new man dep
   */
  public void setManDep(String manDep) {
    this.manDep = manDep;
  }

  /**
   * Gets the project manager of dep.
   *
   * @return the project manager of dep
   */
  public String getProjectManagerOfDep() {
    return projectManagerOfDep;
  }

  /**
   * Sets the project manager of dep.
   *
   * @param projectManagerOfDep the new project manager of dep
   */
  public void setProjectManagerOfDep(String projectManagerOfDep) {
    this.projectManagerOfDep = projectManagerOfDep;
  }

  /**
   * Gets the project version.
   *
   * @return the projectVersion
   */
  public Long getProjectVersion() {
    return projectVersion;
  }

  /**
   * Sets the project version.
   *
   * @param projectVersion the projectVersion
   */
  public void setProjectVersion(Long projectVersion) {
    this.projectVersion = projectVersion;
  }

  @Override
  public String toString() {
    return "TenderDTO [objectName=" + objectName + ", projectId=" + projectId + ", projectName="
      + projectName + ", description=" + description + ", workType=" + workType + ", proccess="
      + proccess + ", proccessType=" + proccessType + ", submissionDeadline=" + submissionDeadline
      + ", manDep=" + manDep + ", projectManagerOfDep=" + projectManagerOfDep + "]";
  }
}
