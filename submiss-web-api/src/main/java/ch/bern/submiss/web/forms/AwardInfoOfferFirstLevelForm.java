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

import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Class AwardInfoOfferFirstLevelForm.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AwardInfoOfferFirstLevelForm {

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
  
  /** The q ex status. */
  private Boolean qExStatus;
  
  /** The exclusion reason first level. */
  private String exclusionReasonFirstLevel;

  /** The exclusion reasons first level. */
  private Set<MasterListValueForm> exclusionReasonsFirstLevel;

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
   * Gets the q ex status.
   *
   * @return the q ex status
   */
  public Boolean getqExStatus() {
    return qExStatus;
  }

  /**
   * Sets the q ex status.
   *
   * @param qExStatus the new q ex status
   */
  public void setqExStatus(Boolean qExStatus) {
    this.qExStatus = qExStatus;
  }

  /**
   * Gets the exclusion reason first level.
   *
   * @return the exclusion reason first level
   */
  public String getExclusionReasonFirstLevel() {
    return exclusionReasonFirstLevel;
  }

  /**
   * Sets the exclusion reason first level.
   *
   * @param exclusionReasonFirstLevel the new exclusion reason first level
   */
  public void setExclusionReasonFirstLevel(String exclusionReasonFirstLevel) {
    this.exclusionReasonFirstLevel = exclusionReasonFirstLevel;
  }

  /**
   * Gets the exclusion reasons first level.
   *
   * @return the exclusion reasons first level
   */
  public Set<MasterListValueForm> getExclusionReasonsFirstLevel() {
    return exclusionReasonsFirstLevel;
  }

  /**
   * Sets the exclusion reasons first level.
   *
   * @param exclusionReasonsFirstLevel the new exclusion reasons first level
   */
  public void setExclusionReasonsFirstLevel(Set<MasterListValueForm> exclusionReasonsFirstLevel) {
    this.exclusionReasonsFirstLevel = exclusionReasonsFirstLevel;
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
