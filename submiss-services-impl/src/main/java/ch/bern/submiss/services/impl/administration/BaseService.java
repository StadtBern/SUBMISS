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

import ch.bern.submiss.services.api.constants.TenderStatus;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.api.util.LookupValues.USER_ATTRIBUTES;
import ch.bern.submiss.services.api.util.LookupValues.WEBSSO_ATTRIBUTES;
import ch.bern.submiss.services.impl.model.QDepartmentHistoryEntity;
import com.eurodyn.qlack2.fuse.aaa.api.UserGroupService;
import com.eurodyn.qlack2.fuse.aaa.api.UserService;
import com.eurodyn.qlack2.fuse.aaa.api.dto.GroupDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO;
import com.eurodyn.qlack2.util.sso.dto.SAMLAttributeDTO;
import com.eurodyn.qlack2.util.sso.dto.WebSSOHolder;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.AbstractJPAQuery;
import com.querydsl.jpa.impl.JPAQuery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.ops4j.pax.cdi.api.OsgiService;

/**
 * The Class BaseService.
 */
public abstract class BaseService {

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER = Logger.getLogger(BaseService.class.getName());

  /**
   * EntityManager setter.
   */
  @PersistenceContext(unitName = "submiss")
  protected EntityManager em;

  /**
   * The user service.
   */
  @OsgiService
  @Inject
  protected UserService userService;

  /**
   * The user group service.
   */
  @OsgiService
  @Inject
  protected UserGroupService userGroupService;

  /**
   * The security.
   */
  @Inject
  protected SecurityBean security;

  /**
   * The q department history entity.
   */
  QDepartmentHistoryEntity qDepartmentHistoryEntity =
    QDepartmentHistoryEntity.departmentHistoryEntity;

  /**
   * Count.
   *
   * @param <E> the element type
   * @param <T> the generic type
   * @param em the em
   * @param entity the entity
   * @param qEntity the q entity
   * @param whereClause the where clause
   * @return the long
   */
  protected <E, T extends EntityPathBase> long count(EntityManager em, E entity, T qEntity,
    BooleanBuilder whereClause) {
    JPAQuery<E> query = new JPAQuery<>(em);
    return ((AbstractJPAQuery) query.select(qEntity).from(qEntity).where(whereClause)).fetchCount();
  }

  /**
   * Get the user Id in the SAML token and try to find a matching user in the database.
   *
   * @return Returns a user matching the user ID in the SAML token in the system's database.
   */
  protected UserDTO getUser() {
    LOGGER.log(Level.CONFIG, "Executing method getUser");
    return userService.getUserById(getUserId());
  }

  /**
   * Find the Id of the current user as present in the SAML token.
   *
   * @return Returns the user Id of the current user.
   */
  protected String getUserId() {
    LOGGER.log(Level.CONFIG, "Executing method getUserId");
    return WebSSOHolder.getAttribute(WEBSSO_ATTRIBUTES.USER_ID.getValue())
      .map(SAMLAttributeDTO::getValue).orElse("");
  }

  /**
   * Get the group of the user.
   *
   * @param user the user
   * @return The group.
   */
  protected String getGroupName(UserDTO user) {
    LOGGER.log(Level.CONFIG, "Executing method getGroupName, Parameters: user {0}", user);
    String groupName = null;
    if (user != null) {
      Set<String> groupIds = userGroupService.getUserGroupsIds(user.getId());
      // in our system each user belongs to one group only
      for (String groupId : groupIds) {
        GroupDTO group = userGroupService.getGroupByID(groupId, true);
        groupName = group.getName();
      }
    }
    return groupName;
  }

  /**
   * Get the group id of the user.
   *
   * @param user the user
   * @return The groupId.
   */
  protected String getGroupId(UserDTO user) {
    LOGGER.log(Level.CONFIG, "Executing method getGroupId, Parameters: user {0}", user);
    String id = null;
    if (user != null) {
      Set<String> groupIds = userGroupService.getUserGroupsIds(user.getId());
      // in our system each user belongs to one group only
      for (String groupId : groupIds) {
        GroupDTO group = userGroupService.getGroupByID(groupId, true);
        id = group.getId();
      }
    }
    return id;
  }

  /**
   * Compare current vs specific status based on the position in the list.
   *
   * @param submissionStatus the submission status
   * @param specificStatus the specific status
   * @return true, if successful
   */
  protected boolean compareCurrentVsSpecificStatus(TenderStatus submissionStatus,
    TenderStatus specificStatus) {
    LOGGER.log(Level.CONFIG,
      "Executing method compareCurrentVsSpecificStatus, Parameters: submissionStatus: {0}"
        + ", specificStatus: {1}",
      new Object[]{submissionStatus, specificStatus});
    return LookupValues.getSubmissstatuses().indexOf(submissionStatus) >= LookupValues
      .getSubmissstatuses().indexOf(specificStatus);
  }

  /**
   * Gets the full name.
   *
   * @return the full name
   */
  protected String getUserFullName() {
    LOGGER.log(Level.CONFIG, "Executing method getUserFullName");
    return getUser().getAttribute(USER_ATTRIBUTES.FIRSTNAME.getValue()).getData() + " "
      + getUser().getAttribute(USER_ATTRIBUTES.LASTNAME.getValue()).getData();
  }

  /**
   * Gets the user permitted department ids.
   *
   * @return the user permitted department ids
   */
  protected List<String> getUserPermittedDepartmentIds() {
    LOGGER.log(Level.CONFIG, "Executing method getUserPermittedDepartmentIds");
    List<String> departmentIds = new ArrayList<>();
    UserDTO userDTO = getUser();
    if (userDTO.getAttribute(USER_ATTRIBUTES.SAML_DEPARTMENT.getValue()) != null) {
      departmentIds.add(userDTO.getAttribute(USER_ATTRIBUTES.SAML_DEPARTMENT.getValue()).getData());
    }
    // and secondary departments
    if (userDTO.getAttribute(USER_ATTRIBUTES.SEC_DEPARTMENTS.getValue()) != null
      && userDTO.getAttribute(USER_ATTRIBUTES.SEC_DEPARTMENTS.getValue()).getData() != null) {
      List<String> secondaryDepartmentIds = Arrays.asList(userDTO
        .getAttribute(USER_ATTRIBUTES.SEC_DEPARTMENTS.getValue()).getData().split("\\s*,\\s*"));
      for (String secondaryDepartmentId : secondaryDepartmentIds) {
        departmentIds.add(secondaryDepartmentId);
      }
    }
    return departmentIds;
  }
}
