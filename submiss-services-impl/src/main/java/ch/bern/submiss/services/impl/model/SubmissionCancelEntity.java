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

import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * The Class SubmissionCancelEntity.
 */
@Entity
@Table(name = "SUB_TENDER_CANCEL")
public class SubmissionCancelEntity extends AbstractEntity {

  /**
   * The submission.
   */
  @ManyToOne
  @JoinColumn(name = "FK_TENDER")
  private SubmissionEntity submission;

  /**
   * The available date.
   */
  @Column(name = "AVAILABLE_DATE")
  private Date availableDate;

  /**
   * The freeze close submission. Shows if the automatic closure of the submission needs to be
   * freezed or not.
   */
  @Column(name = "FREEZE_CLOSE_TENDER")
  private Boolean freezeCloseSubmission;

  /**
   * The object name read.
   */
  @Column(name = "OBJECT_NAME_READ")
  private Boolean objectNameRead;

  /**
   * The project name read.
   */
  @Column(name = "PROJECT_NAME_READ")
  private Boolean projectNameRead;

  /**
   * The working class read.
   */
  @Column(name = "WORKING_CLASS_READ")
  private Boolean workingClassRead;

  /**
   * The description read.
   */
  @Column(name = "DESCRIPTION_READ")
  private Boolean descriptionRead;

  /**
   * The reason.
   */
  @Column(name = "REASON")
  private String reason;

  /**
   * The created by.
   */
  @Column(name = "CREATED_BY")
  private String createdBy;

  /**
   * The created on.
   */
  @CreationTimestamp
  @Column(name = "CREATED_ON")
  private Timestamp createdOn;

  /**
   * The updated by.
   */
  @Column(name = "UPDATED_BY")
  private String updatedBy;

  /**
   * The updated on.
   */
  @UpdateTimestamp
  @Column(name = "UPDATED_ON", insertable = false)
  private Timestamp updatedOn;

  /**
   * The cancelled by.
   */
  @Column(name = "CANCELLED_BY")
  private String cancelledBy;

  /**
   * The cancelled on.
   */
  @Column(name = "CANCELLED_ON")
  private Date cancelledOn;

  /**
   * The work types.
   */
  @ManyToMany
  @JoinTable(name = "SUB_TENDER_CANCEL_REASON", joinColumns = {
    @JoinColumn(name = "FK_TENDER_CANCEL")},
    inverseJoinColumns = {@JoinColumn(name = "FK_CANCEL_REASON")})
  private Set<MasterListValueEntity> workTypes;

  /**
   * The close count down start. This field has a value if the freeze flag was set to true, so the
   * automatic closure of the submission was freezed and then was set to false. At this moment a
   * timer needs to be initiated that counts 40 days until the automatic closure of the submission.
   */
  @Column(name = "CLOSE_COUNTDOWN_START")
  private Date closeCountdownStart;

  /**
   * Gets the submission.
   *
   * @return the submission
   */
  public SubmissionEntity getSubmission() {
    return submission;
  }

  /**
   * Sets the submission.
   *
   * @param submission the new submission
   */
  public void setSubmission(SubmissionEntity submission) {
    this.submission = submission;
  }

  /**
   * Gets the available date.
   *
   * @return the available date
   */
  public Date getAvailableDate() {
    return availableDate;
  }

  /**
   * Sets the available date.
   *
   * @param availableDate the new available date
   */
  public void setAvailableDate(Date availableDate) {
    this.availableDate = availableDate;
  }

  /**
   * Gets the freeze close submission.
   *
   * @return the freeze close submission
   */
  public Boolean getFreezeCloseSubmission() {
    return freezeCloseSubmission;
  }

  /**
   * Sets the freeze close submission.
   *
   * @param freezeCloseSubmission the new freeze close submission
   */
  public void setFreezeCloseSubmission(Boolean freezeCloseSubmission) {
    this.freezeCloseSubmission = freezeCloseSubmission;
  }

  /**
   * Gets the object name read.
   *
   * @return the object name read
   */
  public Boolean getObjectNameRead() {
    return objectNameRead;
  }

  /**
   * Sets the object name read.
   *
   * @param objectNameRead the new object name read
   */
  public void setObjectNameRead(Boolean objectNameRead) {
    this.objectNameRead = objectNameRead;
  }

  /**
   * Gets the project name read.
   *
   * @return the project name read
   */
  public Boolean getProjectNameRead() {
    return projectNameRead;
  }

  /**
   * Sets the project name read.
   *
   * @param projectNameRead the new project name read
   */
  public void setProjectNameRead(Boolean projectNameRead) {
    this.projectNameRead = projectNameRead;
  }

  /**
   * Gets the working class read.
   *
   * @return the working class read
   */
  public Boolean getWorkingClassRead() {
    return workingClassRead;
  }

  /**
   * Sets the working class read.
   *
   * @param workingClassRead the new working class read
   */
  public void setWorkingClassRead(Boolean workingClassRead) {
    this.workingClassRead = workingClassRead;
  }

  /**
   * Gets the description read.
   *
   * @return the description read
   */
  public Boolean getDescriptionRead() {
    return descriptionRead;
  }

  /**
   * Sets the description read.
   *
   * @param descriptionRead the new description read
   */
  public void setDescriptionRead(Boolean descriptionRead) {
    this.descriptionRead = descriptionRead;
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
  public Timestamp getCreatedOn() {
    return createdOn;
  }

  /**
   * Sets the created on.
   *
   * @param createdOn the new created on
   */
  public void setCreatedOn(Timestamp createdOn) {
    this.createdOn = createdOn;
  }

  /**
   * Gets the updated by.
   *
   * @return the updated by
   */
  public String getUpdatedBy() {
    return updatedBy;
  }

  /**
   * Sets the updated by.
   *
   * @param updatedBy the new updated by
   */
  public void setUpdatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
  }

  /**
   * Gets the updated on.
   *
   * @return the updated on
   */
  public Timestamp getUpdatedOn() {
    return updatedOn;
  }

  /**
   * Sets the updated on.
   *
   * @param updatedOn the new updated on
   */
  public void setUpdatedOn(Timestamp updatedOn) {
    this.updatedOn = updatedOn;
  }

  /**
   * Gets the cancelled by.
   *
   * @return the cancelled by
   */
  public String getCancelledBy() {
    return cancelledBy;
  }

  /**
   * Sets the cancelled by.
   *
   * @param cancelledBy the new cancelled by
   */
  public void setCancelledBy(String cancelledBy) {
    this.cancelledBy = cancelledBy;
  }

  /**
   * Gets the cancelled on.
   *
   * @return the cancelled on
   */
  public Date getCancelledOn() {
    return cancelledOn;
  }

  /**
   * Sets the cancelled on.
   *
   * @param cancelledOn the new cancelled on
   */
  public void setCancelledOn(Date cancelledOn) {
    this.cancelledOn = cancelledOn;
  }

  /**
   * Gets the work types.
   *
   * @return the work types
   */
  public Set<MasterListValueEntity> getWorkTypes() {
    return workTypes;
  }

  /**
   * Sets the work types.
   *
   * @param workTypes the new work types
   */
  public void setWorkTypes(Set<MasterListValueEntity> workTypes) {
    this.workTypes = workTypes;
  }

  /**
   * Gets the close count down start.
   *
   * @return the close count down start
   */
  public Date getCloseCountdownStart() {
    return closeCountdownStart;
  }

  /**
   * Sets the close count down start.
   *
   * @param closeCountdownStart the new close count down start
   */
  public void setCloseCountdownStart(Date closeCountdownStart) {
    this.closeCountdownStart = closeCountdownStart;
  }
}
