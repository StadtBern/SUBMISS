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

package ch.bern.submiss.services.impl.administration;

import static java.time.temporal.ChronoUnit.DAYS;

import ch.bern.submiss.services.api.administration.EmailService;
import ch.bern.submiss.services.api.administration.SDDepartmentService;
import ch.bern.submiss.services.api.administration.SDTenantService;
import ch.bern.submiss.services.api.administration.SubmissTaskService;
import ch.bern.submiss.services.api.administration.UserAdministrationService;
import ch.bern.submiss.services.api.administration.UserHistoryService;
import ch.bern.submiss.services.api.constants.AuditEvent;
import ch.bern.submiss.services.api.constants.AuditGroupName;
import ch.bern.submiss.services.api.constants.AuditLevel;
import ch.bern.submiss.services.api.constants.SecurityOperation;
import ch.bern.submiss.services.api.constants.TaskTypes;
import ch.bern.submiss.services.api.dto.DepartmentHistoryDTO;
import ch.bern.submiss.services.api.dto.DirectorateHistoryDTO;
import ch.bern.submiss.services.api.dto.SubmissTaskDTO;
import ch.bern.submiss.services.api.dto.SubmissUserDTO;
import ch.bern.submiss.services.api.dto.TenantDTO;
import ch.bern.submiss.services.api.dto.UserHistoryDTO;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.api.util.LookupValues.USER_ATTRIBUTES;
import ch.bern.submiss.services.api.util.LookupValues.USER_STATUS;
import ch.bern.submiss.services.api.util.LookupValues.WEBSSO_ATTRIBUTES;
import ch.bern.submiss.services.api.util.SubmissConverter;
import ch.bern.submiss.services.api.util.ValidationMessages;
import ch.bern.submiss.services.impl.mappers.UserDTOtoSubmissUserDTOMapper;
import com.eurodyn.qlack2.fuse.aaa.api.OperationService;
import com.eurodyn.qlack2.fuse.aaa.api.criteria.UserSearchCriteria;
import com.eurodyn.qlack2.fuse.aaa.api.criteria.UserSearchCriteria.UserSearchCriteriaBuilder;
import com.eurodyn.qlack2.fuse.aaa.api.dto.GroupDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.ResourceDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.UserAttributeDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import com.eurodyn.qlack2.util.sso.dto.SAMLAttributeDTO;
import com.eurodyn.qlack2.util.sso.dto.WebSSOHolder;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.apache.commons.lang.StringUtils;
import org.ops4j.pax.cdi.api.OsgiService;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

/**
 * The Class UserAdministrationServiceImpl.
 */
@Transactional
@OsgiServiceProvider(classes = {UserAdministrationService.class})
@Singleton
public class UserAdministrationServiceImpl extends BaseService implements
  UserAdministrationService {

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER =
    Logger.getLogger(UserAdministrationServiceImpl.class.getName());

  /**
   * The Constant SPLIT_REGEX.
   */
  private static final String SPLIT_REGEX = "\\s*,\\s*";
  /**
   * The Constant DATE_FORMAT.
   */
  private static final String DATE_FORMAT = "dd.MM.yyyy";
  /**
   * The s D tenant service.
   */
  @Inject
  protected SDTenantService sDTenantService;
  /**
   * The s D department service.
   */
  @Inject
  protected SDDepartmentService sDDepartmentService;
  /**
   * The operation service.
   */
  @OsgiService
  @Inject
  protected OperationService operationService;
  /**
   * The task service.
   */
  @Inject
  private SubmissTaskService taskService;
  /**
   * The report service.
   */
  @Inject
  private ReportServiceImpl reportService;
  /**
   * The audit.
   */
  @Inject
  private AuditBean audit;
  @Inject
  private SubmissCacheServiceImpl cacheService;
  /**
   * The user history service.
   */
  @Inject
  private UserHistoryService userHistoryService;
  /**
   * The email service.
   */
  @Inject
  private EmailService emailService;

  /**
   * Create user.
   *
   * @param dto         the dto
   * @param groupName   The name of the group of the user
   * @param isFirstUser The flag indicating whether the user is the first user
   * @return the user id
   */
  @Override
  public String createUser(UserDTO dto, String groupName, Boolean isFirstUser) {

    LOGGER.log(Level.CONFIG,
      "Executing method createUser, Parameters: dto: {0}, groupName: {1}, "
        + "isFirstUser: {2}",
      new Object[]{dto, groupName, isFirstUser});

    String userId = userService.createUser(dto);

    addUserGroupByName(userId, groupName);
    /*
     * add security resources for the first user the rest of the users will get their security
     * resources upon registration
     */
    if (isFirstUser != null && isFirstUser) {
      security.addTenantResources(dto);
      security.addProjectViewResourcesToUser(dto, groupName); // for project view
      security.addUserViewResourcesToUser(dto, groupName); // for user view, need to be added
      // always, since the first user has by
      // default user admin right
      security.addTenantOperationsToUser(dto, groupName);

      UserDTO persistedUser = userService.getUserById(userId);
      DateFormat df = new SimpleDateFormat(DATE_FORMAT);
      Date date = new Timestamp(new Date().getTime());
      String reportDate = df.format(date);
      UserAttributeDTO attrib = new UserAttributeDTO();
      attrib.setName(USER_ATTRIBUTES.REGISTERED_DATE.getValue());
      attrib.setData(reportDate);
      /* Add attribute to user. */
      persistedUser.setAttribute(attrib);

      userService.updateUser(persistedUser, false);

    } else {
      // Create new task when user registers.
      createTask(userId, TaskTypes.REGISTRATION_APPLICATION);
    }

    return userId;
  }

  /**
   * Creates the task.
   *
   * @param userId   the user id
   * @param taskType the task type
   */
  private void createTask(String userId, TaskTypes taskType) {

    LOGGER.log(Level.CONFIG,
      "Executing method createTask, Parameters: userId: {0}, "
        + "taskType: {1}",
      new Object[]{userId, taskType});

    SubmissTaskDTO taskDTO = new SubmissTaskDTO();
    taskDTO.setUserToEdit(userId);
    taskDTO.setDescription(taskType);
    taskDTO.setCreatedBy(userId);
    taskService.createTask(taskDTO);
  }

  /**
   * Edit user.
   *
   * @param dto                   the UserDTO
   * @param groupName             The name of the group of the user
   * @param groupChanged          The flag indicating whether group is changed or not
   * @param secDeptsChanged       The flag indicating whether secondary Departments are changed or
   *                              not
   * @param userAdminRight        The flag indicating whether the user has the right to edit users
   *                              or not
   * @param userAdminRightChanged The flag indicating whether userAdminRight is changed or not
   * @param register              The flag indicating whether to register a user
   * @return the UserDTO id
   */
  @Override
  public String editUser(UserDTO dto, String groupName, Boolean groupChanged,
    Boolean secDeptsChanged, Boolean userAdminRight, Boolean userAdminRightChanged,
    Boolean register) {

    LOGGER.log(Level.CONFIG,
      "Executing method editUser, Parameters: dto: {0}, groupName: {1}, groupChanged: {2}, "
        + "secDeptsChanged: {3}, userAdminRight: {4}, userAdminRightChanged: {5}, "
        + "register: {6}",
      new Object[]{dto, groupName, groupChanged, secDeptsChanged, userAdminRight,
        userAdminRightChanged, register});

    // if the user needs to be registered set the registered date as attribute
    if (register != null && register) {
      /*
       * Set the date that the user is registered and convert it to string in order to match with
       * user attributes in DB.
       */
      DateFormat df = new SimpleDateFormat(DATE_FORMAT);
      Date date = new Timestamp(new Date().getTime());
      String reportDate = df.format(date);
      UserAttributeDTO attrib = new UserAttributeDTO();
      attrib.setName(USER_ATTRIBUTES.REGISTERED_DATE.getValue());
      attrib.setData(reportDate);
      /* Add attribute to user. */
      dto.setAttribute(attrib);

      // Delete task when user is registered.
      settleTask(dto.getId());
    }

    // Save date when user is deactivated.
    if (userService.getUserStatus(dto.getId()) != dto.getStatus()
      && dto.getStatus() == USER_STATUS.DISABLED_APPROVED.getValue()) {
      dto.setAttribute(new UserAttributeDTO(USER_ATTRIBUTES.DEACTIVATION_DATE.getValue(),
        SubmissConverter.convertToSwissDate(new Timestamp(new Date().getTime()))));
      audit.createLogAudit(AuditLevel.USER_LEVEL.name(), AuditEvent.DEACTIVATION.name(),
        AuditGroupName.USER.name(), "Deactivate User", getUser().getId(), dto.getId(), null, null,
        LookupValues.INTERNAL_LOG);
    }

    if (userService.getUserStatus(dto.getId()) != dto.getStatus()
      && dto.getStatus() == USER_STATUS.ENABLED_APPROVED.getValue()) {
      audit.createLogAudit(AuditLevel.USER_LEVEL.name(), AuditEvent.ACTIVATION.name(),
        AuditGroupName.USER.name(), "Activate User", getUser().getId(), dto.getId(), null, null,
        LookupValues.INTERNAL_LOG);
    }

    userService.updateUser(dto, false);

    updateUserGroupByName(dto.getId(), groupName);

    // when registering user add security resources, if already registered edit them
    if (register != null && register) {
      security.addTenantResources(dto); // for tenant level view
      security.addProjectViewResourcesToUser(dto, groupName); // for project view
      if (userAdminRight) { // if the user has user management right add resources for user view
        security.addUserViewResourcesToUser(dto, groupName); // for user view
      }
      security.addTenantOperationsToUser(dto, groupName);
    } else {
      security.editProjectViewUserResources(dto, groupName, groupChanged, secDeptsChanged); // for
      // project
      // view

      // if the user's right to edit users has changed, then update the resources for user view
      if (userAdminRightChanged != null && userAdminRightChanged) {
        security.editUserViewUserResources(dto, groupName, userAdminRight);
      }
    }

    // Call the handleUserHistory function to modify the user history (if applicable).
    userHistoryService.handleUserHistory(dto.getId(), getGroupByName(groupName).getId(),
      dto.getStatus());

    if (register != null && register) {
      return emailService.openUserEmailTemplate(Boolean.TRUE, dto.getId());
    }
    return dto.getId();
  }

  /**
   * Add user group by name.
   *
   * @param userId    the user id
   * @param groupName the group name
   */
  @Override
  public void addUserGroupByName(String userId, String groupName) {

    LOGGER.log(Level.CONFIG,
      "Executing method addUserGroupByName, Parameters: userId: {0}, "
        + "groupName: {1}",
      new Object[]{userId, groupName});

    if (!StringUtils.isEmpty(groupName)) {
      GroupDTO group = getGroupByName(groupName);
      if (group != null) {
        addUserGroupById(userId, group.getId());
      }
    }
  }

  /**
   * Update user group by name.
   *
   * @param userId    the user id
   * @param groupName the group name
   */
  @Override
  public void updateUserGroupByName(String userId, String groupName) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateUserGroupByName, Parameters: userId: {0}, "
        + "groupName: {1}",
      new Object[]{userId, groupName});

    // if userId or groupName is empty , do nothing
    if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(groupName)) {
      return;
    }
    // get the groups the user belongs to
    Set<String> groups;
    groups = userGroupService.getUserGroupsIds(userId);

    // check if the new group is already related to this user
    // and update accordingly
    GroupDTO newGroup = getGroupByName(groupName);
    for (String group : groups) {
      if (!group.equalsIgnoreCase(newGroup.getId())) {
        userGroupService.removeUser(userId, group);
        userGroupService.addUser(userId, newGroup.getId());
        break;
      }
    }
  }

  /**
   * Add user group by id.
   *
   * @param userId  the user id
   * @param groupId the group id
   */
  @Override
  public void addUserGroupById(String userId, String groupId) {

    LOGGER.log(Level.CONFIG,
      "Executing method addUserGroupById, Parameters: userId: {0}, "
        + "groupId: {1}",
      new Object[]{userId, groupId});

    userGroupService.addUser(userId, groupId);
  }

  /**
   * Get user group by name.
   *
   * @param groupName the group name
   * @return
   */
  @Override
  public GroupDTO getGroupByName(String groupName) {

    LOGGER.log(Level.CONFIG,
      "Executing method getGroupByName, Parameters: groupName: {0}",
      groupName);

    return userGroupService.getGroupByName(groupName, false);
  }

  /**
   * Count users.
   *
   * @param submissUserDTO the submiss user DTO
   * @return the user count
   */
  @Override
  public long countUsers(SubmissUserDTO submissUserDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method countUsers, Parameters: submissUserDTO: {0}",
      submissUserDTO);

    UserSearchCriteriaBuilder userSearchCriteriaBuilder = createSearchCriteria(submissUserDTO);
    UserSearchCriteria userSearchCriteria;
    userSearchCriteria = userSearchCriteriaBuilder.build();
    long userCount = 0;
    /*
     * if departments exist in the search criteria then we need to get all users and from the search
     * results exclude the ones that do not comply with the search criteria of the department
     */

    if ((submissUserDTO.getSecondaryDepartments() != null
      && !submissUserDTO.getSecondaryDepartments().isEmpty())
      || submissUserDTO.getDirectorates() != null
      && !submissUserDTO.getDirectorates().isEmpty()) {
      List<UserDTO> userDTOList = userService.findUsers(userSearchCriteria);
      List<SubmissUserDTO> submissUserDTOs =
        searchDepartmentAndDirectorate(userDTOList, submissUserDTO);
      userCount = submissUserDTOs.size();

    } else {
      userCount = userService.findUserCount(userSearchCriteria);
    }
    return userCount;
  }

  /**
   * User search.
   *
   * @param submissUserDTO the submiss user DTO
   * @return the list with the users
   */
  @Override
  public List<SubmissUserDTO> searchUsers(SubmissUserDTO submissUserDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method searchUsers, Parameters: submissUserDTO: {0}",
      submissUserDTO);

    UserSearchCriteriaBuilder userSearchCriteriaBuilder = createSearchCriteria(submissUserDTO);
    UserSearchCriteria userSearchCriteria = userSearchCriteriaBuilder.build();
    userSearchCriteria.getAttributeCriteria().setUseLike(true);
    List<UserDTO> userDTOList = userService.findUsers(userSearchCriteria);

    return searchDepartmentAndDirectorate(userDTOList, submissUserDTO);
  }

  /**
   * Search department and directorate.
   *
   * @param userDTOList    the user DTO list
   * @param submissUserDTO the submiss user DTO
   * @return the list
   */
  private List<SubmissUserDTO> searchDepartmentAndDirectorate(List<UserDTO> userDTOList,
    SubmissUserDTO submissUserDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method searchDepartmentAndDirectorate, Parameters:  userDTOList: {0}, "
        + "submissUserDTO: {1}",
      new Object[]{userDTOList, submissUserDTO});

    List<SubmissUserDTO> submissUserDTOs = new ArrayList<>();
    List<String> secDepIdsToSearch = new ArrayList<>();

    Integer status = LookupValues.SEARCH_USER_STATUS.NO_DEPARTMENT_NO_DIR.getValue();
    if (submissUserDTO.getSecondaryDepartments() != null
      && !submissUserDTO.getSecondaryDepartments().isEmpty()) {
      // change the value of initialised flag if departments exist in search criteria
      status = LookupValues.SEARCH_USER_STATUS.DEPARTMENT_NO_DIR.getValue();
      for (DepartmentHistoryDTO secondaryDepartment : submissUserDTO.getSecondaryDepartments()) {
        secDepIdsToSearch.add(secondaryDepartment.getDepartmentId().getId());
      }
    }

    List<String> directorateIdsToSearch = new ArrayList<>();
    if (submissUserDTO.getDirectorates() != null && !submissUserDTO.getDirectorates().isEmpty()) {
      // change the value of initialised flag if directorates exist in search criteria
      if (status.equals(LookupValues.SEARCH_USER_STATUS.DEPARTMENT_NO_DIR.getValue())) {
        status = LookupValues.SEARCH_USER_STATUS.DEPARTMENT_DIR.getValue();
      }
      if (status.equals(LookupValues.SEARCH_USER_STATUS.NO_DEPARTMENT_NO_DIR.getValue())) {
        status = LookupValues.SEARCH_USER_STATUS.NO_DEPARTMENT_DIR.getValue();
      }
      for (DirectorateHistoryDTO directorateHistory : submissUserDTO.getDirectorates()) {
        directorateIdsToSearch.add(directorateHistory.getId());
      }
    }

    // apply search for department and directorate in UserDTO
    for (UserDTO userDTO : userDTOList) {
      // initial value for department and directorate in each user
      boolean departmentFound = false;
      boolean directorateFound = false;
      List<DepartmentHistoryDTO> secondaryDepartments = new ArrayList<>();
      Set<String> userGroup = userGroupService.getUserGroupsIds(userDTO.getId());
      List<GroupDTO> groups = userGroupService.getGroupsByID(userGroup, false);
      TenantDTO tenant = sDTenantService
        .getTenantById(userDTO.getAttribute(USER_ATTRIBUTES.TENANT.getValue()).getData());
      DepartmentHistoryDTO mainDepartment = new DepartmentHistoryDTO();
      if (userDTO.getAttribute(USER_ATTRIBUTES.SAML_DEPARTMENT.getValue()) != null
        && userDTO.getAttribute(USER_ATTRIBUTES.SAML_DEPARTMENT.getValue()).getData() != null) {
        // check if saml department exists in search criteria
        String samlDepartment =
          userDTO.getAttribute(USER_ATTRIBUTES.SAML_DEPARTMENT.getValue()).getData();
        mainDepartment = sDDepartmentService.getDepartmentHistByDepartmentId(samlDepartment);
        if (secDepIdsToSearch.contains(samlDepartment)) {
          // change the value of initialised flag if department exists in user attributes
          departmentFound = true;
        }
        if (directorateIdsToSearch.contains(mainDepartment.getDirectorate().getId())) {
          // change the value of initialised flag if directorate exists in user attributes
          directorateFound = true;
        }
      }
      if (userDTO.getAttribute(USER_ATTRIBUTES.SEC_DEPARTMENTS.getValue()) != null
        && userDTO.getAttribute(USER_ATTRIBUTES.SEC_DEPARTMENTS.getValue()).getData() != null) {
        // check if secondary departments exists in search criteria
        List<String> secondaryDepartmentIds = Arrays.asList(userDTO
          .getAttribute(USER_ATTRIBUTES.SEC_DEPARTMENTS.getValue()).getData().split(SPLIT_REGEX));

        for (String secondaryDepartmentId : secondaryDepartmentIds) {
          secondaryDepartments
            .add(sDDepartmentService.getDepartmentHistByDepartmentId(secondaryDepartmentId));
          if (secDepIdsToSearch.contains(secondaryDepartmentId)) {
            // change the value of initialised flag if department exists in user attributes
            departmentFound = true;
          }
        }
        for (DepartmentHistoryDTO department : secondaryDepartments) {
          if (directorateIdsToSearch.contains(department.getDirectorate().getId())) {
            // change the value of initialised flag if directorate exists in user attributes
            directorateFound = true;
          }
        }
      }
      // departments of the search criteria are found
      if (status.equals(LookupValues.SEARCH_USER_STATUS.DEPARTMENT_NO_DIR.getValue())
        && departmentFound) {
        submissUserDTOs.add(
          addSecurityChecksToUser(userDTO, groups, tenant, mainDepartment, secondaryDepartments));
      }
      // directorates of the search criteria are found
      if (directorateFound
        && status.equals(LookupValues.SEARCH_USER_STATUS.NO_DEPARTMENT_DIR.getValue())) {
        submissUserDTOs.add(
          addSecurityChecksToUser(userDTO, groups, tenant, mainDepartment, secondaryDepartments));
      }
      // either departments or directorates exists in search criteria
      if (!directorateFound && !departmentFound
        && status.equals(LookupValues.SEARCH_USER_STATUS.NO_DEPARTMENT_NO_DIR.getValue())) {
        submissUserDTOs.add(
          addSecurityChecksToUser(userDTO, groups, tenant, mainDepartment, secondaryDepartments));
      }
      // both departments and directorates exists in search criteria
      if (departmentFound && directorateFound
        && status.equals(LookupValues.SEARCH_USER_STATUS.DEPARTMENT_DIR.getValue())) {
        submissUserDTOs.add(
          addSecurityChecksToUser(userDTO, groups, tenant, mainDepartment, secondaryDepartments));
      }
    }
    return submissUserDTOs;
  }

  /**
   * Adds the security checks to user.
   *
   * @param userDTO              the user DTO
   * @param groups               the groups
   * @param tenant               the tenant
   * @param mainDepartment       the main department
   * @param secondaryDepartments the secondary departments
   * @return the submiss user DTO
   */
  private SubmissUserDTO addSecurityChecksToUser(UserDTO userDTO, List<GroupDTO> groups,
    TenantDTO tenant, DepartmentHistoryDTO mainDepartment,
    List<DepartmentHistoryDTO> secondaryDepartments) {

    LOGGER.log(Level.CONFIG,
      "Executing method addSecurityChecksToUser, Parameters: userDTO: {0}, "
        + "groups: {1}, tenant: {2}, mainDepartment: {3}, secondaryDepartments: {4}",
      new Object[]{userDTO, groups, tenant, mainDepartment, secondaryDepartments});

    // Applies security check on whether a user can be edited or not.
    Boolean editable =
      security.canUserBeEdited(getUser().getId(), tenant.getId(), groups.get(0).getName());

    // Finds if the user has user view right, in order to inform the according flag
    Boolean userAdminRight = security.isPermitted(userDTO.getId(),
      SecurityOperation.USER_OPERATION_USER_VIEW.getValue(), null);
    return UserDTOtoSubmissUserDTOMapper.INSTANCE.toSubmissUserDTO(userDTO, groups, tenant,
      mainDepartment, secondaryDepartments, null, editable, userAdminRight);
  }

  /**
   * Find a specific user.
   *
   * @param userSearchCriteria the user search criteria
   * @return true if a specific user is found
   */
  @Override
  public boolean findSpecificUser(UserSearchCriteria userSearchCriteria) {

    LOGGER.log(Level.CONFIG,
      "Executing method findSpecificUser, Parameters: userSearchCriteria: {0}",
      userSearchCriteria);

    return userService.findUserCount(userSearchCriteria) > 0;
  }

  /**
   * Get user by id.
   *
   * @param userId the user id
   * @return the user
   */
  @Override
  public SubmissUserDTO getUserById(String userId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getUserById, Parameters: userId: {0}",
      userId);

    UserDTO userDTO = userService.getUserById(userId);
    SubmissUserDTO submissUserDTO = new SubmissUserDTO();
    if (userDTO != null) {
      Set<String> userGroup = userGroupService.getUserGroupsIds(userDTO.getId());

      List<GroupDTO> groups = null;
      if (!userGroup.isEmpty()) {
        groups = userGroupService.getGroupsByID(userGroup, false);
      }

      // get all permitted operation for the user (operations that belong to the user + to his
      // groups)
      Set<String> permittedOperations =
        operationService.getPermittedOperationsForUser(userDTO.getId(), true);

      List<DepartmentHistoryDTO> secondaryDepartments = new ArrayList<>();
      DepartmentHistoryDTO mainDepartment = null;
      TenantDTO tenant = new TenantDTO();
      if (userDTO.getAttribute(USER_ATTRIBUTES.TENANT.getValue()) != null
        && userDTO.getAttribute(USER_ATTRIBUTES.TENANT.getValue()).getData() != null) {
        tenant = sDTenantService
          .getTenantById(userDTO.getAttribute(USER_ATTRIBUTES.TENANT.getValue()).getData());
      }

      if (userDTO.getAttribute(USER_ATTRIBUTES.SAML_DEPARTMENT.getValue()) != null
        && userDTO.getAttribute(USER_ATTRIBUTES.SAML_DEPARTMENT.getValue()).getData() != null) {
        mainDepartment = sDDepartmentService.getDepartmentHistByDepartmentId(
          userDTO.getAttribute(USER_ATTRIBUTES.SAML_DEPARTMENT.getValue()).getData());
      }
      if (userDTO.getAttribute(USER_ATTRIBUTES.SEC_DEPARTMENTS.getValue()) != null
        && userDTO.getAttribute(USER_ATTRIBUTES.SEC_DEPARTMENTS.getValue()).getData() != null) {
        List<String> secondaryDepartmentIds = Arrays.asList(userDTO
          .getAttribute(USER_ATTRIBUTES.SEC_DEPARTMENTS.getValue()).getData().split(SPLIT_REGEX));

        for (String secondaryDepartmentId : secondaryDepartmentIds) {
          secondaryDepartments
            .add(sDDepartmentService.getDepartmentHistByDepartmentId(secondaryDepartmentId));
        }
      }

      // Update Firstname, Lastname, E-Mail if are different in DB from SAML token.
      checkUserAttributes(userDTO);

      // Update user login date.
      updateUserLoginDate(userDTO);

      // Finds if the user has user view right, in order to inform the according flag
      Boolean userAdminRight = security.isPermitted(userDTO.getId(),
        SecurityOperation.USER_OPERATION_USER_VIEW.getValue(), null);

      submissUserDTO = UserDTOtoSubmissUserDTOMapper.INSTANCE.toSubmissUserDTO(userDTO, groups,
        tenant, mainDepartment, secondaryDepartments, permittedOperations, false, userAdminRight);
    }
    return submissUserDTO;
  }

  /**
   * Update user login date.
   *
   * @param userDTO the user DTO
   */
  private void updateUserLoginDate(UserDTO userDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateUserLoginDate, Parameters: userDTO: {0}",
      userDTO);

    userDTO.setAttribute(new UserAttributeDTO(USER_ATTRIBUTES.LOGIN_DATE.getValue(),
      SubmissConverter.convertToSwissDate(new Timestamp(new Date().getTime()))));
    userService.updateAttribute(userDTO.getAttribute(USER_ATTRIBUTES.LOGIN_DATE.getValue()), true);
  }

  /**
   * Check user attributes.
   *
   * @param userDTO the user DTO
   */
  private void checkUserAttributes(UserDTO userDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method checkUserAttributes, Parameters: userDTO: {0}",
      userDTO);

    List<UserAttributeDTO> attr = new ArrayList<>();
    attr.add(new UserAttributeDTO(USER_ATTRIBUTES.FIRSTNAME.getValue(),
      WebSSOHolder.getAttribute(WEBSSO_ATTRIBUTES.FIRST_NAME.getValue())
        .map(SAMLAttributeDTO::getValue).orElse("")));
    attr.add(new UserAttributeDTO(USER_ATTRIBUTES.LASTNAME.getValue(),
      WebSSOHolder.getAttribute(WEBSSO_ATTRIBUTES.LAST_NAME.getValue())
        .map(SAMLAttributeDTO::getValue).orElse("")));
    attr.add(new UserAttributeDTO(USER_ATTRIBUTES.EMAIL.getValue(),
      WebSSOHolder.getAttribute(WEBSSO_ATTRIBUTES.EMAIL.getValue())
        .map(SAMLAttributeDTO::getValue).orElse("")));

    UserSearchCriteria userSearchCriteria = UserSearchCriteria.UserSearchCriteriaBuilder
      .createCriteria().withUsernameLike(userDTO.getUsername())
      .withAttributes(UserSearchCriteria.UserSearchCriteriaBuilder.and(attr)).build();
    if (!findSpecificUser(userSearchCriteria) && userDTO.getUsername() != null) {
      changeUserAttributes(userDTO.getUsername(), attr);
    }
  }

  /**
   * Change user's attributes.
   *
   * @param userName the user name
   * @param attr     the attr
   */
  @Override
  public void changeUserAttributes(String userName, List<UserAttributeDTO> attr) {

    LOGGER.log(Level.CONFIG,
      "Executing method changeUserAttributes, Parameters: userName: {0}, "
        + "attr: {1}",
      new Object[]{userName, attr});

    UserDTO user = userService.getUserByName(userName);
    if (user != null) {
      for (UserAttributeDTO u : attr) {
        u.setUserId(user.getId());
        userService.updateAttribute(u, true);
      }
    }

  }

  /**
   * Decline a user.
   *
   * @param userId the user id
   * @return the email
   */
  @Override
  public String declineUser(String userId) {

    LOGGER.log(Level.CONFIG,
      "Executing method declineUser, Parameters: userId: {0}",
      userId);
    // Delete task when user is not registered.
    settleTask(userId);
    /** When we decline register user we delete the user from DB (UC 192) */
    String email = emailService.openUserEmailTemplate(Boolean.FALSE, userId);
    userService.deleteUser(userId);
    return email;
  }

  /**
   * Settles the task.
   *
   * @param userId the new tle task
   */
  private void settleTask(String userId) {

    LOGGER.log(Level.CONFIG,
      "Executing method settleTask, Parameters: userId: {0}",
      userId);

    taskService.settleTask(null, null, TaskTypes.REGISTRATION_APPLICATION, userId, null);
  }

  /**
   * Get the user status.
   *
   * @return the user status
   */
  @Override
  public USER_STATUS getUserStatus() {

    cacheService.init();

    LOGGER.log(Level.CONFIG, "Executing method getUserStatus");
    // Lookup the user in the database.
    final UserDTO userDTO = getUser();

    // If the user was found return the status as in the database, otherwise return a default
    // status.
    return userDTO == null ? USER_STATUS.DOES_NOT_EXIST
      : USER_STATUS.fromValue(userDTO.getStatus());
  }

  /**
   * Get all users.
   *
   * @return all users count
   */
  @Override
  public long getAllUsers() {

    LOGGER.log(Level.CONFIG, "Executing method getAllUsers");

    UserSearchCriteria userSearchCriteria;
    userSearchCriteria = UserSearchCriteria.UserSearchCriteriaBuilder.createCriteria().build();
    return userService.findUserCount(userSearchCriteria);

  }

  /**
   * Get permitted groups.
   *
   * @return the list with the permitted groups
   */
  @Override
  public List<GroupDTO> getPermittedGroups() {

    LOGGER.log(Level.CONFIG, "Executing method getPermittedGroups");

    List<GroupDTO> permittedGroups = new ArrayList<>();
    List<GroupDTO> userGroups = userGroupService.listGroups();
    // get denied roles
    Set<ResourceDTO> deniedResources = security.getResources(getUser().getId(),
      SecurityOperation.RESOURCE_ROLE_TYPE_USER.getValue(), false, false);
    List<String> deniedRoles = new ArrayList<>();
    if (deniedResources != null) {
      for (ResourceDTO resource : deniedResources) {
        deniedRoles.add(resource.getObjectID());
      }
      // remove groups that are stored as denied
      for (GroupDTO userGroup : userGroups) {
        if (!deniedRoles.contains(userGroup.getName())) {
          permittedGroups.add(userGroup);
        }
      }
    }
    return permittedGroups;
  }

  /**
   * Creates the search criteria.
   *
   * @param submissUserDTO the submiss user DTO
   * @return the user search criteria builder
   */
  private UserSearchCriteriaBuilder createSearchCriteria(SubmissUserDTO submissUserDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method createSearchCriteria, Parameters: submissUserDTO: {0}",
      submissUserDTO);

    List<UserAttributeDTO> attr = new ArrayList<>();
    attr.add(new UserAttributeDTO(USER_ATTRIBUTES.EMAIL.getValue(), submissUserDTO.getEmail()));
    attr.add(
      new UserAttributeDTO(USER_ATTRIBUTES.FIRSTNAME.getValue(), submissUserDTO.getFirstName()));
    attr.add(
      new UserAttributeDTO(USER_ATTRIBUTES.LASTNAME.getValue(), submissUserDTO.getLastName()));

    List<Byte> userStatus = new ArrayList<>();
    if (submissUserDTO.getRegistered() == null && submissUserDTO.getActive() == null) {
      userStatus = Arrays.asList(USER_STATUS.DISABLED.getValue(),
        USER_STATUS.DISABLED_APPROVED.getValue(), USER_STATUS.ENABLED_APPROVED.getValue());
    } else if ("0".equals(submissUserDTO.getRegistered())
      && "0".equals(submissUserDTO.getActive())) {
      userStatus = Arrays.asList(USER_STATUS.DISABLED.getValue());
    } else if ("0".equals(submissUserDTO.getRegistered())
      && "1".equals(submissUserDTO.getActive())) {
      userStatus = Arrays.asList(USER_STATUS.DOES_NOT_EXIST.getValue());
    } else if ("1".equals(submissUserDTO.getRegistered())
      && "0".equals(submissUserDTO.getActive())) {
      userStatus = Arrays.asList(USER_STATUS.DISABLED_APPROVED.getValue());
    } else if ("1".equals(submissUserDTO.getRegistered())
      && "1".equals(submissUserDTO.getActive())) {
      userStatus = Arrays.asList(USER_STATUS.ENABLED_APPROVED.getValue());
    } else if ("0".equals(submissUserDTO.getRegistered())) {
      userStatus = Arrays.asList(USER_STATUS.DISABLED.getValue());
    } else if ("1".equals(submissUserDTO.getRegistered())) {
      userStatus = Arrays.asList(USER_STATUS.ENABLED_APPROVED.getValue(),
        USER_STATUS.DISABLED_APPROVED.getValue());
    } else if ("0".equals(submissUserDTO.getActive())) {
      userStatus =
        Arrays.asList(USER_STATUS.DISABLED.getValue(), USER_STATUS.DISABLED_APPROVED.getValue());
    } else if ("1".equals(submissUserDTO.getActive())) {
      userStatus = Arrays.asList(USER_STATUS.ENABLED_APPROVED.getValue());
    }
    if (submissUserDTO.getTenant() != null) {
      attr.add(new UserAttributeDTO(USER_ATTRIBUTES.TENANT.getValue(),
        submissUserDTO.getTenant().getId()));
    }
    List<String> userGroups = new ArrayList<>();
    UserSearchCriteriaBuilder userSearchCriteriaBuilder;

    userSearchCriteriaBuilder = UserSearchCriteriaBuilder.createCriteria().withStatusIn(userStatus)
      .withAttributes(UserSearchCriteria.UserSearchCriteriaBuilder.and(attr));

    if (submissUserDTO.getUserGroup() != null) {
      userGroups.add(submissUserDTO.getUserGroup().getId());
      userSearchCriteriaBuilder.withGroupIdIn(userGroups);
    }
    return userSearchCriteriaBuilder;
  }

  /**
   * Get usres by tenant and role.
   *
   * @param tenantId the tenant id
   * @param role     the role
   * @return the list with the users
   */
  @Override
  public List<UserDTO> getUsersByTenantAndRole(String tenantId, String role) {

    LOGGER.log(Level.CONFIG,
      "Executing method getUsersByTenantAndRole, Parameters: tenantId: {0}, "
        + "role: {1}",
      new Object[]{tenantId, role});

    UserSearchCriteriaBuilder userSearchCriteriaBuilder;
    List<UserAttributeDTO> attr = new ArrayList<>();
    List<String> userGroups = new ArrayList<>();

    attr.add(new UserAttributeDTO(USER_ATTRIBUTES.TENANT.getValue(), tenantId));
    userSearchCriteriaBuilder = UserSearchCriteriaBuilder.createCriteria()
      .withAttributes(UserSearchCriteria.UserSearchCriteriaBuilder.and(attr));

    // filter by role
    if (!StringUtils.isBlank(role)) {
      userGroups.add(getGroupByName(role).getId());
      userSearchCriteriaBuilder.withGroupIdIn(userGroups);
    }

    UserSearchCriteria userSearchCriteria = userSearchCriteriaBuilder.build();
    userSearchCriteria.getAttributeCriteria().setUseLike(true);
    return userService.findUsers(userSearchCriteria);
  }

  /**
   * Get exported users.
   *
   * @param startDate the start date
   * @param endDate   the end date
   * @param status    the status
   * @return the list with the exported users
   */
  @Override
  public List<SubmissUserDTO> getExportedUsers(Date startDate, Date endDate, String status) {

    LOGGER.log(Level.CONFIG,
      "Executing method getExportedUsers, Parameters: startDate: {0}, "
        + "endDate: {1}, status: {2}",
      new Object[]{startDate, endDate, status});

    List<SubmissUserDTO> users = null;

    SubmissUserDTO submissUserDTO = new SubmissUserDTO();
    submissUserDTO.setActive(status);
    // Fetch users according to given status (active, deactivated or both).
    users = searchUsers(submissUserDTO);

    // if the start date is null the default date is 01/01/2016
    // if the end date is null the default date is the current date
    if (startDate == null) {
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.YEAR, 2016);
      cal.set(Calendar.MONTH, 0);
      cal.set(Calendar.DAY_OF_MONTH, 1);
      startDate = cal.getTime();
    }
    if (endDate == null) {
      endDate = new Date();
    } else {
      // setting hours-minutes-seconds till date end
      // in order to include all records from that day
      Calendar cal = Calendar.getInstance();
      cal.setTime(endDate);
      cal.set(Calendar.HOUR, 23);
      cal.set(Calendar.MINUTE, 59);
      cal.set(Calendar.SECOND, 59);
      endDate = cal.getTime();
    }

    if (users != null && !users.isEmpty()) {
      // Get the user entries whose active period overlaps with the given start and end dates.
      users = getSearchedRangeUsers(users, startDate, endDate);
    }
    return users;
  }

  /**
   * Gets the user entries whose active period overlaps with the given start and end dates.
   *
   * @param users     the users
   * @param startDate the start date
   * @param endDate   the end date
   * @return the user entries
   */
  private List<SubmissUserDTO> getSearchedRangeUsers(List<SubmissUserDTO> users, Date startDate,
    Date endDate) {

    LOGGER.log(Level.CONFIG,
      "Executing method getSearchedRangeUsers, Parameters: users: {0}, "
        + "startDate: {1}, endDate: {2}",
      new Object[]{users, startDate, endDate});

    // Get the current date.
    Timestamp currentDate = new Timestamp(System.currentTimeMillis());

    // Get all the user ids.
    List<String> userIds = new ArrayList<>();
    for (SubmissUserDTO user : users) {
      userIds.add(user.getId());
    }
    // Get the user history entries by their user ids.
    List<UserHistoryDTO> userHistoryDTOs =
      userHistoryService.getUserHistoryEntriesByUserIds(userIds);
    List<SubmissUserDTO> rangeDateUsers = new ArrayList<>();

    for (SubmissUserDTO user : users) {
      List<UserHistoryDTO> userHistoryDTOsPerUser = new ArrayList<>();
      for (UserHistoryDTO userHistoryDTO : userHistoryDTOs) {
        // Check if the user history entry is connected to the current user
        // and the user history entry dates overlap with the given dates.
        if (userHistoryDTO.getUserId().equals(user.getId())
          && ((userHistoryDTO.getFromDate().before(startDate)
          && ((userHistoryDTO.getToDate() == null && currentDate.after(endDate))
          || (userHistoryDTO.getToDate() != null && userHistoryDTO.getToDate().after(endDate))))
          || (userHistoryDTO.getFromDate().before(startDate) && ((userHistoryDTO.getToDate() == null
          && currentDate.before(endDate) && currentDate.after(startDate))
          || (userHistoryDTO.getToDate() != null && userHistoryDTO.getToDate().before(endDate)
          && userHistoryDTO.getToDate().after(startDate))))
          || ((userHistoryDTO.getFromDate().after(startDate)
          && userHistoryDTO.getFromDate().before(endDate))
          && ((userHistoryDTO.getToDate() == null && currentDate.after(endDate))
          || (userHistoryDTO.getToDate() != null
          && userHistoryDTO.getToDate().after(endDate))))
          || ((userHistoryDTO.getFromDate().after(startDate)
          && userHistoryDTO.getFromDate().before(endDate))
          && ((userHistoryDTO.getToDate() == null && currentDate.before(endDate)
          && currentDate.after(startDate))
          || (userHistoryDTO.getToDate() != null
          && userHistoryDTO.getToDate().before(endDate)
          && userHistoryDTO.getToDate().after(startDate)))))) {
          userHistoryDTOsPerUser.add(userHistoryDTO);
        }
      }
      if (!userHistoryDTOsPerUser.isEmpty()) {
        // For every user entry, create a new user instance with the required history values.
        rangeDateUsers
          .addAll(createRangeDateUser(startDate, endDate, currentDate, userHistoryDTOsPerUser, user));
      }
    }
    return rangeDateUsers;
  }

  /**
   * Creates the user with the required history values.
   *
   * @param startDate      the start date
   * @param endDate        the end date
   * @param currentDate    the current date
   * @param userHistoryDTOs the user history DTO list
   * @param user           the user
   * @return the user
   */
  private List<SubmissUserDTO> createRangeDateUser(Date startDate, Date endDate, Timestamp currentDate,
    List<UserHistoryDTO> userHistoryDTOs, SubmissUserDTO user) {

    LOGGER.log(Level.CONFIG,
      "Executing method createRangeDateUser, Parameters: startDate: {0}, "
        + "endDate: {1}, currentDate: {2}, userHistoryDTO: {3}, user: {4}",
      new Object[]{startDate, endDate, currentDate, userHistoryDTOs, user});

    List<SubmissUserDTO> rangeDateUserList = new ArrayList<>();

    for(UserHistoryDTO userHistoryDTO : userHistoryDTOs){
      // Create default "range date" user.
      SubmissUserDTO rangeDateUser = initializeUserValues(user);
      // Get the group from the current history entry and assign it to the user.
      rangeDateUser.setUserGroup(userGroupService.getGroupByID(userHistoryDTO.getGroupId(), false));
      // Get the user start (registered) and end (deactivation) dates from the history entry.
      rangeDateUser.setRegisteredDate(extractDateWithoutTime(userHistoryDTO.getFromDate()));
      if (userHistoryDTO.getToDate() != null) {
        rangeDateUser.setDeactivationDate(extractDateWithoutTime(userHistoryDTO.getToDate()));
      }
      // Get the start calculation date.
      Timestamp startCalculationDate;
      if (userHistoryDTO.getFromDate().before(startDate)) {
        startCalculationDate = new Timestamp(startDate.getTime());
      } else {
        startCalculationDate = userHistoryDTO.getFromDate();
      }
      // Get the end calculation date.
      Timestamp endCalculationDate;
      if ((userHistoryDTO.getToDate() == null && currentDate.after(endDate))
        || (userHistoryDTO.getToDate() != null && userHistoryDTO.getToDate().after(endDate))) {
        endCalculationDate = new Timestamp(endDate.getTime());
      } else {
        if (userHistoryDTO.getToDate() == null) {
          endCalculationDate = currentDate;
        } else {
          endCalculationDate = userHistoryDTO.getToDate();
        }
      }
      // Call function to calculate registered (active) days.
      rangeDateUser
        .setRegisteredDays(calculateUserRegisteredDays(startCalculationDate, endCalculationDate));
      rangeDateUserList.add(rangeDateUser);
    }
    return mergeRangeDatesForUser(rangeDateUserList);
  }

  /**
   * Merges all history entries per role for each user.
   *
   * @param rangeDateUserList the rangeDateUser DTO list
   * @return the mergedRangeDateUser
   */
  private List<SubmissUserDTO> mergeRangeDatesForUser(List<SubmissUserDTO> rangeDateUserList) {
    List<SubmissUserDTO> mergedRangeDateUserList = new ArrayList<>();
    List<SubmissUserDTO> adminUserList = new ArrayList<>();
    List<SubmissUserDTO> plUserList = new ArrayList<>();
    List<SubmissUserDTO> sbUserList = new ArrayList<>();
    List<SubmissUserDTO> dirUserList = new ArrayList<>();

    // Split the rangeDateUserList into separate Lists depending on the user group.
    for(SubmissUserDTO userDTO : rangeDateUserList){
      if(userDTO.getUserGroup().getDescription().equals("Administration")){
        adminUserList.add(userDTO);
      }else if(userDTO.getUserGroup().getDescription().equals("Projektleitung")){
        plUserList.add(userDTO);
      }else if(userDTO.getUserGroup().getDescription().equals("Sachbearbeitung")){
        sbUserList.add(userDTO);
      }else if(userDTO.getUserGroup().getDescription().equals("Direktion")){
        dirUserList.add(userDTO);
      }
    }
    // Merge all history entries of user and sum Registered days per UserGroup
    if(!adminUserList.isEmpty()){
      mergedRangeDateUserList.add(mergeRangeDatesByUserGroup(adminUserList));
    }
    if(!plUserList.isEmpty()){
      mergedRangeDateUserList.add(mergeRangeDatesByUserGroup(plUserList));
    }
    if(!sbUserList.isEmpty()){
      mergedRangeDateUserList.add(mergeRangeDatesByUserGroup(sbUserList));
    }
    if(!dirUserList.isEmpty()){
      mergedRangeDateUserList.add(mergeRangeDatesByUserGroup(dirUserList));
    }
    return mergedRangeDateUserList;
  }

  private SubmissUserDTO mergeRangeDatesByUserGroup(List<SubmissUserDTO> rangeDateUserList) {
    // Sort the list by Registered Date in asc order after converting date String
    // into Date.
    SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
    rangeDateUserList.sort((o1,o2) -> {
      try {
        return df.parse(o1.getRegisteredDate()).compareTo(df.parse(o2.getRegisteredDate()));
      } catch (ParseException e) {
        e.printStackTrace();
      }
      return 0;
    });
    // The SubmissUserDTO which holds the merged history entries
    SubmissUserDTO mergedRangeDateUser = rangeDateUserList.get(0);
    // The first entry in the list is the one we'll use to compare with the next one
    SubmissUserDTO rangeDateUserToCompare = rangeDateUserList.get(0);
    for (int i = 1; i < rangeDateUserList.size(); i++) {
      // Check the Deactivation Date of previous entry with
      // the Registered Date of the current one in list.
      if (rangeDateUserList.get(i).getRegisteredDate()
        .equals(rangeDateUserToCompare.getDeactivationDate())) {
        // If Registered Date and Deactivation Date of the current one in list are the same,
        // add to the sum of Registered Days of mergedRangeDateUser but subtract one day
        // because it's already calculated in previous entry. Finally set Deactivation Date
        // in mergedRangeDateUser to complete the merge of these entries.
        if (!rangeDateUserList.get(i).getRegisteredDate()
          .equals(rangeDateUserList.get(i).getDeactivationDate())) {
          mergedRangeDateUser.setDeactivationDate(rangeDateUserList.get(i).getDeactivationDate());
          mergedRangeDateUser.setRegisteredDays(
            mergedRangeDateUser.getRegisteredDays() + rangeDateUserList.get(i).getRegisteredDays()
              - 1);
        } else if (rangeDateUserList.get(i).getDeactivationDate() == null) {
          mergedRangeDateUser.setDeactivationDate(null);
        }
      } else {
        // If Registered Date and Deactivation Date of compared entries are not the same we
        // continue with the merge of Registered Days and set the new Deactivation Date.
        mergedRangeDateUser.setDeactivationDate(rangeDateUserList.get(i).getDeactivationDate());
        mergedRangeDateUser.setRegisteredDays(
          mergedRangeDateUser.getRegisteredDays() + rangeDateUserList.get(i).getRegisteredDays());
      }
      // Current entry will be used for next iteration.
      rangeDateUserToCompare = rangeDateUserList.get(i);
    }
    return mergedRangeDateUser;
  }

  /**
   * Initializes the "range date" user values.
   *
   * @param user the user
   * @return the "range date" user with the default user values
   */
  private SubmissUserDTO initializeUserValues(SubmissUserDTO user) {

    LOGGER.log(Level.CONFIG,
      "Executing method initializeUserValues, Parameters: user: {0}",
      user);

    SubmissUserDTO rangeDateUser = new SubmissUserDTO();
    rangeDateUser.setDirectorates(user.getDirectorates());
    rangeDateUser.setDirectorateShortNames(user.getDirectorateShortNames());
    rangeDateUser.setDirectoratesStr(user.getDirectoratesStr());
    rangeDateUser.setTenant(user.getTenant());
    rangeDateUser.setTenantName(user.getTenantName());
    rangeDateUser.setMainDepartment(user.getMainDepartment());
    rangeDateUser.setSecondaryDepartments(user.getSecondaryDepartments());
    rangeDateUser.setFirstName(user.getFirstName());
    rangeDateUser.setLastName(user.getLastName());
    return rangeDateUser;
  }

  /**
   * Calculates the user registered (active) days.
   *
   * @param startCalculationDate the start calculation date
   * @param endCalculationDate   the end calculation date
   * @return the user registered days
   */
  private int calculateUserRegisteredDays(Timestamp startCalculationDate,
    Timestamp endCalculationDate) {

    LOGGER.log(Level.CONFIG,
      "Executing method calculateUserRegisteredDays, Parameters: "
        + "startCalculationDate: {0}, endCalculationDate: {1}",
      new Object[]{startCalculationDate, endCalculationDate});

    // The only way to ensure a proper calculation of days difference
    // is to manipulate the time part of the start & end Timestamps
    Calendar calStart = Calendar.getInstance();
    calStart.setTime(startCalculationDate);     // set calStart to startCalculationDate
    calStart.set(Calendar.HOUR_OF_DAY, 0);
    calStart.set(Calendar.MINUTE, 0);
    calStart.set(Calendar.SECOND, 0);
    calStart.set(Calendar.MILLISECOND, 0);

    Calendar calEnd = Calendar.getInstance();
    calEnd.setTime(endCalculationDate);         // set calEnd to endCalculationDate
    calEnd.set(Calendar.HOUR_OF_DAY, 23);
    calEnd.set(Calendar.MINUTE, 59);
    calEnd.set(Calendar.SECOND, 59);
    calEnd.set(Calendar.MILLISECOND, 999);

    // Calculate the difference in milliseconds and convert it into days.
    return (int) DAYS.between(calStart.getTime().toInstant(), calEnd.getTime().toInstant()) + 1;
  }

  /**
   * Extracts the date from a timestamp.
   *
   * @param timestamp the timestamp
   * @return the date as a String
   */
  private String extractDateWithoutTime(Timestamp timestamp) {
    SimpleDateFormat formatWithoutTime = new SimpleDateFormat(DATE_FORMAT);
    return formatWithoutTime.format(timestamp);
  }

  /**
   * Export users.
   *
   * @param users the users
   * @return the byte[]
   */
  @Override
  public byte[] exportUsers(List<SubmissUserDTO> users) {

    LOGGER.log(Level.CONFIG,
      "Executing method exportUsers, Parameters: users: {0}",
      users);

    try {
      Map<String, Object> parameters = new HashMap<>();

      // subReport 1 All active users
      SubmissUserDTO submissUser = new SubmissUserDTO();
      submissUser.setActive("1");
      List<SubmissUserDTO> allActiveUsers = searchUsers(submissUser);

      // Get Template and complile SubReport 1
      InputStream inputStreamSubReport1 =
        reportService.getReportTemplate("RegisteredUsersTemplate");
      JasperDesign jasperDesignSubReport1 = JRXmlLoader.load(inputStreamSubReport1);
      JasperReport jasperReportSubReport1 =
        JasperCompileManager.compileReport(jasperDesignSubReport1);

      // Get Template and complile SubReport 2
      InputStream inputStreamSubReport2 = reportService.getReportTemplate("ActiveUsersTemplate");
      JasperDesign jasperDesignSubReport2 = JRXmlLoader.load(inputStreamSubReport2);
      JasperReport jasperReportSubReport2 =
        JasperCompileManager.compileReport(jasperDesignSubReport2);

      // Get Template and complile MainReport
      InputStream inputStream = reportService.getReportTemplate("UsersExportTemplate");
      JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
      JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

      /* Setting the parameters of report */
      parameters.put("SUBREPORT_1", jasperReportSubReport1);
      parameters.put("SUBREPORT_2", jasperReportSubReport2);
      parameters.put("SUBREPORT_DATA_1", new JRBeanCollectionDataSource(users));
      parameters.put("SUBREPORT_DATA_2", new JRBeanCollectionDataSource(allActiveUsers));

      /*
       * Putting some Dummy Data for Main Report, if you not pass this dummy data then result will
       * be blank sheet.
       */
      List<String> mainReportsList = new ArrayList<>();
      mainReportsList.add("MainReport");

      JRBeanCollectionDataSource beanColDataSource =
        new JRBeanCollectionDataSource(mainReportsList);
      JasperPrint jasperPrint =
        JasperFillManager.fillReport(jasperReport, parameters, beanColDataSource);

      ByteArrayOutputStream outputByteArray = new ByteArrayOutputStream();

      JRXlsxExporter exporter = new JRXlsxExporter();
      SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
      configuration.setDetectCellType(true);
      configuration.setPrintPageTopMargin(34);
      configuration.setPrintPageLeftMargin(144);
      configuration.setPrintPageBottomMargin(18);
      configuration.setFitWidth(1);
      configuration.setFitHeight(0);
      configuration.setPageScale(70);
      configuration.setSheetNames(new String[]{"Verrechnung", "Aktueller Stand"});
      exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
      exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputByteArray));
      exporter.setConfiguration(configuration);
      exporter.exportReport();
      return outputByteArray.toByteArray();

    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }
    return new byte[0];
  }

  /**
   * Users deactivation.
   */
  @Override
  public void automaticallyDeactivateUsers() {

    LOGGER.log(Level.CONFIG, "Executing method automaticallyDeactivateUsers");

    DateFormat format = new SimpleDateFormat(DATE_FORMAT);
    UserSearchCriteria userSearchCriteria;
    userSearchCriteria = UserSearchCriteria.UserSearchCriteriaBuilder.createCriteria().build();
    List<UserDTO> userDTOs = userService.findUsers(userSearchCriteria);
    for (UserDTO userDTO : userDTOs) {
      try {
        // Deactivate user if last login was 6 months ago.
        if ((userDTO.getAttribute(USER_ATTRIBUTES.LOGIN_DATE.getValue()) != null
          && userDTO.getAttribute(USER_ATTRIBUTES.LOGIN_DATE.getValue()).getData() != null)
          && format.parse(userDTO.getAttribute(USER_ATTRIBUTES.LOGIN_DATE.getValue()).getData())
          .toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
          .isBefore(LocalDate.now().minusMonths(6))) {
          // Set user status to inactive.
          userDTO.setStatus(USER_STATUS.DISABLED_APPROVED.getValue());
          // Update user deactivation date.
          userDTO.setAttribute(new UserAttributeDTO(USER_ATTRIBUTES.DEACTIVATION_DATE.getValue(),
            SubmissConverter.convertToSwissDate(new Timestamp(new Date().getTime()))));
          userService.updateUser(userDTO, false);
        }
      } catch (ParseException e) {
        LOGGER.log(Level.SEVERE, e.getMessage());
      }
    }
  }

  /**
   * Get tenant.
   *
   * @return the tenant DTO
   */
  @Override
  public TenantDTO getTenant() {

    LOGGER.log(Level.CONFIG, "Executing method getTenant");

    return getUserById(getUser().getId()).getTenant();
  }

  /**
   * Get specific user.
   *
   * @param userId the user id
   * @return the user
   */
  @Override
  public SubmissUserDTO getSpecificUser(String userId) {

    LOGGER.log(Level.CONFIG, "Executing method getSpecificUser, Parameters: userId: {0}",
      userId);

    UserDTO userDTO = userService.getUserById(userId);
    SubmissUserDTO submissUserDTO = new SubmissUserDTO();
    if (userDTO != null) {
      Set<String> userGroup = userGroupService.getUserGroupsIds(userDTO.getId());

      List<GroupDTO> groups = null;
      if (!userGroup.isEmpty()) {
        groups = userGroupService.getGroupsByID(userGroup, false);
      }

      List<DepartmentHistoryDTO> secondaryDepartments = new ArrayList<>();
      DepartmentHistoryDTO mainDepartment = null;
      TenantDTO tenant = new TenantDTO();
      if (userDTO.getAttribute(USER_ATTRIBUTES.TENANT.getValue()) != null
        && userDTO.getAttribute(USER_ATTRIBUTES.TENANT.getValue()).getData() != null) {
        tenant = sDTenantService
          .getTenantById(userDTO.getAttribute(USER_ATTRIBUTES.TENANT.getValue()).getData());
      }

      if (userDTO.getAttribute(USER_ATTRIBUTES.SEC_DEPARTMENTS.getValue()) != null
        && userDTO.getAttribute(USER_ATTRIBUTES.SEC_DEPARTMENTS.getValue()).getData() != null) {
        List<String> secondaryDepartmentIds = Arrays.asList(userDTO
          .getAttribute(USER_ATTRIBUTES.SEC_DEPARTMENTS.getValue()).getData().split(SPLIT_REGEX));

        for (String secondaryDepartmentId : secondaryDepartmentIds) {
          secondaryDepartments
            .add(sDDepartmentService.getDepartmentHistByDepartmentId(secondaryDepartmentId));
        }
      }

      // Finds if the user has user view right, in order to inform the according flag
      Boolean userAdminRight = security.isPermitted(userDTO.getId(),
        SecurityOperation.USER_OPERATION_USER_VIEW.getValue(), null);

      submissUserDTO = UserDTOtoSubmissUserDTOMapper.INSTANCE.toSubmissUserDTO(userDTO, groups,
        tenant, mainDepartment, secondaryDepartments, null, false, userAdminRight);
    }
    return submissUserDTO;
  }

  @Override
  public String getUserGroupName() {

    LOGGER.log(Level.CONFIG, "Executing method getUserGroupName");

    return getGroupName(getUser());
  }

  @Override
  public Set<ValidationError> editUserOptimisticLock(String userId, Long userVersion,
    Long functionVersion, Long secondaryDepartmentsVersion) {

    LOGGER.log(Level.CONFIG, "Executing method editUserOptimisticLock, Parameters: userId: {0}, "
        + "userVersion: {1}",
      new Object[]{userId, userVersion});

    Set<ValidationError> optimisticLockErrors = new HashSet<>();
    if (checkUserVersion(userId, userVersion)
      || (functionVersion != null && USER_ATTRIBUTES.FUNCTION.getValue() != null
      && checkAttributeVersion(userId, functionVersion, USER_ATTRIBUTES.FUNCTION.getValue()))
      || checkAttributeVersion(userId, secondaryDepartmentsVersion,
      USER_ATTRIBUTES.SEC_DEPARTMENTS.getValue())) {
      optimisticLockErrors
        .add(new ValidationError(ValidationMessages.OPTIMISTIC_LOCK_ERROR_FIELD,
          ValidationMessages.OPTIMISTIC_LOCK));
    }
    return optimisticLockErrors;
  }

  /**
   * Checks if user dto version is not equal with user db version.
   *
   * @param userId      the userId
   * @param userVersion the userVersion
   * @return true/false
   */
  private boolean checkUserVersion(String userId, Long userVersion) {
    Long dbVersion = userService.getUserById(userId).getDbversion();
    return !userVersion.equals(dbVersion);
  }

  /**
   * Checks if userAttribute dto version is not equal with userAttribute db version.
   *
   * @param userId           the userId
   * @param attributeVersion the attributeVersion
   * @param userAttribute    the userAttribute
   * @return true/false
   */
  private boolean checkAttributeVersion(String userId, Long attributeVersion,
    String userAttribute) {
    Long dbVersion = userService.getUserById(userId).getAttribute(userAttribute).getDbversion();
    return !attributeVersion.equals(dbVersion);
  }

  @Override
  public void userExportSecurityCheck() {

    LOGGER.log(Level.CONFIG, "Executing method userExportSecurityCheck");

    security.isPermittedOperationForUser(getUserId(),
      SecurityOperation.GENERATE_USERS_EXPORT_REPORT.getValue(), null);
  }

  @Override
  public void userSearchSecurityCheck() {

    LOGGER.log(Level.CONFIG, "Executing method userSearchSecurityCheck");

    security.isPermittedOperationForUser(getUserId(),
      SecurityOperation.USER_OPERATION_USER_VIEW.getValue(), null);
  }
}
