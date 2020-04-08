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

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

/**
 * The Class LogibEntity.
 */
@Entity
@Table(name = "SUB_LOGIB_HISTORY")
public class LogibHistoryEntity extends AbstractStammdatenEntity {

  @Column(name = "WORKER_NUMBER")
  private Integer workerNumber;

  @Column(name = "MEN_NUMBER")
  private Integer menNumber;

  @Column(name = "WOMEN_NUMBER")
  private Integer womenNumber;

  @Column(name = "IS_ACTIVE")
  private Boolean isActive;

  /** The from date. */
  @Column(name = "FROM_DATE")
  private Timestamp fromDate;

  /** The to date. */
  @Column(name = "TO_DATE")
  private Timestamp toDate;

  /** The tenant. */
  @ManyToOne
  @JoinColumn(name = "FK_TENANT")
  private TenantEntity tenant;

  @ManyToOne
  @JoinColumn(name = "FK_LOGIB")
  private LogibEntity logibId;

  public Integer getWorkerNumber() {
    return workerNumber;
  }

  public void setWorkerNumber(Integer workerNumber) {
    this.workerNumber = workerNumber;
  }

  public Integer getMenNumber() {
    return menNumber;
  }

  public void setMenNumber(Integer menNumber) {
    this.menNumber = menNumber;
  }

  public Integer getWomenNumber() {
    return womenNumber;
  }

  public void setWomenNumber(Integer womenNumber) {
    this.womenNumber = womenNumber;
  }

  public Boolean getIsActive() {
    return isActive;
  }

  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }

  public LogibEntity getLogibId() {
    return logibId;
  }

  public void setLogibId(LogibEntity logibId) {
    this.logibId = logibId;
  }

  public Timestamp getFromDate() {
    return fromDate;
  }

  public void setFromDate(Timestamp fromDate) {
    this.fromDate = fromDate;
  }

  public Timestamp getToDate() {
    return toDate;
  }

  public void setToDate(Timestamp toDate) {
    this.toDate = toDate;
  }

  public TenantEntity getTenant() {
    return tenant;
  }

  public void setTenant(TenantEntity tenant) {
    this.tenant = tenant;
  }

  @Override
  public String toString() {
    return "LogibEntity [id=" + super.getId() + ", name=" + super.getName() + ", version=" + super
      .getVersion() + ", workerNumber=" + workerNumber
      + ", menNumber=" + menNumber + ", womenNumber=" + womenNumber + ", isActive=" + isActive
      + "]";
  }
}
