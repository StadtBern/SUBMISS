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

package ch.bern.submiss.services.api.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import ch.bern.submiss.services.api.util.SubmissConverter;
import ch.bern.submiss.services.api.util.View;

/**
 * The Class OfferDTO.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OfferDTO {

  /** The id. */
  @JsonView(View.Public.class)
  private String id;

  /** The submittent. */
  @JsonView(View.Public.class)
  private SubmittentDTO submittent;

  /** The is awarded. */
  @JsonView(View.Internal.class)
  private Boolean isAwarded;

  /** The offer date. */
  @JsonView(View.Internal.class)
  private Date offerDate;

  /** The is part offer. */
  @JsonView(View.Internal.class)
  private Boolean isPartOffer;

  /** The is excluded from process. */
  @JsonView(View.Internal.class)
  private Boolean isExcludedFromProcess;

  /** The excluded first level. */
  @JsonView(View.Internal.class)
  private Boolean excludedFirstLevel;

  /** The is variant. */
  @JsonView(View.Public.class)
  private Boolean isVariant;

  /** The settlement. */
  @JsonView(View.Internal.class)
  private MasterListValueHistoryDTO settlement;

  /** The gross amount. */
  @JsonView(View.Internal.class)
  private Double grossAmount;

  /** The gross amount corrected. */
  @JsonView(View.Internal.class)
  private Double grossAmountCorrected;

  /** The is corrected. */
  @JsonView(View.Internal.class)
  private Boolean isCorrected;

  /** The discount. */
  @JsonView(View.Internal.class)
  private Double discount;

  /** The is discount percentage. */
  @JsonView(View.Internal.class)
  private Boolean isDiscountPercentage;

  /** The vat. */
  @JsonView(View.Internal.class)
  private Double vat;

  /** The discount 2. */
  @JsonView(View.Internal.class)
  private Double discount2;

  /** The is discount 2 percentage. */
  @JsonView(View.Internal.class)
  private Boolean isDiscount2Percentage;

  /** The discount 2 days. */
  @JsonView(View.Internal.class)
  private Integer discount2Days;

  /** The price increase. */
  @JsonView(View.Internal.class)
  private String priceIncrease;

  /** The modified on. */
  @JsonView(View.Internal.class)
  private Date modifiedOn;

  /** The notes. */
  @JsonView(View.Internal.class)
  private String notes;

  /** The rank. */
  @JsonView(View.Public.class)
  private Integer rank;

  /** The variant notes. */
  @JsonView(View.Internal.class)
  private String variantNotes;

  /** The is empty offer. */
  @JsonView(View.Public.class)
  private Boolean isEmptyOffer;

  /** The application date. */
  @JsonView(View.Internal.class)
  private Date applicationDate;

  /** The is vat percentage. */
  @JsonView(View.Internal.class)
  private Boolean isVatPercentage;

  /** The building costs. */
  @JsonView(View.Internal.class)
  private Double buildingCosts;

  /** The is building costs percentage. */
  @JsonView(View.Internal.class)
  private Boolean isBuildingCostsPercentage;

  /** The ancilliary amount gross. */
  @JsonView(View.Internal.class)
  private Double ancilliaryAmountGross;

  /** The is ancilliary amount percentage. */
  @JsonView(View.Internal.class)
  private Boolean isAncilliaryAmountPercentage;

  /** The ancilliary amount vat. */
  @JsonView(View.Internal.class)
  private String ancilliaryAmountVat;

  /** The operating cost gross. */
  @JsonView(View.Internal.class)
  private Double operatingCostGross;

  /** The operating cost notes. */
  @JsonView(View.Internal.class)
  private String operatingCostNotes;

  /** The operating cost gross corrected. */
  @JsonView(View.Internal.class)
  private Double operatingCostGrossCorrected;

  /** The is operating cost corrected. */
  @JsonView(View.Internal.class)
  private Boolean isOperatingCostCorrected;

  /** The operating cost discount. */
  @JsonView(View.Internal.class)
  private Double operatingCostDiscount;

  /** The is operating cost discount percentage. */
  @JsonView(View.Internal.class)
  private Boolean isOperatingCostDiscountPercentage;

  /** The operating cost discount 2. */
  @JsonView(View.Internal.class)
  private Double operatingCostDiscount2;

  /** The is operating cost discount 2 percentage. */
  @JsonView(View.Internal.class)
  private Boolean isOperatingCostDiscount2Percentage;

  /** The operating cost vat. */
  @JsonView(View.Internal.class)
  private Double operatingCostVat;

  /** The operating cost is vat percentage. */
  @JsonView(View.Internal.class)
  private Boolean operatingCostIsVatPercentage;

  /** The migreated PM. */
  @JsonView(View.Internal.class)
  private String migreatedPM;

  /** The migrated procedure. */
  @JsonView(View.Internal.class)
  private String migratedProcedure;

  /** The migrated department. */
  @JsonView(View.Internal.class)
  private String migratedDepartment;

  /** The migrated submission. */
  @JsonView(View.Internal.class)
  private String migratedSubmission;

  /** The migrated project. */
  @JsonView(View.Internal.class)
  private String migratedProject;

  /** The from migration. */
  @JsonView(View.Internal.class)
  private Boolean fromMigration;

  /** The amount. */
  @JsonView(View.Internal.class)
  private BigDecimal amount;

  /** The discount in percentage. */
  @JsonView(View.Internal.class)
  private BigDecimal discountInPercentage;

  /** The discount 2 in percentage. */
  @JsonView(View.Internal.class)
  private BigDecimal discount2InPercentage;

  /** The operating costs in percentage. */
  @JsonView(View.Internal.class)
  private BigDecimal operatingCostsInPercentage;

  /** The building costs in percentage. */
  @JsonView(View.Internal.class)
  private BigDecimal buildingCostsInPercentage;

  /** The is default offer. */
  @JsonView(View.Internal.class)
  private Boolean isDefaultOffer;

  /** The q ex total grade. */
  @JsonView(View.Public.class)
  private BigDecimal qExTotalGrade;

  /** The q ex status. */
  @JsonView(View.Public.class)
  private Boolean qExStatus;

  /** The q ex examination is fulfilled. */
  @JsonView(View.Public.class)
  private Boolean qExExaminationIsFulfilled;

  /** The q ex suitability notes. */
  @JsonView(View.Public.class)
  private String qExSuitabilityNotes;

  /** The offer criteria. */
  @JsonView(View.Public.class)
  private List<OfferCriterionDTO> offerCriteria;

  /** The offer subcriteria. */
  @JsonView(View.Public.class)
  private List<OfferSubcriterionDTO> offerSubcriteria;

  /** The award rank. */
  @JsonView(View.Public.class)
  private Integer awardRank;

  /** The award total score. */
  @JsonView(View.Public.class)
  private BigDecimal awardTotalScore;

  /** The operating costs amount. */
  @JsonView(View.Internal.class)
  private BigDecimal operatingCostsAmount;

  /** The q ex rank. */
  @JsonView(View.Public.class)
  private Integer qExRank;

  /** The award recipient free text field. */
  @JsonView(View.Public.class)
  private String awardRecipientFreeTextField;

  /** The is excluded from award process. */
  /*
   * this field is not in OfferEntity, it is used for the award form to display excluded offers
   * grayed out and is set under certain cases
   */
  @JsonView(View.Public.class)
  private Boolean isExcludedFromAwardProcess;

  /** The created on. */
  @JsonView(View.Internal.class)
  private Timestamp createdOn;

  /** The created by. */
  @JsonView(View.Internal.class)
  private String createdBy;

  /** The application information. */
  @JsonView(View.Internal.class)
  private String applicationInformation;

  /** The exclusion reason. */
  @JsonView(View.Internal.class)
  private String exclusionReason;

  /** The exclusion reasons. */
  @JsonView(View.Internal.class)
  private Set<MasterListValueHistoryDTO> exclusionReasons;

  /** Amount gross amount or gross amount corrected. */
  @JsonView(View.Internal.class)
  BigDecimal grossAmountOrCorrected;

  /** the percentage of discount. */
  @JsonView(View.Internal.class)
  BigDecimal discountPercentage;

  /** the percentage of discount2. */
  @JsonView(View.Internal.class)
  BigDecimal discount2Percentage;

  /** the percentage of vat. */
  @JsonView(View.Internal.class)
  BigDecimal vatPercentage;

  /** the amount of discount after percentage value. */
  @JsonView(View.Internal.class)
  BigDecimal discountAmountWithPercentage;

  /** the amount of discount2 after percentage value. */
  @JsonView(View.Internal.class)
  BigDecimal discount2AmountWithPercentage;

  /** the amount of Mwst Inclusive. */
  @JsonView(View.Internal.class)
  BigDecimal amountInclusive;

  /** the amount mwst Incl minus discount. */
  @JsonView(View.Internal.class)
  BigDecimal discountBetweenTotal;

  /** the amount mwst Incl minus discount and discount2. */
  @JsonView(View.Internal.class)
  BigDecimal discount2BetweenTotal;

  /** the amount of vat after percentage value. */
  @JsonView(View.Internal.class)
  BigDecimal vatAmountWithPercentage;

  /** the amount of vat after percentage value. */
  @JsonView(View.Internal.class)
  BigDecimal ancilliaryVatAmountWithPercentage;

  /** the amount of ancilliary amount inclusive. */
  @JsonView(View.Internal.class)
  BigDecimal ancilliaryAmountIncl;

  /** the amount of vat after percentage value. */
  @JsonView(View.Internal.class)
  BigDecimal ancilliaryGrossAmountOrCorrected;

  /** the amount of vat after percentage value. */
  @JsonView(View.Internal.class)
  BigDecimal ancilliaryVatPercentage;
  
  @JsonView(View.Internal.class)
  BigDecimal buildingCostsPercentage;

  /** The exclusion reason first level. */
  @JsonView(View.Internal.class)
  private String exclusionReasonFirstLevel;

  /** The exclusion reasons first level. */
  @JsonView(View.Internal.class)
  private Set<MasterListValueHistoryDTO> exclusionReasonsFirstLevel;

  /**
   * Gets the gross amount or corrected.
   *
   * @return the gross amount or corrected
   */
  public BigDecimal getGrossAmountOrCorrected() {
    if (getIsCorrected() && getIsCorrected() != null) {
      grossAmountOrCorrected = BigDecimal.valueOf(getGrossAmountCorrected());
    } else {
      grossAmountOrCorrected = BigDecimal.valueOf(getGrossAmount());
    }
    return grossAmountOrCorrected;
  }


  /**
   * Gets the discount percentage.
   *
   * @return the discount percentage
   */
  public BigDecimal getDiscountPercentage() {
    if (getIsDiscountPercentage()) {
      discountPercentage = BigDecimal.valueOf(getDiscount());
    } else {
      discountPercentage = BigDecimal.ZERO;
    }
    return discountPercentage;
  }

  /**
   * Gets the discount amount with percentage.
   *
   * @return the discount amount with percentage
   */
  public BigDecimal getDiscountAmountWithPercentage() {
    if (grossAmountOrCorrected != null && getDiscountPercentage() != null) {
      if (getIsDiscountPercentage() != null && getIsDiscountPercentage()) {
        discountAmountWithPercentage =
            (grossAmountOrCorrected.divide(new BigDecimal(100))).multiply(getDiscountPercentage());
      } else {
        discountAmountWithPercentage = BigDecimal.valueOf(getDiscount());
      }
      return discountAmountWithPercentage;
    } else {
      return BigDecimal.ZERO;
    }
  }

  /**
   * Gets the discount between total.
   *
   * @return the discount between total
   */
  public BigDecimal getDiscountBetweenTotal() {
    if (getGrossAmountOrCorrected() != null && getDiscountPercentage() != null) {
      if (getIsDiscountPercentage() != null && getIsDiscountPercentage()) {
        discountBetweenTotal = getGrossAmountOrCorrected().subtract((getGrossAmountOrCorrected()
            .divide(new BigDecimal(100)).multiply(getDiscountPercentage())));
      } else {
        discountBetweenTotal =
            getGrossAmountOrCorrected().subtract(BigDecimal.valueOf(getDiscount()));
      }
      return discountBetweenTotal;
    } else {
      return BigDecimal.ZERO;
    }
  }

  /**
   * Gets the discount 2 percentage.
   *
   * @return the discount 2 percentage
   */
  public BigDecimal getDiscount2Percentage() {
    if (getIsDiscount2Percentage() != null && getIsDiscount2Percentage()) {
      discount2Percentage = BigDecimal.valueOf(getDiscount2());
    } else {
      discount2Percentage = BigDecimal.ZERO;
    }
    return discount2Percentage;
  }
  
  public BigDecimal getBuildingCostsPercentage() {
      return BigDecimal.valueOf(getBuildingCosts());
  }

  /**
   * Gets the discount 2 amount with percentage.
   *
   * @return the discount 2 amount with percentage
   */
  public BigDecimal getDiscount2AmountWithPercentage() {
    if (getDiscountBetweenTotal() != null && getDiscount2Percentage() != null) {
      if (getIsDiscount2Percentage() != null && getIsDiscount2Percentage()) {
        discount2AmountWithPercentage = (getDiscountBetweenTotal().divide(new BigDecimal(100)))
            .multiply(getDiscount2Percentage());
      } else {
        discount2AmountWithPercentage = BigDecimal.valueOf(getDiscount2());
      }
      return discount2AmountWithPercentage;
    } else {
      return BigDecimal.ZERO;
    }
  }

  /**
   * Gets the discount 2 between total.
   *
   * @return the discount 2 between total
   */
  public BigDecimal getDiscount2BetweenTotal() {
    if (getDiscountBetweenTotal() != null && getDiscount2Percentage() != null) {
      if (getIsDiscount2Percentage() != null && getIsDiscount2Percentage()) {
        discount2BetweenTotal = getDiscountBetweenTotal()
            .subtract(((getDiscountBetweenTotal().divide(new BigDecimal(100)))
                .multiply(getDiscount2Percentage())));
      } else {
        discount2BetweenTotal =
            getDiscountBetweenTotal().subtract(BigDecimal.valueOf(getDiscount2()));
      }
      return discount2BetweenTotal;
    } else {
      return BigDecimal.ZERO;
    }
  }

  /**
   * Gets the vat percentage.
   *
   * @return the vat percentage
   */
  public BigDecimal getVatPercentage() {
    if (getVat() != null) {
      if (getIsVatPercentage() != null && getIsVatPercentage()) {
        vatPercentage = BigDecimal.valueOf(getVat());
      } else {
        vatPercentage = BigDecimal.ZERO;
      }
      return vatPercentage;
    } else {
      return BigDecimal.ZERO;
    }
  }

  /**
   * Gets the vat amount with percentage.
   *
   * @return the vat amount with percentage
   */
  public BigDecimal getVatAmountWithPercentage() {
    if (getDiscount2BetweenTotal() != null && getVatPercentage() != null) {
      if (getIsVatPercentage() != null && getIsVatPercentage()) {
        vatAmountWithPercentage =
            ((getDiscount2BetweenTotal().divide(new BigDecimal(100))).multiply(getVatPercentage()));
      } else {
        vatAmountWithPercentage = BigDecimal.valueOf(getVat());
      }
      return vatAmountWithPercentage;
    } else {
      return BigDecimal.ZERO;
    }
  }

  /**
   * Gets the amount inclusive.
   *
   * @return the amount inclusive
   */
  public BigDecimal getAmountInclusive() {
    if (getAmount() != null && getVatAmountWithPercentage() != null) {
      amountInclusive = getAmount().add(getVatAmountWithPercentage());
      return SubmissConverter.customRoundNumber(amountInclusive);
    } else {
      return BigDecimal.ZERO;
    }
  }

  /**
   * Gets the ancilliary gross amount or corrected.
   *
   * @return the ancilliary gross amount or corrected
   */
  public BigDecimal getAncilliaryGrossAmountOrCorrected() {
    if (getGrossAmountCorrected() != null && getAncilliaryAmountGross() != null) {
      if (getIsAncilliaryAmountPercentage() != null && getIsAncilliaryAmountPercentage()) {
        ancilliaryGrossAmountOrCorrected = (getGrossAmountOrCorrected().divide(new BigDecimal(100)))
            .multiply(BigDecimal.valueOf(getAncilliaryAmountGross()));
      } else {
        ancilliaryGrossAmountOrCorrected = BigDecimal.valueOf(getAncilliaryAmountGross());
      }
      return ancilliaryGrossAmountOrCorrected;
    } else {
      return BigDecimal.ZERO;
    }
  }

  /**
   * Gets the ancilliary vat percentage.
   *
   * @return the ancilliary vat percentage
   */
  public BigDecimal getAncilliaryVatPercentage() {
    if (getAncilliaryGrossAmountOrCorrected() != null && getAncilliaryAmountVat() != null) {
      if (getIsAncilliaryAmountPercentage() != null && getIsAncilliaryAmountPercentage()) {
        ancilliaryVatPercentage = new BigDecimal(getAncilliaryAmountVat());
        return ancilliaryVatPercentage;
      } else {
        return BigDecimal.ZERO;
      }
    } else {
      return BigDecimal.ZERO;
    }
  }

  /**
   * Gets the ancilliary vat amount with percentage.
   *
   * @return the ancilliary vat amount with percentage
   */
  public BigDecimal getAncilliaryVatAmountWithPercentage() {
    if (getAncilliaryGrossAmountOrCorrected() != null && getAncilliaryAmountVat() != null) {
      ancilliaryVatAmountWithPercentage =
          (getAncilliaryGrossAmountOrCorrected().divide(new BigDecimal(100)))
              .multiply(new BigDecimal(getAncilliaryAmountVat()));
      return ancilliaryVatAmountWithPercentage;
    } else {
      return BigDecimal.ZERO;
    }
  }

  /**
   * Gets the ancilliary amount incl.
   *
   * @return the ancilliary amount incl
   */
  public BigDecimal getAncilliaryAmountIncl() {
    if (getAncilliaryGrossAmountOrCorrected() != null
        && getAncilliaryVatAmountWithPercentage() != null) {
      ancilliaryAmountIncl =
          getAncilliaryGrossAmountOrCorrected().add(getAncilliaryVatAmountWithPercentage());
      return ancilliaryAmountIncl;
    } else {
      return BigDecimal.ZERO;
    }
  }

  /**
   * Gets the id.
   *
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the id.
   *
   * @param id the new id
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the submittent.
   *
   * @return the submittent
   */
  public SubmittentDTO getSubmittent() {
    return submittent;
  }

  /**
   * Sets the submittent.
   *
   * @param submittent the new submittent
   */
  public void setSubmittent(SubmittentDTO submittent) {
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
  public MasterListValueHistoryDTO getSettlement() {
    return settlement;
  }

  /**
   * Sets the settlement.
   *
   * @param settlement the new settlement
   */
  public void setSettlement(MasterListValueHistoryDTO settlement) {
    this.settlement = settlement;
  }

  /**
   * Gets the gross amount.
   *
   * @return the gross amount
   */
  public Double getGrossAmount() {
    return grossAmount;
  }

  /**
   * Sets the gross amount.
   *
   * @param grossAmount the new gross amount
   */
  public void setGrossAmount(Double grossAmount) {
    this.grossAmount = grossAmount;
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
   * Gets the discount.
   *
   * @return the discount
   */
  public Double getDiscount() {
    return discount;
  }

  /**
   * Sets the discount.
   *
   * @param discount the new discount
   */
  public void setDiscount(Double discount) {
    this.discount = discount;
  }

  /**
   * Gets the checks if is discount percentage.
   *
   * @return the checks if is discount percentage
   */
  public Boolean getIsDiscountPercentage() {
    return isDiscountPercentage;
  }

  /**
   * Sets the checks if is discount percentage.
   *
   * @param isDiscountPercentage the new checks if is discount percentage
   */
  public void setIsDiscountPercentage(Boolean isDiscountPercentage) {
    this.isDiscountPercentage = isDiscountPercentage;
  }

  /**
   * Gets the vat.
   *
   * @return the vat
   */
  public Double getVat() {
    return vat;
  }

  /**
   * Sets the vat.
   *
   * @param vat the new vat
   */
  public void setVat(Double vat) {
    this.vat = vat;
  }

  /**
   * Gets the discount 2.
   *
   * @return the discount 2
   */
  public Double getDiscount2() {
    return discount2;
  }

  /**
   * Sets the discount 2.
   *
   * @param discount2 the new discount 2
   */
  public void setDiscount2(Double discount2) {
    this.discount2 = discount2;
  }

  /**
   * Gets the checks if is discount 2 percentage.
   *
   * @return the checks if is discount 2 percentage
   */
  public Boolean getIsDiscount2Percentage() {
    return isDiscount2Percentage;
  }

  /**
   * Sets the checks if is discount 2 percentage.
   *
   * @param isDiscount2Percentage the new checks if is discount 2 percentage
   */
  public void setIsDiscount2Percentage(Boolean isDiscount2Percentage) {
    this.isDiscount2Percentage = isDiscount2Percentage;
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
   * Gets the notes.
   *
   * @return the notes
   */
  public String getNotes() {
    return notes;
  }

  /**
   * Sets the notes.
   *
   * @param notes the new notes
   */
  public void setNotes(String notes) {
    this.notes = notes;
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
   * Gets the checks if is vat percentage.
   *
   * @return the checks if is vat percentage
   */
  public Boolean getIsVatPercentage() {
    return isVatPercentage;
  }

  /**
   * Sets the checks if is vat percentage.
   *
   * @param isVatPercentage the new checks if is vat percentage
   */
  public void setIsVatPercentage(Boolean isVatPercentage) {
    this.isVatPercentage = isVatPercentage;
  }

  /**
   * Gets the building costs.
   *
   * @return the building costs
   */
  public Double getBuildingCosts() {
    return buildingCosts;
  }

  /**
   * Sets the building costs.
   *
   * @param buildingCosts the new building costs
   */
  public void setBuildingCosts(Double buildingCosts) {
    this.buildingCosts = buildingCosts;
  }

  /**
   * Gets the checks if is building costs percentage.
   *
   * @return the checks if is building costs percentage
   */
  public Boolean getIsBuildingCostsPercentage() {
    return isBuildingCostsPercentage;
  }

  /**
   * Sets the checks if is building costs percentage.
   *
   * @param isBuildingCostsPercentage the new checks if is building costs percentage
   */
  public void setIsBuildingCostsPercentage(Boolean isBuildingCostsPercentage) {
    this.isBuildingCostsPercentage = isBuildingCostsPercentage;
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
   *        percentage
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
   *        percentage
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
   * Gets the amount.
   *
   * @return the amount
   */
  public BigDecimal getAmount() {
    return amount;
  }

  /**
   * Sets the amount.
   *
   * @param amount the new amount
   */
  public void setAmount(BigDecimal amount) {
    this.amount = amount;
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
   * Gets the migrated procedure.
   *
   * @return the migrated procedure
   */
  public String getMigratedProcedure() {
    return migratedProcedure;
  }

  /**
   * Sets the migrated procedure.
   *
   * @param migratedProcedure the new migrated procedure
   */
  public void setMigratedProcedure(String migratedProcedure) {
    this.migratedProcedure = migratedProcedure;
  }


  /**
   * Gets the created on.
   *
   * @return the created on
   */
  public Timestamp getCreatedOn() {
    return createdOn;
  }

  /**
   * Sets the created on.
   *
   * @param createdOn the new created on
   */
  public void setCreatedOn(Timestamp createdOn) {
    this.createdOn = createdOn;
  }

  /**
   * Gets the created by.
   *
   * @return the created by
   */
  public String getCreatedBy() {
    return createdBy;
  }

  /**
   * Sets the created by.
   *
   * @param createdBy the new created by
   */
  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
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
   * Gets the exclusion reason.
   *
   * @return the exclusion reason
   */
  public String getExclusionReason() {
    return exclusionReason;
  }

  /**
   * Sets the exclusion reason.
   *
   * @param exclusionReason the new exclusion reason
   */
  public void setExclusionReason(String exclusionReason) {
    this.exclusionReason = exclusionReason;
  }

  /**
   * Gets the exclusion reasons.
   *
   * @return the exclusion reasons
   */
  public Set<MasterListValueHistoryDTO> getExclusionReasons() {
    return exclusionReasons;
  }

  /**
   * Sets the exclusion reasons.
   *
   * @param exclusionReasons the new exclusion reasons
   */
  public void setExclusionReasons(Set<MasterListValueHistoryDTO> exclusionReasons) {
    this.exclusionReasons = exclusionReasons;
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


  /**
   * Gets the exclusion reason first level.
   *
   * @return the exclusion reason first level
   */
  public String getExclusionReasonFirstLevel() {
    return exclusionReasonFirstLevel;
  }


  /**
   * Sets the exclusion reason first level.
   *
   * @param exclusionReasonFirstLevel the new exclusion reason first level
   */
  public void setExclusionReasonFirstLevel(String exclusionReasonFirstLevel) {
    this.exclusionReasonFirstLevel = exclusionReasonFirstLevel;
  }


  /**
   * Gets the exclusion reasons first level.
   *
   * @return the exclusion reasons first level
   */
  public Set<MasterListValueHistoryDTO> getExclusionReasonsFirstLevel() {
    return exclusionReasonsFirstLevel;
  }


  /**
   * Sets the exclusion reasons first level.
   *
   * @param exclusionReasonsFirstLevel the new exclusion reasons first level
   */
  public void setExclusionReasonsFirstLevel(
      Set<MasterListValueHistoryDTO> exclusionReasonsFirstLevel) {
    this.exclusionReasonsFirstLevel = exclusionReasonsFirstLevel;
  }


  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "OfferDTO [id=" + id + ", isAwarded=" + isAwarded + ", offerDate=" + offerDate
        + ", isPartOffer=" + isPartOffer + ", isExcludedFromProcess=" + isExcludedFromProcess
        + ", excludedFirstLevel=" + excludedFirstLevel + ", isVariant=" + isVariant
        + ", grossAmount=" + grossAmount + ", grossAmountCorrected=" + grossAmountCorrected
        + ", isCorrected=" + isCorrected + ", discount=" + discount + ", isDiscountPercentage="
        + isDiscountPercentage + ", vat=" + vat + ", discount2=" + discount2
        + ", isDiscount2Percentage=" + isDiscount2Percentage + ", discount2Days=" + discount2Days
        + ", priceIncrease=" + priceIncrease + ", modifiedOn=" + modifiedOn + ", notes=" + notes
        + ", rank=" + rank + ", variantNotes=" + variantNotes + ", isEmptyOffer=" + isEmptyOffer
        + ", applicationDate=" + applicationDate + ", isVatPercentage=" + isVatPercentage
        + ", buildingCosts=" + buildingCosts + ", isBuildingCostsPercentage="
        + isBuildingCostsPercentage + ", ancilliaryAmountGross=" + ancilliaryAmountGross
        + ", isAncilliaryAmountPercentage=" + isAncilliaryAmountPercentage
        + ", ancilliaryAmountVat=" + ancilliaryAmountVat + ", operatingCostGross="
        + operatingCostGross + ", operatingCostNotes=" + operatingCostNotes
        + ", operatingCostGrossCorrected=" + operatingCostGrossCorrected
        + ", isOperatingCostCorrected=" + isOperatingCostCorrected + ", operatingCostDiscount="
        + operatingCostDiscount + ", isOperatingCostDiscountPercentage="
        + isOperatingCostDiscountPercentage + ", operatingCostDiscount2=" + operatingCostDiscount2
        + ", isOperatingCostDiscount2Percentage=" + isOperatingCostDiscount2Percentage
        + ", operatingCostVat=" + operatingCostVat + ", operatingCostIsVatPercentage="
        + operatingCostIsVatPercentage + ", migreatedPM=" + migreatedPM + ", migratedProcedure="
        + migratedProcedure + ", migratedDepartment=" + migratedDepartment + ", migratedSubmission="
        + migratedSubmission + ", migratedProject=" + migratedProject + ", fromMigration="
        + fromMigration + ", amount=" + amount + ", discountInPercentage=" + discountInPercentage
        + ", discount2InPercentage=" + discount2InPercentage + ", operatingCostsInPercentage="
        + operatingCostsInPercentage + ", buildingCostsInPercentage=" + buildingCostsInPercentage
        + ", isDefaultOffer=" + isDefaultOffer + ", qExTotalGrade=" + qExTotalGrade + ", qExStatus="
        + qExStatus + ", qExExaminationIsFulfilled=" + qExExaminationIsFulfilled
        + ", qExSuitabilityNotes=" + qExSuitabilityNotes + ", awardRank=" + awardRank
        + ", awardTotalScore=" + awardTotalScore + ", operatingCostsAmount=" + operatingCostsAmount
        + ", qExRank=" + qExRank + ", awardRecipientFreeTextField=" + awardRecipientFreeTextField
        + ", isExcludedFromAwardProcess=" + isExcludedFromAwardProcess + ", createdOn=" + createdOn
        + ", createdBy=" + createdBy + ", applicationInformation=" + applicationInformation
        + ", exclusionReason=" + exclusionReason + ", grossAmountOrCorrected="
        + grossAmountOrCorrected + ", discountPercentage=" + discountPercentage
        + ", discount2Percentage=" + discount2Percentage + ", vatPercentage=" + vatPercentage
        + ", discountAmountWithPercentage=" + discountAmountWithPercentage
        + ", discount2AmountWithPercentage=" + discount2AmountWithPercentage + ", amountInclusive="
        + amountInclusive + ", discountBetweenTotal=" + discountBetweenTotal
        + ", discount2BetweenTotal=" + discount2BetweenTotal + ", vatAmountWithPercentage="
        + vatAmountWithPercentage + ", ancilliaryVatAmountWithPercentage="
        + ancilliaryVatAmountWithPercentage + ", ancilliaryAmountIncl=" + ancilliaryAmountIncl
        + ", ancilliaryGrossAmountOrCorrected=" + ancilliaryGrossAmountOrCorrected
        + ", ancilliaryVatPercentage=" + ancilliaryVatPercentage + ", exclusionReasonFirstLevel="
        + exclusionReasonFirstLevel + "]";
  }

}
