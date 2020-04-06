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

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ch.bern.submiss.services.api.dto.CountryDTO;
import ch.bern.submiss.services.api.dto.ProofDTO;
import ch.bern.submiss.services.api.dto.TenantDTO;


@JsonIgnoreProperties(ignoreUnknown = true)
public class ProofHistoryForm {

  private String id;

  private String proofName;

  private String description;

  private Integer proofOrder;

  private String descriptionFr;

  private CountryDTO country;

  private TenantDTO tenant;

  private Integer active;

  private Boolean required;

  private Integer validityPeriod;

  private Timestamp fromDate;

  private Timestamp toDate;

  private ProofDTO proofId;

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

  public CountryDTO getCountry() {
    return country;
  }

  public void setCountry(CountryDTO country) {
    this.country = country;
  }

  public TenantDTO getTenant() {
    return tenant;
  }

  public void setTenant(TenantDTO tenant) {
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

  public ProofDTO getProofId() {
    return proofId;
  }

  public void setProofId(ProofDTO proofId) {
    this.proofId = proofId;
  }

}
