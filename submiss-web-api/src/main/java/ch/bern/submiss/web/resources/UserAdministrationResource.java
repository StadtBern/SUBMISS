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
import ch.bern.submiss.services.api.administration.SDTenantService;
import ch.bern.submiss.services.api.administration.UserAdministrationService;
import ch.bern.submiss.services.api.constants.Group;
import ch.bern.submiss.services.api.constants.Template;
import ch.bern.submiss.services.api.constants.TemplateConstants;
import ch.bern.submiss.services.api.dto.SubmissUserDTO;
import ch.bern.submiss.services.api.dto.TenantDTO;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.api.util.LookupValues.USER_ATTRIBUTES;
import ch.bern.submiss.services.api.util.LookupValues.USER_STATUS;
import ch.bern.submiss.services.api.util.LookupValues.WEBSSO_ATTRIBUTES;
import ch.bern.submiss.services.api.util.ValidationMessages;
import ch.bern.submiss.web.forms.DepartmentHistoryForm;
import ch.bern.submiss.web.forms.ProjectForm;
import ch.bern.submiss.web.forms.UserExportForm;
import ch.bern.submiss.web.forms.UserForm;
import ch.bern.submiss.web.forms.UserRegistrationForm;
import ch.bern.submiss.web.forms.UserSearchForm;
import ch.bern.submiss.web.mappers.DepartmentHistoryFormMapper;
import ch.bern.submiss.web.mappers.TenantMapper;
import ch.bern.submiss.web.mappers.UserFormMapper;
import ch.bern.submiss.web.mappers.UserSearchFormMapper;
import com.eurodyn.qlack2.fuse.aaa.api.OperationService;
import com.eurodyn.qlack2.fuse.aaa.api.UserGroupService;
import com.eurodyn.qlack2.fuse.aaa.api.UserService;
import com.eurodyn.qlack2.fuse.aaa.api.dto.UserAttributeDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import com.eurodyn.qlack2.util.sso.dto.SAMLAttributeDTO;
import com.eurodyn.qlack2.util.sso.dto.WebSSOHolder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import org.apache.commons.lang3.StringUtils;
import org.ops4j.pax.cdi.api.OsgiService;

/**
 * The Class UserAdministrationResource.
 */
@Path("/users")
@Singleton
public class UserAdministrationResource {

  /**
   * The Constant FUNCTION_FIELD.
   */
  private static final String FUNCTION_FIELD = "function";
  /**
   * The user group service.
   */
  @OsgiService
  @Inject
  protected UserGroupService userGroupService;
  /**
   * The s D tenant service.
   */
  @OsgiService
  @Inject
  protected SDTenantService sDTenantService;
  /**
   * The operation service.
   */
  @OsgiService
  @Inject
  protected OperationService operationService;
  /**
   * The user administration service.
   */
  @OsgiService
  @Inject
  private UserAdministrationService userAdministrationService;
  /**
   * The user service.
   */
  @OsgiService
  @Inject
  private UserService userService;
  /**
   * The s D department service.
   */
  @OsgiService
  @Inject
  private SDDepartmentService sDDepartmentService;

  /**
   * Register User.
   *
   * @param form the form
   * @return the response
   */
  @POST
  @Path("/create")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response create(@Valid UserRegistrationForm form) {

    UserDTO userDTO = new UserDTO();
    TenantDTO tenant = null;
    // the attributes of the user are taken from the websso attributes
    Set<UserAttributeDTO> attr = new HashSet<>();
    String department = null;
    String company = null;
    String userId = null;
    String userName = null;
    String firstName = null;
    String lastName = null;
    String email = null;
    List<SAMLAttributeDTO> samlAttributeDTOList = WebSSOHolder.getAttributes();

    for (SAMLAttributeDTO samlAttributeDTO : samlAttributeDTOList) {
      if (WEBSSO_ATTRIBUTES.USER_ID.getValue().equals(samlAttributeDTO.getName())) {
        userId = samlAttributeDTO.getValue();
      } else if (WEBSSO_ATTRIBUTES.USERNAME.getValue().equals(samlAttributeDTO.getName())) {
        userName = samlAttributeDTO.getValue();
      } else if (WEBSSO_ATTRIBUTES.FIRST_NAME.getValue().equals(samlAttributeDTO.getName())) {
        firstName = samlAttributeDTO.getValue();
      } else if (WEBSSO_ATTRIBUTES.LAST_NAME.getValue().equals(samlAttributeDTO.getName())) {
        lastName = samlAttributeDTO.getValue();
      } else if (WEBSSO_ATTRIBUTES.EMAIL.getValue().equals(samlAttributeDTO.getName())) {
        email = samlAttributeDTO.getValue();
      } else if (WEBSSO_ATTRIBUTES.DEPARTMENT.getValue().equals(samlAttributeDTO.getName())) {
        department = samlAttributeDTO.getValue();
      } else if (WEBSSO_ATTRIBUTES.COMPANY.getValue().equals(samlAttributeDTO.getName())) {
        company = samlAttributeDTO.getValue();
      }
    }

    if (userId == null || userName == null || firstName == null || lastName == null
      || email == null) {
      return Response.status(Response.Status.BAD_REQUEST).build();
    }

    Set<ValidationError> errors = validationCreate(form);
    if (!errors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }

    /*
     * Set tenant from websso attributes (if department/company attributes exist). If attributes do
     * not exist, get tenant from UserRegistrationForm.
     */
    if (department != null && company != null
      && sDTenantService.getTenantByDepartmentAndDirectorate(department, company) != null) {
      tenant = sDTenantService.getTenantByDepartmentAndDirectorate(department, company);
      attr.add(new UserAttributeDTO(USER_ATTRIBUTES.TENANT.getValue(), tenant.getId()));
    } else {
      tenant = TenantMapper.INSTANCE.toTenantDTO(form.getTenant());
      attr.add(new UserAttributeDTO(USER_ATTRIBUTES.TENANT.getValue(), tenant.getId()));
    }

    // Augment the DTO with additional information.
    userDTO.setId(userId);

    if (form.getIsFirstUser() != null && form.getIsFirstUser()) {
      userDTO.setStatus(USER_STATUS.ENABLED_APPROVED.getValue());
    } else {
      userDTO.setStatus(USER_STATUS.DISABLED.getValue());
    }

    if (tenant != null) {
      userDTO.setUsername(userName + LookupValues.USER_NAME_SPECIAL_CHARACTER + tenant.getName());
    }

    /* Set mandatory websso attributes to user attributes. */
    attr.add(new UserAttributeDTO(USER_ATTRIBUTES.FIRSTNAME.getValue(), firstName));
    attr.add(new UserAttributeDTO(USER_ATTRIBUTES.LASTNAME.getValue(), lastName));
    attr.add(new UserAttributeDTO(USER_ATTRIBUTES.EMAIL.getValue(), email));

    // the secondary departments of the user are taken from the form.
    // Also, the SAML department, if exists and has not been updated by the user, is stored in
    // secondary departments.
    StringBuilder departmentIdCommaSeparatedValue = new StringBuilder();
    int userAttributeCounter = 1;
    if (form.getSecondaryDepartments() != null && !form.getSecondaryDepartments().isEmpty()) {
      int depsCounter = 0;
      for (DepartmentHistoryForm departmentF : form.getSecondaryDepartments()) {
        // We should split departments in blocks of 27 max due to limitation of 1024 characters
        // and create separate attribute for each block
        if (depsCounter <= 26 && departmentF.getDepartmentId() != null) {
          departmentIdCommaSeparatedValue.append(departmentF.getDepartmentId().getId());
          departmentIdCommaSeparatedValue.append(",");
          depsCounter++;
        } else {
          attr.add(
            new UserAttributeDTO(
              USER_ATTRIBUTES.SEC_DEPARTMENTS.getValue() + "_" + userAttributeCounter,
              departmentIdCommaSeparatedValue.substring(0,
                departmentIdCommaSeparatedValue.length() - 1)));
          departmentIdCommaSeparatedValue.setLength(0);
          userAttributeCounter++;
          departmentIdCommaSeparatedValue.append(departmentF.getDepartmentId().getId());
          departmentIdCommaSeparatedValue.append(",");
          depsCounter = 1;
        }
      }
      if (!departmentIdCommaSeparatedValue.toString().isEmpty()) {
        attr.add(
          new UserAttributeDTO(
            USER_ATTRIBUTES.SEC_DEPARTMENTS.getValue() + "_" + userAttributeCounter,
            departmentIdCommaSeparatedValue.substring(0,
              departmentIdCommaSeparatedValue.length() - 1)));
      }
    }

    if (form.getFunction() != null) {
      attr.add(new UserAttributeDTO(USER_ATTRIBUTES.FUNCTION.getValue(), form.getFunction()));
    }
    userDTO.setUserAttributes(attr);
    userDTO.setExternal(true);

    userAdministrationService.createUser(userDTO, form.getUserGroup().getName(),
      form.getIsFirstUser());

    return Response.ok().build();
  }


  /**
   * Update user.
   *
   * @param form the form
   * @return Ok if user is updated successfully
   */
  @PUT
  @Path("/edit")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response edit(@Valid UserForm form) {
    userAdministrationService.userSearchSecurityCheck();
    // Check for optimistic lock errors
    userAdministrationService
      .editUserOptimisticLock(form.getId(), form.getOldGroupId(), form.getVersion(),
        form.getFunctionVersion(),
        form.getSecondaryDepartmentsVersion());
    // Check for validation errors
    Set<ValidationError> errors = validationEdit(form);
    if (!errors.isEmpty()) {
      return Response.status(Status.BAD_REQUEST).entity(errors).build();
    }
    // Proceed with editing user
    String email = userAdministrationService.editUser(UserFormMapper.INSTANCE.toUserDTO(form),
      form.getUserGroup().getName(), form.getGroupChanged(), form.getSecDeptsChanged(),
      form.isUserAdminRight(), form.getUserAdminRightChanged(), form.getRegister());
    // Return JSON with the email
    ProjectForm emailJson = new ProjectForm();
    emailJson.setId(email);
    return Response.ok(emailJson).build();
  }

  /**
   * Retrieve sorted all users that satisfy fields from searchForm.
   *
   * @param form the form
   * @return The list of users
   */
  @POST
  @Path("/search")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response search(UserSearchForm form) {

    userAdministrationService.userSearchSecurityCheck();

    SubmissUserDTO searchDTO = UserSearchFormMapper.INSTANCE.toSubmissUserDTO(form);

    List<SubmissUserDTO> submissUserDTOs =
      userAdministrationService.searchUsers(searchDTO);

    return Response.ok(submissUserDTOs).build();
  }

  /**
   * Find the count of users that satisfy the search criteria.
   *
   * @param form the form
   * @return The count of users
   */
  @POST
  @Path("/count")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public long countUsers(@Valid UserSearchForm form) {
    SubmissUserDTO searchDTO = UserSearchFormMapper.INSTANCE.toSubmissUserDTO(form);
    return userAdministrationService.countUsers(searchDTO);

  }

  /**
   * Finds the Submiss user that is logged in.
   *
   * @return SubmissUserDTO
   */
  @GET
  @Path("/user")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public SubmissUserDTO getUser() {
    return userAdministrationService
      .getUserById(WebSSOHolder.getAttribute(WEBSSO_ATTRIBUTES.USER_ID.getValue())
        .map(SAMLAttributeDTO::getValue).orElse(""));
  }

  /**
   * Loads SAML attributes that are displayed in user registration form.
   *
   * @return UserForm
   */
  @POST
  @Path("/loadSAMLAttributes")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response loadSAMLAttributes() {

    List<SAMLAttributeDTO> samlAttributeDTOList = WebSSOHolder.getAttributes();
    UserForm userForm = new UserForm();
    String department = null;
    String company = null;
    for (SAMLAttributeDTO samlAttributeDTO : samlAttributeDTOList) {
      if (WEBSSO_ATTRIBUTES.USER_ID.getValue().equals(samlAttributeDTO.getName())) {
        userForm.setId(samlAttributeDTO.getValue());
      } else if (WEBSSO_ATTRIBUTES.USERNAME.getValue().equals(samlAttributeDTO.getName())) {
        userForm.setUsername(samlAttributeDTO.getValue());
      } else if (WEBSSO_ATTRIBUTES.FIRST_NAME.getValue().equals(samlAttributeDTO.getName())) {
        userForm.setFirstName(samlAttributeDTO.getValue());
      } else if (WEBSSO_ATTRIBUTES.LAST_NAME.getValue().equals(samlAttributeDTO.getName())) {
        userForm.setLastName(samlAttributeDTO.getValue());
      } else if (WEBSSO_ATTRIBUTES.EMAIL.getValue().equals(samlAttributeDTO.getName())) {
        userForm.setEmail(samlAttributeDTO.getValue());
      } else if (WEBSSO_ATTRIBUTES.DEPARTMENT.getValue().equals(samlAttributeDTO.getName())) {
        department = samlAttributeDTO.getValue();
      } else if (WEBSSO_ATTRIBUTES.COMPANY.getValue().equals(samlAttributeDTO.getName())) {
        company = samlAttributeDTO.getValue();
      }
    }

    if (department != null && company != null) {
      // Initialize secondary departments and set SAML department to secondary departments.
      List<DepartmentHistoryForm> departmentList = new ArrayList<>();
      DepartmentHistoryForm mainDepartment = DepartmentHistoryFormMapper.INSTANCE
        .toDepartmentHistoryForm(
          sDDepartmentService.getDepartmentBySortName(department, company));
      if (mainDepartment != null) {
        departmentList.add(mainDepartment);
        userForm.setSecondaryDepartments(departmentList);
        // Also, set SAML department to main Department in order to check if Tenant is disabled or not.
        userForm.setMainDepartment(mainDepartment);
      }
      userForm.setTenant(TenantMapper.INSTANCE
        .toTenantForm(sDTenantService.getTenantByDepartmentAndDirectorate(department, company)));
    }

    return Response.ok(userForm).build();
  }

  /**
   * Declines a specific user after registration.
   *
   * @param userId the user id
   * @return the response
   */
  @PUT
  @Path("declineUser/{userId}")
  @Produces(MediaType.TEXT_PLAIN)
  public Response declineUser(@PathParam("userId") String userId) {
    return Response.ok(userAdministrationService.declineUser(userId)).build();

  }

  /**
   * Finds the status of the calling user.
   *
   * @return the user status
   */
  @GET
  @Path("status")
  @Produces(MediaType.APPLICATION_JSON)
  public int getUserStatus() {
    return userAdministrationService.getUserStatus().getValue();
  }

  /**
   * Finds the number of users that have registered on the system.
   *
   * @return the all users
   */
  @GET
  @Path("/all")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public long getAllUsers() {
    return userAdministrationService.getAllUsers();
  }

  /**
   * Validation edit.
   *
   * @param form the form
   * @return the sets the
   */
  private Set<ValidationError> validationEdit(UserForm form) {
    Set<ValidationError> errors = new HashSet<>();
    if (form.getUserGroup() == null || form.getTenant() == null
      || form.getSecondaryDepartments() == null || form.getSecondaryDepartments().isEmpty()) {
      errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
        ValidationMessages.USER_MANDATORY_ERROR_MESSAGE));
      if (form.getTenant() == null) {
        errors.add(new ValidationError("tenant",
          ValidationMessages.USER_MANDATORY_ERROR_MESSAGE));
      }
      if (form.getSecondaryDepartments() == null || form.getSecondaryDepartments().isEmpty()) {
        errors.add(new ValidationError("secondaryDepartments",
          ValidationMessages.USER_MANDATORY_ERROR_MESSAGE));
      }
      if (form.getUserGroup() == null) {
        errors.add(new ValidationError("role",
          ValidationMessages.USER_MANDATORY_ERROR_MESSAGE));
      }
    }
    if (form.getUserGroup() != null && form.getUserGroup().getName().equals(Group.ADMIN.getValue())
      && (form.getFunction() == null || StringUtils.isBlank(form.getFunction()))) {
      errors.add(new ValidationError(FUNCTION_FIELD,
        ValidationMessages.USER_MANDATORY_ERROR_MESSAGE));
      errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
        ValidationMessages.USER_MANDATORY_ERROR_MESSAGE));
    }
    if (form.getFunction() != null && form.getFunction().length() > LookupValues.ONE_HUNDRENT) {
      errors.add(new ValidationError(FUNCTION_FIELD,
        ValidationMessages.FUNCTION_MAX_SIZE));
      errors.add(new ValidationError("functionErrorField",
        ValidationMessages.FUNCTION_MAX_SIZE));
    }
    return errors;
  }

  /**
   * Validation create.
   *
   * @param form the form
   * @return the sets the
   */
  private Set<ValidationError> validationCreate(UserRegistrationForm form) {
    Set<ValidationError> errors = new HashSet<>();
    if (form.getUserGroup() == null || form.getTenant() == null
      || form.getSecondaryDepartments() == null || form.getSecondaryDepartments().isEmpty()) {
      errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
        ValidationMessages.USER_MANDATORY_ERROR_MESSAGE));
      if (form.getTenant() == null) {
        errors.add(new ValidationError("tenant", ValidationMessages.USER_MANDATORY_ERROR_MESSAGE));
      }
      if (form.getSecondaryDepartments() == null || form.getSecondaryDepartments().isEmpty()) {
        errors.add(new ValidationError("secondaryDepartments",
          ValidationMessages.USER_MANDATORY_ERROR_MESSAGE));
      }
      if (form.getUserGroup() == null) {
        errors.add(new ValidationError("role",
          ValidationMessages.USER_MANDATORY_ERROR_MESSAGE));
      }
    }
    validateDepartmentAndFunction(form, errors);
    return errors;
  }


  /**
   * Validate department and function.
   *
   * @param form the form
   * @param errors the errors
   */
  private void validateDepartmentAndFunction(UserRegistrationForm form,
    Set<ValidationError> errors) {
    if (form.getUserGroup() != null && form.getUserGroup().getName().equals(Group.ADMIN.getValue())
      && (form.getFunction() == null || StringUtils.isBlank(form.getFunction()))) {
      errors.add(new ValidationError(FUNCTION_FIELD,
        ValidationMessages.USER_MANDATORY_ERROR_MESSAGE));
      errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
        ValidationMessages.USER_MANDATORY_ERROR_MESSAGE));
    }
    if (form.getFunction() != null && form.getFunction().length() > LookupValues.ONE_HUNDRENT) {
      errors.add(new ValidationError(FUNCTION_FIELD,
        ValidationMessages.FUNCTION_MAX_SIZE));
      errors.add(new ValidationError("functionErrorField",
        ValidationMessages.FUNCTION_MAX_SIZE));
    }
  }

  /**
   * Exports users overview report.
   *
   * @param exportForm the export form
   * @return the response
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces("application/octet-stream")
  @Path("/export")
  public Response exportUsers(UserExportForm exportForm) {
    userAdministrationService.userExportSecurityCheck();
    if (exportForm != null) {
      Date startDate = exportForm.getStartDate();
      Date endDate = exportForm.getEndDate();
      String status = exportForm.getStatus();
      List<SubmissUserDTO> users = userAdministrationService
        .getExportedUsers(startDate, endDate, status);
      byte[] content = userAdministrationService.exportUsers(users);
      ResponseBuilder response = Response.ok(content);
      if (content != null && content.length != 0) {
        StringBuilder fileName = new StringBuilder();
        fileName.append(Template.TEMPLATE_NAMES.BENUTZERUBERSICHT.getValue());
        fileName.append(TemplateConstants.XLSX_FILE_EXTENSION);
        response.header("Content-Disposition", "attachment; filename=" + fileName.toString());
      }
      return response.build();
    }
    return null;
  }

  /**
   * Validate form.
   *
   * @param exportForm the export form
   * @return the response
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/validateExportForm")
  public Response validateForm(UserExportForm exportForm) {
    userAdministrationService.userExportSecurityCheck();
    Set<ValidationError> errors = validateExportForm(exportForm);
    if (!errors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    return Response.ok().build();
  }

  /**
   * Method that validates user export form..
   *
   * @param exportForm the export form
   * @return the sets the
   */
  private Set<ValidationError> validateExportForm(UserExportForm exportForm) {
    Set<ValidationError> errors = new HashSet<>();
    if (exportForm != null) {
      Date today = new Date();
      if (exportForm.getStartDate() != null && exportForm.getStartDate().after(today)
        || exportForm.getEndDate() != null && exportForm.getEndDate().after(today)) {
        errors.add(new ValidationError("futureDateErrorField",
          ValidationMessages.DATE_IN_THE_FUTURE_ERROR_MESSAGE));
      }
      if (exportForm.getStartDate() != null && exportForm.getEndDate() != null && exportForm
        .getStartDate().after(exportForm.getEndDate())) {
        errors.add(new ValidationError("startDateAfterEndDateErrorField",
          ValidationMessages.START_DATE_AFTER_END_DATE_ERROR_MESSAGE));
      }
    }
    return errors;
  }

  /**
   * Run security check before loading User Export.
   *
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/loadUserExport")
  public Response loadUserExport() {
    userAdministrationService.userExportSecurityCheck();
    return Response.ok().build();
  }

  /**
   * Run security check before loading User Edit.
   *
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/loadUserSearch")
  public Response loadUserSearch() {
    userAdministrationService.userSearchSecurityCheck();
    return Response.ok().build();
  }
}
