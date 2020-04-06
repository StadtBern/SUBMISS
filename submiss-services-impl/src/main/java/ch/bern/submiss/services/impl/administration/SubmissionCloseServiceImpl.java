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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import com.eurodyn.qlack2.fuse.cm.api.DocumentService;
import com.eurodyn.qlack2.fuse.cm.api.VersionService;
import com.eurodyn.qlack2.fuse.cm.api.dto.NodeDTO;
import com.eurodyn.qlack2.fuse.cm.api.dto.VersionDTO;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ch.bern.submiss.services.api.administration.ProcedureService;
import ch.bern.submiss.services.api.administration.SubmissionCloseService;
import ch.bern.submiss.services.api.administration.SubmissionService;
import ch.bern.submiss.services.api.administration.UserAdministrationService;
import ch.bern.submiss.services.api.constants.AuditMessages;
import ch.bern.submiss.services.api.constants.DocumentAttributes;
import ch.bern.submiss.services.api.constants.Process;
import ch.bern.submiss.services.api.constants.Template;
import ch.bern.submiss.services.api.constants.TenderStatus;
import ch.bern.submiss.services.api.dto.AwardInfoDTO;
import ch.bern.submiss.services.api.dto.AwardInfoFirstLevelDTO;
import ch.bern.submiss.services.api.dto.AwardInfoOfferDTO;
import ch.bern.submiss.services.api.dto.AwardInfoOfferFirstLevelDTO;
import ch.bern.submiss.services.api.dto.ProcedureHistoryDTO;
import ch.bern.submiss.services.api.dto.SubmissionDTO;
import ch.bern.submiss.services.api.dto.SubmittentDTO;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.api.util.SubmissConverter;
import ch.bern.submiss.services.impl.mappers.AwardInfoDTOMapper;
import ch.bern.submiss.services.impl.mappers.AwardInfoFirstLevelDTOMapper;
import ch.bern.submiss.services.impl.mappers.AwardInfoOfferDTOMapper;
import ch.bern.submiss.services.impl.mappers.AwardInfoOfferFirstLevelDTOMapper;
import ch.bern.submiss.services.impl.mappers.SubmissionMapper;
import ch.bern.submiss.services.impl.model.CompanyEntity;
import ch.bern.submiss.services.impl.model.LegalHearingExclusionEntity;
import ch.bern.submiss.services.impl.model.MasterListValueEntity;
import ch.bern.submiss.services.impl.model.MasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.OfferCriterionEntity;
import ch.bern.submiss.services.impl.model.OfferEntity;
import ch.bern.submiss.services.impl.model.QDocumentDeadlineEntity;
import ch.bern.submiss.services.impl.model.QLegalHearingExclusionEntity;
import ch.bern.submiss.services.impl.model.QLegalHearingTerminateEntity;
import ch.bern.submiss.services.impl.model.QMasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.QOfferCriterionEntity;
import ch.bern.submiss.services.impl.model.QOfferEntity;
import ch.bern.submiss.services.impl.model.QSubmissionAwardInfoEntity;
import ch.bern.submiss.services.impl.model.QSubmissionEntity;
import ch.bern.submiss.services.impl.model.QTenderStatusHistoryEntity;
import ch.bern.submiss.services.impl.model.SubmissionAwardInfoEntity;
import ch.bern.submiss.services.impl.model.SubmissionEntity;
import ch.bern.submiss.services.impl.model.SubmittentEntity;
import ch.bern.submiss.services.impl.model.TenderStatusHistoryEntity;
import ch.bern.submiss.services.impl.util.ComparatorUtil;

/**
 * The Class SubmissionCloseServiceImpl.
 */
@Transactional
@OsgiServiceProvider(classes = {SubmissionCloseService.class})
@Singleton
public class SubmissionCloseServiceImpl extends BaseService implements SubmissionCloseService {

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER = Logger.getLogger(SubmissionCloseServiceImpl.class.getName());
  /**
   * The version service.
   */
  @Inject
  protected VersionService versionService;
  /**
   * The users service.
   */
  @Inject
  protected UserAdministrationService usersService;
  /**
   * The q offer entity.
   */
  QOfferEntity qOfferEntity = QOfferEntity.offerEntity;
  /**
   * The q tender status history entity.
   */
  QTenderStatusHistoryEntity qTenderStatusHistoryEntity =
    QTenderStatusHistoryEntity.tenderStatusHistoryEntity;
  /**
   * Another q tender status history entity.
   */
  QTenderStatusHistoryEntity qTenderStatusHistoryEntity1 =
    new QTenderStatusHistoryEntity(LookupValues.TABLE_ALIAS);
  /**
   * The q submission award info entity.
   */
  QSubmissionAwardInfoEntity qSubmissionAwardInfoEntity =
    QSubmissionAwardInfoEntity.submissionAwardInfoEntity;
  /**
   * The q offer criterion entity.
   */
  QOfferCriterionEntity qOfferCriterionEntity = QOfferCriterionEntity.offerCriterionEntity;
  /**
   * The q legal hearing terminate entity.
   */
  QLegalHearingTerminateEntity qLegalHearingTerminateEntity =
    QLegalHearingTerminateEntity.legalHearingTerminateEntity;
  /**
   * The q master list value history entity.
   */
  QMasterListValueHistoryEntity qMasterListValueHistoryEntity =
    QMasterListValueHistoryEntity.masterListValueHistoryEntity;
  /**
   * The q legal hearing exclusion entity.
   */
  QLegalHearingExclusionEntity qLegalHearingExclusionEntity =
    QLegalHearingExclusionEntity.legalHearingExclusionEntity;
  /**
   * The q document deadline entity.
   */
  QDocumentDeadlineEntity qDocumentDeadlineEntity = QDocumentDeadlineEntity.documentDeadlineEntity;
  /**
   * The q submission entity.
   */
  QSubmissionEntity entity = QSubmissionEntity.submissionEntity;
  /**
   * The procedure service.
   */
  @Inject
  private ProcedureService procedureService;
  /**
   * The submission service.
   */
  @Inject
  private SubmissionService submissionService;
  /**
   * The document service.
   */
  @Inject
  private DocumentService documentService;

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SubmissionCloseService#isOfferAboveThreshold(java.
   * lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public Boolean isOfferAboveThreshold(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method isOfferAboveThreshold, Parameters: submissionId: {0}",
      submissionId);

    // Get the process type id.
    String processTypeId = em.find(SubmissionEntity.class, submissionId).getProcessType()
      .getId();
    // Get the tenant id.
    String submissionTenantId = usersService.getTenant().getId();
    // Get the max amount for the submission to be considered above threshold
    // according to the process id and the tenant id
    ProcedureHistoryDTO thresholdEntity = procedureService.readProcedure(processTypeId,
      Process.INVITATION, submissionTenantId);
    BigDecimal thresholdValue = thresholdEntity.getValue();
    // build a case statement where for each offer if it is above threshold (its amount is greater
    // than the max amount)
    // it will be set to 1, otherwise 0
    NumberExpression<Integer> amountCase =
      new CaseBuilder().when(qOfferEntity.amount.gt(thresholdValue)).then(LookupValues.ONE_INT)
        .otherwise(LookupValues.ZERO_INT);
    // get the sum of all the results of the case
    Integer isAboveThreshold = new JPAQueryFactory(em).select(amountCase.sum()).from(qOfferEntity)
      .where(qOfferEntity.submittent.submissionId.id.eq(submissionId)).fetchOne();
    // if it is greater than 1, then at least one offer is above threshold
    return (isAboveThreshold != null && isAboveThreshold.intValue() >= LookupValues.ONE_INT);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SubmissionCloseService#reopenSubmission(java.lang.
   * String, java.lang.String)
   */
  @Override
  public void reopenSubmission(String submissionId, String reopenReason) {

    LOGGER.log(Level.CONFIG,
      "Executing method reopenSubmission, Parameters: submissionId: {0}, "
        + "reopenReason: {1}",
      new Object[]{submissionId, reopenReason});
    // get the 3 last statuses of the submission in order to discover if the submission was
    // cancelled (could be cancelled and reopened or cancelled, closed and reopened)
    // and if yes get its status before cancellation
    // and in case the submission was not cancelled get the status before closure
    List<String> previousStatuses = new JPAQuery<>(em).select(qTenderStatusHistoryEntity.statusId)
      .from(qTenderStatusHistoryEntity)
      .where(qTenderStatusHistoryEntity.tenderId.id.eq(submissionId))
      // so that we get the current status (status set on the latest date) first
      .orderBy(qTenderStatusHistoryEntity.onDate.desc())
      // return the 3 first results
      .limit(LookupValues.THREE_INT).fetch();

    Boolean isCancelled = false;
    String previousStatus = null;
    for (String itStatus : previousStatuses) {
      // if the status cancelled is found then in this iteration we will get the status before this
      // otherwise we will keep the last status before completed, because we may need to return to
      // this
      if (isCancelled || (!itStatus.equals(TenderStatus.PROCEDURE_COMPLETED.getValue())
        && !itStatus.equals(TenderStatus.PROCEDURE_CANCELED.getValue()))) {
        previousStatus = itStatus;
        break;
      } else if (itStatus.equals(TenderStatus.PROCEDURE_CANCELED.getValue())) {
        isCancelled = true;
      }
    }

    if (!isCancelled) {
      // in this case the status to return to depends on the process type of the submission
      // and on whether the submission is above threshold (for certain process types)
      SubmissionEntity submissionEntity = em.find(SubmissionEntity.class, submissionId);
      if (submissionEntity.getProcess().equals(Process.OPEN)
        || submissionEntity.getProcess().equals(Process.INVITATION)
        || submissionEntity.getProcess().equals(Process.SELECTIVE)
        // in case of the remaining processes, which are negotiated process (+ with competition)
        // we need to check if the submission is above threshold
        // and if the previous status is after (or equal) COMMISSION_PROCUREMENT_DECISION_CLOSED
        || (submissionEntity.getAboveThreshold() != null && submissionEntity.getAboveThreshold()
        && compareCurrentVsSpecificStatus(TenderStatus.fromValue(previousStatus),
        TenderStatus.COMMISSION_PROCUREMENT_DECISION_CLOSED))) {
        previousStatus = TenderStatus.COMMISSION_PROCUREMENT_DECISION_CLOSED.getValue();
        // case negotiated process (+ with competition) below threshold
        // with previous status after (or equal) FORMAL_AUDIT_COMPLETED
      } else if ((submissionEntity.getAboveThreshold() == null
        || !submissionEntity.getAboveThreshold())
        && compareCurrentVsSpecificStatus(TenderStatus.fromValue(previousStatus),
        TenderStatus.FORMAL_AUDIT_COMPLETED)) {
        // if the status is not FORMAL_AUDIT_COMPLETED, so it is after it
        // the award needs to be removed
        if (previousStatus != null
          && !previousStatus.equals(TenderStatus.FORMAL_AUDIT_COMPLETED.getValue())) {
          submissionService.removeAward(submissionId);
        }
        previousStatus = TenderStatus.FORMAL_AUDIT_COMPLETED.getValue();
      }
    }
    // the remaining cases are negotiated process (+ with competition)
    // with status before the status to return to,
    // so returning to the status before closure (previousStatus)

    // update to this status
    submissionService.updateSubmissionStatus(submissionId, previousStatus,
      AuditMessages.REOPEN_SUBMISSION.name(), reopenReason, LookupValues.EXTERNAL_LOG);
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SubmissionCloseService#
   * getAwardNoticesCreatedDateForClose(java.lang.String)
   */
  @Override
  public Timestamp getAwardNoticesCreatedDateForClose(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getAwardNoticesCreatedDateForClose, Parameters: submissionId: {0}",
      submissionId);

    // a query that returns the 2 last statuses of the submission
    List<TenderStatusHistoryEntity> tenderStatusHistoryEntityList = new JPAQuery<>(em)
      // we construct the query so that it returns the entity without the submission,
      // because we don't need the submission data
      .select(Projections.bean(TenderStatusHistoryEntity.class, qTenderStatusHistoryEntity.onDate,
        qTenderStatusHistoryEntity.statusId))
      .from(qTenderStatusHistoryEntity)
      .where(qTenderStatusHistoryEntity.tenderId.id.eq(submissionId))
      // so that we get the current status (status set on the latest date) first
      .orderBy(qTenderStatusHistoryEntity.onDate.desc())
      // return the 2 first results
      .limit(LookupValues.TWO_INT).fetch();

    // iterate them to find if at least one of them is AWARD_NOTICES_CREATED
    // and get its date
    Timestamp onDate = null;
    for (TenderStatusHistoryEntity tenderStatusHistoryEntity : tenderStatusHistoryEntityList) {
      if (tenderStatusHistoryEntity.getStatusId()
        .equals(TenderStatus.AWARD_NOTICES_CREATED.getValue())) {
        onDate = tenderStatusHistoryEntity.getOnDate();
      }
    }
    return onDate;
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SubmissionCloseService#getAwardInfo(java.lang.
   * String)
   */
  @Override
  public AwardInfoDTO getAwardInfo(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getAwardInfo, Parameters: submissionId: {0}",
      submissionId);
    // get the award info of the submission
    SubmissionAwardInfoEntity submissionAwardInfoEntity =
      new JPAQueryFactory(em).select(qSubmissionAwardInfoEntity).from(qSubmissionAwardInfoEntity)
        .where(qSubmissionAwardInfoEntity.submission.id.eq(submissionId)
          .and(qSubmissionAwardInfoEntity.level.eq(LookupValues.Level.SECOND.getValue())))
        .fetchOne();

    // if exists map to dto, if not exists, case create, create new dto
    AwardInfoDTO awardInfoDTO;
    boolean caseCreate = false;
    List<LegalHearingExclusionEntity> legalHearingExclusionList = null;
    if (submissionAwardInfoEntity != null) {
      awardInfoDTO = AwardInfoDTOMapper.INSTANCE.toAwardInfoDTO(submissionAwardInfoEntity);
    } else {
      awardInfoDTO = new AwardInfoDTO();
      // get the submission
      SubmissionDTO submissionDTO =
        SubmissionMapper.INSTANCE.toSubmissionDTO(em.find(SubmissionEntity.class, submissionId));
      awardInfoDTO.setSubmission(submissionDTO);

      // get a list of all legalHearingExclusionEntities of the current submission
      // in order to get the reason for each excluded submittent
      // only for processes where excluded submittents are displayed
      if (!awardInfoDTO.getSubmission().getProcess().equals(Process.NEGOTIATED_PROCEDURE)
        && !awardInfoDTO.getSubmission().getProcess()
        .equals(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION)) {
        legalHearingExclusionList = new JPAQueryFactory(em).selectFrom(qLegalHearingExclusionEntity)
          .where(qLegalHearingExclusionEntity.submittent.submissionId.id.eq(submissionId))
          .fetch();
      }

      caseCreate = true;
    }

    // initialize the values "existsExclusionReasons" and "formalExaminationFulfilled" for each
    // submittent by calling the getSubmittentsBySubmission function
    List<SubmittentDTO> submittentDTOs = submissionService.getSubmittentsBySubmission(submissionId);

    // get the offers of the submission.
    List<OfferEntity> offerEntities = new JPAQueryFactory(em).selectDistinct(qOfferEntity)
      .from(qOfferEntity).where(qOfferEntity.submittent.submissionId.id.eq(submissionId)).fetch();

    List<AwardInfoOfferDTO> awardInfoOfferDTOs = new ArrayList<>();

    // iterate the offers in order to get the needed data
    for (OfferEntity offerEntity : offerEntities) {
      // check if the information of the current offer needs to be sent
      // offers needed to be sent are the excluded for process OPEN, INVITATION, SELECTIVE
      // that is the offers with qExExaminationIsFulfilled = false for OPEN, INVITATION
      // and the offers with existsExclusionReasons = true for SELECTIVE
      // and the awarded for all processes, that is the offers with isAwarded = true
      // first get the awarded
      if (offerEntity.getIsAwarded() != null && offerEntity.getIsAwarded()) {
        AwardInfoOfferDTO awardInfoOfferDTO =
          AwardInfoOfferDTOMapper.INSTANCE.toAwardInfoOfferDTO(offerEntity);

        // construct the name of the submittent
        awardInfoOfferDTO.setSubmittentName(getSubmittentName(offerEntity.getSubmittent()));

        // add to the list of offers
        awardInfoOfferDTOs.add(awardInfoOfferDTO);
      } else {
        // then get the excluded
        if (!awardInfoDTO.getSubmission().getProcess().equals(Process.NEGOTIATED_PROCEDURE)
          && !awardInfoDTO.getSubmission().getProcess()
          .equals(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION)) {

          AwardInfoOfferDTO awardInfoOfferDTO =
            AwardInfoOfferDTOMapper.INSTANCE.toAwardInfoOfferDTO(offerEntity);
          // iterate the submittents in order to find the submittent of this offer,
          // in order to get the fields existsExclusionReasons and formalExaminationFulfilled
          for (SubmittentDTO submittentDTO : submittentDTOs) {
            if (offerEntity.getSubmittent().getId().equals(submittentDTO.getId())) {
              awardInfoOfferDTO
                .setExistsExclusionReasons(submittentDTO.getExistsExclusionReasons());
              awardInfoOfferDTO
                .setFormalExaminationFulfilled(submittentDTO.getFormalExaminationFulfilled());
              break;
            }
          }

          if ((awardInfoDTO.getSubmission().getProcess().equals(Process.SELECTIVE)
            && awardInfoOfferDTO.getExistsExclusionReasons() != null
            && awardInfoOfferDTO.getExistsExclusionReasons())
            || ((awardInfoDTO.getSubmission().getProcess().equals(Process.OPEN)
            || awardInfoDTO.getSubmission().getProcess().equals(Process.INVITATION))
            && awardInfoOfferDTO.getqExExaminationIsFulfilled() != null
            && !awardInfoOfferDTO.getqExExaminationIsFulfilled())) {

            // construct the name of the submittent
            awardInfoOfferDTO.setSubmittentName(getSubmittentName(offerEntity.getSubmittent()));

            // iterate the offerCriteria in order to check if at least one of them is not fulfilled
            // if no criteria exist, then set field to null
            Boolean isFulfilled = null;
            for (OfferCriterionEntity offerCriteria : offerEntity.getOfferCriteria()) {
              if (offerCriteria.getCriterion().getCriterionType()
                .equals(LookupValues.MUST_CRITERION_TYPE)) {
                if (offerCriteria.getIsFulfilled() != null && !offerCriteria.getIsFulfilled()) {
                  isFulfilled = false;
                  break;
                } else {
                  isFulfilled = true;
                }
              }
            }
            // then set the according flag
            awardInfoOfferDTO.setMustCriteriaFulfilled(isFulfilled);

            // construnct the exclusion reason only in case of create
            if (caseCreate) {
              String exclusionReason = null;

              // if Rechtliches Gehor exists, then take the reason from there
              // iterate the list of legalHearingExclusionEntities and
              // get the last created for the current submittent
              Date createdOn = null;
              String reason = null;
              boolean found = false;
              for (LegalHearingExclusionEntity legalHearingExclusion : legalHearingExclusionList) {
                if (legalHearingExclusion.getSubmittent().getId()
                  .equals(offerEntity.getSubmittent().getId())
                  && (createdOn == null || (legalHearingExclusion.getCreatedOn() != null
                  && createdOn.before(legalHearingExclusion.getCreatedOn())))) {
                  createdOn = legalHearingExclusion.getCreatedOn();
                  reason = legalHearingExclusion.getExclusionReason();
                  found = true;
                }
              }

              if (found) {
                exclusionReason = reason;
                // else if Nachweise erbracht is No then construct a text
              } else if (!awardInfoOfferDTO.getFormalExaminationFulfilled()) {
                exclusionReason = LookupValues.EXCLUSION_REASON;
                exclusionReason = exclusionReason.replace("&1",
                  offerEntity.getSubmittent().getCompanyId().getCompanyName());
                exclusionReason = exclusionReason.replace("&2",
                  convertToSwissDateIfNotNull(offerEntity.getOfferDate()));

                // two of the informations needed are taken from the Nachweisbrief document created
                // for the submittent
                HashMap<String, String> attributesMap = new HashMap<>();
                // Retrieve Nachweisbrief templateId.
                MasterListValueHistoryEntity templateId = new JPAQueryFactory(em)
                  .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
                  .where(qMasterListValueHistoryEntity.shortCode.eq(Template.NACHWEISBRIEF_PT)
                    .and(qMasterListValueHistoryEntity.tenant.id
                      .eq(usersService.getUserById(getUserId()).getTenant().getId())))
                  .fetchOne();
                attributesMap.put(DocumentAttributes.TEMPLATE_ID.name(),
                  templateId.getMasterListValueId().getId());
                attributesMap.put(DocumentAttributes.TENDER_ID.name(),
                  offerEntity.getSubmittent().getId());
                List<NodeDTO> nodes =
                  documentService.getNodeByAttributes(submissionId, attributesMap);
                // for each node (actually only one must exist)
                for (NodeDTO node : nodes) {
                  VersionDTO latestVersionDTO = versionService.getFileLatestVersion(node.getId());
                  // get the creation date of the document
                  exclusionReason = exclusionReason.replace("&3", SubmissConverter
                    .convertToSwissDate(new Date(latestVersionDTO.getCreatedOn())));
                  // the deadline is stored in another table per version id
                  Date deadline = new JPAQueryFactory(em).select(qDocumentDeadlineEntity.deadline)
                    .from(qDocumentDeadlineEntity)
                    .where(qDocumentDeadlineEntity.versionId.eq(latestVersionDTO.getId()))
                    .fetchOne();
                  exclusionReason =
                    exclusionReason.replace("&4", convertToSwissDateIfNotNull(deadline));
                }
              }
              awardInfoOfferDTO.setExclusionReason(exclusionReason);
            }

            // add to the list of offers
            awardInfoOfferDTOs.add(awardInfoOfferDTO);

          }
        }
      }
    }

    // add the list of offers to the data to return
    awardInfoDTO.setOffers(awardInfoOfferDTOs);

    return awardInfoDTO;
  }

  /**
   * Constructs the submittent name with the requested format.
   *
   * @param submittent the submittent entity
   * @return the submittent name
   */
  private String getSubmittentName(SubmittentEntity submittent) {

    LOGGER.log(Level.CONFIG,
      "Executing method getSubmittentName, Parameters: submittent: {0}",
      submittent);

    StringBuilder name = new StringBuilder();
    name.append(submittent.getCompanyId().getCompanyName() + ", "
      + submittent.getCompanyId().getLocation());
    if (!submittent.getJointVentures().isEmpty()) {
      name.append(" (ARGE: ");
      name.append(constructCompaniesNames(submittent.getJointVentures()));
    }
    if (!submittent.getSubcontractors().isEmpty()) {
      if (!submittent.getJointVentures().isEmpty()) {
        name.append(", Sub. U: ");
      } else {
        name.append(" (Sub. U: ");
      }
      name.append(constructCompaniesNames(submittent.getSubcontractors()));
    }
    if (!submittent.getJointVentures().isEmpty() || !submittent.getSubcontractors().isEmpty()) {
      name.append(")");
    }
    return name.toString();
  }

  /**
   * Constructs a string as a concatenation of the company names of the companies provided
   * concatenated by /.
   *
   * @param companies the set of companies
   * @return a string with the concatenation of the company names
   */
  private String constructCompaniesNames(Set<CompanyEntity> companies) {

    LOGGER.log(Level.CONFIG,
      "Executing method constructCompaniesNames, Parameters: companies: {0}",
      companies);

    StringBuilder name = new StringBuilder();
    List<CompanyEntity> companyList = new ArrayList<>(companies);
    Collections.sort(companyList, ComparatorUtil.sortCompaniesByCompanyName);
    boolean isFirst = true;
    for (CompanyEntity company : companyList) {
      // add / as separator to all entries besides the first
      if (!isFirst) {
        name.append(" / ");
      } else {
        isFirst = false;
      }
      name.append(company.getCompanyName());
    }
    return name.toString();
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SubmissionCloseService#saveAwardInfo(ch.bern.
   * submiss.services.api.dto.AwardInfoDTO)
   */
  @Override
  public void saveAwardInfo(AwardInfoDTO awardInfoDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method saveAwardInfo, Parameters: awardInfoDTO: {0}",
      awardInfoDTO);

    SubmissionAwardInfoEntity submissionAwardInfoEntity;
    boolean caseCreate = false;
    if (awardInfoDTO.getId() != null) { // case update
      submissionAwardInfoEntity = em.find(SubmissionAwardInfoEntity.class, awardInfoDTO.getId());
    } else { // case create
      submissionAwardInfoEntity = new SubmissionAwardInfoEntity();
      SubmissionEntity submissionEntity = new SubmissionEntity();
      submissionEntity.setId(awardInfoDTO.getSubmission().getId());
      submissionAwardInfoEntity.setSubmission(submissionEntity);
      submissionAwardInfoEntity.setLevel(LookupValues.Level.SECOND.getValue());
      caseCreate = true;
    }

    submissionAwardInfoEntity.setAvailableDate(awardInfoDTO.getAvailableDate());
    submissionAwardInfoEntity.setObjectNameRead(awardInfoDTO.getObjectNameRead());
    submissionAwardInfoEntity.setProjectNameRead(awardInfoDTO.getProjectNameRead());
    submissionAwardInfoEntity.setWorkingClassRead(awardInfoDTO.getWorkingClassRead());
    submissionAwardInfoEntity.setDescriptionRead(awardInfoDTO.getDescriptionRead());

    String userId = getUserId();
    Date now = new Date();
    submissionAwardInfoEntity.setUpdatedBy(userId);
    submissionAwardInfoEntity.setUpdatedOn(now);
    if (caseCreate) { // case create
      submissionAwardInfoEntity.setCreatedBy(userId);
      submissionAwardInfoEntity.setCreatedOn(now);
      submissionAwardInfoEntity.setFreezeCloseSubmission(awardInfoDTO.getFreezeCloseSubmission());
      em.persist(submissionAwardInfoEntity);
    } else { // case update
      // in order to check if the timer for the automatic closure of the submission needs to be
      // updated, we need to check the value of the freeze flag before the update
      // if the freeze flag was false before and now true, then set the timer to null
      // if the freeze flag was false before and now false, then leave the timer as is
      // if the freeze flag was true before and now true, then leave the timer as is (it will be
      // null)
      // if the freeze flag was true before and now false, then initiate the timer
      if ((submissionAwardInfoEntity.getFreezeCloseSubmission() == null
        || !submissionAwardInfoEntity.getFreezeCloseSubmission())
        && (awardInfoDTO.getFreezeCloseSubmission() != null
        && awardInfoDTO.getFreezeCloseSubmission())) {
        submissionAwardInfoEntity.setCloseCountdownStart(null);
      } else if (submissionAwardInfoEntity.getFreezeCloseSubmission() != null
        && submissionAwardInfoEntity.getFreezeCloseSubmission()
        && (awardInfoDTO.getFreezeCloseSubmission() == null
        || !awardInfoDTO.getFreezeCloseSubmission())) {
        submissionAwardInfoEntity.setCloseCountdownStart(now);
      } else {
        // otherwise keep the value that has been set before
        submissionAwardInfoEntity
          .setCloseCountdownStart(submissionAwardInfoEntity.getCloseCountdownStart());
      }
      submissionAwardInfoEntity.setFreezeCloseSubmission(awardInfoDTO.getFreezeCloseSubmission());
      em.merge(submissionAwardInfoEntity);
    }

    // iterate the offers in order to update the needed data
    if (awardInfoDTO.getOffers() != null) {
      for (AwardInfoOfferDTO awardInfoOfferDTO : awardInfoDTO.getOffers()) {
        // get the offer entity
        OfferEntity offerEntity = em.find(OfferEntity.class, awardInfoOfferDTO.getId());

        // set the updated fields
        offerEntity.setManualAmount(awardInfoOfferDTO.getManualAmount());
        offerEntity.setExclusionReason(awardInfoOfferDTO.getExclusionReason());
        Set<MasterListValueEntity> set =
          SubmissionMapper.INSTANCE.masterListValueDTOSetToMasterListValueEntitySet(
            awardInfoOfferDTO.getExclusionReasons());
        if (set != null) {
          offerEntity.setExclusionReasons(set);
        }
        em.merge(offerEntity);
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SubmissionCloseService#getAwardInfoFirstLevel(java.
   * lang.String)
   */
  @Override
  public AwardInfoFirstLevelDTO getAwardInfoFirstLevel(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getAwardInfoFirstLevel, Parameters: submissionId: {0}",
      submissionId);

    // get the award info first level of the submission
    SubmissionAwardInfoEntity submissionAwardInfoEntity =
      new JPAQueryFactory(em).select(qSubmissionAwardInfoEntity).from(qSubmissionAwardInfoEntity)
        .where(qSubmissionAwardInfoEntity.submission.id.eq(submissionId)
          .and(qSubmissionAwardInfoEntity.level.eq(LookupValues.Level.FIRST.getValue())))
        .fetchOne();

    // if exists map to dto, if not exists, case create, create new dto
    AwardInfoFirstLevelDTO awardInfoDTO;
    boolean caseCreate = false;
    List<LegalHearingExclusionEntity> legalHearingExclusionList = null;
    if (submissionAwardInfoEntity != null) {
      awardInfoDTO =
        AwardInfoFirstLevelDTOMapper.INSTANCE.toAwardInfoFirstLevelDTO(submissionAwardInfoEntity);
    } else {
      awardInfoDTO = new AwardInfoFirstLevelDTO();
      // get the submission
      SubmissionDTO submissionDTO =
        SubmissionMapper.INSTANCE.toSubmissionDTO(em.find(SubmissionEntity.class, submissionId));
      awardInfoDTO.setSubmission(submissionDTO);

      // get a list of all legalHearingExclusionEntities of the current submission
      // in order to get the reason for each excluded submittent
      legalHearingExclusionList = new JPAQueryFactory(em).selectFrom(qLegalHearingExclusionEntity)
        .where(qLegalHearingExclusionEntity.submittent.submissionId.id.eq(submissionId)).fetch();

      caseCreate = true;
    }

    // initialize the values "existsExclusionReasons" and "formalExaminationFulfilled" for each
    // submittent by calling the getApplicantsForFormalAudit function
    List<SubmittentDTO> submittentDTOs =
      submissionService.getApplicantsForFormalAudit(submissionId);

    // get the excluded offers of the submission.
    List<OfferEntity> offerEntities = new JPAQueryFactory(em).selectDistinct(qOfferEntity)
      .from(qOfferEntity).where(qOfferEntity.submittent.submissionId.id.eq(submissionId)
        .and(qOfferEntity.qExStatus.isFalse()))
      .fetch();

    List<AwardInfoOfferFirstLevelDTO> awardInfoOfferDTOs = new ArrayList<>();

    // Retrieve Nachweisbrief templateId (for later use, see templateId variable use).
    MasterListValueHistoryEntity templateId =
      new JPAQueryFactory(em).select(qMasterListValueHistoryEntity)
        .from(qMasterListValueHistoryEntity)
        .where(qMasterListValueHistoryEntity.shortCode.eq(Template.NACHWEISBRIEF_PT)
          .and(qMasterListValueHistoryEntity.tenant.id
            .eq(usersService.getUserById(getUserId()).getTenant().getId())))
        .fetchOne();

    // iterate the offers in order to get the needed data
    for (OfferEntity offerEntity : offerEntities) {
      AwardInfoOfferFirstLevelDTO awardInfoOfferDTO =
        AwardInfoOfferFirstLevelDTOMapper.INSTANCE.toAwardInfoOfferFirstLevelDTO(offerEntity);

      // iterate the submittents in order to find the submittent of this offer,
      // in order to get the fields existsExclusionReasons and formalExaminationFulfilled
      for (SubmittentDTO submittentDTO : submittentDTOs) {
        if (offerEntity.getSubmittent().getId().equals(submittentDTO.getId())) {
          awardInfoOfferDTO.setExistsExclusionReasons(submittentDTO.getExistsExclusionReasons());
          awardInfoOfferDTO
            .setFormalExaminationFulfilled(submittentDTO.getFormalExaminationFulfilled());
          break;
        }
      }

      // iterate the offerCriteria in order to check if at least one of them is not fulfilled
      // if no criteria exist, then set field to null
      Boolean isFulfilled = null;
      for (OfferCriterionEntity offerCriteria : offerEntity.getOfferCriteria()) {
        if (offerCriteria.getCriterion().getCriterionType()
          .equals(LookupValues.MUST_CRITERION_TYPE)) {
          if (offerCriteria.getIsFulfilled() != null && !offerCriteria.getIsFulfilled()) {
            isFulfilled = false;
            break;
          } else {
            isFulfilled = true;
          }
        }
      }
      // then set the according flag
      awardInfoOfferDTO.setMustCriteriaFulfilled(isFulfilled);

      // construct the name of the submittent
      awardInfoOfferDTO.setSubmittentName(getSubmittentName(offerEntity.getSubmittent()));

      // construnct the exclusion reason only in case of create
      if (caseCreate) {
        String exclusionReason = null;

        // if Rechtliches Gehor exists, then take the reason from there
        // iterate the list of legalHearingExclusionEntities and
        // get the last created for the current submittent
        Date createdOn = null;
        String reason = null;
        boolean found = false;
        for (LegalHearingExclusionEntity legalHearingExclusion : legalHearingExclusionList) {
          if (legalHearingExclusion.getSubmittent().getId()
            .equals(offerEntity.getSubmittent().getId())
            && (createdOn == null || (legalHearingExclusion.getCreatedOn() != null
            && createdOn.before(legalHearingExclusion.getCreatedOn())))) {
            createdOn = legalHearingExclusion.getCreatedOn();
            reason = legalHearingExclusion.getExclusionReason();
            found = true;
          }
        }

        if (found) {
          exclusionReason = reason;
          // else if Nachweise erbracht is No then construct a text
        } else if (!awardInfoOfferDTO.getFormalExaminationFulfilled()) {
          exclusionReason = LookupValues.EXCLUSION_REASON;
          exclusionReason = exclusionReason.replace("&1",
            offerEntity.getSubmittent().getCompanyId().getCompanyName());
          exclusionReason = exclusionReason.replace("&2",
            convertToSwissDateIfNotNull(offerEntity.getApplicationDate()));

          // two of the informations needed are taken from the Nachweisbrief document created for
          // the submittent
          HashMap<String, String> attributesMap = new HashMap<>();
          attributesMap.put(DocumentAttributes.TEMPLATE_ID.name(),
            templateId.getMasterListValueId().getId());
          attributesMap.put(DocumentAttributes.TENDER_ID.name(),
            offerEntity.getSubmittent().getId());
          List<NodeDTO> nodes = documentService.getNodeByAttributes(submissionId, attributesMap);
          // for each node (actually only one must exist)
          for (NodeDTO node : nodes) {
            VersionDTO latestVersionDTO = versionService.getFileLatestVersion(node.getId());
            // get the creation date of the document
            exclusionReason = exclusionReason.replace("&3",
              convertToSwissDateIfNotNull(new Date(latestVersionDTO.getCreatedOn())));
            // the deadline is stored in another table per version id
            Date deadline = new JPAQueryFactory(em).select(qDocumentDeadlineEntity.deadline)
              .from(qDocumentDeadlineEntity)
              .where(qDocumentDeadlineEntity.versionId.eq(latestVersionDTO.getId())).fetchOne();
            exclusionReason = exclusionReason.replace("&4", convertToSwissDateIfNotNull(deadline));
          }
        }
        awardInfoOfferDTO.setExclusionReasonFirstLevel(exclusionReason);
      }

      // add to the list of offers
      awardInfoOfferDTOs.add(awardInfoOfferDTO);
    }

    // add the list of offers to the data to return
    awardInfoDTO.setOffers(awardInfoOfferDTOs);

    return awardInfoDTO;
  }


  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SubmissionCloseService#saveAwardInfoFirstLevel(ch.
   * bern.submiss.services.api.dto.AwardInfoFirstLevelDTO)
   */
  @Override
  public void saveAwardInfoFirstLevel(AwardInfoFirstLevelDTO awardInfoDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method saveAwardInfoFirstLevel, Parameters: awardInfoDTO: {0}",
      awardInfoDTO);

    SubmissionAwardInfoEntity submissionAwardInfoEntity;
    boolean caseCreate = false;
    if (awardInfoDTO.getId() != null) { // case update
      submissionAwardInfoEntity = em.find(SubmissionAwardInfoEntity.class, awardInfoDTO.getId());
    } else { // case create
      submissionAwardInfoEntity = new SubmissionAwardInfoEntity();
      SubmissionEntity submissionEntity = new SubmissionEntity();
      submissionEntity.setId(awardInfoDTO.getSubmission().getId());
      submissionAwardInfoEntity.setSubmission(submissionEntity);
      submissionAwardInfoEntity.setLevel(LookupValues.Level.FIRST.getValue());
      caseCreate = true;
    }

    submissionAwardInfoEntity.setAvailableDate(awardInfoDTO.getAvailableDate());
    submissionAwardInfoEntity.setObjectNameRead(awardInfoDTO.getObjectNameRead());
    submissionAwardInfoEntity.setProjectNameRead(awardInfoDTO.getProjectNameRead());
    submissionAwardInfoEntity.setWorkingClassRead(awardInfoDTO.getWorkingClassRead());
    submissionAwardInfoEntity.setDescriptionRead(awardInfoDTO.getDescriptionRead());
    submissionAwardInfoEntity.setReason(awardInfoDTO.getReason());

    String userId = getUserId();
    Date now = new Date();
    submissionAwardInfoEntity.setUpdatedBy(userId);
    submissionAwardInfoEntity.setUpdatedOn(now);
    if (caseCreate) { // case create
      submissionAwardInfoEntity.setCreatedBy(userId);
      submissionAwardInfoEntity.setCreatedOn(now);
      em.persist(submissionAwardInfoEntity);
    } else { // case update
      em.merge(submissionAwardInfoEntity);
    }

    // iterate the offers in order to update the needed data
    for (AwardInfoOfferFirstLevelDTO awardInfoOfferDTO : awardInfoDTO.getOffers()) {
      // get the offer entity
      OfferEntity offerEntity = em.find(OfferEntity.class, awardInfoOfferDTO.getId());

      // set the updated fields
      offerEntity.setExclusionReasonFirstLevel(awardInfoOfferDTO.getExclusionReasonFirstLevel());
      Set<MasterListValueEntity> set =
        SubmissionMapper.INSTANCE.masterListValueDTOSetToMasterListValueEntitySet(
          awardInfoOfferDTO.getExclusionReasonsFirstLevel());
      if (set != null) {
        offerEntity.setExclusionReasonsFirstLevel(set);
      }
      em.merge(offerEntity);
    }
  }

  /**
   * Convert to swiss date if not null, otherwise return empty string.
   *
   * @param date the date
   * @return the string
   */
  private String convertToSwissDateIfNotNull(Date date) {
    return date != null ? SubmissConverter.convertToSwissDate(date) : "";
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SubmissionCloseService#
   * getSubmissionsNoPublicationAwardDate()
   */
  @Override
  public List<SubmissionDTO> getSubmissionsNoPublicationAwardDate() {

    LOGGER.log(Level.CONFIG, "Executing method getSubmissionsNoPublicationAwardDate");

    List<SubmissionEntity> submissionEntityList =
      new JPAQueryFactory(em).select(entity).from(entity)
        .where(entity.gattTwo.isTrue().and(entity.publicationDateAward.isNull())
          .and(entity.status.in(TenderStatus.AWARD_NOTICES_CREATED.getValue(),
            TenderStatus.CONTRACT_CREATED.getValue()))
          // check if the current status (or the one before the current in case current status
          // is CONTRACT_CREATED)
          // is AWARD_NOTICES_CREATED and is set more than 20 days ago
          .and(entity.id.in(JPAExpressions.select(qTenderStatusHistoryEntity.tenderId.id)
            .from(qTenderStatusHistoryEntity)
            .where(qTenderStatusHistoryEntity.statusId
              .eq(TenderStatus.AWARD_NOTICES_CREATED.getValue())
              .and(qTenderStatusHistoryEntity.onDate.loe(Timestamp.valueOf(
                LocalDate.now().minusDays(LookupValues.TWENTY).atStartOfDay())))
              .and(qTenderStatusHistoryEntity.onDate
                .in(JPAExpressions.select(qTenderStatusHistoryEntity1.onDate.max())
                  .from(qTenderStatusHistoryEntity1)
                  .where(qTenderStatusHistoryEntity1.tenderId
                    .eq(qTenderStatusHistoryEntity.tenderId)
                    .and(qTenderStatusHistoryEntity1.statusId
                      .ne(TenderStatus.CONTRACT_CREATED.getValue())))))))))
        .fetch();
    return SubmissionMapper.INSTANCE.toSubmissionDTOList(submissionEntityList);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SubmissionCloseService#getSubmissionsNotClosed()
   */
  @Override
  public List<SubmissionDTO> getSubmissionsNotClosed() {

    LOGGER.log(Level.CONFIG, "Executing method getSubmissionsNotClosed");

    List<SubmissionEntity> submissionEntityList = new JPAQueryFactory(em)
      .select(qTenderStatusHistoryEntity.tenderId).from(qTenderStatusHistoryEntity)
      .where(qTenderStatusHistoryEntity.statusId
        .notIn(TenderStatus.AWARD_NOTICES_CREATED.getValue(), TenderStatus.CONTRACT_CREATED
            .getValue(), TenderStatus.PROCEDURE_COMPLETED.getValue(),
          TenderStatus.PROCEDURE_CANCELED.getValue())
        .and(qTenderStatusHistoryEntity.onDate.loe(
          Timestamp.valueOf(LocalDate.now().minusMonths(LookupValues.SIX).atStartOfDay())))
        .and(qTenderStatusHistoryEntity.onDate.in(JPAExpressions
          .select(qTenderStatusHistoryEntity1.onDate.max()).from(qTenderStatusHistoryEntity1)
          .where(
            qTenderStatusHistoryEntity1.tenderId.eq(qTenderStatusHistoryEntity.tenderId)))))
      .fetch();
    return SubmissionMapper.INSTANCE.toSubmissionDTOList(submissionEntityList);
  }
}
