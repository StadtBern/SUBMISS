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

import java.util.Date;
import java.util.List;

import ch.bern.submiss.services.api.dto.LegalHearingExclusionDTO;
import ch.bern.submiss.services.api.dto.LegalHearingTerminateDTO;

/**
 * The Interface LegalHearingService.
 */
public interface LegalHearingService {

  /**
   * Update legal hearing termination.
   *
   * @param legalHearingTerminate the legal hearing terminate
   * @param submissionId 
   */
  void updateLegalHearingTermination(LegalHearingTerminateDTO legalHearingTerminate, String submissionId);

  /**
   * Gets the submission legal hearing termination.
   *
   * @param submissionId the submission id
   * @return the submission legal hearing termination
   */
  LegalHearingTerminateDTO getSubmissionLegalHearingTermination(String submissionId);
  
  /**
   * Gets the excluded submittents.
   *
   * @param submissionId the submission id
   * @return the excluded submittents
   */
  List<LegalHearingExclusionDTO> getExcludedSubmittents(String submissionId);
  
  /**
   * Adds the excluded submittent.
   *
   * @param submittentId the submittent id
   */
  void addExcludedSubmittent(String submittentId);

  /**
   * Update excluded submittent.
   *
   * @param legalHearingExclusionDTO the legal hearing exclusion DTO
   * @param exclusionDate the exclusion date
   * @param submissionId 
   * @param firstLevelExclusionDate 
   */
  void updateExcludedSubmittent(List<LegalHearingExclusionDTO> legalHearingExclusionDTO,
      Date exclusionDate, String submissionId, Date firstLevelExclusionDate);

  /**
   * Gets the exclusion deadline.
   *
   * @param submissionId the submission id
   * @return the exclusion deadline
   */
  Date getExclusionDeadline(String submissionId);
  
  /**
   * Retrieve latest submittent exclusion deadline.
   *
   * @param submittentId the submittent id
   * @param level the level
   * @return the date
   */
  Date retrieveLatestSubmittentExclusionDeadline(String submittentId, Integer level);

  /**
   * Adds the excluded applicant.
   *
   * @param applicantId the applicant id
   */
  void addExcludedApplicant(String applicantId);
  
  /**
   * Gets the legal hearing exclusion by submittent and level.
   *
   * @param submittentId the submittent id
   * @param levels the levels
   * @return the legal hearing exclusion by submittent and level
   */
  LegalHearingExclusionDTO getLegalHearingExclusionBySubmittentAndLevel(String submittentId, List<Integer> levels);
}
