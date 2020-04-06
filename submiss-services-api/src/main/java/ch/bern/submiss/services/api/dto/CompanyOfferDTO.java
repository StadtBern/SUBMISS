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

import java.util.Date;
import java.util.Set;
import ch.bern.submiss.services.api.constants.Process;

/**
 * The Class CompanyOfferDTO.
 */
public class CompanyOfferDTO {

  /** The submission id. */
  private String submissionId;

  /** The project id. */
  private String projectId;

  /** The object name. */
  private String objectName;

  /** The project name. */
  private String projectName;

  /** The work type. */
  private String workType;

  /** The ammount. */
  private Double ammount;

  /** The rank. */
  private Integer rank;

  /** The is awarded. */
  private Boolean isAwarded;

  /** The is excluded from process. */
  private Boolean isExcludedFromProcess;

  /** The deadline 2. */
  private Date deadline2;

  /** The process. */
  private Process process;

  /** The process type. */
  private String processType;

  /** The joint ventures. */
  private Set<CompanyDTO> jointVentures;

  /** The subcontractors. */
  private Set<CompanyDTO> subcontractors;

  /** The department name. */
  private String departmentName;

  /** The pm department name. */
  private String pmDepartmentName;

  /** The from migration. */
  private Boolean fromMigration;

  /** The procedure name. */
  private String procedureName;

  /** The is leading. */
  private boolean isLeading;

  /** The is view permitted. */
  private Boolean isViewPermitted;

  /** The migrated submission. */
  private String migratedSubmission;

  /**
   * Gets the submission id.
   *
   * @return the submission id
   */
  public String getSubmissionId() {
    return submissionId;
  }

  /**
   * Sets the submission id.
   *
   * @param submissionId the new submission id
   */
  public void setSubmissionId(String submissionId) {
    this.submissionId = submissionId;
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
   * Gets the ammount.
   *
   * @return the ammount
   */
  public Double getAmmount() {
    return ammount;
  }

  /**
   * Sets the ammount.
   *
   * @param ammount the new ammount
   */
  public void setAmmount(Double ammount) {
    this.ammount = ammount;
  }

  /**
   * Gets the rank.
   *
   * @return the rank
   */
  public Integer getRank() {
    return rank;
  }

  /**
   * Sets the rank.
   *
   * @param rank the new rank
   */
  public void setRank(Integer rank) {
    this.rank = rank;
  }

  /**
   * Gets the checks if is awarded.
   *
   * @return the checks if is awarded
   */
  public Boolean getIsAwarded() {
    return isAwarded;
  }

  /**
   * Sets the checks if is awarded.
   *
   * @param isAwarded the new checks if is awarded
   */
  public void setIsAwarded(Boolean isAwarded) {
    this.isAwarded = isAwarded;
  }

  /**
   * Gets the checks if is excluded from process.
   *
   * @return the checks if is excluded from process
   */
  public Boolean getIsExcludedFromProcess() {
    return isExcludedFromProcess;
  }

  /**
   * Sets the checks if is excluded from process.
   *
   * @param isExcludedFromProcess the new checks if is excluded from process
   */
  public void setIsExcludedFromProcess(Boolean isExcludedFromProcess) {
    this.isExcludedFromProcess = isExcludedFromProcess;
  }

  /**
   * Gets the deadline 2.
   *
   * @return the deadline 2
   */
  public Date getDeadline2() {
    return deadline2;
  }

  /**
   * Sets the deadline 2.
   *
   * @param deadline2 the new deadline 2
   */
  public void setDeadline2(Date deadline2) {
    this.deadline2 = deadline2;
  }

  /**
   * Gets the process.
   *
   * @return the process
   */
  public Process getProcess() {
    return process;
  }

  /**
   * Sets the process.
   *
   * @param process the new process
   */
  public void setProcess(Process process) {
    this.process = process;
  }

  /**
   * Gets the process type.
   *
   * @return the process type
   */
  public String getProcessType() {
    return processType;
  }

  /**
   * Sets the process type.
   *
   * @param processType the new process type
   */
  public void setProcessType(String processType) {
    this.processType = processType;
  }

  /**
   * Gets the joint ventures.
   *
   * @return the joint ventures
   */
  public Set<CompanyDTO> getJointVentures() {
    return jointVentures;
  }

  /**
   * Sets the joint ventures.
   *
   * @param jointVentures the new joint ventures
   */
  public void setJointVentures(Set<CompanyDTO> jointVentures) {
    this.jointVentures = jointVentures;
  }

  /**
   * Gets the subcontractors.
   *
   * @return the subcontractors
   */
  public Set<CompanyDTO> getSubcontractors() {
    return subcontractors;
  }

  /**
   * Sets the subcontractors.
   *
   * @param subcontractors the new subcontractors
   */
  public void setSubcontractors(Set<CompanyDTO> subcontractors) {
    this.subcontractors = subcontractors;
  }

  /**
   * Gets the department name.
   *
   * @return the department name
   */
  public String getDepartmentName() {
    return departmentName;
  }

  /**
   * Sets the department name.
   *
   * @param departmentName the new department name
   */
  public void setDepartmentName(String departmentName) {
    this.departmentName = departmentName;
  }

  /**
   * Gets the pm department name.
   *
   * @return the pm department name
   */
  public String getPmDepartmentName() {
    return pmDepartmentName;
  }

  /**
   * Sets the pm department name.
   *
   * @param pmDepartmentName the new pm department name
   */
  public void setPmDepartmentName(String pmDepartmentName) {
    this.pmDepartmentName = pmDepartmentName;
  }

  /**
   * Gets the from migration.
   *
   * @return the from migration
   */
  public Boolean getFromMigration() {
    return fromMigration;
  }

  /**
   * Sets the from migration.
   *
   * @param fromMigration the new from migration
   */
  public void setFromMigration(Boolean fromMigration) {
    this.fromMigration = fromMigration;
  }

  /**
   * Gets the procedure name.
   *
   * @return the procedure name
   */
  public String getProcedureName() {
    return procedureName;
  }

  /**
   * Sets the procedure name.
   *
   * @param procedureName the new procedure name
   */
  public void setProcedureName(String procedureName) {
    this.procedureName = procedureName;
  }

  /**
   * Checks if is leading.
   *
   * @return true, if is leading
   */
  public boolean isLeading() {
    return isLeading;
  }

  /**
   * Sets the leading.
   *
   * @param isLeading the new leading
   */
  public void setLeading(boolean isLeading) {
    this.isLeading = isLeading;
  }

  /**
   * Gets the checks if is view permitted.
   *
   * @return the checks if is view permitted
   */
  public Boolean getIsViewPermitted() {
    return isViewPermitted;
  }

  /**
   * Sets the checks if is view permitted.
   *
   * @param isViewPermitted the new checks if is view permitted
   */
  public void setIsViewPermitted(Boolean isViewPermitted) {
    this.isViewPermitted = isViewPermitted;
  }

  /**
   * Gets the migrated submission.
   *
   * @return the migrated submission
   */
  public String getMigratedSubmission() {
    return migratedSubmission;
  }

  /**
   * Sets the migrated submission.
   *
   * @param migratedSubmission the new migrated submission
   */
  public void setMigratedSubmission(String migratedSubmission) {
    this.migratedSubmission = migratedSubmission;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "CompanyOfferDTO [submissionId=" + submissionId + ", projectId=" + projectId
        + ", objectName=" + objectName + ", projectName=" + projectName + ", workType=" + workType
        + ", ammount=" + ammount + ", rank=" + rank + ", isAwarded=" + isAwarded
        + ", isExcludedFromProcess=" + isExcludedFromProcess + ", deadline2=" + deadline2
        + ", process=" + process + ", processType=" + processType + ", departmentName="
        + departmentName + ", pmDepartmentName=" + pmDepartmentName + ", fromMigration="
        + fromMigration + ", procedureName=" + procedureName + ", isLeading=" + isLeading + "]";
  }

}
