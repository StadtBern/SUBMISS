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

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;

import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import ch.bern.submiss.services.api.administration.SDLogibService;
import ch.bern.submiss.services.api.constants.AuditEvent;
import ch.bern.submiss.services.api.constants.AuditGroupName;
import ch.bern.submiss.services.api.constants.AuditLevel;
import ch.bern.submiss.services.api.dto.LogibHistoryDTO;
import ch.bern.submiss.services.api.dto.MasterListTypeDataDTO;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.impl.mappers.LogibHistoryDTOMapper;
import ch.bern.submiss.services.impl.mappers.LogibToTypeDataMapper;
import ch.bern.submiss.services.impl.model.LogibHistoryEntity;
import ch.bern.submiss.services.impl.model.QLogibHistoryEntity;

/**
 * The Class SDLogibServiceImpl.
 */
@Transactional
@OsgiServiceProvider(classes = {SDLogibService.class})
@Singleton
public class SDLogibServiceImpl extends BaseService implements SDLogibService {

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER = Logger.getLogger(SDLogibServiceImpl.class.getName());
  /**
   * The q logib history entity.
   */
  QLogibHistoryEntity qLogibHistoryEntity = QLogibHistoryEntity.logibHistoryEntity;
  @Inject
  private AuditBean audit;

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SDLogibService#readLogib()
   */
  @Override
  public LogibHistoryDTO readLogib() {

    LOGGER.log(Level.CONFIG, "Executing method readLogib");

    JPAQuery<LogibHistoryEntity> query = new JPAQuery<>(em);
    LogibHistoryEntity logibEntity = query.select(qLogibHistoryEntity).from(qLogibHistoryEntity)
      .where(qLogibHistoryEntity.name.eq("Logib").and(qLogibHistoryEntity.toDate.isNull()))
      .fetchOne();

    return LogibHistoryDTOMapper.INSTANCE.toLogibHistoryDTO(logibEntity);
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SDLogibService#readLogibArgib()
   */
  @Override
  public LogibHistoryDTO readLogibArgib() {

    LOGGER.log(Level.CONFIG, "Executing method readLogibArgib");

    JPAQuery<LogibHistoryEntity> query = new JPAQuery<>(em);
    LogibHistoryEntity logibArgibEntity =
      query.select(qLogibHistoryEntity).from(qLogibHistoryEntity)
        .where(
          qLogibHistoryEntity.name.eq("LogibARGIB").and(qLogibHistoryEntity.toDate.isNull()))
        .fetchOne();

    return LogibHistoryDTOMapper.INSTANCE.toLogibHistoryDTO(logibArgibEntity);
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SDLogibService#logibToTypeData()
   */
  @Override
  public List<MasterListTypeDataDTO> logibToTypeData() {

    LOGGER.log(Level.CONFIG, "Executing method logibToTypeData");

    List<MasterListTypeDataDTO> typeDTOs;
    // Retrieving logib history data.
    List<LogibHistoryEntity> logibHistoryEntities =
      new JPAQueryFactory(em).selectFrom(qLogibHistoryEntity)
        .where(qLogibHistoryEntity.toDate.isNull()
          .and(qLogibHistoryEntity.tenant.id.in(security.getPermittedTenants(getUser()))))
        .fetch();
    // Mapping logib data to master list type data.
    typeDTOs = LogibToTypeDataMapper.INSTANCE.toMasterListTypeDataDTOs(logibHistoryEntities);
    return typeDTOs;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SDLogibService#getLogibEntryById(java.lang.String)
   */
  @Override
  public LogibHistoryDTO getLogibEntryById(String entryId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getLogibEntryById, Parameters: entryId: {0}",
      entryId);

    return LogibHistoryDTOMapper.INSTANCE.toLogibHistoryDTO(new JPAQueryFactory(em)
      .selectFrom(qLogibHistoryEntity).where(qLogibHistoryEntity.id.eq(entryId)).fetchOne());
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SDLogibService#saveLogibEntry(ch.bern.submiss.
   * services.api.dto.LogibHistoryDTO)
   */
  @Override
  public void saveLogibEntry(LogibHistoryDTO logibHistoryDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method saveLogibEntry, Parameters: logibHistoryDTO: {0}",
      logibHistoryDTO);

    // Find the entry and set the current date to the toDate property.
    LogibHistoryEntity logibHistEntity = em.find(LogibHistoryEntity.class, logibHistoryDTO.getId());
    logibHistEntity.setToDate(new Timestamp(new Date().getTime()));
    em.merge(logibHistEntity);
    // Now that the old entry is added to the history, create its new instance.
    logibHistEntity = LogibHistoryDTOMapper.INSTANCE.toLogibHistory(logibHistoryDTO);
    // The entry is going to have a new id.
    logibHistEntity.setId(null);
    // Set current date to fromDate property.
    logibHistEntity.setFromDate(new Timestamp(new Date().getTime()));
    // Set null to toDate property.
    logibHistEntity.setToDate(null);
    // Save the new entry.
    em.persist(logibHistEntity);

    auditLog(logibHistEntity.getLogibId().getId(), AuditEvent.UPDATE.name(), null);
  }

  /**
   * Audit log.
   *
   * @param id the id
   * @param event the event
   * @param message the message
   */
  private void auditLog(String id, String event, String message) {
    audit.createLogAudit(AuditLevel.REFERENCE_DATA_LEVEL.name(), event,
      AuditGroupName.REFERENCE_DATA.name(), message, getUser().getId(),
      id, null, null, LookupValues.INTERNAL_LOG);
  }

}
