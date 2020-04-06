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

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AwardAssessForm {

  private String offerId;
  private Integer rank;
  private BigDecimal totalScore;
  private List<OfferCriterionLiteForm> offerCriteria;
  private List<OfferSubcriterionLiteForm> offerSubcriteria;
  
  public String getOfferId() {
    return offerId;
  }
  public void setOfferId(String offerId) {
    this.offerId = offerId;
  }
  public Integer getRank() {
    return rank;
  }
  public void setRank(Integer rank) {
    this.rank = rank;
  }
  public BigDecimal getTotalScore() {
    return totalScore;
  }
  public void setTotalScore(BigDecimal totalScore) {
    this.totalScore = totalScore;
  }
  public List<OfferCriterionLiteForm> getOfferCriteria() {
    return offerCriteria;
  }
  public void setOfferCriteria(List<OfferCriterionLiteForm> offerCriteria) {
    this.offerCriteria = offerCriteria;
  }
  public List<OfferSubcriterionLiteForm> getOfferSubcriteria() {
    return offerSubcriteria;
  }
  public void setOfferSubcriteria(List<OfferSubcriterionLiteForm> offerSubcriteria) {
    this.offerSubcriteria = offerSubcriteria;
  }

}
