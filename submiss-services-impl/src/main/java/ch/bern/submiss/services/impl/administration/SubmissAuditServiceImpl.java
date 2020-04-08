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

import ch.bern.submiss.services.api.administration.SubmissAuditService;
import ch.bern.submiss.services.api.constants.SecurityOperation;
import ch.bern.submiss.services.api.dto.AuditLogDTO;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.api.util.LookupValues.USER_ATTRIBUTES;
import ch.bern.submiss.services.impl.mappers.SubmissAuditCompanyMapper;
import ch.bern.submiss.services.impl.mappers.SubmissAuditProjectMapper;
import ch.bern.submiss.services.impl.model.QSubmissAuditCompanyEntity;
import ch.bern.submiss.services.impl.model.QSubmissAuditProjectEntity;
import ch.bern.submiss.services.impl.model.SubmissAuditCompanyEntity;
import ch.bern.submiss.services.impl.model.SubmissAuditProjectEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.sql.SQLExpressions;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

/**
 * The Class AuditServiceImpl.
 */
@Transactional
@OsgiServiceProvider(classes = {SubmissAuditService.class})
@Singleton
public class SubmissAuditServiceImpl extends BaseService implements SubmissAuditService {

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER = Logger.getLogger(SubmissAuditServiceImpl.class.getName());

  /**
   * The q submiss audit company entity.
   */
  QSubmissAuditCompanyEntity qSubmissAuditCompanyEntity =
    QSubmissAuditCompanyEntity.submissAuditCompanyEntity;

  /**
   * The q submiss audit project entity.
   */
  QSubmissAuditProjectEntity qSubmissAuditProjectEntity =
    QSubmissAuditProjectEntity.submissAuditProjectEntity;


  /**
   * Check if the user is permitted to view proof status FABE.
   *
   * @return true if the the user is permitted to view proof status FABE
   */
  private boolean isUserFABEPermitted() {

    return security.isPermitted(getUserId(),
      SecurityOperation.MAIN_TENANT_BESCHAFFUNGSWESEN_VIEW.getValue(), null);

  }

  @Override
  public List<?> getAuditLogs(int page, int pageItems, String sortColumn, String sortType,
    String levelIdOption, AuditLogDTO auditLogDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method getAuditLogs, Parameters: page: {0}, pageItems: {1}, "
        + "sortColumn: {2}, sortType: {3}, levelIdOption: {4}, auditLogDTO: {5}",
      new Object[]{page, pageItems, sortColumn, sortType, levelIdOption, auditLogDTO});

    JPAQuery<?> query = new JPAQuery<>(em);
    // In case of company level, retrieve company level entries
    if (levelIdOption.equals(LookupValues.COMPANY_LEVEL)) {
      List<SubmissAuditCompanyEntity> submissAuditCompanyEntities;
      // if the user is permitted to view proof status FABE, retrieve all entries

      query.select(qSubmissAuditCompanyEntity).from(qSubmissAuditCompanyEntity);
      query.where(getWhereClause(auditLogDTO, qSubmissAuditCompanyEntity,
        qSubmissAuditProjectEntity, levelIdOption));

      if (!isUserFABEPermitted()) {
        // retrieve company entries that have not 'WITH_FABE' status
        query.where(qSubmissAuditCompanyEntity.proofStatusFabe.notIn(3));
      }
      query.where(qSubmissAuditCompanyEntity.tenantId
        .eq(getUser().getAttribute(USER_ATTRIBUTES.TENANT.getValue()).getData())
        .or(qSubmissAuditCompanyEntity.tenantId.isNull()));
      // Return all entries
      if (pageItems != -1) {
        query.offset((page - 1) * pageItems).limit(pageItems);
      }
      if (sortColumn != null) {
        query.orderBy(getOrderBy(levelIdOption, sortColumn, sortType, qSubmissAuditCompanyEntity,
          qSubmissAuditProjectEntity));
      }

      submissAuditCompanyEntities = (List<SubmissAuditCompanyEntity>) query.fetch();

      return SubmissAuditCompanyMapper.INSTANCE
        .auditCompanyEntityToAuditCompanyDTO(submissAuditCompanyEntities);


    } else {

      // otherwise, retrieve project level entries
      List<SubmissAuditProjectEntity> submissAuditProjectEntities;
      query.select(qSubmissAuditProjectEntity).from(qSubmissAuditProjectEntity)
        .where(getWhereClause(auditLogDTO, qSubmissAuditCompanyEntity, qSubmissAuditProjectEntity,
          levelIdOption)
          .and(qSubmissAuditProjectEntity.tenantId
            .eq(getUser().getAttribute(USER_ATTRIBUTES.TENANT.getValue()).getData())));
      // Return all entries
      if (pageItems != -1) {
        query.offset((page - 1) * pageItems).limit(pageItems);
      }
      if (sortColumn != null) {
        query.orderBy(getOrderBy(levelIdOption, sortColumn, sortType, qSubmissAuditCompanyEntity,
          qSubmissAuditProjectEntity));
      }
      submissAuditProjectEntities = (List<SubmissAuditProjectEntity>) query.fetch();
      return SubmissAuditProjectMapper.INSTANCE
        .auditProjectEntityToAuditProjectDTO(submissAuditProjectEntities);
    }
  }

 @Override
  public long auditLogCount(AuditLogDTO auditLogDTO, String levelIdOption) {

    LOGGER.log(Level.CONFIG,
      "Executing method auditLogCount, Parameters: auditLogDTO: {0}, "
        + "levelIdOption: {1}",
      new Object[]{auditLogDTO, levelIdOption});

    JPAQuery<?> query = new JPAQuery<>(em);
    long auditLogCount = 0;
    // In case of company level, retrieve company level entries
    if (levelIdOption.equals(LookupValues.COMPANY_LEVEL)) {

      query.select(qSubmissAuditCompanyEntity).from(qSubmissAuditCompanyEntity)
        .where(getWhereClause(auditLogDTO, qSubmissAuditCompanyEntity, qSubmissAuditProjectEntity,
          levelIdOption));
      if (!isUserFABEPermitted()) {
        query.where(qSubmissAuditCompanyEntity.proofStatusFabe.notIn(3));
      }
      auditLogCount = query.fetchCount();
    } else {
      auditLogCount = query
        .select(qSubmissAuditProjectEntity).from(qSubmissAuditProjectEntity).where(

          getWhereClause(auditLogDTO, qSubmissAuditCompanyEntity, qSubmissAuditProjectEntity,
            levelIdOption)
            .and(qSubmissAuditProjectEntity.tenantId
              .eq(getUser().getAttribute(USER_ATTRIBUTES.TENANT.getValue()).getData())))
        .fetchCount();

    }

    return auditLogCount;
  }


  /**
   * Gets the where clause.
   *
   * @param auditLogDTO the audit log DTO
   * @param qSubmissAuditCompanyEntity the q submiss audit company entity
   * @param qSubmissAuditProjectEntity the q submiss audit project entity
   * @param levelIdOption the level id option
   * @return the where clause
   */
  private BooleanBuilder getWhereClause(AuditLogDTO auditLogDTO,
    QSubmissAuditCompanyEntity qSubmissAuditCompanyEntity,
    QSubmissAuditProjectEntity qSubmissAuditProjectEntity, String levelIdOption) {

    LOGGER.log(Level.CONFIG,
      "Executing method getWhereClause, Parameters: auditLogDTO: {0}, "
        + "qSubmissAuditCompanyEntity: {1}, qSubmissAuditProjectEntity: {2}, "
        + "levelIdOption: {3}",
      new Object[]{auditLogDTO, qSubmissAuditCompanyEntity, qSubmissAuditProjectEntity,
        levelIdOption});

    BooleanBuilder whereClause = new BooleanBuilder();

    if (levelIdOption.equals(LookupValues.COMPANY_LEVEL)) {
      if (StringUtils.isNotBlank(auditLogDTO.getUserName())) {
        whereClause
          .and(qSubmissAuditCompanyEntity.userName.like("%" + auditLogDTO.getUserName() + "%"));
      }
      if (StringUtils.isNotBlank(auditLogDTO.getShortDescription())) {
        whereClause.and(qSubmissAuditCompanyEntity.translation
          .like("%" + auditLogDTO.getShortDescription() + "%"));
      }
      if (auditLogDTO.getCreatedOn() != null) {
        whereClause.and(SQLExpressions.date(qSubmissAuditCompanyEntity.createdOn)
          .eq(auditLogDTO.getCreatedOn()));
      }
      if (StringUtils.isNotBlank(auditLogDTO.getCompanyName())) {
        whereClause.and(
          qSubmissAuditCompanyEntity.companyName.like("%" + auditLogDTO.getCompanyName() + "%"));
      }

    } else {
      if (StringUtils.isNotBlank(auditLogDTO.getUserName())) {
        whereClause
          .and(qSubmissAuditProjectEntity.userName.like("%" + auditLogDTO.getUserName() + "%"));
      }
      if (StringUtils.isNotBlank(auditLogDTO.getShortDescription())) {
        whereClause.and(qSubmissAuditProjectEntity.translation
          .like("%" + auditLogDTO.getShortDescription() + "%"));
      }
      if (auditLogDTO.getCreatedOn() != null) {
        whereClause.and(SQLExpressions.date(qSubmissAuditProjectEntity.createdOn)
          .eq(auditLogDTO.getCreatedOn()));
      }
      if (StringUtils.isNotBlank(auditLogDTO.getObjectName())) {
        whereClause.and(
          qSubmissAuditProjectEntity.objectName.like("%" + auditLogDTO.getObjectName() + "%"));
      }
      if (StringUtils.isNotBlank(auditLogDTO.getProjectName())) {
        whereClause.and(
          qSubmissAuditProjectEntity.projectName.like("%" + auditLogDTO.getProjectName() + "%"));
      }
      if (StringUtils.isNotBlank(auditLogDTO.getWorkType())) {
        whereClause
          .and(qSubmissAuditProjectEntity.workType.like("%" + auditLogDTO.getWorkType() + "%"));
      }
      if (StringUtils.isNotBlank(auditLogDTO.getReason())) {
        whereClause
          .and(qSubmissAuditProjectEntity.reason.like("%" + auditLogDTO.getReason() + "%"));
      }
    }
    return whereClause;
  }

  /**
   * Gets the order by.
   *
   * @param levelIdOption the level id option
   * @param sortColumn the sort column
   * @param sortType the sort type
   * @param qSubmissAuditCompanyEntity the q submiss audit company entity
   * @param qSubmissAuditProjectEntity the q submiss audit project entity
   * @return the order by
   */
  private OrderSpecifier getOrderBy(String levelIdOption, String sortColumn, String sortType,
    QSubmissAuditCompanyEntity qSubmissAuditCompanyEntity,
    QSubmissAuditProjectEntity qSubmissAuditProjectEntity) {

    LOGGER.log(Level.CONFIG,
      "Executing method getOrderBy, Parameters: levelIdOption: {0}, "
        + "sortColumn: {1}, sortType: {2}, qSubmissAuditCompanyEntity: {3}, "
        + "qSubmissAuditProjectEntity: {4}",
      new Object[]{levelIdOption, sortColumn, sortType, qSubmissAuditCompanyEntity,
        qSubmissAuditProjectEntity});

    OrderSpecifier orderBy = null;

    if (levelIdOption.equals(LookupValues.COMPANY_LEVEL)) {
      if (sortColumn.equals("userName")) {
        return (sortType.equals("asc")) ? qSubmissAuditCompanyEntity.userName.asc()
          : qSubmissAuditCompanyEntity.userName.desc();
      }
      if (sortColumn.equals("shortDescription")) {
        return (sortType.equals("asc")) ? qSubmissAuditCompanyEntity.translation.asc()
          : qSubmissAuditCompanyEntity.translation.desc();
      }
      if (sortColumn.equals("createdOn")) {
        return (sortType.equals("asc")) ? qSubmissAuditCompanyEntity.createdOn.asc()
          : qSubmissAuditCompanyEntity.createdOn.desc();
      }
      if (sortColumn.equals("companyName")) {
        return (sortType.equals("asc")) ? qSubmissAuditCompanyEntity.companyName.asc()
          : qSubmissAuditCompanyEntity.companyName.desc();
      }

    } else {
      if (sortColumn.equals("userName")) {
        return (sortType.equals("asc")) ? qSubmissAuditProjectEntity.userName.asc()
          : qSubmissAuditProjectEntity.userName.desc();
      }
      if (sortColumn.equals("shortDescription")) {
        return (sortType.equals("asc")) ? qSubmissAuditProjectEntity.translation.asc()
          : qSubmissAuditProjectEntity.translation.desc();
      }
      if (sortColumn.equals("createdOn")) {
        return (sortType.equals("asc")) ? qSubmissAuditProjectEntity.createdOn.asc()
          : qSubmissAuditProjectEntity.createdOn.desc();
      }
      if (sortColumn.equals("objectName")) {
        return (sortType.equals("asc")) ? qSubmissAuditProjectEntity.objectName.asc()
          : qSubmissAuditProjectEntity.objectName.desc();
      }
      if (sortColumn.equals("projectName")) {
        return (sortType.equals("asc")) ? qSubmissAuditProjectEntity.projectName.asc()
          : qSubmissAuditProjectEntity.projectName.desc();
      }
      if (sortColumn.equals("workType")) {
        return (sortType.equals("asc")) ? qSubmissAuditProjectEntity.workType.asc()
          : qSubmissAuditProjectEntity.workType.desc();
      }
      if (sortColumn.equals("reason")) {
        return (sortType.equals("asc")) ? qSubmissAuditProjectEntity.reason.asc()
          : qSubmissAuditProjectEntity.reason.desc();
      }
    }
    return orderBy;
  }

  @Override
  public void auditLogSecurityCheck() {

    LOGGER.log(Level.CONFIG, "Executing method auditLogSecurityCheck");

    security.isPermittedOperationForUser(getUserId(),
      SecurityOperation.AUDIT.getValue(), null);
  }
}
