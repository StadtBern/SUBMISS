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
 * The Class SuitabilityDTO.
 */
public class SuitabilityDTO {

  /** The offer id. */
  private String offerId;

  /** The q ex rank. */
  private Integer qExRank;

  /** The must criterion. */
  private List<CriterionLiteDTO> mustCriterion;

  /** The evaluated criterion. */
  private List<CriterionLiteDTO> evaluatedCriterion;

  /** The q ex status. */
  private Boolean qExStatus;

  /** The q ex total grade. */
  private BigDecimal qExTotalGrade;

  /** The q ex examination is fulfilled. */
  private Boolean qExExaminationIsFulfilled;

  /** The q ex suitability notes. */
  private String qExSuitabilityNotes;

  /** The offer subcriteria. */
  private List<OfferSubcriterionLiteDTO> offerSubcriteria;

  /**
   * Gets the offer id.
   *
   * @return the offer id
   */
  public String getOfferId() {
    return offerId;
  }

  /**
   * Sets the offer id.
   *
   * @param offerId the new offer id
   */
  public void setOfferId(String offerId) {
    this.offerId = offerId;
  }

  /**
   * Gets the q ex rank.
   *
   * @return the q ex rank
   */
  public Integer getqExRank() {
    return qExRank;
  }

  /**
   * Sets the q ex rank.
   *
   * @param qExRank the new q ex rank
   */
  public void setqExRank(Integer qExRank) {
    this.qExRank = qExRank;
  }

  /**
   * Gets the must criterion.
   *
   * @return the must criterion
   */
  public List<CriterionLiteDTO> getMustCriterion() {
    return mustCriterion;
  }

  /**
   * Sets the must criterion.
   *
   * @param mustCriterion the new must criterion
   */
  public void setMustCriterion(List<CriterionLiteDTO> mustCriterion) {
    this.mustCriterion = mustCriterion;
  }

  /**
   * Gets the evaluated criterion.
   *
   * @return the evaluated criterion
   */
  public List<CriterionLiteDTO> getEvaluatedCriterion() {
    return evaluatedCriterion;
  }

  /**
   * Sets the evaluated criterion.
   *
   * @param evaluatedCriterion the new evaluated criterion
   */
  public void setEvaluatedCriterion(List<CriterionLiteDTO> evaluatedCriterion) {
    this.evaluatedCriterion = evaluatedCriterion;
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
   * Gets the q ex total grade.
   *
   * @return the q ex total grade
   */
  public BigDecimal getqExTotalGrade() {
    return qExTotalGrade;
  }

  /**
   * Sets the q ex total grade.
   *
   * @param qExTotalGrade the new q ex total grade
   */
  public void setqExTotalGrade(BigDecimal qExTotalGrade) {
    this.qExTotalGrade = qExTotalGrade;
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
   * Gets the q ex suitability notes.
   *
   * @return the q ex suitability notes
   */
  public String getqExSuitabilityNotes() {
    return qExSuitabilityNotes;
  }

  /**
   * Sets the q ex suitability notes.
   *
   * @param qExSuitabilityNotes the new q ex suitability notes
   */
  public void setqExSuitabilityNotes(String qExSuitabilityNotes) {
    this.qExSuitabilityNotes = qExSuitabilityNotes;
  }

  /**
   * Gets the offer subcriteria.
   *
   * @return the offer subcriteria
   */
  public List<OfferSubcriterionLiteDTO> getOfferSubcriteria() {
    return offerSubcriteria;
  }

  /**
   * Sets the offer subcriteria.
   *
   * @param offerSubcriteria the new offer subcriteria
   */
  public void setOfferSubcriteria(List<OfferSubcriterionLiteDTO> offerSubcriteria) {
    this.offerSubcriteria = offerSubcriteria;
  }

  @Override
  public String toString() {
    return "SuitabilityDTO [offerId=" + offerId + ", qExRank=" + qExRank + ", qExStatus="
        + qExStatus + ", qExTotalGrade=" + qExTotalGrade + ", qExExaminationIsFulfilled="
        + qExExaminationIsFulfilled + ", qExSuitabilityNotes=" + qExSuitabilityNotes + "]";
  }

}
