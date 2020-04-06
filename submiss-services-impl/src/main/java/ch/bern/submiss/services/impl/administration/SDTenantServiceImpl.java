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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import com.eurodyn.qlack2.fuse.aaa.api.dto.ResourceDTO;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ch.bern.submiss.services.api.administration.SDTenantService;
import ch.bern.submiss.services.api.constants.SecurityOperation;
import ch.bern.submiss.services.api.dto.TenantDTO;
import ch.bern.submiss.services.api.util.LookupValues.USER_ATTRIBUTES;
import ch.bern.submiss.services.impl.mappers.TenantMapper;
import ch.bern.submiss.services.impl.model.DepartmentHistoryEntity;
import ch.bern.submiss.services.impl.model.DirectorateHistoryEntity;
import ch.bern.submiss.services.impl.model.QDepartmentHistoryEntity;
import ch.bern.submiss.services.impl.model.QDirectorateHistoryEntity;
import ch.bern.submiss.services.impl.model.QTenantEntity;
import ch.bern.submiss.services.impl.model.TenantEntity;

/**
 * The Class SDTenantServiceImpl.
 */
@Transactional
@OsgiServiceProvider(classes = {SDTenantService.class})
@Singleton
public class SDTenantServiceImpl extends BaseService implements SDTenantService {

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER = Logger.getLogger(SDTenantServiceImpl.class.getName());

  /**
   * The q tenant entity.
   */
  QTenantEntity qTenantEntity = QTenantEntity.tenantEntity;

  /**
   * The q department.
   */
  QDepartmentHistoryEntity qDepartment = QDepartmentHistoryEntity.departmentHistoryEntity;

  /**
   * The q directorate.
   */
  QDirectorateHistoryEntity qDirectorate = QDirectorateHistoryEntity.directorateHistoryEntity;

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SDTenantService#readAll()
   */
  @Override
  public List<TenantDTO> readAll() {

    LOGGER.log(Level.CONFIG, "Executing method readAll");

    JPAQuery<TenantEntity> query = new JPAQuery<>(em);
    List<TenantEntity> tenantList = query.select(qTenantEntity).from(qTenantEntity).fetch();

    return TenantMapper.INSTANCE.toTenantDTO(tenantList);
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SDTenantService#getUserTenants()
   */
  @Override
  public List<TenantDTO> getUserTenants() {

    LOGGER.log(Level.CONFIG, "Executing method getUserTenants");

    Set<ResourceDTO> allowedTenants = security.getResources(getUser().getId(),
      SecurityOperation.RESOURCE_USER_VIEW.getValue(), true, false);
    List<TenantDTO> tenants = new ArrayList<>();
    for (ResourceDTO tenantResource : allowedTenants) {
      String tenant = tenantResource.getObjectID();
      TenantEntity tenantEntity = em.find(TenantEntity.class, tenant);
      tenants.add(TenantMapper.INSTANCE.toTenantDTO(tenantEntity));
    }
    return tenants;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SDTenantService#getTenantById(java.lang.String)
   */
  @Override
  public TenantDTO getTenantById(String tenantId) {

    LOGGER.log(Level.CONFIG, "Executing method getTenantById, Parameters: tenantId: {0}",
      tenantId);

    TenantEntity tenantEntity = em.find(TenantEntity.class, tenantId);
    return TenantMapper.INSTANCE.toTenantDTO(tenantEntity);

  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SDTenantService#getTenantByDepartmentAndDirectorate
   * (java.lang.String, java.lang.String)
   */
  @Override
  public TenantDTO getTenantByDepartmentAndDirectorate(String department, String directorate) {

    LOGGER.log(Level.CONFIG,
      "Executing method getTenantByDepartmentAndDirectorate, Parameters: "
        + "department: {0}, directorate: {1}",
      new Object[]{department, directorate});

    String directorateId =
      new JPAQueryFactory(em).select
        (qDirectorate.directorateId.id).from(qDirectorate).where(qDirectorate.shortName
        .eq(directorate))
        .fetchOne();

    return TenantMapper.INSTANCE
      .toTenantDTO(
        new JPAQueryFactory(em)
          .select(qDepartment.tenant).from(qDepartment).where(qDepartment.shortName
          .eq(department).and(qDepartment.directorateEnity.id.eq(directorateId)))
          .fetchOne());
  }

  // This function is used when we want to get the tenant of a specific user and
  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SDTenantService#getTenant()
   */
  // we don't have information about the user
  @Override
  public TenantDTO getTenant() {

    LOGGER.log(Level.CONFIG, "Executing method getTenant");

    TenantDTO tenantDTO = new TenantDTO();
    // we use getUser() function, a function at the BaseService that returns the
    // user as a UserDTO.This way,we can get the tenant that this user belongs to as a
    // TenantDTO through getTenantById(String tenantId) function
    if (getUser() != null && getUser().getAttribute(USER_ATTRIBUTES.TENANT.getValue()) != null
      && getUser().getAttribute(USER_ATTRIBUTES.TENANT.getValue()).getData() != null) {
      tenantDTO =
        getTenantById(getUser().getAttribute(USER_ATTRIBUTES.TENANT.getValue()).getData());
    }
    return tenantDTO;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SDTenantService#getTenantByDepartment(java.lang.
   * String)
   */
  @Override
  public TenantDTO getTenantByDepartment(String departmentId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getTenantByDepartment, Parameters: "
        + "departmentId: {0}",
      departmentId);

    TenantDTO tenantDTO = new TenantDTO();
    DepartmentHistoryEntity departmentEntity = new JPAQueryFactory(em).select(qDepartment)
      .from(qDepartment).where(qDepartment.departmentId.id.eq(departmentId)).fetchOne();
    if (departmentEntity != null) {
      tenantDTO = TenantMapper.INSTANCE.toTenantDTO(departmentEntity.getTenant());
    }
    return tenantDTO;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.SDTenantService#getTenantByDirectorate(java.lang.
   * String)
   */
  @Override
  public TenantDTO getTenantByDirectorate(String directorateId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getTenantByDirectorate, Parameters: "
        + "directorateId: {0}",
      directorateId);

    TenantDTO tenantDTO = new TenantDTO();
    DirectorateHistoryEntity directorateEntity = new JPAQueryFactory(em).select(qDirectorate)
      .from(qDirectorate).where(qDirectorate.directorateId.id.eq(directorateId)).fetchOne();
    if (directorateEntity != null) {
      tenantDTO = TenantMapper.INSTANCE.toTenantDTO(directorateEntity.getTenant());
    }
    return tenantDTO;
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SDTenantService#getMainTenant()
   */
  @Override
  public TenantDTO getMainTenant() {

    LOGGER.log(Level.CONFIG, "Executing method getMainTenant");

    return TenantMapper.INSTANCE.toTenantDTO(new JPAQueryFactory(em).selectFrom(qTenantEntity)
      .where(qTenantEntity.isMain.isTrue()).fetchOne());
  }

  @Override
  public boolean isTenantKantonBern() {
    String currentTenantId = getTenant().getId();
    String kantonBernId = new JPAQueryFactory(em).select(qTenantEntity.id).from(qTenantEntity)
      .where(qTenantEntity.name.eq("Kanton Bern")).fetchOne();
    return currentTenantId.equals(kantonBernId);
  }
}
