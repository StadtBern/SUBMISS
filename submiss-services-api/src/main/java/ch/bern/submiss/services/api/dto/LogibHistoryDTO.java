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

/**
 * The Class LogibHistoryDTO.
 */
public class LogibHistoryDTO extends AbstractStammdatenDTO {

  /** The worker number. */
  private Integer workerNumber;

  /** The women number. */
  private Integer womenNumber;

  /** The men number. */
  private Integer menNumber;

  /** The is active. */
  private Boolean isActive;

  /** The logib id. */
  private LogibDTO logibId;

  /** The tenant. */
  private TenantDTO tenant;

  /**
   * Gets the worker number.
   *
   * @return the worker number
   */
  public Integer getWorkerNumber() {
    return workerNumber;
  }

  /**
   * Sets the worker number.
   *
   * @param workerNumber the new worker number
   */
  public void setWorkerNumber(Integer workerNumber) {
    this.workerNumber = workerNumber;
  }

  /**
   * Gets the women number.
   *
   * @return the women number
   */
  public Integer getWomenNumber() {
    return womenNumber;
  }

  /**
   * Sets the women number.
   *
   * @param womenNumber the new women number
   */
  public void setWomenNumber(Integer womenNumber) {
    this.womenNumber = womenNumber;
  }

  /**
   * Gets the checks if is active.
   *
   * @return the checks if is active
   */
  public Boolean getIsActive() {
    return isActive;
  }

  /**
   * Sets the checks if is active.
   *
   * @param isActive the new checks if is active
   */
  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }

  /**
   * Gets the logib id.
   *
   * @return the logib id
   */
  public LogibDTO getLogibId() {
    return logibId;
  }

  /**
   * Sets the logib id.
   *
   * @param logibId the new logib id
   */
  public void setLogibId(LogibDTO logibId) {
    this.logibId = logibId;
  }

  /**
   * Gets the men number.
   *
   * @return the men number
   */
  public Integer getMenNumber() {
    return menNumber;
  }

  /**
   * Sets the men number.
   *
   * @param menNumber the new men number
   */
  public void setMenNumber(Integer menNumber) {
    this.menNumber = menNumber;
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

  @Override
  public String toString() {
    return "LogibHistoryDTO [id=" + super.getId() + ", name=" + super.getName() + ", version="
      + super.getVersion() + ", workerNumber=" + workerNumber
      + ", womenNumber=" + womenNumber + ", menNumber=" + menNumber + ", isActive=" + isActive
      + "]";
  }
}


