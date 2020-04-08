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
 * The Class LegalHearingExclusionEntity.
 */
@Entity
@Table(name = "SUB_LEGAL_EXCLUSION")
public class LegalHearingExclusionEntity extends AbstractEntity {

  /**
   * The submittent.
   */
  @ManyToOne
  @JoinColumn(name = "FK_TENDERER")
  private SubmittentEntity submittent;

  /**
   * The exclusion deadline.
   */
  @Column(name = "EXCL_HEARING_DEADLINE")
  private Date exclusionDeadline;

  /**
   * The proofs provided.
   */
  @Column(name = "PROOFS_PROVIDED")
  private Boolean proofsProvided;

  /**
   * The exists exl reasons.
   */
  @Column(name = "EXISTS_EXC_REASONS")
  private Boolean existsExlReasons;

  /**
   * The must crit fulfilled.
   */
  @Column(name = "MUST_CRIT_FULFILLED")
  private Boolean mustCritFulfilled;

  /**
   * The exclusion reason.
   */
  @Column(name = "EXCUSION_REASON")
  private String exclusionReason;

  /**
   * The exclusion reasons.
   */
  @ManyToMany
  @JoinTable(name = "SUB_LEGAL_EXCLUSION_REASON", joinColumns = {
    @JoinColumn(name = "FK_LEGAL_EXCLUSION")},
    inverseJoinColumns = {@JoinColumn(name = "FK_HEARING_EXCLUSION_REASON")})
  private Set<MasterListValueEntity> exclusionReasons;

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
  @Column(name = "UPDATED_ON")
  private Timestamp updatedOn;

  /**
   * The level.
   */
  @Column(name = "LEVEL")
  private Integer level;

  /**
   * The first level exclusion date.
   */
  @Column(name = "EXCL_HEARING_DEADLINE_FIRST_LEVEL")
  private Date firstLevelExclusionDate;

  /**
   * Gets the submittent.
   *
   * @return the submittent
   */
  public SubmittentEntity getSubmittent() {
    return submittent;
  }

  /**
   * Sets the submittent.
   *
   * @param submittent the new submittent
   */
  public void setSubmittent(SubmittentEntity submittent) {
    this.submittent = submittent;
  }

  /**
   * Gets the exclusion deadline.
   *
   * @return the exclusion deadline
   */
  public Date getExclusionDeadline() {
    return exclusionDeadline;
  }

  /**
   * Sets the exclusion deadline.
   *
   * @param exclusionDeadline the new exclusion deadline
   */
  public void setExclusionDeadline(Date exclusionDeadline) {
    this.exclusionDeadline = exclusionDeadline;
  }

  /**
   * Gets the proofs provided.
   *
   * @return the proofs provided
   */
  public Boolean getProofsProvided() {
    return proofsProvided;
  }

  /**
   * Sets the proofs provided.
   *
   * @param proofsProvided the new proofs provided
   */
  public void setProofsProvided(Boolean proofsProvided) {
    this.proofsProvided = proofsProvided;
  }

  /**
   * Gets the exists exl reasons.
   *
   * @return the exists exl reasons
   */
  public Boolean getExistsExlReasons() {
    return existsExlReasons;
  }

  /**
   * Sets the exists exl reasons.
   *
   * @param existsExlReasons the new exists exl reasons
   */
  public void setExistsExlReasons(Boolean existsExlReasons) {
    this.existsExlReasons = existsExlReasons;
  }

  /**
   * Gets the must crit fulfilled.
   *
   * @return the must crit fulfilled
   */
  public Boolean getMustCritFulfilled() {
    return mustCritFulfilled;
  }

  /**
   * Sets the must crit fulfilled.
   *
   * @param mustCritFulfilled the new must crit fulfilled
   */
  public void setMustCritFulfilled(Boolean mustCritFulfilled) {
    this.mustCritFulfilled = mustCritFulfilled;
  }

  /**
   * Gets the exclusion reason.
   *
   * @return the exclusion reason
   */
  public String getExclusionReason() {
    return exclusionReason;
  }

  /**
   * Sets the exclusion reason.
   *
   * @param exclusionReason the new exclusion reason
   */
  public void setExclusionReason(String exclusionReason) {
    this.exclusionReason = exclusionReason;
  }

  /**
   * Gets the exclusion reasons.
   *
   * @return the exclusion reasons
   */
  public Set<MasterListValueEntity> getExclusionReasons() {
    return exclusionReasons;
  }

  /**
   * Sets the exclusion reasons.
   *
   * @param exclusionReasons the new exclusion reasons
   */
  public void setExclusionReasons(Set<MasterListValueEntity> exclusionReasons) {
    this.exclusionReasons = exclusionReasons;
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
   * Gets the level.
   *
   * @return the level
   */
  public Integer getLevel() {
    return level;
  }

  /**
   * Sets the level.
   *
   * @param level the new level
   */
  public void setLevel(Integer level) {
    this.level = level;
  }

  /**
   * Gets the first level exclusion date.
   *
   * @return the first level exclusion date
   */
  public Date getFirstLevelExclusionDate() {
    return firstLevelExclusionDate;
  }

  /**
   * Sets the first level exclusion date.
   *
   * @param firstLevelExclusionDate the new first level exclusion date
   */
  public void setFirstLevelExclusionDate(Date firstLevelExclusionDate) {
    this.firstLevelExclusionDate = firstLevelExclusionDate;
  }

  @Override
  public String toString() {
    return "LegalHearingExclusionEntity [id=" + super.getId() + ", exclusionDeadline="
      + exclusionDeadline
      + ", proofsProvided=" + proofsProvided
      + ", existsExlReasons=" + existsExlReasons + ", mustCritFulfilled=" + mustCritFulfilled
      + ", exclusionReason=" + exclusionReason + ", createdBy=" + createdBy + ", createdOn="
      + createdOn + ", updatedBy=" + updatedBy
      + ", updatedOn=" + updatedOn + ", level=" + level + ", firstLevelExclusionDate="
      + firstLevelExclusionDate + "]";
  }
}
