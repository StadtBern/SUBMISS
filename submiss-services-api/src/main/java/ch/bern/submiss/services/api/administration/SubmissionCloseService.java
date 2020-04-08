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

import ch.bern.submiss.services.api.dto.AwardInfoDTO;
import ch.bern.submiss.services.api.dto.AwardInfoFirstLevelDTO;
import ch.bern.submiss.services.api.dto.SubmissionDTO;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

/**
 * The Interface SubmissionCloseService.
 */
public interface SubmissionCloseService {
  
  /**
   * Checks if at least one amount of one offer of the submission is above threshold.
   *
   * @param submissionId the id of the submission to be checked
   * @return a boolean indicating if at least one amount of one offer of the submission is above threshold
   */
  Boolean isOfferAboveThreshold(String submissionId);
  
  /**
   * Reopens a closed or cancelled submission, so sets the status of the submission:
   * in case of process open, invitation, selective to COMMISSION_PROCUREMENT_DECISION_CLOSED
   * in case of process negotiated (+ with competition) below threshold with status before closure
   * after FORMAL_AUDIT_COMPLETED to FORMAL_AUDIT_COMPLETED
   * in case of process negotiated (+ with competition) below threshold with status before closure
   * before FORMAL_AUDIT_COMPLETED to the status before closure
   * in case of process negotiated (+ with competition) above threshold with status before closure
   * after COMMISSION_PROCUREMENT_DECISION_CLOSED to COMMISSION_PROCUREMENT_DECISION_CLOSED
   * in case of process negotiated (+ with competition) above threshold with status before closure
   * before COMMISSION_PROCUREMENT_DECISION_CLOSED to the status before closure
   * in case of status cancelled before closure (or no closure at all, just reopen after cancel) to the status before cancel
   *
   * @param submissionId the id of the submission to be checked
   * @param reopenReason the reopen reason given by the user
   */
  void reopenSubmission(String submissionId, String reopenReason);

  /**
   * Gets the date status AWARD_NOTICES_CREATED is set, if it is one of the two last statuses of the submission,
   * null otherwise.
   *
   * @param submissionId the id of the submission to be checked
   * @return the date status AWARD_NOTICES_CREATED is set, if one of the two last statuses of the submission,
   * null otherwise
   */
  Timestamp getAwardNoticesCreatedDateForClose(String submissionId);
 
  /**
   * Gets the award info given a submission id.
   *
   * @param submissionId the submission id
   * @return the award Info DTO
   */
  AwardInfoDTO getAwardInfo(String submissionId);

  /**
   * Creates the award info entry.
   *
   * @param awardInfoDTO the award Info DTO
   * @return the error
   */
  Set<ValidationError> createAwardInfo(AwardInfoDTO awardInfoDTO);

  /**
   * Updates the award info entry.
   *
   * @param awardInfoDTO the award Info DTO
   * @return the error
   */
  Set<ValidationError> updateAwardInfo(AwardInfoDTO awardInfoDTO);
  
  /**
   * Gets the award info first level given a submission id.
   *
   * @param submissionId the submission id
   * @return the award Info first level DTO
   */
  AwardInfoFirstLevelDTO getAwardInfoFirstLevel(String submissionId);

  /**
   * Creates the award info first level entry.
   *
   * @param awardInfoDTO the award Info first level DTO
   * @return the error
   */
  Set<ValidationError> createAwardInfoFirstLevel(AwardInfoFirstLevelDTO awardInfoDTO);

  /**
   * Updates the award info first level.
   *
   * @param awardInfoDTO the award Info first level DTO
   * @return the error
   */
  Set<ValidationError> updateAwardInfoFirstLevel(AwardInfoFirstLevelDTO awardInfoDTO);
  
  /**
   * Finds all submissions with 
   * current status AWARD_NOTICES_CREATED or CONTRACT_CREATED
   * status AWARD_NOTICES_CREATED (being the last status in case current status is AWARD_NOTICES_CREATED or being the
   * one before last status in case current status is CONTRACT_CREATED) set more than 20 days ago
   * field gattTwo set to true
   * field publicationDateAward empty.
   * 
   * @return a list of submission
   */
  List<SubmissionDTO> getSubmissionsNoPublicationAwardDate();

  /**
   * Finds all submissions with current status not AWARD_NOTICES_CREATED, CONTRACT_CREATED, 
   * PROCEDURE_COMPLETED, PROCEDURE_CANCELED and without any status change in the last 6 months
   * 
   * @return a list of submission
   */
  List<SubmissionDTO> getSubmissionsNotClosed();
}
