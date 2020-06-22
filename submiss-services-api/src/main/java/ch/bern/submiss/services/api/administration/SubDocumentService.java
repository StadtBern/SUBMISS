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

import ch.bern.submiss.services.api.dto.CompanyDTO;
import ch.bern.submiss.services.api.dto.CompanyProofDTO;
import ch.bern.submiss.services.api.dto.CompanySearchDTO;
import ch.bern.submiss.services.api.dto.DirectorateHistoryDTO;
import ch.bern.submiss.services.api.dto.DocumentDTO;
import com.eurodyn.qlack2.fuse.cm.api.dto.VersionDTO;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * The Interface SubDocumentService.
 */
public interface SubDocumentService {

  /**
   * Gets the document list for the project part.
   *
   * @param id the id
   * @return the document list
   */
  List<DocumentDTO> getDocumentListProject(String id);

  /**
   * Gets the document list for the company part.
   *
   * @param id the id
   * @param companyId the company id
   * @return the document list
   */
  List<DocumentDTO> getDocumentListCompany(String id, String companyId);

  /**
   * Creates the document from template.
   *
   * @param documentDTO the document DTO
   * @return the list
   */
  List<String> createDocumentFromTemplate(DocumentDTO documentDTO);

  /**
   * Upload document.
   *
   * @param folderId the folder id
   * @param versionId the version id
   * @param uploadedFileId the uploaded file id
   * @param replaceDocument the replace document
   * @param isProjectDocument whether the document is uploaded on the project part
   * @param errors the errors
   */
  void uploadDocument(String folderId, String versionId, String uploadedFileId,
    Boolean replaceDocument, Boolean isProjectDocument, Set<ValidationError> errors);

  /**
   * Delete document.
   *
   * @param versionId the version id
   * @param reason the reason
   */
  void deleteDocument(String versionId, String reason);

  /**
   * Download document.
   *
   * @param nodeId the node id
   * @param versionName the version name
   * @return the byte[]
   */
  byte[] downloadDocument(String nodeId, String versionName);

  /**
   * Gets the doc latestversion.
   *
   * @param documentDTO the document DTO
   * @param submittentId the submittent id
   * @param companyId the company id
   * @return the doc latestversion
   */
  VersionDTO getDocLatestversion(DocumentDTO documentDTO, String submittentId, String companyId);

  /**
   * Gets the version by id.
   *
   * @param versionId the version id
   * @return the version by id
   */
  VersionDTO getVersionById(String versionId);

  /**
   * Download multiple documents.
   *
   * @param versionIds the version ids
   * @return the byte[]
   */
  byte[] downloadMultipleDocuments(List<String> versionIds);


  /**
   * Update document properties(the title and the Vertaulich value).
   *
   * @param documentDTO the document DTO
   * @param privateDocumentSetChanged the private document set changed
   */
  void updateDocumentProperties(DocumentDTO documentDTO, Boolean privateDocumentSetChanged);


  /**
   * Load document directorate.
   *
   * @param documentId the document id
   * @return the directorate history DTO
   */
  List<DirectorateHistoryDTO> loadDocumentDirectorate(String documentId);

  /**
   * This function calculates if the Nachweisbrief document of a company should be created according
   * to the company's proofs.
   *
   * @param deadline the deadline
   * @param companyProofDTOs the company proof DT os
   * @return true, if a proof document could be created
   */
  boolean createProofDocument(Date deadline, List<CompanyProofDTO> companyProofDTOs);

  /**
   * Generate company part document.
   *
   * @param documentDTO the document DTO
   * @param templateShortCode the template short code
   * @return the byte[]
   */
  byte[] generateCompanyPartDocument(DocumentDTO documentDTO, String templateShortCode);


  /**
   * Generate project part document.
   *
   * @param documentDTO the document DTO
   * @return the byte[]
   */
  byte[] generateProjectPartDocument(DocumentDTO documentDTO);

  /**
   * Gets the document versions by filename.
   *
   * @param folderId the folder id
   * @param fileNameList the file name list
   * @return the document versions by filename
   */
  List<DocumentDTO> getDocumentVersionsByFilename(String folderId, List<String> fileNameList);

  /**
   * Checks if document deletion reason is mandatory.
   *
   * @param templateId the template id
   * @return true, if deletion reason is mandatory
   */
  boolean isDeletionReasonMandatory(String templateId);

  /**
   * Generate company search documents.
   *
   * @param documentDTO the document DTO
   * @param shortCode the short code
   * @param companies the companies
   * @param sortType the sort type
   * @param sortColumn the sort column
   * @param searchDTO the search DTO
   * @return the byte[]
   */
  byte[] generateCompanySearchDocuments(DocumentDTO documentDTO, String shortCode,
    List<CompanyDTO> companies, String sortType, String sortColumn, CompanySearchDTO searchDTO);

  /**
   * Check if given document exists.
   *
   * @param submissionId the submission id
   * @param templateShortCode the template short code
   * @return true, if document exists
   */
  boolean documentExists(String submissionId, String templateShortCode);

  /**
   * Check if Rechtliches Gehör (Abbruch) document exists.
   *
   * @param submissionId the submission id
   * @param templateShortCode the template short code
   * @return true, if document exists
   */
  boolean legalHearingTerminateDocumentExists(String submissionId, String templateShortCode);

  /**
   * Checks the permitted documents for upload.
   *
   * @param folderId the folder id
   * @param filenames the filenames
   * @param isProjectPart the is project part
   * @return the list
   */
  List<String> permittedDocumentsForUpload(String folderId, List<String> filenames,
    boolean isProjectPart);

  /**
   * Validation for Vertrag documents.
   *
   * @param submissionId the submissionId
   * @return the error
   */
  Set<ValidationError> contractDocumentValidation(String submissionId);
}
