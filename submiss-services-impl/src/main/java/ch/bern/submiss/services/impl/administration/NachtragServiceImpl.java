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

import ch.bern.submiss.services.api.administration.NachtragService;
import ch.bern.submiss.services.api.administration.OfferService;
import ch.bern.submiss.services.api.administration.SubDocumentService;
import ch.bern.submiss.services.api.administration.SubmissionService;
import ch.bern.submiss.services.api.constants.AuditEvent;
import ch.bern.submiss.services.api.constants.AuditGroupName;
import ch.bern.submiss.services.api.constants.AuditLevel;
import ch.bern.submiss.services.api.constants.AuditMessages;
import ch.bern.submiss.services.api.constants.SecurityOperation;
import ch.bern.submiss.services.api.constants.Template;
import ch.bern.submiss.services.api.dto.DocumentDTO;
import ch.bern.submiss.services.api.dto.NachtragDTO;
import ch.bern.submiss.services.api.dto.OfferDTO;
import ch.bern.submiss.services.api.dto.SubmissionDTO;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.api.util.ValidationMessages;
import ch.bern.submiss.services.impl.mappers.NachtragDTOMapper;
import ch.bern.submiss.services.impl.mappers.OfferDTOMapper;
import ch.bern.submiss.services.impl.mappers.SubmissionMapper;
import ch.bern.submiss.services.impl.model.MasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.NachtragEntity;
import ch.bern.submiss.services.impl.model.OfferEntity;
import ch.bern.submiss.services.impl.model.QMasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.QNachtragEntity;
import ch.bern.submiss.services.impl.model.QOfferEntity;
import ch.bern.submiss.services.impl.model.SubmissionEntity;
import com.eurodyn.qlack2.fuse.cm.api.dto.VersionDTO;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import com.querydsl.jpa.impl.JPAQueryFactory;
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

@Transactional
@OsgiServiceProvider(classes = {NachtragService.class})
@Singleton
public class NachtragServiceImpl extends BaseService implements NachtragService {

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER = Logger.getLogger(NachtragServiceImpl.class.getName());

  /**
   * The q nachtrag entity.
   */
  QNachtragEntity qNachtragEntity = QNachtragEntity.nachtragEntity;

  /**
   * The q offer entity.
   */
  QOfferEntity qOfferEntity = QOfferEntity.offerEntity;

  /**
   * The q master list value history entity.
   */
  QMasterListValueHistoryEntity qMasterListValueHistoryEntity =
    QMasterListValueHistoryEntity.masterListValueHistoryEntity;

  /**
   * The audit.
   */
  @Inject
  private AuditBean audit;

  @Inject
  protected OfferService offerService;

  @Inject
  protected SubmissionService submissionService;

  @Inject
  protected SubDocumentService documentService;

  @Override
  public NachtragDTO getNachtrag(String nachtragId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getNachtrag, Parameters: nachtragId: {0}",
      nachtragId);

    security.isPermittedOperationForUser(getUserId(),
      SecurityOperation.NACHTRAG_VIEW.getValue(), null);

    NachtragEntity nachtragEntity = em.find(NachtragEntity.class, nachtragId);
    return NachtragDTOMapper.INSTANCE.toNachtragDTO(nachtragEntity);
  }

  @Override
  public List<OfferDTO> getNachtragSubmittents(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getNachtragSubmittents, Parameters: submissionId: {0}",
      submissionId);

    List<OfferEntity> nachtragSubmittentsEntities = new JPAQueryFactory(em).selectFrom(qOfferEntity)
        .where(qOfferEntity.submittent.submissionId.id.eq(submissionId),
          qOfferEntity.isNachtragSubmittent.eq(Boolean.TRUE))
        .fetch();

    List<OfferDTO> nachtragSubmittents = OfferDTOMapper.INSTANCE
      .toOfferDTO(nachtragSubmittentsEntities);

    // If Submittent is not awarded anymore set NachtragSubmittent to false
    nachtragSubmittents.forEach(offerDTO ->
      offerDTO.setNachtragSubmittent(offerDTO.getIsAwarded()));

    // Old Zuschlagsempfänger must disappear from the Auftragserweiterung
    // if they have no Nachtrag
    nachtragSubmittents.removeIf(offerDTO -> hasNoNachtrag(offerDTO));

    nachtragSubmittents.forEach(offerDTO ->
      offerDTO.setHasActiveNachtrag(isNachtragActive(offerDTO.getId())));

    // multiple awarded Submittents
    if(nachtragSubmittents.size() > 1){
      // In case of manual amount in multiple awarded Submittents we have to show it in Nettobetrag
      nachtragSubmittents.forEach(offerDTO ->
        offerDTO.setAmount(offerDTO.getManualAmount() != null
          ? offerDTO.getManualAmount() : offerDTO.getAmount()));
    }
    // We have to calculate the gross amount from the manual amount inserted if it exists
    nachtragSubmittents.forEach(offerDTO ->
      offerDTO.setGrossAmount(calculateGrossAmount(offerDTO, nachtragSubmittents.size() > 1)));
    return nachtragSubmittents;
  }

  private boolean hasNoNachtrag(OfferDTO offerDTO){
    boolean hasNoNachtrag = false;
      // If he has no saved Nachtrag update isNachtragSubmittent to false
      if(!hasNachtrag(offerDTO.getId()) && !offerDTO.getNachtragSubmittent()){
        OfferEntity offer = em.find(OfferEntity.class, offerDTO.getId());
        offer.setNachtragSubmittent(false);
        em.merge(offer);
        hasNoNachtrag = true;
    }
    return hasNoNachtrag;
  }

  private boolean isNachtragActive(String offerId) {

    LOGGER.log(Level.CONFIG,
      "Executing method isNachtragActive, Parameters: offerId: {0}",
      offerId);

    long activeNachtrag = new JPAQueryFactory(em).selectFrom(qNachtragEntity)
      .where(qNachtragEntity.offer.id.eq(offerId),
        qNachtragEntity.isClosed.eq(Boolean.FALSE).or(qNachtragEntity.isClosed.isNull()))
      .fetchCount();

    return activeNachtrag != 0;
  }

  private boolean hasNachtrag(String offerId) {

    LOGGER.log(Level.CONFIG,
      "Executing method isNachtragActive, Parameters: offerId: {0}",
      offerId);

    long nachtragCount = new JPAQueryFactory(em).selectFrom(qNachtragEntity)
      .where(qNachtragEntity.offer.id.eq(offerId))
      .fetchCount();

    return nachtragCount != 0;
  }

  private Double calculateGrossAmount(OfferDTO offerDTO, boolean moreThanOneWinner) {

    if(offerDTO.getManualAmount() != null && offerDTO.getAmount() != null && moreThanOneWinner){
      // Ignore costs and discounts if they are not percentages
      Double rabatt = 0.0;
      Double buildingCosts = 0.0;
      Double skonto = 0.0;
      if(offerDTO.getIsDiscountPercentage()){
        rabatt = offerDTO.getDiscount();
      }
      if(offerDTO.getIsBuildingCostsPercentage()){
        buildingCosts = offerDTO.getBuildingCosts();
      }
      if(offerDTO.getIsDiscount2Percentage()){
        skonto = offerDTO.getDiscount2();
      }
      // Calculation step 1 (Restbrutto % ohne Rabatt)
      Double calcStep1 = 1-(rabatt/100)-((1-(rabatt/100))*(buildingCosts/100));
      // Calculation  step 2 (Restbrutto % ohne Rabatt und ohne BNK)
      Double calcStep2 = calcStep1 * (skonto/100);
      // Calculation step 3 (Restbrutto % ohne Rabatt, ohne BNK und ohne Skonto)
      Double calcStep3 = calcStep1 - calcStep2;
      // Manueller Bruttobetrag :
      return offerDTO.getAmount().doubleValue() / calcStep3;
    }else{
      return offerDTO.getIsCorrected()
        ? offerDTO.getGrossAmountCorrected() : offerDTO.getGrossAmount();
    }
  }

  @Override
  public List<NachtragDTO> getNachtragsByNachtragSubmittent(String offerId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getNachtragsByNachtragSubmittent, Parameters: offerId: {0}",
      offerId);

    return NachtragDTOMapper.INSTANCE
      .toNachtragDTO((new JPAQueryFactory(em).selectFrom(qNachtragEntity)
        .where(qNachtragEntity.offer.id.eq(offerId)).orderBy(qNachtragEntity.createdOn.asc())
        .fetch()));
  }

  @Override
  public List<NachtragDTO> getNachtragsBySubmittentId(String submittentId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getNachtragsByNachtragSubmittent, Parameters: submittentId: {0}",
      submittentId);

    return NachtragDTOMapper.INSTANCE
      .toNachtragDTO((new JPAQueryFactory(em).selectFrom(qNachtragEntity)
        .where((qNachtragEntity.offer.submittent.id.eq(submittentId))
          .and(qNachtragEntity.isClosed.isTrue()))
        .orderBy(qNachtragEntity.nachtragDate.desc())
        .fetch()));
  }

  @Override
  public void updateNachtrag(NachtragDTO nachtragDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateNachtrag, Parameters: nachtragDTO: {0}",
      nachtragDTO);

    // Security check for Nachtrag
    security.isPermittedOperationForUser(getUserId(),
      SecurityOperation.NACHTRAG_EDIT.getValue(), null);

    checkNachtragOptimisticLock(nachtragDTO.getId(),nachtragDTO.getVersion());

      if (nachtragDTO != null) {
        // Update the nachtrag
        NachtragEntity nachtragEntity = NachtragDTOMapper.INSTANCE.toNachtrag(nachtragDTO);
        nachtragEntity.setUpdatedBy(getUserId());
        em.merge(nachtragEntity);

        // Set isNachtragCreated to true for Submission for Nachtrag to appear in document list
        SubmissionDTO submissionDTO = submissionService
          .getSubmissionById(nachtragDTO.getOffer().getSubmittent().getSubmissionId().getId());
        submissionDTO.setIsNachtragCreated(true);
        SubmissionEntity submissionEntity = SubmissionMapper.INSTANCE.toSubmission(submissionDTO);
        em.merge(submissionEntity);
      }
  }

  @Override
  public void createNachtrag(String offerId) {

    LOGGER.log(Level.CONFIG,
      "Executing method createNachtrag, Parameters: offerId: {0}",
      offerId);

    OfferEntity offerEntity = em.find(OfferEntity.class, offerId);

    security.isPermittedOperationForUser(getUserId(),
      SecurityOperation.NACHTRAG_CREATE.getValue(), null);

    NachtragEntity nachtragEntity = new NachtragEntity();
    nachtragEntity.setOffer(offerEntity);
    nachtragEntity.setNachtragName(generateNachtragName(offerId));
    // According to UC[204] we pass the offer's discount value if it was calculated in percentage otherwise the default value is 0.00
    nachtragEntity.setDiscount((Boolean.TRUE.equals(offerEntity.getIsDiscountPercentage())) ? offerEntity.getDiscount() : 0.00);
    nachtragEntity.setIsDiscountPercentage(Boolean.TRUE);
    nachtragEntity.setDiscount2(0.00);
    nachtragEntity.setIsDiscount2Percentage(Boolean.TRUE);
    nachtragEntity.setGrossAmount(0.00);
    if(offerEntity.getIsBuildingCostsPercentage()){
      nachtragEntity.setBuildingCosts(offerEntity.getBuildingCosts());
      nachtragEntity.setIsBuildingCostsPercentage(offerEntity.getIsBuildingCostsPercentage());
    }
    nachtragEntity.setVat(offerEntity.getVat());
    nachtragEntity.setIsVatPercentage(offerEntity.getIsVatPercentage());
    nachtragEntity.setClosed(Boolean.FALSE);
    nachtragEntity.setCreatedBy(getUserId());
    em.persist(nachtragEntity);
  }

  /**
   * Generates the nachtrag name to be stored in the database.
   *
   * @param offerId the offerId
   * @return the nachtrag name
   */
  private String generateNachtragName(String offerId) {

    LOGGER.log(Level.CONFIG,
      "Executing method generateNachtragName, Parameters: offerId: {0}",
      offerId);

    long nachtragCount = new JPAQueryFactory(em).selectFrom(qNachtragEntity)
      .where(qNachtragEntity.offer.id.eq(offerId)).fetchCount();

    return "Nachtrag " + (nachtragCount + 1);
  }

  @Override
  public void deleteNachtrag(String nachtragId, Long version) {

    LOGGER.log(Level.CONFIG,
      "Executing method deleteNachtrag, Parameters: nachtragId: {0}",
      nachtragId);

    NachtragEntity nachtragEntity = checkNachtragOptimisticLock(nachtragId, version);

    security.isPermittedOperationForUser(getUserId(),
      SecurityOperation.NACHTRAG_DELETE.getValue(), null);

    if (nachtragEntity != null) {
      em.remove(nachtragEntity);
    }
  }

  @Override
  public void addNachtragSubmittent(String offerId) {

    LOGGER.log(Level.CONFIG,
      "Executing method addNachtragSubmittent, Parameters: offerId: {0}",
      offerId);

    OfferEntity offerEntity = em.find(OfferEntity.class, offerId);
    offerEntity.setNachtragSubmittent(Boolean.TRUE);
    offerEntity.setUpdatedBy(getUserId());
    em.merge(offerEntity);
  }

  @Override
  public List<OfferDTO> getAwardedOffers(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getAwardedOffers, Parameters: submissionId: {0}",
      submissionId);

    return OfferDTOMapper.INSTANCE
      .toOfferDTO((new JPAQueryFactory(em).selectFrom(qOfferEntity)
        .where(qOfferEntity.submittent.submissionId.id.eq(submissionId),
          qOfferEntity.isAwarded.eq(Boolean.TRUE)).fetch()));
  }

  @Override
  public Set<ValidationError> closeNachtrag(String nachtragId, Long version) {

    LOGGER.log(Level.CONFIG,
      "Executing method closeNachtrag, Parameters: nachtragId: {0}",
      nachtragId);

    Set<ValidationError> validationErrors = new HashSet<>();

    NachtragEntity nachtragEntity = checkNachtragOptimisticLock(nachtragId, version);

    DocumentDTO documentDTO = new DocumentDTO();
    documentDTO.setFolderId(nachtragEntity.getOffer().getSubmittent().getSubmissionId().getId());
    documentDTO.setTemplateId(
      new JPAQueryFactory(em).selectFrom(qMasterListValueHistoryEntity)
        .where(qMasterListValueHistoryEntity.shortCode.eq(Template.NACHTRAG)
          .and(qMasterListValueHistoryEntity.tenant.id
            .eq(nachtragEntity.getOffer().getSubmittent().getSubmissionId().getProject().getTenant().getId())))
        .fetchOne().getMasterListValueId().getId());
    documentDTO.setTenantId(nachtragEntity.getOffer().getSubmittent().getSubmissionId().getProject().getTenant().getId());
    documentDTO.setNachtragId(nachtragEntity.getId());

    VersionDTO latestDocVersion = documentService.getDocLatestversion(documentDTO, nachtragEntity.getOffer().getSubmittent().getId(),
      nachtragEntity.getOffer().getSubmittent().getCompanyId().getId());

    if(latestDocVersion == null){
      validationErrors.add(new ValidationError(ValidationMessages.DOCUMENT_ERROR_FIELD,
        ValidationMessages.MANDATORY_NACHTRAG_DOCUMENT_MESSAGE));
      return validationErrors;
    }
      security.isPermittedOperationForUser(getUserId(),
        SecurityOperation.NACHTRAG_CLOSE.getValue(), null);

      if (nachtragEntity != null) {
        nachtragEntity.setClosed(Boolean.TRUE);
        nachtragEntity.setUpdatedBy(getUserId());
        em.merge(nachtragEntity);

        // Add audit log
        createAuditLog(nachtragEntity, AuditMessages.NACHTRAG_CLOSE.name(), null);
      }
    return validationErrors;
  }

  @Override
  public void reopenNachtrag(String nachtragId, String reopenReason, Long version) {

    LOGGER.log(Level.CONFIG,
      "Executing method reopenNachtrag, Parameters: nachtragId: {0}, reopenReason: {1}",
      new Object[]{nachtragId, reopenReason});

    NachtragEntity nachtragEntity = checkNachtragOptimisticLock(nachtragId, version);

    security.isPermittedOperationForUser(getUserId(),
      SecurityOperation.NACHTRAG_REOPEN.getValue(), null);

    if (nachtragEntity != null) {
      nachtragEntity.setClosed(Boolean.FALSE);
      nachtragEntity.setUpdatedBy(getUserId());
      em.merge(nachtragEntity);

      // Add audit log
      createAuditLog(nachtragEntity, AuditMessages.NACHTRAG_REOPEN.name(), reopenReason);
    }
  }

  /**
   * Create audit log.
   *
   * @param nachtragEntity the nachtragEntity
   * @param auditMessage   the audit message
   * @param reopenReason   the reopen reason
   */
  private void createAuditLog(NachtragEntity nachtragEntity, String auditMessage,
    String reopenReason) {

    LOGGER.log(Level.CONFIG,
      "Executing method createAuditLog, Parameters: nachtragEntity: {0}, auditMessage: {1}, "
        + "reopenReason: {2}",
      new Object[]{nachtragEntity, auditMessage, reopenReason});

    MasterListValueHistoryEntity workType = new JPAQueryFactory(em)
      .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
      .where(qMasterListValueHistoryEntity.masterListValueId
        .eq(nachtragEntity.getOffer().getSubmittent().getSubmissionId().getWorkType())
        .and(qMasterListValueHistoryEntity.toDate.isNull()))
      .fetchOne();

    MasterListValueHistoryEntity objectEntity = new JPAQueryFactory(em)
      .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
      .where(qMasterListValueHistoryEntity.masterListValueId
        .eq(
          nachtragEntity.getOffer().getSubmittent().getSubmissionId().getProject().getObjectName())
        .and(qMasterListValueHistoryEntity.toDate.isNull()))
      .fetchOne();

    String workTypeValue2 = (StringUtils.isEmpty(workType.getValue2())) ? StringUtils.EMPTY
      : " " + workType.getValue2();

    StringBuilder additionalInfo =
      new StringBuilder(
        nachtragEntity.getOffer().getSubmittent().getSubmissionId().getProject().getProjectName())
        .append("[#]").append(objectEntity.getValue1()).append("[#]").append(workType.getValue1())
        .append(workTypeValue2).append("[#]")
        .append(getUser().getAttributeData(LookupValues.USER_ATTRIBUTES.TENANT.getValue()))
        .append("[#]");

    audit.createLogAudit(AuditLevel.PROJECT_LEVEL.name(), AuditEvent.STATUS_CHANGE.name(),
      AuditGroupName.SUBMISSION.name(), auditMessage, getUserId(),
      nachtragEntity
        .getOffer().getSubmittent().getSubmissionId().getId(),
      reopenReason, additionalInfo.toString(), LookupValues.EXTERNAL_LOG);
  }

  public NachtragEntity checkNachtragOptimisticLock(String nachtragId, Long nachtragVersion) {
    NachtragEntity nachtragEntity = em.find(NachtragEntity.class, nachtragId);
    if(nachtragEntity == null){
      throw new OptimisticLockException(ValidationMessages.NACHTRAG_DELETED_MESSAGE);
    }
    if (!nachtragVersion.equals(nachtragEntity.getVersion())) {
      throw new OptimisticLockException();
    }
    return nachtragEntity;
  }
}
