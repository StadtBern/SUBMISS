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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Class UnregisteredUserForm.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnregisteredUserForm {

  /** The tenant id. */
  private String tenantId;

  /** The group name. */
  private String groupName;

  /**
   * Gets the tenant id.
   *
   * @return the tenant id
   */
  public String getTenantId() {
    return tenantId;
  }

  /**
   * Sets the tenant id.
   *
   * @param tenantId the new tenant id
   */
  public void setTenantId(String tenantId) {
    this.tenantId = tenantId;
  }

  /**
   * Gets the group name.
   *
   * @return the group name
   */
  public String getGroupName() {
    return groupName;
  }

  /**
   * Sets the group name.
   *
   * @param groupName the new group name
   */
  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }
}
