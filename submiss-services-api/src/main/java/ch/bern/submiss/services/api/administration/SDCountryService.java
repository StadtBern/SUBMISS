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
import java.util.Map;

import ch.bern.submiss.services.api.dto.CountryHistoryDTO;
import ch.bern.submiss.services.api.dto.MasterListTypeDataDTO;

public interface SDCountryService {

  List<CountryHistoryDTO> readAll();

  /**
   * Country history entities to master list type data.
   *
   * @return the type data list
   */
  List<MasterListTypeDataDTO> countryToTypeData();

  /**
   * Gets the country history entry by id.
   *
   * @param entryId the entry id
   * @return the country history entry
   */
  CountryHistoryDTO getCountryEntryById(String entryId);

  /**
   * Gets the country history DTOs.
   *
   * @return the country history DTOs
   */
  List<CountryHistoryDTO> getCountryHistoryDTOs();

  /**
   * Checks if the country name is unique.
   *
   * @param countryName the country name
   * @param id the country history id
   * @return true, if country name is unique
   */
  boolean isCountryNameUnique(String countryName, String id);

  /**
   * Save the country history entry.
   *
   * @param countryHistoryDTO the country history DTO
   */
  void saveCountryEntry(CountryHistoryDTO countryHistoryDTO);

  /**
   * Maps country ids to the latest country history DTOs.
   *
   * @return the map
   */
  Map<String, CountryHistoryDTO> mapCountryIdsToCountryHistoryDTOs();
}
