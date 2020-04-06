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
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.OptimisticLockException;
import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import com.eurodyn.qlack2.fuse.aaa.api.dto.UserAttributeDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.SQLExpressions;

import ch.bern.submiss.services.api.administration.CompanyProofService;
import ch.bern.submiss.services.api.administration.CompanyService;
import ch.bern.submiss.services.api.administration.SDTenantService;
import ch.bern.submiss.services.api.administration.SubmissTaskService;
import ch.bern.submiss.services.api.constants.AuditEvent;
import ch.bern.submiss.services.api.constants.AuditGroupName;
import ch.bern.submiss.services.api.constants.AuditLevel;
import ch.bern.submiss.services.api.constants.AuditMessages;
import ch.bern.submiss.services.api.constants.CategorySD;
import ch.bern.submiss.services.api.constants.LogibStatus;
import ch.bern.submiss.services.api.constants.ProofStatus;
import ch.bern.submiss.services.api.constants.SecurityOperation;
import ch.bern.submiss.services.api.constants.TaskTypes;
import ch.bern.submiss.services.api.dto.CompanyDTO;
import ch.bern.submiss.services.api.dto.CompanyOfferDTO;
import ch.bern.submiss.services.api.dto.CompanyProofDTO;
import ch.bern.submiss.services.api.dto.CompanySearchDTO;
import ch.bern.submiss.services.api.dto.CountryHistoryDTO;
import ch.bern.submiss.services.api.dto.DepartmentHistoryDTO;
import ch.bern.submiss.services.api.dto.MasterListValueHistoryDTO;
import ch.bern.submiss.services.api.dto.ProofHistoryDTO;
import ch.bern.submiss.services.api.dto.TenantDTO;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.api.util.LookupValues.USER_ATTRIBUTES;
import ch.bern.submiss.services.api.util.ValidationMessages;
import ch.bern.submiss.services.impl.mappers.CompanyMapper;
import ch.bern.submiss.services.impl.mappers.CompanyProofDTOMapper;
import ch.bern.submiss.services.impl.mappers.CompanyWithProofStatusMapper;
import ch.bern.submiss.services.impl.mappers.CountryHistoryMapper;
import ch.bern.submiss.services.impl.mappers.CustomCompanyMapper;
import ch.bern.submiss.services.impl.mappers.DepartmentHistoryMapper;
import ch.bern.submiss.services.impl.mappers.MasterListValueHistoryMapper;
import ch.bern.submiss.services.impl.mappers.ProofHistoryMapper;
import ch.bern.submiss.services.impl.model.CompanyEntity;
import ch.bern.submiss.services.impl.model.CompanyProofEntity;
import ch.bern.submiss.services.impl.model.CountryHistoryEntity;
import ch.bern.submiss.services.impl.model.DepartmentHistoryEntity;
import ch.bern.submiss.services.impl.model.MasterListValueEntity;
import ch.bern.submiss.services.impl.model.MasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.OfferEntity;
import ch.bern.submiss.services.impl.model.ProofHistoryEntity;
import ch.bern.submiss.services.impl.model.QCompanyEntity;
import ch.bern.submiss.services.impl.model.QCompanyProofEntity;
import ch.bern.submiss.services.impl.model.QCountryEntity;
import ch.bern.submiss.services.impl.model.QCountryHistoryEntity;
import ch.bern.submiss.services.impl.model.QDepartmentEntity;
import ch.bern.submiss.services.impl.model.QJointVentureEntity;
import ch.bern.submiss.services.impl.model.QMasterListValueEntity;
import ch.bern.submiss.services.impl.model.QMasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.QOfferEntity;
import ch.bern.submiss.services.impl.model.QProofEntity;
import ch.bern.submiss.services.impl.model.QProofHistoryEntity;
import ch.bern.submiss.services.impl.model.QSubcontractorEntity;
import ch.bern.submiss.services.impl.model.QSubmissionEntity;
import ch.bern.submiss.services.impl.model.QSubmittentEntity;
import ch.bern.submiss.services.impl.model.QTenantEntity;
import ch.bern.submiss.services.impl.model.TenantEntity;
import ch.bern.submiss.services.impl.util.ComparatorUtil;
import ch.bern.submiss.services.impl.util.DBUtils;

/**
 * The Class CompanyServiceImpl.
 */
@Transactional
@OsgiServiceProvider(classes = {CompanyService.class})
@Singleton
public class CompanyServiceImpl extends BaseService implements CompanyService {

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER = Logger.getLogger(CompanyServiceImpl.class.getName());
  /**
   * The Constant PROOF_STATUS.
   */
  private static final String PROOF_STATUS = "proofStatus";
  /**
   * The Constant LOCATION.
   */
  private static final String LOCATION = "location";
  /**
   * The Constant OPTIMISTIC_LOCK.
   */
  private static final String OPTIMISTIC_LOCK = "optimistic_lock";
  /**
   * The Constant OPTIMISTIC_LOCK_ERROR_FIELD.
   */
  private static final String OPTIMISTIC_LOCK_ERROR_FIELD = "optimisticLockErrorField";
  /**
   * The sd tenant service.
   */
  @Inject
  protected SDTenantService sdTenantService;
  /**
   * The q master list value history entity.
   */
  QMasterListValueHistoryEntity qMasterListValueHistoryEntity =
    QMasterListValueHistoryEntity.masterListValueHistoryEntity;

  /**
   * The q master list value entity.
   */
  QMasterListValueEntity qMasterListValueEntity = QMasterListValueEntity.masterListValueEntity;

  /**
   * The q country history entity.
   */
  QCountryHistoryEntity qCountryHistoryEntity = QCountryHistoryEntity.countryHistoryEntity;

  /**
   * The q country entity.
   */
  QCountryEntity qCountryEntity = QCountryEntity.countryEntity;

  /**
   * The q company entity.
   */
  QCompanyEntity qCompanyEntity = QCompanyEntity.companyEntity;

  /**
   * The q department entity.
   */
  QDepartmentEntity qDepartmentEntity = QDepartmentEntity.departmentEntity;

  /**
   * The q proof history entity.
   */
  QProofHistoryEntity qProofHistoryEntity = QProofHistoryEntity.proofHistoryEntity;

  /**
   * The q company proof entity.
   */
  QCompanyProofEntity qCompanyProofEntity = QCompanyProofEntity.companyProofEntity;

  /**
   * The q submittent entity.
   */
  QSubmittentEntity qSubmittentEntity = QSubmittentEntity.submittentEntity;

  /**
   * The q joint venture entity.
   */
  QJointVentureEntity qJointVentureEntity = QJointVentureEntity.jointVentureEntity;

  /**
   * The q offer entity.
   */
  QOfferEntity qOfferEntity = QOfferEntity.offerEntity;

  /**
   * The q sub contractors.
   */
  QSubcontractorEntity qSubContractors = QSubcontractorEntity.subcontractorEntity;

  /**
   * The q proof entity.
   */
  QProofEntity qProofEntity = QProofEntity.proofEntity;

  /**
   * The q tenant entity.
   */
  QTenantEntity qTenantEntity = QTenantEntity.tenantEntity;
  /**
   * The task service.
   */
  @Inject
  private SubmissTaskService taskService;
  /**
   * The audit.
   */
  @Inject
  private AuditBean audit;
  /**
   * The cache bean.
   */
  @Inject
  private CacheBean cacheBean;
  /**
   * The company proof service.
   */
  @Inject
  private CompanyProofService companyProofService;

  /**
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.CompanyService#search(ch.bern.submiss.services.api
   * . dto.CompanySearchDTO, int, int, java.lang.String, java.lang.String)
   */
  @Override
  public List<CompanyDTO> search(CompanySearchDTO companySearchDTO, int page, int pageItems,
    String sortColumn, String sortType) {
    LOGGER.log(Level.CONFIG,
      "Executing method search, Parameters: companySearchDTO: {0}, page: {1}, pageItems: {2}, sortColumn: {3}, sortType: {4}",
      new Object[]{companySearchDTO, page, pageItems, sortColumn, sortType});

    List<CompanyEntity> companies = filter(companySearchDTO, page, pageItems, sortColumn, sortType);
    /**
     * check if the user is permitted to view proof status FABE, so that the DTO is updated with the
     * correct proof status
     */
    boolean canViewProofStatusFabe = security.isPermitted(getUserId(),
      SecurityOperation.MAIN_TENANT_BESCHAFFUNGSWESEN_VIEW.getValue(), null);

    return CompanyWithProofStatusMapper.INSTANCE.toBasicCompanyDTO(companies,
      canViewProofStatusFabe);
  }

  /**
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.CompanyService#getAllCompanies()
   */
  @Override
  public List<CompanyDTO> getAllCompanies() {
    LOGGER.log(Level.CONFIG, "Executing method getAllCompanies");

    List<CompanyEntity> companyEntities = new JPAQueryFactory(em).select(qCompanyEntity)
      .from(qCompanyEntity).orderBy(qCompanyEntity.companyName.asc()).fetch();
    Map<String, MasterListValueHistoryDTO> activeSD = new HashMap<>();
    activeSD.putAll(cacheBean.getActiveSD().row(CategorySD.WORKTYPE.getValue()));
    return CustomCompanyMapper.INSTANCE.companiesToCompaniesDTO(companyEntities, activeSD);
  }

  /**
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.CompanyService#getSearchedCompanies(java.util.List,
   * java.lang.String, java.lang.String)
   */
  @Override
  public List<CompanyDTO> getSearchedCompanies(List<String> companiesIDs, String sortColumn,
    String sortType) {
    LOGGER.log(Level.CONFIG,
      "Executing method getSearchedCompanies, Parameters: companiesIDs: {0}, sortColumn: {1}, sortType: {2}",
      new Object[]{companiesIDs, sortColumn, sortType});

    JPAQuery<CompanyEntity> query = new JPAQuery<>(em);
    QCompanyEntity company = QCompanyEntity.companyEntity;

    query.select(company).from(company).where(company.id.in(companiesIDs));

    /** Apply ordering */
    if (sortColumn.equals("id")) {
      query.orderBy(sortType.equals("asc") ? company.id.asc() : company.id.desc());
    } else if (sortColumn.equals("companyName")) {
      query
        .orderBy(sortType.equals("asc") ? company.companyName.asc() : company.companyName.desc());
    } else if (sortColumn.equals("companyTel")) {
      query.orderBy(sortType.equals("asc") ? company.companyTel.asc() : company.companyTel.desc());
    } else if (sortColumn.equals("postCode")) {
      query.orderBy(sortType.equals("asc") ? company.postCode.asc() : company.postCode.desc());
      /**
       * if the user is permitted to view proof status FABE sort on the field proofStatusFabe of the
       * table
       */
    } else if (sortColumn.equals(PROOF_STATUS)) {
      query.orderBy(
        sortType.equals("asc") ? company.proofStatusFabe.asc() : company.proofStatusFabe.desc());
    } else if (sortColumn.equals("apprenticeFactor")) {
      query.orderBy(sortType.equals("asc") ? company.apprenticeFactor.asc()
        : company.apprenticeFactor.desc());
    } else if (sortColumn.equals("workTypes")) {
      query.orderBy(
        sortType.equals("asc") ? company.workTypes.any().masterListValueHistory.any().value1.asc()
          : company.workTypes.any().masterListValueHistory.any().value1.desc());
    } else if (sortColumn.equals("logIb")) {
      query.orderBy(sortType.equals("asc") ? company.logIb.asc() : company.logIb.desc());
    } else if (sortColumn.equals("logibArgib")) {
      query.orderBy(sortType.equals("asc") ? company.logIbARGIB.asc() : company.logIbARGIB.desc());
    } else if (sortColumn.equals("certificateDate")) {
      query.orderBy(
        sortType.equals("asc") ? company.certificateDate.asc() : company.certificateDate.desc());
    } else if (sortColumn.equals(LOCATION)) {
      query.orderBy(sortType.equals("asc") ? company.location.asc() : company.location.desc());
    } else if (sortColumn.equals("tlp")) {
      query.orderBy(sortType.equals("asc") ? company.tlp.asc() : company.tlp.desc());
    }

    List<CompanyEntity> companyEntities = query.fetch();

    Map<String, MasterListValueHistoryDTO> activeSD = new HashMap<>();
    activeSD.putAll(cacheBean.getActiveSD().row(CategorySD.WORKTYPE.getValue()));
    return CustomCompanyMapper.INSTANCE.companiesToCompaniesDTO(companyEntities, activeSD);
  }

  /**
   * Filter.
   *
   * @param companyDTO the company DTO
   * @param page the page
   * @param pageItems the page items
   * @param sortColumn the sort column
   * @param sortType the sort type
   * @return the list
   */
  private List<CompanyEntity> filter(CompanySearchDTO companyDTO, int page, int pageItems,
    String sortColumn, String sortType) {
    LOGGER.log(Level.CONFIG,
      "Executing method filter, Parameters: companyDTO: {0}, page: {1}, pageItems: {2}, sortColumn: {3}, sortType: {4}",
      new Object[]{companyDTO, page, pageItems, sortColumn, sortType});

    JPAQuery<CompanyEntity> query = new JPAQuery<>(em);
    QCompanyEntity company = QCompanyEntity.companyEntity;

    /**
     * check if the user is permitted to view proof status FABE, so that the query is performed on
     * the correct proof status
     */
    boolean canViewProofStatusFabe = security.isPermitted(getUserId(),
      SecurityOperation.MAIN_TENANT_BESCHAFFUNGSWESEN_VIEW.getValue(), null);

    // -1 when the option to show all results is selected
    if (pageItems == -1) {
      query.select(company).from(company)
        .where(getWhereClause(company, companyDTO, canViewProofStatusFabe)).distinct();
    } else {
      query.select(company).from(company)
        .where(getWhereClause(company, companyDTO, canViewProofStatusFabe)).distinct()
        .offset((page - 1) * pageItems).limit(pageItems);
    }

    /** Apply ordering */
    if (sortColumn.equals("id")) {
      query.orderBy(sortType.equals("asc") ? company.id.asc() : company.id.desc());
    } else if (sortColumn.equals("companyName")) {
      query
        .orderBy(sortType.equals("asc") ? company.companyName.asc() : company.companyName.desc());
    } else if (sortColumn.equals("companyTel")) {
      query.orderBy(sortType.equals("asc") ? company.companyTel.asc() : company.companyTel.desc());
    } else if (sortColumn.equals("postCode")) {
      query.orderBy(sortType.equals("asc") ? company.postCode.asc() : company.postCode.desc());
      /**
       * if the user is permitted to view proof status FABE sort on the field proofStatusFabe of the
       * table
       */
    } else if (sortColumn.equals(PROOF_STATUS) && canViewProofStatusFabe) {
      query.orderBy(
        sortType.equals("asc") ? company.proofStatusFabe.asc() : company.proofStatusFabe.desc());
    } else if (sortColumn.equals(PROOF_STATUS) && !canViewProofStatusFabe) {
      query
        .orderBy(sortType.equals("asc") ? company.proofStatus.asc() : company.proofStatus.desc());
    } else if (sortColumn.equals("apprenticeFactor")) {
      query.orderBy(sortType.equals("asc") ? company.apprenticeFactor.asc()
        : company.apprenticeFactor.desc());
    } else if (sortColumn.equals("workTypes")) {
      query.orderBy(
        sortType.equals("asc") ? company.workTypes.any().masterListValueHistory.any().value1.asc()
          : company.workTypes.any().masterListValueHistory.any().value1.desc());
    } else if (sortColumn.equals("logIb")) {
      query.orderBy(sortType.equals("asc") ? company.logIb.asc() : company.logIb.desc());
    } else if (sortColumn.equals("logibArgib")) {
      query.orderBy(sortType.equals("asc") ? company.logIbARGIB.asc() : company.logIbARGIB.desc());
    } else if (sortColumn.equals("certificateDate")) {
      query.orderBy(
        sortType.equals("asc") ? company.certificateDate.asc() : company.certificateDate.desc());
    } else if (sortColumn.equals(LOCATION)) {
      query.orderBy(sortType.equals("asc") ? company.location.asc() : company.location.desc());
    } else if (sortColumn.equals("tlp")) {
      query.orderBy(sortType.equals("asc") ? company.tlp.asc() : company.tlp.desc());
    } else if (sortColumn.equals("fiftyPlusFactor")) {
      query.orderBy(sortType.equals("asc") ? company.fiftyPlusFactor.asc()
        : company.fiftyPlusFactor.desc());
    }
    return query.fetch();
  }

  /**
   * Gets the where clause.
   *
   * @param entity the entity
   * @param companyDTO the company DTO
   * @param canViewProofStatusFabe the can view proof status fabe
   * @return the where clause
   */
  private BooleanBuilder getWhereClause(QCompanyEntity entity, CompanySearchDTO companyDTO,
    boolean canViewProofStatusFabe) {
    LOGGER.log(Level.CONFIG,
      "Executing method getWhereClause, Parameters: entity: {0}, companyDTO: {1}, canViewProofStatusFabe: {2}",
      new Object[]{entity, companyDTO, canViewProofStatusFabe});

    BooleanBuilder whereClause = new BooleanBuilder();

    if (StringUtils.isNotBlank(companyDTO.getCompanyName())) {
      whereClause.and(entity.companyName.like("%" + companyDTO.getCompanyName() + "%"));
    }
    if (StringUtils.isNotBlank(companyDTO.getPostCode())) {
      whereClause.and(entity.postCode.like("%" + companyDTO.getPostCode() + "%"));
    }
    if (StringUtils.isNotBlank(companyDTO.getLocation())) {
      whereClause.and(entity.location.like("%" + companyDTO.getLocation() + "%"));
    }
    if (companyDTO.getCountryId() != null && StringUtils.isNotBlank(companyDTO.getCountryId())) {
      CountryHistoryEntity countryHistoryEntity =
        new JPAQueryFactory(em).select(qCountryHistoryEntity).from(qCountryHistoryEntity)
          .where(qCountryHistoryEntity.id.eq(companyDTO.getCountryId())).fetchOne();
      whereClause.and(entity.country.id.eq(countryHistoryEntity.getCountryId().getId()));
    }
    if (StringUtils.isNotBlank(companyDTO.getCompanyTel())) {
      whereClause.and(entity.companyTel.like("%" + companyDTO.getCompanyTel() + "%"));
    }
    if (CollectionUtils.isNotEmpty(companyDTO.getWorkTypes())) {
      List<MasterListValueEntity> workTypeEntities =
        new JPAQueryFactory(em).select(qMasterListValueEntity).from(qMasterListValueEntity)
          .where(qMasterListValueEntity.masterListValueHistory.any().in(
            MasterListValueHistoryMapper.INSTANCE.toWorkTypeSet(companyDTO.getWorkTypes())))
          .fetch();
      whereClause.and(entity.workTypes.any().in(workTypeEntities));
    }

    /**
     * if the user is permitted to view proof status FABE query on the field proofStatusFabe of the
     * table
     */
    if (companyDTO.getProofStatus() != null && companyDTO.getProofStatus() != ProofStatus.NULL) {
      if (canViewProofStatusFabe) {
        whereClause.and(entity.proofStatusFabe.eq(companyDTO.getProofStatus().getValue()));
      } else {
        whereClause.and(entity.proofStatus.eq(companyDTO.getProofStatus().getValue()));
      }
    }

    if (companyDTO.getIlo() != null) {
      MasterListValueEntity ilo =
        new JPAQueryFactory(em).select(qMasterListValueEntity).from(qMasterListValueEntity)
          .where(
            qMasterListValueEntity.id.eq(companyDTO.getIlo().getMasterListValueId().getId()))
          .fetchOne();
      whereClause.and(entity.ilo.eq(ilo));
    }

    if (StringUtils.isNotBlank(companyDTO.getNotes())) {
      whereClause.and(entity.notes.like("%" + companyDTO.getNotes() + "%"));
    }
    if (companyDTO.getArchived() != null) {
      whereClause.and(entity.archived.eq(companyDTO.getArchived()));
    }

    /**
     * Selecting LogibStatus, search for the two values(logIb,logIbARGIB) for the correct result
     * LogibStatus corresponds to a combination of logIb and logIbARGIB
     */
    if (companyDTO.getLogibStatus() == LogibStatus.ALL_TRUE_LOGIB) {
      whereClause.and(entity.logIb.eq(1).or(entity.logIbARGIB.eq(1)));
    }
    if (companyDTO.getLogibStatus() == LogibStatus.LOGIB) {
      whereClause.and(entity.logIb.eq(1).and(entity.logIbARGIB.eq(0)));
    }
    if (companyDTO.getLogibStatus() == LogibStatus.LOGIB_ARGIB) {
      whereClause.and(entity.logIb.eq(0).and(entity.logIbARGIB.eq(1)));
    }

    if (companyDTO.getTlp() != null && companyDTO.getTlp().equals(true)) {
      whereClause.and(entity.tlp.eq(companyDTO.getTlp()));
    }

    /** Apply filtering */
    if (companyDTO.getFilter() != null) {

      if (StringUtils.isNotBlank(companyDTO.getFilter().getCompanyName())) {
        whereClause.and(entity.companyName
          .like("%" + companyDTO.getFilter().getCompanyName().toLowerCase() + "%"));
      }
      if (StringUtils.isNotBlank(companyDTO.getFilter().getCompanyTel())) {
        whereClause.and(entity.companyTel.like("%" + companyDTO.getFilter().getCompanyTel() + "%"));
      }
      if (StringUtils.isNotBlank(companyDTO.getFilter().getPostCode())) {
        whereClause.and(entity.postCode.like("%" + companyDTO.getFilter().getPostCode() + "%"))
          .or(entity.location.like("%" + companyDTO.getFilter().getPostCode() + "%"));
      }
      if (StringUtils.isNotBlank(companyDTO.getFilter().getWorkTypes())) {
        List<MasterListValueEntity> workTypes = new JPAQueryFactory(em)
          .select(qMasterListValueEntity).from(qMasterListValueEntity)
          .where(qMasterListValueEntity.masterListValueHistory.any()
            .in(new JPAQueryFactory(em).select(qMasterListValueHistoryEntity)
              .from(qMasterListValueHistoryEntity)
              .where(qMasterListValueHistoryEntity.value2
                .like("%" + companyDTO.getFilter().getWorkTypes().toLowerCase() + "%")
                .or(qMasterListValueHistoryEntity.value1
                  .like("%" + companyDTO.getFilter().getWorkTypes().toLowerCase() + "%")))
              .fetch()))
          .fetch();
        whereClause.and(entity.workTypes.any().in(workTypes));
      }
      if (StringUtils.isNotBlank(companyDTO.getFilter().getApprenticeFactor())) {
        whereClause.and(entity.apprenticeFactor
          .like("%" + companyDTO.getFilter().getApprenticeFactor().toLowerCase() + "%"));
      }
      if (StringUtils.isNotBlank(companyDTO.getFilter().getFiftyPlusFactor())) {
        whereClause.and(entity.fiftyPlusFactor
          .like("%" + companyDTO.getFilter().getFiftyPlusFactor().toLowerCase() + "%"));
      }
      if (companyDTO.getFilter().getLogib() != null) {
        if (companyDTO.getFilter().getLogib() == LogibStatus.ALL_TRUE_LOGIB) {
          whereClause.and(entity.logIb.eq(1).or(entity.logIbARGIB.eq(1)));
        }
        if (companyDTO.getFilter().getLogib() == LogibStatus.LOGIB) {
          whereClause.and(entity.logIb.eq(1).and(entity.logIbARGIB.eq(0)));
        }
        if (companyDTO.getFilter().getLogib() == LogibStatus.LOGIB_ARGIB) {
          whereClause.and(entity.logIb.eq(0).and(entity.logIbARGIB.eq(1)));
        }
      }
      /**
       * if the user is permitted to view proof status FABE query on the field proofStatusFabe of
       * the table
       */
      if (companyDTO.getFilter().getProofStatus() != null) {
        if (canViewProofStatusFabe) {
          whereClause
            .and(entity.proofStatusFabe.eq(companyDTO.getFilter().getProofStatus().getValue()));
        } else {
          whereClause
            .and(entity.proofStatus.eq(companyDTO.getFilter().getProofStatus().getValue()));
        }
      }
      if (companyDTO.getFilter().getCertificateDate() != null) {
        whereClause.and(SQLExpressions.date(entity.certificateDate)
          .eq(companyDTO.getFilter().getCertificateDate()));
      }
    }

    return whereClause;
  }

  /**
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.CompanyService#companyCount(ch.bern.submiss .
   * services.api.dto.CompanySearchDTO)
   */
  @Override
  public long companyCount(CompanySearchDTO companyDTO) {
    LOGGER.log(Level.CONFIG, "Executing method companyCount, Parameters: companyDTO: {0}",
      companyDTO);

    JPAQuery<CompanyEntity> query = new JPAQuery<>(em);
    QCompanyEntity entity = QCompanyEntity.companyEntity;

    /**
     * check if the user is permitted to view proof status FABE, so that the query is performed on
     * the correct proof status
     */
    boolean canViewProofStatusFabe = security.isPermitted(getUserId(),
      SecurityOperation.MAIN_TENANT_BESCHAFFUNGSWESEN_VIEW.getValue(), null);

    return query.select(entity).from(entity)
      .where(getWhereClause(entity, companyDTO, canViewProofStatusFabe)).fetchCount();
  }

  /**
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.CompanyService#fullTextSearch(java.lang.String,
   * java.lang.String)
   */
  @Override
  public List<String> fullTextSearch(String column, String query) {
    LOGGER.log(Level.CONFIG, "Executing method fullTextSearch, Parameters: column: {0}, query: {1}",
      new Object[]{column, query});

    QCompanyEntity entity = QCompanyEntity.companyEntity;

    /** build search expression */
    StringPath path = getStringPath(column, entity);
    BooleanExpression expression = DBUtils.getFullTextSearchExpression(query, path);

    /** execute query */
    JPAQuery<CompanyEntity> jpaQuery = new JPAQuery<>(em);
    return jpaQuery.select(path).distinct().from(entity).where(expression).fetch();

  }

  /**
   * Gets the string path.
   *
   * @param column the column
   * @param entity the entity
   * @return the string path
   */
  private StringPath getStringPath(String column, QCompanyEntity entity) {
    LOGGER.log(Level.CONFIG, "Executing method getStringPath, Parameters: column: {0}, entity: {1}",
      new Object[]{column, entity});

    StringPath path = null;
    switch (column) {
      case "company_name":
        path = entity.companyName;
        break;
      case "post_code":
        path = entity.postCode;
        break;
      case LOCATION:
        path = entity.location;
        break;
      case "company_tel":
        path = entity.companyTel;
        break;
      case "notes":
        path = entity.notes;
        break;
      default:
        break;
    }

    return path;
  }

  /**
   * A function to create a company selecting values from history tables and matching them to their
   * original table id like worktypes.
   *
   * @param company the company
   * @return the string
   */
  @Override
  public String createCompany(CompanyDTO company) {
    LOGGER.log(Level.CONFIG, "Executing method createCompany, Parameters: company: {0}", company);

    company.setCreatedBy(getUserId());
    company.setCreateOn(new Timestamp(new Date().getTime()));
    CompanyEntity companyEntity = CompanyMapper.INSTANCE.toCompany(company);
    if (company.getBranches() != null) {
      companyEntity.setBranches(mapCompanyBranchesDTOtoEntities(company.getBranches()));
    }
    /**
     * Getting the tenant of the user that creates the company and add it to tenant of the company
     */
    TenantEntity tenantEntity =
      new JPAQueryFactory(em).select(qTenantEntity).from(qTenantEntity)
        .where(qTenantEntity.id
          .eq(getUser().getAttribute(USER_ATTRIBUTES.TENANT.getValue()).getData()))
        .fetchOne();
    companyEntity.setTenant(tenantEntity);

    // Make sure that the number of men, number of women, number of trainees and 50+ colleagues
    // contain at least 0 as a value when saved.
    if (companyEntity.getNumberOfMen() == null) {
      companyEntity.setNumberOfMen(0);
    }
    if (companyEntity.getNumberOfWomen() == null) {
      companyEntity.setNumberOfWomen(0);
    }
    if (companyEntity.getNumberOfTrainees() == null) {
      companyEntity.setNumberOfTrainees((long) 0);
    }
    if (companyEntity.getFiftyPlusColleagues() == null) {
      companyEntity.setFiftyPlusColleagues(0);
    }

    /**
     * by creating a company if the user don't select anyone proofstatus automatically it sets
     * itself to NOT_ACTIVE value for ProofStatus by checking the checkboxes proofStatus is changing
     * The proof status FABE can be viewed only by users with certain rights, so it will be set only
     * in the database column proofStatusFabe
     */

    if (company.getProofStatus() == null) {
      if (company.getConsultKaio()) {
        companyEntity.setProofStatus(ProofStatus.WITH_KAIO.getValue());
        companyEntity.setProofStatusFabe(ProofStatus.WITH_KAIO.getValue());
      } else if (company.getConsultAdmin()) {
        companyEntity.setProofStatus(ProofStatus.NOT_ACTIVE.getValue());
        companyEntity.setProofStatusFabe(ProofStatus.WITH_FABE.getValue());
      } else {
        companyEntity.setProofStatus(ProofStatus.NOT_ACTIVE.getValue());
        companyEntity.setProofStatusFabe(ProofStatus.NOT_ACTIVE.getValue());
      }
    }

    /**
     * Set proof to a just created Company depending on Company Country. If the company country
     * exists and is the same with the proof country we take the proofs of this country else if
     * there is not a proof country like company country the company get the proofs of Germany's
     * proof
     */
    List<ProofHistoryEntity> proofs = new JPAQueryFactory(em).select(qProofHistoryEntity)
      .from(qProofHistoryEntity).where(qProofHistoryEntity.country.id
        .eq(companyEntity.getCountry().getId()).and(qProofHistoryEntity.toDate.isNull()))
      .fetch();

    if (proofs.isEmpty()) {
      // Retrieve Germany's proofs when proof list is empty.
      List<ProofHistoryEntity> proofEntities =
        new JPAQueryFactory(em).select(qProofHistoryEntity).from(qProofHistoryEntity)
          .innerJoin(qProofHistoryEntity.country, qCountryEntity)
          .where(
            qCountryEntity
              .in(new JPAQueryFactory(em).select(qCountryHistoryEntity.countryId)
                .from(qCountryHistoryEntity).where(qCountryHistoryEntity.countryShortName
                  .eq("DEU").and(qCountryHistoryEntity.toDate.isNull()))
                .fetch()))
          .fetch();
      proofs.addAll(proofEntities);
    }
    em.persist(companyEntity);
    // Fill CompanyProof table.
    for (ProofHistoryEntity proofHistoryEntity : proofs) {
      CompanyProofEntity companyProofEntity = new CompanyProofEntity();
      companyProofEntity.setCompanyId(companyEntity);
      companyProofEntity.setProofDate(null);
      companyProofEntity.setProofId(proofHistoryEntity.getProofId());
      companyProofEntity.setRequired(proofHistoryEntity.getRequired());
      em.merge(companyProofEntity);
    }

    // 1.create company - "Firmenprofil erstellt"
    StringBuilder additionalInfo = new StringBuilder(companyEntity.getCompanyName()).append("[#]")
      .append(companyEntity.getProofStatusFabe()).append("[#]");
    auditLog(AuditEvent.CREATE.name(), AuditMessages.CREATE_COMPANY.name(), companyEntity.getId(),
      additionalInfo.toString());

    return companyEntity.getId();
  }

  /**
   * Function to Delete a Company by finding the id.
   *
   * @param id the id
   */
  @Override
  public void deleteCompany(String id) {
    LOGGER.log(Level.CONFIG, "Executing method deleteCompany, Parameters: id: {0}", id);

    CompanyEntity companyEntity = em.find(CompanyEntity.class, id);

    if (companyEntity != null) {
      em.remove(companyEntity);
      StringBuilder additionalInfo = new StringBuilder(companyEntity.getCompanyName()).append("[#]")
        .append(companyEntity.getProofStatusFabe()).append("[#]");
      auditLog(AuditEvent.DELETE.name(), AuditMessages.DELETE_COMPANY.name(), companyEntity.getId(),
        additionalInfo.toString());
    }
  }


  /**
   * Function to take(show) Company by taking the id returns a companyDTO.
   *
   * @param id the id
   * @return the company by id
   */
  @Override
  public CompanyDTO getCompanyById(String id) {
    LOGGER.log(Level.CONFIG, "Executing method getCompanyById, Parameters: id: {0}", id);

    CompanyDTO companyDTO = new CompanyDTO();
    CompanyEntity companyEntity = em.find(CompanyEntity.class, id);

    if (companyEntity != null) {
      /**
       * check if the user is permitted to view proof status FABE, so that the DTO is updated with
       * the correct proof status
       */
      boolean canViewProofStatusFabe = security.isPermitted(getUserId(),
        SecurityOperation.MAIN_TENANT_BESCHAFFUNGSWESEN_VIEW.getValue(), null);
      companyDTO =
        CompanyWithProofStatusMapper.INSTANCE.toCompanyDTO(companyEntity, canViewProofStatusFabe);

      if (companyEntity.getModUser() != null) {
        UserDTO user = userService.getUserById(companyEntity.getModUser());

        if (user.getAttribute(USER_ATTRIBUTES.LASTNAME.getValue()) != null
          && user.getAttribute(USER_ATTRIBUTES.LASTNAME.getValue()).getData() != null) {
          companyDTO
            .setModUserLastName(user.getAttribute(USER_ATTRIBUTES.LASTNAME.getValue()).getData());
        }
        if (user.getAttribute(USER_ATTRIBUTES.TENANT.getValue()) != null
          && user.getAttribute(USER_ATTRIBUTES.TENANT.getValue()).getData() != null) {
          TenantDTO tenant = sdTenantService
            .getTenantById(user.getAttribute(USER_ATTRIBUTES.TENANT.getValue()).getData());
          companyDTO.setModUserTenant(tenant.getName());
        }
        if (user.getAttribute(USER_ATTRIBUTES.FIRSTNAME.getValue()) != null
          && user.getAttribute(USER_ATTRIBUTES.FIRSTNAME.getValue()).getData() != null) {
          companyDTO
            .setModUserName(user.getAttribute(USER_ATTRIBUTES.FIRSTNAME.getValue()).getData());
        }
      }

      if (companyEntity.getCountry() != null) {
        CountryHistoryEntity countryEntity = new JPAQueryFactory(em).select(qCountryHistoryEntity)
          .from(qCountryHistoryEntity).where(qCountryHistoryEntity.countryId
            .eq(companyEntity.getCountry()).and(qCountryHistoryEntity.toDate.isNull()))
          .fetchOne();
        CountryHistoryDTO countryDTO =
          CountryHistoryMapper.INSTANCE.toCountryHistoryDTO(countryEntity);
        companyDTO.setCountry(countryDTO);
      }

      if (companyEntity.getIlo() != null) {

        MasterListValueHistoryEntity iloEntity = new JPAQueryFactory(em)
          .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
          .where(qMasterListValueHistoryEntity.masterListValueId.eq(companyEntity.getIlo())
            .and(qMasterListValueHistoryEntity.toDate.isNull()))
          .fetchOne();

        MasterListValueHistoryDTO iloDTO =
          MasterListValueHistoryMapper.INSTANCE.toMasterListValueHistoryDTO(iloEntity);
        companyDTO.setIlo(iloDTO);

      }

      if (companyEntity.getWorkTypes() != null) {
        Set<MasterListValueHistoryDTO> workTypeDTOs = new HashSet<>();
        for (MasterListValueEntity workType : companyEntity.getWorkTypes()) {
          MasterListValueHistoryEntity workTypeEntity = new JPAQueryFactory(em)
            .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
            .where(qMasterListValueHistoryEntity.masterListValueId.eq(workType)
              .and(qMasterListValueHistoryEntity.toDate.isNull()))
            .fetchOne();

          MasterListValueHistoryDTO workTypeDTO =
            MasterListValueHistoryMapper.INSTANCE.toMasterListValueHistoryDTO(workTypeEntity);

          workTypeDTOs.add(workTypeDTO);

        }
        companyDTO.setWorkTypes(workTypeDTOs);
      }

      if (companyEntity.getBranches() != null) {

        List<CompanyDTO> branchDTOs = new ArrayList<>();
        for (CompanyEntity branch : companyEntity.getBranches()) {
          CompanyEntity branchEntity = new JPAQueryFactory(em).select(qCompanyEntity)
            .from(qCompanyEntity).where(qCompanyEntity.id.eq(branch.getId())).fetchOne();

          CompanyDTO branchDTO = CompanyMapper.INSTANCE.toCompanyDTO(branchEntity);
          branchDTOs.add(branchDTO);
        }
        companyDTO.setBranches(branchDTOs);
      }
      /** initialize a default boolean to false */
      companyDTO.setCanBeDeleted(false);
      /** get the users tenant infos and set on tenant variable */
      UserAttributeDTO userTenant = getUser().getAttribute(USER_ATTRIBUTES.TENANT.getValue());
      TenantDTO tenant = sdTenantService.getTenantById(userTenant.getData());
      /** not allow null to go further */
      if (companyDTO.getCreateOn() != null) {
        /**
         * create a Calendar object and add to the company's creation date another 7 days
         */
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(companyDTO.getCreateOn());
        calendar.add(Calendar.DATE, 7);
        /**
         * insert to the validationDate variable the creation date of the company plus 7 days to
         * know the limit
         */
        Timestamp validationDate = new Timestamp(calendar.getTimeInMillis());
        /** set as start the creation date */
        Instant start = companyDTO.getCreateOn().toInstant();
        Instant stop =
          validationDate.toInstant(); /** set to stop the ending date of the validation */
        /** so that the period is between start and stop */
        Instant now = Instant.now(); /** now is the current date and time variable */
        /**
         * the current date and time must be in between of the start and the stop period so that the
         * boolean(isValid) variable be true
         */
        Boolean isValid = (!now.isBefore(start)) && (now.isBefore(stop));
        /**
         * for user of another tenant(not Stadt Bern) and the current date is in between the start
         * and stop dates make the CanBeDeleted true
         */
        if (!tenant.getIsMain() && isValid) {
          companyDTO.setCanBeDeleted(true);
        }
      }
      /**
       * for main tenant users (stadtBern users) always is given the right to delete a company if it
       * is not participating in an offer
       */
      if (tenant.getIsMain()) {
        companyDTO.setCanBeDeleted(true);
      }
    }
    return companyDTO;
  }

  /**
   * Create a list that user insert matches with the telephone on db return only true if the list is
   * empty or null as we want a unique telephone.
   *
   * @param companyTel the company tel
   * @param companyId the company id
   * @return the boolean
   */
  @Override
  public Boolean findIfTelephoneUnique(String companyTel, String companyId) {
    LOGGER.log(Level.CONFIG,
      "Executing method findIfTelephoneUnique, Parameters: companyTel: {0}, companyId: {1}",
      new Object[]{companyTel, companyId});

    QCompanyEntity qCompany = QCompanyEntity.companyEntity;

    List<CompanyEntity> companyEntityList = new ArrayList<>();
    if (companyTel != null) {
      if (StringUtils.isEmpty(companyId)) {
        companyEntityList = (List<CompanyEntity>) new JPAQuery<>(em).select(qCompany).from(qCompany)
          .where(qCompany.companyTel.eq(companyTel)).fetch();
      } else {
        companyEntityList = (List<CompanyEntity>) new JPAQuery<>(em).select(qCompany).from(qCompany)
          .where(qCompany.companyTel.eq(companyTel), qCompany.id.ne(companyId)).fetch();
      }
    }
    return CollectionUtils.isEmpty(companyEntityList);
  }

  /**
   * A function that takes a String as parameter and return a CompanyOfferDTO.
   *
   * @param id the id
   * @param subContractors the sub contractors
   * @return the offer by company id
   */
  @Override
  public List<CompanyOfferDTO> getOfferByCompanyId(String id, boolean subContractors) {
    LOGGER.log(Level.CONFIG,
      "Executing method getOfferByCompanyId, Parameters: id: {0}, subContractors: {1}",
      new Object[]{id, subContractors});

    UserDTO user = getUser();
    JPAQuery<TenantEntity> query = new JPAQuery<>(em);

    /**
     * Find current system offers when they are not from migration and not default.
     */
    /**
     * add the security constraints in the query. An offer can be viewed if it belongs to the tenant
     * of the user and if its submission is in a permitted status or it is from migration
     */
    List<String> allowedSubmissionsStatus = security.securityCheckSubmissionListStatus(user);
    List<String> tenantIds = security.getPermittedTenants(user);
    /**
     * get the submissions of the permitted tenants It would be nicer if in the query we had just
     * added a predicate stating that the id of the tenant is in the given list, but querydsl does
     * not support more than four levels of entity path
     */
    QSubmissionEntity qSubmissionEntity = QSubmissionEntity.submissionEntity;
    List<String> allowedSubmissionsTenant = new JPAQueryFactory(em).select(qSubmissionEntity.id)
      .from(qSubmissionEntity).where(qSubmissionEntity.project.tenant.id.in(tenantIds)).fetch();

    List<OfferEntity> offerEntities = new ArrayList<>();
    /**
     * the migrated offers are offers of the main tenant, so they can be viewed only by users of the
     * main tenant
     */
    String mainTenantId = query.select(qTenantEntity.id).from(qTenantEntity)
      .where(qTenantEntity.isMain.isTrue()).fetchOne();
    boolean addMigrationOffers = tenantIds.contains(mainTenantId);
    if (!subContractors) {
      offerEntities = query.select(qOfferEntity).from(qOfferEntity)
        .where(qOfferEntity.submittent.companyId.id.eq(id)
          .and(qOfferEntity.isDefaultOffer.eq(Boolean.FALSE)
            .or(qOfferEntity.isDefaultOffer.isNull()))
          .and(qOfferEntity.submittent.submissionId.id.in(allowedSubmissionsTenant))
          .and(qOfferEntity.submittent.submissionId.id.in(allowedSubmissionsStatus)))
        .fetch();

      offerEntities.addAll(new JPAQueryFactory(em).select(qOfferEntity).from(qOfferEntity)
        .where(qOfferEntity.submittent
          .in(JPAExpressions.select(qJointVentureEntity.submittent).from(qJointVentureEntity)
            .where(qJointVentureEntity.company.id.eq(id)))
          .and((qOfferEntity.isDefaultOffer.eq(Boolean.FALSE)
            .or(qOfferEntity.isDefaultOffer.isNull())))
          .and(qOfferEntity.submittent.submissionId.id.in(allowedSubmissionsTenant))
          .and(qOfferEntity.submittent.submissionId.id.in(allowedSubmissionsStatus)))
        .fetch());

      if (addMigrationOffers) {
        offerEntities.addAll(new JPAQueryFactory(em).select(qOfferEntity).from(qOfferEntity)
          .where(qOfferEntity.submittent.companyId.id.eq(id)
            .and(qOfferEntity.isDefaultOffer.eq(Boolean.FALSE)
              .or(qOfferEntity.isDefaultOffer.isNull()))
            .and(qOfferEntity.fromMigration.isTrue()))
          .fetch());

        offerEntities.addAll(new JPAQueryFactory(em).select(qOfferEntity).from(qOfferEntity)
          .where(qOfferEntity.submittent
            .in(JPAExpressions.select(qJointVentureEntity.submittent).from(qJointVentureEntity)
              .where(qJointVentureEntity.company.id.eq(id)))
            .and((qOfferEntity.isDefaultOffer.eq(Boolean.FALSE)
              .or(qOfferEntity.isDefaultOffer.isNull())))
            .and(qOfferEntity.fromMigration.isTrue()))
          .fetch());
      }
    }

    if (subContractors) {
      offerEntities = query.select(qOfferEntity).from(qOfferEntity)
        .where(qOfferEntity.submittent.companyId.id.eq(id)
          .and(qOfferEntity.submittent.submissionId.id.in(allowedSubmissionsTenant))
          .and(qOfferEntity.submittent.submissionId.id.in(allowedSubmissionsStatus)))
        .fetch();

      offerEntities.addAll((new JPAQueryFactory(em).select(qOfferEntity).from(qOfferEntity)
        .where(qOfferEntity.submittent
          .in(JPAExpressions.select(qJointVentureEntity.submittent).from(qJointVentureEntity)
            .where(qJointVentureEntity.company.id.eq(id)))
          .and(qOfferEntity.submittent.submissionId.id.in(allowedSubmissionsTenant))
          .and(qOfferEntity.submittent.submissionId.id.in(allowedSubmissionsStatus)))
        .fetch()));

      List<OfferEntity> offerEntitiesSubcontractors = new JPAQueryFactory(em).select(qOfferEntity)
        .from(qOfferEntity)
        .where(qOfferEntity.submittent
          .in(JPAExpressions.select(qSubContractors.submittent).from(qSubContractors)
            .where(qSubContractors.company.id.eq(id)))
          .and(qOfferEntity.submittent.submissionId.id.in(allowedSubmissionsTenant))
          .and(qOfferEntity.submittent.submissionId.id.in(allowedSubmissionsStatus)))
        .fetch();
      offerEntities.addAll(offerEntitiesSubcontractors);

      if (addMigrationOffers) {
        offerEntities.addAll(new JPAQueryFactory(em).select(qOfferEntity).from(qOfferEntity).where(
          qOfferEntity.submittent.companyId.id.eq(id).and(qOfferEntity.fromMigration.isTrue()))
          .fetch());

        offerEntities.addAll(new JPAQueryFactory(em).select(qOfferEntity).from(qOfferEntity)
          .where(qOfferEntity.submittent
            .in(JPAExpressions.select(qJointVentureEntity.submittent).from(qJointVentureEntity)
              .where(qJointVentureEntity.company.id.eq(id)))
            .and(qOfferEntity.fromMigration.isTrue()))
          .fetch());

        offerEntities.addAll(new JPAQueryFactory(em).select(qOfferEntity).from(qOfferEntity)
          .where(qOfferEntity.submittent
            .in(JPAExpressions.select(qSubContractors.submittent).from(qSubContractors)
              .where(qSubContractors.company.id.eq(id)))
            .and(qOfferEntity.fromMigration.isTrue()))
          .fetch());
      }
    }

    List<CompanyOfferDTO> companyOfferDTOs = new ArrayList<>(offerEntities.size());
    for (OfferEntity o : offerEntities) {
      CompanyOfferDTO companyOfferDTO = new CompanyOfferDTO();
      if (!o.getFromMigration()) {

        /**
         * Convert object history entity to object DTO and set the value to companyOfferDTO
         **/
        MasterListValueHistoryEntity objectEntity = new JPAQueryFactory(em)
          .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
          .where(qMasterListValueHistoryEntity.masterListValueId
            .eq(o.getSubmittent().getSubmissionId().getProject().getObjectName())
            .and(qMasterListValueHistoryEntity.toDate.isNull()))
          .fetchOne();
        MasterListValueHistoryDTO objectDTO =
          MasterListValueHistoryMapper.INSTANCE.toMasterListValueHistoryDTO(objectEntity);
        companyOfferDTO.setObjectName(objectDTO.getValue1());
        companyOfferDTO
          .setProjectName(o.getSubmittent().getSubmissionId().getProject().getProjectName());
        companyOfferDTO.setProjectId(o.getSubmittent().getSubmissionId().getProject().getId());
        /**
         * Convert workType history entity to worktType DTO and set the value to companyOfferDTO
         **/
        MasterListValueHistoryEntity workType = new JPAQueryFactory(em)
          .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
          .where(qMasterListValueHistoryEntity.masterListValueId
            .eq(o.getSubmittent().getSubmissionId().getWorkType())
            .and(qMasterListValueHistoryEntity.toDate.isNull()))
          .fetchOne();
        MasterListValueHistoryDTO workTypeDTO =
          MasterListValueHistoryMapper.INSTANCE.toMasterListValueHistoryDTO(workType);
        companyOfferDTO.setWorkType(workTypeDTO.getValue1() + " " + workTypeDTO.getValue2());

        /**
         * Calculate amount (Betrag) and set value to companyOfferDTO
         */
        companyOfferDTO.setAmmount(calculateAmount(o));
        // if o.getRank is null set Rank equals to o.getAwardRank
        companyOfferDTO.setRank((o.getRank() == null) ? o.getAwardRank() : o.getRank());
        companyOfferDTO.setIsAwarded(o.getIsAwarded());
        companyOfferDTO.setIsExcludedFromProcess(o.getIsExcludedFromProcess());
        companyOfferDTO.setDeadline2(o.getSubmittent().getSubmissionId().getSecondDeadline());
        companyOfferDTO.setProcess(o.getSubmittent().getSubmissionId().getProcess());
        companyOfferDTO.setSubmissionId(o.getSubmittent().getSubmissionId().getId());

        /**
         * Convert processType history entity to processType DTO and set the value to
         * companyOfferDTO
         **/
        MasterListValueHistoryEntity processType = new JPAQueryFactory(em)
          .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
          .where(qMasterListValueHistoryEntity.masterListValueId
            .eq(o.getSubmittent().getSubmissionId().getProcessType())
            .and(qMasterListValueHistoryEntity.toDate.isNull()))
          .fetchOne();
        MasterListValueHistoryDTO processTypeDTO =
          MasterListValueHistoryMapper.INSTANCE.toMasterListValueHistoryDTO(processType);
        companyOfferDTO.setProcessType(processTypeDTO.getValue1());
        companyOfferDTO.setJointVentures(
          CompanyMapper.INSTANCE.toCompanyDTO(o.getSubmittent().getJointVentures()));
        companyOfferDTO.setSubcontractors(
          CompanyMapper.INSTANCE.toCompanyDTO(o.getSubmittent().getSubcontractors()));
        /**
         * Convert department history entity to department DTO and set the value to companyOfferDTO
         **/
        DepartmentHistoryEntity departmentEntity =
          new JPAQueryFactory(em).select(qDepartmentHistoryEntity).from(qDepartmentHistoryEntity)
            .where(qDepartmentHistoryEntity.departmentId
              .eq(o.getSubmittent().getSubmissionId().getProject().getDepartment())
              .and(qDepartmentHistoryEntity.toDate.isNull()))
            .fetchOne();
        DepartmentHistoryDTO departmentDTO =
          DepartmentHistoryMapper.INSTANCE
            .toDepartmentHistoryDTO(departmentEntity, cacheBean.getActiveDirectorateHistorySD());

        companyOfferDTO.setDepartmentName(departmentDTO.getName());
        companyOfferDTO
          .setPmDepartmentName(o.getSubmittent().getSubmissionId().getPmDepartmentName());
        companyOfferDTO.setFromMigration(Boolean.FALSE);

        if (o.getSubmittent().getCompanyId().getId().equals(id)) {
          companyOfferDTO.setLeading(true);
        }

      } else {
        companyOfferDTO.setObjectName(o.getMigratedProject());
        companyOfferDTO.setWorkType(o.getMigratedSubmission());
        companyOfferDTO.setDepartmentName(o.getMigratedDepartment());
        companyOfferDTO.setPmDepartmentName(o.getMigreatedPM());
        companyOfferDTO.setProcedureName(o.getMigratedProcedure());
        companyOfferDTO.setAmmount(calculateAmount(o));
        // if o.getRank is null set Rank equals to o.getAwardRank
        companyOfferDTO.setRank((o.getRank() == null) ? o.getAwardRank() : o.getRank());
        companyOfferDTO.setDeadline2(o.getOfferDate());
        companyOfferDTO.setIsAwarded(o.getIsAwarded());
        companyOfferDTO.setIsExcludedFromProcess(o.getIsExcludedFromProcess());
        companyOfferDTO.setJointVentures(
          CompanyMapper.INSTANCE.toCompanyDTO(o.getSubmittent().getJointVentures()));
        companyOfferDTO.setSubcontractors(
          CompanyMapper.INSTANCE.toCompanyDTO(o.getSubmittent().getSubcontractors()));
        companyOfferDTO.setFromMigration(Boolean.TRUE);
        companyOfferDTO.setMigratedSubmission(o.getMigratedSubmission());

      }
      /**
       * in order to indicate if an offer is permitted to be viewed it must be checked if it is from
       * migration (migration offers can not be viewed) and if it meets the security constraints
       */
      companyOfferDTO.setIsViewPermitted(!o.getFromMigration() && security.isEntityPermitted(user,
        o.getSubmittent().getSubmissionId().getProject().getDepartment().getId(),
        o.getSubmittent().getSubmissionId().getId()));
      companyOfferDTOs.add(companyOfferDTO);

    }

    return companyOfferDTOs;
  }

  /**
   * A function that takes a String as parameter and return a CompanyProofDTO.
   *
   * @param id the id
   * @return the proof by company id
   */
  @Override
  public List<CompanyProofDTO> getProofByCompanyId(String id) {
    LOGGER.log(Level.CONFIG, "Executing method getProofByCompanyId, Parameters: id: {0}", id);

    /**
     * Query to take all companyProofs via companyId
     */
    List<CompanyProofEntity> companyProofEntities =
      new JPAQueryFactory(em).select(qCompanyProofEntity).from(qCompanyProofEntity)
        .where(qCompanyProofEntity.companyId.id.eq(id)).fetch();

    /**
     * create a list that the function returns
     */
    List<CompanyProofDTO> companyProofDTOs = new ArrayList<>();
    /**
     * Take for every companyProof using proofId ,all the ProofHistory and add them by one by one to
     * the returning companyProof list
     */
    for (CompanyProofEntity companyProof : companyProofEntities) {
      ProofHistoryEntity proofHistoryEntity = new JPAQueryFactory(em).select(qProofHistoryEntity)
        .from(qProofHistoryEntity).where(qProofHistoryEntity.proofId.eq(companyProof.getProofId())
          .and(qProofHistoryEntity.toDate.isNull()))
        .fetchOne();
      ProofHistoryDTO proofHistoryDTO =
        ProofHistoryMapper.INSTANCE.toProofHistoryDTO(proofHistoryEntity);
      CompanyProofDTO companyProofDTO =
        CompanyProofDTOMapper.INSTANCE.toCompanyProofDTO(companyProof);
      if (proofHistoryDTO != null && proofHistoryDTO.getActive().equals(1)) {
        companyProofDTO.setProof(proofHistoryDTO);
        companyProofDTOs.add(companyProofDTO);
      }

    }
    Collections.sort(companyProofDTOs, ComparatorUtil.sortCompanyProofDTOs);
    return companyProofDTOs;
  }

  /**
   * This method calculates the amount of a specific offer.
   *
   * @param offer the offer
   * @return the double
   */
  private Double calculateAmount(OfferEntity offer) {
    LOGGER.log(Level.CONFIG, "Executing method calculateAmount, Parameters: offer: {0}", offer);

    Double grossAmmount = null;
    Double discount = null;
    Double buildingCosts = null;
    Double discount2 = null;
    if (offer.getGrossAmount() == null && offer.getGrossAmountCorrected() == null) {
      return null;
    }
    if (offer.getIsCorrected() != null && offer.getIsCorrected()) {
      grossAmmount = offer.getGrossAmountCorrected();
    } else {
      grossAmmount = offer.getGrossAmount();
    }
    if (offer.getIsDiscountPercentage()) {
      discount = grossAmmount * offer.getDiscount() / 100;
    }
    /** Calculate building costs */
    if (offer.getIsBuildingCostsPercentage() != null && offer.getIsBuildingCostsPercentage()) {
      if (discount != null) {
        buildingCosts = (grossAmmount - discount) * offer.getBuildingCosts() / 100;
      } else {
        buildingCosts = grossAmmount * offer.getBuildingCosts() / 100;
      }
    }
    /** Calculate discount2 */
    if (offer.getIsDiscount2Percentage() != null && offer.getIsDiscount2Percentage()
      && discount != null) {
      if (buildingCosts != null) {
        discount2 = ((grossAmmount - (discount + buildingCosts)) * offer.getDiscount2()) / 100;
      } else {
        discount2 = ((grossAmmount - discount) * offer.getDiscount2()) / 100;
      }
    }
    /** Check the variables (grossAmmount, discount, buildingCosts, discount2) not to be null */
    if (grossAmmount != null) {
      if (discount == null) {
        discount = 0.0;
      }
      if (buildingCosts == null) {
        buildingCosts = 0.0;
      }
      if (discount2 == null) {
        discount2 = 0.0;
      }
      return grossAmmount - discount - buildingCosts - discount2;
    } else {
      return null;
    }
  }

  /**
   * Updates a company.
   *
   * @param companyDTO the company DTO
   * @param archivedPrevious true if archived is previously checked
   * @return the validation error
   */
  @Override
  public ValidationError updateCompany(CompanyDTO companyDTO, Boolean archivedPrevious) {
    LOGGER.log(Level.CONFIG, "Executing method updateCompany, Parameters: companyDTO: {0}",
      companyDTO);

    CompanyEntity companyEntity = CompanyMapper.INSTANCE.toCompany(companyDTO);
    if (companyEntity != null) {
      if (companyDTO.getBranches() != null) {
        companyEntity.setBranches(mapCompanyBranchesDTOtoEntities(companyDTO.getBranches()));
      }
      TenantEntity tenant =
        new JPAQueryFactory(em).selectFrom(qTenantEntity)
          .where(qTenantEntity.id
            .eq(getUser().getAttribute(USER_ATTRIBUTES.TENANT.getValue()).getData()))
          .fetchOne();
      companyEntity.setTenant(tenant);

      companyEntity.setModifiedOn(new Timestamp(System.currentTimeMillis()));
      companyEntity.setModUser(getUser().getId());

      // Make sure that the number of men, number of women, number of trainees and 50+ colleagues
      // contain at least 0 as a value when saved.
      if (companyEntity.getNumberOfMen() == null) {
        companyEntity.setNumberOfMen(0);
      }
      if (companyEntity.getNumberOfWomen() == null) {
        companyEntity.setNumberOfWomen(0);
      }
      if (companyEntity.getNumberOfTrainees() == null) {
        companyEntity.setNumberOfTrainees((long) 0);
      }
      if (companyEntity.getFiftyPlusColleagues() == null) {
        companyEntity.setFiftyPlusColleagues(0);
      }

      // Retrieve CompanyEntity from DB in order to compare the country with the new country of
      // CompanyDTO. If country is different, remove the proofs before saving the company.
      CompanyEntity currentEntity = em.find(CompanyEntity.class, companyDTO.getId());
      if (!companyEntity.getCountry().getId().equals(currentEntity.getCountry().getId())) {
        List<CompanyProofEntity> oldProofs =
          new JPAQueryFactory(em).select(qCompanyProofEntity).from(qCompanyProofEntity)
            .where(qCompanyProofEntity.companyId.id.eq(companyEntity.getId())).fetch();
        for (CompanyProofEntity oldProof : oldProofs) {
          em.remove(oldProof);
        }

        // Get all the current proofs that have the same country property as the companyEntity.
        List<ProofHistoryEntity> proofHistoryEntities =
          new JPAQueryFactory(em).selectFrom(qProofHistoryEntity)
            .where(qProofHistoryEntity.country.id.eq(companyEntity.getCountry().getId()),
              qProofHistoryEntity.toDate.isNull())
            .fetch();

        // If there are no proofs available for the company, just assign the current German proofs
        // as the
        // default values.
        if (proofHistoryEntities.isEmpty()) {
          proofHistoryEntities = new JPAQueryFactory(em).selectFrom(qProofHistoryEntity)
            .where(
              qProofHistoryEntity.country.id.eq(new JPAQueryFactory(em)
                .select(qCountryHistoryEntity.countryId.id).from(qCountryHistoryEntity)
                .where(qCountryHistoryEntity.toDate.isNull(),
                  qCountryHistoryEntity.countryName.eq("Deutschland"))
                .fetchOne()),
              qProofHistoryEntity.toDate.isNull())
            .fetch();
        }
        for (ProofHistoryEntity proofHistoryEntity : proofHistoryEntities) {
          CompanyProofEntity companyProofEntity = new CompanyProofEntity();
          companyProofEntity.setCompanyId(companyEntity);
          companyProofEntity.setProofDate(null);
          companyProofEntity.setProofId(proofHistoryEntity.getProofId());
          companyProofEntity.setRequired(proofHistoryEntity.getRequired());
          em.merge(companyProofEntity);
        }
      }
      /**
       * Get the proof history map in order to get the ProofHistoryDTO that needs to be checked.
       */
      Map<String, ProofHistoryDTO> proofHistoryMap = cacheBean.getActiveProofs();
      List<CompanyProofDTO> companyProofDTOs = new ArrayList<>();
      List<CompanyProofEntity> companyProofEntities =
        new JPAQueryFactory(em).select(qCompanyProofEntity).from(qCompanyProofEntity)
          .where(qCompanyProofEntity.companyId.id.eq(companyEntity.getId())).fetch();
      for (CompanyProofEntity companyProof : companyProofEntities) {
        ProofHistoryDTO proofHistoryDTO = proofHistoryMap.get(companyProof.getProofId().getId());
        CompanyProofDTO companyProofDTO =
          CompanyProofDTOMapper.INSTANCE.toCompanyProofDTO(companyProof);
        if (proofHistoryDTO != null && proofHistoryDTO.getActive().equals(1)) {
          companyProofDTO.setProof(proofHistoryDTO);
          companyProofDTOs.add(companyProofDTO);
        }
      }
      try {
        updateProofStatus(companyEntity, companyProofDTOs);
      } catch (OptimisticLockException exception) {
        throw new OptimisticLockException(OPTIMISTIC_LOCK);
      }

      // 2. if the Zustand has been changed - "Der Zustand der Firma hat sich auf "archiviert"
      // geändert."
      // OR "Der Zustand der Firma hat sich auf "aktiv" geändert.

      StringBuilder additionalInfo = new StringBuilder(companyEntity.getCompanyName()).append("[#]")
        .append(companyEntity.getProofStatusFabe()).append("[#]");

      if (companyEntity.getArchived() != null && companyEntity.getArchived() && !archivedPrevious) {
        auditLog(AuditEvent.UPDATE.name(), AuditMessages.UPDATE_COMPANY1.name(),
          companyEntity.getId(), additionalInfo.toString());
      } else if (companyEntity.getArchived() != null && !companyEntity.getArchived()
        && archivedPrevious) {
        auditLog(AuditEvent.UPDATE.name(), AuditMessages.UPDATE_COMPANY2.name(),
          companyEntity.getId(), additionalInfo.toString());
      }
    } else {
      return new ValidationError(OPTIMISTIC_LOCK_ERROR_FIELD,
        ValidationMessages.OPTIMISTIC_LOCK_DELETE);
    }
    return null;
  }

  /**
   * Update proof status.
   *
   * @param companyEntity the company entity
   * @param companyProofDTOs the company proof DT os
   */
  private void updateProofStatus(CompanyEntity companyEntity,
    List<CompanyProofDTO> companyProofDTOs) {
    LOGGER.log(Level.CONFIG,
      "Executing method updateProofStatus, Parameters: companyEntity: {0}, companyProofDTOs: {1}",
      new Object[]{companyEntity, companyProofDTOs});
    if (companyEntity != null) {
      boolean changeProofStatus = true;
      int proofStatus = ProofStatus.NOT_ACTIVE.getValue();
      List<CompanyProofEntity> companyProofEntities = new ArrayList<>();
      /** create a new variable to check the ProofStatus if it must change */
      if (companyProofDTOs != null && !companyProofDTOs.isEmpty()) {
        // Set the hasChanged value for every company proof DTO.
        companyProofService.updateHasChangedValues(companyProofDTOs, companyEntity.getId());
        boolean valuesSetToNull = false;
        for (CompanyProofDTO companyProofDTO : companyProofDTOs) {
          /**
           * ProofStatus is changed when the validityPeriod is above the date
           */
          /** The validity period for a proof must be 1 year exactly. */
          /** Check validity date */
          if (changeProofStatus && companyProofDTO.getRequired()
            && companyProofDTO.getProofDate() == null
            || (companyProofDTO.getRequired() && companyProofDTO.getProofDate() != null
            && companyProofDTO.getProof().getValidityPeriod() != null
            && !LocalDate.now().isBefore(companyProofDTO.getProofDate().toLocalDateTime()
            .toLocalDate().plusMonths(companyProofDTO.getProof().getValidityPeriod())))) {
            changeProofStatus = false;
          }

          /** In every update of a Proof the system must save the date and the User */
          if (companyProofDTO.isHasChanged()) {
            CompanyProofEntity companyProofEntity =
              CompanyProofDTOMapper.INSTANCE.toCompanyProof(companyProofDTO);

            // If at least one company proof has been changed, the following values need to be set
            // as null (if not already set).
            if (!valuesSetToNull) {
              companyEntity.setProofDocModBy(null);
              companyEntity.setProofDocModOn(null);
              companyEntity.setProofDocSubmitDate(null);
              valuesSetToNull = true;
            }
            if (companyProofEntity != null) {
              /** By update take the current date and time */
              companyProofEntity.setModDate(new Timestamp(System.currentTimeMillis()));

              TenantEntity tenant = em.find(TenantEntity.class,
                  getUser().getAttribute(USER_ATTRIBUTES.TENANT.getValue()).getData());
              /** Set modUser */
              companyProofEntity.setModUser(tenant.getName() + " "
                  + getUser().getAttribute(USER_ATTRIBUTES.FIRSTNAME.getValue()).getData() + " "
                  + getUser().getAttribute(USER_ATTRIBUTES.LASTNAME.getValue()).getData());
              em.merge(companyProofEntity);
              em.flush();
              companyProofEntities.add(companyProofEntity);
            }
          }
        }
        companyEntity.setCompanyProofs(companyProofEntities);
      }
      if (changeProofStatus) {
        proofStatus = ProofStatus.ALL_PROOF.getValue();
      }
      if (companyEntity.getConsultKaio()) {
        companyEntity.setProofStatus(ProofStatus.WITH_KAIO.getValue());
        companyEntity.setProofStatusFabe(ProofStatus.WITH_KAIO.getValue());
      } else {
        // Update company proof status only when company status was ALL_PROOF or NOT_ACTIVE.
        companyEntity.setProofStatus(proofStatus);
        /**
         * if the checkbox FABE is checked, then the proof status FABE (which will be viewed only by
         * users who have the right to view this proof status) is set to FABE, otherwise it is set
         * to the calculated as the normal proof status
         */
        if (companyEntity.getConsultAdmin()) {
          companyEntity.setProofStatusFabe(ProofStatus.WITH_FABE.getValue());
        } else {
          companyEntity.setProofStatusFabe(companyEntity.getProofStatus());
        }
      }
      try {
        em.merge(companyEntity);
        em.flush();
      } catch (OptimisticLockException exception) {
        throw new OptimisticLockException(OPTIMISTIC_LOCK);
      }
    }
  }

  /**
   * Find If a company is participating in an Offer This function will be called on company delete
   * for every company that will be checked through this function if it will be deleted or not.
   *
   * @param id the id
   * @return the boolean
   */
  @Override
  public Boolean findIfCompanyParticipate(String id) {
    LOGGER.log(Level.CONFIG, "Executing method findIfCompanyParticipate, Parameters: id: {0}", id);

    Boolean participates = false;
    /* check if participates in an offer */
    long countOfferEntities = new JPAQueryFactory(em).select(qOfferEntity).from(qOfferEntity)
      .where(qOfferEntity.submittent.companyId.id.eq(id)).fetchCount();
    if (countOfferEntities != 0) {
      participates = true;
    } else {
      /* if not, check if participates as joint venture */
      countOfferEntities = new JPAQueryFactory(em).select(qOfferEntity).from(qOfferEntity)
        .where(qOfferEntity.submittent.in(JPAExpressions.select(qJointVentureEntity.submittent)
          .from(qJointVentureEntity).where(qJointVentureEntity.company.id.eq(id))))
        .fetchCount();
      if (countOfferEntities != 0) {
        participates = true;
      } else {
        /* if not, check if participates as subcontractor */
        countOfferEntities = new JPAQueryFactory(em).select(qOfferEntity).from(qOfferEntity)
          .where(qOfferEntity.submittent.in(JPAExpressions.select(qSubContractors.submittent)
            .from(qSubContractors).where(qSubContractors.company.id.eq(id))))
          .fetchCount();
        if (countOfferEntities != 0) {
          participates = true;
        }
      }
    }
    return participates;
  }

  /**
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.CompanyService#updateProofs(java.util.List)
   */
  @Override
  public ValidationError updateProofs(List<CompanyProofDTO> companyProofDTOs) {
    LOGGER.log(Level.CONFIG, "Executing method updateProofs, Parameters: companyProofDTOs: {0}",
      companyProofDTOs);
    
    // Change proofStatus if validity Period is over or the date is null (inserted empty field)
    if (companyProofDTOs != null && !companyProofDTOs.isEmpty()) {
      Long dtoVersion = companyProofDTOs.get(0).getCompanyId().getVersion();
      CompanyEntity company = CompanyMapper.INSTANCE.toCompany(companyProofDTOs.get(0).getCompanyId());          
      if (company != null) {
        if (dtoVersion.equals(company.getVersion())) {
          updateProofStatus(company, companyProofDTOs);
          // when the new proofStatus is 'all proofs available' then update also the task
          if (company.getProofStatus().equals(ProofStatus.ALL_PROOF.getValue())) {
            taskService.updateHybridTask(null, company.getId(), TaskTypes.PROOF_REQUEST_PL_XY);
          }
        } else {
          throw new OptimisticLockException(OPTIMISTIC_LOCK);
        }
      } else {
        return new ValidationError(OPTIMISTIC_LOCK_ERROR_FIELD,
            ValidationMessages.OPTIMISTIC_LOCK_DELETE);
      }
    }
    return null;
  }

  /**
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.CompanyService#calculateExpirationDate(java.lang
   * . String)
   */
  @Override
  public Date calculateExpirationDate(String companyId) {
    LOGGER.log(Level.CONFIG, "Executing method calculateExpirationDate, Parameters: companyId: {0}",
      companyId);

    List<CompanyProofEntity> companyProofEntities =
      new JPAQueryFactory(em).select(qCompanyProofEntity).from(qCompanyProofEntity)
        .where(qCompanyProofEntity.companyId.id.eq(companyId)
          .and(qCompanyProofEntity.required.eq(Boolean.TRUE))
          .and(qCompanyProofEntity.proofDate.isNotNull()))
        .orderBy(qCompanyProofEntity.proofDate.desc()).fetch();
    Date date = null;
    if (!companyProofEntities.isEmpty()) {
      date = dateCalculation(companyProofEntities.get(companyProofEntities.size() - 1));

      for (CompanyProofEntity companyProof : companyProofEntities) {
        Date currentDate = dateCalculation(companyProof);
        if (currentDate.before(date)) {
          date = currentDate;
        }

      }
    }

    return date;
  }

  /**
   * This method adds the validity period to the company's proof date.
   *
   * @param companyProofEntity the company proof entity
   * @return the modified date
   */
  private Date dateCalculation(CompanyProofEntity companyProofEntity) {
    LOGGER
      .log(Level.CONFIG, "Executing method dateCalculation, Parameters: companyProofEntity: {0}",
        companyProofEntity);

    Integer firstPeriod = new JPAQueryFactory(em).select(qProofHistoryEntity.validityPeriod)
      .from(qProofHistoryEntity).where(qProofHistoryEntity.proofId
        .eq(companyProofEntity.getProofId()).and(qProofHistoryEntity.toDate.isNull()))
      .fetchOne();

    Calendar cal = Calendar.getInstance();
    cal.setTime(companyProofEntity.getProofDate());
    cal.add(Calendar.MONTH, +firstPeriod);
    cal.add(Calendar.DATE, -1);
    return cal.getTime();
  }

  /**
   * This method loads all companies that are not from migration.
   *
   * @return the List<CompanyDTO>
   */
  @Override
  public List<CompanyDTO> getAllNonMigratedCompanies() {

    LOGGER.log(Level.CONFIG, "Executing method getAllNonMigratedCompanies");

    List<CompanyEntity> companyEntities = new JPAQueryFactory(em).select(qCompanyEntity)
      .from(qCompanyEntity).where(qCompanyEntity.createOn.isNotNull()).fetch();
    return CompanyMapper.INSTANCE.toCompanyDTO(companyEntities);
  }

  /**
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.CompanyService#getProofState(ch.bern.submiss .
   * services.api.dto.CompanyDTO)
   */
  @Override
  public int getProofState(CompanyDTO companyDTO) {
    LOGGER.log(Level.CONFIG, "Executing method getProofState, Parameters: companyDTO: {0}",
      companyDTO);

    boolean changeProofStatus = true;
    int proofStatus = ProofStatus.NOT_ACTIVE.getValue();

    /** check the company proofs, in order to determine the proof status */
    List<CompanyProofEntity> companyProofEntities =
      new JPAQueryFactory(em).select(qCompanyProofEntity).from(qCompanyProofEntity)
        .where(qCompanyProofEntity.companyId.id.eq(companyDTO.getId())).fetch();

    List<CompanyProofDTO> companyProofDTOs = new ArrayList<>();

    /**
     * for every companyProof using proofId ,add all the ProofHistory one by one to the companyProof
     * list
     */
    for (CompanyProofEntity companyProof : companyProofEntities) {
      ProofHistoryEntity proofHistoryEntity = new JPAQueryFactory(em).select(qProofHistoryEntity)
        .from(qProofHistoryEntity).where(qProofHistoryEntity.proofId.eq(companyProof.getProofId())
          .and(qProofHistoryEntity.toDate.isNull()))
        .fetchOne();

      ProofHistoryDTO proofHistoryDTO =
        ProofHistoryMapper.INSTANCE.toProofHistoryDTO(proofHistoryEntity);
      CompanyProofDTO companyProofDTO =
        CompanyProofDTOMapper.INSTANCE.toCompanyProofDTO(companyProof);
      if (proofHistoryDTO != null && proofHistoryDTO.getActive().equals(1)) {
        companyProofDTO.setProof(proofHistoryDTO);
        companyProofDTOs.add(companyProofDTO);
      }
    }

    if (companyProofDTOs != null && !companyProofDTOs.isEmpty()) {
      for (CompanyProofDTO companyProofDTO : companyProofDTOs) {
        /**
         * ProofStatus is changed when the validityPeriod is above the date
         */
        /** The validity period for a proof must be 1 year exactly. */
        /** Check validity date */
        if (changeProofStatus && companyProofDTO.getRequired()
          && companyProofDTO.getProofDate() == null
          || (companyProofDTO.getRequired() && companyProofDTO.getProofDate() != null
          && companyProofDTO.getProof().getValidityPeriod() != null
          && !LocalDate.now().isBefore(companyProofDTO.getProofDate().toLocalDateTime()
          .toLocalDate().plusMonths(companyProofDTO.getProof().getValidityPeriod())))) {
          changeProofStatus = false;
        }
      }
      if (changeProofStatus) {
        proofStatus = ProofStatus.ALL_PROOF.getValue();
      }
    }
    return proofStatus;
  }

  /**
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.CompanyService#getCompanyEmailsById(java.util.List)
   */
  @Override
  public List<String> getCompanyEmailsById(List<String> companyIds) {
    LOGGER.log(Level.CONFIG, "Executing method getCompanyEmailsById");

    return new JPAQueryFactory(em).select(qCompanyEntity.companyEmail).from(qCompanyEntity)
      .where(qCompanyEntity.id.in(companyIds)).fetch();
  }

  /**
   * Audit log.
   *
   * @param event the event
   * @param auditMessage the audit message
   * @param companyId the company id
   */
  private void auditLog(String event, String auditMessage, String companyId,
    String additionalInfo) {
    LOGGER.log(Level.CONFIG,
      "Executing method auditLog, Parameters: event: {0}, auditMessage: {1}, companyId: {2}, additionalInfo: {3}",
      new Object[]{event, auditMessage, companyId, additionalInfo});

    audit.createLogAudit(AuditLevel.COMPANY_LEVEL.name(), event, AuditGroupName.COMPANY.name(),
      auditMessage, getUser().getId(), companyId, null, additionalInfo,
      LookupValues.EXTERNAL_LOG);
  }

  private List<CompanyEntity> mapCompanyBranchesDTOtoEntities(List<CompanyDTO> companyBranches) {

    List<String> branchesIds = new ArrayList<>();
    for (CompanyDTO branch : companyBranches) {
      branchesIds.add(branch.getId());
    }
    JPAQuery<CompanyEntity> query = new JPAQuery<>(em);
    QCompanyEntity branch = QCompanyEntity.companyEntity;
    query.select(branch).from(branch).where(branch.id.in(branchesIds));
    return query.fetch();

  }

  @Override
  public List<CompanyDTO> getCompaniesByCountryId(String countryId) {
    return CompanyMapper.INSTANCE.toCompanyDTO(new JPAQueryFactory(em).selectFrom(qCompanyEntity)
      .where(qCompanyEntity.country.id.eq(countryId)).fetch());
  }

  @Override
  public List<CompanyDTO> getCompaniesById(List<String> companyIds) {
    LOGGER.log(Level.CONFIG, "Executing method getCompanyEmailsById");

    return CompanyMapper.INSTANCE
      .toCompanyDTO(new JPAQueryFactory(em).select(qCompanyEntity).from(qCompanyEntity)
        .where(qCompanyEntity.id.in(companyIds)).fetch());
  }

  @Override
  public int retrieveCompanyProofStatus(String companyId) {
    CompanyEntity company = em.find(CompanyEntity.class, companyId);
    return company.getProofStatus();
  }
}
