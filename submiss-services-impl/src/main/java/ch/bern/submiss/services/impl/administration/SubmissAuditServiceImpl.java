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
import com.querydsl.sql.SQLExpressions;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Singleton;
import javax.persistence.Query;
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

    // In case of company level, retrieve company level entries
    if (levelIdOption.equals(LookupValues.COMPANY_LEVEL)) {

      Query auditCompanyQuery = em.createNativeQuery(
        getAuditQuery(pageItems, sortColumn, sortType, levelIdOption, auditLogDTO),
        "SubmissAuditCompanyMapping");
      // Add the query parameters
      addQueryParameters(page, pageItems, auditLogDTO, auditCompanyQuery);
      List<SubmissAuditCompanyEntity> submissAuditCompanyEntities = auditCompanyQuery
        .getResultList();
      return SubmissAuditCompanyMapper.INSTANCE
        .auditCompanyEntityToAuditCompanyDTO(submissAuditCompanyEntities);
    } else {

      // otherwise, retrieve project level entries
      Query auditQuery = em.createNativeQuery(
        getAuditQuery(pageItems, sortColumn, sortType, levelIdOption, auditLogDTO),
        "SubmissAuditProjectMapping");
      // Add the query parameters
      addQueryParameters(page, pageItems, auditLogDTO, auditQuery);
      List<SubmissAuditProjectEntity> submissAuditProjectEntities = auditQuery.getResultList();
      return SubmissAuditProjectMapper.INSTANCE
        .auditProjectEntityToAuditProjectDTO(submissAuditProjectEntities);
    }
  }

  /**
   * Gets the audit query.
   *
   * @param pageItems     the pageItems
   * @param sortColumn    the sortColumn
   * @param sortType      the sortType
   * @param levelIdOption the levelIdOption
   * @param auditLogDTO   the auditLogDTO
   * @return the query
   */
  private String getAuditQuery(int pageItems, String sortColumn, String sortType,
    String levelIdOption, AuditLogDTO auditLogDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method getAuditLogs, Parameters: pageItems: {0}, "
        + "sortColumn: {1}, sortType: {2}, levelIdOption: {3}, auditLogDTO: {4}",
      new Object[]{pageItems, sortColumn, sortType, levelIdOption, auditLogDTO});

    StringBuilder query = new StringBuilder();

    if (levelIdOption.equals(LookupValues.PROJECT_LEVEL)) {
      query.append(LookupValues.AUDIT_PROJECT_QUERY);
      addProjectFilters(auditLogDTO, query);

    } else if (levelIdOption.equals(LookupValues.COMPANY_LEVEL)) {
      query.append(LookupValues.AUDIT_COMPANY_QUERY);
      addCompanyFilters(auditLogDTO, query);
    }

    // Add common filtering values for both levelIdOptions
    addCommonFilters(auditLogDTO, query);

    // Add sorting
    if (sortColumn != null) {
      query.append(getOrderBy(sortColumn, sortType));
    }

    // Add pagination
    if (pageItems != -1) {
      query.append(" LIMIT :limit OFFSET :offset");
    }

    return query.toString();
  }

  /**
   * Adds the common filter values.
   *
   * @param auditLogDTO the auditLogDTO
   * @param query       the query
   */
  private void addCommonFilters(AuditLogDTO auditLogDTO, StringBuilder query) {

    LOGGER.log(Level.CONFIG,
      "Executing method addCommonFilters, Parameters: auditLogDTO: {0}, "
        + "query: {1}",
      new Object[]{auditLogDTO, query});

    if (StringUtils.isNotBlank(auditLogDTO.getUserName())) {
      query.append(LookupValues.AUDIT_AND_QUERY).append(LookupValues.AUDIT_USERNAME)
        .append(" LIKE :username");
    }
    if (StringUtils.isNotBlank(auditLogDTO.getShortDescription())) {
      query.append(LookupValues.AUDIT_AND_QUERY).append(LookupValues.AUDIT_TRANSLATION)
        .append(" LIKE :shortDescription");
    }
    if (auditLogDTO.getCreatedOn() != null) {
      query.append(LookupValues.AUDIT_AND_QUERY).append(LookupValues.AUDIT_CREATED_ON)
        .append(" LIKE :createdOn");
    }
  }

  /**
   * Adds the company filter values.
   *
   * @param auditLogDTO the auditLogDTO
   * @param query       the query
   */
  private void addCompanyFilters(AuditLogDTO auditLogDTO, StringBuilder query) {

    LOGGER.log(Level.CONFIG,
      "Executing method addCompanyFilters, Parameters: auditLogDTO: {0}, "
        + "query: {1}",
      new Object[]{auditLogDTO, query});

    // Add check for tenant
    query.append(LookupValues.AUDIT_AND_QUERY).append(LookupValues.LEFT_PARENTHESIS)
      .append(LookupValues.AUDIT_COMPANY_TENANT_ID).append(" = :tenant_id")
      .append(LookupValues.AUDIT_OR_QUERY).append(LookupValues.AUDIT_COMPANY_TENANT_ID)
      .append(" IS NULL").append(LookupValues.RIGHT_PARENTHESIS);

    // if the user is permitted to view proof status FABE, retrieve all entries

    if (!isUserFABEPermitted()) {
      // retrieve company entries that have not 'WITH_FABE' status
      query.append(LookupValues.AUDIT_AND_QUERY).append(LookupValues.AUDIT_PROOF_STATUS_FABE)
        .append(" NOT IN(3)");
    }
    // Add filtering values for company level
    if (StringUtils.isNotBlank(auditLogDTO.getCompanyName())) {
      query.append(LookupValues.AUDIT_AND_QUERY).append(LookupValues.AUDIT_COMPANY_NAME)
        .append(" LIKE :companyName");
    }
  }

  /**
   * Adds the project filter values.
   *
   * @param auditLogDTO the auditLogDTO
   * @param query       the query
   */
  private void addProjectFilters(AuditLogDTO auditLogDTO, StringBuilder query) {

    LOGGER.log(Level.CONFIG,
      "Executing method addProjectFilters, Parameters: auditLogDTO: {0}, "
        + "query: {1}",
      new Object[]{auditLogDTO, query});

    // Add check for tenant
    query.append(LookupValues.AUDIT_AND_QUERY).append(LookupValues.AUDIT_PROJECT_TENANT_ID)
      .append(" = :tenant_id");
    // Add filtering values for project level
    if (StringUtils.isNotBlank(auditLogDTO.getObjectName())) {
      query.append(LookupValues.AUDIT_AND_QUERY).append(LookupValues.AUDIT_OBJECT_NAME)
        .append(" LIKE :objectName");
    }
    if (StringUtils.isNotBlank(auditLogDTO.getProjectName())) {
      query.append(LookupValues.AUDIT_AND_QUERY).append(LookupValues.AUDIT_PROJECT_NAME)
        .append(" LIKE :projectName");
    }
    if (StringUtils.isNotBlank(auditLogDTO.getWorkType())) {
      query.append(LookupValues.AUDIT_AND_QUERY).append(LookupValues.LEFT_PARENTHESIS)
        .append(LookupValues.AUDIT_WORKTYPE).append(" LIKE :workType")
        .append(LookupValues.AUDIT_OR_QUERY).append(LookupValues.AUDIT_TENDER_DESCRIPTION)
        .append(" LIKE :tender_description").append(LookupValues.RIGHT_PARENTHESIS);
    }
    if (StringUtils.isNotBlank(auditLogDTO.getReason())) {
      query.append(LookupValues.AUDIT_AND_QUERY).append(LookupValues.AUDIT_REASON)
        .append(" LIKE :reason");
    }
  }

  /**
   * Adds the query parameters.
   *
   * @param page        the page
   * @param pageItems   the pageItems
   * @param auditLogDTO the auditLogDTO
   * @param query       the query
   */
  private void addQueryParameters(int page, int pageItems, AuditLogDTO auditLogDTO, Query query) {

    LOGGER.log(Level.CONFIG,
      "Executing method addQueryParameters, Parameters: page: {0}, "
        + "pageItems: {1}, AuditLogDTO: {2}, query: {3}",
      new Object[]{page, pageItems, auditLogDTO, query});

    query.setParameter("tenant_id",
      getUser().getAttribute(USER_ATTRIBUTES.TENANT.getValue()).getData());

    if (StringUtils.isNotBlank(auditLogDTO.getUserName())) {
      query.setParameter("username", "%" + auditLogDTO.getUserName() + "%");
    }
    if (StringUtils.isNotBlank(auditLogDTO.getShortDescription())) {
      query.setParameter("shortDescription", "%" + auditLogDTO.getShortDescription() + "%");
    }
    if (auditLogDTO.getCreatedOn() != null) {
      query.setParameter("createdOn", "%" + new SimpleDateFormat(
        "yyyy-MM-dd").format(auditLogDTO.getCreatedOn()) + "%");
    }
    if (StringUtils.isNotBlank(auditLogDTO.getObjectName())) {
      query.setParameter("objectName", "%" + auditLogDTO.getObjectName() + "%");
    }
    if (StringUtils.isNotBlank(auditLogDTO.getProjectName())) {
      query.setParameter("projectName", "%" + auditLogDTO.getProjectName() + "%");
    }
    if (StringUtils.isNotBlank(auditLogDTO.getWorkType())) {
      query.setParameter("workType", "%" + auditLogDTO.getWorkType() + "%");
      query.setParameter("tender_description", "%" + auditLogDTO.getWorkType() + "%");
    }
    if (StringUtils.isNotBlank(auditLogDTO.getReason())) {
      query.setParameter("reason", "%" + auditLogDTO.getReason() + "%");
    }
    if (StringUtils.isNotBlank(auditLogDTO.getCompanyName())) {
      query.setParameter("companyName", "%" + auditLogDTO.getCompanyName() + "%");
    }
    // Add pagination parameters
    if (pageItems != -1) {
      query.setParameter("limit", pageItems);
      query.setParameter("offset", (page - 1) * pageItems);
    }
  }

  /**
   * Gets the order by.
   *
   * @param sortColumn the sort column
   * @param sortType   the sort type
   * @return the order by
   */
  private String getOrderBy(String sortColumn, String sortType) {

    LOGGER.log(Level.CONFIG,
      "Executing method getOrderBy, Parameters: sortColumn: {0}, "
        + "sortType: {1}, sortType: {2}",
      new Object[]{sortColumn, sortType});

    StringBuilder orderBy = new StringBuilder();

    switch (sortColumn) {
      case "userName":
        orderBy.append(LookupValues.AUDIT_ORDER_BY_QUERY).append("userName")
          .append(LookupValues.SPACE).append(getOrderType(sortType));
        break;
      case "shortDescription":
        orderBy.append(LookupValues.AUDIT_ORDER_BY_QUERY).append("translation")
          .append(LookupValues.SPACE).append(getOrderType(sortType));
        break;
      case "createdOn":
        orderBy.append(LookupValues.AUDIT_ORDER_BY_QUERY).append("createdOn")
          .append(LookupValues.SPACE).append(getOrderType(sortType));
        break;
      case "objectName":
        orderBy.append(LookupValues.AUDIT_ORDER_BY_QUERY).append("objectName")
          .append(LookupValues.SPACE).append(getOrderType(sortType));
        break;
      case "projectName":
        orderBy.append(LookupValues.AUDIT_ORDER_BY_QUERY).append("projectName")
          .append(LookupValues.SPACE).append(getOrderType(sortType));
        break;
      case "workType":
        orderBy.append(LookupValues.AUDIT_ORDER_BY_QUERY).append("workType")
          .append(LookupValues.SPACE).append(getOrderType(sortType))
          .append(", tender_description ASC");
        break;
      case "reason":
        orderBy.append(LookupValues.AUDIT_ORDER_BY_QUERY).append("reason")
          .append(LookupValues.SPACE).append(getOrderType(sortType));
        break;
      case "companyName":
        orderBy.append(LookupValues.AUDIT_ORDER_BY_QUERY).append("companyName")
          .append(LookupValues.SPACE).append(getOrderType(sortType));
        break;
      default:
        break;
    }
    return orderBy.toString();
  }

  /**
   * Gets the order type.
   *
   * @param sortType the sortType
   * @return asc or desc
   */
  private String getOrderType(String sortType) {

    LOGGER.log(Level.CONFIG,
      "Executing method getOrderType, Parameters: sortType: {0}", sortType);

    return (sortType.equals("asc")) ? "ASC" : "DESC";
  }

  @Override
  public long auditLogCount(AuditLogDTO auditLogDTO, String levelIdOption) {

    LOGGER.log(Level.CONFIG,
      "Executing method auditLogCount, Parameters: auditLogDTO: {0}, "
        + "levelIdOption: {1}",
      new Object[]{auditLogDTO, levelIdOption});

    Query auditCountQuery = em.createNativeQuery(
      getAuditCountQuery(levelIdOption, auditLogDTO));
    // Add the query parameters
    addQueryParameters(1, -1, auditLogDTO, auditCountQuery);
    Object auditLogCount = auditCountQuery.getResultList().get(0);

    return (auditLogCount instanceof BigInteger)
      ? ((BigInteger) auditLogCount).longValue()
      : 0;
  }

  /**
   * Gets the audit count query.
   *
   * @param levelIdOption the levelIdOption
   * @param auditLogDTO   the auditLogDTO
   * @return the query
   */
  private String getAuditCountQuery(String levelIdOption, AuditLogDTO auditLogDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method getAuditCountQuery, Parameters: levelIdOption: {0}, "
        + "auditLogDTO: {1}",
      new Object[]{levelIdOption, auditLogDTO});

    StringBuilder query = new StringBuilder();

    if (levelIdOption.equals(LookupValues.PROJECT_LEVEL)) {
      query.append(LookupValues.AUDIT_COUNT_PROJECT_QUERY);
      addProjectFilters(auditLogDTO, query);

    } else if (levelIdOption.equals(LookupValues.COMPANY_LEVEL)) {
      query.append(LookupValues.AUDIT_COUNT_COMPANY_QUERY);
      addCompanyFilters(auditLogDTO, query);
    }

    // Add common filtering values for both levelIdOptions
    addCommonFilters(auditLogDTO, query);

    return query.toString();
  }

  @Override
  public void auditLogSecurityCheck() {

    LOGGER.log(Level.CONFIG, "Executing method auditLogSecurityCheck");

    security.isPermittedOperationForUser(getUserId(),
      SecurityOperation.AUDIT.getValue(), null);
  }
}
