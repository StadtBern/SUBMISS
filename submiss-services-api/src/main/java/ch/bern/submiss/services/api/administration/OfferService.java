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

import ch.bern.submiss.services.api.dto.OfferDTO;
import ch.bern.submiss.services.api.dto.SubmittentDTO;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * The Interface OfferService.
 */
public interface OfferService {

  /**
   * Deletes a submittent.
   *
   * @param id the UUID of the submittent to be deleted
   */
  void deleteSubmittent(String id);

  /**
   * Deletes an offer.
   * 
   * @param id the UUID of the offer to be deleted.
   * @return the new offer id.
   */
  String deleteOffer(String id);

  /**
   * Find if submittent has offers.
   * 
   * @param id the submittent id.
   * @return true/false
   */
  Boolean findIfSubmittentHasOffer(String id);

  /**
   * Find if submittent has sub-contractors.
   *
   * @param id the submittent id.
   * @return the boolean
   */
  Boolean findIfSubmittentHasSubcontractors(String id);

  /**
   * Close offer validation.
   *
   * @param submissionId the submission (tender) id
   * @return the error
   */
  Set<ValidationError> closeOfferValidation(String submissionId);

  /**
   * Close offer.
   * 
   * @param submissionId the submission (tender) id
   * @param submissionVersion the submissionVersion
   * @return the error
   */
  Set<ValidationError> closeOffer(String submissionId, Long submissionVersion);

  /**
   * Get offer by id.
   * 
   * @param id the offer id
   * @return offer
   */
  OfferDTO getOfferById(String id);

  /**
   * Add sub-contractor to submittent.
   *
   * @param submittent the submittent
   */
  void addSubcontractorToSubmittent(SubmittentDTO submittent);

  /**
   * Update offer.
   *
   * @param offer the offer
   */
  void updateOffer(OfferDTO offer);

 
  /**
   * Reset offer. Resets the offer to default values except the Offer Date
   *  @param offerId the offer id
   * @param offerDate the offer date
   * @param notes
   */
  String resetOffer(String offerId, Date offerDate, String notes);
  
  /**
   * Delete sub-contractor.
   *
   * @param submittentId the submittent id
   * @param subcontractorId the sub-contractor id
   * @return the error
   */
  Set<ValidationError> deleteSubcontractor(String submittentId, String subcontractorId);

  /**
   * Add joint venture to submittent.
   *
   * @param submittent the submittent
   */
  void addJointVentureToSubmittent(SubmittentDTO submittent);

  /**
   * Delete joint venture.
   * 
   * @param submittentId the submittent id
   * @param jointVentureId the joint venture id
   * @return the error
   */
  Set<ValidationError> deleteJointVenture(String submittentId, String jointVentureId);
  
  /**
   * Read active submittents by submission.
   *
   * @param submissionId the submission id
   * @return the list
   */
  List<SubmittentDTO> readActiveSubmittentsBySubmission(String submissionId);

  /**
   * Update offer awards.
   *  @param checkedOffersIds the checked offers ids
   * @param submissionId the submissionId
   */
  void updateOfferAwards(List<String> checkedOffersIds, String submissionId);
  
  /**
   * This method closes the award evaluation.
   *
   * @param awardedOfferIds the awarded offer ids
   * @param submissionId the submission id
   * @param createVersion the create version
   */
  void closeAwardEvaluation(List<String> awardedOfferIds, String submissionId,
    boolean createVersion);
  
  /**
   * This method retrieves the offers of a submission.
   *
   * @param submissionId the submission id
   * @return offer DTOs
   */
  List<OfferDTO> getOffersBySubmission(String submissionId);

  /**
   * Updates the award recipient values.
   *
   * @param offerDTOs the offer DTOs
   */
  void updateAwardRecipients(List<OfferDTO> offerDTOs);
  
  /**
   * Create offer criteria and offer sub-criteria for default offer.
   *
   * @param offerDTO the offer DTO
   */
  void createOfferCriteriaAndSubcriteriaForDefaultOffer(OfferDTO offerDTO);
  
  /**
   * Check if required documents have been generated.
   *
   * @param submissionId the submission id
   * @param templateType the template type
   * @return true, if successful
   */
  boolean requiredDocumentExists(String submissionId, String templateType);
  
  /**
   * Update application.
   *
   * @param applicationId the application id
   * @param applicationDate the application date
   * @param applicationInformation the application information
   * @param applicationVersion the applicationVersion
   * @param submissionVerion the submissionVerion
   * @return the error
   */
  Set<ValidationError> updateApplication(String applicationId, Date applicationDate,
    String applicationInformation, Long applicationVersion, Long submissionVerion);
  
  /**
   * Find if applicant has application.
   *
   * @param applicantId the applicant id
   * @return the boolean
   */
  Boolean findIfApplicantHasApplication(String applicantId);
  
  /**
   * Function to delete an application.
   * 
   * @param applicationId the UUID of the application to be deleted.
   * @return the application id
   */
  String deleteApplication(String applicationId);
  
  /**
   * Calculate offer amount.
   *
   * @param offerDTO the offer DTO
   * @return the offer amount
   */
  BigDecimal calculateOfferAmount(OfferDTO offerDTO);
  
  /**
   * Calculate operating costs amount.
   *
   * @param offerDTO the offer DTO
   * @return the operating costs amount
   */
  BigDecimal calculateOperatingCostsAmount(OfferDTO offerDTO);
  
  /**
   * This method retrieves the applications of a submission (selective process).
   *
   * @param submissionId the submission id
   * @return offer DTOs
   */
  List<OfferDTO> getApplicationsBySubmission(String submissionId);
  
  /**
   * Retrieve not excluded offers.
   *
   * @param submissionId the submission id
   * @return the list
   */
  List<OfferDTO> retrieveNotExcludedOffers(String submissionId);
  
  /**
   * Retrieve excluded offers.
   *
   * @param submissionId the submission id
   * @return the list
   */
  List<OfferDTO> retrieveExcludedOffers(String submissionId);
  
  /**
   * Gets the submission offers with criteria.
   *
   * @param submissionId the submission id
   * @return the submission offers with criteria
   */
  List<OfferDTO> getSubmissionOffersWithCriteria(String submissionId);
  
  /**
   * Gets offers by their ids.
   *
   * @param offerIds the offer ids
   * @return the offers.
   */
  List<OfferDTO> getOffersByOfferIds(List<String> offerIds);
  
  /**
   * Retrieve excluded applicants.
   *
   * @param submissionId the submission id
   * @return the list
   */
  List<OfferDTO> retrieveExcludedApplicants(String submissionId);
  
  /**
   * Retrieve not excluded selective offers.
   *
   * @param submissionId the submission id
   * @return the list
   */
  List<OfferDTO> retrieveNotExcludedSelectiveOffers(String submissionId);
 
	/**
	 * Read award submittents.
	 *
	 * @return the list
	 */
	List<String> readAwardSubmittentNames(String value);
	
	/**
	 * Retrieve offer applicants.
	 *
	 * @param submissionId the submission id
	 * @return the list
	 */
	List<OfferDTO> retrieveOfferApplicants(String submissionId);
	
	/**
	 * Calculate CHFMWST value.
	 *
	 * @param offerDTO the offer DTO
	 * @return the big decimal
	 */
	BigDecimal calculateCHFMWSTValue(OfferDTO offerDTO);

  /**
   * Checks if offer exists.
   *
   * @param offerId the offer id
   * @return true if offer exists
   */
	boolean offerExists(String offerId);

  /**
   * Checks if submittent exists.
   *
   * @param submittentId the submittent id
   * @return true if submittent exists
   */
	boolean submittentExists(String submittentId);

  /**
   * Optimistic Lock check when trying to delete an application.
   *
   * @param applicationId the applicationId
   * @param applicationVersion the applicationVersion
   * @return the error
   */
  Set<ValidationError> deleteApplicationOptimisticLock(String applicationId, Long applicationVersion);

  /**
   * Security check for Offer List.
   */
  void offerListSecurityCheck(String submissionId);
}
