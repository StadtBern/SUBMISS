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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The Class CountryHistoryEntity.
 */
@Entity
@Table(name = "SUB_COUNTRY_HISTORY")
public class CountryHistoryEntity extends AbstractEntity {

  /** The country name. */
  @Column(name = "COUNTRY_NAME", length = 50)
  private String countryName;

  /** The country short name. */
  @Column(name = "COUNTRY_SHORT_NAME", length = 50)
  private String countryShortName;

  /** The tel prefix. */
  @Column(name = "TEL_PREFIX", length = 50)
  private String telPrefix;

  /** The from date. */
  @Column(name = "FROM_DATE")
  private Timestamp fromDate;

  /** The to date. */
  @Column(name = "TO_DATE")
  private Timestamp toDate;

  /** The country id. */
  @ManyToOne
  @JoinColumn(name = "FK_COUNTRY")
  private CountryEntity countryId;

  /** The active. */
  @Column(name = "IS_ACTIVE")
  private Boolean active;

  /**
   * Gets the country name.
   *
   * @return the country name
   */
  public String getCountryName() {
    return countryName;
  }

  /**
   * Sets the country name.
   *
   * @param countryName the new country name
   */
  public void setCountryName(String countryName) {
    this.countryName = countryName;
  }

  /**
   * Gets the country short name.
   *
   * @return the country short name
   */
  public String getCountryShortName() {
    return countryShortName;
  }

  /**
   * Sets the country short name.
   *
   * @param countryShortName the new country short name
   */
  public void setCountryShortName(String countryShortName) {
    this.countryShortName = countryShortName;
  }

  /**
   * Gets the tel prefix.
   *
   * @return the tel prefix
   */
  public String getTelPrefix() {
    return telPrefix;
  }

  /**
   * Sets the tel prefix.
   *
   * @param telPrefix the new tel prefix
   */
  public void setTelPrefix(String telPrefix) {
    this.telPrefix = telPrefix;
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
   * Gets the country id.
   *
   * @return the country id
   */
  public CountryEntity getCountryId() {
    return countryId;
  }

  /**
   * Sets the country id.
   *
   * @param countryId the new country id
   */
  public void setCountryId(CountryEntity countryId) {
    this.countryId = countryId;
  }

  /**
   * Gets the active.
   *
   * @return the active
   */
  public Boolean getActive() {
    return active;
  }

  /**
   * Sets the active.
   *
   * @param active the new active
   */
  public void setActive(Boolean active) {
    this.active = active;
  }

}
