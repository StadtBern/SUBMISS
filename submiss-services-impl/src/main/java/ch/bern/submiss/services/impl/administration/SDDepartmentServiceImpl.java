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

import ch.bern.submiss.services.api.administration.SDDepartmentService;
import ch.bern.submiss.services.api.administration.SDDirectorateService;
import ch.bern.submiss.services.api.administration.UserAdministrationService;
import ch.bern.submiss.services.api.constants.AuditEvent;
import ch.bern.submiss.services.api.constants.AuditGroupName;
import ch.bern.submiss.services.api.constants.AuditLevel;
import ch.bern.submiss.services.api.constants.Group;
import ch.bern.submiss.services.api.constants.Process;
import ch.bern.submiss.services.api.dto.DepartmentHistoryDTO;
import ch.bern.submiss.services.api.dto.DirectorateHistoryDTO;
import ch.bern.submiss.services.api.dto.MasterListTypeDataDTO;
import ch.bern.submiss.services.api.dto.SubmissUserDTO;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.impl.mappers.DepartmentHistoryMapper;
import ch.bern.submiss.services.impl.mappers.DepartmentToTypeDataMapper;
import ch.bern.submiss.services.impl.mappers.DirectorateMapper;
import ch.bern.submiss.services.impl.mappers.TenantMapper;
import ch.bern.submiss.services.impl.model.DepartmentEntity;
import ch.bern.submiss.services.impl.model.DepartmentHistoryEntity;
import ch.bern.submiss.services.impl.model.DirectorateEntity;
import ch.bern.submiss.services.impl.model.QDepartmentHistoryEntity;
import ch.bern.submiss.services.impl.model.QDirectorateEntity;
import ch.bern.submiss.services.impl.model.QDirectorateHistoryEntity;
import ch.bern.submiss.services.impl.model.QSignatureEntity;
import ch.bern.submiss.services.impl.model.SignatureEntity;
import ch.bern.submiss.services.impl.model.SignatureProcessTypeEntitledEntity;
import ch.bern.submiss.services.impl.model.SignatureProcessTypeEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.OptimisticLockException;
import javax.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

/**
 * The Class SDDepartmentServiceImpl.
 */
@Transactional
@OsgiServiceProvider(classes = {SDDepartmentService.class})
@Singleton
public class SDDepartmentServiceImpl extends BaseService implements SDDepartmentService {

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER = Logger.getLogger(SDDepartmentServiceImpl.class.getName());
  /**
   * The users service.
   */
  @Inject
  protected UserAdministrationService usersService;
  /**
   * The department history entity.
   */
  QDepartmentHistoryEntity departmentHistoryEntity =
    QDepartmentHistoryEntity.departmentHistoryEntity;
  /**
   * The directorate history entity.
   */
  QDirectorateHistoryEntity directorateHistoryEntity =
    QDirectorateHistoryEntity.directorateHistoryEntity;
  /**
   * The directorate history entity.
   */
  QDirectorateEntity directorateEntity =
    QDirectorateEntity.directorateEntity;
  /**
   * The directorate service.
   */
  @Inject
  private SDDirectorateService directorateService;

  /**
   * The cache bean.
   */
  @Inject
  private CacheBean cacheBean;

  @Inject
  private AuditBean audit;

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SDDepartmentService#readDepartmentsByUser()
   */
  @Override
  public List<DepartmentHistoryDTO> readDepartmentsByUser() {

    LOGGER.log(Level.CONFIG, "Executing method readDepartmentsByUser");

    JPAQuery<DepartmentHistoryEntity> query = new JPAQuery<>(em);
    List<DepartmentHistoryEntity> departmentHistoryEntities =
      query.select(departmentHistoryEntity).from(departmentHistoryEntity)
        .where(departmentHistoryEntity.departmentId.id
          .in(security.getPermittedDepartments(getUser()))
          .and(departmentHistoryEntity.toDate.isNull()))
        .orderBy(departmentHistoryEntity.name.asc()).fetch();

    return DepartmentHistoryMapper.INSTANCE
      .toDepartmentHistoryDTO(departmentHistoryEntities, cacheBean.getActiveDirectorateHistorySD());

  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SDDepartmentService#getDepartmentsByDirectoratesIds
   * (java.util.List)
   */
  @Override
  public List<DepartmentHistoryDTO> getDepartmentsByDirectoratesIds(List<String> directoratesIDs) {

    LOGGER.log(Level.CONFIG,
      "Executing method getDepartmentsByDirectoratesIds, Parameters: directoratesIDs: {0}",
      directoratesIDs);

    JPAQuery<DepartmentHistoryEntity> query = new JPAQuery<>(em);
    List<String> directorateEntityIds = query.select(directorateHistoryEntity.directorateId.id).distinct()
      .where(directorateHistoryEntity.id.in(directoratesIDs)).from(directorateHistoryEntity)
      .fetch();
    List<DepartmentHistoryEntity> departmentHistoryEntities = query.select(departmentHistoryEntity).distinct()
      .where(departmentHistoryEntity.directorateEnity.id.in(directorateEntityIds)
        .and(departmentHistoryEntity.departmentId.id
          .in(security.getPermittedDepartments(getUser())))
        .and(departmentHistoryEntity.toDate.isNull()))
      .from(departmentHistoryEntity).orderBy(departmentHistoryEntity.name.asc()).fetch();

    return DepartmentHistoryMapper.INSTANCE
      .toDepartmentHistoryDTO(departmentHistoryEntities, cacheBean.getActiveDirectorateHistorySD());
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SDDepartmentService#getDepartmentHistByDepartmentId
   * (java.lang.String)
   */
  @Override
  public DepartmentHistoryDTO getDepartmentHistByDepartmentId(String departmentId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getDepartmentHistByDepartmentId, Parameters: departmentId: {0}",
      departmentId);

    DepartmentHistoryEntity departmentHistory =
      new JPAQueryFactory(em).select(departmentHistoryEntity).from(departmentHistoryEntity)
        .where(departmentHistoryEntity.departmentId.id.eq(departmentId)
          .and(departmentHistoryEntity.toDate.isNull())).fetchFirst();
    return DepartmentHistoryMapper.INSTANCE
      .toDepartmentHistoryDTO(departmentHistory, cacheBean.getActiveDirectorateHistorySD());
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SDDepartmentService#getDepartmentBySortName(java.
   * lang.String, java.lang.String)
   */
  @Override
  public DepartmentHistoryDTO getDepartmentBySortName(String shortName, String companyShortName) {

    LOGGER.log(Level.CONFIG,
      "Executing method getDepartmentBySortName, Parameters: shortName: {0}, "
        + "companyShortName: {1}",
      new Object[]{shortName, companyShortName});

    String directorateEntityIdByShortName = new JPAQueryFactory(em)
      .select(directorateHistoryEntity.directorateId.id)
      .where(directorateHistoryEntity.shortName.eq(companyShortName)).from(directorateHistoryEntity)
      .fetchOne();
    DepartmentHistoryEntity department = new JPAQueryFactory(em).select(departmentHistoryEntity)
      .from(departmentHistoryEntity).where(departmentHistoryEntity.directorateEnity.id
        .eq(directorateEntityIdByShortName).and(departmentHistoryEntity.shortName.eq(shortName)))
      .fetchFirst();
    return DepartmentHistoryMapper.INSTANCE
      .toDepartmentHistoryDTO(department, cacheBean.getActiveDirectorateHistorySD());
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SDDepartmentService#getDepartmentByTenant(java.lang
   * .String)
   */
  @Override
  public List<DepartmentHistoryDTO> getDepartmentByTenant(String tenantId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getDepartmentByTenant, Parameters: tenantId: {0}",
      tenantId);

    List<DepartmentHistoryEntity> departmentHistory =
      new JPAQueryFactory(em).select(departmentHistoryEntity).from(departmentHistoryEntity)
        .where(departmentHistoryEntity.tenant.id.eq(tenantId)
          .and(departmentHistoryEntity.toDate.isNull()))
        .fetch();
    return DepartmentHistoryMapper.INSTANCE
      .toDepartmentHistoryDTO(departmentHistory, cacheBean.getActiveDirectorateHistorySD());
  }

  @Override
  public List<DepartmentHistoryDTO> readAll() {

    LOGGER.log(Level.CONFIG, "Executing method readAll");

    JPAQuery<DepartmentHistoryEntity> query = new JPAQuery<>(em);
    List<DepartmentHistoryEntity> departmentHistoryEntities = query.select(departmentHistoryEntity)
      .from(departmentHistoryEntity).where(departmentHistoryEntity.toDate.isNull())
      .orderBy(departmentHistoryEntity.name.asc()).fetch();

    return DepartmentHistoryMapper.INSTANCE
      .toDepartmentHistoryDTO(departmentHistoryEntities, cacheBean.getActiveDirectorateHistorySD());
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SDDepartmentService#getUserDepartments()
   */
  @Override
  public List<DepartmentHistoryDTO> getUserDepartments() {

    LOGGER.log(Level.CONFIG, "Executing method getUserDepartments");

    SubmissUserDTO user = usersService.getUserById(getUser().getId());
    List<DepartmentHistoryDTO> departments = new ArrayList<>();
    if (user.getMainDepartment() != null) {
      departments.add(user.getMainDepartment());
    }
    if (user.getSecondaryDepartments() != null && !user.getSecondaryDepartments().isEmpty()) {
      departments.addAll(user.getSecondaryDepartments());
    }
    return departments;
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SDDepartmentService#departmentToTypeData()
   */
  @Override
  public List<MasterListTypeDataDTO> departmentToTypeData() {

    LOGGER.log(Level.CONFIG, "Executing method departmentToTypeData");

    List<MasterListTypeDataDTO> typeDTOs;
    // Retrieving department history data.
    List<DepartmentHistoryEntity> departmentHistoryEntities =
      new JPAQueryFactory(em).selectFrom(qDepartmentHistoryEntity)
        .where(qDepartmentHistoryEntity.toDate.isNull().and(
          qDepartmentHistoryEntity.tenant.id.in(security.getPermittedTenants(getUser()))))
        .fetch();
    // Map the current directorate history name to the directorate id.
    HashMap<String, String> dirHistNameToDirIdMap = new HashMap<>();
    for (DirectorateHistoryDTO directorateHistoryDTO : directorateService
      .getAllDirectoratesByUserTenant()) {
      dirHistNameToDirIdMap.put(directorateHistoryDTO.getDirectorateId().getId(),
        directorateHistoryDTO.getName());
    }
    // Map the current directorate history name to the department id.
    HashMap<String, String> dirHistNameToDeptIdMap = new HashMap<>();
    for (DepartmentHistoryEntity deptHistoryEntity : departmentHistoryEntities) {
      dirHistNameToDeptIdMap.put(deptHistoryEntity.getId(),
        dirHistNameToDirIdMap.get(deptHistoryEntity.getDirectorateEnity().getId()));
    }
    // Mapping department data to master list type data.
    typeDTOs =
      DepartmentToTypeDataMapper.INSTANCE.toMasterListTypeDataDTOs(departmentHistoryEntities);
    for (MasterListTypeDataDTO typeDTO : typeDTOs) {
      // Set the directorateName property of the typeDTO using the typeDTO id. 
      typeDTO.setDirectorateName(dirHistNameToDeptIdMap.get(typeDTO.getId()));
    }
    return typeDTOs;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SDDepartmentService#getDepartmentEntryById(java.
   * lang.String)
   */
  @Override
  public DepartmentHistoryDTO getDepartmentEntryById(String entryId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getDepartmentEntryById, Parameters: entryId: {0}",
      entryId);

    return DepartmentHistoryMapper.INSTANCE
      .toDepartmentHistoryDTO(new JPAQueryFactory(em).selectFrom(qDepartmentHistoryEntity)
          .where(qDepartmentHistoryEntity.id.eq(entryId)).fetchOne(),
        cacheBean.getActiveDirectorateHistorySD());
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SDDepartmentService#departmentHistoryQuery(java.
   * lang.String)
   */
  @Override
  public List<DepartmentHistoryDTO> departmentHistoryQuery(String tenantId) {

    LOGGER.log(Level.CONFIG,
      "Executing method departmentHistoryQuery, Parameters: tenantId: {0}",
      tenantId);

    JPAQuery<DepartmentHistoryEntity> query = new JPAQuery<>(em);
    BooleanBuilder whereClause = new BooleanBuilder();

    if (!StringUtils.isBlank(tenantId)) {
      whereClause.and(qDepartmentHistoryEntity.tenant.id.eq(tenantId));
    }

    return DepartmentHistoryMapper.INSTANCE.toDepartmentHistoryDTO(query
      .select(qDepartmentHistoryEntity).from(qDepartmentHistoryEntity).where(whereClause,
        qDepartmentHistoryEntity.toDate.isNull())
      .fetch(), cacheBean.getActiveDirectorateHistorySD());
  }

  @Override
  public List<DepartmentHistoryDTO> getActiveDepartmentsByUserTenant() {

    LOGGER.log(Level.CONFIG, "Executing method getActiveDepartmentsByUserTenant");

    JPAQuery<DepartmentHistoryEntity> query = new JPAQuery<>(em);
    BooleanBuilder whereClause = new BooleanBuilder();

    return DepartmentHistoryMapper.INSTANCE
      .toDepartmentHistoryDTO(
        query.select(qDepartmentHistoryEntity).from(qDepartmentHistoryEntity)
          .where(whereClause, qDepartmentHistoryEntity.active.isTrue(),
            qDepartmentHistoryEntity.toDate.isNull(),
            qDepartmentHistoryEntity.tenant.id
              .eq(usersService.getUserById(getUser().getId()).getTenant().getId()))
          .fetch(), cacheBean.getActiveDirectorateHistorySD());
  }

  @Override
  public boolean isNameAndShortNameUnique(String name, String shortName, String id) {

    LOGGER.log(Level.CONFIG,
      "Executing method isNameUnique, Parameters: name: {0}, shortName: {1}, id: {2}",
      new Object[]{name, shortName, id});

    if (id == null) {
      // If the id is null (case of creating a new entry), replace the null value with an empty
      // String in order to avoid the null pointer exception.
      id = StringUtils.EMPTY;
    }
    return (new JPAQueryFactory(em).select(qDepartmentHistoryEntity).from(qDepartmentHistoryEntity)
      .where(qDepartmentHistoryEntity.toDate.isNull(),
        qDepartmentHistoryEntity.id.notEqualsIgnoreCase(id),
        qDepartmentHistoryEntity.tenant.id.in(security.getPermittedTenants(getUser())),
        qDepartmentHistoryEntity.shortName.eq(shortName),
        qDepartmentHistoryEntity.name.eq(name))
      .fetchCount() == 0);
  }

  @Override
  public void saveDepartmentEntry(DepartmentHistoryDTO departmentHistoryDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method saveDepartmentEntry, Parameters: departmentHistoryDTO: {0}",
      departmentHistoryDTO);

    DepartmentHistoryEntity departmentHistEntity;
    // Check if an old entry is updated or a new entry is created.
    if (StringUtils.isBlank(departmentHistoryDTO.getId())) {
      // Creating a new department entity.
      DepartmentEntity departmentEntity = new DepartmentEntity();
      em.persist(departmentEntity);
      // Creating a new entry.
      departmentHistEntity =
        DepartmentHistoryMapper.INSTANCE.toDepartmentHistory(departmentHistoryDTO);
      // Set current date to fromDate property.
      departmentHistEntity.setFromDate(new Timestamp(new Date().getTime()));
      // Set tenant.
      departmentHistEntity.setTenant(
        TenantMapper.INSTANCE.toTenant(usersService.getUserById(getUser().getId()).getTenant()));
      // Assign the department entity to the department history entity.
      departmentHistEntity.setDepartmentId(departmentEntity);
      // if new Department is created an associated Signature must be created
      createSignature(departmentHistEntity);
      // Create audit log
      auditLog(departmentEntity.getId(), AuditEvent.CREATE.name(), null);
    } else {
      // In case of updating an old entry, find the entry and set the current date to the toDate
      // property.
      departmentHistEntity = em.find(DepartmentHistoryEntity.class, departmentHistoryDTO.getId());
      // If the current version is 1, then return an optimisticLockErrorField
      if (departmentHistEntity.getVersion() == 1) {
        throw new OptimisticLockException();
      }
      departmentHistEntity.setToDate(new Timestamp(new Date().getTime()));
      em.merge(departmentHistEntity);
      // Update the associated signature if directorate is changed
      if (!departmentHistEntity.getDirectorateEnity().getId()
        .equals(departmentHistoryDTO.getDirectorate().getDirectorateId().getId())) {
        updateSignature(departmentHistoryDTO.getDepartmentId().getId(), DirectorateMapper.INSTANCE
          .toDirectorate(departmentHistoryDTO.getDirectorate().getDirectorateId()));
      }
      // Now that the old entry is added to the history, create its new instance.
      departmentHistEntity =
        DepartmentHistoryMapper.INSTANCE.toDepartmentHistory(departmentHistoryDTO);
      // The entry is going to have a new id.
      departmentHistEntity.setId(null);
      // Set current date to fromDate property.
      departmentHistEntity.setFromDate(new Timestamp(new Date().getTime()));
      // Set null to toDate property.
      departmentHistEntity.setToDate(null);
      // Create audit log
      auditLog(departmentHistEntity.getDepartmentId().getId(), AuditEvent.UPDATE.name(), null);
    }
    // Save the new entry.
    em.persist(departmentHistEntity);
    cacheBean.updateDeparmentHistoryEntry(
      DepartmentHistoryMapper.INSTANCE
        .toDepartmentHistoryDTO(departmentHistEntity, cacheBean.getActiveDirectorateHistorySD()));
    cacheBean.updateHistoryDepartmentList(
      DepartmentHistoryMapper.INSTANCE
        .toDepartmentHistoryDTO(departmentHistEntity, cacheBean.getActiveDirectorateHistorySD()));
  }

  /**
   * Creates the associated signature.
   *
   * @param departmentHistEntity the departmentHistEntity
   */
  private void createSignature(DepartmentHistoryEntity departmentHistEntity) {

    LOGGER.log(Level.CONFIG,
      "Executing method createSignature, Parameters: departmentHistEntity: {0}",
      departmentHistEntity);

    SignatureEntity sE = new SignatureEntity();
    sE.setDirectorate(departmentHistEntity.getDirectorateEnity());
    sE.setDepartment(departmentHistEntity.getDepartmentId());
    em.persist(sE);

    List<Process> processTypeList = new ArrayList<>();
    processTypeList.add(Process.NEGOTIATED_PROCEDURE);
    processTypeList.add(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION);
    processTypeList.add(Process.INVITATION);
    processTypeList.add(Process.OPEN);
    processTypeList.add(Process.SELECTIVE);

    // creating SignatureProcessType and SignatureProcessTypeEntitled Entities for each process type
    for (Process p : processTypeList) {
      SignatureProcessTypeEntity sTe = new SignatureProcessTypeEntity();
      sTe.setProcess(p);
      sTe.setSignature(sE);
      em.persist(sTe);
      SignatureProcessTypeEntitledEntity signatureProcessTypeEntitledEntity = new SignatureProcessTypeEntitledEntity();
      signatureProcessTypeEntitledEntity.setProcessType(sTe);
      em.persist(signatureProcessTypeEntitledEntity);
    }
  }

  /**
   * Updates the associated signature.
   * @param departmentId the departmentId
   * @param directorateEntity the directorateEntity
   */
  private void updateSignature(String departmentId, DirectorateEntity directorateEntity) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateSignature, Parameters: departmentId: {0}, "
        + "directorateEntity: {1}",
      new Object[]{departmentId, directorateEntity});

    QSignatureEntity qSignatureEntity = QSignatureEntity.signatureEntity;
    SignatureEntity signatureEntity = new JPAQueryFactory(em).selectFrom(qSignatureEntity)
      .where(qSignatureEntity.department.id.eq(departmentId))
      .fetchOne();
    signatureEntity.setDirectorate(directorateEntity);
    em.merge(signatureEntity);
  }

  @Override
  public List<DepartmentHistoryDTO> departmentHistoryQueryAll(String tenantId) {

    LOGGER.log(Level.CONFIG,
      "Executing method departmentHistoryQueryAll, Parameters: tenantId: {0}",
      tenantId);

    JPAQuery<DepartmentHistoryEntity> query = new JPAQuery<>(em);
    BooleanBuilder whereClause = new BooleanBuilder();

    if (!StringUtils.isBlank(tenantId)) {
      whereClause.and(qDepartmentHistoryEntity.tenant.id.eq(tenantId));
    }

    return DepartmentHistoryMapper.INSTANCE.toDepartmentHistoryDTO(query
        .select(qDepartmentHistoryEntity).from(qDepartmentHistoryEntity).where(whereClause).fetch(),
      cacheBean.getActiveDirectorateHistorySD());
  }

  private void auditLog(String id, String event, String message) {
    audit.createLogAudit(AuditLevel.REFERENCE_DATA_LEVEL.name(), event,
      AuditGroupName.REFERENCE_DATA.name(), message, getUser().getId(),
      id, null, null, LookupValues.INTERNAL_LOG);
  }

  @Override
  public List<DepartmentHistoryDTO> readDepartmentsByUserAndRole() {

    LOGGER.log(Level.CONFIG, "Executing method readDepartmentsByUserAndRole");

    JPAQuery<DepartmentHistoryEntity> query = new JPAQuery<>(em);
    BooleanBuilder whereClause = new BooleanBuilder();

    if (getGroupName(getUser()).equals(Group.PL.getValue())) {
      whereClause.and(departmentHistoryEntity.departmentId.id.in(getUserPermittedDepartmentIds()));
    } else {
      whereClause.and(
        departmentHistoryEntity.departmentId.id.in(security.getPermittedDepartments(getUser())));
    }
    List<DepartmentHistoryEntity> departmentHistoryEntities = query.select(departmentHistoryEntity)
      .from(departmentHistoryEntity).where(whereClause, departmentHistoryEntity.toDate.isNull())
      .orderBy(departmentHistoryEntity.name.asc()).fetch();
    return DepartmentHistoryMapper.INSTANCE.toDepartmentHistoryDTO(departmentHistoryEntities,
      cacheBean.getActiveDirectorateHistorySD());
  }
}
