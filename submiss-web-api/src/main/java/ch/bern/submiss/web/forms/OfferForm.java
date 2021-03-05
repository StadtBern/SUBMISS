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

import ch.bern.submiss.services.api.dto.OfferCriterionDTO;
import ch.bern.submiss.services.api.dto.OfferSubcriterionDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * The Class OfferForm.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OfferForm extends AbstractOfferForm {

  /**
   * The submittent.
   */
  private SubmittentForm submittent;

  /**
   * The is awarded.
   */
  private Boolean isAwarded;

  /**
   * The offer date.
   */
  private Date offerDate;

  /**
   * The is part offer.
   */
  private Boolean isPartOffer;

  /**
   * The is excluded from process.
   */
  private Boolean isExcludedFromProcess;

  /**
   * The excluded first level.
   */
  private Boolean excludedFirstLevel;

  /**
   * The is variant.
   */
  private Boolean isVariant;

  /**
   * The settlement.
   */
  private MasterListValueHistoryForm settlement;

  /**
   * The gross amount corrected.
   */
  private Double grossAmountCorrected;

  /**
   * The is corrected.
   */
  private Boolean isCorrected;

  /**
   * The discount 2 days.
   */
  private Integer discount2Days;

  /**
   * The price increase.
   */
  private String priceIncrease;

  /**
   * The modified on.
   */
  private Date modifiedOn;

  /**
   * The rank.
   */
  private Integer rank;

  /**
   * The variant notes.
   */
  private String variantNotes;

  /**
   * The is empty offer.
   */
  private Boolean isEmptyOffer;

  /**
   * The application date.
   */
  private Date applicationDate;

  /**
   * The ancilliary amount gross.
   */
  private Double ancilliaryAmountGross;

  /**
   * The is ancilliary amount percentage.
   */
  private Boolean isAncilliaryAmountPercentage;

  /**
   * The ancilliary amount vat.
   */
  private String ancilliaryAmountVat;

  /**
   * The operating cost gross.
   */
  private Double operatingCostGross;

  /**
   * The operating cost notes.
   */
  private String operatingCostNotes;

  /**
   * The operating cost gross corrected.
   */
  private Double operatingCostGrossCorrected;

  /**
   * The is operating cost corrected.
   */
  private Boolean isOperatingCostCorrected;

  /**
   * The operating cost discount.
   */
  private Double operatingCostDiscount;

  /**
   * The is operating cost discount percentage.
   */
  private Boolean isOperatingCostDiscountPercentage;

  /**
   * The operating cost discount 2.
   */
  private Double operatingCostDiscount2;

  /**
   * The is operating cost discount 2 percentage.
   */
  private Boolean isOperatingCostDiscount2Percentage;

  /**
   * The operating cost vat.
   */
  private Double operatingCostVat;

  /**
   * The operating cost is vat percentage.
   */
  private Boolean operatingCostIsVatPercentage;

  /**
   * The migreated PM.
   */
  private String migreatedPM;

  /**
   * The migrated department.
   */
  private String migratedDepartment;

  /**
   * The migrated submission.
   */
  private String migratedSubmission;

  /**
   * The migrated project.
   */
  private String migratedProject;

  /**
   * The from migration.
   */
  private Boolean fromMigration;

  /**
   * The discount in percentage.
   */
  private BigDecimal discountInPercentage;

  /**
   * The discount 2 in percentage.
   */
  private BigDecimal discount2InPercentage;

  /**
   * The operating costs in percentage.
   */
  private BigDecimal operatingCostsInPercentage;

  /**
   * The building costs in percentage.
   */
  private BigDecimal buildingCostsInPercentage;

  /**
   * The is default offer.
   */
  private Boolean isDefaultOffer;

  /**
   * The q ex total grade.
   */
  private BigDecimal qExTotalGrade;

  /**
   * The q ex status.
   */
  private Boolean qExStatus;

  /**
   * The q ex examination is fulfilled.
   */
  private Boolean qExExaminationIsFulfilled;

  /**
   * The q ex suitability notes.
   */
  private String qExSuitabilityNotes;

  /**
   * The offer criteria.
   */
  private List<OfferCriterionDTO> offerCriteria;

  /**
   * The offer subcriteria.
   */
  private List<OfferSubcriterionDTO> offerSubcriteria;

  /**
   * The award rank.
   */
  private Integer awardRank;

  /**
   * The award total score.
   */
  private BigDecimal awardTotalScore;

  /**
   * The operating costs amount.
   */
  private BigDecimal operatingCostsAmount;

  /**
   * The q ex rank.
   */
  private Integer qExRank;

  /**
   * The award recipient free text field.
   */
  private String awardRecipientFreeTextField;

  // This field is not in OfferEntity, it is used for the award form to display excluded offers
  /**
   * The is excluded from award process.
   */
  // grayed out and is set under certain cases.
  private Boolean isExcludedFromAwardProcess;

  /**
   * The application information.
   */
  private String applicationInformation;

  /**
   * The is nachtrag submittent.
   */
  private Boolean isNachtragSubmittent;

  /**
   * Gets the submittent.
   *
   * @return the submittent
   */
  public SubmittentForm getSubmittent() {
    return submittent;
  }

  /**
   * Sets the submittent.
   *
   * @param submittent the new submittent
   */
  public void setSubmittent(SubmittentForm submittent) {
    this.submittent = submittent;
  }

  /**
   * Gets the checks if is awarded.
   *
   * @return the checks if is awarded
   */
  public Boolean getIsAwarded() {
    return isAwarded;
  }

  /**
   * Sets the checks if is awarded.
   *
   * @param isAwarded the new checks if is awarded
   */
  public void setIsAwarded(Boolean isAwarded) {
    this.isAwarded = isAwarded;
  }

  /**
   * Gets the offer date.
   *
   * @return the offer date
   */
  public Date getOfferDate() {
    return offerDate;
  }

  /**
   * Sets the offer date.
   *
   * @param offerDate the new offer date
   */
  public void setOfferDate(Date offerDate) {
    this.offerDate = offerDate;
  }

  /**
   * Gets the checks if is part offer.
   *
   * @return the checks if is part offer
   */
  public Boolean getIsPartOffer() {
    return isPartOffer;
  }

  /**
   * Sets the checks if is part offer.
   *
   * @param isPartOffer the new checks if is part offer
   */
  public void setIsPartOffer(Boolean isPartOffer) {
    this.isPartOffer = isPartOffer;
  }

  /**
   * Gets the checks if is excluded from process.
   *
   * @return the checks if is excluded from process
   */
  public Boolean getIsExcludedFromProcess() {
    return isExcludedFromProcess;
  }

  /**
   * Sets the checks if is excluded from process.
   *
   * @param isExcludedFromProcess the new checks if is excluded from process
   */
  public void setIsExcludedFromProcess(Boolean isExcludedFromProcess) {
    this.isExcludedFromProcess = isExcludedFromProcess;
  }

  /**
   * Gets the checks if is variant.
   *
   * @return the checks if is variant
   */
  public Boolean getIsVariant() {
    return isVariant;
  }

  /**
   * Sets the checks if is variant.
   *
   * @param isVariant the new checks if is variant
   */
  public void setIsVariant(Boolean isVariant) {
    this.isVariant = isVariant;
  }

  /**
   * Gets the settlement.
   *
   * @return the settlement
   */
  public MasterListValueHistoryForm getSettlement() {
    return settlement;
  }

  /**
   * Sets the settlement.
   *
   * @param settlement the new settlement
   */
  public void setSettlement(MasterListValueHistoryForm settlement) {
    this.settlement = settlement;
  }

  /**
   * Gets the gross amount corrected.
   *
   * @return the gross amount corrected
   */
  public Double getGrossAmountCorrected() {
    return grossAmountCorrected;
  }

  /**
   * Sets the gross amount corrected.
   *
   * @param grossAmountCorrected the new gross amount corrected
   */
  public void setGrossAmountCorrected(Double grossAmountCorrected) {
    this.grossAmountCorrected = grossAmountCorrected;
  }

  /**
   * Gets the checks if is corrected.
   *
   * @return the checks if is corrected
   */
  public Boolean getIsCorrected() {
    return isCorrected;
  }

  /**
   * Sets the checks if is corrected.
   *
   * @param isCorrected the new checks if is corrected
   */
  public void setIsCorrected(Boolean isCorrected) {
    this.isCorrected = isCorrected;
  }

  /**
   * Gets the discount 2 days.
   *
   * @return the discount 2 days
   */
  public Integer getDiscount2Days() {
    return discount2Days;
  }

  /**
   * Sets the discount 2 days.
   *
   * @param discount2Days the new discount 2 days
   */
  public void setDiscount2Days(Integer discount2Days) {
    this.discount2Days = discount2Days;
  }

  /**
   * Gets the price increase.
   *
   * @return the price increase
   */
  public String getPriceIncrease() {
    return priceIncrease;
  }

  /**
   * Sets the price increase.
   *
   * @param priceIncrease the new price increase
   */
  public void setPriceIncrease(String priceIncrease) {
    this.priceIncrease = priceIncrease;
  }

  /**
   * Gets the modified on.
   *
   * @return the modified on
   */
  public Date getModifiedOn() {
    return modifiedOn;
  }

  /**
   * Sets the modified on.
   *
   * @param modifiedOn the new modified on
   */
  public void setModifiedOn(Date modifiedOn) {
    this.modifiedOn = modifiedOn;
  }

  /**
   * Gets the rank.
   *
   * @return the rank
   */
  public Integer getRank() {
    return rank;
  }

  /**
   * Sets the rank.
   *
   * @param rank the new rank
   */
  public void setRank(Integer rank) {
    this.rank = rank;
  }

  /**
   * Gets the variant notes.
   *
   * @return the variant notes
   */
  public String getVariantNotes() {
    return variantNotes;
  }

  /**
   * Sets the variant notes.
   *
   * @param variantNotes the new variant notes
   */
  public void setVariantNotes(String variantNotes) {
    this.variantNotes = variantNotes;
  }

  /**
   * Gets the checks if is empty offer.
   *
   * @return the checks if is empty offer
   */
  public Boolean getIsEmptyOffer() {
    return isEmptyOffer;
  }

  /**
   * Sets the checks if is empty offer.
   *
   * @param isEmptyOffer the new checks if is empty offer
   */
  public void setIsEmptyOffer(Boolean isEmptyOffer) {
    this.isEmptyOffer = isEmptyOffer;
  }

  /**
   * Gets the application date.
   *
   * @return the application date
   */
  public Date getApplicationDate() {
    return applicationDate;
  }

  /**
   * Sets the application date.
   *
   * @param applicationDate the new application date
   */
  public void setApplicationDate(Date applicationDate) {
    this.applicationDate = applicationDate;
  }

  /**
   * Gets the ancilliary amount gross.
   *
   * @return the ancilliary amount gross
   */
  public Double getAncilliaryAmountGross() {
    return ancilliaryAmountGross;
  }

  /**
   * Sets the ancilliary amount gross.
   *
   * @param ancilliaryAmountGross the new ancilliary amount gross
   */
  public void setAncilliaryAmountGross(Double ancilliaryAmountGross) {
    this.ancilliaryAmountGross = ancilliaryAmountGross;
  }

  /**
   * Gets the checks if is ancilliary amount percentage.
   *
   * @return the checks if is ancilliary amount percentage
   */
  public Boolean getIsAncilliaryAmountPercentage() {
    return isAncilliaryAmountPercentage;
  }

  /**
   * Sets the checks if is ancilliary amount percentage.
   *
   * @param isAncilliaryAmountPercentage the new checks if is ancilliary amount percentage
   */
  public void setIsAncilliaryAmountPercentage(Boolean isAncilliaryAmountPercentage) {
    this.isAncilliaryAmountPercentage = isAncilliaryAmountPercentage;
  }

  /**
   * Gets the ancilliary amount vat.
   *
   * @return the ancilliary amount vat
   */
  public String getAncilliaryAmountVat() {
    return ancilliaryAmountVat;
  }

  /**
   * Sets the ancilliary amount vat.
   *
   * @param ancilliaryAmountVat the new ancilliary amount vat
   */
  public void setAncilliaryAmountVat(String ancilliaryAmountVat) {
    this.ancilliaryAmountVat = ancilliaryAmountVat;
  }

  /**
   * Gets the operating cost gross.
   *
   * @return the operating cost gross
   */
  public Double getOperatingCostGross() {
    return operatingCostGross;
  }

  /**
   * Sets the operating cost gross.
   *
   * @param operatingCostGross the new operating cost gross
   */
  public void setOperatingCostGross(Double operatingCostGross) {
    this.operatingCostGross = operatingCostGross;
  }

  /**
   * Gets the operating cost notes.
   *
   * @return the operating cost notes
   */
  public String getOperatingCostNotes() {
    return operatingCostNotes;
  }

  /**
   * Sets the operating cost notes.
   *
   * @param operatingCostNotes the new operating cost notes
   */
  public void setOperatingCostNotes(String operatingCostNotes) {
    this.operatingCostNotes = operatingCostNotes;
  }

  /**
   * Gets the operating cost gross corrected.
   *
   * @return the operating cost gross corrected
   */
  public Double getOperatingCostGrossCorrected() {
    return operatingCostGrossCorrected;
  }

  /**
   * Sets the operating cost gross corrected.
   *
   * @param operatingCostGrossCorrected the new operating cost gross corrected
   */
  public void setOperatingCostGrossCorrected(Double operatingCostGrossCorrected) {
    this.operatingCostGrossCorrected = operatingCostGrossCorrected;
  }

  /**
   * Gets the checks if is operating cost corrected.
   *
   * @return the checks if is operating cost corrected
   */
  public Boolean getIsOperatingCostCorrected() {
    return isOperatingCostCorrected;
  }

  /**
   * Sets the checks if is operating cost corrected.
   *
   * @param isOperatingCostCorrected the new checks if is operating cost corrected
   */
  public void setIsOperatingCostCorrected(Boolean isOperatingCostCorrected) {
    this.isOperatingCostCorrected = isOperatingCostCorrected;
  }

  /**
   * Gets the operating cost discount.
   *
   * @return the operating cost discount
   */
  public Double getOperatingCostDiscount() {
    return operatingCostDiscount;
  }

  /**
   * Sets the operating cost discount.
   *
   * @param operatingCostDiscount the new operating cost discount
   */
  public void setOperatingCostDiscount(Double operatingCostDiscount) {
    this.operatingCostDiscount = operatingCostDiscount;
  }

  /**
   * Gets the checks if is operating cost discount percentage.
   *
   * @return the checks if is operating cost discount percentage
   */
  public Boolean getIsOperatingCostDiscountPercentage() {
    return isOperatingCostDiscountPercentage;
  }

  /**
   * Sets the checks if is operating cost discount percentage.
   *
   * @param isOperatingCostDiscountPercentage the new checks if is operating cost discount
   *                                          percentage
   */
  public void setIsOperatingCostDiscountPercentage(Boolean isOperatingCostDiscountPercentage) {
    this.isOperatingCostDiscountPercentage = isOperatingCostDiscountPercentage;
  }

  /**
   * Gets the operating cost discount 2.
   *
   * @return the operating cost discount 2
   */
  public Double getOperatingCostDiscount2() {
    return operatingCostDiscount2;
  }

  /**
   * Sets the operating cost discount 2.
   *
   * @param operatingCostDiscount2 the new operating cost discount 2
   */
  public void setOperatingCostDiscount2(Double operatingCostDiscount2) {
    this.operatingCostDiscount2 = operatingCostDiscount2;
  }

  /**
   * Gets the checks if is operating cost discount 2 percentage.
   *
   * @return the checks if is operating cost discount 2 percentage
   */
  public Boolean getIsOperatingCostDiscount2Percentage() {
    return isOperatingCostDiscount2Percentage;
  }

  /**
   * Sets the checks if is operating cost discount 2 percentage.
   *
   * @param isOperatingCostDiscount2Percentage the new checks if is operating cost discount 2
   *                                           percentage
   */
  public void setIsOperatingCostDiscount2Percentage(Boolean isOperatingCostDiscount2Percentage) {
    this.isOperatingCostDiscount2Percentage = isOperatingCostDiscount2Percentage;
  }

  /**
   * Gets the operating cost vat.
   *
   * @return the operating cost vat
   */
  public Double getOperatingCostVat() {
    return operatingCostVat;
  }

  /**
   * Sets the operating cost vat.
   *
   * @param operatingCostVat the new operating cost vat
   */
  public void setOperatingCostVat(Double operatingCostVat) {
    this.operatingCostVat = operatingCostVat;
  }

  /**
   * Gets the operating cost is vat percentage.
   *
   * @return the operating cost is vat percentage
   */
  public Boolean getOperatingCostIsVatPercentage() {
    return operatingCostIsVatPercentage;
  }

  /**
   * Sets the operating cost is vat percentage.
   *
   * @param operatingCostIsVatPercentage the new operating cost is vat percentage
   */
  public void setOperatingCostIsVatPercentage(Boolean operatingCostIsVatPercentage) {
    this.operatingCostIsVatPercentage = operatingCostIsVatPercentage;
  }

  /**
   * Gets the migreated PM.
   *
   * @return the migreated PM
   */
  public String getMigreatedPM() {
    return migreatedPM;
  }

  /**
   * Sets the migreated PM.
   *
   * @param migreatedPM the new migreated PM
   */
  public void setMigreatedPM(String migreatedPM) {
    this.migreatedPM = migreatedPM;
  }

  /**
   * Gets the migrated department.
   *
   * @return the migrated department
   */
  public String getMigratedDepartment() {
    return migratedDepartment;
  }

  /**
   * Sets the migrated department.
   *
   * @param migratedDepartment the new migrated department
   */
  public void setMigratedDepartment(String migratedDepartment) {
    this.migratedDepartment = migratedDepartment;
  }

  /**
   * Gets the migrated submission.
   *
   * @return the migrated submission
   */
  public String getMigratedSubmission() {
    return migratedSubmission;
  }

  /**
   * Sets the migrated submission.
   *
   * @param migratedSubmission the new migrated submission
   */
  public void setMigratedSubmission(String migratedSubmission) {
    this.migratedSubmission = migratedSubmission;
  }

  /**
   * Gets the migrated project.
   *
   * @return the migrated project
   */
  public String getMigratedProject() {
    return migratedProject;
  }

  /**
   * Sets the migrated project.
   *
   * @param migratedProject the new migrated project
   */
  public void setMigratedProject(String migratedProject) {
    this.migratedProject = migratedProject;
  }

  /**
   * Gets the discount in percentage.
   *
   * @return the discount in percentage
   */
  public BigDecimal getDiscountInPercentage() {
    return discountInPercentage;
  }

  /**
   * Sets the discount in percentage.
   *
   * @param discountInPercentage the new discount in percentage
   */
  public void setDiscountInPercentage(BigDecimal discountInPercentage) {
    this.discountInPercentage = discountInPercentage;
  }

  /**
   * Gets the discount 2 in percentage.
   *
   * @return the discount 2 in percentage
   */
  public BigDecimal getDiscount2InPercentage() {
    return discount2InPercentage;
  }

  /**
   * Sets the discount 2 in percentage.
   *
   * @param discount2InPercentage the new discount 2 in percentage
   */
  public void setDiscount2InPercentage(BigDecimal discount2InPercentage) {
    this.discount2InPercentage = discount2InPercentage;
  }

  /**
   * Gets the operating costs in percentage.
   *
   * @return the operating costs in percentage
   */
  public BigDecimal getOperatingCostsInPercentage() {
    return operatingCostsInPercentage;
  }

  /**
   * Sets the operating costs in percentage.
   *
   * @param operatingCostsInPercentage the new operating costs in percentage
   */
  public void setOperatingCostsInPercentage(BigDecimal operatingCostsInPercentage) {
    this.operatingCostsInPercentage = operatingCostsInPercentage;
  }

  /**
   * Gets the building costs in percentage.
   *
   * @return the building costs in percentage
   */
  public BigDecimal getBuildingCostsInPercentage() {
    return buildingCostsInPercentage;
  }

  /**
   * Sets the building costs in percentage.
   *
   * @param buildingCostsInPercentage the new building costs in percentage
   */
  public void setBuildingCostsInPercentage(BigDecimal buildingCostsInPercentage) {
    this.buildingCostsInPercentage = buildingCostsInPercentage;
  }

  /**
   * Gets the operating costs amount.
   *
   * @return the operating costs amount
   */
  public BigDecimal getOperatingCostsAmount() {
    return operatingCostsAmount;
  }

  /**
   * Sets the operating costs amount.
   *
   * @param operatingCostsAmount the new operating costs amount
   */
  public void setOperatingCostsAmount(BigDecimal operatingCostsAmount) {
    this.operatingCostsAmount = operatingCostsAmount;
  }

  /**
   * Gets the award recipient free text field.
   *
   * @return the award recipient free text field
   */
  public String getAwardRecipientFreeTextField() {
    return awardRecipientFreeTextField;
  }

  /**
   * Sets the award recipient free text field.
   *
   * @param awardRecipientFreeTextField the new award recipient free text field
   */
  public void setAwardRecipientFreeTextField(String awardRecipientFreeTextField) {
    this.awardRecipientFreeTextField = awardRecipientFreeTextField;
  }

  /**
   * Gets the from migration.
   *
   * @return the from migration
   */
  public Boolean getFromMigration() {
    return fromMigration;
  }

  /**
   * Sets the from migration.
   *
   * @param fromMigration the new from migration
   */
  public void setFromMigration(Boolean fromMigration) {
    this.fromMigration = fromMigration;
  }

  /**
   * Gets the checks if is default offer.
   *
   * @return the checks if is default offer
   */
  public Boolean getIsDefaultOffer() {
    return isDefaultOffer;
  }

  /**
   * Sets the checks if is default offer.
   *
   * @param isDefaultOffer the new checks if is default offer
   */
  public void setIsDefaultOffer(Boolean isDefaultOffer) {
    this.isDefaultOffer = isDefaultOffer;
  }

  /**
   * Gets the q ex total grade.
   *
   * @return the q ex total grade
   */
  public BigDecimal getqExTotalGrade() {
    return qExTotalGrade;
  }

  /**
   * Sets the q ex total grade.
   *
   * @param qExTotalGrade the new q ex total grade
   */
  public void setqExTotalGrade(BigDecimal qExTotalGrade) {
    this.qExTotalGrade = qExTotalGrade;
  }

  /**
   * Gets the q ex status.
   *
   * @return the q ex status
   */
  public Boolean getqExStatus() {
    return qExStatus;
  }

  /**
   * Sets the q ex status.
   *
   * @param qExStatus the new q ex status
   */
  public void setqExStatus(Boolean qExStatus) {
    this.qExStatus = qExStatus;
  }

  /**
   * Gets the q ex examination is fulfilled.
   *
   * @return the q ex examination is fulfilled
   */
  public Boolean getqExExaminationIsFulfilled() {
    return qExExaminationIsFulfilled;
  }

  /**
   * Sets the q ex examination is fulfilled.
   *
   * @param qExExaminationIsFulfilled the new q ex examination is fulfilled
   */
  public void setqExExaminationIsFulfilled(Boolean qExExaminationIsFulfilled) {
    this.qExExaminationIsFulfilled = qExExaminationIsFulfilled;
  }

  /**
   * Gets the q ex suitability notes.
   *
   * @return the q ex suitability notes
   */
  public String getqExSuitabilityNotes() {
    return qExSuitabilityNotes;
  }

  /**
   * Sets the q ex suitability notes.
   *
   * @param qExSuitabilityNotes the new q ex suitability notes
   */
  public void setqExSuitabilityNotes(String qExSuitabilityNotes) {
    this.qExSuitabilityNotes = qExSuitabilityNotes;
  }

  /**
   * Gets the offer criteria.
   *
   * @return the offer criteria
   */
  public List<OfferCriterionDTO> getOfferCriteria() {
    return offerCriteria;
  }

  /**
   * Sets the offer criteria.
   *
   * @param offerCriteria the new offer criteria
   */
  public void setOfferCriteria(List<OfferCriterionDTO> offerCriteria) {
    this.offerCriteria = offerCriteria;
  }

  /**
   * Gets the offer subcriteria.
   *
   * @return the offer subcriteria
   */
  public List<OfferSubcriterionDTO> getOfferSubcriteria() {
    return offerSubcriteria;
  }

  /**
   * Sets the offer subcriteria.
   *
   * @param offerSubcriteria the new offer subcriteria
   */
  public void setOfferSubcriteria(List<OfferSubcriterionDTO> offerSubcriteria) {
    this.offerSubcriteria = offerSubcriteria;
  }

  /**
   * Gets the award rank.
   *
   * @return the award rank
   */
  public Integer getAwardRank() {
    return awardRank;
  }

  /**
   * Sets the award rank.
   *
   * @param awardRank the new award rank
   */
  public void setAwardRank(Integer awardRank) {
    this.awardRank = awardRank;
  }

  /**
   * Gets the award total score.
   *
   * @return the award total score
   */
  public BigDecimal getAwardTotalScore() {
    return awardTotalScore;
  }

  /**
   * Sets the award total score.
   *
   * @param awardTotalScore the new award total score
   */
  public void setAwardTotalScore(BigDecimal awardTotalScore) {
    this.awardTotalScore = awardTotalScore;
  }

  /**
   * Gets the q ex rank.
   *
   * @return the q ex rank
   */
  public Integer getqExRank() {
    return qExRank;
  }

  /**
   * Sets the q ex rank.
   *
   * @param qExRank the new q ex rank
   */
  public void setqExRank(Integer qExRank) {
    this.qExRank = qExRank;
  }

  /**
   * Gets the checks if is excluded from award process.
   *
   * @return the checks if is excluded from award process
   */
  public Boolean getIsExcludedFromAwardProcess() {
    return isExcludedFromAwardProcess;
  }

  /**
   * Sets the checks if is excluded from award process.
   *
   * @param isExcludedFromAwardProcess the new checks if is excluded from award process
   */
  public void setIsExcludedFromAwardProcess(Boolean isExcludedFromAwardProcess) {
    this.isExcludedFromAwardProcess = isExcludedFromAwardProcess;
  }

  /**
   * Gets the application information.
   *
   * @return the application information
   */
  public String getApplicationInformation() {
    return applicationInformation;
  }

  /**
   * Sets the application information.
   *
   * @param applicationInformation the new application information
   */
  public void setApplicationInformation(String applicationInformation) {
    this.applicationInformation = applicationInformation;
  }

  /**
   * Gets the excluded first level.
   *
   * @return the excluded first level
   */
  public Boolean getExcludedFirstLevel() {
    return excludedFirstLevel;
  }

  /**
   * Sets the excluded first level.
   *
   * @param excludedFirstLevel the new excluded first level
   */
  public void setExcludedFirstLevel(Boolean excludedFirstLevel) {
    this.excludedFirstLevel = excludedFirstLevel;
  }

  public Boolean getNachtragSubmittent() {
    return isNachtragSubmittent;
  }

  public void setNachtragSubmittent(Boolean nachtragSubmittent) {
    isNachtragSubmittent = nachtragSubmittent;
  }
}
