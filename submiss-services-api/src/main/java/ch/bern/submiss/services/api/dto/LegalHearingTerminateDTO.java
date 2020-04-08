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

import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

/**
 * The Class LegalHearingTerminateDTO.
 */
public class LegalHearingTerminateDTO extends AbstractDTO {

  /**
   * The deadline.
   */
  private Date deadline;

  /**
   * The reason.
   */
  private String reason;

  /**
   * The termination reason.
   */
  private Set<MasterListValueDTO> terminationReason;

  /**
   * The submission.
   */
  private String submissionId;

  /**
   * The created on.
   */
  private Timestamp createdOn;

  /**
   * The updated on.
   */
  private Timestamp updatedOn;

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
  public Set<MasterListValueDTO> getTerminationReason() {
    return terminationReason;
  }

  /**
   * Sets the termination reason.
   *
   * @param terminationReason the new termination reason
   */
  public void setTerminationReason(Set<MasterListValueDTO> terminationReason) {
    this.terminationReason = terminationReason;
  }

  /**
   * Gets the submission.
   *
   * @return the submission
   */
  public String getSubmissionId() {
    return submissionId;
  }

  /**
   * Sets the submission.
   *
   * @param submissionId the new submission
   */
  public void setSubmissionId(String submissionId) {
    this.submissionId = submissionId;
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

  @Override
  public String toString() {
    return "LegalHearingTerminateDTO [id=" + super.getId() + ", deadline=" + deadline + ", reason="
      + reason
      + ", submissionId=" + submissionId + ", lastModifiedOn=" + "]";
  }
}
