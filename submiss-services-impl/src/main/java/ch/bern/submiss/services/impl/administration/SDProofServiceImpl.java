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

import ch.bern.submiss.services.api.administration.CompanyProofService;
import ch.bern.submiss.services.api.administration.SDCountryService;
import ch.bern.submiss.services.api.administration.SDProofService;
import ch.bern.submiss.services.api.administration.UserAdministrationService;
import ch.bern.submiss.services.api.constants.AuditEvent;
import ch.bern.submiss.services.api.constants.AuditGroupName;
import ch.bern.submiss.services.api.constants.AuditLevel;
import ch.bern.submiss.services.api.constants.Process;
import ch.bern.submiss.services.api.constants.SelectiveLevel;
import ch.bern.submiss.services.api.constants.TenderStatus;
import ch.bern.submiss.services.api.dto.CompanyDTO;
import ch.bern.submiss.services.api.dto.MasterListTypeDataDTO;
import ch.bern.submiss.services.api.dto.ProofHistoryDTO;
import ch.bern.submiss.services.api.dto.ProofProvidedMapDTO;
import ch.bern.submiss.services.api.dto.SubmissionDTO;
import ch.bern.submiss.services.api.dto.SubmittentDTO;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.api.util.ValidationMessages;
import ch.bern.submiss.services.impl.mappers.ProofHistoryMapper;
import ch.bern.submiss.services.impl.mappers.ProofToTypeDataMapper;
import ch.bern.submiss.services.impl.mappers.TenantMapper;
import ch.bern.submiss.services.impl.model.ProofEntity;
import ch.bern.submiss.services.impl.model.ProofHistoryEntity;
import ch.bern.submiss.services.impl.model.QProofHistoryEntity;
import ch.bern.submiss.services.impl.model.QSubmittentProofProvidedEntity;
import ch.bern.submiss.services.impl.model.SubmittentProofProvidedEntity;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.OptimisticLockException;
import javax.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

/**
 * The Class SDProofServiceImpl.
 */
@Transactional
@OsgiServiceProvider(classes = {SDProofService.class})
@Singleton
public class SDProofServiceImpl extends BaseService implements SDProofService {

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER = Logger.getLogger(SDProofServiceImpl.class.getName());

  /**
   * The Constant TENANTID_MSG.
   */
  private static final String TENANTID_MSG = "Executing method getAllCurrentProofEntries. Parameters: tenantId {0}";
  /**
   * The q proof history entity.
   */
  QProofHistoryEntity qProofHistoryEntity = QProofHistoryEntity.proofHistoryEntity;

  /**
   * The q submittent proof provided.
   */
  QSubmittentProofProvidedEntity qSubmittentProofProvided =
    QSubmittentProofProvidedEntity.submittentProofProvidedEntity;

  /**
   * The cache bean.
   */
  @Inject
  private CacheBean cacheBean;
  /**
   * The users service.
   */
  @Inject
  private UserAdministrationService usersService;
  @Inject
  private CompanyProofService companyProofService;

  @Inject
  private SDCountryService sDCountryService;

  @Inject
  private AuditBean audit;

  @Override
  public List<MasterListTypeDataDTO> proofToTypeData() {

    LOGGER.log(Level.CONFIG, "Executing method proofToTypeData");

    List<MasterListTypeDataDTO> typeDTOs;
    // Retrieving proof history data.
    List<ProofHistoryEntity> proofHistoryEntities =
      new JPAQueryFactory(em).selectFrom(qProofHistoryEntity)
        .where(qProofHistoryEntity.toDate.isNull()
          .and(qProofHistoryEntity.tenant.id.in(security.getPermittedTenants(getUser()))))
        .fetch();
    // Mapping proof data to master list type data.
    typeDTOs = ProofToTypeDataMapper.INSTANCE.toMasterListTypeDataDTOs(proofHistoryEntities,
        sDCountryService.mapCountryIdsToCountryHistoryDTOs());
    return typeDTOs;
  }

  @Override
  public ProofHistoryDTO getProofEntryById(String entryId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getProofEntryById. Parameters: entryId: {0}",
      entryId);

    return ProofHistoryMapper.INSTANCE.toProofHistoryDTO(new JPAQueryFactory(em)
      .selectFrom(qProofHistoryEntity).where(qProofHistoryEntity.id.eq(entryId)).fetchOne());
  }

  @Override
  public boolean isProofNameAndCountryUnique(String proofId, String proofName, String countryId) {

    LOGGER.log(Level.CONFIG,
      "Executing method isProofNameAndCountryUnique. Parameters: proofId: {0}, "
        + "proofName: {1}, countryId: {2}",
      new Object[]{proofId, proofName, countryId});

    if (proofId == null) {
      // If the proofId is null (case of creating a new entry), replace the null value with an empty
      // String in order to avoid the null pointer exception.
      proofId = StringUtils.EMPTY;
    }
    return (new JPAQueryFactory(em).select(qProofHistoryEntity).from(qProofHistoryEntity)
      .where(qProofHistoryEntity.toDate.isNull(),
        qProofHistoryEntity.id.notEqualsIgnoreCase(proofId),
        qProofHistoryEntity.tenant.id.in(security.getPermittedTenants(getUser())),
        qProofHistoryEntity.country.id.eq(countryId),
        qProofHistoryEntity.name.eq(proofName))
      .fetchCount() == 0);
  }

  @Override
  public List<ProofHistoryDTO> getAllCurrentProofEntries(String tenantId) {

    LOGGER.log(Level.CONFIG, TENANTID_MSG, tenantId);

    JPAQuery<ProofHistoryEntity> query = new JPAQuery<>(em);
    BooleanBuilder whereClause = new BooleanBuilder();

    if (!StringUtils.isBlank(tenantId)) {
      whereClause.and(qProofHistoryEntity.tenant.id.eq(tenantId));
    }

    return ProofHistoryMapper.INSTANCE.toProofHistoryDTO(query.select(qProofHistoryEntity)
      .from(qProofHistoryEntity)
      .where(whereClause, qProofHistoryEntity.active.eq(1), qProofHistoryEntity.toDate.isNull())
      .fetch());
  }

  @Override
  public Set<ValidationError> saveProofsEntry(ProofHistoryDTO proofHistoryDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method saveProofsEntry. Parameters: proofHistoryDTO {0}",
      proofHistoryDTO);

    Set<ValidationError> error =  new HashSet<>();
    ProofHistoryEntity proofHistEntity;
    boolean newEntryCreated = false;
    boolean countryChanged = false;
    boolean requiredChanged = false;
    boolean activeChanged = false;
    // Check if an old entry is being updated or a new entry is created.
    if (StringUtils.isBlank(proofHistoryDTO.getId())) {
      newEntryCreated = true;
      // Creating a new entry.
      proofHistEntity = ProofHistoryMapper.INSTANCE.toProofHistory(proofHistoryDTO);
      // Set current date to fromDate property.
      proofHistEntity.setFromDate(new Timestamp(new Date().getTime()));
      // Set tenant.
      proofHistEntity.setTenant(
        TenantMapper.INSTANCE.toTenant(usersService.getUserById(getUser().getId()).getTenant()));
      // Creating a new proof entity.
      ProofEntity proofEntity = new ProofEntity();
      em.persist(proofEntity);
      // Assign the proof entity to the proof history entity.
      proofHistEntity.setProofId(proofEntity);
      // Calculate the proof order value.
      if (proofHistEntity.getProofOrder() == null) {
        proofHistEntity
          .setProofOrder(calculateProofOrder(null, proofHistEntity.getCountry().getId()));
      } else {
        proofHistEntity.setProofOrder(calculateProofOrder(proofHistEntity.getProofOrder(),
          proofHistEntity.getCountry().getId()));
      }
      // Remove default proofs of the companies if the added proof is the first proof of its country.
      if (isFirstCountryProof(proofHistoryDTO.getCountry().getId())) {
        companyProofService.removeDefaultProofs(proofHistoryDTO);
      }
      auditLog(proofEntity.getId(), AuditEvent.CREATE.name(), null);
    } else {
      // In case of updating an old entry, find the entry and set the current date to the toDate
      // property.
      proofHistEntity = em.find(ProofHistoryEntity.class, proofHistoryDTO.getId());
      if (proofHistEntity.getVersion() != proofHistoryDTO.getVersion()) {
        error
          .add(new ValidationError("optimisticLockErrorField", ValidationMessages.OPTIMISTIC_LOCK));
        return error;
      }
      proofHistEntity.setToDate(new Timestamp(new Date().getTime()));
      int oldProofOrder = proofHistEntity.getProofOrder();
      // Check if the country property has been changed.
      if (!proofHistEntity.getCountry().getId().equals(proofHistoryDTO.getCountry().getId())) {
        countryChanged = true;
      }
      // Check if the required property has been changed.
      if (!proofHistEntity.getRequired().equals(proofHistoryDTO.getRequired())) {
        requiredChanged = true;
      }
      if (!proofHistEntity.getActive().equals(proofHistoryDTO.getActive())) {
        activeChanged = true;
      }
      em.merge(proofHistEntity);
      // Now that the old entry is added to the history, create its new instance.
      proofHistEntity = ProofHistoryMapper.INSTANCE.toProofHistory(proofHistoryDTO);
      // Check if a new proof order value is required.
      if (proofHistEntity.getProofOrder() == null
        || proofHistEntity.getProofOrder() != oldProofOrder) {
        // Calculate the new proof order value.
        if (proofHistEntity.getProofOrder() == null) {
          proofHistEntity
            .setProofOrder(calculateProofOrder(null, proofHistEntity.getCountry().getId()));
        } else {
          proofHistEntity.setProofOrder(calculateProofOrder(proofHistEntity.getProofOrder(),
            proofHistEntity.getCountry().getId()));
        }
      }
      // The entry is going to have a new id.
      proofHistEntity.setId(null);
      // Set current date to fromDate property.
      proofHistEntity.setFromDate(new Timestamp(new Date().getTime()));
      // Set null to toDate property.
      proofHistEntity.setToDate(null);

      auditLog(proofHistEntity.getProofId().getId(), AuditEvent.UPDATE.name(), null);
    }
    // Save the new entry.
    em.persist(proofHistEntity);
    em.flush();
    proofHistoryDTO = ProofHistoryMapper.INSTANCE.toProofHistoryDTO(proofHistEntity);
    actionsAfterSave(proofHistoryDTO, newEntryCreated, countryChanged, requiredChanged,
      activeChanged);
    return error;
  }

  /**
   * Actions after saving a proof entry.
   *
   * @param proofHistoryDTO the proofHistoryDTO
   * @param newEntryCreated the newEntryCreated
   * @param countryChanged the countryChanged
   * @param requiredChanged the requiredChanged
   * @param activeChanged the activeChanged
   */
  private void actionsAfterSave(ProofHistoryDTO proofHistoryDTO, boolean newEntryCreated,
    boolean countryChanged, boolean requiredChanged, boolean activeChanged) {
    cacheBean.updateproofsEntry(proofHistoryDTO);
    cacheBean.updateProofList(proofHistoryDTO);
    cacheBean.updateMostRecenProofsEntry(proofHistoryDTO);
    // Removing the proof from the companies that shouldn't have it.
    if (countryChanged) {
      companyProofService.removeProofFromCompanies(proofHistoryDTO);
    }
    // Adding the proof to the companies that should have it.
    if (newEntryCreated || countryChanged) {
      companyProofService.addProofToCompanies(proofHistoryDTO);
    }
    if (activeChanged) {
      companyProofService.updateInactiveCompanyProofs(proofHistoryDTO);
    }
    // In case of an already existing proof whose country property has not been changed but whose
    // required property has been changed, all companies who have this proof need to have their
    // required proof property updated. If the country property has been changed however or a new
    // proof entry is created, the update is done during the company proof addition.
    if (!countryChanged && !activeChanged && requiredChanged) {
      companyProofService.updateRequiredPropertyOfCompanyProof(proofHistoryDTO);
    }
  }

  /**
   * Calculates the proof order.
   *
   * @param givenProofOrder the proof order given by the user.
   * @param countryId the country id
   * @return the proof order to be assigned.
   */
  private int calculateProofOrder(Integer givenProofOrder, String countryId) {

    LOGGER.log(Level.CONFIG,
      "Executing method calculateProofOrder. Parameters: givenProofOrder: {0}, "
        + "countryId: {1}",
      new Object[]{givenProofOrder, countryId});

    // Retrieve proof data by country and tenant.
    List<ProofHistoryEntity> proofHistoryEntities = new JPAQueryFactory(em)
      .selectFrom(qProofHistoryEntity)
      .where(qProofHistoryEntity.toDate.isNull().and(qProofHistoryEntity.country.id.eq(countryId))
        .and(qProofHistoryEntity.tenant.id
          .in(usersService.getUserById(getUser().getId()).getTenant().getId())))
      .orderBy(qProofHistoryEntity.proofOrder.asc()).fetch();
    int maxProofOrder = 0;
    if (proofHistoryEntities != null && !proofHistoryEntities.isEmpty()) {
      // Get the biggest proof order value (if proof history entities exist).
      maxProofOrder = proofHistoryEntities.get(proofHistoryEntities.size() - 1).getProofOrder();
    }
    if (givenProofOrder == null || givenProofOrder >= maxProofOrder) {
      return maxProofOrder + 1;
    } else {
      for (ProofHistoryEntity proofHistoryEntity : proofHistoryEntities) {
        // Check if the proof order value of the current entity is equal or greater than the given
        // proof order.
        if (proofHistoryEntity.getProofOrder() >= givenProofOrder) {
          // Increment the proof order value of the current entity by 1.
          proofHistoryEntity.setProofOrder(proofHistoryEntity.getProofOrder() + 1);
          em.merge(proofHistoryEntity);
        }
      }
      return givenProofOrder;
    }
  }

  @Override
  public List<ProofHistoryDTO> getAllProofEntries(String tenantId) {

    LOGGER.log(Level.CONFIG, TENANTID_MSG, tenantId);

    JPAQuery<ProofHistoryEntity> query = new JPAQuery<>(em);
    BooleanBuilder whereClause = new BooleanBuilder();

    if (!StringUtils.isBlank(tenantId)) {
      whereClause.and(qProofHistoryEntity.tenant.id.eq(tenantId));
    }

    return ProofHistoryMapper.INSTANCE.toProofHistoryDTO(query.select(qProofHistoryEntity)
      .from(qProofHistoryEntity)
      .fetch());
  }

  @Override
  public List<ProofHistoryDTO> getMostRecentdProofEntries(String tenantId) {

    LOGGER.log(Level.CONFIG, TENANTID_MSG, tenantId);

    JPAQuery<ProofHistoryEntity> query = new JPAQuery<>(em);
    BooleanBuilder whereClause = new BooleanBuilder();

    if (!StringUtils.isBlank(tenantId)) {
      whereClause.and(qProofHistoryEntity.tenant.id.eq(tenantId));
    }

    return ProofHistoryMapper.INSTANCE.toProofHistoryDTO(query.select(qProofHistoryEntity)
      .from(qProofHistoryEntity).where(whereClause, qProofHistoryEntity.toDate.isNull())
      .fetch());
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
  public boolean isFirstCountryProof(String countryId) {
    long count = new JPAQueryFactory(em).select(qProofHistoryEntity).from(qProofHistoryEntity)
      .where(
        qProofHistoryEntity.country.id.eq(countryId).and(qProofHistoryEntity.toDate.isNull()))
      .fetchCount();
    return count == 0 ? true : false;
  }

  @Override
  public boolean checkProofsForInconsistencies(SubmissionDTO submissionDTO,
    List<ProofProvidedMapDTO> proofProvidedMapDTOs) {
    LOGGER.log(Level.CONFIG,
      "Executing method checkProofsForInconsistencies. Parameters: submissionDTO: {0}, "
        + "proofProvidedMapDTOs: {1}",
      new Object[]{submissionDTO, proofProvidedMapDTOs});

    BooleanBuilder whereClause = new BooleanBuilder();
    SelectiveLevel level;
    // Create dynamic query according to process and status.
    if (!submissionDTO.getProcess().equals(Process.SELECTIVE)) {
      // Case for all processes except for selective.
      level = SelectiveLevel.ZERO_LEVEL;
      whereClause.and(qSubmittentProofProvided.submittent.offer.isEmptyOffer.isFalse());
    } else {
      if (compareCurrentVsSpecificStatus(TenderStatus.fromValue(submissionDTO.getStatus()),
        TenderStatus.SUITABILITY_AUDIT_COMPLETED_S)) {
        // Case for second level selective process.
        level = SelectiveLevel.SECOND_LEVEL;
        whereClause.and((qSubmittentProofProvided.submittent.offer.isEmptyOffer.isFalse()
          .or(qSubmittentProofProvided.submittent.offer.isEmptyOffer.isNull()))
          .and((qSubmittentProofProvided.submittent.offer.excludedFirstLevel.isFalse()
            .and(qSubmittentProofProvided.submittent.isApplicant.isTrue()))
            .or(qSubmittentProofProvided.submittent.isApplicant.isNull())));
      } else {
        // Case for first level selective process.
        level = SelectiveLevel.FIRST_LEVEL;
        whereClause.and(qSubmittentProofProvided.submittent.isApplicant.isTrue());
      }
    }
    // Get the proof provided entities.
    List<SubmittentProofProvidedEntity> proofProvidedEntities = new JPAQueryFactory(em)
      .selectFrom(qSubmittentProofProvided)
      .where(qSubmittentProofProvided.submittent.submissionId.id.eq(submissionDTO.getId()),
        qSubmittentProofProvided.level.eq(level.getValue()), whereClause).fetch();
    // Iterate through the saved (proofProvidedEntities) and current (proofProvidedMapDTOs) proof
    // provided values.
    for (SubmittentProofProvidedEntity proofProvidedEntity : proofProvidedEntities) {
      for (ProofProvidedMapDTO proofProvidedMapDTO : proofProvidedMapDTOs) {
        if (
          proofProvidedEntity.getSubmittent().getId().equals(proofProvidedMapDTO.getSubmittentId())
            && (proofProvidedEntity.getCompany().getId()
            .equals(proofProvidedMapDTO.getCompanyId()))) {
          // Compare the saved proof provided value with its current value. If the two values do
          // not agree, return true (inconsistencies are present). Otherwise, keep checking.
          if (proofProvidedEntity.getIsProvided()
            .equals(proofProvidedMapDTO.getIsProofProvided())) {
            break;
          } else {
            return true;
          }
        }
      }
    }
    return false;
  }

  @Override
  public void deleteSubmittentProofProvidedEntries(SubmittentDTO submittentDTO, String companyId,
    boolean isSubmittent) {
    // Create list which is going to contain the submittent (company), subcontractor and joint
    // venture ids.
    List<String> companyIds = new ArrayList<>();
    companyIds.add(submittentDTO.getCompanyId().getId());
    for (CompanyDTO jointVenture : submittentDTO.getJointVentures()) {
      companyIds.add(jointVenture.getId());
    }
    for (CompanyDTO subcontractor : submittentDTO.getSubcontractors()) {
      companyIds.add(subcontractor.getId());
    }
    // If the companyId is included in the companyIds and the deletion process is not for the
    // submittent itself, that means that the given company is either a joint venture or a
    // subcontractor of the submittent as well, so do not delete the proof entries (exit the
    // function).
    if (companyIds.contains(companyId) && !isSubmittent) {
      return;
    }
    // Find the submittentProofProvidedEntities of the current company and delete them.
    List<SubmittentProofProvidedEntity> subProofEntities = new JPAQueryFactory(em)
      .selectFrom(qSubmittentProofProvided)
      .where(qSubmittentProofProvided.company.id.eq(companyId),
        qSubmittentProofProvided.submittent.id.eq(submittentDTO.getId())).fetch();
    for (SubmittentProofProvidedEntity subProofEntity : subProofEntities) {
      em.remove(subProofEntity);
    }
  }
}
