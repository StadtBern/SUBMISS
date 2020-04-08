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

import ch.bern.submiss.services.api.dto.CountryDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Class CountryHistoryForm.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryHistoryForm extends AbstractForm {

  /** The country name. */
  private String countryName;

  /** The country short name. */
  private String countryShortName;

  /** The tel prefix. */
  private String telPrefix;

  /** The country id. */
  private CountryDTO countryId;

  /** The active. */
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
   * Gets the country id.
   *
   * @return the country id
   */
  public CountryDTO getCountryId() {
    return countryId;
  }

  /**
   * Sets the country id.
   *
   * @param countryId the new country id
   */
  public void setCountryId(CountryDTO countryId) {
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
