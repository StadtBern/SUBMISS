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
import java.util.List;

/**
 * The Class ExaminationDTO.
 */
public class ExaminationDTO {

  /** The min grade. */
  private BigDecimal minGrade;

  /** The max grade. */
  private BigDecimal maxGrade;

  /** The submission id. */
  private String submissionId;

  /** The criterion. */
  private List<CriterionDTO> criterion;

  /**
   * The number of passing applicants to the 2nd stage of Selektiv.
   */
  private Integer passingApplicants;

  /**
   * Gets the min grade.
   *
   * @return the min grade
   */
  public BigDecimal getMinGrade() {
    return minGrade;
  }

  /**
   * Sets the min grade.
   *
   * @param minGrade the new min grade
   */
  public void setMinGrade(BigDecimal minGrade) {
    this.minGrade = minGrade;
  }

  /**
   * Gets the max grade.
   *
   * @return the max grade
   */
  public BigDecimal getMaxGrade() {
    return maxGrade;
  }

  /**
   * Sets the max grade.
   *
   * @param maxGrade the new max grade
   */
  public void setMaxGrade(BigDecimal maxGrade) {
    this.maxGrade = maxGrade;
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
   * Gets the criterion.
   *
   * @return the criterion
   */
  public List<CriterionDTO> getCriterion() {
    return criterion;
  }

  /**
   * Sets the criterion.
   *
   * @param criterion the new criterion
   */
  public void setCriterion(List<CriterionDTO> criterion) {
    this.criterion = criterion;
  }

  public Integer getPassingApplicants() {
    return passingApplicants;
  }

  public void setPassingApplicants(Integer passingApplicants) {
    this.passingApplicants = passingApplicants;
  }

  @Override
  public String toString() {
    return "ExaminationDTO [minGrade=" + minGrade + ", maxGrade=" + maxGrade + ", submissionId="
        + submissionId + "]";
  }

}
