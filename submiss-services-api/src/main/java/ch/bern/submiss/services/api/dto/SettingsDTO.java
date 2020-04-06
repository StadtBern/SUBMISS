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

/**
 * The Class SettingsDTO.
 */
public class SettingsDTO {

  /** The contact location. */
  private String contactLocation;

  /** The contact telephone. */
  private String contactTelephone;

  /** The contact email. */
  private String contactEmail;

  /** The support. */
  private String support;

  /** The tech telephone. */
  private String techTelephone;

  /** The tech email. */
  private String techEmail;

  /** The ip printer address. */
  private String ipPrinterAddress;
  
  /** The ip printer port. */
  private String ipPrinterPort;
  /**
   * Gets the contact location.
   *
   * @return the contact location
   */
  public String getContactLocation() {
    return contactLocation;
  }

  /**
   * Sets the contact location.
   *
   * @param contactLocation the new contact location
   */
  public void setContactLocation(String contactLocation) {
    this.contactLocation = contactLocation;
  }

  /**
   * Gets the contact telephone.
   *
   * @return the contact telephone
   */
  public String getContactTelephone() {
    return contactTelephone;
  }

  /**
   * Sets the contact telephone.
   *
   * @param contactTelephone the new contact telephone
   */
  public void setContactTelephone(String contactTelephone) {
    this.contactTelephone = contactTelephone;
  }

  /**
   * Gets the contact email.
   *
   * @return the contact email
   */
  public String getContactEmail() {
    return contactEmail;
  }

  /**
   * Sets the contact email.
   *
   * @param contactEmail the new contact email
   */
  public void setContactEmail(String contactEmail) {
    this.contactEmail = contactEmail;
  }

  /**
   * Gets the support.
   *
   * @return the support
   */
  public String getSupport() {
    return support;
  }

  /**
   * Sets the support.
   *
   * @param support the new support
   */
  public void setSupport(String support) {
    this.support = support;
  }

  /**
   * Gets the tech telephone.
   *
   * @return the tech telephone
   */
  public String getTechTelephone() {
    return techTelephone;
  }

  /**
   * Sets the tech telephone.
   *
   * @param techTelephone the new tech telephone
   */
  public void setTechTelephone(String techTelephone) {
    this.techTelephone = techTelephone;
  }

  /**
   * Gets the tech email.
   *
   * @return the tech email
   */
  public String getTechEmail() {
    return techEmail;
  }

  /**
   * Sets the tech email.
   *
   * @param techEmail the new tech email
   */
  public void setTechEmail(String techEmail) {
    this.techEmail = techEmail;
  }

  /**
   * Gets the ip printer address.
   *
   * @return the ip printer address
   */
  public String getIpPrinterAddress() {
    return ipPrinterAddress;
  }

  /**
   * Sets the ip printer address.
   *
   * @param ipPrinterAddress the new ip printer address
   */
  public void setIpPrinterAddress(String ipPrinterAddress) {
    this.ipPrinterAddress = ipPrinterAddress;
  }

  /**
   * Gets the ip printer port.
   *
   * @return the ip printer port
   */
  public String getIpPrinterPort() {
    return ipPrinterPort;
  }

  /**
   * Sets the ip printer port.
   *
   * @param ipPrinterPort the new ip printer port
   */
  public void setIpPrinterPort(String ipPrinterPort) {
    this.ipPrinterPort = ipPrinterPort;
  }


}
