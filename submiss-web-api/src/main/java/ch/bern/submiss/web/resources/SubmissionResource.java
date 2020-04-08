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

import ch.bern.submiss.services.api.administration.LegalHearingService;
import ch.bern.submiss.services.api.administration.ProjectService;
import ch.bern.submiss.services.api.administration.RuleService;
import ch.bern.submiss.services.api.administration.SDProofService;
import ch.bern.submiss.services.api.administration.SubmissionCloseService;
import ch.bern.submiss.services.api.administration.SubmissionService;
import ch.bern.submiss.services.api.constants.AuditMessages;
import ch.bern.submiss.services.api.constants.Process;
import ch.bern.submiss.services.api.constants.SelectiveLevel;
import ch.bern.submiss.services.api.constants.StatusToReopen;
import ch.bern.submiss.services.api.constants.TenderStatus;
import ch.bern.submiss.services.api.dto.LegalHearingTerminateDTO;
import ch.bern.submiss.services.api.dto.SubmissionDTO;
import ch.bern.submiss.services.api.dto.SubmittentDTO;
import ch.bern.submiss.services.api.dto.SubmittentOfferDTO;
import ch.bern.submiss.services.api.dto.TenderStatusHistoryDTO;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.api.util.ValidationMessages;
import ch.bern.submiss.services.api.util.View;
import ch.bern.submiss.web.forms.AwardInfoFirstLevelForm;
import ch.bern.submiss.web.forms.AwardInfoForm;
import ch.bern.submiss.web.forms.AwardInfoOfferFirstLevelForm;
import ch.bern.submiss.web.forms.AwardInfoOfferForm;
import ch.bern.submiss.web.forms.ExaminationForm;
import ch.bern.submiss.web.forms.LegalExclusionForm;
import ch.bern.submiss.web.forms.LegalHearingExclusionForm;
import ch.bern.submiss.web.forms.LegalHearingTerminateForm;
import ch.bern.submiss.web.forms.ProjectForm;
import ch.bern.submiss.web.forms.ReopenForm;
import ch.bern.submiss.web.forms.SubmissionForm;
import ch.bern.submiss.web.forms.SubmittentForm;
import ch.bern.submiss.web.mappers.AwardInfoFirstLevelFormMapper;
import ch.bern.submiss.web.mappers.AwardInfoFormMapper;
import ch.bern.submiss.web.mappers.LegalHearingExclusionFormMapper;
import ch.bern.submiss.web.mappers.LegalHearingTerminateFormMapper;
import ch.bern.submiss.web.mappers.SubmissionMapper;
import ch.bern.submiss.web.mappers.SubmittentFormMapper;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import com.fasterxml.jackson.annotation.JsonView;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.OptimisticLockException;
import javax.persistence.RollbackException;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.commons.lang3.StringUtils;
import org.ops4j.pax.cdi.api.OsgiService;

/**
 * REST handler for submission.
 */
@Path("/submission")
@Singleton
public class SubmissionResource {

  /**
   * The Constant REASON_GIVEN.
   */
  private static final String REASON_GIVEN = "reasonGiven";
  /**
   * The Constant INVALID_GRADES.
   */
  private static final String INVALID_GRADES = "invalid_grades";
  /**
   * The Constant EMPTY_VALUES.
   */
  private static final String EMPTY_VALUES = "empty_values";
  /**
   * The Constant INVALID_SUBCRITERION_WEIGHTINGS.
   */
  private static final String INVALID_SUBCRITERION_WEIGHTINGS = "invalid_subcriterion_weightings";
  /**
   * The Constant INVALID_CRITERION_WEIGHTINGS.
   */
  private static final String INVALID_CRITERION_WEIGHTINGS = "invalid_criterion_weightings";
  /**
   * The Constant NOTES.
   */
  private static final String NOTES = "notes";
  /**
   * The Constant NULL_MIN_GRADE_MAX_GRADE.
   */
  private static final String NULL_MIN_GRADE_MAX_GRADE = "null_min_grade_max_grade";
  /**
   * The Constant NO_EXAMINATION_DOCUMENT.
   */
  private static final String NO_EXAMINATION_DOCUMENT = "no_examination_document";
  /**
   * The Constant PM_DEPARTMENT_NAME.
   */
  private static final String PM_DEPARTMENT_NAME = "pmDepartmentName";
  /**
   * The Constant PROOF_INCONSISTENCIES.
   */
  private static final String PROOF_INCONSISTENCIES = "proofInconsistencies";
  /**
   * The submission service.
   */
  @OsgiService
  @Inject
  private SubmissionService submissionService;
  /**
   * The rule service.
   */
  @OsgiService
  @Inject
  private RuleService ruleService;
  /**
   * The submission close service.
   */
  @OsgiService
  @Inject
  private SubmissionCloseService submissionCloseService;
  /**
   * The legal hearing service.
   */
  @OsgiService
  @Inject
  private LegalHearingService legalHearingService;

  /**
   * The sd proof service.
   */
  @OsgiService
  @Inject
  private SDProofService sDProofService;

  /**
   * The project service.
   */
  @OsgiService
  @Inject
  private ProjectService projectService;

  /**
   * Creates a new submission.
   *
   * @param submission the submission to be created
   * @return Ok
   */
  @POST
  @Path("/create")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response create(@Valid SubmissionForm submission) {
    submissionService.submissionCreateSecurityCheck();
    // Check for validation errors
    Set<ValidationError> validationErrors = validation(null, submission);
    if (!validationErrors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(validationErrors).build();
    }
    // Check for optimistic locking in Project before creating the new submission
    Set<ValidationError> optimisticLockErrors = projectService
      .optimisticLockProject(submission.getProject().getId(), submission.getProject().getVersion());
    if (!optimisticLockErrors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(optimisticLockErrors).build();
    }
    // If there are no errors proceed with submission creation
    String id =
      submissionService.createSubmission(SubmissionMapper.INSTANCE.toSubmissionDTO(submission));
    // Return JSON with the project id
    ProjectForm form = new ProjectForm();
    form.setId(id);
    return Response.ok(form).build();
  }

  /**
   * This method sets a company to a tender when a tenderer is added to the tenderer list.
   *
   * @param submissionId The id of the submission
   * @param companyIds the company ids
   * @return Ok
   */
  @PUT
  @Path("/addSubmittent/{submissionId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response setCompanyToSubmission(@PathParam("submissionId") String submissionId,
    @QueryParam("companyId") List<String> companyIds) {
    String id = submissionService.setCompanyToSubmission(submissionId, companyIds);
    // Return JSON with the offer id
    ProjectForm form = new ProjectForm();
    form.setId(id);
    return Response.ok(form).build();
  }

  /**
   * Updates a submission.
   *
   * @param submission the submission
   * @return Ok
   */
  @PUT
  @Path("/update")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response update(@Valid SubmissionForm submission) {
    Set<ValidationError> validationErrors = validation(null, submission);
    if (!validationErrors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(validationErrors).build();
    }
    Set<ValidationError> optimisticLockErrors = new HashSet<>();
    try {
      optimisticLockErrors = submissionService
        .updateSubmission(SubmissionMapper.INSTANCE.toSubmissionDTO(submission));
    } catch (OptimisticLockException e) {
      optimisticLockErrors
        .add(new ValidationError("optimisticLockErrorField", ValidationMessages.OPTIMISTIC_LOCK));
    }
    return (optimisticLockErrors.isEmpty())
      ? Response.ok().build()
      : Response.status(Response.Status.BAD_REQUEST).entity(optimisticLockErrors).build();
  }

  /**
   * Deletes a submission.
   *
   * @param id the UUID of the submission to be deleted
   * @return the response
   */
  @DELETE
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/delete/{id}")
  public Response deleteSubmission(@PathParam("id") String id) {
    // Check if submission is already deleted by another user
    if (!submissionService.submissionExists(id)) {
      throw new OptimisticLockException(ValidationMessages.SUBMISSION_DELETED);
    }
    // Check if submission has submittents
    Boolean submissionHasSubmittent = false;
    if (id != null) {
      submissionHasSubmittent = submissionService.findIfSubmissionHasSubmittent(id);
    }
    Set<ValidationError> errors = validation(submissionHasSubmittent, null);
    if (!errors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    submissionService.deleteSubmission(id);
    return Response.ok().build();
  }

  /**
   * This method returns a submission with a specific id.
   *
   * @param id the id
   * @return SubmissionDTO
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{id}")
  public Response getSubmissionById(@PathParam("id") String id) {
    SubmissionDTO submissionDTO = submissionService.getSubmissionById(id);
    if (submissionDTO == null) {
      throw new OptimisticLockException(ValidationMessages.SUBMISSION_NOT_FOUND);
    }
    // set the timestamp of the GET request from user
    submissionDTO.setPageRequestedOn(new Timestamp(new Date().getTime()));
    return Response.ok(submissionDTO).build();
  }

  /**
   * This method returns a list of companies of a specific submission.
   *
   * @param id The UUID of a submission
   * @return companyDTO list
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/companies/{id}")
  public Response getCompaniesBySubmission(@PathParam("id") String id) {
    List<SubmittentOfferDTO> companyDTOs = submissionService.getCompaniesBySubmission(id);
    return Response.ok(companyDTOs).build();
  }

  /**
   * Gets the submissions by project.
   *
   * @param projectId The id of the project
   * @return List of submissions that have been created for a project
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/project/{projectId}")
  public Response getSubmissionsByProject(@PathParam("projectId") String projectId) {
    List<SubmissionDTO> submissionProjectList =
      submissionService.getSubmissionsByProject(projectId);
    return Response.ok(submissionProjectList).build();
  }

  /**
   * Gets the current status of submission.
   *
   * @param submissionId the submission id
   * @return the current status of submission
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/status/{submissionId}")
  public Response getCurrentStatusOfSubmission(@PathParam("submissionId") String submissionId) {
    String currentStatusOfSubmission = submissionService.getCurrentStatusOfSubmission(submissionId);
    return Response.ok(currentStatusOfSubmission).build();
  }

  /**
   * Checks for submission status.
   *
   * @param submissionId the submission id
   * @param statusId the status id
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/status/{submissionId}/{statusId}")
  public Response hasSubmissionStatus(@PathParam("submissionId") String submissionId,
    @PathParam("statusId") String statusId) {
    Boolean hasSubmissionStatus = submissionService.hasSubmissionStatus(submissionId, statusId);
    return Response.ok(hasSubmissionStatus).build();
  }

  /**
   * Check submittent list.
   *
   * @param submissionId the submission id
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/status/submittent/{submissionId}")
  public Response checkSubmittentList(@PathParam("submissionId") String submissionId) {
    submissionService.checkSubmittentList(submissionId);
    return Response.ok().build();
  }

  /**
   * Checked submittent list.
   *
   * @param submissionId the submission id
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.TEXT_PLAIN)
  @Path("/status/checked/{submissionId}")
  public Response checkedSubmittentList(@PathParam("submissionId") String submissionId) {
    return Response.ok(submissionService.checkedSubmittentList(submissionId)).build();
  }

  /**
   * This method returns the list of the submittents of the selected submission.
   *
   * @param id The UUID of the selected submission
   * @return List of SubmittentDTO.
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{id}/submittents")
  public Response getSubmittentsBySubmission(@PathParam("id") String id) {
    List<SubmittentDTO> submittentDTOs = submissionService.getSubmittentsBySubmission(id);
    return Response.ok(submittentDTOs).build();
  }

  /**
   * This method updates a list of submittents in Formal Examination.
   *
   * @param submittentForms the submittent forms
   * @return the response
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/examination/update")
  public Response updateFormalAuditExamination(@Valid List<SubmittentForm> submittentForms) {
    // Check for validation errors
    Set<ValidationError> validationErrors = validateFormalAuditUpdate(submittentForms);
    if (!validationErrors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(validationErrors).build();
    }
    // Check for optimistic lock errors
    Set<ValidationError> optimisticLockErrors = new HashSet<>();
    try {
      optimisticLockErrors = submissionService.updateFormalAuditExamination(
        SubmittentFormMapper.INSTANCE.toSubmittentDTO(submittentForms));
    } catch (OptimisticLockException | RollbackException e) {
      optimisticLockErrors
        .add(new ValidationError(ValidationMessages.OPTIMISTIC_LOCK_ERROR_FIELD, ValidationMessages.OPTIMISTIC_LOCK));
    }
    return (optimisticLockErrors.isEmpty())
      ? Response.ok().build()
      : Response.status(Response.Status.BAD_REQUEST).entity(optimisticLockErrors).build();
  }

  /**
   * Validate formal audit update.
   *
   * @param submittentForms the submittent forms
   * @return the sets the
   */
  private Set<ValidationError> validateFormalAuditUpdate(List<SubmittentForm> submittentForms) {
    // Get submission process type.
    Process process = submittentForms.get(0).getSubmissionId().getProcess();
    Set<ValidationError> errors = new HashSet<>();
    if (process.equals(Process.NEGOTIATED_PROCEDURE)
      || process.equals(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION)) {
      // The variable i is used as iteration index.
      int i = 0;
      for (SubmittentForm submittentForm : submittentForms) {
        if ((!submittentForm.getFormalExaminationFulfilled()
          && submittentForm.getExistsExclusionReasons() != null
          && !submittentForm.getExistsExclusionReasons())
          || (submittentForm.getFormalExaminationFulfilled()
          && submittentForm.getExistsExclusionReasons() != null
          && submittentForm.getExistsExclusionReasons())) {
          if (StringUtils.isBlank(submittentForm.getFormalAuditNotes())) {
            // If mandatory field is empty.
            errors.add(new ValidationError("mandatoryFormalAuditNotes",
              ValidationMessages.MANDATORY_ERROR_MESSAGE));
            errors.add(new ValidationError(NOTES + i, ValidationMessages.MANDATORY_ERROR_MESSAGE));
          } else if (submittentForm.getFormalAuditNotes().length() < 10) {
            // If mandatory field value does not reach minimum length.
            errors.add(new ValidationError("formalAuditNotesLength",
              ValidationMessages.SUITABILITY_TEXT_ERROR_MESSAGE));
            errors.add(new ValidationError(NOTES + i,
              ValidationMessages.SUITABILITY_TEXT_ERROR_MESSAGE));
          }
        }
        if (submittentForm.getFormalAuditNotes() != null
          && submittentForm.getFormalAuditNotes().length() > 150) {
          // If formal audit notes value exceeds allowed length.
          errors.add(new ValidationError("formalAuditNotesLength",
            ValidationMessages.FORMAL_AUDIT_NOTES_MESSAGE));
          errors.add(new ValidationError(NOTES + i,
            ValidationMessages.FORMAL_AUDIT_NOTES_MESSAGE));
        }
        i++;
      }
    }
    return errors;
  }

  /**
   * This method updates the status of submission when the submittent list is updated.
   *
   * @param id the UUID of the submission.
   * @return the response
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/examination/formal/{id}")
  public Response updateSubmissionFormalAuditExaminationStatus(@PathParam("id") String id) {
    submissionService.updateSubmissionFormalAuditExaminationStatus(id);
    return Response.ok().build();
  }

  /**
   * Close formal audit.
   *
   * @param id the id
   * @return the response
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/formal/close/{id}/{version}")
  public Response closeFormalAudit(@PathParam("id") String id, @PathParam("version") Long version) {
    submissionService.checkOptimisticLockSubmission(id, version);
    Set<ValidationError> errors = new HashSet<>();
    SubmissionDTO submissionDTO = submissionService.getSubmissionById(id);

    // Check if the saved proof provided values differ from the current proof provided values.
    if (sDProofService
      .checkProofsForInconsistencies(submissionDTO, submissionService.mapProofProvidedValues(id))) {
      errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
        ValidationMessages.PROOF_INCONSISTENCIES_MESSAGE));
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }

    // Validate the submittent values of the formal audit form, before attempting to close the
    // formal audit. Only applicable for the
    // NEGOTIATED_PROCEDURE/NEGOTIATED_PROCEDURE_WITH_COMPETITION processes.
    if (submissionDTO.getProcess().equals(Process.NEGOTIATED_PROCEDURE)
      || submissionDTO.getProcess().equals(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION)) {
      List<SubmittentForm> submittentForms = SubmittentFormMapper.INSTANCE
        .toSubmittentForm(submissionService.getSubmittentsBySubmission(id));
      errors = validateFormalAuditUpdate(submittentForms);
    }

    if (!errors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }

    List<String> results = submissionService.closeFormalAudit(id);

    if (!results.isEmpty()) {
      for (String result : results) {
        // No exclusion reasons for selective process.
        if (result.equals(ValidationMessages.SELECTIVE_FORMAL_AUDIT_EMPTY_FIELD)) {
          errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
            ValidationMessages.SELECTIVE_FORMAL_AUDIT_EMPTY_FIELD));
        }
        // No exclusion reasons for negotiated procedure.
        else if (result.equals(ValidationMessages.NEGOTIATED_PROCEDURE_FORMAL_AUDIT_EMPTY_FIELD)) {
          errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
            ValidationMessages.NEGOTIATED_PROCEDURE_FORMAL_AUDIT_EMPTY_FIELD));
        }
        // Document not created.
        else if (result.equals(ValidationMessages.DOCUMENT_SHOULD_BE_CREATED)) {
          errors.add(new ValidationError(ValidationMessages.DOCUMENT_ERROR_FIELD,
            ValidationMessages.DOCUMENT_SHOULD_BE_CREATED));
        }
        // Proof document not created
        else if (result.equals(ValidationMessages.PROOF_DOCUMENT_SHOULD_BE_CREATED)) {
          errors.add(new ValidationError(ValidationMessages.PROOF_DOCUMENT_ERROR_FIELD,
            ValidationMessages.PROOF_DOCUMENT_SHOULD_BE_CREATED));
        }
        // Legal hearing document not created
        else if (result.equals(ValidationMessages.LEGAL_HEARING_DOCUMENT_SHOULD_BE_CREATED)) {
          errors.add(new ValidationError(ValidationMessages.LEGAL_HEARING_ERROR_FIELD,
            ValidationMessages.LEGAL_HEARING_DOCUMENT_SHOULD_BE_CREATED));
        }
      }
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }

    return Response.ok().build();
  }

  /**
   * Close examination.
   *
   * @param examinationForm the examination form
   * @return the response
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/examination/close")
  public Response closeExamination(@Valid ExaminationForm examinationForm) {
    submissionService.checkOptimisticLockSubmission(examinationForm.getSubmissionId(),
      examinationForm.getSubmissionVersion());
    List<String> results = submissionService.closeExamination(examinationForm.getSubmissionId(),
      examinationForm.getMinGrade(), examinationForm.getMaxGrade());
    // Check if errors have occurred.
    if (!results.isEmpty()) {
      Set<ValidationError> errors = new HashSet<>();
      for (String result : results) {
        // Invalid grades error.
        if (result.equals(INVALID_GRADES)) {
          errors.add(new ValidationError("invalidGradesField",
            ValidationMessages.CLOSE_EXAMINATION_INVALID_GRADES));
        }
        // Empty values error.
        else if (result.equals(EMPTY_VALUES)) {
          errors.add(new ValidationError("emptyFieldsField",
            ValidationMessages.CLOSE_EXAMINATION_EMPTY_FIELD));
        }
        // Invalid subcriteria weighting sum.
        else if (result.equals(INVALID_SUBCRITERION_WEIGHTINGS)) {
          errors.add(new ValidationError("invalidSubcriteriaWeightingsField",
            ValidationMessages.INVALID_SUBCRITERIA_WEIGHTINGS));
        }
        // Invalid criteria weighting sum.
        else if (result.equals(INVALID_CRITERION_WEIGHTINGS)) {
          errors.add(new ValidationError("invalidCriteriaWeightingsField",
            ValidationMessages.INVALID_CRITERIA_WEIGHTINGS));
        } else if (result.equals(ValidationMessages.DOCUMENT_SHOULD_BE_CREATED)) {
          errors.add(new ValidationError("mandatoryDocument",
            ValidationMessages.DOCUMENT_SHOULD_BE_CREATED));
        } else if (result.equals(ValidationMessages.LEGAL_HEARING_DOCUMENT_SHOULD_BE_CREATED)) {
          errors.add(new ValidationError("mandatoryLegalHearingDocument",
            ValidationMessages.LEGAL_HEARING_DOCUMENT_SHOULD_BE_CREATED));
        } else if (result.equals(ValidationMessages.PROOF_DOCUMENT_SHOULD_BE_CREATED)) {
          errors.add(new ValidationError("mandatoryProofDocument",
            ValidationMessages.PROOF_DOCUMENT_SHOULD_BE_CREATED));
        }
        // If minimum or maximum grades are set as null and are also mandatory fields. 
        else if (result.equals(NULL_MIN_GRADE_MAX_GRADE)) {
          errors.add(new ValidationError("nullMinGradeMaxGradeErrorField",
            ValidationMessages.MANDATORY_ERROR_MESSAGE));
          if (examinationForm.getMinGrade() == null) {
            errors.add(new ValidationError("minGrade",
              ValidationMessages.MANDATORY_ERROR_MESSAGE));
          }
          if (examinationForm.getMaxGrade() == null) {
            errors.add(new ValidationError("maxGrade",
              ValidationMessages.MANDATORY_ERROR_MESSAGE));
          }
        }
        // Eignungsprüfung document has not been created.
        else if (result.equals(NO_EXAMINATION_DOCUMENT)) {
          errors.add(new ValidationError("noExaminationDocumentField",
            ValidationMessages.NO_EXAMINATION_DOCUMENT));
        }
        // Proof provided values of companies have been changed.
        else if (result.equals(PROOF_INCONSISTENCIES)) {
          errors.add(new ValidationError("proofInconsistenciesField",
            ValidationMessages.PROOF_INCONSISTENCIES_MESSAGE));
        }
      }
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    return Response.ok().build();
  }

  /**
   * This function implements the reopening of the offer.
   *
   * @param reopenForm the reopen form
   * @param id the id
   * @return Ok
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/reopen/offer/{id}/{version}")
  public Response reopenOffer(@Valid ReopenForm reopenForm, @PathParam("id") String id,
    @PathParam("version") Long version) {
    submissionService.checkOptimisticLockSubmission(id, version);
    submissionService.reopenOffer(reopenForm.getReopenReason(), id);
    return Response.ok().build();
  }


  /**
   * This function implements the reopening of the formal audit.
   *
   * @param reopenForm the reopen form
   * @param id the id
   * @return Ok
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/reopen/formal/{id}/{version}")
  public Response reopenFormalAudit(@Valid ReopenForm reopenForm, @PathParam("id") String id,
    @PathParam("version") Long version) {
    submissionService.checkOptimisticLockSubmission(id, version);
    submissionService.reopenFormalAudit(reopenForm.getReopenReason(), id);
    return Response.ok().build();
  }

  /**
   * This function implements the reopening of the examination.
   *
   * @param reopenForm the reopen form
   * @param id the id
   * @return Ok
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/reopen/examination/{id}/{version}")
  public Response reopenExamination(@Valid ReopenForm reopenForm, @PathParam("id") String id,
    @PathParam("version") Long version) {
    submissionService.checkOptimisticLockSubmission(id, version);
    submissionService.reopenExamination(reopenForm.getReopenReason(), id);
    return Response.ok().build();
  }

  /**
   * Reopen manual award.
   *
   * @param reopenForm the reopen form
   * @param id the submission id
   * @param version the submission version
   * @return the response
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/reopen/manualAward/{id}/{version}")
  public Response reopenManualAward(@Valid ReopenForm reopenForm,
    @PathParam("id") String id, @PathParam("version") Long version) {
    submissionService.checkOptimisticLockSubmission(id, version);
    submissionService.reopenManualAward(id, reopenForm.getReopenReason());
    return Response.ok().build();
  }

  /**
   * Validation.
   *
   * @param submissionHasSubmittent the submission has submittent
   * @param submission the submission
   * @return the sets the
   */
  private Set<ValidationError> validation(Boolean submissionHasSubmittent,
    SubmissionForm submission) {
    Set<ValidationError> errors = new HashSet<>();
    if (submissionHasSubmittent != null && submissionHasSubmittent) {
      errors.add(new ValidationError("submissionHasSubmittentField",
        ValidationMessages.SUBMITTENT_EXISTS_ERROR_MESSAGE));
    }
    if (submission != null) {
      if (submission.getWorkType() == null || submission.getProcess() == null
        || submission.getCostEstimate() == null || submission.getProcessType() == null
        || submission.getGattTwo() == null
        || StringUtils.isBlank(submission.getPmDepartmentName())
        || ((submission.getReasonFreeAward() == null
        || StringUtils.isBlank(submission.getReasonFreeAward().getValue2()))
        && submission.getAboveThreshold() != null && submission.getAboveThreshold())) {
        errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
          ValidationMessages.MANDATORY_ERROR_MESSAGE));
        if (submission.getWorkType() == null) {
          errors.add(new ValidationError("workType",
            ValidationMessages.MANDATORY_ERROR_MESSAGE));
        }
        if (submission.getProcess() == null) {
          errors.add(new ValidationError("process",
            ValidationMessages.MANDATORY_ERROR_MESSAGE));
        }
        if (submission.getCostEstimate() == null) {
          errors
            .add(new ValidationError("costEstimate",
              ValidationMessages.MANDATORY_ERROR_MESSAGE));
        }
        if (submission.getProcessType() == null) {
          errors
            .add(new ValidationError("processType",
              ValidationMessages.MANDATORY_ERROR_MESSAGE));
        }
        if (submission.getGattTwo() == null) {
          errors.add(new ValidationError("gattWto",
            ValidationMessages.MANDATORY_ERROR_MESSAGE));
        }
        if (StringUtils.isBlank(submission.getPmDepartmentName())) {
          errors.add(
            new ValidationError(PM_DEPARTMENT_NAME,
              ValidationMessages.MANDATORY_ERROR_MESSAGE));
        }
        if ((submission.getReasonFreeAward() == null
          || StringUtils.isBlank(submission.getReasonFreeAward().getValue2()))
          && submission.getAboveThreshold() != null && submission.getAboveThreshold()) {
          errors.add(
            new ValidationError("reasonFreeAward",
              ValidationMessages.MANDATORY_ERROR_MESSAGE));
        }
      }
      if (submission.getPmDepartmentName() != null
        && submission.getPmDepartmentName().length() >= 100) {
        errors.add(new ValidationError("pmDepartmentNameErrorField",
          ValidationMessages.PROJECT_MAX_SIZE_PM_DEPARTMENTNAME_MESSAGE));
        errors.add(new ValidationError(PM_DEPARTMENT_NAME,
          ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if (StringUtils.isBlank(submission.getPmDepartmentName())
        || submission.getPmDepartmentName().length() < 3) {
        errors.add(new ValidationError("pmDepartmentNameErrorField",
          ValidationMessages.PROJECT_MIN_SIZE_PM_DEPARTMENTNAME_MESSAGE));
        errors.add(new ValidationError(PM_DEPARTMENT_NAME,
          ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if (submission.getDescription() != null && submission.getDescription().length() > 100) {
        errors.add(new ValidationError("descriptionErrorField",
          ValidationMessages.DESCRIPTION_MAX_SIZE_MESSAGE));
        errors.add(new ValidationError("description",
          ValidationMessages.DESCRIPTION_MAX_SIZE_MESSAGE));
      }
      if (submission.getProcess() != null
        && (submission.getProcess().equals(Process.SELECTIVE)
        || submission.getProcess().equals(Process.OPEN))
        && submission.getPublicationDate() == null) {
        errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
          ValidationMessages.MANDATORY_ERROR_MESSAGE));
        errors.add(new ValidationError("publicationDate",
          ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if (submission.getNotes() != null && submission.getNotes().length() > 100) {
        errors.add(new ValidationError("notesErrorField",
          ValidationMessages.SUBMISSION_MAX_SIZE_NOTES_MESSAGE));
        errors.add(new ValidationError(NOTES,
          ValidationMessages.SUBMISSION_MAX_SIZE_NOTES_MESSAGE));
      }
      if (submission.getCostEstimate() != null
        && submission.getCostEstimate().doubleValue() > 9999999999999.99) {
        errors.add(new ValidationError("costEstimate",
          ValidationMessages.COST_ESTIMATE_MAX_ERROR_MESSAGE));
        errors.add(new ValidationError("costEstimateErrorField",
          ValidationMessages.COST_ESTIMATE_MAX_ERROR_MESSAGE));
      }
      if (submission.getProcess() != null && (submission.getProcess().equals(Process.INVITATION)
        || submission.getProcess().equals(Process.SELECTIVE)
        || submission.getProcess().equals(Process.OPEN))
        && (submission.getProcedure() == null || submission.getProcedure().getValue1() == null)) {
        errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
          ValidationMessages.MANDATORY_ERROR_MESSAGE));
        errors.add(new ValidationError("procedure",
          ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
    }
    return errors;
  }

  /**
   * Check reopen reason text for invalid values.
   *
   * @param reopen the reopen
   * @param statusToReopen the status to reopen
   * @return the response
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/reopenStatus/{statusToReopen}")
  public Response checkReasonGiven(@Valid ReopenForm reopen,
    @PathParam("statusToReopen") int statusToReopen) {
    if (reopen.getReopenReason() == null || reopen.getReopenReason().length() == 0) {
      Set<ValidationError> errors = new HashSet<>();
      /* Display different error message for every reopening case */
      if (statusToReopen == StatusToReopen.APPLICATION_OPENING.getValue()) {
        errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
          ValidationMessages.APPLICATION_OPENING_REOPEN_ERROR_MESSAGE));
        errors.add(new ValidationError(REASON_GIVEN,
          ValidationMessages.APPLICATION_OPENING_REOPEN_ERROR_MESSAGE));
      } else if (statusToReopen == StatusToReopen.OFFER_OPENING.getValue()) {
        errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
          ValidationMessages.OFFER_OPENING_REOPEN_ERROR_MESSAGE));
        errors.add(new ValidationError(REASON_GIVEN,
          ValidationMessages.OFFER_OPENING_REOPEN_ERROR_MESSAGE));
      } else if (statusToReopen == StatusToReopen.SUITABILITY_AUDIT.getValue()) {
        errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
          ValidationMessages.SUITABILITY_AUDIT_REOPEN_ERROR_MESSAGE));
        errors.add(new ValidationError(REASON_GIVEN,
          ValidationMessages.SUITABILITY_AUDIT_REOPEN_ERROR_MESSAGE));
      } else if (statusToReopen == StatusToReopen.FORMAL_AUDIT.getValue()) {
        errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
          ValidationMessages.FORMAL_AUDIT_REOPEN_ERROR_MESSAGE));
        errors.add(new ValidationError(REASON_GIVEN,
          ValidationMessages.FORMAL_AUDIT_REOPEN_ERROR_MESSAGE));
      } else if (statusToReopen == StatusToReopen.AWARD_EVALUATION.getValue()) {
        errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
          ValidationMessages.AWARD_EVALUATION_REOPEN_ERROR_MESSAGE));
        errors.add(new ValidationError(REASON_GIVEN,
          ValidationMessages.AWARD_EVALUATION_REOPEN_ERROR_MESSAGE));
      } else if (statusToReopen == StatusToReopen.COMMISSION_PROCUREMENT_PROPOSAL.getValue()) {
        errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
          ValidationMessages.COMMISSION_PROCUREMENT_PROPOSAL_REOPEN_ERROR_MESSAGE));
        errors.add(new ValidationError(REASON_GIVEN,
          ValidationMessages.COMMISSION_PROCUREMENT_PROPOSAL_REOPEN_ERROR_MESSAGE));
      } else if (statusToReopen == StatusToReopen.COMMISSION_PROCUREMENT_DECISION.getValue()) {
        errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
          ValidationMessages.COMMISSION_PROCUREMENT_DECISION_REOPEN_ERROR_MESSAGE));
        errors.add(new ValidationError(REASON_GIVEN,
          ValidationMessages.COMMISSION_PROCUREMENT_DECISION_REOPEN_ERROR_MESSAGE));
      } else if (statusToReopen == StatusToReopen.PROCESS.getValue()) {
        errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
          ValidationMessages.PROCESS_REOPEN_ERROR_MESSAGE));
        errors.add(new ValidationError(REASON_GIVEN,
          ValidationMessages.PROCESS_REOPEN_ERROR_MESSAGE));
      } else if (statusToReopen == StatusToReopen.MANUAL_AWARD.getValue()) {
        errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
          ValidationMessages.MANUAL_AWARD_REOPEN_ERROR_MESSAGE));
        errors.add(new ValidationError(REASON_GIVEN,
          ValidationMessages.MANUAL_AWARD_REOPEN_ERROR_MESSAGE));
      } else {
        errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
          ValidationMessages.MANDATORY_ERROR_MESSAGE));
        errors.add(new ValidationError(REASON_GIVEN,
          ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    } else if (reopen.getReopenReason().length() > 100) {
      Set<ValidationError> errors = new HashSet<>();
      errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
        ValidationMessages.REASON_GIVEN_MAX_ERROR_MESSAGE));
      errors.add(new ValidationError(REASON_GIVEN,
        ValidationMessages.REASON_GIVEN_MAX_ERROR_MESSAGE));
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    return Response.ok().build();
  }

  /**
   * This function implements the reopening of the award evaluation.
   *
   * @param reopenForm the reopen form
   * @param submissionId the submission id
   * @return the response
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/reopen/awardEvaluation/{submissionId}/{submissionVersion}")
  public Response reopenAwardEvaluation(@Valid ReopenForm reopenForm,
    @PathParam("submissionId") String submissionId,
    @PathParam("submissionVersion") Long submissionVersion) {
    submissionService.checkOptimisticLockSubmission(submissionId, submissionVersion);
    submissionService.reopenAwardEvaluation(reopenForm.getReopenReason(), submissionId);
    return Response.ok().build();
  }

  /**
   * Check if status offer opening closed has been set before.
   *
   * @param submissionId the submission id
   * @return true/false
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/hasOfferOpeningBeenClosedBefore/{submissionId}")
  public Response hasOfferOpeningBeenClosedBefore(@PathParam("submissionId") String submissionId) {
    return Response.ok(submissionService.hasOfferOpeningBeenClosedBefore(submissionId)).build();
  }


  /**
   * Gets the templates by submission.
   *
   * @param submissionId the submission id
   * @return the templates by submission
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/getTemplates/{submissionId}")
  public Response getTemplatesBySubmission(@PathParam("submissionId") String submissionId) {

    SubmissionDTO submissionDTO = submissionService.getSubmissionById(submissionId);
    submissionDTO.setCurrentState(
      TenderStatus.fromValue(submissionService.getCurrentStatusOfSubmission(submissionId)));

    return Response.ok(ruleService.getProjectAllowedTemplates(submissionDTO)).build();
  }

  /**
   * This method is used to determine if a Nachweisbrief document should be generated (Freihändig
   * and Freihändig mit Konkurrenz).
   *
   * @param submittentForms the submittent forms
   * @return the response
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/generate/proofs")
  public Response generateProofDocNegotiatedProcedure(@Valid List<SubmittentForm> submittentForms) {
    // If at least one Submittent is selected to generate Nachweisbrief then set boolean to true.
    boolean generateDoc = false;
    for (SubmittentForm s : submittentForms) {
      if (s.getProofDocPending()) {
        generateDoc = true;
        break;
      }
    }
    // If Submittent is not selected then show a message.
    if (!generateDoc) {
      Set<ValidationError> errors = new HashSet<>();
      errors.add(new ValidationError("noSubmittentsChecked",
        ValidationMessages.EMPTY_SUBMITTENT_LIST_ERROR_MESSAGE));
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    submissionService.generateProofDocNegotiatedProcedure(
      SubmittentFormMapper.INSTANCE.toSubmittentDTO(submittentForms));
    return Response.ok().build();
  }

  /**
   * Manual award reopen check.
   *
   * @param submissionId the submission id
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/manualAwardReopenCheck/{submissionId}")
  public Response manualAwardReopenCheck(@PathParam("submissionId") String submissionId) {
    boolean reopenRights = submissionService.manualAwardReopenCheck(submissionId);
    if (!reopenRights) {
      Set<ValidationError> errors = new HashSet<>();
      errors.add(new ValidationError("manualAwardReopenPLErrorField",
        ValidationMessages.MANUAL_AWARD_REOPEN_PL_ERROR_MESSAGE));
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    return Response.ok().build();
  }

  /**
   * Checks if at least one amount of one offer of the submission is above threshold.
   *
   * @param submissionId the id of the submission to be checked
   * @return a boolean indicating if at least one amount of one offer of the submission is above
   * threshold
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/isOfferAboveThreshold/{submissionId}")
  public Response isOfferAboveThreshold(@PathParam("submissionId") String submissionId) {
    Boolean isOfferAboveThreshold = submissionCloseService
      .isOfferAboveThreshold(submissionId);
    return Response.ok(isOfferAboveThreshold).build();
  }

  /**
   * Updates the legal hearing terminate.
   *
   * @param legalHearingTerminate the legal hearing terminate
   * @param submissionId the submission id
   * @return the response
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/legal/hearing/terminate/{submissionId}")
  public Response updateLegalHearingTerminate(
    @Valid LegalHearingTerminateForm legalHearingTerminate,
    @PathParam("submissionId") String submissionId) {
    Set<ValidationError> errors = validateLegalHearingTerminate(legalHearingTerminate);
    if (!errors.isEmpty()) {
      return Response.status(Status.BAD_REQUEST).entity(errors).build();
    }
    Set<ValidationError> optimisticLockErrors = legalHearingService.updateLegalHearingTermination(
      LegalHearingTerminateFormMapper.INSTANCE.toLegalHearingTerminateDTO(legalHearingTerminate), submissionId);

    return (optimisticLockErrors.isEmpty())
      ? Response.ok().build()
      : Response.status(Status.CONFLICT).entity(optimisticLockErrors).build();
  }

  /**
   * Creates a legal hearing terminate entry the first time .
   *
   * @param legalHearingTerminate the legal hearing terminate
   * @param submissionId the submission id
   * @return the response
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/legal/hearing/terminate/{submissionId}")
  public Response createLegalHearingTerminate(
    @Valid LegalHearingTerminateForm legalHearingTerminate,
    @PathParam("submissionId") String submissionId) {
    Set<ValidationError> errors = validateLegalHearingTerminate(legalHearingTerminate);
    if (!errors.isEmpty()) {
      return Response.status(Status.BAD_REQUEST).entity(errors).build();
    }
    Set<ValidationError> optimisticLockErrors = legalHearingService.createLegalHearingTermination(
      LegalHearingTerminateFormMapper.INSTANCE.toLegalHearingTerminateDTO(legalHearingTerminate), submissionId);

    return (optimisticLockErrors.isEmpty())
      ? Response.ok().build()
      : Response.status(Status.CONFLICT).entity(optimisticLockErrors).build();
  }

  /**
   * Gets the submission legal hearing termination.
   *
   * @param submissionId the submission id
   * @return the submission legal hearing termination
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/legal/hearing/terminate/{submissionId}")
  public Response getSubmissionLegalHearingTermination(
    @PathParam("submissionId") String submissionId) {
    LegalHearingTerminateDTO l = legalHearingService
      .getSubmissionLegalHearingTermination(submissionId);
    return (l != null)
      ? Response.ok(l).build()
      : Response.status(Status.NO_CONTENT).build();
  }

  /**
   * Gets all data of the current status of submission, so also the date the status is set.
   *
   * @param submissionId the submission id
   * @return the tender status history dto
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/status/data/{submissionId}")
  @JsonView(View.Public.class)
  public Response getCurrentStatusDataOfSubmission(@PathParam("submissionId") String submissionId) {
    TenderStatusHistoryDTO currentStatusOfSubmission =
      submissionService.getCurrentStatusDataOfSubmission(submissionId);
    return Response.ok(currentStatusOfSubmission).build();
  }

  /**
   * Returns the date status AWARD_NOTICES_CREATED is set, if it is one of the two last statuses of
   * the submission, null otherwise.
   *
   * @param submissionId the submission id
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/awardNoticesCreatedDate/{submissionId}")
  public Response getAwardNoticesCreatedDateForClose(
    @PathParam("submissionId") String submissionId) {
    Timestamp getSubmissionStatusDate = submissionCloseService
      .getAwardNoticesCreatedDateForClose(submissionId);
    return Response.ok(getSubmissionStatusDate).build();
  }

  /**
   * Closes the given submission.
   *
   * @param submissionId the submission UUID
   * @return empty response
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/close/{submissionId}/version/{version}")
  public Response closeSubmission(@PathParam("submissionId") String submissionId, @PathParam("version") Long version) {
    Set<ValidationError> optimisticLockErrors = submissionService.updateSubmissionStatus(submissionId, version,
      TenderStatus.PROCEDURE_COMPLETED.getValue(), AuditMessages.CLOSE_SUBMISSION.name(),
      null, LookupValues.EXTERNAL_LOG);
    return (optimisticLockErrors.isEmpty())
      ? Response.ok().build()
      : Response.status(Response.Status.BAD_REQUEST).entity(optimisticLockErrors).build();
  }

  /**
   * Reopens the given submission.
   *
   * @param submissionId the submission UUID
   * @param reopenForm the reopen form
   * @return empty response
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/reopenSubmission/{submissionId}")
  public Response reopenSubmission(@PathParam("submissionId") String submissionId,
    @Valid ReopenForm reopenForm) {
    submissionCloseService.reopenSubmission(submissionId, reopenForm.getReopenReason());
    return Response.ok().build();
  }

  /**
   * Returns the date a submission has been reopened and the status before the reopen (closed or
   * cancelled). Returns null if the submission has never been reopened or has been reopened and
   * closed again.
   *
   * @param submissionId the submission UUID
   * @return the response
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/status/getDateAndPreviousReopen/{submissionId}")
  public Response getDateAndPreviousReopenStatus(@PathParam("submissionId") String submissionId) {
    TenderStatusHistoryDTO getDateAndPreviousReopenStatus = submissionService
      .getDateAndPreviousReopenStatus(submissionId);
    return Response.ok(getDateAndPreviousReopenStatus).build();
  }

  /**
   * Gets the excluded submittents.
   *
   * @param submissionId the submission id
   * @return the excluded submittents
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/legal/exclude/{submissionId}")
  public Response getExcludedSubmittents(
    @PathParam("submissionId") String submissionId) {
    return Response.ok(legalHearingService.getExcludedSubmittents(submissionId)).build();
  }

  /**
   * Adds the excluded submittent.
   *
   * @param submittentId the submittent id
   * @return the response
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/legal/exclude/submittent/{submittentId}")
  public Response addExcludedSubmittent(
    @PathParam("submittentId") String submittentId) {
    legalHearingService.addExcludedSubmittent(submittentId);
    return Response.ok().build();
  }


  /**
   * Adds the excluded applicant.
   *
   * @param submittentId the submittent id
   * @return the response
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/legal/exclude/applicant/{submittentId}")
  public Response addExcludedApplicant(
    @PathParam("submittentId") String submittentId) {
    legalHearingService.addExcludedApplicant(submittentId);
    return Response.ok().build();
  }

  /**
   * Update excluded submittent.
   *
   * @param legalHearingExclusionForm the legal hearing exclusion form
   * @return the response
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/legal/exclude/update")
  public Response updateExcludedSubmittent(
    @Valid LegalHearingExclusionForm legalHearingExclusionForm) {
    Set<ValidationError> errors = validateLegalHearingExclusion(legalHearingExclusionForm);
    if (!errors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    Set<ValidationError> optimisticLockErrors = new HashSet<>();
    try {
      optimisticLockErrors = legalHearingService.updateExcludedSubmittent(
        LegalHearingExclusionFormMapper.INSTANCE
          .toLegalHearingExclusionDTO(legalHearingExclusionForm.getLegalExclusions()),
        legalHearingExclusionForm.getExclusionDate(), legalHearingExclusionForm.getSubmissionId(),
        legalHearingExclusionForm.getSubmissionVersion(), legalHearingExclusionForm.getFirstLevelExclusionDate());
    } catch (OptimisticLockException | RollbackException e) {
      optimisticLockErrors
        .add(new ValidationError(ValidationMessages.OPTIMISTIC_LOCK_ERROR_FIELD, ValidationMessages.OPTIMISTIC_LOCK));
    }
    return (optimisticLockErrors.isEmpty())
      ? Response.ok().build()
      : Response.status(Response.Status.BAD_REQUEST).entity(optimisticLockErrors).build();
  }

  /**
   * Close application opening.
   *
   * @param submissionId the submission id
   * @param submissionVersion the submissionVersion
   * @return the response
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/closeApplicationOpening/{submissionId}/{submissionVersion}")
  public Response closeApplicationOpening(@PathParam("submissionId") String submissionId,
    @PathParam("submissionVersion") Long submissionVersion) {
    Set<ValidationError> errors = closeApplicationOpeningValidation(submissionId);
    if (!errors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    submissionService.checkOptimisticLockSubmission(submissionId, submissionVersion);
    submissionService.closeApplicationOpening(submissionId);
    return Response.ok().build();
  }

  /**
   * Validates closeApplicationOpening.
   *
   * @param submissionId the submissionId
   * @return the errors
   */
  private Set<ValidationError> closeApplicationOpeningValidation(String submissionId) {
    Set<ValidationError> validationErrors = new HashSet<>();
    if (!submissionService.applicationDatesFilled(submissionId)) {
      validationErrors.add(new ValidationError(ValidationMessages.MISSING_APPLICATION_DATES_ERROR_FIELD,
        ValidationMessages.MISSING_APPLICATION_DATES));
    }
    if (!submissionService.applicantOverviewDocumentExists(submissionId)) {
      validationErrors.add(new ValidationError(ValidationMessages.NO_APPLICANT_DOCUMENT_ERROR_FIELD,
        ValidationMessages.NO_APPLICANT_OVERVIEW_DOCUMENT));
    }
    return validationErrors;
  }

  /**
   * Checks if the application opening has been closed before.
   *
   * @param submissionId the submission id
   * @return true/false
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/hasApplicationOpeningBeenClosedBefore/{submissionId}")
  public Response hasApplicationOpeningBeenClosedBefore(
    @PathParam("submissionId") String submissionId) {
    return Response.ok(submissionService.hasApplicationOpeningBeenClosedBefore(submissionId))
      .build();
  }

  /**
   * Function to reopen the application opening.
   *
   * @param reopenForm the reopen form
   * @param submissionId the submission id
   * @return response
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/reopenApplicationOpening/{submissionId}/{submissionVersion}")
  public Response reopenApplicationOpening(@Valid ReopenForm reopenForm,
    @PathParam("submissionId") String submissionId,
    @PathParam("submissionVersion") Long submissionVersion) {
    submissionService.checkOptimisticLockSubmission(submissionId, submissionVersion);
    submissionService.reopenApplicationOpening(reopenForm.getReopenReason(), submissionId);
    return Response.ok().build();
  }

  /**
   * Gets the award info.
   *
   * @param submissionId the submission id
   * @return the award info
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/award/info/{submissionId}")
  public Response getAwardInfo(
    @PathParam("submissionId") String submissionId) {
    return Response.ok(submissionCloseService.getAwardInfo(submissionId)).build();
  }

  /**
   * Creates the award info entry.
   *
   * @param awardInfo the awardInfo entity to be created
   * @return empty response or the error
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/saveAwardInfo")
  public Response createAwardInfo(@Valid AwardInfoForm awardInfo) {
    Set<ValidationError> errors = validateAwardInfo(awardInfo);
    if (!errors.isEmpty()) {
      return Response.status(Status.BAD_REQUEST).entity(errors).build();
    }
    Set<ValidationError> optimisticLockErrors = submissionCloseService
      .createAwardInfo(AwardInfoFormMapper.INSTANCE.toAwardInfoDTO(awardInfo));
    return (optimisticLockErrors.isEmpty())
      ? Response.ok().build()
      : Response.status(Status.CONFLICT).entity(optimisticLockErrors).build();
  }

  /**
   * Updates the award info entry.
   *
   * @param awardInfo the awardInfo entity to be updated
   * @return empty response or the error
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/saveAwardInfo")
  public Response updateAwardInfo(@Valid AwardInfoForm awardInfo) {
    Set<ValidationError> errors = validateAwardInfo(awardInfo);
    if (!errors.isEmpty()) {
      return Response.status(Status.BAD_REQUEST).entity(errors).build();
    }
    Set<ValidationError> optimisticLockErrors = submissionCloseService
      .updateAwardInfo(AwardInfoFormMapper.INSTANCE.toAwardInfoDTO(awardInfo));

    return (optimisticLockErrors.isEmpty())
      ? Response.ok().build()
      : Response.status(Status.CONFLICT).entity(optimisticLockErrors).build();
  }

  /**
   * Validate award info.
   *
   * @param awardInfo the award info
   * @return the sets the
   */
  private Set<ValidationError> validateAwardInfo(AwardInfoForm awardInfo) {
    Set<ValidationError> errors = new HashSet<>();
    for (AwardInfoOfferForm offer : awardInfo.getOffers()) {
      if (offer.getExclusionReason() != null
        && offer.getExclusionReason().length() > LookupValues.MAX_REASON_TEXT_LENGTH) {
        errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
          ValidationMessages.CPP_REASON_GIVEN_ERROR_MESSAGE));
        break;
      }
    }
    return errors;
  }

  /**
   * Gets the award info first level.
   *
   * @param submissionId the submission id
   * @return the award info first level
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/awardInfoFirstLevel/{submissionId}")
  public Response getAwardInfoFirstLevel(
    @PathParam("submissionId") String submissionId) {
    return Response.ok(submissionCloseService.getAwardInfoFirstLevel(submissionId)).build();
  }

  /**
   * Updates the award infos first level.
   *
   * @param awardInfo the awardInfoFirstLevel entity to be created
   * @return empty response
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/saveAwardInfoFirstLevel")
  public Response createAwardInfoFirstLevel(@Valid AwardInfoFirstLevelForm awardInfo) {
    Set<ValidationError> errors = validateAwardInfoFirstLevel(awardInfo);
    if (!errors.isEmpty()) {
      return Response.status(Status.BAD_REQUEST).entity(errors).build();
    }
    Set<ValidationError> optimisticLockErrors = submissionCloseService.createAwardInfoFirstLevel(
      AwardInfoFirstLevelFormMapper.INSTANCE.toAwardInfoFirstLevelDTO(awardInfo));
    return (optimisticLockErrors.isEmpty())
      ? Response.ok().build()
      : Response.status(Status.CONFLICT).entity(optimisticLockErrors).build();
  }

  /**
   * Updates the award infos first level.
   *
   * @param awardInfo the awardInfoFirstLevel entity to be created
   * @return empty response
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/saveAwardInfoFirstLevel")
  public Response updateAwardInfoFirstLevel(@Valid AwardInfoFirstLevelForm awardInfo) {
    Set<ValidationError> errors = validateAwardInfoFirstLevel(awardInfo);
    if (!errors.isEmpty()) {
      return Response.status(Status.BAD_REQUEST).entity(errors).build();
    }
    Set<ValidationError> optimisticLockErrors = submissionCloseService.updateAwardInfoFirstLevel(
      AwardInfoFirstLevelFormMapper.INSTANCE.toAwardInfoFirstLevelDTO(awardInfo));
    return (optimisticLockErrors.isEmpty())
      ? Response.ok().build()
      : Response.status(Status.CONFLICT).entity(optimisticLockErrors).build();
  }

  /**
   * Validate award info first level.
   *
   * @param awardInfo the award info
   * @return the sets the
   */
  private Set<ValidationError> validateAwardInfoFirstLevel(AwardInfoFirstLevelForm awardInfo) {
    Set<ValidationError> errors = new HashSet<>();
    if (awardInfo.getReason() != null
      && awardInfo.getReason().length() > LookupValues.MAX_REASON_TEXT_LENGTH) {
      errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
        ValidationMessages.REASON_APPROVAL_MAX_ERROR_MESSAGE));
      errors.add(new ValidationError(ValidationMessages.REASON,
        ValidationMessages.REASON_APPROVAL_MAX_ERROR_MESSAGE));
    }
    for (AwardInfoOfferFirstLevelForm offer : awardInfo.getOffers()) {
      if (offer.getExclusionReasonFirstLevel() != null
        && offer.getExclusionReasonFirstLevel().length() > LookupValues.MAX_REASON_TEXT_LENGTH) {
        errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
          ValidationMessages.REASON_EXLUSION_MAX_ERROR_MESSAGE));
        break;
      }
    }
    return errors;
  }

  /**
   * Gets the templates by department.
   *
   * @param submissionId the submission id
   * @return the templates by department
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/getTemplates/department/{submissionId}")
  public Response getTemplatesByDepartment(@PathParam("submissionId") String submissionId) {

    SubmissionDTO submissionDTO = submissionService.getSubmissionById(submissionId);
    submissionDTO.setCurrentState(
      TenderStatus.fromValue(submissionService.getCurrentStatusOfSubmission(submissionId)));

    return Response.ok(ruleService.getProjectDepartmentTemplates(submissionDTO)).build();
  }

  /**
   * Check if status suitability audit completed (selective process) has been set before.
   *
   * @param submissionId the submission id
   * @return true/false
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/hasSuitabilityAuditCompletedSBeenSetBefore/{submissionId}")
  public Response hasSuitabilityAuditCompletedSBeenSetBefore(
    @PathParam("submissionId") String submissionId) {
    return Response.ok(submissionService.hasSuitabilityAuditCompletedSBeenSetBefore(submissionId))
      .build();
  }

  /**
   * Validate legal hearing terminate.
   *
   * @param legalHearingTerminate the legal hearing terminate
   * @return the sets the
   */
  private Set<ValidationError> validateLegalHearingTerminate(
    LegalHearingTerminateForm legalHearingTerminate) {
    Set<ValidationError> errors = new HashSet<>();
    if (legalHearingTerminate.getDeadline() == null) {
      // Check if no date value or invalid date value has been given.
      if (StringUtils.isBlank(legalHearingTerminate.getDeadlineViewValue())) {
        errors.add(
          new ValidationError("hearingDeadline", ValidationMessages.MANDATORY_ERROR_MESSAGE));
        errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
          ValidationMessages.MANDATORY_ERROR_MESSAGE));
      } else {
        errors.add(new ValidationError("hearingDeadline", ValidationMessages.INVALID_DATE));
        errors.add(new ValidationError("deadlineErrorField", ValidationMessages.INVALID_DATE));
      }
    }
    if (legalHearingTerminate.getReason() == null
      || StringUtils.isBlank(legalHearingTerminate.getReason())) {
      errors.add(new ValidationError("reason", ValidationMessages.MANDATORY_ERROR_MESSAGE));
      errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
        ValidationMessages.MANDATORY_ERROR_MESSAGE));
    }
    if (legalHearingTerminate.getTerminationReason().isEmpty()) {
      errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
        ValidationMessages.MANDATORY_ERROR_MESSAGE));
    }
    if (legalHearingTerminate.getReason() != null
      && legalHearingTerminate.getReason().length() > LookupValues.MAX_REASON_TEXT_LENGTH) {
      errors
        .add(new ValidationError("reasonError", ValidationMessages.CPP_REASON_GIVEN_ERROR_MESSAGE));
      errors.add(new ValidationError("reason", ValidationMessages.CPP_REASON_GIVEN_ERROR_MESSAGE));
    }
    return errors;
  }

  /**
   * Validate legal hearing exclusion.
   *
   * @param legalHearingExclusionForm the legal hearing exclusion form
   * @return the sets the
   */
  private Set<ValidationError> validateLegalHearingExclusion(
    LegalHearingExclusionForm legalHearingExclusionForm) {
    Set<ValidationError> errors = new HashSet<>();
    if (!legalHearingExclusionForm.getLegalExclusions().isEmpty()) {
      validateLegalExclusionDeadline(legalHearingExclusionForm, errors);
    }
    return errors;
  }

  /**
   * Validate legal exclusion deadline.
   *
   * @param legalHearingExclusionForm the legal hearing exclusion form
   * @param errors the errors
   */
  private void validateLegalExclusionDeadline(LegalHearingExclusionForm legalHearingExclusionForm,
    Set<ValidationError> errors) {
    String status = submissionService
      .getCurrentStatusOfSubmission(legalHearingExclusionForm.getSubmissionId());
    for (LegalExclusionForm exclusion : legalHearingExclusionForm.getLegalExclusions()) {
      if (Integer.valueOf(status)
        .compareTo(Integer.valueOf(TenderStatus.AWARD_NOTICES_1_LEVEL_CREATED.getValue())) >= 0
        && (exclusion.getLevel().equals(SelectiveLevel.SECOND_LEVEL.getValue())
        || exclusion.getLevel().equals(SelectiveLevel.ZERO_LEVEL.getValue()))
        && legalHearingExclusionForm.getExclusionDate() == null) {
        // Check if no date value or invalid date value has been given.
        if (StringUtils.isBlank(legalHearingExclusionForm.getexclusionDateViewValue())) {
          errors.add(
            new ValidationError("exclusionDate", ValidationMessages.MANDATORY_ERROR_MESSAGE));
          errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
            ValidationMessages.MANDATORY_ERROR_MESSAGE));
        } else {
          errors.add(new ValidationError("exclusionDate", ValidationMessages.INVALID_DATE));
          errors.add(new ValidationError("dateErrorField", ValidationMessages.INVALID_DATE));
        }
        break;
      }
      if (Integer.valueOf(status)
        .compareTo(Integer.valueOf(TenderStatus.AWARD_NOTICES_1_LEVEL_CREATED.getValue())) < 0
        && exclusion.getLevel().equals(SelectiveLevel.FIRST_LEVEL.getValue())
        && legalHearingExclusionForm.getFirstLevelExclusionDate() == null) {
        // Check if no date value or invalid date value has been given.
        if (StringUtils.isBlank(legalHearingExclusionForm.getfLExclusionDateViewValue())) {
          errors.add(new ValidationError("firstLevelExclusionDate",
            ValidationMessages.MANDATORY_ERROR_MESSAGE));
          errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
            ValidationMessages.MANDATORY_ERROR_MESSAGE));
        } else {
          errors
            .add(new ValidationError("firstLevelExclusionDate", ValidationMessages.INVALID_DATE));
          errors.add(new ValidationError("dateErrorField", ValidationMessages.INVALID_DATE));
        }
        break;
      }
      if (exclusion.getExclusionReason() != null
        && exclusion.getExclusionReason().length() > LookupValues.MAX_REASON_TEXT_LENGTH) {
        errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
          ValidationMessages.CPP_REASON_GIVEN_ERROR_MESSAGE));
        break;
      }
    }
  }

  /**
   * Load signatures.
   *
   * @param submissionId the submission id
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/signatures/{submissionId}")
  public Response loadSignatures(
    @PathParam("submissionId") String submissionId) {
    return Response.ok(submissionService.loadSubmissionSignatures(submissionId))
      .build();
  }

  /**
   * This method returns a list of applicants of a specific submission.
   *
   * @param submissionId the submission id
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/applicants/{submissionId}")
  public Response getApplicantsBySubmission(@PathParam("submissionId") String submissionId) {
    List<SubmittentOfferDTO> submittentOfferDTOs = submissionService
      .getApplicantsBySubmission(submissionId);
    return Response.ok(submittentOfferDTOs).build();
  }

  /**
   * This method returns the applicants by submission for the formal audit.
   *
   * @param submissionId the submission id
   * @return the applicants by submission
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/getApplicantsForFormalAudit/{submissionId}")
  public Response getApplicantsForFormalAudit(@PathParam("submissionId") String submissionId) {
    List<SubmittentDTO> submittentDTOs =
      submissionService.getApplicantsForFormalAudit(submissionId);
    return Response.ok(submittentDTOs).build();
  }

  /**
   * This method updates the submittents of a selective process in formal audit 1st level.
   *
   * @param submittentForms the submittent forms.
   * @return the response
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/updateSelectiveFormalAudit")
  public Response updateSelectiveFormalAudit(@Valid List<SubmittentForm> submittentForms) {
    Set<ValidationError> optimisticLockErrors = submissionService
      .updateSelectiveFormalAudit(SubmittentFormMapper.INSTANCE.toSubmittentDTO(submittentForms));
    return (optimisticLockErrors.isEmpty())
      ? Response.ok().build()
      : Response.status(Response.Status.BAD_REQUEST).entity(optimisticLockErrors).build();
  }

  /**
   * Gets the exclusion deadline.
   *
   * @param submissionId the submission id
   * @return the exclusion deadline
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/legal/deadline/{submissionId}")
  public Response getExclusionDeadline(
    @PathParam("submissionId") String submissionId) {
    return Response.ok(legalHearingService.getExclusionDeadline(submissionId)).build();
  }

  /**
   * Function to handle navigation to the submission canceling tab.
   *
   * @param submissionId the submission id.
   * @return the response.
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/submissionCancelNavigation/{submissionId}")
  public Response submissionCancelNavigation(@PathParam("submissionId") String submissionId) {
    boolean navigationPossible = submissionService.submissionCancelNavigation(submissionId);
    return (navigationPossible)
      ? Response.ok().build()
      : Response.status(Status.BAD_REQUEST).build();
  }

  /**
   * Move project data.
   *
   * @param submissionId the submission id
   * @param projectId the project id
   * @return the response
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/moveProjectData/{submissionId}/{submissionVersion}/{projectId}/{projectVersion}")
  public Response moveProjectData(@PathParam("submissionId") String submissionId,
    @PathParam("submissionVersion") Long submissionVersion, @PathParam("projectId") String projectId,
    @PathParam("projectVersion") Long projectVersion) {
    Set<ValidationError> optimisticLockErrors =
      submissionService.moveProjectData(submissionId, submissionVersion, projectId, projectVersion);
    return (optimisticLockErrors.isEmpty())
      ? Response.ok().build()
      : Response.status(Status.CONFLICT).entity(optimisticLockErrors).build();
  }

  /**
   * Lock submission.
   *
   * @param submissionId the submission id
   * @param type the type
   * @return the response
   */
  @PUT
  @Path("/lock/{submissionId}/{type}")
  public Response lockSubmission(@PathParam("submissionId") String submissionId,
    @PathParam("type") String type) {
    submissionService
      .lockSubmission(submissionId, type, Boolean.FALSE);
    return Response.ok().build();
  }

  /**
   * Unlock submission.
   *
   * @param submissionId the submission id
   * @param type the type
   * @return the response
   */
  @PUT
  @Path("/unlock/{submissionId}/{type}")
  public Response unlockSubmission(@PathParam("submissionId") String submissionId,
    @PathParam("type") String type) {
    submissionService
      .unlockSubmission(submissionId, type);
    return Response.ok().build();
  }

  /**
   * Gets the submittents count.
   *
   * @param id the id
   * @return the submittents count
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/submittents/{id}")
  public Response getSubmittentsCount(@PathParam("id") String id) {
    int count = submissionService.getSubmittentsCount(id);
    return Response.ok(count).build();
  }

  /**
   * Checks if submission exists.
   *
   * @param id the submission id
   * @return true if exists, otherwise the error for deleted submission
   */
  @GET
  @Path("/exists/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response submissionExists(@PathParam("id") String id) {
    if (submissionService.submissionExists(id)) {
      return Response.ok(true).build();
    }
    Set<ValidationError> submissionDeleted = new HashSet<>();
    submissionDeleted
      .add(new ValidationError(ValidationMessages.DELETED_BY_ANOTHER_USER_ERROR_FIELD,
        ValidationMessages.SUBMISSION_DELETED));
    return Response.status(Response.Status.BAD_REQUEST).entity(submissionDeleted).build();
  }

  @GET
  @Path("/isStatusChanged/{id}/{status}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response isStatusChanged(@PathParam("id") String id, @PathParam("status") String status) {
    if (submissionService.isStatusChanged(id, status)) {
      Set<ValidationError> statusChanged = new HashSet<>();
      statusChanged
        .add(new ValidationError(ValidationMessages.STATUS_CHANGED_ERROR_FIELD, ValidationMessages.STATUS_CHANGED));
      return Response.status(Response.Status.BAD_REQUEST).entity(statusChanged).build();
    }
    return Response.ok().build();
  }

  /**
   * Run security check before loading Submission Create form.
   *
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/loadSubmissionCreate")
  public Response loadSubmissionCreate() {
    submissionService.submissionCreateSecurityCheck();
    return Response.ok().build();
  }

  /**
   * Run security check before loading Document Area.
   *
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/loadDocumentArea/{submissionId}")
  public Response loadDocumentArea(@PathParam("submissionId") String submissionId) {
    submissionService.submissionDocumentAreaSecurityCheck(submissionId);
    return Response.ok().build();
  }

  /**
   * Run security check before loading Formal Audit.
   *
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/loadFormalAudit/{submissionId}")
  public Response loadFormalAudit(@PathParam("submissionId") String submissionId) {
    submissionService.formalAuditSecurityCheck(submissionId);
    return Response.ok().build();
  }

  /**
   * Run security check before loading Formal Audit.
   *
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/loadSuitabilityAudit/{submissionId}")
  public Response loadSuitabilityAudit(@PathParam("submissionId") String submissionId) {
    submissionService.suitabilityAuditSecurityCheck(submissionId);
    return Response.ok().build();
  }

  /**
   * Run security check before loading Applicants List.
   *
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/loadApplicants/{submissionId}")
  public Response loadApplicants(@PathParam("submissionId") String submissionId) {
    submissionService.applicantsSecurityCheck(submissionId);
    return Response.ok().build();
  }

  /**
   * Run security check before loading Submission.
   *
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/loadSubmission/{submissionId}")
  public Response loadSubmission(@PathParam("submissionId") String submissionId) {
    submissionService.submissionViewSecurityCheck(submissionId);
    return Response.ok().build();
  }
}
