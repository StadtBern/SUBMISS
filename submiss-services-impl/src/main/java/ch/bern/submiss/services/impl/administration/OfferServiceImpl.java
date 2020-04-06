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
import ch.bern.submiss.services.api.administration.OfferService;
import ch.bern.submiss.services.api.administration.ProcedureService;
import ch.bern.submiss.services.api.administration.SDProofService;
import ch.bern.submiss.services.api.administration.SDVatService;
import ch.bern.submiss.services.api.administration.SubmissionService;
import ch.bern.submiss.services.api.administration.TenderStatusHistoryService;
import ch.bern.submiss.services.api.administration.UserAdministrationService;
import ch.bern.submiss.services.api.constants.AuditEvent;
import ch.bern.submiss.services.api.constants.AuditGroupName;
import ch.bern.submiss.services.api.constants.AuditLevel;
import ch.bern.submiss.services.api.constants.AuditMessages;
import ch.bern.submiss.services.api.constants.CategorySD;
import ch.bern.submiss.services.api.constants.CommissionProcurementProposalReasonGiven;
import ch.bern.submiss.services.api.constants.CommissionProcurementProposalReservation;
import ch.bern.submiss.services.api.constants.ConstructionPermit;
import ch.bern.submiss.services.api.constants.DocumentAttributes;
import ch.bern.submiss.services.api.constants.LoanApproval;
import ch.bern.submiss.services.api.constants.Process;
import ch.bern.submiss.services.api.constants.Template;
import ch.bern.submiss.services.api.constants.TenderStatus;
import ch.bern.submiss.services.api.dto.CompanyDTO;
import ch.bern.submiss.services.api.dto.MasterListValueHistoryDTO;
import ch.bern.submiss.services.api.dto.OfferDTO;
import ch.bern.submiss.services.api.dto.ProcedureHistoryDTO;
import ch.bern.submiss.services.api.dto.SubmissionDTO;
import ch.bern.submiss.services.api.dto.SubmittentDTO;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.api.util.ValidationMessages;
import ch.bern.submiss.services.impl.mappers.OfferDTOMapper;
import ch.bern.submiss.services.impl.mappers.OfferDTOWithCriteriaMapper;
import ch.bern.submiss.services.impl.mappers.SubmissionMapper;
import ch.bern.submiss.services.impl.mappers.SubmittentDTOMapper;
import ch.bern.submiss.services.impl.model.CompanyEntity;
import ch.bern.submiss.services.impl.model.CriterionEntity;
import ch.bern.submiss.services.impl.model.MasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.OfferCriterionEntity;
import ch.bern.submiss.services.impl.model.OfferEntity;
import ch.bern.submiss.services.impl.model.OfferSubriterionEntity;
import ch.bern.submiss.services.impl.model.QCompanyEntity;
import ch.bern.submiss.services.impl.model.QCriterionEntity;
import ch.bern.submiss.services.impl.model.QFormalAuditEntity;
import ch.bern.submiss.services.impl.model.QMasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.QOfferCriterionEntity;
import ch.bern.submiss.services.impl.model.QOfferEntity;
import ch.bern.submiss.services.impl.model.QSubcriterionEntity;
import ch.bern.submiss.services.impl.model.QSubmittentEntity;
import ch.bern.submiss.services.impl.model.SubcriterionEntity;
import ch.bern.submiss.services.impl.model.SubmissionEntity;
import ch.bern.submiss.services.impl.model.SubmittentEntity;
import ch.bern.submiss.services.impl.util.ComparatorUtil;
import com.eurodyn.qlack2.fuse.cm.api.DocumentService;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

/**
 * The Class OfferServiceImpl.
 */
@Transactional
@OsgiServiceProvider(classes = {OfferService.class})
@Singleton
public class OfferServiceImpl extends BaseService implements OfferService {

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER = Logger.getLogger(OfferServiceImpl.class.getName());
  /**
   * The users service.
   */
  @Inject
  protected UserAdministrationService usersService;
  @Inject
  protected SDVatServiceImpl vatService;
  /**
   * The project bean.
   */
  @Inject
  ProjectBean projectBean;
  /**
   * The q offer entity.
   */
  QOfferEntity qOfferEntity = QOfferEntity.offerEntity;
  /**
   * The q submittent entity.
   */
  QSubmittentEntity qSubmittentEntity = QSubmittentEntity.submittentEntity;
  /**
   * The q company entity.
   */
  QCompanyEntity qCompanyEntity = QCompanyEntity.companyEntity;
  /**
   * The q offer criterion entity.
   */
  QOfferCriterionEntity qOfferCriterionEntity = QOfferCriterionEntity.offerCriterionEntity;
  /**
   * The q formal audit entity.
   */
  QFormalAuditEntity qFormalAuditEntity = QFormalAuditEntity.formalAuditEntity;
  QMasterListValueHistoryEntity qMasterListValueHistoryEntity =
    QMasterListValueHistoryEntity.masterListValueHistoryEntity;
  @Inject
  CacheBean cacheBean;
  /**
   * The submission service.
   */
  @Inject
  private SubmissionService submissionService;
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
   * The document service.
   */
  @Inject
  private DocumentService documentService;
  /**
   * The tender status history service.
   */
  @Inject
  private TenderStatusHistoryService tenderStatusHistoryService;

  /**
   * The SD vat service.
   */
  @Inject
  private SDVatService sDVatService;

  /**
   * The SD proof service.
   */
  @Inject
  private SDProofService sDProofService;

  /**
   * The audit.
   */
  @Inject
  private AuditBean audit;

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.OfferService#deleteSubmittent(java.lang.String)
   */
  @Override
  public void deleteSubmittent(String id) {

    LOGGER.log(Level.CONFIG,
      "Executing method deleteSubmittent, Parameters: id: {0}",
      id);

    SubmittentEntity submittentEntity = em.find(SubmittentEntity.class, id);
    SubmissionEntity submissionEntity = submittentEntity.getSubmissionId();
    List<OfferEntity> offerEntityList = new JPAQueryFactory(em).select(qOfferEntity)
      .from(qOfferEntity).where(qOfferEntity.submittent.id.eq(id)).fetch();
    for (OfferEntity offer : offerEntityList) {
      em.remove(offer);
    }

    // Create log entry if the submittentenliste has been examined (geprüft).
    if (!tenderStatusHistoryService
      .retrieveSubmissionSpecificStatuses(submittentEntity.getSubmissionId().getId(),
        TenderStatus.SUBMITTENTLIST_CHECKED.getValue())
      .isEmpty()) {

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

      auditLog(AuditEvent.REMOVE.name(), AuditMessages.SUBMITTENT_REMOVED.name(),
        submittentEntity.getSubmissionId().getId(), submissionVars.toString());
    }

    if (submissionEntity.getSubmittents().size() == 1) {
      submissionService.updateSubmissionStatus(submissionEntity.getId(),
        TenderStatus.SUBMISSION_CREATED.getValue(), null, null, LookupValues.INTERNAL_LOG);
      submissionService.resetEvaluationData(submissionEntity.getId());
      submissionService.resetDocumentData(submissionEntity.getId());
    }
    // Delete the submittent proof provided entries of the submittent company.
    sDProofService.deleteSubmittentProofProvidedEntries(
      SubmittentDTOMapper.INSTANCE.toSubmittentDTO(submittentEntity),
      submittentEntity.getCompanyId().getId(), true);
    em.remove(submittentEntity);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.OfferService#findIfSubmittentHasOffer(java.lang.
   * String)
   */
  public Boolean findIfSubmittentHasOffer(String id) {

    LOGGER.log(Level.CONFIG,
      "Executing method findIfSubmittentHasOffer, Parameters: id: {0}",
      id);

    List<OfferEntity> offerEntity = new JPAQueryFactory(em).select(qOfferEntity).from(qOfferEntity)
      .where(qOfferEntity.submittent.id.eq(id).and(qOfferEntity.isDefaultOffer.eq(Boolean.FALSE)))
      .fetch();
    return (!offerEntity.isEmpty());
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.OfferService#findIfSubmittentHasSubcontractors(java
   * .lang.String)
   */
  public Boolean findIfSubmittentHasSubcontractors(String id) {

    LOGGER.log(Level.CONFIG,
      "Executing method findIfSubmittentHasSubcontractors, Parameters: id: {0}",
      id);

    List<SubmittentEntity> subconstractorJointVentureEntityList =
      new JPAQueryFactory(em).select(qSubmittentEntity).from(qSubmittentEntity)
        .where(qSubmittentEntity.id.eq(id).and(qSubmittentEntity.subcontractors.isNotEmpty()
          .or(qSubmittentEntity.jointVentures.isNotEmpty())))
        .fetch();
    return (!subconstractorJointVentureEntityList.isEmpty());
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.OfferService#deleteOffer(java.lang.String)
   */
  @Override
  public String deleteOffer(String id) {

    LOGGER.log(Level.CONFIG,
      "Executing method deleteOffer, Parameters: id: {0}",
      id);

    OfferEntity offerEntity = em.find(OfferEntity.class, id);

    if (offerEntity != null) {
      SubmittentEntity submittentEntity = offerEntity.getSubmittent();
      if (!offerEntity.getIsDefaultOffer()) {
        if (offerEntity.getSubmittent().getIsApplicant() == null
          || !offerEntity.getSubmittent().getIsApplicant()) {
          // If the submittent is not an applicant, delete the offer completely.
          em.remove(offerEntity);
          offerEntity = projectBean.createDefaultOffer(submittentEntity);
        } else {
          // If the submittent is an applicant as well, delete specific values.
          offerEntity = projectBean.createDefaultOfferForApplicant(offerEntity);
        }
      }

      /** If all offers have the default values, set tender status to SUBMITTENT_LIST_CREATED_ID */
      long offers =
        new JPAQueryFactory(em).select(qOfferEntity).from(qOfferEntity)
          .where(qOfferEntity.submittent.submissionId
            .eq(offerEntity.getSubmittent().getSubmissionId())
            .and(qOfferEntity.isDefaultOffer.eq(Boolean.FALSE)))
          .fetchCount();
      if (offers == 0) {
        submissionService.updateSubmissionStatus(
          offerEntity.getSubmittent().getSubmissionId().getId(),
          TenderStatus.SUBMITTENT_LIST_CREATED.getValue(), null, null, LookupValues.INTERNAL_LOG);
      }
      return offerEntity.getId();
    }
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.OfferService#resetOffer(java.lang.String,
   * java.util.Date)
   */
  @Override
  public String resetOffer(String offerId, Date offerDate) {

    LOGGER.log(Level.CONFIG,
      "Executing method resetOffer, Parameters: offerId: {0}, offerDate: {1}",
      new Object[]{offerId, offerDate});

    OfferEntity offerEntity = em.find(OfferEntity.class, offerId);
    Date tempOfferDate = offerDate;
    SubmittentEntity submittentEntity = offerEntity.getSubmittent();
    if (submittentEntity != null) {
      if (submittentEntity.getIsApplicant() == null || !submittentEntity.getIsApplicant()) {
        em.remove(offerEntity);
        offerEntity = projectBean.createDefaultOffer(submittentEntity);
      } else {
        offerEntity = projectBean.createDefaultOfferForApplicant(offerEntity);
      }
      /** Reseting all default values except the Offer Date and the Empty Offer checkbox */
      offerEntity.setOfferDate(tempOfferDate);
      offerEntity.setIsEmptyOffer(Boolean.TRUE);
      offerEntity.setIsDefaultOffer(Boolean.FALSE);

      // Change the submission (tender) status to offer opening started, if no higher or equal
      // status has
      // been set.
      if (!compareCurrentVsSpecificStatus(
        TenderStatus.fromValue(submissionService
          .getCurrentStatusOfSubmission(submittentEntity.getSubmissionId().getId())),
        TenderStatus.OFFER_OPENING_STARTED)) {
        submissionService.updateSubmissionStatus(submittentEntity.getSubmissionId().getId(),
          TenderStatus.OFFER_OPENING_STARTED.getValue(), null, null, LookupValues.INTERNAL_LOG);
      }
      em.merge(offerEntity);
      return offerEntity.getId();
    }
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.OfferService#closeOffer(java.lang.String)
   */
  @Override
  public List<String> closeOffer(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method closeOffer, Parameters: submissionId: {0}",
      submissionId);

    List<String> messages = new ArrayList<>();
    SubmissionEntity submissionEntity = em.find(SubmissionEntity.class, submissionId);
    if ((submissionEntity.getProcess().equals(Process.INVITATION)
      || submissionEntity.getProcess().equals(Process.OPEN)
      || submissionEntity.getProcess().equals(Process.SELECTIVE))
      && (submissionEntity.getIsServiceTender() == null || !submissionEntity.getIsServiceTender())
      && requiredDocumentExists(submissionEntity.getId(), Template.OFFERRTOFFNUNGSPROTOKOLL)) {
      messages.add(ValidationMessages.MANDATORY_OFFER_PROTOCOL_DOCUMENT_MESSAGE);
    }
    if ((submissionEntity.getProcess().equals(Process.OPEN)
      || submissionEntity.getProcess().equals(Process.SELECTIVE))
      && (submissionEntity.getIsServiceTender() != null && submissionEntity.getIsServiceTender())
      && requiredDocumentExists(submissionEntity.getId(),
      Template.OFFERRTOFFNUNGSPROTOKOLL_DL_WW)) {
      messages.add(ValidationMessages.MANDATORY_OFFER_PROTOCOL_DL_DOCUMENT_MESSAGE);
    }
    if ((submissionEntity.getProcess().equals(Process.INVITATION)
      || submissionEntity.getProcess().equals(Process.NEGOTIATED_PROCEDURE)
      || submissionEntity.getProcess().equals(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION))
      && submissionService.hasSubmissionStatus(submissionId,
      TenderStatus.SUBMITTENTLIST_CHECKED.getValue())
      && requiredDocumentExists(submissionEntity.getId(), Template.SUBMITTENTENLISTE)) {
      messages.add(ValidationMessages.MANDATORY_SUBMITTENTLISTE_DOCUMENT_MESSAGE);
    }
    List<OfferDTO> offerDTOs = getOffersBySubmission(submissionId);
    for (OfferDTO offerDTO : offerDTOs) {
      // Check if there are offers without a date.
      if (offerDTO.getOfferDate() == null) {
        messages.add(ValidationMessages.OFFERS_NO_DATE_ERROR);
        break;
      }
    }
    if (messages.isEmpty()) {
      closeOfferStatus(submissionEntity);
    }
    return messages;
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.OfferService#getOfferById(java.lang.String)
   */
  @Override
  public OfferDTO getOfferById(String id) {

    LOGGER.log(Level.CONFIG,
      "Executing method getOfferById, Parameters: id: {0}",
      id);

    OfferEntity offer = em.find(OfferEntity.class, id);
    OfferDTO offerDTO = OfferDTOMapper.INSTANCE.toOfferDTO(offer);
    MasterListValueHistoryDTO settlementDTO =
      cacheBean.getValue(CategorySD.SETTLEMENT_TYPE.getValue(), offer.getSettlement().getId(),
        submissionService.getDateOfCompletedOrCanceledStatus(
          offerDTO.getSubmittent().getSubmissionId().getId()),
        submissionService.getDateOfCreationStatusOfSubmission(
          offerDTO.getSubmittent().getSubmissionId().getId()),
        null);
    offerDTO.setSettlement(settlementDTO);
    MasterListValueHistoryDTO currentMainVatRate = sDVatService.getCurrentMainVatRate();
    // If the offer has not been saved and during that time the main vat rate has changed, assign
    // the new main vat rate to all the required fields.
    if (offerDTO.getOfferDate() == null
        && !offerDTO.getVat().equals(Double.valueOf(currentMainVatRate.getValue2()))) {
      offerDTO.setVat(Double.valueOf(currentMainVatRate.getValue2()));
      offerDTO.setOperatingCostVat(Double.valueOf(currentMainVatRate.getValue2()));
      offerDTO.setAncilliaryAmountVat(currentMainVatRate.getValue2());
    }

    return offerDTO;
  }

  /**
   * Adding Subcontractor to Submittent (Tenderer).
   *
   * @param submittent the submittent
   */
  @Override
  public void addSubcontractorToSubmittent(SubmittentDTO submittent) {

    LOGGER.log(Level.CONFIG,
      "Executing method addSubcontractorToSubmittent, Parameters: submittent: {0}",
      submittent);

    SubmittentEntity submittentEntity = em.find(SubmittentEntity.class, submittent.getId());
    if (submittentEntity != null) {
      Set<CompanyEntity> companyEntities = new HashSet<>();
      for (CompanyDTO company : submittent.getSubcontractors()) {
        CompanyEntity companyEntity = em.find(CompanyEntity.class, company.getId());
        companyEntities.add(companyEntity);
      }
      if (submittentEntity.getSubcontractors() != null) {
        submittentEntity.getSubcontractors().addAll(companyEntities);
      }
      em.merge(submittentEntity);

      // Create log entry if the submittentenliste has been examined (geprüft) AND/OR Status
      // "Offertöffnung abgeschlossen" has been set
      if (!tenderStatusHistoryService
        .retrieveSubmissionSpecificStatuses(submittentEntity.getSubmissionId().getId(),
          TenderStatus.SUBMITTENTLIST_CHECKED.getValue())
        .isEmpty()
        || !tenderStatusHistoryService
        .retrieveSubmissionSpecificStatuses(submittentEntity.getSubmissionId().getId(),
          TenderStatus.OFFER_OPENING_CLOSED.getValue())
        .isEmpty()) {

        MasterListValueHistoryEntity workType = new JPAQueryFactory(em)
          .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
          .where(qMasterListValueHistoryEntity.masterListValueId
            .eq(submittentEntity.getSubmissionId().getWorkType())
            .and(qMasterListValueHistoryEntity.toDate.isNull()))
          .fetchOne();

        MasterListValueHistoryEntity objectEntity = new JPAQueryFactory(em)
          .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
          .where(qMasterListValueHistoryEntity.masterListValueId
            .eq(submittentEntity.getSubmissionId().getProject().getObjectName())
            .and(qMasterListValueHistoryEntity.toDate.isNull()))
          .fetchOne();

        String workTypeValue2 = (StringUtils.isEmpty(workType.getValue2())) ? StringUtils.EMPTY
          : " " + workType.getValue2();

        StringBuilder submissionVars =
          new StringBuilder(submittentEntity.getSubmissionId().getProject().getProjectName())
            .append("[#]")
            .append(objectEntity.getValue1()).append("[#]")
            .append(workType.getValue1() + workTypeValue2).append("[#]")
            .append(usersService.getUserById(getUser().getId()).getTenant().getId()).append("[#]");

        auditLog(AuditEvent.ADD.name(), AuditMessages.SUBCONTRACTOR_ADDED.name(),
          submittentEntity.getSubmissionId().getId(), submissionVars.toString());
      }
    }
  }

  /**
   * Deleting Subcontractor from Submittent (Tenderer).
   *
   * @param submittentId the submittent id
   * @param subcontractorId the subcontractor id
   */
  @Override
  public void deleteSubcontractor(String submittentId, String subcontractorId) {

    LOGGER.log(Level.CONFIG, "Executing method deleteSubcontractor, Parameters: "
        + "submittentId: {0}, subcontractorId: {1}",
      new Object[]{submittentId, subcontractorId});

    SubmittentEntity submittentEntity = em.find(SubmittentEntity.class, submittentId);
    CompanyEntity subcontractor = em.find(CompanyEntity.class, subcontractorId);
    submittentEntity.getSubcontractors().remove(subcontractor);
    // Delete the submittent proof provided entries of the subcontractor.
    sDProofService.deleteSubmittentProofProvidedEntries(
      SubmittentDTOMapper.INSTANCE.toSubmittentDTO(submittentEntity), subcontractorId, false);
    em.merge(submittentEntity);

    // Create log entry if the submittentenliste has been examined (geprüft) AND/OR Status
    // "Offertöffnung abgeschlossen" has been set
    if (!tenderStatusHistoryService
      .retrieveSubmissionSpecificStatuses(submittentEntity.getSubmissionId().getId(),
        TenderStatus.SUBMITTENTLIST_CHECKED.getValue())
      .isEmpty()
      || !tenderStatusHistoryService
      .retrieveSubmissionSpecificStatuses(submittentEntity.getSubmissionId().getId(),
        TenderStatus.OFFER_OPENING_CLOSED.getValue())
      .isEmpty()) {

      MasterListValueHistoryEntity workType = new JPAQueryFactory(em)
        .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
        .where(qMasterListValueHistoryEntity.masterListValueId
          .eq(submittentEntity.getSubmissionId().getWorkType())
          .and(qMasterListValueHistoryEntity.toDate.isNull()))
        .fetchOne();

      MasterListValueHistoryEntity objectEntity = new JPAQueryFactory(em)
        .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
        .where(qMasterListValueHistoryEntity.masterListValueId
          .eq(submittentEntity.getSubmissionId().getProject().getObjectName())
          .and(qMasterListValueHistoryEntity.toDate.isNull()))
        .fetchOne();

      String workTypeValue2 = (StringUtils.isEmpty(workType.getValue2())) ? StringUtils.EMPTY
        : " " + workType.getValue2();

      StringBuilder submissionVars =
        new StringBuilder(submittentEntity.getSubmissionId().getProject().getProjectName())
          .append("[#]")
          .append(objectEntity.getValue1()).append("[#]")
          .append(workType.getValue1() + workTypeValue2).append("[#]")
          .append(usersService.getUserById(getUser().getId()).getTenant().getId()).append("[#]");

      auditLog(AuditEvent.REMOVE.name(), AuditMessages.SUBCONTRACTOR_REMOVED.name(),
        submittentEntity.getSubmissionId().getId(), submissionVars.toString());
    }
  }

  /**
   * Adding JointVenture to Submittent (Tenderer).
   *
   * @param submittent the submittent
   */
  @Override
  public void addJointVentureToSubmittent(SubmittentDTO submittent) {

    LOGGER.log(Level.CONFIG,
      "Executing method addJointVentureToSubmittent, Parameters: submittent: {0}",
      submittent);

    SubmittentEntity submittentEntity = em.find(SubmittentEntity.class, submittent.getId());
    if (submittentEntity != null) {
      Set<CompanyEntity> companyEntities = new HashSet<>();
      for (CompanyDTO company : submittent.getJointVentures()) {
        CompanyEntity companyEntity = em.find(CompanyEntity.class, company.getId());
        companyEntities.add(companyEntity);
      }
      if (submittentEntity.getJointVentures() != null) {
        submittentEntity.getJointVentures().addAll(companyEntities);
      }
      em.merge(submittentEntity);

      // Create log entry if the submittentenliste has been examined (geprüft).
      if (!tenderStatusHistoryService
        .retrieveSubmissionSpecificStatuses(submittent.getSubmissionId().getId(),
          TenderStatus.SUBMITTENTLIST_CHECKED.getValue())
        .isEmpty()) {

        MasterListValueHistoryEntity workType = new JPAQueryFactory(em)
          .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
          .where(qMasterListValueHistoryEntity.masterListValueId
            .eq(submittentEntity.getSubmissionId().getWorkType())
            .and(qMasterListValueHistoryEntity.toDate.isNull()))
          .fetchOne();

        MasterListValueHistoryEntity objectEntity = new JPAQueryFactory(em)
          .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
          .where(qMasterListValueHistoryEntity.masterListValueId
            .eq(submittentEntity.getSubmissionId().getProject().getObjectName())
            .and(qMasterListValueHistoryEntity.toDate.isNull()))
          .fetchOne();

        String workTypeValue2 = (StringUtils.isEmpty(workType.getValue2())) ? StringUtils.EMPTY
          : " " + workType.getValue2();

        StringBuilder submissionVars =
          new StringBuilder(submittentEntity.getSubmissionId().getProject().getProjectName())
            .append("[#]")
            .append(objectEntity.getValue1()).append("[#]")
            .append(workType.getValue1() + workTypeValue2).append("[#]")
            .append(usersService.getUserById(getUser().getId()).getTenant().getId()).append("[#]");

        auditLog(AuditEvent.ADD.name(), AuditMessages.ARGE_ADDED.name(),
          submittent.getSubmissionId().getId(), submissionVars.toString());
      }
    }
  }

  /**
   * Deleting JointVenture from Submittent (Tenderer).
   *
   * @param submittentId the submittent id
   * @param jointVentureId the joint venture id
   */
  @Override
  public void deleteJointVenture(String submittentId, String jointVentureId) {

    LOGGER.log(Level.CONFIG,
      "Executing method deleteJointVenture, Parameters: submittentId: {0}, "
        + "jointVentureId: {1}",
      new Object[]{submittentId, jointVentureId});

    SubmittentEntity submittentEntity = em.find(SubmittentEntity.class, submittentId);
    CompanyEntity jointVenture = em.find(CompanyEntity.class, jointVentureId);
    submittentEntity.getJointVentures().remove(jointVenture);
    // Delete the submittent proof provided entries of the joint venture.
    sDProofService.deleteSubmittentProofProvidedEntries(
      SubmittentDTOMapper.INSTANCE.toSubmittentDTO(submittentEntity), jointVentureId, false);
    em.merge(submittentEntity);

    // Create log entry if the submittentenliste has been examined (geprüft).
    if (!tenderStatusHistoryService
      .retrieveSubmissionSpecificStatuses(submittentEntity.getSubmissionId().getId(),
        TenderStatus.SUBMITTENTLIST_CHECKED.getValue())
      .isEmpty()) {

      MasterListValueHistoryEntity workType = new JPAQueryFactory(em)
        .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
        .where(qMasterListValueHistoryEntity.masterListValueId
          .eq(submittentEntity.getSubmissionId().getWorkType())
          .and(qMasterListValueHistoryEntity.toDate.isNull()))
        .fetchOne();

      MasterListValueHistoryEntity objectEntity = new JPAQueryFactory(em)
        .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
        .where(qMasterListValueHistoryEntity.masterListValueId
          .eq(submittentEntity.getSubmissionId().getProject().getObjectName())
          .and(qMasterListValueHistoryEntity.toDate.isNull()))
        .fetchOne();

      String workTypeValue2 = (StringUtils.isEmpty(workType.getValue2())) ? StringUtils.EMPTY
        : " " + workType.getValue2();

      StringBuilder submissionVars =
        new StringBuilder(submittentEntity.getSubmissionId().getProject().getProjectName())
          .append("[#]")
          .append(objectEntity.getValue1()).append("[#]")
          .append(workType.getValue1() + workTypeValue2).append("[#]")
          .append(usersService.getUserById(getUser().getId()).getTenant().getId()).append("[#]");

      auditLog(AuditEvent.REMOVE.name(), AuditMessages.ARGE_REMOVED.name(),
        submittentEntity.getSubmissionId().getId(), submissionVars.toString());
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.OfferService#updateOffer(ch.bern.submiss.services.
   * api.dto.OfferDTO)
   */
  @Override
  public void updateOffer(OfferDTO offerDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateOffer, Parameters: offerDTO: {0}",
      offerDTO);

    OfferEntity offerEntity = em.find(OfferEntity.class, offerDTO.getId());
    if (offerEntity != null) {
      SubmissionEntity submissionEntity =
        em.find(SubmissionEntity.class, offerEntity.getSubmittent().getSubmissionId().getId());
      // Change the submission (tender) status to offer opening started, if no higher or equal
      // status has
      // been set.
      if (!compareCurrentVsSpecificStatus(
        TenderStatus
          .fromValue(submissionService.getCurrentStatusOfSubmission(submissionEntity.getId())),
        TenderStatus.OFFER_OPENING_STARTED)) {
        submissionService.updateSubmissionStatus(submissionEntity.getId(),
          TenderStatus.OFFER_OPENING_STARTED.getValue(), null, null, LookupValues.INTERNAL_LOG);
      }
      offerEntity = OfferDTOWithCriteriaMapper.INSTANCE.toOffer(offerDTO);
      offerEntity.setFromMigration(Boolean.FALSE);
      offerEntity.setIsDefaultOffer(Boolean.FALSE);
      em.merge(offerEntity);
      // Update the award evaluation page.
      submissionService.updateAwardEvaluationPage(submissionEntity.getId());
      int offerCriteria = new JPAQueryFactory(em).selectFrom(qOfferCriterionEntity)
        .where(qOfferCriterionEntity.offer.id.eq(offerEntity.getId())).fetch().size();
      // Only create offer criteria and offer sub-criteria for offer, if it doesn't already have
      // offer criteria.
      if (offerCriteria == 0) {
        createOfferCriteriaAndOfferSubcriteria(offerEntity);
      }
    }
  }

  /**
   * This method create empty OfferCriterion and OfferSubcriterion when an offer is added to
   * submission.
   *
   * @param offer the offer
   */
  private void createOfferCriteriaAndOfferSubcriteria(OfferEntity offer) {

    LOGGER.log(Level.CONFIG,
      "Executing method createOfferCriteriaAndOfferSubcriteria, Parameters: offer: {0}",
      offer);

    /*
     * This functionality added in order to update OfferSubcriterion and OfferCriterion when a
     * submittent is added after offer reopen.
     */
    QCriterionEntity qCriterionEntity = QCriterionEntity.criterionEntity;
    QSubcriterionEntity qSubriterionEntity = QSubcriterionEntity.subcriterionEntity;
    List<CriterionEntity> criterionEntities =
      new JPAQueryFactory(em).select(qCriterionEntity).from(qCriterionEntity)
        .where(qCriterionEntity.submission.eq(offer.getSubmittent().getSubmissionId())).fetch();
    // Create empty OfferCriterionEntities
    if (!criterionEntities.isEmpty()) {
      for (CriterionEntity criterion : criterionEntities) {
        OfferCriterionEntity offerCriterionEntity = new OfferCriterionEntity();
        offerCriterionEntity.setOffer(offer);
        offerCriterionEntity.setCriterion(criterion);
        em.persist(offerCriterionEntity);
      }
    }
    List<SubcriterionEntity> subcriterionEntities =
      new JPAQueryFactory(em).select(qSubriterionEntity).from(qSubriterionEntity)
        .where(
          qSubriterionEntity.criterion.submission.eq(offer.getSubmittent().getSubmissionId()))
        .fetch();
    if (!subcriterionEntities.isEmpty()) {
      for (SubcriterionEntity subcriterion : subcriterionEntities) {
        OfferSubriterionEntity offerCriterionEntity = new OfferSubriterionEntity();
        offerCriterionEntity.setOffer(offer);
        offerCriterionEntity.setSubriterion(subcriterion);
        em.persist(offerCriterionEntity);
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.OfferService#readActiveSubmittentsBySubmission(java
   * .lang.String)
   */
  @Override
  public List<SubmittentDTO> readActiveSubmittentsBySubmission(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method readActiveSubmittentsBySubmission, Parameters: submissionId: {0}",
      submissionId);

    return SubmittentDTOMapper.INSTANCE
      .toSubmittentDTO((new JPAQueryFactory(em).select(qSubmittentEntity).from(qSubmittentEntity)
        .where(qSubmittentEntity.submissionId.id.eq(submissionId)
          .and(qSubmittentEntity.existsExclusionReasons.eq(Boolean.FALSE)
            .or(qSubmittentEntity.existsExclusionReasons.isNull())))
        .fetch()));

  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.OfferService#readAwardSubmittentNames(java.lang.
   * String)
   */
  @Override
  public List<String> readAwardSubmittentNames(String value) {

    LOGGER.log(Level.CONFIG, "Executing method readAwardSubmittentNames, Parameters: value: {0}",
        value);

    StringPath path = qOfferEntity.submittent.companyId.companyName;
    return new JPAQuery<>(em).select(path).distinct().from(qOfferEntity)
        .where(qOfferEntity.isAwarded.isTrue()
            .and(qOfferEntity.submittent.companyId.companyName.like("%" + value + "%")))
        .fetch();
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.OfferService#updateOfferAwards(java.util.List,
   * java.lang.String)
   */
  @Override
  public void updateOfferAwards(List<String> checkedOffersIds, String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateOfferAwards, Parameters: submissionId: {0}",
      submissionId);

    // Get offers by their offer ids.
    List<OfferEntity> offerEntities = new JPAQueryFactory(em).selectFrom(qOfferEntity)
      .where(qOfferEntity.id.in(checkedOffersIds)).fetch();
    // Set above threshold on submission.
    updateAndGetThreshold(submissionId, offerEntities);
    for (OfferEntity offer : offerEntities) {
      /** update offer to take the award */
      offer.setIsAwarded(true);
      em.merge(offer);
    }
    // Initialize the commission procurement proposal values.
    initializeCommissionProcurementProposalValues(submissionId);
    // Use submission id from offers to update submission status.
    submissionService.updateSubmissionStatus(submissionId,
      TenderStatus.MANUAL_AWARD_COMPLETED.getValue(), AuditMessages.MANUAL_AWARD_COMPLETED.name(),
      null, LookupValues.EXTERNAL_LOG);
  }

  /**
   * Function to close the award evaluation.
   *
   * @param awardedOfferIds the awarded offer ids
   * @param submissionId the submission id
   */
  @Override
  public void closeAwardEvaluation(List<String> awardedOfferIds, String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method closeAwardEvaluation, Parameters:  awardedOfferIds: {0}, "
        + "submissionId: {1}",
      new Object[]{awardedOfferIds, submissionId});

    // Give awards to offers.
    for (String id : awardedOfferIds) {
      OfferEntity offer = em.find(OfferEntity.class, id);
      offer.setIsAwarded(true);
      em.merge(offer);
    }
    // Initialize the commission procurement proposal values.
    initializeCommissionProcurementProposalValues(submissionId);
    // Update submission status.
    submissionService.updateSubmissionStatus(submissionId,
      TenderStatus.AWARD_EVALUATION_CLOSED.getValue(),
      AuditMessages.AWARD_EVALUATION_CLOSE.name(), null, LookupValues.EXTERNAL_LOG);
  }

  /**
   * Initialize submission values for the commission procurement proposal.
   *
   * @param submissionId the submission id
   */
  private void initializeCommissionProcurementProposalValues(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method initializeCommissionProcurementProposalValues, Parameters: "
        + "submissionId: {0}",
      submissionId);

    if (submissionId != null) {
      SubmissionDTO submissionDTO = submissionService.getSubmissionById(submissionId);
      // Initialize submission values for commission procurement proposal where required.
      // Initializing the commission procurement proposal reservation value.
      if (submissionDTO.getConstructionPermit().equals(ConstructionPermit.PENDING)
        && !submissionDTO.getLoanApproval().equals(LoanApproval.PENDING)) {
        submissionDTO.setCommissionProcurementProposalReservation(
          CommissionProcurementProposalReservation.RESERVATION_CONSTRUCTION.getValue());
      } else if (!submissionDTO.getConstructionPermit().equals(ConstructionPermit.PENDING)
        && submissionDTO.getLoanApproval().equals(LoanApproval.PENDING)) {
        submissionDTO.setCommissionProcurementProposalReservation(
          CommissionProcurementProposalReservation.RESERVATION_LOAN.getValue());
      } else if (submissionDTO.getConstructionPermit().equals(ConstructionPermit.PENDING)
        && submissionDTO.getLoanApproval().equals(LoanApproval.PENDING)) {
        submissionDTO.setCommissionProcurementProposalReservation(
          CommissionProcurementProposalReservation.RESERVATION_CONSTRUCTION_AND_LOAN.getValue());
      } else {
        submissionDTO.setCommissionProcurementProposalReservation(
          CommissionProcurementProposalReservation.RESERVATION_NONE.getValue());
      }
      if (submissionDTO.getAboveThreshold() != null
        && (submissionDTO.getProcess().equals(Process.NEGOTIATED_PROCEDURE)
        || submissionDTO.getProcess().equals(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION))
        && submissionDTO.getAboveThreshold()) {
        // Initializing the commission procurement proposal pre-remarks value.
        submissionDTO.setCommissionProcurementProposalPreRemarks(
          submissionDTO.getReasonFreeAward().getValue2());
      }
      // Initializing reason given value of submission.
      if (submissionDTO.getIsServiceTender() != null && submissionDTO.getIsServiceTender()) {
        submissionDTO.setCommissionProcurementProposalReasonGiven(
          CommissionProcurementProposalReasonGiven.REASON_GIVEN_DL.getValue());
      } else {
        submissionDTO.setCommissionProcurementProposalReasonGiven(
          CommissionProcurementProposalReasonGiven.REASON_GIVEN_NOT_DL.getValue());
      }
      // Initializing the suitability audit text value.
      String suitabilityAuditText = "";
      List<OfferDTO> offerDTOs = getOffersBySubmission(submissionId);
      if (submissionDTO.getProcess().equals(Process.SELECTIVE)) {
        // In case of selective process.
        for (OfferDTO offerDTO : offerDTOs) {
          if (offerDTO.getSubmittent().getExistsExclusionReasons() != null
            && offerDTO.getSubmittent().getExistsExclusionReasons()) {
            suitabilityAuditText = setSuitabiltityAuditText(offerDTO, suitabilityAuditText);
          }
        }

      } else {
        // In case of any other process.
        for (OfferDTO offerDTO : offerDTOs) {
          if (offerDTO.getqExExaminationIsFulfilled() != null
            && !offerDTO.getqExExaminationIsFulfilled()) {
            suitabilityAuditText = setSuitabiltityAuditText(offerDTO, suitabilityAuditText);
          }
        }
      }
      submissionDTO.setCommissionProcurementProposalSuitabilityAuditText(suitabilityAuditText);
      SubmissionEntity submissionEntity = SubmissionMapper.INSTANCE.toSubmission(submissionDTO);
      em.merge(submissionEntity);
    }
  }

  /**
   * Sets the suitabiltity audit text.
   *
   * @param offerDTO the offer DTO
   * @param oldText the old text
   * @return the string
   */
  private String setSuitabiltityAuditText(OfferDTO offerDTO, String oldText) {

    LOGGER.log(Level.CONFIG,
      "Executing method setSuitabiltityAuditText, Parameters: offerDTO: {0}, "
        + "oldText: {1}",
      new Object[]{offerDTO, oldText});
    StringBuilder bld = new StringBuilder();
    bld.append(oldText);
    if (bld.toString().isEmpty()) {
      bld.append(offerDTO.getSubmittent().getSubmittentNameArgeSub() + " (");
    } else {
      bld.append("; " + offerDTO.getSubmittent().getSubmittentNameArgeSub() + " (");
    }
    if (offerDTO.getSubmittent().getExistsExclusionReasons()) {
      bld.append("Ausschlussgründe Art. 24 ÖBV");
    }
    if (!offerDTO.getSubmittent().getFormalExaminationFulfilled()) {
      if (offerDTO.getSubmittent().getExistsExclusionReasons()) {
        bld.append(" / Nachweise nicht erbracht");
      } else {
        bld.append("Nachweise nicht erbracht");
      }
    }
    if (criterionService.offerMustCriterionNotFulfilled(offerDTO.getId())) {
      if (offerDTO.getSubmittent().getExistsExclusionReasons()
        || !offerDTO.getSubmittent().getFormalExaminationFulfilled()) {
        bld.append(" / MUSS-Kriterien nicht erfüllt");
      } else {
        bld.append("MUSS-Kriterien nicht erfüllt");
      }
    }
    bld.append(")");
    return bld.toString();
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.OfferService#getOffersBySubmission(java.lang.
   * String)
   */
  @Override
  public List<OfferDTO> getOffersBySubmission(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getOffersBySubmission, Parameters: submissionId: {0}",
      submissionId);

    List<OfferEntity> offerEntityList =
      new JPAQueryFactory(em).select(qOfferEntity).from(qOfferEntity)
        .where(qOfferEntity.submittent.submissionId.id.eq(submissionId)
          .and((qOfferEntity.submittent.isApplicant.eq(Boolean.TRUE)
            .and(qOfferEntity.excludedFirstLevel.eq(Boolean.FALSE)))
            .or(qOfferEntity.submittent.isApplicant.eq(Boolean.FALSE))
            .or(qOfferEntity.submittent.isApplicant.isNull())))
        .fetch();
    return OfferDTOMapper.INSTANCE.toOfferDTO(offerEntityList);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.OfferService#getApplicationsBySubmission(java.lang.
   * String)
   */
  @Override
  public List<OfferDTO> getApplicationsBySubmission(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getApplicationsBySubmission, Parameters: submissionId: {0}",
      submissionId);

    List<OfferEntity> offerEntityList =
      new JPAQueryFactory(em)
        .select(qOfferEntity).from(qOfferEntity).where(qOfferEntity.submittent.submissionId.id
        .eq(submissionId).and(qOfferEntity.submittent.isApplicant.eq(Boolean.TRUE)))
        .fetch();
    return OfferDTOMapper.INSTANCE.toOfferDTO(offerEntityList);
  }

  @Override
  public void updateAwardRecipients(List<OfferDTO> offerDTOs) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateAwardRecipients, Parameters: offerDTOs: {0}",
      offerDTOs);
    List<OfferEntity> offerEntities = OfferDTOMapper.INSTANCE.toOffer(offerDTOs);
    for (OfferEntity offerEntity : offerEntities) {
      em.merge(offerEntity);
    }
  }

  /**
   * Update and get threshold.
   *
   * @param submissionId the submission id
   * @param offerEntities the offer entities
   */
  private void updateAndGetThreshold(String submissionId, List<OfferEntity> offerEntities) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateAndGetThreshold, Parameters: submissionId: {0}, "
        + "offerEntities: {1}",
      new Object[]{submissionId, offerEntities});

    ProcedureHistoryDTO thresholdEntity = null;
    SubmissionEntity submissionEntity = em.find(SubmissionEntity.class, submissionId);
    /**
     * Get a procedure history entity so to compare the value with the max Offer afterwards. Process
     * must be the next step of Freihandiges verfarhen(process: Negotiated Procedure)
     */
    thresholdEntity = procedureService.readProcedure(submissionEntity.getProcessType().getId(),
      Process.INVITATION, submissionEntity.getProject().getTenant().getId());

    for (OfferEntity offerEntity : offerEntities) {
      if (thresholdEntity != null) {
        /** Convert String to BigDecimal */
        BigDecimal tresholdValue = thresholdEntity.getValue();
        /** if the offer amount value is greater than the value of threshold then set it to true */
        if (offerEntity.getAmount() != null
          && (offerEntity.getAmount().compareTo(tresholdValue)) == 1
          && ((submissionEntity.getProcess() == Process.NEGOTIATED_PROCEDURE) || submissionEntity
          .getProcess() == Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION)) {
          submissionEntity.setAboveThreshold(true);
          //Bug 13233 - Freihändiges Verfahren oberhalb Schwellenwert und GeKo
          if (!submissionEntity.getIsGekoEntry()) {
            submissionEntity.setIsGekoEntry(true);
            submissionEntity.setIsGekoEntryByManualAward(true);
          }
        }
      }
    }
  }

  /**
   * Create offer criteria and offer sub-criteria for default offer.
   *
   * @param offerDTO the offer DTO
   */
  @Override
  public void createOfferCriteriaAndSubcriteriaForDefaultOffer(OfferDTO offerDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method createOfferCriteriaAndSubcriteriaForDefaultOffer, Parameters: "
        + "offerDTO: {0}",
      offerDTO);

    if (offerDTO != null) {
      offerDTO.setCreatedBy(getUserId());
      offerDTO.setCreatedOn(new Timestamp(new Date().getTime()));
      offerDTO.getSubmittent().setCreatedBy(getUserId());
      offerDTO.getSubmittent().setCreatedOn(new Timestamp(new Date().getTime()));
      createOfferCriteriaAndOfferSubcriteria(OfferDTOMapper.INSTANCE.toOffer(offerDTO));
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.OfferService#requiredDocumentExists(java.lang.
   * String, java.lang.String)
   */
  @Override
  public boolean requiredDocumentExists(String submissionId, String templateType) {

    LOGGER.log(Level.CONFIG, "Executing method requiredDocumentExists, Parameters: "
        + "submissionId: {0}, templateType: {1}",
      new Object[]{submissionId, templateType});

    QMasterListValueHistoryEntity qMasterListValueHistoryEntity =
      QMasterListValueHistoryEntity.masterListValueHistoryEntity;
    HashMap<String, String> attributesMap = new HashMap<>();
    MasterListValueHistoryEntity templateId =
      new JPAQueryFactory(em).select(qMasterListValueHistoryEntity)
        .from(qMasterListValueHistoryEntity)
        .where(qMasterListValueHistoryEntity.shortCode.eq(templateType)
          .and(qMasterListValueHistoryEntity.tenant.id
            .eq(usersService.getUserById(getUser().getId()).getTenant().getId())))
        .fetchOne();
    attributesMap.put(DocumentAttributes.TEMPLATE_ID.name(),
      templateId.getMasterListValueId().getId());
    return documentService.getNodeByAttributes(submissionId, attributesMap).isEmpty();
  }

  /**
   * This method is used to close the offer period of the submission and to update the order of the
   * submitted offers.
   *
   * @param submissionEntity the submission entity
   */
  private void closeOfferStatus(SubmissionEntity submissionEntity) {

    LOGGER.log(Level.CONFIG,
      "Executing method closeOfferStatus, Parameters: submissionEntity: {0}",
      submissionEntity);

    // Set minGrade and maxGrade values if the offer opening is closed for the first time.
    if (!submissionService.hasOfferOpeningBeenClosedBefore(submissionEntity.getId())) {
      submissionEntity.setMinGrade(BigDecimal.valueOf(0));
      submissionEntity.setMaxGrade(BigDecimal.valueOf(5));
      em.merge(submissionEntity);
    }
    submissionService.updateSubmissionStatus(submissionEntity.getId(),
      TenderStatus.OFFER_OPENING_CLOSED.getValue(), AuditMessages.CLOSE_OFFER.name(), null,
      LookupValues.EXTERNAL_LOG);

    /* Update offer sort order. */
    if (!submissionEntity.getProcess().equals(Process.SELECTIVE)) {
      Integer sortOrder = 1;
      List<OfferEntity> offerEntities =
        new JPAQueryFactory(em).select(qOfferEntity).from(qOfferEntity)
          .where(qOfferEntity.submittent.submissionId.id.eq(submissionEntity.getId())).fetch();
      Collections.sort(offerEntities, ComparatorUtil.sortOfferEntities);
      for (OfferEntity o : offerEntities) {
        o.getSubmittent().setSortOrder(sortOrder);
        em.merge(o);
        sortOrder++;
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.OfferService#updateApplication(java.lang.String,
   * java.util.Date, java.lang.String)
   */
  public void updateApplication(String applicationId, Date applicationDate,
    String applicationInformation) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateApplication, Parameters: applicationId: {0}, "
        + "applicationDate: {1}, applicationInformation: {2}",
      new Object[]{applicationId, applicationDate, applicationInformation});

    // Application and offer are the same thing, but occur at different statuses.
    OfferEntity offerEntity = em.find(OfferEntity.class, applicationId);
    if (offerEntity != null) {
      SubmissionEntity submissionEntity =
        em.find(SubmissionEntity.class, offerEntity.getSubmittent().getSubmissionId().getId());
      // Change the submission (tender) status to application opening started, if no higher or equal
      // status has been set.
      if (!compareCurrentVsSpecificStatus(
        TenderStatus
          .fromValue(submissionService.getCurrentStatusOfSubmission(submissionEntity.getId())),
        TenderStatus.APPLICATION_OPENING_STARTED)) {
        submissionService.updateSubmissionStatus(submissionEntity.getId(),
          TenderStatus.APPLICATION_OPENING_STARTED.getValue(), null, null,
          LookupValues.INTERNAL_LOG);
      }
      offerEntity.setApplicationDate(applicationDate);
      offerEntity.setApplicationInformation(applicationInformation);
      em.merge(offerEntity);
      int offerCriteria = new JPAQueryFactory(em).selectFrom(qOfferCriterionEntity)
        .where(qOfferCriterionEntity.offer.id.eq(offerEntity.getId())).fetch().size();
      // Only create offer criteria and offer sub-criteria for applicant, if they don't already have
      // offer criteria.
      if (offerCriteria == 0) {
        createOfferCriteriaAndOfferSubcriteria(offerEntity);
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.OfferService#findIfApplicantHasApplication(java.
   * lang.String)
   */
  public Boolean findIfApplicantHasApplication(String applicantId) {

    LOGGER.log(Level.CONFIG,
      "Executing method findIfApplicantHasApplication, Parameters: applicantId: {0}",
      applicantId);

    // Application and offer are the same thing, but occur at different statuses.
    List<OfferEntity> offerEntity = new JPAQueryFactory(em).select(qOfferEntity).from(qOfferEntity)
      .where(qOfferEntity.submittent.id.eq(applicantId)
        .and(qOfferEntity.applicationDate.isNotNull()))
      .fetch();
    return (!offerEntity.isEmpty());
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.OfferService#deleteApplication(java.lang.String)
   */
  @Override
  public String deleteApplication(String applicationId) {

    LOGGER.log(Level.CONFIG,
      "Executing method deleteApplication, Parameters: applicationId: {0}",
      applicationId);

    // Application and offer are the same thing, but occur at different statuses.
    OfferEntity applicationEntity = em.find(OfferEntity.class, applicationId);
    if (applicationEntity != null) {
      SubmittentEntity submittentEntity = applicationEntity.getSubmittent();
      if (applicationEntity.getApplicationDate() != null) {
        // Creating default application.
        applicationEntity.setFromMigration(Boolean.FALSE);
        applicationEntity.setApplicationDate(null);
        applicationEntity.setApplicationInformation(null);
        applicationEntity.setCreatedBy(submittentEntity.getCreatedBy());
        applicationEntity.setCreatedOn(submittentEntity.getCreatedOn());
        applicationEntity = projectBean.createDefaultOfferForApplicant(applicationEntity);
        em.merge(applicationEntity);
      }
      // If all applications have the default values (application date as null), set tender status
      // to APPLICANTS_LIST_CREATED.
      long applicationsWithDate = new JPAQueryFactory(em).select(qOfferEntity).from(qOfferEntity)
        .where(qOfferEntity.submittent.submissionId
          .eq(applicationEntity.getSubmittent().getSubmissionId())
          .and(qOfferEntity.applicationDate.isNotNull()))
        .fetchCount();
      if (applicationsWithDate == 0) {
        submissionService.updateSubmissionStatus(
          applicationEntity.getSubmittent().getSubmissionId().getId(),
          TenderStatus.APPLICANTS_LIST_CREATED.getValue(), null, null, LookupValues.INTERNAL_LOG);
      }
      return applicationEntity.getId();
    }
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.OfferService#calculateOfferAmount(ch.bern.submiss.
   * services.api.dto.OfferDTO)
   */
  @Override
  public BigDecimal calculateOfferAmount(OfferDTO offerDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method calculateOfferAmount, Parameters: offerDTO: {0}",
      offerDTO);

    Double grossAmount = offerDTO.getGrossAmount();
    Double discount = offerDTO.getDiscount();
    Double buildingCosts = offerDTO.getBuildingCosts();
    Double discount2 = offerDTO.getDiscount2();
    if (offerDTO.getGrossAmount() == null && offerDTO.getGrossAmountCorrected() == null) {
      return null;
    }
    if (offerDTO.getIsCorrected()) {
      grossAmount = offerDTO.getGrossAmountCorrected();
    }
    if (offerDTO.getIsDiscountPercentage()) {
      discount = (grossAmount * offerDTO.getDiscount()) / 100;
    }
    if (offerDTO.getIsBuildingCostsPercentage()) {
      buildingCosts = ((grossAmount - discount) * offerDTO.getBuildingCosts()) / 100;
    }
    if (offerDTO.getIsDiscount2Percentage()) {
      discount2 = ((grossAmount - (discount + buildingCosts)) * discount2) / 100;
    }
    return BigDecimal
      .valueOf(customRoundNumber(grossAmount - discount - buildingCosts - discount2));
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.OfferService#calculateOperatingCostsAmount(ch.bern.
   * submiss.services.api.dto.OfferDTO)
   */
  @Override
  public BigDecimal calculateOperatingCostsAmount(OfferDTO offerDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method calculateOperatingCostsAmount, Parameters: offerDTO: {0}",
      offerDTO);

    Double operatingCostGross = offerDTO.getOperatingCostGross();
    Double operatingCostDiscount = offerDTO.getOperatingCostDiscount();
    Double operatingCostDiscount2 = offerDTO.getOperatingCostDiscount2();
    if (offerDTO.getOperatingCostGross() == null
      && offerDTO.getOperatingCostGrossCorrected() == null) {
      return null;
    }
    if (offerDTO.getIsOperatingCostCorrected()) {
      operatingCostGross = offerDTO.getOperatingCostGrossCorrected();
    }
    if (offerDTO.getIsOperatingCostDiscountPercentage()) {
      operatingCostDiscount = (operatingCostGross * offerDTO.getOperatingCostDiscount()) / 100;
    }
    if (offerDTO.getIsOperatingCostDiscount2Percentage()) {
      operatingCostDiscount2 =
        ((operatingCostGross - operatingCostDiscount) * offerDTO.getOperatingCostDiscount2())
          / 100;
    }
    return BigDecimal.valueOf(
      customRoundNumber(operatingCostGross - operatingCostDiscount - operatingCostDiscount2));
  }

  /**
   * Function to implement the swiss number rounding standard ("Rappenrundung").
   *
   * @param num the num
   * @return the double
   */
  private double customRoundNumber(Double num) {

    LOGGER.log(Level.CONFIG, "Executing method customRoundNumber, Parameters: num: {0}",
      num);

    if (num == null || num == 0) {
      return 0.00;
    }
    double tenNum = num * 10;
    double numWithOneDecimal = Double.valueOf((long) tenNum) / 10;
    double diff = num - numWithOneDecimal;
    if (diff < 0.025) {
      return numWithOneDecimal;
    } else if (diff >= 0.025 && diff < 0.075) {
      return numWithOneDecimal + 0.05;
    } else {
      return numWithOneDecimal + 0.1;
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.OfferService#retrieveNotExcludedOffers(java.lang.
   * String)
   */
  @Override
  public List<OfferDTO> retrieveNotExcludedOffers(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method retrieveNotExcludedOffers, Parameters: submissionId: {0}",
      submissionId);

    return OfferDTOWithCriteriaMapper.INSTANCE
      .toOfferDTO(new JPAQueryFactory(em).selectFrom(qOfferEntity)
        .where((qOfferEntity.isExcludedFromProcess.isFalse()
          .or(qOfferEntity.isExcludedFromProcess.isNull()))
          .and(qOfferEntity.submittent.submissionId.id.eq(submissionId)))
        .fetch());
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.OfferService#retrieveExcludedOffers(java.lang.
   * String)
   */
  @Override
  public List<OfferDTO> retrieveExcludedOffers(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method retrieveExcludedOffers, Parameters: submissionId: {0}",
      submissionId);

    return OfferDTOWithCriteriaMapper.INSTANCE.toOfferDTO(new JPAQueryFactory(em)
      .selectFrom(qOfferEntity).where((qOfferEntity.isExcludedFromProcess.isTrue())
        .and(qOfferEntity.submittent.submissionId.id.eq(submissionId)))
      .fetch());
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.OfferService#getSubmissionOffersWithCriteria(java.
   * lang.String)
   */
  @Override
  public List<OfferDTO> getSubmissionOffersWithCriteria(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getSubmissionOffersWithCriteria, Parameters: submissionId: {0}",
      submissionId);

    return OfferDTOWithCriteriaMapper.INSTANCE
      .toOfferDTO(new JPAQueryFactory(em).select(qOfferEntity).from(qOfferEntity)
        .where(qOfferEntity.submittent.submissionId.id.eq(submissionId)
          .and((qOfferEntity.isEmptyOffer.isNull().or(qOfferEntity.isEmptyOffer.isFalse())))
          .and((qOfferEntity.submittent.isApplicant.eq(Boolean.TRUE)
            .and(qOfferEntity.excludedFirstLevel.eq(Boolean.FALSE)))
            .or(qOfferEntity.submittent.isApplicant.eq(Boolean.FALSE))
            .or(qOfferEntity.submittent.isApplicant.isNull())))
        .fetch());
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.OfferService#getOffersByOfferIds(java.util.List)
   */
  @Override
  public List<OfferDTO> getOffersByOfferIds(List<String> offerIds) {

    LOGGER.log(Level.CONFIG, "Executing method getOffersByOfferIds, Parameters: "
        + "offerIds: {0}",
      offerIds);

    List<OfferEntity> offerEntities = new JPAQueryFactory(em).selectFrom(qOfferEntity)
      .where(qOfferEntity.id.in(offerIds)).fetch();
    return OfferDTOMapper.INSTANCE.toOfferDTO(offerEntities);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.OfferService#retrieveExcludedApplicants(java.lang.
   * String)
   */
  @Override
  public List<OfferDTO> retrieveExcludedApplicants(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method retrieveExcludedApplicants, Parameters: submissionId: {0}",
      submissionId);

    return OfferDTOWithCriteriaMapper.INSTANCE.toOfferDTO(new JPAQueryFactory(em)
      .selectFrom(qOfferEntity)
      .where(
        (qOfferEntity.excludedFirstLevel.isFalse().or(qOfferEntity.excludedFirstLevel.isNull()))
          .and(qOfferEntity.submittent.submissionId.id.eq(submissionId)))
      .fetch());
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.OfferService#retrieveNotExcludedSelectiveOffers(
   * java.lang.String)
   */
  @Override
  public List<OfferDTO> retrieveNotExcludedSelectiveOffers(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method retrieveNotExcludedSelectiveOffers, Parameters: submissionId: {0}",
      submissionId);

    return OfferDTOWithCriteriaMapper.INSTANCE
      .toOfferDTO(new JPAQueryFactory(em).select(qOfferEntity).from(qOfferEntity)
        .where(qOfferEntity.submittent.submissionId.id.eq(submissionId)
          .and((qOfferEntity.isExcludedFromProcess.isFalse()
            .or(qOfferEntity.isExcludedFromProcess.isNull())))
          .and((qOfferEntity.submittent.isApplicant.eq(Boolean.TRUE)
            .and(qOfferEntity.excludedFirstLevel.eq(Boolean.FALSE)))
            .or(qOfferEntity.submittent.isApplicant.eq(Boolean.FALSE))
            .or(qOfferEntity.submittent.isApplicant.isNull())))
        .fetch());
  }

  /**
   * Audit log.
   *
   * @param event the event
   * @param auditMessage the audit message
   * @param submissionId the submission id
   */
  private void auditLog(String event, String auditMessage, String submissionId,
    String additionalInfo) {

    LOGGER.log(Level.CONFIG, "Executing method auditLog, Parameters: event: {0}, "
        + "auditMessage: {1}, submissionId: {2}, additionalInfo: {3}",
      new Object[]{event, auditMessage, submissionId, additionalInfo});

    audit.createLogAudit(AuditLevel.PROJECT_LEVEL.name(), event, AuditGroupName.SUBMISSION.name(),
      auditMessage, getUser().getId(), submissionId, null, null, LookupValues.EXTERNAL_LOG);
  }

  @Override
  public List<OfferDTO> retrieveOfferApplicants(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method retrieveOfferApplicants, Parameters: submissionId: {0}",
      submissionId);

    return OfferDTOWithCriteriaMapper.INSTANCE
      .toOfferDTO(new JPAQueryFactory(em)
        .select(qOfferEntity).from(qOfferEntity).where(qOfferEntity.submittent.submissionId.id
          .eq(submissionId).and(qOfferEntity.submittent.isApplicant.eq(Boolean.TRUE)))
        .fetch());
  }

  @Override
  public BigDecimal calculateCHFMWSTValue(OfferDTO offerDTO) {
    BigDecimal value;
    BigDecimal vat = new BigDecimal(offerDTO.getVat());
    if (offerDTO.getIsVatPercentage() != null && offerDTO.getIsVatPercentage().equals(true)) {
      if (offerDTO.getAmount() != null) {
        value = offerDTO.getAmount().multiply(vat).divide(new BigDecimal(100));
      } else {
        value = new BigDecimal(0);
      }
    } else {
      value = vat;
    }
    return value;
  }

}
