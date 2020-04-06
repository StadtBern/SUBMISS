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

package ch.bern.submiss.web.resources;

import ch.bern.submiss.services.api.administration.CriterionService;
import ch.bern.submiss.services.api.administration.OfferService;
import ch.bern.submiss.services.api.administration.ProcedureService;
import ch.bern.submiss.services.api.administration.SDVatService;
import ch.bern.submiss.services.api.administration.SubmissTaskService;
import ch.bern.submiss.services.api.administration.SubmissionService;
import ch.bern.submiss.services.api.administration.UserAdministrationService;
import ch.bern.submiss.services.api.constants.Group;
import ch.bern.submiss.services.api.constants.Process;
import ch.bern.submiss.services.api.constants.TenderStatus;
import ch.bern.submiss.services.api.dto.OfferDTO;
import ch.bern.submiss.services.api.dto.SubmissionDTO;
import ch.bern.submiss.services.api.dto.SubmittentDTO;
import ch.bern.submiss.services.api.util.ValidationMessages;
import ch.bern.submiss.web.forms.ApplicationForm;
import ch.bern.submiss.web.forms.ManualAwardForm;
import ch.bern.submiss.web.forms.OfferForm;
import ch.bern.submiss.web.forms.SubmittentForm;
import ch.bern.submiss.web.mappers.OfferFormMapper;
import ch.bern.submiss.web.mappers.SubmittentFormMapper;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.ops4j.pax.cdi.api.OsgiService;

/**
 * REST handler for offer.
 */
@Path("/offer")
@Singleton
public class OfferResource {

  /**
   * The Constant OFFER_DATE.
   */
  private static final String OFFER_DATE = "offerDate";
  /**
   * The Constant ERROR_DATE_FIELD.
   */
  private static final String ERROR_DATE_FIELD = "errorDateField";
  /**
   * The offer service.
   */
  @OsgiService
  @Inject
  private OfferService offerService;
  /**
   * The submission service.
   */
  @OsgiService
  @Inject
  private SubmissionService submissionService;
  /**
   * The procedure service.
   */
  @OsgiService
  @Inject
  private ProcedureService procedureService;
  /**
   * The task service.
   */
  @OsgiService
  @Inject
  private SubmissTaskService taskService;
  /**
   * The user service.
   */
  @OsgiService
  @Inject
  private UserAdministrationService userService;
  /**
   * The SD vat service.
   */
  @OsgiService
  @Inject
  private SDVatService sDVatService;
  /**
   * The criterion service.
   */
  @OsgiService
  @Inject
  private CriterionService criterionService;

  /**
   * Deletes a submittent.
   *
   * @param id the UUID of the submittent to be deleted
   * @return the response
   */
  @DELETE
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/submittent/{id}")
  public Response deleteSubmittent(@PathParam("id") String id) {

    Boolean submittentHasOffer = false;
    Boolean submittentHasSubcontractors = false;
    if (id != null) {
      submittentHasOffer = offerService.findIfSubmittentHasOffer(id);
      submittentHasSubcontractors = offerService.findIfSubmittentHasSubcontractors(id);
    }
    Set<ValidationError> errors = deleteSubmittentValidation(submittentHasOffer,
      submittentHasSubcontractors);
    if (!errors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    offerService.deleteSubmittent(id);
    return Response.ok().build();
  }

  /**
   * Deletes an offer.
   *
   * @param id the UUID of the offer to be deleted
   * @return the new offer id
   */
  @DELETE
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/{id}")
  public Response deleteOffer(@PathParam("id") String id) {
    List<String> newOfferId = new ArrayList<>();
    newOfferId.add(offerService.deleteOffer(id));
    return Response.ok(newOfferId).build();
  }

  /**
   * Close offer.
   *
   * @param submissionId the submission id
   * @return the response
   */
  @PUT
  @Path("/close/{submissionId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response closeOffer(@PathParam("submissionId") String submissionId) {
    List<String> results = offerService.closeOffer(submissionId);
    Set<ValidationError> errors = closeOfferValidation(results);
    if (!errors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    return Response.ok().build();
  }

  /**
   * Reset offer.
   *
   * @param offer the offer
   * @return the response
   */
  @PUT
  @Path("/resetOffer")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response resetOffer(@Valid OfferForm offer) {
    Set<ValidationError> errors = resetOfferValidation(offer);
    String offerId = null;
    if (!errors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    if (offer != null) {
      offerId = offerService.resetOffer(offer.getId(), offer.getOfferDate());
    }
    OfferDTO offerDTO = new OfferDTO();
    offerDTO.setId(offerId);
    return Response.ok(offerDTO).build();
  }

  /**
   * Update offer.
   *
   * @param offer the offer
   * @return the response
   */
  @PUT
  @Path("/update")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateOffer(@Valid OfferForm offer) {
    Set<ValidationError> errors = updateOfferValidation(offer);
    if (!errors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    OfferDTO offerDTO = OfferFormMapper.INSTANCE.toOfferDTO(offer);
    offerDTO.setAmount(offerService.calculateOfferAmount(offerDTO));
    offerService.updateOffer(offerDTO);
    return Response.ok().build();
  }

  /**
   * Update operating cost offer.
   *
   * @param offer the offer
   * @return the response
   */
  @PUT
  @Path("/operating/update")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateOperatingCostOffer(@Valid OfferForm offer) {
    Set<ValidationError> errors = operatingCostValidation(null, null, offer);
    if (!errors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    OfferDTO offerDTO = OfferFormMapper.INSTANCE.toOfferDTO(offer);
    offerDTO.setOperatingCostsAmount(offerService.calculateOperatingCostsAmount(offerDTO));
    offerService.updateOffer(offerDTO);
    return Response.ok().build();
  }

  /**
   * Gets the offer by id.
   *
   * @param id the id
   * @return the offer by id
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{id}")
  public Response getOfferById(@PathParam("id") String id) {
    OfferDTO offerDTO = offerService.getOfferById(id);
    return Response.ok(offerDTO).build();
  }

  /**
   * Read active submittents by submission.
   *
   * @param submissionId the submission id
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/submittent/submission/{submissionId}")
  public Response readActiveSubmittentsBySubmission(
    @PathParam("submissionId") String submissionId) {
    List<SubmittentDTO> submittentDTOs = offerService
      .readActiveSubmittentsBySubmission(submissionId);
    return Response.ok(submittentDTOs).build();

  }

  /**
   * Read award submittents..
   *
   * @return the response
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/awardSubmittents")
  public Response readAwardSubmittents(String value) {
    List<String> submittentDTOs = offerService.readAwardSubmittentNames(value);
    return Response.ok(submittentDTOs).build();

  }

  /**
   * Adds the subcontractor to submittent.
   *
   * @param submittent the submittent
   * @return the response
   */
  @PUT
  @Path("/subcontractor")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response addSubcontractorToSubmittent(@Valid SubmittentForm submittent) {
    SubmittentDTO submittentDTO = SubmittentFormMapper.INSTANCE.toSubmittentDTO(submittent);
    offerService.addSubcontractorToSubmittent(submittentDTO);
    return Response.ok().build();
  }

  /**
   * Delete subcontractor.
   *
   * @param submittentId the submittent id
   * @param subcontractorId the subcontractor id
   * @return the response
   */
  @DELETE
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/submittent/{submittentId}/subcontractor/{subcontractorId}")
  public Response deleteSubcontractor(@PathParam("submittentId") String submittentId,
    @PathParam("subcontractorId") String subcontractorId) {
    offerService.deleteSubcontractor(submittentId, subcontractorId);
    return Response.ok().build();
  }

  /**
   * Adds the joint venture to submittent.
   *
   * @param submittent the submittent
   * @return the response
   */
  @PUT
  @Path("/jointVenture")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response addJointVentureToSubmittent(@Valid SubmittentForm submittent) {
    SubmittentDTO submittentDTO = SubmittentFormMapper.INSTANCE.toSubmittentDTO(submittent);
    offerService.addJointVentureToSubmittent(submittentDTO);
    return Response.ok().build();
  }

  /**
   * Delete joint venture.
   *
   * @param submittentId the submittent id
   * @param jointVentureId the joint venture id
   * @return the response
   */
  @DELETE
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/submittent/{submittentId}/jointVenture/{jointVentureId}")
  public Response deleteJointVenture(@PathParam("submittentId") String submittentId,
    @PathParam("jointVentureId") String jointVentureId) {
    offerService.deleteJointVenture(submittentId, jointVentureId);
    return Response.ok().build();
  }

  /**
   * Update the list of offers with criteria.
   *
   * @param checkedOffersIds the checked offers ids
   * @return the response
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/update/award/{submissionId}")
  public Response updateOfferAwards(@Valid List<String> checkedOffersIds,
    @PathParam("submissionId") String submissionId) {
    offerService.updateOfferAwards(checkedOffersIds, submissionId);
    return Response.ok().build();
  }

  /**
   * Close award evaluation.
   *
   * @param submissionId the submission id
   * @param awardedOfferIds the awarded offer ids
   * @return the response
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/closeAwardEvaluation/{submissionId}")
  public Response closeAwardEvaluation(@PathParam("submissionId") String submissionId,
    @Valid List<String> awardedOfferIds) {
    offerService.closeAwardEvaluation(awardedOfferIds, submissionId);
    return Response.ok().build();
  }

  /**
   * Update applicant.
   *
   * @param application the applicant
   * @return the response
   */
  @PUT
  @Path("/updateApplication")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateApplication(@Valid ApplicationForm application) {
    Set<ValidationError> errors = new HashSet<>();
    // Check if application date value exists.
    if (application.getApplicationDate() == null) {
      errors.add(new ValidationError("applicationDateErrorField",
        ValidationMessages.MANDATORY_ERROR_MESSAGE));
      errors
        .add(new ValidationError("applicationDate", ValidationMessages.MANDATORY_ERROR_MESSAGE));
    }
    // Check application information text length.
    if (application.getApplicationInformation() != null
      && application.getApplicationInformation().length() > 500) {
      errors.add(new ValidationError("applicationInformationErrorField",
        ValidationMessages.APPLICATION_INFORMATION_ERROR_MESSAGE));
      errors.add(new ValidationError("applicationInformation",
        ValidationMessages.APPLICATION_INFORMATION_ERROR_MESSAGE));
    }
    if (!errors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    offerService.updateApplication(application.getApplicationId(), application.getApplicationDate(),
      application.getApplicationInformation());
    return Response.ok().build();
  }

  /**
   * Function to delete an applicant.
   *
   * @param id the UUID of the applicant to be deleted.
   * @return the response
   */
  @DELETE
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/deleteApplicant/{id}")
  public Response deleteApplicant(@PathParam("id") String id) {

    Boolean applicantHasApplication = false;
    Boolean applicantHasSubcontractorsOrJointVenture = false;
    if (id != null) {
      applicantHasApplication = offerService.findIfApplicantHasApplication(id);
      applicantHasSubcontractorsOrJointVenture = offerService.findIfSubmittentHasSubcontractors(id);
    }
    Set<ValidationError> errors = applicationValidation(applicantHasApplication,
      applicantHasSubcontractorsOrJointVenture);
    if (!errors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    offerService.deleteSubmittent(id);
    return Response.ok().build();
  }

  /**
   * Function to delete an application.
   *
   * @param applicationId the UUID of the application to be deleted.
   * @return the response including the application id.
   */
  @DELETE
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/deleteApplication/{applicationId}")
  public Response deleteApplication(@PathParam("applicationId") String applicationId) {
    List<String> newApplicationId = new ArrayList<>();
    newApplicationId.add(offerService.deleteApplication(applicationId));
    return Response.ok(newApplicationId).build();
  }

  /**
   * Reset offer operating cost values.
   *
   * @param offerForm the offer form
   * @return the response
   */
  @PUT
  @Path("/resetOperatingCostValues")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response resetOperatingCostValues(@Valid OfferForm offerForm) {
    OfferDTO offerDTO = OfferFormMapper.INSTANCE.toOfferDTO(offerForm);
    offerDTO.setOperatingCostNotes(null);
    offerDTO.setOperatingCostGross(null);
    offerDTO.setIsOperatingCostCorrected(false);
    offerDTO.setOperatingCostGrossCorrected(null);
    offerDTO.setOperatingCostDiscount(0.0);
    offerDTO.setIsOperatingCostDiscountPercentage(true);
    offerDTO.setOperatingCostDiscount2(0.0);
    offerDTO.setIsOperatingCostDiscount2Percentage(true);
    offerDTO.setOperatingCostsAmount(null);
    offerDTO.
      setOperatingCostVat(Double.valueOf(sDVatService.getCurrentMainVatRate().getValue2()));
    offerDTO.setOperatingCostIsVatPercentage(true);
    offerDTO.setOperatingCostsInPercentage(null);
    offerService.updateOffer(offerDTO);
    criterionService
      .deleteOperatingCostCriterion(offerDTO.getSubmittent().getSubmissionId().getId());
    return Response.ok().build();
  }

  /**
   * Function to validate if manual award is possible.
   *
   * @param manualAwardForm the manual award form
   * @return the response
   */
  @PUT
  @Path("/validateManualAward")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response validateManualAward(@Valid ManualAwardForm manualAwardForm) {
    Set<ValidationError> errors = new HashSet<>();
    if (manualAwardForm.getOfferIds() == null || manualAwardForm.getOfferIds().isEmpty()) {
      // If no offers have been chosen for award assignment, return error.
      errors.add(new ValidationError("submittentNotSelected",
        ValidationMessages.SUBMITTENT_NOT_SELECTED_ERROR));
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    // Get submission data.
    SubmissionDTO submissionDTO =
      submissionService.getSubmissionById(manualAwardForm.getSubmissionId());
    // Get the submission threshold value.
    BigDecimal thresholdValue =
      submissionService.getThresholdValue(manualAwardForm.getSubmissionId());

    // Get the offers to be awarded.
    List<OfferDTO> offerDTOs = offerService.getOffersByOfferIds(manualAwardForm.getOfferIds());
    // Check if user is PL and the submission is above threshold.
    if (userService.getUserGroupName().equals(Group.PL.getValue())
      && (submissionDTO.getProcess().equals(Process.NEGOTIATED_PROCEDURE)
      || submissionDTO.getProcess().equals(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION))) {

      for (OfferDTO offerDTO : offerDTOs) {
        // Check if any of the offer amounts exceeds the threshold.
        if (offerDTO.getAmount().compareTo(thresholdValue) > 0) {
          errors.add(new ValidationError("offerAboveThresholdPL",
            ValidationMessages.OFFER_ABOVE_THRESHOLD_PL));
          errors.add(new ValidationError(offerDTO.getId(),
            ValidationMessages.OFFER_ABOVE_THRESHOLD_PL));
        }
      }
      if (!errors.isEmpty()) {
        return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
      }
    } else if (userService.getUserGroupName().equals(Group.ADMIN.getValue())
      && submissionDTO.getReasonFreeAward().getId() == null
      && (submissionDTO.getProcess().equals(Process.NEGOTIATED_PROCEDURE)
      || submissionDTO.getProcess().equals(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION))) {
      // Check if in case of Admin the reasonFreeAward value is null when the
      // submission is above
      // threshold.
      for (OfferDTO offerDTO : offerDTOs) {
        // Check if any of the offer amounts exceeds the threshold.
        if (offerDTO.getAmount().compareTo(thresholdValue) > 0) {
          errors.add(new ValidationError("errorReasonFreeAwardAdmin",
            ValidationMessages.ERROR_ABOVE_THRESHOLD_ADMIN));
          return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
        }
      }
    }
    // Do not proceed with manual award if submission process is not compatible with
    // manual award.
    if (!(submissionDTO.getProcess().equals(Process.NEGOTIATED_PROCEDURE)
      || submissionDTO.getProcess().equals(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION)
      || ((submissionDTO.getProcess().equals(Process.SELECTIVE)
      || submissionDTO.getProcess().equals(Process.OPEN))
      && (submissionDTO.getIsServiceTender() != null
      && submissionDTO.getIsServiceTender())))) {
      return Response.status(Response.Status.BAD_REQUEST).entity(null).build();
    }
    return Response.ok().build();
  }

  /*---- Helper methods ----*/

  /**
   * Update offer validation.
   *
   * @param offer the offer
   * @return the validation errors
   */
  private Set<ValidationError> updateOfferValidation(OfferForm offer) {
    Set<ValidationError> errors = new HashSet<>();
    if (offer != null) {
      /* Mandatory validation */
      updateOfferMandatoryValidation(offer, errors);
      /* Notes validation */
      notesValidation(offer, errors);
      /* Variant notes validation */
      variantNotesValidation(offer, errors);
      /* Price increase validation */
      priceIncreaseValidation(offer, errors);
      /* Operating cost note validation */
      operatingCostNotesValidation(offer, errors);
      /* Amount validation */
      amountValidation(offer, errors);
    }
    return errors;
  }

  /**
   * Delete submittent validation.
   *
   * @param submittentHasOffer the submittent has offer
   * @param submittentHasSubcontractors the submittent has subcontractor
   * @return the validation errors
   */
  private Set<ValidationError> deleteSubmittentValidation(Boolean submittentHasOffer,
    Boolean submittentHasSubcontractors) {

    Set<ValidationError> errors = new HashSet<>();

    submittentHasOfferValidation(submittentHasOffer, errors);
    submittentHasSubcontractorsValidation(submittentHasSubcontractors, errors);
    return errors;
  }

  /**
   * Operating cost validation.
   *
   * @param submittentHasOffer the submittent has offer
   * @param submittentHasSubcontractors the submittent has subcontractors
   * @param offer the offer
   * @return the sets the
   */
  private Set<ValidationError> operatingCostValidation(Boolean submittentHasOffer,
    Boolean submittentHasSubcontractors, OfferForm offer) {

    Set<ValidationError> errors = new HashSet<>();

    submittentHasOfferValidation(submittentHasOffer, errors);
    submittentHasSubcontractorsValidation(submittentHasSubcontractors, errors);

    if (offer != null) {
      if (offer.getOperatingCostsAmount() == null
        || offer.getOperatingCostsAmount().doubleValue() == 0
        && (offer.getSubmittent().getSubmissionId().getIsServiceTender() == null
        || !offer.getSubmittent().getSubmissionId().getIsServiceTender())) {
        errors.add(new ValidationError("operatingCostsAmountErrorField",
          ValidationMessages.ZERO_AMOUNT_ERROR_MESSAGE));
      }
      String status = submissionService
        .getCurrentStatusOfSubmission(offer.getSubmittent().getSubmissionId().getId());
      if (Integer.parseInt(status) < (Integer
        .parseInt(TenderStatus.OFFER_OPENING_CLOSED.getValue()))) {
        if (offer.getOperatingCostGross() == null || offer.getOperatingCostGross().equals(0.0)) {
          OfferDTO offerDTO = offerService.getOfferById(offer.getId());
          if (offerDTO.getOperatingCostGross() != null && !offerDTO.getOperatingCostGross()
            .equals(0.0)) {
            errors.add(new ValidationError("operatingCostGrossErrorField",
              ValidationMessages.OFFER_OPERATINGCOSTGROSS_ERROR));
          }
        }
        // error message for operatingCosts Tab if user has not filled offerDate in
        // offerDetails Tab
        if (offer.getOfferDate() == null) {
          errors.add(new ValidationError(OFFER_DATE, ValidationMessages.OFFER_DATE_ERROR));
        }
        if (offer.getOperatingCostNotes() != null && offer.getOperatingCostNotes().length() > 100) {

          errors.add(new ValidationError("operatingCostNotesErrorField",
            ValidationMessages.OFFER_MAX_SIZE_OPERATING_COST_NOTES));
          errors.add(new ValidationError("operatingCostNotes",
            ValidationMessages.OFFER_MAX_SIZE_OPERATING_COST_NOTES));
        }
      } else {
        if (offer.getIsOperatingCostCorrected() && (offer.getOperatingCostGrossCorrected() == null
          || offer.getOperatingCostGrossCorrected().equals(0.0))) {
          errors.add(new ValidationError("operatingCostGrossCorrectedErrorField",
            ValidationMessages.OFFER_OPERATINGCOSTGROSS_CORRECTED));
        }
      }
    }
    return errors;
  }

  /**
   * Reset offer validation.
   *
   * @param offer the offer
   * @return the validation errors
   */
  private Set<ValidationError> resetOfferValidation(OfferForm offer) {
    Set<ValidationError> errors = new HashSet<>();
    if (offer != null) {
      String status = submissionService
        .getCurrentStatusOfSubmission(offer.getSubmittent().getSubmissionId().getId());
      if (!status.equals(TenderStatus.OFFER_OPENING_CLOSED.getValue())
        && offer.getOfferDate() == null) {
        errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
          ValidationMessages.OFFER_MANDATORY_ERROR_MESSAGE));
        errors.add(new ValidationError(ERROR_DATE_FIELD,
          ValidationMessages.OFFER_DATE_ERROR_MESSAGE));
        // error message for ancillaryCosts Tab if user has not filled offerDate in
        // offerDetails Tab
        errors.add(new ValidationError(OFFER_DATE,
          ValidationMessages.OFFER_DATE_ERROR));
      }
    }
    return errors;
  }

  /**
   * Applicant validation.
   *
   * @param applicantHasApplication the applicant has application
   * @param applicantHasSubcontractorsOrJointVenture the applicant has subcontractors or joint
   * venture
   * @return the errors if present.
   */
  private Set<ValidationError> applicationValidation(Boolean applicantHasApplication,
    Boolean applicantHasSubcontractorsOrJointVenture) {
    Set<ValidationError> errors = new HashSet<>();
    if (applicantHasApplication) {
      errors.add(new ValidationError("applicantHasApplicationErrorField",
        ValidationMessages.APPLICANT_HAS_APPLICATION_ERROR_MESSAGE));
    }
    if (applicantHasSubcontractorsOrJointVenture) {
      errors.add(new ValidationError("applicantHasSubcontractorsOrJointVentureErrorField",
        ValidationMessages.SUBCONTRACTOR_EXISTS));
    }
    return errors;
  }

  /**
   * Close offer validation
   *
   * @param results the results
   * @return the validation errors
   */
  private Set<ValidationError> closeOfferValidation(List<String> results) {
    Set<ValidationError> errors = new HashSet<>();
    if (!results.isEmpty()) {
      for (String result : results) {
        if (result.equals(ValidationMessages.MANDATORY_OFFER_PROTOCOL_DOCUMENT_MESSAGE)) {
          errors.add(new ValidationError("offerProtocolErrorField",
            ValidationMessages.MANDATORY_OFFER_PROTOCOL_DOCUMENT_MESSAGE));
        } else if (result.equals(ValidationMessages.MANDATORY_SUBMITTENTLISTE_DOCUMENT_MESSAGE)) {
          errors.add(new ValidationError("submittentlisteErrorField",
            ValidationMessages.MANDATORY_SUBMITTENTLISTE_DOCUMENT_MESSAGE));
        } else if (result.equals(ValidationMessages.MANDATORY_OFFER_PROTOCOL_DL_DOCUMENT_MESSAGE)) {
          errors.add(new ValidationError("offerProtocolErrorField",
            ValidationMessages.MANDATORY_OFFER_PROTOCOL_DL_DOCUMENT_MESSAGE));
        }
        if (result.equals(ValidationMessages.OFFERS_NO_DATE_ERROR)) {
          errors.add(new ValidationError("offersNoDateErrorField",
            ValidationMessages.OFFERS_NO_DATE_ERROR));
        }
      }
    }
    return errors;
  }

  /**
   * submittentHasOffer validation.
   *
   * @param submittentHasOffer the boolean submittentHasOffer
   * @param errors the validation errors
   */
  private void submittentHasOfferValidation(Boolean submittentHasOffer,
    Set<ValidationError> errors) {
    if (submittentHasOffer != null && submittentHasOffer) {
      errors.add(new ValidationError("submittentHasOfferField",
        ValidationMessages.OFFER_EXISTS));
    }
  }

  /**
   * submittentHasSubcontractors validation.
   *
   * @param submittentHasSubcontractors the boolean submittentHasSubcontractors
   * @param errors the validation errors
   */
  private void submittentHasSubcontractorsValidation(Boolean submittentHasSubcontractors,
    Set<ValidationError> errors) {
    if (submittentHasSubcontractors != null && submittentHasSubcontractors) {
      errors.add(new ValidationError("submittentHasSubcontractorsField",
        ValidationMessages.SUBCONTRACTOR_EXISTS));
    }
  }

  /**
   * Mandatory validation for update offer.
   *
   * @param offer the offer form
   * @param errors the validation errors
   */
  private void updateOfferMandatoryValidation(OfferForm offer, Set<ValidationError> errors) {
    // Check mandatory fields
    boolean mandatoryFields =
      offer.getOfferDate() == null || offer.getGrossAmount() == null || offer.getGrossAmount() == 0;
    // Check if submission is DL WW
    boolean isServiceTender = offer.getSubmittent().getSubmissionId().getIsServiceTender() != null
      && offer.getSubmittent().getSubmissionId().getIsServiceTender();

    /*
     * If submission is DL WW we check only for date error field.
     * Offers with 0.00 amount should be accepted.
     */
    if (isServiceTender) {
      if (offer.getOfferDate() == null) {
        errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
          ValidationMessages.OFFER_MANDATORY_ERROR_MESSAGE));
        errors.add(new ValidationError(OFFER_DATE,
          ValidationMessages.OFFER_MANDATORY_ERROR_MESSAGE));
      }
    } else {
      if (mandatoryFields) {
        errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
          ValidationMessages.OFFER_MANDATORY_ERROR_MESSAGE));
        if (offer.getOfferDate() == null) {
          errors.add(new ValidationError(OFFER_DATE,
            ValidationMessages.OFFER_MANDATORY_ERROR_MESSAGE));
        }
        if (offer.getGrossAmount() == null || offer.getGrossAmount() == 0) {
          errors.add(new ValidationError("grossAmount",
            ValidationMessages.OFFER_MANDATORY_ERROR_MESSAGE));
        }
      }
    }
  }

  /**
   * Notes validation for update offer.
   *
   * @param offer the offer form
   * @param errors the validation errors
   */
  private void notesValidation(OfferForm offer, Set<ValidationError> errors) {
    if (offer.getNotes() != null && offer.getNotes().length() > 500) {
      errors.add(new ValidationError("offerNotesErrorField",
        ValidationMessages.OFFER_MAX_SIZE_NOTES_MESSAGE));
      errors.add(new ValidationError("notes",
        ValidationMessages.OFFER_MAX_SIZE_NOTES_MESSAGE));
    }
  }

  /**
   * Variant notes validation for update offer.
   *
   * @param offer the offer form
   * @param errors the validation errors
   */
  private void variantNotesValidation(OfferForm offer, Set<ValidationError> errors) {
    if (offer.getIsVariant() != null && offer.getVariantNotes() != null && offer.getIsVariant()
      && offer.getVariantNotes().length() > 20) {
      errors.add(new ValidationError("offerVariantNotesErrorField",
        ValidationMessages.OFFER_MAX_SIZE_VARIANT_NOTES_MESSAGE));
      errors.add(
        new ValidationError("variantNotes",
          ValidationMessages.OFFER_MAX_SIZE_VARIANT_NOTES_MESSAGE));
    }
  }

  /**
   * Price increase validation for update offer.
   *
   * @param offer the offer form
   * @param errors the validation errors
   */
  private void priceIncreaseValidation(OfferForm offer, Set<ValidationError> errors) {
    if (offer.getPriceIncrease() != null && offer.getPriceIncrease().length() > 50) {
      errors.add(new ValidationError("offerPriceIncreaseErrorField",
        ValidationMessages.OFFER_MAX_SIZE_PRICE_INCREASE_MESSAGE));
      errors.add(new ValidationError("priceIncrease",
        ValidationMessages.OFFER_MAX_SIZE_PRICE_INCREASE_MESSAGE));
    }
  }

  /**
   * Operating cost notes for update offer.
   *
   * @param offer the offer form
   * @param errors the validation errors
   */
  private void operatingCostNotesValidation(OfferForm offer, Set<ValidationError> errors) {
    if (offer.getOperatingCostNotes() != null && offer.getOperatingCostNotes().length() > 100) {
      errors.add(new ValidationError("operatingCostNotesErrorField",
        ValidationMessages.OFFER_MAX_SIZE_OPERATING_COST_NOTES));
      errors.add(new ValidationError("operatingCostNotes",
        ValidationMessages.OFFER_MAX_SIZE_OPERATING_COST_NOTES));
    }
  }

  /**
   * Amount validation for update offer.
   *
   * @param offer the offer form
   * @param errors the validation errors
   */
  private void amountValidation(OfferForm offer, Set<ValidationError> errors) {
    if (offer.getAmount() == null || offer.getAmount().doubleValue() == 0
      && (offer.getSubmittent().getSubmissionId().getIsServiceTender() == null
      || !offer.getSubmittent().getSubmissionId().getIsServiceTender())) {
      errors.add(new ValidationError("offerAmountErrorField",
        ValidationMessages.ZERO_AMOUNT_ERROR_MESSAGE));
    }
  }
}
