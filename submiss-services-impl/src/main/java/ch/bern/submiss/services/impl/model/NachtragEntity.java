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
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The type Nachtrag entity.
 */
@Entity
@Table(name = "SUB_ADDENDUM")
public class NachtragEntity extends AbstractOfferEntity {

  /**
   * The offer.
   */
  @ManyToOne
  @JoinColumn(name = "FK_OFFER")
  private OfferEntity offer;

  /**
   * The Nachtrag name.
   */
  @Column(name = "ADDENDUM_NAME", length = 100)
  private String nachtragName;

  /**
   * The Nachtrag date.
   */
  @Column(name = "ADDENDUM_DATE")
  private Timestamp nachtragDate;

  /**
   * The Nachtrag is closed.
   */
  @Column(name = "IS_CLOSED")
  private Boolean isClosed;

  /**
   * The Nachtrag title.
   */
  @Column(name = "TITLE", length = 100)
  private String title;

  /**
   * The Nachtrag discount description.
   */
  @Column(name = "DISCOUNT_DESCRIPTION", length = 100)
  private String discountDescription;

  /**
   * The Nachtrag building costs value.
   */
  @Column(name = "BUILDING_COSTS_VALUE")
  private BigDecimal buildingCostsValue;

  /**
   * The Nachtrag vat value.
   */
  @Column(name = "VAT_VALUE")
  private BigDecimal vatValue;

  /**
   * The Nachtrag discount1 value.
   */
  @Column(name = "DISCOUNT1_VALUE")
  private BigDecimal discount1Value;

  /**
   * The Nachtrag discount2 value.
   */
  @Column(name = "DISCOUNT2_VALUE")
  private BigDecimal discount2Value;

  /**
   * The Nachtrag amount inclusive.
   */
  @Column(name = "AMOUNT_INCLUSIVE")
  private BigDecimal amountInclusive;

  public OfferEntity getOffer() {
    return offer;
  }

  public void setOffer(OfferEntity offer) {
    this.offer = offer;
  }

  public String getNachtragName() {
    return nachtragName;
  }

  public void setNachtragName(String nachtragName) {
    this.nachtragName = nachtragName;
  }

  public Timestamp getNachtragDate() {
    return nachtragDate;
  }

  public void setNachtragDate(Timestamp nachtragDate) {
    this.nachtragDate = nachtragDate;
  }

  public Boolean getClosed() {
    return isClosed;
  }

  public void setClosed(Boolean closed) {
    isClosed = closed;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDiscountDescription() {
    return discountDescription;
  }

  public void setDiscountDescription(String discountDescription) {
    this.discountDescription = discountDescription;
  }

  public BigDecimal getBuildingCostsValue() {
    return buildingCostsValue;
  }

  public void setBuildingCostsValue(BigDecimal buildingCostsValue) {
    this.buildingCostsValue = buildingCostsValue;
  }

  public BigDecimal getVatValue() {
    return vatValue;
  }

  public void setVatValue(BigDecimal vatValue) {
    this.vatValue = vatValue;
  }

  public BigDecimal getDiscount1Value() {
    return discount1Value;
  }

  public void setDiscount1Value(BigDecimal discount1Value) {
    this.discount1Value = discount1Value;
  }

  public BigDecimal getDiscount2Value() {
    return discount2Value;
  }

  public void setDiscount2Value(BigDecimal discount2Value) {
    this.discount2Value = discount2Value;
  }

  public BigDecimal getAmountInclusive() {
    return amountInclusive;
  }

  public void setAmountInclusive(BigDecimal amountInclusive) {
    this.amountInclusive = amountInclusive;
  }
}
