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

import ch.bern.submiss.services.api.administration.SDDirectorateService;
import ch.bern.submiss.services.api.administration.UserAdministrationService;
import ch.bern.submiss.services.api.constants.AuditEvent;
import ch.bern.submiss.services.api.constants.AuditGroupName;
import ch.bern.submiss.services.api.constants.AuditLevel;
import ch.bern.submiss.services.api.dto.DirectorateHistoryDTO;
import ch.bern.submiss.services.api.dto.MasterListTypeDataDTO;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.api.util.ValidationMessages;
import ch.bern.submiss.services.impl.mappers.DirectorateHistoryMapper;
import ch.bern.submiss.services.impl.mappers.DirectorateToTypeDataMapper;
import ch.bern.submiss.services.impl.mappers.TenantMapper;
import ch.bern.submiss.services.impl.model.DirectorateEntity;
import ch.bern.submiss.services.impl.model.DirectorateHistoryEntity;
import ch.bern.submiss.services.impl.model.ProjectEntity;
import ch.bern.submiss.services.impl.model.QDepartmentHistoryEntity;
import ch.bern.submiss.services.impl.model.QDirectorateEntity;
import ch.bern.submiss.services.impl.model.QDirectorateHistoryEntity;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.sql.Timestamp;
import java.util.Date;
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
 * The Class SDDirectorateServiceImpl.
 */
@Transactional
@OsgiServiceProvider(classes = {SDDirectorateService.class})
@Singleton
public class SDDirectorateServiceImpl extends BaseService implements SDDirectorateService {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = Logger.getLogger(SDDirectorateServiceImpl.class.getName());

  /** The users service. */
  @Inject
  private UserAdministrationService usersService;

  /** The cache bean. */
  @Inject
  private CacheBean cacheBean;
  
  @Inject
  private AuditBean audit;

  /** The directorate history entity. */
  QDirectorateHistoryEntity directorateHistoryEntity =
      QDirectorateHistoryEntity.directorateHistoryEntity;
  
  QDirectorateEntity directorateEntity =
	      QDirectorateEntity.directorateEntity;

  @Override
  public List<DirectorateHistoryDTO> getAllPermittedDirectorates() {

    LOGGER.log(Level.CONFIG, "Executing method getAllPermittedDirectorates");

    JPAQuery<DirectorateHistoryEntity> query = new JPAQuery<>(em);

    List<DirectorateHistoryEntity> directorateHistoryEntities =
      query.select(directorateHistoryEntity).from(directorateHistoryEntity)
        .where(directorateHistoryEntity.directorateId.id
          .in(security.getPermittedDirectorates(getUser()))
          .and(directorateHistoryEntity.toDate.isNull())
          .and(directorateHistoryEntity.active.isTrue()))
        .orderBy(directorateHistoryEntity.name.asc()).fetch();

    return DirectorateHistoryMapper.INSTANCE.toDirectorateHistoryDTO(directorateHistoryEntities);
  }

  @Override
  public List<DirectorateHistoryDTO> getDirectoratesByDepartmentIds(List<String> departmentIDs) {

    LOGGER.log(Level.CONFIG,
      "Executing method getDirectoratesByDepartmentIds, Parameters: departmentIDs: {0}",
      departmentIDs);

    JPAQuery<DirectorateEntity> query = new JPAQuery<>(em);
    QDepartmentHistoryEntity departmentHistoryEntity =
      QDepartmentHistoryEntity.departmentHistoryEntity;
    //departmentHistory table change
    QDirectorateEntity directorateEntity =
      QDirectorateEntity.directorateEntity;

    List<String> directorateIds =
      query.select(directorateEntity.id).from(directorateEntity, departmentHistoryEntity)
        .where(departmentHistoryEntity.id.in(departmentIDs),
          departmentHistoryEntity.directorateEnity.id.eq(directorateEntity.id),
          departmentHistoryEntity.toDate.isNull())
        .fetch();

    List<DirectorateHistoryEntity> directorateHistoryEntities = new JPAQueryFactory(em)
      .select(directorateHistoryEntity).from(directorateHistoryEntity)
      .where(directorateHistoryEntity.directorateId.id.in(directorateIds),
        (directorateHistoryEntity.toDate.isNull())).orderBy(directorateHistoryEntity.name.asc())
      .fetch();

    return DirectorateHistoryMapper.INSTANCE.toDirectorateHistoryDTO(directorateHistoryEntities);
  }

  @Override
  public List<DirectorateHistoryDTO> getDirectoratesByIds(List<String> directoratesIDs) {

    LOGGER.log(Level.CONFIG,
      "Executing method getDirectoratesByIds, Parameters: directoratesIDs: {0}",
      directoratesIDs);

    JPAQuery<DirectorateHistoryEntity> query = new JPAQuery<>(em);
    QDepartmentHistoryEntity departmentHistoryEntity =
      QDepartmentHistoryEntity.departmentHistoryEntity;

    List<DirectorateHistoryEntity> directorateHistoryEntities =
      query.select(directorateHistoryEntity).distinct()
        .where(directorateHistoryEntity.id.in(directoratesIDs))
        .from(directorateHistoryEntity, departmentHistoryEntity).fetch();

    return DirectorateHistoryMapper.INSTANCE.toDirectorateHistoryDTO(directorateHistoryEntities);
  }

  @Override
  public List<DirectorateHistoryDTO> getDirectoratesByTenantId(String tenantId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getDirectoratesByTenantId, Parameters: tenantId: {0}",
      tenantId);

    JPAQuery<DirectorateHistoryEntity> query = new JPAQuery<>(em);

    List<DirectorateHistoryEntity> directorateHistoryEntities = query
      .select(directorateHistoryEntity).where(directorateHistoryEntity.tenant.id.eq(tenantId))
      .from(directorateHistoryEntity).fetch();
    return DirectorateHistoryMapper.INSTANCE.toDirectorateHistoryDTO(directorateHistoryEntities);
  }

  @Override
  public List<MasterListTypeDataDTO> directorateToTypeData() {

    LOGGER.log(Level.CONFIG, "Executing method directorateToTypeData");

    List<MasterListTypeDataDTO> typeDTOs;
    // Retrieving directorate history data.
    List<DirectorateHistoryEntity> directorateHistoryEntities =
      new JPAQueryFactory(em).selectFrom(directorateHistoryEntity)
        .where(directorateHistoryEntity.toDate.isNull().and(
          directorateHistoryEntity.tenant.id.in(security.getPermittedTenants(getUser()))))
        .fetch();
    // Mapping directorate data to master list type data.
    typeDTOs =
      DirectorateToTypeDataMapper.INSTANCE.toMasterListTypeDataDTOs(directorateHistoryEntities);
    return typeDTOs;
  }

  @Override
  public DirectorateHistoryDTO getDirectorateEntryById(String entryId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getDirectorateEntryById, Parameters: entryId: {0}",
      entryId);

    return DirectorateHistoryMapper.INSTANCE
      .toDirectorateHistoryDTO(new JPAQueryFactory(em).selectFrom(directorateHistoryEntity)
        .where(directorateHistoryEntity.id.eq(entryId)).fetchOne());
  }

  @Override
  public List<DirectorateHistoryDTO> directorateHistoryQuery(String tenantId) {

    LOGGER.log(Level.CONFIG,
      "Executing method directorateHistoryQuery, Parameters: tenantId: {0}",
      tenantId);

    JPAQuery<ProjectEntity> query = new JPAQuery<>(em);
    BooleanBuilder whereClause = new BooleanBuilder();

    if (!StringUtils.isBlank(tenantId)) {
      whereClause.and(directorateHistoryEntity.tenant.id.eq(tenantId));
    }

    return DirectorateHistoryMapper.INSTANCE.toDirectorateHistoryDTO(
      query.select(directorateHistoryEntity).from(directorateHistoryEntity)
        .where(whereClause, directorateHistoryEntity.toDate.isNull()).fetch());
  }

  @Override
  public boolean isNameUnique(String name, String id) {

    LOGGER.log(Level.CONFIG,
      "Executing method isNameUnique, Parameters: name: {0}, id: {1}",
      new Object[]{name, id});

    if (id == null) {
      // If the id is null (case of creating a new entry), replace the null value with an empty
      // String in order to avoid the null pointer exception.
      id = StringUtils.EMPTY;
    }
    return (new JPAQueryFactory(em).select(directorateHistoryEntity).from(directorateHistoryEntity)
      .where(directorateHistoryEntity.toDate.isNull(),
        directorateHistoryEntity.id.notEqualsIgnoreCase(id),
        directorateHistoryEntity.tenant.id.in(security.getPermittedTenants(getUser())),
        directorateHistoryEntity.name.eq(name))
      .fetchCount() == 0);
  }

  @Override
  public Set<ValidationError> saveDirectorateEntry(DirectorateHistoryDTO directorateHistoryDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method saveDirectorateEntry, Parameters: directorateHistoryDTO: {0}",
      directorateHistoryDTO);

    Set<ValidationError> error = new HashSet<>();
    DirectorateHistoryEntity directorateHistEntity;
    // Check if an old entry is being updated or a new entry is created.
    if (StringUtils.isBlank(directorateHistoryDTO.getId())) {
      // Creating a new directorate entity.
      DirectorateEntity directorate = new DirectorateEntity();
      em.persist(directorate);
      // Creating a new entry.
      directorateHistEntity =
        DirectorateHistoryMapper.INSTANCE.toDirectorateHistory(directorateHistoryDTO);
      // Set current date to fromDate property.
      directorateHistEntity.setFromDate(new Timestamp(new Date().getTime()));
      // Set tenant.
      directorateHistEntity.setTenant(
        TenantMapper.INSTANCE.toTenant(usersService.getUserById(getUser().getId()).getTenant()));
      // Assign the directorate entity to the directorate history entity.
      directorateHistEntity.setDirectorateId(directorate);

      auditLog(directorate.getId(), AuditEvent.CREATE.name(), null);
    } else {
      // In case of updating an old entry, find the entry and set the current date to the toDate
      // property.
      directorateHistEntity =
        em.find(DirectorateHistoryEntity.class, directorateHistoryDTO.getId());
      // If the current version is 1, then return an optimisticLockErrorField
      if (directorateHistEntity.getVersion() == 1) {
        error.add(new ValidationError("optimisticLockErrorField", ValidationMessages.OPTIMISTIC_LOCK));
        return error;
      }
      directorateHistEntity.setToDate(new Timestamp(new Date().getTime()));
      em.merge(directorateHistEntity);
      // Now that the old entry is added to the history, create its new instance.
      directorateHistEntity =
        DirectorateHistoryMapper.INSTANCE.toDirectorateHistory(directorateHistoryDTO);
      // The entry is going to have a new id.
      directorateHistEntity.setId(null);
      // Set current date to fromDate property.
      directorateHistEntity.setFromDate(new Timestamp(new Date().getTime()));
      // Set null to toDate property.
      directorateHistEntity.setToDate(null);

      auditLog(directorateHistEntity.getDirectorateId().getId(), AuditEvent.UPDATE.name(), null);
    }
    // Save the new entry.
    em.persist(directorateHistEntity);
    cacheBean.updateDirectorateHistoryEntry(
      DirectorateHistoryMapper.INSTANCE.toDirectorateHistoryDTO(directorateHistEntity));
    cacheBean.updateHistoryDirectoratesList(
      DirectorateHistoryMapper.INSTANCE.toDirectorateHistoryDTO(directorateHistEntity));
    return error;
  }

  @Override
  public List<DirectorateHistoryDTO> getAllDirectoratesByUserTenant() {

    LOGGER.log(Level.CONFIG, "Executing method getAllDirectoratesByUserTenant");

    // Retrieving directorate history data.
    return DirectorateHistoryMapper.INSTANCE
      .toDirectorateHistoryDTO(
        new JPAQueryFactory(em).selectFrom(directorateHistoryEntity)
          .where(directorateHistoryEntity.toDate.isNull()
            .and(directorateHistoryEntity.tenant.id
              .eq(usersService.getUserById(getUser().getId()).getTenant().getId())))
          .fetch());

  }
  
  @Override
  public List<DirectorateHistoryDTO> directorateHistoryQueryAll(String tenantId) {

    LOGGER.log(Level.CONFIG,
      "Executing method directorateHistoryQueryAll, Parameters: tenantId: {0}",
      tenantId);

    JPAQuery<ProjectEntity> query = new JPAQuery<>(em);
    BooleanBuilder whereClause = new BooleanBuilder();

    if (!StringUtils.isBlank(tenantId)) {
      whereClause.and(directorateHistoryEntity.tenant.id.eq(tenantId));
    }

    return DirectorateHistoryMapper.INSTANCE
      .toDirectorateHistoryDTO(
        query.select(directorateHistoryEntity).from(directorateHistoryEntity).fetch());
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
