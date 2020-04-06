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
 * The Class AwardAssessDTO.
 */
public class AwardAssessDTO {

  /** The offer id. */
  private String offerId;

  /** The rank. */
  private Integer rank;

  /** The total score. */
  private BigDecimal totalScore;

  /** The offer criteria. */
  private List<OfferCriterionLiteDTO> offerCriteria;

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
   * Gets the rank.
   *
   * @return the rank
   */
  public Integer getRank() {
    return rank;
  }

  /**
   * Sets the rank.
   *
   * @param rank the new rank
   */
  public void setRank(Integer rank) {
    this.rank = rank;
  }

  /**
   * Gets the total score.
   *
   * @return the total score
   */
  public BigDecimal getTotalScore() {
    return totalScore;
  }

  /**
   * Sets the total score.
   *
   * @param totalScore the new total score
   */
  public void setTotalScore(BigDecimal totalScore) {
    this.totalScore = totalScore;
  }

  /**
   * Gets the offer criteria.
   *
   * @return the offer criteria
   */
  public List<OfferCriterionLiteDTO> getOfferCriteria() {
    return offerCriteria;
  }

  /**
   * Sets the offer criteria.
   *
   * @param offerCriteria the new offer criteria
   */
  public void setOfferCriteria(List<OfferCriterionLiteDTO> offerCriteria) {
    this.offerCriteria = offerCriteria;
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

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "AwardAssessDTO [offerId=" + offerId + ", rank=" + rank + ", totalScore=" + totalScore
        + "]";
  }

}
