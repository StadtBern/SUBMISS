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

import ch.bern.submiss.services.api.administration.CriterionService;
import ch.bern.submiss.services.api.administration.OfferService;
import ch.bern.submiss.services.api.administration.SubmissionService;
import ch.bern.submiss.services.api.constants.Process;
import ch.bern.submiss.services.api.constants.SecurityOperation;
import ch.bern.submiss.services.api.constants.TemplateConstants;
import ch.bern.submiss.services.api.constants.TenderStatus;
import ch.bern.submiss.services.api.dto.AwardAssessDTO;
import ch.bern.submiss.services.api.dto.AwardDTO;
import ch.bern.submiss.services.api.dto.CriterionDTO;
import ch.bern.submiss.services.api.dto.CriterionLiteDTO;
import ch.bern.submiss.services.api.dto.ExaminationDTO;
import ch.bern.submiss.services.api.dto.OfferCriterionDTO;
import ch.bern.submiss.services.api.dto.OfferCriterionLiteDTO;
import ch.bern.submiss.services.api.dto.OfferDTO;
import ch.bern.submiss.services.api.dto.OfferSubcriterionLiteDTO;
import ch.bern.submiss.services.api.dto.SubcriterionDTO;
import ch.bern.submiss.services.api.dto.SubmissionDTO;
import ch.bern.submiss.services.api.dto.SuitabilityDTO;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.api.util.ValidationMessages;
import ch.bern.submiss.services.impl.mappers.CriterionDTOMapper;
import ch.bern.submiss.services.impl.mappers.MasterListValueMapper;
import ch.bern.submiss.services.impl.mappers.OfferDTOWithCriteriaMapper;
import ch.bern.submiss.services.impl.mappers.SubcriterionDTOMapper;
import ch.bern.submiss.services.impl.mappers.SubmittentDTOMapper;
import ch.bern.submiss.services.impl.model.CriterionEntity;
import ch.bern.submiss.services.impl.model.FormalAuditEntity;
import ch.bern.submiss.services.impl.model.OfferCriterionEntity;
import ch.bern.submiss.services.impl.model.OfferEntity;
import ch.bern.submiss.services.impl.model.OfferSubcriterionEntity;
import ch.bern.submiss.services.impl.model.QCriterionEntity;
import ch.bern.submiss.services.impl.model.QFormalAuditEntity;
import ch.bern.submiss.services.impl.model.QOfferCriterionEntity;
import ch.bern.submiss.services.impl.model.QOfferEntity;
import ch.bern.submiss.services.impl.model.QOfferSubcriterionEntity;
import ch.bern.submiss.services.impl.model.QSubcriterionEntity;
import ch.bern.submiss.services.impl.model.QSubmissionEntity;
import ch.bern.submiss.services.impl.model.QSubmittentEntity;
import ch.bern.submiss.services.impl.model.SubcriterionEntity;
import ch.bern.submiss.services.impl.model.SubmissionEntity;
import ch.bern.submiss.services.impl.model.SubmittentEntity;
import ch.bern.submiss.services.impl.util.ComparatorUtil;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.OptimisticLockException;
import javax.persistence.RollbackException;
import javax.transaction.Transactional;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

/**
 * The Class CriterionServiceImpl.
 */
@Transactional
@OsgiServiceProvider(classes = {CriterionService.class})
@Singleton
public class CriterionServiceImpl extends BaseService implements CriterionService {

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER = Logger.getLogger(CriterionServiceImpl.class.getName());

  /**
   * The Constant FORMULA.
   */
  private static final String FORMULA = "formula";

  /**
   * The Constant MIN_ERROR_FIELD.
   */
  private static final String MIN_ERROR_FIELD = "minErrorField";

  /**
   * The Constant MAX_ERROR_FIELD.
   */
  private static final String MAX_ERROR_FIELD = "maxErrorField";

  /**
   * The Constant WEIGHTING.
   */
  private static final String WEIGHTING = ", weighting: {1}";

  /**
   * The math parser.
   */
  @Inject
  protected MathParserBean mathParser;
  @Inject
  ProjectBean projectBean;
  /**
   * The q criterion entity.
   */
  QCriterionEntity qCriterionEntity = QCriterionEntity.criterionEntity;
  /**
   * The q subcriterion entity.
   */
  QSubcriterionEntity qSubcriterionEntity = QSubcriterionEntity.subcriterionEntity;
  /**
   * The q offer entity.
   */
  QOfferEntity qOfferEntity = QOfferEntity.offerEntity;
  /**
   * The q submittent entity.
   */
  QSubmittentEntity qSubmittentEntity = QSubmittentEntity.submittentEntity;
  /**
   * The q submission entity.
   */
  QSubmissionEntity qSubmissionEntity = QSubmissionEntity.submissionEntity;
  /**
   * The q offer subriterion entity.
   */
  QOfferSubcriterionEntity qOfferSubriterionEntity = QOfferSubcriterionEntity.offerSubcriterionEntity;
  /**
   * The q offer criterion entity.
   */
  QOfferCriterionEntity qOfferCriterionEntity = QOfferCriterionEntity.offerCriterionEntity;
  /**
   * The q formal audit entity.
   */
  QFormalAuditEntity qFormalAuditEntity = QFormalAuditEntity.formalAuditEntity;
  /**
   * The submission service.
   */
  @Inject
  private SubmissionService submissionService;

  /**
   * The offer service.
   */
  @Inject
  private OfferService offerService;
  
  @Override
  public String addCriterionToSubmission(CriterionDTO criterion) {

    LOGGER.log(Level.CONFIG,
      "Executing method addCriterionToSubmission, Parameters: criterion: {0}", criterion);

    CriterionEntity criterionEntity = CriterionDTOMapper.INSTANCE.toCriterion(criterion);
    SubmissionEntity submissionEntity = em.find(SubmissionEntity.class, criterion.getSubmission());

    criterionEntity.setSubmission(submissionEntity);
    em.persist(criterionEntity);

    // after inserting a new criterion the weighting of the price criterion is decreased
    Double priceCriterionWeighting = null;
    if (criterion.getCriterionType().equals(LookupValues.AWARD_CRITERION_TYPE)
      || criterion.getCriterionType().equals(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE)) {
      CriterionEntity priceCriterion =
        new JPAQueryFactory(em).select(qCriterionEntity).from(qCriterionEntity)
          .where(qCriterionEntity.submission.id.eq(criterion.getSubmission()).and(
            (qCriterionEntity.criterionType.eq(LookupValues.PRICE_AWARD_CRITERION_TYPE))))
          .fetchOne();
      priceCriterionWeighting = priceCriterion.getWeighting() - criterion.getWeighting();
      priceCriterion.setWeighting(priceCriterionWeighting);
      em.persist(priceCriterion);
    }
    /* UC(039) Create empty OfferCriterionEntity for each offer of the specific submission. */
    createOfferCriterion(criterionEntity);

    // because the weighting of the price criterion is decreased the score of all offer criteria of
    // the price criterion
    // need to be recalculated and total score, total rank need to be recalculated

    List<OfferEntity> offerEntities = new JPAQueryFactory(em).select(qOfferEntity)
      .from(qOfferEntity).where(qOfferEntity.submittent.submissionId.id
        .eq(criterion.getSubmission()).and(qOfferEntity.isEmptyOffer.isFalse()))
      .fetch();
    /*
     * iterate all offer criteria in order to find and update the score of the price criterion and
     * in order to add the scores of all other criteria in order to calculate the total score of
     * each offer
     */
    for (OfferEntity offer : offerEntities) {
      double sumTotalScore = 0;
      for (OfferCriterionEntity offerCriterion : offer.getOfferCriteria()) {
        if (priceCriterionWeighting != null && offerCriterion.getCriterion().getCriterionType()
          .equals(LookupValues.PRICE_AWARD_CRITERION_TYPE)) {
          BigDecimal score = calculateScore(offerCriterion.getGrade(), priceCriterionWeighting);
          offerCriterion.setScore(score);
          em.merge(offerCriterion);
          if (score != null) {
            sumTotalScore = sumTotalScore + score.doubleValue();
          }
        } else if ((!offerCriterion.getCriterion().getCriterionType()
          .equals(LookupValues.EVALUATED_CRITERION_TYPE)
          && !offerCriterion.getCriterion().getCriterionType()
          .equals(LookupValues.MUST_CRITERION_TYPE))
          && offerCriterion.getScore() != null) {
          sumTotalScore = sumTotalScore + offerCriterion.getScore().doubleValue();
        }
      }
      offer.setAwardTotalScore(BigDecimal.valueOf(sumTotalScore));
    }

    TenderStatus submissionStatus = TenderStatus
      .fromValue(submissionService.getCurrentStatusOfSubmission(criterion.getSubmission()));
    rankOffers(offerEntities, submissionStatus);

    return criterionEntity.getId();
  }

  @Override
  public Set<ValidationError> deleteCriterion(String id, Timestamp pageRequestedOn) {

    LOGGER.log(Level.CONFIG, "Executing method deleteCriterion, Parameters: id: {0}, "
        + "pageRequestedOn: {1}",
      new Object[]{id, pageRequestedOn});

    CriterionEntity criterion = em.find(CriterionEntity.class, id);
    Set<ValidationError> optimisticLockErrors = deleteCriterionOptimisticLock(criterion, pageRequestedOn);
    if (optimisticLockErrors.isEmpty()) {
      deleteCriterionProcess(criterion);
    }
    return optimisticLockErrors;
  }

  /**
   * Checking for Optimistic Lock errors.
   *
   * @param criterion the criterion
   * @param pageRequestedOn the pageRequestedOn
   * @return the error
   */
  private Set<ValidationError> deleteCriterionOptimisticLock(CriterionEntity criterion, Timestamp pageRequestedOn) {
    Set<ValidationError> optimisticLockErrors = new HashSet<>();
    // Check if criterion is deleted by another user
    if (criterion == null) {
      optimisticLockErrors
        .add(new ValidationError(ValidationMessages.DELETED_BY_ANOTHER_USER_ERROR_FIELD,
          ValidationMessages.CRITERION_DELETED));
    }
    // If the criterion exists, check if there are any updates by another user
    else {
      optimisticLockErrors = awardCheckForChangesByOtherUsers(criterion.getSubmission().getId(), pageRequestedOn);
    }
    return optimisticLockErrors;
  }

  /**
   * The delete criterion process.
   *
   * @param criterion the criterion
   */
  private void deleteCriterionProcess(CriterionEntity criterion) {

    LOGGER.log(Level.CONFIG, "Executing method deleteCriterionProcess, Parameters: criterion: {0}",
      criterion);

    if (criterion != null) {
      String submissionId = criterion.getSubmission().getId();
      String deletedCriterionType = criterion.getCriterionType();
      em.remove(criterion);
      if (deletedCriterionType.equals(LookupValues.EVALUATED_CRITERION_TYPE)) {
        // Retrieve all Bewertetes Kriterien of submission.
        List<CriterionEntity> evaluatedCriterionEntities =
          new JPAQueryFactory(em).select(qCriterionEntity).from(qCriterionEntity)
            .where(qCriterionEntity.submission.id.eq(submissionId)
              .and(qCriterionEntity.criterionType.eq(LookupValues.EVALUATED_CRITERION_TYPE)))
            .fetch();
        // If submission has no Bewertetes Kriterien then set offer rank to null.
        if (evaluatedCriterionEntities.isEmpty()) {
          Integer sortOrder = 1;
          List<OfferEntity> offerEntities =
            new JPAQueryFactory(em).select(qOfferEntity).from(qOfferEntity)
              .where(qOfferEntity.submittent.submissionId.id.eq(submissionId)).fetch();
          Collections.sort(offerEntities, ComparatorUtil.sortOfferEntities);
          for (OfferEntity offerEntity : offerEntities) {
            offerEntity.setqExRank(sortOrder);
            offerEntity.getSubmittent().setSortOrder(sortOrder);
            em.merge(offerEntity);
            sortOrder++;
          }
        }
      }
    }
  }

  @Override
  public List<CriterionDTO> readCriteriaOfSubmission(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method readCriteriaOfSubmission, Parameters: submissionId: {0}", submissionId);

    List<CriterionEntity> mustCriterionEntities =
      new JPAQueryFactory(em).select(qCriterionEntity).from(qCriterionEntity)
        .where(qCriterionEntity.submission.id.eq(submissionId)
          .and(qCriterionEntity.criterionType.eq(LookupValues.MUST_CRITERION_TYPE)))
        .orderBy(qCriterionEntity.criterionText.asc()).fetch();
    List<CriterionDTO> mustCriterionDTOs =
      CriterionDTOMapper.INSTANCE.toCriterionDTO(mustCriterionEntities);
    Collections.sort(mustCriterionDTOs, ComparatorUtil.criteriaWithoutWeightings);
    List<CriterionEntity> evaluatedCriterionEntities =
      new JPAQueryFactory(em).select(qCriterionEntity).from(qCriterionEntity)
        .where(qCriterionEntity.submission.id.eq(submissionId)
          .and(qCriterionEntity.criterionType.eq(LookupValues.EVALUATED_CRITERION_TYPE)))
        .fetch();
    List<CriterionDTO> evaluatedCriterionDTOs =
      CriterionDTOMapper.INSTANCE.toCriterionDTO(evaluatedCriterionEntities);
    Collections.sort(evaluatedCriterionDTOs, ComparatorUtil.criteriaWithWeightings);
    for (CriterionDTO criterionDTO : evaluatedCriterionDTOs) {
      Collections.sort(criterionDTO.getSubcriterion(), ComparatorUtil.subcriteria);
    }
    List<CriterionEntity> awardCriterionEntities =
      new JPAQueryFactory(em).select(qCriterionEntity).from(qCriterionEntity)
        .where(qCriterionEntity.submission.id.eq(submissionId)
          .and((qCriterionEntity.criterionType.eq(LookupValues.AWARD_CRITERION_TYPE)
            .or(qCriterionEntity.criterionType.eq(LookupValues.PRICE_AWARD_CRITERION_TYPE))
            .or(qCriterionEntity.criterionType
              .eq(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE)))))
        .fetch();
    CriterionEntity addedOperatingCostCriterion = new JPAQueryFactory(em).select(qCriterionEntity)
      .from(qCriterionEntity)
      .where(qCriterionEntity.submission.id.eq(submissionId).and(
        (qCriterionEntity.criterionType.eq(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE))))
      .fetchOne();
    // Check if the operating cost award criterion doesn't already exist and if the offer opening
    // has not been closed before.
    if (addedOperatingCostCriterion == null
      && !submissionService.hasOfferOpeningBeenClosedBefore(submissionId)) {
      List<OfferEntity> offerEntities =
        new JPAQueryFactory(em).select(qOfferEntity).from(qOfferEntity)
          .where(qOfferEntity.submittent.submissionId.id.eq(submissionId)).fetch();
      boolean createOperatingCostCriterion = false;
      for (OfferEntity offerEntity : offerEntities) {
        /* Check if at least one offer has non-zero operating cost gross */
        if ((offerEntity.getOperatingCostGross() != null && offerEntity.getOperatingCostGross() > 0)
          || (offerEntity.getOperatingCostGrossCorrected() != null
          && offerEntity.getOperatingCostGrossCorrected() > 0)) {
          createOperatingCostCriterion = true;
          break;
        }
      }
      /* If operating cost gross is non-zero create operating cost award criterion */
      if (createOperatingCostCriterion) {
        CriterionEntity operatingCostCriterion = new CriterionEntity();
        operatingCostCriterion.setCriterionText("Betriebskosten");
        operatingCostCriterion.setCriterionType(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE);
        operatingCostCriterion.setWeighting(0.00);
        operatingCostCriterion.setSubmission(em.find(SubmissionEntity.class, submissionId));
        em.persist(operatingCostCriterion);
        awardCriterionEntities.add(operatingCostCriterion);
        /* create empty OfferCriterionEntity */
        createOfferCriterion(operatingCostCriterion);
      }
    }
    /* Create price criterion */
    if (awardCriterionEntities.isEmpty()) {
      CriterionEntity priceCriterion = new CriterionEntity();
      priceCriterion.setCriterionText("Preis");
      priceCriterion.setCriterionType(LookupValues.PRICE_AWARD_CRITERION_TYPE);
      priceCriterion.setWeighting(100.00);
      priceCriterion.setSubmission(em.find(SubmissionEntity.class, submissionId));
      em.persist(priceCriterion);
      awardCriterionEntities.add(priceCriterion);
      /* create empty OfferCriterionEntity */
      createOfferCriterion(priceCriterion);
    } else {
      boolean flagPrice = false;
      double sum = 0;
      for (CriterionEntity criterionEntity : awardCriterionEntities) {
        sum += criterionEntity.getWeighting();
        if (criterionEntity.getCriterionType().equals(LookupValues.PRICE_AWARD_CRITERION_TYPE)) {
          flagPrice = true;
          break;
        }
      }
      if (!flagPrice) {
        CriterionEntity priceCriterion = new CriterionEntity();
        priceCriterion.setCriterionText("Preis");
        priceCriterion.setCriterionType(LookupValues.PRICE_AWARD_CRITERION_TYPE);
        priceCriterion.setWeighting(100 - sum);
        priceCriterion.setSubmission(em.find(SubmissionEntity.class, submissionId));
        em.persist(priceCriterion);
        awardCriterionEntities.add(priceCriterion);
        /* create empty OfferCriterionEntity */
        createOfferCriterion(priceCriterion);
      }
    }
    List<CriterionDTO> awardCriterionDTOs =
      CriterionDTOMapper.INSTANCE.toCriterionDTO(awardCriterionEntities);
    Collections.sort(awardCriterionDTOs, ComparatorUtil.criteriaWithWeightings);
    if (awardCriterionDTOs.size() > 1) {
      for (CriterionDTO criterionDTO : awardCriterionDTOs) {
        if (criterionDTO.getSubcriterion() != null) {
          Collections.sort(criterionDTO.getSubcriterion(), ComparatorUtil.subcriteria);
        }
      }
    }
    List<CriterionDTO> criterionDTOs = new ArrayList<>(mustCriterionDTOs);
    criterionDTOs.addAll(evaluatedCriterionDTOs);
    criterionDTOs.addAll(awardCriterionDTOs);
    return criterionDTOs;
  }

  @Override
  public void updateCriterion(ExaminationDTO examinationDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateCriterion, Parameters: examinationDTO: {0}", examinationDTO);

    if (examinationDTO.getSubmissionId() != null) {
      SubmissionEntity submissionEntity =
        em.find(SubmissionEntity.class, examinationDTO.getSubmissionId());
      // Set minimum and maximum grade.
      if (submissionEntity != null) {
        submissionEntity.setMaxGrade(examinationDTO.getMaxGrade());
        submissionEntity.setMinGrade(examinationDTO.getMinGrade());
      }
    }
    if (!examinationDTO.getCriterion().isEmpty()) {
      List<OfferEntity> offerEntities = new JPAQueryFactory(em).select(qOfferEntity)
        .from(qOfferEntity).where(qOfferEntity.submittent.submissionId.id
          .eq(examinationDTO.getSubmissionId()).and(qOfferEntity.isEmptyOffer.isFalse()))
        .fetch();
      for (CriterionDTO criterionDTO : examinationDTO.getCriterion()) {
        CriterionEntity criterionEntity = em.find(CriterionEntity.class, criterionDTO.getId());
        if (criterionEntity != null) {
          // Set criterion text.
          if (criterionDTO.getCriterionText() != null) {
            criterionEntity.setCriterionText(criterionDTO.getCriterionText());
          }
          // Set criterion weighting.
          if (criterionDTO.getWeighting() != null) {
            criterionEntity.setWeighting(criterionDTO.getWeighting());
          }
          em.merge(criterionEntity);
        }
        if (criterionDTO.getSubcriterion() != null) {
          for (SubcriterionDTO subcriterionDTO : criterionDTO.getSubcriterion()) {
            SubcriterionEntity subcriterionEntity =
              em.find(SubcriterionEntity.class, subcriterionDTO.getId());
            if (subcriterionEntity != null) {
              // Set subcriterion text.
              if (criterionDTO.getCriterionText() != null) {
                subcriterionEntity.setSubcriterionText(subcriterionDTO.getSubcriterionText());
              }
              // Set subcriterion weighting.
              if (criterionDTO.getWeighting() != null) {
                subcriterionEntity.setWeighting(subcriterionDTO.getWeighting());
              }
              em.merge(subcriterionEntity);
            }
          }
        }
      }
      // Calculate grade and score of offer criteria and subcriteria (updates Ubersicht table).
      for (OfferEntity offerEntity : offerEntities) {
        double totalScore = 0;
        for (OfferCriterionEntity offerCriterionEntity : offerEntity.getOfferCriteria()) {
          if (offerCriterionEntity.getCriterion().getCriterionType() != null && offerCriterionEntity
            .getCriterion().getCriterionType().equals(LookupValues.EVALUATED_CRITERION_TYPE)) {
            // Calculate criterion score if there are no subcriteria.
            if (offerCriterionEntity.getCriterion().getSubcriteria().isEmpty()
              && offerCriterionEntity.getGrade() != null) {
              offerCriterionEntity
                .setScore(BigDecimal.valueOf(offerCriterionEntity.getGrade().doubleValue()
                  * offerCriterionEntity.getCriterion().getWeighting().doubleValue() / 100));
            }
            // Calculate criterion score in case of subcriteria.
            else if (!offerCriterionEntity.getCriterion().getSubcriteria().isEmpty()) {
              double subcriterionScore = 0;
              double offerCriterionGrade = 0;
              for (OfferSubcriterionEntity offerSubcriterionEntity : offerEntity
                .getOfferSubcriteria()) {
                if (offerSubcriterionEntity.getSubcriterion().getCriterion().getId()
                  .equals(offerCriterionEntity.getCriterion().getId())
                  && offerSubcriterionEntity.getGrade() != null) {
                  // Calculate offer subcriterion score.
                  subcriterionScore = offerSubcriterionEntity.getGrade().doubleValue()
                    * offerSubcriterionEntity.getSubcriterion().getWeighting() / 100;
                  offerSubcriterionEntity.setScore(BigDecimal.valueOf(subcriterionScore));
                  em.merge(offerSubcriterionEntity);
                  offerCriterionGrade += subcriterionScore;
                }
              }
              // Update offer criterion grade in case of subcriteria.
              offerCriterionEntity.setGrade(BigDecimal.valueOf(offerCriterionGrade));
              // Update offer criterion score in case of subcriteria.
              offerCriterionEntity.setScore(BigDecimal.valueOf(
                offerCriterionGrade * offerCriterionEntity.getCriterion().getWeighting() / 100));
            }
            // Add offer criterion score to total score.
            if (offerCriterionEntity.getScore() != null) {
              totalScore += offerCriterionEntity.getScore().doubleValue();
            }
            em.merge(offerCriterionEntity);
          }
        }
        offerEntity.setqExTotalGrade(BigDecimal.valueOf(totalScore));
        em.merge(offerEntity);
      }
    }
  }

  @Override
  public boolean updateAwardCriterion(AwardDTO awardDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateAwardCriterion, Parameters: awardDTO: {0}", awardDTO);

    /*
     * iterate the offers of the submission to find the lowest price and operatingCost, because they
     * are needed for the calculation of the price and operating cost criterion grade and score if
     * at least one offer has zero betrag the update will fail (and the method will return true)
     */
    BigDecimal minPrice = null;
    BigDecimal minOperatingCost = null;
    List<OfferEntity> offerEntities = new JPAQueryFactory(em).select(qOfferEntity)
      .from(qOfferEntity).where(qOfferEntity.submittent.submissionId.id
        .eq(awardDTO.getSubmissionId()).and(qOfferEntity.isEmptyOffer.isFalse()))
      .fetch();
    // Retrieving submission process.
    Process process =
      new JPAQueryFactory(em).select(qSubmissionEntity.process).from(qSubmissionEntity)
        .where(qSubmissionEntity.id.eq(awardDTO.getSubmissionId())).fetchOne();
    if (awardDTO.getSubmissionId() != null) {
      SubmissionEntity submissionEntity = null;
      for (OfferEntity offer : offerEntities) {
        /*
         * if at least one offer has zero Betrag, then no calculation can be made, so exit and
         * inform for the error
         */
        if (offer.getAmount() != null && offer.getAmount().doubleValue() == 0
          && (offer.getExcludedFirstLevel() == null
          || offer.getExcludedFirstLevel().equals(Boolean.FALSE))) {
          return true;
        }
        if (offer.getAmount() != null
          && (offer.getIsExcludedFromProcess() != null
          && offer.getIsExcludedFromProcess().equals(Boolean.FALSE))
          && (minPrice == null || offer.getAmount().doubleValue() < minPrice.doubleValue())) {
          /*
           * set the value to the variable if it is the first one found or if it is less than the
           * one found
           */
          minPrice = offer.getAmount();
        }
        if (offer.getOperatingCostsAmount() != null
          && (offer.getIsExcludedFromProcess() != null
          && offer.getIsExcludedFromProcess().equals(Boolean.FALSE))
          && offer.getOperatingCostsAmount().compareTo(BigDecimal.ZERO) != 0
          && (minOperatingCost == null
          || offer.getOperatingCostsAmount().compareTo(minOperatingCost) < 0)) {
          /*
           * set the value to the variable if it is not 0 and if it is the first one found or if it
           * is less than the one found
           */
          minOperatingCost = offer.getOperatingCostsAmount();
        }
        submissionEntity = offer.getSubmittent().getSubmissionId();
      }

      if (submissionEntity != null) {
        if (awardDTO.getAwardMaxGrade() != null) {
          submissionEntity.setAwardMaxGrade(awardDTO.getAwardMaxGrade());
        }
        if (awardDTO.getAwardMinGrade() != null) {
          submissionEntity.setAwardMinGrade(awardDTO.getAwardMinGrade());
        }

        if (awardDTO.getCustomOperatingCostFormula() != null) {
          submissionEntity.setCustomOperatingCostFormula(awardDTO.getCustomOperatingCostFormula());
          // custom formula exists, set other formula to null
          submissionEntity.setOperatingCostFormula(null);
        } else if (awardDTO.getOperatingCostFormula() != null
          && awardDTO.getOperatingCostFormula().getMasterListValueId() != null) {
          submissionEntity.setOperatingCostFormula(MasterListValueMapper.INSTANCE
            .toMasterListValue(awardDTO.getOperatingCostFormula().getMasterListValueId()));
          // formula exists, set custom formula to null
          submissionEntity.setCustomOperatingCostFormula(null);
        }
        if (awardDTO.getCustomPriceFormula() != null) {
          submissionEntity.setCustomPriceFormula(awardDTO.getCustomPriceFormula());
          // custom formula exists, set other formula to null
          submissionEntity.setPriceFormula(null);
        } else if (awardDTO.getPriceFormula() != null
          && awardDTO.getPriceFormula().getMasterListValueId() != null) {
          submissionEntity.setPriceFormula(MasterListValueMapper.INSTANCE
            .toMasterListValue(awardDTO.getPriceFormula().getMasterListValueId()));
          // formula exists, set custom formula to null
          submissionEntity.setCustomPriceFormula(null);
        }
        if (awardDTO.getAddedAwardRecipients() != null) {
          submissionEntity.setAddedAwardRecipients(awardDTO.getAddedAwardRecipients());
        }
        if (awardDTO.getEvaluationThrough() != null) {
          submissionEntity.setEvaluationThrough(awardDTO.getEvaluationThrough());
        }
      }
    }

    if (!awardDTO.getCriterion().isEmpty()) {
      List<CriterionEntity> awardCriterionEntities = new JPAQueryFactory(em)
        .select(qCriterionEntity).from(qCriterionEntity)
        .where(qCriterionEntity.submission.id.eq(awardDTO.getSubmissionId())
          .and((qCriterionEntity.criterionType.eq(LookupValues.AWARD_CRITERION_TYPE)
            .or(qCriterionEntity.criterionType.eq(LookupValues.PRICE_AWARD_CRITERION_TYPE))
            .or(qCriterionEntity.criterionType
              .eq(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE)))))
        .fetch();
      double sum = 0;
      CriterionEntity priceCriterionEntity = null;
      for (CriterionDTO c : awardDTO.getCriterion()) {
        // find the criterion entity that matches the criterion
        CriterionEntity criterionEntity = null;
        for (CriterionEntity criterionIt : awardCriterionEntities) {
          if (criterionIt.getId().equals(c.getId())) {
            criterionEntity = criterionIt;
            break;
          }
        }
        if (criterionEntity != null) {
          if (c.getCriterionText() != null) {
            criterionEntity.setCriterionText(c.getCriterionText());
          }
          if ((c.getWeighting() != null)
            && (c.getCriterionType().equals(LookupValues.AWARD_CRITERION_TYPE) || c
            .getCriterionType().equals(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE))) {
            criterionEntity.setWeighting(c.getWeighting());
            sum += c.getWeighting();
            em.merge(criterionEntity);
          } else if (c.getCriterionType().equals(LookupValues.PRICE_AWARD_CRITERION_TYPE)) {
            // keep the price criterion in order to update its weighting afterwards as rest of
            // weightings
            priceCriterionEntity = criterionEntity;
          }
        }
        if (c.getSubcriterion() != null) {
          for (SubcriterionDTO s : c.getSubcriterion()) {
            // find the subcriterion entity that matches the subcriterion
            SubcriterionEntity subcriterionEntity = null;
            if (criterionEntity != null && criterionEntity.getSubcriteria() != null) {
              for (SubcriterionEntity subcriterionIt : criterionEntity.getSubcriteria()) {
                if (subcriterionIt.getId().equals(s.getId())) {
                  subcriterionEntity = subcriterionIt;
                  break;
                }
              }
            }
            if (subcriterionEntity != null) {
              if (c.getCriterionText() != null) {
                subcriterionEntity.setSubcriterionText(s.getSubcriterionText());
              }
              if (c.getWeighting() != null) {
                subcriterionEntity.setWeighting(s.getWeighting());
              }
              em.merge(subcriterionEntity);
            }
          }
        }
      }

      // update the weighting of the price criterion as the rest of weightings
      priceCriterionEntity.setWeighting(100 - sum);
      em.merge(priceCriterionEntity);

      // Set status to award evaluation started if condition is fulfilled.
      TenderStatus submissionStatus = TenderStatus
        .fromValue(submissionService.getCurrentStatusOfSubmission(awardDTO.getSubmissionId()));
      if (!compareCurrentVsSpecificStatus(submissionStatus, TenderStatus.AWARD_EVALUATION_STARTED)
        && !submissionStatus.equals(TenderStatus.OFFER_OPENING_STARTED)
        && !(process.equals(Process.SELECTIVE)
        && (submissionStatus.equals(TenderStatus.FORMAL_EXAMINATION_STARTED)
        || submissionStatus.equals(TenderStatus.OFFER_OPENING_CLOSED)))) {
        submissionService.updateSubmissionStatus(awardDTO.getSubmissionId(),
          TenderStatus.AWARD_EVALUATION_STARTED.getValue(), null, null,
          LookupValues.INTERNAL_LOG);
      }

      /*
       * calculate price and operating cost criterion grade and score according to the given
       * formulas
       *
       */
      String priceFormula =
        (awardDTO.getCustomPriceFormula() != null) ? awardDTO.getCustomPriceFormula()
          : awardDTO.getPriceFormula().getValue2();
      String operatingCostFormula = (awardDTO.getCustomOperatingCostFormula() != null)
        ? awardDTO.getCustomOperatingCostFormula()
        : awardDTO.getOperatingCostFormula().getValue2();

      /*
       * iterate the offers of the submission again 1) to set the grade of the price and operating
       * cost criterion according to the calculation formulas and 2) in order to update all offer
       * criterion grade and scores according to the updated criterion weightings
       */
      BigDecimal score;

      for (OfferEntity offer : offerEntities) {
        /*
         * first iterate the offer subcriteria 1) calculate the offer subcriterion score and 2)
         * calculate the grade of the offer criterion the subcriterion belongs to as the sums of the
         * subcriterions scores. Before this we need to instantiate all offer criteria grades to
         * null when having subcriteria. We are interested only in award criterion type, price
         * criterion type and operating cost criterion type
         */
        for (OfferCriterionEntity offerCriterion : offer.getOfferCriteria()) {
          if ((offerCriterion.getCriterion().getCriterionType()
            .equals(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE)
            || offerCriterion.getCriterion().getCriterionType()
            .equals(LookupValues.AWARD_CRITERION_TYPE)
            || offerCriterion.getCriterion().getCriterionType()
            .equals(LookupValues.PRICE_AWARD_CRITERION_TYPE))
            && offerCriterion.getCriterion().getSubcriteria() != null
            && !offerCriterion.getCriterion().getSubcriteria().isEmpty()) {
            offerCriterion.setGrade(null);
          }
        }
        // there is one specific scenario, when under ZB the  Undercriteria are not 100% this getter -> offer.getOfferSubcriteria() returns an empty Subcriteria Entity
        // that's the call is necessary
        List<OfferSubcriterionEntity> offerSubcriteria =  getSubCriteriaByOfferId(offer.getId());
        for (OfferSubcriterionEntity offerSubcriterion : offerSubcriteria) {
          if (offerSubcriterion.getSubcriterion().getCriterion().getCriterionType()
            .equals(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE)
            || offerSubcriterion.getSubcriterion().getCriterion().getCriterionType()
            .equals(LookupValues.AWARD_CRITERION_TYPE)
            || offerSubcriterion.getSubcriterion().getCriterion().getCriterionType()
            .equals(LookupValues.PRICE_AWARD_CRITERION_TYPE)) {
            score = calculateScore(offerSubcriterion.getGrade(),
              offerSubcriterion.getSubcriterion().getWeighting());
            offerSubcriterion.setScore(score);
            if (score != null) {
              /*
               * iterate offerCriteria to find the offer criterion the subcriterion belongs to and
               * add the subcriterion score to the criterions grade
               */
              for (OfferCriterionEntity offerCriterion : offer.getOfferCriteria()) {
                if (offerCriterion.getCriterion().getSubcriteria()
                  .contains(offerSubcriterion.getSubcriterion())) {
                  if (offerCriterion.getGrade() == null) {
                    offerCriterion.setGrade(offerSubcriterion.getScore());
                  } else {
                    offerCriterion
                      .setGrade(BigDecimal.valueOf(offerCriterion.getGrade().doubleValue()
                        + offerSubcriterion.getScore().doubleValue()));
                  }
                }
              }
            }
          }
        }

        double sumTotalScore = 0;
        /* then iterate offer criteria */
        List<OfferCriterionEntity> offerCriteria = offer.getOfferCriteria();
        for (OfferCriterionEntity offerCriterion : offerCriteria) {
          if (offerCriterion.getCriterion().getCriterionType()
            .equals(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE)
            || offerCriterion.getCriterion().getCriterionType()
            .equals(LookupValues.AWARD_CRITERION_TYPE)
            || offerCriterion.getCriterion().getCriterionType()
            .equals(LookupValues.PRICE_AWARD_CRITERION_TYPE)) {

            /*
             * set the grade of the price and operating cost criterion according to the calculation
             * formulas
             */
            if (offerCriterion.getCriterion().getCriterionType()
              .equals(LookupValues.PRICE_AWARD_CRITERION_TYPE) && offer.getAmount() != null
              && minPrice != null && priceFormula != null) {
              offerCriterion
                .setGrade(calculateGrade(offer.getAmount(), minPrice, priceFormula));
            }
            if (offerCriterion.getCriterion().getCriterionType()
              .equals(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE)) {
              if (offer.getOperatingCostsAmount() != null
                && offer.getOperatingCostsAmount().compareTo(BigDecimal.ZERO) != 0
                && minOperatingCost != null && operatingCostFormula != null) {
                offerCriterion.setGrade(calculateGrade(offer.getOperatingCostsAmount(),
                  minOperatingCost, operatingCostFormula));
              } else {
                offerCriterion.setGrade(null);
              }
            }
            /*
             * update all offer criterion grade and scores according to the updated criterion
             * weightings
             */
            score = calculateScore(offerCriterion.getGrade(),
              offerCriterion.getCriterion().getWeighting());

            if (score != null) {
              sumTotalScore = sumTotalScore + score.doubleValue();
            }
            offerCriterion.setScore(score);
            em.merge(offerCriterion);
          }
        }
        offer.setAwardTotalScore(BigDecimal.valueOf(sumTotalScore));
      }

      rankOffers(offerEntities, submissionStatus);
    }
    return false;
  }

  @Override
  public String addSubcriterionToCriterion(SubcriterionDTO subcriterion) {

    LOGGER.log(Level.CONFIG,
      "Executing method addSubcriterionToCriterion, Parameters: subcriterion: {0}",
      subcriterion);

    SubcriterionEntity subcriterionEntity =
      SubcriterionDTOMapper.INSTANCE.toSubcriterion(subcriterion);
    CriterionEntity criterionEntity = em.find(CriterionEntity.class, subcriterion.getCriterion());

    subcriterionEntity.setCriterion(criterionEntity);
    em.persist(subcriterionEntity);

    /* UC(039) Create empty OfferCriterionEntity for each offer of the specific submission. */
    List<OfferEntity> offerEntities =
      new JPAQueryFactory(em).select(qOfferEntity).from(qOfferEntity)
        .where(
          qOfferEntity.submittent.submissionId.id.eq(criterionEntity.getSubmission().getId()))
        .fetch();
    for (OfferEntity offer : offerEntities) {
      OfferSubcriterionEntity offerCriterionEntity = new OfferSubcriterionEntity();
      offerCriterionEntity.setOffer(offer);
      offerCriterionEntity.setSubriterion(subcriterionEntity);
      em.persist(offerCriterionEntity);
    }
    return subcriterionEntity.getId();
  }

  @Override
  public Set<ValidationError> deleteSubcriterion(String id, Timestamp pageRequestedOn) {

    LOGGER.log(Level.CONFIG,
      "Executing method deleteSubcriterion, Parameters: id: {0}"
        + "pageRequestedOn: {1}",
      new Object[]{id, pageRequestedOn});

    SubcriterionEntity subcriterion = em.find(SubcriterionEntity.class, id);
    Set<ValidationError> optimisticLockErrors = deleteSubCriterionOptimisticLock(subcriterion, pageRequestedOn);
    if (optimisticLockErrors.isEmpty()) {
      em.remove(subcriterion);
    }
    return optimisticLockErrors;
  }

  /**
   * Checking for Optimistic Lock errors.
   *
   * @param subCriterion the subCriterion
   * @param pageRequestedOn the pageRequestedOn
   * @return the error
   */
  private Set<ValidationError> deleteSubCriterionOptimisticLock(SubcriterionEntity subCriterion, Timestamp pageRequestedOn) {
    Set<ValidationError> optimisticLockErrors = new HashSet<>();
    // Check if subCriterion is deleted by another user
    if (subCriterion == null) {
      optimisticLockErrors
        .add(new ValidationError(ValidationMessages.DELETED_BY_ANOTHER_USER_ERROR_FIELD,
          ValidationMessages.SUBCRITERION_DELETED));
    }
    // If the subCriterion exists, check if there are any updates by another user
    else {
      optimisticLockErrors = awardCheckForChangesByOtherUsers(subCriterion.getCriterion().getSubmission().getId(), pageRequestedOn);
    }
    return optimisticLockErrors;
  }

  @Override
  public List<SubcriterionDTO> readSubcriteriaOfCriterion(String criterionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method readSubcriteriaOfCriterion, Parameters: criterionId: {0}",
      criterionId);

    List<SubcriterionEntity> subcriterionEntities =
      new JPAQueryFactory(em).select(qSubcriterionEntity).from(qSubcriterionEntity)
        .where(qSubcriterionEntity.criterion.id.eq(criterionId)).fetch();
    List<SubcriterionDTO> subcriterionDTOs =
      SubcriterionDTOMapper.INSTANCE.toSubcriterionDTO(subcriterionEntities);
    Collections.sort(subcriterionDTOs, ComparatorUtil.subcriteria);

    return subcriterionDTOs;
  }

  @Override
  public boolean checkEvaluatedCriteriaWeightingLimit(String submissionId, Double weighting) {

    LOGGER.log(Level.CONFIG,
      "Executing method checkEvaluatedCriteriaWeightingLimit, Parameters: submissionId: {0}"
        + WEIGHTING,
      new Object[]{submissionId, weighting});

    List<CriterionEntity> criterionEntityList =
      new JPAQueryFactory(em).select(qCriterionEntity).from(qCriterionEntity)
        .where(qCriterionEntity.submission.id.eq(submissionId)
          .and(qCriterionEntity.criterionType.eq(LookupValues.EVALUATED_CRITERION_TYPE)))
        .fetch();
    double sum = 0;
    for (CriterionEntity criterionEntity : criterionEntityList) {
      sum += criterionEntity.getWeighting();
    }
    return sum + weighting <= 100;
  }

  @Override
  public boolean checkAwardCriteriaWeightingLimit(String submissionId, Double weighting) {

    LOGGER.log(Level.CONFIG,
      "Executing method checkAwardCriteriaWeightingLimit, Parameters: submissionId: {0}"
        + WEIGHTING,
      new Object[]{submissionId, weighting});

    List<CriterionEntity> criterionEntityList =
      new JPAQueryFactory(em)
        .select(qCriterionEntity).from(
        qCriterionEntity)
        .where(qCriterionEntity.submission.id.eq(submissionId)
          .and((qCriterionEntity.criterionType.eq(LookupValues.AWARD_CRITERION_TYPE)
            .or(qCriterionEntity.criterionType
              .eq(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE)))))
        .fetch();
    double sum = 0;
    for (CriterionEntity criterionEntity : criterionEntityList) {
      sum += criterionEntity.getWeighting();
    }
    return sum + weighting <= 99;
  }

  @Override
  public boolean checkSubcriteriaWeightingLimit(String criterionId, Double weighting) {

    LOGGER.log(Level.CONFIG,
      "Executing method checkSubcriteriaWeightingLimit, Parameters: criterionId: {0}"
        + WEIGHTING,
      new Object[]{criterionId, weighting});

    List<SubcriterionEntity> subcriterionEntityList =
      new JPAQueryFactory(em).select(qSubcriterionEntity).from(qSubcriterionEntity)
        .where(qSubcriterionEntity.criterion.id.eq(criterionId)).fetch();
    double sum = 0;
    for (SubcriterionEntity subcriterionEntity : subcriterionEntityList) {
      sum += subcriterionEntity.getWeighting();
    }
    return sum + weighting <= 100;
  }

  @Override
  public int checkExaminationCriterionWeightingLimit(List<CriterionDTO> criterionDTOs) {

    LOGGER.log(Level.CONFIG, "Executing method checkExaminationCriterionWeightingLimit");

    int returnedValue = 0;
    double criterionSum = 0;
    boolean criterion = true;
    boolean subcriterion = true;
    for (CriterionDTO criterionDTO : criterionDTOs) {
      if (criterionDTO.getWeighting() != null && criterion
        && criterionDTO.getCriterionType().equals(LookupValues.EVALUATED_CRITERION_TYPE)) {
        criterionSum += criterionDTO.getWeighting();
        if (criterionSum > 100) {
          criterion = false;
        }
      }
      subcriterion = hasReachedWeightingLimit(criterionDTO.getSubcriterion(), subcriterion);
      returnedValue = updateCheckWeightingLimit(criterion, subcriterion);
    }
    return returnedValue;
  }

  @Override
  public int checkAwardCriterionWeightingLimit(List<CriterionDTO> criterionDTOs) {

    LOGGER.log(Level.CONFIG, "Executing method checkAwardCriterionWeightingLimit");

    int returnedValue = 0;
    double criterionSum = 0;
    boolean criterion = true;
    boolean subcriterion = true;
    for (CriterionDTO criterionDTO : criterionDTOs) {
      if (criterionDTO.getWeighting() != null && criterion
        && (criterionDTO.getCriterionType().equals(LookupValues.AWARD_CRITERION_TYPE)
        || criterionDTO.getCriterionType()
        .equals(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE))) {
        criterionSum += criterionDTO.getWeighting();
        if (criterionSum > 99) {
          criterion = false;
        }
      }
      subcriterion = hasReachedWeightingLimit(criterionDTO.getSubcriterion(), subcriterion);
      returnedValue = updateCheckWeightingLimit(criterion, subcriterion);
    }
    return returnedValue;
  }

  /**
   * Updates the value of the returnedValue.
   *
   * @param criterion the criterion
   * @param subcriterion the subcriterion
   * @return returnedValue: Depending on the given booleans
   */
  private int updateCheckWeightingLimit(boolean criterion, boolean subcriterion) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateCheckWeightingLimit, Parameters: criterion: {0}"
        + ", subcriterion: {1}",
      new Object[]{criterion, subcriterion});

    int returnedValue = 0;
    /* If subcriterias' weighting is greater than limit. */
    if (criterion && !subcriterion) {
      returnedValue = 1;
    }
    /* If criterias' weighting is greater than limit. */
    if (!criterion && subcriterion) {
      returnedValue = 2;
    }
    /* If criterias' weighting and subcriterias' weighting are greater than their limits. */
    if (!criterion && !subcriterion) {
      returnedValue = 3;
    }
    return returnedValue;
  }

  /**
   * Calculates the weighting of subcriteria.
   *
   * @param subcriterionDTOs the subcriterion DT os
   * @param subcriterion the subcriterion
   * @return true, if successful
   */
  private boolean hasReachedWeightingLimit(List<SubcriterionDTO> subcriterionDTOs,
    boolean subcriterion) {

    LOGGER.log(Level.CONFIG,
      "Executing method hasReachedWeightingLimit, Parameters: subcriterion: {0}", subcriterion);

    double subcriterionSum = 0;
    for (SubcriterionDTO subcriterionDTO : subcriterionDTOs) {
      if (subcriterion) {
        if (subcriterionDTO.getWeighting() != null) {
          subcriterionSum += subcriterionDTO.getWeighting();
        }
        if (subcriterionSum > 100) {
          subcriterion = false;
        }
      }
    }
    return subcriterion;
  }

  @Override
  public boolean canOperatingCostAwardCriterionBeAdded(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method canOperatingCostAwardCriterionBeAdded, Parameters: submissionId: {0}",
      submissionId);

    List<OfferEntity> offerEntities = new JPAQueryFactory(em).select(qOfferEntity)
      .from(qOfferEntity).where(qOfferEntity.submittent.submissionId.id.eq(submissionId)).fetch();
    for (OfferEntity offerEntity : offerEntities) {
      if ((offerEntity.getOperatingCostGross() != null && offerEntity.getOperatingCostGross() > 0)
        || (offerEntity.getOperatingCostGrossCorrected() != null
        && offerEntity.getOperatingCostGrossCorrected() > 0)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public int calculateActiveSubmittentsOfSubmission(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method calculateActiveSubmittentsOfSubmission, Parameters: submissionId: {0}",
      submissionId);

    Process process = em.find(SubmissionEntity.class, submissionId).getProcess();
    List<OfferEntity> offerEntities = null;
    if (process != Process.SELECTIVE) {
      offerEntities = new JPAQueryFactory(em).select(qOfferEntity).from(qOfferEntity)
        .where(qOfferEntity.submittent.submissionId.id.eq(submissionId)
          .and(qOfferEntity.isExcludedFromProcess.eq(Boolean.FALSE))
          .and(qOfferEntity.isEmptyOffer.eq(Boolean.FALSE)))
        .fetch();
    } else {
      offerEntities = new JPAQueryFactory(em).select(qOfferEntity).from(qOfferEntity)
        .where(qOfferEntity.submittent.submissionId.id.eq(submissionId)
          .and(qOfferEntity.submittent.isApplicant.isNull()
            .or(qOfferEntity.submittent.isApplicant.isTrue()
              .and(qOfferEntity.excludedFirstLevel.isFalse())))
          .and(qOfferEntity.isExcludedFromProcess.eq(Boolean.FALSE))
          .and(qOfferEntity.isEmptyOffer.eq(Boolean.FALSE)))
        .fetch();
    }
    return offerEntities.size();
  }

  @Override
  public List<OfferDTO> getOfferCriteria(String submissionId, String type) {

    LOGGER.log(Level.CONFIG, "Executing method getOfferCriteria, Parameters: submissionId: {0}"
        + ", type: {1}",
      new Object[]{submissionId, type});

    List<OfferEntity> offerEntities = null;
    List<OfferDTO> offerDTOs = null;
    // Get process type of submission.
    SubmissionEntity submissionEntity = new JPAQueryFactory(em).select(qSubmissionEntity)
      .from(qSubmissionEntity).where(qSubmissionEntity.id.eq(submissionId)).fetchFirst();
    if (type.equals(LookupValues.CRITERION_TYPE.SUITABILITY.getValue())) {
      // Initialize the values "existsExclusionReasons" and "formalExaminationFulfilled" for each
      // submittent by calling the getSubmittentsBySubmission function. In case of selective process
      // only call function if status SUITABILITY_AUDIT_COMPLETED_S is not set yet.
      if (!submissionEntity.getProcess().equals(Process.SELECTIVE)
        || (submissionEntity.getProcess().equals(Process.SELECTIVE)
        && !compareCurrentVsSpecificStatus(
        TenderStatus.fromValue(submissionService.getCurrentStatusOfSubmission(submissionId)),
        TenderStatus.SUITABILITY_AUDIT_COMPLETED_S))) {
        if (!submissionEntity.getProcess().equals(Process.SELECTIVE)) {
          List<SubmittentEntity> submittentEntities = SubmittentDTOMapper.INSTANCE
            .toSubmittent(submissionService.getSubmittentsBySubmission(submissionId));
          for (SubmittentEntity submittentEntity : submittentEntities) {
            em.merge(submittentEntity);
          }
        } else {
          List<SubmittentEntity> submittentEntities = SubmittentDTOMapper.INSTANCE
            .toSubmittent(submissionService.getApplicantsForFormalAudit(submissionId));
          // In case of selective process save the values in formalAudit instead of submittent.
          List<FormalAuditEntity> formalAuditEntities =
            new JPAQueryFactory(em).selectFrom(qFormalAuditEntity)
              .where(qFormalAuditEntity.submittent.submissionId.id.eq(submissionId)).fetch();
          if (formalAuditEntities == null || formalAuditEntities.isEmpty()) {
            // If there are no formal audit entities, create a formal audit entity for every
            // submittent.
            for (SubmittentEntity submittentEntity : submittentEntities) {
              createFormalAuditEntity(submittentEntity);
            }
          } else if (formalAuditEntities.size() != submittentEntities.size()) {
            // If the formal audit entities do not equal the submittent entities, that means there
            // are submittents without a formal audit, which needs to be created.
            for (SubmittentEntity submittentEntity : submittentEntities) {
              boolean formalAuditFound = false;
              for (FormalAuditEntity formalAuditEntity : formalAuditEntities) {
                // Check if a formal audit entity exists for the current submittent.
                if (formalAuditEntity.getSubmittent().getId().equals(submittentEntity.getId())) {
                  formalAuditFound = true;
                  break;
                }
              }
              // If there is no formal audit entity for the current submittent, create one.
              if (!formalAuditFound) {
                createFormalAuditEntity(submittentEntity);
              }
            }
          }
        }
      }
      if (submissionEntity.getProcess() == Process.SELECTIVE) {
        // Get offers where submittent is set from the start or submittent is set as not excluded
        // applicant.
        offerEntities = new JPAQueryFactory(em).selectDistinct(qOfferEntity).from(qOfferEntity)
          .leftJoin(qOfferEntity.offerCriteria, qOfferCriterionEntity)
          .leftJoin(qOfferCriterionEntity.criterion, qCriterionEntity)
          .on(qCriterionEntity.criterionType.in(LookupValues.MUST_CRITERION_TYPE,
            LookupValues.EVALUATED_CRITERION_TYPE))
          .where(qOfferEntity.submittent.submissionId.id.eq(submissionId)
            .and(qOfferEntity.submittent.isApplicant.isTrue())
            .and(qOfferEntity.isEmptyOffer.isFalse().or(qOfferEntity.isEmptyOffer.isNull())))
          .fetch();
      } else {
        offerEntities = new JPAQueryFactory(em).selectDistinct(qOfferEntity).from(qOfferEntity)
          .leftJoin(qOfferEntity.offerCriteria, qOfferCriterionEntity)
          .leftJoin(qOfferCriterionEntity.criterion, qCriterionEntity)
          .on(qCriterionEntity.criterionType.in(LookupValues.MUST_CRITERION_TYPE,
            LookupValues.EVALUATED_CRITERION_TYPE))
          .where(qOfferEntity.submittent.submissionId.id.eq(submissionId)
            .and(qOfferEntity.isEmptyOffer.isFalse()))
          .fetch();
      }
      Collections.sort(offerEntities, ComparatorUtil.sortOfferEntities);
      offerDTOs = OfferDTOWithCriteriaMapper.INSTANCE.toOfferDTO(offerEntities);
      if (submissionEntity.getProcess() == Process.SELECTIVE) {
        // If process is selective, retrieve formal audit values from formal audit entity instead of
        // submittent.
        List<FormalAuditEntity> formalAuditEntities =
          new JPAQueryFactory(em).selectFrom(qFormalAuditEntity)
            .where(qFormalAuditEntity.submittent.submissionId.id.eq(submissionId)).fetch();
        for (OfferDTO offerDTO : offerDTOs) {
          for (FormalAuditEntity formalAuditEntity : formalAuditEntities) {
            if (formalAuditEntity.getSubmittent().getId()
              .equals(offerDTO.getSubmittent().getId())) {
              offerDTO.getSubmittent()
                .setExistsExclusionReasons(formalAuditEntity.getExistsExclusionReasons());
              offerDTO.getSubmittent()
                .setFormalExaminationFulfilled(formalAuditEntity.getFormalExaminationFulfilled());
              break;
            }
          }
        }
      }
      for (OfferDTO offer : offerDTOs) {
        Collections.sort(offer.getOfferCriteria(), ComparatorUtil.offerCriteriaWithoutWeightings);
        Collections.sort(offer.getOfferCriteria(), ComparatorUtil.offerCriteriaWithWeightings);
        for (OfferCriterionDTO offerCriterion : offer.getOfferCriteria()) {
          Collections.sort(offerCriterion.getCriterion().getSubcriterion(),
            ComparatorUtil.subcriteria);
        }
      }
    }
    if (type.equals(LookupValues.CRITERION_TYPE.AWARD.getValue())) {
      if (submissionEntity.getProcess() == Process.SELECTIVE && compareCurrentVsSpecificStatus(
        TenderStatus.fromValue(submissionEntity.getStatus()),
        TenderStatus.SUITABILITY_AUDIT_COMPLETED_S)) {
        // Get offers where submittent is set from the start or submittent is set as not excluded
        // applicant.
        offerEntities = new JPAQueryFactory(em).select(qOfferEntity).from(qOfferEntity)
          .where(qOfferEntity.submittent.submissionId.id.eq(submissionId)
            .and((qOfferEntity.offerCriteria.any().criterion.criterionType
              .eq(LookupValues.AWARD_CRITERION_TYPE)
              .or(qOfferEntity.offerCriteria.any().criterion.criterionType
                .eq(LookupValues.PRICE_AWARD_CRITERION_TYPE))
              .or(qOfferEntity.offerCriteria.any().criterion.criterionType
                .eq(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE))))
            .and(qOfferEntity.submittent.isApplicant.isNull()
              .or(qOfferEntity.submittent.isApplicant.isTrue()
                .and(qOfferEntity.excludedFirstLevel.isFalse())))
            .and(qOfferEntity.isEmptyOffer.isFalse()))
          .fetch();
      } else {
        offerEntities = new JPAQueryFactory(em).select(qOfferEntity).from(qOfferEntity)
          .where(qOfferEntity.submittent.submissionId.id.eq(submissionId)
            .and(qOfferEntity.submittent.isApplicant.isNull())
            .and((qOfferEntity.offerCriteria.any().criterion.criterionType
              .eq(LookupValues.AWARD_CRITERION_TYPE)
              .or(qOfferEntity.offerCriteria.any().criterion.criterionType
                .eq(LookupValues.PRICE_AWARD_CRITERION_TYPE))
              .or(qOfferEntity.offerCriteria.any().criterion.criterionType
                .eq(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE))))
            .and(qOfferEntity.isEmptyOffer.isFalse()))
          .fetch();
      }

      /*
       * if the status of the submission is after "Eignungsprüfung abgeschlossen" or
       * "Eignungsprüfung abgeschlossen & Zuschlagsbewertung gestartet" then the excluded offers
       * must be displayed last in the list sorted in alphabetical order and grayed out
       */
      int currentStatus =
        (new Integer(submissionEntity.getStatus())).intValue();
      int suitabilityCompletedStatus =
        (new Integer(TenderStatus.SUITABILITY_AUDIT_COMPLETED.getValue())).intValue();
      if (currentStatus >= suitabilityCompletedStatus) {
        offerDTOs = OfferDTOWithCriteriaMapper.INSTANCE.toOfferDTO(offerEntities);
        for (OfferDTO offer : offerDTOs) {
          /* inform a flag in OfferDTO, so that the excluded offers are displayed grayed out */
          if (offer.getSubmittent().getSubmissionId().getProcess().equals(Process.OPEN)
            || offer.getSubmittent().getSubmissionId().getProcess().equals(Process.INVITATION)) {
            offer.setIsExcludedFromAwardProcess(
              (offer.getqExExaminationIsFulfilled() == null) ? false
                : !offer.getqExExaminationIsFulfilled());
          } else {
            offer.setIsExcludedFromAwardProcess(
              (offer.getSubmittent().getExistsExclusionReasons() == null) ? true
                : offer.getSubmittent().getExistsExclusionReasons());
          }
          if (offer.getIsExcludedFromProcess()) {
            // If offer is excluded, set award total score to null.
            offer.setAwardTotalScore(null);
            // Set grade and score of price and operating cost offer criteria to null.
            for (OfferCriterionDTO offerCriterionDTO : offer.getOfferCriteria()) {
              if (offerCriterionDTO.getCriterion().getCriterionType()
                .equals(LookupValues.PRICE_AWARD_CRITERION_TYPE)
                || offerCriterionDTO.getCriterion().getCriterionType()
                .equals(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE)) {
                if (offerCriterionDTO.getScore() != null) {
                  offerCriterionDTO.setScore(null);
                }
                if (offerCriterionDTO.getGrade() != null) {
                  offerCriterionDTO.setGrade(null);
                }
              }
            }
          }
        }
        Collections.sort(offerDTOs, ComparatorUtil.sortOfferEntitiesAndExcluded);
      } else {
        Collections.sort(offerEntities, ComparatorUtil.sortOfferEntities);
        offerDTOs = OfferDTOWithCriteriaMapper.INSTANCE.toOfferDTO(offerEntities);
        for (OfferDTO offer : offerDTOs) {
          /* no excluded offer in this case */
          offer.setIsExcludedFromAwardProcess(false);
        }
      }
      for (OfferDTO offer : offerDTOs) {
        Collections.sort(offer.getOfferCriteria(), ComparatorUtil.offerCriteriaWithWeightings);
        for (OfferCriterionDTO offerCriterion : offer.getOfferCriteria()) {
          Collections.sort(offerCriterion.getCriterion().getSubcriterion(),
            ComparatorUtil.subcriteria);
        }
      }
    }
    return offerDTOs;
  }

  /**
   * Creates a formal audit entity.
   *
   * @param submittentEntity the submittent entity
   */
  private void createFormalAuditEntity(SubmittentEntity submittentEntity) {

    LOGGER.log(Level.CONFIG,
      "Executing method createFormalAuditEntity, Parameters: submittentEntity: {0}",
      submittentEntity);

    FormalAuditEntity formalAuditEntity = new FormalAuditEntity();
    formalAuditEntity.setExistsExclusionReasons(submittentEntity.getExistsExclusionReasons());
    formalAuditEntity
      .setFormalExaminationFulfilled(submittentEntity.getFormalExaminationFulfilled());
    formalAuditEntity.setSubmittent(submittentEntity);
    em.merge(formalAuditEntity);
  }

  /**
   * Create an empty OfferCriterionEntity for each offer of the submission of the given criterion.
   *
   * @param criterion the criterion Entity
   */
  private void createOfferCriterion(CriterionEntity criterion) {

    LOGGER.log(Level.CONFIG,
      "Executing method createOfferCriterion, Parameters: criterion: {0}", criterion);

    List<OfferEntity> offerEntities =
      new JPAQueryFactory(em).select(qOfferEntity).from(qOfferEntity)
        .where(qOfferEntity.submittent.submissionId.id.eq(criterion.getSubmission().getId()))
        .fetch();
    for (OfferEntity offer : offerEntities) {
      OfferCriterionEntity offerCriterionEntity = new OfferCriterionEntity();
      offerCriterionEntity.setOffer(offer);
      offerCriterionEntity.setCriterion(criterion);
      em.persist(offerCriterionEntity);
    }
  }

  @Override
  public Set<ValidationError> updateOfferCriteria(List<SuitabilityDTO> suitabilityDTOs) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateOfferCriteria, Parameters: suitabilityDTOs: {0}",
      suitabilityDTOs);

    Set<ValidationError> optimisticLockErrors = new HashSet<>();
    try {
      // Create temporary list in order to apply the order and then set value to qExRank.
      List<OfferEntity> offerEntities = new ArrayList<>();
      boolean evaluatedCriteriaExists = false;
      for (SuitabilityDTO suitabilityDTO : suitabilityDTOs) {
        OfferEntity offer = em.find(OfferEntity.class, suitabilityDTO.getOfferId());

        if (!suitabilityDTO.getOfferVersion().equals(offer.getVersion())) {
          optimisticLockErrors
            .add(new ValidationError(ValidationMessages.OPTIMISTIC_LOCK_ERROR_FIELD,
              ValidationMessages.OPTIMISTIC_LOCK));
          return optimisticLockErrors;
        }

        offer.setqExRank(suitabilityDTO.getqExRank());
        offer.setqExExaminationIsFulfilled(suitabilityDTO.getqExExaminationIsFulfilled());
        /*
         * Update status according to values that have already set during formal examination of the
         * submittent.
         */
        updateOfferExStatus(offer);
        offer.setqExSuitabilityNotes(suitabilityDTO.getqExSuitabilityNotes());
        offer.setqExTotalGrade(suitabilityDTO.getqExTotalGrade());
        offer.setqExStatus(suitabilityDTO.getqExStatus());
        for (CriterionLiteDTO criterionLiteDTO : suitabilityDTO.getMustCriterion()) {
          OfferCriterionEntity offerCriterionEntity =
            em.find(OfferCriterionEntity.class, criterionLiteDTO.getCriterionId());
          offerCriterionEntity.setGrade(criterionLiteDTO.getGrade());
          offerCriterionEntity.setIsFulfilled(criterionLiteDTO.getIsFulfilled());
          em.merge(offerCriterionEntity);
        }

        for (CriterionLiteDTO criterionLiteDTO : suitabilityDTO.getEvaluatedCriterion()) {
          OfferCriterionEntity offerCriterionEntity =
            em.find(OfferCriterionEntity.class, criterionLiteDTO.getCriterionId());
          offerCriterionEntity.setGrade(criterionLiteDTO.getGrade());
          offerCriterionEntity.setScore(criterionLiteDTO.getScore());
          evaluatedCriteriaExists = true;

        }
        /* update OfferSubcriteria */
        updateOfferSubcriteria(suitabilityDTO.getOfferSubcriteria(), offer);
        em.merge(offer);
        offerEntities.add(offer);
      }
      // Sort criteria with rank and update qExRank value.
      if (evaluatedCriteriaExists) {
        Collections.sort(offerEntities, ComparatorUtil.sortOfferEntitiesWithRank);
      } else {
        Collections.sort(offerEntities, ComparatorUtil.sortOfferEntities);
      }
      /* inform rank field */
      for (OfferEntity offer : offerEntities) {
        offer.setqExRank(Integer.valueOf(offerEntities.indexOf(offer) + 1));
        offer.getSubmittent().setSortOrder(offer.getqExRank());
        em.merge(offer);
      }
    } catch (OptimisticLockException | RollbackException e) {
      optimisticLockErrors
        .add(new ValidationError(ValidationMessages.OPTIMISTIC_LOCK_ERROR_FIELD,
          ValidationMessages.OPTIMISTIC_LOCK));
    }
    return optimisticLockErrors;
  }

  /**
   * Update offer subcriteria.
   *
   * @param offerSubcriterionLiteDTOs the offer subcriterion lite DT os
   * @param offer the offer
   */
  private void updateOfferSubcriteria(List<OfferSubcriterionLiteDTO> offerSubcriterionLiteDTOs,
    OfferEntity offer) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateOfferSubcriteria, Parameters: offer: {0}", offer);

    for (OfferSubcriterionLiteDTO offerSubcriterionLiteDTO : offerSubcriterionLiteDTOs) {
      OfferSubcriterionEntity offerSubcriterionEntity = new JPAQueryFactory(em)
        .select(qOfferSubriterionEntity).from(qOfferSubriterionEntity)
        .where(qOfferSubriterionEntity.offer.eq(offer).and(qOfferSubriterionEntity.subcriterion.id
          .eq(offerSubcriterionLiteDTO.getOfferSubcriterionId())))
        .fetchOne();
      offerSubcriterionEntity.setGrade(offerSubcriterionLiteDTO.getGrade());

      offerSubcriterionEntity.setScore(offerSubcriterionLiteDTO.getScore());
      em.merge(offerSubcriterionEntity);
    }
  }

  /**
   * Update offer ex status.
   *
   * @param offer the offer
   */
  private void updateOfferExStatus(OfferEntity offer) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateOfferExStatus, Parameters: offer: {0}", offer);

    if (offer.getSubmittent().getFormalExaminationFulfilled() != null
      && offer.getSubmittent().getExistsExclusionReasons() != null
      && (!offer.getSubmittent().getFormalExaminationFulfilled()
      || offer.getSubmittent().getExistsExclusionReasons())) {
      offer.setqExStatus(Boolean.FALSE);
    } else {
      offer.setqExStatus(Boolean.TRUE);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.CriterionService#updateOfferCriteriaAward(java.util
   * .List)
   */
  @Override
  public void updateOfferCriteriaAward(List<AwardAssessDTO> awardAssessDTOs) {

    LOGGER.log(Level.CONFIG, "Executing method updateOfferCriteriaAward");

    for (AwardAssessDTO awardAssessDTO : awardAssessDTOs) {
      OfferEntity offer = em.find(OfferEntity.class, awardAssessDTO.getOfferId());
      offer.setAwardRank(awardAssessDTO.getRank());
      offer.setAwardTotalScore(awardAssessDTO.getTotalScore());
      /* update OfferCriteria */
      for (OfferCriterionLiteDTO offerCriterionLiteDTO : awardAssessDTO.getOfferCriteria()) {
        OfferCriterionEntity offerCriterionEntity =
          em.find(OfferCriterionEntity.class, offerCriterionLiteDTO.getOfferCriterionId());
        /*
         * update only if the criterion is not price or operating cost criterion, because those
         * criteria get calculated in every update, but after the update this method is called, so
         * they will again get the grade zero, that they initially had
         */
        if (!offerCriterionEntity.getCriterion().getCriterionType()
          .equals(LookupValues.PRICE_AWARD_CRITERION_TYPE)
          && !offerCriterionEntity.getCriterion().getCriterionType()
          .equals(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE)) {
          offerCriterionEntity.setGrade(offerCriterionLiteDTO.getGrade());
          offerCriterionEntity.setScore(offerCriterionLiteDTO.getScore());
        }
        em.merge(offerCriterionEntity);
      }
      /* update OfferSubcriteria */
      for (OfferSubcriterionLiteDTO offerSubcriterionLiteDTO : awardAssessDTO
        .getOfferSubcriteria()) {
        OfferSubcriterionEntity offerSubcriterionEntity = em.find(OfferSubcriterionEntity.class,
          offerSubcriterionLiteDTO.getOfferSubcriterionId());
        offerSubcriterionEntity.setGrade(offerSubcriterionLiteDTO.getGrade());
        offerSubcriterionEntity.setScore(offerSubcriterionLiteDTO.getScore());
        em.merge(offerSubcriterionEntity);
      }
      em.merge(offer);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.CriterionService#checkSubcriteriaWeightingLimit(
   * java.util.List)
   */
  @Override
  public List<String> checkSubcriteriaWeightingLimit(List<SuitabilityDTO> suitabilityDTOs) {

    LOGGER.log(Level.CONFIG, "Executing method checkSubcriteriaWeightingLimit");

    List<String> notesAreMandatory = new ArrayList<>();
    for (SuitabilityDTO suitabilityDTO : suitabilityDTOs) {
      if (suitabilityDTO.getqExStatus() != null
        && suitabilityDTO.getqExExaminationIsFulfilled() != null
        && !suitabilityDTO.getqExStatus().equals(suitabilityDTO.getqExExaminationIsFulfilled())) {
        notesAreMandatory.add(suitabilityDTO.getOfferId());
      }
    }
    return notesAreMandatory;

  }

  /*
   * (non-Javadoc)
   *
   * @see
   * ch.bern.submiss.services.api.administration.CriterionService#existNullGrade(java.lang.String)
   */
  @Override
  public boolean existNullGrade(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method existNullGrade, Parameters: submissionId: {0}", submissionId);

    Process process = new JPAQueryFactory(em).select(qSubmissionEntity.process)
      .from(qSubmissionEntity).where(qSubmissionEntity.id.eq(submissionId)).fetchFirst();
    // Calculate offer criteria which do not contain a grade.
    int nullGradedOfferCriteria = 0;
    if (process == Process.SELECTIVE) {
      nullGradedOfferCriteria = new JPAQueryFactory(em).selectFrom(qOfferCriterionEntity)
        .where(qOfferCriterionEntity.criterion.submission.id.eq(submissionId)
          .and(qOfferCriterionEntity.offer.isExcludedFromProcess.isFalse())
          .and(qOfferCriterionEntity.offer.isEmptyOffer.isFalse())
          .and(qOfferCriterionEntity.offer.submittent.isApplicant.isNull()
            .or(qOfferCriterionEntity.offer.submittent.isApplicant.isTrue()
              .and(qOfferCriterionEntity.offer.excludedFirstLevel.isFalse())))
          .and(qOfferCriterionEntity.criterion.criterionType
            .eq(LookupValues.AWARD_CRITERION_TYPE))
          .and(qOfferCriterionEntity.grade.isNull()))
        .fetch().size();
    } else {
      nullGradedOfferCriteria = new JPAQueryFactory(em).selectFrom(qOfferCriterionEntity)
        .where(qOfferCriterionEntity.criterion.submission.id.eq(submissionId)
          .and(qOfferCriterionEntity.offer.isExcludedFromProcess.isFalse())
          .and(qOfferCriterionEntity.offer.isEmptyOffer.isFalse())
          .and(qOfferCriterionEntity.criterion.criterionType
            .eq(LookupValues.AWARD_CRITERION_TYPE))
          .and(qOfferCriterionEntity.grade.isNull()))
        .fetch().size();
    }
    if (nullGradedOfferCriteria > 0) {
      return true;
    }
    // Calculate offer sub-criteria which do not contain a grade.
    int nullGradedOfferSubcriteria = 0;
    if (process == Process.SELECTIVE) {
      nullGradedOfferSubcriteria = new JPAQueryFactory(em).selectFrom(qOfferSubriterionEntity)
        .where(qOfferSubriterionEntity.subcriterion.criterion
          .in(JPAExpressions.select(qCriterionEntity).from(qCriterionEntity)
            .where(qCriterionEntity.submission.id.eq(submissionId)))
          .and(qOfferSubriterionEntity.offer.isExcludedFromProcess.isFalse())
          .and(qOfferSubriterionEntity.offer.isEmptyOffer.isFalse())
          .and(qOfferSubriterionEntity.offer.submittent.isApplicant.isNull()
            .or(qOfferSubriterionEntity.offer.submittent.isApplicant.isTrue()
              .and(qOfferSubriterionEntity.offer.excludedFirstLevel.isFalse())))
          .and(qOfferSubriterionEntity.subcriterion.criterion.criterionType
            .eq(LookupValues.AWARD_CRITERION_TYPE))
          .and(qOfferSubriterionEntity.grade.isNull()))
        .fetch().size();
    } else {
      nullGradedOfferSubcriteria = new JPAQueryFactory(em).selectFrom(qOfferSubriterionEntity)
        .where(qOfferSubriterionEntity.subcriterion.criterion
          .in(JPAExpressions.select(qCriterionEntity).from(qCriterionEntity)
            .where(qCriterionEntity.submission.id.eq(submissionId)))
          .and(qOfferSubriterionEntity.offer.isExcludedFromProcess.isFalse())
          .and(qOfferSubriterionEntity.offer.isEmptyOffer.isFalse())
          .and(qOfferSubriterionEntity.subcriterion.criterion.criterionType
            .eq(LookupValues.AWARD_CRITERION_TYPE))
          .and(qOfferSubriterionEntity.grade.isNull()))
        .fetch().size();
    }
    return nullGradedOfferSubcriteria > 0;
  }

  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.CriterionService#existInvalidGrade(java.lang.
   * String, java.math.BigDecimal, java.math.BigDecimal)
   */
  @Override
  public boolean existInvalidGrade(String submissionId, BigDecimal awardMinGrade,
    BigDecimal awardMaxGrade) {

    LOGGER.log(Level.CONFIG,
      "Executing method existInvalidGrade, Parameters: submissionId: {0}"
        + ", awardMinGrade: {1}, awardMaxGrade: {2}",
      new Object[]{submissionId, awardMinGrade, awardMaxGrade});

    Process process = new JPAQueryFactory(em).select(qSubmissionEntity.process)
      .from(qSubmissionEntity).where(qSubmissionEntity.id.eq(submissionId)).fetchFirst();
    // Calculate offer criteria which contain an invalid grade.
    int invalidGradedOfferCriteria = 0;
    if (process == Process.SELECTIVE) {
      invalidGradedOfferCriteria = new JPAQueryFactory(em).selectFrom(qOfferCriterionEntity)
        .where(qOfferCriterionEntity.criterion.submission.id.eq(submissionId)
          .and(qOfferCriterionEntity.offer.isExcludedFromProcess.isFalse())
          .and(qOfferCriterionEntity.offer.isEmptyOffer.isFalse())
          .and(qOfferCriterionEntity.offer.submittent.isApplicant.isNull()
            .or(qOfferCriterionEntity.offer.submittent.isApplicant.isTrue()
              .and(qOfferCriterionEntity.offer.excludedFirstLevel.isFalse())))
          .and(qOfferCriterionEntity.criterion.criterionType
            .eq(LookupValues.AWARD_CRITERION_TYPE))
          .and(qOfferCriterionEntity.criterion.subcriteria.size().eq(0))
          .and(qOfferCriterionEntity.grade.notBetween(awardMinGrade, awardMaxGrade)))
        .fetch().size();
    } else {
      invalidGradedOfferCriteria = new JPAQueryFactory(em).selectFrom(qOfferCriterionEntity)
        .where(qOfferCriterionEntity.criterion.submission.id.eq(submissionId)
          .and(qOfferCriterionEntity.offer.isExcludedFromProcess.isFalse())
          .and(qOfferCriterionEntity.offer.isEmptyOffer.isFalse())
          .and(qOfferCriterionEntity.criterion.criterionType
            .eq(LookupValues.AWARD_CRITERION_TYPE))
          .and(qOfferCriterionEntity.criterion.subcriteria.size().eq(0))
          .and(qOfferCriterionEntity.grade.notBetween(awardMinGrade, awardMaxGrade)))
        .fetch().size();
    }
    if (invalidGradedOfferCriteria > 0) {
      return true;
    }
    int invalidGradedOfferSubcriteria = 0;
    // Calculate offer sub-criteria which contain an invalid grade.
    if (process == Process.SELECTIVE) {
      invalidGradedOfferSubcriteria = new JPAQueryFactory(em).selectFrom(qOfferSubriterionEntity)
        .where(qOfferSubriterionEntity.subcriterion.criterion
          .in(JPAExpressions.select(qCriterionEntity).from(qCriterionEntity)
            .where(qCriterionEntity.submission.id.eq(submissionId)))
          .and(qOfferSubriterionEntity.offer.isExcludedFromProcess.isFalse())
          .and(qOfferSubriterionEntity.offer.isEmptyOffer.isFalse())
          .and(qOfferSubriterionEntity.offer.submittent.isApplicant.isNull()
            .or(qOfferSubriterionEntity.offer.submittent.isApplicant.isTrue()
              .and(qOfferSubriterionEntity.offer.excludedFirstLevel.isFalse())))
          .and(qOfferSubriterionEntity.subcriterion.criterion.criterionType
            .eq(LookupValues.AWARD_CRITERION_TYPE))
          .and(qOfferSubriterionEntity.grade.notBetween(awardMinGrade, awardMaxGrade)))
        .fetch().size();
    } else {
      invalidGradedOfferSubcriteria = new JPAQueryFactory(em).selectFrom(qOfferSubriterionEntity)
        .where(qOfferSubriterionEntity.subcriterion.criterion
          .in(JPAExpressions.select(qCriterionEntity).from(qCriterionEntity)
            .where(qCriterionEntity.submission.id.eq(submissionId)))
          .and(qOfferSubriterionEntity.offer.isExcludedFromProcess.isFalse())
          .and(qOfferSubriterionEntity.offer.isEmptyOffer.isFalse())
          .and(qOfferSubriterionEntity.subcriterion.criterion.criterionType
            .eq(LookupValues.AWARD_CRITERION_TYPE))
          .and(qOfferSubriterionEntity.grade.notBetween(awardMinGrade, awardMaxGrade)))
        .fetch().size();
    }
    return invalidGradedOfferSubcriteria > 0;
  }

  /**
   * Calculate the score given the grade and weighting.
   *
   * @param grade the grade
   * @param weighting the weighting
   * @return the score
   */
  private BigDecimal calculateScore(BigDecimal grade, Double weighting) {

    LOGGER.log(Level.CONFIG,
      "Executing method calculateScore, Parameters: grade: {0}, weighting: {1}",
      new Object[]{grade, weighting});

    if (grade != null && weighting != null) {
      BigDecimal weight = new BigDecimal(weighting);
      return grade.multiply(weight).divide(BigDecimal.valueOf(100),3, RoundingMode.HALF_UP);
    }
    return null;
  }

  @Override
  public boolean offerMustCriterionNotFulfilled(String offerId) {

    LOGGER.log(Level.CONFIG,
      "Executing method offerMustCriterionNotFulfilled, Parameters: offerId: {0}", offerId);

    List<OfferCriterionEntity> offerMustCriteria =
      new JPAQueryFactory(em).select(qOfferCriterionEntity).from(qOfferCriterionEntity)
        .where(qOfferCriterionEntity.offer.id.eq(offerId).and(
          qOfferCriterionEntity.criterion.criterionType.eq(LookupValues.MUST_CRITERION_TYPE)))
        .fetch();
    for (OfferCriterionEntity offerMustCriterion : offerMustCriteria) {
      if (offerMustCriterion.getIsFulfilled() != null
        && offerMustCriterion.getIsFulfilled() == (Boolean.FALSE)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Given a list of offer entities it sets in the field awardRank of each offer the ranking
   * according to the field awardTotalScore and whether they are excluded from the process.
   *
   * @param offerEntities the offer entities
   * @param submissionStatus the submission status
   */
  private void rankOffers(List<OfferEntity> offerEntities, TenderStatus submissionStatus) {

    LOGGER.log(Level.CONFIG,
      "Executing method rankOffers, Parameters: submissionStatus: {0}", submissionStatus);

    if (offerEntities.get(0).getSubmittent().getSubmissionId().getProcess() == Process.SELECTIVE) {
      // In case of selective process, exclude applicants who have been excluded from the process,
      // from the ranking.
      offerEntities.removeIf(offerEntity -> offerEntity.getSubmittent().getIsApplicant() != null
        && offerEntity.getSubmittent().getIsApplicant().equals(Boolean.TRUE)
        && offerEntity.getExcludedFirstLevel() != null
        && offerEntity.getExcludedFirstLevel().equals(Boolean.TRUE));
    }
    /*
     * Create a new offer list as copy of the existing, in order to sort it according to the ranking
     * and use it to inform the current offer list about the ranking
     */
    List<OfferEntity> tmpOfferEntities = new ArrayList<>(offerEntities);
    /*
     * if the status of the submission is after "Eignungsprüfung abgeschlossen" or
     * "Eignungsprüfung abgeschlossen & Zuschlagsbewertung gestartet" then the excluded offers must
     * be excluded from the ranking
     */
    if (compareCurrentVsSpecificStatus(submissionStatus,
      TenderStatus.SUITABILITY_AUDIT_COMPLETED)) {
      for (OfferEntity offer : offerEntities) {
        if (offer.getSubmittent().getSubmissionId().getProcess().equals(Process.OPEN)
          || offer.getSubmittent().getSubmissionId().getProcess().equals(Process.INVITATION)) {
          if (offer.getqExExaminationIsFulfilled() != null
            && !offer.getqExExaminationIsFulfilled()) {
            tmpOfferEntities.remove(offer);
          }
        } else {
          if (offer.getSubmittent().getExistsExclusionReasons() == null
            || offer.getSubmittent().getExistsExclusionReasons()) {
            tmpOfferEntities.remove(offer);
          }
        }
      }
    }
    /* sort */
    Collections.sort(tmpOfferEntities, ComparatorUtil.sortOfferEntitiesScore);
    /* inform rank field */
    for (OfferEntity offer : offerEntities) {
      if (tmpOfferEntities.contains(offer)) { // is not excluded
        for (OfferEntity sortedOffer : tmpOfferEntities) {
          if (offer.equals(sortedOffer)) {
            offer.setAwardRank(Integer.valueOf(tmpOfferEntities.indexOf(sortedOffer) + 1));
          }
        }
      } else { // the excluded offers don't get a rank
        offer.setAwardRank(null);
      }
    }
  }

  @Override
  public Set<ValidationError> updateCustomCalculationFormula(String calculationFormula,
    boolean isPrice, String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateCustomCalculationFormula, Parameters: calculationFormula: {0}"
        + ", isPrice: {1}, submissionId: {2}",
      new Object[]{calculationFormula, isPrice, submissionId});

    Set<ValidationError> errors = mathParser.checkCalculationFormula(calculationFormula);
    // if no errors exist in the calculation formula
    // then update the custom calculation formula
    if (errors.isEmpty()) {
      List<OfferEntity> offerEntities = null;
      // update the custom calculation formula
      SubmissionEntity submissionEntity = em.find(SubmissionEntity.class, submissionId);
      if (isPrice) {

        // recalculate the grades, scores according to the new formula
        Double criterionWeighting =
          new JPAQueryFactory(em).select(qCriterionEntity.weighting).from(qCriterionEntity)
            .where(qCriterionEntity.submission.id.eq(submissionId).and(
              (qCriterionEntity.criterionType.eq(LookupValues.PRICE_AWARD_CRITERION_TYPE))))
            .fetchOne();

        if (criterionWeighting != null) {
          // get all offers of the sumbission
          offerEntities = new JPAQueryFactory(em).select(qOfferEntity).from(qOfferEntity)
            .where(qOfferEntity.submittent.submissionId.id.eq(submissionId)
              .and(qOfferEntity.isEmptyOffer.isFalse().or(qOfferEntity.isEmptyOffer.isNull()))
              .and(qOfferEntity.excludedFirstLevel.isFalse().or(qOfferEntity.excludedFirstLevel.isNull())))
            .fetch();

          // iterate the offers of the submission to find the lowest price, because they are needed
          // for the calculation of the price criterion grade
          BigDecimal minPrice = null;
          for (OfferEntity offer : offerEntities) {
            if (offer.getAmount() != null
              && (offer.getIsExcludedFromProcess() == null || !offer.getIsExcludedFromProcess())
              && (minPrice == null || offer.getAmount().doubleValue() < minPrice.doubleValue())) {
              /*
               * set the value to the variable if it is the first one found or if it is less than
               * the one found
               */
              minPrice = offer.getAmount();
            }
          }

          // iterate all offer criteria in order to find and update the grade of the criterion
          // according to the new formula
          // then the scores and the total score and rank must be updated accordingly
          boolean maxErrorFound = false;
          boolean minErrorFound = false;
          for (OfferEntity offer : offerEntities) {
            // If the formula produces grades with both minimum and maximum value errors, exit the
            // loop.
            if (minErrorFound && maxErrorFound) {
              break;
            }
            double sumTotalScore = 0;
            for (OfferCriterionEntity offerCriterion : offer.getOfferCriteria()) {
              if (offerCriterion.getCriterion().getCriterionType()
                .equals(LookupValues.PRICE_AWARD_CRITERION_TYPE)
                && (offer.getIsExcludedFromProcess() == null
                || !offer.getIsExcludedFromProcess())) {
                BigDecimal grade =
                  calculateGrade(offer.getAmount(), minPrice, calculationFormula);
                // If the calculated grade exceeds the maximum grade, return an error accordingly.
                if (grade.compareTo(submissionEntity.getAwardMaxGrade()) > 0 && !maxErrorFound) {
                  errors.add(new ValidationError(MAX_ERROR_FIELD,
                    ValidationMessages.FORMULA_MAX_GRADE_ERROR));
                  errors.add(
                    new ValidationError(FORMULA, ValidationMessages.FORMULA_MAX_GRADE_ERROR));
                  maxErrorFound = true;
                }
                // If the calculated grade is smaller than the minimum grade, return an error
                // accordingly.
                if (grade.compareTo(submissionEntity.getAwardMinGrade()) < 0 && !minErrorFound) {
                  errors.add(new ValidationError(MIN_ERROR_FIELD,
                    ValidationMessages.FORMULA_MIN_GRADE_ERROR));
                  errors.add(
                    new ValidationError(FORMULA, ValidationMessages.FORMULA_MIN_GRADE_ERROR));
                  minErrorFound = true;
                }
                // If invalid grade has been found exit the loop.
                if (maxErrorFound || minErrorFound) {
                  break;
                }
                offerCriterion.setGrade(grade);
                BigDecimal score = calculateScore(offerCriterion.getGrade(), criterionWeighting);
                offerCriterion.setScore(score);
                if (score != null) {
                  sumTotalScore = sumTotalScore + score.doubleValue();
                }
              } else if ((!offerCriterion.getCriterion().getCriterionType()
                .equals(LookupValues.EVALUATED_CRITERION_TYPE)
                && !offerCriterion.getCriterion().getCriterionType()
                .equals(LookupValues.MUST_CRITERION_TYPE))
                && offerCriterion.getScore() != null) {
                sumTotalScore = sumTotalScore + offerCriterion.getScore().doubleValue();
              }
            }
            offer.setAwardTotalScore(BigDecimal.valueOf(sumTotalScore));
          }
          if (errors != null && !errors.isEmpty()) {
            return errors;
          }
        }
        submissionEntity.setCustomPriceFormula(calculationFormula);
        // custom formula exists, set other formula to null
        submissionEntity.setPriceFormula(null);

      } else {

        // recalculate the grades, scores according to the new formula
        Double criterionWeighting =
          new JPAQueryFactory(em)
            .select(
              qCriterionEntity.weighting)
            .from(qCriterionEntity)
            .where(qCriterionEntity.submission.id.eq(submissionId)
              .and((qCriterionEntity.criterionType
                .eq(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE))))
            .fetchOne();

        if (criterionWeighting != null) {
          // get all offers of the sumbission
          offerEntities = new JPAQueryFactory(em).select(qOfferEntity).from(qOfferEntity)
            .where(qOfferEntity.submittent.submissionId.id.eq(submissionId)
              .and(qOfferEntity.isEmptyOffer.isFalse()))
            .fetch();

          // iterate the offers of the submission to find the lowest operating cost, because they
          // are needed
          // for the calculation of the operating cost criterion grade
          BigDecimal minOperatingCost = null;
          for (OfferEntity offer : offerEntities) {
            if (offer.getOperatingCostsAmount() != null
              && offer.getOperatingCostsAmount().compareTo(BigDecimal.ZERO) != 0
              && (offer.getIsExcludedFromProcess() == null || !offer.getIsExcludedFromProcess())
              && (minOperatingCost == null
              || offer.getOperatingCostsAmount().compareTo(minOperatingCost) < 0)) {
              /*
               * set the value to the variable if it is not 0 and if it is the first one found or if
               * it is less than the one found
               */
              minOperatingCost = offer.getOperatingCostsAmount();
            }
          }

          // iterate all offer criteria in order to find and update the grade of the criterion
          // according to the new formula
          // then the scores and the total score and rank must be updated accordingly
          boolean maxErrorFound = false;
          boolean minErrorFound = false;
          for (OfferEntity offer : offerEntities) {
            // If the formula produces grades with both minimum and maximum value errors, exit the
            // loop.
            if (minErrorFound && maxErrorFound) {
              break;
            }
            double sumTotalScore = 0;
            for (OfferCriterionEntity offerCriterion : offer.getOfferCriteria()) {
              BigDecimal grade = null;
              if (offerCriterion.getCriterion().getCriterionType()
                .equals(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE)) {
                if (offer.getOperatingCostsAmount() != null
                  && offer.getOperatingCostsAmount().compareTo(BigDecimal.ZERO) != 0
                  && minOperatingCost != null && calculationFormula != null
                  && (offer.getIsExcludedFromProcess() == null
                  || !offer.getIsExcludedFromProcess())) {
                  grade = calculateGrade(offer.getOperatingCostsAmount(), minOperatingCost,
                    calculationFormula);
                  // If the calculated grade exceeds the maximum grade, return an error accordingly.
                  if (grade.compareTo(submissionEntity.getAwardMaxGrade()) > 0 && !maxErrorFound) {
                    errors.add(new ValidationError(MAX_ERROR_FIELD,
                      ValidationMessages.FORMULA_MAX_GRADE_ERROR));
                    errors.add(
                      new ValidationError(FORMULA, ValidationMessages.FORMULA_MAX_GRADE_ERROR));
                    maxErrorFound = true;
                  }
                  // If the calculated grade is smaller than the minimum grade, return an error
                  // accordingly.
                  if (grade.compareTo(submissionEntity.getAwardMinGrade()) < 0 && !minErrorFound) {
                    errors.add(new ValidationError(MIN_ERROR_FIELD,
                      ValidationMessages.FORMULA_MIN_GRADE_ERROR));
                    errors.add(
                      new ValidationError(FORMULA, ValidationMessages.FORMULA_MIN_GRADE_ERROR));
                    minErrorFound = true;
                  }
                }
                // If invalid grade has been bound exit the loop.
                if (maxErrorFound || minErrorFound) {
                  break;
                }
                offerCriterion.setGrade(grade);
                BigDecimal score = calculateScore(offerCriterion.getGrade(), criterionWeighting);
                offerCriterion.setScore(score);
                if (score != null) {
                  sumTotalScore = sumTotalScore + score.doubleValue();
                }
              } else if ((!offerCriterion.getCriterion().getCriterionType()
                .equals(LookupValues.EVALUATED_CRITERION_TYPE)
                && !offerCriterion.getCriterion().getCriterionType()
                .equals(LookupValues.MUST_CRITERION_TYPE))
                && offerCriterion.getScore() != null) {
                sumTotalScore = sumTotalScore + offerCriterion.getScore().doubleValue();
              }
            }
            offer.setAwardTotalScore(BigDecimal.valueOf(sumTotalScore));
          }
          if (errors != null && !errors.isEmpty()) {
            return errors;
          }
        } else {
          // if no operating cost criterion exists, just return the errors.
          return errors;
        }
        submissionEntity.setCustomOperatingCostFormula(calculationFormula);
        // custom formula exists, set other formula to null
        submissionEntity.setOperatingCostFormula(null);
      }
      // in any case update the ranking
      TenderStatus submissionStatus =
        TenderStatus.fromValue(submissionService.getCurrentStatusOfSubmission(submissionId));
      rankOffers(offerEntities, submissionStatus);
    }
    return errors;
  }

  @Override
  public boolean isOfferCriteriaForChanged(List<SuitabilityDTO> suitabilityDTOs) {

    LOGGER.log(Level.CONFIG,
      "Executing isOfferCriteriaForChanged Parameters: suitabilityDTOs: {0}",
      suitabilityDTOs);

    for (SuitabilityDTO suitabilityDTO : suitabilityDTOs) {
      OfferEntity offer = em.find(OfferEntity.class, suitabilityDTO.getOfferId());

      if (ObjectUtils.compare(offer.getqExRank(), suitabilityDTO.getqExRank()) != 0
        || ObjectUtils.compare(offer.getqExExaminationIsFulfilled(),
        suitabilityDTO.getqExExaminationIsFulfilled()) != 0) {
        return true;
      }

      if (
        ObjectUtils.compare(offer.getqExSuitabilityNotes(), suitabilityDTO.getqExSuitabilityNotes())
          != 0
          || ObjectUtils.compare(offer.getqExTotalGrade(), suitabilityDTO.getqExTotalGrade()) != 0
          || ObjectUtils.compare(offer.getqExStatus(), suitabilityDTO.getqExStatus()) != 0) {
        return true;
      }

      for (CriterionLiteDTO criterionLiteDTO : suitabilityDTO.getMustCriterion()) {
        OfferCriterionEntity offerCriterionEntity =
          em.find(OfferCriterionEntity.class, criterionLiteDTO.getCriterionId());

        if (ObjectUtils.compare(offerCriterionEntity.getGrade(), criterionLiteDTO.getGrade()) != 0
          || ObjectUtils
          .compare(offerCriterionEntity.getIsFulfilled(), criterionLiteDTO.getIsFulfilled()) != 0) {
          return true;
        }
      }

      for (CriterionLiteDTO criterionLiteDTO : suitabilityDTO.getEvaluatedCriterion()) {
        OfferCriterionEntity offerCriterionEntity =
          em.find(OfferCriterionEntity.class, criterionLiteDTO.getCriterionId());

        if (ObjectUtils.compare(offerCriterionEntity.getGrade(), criterionLiteDTO.getGrade()) != 0
          || ObjectUtils.compare(offerCriterionEntity.getScore(), criterionLiteDTO.getScore())
          != 0) {
          return true;
        }
      }
    }

    return false;
  }

  @Override
  public boolean isOfferCriteriaForChangedAward(List<AwardAssessDTO> awardAssessDTOs) {

    LOGGER.log(Level.CONFIG,
      "Executing method isOfferCriteriaForChangedAward. Parameters: awardAssessDTOs: {0}",
      awardAssessDTOs);

    for (AwardAssessDTO awardAssessDTO : awardAssessDTOs) {
      OfferEntity offer = em.find(OfferEntity.class, awardAssessDTO.getOfferId());

      if ((!offer.getAwardRank().equals(awardAssessDTO.getRank()))
        || ObjectUtils.compare(offer.getAwardTotalScore(), awardAssessDTO.getTotalScore()) != 0
      ) {
        return true;
      }
      /* check OfferCriteria */
      for (OfferCriterionLiteDTO offerCriterionLiteDTO : awardAssessDTO.getOfferCriteria()) {
        OfferCriterionEntity offerCriterionEntity =
          em.find(OfferCriterionEntity.class, offerCriterionLiteDTO.getOfferCriterionId());

        if (!offerCriterionEntity.getCriterion().getCriterionType()
          .equals(LookupValues.PRICE_AWARD_CRITERION_TYPE)
          && !offerCriterionEntity.getCriterion().getCriterionType()
          .equals(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE)) {

          if (ObjectUtils.compare(offerCriterionEntity.getGrade(),
            offerCriterionLiteDTO.getGrade()) != 0
            || ObjectUtils.compare(offerCriterionEntity.getScore(),
            offerCriterionLiteDTO.getScore()) != 0) {
            return true;
          }
        }
      }
      /* check OfferSubcriteria */
      for (OfferSubcriterionLiteDTO offerSubcriterionLiteDTO : awardAssessDTO
        .getOfferSubcriteria()) {
        OfferSubcriterionEntity offerSubcriterionEntity = em.find(OfferSubcriterionEntity.class,
          offerSubcriterionLiteDTO.getOfferSubcriterionId());

        if (ObjectUtils.compare(offerSubcriterionEntity.getGrade(),
          offerSubcriterionLiteDTO.getGrade()) != 0
          || ObjectUtils.compare(offerSubcriterionEntity.getScore(),
          offerSubcriterionLiteDTO.getScore()) != 0) {
          return true;
        }
      }
    }

    return false;
  }

  @Override
  public BigDecimal calculateGrade(BigDecimal currentAmount, BigDecimal minAmount,
    String expression) {

    LOGGER.log(Level.CONFIG,
      "Executing method getOfferCriteria, Parameters: currentAmount: {0}, "
        + "minAmount: {1}, expression: {2}",
      new Object[]{currentAmount, minAmount, expression});

    return mathParser.calculate(currentAmount, minAmount, expression)
      .setScale(2, RoundingMode.HALF_UP);
  }

  @Override
  public List<OfferDTO> getExaminationSubmittentListWithCriteria(String submissionId, String type,
    Boolean all) {

    LOGGER.log(Level.CONFIG,
      "Executing method getOfferCriteria, Parameters: submissionId: {0}"
        + ", type: {1}, all: {2}",
      new Object[]{submissionId, type, all});

    List<OfferEntity> offers = null;
    SubmissionEntity submission = new JPAQueryFactory(em).select(qSubmissionEntity)
      .from(qSubmissionEntity).where(qSubmissionEntity.id.eq(submissionId)).fetchOne();
    if (all && submission.getProcess().equals(Process.SELECTIVE)) {
      offers = new JPAQueryFactory(em).selectDistinct(qOfferEntity).from(qOfferEntity)
        .where(qOfferEntity.submittent.submissionId.id.eq(submissionId)
          .and(qOfferEntity.submittent.isApplicant.isTrue()))
        .orderBy(qOfferEntity.submittent.companyId.companyName.toLowerCase().asc()).fetch();
    } else if (all && !submission.getProcess().equals(Process.SELECTIVE)) {
      offers = new JPAQueryFactory(em).selectDistinct(qOfferEntity).from(qOfferEntity)
        .where(qOfferEntity.submittent.submissionId.id.eq(submissionId)
          .and(qOfferEntity.isEmptyOffer.isFalse()))
        .fetch();
      Collections.sort(offers, ComparatorUtil.sortOfferEntities);
    } else if (!all && submission.getProcess().equals(Process.SELECTIVE)) {
      offers = new JPAQueryFactory(em).selectDistinct(qOfferEntity).from(qOfferEntity)
        .where(qOfferEntity.submittent.submissionId.id.eq(submissionId)
          .and(qOfferEntity.submittent.isApplicant.isTrue()))
        .orderBy(qOfferEntity.submittent.sortOrder.asc()).limit(5).fetch();
    } else {
      offers = new JPAQueryFactory(em).selectDistinct(qOfferEntity).from(qOfferEntity)
        .where(qOfferEntity.submittent.submissionId.id.eq(submissionId)
          .and(qOfferEntity.isEmptyOffer.isFalse()))
        .orderBy(qOfferEntity.submittent.sortOrder.asc()).limit(5).fetch();
    }

    Date deadline;
    if (submission.getProcess().equals(Process.SELECTIVE)) {
      deadline = submission.getFirstDeadline();
    } else {
      deadline = submission.getSecondDeadline();
    }
    List<OfferDTO> offerDTOs = OfferDTOWithCriteriaMapper.INSTANCE.toOfferDTO(offers);
    for (OfferDTO offer : offerDTOs) {
      if (((submission.getProcess().equals(Process.OPEN)
        || submission.getProcess().equals(Process.INVITATION))
        && !compareCurrentVsSpecificStatus(TenderStatus.fromValue(submission.getStatus()),
        TenderStatus.SUITABILITY_AUDIT_COMPLETED_AND_AWARD_EVALUATION_STARTED))
        || (submission.getProcess().equals(Process.SELECTIVE)
        && !compareCurrentVsSpecificStatus(TenderStatus.fromValue(submission.getStatus()),
        TenderStatus.SUITABILITY_AUDIT_COMPLETED_S))) {
        projectBean.calculateSubmittentValues(offer.getSubmittent(), deadline,
          submission.getProcess());
      }
      if (submission.getProcess().equals(Process.SELECTIVE)) {
        FormalAuditEntity formalAuditEntity = new JPAQueryFactory(em).selectFrom(qFormalAuditEntity)
          .where(qFormalAuditEntity.submittent.id.eq(offer.getSubmittent().getId())).fetchOne();
        if (formalAuditEntity != null) {
          offer.getSubmittent()
            .setExistsExclusionReasons(formalAuditEntity.getExistsExclusionReasons());
          if (compareCurrentVsSpecificStatus(TenderStatus.fromValue(submission.getStatus()),
            TenderStatus.SUITABILITY_AUDIT_COMPLETED_S)) {
            offer.getSubmittent()
              .setFormalExaminationFulfilled(formalAuditEntity.getFormalExaminationFulfilled());
          }
        }
      }
      Collections.sort(offer.getOfferCriteria(), ComparatorUtil.offerCriteriaWithoutWeightings);
      Collections.sort(offer.getOfferCriteria(), ComparatorUtil.offerCriteriaWithWeightings);
      for (OfferCriterionDTO offerCriterion : offer.getOfferCriteria()) {
        Collections.sort(offerCriterion.getCriterion().getSubcriterion(),
          ComparatorUtil.subcriteria);
      }
    }
    return offerDTOs;
  }

  @Override
  public List<CriterionDTO> readSubmissionEvaluatedCriteria(String submissionId) {
    List<CriterionEntity> evaluatedCriterionEntities =
      new JPAQueryFactory(em).select(qCriterionEntity).from(qCriterionEntity)
        .where(qCriterionEntity.submission.id.eq(submissionId)
          .and(qCriterionEntity.criterionType.eq(LookupValues.EVALUATED_CRITERION_TYPE)))
        .fetch();
    return CriterionDTOMapper.INSTANCE.toCriterionDTO(evaluatedCriterionEntities);
  }

  @Override
  public void updateOfferCriteriaFromXlsx(List<SuitabilityDTO> suitabilityDTOs) {
    LOGGER.log(Level.CONFIG,
      "Executing method updateOfferCriteriaFromXlsx, Parameters: suitabilityDTOs: {0}",
      suitabilityDTOs);

    // Create offer entity list (used for the ranking calculation).
    List<OfferEntity> offerEntities = new ArrayList<>();
    for (SuitabilityDTO suitabilityDTO : suitabilityDTOs) {
      OfferEntity offer = em.find(OfferEntity.class, suitabilityDTO.getOfferId());
      if (StringUtils.isNotBlank(suitabilityDTO.getqExSuitabilityNotes())) {
        // Only replace the qExSuitabilityNotes value if an input has been given.
        offer.setqExSuitabilityNotes(suitabilityDTO.getqExSuitabilityNotes());
      }
      for (CriterionLiteDTO criterionLiteDTO : suitabilityDTO.getMustCriterion()) {
        OfferCriterionEntity offerCriterionEntity =
          em.find(OfferCriterionEntity.class, criterionLiteDTO.getCriterionId());
        if (criterionLiteDTO.getIsFulfilled() != null) {
          // Only replace the isFulfilled value if an input has been given.
          offerCriterionEntity.setIsFulfilled(criterionLiteDTO.getIsFulfilled());
          em.merge(offerCriterionEntity);
        }
      }

      // Call function to update the offer subcriteria and to map the offer subcriteria total score
      // to the criterion id.
      Map<String, BigDecimal> offerSubcriteriaScoreToCriterionId =
        updateOfferSubcriteriaFromXlsx(suitabilityDTO.getOfferSubcriteria(), offer.getId());

      BigDecimal totalScore = BigDecimal.ZERO;
      for (CriterionLiteDTO criterionLiteDTO : suitabilityDTO.getEvaluatedCriterion()) {
        OfferCriterionEntity offerCriterionEntity =
          em.find(OfferCriterionEntity.class, criterionLiteDTO.getCriterionId());
        // Check if the criterion has no subcriteria.
        if (offerCriterionEntity.getCriterion().getSubcriteria().isEmpty()) {
          if (criterionLiteDTO.getGrade() != null) {
            // Only replace the grade value and update the score if a grade input has been given.
            offerCriterionEntity.setGrade(criterionLiteDTO.getGrade());
            offerCriterionEntity.setScore(criterionLiteDTO.getScore());
            em.merge(offerCriterionEntity);
          }
          if (offerCriterionEntity.getScore() != null) {
            // If the offerCriterionEntity score is not null, add the score to the total score.
            totalScore = totalScore.add(offerCriterionEntity.getScore());
          }
        } else {
          // Check if the total subcriteria score of the criterion is not null.
          if (offerSubcriteriaScoreToCriterionId
            .get(offerCriterionEntity.getCriterion().getId()) != null) {
            offerCriterionEntity.setGrade(offerSubcriteriaScoreToCriterionId
              .get(offerCriterionEntity.getCriterion().getId()));
            // criterionScore = criterionGrade * (criterionWeighting / 100)
            offerCriterionEntity.setScore(offerCriterionEntity.getGrade().multiply(
              BigDecimal.valueOf(offerCriterionEntity.getCriterion().getWeighting() / 100)));
            totalScore = totalScore.add(offerCriterionEntity.getScore());
          }
        }
      }

      offer.setqExTotalGrade(totalScore);
      // Add offer to the offer entity list.
      offerEntities.add(offer);
    }
    // Sort offers.
    Collections.sort(offerEntities, ComparatorUtil.sortOfferEntitiesWithRank);
    // Update the qExRank values.
    for (OfferEntity offerEntity : offerEntities) {
      offerEntity.setqExRank(Integer.valueOf(offerEntities.indexOf(offerEntity) + 1));
      offerEntity.getSubmittent().setSortOrder(offerEntity.getqExRank());
      em.merge(offerEntity);
    }
  }

  /**
   * Updates the suitability/examination or award offer subcriteria from the imported xlsx file.
   *
   * @param offerSubcriterionLiteDTOs the offer subcriterion lite DTOs
   * @param offerId the offer id
   * @return offer subcriteria total score to criterion id map
   */
  private Map<String, BigDecimal> updateOfferSubcriteriaFromXlsx(
    List<OfferSubcriterionLiteDTO> offerSubcriterionLiteDTOs, String offerId) {
    LOGGER.log(Level.CONFIG,
      "Executing method updateOfferSubcriteriaFromXlsx, Parameters: offerSubcriterionLiteDTOs: {0}, offerId: {1}",
      new Object[]{offerSubcriterionLiteDTOs, offerId});

    // Offer subcriteria total score to criterion id map.
    Map<String, BigDecimal> offerSubcriteriaScoreToCriterionId = new HashMap<>();
    // Add offerSubcriterionIds list where all the offer subcriterion ids are going to be stored.
    List<String> offerSubcriterionIds = new ArrayList<>();
    for (OfferSubcriterionLiteDTO offerSubcriterionLiteDTO : offerSubcriterionLiteDTOs) {
      offerSubcriterionIds.add(offerSubcriterionLiteDTO.getOfferSubcriterionId());
    }
    List<OfferSubcriterionEntity> offerSubcriterionEntities;
    // Get the offer subcriterion entities.
    if (offerId != null) {
      // Fetch offer subcriteria in case of suitability/examination.
      offerSubcriterionEntities = new JPAQueryFactory(em).selectFrom(qOfferSubriterionEntity)
        .where(qOfferSubriterionEntity.offer.id.eq(offerId)
          .and(qOfferSubriterionEntity.subcriterion.id.in(offerSubcriterionIds)))
        .fetch();
    } else {
      // Fetch offer subcriteria in case of award.
      offerSubcriterionEntities = new JPAQueryFactory(em).selectFrom(qOfferSubriterionEntity)
        .where(qOfferSubriterionEntity.id.in(offerSubcriterionIds)).fetch();
    }
    // Match the offerSubcriterionLiteDTOs with the offerSubcriterionEntities.
    for (OfferSubcriterionLiteDTO offerSubcriterionLiteDTO : offerSubcriterionLiteDTOs) {
      for (OfferSubcriterionEntity offerSubcriterionEntity : offerSubcriterionEntities) {
        // Check if the offerSubcriterionLiteDTO and offerSubcriterionEntity match. The checking
        // functionality changes if suitability/examination or award.
        if ((offerSubcriterionLiteDTO.getOfferSubcriterionId()
          .equals(offerSubcriterionEntity.getSubcriterion().getId()) && offerId != null)
          || (offerSubcriterionLiteDTO.getOfferSubcriterionId()
          .equals(offerSubcriterionEntity.getId()) && offerId == null)) {
          if (offerSubcriterionLiteDTO.getGrade() != null) {
            // Only replace the grade value and calculate the score if a grade input has been given.
            offerSubcriterionEntity.setGrade(offerSubcriterionLiteDTO.getGrade());
            offerSubcriterionEntity.setScore(offerSubcriterionLiteDTO.getScore());
            em.merge(offerSubcriterionEntity);
          }
          // Functionality to map the offer subcriteria total score to the criterion id.
          if (offerSubcriterionEntity.getScore() != null) {
            BigDecimal subcriteriaScore = offerSubcriteriaScoreToCriterionId
              .get(offerSubcriterionEntity.getSubcriterion().getCriterion().getId());
            if (subcriteriaScore == null) {
              subcriteriaScore = offerSubcriterionEntity.getScore();
            } else {
              subcriteriaScore = subcriteriaScore.add(offerSubcriterionEntity.getScore());
            }
            offerSubcriteriaScoreToCriterionId.put(
              offerSubcriterionEntity.getSubcriterion().getCriterion().getId(), subcriteriaScore);
          }
          break;
        }
      }
    }
    return offerSubcriteriaScoreToCriterionId;
  }

  @Override
  public void updateAwardOfferCriteriaFromXlsx(List<AwardAssessDTO> awardAssessDTOs) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateAwardOfferCriteriaFromXlsx, Parameters: awardAssessDTOs: {0}",
      awardAssessDTOs);

    // Create offer entity list (used for the ranking calculation).
    List<OfferEntity> offerEntities = new ArrayList<>();
    for (AwardAssessDTO awardAssessDTO : awardAssessDTOs) {
      OfferEntity offer = em.find(OfferEntity.class, awardAssessDTO.getOfferId());
      offer.setAwardRank(awardAssessDTO.getRank());

      // Call function to update the offer subcriteria and to map the offer subcriteria total score
      // to the criterion id.
      Map<String, BigDecimal> offerSubcriteriaScoreToCriterionId =
        updateOfferSubcriteriaFromXlsx(awardAssessDTO.getOfferSubcriteria(), null);

      BigDecimal totalScore = BigDecimal.ZERO;
      for (OfferCriterionLiteDTO offerCriterionLiteDTO : awardAssessDTO.getOfferCriteria()) {
        OfferCriterionEntity offerCriterionEntity =
          em.find(OfferCriterionEntity.class, offerCriterionLiteDTO.getOfferCriterionId());
        // Check if the criterion has no subcriteria.
        if (offerCriterionEntity.getCriterion().getSubcriteria().isEmpty()) {
          // Only update the offer criterion if it is not a price or operating cost criterion, as
          // these offer criterion values are calculated automatically and cannot be imported. Also
          // proceed with the update if a grade value has been given.
          if (!offerCriterionEntity.getCriterion().getCriterionType()
            .equals(LookupValues.PRICE_AWARD_CRITERION_TYPE)
            && !offerCriterionEntity.getCriterion().getCriterionType()
            .equals(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE)
            && offerCriterionLiteDTO.getGrade() != null) {
            offerCriterionEntity.setGrade(offerCriterionLiteDTO.getGrade());
            offerCriterionEntity.setScore(offerCriterionLiteDTO.getScore());
          }
          // If the offerCriterionEntity score is not null, add the score to the total score.
          if (offerCriterionEntity.getScore() != null) {
            totalScore = totalScore.add(offerCriterionEntity.getScore());
          }
        } else {
          // Check if the total subcriteria score of the criterion is not null.
          if (offerSubcriteriaScoreToCriterionId
            .get(offerCriterionEntity.getCriterion().getId()) != null) {
            offerCriterionEntity.setGrade(offerSubcriteriaScoreToCriterionId
              .get(offerCriterionEntity.getCriterion().getId()));
            // criterionScore = criterionGrade * (criterionWeighting / 100)
            offerCriterionEntity.setScore(offerCriterionEntity.getGrade().multiply(
              BigDecimal.valueOf(offerCriterionEntity.getCriterion().getWeighting() / 100)));
            totalScore = totalScore.add(offerCriterionEntity.getScore());
          }
        }
        em.merge(offerCriterionEntity);
      }
      offer.setAwardTotalScore(totalScore);
      offerEntities.add(offer);
    }
    // Assign award rank to offers and save the changes.
    rankOffers(offerEntities, TenderStatus.fromValue(submissionService.getCurrentStatusOfSubmission(
      offerEntities.get(0).getSubmittent().getSubmissionId().getId())));
  }

  @Override
  public void deleteOperatingCostCriterion(String submissionId) {
    LOGGER.log(Level.CONFIG,
      "Executing method deleteOperatingCostCriterion, Parameters: submissionId: {0}",
      submissionId);

    // Get the operating cost criterion.
    CriterionEntity operatingCostCriterion = new JPAQueryFactory(em).selectFrom(qCriterionEntity)
      .where(qCriterionEntity.submission.id.eq(submissionId),
        qCriterionEntity.criterionType.eq(LookupValues.OPERATING_COST_AWARD_CRITERION_TYPE))
      .fetchOne();
    // Check if the criterion actually exists.
    if (operatingCostCriterion != null) {
      boolean amountExists = false;
      // Get the submission offers.
      List<OfferDTO> offerDTOs = offerService.getOffersBySubmission(submissionId);
      for (OfferDTO offerDTO : offerDTOs) {
        // For every offer, check if the operating costs amount value is not null and not 0.
        if (offerDTO.getOperatingCostsAmount() != null
          && offerDTO.getOperatingCostsAmount().compareTo(BigDecimal.ZERO) != 0) {
          amountExists = true;
          break;
        }
      }
      // If no operating costs amount exists, delete the operating cost criterion.
      if (!amountExists) {
        deleteCriterionProcess(operatingCostCriterion);
      }
    }
  }

  @Override
  public Set<ValidationError> checkForChangesByOtherUsers(String submissionId,
    Timestamp pageRequestedOn) {

    LOGGER.log(Level.CONFIG,
      "Executing method checkForChangesByOtherUsers, Parameters: submissionId: {0}, "
        + "pageRequestedOn: {1}",
      new Object[]{submissionId, pageRequestedOn});

    Set<ValidationError> optimisticLockErrors = new HashSet<>();

    // Create a list with all timestamps
    List<Timestamp> timestamps = getAllTimestamps(submissionId);

    // Check if at least one timestamp from the list is after pageRequestedOn timestamp
    Optional<Timestamp> timestampCheck = timestamps.stream().filter(timestamp ->
      timestamp.after(pageRequestedOn)).findFirst();

    /*
     * If timestampCheck isPresent that means that
     * a criterion/subcriterion/offer criterion/offer subcriterion
     * is created or edited by another user
     */
    if (timestampCheck.isPresent()) {
      optimisticLockErrors
        .add(new ValidationError(ValidationMessages.OPTIMISTIC_LOCK_ERROR_FIELD,
          ValidationMessages.OPTIMISTIC_LOCK));
    }

    return optimisticLockErrors;
  }

  /**
   * Get all timestamps for optimistic lock checking.
   *
   * @param submissionId the submissionId
   * @return the timestamps
   */
  private List<Timestamp> getAllTimestamps(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getAllTimestamps, Parameters: submissionId: {0}",
      submissionId);

    List<Timestamp> timestamps = new ArrayList<>();
    // add the Criterion createdOn timestamps
    timestamps.addAll(getCriterionCreatedOnTimestamps(submissionId));
    // add the Criterion updatedOn timestamps
    timestamps.addAll(getSubCriterionCreatedOnTimestamps(submissionId));
    // add the Subcriterion createdOn timestamps
    timestamps.addAll(getCriterionUpdatedOnTimestamps(submissionId));
    // add the Subcriterion updatedOn timestamps
    timestamps.addAll(getSubCriterionUpdatedOnTimestamps(submissionId));
    // add the offer Criterion createdOn timestamps
    timestamps.addAll(getOfferCriterionCreatedOnTimestamps(submissionId));
    // add the offer Criterion updatedOn timestamps
    timestamps.addAll(getOfferCriterionUpdatedOnTimestamps(submissionId));

    /*
     * In order to proceed with adding the offer Subcriterion createdOn/updatedOn timestamps
     * we have to retrieve the criterionIds and subCriterionIds based on the submissionId first.
     */
    List<String> criterionIds = getCriterionIds(submissionId);
    List<String> subCriterionIds = getSubCriterionIds(criterionIds);

    // add the offer Subcriterion createdOn timestamps
    timestamps.addAll(getOfferSubCriterionCreatedOnTimestamps(subCriterionIds));
    // add the offer Subcriterion updatedOn timestamps
    timestamps.addAll(getOfferSubCriterionUpdatedOnTimestamps(subCriterionIds));

    return timestamps;
  }

  /**
   * Get the criterion createdOn timestamps.
   *
   * @param submissionId the submissionId
   * @return the timestamps
   */
  private List<Timestamp> getCriterionCreatedOnTimestamps(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getCriterionCreatedOnTimestamps, Parameters: submissionId: {0}",
      submissionId);

    return new JPAQueryFactory(em).select(qCriterionEntity.createdOn).from(qCriterionEntity)
      .where(
        qCriterionEntity.submission.id.eq(submissionId).and(qCriterionEntity.createdOn.isNotNull()))
      .fetch();
  }

  /**
   * Get the criterion updatedOn timestamps.
   *
   * @param submissionId the submissionId
   * @return the timestamps
   */
  private List<Timestamp> getCriterionUpdatedOnTimestamps(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getCriterionUpdatedOnTimestamps, Parameters: submissionId: {0}",
      submissionId);

    return new JPAQueryFactory(em).select(qCriterionEntity.updatedOn).from(qCriterionEntity)
      .where(
        qCriterionEntity.submission.id.eq(submissionId).and(qCriterionEntity.updatedOn.isNotNull()))
      .fetch();
  }

  /**
   * Get the sub criterion createdOn timestamps.
   *
   * @param submissionId the submissionId
   * @return the timestamps
   */
  private List<Timestamp> getSubCriterionCreatedOnTimestamps(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getSubCriterionCreatedOnTimestamps, Parameters: submissionId: {0}",
      submissionId);

    return new JPAQueryFactory(em).select(qSubcriterionEntity.createdOn).from(qSubcriterionEntity)
      .where(qSubcriterionEntity.criterion.id.in(
        new JPAQueryFactory(em).select(qCriterionEntity.id).from(qCriterionEntity)
          .where(qCriterionEntity.submission.id.eq(submissionId)))
        .and(qSubcriterionEntity.createdOn.isNotNull()))
      .fetch();
  }

  /**
   * Get the sub criterion updatedOn timestamps.
   *
   * @param submissionId the submissionId
   * @return the timestamps
   */
  private List<Timestamp> getSubCriterionUpdatedOnTimestamps(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getSubCriterionUpdatedOnTimestamps, Parameters: submissionId: {0}",
      submissionId);

    return new JPAQueryFactory(em).select(qSubcriterionEntity.updatedOn).from(qSubcriterionEntity)
      .where(qSubcriterionEntity.criterion.id.in(
        new JPAQueryFactory(em).select(qCriterionEntity.id).from(qCriterionEntity)
          .where(qCriterionEntity.submission.id.eq(submissionId)))
        .and(qSubcriterionEntity.updatedOn.isNotNull()))
      .fetch();
  }

  /**
   * Get the offer criterion createdOn timestamps based on the submission id.
   *
   * @param submissionId the submissionId
   * @return the timestamps
   */
  private List<Timestamp> getOfferCriterionCreatedOnTimestamps(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getOfferCriterionCreatedOnTimestamps, Parameters: submissionId: {0}",
      submissionId);

    return new JPAQueryFactory(em).select(qOfferCriterionEntity.createdOn).from(qOfferCriterionEntity)
      .where(qOfferCriterionEntity.criterion.id.in(
        new JPAQueryFactory(em).select(qCriterionEntity.id).from(qCriterionEntity)
          .where(qCriterionEntity.submission.id.eq(submissionId)))
        .and(qOfferCriterionEntity.createdOn.isNotNull()))
      .fetch();
  }

  /**
   * Get the offer criterion updatedOn timestamps based on the submission id.
   *
   * @param submissionId the submissionId
   * @return the timestamps
   */
  private List<Timestamp> getOfferCriterionUpdatedOnTimestamps(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getOfferCriterionUpdatedOnTimestamps, Parameters: submissionId: {0}",
      submissionId);

    return new JPAQueryFactory(em).select(qOfferCriterionEntity.updatedOn).from(qOfferCriterionEntity)
      .where(qOfferCriterionEntity.criterion.id.in(
        new JPAQueryFactory(em).select(qCriterionEntity.id).from(qCriterionEntity)
          .where(qCriterionEntity.submission.id.eq(submissionId)))
        .and(qOfferCriterionEntity.updatedOn.isNotNull()))
      .fetch();
  }

  /**
   * Get the offer criterion createdOn timestamps based on the subCriterionIds.
   *
   * @param subCriterionIds the subCriterionIds
   * @return the timestamps
   */
  private List<Timestamp> getOfferSubCriterionCreatedOnTimestamps(List<String> subCriterionIds) {

    LOGGER.log(Level.CONFIG,
      "Executing method getOfferSubCriterionCreatedOnTimestamps, Parameters: subCriterionIds: {0}",
      subCriterionIds);

    return new JPAQueryFactory(em).select(qOfferSubriterionEntity.createdOn).from(qOfferSubriterionEntity)
      .where(qOfferSubriterionEntity.subcriterion.id.in(subCriterionIds)
        .and(qOfferSubriterionEntity.createdOn.isNotNull()))
      .fetch();
  }

  /**
   * Get the offer criterion updatedOn timestamps based on the subCriterionIds.
   *
   * @param subCriterionIds the subCriterionIds
   * @return the timestamps
   */
  private List<Timestamp> getOfferSubCriterionUpdatedOnTimestamps(List<String> subCriterionIds) {

    LOGGER.log(Level.CONFIG,
      "Executing method getOfferSubCriterionUpdatedOnTimestamps, Parameters: subCriterionIds: {0}",
      subCriterionIds);

    return new JPAQueryFactory(em).select(qOfferSubriterionEntity.updatedOn).from(qOfferSubriterionEntity)
      .where(qOfferSubriterionEntity.subcriterion.id.in(subCriterionIds)
        .and(qOfferSubriterionEntity.updatedOn.isNotNull()))
      .fetch();
  }

  /**
   * Get the criterion ids based on the submission id.
   *
   * @param submissionId the submissionId
   * @return the criterionIds
   */
  private List<String> getCriterionIds(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getCriterionIds, Parameters: submissionId: {0}",
      submissionId);

    return new JPAQueryFactory(em).select(qCriterionEntity.id).from(qCriterionEntity)
      .where(qCriterionEntity.submission.id.eq(submissionId))
      .fetch();
  }

  /**
   * Get the sub criterion ids based on the criterion ids.
   *
   * @param criterionIds the criterionIds
   * @return the subCriterionIds
   */
  private List<String> getSubCriterionIds(List<String> criterionIds) {

    LOGGER.log(Level.CONFIG,
      "Executing method getSubCriterionIds, Parameters: criterionIds: {0}",
      criterionIds);

    return new JPAQueryFactory(em).select(qSubcriterionEntity.id).from(qSubcriterionEntity)
      .where(qSubcriterionEntity.criterion.id.in(criterionIds))
      .fetch();
  }

  @Override
  public boolean criterionExists(String criterionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method criterionExists, Parameters: criterionId: {0}",
      criterionId);

    CriterionEntity criterionEntity = em.find(CriterionEntity.class, criterionId);
    return criterionEntity != null;
  }

  @Override
  public boolean subCriterionExists(String subCriterionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method subCriterionExists, Parameters: subCriterionId: {0}",
      subCriterionId);

    SubcriterionEntity subcriterionEntity = em.find(SubcriterionEntity.class, subCriterionId);
    return subcriterionEntity != null;
  }

  @Override
  public boolean offerCriterionExists(String offerCriterionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method offerCriterionExists, Parameters: offerCriterionId: {0}",
      offerCriterionId);

    OfferCriterionEntity offerCriterionEntity =
      em.find(OfferCriterionEntity.class, offerCriterionId);
    return offerCriterionEntity != null;
  }

  @Override
  public Set<ValidationError> suitabilityCheckForChangesByOtherUsers(String submissionId,
    Timestamp pageRequestedOn, List<SuitabilityDTO> suitabilityDTOs) {

    LOGGER.log(Level.CONFIG,
      "Executing method suitabilityCheckForChangesByOtherUsers, Parameters: submissionId: {0}, "
        + "pageRequestedOn: {1}, suitabilityDTOs: {2}",
      new Object[]{submissionId, pageRequestedOn, suitabilityDTOs});

    Set<ValidationError> optimisticLockErrors = new HashSet<>();

    // First, check if at least one criterion/subcriterion was created or updated by another user
    boolean checkSavedCriteria = !checkForChangesByOtherUsers(submissionId, pageRequestedOn)
      .isEmpty();

    // Then check if at least one offer criterion/subcriterion was deleted by another user
    Optional<SuitabilityDTO> checkDeletedCriteria = suitabilityDTOs.stream()
      .filter(suitabilityDTO -> {
        boolean checkMustCriteria = suitabilityDTO.getMustCriterion().stream()
          .anyMatch(criterionLiteDTO -> !offerCriterionExists(criterionLiteDTO.getCriterionId()));
        boolean checkEvaluatedCriteria = suitabilityDTO.getMustCriterion().stream()
          .anyMatch(criterionLiteDTO -> !offerCriterionExists(criterionLiteDTO.getCriterionId()));
        boolean checkSubCriteria = suitabilityDTO.getOfferSubcriteria().stream()
          /*
           * For some reason the OfferSubCriterion ID is mapped with the SubCriterion ID in the db.
           * So we call subCriterionExists to compare the given IDs with the SubCriterion IDs in the db.
           */
          .anyMatch(criterionLiteDTO ->
            !subCriterionExists(criterionLiteDTO.getOfferSubcriterionId()));
        return checkMustCriteria || checkEvaluatedCriteria || checkSubCriteria;
      })
      .findFirst();

    if (checkSavedCriteria || checkDeletedCriteria.isPresent()) {
      optimisticLockErrors
        .add(new ValidationError(ValidationMessages.OPTIMISTIC_LOCK_ERROR_FIELD, ValidationMessages.OPTIMISTIC_LOCK));
    }
    return optimisticLockErrors;
  }

  @Override
  public Set<ValidationError> examinationLockedByAnotherUser(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method examinationLockedByAnotherUser, Parameters: submissionId: {0}",
      submissionId);

    Set<ValidationError> optimisticLockErrors = new HashSet<>();
    SubmissionDTO submission = submissionService.getSubmissionById(submissionId);
    if (submission.getExaminationIsLocked() != null
      && submission.getExaminationIsLocked()) {
      optimisticLockErrors
        .add(new ValidationError(ValidationMessages.OPTIMISTIC_LOCK_ERROR_FIELD,
          ValidationMessages.EXAMINATION_LOCKED));
    }
    return optimisticLockErrors;
  }

  @Override
  public Set<ValidationError> awardLockedByAnotherUser(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method awardLockedByAnotherUser, Parameters: submissionId: {0}",
      submissionId);

    Set<ValidationError> optimisticLockErrors = new HashSet<>();
    SubmissionDTO submission = submissionService.getSubmissionById(submissionId);
    if (submission.getAwardIsLocked() != null
      && submission.getAwardIsLocked()) {
      optimisticLockErrors
        .add(new ValidationError(ValidationMessages.OPTIMISTIC_LOCK_ERROR_FIELD,
          ValidationMessages.AWARD_LOCKED));
    }
    return optimisticLockErrors;
  }

  @Override
  public Set<ValidationError> awardCheckForChangesByOtherUsers(String submissionId,
    Timestamp pageRequestedOn) {

    LOGGER.log(Level.CONFIG,
      "Executing method awardCheckForChangesByOtherUsers, Parameters: submissionId: {0}, "
        + "pageRequestedOn: {1}",
      new Object[]{submissionId, pageRequestedOn});

    Set<ValidationError> optimisticLockErrors = new HashSet<>();

    // First, check if at least one criterion/subcriterion was created or updated by another user
    boolean checkSavedCriteria = !checkForChangesByOtherUsers(submissionId, pageRequestedOn)
      .isEmpty();
    // Then, check if submission is updated by another user comparing with the pageRequestedOn timestamp
    Timestamp submissionUpdatedOn = getSubmissionUpdatedOnTimestamp(submissionId);
    boolean isSubmissionUpdated = pageRequestedOn.before(submissionUpdatedOn);

    if (checkSavedCriteria || isSubmissionUpdated) {
      optimisticLockErrors
        .add(new ValidationError(ValidationMessages.OPTIMISTIC_LOCK_ERROR_FIELD,
          ValidationMessages.OPTIMISTIC_LOCK));
    }

    return optimisticLockErrors;
  }

  /**
   * Get the submission updatedOn timestamp.
   *
   * @param submissionId the submissionId
   * @return the timestamp
   */
  private Timestamp getSubmissionUpdatedOnTimestamp(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getSubmissionUpdatedOnTimestamp, Parameters: submissionId: {0}",
      submissionId);

    return new JPAQueryFactory(em).select(qSubmissionEntity.updatedOn)
      .from(qSubmissionEntity)
      .where(qSubmissionEntity.id.eq(submissionId)
        .and(qSubmissionEntity.updatedOn.isNotNull()))
      .fetchOne();
  }

  /**
   * Gets the version of submission.
   *
   * @param submissionId the submissionId
   * @return the version
   */
  private Long getSubmissionVersion(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getSubmissionVersion, Parameters: submissionId: {0}",
      submissionId);

    return new JPAQueryFactory(em).select(qSubmissionEntity.version)
      .from(qSubmissionEntity)
      .where(qSubmissionEntity.id.eq(submissionId))
      .fetchOne();
  }

  @Override
  public Set<ValidationError> examinationCheckForChangesByOtherUsers(String submissionId,
    Timestamp pageRequestedOn, Long submissionVersion) {

    LOGGER.log(Level.CONFIG,
      "Executing method formalAuditCheckForChangesByOtherUsers, Parameters: submissionId: {0}, "
        + "pageRequestedOn: {1}, submissionVersion: {2}",
      new Object[]{submissionId, pageRequestedOn, submissionVersion});

    Set<ValidationError> optimisticLockErrors = new HashSet<>();

    // First, check if at least one criterion/subcriterion was created or updated by another user
    boolean checkSavedCriteria = !checkForChangesByOtherUsers(submissionId, pageRequestedOn)
      .isEmpty();
    // Then check if the submission version is changed by another user
    Long currentSubmissionVersion = getSubmissionVersion(submissionId);
    boolean isSubmissionUpdated = !currentSubmissionVersion.equals(submissionVersion);

    if (checkSavedCriteria || isSubmissionUpdated) {
      optimisticLockErrors
        .add(new ValidationError(ValidationMessages.OPTIMISTIC_LOCK_ERROR_FIELD,
          ValidationMessages.OPTIMISTIC_LOCK));
    }

    return optimisticLockErrors;
  }

  @Override
  public void awardEvaluationViewSecurityCheck(String submissionId) {
    security.isPermittedOperationForUser(getUserId(),
      submissionService.getSubmissionProcess(submissionId).getValue() + LookupValues.DOT +
        SecurityOperation.AWARD_EVALUATION_VIEW.getValue(), submissionId);
  }

  @Override
  public void awardEvaluationEditSecurityCheck(String submissionId) {
    security.isPermittedOperationForUser(getUserId(),
      submissionService.getSubmissionProcess(submissionId).getValue() + LookupValues.DOT +
        SecurityOperation.AWARD_EVALUATION_EDIT.getValue(), submissionId);
  }

  /**
   * Get SubCriteria from a specific offer
   * @param offerId
   * @return
   */
  private List<OfferSubcriterionEntity> getSubCriteriaByOfferId(String offerId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getSubCriteria, Parameters: offerId: {0}",
      offerId);

    return new JPAQueryFactory(em).select(qOfferSubriterionEntity).from(qOfferSubriterionEntity)
      .where(qOfferSubriterionEntity.offer.id.in(offerId))
      .fetch();
  }
}
