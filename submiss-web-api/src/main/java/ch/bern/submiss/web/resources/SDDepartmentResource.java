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

import ch.bern.submiss.services.api.administration.SDDepartmentService;
import ch.bern.submiss.services.api.administration.SDService;
import ch.bern.submiss.services.api.constants.TextType;
import ch.bern.submiss.services.api.dto.DepartmentHistoryDTO;
import ch.bern.submiss.services.api.util.ValidationMessages;
import ch.bern.submiss.web.forms.DepartmentHistoryForm;
import ch.bern.submiss.web.mappers.DepartmentHistoryFormMapper;
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

@Path("/sd/department")
@Singleton
public class SDDepartmentResource {

  /** The Constant TELEPHONE. */
  private static final String TELEPHONE = "telephone";

  /** The Constant EMAIL. */
  private static final String EMAIL = "email";

  /** The Constant INPUT_NAME. */
  private static final String INPUT_NAME = "inputName";

  /** The Constant INPUT_SHORT_NAME. */
  private static final String INPUT_SHORT_NAME = "inputShortName";

  @OsgiService
  @Inject
  private SDDepartmentService sDDepartmentService;

  @OsgiService
  @Inject
  private SDService sDService;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<DepartmentHistoryDTO> readDepartmentsByUser() {
    return sDDepartmentService.readDepartmentsByUser();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/user")
  public List<DepartmentHistoryDTO> readAll() {
    return sDDepartmentService.readAll();
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/directorate")
  public List<DepartmentHistoryDTO> getDepartmentsByDirectoratesIds(List<String> directoratesIDs) {
    return sDDepartmentService.getDepartmentsByDirectoratesIds(directoratesIDs);
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/shortName")
  public DepartmentHistoryDTO getDepartmentBySortName(String shortName, String companyShortName) {
    return sDDepartmentService.getDepartmentBySortName(shortName, companyShortName);
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/tenant/{tenantId}")
  public List<DepartmentHistoryDTO> getDepartmentByTenant(@PathParam("tenantId") String tenantId) {
    return sDDepartmentService.getDepartmentByTenant(tenantId);
  }
  
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/departments")
  public Response getUserDepartments() {
    List<DepartmentHistoryDTO> departments = sDDepartmentService.getUserDepartments();
    return Response.ok(departments).build();
  }
  
  /**
   * Save department entry.
   *
   * @param departmentHistoryForm the department history form
   * @param isNameOrShortNameChanged the isNameOrShortNameChanged
   * @return the response
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/saveDepartmentEntry/{isNameOrShortNameChanged}")
  public Response saveDepartmentEntry(@Valid DepartmentHistoryForm departmentHistoryForm,
    @PathParam("isNameOrShortNameChanged") boolean isNameOrShortNameChanged) {
    sDService.sdSecurityCheck();
    Set<ValidationError> validationErrors = validateDepartment(departmentHistoryForm, isNameOrShortNameChanged);
    if (!validationErrors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(validationErrors).build();
    }
    // If there are no validation errors, proceed with saving/updating the department history entry.
    sDDepartmentService.saveDepartmentEntry(
      DepartmentHistoryFormMapper.INSTANCE.toDepartmentHistoryDTO(departmentHistoryForm));
    return Response.ok().build();
  }

  /**
   * Department create/update validation.
   *
   * @param departmentHistoryForm the departmentHistoryForm
   * @param isNameOrShortNameChanged the isNameOrShortNameChanged
   * @return the validation errors
   */
  private Set<ValidationError> validateDepartment(DepartmentHistoryForm departmentHistoryForm,
    boolean isNameOrShortNameChanged) {
    // Using regex. The regex code accepts a telephone/fax number. The characters can be numbers,
    // parentheses(), hyphens, periods & may contain the plus sign (+) in the beginning and can
    // contain whitespaces in between.
    String phoneRegex = "^\\+?[0-9. ()-]{0,50}$";
    Pattern phonePattern = Pattern.compile(phoneRegex);
    Set<ValidationError> errors = new HashSet<>();
    // Check if the mandatory fields are filled out.
    validateDepartmentMandatory(departmentHistoryForm, errors);
    // Check the name length.
    validateDepartmentName(departmentHistoryForm, errors);
    // Check the short name length.
    validateDepartmentShortName(departmentHistoryForm, errors);
    // Check if the name and short name combination is unique.
    validateDepartmentNameShortName(departmentHistoryForm, errors, isNameOrShortNameChanged);
    // Check the address length.
    validateDepartmentAddress(departmentHistoryForm, errors);
    // Check the post code length.
    validateDepartmentPostCode(departmentHistoryForm, errors);
    // Check the location length.
    validateDepartmentLocation(departmentHistoryForm, errors);
    // Check the telephone number.
    validateDepartmentTelephone(departmentHistoryForm, phonePattern, errors);
    // Check the fax number.
    validateDepartmentFax(departmentHistoryForm, phonePattern, errors);
    // Check the email.
    validateDepartmentEmail(departmentHistoryForm, errors);
    // Check the website address length.
    validateDepartmentWebsite(departmentHistoryForm, errors);
    return errors;
  }

  /**
   * Mandatory department validation.
   *
   * @param departmentHistoryForm the departmentHistoryForm
   * @param errors the errors
   */
  private void validateDepartmentMandatory(DepartmentHistoryForm departmentHistoryForm,
    Set<ValidationError> errors) {
    if (StringUtils.isBlank(departmentHistoryForm.getName())
      || StringUtils.isBlank(departmentHistoryForm.getShortName())
      || departmentHistoryForm.getDirectorate() == null
      || departmentHistoryForm.getBooleanInternalValue() == null
      || StringUtils.isBlank(departmentHistoryForm.getAddress())
      || StringUtils.isBlank(departmentHistoryForm.getPostCode())
      || StringUtils.isBlank(departmentHistoryForm.getLocation())
      || StringUtils.isBlank(departmentHistoryForm.getTelephone())
      || StringUtils.isBlank(departmentHistoryForm.getEmail())
      || StringUtils.isBlank(departmentHistoryForm.getWebsite())) {
      errors.add(
        new ValidationError("emptyMandatoryField", ValidationMessages.MANDATORY_ERROR_MESSAGE));
      if (StringUtils.isBlank(departmentHistoryForm.getName())) {
        errors.add(new ValidationError(INPUT_NAME, ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if (StringUtils.isBlank(departmentHistoryForm.getShortName())) {
        errors
          .add(new ValidationError(INPUT_SHORT_NAME, ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if (departmentHistoryForm.getDirectorate() == null) {
        errors.add(new ValidationError("directorate", ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if (departmentHistoryForm.getBooleanInternalValue() == null) {
        errors.add(new ValidationError("internal", ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if (StringUtils.isBlank(departmentHistoryForm.getAddress())) {
        errors.add(new ValidationError("inputAddress", ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if (StringUtils.isBlank(departmentHistoryForm.getPostCode())) {
        errors.add(new ValidationError("postCode", ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if (StringUtils.isBlank(departmentHistoryForm.getLocation())) {
        errors.add(new ValidationError("location", ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if (StringUtils.isBlank(departmentHistoryForm.getTelephone())) {
        errors.add(new ValidationError(TELEPHONE, ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if (StringUtils.isBlank(departmentHistoryForm.getEmail())) {
        errors.add(new ValidationError(EMAIL, ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
      if (StringUtils.isBlank(departmentHistoryForm.getWebsite())) {
        errors.add(new ValidationError("website", ValidationMessages.MANDATORY_ERROR_MESSAGE));
      }
    }
  }

  /**
   * Department name validation.
   *
   * @param departmentHistoryForm the departmentHistoryForm
   * @param errors the errors
   */
  private void validateDepartmentName(DepartmentHistoryForm departmentHistoryForm,
    Set<ValidationError> errors) {
    if (StringUtils.isNotBlank(departmentHistoryForm.getName())
        && departmentHistoryForm.getName().length() > TextType.MIDDLE_TEXT.getValue()) {
      errors.add(new ValidationError(INPUT_NAME, ValidationMessages.DESCRIPTION_MAX_SIZE_MESSAGE));
      errors.add(new ValidationError("descriptionErrorField",
          ValidationMessages.DESCRIPTION_MAX_SIZE_MESSAGE));
    }
  }

  /**
   * Department short name validation.
   *
   * @param departmentHistoryForm the departmentHistoryForm
   * @param errors the errors
   */
  private void validateDepartmentShortName(DepartmentHistoryForm departmentHistoryForm,
    Set<ValidationError> errors) {
    if (StringUtils.isNotBlank(departmentHistoryForm.getShortName())
        && departmentHistoryForm.getShortName().length() > TextType.SHORT_TEXT.getValue()) {
      errors.add(
          new ValidationError(INPUT_SHORT_NAME, ValidationMessages.SHORT_NAME_MAX_SIZE_MESSAGE));
      errors.add(new ValidationError("shortNameErrorField",
          ValidationMessages.SHORT_NAME_MAX_SIZE_MESSAGE));
    }
  }

  /**
   * Validate if the combination name - short name is unique.
   *
   * @param departmentHistoryForm the departmentHistoryForm
   * @param errors the errors
   * @param isNameOrShortNameChanged isNameOrShortNameChanged
   */
  private void validateDepartmentNameShortName(DepartmentHistoryForm departmentHistoryForm,
    Set<ValidationError> errors, boolean isNameOrShortNameChanged) {
    boolean validationCheck = (StringUtils.isBlank(departmentHistoryForm.getId()))
      // validation check for creating a department
      ? (StringUtils.isNotBlank(departmentHistoryForm.getName())
      && StringUtils.isNotBlank(departmentHistoryForm.getShortName())
      && !sDDepartmentService.isNameAndShortNameUnique(departmentHistoryForm.getName(),
      departmentHistoryForm.getShortName(), departmentHistoryForm.getId()))
      // validation check for editing a department
      : isNameOrShortNameChanged && (StringUtils.isNotBlank(departmentHistoryForm.getName())
        && StringUtils.isNotBlank(departmentHistoryForm.getShortName())
        && !sDDepartmentService.isNameAndShortNameUnique(departmentHistoryForm.getName(),
        departmentHistoryForm.getShortName(), departmentHistoryForm.getId())
        && departmentHistoryForm.getVersion() == 0);
    if (validationCheck) {
      errors.add(new ValidationError(INPUT_NAME, ValidationMessages.UNIQUE_NAME_AND_SHORT_NAME));
      errors.add(
        new ValidationError(INPUT_SHORT_NAME, ValidationMessages.UNIQUE_NAME_AND_SHORT_NAME));
      errors.add(
        new ValidationError("nameAndShortNameField",
          ValidationMessages.UNIQUE_NAME_AND_SHORT_NAME));
    }
  }

  /**
   * Department address validation.
   *
   * @param departmentHistoryForm the departmentHistoryForm
   * @param errors the errors
   */
  private void validateDepartmentAddress(DepartmentHistoryForm departmentHistoryForm,
    Set<ValidationError> errors) {
    if (StringUtils.isNotBlank(departmentHistoryForm.getAddress())
        && departmentHistoryForm.getAddress().length() > TextType.MIDDLE_TEXT.getValue()) {
      errors.add(new ValidationError("inputAddress", ValidationMessages.ADDRESS_MAX_SIZE_MESSAGE));
      errors.add(
          new ValidationError("addressErrorField", ValidationMessages.ADDRESS_MAX_SIZE_MESSAGE));
    }
  }

  /**
   * Department post code validation.
   *
   * @param departmentHistoryForm the departmentHistoryForm
   * @param errors the errors
   */
  private void validateDepartmentPostCode(DepartmentHistoryForm departmentHistoryForm,
    Set<ValidationError> errors) {
    if (StringUtils.isNotBlank(departmentHistoryForm.getPostCode())
        && departmentHistoryForm.getPostCode().length() > 10) {
      errors.add(new ValidationError("postCode", ValidationMessages.POST_CODE_MAX_SIZE_MESSAGE));
      errors.add(
          new ValidationError("postCodeErrorField", ValidationMessages.POST_CODE_MAX_SIZE_MESSAGE));
    }
  }

  /**
   * Department location validation.
   *
   * @param departmentHistoryForm the departmentHistoryForm
   * @param errors the errors
   */
  private void validateDepartmentLocation(DepartmentHistoryForm departmentHistoryForm,
    Set<ValidationError> errors) {
    if (StringUtils.isNotBlank(departmentHistoryForm.getLocation())
        && departmentHistoryForm.getLocation().length() > TextType.SHORT_TEXT.getValue()) {
      errors.add(new ValidationError("location", ValidationMessages.LOCATION_MAX_SIZE_MESSAGE));
      errors.add(
          new ValidationError("locationErrorField", ValidationMessages.LOCATION_MAX_SIZE_MESSAGE));
    }
  }

  /**
   * Department telephone validation.
   *
   * @param departmentHistoryForm the departmentHistoryForm
   * @param phonePattern the phonePattern
   * @param errors the errors
   */
  private void validateDepartmentTelephone(DepartmentHistoryForm departmentHistoryForm,
    Pattern phonePattern, Set<ValidationError> errors) {
    if (StringUtils.isNotBlank(departmentHistoryForm.getTelephone())) {
      // Check the telephone number length.
      if (departmentHistoryForm.getTelephone().length() > 20) {
        errors.add(new ValidationError(TELEPHONE, ValidationMessages.TELEPHONE_MAX_SIZE_MESSAGE));
        errors.add(new ValidationError("telephoneErrorField",
            ValidationMessages.TELEPHONE_MAX_SIZE_MESSAGE));
      }
      Matcher matcher = phonePattern.matcher(departmentHistoryForm.getTelephone());
      // Check if the telephone number is valid.
      if (!matcher.matches()) {
        errors.add(new ValidationError(TELEPHONE, ValidationMessages.TELEPHONE_INVALID_MESSAGE));
        errors.add(new ValidationError("telephoneErrorField",
            ValidationMessages.TELEPHONE_INVALID_MESSAGE));
      }
    }
  }

  /**
   * Department fax validation.
   *
   * @param departmentHistoryForm the departmentHistoryForm
   * @param phonePattern the phonePattern
   * @param errors the errors
   */
  private void validateDepartmentFax(DepartmentHistoryForm departmentHistoryForm,
    Pattern phonePattern, Set<ValidationError> errors) {
    if (StringUtils.isNotBlank(departmentHistoryForm.getFax())) {
      // Check the fax number length.
      if (departmentHistoryForm.getFax().length() > 20) {
        errors.add(new ValidationError("fax", ValidationMessages.FAX_MAX_SIZE_MESSAGE));
        errors.add(new ValidationError("faxErrorField", ValidationMessages.FAX_MAX_SIZE_MESSAGE));
      }
      Matcher matcher = phonePattern.matcher(departmentHistoryForm.getFax());
      // Check if the fax number is valid.
      if (!matcher.matches()) {
        errors.add(new ValidationError("fax", ValidationMessages.FAX_INVALID_MESSAGE));
        errors.add(new ValidationError("faxErrorField", ValidationMessages.FAX_INVALID_MESSAGE));
      }
    }
  }

  /**
   * Department email validation.
   *
   * @param departmentHistoryForm the departmentHistoryForm
   * @param errors the errors
   */
  private void validateDepartmentEmail(DepartmentHistoryForm departmentHistoryForm,
    Set<ValidationError> errors) {
    if (StringUtils.isNotBlank(departmentHistoryForm.getEmail())) {
      // Check the email length.
      if (departmentHistoryForm.getEmail().length() > TextType.MIDDLE_TEXT.getValue()) {
        errors.add(new ValidationError(EMAIL, ValidationMessages.EMAIL_MAX_SIZE_ERROR_MESSAGE));
        errors.add(new ValidationError("emailErrorField",
            ValidationMessages.EMAIL_MAX_SIZE_ERROR_MESSAGE));
      }
      // Using regex to check if a given email is valid (contains @).
      String emailRegex = "^(.+)@(.+)$";
      Pattern emailPattern = Pattern.compile(emailRegex);
      Matcher matcher = emailPattern.matcher(departmentHistoryForm.getEmail());
      // Check if the email is valid.
      if (!matcher.matches()) {
        errors.add(new ValidationError(EMAIL, ValidationMessages.EMAIL_FORMAT_ERROR_MESSAGE));
        errors.add(
            new ValidationError("emailErrorField", ValidationMessages.EMAIL_FORMAT_ERROR_MESSAGE));
      }
    }
  }

  /**
   * Department website validation.
   *
   * @param departmentHistoryForm the departmentHistoryForm
   * @param errors the errors
   */
  private void validateDepartmentWebsite(DepartmentHistoryForm departmentHistoryForm,
    Set<ValidationError> errors) {
    if (StringUtils.isNotBlank(departmentHistoryForm.getWebsite())
        && departmentHistoryForm.getWebsite().length() > TextType.MIDDLE_TEXT.getValue()) {
      errors.add(new ValidationError("website", ValidationMessages.WEBSITE_MAX_SIZE_MESSAGE));
      errors.add(
          new ValidationError("websiteErrorField", ValidationMessages.WEBSITE_MAX_SIZE_MESSAGE));
    }
  }

  @GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getAllActiveDepartemntsByUserTenant")
	public Response getAllActiveDepartemntsByUserTenant() {
		List<DepartmentHistoryDTO> departments = sDDepartmentService.getActiveDepartmentsByUserTenant();
		return Response.ok(departments).build();
	}

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/role")
  public List<DepartmentHistoryDTO> readDepartmentsByUserAndRole() {
    return sDDepartmentService.readDepartmentsByUserAndRole();
  }
}
