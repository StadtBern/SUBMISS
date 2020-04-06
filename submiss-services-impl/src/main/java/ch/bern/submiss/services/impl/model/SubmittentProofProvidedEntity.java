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

/**
 * The Class SubmittentProofProvided.
 */
@Entity
@Table(name = "SUB_TENDERER_PROOF_PROVIDED")
public class SubmittentProofProvidedEntity {
  
  /** The id. */
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private String id;
  
  /** The submittent. */
  @ManyToOne
  @JoinColumn(name = "FK_TENDERER")
  private SubmittentEntity submittent;
  
  /** The company. */
  @ManyToOne
  @JoinColumn(name = "FK_COMPANY")
  private CompanyEntity company;
  
  /** The is provided. */
  @Column(name = "IS_PROVIDED")
  private Boolean isProvided;
  
  /** The level. */
  @Column(name = "LEVEL")
  private Integer level;

  /**
   * Gets the id.
   *
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the id.
   *
   * @param id the new id
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the submittent.
   *
   * @return the submittent
   */
  public SubmittentEntity getSubmittent() {
    return submittent;
  }

  /**
   * Sets the submittent.
   *
   * @param submittent the new submittent
   */
  public void setSubmittent(SubmittentEntity submittent) {
    this.submittent = submittent;
  }

  /**
   * Gets the company.
   *
   * @return the company
   */
  public CompanyEntity getCompany() {
    return company;
  }

  /**
   * Sets the company.
   *
   * @param company the new company
   */
  public void setCompany(CompanyEntity company) {
    this.company = company;
  }

  /**
   * Gets the checks if is provided.
   *
   * @return the checks if is provided
   */
  public Boolean getIsProvided() {
    return isProvided;
  }

  /**
   * Sets the checks if is provided.
   *
   * @param isProvided the new checks if is provided
   */
  public void setIsProvided(Boolean isProvided) {
    this.isProvided = isProvided;
  }

  /**
   * Gets the level.
   *
   * @return the level
   */
  public Integer getLevel() {
    return level;
  }

  /**
   * Sets the level.
   *
   * @param level the new level
   */
  public void setLevel(Integer level) {
    this.level = level;
  }
}
