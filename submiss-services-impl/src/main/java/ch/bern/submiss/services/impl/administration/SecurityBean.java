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

import ch.bern.submiss.services.api.administration.SubmissionService;
import ch.bern.submiss.services.api.administration.UserAdministrationService;
import ch.bern.submiss.services.api.dto.SubmissionDTO;
import com.eurodyn.qlack2.fuse.aaa.api.UserService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.ops4j.pax.cdi.api.OsgiService;
import com.eurodyn.qlack2.fuse.aaa.api.OperationService;
import com.eurodyn.qlack2.fuse.aaa.api.ResourceService;
import com.eurodyn.qlack2.fuse.aaa.api.UserGroupService;
import com.eurodyn.qlack2.fuse.aaa.api.dto.GroupDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.ResourceDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO;
import com.eurodyn.qlack2.fuse.cm.api.DocumentService;
import com.eurodyn.qlack2.fuse.cm.api.VersionService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ch.bern.submiss.services.api.administration.SDDepartmentService;
import ch.bern.submiss.services.api.administration.SDTenantService;
import ch.bern.submiss.services.api.constants.Group;
import ch.bern.submiss.services.api.constants.SecurityOperation;
import ch.bern.submiss.services.api.constants.SecurityResource;
import ch.bern.submiss.services.api.constants.TenderStatus;
import ch.bern.submiss.services.api.dto.DepartmentHistoryDTO;
import ch.bern.submiss.services.api.dto.TenantDTO;
import ch.bern.submiss.services.api.exceptions.AuthorisationException;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.api.util.LookupValues.USER_ATTRIBUTES;
import ch.bern.submiss.services.impl.model.QDepartmentHistoryEntity;
import ch.bern.submiss.services.impl.model.QDirectorateHistoryEntity;
import ch.bern.submiss.services.impl.model.QProjectEntity;
import ch.bern.submiss.services.impl.model.QSubmissionEntity;
import ch.bern.submiss.services.impl.model.SubmissionEntity;
import ch.bern.submiss.services.api.constants.Process;

/**
 * The Class SecurityBean.
 */
@Singleton
public class SecurityBean{

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER = Logger.getLogger(SecurityBean.class.getName());

  /**
   * EntityManager setter.
   */
  @PersistenceContext(unitName = "submiss")
  protected EntityManager em;
  /**
   * The s D department service.
   */
  @Inject
  protected SDDepartmentService sDDepartmentService;
  /**
   * The document service.
   */
  @OsgiService
  @Inject
  protected DocumentService documentService;
  /**
   * The version service.
   */
  @OsgiService
  @Inject
  protected VersionService versionService;
  /**
   * The q department history entity.
   */
  QDepartmentHistoryEntity qDepartmentHistoryEntity =
    QDepartmentHistoryEntity.departmentHistoryEntity;
  /**
   * The operation service.
   */
  @OsgiService
  @Inject
  private OperationService operationService;
  /**
   * The resource service.
   */
  @OsgiService
  @Inject
  private ResourceService resourceService;
  /**
   * The user group service.
   */
  @OsgiService
  @Inject
  private UserGroupService userGroupService;

  @OsgiService
  @Inject
  private UserService userService;
  /**
   * The sd tenant service.
   */
  @Inject
  private SDTenantService sdTenantService;
  /**
   * The cache bean.
   */
  @Inject
  private CacheBean cacheBean;

  @Inject
  private SubmissionService submissionService;

  @Inject
  private UserAdministrationService userAdministrationService;

  /**
   * Verify that the given user has the necessary access rights to perform the given operation, if
   * not, then throw exception.
   *
   * @param userId The id of the user
   * @param operationName The requested operation
   */
  public void requireOperation(String userId, String operationName) {

    LOGGER.log(Level.CONFIG,
      "Executing method requireOperation, Parameters: userId: {0}, "
        + "operationName: {1}",
      new Object[]{userId, operationName});

    if (!isPermitted(userId, operationName, null).equals(Boolean.TRUE)) {
      throw new AuthorisationException(userId, operationName);
    }
  }

  /**
   * Checks whether a specific operation, for a specific user, for a specific resource is
   * permitted.
   *
   * @param userId The id of the user
   * @param operation The requested operation
   * @param resource The requested resource
   * @return true, if the user is allowed the operation. false, if the user is not allowed the
   * operation (denied or no info)
   */
  public Boolean isPermitted(String userId, String operation, String resource) {

    LOGGER.log(Level.CONFIG,
      "Executing method isPermitted, Parameters: userId: {0}, "
        + "operation: {1}, resource: {2}",
      new Object[]{userId, operation, resource});

    Boolean isPermitted;
    /* this method fails if the requested resource does not exist */
    try {
      isPermitted = operationService.isPermitted(userId, operation, resource);
    } catch (Exception e) {
      isPermitted = false;
    }
    if (isPermitted == null) {
      isPermitted = false;
    }
    return isPermitted;
  }

  /**
   * Checks whether a specific operation, for a specific group, for a specific resource is
   * permitted.
   *
   * @param groupId the group id
   * @param operation The requested operation
   * @param resource The requested resource
   * @return true, if the user is allowed the operation. false, if the user is not allowed the
   * operation (denied or no info)
   */
  public Boolean isPermittedForGroup(String groupId, String operation, String resource) {

    LOGGER.log(Level.CONFIG,
      "Executing method isPermittedForGroup, Parameters: groupId: {0}. operation: {1}, "
        + "resource: {2}",
      new Object[]{groupId, operation, resource});

    Boolean isPermitted;
    /* this method fails if the requested resource does not exist */
    try {
      isPermitted = operationService.isPermittedForGroup(groupId, operation, resource);
    } catch (Exception e) {
      isPermitted = false;
    }
    if (isPermitted == null) {
      isPermitted = false;
    }
    return isPermitted;
  }

  /**
   * Checks whether a specific operation, for a specific user, for a specific resource is denied.
   *
   * @param userId The id of the user
   * @param operation The requested operation
   * @param resource The requested resource
   * @return true, if the user is denied the operation. false, if the user is allowed the operation,
   * or no info exists
   */
  public Boolean isDenied(String userId, String operation, String resource) {

    LOGGER.log(Level.CONFIG,
      "Executing method isDenied, Parameters: userId: {0}, operation: {1}, "
        + "resource: {2}",
      new Object[]{userId, operation, resource});

    Boolean isDenied = false;
    if (resource != null) {
      Set<ResourceDTO> deniedResources =
        operationService.getResourceForOperation(userId, operation, false, true);
      for (ResourceDTO denied : deniedResources) {
        if (denied.getObjectID().equals(resource)) {
          isDenied = true;
        }
      }
    }
    return isDenied;
  }

  /**
   * Get the user's resources for a given operation.
   *
   * @param userID The ID of the user for whom to retrieve the resources
   * @param operationName The name of the operation for whom to retrieve the resources
   * @param getAllowed True if the operation is permitted
   * @param checkUserGroups True if also the resources of the groups the user belongs to should be
   * retrieved
   * @return The resources for the given operation of a specific user
   */
  public Set<ResourceDTO> getResources(String userID, String operationName, boolean getAllowed,
    boolean checkUserGroups) {

    LOGGER.log(Level.CONFIG,
      "Executing method getResources, Parameters: userID: {0}, "
        + "operationName: {1}, getAllowed: {2}, checkUserGroups: {3}",
      new Object[]{userID, operationName, getAllowed, checkUserGroups});

    return operationService.getResourceForOperation(userID, operationName, getAllowed,
      checkUserGroups);
  }

  /**
   * Add aaa resources in order to apply security at tenant level used in following cases 1)
   * Eingereichte Offerte are viewed at tenant level 2) Documents are viewed at tenant level 2) The
   * admin of the main tenant can edit only users of his tenant (and admins of other tenants).
   *
   * @param userDTO The dto of the user
   */
  public void addTenantResources(UserDTO userDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method addTenantResources, Parameters: userDTO: {0}",
      userDTO);

    // the resource will be the tenant
    String tenant = userDTO.getAttribute(USER_ATTRIBUTES.TENANT.getValue()).getData();

    // find or create resource
    ResourceDTO resourceDTO = getOrCreateResource(tenant, SecurityResource.TENANT.getValue());

    // add this resource to the given user
    operationService.addOperationToUser(userDTO.getId(),
      SecurityOperation.RESOURCE_TENANT.getValue(), resourceDTO.getId(), false);
  }

  /**
   * Add aaa resources in order to apply security for project view for a given user according to the
   * following business rules if User = Admin then he can view all Projects of his Tenant if User =
   * Dir then he can view all Projects of his Directorate (and subdirectorates) if User = PL then he
   * can view all Projects of his Department (and subdepartments) if User = SB or the user does not
   * exist or is not active then he can not view any project.
   *
   * @param userDTO The dto of the user
   * @param groupName The name of the user's group
   */
  public void addProjectViewResourcesToUser(UserDTO userDTO, String groupName) {

    LOGGER.log(Level.CONFIG,
      "Executing method addProjectViewResourcesToUser, Parameters: userDTO: {0}, "
        + "groupName: {1}",
      new Object[]{userDTO, groupName});

    if (groupName.equals(Group.ADMIN.getValue())) {
      // if User = Admin then he can view all Projects of his Tenant
      // so the resource will be the tenant
      String tenant = userDTO.getAttribute(USER_ATTRIBUTES.TENANT.getValue()).getData();

      // find or create resource
      ResourceDTO resourceDTO = getOrCreateResource(tenant, SecurityResource.TENANT.getValue());

      // add this resource to the given user
      operationService.addOperationToUser(userDTO.getId(),
        SecurityOperation.RESOURCE_PROJECT_VIEW.getValue(), resourceDTO.getId(), false);
    } else if (groupName.equals(Group.DIR.getValue())) {
      // create a list with the resources to add to the user
      List<String> resources = new ArrayList<>();

      // if User = Dir then he can view all Projects of his Directorate (and subdirectorates)
      // so the resource will be the directorate
      if (userDTO.getAttribute(USER_ATTRIBUTES.SAML_DEPARTMENT.getValue()) != null) {
        String directorate =
          userDTO.getAttribute(USER_ATTRIBUTES.SAML_DEPARTMENT.getValue()).getData();
        resources.add(directorate);
      }
      // and subdirectorates
      List<String> secondaryDepartmentIds = userAdministrationService
        .getAllSecondaryDepartments(userDTO);
      for (String secondaryDepartmentId : secondaryDepartmentIds) {
        resources.add(secondaryDepartmentId);
      }
      for (String resource : resources) {
        // get directorate id from department id
        // get current department history dto of the given department
        DepartmentHistoryDTO departmentHistoryDTO =
          cacheBean.getActiveDepartmentHistorySD().get(resource);
        // from the department history dto get its directorate
        String directorate = departmentHistoryDTO.getDirectorate().getDirectorateId().getId();
        // find or create resource
        ResourceDTO resourceDTO =
          getOrCreateResource(directorate, SecurityResource.DIRECTORATE.getValue());

        // add this resource to the given user
        operationService.addOperationToUser(userDTO.getId(),
          SecurityOperation.RESOURCE_PROJECT_VIEW.getValue(), resourceDTO.getId(), false);
      }
    } else if (groupName.equals(Group.PL.getValue())) {
      // create a list with the resources to add to the user
      List<String> resources = new ArrayList<>();

      // if User = PL then he can view all Projects of his Department (and subdepartments)
      // so the resource will be the department
      if (userDTO.getAttribute(USER_ATTRIBUTES.SAML_DEPARTMENT.getValue()) != null) {
        String department =
          userDTO.getAttribute(USER_ATTRIBUTES.SAML_DEPARTMENT.getValue()).getData();
        resources.add(department);
      }
      // and subdepartments
      List<String> secondaryDepartmentIds = userAdministrationService
        .getAllSecondaryDepartments(userDTO);
      for (String secondaryDepartmentId : secondaryDepartmentIds) {
        resources.add(secondaryDepartmentId);
      }

      for (String resource : resources) {
        // find or create resource
        ResourceDTO resourceDTO =
          getOrCreateResource(resource, SecurityResource.DEPARTMENT.getValue());

        // add this resource to the given user
        operationService.addOperationToUser(userDTO.getId(),
          SecurityOperation.RESOURCE_PROJECT_VIEW.getValue(), resourceDTO.getId(), false);
      }
    }
  }

  /**
   * Find a resource given its objectId, which is unique (in our database all tables use uuid, so we
   * can use ids from different tables as objectId without problem). If the resource does not exist
   * create it.
   *
   * @param resourceObjectId The objectId of the resource
   * @param resourceName The name of the resource
   * @return the found or created resource
   */
  private ResourceDTO getOrCreateResource(String resourceObjectId, String resourceName) {

    LOGGER.log(Level.CONFIG,
      "Executing method getOrCreateResource, Parameters: resourceObjectId: {0}, "
        + "resourceName: {1}",
      new Object[]{resourceObjectId, resourceName});

    // check if resource already exists
    ResourceDTO resourceDTO = resourceService.getResourceByObjectId(resourceObjectId);
    // if it does not exist create it
    if (resourceDTO == null) {
      resourceDTO = new ResourceDTO();
      resourceDTO.setName(resourceName);
      resourceDTO.setDescription(resourceName);
      resourceDTO.setObjectID(resourceObjectId);
      String resourceId = resourceService.createResource(resourceDTO);
      resourceDTO.setId(resourceId);
    }
    return resourceDTO;
  }

  /**
   * Edit aaa resources for security for project view when user is edited. A change in the resources
   * is needed in following cases 1) if the group is changed 2) if the secondary departments are
   * changed and the user is Dir or PL
   *
   * @param userDTO The dto of the user
   * @param groupName The name of the user's group
   * @param groupChanged The flag indicating whether group is changed or not
   * @param secDeptsChanged The flag indicating whether secondary Departments are changed or not
   */
  public void editProjectViewUserResources(UserDTO userDTO, String groupName, Boolean groupChanged,
    Boolean secDeptsChanged) {

    LOGGER.log(Level.CONFIG,
      "Executing method editProjectViewUserResources, Parameters: userDTO: {0}, "
        + "groupName: {1}, groupChanged: {2}, secDeptsChanged: {3}",
      new Object[]{userDTO, groupName, groupChanged, secDeptsChanged});

    if (groupChanged
      || (groupName.equals(Group.DIR.getValue()) || groupName.equals(Group.PL.getValue()))
      && secDeptsChanged) {
      // remove all user's resources for the given operation
      Set<ResourceDTO> resources = operationService.getResourceForOperation(userDTO.getId(),
        SecurityOperation.RESOURCE_PROJECT_VIEW.getValue(), true);
      for (ResourceDTO resource : resources) {
        operationService.removeOperationFromUser(userDTO.getId(),
          SecurityOperation.RESOURCE_PROJECT_VIEW.getValue(), resource.getId());
      }
      // recreate them
      addProjectViewResourcesToUser(userDTO, groupName);
    }
  }

  /**
   * Applies security check on project search.
   *
   * @param userDTO The dto of the user
   * @param projectEntity the project entity
   * @param submissionEntity the submission entity
   * @param whereClause The whereClause the predicates that need to be applied due to security
   * should be added in
   */
  public void securityCheckProjectSearch(UserDTO userDTO, QProjectEntity projectEntity,
    QSubmissionEntity submissionEntity, BooleanBuilder whereClause) {

    LOGGER.log(Level.CONFIG,
      "Executing method securityCheckProjectSearch, Parameters: userDTO: {0}, "
        + "projectEntity: {1}, submissionEntity: {2}, whereClause: {3}",
      new Object[]{userDTO, projectEntity, submissionEntity, whereClause});

    List<String> tenants = new ArrayList<>();
    List<String> directorates = new ArrayList<>();
    List<String> departments = new ArrayList<>();
    getResourcesByUser(userDTO, tenants, directorates, departments);

    if (!tenants.isEmpty()) {
      whereClause.and(projectEntity.tenant.id.in(tenants));
    }
    if (!directorates.isEmpty()) {
      whereClause.and(projectEntity.department.id.in(JPAExpressions
        .select(qDepartmentHistoryEntity.departmentId.id).from(qDepartmentHistoryEntity)
        .where(qDepartmentHistoryEntity.directorateEnity.id.in(directorates))));
    }
    if (!departments.isEmpty()) {
      whereClause.and(projectEntity.department.id.in(departments));
    }
  }

  private void getResourcesByUser(UserDTO userDTO, List<String> tenants, List<String> directorates,List<String> departments) {
    // get the aaa resources having to do with the department the user can see projects from
    Set<ResourceDTO> resources = operationService.getResourceForOperation(userDTO.getId(),
      SecurityOperation.RESOURCE_PROJECT_VIEW.getValue(), true);
    for (ResourceDTO resource : resources) {
      if (resource.getName().equals(SecurityResource.TENANT.getValue())) {
        // the user has access to all projects of this tenant
        tenants.add(resource.getObjectID());
        //whereClause.and(projectEntity.tenant.id.eq(tenant));
      } else if (resource.getName().equals(SecurityResource.DIRECTORATE.getValue())) {
        // the user has access to all projects of this directorate
        directorates.add(resource.getObjectID());
      } else if (resource.getName().equals(SecurityResource.DEPARTMENT.getValue())) {
        // the user has access to all projects of this department
        departments.add(resource.getObjectID());
      }
    }
  }

  /**
   * Get the submissions a given user can not view.
   *
   * @param userDTO The dto of the user
   * @return A list of the submissions the given user can not view
   */
  public List<String> getLockedSubmissions(UserDTO userDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method getLockedSubmissions, Parameters: userDTO: {0}",
      userDTO);

    Set<ResourceDTO> resources = operationService.getResourceForOperation(userDTO.getId(),
      SecurityOperation.RESOURCE_SUBMISSION_LOCKED.getValue(), false, true);
    List<String> lockedSubmissions = new ArrayList<>();
    for (ResourceDTO resource : resources) {
      lockedSubmissions.add(resource.getObjectID());
    }
    return lockedSubmissions;
  }

  /**
   * Get the departments a given user can view.
   *
   * @param userDTO The dto of the user
   * @return A list of the departments the given user can view
   */
  public List<String> getPermittedDepartments(UserDTO userDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method getPermittedDepartments, Parameters: userDTO: {0}",
      userDTO);

    List<String> departmentList = new ArrayList<>();
    // get the aaa resources for the given operation and user
    Set<ResourceDTO> resources = operationService.getResourceForOperation(userDTO.getId(),
      SecurityOperation.RESOURCE_PROJECT_VIEW.getValue(), true);
    for (ResourceDTO resource : resources) {
      if (resource.getName().equals(SecurityResource.TENANT.getValue())) {
        // the user has access to all departments of this tenant
        String tenant = resource.getObjectID();
        List<String> departmentIds =
          new JPAQueryFactory(em).selectDistinct(qDepartmentHistoryEntity.departmentId.id)
            .where(qDepartmentHistoryEntity.tenant.id.eq(tenant)).from(qDepartmentHistoryEntity)
            .fetch();
        departmentList.addAll(departmentIds);
      } else if (resource.getName().equals(SecurityResource.DIRECTORATE.getValue())) {
        // the user has access to all departments of this directorate
        String directorate = resource.getObjectID();
        List<String> departmentIds =
          new JPAQueryFactory(em).selectDistinct(qDepartmentHistoryEntity.departmentId.id)
            .where(qDepartmentHistoryEntity.directorateEnity.id.eq(directorate))
            .from(qDepartmentHistoryEntity).fetch();
        departmentList.addAll(departmentIds);
      } else if (resource.getName().equals(SecurityResource.DEPARTMENT.getValue())) {
        // the user has access to this department
        String department = resource.getObjectID();
        departmentList.add(department);
      }
    }
    return departmentList;
  }

  /**
   * Get the directorates a given user can view.
   *
   * @param userDTO The dto of the user
   * @return A list of the departments the given user can view
   */
  public List<String> getPermittedDirectorates(UserDTO userDTO) {

    LOGGER.log(Level.CONFIG, "Executing method getPermittedDirectorates, Parameters: userDTO: {0}",
      userDTO);

    List<String> directorateList = new ArrayList<>();
    QDirectorateHistoryEntity directorateHistoryEntity =
      QDirectorateHistoryEntity.directorateHistoryEntity;
    QDepartmentHistoryEntity qDepartmentHistoryEntity =
      QDepartmentHistoryEntity.departmentHistoryEntity;
    // get the aaa resources for the given operation and user
    Set<ResourceDTO> resources = operationService.getResourceForOperation(userDTO.getId(),
      SecurityOperation.RESOURCE_PROJECT_VIEW.getValue(), true);
    for (ResourceDTO resource : resources) {
      if (resource.getName().equals(SecurityResource.TENANT.getValue())) {
        // the user has access to all directorates of this tenant
        String tenant = resource.getObjectID();
        List<String> directorateIds =
          new JPAQueryFactory(em).selectDistinct(directorateHistoryEntity.directorateId.id)
            .where(directorateHistoryEntity.tenant.id.eq(tenant)).from(directorateHistoryEntity)
            .fetch();
        directorateList.addAll(directorateIds);
      } else if (resource.getName().equals(SecurityResource.DIRECTORATE.getValue())) {
        // the user has access to this directorate
        String directorate = resource.getObjectID();
        directorateList.add(directorate);
      } else if (resource.getName().equals(SecurityResource.DEPARTMENT.getValue())) {
        String department = resource.getObjectID();
        List<String> directorateIds =
          new JPAQueryFactory(em).selectDistinct(qDepartmentHistoryEntity.directorateEnity.id)
            .where(qDepartmentHistoryEntity.departmentId.id.eq(department))
            .from(qDepartmentHistoryEntity).fetch();
        directorateList.addAll(directorateIds);
      }
    }
    return directorateList;
  }

  /**
   * Get permitted tenants. Now our business states that each user can see only one tenant, his
   * tenant
   *
   * @param userDTO The dto of the user
   * @return A list of the tenants the given user can view
   */
  public List<String> getPermittedTenants(UserDTO userDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method getPermittedTenants, Parameters: userDTO: {0}",
      userDTO);

    List<String> tenants = new ArrayList<>();
    for (ResourceDTO resource : getResources(userDTO.getId(),
      SecurityOperation.RESOURCE_TENANT.getValue(), true, false)) {
      tenants.add(resource.getObjectID());
    }
    return tenants;
  }

  /**
   * Applies security check on project and all entities that are under a project, so submission,
   * submittent, offer, subunternehmen, arge partner, documents(that belong to submittent) in view,
   * search, create, update, delete. In this case we already know the project id and also the
   * department of the project, so the check is if the given department is valid for the given user.
   * Also a check is made is the submission is locked for the given user. If not, then throw
   * exception
   *
   * @param userDTO The dto of the user
   * @param departmentId the department id
   * @param submissionId the submission id
   * @param operationName the operation name
   */
  public void requireCheckEntity(UserDTO userDTO, String departmentId, String submissionId,
    String operationName) {

    LOGGER.log(Level.CONFIG,
      "Executing method requireCheckEntity, Parameters: userDTO: {0}, "
        + "departmentId: {1}, submissionId: {2}, operationName: {3}",
      new Object[]{userDTO, departmentId, submissionId, operationName});

    if (!isEntityPermitted(userDTO, departmentId, submissionId)) {
      throw new AuthorisationException(userDTO.getId(), operationName + LookupValues.CHECK_ENTITY);
    }
  }

  /**
   * Checks if an entry is permitted according to the following the submission is not locked for the
   * user and if the given department is valid for the given user.
   *
   * @param userDTO The dto of the user
   * @param departmentId the department id
   * @param submissionId the submission id
   * @return the boolean
   */
  public Boolean isEntityPermitted(UserDTO userDTO, String departmentId, String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method isEntityPermitted, Parameters: userDTO: {0}, "
        + "departmentId: {1}, submissionId: {2}",
      new Object[]{userDTO, departmentId, submissionId});

    // check if the submission is not locked for the group of the user
    Boolean isPermitted = !isDenied(userDTO.getId(),
      SecurityOperation.RESOURCE_SUBMISSION_LOCKED.getValue(), submissionId);
    if (isPermitted) {
      Boolean isPermittedDepartment = false;
      /* get the users department */
      DepartmentHistoryDTO department =
        sDDepartmentService.getDepartmentHistByDepartmentId(departmentId);

      /* get the aaa resources having to do with the department the user can see projects from */
      Set<ResourceDTO> resources = operationService.getResourceForOperation(userDTO.getId(),
        SecurityOperation.RESOURCE_PROJECT_VIEW.getValue(), true);
      for (ResourceDTO resource : resources) {
        if (resource.getName().equals(SecurityResource.TENANT.getValue())) {
          /* check if this tenant is the tenant of the given department */
          String tenant = resource.getObjectID();
          if (tenant.equals(department.getTenant().getId())) {
            isPermittedDepartment = true;
            break;
          }
        } else if (resource.getName().equals(SecurityResource.DIRECTORATE.getValue())) {
          /* check if this tenant is the directorate of the given department */
          String directorate = resource.getObjectID();
          if (directorate.equals(department.getDirectorate().getDirectorateId().getId())) {
            isPermittedDepartment = true;
            break;
          }
        } else if (resource.getName().equals(SecurityResource.DEPARTMENT.getValue())) {
          /* check if this department is the given department */
          String departmentResource = resource.getObjectID();
          if (departmentResource.equals(departmentId)) {
            isPermittedDepartment = true;
            break;
          }
        }
      }
      isPermitted = isPermittedDepartment;
    }
    return isPermitted;
  }

  /**
   * Add aaa resources in order to apply security for user view for a given user according to the
   * following business rules 1) Only the users with user management right(checkbox
   * 'Benutzervewaltungs-Recht') can see and edit users. For this an operation will be added to the
   * user with name USER_VIEW. 2) Only the users of the main tenant will see all users, the rest of
   * the users will see only users of their tenant. For this a resource will be created for each
   * tenant for operation RESOURCE_USER_VIEW. 3) Only the user of the main tenant can set Role
   * Admin. For this a resource will be created for role Admin for the users of not the main tenant
   * with deny for operation RESOURCE_ROLE_TYPE_USER. 4) The user of the main tenant is allowed to
   * edit only users of his tenant and admins of all tenants. For this the operation
   * RESOURCE_USER_VIEW will be used and a resource will be created for role Admin for the user of
   * the main tenant for operation RESOURCE_ROLE_TYPE_USER. 5) The users of all other tenants are
   * allowed to edit users of their tenant but not admins. For this the operations
   * RESOURCE_USER_VIEW and RESOURCE_ROLE_TYPE_USER will be used.
   *
   * @param userDTO The dto of the user
   * @param groupName The name of the user's group
   */
  public void addUserViewResourcesToUser(UserDTO userDTO, String groupName) {

    LOGGER.log(Level.CONFIG,
      "Executing method addUserViewResourcesToUser, Parameters: userDTO: {0}, "
        + "groupName: {1}",
      new Object[]{userDTO, groupName});

    // if the user is not admin, then he has no right to view users
    if (groupName.equals(Group.ADMIN.getValue())) {
      // add the operation USER_VIEW to the user
      operationService.addOperationToUser(userDTO.getId(),
        SecurityOperation.USER_OPERATION_USER_VIEW.getValue(), false);

      // check if the user is Admin of the main tenant
      String tenant = userDTO.getAttribute(USER_ATTRIBUTES.TENANT.getValue()).getData();
      TenantDTO tenantDTO = sdTenantService.getTenantById(tenant);
      if (tenantDTO.getIsMain()) {
        operationService.addOperationToUser(userDTO.getId(),
          SecurityOperation.GENERATE_USERS_EXPORT_REPORT.getValue(), false);
        // if yes then he can view users of all tenants, so add all tenants as resource
        List<TenantDTO> allTenantDTO = sdTenantService.readAll();
        for (TenantDTO tenantDTOit : allTenantDTO) {
          // find or create resource
          ResourceDTO resourceDTO =
            getOrCreateResource(tenantDTOit.getId(), SecurityResource.TENANT.getValue());

          // add this resource to the given user
          operationService.addOperationToUser(userDTO.getId(),
            SecurityOperation.RESOURCE_USER_VIEW.getValue(), resourceDTO.getId(), false);
        }
        // if yes then he must be given the resource Admin
        ResourceDTO resourceRoleDTO = getOrCreateResource(SecurityResource.ADMIN.getValue(),
          SecurityResource.ADMIN.getValue());

        // add this resource to the given user
        operationService.addOperationToUser(userDTO.getId(),
          SecurityOperation.RESOURCE_ROLE_TYPE_USER.getValue(), resourceRoleDTO.getId(), false);
      } else {
        // if no he can view only users of his tenant, so add as resource only the tenant of the
        // user
        ResourceDTO resourceDTO = getOrCreateResource(tenant, SecurityResource.TENANT.getValue());

        // add this resource to the given user
        operationService.addOperationToUser(userDTO.getId(),
          SecurityOperation.RESOURCE_USER_VIEW.getValue(), resourceDTO.getId(), false);

        // if no then he must be given the resource Admin with deny=true
        ResourceDTO resourceRoleDTO = getOrCreateResource(SecurityResource.ADMIN.getValue(),
          SecurityResource.ADMIN.getValue());

        // add this resource to the given user
        operationService.addOperationToUser(userDTO.getId(),
          SecurityOperation.RESOURCE_ROLE_TYPE_USER.getValue(), resourceRoleDTO.getId(), true);
      }
    }
  }

  /**
   * Edit aaa resources for security for user view when user is edited. A change in the resources is
   * needed in following cases 1) if the userAdminRight is added or removed 2) if the tenant is
   * changed
   *
   * @param userDTO The dto of the user
   * @param groupName The name of the user's group
   * @param userAdminRight The flag indicating if the user has view users right
   */
  public void editUserViewUserResources(UserDTO userDTO, String groupName, Boolean userAdminRight) {

    LOGGER.log(Level.CONFIG,
      "Executing method editUserViewUserResources, Parameters: userDTO: {0}, "
        + "groupName: {1}, userAdminRight: {2}",
      new Object[]{userDTO, groupName, userAdminRight});

    if (userAdminRight && groupName.equals(Group.ADMIN.getValue())) {
      /*
       * the right is set to true if the user is not admin, then he has no right to view users add
       * resources
       */
      addUserViewResourcesToUser(userDTO, groupName);
    } else {
      /*
       * the right is set to false remove resources
       */
      Set<ResourceDTO> resources = operationService.getResourceForOperation(userDTO.getId(),
        SecurityOperation.RESOURCE_USER_VIEW.getValue(), true);
      for (ResourceDTO resource : resources) {
        operationService.removeOperationFromUser(userDTO.getId(),
          SecurityOperation.RESOURCE_USER_VIEW.getValue(), resource.getId());
      }
      // remove the operation USER_VIEW to the user
      operationService.removeOperationFromUser(userDTO.getId(),
        SecurityOperation.USER_OPERATION_USER_VIEW.getValue());
      operationService.removeOperationFromUser(userDTO.getId(),
        SecurityOperation.GENERATE_USERS_EXPORT_REPORT.getValue());
    }
  }

  /**
   * Applies security check on whether a user can be edited or not according to the following rule
   * The admin of the main tenant with user management right is allowed to edit only users of his
   * tenant and admins of all tenants and the admins of all other tenants with user management right
   * are allowed to edit users of their tenant but not admins. So a check will be made if
   * currentUsersTenant(user to search for) = usersTenant(the user that makes the search) if
   * currentUsersRole = role that the user can not edit(for other admins role Admin, so the user of
   * other than the main tenant will not be allowed to edit Admins) can not be edited else can be
   * edited else if currentUsersRole = role that the user can edit(for admins of the main tenant
   * role Admin, so the user of the main tenant will be allowed to edit Admins) can be edited else
   * can not be edited
   *
   * @param userId The id of the user that will make the edit
   * @param tenant The tenant of the user to be edited
   * @param role The role of the user to be edited
   * @return true if the user can be edited, false otherwise
   */
  public Boolean canUserBeEdited(String userId, String tenant, String role) {

    LOGGER.log(Level.CONFIG,
      "Executing method canUserBeEdited, Parameters: userId: {0}, "
        + "tenant: {1}, role: {2}",
      new Object[]{userId, tenant, role});

    Boolean canBeEdited = false;
    Boolean tenantPermitted =
      isPermitted(userId, SecurityOperation.RESOURCE_TENANT.getValue(), tenant);
    if (tenantPermitted) {
      Boolean roleDenied = false;
      Set<ResourceDTO> rolesDenied =
        getResources(userId, SecurityOperation.RESOURCE_ROLE_TYPE_USER.getValue(), false, false);
      for (ResourceDTO resource : rolesDenied) {
        if (role.equals(resource.getObjectID())) {
          roleDenied = true;
        }
      }
      if (roleDenied) {
        canBeEdited = false;
      } else {
        canBeEdited = true;
      }
    } else {
      Boolean rolePermitted = false;
      Set<ResourceDTO> rolesPermitted =
        getResources(userId, SecurityOperation.RESOURCE_ROLE_TYPE_USER.getValue(), true, false);
      for (ResourceDTO resource : rolesPermitted) {
        if (role.equals(resource.getObjectID())) {
          rolePermitted = true;
        }
      }
      if (rolePermitted) {
        canBeEdited = true;
      } else {
        canBeEdited = false;
      }
    }
    return canBeEdited;
  }

  /**
   * Add aaa operations that are allowed only for the users according to their tenant.
   *
   * @param userDTO The dto of the user
   * @param groupName The name of the user's group
   */
  public void addTenantOperationsToUser(UserDTO userDTO, String groupName) {

    LOGGER.log(Level.CONFIG,
      "Executing method addTenantOperationsToUser, Parameters: userDTO: {0}, "
        + "groupName: {1}",
      new Object[]{userDTO, groupName});

    String tenant = userDTO.getAttribute(USER_ATTRIBUTES.TENANT.getValue()).getData();
    TenantDTO tenantDTO = sdTenantService.getTenantById(tenant);
    if (tenantDTO.getIsMain()) {
      operationService.addOperationToUser(userDTO.getId(),
        SecurityOperation.MAIN_TENANT_BESCHAFFUNGSWESEN_VIEW.getValue(), false);
      if (groupName.equals(Group.ADMIN.getValue())) {
        operationService.addOperationToUser(userDTO.getId(),
          SecurityOperation.MAIN_TENANT_BESCHAFFUNGSWESEN_EDIT.getValue(), false);
        operationService.addOperationToUser(userDTO.getId(),
          SecurityOperation.MAIN_TENANT_BEMERKUNG_FABE_VIEW.getValue(), false);
        operationService.addOperationToUser(userDTO.getId(),
          SecurityOperation.MAIN_TENANT_BEMERKUNG_FABE_EDIT.getValue(), false);
      }
    }
    if (!tenantDTO.getName().equals(LookupValues.TENANT_KANTON_BERN)) {
      operationService.addOperationToUser(userDTO.getId(),
        SecurityOperation.EXTENDED_ACCESS.getValue(), false);
    }
  }

  /**
   * Edit aaa resources for security for Eingereichte Offerte list. In this case the resource is the
   * submission. The offer of a submission is allowed to be viewed in the list of Eingereichte
   * Offerte in the following cases For Admin after status 'Offertöffnung gestartet'. For DIR and PL
   * if the submission is Open or Selective and is marked as 'DL-Wettbewerb' if the submission is
   * unlocked after status 'Offertöffnung gestartet' else after status 'Manuelle Zuschlagserteilung
   * abgeschlossen' else after status 'Offertöffnung gestartet' This edit is needed in following
   * cases 1) if the status of the submission is changed 2) if the process type of the submission is
   * changed 3) if the flag 'DL-Wettbewerb' of a submission is changed 4) if the flag gesperrt of a
   * submission is changed
   *
   * @param submissionEntity The submission
   * @param status The status of the submission
   */
  public void editSubmissionListGroupResources(SubmissionEntity submissionEntity, String status) {

    LOGGER.log(Level.CONFIG,
      "Executing method editSubmissionListGroupResources, Parameters: "
        + "submissionEntity: {0}, status: {1}",
      new Object[]{submissionEntity, status});

    long statusIdLong = Long.parseLong(status);
    long manualAwardCompleted = Long.parseLong(TenderStatus.MANUAL_AWARD_COMPLETED.getValue());
    long offerOpeningStarted = Long.parseLong(TenderStatus.OFFER_OPENING_STARTED.getValue());
    Boolean isDL = (submissionEntity.getProcess().equals(Process.OPEN)
      || submissionEntity.getProcess().equals(Process.SELECTIVE))
      && submissionEntity.getIsServiceTender() != null && submissionEntity.getIsServiceTender();
    ResourceDTO resourceDTO =
      getOrCreateResource(submissionEntity.getId(), SecurityResource.SUBMISSION.getValue());
    /* get all groups */
    List<GroupDTO> groups = userGroupService.listGroups();
    for (GroupDTO group : groups) {
      if (group.getName().equals(Group.ADMIN.getValue())) {
        if (statusIdLong >= offerOpeningStarted) {
          operationService.addOperationToGroup(group.getId(),
            SecurityOperation.RESOURCE_SUBMISSION_LIST.getValue(), resourceDTO.getId(), false);
        } else {
          operationService.removeOperationFromGroup(group.getId(),
            SecurityOperation.RESOURCE_SUBMISSION_LIST.getValue(), resourceDTO.getId());
        }
      } else if (group.getName().equals(Group.DIR.getValue())
        || group.getName().equals(Group.PL.getValue())) {
        if (isDL) {
          if (isPermittedForGroup(group.getId(),
            SecurityOperation.RESOURCE_SUBMISSION_UNLOCKED.getValue(), submissionEntity.getId())
            && statusIdLong >= offerOpeningStarted || statusIdLong >= manualAwardCompleted) {
            operationService.addOperationToGroup(group.getId(),
              SecurityOperation.RESOURCE_SUBMISSION_LIST.getValue(), resourceDTO.getId(), false);
          } else {
            operationService.removeOperationFromGroup(group.getId(),
              SecurityOperation.RESOURCE_SUBMISSION_LIST.getValue(), resourceDTO.getId());
          }
        } else {
          if (statusIdLong >= offerOpeningStarted) {
            operationService.addOperationToGroup(group.getId(),
              SecurityOperation.RESOURCE_SUBMISSION_LIST.getValue(), resourceDTO.getId(), false);
          } else {
            operationService.removeOperationFromGroup(group.getId(),
              SecurityOperation.RESOURCE_SUBMISSION_LIST.getValue(), resourceDTO.getId());
          }
        }
      }
    }
  }


  /**
   * Delete aaa resources for security for Eingereichte Offerte list if the submission is deleted.
   *
   * @param submissionId The id of the submission
   */
  public void deleteSubmissionListGroupResources(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method deleteSubmissionListGroupResources, Parameters: submissionId: {0}",
      submissionId);

    // check if resource already exists
    ResourceDTO resourceDTO = resourceService.getResourceByObjectId(submissionId);
    // if it does not exist then no need to delete it
    if (resourceDTO != null) {
      /* get all groups */
      List<GroupDTO> groups = userGroupService.listGroups();
      /* remove resource from all groups if exists */
      for (GroupDTO group : groups) {
        operationService.removeOperationFromGroup(group.getId(),
          SecurityOperation.RESOURCE_SUBMISSION_LIST.getValue(), resourceDTO.getId());
      }
      /* delete also the resource */
      resourceService.deleteResource(resourceDTO.getId());
    }
  }

  /**
   * Applies security check on Eingereichte Offerte list according to the status rules (applied in
   * the group operation ResourceSubmissionList) which are as follows A submission is displayed
   * after status='Offertöffnung gestartet' If a submission is Open or Selective and is marked as
   * 'DL-Wettbewerb' the Projektleiter, Direktion can view offers in the list after status='Manuelle
   * Zuschlagserteilung abgeschlossen'.
   *
   * @param userDTO The dto of the user
   * @return a list of the allowed submission ids
   */
  public List<String> securityCheckSubmissionListStatus(UserDTO userDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method securityCheckSubmissionListStatus, Parameters: userDTO: {0}",
      userDTO);
    /*
     * get group resources for operation ResourceSubmissionList, so allowed submission ids will be
     * returned
     */
    Set<ResourceDTO> resources = operationService.getResourceForOperation(userDTO.getId(),
      SecurityOperation.RESOURCE_SUBMISSION_LIST.getValue(), true, true);
    List<String> submissionIds = new ArrayList<>();
    for (ResourceDTO resource : resources) {
      submissionIds.add(resource.getObjectID());
    }
    return submissionIds;
  }

  /**
   * Add aaa resources to operation RESOURCE_SUBMISSION_LOCKED in order to gather the locked
   * submissions. Only Admin has the right to view locked submissions, Dir and Pl have no right to
   * view them. So the locked submissions will be added to the groups Dir and Pl with deny.
   *
   * @param submissionId The id of the locked submission
   */
  public void addSubmissionLockedGroupResources(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method addSubmissionLockedGroupResources, Parameters: submissionId: {0}",
      submissionId);
    // find or create resource
    ResourceDTO resourceDTO =
      getOrCreateResource(submissionId, SecurityResource.SUBMISSION.getValue());
    /* get all groups */
    List<GroupDTO> groups = userGroupService.listGroups();
    for (GroupDTO group : groups) {
      if (group.getName().equals(Group.DIR.getValue())
        || group.getName().equals(Group.PL.getValue())) {
        operationService.addOperationToGroup(group.getId(),
          SecurityOperation.RESOURCE_SUBMISSION_LOCKED.getValue(), resourceDTO.getId(), true);
      }
    }
  }

  /**
   * Edit aaa operation RESOURCE_SUBMISSION_LOCKED in order to gather the locked submissions, if a
   * change has been made, an unlocked submission is marked as locked or a locked is marked as
   * unlocked. Also a requirement states that if a submission was marked as locked and was after
   * unlocked it should be viewed by DIR, PL independent of its status or Verfahren or DL-Wettbewerb
   * flag, so the resource will be added also to operation RESOURCE_SUBMISSION_UNLOCKED for this
   *
   * @param submissionId The id of the submission
   * @param isLocked Flag indicating whether a submission is locked or unlocked
   */
  public void editSubmissionLockedGroupResources(String submissionId, Boolean isLocked) {

    LOGGER.log(Level.CONFIG,
      "Executing method editSubmissionLockedGroupResources, Parameters: submissionId: {0}, "
        + "isLocked: {1}",
      new Object[]{submissionId, isLocked});
    /* find resource */
    ResourceDTO resourceDTO = resourceService.getResourceByObjectId(submissionId);
    /* get all groups */
    List<GroupDTO> groups = userGroupService.listGroups();
    if (isLocked) {
      /*
       * add operation RESOURCE_SUBMISSION_LOCKED and remove operation RESOURCE_SUBMISSION_UNLOCKED
       * if exists, that is if the submission was locked and unlocked before
       */
      for (GroupDTO group : groups) {
        if (group.getName().equals(Group.DIR.getValue())
          || group.getName().equals(Group.PL.getValue())) {
          operationService.removeOperationFromGroup(group.getId(),
            SecurityOperation.RESOURCE_SUBMISSION_UNLOCKED.getValue(), resourceDTO.getId());
          operationService.addOperationToGroup(group.getId(),
            SecurityOperation.RESOURCE_SUBMISSION_LOCKED.getValue(), resourceDTO.getId(), true);
        }
      }
    } else {
      /*
       * remove operation RESOURCE_SUBMISSION_LOCKED if exists and add operation
       * RESOURCE_SUBMISSION_UNLOCKED
       */
      for (GroupDTO group : groups) {
        if (group.getName().equals(Group.DIR.getValue())
          || group.getName().equals(Group.PL.getValue())) {
          operationService.removeOperationFromGroup(group.getId(),
            SecurityOperation.RESOURCE_SUBMISSION_LOCKED.getValue(), resourceDTO.getId());
          operationService.addOperationToGroup(group.getId(),
            SecurityOperation.RESOURCE_SUBMISSION_UNLOCKED.getValue(), resourceDTO.getId(),
            false);
        }
      }
    }
  }

  /**
   * Finds if it is allowed to access a Operation
   * @param userId
   * @param operation
   * @param resource
   * @throws AuthorisationException
   */
  public void isPermittedOperationForUser(String userId, String operation, String resource) {

    LOGGER.log(Level.CONFIG, "Executing method isPermittedOperationForUser, Parameters: "
        + "userId: {0}, operation{1}, resource: {2}",
      new Object[]{userId, operation, resource});

    Set<String> userGroups = userGroupService.getUserGroupsIds(userId);
    if ( resource != null ){
      isResourcePermittedForUser(userId, operation, resource);
    }
    if (!userGroups.isEmpty() && userGroups.size() == 1) {
      String groupId = userGroups.iterator().next();
      if (!isPermittedForGroup(groupId, operation, null)) {
        throw new AuthorisationException(groupId, operation);
      }
    }

  }

  /**
   *  @param userId
   * @param operation
   * @param resourceId
   */
  private void isResourcePermittedForUser(String userId, String operation,
    String resourceId) {

    LOGGER.log(Level.CONFIG, "Executing method isResourcePermittedForUser, Parameters: "
        + "userId: {0}, resourceId{1}",
      new Object[]{userId, resourceId});

    final SubmissionDTO submissionDTO = submissionService.getSubmissionByResourceId(resourceId);
    if (submissionDTO == null) {
      //TODO throw 404
    } else {
      UserDTO userDTO = new UserDTO();
      userDTO.setId(userId);
      if (!getPermittedDepartments(userDTO)
        .contains(submissionDTO.getProject().getDepartment().getDepartmentId().getId())) {
        throw new AuthorisationException(userId, resourceId);
      }

      String userGroupName = getUserGroupName(userDTO);
      if (userGroupName != null &&
        (userGroupName.equals(Group.DIR.getValue()) || userGroupName.equals(Group.PL.getValue()))
        && submissionDTO.getIsLocked()
        && (!operation
        .equals(ch.bern.submiss.services.api.constants.SecurityOperation.PROJECT.getValue())
        && !operation.equals(SecurityOperation.TENDER_LIST_VIEW.getValue()))) {
        throw new AuthorisationException(userId, resourceId);
      }


      if (!submissionDTO.getProject().getTenant().getId().equals(
        userService.getUserById(userId).getAttribute(USER_ATTRIBUTES.TENANT.getValue())
          .getData())) {
        throw new AuthorisationException(userId, resourceId);
      }
    }
  }

  private String getUserGroupName(UserDTO user) {
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
}
