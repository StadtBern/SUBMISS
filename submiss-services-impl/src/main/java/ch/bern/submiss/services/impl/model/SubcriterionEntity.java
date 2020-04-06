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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "SUB_SUBCRITERION")
public class SubcriterionEntity {

  @Id
  @GeneratedValue(generator = "uuid1")
  @GenericGenerator(name = "uuid1", strategy = "uuid2")
  private String id;

  @ManyToOne
  @JoinColumn(name = "FK_CRITERION")
  private CriterionEntity criterion;

  @Column(name = "SUBCRITERION_TEXT")
  private String subcriterionText;

  @Column(name = "WEIGHTING")
  private Double weighting;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public CriterionEntity getCriterion() {
    return criterion;
  }

  public void setCriterion(CriterionEntity criterion) {
    this.criterion = criterion;
  }

  public String getSubcriterionText() {
    return subcriterionText;
  }

  public void setSubcriterionText(String subcriterionText) {
    this.subcriterionText = subcriterionText;
  }

  public Double getWeighting() {
    return weighting;
  }

  public void setWeighting(Double weighting) {
    this.weighting = weighting;
  }



}
