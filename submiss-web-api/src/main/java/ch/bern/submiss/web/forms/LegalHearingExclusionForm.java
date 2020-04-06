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
 * The Class LegalHearingExclusionForm.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LegalHearingExclusionForm {

  /** The exclusion date. */
  private Date exclusionDate;

  /** The legal exclusions. */
  private List<LegalExclusionForm> legalExclusions;

  /** The submission id. */
  private String submissionId;

  /** The first level exclusion date. */
  private Date firstLevelExclusionDate;

  /** The first level exclusion date view value. */
  private String fLExclusionDateViewValue;

  /** The exclusion date view value. */
  private String exclusionDateViewValue;

  /**
   * Gets the exclusion date.
   *
   * @return the exclusion date
   */
  public Date getExclusionDate() {
    return exclusionDate;
  }

  /**
   * Sets the exclusion date.
   *
   * @param exclusionDate the new exclusion date
   */
  public void setExclusionDate(Date exclusionDate) {
    this.exclusionDate = exclusionDate;
  }

  /**
   * Gets the legal exclusions.
   *
   * @return the legal exclusions
   */
  public List<LegalExclusionForm> getLegalExclusions() {
    return legalExclusions;
  }

  /**
   * Sets the legal exclusions.
   *
   * @param legalExclusions the new legal exclusions
   */
  public void setLegalExclusions(List<LegalExclusionForm> legalExclusions) {
    this.legalExclusions = legalExclusions;
  }

  /**
   * Gets the submission id.
   *
   * @return the submission id
   */
  public String getSubmissionId() {
    return submissionId;
  }

  /**
   * Sets the submission id.
   *
   * @param submissionId the new submission id
   */
  public void setSubmissionId(String submissionId) {
    this.submissionId = submissionId;
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

  /**
   * Gets the first level exclusion date view value.
   *
   * @return the first level exclusion date view value
   */
  public String getfLExclusionDateViewValue() {
    return fLExclusionDateViewValue;
  }

  /**
   * Sets the first level exclusion date view value.
   *
   * @param fLExclusionDateViewValue the new first level exclusion date view value
   */
  public void setfLExclusionDateViewValue(String fLExclusionDateViewValue) {
    this.fLExclusionDateViewValue = fLExclusionDateViewValue;
  }

  /**
   * Gets the exclusion date view value.
   *
   * @return the exclusion date view value
   */
  public String getexclusionDateViewValue() {
    return exclusionDateViewValue;
  }

  /**
   * Sets the exclusion date view value.
   *
   * @param exclusionDateViewValue the new exclusion date view value
   */
  public void setexclusionDateViewValue(String exclusionDateViewValue) {
    this.exclusionDateViewValue = exclusionDateViewValue;
  }

}
