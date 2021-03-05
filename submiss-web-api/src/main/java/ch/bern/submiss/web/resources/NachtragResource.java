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

import ch.bern.submiss.services.api.administration.NachtragService;
import ch.bern.submiss.services.api.dto.NachtragDTO;
import ch.bern.submiss.services.api.util.ValidationMessages;
import ch.bern.submiss.web.forms.NachtragForm;
import ch.bern.submiss.web.forms.ReopenForm;
import ch.bern.submiss.web.mappers.NachtragFormMapper;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import java.util.Calendar;
import java.util.HashSet;
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
import javax.ws.rs.core.Response.Status;
import org.apache.commons.lang3.StringUtils;
import org.ops4j.pax.cdi.api.OsgiService;

/**
 * REST handler for nachtrag.
 */
@Path("/nachtrag")
@Singleton
public class NachtragResource {

  @OsgiService
  @Inject
  private NachtragService nachtragService;

  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{nachtragId}")
  public Response getNachtrag(@PathParam("nachtragId") String nachtragId) {
    return Response.ok(nachtragService.getNachtrag(nachtragId)).build();
  }

  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/submittents/{submissionId}")
  public Response getNachtragSubmittents(@PathParam("submissionId") String submissionId) {
    return Response.ok(nachtragService.getNachtragSubmittents(submissionId)).build();
  }

  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/submittent/{offerId}")
  public Response getNachtragsByNachtragSubmittent(@PathParam("offerId") String offerId) {
    return Response.ok(nachtragService.getNachtragsByNachtragSubmittent(offerId)).build();
  }

  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateNachtrag(@Valid NachtragForm nachtragForm) {
    NachtragDTO nachtragDTO = NachtragFormMapper.INSTANCE.toNachtragDTO(nachtragForm);
    // Check for validation errors
    Set<ValidationError> validationErrors = validate(nachtragDTO);
    if (!validationErrors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(validationErrors).build();
    }
    nachtragService.updateNachtrag(nachtragDTO);

    return Response.ok().build();
  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/{offerId}")
  public Response createNachtrag(@PathParam("offerId") String offerId) {
    nachtragService.createNachtrag(offerId);
    return Response.ok().build();
  }

  @DELETE
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/{nachtragId}/{version}")
  public Response deleteNachtrag(@PathParam("nachtragId") String nachtragId,
  @PathParam("version") Long version){
    nachtragService.deleteNachtrag(nachtragId, version);
    return Response.ok().build();
  }

  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/submittent/{offerId}")
  public Response addNachtragSubmittent(@PathParam("offerId") String offerId) {
    nachtragService.addNachtragSubmittent(offerId);
    return Response.ok().build();
  }

  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/awardedOffers/{submissionId}")
  public Response getAwardedOffers(@PathParam("submissionId") String submissionId) {
    return Response.ok(nachtragService.getAwardedOffers(submissionId)).build();
  }

  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/close/{nachtragId}/{version}")
  public Response closeNachtrag(@PathParam("nachtragId") String nachtragId,
    @PathParam("version") Long version) {
    Set<ValidationError> validationErrors = nachtragService.closeNachtrag(nachtragId, version);
    return (validationErrors.isEmpty())
      ? Response.ok().build()
      : Response.status(Status.BAD_REQUEST).entity(validationErrors).build();
  }

  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/reopen/{nachtragId}/{version}")
  public Response reopenNachtrag(@PathParam("nachtragId") String nachtragId,
    @PathParam("version") Long version, @Valid ReopenForm reopenForm) {
    nachtragService.reopenNachtrag(nachtragId, reopenForm.getReopenReason(), version);
    return Response.ok().build();
  }

  /**
   * Validation for update Nachtrag.
   *
   * @param nachtragDTO the Nachtrag DTO
   */
  private Set<ValidationError> validate(NachtragDTO nachtragDTO) {
    Set<ValidationError> errors = new HashSet<>();
    if (nachtragDTO != null) {
      updateNachtragMandatoryValidation(nachtragDTO, errors);
      /* Date validation */
      nachtragDateValidation(nachtragDTO, errors);
      /* Nachtrag max size validation */
      nachtragMaxSizeValication(nachtragDTO, errors);
    }
    return errors;
  }

  /**
   * Mandatory validation for update Nachtrag.
   *
   * @param nachtragDTO the Nachtrag DTO
   * @param errors      the validation errors
   */
  private void updateNachtragMandatoryValidation(NachtragDTO nachtragDTO,
    Set<ValidationError> errors) {
    // Check mandatory fields
    if (StringUtils.isBlank(nachtragDTO.getTitle()) || nachtragDTO.getNachtragDate() == null
      || nachtragDTO.getGrossAmount() == null || nachtragDTO.getGrossAmount() == 0) {
      errors.add(new ValidationError(ValidationMessages.NACHTRAG_MANDATORY_ERROR_FIELD,
        ValidationMessages.NACHTRAG_MANDATORY_ERROR_MESSAGE));
      if (StringUtils.isBlank(nachtragDTO.getTitle())) {
        errors.add(new ValidationError("title",
          ValidationMessages.NACHTRAG_MANDATORY_ERROR_MESSAGE));
      }
      if (nachtragDTO.getNachtragDate() == null) {
        errors.add(new ValidationError("nachtragDate",
          ValidationMessages.NACHTRAG_MANDATORY_ERROR_MESSAGE));
      }
      if (nachtragDTO.getGrossAmount() == null || nachtragDTO.getGrossAmount() == 0) {
        errors.add(new ValidationError("grossAmount",
          ValidationMessages.NACHTRAG_MANDATORY_ERROR_MESSAGE));
      }
    }
  }

  /**
   * Date validation for update Nachtrag.
   *
   * @param nachtragDTO the Nachtrag DTO
   * @param errors      the validation errors
   */
  private void nachtragDateValidation(NachtragDTO nachtragDTO, Set<ValidationError> errors) {
    if (nachtragDTO.getOffer().getOfferDate() != null && nachtragDTO.getNachtragDate() != null){
      Calendar cal1 = Calendar.getInstance();
      Calendar cal2 = Calendar.getInstance();
      cal1.setTime(nachtragDTO.getNachtragDate());
      cal2.setTime(nachtragDTO.getOffer().getSubmittent().getSubmissionId().getSecondDeadline());
      if (cal1.get(Calendar.YEAR) < cal2.get(Calendar.YEAR)
            || (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
            && cal1.get(Calendar.DAY_OF_YEAR) < cal2.get(Calendar.DAY_OF_YEAR))) {
        errors.add(new ValidationError(ValidationMessages.NACHTRAG_DATE_ERROR_FIELD,
          ValidationMessages.NACHTRAG_DATE_BEFORE_START_DATE_ERROR_MESSAGE));
        errors.add(new ValidationError("nachtragDate",
          ValidationMessages.NACHTRAG_DATE_BEFORE_START_DATE_ERROR_MESSAGE));
      }
    }
  }

  /**
   * Max size validation for update Nachtrag.
   *
   * @param nachtragDTO the Nachtrag DTO
   * @param errors      the validation errors
   */
  private void nachtragMaxSizeValication(NachtragDTO nachtragDTO, Set<ValidationError> errors) {
    if ((!StringUtils.isBlank(nachtragDTO.getTitle()) && nachtragDTO.getTitle().length() > 100)
      || (!StringUtils.isBlank(nachtragDTO.getNotes()) && nachtragDTO.getNotes().length() > 65535)
      || (!StringUtils.isBlank(nachtragDTO.getDiscountDescription())
      && nachtragDTO.getDiscountDescription().length() > 100)) {
      errors.add(new ValidationError(ValidationMessages.NACHTRAG_MAX_SIZE_ERROR_FIELD,
        ValidationMessages.NACHTRAG_MAX_SIZE_TITLE_MESSAGE));
      if (!nachtragDTO.getTitle().isEmpty()
        && nachtragDTO.getTitle().length() > 100) {
        errors.add(new ValidationError("title",
          ValidationMessages.NACHTRAG_MAX_SIZE_TITLE_MESSAGE));
      }
      if (!StringUtils.isBlank(nachtragDTO.getNotes())
        && nachtragDTO.getNotes().length() > 65535) {
        errors.add(new ValidationError("notes",
          ValidationMessages.NACHTRAG_MAX_SIZE_NOTES_MESSAGE));
      }
      if (!StringUtils.isBlank(nachtragDTO.getDiscountDescription())
        && nachtragDTO.getNachtragName().length() > 100) {
        errors.add(new ValidationError("discountName",
          ValidationMessages.NACHTRAG_MAX_SIZE_DISCOUNT_NAME_MESSAGE));
      }
    }
  }
}
