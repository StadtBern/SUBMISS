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

import ch.bern.submiss.services.api.administration.EmailService;
import ch.bern.submiss.services.api.administration.SDCountryService;
import ch.bern.submiss.services.api.administration.SDDepartmentService;
import ch.bern.submiss.services.api.administration.SDDirectorateService;
import ch.bern.submiss.services.api.administration.SDLogibService;
import ch.bern.submiss.services.api.administration.SDProofService;
import ch.bern.submiss.services.api.administration.SDService;
import ch.bern.submiss.services.api.administration.SDTenantService;
import ch.bern.submiss.services.api.administration.SubmissionService;
import ch.bern.submiss.services.api.administration.UserAdministrationService;
import ch.bern.submiss.services.api.constants.AuditEvent;
import ch.bern.submiss.services.api.constants.AuditGroupName;
import ch.bern.submiss.services.api.constants.AuditLevel;
import ch.bern.submiss.services.api.constants.CategorySD;
import ch.bern.submiss.services.api.constants.Group;
import ch.bern.submiss.services.api.constants.Process;
import ch.bern.submiss.services.api.constants.ShortCode;
import ch.bern.submiss.services.api.dto.DepartmentDTO;
import ch.bern.submiss.services.api.dto.DirectorateDTO;
import ch.bern.submiss.services.api.dto.MasterListDTO;
import ch.bern.submiss.services.api.dto.MasterListTypeDataDTO;
import ch.bern.submiss.services.api.dto.MasterListValueHistoryDTO;
import ch.bern.submiss.services.api.dto.SettingsDTO;
import ch.bern.submiss.services.api.dto.SignatureCopyDTO;
import ch.bern.submiss.services.api.dto.SignatureProcessTypeDTO;
import ch.bern.submiss.services.api.dto.SignatureProcessTypeEntitledDTO;
import ch.bern.submiss.services.api.dto.SubmissUserDTO;
import ch.bern.submiss.services.api.dto.TenantDTO;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.api.util.LookupValues.USER_ATTRIBUTES;
import ch.bern.submiss.services.impl.mappers.CycleAvoidingMappingContext;
import ch.bern.submiss.services.impl.mappers.DepartmentMapper;
import ch.bern.submiss.services.impl.mappers.MasterListMapper;
import ch.bern.submiss.services.impl.mappers.MasterListTypeDataMapper;
import ch.bern.submiss.services.impl.mappers.MasterListValueHistoryMapper;
import ch.bern.submiss.services.impl.mappers.SignatureCopyDTOMapper;
import ch.bern.submiss.services.impl.mappers.SignatureProcessTypeDTOMapper;
import ch.bern.submiss.services.impl.mappers.TenantMapper;
import ch.bern.submiss.services.impl.model.MasterListEntity;
import ch.bern.submiss.services.impl.model.MasterListValueEntity;
import ch.bern.submiss.services.impl.model.MasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.ProjectEntity;
import ch.bern.submiss.services.impl.model.QMasterListEntity;
import ch.bern.submiss.services.impl.model.QMasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.QSignatureCopyEntity;
import ch.bern.submiss.services.impl.model.QSignatureProcessTypeEntitledEntity;
import ch.bern.submiss.services.impl.model.QSignatureProcessTypeEntity;
import ch.bern.submiss.services.impl.model.SignatureCopyEntity;
import ch.bern.submiss.services.impl.model.SignatureProcessTypeEntitledEntity;
import ch.bern.submiss.services.impl.model.SignatureProcessTypeEntity;
import ch.bern.submiss.services.impl.util.ComparatorUtil;
import com.eurodyn.qlack2.fuse.fileupload.api.FileUpload;
import com.eurodyn.qlack2.fuse.fileupload.api.response.FileGetResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;
import org.ops4j.pax.cdi.api.OsgiService;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

/**
 * The Class SDServiceImpl.
 */
@Transactional
@OsgiServiceProvider(classes = {SDService.class})
@Singleton
public class SDServiceImpl extends BaseService implements SDService {

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER = Logger.getLogger(SDServiceImpl.class.getName());
  /**
   * The Constant KANTON_BERN.
   */
  private static final String KANTON_BERN = "Kanton Bern";
  /**
   * The users service.
   */
  @Inject
  protected UserAdministrationService usersService;
  /**
   * The s D department service.
   */
  @Inject
  protected SDDepartmentService sDDepartmentService;
  /**
   * The s D directorate service.
   */
  @Inject
  protected SDDirectorateService sDDirectorateService;
  /**
   * The s D country service.
   */
  @Inject
  protected SDCountryService sDCountryService;
  /**
   * The s D logib service.
   */
  @Inject
  protected SDLogibService sDLogibService;
  /**
   * The s D proof service.
   */
  @Inject
  protected SDProofService sDProofService;
  /**
   * The email service.
   */
  @Inject
  protected EmailService emailService;
  /**
   * The tenant service.
   */
  @Inject
  protected SDTenantService tenantService;
  /**
   * The q master list value history entity.
   */
  QMasterListValueHistoryEntity qMasterListValueHistoryEntity =
    QMasterListValueHistoryEntity.masterListValueHistoryEntity;
  /**
   * The q master list entity.
   */
  QMasterListEntity qMasterListEntity = QMasterListEntity.masterListEntity;
  /**
   * The cache bean.
   */
  @Inject
  private CacheBean cacheBean;
  /**
   * The file upload.
   */
  @OsgiService
  @Inject
  private FileUpload fileUpload;
  /**
   * The submission service.
   */
  @Inject
  private SubmissionService submissionService;
  @Inject
  private AuditBean audit;

  /**
   * Returns Master List Value History DTOs according to the name parameter and tenant id.
   *
   * @param type the type
   * @param tenantId the tenant id
   * @return Master List Value History DTOs
   */
  @Override
  public List<MasterListValueHistoryDTO> masterListValueHistoryQuery(CategorySD type,
    String tenantId) {

    LOGGER.log(Level.CONFIG,
      "Executing method masterListValueHistoryQuery, Parameters: type: {0}, "
        + "tenantId",
      new Object[]{type, tenantId});

    JPAQuery<ProjectEntity> query = new JPAQuery<>(em);
    BooleanBuilder whereClause = new BooleanBuilder();

    if (!StringUtils.isBlank(tenantId)) {
      whereClause.and(qMasterListValueHistoryEntity.tenant.id.eq(tenantId));
    }

    if (type != null) {
      whereClause.and(qMasterListValueHistoryEntity.masterListValueId.masterList.categoryType
        .equalsIgnoreCase(type.getValue()));
    }

    return MasterListValueHistoryMapper.INSTANCE.toMasterListValueHistoryDTO(
      query.select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
        .where(whereClause, qMasterListValueHistoryEntity.toDate.isNull())
        .orderBy(qMasterListValueHistoryEntity.value1.asc()).fetch());
  }

  /**
   * Returns Master List Value History DTOs according to the given type.
   *
   * @param type the type
   * @return Master List Value History DTOs
   */
  @Override
  public List<MasterListValueHistoryDTO> getMasterListHistoryByType(String type) {

    LOGGER.log(Level.CONFIG,
      "Executing method getMasterListHistoryByType, Parameters: type: {0}",
      type);

    return MasterListValueHistoryMapper.INSTANCE
      .toMasterListValueHistoryDTO(new JPAQueryFactory(em).select(qMasterListValueHistoryEntity)
        .from(qMasterListValueHistoryEntity)
        .where(
          qMasterListValueHistoryEntity.masterListValueId.masterList.name
            .equalsIgnoreCase(type),
          qMasterListValueHistoryEntity.active.isTrue(),
          qMasterListValueHistoryEntity.toDate.isNull(),
          qMasterListValueHistoryEntity.tenant.id.in(security.getPermittedTenants(getUser())))
        .orderBy(qMasterListValueHistoryEntity.value1.asc()).fetch());
  }

  /**
   * Gets the master list history by code.
   *
   * @param codes the codes
   * @return the master list history by code
   */
  @Override
  public List<MasterListValueHistoryDTO> getMasterListHistoryByCode(List<String> codes) {

    LOGGER.log(Level.CONFIG,
      "Executing method getMasterListHistoryByCode, Parameters: codes: {0}",
      codes);

    return MasterListValueHistoryMapper.INSTANCE.toMasterListValueHistoryDTO(new JPAQueryFactory(em)
      .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
      .where(
        qMasterListValueHistoryEntity.masterListValueId.masterList.name
          .equalsIgnoreCase("Vorlagenart"),
        (qMasterListValueHistoryEntity.tenant.id
          .eq(getUser().getAttribute(USER_ATTRIBUTES.TENANT.getValue()).getData())),
        (qMasterListValueHistoryEntity.shortCode.in(codes)))
      .orderBy(qMasterListValueHistoryEntity.value1.asc()).fetch());
  }

  /**
   * Retrieve signature.
   *
   * @param departmentId the department id
   * @param directorateId the directorate id
   * @param process the process
   * @return the signature process type DTO
   */
  @Override
  public SignatureProcessTypeDTO retrieveSignature(String departmentId, String directorateId,
    Process process) {

    LOGGER.log(Level.CONFIG,
      "Executing method retrieveSignature, Parameters: departmentId: {0}, "
        + "directorateId: {1}, process: {2}",
      new Object[]{departmentId, directorateId, process});

    QSignatureProcessTypeEntity qSignatureProcessTypeEntity =
      QSignatureProcessTypeEntity.signatureProcessTypeEntity;
    SignatureProcessTypeDTO signatureProcessTypeDTO =
      SignatureProcessTypeDTOMapper.INSTANCE.toSignatureProcessTypeDTO(new JPAQueryFactory(em)
        .select(qSignatureProcessTypeEntity).from(qSignatureProcessTypeEntity)
        .where(qSignatureProcessTypeEntity.process.eq(process),
          qSignatureProcessTypeEntity.signature.directorate.id.eq(directorateId),
          (qSignatureProcessTypeEntity.signature.department.id.eq(departmentId)))
        .fetchOne(), new CycleAvoidingMappingContext());
    sortSignaturesBySortNumber(signatureProcessTypeDTO);
    return sortSignaturesBySortNumber(signatureProcessTypeDTO);
  }

  /**
   * Gets the master list types.
   *
   * @return the master list types
   */
  @Override
  public List<MasterListDTO> getMasterListTypes() {

    LOGGER.log(Level.CONFIG, "Executing method getMasterListTypes");

    SubmissUserDTO user = usersService.getUserById(getUser().getId());
    // Only administrators have access to the master list data.
    if (user.getUserGroup().getName().equals(Group.ADMIN.getValue())) {
      List<MasterListEntity> masterListEntities =
        new JPAQueryFactory(em).selectFrom(qMasterListEntity).fetch();
      // Get tenant information.
      TenantDTO tenant = user.getTenant();
      // Check if user belongs to main tenant.
      boolean isMainTenant = tenant.getIsMain();
      List<MasterListDTO> masterListTypes = new ArrayList<>();
      for (MasterListEntity masterListEntity : masterListEntities) {
        // If the tenant is "Kanton Bern" and the category type is one of the following, just go to
        // the next iteration.
        if (tenant.getName().equals(KANTON_BERN) && (masterListEntity.getCategoryType()
          .equals(CategorySD.SETTLEMENT_TYPE.getValue())
          || masterListEntity.getCategoryType().equals(CategorySD.PROCESS_TYPE.getValue())
          || masterListEntity.getCategoryType().equals(CategorySD.PROCESS.getValue())
          || masterListEntity.getCategoryType().equals(CategorySD.AUTHORISED_SIGNATORY.getValue())
          || masterListEntity.getCategoryType().equals(CategorySD.OBJECT.getValue())
          || masterListEntity.getCategoryType().equals(CategorySD.VAT_RATE.getValue())
          || masterListEntity.getCategoryType().equals(CategorySD.CALCULATION_FORMULA.getValue())
          || masterListEntity.getCategoryType().equals(CategorySD.NEGOTIATION_REASON.getValue())
          || masterListEntity.getCategoryType().equals(CategorySD.EXCLUSION_CRITERION.getValue())
          || masterListEntity.getCategoryType().equals(CategorySD.CANCEL_REASON.getValue()))) {
          continue;
        }
        if (masterListEntity.getCategoryType().equals(CategorySD.WORKTYPE.getValue())
          || masterListEntity.getCategoryType().equals(CategorySD.COUNTRY.getValue())
          || masterListEntity.getCategoryType().equals(CategorySD.ILO.getValue())
          || masterListEntity.getCategoryType().equals(CategorySD.LOGIB.getValue())
          || masterListEntity.getCategoryType().equals(CategorySD.PROOFS.getValue())) {
          if (isMainTenant) {
            // Only add the above master list values to the returned list, if the user belongs to
            // the main tenant.
            masterListTypes.add(MasterListMapper.INSTANCE.toMasterListDTO(masterListEntity));
          }
        } else {
          masterListTypes.add(MasterListMapper.INSTANCE.toMasterListDTO(masterListEntity));
        }
      }
      Collections.sort(masterListTypes, ComparatorUtil.sortMasterListValuesByName);
      return masterListTypes;
    }
    return Collections.emptyList();
  }

  /**
   * Gets the master list value history entities by type.
   *
   * @param type the master list type
   * @return the master list value history entities by type
   */
  private List<MasterListValueHistoryEntity> getMasterListDataByType(String type) {

    LOGGER.log(Level.CONFIG, "Executing method getMasterListDataByType, Parameters: type: {0}",
        type);
   
    // Retrieve all Master Data instead of the one that is used in order to save the lpr command for
    // the print implementation.

    return (new JPAQueryFactory(em).select(qMasterListValueHistoryEntity)
        .from(qMasterListValueHistoryEntity)
        .where(
            qMasterListValueHistoryEntity.masterListValueId.masterList.categoryType
                .equalsIgnoreCase(type),
            qMasterListValueHistoryEntity.toDate.isNull(),
            qMasterListValueHistoryEntity.shortCode.isNull()
                .or(qMasterListValueHistoryEntity.shortCode.isNotNull().and(
                    qMasterListValueHistoryEntity.shortCode.notIn(LookupValues.PRINT_COMMAND))),
            qMasterListValueHistoryEntity.tenant.id.in(security.getPermittedTenants(getUser())))
        .orderBy(qMasterListValueHistoryEntity.value1.asc()).fetch());
  }

  /**
   * Returns active and inactive Master List Value History DTOs according to the given type.
   *
   * @param type the type
   * @return Master List Value History DTOs
   */
  @Override
  public List<MasterListValueHistoryDTO> getAllMasterListDataDTOByType(String type) {

    LOGGER.log(Level.CONFIG,
      "Executing method getAllMasterListDataDTOByType, Parameters: type: {0}",
      type);

    return MasterListValueHistoryMapper.INSTANCE
      .toMasterListValueHistoryDTO(getMasterListDataByType(type));
  }

  /**
   * Gets the master list type data.
   *
   * @param type the master list type
   * @return the master list type data
   */
  @Override
  public List<MasterListTypeDataDTO> getMasterListTypeData(String type) {

    LOGGER.log(Level.CONFIG,
      "Executing method getMasterListTypeData, Parameters: type: {0}",
      type);

    List<MasterListTypeDataDTO> typeDTOs = null;
    if (type.equals(CategorySD.DEPARTMENT.getValue())) {
      typeDTOs = sDDepartmentService.departmentToTypeData();
    } else if (type.equals(CategorySD.DIRECTORATE.getValue())) {
      typeDTOs = sDDirectorateService.directorateToTypeData();
    } else if (type.equals(CategorySD.COUNTRY.getValue())) {
      typeDTOs = sDCountryService.countryToTypeData();
    } else if (type.equals(CategorySD.LOGIB.getValue())) {
      typeDTOs = sDLogibService.logibToTypeData();
    } else if (type.equals(CategorySD.PROOFS.getValue())) {
      typeDTOs = sDProofService.proofToTypeData();
    } else if (type.equals(CategorySD.EMAIL_TEMPLATE.getValue())) {
      typeDTOs = emailService.emailToTypeData();
    } else {
      typeDTOs =
        MasterListTypeDataMapper.INSTANCE.toMasterListTypeDataDTOs(getMasterListDataByType(type));
      // If the type is CANCEL_REASON, EXCLUSION_CRITERION, NEGOTIATION_REASON or WORKTYPE, then
      // invert the description and addedDescription values.
      if (type.equals(CategorySD.CANCEL_REASON.getValue())
        || type.equals(CategorySD.EXCLUSION_CRITERION.getValue())
        || type.equals(CategorySD.NEGOTIATION_REASON.getValue())
        || type.equals(CategorySD.WORKTYPE.getValue())) {
        String tempDescription = null;
        for (MasterListTypeDataDTO typeDTO : typeDTOs) {
          tempDescription = typeDTO.getDescription();
          typeDTO.setDescription(typeDTO.getAddedDescription());
          typeDTO.setAddedDescription(tempDescription);
        }
      }
    }
    // Sort master list type data accordingly.
    if (type.equals(CategorySD.CANCEL_REASON.getValue())
      || type.equals(CategorySD.EXCLUSION_CRITERION.getValue())) {
      Collections.sort(typeDTOs, ComparatorUtil.sortMasterListTypeDataByAddedDescription);
    } else {
      Collections.sort(typeDTOs, ComparatorUtil.sortMasterListTypeDataByDescription);
    }
    return typeDTOs;
  }

  /**
   * Gets the master list value history entry by id.
   *
   * @param entryId the entry id
   * @return the master list value history entry
   */
  @Override
  public MasterListValueHistoryDTO getSDEntryById(String entryId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getSDEntryById, Parameters: entryId: {0}",
      entryId);

    return MasterListValueHistoryMapper.INSTANCE.toMasterListValueHistoryDTO(
      new JPAQueryFactory(em).selectFrom(qMasterListValueHistoryEntity)
        .where(qMasterListValueHistoryEntity.id.eq(entryId)).fetchOne());
  }

  /**
   * Checks if the description is unique.
   *
   * @param description the description
   * @param type the master list type
   * @param id the master list value history id
   * @return true, if description is unique
   */
  @Override
  public boolean isDescriptionUnique(String description, String type, String id) {

    LOGGER.log(Level.CONFIG,
      "Executing method isDescriptionUnique, Parameters: description: {0}, type: {1}, "
        + "id: {2}",
      new Object[]{description, type, id});

    if (id == null) {
      // If the id is null (case of creating a new entry), replace the null value with an empty
      // String in order to avoid the null pointer exception.
      id = StringUtils.EMPTY;
    }
    if (type.equals(CategorySD.CANCEL_REASON.getValue())
      || type.equals(CategorySD.WORKTYPE.getValue())
      || type.equals(CategorySD.EXCLUSION_CRITERION.getValue())
      || type.equals(CategorySD.NEGOTIATION_REASON.getValue())) {
      return (new JPAQueryFactory(em).select(qMasterListValueHistoryEntity)
        .from(qMasterListValueHistoryEntity)
        .where(
          qMasterListValueHistoryEntity.masterListValueId.masterList.categoryType
            .equalsIgnoreCase(type),
          qMasterListValueHistoryEntity.toDate.isNull(),
          qMasterListValueHistoryEntity.id.notEqualsIgnoreCase(id),
          qMasterListValueHistoryEntity.tenant.id.in(security.getPermittedTenants(getUser())),
          qMasterListValueHistoryEntity.value2.eq(description))
        .fetchCount() == 0);
    } else if (type.equals(CategorySD.CALCULATION_FORMULA.getValue())
      || type.equals(CategorySD.ILO.getValue()) || type.equals(CategorySD.VAT_RATE.getValue())
      || type.equals(CategorySD.OBJECT.getValue())
      || type.equals(CategorySD.PROCESS_PM.getValue())
      || type.equals(CategorySD.SETTLEMENT_TYPE.getValue())) {
      return (new JPAQueryFactory(em).select(qMasterListValueHistoryEntity)
        .from(qMasterListValueHistoryEntity)
        .where(
          qMasterListValueHistoryEntity.masterListValueId.masterList.categoryType
            .equalsIgnoreCase(type),
          qMasterListValueHistoryEntity.toDate.isNull(),
          qMasterListValueHistoryEntity.id.notEqualsIgnoreCase(id),
          qMasterListValueHistoryEntity.tenant.id.in(security.getPermittedTenants(getUser())),
          qMasterListValueHistoryEntity.value1.eq(description))
        .fetchCount() == 0);
    }
    return false;
  }

  /**
   * Checks if formula is valid.
   *
   * @param formula the formula
   * @return true, if formula is valid
   */
  @Override
  public boolean isFormulaValid(String formula) {

    LOGGER.log(Level.CONFIG,
      "Executing method isFormulaValid, Parameters: formula: {0}",
      formula);

    // Dummy ai and ao values in order to validate the formula.
    Argument ai = new Argument("ai", 0);
    Argument ao = new Argument("ao", 0);
    Expression expression = new Expression(formula, ai, ao);
    return expression.checkSyntax();
  }

  /**
   * Save the master list value history entry.
   *
   * @param sdHistoryDTO the master list value history DTO
   * @param type the master list type
   */
  @Override
  public void saveSDEntry(MasterListValueHistoryDTO sdHistoryDTO, String type) {

    LOGGER.log(Level.CONFIG,
      "Executing method saveSDEntry, Parameters: sdHistoryDTO: {0}, "
        + "type: {1}",
      new Object[]{sdHistoryDTO, type});

    MasterListValueHistoryEntity sdHistoryEntity;
    // If the type isn't SETTINGS, the history functionality needs to be implemented.
    if (!type.equals(CategorySD.SETTINGS.getValue())) {
      // Check if an old entry is updated or a new entry is created.
      if (StringUtils.isBlank(sdHistoryDTO.getId())) {
        // Creating a new entry.
        sdHistoryEntity =
          MasterListValueHistoryMapper.INSTANCE.toMasterListValueHistory(sdHistoryDTO);
        // Set current date to fromDate property.
        sdHistoryEntity.setFromDate(new Timestamp(new Date().getTime()));
        // Set tenant.
        sdHistoryEntity.setTenant(TenantMapper.INSTANCE
          .toTenant(usersService.getUserById(getUser().getId()).getTenant()));
        // Set by default as internal.
        sdHistoryEntity.setInternalVersion(1);
        // In case of creating a new settlement type value, assign short code automatically.
        if (type.equals(CategorySD.SETTLEMENT_TYPE.getValue())) {
          // Get number of current settlement types.
          long currentSettlementTypes = new JPAQueryFactory(em)
            .selectFrom(qMasterListValueHistoryEntity)
            .where(qMasterListValueHistoryEntity.toDate.isNull(),
              qMasterListValueHistoryEntity.tenant.id.eq(sdHistoryEntity.getTenant().getId()),
              qMasterListValueHistoryEntity.masterListValueId.masterList.categoryType
                .eq(CategorySD.SETTLEMENT_TYPE.getValue()))
            .fetchCount();
          sdHistoryEntity.setShortCode("ST" + (currentSettlementTypes + 1));
        }
        // Get the master list entity by type.
        MasterListEntity masterListEntity = new JPAQueryFactory(em).selectFrom(qMasterListEntity)
          .where(qMasterListEntity.categoryType.eq(type)).fetchOne();
        MasterListValueEntity masterListValueEntity = new MasterListValueEntity();
        // Assign the master list entity to the master list value entity.
        masterListValueEntity.setMasterList(masterListEntity);
        em.persist(masterListValueEntity);
        // Assign the master list value entity to the entry.
        sdHistoryEntity.setMasterListValueId(masterListValueEntity);

        auditLog(masterListValueEntity.getId(), AuditEvent.CREATE.name(), null);
      } else {
        // In case of updating an old entry, find the entry and set the current date to the toDate
        // property.
        sdHistoryEntity = em.find(MasterListValueHistoryEntity.class, sdHistoryDTO.getId());
        sdHistoryEntity.setToDate(new Timestamp(new Date().getTime()));
        em.merge(sdHistoryEntity);
        // Now that the old entry is added to the history, create its new instance.
        sdHistoryEntity =
          MasterListValueHistoryMapper.INSTANCE.toMasterListValueHistory(sdHistoryDTO);
        // The entry is going to have a new id.
        sdHistoryEntity.setId(null);
        // Set current date to fromDate property.
        sdHistoryEntity.setFromDate(new Timestamp(new Date().getTime()));
        // Set null to toDate property.
        sdHistoryEntity.setToDate(null);

        auditLog(sdHistoryEntity.getMasterListValueId().getId(), AuditEvent.UPDATE.name(), null);
      }
      // Save the new entry.
      em.persist(sdHistoryEntity);
      // updating cacheBean
      cacheBean.updateSpecificSDEntry(
        MasterListValueHistoryMapper.INSTANCE.toMasterListValueHistoryDTO(sdHistoryEntity));
      cacheBean.updateHistorySDEntry(
        MasterListValueHistoryMapper.INSTANCE.toMasterListValueHistoryDTO(sdHistoryEntity));
    } else {
      // In case of type SETTINGS, no history is required.
      sdHistoryEntity =
        MasterListValueHistoryMapper.INSTANCE.toMasterListValueHistory(sdHistoryDTO);
      // Set current date to fromDate property.
      sdHistoryEntity.setFromDate(new Timestamp(new Date().getTime()));
      // Save the entry.
      em.merge(sdHistoryEntity);
    }
  }

  /**
   * Gets the signatures by directorate id.
   *
   * @param directorateId the directorate id
   * @return the signatures by directorate id
   */
  @Override
  public List<SignatureProcessTypeDTO> getSignaturesByDirectorateId(String directorateId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getSignaturesByDirectorateId, Parameters: directorateId: {0}",
      directorateId);

    QSignatureProcessTypeEntity qSignatureProcessTypeEntity =
      QSignatureProcessTypeEntity.signatureProcessTypeEntity;
    List<SignatureProcessTypeDTO> signatureProcessTypeDTOList =
      SignatureProcessTypeDTOMapper.INSTANCE.toSignatureProcessTypeDTO(new JPAQueryFactory(em)
          .select(qSignatureProcessTypeEntity).from(qSignatureProcessTypeEntity)
          .where(qSignatureProcessTypeEntity.signature.directorate.id.eq(directorateId)).fetch(),
        new CycleAvoidingMappingContext());

    for (SignatureProcessTypeDTO signatureProcessTypeDTO : signatureProcessTypeDTOList) {
      manageDepartmentsAndDirectoratesUsingCacheBean(signatureProcessTypeDTO);
      sortSignaturesBySortNumber(signatureProcessTypeDTO);
    }

    return signatureProcessTypeDTOList;
  }

  /**
   * Gets the signature copies by signature id.
   *
   * @param signatureId the signature id
   * @return the signature copies by signature id
   */
  @Override
  public List<SignatureCopyDTO> getSignatureCopiesBySignatureId(String signatureId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getSignatureCopiesBySignatureId, Parameters: signatureId: {0}",
      signatureId);

    QSignatureCopyEntity qSignatureCopyEntity = QSignatureCopyEntity.signatureCopyEntity;
    List<SignatureCopyDTO> signatureCopies = SignatureCopyDTOMapper.INSTANCE.toSignatureCopyDTO(
      new JPAQueryFactory(em).select(qSignatureCopyEntity).from(qSignatureCopyEntity)
        .where(qSignatureCopyEntity.processType.id.eq(signatureId)).fetch());

    for (SignatureCopyDTO signatureCopyDTO : signatureCopies) {
      if (signatureCopyDTO.getDepartment() != null) {
        signatureCopyDTO.setDepartmentHistory(
          cacheBean.getActiveDepartmentHistorySD().get(signatureCopyDTO.getDepartment().getId()));
      }
    }
    return signatureCopies;
  }

  /**
   * Retrieve signature by id.
   *
   * @param id the id
   * @return the signature process type DTO
   */
  @Override
  public SignatureProcessTypeDTO retrieveSignatureById(String id) {

    LOGGER.log(Level.CONFIG,
      "Executing method retrieveSignatureById, Parameters: id: {0}",
      id);

    QSignatureProcessTypeEntity qSignatureProcessTypeEntity =
      QSignatureProcessTypeEntity.signatureProcessTypeEntity;
    SignatureProcessTypeDTO signatureProcessTypeDTO = SignatureProcessTypeDTOMapper.INSTANCE
      .toSignatureProcessTypeDTO(new JPAQueryFactory(em).select(qSignatureProcessTypeEntity)
        .from(qSignatureProcessTypeEntity).where(qSignatureProcessTypeEntity.id.eq(id))
        .fetchOne(), new CycleAvoidingMappingContext());

    manageDepartmentsAndDirectoratesUsingCacheBean(signatureProcessTypeDTO);
    sortSignaturesBySortNumber(signatureProcessTypeDTO);
    return signatureProcessTypeDTO;
  }

  /**
   * Manage departments and directorates using cache bean.
   *
   * @param signatureProcessTypeDTO the signature process type DTO
   */
  private void manageDepartmentsAndDirectoratesUsingCacheBean(
    SignatureProcessTypeDTO signatureProcessTypeDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method manageDepartmentsAndDirectoratesUsingCacheBean, "
        + "Parameters: signatureProcessTypeDTO: {0}",
      signatureProcessTypeDTO);

    DepartmentDTO dep = signatureProcessTypeDTO.getSignature().getDepartment();
    DirectorateDTO dir = signatureProcessTypeDTO.getSignature().getDirectorate();

    if (dep != null) {
      signatureProcessTypeDTO.getSignature()
        .setDepartmentHistory(cacheBean.getActiveDepartmentHistorySD().get(dep.getId()));
    }
    if (dir != null) {
      signatureProcessTypeDTO.getSignature()
        .setDirectorateHistory(cacheBean.getActiveDirectorateHistorySD().get(dir.getId()));
    }

    if (signatureProcessTypeDTO.getSignatureCopies() != null) {
      for (SignatureCopyDTO signatureCopyDTO : signatureProcessTypeDTO.getSignatureCopies()) {
        if (signatureCopyDTO.getDepartment() != null) {
          signatureCopyDTO.setDepartmentHistory(cacheBean.getActiveDepartmentHistorySD()
            .get(signatureCopyDTO.getDepartment().getId()));
        }
      }
    }

    if (signatureProcessTypeDTO.getSignatureProcessTypeEntitled() != null) {
      for (SignatureProcessTypeEntitledDTO signatureProcessTypeEntitledDTO : signatureProcessTypeDTO
        .getSignatureProcessTypeEntitled()) {
        if (signatureProcessTypeEntitledDTO.getDepartment() != null) {
          signatureProcessTypeEntitledDTO
            .setDepartmentHistory(cacheBean.getActiveDepartmentHistorySD()
              .get(signatureProcessTypeEntitledDTO.getDepartment().getId()));
        }
      }
    }
  }

  /**
   * Update signature process entitled.
   *
   * @param signatureProcessTypeDTO the signature process type DTO
   */
  @Override
  public void updateSignatureProcessEntitled(SignatureProcessTypeDTO signatureProcessTypeDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateSignatureProcessEntitled, Parameters: "
        + "signatureProcessTypeDTO: {0}",
      signatureProcessTypeDTO);

    QSignatureProcessTypeEntity qSignatureProcessTypeEntity =
      QSignatureProcessTypeEntity.signatureProcessTypeEntity;
    QSignatureProcessTypeEntitledEntity qsignatureProcessTypeEntitledEntity =
      QSignatureProcessTypeEntitledEntity.signatureProcessTypeEntitledEntity;

    SignatureProcessTypeEntity sPtE = new JPAQueryFactory(em).select(qSignatureProcessTypeEntity)
      .from(qSignatureProcessTypeEntity)
      .where(qSignatureProcessTypeEntity.id.eq(signatureProcessTypeDTO.getId())).fetchOne();

    List<SignatureProcessTypeEntitledEntity> sPTEentities = new JPAQueryFactory(em)
      .select(qsignatureProcessTypeEntitledEntity).from(qsignatureProcessTypeEntitledEntity)
      .where(
        qsignatureProcessTypeEntitledEntity.processType.id.eq(signatureProcessTypeDTO.getId()))
      .fetch();

    deleteOldAndInsertNewSignatureProcessTypeEntitled(sPtE, sPTEentities, signatureProcessTypeDTO);

    if (signatureProcessTypeDTO.getProcess().getValue().equals(Process.SELECTIVE.getValue())) {
      SignatureProcessTypeEntity sPtEOff =
        new JPAQueryFactory(em).select(qSignatureProcessTypeEntity)
          .from(qSignatureProcessTypeEntity)
          .where(qSignatureProcessTypeEntity.process.eq(Process.OPEN)
            .and(qSignatureProcessTypeEntity.signature.department.id
              .eq(signatureProcessTypeDTO.getSignature().getDepartment().getId())))
          .fetchOne();

      List<SignatureProcessTypeEntitledEntity> sPTEentitiesOpen = new JPAQueryFactory(em)
        .select(qsignatureProcessTypeEntitledEntity).from(qsignatureProcessTypeEntitledEntity)
        .where(qsignatureProcessTypeEntitledEntity.processType.eq(sPtEOff)).fetch();

      deleteOldAndInsertNewSignatureProcessTypeEntitled(sPtEOff, sPTEentitiesOpen,
        signatureProcessTypeDTO);

    } else if (signatureProcessTypeDTO.getProcess()
      .equals(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION)) {
      SignatureProcessTypeEntity sPtENpro =
        new JPAQueryFactory(em).select(qSignatureProcessTypeEntity)
          .from(qSignatureProcessTypeEntity)
          .where(qSignatureProcessTypeEntity.process.eq(Process.NEGOTIATED_PROCEDURE)
            .and(qSignatureProcessTypeEntity.signature.department.id
              .eq(signatureProcessTypeDTO.getSignature().getDepartment().getId())))
          .fetchOne();

      List<SignatureProcessTypeEntitledEntity> sPTEentitiesNpro = new JPAQueryFactory(em)
        .select(qsignatureProcessTypeEntitledEntity).from(qsignatureProcessTypeEntitledEntity)
        .where(qsignatureProcessTypeEntitledEntity.processType.eq(sPtENpro)).fetch();

      deleteOldAndInsertNewSignatureProcessTypeEntitled(sPtENpro, sPTEentitiesNpro,
        signatureProcessTypeDTO);
    }
  }

  /**
   * Delete old and insert new signature process type entitled.
   *
   * @param sPtE the s pt E
   * @param sPTEentities the s PT eentities
   * @param signatureProcessTypeDTO the signature process type DTO
   */
  private void deleteOldAndInsertNewSignatureProcessTypeEntitled(SignatureProcessTypeEntity sPtE,
    List<SignatureProcessTypeEntitledEntity> sPTEentities,
    SignatureProcessTypeDTO signatureProcessTypeDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method deleteOldAndInsertNewSignatureProcessTypeEntitled, "
        + "Parameters: sPtE: {0}, sPTEentities: {1}, signatureProcessTypeDTO: {2}",
      new Object[]{sPtE, sPTEentities, signatureProcessTypeDTO});

    // remove the old SignatureProcessTypeEntitled
    for (SignatureProcessTypeEntitledEntity sPTEe : sPTEentities) {
      em.remove(sPTEe);
    }

    // insert the new SignatureProcessTypeEntitled sent from the dto
    if (signatureProcessTypeDTO.getSignatureProcessTypeEntitled() != null) {
      for (SignatureProcessTypeEntitledDTO sPTEeDTO : signatureProcessTypeDTO
        .getSignatureProcessTypeEntitled()) {
        SignatureProcessTypeEntitledEntity sPTEe = new SignatureProcessTypeEntitledEntity();
        sPTEe.setName(sPTEeDTO.getName());
        sPTEe.setDepartment(DepartmentMapper.INSTANCE.toDepartment(sPTEeDTO.getDepartment()));
        sPTEe.setProcessType(sPtE);
        sPTEe.setFunction(sPTEeDTO.getFunction());
        sPTEe.setSortNumber(sPTEeDTO.getSortNumber());
        em.persist(sPTEe);
      }
    }
  }

  /**
   * Update signature copies.
   *
   * @param signatureProcessTypeDTO the signature process type DTO
   */
  @Override
  public void updateSignatureCopies(SignatureProcessTypeDTO signatureProcessTypeDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateSignatureCopies, Parameters: "
        + "signatureProcessTypeDTO: {0}",
      signatureProcessTypeDTO);

    QSignatureCopyEntity qSignatureCopyEntity = QSignatureCopyEntity.signatureCopyEntity;

    QSignatureProcessTypeEntity qSignatureProcessTypeEntity =
      QSignatureProcessTypeEntity.signatureProcessTypeEntity;

    SignatureProcessTypeEntity sPtE = new JPAQueryFactory(em).select(qSignatureProcessTypeEntity)
      .from(qSignatureProcessTypeEntity)
      .where(qSignatureProcessTypeEntity.id.eq(signatureProcessTypeDTO.getId())).fetchOne();

    List<SignatureCopyEntity> copyEntities =
      new JPAQueryFactory(em).select(qSignatureCopyEntity).from(qSignatureCopyEntity)
        .where(qSignatureCopyEntity.processType.id.eq(signatureProcessTypeDTO.getId())).fetch();

    deleteOldAndInsertNewSignatureCopies(sPtE, copyEntities, signatureProcessTypeDTO);

    // update signature Copies in offen process if selective
    if (signatureProcessTypeDTO.getProcess().getValue().equals(Process.SELECTIVE.getValue())) {
      SignatureProcessTypeEntity sPtOffen =
        new JPAQueryFactory(em).select(qSignatureProcessTypeEntity)
          .from(qSignatureProcessTypeEntity)
          .where(qSignatureProcessTypeEntity.process.eq(Process.OPEN)
            .and(qSignatureProcessTypeEntity.signature.department.id
              .eq(signatureProcessTypeDTO.getSignature().getDepartment().getId())))
          .fetchOne();

      List<SignatureCopyEntity> copyEntitiesOffen =
        new JPAQueryFactory(em).select(qSignatureCopyEntity).from(qSignatureCopyEntity)
          .where(qSignatureCopyEntity.processType.eq(sPtOffen)).fetch();

      deleteOldAndInsertNewSignatureCopies(sPtOffen, copyEntitiesOffen, signatureProcessTypeDTO);

      // update signature Copies in NEGOTIATED_PROCEDURE process if
      // NEGOTIATED_PROCEDURE_WITH_COMPETITION
    } else if (signatureProcessTypeDTO.getProcess().getValue()
      .equals(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION.getValue())) {
      SignatureProcessTypeEntity sptNP =
        new JPAQueryFactory(em).select(qSignatureProcessTypeEntity)
          .from(qSignatureProcessTypeEntity)
          .where(qSignatureProcessTypeEntity.process.eq(Process.NEGOTIATED_PROCEDURE)
            .and(qSignatureProcessTypeEntity.signature.department.id
              .eq(signatureProcessTypeDTO.getSignature().getDepartment().getId())))
          .fetchOne();

      List<SignatureCopyEntity> copyEntitiesNP =
        new JPAQueryFactory(em).select(qSignatureCopyEntity).from(qSignatureCopyEntity)
          .where(qSignatureCopyEntity.processType.eq(sptNP)).fetch();
      deleteOldAndInsertNewSignatureCopies(sptNP, copyEntitiesNP, signatureProcessTypeDTO);
    }
  }

  /**
   * Delete old and insert new signature copies.
   *
   * @param sPtE the s pt E
   * @param copyEntities the copy entities
   * @param signatureProcessTypeDTO the signature process type DTO
   */
  private void deleteOldAndInsertNewSignatureCopies(SignatureProcessTypeEntity sPtE,
    List<SignatureCopyEntity> copyEntities, SignatureProcessTypeDTO signatureProcessTypeDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method deleteOldAndInsertNewSignatureCopies, Parameters: sPtE: {0}, "
        + "copyEntities: {1}, signatureProcessTypeDTO: {2}",
      new Object[]{sPtE, copyEntities, signatureProcessTypeDTO});

    // remove the old SignatureCopies
    for (SignatureCopyEntity cE : copyEntities) {
      em.remove(cE);
    }

    // insert the new SignatureCopies sent from the dto
    if (signatureProcessTypeDTO.getSignatureCopies() != null) {
      for (SignatureCopyDTO copyDTO : signatureProcessTypeDTO.getSignatureCopies()) {
        SignatureCopyEntity sCe = new SignatureCopyEntity();
        sCe.setDepartment(DepartmentMapper.INSTANCE.toDepartment(copyDTO.getDepartment()));
        sCe.setProcessType(sPtE);
        em.persist(sCe);
      }
    }
  }


  /**
   * Uploads image.
   *
   * @param uploadedImageId the uploaded image id
   * @return the image in the form of byte array.
   */
  @Override
  public byte[] uploadImage(String uploadedImageId) {

    LOGGER.log(Level.CONFIG,
      "Executing method uploadImage, Parameters: uploadedImageId: {0}",
      uploadedImageId);

    // Retrieve image file from database
    FileGetResponse fileGetResponse = fileUpload.getByID(uploadedImageId);
    byte[] image = fileGetResponse.getFile().getFileData();
    // Check if the file extension is png. If not just return an empty byte array.
    try {
      String extension = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(image));
      if (extension != null && extension.equals("image/png")) {
        return image;
      }
    } catch (IOException e) {
      return new byte[0];
    }
    return new byte[0];
  }

  /**
   * Sort signatures by sort number.
   *
   * @param signatureProcessTypeDTO the signature process type DTO
   * @return the signature process type DTO
   */
  /*
   * this method is used to sort the SignatureProcessTypeEntitled List in order to be shown in the
   * correct order in the UI,depending on the order the user gives when editing.If no editing has
   * been done before,sorting is not done
   */
  private SignatureProcessTypeDTO sortSignaturesBySortNumber(
    SignatureProcessTypeDTO signatureProcessTypeDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method sortSignaturesBySortNumber, Parameters: "
        + "signatureProcessTypeDTO: {0}",
      signatureProcessTypeDTO);

    if (signatureProcessTypeDTO.getSignatureProcessTypeEntitled() != null) {
      for (SignatureProcessTypeEntitledDTO s : signatureProcessTypeDTO
        .getSignatureProcessTypeEntitled()) {
        if (s.getSortNumber() == null) {
          return signatureProcessTypeDTO;
        }
      }
      Collections.sort(signatureProcessTypeDTO.getSignatureProcessTypeEntitled(),
        ComparatorUtil.SortNumberComparator);
    }
    return signatureProcessTypeDTO;
  }

  /**
   * Gets the report maximum results value.
   *
   * @return the report maximum results value
   */
  /* this function gets the maximum results for the of the reports when generate */
  @Override
  public Long getReportMaximumResultsValue() {

    LOGGER.log(Level.CONFIG, "Executing method getReportMaximumResultsValue");

    JPAQuery<ProjectEntity> query = new JPAQuery<>(em);
    BooleanBuilder whereClause = new BooleanBuilder();

    whereClause.and(qMasterListValueHistoryEntity.tenant.id
      .eq(usersService.getUserById(getUser().getId()).getTenant().getId()));
    whereClause.and(qMasterListValueHistoryEntity.masterListValueId.masterList.categoryType
      .equalsIgnoreCase(CategorySD.SETTINGS.getValue()));
    whereClause.and(qMasterListValueHistoryEntity.shortCode.eq(ShortCode.S6.name()));

    return Long
      .parseLong(
        MasterListValueHistoryMapper.INSTANCE
          .toMasterListValueHistoryDTO(
            query.select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
              .where(whereClause, qMasterListValueHistoryEntity.active.isTrue(),
                qMasterListValueHistoryEntity.toDate.isNull())
              .orderBy(qMasterListValueHistoryEntity.value1.asc()).fetchOne())
          .getValue2());

  }

  /**
   * Gets the user settings for the contact information.
   *
   * @param tenantId the tenant id
   * @param groupName the group name
   * @return the user settings
   */
  @Override
  public SettingsDTO getUserSettings(String tenantId, String groupName) {

    LOGGER.log(Level.CONFIG,
      "Executing method getUserSettings, Parameters: tenantId: {0}, "
        + "groupName: {1}",
      new Object[]{tenantId, groupName});

    List<MasterListValueHistoryDTO> settings = null;
    TenantDTO mainTenant = tenantService.getMainTenant();

    if (getUser() != null && getUser().getAttribute(USER_ATTRIBUTES.TENANT.getValue()) != null
      && getUser().getAttribute(USER_ATTRIBUTES.TENANT.getValue()).getData() != null) {
      TenantDTO tenant = tenantService
        .getTenantById(getUser().getAttribute(USER_ATTRIBUTES.TENANT.getValue()).getData());
      if (!tenant.getIsMain() && getGroupName(getUser()).equals(Group.ADMIN.getValue())) {
        // Retrieve main tenant settings.
        settings = masterListValueHistoryQuery(CategorySD.SETTINGS, mainTenant.getId());
      } else {
        // Retrieve user tenant settings.
        settings = masterListValueHistoryQuery(CategorySD.SETTINGS, tenant.getId());
      }
    } else {
      if (tenantId == null || (groupName != null && groupName.equals(Group.ADMIN.getValue()))) {
        // If no tenant id has been given or if the group is administrator, get the main tenant
        // information.
        settings = masterListValueHistoryQuery(CategorySD.SETTINGS, mainTenant.getId());
      } else {
        // In any other case, get the given tenant information.
        settings = masterListValueHistoryQuery(CategorySD.SETTINGS, tenantId);
      }
    }

    SettingsDTO dto = new SettingsDTO();

    for (MasterListValueHistoryDTO s : settings) {
      if (s.getShortCode().equals(ShortCode.S3.name())) {
        dto.setContactEmail(s.getValue2());
      }
      if (s.getShortCode().equals(ShortCode.S1.name())) {
        dto.setContactLocation(s.getValue2());
      }
      if (s.getShortCode().equals(ShortCode.S10.name())) {
        dto.setContactTelephone(s.getValue2());
      }
      if (s.getShortCode().equals(ShortCode.S2.name())) {
        dto.setSupport(s.getValue2());
      }
      if (s.getShortCode().equals(ShortCode.S4.name())) {
        dto.setTechEmail(s.getValue2());
      }
      if (s.getShortCode().equals(ShortCode.S11.name())) {
        dto.setTechTelephone(s.getValue2());
      }
      if (s.getShortCode().equals(ShortCode.S7.name())) {
        dto.setIpPrinterAddress(s.getValue2());
      }
      if (s.getShortCode().equals(ShortCode.S12.name())) {
        dto.setIpPrinterPort(s.getValue2());
      }
    }
    return dto;
  }

  /**
   * Gets the printer settings for main tenant.
   *
   * @return the printer settings
   */
  @Override
  public SettingsDTO getPrinterSettings() {

    LOGGER.log(Level.CONFIG,
      "Executing method getPrinterSettings");

    SettingsDTO printerSettings = new SettingsDTO();
    TenantDTO mainTenant = tenantService.getMainTenant();
    List<MasterListValueHistoryDTO> settings = masterListValueHistoryQuery(CategorySD.SETTINGS, mainTenant.getId());

    for (MasterListValueHistoryDTO s : settings) {
      if (s.getShortCode().equals(ShortCode.S7.name())) {
        printerSettings.setIpPrinterAddress(s.getValue2());
      }
      if (s.getShortCode().equals(ShortCode.S12.name())) {
        printerSettings.setIpPrinterPort(s.getValue2());
      }
    }
    return printerSettings;
  }

  /**
   * Gets Master List Value History
   *
   * @param type the type
   * @param tenantId the tenantId
   * @return the Master List Value History
   */
  @Override
  public List<MasterListValueHistoryDTO> historySdQuery(CategorySD type, String tenantId) {

    LOGGER.log(Level.CONFIG,
      "Executing method historySdQuery, Parameters: type: {0}, "
        + "tenantId: {1}",
      new Object[]{type, tenantId});

    JPAQuery<ProjectEntity> query = new JPAQuery<>(em);
    BooleanBuilder whereClause = new BooleanBuilder();

    if (!StringUtils.isBlank(tenantId)) {
      whereClause.and(qMasterListValueHistoryEntity.tenant.id.eq(tenantId));
    }

    if (type != null) {
      whereClause.and(qMasterListValueHistoryEntity.masterListValueId.masterList.categoryType
        .equalsIgnoreCase(type.getValue()));
    }

    return MasterListValueHistoryMapper.INSTANCE.toMasterListValueHistoryDTO(
      query.select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
        .where(whereClause).orderBy(qMasterListValueHistoryEntity.value1.asc()).fetch());
  }

  /**
   * Gets the Master List Value History by submission.
   *
   * @param submissionId the submissionId
   * @param category the category
   * @return the Master List Value History by submission
   */
  /*This function is used for the historization part*/
  @Override
  public List<MasterListValueHistoryDTO> getMasterListValueHistoryDataBySubmission(
    String submissionId,
    String category) {

    Timestamp submissionReferenceDate = null;
    if (category.equals(CategorySD.WORKTYPE.getValue()) || category
      .equals(CategorySD.SETTLEMENT_TYPE.getValue())) {
      submissionReferenceDate = submissionService.getDateOfCreationStatusOfSubmission(submissionId);

    } else {
      submissionReferenceDate = submissionService.getDateOfCompletedOrCanceledStatus(submissionId);

    }
    String tenantId = usersService.getUserById(getUser().getId()).getTenant().getId();
    return cacheBean
      .getMasterListValueHistoryDataBySubmissionCreationDate(category, submissionReferenceDate,
        tenantId);

  }

  /**
   * Gets the tenant logo.
   *
   * @return the tenant logo
   */
  @Override
  public byte[] getTenantLogo() {
    String tenantId = tenantService.getTenant().getId();
    return new JPAQueryFactory(em).select(qMasterListValueHistoryEntity.tenantLogo)
      .from(qMasterListValueHistoryEntity)
      .where(qMasterListValueHistoryEntity.tenant.id.eq(tenantId)
        .and(qMasterListValueHistoryEntity.masterListValueId.masterList.categoryType
          .eq(CategorySD.SETTINGS.getValue()))
        .and(qMasterListValueHistoryEntity.shortCode.eq("S5")))
      .fetchOne();
  }

  /**
   * Gets the document template id by its short code.
   *
   * @param templateShortCode the template short code
   * @param tenantId the tenant id
   * @return the template id
   */
  @Override
  public String getTemplateIdByShortCode(String templateShortCode, String tenantId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getTemplateIdByShortCode, Parameters: templateShortCode: {0}, "
        + "tenantId: {1}",
      new Object[]{templateShortCode, tenantId});

    return new JPAQueryFactory(em).select(qMasterListValueHistoryEntity)
      .from(qMasterListValueHistoryEntity)
      .where(qMasterListValueHistoryEntity.shortCode.eq(templateShortCode)
        .and(qMasterListValueHistoryEntity.tenant.id.eq(tenantId))
        .and(qMasterListValueHistoryEntity.toDate.isNull()))
      .fetchOne().getMasterListValueId().getId();
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

  /**
   * Gets the name of the master list type.
   *
   * @param type the master list type
   * @return the name
   */
  @Override
  public String getNameOfMasterListType(String type) {
    return new JPAQueryFactory(em).select(qMasterListEntity.name).from(qMasterListEntity)
      .where(qMasterListEntity.categoryType.eq(type)).fetchOne();
  }

  /**
   * Gets the Master List Value History by code.
   *
   * @param code the code
   * @return the Master List Value History by code
   */
  @Override
  public MasterListValueHistoryDTO getMasterListHistoryByCode(String code) {

    LOGGER.log(Level.CONFIG, "Executing method getMasterListHistoryByCode, Parameters: code: {0}",
        code);

    return MasterListValueHistoryMapper.INSTANCE.toMasterListValueHistoryDTO(new JPAQueryFactory(em)
        .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
        .where(
            qMasterListValueHistoryEntity.tenant.id
                .eq(getUser().getAttribute(USER_ATTRIBUTES.TENANT.getValue()).getData()),
            (qMasterListValueHistoryEntity.shortCode.eq(code)))
        .orderBy(qMasterListValueHistoryEntity.value1.asc()).fetchOne());
  }

  /**
   * Gets the current Master List Value History Entries by type for the given submission.
   *
   * @param submissionId the submission id
   * @param typeName the master list type name
   * @return the current Master List Value History Entries by type
   */
  @Override
  public List<MasterListValueHistoryDTO> getCurrentMLVHEntriesForSubmission(String submissionId,
      String typeName) {

    LOGGER.log(Level.CONFIG,
        "Executing method getCurrentMLVHEntriesForSubmission, Parameters: submissionId: {0}, "
            + "typeName: {1}",
        new Object[] {submissionId, typeName});

    // Get the submission cancellation or completion date.
    Timestamp cancellationOrCompletionDate =
        submissionService.getDateOfCompletedOrCanceledStatus(submissionId);
    if (cancellationOrCompletionDate == null) {
      // If the submission has not been completed or cancelled, just return the latest history
      // entries of the given type.
      return getMasterListHistoryByType(typeName);
    }

    // Get the master list value ids for the given type.
    List<String> masterListValueIds = new JPAQueryFactory(em)
        .select(qMasterListValueHistoryEntity.masterListValueId.id)
        .from(qMasterListValueHistoryEntity)
        .where(qMasterListValueHistoryEntity.toDate.isNull(),
            qMasterListValueHistoryEntity.masterListValueId.masterList.name.eq(typeName),
            qMasterListValueHistoryEntity.tenant.id.eq(usersService.getTenant().getId()))
        .fetch();
    // Get the submission creation date.
    Timestamp creationDate = submissionService.getDateOfCreationStatusOfSubmission(submissionId);
    List<MasterListValueHistoryDTO> entries = new ArrayList<>();
    // Get the type from the type name.
    String type = new JPAQueryFactory(em).select(qMasterListEntity.categoryType)
        .from(qMasterListEntity).where(qMasterListEntity.name.eq(typeName)).fetchOne();
    for (String masterListValueId : masterListValueIds) {
      // For every master list value id, get the current master list value history entry for the
      // completed/cancelled submission.
      entries.add(cacheBean.getValue(type, masterListValueId, cancellationOrCompletionDate,
          creationDate, null));
    }
    Collections.sort(entries, ComparatorUtil.sortMLVHistoryDTOsByValue1);
    return entries;
  }
}