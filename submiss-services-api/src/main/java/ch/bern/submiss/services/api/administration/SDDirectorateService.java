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


import java.util.List;
import ch.bern.submiss.services.api.dto.DirectorateHistoryDTO;
import ch.bern.submiss.services.api.dto.MasterListTypeDataDTO;

/**
 * The Interface SDDirectorateService.
 */
public interface SDDirectorateService {


  /**
   * Gets the directorates by department ids.
   *
   * @param departmentIDs the department I ds
   * @return the directorates by department ids
   */
  List<DirectorateHistoryDTO> getDirectoratesByDepartmentIds(List<String> departmentIDs);
  
  
  /**
   * Gets the directorates by ids.
   *
   * @param directoratesIDs the directorate Ids
   * @return the directorates by their ids
   */
  List<DirectorateHistoryDTO> getDirectoratesByIds(List<String> directoratesIDs);


/**
 * Gets the directorates by tenant id.
 *
 * @param tenantId the tenant id
 * @return the directorates by tenant id
 */
List<DirectorateHistoryDTO> getDirectoratesByTenantId(String tenantId);

/**
 * Gets the all permitted directorates.
 *
 * @return the all permitted directorates
 */
List<DirectorateHistoryDTO> getAllPermittedDirectorates();

  /**
   * Directorate history entities to master list type data.
   *
   * @return the type data list
   */
  List<MasterListTypeDataDTO> directorateToTypeData();

  /**
   * Gets the directorate history entry by id.
   *
   * @param entryId the entry id
   * @return the directorate history entry
   */
  DirectorateHistoryDTO getDirectorateEntryById(String entryId);

  /**
   * Directorate history query.
   *
   * @param tenantId the tenant id
   * @return the list
   */
  List<DirectorateHistoryDTO> directorateHistoryQuery(String tenantId);

  /**
   * Checks if the name is unique.
   *
   * @param name the name
   * @param id the directorate history id
   * @return true, if name is unique
   */
  boolean isNameUnique(String name, String id);

  /**
   * Save the directorate history entry.
   *
   * @param directorateHistoryDTO the directorate history DTO
   */
  void saveDirectorateEntry(DirectorateHistoryDTO directorateHistoryDTO);

  
  /**
   * Gets the all directorates by user tenant.
   *
   * @return the all directorates by user tenant
   */
  List<DirectorateHistoryDTO> getAllDirectoratesByUserTenant();


/**
 * Directorate history query all.
 *
 * @param tenantId the tenant id
 * @return the list
 */
List<DirectorateHistoryDTO> directorateHistoryQueryAll(String tenantId);
}
