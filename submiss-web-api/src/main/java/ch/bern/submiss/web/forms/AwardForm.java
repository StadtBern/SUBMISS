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

package ch.bern.submiss.web.forms;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import ch.bern.submiss.services.api.dto.MasterListValueHistoryDTO;

/**
 * The Class AwardForm.
 */
public class AwardForm {

  /** The submission id. */
  private String submissionId;

  /** The criterion. */
  private List<CriterionForm> criterion;

  /** The price formula. */
  private MasterListValueHistoryDTO priceFormula;

  /** The operating cost formula. */
  private MasterListValueHistoryDTO operatingCostFormula;

  /** The evaluation through. */
  private String evaluationThrough;

  /** The added award recipients. */
  private BigDecimal addedAwardRecipients;

  /** The award min grade. */
  private BigDecimal awardMinGrade;

  /** The award max grade. */
  private BigDecimal awardMaxGrade;

  /** The custom price formula. */
  private String customPriceFormula;

  /** The custom operating cost formula. */
  private String customOperatingCostFormula;

  /** The offers. */
  private List<OfferForm> offers;

  /**
   * The pageRequestedOn timestamp.
   */
  private Timestamp pageRequestedOn;

  /**
   * Gets the submission id.
   *
   * @return the submission id
   */
  public String getSubmissionId() {
    return submissionId;
  }

  /**
   * Sets the submission id.
   *
   * @param submissionId the new submission id
   */
  public void setSubmissionId(String submissionId) {
    this.submissionId = submissionId;
  }

  /**
   * Gets the criterion.
   *
   * @return the criterion
   */
  public List<CriterionForm> getCriterion() {
    return criterion;
  }

  /**
   * Sets the criterion.
   *
   * @param criterion the new criterion
   */
  public void setCriterion(List<CriterionForm> criterion) {
    this.criterion = criterion;
  }

  /**
   * Gets the price formula.
   *
   * @return the price formula
   */
  public MasterListValueHistoryDTO getPriceFormula() {
    return priceFormula;
  }

  /**
   * Sets the price formula.
   *
   * @param priceFormula the new price formula
   */
  public void setPriceFormula(MasterListValueHistoryDTO priceFormula) {
    this.priceFormula = priceFormula;
  }

  /**
   * Gets the operating cost formula.
   *
   * @return the operating cost formula
   */
  public MasterListValueHistoryDTO getOperatingCostFormula() {
    return operatingCostFormula;
  }

  /**
   * Sets the operating cost formula.
   *
   * @param operatingCostFormula the new operating cost formula
   */
  public void setOperatingCostFormula(MasterListValueHistoryDTO operatingCostFormula) {
    this.operatingCostFormula = operatingCostFormula;
  }

  /**
   * Gets the evaluation through.
   *
   * @return the evaluation through
   */
  public String getEvaluationThrough() {
    return evaluationThrough;
  }

  /**
   * Sets the evaluation through.
   *
   * @param evaluationThrough the new evaluation through
   */
  public void setEvaluationThrough(String evaluationThrough) {
    this.evaluationThrough = evaluationThrough;
  }

  /**
   * Gets the added award recipients.
   *
   * @return the added award recipients
   */
  public BigDecimal getAddedAwardRecipients() {
    return addedAwardRecipients;
  }

  /**
   * Sets the added award recipients.
   *
   * @param addedAwardRecipients the new added award recipients
   */
  public void setAddedAwardRecipients(BigDecimal addedAwardRecipients) {
    this.addedAwardRecipients = addedAwardRecipients;
  }

  /**
   * Gets the award min grade.
   *
   * @return the award min grade
   */
  public BigDecimal getAwardMinGrade() {
    return awardMinGrade;
  }

  /**
   * Sets the award min grade.
   *
   * @param awardMinGrade the new award min grade
   */
  public void setAwardMinGrade(BigDecimal awardMinGrade) {
    this.awardMinGrade = awardMinGrade;
  }

  /**
   * Gets the award max grade.
   *
   * @return the award max grade
   */
  public BigDecimal getAwardMaxGrade() {
    return awardMaxGrade;
  }

  /**
   * Sets the award max grade.
   *
   * @param awardMaxGrade the new award max grade
   */
  public void setAwardMaxGrade(BigDecimal awardMaxGrade) {
    this.awardMaxGrade = awardMaxGrade;
  }

  /**
   * Gets the custom price formula.
   *
   * @return the custom price formula
   */
  public String getCustomPriceFormula() {
    return customPriceFormula;
  }

  /**
   * Sets the custom price formula.
   *
   * @param customPriceFormula the new custom price formula
   */
  public void setCustomPriceFormula(String customPriceFormula) {
    this.customPriceFormula = customPriceFormula;
  }

  /**
   * Gets the custom operating cost formula.
   *
   * @return the custom operating cost formula
   */
  public String getCustomOperatingCostFormula() {
    return customOperatingCostFormula;
  }

  /**
   * Sets the custom operating cost formula.
   *
   * @param customOperatingCostFormula the new custom operating cost formula
   */
  public void setCustomOperatingCostFormula(String customOperatingCostFormula) {
    this.customOperatingCostFormula = customOperatingCostFormula;
  }

  /**
   * Gets the offers.
   *
   * @return the offers
   */
  public List<OfferForm> getOffers() {
    return offers;
  }

  /**
   * Sets the offers.
   *
   * @param offers the new offers
   */
  public void setOffers(List<OfferForm> offers) {
    this.offers = offers;
  }

  /**
   * Gets the pageRequestedOn.
   *
   * @return the pageRequestedOn
   */
  public Timestamp getPageRequestedOn() {
    return pageRequestedOn;
  }

  /**
   * Sets pageRequestedOn as the timestamp of the GET request.
   *
   * @param pageRequestedOn the pageRequestedOn
   */
  public void setPageRequestedOn(Timestamp pageRequestedOn) {
    this.pageRequestedOn = pageRequestedOn;
  }
}
