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

package ch.bern.submiss.web.forms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;
import java.util.Set;

/**
 * The Class LegalHearingTerminateForm.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LegalHearingTerminateForm extends AbstractForm {

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
  private Set<MasterListValueForm> terminationReason;

  /**
   * The submission.
   */
  private String submissionId;

  /**
   * The deadline view value.
   */
  private String deadlineViewValue;

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
  public Set<MasterListValueForm> getTerminationReason() {
    return terminationReason;
  }

  /**
   * Sets the termination reason.
   *
   * @param terminationReason the new termination reason
   */
  public void setTerminationReason(Set<MasterListValueForm> terminationReason) {
    this.terminationReason = terminationReason;
  }

  /**
   * Gets the submission.
   *
   * @return the submission
   */
  public String getSubmission() {
    return submissionId;
  }

  /**
   * Sets the submission.
   *
   * @param submissionId the new submission
   */
  public void setSubmission(String submissionId) {
    this.submissionId = submissionId;
  }

  /**
   * Gets the deadline view value.
   *
   * @return the deadline view value
   */
  public String getDeadlineViewValue() {
    return deadlineViewValue;
  }

  /**
   * Sets the deadline view value.
   *
   * @param deadlineViewValue the new deadline view value
   */
  public void setDeadlineViewValue(String deadlineViewValue) {
    this.deadlineViewValue = deadlineViewValue;
  }
}
