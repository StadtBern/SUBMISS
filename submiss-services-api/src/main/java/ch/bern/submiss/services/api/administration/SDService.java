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

import ch.bern.submiss.services.api.constants.CategorySD;
import ch.bern.submiss.services.api.constants.Process;
import ch.bern.submiss.services.api.dto.MasterListDTO;
import ch.bern.submiss.services.api.dto.MasterListTypeDataDTO;
import ch.bern.submiss.services.api.dto.MasterListValueHistoryDTO;
import ch.bern.submiss.services.api.dto.SettingsDTO;
import ch.bern.submiss.services.api.dto.SignatureCopyDTO;
import ch.bern.submiss.services.api.dto.SignatureProcessTypeDTO;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import java.util.List;
import java.util.Set;

/**
 * The Interface SDService.
 */
public interface SDService {

  /**
   * Returns Master List Value History DTOs according to the name parameter and tenant id.
   *
   * @param type     the type
   * @param tenantId the tenant id
   * @return Master List Value History DTOs
   */
  List<MasterListValueHistoryDTO> masterListValueHistoryQuery(CategorySD type, String tenantId);

  /**
   * Returns Master List Value History DTOs according to the given type.
   *
   * @param type the type
   * @return Master List Value History DTOs
   */
  List<MasterListValueHistoryDTO> getMasterListHistoryByType(String type);

  /**
   * Gets the master list history by code.
   *
   * @param codes the codes
   * @return the master list history by code
   */
  List<MasterListValueHistoryDTO> getMasterListHistoryByCode(List<String> codes);

  /**
   * Retrieve signature.
   *
   * @param departmentId  the department id
   * @param directorateId the directorate id
   * @param process       the process
   * @return the signature process type DTO
   */
  SignatureProcessTypeDTO retrieveSignature(String departmentId, String directorateId,
    Process process);

  /**
   * Gets the master list types.
   *
   * @return the master list types
   */
  List<MasterListDTO> getMasterListTypes();

  /**
   * Gets the master list type data.
   *
   * @param type the master list type
   * @return the master list type data
   */
  List<MasterListTypeDataDTO> getMasterListTypeData(String type);

  /**
   * Gets the master list value history entry by id.
   *
   * @param entryId the entry id
   * @return the master list value history entry
   */
  MasterListValueHistoryDTO getSDEntryById(String entryId);

  /**
   * Gets the signatures by directorate id.
   *
   * @param directorateId the directorate id
   * @return the signatures by directorate id
   */
  List<SignatureProcessTypeDTO> getSignaturesByDirectorateId(String directorateId);

  /**
   * Checks if the description is unique.
   *
   * @param description the description
   * @param type        the master list type
   * @param id          the master list value history id
   * @return true, if description is unique
   */
  boolean isDescriptionUnique(String description, String type, String id);

  /**
   * Checks if formula is valid.
   *
   * @param formula the formula
   * @return true, if formula is valid
   */
  boolean isFormulaValid(String formula);

  /**
   * Save the master list value history entry.
   *
   * @param sdHistoryDTO the master list value history DTO
   * @param type         the master list type
   */
  Set<ValidationError> saveSDEntry(MasterListValueHistoryDTO sdHistoryDTO, String type);

  /**
   * Returns active and inactive Master List Value History DTOs according to the given type.
   *
   * @param type the type
   * @return Master List Value History DTOs
   */
  List<MasterListValueHistoryDTO> getAllMasterListDataDTOByType(String type);

  /**
   * Gets the signature copies by signature id.
   *
   * @param signatureId the signature id
   * @return the signature copies by signature id
   */
  List<SignatureCopyDTO> getSignatureCopiesBySignatureId(String signatureId);

  /**
   * Retrieve signature by id.
   *
   * @param id the id
   * @return the signature process type DTO
   */
  SignatureProcessTypeDTO retrieveSignatureById(String id);

  /**
   * Update signature process entitled.
   *
   * @param signatureProcessTypeDTO the signature process type DTO
   */
  void updateSignatureProcessEntitled(SignatureProcessTypeDTO signatureProcessTypeDTO);

  /**
   * Update signature copies.
   *
   * @param signatureProcessTypeDTO the signature process type DTO
   */
  void updateSignatureCopies(SignatureProcessTypeDTO signatureProcessTypeDTO);

  /**
   * Uploads image.
   *
   * @param uploadedImageId the uploaded image id
   * @return the image in the form of byte array.
   */
  byte[] uploadImage(String uploadedImageId);

  /**
   * Gets the report maximum results value.
   *
   * @return the report maximum results value
   */
  Long getReportMaximumResultsValue();

  /**
   * Gets the user settings for the contact information.
   *
   * @param tenantId  the tenant id
   * @param groupName the group name
   * @return the user settings
   */
  SettingsDTO getUserSettings(String tenantId, String groupName);

  /**
   * Gets the printer settings for main tenant.
   *
   * @return the printer settings
   */
  SettingsDTO getPrinterSettings();

  /**
   * Gets Master List Value History
   *
   * @param type     the type
   * @param tenantId the tenantId
   * @return the Master List Value History
   */
  List<MasterListValueHistoryDTO> historySdQuery(CategorySD type, String tenantId);

  /**
   * Gets the Master List Value History by submission.
   *
   * @param submissionId the submissionId
   * @param category     the category
   * @return the Master List Value History by submission
   */
  List<MasterListValueHistoryDTO> getMasterListValueHistoryDataBySubmission(String submissionId,
    String category);

  /**
   * Gets the tenant logo.
   *
   * @return the tenant logo
   */
  byte[] getTenantLogo();

  /**
   * Gets the document template id by its short code.
   *
   * @param templateShortCode the template short code
   * @param tenantId          the tenant id
   * @return the template id
   */
  String getTemplateIdByShortCode(String templateShortCode, String tenantId);

  /**
   * Gets the name of the master list type.
   *
   * @param type the master list type
   * @return the name
   */
  String getNameOfMasterListType(String type);

  /**
   * Gets the Master List Value History by code.
   *
   * @param code the code
   * @return the Master List Value History by code
   */
  MasterListValueHistoryDTO getMasterListHistoryByCode(String code);

  /**
   * Gets the current Master List Value History Entries by type for the given submission.
   *
   * @param submissionId the submission id
   * @param typeName     the master list type name
   * @return the current Master List Value History Entries by type
   */
  List<MasterListValueHistoryDTO> getCurrentMLVHEntriesForSubmission(String submissionId,
    String typeName);

  /**
   * Security check for Stammdaten.
   */
  void sdSecurityCheck();
}
