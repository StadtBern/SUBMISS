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
import ch.bern.submiss.services.api.administration.SDService;
import ch.bern.submiss.services.api.constants.TextType;
import ch.bern.submiss.services.api.util.ValidationMessages;
import ch.bern.submiss.web.forms.ProofHistoryForm;
import ch.bern.submiss.web.mappers.ProofHistoryMapper;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.OptimisticLockException;
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

  @OsgiService
  @Inject
  private SDService sDService;

  /**
   * Save proofs entry.
   *
   * @param proofHistoryForm the proof history form
   * @param isNameOrCountryChanged the isNameOrCountryChanged
   * @return the response
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/saveProofsEntry/{isNameOrCountryChanged}")
  public Response saveProofsEntry(@Valid ProofHistoryForm proofHistoryForm,
    @PathParam("isNameOrCountryChanged") boolean isNameOrCountryChanged) {
    sDService.sdSecurityCheck();
    Set<ValidationError> validationErrors = validateProofs(proofHistoryForm, isNameOrCountryChanged);
    if (!validationErrors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(validationErrors).build();
    }
    // If there are no errors, proceed with saving/updating the proofs history entry.
    Set<ValidationError> optimisticLockErrors = new HashSet<>();
    try {
      optimisticLockErrors = sDProofService
        .saveProofsEntry(ProofHistoryMapper.INSTANCE.toProofHistoryDTO(proofHistoryForm));
    } catch (OptimisticLockException e) {
      optimisticLockErrors
        .add(new ValidationError("optimisticLockErrorField", ValidationMessages.OPTIMISTIC_LOCK));
    }

    return (optimisticLockErrors.isEmpty())
      ? Response.ok().build()
      : Response.status(Response.Status.BAD_REQUEST).entity(optimisticLockErrors).build();

  }

  /**
   * Proof validation.
   *
   * @param proofHistoryForm the proofHistoryForm
   * @param isNameOrCountryChanged the isNameOrCountryChanged
   * @return the errors
   */
  private Set<ValidationError> validateProofs(ProofHistoryForm proofHistoryForm,
    boolean isNameOrCountryChanged) {
    boolean frenchTextMandatory = isFrenchTextMandatory(proofHistoryForm);
    Set<ValidationError> errors = new HashSet<>();
    // Check if the mandatory fields are filled out.
    validateProofsMandatory(proofHistoryForm, frenchTextMandatory, errors);
    // Check the proof name length.
    validateProofsName(proofHistoryForm, errors);
    // Check the validity period.
    validateProofsValidityPeriod(proofHistoryForm, errors);
    // Check the German text length.
    validateProofsGermanText(proofHistoryForm, errors);
    // Check the French text length.
    validateProofsFrenchText(proofHistoryForm, errors);
    // Check the proof order.
    validateProofsOrder(proofHistoryForm, errors);
    // Check if the country and proof name combination is unique.
    validateProofNameCountry(proofHistoryForm, errors, isNameOrCountryChanged);
    return errors;
  }

  /**
   * Validate if combination (name, country) is unique.
   *
   * @param proofHistoryForm the proofHistoryForm
   * @param errors the errors
   * @param isNameOrCountryChanged the isNameOrCountryChanged
   */
  private void validateProofNameCountry(ProofHistoryForm proofHistoryForm,
    Set<ValidationError> errors, boolean isNameOrCountryChanged) {

    boolean validationCheck = (StringUtils.isBlank(proofHistoryForm.getId()))
      // validation check for creating a proof
      ? (StringUtils.isNotBlank(proofHistoryForm.getName())
      && proofHistoryForm.getCountry().getId() != null
      && !sDProofService.isProofNameAndCountryUnique(proofHistoryForm.getId(),
      proofHistoryForm.getName(), proofHistoryForm.getCountry().getId()))
      // validation check for editing a proof
      : (isNameOrCountryChanged) && (StringUtils.isNotBlank(proofHistoryForm.getName())
        && proofHistoryForm.getCountry().getId() != null
        && !sDProofService.isProofNameAndCountryUnique(proofHistoryForm.getId(),
        proofHistoryForm.getName(), proofHistoryForm.getCountry().getId())
        && proofHistoryForm.getVersion() == 0);

    if (validationCheck) {
      errors.add(new ValidationError("proofNameAndCountryField",
        ValidationMessages.UNIQUE_PROOF_NAME_AND_COUNTRY));
      errors.add(new ValidationError(PROOF_NAME, ValidationMessages.UNIQUE_PROOF_NAME_AND_COUNTRY));
      errors.add(new ValidationError("country", ValidationMessages.UNIQUE_PROOF_NAME_AND_COUNTRY));
    }
  }

  /**
   * Proof order validation.
   *
   * @param proofHistoryForm the proofHistoryForm
   * @param errors the errors
   */
  private void validateProofsOrder(ProofHistoryForm proofHistoryForm, Set<ValidationError> errors) {
    if (proofHistoryForm.getProofOrder() != null && proofHistoryForm.getProofOrder() <= 0) {
      errors.add(new ValidationError("proofOrder", ValidationMessages.INVALID_PROOF_ORDER));
      errors
          .add(new ValidationError("proofOrderErrorField", ValidationMessages.INVALID_PROOF_ORDER));
    }
  }

  /**
   * Proof French text validation.
   *
   * @param proofHistoryForm the proofHistoryForm
   * @param errors the errors
   */
  private void validateProofsFrenchText(ProofHistoryForm proofHistoryForm,
    Set<ValidationError> errors) {
    if (StringUtils.isNotBlank(proofHistoryForm.getDescriptionFr())
        && proofHistoryForm.getDescriptionFr().length() > TextType.LONG_TEXT.getValue()) {
      errors.add(new ValidationError("frenchText", ValidationMessages.FR_TEXT_MAX_SIZE_MESSAGE));
      errors.add(
          new ValidationError("frenchTextErrorField", ValidationMessages.FR_TEXT_MAX_SIZE_MESSAGE));
    }
  }

  /**
   * Proof German text validation.
   *
   * @param proofHistoryForm the proofHistoryForm
   * @param errors the errors
   */
  private void validateProofsGermanText(ProofHistoryForm proofHistoryForm,
    Set<ValidationError> errors) {
    if (StringUtils.isNotBlank(proofHistoryForm.getDescription())
        && proofHistoryForm.getDescription().length() > TextType.LONG_TEXT.getValue()) {
      errors.add(new ValidationError("germanText", ValidationMessages.GER_TEXT_MAX_SIZE_MESSAGE));
      errors.add(new ValidationError("germanTextErrorField",
          ValidationMessages.GER_TEXT_MAX_SIZE_MESSAGE));
    }
  }

  /**
   * Proof validity period validation.
   *
   * @param proofHistoryForm the proofHistoryForm
   * @param errors the errors
   */
  private void validateProofsValidityPeriod(ProofHistoryForm proofHistoryForm,
    Set<ValidationError> errors) {
    if (proofHistoryForm.getValidityPeriod() != null && proofHistoryForm.getValidityPeriod() <= 0) {
      errors.add(new ValidationError("validityPeriod", ValidationMessages.INVALID_VALIDITY_PERIOD));
      errors.add(new ValidationError("validityPeriodErrorField",
          ValidationMessages.INVALID_VALIDITY_PERIOD));
    }
  }

  /**
   * Proof name validation.
   *
   * @param proofHistoryForm the proofHistoryForm
   * @param errors the errors
   */
  private void validateProofsName(ProofHistoryForm proofHistoryForm, Set<ValidationError> errors) {
    if (StringUtils.isNotBlank(proofHistoryForm.getName())
        && proofHistoryForm.getName().length() > TextType.MIDDLE_TEXT.getValue()) {
      errors.add(new ValidationError(PROOF_NAME, ValidationMessages.DESCRIPTION_MAX_SIZE_MESSAGE));
      errors.add(new ValidationError("descriptionErrorField",
          ValidationMessages.DESCRIPTION_MAX_SIZE_MESSAGE));
    }
  }

  /**
   * Mandatory proof validation.
   *
   * @param proofHistoryForm the proofHistoryForm
   * @param frenchTextMandatory the frenchTextMandatory
   * @param errors the errors
   */
  private void validateProofsMandatory(ProofHistoryForm proofHistoryForm,
    boolean frenchTextMandatory, Set<ValidationError> errors) {
    if (StringUtils.isBlank(proofHistoryForm.getName())
        || proofHistoryForm.getCountry().getId() == null
        || StringUtils.isBlank(proofHistoryForm.getDescription())
        || (StringUtils.isBlank(proofHistoryForm.getDescriptionFr()) && frenchTextMandatory)) {
      errors.add(
          new ValidationError("emptyMandatoryField", ValidationMessages.MANDATORY_ERROR_MESSAGE));
      if (StringUtils.isBlank(proofHistoryForm.getName())) {
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
  }

  /**
   * Check if French text is mandatory.
   *
   * @param proofHistoryForm the proofHistoryForm
   * @return boolean frenchTextMandatory
   */
  private boolean isFrenchTextMandatory(ProofHistoryForm proofHistoryForm) {
    String switzerlandId = sDCountryService.readAll().get(0).getCountryId().getId();
    boolean frenchTextMandatory = false;
    if (proofHistoryForm.getCountry() != null && proofHistoryForm.getCountry().getId() != null
        && proofHistoryForm.getCountry().getId().equals(switzerlandId)) {
      // If the country is Switzerland, the French text field is mandatory.
      frenchTextMandatory = true;
    }
    return frenchTextMandatory;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/first/{countryId}")
  public Response isFirstCountryProof(@PathParam("countryId") String countryId) {
    return Response.ok(sDProofService.isFirstCountryProof(countryId)).build();
  }
}
