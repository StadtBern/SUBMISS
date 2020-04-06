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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * The Class CommissionProcurementProposalForm.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommissionProcurementProposalForm {

  /** The date. */
  private Date date;

  /** The business. */
  private BigDecimal business;

  /** The object. */
  private String object;

  /** The suitability audit dropdown. */
  private String suitabilityAuditDropdown;

  /** The suitability audit text. */
  private String suitabilityAuditText;

  /** The pre remarks. */
  private String preRemarks;

  /** The reservation. */
  private String reservation;

  /** The reason given. */
  private String reasonGiven;

  /** The award recipients. */
  private List<OfferForm> awardRecipients;

  /** The invalid business. */
  private boolean invalidBusiness;

  /**
   * Gets the date.
   *
   * @return the date
   */
  public Date getDate() {
    return date;
  }

  /**
   * Sets the date.
   *
   * @param date the new date
   */
  public void setDate(Date date) {
    this.date = date;
  }

  /**
   * Gets the business.
   *
   * @return the business
   */
  public BigDecimal getBusiness() {
    return business;
  }

  /**
   * Sets the business.
   *
   * @param business the new business
   */
  public void setBusiness(BigDecimal business) {
    this.business = business;
  }

  /**
   * Gets the object.
   *
   * @return the object
   */
  public String getObject() {
    return object;
  }

  /**
   * Sets the object.
   *
   * @param object the new object
   */
  public void setObject(String object) {
    this.object = object;
  }

  /**
   * Gets the suitability audit dropdown.
   *
   * @return the suitability audit dropdown
   */
  public String getSuitabilityAuditDropdown() {
    return suitabilityAuditDropdown;
  }

  /**
   * Sets the suitability audit dropdown.
   *
   * @param suitabilityAuditDropdown the new suitability audit dropdown
   */
  public void setSuitabilityAuditDropdown(String suitabilityAuditDropdown) {
    this.suitabilityAuditDropdown = suitabilityAuditDropdown;
  }

  /**
   * Gets the suitability audit text.
   *
   * @return the suitability audit text
   */
  public String getSuitabilityAuditText() {
    return suitabilityAuditText;
  }

  /**
   * Sets the suitability audit text.
   *
   * @param suitabilityAuditText the new suitability audit text
   */
  public void setSuitabilityAuditText(String suitabilityAuditText) {
    this.suitabilityAuditText = suitabilityAuditText;
  }

  /**
   * Gets the pre remarks.
   *
   * @return the pre remarks
   */
  public String getPreRemarks() {
    return preRemarks;
  }

  /**
   * Sets the pre remarks.
   *
   * @param preRemarks the new pre remarks
   */
  public void setPreRemarks(String preRemarks) {
    this.preRemarks = preRemarks;
  }

  /**
   * Gets the reservation.
   *
   * @return the reservation
   */
  public String getReservation() {
    return reservation;
  }

  /**
   * Sets the reservation.
   *
   * @param reservation the new reservation
   */
  public void setReservation(String reservation) {
    this.reservation = reservation;
  }

  /**
   * Gets the reason given.
   *
   * @return the reason given
   */
  public String getReasonGiven() {
    return reasonGiven;
  }

  /**
   * Sets the reason given.
   *
   * @param reasonGiven the new reason given
   */
  public void setReasonGiven(String reasonGiven) {
    this.reasonGiven = reasonGiven;
  }

  /**
   * Gets the award recipients.
   *
   * @return the award recipients
   */
  public List<OfferForm> getAwardRecipients() {
    return awardRecipients;
  }

  /**
   * Sets the award recipients.
   *
   * @param awardRecipients the new award recipients
   */
  public void setAwardRecipients(List<OfferForm> awardRecipients) {
    this.awardRecipients = awardRecipients;
  }

  /**
   * Checks if is invalid business.
   *
   * @return true, if is invalid business
   */
  public boolean isInvalidBusiness() {
    return invalidBusiness;
  }

  /**
   * Sets the invalid business.
   *
   * @param invalidBusiness the new invalid business
   */
  public void setInvalidBusiness(boolean invalidBusiness) {
    this.invalidBusiness = invalidBusiness;
  }
}
