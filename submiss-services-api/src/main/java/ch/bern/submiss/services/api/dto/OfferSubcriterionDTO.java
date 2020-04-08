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
import java.sql.Timestamp;

public class OfferSubcriterionDTO extends AbstractDTO {

  private OfferDTO offer;

  private SubcriterionDTO subcriterion;

  private BigDecimal grade;

  private BigDecimal score;

  private Timestamp createdOn;

  private Timestamp updatedOn;

  public OfferDTO getOffer() {
    return offer;
  }

  public void setOffer(OfferDTO offer) {
    this.offer = offer;
  }

  public SubcriterionDTO getSubcriterion() {
    return subcriterion;
  }

  public void setSubcriterion(SubcriterionDTO subcriterion) {
    this.subcriterion = subcriterion;
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

  public Timestamp getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(Timestamp createdOn) {
    this.createdOn = createdOn;
  }

  public Timestamp getUpdatedOn() {
    return updatedOn;
  }

  public void setUpdatedOn(Timestamp updatedOn) {
    this.updatedOn = updatedOn;
  }
}
