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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ch.bern.submiss.services.api.administration.SDCountryService;
import ch.bern.submiss.services.api.constants.AuditEvent;
import ch.bern.submiss.services.api.constants.AuditGroupName;
import ch.bern.submiss.services.api.constants.AuditLevel;
import ch.bern.submiss.services.api.constants.AuditMessages;
import ch.bern.submiss.services.api.dto.CountryHistoryDTO;
import ch.bern.submiss.services.api.dto.MasterListTypeDataDTO;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.impl.mappers.CountryHistoryMapper;
import ch.bern.submiss.services.impl.mappers.CountryToTypeDataMapper;
import ch.bern.submiss.services.impl.model.CountryEntity;
import ch.bern.submiss.services.impl.model.CountryHistoryEntity;
import ch.bern.submiss.services.impl.model.QCountryHistoryEntity;

/**
 * The Class SDCountryServiceImpl.
 */
@Transactional
@OsgiServiceProvider(classes = {SDCountryService.class})
@Singleton
public class SDCountryServiceImpl extends BaseService implements SDCountryService {

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER = Logger.getLogger(SDCountryServiceImpl.class.getName());
  /**
   * The q country history entity.
   */
  QCountryHistoryEntity qCountryHistoryEntity = QCountryHistoryEntity.countryHistoryEntity;
  @Inject
  private AuditBean audit;

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SDCountryService#readAll()
   */
  @Override
  public List<CountryHistoryDTO> readAll() {

    LOGGER.log(Level.CONFIG, "Executing method readAll");

    JPAQuery<CountryHistoryEntity> query = new JPAQuery<>(em);

    /**
     * Query to take a list with all countries sorted by name.
     */
    List<CountryHistoryEntity> countryEntities = query.select(qCountryHistoryEntity)
      .from(qCountryHistoryEntity).where(qCountryHistoryEntity.toDate.isNull())
      .orderBy(qCountryHistoryEntity.countryName.asc()).fetch();

    /**
     * Country Schweiz must be shown first in the list: So make a loop and search for the country
     * with the specific id which belongs to Schweiz. When the id is the same, hold the current
     * position of the list (countryIndex).
     */
    int countryIndex = 0;
    for (CountryHistoryEntity country : countryEntities) {

      if (country.getCountryId().getId().equals("51c584d3-ae23-4140-96e0-5ea9079c3c73")) {
        break;
      }
      countryIndex++;
    }

    /**
     * Firstly save the object from this position in a temporary object and remove it from the list.
     * And then add the temporary object in the first position and return the list.
     */
    CountryHistoryEntity tempCountry = countryEntities.get(countryIndex);
    countryEntities.remove(countryIndex);
    countryEntities.add(0, tempCountry);

    return CountryHistoryMapper.INSTANCE.toCountryHistoryDTO(countryEntities);
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SDCountryService#getCountryHistoryDTOs()
   */
  @Override
  public List<CountryHistoryDTO> getCountryHistoryDTOs() {

    LOGGER.log(Level.CONFIG, "Executing method getCountryHistoryDTOs");

    return (CountryHistoryMapper.INSTANCE.toCountryHistoryDTO(new JPAQueryFactory(em)
      .selectFrom(qCountryHistoryEntity).where(qCountryHistoryEntity.toDate.isNull()).fetch()));
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SDCountryService#countryToTypeData()
   */
  @Override
  public List<MasterListTypeDataDTO> countryToTypeData() {

    LOGGER.log(Level.CONFIG, "Executing method countryToTypeData");

    List<MasterListTypeDataDTO> typeDTOs;
    // Retrieving country history data.
    List<CountryHistoryEntity> countryHistoryEntities = new JPAQueryFactory(em)
      .selectFrom(qCountryHistoryEntity).where(qCountryHistoryEntity.toDate.isNull()).fetch();
    // Mapping country data to master list type data.
    typeDTOs = CountryToTypeDataMapper.INSTANCE.toMasterListTypeDataDTOs(countryHistoryEntities);
    return typeDTOs;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SDCountryService#getCountryEntryById(java.lang.
   * String)
   */
  @Override
  public CountryHistoryDTO getCountryEntryById(String entryId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getCountryEntryById, Parameters: entryId: {0}",
      entryId);

    return CountryHistoryMapper.INSTANCE.toCountryHistoryDTO(new JPAQueryFactory(em)
      .selectFrom(qCountryHistoryEntity).where(qCountryHistoryEntity.id.eq(entryId)).fetchOne());
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SDCountryService#isCountryNameUnique(java.lang.
   * String, java.lang.String)
   */
  @Override
  public boolean isCountryNameUnique(String countryName, String id) {

    LOGGER.log(Level.CONFIG,
      "Executing method isCountryNameUnique, Parameters: countryName: {0}, "
        + "id: {1}",
      new Object[]{countryName, id});

    if (id == null) {
      // If the id is null (case of creating a new entry), replace the null value with an empty
      // String in order to avoid the null pointer exception.
      id = StringUtils.EMPTY;
    }
    return (new JPAQueryFactory(em).select(qCountryHistoryEntity).from(qCountryHistoryEntity)
      .where(qCountryHistoryEntity.toDate.isNull(),
        qCountryHistoryEntity.id.notEqualsIgnoreCase(id),
        qCountryHistoryEntity.countryName.eq(countryName))
      .fetchCount() == 0);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SDCountryService#saveCountryEntry(ch.bern.submiss.
   * services.api.dto.CountryHistoryDTO)
   */
  @Override
  public void saveCountryEntry(CountryHistoryDTO countryHistoryDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method saveCountryEntry, Parameters: countryHistoryDTO: {0}",
      countryHistoryDTO);

    CountryHistoryEntity countryHistEntity;
    // Check if an old entry is being updated or a new entry is created.
    if (StringUtils.isBlank(countryHistoryDTO.getId())) {
      // Creating a new entry.
      countryHistEntity = CountryHistoryMapper.INSTANCE.toCountryHistory(countryHistoryDTO);
      // Set current date to fromDate property.
      countryHistEntity.setFromDate(new Timestamp(new Date().getTime()));
      // Creating a new country entity.
      CountryEntity countryEntity = new CountryEntity();
      em.persist(countryEntity);
      // Assign the country entity to the country history entity.
      countryHistEntity.setCountryId(countryEntity);

      auditLog(countryEntity.getId(), AuditEvent.CREATE.name(), AuditMessages.COUNTRY_ADDED.name());
    } else {
      // In case of updating an old entry, find the entry and set the current date to the toDate
      // property.
      countryHistEntity = em.find(CountryHistoryEntity.class, countryHistoryDTO.getId());
      countryHistEntity.setToDate(new Timestamp(new Date().getTime()));
      em.merge(countryHistEntity);
      // Now that the old entry is added to the history, create its new instance.
      countryHistEntity = CountryHistoryMapper.INSTANCE.toCountryHistory(countryHistoryDTO);
      // The entry is going to have a new id.
      countryHistEntity.setId(null);
      // Set current date to fromDate property.
      countryHistEntity.setFromDate(new Timestamp(new Date().getTime()));
      // Set null to toDate property.
      countryHistEntity.setToDate(null);

      auditLog(countryHistEntity.getCountryId().getId(), AuditEvent.UPDATE.name(),
        AuditMessages.COUNTRY_UPDATED.name());
    }
    // Save the new entry.
    em.persist(countryHistEntity);
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

  @Override
  public Map<String, CountryHistoryDTO> mapCountryIdsToCountryHistoryDTOs() {
    LOGGER.log(Level.CONFIG, "Executing method mapCountryIdsToCountryHistoryDTOs");

    // Get all the latest country history DTOs.
    List<CountryHistoryDTO> countryHistoryDTOs = CountryHistoryMapper.INSTANCE
        .toCountryHistoryDTO(new JPAQueryFactory(em).selectFrom(qCountryHistoryEntity)
            .where(qCountryHistoryEntity.toDate.isNull()).fetch());

    Map<String, CountryHistoryDTO> countryMapper = new HashMap<>();
    // Map the country ids to the country history DTOs.
    for (CountryHistoryDTO countryHistoryDTO : countryHistoryDTOs) {
      countryMapper.put(countryHistoryDTO.getCountryId().getId(), countryHistoryDTO);
    }
    return countryMapper;
  }
}
