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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OfferSubcriterionLiteForm {

  private String offerSubcriterionId;
  private BigDecimal grade;
  private BigDecimal score;
  
  public String getOfferSubcriterionId() {
    return offerSubcriterionId;
  }
  public void setOfferSubcriterionId(String offerSubcriterionId) {
    this.offerSubcriterionId = offerSubcriterionId;
  }
  public BigDecimal getGrade() {
    return grade;
  }
  public void setGrade(BigDecimal grade) {
    this.grade = grade;
  }
  public BigDecimal getScore() {
    return score;
  }
  public void setScore(BigDecimal score) {
    this.score = score;
  }
}
