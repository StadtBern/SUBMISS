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

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * The Class SubmittentProofProvided.
 */
@Entity
@Table(name = "SUB_TENDERER_PROOF_PROVIDED")
public class SubmittentProofProvidedEntity extends AbstractEntity {
  
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
   * The created on.
   */
  @CreationTimestamp
  @Column(name = "CREATED_ON")
  private Timestamp createdOn;

  /**
   * The updated on.
   */
  @UpdateTimestamp
  @Column(name = "UPDATED_ON", insertable = false)
  private Timestamp updatedOn;

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

  /**
   * Gets the created on.
   *
   * @return the created on
   */
  public Timestamp getCreatedOn() {
    return createdOn;
  }

  /**
   * Sets the created on.
   *
   * @param createdOn the new created on
   */
  public void setCreatedOn(Timestamp createdOn) {
    this.createdOn = createdOn;
  }

  /**
   * Gets the updatedOn.
   *
   * @return the updatedOn
   */
  public Timestamp getUpdatedOn() {
    return updatedOn;
  }

  /**
   * Sets the updatedOn.
   *
   * @param updatedOn the updatedOn
   */
  public void setUpdatedOn(Timestamp updatedOn) {
    this.updatedOn = updatedOn;
  }
}
