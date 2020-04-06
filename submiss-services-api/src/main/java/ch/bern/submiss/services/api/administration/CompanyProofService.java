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

package ch.bern.submiss.services.api.administration;

import java.util.List;

import ch.bern.submiss.services.api.dto.CompanyProofDTO;
import ch.bern.submiss.services.api.dto.ProofHistoryDTO;

/**
 * The Interface CompanyProofService.
 */
public interface CompanyProofService {

  /**
   * Removes the given proof from companies who have it, if the proof country property has changed.
   *
   * @param proofHistoryDTO the proof history DTO
   */
  void removeProofFromCompanies(ProofHistoryDTO proofHistoryDTO);

  /**
   * Assigns the given proof to companies who have the same country property as the proof.
   *
   * @param proofHistoryDTO the proof history DTO
   */
  void addProofToCompanies(ProofHistoryDTO proofHistoryDTO);

  /**
   * Updates the required property of the company proof.
   *
   * @param proofHistoryDTO the proof history DTO
   */
  void updateRequiredPropertyOfCompanyProof(ProofHistoryDTO proofHistoryDTO);
  
  /**
   * Update inactive proof of companies.
   *
   * @param proofHistoryDTO the proof history DTO
   */
  void updateInactiveCompanyProofs(ProofHistoryDTO proofHistoryDTO);

  /**
   * Update hasChanged value for every company proof DTO.
   *
   * @param companyProofDTOs the company proof DTOs
   * @param companyId the company id
   * @return updated company proofs
   */
  void updateHasChangedValues(List<CompanyProofDTO> companyProofDTOs,
      String companyId);
  
  /**
   * Removes the default proofs.
   *
   * @param proofHistoryDTO the proof history DTO
   */
  void removeDefaultProofs(ProofHistoryDTO proofHistoryDTO);
}
