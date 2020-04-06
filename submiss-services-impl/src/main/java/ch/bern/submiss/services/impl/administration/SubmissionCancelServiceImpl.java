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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;

import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import com.eurodyn.qlack2.fuse.cm.api.DocumentService;
import com.querydsl.jpa.impl.JPAQueryFactory;

import ch.bern.submiss.services.api.administration.LegalHearingService;
import ch.bern.submiss.services.api.administration.SubmissionCancelService;
import ch.bern.submiss.services.api.administration.UserAdministrationService;
import ch.bern.submiss.services.api.constants.DocumentAttributes;
import ch.bern.submiss.services.api.constants.Template;
import ch.bern.submiss.services.api.dto.LegalHearingTerminateDTO;
import ch.bern.submiss.services.api.dto.SubmissionCancelDTO;
import ch.bern.submiss.services.impl.mappers.SubmissionCancelDTOMapper;
import ch.bern.submiss.services.impl.model.MasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.QMasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.QSubmissionCancelEntity;
import ch.bern.submiss.services.impl.model.SubmissionCancelEntity;
import ch.bern.submiss.services.impl.model.SubmissionEntity;
import ch.bern.submiss.services.impl.model.SubmittentEntity;

/**
 * The Class SubmissionCancelServiceImpl.
 */
@Transactional
@OsgiServiceProvider(classes = {SubmissionCancelService.class})
@Singleton
public class SubmissionCancelServiceImpl extends BaseService implements SubmissionCancelService {

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER =
    Logger.getLogger(SubmissionCancelServiceImpl.class.getName());
  /**
   * The users service.
   */
  @Inject
  protected UserAdministrationService usersService;
  /**
   * The q submission cancel entity.
   */
  QSubmissionCancelEntity qSubmissionCancelEntity = QSubmissionCancelEntity.submissionCancelEntity;
  /**
   * The q master list value history entity.
   */
  QMasterListValueHistoryEntity qMasterListValueHistoryEntity =
    QMasterListValueHistoryEntity.masterListValueHistoryEntity;
  /**
   * The legal hearing service.
   */
  @Inject
  private LegalHearingService legalHearingService;
  /**
   * The document service.
   */
  @Inject
  private DocumentService documentService;

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SubmissionCancelService#getBySubmissionId(java.lang
   * .String)
   */
  @Override
  public SubmissionCancelDTO getBySubmissionId(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getBySubmissionId, Parameters: submissionId: {0}",
      submissionId);

    SubmissionCancelEntity submissionCancelEntity =
      new JPAQueryFactory(em).select(qSubmissionCancelEntity).from(qSubmissionCancelEntity)
        .where(qSubmissionCancelEntity.submission.id.eq(submissionId)).fetchOne();

    SubmissionCancelDTO submissionCancelDTO;
    if (submissionCancelEntity != null) {
      submissionCancelDTO =
        SubmissionCancelDTOMapper.INSTANCE.toSubmissionCancelDTO(submissionCancelEntity);
    } else {
      submissionCancelDTO = new SubmissionCancelDTO();

      // case create, take values from Rechtliches Gehör
      LegalHearingTerminateDTO legalHearingTerminateDTO =
        legalHearingService.getSubmissionLegalHearingTermination(submissionId);

      if (legalHearingTerminateDTO != null) {
        submissionCancelDTO.setWorkTypes(legalHearingTerminateDTO.getTerminationReason());
        submissionCancelDTO.setReason(legalHearingTerminateDTO.getReason());
      }
    }

    return submissionCancelDTO;
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SubmissionCancelService#set(ch.bern.submiss.
   * services.api.dto.SubmissionCancelDTO)
   */
  @Override
  public String set(SubmissionCancelDTO submissionCancel) {

    LOGGER.log(Level.CONFIG,
      "Executing method set, Parameters: submissionCancel: {0}",
      submissionCancel);

    SubmissionCancelEntity submissionCancelEntity =
      SubmissionCancelDTOMapper.INSTANCE.toSubmissionCancel(submissionCancel);

    String userId = getUserId();
    Date now = new Date();
    submissionCancelEntity.setUpdatedBy(userId);
    submissionCancelEntity.setUpdatedOn(now);
    if (submissionCancelEntity.getId() == null) { // case create
      submissionCancelEntity.setCreatedBy(userId);
      submissionCancelEntity.setCreatedOn(now);
      em.persist(submissionCancelEntity);
    } else { // case update
      // in order to check if the timer for the automatic closure of the submission needs
      // to be updated we need to perform a query in the database to check the value of the
      // freeze flag before the update.
      SubmissionCancelEntity submissionCancelEntityBefore =
        new JPAQueryFactory(em).select(qSubmissionCancelEntity).from(qSubmissionCancelEntity)
          .where(qSubmissionCancelEntity.id.eq(submissionCancel.getId())).fetchOne();

      // if the freeze flag was false before and now true, then set the timer to null
      // if the freeze flag was false before and now false, then leave the timer as is
      // if the freeze flag was true before and now true, then leave the timer as is (it will be
      // null)
      // if the freeze flag was true before and now false, then initiate the timer
      if ((submissionCancelEntityBefore.getFreezeCloseSubmission() == null
        || !submissionCancelEntityBefore.getFreezeCloseSubmission())
        && (submissionCancel.getFreezeCloseSubmission() != null
        && submissionCancel.getFreezeCloseSubmission())) {
        submissionCancelEntity.setCloseCountdownStart(null);
      } else if ((submissionCancelEntityBefore.getFreezeCloseSubmission() != null
        && submissionCancelEntityBefore.getFreezeCloseSubmission())
        && (submissionCancel.getFreezeCloseSubmission() == null
        || !submissionCancel.getFreezeCloseSubmission())) {
        submissionCancelEntity.setCloseCountdownStart(now);
      } else {
        // otherwise keep the value that has been set before
        submissionCancelEntity
          .setCloseCountdownStart(submissionCancelEntityBefore.getCloseCountdownStart());
      }
      em.merge(submissionCancelEntity);
    }
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SubmissionCancelService#
   * getAvailableDateBySubmissionId(java.lang.String)
   */
  @Override
  public Date getAvailableDateBySubmissionId(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getAvailableDateBySubmissionId, Parameters: submissionId: {0}",
      submissionId);

    return new JPAQueryFactory(em).select(qSubmissionCancelEntity.availableDate)
      .from(qSubmissionCancelEntity).where(qSubmissionCancelEntity.submission.id.eq(submissionId))
      .fetchOne();
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SubmissionCancelService#cancellationDocumentCreated
   * (java.lang.String)
   */
  @Override
  public boolean cancellationDocumentCreated(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method cancellationDocumentCreated, Parameters: submissionId: {0}",
      submissionId);

    // Checks if the Verfahrensabbruch document has been generated for all tenderers.
    HashMap<String, String> attributesMap = new HashMap<>();
    SubmissionEntity submissionEntity = em.find(SubmissionEntity.class, submissionId);
    MasterListValueHistoryEntity templateId = new JPAQueryFactory(em)
      .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
      .where(qMasterListValueHistoryEntity.shortCode.eq(Template.VERFAHRENSABBRUCH)
        .and(qMasterListValueHistoryEntity.tenant.id
          .eq(usersService.getUserById(getUserId()).getTenant().getId())))
      .fetchOne();
    attributesMap.put(DocumentAttributes.TENANT_ID.name(), templateId.getTenant().getId());
    attributesMap.put(DocumentAttributes.TEMPLATE_ID.name(),
      templateId.getMasterListValueId().getId());
    for (SubmittentEntity submittentEntity : submissionEntity.getSubmittents()) {
      attributesMap.put(DocumentAttributes.TENDER_ID.name(), submittentEntity.getId());
      if (documentService.getNodeByAttributes(submissionEntity.getId(), attributesMap).isEmpty()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public List<String> getSubmissionIdsByCancellationReasonIds(List<String> cancellationReasonIds) {

    LOGGER.log(Level.CONFIG,
      "Executing method getSubmissionIdsByCancellationReasonIds, Parameters: "
        + "cancellationReasonIds: {0}",
      cancellationReasonIds);

    return new JPAQueryFactory(em).select(qSubmissionCancelEntity.submission.id)
      .from(qSubmissionCancelEntity)
      .where(qSubmissionCancelEntity.workTypes.any().id.in(cancellationReasonIds)).fetch();
  }
}
