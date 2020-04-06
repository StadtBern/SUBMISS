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

import org.hibernate.annotations.GenericGenerator;

/**
 * The Class ProofHistoryEntity.
 */
@Entity
@Table(name = "SUB_PROOF_HISTORY")
public class ProofHistoryEntity {

  @Id
  @GeneratedValue(generator = "uuid1")
  @GenericGenerator(name = "uuid1", strategy = "uuid2")
  private String id;

  @Column(name = "NAME")
  private String proofName;

  @Column(name = "DESCRIPTION")
  private String description;

  @Column(name = "PROOF_ORDER")
  private Integer proofOrder;

  @Column(name = "DESCRIPTION_FR")
  private String descriptionFr;

  @ManyToOne
  @JoinColumn(name = "FK_COUNTRY")
  private CountryEntity country;

  @ManyToOne
  @JoinColumn(name = "FK_TENANT")
  private TenantEntity tenant;

  @Column(name = "IS_ACTIVE")
  private Integer active;

  @Column(name = "IS_REQUIRED")
  private Boolean required;

  @Column(name = "VALIDITY_PERIOD")
  private Integer validityPeriod;

  @Column(name = "FROM_DATE")
  private Timestamp fromDate;

  @Column(name = "TO_DATE")
  private Timestamp toDate;

  @ManyToOne
  @JoinColumn(name = "FK_PROOF")
  private ProofEntity proofId;


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getProofName() {
    return proofName;
  }

  public void setProofName(String proofName) {
    this.proofName = proofName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getProofOrder() {
    return proofOrder;
  }

  public void setProofOrder(Integer proofOrder) {
    this.proofOrder = proofOrder;
  }

  public String getDescriptionFr() {
    return descriptionFr;
  }

  public void setDescriptionFr(String descriptionFr) {
    this.descriptionFr = descriptionFr;
  }

  public CountryEntity getCountry() {
    return country;
  }

  public void setCountry(CountryEntity country) {
    this.country = country;
  }

  public TenantEntity getTenant() {
    return tenant;
  }

  public void setTenant(TenantEntity tenant) {
    this.tenant = tenant;
  }

  public Integer getActive() {
    return active;
  }

  public void setActive(Integer active) {
    this.active = active;
  }

  public Boolean getRequired() {
    return required;
  }

  public void setRequired(Boolean required) {
    this.required = required;
  }

  public Integer getValidityPeriod() {
    return validityPeriod;
  }

  public void setValidityPeriod(Integer validityPeriod) {
    this.validityPeriod = validityPeriod;
  }

  public Timestamp getFromDate() {
    return fromDate;
  }

  public void setFromDate(Timestamp fromDate) {
    this.fromDate = fromDate;
  }

  public Timestamp getToDate() {
    return toDate;
  }

  public void setToDate(Timestamp toDate) {
    this.toDate = toDate;
  }

  public ProofEntity getProofId() {
    return proofId;
  }

  public void setProofId(ProofEntity proofId) {
    this.proofId = proofId;
  }


}
