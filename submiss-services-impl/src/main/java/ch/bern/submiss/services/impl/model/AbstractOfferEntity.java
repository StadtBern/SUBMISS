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

package ch.bern.submiss.services.impl.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@MappedSuperclass
public abstract class AbstractOfferEntity extends AbstractEntity {

  /**
   * The created on.
   */
  @CreationTimestamp
  @Column(name = "CREATED_ON")
  private Timestamp createdOn;

  /**
   * The created by.
   */
  @Column(name = "CREATED_BY")
  private String createdBy;

  /**
   * The updated on.
   */
  @UpdateTimestamp
  @Column(name = "UPDATED_ON", insertable = false)
  private Timestamp updatedOn;

  /**
   * The updated by.
   */
  @Column(name = "UPDATED_BY")
  private String updatedBy;

  /**
   * The notes.
   */
  @Column(name = "NOTES")
  private String notes;

  /**
   * The gross amount.
   */
  @Column(name = "GROSS_AMOUNT")
  private Double grossAmount;

  /**
   * The discount.
   */
  @Column(name = "DISCOUNT")
  private Double discount;

  /**
   * The is discount percentage.
   */
  @Column(name = "IS_DISCOUNT_PERCENTAGE")
  private Boolean isDiscountPercentage;

  /**
   * The vat.
   */
  @Column(name = "VAT")
  private Double vat;

  /**
   * The is vat percentage.
   */
  @Column(name = "IS_VAT_PERCENTAGE")
  private Boolean isVatPercentage;

  /**
   * The discount 2.
   */
  @Column(name = "DISCOUNT2")
  private Double discount2;

  /**
   * The is discount 2 percentage.
   */
  @Column(name = "IS_DISCOUNT2_PERCENTAGE")
  private Boolean isDiscount2Percentage;

  /**
   * The amount.
   */
  @Column(name = "AMOUNT")
  private BigDecimal amount;


  /**
   * The building costs.
   */
  @Column(name = "BUILDING_COSTS")
  private Double buildingCosts;

  /**
   * The is building costs percentage.
   */
  @Column(name = "IS_BUILDING_COSTS_PERCENTAGE")
  private Boolean isBuildingCostsPercentage;

  public Timestamp getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(Timestamp createdOn) {
    this.createdOn = createdOn;
  }

  public Timestamp getUpdatedOn() {
    return updatedOn;
  }

  public void setUpdatedOn(Timestamp updatedOn) {
    this.updatedOn = updatedOn;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public String getUpdatedBy() {
    return updatedBy;
  }

  public void setUpdatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public Double getGrossAmount() {
    return grossAmount;
  }

  public void setGrossAmount(Double grossAmount) {
    this.grossAmount = grossAmount;
  }

  public Double getDiscount() {
    return discount;
  }

  public void setDiscount(Double discount) {
    this.discount = discount;
  }

  public Boolean getIsDiscountPercentage() {
    return isDiscountPercentage;
  }

  public void setIsDiscountPercentage(Boolean discountPercentage) {
    isDiscountPercentage = discountPercentage;
  }

  public Double getVat() {
    return vat;
  }

  public void setVat(Double vat) {
    this.vat = vat;
  }

  public Double getDiscount2() {
    return discount2;
  }

  public void setDiscount2(Double discount2) {
    this.discount2 = discount2;
  }

  public Boolean getIsDiscount2Percentage() {
    return isDiscount2Percentage;
  }

  public void setIsDiscount2Percentage(Boolean discount2Percentage) {
    isDiscount2Percentage = discount2Percentage;
  }

  public Boolean getIsVatPercentage() {
    return isVatPercentage;
  }

  public void setIsVatPercentage(Boolean vatPercentage) {
    isVatPercentage = vatPercentage;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public Double getBuildingCosts() {
    return buildingCosts;
  }

  public void setBuildingCosts(Double buildingCosts) {
    this.buildingCosts = buildingCosts;
  }

  public Boolean getIsBuildingCostsPercentage() {
    return isBuildingCostsPercentage;
  }

  public void setIsBuildingCostsPercentage(Boolean isBuildingCostsPercentage) {
    this.isBuildingCostsPercentage = isBuildingCostsPercentage;
  }
}
