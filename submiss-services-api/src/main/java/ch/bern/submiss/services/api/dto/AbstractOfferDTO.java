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

public abstract class AbstractOfferDTO extends AbstractDTO {

  private Timestamp createdOn;
  private String createdBy;
  private Timestamp updatedOn;
  private String updatedBy;
  private String notes;
  private Double grossAmount;
  private Double discount;
  private Boolean isDiscountPercentage;
  private Double vat;
  private Boolean isVatPercentage;
  private Double discount2;
  private Boolean isDiscount2Percentage;
  private BigDecimal amount;
  private Double buildingCosts;
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
