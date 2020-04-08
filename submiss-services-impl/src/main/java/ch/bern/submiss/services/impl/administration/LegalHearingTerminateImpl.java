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

import ch.bern.submiss.services.api.administration.LegalHearingService;
import ch.bern.submiss.services.api.administration.SubmissionService;
import ch.bern.submiss.services.api.administration.UserAdministrationService;
import ch.bern.submiss.services.api.constants.CategorySD;
import ch.bern.submiss.services.api.constants.DocumentAttributes;
import ch.bern.submiss.services.api.constants.Process;
import ch.bern.submiss.services.api.constants.SelectiveLevel;
import ch.bern.submiss.services.api.constants.Template;
import ch.bern.submiss.services.api.constants.TenderStatus;
import ch.bern.submiss.services.api.dto.ExclusionReasonDTO;
import ch.bern.submiss.services.api.dto.LegalHearingExclusionDTO;
import ch.bern.submiss.services.api.dto.LegalHearingTerminateDTO;
import ch.bern.submiss.services.api.dto.MasterListValueHistoryDTO;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.api.util.ValidationMessages;
import ch.bern.submiss.services.impl.mappers.LegalHearingExclusionDTOMapper;
import ch.bern.submiss.services.impl.mappers.LegalHearingTerminateDTOMapper;
import ch.bern.submiss.services.impl.mappers.MasterListValueHistoryMapper;
import ch.bern.submiss.services.impl.mappers.MasterListValueMapper;
import ch.bern.submiss.services.impl.mappers.SubmissionMapper;
import ch.bern.submiss.services.impl.mappers.SubmittentDTOMapper;
import ch.bern.submiss.services.impl.model.FormalAuditEntity;
import ch.bern.submiss.services.impl.model.LegalHearingExclusionEntity;
import ch.bern.submiss.services.impl.model.LegalHearingTerminateEntity;
import ch.bern.submiss.services.impl.model.MasterListValueEntity;
import ch.bern.submiss.services.impl.model.MasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.OfferCriterionEntity;
import ch.bern.submiss.services.impl.model.QFormalAuditEntity;
import ch.bern.submiss.services.impl.model.QLegalHearingExclusionEntity;
import ch.bern.submiss.services.impl.model.QLegalHearingTerminateEntity;
import ch.bern.submiss.services.impl.model.QMasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.QOfferCriterionEntity;
import ch.bern.submiss.services.impl.model.QOfferEntity;
import ch.bern.submiss.services.impl.model.QSubmissionEntity;
import ch.bern.submiss.services.impl.model.QSubmittentEntity;
import ch.bern.submiss.services.impl.model.SubmissionEntity;
import ch.bern.submiss.services.impl.model.SubmittentEntity;
import ch.bern.submiss.services.impl.util.ComparatorUtil;
import com.eurodyn.qlack2.fuse.cm.api.DocumentService;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.OptimisticLockException;
import javax.persistence.RollbackException;
import javax.transaction.Transactional;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

/**
 * The Class LegalHearingTerminateImpl.
 */
@Transactional
@OsgiServiceProvider(classes = {LegalHearingService.class})
@Singleton
public class LegalHearingTerminateImpl extends BaseService implements LegalHearingService {

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER = Logger.getLogger(LegalHearingTerminateImpl.class.getName());
  /**
   * The q offer criterion entity.
   */
  QOfferCriterionEntity qOfferCriterionEntity = QOfferCriterionEntity.offerCriterionEntity;
  /**
   * The q submittent entity.
   */
  QSubmittentEntity qSubmittentEntity = QSubmittentEntity.submittentEntity;
  /**
   * The q offer entity.
   */
  QOfferEntity qOfferEntity = QOfferEntity.offerEntity;
  /**
   * The q legal hearing exclusion entity.
   */
  QLegalHearingExclusionEntity qLegalHearingExclusionEntity =
    QLegalHearingExclusionEntity.legalHearingExclusionEntity;
  /**
   * The q master list value history entity.
   */
  QMasterListValueHistoryEntity qMasterListValueHistoryEntity =
    QMasterListValueHistoryEntity.masterListValueHistoryEntity;
  /**
   * The q legal hearing terminate entity.
   */
  QLegalHearingTerminateEntity qLegalHearingTerminateEntity =
    QLegalHearingTerminateEntity.legalHearingTerminateEntity;
  /**
   * The q formal audit entity.
   */
  QFormalAuditEntity qFormalAuditEntity = QFormalAuditEntity.formalAuditEntity;
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
   * The document service.
   */
  @Inject
  private DocumentService documentService;
  @Inject
  private CacheBean cacheBean;

  @Override
  public Set<ValidationError> createLegalHearingTermination(
    LegalHearingTerminateDTO legalHearingTerminateDTO, String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method createLegalHearingTermination, Parameters: "
        + "legalHearingTerminateDTO: {0}, submissionId: {1}",
      new Object[]{legalHearingTerminateDTO, submissionId});

    Set<ValidationError> optimisticLockErrors = new HashSet<>();

    // Check if entry already exists in database
    if (legalHearingTerminateEntryExists(submissionId)) {
      optimisticLockErrors
        .add(new ValidationError(ValidationMessages.OPTIMISTIC_LOCK_ERROR_FIELD,
          ValidationMessages.OPTIMISTIC_LOCK));
      return optimisticLockErrors;
    }

    try {
      // Create a legalHearingTerminate entry
      LegalHearingTerminateEntity legalHearingTerminateEntity =
        LegalHearingTerminateDTOMapper.INSTANCE
          .toLegalHearingTerminateEntity(legalHearingTerminateDTO);
      legalHearingTerminateEntity.setSubmission(em.find(SubmissionEntity.class, submissionId));
      legalHearingTerminateEntity.setCreatedBy(getUserId());
      em.persist(legalHearingTerminateEntity);
    } catch (OptimisticLockException | RollbackException e) {
      optimisticLockErrors
        .add(new ValidationError(ValidationMessages.OPTIMISTIC_LOCK_ERROR_FIELD,
          ValidationMessages.OPTIMISTIC_LOCK));
    }
    return optimisticLockErrors;
  }

  /**
   * Check if legalHearingTerminate entry already exists in database.
   *
   * @param submissionId the submissionId
   * @return true if entry exists
   */
  private boolean legalHearingTerminateEntryExists(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method createLegalHearingTermination, Parameters: "
        + "submissionId: {1}", submissionId);

    return new JPAQueryFactory(em)
      .selectFrom(qLegalHearingTerminateEntity)
      .where(qLegalHearingTerminateEntity.submission.id.eq(submissionId))
      .fetchCount() > 0;
  }

  @Override
  public Set<ValidationError> updateLegalHearingTermination(
    LegalHearingTerminateDTO legalHearingTerminateDTO, String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateLegalHearingTermination, Parameters: legalHearingTerminateDTO: {0}",
      legalHearingTerminateDTO);

    Set<ValidationError> optimisticLockErrors = new HashSet<>();
    LegalHearingTerminateEntity legalHearingTerminateEntity =
      em.find(LegalHearingTerminateEntity.class, legalHearingTerminateDTO.getId());

    // Check fot optimistic lock errors
    if (!legalHearingTerminateDTO.getVersion()
      .equals(legalHearingTerminateEntity.getVersion())) {
      optimisticLockErrors
        .add(new ValidationError(ValidationMessages.OPTIMISTIC_LOCK_ERROR_FIELD,
          ValidationMessages.OPTIMISTIC_LOCK));
      return optimisticLockErrors;
    }
    
    try {
      // Update legalHearingTerminateEntity
      legalHearingTerminateEntity.setUpdatedBy(getUserId());
      legalHearingTerminateEntity.setDeadline(legalHearingTerminateDTO.getDeadline());
      legalHearingTerminateEntity.setReason(legalHearingTerminateDTO.getReason());
      legalHearingTerminateEntity.setTerminationReason(SubmissionMapper.INSTANCE
        .masterListValueDTOSetToMasterListValueEntitySet(
          legalHearingTerminateDTO.getTerminationReason()));
    } catch (OptimisticLockException | RollbackException e) {
      optimisticLockErrors
        .add(new ValidationError(ValidationMessages.OPTIMISTIC_LOCK_ERROR_FIELD,
          ValidationMessages.OPTIMISTIC_LOCK));
    }
    return optimisticLockErrors;
  }

  @Override
  public LegalHearingTerminateDTO getSubmissionLegalHearingTermination(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getSubmissionLegalHearingTermination, Parameters: submissionId: {0}",
      submissionId);

    LegalHearingTerminateEntity entity =
      new JPAQueryFactory(em).selectFrom(qLegalHearingTerminateEntity)
        .where(qLegalHearingTerminateEntity.submission.id.eq(submissionId)).fetchOne();
    return LegalHearingTerminateDTOMapper.INSTANCE.toLegalHearingTerminateDTO(entity);
  }

  /**
   * Retrieve existed excluded submittents.
   *
   * @param dtos the dtos
   * @param submissionId the submission id
   * @param exclusionReasonsDTOs the exclusion reasons DT os
   * @param process the process
   */
  private void retrieveExistedExcludedSubmittents(List<LegalHearingExclusionDTO> dtos,
    String submissionId, List<MasterListValueHistoryDTO> exclusionReasonsDTOs, Process process) {

    LOGGER.log(Level.CONFIG,
      "Executing method retrieveExistedExcludedSubmittents, Parameters: dtos: {0}, "
        + "submissionId: {1}, exclusionReasonsDTOs: {2}, process: {3}",
      new Object[]{dtos, submissionId, exclusionReasonsDTOs, process});

    if ((process.equals(Process.INVITATION) || process.equals(Process.OPEN)
      || process.equals(Process.SELECTIVE))
      && Integer.valueOf(submissionService.getCurrentStatusOfSubmission(submissionId)).compareTo(
      Integer.valueOf(TenderStatus.SUITABILITY_AUDIT_COMPLETED_AND_AWARD_EVALUATION_STARTED
        .getValue())) >= 0) {
      // Retrieve excluded submittents
      List<SubmittentEntity> exclSubmittents = new JPAQueryFactory(em).selectFrom(qSubmittentEntity)
        .where(qSubmittentEntity
          .in(new JPAQueryFactory(em).select(qOfferEntity.submittent).from(qOfferEntity)
            .where(qOfferEntity.isExcludedFromProcess.isTrue()
              .and(qOfferEntity.submittent.submissionId.id.eq(submissionId)))
            .fetch()))
        .fetch();
      for (SubmittentEntity s : exclSubmittents) {
        // if submittent is excluded by the system and has not updated yet then add the exclusion
        // reason to LegalHearingExclusionDTO to false.
        List<LegalHearingExclusionEntity> excl = new JPAQueryFactory(em)
          .select(qLegalHearingExclusionEntity).from(qLegalHearingExclusionEntity)
          .where(qLegalHearingExclusionEntity.submittent.id.eq(s.getId()),
            qLegalHearingExclusionEntity.updatedOn.isNotNull(),
            qLegalHearingExclusionEntity.level.in(SelectiveLevel.ZERO_LEVEL.getValue(),
              SelectiveLevel.SECOND_LEVEL.getValue()))
          .fetch();
        if (!excl.isEmpty()) {
          for (LegalHearingExclusionEntity lexEntity : excl) {
            if (lexEntity.getCreatedOn() == null
              && (lexEntity.getExclusionDeadline() != null
              || lexEntity.getFirstLevelExclusionDate() != null)
              && hasDocumentBeenGenerated(lexEntity.getSubmittent())) {

              LegalHearingExclusionDTO dto =
                LegalHearingExclusionDTOMapper.INSTANCE.toLegalHearingExclusionDTO(lexEntity);
              updateExclusionRreasons(dto, lexEntity, exclusionReasonsDTOs);
              dtos.add(dto);
            }
          }
        }
      }
    } else {
      // If submittent has not yet been excluded then calculate the formalExaminationFulfilled and
      // existsExclusionReasons values dynamically using the getSubmittentsBySubmission method.
      List<SubmittentEntity> submittents = SubmittentDTOMapper.INSTANCE
        .toSubmittent(submissionService.getSubmittentsBySubmission(submissionId));
      for (SubmittentEntity s : submittents) {
        // if submittent is excluded by the system and has not updated yet then add the exclusion
        // reason to LegalHearingExclusionDTO to false.
        List<LegalHearingExclusionEntity> excl = new JPAQueryFactory(em)
          .select(qLegalHearingExclusionEntity).from(qLegalHearingExclusionEntity)
          .where(qLegalHearingExclusionEntity.submittent.id.eq(s.getId()),
            qLegalHearingExclusionEntity.updatedOn.isNotNull(),
            qLegalHearingExclusionEntity.level.in(SelectiveLevel.ZERO_LEVEL.getValue(),
              SelectiveLevel.SECOND_LEVEL.getValue()))
          .fetch();
        if (excl.isEmpty()) {
          LegalHearingExclusionEntity lexEntity = new LegalHearingExclusionEntity();
          lexEntity.setSubmittent(s);

          updateExcludedSubmittentMustCriteriaValue(lexEntity);

          LegalHearingExclusionDTO dto =
            LegalHearingExclusionDTOMapper.INSTANCE.toLegalHearingExclusionDTO(lexEntity);

          setDefaultExclusionReasonsToFalse(dto, exclusionReasonsDTOs);
          // If submittent is excluded then add submittent to LegalHearingExclusionDTO list.
          List<OfferCriterionEntity> offerCriterionList =
            new JPAQueryFactory(em).selectFrom(qOfferCriterionEntity)
              .where(qOfferCriterionEntity.offer.submittent.eq(s)
                .and(qOfferCriterionEntity.criterion.criterionType
                  .eq(LookupValues.MUST_CRITERION_TYPE)))
              .fetch();
          if (!offerCriterionList.isEmpty() && !process.equals(Process.SELECTIVE)) {
            calculateExcludedSubmittentsWithCriteria(dtos, s, dto, offerCriterionList,
              SelectiveLevel.ZERO_LEVEL.getValue());
          } else {
            // Case Einladung, Offen when Submission has not MUSS criteria check only Formelle
            // Prufung.
            if (process.equals(Process.INVITATION) || process.equals(Process.OPEN)) {
              if ((s.getFormalExaminationFulfilled() == null || s.getFormalExaminationFulfilled())
                && (s.getExistsExclusionReasons() != null && s.getExistsExclusionReasons())) {
                addSubmittent(dtos, s, dto, SelectiveLevel.ZERO_LEVEL.getValue());
              }
            } else {
              // Case Selektiv 2. Stufe, exclude submittents according to Formelle Prufung.
              // If OBV Art 24 = Ja and Nachweis erbracht = Ja then exclude.
              if ((s.getFormalExaminationFulfilled() != null && s.getFormalExaminationFulfilled())
                && (s.getExistsExclusionReasons() != null && s.getExistsExclusionReasons())) {
                // exclude
                addSubmittent(dtos, s, dto, SelectiveLevel.SECOND_LEVEL.getValue());
              }
            }
          }
        }
      }
    }
  }

  /**
   * Adds the submittent.
   *
   * @param dtos the dtos
   * @param s the s
   * @param dto the dto
   * @param level the level
   */
  private void addSubmittent(List<LegalHearingExclusionDTO> dtos, SubmittentEntity s,
    LegalHearingExclusionDTO dto, Integer level) {

    LOGGER.log(Level.CONFIG,
      "Executing method addSubmittent, Parameters:  dtos: {0}, s: {1}, "
        + "dto: {2}, level: {3}",
      new Object[]{dtos, s, dto, level});

    List<LegalHearingExclusionEntity> legalEntities = new JPAQueryFactory(em)
      .select(qLegalHearingExclusionEntity).from(qLegalHearingExclusionEntity)
      .where(qLegalHearingExclusionEntity.submittent.id.eq(s.getId()),
        qLegalHearingExclusionEntity.level.eq(level))
      .fetch();
    if (!legalEntities.isEmpty()) {
      dto.setDisable(Boolean.TRUE);
    }
    dto.setLevel(level);
    dtos.add(dto);
  }

  /**
   * Retrieve added excluded submittents.
   *
   * @param dtos the dtos
   * @param submissionId the submission id
   * @param exclusionReasonsDTOs the exclusion reasons DT os
   */
  private void retrieveAddedExcludedSubmittents(List<LegalHearingExclusionDTO> dtos,
    String submissionId, List<MasterListValueHistoryDTO> exclusionReasonsDTOs) {

    LOGGER.log(Level.CONFIG,
      "Executing method retrieveAddedExcludedSubmittents, Parameters: dtos: {0}, "
        + "submissionId: {1}, exclusionReasonsDTOs: {2}",
      new Object[]{dtos, submissionId, exclusionReasonsDTOs});

    // Retrieve submittents that have added in order to send a Rechtliches Gehor (Ausschluss)
    // document.
    List<LegalHearingExclusionEntity> legalExclSubmittents = new JPAQueryFactory(em)
      .select(qLegalHearingExclusionEntity).from(qLegalHearingExclusionEntity)
      .where(qLegalHearingExclusionEntity.submittent.submissionId.id.eq(submissionId)).fetch();

    for (LegalHearingExclusionEntity l : legalExclSubmittents) {

      updateExcludedSubmittentMustCriteriaValue(l);

      LegalHearingExclusionDTO dto =
        LegalHearingExclusionDTOMapper.INSTANCE.toLegalHearingExclusionDTO(l);
      // If existsExclusionReasons is null, it means that the submittent is not excluded
      if (l.getSubmittent().getExistsExclusionReasons() == null) {
        dto.getSubmittent().setExistsExclusionReasons(false);
      }
      // Case of selective. Initialize value because Formal Audit First Level is stored in
      // FormalAuditEntity.
      if (l.getExistsExlReasons() != null) {
        dto.getSubmittent().setExistsExclusionReasons(l.getExistsExlReasons());
      }
      if (l.getProofsProvided() != null) {
        dto.getSubmittent().setFormalExaminationFulfilled(l.getProofsProvided());
      }
      if (l.getUpdatedOn() != null && l.getUpdatedBy() != null) {
        // Add submittents only if Rechtliches Gehor (Ausschluss) document has been generated.
        // Do not add submittents that have only been saved in Rechtliches Gehor (Ausschluss) mask.
        calculateUpdatedExcludedSubmittents(l, dto, dtos, exclusionReasonsDTOs);
      } else {
        // Set default exclusion reasons to false when added excluded submittent has not yet been
        // updated.
        setDefaultExclusionReasonsToFalse(dto, exclusionReasonsDTOs);
        dtos.add(dto);
      }
    }
  }

  /**
   * Calculate updated excluded submittents.
   *
   * @param l the l
   * @param dto the dto
   * @param dtos the dtos
   * @param exclusionReasonsDTOs the exclusion reasons DT os
   */
  private void calculateUpdatedExcludedSubmittents(LegalHearingExclusionEntity l,
    LegalHearingExclusionDTO dto, List<LegalHearingExclusionDTO> dtos,
    List<MasterListValueHistoryDTO> exclusionReasonsDTOs) {

    LOGGER.log(Level.CONFIG,
      "Executing method calculateUpdatedExcludedSubmittents, Parameters: l: {0}, "
        + "dto: {1}, dtos: {2}, exclusionReasonsDTOs: {3}",
      new Object[]{l, dto, dtos, exclusionReasonsDTOs});

    if (!hasDocumentBeenGenerated(l.getSubmittent())) {
      updateExclusionRreasons(dto, l, exclusionReasonsDTOs);
      dtos.add(dto);
    } else {
      if (l.getCreatedOn() == null) {
        if (l.getLevel().equals(SelectiveLevel.SECOND_LEVEL.getValue())) {
          // Check Formelle Prufung for Selektiv Verfahren 2.Stufe
          if ((l.getSubmittent().getExistsExclusionReasons() == null
            || l.getSubmittent().getExistsExclusionReasons())
            && compareCurrentVsSpecificStatus(
            TenderStatus.fromValue(submissionService
              .getCurrentStatusOfSubmission(l.getSubmittent().getSubmissionId().getId())),
            TenderStatus.OFFER_OPENING_STARTED)) {
            updateExclusionRreasons(dto, l, exclusionReasonsDTOs);
            dtos.add(dto);
          }
        } else {
          if (l.getLevel().equals(SelectiveLevel.FIRST_LEVEL.getValue())) {
            // Case Selektiv 1. Stufe: Retrieve FormalAudit as it is stored in different table in
            // DB.
            // Then, exclude submittents dynamically.
            FormalAuditEntity formalAudit = new JPAQueryFactory(em).select(qFormalAuditEntity)
              .from(qFormalAuditEntity)
              .where(qFormalAuditEntity.submittent.id.eq(l.getSubmittent().getId())).fetchOne();
            l.getSubmittent().setExistsExclusionReasons(formalAudit.getExistsExclusionReasons());
            l.getSubmittent()
              .setFormalExaminationFulfilled(formalAudit.getFormalExaminationFulfilled());
          }
          if (l.getMustCritFulfilled() != null) {
            if ((l.getSubmittent().getFormalExaminationFulfilled() != null
              && !l.getSubmittent().getFormalExaminationFulfilled())) {
              if (!l.getMustCritFulfilled()) {
                updateExclusionRreasons(dto, l, exclusionReasonsDTOs);
                dtos.add(dto);
              }
            } else {
              if (l.getSubmittent().getExistsExclusionReasons() != null
                && l.getSubmittent().getExistsExclusionReasons()) {
                updateExclusionRreasons(dto, l, exclusionReasonsDTOs);
                dtos.add(dto);
              } else {
                if (!l.getMustCritFulfilled()) {
                  updateExclusionRreasons(dto, l, exclusionReasonsDTOs);
                  dtos.add(dto);
                }
              }
            }
          } else {
            if (l.getLevel().equals(SelectiveLevel.ZERO_LEVEL.getValue())) {
              if ((l.getSubmittent().getExistsExclusionReasons() != null
                && l.getSubmittent().getExistsExclusionReasons())
                && (l.getSubmittent().getFormalExaminationFulfilled() == null
                || l.getSubmittent().getFormalExaminationFulfilled())) {
                updateExclusionRreasons(dto, l, exclusionReasonsDTOs);
                dtos.add(dto);
              }
            } else {
              // Case Selektiv 1. Stufe: Retrieve FormalAudit as it is stored in different table in
              // DB.
              // Then, exclude submittents dynamically.
              FormalAuditEntity formalAudit = new JPAQueryFactory(em).select(qFormalAuditEntity)
                .from(qFormalAuditEntity)
                .where(qFormalAuditEntity.submittent.id.eq(l.getSubmittent().getId())).fetchOne();
              if ((formalAudit.getFormalExaminationFulfilled() != null
                && formalAudit.getFormalExaminationFulfilled())
                && (formalAudit.getExistsExclusionReasons() != null
                && formalAudit.getExistsExclusionReasons())) {
                updateExclusionRreasons(dto, l, exclusionReasonsDTOs);
                dtos.add(dto);
              }
            }
          }
        }
      } else {
        updateExclusionRreasons(dto, l, exclusionReasonsDTOs);
        dtos.add(dto);
      }
    }
  }

  /**
   * Checks for document been generated.
   *
   * @param submittent the submittent
   * @return true, if successful
   */
  private boolean hasDocumentBeenGenerated(SubmittentEntity submittent) {

    LOGGER.log(Level.CONFIG,
      "Executing method hasDocumentBeenGenerated, Parameters: submittent: {0}",
      submittent);

    HashMap<String, String> attributesMap = new HashMap<>();
    MasterListValueHistoryEntity templateId =
      new JPAQueryFactory(em).select(qMasterListValueHistoryEntity)
        .from(qMasterListValueHistoryEntity)
        .where(qMasterListValueHistoryEntity.shortCode.eq(Template.RECHTLICHES_GEHOR)
          .and(qMasterListValueHistoryEntity.tenant.id
            .eq(usersService.getUserById(getUser().getId()).getTenant().getId())))
        .fetchOne();
    attributesMap.put(DocumentAttributes.TEMPLATE_ID.name(),
      templateId.getMasterListValueId().getId());
    attributesMap.put(DocumentAttributes.TENDER_ID.name(), submittent.getId());
    attributesMap.put(DocumentAttributes.TENANT_ID.name(), templateId.getTenant().getId());
    attributesMap.put(DocumentAttributes.ADDITIONAL_INFO.name(), "EXCLUSION");
    return documentService.getNodeByAttributes(submittent.getSubmissionId().getId(), attributesMap)
      .isEmpty();
  }

  /**
   * Update exclusion reasons.
   *
   * @param dto the dto
   * @param l the l
   * @param exclusionReasonsDTOs the exclusion reasons DT os
   */
  private void updateExclusionRreasons(LegalHearingExclusionDTO dto, LegalHearingExclusionEntity l,
    List<MasterListValueHistoryDTO> exclusionReasonsDTOs) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateExclusionRreasons, Parameters: dto: {0}, "
        + "l: {1}, exclusionReasonsDTOs: {2}",
      new Object[]{dto, l, exclusionReasonsDTOs});

    // Initialize exclusion reasons.
    Set<ExclusionReasonDTO> exclusionReasonList = new HashSet<>();

    // Initialize list of Ids of the exclusion reasons.
    List<String> existedExclusionReasonIds = new ArrayList<>();
    for (MasterListValueEntity m : l.getExclusionReasons()) {
      existedExclusionReasonIds.add(m.getId());
    }

    // Iterate through all exclusion reasons (Stammdaten) and initialize ExclusionReasonDTO if
    // current exclusion reason exists in the excluded submittent.
    for (MasterListValueHistoryDTO exclusionReasonDTO : exclusionReasonsDTOs) {
      ExclusionReasonDTO exclReason = new ExclusionReasonDTO();
      exclReason.setExclusionReason(exclusionReasonDTO);
      // If the Id exists in the list of submittent exclusion reasons then set boolean to true.
      if (!existedExclusionReasonIds.isEmpty()
        && existedExclusionReasonIds.contains(exclusionReasonDTO.getMasterListValueId().getId())) {
        exclReason.setReasonExists(Boolean.TRUE);
      } else {
        exclReason.setReasonExists(Boolean.FALSE);
      }
      exclusionReasonList.add(exclReason);
    }
    dto.setExclusionReasons(exclusionReasonList);
  }

  /**
   * Get the excluded submittents
   *
   * @param submissionId the submission id
   * @return the excluded submittents
   */
  @Override
  public List<LegalHearingExclusionDTO> getExcludedSubmittents(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getExcludedSubmittents, Parameters: submissionId: {0}",
      submissionId);

    List<LegalHearingExclusionDTO> dtos = new ArrayList<>();

    List<MasterListValueHistoryDTO> exclusionReasonsDTOs =
      MasterListValueHistoryMapper.INSTANCE.toMasterListValueHistoryDTO(getAllExclusionReasons());

    /* historization part */
    List<MasterListValueHistoryDTO> historisedExclusionReasonDTOs = new ArrayList<>();
    Timestamp creationDate = submissionService.getDateOfCreationStatusOfSubmission(submissionId);
    Timestamp closeDate = submissionService.getDateOfCompletedOrCanceledStatus(submissionId);

    for (MasterListValueHistoryDTO exclusionReasonsDTO : exclusionReasonsDTOs) {
      MasterListValueHistoryDTO historisedExclusionReasonDTO =
        cacheBean.getValue(CategorySD.EXCLUSION_CRITERION.getValue(),
          exclusionReasonsDTO.getMasterListValueId().getId(), closeDate, creationDate, null);
      historisedExclusionReasonDTOs.add(historisedExclusionReasonDTO);
    }
    /* historization part */
    Process process = submissionService.getSubmissionProcess(submissionId);

    if (process.equals(Process.SELECTIVE)) {
      retrieveExistedExcludedApplicants(dtos, submissionId, historisedExclusionReasonDTOs);
    }

    retrieveExistedExcludedSubmittents(dtos, submissionId, historisedExclusionReasonDTOs, process);
    retrieveAddedExcludedSubmittents(dtos, submissionId, historisedExclusionReasonDTOs);

    for (LegalHearingExclusionDTO dto : dtos) {
      if (dto.getId() != null) {
        /*---- If submittent updated, disable previous same submittents ----*/
        if (dto.getCreatedOn() == null) {
          List<LegalHearingExclusionEntity> entity =
            new JPAQueryFactory(em).selectFrom(qLegalHearingExclusionEntity)
              .where(qLegalHearingExclusionEntity.submittent.id.eq(dto.getSubmittent().getId())
                .and(qLegalHearingExclusionEntity.createdOn.isNotNull())
                .and(qLegalHearingExclusionEntity.level.eq(dto.getLevel())))
              .fetch();
          if (!entity.isEmpty()) {
            dto.setDisable(Boolean.TRUE);
          }
        }
        /*---- If submittent added from user, disable previous same submittents ----*/
        else {
          List<LegalHearingExclusionEntity> entity = new JPAQueryFactory(em)
            .selectFrom(qLegalHearingExclusionEntity)
            .where(qLegalHearingExclusionEntity.submittent.id.eq(dto.getSubmittent().getId())
              .and(qLegalHearingExclusionEntity.level.eq(dto.getLevel()))
              .and(qLegalHearingExclusionEntity.createdOn.after(dto.getCreatedOn())))
            .fetch();
          if (!entity.isEmpty()) {
            dto.setDisable(Boolean.TRUE);
          }
        }
      }
      /*---- Disable Bewerber when status is greater than SUITABILITY_AUDIT_COMPLETED_S ----*/
      if (dto.getLevel().equals(SelectiveLevel.FIRST_LEVEL.getValue())
        && compareCurrentVsSpecificStatus(
        TenderStatus.fromValue(submissionService.getCurrentStatusOfSubmission(submissionId)),
        TenderStatus.SUITABILITY_AUDIT_COMPLETED_S)) {
        dto.setDisable(Boolean.TRUE);
      }
    }
    Collections.sort(dtos, ComparatorUtil.sortExcludedSubmittents);
    return dtos;
  }

  /**
   * Retrieve existed excluded applicants.
   *
   * @param dtos the dtos
   * @param submissionId the submission id
   * @param exclusionReasonsDTOs the exclusion reasons DT os
   */
  private void retrieveExistedExcludedApplicants(List<LegalHearingExclusionDTO> dtos,
    String submissionId, List<MasterListValueHistoryDTO> exclusionReasonsDTOs) {

    LOGGER.log(Level.CONFIG,
      "Executing method retrieveExistedExcludedApplicants, Parameters: dtos: {0}, "
        + "submissionId: {1}, exclusionReasonsDTOs: {2}",
      new Object[]{dtos, submissionId, exclusionReasonsDTOs});

    List<SubmittentEntity> applicants = SubmittentDTOMapper.INSTANCE
      .toSubmittent(submissionService.getApplicantsForFormalAudit(submissionId));
    for (SubmittentEntity s : applicants) {
      // if submittent is excluded by the system and has not updated yet then add the exclusion
      // reason to LegalHearingExclusionDTO to false.
      List<LegalHearingExclusionEntity> excl = new JPAQueryFactory(em)
        .select(qLegalHearingExclusionEntity).from(qLegalHearingExclusionEntity)
        .where(qLegalHearingExclusionEntity.submittent.id.eq(s.getId()),
          qLegalHearingExclusionEntity.updatedOn.isNotNull())
        .fetch();
      if (excl.isEmpty()) {
        LegalHearingExclusionEntity lexEntity = new LegalHearingExclusionEntity();
        lexEntity.setSubmittent(s);

        updateExcludedSubmittentMustCriteriaValue(lexEntity);

        LegalHearingExclusionDTO dto =
          LegalHearingExclusionDTOMapper.INSTANCE.toLegalHearingExclusionDTO(lexEntity);

        setDefaultExclusionReasonsToFalse(dto, exclusionReasonsDTOs);
        // If submittent is excluded then add submittent to LegalHearingExclusionDTO list.
        List<OfferCriterionEntity> offerCriterionList = new JPAQueryFactory(em)
          .selectFrom(qOfferCriterionEntity)
          .where(qOfferCriterionEntity.offer.submittent.eq(s).and(
            qOfferCriterionEntity.criterion.criterionType.eq(LookupValues.MUST_CRITERION_TYPE)))
          .fetch();
        if (!offerCriterionList.isEmpty()) {
          calculateExcludedSubmittentsWithCriteria(dtos, s, dto, offerCriterionList,
            SelectiveLevel.FIRST_LEVEL.getValue());
        } else {
          if ((s.getFormalExaminationFulfilled() == null || s.getFormalExaminationFulfilled())
            && (s.getExistsExclusionReasons() != null && s.getExistsExclusionReasons())) {
            addSubmittent(dtos, s, dto, SelectiveLevel.FIRST_LEVEL.getValue());
          }
        }
      }
    }
  }

  /**
   * Calculate excluded submittents with criteria.
   *
   * @param dtos the dtos
   * @param s the s
   * @param dto the dto
   * @param offerCriterionList the offer criterion list
   * @param level the level
   */
  private void calculateExcludedSubmittentsWithCriteria(List<LegalHearingExclusionDTO> dtos,
    SubmittentEntity s, LegalHearingExclusionDTO dto,
    List<OfferCriterionEntity> offerCriterionList, Integer level) {

    LOGGER.log(Level.CONFIG,
      "Executing method calculateExcludedSubmittentsWithCriteria, Parameters:  dtos: {0}, "
        + "dto: {1}, offerCriterionList: {2}, level: {3}",
      new Object[]{dtos, dto, offerCriterionList, level});

    for (OfferCriterionEntity offerCriterion : offerCriterionList) {
      // If submittent has Nachweis erbracht = Nein then check MUSS Criteria.
      if (s.getFormalExaminationFulfilled() != null && !s.getFormalExaminationFulfilled()) {
        if (offerCriterion.getCriterion().getCriterionType()
          .equals(LookupValues.MUST_CRITERION_TYPE) && offerCriterion.getIsFulfilled() == null
          || !offerCriterion.getIsFulfilled()) {
          addSubmittent(dtos, s, dto, level);
          break;
        }
      } else {
        // If submittent has Nachweis erbracht = Ja then if check existsExclusionReason =
        // true (OBV Art 24).
        if (s.getExistsExclusionReasons() != null && s.getExistsExclusionReasons()) {
          addSubmittent(dtos, s, dto, level);
          break;
        } else {
          // If existsExclusionReason = false then check MUSS Criteria.
          if (offerCriterion.getCriterion().getCriterionType()
            .equals(LookupValues.MUST_CRITERION_TYPE) && offerCriterion.getIsFulfilled() != null
            && !offerCriterion.getIsFulfilled()) {
            addSubmittent(dtos, s, dto, level);
            break;
          }
        }
      }
    }
  }

  /**
   * This method is used to set the default exclusion reason of the excluded submittent to false. It
   * is used when submittent is excluded from the system or it has been added by user and has not
   * yet been updated.
   *
   * @param dto the dto
   * @param exclusionReasonsDTOs the exclusion reasons DT os
   */
  private void setDefaultExclusionReasonsToFalse(LegalHearingExclusionDTO dto,
    List<MasterListValueHistoryDTO> exclusionReasonsDTOs) {

    LOGGER.log(Level.CONFIG,
      "Executing method setDefaultExclusionReasonsToFalse, Parameters: "
        + "dto: {0}, exclusionReasonsDTOs: {1}",
      new Object[]{dto, exclusionReasonsDTOs});

    // Initialize exclusion reasons.
    Set<ExclusionReasonDTO> exclusionReasonList = new HashSet<>();
    for (MasterListValueHistoryDTO exclReasonDTO : exclusionReasonsDTOs) {
      ExclusionReasonDTO exclReason = new ExclusionReasonDTO();
      exclReason.setExclusionReason(exclReasonDTO);
      exclReason.setReasonExists(Boolean.FALSE);
      exclusionReasonList.add(exclReason);
    }
    dto.setExclusionReasons(exclusionReasonList);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.LegalHearingService#addExcludedSubmittent(java.lang
   * .String)
   */
  @Override
  public void addExcludedSubmittent(String submittentId) {

    LOGGER.log(Level.CONFIG,
      "Executing method addExcludedSubmittent, Parameters: submittentId: {0}",
      submittentId);

    SubmittentEntity submittentEntity = em.find(SubmittentEntity.class, submittentId);
    LegalHearingExclusionEntity legalHearingExclusionEntity = new LegalHearingExclusionEntity();
    if (submittentEntity.getSubmissionId().getProcess().equals(Process.SELECTIVE)) {
      legalHearingExclusionEntity.setLevel(SelectiveLevel.SECOND_LEVEL.getValue());
    } else {
      legalHearingExclusionEntity.setLevel(SelectiveLevel.ZERO_LEVEL.getValue());
    }
    createLegalHearingExclusionEntity(submittentEntity, legalHearingExclusionEntity);
  }

  /**
   * Creates the legal hearing exclusion entity.
   *
   * @param submittentEntity the submittent entity
   * @param legalHearingExclusionEntity the legal hearing exclusion entity
   */
  private void createLegalHearingExclusionEntity(SubmittentEntity submittentEntity,
    LegalHearingExclusionEntity legalHearingExclusionEntity) {

    LOGGER.log(Level.CONFIG,
      "Executing method createLegalHearingExclusionEntity, Parameters: submittentEntity: {0},"
        + "legalHearingExclusionEntity: {1}",
      new Object[]{submittentEntity, legalHearingExclusionEntity});

    legalHearingExclusionEntity.setSubmittent(submittentEntity);
    legalHearingExclusionEntity.setCreatedBy(getUserId());

    updateExcludedSubmittentMustCriteriaValue(legalHearingExclusionEntity);
    // Initialize exclusion reasons.
    Set<MasterListValueEntity> exclusionReasonList = new HashSet<>();
    List<MasterListValueHistoryEntity> exclusionReasonEntities = getAllExclusionReasons();

    for (MasterListValueHistoryEntity m : exclusionReasonEntities) {
      exclusionReasonList.add(m.getMasterListValueId());
    }
    legalHearingExclusionEntity.setExclusionReasons(exclusionReasonList);

    em.persist(legalHearingExclusionEntity);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.LegalHearingService#updateExcludedSubmittent(java.
   * util.List, java.util.Date, java.lang.String, java.util.Date)
   */
  @Override
  public Set<ValidationError> updateExcludedSubmittent(List<LegalHearingExclusionDTO> legalHearingExclusionDTO,
    Date exclusionDate, String submissionId, Long submissionVersion, Date firstLevelExclusionDate) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateExcludedSubmittent, Parameters:  legalHearingExclusionDTO: {0},"
        + "exclusionDate: {1}, submissionId: {2}, firstLevelExclusionDate: {3}",
      new Object[]{legalHearingExclusionDTO, exclusionDate, submissionId, firstLevelExclusionDate});

    Set<ValidationError> optimisticLockErrors = new HashSet<>();
    List<MasterListValueHistoryEntity> exclusionReasonEntities = getAllExclusionReasons();
    if (submissionId != null) {
      SubmissionEntity submission = em.find(SubmissionEntity.class, submissionId);
      if (!submission.getVersion().equals(submissionVersion)) {
        optimisticLockErrors
          .add(new ValidationError(ValidationMessages.OPTIMISTIC_LOCK_ERROR_FIELD, ValidationMessages.OPTIMISTIC_LOCK));
        return optimisticLockErrors;
      }
      if (exclusionDate != null) {
        submission.setExclusionDeadline(exclusionDate);
      }
      if (firstLevelExclusionDate != null) {
        submission.setFirstLevelExclusionDate(firstLevelExclusionDate);
      }
      em.merge(submission);
    }
    for (LegalHearingExclusionDTO l : legalHearingExclusionDTO) {
      if (l.getId() != null) {
        optimisticLockErrors = updateAddedExlcudedSubmittent(l, exclusionReasonEntities);
      } else {
        updateExistedExcludedSubmittents(l, exclusionReasonEntities);
      }
      if (!optimisticLockErrors.isEmpty()) {
        break;
      }
    }
    return optimisticLockErrors;
  }

  /**
   * Update existed excluded submittents.
   *
   * @param l the l
   * @param exclusionReasonEntities the exclusion reason entities
   */
  private void updateExistedExcludedSubmittents(LegalHearingExclusionDTO l,
    List<MasterListValueHistoryEntity> exclusionReasonEntities) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateExistedExcludedSubmittents, Parameters: l: {0}, "
        + "exclusionReasonEntities: {1}",
      new Object[]{l, exclusionReasonEntities});

    SubmittentEntity submittentEntity =
      SubmittentDTOMapper.INSTANCE.toSubmittent(l.getSubmittent());
    LegalHearingExclusionEntity legalHearingExclusionEntity =
      LegalHearingExclusionDTOMapper.INSTANCE.toLegalHearingExclusionEntity(l);
    legalHearingExclusionEntity.setSubmittent(submittentEntity);

    // Case selective. Update values because Formelle Prufung of Selektiv 1. Stufe is stored in
    // FormalAudit table.
    FormalAuditEntity formalAudit =
      new JPAQueryFactory(em).select(qFormalAuditEntity).from(qFormalAuditEntity)
        .where(qFormalAuditEntity.submittent.id.eq(l.getSubmittent().getId())).fetchOne();
    if (formalAudit != null) {
      legalHearingExclusionEntity.setProofsProvided(formalAudit.getFormalExaminationFulfilled());
      legalHearingExclusionEntity.setExistsExlReasons(formalAudit.getExistsExclusionReasons());
    }

    legalHearingExclusionEntity.setExclusionReason(l.getExclusionReason());
    legalHearingExclusionEntity.setExclusionDeadline(l.getExclusionDeadline());

    legalHearingExclusionEntity.setUpdatedBy(getUserId());

    updateExcludedSubmittentMustCriteriaValue(legalHearingExclusionEntity);

    // Iterate through all exclusion reasons.
    for (Iterator<MasterListValueHistoryEntity> iterator =
      exclusionReasonEntities.iterator(); iterator.hasNext(); ) {
      MasterListValueHistoryEntity exclusionEntity = iterator.next();
      for (ExclusionReasonDTO exclReason : l.getExclusionReasons()) {
        // Compare existed exclusion reason with the exclusion reasons from Stammdaten.
        // If existsReason is true add to exclusion reason list.
        if (exclusionEntity.getMasterListValueId().getId()
          .equals(exclReason.getExclusionReason().getMasterListValueId().getId())
          && exclReason.getReasonExists()) {

          legalHearingExclusionEntity.getExclusionReasons().add(MasterListValueMapper.INSTANCE
            .toMasterListValue(exclReason.getExclusionReason().getMasterListValueId()));

        }
      }
    }
    for (Iterator<MasterListValueEntity> iterator =
      legalHearingExclusionEntity.getExclusionReasons().iterator(); iterator.hasNext(); ) {
      MasterListValueEntity exclusionEntity = iterator.next();
      for (ExclusionReasonDTO exclReason : l.getExclusionReasons()) {
        if (exclusionEntity.getId()
          .equals(exclReason.getExclusionReason().getMasterListValueId().getId())
          && !exclReason.getReasonExists()) {
          // Remove exclusion reason if reasonExists value is false.
          iterator.remove();
        }
      }
    }
    em.persist(legalHearingExclusionEntity);
  }

  /**
   * Update added exlcuded submittent.
   *
   * @param l the LegalHearingExclusionDTO
   * @param exclusionReasonEntities the exclusion reason entities
   */
  private Set<ValidationError> updateAddedExlcudedSubmittent(LegalHearingExclusionDTO l,
    List<MasterListValueHistoryEntity> exclusionReasonEntities) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateAddedExlcudedSubmittent, Parameters: l: {0},"
        + "exclusionReasonEntities: {1}",
      new Object[]{l, exclusionReasonEntities});

    Set<ValidationError> optimisticLockErrors = new HashSet<>();

    // Update excluded submittents that user has added and already exists in DB.
    LegalHearingExclusionEntity legalHearingExclusionEntity =
      em.find(LegalHearingExclusionEntity.class, l.getId());

    if (!legalHearingExclusionEntity.getVersion().equals(l.getVersion())) {
      optimisticLockErrors
        .add(new ValidationError(ValidationMessages.OPTIMISTIC_LOCK_ERROR_FIELD,
          ValidationMessages.OPTIMISTIC_LOCK));
      return optimisticLockErrors;
    }

    legalHearingExclusionEntity
      .setSubmittent(SubmittentDTOMapper.INSTANCE.toSubmittent(l.getSubmittent()));
    legalHearingExclusionEntity.setUpdatedBy(getUserId());
    legalHearingExclusionEntity.setExclusionReason(l.getExclusionReason());
    legalHearingExclusionEntity.setExclusionDeadline(l.getExclusionDeadline());
    updateExcludedSubmittentMustCriteriaValue(legalHearingExclusionEntity);

    // Iterate through all exclusion reasons.
    for (Iterator<MasterListValueHistoryEntity> iterator =
      exclusionReasonEntities.iterator(); iterator.hasNext(); ) {
      MasterListValueHistoryEntity exclusionEntity = iterator.next();
      // Use ExclusionReasonDTO which contains a mapping between an exclusion reason (Stammdaten)
      // and
      // true/false depending on user input.
      for (ExclusionReasonDTO exclReason : l.getExclusionReasons()) {
        if (exclusionEntity.getMasterListValueId().getId()
          .equals(exclReason.getExclusionReason().getMasterListValueId().getId())
          && exclReason.getReasonExists()) {
          // Add exclusion reason if reasonExists value is true.
          legalHearingExclusionEntity.getExclusionReasons().add(MasterListValueMapper.INSTANCE
            .toMasterListValue(exclReason.getExclusionReason().getMasterListValueId()));
        }
      }
    }
    for (Iterator<MasterListValueEntity> iterator =
      legalHearingExclusionEntity.getExclusionReasons().iterator(); iterator.hasNext(); ) {
      MasterListValueEntity exclusionEntity = iterator.next();
      for (ExclusionReasonDTO exclReason : l.getExclusionReasons()) {
        if (exclusionEntity.getId()
          .equals(exclReason.getExclusionReason().getMasterListValueId().getId())
          && !exclReason.getReasonExists()) {
          // Remove exclusion reason if reasonExists value is false.
          iterator.remove();
        }
      }
    }
    em.merge(legalHearingExclusionEntity);
    return optimisticLockErrors;
  }

  /**
   * Update excluded submittent must criteria value.
   *
   * @param legalHearingExclusionEntity the legal hearing exclusion entity
   */
  private void updateExcludedSubmittentMustCriteriaValue(
    LegalHearingExclusionEntity legalHearingExclusionEntity) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateExcludedSubmittentMustCriteriaValue, Parameters: "
        + "legalHearingExclusionEntity: {0}",
      legalHearingExclusionEntity);

    // If at least one MUSS Criterion is false then set mustCritFulfilled to false.
    List<OfferCriterionEntity> offerCriteria = new JPAQueryFactory(em).select(qOfferCriterionEntity)
      .from(qOfferCriterionEntity)
      .where(qOfferCriterionEntity.criterion.criterionType.eq(LookupValues.MUST_CRITERION_TYPE)

        .and(qOfferCriterionEntity.offer.submittent.id
          .eq(legalHearingExclusionEntity.getSubmittent().getId())))
      .fetch();
    // Update Submittent MUSS criteria when Submittent is not Selektiv 2. Stufe.
    // Selektiv 2. Stufe has not MUSS criteria.
    if (!offerCriteria.isEmpty()) {
      for (OfferCriterionEntity offer : offerCriteria) {
        if (offer.getIsFulfilled() != null) {
          if (!offer.getIsFulfilled()) {
            legalHearingExclusionEntity.setMustCritFulfilled(Boolean.FALSE);
            break;
          } else {
            legalHearingExclusionEntity.setMustCritFulfilled(Boolean.TRUE);
          }
        } else {
          legalHearingExclusionEntity.setMustCritFulfilled(null);
        }
      }
    } else {
      legalHearingExclusionEntity.setMustCritFulfilled(null);
    }
  }

  /**
   * Gets all exclusion reasons from Stammdaten.
   *
   * @return the all exclusion reasons
   */
  private List<MasterListValueHistoryEntity> getAllExclusionReasons() {

    LOGGER.log(Level.CONFIG, "Executing method getAllExclusionReasons");

    return new JPAQueryFactory(em).select(qMasterListValueHistoryEntity)
      .from(qMasterListValueHistoryEntity)
      .where(
        qMasterListValueHistoryEntity.masterListValueId.masterList.categoryType
          .equalsIgnoreCase(CategorySD.EXCLUSION_CRITERION.getValue()),
        qMasterListValueHistoryEntity.active.isTrue(),
        qMasterListValueHistoryEntity.toDate.isNull(),
        qMasterListValueHistoryEntity.tenant.id.in(security.getPermittedTenants(getUser())))
      .orderBy(qMasterListValueHistoryEntity.value1.asc()).fetch();
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.LegalHearingService#getExclusionDeadline(java.lang.
   * String)
   */
  @Override
  public Date getExclusionDeadline(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getExclusionDeadline, Parameters: submissionId: {0}",
      submissionId);

    QSubmissionEntity qSubmissionEntity = QSubmissionEntity.submissionEntity;
    return new JPAQueryFactory(em).select(qSubmissionEntity.exclusionDeadline)
      .from(qSubmissionEntity).where(qSubmissionEntity.id.eq(submissionId)).fetchOne();
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.LegalHearingService#
   * retrieveLatestSubmittentExclusionDeadline(java.lang.String, java.lang.Integer)
   */
  @Override
  public Date retrieveLatestSubmittentExclusionDeadline(String submittentId, Integer level) {

    LOGGER.log(Level.CONFIG,
      "Executing method retrieveLatestSubmittentExclusionDeadline, Parameters: "
        + "submittentId: {0}, level: {1}",
      new Object[]{submittentId, level});

    if (level.equals(SelectiveLevel.FIRST_LEVEL.getValue())) {
      return new JPAQueryFactory(em).select(qLegalHearingExclusionEntity.firstLevelExclusionDate)
        .from(qLegalHearingExclusionEntity)
        .where(qLegalHearingExclusionEntity.submittent.id.eq(submittentId))
        .orderBy(qLegalHearingExclusionEntity.updatedOn.desc()).fetchFirst();
    } else {
      return new JPAQueryFactory(em).select(qLegalHearingExclusionEntity.exclusionDeadline)
        .from(qLegalHearingExclusionEntity)
        .where(qLegalHearingExclusionEntity.submittent.id.eq(submittentId))
        .orderBy(qLegalHearingExclusionEntity.updatedOn.desc()).fetchFirst();
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.LegalHearingService#addExcludedApplicant(java.lang.
   * String)
   */
  @Override
  public void addExcludedApplicant(String applicantId) {

    LOGGER.log(Level.CONFIG,
      "Executing method addExcludedApplicant, Parameters: applicantId: {0}",
      applicantId);

    SubmittentEntity submittentEntity = em.find(SubmittentEntity.class, applicantId);
    LegalHearingExclusionEntity legalHearingExclusionEntity = new LegalHearingExclusionEntity();
    legalHearingExclusionEntity.setLevel(SelectiveLevel.FIRST_LEVEL.getValue());
    createLegalHearingExclusionEntity(submittentEntity, legalHearingExclusionEntity);
  }

  @Override
  public LegalHearingExclusionDTO getLegalHearingExclusionBySubmittentAndLevel(String submittentId,
    List<Integer> levels) {
    return LegalHearingExclusionDTOMapper.INSTANCE
      .toLegalHearingExclusionDTO(new JPAQueryFactory(em).selectFrom(qLegalHearingExclusionEntity)
        .where(qLegalHearingExclusionEntity.submittent.id.eq(submittentId)
          .and(qLegalHearingExclusionEntity.level.in(levels)))
        .orderBy(qLegalHearingExclusionEntity.createdOn.desc()).fetchFirst());
  }
}
