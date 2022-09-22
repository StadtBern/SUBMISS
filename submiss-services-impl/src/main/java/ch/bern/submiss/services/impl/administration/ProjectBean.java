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

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ch.bern.submiss.services.impl.model.*;
import org.apache.commons.lang3.StringUtils;

import com.querydsl.jpa.impl.JPAQueryFactory;

import ch.bern.submiss.services.api.constants.TenderStatus;
import ch.bern.submiss.services.api.administration.SDService;
import ch.bern.submiss.services.api.administration.SDVatService;
import ch.bern.submiss.services.api.administration.SubmissionService;
import ch.bern.submiss.services.api.constants.Process;
import ch.bern.submiss.services.api.dto.CompanyDTO;
import ch.bern.submiss.services.api.dto.MasterListValueHistoryDTO;
import ch.bern.submiss.services.api.dto.ProofHistoryDTO;
import ch.bern.submiss.services.api.dto.SubmittentDTO;
import ch.bern.submiss.services.impl.mappers.MasterListValueMapper;

/**
 * The Class ProjectBean.
 */
@Singleton
public class ProjectBean {

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER = Logger.getLogger(ProjectBean.class.getName());
  /**
   * The Constant SETTLEMENT_TYPE.
   */
  private static final String SETTLEMENT_TYPE = "Verrechnungsart";
  /**
   * The Constant ST3.
   */
  private static final String ST3 = "ST3";
  /**
   * The Constant VT1.
   */
  private static final String VT1 = "VT1";
  /**
   * EntityManager setter.
   */
  @PersistenceContext(unitName = "submiss")
  protected EntityManager em;
  /**
   * The q company proof entity.
   */
  QCompanyProofEntity qCompanyProofEntity = QCompanyProofEntity.companyProofEntity;
  /**
   * The q offer entity.
   */
  QOfferEntity qOfferEntity = QOfferEntity.offerEntity;
  /**
   * The SDservice.
   */
  @Inject
  private SDService sDservice;
  @Inject
  private CacheBean cacheBean;

  @Inject
  private SubmissionService submissionService;

  @Inject
  private SDVatService sDVatService;

  @Inject
  private BaseService baseService;


  /**
   * Creates the default offer.
   *
   * @param submittentEntity the submittent entity
   * @return the offer entity
   */
  public OfferEntity createDefaultOffer(SubmittentEntity submittentEntity) {

    LOGGER.log(Level.CONFIG,
      "Executing method createDefaultOffer, Parameters: submittentEntity: {0}",
      submittentEntity);

    // Get the submission id.
    String submissionId = submittentEntity.getSubmissionId().getId();

    // Get the main vat rate value.
    BigDecimal mainVatRateValue = getMainVatRate(submissionId);

    /** Create default offer */
    OfferEntity offerEntity = new OfferEntity();
    offerEntity.setSubmittent(submittentEntity);
    offerEntity.setGrossAmount(0.0);
    offerEntity.setIsCorrected(Boolean.FALSE);
    offerEntity.setDiscount(0.0);
    offerEntity.setIsOperatingCostDiscountPercentage(Boolean.TRUE);
    offerEntity.setBuildingCosts(0.0);
    offerEntity.setIsBuildingCostsPercentage(Boolean.TRUE);
    offerEntity.setDiscount2Days(30);
    offerEntity.setDiscount2(0.0);
    offerEntity.setIsDiscount2Percentage(Boolean.TRUE);
    offerEntity.setIsDiscountPercentage(Boolean.TRUE);
    offerEntity.setIsDefaultOffer(Boolean.TRUE);
    offerEntity.setSettlement(setSettlementValue());
    offerEntity.setAncilliaryAmountVat(mainVatRateValue.toString());
    offerEntity.setIsAncilliaryAmountPercentage(Boolean.TRUE);
    offerEntity.setAncilliaryAmountGross(0.00);
    offerEntity.setOperatingCostDiscount(0.00);
    offerEntity.setOperatingCostIsVatPercentage(Boolean.TRUE);
    offerEntity.setOperatingCostVat(mainVatRateValue.doubleValue());
    offerEntity.setIsOperatingCostDiscount2Percentage(Boolean.TRUE);
    offerEntity.setIsOperatingCostCorrected(Boolean.FALSE);
    offerEntity.setOperatingCostDiscount2(0.00);
    offerEntity.setVat(mainVatRateValue.doubleValue());
    offerEntity.setIsVatPercentage(Boolean.TRUE);
    offerEntity.setFromMigration(Boolean.FALSE);
    offerEntity.setIsPartOffer(Boolean.FALSE);
    offerEntity.setIsExcludedFromProcess(Boolean.FALSE);
    offerEntity.setIsEmptyOffer(Boolean.FALSE);
    offerEntity.setNachtragSubmittent(Boolean.FALSE);
    offerEntity.setCreatedBy(submittentEntity.getCreatedBy());
    offerEntity.setCreatedOn(submittentEntity.getCreatedOn());

    em.persist(offerEntity);

    return offerEntity;
  }

  /**
   * Creates the default offer for an applicant.
   *
   * @param offerEntity the offer entity
   * @return the offer entity
   */
  public OfferEntity createDefaultOfferForApplicant(OfferEntity offerEntity) {

    LOGGER.log(Level.CONFIG,
      "Executing method createDefaultOfferForApplicant, Parameters: offerEntity: {0}",
      offerEntity);

    // Get the submission id.
    String submissionId = offerEntity.getSubmittent().getSubmissionId().getId();

    // Get the main vat rate value.
    BigDecimal mainVatRateValue = getMainVatRate(submissionId);

    offerEntity.setOfferDate(null);
    offerEntity.setGrossAmount(0.0);
    offerEntity.setGrossAmountCorrected(0.0);
    offerEntity.setIsCorrected(Boolean.FALSE);
    offerEntity.setPriceIncrease(null);
    offerEntity.setNotes(null);
    offerEntity.setIsVariant(null);
    offerEntity.setVariantNotes(null);
    offerEntity.setDiscount(0.0);
    offerEntity.setIsOperatingCostDiscountPercentage(Boolean.TRUE);
    offerEntity.setBuildingCosts(0.0);
    offerEntity.setIsBuildingCostsPercentage(Boolean.TRUE);
    offerEntity.setDiscount2Days(30);
    offerEntity.setDiscount2(0.0);
    offerEntity.setIsDiscount2Percentage(Boolean.TRUE);
    offerEntity.setIsDiscountPercentage(Boolean.TRUE);
    offerEntity.setIsDefaultOffer(Boolean.TRUE);
    offerEntity.setSettlement(setSettlementValue());
    offerEntity.setAncilliaryAmountVat(mainVatRateValue.toString());
    offerEntity.setIsAncilliaryAmountPercentage(Boolean.TRUE);
    offerEntity.setAncilliaryAmountGross(0.00);
    offerEntity.setOperatingCostDiscount(0.00);
    offerEntity.setOperatingCostIsVatPercentage(Boolean.TRUE);
    offerEntity.setOperatingCostVat(mainVatRateValue.doubleValue());
    offerEntity.setOperatingCostNotes(null);
    offerEntity.setOperatingCostsAmount(null);
    offerEntity.setOperatingCostGross(0.0);
    offerEntity.setOperatingCostGrossCorrected(0.0);
    offerEntity.setIsOperatingCostDiscount2Percentage(Boolean.TRUE);
    offerEntity.setIsOperatingCostCorrected(Boolean.FALSE);
    offerEntity.setOperatingCostDiscount2(0.00);
    offerEntity.setVat(mainVatRateValue.doubleValue());
    offerEntity.setIsVatPercentage(Boolean.TRUE);
    offerEntity.setFromMigration(Boolean.FALSE);
    offerEntity.setIsPartOffer(Boolean.FALSE);
    offerEntity.setIsEmptyOffer(Boolean.FALSE);
    offerEntity.setAmount(BigDecimal.ZERO);
    offerEntity.setOperatingCostsAmount(BigDecimal.ZERO);
    offerEntity.setNachtragSubmittent(Boolean.FALSE);
    em.merge(offerEntity);
    return offerEntity;
  }

  /**
   * Function to return the default settlement type value.
   *
   * @return the master list value entity
   */
  private MasterListValueEntity setSettlementValue() {

    LOGGER.log(Level.CONFIG, "Executing method setSettlementValue");

    // Get all the settlement types.
    List<MasterListValueHistoryDTO> settlementTypes =
      sDservice.getMasterListHistoryByType(SETTLEMENT_TYPE);
    for (MasterListValueHistoryDTO settlementType : settlementTypes) {
      // Set the settlement type with short code "ST3" as the default value.
      if (settlementType.getShortCode().equals(ST3)) {
        return MasterListValueMapper.INSTANCE
          .toMasterListValue(settlementType.getMasterListValueId());
      }
    }
    return null;
  }

  /**
   * Gets the main vat rate.
   *
   * @param submissionId the submission id
   * @return the main vat rate
   */
  private BigDecimal getMainVatRate(String submissionId) {
    String mainVatId = null;
    for (MasterListValueHistoryDTO vat : sDVatService.readAll()) {
      if (StringUtils.isNotBlank(vat.getShortCode()) && vat.getShortCode().equals(VT1)) {
        mainVatId = vat.getMasterListValueId().getId();
      }
    }
    MasterListValueHistoryDTO mainVatDTO =
      sDVatService.readVatBySubmission(submissionId, mainVatId);
    return new BigDecimal(mainVatDTO.getValue2());
  }

  public void calculateSubmittentValues(SubmittentDTO submittent, Date deadline,
    Process processType, Boolean forEignungspruefungDoc) {

    LOGGER.log(Level.CONFIG,
      "Executing method calculateSubmittentValues, Parameters: submittent: {0}, "
        + "deadline: {1}, processType: {2}",
      new Object[]{submittent, deadline, processType});

    // Use SubmittentDTO in order to update the value isProofProvided in CompanyDTO.
    // Return the list of proof statuses of every main submittent.
    List<CompanyProofEntity> companyProofEntities =
      new JPAQueryFactory(em).select(qCompanyProofEntity).from(qCompanyProofEntity)
        .where(qCompanyProofEntity.companyId.id.eq(submittent.getCompanyId().getId())
          .and(qCompanyProofEntity.required.isTrue()))
        .fetch();
    // If none of the proofs of the specific company is required then "Nachweise erbracht"
    // should be yes. Set by default isProofProvided to true.
    submittent.getCompanyId().setIsProofProvided(Boolean.TRUE);
    // Calculate the value isProofProvided for every main submittent of the current submission.
    for (CompanyProofEntity cProof : companyProofEntities) {
      // historization part UC160
      ProofHistoryDTO proof = cacheBean.getValueProof(cProof.getProofId().getId(),
        submissionService.getRefernceDateForProofs(submittent.getSubmissionId().getId()));
      if (proof.getActive() != null && proof.getActive().equals(1)
        && proof.getValidityPeriod() != null) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(deadline);
        cal.add(Calendar.MONTH, -proof.getValidityPeriod());
        Date beforeProofValidityPeriod = cal.getTime();
        if (cProof != null && cProof.getProofDate() != null
          && cProof.getProofDate().after(beforeProofValidityPeriod)) {
          submittent.getCompanyId().setIsProofProvided(Boolean.TRUE);
        } else {
          submittent.getCompanyId().setIsProofProvided(Boolean.FALSE);
          break;
        }
      }
    }
    boolean isNotNegotiatedProcedure = !processType.equals(Process.NEGOTIATED_PROCEDURE)
      && !processType.equals(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION);
    // If the calculated isProofProvided value of the main submittent is false then set the
    // formalExaminationFulfilled to false.
    if(isNotNegotiatedProcedure){
      // We don't want to ovewrite the saved value of FormalExaminationFulfilled for
      // Negotiated Procedures
      if (submittent.getCompanyId().getIsProofProvided() != null
        && !submittent.getCompanyId().getIsProofProvided()) {
        submittent.setFormalExaminationFulfilled(Boolean.FALSE);
      } else {
        submittent.setFormalExaminationFulfilled(Boolean.TRUE);
      }
    }

    // Calculate the value isProofProvided for every joint venture of the specific submittent of
    // the current submission.
    if (!submittent.getJointVentures().isEmpty()) {
      Set<CompanyDTO> jointVenturesDTOs = new HashSet<>();
      for (CompanyDTO jointVentureDTO : submittent.getJointVentures()) {
        // Return the list of proof statuses of the joint ventures
        List<CompanyProofEntity> jointVenturesCompanyProofEntities =
          new JPAQueryFactory(em).select(qCompanyProofEntity).from(qCompanyProofEntity)
            .where(qCompanyProofEntity.companyId.id.eq(jointVentureDTO.getId())
              .and(qCompanyProofEntity.required.isTrue()))
            .fetch();
        jointVentureDTO.setIsProofProvided(Boolean.TRUE);
        // Calculate the isProofProvided for every joint venture of the main submittent in the
        // current submission.
        for (CompanyProofEntity cProof : jointVenturesCompanyProofEntities) {
          // historization part UC160
          ProofHistoryDTO proof = cacheBean.getValueProof(cProof.getProofId().getId(),
            submissionService.getRefernceDateForProofs(submittent.getSubmissionId().getId()));
          if (proof.getActive() != null && proof.getActive().equals(1)
            && proof.getValidityPeriod() != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(deadline);
            cal.add(Calendar.MONTH, -proof.getValidityPeriod());
            Date beforeProofValidityPeriod = cal.getTime();
            if (cProof != null && cProof.getProofDate() != null
              && cProof.getProofDate().after(beforeProofValidityPeriod)) {
              jointVentureDTO.setIsProofProvided(Boolean.TRUE);
            } else {
              jointVentureDTO.setIsProofProvided(Boolean.FALSE);
              jointVenturesDTOs.add(jointVentureDTO);
              break;
            }
          }
        }
        jointVenturesDTOs.add(jointVentureDTO);
        // If the calculated isProofProvided value of one of the joint ventures of the main
        // submittent is false then set the formalExaminationFulfilled to false
        if (jointVentureDTO.getIsProofProvided() != null && !jointVentureDTO.getIsProofProvided()) {
          if(isNotNegotiatedProcedure){
            // We don't want to ovewrite the saved value of FormalExaminationFulfilled for
            // Negotiated Procedures
            submittent.setFormalExaminationFulfilled(Boolean.FALSE);
          }
          // If called for Eignungsprüfung document we need to set
          // Proof status of main Submittent to false.
          if(forEignungspruefungDoc){
            submittent.getCompanyId().setIsProofProvided(Boolean.FALSE);
          }
        }
      }
      submittent.setJointVentures(jointVenturesDTOs);
    }
    if (!submittent.getSubcontractors().isEmpty()) {
      Set<CompanyDTO> subcontractorsDTOs = new HashSet<>();
      for (CompanyDTO subcontractor : submittent.getSubcontractors()) {
        // Return the list of proof statuses of the joint ventures
        List<CompanyProofEntity> subcontractorCompanyProofEntities =
          new JPAQueryFactory(em).select(qCompanyProofEntity).from(qCompanyProofEntity)
            .where(qCompanyProofEntity.companyId.id.eq(subcontractor.getId())
              .and(qCompanyProofEntity.required.isTrue()))
            .fetch();
        subcontractor.setIsProofProvided(Boolean.TRUE);
        // Calculate the isProofProvided for every joint venture of the main submittent in the
        // current submission.
        for (CompanyProofEntity cProof : subcontractorCompanyProofEntities) {
          // historization part UC160 Nachweisart
          ProofHistoryDTO proof = cacheBean.getValueProof(cProof.getProofId().getId(),
            submissionService.getRefernceDateForProofs(submittent.getSubmissionId().getId()));
          if (proof.getActive() != null && proof.getActive().equals(1)
            && proof.getValidityPeriod() != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(deadline);
            cal.add(Calendar.MONTH, -proof.getValidityPeriod());
            Date beforeProofValidityPeriod = cal.getTime();
            if (cProof != null && cProof.getProofDate() != null
              && cProof.getProofDate().after(beforeProofValidityPeriod)) {
              subcontractor.setIsProofProvided(Boolean.TRUE);
            } else {
              subcontractor.setIsProofProvided(Boolean.FALSE);
              subcontractorsDTOs.add(subcontractor);
              break;
            }
          }
        }
        subcontractorsDTOs.add(subcontractor);
        // If the calculated isProofProvided value of one of the subcontractors of the main
        // submittent is false then set the formalExaminationFulfilled to false and Proof status of
        // main Submittent to false.
        if (subcontractor.getIsProofProvided() != null && !subcontractor.getIsProofProvided()) {
          if(isNotNegotiatedProcedure){
            // We don't want to ovewrite the saved value of FormalExaminationFulfilled for
            // Negotiated Procedures
            submittent.setFormalExaminationFulfilled(Boolean.FALSE);
          }
          // If called for Eignungsprüfung document we need to set
          // Proof status of main Submittent to false.
          if(forEignungspruefungDoc){
            submittent.getCompanyId().setIsProofProvided(Boolean.FALSE);
          }
        }
      }
      submittent.setSubcontractors(subcontractorsDTOs);
    }
    /*
     * If value formalExaminationFulfilled is false then set existsExclusionReasons to true. Don't
     * set this Value for the following Processes 1.NEGOTIATED_PROCEDURE
     * 2.NEGOTIATED_PROCEDURE_WITH_COMPETITION
     * 3.SELECTIVE 1 STUFE
     */
    //missing "Nachweise" are no longer an automatic reason for exlusion in "Selektives Verfahren" 1. Stufe.
    if (submittent.getFormalExaminationFulfilled() != null
      && !submittent.getFormalExaminationFulfilled()
      && !processType.equals(Process.NEGOTIATED_PROCEDURE)
      && !processType.equals(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION)
      && !forEignungspruefungDoc
      && (!processType.equals(Process.SELECTIVE)
      || (processType.equals(Process.SELECTIVE)
      && baseService.compareCurrentVsSpecificStatus(TenderStatus.fromValue(submittent.getSubmissionId().getStatus()), TenderStatus.SUBMITTENT_LIST_CREATED))))
    {
      submittent.setExistsExclusionReasons(Boolean.TRUE);
    }

    Boolean isVariant = new JPAQueryFactory(em).select(qOfferEntity.isVariant).from(qOfferEntity)
      .where(qOfferEntity.submittent.id.eq(submittent.getId())).fetchFirst();
    submittent.setIsVariant(isVariant);

  }
}
