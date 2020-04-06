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

import ch.bern.submiss.services.api.administration.OfferService;
import ch.bern.submiss.services.api.administration.SubmissionService;
import ch.bern.submiss.services.api.constants.SuitabilityAuditDropdownChoices;
import ch.bern.submiss.services.api.constants.TenderStatus;
import ch.bern.submiss.services.api.dto.OfferDTO;
import ch.bern.submiss.services.api.dto.SubmissionDTO;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.api.util.ValidationMessages;
import ch.bern.submiss.web.forms.CommissionProcurementDecisionForm;
import ch.bern.submiss.web.forms.CommissionProcurementProposalForm;
import ch.bern.submiss.web.forms.OfferForm;
import ch.bern.submiss.web.forms.ReopenForm;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
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
 * The Class CommissionProcurementResource.
 */
@Path("/commissionProcurement")
@Singleton
public class CommissionProcurementResource {

  /**
   * The Constant MAX_DECISION_RECOMMENDATION_LENGTH.
   */
  private static final int MAX_DECISION_RECOMMENDATION_LENGTH = 200;
  @OsgiService
  @Inject
  private SubmissionService submissionService;
  @OsgiService
  @Inject
  private OfferService offerService;

  /**
   * Function to retrieve offers of submission
   */
  @GET
  @Path("/submissionOffers/{submissionId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response getOffersBySubmission(@PathParam("submissionId") String submissionId) {
    return Response.ok(offerService.getOffersBySubmission(submissionId)).build();
  }

  /**
   * This method returns the submission for the commission procurement proposal
   *
   * @param submissionId the submission id
   * @return SubmissionDTO
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{submissionId}")
  public Response getSubmissionForCommissionProcurementProposal(
    @PathParam("submissionId") String submissionId) {
    SubmissionDTO submissionDTO = submissionService.getSubmissionById(submissionId);
    return Response.ok(submissionDTO).build();
  }

  /**
   * Update the commission procurement proposal
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/updateCommissionProcurementProposal/{submissionId}")
  public Response updateCommissionProcurementProposal(@Valid CommissionProcurementProposalForm form,
    @PathParam("submissionId") String submissionId) {
    /* Calling validation function  */
    Set<ValidationError> errors = commissionProcurementProposalValidation(form);
    if (!errors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    /* Updating values of submission */
    SubmissionDTO submissionDTO = submissionService.getSubmissionById(submissionId);
    submissionDTO.setCommissionProcurementProposalBusiness(form.getBusiness());
    submissionDTO.setCommissionProcurementProposalDate(form.getDate());
    submissionDTO.setCommissionProcurementProposalObject(form.getObject());
    submissionDTO.setCommissionProcurementProposalPreRemarks(form.getPreRemarks());
    submissionDTO.setCommissionProcurementProposalReservation(form.getReservation());
    submissionDTO.setCommissionProcurementProposalSuitabilityAuditDropdown(
      form.getSuitabilityAuditDropdown());
    submissionDTO
      .setCommissionProcurementProposalSuitabilityAuditText(form.getSuitabilityAuditText());
    submissionDTO.setCommissionProcurementProposalReasonGiven(form.getReasonGiven());
    submissionService.updateSubmission(submissionDTO);
    // Create list where offer ids are going to be stored.
    List<String> offerIds = new ArrayList<>();
    // Map offer ids to offer forms (award recipients).
    Map<String, OfferForm> offerIdsToOfferForms = new HashMap<>();
    for (OfferForm offerForm : form.getAwardRecipients()) {
      offerIds.add(offerForm.getId());
      offerIdsToOfferForms.put(offerForm.getId(), offerForm);
    }
    // Get offer DTOs by their offer ids.
    List<OfferDTO> offerDTOs = offerService.getOffersByOfferIds(offerIds);
    for (OfferDTO offerDTO : offerDTOs) {
      // For every offer DTO set the awardRecipientFreeTextField value from the corresponding offer
      // form (award recipient).
      offerDTO.setAwardRecipientFreeTextField(
        offerIdsToOfferForms.get(offerDTO.getId()).getAwardRecipientFreeTextField());
    }
    // Update the award recipients.
    offerService.updateAwardRecipients(offerDTOs);
    /* Set submission status to commission procurement proposal started if not already set */
    if (!submissionService.getCurrentStatusOfSubmission(submissionId)
      .equals(TenderStatus.COMMISSION_PROCUREMENT_PROPOSAL_STARTED.getValue())) {
      submissionService.startCommissionProcurementProposal(submissionDTO);
    }
    return Response.ok().build();
  }

  /**
   * Checking for invalid values
   */
  private Set<ValidationError> commissionProcurementProposalValidation(
    CommissionProcurementProposalForm form) {
    Set<ValidationError> errors = new HashSet<>();
    if (form != null) {
      if (form.getDate() == null) {
        errors.add(new ValidationError("commissionProcurementDate",
          ValidationMessages.MANDATORY_ERROR_MESSAGE));
        errors.add(new ValidationError("procurementDateErrorField",
          ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if (form.getObject() != null && form.getObject().length() > 500) {
        errors.add(new ValidationError("procurementObject",
          ValidationMessages.CPP_OBJECT_ERROR_MESSAGE));
        errors.add(new ValidationError("procurementObjectErrorField",
          ValidationMessages.CPP_OBJECT_ERROR_MESSAGE));
      }
      if (form.isInvalidBusiness()
        || (form.getBusiness() != null && (form.getBusiness().doubleValue() < 0
        || form.getBusiness().doubleValue() != form.getBusiness().intValue()))) {
        errors.add(new ValidationError("commissionProcurementBusiness",
          ValidationMessages.CPP_BUSINESS_ERROR_MESSAGE));
        errors.add(new ValidationError("procurementBusinessErrorField",
          ValidationMessages.CPP_BUSINESS_ERROR_MESSAGE));
      }
      if (form.getPreRemarks() != null && form.getPreRemarks().length() > 1000) {
        errors.add(new ValidationError("procurementPreRemarks",
          ValidationMessages.CPP_PRE_REMARKS_ERROR_MESSAGE));
        errors.add(new ValidationError("procurementPreRemarksErrorField",
          ValidationMessages.CPP_PRE_REMARKS_ERROR_MESSAGE));
      }
      if (form.getSuitabilityAuditText() != null && form.getSuitabilityAuditText().length() > 500) {
        errors.add(new ValidationError("procurementSuitabilityAuditText",
          ValidationMessages.CPP_SUITABILITY_AUDIT_TEXT_ERROR_MESSAGE));
        errors.add(new ValidationError("procurementSuitabilityAuditTextErrorField",
          ValidationMessages.CPP_SUITABILITY_AUDIT_TEXT_ERROR_MESSAGE));
      }
      if (form.getReservation() != null && form.getReservation().length() > 1000) {
        errors.add(new ValidationError("procurementReservation",
          ValidationMessages.CPP_RESERVATION_ERROR_MESSAGE));
        errors.add(new ValidationError("procurementReservationErrorField",
          ValidationMessages.CPP_RESERVATION_ERROR_MESSAGE));
      }
      if (form.getReasonGiven() != null && form.getReasonGiven().length() > 10000) {
        errors.add(new ValidationError("procurementReasonGiven",
          ValidationMessages.CPP_REASON_GIVEN_ERROR_MESSAGE));
        errors.add(new ValidationError("procurementReasonGivenErrorField",
          ValidationMessages.CPP_REASON_GIVEN_ERROR_MESSAGE));
      }
      if (form.getAwardRecipients() != null && !form.getAwardRecipients().isEmpty()) {
        /* Check every award recipient for invalid values */
        for (OfferForm offer : form.getAwardRecipients()) {
          if (offer.getAwardRecipientFreeTextField() != null
            && offer.getAwardRecipientFreeTextField().length() > 1000) {
            errors.add(new ValidationError("procurementFreeTextField" + offer.getId(),
              ValidationMessages.CPP_FREE_TEXT_FIELD_ERROR_MESSAGE));
            errors.add(new ValidationError("procurementFreeTextFieldErrorField",
              ValidationMessages.CPP_FREE_TEXT_FIELD_ERROR_MESSAGE));
          }
        }
      }
    }
    return errors;
  }

  /**
   * Function to retrieve the suitability audit drop-down choices
   *
   * @return the suitability audit drop-down choices
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/suitabilityAuditDropdownChoices")
  public List<String> getSuitabilityAuditDropdownChoices() {
    List<String> suitabilityAuditDropdownChoices = new ArrayList<>();
    suitabilityAuditDropdownChoices
      .add(SuitabilityAuditDropdownChoices.DROPDOWN_CHOICE_0.getValue());
    suitabilityAuditDropdownChoices
      .add(SuitabilityAuditDropdownChoices.DROPDOWN_CHOICE_1.getValue());
    return suitabilityAuditDropdownChoices;
  }

  /**
   * Function to check for errors when closing the commission procurement proposal.
   *
   * @param submissionId the submission id
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/closeProposalNoErrors/{submissionId}")
  public Response closeProposalNoErrors(@PathParam("submissionId") String submissionId) {
    boolean proposalDocumentExists = submissionService.proposalDocumentExists(submissionId);
    Set<ValidationError> errors = new HashSet<>();
    if (!proposalDocumentExists) {
      errors.add(new ValidationError("noProposalDocumentErrorField",
        ValidationMessages.NO_PROPOSAL_DOCUMENT_ERROR_MESSAGE));
    }
    if (!errors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    return Response.ok().build();
  }

  /**
   * Function to close the commission procurement proposal.
   *
   * @param submissionId the submission id
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/closeCommissionProcurementProposal/{submissionId}")
  public Response closeCommissionProcurementProposal(
    @PathParam("submissionId") String submissionId) {
    submissionService.closeCommissionProcurementProposal(submissionId);
    return Response.ok().build();
  }

  /**
   * This function implements the reopening of the commission procurement proposal.
   *
   * @param reopenForm the reopen form
   * @param submissionId the submission id
   * @return the response
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/reopen/commissionProcurementProposal/{submissionId}")
  public Response reopenCommissionProcurementProposal(@Valid ReopenForm reopenForm,
    @PathParam("submissionId") String submissionId) {
    submissionService
      .reopenCommissionProcurementProposal(reopenForm.getReopenReason(), submissionId);
    return Response.ok().build();
  }

  /**
   * Update the commission procurement decision
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/updateCommissionProcurementDecision/{submissionId}")
  public Response updateCommissionProcurementDecision(
    @PathParam("submissionId") String submissionId,
    @Valid CommissionProcurementDecisionForm commissionProcurementDecisionForm) {
    Set<ValidationError> errors = new HashSet<>();
    if (commissionProcurementDecisionForm.getRecommendation() != null
      && commissionProcurementDecisionForm.getRecommendation().length()
      > MAX_DECISION_RECOMMENDATION_LENGTH) {
      errors.add(new ValidationError("decisionRecommendation",
        ValidationMessages.DECISION_RECOMMENDATION_ERROR_MESSAGE));
      errors.add(new ValidationError("decisionRecommendationErrorField",
        ValidationMessages.DECISION_RECOMMENDATION_ERROR_MESSAGE));
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    // Updating values of submission.
    SubmissionDTO submissionDTO = submissionService.getSubmissionById(submissionId);
    submissionDTO.setCommissionProcurementDecisionRecommendation(
      commissionProcurementDecisionForm.getRecommendation());
    submissionService.updateSubmission(submissionDTO);
    // Set submission status to commission procurement decision started if not already set.
    if (!submissionService.getCurrentStatusOfSubmission(submissionId)
      .equals(TenderStatus.COMMISSION_PROCUREMENT_DECISION_STARTED.getValue())) {
      submissionService.updateSubmissionStatus(submissionId,
        TenderStatus.COMMISSION_PROCUREMENT_DECISION_STARTED.getValue(), null, null,
        LookupValues.INTERNAL_LOG);
    }
    return Response.ok().build();
  }

  /**
   * Function to check for errors when closing the commission procurement decision.
   *
   * @param submissionId the submission id
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/closeDecisionNoErrors/{submissionId}")
  public Response closeDecisionNoErrors(@PathParam("submissionId") String submissionId) {
    boolean decisionDocumentExists = submissionService.decisionDocumentExists(submissionId);
    Set<ValidationError> errors = new HashSet<>();
    if (!decisionDocumentExists) {
      errors.add(new ValidationError("noDecisionDocumentErrorField",
        ValidationMessages.NO_DECISION_DOCUMENT_ERROR_MESSAGE));
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    return Response.ok().build();
  }

  /**
   * Function to close the commission procurement decision.
   *
   * @param submissionId the submission id
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/closeCommissionProcurementDecision/{submissionId}")
  public Response closeCommissionProcurementDecision(
    @PathParam("submissionId") String submissionId) {
    submissionService.closeCommissionProcurementDecision(submissionId);
    return Response.ok().build();
  }

  /**
   * This function implements the reopening of the commission procurement decision.
   *
   * @param reopenForm the reopen form
   * @param submissionId the submission id
   * @return the response
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/reopen/commissionProcurementDecision/{submissionId}")
  public Response reopenCommissionProcurementDecision(@Valid ReopenForm reopenForm,
    @PathParam("submissionId") String submissionId) {
    submissionService
      .reopenCommissionProcurementDecision(reopenForm.getReopenReason(), submissionId);
    return Response.ok().build();
  }
}
