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

import java.math.BigDecimal;
import java.util.Set;

/**
 * The Class AwardInfoOfferDTO.
 */
public class AwardInfoOfferDTO {

  /** The id. */
  private String id;
  
  /** The submittent name. */
  private String submittentName;

  /** The exists exclusion reasons. */
  private Boolean existsExclusionReasons;

  /** The formal examination fulfilled. */
  private Boolean formalExaminationFulfilled;

  /** The must criteria fulfilled. */
  private Boolean mustCriteriaFulfilled;
  
  /** The q ex examination is fulfilled. */
  private Boolean qExExaminationIsFulfilled;

  /** The is awarded. */
  private Boolean isAwarded;

  /** The amount. */
  private BigDecimal amount;
  
  /** The manual amount. */
  private BigDecimal manualAmount;
  
  /** The exclusion reason. */
  private String exclusionReason;

  /** The exclusionReasons. */
  private Set<MasterListValueDTO> exclusionReasons;

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
   * Gets the exists exclusion reasons.
   *
   * @return the exists exclusion reasons
   */
  public Boolean getExistsExclusionReasons() {
    return existsExclusionReasons;
  }

  /**
   * Sets the exists exclusion reasons.
   *
   * @param existsExclusionReasons the new exists exclusion reasons
   */
  public void setExistsExclusionReasons(Boolean existsExclusionReasons) {
    this.existsExclusionReasons = existsExclusionReasons;
  }

  /**
   * Gets the formal examination fulfilled.
   *
   * @return the formal examination fulfilled
   */
  public Boolean getFormalExaminationFulfilled() {
    return formalExaminationFulfilled;
  }

  /**
   * Sets the formal examination fulfilled.
   *
   * @param formalExaminationFulfilled the new formal examination fulfilled
   */
  public void setFormalExaminationFulfilled(Boolean formalExaminationFulfilled) {
    this.formalExaminationFulfilled = formalExaminationFulfilled;
  }

  /**
   * Gets the must criteria fulfilled.
   *
   * @return the must criteria fulfilled
   */
  public Boolean getMustCriteriaFulfilled() {
    return mustCriteriaFulfilled;
  }

  /**
   * Sets the must criteria fulfilled.
   *
   * @param mustCriteriaFulfilled the new must criteria fulfilled
   */
  public void setMustCriteriaFulfilled(Boolean mustCriteriaFulfilled) {
    this.mustCriteriaFulfilled = mustCriteriaFulfilled;
  }

  /**
   * Gets the q ex examination is fulfilled.
   *
   * @return the q ex examination is fulfilled
   */
  public Boolean getqExExaminationIsFulfilled() {
    return qExExaminationIsFulfilled;
  }

  /**
   * Sets the q ex examination is fulfilled.
   *
   * @param qExExaminationIsFulfilled the new q ex examination is fulfilled
   */
  public void setqExExaminationIsFulfilled(Boolean qExExaminationIsFulfilled) {
    this.qExExaminationIsFulfilled = qExExaminationIsFulfilled;
  }

  /**
   * Gets the checks if is awarded.
   *
   * @return the checks if is awarded
   */
  public Boolean getIsAwarded() {
    return isAwarded;
  }

  /**
   * Sets the checks if is awarded.
   *
   * @param isAwarded the new checks if is awarded
   */
  public void setIsAwarded(Boolean isAwarded) {
    this.isAwarded = isAwarded;
  }
  
  /**
   * Gets the amount.
   *
   * @return the amount
   */
  public BigDecimal getAmount() {
    return amount;
  }

  /**
   * Sets the amount.
   *
   * @param amount the new amount
   */
  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  /**
   * Gets the manual amount.
   *
   * @return the manual amount
   */
  public BigDecimal getManualAmount() {
    return manualAmount;
  }

  /**
   * Sets the manual amount.
   *
   * @param manualAmount the new manual amount
   */
  public void setManualAmount(BigDecimal manualAmount) {
    this.manualAmount = manualAmount;
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
  public Set<MasterListValueDTO> getExclusionReasons() {
    return exclusionReasons;
  }

  /**
   * Sets the exclusion reasons.
   *
   * @param exclusionReasons the new exclusion reasons
   */
  public void setExclusionReasons(Set<MasterListValueDTO> exclusionReasons) {
    this.exclusionReasons = exclusionReasons;
  }

  /**
   * Gets the submittent name.
   *
   * @return the submittent name
   */
  public String getSubmittentName() {
    return submittentName;
  }

  /**
   * Sets the submittent name.
   *
   * @param submittentName the new submittent name
   */
  public void setSubmittentName(String submittentName) {
    this.submittentName = submittentName;
  }
}
