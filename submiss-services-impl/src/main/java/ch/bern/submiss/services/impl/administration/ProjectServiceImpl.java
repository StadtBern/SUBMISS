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

import ch.bern.submiss.services.api.administration.ProjectService;
import ch.bern.submiss.services.api.administration.SubmissionService;
import ch.bern.submiss.services.api.administration.UserAdministrationService;
import ch.bern.submiss.services.api.constants.AuditEvent;
import ch.bern.submiss.services.api.constants.AuditGroupName;
import ch.bern.submiss.services.api.constants.AuditLevel;
import ch.bern.submiss.services.api.constants.AuditMessages;
import ch.bern.submiss.services.api.constants.CategorySD;
import ch.bern.submiss.services.api.constants.CommissionProcurementProposalReservation;
import ch.bern.submiss.services.api.constants.ConstructionPermit;
import ch.bern.submiss.services.api.constants.LoanApproval;
import ch.bern.submiss.services.api.constants.Process;
import ch.bern.submiss.services.api.constants.ProcessTypeShortCode;
import ch.bern.submiss.services.api.constants.SecurityOperation;
import ch.bern.submiss.services.api.constants.TenderStatus;
import ch.bern.submiss.services.api.dto.DepartmentHistoryDTO;
import ch.bern.submiss.services.api.dto.DirectorateHistoryDTO;
import ch.bern.submiss.services.api.dto.FilterDTO;
import ch.bern.submiss.services.api.dto.MasterListValueHistoryDTO;
import ch.bern.submiss.services.api.dto.ProjectDTO;
import ch.bern.submiss.services.api.dto.SearchDTO;
import ch.bern.submiss.services.api.dto.SubmissionDTO;
import ch.bern.submiss.services.api.dto.TenderDTO;
import ch.bern.submiss.services.api.exceptions.AuthorisationException;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.api.util.ValidationMessages;
import ch.bern.submiss.services.impl.mappers.DepartmentHistoryMapper;
import ch.bern.submiss.services.impl.mappers.DepartmentMapper;
import ch.bern.submiss.services.impl.mappers.MasterListValueHistoryMapper;
import ch.bern.submiss.services.impl.mappers.MasterListValueMapper;
import ch.bern.submiss.services.impl.mappers.ProjectMapper;
import ch.bern.submiss.services.impl.mappers.SubmissionMapper;
import ch.bern.submiss.services.impl.model.CompanyEntity;
import ch.bern.submiss.services.impl.model.DepartmentEntity;
import ch.bern.submiss.services.impl.model.DepartmentHistoryEntity;
import ch.bern.submiss.services.impl.model.MasterListValueEntity;
import ch.bern.submiss.services.impl.model.MasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.ProjectEntity;
import ch.bern.submiss.services.impl.model.QDepartmentEntity;
import ch.bern.submiss.services.impl.model.QDirectorateHistoryEntity;
import ch.bern.submiss.services.impl.model.QMasterListValueEntity;
import ch.bern.submiss.services.impl.model.QMasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.QOfferEntity;
import ch.bern.submiss.services.impl.model.QProcedureHistoryEntity;
import ch.bern.submiss.services.impl.model.QProjectEntity;
import ch.bern.submiss.services.impl.model.QSubmissionEntity;
import ch.bern.submiss.services.impl.model.QSubmittentEntity;
import ch.bern.submiss.services.impl.model.QTenderStatusHistoryEntity;
import ch.bern.submiss.services.impl.model.SubmissionEntity;
import ch.bern.submiss.services.impl.model.TenantEntity;
import com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.SQLExpressions;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

/**
 * The Class ProjectServiceImpl.
 */
@Transactional
@OsgiServiceProvider(classes = {ProjectService.class})
@Singleton
public class ProjectServiceImpl extends BaseService implements ProjectService {

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER = Logger.getLogger(ProjectServiceImpl.class.getName());
  /**
   * The user administration service.
   */
  @Inject
  protected UserAdministrationService userAdmService;
  /**
   * The q master list value history entity.
   */
  QMasterListValueHistoryEntity qMasterListValueHistoryEntity =
    QMasterListValueHistoryEntity.masterListValueHistoryEntity;
  /**
   * The q master list value entity.
   */
  QMasterListValueEntity qMasterListValueEntity = QMasterListValueEntity.masterListValueEntity;
  /**
   * The q department entity.
   */
  QDepartmentEntity qDepartmentEntity = QDepartmentEntity.departmentEntity;
  /**
   * The q submittent entity.
   */
  QSubmittentEntity qSubmittentEntity = QSubmittentEntity.submittentEntity;
  /**
   * The q tender status history entity.
   */
  QTenderStatusHistoryEntity qTenderStatusHistoryEntity =
    QTenderStatusHistoryEntity.tenderStatusHistoryEntity;
  /**
   * The q offer entity.
   */
  QOfferEntity qOfferEntity = QOfferEntity.offerEntity;
  /**
   * The submission.
   */
  QSubmissionEntity submission = QSubmissionEntity.submissionEntity;
  /**
   * The project.
   */
  QProjectEntity project = QProjectEntity.projectEntity;
  /**
   * The object name.
   */
  QMasterListValueEntity objectName = QMasterListValueEntity.masterListValueEntity;
  /**
   * The object name history.
   */
  QMasterListValueHistoryEntity objectNameHistory =
    QMasterListValueHistoryEntity.masterListValueHistoryEntity;
  /**
   * The work type.
   */
  QMasterListValueEntity workType = QMasterListValueEntity.masterListValueEntity;
  /**
   * The work type history.
   */
  QMasterListValueHistoryEntity workTypeHistory =
    QMasterListValueHistoryEntity.masterListValueHistoryEntity;
  /**
   * The process type.
   */
  QMasterListValueEntity processType = QMasterListValueEntity.masterListValueEntity;
  /**
   * The process type history.
   */
  QMasterListValueHistoryEntity processTypeHistory =
    QMasterListValueHistoryEntity.masterListValueHistoryEntity;
  /**
   * The q submission entity.
   */
  QSubmissionEntity qSubmissionEntity = QSubmissionEntity.submissionEntity;
  /**
   * The q procedure history entity.
   */
  QProcedureHistoryEntity qProcedureHistoryEntity = QProcedureHistoryEntity.procedureHistoryEntity;
  /**
   * The submission service.
   */
  @Inject
  private SubmissionService submissionService;
  /**
   * The cache bean.
   */
  @Inject
  private CacheBean cacheBean;
  /**
   * The audit.
   */
  @Inject
  private AuditBean audit;

  @Override
  public ProjectDTO getProjectById(String id) {

    LOGGER.log(Level.CONFIG,
      "Executing method getProjectById, Parameters: id: {0}",
      id);

    ProjectEntity projectEntity = em.find(ProjectEntity.class, id);

    if (projectEntity == null) {
      // If no project entity has been found, it means that the given id belongs to a submission and
      // not to a project. This happens when the user navigates to the project view via the
      // breadcrumb. In that case we get the project through the submission.
      projectEntity =
          ProjectMapper.INSTANCE.toProject(submissionService.getSubmissionById(id).getProject());
    }

    ProjectDTO projectDTO = ProjectMapper.INSTANCE.toProjectDTO(projectEntity);
    DepartmentHistoryEntity departmentEntity =
      new JPAQueryFactory(em).select(qDepartmentHistoryEntity).from(qDepartmentHistoryEntity)
        .where(qDepartmentHistoryEntity.departmentId.eq(projectEntity.getDepartment())
          .and(qDepartmentHistoryEntity.toDate.isNull()))
        .fetchOne();
    DepartmentHistoryDTO departmentDTO =
      DepartmentHistoryMapper.INSTANCE
        .toDepartmentHistoryDTO(departmentEntity, cacheBean.getActiveDirectorateHistorySD());
    projectDTO.setDepartment(departmentDTO);

    MasterListValueHistoryEntity objectEntity = new JPAQueryFactory(em)
      .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
      .where(qMasterListValueHistoryEntity.masterListValueId.eq(projectEntity.getObjectName())
        .and(qMasterListValueHistoryEntity.toDate.isNull()))
      .fetchOne();
    MasterListValueHistoryDTO objectDTO =
      MasterListValueHistoryMapper.INSTANCE.toMasterListValueHistoryDTO(objectEntity);
    projectDTO.setObjectName(objectDTO);

    MasterListValueHistoryEntity procedure = new JPAQueryFactory(em)
      .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
      .where(qMasterListValueHistoryEntity.masterListValueId.eq(projectEntity.getProcedure())
        .and(qMasterListValueHistoryEntity.toDate.isNull()))
      .fetchOne();
    MasterListValueHistoryDTO procedureDTO =
      MasterListValueHistoryMapper.INSTANCE.toMasterListValueHistoryDTO(procedure);
    projectDTO.setProcedure(procedureDTO);

    return projectDTO;
  }

  @Override
  public String createProject(ProjectDTO project) {

    LOGGER.log(Level.CONFIG, "Executing method createProject, Parameters: project {0}",
      project);

    project.setCreatedBy(getUserId());
    project.setCreatedOn(new Timestamp(new Date().getTime()));
    ProjectEntity projectEntity = ProjectMapper.INSTANCE.toProject(project);
    if (project.getPmExternal() != null) {
      projectEntity.setPmExternal(em.find(CompanyEntity.class, project.getPmExternal().getId()));
    }
    projectEntity.setPmDepartmentName(project.getPmDepartmentName());

    if(projectEntity.getProjectNumber() != null
      && projectEntity.getProjectNumber().trim().isEmpty()){
      projectEntity.setProjectNumber(null);
    }

    if (project.getDepartment() != null) {
      DepartmentHistoryDTO departmentDTO =
        cacheBean.getValue(project.getDepartment().getId(), null);
      project.setDepartment(departmentDTO);
      if (departmentDTO != null) {
        project.getDepartment().setDirectorate(cacheBean
          .getValueDirectorate(departmentDTO.getDirectorate().getDirectorateId().getId(), null));
        DepartmentEntity dep =
          DepartmentMapper.INSTANCE.toDepartment(departmentDTO.getDepartmentId());
        projectEntity.setDepartment(dep);
      }
    }

    if (project.getObjectName() != null) {
      MasterListValueHistoryDTO objectDTO = cacheBean.getValue(CategorySD.OBJECT.getValue(),
          project.getObjectName().getId(), null, null, null);
      if (objectDTO != null) {
        projectEntity.setObjectName(
            MasterListValueMapper.INSTANCE.toMasterListValue(objectDTO.getMasterListValueId()));
      }
    }
    String usersTenant =
      getUser().getAttribute(LookupValues.USER_ATTRIBUTES.TENANT.getValue()).getData();
    TenantEntity tenantEntity = em.find(TenantEntity.class, usersTenant);
    projectEntity.setTenant(tenantEntity);

    em.persist(projectEntity);

    // Create log entry    
    StringBuilder projectVars = new StringBuilder(project.getProjectName()).append("[#]")
      .append(project.getObjectName().getValue1()).append("[#]").append("[#]")
      .append(usersTenant).append("[#]");

    audit.createLogAudit(AuditLevel.PROJECT_LEVEL.name(), AuditEvent.CREATE.name(),
      AuditGroupName.PROJECT.name(), AuditMessages.CREATE_PROJECT.name(), getUser().getId(),
      projectEntity.getId(), null, projectVars.toString(), LookupValues.EXTERNAL_LOG);

    return projectEntity.getId();
  }

  @Override
  public void deleteProject(String id) {

    LOGGER.log(Level.CONFIG,
      "Executing method deleteProject, Parameters: id: {0}",
      id);

    security.isPermittedOperationForUser(getUserId(),
      SecurityOperation.PROJECT_DELETE.getValue(), id);

    ProjectEntity projectEntity = em.find(ProjectEntity.class, id);

    if (projectEntity != null) {
      List<SubmissionEntity> submissions = new JPAQueryFactory(em).select(qSubmissionEntity)
        .from(qSubmissionEntity).where(qSubmissionEntity.project.eq(projectEntity)).fetch();
      if (!submissions.isEmpty()) {
        for (SubmissionEntity s : submissions) {
          submissionService.deleteSubmission(s.getId());
        }
      }
      em.remove(projectEntity);
      // Create log entry
      MasterListValueHistoryEntity objectEntity = new JPAQueryFactory(em)
        .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
        .where(qMasterListValueHistoryEntity.masterListValueId.eq(projectEntity.getObjectName())
          .and(qMasterListValueHistoryEntity.toDate.isNull()))
        .fetchOne();

      StringBuilder projectVars = new StringBuilder(projectEntity.getProjectName()).append("[#]")
        .append(objectEntity.getValue1()).append("[#]").append("[#]")
        .append(userAdmService.getUserById(getUser().getId()).getTenant().getId()).append("[#]");

      audit.createLogAudit(AuditLevel.PROJECT_LEVEL.name(), AuditEvent.DELETE.name(),
        AuditGroupName.PROJECT.name(), AuditMessages.DELETE_PROJECT.name(), getUser().getId(), id,
        null, projectVars.toString(), LookupValues.EXTERNAL_LOG);

    }
  }

  @Override
  public List<TenderDTO> search(SearchDTO searchDTO, int page, int pageItems, String sortColumn,
    String sortType) throws AuthorisationException {

    LOGGER.log(Level.CONFIG,
      "Executing method search, Parameters: searchDTO {0}, page: {1}, pageItems: {2}, "
        + "sortColumn: {3}, rotType: {4}",
      new Object[]{searchDTO, page, pageItems, sortColumn, sortType});

    UserDTO user = getUser();

    security.isPermittedOperationForUser(getUserId(),
      SecurityOperation.PROJECT_SEARCH.getValue(), null);
    // check if this operation is allowed for this user
    security.requireOperation(user.getId(), SecurityOperation.PROJECT_SEARCH.getValue());

    List<SubmissionEntity> submissions = filter(searchDTO, page, pageItems, sortColumn, sortType);
    List<TenderDTO> tenderDTOList = new ArrayList<>(submissions.size());
    for (SubmissionEntity s : submissions) {
      Timestamp statusDate = null;
      Timestamp creationDate = null;
      Timestamp objectDate = null;
      // Substituting the getDateOfCompletedOrCanceledStatus, getDateOfCreationStatusOfSubmission
      // and getRefernceDateForObject SubmissionService functions with a single iteration, in order
      // to improve the performance.
      if (s.getId() != null) {
        statusDate = submissionService.getDateOfCompletedOrCanceledStatus(s.getId());
        creationDate = submissionService.getDateOfCreationStatusOfSubmission(s.getId());
        objectDate = submissionService.getRefernceDateForObject(s.getId());
      }
      TenderDTO tenderDTO = new TenderDTO();
      SubmissionDTO submissionDTO = SubmissionMapper.INSTANCE.toSubmissionDTO(s);
      tenderDTO.setId(submissionDTO.getId());
      tenderDTO.setVersion(submissionDTO.getVersion());
      tenderDTO.setProjectId(submissionDTO.getProject().getId());
      tenderDTO.setProjectVersion(submissionDTO.getProject().getVersion());
      tenderDTO.setProjectName(submissionDTO.getProject().getProjectName());
      // setting Object
      MasterListValueHistoryDTO objectNameDTO =
        cacheBean.getValue(s.getProject().getObjectName().getMasterList().getCategoryType(),
          s.getProject().getObjectName().getId(), statusDate, creationDate, objectDate);
      submissionDTO.getProject().setObjectName(objectNameDTO);
      tenderDTO.setObjectName(submissionDTO.getProject().getObjectName().getValue1());

      if (s.getWorkType() != null) {
        // setting WorkType
        MasterListValueHistoryDTO workTypeDTO =
          cacheBean.getValue(s.getWorkType().getMasterList().getCategoryType(),
            s.getWorkType().getId(), statusDate, creationDate, null);
        submissionDTO.setWorkType(workTypeDTO);

        tenderDTO.setWorkType(submissionDTO.getWorkType().getValue1() + LookupValues.SPACE
          + submissionDTO.getWorkType().getValue2());
      }
      tenderDTO.setDescription(submissionDTO.getDescription());
      tenderDTO.setProccess(submissionDTO.getProcess());

      if (s.getProcessType() != null) {
        // setting proccessType
        MasterListValueHistoryDTO processTypeDTO =
          cacheBean.getValue(s.getProcessType().getMasterList().getCategoryType(),
            s.getProcessType().getId(), statusDate, creationDate, null);
        submissionDTO.setProcessType(processTypeDTO);
        if (submissionDTO.getProcessType().getValue1() != null) {
          tenderDTO.setProccessType(submissionDTO.getProcessType().getValue1());

        }
      }
      // setting department
      DepartmentHistoryDTO departmentDTO =
        cacheBean.getValue(s.getProject().getDepartment().getId(), statusDate);
      submissionDTO.getProject().setDepartment(departmentDTO);
      // Setting directorate to department.
      DirectorateHistoryDTO directorate = cacheBean.getValueDirectorate(
        departmentDTO.getDirectorate().getDirectorateId().getId(), statusDate);
      submissionDTO.getProject().getDepartment().setDirectorate(directorate);
      tenderDTO.setManDep(submissionDTO.getProject().getDepartment().getDirectorate().getShortName()
        + LookupValues.SLASH + submissionDTO.getProject().getDepartment().getShortName());
      tenderDTO.setSubmissionDeadline(
        (submissionDTO.getSecondDeadline() != null) ? submissionDTO.getSecondDeadline()
          : submissionDTO.getFirstDeadline());
      if (submissionDTO.getPmDepartmentName() != null) {
        tenderDTO.setProjectManagerOfDep(submissionDTO.getPmDepartmentName());
      } else if (submissionDTO.getProject().getPmDepartmentName() != null) {
        tenderDTO.setProjectManagerOfDep(submissionDTO.getProject().getPmDepartmentName());
      }
      tenderDTO.setSubmissionDeadline(
        submissionDTO.getSecondDeadline() != null ? submissionDTO.getSecondDeadline()
          : submissionDTO.getFirstDeadline());

      tenderDTOList.add(tenderDTO);
    }
    return tenderDTOList;
  }

  @Override
  public List<String> getProjectsByObjectId(String objectId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getProjectsByObjectId, Parameters: objectId: {0}",
      objectId);

    UserDTO user = getUser();
    JPAQuery<ProjectEntity> query = new JPAQuery<>(em);
    QProjectEntity entity = QProjectEntity.projectEntity;
    QMasterListValueHistoryEntity qMasterListValueHistory =
      QMasterListValueHistoryEntity.masterListValueHistoryEntity;

    BooleanBuilder whereClause = new BooleanBuilder();

    security.securityCheckProjectSearch(user, entity, null, whereClause);

    whereClause.and(entity.objectName.id.eq(new JPAQueryFactory(em)
      .select(qMasterListValueEntity.id).from(
        qMasterListValueEntity)
      .where(qMasterListValueEntity.id
        .eq(new JPAQueryFactory(em).select(qMasterListValueHistory.masterListValueId.id)
          .from(qMasterListValueHistory).where(qMasterListValueHistory.id.eq(objectId)
            .and(qMasterListValueHistory.toDate.isNull()))
          .fetchOne()))
      .fetchOne()));

    return query.select(entity.projectName).distinct().from(entity).where(whereClause).fetch();
  }

  @Override
  public List<ProjectDTO> getProjectsByObjectsIDs(List<String> objectsIDs) {

    LOGGER.log(Level.CONFIG,
      "Executing method getProjectsByObjectsIDs, Parameters: objectsIDs: {0}",
      objectsIDs);

    UserDTO user = getUser();
    JPAQuery<ProjectEntity> query = new JPAQuery<>(em);
    QProjectEntity projectEntity = QProjectEntity.projectEntity;
    QMasterListValueHistoryEntity qMasterListValueHistory =
      QMasterListValueHistoryEntity.masterListValueHistoryEntity;

    BooleanBuilder whereClause = new BooleanBuilder();
    security.securityCheckProjectSearch(user, projectEntity, null, whereClause);

    whereClause.and(projectEntity.objectName.id
      .in(query.select(qMasterListValueEntity.id).from(qMasterListValueEntity)
        .where(qMasterListValueEntity.id.in(query
          .select(qMasterListValueHistory.masterListValueId.id).from(qMasterListValueHistory)
          .where(qMasterListValueHistory.id.in(objectsIDs)).fetch()))
        .fetch()));

    List<ProjectEntity> projectEntityList =
      query.select(projectEntity).distinct().from(projectEntity).where(whereClause).fetch();
    return ProjectMapper.INSTANCE.toProjectDTO(projectEntityList);
  }

  @Override
  public List<String> getAllProjectsNames(String excludedProject) {

    LOGGER.log(Level.CONFIG,
      "Executing method getAllProjectsNames, Parameters: excludedProject: {0}",
      excludedProject);

    UserDTO user = getUser();
    JPAQuery<ProjectEntity> query = new JPAQuery<>(em);
    QProjectEntity entity = QProjectEntity.projectEntity;

    BooleanBuilder whereClause = new BooleanBuilder();

    security.securityCheckProjectSearch(user, entity, null, whereClause);

    if (excludedProject != null) {
      whereClause.and(entity.id.ne(excludedProject));
    }

    return query.orderBy(entity.projectName.asc()).select(entity.projectName).distinct()
      .from(entity).where(whereClause).fetch();
  }

  @Override
  public List<ProjectDTO> getAllProjects() {

    LOGGER.log(Level.CONFIG, "Executing method getAllProjects");

    UserDTO user = getUser();
    JPAQuery<ProjectEntity> query = new JPAQuery<>(em);
    QProjectEntity entity = QProjectEntity.projectEntity;

    BooleanBuilder whereClause = new BooleanBuilder();

    security.securityCheckProjectSearch(user, entity, null, whereClause);

    List<ProjectEntity> projectEntities = query.orderBy(entity.projectName.asc()).select(entity)
      .distinct().from(entity).where(whereClause).fetch();
    return ProjectMapper.INSTANCE.toProjectDTO(projectEntities);
  }

  @Override
  public List<String> getAllProjectsCreditNos() {

    LOGGER.log(Level.CONFIG, "Executing method getAllProjectsCreditNos");

    UserDTO user = getUser();
    JPAQuery<ProjectEntity> query = new JPAQuery<>(em);
    QProjectEntity entity = QProjectEntity.projectEntity;

    BooleanBuilder whereClause = new BooleanBuilder();

    security.securityCheckProjectSearch(user, entity, null, whereClause);

    return query.orderBy(entity.projectNumber.asc()).select(entity.projectNumber).distinct()
      .from(entity).where(whereClause.and(entity.projectNumber.isNotNull())).fetch();
  }

  @Override
  public List<String> getAllProjectManagers() {

    LOGGER.log(Level.CONFIG, "Executing method getAllProjectManagers");

    UserDTO user = getUser();
    JPAQuery<ProjectEntity> query = new JPAQuery<>(em);
    QProjectEntity entity = QProjectEntity.projectEntity;

    BooleanBuilder whereClause = new BooleanBuilder();

    security.securityCheckProjectSearch(user, entity, null, whereClause);

    return query.orderBy(entity.pmDepartmentName.asc()).select(entity.pmDepartmentName).distinct()
      .from(entity).where(whereClause.and(entity.pmDepartmentName.isNotNull())).fetch();
  }

  @Override
  public Set<ValidationError> updateProject(ProjectDTO project) {

    LOGGER.log(Level.CONFIG, "Executing method project, Parameters: project: {0}",
      project);

    // Check for Optimistic Locking if project is deleted by another user
    Set<ValidationError> optimisticLockErrors = optimisticLockDeleteCheck(project.getId());

    if (!optimisticLockErrors.isEmpty()) {
      return optimisticLockErrors;
    }

    ProjectEntity projectEntity = ProjectMapper.INSTANCE.toProject(project);
    if (project.getPmExternal() != null) {
      projectEntity.setPmExternal(em.find(CompanyEntity.class, project.getPmExternal().getId()));
    }
    if (project.getDepartment() != null) {
      DepartmentHistoryDTO departmentDTO =
        cacheBean.getValue(project.getDepartment().getId(), null);
      project.setDepartment(departmentDTO);
      if (departmentDTO != null) {
        project.getDepartment().setDirectorate(cacheBean
          .getValueDirectorate(departmentDTO.getDirectorate().getDirectorateId().getId(), null));
        DepartmentEntity dep =
          DepartmentMapper.INSTANCE.toDepartment(departmentDTO.getDepartmentId());
        projectEntity.setDepartment(dep);
      }
    }

    if (project.getObjectName() != null) {
      MasterListValueHistoryDTO objectDTO = cacheBean.getValue(CategorySD.OBJECT.getValue(),
          project.getObjectName().getId(), null, null, null);
      if (objectDTO != null) {
        projectEntity.setObjectName(
            MasterListValueMapper.INSTANCE.toMasterListValue(objectDTO.getMasterListValueId()));
      }
    }

    projectEntity.setPmDepartmentName(project.getPmDepartmentName());

    String usersTenant =
      getUser().getAttribute(LookupValues.USER_ATTRIBUTES.TENANT.getValue()).getData();
    TenantEntity tenantEntity = em.find(TenantEntity.class, usersTenant);
    projectEntity.setTenant(tenantEntity);

    // we need to update fields Projektleitung der Abt, Externe Projektleitung, Verfahrensleitung,
    // Baubewilligung, Kreditbewilligung, GATT / WTO
    // of the related with the project submissions if they are not manually updated
    // (fields Baubewillligung, Kreditbewilligung can not be manually updated by the submission,
    // so no check needed for them)
    // and if the submission is not closed
    // and if the status after Zuschlagsbewertung abgeschlossen and before BeKo Antrag abgeschlossen
    // we need to update field Vorbehalt of the submission if not manually updated
    List<SubmissionEntity> submissionEntityList = new JPAQueryFactory(em).select(submission)
      .from(submission).where(submission.project.id.eq(project.getId())).fetch();
    for (SubmissionEntity submissionEntity : submissionEntityList) {
      // get the status of the submission
      TenderStatus submissionStatus = TenderStatus.fromValue(submissionEntity.getStatus());
      // perform changes only if the submission is not closed
      if (!compareCurrentVsSpecificStatus(submissionStatus, TenderStatus.PROCEDURE_COMPLETED)) {
        // and only if the field is not manually updated
        if (!submissionEntity.getIsPmDepartmentNameUpdated()) {
          submissionEntity.setPmDepartmentName(projectEntity.getPmDepartmentName());
        }
        if (!submissionEntity.getIsPmExternalUpdated()) {
          submissionEntity.setPmExternal(projectEntity.getPmExternal());
        }
        if (!submissionEntity.getIsProcedureUpdated()) {
          submissionEntity.setProcedure(projectEntity.getProcedure());
        }
        if (!submissionEntity.getIsGattTwoUpdated()) {
          submissionEntity.setGattTwo(projectEntity.getGattWto());
          // If the gattTwo is set to false, then a check must be made if a to do task exists to set
          // the publicationDateAward of the submission. If it exists then it must be deleted and if
          // the other preconditions for the automatically closure of the submission apply, then the
          // submission must be closed.
          if (!projectEntity.getGattWto()) {
            submissionService.checkToDeleteTaskAndCloseSubmission(
              SubmissionMapper.INSTANCE.toSubmissionDTO(submissionEntity));
          }
        }
        // fields Baubewillligung, Kreditbewilligung can not be manually updated by the submission,
        // so no check needed for them
        submissionEntity.setConstructionPermit(projectEntity.getConstructionPermit());
        submissionEntity.setLoanApproval(projectEntity.getLoanApproval());
      }
      // if the status after Zuschlagsbewertung abgeschlossen and before BeKo Antrag abgeschlossen
      // and if the field Vorbehalt of the submission if not manually updated
      if (compareCurrentVsSpecificStatus(submissionStatus, TenderStatus.AWARD_EVALUATION_CLOSED)
        && !compareCurrentVsSpecificStatus(submissionStatus,
        TenderStatus.COMMISSION_PROCUREMENT_PROPOSAL_CLOSED)
        && submissionEntity.getCommissionProcurementProposalReservation() != null
        && (submissionEntity.getCommissionProcurementProposalReservation()
        .equals(CommissionProcurementProposalReservation.RESERVATION_CONSTRUCTION.getValue())
        || submissionEntity.getCommissionProcurementProposalReservation()
        .equals(CommissionProcurementProposalReservation.RESERVATION_LOAN.getValue())
        || submissionEntity.getCommissionProcurementProposalReservation()
        .equals(CommissionProcurementProposalReservation.RESERVATION_CONSTRUCTION_AND_LOAN
          .getValue())
        || submissionEntity.getCommissionProcurementProposalReservation()
        .equals(CommissionProcurementProposalReservation.RESERVATION_NONE.getValue()))) {
        if (project.getConstructionPermit().equals(ConstructionPermit.PENDING)
          && !project.getLoanApproval().equals(LoanApproval.PENDING)) {
          submissionEntity.setCommissionProcurementProposalReservation(
            CommissionProcurementProposalReservation.RESERVATION_CONSTRUCTION.getValue());
        } else if (!project.getConstructionPermit().equals(ConstructionPermit.PENDING)
          && project.getLoanApproval().equals(LoanApproval.PENDING)) {
          submissionEntity.setCommissionProcurementProposalReservation(
            CommissionProcurementProposalReservation.RESERVATION_LOAN.getValue());
        } else if (project.getConstructionPermit().equals(ConstructionPermit.PENDING)
          && project.getLoanApproval().equals(LoanApproval.PENDING)) {
          submissionEntity.setCommissionProcurementProposalReservation(
            CommissionProcurementProposalReservation.RESERVATION_CONSTRUCTION_AND_LOAN
              .getValue());
        } else {
          submissionEntity.setCommissionProcurementProposalReservation(
            CommissionProcurementProposalReservation.RESERVATION_NONE.getValue());
        }
      }
    }
    em.merge(projectEntity);
    return optimisticLockErrors;
  }

  /**
   * Check if project is deleted by another user.
   *
   * @param id the project id
   * @return the error
   */
  private Set<ValidationError> optimisticLockDeleteCheck(String id) {

    LOGGER.log(Level.CONFIG,
      "Executing method optimisticLockDeleteCheck, Parameters: id: {0}", id);

    Set<ValidationError> optimisticLockErrors = new HashSet<>();
    ProjectEntity projectEntity = em.find(ProjectEntity.class, id);
    if (projectEntity == null) {
      optimisticLockErrors
        .add(new ValidationError("optimisticLockErrorField",
          ValidationMessages.OPTIMISTIC_LOCK_DELETE));
    }
    return optimisticLockErrors;
  }

  /**
   * Filter.
   *
   * @param searchDTO the search DTO
   * @param page the page
   * @param pageItems the page items
   * @param sortColumn the sort column
   * @param sortType the sort type
   * @return the list
   */
  private List<SubmissionEntity> filter(SearchDTO searchDTO, int page, int pageItems,
    String sortColumn, String sortType) {

    LOGGER.log(Level.CONFIG,
      "Executing method filter, Parameters: searchDTO: {0}, page: {1}, "
        + "pageItems: {2}, sortColumn: {3}, sortType: {4}",
      new Object[]{searchDTO, page, pageItems, sortColumn, sortType});

    JPAQuery<Tuple> query = new JPAQuery<>(em);

    // If ExcludedProject null then get submission data for project search, else get project data
    // for daten verschieben
    if (searchDTO.getExcludedProject() == null) {
      query.select(submission, project, objectNameHistory, workTypeHistory, processTypeHistory,
        qDepartmentHistoryEntity);

    } else {
      query.select(project, objectNameHistory).distinct();
    }

    query.from(submission).rightJoin(submission.project, project)
      .leftJoin(submission.project.objectName, objectName)
      .leftJoin(objectName.masterListValueHistory, objectNameHistory)
      .leftJoin(submission.project.department, qDepartmentEntity)
      .leftJoin(qDepartmentEntity.department, qDepartmentHistoryEntity)
      .where(objectNameHistory.toDate.isNull(), workTypeHistory.toDate.isNull(),
        processTypeHistory.toDate.isNull(), qDepartmentHistoryEntity.toDate.isNull(),
        getWhereClause(submission, project, searchDTO));

    // When pageItems -1, the show all results
    if (pageItems != -1) {
      query.offset((page - 1) * pageItems).limit(pageItems);
    }

    /** Apply ordering **/
    if (sortColumn.equals("projectName")) {
      query
        .orderBy(sortType.equals("asc") ? project.projectName.asc() : project.projectName.desc());
    } else if (sortColumn.equals("objectName")) {
      query.orderBy(sortType.equals("asc") ? objectNameHistory.value1.asc()
        : objectNameHistory.value1.desc());
    } else if (sortColumn.equals("workType")) {
      query.orderBy(
        sortType.equals("asc") ? submission.workType.masterListValueHistory.any().value1.asc()
          : submission.workType.masterListValueHistory.any().value1.desc());
    } else if (sortColumn.equals("description")) {
      query.orderBy(
        sortType.equals("asc") ? submission.description.asc() : submission.description.desc());

    } else if (sortColumn.equals("proccess")) {
      query.orderBy(sortType.equals("asc") ? submission.process.asc() : submission.process.desc());
    } else if (sortColumn.equals("proccessType")) {
      query.orderBy(
        sortType.equals("asc") ? submission.processType.masterListValueHistory.any().value1.asc()
          : submission.processType.masterListValueHistory.any().value1.desc());
    } else if (sortColumn.equals("submissionDeadline")) {

      query.orderBy(

        sortType.equals("asc")
          ? new CaseBuilder().when(submission.secondDeadline.isNotNull())
          .then(submission.secondDeadline).otherwise(submission.firstDeadline).asc()
          : new CaseBuilder().when(submission.secondDeadline.isNotNull())
            .then(submission.secondDeadline).otherwise(submission.firstDeadline).desc()

      );

    } else if (sortColumn.equals("manDep")) {
      query.orderBy(sortType.equals("asc") ? project.department.department.any().name.asc()
        : project.department.department.any().name.desc());
    } else if (sortColumn.equals("projectManagerOfDep")) {
      query.orderBy(sortType.equals("asc") ? submission.pmDepartmentName.asc()
        : submission.pmDepartmentName.desc());
    }

    List<ProjectEntity> projectsWithLockedSubmissions = new ArrayList<>();
    List<SubmissionEntity> submissionList = new ArrayList<>();
    for (Tuple entity : query.fetch()) {
      SubmissionEntity submissionEntity = entity.get(submission);
      ProjectEntity projectEntity = entity.get(project);
      if (submissionEntity == null) {
        SubmissionEntity emptySubmission = new SubmissionEntity();
        emptySubmission.setProject(projectEntity);
        submissionList.add(emptySubmission);
      } else if (!security.getLockedSubmissions(getUser()).isEmpty()
        && security.getLockedSubmissions(getUser()).contains(submissionEntity.getId())) {
        if (!projectsWithLockedSubmissions.contains(projectEntity)
          && projectContainsOnlyLockedSubmissions(projectEntity.getId()) && (searchDTO == null
          || (noSubmissionSearchCriteriaSelected(searchDTO)
          && noSubmissionFilterCriteriaSelected(searchDTO.getFilter())
          && noSubmittentCriteriaSelected(searchDTO)))) {
          SubmissionEntity lockedSubmission = new SubmissionEntity();
          lockedSubmission.setProject(projectEntity);
          submissionList.add(lockedSubmission);
          projectsWithLockedSubmissions.add(projectEntity);
        }
      } else {
        submissionList.add(submissionEntity);
      }
    }
    return submissionList;
  }

  /**
   * Gets the where clause.
   *
   * @param entity the entity
   * @param project the project
   * @param searchDTO the search DTO
   * @return the where clause
   */
  private BooleanBuilder getWhereClause(QSubmissionEntity entity, QProjectEntity project,
    SearchDTO searchDTO) {

    LOGGER.log(Level.CONFIG, "Executing method getWhereClause, Parameters: entity: {0}, "
        + "project: {1}, searchDTO: {2}",
      new Object[]{entity, project, searchDTO});

    /**
     * Here we create the whereClause for the query during search. In most cases there is a need to
     * convert from history entities that we get from UI to entities that we have store in DB and
     * then check according to these values.
     **/

    BooleanBuilder whereClause = new BooleanBuilder();

    // add security checks
    UserDTO user = getUser();
    security.securityCheckProjectSearch(user, project, entity, whereClause);

    if (StringUtils.isNotBlank(searchDTO.getObjectId())) {
      MasterListValueHistoryEntity objectHistory =
        new JPAQueryFactory(em).select(qMasterListValueHistoryEntity)
          .from(qMasterListValueHistoryEntity).where(qMasterListValueHistoryEntity.id
          .eq(searchDTO.getObjectId()).and(qMasterListValueHistoryEntity.toDate.isNull()))
          .fetchOne();
      MasterListValueEntity object =
        new JPAQueryFactory(em).select(qMasterListValueEntity).from(qMasterListValueEntity)
          .where(qMasterListValueEntity.id.eq(objectHistory.getMasterListValueId().getId()))
          .fetchOne();
      whereClause.and(project.objectName.eq(object));
    }
    if (CollectionUtils.isNotEmpty(searchDTO.getProjectNames())) {
      whereClause.and(project.projectName.in(searchDTO.getProjectNames()));
    }
    if (StringUtils.isNotEmpty(searchDTO.getProcedureId())) {
      MasterListValueHistoryEntity procedureEntity = new JPAQueryFactory(em)
        .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
        .where(qMasterListValueHistoryEntity.id.eq(searchDTO.getProcedureId())
          .and(qMasterListValueHistoryEntity.toDate.isNull()))
        .fetchOne();
      MasterListValueEntity procedure =
        new JPAQueryFactory(em).select(qMasterListValueEntity).from(qMasterListValueEntity)
          .where(qMasterListValueEntity.id.eq(procedureEntity.getMasterListValueId().getId()))
          .fetchOne();
      whereClause.and(project.procedure.id.eq(procedure.getId()));
    }
    if (CollectionUtils.isNotEmpty(searchDTO.getDepartmentsIDs())) {
      List<DepartmentEntity> departmentEntities = new ArrayList<>();
      for (String departmentId : searchDTO.getDepartmentsIDs()) {
        DepartmentHistoryEntity departmentHistory =
          new JPAQueryFactory(em).select(qDepartmentHistoryEntity).from(qDepartmentHistoryEntity)
            .where(qDepartmentHistoryEntity.id.eq(departmentId)
              .and(qDepartmentHistoryEntity.toDate.isNull()))
            .fetchOne();
        DepartmentEntity departmentEntity =
          new JPAQueryFactory(em).select(qDepartmentEntity).from(qDepartmentEntity)
            .where(qDepartmentEntity.eq(departmentHistory.getDepartmentId())).fetchOne();
        departmentEntities.add(departmentEntity);
      }
      whereClause.and(project.department.in(departmentEntities));
    }
    if (CollectionUtils.isNotEmpty(searchDTO.getWorkTypes())) {
      Set<MasterListValueEntity> workTypeEntities = new HashSet<>();
      for (MasterListValueHistoryDTO workTypeDTO : searchDTO.getWorkTypes()) {
        MasterListValueHistoryEntity workTypeHistoryEntity =
          new JPAQueryFactory(em).select(qMasterListValueHistoryEntity)
            .from(qMasterListValueHistoryEntity).where(qMasterListValueHistoryEntity.id
            .eq(workTypeDTO.getId()).and(qMasterListValueHistoryEntity.toDate.isNull()))
            .fetchOne();
        MasterListValueEntity workTypeEntity = new JPAQueryFactory(em)
          .select(qMasterListValueEntity).from(qMasterListValueEntity)
          .where(
            qMasterListValueEntity.id.eq(workTypeHistoryEntity.getMasterListValueId().getId()))
          .fetchOne();
        workTypeEntities.add(workTypeEntity);

      }
      whereClause.and(entity.workType.in(workTypeEntities));
    }

    if (searchDTO.getExcludedProject() != null) {
      whereClause.and(project.id.ne(searchDTO.getExcludedProject()));
    }

    List<Process> processes = new ArrayList<>();
    if (searchDTO.getSelective() != null && searchDTO.getSelective().equals(true)) {
      processes.add(Process.SELECTIVE);
    }
    if (searchDTO.getOpen() != null && searchDTO.getOpen().equals(true)) {
      processes.add(Process.OPEN);
    }
    if (searchDTO.getInvitation() != null && searchDTO.getInvitation().equals(true)) {
      processes.add(Process.INVITATION);
    }
    if (searchDTO.getNegotiatedProcedure() != null
      && searchDTO.getNegotiatedProcedure().equals(true)) {
      processes.add(Process.NEGOTIATED_PROCEDURE);

    }
    if (searchDTO.getNegotiatedProcedureWithCompetition() != null
      && searchDTO.getNegotiatedProcedureWithCompetition().equals(true)) {
      processes.add(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION);

    }

    if (!processes.isEmpty()) {
      if (searchDTO.getNegotiatedProcedureAboveThreshold() != null
        && searchDTO.getNegotiatedProcedureAboveThreshold().equals(true)) {
        whereClause.and(entity.process.in(processes).or(entity.aboveThreshold.isTrue()));
      } else {
        whereClause.and(entity.process.in(processes));
      }

    } else {
      if (searchDTO.getNegotiatedProcedureAboveThreshold() != null
        && searchDTO.getNegotiatedProcedureAboveThreshold().equals(true)) {
        whereClause.and(entity.aboveThreshold.isTrue());
      }
    }
    if (StringUtils.isNotBlank(searchDTO.getDescription())) {
      whereClause.and(entity.description.like("%" + searchDTO.getDescription() + "%"));
    }
    if (StringUtils.isNotBlank(searchDTO.getPmDepartmentName())) {
      whereClause.and(entity.pmDepartmentName.like("%" + searchDTO.getPmDepartmentName() + "%"));
    }
    List<MasterListValueEntity> processTypeList = new ArrayList<>();
    String tenantId = userAdmService.getUserById(getUser().getId()).getTenant().getId();
    if (searchDTO.getConstructionIndustry() != null
      && searchDTO.getConstructionIndustry().equals(true)) {
      MasterListValueHistoryEntity processTypeHistoryEntity = new JPAQueryFactory(em)
        .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
        .where(qMasterListValueHistoryEntity.shortCode
          .equalsIgnoreCase(ProcessTypeShortCode.PT1.getValue())
          .and(qMasterListValueHistoryEntity.tenant.id.eq(tenantId))
          .and(qMasterListValueHistoryEntity.toDate.isNull()))
        .fetchOne();
      MasterListValueEntity processTypeEntity = em.find(MasterListValueEntity.class,
        processTypeHistoryEntity.getMasterListValueId().getId());
      processTypeList.add(processTypeEntity);
    }
    if (searchDTO.getRelatedTrades() != null && searchDTO.getRelatedTrades().equals(true)) {
      MasterListValueHistoryEntity processTypeHistoryEntity = new JPAQueryFactory(em)
        .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
        .where(qMasterListValueHistoryEntity.shortCode
          .equalsIgnoreCase(ProcessTypeShortCode.PT2.getValue())
          .and(qMasterListValueHistoryEntity.tenant.id.eq(tenantId))
          .and(qMasterListValueHistoryEntity.toDate.isNull()))
        .fetchOne();
      MasterListValueEntity processTypeEntity = em.find(MasterListValueEntity.class,
        processTypeHistoryEntity.getMasterListValueId().getId());
      processTypeList.add(processTypeEntity);
    }
    if (searchDTO.getdLAssignments() != null && searchDTO.getdLAssignments().equals(true)) {

      MasterListValueHistoryEntity processTypeHistoryEntity = new JPAQueryFactory(em)
        .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
        .where(qMasterListValueHistoryEntity.shortCode
          .equalsIgnoreCase(ProcessTypeShortCode.PT3.getValue())
          .and(qMasterListValueHistoryEntity.tenant.id.eq(tenantId))
          .and(qMasterListValueHistoryEntity.toDate.isNull()))
        .fetchOne();
      MasterListValueEntity processTypeEntity = em.find(MasterListValueEntity.class,
        processTypeHistoryEntity.getMasterListValueId().getId());
      processTypeList.add(processTypeEntity);
    }
    if (searchDTO.getSupplyContracts() != null && searchDTO.getSupplyContracts().equals(true)) {
      MasterListValueHistoryEntity processTypeHistoryEntity = new JPAQueryFactory(em)
        .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
        .where(qMasterListValueHistoryEntity.shortCode
          .equalsIgnoreCase(ProcessTypeShortCode.PT4.getValue())
          .and(qMasterListValueHistoryEntity.tenant.id.eq(tenantId))
          .and(qMasterListValueHistoryEntity.toDate.isNull()))
        .fetchOne();
      MasterListValueEntity processTypeEntity = em.find(MasterListValueEntity.class,
        processTypeHistoryEntity.getMasterListValueId().getId());
      processTypeList.add(processTypeEntity);
    }

    if (!processTypeList.isEmpty()) {
      if ((searchDTO.getIsServiceTender() != null && searchDTO.getIsServiceTender().equals(true))) {
        whereClause.and(entity.processType.in(processTypeList).or(entity.isServiceTender.isTrue()));
      } else {
        whereClause.and(entity.processType.in(processTypeList));
      }
    }

    if (processTypeList.isEmpty() && (searchDTO.getIsServiceTender() != null
      && searchDTO.getIsServiceTender().equals(true))) {
      whereClause.and(entity.isServiceTender.isTrue());
    }

    /**
     * if checkbox Abgeschlossen(completed) is checked
     */
    if ((searchDTO.getCompleted() != null && searchDTO.getCompleted().equals(true))
      && (searchDTO.getRunning() == null || searchDTO.getRunning().equals(false))) {
      whereClause.and(entity.status.eq(TenderStatus.PROCEDURE_COMPLETED.getValue()));
    }
    /**
     * if checkbox Laufend(running)is checked
     */
    if ((searchDTO.getRunning() != null && searchDTO.getRunning().equals(true))
      && (searchDTO.getCompleted() == null || searchDTO.getCompleted().equals(false))) {
      whereClause.and(entity.status.notIn(TenderStatus.PROCEDURE_CANCELED.getValue(),
        TenderStatus.PROCEDURE_COMPLETED.getValue()));
    }

    /**
     * if checkboxes Laufend(running) and Abgeschlossen(completed) are checked
     */
    if (searchDTO.getRunning() != null && searchDTO.getRunning().equals(true)
      && searchDTO.getCompleted() != null && searchDTO.getCompleted().equals(true)) {
      whereClause.and(entity.status.notIn(TenderStatus.PROCEDURE_CANCELED.getValue()));
    }

    if (CollectionUtils.isNotEmpty(searchDTO.getDirectoratesIDs())) {
      QDirectorateHistoryEntity qDirectorateHistory = QDirectorateHistoryEntity.directorateHistoryEntity;

      List<DepartmentEntity> departmentEntities = new ArrayList<>();
      for (String directorateId : searchDTO.getDirectoratesIDs()) {
        //DEPARTMENT HISTORY CHANGE 

        String directorateEntityId = new JPAQueryFactory(em)
          .select(qDirectorateHistory.directorateId.id).from(qDirectorateHistory)
          .where(qDirectorateHistory.id.eq(directorateId))
          .fetchOne();

        List<DepartmentHistoryEntity> departmentHistoryEntities =
          new JPAQueryFactory(em).select(qDepartmentHistoryEntity).from(qDepartmentHistoryEntity)
            .where(qDepartmentHistoryEntity.directorateEnity.id.eq(directorateEntityId)
              .and(qDepartmentHistoryEntity.toDate.isNull()))
            .fetch();
        //DEPARTMENT HISTORY CHANGE
        for (DepartmentHistoryEntity d : departmentHistoryEntities) {
          DepartmentEntity departmentEntity = new JPAQueryFactory(em).select(qDepartmentEntity)
            .from(qDepartmentEntity).where(qDepartmentEntity.eq(d.getDepartmentId())).fetchOne();
          departmentEntities.add(departmentEntity);
        }
      }
      whereClause.and(project.department.in(departmentEntities));
    }
    if (StringUtils.isNotBlank(searchDTO.getCompanyName())) {
      String companyName = searchDTO.getCompanyName().toLowerCase();
      whereClause.and(entity.in(new JPAQueryFactory(em).select(qSubmittentEntity.submissionId)
        .from(qSubmittentEntity)
        .where((qSubmittentEntity.companyId.companyName.like("%" + companyName + "%"))).fetch()));
    }

    if (searchDTO.getOfferDateFrom() != null) {
      whereClause.and(entity.in(new JPAQueryFactory(em).select(qOfferEntity.submittent.submissionId)
        .from(qOfferEntity).where(qOfferEntity.offerDate.after(searchDTO.getOfferDateFrom())
          .or(qOfferEntity.offerDate.eq(searchDTO.getOfferDateFrom())))
        .fetch()));
    }
    if (searchDTO.getOfferDateUntil() != null) {
      whereClause.and(entity.in(new JPAQueryFactory(em).select(qOfferEntity.submittent.submissionId)
        .from(qOfferEntity).where(qOfferEntity.offerDate.before(searchDTO.getOfferDateUntil())
          .or(qOfferEntity.offerDate.eq(searchDTO.getOfferDateUntil())))
        .fetch()));
    }

    /** Apply filtering **/
    if (searchDTO.getFilter() != null) {
      if (StringUtils.isNotBlank(searchDTO.getFilter().getObjectName())) {
        List<MasterListValueHistoryEntity> objectNameHistoryEntities =
          new JPAQueryFactory(em).select(qMasterListValueHistoryEntity)
            .from(qMasterListValueHistoryEntity).where(qMasterListValueHistoryEntity.value1
            .like("%" + searchDTO.getFilter().getObjectName().toLowerCase() + "%"))
            .fetch();
        List<MasterListValueEntity> objectNames = new ArrayList<>();
        for (MasterListValueHistoryEntity objectNamesEntity : objectNameHistoryEntities) {
          List<MasterListValueEntity> objectNamesEntities = new JPAQueryFactory(em)
            .select(qMasterListValueEntity).from(qMasterListValueEntity)
            .where(qMasterListValueEntity.id.eq(objectNamesEntity.getMasterListValueId().getId()))
            .fetch();
          objectNames.addAll(objectNamesEntities);
        }
        whereClause.and(project.objectName.in(objectNames));
      }
      if (StringUtils.isNotBlank(searchDTO.getFilter().getProjectName())) {
        String projectName = searchDTO.getFilter().getProjectName();
        whereClause.and(project.projectName.toLowerCase().like("%" + projectName + "%"));
      }
      if (StringUtils.isNotBlank(searchDTO.getFilter().getProjectManagerOfDep())) {
        String projectManager = searchDTO.getFilter().getProjectManagerOfDep();
        whereClause.and(entity.pmDepartmentName.toLowerCase().like("%" + projectManager + "%"));
      }
      if (StringUtils.isNotBlank(searchDTO.getFilter().getWorkType())) {
        String companyworkType = searchDTO.getFilter().getWorkType().toLowerCase();
        List<MasterListValueHistoryEntity> workTypeHistoryEntities = new JPAQueryFactory(em)
          .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
          .where(qMasterListValueHistoryEntity.value2.like("%" + companyworkType + "%")
            .or(qMasterListValueHistoryEntity.value1.like("%" + companyworkType + "%")))
          .fetch();
        List<MasterListValueEntity> workTypes = new ArrayList<>();
        for (MasterListValueHistoryEntity workTypeEntity : workTypeHistoryEntities) {
          List<MasterListValueEntity> workTypeEntities =
            new JPAQueryFactory(em).select(qMasterListValueEntity).from(qMasterListValueEntity)
              .where(
                qMasterListValueEntity.id.eq(workTypeEntity.getMasterListValueId().getId()))
              .fetch();
          workTypes.addAll(workTypeEntities);
        }
        whereClause.and(entity.workType.in(workTypes));

      }

      if (StringUtils.isNotBlank(searchDTO.getFilter().getDescription())) {
        String description = searchDTO.getFilter().getDescription().toLowerCase();
        whereClause.and(entity.description.like("%" + description + "%"));
      }
      if (StringUtils.isNotBlank(searchDTO.getFilter().getProccessType())) {
        List<MasterListValueHistoryEntity> processTypeHistoryEntities =
          new JPAQueryFactory(em).select(qMasterListValueHistoryEntity)
            .from(qMasterListValueHistoryEntity)
            .where(qMasterListValueHistoryEntity.value1
              .like("%" + searchDTO.getFilter().getProccessType().toLowerCase() + "%"))
            .fetch();
        List<MasterListValueEntity> processTypes = new ArrayList<>();
        for (MasterListValueHistoryEntity processTypeEntity : processTypeHistoryEntities) {
          List<MasterListValueEntity> processTypeEntities = new JPAQueryFactory(em)
            .select(qMasterListValueEntity).from(qMasterListValueEntity)
            .where(qMasterListValueEntity.id.eq(processTypeEntity.getMasterListValueId().getId()))
            .fetch();
          processTypes.addAll(processTypeEntities);
        }
        whereClause.and(entity.processType.in(processTypes));
      }
      if (StringUtils.isNotBlank(searchDTO.getFilter().getManDep())) {
        String dep = searchDTO.getFilter().getManDep().toLowerCase();
        List<DepartmentHistoryEntity> departmentHistoryEntities =
          new JPAQueryFactory(em).select(qDepartmentHistoryEntity).from(qDepartmentHistoryEntity)
            .where(qDepartmentHistoryEntity.shortName.like("%" + dep + "%")).fetch();
        List<DepartmentEntity> departments = new ArrayList<>();
        for (DepartmentHistoryEntity d : departmentHistoryEntities) {
          List<DepartmentEntity> departmentEntities =
            new JPAQueryFactory(em).select(qDepartmentEntity).from(qDepartmentEntity)
              .where(qDepartmentEntity.eq(d.getDepartmentId())).fetch();
          departments.addAll(departmentEntities);
        }
        whereClause.and(entity.project.department.in(departments));

      }
      if (searchDTO.getFilter().getProccess() != null) {
        whereClause.and(entity.process.eq(searchDTO.getFilter().getProccess()));
      }
      if (searchDTO.getFilter().getSubmissionDeadline() != null) {
        whereClause.and(SQLExpressions.date(entity.firstDeadline)
          .eq(searchDTO.getFilter().getSubmissionDeadline()).and(entity.secondDeadline.isNull())
          .or(SQLExpressions.date(entity.secondDeadline)
            .eq(searchDTO.getFilter().getSubmissionDeadline())));
      }

    }

    return whereClause;
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.ProjectService#projectCount(ch.bern.submiss.
   * services.api.dto.SearchDTO)
   */
  @Override
  public long projectCount(SearchDTO searchDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method projectCount, Parameters: searchDTO: {0}",
      searchDTO);

    JPAQuery<Tuple> query = new JPAQuery<>(em);
    Long projectCount;

    // If ExcludedProject null then get submission count for project search, else get project count
    // for daten verschieben
    if (searchDTO.getExcludedProject() == null) {
      projectCount =
        query.select(project.id).from(submission).rightJoin(submission.project, project)
          .where(getWhereClause(submission, project, searchDTO)).fetchCount();
    } else {
      projectCount = query.select(project.id).distinct().from(submission)
        .rightJoin(submission.project, project)
        .where(getWhereClause(submission, project, searchDTO)).fetchCount();
    }

    return projectCount;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.ProjectService#findIfNameUnique(java.lang.String,
   * java.lang.String, java.lang.String)
   */
  @Override
  public Boolean findIfNameUnique(String projectName, String objectNameId, String projectId) {

    LOGGER.log(Level.CONFIG,
      "Executing method findIfNameUnique, Parameters: projectName: {0}, "
        + "objectNameId: {1}, projectId: {2}",
      new Object[]{projectName, objectNameId, projectId});

    QProjectEntity qProjectEntity = QProjectEntity.projectEntity;

    MasterListValueHistoryEntity objectNameEntity =
      new JPAQueryFactory(em).select(qMasterListValueHistoryEntity)
        .from(qMasterListValueHistoryEntity).where(qMasterListValueHistoryEntity.id
        .eq(objectNameId).and(qMasterListValueHistoryEntity.toDate.isNull()))
        .fetchOne();
    MasterListValueEntity object =
      new JPAQueryFactory(em).select(qMasterListValueEntity).from(qMasterListValueEntity)
        .where(qMasterListValueEntity.id.eq(objectNameEntity.getMasterListValueId().getId()))
        .fetchOne();
    List<ProjectEntity> projectEntityList =
      new JPAQuery<>(em).select(qProjectEntity).from(qProjectEntity).fetch();
    for (ProjectEntity projectEntity : projectEntityList) {
      QProjectEntity qProject = QProjectEntity.projectEntity;
      ProjectEntity entity = new JPAQuery<>(em).select(qProject).from(qProject)
        .where(qProject.id.eq(projectEntity.getId())).fetchFirst();
      if ((entity.getProjectName().equals(projectName)
        && entity.getObjectName().getId().equals(object.getId()))
        && (!entity.getId().equals(projectId) || projectId == null)) {
        return true;
      }

    }

    return false;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.ProjectService#findIfProjectHasSubmission(java.lang
   * .String)
   */
  @Override
  public Boolean findIfProjectHasSubmission(String id) {

    LOGGER.log(Level.CONFIG,
      "Executing method findIfProjectHasSubmission, Parameters: id: {0}",
      id);

    List<SubmissionEntity> submissions = new JPAQueryFactory(em).select(qSubmissionEntity)
      .from(qSubmissionEntity).where(qSubmissionEntity.project.id.eq(id)).fetch();
    return !submissions.isEmpty();
  }

  /**
   * This function returns a boolean (hasStatusAfterOfferOpeningClosed) by taking the id of the
   * project. The boolean is true if at least one of the submissions of this project has a status:
   * offer_opening_closed or greater than it. If not it returns false.
   *
   * @param projectId the project id
   * @return true, if successful
   */
  @Override
  public boolean hasStatusAfterOfferOpeningClosed(String projectId) {

    LOGGER.log(Level.CONFIG,
      "Executing method hasStatusAfterOfferOpeningClosed, "
        + "Parameters: projectId: {0}", projectId);

    boolean hasStatusAfterOfferOpeningClosed = false;
    List<SubmissionDTO> submissions = submissionService.getSubmissionsByProject(projectId);
    for (SubmissionDTO submissionDTO : submissions) {
      if (compareCurrentVsSpecificStatus(
        TenderStatus
          .fromValue(submissionService.getCurrentStatusOfSubmission(submissionDTO.getId())),
        TenderStatus.OFFER_OPENING_CLOSED)) {
        hasStatusAfterOfferOpeningClosed = true;
        break;
      }
    }
    return hasStatusAfterOfferOpeningClosed;
  }

  /**
   * No submission search criteria selected.
   *
   * @param searchDTO the search DTO
   * @return the boolean
   */
  private Boolean noSubmissionSearchCriteriaSelected(SearchDTO searchDTO) {

    return ((searchDTO.getOpen() == null || !searchDTO.getOpen())
      && (searchDTO.getNegotiatedProcedure() == null || !searchDTO.getNegotiatedProcedure())
      && (searchDTO.getNegotiatedProcedureWithCompetition() == null
      || !searchDTO.getNegotiatedProcedureWithCompetition())
      && (searchDTO.getNegotiatedProcedureAboveThreshold() == null
      || !searchDTO.getNegotiatedProcedureAboveThreshold())
      && (searchDTO.getInvitation() == null || !searchDTO.getInvitation())
      && (searchDTO.getSelective() == null || !searchDTO.getSelective())
      && (searchDTO.getWorkTypes() == null || searchDTO.getWorkTypes().isEmpty())
      && (searchDTO.getDescription() == null || searchDTO.getDescription().isEmpty()));

  }

  /**
   * No submission filter criteria selected.
   *
   * @param filterDTO the filter DTO
   * @return the boolean
   */
  private Boolean noSubmissionFilterCriteriaSelected(FilterDTO filterDTO) {
    return (filterDTO == null
      || (filterDTO.getWorkType() == null || filterDTO.getWorkType().isEmpty())
      && (filterDTO.getDescription() == null || filterDTO.getDescription().isEmpty())
      && filterDTO.getProccess() == null
      && (filterDTO.getProccessType() == null || filterDTO.getProccessType().isEmpty())
      && filterDTO.getSubmissionDeadline() == null);
  }

  /**
   * No submittent criteria selected.
   *
   * @param searchDTO the search DTO
   * @return the boolean
   */
  private Boolean noSubmittentCriteriaSelected(SearchDTO searchDTO) {
    return ((searchDTO.getCompanyName() == null || searchDTO.getCompanyName().isEmpty())
      && searchDTO.getOfferDateFrom() == null && searchDTO.getOfferDateUntil() == null);
  }

  /**
   * Project contains only locked submissions.
   *
   * @param projectId the project id
   * @return the boolean
   */
  private Boolean projectContainsOnlyLockedSubmissions(String projectId) {
    List<SubmissionDTO> projectSubmissions = submissionService.getSubmissionsByProject(projectId);
    for (SubmissionDTO submissionDTO : projectSubmissions) {
      if (!submissionDTO.getIsLocked()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public Set<ValidationError> optimisticLockProject(String projectId, Long currentVersion) {

    LOGGER.log(Level.CONFIG,
      "Executing method optimisticLockProject, "
        + "Parameters: projectId: {0}, currentVersion: {1}",
      new Object[]{projectId, currentVersion});

    Set<ValidationError> optimisticLockErrors = new HashSet<>();
    ProjectEntity projectEntity = em.find(ProjectEntity.class, projectId);
    // Check if current version is different from the version stored in the database
    if (!currentVersion.equals(projectEntity.getVersion())) {
      optimisticLockErrors
        .add(new ValidationError("optimisticLockErrorField",
          ValidationMessages.OPTIMISTIC_LOCK));
    }
    return optimisticLockErrors;
  }

  @Override
  public boolean projectExists(String projectId) {
    ProjectEntity projectEntity = em.find(ProjectEntity.class, projectId);
    return projectEntity != null;
  }

  @Override
  public void projectCreateSecurityCheck() {
    security.isPermittedOperationForUser(getUserId(),
      SecurityOperation.PROJECT_CREATE.getValue(), null);
  }

  @Override
  public void projectEditSecurityCheck(String id) {
    security.isPermittedOperationForUser(getUserId(),
      SecurityOperation.PROJECT_EDIT.getValue(), id);
  }

  @Override
  public void projectDetailsSecurityCheck(String projectId) {
    security.isPermittedOperationForUser(getUserId(),
      SecurityOperation.PROJECT.getValue(), projectId);
  }

  @Override
  public void submissionListSecurityCheck(String projectId) {
    security.isPermittedOperationForUser(getUserId(),
      SecurityOperation.TENDER_LIST_VIEW.getValue(), projectId);
  }

  @Override
  public void projectSearchSecurityCheck() {
    security.isPermittedOperationForUser(getUserId(),
      SecurityOperation.PROJECT_SEARCH.getValue(), null);
  }
}
