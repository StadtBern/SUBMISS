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

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * The Class LegalHearingTerminateEntity.
 */
@Entity
@Table(name = "SUB_LEGAL_HEARING")
public class LegalHearingTerminateEntity {

  /** The id. */
  @Id
  @GeneratedValue(generator = "uuid1")
  @GenericGenerator(name = "uuid1", strategy = "uuid2")
  private String id;
  
  /** The submission. */
  @ManyToOne
  @JoinColumn(name = "FK_TENDER")
  private SubmissionEntity submission;
  
  /** The deadline. */
  @Column(name = "HEARING_DEADLINE")
  private Date deadline;
  
  /** The reason. */
  @Column(name = "CANCEL_REASON")
  private String reason;
  
  /** The termination reason. */
  @ManyToMany
  @JoinTable(name = "SUB_LEGAL_HEARING_CANCEL_REASON", joinColumns = {@JoinColumn(name = "FK_LEGAL_HEARING")},
      inverseJoinColumns = {@JoinColumn(name = "FK_HEARING_CANCEL_REASON")})
  private Set<MasterListValueEntity> terminationReason;

  /** The created by. */
  @Column(name = "CREATED_BY")
  private String createdBy;
  
  /** The created on. */
  @Column(name = "CREATED_ON")
  private Date createdOn;
  
  /** The updated by. */
  @Column(name = "UPDATED_BY")
  private String updatedBy;
  
  /** The updated on. */
  @Column(name = "UPDATED_ON")
  private Date updatedOn;
  
  /**
   * Gets the id.
   *
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the id.
   *
   * @param id the new id
   */
  public void setId(String id) {
    this.id = id;
  }

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
   * Gets the deadline.
   *
   * @return the deadline
   */
  public Date getDeadline() {
    return deadline;
  }

  /**
   * Sets the deadline.
   *
   * @param deadline the new deadline
   */
  public void setDeadline(Date deadline) {
    this.deadline = deadline;
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
   * Gets the termination reason.
   *
   * @return the termination reason
   */
  public Set<MasterListValueEntity> getTerminationReason() {
    return terminationReason;
  }

  /**
   * Sets the termination reason.
   *
   * @param terminationReason the new termination reason
   */
  public void setTerminationReason(Set<MasterListValueEntity> terminationReason) {
    this.terminationReason = terminationReason;
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
  public Date getUpdatedOn() {
    return updatedOn;
  }

  /**
   * Sets the updated on.
   *
   * @param updatedOn the new updated on
   */
  public void setUpdatedOn(Date updatedOn) {
    this.updatedOn = updatedOn;
  }
  
  
}
