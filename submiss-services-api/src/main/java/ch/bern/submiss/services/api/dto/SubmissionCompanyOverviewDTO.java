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
 * The Class SubmissionCompanyOverviewDTO.
 */
public class SubmissionCompanyOverviewDTO {

  /** The name. */
  private String name;
  
  /** The location. */
  private String location;
  
  /** The company remarks. */
  private String companyRemarks;
  
  /** The proof status. */
  private String proofStatus;
  
  /** The all proof statuses. */
  private String allProofStatuses;
  
  /** The company partnership desc. */
  private String companyPartnershipDesc;
  
  /**
   * Gets the name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name.
   *
   * @param name the new name
   */
  public void setName(String name) {
    this.name = name;
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
   * Gets the proof status.
   *
   * @return the proof status
   */
  public String getProofStatus() {
    return proofStatus;
  }
  
  /**
   * Sets the proof status.
   *
   * @param proofStatus the new proof status
   */
  public void setProofStatus(String proofStatus) {
    this.proofStatus = proofStatus;
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
  
  /**
   * Gets the company partnership desc.
   *
   * @return the company partnership desc
   */
  public String getCompanyPartnershipDesc() {
    return companyPartnershipDesc;
  }

  /**
   * Sets the company partnership desc.
   *
   * @param companyPartnershipDesc the new company partnership desc
   */
  public void setCompanyPartnershipDesc(String companyPartnershipDesc) {
    this.companyPartnershipDesc = companyPartnershipDesc;
  }
  
}
