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

import ch.bern.submiss.services.api.administration.SDDirectorateService;
import ch.bern.submiss.services.api.administration.SDService;
import ch.bern.submiss.services.api.constants.TextType;
import ch.bern.submiss.services.api.dto.DirectorateHistoryDTO;
import ch.bern.submiss.services.api.util.ValidationMessages;
import ch.bern.submiss.web.forms.DirectorateHistoryForm;
import ch.bern.submiss.web.mappers.DirectorateMapper;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import org.apache.commons.lang3.StringUtils;
import org.ops4j.pax.cdi.api.OsgiService;

@Path("/sd/directorate")
@Singleton
public class SDDirectorateResource {

  /** The Constant TELEPHONE. */
  private static final String TELEPHONE = "telephone";

  /** The Constant EMAIL. */
  private static final String EMAIL = "email";

  /** The Constant INPUT_NAME. */
  private static final String INPUT_NAME = "inputName";

  @OsgiService
  @Inject
  private SDDirectorateService sDDirectorateService;

  @OsgiService
  @Inject
  private SDService sDService;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<DirectorateHistoryDTO> getPermittedDirectories() {
    return sDDirectorateService.getAllPermittedDirectorates();
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/department")
  public List<DirectorateHistoryDTO> getDirectoratesByDepartmentIds(List<String> departmentIDs) {
    return sDDirectorateService.getDirectoratesByDepartmentIds(departmentIDs);
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/id")
  public List<DirectorateHistoryDTO> getDirectoratesByIds(List<String> directoratesIDs) {
    return sDDirectorateService.getDirectoratesByIds(directoratesIDs);
  }
  
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/tenant/{tenantId}")
  public List<DirectorateHistoryDTO> getDirectoratesByTenantId(
      @PathParam("tenantId") String tenantId) {
    return sDDirectorateService.getDirectoratesByTenantId(tenantId);
  }

  /**
   * Save department entry.
   *
   * @param directorateHistoryForm the department history form
   * @param isNameChanged the isNameChanged
   * @return the response
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/saveDirectorateEntry/{isNameChanged}")
  public Response saveDirectorateEntry(@Valid DirectorateHistoryForm directorateHistoryForm,
    @PathParam("isNameChanged") boolean isNameChanged) {
    sDService.sdSecurityCheck();
    Set<ValidationError> errors = validateDirectorate(directorateHistoryForm, isNameChanged);
    if (!errors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    // If there are no errors proceed with saving/updating the directorate history entry.
    Set<ValidationError> optimisticLockErrors = sDDirectorateService.saveDirectorateEntry(
        DirectorateMapper.INSTANCE.toDirectorateHistoryDTO(directorateHistoryForm));
    return (optimisticLockErrors.isEmpty())
      ? Response.ok().build()
      : Response.status(Response.Status.BAD_REQUEST).entity(optimisticLockErrors).build();
  }

  /** Function to validate the department */
  private Set<ValidationError> validateDirectorate(DirectorateHistoryForm directorateHistoryForm,
    boolean isNameChanged) {
    // Using regex. The regex code accepts a telephone/fax number. The characters can be numbers,
    // parentheses(), hyphens, periods & may contain the plus sign (+) in the beginning and can
    // contain whitespaces in between.
    String phoneRegex = "^\\+?[0-9. ()-]{0,50}$";
    Pattern phonePattern = Pattern.compile(phoneRegex);
    Set<ValidationError> errors = new HashSet<>();
    // Check if the mandatory fields are filled out.
    validateDirectorateMandatory(directorateHistoryForm, errors);
    // Check the name.
    validateDirectorateName(directorateHistoryForm, errors, isNameChanged);
    // Check the short name length.
    validateDirectorateShortName(directorateHistoryForm, errors);
    // Check the address length.
    validateDirectorateAddress(directorateHistoryForm, errors);
    // Check the post code length.
    validateDirectoratePostCode(directorateHistoryForm, errors);
    // Check the location length.
    validateDirectorateLocation(directorateHistoryForm, errors);
    // Check the telephone number.
    validateDirectorateTelephone(directorateHistoryForm, phonePattern, errors);
    // Check the fax number.
    validateDirectorateFax(directorateHistoryForm, phonePattern, errors);
    // Check the email.
    validateDirectorateEmail(directorateHistoryForm, errors);
    // Check the website address length.
    validateDirectorateWebsite(directorateHistoryForm, errors);
    return errors;
  }

  /**
   * Mandatory directorate validation.
   *
   * @param directorateHistoryForm the directorateHistoryForm
   * @param errors the errors
   */
  private void validateDirectorateMandatory(DirectorateHistoryForm directorateHistoryForm,
    Set<ValidationError> errors) {
    if (StringUtils.isBlank(directorateHistoryForm.getName())
      || StringUtils.isBlank(directorateHistoryForm.getShortName())
      || StringUtils.isBlank(directorateHistoryForm.getAddress())
      || StringUtils.isBlank(directorateHistoryForm.getPostCode())
      || StringUtils.isBlank(directorateHistoryForm.getLocation())
      || StringUtils.isBlank(directorateHistoryForm.getTelephone())
      || StringUtils.isBlank(directorateHistoryForm.getEmail())
      || StringUtils.isBlank(directorateHistoryForm.getWebsite())) {
      errors.add(
        new ValidationError("emptyMandatoryField", ValidationMessages.MANDATORY_ERROR_MESSAGE));
      if (StringUtils.isBlank(directorateHistoryForm.getName())) {
        errors.add(new ValidationError(INPUT_NAME, ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if (StringUtils.isBlank(directorateHistoryForm.getShortName())) {
        errors
          .add(new ValidationError("inputShortName", ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if (StringUtils.isBlank(directorateHistoryForm.getAddress())) {
        errors.add(new ValidationError("inputAddress", ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if (StringUtils.isBlank(directorateHistoryForm.getPostCode())) {
        errors.add(new ValidationError("postCode", ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if (StringUtils.isBlank(directorateHistoryForm.getLocation())) {
        errors.add(new ValidationError("location", ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if (StringUtils.isBlank(directorateHistoryForm.getTelephone())) {
        errors.add(new ValidationError(TELEPHONE, ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if (StringUtils.isBlank(directorateHistoryForm.getEmail())) {
        errors.add(new ValidationError(EMAIL, ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if (StringUtils.isBlank(directorateHistoryForm.getWebsite())) {
        errors.add(new ValidationError("website", ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
    }
  }

  /**
   * Directorate name validation.
   *
   * @param directorateHistoryForm the directorateHistoryForm
   * @param errors the errors
   * @param isNameChanged the isNameChanged
   */
  private void validateDirectorateName(DirectorateHistoryForm directorateHistoryForm,
    Set<ValidationError> errors, boolean isNameChanged) {
    if (StringUtils.isNotBlank(directorateHistoryForm.getName())) {
      // Check the name length.
      if (directorateHistoryForm.getName().length() > TextType.MIDDLE_TEXT.getValue()) {
        errors
          .add(new ValidationError(INPUT_NAME, ValidationMessages.DESCRIPTION_MAX_SIZE_MESSAGE));
        errors.add(new ValidationError("descriptionErrorField",
          ValidationMessages.DESCRIPTION_MAX_SIZE_MESSAGE));
      }
      // Check if the name is unique.
      boolean validationCheck = (StringUtils.isBlank(directorateHistoryForm.getId()))
        // validation check for creating a directorate
        ? !sDDirectorateService.isNameUnique(directorateHistoryForm.getName(),
        directorateHistoryForm.getId())
        // validation check for editing a directorate
        : isNameChanged && !sDDirectorateService.isNameUnique(directorateHistoryForm.getName(),
          directorateHistoryForm.getId()) && directorateHistoryForm.getVersion() == 0;
      if (validationCheck) {
        errors.add(new ValidationError(INPUT_NAME, ValidationMessages.SAME_DESCRIPTION));
        errors
          .add(new ValidationError("descriptionErrorField", ValidationMessages.SAME_DESCRIPTION));
      }
    }
  }

  /**
   * Directorate short name validation.
   *
   * @param directorateHistoryForm the directorateHistoryForm
   * @param errors the errors
   */
  private void validateDirectorateShortName(DirectorateHistoryForm directorateHistoryForm,
    Set<ValidationError> errors) {
    if (StringUtils.isNotBlank(directorateHistoryForm.getShortName())
      && directorateHistoryForm.getShortName().length() > TextType.SHORT_TEXT.getValue()) {
      errors.add(
        new ValidationError("inputShortName", ValidationMessages.SHORT_NAME_MAX_SIZE_MESSAGE));
      errors.add(new ValidationError("shortNameErrorField",
        ValidationMessages.SHORT_NAME_MAX_SIZE_MESSAGE));
    }
  }

  /**
   * Directorate address validation.
   *
   * @param directorateHistoryForm the directorateHistoryForm
   * @param errors the errors
   */
  private void validateDirectorateAddress(DirectorateHistoryForm directorateHistoryForm,
    Set<ValidationError> errors) {
    if (StringUtils.isNotBlank(directorateHistoryForm.getAddress())
      && directorateHistoryForm.getAddress().length() > TextType.MIDDLE_TEXT.getValue()) {
      errors.add(new ValidationError("inputAddress", ValidationMessages.ADDRESS_MAX_SIZE_MESSAGE));
      errors.add(
        new ValidationError("addressErrorField", ValidationMessages.ADDRESS_MAX_SIZE_MESSAGE));
    }
  }

  /**
   * Directorate post code validation.
   *
   * @param directorateHistoryForm the directorateHistoryForm
   * @param errors the errors
   */
  private void validateDirectoratePostCode(DirectorateHistoryForm directorateHistoryForm,
    Set<ValidationError> errors) {
    if (StringUtils.isNotBlank(directorateHistoryForm.getPostCode())
      && directorateHistoryForm.getPostCode().length() > 10) {
      errors.add(new ValidationError("postCode", ValidationMessages.POST_CODE_MAX_SIZE_MESSAGE));
      errors.add(
        new ValidationError("postCodeErrorField", ValidationMessages.POST_CODE_MAX_SIZE_MESSAGE));
    }
  }

  /**
   * Directorate location validation.
   *
   * @param directorateHistoryForm the directorateHistoryForm
   * @param errors the errors
   */
  private void validateDirectorateLocation(DirectorateHistoryForm directorateHistoryForm,
    Set<ValidationError> errors) {
    if (StringUtils.isNotBlank(directorateHistoryForm.getLocation())
      && directorateHistoryForm.getLocation().length() > TextType.SHORT_TEXT.getValue()) {
      errors.add(new ValidationError("location", ValidationMessages.LOCATION_MAX_SIZE_MESSAGE));
      errors.add(
        new ValidationError("locationErrorField", ValidationMessages.LOCATION_MAX_SIZE_MESSAGE));
    }
  }

  /**
   * Directorate telephone validation.
   *
   * @param directorateHistoryForm the directorateHistoryForm
   * @param phonePattern the phonePattern
   * @param errors the errors
   */
  private void validateDirectorateTelephone(DirectorateHistoryForm directorateHistoryForm,
    Pattern phonePattern, Set<ValidationError> errors) {
    if (StringUtils.isNotBlank(directorateHistoryForm.getTelephone())) {
      // Check the telephone number length.
      if (directorateHistoryForm.getTelephone().length() > 20) {
        errors.add(new ValidationError(TELEPHONE, ValidationMessages.TELEPHONE_MAX_SIZE_MESSAGE));
        errors.add(new ValidationError("telephoneErrorField",
          ValidationMessages.TELEPHONE_MAX_SIZE_MESSAGE));
      }
      Matcher matcher = phonePattern.matcher(directorateHistoryForm.getTelephone());
      // Check if the telephone number is valid.
      if (!matcher.matches()) {
        errors.add(new ValidationError(TELEPHONE, ValidationMessages.TELEPHONE_INVALID_MESSAGE));
        errors.add(new ValidationError("telephoneErrorField",
          ValidationMessages.TELEPHONE_INVALID_MESSAGE));
      }
    }
  }

  /**
   * Directorate fax validation.
   *
   * @param directorateHistoryForm the directorateHistoryForm
   * @param phonePattern the phonePattern
   * @param errors the errors
   */
  private void validateDirectorateFax(DirectorateHistoryForm directorateHistoryForm,
    Pattern phonePattern, Set<ValidationError> errors) {
    if (StringUtils.isNotBlank(directorateHistoryForm.getFax())) {
      // Check the fax number length.
      if (directorateHistoryForm.getFax().length() > 20) {
        errors.add(new ValidationError("fax", ValidationMessages.FAX_MAX_SIZE_MESSAGE));
        errors.add(new ValidationError("faxErrorField", ValidationMessages.FAX_MAX_SIZE_MESSAGE));
      }
      Matcher matcher = phonePattern.matcher(directorateHistoryForm.getFax());
      // Check if the fax number is valid.
      if (!matcher.matches()) {
        errors.add(new ValidationError("fax", ValidationMessages.FAX_INVALID_MESSAGE));
        errors.add(new ValidationError("faxErrorField", ValidationMessages.FAX_INVALID_MESSAGE));
      }
    }
  }

  /**
   * Directorate email validation.
   *
   * @param directorateHistoryForm the directorateHistoryForm
   * @param errors the errors
   */
  private void validateDirectorateEmail(DirectorateHistoryForm directorateHistoryForm,
    Set<ValidationError> errors) {
    // Using regex to check if a given email is valid (contains @).
    String emailRegex = "^(.+)@(.+)$";
    Pattern emailPattern = Pattern.compile(emailRegex);
    if (StringUtils.isNotBlank(directorateHistoryForm.getEmail())) {
      // Check the email length.
      if (directorateHistoryForm.getEmail().length() > TextType.MIDDLE_TEXT.getValue()) {
        errors.add(new ValidationError(EMAIL, ValidationMessages.EMAIL_MAX_SIZE_ERROR_MESSAGE));
        errors.add(new ValidationError("emailErrorField",
          ValidationMessages.EMAIL_MAX_SIZE_ERROR_MESSAGE));
      }
      Matcher matcher = emailPattern.matcher(directorateHistoryForm.getEmail());
      // Check if the email is valid.
      if (!matcher.matches()) {
        errors.add(new ValidationError(EMAIL, ValidationMessages.EMAIL_FORMAT_ERROR_MESSAGE));
        errors.add(
          new ValidationError("emailErrorField", ValidationMessages.EMAIL_FORMAT_ERROR_MESSAGE));
      }
    }
  }

  /**
   * Directorate website validation.
   *
   * @param directorateHistoryForm the directorateHistoryForm
   * @param errors the errors
   */
  private void validateDirectorateWebsite(DirectorateHistoryForm directorateHistoryForm,
    Set<ValidationError> errors) {
    if (StringUtils.isNotBlank(directorateHistoryForm.getWebsite())
        && directorateHistoryForm.getWebsite().length() > TextType.MIDDLE_TEXT.getValue()) {
      errors.add(new ValidationError("website", ValidationMessages.WEBSITE_MAX_SIZE_MESSAGE));
      errors.add(
          new ValidationError("websiteErrorField", ValidationMessages.WEBSITE_MAX_SIZE_MESSAGE));
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/getAllDirectoratesByUserTenant/")
  public List<DirectorateHistoryDTO> getAllDirectoratesByUserTenant() {
    return sDDirectorateService.getAllDirectoratesByUserTenant();
  }
}
