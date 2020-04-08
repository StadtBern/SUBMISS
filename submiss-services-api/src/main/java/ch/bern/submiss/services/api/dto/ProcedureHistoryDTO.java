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

import ch.bern.submiss.services.api.constants.Process;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The Class ProcedureHistoryDTO.
 */
public class ProcedureHistoryDTO extends AbstractDTO {

  /** The tenant. */
  private TenantDTO tenant;

  /** The value. */
  private BigDecimal value;

  /** The is active. */
  private boolean isActive;

  /** The from date. */
  private Timestamp fromDate;

  /** The to date. */
  private Timestamp toDate;

  /** The procedure. */
  private ProcedureDTO procedure;

  /** The process. */
  private Process process;

  /** The process type. */
  private MasterListValueDTO processType;

  /** The process type history. */
  private MasterListValueHistoryDTO processTypeHistory;


  /**
   * Gets the process type history.
   *
   * @return the process type history
   */
  public MasterListValueHistoryDTO getProcessTypeHistory() {
    return processTypeHistory;
  }

  /**
   * Sets the process type history.
   *
   * @param processTypeHistory the new process type history
   */
  public void setProcessTypeHistory(MasterListValueHistoryDTO processTypeHistory) {
    this.processTypeHistory = processTypeHistory;
  }

  /**
   * Gets the tenant.
   *
   * @return the tenant
   */
  public TenantDTO getTenant() {
    return tenant;
  }

  /**
   * Sets the tenant.
   *
   * @param tenant the new tenant
   */
  public void setTenant(TenantDTO tenant) {
    this.tenant = tenant;
  }

  /**
   * Gets the value.
   *
   * @return the value
   */
  public BigDecimal getValue() {
    return value;
  }

  /**
   * Sets the value.
   *
   * @param value the new value
   */
  public void setValue(BigDecimal value) {
    this.value = value;
  }

  /**
   * Checks if is active.
   *
   * @return true, if is active
   */
  public boolean isActive() {
    return isActive;
  }

  /**
   * Sets the active.
   *
   * @param isActive the new active
   */
  public void setActive(boolean isActive) {
    this.isActive = isActive;
  }

  /**
   * Gets the from date.
   *
   * @return the from date
   */
  public Timestamp getFromDate() {
    return fromDate;
  }

  /**
   * Sets the from date.
   *
   * @param fromDate the new from date
   */
  public void setFromDate(Timestamp fromDate) {
    this.fromDate = fromDate;
  }

  /**
   * Gets the to date.
   *
   * @return the to date
   */
  public Timestamp getToDate() {
    return toDate;
  }

  /**
   * Sets the to date.
   *
   * @param toDate the new to date
   */
  public void setToDate(Timestamp toDate) {
    this.toDate = toDate;
  }

  /**
   * Gets the procedure.
   *
   * @return the procedure
   */
  public ProcedureDTO getProcedure() {
    return procedure;
  }

  /**
   * Sets the procedure.
   *
   * @param procedure the new procedure
   */
  public void setProcedure(ProcedureDTO procedure) {
    this.procedure = procedure;
  }

  /**
   * Gets the process.
   *
   * @return the process
   */
  public Process getProcess() {
    return process;
  }

  /**
   * Sets the process.
   *
   * @param process the new process
   */
  public void setProcess(Process process) {
    this.process = process;
  }

  /**
   * Gets the process type.
   *
   * @return the process type
   */
  public MasterListValueDTO getProcessType() {
    return processType;
  }

  /**
   * Sets the process type.
   *
   * @param processType the new process type
   */
  public void setProcessType(MasterListValueDTO processType) {
    this.processType = processType;
  }

  @Override
  public String toString() {
    return "ProcedureHistoryDTO [id=" + super.getId() + ", version=" + super.getVersion()
      + ", value=" + value + ", isActive=" + isActive
      + ", fromDate=" + fromDate + ", toDate=" + toDate + ", process=" + process + "]";
  }
}
