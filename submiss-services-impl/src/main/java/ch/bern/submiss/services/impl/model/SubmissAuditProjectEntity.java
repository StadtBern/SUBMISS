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

package ch.bern.submiss.services.impl.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * The Class SubmissAuditProjectEntity.
 */
@Entity
@Table(name = "VW_AUDIT_PROJECT")
public class SubmissAuditProjectEntity extends SubmissAuditEntity {

  /** The object name. */
  @Column(name = "objectName")
  private String objectName;

  /** The project name. */
  @Column(name = "projectName")
  private String projectName;

  /** The work type. */
  @Column(name = "workType")
  private String workType;

  /** The reason. */
  @Column(name = "reason")
  private String reason;

  /** The reference id. */
  @Column(name = "reference_id")
  private String referenceId;



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
   * Gets the reason.
   *
   * @return the reason
   */
  public String getReason() {
    return reason;
  }

  /**
   * Sets the reason.
   *
   * @param reason the new reason
   */
  public void setReason(String reason) {
    this.reason = reason;
  }

  /**
   * Gets the reference id.
   *
   * @return the reference id
   */
  public String getReferenceId() {
    return referenceId;
  }

  /**
   * Sets the reference id.
   *
   * @param referenceId the new reference id
   */
  public void setReferenceId(String referenceId) {
    this.referenceId = referenceId;
  }



}
