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

import ch.bern.submiss.services.api.administration.SDCountryService;
import ch.bern.submiss.services.api.administration.SDProofService;
import ch.bern.submiss.services.api.constants.TextType;
import ch.bern.submiss.services.api.util.ValidationMessages;
import ch.bern.submiss.web.forms.ProofHistoryForm;
import ch.bern.submiss.web.mappers.ProofHistoryMapper;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.ops4j.pax.cdi.api.OsgiService;

@Path("/sd/proof")
@Singleton
public class SDProofResource {

  /** The Constant PROOF_NAME. */
  private static final String PROOF_NAME = "proofName";

  @OsgiService
  @Inject
  private SDProofService sDProofService;

  @Inject
  private SDCountryService sDCountryService;

  /**
   * Save proofs entry.
   *
   * @param proofHistoryForm the proof history form
   * @return the response
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/saveProofsEntry")
  public Response saveProofsEntry(@Valid ProofHistoryForm proofHistoryForm) {
    Set<ValidationError> errors = validateProofs(proofHistoryForm);
    if (!errors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    // If there are no errors, proceed with saving/updating the proofs history entry.
    sDProofService.saveProofsEntry(ProofHistoryMapper.INSTANCE.toProofHistoryDTO(proofHistoryForm));
    return Response.ok().build();
  }

  /** Function to validate the proofs type */
  private Set<ValidationError> validateProofs(ProofHistoryForm proofHistoryForm) {
    String switzerlandId = sDCountryService.readAll().get(0).getCountryId().getId();
    boolean frenchTextMandatory = false;
    if (proofHistoryForm.getCountry() != null && proofHistoryForm.getCountry().getId() != null
        && proofHistoryForm.getCountry().getId().equals(switzerlandId)) {
      // If the country is Switzerland, the French text field is mandatory.
      frenchTextMandatory = true;
    }
    Set<ValidationError> errors = new HashSet<>();
    // Check if the mandatory fields are filled out.
    if (StringUtils.isBlank(proofHistoryForm.getProofName())
        || proofHistoryForm.getCountry().getId() == null
        || StringUtils.isBlank(proofHistoryForm.getDescription())
        || (StringUtils.isBlank(proofHistoryForm.getDescriptionFr()) && frenchTextMandatory)) {
      errors.add(
          new ValidationError("emptyMandatoryField", ValidationMessages.MANDATORY_ERROR_MESSAGE));
      if (StringUtils.isBlank(proofHistoryForm.getProofName())) {
        errors.add(new ValidationError(PROOF_NAME, ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if (proofHistoryForm.getCountry().getId() == null) {
        errors.add(new ValidationError("country", ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if (StringUtils.isBlank(proofHistoryForm.getDescription())) {
        errors.add(new ValidationError("germanText", ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if (StringUtils.isBlank(proofHistoryForm.getDescriptionFr()) && frenchTextMandatory) {
        errors.add(new ValidationError("frenchText", ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
    }
    // Check the proof name length.
    if (StringUtils.isNotBlank(proofHistoryForm.getProofName())
        && proofHistoryForm.getProofName().length() > TextType.MIDDLE_TEXT.getValue()) {
      errors.add(new ValidationError(PROOF_NAME, ValidationMessages.DESCRIPTION_MAX_SIZE_MESSAGE));
      errors.add(new ValidationError("descriptionErrorField",
          ValidationMessages.DESCRIPTION_MAX_SIZE_MESSAGE));
    }
    // Check the validity period.
    if (proofHistoryForm.getValidityPeriod() != null && proofHistoryForm.getValidityPeriod() <= 0) {
      errors.add(new ValidationError("validityPeriod", ValidationMessages.INVALID_VALIDITY_PERIOD));
      errors.add(new ValidationError("validityPeriodErrorField",
          ValidationMessages.INVALID_VALIDITY_PERIOD));
    }
    // Check the German text length.
    if (StringUtils.isNotBlank(proofHistoryForm.getDescription())
        && proofHistoryForm.getDescription().length() > TextType.LONG_TEXT.getValue()) {
      errors.add(new ValidationError("germanText", ValidationMessages.GER_TEXT_MAX_SIZE_MESSAGE));
      errors.add(new ValidationError("germanTextErrorField",
          ValidationMessages.GER_TEXT_MAX_SIZE_MESSAGE));
    }
    // Check the French text length.
    if (StringUtils.isNotBlank(proofHistoryForm.getDescriptionFr())
        && proofHistoryForm.getDescriptionFr().length() > TextType.LONG_TEXT.getValue()) {
      errors.add(new ValidationError("frenchText", ValidationMessages.FR_TEXT_MAX_SIZE_MESSAGE));
      errors.add(
          new ValidationError("frenchTextErrorField", ValidationMessages.FR_TEXT_MAX_SIZE_MESSAGE));
    }
    // Check the proof order.
    if (proofHistoryForm.getProofOrder() != null && proofHistoryForm.getProofOrder() <= 0) {
      errors.add(new ValidationError("proofOrder", ValidationMessages.INVALID_PROOF_ORDER));
      errors
          .add(new ValidationError("proofOrderErrorField", ValidationMessages.INVALID_PROOF_ORDER));
    }
    // Check if the country and proof name combination is unique.
    if (StringUtils.isNotBlank(proofHistoryForm.getProofName())
        && proofHistoryForm.getCountry().getId() != null
        && !sDProofService.isProofNameAndCountryUnique(proofHistoryForm.getId(),
            proofHistoryForm.getProofName(), proofHistoryForm.getCountry().getId())) {
      errors.add(new ValidationError("proofNameAndCountryField",
          ValidationMessages.UNIQUE_PROOF_NAME_AND_COUNTRY));
      errors.add(new ValidationError(PROOF_NAME, ValidationMessages.UNIQUE_PROOF_NAME_AND_COUNTRY));
      errors.add(new ValidationError("country", ValidationMessages.UNIQUE_PROOF_NAME_AND_COUNTRY));
    }
    return errors;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/first/{countryId}")
  public Response isFirstCountryProof(@PathParam("countryId") String countryId) {
    return Response.ok(sDProofService.isFirstCountryProof(countryId)).build();
  }
}
