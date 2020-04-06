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

import ch.bern.submiss.services.api.administration.SubmissionCancelService;
import ch.bern.submiss.services.api.administration.SubmissionService;
import ch.bern.submiss.services.api.constants.AuditMessages;
import ch.bern.submiss.services.api.constants.TenderStatus;
import ch.bern.submiss.services.api.dto.SubmissionCancelDTO;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.api.util.ValidationMessages;
import ch.bern.submiss.web.forms.SubmissionCancelForm;
import ch.bern.submiss.web.mappers.SubmissionCancelFormMapper;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.ops4j.pax.cdi.api.OsgiService;

/**
 * REST handler for the submission cancel procedure.
 */
@Path("/submissionCancel")
@Singleton
public class SubmissionCancelResource {
  
  /** The submission cancel service. */
  @OsgiService
  @Inject
  private SubmissionCancelService submissionCancelService;
 
  @OsgiService
  @Inject
  private SubmissionService submissionService;

  /**
   * Finds a submission cancel entity given a submissionId.
   *
   * @param submissionId the submission UUID
   * @return the submission cancel entity
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{submissionId}")
  public Response getBySubmissionId(@PathParam("submissionId") String submissionId) {
    SubmissionCancelDTO submissionCancel = submissionCancelService.getBySubmissionId(submissionId);
    if (submissionCancel != null) {
      return Response.ok(submissionCancel).build();
    // case create
    } else {
      return Response.status(Response.Status.NO_CONTENT).build();
    }
  }
  
  /**
   * Updates or creates (if not exists) a submission cancel entity.
   *
   * @param submissionCancel the submissionCancel entity to be created
   * @return empty response
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response set(@Valid SubmissionCancelForm submissionCancel) {
    Set<ValidationError> errors = validation(submissionCancel);
    if (!errors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    submissionCancelService.set(SubmissionCancelFormMapper.INSTANCE.toSubmissionCancelDTO(submissionCancel));
    return Response.ok().build();
  }

  private Set<ValidationError> validation(SubmissionCancelForm submissionCancel) {
    Set<ValidationError> errors = new HashSet<>();
    if (submissionCancel.getReason() == null
        || submissionCancel.getReason().length() == LookupValues.ZERO_DOUBLE) {
      errors.add(new ValidationError("reason", ValidationMessages.MANDATORY_ERROR_MESSAGE));
      errors.add(new ValidationError(ValidationMessages.ERROR_FIELD, ValidationMessages.MANDATORY_ERROR_MESSAGE));
    } else if (submissionCancel.getReason().length() > LookupValues.TEN_THOUSAND_DOUBLE) {
      errors.add(new ValidationError("reason", ValidationMessages.CPP_REASON_GIVEN_ERROR_MESSAGE));
      errors.add(new ValidationError(ValidationMessages.ERROR_FIELD, ValidationMessages.CPP_REASON_GIVEN_ERROR_MESSAGE));
    }
    if (submissionCancel.getWorkTypes().isEmpty()) {
      errors.add(new ValidationError(ValidationMessages.ERROR_FIELD, ValidationMessages.SUBMISSION_CANCEL_REASON_SELECT_ERROR_MESSAGE));
    }
    return errors;
  }
  
  /**
   * Cancels the given submission
   *
   * @param submissionId the submission UUID
   * @return empty response
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/cancel/{submissionId}")
  public Response cancelSubmission(@PathParam("submissionId") String submissionId) {
    submissionService.updateSubmissionStatus(submissionId,
        TenderStatus.PROCEDURE_CANCELED.getValue(), AuditMessages.CANCEL_SUBMISSION.name(),
        null, LookupValues.EXTERNAL_LOG);
    return Response.ok().build();
  }
  
  /**
   * Finds the available date of the submission cancel entity given a submissionId.
   *
   * @param submissionId the submission UUID
   * @return the available date
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/availableDate/{submissionId}")
  public Response getAvailableDateBySubmissionId(@PathParam("submissionId") String submissionId) {
    Date availableDate = submissionCancelService.getAvailableDateBySubmissionId(submissionId);
    return Response.ok(availableDate).build();
  }
  
  /**
   * Checks if the Verfahrensabbruch document has been generated for all tenderers.
   *
   * @param submissionId the submission UUID
   * @return a boolean indicating whether the document has been created or not.
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/cancellationDocumentCreated/{submissionId}")
  public Response cancellationDocumentCreated(@PathParam("submissionId") String submissionId) {
    Boolean isCreated = submissionCancelService.cancellationDocumentCreated(submissionId);
    Set<ValidationError> errors = new HashSet<>();
    if (!isCreated) {
      errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
          ValidationMessages.NO_CANCELLATION_DOCUMENT_ERROR_MESSAGE));
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    return Response.ok().build();
  }
}
