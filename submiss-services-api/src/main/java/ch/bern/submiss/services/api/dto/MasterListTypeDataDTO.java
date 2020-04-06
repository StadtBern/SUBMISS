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

import ch.bern.submiss.services.api.constants.EmailTemplate;

/**
 * The Class MasterListTypeDataDTO.
 */
public class MasterListTypeDataDTO {

  /** The id. */
  private String id;

  /** The description. */
  private String description;

  /** The added description. */
  private String addedDescription;

  /** The is active. */
  private Boolean isActive;

  /** The directorate name. */
  private String directorateName;

  /** The location. */
  private String location;

  /** The address. */
  private String address;
  
  /** The tel prefix. */
  private String telPrefix;
  
  /** The worker number. */
  private Integer workerNumber;
  
  /** The men number. */
  private Integer menNumber;
  
  /** The women number. */
  private Integer womenNumber;
  
  /** The proof order. */
  private Integer proofOrder;
  
  /** The required. */
  private Boolean required;
  
  /** The validity period. */
  private Integer validityPeriod;
  
  /** The country name. */
  private String countryName;
  
  /** The available part. */
  private EmailTemplate.AVAILABLE_PART availablePart;

  /** The short code. */
  private String shortCode;

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
   * Gets the checks if is active.
   *
   * @return the checks if is active
   */
  public Boolean getIsActive() {
    return isActive;
  }

  /**
   * Sets the checks if is active.
   *
   * @param isActive the new checks if is active
   */
  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }

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
   * Gets the added description.
   *
   * @return the added description
   */
  public String getAddedDescription() {
    return addedDescription;
  }

  /**
   * Sets the added description.
   *
   * @param addedDescription the new added description
   */
  public void setAddedDescription(String addedDescription) {
    this.addedDescription = addedDescription;
  }

  /**
   * Gets the directorate name.
   *
   * @return the directorate name
   */
  public String getDirectorateName() {
    return directorateName;
  }

  /**
   * Sets the directorate name.
   *
   * @param directorateName the new directorate name
   */
  public void setDirectorateName(String directorateName) {
    this.directorateName = directorateName;
  }

  /**
   * Gets the location.
   *
   * @return the location
   */
  public String getLocation() {
    return location;
  }

  /**
   * Sets the location.
   *
   * @param location the new location
   */
  public void setLocation(String location) {
    this.location = location;
  }

  /**
   * Gets the address.
   *
   * @return the address
   */
  public String getAddress() {
    return address;
  }

  /**
   * Sets the address.
   *
   * @param address the new address
   */
  public void setAddress(String address) {
    this.address = address;
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
   * Gets the worker number.
   *
   * @return the worker number
   */
  public Integer getWorkerNumber() {
    return workerNumber;
  }

  /**
   * Sets the worker number.
   *
   * @param workerNumber the new worker number
   */
  public void setWorkerNumber(Integer workerNumber) {
    this.workerNumber = workerNumber;
  }

  /**
   * Gets the men number.
   *
   * @return the men number
   */
  public Integer getMenNumber() {
    return menNumber;
  }

  /**
   * Sets the men number.
   *
   * @param menNumber the new men number
   */
  public void setMenNumber(Integer menNumber) {
    this.menNumber = menNumber;
  }

  /**
   * Gets the women number.
   *
   * @return the women number
   */
  public Integer getWomenNumber() {
    return womenNumber;
  }

  /**
   * Sets the women number.
   *
   * @param womenNumber the new women number
   */
  public void setWomenNumber(Integer womenNumber) {
    this.womenNumber = womenNumber;
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
   * Gets the available part.
   *
   * @return the available part
   */
  public EmailTemplate.AVAILABLE_PART getAvailablePart() {
    return availablePart;
  }

  /**
   * Sets the available part.
   *
   * @param availablePart the new available part
   */
  public void setAvailablePart(EmailTemplate.AVAILABLE_PART availablePart) {
    this.availablePart = availablePart;
  }

  /**
   * Gets the short code.
   *
   * @return the short code
   */
  public String getShortCode() {
    return shortCode;
  }

  /**
   * Sets the short code.
   *
   * @param shortCode the new short code
   */
  public void setShortCode(String shortCode) {
    this.shortCode = shortCode;
  }
}
