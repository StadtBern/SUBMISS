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

import ch.bern.submiss.services.api.dto.ProofProvidedMapDTO;
import ch.bern.submiss.services.api.dto.SubmissionDTO;
import ch.bern.submiss.services.api.dto.SubmittentDTO;
import java.util.List;

import ch.bern.submiss.services.api.dto.MasterListTypeDataDTO;
import ch.bern.submiss.services.api.dto.ProofHistoryDTO;


public interface SDProofService {

  /**
   * Proof history entities to master list type data.
   *
   * @return the type data list
   */
  List<MasterListTypeDataDTO> proofToTypeData();

  /**
   * Gets the proof history entry by id.
   *
   * @param entryId the entry id
   * @return the proof history entry
   */
  ProofHistoryDTO getProofEntryById(String entryId);

  /**
   * Checks if the country and proof name combination is unique.
   *
   * @param proofId the proof id
   * @param proofName the proof name
   * @param countryId the country id
   * @return true, if the combination is unique
   */
  boolean isProofNameAndCountryUnique(String proofId, String proofName, String countryId);
  
  /**
   * Gets all current proof entries for the given tenant id. If no tenant id given, then all current
   * proof entries for all tenants are returned.
   *
   * @param tenantId the tenant id
   * @return a list of all current proof entries
   */
  List<ProofHistoryDTO> getAllCurrentProofEntries(String tenantId);

  /**
   * Save the proofs history entry.
   *
   * @param proofHistoryDTO the proof history DTO
   */
  void saveProofsEntry(ProofHistoryDTO proofHistoryDTO);

  /**
   * Gets the all proof entries.
   *
   * @param tenantId the tenant id
   * @return the all proof entries
   */
  List<ProofHistoryDTO> getAllProofEntries(String tenantId);


  /**
   * Gets the most recentd proof entries.
   *
   * @param tenantId the tenant id
   * @return the most recentd proof entries
   */
  List<ProofHistoryDTO> getMostRecentdProofEntries(String tenantId);
  
  /**
   * Checks if is first country proof.
   *
   * @param countryId the country id
   * @return true, if is first country proof
   */
  boolean isFirstCountryProof(String countryId);

  /**
   * Checks the proof provided values for inconsistencies.
   *
   * @param submissionDTO the submission DTO
   * @param proofProvidedMapDTOs the proof provided map DTOs
   * @return true if the saved proof provided values differ from the current proof provided values.
   */
  boolean checkProofsForInconsistencies(SubmissionDTO submissionDTO,
    List<ProofProvidedMapDTO> proofProvidedMapDTOs);

  /**
   * Deletes the submittent proof provided entries of a given company.
   *
   * @param submittentDTO the submittent DTO
   * @param companyId the company id
   * @param isSubmittent true if the given company is the submittent
   */
  void deleteSubmittentProofProvidedEntries(SubmittentDTO submittentDTO, String companyId,
    boolean isSubmittent);
}
