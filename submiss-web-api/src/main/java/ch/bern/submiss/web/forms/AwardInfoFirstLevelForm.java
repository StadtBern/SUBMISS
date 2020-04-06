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

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Class AwardInfoFirstLevelForm.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AwardInfoFirstLevelForm {

  /** The id. */
  private String id;
  
  /** The submission. */
  private SubmissionForm submission;

  /** The available date. */
  private Date availableDate;
  
  /** The object name read. */
  private Boolean objectNameRead;

  /** The project name read. */
  private Boolean projectNameRead;

  /** The working class read. */
  private Boolean workingClassRead;

  /** The description read. */
  private Boolean descriptionRead;
  
  /** The reason. */
  private String reason;
  
  /** The offers. */
  private List<AwardInfoOfferFirstLevelForm> offers;

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
   * @param addedAwardRecipients the new reason
   */
  public void setReason(String reason) {
    this.reason = reason;
  }
  
  /**
   * Gets the offers.
   *
   * @return the offers
   */
  public List<AwardInfoOfferFirstLevelForm> getOffers() {
    return offers;
  }

  /**
   * Sets the offers.
   *
   * @param offers the new offers
   */
  public void setOffers(List<AwardInfoOfferFirstLevelForm> offers) {
    this.offers = offers;
  }
}
