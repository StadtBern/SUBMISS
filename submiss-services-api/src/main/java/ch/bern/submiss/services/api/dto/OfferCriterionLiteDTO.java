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

/**
 * The Class OfferCriterionLiteDTO.
 */
public class OfferCriterionLiteDTO {

  /** The offer criterion id. */
  private String offerCriterionId;
  
  /** The grade. */
  private BigDecimal grade;
  
  /** The score. */
  private BigDecimal score;
  
  /**
   * Gets the offer criterion id.
   *
   * @return the offer criterion id
   */
  public String getOfferCriterionId() {
    return offerCriterionId;
  }
  
  /**
   * Sets the offer criterion id.
   *
   * @param offerCriterionId the new offer criterion id
   */
  public void setOfferCriterionId(String offerCriterionId) {
    this.offerCriterionId = offerCriterionId;
  }
  
  /**
   * Gets the grade.
   *
   * @return the grade
   */
  public BigDecimal getGrade() {
    return grade;
  }
  
  /**
   * Sets the grade.
   *
   * @param grade the new grade
   */
  public void setGrade(BigDecimal grade) {
    this.grade = grade;
  }
  
  /**
   * Gets the score.
   *
   * @return the score
   */
  public BigDecimal getScore() {
    return score;
  }
  
  /**
   * Sets the score.
   *
   * @param score the new score
   */
  public void setScore(BigDecimal score) {
    this.score = score;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "OfferCriterionLiteDTO [offerCriterionId=" + offerCriterionId + ", grade=" + grade
        + ", score=" + score + "]";
  }
}
