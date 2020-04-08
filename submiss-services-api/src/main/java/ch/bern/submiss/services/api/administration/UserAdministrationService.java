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

package ch.bern.submiss.services.api.administration;

import ch.bern.submiss.services.api.dto.SubmissUserDTO;
import ch.bern.submiss.services.api.dto.TenantDTO;
import ch.bern.submiss.services.api.util.LookupValues.USER_STATUS;
import com.eurodyn.qlack2.fuse.aaa.api.criteria.UserSearchCriteria;
import com.eurodyn.qlack2.fuse.aaa.api.dto.GroupDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.UserAttributeDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * The Interface UserAdministrationService.
 */
public interface UserAdministrationService {

  /**
   * Creates the user and creates security resources for the first user, because the first user is
   * automatically registered, the rest of the users will get their security resources upon
   * registration.
   *
   * @param dto         the dto
   * @param groupName   The name of the group of the user
   * @param isFirstUser The flag indicating whether the user is the first user
   * @return Returns the id of the user.
   */
  String createUser(com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO dto, String groupName,
    Boolean isFirstUser);

  /**
   * Adds the user group by name.
   *
   * @param userId    the user id
   * @param groupName the group name
   */
  void addUserGroupByName(String userId, String groupName);

  /**
   * Adds the user group by id.
   *
   * @param userId  the user id
   * @param groupId the group id
   */
  void addUserGroupById(String userId, String groupId);

  /**
   * Gets the group by name.
   *
   * @param groupName the group name
   * @return the group by name
   */
  GroupDTO getGroupByName(String groupName);

  /**
   * Count users.
   *
   * @param submissUserDTO the submiss user DTO
   * @return the long
   */
  long countUsers(SubmissUserDTO submissUserDTO);

  /**
   * Search users.
   *
   * @param submissUserDTO the submiss user DTO
   * @return the list
   */
  List<SubmissUserDTO> searchUsers(SubmissUserDTO submissUserDTO);

  /**
   * Find specific user.
   *
   * @param userSearchCriteria the user search criteria
   * @return true, if successful
   */
  boolean findSpecificUser(UserSearchCriteria userSearchCriteria);

  /**
   * Gets the user by id.
   *
   * @param userId the user id
   * @return the user by id
   */
  SubmissUserDTO getUserById(String userId);

  /**
   * Edits the user and creates or updates security resources, creates in case of registration,
   * updates in case of changes.
   *
   * @param userDTO               The dto of the user
   * @param groupName             The name of the group of the user
   * @param groupChanged          The flag indicating whether group is changed or not
   * @param secDeptsChanged       The flag indicating whether secondary Departments are changed or
   *                              not
   * @param userAdminRight        The flag indicating whether the user has the right to edit users
   *                              or not
   * @param userAdminRightChanged The flag indicating whether userAdminRight is changed or not
   * @param register              The flag indicating whether to register a user
   * @return Returns the id of the user.
   */
  String editUser(UserDTO userDTO, String groupName, Boolean groupChanged,
    Boolean secDeptsChanged, Boolean userAdminRight, Boolean userAdminRightChanged,
    Boolean register);

  /**
   * Checks for optimistic lock errors when updating the user.
   *
   * @param userId      the userId
   * @param userVersion the userVersion
   * @return the error
   */
  Set<ValidationError> editUserOptimisticLock(String userId, Long userVersion, Long functionVersion,
    Long secondaryDepartmentsVersion);

  /**
   * Update user group by name.
   *
   * @param userId    the user id
   * @param groupName the group name
   */
  void updateUserGroupByName(String userId, String groupName);

  /**
   * Change user attributes.
   *
   * @param userName the user name
   * @param attr     the attr
   */
  void changeUserAttributes(String userName, List<UserAttributeDTO> attr);

  /**
   * Decline user.
   *
   * @param userId the user id
   */
  String declineUser(String userId);

  /**
   * Finds the status of the calling user in the database.
   *
   * @return Returns the status of the user registered in the database.
   */
  USER_STATUS getUserStatus();

  /**
   * Gets the all users.
   *
   * @return the all users
   */
  long getAllUsers();

  /**
   * Gets a list of the groups the user is permitted to set.
   *
   * @return Returns a list of all permitted groups
   */
  List<GroupDTO> getPermittedGroups();

  /**
   * Gets the users by tenant and role.
   *
   * @param tenantId the tenant id
   * @param role     the role
   * @return the users by tenant and role
   */
  List<UserDTO> getUsersByTenantAndRole(String tenantId, String role);

  /**
   * Export users.
   *
   * @param users the users
   * @return the byte[]
   */
  byte[] exportUsers(List<SubmissUserDTO> users);

  /**
   * Gets the exported users.
   *
   * @param startDate the start date
   * @param endDate   the end date
   * @param status    the status
   * @return the exported users
   */
  List<SubmissUserDTO> getExportedUsers(Date startDate, Date endDate, String status);

  /**
   * Automatically deactivate users.
   */
  void automaticallyDeactivateUsers();

  /**
   * Gets the tenant.
   *
   * @return the tenant
   */
  TenantDTO getTenant();

  /**
   * Gets the specific user.
   *
   * @param userId the user id
   * @return the specific user
   */
  SubmissUserDTO getSpecificUser(String userId);

  /**
   * Gets the user group name.
   *
   * @return the user group name
   */
  String getUserGroupName();

  /**
   * Security check for UserExport.
   */
  void userExportSecurityCheck();

  /**
   * Security check for UserEdit.
   */
  void userSearchSecurityCheck();
}
