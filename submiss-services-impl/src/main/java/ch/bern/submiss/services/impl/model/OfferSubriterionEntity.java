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

package ch.bern.submiss.services.impl.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "SUB_OFFER_SUBCRITERION")
public class OfferSubriterionEntity {

  @Id
  @GeneratedValue(generator = "uuid1")
  @GenericGenerator(name = "uuid1", strategy = "uuid2")
  private String id;

  @ManyToOne
  @JoinColumn(name = "FK_OFFER")
  private OfferEntity offer;

  @ManyToOne
  @JoinColumn(name = "FK_SUBCRITERION")
  private SubcriterionEntity subcriterion;

  @Column(name = "GRADE")
  private BigDecimal grade;

  @Column(name = "SCORE")
  private BigDecimal score;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public OfferEntity getOffer() {
    return offer;
  }

  public void setOffer(OfferEntity offer) {
    this.offer = offer;
  }

  public SubcriterionEntity getSubcriterion() {
    return subcriterion;
  }

  public void setSubriterion(SubcriterionEntity subcriterion) {
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
}
