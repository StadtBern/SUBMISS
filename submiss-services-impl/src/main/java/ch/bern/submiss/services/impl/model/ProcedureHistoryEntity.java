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
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import ch.bern.submiss.services.api.constants.Process;

/**
 * The Class ProcedureHistoryEntity.
 */
@Entity
@Table(name = "SUB_PROCEDURE_HISTORY")
public class ProcedureHistoryEntity {

  @Id
  @GeneratedValue(generator = "uuid1")
  @GenericGenerator(name = "uuid1", strategy = "uuid2")
  private String id;

  @ManyToOne
  @JoinColumn(name = "FK_TENANT")
  private TenantEntity tenant;

  @Column(name = "VALUE")
  private BigDecimal value;
  
  @Column(name = "IS_ACTIVE")
  private boolean isActive;
  
  @Column(name = "FROM_DATE")
  private Timestamp fromDate;
  
  @Column(name = "TO_DATE")
  private Timestamp toDate;
  
  @OneToOne
  @JoinColumn(name = "FK_PROCEDURE")
  private ProcedureEntity procedure;
  
  @Column(name = "PROCESS")
  @Enumerated
  private Process process;

  @ManyToOne
  @JoinColumn(name = "FK_PROCESS_TYPE")
  private MasterListValueEntity processType;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public TenantEntity getTenant() {
    return tenant;
  }

  public void setTenant(TenantEntity tenant) {
    this.tenant = tenant;
  }

  public BigDecimal getValue() {
    return value;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }

  public boolean isActive() {
    return isActive;
  }

  public void setActive(boolean isActive) {
    this.isActive = isActive;
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

  public ProcedureEntity getProcedure() {
    return procedure;
  }

  public void setProcedure(ProcedureEntity procedure) {
    this.procedure = procedure;
  }

  public Process getProcess() {
    return process;
  }

  public void setProcess(Process process) {
    this.process = process;
  }

  public MasterListValueEntity getProcessType() {
    return processType;
  }

  public void setProcessType(MasterListValueEntity processType) {
    this.processType = processType;
  }
  
  
}
