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

import ch.bern.submiss.services.api.dto.AwardAssessDTO;
import ch.bern.submiss.services.api.dto.AwardDTO;
import ch.bern.submiss.services.api.dto.CriterionDTO;
import ch.bern.submiss.services.api.dto.ExaminationDTO;
import ch.bern.submiss.services.api.dto.OfferDTO;
import ch.bern.submiss.services.api.dto.SubcriterionDTO;
import ch.bern.submiss.services.api.dto.SuitabilityDTO;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

public interface CriterionService {

  /**
   * Delete criterion.
   * 
   * @param id the criterion id
   * @param pageRequestedOn the pageRequestedOn
   * @return the error
   */
  Set<ValidationError> deleteCriterion(String id, Timestamp pageRequestedOn);

  /**
   * Add criterion to submission (tender).
   * 
   * @param criterion
   * @return added criterion.
   */
  String addCriterionToSubmission(CriterionDTO criterion);

  /**
   * Read criteria of submission (tender).
   * 
   * @param submissionId the submission id.
   * @return list of criteria.
   */
  List<CriterionDTO> readCriteriaOfSubmission(String submissionId);

  /**
   * Update criterion.
   * 
   * @param examinationDTO
   */
  void updateCriterion(ExaminationDTO examinationDTO);
  
  /**
   * Update award criterion.
   * Returns true if at least one offer has zero Betrag.
   * 
   * @param awardDTO
   */
  boolean updateAwardCriterion(AwardDTO awardDTO);

  /**
   * Delete sub-criterion.
   * 
   * @param id the sub criterion id
   * @param pageRequestedOn the pageRequestedOn
   * @return the error
   */
  Set<ValidationError> deleteSubcriterion(String id, Timestamp pageRequestedOn);

  /**
   * Add sub-criterion to criterion.
   * 
   * @param subcriterion
   * @return added sub-criterion
   */
  String addSubcriterionToCriterion(ch.bern.submiss.services.api.dto.SubcriterionDTO subcriterion);

  /**
   * Read sub-criteria of criterion.
   * 
   * @param criterionId the criterion id
   * @return list of sub-criteria
   */
  List<SubcriterionDTO> readSubcriteriaOfCriterion(String criterionId);

  /**
   * Check if the evaluated criteria weighting limit is crossed during criterion addition.
   * 
   * @param submissionId the submission (tender) id
   * @param weighting the criterion weighting
   * @return true/false
   */
  boolean checkEvaluatedCriteriaWeightingLimit(String submissionId, Double weighting);

  /**
   * Check if the sub-criteria weighting limit is crossed during criterion addition.
   * 
   * @param criterionId the criterion id
   * @param weighting the sub-criterion weighting 
   * @return true/false
   */
  boolean checkSubcriteriaWeightingLimit(String criterionId, Double weighting);

  /**
   * Check if the award criteria weighting limit is crossed during criterion addition.
   * 
   * @param criterionId the criterion id
   * @param weighting the criterion weighting
   * @return true/false
   */
  boolean checkAwardCriteriaWeightingLimit(String criterionId, Double weighting);

  /**
   * Check if the examination (evaluated) criteria and sub-criteria weighting limit is crossed during update.
   * 
   * @param list of criteria
   * @return value according to which limits have been crossed
   */
  int checkExaminationCriterionWeightingLimit(List<CriterionDTO> list);
  
  /**
   * Check if the award criteria and sub-criteria weighting limit is crossed during update.
   * 
   * @param list of criteria
   * @return value according to which limits have been crossed
   */
  int checkAwardCriterionWeightingLimit(List<CriterionDTO> list);
  
  /**
   * Checks if operating cost award criterion can be added. 
   * 
   * @param submissionId the submission id
   * @return true/false
   */
  boolean canOperatingCostAwardCriterionBeAdded(String submissionId);
  
  /**
   * Return number of active tenderers (submittents). 
   * 
   * @param submissionId the submission id
   * @return number of active tenderers
   */
  int calculateActiveSubmittentsOfSubmission (String submissionId);
  
  /**
   * Return list of offers with criteria
   * @param submissionId the UUID of the submission
   * @param type The type of criteria to be returned
   */
  List<OfferDTO> getOfferCriteria(String submissionId, String type);

  /**
   * Update the list of offers with criteria
   * @param The list of the offers with criteria to be updated.
   */
  Set<ValidationError> updateOfferCriteria(List<SuitabilityDTO> suitabilityDTO);
  
  /**
   * Update the list of offer criteria for the award process
   * @param The list of the offer criteria to be updated.
   */
  void updateOfferCriteriaAward(List<AwardAssessDTO> awardAssessDTOs);

  List<String> checkSubcriteriaWeightingLimit(List<SuitabilityDTO> suitabilityDTOs);
  
  /**
   * Check if a grade is null
   * 
   * @param submissionId the submission id
   * @return true/false
   */
  boolean existNullGrade(String submissionId);
  
  /**
   * Check if a grade is invalid
   * 
   * @param submissionId the submission id
   * @param awardMinGrade the award minimum grade
   * @param awardMaxGrade the award maximum grade
   * @return true/false
   */
  boolean existInvalidGrade(String submissionId, BigDecimal awardMinGrade,
      BigDecimal awardMaxGrade);
  
  /**
   * Check if an offer must criterion is not fulfilled
   * @param offerId the offer id
   * @return true/false
   */
  boolean offerMustCriterionNotFulfilled(String offerId);

  /**
   * Proof if the given calculation formula is valid
   * and if it is valid then update the price of operating costs field accordingly
   * and perform all calculations with the new formula and update the according fields
   * 
   * @param calculationFormula the calculation formula
   * @param isPrice a boolean indicating whether the price or the operating costs
   *                should be updated
   * @param submissionId the submission id
   * @return a set of the errors that have occurred in the formula, empty set if none
   */
  Set<ValidationError> updateCustomCalculationFormula(String calculationFormula, boolean isPrice, String submissionId);
  
  /**
   * Checks if there is any change in imported criteria.
   * 
   * @param suitabilities
   * @returns true/false
   */
  boolean isOfferCriteriaForChanged(List<SuitabilityDTO> suitabilities);
  
  
  /**
   * Checks if there is any change in list of offer criteria for the award process
   * @param The list of the offer criteria to be updated.
   * @returns  true/false
   */
  boolean isOfferCriteriaForChangedAward(List<AwardAssessDTO> awardAssessDTOs);

  /**
   * Calculates the grade.
   *
   * @param currentAmount the current amount
   * @param minAmount the minimum amount
   * @param expression the expression
   * @return the grade
   */
  BigDecimal calculateGrade(BigDecimal currentAmount, BigDecimal minAmount, String expression);

  /**
   * Gets the examination submittent list with criteria.
   *
   * @param submissionId the submission id
   * @param type the type
   * @param all the all
   * @param forEignungspruefungDoc true if called for Eignungsprüfung document creation
   * @return the examination submittent list with criteria
   */
  List<OfferDTO> getExaminationSubmittentListWithCriteria(String submissionId, String type,
    Boolean all, Boolean forEignungspruefungDoc);
  
  /**
   * Read submission evaluated criteria.
   *
   * @param submissionId the submission id
   * @return the list
   */
  List<CriterionDTO> readSubmissionEvaluatedCriteria(String submissionId);

  /**
   * Updates the suitability/examination offer criteria values from the imported xlsx file.
   *
   * @param suitabilityDTOs the suitability DTOs
   */
  void updateOfferCriteriaFromXlsx(List<SuitabilityDTO> suitabilityDTOs);

  /**
   * Updates the award offer criteria from the imported xlsx file.
   * 
   * @param awardAssessDTOs the award assess DTOs.
   */
  void updateAwardOfferCriteriaFromXlsx(List<AwardAssessDTO> awardAssessDTOs);

  /**
   * Deletes the operating cost criterion, if the operating costs amount of all offers is null or 0.
   *
   * @param submissionId the submission id
   */
  void deleteOperatingCostCriterion(String submissionId);

  /**
   * Checks for changes by other users before adding or updating a criterion or sub criterion.
   *
   * @param submissionId the submissionId
   * @param pageRequestedOn the pageRequestedOn
   * @return the error
   */
  Set<ValidationError> checkForChangesByOtherUsers(String submissionId, Timestamp pageRequestedOn);

  /**
   * Checks if criterion exists.
   *
   * @param criterionId the criterion id
   * @return true if criterion exists
   */
  boolean criterionExists(String criterionId);

  /**
   * Checks if subCriterion exists.
   *
   * @param subCriterionId the subCriterion id
   * @return true if subCriterion exists
   */
  boolean subCriterionExists(String subCriterionId);

  /**
   * Checks if offerCriterion exists.
   *
   * @param offerCriterionId the offerCriterion id
   * @return true if offerCriterion exists
   */
  boolean offerCriterionExists(String offerCriterionId);

  /**
   * Checks for changes by other users before saving the Eignungsprüfungstabelle.
   *
   * @param submissionId    the submissionId
   * @param pageRequestedOn the pageRequestedOn
   * @param suitabilityDTOs the suitabilityDTOs
   * @return the error
   */
  Set<ValidationError> suitabilityCheckForChangesByOtherUsers(String submissionId,
    Timestamp pageRequestedOn, List<SuitabilityDTO> suitabilityDTOs);

  /**
   * Checks if examination is already locked by another user.
   *
   * @param submissionId the submissionId
   * @return the error
   */
  Set<ValidationError> examinationLockedByAnotherUser(String submissionId);

  /**
   * Checks if award is already locked by another user.
   *
   * @param submissionId the submissionId
   * @return the error
   */
  Set<ValidationError> awardLockedByAnotherUser(String submissionId);

  /**
   * Checks for changes by other users before saving the Zuschlagsbewertung form.
   *
   * @param submissionId    the submissionId
   * @param pageRequestedOn the pageRequestedOn
   * @return the error
   */
  Set<ValidationError> awardCheckForChangesByOtherUsers(String submissionId,
    Timestamp pageRequestedOn);

  /**
   * Checks for changes by other users before saving the Formelle/Eignungsprüfung form.
   *
   * @param submissionId    the submissionId
   * @param pageRequestedOn the pageRequestedOn
   * @param submissionVersion the submissionVersion
   * @return the error
   */
  Set<ValidationError> examinationCheckForChangesByOtherUsers(String submissionId,
    Timestamp pageRequestedOn, Long submissionVersion);

  /**
   * Security check for Zuschlagsbewertung view.
   */
  void awardEvaluationViewSecurityCheck(String submissionId);

  /**
   * Security check for Zuschlagsbewertung edit.
   */
  void awardEvaluationEditSecurityCheck(String submissionId);
}
