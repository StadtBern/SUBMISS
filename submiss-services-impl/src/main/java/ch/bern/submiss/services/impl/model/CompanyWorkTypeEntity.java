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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * The Class WorkTypeEntity.
 */
@Entity
@Table(name = "SUB_COMPANY_WORK_TYPE")
public class CompanyWorkTypeEntity extends AbstractStammdatenEntity {

  /** The company id. */
  @Column(name = "FK_COMPANY")
  private String companyId;

  /** The master list value id. */
  @Column(name = "FK_WORK_TYPE")
  private String masterListValueId;


  /**
   * Gets the company id.
   *
   * @return the company id
   */
  public String getCompanyId() {
    return companyId;
  }

  /**
   * Sets the company id.
   *
   * @param companyId the new company id
   */
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }

  /**
   * Gets the master list value id.
   *
   * @return the master list value id
   */
  public String getMasterListValueId() {
    return masterListValueId;
  }

  /**
   * Sets the master list value id.
   *
   * @param masterListValueId the new master list value id
   */
  public void setMasterListValueId(String masterListValueId) {
    this.masterListValueId = masterListValueId;
  }


}
