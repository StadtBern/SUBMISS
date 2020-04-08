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

import ch.bern.submiss.services.api.dto.DepartmentHistoryDTO;
import ch.bern.submiss.services.api.dto.MasterListTypeDataDTO;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import java.util.List;
import java.util.Set;

public interface SDDepartmentService {

  List<DepartmentHistoryDTO> readDepartmentsByUser();

  List<DepartmentHistoryDTO> readAll();

  List<DepartmentHistoryDTO> getDepartmentsByDirectoratesIds(List<String> directoratesIDs);

  /**
   * Get the department history entry for the given department id.
   *
   * @param departmentId the department id
   * @return The department history entry of the given department id
   */
  DepartmentHistoryDTO getDepartmentHistByDepartmentId(String departmentId);

  DepartmentHistoryDTO getDepartmentBySortName(String shortName, String company);

  List<DepartmentHistoryDTO> getDepartmentByTenant(String tenantId);

  List<DepartmentHistoryDTO> getUserDepartments();

  /**
   * Department history entities to master list type data.
   *
   * @return the type data list
   */
  List<MasterListTypeDataDTO> departmentToTypeData();

  /**
   * Gets the department history entry by id.
   *
   * @param entryId the entry id
   * @return the department history entry
   */
  DepartmentHistoryDTO getDepartmentEntryById(String entryId);

  /**
   * Department history query.
   *
   * @param tenantId the tenant id
   * @return the list
   */
  List<DepartmentHistoryDTO> departmentHistoryQuery(String tenantId);

  /**
   * Checks if the name is unique.
   *
   * @param name the name
   * @param shortName the short name
   * @param id the department history id
   * @return true, if name is unique
   */
  boolean isNameAndShortNameUnique(String name, String shortName, String id);

  /**
   * Save the department entry.
   *
   * @param departmentHistoryDTO the department history DTO
   */
  Set<ValidationError> saveDepartmentEntry(DepartmentHistoryDTO departmentHistoryDTO);


  /**
   * Gets the active departments by user tenant.
   *
   * @return the active departments by user tenant
   */
  List<DepartmentHistoryDTO> getActiveDepartmentsByUserTenant();

  List<DepartmentHistoryDTO> departmentHistoryQueryAll(String tenantId);

  /**
   * Read departments by user and role.
   *
   * @return the list
   */
  List<DepartmentHistoryDTO> readDepartmentsByUserAndRole();
}
