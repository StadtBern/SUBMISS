
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

package ch.bern.submiss.services.api.administration;

import ch.bern.submiss.services.api.dto.TenantDTO;

import java.util.List;

/**
 * The Interface SDTenantService.
 */
public interface SDTenantService {

  /**
   * Read all.
   *
   * @return the list
   */
  List<TenantDTO> readAll();

  /**
   * Gets the tenants a user can see users from according security.
   *
   * @return Returns a list of the allowed tenants
   */
  public List<TenantDTO> getUserTenants();

  /**
   * Gets the tenant by id.
   *
   * @param tenantId the tenant id
   * @return the tenant by id
   */
  public TenantDTO getTenantById(String tenantId);

  /**
   * Gets the tenant by department and directorate.
   *
   * @param department the department
   * @param directorate the directorate
   * @return the tenant by department and directorate
   */
  public TenantDTO getTenantByDepartmentAndDirectorate(String department, String directorate);

  /**
   * Gets the tenant.
   *
   * @return the tenant
   */
  public TenantDTO getTenant();

  /**
   * Gets the tenant by department.
   *
   * @param departmentId the department id
   * @return the tenant by department
   */
  public TenantDTO getTenantByDepartment(String departmentId);

  /**
   * Gets the tenant by directorate.
   *
   * @param directorateId the directorate id
   * @return the tenant by directorate
   */
  public TenantDTO getTenantByDirectorate(String directorateId);
  
  /**
   * Gets the main tenant.
   *
   * @return the main tenant
   */
  public TenantDTO getMainTenant();

  /**
   * Checks if the current tenant is Kanton Bern.
   *
   * @return true, if current tenant is Kanton Bern
   */
  public boolean isTenantKantonBern();
}
