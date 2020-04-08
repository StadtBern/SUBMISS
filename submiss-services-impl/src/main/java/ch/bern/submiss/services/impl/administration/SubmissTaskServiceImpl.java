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

import ch.bern.submiss.services.api.administration.CompanyService;
import ch.bern.submiss.services.api.administration.LexiconService;
import ch.bern.submiss.services.api.administration.SubmissTaskService;
import ch.bern.submiss.services.api.administration.SubmissionService;
import ch.bern.submiss.services.api.administration.UserAdministrationService;
import ch.bern.submiss.services.api.constants.AuditEvent;
import ch.bern.submiss.services.api.constants.AuditGroupName;
import ch.bern.submiss.services.api.constants.AuditLevel;
import ch.bern.submiss.services.api.constants.AuditMessages;
import ch.bern.submiss.services.api.constants.CategorySD;
import ch.bern.submiss.services.api.constants.Group;
import ch.bern.submiss.services.api.constants.ProofStatus;
import ch.bern.submiss.services.api.constants.SecurityOperation;
import ch.bern.submiss.services.api.constants.TaskTypes;
import ch.bern.submiss.services.api.dto.MasterListValueHistoryDTO;
import ch.bern.submiss.services.api.dto.SubmissTaskDTO;
import ch.bern.submiss.services.api.dto.SubmissUserDTO;
import ch.bern.submiss.services.api.dto.SubmissionDTO;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.api.util.LookupValues.USER_ATTRIBUTES;
import ch.bern.submiss.services.api.util.ValidationMessages;
import ch.bern.submiss.services.impl.mappers.SubmissTaskDTOMapper;
import ch.bern.submiss.services.impl.mappers.SubmissionMapper;
import ch.bern.submiss.services.impl.model.MasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.ProjectEntity;
import ch.bern.submiss.services.impl.model.QMasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.QProjectEntity;
import ch.bern.submiss.services.impl.model.QSubmissTasksEntity;
import ch.bern.submiss.services.impl.model.SubmissTasksEntity;
import ch.bern.submiss.services.impl.model.SubmissionEntity;
import com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

/**
 * The Class SubmissTaskServiceImpl.
 */
@Transactional
@OsgiServiceProvider(classes = {SubmissTaskService.class})
@Singleton
public class SubmissTaskServiceImpl extends BaseService implements SubmissTaskService {

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER = Logger.getLogger(SubmissTaskServiceImpl.class.getName());
  /**
   * The Constant ADMINISTRATION.
   */
  private static final String ADMINISTRATION = "Administration";
  /**
   * The hybrid task.
   */
  private static Integer hybridTask = 1;
  /**
   * The q submiss task.
   */
  QSubmissTasksEntity qSubmissTask = QSubmissTasksEntity.submissTasksEntity;
  /**
   * The cache bean.
   */
  @Inject
  private CacheBean cacheBean;
  /**
   * The audit bean.
   */
  @Inject
  private AuditBean auditBean;
  /**
   * The company service.
   */
  @Inject
  private CompanyService companyService;
  /**
   * The submission service.
   */
  @Inject
  private SubmissionService submissionService;
  /**
   * The users service.
   */
  @Inject
  private UserAdministrationService usersService;
  /**
   * The lexicon service.
   */
  @Inject
  private LexiconService lexiconService;
  /**
   * The q master list value history entity.
   */
  private QMasterListValueHistoryEntity qMasterListValueHistoryEntity =
    QMasterListValueHistoryEntity.masterListValueHistoryEntity;

  @Override
  public List<SubmissTaskDTO> getAllTasks(boolean showUserTasks) {

    LOGGER.log(Level.CONFIG,
      "Executing method getAllTasks, Parameters: showUserTasks: {0}",
      showUserTasks);

    security.isPermittedOperationForUser(getUserId(),
      SecurityOperation.PENDING.getValue(), null);

    QProjectEntity qProject = QProjectEntity.projectEntity;
    SubmissUserDTO currentUser = usersService.getUserById(getUser().getId());

    Map<String, UserDTO> userMap = new HashMap<>();
    List<String> userIds = new ArrayList<>();
    String tenantId = currentUser.getTenant().getId();
    List<UserDTO> userDTO = new ArrayList<>();

    // Get all tenant users except for the Admin.
    userDTO.addAll(usersService.getUsersByTenantAndRole(tenantId, Group.DIR.name()));
    userDTO.addAll(usersService.getUsersByTenantAndRole(tenantId, Group.PL.name()));
    userDTO.addAll(usersService.getUsersByTenantAndRole(tenantId, Group.SB.name()));

    if (!currentUser.getTenant().getIsMain()) {
      // If the tenant is not the main tenant, just add the Admin user.
      userDTO.addAll(usersService.getUsersByTenantAndRole(tenantId, Group.ADMIN.name()));
    } else {
      // Case of main tenant.
      if (showUserTasks) {
        // In case of user tasks, add all the Admins and not just the Admin of the main tenant
        // (their ids will be used below to fetch registration applications of Admins who do not
        // belong to the main tenant, but whose applications must be resolved by the Admin of the
        // main tenant).
        userDTO.addAll(usersService.getUsersByTenantAndRole(null, Group.ADMIN.name()));
      } else {
        // In case we do not have user tasks, just add the Admin user.
        userDTO.addAll(usersService.getUsersByTenantAndRole(tenantId, Group.ADMIN.name()));
      }
    }

    for (UserDTO user : userDTO) {
      userIds.add(user.getId());
      userMap.put(user.getId(), user);
    }

    List<SubmissTasksEntity> submissTaskEntity;
    if (showUserTasks) {
      submissTaskEntity = new JPAQueryFactory(em).select(qSubmissTask).from(qSubmissTask)
        .where(qSubmissTask.createdBy.in(userIds).and(qSubmissTask.userToEdit.isNotNull()))
        .orderBy(qSubmissTask.createdOn.asc()).fetch();
    } else {
      List<ProjectEntity> projects = new JPAQueryFactory(em).select(qProject).from(qProject)
        .where(qProject.tenant.id
          .eq(getUser().getAttributeData(LookupValues.USER_ATTRIBUTES.TENANT.getValue())))
        .fetch();
      submissTaskEntity = new JPAQueryFactory(em).select(qSubmissTask).from(qSubmissTask)
        .leftJoin(qSubmissTask.submission.project, qProject)
        .where(qSubmissTask.createdBy.in(userIds)
          .or(qSubmissTask.submission.project.in(projects))
          .and(qSubmissTask.userToEdit.isNull()))
        .orderBy(qSubmissTask.createdOn.asc()).fetch();
    }

    Map<String, MasterListValueHistoryDTO> activeSD = new HashMap<>();
    activeSD.putAll(cacheBean.getActiveSD().row(CategorySD.OBJECT.getValue()));
    activeSD.putAll(cacheBean.getActiveSD().row(CategorySD.WORKTYPE.getValue()));

    List<SubmissTaskDTO> submissTaskDTOList =
      SubmissTaskDTOMapper.INSTANCE.tasksToTasksDTO(submissTaskEntity, activeSD, userMap);

    if (showUserTasks) {
      // Pass values to the top object due to angular limitation to filter on 3rd level
      for (SubmissTaskDTO submissTaskDTO : submissTaskDTOList) {
        submissTaskDTO
          .setUserToEditDTO(usersService.getSpecificUser(submissTaskDTO.getUserToEdit()));
        submissTaskDTO.setFirstName(submissTaskDTO.getUserToEditDTO().getFirstName());
        submissTaskDTO.setLastName(submissTaskDTO.getUserToEditDTO().getLastName());
        submissTaskDTO.setEmail(submissTaskDTO.getUserToEditDTO().getEmail());
        submissTaskDTO.setId(submissTaskDTO.getUserToEditDTO().getUsername());
        submissTaskDTO
          .setMainDepartmentStr(submissTaskDTO.getUserToEditDTO().getMainDepartmentStr());
        submissTaskDTO
          .setDirectoratesStr(submissTaskDTO.getUserToEditDTO().getDirectorateShortNames());
        submissTaskDTO.setTenantName(submissTaskDTO.getUserToEditDTO().getTenantName());
        submissTaskDTO.setRole(submissTaskDTO.getUserToEditDTO().getRole());
      }
    }

    if (showUserTasks && currentUser.getTenant().getIsMain()) {
      // If we have user tasks and the tenant is the main tenant, keep all the tasks of the main
      // tenant and all the Admin registration applications of the other tenants. Remove the rest.
      for (Iterator<SubmissTaskDTO> iterator = submissTaskDTOList.iterator();
        iterator.hasNext(); ) {
        SubmissTaskDTO submissTaskDTO = iterator.next();
        if (!submissTaskDTO.getDescription().equals(TaskTypes.REGISTRATION_APPLICATION)
          && !submissTaskDTO.getRole().equals(ADMINISTRATION)
          && !submissTaskDTO.getUserToEditDTO().getTenant().getIsMain()) {
          iterator.remove();
        }
      }
    }
    return submissTaskDTOList;
  }

  @Override
  public SubmissTaskDTO getCompanyTask(String companyId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getCompanyTask, Parameters: companyId: {0}",
      companyId);

    SubmissTasksEntity submissTaskEntity =
      new JPAQueryFactory(em).select(qSubmissTask).from(qSubmissTask)
        .where(qSubmissTask.company.id.eq(companyId)
          .and(qSubmissTask.description.eq(TaskTypes.PROOF_REQUEST))
          .and(qSubmissTask.submission.isNull())).fetchOne();

    SubmissTaskDTO submissTaskDTO = new SubmissTaskDTO();

    if (submissTaskEntity != null) {
      submissTaskDTO = SubmissTaskDTOMapper.INSTANCE.tasksToTasksDTO(submissTaskEntity, null, null);
      // Set user's first and last name in submissTaskDTO
      SubmissUserDTO user = usersService.getSpecificUser(submissTaskDTO.getCreatedBy());
      submissTaskDTO.setFirstName(user.getFirstName());
      submissTaskDTO.setLastName(user.getLastName());
    }

    return submissTaskDTO;
  }

  @Override
  public void settleTask(String taskId) {

    LOGGER.log(Level.CONFIG,
      "Executing method settleTask, Parameters: taskId: {0}",
      taskId);

    SubmissTasksEntity task = em.find(SubmissTasksEntity.class, taskId);

    if (task != null) {
      settleTask(task, null);
    }

  }

  @Override
  public boolean settleControllingTask(String taskId) {

    LOGGER.log(Level.CONFIG,
      "Executing method settleControllingTask, Parameters: taskId: {0}",
      taskId);

    SubmissTasksEntity task = em.find(SubmissTasksEntity.class, taskId);
    // check if the Task is a ControllingTask
    if (TaskTypes.getControllingTask().contains(task.getDescription())) {
      settleTask(taskId);
      return true;
    }
    return false;
  }

  @Override
  public void undertakeTask(String taskId) {

    LOGGER.log(Level.CONFIG,
      "Executing method undertakeTask, Parameters: taskId: {0}",
      taskId);

    SubmissTasksEntity task = em.find(SubmissTasksEntity.class, taskId);
    task.setUserAssigned(getUserId());
    em.merge(task);
  }

  @Override
  public boolean createTask(SubmissTaskDTO taskDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method createTask, Parameters: taskDTO: {0}",
      taskDTO);

    boolean projectTaskCreated = false;
    if (taskCanBeCreated(taskDTO)) {
      // If Nachweisaufforderung gewünscht company part task is selected then check for company
      // proof status and create task.
      if (taskDTO.getDescription().equals(TaskTypes.PROOF_REQUEST)
        && taskDTO.getSubmission() == null) {
        if (companyService.retrieveCompanyProofStatus(
          taskDTO.getCompany().getId()) != ProofStatus.ALL_PROOF.getValue()) {

          saveTask(taskDTO);

          // Audit for Nachweisaufforderung gewünscht company part
          StringBuilder additionalInfo = new StringBuilder(taskDTO.getCompany().getCompanyName())
            .append("[#]").append(taskDTO.getCompany().getProofStatusFabe()).append("[#]");

          createAuditLog(AuditLevel.COMPANY_LEVEL.name(), AuditEvent.CREATE.name(),
            AuditGroupName.COMPANY.name(), taskDTO.getCompany().getId(),
            additionalInfo.toString(), AuditMessages.PROOF_REQUEST.name());
        }
      } else {

        saveTask(taskDTO);
        projectTaskCreated = true;
      }

    }
    return projectTaskCreated;
  }

  /**
   * Save task.
   *
   * @param taskDTO the task DTO
   */
  private void saveTask(SubmissTaskDTO taskDTO) {

    LOGGER.log(Level.CONFIG, "Executing method saveTask, Parameters: taskDTO: {0}", taskDTO);

    if (taskDTO.getCreatedBy() == null || (taskDTO.getCreatedBy() != null
      && !taskDTO.getCreatedBy().equals(LookupValues.SYSTEM))) {
      taskDTO.setCreatedBy(getUserId());
    }
    taskDTO.setCreatedOn(new Timestamp(new Date().getTime()));
    SubmissTasksEntity taskEntity = SubmissTaskDTOMapper.INSTANCE.taskDTOtoTask(taskDTO);
    em.persist(taskEntity);
  }

  @Override
  public boolean settleTask(String submissionId, String companyId, TaskTypes taskType,
    String userId, Date submitDate) {

    LOGGER.log(Level.CONFIG,
      "Executing method settleTask, Parameters: submissionId: {0}, companyId: {1}, "
        + "taskType: {2}, userId: {3}, submitDate: {4}",
      new Object[]{submissionId, companyId, taskType, userId, submitDate});

    JPAQuery<SubmissTasksEntity> query = new JPAQuery<>(em);
    BooleanBuilder whereClause = new BooleanBuilder();

    SubmissUserDTO currentUser = usersService.getUserById(getUser().getId());

    List<String> userIds = new ArrayList<>();
    String tenantId = currentUser.getTenant().getId();
    List<UserDTO> userDTO = new ArrayList<>();

    // Get all tenant users except for the Admin.
    userDTO.addAll(usersService.getUsersByTenantAndRole(tenantId, Group.DIR.name()));
    userDTO.addAll(usersService.getUsersByTenantAndRole(tenantId, Group.PL.name()));
    userDTO.addAll(usersService.getUsersByTenantAndRole(tenantId, Group.SB.name()));

    if (!currentUser.getTenant().getIsMain()) {
      // If the tenant is not the main tenant, just add the Admin user.
      userDTO.addAll(usersService.getUsersByTenantAndRole(tenantId, Group.ADMIN.name()));
    } else {
      // Case of main tenant.
      if (taskType.equals(TaskTypes.REGISTRATION_APPLICATION)) {
        // If the task type is registration application, add all the Admins and not just the Admin
        // of the main tenant, as the Admin of the main tenant settles the registration application
        // tasks, created from Admins of the other tenants.
        userDTO.addAll(usersService.getUsersByTenantAndRole(null, Group.ADMIN.name()));
      } else {
        // If the task type is not registration application, just add the Admin user.
        userDTO.addAll(usersService.getUsersByTenantAndRole(tenantId, Group.ADMIN.name()));
      }
    }

    // Use userIds in order to check if task exists only on permitted tenant task list.
    for (UserDTO user : userDTO) {
      userIds.add(user.getId());
    }
    // Add also the system for system created tasks (NOT_CLOSED_SUBMISSION, SET_SURCHARGE_PDATE).
    userIds.add(LookupValues.SYSTEM);

    if (!StringUtils.isBlank(submissionId)) {
      whereClause.and(qSubmissTask.submission.id.eq(submissionId));
    }

    if (!StringUtils.isBlank(companyId)) {
      if (StringUtils.isBlank(submissionId) && taskType.equals(TaskTypes.PROOF_REQUEST)) {
        whereClause.and(qSubmissTask.submission.isNull());
      }
      whereClause.and(qSubmissTask.company.id.eq(companyId));
    }

    if (!StringUtils.isBlank(userId)) {
      whereClause.and(qSubmissTask.userToEdit.eq(userId));
    }

    SubmissTasksEntity taskEntity =
      query
        .select(qSubmissTask).from(qSubmissTask).where(whereClause,
        qSubmissTask.description.eq(taskType), qSubmissTask.createdBy.in(userIds))
        .fetchOne();
    if (taskEntity != null) {
      settleTask(taskEntity, submitDate);
      return true;
    } else {
      return false;
    }
  }

  /**
   * Sets the tle task.
   *
   * @param taskEntity the new tle task
   * @param submitDate the submit date
   */
  private void settleTask(SubmissTasksEntity taskEntity, Date submitDate) {

    LOGGER.log(Level.CONFIG,
      "Executing method settleTask, Parameters: taskEntity: {0}",
      taskEntity);

    em.remove(taskEntity);
    createAuditForSettledTasks(taskEntity);

    createNextTask(taskEntity, submitDate);
  }

  /**
   * Creates the next task.
   *
   * @param taskEntity the task entity
   * @param submitDate the submit date
   */
  private void createNextTask(SubmissTasksEntity taskEntity, Date submitDate) {

    LOGGER.log(Level.CONFIG,
      "Executing method createNextTask, Parameters: taskEntity: {0}",
      taskEntity);

    SubmissTaskDTO taskDTO = new SubmissTaskDTO();
    if (taskEntity.getDescription().equals(TaskTypes.PROOF_REQUEST)) {

      if (taskEntity.getCompany() != null) {
        taskDTO.setCompany(companyService.getCompanyById(taskEntity.getCompany().getId()));
      }

      if (taskEntity.getSubmission() != null) {
        taskDTO
          .setSubmission(submissionService.getSubmissionById(taskEntity.getSubmission().getId()));
      }
      taskDTO.setDescription(TaskTypes.PROOF_REQUEST_PL_XY);
      taskDTO.setUserAutoAssigned(taskEntity.getCreatedBy());
      if (submitDate != null) {
        taskDTO.setSubmitDate(submitDate);
        // Check submit date and save the task as Controlling Pendenz.
        if (submitDate.before(new Date())) {
          taskDTO.setType(hybridTask);
        }
      }
      createTask(taskDTO);

    }
  }

  /**
   * Only one task by submission, company and taskType can be created, so check if there is already
   * a similar task It is not possible to create a unique constraint in the DB. MySQL does not
   * support unique constraints with null values
   *
   * @param taskDTO the task DTO
   * @return true, if successful
   */
  private boolean taskCanBeCreated(SubmissTaskDTO taskDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method taskCanBeCreated, Parameters: taskDTO: {0}",
      taskDTO);

    JPAQuery<SubmissTasksEntity> query = new JPAQuery<>(em);
    BooleanBuilder whereClause = new BooleanBuilder();
    String tenantId = null;
    List<String> userIds = new ArrayList<>();
    List<UserDTO> userDTO = new ArrayList<>();
    if (getUser() != null) {
      tenantId = getUser().getAttribute(USER_ATTRIBUTES.TENANT.getValue()).getData();
      // Retrieve specific user tasks when tenant is not main tenant.
      userDTO.addAll(usersService.getUsersByTenantAndRole(tenantId, null));
    }
    for (UserDTO user : userDTO) {
      userIds.add(user.getId());
    }
    // Add also the system for system created tasks (NOT_CLOSED_SUBMISSION, SET_SURCHARGE_PDATE).
    userIds.add(LookupValues.SYSTEM);

    if (taskDTO.getSubmission() != null) {
      whereClause.and(qSubmissTask.submission.id.eq(taskDTO.getSubmission().getId()));
    }

    if (taskDTO.getCompany() != null) {
      if (taskDTO.getSubmission() == null
        && taskDTO.getDescription().equals(TaskTypes.PROOF_REQUEST)) {
        whereClause.and(qSubmissTask.submission.isNull());
      }
      whereClause.and(qSubmissTask.company.id.eq(taskDTO.getCompany().getId()));
    }

    if (taskDTO.getUserToEdit() != null) {
      whereClause.and(qSubmissTask.userToEdit.eq(taskDTO.getUserToEdit()));
    }

    return query.select(qSubmissTask).from(qSubmissTask).where(whereClause,
      qSubmissTask.description.eq(taskDTO.getDescription()), qSubmissTask.createdBy.in(userIds))
      .fetchCount() == 0 ? true : false;

  }

  @Override
  public String getTaskBySubmissionIdAndDescription(String submissionId, TaskTypes description,
    String companyId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getTaskBySubmissionIdAndDescription, Parameters: submissionId: {0}, "
        + "description: {1}, companyId: {2}",
      new Object[]{submissionId, description, companyId});

    JPAQuery<SubmissTasksEntity> query = new JPAQuery<>(em);
    BooleanBuilder whereClause = new BooleanBuilder();
    // Check userId in order to retrieve only tenant tasks.
    String tenantId = null;
    List<String> userIds = new ArrayList<>();
    List<UserDTO> userDTOList = new ArrayList<>();
    if (getUser() != null) {
      tenantId = getUser().getAttribute(USER_ATTRIBUTES.TENANT.getValue()).getData();
      // Retrieve specific user tasks when tenant is not main tenant.
      userDTOList.addAll(usersService.getUsersByTenantAndRole(tenantId, null));
    }
    for (UserDTO user : userDTOList) {
      userIds.add(user.getId());
    }
    // Add also the system for system created tasks (NOT_CLOSED_SUBMISSION, SET_SURCHARGE_PDATE).
    userIds.add(LookupValues.SYSTEM);

    if (submissionId != null) {
      whereClause.and(qSubmissTask.submission.id.eq(submissionId));
    }
    if (companyId != null) {
      whereClause.and(qSubmissTask.company.id.eq(companyId));
    }
    whereClause.and(qSubmissTask.description.eq(description));

    SubmissUserDTO userDTO = new SubmissUserDTO();
    // Return user email according to Task type in order to fill email attributes when the email
    // client opens.
    if (description.equals(TaskTypes.CHECK_TENDERLIST)) {
      userDTO = usersService.getSpecificUser(query.select(qSubmissTask).from(qSubmissTask)
        .where(whereClause, qSubmissTask.description.eq(description)).fetchOne().getCreatedBy());
    } else if (description.equals(TaskTypes.PROOF_REQUEST_PL_XY)) {
      userDTO = usersService.getSpecificUser(query
        .select(qSubmissTask).from(qSubmissTask).where(whereClause,
          qSubmissTask.description.eq(description), qSubmissTask.createdBy.in(userIds))
        .fetchOne().getUserAutoAssigned());
    }
    return userDTO.getEmail();
  }

  @Override
  public void updateHybridTask(String submissionId, String companyId, TaskTypes taskDescription) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateHybridTask, Parameters: companyId: {0}, taskDescription: {1}",
      new Object[]{companyId, taskDescription});

    JPAQuery<SubmissTasksEntity> query = new JPAQuery<>(em);
    BooleanBuilder whereClause = new BooleanBuilder();

    if (!StringUtils.isBlank(submissionId)) {
      whereClause.and(qSubmissTask.submission.id.eq(submissionId));
    }

    if (!StringUtils.isBlank(companyId)) {
      whereClause.and(qSubmissTask.company.id.eq(companyId));
    }

    List<SubmissTasksEntity> taskEntity = query.select(qSubmissTask).from(qSubmissTask)
      .where(whereClause, qSubmissTask.description.eq(taskDescription)).fetch();

    if (taskEntity != null) {
      for (SubmissTasksEntity submissTasksEntity : taskEntity) {
        submissTasksEntity.setType(hybridTask);
        em.merge(submissTasksEntity);
      }
    }
  }

  @Override
  public void automaticallyUpdateTaskType() {

    LOGGER.log(Level.CONFIG, "Executing method automaticallyUpdateTaskType");

    List<SubmissTasksEntity> taskEntities = new JPAQueryFactory(em).selectFrom(qSubmissTask)
      .where(qSubmissTask.description.eq(TaskTypes.PROOF_REQUEST_PL_XY)).fetch();
    for (SubmissTasksEntity taskEntity : taskEntities) {
      if (taskEntity.getSubmitDate() != null && taskEntity.getSubmitDate().before(new Date())) {
        taskEntity.setType(hybridTask);
        em.merge(taskEntity);
      }
    }
  }

  @Override
  public void createProofRequestLogForProjectPart(SubmissTaskDTO taskDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method createProofRequestLogForProjectPart, Parameters: taskDTO: {0}",
      taskDTO);

    if (taskDTO.getDescription().equals(TaskTypes.PROOF_REQUEST)) {
      StringBuilder additionalInfo =
        new StringBuilder(taskDTO.getSubmission().getProject().getProjectName()).append("[#]")
          .append(taskDTO.getSubmission().getProject().getObjectName().getValue1())
          .append("[#]")
          .append(taskDTO.getSubmission().getWorkType().getValue1()
            + taskDTO.getSubmission().getWorkType().getValue2())
          .append("[#]")
          .append(getUser().getAttributeData(LookupValues.USER_ATTRIBUTES.TENANT.getValue()))
          .append("[#]").append(taskDTO.getDescription().name());
      // Create audit log.
      createAuditLog(AuditLevel.PROJECT_LEVEL.name(), AuditEvent.CREATE.name(),
        AuditGroupName.SUBMISSION.name(), taskDTO.getSubmission().getId(),
        additionalInfo.toString(), AuditMessages.PROOF_REQUEST.name());
    }
  }

  @Override
  public void createLogForSettledProofRequestTasks(SubmissionDTO submissionDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method createLogForSettledProofRequestTasks, Parameters: submissionDTO: {0}",
      submissionDTO);

    SubmissionEntity submissionEntity = SubmissionMapper.INSTANCE.toSubmission(submissionDTO);
    // Set the audit event value.
    String auditEvent = AuditEvent.TODO_TASK_SETTLED.name();
    if (TaskTypes.getControllingTask().contains(TaskTypes.PROOF_REQUEST)) {
      auditEvent = AuditEvent.CONTROLLING_TASK_SETTLED.name();
    }
    if (TaskTypes.getSubmissionRelatedTasks().contains(TaskTypes.PROOF_REQUEST)) {
      // Get the work type information.
      MasterListValueHistoryEntity workType = new JPAQueryFactory(em)
        .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
        .where(qMasterListValueHistoryEntity.masterListValueId.eq(submissionEntity.getWorkType())
          .and(qMasterListValueHistoryEntity.toDate.isNull()))
        .fetchOne();
      String workTypeValue2 = (StringUtils.isEmpty(workType.getValue2())) ? StringUtils.EMPTY
        : " " + workType.getValue2();
      // Get the object information.
      MasterListValueHistoryEntity objectEntity = new JPAQueryFactory(em)
        .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
        .where(qMasterListValueHistoryEntity.masterListValueId
          .eq(submissionEntity.getProject().getObjectName())
          .and(qMasterListValueHistoryEntity.toDate.isNull()))
        .fetchOne();
      // Collect the additional info for the audit log.
      StringBuilder additionalInfo =
        new StringBuilder(submissionEntity.getProject().getProjectName()).append("[#]")
          .append(objectEntity.getValue1()).append("[#]")
          .append(workType.getValue1() + workTypeValue2).append("[#]")
          .append(getUser().getAttributeData(LookupValues.USER_ATTRIBUTES.TENANT.getValue()))
          .append("[#]").append(TaskTypes.PROOF_REQUEST.name());
      // Create audit log.
      createAuditLog(AuditLevel.PROJECT_LEVEL.name(), auditEvent, AuditGroupName.SUBMISSION.name(),
        submissionEntity.getId(), additionalInfo.toString(), AuditMessages.TASK_SETTLED.name());
    }
  }

  /**
   * Creates the audit for settled tasks.
   *
   * @param taskEntity the task entity
   */
  private void createAuditForSettledTasks(SubmissTasksEntity taskEntity) {

    LOGGER.log(Level.CONFIG,
      "Executing method createAuditForSettledTasks, Parameters: taskEntity: {0}",
      taskEntity);

    String auditEvent = AuditEvent.TODO_TASK_SETTLED.name();
    /*
     * In order to avoid XY and lexicon-variables issues
     * we need to pass the translated value as a String
     * in the description for the additional info
     */
    String descriptionTr = lexiconService
      .getTranslation(taskEntity.getDescription().name(), "de-CH");
    StringBuilder additionalInfo;

    /*
     * The translated value above contains XY as a variable only if
     * taskType is PROOF_REQUEST_PL_XY.
     * In order to avoid these issues we replace the {{XY}}
     * with the translated name of the PL.
     */
    if (descriptionTr.contains("{{XY}}") && taskEntity.getDescription()
      .equals(TaskTypes.PROOF_REQUEST_PL_XY)) {
      String userID = taskEntity.getUserAutoAssigned();
      SubmissUserDTO user = usersService.getSpecificUser(userID);
      descriptionTr = descriptionTr
        .replace("{{XY}}", user.getFirstName() + " " + user.getLastName());
    }

    if (TaskTypes.getControllingTask().contains(taskEntity.getDescription())) {
      auditEvent = AuditEvent.CONTROLLING_TASK_SETTLED.name();
    }

    if (TaskTypes.getSubmissionRelatedTasks().contains(taskEntity.getDescription())
      && !taskEntity.getDescription().equals(TaskTypes.PROOF_REQUEST)
      && taskEntity.getSubmission() != null) {
      MasterListValueHistoryEntity workType = new JPAQueryFactory(em)
        .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
        .where(qMasterListValueHistoryEntity.masterListValueId
          .eq(taskEntity.getSubmission().getWorkType())
          .and(qMasterListValueHistoryEntity.toDate.isNull()))
        .fetchOne();

      MasterListValueHistoryEntity objectEntity = new JPAQueryFactory(em)
        .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
        .where(qMasterListValueHistoryEntity.masterListValueId
          .eq(taskEntity.getSubmission().getProject().getObjectName())
          .and(qMasterListValueHistoryEntity.toDate.isNull()))
        .fetchOne();

      String workTypeValue2 = (StringUtils.isEmpty(workType.getValue2())) ? StringUtils.EMPTY
        : " " + workType.getValue2();

      additionalInfo =
        new StringBuilder(taskEntity.getSubmission().getProject().getProjectName()).append("[#]")
          .append(objectEntity.getValue1()).append("[#]")
          .append(workType.getValue1() + workTypeValue2).append("[#]")
          .append(getUser().getAttributeData(LookupValues.USER_ATTRIBUTES.TENANT.getValue()))
          .append("[#]").append(descriptionTr);

      createAuditLog(AuditLevel.PROJECT_LEVEL.name(), auditEvent, AuditGroupName.SUBMISSION.name(),
        taskEntity.getSubmission().getId(), additionalInfo.toString(),
        AuditMessages.TASK_SETTLED.name());
    }

    if (TaskTypes.getCompanyRelatedTasks().contains(taskEntity.getDescription())
      && taskEntity.getCompany() != null && taskEntity.getCompany().getId() != null
      && taskEntity.getSubmission() == null) {

      additionalInfo =
        new StringBuilder(taskEntity.getCompany().getCompanyName()).append("[#]")
          .append(taskEntity.getCompany().getProofStatusFabe()).append("[#]")
          .append(descriptionTr);

      createAuditLog(AuditLevel.COMPANY_LEVEL.name(), auditEvent, AuditGroupName.COMPANY.name(),
        taskEntity.getCompany().getId(), additionalInfo.toString(),
        AuditMessages.TASK_SETTLED.name());
    }

  }

  /**
   * Creates the audit log.
   *
   * @param auditLevel the audit level
   * @param auditEvent the audit event
   * @param auditGroupName the audit group name
   * @param referenceId the reference id
   * @param additionalInfo the additional info
   * @param message the message
   */
  private void createAuditLog(String auditLevel, String auditEvent, String auditGroupName,
    String referenceId, String additionalInfo, String message) {

    LOGGER.log(Level.CONFIG,
      "Executing method createAuditLog, Parameters: auditLevel: {0}, auditEvent: {1}, "
        + "auditGroupName: {2}, referenceId: {3}, additionalInfo: {4}, message: {5}",
      new Object[]{auditLevel, auditEvent, auditGroupName, referenceId, additionalInfo, message});

    auditBean.createLogAudit(auditLevel, auditEvent, auditGroupName,
      message, getUser().getId(), referenceId, null, additionalInfo,
      LookupValues.EXTERNAL_LOG);
  }

  @Override
  public Set<ValidationError> settleTaskOptimisticLock(String taskId) {

    LOGGER.log(Level.CONFIG,
      "Executing method settleTaskOptimisticLock, Parameters: taskId: {0}", taskId);
    Set<ValidationError> optimisticLockErrors = new HashSet<>();
    SubmissTasksEntity taskEntity = em.find(SubmissTasksEntity.class, taskId);
    if (taskEntity == null) {
      optimisticLockErrors
        .add(new ValidationError(ValidationMessages.TASK_COMPLETED_ERROR_FIELD,
          ValidationMessages.TASK_COMPLETED));
    }
    return optimisticLockErrors;
  }
}
