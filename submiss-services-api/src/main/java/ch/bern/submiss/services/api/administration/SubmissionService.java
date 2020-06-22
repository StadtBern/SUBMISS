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

import ch.bern.submiss.services.api.constants.Process;
import ch.bern.submiss.services.api.dto.CommissionProcurementDecisionDTO;
import ch.bern.submiss.services.api.dto.CompanyDTO;
import ch.bern.submiss.services.api.dto.ProofProvidedMapDTO;
import ch.bern.submiss.services.api.dto.SignatureProcessTypeDTO;
import ch.bern.submiss.services.api.dto.SubmissionDTO;
import ch.bern.submiss.services.api.dto.SubmittentDTO;
import ch.bern.submiss.services.api.dto.SubmittentOfferDTO;
import ch.bern.submiss.services.api.dto.TenderStatusHistoryDTO;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

/**
 * The Interface SubmissionService.
 */
public interface SubmissionService {

  /**
   * Creates a submission.
   *
   * @param submissionDTO the submission to be created
   * @return the UUID of the created submission
   */
  String createSubmission(SubmissionDTO submissionDTO);

  /**
   * Finds all submissions that belong to a project.
   *
   * @param projectId the UUID of the project
   * @return a list of submission
   */
  List<SubmissionDTO> getSubmissionsByProject(String projectId);

  /**
   * Deletes a submission.
   *
   * @param id the UUID of the submission to be deleted
   */
  void deleteSubmission(String id);

  /**
   * This method returns a submission with a specific id.
   *
   * @param id the id
   * @return SubmissionDTO
   */

  SubmissionDTO getSubmissionById(String id);

  /**
   * Update submission.
   *
   * @param submissionDTO the submission to be updated
   * @return the error
   */
  Set<ValidationError> updateSubmission(SubmissionDTO submissionDTO);

  /**
   * This method sets a company to a tender when a tenderer is added to the tenderer list.
   *
   * @param submissionId The id of the tender
   * @param companyId    The id of the company
   * @return Ok
   */
  String setCompanyToSubmission(String submissionId, List<String> companyId);

  /**
   * This method returns a list of companies of a specific submission.
   *
   * @param id The UUID of a submission
   * @return companyDTO list
   */
  List<SubmittentOfferDTO> getCompaniesBySubmission(String id);

  /**
   * Returns the current status of a submission.
   *
   * @param submissionId the UUID of the submission
   * @return the id of the current status of the submission as string
   */
  String getCurrentStatusOfSubmission(String submissionId);

  /**
   * Returns all data of the current status of submission, so also the date the status is set.
   *
   * @param submissionId the UUID of the submission
   * @return the tender status history dto
   */
  TenderStatusHistoryDTO getCurrentStatusDataOfSubmission(String submissionId);

  /**
   * Gets the submission by company id.
   *
   * @param companyId the company id
   * @return the submission by company id
   */
  List<SubmissionDTO> getSubmissionByCompanyId(String companyId);


  /**
   * Returns a boolean stating whether a submission has had a certain status (in the past or
   * currently) or not.
   *
   * @param submissionId the UUID of the submission
   * @param statusId     the UUID of the requested status
   * @return a boolean stating whether a submission has had a certain status (in the past or
   * currently) or not.
   */
  Boolean hasSubmissionStatus(String submissionId, String statusId);

  /**
   * This method set the status of submission to "Submittentenliste in Prüfung".
   *
   * @param submissionId the UUID of the submission.
   * @return Ok
   */
  void checkSubmittentList(String submissionId);

  /**
   * This method set the status of submission to "Submittentenliste geprüft".
   *
   * @param submissionId the UUID of the submission.
   * @return Ok
   */
  String checkedSubmittentList(String submissionId);

  /**
   * This method checks if submission has a submittent.
   *
   * @param submissionId the UUID of the submission.
   * @return True or False whether submission has a submittent or not.
   */
  Boolean findIfSubmissionHasSubmittent(String submissionId);

  /**
   * Gets the submittents by submission.
   *
   * @param id the id
   * @return the submittents by submission
   */
  List<SubmittentDTO> getSubmittentsBySubmission(String id);

  /**
   * This method updates a list of submittents in Formal Examination.
   *
   * @param submittentDTO the list of submittents to be updated.
   * @return the error
   */
  Set<ValidationError> updateFormalAuditExamination(List<SubmittentDTO> submittentDTO);

  /**
   * This method updates the status of submission when the submittent list is updated.
   *
   * @param id the UUID of the submission.
   */
  void updateSubmissionFormalAuditExaminationStatus(String id);

  /**
   * This method updates the status of submission according to submission process.
   *
   * @param id the UUID of the submission.
   * @return int according to process and validation
   */
  List<String> closeFormalAudit(String id);


  /**
   * Close examination.
   *
   * @param id       the submission id
   * @param minGrade the minimum grade
   * @param maxGrade the maximum grade
   * @return String List
   */
  List<String> closeExamination(String id, BigDecimal minGrade, BigDecimal maxGrade);

  /**
   * This method updates the status of submission.
   *
   * @param submissionId  the submission id
   * @param status        the status
   * @param description   the description
   * @param reopenReason  the reopen reason
   * @param internalValue the internal value
   */
  void updateSubmissionStatus(String submissionId, String status, String description,
    String reopenReason, String internalValue);

  /**
   * This method updates the status of submission.
   *
   * @param submissionId  the submission id
   * @param version       the version
   * @param status        the status
   * @param description   the description
   * @param reason        the  reason
   * @param internalValue the internal value
   * @return
   */
  Set<ValidationError> updateSubmissionStatus(String submissionId, Long version, String status,
    String description, String reason, String internalValue);

  /**
   * This method reopens the offer process of the submission.
   *
   * @param reopenReason the reopen reason
   * @param id           the id
   */
  void reopenOffer(String reopenReason, String id);

  /**
   * This method reopens the formal audit process of the submission (after the offer process is
   * closed).
   *
   * @param reopenReason the reopen reason
   * @param id           the id
   */
  void reopenFormalAudit(String reopenReason, String id);

  /**
   * This method reopens the examination process of the submission.
   *
   * @param reopenReason the reopen reason
   * @param id           the id
   */
  void reopenExamination(String reopenReason, String id);

  /**
   * Reopen manual award.
   *
   * @param submissionId the submission id
   * @param reopenReason the reopen reason
   */
  void reopenManualAward(String submissionId, String reopenReason);

  /**
   * This method removes the award, if any, from the offers of the given submission.
   *
   * @param submissionId the submission id
   */
  void removeAward(String submissionId);

  /**
   * This function implements the reopening of the award evaluation.
   *
   * @param reopenReason the reopen reason
   * @param submissionId the submission id
   */
  void reopenAwardEvaluation(String reopenReason, String submissionId);

  /**
   * Update the submission status to commission procurement proposal started.
   *
   * @param submissionId the submission id
   */
  void startCommissionProcurementProposal(String submissionId);

  /**
   * Check if statuses award evaluation closed or manual award completed have been set before.
   *
   * @param submissionId the submission id
   * @return true/false
   */
  boolean haveAwardStatusesBeenClosed(String submissionId);

  /**
   * Check if status offer opening closed has been set before.
   *
   * @param submissionId the submission id
   * @return true/false
   */
  boolean hasOfferOpeningBeenClosedBefore(String submissionId);

  /**
   * Check if offer opening is closed for the first time.
   *
   * @param submissionId the submission id
   * @return true/false
   */
  boolean isOfferOpeningFirstTimeClosed(String submissionId);

  /**
   * Check if commission procurement proposal document exists.
   *
   * @param submissionId the submission id
   * @return true/false
   */
  boolean proposalDocumentExists(String submissionId);

  /**
   * Function to close the commission procurement proposal.
   *
   * @param submissionId the submission id
   */
  void closeCommissionProcurementProposal(String submissionId);

  /**
   * Check if the commission procurement proposal has been closed before.
   *
   * @param submissionId the submission id
   * @return true/false
   */
  boolean hasCommissionProcurementProposalBeenClosed(String submissionId);

  /**
   * This function implements the reopening of the commission procurement proposal.
   *
   * @param reopenReason the reopen reason
   * @param submissionId the submission id
   */
  void reopenCommissionProcurementProposal(String reopenReason, String submissionId);

  /**
   * Check if commission procurement decision document exists.
   *
   * @param submissionId the submission id
   * @return true if decision document exists, else return false
   */
  boolean decisionDocumentExists(String submissionId);

  /**
   * Function to close the commission procurement decision.
   *
   * @param submissionId the submission id
   */
  void closeCommissionProcurementDecision(String submissionId);

  /**
   * Check if the commission procurement decision has been closed before.
   *
   * @param submissionId the submission id
   * @return true if the commission procurement decision has been closed before, else return false
   */
  boolean hasCommissionProcurementDecisionBeenClosed(String submissionId);

  /**
   * This function implements the reopening of the commission procurement decision.
   *
   * @param reopenReason the reopen reason
   * @param submissionId the submission id
   */
  void reopenCommissionProcurementDecision(String reopenReason, String submissionId);

  /**
   * Update the award evaluation page.
   *
   * @param submissionId the submission id
   */
  void updateAwardEvaluationPage(String submissionId);

  /**
   * This method is used to determine if a Nachweisbrief document should be generated (Freihändig
   * and Freihändig mit Konkurrenz).
   *
   * @param submittentDTO the submittent DTO
   */
  void generateProofDocNegotiatedProcedure(List<SubmittentDTO> submittentDTO);

  /**
   * Manual award reopen check.
   *
   * @param submissionId the submission id
   * @return true if reopening is possible.
   */
  boolean manualAwardReopenCheck(String submissionId);

  /**
   * Resets the evaluation data after deleting the last submittent.
   *
   * @param submissionId the submission id
   */
  void resetEvaluationData(String submissionId);

  /**
   * Resets the document data after deleting the last submittent.
   *
   * @param submissionId the submission id
   */
  void resetDocumentData(String submissionId);

  /**
   * Returns the date a submission has been reopened and the status before the reopen (closed or
   * cancelled). Returns null if the submission has never been reopened or has been reopened and
   * closed again.
   *
   * @param submissionId the UUID of the submission
   * @return the tender status history dto with the date of the reopen and the status before the
   * reopen
   */
  TenderStatusHistoryDTO getDateAndPreviousReopenStatus(String submissionId);

  /**
   * Close application opening.
   *
   * @param submissionId the submission id
   */
  void closeApplicationOpening(String submissionId);

  /**
   * Check if applicant overview document exists.
   *
   * @param submissionId the submission id
   * @return true/false
   */
  boolean applicantOverviewDocumentExists(String submissionId);

  /**
   * Check if all application dates are filled.
   *
   * @param submissionId the submission id
   * @return true/false
   */
  boolean applicationDatesFilled(String submissionId);

  /**
   * Checks if the application opening has been closed before.
   *
   * @param submissionId the submission id
   * @return true/false
   */
  boolean hasApplicationOpeningBeenClosedBefore(String submissionId);

  /**
   * Function to reopen the application opening.
   *
   * @param reopenReason the reopen reason
   * @param submissionId the submission id
   */
  void reopenApplicationOpening(String reopenReason, String submissionId);

  /**
   * Gets the submission process.
   *
   * @param submissionId the submission id
   * @return the submission process
   */
  Process getSubmissionProcess(String submissionId);

  /**
   * Check if status suitability audit completed (selective process) has been set before.
   *
   * @param submissionId the submission id
   * @return true/false
   */
  boolean hasSuitabilityAuditCompletedSBeenSetBefore(String submissionId);

  /**
   * Load submission signatures.
   *
   * @param submissionId the submission id
   * @return the signature process type DTO
   */
  SignatureProcessTypeDTO loadSubmissionSignatures(String submissionId);

  /**
   * This method returns a list of applicants of a specific submission (selective process).
   *
   * @param submissionId the submission id
   * @return SubmittentOfferDTO list
   */
  List<SubmittentOfferDTO> getApplicantsBySubmission(String submissionId);

  /**
   * Gets applicants by submission for formal audit.
   *
   * @param submissionId the submission id
   * @return the applicants by submission
   */
  List<SubmittentDTO> getApplicantsForFormalAudit(String submissionId);

  /**
   * This method updates the submittents of a selective process in formal audit 1st level.
   *
   * @param submittentDTOs the submittent DTOs.
   * @return the error
   */
  Set<ValidationError> updateSelectiveFormalAudit(List<SubmittentDTO> submittentDTOs);

  /**
   * Function to handle navigation to the submission canceling tab.
   *
   * @param submissionId the submission id
   * @return true, if navigation possible
   */
  boolean submissionCancelNavigation(String submissionId);

  /**
   * Gets the user group name.
   *
   * @return the user group name
   */
  String getUserGroupName();

  /**
   * Gets the submission threshold value.
   *
   * @param submissionId the submission id
   * @return the threshold value
   */
  BigDecimal getThresholdValue(String submissionId);

  /**
   * Gets the submission pm external.
   *
   * @param submissionId the submission id
   * @return the submission pm external
   */
  CompanyDTO getSubmissionPmExternal(String submissionId);

  /**
   * Gets the submittent list emails.
   *
   * @param submissionId the submission id
   * @return the submittent list emails
   */
  List<String> getSubmittentListEmails(String submissionId);

  /**
   * Move project data.
   *
   * @param submissionId the submission id
   * @param projectId    the project id
   * @return the error
   */
  Set<ValidationError> moveProjectData(String submissionId, Long submissionVersion,
    String projectId, Long projectVersion);

  /**
   * Automatically close submissions.
   */
  void automaticallyCloseSubmissions();

  /**
   * Check if a to do task exists to set the publicationDateAward. If it exists then it must be
   * deleted and if
   * 1) the current status of the submission is AWARD_NOTICES_CREATED or CONTRACT_CREATED
   * 2) there was no status change in the last 40 days (move to status
   * CONTRACT_CREATED does not count as status change, the counter for the 40 days starts at the set
   * of status AWARD_NOTICES_CREATED)
   * 3) the counter for the automatic closure of the submission is
   * set more than 40 days ago (this counter is started when the Beschwerdeeingang field (in
   * 'Verfügungen erstellt' tab) gets unselected), then the submission needs to be closed This
   * method is called if during the update of a project the field GattWto of the project is set to
   * false and this update is performed also in the given submission.
   *
   * @param submissionDTO the submission dto
   */
  void checkToDeleteTaskAndCloseSubmission(SubmissionDTO submissionDTO);

  /**
   * Lock submission.
   *
   * @param submissionId the submission id
   * @param type         the type
   */
  void lockSubmission(String submissionId, String type, boolean isExortlocked);

  /**
   * Unlock submission.
   *
   * @param submissionId the submission id
   * @param type         the type
   */
  void unlockSubmission(String submissionId, String type);

  /**
   * Gets the unique submittent list (used in Submittentenliste Postliste and Submittentenliste als
   * Etiketten documents.
   *
   * @param submissionId the submission id
   * @return the unique submittent list
   */
  List<CompanyDTO> getUniqueSubmittentList(String submissionId);

  /**
   * Check submission threshold.
   *
   * @param submissionId the submission id
   * @return true, if successful
   */
  boolean checkSubmissionThreshold(String submissionId);

  /**
   * Gets the date of completed or canceled status.
   *
   * @param submissionId the submission id
   * @return the date of completed or canceled status
   */
  Timestamp getDateOfCompletedOrCanceledStatus(String submissionId);

  /**
   * Gets the date of creation status of submission.
   *
   * @param submissionId the submission id
   * @return the date of creation status of submission
   */
  Timestamp getDateOfCreationStatusOfSubmission(String submissionId);

  /**
   * Gets the refernce date for object.
   *
   * @param submissionId the submission id
   * @return the refernce date for object
   */
  Timestamp getRefernceDateForObject(String submissionId);

  /**
   * Gets the refernce date for proofs.
   *
   * @param submissionId the submission id
   * @return the refernce date for proofs
   */
  Timestamp getRefernceDateForProofs(String submissionId);

  /**
   * Gets the submittents count.
   *
   * @param id the id
   * @return the submittents count
   */
  int getSubmittentsCount(String id);

  List<SubmittentDTO> retrieveEmptyOffers(SubmissionDTO submission);

  /**
   * Gets the submittents for the negotiated procedure.
   *
   * @param submittentIds the submittent ids
   * @return the submittent DTOs
   */
  List<SubmittentDTO> getSubmittentsForNegotiatedProcedure(List<String> submittentIds);

  /**
   * Maps the proof provided values.
   *
   * @param submissionId the submission id
   * @return the mapped proof provided values.
   */
  List<ProofProvidedMapDTO> mapProofProvidedValues(String submissionId);

  /**
   * Checks if submission exists.
   *
   * @param submissionId the submission id
   * @return true if submission exists
   */
  boolean submissionExists(String submissionId);

  /**
   * Checks if current status is changed by another user.
   *
   * @param id     the submission id
   * @param status the status to check
   * @return true if status is changed by another user
   */
  boolean isStatusChanged(String id, String status);

  /**
   * Update CommissionProcurementDecision.
   *
   * @param commissionProcurementDecisionDTO the commissionProcurementDecisionDTO
   * @param submissionId the submissionId
   * @param submissionVersion the submissionVersion
   * @return the error
   */
  Set<ValidationError> updateCommissionProcurementDecision(CommissionProcurementDecisionDTO
    commissionProcurementDecisionDTO, String submissionId, Long submissionVersion);

  /**
   * Checks for Optimistic Lock Exception.
   * If there is an Optimistic Lock Exception throws a custom response with message from
   * OptimisticLockExceptionMapper.
   *
   * @param submissionId the submissionId
   * @param submissionVersion the submissionVersion
   */
  void checkOptimisticLockSubmission(String submissionId, Long submissionVersion);

  /**
   * Security check for Submission Create.
   */
  void submissionCreateSecurityCheck();

  /**
   * Security check for Document Area.
   */
  void submissionDocumentAreaSecurityCheck(String submissionId);

  /**
   * Security check for Formal Audit.
   */
  void formalAuditSecurityCheck(String submissionId);

  /**
   * Security check for Suitability Audit.
   */
  void suitabilityAuditSecurityCheck(String submissionId);

  /**
   * Security check for Applicants List.
   */
  void applicantsSecurityCheck(String submissionId);

  /**
   * Security check for Submission.
   */
  void submissionViewSecurityCheck(String submissionId);

  /**
   * resource Id can be the Submission, Project or Tenant Id.
   */
  SubmissionDTO getSubmissionByResourceId(String resourceID);
}
