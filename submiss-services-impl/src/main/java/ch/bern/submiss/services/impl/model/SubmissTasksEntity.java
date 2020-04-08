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

import ch.bern.submiss.services.api.constants.TaskTypes;
import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * The Class SubmissTasksEntity.
 */
@Entity
@Table(name = "SUB_TASKS")
public class SubmissTasksEntity extends AbstractEntity {

  /**
   * The submission.
   */
  @ManyToOne
  @JoinColumn(name = "FK_TENDER")
  private SubmissionEntity submission;

  /**
   * The submission.
   */
  @ManyToOne
  @JoinColumn(name = "FK_COMPANY")
  private CompanyEntity company;

  /**
   * User to edit.
   */
  @Column(name = "FK_USER_TO_EDIT")
  private String userToEdit;

  /**
   * User assigned.
   */
  @Column(name = "FK_USER_ASSIGNED")
  private String userAssigned;

  /**
   * description is the task name.
   */
  @Column(name = "DESCRIPTION")
  @Enumerated(EnumType.STRING)
  private TaskTypes description;

  /**
   * The created by.
   */
  @Column(name = "CREATED_BY")
  private String createdBy;

  /**
   * The created on.
   */
  @Column(name = "CREATED_ON")
  private Date createdOn;

  /**
   * The updated on.
   */
  @UpdateTimestamp
  @Column(name = "UPDATED_ON", insertable = false)
  private Timestamp updatedOn;

  /**
   * User Assigned by the system.
   */
  @Column(name = "FK_USER_AUTOASSIGNED")
  private String userAutoAssigned;

  /**
   * The type. Only for hybrid tasks.
   */
  @Column(name = "TYPE")
  private Integer type;

  /**
   * The submit date.
   */
  @Column(name = "SUBMIT_DATE")
  private Date submitDate;

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
   * Gets the company.
   *
   * @return the company
   */
  public CompanyEntity getCompany() {
    return company;
  }

  /**
   * Sets the company.
   *
   * @param company the new company
   */
  public void setCompany(CompanyEntity company) {
    this.company = company;
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
    return "SubmissTasksEntity [id=" + super.getId() + ", userToEdit=" + userToEdit
      + ", userAssigned="
      + userAssigned + ", description=" + description + ", createdBy=" + createdBy
      + ", createdOn=" + createdOn + ", userAutoAssigned=" + userAutoAssigned + "]";
  }
}
