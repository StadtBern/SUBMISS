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

package ch.bern.submiss.web.resources;

import ch.bern.submiss.services.api.administration.CompanyService;
import ch.bern.submiss.services.api.administration.RuleService;
import ch.bern.submiss.services.api.administration.SDService;
import ch.bern.submiss.services.api.administration.SubDocumentService;
import ch.bern.submiss.services.api.administration.SubmissPrintService;
import ch.bern.submiss.services.api.administration.SubmissionService;
import ch.bern.submiss.services.api.administration.UserAdministrationService;
import ch.bern.submiss.services.api.dto.CompanyDTO;
import ch.bern.submiss.services.api.dto.CompanySearchDTO;
import ch.bern.submiss.services.api.dto.DocumentDTO;
import ch.bern.submiss.services.api.dto.SettingsDTO;
import ch.bern.submiss.services.api.util.ValidationMessages;
import ch.bern.submiss.web.forms.CompanySearchForm;
import ch.bern.submiss.web.forms.DocumentEditForm;
import ch.bern.submiss.web.forms.DocumentForm;
import ch.bern.submiss.web.forms.DocumentUploadForm;
import ch.bern.submiss.web.forms.LegalExclusionForm;
import ch.bern.submiss.web.mappers.CompanySearchMapper;
import ch.bern.submiss.web.mappers.DepartmentHistoryFormMapper;
import ch.bern.submiss.web.mappers.LegalHearingExclusionFormMapper;
import com.eurodyn.qlack2.fuse.cm.api.dto.VersionDTO;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import org.apache.commons.lang3.StringUtils;
import org.ops4j.pax.cdi.api.OsgiService;

/**
 * The Class DocumentResource.
 */
@Path("/document")
@Singleton
public class DocumentResource {

  /**
   * The Constant DEPT_AMOUNTS_EMPTY_VALUE.
   */
  private static final String DEPT_AMOUNTS_EMPTY_VALUE = "null";

  /**
   * The Constant NACHWEISBRIEF.
   */
  private static final String NACHWEISBRIEF = "Nachweisbrief";

  /**
   * The Constant SUBMIT_DATE.
   */
  private static final String SUBMIT_DATE = "submitDate";

  /**
   * The Constant PROCUREMENT.
   */
  private static final String PROCUREMENT = "procurement";

  /**
   * The Constant SUBMIT_DATE_ERROR_FIELD.
   */
  private static final String SUBMIT_DATE_ERROR_FIELD = "submitDateErrorField";

  /**
   * The Constant CONTENT_DISPOSITION.
   */
  private static final String CONTENT_DISPOSITION = "Content-Disposition";

  /**
   * The Constant ATTACHMENT_FILENAME.
   */
  private static final String ATTACHMENT_FILENAME = "attachment; filename=\"";

  /**
   * The company service.
   */
  @OsgiService
  @Inject
  protected CompanyService companyService;

  /**
   * The document service.
   */
  @OsgiService
  @Inject
  private SubDocumentService documentService;

  /**
   * The submission service.
   */
  @OsgiService
  @Inject
  private SubmissionService submissionService;

  /**
   * The rule service.
   */
  @OsgiService
  @Inject
  private RuleService ruleService;

  @OsgiService
  @Inject
  private SubmissPrintService submissPrintService;

  @Inject
  private SDService sDService;

  @OsgiService
  @Inject
  private UserAdministrationService userService;

  /**
   * Gets the document list for the project part.
   *
   * @param id the submission id
   * @return the document list
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/getDocumentListProject/{id}")
  public Response getDocumentListProject(@PathParam("id") String id) {
    return Response.ok(documentService.getDocumentListProject(id)).build();
  }

  /**
   * Gets the document list for the company part.
   *
   * @param id the id
   * @return the document list
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/getDocumentListCompany/{id}")
  public Response getDocumentListCompany(@PathParam("id") String id) {
    return Response.ok(documentService.getDocumentListCompany(id, null)).build();
  }

  /**
   * Creates the document.
   *
   * @param documentForm the document form
   * @return the response
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/createDocument")
  public Response createDocument(DocumentForm documentForm) {
    Set<ValidationError> errors = validation(documentForm);
    List<String> createdDocumentIds;
    if (!errors.isEmpty()) {
      return Response.status(Status.BAD_REQUEST).entity(errors).build();
    }

    DocumentDTO documentDTO = new DocumentDTO();
    documentDTO.setFolderId(documentForm.getId());
    documentDTO.setCreateVersion(documentForm.isCreateVersion());
    documentDTO.setTemplateId(documentForm.getTemplateId());
    documentDTO.setTenantId(documentForm.getTenantId());
    documentDTO.setFilename(documentForm.getTemplateName());
    documentDTO.setSubmitDate(documentForm.getSubmitDate());
    documentDTO.setGenerateProofTemplate(documentForm.getGenerateProofTemplate());
    documentDTO.setProcurement(documentForm.getProcurement());
    documentDTO.setDepartment(
      DepartmentHistoryFormMapper.INSTANCE.toDepartmentHistoryDTO(documentForm.getDepartment()));
    documentDTO.setIsCompanyCertificate(documentForm.getIsCompanyCertificate());
    documentDTO.setDeptAmounts(documentForm.getDeptAmounts());
    documentDTO.setExpirationDate(documentForm.getExpirationDate());
    documentDTO.setTitle(StringUtils.isNotBlank(documentForm.getTitle()) ? documentForm.getTitle()
      : documentForm.getTemplateName());
    documentDTO.setProjectDocument(documentForm.isProjectDocument());
    if (documentForm.getNachtragId() != null) {
      documentDTO.setNachtragId(documentForm.getNachtragId());
    }
    if (documentForm.getLegalHearingType() != null) {
      documentDTO.setLegalHearingType(documentForm.getLegalHearingType());
    }
    if (documentForm.getLegalHearingExclusion() != null) {
      documentDTO.setLegalHearingExclusion(LegalHearingExclusionFormMapper.INSTANCE
        .toLegalHearingExclusionDTO(documentForm.getLegalHearingExclusion()));
    }
    if (documentForm.getFirstSignature() != null) {
      documentDTO.setFirstSignature(documentForm.getFirstSignature());
    }
    if (documentForm.getSecondSignature() != null) {
      documentDTO.setSecondSignature(documentForm.getSecondSignature());
    }
    if (documentForm.getSignatureCopies() != null && !documentForm.getSignatureCopies().isEmpty()) {
      documentDTO.setSignatureCopies(documentForm.getSignatureCopies());
    }
    createdDocumentIds = documentService.createDocumentFromTemplate(documentDTO);
    return Response.ok(createdDocumentIds).build();
  }

  /**
   * Download company document.
   *
   * @param id the id
   * @param templateShortCode the template short code
   * @param expirationDate the expiration date
   * @param deptAmounts the dept amounts
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces("application/octet-stream")
  @Path("/downloadCompanyDocument/{templateShortCode}")
  public Response downloadCompanyDocument(
    @PathParam("templateShortCode") String templateShortCode,
    @QueryParam("id") String id,
    @QueryParam("expirationDate") Long expirationDate,
    @QueryParam("deptAmounts") BigDecimal deptAmounts,
    @QueryParam("deptAmountAction") String deptAmountAction) {
    DocumentDTO documentDTO = new DocumentDTO();
    documentDTO.setFolderId(id);
    documentDTO.setDeptAmounts(deptAmounts);
    documentDTO.setExpirationDate(toDate(expirationDate));
    documentDTO.setDeptAmountAction(deptAmountAction);

    byte[] content = documentService
      .generateCompanyPartDocument(documentDTO, templateShortCode);

    ResponseBuilder response = Response.ok(content);
    if (content != null && content.length != 0) {
      response.header(CONTENT_DISPOSITION,
        ATTACHMENT_FILENAME + documentDTO.getFilename() + "\"");
    }
    return response.build();
  }

  /**
   * Download and print company document.
   *
   * @param id the id
   * @param templateShortCode the template short code
   * @param expirationDate the expiration date
   * @param deptAmounts the dept amounts
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces("application/octet-stream")
  @Path("/downloadCompanyDocument/print/{templateShortCode}")
  public Response downloadAndPrintCompanyDocument(
    @PathParam("templateShortCode") String templateShortCode,
    @QueryParam("id") String id,
    @QueryParam("expirationDate") Long expirationDate,
    @QueryParam("deptAmounts") BigDecimal deptAmounts,
    @QueryParam("deptAmountAction") String deptAmountAction) {

    DocumentDTO documentDTO = new DocumentDTO();
    documentDTO.setFolderId(id);
    documentDTO.setDeptAmounts(deptAmounts);
    documentDTO.setExpirationDate(toDate(expirationDate));
    documentDTO.setDeptAmountAction(deptAmountAction);

    byte[] content = documentService
      .generateCompanyPartDocument(documentDTO, templateShortCode);

    return printGeneratedDocuments(content, documentDTO);
  }

  /**
   * Document version exists.
   *
   * @param documentForm the document form
   * @return the response
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.TEXT_PLAIN)
  @Path("/documentVersionExists")
  public Response documentVersionExists(DocumentForm documentForm) {
    VersionDTO latestDocVersion = null;
    DocumentDTO documentDTO = new DocumentDTO();
    documentDTO.setFolderId(documentForm.getId());
    documentDTO.setTemplateId(documentForm.getTemplateId());
    documentDTO.setTenantId(documentForm.getTenantId());

    if (documentForm.getLegalHearingType() != null
      && documentForm.getLegalHearingType().equals("EXCLUSION")
      && !documentForm.getLegalHearingExclusion().isEmpty()) {
      documentDTO.setLegalHearingType(documentForm.getLegalHearingType());
      for (LegalExclusionForm legal : documentForm.getLegalHearingExclusion()) {
        latestDocVersion =
          documentService.getDocLatestversion(documentDTO, legal.getSubmittent().getId(), null);
        if (latestDocVersion != null) {
          break;
        }
      }
    } else if(documentForm.getTemplateName().equals("Nachtrag")){
      documentDTO.setNachtragId(documentForm.getNachtragId());
      latestDocVersion =
        documentService.getDocLatestversion(documentDTO, documentForm.getNachtragSubmittentId(),
          documentForm.getNachtragCompanyId());
    } else {
      if (documentForm.getLegalHearingType() != null
        && !documentForm.getLegalHearingType().equals("EXCLUSION")) {
        documentDTO.setLegalHearingType(documentForm.getLegalHearingType());
      }
      latestDocVersion = documentService.getDocLatestversion(documentDTO, null, null);
    }

    if (!documentForm.isCreateVersion() && latestDocVersion != null) {
      return Response.ok("versionExists").build();
    }

    return Response.ok("versionNotExists").build();
  }

  /**
   * Download document.
   *
   * @param id the id
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces("application/octet-stream")
  @Path("/downloadDocument/{id}")
  public Response downloadDocument(@PathParam("id") String id) {
    VersionDTO version = documentService.getVersionById(id);
    byte[] content = documentService.downloadDocument(version.getNodeId(), version.getName());
    ResponseBuilder response = Response.ok(content);
    if (content != null) {
      response.header(CONTENT_DISPOSITION,
        ATTACHMENT_FILENAME + version.getFilename() + "\"");
    }
    return response.build();
  }

  /**
   * Download multiple documents.
   *
   * @param ids the ids
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces("application/octet-stream")
  @Path("/downloadMultipleDocuments")
  public Response downloadMultipleDocuments(@QueryParam("id") List<String> ids) {

    byte[] documentsZip = documentService.downloadMultipleDocuments(ids);

    ResponseBuilder response = Response.ok(documentsZip);
    if (documentsZip != null) {
      response.header(CONTENT_DISPOSITION, ATTACHMENT_FILENAME + "Dokumente.zip\"");
    }

    return response.build();
  }

  /**
   * Upload document.
   *
   * @param versionId the version id
   * @param uploadedFileId the uploaded file id
   * @param replaceDocument the replace document
   * @param isProjectDocument the is project document
   * @return the response
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/uploadDocument/{versionId}/{uploadedFileId}/{replaceDocument}/{isProjectDocument}")
  public Response uploadDocument(@PathParam("versionId") String versionId,
    @PathParam("uploadedFileId") String uploadedFileId,
    @PathParam("replaceDocument") Boolean replaceDocument,
    @PathParam("isProjectDocument") Boolean isProjectDocument) {

    Set<ValidationError> errors = new HashSet<>();
    documentService.uploadDocument(null, versionId, uploadedFileId, replaceDocument,
      isProjectDocument, errors);

    if (errors.isEmpty()) {
      return Response.ok(null).build();
    } else {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
  }

  /**
   * Delete document.
   *
   * @param versionId the version id
   * @param reason the reason
   * @return the response
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/deleteDocument/{templateId}/{versionId}/{reason: .*}")
  public Response deleteDocument(@PathParam("templateId") String templateId,
    @PathParam("versionId") String versionId, @PathParam("reason") String reason) {
    Set<ValidationError> errors = new HashSet<>();
    if (reason.isEmpty() && documentService.isDeletionReasonMandatory(templateId)) {
      errors.add(new ValidationError("reasonErrorField",
        ValidationMessages.MANDATORY_ERROR_MESSAGE));
      errors.add(new ValidationError("reason",
        ValidationMessages.MANDATORY_ERROR_MESSAGE));
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    documentService.deleteDocument(versionId, reason);
    return Response.ok().build();
  }

  /**
   * Checks if document deletion reason is mandatory.
   *
   * @param templateId the template id
   * @return true, if deletion reason is mandatory
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/isDeletionReasonMandatory/{templateId}")
  public Response isDeletionReasonMandatory(@PathParam("templateId") String templateId) {
    return Response.ok(documentService.isDeletionReasonMandatory(templateId)).build();
  }

  /**
   * Check if statuses award evaluation closed or manual award completed have been set before.
   *
   * @param submissionId the submission id
   * @return true/false
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/haveAwardStatusesBeenClosed/{submissionId}")

  public Response haveAwardStatusesBeenClosed(@PathParam("submissionId") String submissionId) {
    return Response.ok(submissionService.haveAwardStatusesBeenClosed(submissionId)).build();
  }

  /**
   * This function handles the document creation validation for the project and company part.
   *
   * @param document the document
   * @return the sets the
   */
  private Set<ValidationError> validation(DocumentForm document) {
    Set<ValidationError> errors = new HashSet<>();
    // Validation for empty template
    if (document.getTemplateId() == null
      && (document.getGenerateProofTemplate() == null || !document.getGenerateProofTemplate())
      && (document.getIsCompanyCertificate() == null || !document.getIsCompanyCertificate())) {
      errors.add(new ValidationError("templateNotSelectedError",
        ValidationMessages.NO_TEMPLATE_SELECTED));
      errors.add(new ValidationError("template",
        ValidationMessages.NO_TEMPLATE_SELECTED));
    }

    if ((document.getGenerateProofTemplate() != null && document.getGenerateProofTemplate())
      && document.getSubmitDate() == null) {
      errors.add(new ValidationError(SUBMIT_DATE,
        ValidationMessages.MANDATORY_ERROR_MESSAGE));
      errors.add(new ValidationError(SUBMIT_DATE_ERROR_FIELD,
        ValidationMessages.MANDATORY_ERROR_MESSAGE));
    }

    // Validation for "Nachweisbrief" creation.
    if (document.getTemplateName() != null && document.getTemplateName().equals(NACHWEISBRIEF)) {
      errors = validateProofRequestInCompanyPart(document);
    }

    return errors;
  }

  /**
   * Validates proof request in company part.
   *
   * @param document the document
   * @return validation errors (if present)
   */
  private Set<ValidationError> validateProofRequestInCompanyPart(DocumentForm document) {
    Set<ValidationError> errors = new HashSet<>();
    // Check if a submit date has been given.
    if (document.getSubmitDate() == null) {
      errors.add(new ValidationError(SUBMIT_DATE,
        ValidationMessages.MANDATORY_ERROR_MESSAGE));
      errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
        ValidationMessages.MANDATORY_ERROR_MESSAGE));
    }
    return errors;
  }

  /**
   * To date.
   *
   * @param date the date
   * @return the date
   */
  private Date toDate(Long date) {
    return (date != null) ? new Date(date) : null;
  }

  /**
   * Check if the commission procurement proposal has been closed before.
   *
   * @param submissionId the submission id
   * @return true/false
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/hasCommissionProcurementProposalBeenClosed/{submissionId}")
  public Response hasCommissionProcurementProposalBeenClosed(
    @PathParam("submissionId") String submissionId) {
    return Response.ok(submissionService.hasCommissionProcurementProposalBeenClosed(submissionId))
      .build();
  }

  /**
   * Check if the commission procurement decision has been closed before.
   *
   * @param submissionId the submission id
   * @return true/false
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/hasCommissionProcurementDecisionBeenClosed/{submissionId}")
  public Response hasCommissionProcurementDecisionBeenClosed(
    @PathParam("submissionId") String submissionId) {
    return Response.ok(submissionService.hasCommissionProcurementDecisionBeenClosed(submissionId))
      .build();
  }


  /**
   * updates the title and the Vertaulich value(true/false) of a Document and is associated with UC
   * 114,187.
   *
   * @param documentEditForm the document edit form
   * @return the response
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/updateDocumentProperties/")
  public Response updateDocumentProperties(DocumentEditForm documentEditForm) {
    Set<ValidationError> errors = validation(documentEditForm);
    if (!errors.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
    DocumentDTO documentDTO = new DocumentDTO();
    documentDTO.setId(documentEditForm.getId());
    documentDTO.setTitle(documentEditForm.getTitle());
    documentDTO.setPrivateDocument(documentEditForm.getPrivateDocument());
    documentDTO.setLastModifiedOn(documentEditForm.getLastModifiedOn());
    documentService.updateDocumentProperties(documentDTO,
      documentEditForm.getPrivateDocumentSetChanged());
    return Response.ok().build();
  }

  /**
   * handles the validation errors of updateDocumentProperties action *.
   *
   * @param document the document
   * @return the sets the
   */
  private Set<ValidationError> validation(DocumentEditForm document) {
    Set<ValidationError> errors = new HashSet<>();
    if (document.getTitle() == null || document.getTitle().isEmpty()) {
      errors.add(new ValidationError("titleNotGivenError",
        ValidationMessages.MANDATORY_ERROR_MESSAGE));
      errors.add(new ValidationError("title",
        ValidationMessages.MANDATORY_ERROR_MESSAGE));
    }
    if (document.getTitle().length() > 100) {
      errors.add(new ValidationError("titleTooBig",
        ValidationMessages.TITLE_DOC_ERROR_MESSAGE));
      errors.add(new ValidationError("title",
        ValidationMessages.TITLE_DOC_ERROR_MESSAGE));
    }
    return errors;
  }

  /**
   * Load document directorate.
   *
   * @param documentId the document id
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/loadDocumentDirectorate/{documentId}")
  public Response loadDocumentDirectorate(@PathParam("documentId") String documentId) {
    return Response.ok(documentService.loadDocumentDirectorate(documentId)).build();
  }

  /**
   * Download project part documents.
   *
   * @param id the id
   * @param templateId the template short code
   * @param expirationDate the expiration date
   * @param deptAmounts the dept amounts
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces("application/octet-stream")
  @Path("/downloadProjectDocument/{id}/{templateId}")
  public Response downloadProjectDocument(@PathParam("id") String id,
    @PathParam("templateId") String templateId, @QueryParam("expirationDate") Long expirationDate,
    @QueryParam("deptAmounts") BigDecimal deptAmounts) {
    DocumentDTO documentDTO = new DocumentDTO();
    documentDTO.setFolderId(id);
    documentDTO.setDeptAmounts(deptAmounts);
    documentDTO.setExpirationDate(toDate(expirationDate));
    documentDTO.setTemplateId(templateId);

    byte[] content = documentService.generateProjectPartDocument(documentDTO);

    ResponseBuilder response = Response.ok(content);
    if (content != null && content.length != 0) {
      response.header(CONTENT_DISPOSITION,
        ATTACHMENT_FILENAME + documentDTO.getFilename() + "\"");
    }
    return response.build();
  }

  /**
   * Download and print project part documents.
   *
   * @param id the id
   * @param templateId the template short code
   * @param expirationDate the expiration date
   * @param deptAmounts the dept amounts
   * @return the response
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces("application/octet-stream")
  @Path("/downloadProjectDocument/print/{id}/{templateId}")
  public Response downloadAndPrintProjectDocument(@PathParam("id") String id,
    @PathParam("templateId") String templateId, @QueryParam("expirationDate") Long expirationDate,
    @QueryParam("deptAmounts") BigDecimal deptAmounts) {
    DocumentDTO documentDTO = new DocumentDTO();
    documentDTO.setFolderId(id);
    documentDTO.setDeptAmounts(deptAmounts);
    documentDTO.setExpirationDate(toDate(expirationDate));
    documentDTO.setTemplateId(templateId);

    byte[] content = documentService.generateProjectPartDocument(documentDTO);

    return printGeneratedDocuments(content, documentDTO);
  }

  private Response printGeneratedDocuments(byte[] content, DocumentDTO documentDTO) {
    if (userService.getTenant().getIsMain()) {
      /* Only the main tenant has the right to print from the server */
      SettingsDTO settings = sDService.getPrinterSettings();

      // check for printer settings
      if (settings != null && settings.getIpPrinterAddress() != null
        && StringUtils.isNotBlank(settings.getIpPrinterAddress())) {
        submissPrintService
          .printGeneratedDocument(content, documentDTO.getFilename());
        return Response.ok(null).build();
      }
    }
    // otherwise print from the browser
    return browserResponse(content, documentDTO);
  }

  /**
   * Response for browser printing of generated documents. Refactors code to prevent duplicates
   *
   * @param content the document byte array
   * @param documentDTO the documentDTO
   * @return the response
   */
  private Response browserResponse(byte[] content, DocumentDTO documentDTO) {
    if (documentDTO.getFilename().endsWith("docx")) {
      content = submissPrintService.convertToPDF(content);
      documentDTO.setFilename(submissPrintService.getPdfExtension(documentDTO.getFilename()));
    }
    ResponseBuilder response = Response.ok(content);
    if (content != null && content.length != 0) {
      response.header(CONTENT_DISPOSITION,
        ATTACHMENT_FILENAME + documentDTO.getFilename() + "\"");
    }
    return response.build();
  }

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/getDocumentVersionsByFilename/{folderId}")
  public Response getDocumentVersionsByFilename(@PathParam("folderId") String folderId,
    @Valid List<String> filenames) {
    List<DocumentDTO> temp = documentService.getDocumentVersionsByFilename(folderId, filenames);
    return Response.ok(temp).build();
  }

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/getPermittedDocumentsForUpload/{folderId}/{isProjectPart}")
  public Response getPermittedDocumentsForUpload(@PathParam("folderId") String folderId,
    @PathParam("isProjectPart") boolean isProjectPart,
    @Valid List<String> filenames) {
    List<String> temp =
      documentService.permittedDocumentsForUpload(folderId, filenames, isProjectPart);
    return Response.ok(temp).build();
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/uploadMultipleDocuments")
  public Response uploadMultipleDocuments(List<DocumentUploadForm> uploadedDocumentList) {

    Set<ValidationError> errors = new HashSet<>();
    for (DocumentUploadForm documentUploadForm : uploadedDocumentList) {
      documentService.uploadDocument(documentUploadForm.getFolderId(),
        documentUploadForm.getVersionId(), documentUploadForm.getUploadedFileId(),
        !documentUploadForm.getVersionDocument(), documentUploadForm.getProjectDocument(),
        errors);
    }

    if (errors.isEmpty()) {
      return Response.ok(null).build();
    } else {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
  }

  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces("application/octet-stream")
  @Path("/printDocument/{id}")
  public Response printDocument(@PathParam("id") String id) {
    VersionDTO version = documentService.getVersionById(id);
    SettingsDTO settings = (userService.getTenant().getIsMain())
      ? sDService.getPrinterSettings()
      : null;
    // check for printer settings
    if (settings != null && settings.getIpPrinterAddress() != null
      && StringUtils.isNotBlank(settings.getIpPrinterAddress())) {
      submissPrintService
        .printDocument(version.getNodeId(), version.getName(), version.getFilename());
      return Response.ok(null).build();
    } else {
      // browser printing
      return getDownloadResponse(version);
    }
  }

  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces("application/octet-stream")
  @Path("/downloadPDFDocument/{id}")
  public Response downloadPDFDocument(@PathParam("id") String id) {
    VersionDTO version = documentService.getVersionById(id);
    return getDownloadResponse(version);
  }

  /**
   * Downloads a document from the db. Refactors code to prevent duplicates
   *
   * @return the response
   */
  private Response getDownloadResponse(VersionDTO version) {
    byte[] pdfcontent;
    if (version.getMimetype().equals("application/pdf")) { // for pdf files
      pdfcontent = documentService.downloadDocument(version.getNodeId(), version.getName());
    } else if (version.getMimetype().equals("image/png")) { // for image files
      pdfcontent = submissPrintService.convertImageToPDF(
        documentService.downloadDocument(version.getNodeId(), version.getName()));
    } else { // for docx and txt files
      pdfcontent = submissPrintService
        .convertToPDF(documentService.downloadDocument(version.getNodeId(), version.getName()));
    }

    String filename = submissPrintService.getPdfExtension(version.getFilename());

    ResponseBuilder response = Response.ok(pdfcontent);
    if (pdfcontent != null) {
      response.header(CONTENT_DISPOSITION,
        ATTACHMENT_FILENAME + filename + "\"");
      response.header("Content-type", "application/pdf");
    }
    return response.build();
  }

  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces("application/octet-stream")
  @Path("/generateCompaniesListDocument/{shortCode}")
  public Response generateCompaniesListDocument(@PathParam("shortCode") String shortCode) {
    DocumentDTO documentDTO = new DocumentDTO();
    byte[] content = documentService
      .generateCompanySearchDocuments(documentDTO, shortCode, null, null, null, null);
    ResponseBuilder response = Response.ok(content);
    if (content != null) {
      response.header(CONTENT_DISPOSITION, ATTACHMENT_FILENAME + documentDTO.getFilename() + "\"");
    }
    return response.build();
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces("application/octet-stream")
  @Path("/generateSearchedCompaniesDocument/{shortCode}/{page}/{pageItems}/{sortColumn}/{sortType}")
  public Response generateCompaniesListDocument(CompanySearchForm searchForm,
    @PathParam("shortCode") String shortCode, @PathParam("page") int page,
    @PathParam("pageItems") int pageItems, @PathParam("sortColumn") String sortColumn,
    @PathParam("sortType") String sortType) {
    DocumentDTO documentDTO = new DocumentDTO();
    CompanySearchDTO searchDTO = CompanySearchMapper.INSTANCE.toCompanySearchDTO(searchForm);
    List<CompanyDTO> companyDTOList =
      companyService.search(searchDTO, page, pageItems, sortColumn, sortType);
    byte[] content =
      documentService
        .generateCompanySearchDocuments(documentDTO, shortCode, companyDTOList, sortColumn,
          sortType, searchDTO);
    ResponseBuilder response = Response.ok(content);
    if (content != null) {
      response.header(CONTENT_DISPOSITION, ATTACHMENT_FILENAME + documentDTO.getFilename() + "\"");
    }
    return response.build();
  }

  /**
   * Function to display empty first signature error.
   *
   * @return the response
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/emptyFirstSignature")
  public Response emptyFirstSignature() {
    Set<ValidationError> errors = new HashSet<>();
    errors.add(new ValidationError("mandatoryErrorField",
      ValidationMessages.MANDATORY_ERROR_MESSAGE));
    errors.add(new ValidationError("firstSignature",
      ValidationMessages.MANDATORY_ERROR_MESSAGE));
    errors.add(new ValidationError("department",
      ValidationMessages.MANDATORY_ERROR_MESSAGE));
    return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
  }

  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/validateCertificate/{deptamounts}/{nullFromUser}")
  public Response cetrificateValidation(@PathParam("deptamounts") String deptamounts,
    @PathParam("nullFromUser") boolean nullFromUser) {
    Set<ValidationError> errors = new HashSet<>();
    // the boolean variable nullFromUser is a way to separate if null value to
    // deptAmounts is just an empty input or a string "null" set from the user.
    // if is "null" from the user,then this variable comes as true and an error message
    // has to be shown.
    if (!deptamounts.equals(DEPT_AMOUNTS_EMPTY_VALUE) || nullFromUser) {
      try {
        new BigDecimal(deptamounts);
      } catch (NumberFormatException e) {
        errors.add(new ValidationError("invalid_number_error",
          ValidationMessages.INVALID_NUMBER_ERROR_MESSAGE));
        errors.add(new ValidationError("deptAmounts",
          ValidationMessages.INVALID_NUMBER_ERROR_MESSAGE));
      }

    }

    if (errors.isEmpty()) {
      return Response.ok(null).build();
    } else {
      return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
  }

  /**
   * Checks for Vertrag (Contract) documents validation.
   *
   * @param submissionId the submissionId
   * @return the response
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/contract/{submissionId}")
  public Response checkContractDocument(@PathParam("submissionId") String submissionId) {
    // Validation for Vertrag (Contract) documents
    Set<ValidationError> errors = documentService.contractDocumentValidation(submissionId);
    return (errors.isEmpty())
      ? Response.ok().build()
      : Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
  }

  /**
   * Check if given document exists
   *
   * @param submissionId the submission id
   * @param templateId the template id
   * @return true/false
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/documentExists/{submissionId}/{templateId}")
  public Response documentExists(@PathParam("submissionId") String submissionId,
    @PathParam("templateId") String templateId) {
    return Response.ok(documentService.documentExists(submissionId, templateId))
      .build();
  }

}