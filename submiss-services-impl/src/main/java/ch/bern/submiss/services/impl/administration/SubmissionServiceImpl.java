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

import ch.bern.submiss.services.api.administration.CriterionService;
import ch.bern.submiss.services.api.administration.EmailService;
import ch.bern.submiss.services.api.administration.OfferService;
import ch.bern.submiss.services.api.administration.ProcedureService;
import ch.bern.submiss.services.api.administration.SDProofService;
import ch.bern.submiss.services.api.administration.SDService;
import ch.bern.submiss.services.api.administration.SubDocumentService;
import ch.bern.submiss.services.api.administration.SubmissAuditService;
import ch.bern.submiss.services.api.administration.SubmissTaskService;
import ch.bern.submiss.services.api.administration.SubmissionService;
import ch.bern.submiss.services.api.administration.TenderStatusHistoryService;
import ch.bern.submiss.services.api.administration.UserAdministrationService;
import ch.bern.submiss.services.api.constants.AuditEvent;
import ch.bern.submiss.services.api.constants.AuditGroupName;
import ch.bern.submiss.services.api.constants.AuditLevel;
import ch.bern.submiss.services.api.constants.AuditMessages;
import ch.bern.submiss.services.api.constants.CategorySD;
import ch.bern.submiss.services.api.constants.CommissionProcurementProposalReservation;
import ch.bern.submiss.services.api.constants.ConstructionPermit;
import ch.bern.submiss.services.api.constants.DocumentAttributes;
import ch.bern.submiss.services.api.constants.Group;
import ch.bern.submiss.services.api.constants.LoanApproval;
import ch.bern.submiss.services.api.constants.Process;
import ch.bern.submiss.services.api.constants.SecurityOperation;
import ch.bern.submiss.services.api.constants.SelectiveLevel;
import ch.bern.submiss.services.api.constants.TaskTypes;
import ch.bern.submiss.services.api.constants.Template;
import ch.bern.submiss.services.api.constants.TenderStatus;
import ch.bern.submiss.services.api.dto.AwardDTO;
import ch.bern.submiss.services.api.dto.CommissionProcurementDecisionDTO;
import ch.bern.submiss.services.api.dto.CompanyDTO;
import ch.bern.submiss.services.api.dto.CompanyProofDTO;
import ch.bern.submiss.services.api.dto.CriterionDTO;
import ch.bern.submiss.services.api.dto.DepartmentHistoryDTO;
import ch.bern.submiss.services.api.dto.MasterListValueHistoryDTO;
import ch.bern.submiss.services.api.dto.OfferCriterionDTO;
import ch.bern.submiss.services.api.dto.OfferDTO;
import ch.bern.submiss.services.api.dto.OfferSubcriterionDTO;
import ch.bern.submiss.services.api.dto.ProcedureHistoryDTO;
import ch.bern.submiss.services.api.dto.ProofHistoryDTO;
import ch.bern.submiss.services.api.dto.ProofProvidedMapDTO;
import ch.bern.submiss.services.api.dto.SignatureProcessTypeDTO;
import ch.bern.submiss.services.api.dto.SubcriterionDTO;
import ch.bern.submiss.services.api.dto.SubmissTaskDTO;
import ch.bern.submiss.services.api.dto.SubmissUserDTO;
import ch.bern.submiss.services.api.dto.SubmissionDTO;
import ch.bern.submiss.services.api.dto.SubmittentDTO;
import ch.bern.submiss.services.api.dto.SubmittentOfferDTO;
import ch.bern.submiss.services.api.dto.TenderStatusHistoryDTO;
import ch.bern.submiss.services.api.exceptions.AuthorisationException;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.api.util.ValidationMessages;
import ch.bern.submiss.services.impl.mappers.CompanyMapper;
import ch.bern.submiss.services.impl.mappers.CompanyProofDTOMapper;
import ch.bern.submiss.services.impl.mappers.OfferDTOMapper;
import ch.bern.submiss.services.impl.mappers.OfferDTOWithCriteriaMapper;
import ch.bern.submiss.services.impl.mappers.ProjectMapper;
import ch.bern.submiss.services.impl.mappers.SubmissionDTOMBasicMapper;
import ch.bern.submiss.services.impl.mappers.SubmissionMapper;
import ch.bern.submiss.services.impl.mappers.SubmittentDTOMapper;
import ch.bern.submiss.services.impl.mappers.TenderStatusHistoryDTOMapper;
import ch.bern.submiss.services.impl.model.CompanyEntity;
import ch.bern.submiss.services.impl.model.CompanyProofEntity;
import ch.bern.submiss.services.impl.model.CriterionEntity;
import ch.bern.submiss.services.impl.model.FormalAuditEntity;
import ch.bern.submiss.services.impl.model.MasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.OfferCriterionEntity;
import ch.bern.submiss.services.impl.model.OfferEntity;
import ch.bern.submiss.services.impl.model.OfferSubcriterionEntity;
import ch.bern.submiss.services.impl.model.ProjectEntity;
import ch.bern.submiss.services.impl.model.QCompanyEntity;
import ch.bern.submiss.services.impl.model.QCompanyProofEntity;
import ch.bern.submiss.services.impl.model.QCriterionEntity;
import ch.bern.submiss.services.impl.model.QDepartmentEntity;
import ch.bern.submiss.services.impl.model.QFormalAuditEntity;
import ch.bern.submiss.services.impl.model.QMasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.QOfferCriterionEntity;
import ch.bern.submiss.services.impl.model.QOfferEntity;
import ch.bern.submiss.services.impl.model.QOfferSubcriterionEntity;
import ch.bern.submiss.services.impl.model.QProjectEntity;
import ch.bern.submiss.services.impl.model.QProofHistoryEntity;
import ch.bern.submiss.services.impl.model.QSubmissTasksEntity;
import ch.bern.submiss.services.impl.model.QSubmissionAwardInfoEntity;
import ch.bern.submiss.services.impl.model.QSubmissionCancelEntity;
import ch.bern.submiss.services.impl.model.QSubmissionEntity;
import ch.bern.submiss.services.impl.model.QSubmittentEntity;
import ch.bern.submiss.services.impl.model.QSubmittentProofProvidedEntity;
import ch.bern.submiss.services.impl.model.QTenderStatusHistoryEntity;
import ch.bern.submiss.services.impl.model.SubmissionCancelEntity;
import ch.bern.submiss.services.impl.model.SubmissionEntity;
import ch.bern.submiss.services.impl.model.SubmittentEntity;
import ch.bern.submiss.services.impl.model.SubmittentProofProvidedEntity;
import ch.bern.submiss.services.impl.model.TenderStatusHistoryEntity;
import ch.bern.submiss.services.impl.util.ComparatorUtil;
import com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO;
import com.eurodyn.qlack2.fuse.auditing.api.dto.AuditLogDTO;
import com.eurodyn.qlack2.fuse.cm.api.DocumentService;
import com.eurodyn.qlack2.fuse.cm.api.dto.FolderDTO;
import com.eurodyn.qlack2.fuse.cm.api.dto.NodeDTO;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
import javax.persistence.OptimisticLockException;
import javax.persistence.RollbackException;
import javax.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

/**
 * The Class SubmissionServiceImpl.
 */
@Transactional
@OsgiServiceProvider(classes = {SubmissionService.class})
@Singleton
public class SubmissionServiceImpl extends BaseService implements SubmissionService {

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER = Logger.getLogger(SubmissionServiceImpl.class.getName());
  /**
   * The Constant EMPTY_VALUES.
   */
  private static final String EMPTY_VALUES = "empty_values";
  /**
   * The Constant INVALID_SUBCRITERION_WEIGHTINGS.
   */
  private static final String INVALID_SUBCRITERION_WEIGHTINGS = "invalid_subcriterion_weightings";
  /**
   * The Constant INVALID_CRITERION_WEIGHTINGS.
   */
  private static final String INVALID_CRITERION_WEIGHTINGS = "invalid_criterion_weightings";
  /**
   * The Constant NULL_EXCLUSION_REASONS_SELECTIVE.
   */
  private static final String NULL_EXCLUSION_REASONS_SELECTIVE =
    "selective_formal_audit_empty_field";
  /**
   * The Constant NULL_EXCLUSION_REASONS_NEGOTIATED.
   */
  private static final String NULL_EXCLUSION_REASONS_NEGOTIATED =
    "negotiated_procedure_formal_audit_empty_field";
  /**
   * The Constant NULL_MIN_GRADE_MAX_GRADE.
   */
  private static final String NULL_MIN_GRADE_MAX_GRADE = "null_min_grade_max_grade";
  /**
   * The Constant NO_EXAMINATION_DOCUMENT.
   */
  private static final String NO_EXAMINATION_DOCUMENT = "no_examination_document";
  /**
   * The Constant PROOF_INCONSISTENCIES.
   */
  private static final String PROOF_INCONSISTENCIES = "proofInconsistencies";
  /**
   * The Constant SUBMITTENTLIST_CHECK.
   */
  private static final String SUBMITTENTLIST_CHECK = "SUBMITTENTLIST_CHECK";
  /**
   * The sub document service.
   */
  @Inject
  protected SubDocumentService subDocumentService;
  /**
   * The users service.
   */
  @Inject
  protected UserAdministrationService usersService;
  /**
   * The submission service.
   */
  @Inject
  protected SubmissionService submissionService;
  /**
   * The q department entity.
   */
  QDepartmentEntity qDepartmentEntity = QDepartmentEntity.departmentEntity;
  /**
   * The q tender status history entity.
   */
  QTenderStatusHistoryEntity qTenderStatusHistoryEntity =
    QTenderStatusHistoryEntity.tenderStatusHistoryEntity;
  /**
   * The q submittent entity.
   */
  QSubmittentEntity qSubmittentEntity = QSubmittentEntity.submittentEntity;
  /**
   * The q submission entity.
   */
  QSubmissionEntity qSubmissionEntity = QSubmissionEntity.submissionEntity;
  /**
   * The q company proof entity.
   */
  QCompanyProofEntity qCompanyProofEntity = QCompanyProofEntity.companyProofEntity;
  /**
   * The q proof history entity.
   */
  QProofHistoryEntity qProofHistoryEntity = QProofHistoryEntity.proofHistoryEntity;
  /**
   * The q offer criterion entity.
   */
  QOfferCriterionEntity qOfferCriterionEntity = QOfferCriterionEntity.offerCriterionEntity;
  /**
   * The q offer subriterion.
   */
  QOfferSubcriterionEntity qOfferSubriterion = QOfferSubcriterionEntity.offerSubcriterionEntity;
  /**
   * The q offer entity.
   */
  QOfferEntity qOfferEntity = QOfferEntity.offerEntity;
  /**
   * The q company entity.
   */
  QCompanyEntity qCompanyEntity = QCompanyEntity.companyEntity;
  /**
   * The q master list value history entity.
   */
  QMasterListValueHistoryEntity qMasterListValueHistoryEntity =
    QMasterListValueHistoryEntity.masterListValueHistoryEntity;
  /**
   * The q formal audit entity.
   */
  QFormalAuditEntity qFormalAuditEntity = QFormalAuditEntity.formalAuditEntity;
  /**
   * The q submiss task.
   */
  QSubmissTasksEntity qSubmissTask = QSubmissTasksEntity.submissTasksEntity;
  /**
   * The q submission award info entity.
   */
  QSubmissionAwardInfoEntity qSubmissionAwardInfoEntity =
    QSubmissionAwardInfoEntity.submissionAwardInfoEntity;
  /**
   * The q submission cancel entity.
   */
  QSubmissionCancelEntity qSubmissionCancelEntity = QSubmissionCancelEntity.submissionCancelEntity;
  /**
   * The q tender status history entity 1.
   */
  QTenderStatusHistoryEntity qTenderStatusHistoryEntity1 =
    new QTenderStatusHistoryEntity(LookupValues.TABLE_ALIAS);
  /**
   * The q submittent proof provided.
   */
  QSubmittentProofProvidedEntity qSubmittentProofProvided =
    QSubmittentProofProvidedEntity.submittentProofProvidedEntity;
  /**
   * The project bean.
   */
  @Inject
  ProjectBean projectBean;
  /**
   * The audit.
   */
  @Inject
  private AuditBean audit;
  /**
   * The offer service.
   */
  @Inject
  private OfferService offerService;
  /**
   * The document service.
   */
  @Inject
  private DocumentService documentService;
  /**
   * The criterion service.
   */
  @Inject
  private CriterionService criterionService;
  /**
   * The procedure service.
   */
  @Inject
  private ProcedureService procedureService;

  /**
   * The sd service.
   */
  @Inject
  private SDService sdService;

  /**
   * The task service.
   */
  @Inject
  private SubmissTaskService taskService;

  /**
   * The tender status history service.
   */
  @Inject
  private TenderStatusHistoryService tenderStatusHistoryService;

  /**
   * The email service.
   */
  @Inject
  private EmailService emailService;

  /**
   * The cache bean.
   */
  @Inject
  private CacheBean cacheBean;

  /**
   * The sd proof service.
   */
  @Inject
  private SDProofService sDProofService;

  /**
   * The sd proof service.
   */
  @Inject
  private SubmissAuditService submissAuditService;

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SubmissionService#createSubmission(ch.bern.submiss.
   * services.api.dto.SubmissionDTO)
   */
  @Override
  public String createSubmission(SubmissionDTO submissionDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method createSubmission, Parameters: submissionDTO: {0}",
      submissionDTO);

    /* security checks */
    UserDTO user = getUser();
    // check if this operation is allowed for this user
    String operation = SecurityOperation.TENDER_CREATE.getValue();
    security.requireOperation(user.getId(), operation);
    /* check if the entities accessed by this operation(department) are permitted for the user */
    security.requireCheckEntity(user,
      submissionDTO.getProject().getDepartment().getDepartmentId().getId(), null, operation);
    /* check if all preconditions of this operation are met */
    if (submissionDTO.getProject().getConstructionPermit() == null
      || submissionDTO.getProject().getLoanApproval() == null
      || submissionDTO.getProject().getGattWto() == null) {
      throw new AuthorisationException(user.getId(), operation + LookupValues.CHECK_PRECONDITIONS);
    }

    submissionDTO.setCreatedBy(getUserId());
    submissionDTO.setCreatedOn(new Timestamp(new Date().getTime()));
    SubmissionEntity submissionEntity = SubmissionMapper.INSTANCE.toSubmission(submissionDTO);
    if (submissionDTO.getPmExternal() != null) {
      submissionEntity
        .setPmExternal(em.find(CompanyEntity.class, submissionDTO.getPmExternal().getId()));
    }

    ProjectEntity projectEntity = ProjectMapper.INSTANCE.toProject(submissionDTO.getProject());
    submissionEntity.setProject(projectEntity);

    // we need to set the flag is updated of the fields
    // PmDepartmentName, PmExternal, Procedure, GattTwo
    updateIsUpdatedFlagOnCreate(submissionEntity);

    em.persist(submissionEntity);
    /** Set the tender status to submission created **/
    updateSubmissionStatus(submissionEntity, TenderStatus.SUBMISSION_CREATED.getValue(), null, null,
      LookupValues.INTERNAL_LOG, getUserId());
    if (submissionDTO.getIsLocked()) {
      /*
       * if the submission is marked as locked then the security resources for the locked
       * submissions must be added
       */
      security.addSubmissionLockedGroupResources(submissionEntity.getId());
    }
    StringBuilder submissionVars = new StringBuilder(projectEntity.getProjectName()).append("[#]")
      .append(submissionDTO.getProject().getObjectName().getValue1()).append("[#]")
      .append(submissionDTO.getWorkType().getValue1() + submissionDTO.getWorkType().getValue2())
      .append("[#]")
      .append(usersService.getUserById(getUser().getId()).getTenant().getId()).append("[#]");

    auditLog(AuditEvent.CREATE.name(), AuditMessages.CREATE_SUBMISSION.name(),
      submissionEntity.getId(), submissionVars.toString());
    return submissionEntity.getId();
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SubmissionService#getSubmissionsByProject(java.lang
   * .String)
   */
  @Override
  public List<SubmissionDTO> getSubmissionsByProject(String projectId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getSubmissionsByProject, Parameters: projectId: {0}",
      projectId);

    security.isPermittedOperationForUser(getUserId(),
      SecurityOperation.TENDER_LIST_VIEW.getValue(), projectId);

    JPAQuery<SubmissionEntity> query = new JPAQuery<>(em);
    QSubmissionEntity entity = QSubmissionEntity.submissionEntity;
    BooleanBuilder whereClause = new BooleanBuilder();
    whereClause.and(entity.project.id.eq(projectId));
    // add security check
    UserDTO user = getUser();
    whereClause.and(entity.id.notIn(security.getLockedSubmissions(user)));
    List<SubmissionEntity> submissionEntityList =
      query.select(entity).distinct().from(entity).where(whereClause)
        .orderBy(entity.workType.masterListValueHistory.any().value2.asc()).fetch();
    List<SubmissionDTO> submissionDTOList = new ArrayList<>(submissionEntityList.size());
    for (SubmissionEntity submission : submissionEntityList) {
      SubmissionDTO submissionDTO = SubmissionMapper.INSTANCE.toSubmissionDTO(submission);
      Timestamp statusDate = getDateOfCompletedOrCanceledStatus(submission.getId());
      Timestamp creationDate = getDateOfCreationStatusOfSubmission(submission.getId());
      Timestamp objectReferenceDate = getRefernceDateForObject(submission.getId());

      if (submission.getWorkType() != null) {
        MasterListValueHistoryDTO workTypeDTO = cacheBean.getValue(
          CategorySD.WORKTYPE.getValue(),
          submission.getWorkType().getId(), statusDate, creationDate, null);
        submissionDTO.setWorkType(workTypeDTO);
      }

      if (submission.getProcessType() != null) {
        MasterListValueHistoryDTO processTypeDTO = cacheBean.getValue(
          CategorySD.PROCESS_TYPE.getValue(),

          submission.getProcessType().getId(), statusDate, creationDate, null);
        submissionDTO.setProcessType(processTypeDTO);
      }

      if (submission.getProcedure() != null) {
        MasterListValueHistoryDTO procedureDTO = cacheBean.getValue(
          submission.getProcedure().getMasterList().getCategoryType(),

          submission.getProcedure().getId(), statusDate, creationDate, null);
        submissionDTO.setProcedure(procedureDTO);

      }

      if (submission.getProject().getObjectName() != null) {
        MasterListValueHistoryDTO objectNameDTO = cacheBean.getValue(
          CategorySD.OBJECT.getValue(),

          submission.getProject().getObjectName().getId(), statusDate, creationDate,
          objectReferenceDate);
        submissionDTO.getProject().setObjectName(objectNameDTO);
      }

      if (submission.getReasonFreeAward() != null) {
        MasterListValueHistoryDTO reasonFreeAwardDTO = cacheBean.getValue(
          CategorySD.NEGOTIATION_REASON.getValue(),

          submission.getReasonFreeAward().getId(), statusDate, creationDate, null);
        submissionDTO.setReasonFreeAward(reasonFreeAwardDTO);
      }

      if (submission.getProject().getDepartment() != null) {
        DepartmentHistoryDTO departmentDTO = cacheBean
          .getValue(submission.getProject().getDepartment().getId(), statusDate);
        submissionDTO.getProject().setDepartment(departmentDTO);
        if (departmentDTO != null) {
          submissionDTO.getProject().getDepartment().setDirectorate(cacheBean.getValueDirectorate(
            departmentDTO.getDirectorate().getDirectorateId().getId(), statusDate));
        }

      }

      submissionDTOList.add(submissionDTO);

    }

    return submissionDTOList;
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SubmissionService#deleteSubmission(java.lang.
   * String)
   */
  @Override
  public void deleteSubmission(String id) {

    LOGGER.log(Level.CONFIG, "Executing method deleteSubmission, Parameters: id: {0}",
      id);

    security.isPermittedOperationForUser(getUserId(),
      SecurityOperation.TENDER_DELETE.getValue(), id);

    List<OfferEntity> offerList = new JPAQueryFactory(em).select(qOfferEntity).from(qOfferEntity)
      .where(qOfferEntity.submittent.submissionId.id.eq(id)).fetch();
    for (OfferEntity offer : offerList) {
      em.remove(offer);
    }
    JPAQuery<SubmittentEntity> querySubmittent = new JPAQuery<>(em);
    List<SubmittentEntity> submittentList = querySubmittent.select(qSubmittentEntity)
      .from(qSubmittentEntity).where(qSubmittentEntity.submissionId.id.eq(id)).fetch();
    for (SubmittentEntity submittent : submittentList) {
      em.remove(submittent);
    }
    JPAQuery<TenderStatusHistoryEntity> query = new JPAQuery<>(em);
    List<TenderStatusHistoryEntity> tenderStatusList =
      query.select(qTenderStatusHistoryEntity).from(qTenderStatusHistoryEntity)
        .where(qTenderStatusHistoryEntity.tenderId.id.eq(id)).fetch();
    for (TenderStatusHistoryEntity tenderStatus : tenderStatusList) {
      em.remove(tenderStatus);
    }
    /* deletes aaa resources for security for Eingereichte Offerte list if created */
    security.deleteSubmissionListGroupResources(id);
    SubmissionEntity submissionEntity = em.find(SubmissionEntity.class, id);
    if (submissionEntity != null) {
      MasterListValueHistoryEntity workType = new JPAQueryFactory(em)
        .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
        .where(qMasterListValueHistoryEntity.masterListValueId.eq(submissionEntity.getWorkType())
          .and(qMasterListValueHistoryEntity.toDate.isNull()))
        .fetchOne();

      MasterListValueHistoryEntity objectEntity = new JPAQueryFactory(em)
        .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
        .where(qMasterListValueHistoryEntity.masterListValueId
          .eq(submissionEntity.getProject().getObjectName())
          .and(qMasterListValueHistoryEntity.toDate.isNull()))
        .fetchOne();

      String workTypeValue2 = (StringUtils.isEmpty(workType.getValue2())) ? StringUtils.EMPTY
        : " " + workType.getValue2();

      StringBuilder submissionVars =
        new StringBuilder(submissionEntity.getProject().getProjectName()).append("[#]")
          .append(objectEntity.getValue1()).append("[#]")
          .append(workType.getValue1() + workTypeValue2).append("[#]")
          .append(usersService.getUserById(getUser().getId()).getTenant().getId()).append("[#]");

      em.remove(submissionEntity);

      auditLog(AuditEvent.DELETE.name(), AuditMessages.DELETE_SUBMISSION.name(), id,
        submissionVars.toString());
    }

  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SubmissionService#getSubmissionById(java.lang.
   * String)
   */
  @Override
  public SubmissionDTO getSubmissionById(String id) {

    LOGGER.log(Level.CONFIG,
      "Executing method getSubmissionById, Parameters: id: {0}",
      id);

    security.isPermittedOperationForUser(getUserId(),
      SecurityOperation.TENDER_VIEW.getValue(), id);

    SubmissionEntity submissionEntity = em.find(SubmissionEntity.class, id);
    SubmissionDTO submissionDTO = SubmissionDTOMBasicMapper.INSTANCE
      .toBasicSubmissionDTO(submissionEntity);
    if (submissionEntity != null) {
      Timestamp statusDate = getDateOfCompletedOrCanceledStatus(submissionEntity.getId());
      Timestamp creationDate = getDateOfCreationStatusOfSubmission(submissionEntity.getId());
      Timestamp objectDate = getRefernceDateForObject(submissionEntity.getId());

      if (submissionEntity.getWorkType() != null) {
        MasterListValueHistoryDTO workTypeDTO = cacheBean.getValue(CategorySD.WORKTYPE.getValue(),
          submissionEntity.getWorkType().getId(), statusDate, creationDate, null);
        submissionDTO.setWorkType(workTypeDTO);
      }

      if (submissionEntity.getProcessType() != null) {
        MasterListValueHistoryDTO processTypeDTO = cacheBean
          .getValue(CategorySD.PROCESS_TYPE.getValue(),

            submissionEntity.getProcessType().getId(), statusDate, creationDate, null);
        submissionDTO.setProcessType(processTypeDTO);
      }

      if (submissionEntity.getProcedure() != null) {
        MasterListValueHistoryDTO procedureDTO = cacheBean.getValue(
          submissionEntity.getProcedure().getMasterList().getCategoryType(),

          submissionEntity.getProcedure().getId(), statusDate, creationDate, null);
        submissionDTO.setProcedure(procedureDTO);

      }

      if (submissionEntity.getProject().getObjectName() != null) {
        MasterListValueHistoryDTO objectNameDTO = cacheBean.getValue(CategorySD.OBJECT.getValue(),

          submissionEntity.getProject().getObjectName().getId(), statusDate, creationDate,
          objectDate);
        submissionDTO.getProject().setObjectName(objectNameDTO);
      }

      if (submissionEntity.getReasonFreeAward() != null) {
        MasterListValueHistoryDTO reasonFreeAwardDTO = cacheBean.getValue(
          CategorySD.NEGOTIATION_REASON.getValue(),

          submissionEntity.getReasonFreeAward().getId(), statusDate, creationDate, null);
        submissionDTO.setReasonFreeAward(reasonFreeAwardDTO);
      }

      if (submissionEntity.getProject().getDepartment() != null) {
        DepartmentHistoryDTO departmentDTO = cacheBean
          .getValue(submissionEntity.getProject().getDepartment().getId(), statusDate);
        submissionDTO.getProject().setDepartment(departmentDTO);
        if (departmentDTO != null) {
          submissionDTO.getProject().getDepartment().setDirectorate(cacheBean.getValueDirectorate(
            departmentDTO.getDirectorate().getDirectorateId().getId(), statusDate));
        }
      }

      if (submissionEntity.getPriceFormula() != null) {
        MasterListValueHistoryDTO priceFormulaDTO = cacheBean.getValue(
          CategorySD.CALCULATION_FORMULA.getValue(),

          submissionEntity.getPriceFormula().getId(), statusDate, creationDate, null);
        submissionDTO.setPriceFormula(priceFormulaDTO);
      }

      if (submissionEntity.getOperatingCostFormula() != null) {
        MasterListValueHistoryDTO operatingCostFormulaDTO = cacheBean.getValue(
          CategorySD.CALCULATION_FORMULA.getValue(),
          submissionEntity.getOperatingCostFormula().getId(),
          statusDate, creationDate, null);
        submissionDTO.setOperatingCostFormula(operatingCostFormulaDTO);
      }

      /*
       * if status is SUBMITTENTLIST_CHECK and user is PL
       * pass SUBMITTENTLIST_CHECK info to DTO
       */
      if (getCurrentStatusOfSubmission(submissionDTO.getId())
        .equals(TenderStatus.SUBMITTENTLIST_CHECK.getValue())
        && getGroupName(getUser()).equals(Group.PL.getValue())
        && submissionEntity.getSubmittentListCheckedBy() != null
        && submissionEntity.getSubmittentListCheckedOn() != null) {
        SubmissUserDTO user = usersService
          .getSpecificUser(submissionEntity.getSubmittentListCheckedBy());
        String userName = user.getFirstName() + LookupValues.SPACE + user.getLastName();
        submissionDTO.setSubmittentListCheckedBy(userName);
        submissionDTO.setSubmittentListCheckedOn(submissionEntity.getSubmittentListCheckedOn());
      }
    }
    return submissionDTO;
  }

  @Override
  public Set<ValidationError> updateSubmission(SubmissionDTO submission) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateSubmission, Parameters: submission: {0}",
      submission);

    // Check for Optimistic Locking if project is deleted by another user
    Set<ValidationError> optimisticLockErrors = optimisticLockDeleteCheck(submission.getId());

    if (!optimisticLockErrors.isEmpty()) {
      return optimisticLockErrors;
    }

    SubmissionEntity submissionEntity = SubmissionMapper.INSTANCE.toSubmission(submission);
    if (submission.getPmExternal() != null) {
      submissionEntity
        .setPmExternal(em.find(CompanyEntity.class, submission.getPmExternal().getId()));
    }
    if (submission != null) {
      if (submission.getChangeForSec() != null && submission.getChangeForSec()) {
        /*
         * if the flag isLocked is changed, then an update in the security resources for the locked
         * submissions is needed the flag changeForSec is set to true if isLocked is changed the
         * security resources for the locked submissions must be updated before the resources for
         * Eingereichte Offerte list because they affect them
         */
        if (submission.getIsLockedChanged()) {
          security.editSubmissionLockedGroupResources(submission.getId(), submission.getIsLocked());
        }
        /*
         * an update in the security resources of the submission (for Eingereichte Offerte list) is
         * needed
         */
        security.editSubmissionListGroupResources(submissionEntity,
          getCurrentStatusOfSubmission(submission.getId()));
      }

      // we need to set the flag is updated of the fields
      // PmDepartmentName, PmExternal, Procedure, GattTwo
      updateIsUpdatedFlagOnUpdate(submissionEntity);

      // If the publicationDateAward is set or the gattTwo is set to false, then a check must be
      // made
      // if a to do task exists to set the publicationDateAward. If it exists then it must be
      // deleted
      // and if the other preconditions for the automatically closure of the submission apply, then
      // the submission must be closed.
      if (submissionEntity.getPublicationDateAward() != null || !submissionEntity.getGattTwo()) {
        checkToDeleteTaskAndCloseSubmission(submissionEntity);
      }
    }
    em.merge(submissionEntity);
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
    SubmissionEntity submissionEntity = em.find(SubmissionEntity.class, id);
    if (submissionEntity == null) {
      optimisticLockErrors
        .add(new ValidationError("optimisticLockErrorField",
          ValidationMessages.OPTIMISTIC_LOCK_DELETE));
    }
    return optimisticLockErrors;
  }

  @Override
  public String setCompanyToSubmission(String submissionId, List<String> companyIds) {

    LOGGER.log(Level.CONFIG,
      "Executing method setCompanyToSubmission, Parameters: submissionId: {0} , "
        + "companyIds: {1}",
      new Object[]{submissionId, companyIds});

    List<OfferEntity> offerEntities = new ArrayList<>();

    SubmissionEntity submissionEntity = em.find(SubmissionEntity.class, submissionId);
    for (String companyId : companyIds) {
      CompanyEntity companyEntity = em.find(CompanyEntity.class, companyId);
      SubmittentEntity submittentEntity = new SubmittentEntity();
      submittentEntity.setCompanyId(companyEntity);
      submittentEntity.setSubmissionId(submissionEntity);
      submittentEntity.setCreatedOn(new Timestamp(new Date().getTime()));
      submittentEntity.setCreatedBy(getUserId());
      // If the submission process is selective and the status APPLICATION_OPENING_CLOSED is not set
      // yet, add the submittent as an applicant as well.
      if (submissionEntity.getProcess().equals(Process.SELECTIVE)
        && !compareCurrentVsSpecificStatus(TenderStatus.fromValue(submissionEntity.getStatus()),
        TenderStatus.APPLICATION_OPENING_CLOSED)) {
        submittentEntity.setIsApplicant(true);
      }
      submittentEntity.setCreatedBy(getUserId());
      submittentEntity.setCreatedOn(new Timestamp(new Date().getTime()));
      em.persist(submittentEntity);

      OfferEntity offerEntity = projectBean.createDefaultOffer(submittentEntity);
      // Create offer criteria and offer sub-criteria for default offer.
      offerService.createOfferCriteriaAndSubcriteriaForDefaultOffer(
        OfferDTOMapper.INSTANCE.toOfferDTO(offerEntity));
      offerEntities.add(offerEntity);
    }

    /**
     * Check if submission has already the status after the add of a submittent
     */
    List<TenderStatusHistoryDTO> tenderStatusHistoryList =
      tenderStatusHistoryService.retrieveSubmissionSpecificStatuses(submissionId,
        TenderStatus.SUBMITTENT_LIST_CREATED.getValue());
    if (tenderStatusHistoryList.isEmpty()
      || (!tenderStatusHistoryList.isEmpty()) && submissionEntity.getSubmittents().isEmpty()) {
      if (submissionEntity.getProcess().equals(Process.SELECTIVE)) {
        if (!compareCurrentVsSpecificStatus(TenderStatus.fromValue(submissionEntity.getStatus()),
          TenderStatus.APPLICANTS_LIST_CREATED)) {
          // Set status of submission to APPLICANTS_LIST_CREATED if process type is selective and
          // no higher status has been set yet.
          updateSubmissionStatus(submissionEntity, TenderStatus.APPLICANTS_LIST_CREATED.getValue(),
            null, null, LookupValues.INTERNAL_LOG, getUserId());
        }
      } else {
        // In any other case set status of submission to SUBMITTENT_LIST_CREATED.
        updateSubmissionStatus(submissionEntity, TenderStatus.SUBMITTENT_LIST_CREATED.getValue(),
          null, null, LookupValues.INTERNAL_LOG, getUserId());
      }
    }

    // Create log entry if the submittentenliste has been examined (geprüft).
    if (!tenderStatusHistoryService.retrieveSubmissionSpecificStatuses(submissionId,
      TenderStatus.SUBMITTENTLIST_CHECKED.getValue()).isEmpty()) {

      MasterListValueHistoryEntity workType = new JPAQueryFactory(em)
        .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
        .where(qMasterListValueHistoryEntity.masterListValueId.eq(submissionEntity.getWorkType())
          .and(qMasterListValueHistoryEntity.toDate.isNull()))
        .fetchOne();

      MasterListValueHistoryEntity objectEntity = new JPAQueryFactory(em)
        .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
        .where(qMasterListValueHistoryEntity.masterListValueId
          .eq(submissionEntity.getProject().getObjectName())
          .and(qMasterListValueHistoryEntity.toDate.isNull()))
        .fetchOne();

      String workTypeValue2 = (StringUtils.isEmpty(workType.getValue2())) ? StringUtils.EMPTY
        : " " + workType.getValue2();

      StringBuilder submissionVars =
        new StringBuilder(submissionEntity.getProject().getProjectName()).append("[#]")
          .append(objectEntity.getValue1()).append("[#]")
          .append(workType.getValue1() + workTypeValue2).append("[#]")
          .append(usersService.getUserById(getUser().getId()).getTenant().getId()).append("[#]");

      auditLog(AuditEvent.ADD.name(), AuditMessages.SUBMITTENT_ADDED.name(), submissionId,
        submissionVars.toString());
    }

    Collections.sort(offerEntities, ComparatorUtil.sortOfferEntities);

    return offerEntities.get(0).getId();
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SubmissionService#getCompaniesBySubmission(java.
   * lang.String)
   */
  @Override
  public List<SubmittentOfferDTO> getCompaniesBySubmission(String id) {

    LOGGER.log(Level.CONFIG,
      "Executing method getCompaniesBySubmission, Parameters: id: {0}",
      id);

    security.isPermittedOperationForUser(getUserId(),
      SecurityOperation.TENDER_VIEW.getValue(), id);

    // Get process type of submission.
    SubmissionEntity submissionEntity = em.find(SubmissionEntity.class, id);
    // Get status of submission.
    List<OfferEntity> offerEntityList;
    if (submissionEntity.getProcess().equals(Process.SELECTIVE)
      && !compareCurrentVsSpecificStatus(TenderStatus.fromValue(submissionEntity.getStatus()),
      TenderStatus.SUITABILITY_AUDIT_COMPLETED_S)) {
      // If the process type is selective and the suitability audit has not been completed yet,
      // fetch
      // only submittents who are not applicants.
      offerEntityList = new JPAQueryFactory(em).select(qOfferEntity).from(qOfferEntity)
        .where(qOfferEntity.submittent.submissionId.id.eq(id)
          .and(qOfferEntity.submittent.isApplicant.isNull()))
        .fetch();
    } else {
      // Fetch all submittents who are either submittents by default, or are applicants who have
      // not been excluded from the process.
      offerEntityList = new JPAQueryFactory(em).select(qOfferEntity).from(qOfferEntity)
        .where(qOfferEntity.submittent.submissionId.id.eq(id)
          .and((qOfferEntity.submittent.isApplicant.eq(Boolean.TRUE)
            .and(qOfferEntity.excludedFirstLevel.eq(Boolean.FALSE)))
            .or(qOfferEntity.submittent.isApplicant.eq(Boolean.FALSE))
            .or(qOfferEntity.submittent.isApplicant.isNull())))
        .fetch();
    }
    List<SubmittentOfferDTO> submittentOfferDTOs = new ArrayList<>();
    for (OfferEntity offer : offerEntityList) {
      SubmittentOfferDTO submittentOfferDTO = new SubmittentOfferDTO();
      OfferDTO offerDTO = OfferDTOMapper.INSTANCE.toOfferDTO(offer);
      SubmittentDTO submittentDTO =
        SubmittentDTOMapper.INSTANCE.toSubmittentDTO(offer.getSubmittent());
      submittentOfferDTO.setOffer(offerDTO);
      submittentOfferDTO.setSubmittent(submittentDTO);
      submittentOfferDTOs.add(submittentOfferDTO);
    }
    // Sort offers in case submission process is negotiated procedure with competition.
    if (submissionEntity.getProcess().equals(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION)
      && compareCurrentVsSpecificStatus(TenderStatus.fromValue(submissionEntity.getStatus()),
      TenderStatus.FORMAL_AUDIT_COMPLETED)) {
      Collections.sort(submittentOfferDTOs,
        ComparatorUtil.sortOffersForNegotiatedProcedureWithCompetition);
    } else {
      Collections.sort(submittentOfferDTOs, ComparatorUtil.sortCompaniesByOffers);
    }
    return submittentOfferDTOs;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SubmissionService#getCurrentStatusOfSubmission(java
   * .lang.String)
   */
  @Override
  public String getCurrentStatusOfSubmission(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getCurrentStatusOfSubmission, Parameters: "
        + "submissionId: {0}",
      submissionId);

    JPAQuery<String> query = new JPAQuery<>(em);
    return query.select(qTenderStatusHistoryEntity.statusId).from(qTenderStatusHistoryEntity)
      .where(qTenderStatusHistoryEntity.tenderId.id.eq(submissionId))
      /* so that we get the current status (status set on the latest date) first */
      .orderBy(qTenderStatusHistoryEntity.onDate.desc()).fetchFirst();
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SubmissionService#getSubmissionByCompanyId(java.
   * lang.String)
   */
  @Override
  public List<SubmissionDTO> getSubmissionByCompanyId(String companyId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getSubmissionByCompanyId, Parameters: companyId: {0}",
      companyId);

    QProjectEntity qProjectEntity = QProjectEntity.projectEntity;
    return SubmissionMapper.INSTANCE.toSubmissionDTOList(new JPAQueryFactory(em)
      .select(qSubmittentEntity.submissionId).from(qSubmittentEntity)
      .where((qSubmittentEntity.jointVentures.any().id.eq(companyId)
        .or(qSubmittentEntity.subcontractors.any().id.eq(companyId))
        .or(qSubmittentEntity.companyId.id.eq(companyId)))
        .and(qSubmittentEntity.submissionId.project.in(new JPAQueryFactory(em)
          .select(qProjectEntity).from(qProjectEntity)
          .where(qProjectEntity.tenant.id.eq(
            getUser().getAttributeData(LookupValues.USER_ATTRIBUTES.TENANT.getValue())))
          .fetch())))
      .fetch());
  }


  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SubmissionService#hasSubmissionStatus(java.lang.
   * String, java.lang.String)
   */
  @Override
  public Boolean hasSubmissionStatus(String submissionId, String statusId) {

    LOGGER.log(Level.CONFIG,
      "Executing method hasSubmissionStatus, Parameters: submissionId: {0}, statusId: {1}",
      new Object[]{submissionId, statusId});

    JPAQuery<TenderStatusHistoryEntity> query = new JPAQuery<>(em);
    // check if an entry exists in the database with the current tender and
    // current status
    long hasSubmissionStatus = query.select(qTenderStatusHistoryEntity)
      .from(qTenderStatusHistoryEntity).where(qTenderStatusHistoryEntity.tenderId.id
        .eq(submissionId).and(qTenderStatusHistoryEntity.statusId.eq(statusId)))
      .fetchCount();
    return (hasSubmissionStatus == 0) ? false : true;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SubmissionService#checkSubmittentList(java.lang.
   * String)
   */
  @Override
  public void checkSubmittentList(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method checkSubmittentList, Parameters: submissionId: {0}",
      submissionId);

    SubmissionEntity submissionEntity = em.find(SubmissionEntity.class, submissionId);
    if (submissionEntity != null) {
      updateSubmissionStatus(submissionEntity, TenderStatus.SUBMITTENTLIST_CHECK.getValue(),
        AuditMessages.SUBMITTENTLIST_CHECK.name(), null, LookupValues.EXTERNAL_LOG, getUserId());

      createTask(SubmissionMapper.INSTANCE.toSubmissionDTO(submissionEntity),
        TaskTypes.CHECK_TENDERLIST);

    }
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SubmissionService#checkedSubmittentList(java.lang.
   * String)
   */
  @Override
  public String checkedSubmittentList(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method checkedSubmittentList, Parameters: submissionId: {0}",
      submissionId);

    String email = null;
    SubmissionEntity submissionEntity = em.find(SubmissionEntity.class, submissionId);
    if (submissionEntity != null) {
      updateSubmissionStatus(submissionEntity, TenderStatus.SUBMITTENTLIST_CHECKED.getValue(),
        AuditMessages.SUBMITTENTLIST_CHECKED.name(), null, LookupValues.EXTERNAL_LOG,
        getUserId());
      email = emailService.checkedSubmittentListEmail(submissionEntity.getId());
      taskService
        .settleTask(submissionEntity.getId(), null, TaskTypes.CHECK_TENDERLIST, null, null);
    }
    return email;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SubmissionService#getSubmittentsBySubmission(java.
   * lang.String)
   */
  @Override
  public List<SubmittentDTO> getSubmittentsBySubmission(String id) {

    LOGGER.log(Level.CONFIG,
      "Executing method getSubmittentsBySubmission, Parameters: id: {0}",
      id);
    // Get the status of submission in order to calculate the proof status in the current
    // submission.
    SelectiveLevel level = null;
    Process processType = new JPAQueryFactory(em).select(qSubmittentEntity.submissionId.process)
      .from(qSubmittentEntity).where(qSubmittentEntity.submissionId.id.eq(id)).fetchOne();
    List<SubmittentEntity> submittentEntities;
    String status = getCurrentStatusOfSubmission(id);
    if (processType == Process.SELECTIVE
      && compareCurrentVsSpecificStatus(TenderStatus.fromValue(status),
      TenderStatus.SUITABILITY_AUDIT_COMPLETED_S)) {
      submittentEntities = new JPAQueryFactory(em).select(qSubmittentEntity).from(qSubmittentEntity)
        .where(qSubmittentEntity.submissionId.id.eq(id)
          .and(qSubmittentEntity.in(JPAExpressions.select(qOfferEntity.submittent)
            .from(qOfferEntity)
            .where(qOfferEntity.isEmptyOffer.isFalse().or(qOfferEntity.isEmptyOffer.isNull())
              .and(qOfferEntity.excludedFirstLevel.eq(Boolean.FALSE)
                .and(qSubmittentEntity.isApplicant.eq(Boolean.TRUE))
                .or(qSubmittentEntity.isApplicant.isNull()))))))
        .orderBy(qSubmittentEntity.sortOrder.asc()).fetch();
    } else {
      submittentEntities =
        new JPAQueryFactory(em).select(qSubmittentEntity).from(qSubmittentEntity)
          .where(qSubmittentEntity.submissionId.id.eq(id)
            .and(qSubmittentEntity.isApplicant.isNull())
            .and(qSubmittentEntity.in(JPAExpressions.select(qOfferEntity.submittent)
              .from(qOfferEntity).where(qOfferEntity.isEmptyOffer.isFalse()))))
          .orderBy(qSubmittentEntity.sortOrder.asc()).fetch();
    }
    List<SubmittentDTO> submittentDTOs = new ArrayList<>();
    Date deadline;
    if (processType.equals(Process.SELECTIVE)) {
      deadline = new JPAQueryFactory(em).select(qSubmittentEntity.submissionId.firstDeadline)
        .from(qSubmittentEntity).where(qSubmittentEntity.submissionId.id.eq(id)).fetchOne();
      level = SelectiveLevel.SECOND_LEVEL;
    } else {
      deadline = new JPAQueryFactory(em).select(qSubmittentEntity.submissionId.secondDeadline)
        .from(qSubmittentEntity).where(qSubmittentEntity.submissionId.id.eq(id)).fetchOne();
      level = SelectiveLevel.ZERO_LEVEL;
    }

    if (deadline != null) {
      submittentDTOs = calculateSubmittentValues(submittentEntities, deadline, processType, level);
    }
    retrieveFormalAuditSavedValues(processType, status, submittentDTOs);
    return submittentDTOs;
  }

  private void retrieveFormalAuditSavedValues(Process processType, String status,
    List<SubmittentDTO> submittentDTOs) {
    if (((processType.equals(Process.OPEN) || processType.equals(Process.INVITATION))
      && compareCurrentVsSpecificStatus(TenderStatus.fromValue(status),
      TenderStatus.SUITABILITY_AUDIT_COMPLETED_AND_AWARD_EVALUATION_STARTED))
      || ((processType.equals(Process.SELECTIVE)
      || processType.equals(Process.NEGOTIATED_PROCEDURE)
      || processType.equals(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION))
      && compareCurrentVsSpecificStatus(
      TenderStatus.fromValue(status),
      TenderStatus.FORMAL_AUDIT_COMPLETED))) {
      // Create list of submittent ids.
      List<String> submittentIds = new ArrayList<>();
      for (SubmittentDTO submittentDTO : submittentDTOs) {
        submittentIds.add(submittentDTO.getId());
      }
      // Get submittent entities from submittent ids.
      List<SubmittentEntity> submittentEntities = new JPAQueryFactory(em)
        .selectFrom(qSubmittentEntity)
        .where(qSubmittentEntity.id.in(submittentIds)).fetch();
      // Map submittent ids to submittent entities.
      Map<String, SubmittentEntity> subIdsToSubEntities = new HashMap<>();
      for (SubmittentEntity submittentEntity : submittentEntities) {
        subIdsToSubEntities.put(submittentEntity.getId(), submittentEntity);
      }
      // Get the proofProvidedEntities of all submittents.
      List<SubmittentProofProvidedEntity> proofProvidedEntities = new JPAQueryFactory(em)
        .selectFrom(qSubmittentProofProvided)
        .where(qSubmittentProofProvided.submittent.id.in(submittentIds)
          .and(qSubmittentProofProvided.level.in(SelectiveLevel.SECOND_LEVEL.getValue(),
            SelectiveLevel.ZERO_LEVEL.getValue())))
        .fetch();
      // Mapper used to map the submittent ids to an additional mapper used to map the id of
      // companies (submittent/joint venture/subcontractor), which are included in a submittent,
      // to their proof provided entities.
      Map<String, Map<String, SubmittentProofProvidedEntity>> submittentIdsToProofMappers =
        new HashMap<>();
      for (SubmittentProofProvidedEntity proofProvidedEntity : proofProvidedEntities) {
        if (submittentIdsToProofMappers.get(proofProvidedEntity.getSubmittent().getId()) == null) {
          // Create new inner mapper for the current submittent, if none exists.
          submittentIdsToProofMappers
            .put(proofProvidedEntity.getSubmittent().getId(), new HashMap<>());
        }
        // Map submittent id to company id to proofProvidedEntity.
        submittentIdsToProofMappers.get(proofProvidedEntity.getSubmittent().getId())
          .put(proofProvidedEntity.getCompany().getId(), proofProvidedEntity);
      }
      for (SubmittentDTO submittentDTO : submittentDTOs) {
        SubmittentEntity submittentEntity = subIdsToSubEntities.get(submittentDTO.getId());
        // Set formalExaminationFulfilled to submittent DTO.
        submittentDTO
          .setFormalExaminationFulfilled(submittentEntity.getFormalExaminationFulfilled());
        // Set existsExclusionReasons to submittent DTO.
        submittentDTO.setExistsExclusionReasons(submittentEntity.getExistsExclusionReasons());
        // Set isProofProvided to company of submittent.
        if (!submittentIdsToProofMappers.isEmpty()) {
          submittentDTO.getCompanyId().setIsProofProvided(
            submittentIdsToProofMappers.get(submittentDTO.getId())
              .get(submittentDTO.getCompanyId().getId()).getIsProvided());
          for (CompanyDTO jointVenture : submittentDTO.getJointVentures()) {
            // Set isProofProvided to joint venture.
            jointVenture.setIsProofProvided(
              submittentIdsToProofMappers.get(submittentDTO.getId()).get(jointVenture.getId())
                .getIsProvided());
          }
          for (CompanyDTO subcontractor : submittentDTO.getSubcontractors()) {
            // Set isProofProvided to subcontractor.
            subcontractor.setIsProofProvided(
              submittentIdsToProofMappers.get(submittentDTO.getId()).get(subcontractor.getId())
                .getIsProvided());
          }
        }
      }
    }
  }

  @Override
  public Set<ValidationError> updateFormalAuditExamination(List<SubmittentDTO> submittentDTOs) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateFormalAuditExamination, Parameters: submittentDTOs: {0}",
      submittentDTOs);

    Set<ValidationError> optimisticLockErrors = new HashSet<>();

    Long submissionDbVersion = new JPAQueryFactory(em).select(qSubmissionEntity.version)
      .from(qSubmissionEntity)
      .where(qSubmissionEntity.id.eq(submittentDTOs.get(0).getSubmissionId().getId())).fetchOne();

    if (!submissionDbVersion.equals(submittentDTOs.get(0).getSubmissionId().getVersion())) {
      optimisticLockErrors
        .add(
          new ValidationError(ValidationMessages.OPTIMISTIC_LOCK_ERROR_FIELD,
            ValidationMessages.OPTIMISTIC_LOCK));
      return optimisticLockErrors;
    }

    // Getting the process from the first submittent.
    Process process = submittentDTOs.get(0).getSubmissionId().getProcess();
    // List of submittent ids, whose existsExclusionReasons property has been changed.
    List<String> changedSubmittentIds = new ArrayList<>();

    for (SubmittentDTO s : submittentDTOs) {
      SubmittentEntity submittentEntity = em.find(SubmittentEntity.class, s.getId());
      if (submittentEntity != null) {
        /*
         * Check dto and entity version.
         * If these 2 are not equal, another user has already saved the form.
         * Return an optimisticLockErrorField.
         */
        if (!submittentEntity.getVersion().equals(s.getVersion())) {
          optimisticLockErrors
            .add(new ValidationError(ValidationMessages.OPTIMISTIC_LOCK_ERROR_FIELD,
              ValidationMessages.OPTIMISTIC_LOCK));
          return optimisticLockErrors;
        }

        if (submittentEntity.getExistsExclusionReasons() != s.getExistsExclusionReasons()) {
          submittentEntity.setExistsExclusionReasons(s.getExistsExclusionReasons());
          changedSubmittentIds.add(s.getId());

        }
        if (s.getFormalExaminationFulfilled() != null) {
          submittentEntity.setFormalExaminationFulfilled(s.getFormalExaminationFulfilled());

        }
        if (s.getFormalAuditNotes() != null) {
          submittentEntity.setFormalAuditNotes(s.getFormalAuditNotes());
        }
        em.merge(submittentEntity);
        SelectiveLevel level;
        if (process.equals(Process.SELECTIVE)) {
          level = SelectiveLevel.SECOND_LEVEL;
        } else {
          level = SelectiveLevel.ZERO_LEVEL;
        }
        setSubmittentProofProvided(submittentEntity, level, s);
      }
    }
    if (process.equals(Process.OPEN) || process.equals(Process.INVITATION)) {
      // For the submittents whose existsExclusionReasons property has been changed, their
      // qExExaminationIsFulfilled offer property needs to be reset to null.
      List<OfferEntity> offerEntities = new JPAQueryFactory(em).selectFrom(qOfferEntity)
        .where(qOfferEntity.submittent.id.in(changedSubmittentIds)).fetch();
      for (OfferEntity offerEntity : offerEntities) {
        offerEntity.setqExExaminationIsFulfilled(null);
        em.merge(offerEntity);
      }
    }
    return optimisticLockErrors;
  }

  /**
   * This function is used to save the Formelle Prufung values of every company (Freeze
   * impelementation). Feature : Formelle Prüfung - Values of Nachweise erbracht after
   * Eignungsprüfung abgeschlossen.
   *
   * @param submittentEntity the submittent entity
   * @param level            the level
   * @param s                the s
   */
  private void setSubmittentProofProvided(SubmittentEntity submittentEntity, SelectiveLevel level,
    SubmittentDTO s) {
    // Save proofProvided value of submittent into separate table
    if (level != null) {
      List<SubmittentProofProvidedEntity> submittentProofProvidedList = new JPAQueryFactory(em)
        .selectFrom(qSubmittentProofProvided).where(qSubmittentProofProvided.submittent
          .eq(submittentEntity).and(qSubmittentProofProvided.level.eq(level.getValue())))
        .fetch();
      // Check if proofs has been saved before.
      // If proofs has not been saved yet, create new entity.
      if (submittentProofProvidedList.isEmpty()) {
        // List of company ids for which the SubmittentProofProvidedEntity has been created.
        List<String> createdProofCompanyIds = new ArrayList<>();
        // Create SubmittentProofProvidedEntity for submittent.
        initSubmittentProofProvided(submittentEntity, submittentEntity.getCompanyId(), level,
          s.getCompanyId().getIsProofProvided());
        createdProofCompanyIds.add(submittentEntity.getCompanyId().getId());
        for (CompanyDTO jointVenture : s.getJointVentures()) {
          // Check if the SubmittentProofProvidedEntity has not been created for this joint
          // venture (company).
          if (!createdProofCompanyIds.contains(jointVenture.getId())) {
            // Create SubmittentProofProvidedEntity for joint venture.
            initSubmittentProofProvided(submittentEntity,
              CompanyMapper.INSTANCE.toCompany(jointVenture), level,
              jointVenture.getIsProofProvided());
            createdProofCompanyIds.add(jointVenture.getId());
          }
        }
        for (CompanyDTO subcontractor : s.getSubcontractors()) {
          // Check if the SubmittentProofProvidedEntity has not been created for this subcontractor
          // (company).
          if (!createdProofCompanyIds.contains(subcontractor.getId())) {
            // Create SubmittentProofProvidedEntity for subcontractor.
            initSubmittentProofProvided(submittentEntity,
              CompanyMapper.INSTANCE.toCompany(subcontractor), level,
              subcontractor.getIsProofProvided());
            createdProofCompanyIds.add(subcontractor.getId());
          }
        }
      } else {
        setProofProvidedValues(submittentProofProvidedList, level, s);
      }
    }
  }

  /**
   * Inits the submittent proof provided.
   *
   * @param submittentEntity the submittent entity
   * @param companyEntity    the company entity
   * @param level            the level
   * @param proofProvided    the proof provided
   */
  private void initSubmittentProofProvided(SubmittentEntity submittentEntity,
    CompanyEntity companyEntity, SelectiveLevel level, Boolean proofProvided) {
    SubmittentProofProvidedEntity submittentProofProvided = new SubmittentProofProvidedEntity();
    submittentProofProvided.setSubmittent(submittentEntity);
    submittentProofProvided.setCompany(companyEntity);
    submittentProofProvided.setIsProvided(proofProvided);
    submittentProofProvided.setLevel(level.getValue());
    em.merge(submittentProofProvided);
  }

  /**
   * Sets the company proof provided values.
   *
   * @param subProofProvidedEntities the submittent proof provided entities
   * @param level                    the level
   * @param submittentDTO            the submittent DTO
   */
  private void setProofProvidedValues(List<SubmittentProofProvidedEntity> subProofProvidedEntities,
    SelectiveLevel level, SubmittentDTO submittentDTO) {
    // Map company ids to submittent proof provided entities.
    Map<String, SubmittentProofProvidedEntity> companyIdsToProofEntities = new HashMap<>();
    for (SubmittentProofProvidedEntity proofEntity : subProofProvidedEntities) {
      companyIdsToProofEntities
        .put(proofEntity.getCompany().getId(), proofEntity);
    }
    // Create companyDTOs list.
    List<CompanyDTO> companyDTOs = new ArrayList<>();
    // Add submittent, joint ventures and subcontractors to companyDTOs list.
    companyDTOs.add(submittentDTO.getCompanyId());
    for (CompanyDTO jointVenture : submittentDTO.getJointVentures()) {
      companyDTOs.add(jointVenture);
    }
    for (CompanyDTO subcontractor : submittentDTO.getSubcontractors()) {
      companyDTOs.add(subcontractor);
    }
    // List of company ids for which the SubmittentProofProvidedEntity has been created.
    List<String> createdProofCompanyIds = new ArrayList<>();
    for (CompanyDTO companyDTO : companyDTOs) {
      // Get proofEntity of current company.
      SubmittentProofProvidedEntity proofEntity = companyIdsToProofEntities.get(companyDTO.getId());
      // Check if the proofEntity exists for the current company.
      if (proofEntity != null) {
        // Set isProvided value to proofEntity from company.
        proofEntity.setIsProvided(companyDTO.getIsProofProvided());
        em.merge(proofEntity);
      } else {
        // Check if the SubmittentProofProvidedEntity has not already been created for this
        // company (while executing the current function).
        if (!createdProofCompanyIds.contains(companyDTO.getId())) {
          // Create new proof entity.
          SubmittentProofProvidedEntity newProofEntity = new SubmittentProofProvidedEntity();
          newProofEntity.setSubmittent(SubmittentDTOMapper.INSTANCE.toSubmittent(submittentDTO));
          newProofEntity.setCompany(CompanyMapper.INSTANCE.toCompany(companyDTO));
          newProofEntity.setIsProvided(companyDTO.getIsProofProvided());
          newProofEntity.setLevel(level.getValue());
          em.merge(newProofEntity);
          createdProofCompanyIds.add(companyDTO.getId());
        }
      }
    }
  }

  @Override
  public Set<ValidationError> updateSelectiveFormalAudit(List<SubmittentDTO> submittentDTOs) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateSelectiveFormalAudit, Parameters: submittentDTOs: {0}",
      submittentDTOs);

    Set<ValidationError> optimisticLockErrors = new HashSet<>();
    try {
      if (submittentDTOs != null && !submittentDTOs.isEmpty()) {
        // If there are submittents present, get their submission id from the first submittent.
        String submissionId = submittentDTOs.get(0).getSubmissionId().getId();
        // Get the formal audit entities connected to the submission.
        List<FormalAuditEntity> formalAuditEntities =
          new JPAQueryFactory(em).selectFrom(qFormalAuditEntity)
            .where(qFormalAuditEntity.submittent.submissionId.id.eq(submissionId)).fetch();
        // Check if the form is already saved by another user
        if (checkSelectiveFormalAuditOptimisticLock(submittentDTOs, formalAuditEntities)) {
          optimisticLockErrors
            .add(new ValidationError(ValidationMessages.OPTIMISTIC_LOCK_ERROR_FIELD,
              ValidationMessages.OPTIMISTIC_LOCK));
          return optimisticLockErrors;
        }
        // List of submittent ids, whose existsExclusionReasons property has been changed.
        List<String> changedSubmittentIds = new ArrayList<>();
        for (SubmittentDTO submittentDTO : submittentDTOs) {
          boolean formatAuditFound = false;
          for (FormalAuditEntity formalAuditEntity : formalAuditEntities) {
            // Check if the formal audit entity is connected to the current submittent.
            if (formalAuditEntity.getSubmittent().getId().equals(submittentDTO.getId())) {
              formatAuditFound = true;
              // Update the formal audit values.
              if (formalAuditEntity.getExistsExclusionReasons() != submittentDTO
                .getExistsExclusionReasons()) {
                formalAuditEntity
                  .setExistsExclusionReasons(submittentDTO.getExistsExclusionReasons());
                changedSubmittentIds.add(submittentDTO.getId());
              }
              if (submittentDTO.getFormalExaminationFulfilled() != null) {
                formalAuditEntity
                  .setFormalExaminationFulfilled(submittentDTO.getFormalExaminationFulfilled());
              }
              em.merge(formalAuditEntity);
              break;
            }
          }
          // If there is no formal audit entity connected to the submittent, create one.
          if (!formatAuditFound) {
            FormalAuditEntity formalAuditEntity = new FormalAuditEntity();
            formalAuditEntity
              .setSubmittent(SubmittentDTOMapper.INSTANCE.toSubmittent(submittentDTO));
            formalAuditEntity.setExistsExclusionReasons(submittentDTO.getExistsExclusionReasons());
            formalAuditEntity
              .setFormalExaminationFulfilled(submittentDTO.getFormalExaminationFulfilled());
            em.merge(formalAuditEntity);
          }
          setSubmittentProofProvided(SubmittentDTOMapper.INSTANCE.toSubmittent(submittentDTO),
            SelectiveLevel.FIRST_LEVEL, submittentDTO);
        }
        // For the submittents whose existsExclusionReasons property has been changed, their
        // qExExaminationIsFulfilled offer property needs to be reset to null.
        List<OfferEntity> offerEntities = new JPAQueryFactory(em).selectFrom(qOfferEntity)
          .where(qOfferEntity.submittent.id.in(changedSubmittentIds)).fetch();
        for (OfferEntity offerEntity : offerEntities) {
          offerEntity.setqExExaminationIsFulfilled(null);
          em.merge(offerEntity);
        }
      }
    } catch (OptimisticLockException | RollbackException e) {
      optimisticLockErrors
        .add(new ValidationError(ValidationMessages.OPTIMISTIC_LOCK_ERROR_FIELD,
          ValidationMessages.OPTIMISTIC_LOCK));
    }
    return optimisticLockErrors;
  }

  /**
   * Checks if the selective formal audit form is already updated by another user.
   *
   * @param submittentDTOs      the submittentDTOs
   * @param formalAuditEntities the formalAuditEntities
   * @return true/false
   */
  private boolean checkSelectiveFormalAuditOptimisticLock(List<SubmittentDTO> submittentDTOs,
    List<FormalAuditEntity> formalAuditEntities) {

    LOGGER.log(Level.CONFIG,
      "Executing method checkSelectiveFormalAuditOptimisticLock, Parameters: submittentDTOs: {0}, "
        + "formalAuditEntities: {1}",
      new Object[]{submittentDTOs, formalAuditEntities});

    for (SubmittentDTO submittentDTO : submittentDTOs) {
      for (FormalAuditEntity formalAuditEntity : formalAuditEntities) {
        if (formalAuditEntity.getSubmittent().getId().equals(submittentDTO.getId())
          && !formalAuditEntity.getVersion().equals(submittentDTO.getFormalAuditVersion())) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Finds if submission has submittents (This method is used for validation when a submission is
   * deleted).
   *
   * @param submissionId the submission id
   * @return the boolean
   */
  @Override
  public Boolean findIfSubmissionHasSubmittent(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method findIfSubmissionHasSubmittent, Parameters: submissionId: {0}",
      submissionId);

    List<SubmittentEntity> submittentEntities = new JPAQueryFactory(em).select(qSubmittentEntity)
      .from(qSubmittentEntity).where(qSubmittentEntity.submissionId.id.eq(submissionId)).fetch();
    return !submittentEntities.isEmpty();
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SubmissionService#
   * updateSubmissionFormalAuditExaminationStatus(java.lang.String)
   */
  @Override
  public void updateSubmissionFormalAuditExaminationStatus(String id) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateSubmissionFormalAuditExaminationStatus, Parameters: id: {0}",
      id);

    SubmissionEntity submissionEntity = em.find(SubmissionEntity.class, id);
    if (submissionEntity != null) {
      String status = getCurrentStatusOfSubmission(submissionEntity.getId());
      if ((submissionEntity.getProcess().equals(Process.INVITATION)
        || submissionEntity.getProcess().equals(Process.OPEN))
        && !compareCurrentVsSpecificStatus(TenderStatus.fromValue(status),
        TenderStatus.FORMAL_EXAMINATION_AND_QUALIFICATION_STARTED)) {
        updateSubmissionStatus(submissionEntity,
          TenderStatus.FORMAL_EXAMINATION_AND_QUALIFICATION_STARTED.getValue(), null, null,
          LookupValues.INTERNAL_LOG, getUserId());

      }
      if ((submissionEntity.getProcess().equals(Process.NEGOTIATED_PROCEDURE)
        || submissionEntity.getProcess().equals(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION)
        || (submissionEntity.getProcess().equals(Process.SELECTIVE)
        && compareCurrentVsSpecificStatus(TenderStatus.fromValue(status),
        TenderStatus.OFFER_OPENING_CLOSED)))
        && !status.equals(TenderStatus.FORMAL_EXAMINATION_STARTED.getValue())) {
        updateSubmissionStatus(submissionEntity, TenderStatus.FORMAL_EXAMINATION_STARTED.getValue(),
          null, null, LookupValues.INTERNAL_LOG, getUserId());
      }
      if (submissionEntity.getProcess().equals(Process.SELECTIVE)
        && compareCurrentVsSpecificStatus(TenderStatus.fromValue(status),
        TenderStatus.APPLICATION_OPENING_CLOSED)
        && !compareCurrentVsSpecificStatus(TenderStatus.fromValue(status),
        TenderStatus.SUITABILITY_AUDIT_COMPLETED_S)
        && !compareCurrentVsSpecificStatus(TenderStatus.fromValue(status),
        TenderStatus.FORMAL_EXAMINATION_STARTED)) {
        updateSubmissionStatus(submissionEntity,
          TenderStatus.FORMAL_EXAMINATION_SUITABILITY_AUDIT_STARTED.getValue(), null, null,
          LookupValues.INTERNAL_LOG, getUserId());
      }
    }
  }

  /**
   * Update submission status.
   *
   * @param submissionEntity the submission entity
   * @param status           the status
   * @param auditDescription the audit description
   * @param reopenReason     the reopen reason
   * @param internalValue    the internal value
   * @param userId           the user id
   */
  private void updateSubmissionStatus(SubmissionEntity submissionEntity, String status,
    String auditDescription, String reopenReason, String internalValue, String userId) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateSubmissionStatus, Parameters: submissionEntity: {0}, "
        + "status: {1}, auditDescription: {2}, reopenReason: {3}, "
        + "internalValue: {4}, userId: {5}",
      new Object[]{submissionEntity, status, auditDescription, reopenReason, internalValue,
        userId});

    TenderStatusHistoryEntity tenderStatus = new TenderStatusHistoryEntity();
    tenderStatus.setTenderId(submissionEntity);
    tenderStatus.setStatusId(status);
    tenderStatus.setOnDate(new Timestamp(new Date().getTime()));
    em.persist(tenderStatus);


    /*
     * Update status at tender table
     */
    submissionEntity.setStatus(status);
    /*
     * set SubmittentListChecked info at tender table
     */
    if (status.equals(TenderStatus.SUBMITTENTLIST_CHECK.getValue())) {
      submissionEntity.setSubmittentListCheckedBy(userId);
      submissionEntity.setSubmittentListCheckedOn(new Date().getTime());
    }
    em.merge(submissionEntity);
    /*
     * check if an update in the security resources of the submission (for Eingereichte Offerte
     * list) is needed
     */
    security.editSubmissionListGroupResources(submissionEntity, status);

    MasterListValueHistoryEntity workType = new JPAQueryFactory(em)
      .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
      .where(qMasterListValueHistoryEntity.masterListValueId
        .eq(submissionEntity.getWorkType())
        .and(qMasterListValueHistoryEntity.toDate.isNull()))
      .fetchOne();

    MasterListValueHistoryEntity objectEntity = new JPAQueryFactory(em)
      .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
      .where(qMasterListValueHistoryEntity.masterListValueId
        .eq(submissionEntity.getProject().getObjectName())
        .and(qMasterListValueHistoryEntity.toDate.isNull()))
      .fetchOne();

    String workTypeValue2 = (StringUtils.isEmpty(workType.getValue2())) ? StringUtils.EMPTY
      : " " + workType.getValue2();

    StringBuilder additionalInfo = new StringBuilder(submissionEntity.getProject().getProjectName())
      .append("[#]").append(objectEntity.getValue1()).append("[#]")
      .append(workType.getValue1() + workTypeValue2).append("[#]")
      .append(submissionEntity.getProject().getTenant().getId()).append("[#]");

    audit.createLogAudit(AuditLevel.PROJECT_LEVEL.name(), AuditEvent.STATUS_CHANGE.name(),
      AuditGroupName.SUBMISSION.name(), auditDescription, userId, submissionEntity.getId(),
      reopenReason, additionalInfo.toString(), internalValue);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SubmissionService#updateSubmissionStatus(java.lang.
   * String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public void updateSubmissionStatus(String submissionId, String status, String description,
    String reopenReason, String internalValue) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateSubmissionStatus, Parameters: submissionId: {0}, status: {1}, "
        + "description: {2}, reopenReason: {3}, "
        + "internalValue: {4}",
      new Object[]{submissionId, status, description, reopenReason, internalValue});

    SubmissionEntity submissionEntity = em.find(SubmissionEntity.class, submissionId);
    updateSubmissionStatus(submissionEntity, status, description, reopenReason, internalValue,
      getUserId());
  }

  @Override
  public Set<ValidationError> updateSubmissionStatus(String submissionId, Long version,
    String status, String description,
    String reopenReason, String internalValue) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateSubmissionStatus, Parameters: submissionId: {0}, status: {1}, "
        + "description: {2}, reopenReason: {3}, "
        + "internalValue: {4}",
      new Object[]{submissionId, status, description, reopenReason, internalValue});

    Set<ValidationError> optimisticLockErrors = new HashSet<>();
    SubmissionEntity submissionEntity = em.find(SubmissionEntity.class, submissionId);
    if (!version.equals(submissionEntity.getVersion())) {
      optimisticLockErrors.add(new ValidationError(ValidationMessages.OPTIMISTIC_LOCK_ERROR_FIELD,
        ValidationMessages.OPTIMISTIC_LOCK));
      return optimisticLockErrors;
    }
    updateSubmissionStatus(submissionEntity, status, description, reopenReason, internalValue,
      getUserId());
    return optimisticLockErrors;
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SubmissionService#closeFormalAudit(java.lang.
   * String)
   */
  @Override
  public List<String> closeFormalAudit(String id) {

    LOGGER.log(Level.CONFIG,
      "Executing method closeFormalAudit, Parameters: id: {0}",
      id);

    List<String> messages = new ArrayList<>();
    SubmissionEntity submissionEntity = em.find(SubmissionEntity.class, id);
    List<OfferEntity> offerEntities =
      new JPAQueryFactory(em).select(qOfferEntity).from(qOfferEntity)
        .where(qOfferEntity.submittent.submissionId.id.eq(id)
          .and(qOfferEntity.isEmptyOffer.isFalse().or(qOfferEntity.isEmptyOffer.isNull()))
          .and(
            qOfferEntity.excludedFirstLevel.isFalse().or(qOfferEntity.excludedFirstLevel.isNull())))
        .fetch();

    for (OfferEntity offer : offerEntities) {
      // check if existsExclusionReasons is empty and return different values for Selective and
      // Negotiated processes.
      if (offer.getSubmittent().getExistsExclusionReasons() == null
        && submissionEntity.getProcess().equals(Process.SELECTIVE)
        && (offer.getSubmittent().getIsApplicant() == null
        || (offer.getSubmittent().getIsApplicant() && !offer.getExcludedFirstLevel()))) {
        messages.add(NULL_EXCLUSION_REASONS_SELECTIVE);
      } else if (offer.getSubmittent().getExistsExclusionReasons() == null
        && (submissionEntity.getProcess().equals(Process.NEGOTIATED_PROCEDURE) || submissionEntity
        .getProcess().equals(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION))
        && (offer.getIsEmptyOffer() == null || !offer.getIsEmptyOffer())) {
        messages.add(NULL_EXCLUSION_REASONS_NEGOTIATED);
      }

      // Check if Rechtliches Gehor document has been generated for 2nd level selective process.
      if (checkLegalHearingDocumentFormalAudit(submissionEntity, offer)) {
        messages.add(ValidationMessages.LEGAL_HEARING_DOCUMENT_SHOULD_BE_CREATED);
        break;
      }
    }

    // If no issue has been found, check if proof documents need to be generated for the 2nd level
    // selective process.
    if (messages.isEmpty() && submissionEntity.getProcess().equals(Process.SELECTIVE)) {
      generateProofDocuments(id, !messages.isEmpty(), messages, submissionEntity);
    }

    if (messages.isEmpty()) {
      // If process is selective, exclude all offers from process whose submittents have exclusion
      // reasons.
      if (submissionEntity.getProcess().equals(Process.SELECTIVE)) {
        for (OfferEntity offerEntity : offerEntities) {
          if ((offerEntity.getSubmittent().getIsApplicant() == null
            || (offerEntity.getSubmittent().getIsApplicant()
            && (offerEntity.getExcludedFirstLevel() == null
            || !offerEntity.getExcludedFirstLevel())))
            && (offerEntity.getSubmittent().getExistsExclusionReasons() != null
            && offerEntity.getSubmittent().getExistsExclusionReasons())) {
            offerEntity.setIsExcludedFromProcess(Boolean.TRUE);
            // If offer is excluded, set award offer criteria score and grade to null.
            for (OfferCriterionEntity offerCriterionEntity : offerEntity.getOfferCriteria()) {
              if (offerCriterionEntity.getCriterion().getCriterionType()
                .equals(LookupValues.AWARD_CRITERION_TYPE)) {
                offerCriterionEntity.setGrade(null);
                offerCriterionEntity.setScore(null);
              }
            }
            // If offer is excluded, set award offer sub-criteria score and grade to null.
            for (OfferSubcriterionEntity offerSubcriterionEntity : offerEntity
              .getOfferSubcriteria()) {
              if (offerSubcriterionEntity.getSubcriterion().getCriterion().getCriterionType()
                .equals(LookupValues.AWARD_CRITERION_TYPE)) {
                offerSubcriterionEntity.setGrade(null);
                offerSubcriterionEntity.setScore(null);
              }
            }
          } else {
            offerEntity.setIsExcludedFromProcess(Boolean.FALSE);
          }
        }
      } else {
        if ((submissionEntity.getProcess().equals(Process.NEGOTIATED_PROCEDURE) || submissionEntity
          .getProcess().equals(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION))) {
          for (OfferEntity offerEntity : offerEntities) {
            if (offerEntity.getSubmittent().getExistsExclusionReasons() != null
              && offerEntity.getSubmittent().getExistsExclusionReasons()) {
              offerEntity.setIsExcludedFromProcess(Boolean.TRUE);
            } else {
              offerEntity.setIsExcludedFromProcess(Boolean.FALSE);
            }
          }
        }
      }
      updateCloseFormalAuditStatus(submissionEntity);
      // Update the award evaluation page if applicable.
      if (submissionEntity.getProcess() == Process.SELECTIVE) {
        updateAwardEvaluationPage(id);
      }
    }
    return messages;
  }

  /**
   * Check legal hearing document formal audit.
   *
   * @param submissionEntity the submission entity
   * @param offer            the offer
   * @return true, if successful
   */
  private boolean checkLegalHearingDocumentFormalAudit(SubmissionEntity submissionEntity,
    OfferEntity offer) {

    LOGGER.log(Level.CONFIG,
      "Executing method checkLegalHearingDocumentFormalAudit, Parameters: "
        + "submissionEntity: {0}, "
        + "offer: {1}",
      new Object[]{submissionEntity, offer});

    boolean documentShouldBeCreated = false;
    HashMap<String, String> attributesMap = new HashMap<>();
    if (submissionEntity.getProcess().equals(Process.SELECTIVE)
      && (offer.getSubmittent().getExistsExclusionReasons() != null
      && offer.getSubmittent().getExistsExclusionReasons())
      && (offer.getSubmittent().getFormalExaminationFulfilled() != null
      && offer.getSubmittent().getFormalExaminationFulfilled())
      && (offer.getExcludedFirstLevel() == null || !offer.getExcludedFirstLevel())) {
      MasterListValueHistoryEntity templateId =
        new JPAQueryFactory(em).select(qMasterListValueHistoryEntity)
          .from(qMasterListValueHistoryEntity)
          .where(qMasterListValueHistoryEntity.shortCode.eq(Template.RECHTLICHES_GEHOR)
            .and(qMasterListValueHistoryEntity.toDate.isNull())
            .and(qMasterListValueHistoryEntity.tenant.id
              .eq(usersService.getUserById(getUser().getId()).getTenant().getId())))
          .fetchOne();
      attributesMap.put(DocumentAttributes.TEMPLATE_ID.name(),
        templateId.getMasterListValueId().getId());
      attributesMap.put(DocumentAttributes.TENDER_ID.name(), offer.getSubmittent().getId());
      attributesMap.put(DocumentAttributes.TENANT_ID.name(), templateId.getTenant().getId());
      attributesMap.put(DocumentAttributes.ADDITIONAL_INFO.name(), "EXCLUSION");
      // Return true if docs have not been generated.
      if (documentService.getNodeByAttributes(submissionEntity.getId(), attributesMap).isEmpty()) {
        documentShouldBeCreated = true;
      }
    }
    return documentShouldBeCreated;
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SubmissionService#closeExamination(java.lang.
   * String, java.math.BigDecimal, java.math.BigDecimal)
   */
  @Override
  public List<String> closeExamination(String submissionId, BigDecimal minGrade,
    BigDecimal maxGrade) {

    LOGGER.log(Level.CONFIG,
      "Executing method closeExamination, Parameters: id: {0}, "
        + "minGrade: {1}, maxGrade: {2}",
      new Object[]{submissionId, minGrade, maxGrade});

    boolean existsExclusionReasons = false;
    List<String> results = new ArrayList<>();
    SubmissionEntity submissionEntity = em.find(SubmissionEntity.class, submissionId);

    // Check if the saved proof provided values differ from the current proof provided values.
    if (sDProofService
      .checkProofsForInconsistencies(SubmissionMapper.INSTANCE.toSubmissionDTO(submissionEntity),
        mapProofProvidedValues(submissionId))) {
      existsExclusionReasons = true;
      results.add(PROOF_INCONSISTENCIES);
    }

    // If no exclusion reason exists yet, check if the legal hearing document needs to be created.
    if (!existsExclusionReasons) {
      for (SubmittentEntity submittent : submissionEntity.getSubmittents()) {
        if (submissionEntity.getProcess().equals(Process.SELECTIVE)) {
          if (!submittent.getOffer().getIsEmptyOffer()
            && (submittent.getIsApplicant() != null && submittent.getIsApplicant())
            && isLegalHearingDocumentRequired(submittent, submissionEntity)) {
            existsExclusionReasons = true;
            results.add(ValidationMessages.LEGAL_HEARING_DOCUMENT_SHOULD_BE_CREATED);
            break;
          }
        } else {
          if (!submittent.getOffer().getIsEmptyOffer()
            && isLegalHearingDocumentRequired(submittent, submissionEntity)) {
            existsExclusionReasons = true;
            results.add(ValidationMessages.LEGAL_HEARING_DOCUMENT_SHOULD_BE_CREATED);
            break;
          }
        }
      }
    }

    // If generateProofDocuments returns false then we shouldn't change existsExclusionReasons to false in case it is true.
    existsExclusionReasons |= generateProofDocuments(submissionId, existsExclusionReasons, results,
      submissionEntity);

    List<CriterionDTO> criterionDTOs =
      criterionService.readSubmissionEvaluatedCriteria(submissionId);
    double criterionWeightingSum = 0;
    boolean nullMinGradeMaxGrade = false;
    for (CriterionDTO criterionDTO : criterionDTOs) {
      if (criterionDTO.getCriterionType().equals(LookupValues.EVALUATED_CRITERION_TYPE)) {
        // Check if there are evaluated criteria present, but the minimum or maximum grades are set
        // as
        // null.
        if ((minGrade == null || maxGrade == null) && !nullMinGradeMaxGrade) {
          nullMinGradeMaxGrade = true;
          results.add(NULL_MIN_GRADE_MAX_GRADE);
          if (!existsExclusionReasons) {
            existsExclusionReasons = true;
          }
        }
        // Proceed if criterion contains sub-criteria.
        if (!criterionDTO.getSubcriterion().isEmpty()) {
          double subcriterionWeightingSum = 0;
          for (SubcriterionDTO subcriterionDTO : criterionDTO.getSubcriterion()) {
            subcriterionWeightingSum += subcriterionDTO.getWeighting();
          }
          // Check if sub-criteria weighting sum of criterion doesn't reach 100%.
          if (subcriterionWeightingSum != 100) {
            results.add(INVALID_SUBCRITERION_WEIGHTINGS);
            existsExclusionReasons = true;
          }
        }
        criterionWeightingSum += criterionDTO.getWeighting();
      }
    }
    // Check if criteria weighting sum doesn't reach 100%.
    if (criterionWeightingSum != 0 && criterionWeightingSum != 100) {
      results.add(INVALID_CRITERION_WEIGHTINGS);
      existsExclusionReasons = true;
    }
    // Check if user has not set values in Formelle Prüfung durchführen.
    List<OfferDTO> offerDTOs;
    if (submissionEntity.getProcess().equals(Process.SELECTIVE)) {
      offerDTOs = offerService.retrieveOfferApplicants(submissionId);
      List<FormalAuditEntity> formalAuditEntities =
        new JPAQueryFactory(em).selectFrom(qFormalAuditEntity)
          .where(qFormalAuditEntity.submittent.submissionId.id.eq(submissionId)).fetch();
      for (OfferDTO offerDTO : offerDTOs) {
        for (FormalAuditEntity formalAuditEntity : formalAuditEntities) {
          // Check if the formal audit entity is connected to the current submittent.
          if (formalAuditEntity.getSubmittent().getId().equals(offerDTO.getSubmittent().getId())) {
            // Get the values existsExclusionReasons and formalExaminationFulfilled from the formal
            // audit entity and set them to the submittent.
            offerDTO.getSubmittent()
              .setExistsExclusionReasons(formalAuditEntity.getExistsExclusionReasons());
            offerDTO.getSubmittent()
              .setFormalExaminationFulfilled(formalAuditEntity.getFormalExaminationFulfilled());
            break;
          }
        }
      }
    } else {
      offerDTOs = offerService.getSubmissionOffersWithCriteria(submissionId);
    }
    existsExclusionReasons =
      validateOfferCriteriaValues(existsExclusionReasons, results, offerDTOs);
    // Check if the Eignungsprüfung document has been created.
    if (!subDocumentService.documentExists(submissionId, Template.EIGNUNGSPRUFUNG)) {
      // Check if there has already been an exclusion.
      if (!existsExclusionReasons) {
        existsExclusionReasons = true;
      }
      results.add(NO_EXAMINATION_DOCUMENT);
    }
    // Change the status of submission if there has been no exclusion.
    if (!existsExclusionReasons) {
      proceedWithCloseExamination(submissionId, submissionEntity, offerDTOs);
    }
    return results;
  }

  @Override
  public List<ProofProvidedMapDTO> mapProofProvidedValues(String submissionId) {
    LOGGER.log(Level.CONFIG,
      "Executing method mapProofProvidedValues, Parameters: submissionId: {0}",
      submissionId);

    // Get the submission.
    SubmissionDTO submissionDTO = getSubmissionById(submissionId);

    List<SubmittentDTO> submittentDTOs;

    // Get the submittent DTOs.
    if (submissionDTO.getProcess().equals(Process.SELECTIVE) && !compareCurrentVsSpecificStatus(
      TenderStatus.fromValue(submissionDTO.getStatus()),
      TenderStatus.SUITABILITY_AUDIT_COMPLETED_S)) {
      submittentDTOs = getApplicantsForFormalAudit(submissionId);
    } else {
      submittentDTOs = getSubmittentsBySubmission(submissionId);
    }

    // List where all the mapped proof provided values will be stored.
    List<ProofProvidedMapDTO> proofProvidedMapDTOs = new ArrayList<>();
    ProofProvidedMapDTO proofProvidedMapDTO;
    for (SubmittentDTO submittentDTO : submittentDTOs) {
      proofProvidedMapDTO = new ProofProvidedMapDTO();
      // Map the submittent proof provided value.
      proofProvidedMapDTO.setSubmittentId(submittentDTO.getId());
      proofProvidedMapDTO.setCompanyId(submittentDTO.getCompanyId().getId());
      proofProvidedMapDTO.setIsProofProvided(submittentDTO.getCompanyId().getIsProofProvided());
      proofProvidedMapDTOs.add(proofProvidedMapDTO);
      for (CompanyDTO jointVenture : submittentDTO.getJointVentures()) {
        proofProvidedMapDTO = new ProofProvidedMapDTO();
        // Map the joint venture proof provided value.
        proofProvidedMapDTO.setSubmittentId(submittentDTO.getId());
        proofProvidedMapDTO.setCompanyId(jointVenture.getId());
        proofProvidedMapDTO.setIsProofProvided(jointVenture.getIsProofProvided());
        proofProvidedMapDTOs.add(proofProvidedMapDTO);
      }
      for (CompanyDTO subcontractor : submittentDTO.getSubcontractors()) {
        proofProvidedMapDTO = new ProofProvidedMapDTO();
        // Map the subcontractor proof provided value.
        proofProvidedMapDTO.setSubmittentId(submittentDTO.getId());
        proofProvidedMapDTO.setCompanyId(subcontractor.getId());
        proofProvidedMapDTO.setIsProofProvided(subcontractor.getIsProofProvided());
        proofProvidedMapDTOs.add(proofProvidedMapDTO);
      }
    }
    return proofProvidedMapDTOs;
  }

  private boolean validateOfferCriteriaValues(boolean existsExclusionReasons, List<String> results,
    List<OfferDTO> offerDTOs) {
    for (OfferDTO offerDTO : offerDTOs) {
      // Check if user has not set values in Eignungsprüfung durchführen or if
      // criterion/sub-criterion grade values are out of specified limits.
      // Check criteria.
      if (offerDTO.getIsEmptyOffer() == null || !offerDTO.getIsEmptyOffer()) {
        for (OfferCriterionDTO offerCriterionDTO : offerDTO.getOfferCriteria()) {
          if ((offerCriterionDTO.getCriterion().getCriterionType().equals(
            LookupValues.MUST_CRITERION_TYPE) && offerCriterionDTO.getIsFulfilled() == null)
            || offerDTO.getqExStatus() == null || offerDTO.getqExExaminationIsFulfilled() == null
            || offerDTO.getSubmittent().getExistsExclusionReasons() == null
            || (offerCriterionDTO.getCriterion().getSubcriterion().isEmpty()
            && offerCriterionDTO.getCriterion().getCriterionType()
            .equals(LookupValues.EVALUATED_CRITERION_TYPE)
            && offerCriterionDTO.getGrade() == null)) {
            existsExclusionReasons = true;
            // If exclusion is not because of invalid grades, then it is caused by empty values.
            results.add(EMPTY_VALUES);
            break;
          }
          // Check sub-criteria.
          for (OfferSubcriterionDTO offerSubriterionDTO : offerDTO.getOfferSubcriteria()) {
            if ((offerSubriterionDTO.getSubcriterion().getCriterion()
              .equals(offerCriterionDTO.getCriterion().getId())
              && offerCriterionDTO.getCriterion().getCriterionType()
              .equals(LookupValues.EVALUATED_CRITERION_TYPE))
              && offerSubriterionDTO.getGrade() == null) {
              existsExclusionReasons = true;
              // If exclusion is not because of invalid grades, then it is caused by empty
              // grades.
              results.add(EMPTY_VALUES);
              break;
            }
          }
        }
      }
      if (results.contains(EMPTY_VALUES)) {
        break;
      }
    }
    return existsExclusionReasons;
  }

  /**
   * Proceed with close examination.
   *
   * @param submissionId     the submission id
   * @param submissionEntity the submission entity
   * @param offerDTOs        the offer DT os
   */
  private void proceedWithCloseExamination(String submissionId, SubmissionEntity submissionEntity,
    List<OfferDTO> offerDTOs) {

    LOGGER.log(Level.CONFIG,
      "Executing method proceedWithCloseExamination, Parameters: submissionId: {0}, "
        + "submissionEntity: {1}, offerDTOs: {2}",
      new Object[]{submissionId, submissionEntity, offerDTOs});

    SelectiveLevel level = null;
    if ((submissionEntity.getProcess().equals(Process.INVITATION)
      || submissionEntity.getProcess().equals(Process.OPEN))
      && submissionEntity.getStatus().compareTo(
      TenderStatus.FORMAL_EXAMINATION_AND_QUALIFICATION_STARTED.getValue()) >= 0) {
      updateSubmissionStatus(submissionEntity,
        TenderStatus.SUITABILITY_AUDIT_COMPLETED_AND_AWARD_EVALUATION_STARTED.getValue(),
        AuditMessages.SUITABILITY_AUDIT_CLOSE.name(), null, LookupValues.EXTERNAL_LOG,
        getUserId());
      updateIsExcludedFromProcess(OfferDTOWithCriteriaMapper.INSTANCE.toOffer(offerDTOs));
    }
    if (submissionEntity.getProcess().equals(Process.SELECTIVE) && submissionEntity.getStatus()
      .compareTo(TenderStatus.FORMAL_EXAMINATION_SUITABILITY_AUDIT_STARTED.getValue()) == 0) {
      boolean higherStatusSet = false;
      boolean awardNoticesDocumentExists = awardNoticesFirstLevelDocumentExists(submissionId);
      for (OfferDTO offerDTO : offerService.getOffersBySubmission(submissionId)) {
        if (offerDTO.getOfferDate() != null && awardNoticesDocumentExists) {
          // If at least one offer has been saved and the award notices first level document
          // exists,
          // set status to OFFER_OPENING_STARTED.
          higherStatusSet = true;
          updateSubmissionStatus(submissionEntity, TenderStatus.OFFER_OPENING_STARTED.getValue(),
            AuditMessages.SUITABILITY_AUDIT_CLOSE.name(), null, LookupValues.EXTERNAL_LOG,
            getUserId());
          break;
        }
      }
      if (!higherStatusSet) {
        if (awardNoticesDocumentExists) {
          // If the award notices first level document has been created and no higher status has
          // been set, set the status to AWARD_NOTICES_1_LEVEL_CREATED.
          updateSubmissionStatus(submissionEntity,
            TenderStatus.AWARD_NOTICES_1_LEVEL_CREATED.getValue(),
            AuditMessages.SUITABILITY_AUDIT_CLOSE.name(), null, LookupValues.EXTERNAL_LOG,
            getUserId());
        } else {
          // In any other case, set status to SUITABILITY_AUDIT_COMPLETED_S.
          updateSubmissionStatus(submissionEntity,
            TenderStatus.SUITABILITY_AUDIT_COMPLETED_S.getValue(),
            AuditMessages.SUITABILITY_AUDIT_CLOSE.name(), null, LookupValues.EXTERNAL_LOG,
            getUserId());
        }
      }
      updateIsExcludedFromProcess(OfferDTOWithCriteriaMapper.INSTANCE.toOffer(offerDTOs));
    }
    Date deadline = null;
    if (submissionEntity.getProcess().equals(Process.SELECTIVE)) {
      // In case of a selective process the deadline is the 1st deadline.
      level = SelectiveLevel.FIRST_LEVEL;
      deadline = submissionEntity.getFirstDeadline();
    } else {
      // In any other case the deadline is the 2nd deadline.
      level = SelectiveLevel.ZERO_LEVEL;
      deadline = submissionEntity.getSecondDeadline();
    }
    // Automatically update Formelle Prufung table (Solve wiederaufnehmen issue for unsaved form).
    List<SubmittentDTO> submittentDTOs =
      calculateSubmittentValues(submissionEntity.getSubmittents(), deadline,
        submissionEntity.getProcess(), level);
    for (SubmittentDTO submittentDTO : submittentDTOs) {
      SubmittentEntity submittentEntity = SubmittentDTOMapper.INSTANCE.toSubmittent(submittentDTO);
      submittentEntity.setFormalExaminationFulfilled(submittentDTO.getFormalExaminationFulfilled());
      em.merge(submittentEntity);
      setSubmittentProofProvided(submittentEntity, level, submittentDTO);
    }
    // Update the award evaluation page if applicable.
    if (submissionEntity.getProcess() != Process.SELECTIVE) {
      updateAwardEvaluationPage(submissionId);
    }
  }

  /**
   * Generate proof documents.
   *
   * @param id                     the id
   * @param existsExclusionReasons the exists exclusion reasons
   * @param results                the results
   * @param submissionEntity       the submission entity
   * @return true, if successful
   */
  private boolean generateProofDocuments(String id, boolean existsExclusionReasons,
    List<String> results, SubmissionEntity submissionEntity) {
    QCompanyEntity qCompanyEntitySubcontractors = new QCompanyEntity("subcontractors");
    QCompanyEntity qCompanyEntityjointVentures = new QCompanyEntity("jointVentures");

    // Retrieve unique submittent. For example, when submittent A (ARGE B, Sub C) exists twice,
    // generate only one Nachweisbrief for this submittent.
    List<SubmittentEntity> submittents = new JPAQueryFactory(em).selectFrom(qSubmittentEntity)
      .leftJoin(qSubmittentEntity.jointVentures, qCompanyEntityjointVentures)
      .leftJoin(qSubmittentEntity.subcontractors, qCompanyEntitySubcontractors)
      .where((qSubmittentEntity.submissionId.id.eq(id))
        .and(qSubmittentEntity.offer.excludedFirstLevel.isFalse()
          .or(qSubmittentEntity.offer.excludedFirstLevel.isNull())))
      .groupBy(qSubmittentEntity.companyId, qCompanyEntitySubcontractors,
        qCompanyEntityjointVentures).fetch();
    Set<SubmittentEntity> submittentSet = new HashSet<>(submittents);
    // Retrieve Nachweisbrief templateId.
    MasterListValueHistoryEntity template = new JPAQueryFactory(em)
      .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
      .where(qMasterListValueHistoryEntity.shortCode.eq(Template.NACHWEISBRIEF_PT)
        .and(qMasterListValueHistoryEntity.tenant.id
          .eq(usersService.getUserById(getUser().getId()).getTenant().getId()))
        .and(qMasterListValueHistoryEntity.toDate.isNull()))
      .fetchOne();

    // if documentShoulBeCreated has not been set to true yet, iterate through subcontractors.
    // subcontractors have different template.
    MasterListValueHistoryEntity templateSub = new JPAQueryFactory(em)
      .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
      .where(qMasterListValueHistoryEntity.shortCode.eq(Template.NACHWEISBRIEF_SUB)
        .and(qMasterListValueHistoryEntity.tenant.id
          .eq(usersService.getUserById(getUser().getId()).getTenant().getId()))
        .and(qMasterListValueHistoryEntity.toDate.isNull()))
      .fetchOne();
    for (SubmittentEntity submittent : submittentSet) {
      if (submittent.getOffer().getIsEmptyOffer() == null
        || !submittent.getOffer().getIsEmptyOffer()) {
        if (submissionEntity.getProcess().equals(Process.SELECTIVE)
          && !compareCurrentVsSpecificStatus(TenderStatus.fromValue(submissionEntity.getStatus()),
          TenderStatus.SUITABILITY_AUDIT_COMPLETED_S)) {
          if ((submittent.getIsApplicant() != null && submittent.getIsApplicant())
            && isProofDocumentRequired(submittent, submissionEntity, template, templateSub)) {
            existsExclusionReasons = true;
            results.add(ValidationMessages.PROOF_DOCUMENT_SHOULD_BE_CREATED);
            break;
          }
        } else {
          if (isProofDocumentRequired(submittent, submissionEntity, template, templateSub)) {
            existsExclusionReasons = true;
            results.add(ValidationMessages.PROOF_DOCUMENT_SHOULD_BE_CREATED);
            break;
          }
        }
      }
    }
    return existsExclusionReasons;
  }

  /**
   * Update is excluded from process.
   *
   * @param offerEntities the offer entities
   */
  private void updateIsExcludedFromProcess(List<OfferEntity> offerEntities) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateIsExcludedFromProcess, Parameters: offerCriterionEntities: {0}",
      offerEntities);

    for (OfferEntity offerEntity : offerEntities) {
      if ((offerEntity.getSubmittent().getExistsExclusionReasons() != null
        && offerEntity.getSubmittent().getExistsExclusionReasons()
        && (offerEntity.getSubmittent().getSubmissionId().getProcess()
        .equals(Process.NEGOTIATED_PROCEDURE)
        || offerEntity.getSubmittent().getSubmissionId().getProcess()
        .equals(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION)))
        || (offerEntity.getqExExaminationIsFulfilled() != null
        && !offerEntity.getqExExaminationIsFulfilled()
        && (offerEntity.getSubmittent().getSubmissionId().getProcess()
        .equals(Process.INVITATION)
        || offerEntity.getSubmittent().getSubmissionId().getProcess()
        .equals(Process.OPEN)
        || offerEntity.getSubmittent().getSubmissionId().getProcess()
        .equals(Process.SELECTIVE)))) {
        if (offerEntity.getSubmittent().getSubmissionId().getProcess()
          .equals(Process.SELECTIVE)) {
          offerEntity.setExcludedFirstLevel(Boolean.TRUE);
        } else {
          offerEntity.setIsExcludedFromProcess(Boolean.TRUE);
        }
        em.merge(offerEntity);
      }
      if (offerEntity.getExcludedFirstLevel() == null && offerEntity
        .getSubmittent().getSubmissionId().getProcess().equals(Process.SELECTIVE)) {
        // If the process type is selective and the excludedFirstLevel value is still null, set it
        // to false.
        offerEntity.setExcludedFirstLevel(Boolean.FALSE);
        em.merge(offerEntity);
      }
      // If offer is excluded, set award offer criteria score and grade to null.
      for (OfferCriterionEntity offerCriterionEntity : offerEntity.getOfferCriteria()) {
        if (offerEntity.getIsExcludedFromProcess() != null && offerEntity.getIsExcludedFromProcess()
          && offerCriterionEntity.getCriterion().getCriterionType()
          .equals(LookupValues.AWARD_CRITERION_TYPE)) {
          offerCriterionEntity.setGrade(null);
          offerCriterionEntity.setScore(null);
          // If offer is excluded, set award offer sub-criteria score and grade to null.
          for (OfferSubcriterionEntity offerSubcriterionEntity : offerEntity
            .getOfferSubcriteria()) {
            if (offerSubcriterionEntity.getSubcriterion().getCriterion().getId()
              .equals(offerCriterionEntity.getCriterion().getId())) {
              offerSubcriterionEntity.setGrade(null);
              offerSubcriterionEntity.setScore(null);
            }
          }
        }
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SubmissionService#reopenOffer(java.lang.String,
   * java.lang.String)
   */
  @Override
  public void reopenOffer(String reopenReason, String id) {

    LOGGER.log(Level.CONFIG, "Executing method reopenOffer, Parameters: reopenReason: {0}, "
        + "id: {1}",
      new Object[]{reopenReason, id});

    String currentStatus = getCurrentStatusOfSubmission(id);
    if (Integer.parseInt(
      currentStatus) >= (Integer.parseInt(TenderStatus.OFFER_OPENING_CLOSED.getValue()))) {
      updateSubmissionStatus(id, TenderStatus.OFFER_OPENING_STARTED.getValue(),
        AuditMessages.OFFER_REOPEN.name(), reopenReason, LookupValues.EXTERNAL_LOG);
    }
  }

  @Override
  public void reopenFormalAudit(String reopenReason, String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method reopenFormalAudit, Parameters: reopenReason: {0}, "
        + "submissionId: {1}",
      new Object[]{reopenReason, submissionId});

    updateSubmissionStatus(submissionId,
      TenderStatus.FORMAL_EXAMINATION_STARTED.getValue(),
      AuditMessages.FORMAL_AUDIT_REOPEN.name(), reopenReason, LookupValues.EXTERNAL_LOG);

    /*
     * Set isExcludedFromProcess value of submission offers back to false (UC 175).
     */
    setDefaultIsExcludedFromProcess(submissionId);
    // Get submission process type.
    Process process = new JPAQueryFactory(em).select(qSubmissionEntity.process)
      .from(qSubmissionEntity).where(qSubmissionEntity.id.eq(submissionId)).fetchFirst();
    // Update the award evaluation page if applicable.
    if (process == Process.SELECTIVE) {
      updateAwardEvaluationPage(submissionId);
    }
  }

  @Override
  public void reopenExamination(String reopenReason, String id) {

    LOGGER.log(Level.CONFIG,
      "Executing method reopenExamination, Parameters: reopenReason: {0}, "
        + "id: {1}",
      new Object[]{reopenReason, id});

    Process process = new JPAQueryFactory(em).select(qSubmissionEntity.process)
      .from(qSubmissionEntity).where(qSubmissionEntity.id.eq(id)).fetchFirst();
    if (process != Process.SELECTIVE) {
      // Change the submission status and create log.
      updateSubmissionStatus(id,
        TenderStatus.FORMAL_EXAMINATION_AND_QUALIFICATION_STARTED.getValue(),
        AuditMessages.FORMAL_EXAMINATION_REOPEN.name(), reopenReason, LookupValues.EXTERNAL_LOG);
    } else {
      // Change the submission status and create log.
      updateSubmissionStatus(id,
        TenderStatus.FORMAL_EXAMINATION_SUITABILITY_AUDIT_STARTED.getValue(),
        AuditMessages.FORMAL_EXAMINATION_REOPEN.name(), reopenReason, LookupValues.EXTERNAL_LOG);
    }
    // Set isExcludedFromProcess value of submission offers back to false (UC 041).
    setDefaultIsExcludedFromProcess(id);
    if (process != Process.SELECTIVE) {
      // Update the award evaluation page if applicable.
      updateAwardEvaluationPage(id);
    }
  }

  /**
   * Change the status of submission to the one before. Check if offers of this submission have an
   * award and took it back Send the reopen reason to save it at audit log
   *
   * @param submissionId the submission id
   * @param reopenReason the reopen reason
   */
  @Override
  public void reopenManualAward(String submissionId, String reopenReason) {

    LOGGER.log(Level.CONFIG,
      "Executing method reopenManualAward, Parameters: submissionId: {0}, "
        + "reopenReason: {1}",
      new Object[]{submissionId, reopenReason});

    SubmissionEntity submissionEntity = em.find(SubmissionEntity.class, submissionId);

    updateSubmissionStatus(submissionId,
      getManualAwardReopenStatus(submissionEntity.getProcess(),
        submissionEntity.getIsServiceTender()),
      AuditMessages.MANUAL_AWARD_REOPEN.name(), reopenReason, LookupValues.EXTERNAL_LOG);

    // remove the award
    removeAward(submissionId);

    if (submissionEntity.getAboveThreshold() != null && submissionEntity.getAboveThreshold()) {
      submissionEntity.setAboveThreshold(false);
      if (((submissionEntity.getProcess() == Process.NEGOTIATED_PROCEDURE)
        || (submissionEntity.getProcess() == Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION))
        && submissionEntity.getIsGekoEntryByManualAward() != null
        && submissionEntity.getIsGekoEntryByManualAward()) {
        submissionEntity.setIsGekoEntryByManualAward(false);
        submissionEntity.setIsGekoEntry(false);
      }
    }
  }

  /**
   * Get the reopen status
   *
   * @param process the process
   * @param isServiceTender the isServiceTender
   * @return the reopen status
   */
  private String getManualAwardReopenStatus(Process process, Boolean isServiceTender) {
    String status;
    // if process is freihandig/freihandig MK change status to 150
    if ((process == Process.NEGOTIATED_PROCEDURE)
      || (process == Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION)) {
      status = TenderStatus.FORMAL_AUDIT_COMPLETED.getValue();
    }
    // if process is selektiv DL WW change status to 190
    else if (process == Process.SELECTIVE
      && Boolean.TRUE.equals(isServiceTender)) {
      status = TenderStatus.FORMAL_AUDIT_COMPLETED_AND_AWARD_EVALUATION_STARTED.getValue();
    }
    // else change status to 180
    else {
      status = TenderStatus.SUITABILITY_AUDIT_COMPLETED_AND_AWARD_EVALUATION_STARTED.getValue();
    }
    return status;
  }

  @Override
  public void removeAward(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method removeAward, Parameters: submissionId: {0}",
      submissionId);
    // find offers and update award
    JPAQuery<SubmissionEntity> query = new JPAQuery<>(em);
    List<OfferEntity> offerEntityList = query.select(qOfferEntity).from(qOfferEntity)
      .where(qOfferEntity.submittent.submissionId.id.eq(submissionId)).fetch();
    for (OfferEntity offerEntity : offerEntityList) {
      if (offerEntity != null && offerEntity.getIsAwarded() != null && offerEntity.getIsAwarded()) {
        offerEntity.setIsAwarded(false);
        em.persist(offerEntity);
      }
    }
  }

  /**
   * This function implements the reopening of the award evaluation.
   *
   * @param reopenReason the reopen reason
   * @param submissionId the submission id
   */
  @Override
  public void reopenAwardEvaluation(String reopenReason, String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method reopenAwardEvaluation, Parameters: submissionId: {0}, "
        + "reopenReason: {1}",
      new Object[]{submissionId, reopenReason});

    List<OfferEntity> offerEntities = new JPAQueryFactory(em).select(qOfferEntity)
      .from(qOfferEntity).where(qOfferEntity.submittent.submissionId.id.eq(submissionId)).fetch();
    /* Remove award from offer if it has been given */
    for (OfferEntity offerEntity : offerEntities) {
      if (offerEntity != null && offerEntity.getIsAwarded() != null && offerEntity.getIsAwarded()) {
        offerEntity.setIsAwarded(false);
      }
    }
    /* Setting status of submission according to its process */
    SubmissionEntity submission = em.find(SubmissionEntity.class, submissionId);
    if (submission.getProcess().equals(Process.OPEN)
      || submission.getProcess().equals(Process.INVITATION)) {
      updateSubmissionStatus(submissionId,
        TenderStatus.SUITABILITY_AUDIT_COMPLETED_AND_AWARD_EVALUATION_STARTED.getValue(),
        AuditMessages.AWARD_EVALUATION_REOPEN.name(), reopenReason, LookupValues.EXTERNAL_LOG);
    } else if (submission.getProcess().equals(Process.SELECTIVE)) {
      updateSubmissionStatus(submissionId,
        TenderStatus.FORMAL_AUDIT_COMPLETED_AND_AWARD_EVALUATION_STARTED.getValue(),
        AuditMessages.AWARD_EVALUATION_REOPEN.name(), reopenReason, LookupValues.EXTERNAL_LOG);
    }
  }

  /**
   * Private method to reset the value isExlcudedFromProcess which exists in OfferEntity.
   *
   * @param submissionId the new default is excluded from process
   */
  private void setDefaultIsExcludedFromProcess(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method setDefaultIsExcludedFromProcess, Parameters: submissionId: {0}",
      submissionId);

    List<OfferEntity> offerEntities = new JPAQueryFactory(em).select(qOfferEntity)
      .from(qOfferEntity).where(qOfferEntity.submittent.submissionId.id.eq(submissionId)).fetch();
    Process process = new JPAQueryFactory(em).select(qSubmissionEntity.process)
      .from(qSubmissionEntity).where(qSubmissionEntity.id.eq(submissionId)).fetchFirst();
    if (process == Process.SELECTIVE && !compareCurrentVsSpecificStatus(
      TenderStatus.fromValue(getCurrentStatusOfSubmission(submissionId)),
      TenderStatus.SUITABILITY_AUDIT_COMPLETED_S)) {
      for (OfferEntity o : offerEntities) {
        o.setExcludedFirstLevel(Boolean.FALSE);
      }
    } else {
      for (OfferEntity o : offerEntities) {
        o.setIsExcludedFromProcess(Boolean.FALSE);
      }
    }
  }

  /**
   * Update the submission status to commission procurement proposal started.
   *
   * @param submissionId the submission id
   */
  @Override
  public void startCommissionProcurementProposal(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method startCommissionProcurementProposal, Parameters: "
        + "submissionId: {0}",
      submissionId);

    SubmissionEntity submissionEntity = em.find(SubmissionEntity.class, submissionId);
    updateSubmissionStatus(submissionEntity,
      TenderStatus.COMMISSION_PROCUREMENT_PROPOSAL_STARTED.getValue(), null, null,
      LookupValues.INTERNAL_LOG, getUserId());
  }

  /**
   * Check if statuses award evaluation closed or manual award completed have been set before.
   *
   * @param submissionId the submission id
   * @return true/false
   */
  @Override
  public boolean haveAwardStatusesBeenClosed(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method haveAwardStatusesBeenClosed, Parameters: submissionId: {0}",
      submissionId);

    List<TenderStatusHistoryEntity> tenderStatuses =
      new JPAQueryFactory(em).select(qTenderStatusHistoryEntity).from(qTenderStatusHistoryEntity)
        .where(qTenderStatusHistoryEntity.tenderId.id.eq(submissionId)
          .and(qTenderStatusHistoryEntity.statusId
            .eq(TenderStatus.AWARD_EVALUATION_CLOSED.getValue())
            .or(qTenderStatusHistoryEntity.statusId
              .eq(TenderStatus.MANUAL_AWARD_COMPLETED.getValue()))))
        .fetch();
    return !tenderStatuses.isEmpty();
  }

  /**
   * Check if status offer opening closed has been set before.
   *
   * @param submissionId the submission id
   * @return true/false
   */
  @Override
  public boolean hasOfferOpeningBeenClosedBefore(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method hasOfferOpeningBeenClosedBefore, Parameters: submissionId: {0}",
      submissionId);

    boolean offerOpeningClosedBefore = false;
    if (submissionId != null) {
      List<TenderStatusHistoryDTO> allRecentStatuses =
        tenderStatusHistoryService.getSubmissionStatuses(submissionId);
      for (TenderStatusHistoryDTO status : allRecentStatuses) {
        if (status.getStatusId().equals(TenderStatus.OFFER_OPENING_CLOSED.getValue())) {
          offerOpeningClosedBefore = true;
          break;
        }
      }
    }
    return offerOpeningClosedBefore;
  }

  @Override
  public boolean isOfferOpeningFirstTimeClosed(String submissionId) {
    List<TenderStatusHistoryDTO> allRecentStatuses =
      tenderStatusHistoryService.getSubmissionStatuses(submissionId);
    return allRecentStatuses.stream()
      .filter(tenderStatusHistoryDTO -> tenderStatusHistoryDTO.getStatusId()
        .equals(TenderStatus.OFFER_OPENING_CLOSED.getValue())).count() == 1;
  }

  /**
   * Check if commission procurement proposal document exists.
   *
   * @param submissionId the submission id
   * @return true/false
   */
  @Override
  public boolean proposalDocumentExists(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method proposalDocumentExists, Parameters: submissionId: {0}",
      submissionId);

    Map<String, String> map = new HashMap<>();
    String tenantId = new JPAQueryFactory(em).select(qSubmissionEntity.project.tenant.id)
      .from(qSubmissionEntity).where(qSubmissionEntity.id.eq(submissionId)).fetchOne();
    MasterListValueHistoryEntity template = new JPAQueryFactory(em)
      .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
      .where(qMasterListValueHistoryEntity.shortCode.eq(Template.BEKO_ANTRAG)
        .and(qMasterListValueHistoryEntity.tenant.id.eq(tenantId))
        .and(qMasterListValueHistoryEntity.toDate.isNull()))
      .fetchOne();
    map.put(DocumentAttributes.TEMPLATE_ID.name(), template.getMasterListValueId().getId());
    List<NodeDTO> nodeDTOs = documentService.getNodeByAttributes(submissionId, map);
    return !nodeDTOs.isEmpty();
  }

  /**
   * Function to close the commission procurement proposal.
   *
   * @param submissionId the submission id
   */
  @Override
  public void closeCommissionProcurementProposal(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method closeCommissionProcurementProposal, Parameters: submissionId: {0}",
      submissionId);
    // Initialize submission values for the commission procurement decision.
    SubmissionDTO submissionDTO = getSubmissionById(submissionId);
    submissionDTO.setCommissionProcurementDecisionRecommendation(
      "Die Städtische Beschaffungskommission empfiehlt der "
        + submissionDTO.getProject().getDepartment().getDirectorate().getName()
        + " einstimmig, den Auftrag antragsgemäss zu vergeben.");
    SubmissionEntity submissionEntity = SubmissionMapper.INSTANCE.toSubmission(submissionDTO);
    em.merge(submissionEntity);
    // Updating the status.
    updateSubmissionStatus(submissionId,
      TenderStatus.COMMISSION_PROCUREMENT_PROPOSAL_CLOSED.getValue(),
      AuditMessages.COMMISSION_PROCUREMENT_PROPOSAL_CLOSE.name(), null,
      LookupValues.EXTERNAL_LOG);
  }

  /**
   * Check if the commission procurement proposal has been closed before.
   *
   * @param submissionId the submission id
   * @return true/false
   */
  @Override
  public boolean hasCommissionProcurementProposalBeenClosed(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method hasCommissionProcurementProposalBeenClosed, Parameters: "
        + "submissionId: {0}",
      submissionId);
    boolean procurementProposalClosed = false;
    List<TenderStatusHistoryDTO> allRecentStatuses =
      tenderStatusHistoryService.getSubmissionStatuses(submissionId);
    for (TenderStatusHistoryDTO status : allRecentStatuses) {
      if (status.getStatusId()
        .equals(TenderStatus.COMMISSION_PROCUREMENT_PROPOSAL_CLOSED.getValue())) {
        procurementProposalClosed = true;
        break;
      }
    }
    return procurementProposalClosed;
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SubmissionService#
   * reopenCommissionProcurementProposal(java.lang.String, java.lang.String)
   */
  @Override
  public void reopenCommissionProcurementProposal(String reopenReason, String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method reopenCommissionProcurementProposal, Parameters: "
        + "reopenReason: {0}, submissionId: {1}",
      new Object[]{reopenReason, submissionId});
    // Delete values of the commission procurement decision.
    SubmissionDTO submissionDTO = getSubmissionById(submissionId);
    submissionDTO.setCommissionProcurementDecisionRecommendation(null);
    SubmissionEntity submissionEntity = SubmissionMapper.INSTANCE.toSubmission(submissionDTO);
    // Set the value for the "Vorbehalt" (reservation) field.
    submissionEntity
      .setCommissionProcurementProposalReservation(setReservationValue(submissionEntity));
    em.merge(submissionEntity);
    // Updating the status.
    updateSubmissionStatus(submissionId,
      TenderStatus.COMMISSION_PROCUREMENT_PROPOSAL_STARTED.getValue(),
      AuditMessages.COMMISSION_PROCUREMENT_PROPOSAL_REOPEN.name(), reopenReason,
      LookupValues.EXTERNAL_LOG);
  }

  /**
   * Sets the commission procurement proposal reservation value.
   *
   * @param submissionEntity the submission entity
   * @return the reservation value
   */
  private String setReservationValue(SubmissionEntity submissionEntity) {

    LOGGER.log(Level.CONFIG,
      "Executing method setReservationValue, Parameters: submissionEntity: {0}",
      submissionEntity);

    String reservation = submissionEntity.getCommissionProcurementProposalReservation();
    // Check if the reservation value is one of the predefined values.
    if (StringUtils.isNotBlank(reservation) && (reservation
      .equals(CommissionProcurementProposalReservation.RESERVATION_CONSTRUCTION.getValue())
      || reservation.equals(CommissionProcurementProposalReservation.RESERVATION_LOAN.getValue())
      || reservation.equals(
      CommissionProcurementProposalReservation.RESERVATION_CONSTRUCTION_AND_LOAN.getValue())
      || reservation
      .equals(CommissionProcurementProposalReservation.RESERVATION_NONE.getValue()))) {
      // Set the new reservation value.
      if (submissionEntity.getConstructionPermit().equals(ConstructionPermit.PENDING)
        && !submissionEntity.getLoanApproval().equals(LoanApproval.PENDING)) {
        reservation = CommissionProcurementProposalReservation.RESERVATION_CONSTRUCTION.getValue();
      } else if (!submissionEntity.getConstructionPermit().equals(ConstructionPermit.PENDING)
        && submissionEntity.getLoanApproval().equals(LoanApproval.PENDING)) {
        reservation = CommissionProcurementProposalReservation.RESERVATION_LOAN.getValue();
      } else if (submissionEntity.getConstructionPermit().equals(ConstructionPermit.PENDING)
        && submissionEntity.getLoanApproval().equals(LoanApproval.PENDING)) {
        reservation =
          CommissionProcurementProposalReservation.RESERVATION_CONSTRUCTION_AND_LOAN.getValue();
      } else {
        reservation = CommissionProcurementProposalReservation.RESERVATION_NONE.getValue();
      }
    }
    return reservation;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SubmissionService#decisionDocumentExists(java.lang.
   * String)
   */
  @Override
  public boolean decisionDocumentExists(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method decisionDocumentExists, Parameters: submissionId: {0}",
      submissionId);

    Map<String, String> map = new HashMap<>();
    String tenantId = new JPAQueryFactory(em).select(qSubmissionEntity.project.tenant.id)
      .from(qSubmissionEntity).where(qSubmissionEntity.id.eq(submissionId)).fetchOne();
    MasterListValueHistoryEntity template = new JPAQueryFactory(em)
      .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
      .where(qMasterListValueHistoryEntity.shortCode.eq(Template.BEKO_BESCHLUSS)
        .and(qMasterListValueHistoryEntity.tenant.id.eq(tenantId))
        .and(qMasterListValueHistoryEntity.toDate.isNull()))
      .fetchOne();
    map.put(DocumentAttributes.TEMPLATE_ID.name(), template.getMasterListValueId().getId());
    List<NodeDTO> nodeDTOs = documentService.getNodeByAttributes(submissionId, map);
    return !nodeDTOs.isEmpty();
  }

  @Override
  public void closeCommissionProcurementDecision(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method closeCommissionProcurementDecision, Parameters: "
        + "submissionId: {0}",
      submissionId);
    // Updating the status.
    updateSubmissionStatus(submissionId,
      TenderStatus.COMMISSION_PROCUREMENT_DECISION_CLOSED.getValue(),
      AuditMessages.COMMISSION_PROCUREMENT_DECISION_CLOSE.name(), null,
      LookupValues.EXTERNAL_LOG);
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SubmissionService#
   * hasCommissionProcurementDecisionBeenClosed(java.lang.String)
   */
  @Override
  public boolean hasCommissionProcurementDecisionBeenClosed(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method hasCommissionProcurementDecisionBeenClosed, "
        + "Parameters: submissionId: {0}",
      submissionId);

    boolean procurementDecisionClosed = false;
    List<TenderStatusHistoryDTO> allRecentStatuses =
      tenderStatusHistoryService.getSubmissionStatuses(submissionId);
    for (TenderStatusHistoryDTO status : allRecentStatuses) {
      if (status.getStatusId()
        .equals(TenderStatus.COMMISSION_PROCUREMENT_DECISION_CLOSED.getValue())) {
        procurementDecisionClosed = true;
        break;
      }
    }
    return procurementDecisionClosed;
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SubmissionService#
   * reopenCommissionProcurementDecision(java.lang.String, java.lang.String)
   */
  @Override
  public void reopenCommissionProcurementDecision(String reopenReason, String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method reopenCommissionProcurementDecision, "
        + "Parameters: reopenReason: {0}, submissionId: {1}",
      new Object[]{reopenReason, submissionId});
    // Updating the status.
    updateSubmissionStatus(submissionId,
      TenderStatus.COMMISSION_PROCUREMENT_DECISION_STARTED.getValue(),
      AuditMessages.COMMISSION_PROCUREMENT_DECISION_REOPEN.name(), reopenReason,
      LookupValues.EXTERNAL_LOG);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SubmissionService#updateAwardEvaluationPage(java.
   * lang.String)
   */
  @Override
  public void updateAwardEvaluationPage(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateAwardEvaluationPage, "
        + "Parameters: submissionId: {0}",
      submissionId);

    SubmissionDTO submissionDTO = getSubmissionById(submissionId);
    if (!(submissionDTO.getProcess().equals(Process.NEGOTIATED_PROCEDURE)
      || submissionDTO.getProcess().equals(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION)
      || ((submissionDTO.getProcess().equals(Process.OPEN)
      || submissionDTO.getProcess().equals(Process.SELECTIVE))
      && (submissionDTO.getIsServiceTender() != null
      && submissionDTO.getIsServiceTender())))) {
      // Fill in the values for the award DTO.
      AwardDTO awardDTO = new AwardDTO();
      awardDTO.setAddedAwardRecipients(submissionDTO.getAddedAwardRecipients());
      awardDTO.setAwardMaxGrade(submissionDTO.getAwardMaxGrade());
      awardDTO.setAwardMinGrade(submissionDTO.getAwardMinGrade());
      awardDTO.setEvaluationThrough(submissionDTO.getEvaluationThrough());
      awardDTO.setOperatingCostFormula(submissionDTO.getOperatingCostFormula());
      awardDTO.setPriceFormula(submissionDTO.getPriceFormula());
      awardDTO.setCustomOperatingCostFormula(submissionDTO.getCustomOperatingCostFormula());
      awardDTO.setCustomPriceFormula(submissionDTO.getCustomPriceFormula());
      awardDTO.setSubmissionId(submissionDTO.getId());
      List<CriterionDTO> awardCriteria = new ArrayList<>();
      for (CriterionDTO criterionDTO : criterionService
        .readCriteriaOfSubmission(submissionDTO.getId())) {
        // Select only award criteria for the award DTO.
        if (criterionDTO.getCriterionType().equals(LookupValues.AWARD_CRITERION_TYPE)
          || criterionDTO.getCriterionType()
          .equals(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE)
          || criterionDTO.getCriterionType().equals(LookupValues.PRICE_AWARD_CRITERION_TYPE)) {
          awardCriteria.add(criterionDTO);
        }
      }
      awardDTO.setCriterion(awardCriteria);
      criterionService.updateAwardCriterion(awardDTO);
    }
  }

  /**
   * This method checks if Nachweisbrief document should be generated before the close of the
   * examination procedure.
   *
   * @param submittent       the submittent
   * @param submissionEntity the submission entity
   * @param template         the template
   * @param templateSub      the template sub
   * @return true, if is proof document required
   */
  private boolean isProofDocumentRequired(SubmittentEntity submittent,
    SubmissionEntity submissionEntity, MasterListValueHistoryEntity template,
    MasterListValueHistoryEntity templateSub) {

    LOGGER.log(Level.CONFIG, "Executing method isProofDocumentRequired, "
        + "Parameters: submittent: {0}, "
        + "submissionEntity: {1}",
      new Object[]{submittent, submissionEntity});

    // Set deadline value according to submission process type.
    Date deadline;
    if (submissionEntity.getProcess().equals(Process.SELECTIVE)) {
      deadline = submissionEntity.getFirstDeadline();
    } else {
      deadline = submissionEntity.getSecondDeadline();
    }

    List<CompanyEntity> companies = new ArrayList<>();
    companies.add(submittent.getCompanyId());
    companies.addAll(submittent.getJointVentures());
    for (CompanyEntity jointVentures : companies) {
      if (setDocumentShouldBeCreated(jointVentures, submittent.getId(),
        template, submissionEntity.getId(), deadline)) {
        return true;
      }
    }
    for (CompanyEntity subcontractors : submittent.getSubcontractors()) {
      if (setDocumentShouldBeCreated(subcontractors, submittent.getId(),
        templateSub, submissionEntity.getId(), deadline)) {
        return true;
      }
    }
    return false;
  }

  /**
   * This method set a boolean value to true if the Nachweisbrief document should be created.
   *
   * @param companyEntity the company entity
   * @param submittentId  the submittent id
   * @param template      the template
   * @param submissionId  the submission id
   * @param deadline      the deadline
   * @return true, if successful
   */
  private boolean setDocumentShouldBeCreated(CompanyEntity companyEntity, String submittentId,
    MasterListValueHistoryEntity template, String submissionId, Date deadline) {

    HashMap<String, String> attributesMap = new HashMap<>();
    boolean documentShoulBeCreated = false;

    // Retrieve all company proofs.
    List<CompanyProofDTO> companyProofDTOs =
      CompanyProofDTOMapper.INSTANCE.toCompanyProofDTO(companyEntity.getCompanyProofs());

    // Find if Nachweisbrief document is required according to company's proof status.
    boolean requiredProofDocument =
      subDocumentService.createProofDocument(deadline, companyProofDTOs);

    attributesMap.put(DocumentAttributes.TEMPLATE_ID.name(),
      template.getMasterListValueId().getId());
    attributesMap.put(DocumentAttributes.TENDER_ID.name(), submittentId);
    attributesMap.put(DocumentAttributes.TENANT_ID.name(), template.getTenant().getId());
    attributesMap.put(DocumentAttributes.COMPANY_ID.name(), companyEntity.getId());
    if (requiredProofDocument
      && documentService.getNodeByAttributes(submissionId, attributesMap).isEmpty()) {
      documentShoulBeCreated = true;
    }
    return documentShoulBeCreated;
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SubmissionService#
   * generateProofDocNegotiatedProcedure(java.util.List)
   */
  @Override
  public void generateProofDocNegotiatedProcedure(List<SubmittentDTO> submittentDTOs) {

    LOGGER.log(Level.CONFIG,
      "Executing method generateProofDocNegotiatedProcedure, "
        + "Parameters: submittentDTOs:{0}",
      submittentDTOs);

    for (SubmittentDTO submittentDTO : submittentDTOs) {
      SubmittentEntity submittent = em.find(SubmittentEntity.class, submittentDTO.getId());
      submittent.setProofDocPending(submittentDTO.getProofDocPending());
      em.merge(submittent);
    }
  }

  /**
   * Update close formal audit status.
   *
   * @param submissionEntity the submission entity
   */
  private void updateCloseFormalAuditStatus(SubmissionEntity submissionEntity) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateCloseFormalAuditStatus, Parameters: submissionEntity:{0}",
      submissionEntity);

    SelectiveLevel level = null;
    if ((submissionEntity.getProcess().equals(Process.NEGOTIATED_PROCEDURE)
      || submissionEntity.getProcess().equals(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION))
      && submissionEntity.getStatus()
      .equals(TenderStatus.FORMAL_EXAMINATION_STARTED.getValue())) {
      level = SelectiveLevel.ZERO_LEVEL;
      updateSubmissionStatus(submissionEntity, TenderStatus.FORMAL_AUDIT_COMPLETED.getValue(),
        AuditMessages.FORMAL_AUDIT_CLOSE.name(), null, LookupValues.EXTERNAL_LOG, getUserId());
    } else if (submissionEntity.getProcess().equals(Process.SELECTIVE)) {
      level = SelectiveLevel.SECOND_LEVEL;
      updateSubmissionStatus(submissionEntity,
        TenderStatus.FORMAL_AUDIT_COMPLETED_AND_AWARD_EVALUATION_STARTED.getValue(),
        AuditMessages.FORMAL_AUDIT_CLOSE.name(), null, LookupValues.EXTERNAL_LOG, getUserId());
    }

    // Automatically update Formelle Prufung table (Solve wiederaufnehmen issue for unsaved form).
    Date deadline = null;
    if (submissionEntity.getProcess().equals(Process.SELECTIVE)) {
      deadline = submissionEntity.getFirstDeadline();
    } else {
      deadline = submissionEntity.getSecondDeadline();
    }
    List<SubmittentDTO> submittentDTOs =
      calculateSubmittentValues(submissionEntity.getSubmittents(),
        deadline, submissionEntity.getProcess(), level);
    for (SubmittentDTO submittentDTO : submittentDTOs) {
      SubmittentEntity submittentEntity = SubmittentDTOMapper.INSTANCE.toSubmittent(submittentDTO);
      submittentEntity.setFormalExaminationFulfilled(submittentDTO.getFormalExaminationFulfilled());
      em.merge(submittentEntity);
      setSubmittentProofProvided(submittentEntity, level, submittentDTO);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SubmissionService#manualAwardReopenCheck(java.lang.
   * String)
   */
  @Override
  public boolean manualAwardReopenCheck(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method manualAwardReopenCheck, Parameters: submissionId:{0}",
      submissionId);

    SubmissionDTO submissionDTO = getSubmissionById(submissionId);
    String groupName = getGroupName(getUser());
    // The PL has no right to reopen the manual award in a negotiated procedure (with or without
    // competition) if the submission is above threshold.
    return !(groupName.equals(Group.PL.getValue()) && submissionDTO.getAboveThreshold() != null
      && submissionDTO.getAboveThreshold()
      && (submissionDTO.getProcess().equals(Process.NEGOTIATED_PROCEDURE)
      || submissionDTO.getProcess().equals(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION)));
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SubmissionService#resetEvaluationData(java.lang.
   * String)
   */
  @Override
  public void resetEvaluationData(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method resetEvaluationData, Parameters: submissionId:{0}",
      submissionId);

    SubmissionEntity submissionEntity = em.find(SubmissionEntity.class, submissionId);
    if (submissionEntity != null) {
      deleteSubmissionExaminationData(submissionEntity);
      deleteSubmissionAwardData(submissionEntity);
      em.merge(submissionEntity);
    }
  }

  /**
   * delete submission examination data.
   *
   * @param submissionEntity the submission entity
   */
  private void deleteSubmissionExaminationData(SubmissionEntity submissionEntity) {

    LOGGER.log(Level.CONFIG,
      "Executing method deleteSubmissionExaminationData, "
        + "Parameters: submissionEntity: {0}",
      submissionEntity);

    if (submissionEntity != null) {
      deleteCriteriaByType(submissionEntity, LookupValues.MUST_CRITERION_TYPE);
      deleteCriteriaByType(submissionEntity, LookupValues.EVALUATED_CRITERION_TYPE);
      submissionEntity.setMinGrade(BigDecimal.valueOf(0));
      submissionEntity.setMaxGrade(BigDecimal.valueOf(5));
    }
  }

  /**
   * delete submission award data.
   *
   * @param submissionEntity the submission entity
   */
  private void deleteSubmissionAwardData(SubmissionEntity submissionEntity) {

    LOGGER.log(Level.CONFIG,
      "Executing method deleteSubmissionAwardData, Parameters: submissionEntity:{0}",
      submissionEntity);

    if (submissionEntity != null) {
      deleteCriteriaByType(submissionEntity, LookupValues.AWARD_CRITERION_TYPE);
      deleteCriteriaByType(submissionEntity, LookupValues.PRICE_AWARD_CRITERION_TYPE);
      submissionEntity.setEvaluationThrough(null);
      submissionEntity.setAddedAwardRecipients(null);
      submissionEntity.setAwardMinGrade(null);
      submissionEntity.setAwardMaxGrade(null);
    }
  }

  /**
   * Delete criteria by type.
   *
   * @param submissionEntity the submission entity
   * @param criterionType    the criterion type
   */
  private void deleteCriteriaByType(SubmissionEntity submissionEntity, String criterionType) {

    LOGGER.log(Level.CONFIG,
      "Executing method deleteCriteriaByType, Parameters: submissionEntity: {0}, "
        + "criterionType: {1}",
      new Object[]{submissionEntity, criterionType});

    List<CriterionEntity> criterionList = new JPAQueryFactory(em)
      .select(QCriterionEntity.criterionEntity).from(QCriterionEntity.criterionEntity)
      .where(QCriterionEntity.criterionEntity.submission.id.eq(submissionEntity.getId())
        .and(QCriterionEntity.criterionEntity.criterionType.eq(criterionType)))
      .fetch();
    if (!criterionList.isEmpty()) {
      for (CriterionEntity criterionEntity : criterionList) {
        em.remove(criterionEntity);
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SubmissionService#resetDocumentData(java.lang.
   * String)
   */
  @Override
  public void resetDocumentData(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method resetDocumentData, Parameters: submissionId: {0}",
      submissionId);

    SubmissionEntity submissionEntity = em.find(SubmissionEntity.class, submissionId);
    if (submissionEntity != null) {
      deleteCommissionProcurementProposalData(submissionEntity);
      deleteSubmissionCancelData(submissionEntity);
      deleteSubmissionDocuments(submissionEntity);
      em.merge(submissionEntity);
    }
  }

  /**
   * Delete submission documents.
   *
   * @param submissionEntity the submission entity
   */
  private void deleteSubmissionDocuments(SubmissionEntity submissionEntity) {

    LOGGER.log(Level.CONFIG,
      "Executing method deleteSubmissionDocuments, Parameters: submissionEntity: {0}",
      submissionEntity);

    FolderDTO folderDTO = documentService.getFolderByID(submissionEntity.getId(), false, false);
    if (folderDTO != null) {
      documentService.deleteFolder(folderDTO.getId(), null);
    }
  }

  /**
   * Delete submission cancel data.
   *
   * @param submissionEntity the submission entity
   */
  private void deleteSubmissionCancelData(SubmissionEntity submissionEntity) {

    LOGGER.log(Level.CONFIG,
      "Executing method deleteSubmissionCancelData, Parameters: submissionEntity: {0}",
      submissionEntity);

    if (submissionEntity != null) {
      SubmissionCancelEntity submissionCancelEntity =
        new JPAQueryFactory(em).select(QSubmissionCancelEntity.submissionCancelEntity)
          .from(QSubmissionCancelEntity.submissionCancelEntity)
          .where(QSubmissionCancelEntity.submissionCancelEntity.submission.id
            .eq(submissionEntity.getId()))
          .fetchOne();
      if (submissionCancelEntity != null) {
        em.remove(submissionCancelEntity);
      }
    }
  }

  /**
   * Delete commission procurement proposal data.
   *
   * @param submissionEntity the submission entity
   */
  private void deleteCommissionProcurementProposalData(SubmissionEntity submissionEntity) {

    LOGGER.log(Level.CONFIG,
      "Executing method deleteCommissionProcurementProposalData, "
        + "Parameters: submissionEntity: {0}",
      submissionEntity);

    if (submissionEntity != null) {
      submissionEntity.setCommissionProcurementProposalBusiness(null);
      submissionEntity.setCommissionProcurementProposalDate(null);
      submissionEntity.setCommissionProcurementProposalObject(null);
      submissionEntity.setCommissionProcurementProposalPreRemarks(null);
      submissionEntity.setCommissionProcurementProposalReservation(null);
      submissionEntity.setCommissionProcurementProposalSuitabilityAuditDropdown(null);
      submissionEntity.setCommissionProcurementProposalSuitabilityAuditText(null);
      submissionEntity.setCommissionProcurementProposalReasonGiven(null);
    }
  }


  /**
   * We need to set the flag is updated of the fields PmDepartmentName, PmExternal, Procedure,
   * GattTwo in order to distinguish whether the field is manually updated in the submission or just
   * taken from the project because if it is taken from the project then it needs to be updated in
   * every change of the project.
   *
   * @param submissionEntity the submission entity
   */
  private void updateIsUpdatedFlagOnCreate(SubmissionEntity submissionEntity) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateIsUpdatedFlagOnCreate, Parameters: submissionEntity: {0}",
      submissionEntity);

    submissionEntity.setIsPmDepartmentNameUpdated(checkIfPmDepartmentNameDiffers(submissionEntity));
    submissionEntity.setIsPmExternalUpdated(checkIfPmExternalDiffers(submissionEntity));
    submissionEntity.setIsProcedureUpdated(checkIfProcedureDiffers(submissionEntity));
    submissionEntity.setIsGattTwoUpdated(checkIfGattWtoDiffers(submissionEntity));
  }

  /**
   * We need to set the flag is updated of the fields PmDepartmentName, PmExternal, Procedure,
   * GattTwo in order to distinguish whether the field is manually updated in the submission or just
   * taken from the project because if it is taken from the project then it needs to be updated in
   * every change of the project. first check if already manually updated. if not compare with the
   * relative field from the project.
   *
   * @param submissionEntity the submission entity
   */
  private void updateIsUpdatedFlagOnUpdate(SubmissionEntity submissionEntity) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateIsUpdatedFlagOnUpdate, Parameters: submissionEntity: {0}",
      submissionEntity);

    submissionEntity
      .setIsPmDepartmentNameUpdated(submissionEntity.getIsPmDepartmentNameUpdated() != null
        && submissionEntity.getIsPmDepartmentNameUpdated() ? true
        : checkIfPmDepartmentNameDiffers(submissionEntity));
    submissionEntity.setIsPmExternalUpdated(submissionEntity.getIsPmExternalUpdated() != null
      && submissionEntity.getIsPmExternalUpdated() ? true
      : checkIfPmExternalDiffers(submissionEntity));
    submissionEntity.setIsProcedureUpdated(
      submissionEntity.getIsProcedureUpdated() != null && submissionEntity.getIsProcedureUpdated()
        ? true
        : checkIfProcedureDiffers(submissionEntity));
    submissionEntity.setIsGattTwoUpdated(
      submissionEntity.getIsGattTwoUpdated() != null && submissionEntity.getIsGattTwoUpdated()
        ? true
        : checkIfGattWtoDiffers(submissionEntity));
  }

  /**
   * Function that compares the field PmDepartmentName of a submission with the relative field from
   * the project and returns true if they match, false otherwise.
   *
   * @param submissionEntity the submission entity
   * @return the boolean
   */
  Boolean checkIfPmDepartmentNameDiffers(SubmissionEntity submissionEntity) {

    LOGGER.log(Level.CONFIG,
      "Executing method checkIfPmDepartmentNameDiffers, Parameters: submissionEntity: {0}",
      submissionEntity);

    return (submissionEntity.getPmDepartmentName() == null
      && submissionEntity.getProject().getPmDepartmentName() != null)
      || (submissionEntity.getPmDepartmentName() != null && !submissionEntity
      .getPmDepartmentName().equals(submissionEntity.getProject().getPmDepartmentName()));
  }

  /**
   * Function that compares the field PmExternal of a submission with the relative field from the
   * project and returns true if they match, false otherwise.
   *
   * @param submissionEntity the submission entity
   * @return the boolean
   */
  Boolean checkIfPmExternalDiffers(SubmissionEntity submissionEntity) {

    LOGGER.log(Level.CONFIG,
      "Executing method checkIfPmExternalDiffers, Parameters: submissionEntity: {0}",
      submissionEntity);

    return (submissionEntity.getPmExternal() == null
      && submissionEntity.getProject().getPmExternal() != null)
      || (submissionEntity.getPmExternal() != null
      && submissionEntity.getProject().getPmExternal() != null
      && !submissionEntity.getPmExternal().getId()
      .equals(submissionEntity.getProject().getPmExternal().getId()))
      || (submissionEntity.getPmExternal() != null
      && submissionEntity.getProject().getPmExternal() == null);
  }

  /**
   * Function that compares the field Procedure of a submission with the relative field from the
   * project and returns true if they match, false otherwise.
   *
   * @param submissionEntity the submission entity
   * @return the boolean
   */
  Boolean checkIfProcedureDiffers(SubmissionEntity submissionEntity) {

    LOGGER.log(Level.CONFIG,
      "Executing method checkIfProcedureDiffers, Parameters: submissionEntity: {0}",
      submissionEntity);

    return (submissionEntity.getProcedure() == null
      && submissionEntity.getProject().getProcedure() != null)
      || (submissionEntity.getProcedure() != null
      && submissionEntity.getProject().getProcedure() != null
      && !submissionEntity.getProcedure().getId()
      .equals(submissionEntity.getProject().getProcedure().getId()))
      || (submissionEntity.getProcedure() != null
      && submissionEntity.getProject().getProcedure() == null);
  }

  /**
   * Function that compares the field GattWto of a submission with the relative field from the
   * project and returns true if they match, false otherwise.
   *
   * @param submissionEntity the submission entity
   * @return the boolean
   */
  Boolean checkIfGattWtoDiffers(SubmissionEntity submissionEntity) {

    LOGGER.log(Level.CONFIG,
      "Executing method checkIfGattWtoDiffers, Parameters: submissionEntity: {0}",
      submissionEntity);

    return (submissionEntity.getGattTwo() == null
      && submissionEntity.getProject().getGattWto() != null)
      || (submissionEntity.getGattTwo() != null
      && !submissionEntity.getGattTwo().equals(submissionEntity.getProject().getGattWto()));
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SubmissionService#getCurrentStatusDataOfSubmission(
   * java.lang.String)
   */
  @Override
  public TenderStatusHistoryDTO getCurrentStatusDataOfSubmission(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getCurrentStatusDataOfSubmission, Parameters: submissionId: {0}",
      submissionId);

    JPAQuery<TenderStatusHistoryEntity> query = new JPAQuery<>(em);
    // we construct the query so that it returns the entity without the submission,
    // because we don't need the submission data
    TenderStatusHistoryEntity tenderStatusHistoryEntity = query
      .select(Projections.bean(TenderStatusHistoryEntity.class, qTenderStatusHistoryEntity.onDate,
        qTenderStatusHistoryEntity.statusId))
      .from(qTenderStatusHistoryEntity)
      .where(qTenderStatusHistoryEntity.tenderId.id.eq(submissionId))
      // so that we get the current status (status set on the latest date) first
      .orderBy(qTenderStatusHistoryEntity.onDate.desc()).fetchFirst();
    return TenderStatusHistoryDTOMapper.INSTANCE
      .toTenderStatusHistoryDTO(tenderStatusHistoryEntity);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SubmissionService#getDateAndPreviousReopenStatus(
   * java.lang.String)
   */
  @Override
  public TenderStatusHistoryDTO getDateAndPreviousReopenStatus(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getDateAndPreviousReopenStatus, Parameters: submissionId: {0}",
      submissionId);

    JPAQuery<TenderStatusHistoryEntity> query = new JPAQuery<>(em);
    // we construct the query so that it returns the entity without the submission,
    // because we don't need the submission data
    List<TenderStatusHistoryEntity> tenderStatusHistoryEntityList = query
      .select(Projections.bean(TenderStatusHistoryEntity.class, qTenderStatusHistoryEntity.onDate,
        qTenderStatusHistoryEntity.statusId))
      .from(qTenderStatusHistoryEntity)
      .where(qTenderStatusHistoryEntity.tenderId.id.eq(submissionId))
      // so that we get the current status (status set on the latest date) first
      .orderBy(qTenderStatusHistoryEntity.onDate.desc()).fetch();

    // if the last status is closed, then the submission is closed, so return null
    if (!tenderStatusHistoryEntityList.isEmpty() && tenderStatusHistoryEntityList.get(0)
      .getStatusId().equals(TenderStatus.PROCEDURE_COMPLETED.getValue())) {
      return null;
    }

    // iterate the list in order to find if the submission has ever been reopened
    // (in this case we will find a closed or cancelled submission status (but not the last one)
    // keep always the previous date, because it is the date we need, the date of the closure
    Timestamp previousDate = null;
    String previousStatus = null;
    Boolean isReopened = false;
    for (TenderStatusHistoryEntity tenderStatusHistoryEntity : tenderStatusHistoryEntityList) {
      // we use 'previousDate != null' because we are not interested in the first entry (which is
      // actually the last)
      if (previousDate != null && (tenderStatusHistoryEntity.getStatusId()
        .equals(TenderStatus.PROCEDURE_COMPLETED.getValue())
        || tenderStatusHistoryEntity.getStatusId()
        .equals(TenderStatus.PROCEDURE_CANCELED.getValue()))) {
        previousStatus = tenderStatusHistoryEntity.getStatusId();
        isReopened = true;
        break;
      }
      previousDate = tenderStatusHistoryEntity.getOnDate();
    }
    if (isReopened) {
      TenderStatusHistoryEntity returnEntity = new TenderStatusHistoryEntity();
      returnEntity.setStatusId(previousStatus);
      returnEntity.setOnDate(previousDate);
      return TenderStatusHistoryDTOMapper.INSTANCE.toTenderStatusHistoryDTO(returnEntity);
    }

    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SubmissionService#closeApplicationOpening(java.lang
   * .String)
   */
  @Override
  public void closeApplicationOpening(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method closeApplicationOpening, Parameters: submissionId: {0}",
      submissionId);

    SubmissionEntity submissionEntity = em.find(SubmissionEntity.class, submissionId);
    // Set minGrade and maxGrade values of the suitability audit, if the application opening is
    // closed for the first time.
    if (!hasApplicationOpeningBeenClosedBefore(submissionEntity.getId())) {
      submissionEntity.setMinGrade(BigDecimal.valueOf(0));
      submissionEntity.setMaxGrade(BigDecimal.valueOf(5));
      em.merge(submissionEntity);
    }

    /* Update offer sort order. */
    Integer sortOrder = 1;
    List<OfferEntity> offerEntities =
      new JPAQueryFactory(em).select(qOfferEntity).from(qOfferEntity)
        .where(qOfferEntity.submittent.submissionId.id.eq(submissionEntity.getId())
          .and(qOfferEntity.submittent.isApplicant.isTrue())).fetch();
    Collections.sort(offerEntities, ComparatorUtil.sortOfferEntities);
    for (OfferEntity o : offerEntities) {
      o.getSubmittent().setSortOrder(sortOrder);
      em.merge(o);
      sortOrder++;
    }
    // Update status.
    updateSubmissionStatus(submissionEntity, TenderStatus.APPLICATION_OPENING_CLOSED.getValue(),
      AuditMessages.APPLICATION_OPENING_CLOSE.name(), null, LookupValues.EXTERNAL_LOG,
      getUserId());
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SubmissionService#applicantOverviewDocumentExists(
   * java.lang.String)
   */
  @Override
  public boolean applicantOverviewDocumentExists(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method applicantOverviewDocumentExists, Parameters: submissionId: {0}",
      submissionId);

    Map<String, String> map = new HashMap<>();
    String tenantId = new JPAQueryFactory(em).select(qSubmissionEntity.project.tenant.id)
      .from(qSubmissionEntity).where(qSubmissionEntity.id.eq(submissionId)).fetchOne();
    MasterListValueHistoryEntity template = new JPAQueryFactory(em)
      .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
      .where(qMasterListValueHistoryEntity.shortCode.eq(Template.BEWERBER_UBERSICHT)
        .and(qMasterListValueHistoryEntity.tenant.id.eq(tenantId))
        .and(qMasterListValueHistoryEntity.toDate.isNull()))
      .fetchOne();
    map.put(DocumentAttributes.TEMPLATE_ID.name(), template.getMasterListValueId().getId());
    List<NodeDTO> nodeDTOs = documentService.getNodeByAttributes(submissionId, map);
    return !nodeDTOs.isEmpty();
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SubmissionService#applicationDatesFilled(java.lang.
   * String)
   */
  @Override
  public boolean applicationDatesFilled(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method applicationDatesFilled, Parameters: submissionId: {0}",
      submissionId);

    int applicationsWithoutDate = new JPAQueryFactory(em).selectFrom(qOfferEntity)
      .where(qOfferEntity.submittent
        .in(JPAExpressions.selectFrom(qSubmittentEntity)
          .where(qSubmittentEntity.submissionId.id.eq(submissionId)
            .and(qSubmittentEntity.isApplicant.eq(Boolean.TRUE))))
        .and(qOfferEntity.applicationDate.isNull()))
      .fetch().size();
    return applicationsWithoutDate == 0;
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SubmissionService#
   * hasApplicationOpeningBeenClosedBefore(java.lang.String)
   */
  @Override
  public boolean hasApplicationOpeningBeenClosedBefore(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method hasApplicationOpeningBeenClosedBefore, "
        + "Parameters: submissionId: {0}",
      submissionId);

    boolean applicationOpeningClosedBefore = false;
    if (submissionId != null) {
      List<TenderStatusHistoryDTO> allRecentStatuses =
        tenderStatusHistoryService.getSubmissionStatuses(submissionId);
      for (TenderStatusHistoryDTO status : allRecentStatuses) {
        if (status.getStatusId().equals(TenderStatus.APPLICATION_OPENING_CLOSED.getValue())) {
          applicationOpeningClosedBefore = true;
          break;
        }
      }
    }

    return applicationOpeningClosedBefore;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SubmissionService#reopenApplicationOpening(java.
   * lang.String, java.lang.String)
   */
  @Override
  public void reopenApplicationOpening(String reopenReason, String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method reopenApplicationOpening, Parameters: reopenReason: {0}, "
        + "submissionId: {1}",
      new Object[]{reopenReason, submissionId});

    if (getCurrentStatusOfSubmission(submissionId)
      .compareTo(TenderStatus.APPLICATION_OPENING_STARTED.getValue()) > 0) {
      // Change the submission status and create log.
      updateSubmissionStatus(submissionId, TenderStatus.APPLICATION_OPENING_STARTED.getValue(),
        AuditMessages.APPLICATION_OPENING_REOPEN.name(), reopenReason, LookupValues.EXTERNAL_LOG);
    }
  }

  /**
   * Checks if is legal hearing document required.
   *
   * @param submittent       the offer criterion entity
   * @param submissionEntity the submission id
   * @return true, if is legal hearing document required
   */
  private boolean isLegalHearingDocumentRequired(SubmittentEntity submittent,

    SubmissionEntity submissionEntity) {
    LOGGER.log(Level.CONFIG,
      "Executing method isLegalHearingDocumentRequired, Parameters: submittent: {0}, "
        + "submissionEntity: {1}",
      new Object[]{submittent, submissionEntity});

    HashMap<String, String> attributesMap = new HashMap<>();
    boolean documentShoulBeCreated = false;
    // Document should be generated only for submittents that should be excluded from process or
    // their criteria are not fulfilled.
    boolean checkIfDocHasBeenCreated = false;

    List<OfferCriterionEntity> offerCriterionList =
      new JPAQueryFactory(em).selectFrom(qOfferCriterionEntity)
        .where(qOfferCriterionEntity.offer.submittent.id.eq(submittent.getId()).and(
          qOfferCriterionEntity.criterion.criterionType.eq(LookupValues.MUST_CRITERION_TYPE)))
        .fetch();

    // Check if Rechtliches Gehor document has been generated for submittents that should have
    // been
    // excluded.
    if (!offerCriterionList.isEmpty()) {
      for (OfferCriterionEntity offerCriterion : offerCriterionList) {
        // If submittent has Nachweis erbracht = Nein then check MUSS Criteria.
        if (submittent.getFormalExaminationFulfilled() != null
          && !submittent.getFormalExaminationFulfilled()) {
          if (offerCriterion.getCriterion().getCriterionType()
            .equals(LookupValues.MUST_CRITERION_TYPE) && offerCriterion.getIsFulfilled() == null
            || !offerCriterion.getIsFulfilled()) {
            checkIfDocHasBeenCreated = true;
            break;
          }
        } else {
          // If submittent has Nachweis erbracht = Ja then if check existsExclusionReason =
          // true (OBV Art 24).
          if (submittent.getExistsExclusionReasons() != null
            && submittent.getExistsExclusionReasons() && !checkIfDocHasBeenCreated) {
            checkIfDocHasBeenCreated = true;
            break;
          } else {
            // If existsExclusionReason = false then check MUSS Criteria.
            if (offerCriterion.getCriterion().getCriterionType()
              .equals(LookupValues.MUST_CRITERION_TYPE) && offerCriterion.getIsFulfilled() != null
              && !offerCriterion.getIsFulfilled() && !checkIfDocHasBeenCreated) {
              checkIfDocHasBeenCreated = true;
              break;
            }
          }
        }
      }
    } else {
      if (submittent.getIsApplicant() != null && submittent.getIsApplicant()) {
        FormalAuditEntity formalAudit =
          new JPAQueryFactory(em).select(qFormalAuditEntity).from(qFormalAuditEntity)
            .where(qFormalAuditEntity.submittent.id.eq(submittent.getId())).fetchOne();
        if (formalAudit != null && (formalAudit.getFormalExaminationFulfilled() != null
          && formalAudit.getFormalExaminationFulfilled())
          && (formalAudit.getExistsExclusionReasons() != null
          && formalAudit.getExistsExclusionReasons())) {
          checkIfDocHasBeenCreated = true;
        }
      } else {
        if ((submittent.getFormalExaminationFulfilled() == null
          || submittent.getFormalExaminationFulfilled())
          && (submittent.getExistsExclusionReasons() != null
          && submittent.getExistsExclusionReasons())
          && !checkIfDocHasBeenCreated) {
          checkIfDocHasBeenCreated = true;
        }
      }
    }
    if (checkIfDocHasBeenCreated) {
      // Retrieve Rechtliches Gehor document.
      MasterListValueHistoryEntity templateId =
        new JPAQueryFactory(em).select(qMasterListValueHistoryEntity)
          .from(qMasterListValueHistoryEntity)
          .where(qMasterListValueHistoryEntity.shortCode.eq(Template.RECHTLICHES_GEHOR)
            .and(qMasterListValueHistoryEntity.toDate.isNull())
            .and(qMasterListValueHistoryEntity.tenant.id
              .eq(usersService.getUserById(getUser().getId()).getTenant().getId())))
          .fetchOne();
      attributesMap.put(DocumentAttributes.TEMPLATE_ID.name(),
        templateId.getMasterListValueId().getId());
      attributesMap.put(DocumentAttributes.TENDER_ID.name(), submittent.getId());
      attributesMap.put(DocumentAttributes.TENANT_ID.name(), templateId.getTenant().getId());
      attributesMap.put(DocumentAttributes.ADDITIONAL_INFO.name(), "EXCLUSION");
      // Return true if docs have not been generated.
      if (documentService.getNodeByAttributes(submissionEntity.getId(), attributesMap).isEmpty()) {
        documentShoulBeCreated = true;
      }
    }

    return documentShoulBeCreated;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SubmissionService#getSubmissionProcess(java.lang.
   * String)
   */
  public Process getSubmissionProcess(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getSubmissionProcess, Parameters: submissionId: {0}",
      submissionId);

    return new JPAQueryFactory(em).select(qSubmissionEntity.process).from(qSubmissionEntity)
      .where(qSubmissionEntity.id.eq(submissionId)).fetchOne();
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SubmissionService#
   * hasSuitabilityAuditCompletedSBeenSetBefore(java.lang.String)
   */
  @Override
  public boolean hasSuitabilityAuditCompletedSBeenSetBefore(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method hasSuitabilityAuditCompletedSBeenSetBefore, "
        + "Parameters: submissionId: {0}",
      submissionId);

    boolean statusSetBefore = false;
    Process process = new JPAQueryFactory(em).select(qSubmissionEntity.process)
      .from(qSubmissionEntity).where(qSubmissionEntity.id.eq(submissionId)).fetchFirst();
    if (submissionId != null && process.equals(Process.SELECTIVE)) {
      List<TenderStatusHistoryDTO> allRecentStatuses =
        tenderStatusHistoryService.getSubmissionStatuses(submissionId);
      for (TenderStatusHistoryDTO status : allRecentStatuses) {
        if (status.getStatusId().equals(TenderStatus.SUITABILITY_AUDIT_COMPLETED_S.getValue())) {
          statusSetBefore = true;
          break;
        }
      }
    }
    return statusSetBefore;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SubmissionService#loadSubmissionSignatures(java.
   * lang.String)
   */
  @Override
  public SignatureProcessTypeDTO loadSubmissionSignatures(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method loadSubmissionSignatures, Parameters: submissionId: {0}",
      submissionId);

    SubmissionEntity submission = em.find(SubmissionEntity.class, submissionId);
    return sdService.retrieveSignature(submission.getProject().getDepartment().getId(),
      submission.getProject().getDepartmentHistory().getDirectorateEnity().getId(),
      submission.getProcess());
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SubmissionService#getApplicantsBySubmission(java.
   * lang.String)
   */
  @Override
  public List<SubmittentOfferDTO> getApplicantsBySubmission(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getApplicantsBySubmission, Parameters: submissionId: {0}",
      submissionId);

    List<OfferEntity> offerEntityList =
      new JPAQueryFactory(em)
        .select(qOfferEntity).from(qOfferEntity).where(qOfferEntity.submittent.submissionId.id
        .eq(submissionId).and(qOfferEntity.submittent.isApplicant.eq(Boolean.TRUE)))
        .fetch();
    List<SubmittentOfferDTO> submittentOfferDTOs = new ArrayList<>();
    for (OfferEntity offer : offerEntityList) {
      SubmittentOfferDTO submittentOfferDTO = new SubmittentOfferDTO();
      OfferDTO offerDTO = OfferDTOMapper.INSTANCE.toOfferDTO(offer);
      SubmittentDTO submittentDTO =
        SubmittentDTOMapper.INSTANCE.toSubmittentDTO(offer.getSubmittent());
      submittentOfferDTO.setOffer(offerDTO);
      submittentOfferDTO.setSubmittent(submittentDTO);
      submittentOfferDTOs.add(submittentOfferDTO);
    }
    Collections.sort(submittentOfferDTOs, ComparatorUtil.sortCompaniesByOffers);
    return submittentOfferDTOs;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SubmissionService#getApplicantsForFormalAudit(java.
   * lang.String)
   */
  @Override
  public List<SubmittentDTO> getApplicantsForFormalAudit(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getApplicantsForFormalAudit, Parameters: submissionId: {0}",
      submissionId);
    // Get submittent entities by submission.
    List<SubmittentEntity> submittentEntities =
      new JPAQueryFactory(em).select(qSubmittentEntity).from(qSubmittentEntity)
        .where(qSubmittentEntity.submissionId.id.eq(submissionId)
          .and(qSubmittentEntity.isApplicant.eq(Boolean.TRUE)))
        .orderBy(qSubmittentEntity.sortOrder.asc()).fetch();
    List<SubmittentDTO> submittentDTOs = SubmittentDTOMapper.INSTANCE
      .toSubmittentDTO(submittentEntities);
    // Get process of submission.
    Process processType = submittentDTOs.get(0).getSubmissionId().getProcess();
    // Get deadline.
    Date deadline = submittentDTOs.get(0).getSubmissionId().getFirstDeadline();
    // Get formal audit entities by submission.
    List<FormalAuditEntity> formalAuditEntities =
      new JPAQueryFactory(em).selectFrom(qFormalAuditEntity)
        .where(qFormalAuditEntity.submittent.submissionId.id.eq(submissionId)).fetch();
    for (SubmittentDTO submittentDTO : submittentDTOs) {
      for (FormalAuditEntity formalAuditEntity : formalAuditEntities) {
        // Check if the formal audit entity is connected to the current submittent.
        if (formalAuditEntity.getSubmittent().getId().equals(submittentDTO.getId())) {
          // Get the version of formalAuditEntity
          submittentDTO.setFormalAuditVersion(formalAuditEntity.getVersion());
          // Get the values existsExclusionReasons and formalExaminationFulfilled from the formal
          // audit entity and set them to the submittent.
          if (formalAuditEntity.getExistsExclusionReasons() != null) {
            submittentDTO.setExistsExclusionReasons(formalAuditEntity.getExistsExclusionReasons());
          }
          if (formalAuditEntity.getFormalExaminationFulfilled() != null) {
            submittentDTO
              .setFormalExaminationFulfilled(formalAuditEntity.getFormalExaminationFulfilled());
          }
          break;
        }
      }
      projectBean.calculateSubmittentValues(
        submittentDTO, deadline, processType);
    }
    /*if (deadline != null) {
      projectBean.calculateSubmittentValues(
          submittentDTOs, deadline, processType);
    }*/
    if (processType.equals(Process.SELECTIVE) && compareCurrentVsSpecificStatus(
      TenderStatus.fromValue(getCurrentStatusOfSubmission(submissionId)),
      TenderStatus.SUITABILITY_AUDIT_COMPLETED_S)) {
      handleApplicantValues(submittentDTOs);
    }
    return submittentDTOs;
  }

  /**
   * Handles applicant values for formal audit (first level).
   *
   * @param submittentDTOs the submittent DTOs
   */
  private void handleApplicantValues(List<SubmittentDTO> submittentDTOs) {
    List<String> submittentIds = new ArrayList<>();
    // Submittent id to submittent Mapper.
    Map<String, SubmittentDTO> submittentIdsToSubmittents = new HashMap<>();
    for (SubmittentDTO submittentDTO : submittentDTOs) {
      // Add submittent id to submittentIds.
      submittentIds.add(submittentDTO.getId());
      // Map submittent id to submittent DTO.
      submittentIdsToSubmittents.put(submittentDTO.getId(), submittentDTO);
    }
    // Get formal audit entities from submittent ids.
    List<FormalAuditEntity> formalAuditEntities = new JPAQueryFactory(em)
      .selectFrom(qFormalAuditEntity)
      .where(qFormalAuditEntity.submittent.id.in(submittentIds)).fetch();
    for (FormalAuditEntity formalAuditEntity : formalAuditEntities) {
      // Set existsExclusionReasons value to corresponding submittent.
      submittentIdsToSubmittents.get(formalAuditEntity.getSubmittent().getId())
        .setExistsExclusionReasons(formalAuditEntity.getExistsExclusionReasons());
      // Set formalExaminationFulfilled value to corresponding submittent.
      submittentIdsToSubmittents.get(formalAuditEntity.getSubmittent().getId())
        .setFormalExaminationFulfilled(formalAuditEntity.getFormalExaminationFulfilled());
    }
    // Get submittent proof provided entities from submittent ids (first level).
    List<SubmittentProofProvidedEntity> subProofProvidedEntities = new JPAQueryFactory(em)
      .selectFrom(qSubmittentProofProvided)
      .where(qSubmittentProofProvided.submittent.id.in(submittentIds)
        .and(qSubmittentProofProvided.level.eq(SelectiveLevel.FIRST_LEVEL.getValue())))
      .fetch();
    for (SubmittentProofProvidedEntity subProofProvidedEntity : subProofProvidedEntities) {
      // Get the corresponding submittent from the subProofProvidedEntity submittent id.
      SubmittentDTO submittentDTO = submittentIdsToSubmittents
        .get(subProofProvidedEntity.getSubmittent().getId());
      // Check if the submittent company id equals the subProofProvidedEntity company id.
      if (submittentDTO.getCompanyId().getId()
        .equals(subProofProvidedEntity.getCompany().getId())) {
        // Set the isProofProvided value to the submittent from the subProofProvidedEntity.
        submittentDTO.getCompanyId().setIsProofProvided(subProofProvidedEntity.getIsProvided());
      } else {
        for (CompanyDTO jointVenture : submittentDTO.getJointVentures()) {
          // Check if the joint venture company id equals the subProofProvidedEntity company id.
          if (jointVenture.getId().equals(subProofProvidedEntity.getCompany().getId())) {
            // Set the isProofProvided value to the joint venture from the subProofProvidedEntity.
            jointVenture.setIsProofProvided(subProofProvidedEntity.getIsProvided());
            break;
          }
        }
        for (CompanyDTO subcontractor : submittentDTO.getSubcontractors()) {
          // Check if the subcontractor company id equals the subProofProvidedEntity company id.
          if (subcontractor.getId().equals(subProofProvidedEntity.getCompany().getId())) {
            // Set the isProofProvided value to the subcontractor from the subProofProvidedEntity.
            subcontractor.setIsProofProvided(subProofProvidedEntity.getIsProvided());
            break;
          }
        }
      }
    }
  }

  /**
   * Function to calculate submittent values for the formal audit.
   *
   * @param submittentEntities the submittent entities
   * @param deadline           the deadline
   * @param processType        the process type
   * @return the list
   */
  private List<SubmittentDTO> calculateSubmittentValues(List<SubmittentEntity> submittentEntities,
    Date deadline, Process processType, SelectiveLevel level) {

    LOGGER.log(Level.CONFIG,
      "Executing method calculateSubmittentValues, Parameters: "
        + "submittentEntities: {0}, deadline: {1}, "
        + "processType: {2}",
      new Object[]{submittentEntities, deadline, processType});

    List<SubmittentDTO> submittentDTOs = new ArrayList<>();
    for (SubmittentEntity submittent : submittentEntities) {
      // Use SubmittentDTO in order to update the value isProofProvided in CompanyDTO.
      SubmittentDTO submittentDTO = SubmittentDTOMapper.INSTANCE.toSubmittentDTO(submittent);
      // If none of the proofs of the specific company is required then "Nachweise erbracht"
      // should be yes. Set by default isProofProvided to true.
      submittentDTO.getCompanyId().setIsProofProvided(Boolean.TRUE);
      // Calculate the value isProofProvided for every main submittent of the current submission.
      for (CompanyProofEntity cProof : submittent.getCompanyId().getCompanyProofs()) {
        // historization part UC160
        if (cProof.getRequired() != null && cProof.getRequired()) {
          ProofHistoryDTO proof = cacheBean.getValueProof(cProof.getProofId().getId(),
            getRefernceDateForProofs(submittent.getSubmissionId().getId()));
          if (proof.getActive() != null && proof.getActive().equals(1)
            && proof.getValidityPeriod() != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(deadline);
            cal.add(Calendar.MONTH, -proof.getValidityPeriod());
            Date beforeProofValidityPeriod = cal.getTime();
            if (cProof != null && cProof.getProofDate() != null
              && cProof.getProofDate().after(beforeProofValidityPeriod)) {
              submittentDTO.getCompanyId().setIsProofProvided(Boolean.TRUE);
            } else {
              submittentDTO.getCompanyId().setIsProofProvided(Boolean.FALSE);
              break;
            }
          }
        }
      }
      // If the calculated isProofProvided value of the main submittent is false then set the
      // formalExaminationFulfilled to false.
      if (submittentDTO.getCompanyId().getIsProofProvided() != null
        && !submittentDTO.getCompanyId().getIsProofProvided()) {
        submittentDTO.setFormalExaminationFulfilled(Boolean.FALSE);
      } else {
        submittentDTO.setFormalExaminationFulfilled(Boolean.TRUE);
      }
      // Calculate the value isProofProvided for every joint venture of the specific submittent of
      // the current submission.
      if (!submittent.getJointVentures().isEmpty()) {
        Set<CompanyDTO> jointVenturesDTOs = new HashSet<>();
        for (CompanyEntity jointVenture : submittent.getJointVentures()) {
          CompanyDTO jointVentureDTO = CompanyMapper.INSTANCE.toCompanyDTO(jointVenture);
          jointVentureDTO.setIsProofProvided(Boolean.TRUE);
          // Calculate the isProofProvided for every joint venture of the main submittent in the
          // current submission.
          for (CompanyProofEntity cProof : jointVenture.getCompanyProofs()) {
            // historization part UC160
            if (cProof.getRequired() != null && cProof.getRequired()) {
              ProofHistoryDTO proof = cacheBean.getValueProof(cProof.getProofId().getId(),
                getRefernceDateForProofs(submittent.getSubmissionId().getId()));
              if (proof.getActive() != null && proof.getActive().equals(1)
                && proof.getValidityPeriod() != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(deadline);
                cal.add(Calendar.MONTH, -proof.getValidityPeriod());
                Date beforeProofValidityPeriod = cal.getTime();
                if (cProof != null && cProof.getProofDate() != null
                  && cProof.getProofDate().after(beforeProofValidityPeriod)) {
                  jointVentureDTO.setIsProofProvided(Boolean.TRUE);
                } else {
                  jointVentureDTO.setIsProofProvided(Boolean.FALSE);
                  jointVenturesDTOs.add(jointVentureDTO);
                  break;
                }
              }
            }
          }
          jointVenturesDTOs.add(jointVentureDTO);
          // If the calculated isProofProvided value of one of the joint ventures of the main
          // submittent is false then set the formalExaminationFulfilled to false.
          if (jointVentureDTO.getIsProofProvided() != null
            && !jointVentureDTO.getIsProofProvided()) {
            submittentDTO.setFormalExaminationFulfilled(Boolean.FALSE);
          }
        }
        submittentDTO.setJointVentures(jointVenturesDTOs);
      }
      if (!submittent.getSubcontractors().isEmpty()) {
        Set<CompanyDTO> subcontractorsDTOs = new HashSet<>();
        for (CompanyEntity subcontractor : submittent.getSubcontractors()) {
          CompanyDTO subcontractorDTO = CompanyMapper.INSTANCE.toCompanyDTO(subcontractor);
          subcontractorDTO.setIsProofProvided(Boolean.TRUE);
          // Calculate the isProofProvided for every joint venture of the main submittent in the
          // current submission.
          for (CompanyProofEntity cProof : subcontractor.getCompanyProofs()) {
            // historization part UC160 Nachweisart
            if (cProof.getRequired() != null && cProof.getRequired()) {
              ProofHistoryDTO proof = cacheBean.getValueProof(cProof.getProofId().getId(),
                getRefernceDateForProofs(submittent.getSubmissionId().getId()));
              if (proof.getActive() != null && proof.getActive().equals(1)
                && proof.getValidityPeriod() != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(deadline);
                cal.add(Calendar.MONTH, -proof.getValidityPeriod());
                Date beforeProofValidityPeriod = cal.getTime();
                if (cProof != null && cProof.getProofDate() != null
                  && cProof.getProofDate().after(beforeProofValidityPeriod)) {
                  subcontractorDTO.setIsProofProvided(Boolean.TRUE);
                } else {
                  subcontractorDTO.setIsProofProvided(Boolean.FALSE);
                  subcontractorsDTOs.add(subcontractorDTO);
                  break;
                }
              }
            }
          }
          subcontractorsDTOs.add(subcontractorDTO);
          // If the calculated isProofProvided value of one of the subcontractors of the main
          // submittent is false then set the formalExaminationFulfilled to false.
          if (subcontractorDTO.getIsProofProvided() != null
            && !subcontractorDTO.getIsProofProvided()) {
            submittentDTO.setFormalExaminationFulfilled(Boolean.FALSE);
          }
        }
        submittentDTO.setSubcontractors(subcontractorsDTOs);
      }
      /*
       * If value formalExaminationFulfilled is false then set existsExclusionReasons to true. Don't
       * set this Value for the following Processes 1.NEGOTIATED_PROCEDURE
       * 2.NEGOTIATED_PROCEDURE_WITH_COMPETITION
       */
      if (submittentDTO.getFormalExaminationFulfilled() != null
        && !submittentDTO.getFormalExaminationFulfilled()
        && !processType.equals(Process.NEGOTIATED_PROCEDURE)
        && !processType.equals(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION)
        && !(processType.equals(Process.SELECTIVE)
        && level.equals(SelectiveLevel.SECOND_LEVEL))) {
        submittentDTO.setExistsExclusionReasons(Boolean.TRUE);
      }

      Boolean isVariant = new JPAQueryFactory(em).select(qOfferEntity.isVariant).from(qOfferEntity)
        .where(qOfferEntity.submittent.id.eq(submittent.getId())).fetchFirst();
      submittentDTO.setIsVariant(isVariant);

      submittentDTOs.add(submittentDTO);
    }
    return submittentDTOs;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SubmissionService#submissionCancelNavigation(java.
   * lang.String)
   */
  @Override
  public boolean submissionCancelNavigation(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method submissionCancelNavigation, Parameters: submissionId: {0}",
      submissionId);
    // Check if Rechtliches Gehor (Abbruch) document has been generated.
    HashMap<String, String> attributesMap = new HashMap<>();
    SubmissionEntity submissionEntity = em.find(SubmissionEntity.class, submissionId);
    MasterListValueHistoryEntity templateId =
      new JPAQueryFactory(em).select(qMasterListValueHistoryEntity)
        .from(qMasterListValueHistoryEntity)
        .where(qMasterListValueHistoryEntity.shortCode.eq(Template.RECHTLICHES_GEHOR)
          .and(qMasterListValueHistoryEntity.toDate.isNull())
          .and(qMasterListValueHistoryEntity.tenant.id
            .eq(usersService.getUserById(getUser().getId()).getTenant().getId())))
        .fetchOne();
    attributesMap.put(DocumentAttributes.TENANT_ID.name(), templateId.getTenant().getId());
    attributesMap.put(DocumentAttributes.TEMPLATE_ID.name(),
      templateId.getMasterListValueId().getId());
    attributesMap.put(DocumentAttributes.ADDITIONAL_INFO.name(), "CANCELATION");
    for (SubmittentEntity submittentEntity : submissionEntity.getSubmittents()) {
      attributesMap.put(DocumentAttributes.TENDER_ID.name(), submittentEntity.getId());
      if (documentService.getNodeByAttributes(submissionEntity.getId(), attributesMap).isEmpty()) {
        return false;
      }
    }
    return true;
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SubmissionService#getUserGroupName()
   */
  @Override
  public String getUserGroupName() {

    LOGGER.log(Level.CONFIG, "Executing method getUserGroupName");

    UserDTO user = getUser();
    return getGroupName(user);
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SubmissionService#getThresholdValue(java.lang.
   * String)
   */
  @Override
  public BigDecimal getThresholdValue(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getThresholdValue, Parameters: submissionId: {0}",
      submissionId);

    SubmissionEntity submissionEntity = em.find(SubmissionEntity.class, submissionId);
    return procedureService.readProcedure(submissionEntity.getProcessType().getId(),
      Process.INVITATION, submissionEntity.getProject().getTenant().getId()).getValue();
  }

  /**
   * Creates the task.
   *
   * @param submissionDTO the submission DTO
   * @param taskType      the task type
   */
  private void createTask(SubmissionDTO submissionDTO, TaskTypes taskType) {

    LOGGER.log(Level.CONFIG,
      "Executing method createTask, Parameters: submissionDTO: {0}, "
        + "taskType: {1}",
      new Object[]{submissionDTO, taskType});

    SubmissTaskDTO taskDTO = new SubmissTaskDTO();
    taskDTO.setSubmission(submissionDTO);
    taskDTO.setDescription(taskType);

    taskService.createTask(taskDTO);

  }

  /**
   * Check if the award notices first level document exists.
   *
   * @param submissionId the submission id
   * @return true if document exists
   */
  private boolean awardNoticesFirstLevelDocumentExists(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method awardNoticesFirstLevelDocumentExists, "
        + "Parameters: submissionId: {0}",
      submissionId);

    Map<String, String> map = new HashMap<>();
    String tenantId = new JPAQueryFactory(em).select(qSubmissionEntity.project.tenant.id)
      .from(qSubmissionEntity).where(qSubmissionEntity.id.eq(submissionId)).fetchOne();
    MasterListValueHistoryEntity template = new JPAQueryFactory(em)
      .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
      .where(qMasterListValueHistoryEntity.shortCode.eq(Template.SELEKTIV_1_STUFE)
        .and(qMasterListValueHistoryEntity.toDate.isNull())
        .and(qMasterListValueHistoryEntity.tenant.id.eq(tenantId)))
      .fetchOne();
    map.put(DocumentAttributes.TEMPLATE_ID.name(), template.getMasterListValueId().getId());
    List<NodeDTO> nodeDTOs = documentService.getNodeByAttributes(submissionId, map);
    return !nodeDTOs.isEmpty();
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SubmissionService#getSubmissionPmExternal(java.lang
   * .String)
   */
  @Override
  public CompanyDTO getSubmissionPmExternal(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getSubmissionPmExternal, Parameters: submissionId: {0}",
      submissionId);

    return CompanyMapper.INSTANCE
      .toCompanyDTO(new JPAQueryFactory(em).select(qSubmissionEntity.pmExternal)
        .from(qSubmissionEntity).where(qSubmissionEntity.id.eq(submissionId)).fetchOne());
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SubmissionService#getSubmittentListEmails(java.lang
   * .String)
   */
  @Override
  public List<String> getSubmittentListEmails(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getSubmittentListEmails, Parameters: submissionId: {0}",
      submissionId);

    List<SubmittentEntity> submittents = new JPAQueryFactory(em).selectDistinct(qSubmittentEntity)
      .from(qSubmittentEntity).where(qSubmittentEntity.submissionId.id.eq(submissionId)).fetch();
    List<String> emailList = new ArrayList<>();
    for (SubmittentEntity submittent : submittents) {
      if (!emailList.contains(submittent.getCompanyId().getCompanyEmail())) {
        emailList.add(submittent.getCompanyId().getCompanyEmail());
      }
      for (CompanyEntity subcontractor : submittent.getSubcontractors()) {
        if (!emailList.contains(subcontractor.getCompanyEmail())) {
          emailList.add(subcontractor.getCompanyEmail());
        }
      }
      for (CompanyEntity jointVenture : submittent.getJointVentures()) {
        if (!emailList.contains(jointVenture.getCompanyEmail())) {
          emailList.add(jointVenture.getCompanyEmail());
        }
      }
    }
    return emailList;
  }


  @Override
  public Set<ValidationError> moveProjectData(String submissionId, Long submissionVersion,
    String projectId, Long projectVersion) {

    LOGGER.log(Level.CONFIG, "Executing method moveProjectData, Parameters: submissionId: {0}, "
        + "submissionVersion: {1}, projectId: {2}, projectVersion: {3}",
      new Object[]{submissionId, submissionVersion, projectId, projectVersion});

    SubmissionEntity submissionEntity = em.find(SubmissionEntity.class, submissionId);
    ProjectEntity projectEntity = em.find(ProjectEntity.class, projectId);

    // Check for optimistic lock errors
    Set<ValidationError> optimisticLockErrors =
      moveProjectDataOptimisticLock(submissionEntity, submissionVersion, projectEntity,
        projectVersion);

    if (!optimisticLockErrors.isEmpty()) {
      return optimisticLockErrors;
    }

    try {
      submissionEntity.setProject(projectEntity);
      em.merge(submissionEntity);
      auditLogMoveProjectData(submissionEntity);
    } catch (OptimisticLockException | RollbackException e) {
      optimisticLockErrors
        .add(new ValidationError(ValidationMessages.OPTIMISTIC_LOCK_ERROR_FIELD,
          ValidationMessages.OPTIMISTIC_LOCK));
    }

    return optimisticLockErrors;
  }

  /**
   * Create audit log for moveProjectData.
   *
   * @param submissionEntity the submissionEntity
   */
  private void auditLogMoveProjectData(SubmissionEntity submissionEntity) {

    LOGGER.log(Level.CONFIG, "Executing method auditLogMoveProjectData, Parameters: "
      + "submissionEntity: {0}", submissionEntity);

    MasterListValueHistoryEntity workType = new JPAQueryFactory(em)
      .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
      .where(qMasterListValueHistoryEntity.masterListValueId
        .eq(submissionEntity.getWorkType())
        .and(qMasterListValueHistoryEntity.toDate.isNull()))
      .fetchOne();

    MasterListValueHistoryEntity objectEntity = new JPAQueryFactory(em)
      .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
      .where(qMasterListValueHistoryEntity.masterListValueId
        .eq(submissionEntity.getProject().getObjectName())
        .and(qMasterListValueHistoryEntity.toDate.isNull()))
      .fetchOne();

    String workTypeValue2 = (StringUtils.isEmpty(workType.getValue2())) ? StringUtils.EMPTY
      : LookupValues.SPACE + workType.getValue2();

    StringBuilder additionalInfo =
      new StringBuilder(submissionEntity.getProject().getProjectName()).append("[#]")
        .append(objectEntity.getValue1()).append("[#]").append(workType.getValue1())
        .append(workTypeValue2).append("[#]")
        .append(getUser().getAttributeData(LookupValues.USER_ATTRIBUTES.TENANT.getValue()))
        .append("[#]");

    auditLog(AuditEvent.DATA_MOVED.name(), AuditMessages.SUMBISSION_DATA_MOVED.name(),
      submissionEntity.getId(), additionalInfo.toString());
  }

  /**
   * Check for optimistic lock errors when moving project data.
   *
   * @param submissionEntity  the submissionEntity
   * @param submissionVersion the submissionVersion
   * @param projectEntity     the projectEntity
   * @param projectVersion    the projectVersion
   * @return the error
   */
  private Set<ValidationError> moveProjectDataOptimisticLock(SubmissionEntity submissionEntity,
    Long submissionVersion, ProjectEntity projectEntity, Long projectVersion) {

    LOGGER.log(Level.CONFIG,
      "Executing method moveProjectDataOptimisticLock, Parameters: submissionEntity: {0}, "
        + "submissionVersion: {1}, projectEntity: {2}, projectVersion: {3}",
      new Object[]{submissionEntity, submissionVersion, projectEntity, projectVersion});

    Set<ValidationError> optimisticLockErrors = new HashSet<>();
    if (submissionEntity == null || projectEntity == null
      || !submissionEntity.getVersion().equals(submissionVersion)
      || !projectEntity.getVersion().equals(projectVersion)) {
      optimisticLockErrors
        .add(new ValidationError(ValidationMessages.OPTIMISTIC_LOCK_ERROR_FIELD,
          ValidationMessages.OPTIMISTIC_LOCK));
    }
    return optimisticLockErrors;
  }

  @Override
  public void automaticallyCloseSubmissions() {

    LOGGER.log(Level.CONFIG, "Executing method automaticallyCloseSubmissions");

    // find submissions that meet the conditions for automatic closure
    List<SubmissionEntity> submissionEntityList = getSubmissionsToClose();

    // every one of them needs to be closed
    for (SubmissionEntity submissionEntity : submissionEntityList) {
      updateSubmissionStatus(submissionEntity, TenderStatus.PROCEDURE_COMPLETED.getValue(),
        ValidationMessages.AUTOMATICALLY_CLOSE_SUBMISSION_MESSAGE, null,
        LookupValues.EXTERNAL_LOG, LookupValues.SYSTEM);
    }
  }

  /**
   * Finds all submissions with current status AWARD_NOTICES_CREATED or CONTRACT_CREATED or
   * PROCEDURE_CANCELED without any status change in the last 40 days (move to status
   * CONTRACT_CREATED does not count as status change, the counter for the 40 days starts at the set
   * of status AWARD_NOTICES_CREATED) having the Beschwerdeeingang field (in Verfügungen erstellt or
   * Verfahrensabbruch tab) not selected and if the counter for the automatic closure of the
   * submission is initiated (it gets initiated when the Beschwerdeeingang field gets selected and
   * then deselected) then to be initiated more than 40 days ago without having a to do task to set
   * the publicationDateAward field (this check is only for status AWARD_NOTICES_CREATED or
   * CONTRACT_CREATED).
   *
   * @return a list of submission
   */
  private List<SubmissionEntity> getSubmissionsToClose() {

    LOGGER.log(Level.CONFIG, "Executing method getSubmissionsToClose");

    return new JPAQueryFactory(em).select(qSubmissionEntity).from(qSubmissionEntity)
      .where(
        qSubmissionEntity.status
          .in(TenderStatus.AWARD_NOTICES_CREATED.getValue(),
            TenderStatus.CONTRACT_CREATED.getValue(),
            TenderStatus.PROCEDURE_CANCELED.getValue())
          // current status is AWARD_NOTICES_CREATED (or the one before the current in case
          // current status is CONTRACT_CREATED)
          // and it is set more than 40 days ago
          .and(qSubmissionEntity.id
            .in(JPAExpressions.select(qTenderStatusHistoryEntity.tenderId.id)
              .from(qTenderStatusHistoryEntity)
              .where(qTenderStatusHistoryEntity.statusId
                .eq(TenderStatus.AWARD_NOTICES_CREATED.getValue())
                .and(qTenderStatusHistoryEntity.onDate.loe(Timestamp
                  .valueOf(
                    LocalDate.now().minusDays(LookupValues.FORTY).atStartOfDay())))
                .and(qTenderStatusHistoryEntity.onDate
                  .in(JPAExpressions.select(qTenderStatusHistoryEntity1.onDate.max())
                    .from(qTenderStatusHistoryEntity1)
                    .where(qTenderStatusHistoryEntity1.tenderId
                      .eq(qTenderStatusHistoryEntity.tenderId)
                      .and(qTenderStatusHistoryEntity1.statusId
                        .ne(TenderStatus.CONTRACT_CREATED.getValue())))))
                // no entry exists with freeze flag true or timer set less than 40 days
                // ago
                .and(qTenderStatusHistoryEntity.tenderId.id.notIn(
                  JPAExpressions.select(qSubmissionAwardInfoEntity.submission.id)
                    .from(qSubmissionAwardInfoEntity)
                    .where(qSubmissionAwardInfoEntity.freezeCloseSubmission.isTrue()
                      .or(qSubmissionAwardInfoEntity.closeCountdownStart
                        .gt(Timestamp.valueOf(LocalDate.now()
                          .minusDays(LookupValues.FORTY).atStartOfDay()))))))
                // no to do task to set the publicationDateAward field
                .and(qTenderStatusHistoryEntity.tenderId.id.notIn(JPAExpressions
                  .select(qSubmissTask.submission.id).from(qSubmissTask).where(
                    qSubmissTask.description.eq(TaskTypes.SET_SURCHARGE_PDATE))))))
            // current status is PROCEDURE_CANCELED
            // and it is set more than 40 days ago
            .or(qSubmissionEntity.id.in(JPAExpressions
              .select(qTenderStatusHistoryEntity.tenderId.id)
              .from(qTenderStatusHistoryEntity)
              .where(qTenderStatusHistoryEntity.statusId
                .eq(TenderStatus.PROCEDURE_CANCELED.getValue())
                .and(qTenderStatusHistoryEntity.onDate.loe(Timestamp.valueOf(
                  LocalDate.now().minusDays(LookupValues.FORTY).atStartOfDay())))
                .and(qTenderStatusHistoryEntity.onDate
                  .in(JPAExpressions.select(qTenderStatusHistoryEntity1.onDate.max())
                    .from(qTenderStatusHistoryEntity1)
                    .where(qTenderStatusHistoryEntity1.tenderId
                      .eq(qTenderStatusHistoryEntity.tenderId))))
                // no entry exists with freeze flag true or timer set less than 40 days
                // ago
                .and(qTenderStatusHistoryEntity.tenderId.id.notIn(JPAExpressions
                  .select(qSubmissionCancelEntity.submission.id)
                  .from(qSubmissionCancelEntity)
                  .where(qSubmissionCancelEntity.freezeCloseSubmission.isTrue()
                    .or(qSubmissionCancelEntity.closeCountdownStart
                      .gt(Timestamp.valueOf(LocalDate.now()
                        .minusDays(LookupValues.FORTY).atStartOfDay())))))))))))
      .fetch();
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SubmissionService#
   * checkToDeleteTaskAndCloseSubmission(ch.bern.submiss.services.api.dto.SubmissionDTO)
   */
  @Override
  public void checkToDeleteTaskAndCloseSubmission(SubmissionDTO submissionDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method checkToDeleteTaskAndCloseSubmission, Parameters: submissionDTO: {0}",
      submissionDTO);

    SubmissionEntity submissionEntity = SubmissionMapper.INSTANCE.toSubmission(submissionDTO);
    checkToDeleteTaskAndCloseSubmission(submissionEntity);
  }

  /**
   * Check if a to do task exists to set the publicationDateAward. If it exists then it must be
   * deleted and if 1) the current status of the submission is AWARD_NOTICES_CREATED or
   * CONTRACT_CREATED 2) there was no status change in the last 40 days (move to status
   * CONTRACT_CREATED does not count as status change, the counter for the 40 days starts at the set
   * of status AWARD_NOTICES_CREATED) 3) the Beschwerdeeingang field (in Verfügungen erstellt or
   * Verfahrensabbruch tab) is unselected 4) if the counter for the automatic closure of the
   * submission is initiated (it gets initiated when the Beschwerdeeingang field gets selected and
   * then deselected), then to be initiated more than 40 days ago, then the submission needs to be
   * closed
   *
   * @param submissionEntity the submission entity
   * @return the string
   */
  private void checkToDeleteTaskAndCloseSubmission(SubmissionEntity submissionEntity) {

    LOGGER.log(Level.CONFIG,
      "Executing method checkToDeleteTaskAndCloseSubmission, Parameters: "
        + "submissionEntity: {0}",
      submissionEntity);
    // Delete task. If found
    if (taskService.settleTask(submissionEntity.getId(), null, TaskTypes.SET_SURCHARGE_PDATE, null,
      null)
      // and the current status of the submission is AWARD_NOTICES_CREATED or CONTRACT_CREATED
      && (submissionEntity.getStatus().equals(TenderStatus.AWARD_NOTICES_CREATED.getValue())
      || submissionEntity.getStatus().equals(TenderStatus.CONTRACT_CREATED.getValue()))
      // and there was no status change in the last 40 days
      // and the counter for the automatic closure of the submission is set more than 40 days ago
      && checkConditionsToCloseSubmission(submissionEntity.getId())) {
      updateSubmissionStatus(submissionEntity, TenderStatus.PROCEDURE_COMPLETED.getValue(),
        AuditMessages.AUTOMATICALLY_CLOSE_SUBMISSION.name(), null, LookupValues.EXTERNAL_LOG,
        getUserId());
    }
  }

  /**
   * Checks if the status AWARD_NOTICES_CREATED is the current status of the submission (or the one
   * before the current in case current status is CONTRACT_CREATED) and it is set more than 40 days
   * ago and the Beschwerdeeingang field (in Verfügungen erstellt or Verfahrensabbruch tab) is not
   * selected and if the counter for the automatic closure of the submission is initiated (it gets
   * initiated when the Beschwerdeeingang field gets selected and then deselected), then to be
   * initiated more than 40 days ago.
   *
   * @param submissionId the submission id
   * @return a boolean indicating if the given submission applies the above conditions
   */
  private boolean checkConditionsToCloseSubmission(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method checkConditionsToCloseSubmission, Parameters: submissionId: {0}",
      submissionId);

    long found =
      new JPAQueryFactory(em).select(qTenderStatusHistoryEntity).from(qTenderStatusHistoryEntity)
        // check if this submission
        .where(qTenderStatusHistoryEntity.tenderId.id.eq(submissionId)
          // has status AWARD_NOTICES_CREATED
          .and(qTenderStatusHistoryEntity.statusId
            .eq(TenderStatus.AWARD_NOTICES_CREATED.getValue())
            // set more than 40 days ago
            .and(qTenderStatusHistoryEntity.onDate.loe(Timestamp
              .valueOf(LocalDate.now().minusDays(LookupValues.FORTY).atStartOfDay())))
            // and this is the current status of the submission
            .and(qTenderStatusHistoryEntity.onDate
              .in(JPAExpressions.select(qTenderStatusHistoryEntity1.onDate.max())
                .from(qTenderStatusHistoryEntity1)
                .where(qTenderStatusHistoryEntity1.tenderId.id.eq(submissionId)
                  // (or the one before the current in case current status is
                  // CONTRACT_CREATED)
                  .and(qTenderStatusHistoryEntity1.statusId
                    .ne(TenderStatus.CONTRACT_CREATED.getValue()))))))
          // no entry exists with freeze flag true or timer set less than 40 days
          // ago
          .and(qTenderStatusHistoryEntity.tenderId.id
            .notIn(JPAExpressions.select(qSubmissionAwardInfoEntity.submission.id)
              .from(qSubmissionAwardInfoEntity)
              .where(qSubmissionAwardInfoEntity.freezeCloseSubmission.isTrue()
                .or(qSubmissionAwardInfoEntity.closeCountdownStart.gt(Timestamp.valueOf(
                  LocalDate.now().minusDays(LookupValues.FORTY).atStartOfDay())))))))
        .fetchCount();
    return (found != 0);
  }

  /**
   * Audit log.
   *
   * @param event        the event
   * @param auditMessage the audit message
   * @param submissionId the submission id
   * @param projectVars  the project vars
   */
  private void auditLog(String event, String auditMessage, String submissionId,
    String projectVars) {

    LOGGER.log(Level.CONFIG,
      "Executing method auditLog, Parameters: event: {0} , auditMessage: {1}, "
        + "submissionId: {2}, projectVars: {3}",
      new Object[]{event, auditMessage, submissionId, projectVars});

    audit.createLogAudit(AuditLevel.PROJECT_LEVEL.name(), event, AuditGroupName.SUBMISSION.name(),
      auditMessage, getUser().getId(), submissionId, null, projectVars,
      LookupValues.EXTERNAL_LOG);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SubmissionService#lockSubmission(java.lang.String,
   * java.lang.String)
   */
  @Override
  public void lockSubmission(String submissionId, String type, boolean isExortlocked) {

    LOGGER.log(Level.CONFIG, "Executing method lockSubmission, Parameters: submissionId: {0}, "
        + "type: {1}, isExortlocked: {2}",
      new Object[]{submissionId, type, isExortlocked});

    String userName = getUserFullName();
    SubmissionEntity submission = em.find(SubmissionEntity.class, submissionId);

    if (type.equals(LookupValues.CRITERION_TYPE.SUITABILITY.getValue())) {
      submission.setExaminationIsLocked(Boolean.TRUE);
      if (isExortlocked) {
        submission.setExaminationLockedBy(userName);
        submission.setExaminationLockedTime(new Timestamp((new Date()).getTime()));
      }
    } else {
      submission.setAwardIsLocked(Boolean.TRUE);
      if (isExortlocked) {
        submission.setAwardLockedBy(userName);
        submission.setAwardLockedTime(new Timestamp((new Date()).getTime()));
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SubmissionService#unlockSubmission(java.lang.
   * String, java.lang.String)
   */
  @Override
  public void unlockSubmission(String submissionId, String type) {

    LOGGER.log(Level.CONFIG,
      "Executing method unlockSubmission, Parameters: submissionId: {0}" + ", type: {1}",
      new Object[]{submissionId, type});

    SubmissionEntity submission = em.find(SubmissionEntity.class, submissionId);
    if (type.equals(LookupValues.CRITERION_TYPE.SUITABILITY.getValue())) {
      submission.setExaminationIsLocked(Boolean.FALSE);
      submission.setExaminationLockedBy("");
    } else {
      submission.setAwardIsLocked(Boolean.FALSE);
      submission.setAwardLockedBy("");
    }

  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SubmissionService#getUniqueSubmittentList(java.lang
   * .String)
   */
  @Override
  public List<CompanyDTO> getUniqueSubmittentList(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getUniqueSubmittentList, Parameters: submissionId: {0}",
      submissionId);

    return CompanyMapper.INSTANCE.toCompanyDTO(
      new JPAQueryFactory(em).selectDistinct(qSubmittentEntity.companyId).from(qSubmittentEntity)
        .where(qSubmittentEntity.submissionId.id.eq(submissionId)).fetch());
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SubmissionService#checkSubmissionThreshold(java.
   * lang.String)
   */
  @Override
  public boolean checkSubmissionThreshold(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method checkSubmissionThreshold, Parameters: submissionId: {0}",
      submissionId);

    ProcedureHistoryDTO threshold = null;
    SubmissionEntity submissionEntity = em.find(SubmissionEntity.class, submissionId);

    threshold = procedureService.readProcedure(submissionEntity.getProcessType().getId(),
      Process.INVITATION, submissionEntity.getProject().getTenant().getId());

    List<OfferEntity> offerEntities =
      new JPAQueryFactory(em).select(qOfferEntity).from(qOfferEntity)
        .where(qOfferEntity.submittent.submissionId.eq(submissionEntity)).fetch();

    for (OfferEntity offerEntity : offerEntities) {
      if (threshold != null) {
        BigDecimal tresholdValue = threshold.getValue();
        if (offerEntity.getAmount() != null
          && (offerEntity.getAmount().compareTo(tresholdValue)) == 1
          && ((submissionEntity.getProcess() == Process.NEGOTIATED_PROCEDURE) || submissionEntity
          .getProcess() == Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION)) {
          submissionEntity.setAboveThreshold(true);
          return true;
        }

      }
    }
    return false;
  }

  /**
   * The below functions:
   * (getDateOfCompletedOrCanceledStatus,getDateOfCreationStatusOfSubmission,
   * getRefernceDateForObject,getRefernceDateForProofs) are set to achieve the
   * historization required for UC 160 and retrieve specific dates that are
   * different for each MasterData type. These dates are useful if the submission
   * status is greater than a specific value,because above this status, MasterData
   * values need to 'freeze',regardless if they change in Stammdaten bearbeiten
   * part of the app.If submission status is not above a specific specific status
   * value,then null date is returned. The function
   * getDateOfCreationStatusOfSubmission is used for Arbeitsgattung and
   * Verrechnungsart data type, where MasterData values need to 'freeze' after
   * Submission is created,that's why null date is never returned there.
   *
   * @param submissionId the submission id
   * @return the date of completed or canceled status
   */

  /**
   * This function is used for historization purposes,UC 160
   */
  @Override
  public Timestamp getDateOfCompletedOrCanceledStatus(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getDateOfCompletedOrCanceledStatus, Parameters: submissionId: {0}",
      submissionId);

    SubmissionEntity submissionEntity = em.find(SubmissionEntity.class, submissionId);
    if (compareCurrentVsSpecificStatus(TenderStatus.fromValue(submissionEntity.getStatus()),
      TenderStatus.PROCEDURE_CANCELED)) {
      TenderStatusHistoryEntity cancelStatus = new TenderStatusHistoryEntity();
      for (Iterator<TenderStatusHistoryEntity> iterator =
        submissionEntity.getTenderStatusHistory().iterator(); iterator.hasNext(); ) {
        TenderStatusHistoryEntity status = iterator.next();
        if (status.getStatusId().equals(TenderStatus.PROCEDURE_CANCELED.getValue())
          || status.getStatusId().equals(TenderStatus.PROCEDURE_COMPLETED.getValue())) {
          cancelStatus = status;
          break;
        }
      }
      // Return the latest date when the submission has been cancelled or completed.
      return cancelStatus.getOnDate();
    }
    return null;
  }

  /**
   * This function is used for historization purposes,UC 160 Arbeitsgattung, Verrechnungsart.
   *
   * @param submissionId the submission id
   * @return the date of creation status of submission
   */
  @Override
  public Timestamp getDateOfCreationStatusOfSubmission(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getDateOfCreationStatusOfSubmission, Parameters: submissionId: {0}",
      submissionId);

    SubmissionEntity submissionEntity = em.find(SubmissionEntity.class, submissionId);
    Iterator<TenderStatusHistoryEntity> itr = submissionEntity.getTenderStatusHistory().iterator();
    TenderStatusHistoryEntity creationStatus = itr.next();
    while (itr.hasNext()) {
      creationStatus = itr.next();
    }

    return creationStatus.getOnDate();
  }

  /**
   * This function is used for historization purposes,UC 160 Object.
   *
   * @param submissionId the submission id
   * @return the reference date for object
   */
  @Override
  public Timestamp getRefernceDateForObject(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getRefernceDateForObject, Parameters: submissionId: {0}",
      submissionId);

    SubmissionEntity submissionEntity = em.find(SubmissionEntity.class, submissionId);
    // Compare with the current submission status.
    if ((submissionEntity.getProcess() == Process.SELECTIVE
      && (compareCurrentVsSpecificStatus(TenderStatus.fromValue(submissionEntity.getStatus()),
      TenderStatus.APPLICATION_OPENING_CLOSED)))
      || (submissionEntity.getProcess() != Process.SELECTIVE
      && compareCurrentVsSpecificStatus(TenderStatus.fromValue(submissionEntity.getStatus()),
      TenderStatus.OFFER_OPENING_CLOSED))) {
      TenderStatusHistoryEntity offerCloseStatus = new TenderStatusHistoryEntity();
      for (Iterator<TenderStatusHistoryEntity> iterator =
        submissionEntity.getTenderStatusHistory().iterator(); iterator.hasNext(); ) {
        TenderStatusHistoryEntity status = iterator.next();
        // Get the object reference date.
        if ((!submissionEntity.getProcess().equals(Process.SELECTIVE)
          && status.getStatusId().equals(TenderStatus.OFFER_OPENING_CLOSED.getValue())) ||
          (submissionEntity.getProcess().equals(Process.SELECTIVE)
            && status.getStatusId().equals(TenderStatus.APPLICATION_OPENING_CLOSED.getValue()))) {
          offerCloseStatus = status;
          break;
        }
      }
      // Return the latest date when the offer has been closed.
      return offerCloseStatus.getOnDate();
    } else {
      // Return null if submission status is not equals with offer close.
      return null;
    }
  }


  /**
   * This function is used for historization purposes,UC 160 Nachweisart.
   *
   * @param submissionId the submission id
   * @return the refernce date for proofs
   */
  @Override
  public Timestamp getRefernceDateForProofs(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getRefernceDateForProofs, Parameters: submissionId: {0}",
      submissionId);

    SubmissionEntity submissionEntity = em.find(SubmissionEntity.class, submissionId);
    if ((submissionEntity.getProcess() != Process.OPEN
      && (compareCurrentVsSpecificStatus(TenderStatus.fromValue(submissionEntity.getStatus()),
      TenderStatus.FORMAL_AUDIT_COMPLETED)))
      || (submissionEntity.getProcess() == Process.OPEN
      && compareCurrentVsSpecificStatus(TenderStatus.fromValue(submissionEntity.getStatus()),
      TenderStatus.SUITABILITY_AUDIT_COMPLETED))) {
      TenderStatusHistoryEntity formalAuditCloseStatus = new TenderStatusHistoryEntity();
      for (Iterator<TenderStatusHistoryEntity> iterator =
        submissionEntity.getTenderStatusHistory().iterator(); iterator.hasNext(); ) {
        TenderStatusHistoryEntity status = iterator.next();
        if ((!submissionEntity.getProcess().equals(Process.OPEN)
          && status.getStatusId().equals(TenderStatus.FORMAL_AUDIT_COMPLETED.getValue())) ||
          (submissionEntity.getProcess().equals(Process.OPEN)
            && status.getStatusId().equals(TenderStatus.SUITABILITY_AUDIT_COMPLETED.getValue()))) {
          formalAuditCloseStatus = status;
          break;
        }
      }
      // Return the latest date when the formal audit has been closed.
      return formalAuditCloseStatus.getOnDate();
    } else {
      return null;
    }

  }

  /* (non-Javadoc)
   * @see ch.bern.submiss.services.api.administration.SubmissionService#getSubmittentsCount(java.lang.String)
   */
  @Override
  public int getSubmittentsCount(String id) {

    LOGGER.log(Level.CONFIG,
      "Executing method getSubmittentsCount, Parameters: id: {0}",
      id);

    SubmissionEntity submission = em.find(SubmissionEntity.class, id);
    return submission.getSubmittents().size();
  }

  @Override
  public List<SubmittentDTO> retrieveEmptyOffers(SubmissionDTO submission) {
    List<SubmittentEntity> submittents =
      new JPAQueryFactory(em)
        .select(qOfferEntity.submittent).from(qOfferEntity).where(qOfferEntity.isEmptyOffer
        .isTrue().and(qOfferEntity.submittent.submissionId.id.eq(submission.getId())))
        .fetch();
    List<SubmittentDTO> submittentDTOs = calculateSubmittentValues(submittents,
      submission.getSecondDeadline(), submission.getProcess(), SelectiveLevel.ZERO_LEVEL);
    retrieveFormalAuditSavedValues(submission.getProcess(), submission.getStatus(), submittentDTOs);
    return submittentDTOs;
  }

  @Override
  public List<SubmittentDTO> getSubmittentsForNegotiatedProcedure(List<String> submittentIds) {
    List<SubmittentEntity> submittentEntities = new JPAQueryFactory(em)
      .selectFrom(qSubmittentEntity).where(qSubmittentEntity.id.in(submittentIds)).fetch();
    Date deadline = submittentEntities.get(0).getSubmissionId().getSecondDeadline();

    return calculateSubmittentValues(submittentEntities, deadline,
      submittentEntities.get(0).getSubmissionId().getProcess(), SelectiveLevel.ZERO_LEVEL);
  }

  @Override
  public boolean submissionExists(String submissionId) {
    SubmissionEntity submissionEntity = em.find(SubmissionEntity.class, submissionId);
    return submissionEntity != null;
  }

  @Override
  public boolean isStatusChanged(String id, String status) {

    int submissionStatus = Integer.parseInt(getCurrentStatusOfSubmission(id));
    int currentStatus = Integer.parseInt(status);
    boolean result = false;

    if (currentStatus != submissionStatus
      && isStatusChangedByAnotherUser(currentStatus, submissionStatus,
      Integer.parseInt(TenderStatus.OFFER_OPENING_CLOSED.getValue()))) {
      result = true;
    }

    return result;
  }

  private boolean isStatusChangedByAnotherUser(int currentStatus, int submissionStatus,
    int specificStatus) {
    return
      // check if status is closed by another user
      (currentStatus < specificStatus && submissionStatus >= specificStatus)
        // check if status is reopened by another user
        || (currentStatus >= specificStatus && submissionStatus < specificStatus);
  }

  @Override
  public Set<ValidationError> updateCommissionProcurementDecision(
    CommissionProcurementDecisionDTO commissionProcurementDecisionDTO,
    String submissionId, Long submissionVersion) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateCommissionProcurementDecision, Parameters: "
        + "commissionProcurementDecisionDTO: {0}, submissionId: {1}, submissionVersion: {2}",
      new Object[]{commissionProcurementDecisionDTO, submissionId, submissionVersion});

    Set<ValidationError> optimisticLockErrors = new HashSet<>();
    SubmissionDTO submissionDTO = getSubmissionById(submissionId);
    // Check for optimistic locking errors
    if (!submissionVersion.equals(submissionDTO.getVersion())) {
      optimisticLockErrors
        .add(new ValidationError(ValidationMessages.OPTIMISTIC_LOCK_ERROR_FIELD,
          ValidationMessages.OPTIMISTIC_LOCK));
      return optimisticLockErrors;
    }
    try {
      // Updating values of submission.
      submissionDTO.setCommissionProcurementDecisionRecommendation(
        commissionProcurementDecisionDTO.getRecommendation());
      updateSubmission(submissionDTO);
      // Set submission status to commission procurement decision started if not already set.
      if (!getCurrentStatusOfSubmission(submissionId)
        .equals(TenderStatus.COMMISSION_PROCUREMENT_DECISION_STARTED.getValue())) {
        updateSubmissionStatus(submissionId,
          TenderStatus.COMMISSION_PROCUREMENT_DECISION_STARTED.getValue(), null, null,
          LookupValues.INTERNAL_LOG);
      }
    } catch (OptimisticLockException | RollbackException e) {
      optimisticLockErrors
        .add(new ValidationError(ValidationMessages.OPTIMISTIC_LOCK_ERROR_FIELD,
          ValidationMessages.OPTIMISTIC_LOCK));
    }
    return optimisticLockErrors;
  }

  @Override
  public void checkOptimisticLockSubmission(String submissionId, Long submissionVersion) {
    SubmissionEntity submissionEntity = em.find(SubmissionEntity.class, submissionId);
    if (!submissionVersion.equals(submissionEntity.getVersion())) {
      throw new OptimisticLockException();
    }
  }

  @Override
  public void submissionCreateSecurityCheck() {
    security.isPermittedOperationForUser(getUserId(),
      SecurityOperation.TENDER_CREATE.getValue(), null);
  }

  @Override
  public void submissionDocumentAreaSecurityCheck(String submissionId) {
    security.isPermittedOperationForUser(getUserId(),
      SecurityOperation.PROJECT_DOCUMENT_VIEW.getValue(), submissionId);
  }

  @Override
  public void formalAuditSecurityCheck(String submissionId) {
    security.isPermittedOperationForUser(getUserId(),
      submissionService.getSubmissionProcess(submissionId).getValue() + LookupValues.DOT +
        SecurityOperation.FORMAL_AUDIT_VIEW.getValue(), submissionId);
  }

  @Override
  public void suitabilityAuditSecurityCheck(String submissionId) {
    security.isPermittedOperationForUser(getUserId(),
      SecurityOperation.SUITABILITY_EXECUTE.getValue(), submissionId);
  }

  @Override
  public void applicantsSecurityCheck(String submissionId) {
    security.isPermittedOperationForUser(getUserId(),
      submissionService.getSubmissionProcess(submissionId).getValue() + LookupValues.DOT +
        SecurityOperation.APPLICANTS_VIEW.getValue(), submissionId);
  }

  @Override
  public void submissionViewSecurityCheck(String submissionId) {
    security.isPermittedOperationForUser(getUserId(),
      SecurityOperation.TENDER_VIEW.getValue(), submissionId);
  }

  @Override
  public SubmissionDTO getSubmissionByResourceId(String resourceID) {
    return SubmissionMapper.INSTANCE.toSubmissionDTO(
      new JPAQuery<>(em).select(qSubmissionEntity).from(qSubmissionEntity)
        .where(qSubmissionEntity.id.eq(resourceID).or(qSubmissionEntity.project.id.eq(resourceID))
          .or(qSubmissionEntity.project.tenant.id.eq(resourceID)))
        .fetchFirst());
  }
}
