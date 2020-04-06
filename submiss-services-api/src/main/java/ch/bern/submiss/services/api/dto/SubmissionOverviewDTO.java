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

import java.util.Set;

/**
 * The Class SubmissionOverviewDTO.
 */
public class SubmissionOverviewDTO {

  /** The submittent. */
  private SubmittentDTO submittent;

  /** The offer. */
  private OfferDTO offer;

  /** The submittent ARGE sub contractors. */
  private String submittentARGESubContractors;

  /** The all companies. */
  private Set<CompanyDTO> allCompanies;

  /** The has partners. */
  private boolean hasPartners;

  /** The partners overview. */
  private Set<SubmissionCompanyOverviewDTO> partnersOverview;

  /** The price percentage. */
  private String pricePercentage;

  /** The offer amount. */
  private String offerAmount;

  /** The operating offer amount. */
  private String operatingOfferAmount;

  /** The rank. */
  private int rank;
  
  /** The status. */
  private String statusOfCompanies;

  /** The status. */
  private String operatingCostNotes;
  
  /** The status. */
  private String companyRemarks;
  
  /** The all proof statuses. */
  private String allProofStatuses;
  
  /**
   * Gets the all companies.
   *
   * @return the all companies
   */
  public Set<CompanyDTO> getAllCompanies() {
    return allCompanies;
  }

  /**
   * Sets the all companies.
   *
   * @param allCompanies the new all companies
   */
  public void setAllCompanies(Set<CompanyDTO> allCompanies) {
    this.allCompanies = allCompanies;
  }

  /**
   * Gets the submittent.
   *
   * @return the submittent
   */
  public SubmittentDTO getSubmittent() {
    return submittent;
  }

  /**
   * Sets the submittent.
   *
   * @param submittent the new submittent
   */
  public void setSubmittent(SubmittentDTO submittent) {
    this.submittent = submittent;
  }

  /**
   * Gets the offer.
   *
   * @return the offer
   */
  public OfferDTO getOffer() {
    return offer;
  }

  /**
   * Sets the offer.
   *
   * @param offer the new offer
   */
  public void setOffer(OfferDTO offer) {
    this.offer = offer;
  }

  /**
   * Gets the submittent ARGE sub contractors.
   *
   * @return the submittent ARGE sub contractors
   */
  public String getSubmittentARGESubContractors() {
    return submittentARGESubContractors;
  }

  /**
   * Sets the submittent ARGE sub contractors.
   *
   * @param submittentARGESubContractors the new submittent ARGE sub contractors
   */
  public void setSubmittentARGESubContractors(String submittentARGESubContractors) {
    this.submittentARGESubContractors = submittentARGESubContractors;
  }

  /**
   * Checks if is checks for partners.
   *
   * @return true, if is checks for partners
   */
  public boolean isHasPartners() {
    return hasPartners;
  }

  /**
   * Sets the checks for partners.
   *
   * @param hasPartners the new checks for partners
   */
  public void setHasPartners(boolean hasPartners) {
    this.hasPartners = hasPartners;
  }

  /**
   * Gets the partners overview.
   *
   * @return the partners overview
   */
  public Set<SubmissionCompanyOverviewDTO> getPartnersOverview() {
    return partnersOverview;
  }

  /**
   * Sets the partners overview.
   *
   * @param partnersOverview the new partners overview
   */
  public void setPartnersOverview(Set<SubmissionCompanyOverviewDTO> partnersOverview) {
    this.partnersOverview = partnersOverview;
  }

  /**
   * Gets the price percentage.
   *
   * @return the price percentage
   */
  public String getPricePercentage() {
    return pricePercentage;
  }

  /**
   * Sets the price percentage.
   *
   * @param pricePercentage the new price percentage
   */
  public void setPricePercentage(String pricePercentage) {
    this.pricePercentage = pricePercentage;
  }

  /**
   * Gets the offer amount.
   *
   * @return the offer amount
   */
  public String getOfferAmount() {
    return offerAmount;
  }

  /**
   * Sets the offer amount.
   *
   * @param offerAmount the new offer amount
   */
  public void setOfferAmount(String offerAmount) {
    this.offerAmount = offerAmount;
  }

  /**
   * Gets the operating offer amount.
   *
   * @return the operating offer amount
   */
  public String getOperatingOfferAmount() {
    return operatingOfferAmount;
  }

  /**
   * Sets the operating offer amount.
   *
   * @param operatingOfferAmount the new operating offer amount
   */
  public void setOperatingOfferAmount(String operatingOfferAmount) {
    this.operatingOfferAmount = operatingOfferAmount;
  }

  /**
   * Gets the rank.
   *
   * @return the rank
   */
  public int getRank() {
    return rank;
  }

  /**
   * Sets the rank.
   *
   * @param rank the new rank
   */
  public void setRank(int rank) {
    this.rank = rank;
  }

  /**
   * Gets the status.
   *
   * @return the status
   */
  public String getStatusOfCompanies() {
    return statusOfCompanies;
  }

  /**
   * Sets the status.
   *
   * @param statusOfCompanies the new status of companies
   */
  public void setStatusOfCompanies(String statusOfCompanies) {
    this.statusOfCompanies = statusOfCompanies;
  }

  /**
   * Gets the operating cost notes.
   *
   * @return the operating cost notes
   */
  public String getOperatingCostNotes() {
    return operatingCostNotes;
  }

  /**
   * Sets the operating cost notes.
   *
   * @param operatingCostNotes the new operating cost notes
   */
  public void setOperatingCostNotes(String operatingCostNotes) {
    this.operatingCostNotes = operatingCostNotes;
  }

  /**
   * Gets the company remarks.
   *
   * @return the company remarks
   */
  public String getCompanyRemarks() {
    return companyRemarks;
  }

  /**
   * Sets the company remarks.
   *
   * @param companyRemarks the new company remarks
   */
  public void setCompanyRemarks(String companyRemarks) {
    this.companyRemarks = companyRemarks;
  }

  /**
   * Gets the all proof statuses.
   *
   * @return the all proof statuses
   */
  public String getAllProofStatuses() {
    return allProofStatuses;
  }

  /**
   * Sets the all proof statuses.
   *
   * @param allProofStatuses the new all proof statuses
   */
  public void setAllProofStatuses(String allProofStatuses) {
    this.allProofStatuses = allProofStatuses;
  }

}
