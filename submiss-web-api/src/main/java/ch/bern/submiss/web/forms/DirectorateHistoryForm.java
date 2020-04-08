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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ch.bern.submiss.services.api.dto.DirectorateDTO;

/**
 * The Class DirectorateHistoryForm.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DirectorateHistoryForm extends AbstractStammdatenForm {

  /** The active. */
  private boolean active;

  /** The short name. */
  private String shortName;

  /** The directorate id. */
  private DirectorateDTO directorateId;

  /** The address. */
  private String address;

  /** The telephone. */
  private String telephone;

  /** The email. */
  private String email;

  /** The website. */
  private String website;

  /** The post code. */
  private String postCode;

  /** The location. */
  private String location;

  /** The fax. */
  private String fax;

  /** The tenant. */
  private TenantForm tenant;

  /**
   * Checks if is active.
   *
   * @return true, if is active
   */
  public boolean isActive() {
    return active;
  }

  /**
   * Sets the active.
   *
   * @param active the new active
   */
  public void setActive(boolean active) {
    this.active = active;
  }

  /**
   * Gets the directorate id.
   *
   * @return the directorate id
   */
  public DirectorateDTO getDirectorateId() {
    return directorateId;
  }

  /**
   * Sets the directorate id.
   *
   * @param directorateId the new directorate id
   */
  public void setDirectorateId(DirectorateDTO directorateId) {
    this.directorateId = directorateId;
  }

  /**
   * Gets the short name.
   *
   * @return the short name
   */
  public String getShortName() {
    return shortName;
  }

  /**
   * Sets the short name.
   *
   * @param shortName the new short name
   */
  public void setShortName(String shortName) {
    this.shortName = shortName;
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
   * Gets the telephone.
   *
   * @return the telephone
   */
  public String getTelephone() {
    return telephone;
  }

  /**
   * Sets the telephone.
   *
   * @param telephone the new telephone
   */
  public void setTelephone(String telephone) {
    this.telephone = telephone;
  }

  /**
   * Gets the email.
   *
   * @return the email
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets the email.
   *
   * @param email the new email
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Gets the website.
   *
   * @return the website
   */
  public String getWebsite() {
    return website;
  }

  /**
   * Sets the website.
   *
   * @param website the new website
   */
  public void setWebsite(String website) {
    this.website = website;
  }

  /**
   * Gets the post code.
   *
   * @return the post code
   */
  public String getPostCode() {
    return postCode;
  }

  /**
   * Sets the post code.
   *
   * @param postCode the new post code
   */
  public void setPostCode(String postCode) {
    this.postCode = postCode;
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
   * Gets the fax.
   *
   * @return the fax
   */
  public String getFax() {
    return fax;
  }

  /**
   * Sets the fax.
   *
   * @param fax the new fax
   */
  public void setFax(String fax) {
    this.fax = fax;
  }

  /**
   * Gets the tenant.
   *
   * @return the tenant
   */
  public TenantForm getTenant() {
    return tenant;
  }

  /**
   * Sets the tenant.
   *
   * @param tenant the new tenant
   */
  public void setTenant(TenantForm tenant) {
    this.tenant = tenant;
  }

}
