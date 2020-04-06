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

import java.sql.Timestamp;


/**
 * The Class ProofHistoryDTO.
 */
public class ProofHistoryDTO {

  /** The id. */
  private String id;

  /** The proof name. */
  private String proofName;

  /** The description. */
  private String description;

  /** The proof order. */
  private Integer proofOrder;

  /** The description fr. */
  private String descriptionFr;

  /** The country. */
  private CountryDTO country;

  /** The tenant. */
  private TenantDTO tenant;

  /** The active. */
  private Integer active;

  /** The required. */
  private Boolean required;

  /** The validity period. */
  private Integer validityPeriod;

  /** The from date. */
  private Timestamp fromDate;

  /** The to date. */
  private Timestamp toDate;

  /** The proof id. */
  private ProofDTO proofId;

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
   * Gets the proof name.
   *
   * @return the proof name
   */
  public String getProofName() {
    return proofName;
  }

  /**
   * Sets the proof name.
   *
   * @param proofName the new proof name
   */
  public void setProofName(String proofName) {
    this.proofName = proofName;
  }

  /**
   * Gets the description.
   *
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description.
   *
   * @param description the new description
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the proof order.
   *
   * @return the proof order
   */
  public Integer getProofOrder() {
    return proofOrder;
  }

  /**
   * Sets the proof order.
   *
   * @param proofOrder the new proof order
   */
  public void setProofOrder(Integer proofOrder) {
    this.proofOrder = proofOrder;
  }

  /**
   * Gets the description fr.
   *
   * @return the description fr
   */
  public String getDescriptionFr() {
    return descriptionFr;
  }

  /**
   * Sets the description fr.
   *
   * @param descriptionFr the new description fr
   */
  public void setDescriptionFr(String descriptionFr) {
    this.descriptionFr = descriptionFr;
  }

  /**
   * Gets the country.
   *
   * @return the country
   */
  public CountryDTO getCountry() {
    return country;
  }

  /**
   * Sets the country.
   *
   * @param country the new country
   */
  public void setCountry(CountryDTO country) {
    this.country = country;
  }

  /**
   * Gets the tenant.
   *
   * @return the tenant
   */
  public TenantDTO getTenant() {
    return tenant;
  }

  /**
   * Sets the tenant.
   *
   * @param tenant the new tenant
   */
  public void setTenant(TenantDTO tenant) {
    this.tenant = tenant;
  }

  /**
   * Gets the active.
   *
   * @return the active
   */
  public Integer getActive() {
    return active;
  }

  /**
   * Sets the active.
   *
   * @param active the new active
   */
  public void setActive(Integer active) {
    this.active = active;
  }

  /**
   * Gets the required.
   *
   * @return the required
   */
  public Boolean getRequired() {
    return required;
  }

  /**
   * Sets the required.
   *
   * @param required the new required
   */
  public void setRequired(Boolean required) {
    this.required = required;
  }

  /**
   * Gets the validity period.
   *
   * @return the validity period
   */
  public Integer getValidityPeriod() {
    return validityPeriod;
  }

  /**
   * Sets the validity period.
   *
   * @param validityPeriod the new validity period
   */
  public void setValidityPeriod(Integer validityPeriod) {
    this.validityPeriod = validityPeriod;
  }

  /**
   * Gets the from date.
   *
   * @return the from date
   */
  public Timestamp getFromDate() {
    return fromDate;
  }

  /**
   * Sets the from date.
   *
   * @param fromDate the new from date
   */
  public void setFromDate(Timestamp fromDate) {
    this.fromDate = fromDate;
  }

  /**
   * Gets the to date.
   *
   * @return the to date
   */
  public Timestamp getToDate() {
    return toDate;
  }

  /**
   * Sets the to date.
   *
   * @param toDate the new to date
   */
  public void setToDate(Timestamp toDate) {
    this.toDate = toDate;
  }

  /**
   * Gets the proof id.
   *
   * @return the proof id
   */
  public ProofDTO getProofId() {
    return proofId;
  }

  /**
   * Sets the proof id.
   *
   * @param proofId the new proof id
   */
  public void setProofId(ProofDTO proofId) {
    this.proofId = proofId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "ProofHistoryDTO [id=" + id + ", proofName=" + proofName + ", description=" + description
        + ", proofOrder=" + proofOrder + ", descriptionFr=" + descriptionFr + ", active=" + active
        + ", required=" + required + ", validityPeriod=" + validityPeriod + ", fromDate=" + fromDate
        + ", toDate=" + toDate + "]";
  }

}
