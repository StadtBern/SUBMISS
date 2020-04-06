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

import java.util.Arrays;

/**
 * The Class TenantDTO.
 */
public class TenantDTO extends AbstractDTO {

  /** The is main. */
  private Boolean isMain;

  /** The description. */
  private String description;

  /** The email. */
  private String email;

  /** The phone. */
  private String phone;

  /** The logo. */
  private byte[] logo;

  /**
   * Gets the checks if is main.
   *
   * @return the checks if is main
   */
  public Boolean getIsMain() {
    return isMain;
  }

  /**
   * Sets the checks if is main.
   *
   * @param isMain the new checks if is main
   */
  public void setIsMain(Boolean isMain) {
    this.isMain = isMain;
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
   * Gets the phone.
   *
   * @return the phone
   */
  public String getPhone() {
    return phone;
  }

  /**
   * Sets the phone.
   *
   * @param phone the new phone
   */
  public void setPhone(String phone) {
    this.phone = phone;
  }

  /**
   * Gets the logo.
   *
   * @return the logo
   */
  public byte[] getLogo() {
    return logo;
  }

  /**
   * Sets the logo.
   *
   * @param logo the new logo
   */
  public void setLogo(byte[] logo) {
    this.logo = logo;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "TenantDTO [isMain=" + isMain + ", description=" + description + ", email=" + email
        + ", phone=" + phone + ", logo=" + Arrays.toString(logo) + "]";
  }

}
