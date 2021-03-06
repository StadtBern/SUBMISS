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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Class AwardInfoDTO.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AwardInfoForm extends AbstractForm {

  /** The submission. */
  private SubmissionForm submission;

  /** The available date. */
  private Date availableDate;

  /** The freeze close submission. */
  private Boolean freezeCloseSubmission;
  
  /** The object name read. */
  private Boolean objectNameRead;

  /** The project name read. */
  private Boolean projectNameRead;

  /** The working class read. */
  private Boolean workingClassRead;

  /** The description read. */
  private Boolean descriptionRead;
  
  /** The added award recipients. */
  private BigDecimal addedAwardRecipients;
  
  /** The offers. */
  private List<AwardInfoOfferForm> offers;

  /**
   * Gets the submission.
   *
   * @return the submission
   */
  public SubmissionForm getSubmission() {
    return submission;
  }

  /**
   * Sets the submission.
   *
   * @param submission the new submission
   */
  public void setSubmission(SubmissionForm submission) {
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
   * Gets the added award recipients.
   *
   * @return the added award recipients
   */
  public BigDecimal getAddedAwardRecipients() {
    return addedAwardRecipients;
  }

  /**
   * Sets the added award recipients.
   *
   * @param addedAwardRecipients the new added award recipients
   */
  public void setAddedAwardRecipients(BigDecimal addedAwardRecipients) {
    this.addedAwardRecipients = addedAwardRecipients;
  }

  /**
   * Gets the offers.
   *
   * @return the offers
   */
  public List<AwardInfoOfferForm> getOffers() {
    return offers;
  }

  /**
   * Sets the offers.
   *
   * @param offers the new offers
   */
  public void setOffers(List<AwardInfoOfferForm> offers) {
    this.offers = offers;
  }

  @Override
  public String toString() {
    return "AwardInfoForm [id=" + super.getId() + ", availableDate=" + availableDate
        + ", freezeCloseSubmission=" + freezeCloseSubmission + ", objectNameRead=" + objectNameRead
        + ", projectNameRead=" + projectNameRead + ", workingClassRead=" + workingClassRead
        + ", descriptionRead=" + descriptionRead + ", addedAwardRecipients=" + addedAwardRecipients
        + ", offers=" + offers + "]";
  }
}
