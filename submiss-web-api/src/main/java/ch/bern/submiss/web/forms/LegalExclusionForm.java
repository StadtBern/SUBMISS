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
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Class LegalExclusionForm.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LegalExclusionForm {
  /** The id. */
  private String id;

  /** The submittent. */
  private SubmittentForm submittent;

  /** The exclusion deadline. */
  private Date exclusionDeadline;

  /** The proofs provided. */
  private Boolean proofsProvided;

  /** The exists exl reasons. */
  private Boolean existsExlReasons;

  /** The must crit fulfilled. */
  private Boolean mustCritFulfilled;

  /** The exclusion reason. */
  private String exclusionReason;

  /** The exclusion reasons. */
  private Set<ExclusionReasonForm> exclusionReasons;

  /** The level. */
  private Integer level;

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
   * Gets the submittent.
   *
   * @return the submittent
   */
  public SubmittentForm getSubmittent() {
    return submittent;
  }

  /**
   * Sets the submittent.
   *
   * @param submittent the new submittent
   */
  public void setSubmittent(SubmittentForm submittent) {
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
  public Set<ExclusionReasonForm> getExclusionReasons() {
    return exclusionReasons;
  }

  /**
   * Sets the exclusion reasons.
   *
   * @param exclusionReasons the new exclusion reasons
   */
  public void setExclusionReasons(Set<ExclusionReasonForm> exclusionReasons) {
    this.exclusionReasons = exclusionReasons;
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
}
