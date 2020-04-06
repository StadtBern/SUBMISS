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

import ch.bern.submiss.services.api.administration.TenderStatusHistoryService;
import ch.bern.submiss.services.api.constants.TenderStatus;
import ch.bern.submiss.services.api.dto.TenderStatusHistoryDTO;
import ch.bern.submiss.services.impl.mappers.TenderStatusHistoryDTOMapper;
import ch.bern.submiss.services.impl.model.QTenderStatusHistoryEntity;
import ch.bern.submiss.services.impl.model.TenderStatusHistoryEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

/**
 * The Class TenderStatusHistoryServiceImpl.
 */
@Transactional
@OsgiServiceProvider(classes = {TenderStatusHistoryService.class})
@Singleton
public class TenderStatusHistoryServiceImpl extends BaseService
  implements TenderStatusHistoryService {

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER =
    Logger.getLogger(TenderStatusHistoryServiceImpl.class.getName());

  /**
   * The q tender status history entity.
   */
  QTenderStatusHistoryEntity qTenderStatusHistoryEntity =
    QTenderStatusHistoryEntity.tenderStatusHistoryEntity;

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.TenderStatusHistoryService#getSubmissionStatuses(
   * java.lang.String)
   */
  @Override
  public List<TenderStatusHistoryDTO> getSubmissionStatuses(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getSubmissionStatuses, Parameters: submissionId: {0}",
      submissionId);

    // Get the submission creation date.
    Timestamp submissionCreationDate = new JPAQueryFactory(em)
      .select(qTenderStatusHistoryEntity.onDate.max()).from(qTenderStatusHistoryEntity)
      .where(qTenderStatusHistoryEntity.statusId
        .eq(TenderStatus.SUBMISSION_CREATED.getValue())
        .and(qTenderStatusHistoryEntity.tenderId.id.eq(submissionId))).fetchOne();

    // Get all the status entries from the submission (tender) status history, until the first
    // occurrence of the status SUBMISSION_CREATED.
    List<TenderStatusHistoryEntity> statuses = new JPAQueryFactory(em)
      .select(qTenderStatusHistoryEntity).from(qTenderStatusHistoryEntity)
      .where(qTenderStatusHistoryEntity.tenderId.id.eq(submissionId)
        .and(qTenderStatusHistoryEntity.onDate.gt(submissionCreationDate)))
      .orderBy(qTenderStatusHistoryEntity.onDate.desc()).fetch();
    return TenderStatusHistoryDTOMapper.INSTANCE.toTenderStatusHistoryDTO(statuses);
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.TenderStatusHistoryService#
   * retrieveSubmissionSpecificStatuses(java.lang.String, java.lang.String)
   */
  @Override
  public List<TenderStatusHistoryDTO> retrieveSubmissionSpecificStatuses(String submissionId,
    String status) {

    LOGGER.log(Level.CONFIG,
      "Executing method retrieveSubmissionSpecificStatuses, Parameters: submissionId: {0}, "
        + "status: {1}",
      new Object[]{submissionId, status});

    return TenderStatusHistoryDTOMapper.INSTANCE
      .toTenderStatusHistoryDTO(new JPAQueryFactory(em).select(qTenderStatusHistoryEntity)
        .from(qTenderStatusHistoryEntity).where(qTenderStatusHistoryEntity.tenderId.id
          .eq(submissionId).and(qTenderStatusHistoryEntity.statusId.eq(status)))
        .fetch());
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.TenderStatusHistoryService#getAll()
   */
  @Override
  public List<TenderStatusHistoryDTO> getAll() {

    LOGGER.log(Level.CONFIG, "Executing method getAll");

    return TenderStatusHistoryDTOMapper.INSTANCE.toTenderStatusHistoryDTO(new JPAQueryFactory(em)
      .select(qTenderStatusHistoryEntity).from(qTenderStatusHistoryEntity).fetch());
  }

  @Override
  public String getSubmissionStatusBeforeCancel(String submissionId) {
    return new JPAQueryFactory(em)
      .select(qTenderStatusHistoryEntity.statusId).from(qTenderStatusHistoryEntity)
      .where(qTenderStatusHistoryEntity.tenderId.id.eq(submissionId).and(
        qTenderStatusHistoryEntity.statusId.ne(TenderStatus.PROCEDURE_CANCELED.getValue())))
      .orderBy(qTenderStatusHistoryEntity.onDate.desc()).fetchFirst();
  }
}
