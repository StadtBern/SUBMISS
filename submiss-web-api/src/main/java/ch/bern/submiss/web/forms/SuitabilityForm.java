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
public class SuitabilityForm {

  private String offerId;
  private Integer qExRank;
  private List<CriterionLiteForm> mustCriterion;
  private List<CriterionLiteForm> evaluatedCriterion;
  private Boolean qExStatus;
  private BigDecimal qExTotalGrade;
  private Boolean qExExaminationIsFulfilled;
  private String qExSuitabilityNotes;
  private List<OfferSubcriterionLiteForm> offerSubcriteria;

  public String getOfferId() {
    return offerId;
  }

  public void setOfferId(String offerId) {
    this.offerId = offerId;
  }

  public Integer getqExRank() {
    return qExRank;
  }

  public void setqExRank(Integer qExRank) {
    this.qExRank = qExRank;
  }

  public List<CriterionLiteForm> getMustCriterion() {
    return mustCriterion;
  }

  public void setMustCriterion(List<CriterionLiteForm> mustCriterion) {
    this.mustCriterion = mustCriterion;
  }

  public List<CriterionLiteForm> getEvaluatedCriterion() {
    return evaluatedCriterion;
  }

  public void setEvaluatedCriterion(List<CriterionLiteForm> evaluatedCriterion) {
    this.evaluatedCriterion = evaluatedCriterion;
  }

  public Boolean getqExStatus() {
    return qExStatus;
  }

  public void setqExStatus(Boolean qExStatus) {
    this.qExStatus = qExStatus;
  }

  public BigDecimal getqExTotalGrade() {
    return qExTotalGrade;
  }

  public void setqExTotalGrade(BigDecimal qExTotalGrade) {
    this.qExTotalGrade = qExTotalGrade;
  }

  public Boolean getqExExaminationIsFulfilled() {
    return qExExaminationIsFulfilled;
  }

  public void setqExExaminationIsFulfilled(Boolean qExExaminationIsFulfilled) {
    this.qExExaminationIsFulfilled = qExExaminationIsFulfilled;
  }

  public String getqExSuitabilityNotes() {
    return qExSuitabilityNotes;
  }

  public void setqExSuitabilityNotes(String qExSuitabilityNotes) {
    this.qExSuitabilityNotes = qExSuitabilityNotes;
  }

  public List<OfferSubcriterionLiteForm> getOfferSubcriteria() {
    return offerSubcriteria;
  }

  public void setOfferSubcriteria(List<OfferSubcriterionLiteForm> offerSubcriteria) {
    this.offerSubcriteria = offerSubcriteria;
  }

}
