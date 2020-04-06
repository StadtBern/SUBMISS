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

import java.util.List;

/**
 * The Class SubmissionOverviewDTO.
 */
public class SuitabilityDocumentDTO {

  /** The company name. */
  private String companyName;
  
  /** The joint ventures. */
  private String jointVentures;
  
  /** The sub contractors. */
  private String subContractors;
  
  /** The proof status. */
  private int proofStatus;
  
  /** The offer notes. */
  private String offerNotes;

  /** The offer. */
  private OfferDTO offer;
  
  /** The offer criteria. */
  private List<OfferCriterionDTO> offerCriteria;
  
  /** The formal examination fulfilled. */
  private String formalExaminationFulfilled;
  
  /** The exists exclusion reasons. */
  private String existsExclusionReasons;
  
  /** The q ex examination is fulfilled. */
  private String qExExaminationIsFulfilled;
  
  private String mussCriterienSummary;
  
  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public String getJointVentures() {
    return jointVentures;
  }

  public void setJointVentures(String jointVentures) {
    this.jointVentures = jointVentures;
  }

  public String getSubContractors() {
    return subContractors;
  }

  public void setSubContractors(String subContractors) {
    this.subContractors = subContractors;
  }

  public int getProofStatus() {
    return proofStatus;
  }

  public void setProofStatus(int proofStatus) {
    this.proofStatus = proofStatus;
  }

  public String getOfferNotes() {
    return offerNotes;
  }

  public void setOfferNotes(String offerNotes) {
    this.offerNotes = offerNotes;
  }

  public List<OfferCriterionDTO> getOfferCriteria() {
    return offerCriteria;
  }

  public void setOfferCriteria(List<OfferCriterionDTO> offerCriteria) {
    this.offerCriteria = offerCriteria;
  }

  public OfferDTO getOffer() {
    return offer;
  }

  public void setOffer(OfferDTO offer) {
    this.offer = offer;
  }

  public String getFormalExaminationFulfilled() {
    return formalExaminationFulfilled;
  }

  public void setFormalExaminationFulfilled(String formalExaminationFulfilled) {
    this.formalExaminationFulfilled = formalExaminationFulfilled;
  }

  public String getExistsExclusionReasons() {
    return existsExclusionReasons;
  }

  public void setExistsExclusionReasons(String existsExclusionReasons) {
    this.existsExclusionReasons = existsExclusionReasons;
  }

  public String getqExExaminationIsFulfilled() {
    return qExExaminationIsFulfilled;
  }

  public void setqExExaminationIsFulfilled(String qExExaminationIsFulfilled) {
    this.qExExaminationIsFulfilled = qExExaminationIsFulfilled;
  }

  public String getMussCriterienSummary() {
    return mussCriterienSummary;
  }

  public void setMussCriterienSummary(String mussCriterienSummary) {
    this.mussCriterienSummary = mussCriterienSummary;
  }
  
}
