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

package ch.bern.submiss.services.impl.administration;

import ch.bern.submiss.services.api.administration.CompanyService;
import ch.bern.submiss.services.api.administration.LegalHearingService;
import ch.bern.submiss.services.api.administration.LexiconService;
import ch.bern.submiss.services.api.administration.NachtragService;
import ch.bern.submiss.services.api.administration.OfferService;
import ch.bern.submiss.services.api.administration.RuleService;
import ch.bern.submiss.services.api.administration.SDDepartmentService;
import ch.bern.submiss.services.api.administration.SDDirectorateService;
import ch.bern.submiss.services.api.administration.SDService;
import ch.bern.submiss.services.api.administration.SDTenantService;
import ch.bern.submiss.services.api.administration.SDWorkTypeService;
import ch.bern.submiss.services.api.administration.SubDocumentService;
import ch.bern.submiss.services.api.administration.SubmissPrintService;
import ch.bern.submiss.services.api.administration.SubmissTaskService;
import ch.bern.submiss.services.api.administration.SubmissionCancelService;
import ch.bern.submiss.services.api.administration.SubmissionCloseService;
import ch.bern.submiss.services.api.administration.SubmissionService;
import ch.bern.submiss.services.api.administration.UserAdministrationService;
import ch.bern.submiss.services.api.constants.AuditEvent;
import ch.bern.submiss.services.api.constants.AuditGroupName;
import ch.bern.submiss.services.api.constants.AuditLevel;
import ch.bern.submiss.services.api.constants.AuditMessages;
import ch.bern.submiss.services.api.constants.CategorySD;
import ch.bern.submiss.services.api.constants.CommissionProcurementProposalReservation;
import ch.bern.submiss.services.api.constants.DocumentAttributes;
import ch.bern.submiss.services.api.constants.DocumentCreationType;
import ch.bern.submiss.services.api.constants.DocumentPlaceholders;
import ch.bern.submiss.services.api.constants.DocumentProperties;
import ch.bern.submiss.services.api.constants.Group;
import ch.bern.submiss.services.api.constants.Process;
import ch.bern.submiss.services.api.constants.ProofStatus;
import ch.bern.submiss.services.api.constants.Rule;
import ch.bern.submiss.services.api.constants.SelectiveLevel;
import ch.bern.submiss.services.api.constants.TaskTypes;
import ch.bern.submiss.services.api.constants.Template;
import ch.bern.submiss.services.api.constants.Template.TEMPLATE_NAMES;
import ch.bern.submiss.services.api.constants.TemplateConstants;
import ch.bern.submiss.services.api.constants.TenderStatus;
import ch.bern.submiss.services.api.dto.AwardEvaluationDocumentDTO;
import ch.bern.submiss.services.api.dto.AwardInfoDTO;
import ch.bern.submiss.services.api.dto.AwardInfoFirstLevelDTO;
import ch.bern.submiss.services.api.dto.CompanyDTO;
import ch.bern.submiss.services.api.dto.CompanyOfferDTO;
import ch.bern.submiss.services.api.dto.CompanyProofDTO;
import ch.bern.submiss.services.api.dto.CompanySearchDTO;
import ch.bern.submiss.services.api.dto.DepartmentHistoryDTO;
import ch.bern.submiss.services.api.dto.DirectorateHistoryDTO;
import ch.bern.submiss.services.api.dto.DocumentDTO;
import ch.bern.submiss.services.api.dto.DocumentPropertiesDTO;
import ch.bern.submiss.services.api.dto.ExclusionReasonDTO;
import ch.bern.submiss.services.api.dto.LegalHearingExclusionDTO;
import ch.bern.submiss.services.api.dto.MasterListValueDTO;
import ch.bern.submiss.services.api.dto.MasterListValueHistoryDTO;
import ch.bern.submiss.services.api.dto.NachtragDTO;
import ch.bern.submiss.services.api.dto.OfferCriterionDTO;
import ch.bern.submiss.services.api.dto.OfferDTO;
import ch.bern.submiss.services.api.dto.ProofHistoryDTO;
import ch.bern.submiss.services.api.dto.SignatureCopyDTO;
import ch.bern.submiss.services.api.dto.SignatureProcessTypeEntitledDTO;
import ch.bern.submiss.services.api.dto.SubmissUserDTO;
import ch.bern.submiss.services.api.dto.SubmissionDTO;
import ch.bern.submiss.services.api.dto.SubmissionOverviewDTO;
import ch.bern.submiss.services.api.dto.SubmittentDTO;
import ch.bern.submiss.services.api.dto.SubmittentOfferDTO;
import ch.bern.submiss.services.api.dto.SuitabilityDocumentDTO;
import ch.bern.submiss.services.api.dto.TenantDTO;
import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.api.util.LookupValues.USER_ATTRIBUTES;
import ch.bern.submiss.services.api.util.SubmissConverter;
import ch.bern.submiss.services.api.util.ValidationMessages;
import ch.bern.submiss.services.impl.mappers.DepartmentHistoryMapper;
import ch.bern.submiss.services.impl.mappers.DirectorateHistoryMapper;
import ch.bern.submiss.services.impl.mappers.DocumentMapper;
import ch.bern.submiss.services.impl.mappers.SubmittentDTOMapper;
import ch.bern.submiss.services.impl.model.CompanyEntity;
import ch.bern.submiss.services.impl.model.DirectorateHistoryEntity;
import ch.bern.submiss.services.impl.model.DocumentDeadlineEntity;
import ch.bern.submiss.services.impl.model.LegalHearingExclusionEntity;
import ch.bern.submiss.services.impl.model.MasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.QCompanyEntity;
import ch.bern.submiss.services.impl.model.QDirectorateHistoryEntity;
import ch.bern.submiss.services.impl.model.QDocumentDeadlineEntity;
import ch.bern.submiss.services.impl.model.QMasterListValueHistoryEntity;
import ch.bern.submiss.services.impl.model.QSubmittentEntity;
import ch.bern.submiss.services.impl.model.SubmissionEntity;
import ch.bern.submiss.services.impl.model.SubmittentEntity;
import ch.bern.submiss.services.impl.model.TenantEntity;
import ch.bern.submiss.services.impl.util.ComparatorUtil;
import com.eurodyn.qlack2.fuse.aaa.api.dto.GroupDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO;
import com.eurodyn.qlack2.fuse.cm.api.DocumentService;
import com.eurodyn.qlack2.fuse.cm.api.VersionService;
import com.eurodyn.qlack2.fuse.cm.api.dto.CreateFileAndVersionStatusDTO;
import com.eurodyn.qlack2.fuse.cm.api.dto.FileDTO;
import com.eurodyn.qlack2.fuse.cm.api.dto.FolderDTO;
import com.eurodyn.qlack2.fuse.cm.api.dto.NodeDTO;
import com.eurodyn.qlack2.fuse.cm.api.dto.VersionDTO;
import com.eurodyn.qlack2.fuse.fileupload.api.FileUpload;
import com.eurodyn.qlack2.fuse.fileupload.api.response.FileGetResponse;
import com.eurodyn.qlack2.fuse.ts.api.TemplateService;
import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;
import com.google.common.collect.Lists;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.OptimisticLockException;
import javax.transaction.Transactional;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.ops4j.pax.cdi.api.OsgiService;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * The Class SubDocumentServiceImpl.
 */
@Transactional
@OsgiServiceProvider(classes = {SubDocumentService.class})
@Singleton
public class SubDocumentServiceImpl extends BaseService implements SubDocumentService {

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER = Logger.getLogger(SubDocumentServiceImpl.class.getName());

  /**
   * The Constant DEFAULT_DOUBLE.
   */
  private static final Double DEFAULT_DOUBLE = Double.valueOf(0);

  /**
   * The Constant S_OBJ_PRO_WORKTYPE_DESCR.
   */
  private static final String S_OBJ_PRO_WORKTYPE_DESCR = "s_obj_pro_worktype_descr";

  /**
   * The Constant REPEAT_HEADER.
   */
  private static final String REPEAT_HEADER = "repeatHeader";

  /**
   * The version service.
   */
  @OsgiService
  @Inject
  protected VersionService versionService;

  /**
   * The document service.
   */
  @OsgiService
  @Inject
  protected DocumentService documentService;

  // Use these annotation in order to inject Qlack services with the same name in different classes.
  // Add different names
  /**
   * The template service.
   */
  // for this injection (See also EmailServiceImpl).
  @OsgiService
  @Autowired
  @Qualifier("templateService2")
  protected TemplateService templateService;

  /**
   * The submission service.
   */
  @Inject
  protected SubmissionService submissionService;
  /**
   * The users service.
   */
  @Inject
  protected UserAdministrationService usersService;
  /**
   * The company service.
   */
  @Inject
  protected CompanyService companyService;
  /**
   * The rule service.
   */
  @Inject
  protected RuleService ruleService;
  /**
   * The department code service.
   */
  @Inject
  protected SDDepartmentService sDDepartmentService;
  /**
   * The lexicon service.
   */
  @Inject
  protected LexiconService lexiconService;
  /**
   * The submission cancel service.
   */
  @Inject
  protected SubmissionCancelService submissionCancelService;
  /**
   * The s D directorate service.
   */
  protected SDDirectorateService sDDirectorateService;
  /**
   * The SDWorkType service.
   */
  @Inject
  protected SDWorkTypeService sDWorktypeService;
  /**
   * The tenant service.
   */
  @Inject
  protected SDTenantService sDTenantService;
  /**
   * The offerService service.
   */
  @Inject
  protected OfferService offerService;
  /**
   * The task service.
   */
  @Inject
  protected SubmissTaskService taskService;
  /**
   * The legal hearing service.
   */
  @Inject
  protected LegalHearingService legalHearingService;
  /**
   * The template bean.
   */
  @Inject
  protected TemplateBean templateBean;
  /**
   * The submiss print service.
   */
  @Inject
  protected SubmissPrintService submissPrintService;
  /**
   * The file upload.
   */
  @OsgiService
  @Inject
  private FileUpload fileUpload;
  /**
   * The audit.
   */
  @Inject
  private AuditBean audit;
  /**
   * The cache bean.
   */
  @Inject
  private CacheBean cacheBean;

  /**
   * The import export file service.
   */
  @Inject
  private ImportExportFileServiceImpl importExportFileService;

  /**
   * The criterion service.
   */
  @Inject
  private CriterionServiceImpl criterionService;

  /**
   * The SD service.
   */
  @Inject
  private SDService sDService;

  @Inject
  private SubmissionCloseService submissionCloseService;

  @Inject
  private NachtragService nachtragService;

  /**
   * The q master list value history entity.
   */
  private QMasterListValueHistoryEntity qMasterListValueHistoryEntity =
    QMasterListValueHistoryEntity.masterListValueHistoryEntity;

  /**
   * The q document deadline entity.
   */
  private QDocumentDeadlineEntity qDocumentDeadlineEntity = QDocumentDeadlineEntity.documentDeadlineEntity;


  @Override
  public List<DocumentDTO> getDocumentListProject(String folderId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getDocumentListProject, Parameters: folderId: {0}",
      folderId);

    QSubmittentEntity qSubmittentEntity = QSubmittentEntity.submittentEntity;
    List<DocumentDTO> documentDTOList = new ArrayList<>();
    FolderDTO folderDTO = documentService.getFolderByID(folderId, false, false);
    if (folderDTO == null) {
      documentDTOList = new ArrayList<>();
    } else {
      /*
       * In this part of the function we get the submittents of the specific submission and map the
       * associated company name in order to avoid multiple calls to the database
       */
      List<SubmittentEntity> submittents = new JPAQueryFactory(em).selectFrom(qSubmittentEntity)
        .where(qSubmittentEntity.submissionId.id.eq(folderId)).fetch();
      HashMap<String, String> companyMap = new HashMap<>();
      for (SubmittentEntity s : submittents) {
        companyMap.put(s.getCompanyId().getId(), s.getCompanyId().getCompanyName());
        for (CompanyEntity jointVenture : s.getJointVentures()) {
          companyMap.put(jointVenture.getId(), jointVenture.getCompanyName());
        }
        for (CompanyEntity sub : s.getSubcontractors()) {
          companyMap.put(sub.getId(), sub.getCompanyName());
        }
      }
      Map<String, DepartmentHistoryDTO> departmentMapForProject = getDepartmentHistoryDTO();

      /* get the template types that can be uploaded by the user */
      List<MasterListValueHistoryDTO> permittedUploadTemplateDTOs =
        ruleService.getProjectUploadAllowedTemplates();
      List<String> permittedUploadTemplates = new ArrayList<>();
      List<String> nachweisbriefTemplates = new ArrayList<>();
      if (permittedUploadTemplateDTOs != null) {
        for (MasterListValueHistoryDTO dto : permittedUploadTemplateDTOs) {
          permittedUploadTemplates.add(dto.getMasterListValueId().getId());
          // Store the templateIds of the Nachweisbrief PT and Nachweisbrief Sub on separate list,
          // because in case of this type of template a special check must
          // be made whether it can be deleted
          if (dto.getShortCode().equals(Template.NACHWEISBRIEF_SUB)
            || dto.getShortCode().equals(Template.NACHWEISBRIEF_PT)) {
            nachweisbriefTemplates.add(dto.getMasterListValueId().getId());
          }
        }
      }

      boolean canDocumentBeUploaded;
      boolean canDocumentBeDeleted;

      SubmissionDTO submissionDTO = submissionService.getSubmissionById(folderId);
      submissionDTO.setCurrentState(TenderStatus.fromValue(submissionDTO.getStatus()));

      /* get the template types that can be deleted by the user */
      List<MasterListValueHistoryDTO> permittedDeleteTemplateDTOs =
        ruleService.getProjectAllowedTemplates(submissionDTO);
      permittedDeleteTemplateDTOs
        .addAll(ruleService.getProjectDepartmentTemplates(submissionDTO));
      List<String> permittedDeleteTemplates = new ArrayList<>();
      if (permittedDeleteTemplateDTOs != null) {
        for (MasterListValueHistoryDTO dto : permittedDeleteTemplateDTOs) {
          permittedDeleteTemplates.add(dto.getMasterListValueId().getId());
        }
      }
      String usersGroup = getGroupName(getUser());

      for (NodeDTO nodeDTO : folderDTO.getChildren()) {

        // if the document is private and set so by Admin, then only Admins can view it
        boolean canView = true;
        String isPrivateString =
          nodeDTO.getAttributes().get(DocumentAttributes.PRIVATE_DOCUMENT.name());
        boolean isPrivate =
          isPrivateString != null && isPrivateString.equalsIgnoreCase(Boolean.TRUE.toString());
        GroupDTO privateGroupDTO = null;
        if (isPrivate) {
          String privateGroup = nodeDTO.getAttributes()
            .get(DocumentAttributes.PRIVATE_GROUP.name());
          privateGroupDTO = userGroupService.getGroupByName(privateGroup, true);
          if (privateGroup == null || // this should never be the case
            (privateGroup != null && privateGroup.equals(Group.ADMIN.getValue())
              && !usersGroup.equals(Group.ADMIN.getValue()))) {
            canView = false;
          }
        }

        if (canView) {

          String nodeTemplateId =
            nodeDTO.getAttributes().get(DocumentAttributes.TEMPLATE_ID.name());

          /*
           * check if this document can be uploaded, so if the document type of the document is in
           * the permitted template types
           */
          if (nodeTemplateId == null || permittedUploadTemplates.contains(nodeTemplateId)) {
            canDocumentBeUploaded = true;
          } else {
            canDocumentBeUploaded = false;
          }

          // A document can be deleted only by Admin or PL if it can be created
          // A PL can delete a document only under following conditions
          // 1) in the project part
          // 2) if the process type is NegotiatedTender or NegotiatedCompetitionTender
          // 3) if the submission is below threshold
          // 4) if the creator of the document is PL (this will be checked per document version)
          canDocumentBeDeleted = false;
          if (nodeTemplateId == null) {
            canDocumentBeDeleted = true;
          } else {
            if ((usersGroup.equals(Group.ADMIN.getValue())
              || (usersGroup.equals(Group.PL.getValue())
              && (submissionDTO.getProcess().equals(Process.NEGOTIATED_PROCEDURE)
              || submissionDTO.getProcess()
              .equals(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION))
              && (submissionDTO.getAboveThreshold() == null
              || !submissionDTO.getAboveThreshold())))
              && permittedDeleteTemplates.contains(nodeTemplateId)) {
              canDocumentBeDeleted = true;
            }

            // check if document can be deleted for NACHWEISBRIEF PT or NACHWEISBRIEF Sub
            for (String templateId : nachweisbriefTemplates) {
              if (templateId.equals(nodeTemplateId) && usersGroup.equals(Group.ADMIN.getValue())) {
                canDocumentBeDeleted =
                  findIfCanBeDeletedForNachweisbrief(submissionDTO.getCurrentState());
                break;
              }
            }
          }

          List<VersionDTO> versionList = versionService.getFileVersions(nodeDTO.getId());

          for (VersionDTO v : versionList) {

            DocumentDTO document = DocumentMapper.INSTANCE.toDocumentDTO(v);

            /*
             * In every documentDTO we set the submittentName depending on the mapping we did before
             */
            document.setSubmitentName(
              companyMap.get(nodeDTO.getAttributes().get(DocumentAttributes.COMPANY_ID.name())));
            document.setDepartment(departmentMapForProject
              .get(v.getAttributes().get(DocumentAttributes.DEPARTMENT.name())));
            document
              .setTemplateId(nodeDTO.getAttributes().get(DocumentAttributes.TEMPLATE_ID.name()));
            document.setPrivateDocument(isPrivate);
            if (privateGroupDTO != null) {
              document.setPrivateGroup(privateGroupDTO.getDescription());
            }

            // set actions
            // The group constraints will apply on the front end.
            document.setCanBeDownloaded(true);
            document.setCanBePrinted(true);
            document.setCanBeUploaded(canDocumentBeUploaded);

            String creatorsGroup = v.getAttributes().get(DocumentAttributes.CREATORS_GROUP.name());

            // PL can delete only documents created by PL
            if (canDocumentBeDeleted && usersGroup.equals(Group.PL.getValue())
              && !creatorsGroup.equals(Group.PL.getValue())) {
              canDocumentBeDeleted = false;
            }

            document.setCanBeDeleted(canDocumentBeDeleted);

            // As soon as the document can no longer be deleted it means
            // that it shouldn't be replaced either
            if (!canDocumentBeDeleted) {
              document.setCanBeUploaded(false);
            }

            // if the document is created by Admin, only Admins can update its properties
            boolean canBeEdited = !(creatorsGroup.equals(Group.ADMIN.getValue())
              && !usersGroup.equals(Group.ADMIN.getValue()));

            document.setCanPropertiesBeEdited(canBeEdited);

            // In case a document cann't be delete-uploaded it is also not 
            // available for any further edit
            if (!document.getCanBeDeleted() && !document.getCanBeUploaded()) {
              document.setCanPropertiesBeEdited(false);
            }

            documentDTOList.add(document);
          }
        }
      }
    }
    return documentDTOList;
  }

  @Override
  public List<DocumentDTO> getDocumentListCompany(String folderId, String companyId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getDocumentListCompany, Parameters: folderId: {0}, "
        + "companyId: {1}",
      new Object[]{folderId, companyId});

    List<DocumentDTO> documentDTOList = new ArrayList<>();
    List<FolderDTO> folderDTOs = new ArrayList<>();
    FolderDTO folder = documentService.getFolderByID(folderId, false, false);
    if (folder != null) {
      folderDTOs.add(folder);
    }
    // Retrieve submissions that the company is associated to in order to get the document list.
    // Documents that have been generated on project part should be also visible on company part of
    // the selected company.
    List<SubmissionDTO> submissionDTOs = submissionService.getSubmissionByCompanyId(folderId);
    HashMap<String, String> submissionStatusMap = new HashMap<>();
    for (SubmissionDTO submission : submissionDTOs) {
      FolderDTO submissionFolder = documentService.getFolderByID(submission.getId(), false, false);
      if (submissionFolder != null) {
        folderDTOs.add(submissionFolder);
        // Store submission id and status into a map in order to use it later on delete permision of
        // Nachweisbrief document that has been generated on project part.
        submissionStatusMap.put(submission.getId(), submission.getStatus());
      }
    }
    if (folderDTOs.isEmpty()) {
      documentDTOList = new ArrayList<>();
    } else {
      UserDTO user = getUser();
      /* exclude documents, whose tenant is not permitted to the user */
      List<String> permittedTenants = security.getPermittedTenants(user);

      Map<String, DepartmentHistoryDTO> departmentMapForProject = getDepartmentHistoryDTO();

      /* get the template types that can be uploaded by the user */
      List<MasterListValueHistoryDTO> permittedUploadTemplateDTOs =
        ruleService.getCompanyUploadAllowedTemplates();
      List<String> permittedUploadTemplates = new ArrayList<>();
      List<String> nachweisbriefTemplates = new ArrayList<>();
      if (permittedUploadTemplateDTOs != null) {
        for (MasterListValueHistoryDTO dto : permittedUploadTemplateDTOs) {
          permittedUploadTemplates.add(dto.getMasterListValueId().getId());
          // Store the templateIds of the Nachweisbrief PT and Nachweisbrief Sub on separate list,
          // because in case of this type of template a special check must
          // be made whether it can be deleted
          if (dto.getShortCode().equals(Template.NACHWEISBRIEF_SUB)
            || dto.getShortCode().equals(Template.NACHWEISBRIEF_PT)) {
            nachweisbriefTemplates.add(dto.getMasterListValueId().getId());
          }
        }
      }

      boolean canDocumentBeUploaded;
      boolean canDocumentBeDeleted;
      boolean canDocumentPropertiesBeEdited;
      boolean canDocumentPropertiesBeEditedCheck;

      // if the document is created by Admin, only Admins can update its properties
      // if the document is not created by Admin, then only users of the same department
      // with the creator of the document can update its properties
      // so for other users besides Admin getting the permitted departments in order to
      // compare with the departments of the document
      String usersGroup = getGroupName(user);
      List<String> permittedDeps = null;
      List<MasterListValueHistoryDTO> permittedDeleteTemplates = null;
      // if the user is SB then his permitted departments can not be taken from the security
      // resources of the project, since he has no permission on the project part
      // so for SB getting his permitted departments from his user information
      if (usersGroup.equals(Group.SB.getValue())) {
        permittedDeps = getDepartments(user);
      } else if (!usersGroup.equals(Group.ADMIN.getValue())) {
        permittedDeps = security.getPermittedDepartments(user);
      } else {
        // case Admin
        // Documents can only be deleted by Admin and if a user can create them as well. So getting
        // all allowed templates.
        CompanyDTO companyDTO =
          companyService.getCompanyById(companyId != null ? companyId : folderId);
        permittedDeleteTemplates = ruleService.getCompanyAllowedTemplates(companyDTO);
      }

      for (FolderDTO folderDTO : folderDTOs) {
        for (NodeDTO nodeDTO : folderDTO.getChildren()) {
          if (permittedTenants
            .contains(nodeDTO.getAttributes().get(DocumentAttributes.TENANT_ID.name()))
            && Boolean
            .valueOf(nodeDTO.getAttributes().get(DocumentAttributes.SHOW_IN_COMPANY.name()))
            && nodeDTO.getAttributes().get(DocumentAttributes.COMPANY_ID.name()) != null
            && nodeDTO.getAttributes().get(DocumentAttributes.COMPANY_ID.name())
            .equals(folderId)) {

            String nodeTemplateId =
              nodeDTO.getAttributes().get(DocumentAttributes.TEMPLATE_ID.name());

            // if the document is private and set so by Admin, then only Admins can view it
            // if it is not set by Admin then only Admins and users of the departments of the user
            // that has set the document to private can view it
            boolean canView = true;
            String isPrivateString =
              nodeDTO.getAttributes().get(DocumentAttributes.PRIVATE_DOCUMENT.name());
            boolean isPrivate = isPrivateString != null
              && isPrivateString.equalsIgnoreCase(Boolean.TRUE.toString());
            String privateGroup = null;
            GroupDTO privateGroupDTO = null;
            if (isPrivate) {
              privateGroup = nodeDTO.getAttributes().get(DocumentAttributes.PRIVATE_GROUP.name());
              privateGroupDTO = userGroupService.getGroupByName(privateGroup, true);
              // if the current user is Admin then he can see all docs
              if (!usersGroup.equals(Group.ADMIN.getValue())) {
                // if the document is set to private by Admin
                // and the current user is not Admin (which is the case, since Admins do not enter
                // this if)
                // then he can not view it
                if (privateGroup == null || // this should never be the case
                  (privateGroup != null && privateGroup.equals(Group.ADMIN.getValue()))) {
                  canView = false;
                  // if the document is set to private by not Admin and the current user is not
                  // Admin
                  // then check if the current user belongs to the departments of the user that
                  // set the document to private. In this case he can view it, otherwise not
                } else {
                  canView = false;
                  String privateDepartments =
                    nodeDTO.getAttributes().get(DocumentAttributes.PRIVATE_DEPARTMENTS.name());
                  if (privateDepartments != null) {
                    List<String> departmentIds = Arrays.asList(
                      nodeDTO.getAttributes().get(DocumentAttributes.PRIVATE_DEPARTMENTS.name())
                        .split(TemplateConstants.DOC_SPLIT_STRING));
                    for (String id : departmentIds) {
                      if (permittedDeps.contains(id)) {
                        canView = true;
                        break;
                      }
                    }
                  }
                }
              }
            }
            if (canView) {
              /*
               * check if this document can be uploaded, so if the document type of the document is
               * in the permitted template types
               */
              if (nodeTemplateId == null || permittedUploadTemplates
                .contains(nodeDTO.getAttributes().get(DocumentAttributes.TEMPLATE_ID.name()))) {
                canDocumentBeUploaded = true;
              } else {
                canDocumentBeUploaded = false;
              }

              canDocumentBeDeleted = false;
              // Admin can delete all uploaded documents.
              if (nodeTemplateId == null && usersGroup.equals(Group.ADMIN.getValue())) {
                canDocumentBeDeleted = true;
              } else {
                if (permittedDeleteTemplates != null) {
                  for (MasterListValueHistoryDTO masterListValueHistoryDTO : permittedDeleteTemplates) {
                    if (masterListValueHistoryDTO.getMasterListValueId().getId()
                      .equals(nodeTemplateId)) {
                      canDocumentBeDeleted = true;
                      break;
                    }
                  }
                }
                // check if document can be deleted for NACHWEISBRIEF PT or NACHWEISBRIEF Sub
                for (String templateId : nachweisbriefTemplates) {
                  if (templateId.equals(nodeTemplateId)
                    && usersGroup.equals(Group.ADMIN.getValue())) {
                    canDocumentBeDeleted = findIfCanBeDeletedForNachweisbrief(
                      TenderStatus.fromValue(submissionStatusMap.get(nodeDTO.getParentId())));
                    break;
                  }
                }
              }
              List<VersionDTO> versionList = versionService.getFileVersions(nodeDTO.getId());
              for (VersionDTO v : versionList) {
                List<DepartmentHistoryDTO> departments = new ArrayList<>();
                DocumentDTO document = DocumentMapper.INSTANCE.toDocumentDTO(v);

                document.setPrivateDocument(isPrivate);
                if (privateGroup != null) {
                  document.setPrivateGroup(privateGroupDTO.getDescription());
                }

                // set actions
                // The group constraints will apply on the front end.
                document.setCanBeDownloaded(true);
                document.setCanBePrinted(true);
                // if the document is created by Admin, only Admins can update its properties
                // if the document is not created by Admin, then only users of the same
                // department
                // with the creator of the document can update its properties
                if (v.getAttributes().get(DocumentAttributes.CREATORS_GROUP.name())
                  .equals(Group.ADMIN.getValue()) && !usersGroup.equals(Group.ADMIN.getValue())) {
                  canDocumentPropertiesBeEdited = false;
                  canDocumentPropertiesBeEditedCheck = false;
                  // Enable edit of documents created by Direktion user when logged in user is SB.
                  // Bug :
                  // https://www.meistertask.com/app/task/qsP0Y6MP/firmenteil-hochladen-dokument-direktion-role-eigenschaften-not-correct
                  // (2nd fix).
                } else if (v.getAttributes().get(DocumentAttributes.CREATORS_GROUP.name())
                  .equals(Group.DIR.getValue()) && usersGroup.equals(Group.SB.getValue())) {
                  canDocumentPropertiesBeEdited = true;
                  canDocumentPropertiesBeEditedCheck = false;
                } else if (v.getAttributes().get(DocumentAttributes.CREATORS_GROUP.name())
                  .equals(Group.DIR.getValue()) && usersGroup.equals(Group.DIR.getValue())) {
                  canDocumentPropertiesBeEdited = true;
                  canDocumentPropertiesBeEditedCheck = false;
                } else {
                  if (usersGroup.equals(Group.ADMIN.getValue())) {
                    canDocumentPropertiesBeEdited = true;
                    canDocumentPropertiesBeEditedCheck = false;
                  } else {
                    canDocumentPropertiesBeEdited = false;
                    canDocumentPropertiesBeEditedCheck = true;
                  }
                }
                List<String> departmentIds = new ArrayList<>();
                if (v.getAttributes().get(DocumentAttributes.DEPARTMENT.name()) != null) {
                  departmentIds =
                    Arrays.asList(v.getAttributes().get(DocumentAttributes.DEPARTMENT.name())
                      .split(TemplateConstants.DOC_SPLIT_STRING));
                }
                if (v.getAttributes().get(DocumentAttributes.DEPARTMENT.name()) != null
                  && (!v.getAttributes().get(DocumentAttributes.CREATORS_GROUP.name())
                  .equals(Group.ADMIN.getValue()) || canDocumentPropertiesBeEditedCheck)) {
                  for (String departmentId : departmentIds) {
                    departments.add(departmentMapForProject.get(departmentId));
                    // check if the user belongs to the same department with the creator
                    // of the document, so that it can update its properties
                    if (canDocumentPropertiesBeEditedCheck && permittedDeps.contains(departmentId)
                      && ((v.getAttributes().get(DocumentAttributes.CREATORS_GROUP.name())
                      .equals(Group.PL.getValue())) || canDocumentPropertiesBeEditedCheck)) {
                      canDocumentPropertiesBeEdited = true;
                    }
                    // PL, Dir and SB user should be able to delete uploaded docs, that have been
                    // uploaded by them.
                    // Check user department if document has been uploaded by the same user role as
                    // the logged in user and is not Admin.
                    if (!usersGroup.equals(Group.ADMIN.getValue())
                      && (permittedDeps.contains(departmentId)
                      && v.getAttributes()
                      .get(DocumentAttributes.DOCUMENT_CREATION_TYPE.name())
                      .equals(DocumentCreationType.UPLOADED.name())
                      && !v.getAttributes().get(DocumentAttributes.CREATORS_GROUP.name())
                      .equals(Group.ADMIN.getValue())
                      && v.getAttributes().get(DocumentAttributes.CREATORS_GROUP.name())
                      .equals(usersGroup))) {
                      canDocumentBeDeleted = true;
                    }
                  }
                  document.setUserDepartments(departments);
                }
                document.setCanBeUploaded(canDocumentBeUploaded);
                document.setCanBeDeleted(canDocumentBeDeleted);

                // As soon as the document can no longer be deleted it means
                // that it shouldn't be replaced either
                if (!canDocumentBeDeleted
                  && !v.getAttributes().get(DocumentAttributes.DOCUMENT_CREATION_TYPE.name())
                  .equals(DocumentCreationType.UPLOADED.name())) {
                  document.setCanBeUploaded(false);
                }

                document.setCanPropertiesBeEdited(canDocumentPropertiesBeEdited);

                // In case a document cann't be deleted-uploaded it is also not
                // available for any further edit
                if (!document.getCanBeDeleted() && !document.getCanBeUploaded()
                  && !v.getAttributes().get(DocumentAttributes.DOCUMENT_CREATION_TYPE.name())
                  .equals(DocumentCreationType.UPLOADED.name())) {
                  document.setCanPropertiesBeEdited(false);
                }
                documentDTOList.add(document);
              }
            }
          }
        }
      }
    }
    return documentDTOList;
  }

  /**
   * Document NACHWEISBRIEF PT and NACHWEISBRIEF Sub can be deleted only by Admin if the status is
   * set to "Formelle Prüfung gestartet" / "Formelle & Eignungsprüfung gestartet" and the Status
   * "Formelle Prüfung abgeschlossen" / "Eignungsprüfung abgeschlossen & Zuschlagsbewertung
   * gestartet" has not been set yet or the status is set to "Formelle Prüfung & Eignungsprüfung
   * gestartet (S)" and the Status "Eignungsprüfung abgeschlossen (S)" has not been set yet (for
   * selective 1 stufe).
   *
   * @param submissionStatus the submission status
   * @return true, if successful
   */
  private boolean findIfCanBeDeletedForNachweisbrief(TenderStatus submissionStatus) {

    LOGGER.log(Level.CONFIG,
      "Executing method findIfCanBeDeletedForNachweisbrief, Parameters: "
        + "submissionStatus: {0}",
      submissionStatus);

    return ((compareCurrentVsSpecificStatus(submissionStatus,
      TenderStatus.FORMAL_EXAMINATION_STARTED)
      || compareCurrentVsSpecificStatus(submissionStatus,
      TenderStatus.FORMAL_EXAMINATION_AND_QUALIFICATION_STARTED))
      && !(compareCurrentVsSpecificStatus(submissionStatus, TenderStatus.FORMAL_AUDIT_COMPLETED))
      && !(compareCurrentVsSpecificStatus(submissionStatus,
      TenderStatus.SUITABILITY_AUDIT_COMPLETED_AND_AWARD_EVALUATION_STARTED)))
      || (compareCurrentVsSpecificStatus(submissionStatus,
      TenderStatus.FORMAL_EXAMINATION_SUITABILITY_AUDIT_STARTED)
      && !(compareCurrentVsSpecificStatus(submissionStatus,
      TenderStatus.SUITABILITY_AUDIT_COMPLETED_S)));
  }

  @Override
  public void autoCreateDocumentFromTemplate(String submissionId, String tenantId, String filename,
    String template, boolean createVersion) {
    DocumentDTO documentDTO = new DocumentDTO();
    documentDTO.setFolderId(submissionId);
    documentDTO.setFilename(filename);
    documentDTO.setTitle(filename);
    documentDTO.setTemplateId(
      new JPAQueryFactory(em).selectFrom(qMasterListValueHistoryEntity)
        .where(qMasterListValueHistoryEntity.shortCode.eq(template)
          .and(qMasterListValueHistoryEntity.tenant.id.eq(tenantId)))
        .fetchOne().getMasterListValueId().getId());
    documentDTO.setTenantId(tenantId);
    documentDTO.setGenerateProofTemplate(Boolean.FALSE);
    documentDTO.setIsCompanyCertificate(Boolean.FALSE);
    documentDTO.setCreateVersion(createVersion);
    createDocumentFromTemplate(documentDTO);
  }

  @Override
  public List<String> createDocumentFromTemplate(DocumentDTO documentDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method createDocumentFromTemplate, Parameters: documentDTO: {0}",
      documentDTO);

    List<String> createdDocumentIds = new ArrayList<>();
    MasterListValueHistoryEntity template = null;
    InputStream inputStream = null;
    List<NodeDTO> templateList = new ArrayList<>();

    try {

      if (!documentDTO.getGenerateProofTemplate() && !documentDTO.getIsCompanyCertificate()) {
        // Retrieve template
        templateList = retrieveTemplate(documentDTO);

        // Retrieve template short name
        if (documentDTO.getTemplateId() != null) {
          template = new JPAQueryFactory(em).selectFrom(qMasterListValueHistoryEntity).where(
            qMasterListValueHistoryEntity.masterListValueId.id.eq(documentDTO.getTemplateId()))
            .fetchOne();
          // Retrieve template input from DB
          inputStream = getTemplateInputStream(documentDTO, templateList, inputStream);
          // In the field VALUE3 of the table MasterListValueHistory is stored whether
          // the document can be created only by Admin or not
          if (template.getValue3() != null) {
            documentDTO.setIsAdminRightsOnly(
              template.getValue3().equals(LookupValues.ZERO_STRING) ? Boolean.FALSE
                : Boolean.TRUE);
          }
        }
      }

      if (documentDTO.getGenerateProofTemplate()) {
        // Always create version when generate Nachweisbrief documents.
        documentDTO.setCreateVersion(true);
        createdDocumentIds.addAll(generateTendererProofDocuments(documentDTO));
      }

      // document is from project part
      if (template != null && (template.getValue2() != null && template.getValue2().equals("0"))) {
        createdDocumentIds
          .addAll(projectPartDocuments(documentDTO, template, inputStream, templateList));
        // document is from company part
      } else if (template != null
        && (template.getValue2() != null && template.getValue2().equals("1"))) {
        createdDocumentIds.addAll(createProofDocument(documentDTO, template, inputStream));
      } else if (template != null && template.getValue2() == null) {
        createdDocumentIds
          .addAll(createContractDocument(documentDTO, template, inputStream, templateList));
      }

      return createdDocumentIds;

    } finally {
      try {
        if (inputStream != null) {
          inputStream.close();
        }
      } catch (IOException e) {
        LOGGER.log(Level.SEVERE, TemplateConstants.INPUTSTREAM_ERROR + e);
      }
    }

  }

  /**
   * Creates the contract document.
   *
   * @param documentDTO the document DTO
   * @param template the template
   * @param inputStream the input stream
   * @param templateList the template list
   * @return the list
   */
  private List<String> createContractDocument(DocumentDTO documentDTO,
    MasterListValueHistoryEntity template, InputStream inputStream, List<NodeDTO> templateList) {

    LOGGER.log(Level.CONFIG,
      "Executing method createContractDocument, Parameters: documentDTO: {0}, "
        + "template: {1}, inputStream: {2}, templateList: {3}",
      new Object[]{documentDTO, template, inputStream, templateList});

    List<String> createdDocumentIds = new ArrayList<>();
    HashMap<String, String> placeholders = new HashMap<>();
    byte[] logo = getLogo(documentDTO.getFolderId(), placeholders);
    // submission
    SubmissionDTO submission = submissionService.getSubmissionById(documentDTO.getFolderId());
    List<SubmittentOfferDTO> offers =
      submissionService.getCompaniesBySubmission(documentDTO.getFolderId());

    templateBean.replaceSubmissionPlaceholders(placeholders, submission, offers);
    switch (template.getShortCode()) {
      case Template.VERTRAG_AUFTRAGSBESTATIGUNG_FREIHANDIG_ISB:
      case Template.VERTRAG_DIENSTLEISTUNGSVERTRAG_LB:
      case Template.VERTRAG_PLANERVERTRAG_SGB:
      case Template.VERTRAG_AUFTRAGSBESTATIGUNG_FREIHANDIG_SGB:
      case Template.VERTRAG_BESTELLUNG_PLANERLEISTUNGEN_SGB:
      case Template.VERTRAG_WERKVERTRAG_SGB:
      case Template.VERTRAG_KAUFVERTRAG_SGB:
      case Template.VERTRAG_LIEFER_ANBAUVERTRAG:
      case Template.VERTRAG_KAUF_LIEFERVERTRAG_LB:
      case Template.VERTRAG_WERKVERTRAG_HSB:
      case Template.VERTRAG_WERKVERTRAG_ISB:


        documentDTO.setFilename(documentDTO.getFilename()
          + setFileExtension(Template.VERTRAG_AUFTRAGSBESTATIGUNG_FREIHANDIG_ISB));

        for (SubmittentOfferDTO offer : offers) {
          /* Only for the Awarded tenderer can be created a contract Document */
          if (offer.getOffer() != null && offer.getOffer().getIsAwarded() != null
            && offer.getOffer().getIsAwarded()) {

            /* Replace placeholders of document though RU007 */
            ruleService.runProjectTemplateRules(submission, offer.getSubmittent().getCompanyId(),
              null, offer.getOffer(), placeholders, Rule.RU007.name());

            templateBean.removeCurrencySymbol(placeholders);

            templateBean.replaceJointVenturesPlaceholder(offer, placeholders);
            placeholders.put(DocumentPlaceholders.O_SETTLEMENT.getValue(),
              offer.getOffer().getSettlement().getValue1());

            templateBean.setCompanyNameOrArge(offer.getOffer(), placeholders,
              DocumentPlaceholders.F_COMPANY_NAME_OR_ARGE.getValue());

            if (offer.getOffer().getIsDiscountPercentage().equals(false)) {
              placeholders.put(DocumentPlaceholders.O_DISCOUNT_PERCENT.getValue(),
                TemplateConstants.EMPTY_STRING);
            }
            if (offer.getOffer().getIsDiscount2Percentage().equals(false)) {
              placeholders.put(DocumentPlaceholders.O_DISCOUNT2_PERCENT.getValue(),
                TemplateConstants.EMPTY_STRING);
            }
            if (offer.getOffer().getIsVatPercentage().equals(false)) {
              placeholders.put(DocumentPlaceholders.O_VAT_AMOUNT_PERCENT.getValue(),
                TemplateConstants.EMPTY_STRING);
            }
            if(submission.getPublicationDate() != null){
              placeholders.put(DocumentPlaceholders.S_PUBLICATION_DATE.getValue(),
                SubmissConverter.convertToSwissDate(submission.getPublicationDate()));
            }else{
              placeholders.put(DocumentPlaceholders.S_PUBLICATION_DATE.getValue(),
                TemplateConstants.EMPTY_STRING);
            }
            placeholders.put(DocumentPlaceholders.S_CURRENT_DATE.getValue(),
              SubmissConverter.convertToSwissDate(new Date()));

            placeholders.put(DocumentPlaceholders.F_COMPANY_TEL.getValue(),
              offer.getOffer().getSubmittent().getCompanyId().getCompanyTel());
            placeholders.put(DocumentPlaceholders.F_COMPANY_MAIL.getValue(),
              offer.getOffer().getSubmittent().getCompanyId().getCompanyEmail());

            if(submission.getProject().getDepartment().getTelephone() != null){
              placeholders.put(DocumentPlaceholders.R_DEPARTMENT_TEL.getValue(),
                submission.getProject().getDepartment().getTelephone());
            }else{
              placeholders.put(DocumentPlaceholders.R_DEPARTMENT_TEL.getValue(),
                TemplateConstants.EMPTY_STRING);
            }
            if(submission.getProject().getDepartment().getEmail() != null){
              placeholders.put(DocumentPlaceholders.R_DEPARTMENT_EMAIL.getValue(),
                submission.getProject().getDepartment().getEmail());
            }else{
              placeholders.put(DocumentPlaceholders.R_DEPARTMENT_EMAIL.getValue(),
                TemplateConstants.EMPTY_STRING);
            }

            try (InputStream inputStream1 = new ByteArrayInputStream(
              versionService.getBinContent(templateList.get(0).getId()))) {
              createdDocumentIds
                .add(
                  createDocument(documentDTO,
                    templateService
                      .replacePlaceholdersWordDoc(inputStream1,
                        SubmissConverter.replaceSpecialCharactersInPlaceholders(
                          placeholders),
                        logo, TemplateConstants.SB_LOGO_WIDTH)
                      .toByteArray(),
                    offer.getSubmittent().getId(),
                    offer.getSubmittent().getCompanyId().getId(), false));
              updateSubmissionStatus(submission);
            } catch (IOException e) {
              LOGGER.log(Level.SEVERE, TemplateConstants.INPUTSTREAM_ERROR + e.getMessage());
            }
          }
        }
        break;
      default:
        break;
    }

    return createdDocumentIds;
  }

  @Override
  public Set<ValidationError> contractDocumentValidation(String submissionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method contractDocumentValidation, Parameters: submissionId: {0}",
      submissionId);

    Set<ValidationError> validationErrors = new HashSet<>();
    List<SubmittentOfferDTO> offers =
      submissionService.getCompaniesBySubmission(submissionId);
    // check if there is no awarded submittent(s) to throw a validation error
    if (templateBean.getAwardedNumber(offers) == 0) {
      validationErrors.add(new ValidationError(ValidationMessages.CONTRACT_DOCUMENT_ERROR_FIELD,
        ValidationMessages.CONTRACT_DOCUMENT_ERROR));
    }

    return validationErrors;
  }

  /**
   * Update submission status.
   *
   * @param submission the submission
   */
  private void updateSubmissionStatus(SubmissionDTO submission) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateSubmissionStatus, Parameters: submission: {0}",
      submission);

    if (!compareCurrentVsSpecificStatus(TenderStatus.fromValue(submission.getStatus()),
      TenderStatus.CONTRACT_CREATED)) {
      submissionService.updateSubmissionStatus(submission.getId(),
        TenderStatus.CONTRACT_CREATED.getValue(), null, null, LookupValues.INTERNAL_LOG);
    }
  }

  /**
   * Creates the proof document.
   *
   * @param documentDTO the document DTO
   * @param template the template
   * @param inputStream the input stream
   * @return the list
   */
  private List<String> createProofDocument(DocumentDTO documentDTO,
    MasterListValueHistoryEntity template, InputStream inputStream) {

    LOGGER.log(Level.CONFIG,
      "Executing method createProofDocument, Parameters: documentDTO: {0}, "
        + "template:{1} , inputStream: {2}",
      new Object[]{documentDTO, template, inputStream});

    List<String> createdDocumentIds = new ArrayList<>();
    HashMap<String, String> placeholders = new HashMap<>();
    CompanyDTO company = companyService.getCompanyById(documentDTO.getFolderId());

    /*
     * Retrieve company templates depending on the following rule.
     */
    List<MasterListValueHistoryDTO> allowedTemplates =
      ruleService.getCompanyAllowedTemplates(company);
    documentDTO
      .setFilename(documentDTO.getFilename() + setFileExtension(Template.NACHWEISBRIEF_FT));

    initReferenceData(template.getShortCode(), placeholders, documentDTO);

    TenantDTO tenant = sDTenantService
      .getTenantById(getUser().getAttributeData(USER_ATTRIBUTES.TENANT.getValue()));

    /** Get logo */
    byte[] logo = getLogo(documentDTO.getFolderId(), placeholders);
    /** Get logo width per tenant */
    long logoWidth = getLogoWidthForTenant(tenant);

    // Apply security check through allowed templates retrieved from rule.
    for (MasterListValueHistoryDTO allowedTemplate : allowedTemplates) {
      if (allowedTemplate.getShortCode().equals(Template.NACHWEISBRIEF_FT)) {
        List<CompanyProofDTO> companyProofDTOs =
          companyService.getProofByCompanyId(company.getId());
        Date date = new Date();
        addProofPlaceholder(companyProofDTOs, date, placeholders);

        ruleService.runProjectTemplateRules(null, company, documentDTO, null, placeholders,
          Template.NACHWEISBRIEF_FT);

        List<String> paragraphList = new ArrayList<>();
        paragraphList.add(DocumentPlaceholders.F_GERMAN_PROOFS.getValue());
        createdDocumentIds.add(createDocument(documentDTO,
          templateService.replacePlaceholdersWordDoc(inputStream,
            SubmissConverter.replaceSpecialCharactersInPlaceholders(placeholders), logo,
            logoWidth, paragraphList, setParagraphPosition(Template.NACHWEISBRIEF_FT))
            .toByteArray(),
          null, company.getId(), true));

        // Update company entity with the new document values.
        updateCompanyDocumentValues(documentDTO.getFolderId(), documentDTO,
          Template.NACHWEISBRIEF_FT);

        // Create log in company after Nachweisbrief document is generated.
        CompanyEntity companyEntity = em.find(CompanyEntity.class, company.getId());
        StringBuilder additionalInfo = new StringBuilder(company.getCompanyName()).append("[#]")
          .append(companyEntity.getProofStatusFabe()).append("[#]");

        audit.createLogAudit(AuditLevel.COMPANY_LEVEL.name(), AuditEvent.CREATE.name(),
          AuditGroupName.COMPANY.name(), AuditMessages.GENERATE_PROOF_DOCUMENT.name(),
          getUser().getId(), company.getId(), null, additionalInfo.toString(),
          LookupValues.EXTERNAL_LOG);

        settleProofRequestTask(null, company.getId(), documentDTO.getSubmitDate());

      }
    }

    return createdDocumentIds;
  }

  /**
   * This method generates documents derived from project part.
   *
   * @param documentDTO the document DTO
   * @param template the template
   * @param inputStream the input stream
   * @param templateList the template list
   * @return the list
   */
  private List<String> projectPartDocuments(DocumentDTO documentDTO,
    MasterListValueHistoryEntity template, InputStream inputStream, List<NodeDTO> templateList) {

    LOGGER.log(Level.CONFIG,
      "Executing method projectPartDocuments, Parameters: documentDTO: {0}, "
        + "template: {1}, inputStream: {2}, templateList: {3}",
      new Object[]{documentDTO, template, inputStream, templateList});

    List<String> createdDocumentIds = new ArrayList<>();
    HashMap<String, String> attributesMap = new HashMap<>();
    HashMap<String, String> placeholders = new HashMap<>();
    // submission
    SubmissionDTO submission = submissionService.getSubmissionById(documentDTO.getFolderId());

    /* Get from User the Tenant */
    TenantDTO tenant = sDTenantService
      .getTenantById(getUser().getAttributeData(USER_ATTRIBUTES.TENANT.getValue()));

    /* Get logo */
    byte[] logo = getLogo(documentDTO.getFolderId(), placeholders);
    /* Get logo width per tenant */
    long logoWidth = getLogoWidthForTenant(tenant);


    String tableIndentLeft = (tenant.getIsMain())
      ? "365" // indent left : 0.25"
      : "432"; // indent left : 0.3"

    // Ausschluss / Absage and Zuschlag docs should be generated with "vertraulich" checked with
    // only one exception: if it is a Freihändig or Freihändig mit Konkurrenz Verfahren under the
    // threshold.
    if ((template.getShortCode().equals(Template.AUSSCHLUSS)
      || template.getShortCode().equals(Template.VERFUGUNGEN))
      && ((submission.getProcess().equals(Process.NEGOTIATED_PROCEDURE)
      || submission.getProcess().equals(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION))
      && (submission.getAboveThreshold() == null || !submission.getAboveThreshold()))) {
      documentDTO.setIsAdminRightsOnly(Boolean.FALSE);
    }

    /*
     * Retrieve the allowed templates. A document can only be generated if has been retrieved from
     * the following rule. Check the user right to in order to generate the document.
     */
    submission.setCurrentState(TenderStatus
      .fromValue(submissionService.getCurrentStatusOfSubmission(documentDTO.getFolderId())));
    List<MasterListValueHistoryDTO> allowedTemplates =
      ruleService.getProjectAllowedTemplates(submission);

    // Replace Reference placeholders.
    initReferenceData(template.getShortCode(), placeholders, documentDTO);

    // list of submittents
    List<SubmittentOfferDTO> offers =
      submissionService.getCompaniesBySubmission(documentDTO.getFolderId());
    templateBean.replaceSubmissionPlaceholders(placeholders, submission, offers);

    switch (template.getShortCode()) {
      case Template.ANGEBOTSDECKBLATT:
        // Apply security check through allowed templates retrieved from rule.
        for (MasterListValueHistoryDTO allowedTemplate : allowedTemplates) {
          if (allowedTemplate.getShortCode().equals(Template.ANGEBOTSDECKBLATT)) {
            // Replace joint venture placeholder.
            for (SubmittentOfferDTO offer : offers) {
              documentDTO.setFilename(Template.TEMPLATE_NAMES.ANGEBOTSDECKBLATT.getValue()
                + LookupValues.UNDER_SCORE + offer.getSubmittent().getCompanyId().getCompanyName()
                + setFileExtension(Template.ANGEBOTSDECKBLATT));
              templateBean.replaceJointVenturesPlaceholder(offer, placeholders);
              try (InputStream inputStream1 = new ByteArrayInputStream(
                versionService.getBinContent(templateList.get(0).getId()))) {
                ruleService.runProjectTemplateRules(submission,
                  offer.getSubmittent().getCompanyId(), null, null, placeholders,
                  Template.ANGEBOTSDECKBLATT);
                createdDocumentIds
                  .add(
                    createDocument(documentDTO,
                      templateService.replacePlaceholdersWordDoc(inputStream1,
                        SubmissConverter.replaceSpecialCharactersInPlaceholders(
                          placeholders),
                        logo, logoWidth).toByteArray(),
                      offer.getSubmittent().getId(),
                      offer.getSubmittent().getCompanyId().getId(), false));
              } catch (IOException e) {
                LOGGER.log(Level.SEVERE, TemplateConstants.INPUTSTREAM_ERROR + e.getMessage());
              }
            }
            break;
          }
        }
        break;
      case Template.BEKO_ANTRAG:
      case Template.BEKO_BESCHLUSS:
        generateBekoDocument(documentDTO, inputStream, createdDocumentIds, placeholders, submission,
          logo, logoWidth, allowedTemplates, offers, template.getShortCode());
        break;
      case Template.VERFAHRENSABBRUCH:

        /*
         * Get the creation date of Rechtliches Gehör (Abbruch)
         * to map with the placeholder r_date
         */
        retrieveRechtlichesDate(submission);

        if (submission.getProcess().equals(Process.SELECTIVE)) {
          attributesMap.put(DocumentAttributes.ADDITIONAL_INFO.name(), Process.OPEN.name());
        } else {
          attributesMap.put(DocumentAttributes.ADDITIONAL_INFO.name(),
            submission.getProcess().name());
        }
        attributesMap.put(DocumentAttributes.TEMPLATE_ID.name(), documentDTO.getTemplateId());
        attributesMap.put(DocumentAttributes.TENANT_ID.name(), documentDTO.getTenantId());
        // Retrieve template input from DB
        templateList =
          documentService.getNodeByAttributes(LookupValues.TEMPLATE_FOLDER_ID, attributesMap);

        templateBean.replaceSubmissionCancelPlaceholders(submission, placeholders);
        getOfferListForCancel(submission, offers);

        for (SubmittentOfferDTO offer : offers) {
          documentDTO.setFilename(TEMPLATE_NAMES.VERFAHRENSABBRUCH.getValue()
            + LookupValues.UNDER_SCORE + offer.getSubmittent().getCompanyId().getCompanyName()
            + setFileExtension(Template.VERFAHRENSABBRUCH));
          try (InputStream inputStream1 =
            new ByteArrayInputStream(versionService.getBinContent(templateList.get(0).getId()))) {
            if (submission.getSubmissionCancel().isEmpty()) {
              ruleService.runProjectTemplateRules(submission, offer.getSubmittent().getCompanyId(),
                null, null, placeholders, Template.VERFAHRENSABBRUCH_EMPTY);
            } else {
              ruleService.runProjectTemplateRules(submission, offer.getSubmittent().getCompanyId(),
                null, null, placeholders, Template.VERFAHRENSABBRUCH);
            }

            // Replace f_company_name placeholder according to Bug 3887.
            templateBean.setCompanyNameOrArge(offer.getOffer(), placeholders,
              DocumentPlaceholders.F_COMPANY_NAME_OR_ARGE.getValue());

            createdDocumentIds.add(createDocument(documentDTO,
              templateService.replacePlaceholdersWordDoc(inputStream1,
                SubmissConverter.replaceSpecialCharactersInPlaceholders(placeholders),
                logo, logoWidth).toByteArray(),
              offer.getSubmittent().getId(), offer.getSubmittent().getCompanyId().getId(),
              false));
          } catch (IOException e) {
            LOGGER.log(Level.SEVERE, TemplateConstants.INPUTSTREAM_ERROR + e.getMessage());
          }
        }
        StringBuilder additionalInfoAbbruch =
          new StringBuilder(submission.getProject().getProjectName()).append("[#]")
            .append(submission.getProject().getObjectName().getValue1()).append("[#]")
            .append(submission.getWorkType().getValue1() + submission.getWorkType().getValue2())
            .append("[#]")
            .append(getUser().getAttributeData(LookupValues.USER_ATTRIBUTES.TENANT.getValue()))
            .append("[#]");

        auditLog(AuditLevel.PROJECT_LEVEL.name(), AuditEvent.CREATE.name(),
          AuditMessages.VERFAHRENSABBRUCH_DOC_GENERATED.name(), submission.getId(),
          additionalInfoAbbruch.toString());

        break;
      case Template.VERFUGUNGEN:
        // initialize awardInfo if not saved from user
        initializeAwardInfo(submission);
        attributesMap.put(DocumentAttributes.ADDITIONAL_INFO.name(),
          submission.getProcess().name());
        attributesMap.put(DocumentAttributes.TEMPLATE_ID.name(), documentDTO.getTemplateId());
        attributesMap.put(DocumentAttributes.TENANT_ID.name(), documentDTO.getTenantId());

        if ((submission.getProcess().equals(Process.NEGOTIATED_PROCEDURE)
          || submission.getProcess().equals(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION))
          && (submission.getAboveThreshold() != null && submission.getAboveThreshold())) {
          attributesMap.put(LookupValues.ABOVE_THRESHOLD, "YES");
        }

        List<LinkedHashMap<Map<String, String>, Map<String, String>>> awardCriteriaList =
          templateBean.replaceAwardedSubmittentPlaceholders(attributesMap, offers, placeholders,
            documentDTO, submission);

        templateBean.replaceAwardInfoPlaceholders(documentDTO.getFolderId(), submission,
          placeholders);

        List<OfferDTO> offersWithCriteria =
          offerService.getSubmissionOffersWithCriteria(documentDTO.getFolderId());
        placeholders.put("sum_of_offers", String.valueOf(offersWithCriteria.size()));
        for (OfferDTO offer : offersWithCriteria) {
          if (offer.getIsEmptyOffer() == null || !offer.getIsEmptyOffer()) {
            Collections.sort(offer.getOfferCriteria(), ComparatorUtil.offerCriteriaWithWeightings);
            templateBean.setCompanyNameOrArge(offer, placeholders,
              DocumentPlaceholders.F_COMPANY_NAME_OR_ARGE.getValue());

            templateBean.replaceExcludedPlaceholders(placeholders, offer.getExclusionReasons());

            List<LinkedHashMap<Map<String, String>, Map<String, String>>> offerCriteria = new ArrayList<>();

            if (offer.getIsExcludedFromProcess() == null || !offer.getIsExcludedFromProcess()) {
              offerCriteria =
                templateBean.replaceOfferCriteriaPlaceholders(placeholders, offer);
            }

            ruleService.runProjectTemplateRules(submission, offer.getSubmittent().getCompanyId(),
              null, offer, placeholders, Rule.RU007.name());
            ruleService.runProjectTemplateRules(submission, offer.getSubmittent().getCompanyId(),
              null, offer, placeholders, Rule.RU005.name());

            if ((offer.getIsExcludedFromProcess() != null && offer.getIsExcludedFromProcess())
              && !submission.getProcess().equals(Process.NEGOTIATED_PROCEDURE) && !submission
              .getProcess().equals(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION)) {
              templateBean.setCancelDeadline(placeholders, offer,
                SelectiveLevel.SECOND_LEVEL.getValue());

              List<Integer> levels = new ArrayList<>();
              levels.add(SelectiveLevel.SECOND_LEVEL.getValue());
              levels.add(SelectiveLevel.ZERO_LEVEL.getValue());
              if (offer.getExclusionReasons() == null || offer.getExclusionReasons().isEmpty()) {
                templateBean.replaceLegalHearingPlaceholders(placeholders, offer, levels);
              }

              documentDTO.setFilename(Template.TEMPLATE_NAMES.AUSSCHLUSS.getValue()
                + LookupValues.UNDER_SCORE + offer.getSubmittent().getCompanyId().getCompanyName()
                + setFileExtension(Template.AUSSCHLUSS));
              documentDTO.setTitle(TemplateConstants.AUSSCHLUSS);
              // Set attributes of cm_node_attribute in order to retrieve Ausschluss document.
              // Set WITH_VORBEHALT attribute to NO. Ausschluss template is the same for project with or without Vorbehalt.
              attributesMap.put(LookupValues.TYPE, TemplateConstants.EXCLUSION);
              attributesMap.put(LookupValues.WITH_VORBEHALT, "NO");
            } else if (offer.getIsExcludedFromProcess() == null || !offer.getIsExcludedFromProcess()
              && (offer.getIsAwarded() != null && offer.getIsAwarded())) {
              templateBean.setAwardedValues(placeholders, offer);
              documentDTO.setFilename(TemplateConstants.ZUSCHLAG + LookupValues.UNDER_SCORE
                + offer.getSubmittent().getCompanyId().getCompanyName()
                + setFileExtension(Template.VERFUGUNGEN));
              documentDTO.setTitle(TemplateConstants.ZUSCHLAG);
              if (submission.getAboveThreshold() == null || !submission.getAboveThreshold()) {
                attributesMap.put(LookupValues.TYPE, TemplateConstants.AWARD);
                if (!submission.getProcess().equals(Process.NEGOTIATED_PROCEDURE)
                  && !submission.getProcess()
                  .equals(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION)
                  && (StringUtils
                  .isNotBlank(submission.getCommissionProcurementProposalReservation())
                  && !submission.getCommissionProcurementProposalReservation()
                  .equalsIgnoreCase(
                    CommissionProcurementProposalReservation.RESERVATION_NONE
                      .getValue()))) {
                  attributesMap.put(LookupValues.WITH_VORBEHALT, "YES");
                  placeholders.put(DocumentPlaceholders.BA_RESERVATION.getValue(),
                    submission.getCommissionProcurementProposalReservation());
                } else {
                  attributesMap.put(LookupValues.WITH_VORBEHALT, "NO");
                }
              }
            } else if ((!submission.getProcess().equals(Process.NEGOTIATED_PROCEDURE)
              && !submission.getProcess().equals(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION)
              && (offer.getIsExcludedFromProcess() == null
              || !offer.getIsExcludedFromProcess()
              && (offer.getIsAwarded() == null || !offer.getIsAwarded())))
              || ((submission.getProcess().equals(Process.NEGOTIATED_PROCEDURE)
              || submission.getProcess().equals(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION))
              && (offer.getIsAwarded() == null
              || !offer.getIsAwarded()))) {
              documentDTO.setFilename(TemplateConstants.ABSAGE + LookupValues.UNDER_SCORE
                + offer.getSubmittent().getCompanyId().getCompanyName()
                + setFileExtension(Template.VERFUGUNGEN));
              documentDTO.setTitle(TemplateConstants.ABSAGE);
              if (submission.getAboveThreshold() == null || !submission.getAboveThreshold()) {
                attributesMap.put(LookupValues.TYPE, TemplateConstants.REJECTION);
              }
              if (submission.getAboveThreshold() == null || !submission.getAboveThreshold()) {
                attributesMap.put(LookupValues.WITH_VORBEHALT, "NO");
              }
            }

            templateList = documentService.getNodeByAttributes(LookupValues.TEMPLATE_FOLDER_ID,
              attributesMap);
            try (InputStream inputStream1 = new ByteArrayInputStream(
              versionService.getBinContent(templateList.get(0).getId()))) {
              createdDocumentIds.add(createDocument(documentDTO,
                templateService.replacePlaceholdersWordDoc(
                  new ByteArrayInputStream(templateService
                    .replacePlaceholderWithTable(
                      new ByteArrayInputStream(templateService
                        .replacePlaceholderWithTable(inputStream1, offerCriteria,
                          "offer_criterion_name", tableIndentLeft)
                        .toByteArray()),
                      awardCriteriaList, "award_criterion_name", tableIndentLeft)
                    .toByteArray()),
                  SubmissConverter.replaceSpecialCharactersInPlaceholders(placeholders),
                  logo, logoWidth).toByteArray(),
                offer.getSubmittent().getId(), offer.getSubmittent().getCompanyId().getId(),
                false));
            } catch (IOException e) {
              LOGGER.log(Level.SEVERE, TemplateConstants.INPUTSTREAM_ERROR + e.getMessage());
            }
          }
        }
        updateSubmissionAwardNoticesStatus(submission);
        break;
      case Template.VERFUGUNGEN_DL_WETTBEWERB:
        // initialize awardInfo if not saved from user
        initializeAwardInfo(submission);
        attributesMap.put(DocumentAttributes.ADDITIONAL_INFO.name(),
          submission.getProcess().name());
        attributesMap.put(DocumentAttributes.TEMPLATE_ID.name(), documentDTO.getTemplateId());
        attributesMap.put(DocumentAttributes.TENANT_ID.name(), documentDTO.getTenantId());
        templateBean.replaceAwardedSubmittentPlaceholders(attributesMap, offers, placeholders,
          documentDTO, submission);
        templateBean.replaceAwardInfoPlaceholders(documentDTO.getFolderId(), submission,
          placeholders);

        for (SubmittentOfferDTO offer : offers) {
          if (offer.getOffer().getIsEmptyOffer() == null || !offer.getOffer().getIsEmptyOffer()) {
            attributesMap.put(DocumentAttributes.ADDITIONAL_INFO.name(),
              submission.getProcess().name());
            attributesMap.put(DocumentAttributes.TEMPLATE_ID.name(), documentDTO.getTemplateId());
            attributesMap.put(DocumentAttributes.TENANT_ID.name(), documentDTO.getTenantId());

            templateBean.setCompanyNameOrArge(offer.getOffer(), placeholders,
              DocumentPlaceholders.F_COMPANY_NAME_OR_ARGE.getValue());

            ruleService.runProjectTemplateRules(submission, offer.getSubmittent().getCompanyId(),
              null, offer.getOffer(), placeholders, Rule.RU007.name());

            if (offer.getOffer().getIsAwarded() != null && offer.getOffer().getIsAwarded()) {
              // Zuschlag
              setAwardProperties(documentDTO, placeholders, attributesMap, offer, submission);

            } else if (offer.getOffer().getIsExcludedFromProcess() != null
              && offer.getOffer().getIsExcludedFromProcess()) {

              templateBean.replaceExcludedPlaceholders(placeholders,
                offer.getOffer().getExclusionReasons());
              List<Integer> levels = new ArrayList<>();
              levels.add(SelectiveLevel.SECOND_LEVEL.getValue());
              levels.add(SelectiveLevel.ZERO_LEVEL.getValue());
              if (offer.getOffer().getExclusionReasons() == null
                || offer.getOffer().getExclusionReasons().isEmpty()) {
                templateBean.replaceLegalHearingPlaceholders(placeholders, offer.getOffer(),
                  levels);
              }
              // Ausschluss
              setRejectedExcludedProperties(documentDTO, offer, attributesMap,
                TemplateConstants.EXCLUSION, TemplateConstants.AUSSCHLUSS);
            } else {
              // Absage
              setRejectedExcludedProperties(documentDTO, offer, attributesMap,
                TemplateConstants.REJECTION, TemplateConstants.ABSAGE);
            }

            templateList =
              documentService.getNodeByAttributes(LookupValues.TEMPLATE_FOLDER_ID, attributesMap);
            try (InputStream inputStream1 = new ByteArrayInputStream(
              versionService.getBinContent(templateList.get(0).getId()))) {
              createdDocumentIds.add(createDocument(documentDTO,
                templateService.replacePlaceholdersWordDoc(inputStream1,
                  SubmissConverter.replaceSpecialCharactersInPlaceholders(placeholders),
                  logo, logoWidth).toByteArray(),
                offer.getSubmittent().getId(), offer.getSubmittent().getCompanyId().getId(),
                false));
            } catch (IOException e) {
              LOGGER.log(Level.SEVERE, TemplateConstants.INPUTSTREAM_ERROR + e.getMessage());
            }
            if (!compareCurrentVsSpecificStatus(
              TenderStatus.fromValue(
                submissionService.getCurrentStatusOfSubmission(documentDTO.getFolderId())),
              TenderStatus.AWARD_NOTICES_CREATED)) {
              updateSubmissionAwardNoticesStatus(submission);
            }
          }
        }
        break;
      case Template.SELEKTIV_1_STUFE:
        generateAwardInfoFirstLevelDocuments(documentDTO, createdDocumentIds, attributesMap,
          placeholders,
          submission, logo, logoWidth, tableIndentLeft);
        break;
      case Template.SUBMITTENTENLISTE:
        DocumentPropertiesDTO docProperties = new DocumentPropertiesDTO();
        docProperties.setBoldHeader(Boolean.TRUE.toString());
        docProperties.setFonts(DocumentProperties.ARIAL.getValue());
        docProperties.setFontSize(DocumentProperties.NUMBER_20.getValue());
        docProperties.setRemoveBorder(Boolean.FALSE.toString());
        docProperties.setTablePosition(DocumentProperties.NUMBER_7.getValue());
        docProperties.setBottomMargin(DocumentProperties.NUMBER_260.getValue());
        docProperties.setTopMargin(DocumentProperties.NUMBER_60.getValue());
        docProperties.setBoldContent(Boolean.FALSE.toString());
        Map<String, String> tableProperties = templateBean.fillTableProperties(null, docProperties);
        documentDTO
          .setFilename(documentDTO.getFilename() + setFileExtension(Template.SUBMITTENTENLISTE));
        /*
         * Create 2 variables header and content that are coming filled from function
         * createSubmittentenListeTable
         */
        LinkedList<String> header = new LinkedList<>();
        List<LinkedHashMap<Map<String, String>, String>> content = new ArrayList<>();

        // Clear Submittent list when Selektiv process and status is not greater than
        // OFFER_OPENING_STARTED.
        if (submission.getProcess().equals(Process.SELECTIVE)
          && !compareCurrentVsSpecificStatus(submission.getCurrentState(),
          TenderStatus.SUITABILITY_AUDIT_COMPLETED_S)) {
          offers.clear();
          offers.addAll(submissionService.getApplicantsBySubmission(submission.getId()));
        }

        for (SubmittentOfferDTO offer : offers) {
          // For every offer submittent, calculate the submittent proof status FaBe.
          offer.getSubmittent().getCompanyId()
            .setProofStatusFabe(handleSubmittentProofStatusFabe(offer.getSubmittent()));
        }

        List<Map<byte[], String>> iconsToReplaced =
          templateBean.createSubmittentenListeTable(header, content, offers);

        // Repeat table header on every page.
        tableProperties.put(REPEAT_HEADER, Boolean.TRUE.toString());

        /* replace placeholders , add table create docx file. */
        createdDocumentIds.add(createDocument(documentDTO,
          submissPrintService.convertToPDF(templateService
            .replacePlaceholdersWordDoc(
              new ByteArrayInputStream(templateService.createTableInDocxWithCustomBorders(
                inputStream, header, null, content, tableProperties, iconsToReplaced)
                .toByteArray()),
              SubmissConverter.replaceSpecialCharactersInPlaceholders(placeholders))
            .toByteArray()),
          null, null, false));

        break;
      case Template.A_OFFERRTOFFNUNGSPROTOKOLL:
      case Template.OFFERRTOFFNUNGSPROTOKOLL:
      case Template.OFFERTVERGLEICH_FREIHANDIG:
      case Template.OFFERRTOFFNUNGSPROTOKOLL_DL_WW:
      case Template.A_OFFERRTOFFNUNGSPROTOKOLL_DL_WW:
      case Template.BEWERBER_UBERSICHT:

        documentDTO
          .setFilename(documentDTO.getFilename() + setFileExtension(template.getShortCode()));

        // check if submission has only empty offers in order
        // to generate different document for OFFERRTOFFNUNGSPROTOKOLL
        if (templateBean.tenderHasEmptyOffers(submission, template.getShortCode())
          && (template.getShortCode().equals(Template.OFFERRTOFFNUNGSPROTOKOLL)
          || template.getShortCode().equals(Template.A_OFFERRTOFFNUNGSPROTOKOLL))) {

          createOffertoeffnungsDocNoOffers(documentDTO, template, createdDocumentIds, attributesMap,
            placeholders, submission);
        } else {
          /*
           * Create 2 variables header and content that are coming filled from function
           * createSubmittentenListeTable
           */
          LinkedList<String> documentTableHeader = new LinkedList<>();
          List<LinkedHashMap<Map<String, String>, String>> documentTableContent = new ArrayList<>();

          DocumentPropertiesDTO offerDocProperties = new DocumentPropertiesDTO();
          offerDocProperties.setBoldHeader(Boolean.TRUE.toString());
          offerDocProperties.setFonts(DocumentProperties.ARIAL.getValue());
          offerDocProperties.setFontSize(DocumentProperties.NUMBER_20.getValue());
          offerDocProperties.setRemoveBorder(Boolean.FALSE.toString());
          offerDocProperties.setTablePosition(DocumentProperties.NUMBER_10.getValue());
          offerDocProperties.setBoldContent(Boolean.FALSE.toString());
          offerDocProperties.setBottomMargin(DocumentProperties.NUMBER_60.getValue());
          offerDocProperties.setTopMargin(DocumentProperties.NUMBER_60.getValue());
          Map<String, String> tableProperties2 =
            templateBean.fillTableProperties(null, offerDocProperties);

          List<OfferDTO> submissionOffers = templateBean.setOfferProtocolTableProperties(template,
            submission, documentTableHeader, documentTableContent, tableProperties2);
          ruleService.runProjectTemplateRules(submission, null, null, null, placeholders,
            template.getShortCode());
          placeholders.put(DocumentPlaceholders.S_SUM_OFFERS.getValue(),
            String.valueOf(submissionOffers.stream().filter(offer ->  !offer.getIsEmptyOffer()).count()));

          // Repeat table header on every page.
          tableProperties2.put(REPEAT_HEADER, Boolean.TRUE.toString());

          /* replace placeholders , add table create docx file. */
          createdDocumentIds.add(createDocument(documentDTO,
            submissPrintService.convertToPDF(templateService
              .replacePlaceholdersWordDoc(
                new ByteArrayInputStream(templateService
                  .createTableInDocxWithCustomBorders(inputStream, documentTableHeader, null,
                    documentTableContent, tableProperties2, null)
                  .toByteArray()),
                SubmissConverter.replaceSpecialCharactersInPlaceholders(placeholders))
              .toByteArray()),
            null, null, false));
        }
        break;
      case Template.RECHTLICHES_GEHOR:
        if (documentDTO.getLegalHearingType() != null
          && documentDTO.getLegalHearingType().equals(TemplateConstants.EXCLUSION)
          && !documentDTO.getLegalHearingExclusion().isEmpty()) {
          documentDTO.setTitle(documentDTO.getTitle() + " (Ausschluss)");
          attributesMap.put(DocumentAttributes.ADDITIONAL_INFO.name(),
            documentDTO.getLegalHearingType());
          attributesMap.put(DocumentAttributes.TEMPLATE_ID.name(), documentDTO.getTemplateId());
          attributesMap.put(DocumentAttributes.TENANT_ID.name(), documentDTO.getTenantId());
          templateList =
            documentService.getNodeByAttributes(LookupValues.TEMPLATE_FOLDER_ID, attributesMap);
          for (LegalHearingExclusionDTO legal : documentDTO.getLegalHearingExclusion()) {

            // Update Frist Anhörung when exclusion submittent is selected.
            // Use different date when Selektiv 1. Stufe.
            setExclusionDate(submission, legal);
            StringBuilder cancelNumber = new StringBuilder();
            StringBuilder cancelDescr = new StringBuilder();
            for (ExclusionReasonDTO exclusionReason : legal.getExclusionReasons()) {
              if (exclusionReason.getReasonExists()) {
                cancelNumber.append(exclusionReason.getExclusionReason().getValue1())
                  .append(" und Art. 24, Abs. 1 ");
                cancelDescr.append(exclusionReason.getExclusionReason().getValue2())
                  .append(" und ");
              }
            }
            if (cancelNumber.length() > 1) {
              placeholders.put(DocumentPlaceholders.R_CANCEL_ART_NUMBER.getValue(),
                cancelNumber.substring(0, cancelNumber.length() - 21));
              placeholders.put(DocumentPlaceholders.R_CANCEL_ART_DESCRIPTION.getValue(),
                cancelDescr.substring(0, cancelDescr.length() - 5));
            } else {
              placeholders.put(DocumentPlaceholders.R_CANCEL_ART_NUMBER.getValue(),
                TemplateConstants.EMPTY_STRING);
              placeholders.put(DocumentPlaceholders.R_CANCEL_ART_DESCRIPTION.getValue(),
                TemplateConstants.EMPTY_STRING);
            }
            if (legal.getExclusionReason() != null) {
              placeholders.put(DocumentPlaceholders.R_CANCEL_REASON.getValue(),
                legal.getExclusionReason());
            } else {
              placeholders.put(DocumentPlaceholders.R_CANCEL_REASON.getValue(), "");
            }
            if (legal.getExclusionDeadline() != null) {
              placeholders.put(DocumentPlaceholders.R_CANCEL_DEADLINE.getValue(),
                SubmissConverter.convertToSwissDate(legal.getExclusionDeadline()));
            } else {
              placeholders.put(DocumentPlaceholders.R_CANCEL_DEADLINE.getValue(), "");
            }
            placeholders.put(DocumentPlaceholders.COPY_REFERENCE.getValue(),
              submission.getProject().getDepartment().getName());
            documentDTO.setFilename(Template.TEMPLATE_NAMES.RECHTLICHES_GEHOR_AUS.getValue()
              + legal.getSubmittent().getCompanyId().getCompanyName()
              + setFileExtension(template.getShortCode()));

            try (InputStream inputStream1 = new ByteArrayInputStream(
              versionService.getBinContent(templateList.get(0).getId()))) {
              ruleService.runProjectTemplateRules(submission, legal.getSubmittent().getCompanyId(),
                null, null, placeholders, Template.RECHTLICHES_GEHOR_AUSSCHLUSS);
              createdDocumentIds.add(createDocument(documentDTO,
                templateService.replacePlaceholdersWordDoc(inputStream1,
                  SubmissConverter.replaceSpecialCharactersInPlaceholders(placeholders),
                  logo, logoWidth).toByteArray(),
                legal.getSubmittent().getId(), legal.getSubmittent().getCompanyId().getId(),
                false));
            } catch (IOException e) {
              LOGGER.log(Level.SEVERE, TemplateConstants.INPUTSTREAM_ERROR + e.getMessage());
            }

            StringBuilder additionalInfo =
              new StringBuilder(submission.getProject().getProjectName()).append("[#]")
                .append(submission.getProject().getObjectName().getValue1()).append("[#]")
                .append(
                  submission.getWorkType().getValue1() + submission.getWorkType().getValue2())
                .append("[#]")
                .append(
                  getUser().getAttributeData(LookupValues.USER_ATTRIBUTES.TENANT.getValue()))
                .append("[#]");

            auditLog(AuditLevel.PROJECT_LEVEL.name(), AuditEvent.CREATE.name(),
              AuditMessages.RECHTLICHES_GEHOR_DOC_GENERATED.name(), submission.getId(),
              additionalInfo.toString());
          }
        } else {
          documentDTO.setTitle(documentDTO.getTitle() + " (Abbruch)");
          attributesMap.put(DocumentAttributes.ADDITIONAL_INFO.name(),
            TemplateConstants.CANCELATION);
          attributesMap.put(DocumentAttributes.TEMPLATE_ID.name(), documentDTO.getTemplateId());
          attributesMap.put(DocumentAttributes.TENANT_ID.name(), documentDTO.getTenantId());
          templateList =
            documentService.getNodeByAttributes(LookupValues.TEMPLATE_FOLDER_ID, attributesMap);
          if (!submission.getLegalHearingTerminate().isEmpty()) {
            StringBuilder cancelNumber = new StringBuilder();
            StringBuilder cancelDescr = new StringBuilder();
            for (MasterListValueDTO cancel : submission.getLegalHearingTerminate().get(0)
              .getTerminationReason()) {
              MasterListValueHistoryEntity cancelEntity = new JPAQueryFactory(em)
                .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
                .where(qMasterListValueHistoryEntity.masterListValueId.id.eq(cancel.getId())
                  .and(qMasterListValueHistoryEntity.toDate.isNull()))
                .fetchOne();
              cancelNumber.append(cancelEntity.getValue1()).append(" und Art. 29, Absatz ");
              cancelDescr.append(cancelEntity.getValue2()).append(" und ");
            }
            if (cancelNumber.length() > 1) {
              placeholders.put(DocumentPlaceholders.R_CANCEL_ART_NUMBER.getValue(),
                cancelNumber.substring(0, cancelNumber.length() - 21));
              placeholders.put(DocumentPlaceholders.R_CANCEL_ART_DESCRIPTION.getValue(),
                cancelDescr.substring(0, cancelDescr.length() - 5));
            } else {
              placeholders.put(DocumentPlaceholders.R_CANCEL_ART_NUMBER.getValue(),
                TemplateConstants.EMPTY_STRING);
              placeholders.put(DocumentPlaceholders.R_CANCEL_ART_DESCRIPTION.getValue(),
                TemplateConstants.EMPTY_STRING);
            }
            placeholders.put(DocumentPlaceholders.COPY_REFERENCE.getValue(),
              submission.getProject().getDepartment().getName());
            getOfferListForCancel(submission, offers);
            for (SubmittentOfferDTO offer : offers) {
              documentDTO.setFilename(Template.TEMPLATE_NAMES.RECHTLICHES_GEHOR_AB.getValue()
                + offer.getSubmittent().getCompanyId().getCompanyName()
                + setFileExtension(template.getShortCode()));

              try (InputStream inputStream1 = new ByteArrayInputStream(
                versionService.getBinContent(templateList.get(0).getId()))) {
                ruleService.runProjectTemplateRules(submission,
                  offer.getSubmittent().getCompanyId(), null, null, placeholders,
                  Template.RECHTLICHES_GEHOR);
                ruleService.runProjectTemplateRules(submission,
                  offer.getSubmittent().getCompanyId(), null, null, placeholders,
                  Template.RECHTLICHES_GEHOR_EMPTY);
                createdDocumentIds
                  .add(
                    createDocument(documentDTO,
                      templateService.replacePlaceholdersWordDoc(inputStream1,
                        SubmissConverter.replaceSpecialCharactersInPlaceholders(
                          placeholders),
                        logo, logoWidth).toByteArray(),
                      offer.getSubmittent().getId(),
                      offer.getSubmittent().getCompanyId().getId(), false));
              } catch (IOException e) {
                LOGGER.log(Level.SEVERE, TemplateConstants.INPUTSTREAM_ERROR + e.getMessage());
              }
            }

            StringBuilder additionalInfo =
              new StringBuilder(submission.getProject().getProjectName()).append("[#]")
                .append(submission.getProject().getObjectName().getValue1()).append("[#]")
                .append(
                  submission.getWorkType().getValue1() + submission.getWorkType().getValue2())
                .append("[#]")
                .append(
                  getUser().getAttributeData(LookupValues.USER_ATTRIBUTES.TENANT.getValue()))
                .append("[#]");

            auditLog(AuditLevel.PROJECT_LEVEL.name(), AuditEvent.CREATE.name(),
              AuditMessages.RECHTLICHES_GEHOR_DOC_GENERATED.name(), submission.getId(),
              additionalInfo.toString());
          }
        }
        break;
      case Template.SUBMISSIONSUBERSICHT:
        generateSubmissionsubersicht(documentDTO, inputStream, createdDocumentIds, submission,
          offers);
        break;
      case Template.EIGNUNGSPRUFUNG:
        documentDTO
          .setFilename(documentDTO.getFilename() + setFileExtension(Template.EIGNUNGSPRUFUNG));
        try {
          List<SuitabilityDocumentDTO> suitabilityOffers = new ArrayList<>();
          List<OfferDTO> criterionOffers = criterionService
            .getExaminationSubmittentListWithCriteria(submission.getId(),
              TemplateConstants.SUITABILITY, true, true);

          // Clear Submittent list when Selektiv process and status is not greater than
          // OFFER_OPENING_STARTED.
          if (submission.getProcess().equals(Process.SELECTIVE)
            && !compareCurrentVsSpecificStatus(submission.getCurrentState(),
            TenderStatus.SUITABILITY_AUDIT_COMPLETED_S)) {
            offers.clear();
            offers.addAll(submissionService.getApplicantsBySubmission(submission.getId()));
          }

          for (OfferCriterionDTO offerCriterionDTO : criterionOffers.get(0).getOfferCriteria()) {
            // If at least one of the criteria is an evaluated criterion, sort the offers by their
            // examination rank.
            if (offerCriterionDTO.getCriterion().getCriterionType()
              .equals(LookupValues.EVALUATED_CRITERION_TYPE)) {
              Collections.sort(criterionOffers, ComparatorUtil.sortOfferDTOsByExaminationRank);
              break;
            }
          }

          for (OfferDTO criterionOffer : criterionOffers) {
            for (SubmittentOfferDTO submittentOfferDTO : offers) {
              if (submittentOfferDTO.getOffer().getId().equals(criterionOffer.getId())) {
                SuitabilityDocumentDTO suitabilityDTO = new SuitabilityDocumentDTO();
                suitabilityDTO.setCompanyName(
                  submittentOfferDTO.getSubmittent().getCompanyId().getCompanyName());
                suitabilityDTO.setJointVentures(templateBean
                  .getFormattedPartners(submittentOfferDTO.getSubmittent().getJointVentures()));
                suitabilityDTO.setSubContractors(templateBean
                  .getFormattedPartners(submittentOfferDTO.getSubmittent().getSubcontractors()));
                Collections.sort(criterionOffer.getOfferCriteria(),
                  ComparatorUtil.offerCriteriaWithWeightings);
                Collections.reverse(criterionOffer.getOfferCriteria());
                suitabilityDTO.setOffer(criterionOffer);
                if(criterionOffer.getSubmittent().getCompanyId().getIsProofProvided() != null
                  && criterionOffer.getSubmittent().getCompanyId().getIsProofProvided()){
                  suitabilityDTO.setProofStatus(1);
                }else{
                  suitabilityDTO.setProofStatus(0);
                }
                if(submission.getProcess().equals(Process.NEGOTIATED_PROCEDURE)
                || submission.getProcess().equals(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION)){
                  suitabilityDTO.setOfferNotes(submittentOfferDTO
                    .getSubmittent().getFormalAuditNotes());
                }else{
                  suitabilityDTO.setOfferNotes(submittentOfferDTO.getOffer().getNotes());
                }
                suitabilityDTO.setOfferCriteria(criterionOffer.getOfferCriteria());
                suitabilityDTO
                  .setMussCriterienSummary(templateBean.suitabilityDocMussSummary(criterionOffer));
                if (suitabilityDTO.getOffer().getSubmittent().getExistsExclusionReasons() == null){
                  suitabilityDTO.setExistsExclusionReasons(TemplateConstants.EMPTY_STRING);
                } else if (suitabilityDTO.getOffer().getSubmittent().getExistsExclusionReasons() != null
                  && suitabilityDTO.getOffer().getSubmittent().getExistsExclusionReasons()) {
                  suitabilityDTO.setExistsExclusionReasons(TemplateConstants.YES);
                } else {
                  suitabilityDTO.setExistsExclusionReasons(TemplateConstants.NO);
                }

                if (suitabilityDTO.getOffer().getqExExaminationIsFulfilled() != null
                  && suitabilityDTO.getOffer().getqExExaminationIsFulfilled()) {
                  suitabilityDTO.setqExExaminationIsFulfilled(TemplateConstants.YES.toUpperCase());
                } else {
                  suitabilityDTO.setqExExaminationIsFulfilled(TemplateConstants.NO.toUpperCase());
                }

                if (
                  suitabilityDTO.getOffer().getSubmittent().getFormalExaminationFulfilled() != null
                    && suitabilityDTO.getOffer().getSubmittent().getFormalExaminationFulfilled()) {
                  suitabilityDTO.setFormalExaminationFulfilled(TemplateConstants.YES);
                } else {
                  suitabilityDTO.setFormalExaminationFulfilled(TemplateConstants.NO);
                }
                suitabilityOffers.add(suitabilityDTO);
              }
            }
          }

          // create empty suitabilityDTO's to fill the rest columns
          while ((suitabilityOffers.size() % 4) != 0) {
            SuitabilityDocumentDTO suitabilityDTO = new SuitabilityDocumentDTO();
            suitabilityDTO
              .setOffer(templateBean.createEmptyOfferCriteria(suitabilityOffers.get(0).getOffer()));
            suitabilityDTO.setExistsExclusionReasons(TemplateConstants.EMPTY_STRING);
            suitabilityDTO.setqExExaminationIsFulfilled(TemplateConstants.EMPTY_STRING);
            suitabilityDTO.setFormalExaminationFulfilled(TemplateConstants.EMPTY_STRING);
            suitabilityOffers.add(suitabilityDTO);
          }

          byte[] docStream = submissPrintService
            .convertExcelToPDF(importExportFileService.exportDocumentOfCriteria(submission.getId(),
              suitabilityOffers, TemplateConstants.SUITABILITY));

          createdDocumentIds
            .add(createDocument(documentDTO, docStream, null, null, false));
        } catch (Exception e) {
          LOGGER.log(Level.SEVERE, e.getMessage());
        }
        break;
      case Template.ZUSCHLAGSBEWERTUNG:
        documentDTO
          .setFilename(documentDTO.getFilename() + setFileExtension(Template.ZUSCHLAGSBEWERTUNG));
        try {
          List<AwardEvaluationDocumentDTO> awardEvaluationOffers = new ArrayList<>();
          List<OfferDTO> criterionOffers = criterionService
            .getOfferCriteria(submission.getId(), TemplateConstants.AWARD.toLowerCase());
          // sort offers by rank
          Collections.sort(criterionOffers, ComparatorUtil.sortOfferDTOsWithAwardRank);

          for (OfferDTO criterionOffer : criterionOffers) {
            for (SubmittentOfferDTO submittentOfferDTO : offers) {
              if (submittentOfferDTO.getOffer().getId().equals(criterionOffer.getId())) {
                AwardEvaluationDocumentDTO awardEvaluationDTO = new AwardEvaluationDocumentDTO();
                awardEvaluationDTO.setCompanyName(
                  submittentOfferDTO.getSubmittent().getCompanyId().getCompanyName());
                awardEvaluationDTO.setJointVentures(templateBean
                  .getFormattedPartners(submittentOfferDTO.getSubmittent().getJointVentures()));
                awardEvaluationDTO.setSubContractors(templateBean
                  .getFormattedPartners(submittentOfferDTO.getSubmittent().getSubcontractors()));
                Collections.sort(criterionOffer.getOfferCriteria(),
                  ComparatorUtil.offerCriteriaWithWeightings);
                awardEvaluationDTO.setOffer(criterionOffer);
                awardEvaluationOffers.add(awardEvaluationDTO);
              }
            }
          }

          // create empty awardEvaluationDTOs to fill the rest columns
          while ((awardEvaluationOffers.size() % 4) != 0) {
            AwardEvaluationDocumentDTO awardEvaluationDTO = new AwardEvaluationDocumentDTO();
            awardEvaluationDTO.setOffer(
              templateBean.createEmptyOfferCriteria(awardEvaluationOffers.get(0).getOffer()));
            awardEvaluationOffers.add(awardEvaluationDTO);
          }

          // create Document
          byte[] docStream = submissPrintService.convertExcelToPDF(
            importExportFileService.exportAwardEvaluationDocument(submission.getId(),
              awardEvaluationOffers, TemplateConstants.AWARD));

          createdDocumentIds
            .add(createDocument(documentDTO, docStream, null, null, false));
        } catch (Exception e) {
          LOGGER.log(Level.SEVERE, e.getMessage());
        }
        break;
      case Template.NACHTRAG:
        generateNachtrag(documentDTO, templateList, createdDocumentIds, logo,
          logoWidth, placeholders, submission, offers);
        break;
    }

    return createdDocumentIds;
  }

  private void generateNachtrag(DocumentDTO documentDTO, List<NodeDTO> templateList,
    List<String> createdDocumentIds, byte[] logo, long logoWidth,
    HashMap<String, String> placeholders, SubmissionDTO submission,
    List<SubmittentOfferDTO> offers) {

    NachtragDTO nachtragDTO = nachtragService.getNachtrag(documentDTO.getNachtragId());

    try (InputStream inputStream1 = new ByteArrayInputStream(
      versionService.getBinContent(templateList.get(0).getId()))) {

      for(SubmittentOfferDTO offer : offers){
        if(offer.getOffer().getIsAwarded() != null && offer.getOffer().getIsAwarded()
        && StringUtils.equals(offer.getOffer().getId(), nachtragDTO.getOffer().getId())){

          documentDTO
            .setFilename(nachtragDTO.getTitle() + LookupValues.UNDER_SCORE
              + offer.getSubmittent().getCompanyId().getCompanyName() + setFileExtension(
              Template.NACHTRAG));

          replaceNachtragPlaceholders(placeholders, submission, nachtragDTO, offer);

          // create Document
          createdDocumentIds
            .add(
              createDocument(documentDTO,
                templateService.replacePlaceholdersWordDoc(inputStream1,
                  SubmissConverter.replaceSpecialCharactersInPlaceholders(
                    placeholders),
                  logo, logoWidth).toByteArray(), offer.getSubmittent().getId(),
                offer.getSubmittent().getCompanyId().getId(), false));

          nachtragService.updateNachtrag(nachtragDTO);
        }
      }
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, TemplateConstants.INPUTSTREAM_ERROR + e.getMessage());
    }
    StringBuilder additionalInfo =
      new StringBuilder(submission.getProject().getProjectName()).append("[#]")
        .append(submission.getProject().getObjectName().getValue1()).append("[#]")
        .append(submission.getWorkType().getValue1() + submission.getWorkType().getValue2())
        .append("[#]").append(getUser()
        .getAttributeData(LookupValues.USER_ATTRIBUTES.TENANT.getValue())).append("[#]");

    auditLog(AuditLevel.PROJECT_LEVEL.name(), AuditEvent.CREATE.name(),
      AuditMessages.NACHTRAG_CREATED.name(), submission.getId(),
      additionalInfo.toString());
  }

  private void replaceNachtragPlaceholders(HashMap<String, String> placeholders,
    SubmissionDTO submission, NachtragDTO nachtragDTO, SubmittentOfferDTO offer) {

    templateBean.replaceJointVenturesPlaceholder(offer, placeholders);
    templateBean.setCompanyNameOrArge(offer.getOffer(), placeholders,
      DocumentPlaceholders.F_COMPANY_NAME_OR_ARGE.getValue());

    if(submission.getProject().getObjectName().getValue2()!=null){
      placeholders.put("p_object_both_values", submission.getProject().getObjectName().getValue1()
        + ", " + submission.getProject().getObjectName().getValue2());
    } else {
      placeholders.put("p_object_both_values", submission.getProject().getObjectName().getValue1());
    }
    if (offer.getSubmittent().getCompanyId().getAddress2() != null) {
      placeholders.put("f_company_address", offer.getSubmittent().getCompanyId().getAddress1()
        + "\\n" + offer.getSubmittent().getCompanyId().getAddress2());
    } else {
      placeholders.put("f_company_address", offer.getSubmittent().getCompanyId().getAddress1());
    }
    placeholders.put("f_company_location", offer.getSubmittent().getCompanyId().getLocation());
    placeholders.put("f_company_name", offer.getSubmittent().getCompanyId().getCompanyName());
    placeholders.put("f_company_post", offer.getSubmittent().getCompanyId().getPostCode()
      + " " + offer.getSubmittent().getCompanyId().getLocation());

    placeholders.put(DocumentPlaceholders.N_NACHTRAG_NAME.getValue(), nachtragDTO.getNachtragName());

    placeholders.put(DocumentPlaceholders.N_NACHTRAG_DATE.getValue(),
      SubmissConverter.convertToSwissDate(nachtragDTO.getNachtragDate()));

    placeholders.put(DocumentPlaceholders.N_GROSS_AMOUNT.getValue(),
      SubmissConverter.convertToCHFCurrency(BigDecimal.valueOf(nachtragDTO.getGrossAmount())).substring(4));

    BigDecimal discountTotal;

    if(nachtragDTO.getIsDiscountPercentage() && nachtragDTO.getIsDiscount2Percentage()){
      placeholders.put(DocumentPlaceholders.N_DISCOUNT_PERCENT.getValue(),
        String.format("%.2f", nachtragDTO.getDiscount() + nachtragDTO.getDiscount2()) + " %");
      discountTotal = nachtragDTO.getDiscount1Value().add(nachtragDTO.getDiscount2Value());
      placeholders.put(DocumentPlaceholders.N_DISCOUNT_TOTAL.getValue(),
        SubmissConverter.convertToCHFCurrency(discountTotal).substring(4));
    } else if(nachtragDTO.getIsDiscountPercentage() && !nachtragDTO.getIsDiscount2Percentage()){
      placeholders.put(DocumentPlaceholders.N_DISCOUNT_PERCENT.getValue(),
        TemplateConstants.EMPTY_STRING);
      discountTotal = BigDecimal.valueOf(nachtragDTO.getDiscount2())
        .add(nachtragDTO.getDiscount1Value());
        placeholders.put(DocumentPlaceholders.N_DISCOUNT_TOTAL.getValue(),
        SubmissConverter.convertToCHFCurrency(discountTotal).substring(4));
    } else if(!nachtragDTO.getIsDiscountPercentage() && nachtragDTO.getIsDiscount2Percentage()){
      placeholders.put(DocumentPlaceholders.N_DISCOUNT_PERCENT.getValue(),
        TemplateConstants.EMPTY_STRING);
      discountTotal = BigDecimal.valueOf(nachtragDTO.getDiscount())
        .add(nachtragDTO.getDiscount2Value());
      placeholders.put(DocumentPlaceholders.N_DISCOUNT_TOTAL.getValue(),
        SubmissConverter.convertToCHFCurrency(discountTotal).substring(4));
    } else {
      placeholders.put(DocumentPlaceholders.N_DISCOUNT_PERCENT.getValue(),
        TemplateConstants.EMPTY_STRING);
      discountTotal = BigDecimal.valueOf(nachtragDTO.getDiscount() + nachtragDTO.getDiscount2());
      placeholders.put(DocumentPlaceholders.N_DISCOUNT_TOTAL.getValue(),
        SubmissConverter.convertToCHFCurrency(discountTotal).substring(4));
    }

    placeholders.put(DocumentPlaceholders.N_TOTAL_1.getValue(),
      SubmissConverter.convertToCHFCurrency(BigDecimal
        .valueOf(nachtragDTO.getGrossAmount()).subtract(discountTotal)).substring(4));

    if (nachtragDTO.getIsBuildingCostsPercentage() != null
      && nachtragDTO.getIsBuildingCostsPercentage()){
      if(nachtragDTO.getBuildingCosts() != null && nachtragDTO.getBuildingCosts() != 0){
        placeholders.put(DocumentPlaceholders.N_BUILDING_COSTS_PERCENT.getValue(),
          String.format("%.2f", nachtragDTO.getBuildingCosts()) + " %");
        placeholders.put(DocumentPlaceholders.N_BUILDING_COSTS.getValue(),
          SubmissConverter.convertToCHFCurrency(nachtragDTO.getBuildingCostsValue()).substring(4));
      }else{
        placeholders.put(DocumentPlaceholders.N_BUILDING_COSTS_PERCENT.getValue(), "0.00 %");
        placeholders.put(DocumentPlaceholders.N_BUILDING_COSTS.getValue(), "0.00");
      }
    }else {
      if(nachtragDTO.getBuildingCosts() != null && nachtragDTO.getBuildingCosts() != 0){
        placeholders.put(DocumentPlaceholders.N_BUILDING_COSTS_PERCENT.getValue(),
          TemplateConstants.EMPTY_STRING);
        placeholders.put(DocumentPlaceholders.N_BUILDING_COSTS.getValue(),
          SubmissConverter.convertToCHFCurrency(new BigDecimal(nachtragDTO.getBuildingCosts()))
            .substring(4));
      }else{
        placeholders.put(DocumentPlaceholders.N_BUILDING_COSTS_PERCENT.getValue(), "0.00 %");
        placeholders.put(DocumentPlaceholders.N_BUILDING_COSTS.getValue(), "0.00");
      }
    }

    placeholders.put(DocumentPlaceholders.N_TOTAL_2.getValue(),
      SubmissConverter.convertToCHFCurrency(nachtragDTO.getAmount()).substring(4));

    if (nachtragDTO.getIsVatPercentage() != null && nachtragDTO.getIsVatPercentage()){
      if(nachtragDTO.getVat() != null && nachtragDTO.getVat() != 0){
        placeholders.put(DocumentPlaceholders.N_VAT_AMOUNT_PERCENT.getValue(),
          String.format("%.2f", nachtragDTO.getVat()) + " %");
        placeholders.put(DocumentPlaceholders.N_VAT_AMOUNT.getValue(),
          SubmissConverter.convertToCHFCurrency(nachtragDTO.getVatValue()).substring(4));
      }else{
        placeholders.put(DocumentPlaceholders.N_VAT_AMOUNT_PERCENT.getValue(), "0.00 %");
        placeholders.put(DocumentPlaceholders.N_VAT_AMOUNT.getValue(), "0.00");
      }
    }else {
      if(nachtragDTO.getVat() != null && nachtragDTO.getVat() != 0
        && nachtragDTO.getVatValue() != null){
        placeholders.put(DocumentPlaceholders.N_VAT_AMOUNT_PERCENT.getValue(),
          TemplateConstants.EMPTY_STRING);
        placeholders.put(DocumentPlaceholders.N_VAT_AMOUNT.getValue(),
          SubmissConverter.convertToCHFCurrency(nachtragDTO.getVatValue()).substring(4));
      }else{
        placeholders.put(DocumentPlaceholders.N_VAT_AMOUNT_PERCENT.getValue(), "0.00 %");
        placeholders.put(DocumentPlaceholders.N_VAT_AMOUNT.getValue(), "0.00");
      }
    }

    placeholders.put(DocumentPlaceholders.N_AMOUNT_INCL.getValue(),
      SubmissConverter.convertToCHFCurrency(nachtragDTO.getAmountInclusive()).substring(4));

    placeholders.put(DocumentPlaceholders.S_CURRENT_DATE.getValue(),
      SubmissConverter.convertToSwissDate(new Date()));
  }

  /**
   * Generate Award Info (Verfugungen) 1st level documents.
   *
   * @param documentDTO        the documentDTO
   * @param createdDocumentIds the createdDocumentIds
   * @param attributesMap      the attributesMap
   * @param placeholders       the placeholders
   * @param submission         the submission
   * @param logo               the logo
   * @param logoWidth          the logoWidth
   * @param tableIndentLeft    the tableIndentLeft
   */
  private void generateAwardInfoFirstLevelDocuments(DocumentDTO documentDTO,
    List<String> createdDocumentIds, HashMap<String, String> attributesMap,
    HashMap<String, String> placeholders, SubmissionDTO submission, byte[] logo, long logoWidth,
    String tableIndentLeft) {

    LOGGER.log(Level.CONFIG,
      "Executing method generateAwardInfoFirstLevelDocuments, Parameters: documentDTO: {0}, "
        + "createdDocumentIds: {1}, attributesMap: {2}, placeholders: {3}, submission: {4}, "
        + "logo: {5}, logoWidth: {6}, tableIndentLeft: {7}",
      new Object[]{documentDTO, createdDocumentIds, attributesMap, placeholders, submission,
        logo, logoWidth, tableIndentLeft});

    // initialize awardInfo if not saved from user
    initializeAwardInfoFirstLevel(submission.getId());
    attributesMap.put(DocumentAttributes.ADDITIONAL_INFO.name(),
      submission.getProcess().name());
    attributesMap.put(DocumentAttributes.TEMPLATE_ID.name(), documentDTO.getTemplateId());
    attributesMap.put(DocumentAttributes.TENANT_ID.name(), documentDTO.getTenantId());
    templateBean.replaceAwardInfoFirstLevelPlaceholders(documentDTO.getFolderId(), submission,
      placeholders);
    // Get the awarded applicants
    List<OfferDTO> notExcludedApplicants =
      offerService.retrieveOfferApplicants(documentDTO.getFolderId());
    // Get the awarded applicants table
    List<LinkedHashMap<Map<String, String>, Map<String, String>>> awardedApplicants = templateBean
      .replaceAwardedApplicantsPlaceholdersWithNames(notExcludedApplicants);

    for (OfferDTO offer : notExcludedApplicants) {
      templateBean.replaceExcludedPlaceholders(placeholders,
        offer.getExclusionReasonsFirstLevel());
      Collections.sort(offer.getOfferCriteria(), ComparatorUtil.offerCriteriaWithWeightings);
      ruleService.runProjectTemplateRules(submission, offer.getSubmittent().getCompanyId(),
        null, offer, placeholders, Rule.RU007.name());
      List<LinkedHashMap<Map<String, String>, Map<String, String>>> eCriteria =
        templateBean.replaceAwardedApplicantsPlaceholders(offer, documentDTO, placeholders);
      templateBean.setCompanyNameOrArge(offer, placeholders,
        DocumentPlaceholders.F_COMPANY_NAME_OR_ARGE.getValue());
      // Absage
      if (((offer.getqExStatus() != null && offer.getqExStatus())
        && (offer.getqExExaminationIsFulfilled() == null
        || !offer.getqExExaminationIsFulfilled()))
        || ((offer.getExcludedByPassingApplicants() != null
        && offer.getExcludedByPassingApplicants()))) {
        attributesMap.put(LookupValues.TYPE, TemplateConstants.REJECTION);
        documentDTO
          .setFilename(TEMPLATE_NAMES.SELEKTIV_1_STUFE.getValue() + " (Absage)"
            + LookupValues.UNDER_SCORE + offer.getSubmittent().getCompanyId().getCompanyName()
            + setFileExtension(Template.SELEKTIV_1_STUFE));
      }
      // Ausschluss
      else if ((offer.getqExStatus() == null || !offer.getqExStatus())
        && (offer.getqExExaminationIsFulfilled() == null
        || !offer.getqExExaminationIsFulfilled())) {

        templateBean.setCancelDeadline(placeholders, offer,
          SelectiveLevel.FIRST_LEVEL.getValue());
        List<Integer> levels = new ArrayList<>(SelectiveLevel.FIRST_LEVEL.getValue());
        if (offer.getExclusionReasonsFirstLevel() == null
          || offer.getExclusionReasonsFirstLevel().isEmpty()) {
          templateBean.replaceLegalHearingPlaceholders(placeholders, offer, levels);
        }
        attributesMap.put(LookupValues.TYPE, TemplateConstants.EXCLUSION);
        documentDTO
          .setFilename(TEMPLATE_NAMES.SELEKTIV_1_STUFE.getValue() + " (Ausschluss)"
            + LookupValues.UNDER_SCORE + offer.getSubmittent().getCompanyId().getCompanyName()
            + setFileExtension(Template.SELEKTIV_1_STUFE));
      }
      // Zulassung
      else if (offer.getExcludedFirstLevel() == null
        || !offer.getExcludedFirstLevel()) {
        attributesMap.put(LookupValues.TYPE, TemplateConstants.AWARD);
        documentDTO
          .setFilename(TEMPLATE_NAMES.SELEKTIV_1_STUFE.getValue() + " (Zulassung)"
            + LookupValues.UNDER_SCORE + offer.getSubmittent().getCompanyId().getCompanyName()
            + setFileExtension(Template.SELEKTIV_1_STUFE));
      }
      placeholders.put(DocumentPlaceholders.O_SUM_OF_APPLICANTS.getValue(),
        String.valueOf(notExcludedApplicants.size()));
      List<NodeDTO> templateList =
        documentService.getNodeByAttributes(LookupValues.TEMPLATE_FOLDER_ID, attributesMap);
      // Map the placeholder with its table
      HashMap placeholdersWithTables = new HashMap<String, List<LinkedHashMap<Map<String, String>, Map<String, String>>>>();
      placeholdersWithTables.put(DocumentPlaceholders.AWARDED_COMPANY_NAME.getValue(), awardedApplicants);
      placeholdersWithTables.put(DocumentPlaceholders.E_CRITERION_NAME.getValue(), eCriteria);
      // create the document
      try (InputStream inputStream1 = new ByteArrayInputStream(
        versionService.getBinContent(templateList.get(0).getId()))) {
        createdDocumentIds
          .add(createDocument(documentDTO,
            templateService.replacePlaceholdersWordDoc(
              new ByteArrayInputStream(templateService
                .replacePlaceholdersWithTables(inputStream1, placeholdersWithTables,
                  tableIndentLeft).toByteArray()),
              SubmissConverter.replaceSpecialCharactersInPlaceholders(placeholders),
              logo, logoWidth).toByteArray(),
            offer.getSubmittent().getId(), offer.getSubmittent().getCompanyId().getId(),
            false));
      } catch (IOException e) {
        LOGGER.log(Level.SEVERE, TemplateConstants.INPUTSTREAM_ERROR + e.getMessage());
      }
    }
    // Update status (if applicable).
    if (!compareCurrentVsSpecificStatus(
      TenderStatus.fromValue(
        submissionService.getCurrentStatusOfSubmission(documentDTO.getFolderId())),
      TenderStatus.AWARD_NOTICES_1_LEVEL_CREATED)) {
      submissionService.updateSubmissionStatus(submission.getId(),
        TenderStatus.AWARD_NOTICES_1_LEVEL_CREATED.getValue(),
        AuditMessages.SELEKTIV_1_STUFE_DOC_GENERATED.name(), null,
        LookupValues.EXTERNAL_LOG);
    }
  }

  /**
   * Initialize award info first level.
   *
   * @param submissionId the submissionId
   */
  private void initializeAwardInfoFirstLevel(String submissionId) {

    LOGGER.log(Level.CONFIG, "Executing method initializeAwardInfoFirstLevel, Parameters: submissionId: {0}",
      submissionId);

    // Save award info default values.
    AwardInfoFirstLevelDTO awardInfoFirstLevel =
      submissionCloseService.getAwardInfoFirstLevel(submissionId);
    if (awardInfoFirstLevel == null || awardInfoFirstLevel.getAvailableDate() == null) {
      // Save the first level award info default values.
      if (awardInfoFirstLevel == null) {
        awardInfoFirstLevel = new AwardInfoFirstLevelDTO();
      }
      awardInfoFirstLevel.setAvailableDate(new Date());
      awardInfoFirstLevel.setObjectNameRead(Boolean.TRUE);
      awardInfoFirstLevel.setProjectNameRead(Boolean.TRUE);
      awardInfoFirstLevel.setWorkingClassRead(Boolean.TRUE);
      awardInfoFirstLevel.setDescriptionRead(Boolean.TRUE);
      submissionCloseService.createAwardInfoFirstLevel(awardInfoFirstLevel);
    } else {
      // we need to update the award in case of reopening statuses and adding new Submittents
      submissionCloseService.updateAwardInfoFirstLevel(awardInfoFirstLevel);
    }
  }

  /**
   * Generate Beko document.
   *
   * @param documentDTO        the documentDTO
   * @param inputStream        the inputStream
   * @param createdDocumentIds the createdDocumentIds
   * @param placeholders       the placeholders
   * @param submission         the submission
   * @param logo               the logo
   * @param logoWidth          the logoWidth
   * @param allowedTemplates   the allowedTemplates
   * @param offers             the offers
   * @param title              the title
   */
  private void generateBekoDocument(DocumentDTO documentDTO, InputStream inputStream,
    List<String> createdDocumentIds, HashMap<String, String> placeholders,
    SubmissionDTO submission, byte[] logo, long logoWidth,
    List<MasterListValueHistoryDTO> allowedTemplates, List<SubmittentOfferDTO> offers,
    String title) {

    LOGGER.log(Level.CONFIG,
      "Executing method generateBekoDocument, Parameters: documentDTO: {0}, "
        + "inputStream: {1}, createdDocumentIds: {2}, placeholders: {3}, submission: {4}, "
        + "logo: {5}, logoWidth: {6}, allowedTemplates: {7}, offers: {8}, title: {9}",
      new Object[]{documentDTO, inputStream, createdDocumentIds, placeholders, submission,
        logo, logoWidth, allowedTemplates, offers, title});

    documentDTO.setFilename(documentDTO.getFilename() + setFileExtension(title));
    // Security check if template exists in template list
    Optional<MasterListValueHistoryDTO> isTemplateAllowed = allowedTemplates.stream()
      .filter(allowedTemplate -> allowedTemplate.getShortCode().equals(title)).findFirst();
    if (isTemplateAllowed.isPresent()) {
      createBekoDocument(documentDTO, inputStream, createdDocumentIds, placeholders, submission,
        logo, logoWidth, offers, title);
    }
  }

  /**
   * Creating the Beko document.
   *
   * @param documentDTO        the documentDTO
   * @param inputStream        the inputStream
   * @param createdDocumentIds the createdDocumentIds
   * @param placeholders       the placeholders
   * @param submission         the submission
   * @param logo               the logo
   * @param logoWidth          the logoWidth
   * @param offers             the offers
   * @param title              the title
   */
  private void createBekoDocument(DocumentDTO documentDTO, InputStream inputStream,
    List<String> createdDocumentIds, HashMap<String, String> placeholders,
    SubmissionDTO submission, byte[] logo, long logoWidth, List<SubmittentOfferDTO> offers,
    String title) {

    LOGGER.log(Level.CONFIG,
      "Executing method createBekoDocument, Parameters: documentDTO: {0}, "
        + "inputStream: {1}, createdDocumentIds: {2}, placeholders: {3}, submission: {4}, "
        + "logo: {5}, logoWidth: {6}, offers: {7}, title: {8}",
      new Object[]{documentDTO, inputStream, createdDocumentIds, placeholders, submission,
        logo, logoWidth, offers, title});

    ruleService.runProjectTemplateRules(submission, null, null,
      null, placeholders, title);
    List<LinkedHashMap<Map<String, String>, Map<String, String>>> tableList =
      templateBean.replaceBekoAwardedCompanies(offers);
    createdDocumentIds.add(createDocument(documentDTO,
      templateService.replacePlaceholdersWordDoc(new ByteArrayInputStream(
          templateService.replacePlaceholderWithTable(inputStream,
            tableList, "ba_award_table", "0").toByteArray()),
        SubmissConverter.replaceSpecialCharactersInPlaceholders(placeholders),
        logo, logoWidth).toByteArray(), null, null, false));
  }

  /**
   * Generate Submissionsubersicht document.
   *
   * @param documentDTO        the documentDTO
   * @param inputStream        the inputStream
   * @param createdDocumentIds the createdDocumentIds
   * @param submission         the submission
   * @param offers             the offers
   */
  private void generateSubmissionsubersicht(DocumentDTO documentDTO, InputStream inputStream,
    List<String> createdDocumentIds, SubmissionDTO submission, List<SubmittentOfferDTO> offers) {

    LOGGER.log(Level.CONFIG,
      "Executing method generateSubmissionsubersicht, Parameters: documentDTO: {0}, "
        + "inputStream: {1}, createdDocumentIds: {2}, submission: {3}, offers: {4}",
      new Object[]{documentDTO, inputStream, createdDocumentIds, submission, offers});

    documentDTO
      .setFilename(documentDTO.getFilename() + setFileExtension(Template.SUBMISSIONSUBERSICHT));

    List<SubmittentDTO> submittentsList = submissionService
      .getSubmittentsBySubmission(submission.getId());

    // Retrieve also empty offers
    submittentsList.addAll(submissionService.retrieveEmptyOffers(submission));
    for (SubmittentOfferDTO submittentOfferDTO : offers) {
      for (SubmittentDTO submittentDTO : submittentsList) {
        if (submittentOfferDTO.getSubmittent().getId().equals(submittentDTO.getId())) {
          submittentOfferDTO.setSubmittent(submittentDTO);
        }
      }
    }

    // Clear Submittent list when Selektiv process and status is not greater than
    // OFFER_OPENING_STARTED.
    if (submission.getProcess().equals(Process.SELECTIVE)
      && (!compareCurrentVsSpecificStatus(submission.getCurrentState(),
      TenderStatus.SUITABILITY_AUDIT_COMPLETED_S)
      || submission.getStatus()
      .equals(TenderStatus.SUITABILITY_AUDIT_COMPLETED_S.getValue()))) {
      submittentsList = submissionService.getApplicantsForFormalAudit(submission.getId());
      offers.clear();
      offers.addAll(submissionService.getApplicantsBySubmission(submission.getId()));
      for (SubmittentOfferDTO submittentOfferDTO : offers) {
        for (SubmittentDTO submittentDTO : submittentsList) {
          if (submittentOfferDTO.getSubmittent().getId().equals(submittentDTO.getId())) {
            submittentOfferDTO.setSubmittent(submittentDTO);
          }
        }
      }
    }

    List<SubmissionOverviewDTO> submissionOverviewList = new ArrayList<>();
    for (SubmittentOfferDTO submittentOffer : offers) {
      SubmissionOverviewDTO submissionOverview = new SubmissionOverviewDTO();
      submissionOverview.setOffer(submittentOffer.getOffer());
      submissionOverview.setSubmittent(submittentOffer.getSubmittent());
      submissionOverview
        .setOfferAmount(templateBean.setCurrencyFormat(submittentOffer.getOffer().getAmount()));
      submissionOverview.setOperatingOfferAmount(
        templateBean.setCurrencyFormat(submittentOffer.getOffer().getOperatingCostsAmount()));
      submissionOverview
        .setOperatingCostNotes(submittentOffer.getOffer().getOperatingCostNotes());
      submissionOverview.setSubmittentARGESubContractors(
        templateBean.getSubmittentArgeSubContractorNames(submittentOffer));
      submissionOverview.setAllCompanies(
        templateBean
          .getAllCompaniesOverView(submittentOffer.getSubmittent(), submissionOverview));
      if (submissionOverview.isHasPartners()) {
        submissionOverview.setStatusOfCompanies(
          templateBean.getAllCompaniesOverViewStatus(submissionOverview.getAllCompanies()));
      } else {
        StringBuilder sbRemarks = new StringBuilder();
        templateBean
          .getCompanyRemarks(submittentOffer.getSubmittent().getCompanyId(), sbRemarks);
        submissionOverview.setCompanyRemarks(sbRemarks.toString());

        StringBuilder sbProofs = new StringBuilder();
        List<CompanyProofDTO> companyProofs = companyService
          .getProofByCompanyId(submittentOffer.getSubmittent().getCompanyId().getId());
        Collections.reverse(companyProofs);
        List<String> proofs = templateBean.getCompanyListOfProofs(companyProofs, sbProofs);
        submissionOverview.setAllProofStatuses(StringUtils.join(proofs, " / "));

        submissionOverview.setStatusOfCompanies(templateBean
          .getCompanyOverviewProofStatus(submittentOffer.getSubmittent().getCompanyId()));
      }
      submissionOverviewList.add(submissionOverview);
    }

    // sort results according to status of submission
    templateBean.setCompaniesRank(submission, submissionOverviewList);
    // calculate the price percentage for every submittent
    templateBean.calculateOfferPricePercentage(submission, submissionOverviewList);

    try {
      Map<String, Object> parameters = new HashMap<>();
      StringBuilder readValues = templateBean.readTemplatesValues(submission);
      parameters.put("READ_VALUES", readValues.toString());
      parameters.put(TemplateConstants.DATASOURCE,
        new JRBeanCollectionDataSource(submissionOverviewList));
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      JasperCompileManager.compileReportToStream(inputStream, byteArrayOutputStream);
      InputStream stream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
      JasperReport jasperReport = (JasperReport) JRLoader.loadObject(stream);
      JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
        new JRBeanCollectionDataSource(submissionOverviewList));

      /*
       * check if template generates extra blank page at the end of document
       * If extra page is generated that means that the page has only 3 elements (header - submission info values - paging)
       * so we check if there are only 3 values and remove that page
       * then we fix page numbering
       */
      for (Iterator<JRPrintPage> i = jasperPrint.getPages().iterator(); i.hasNext(); ) {
        JRPrintPage page = i.next();
        if (page.getElements().size() == 3) {
          i.remove();
          templateBean.fixTemplatePaging(jasperPrint);
        }
      }

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      JRPdfExporter exporter = new JRPdfExporter();
      SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
      exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
      exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
      exporter.setConfiguration(configuration);
      exporter.exportReport();
      createdDocumentIds
        .add(createDocument(documentDTO, outputStream.toByteArray(), null, null, false));
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }
  }

  private void createOffertoeffnungsDocNoOffers(DocumentDTO documentDTO,
    MasterListValueHistoryEntity template, List<String> createdDocumentIds,
    HashMap<String, String> attributesMap, HashMap<String, String> placeholders,
    SubmissionDTO submission) {

    LOGGER.log(Level.CONFIG,
      "Executing method createOffertoeffnungsDocNoOffers, Parameters: documentDTO: {0}, "
        + "template: {1}, createdDocumentIds: {2}, attributesMap: {3}, placeholders: {4}, "
        + "submission: {5}",
      new Object[]{documentDTO, template, createdDocumentIds, attributesMap, placeholders,
        submission});

    List<NodeDTO> templateList;
    attributesMap.put(DocumentAttributes.TENANT_ID.name(), documentDTO.getTenantId());
    attributesMap.put("NAME", TemplateConstants.OFFERRTOFFNUNGSPROTOKOLL_NO_OFFERS);
    templateList =
      documentService.getNodeByAttributes(LookupValues.TEMPLATE_FOLDER_ID, attributesMap);
    try (InputStream inputStream1 =
      new ByteArrayInputStream(versionService.getBinContent(templateList.get(0).getId()))) {
      templateBean.getOfferrtoffnungsprotokollTitle(placeholders, template.getShortCode());
      ruleService.runProjectTemplateRules(submission, null, null, null, placeholders,
        template.getShortCode());
      createdDocumentIds
        .add(
          createDocument(documentDTO,
            submissPrintService.convertToPDF(templateService
              .replacePlaceholdersWordDoc(inputStream1,
                SubmissConverter
                  .replaceSpecialCharactersInPlaceholders(placeholders))
              .toByteArray()),
            null, null, false));
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, TemplateConstants.INPUTSTREAM_ERROR + e.getMessage());
    }
  }

  /**
   * Handles the submittent proof status fabe calculation in case of SUBMITTENTENLISTE document
   * creation.
   *
   * @param submittentDTO the submittent DTO
   * @return the submittent proof status fabe
   */
  private Integer handleSubmittentProofStatusFabe(SubmittentDTO submittentDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method handleSubmittentProofStatusFabe, Parameters: submittentDTO: {0}",
      submittentDTO);

    if ((submittentDTO.getJointVentures().isEmpty() && submittentDTO.getSubcontractors().isEmpty())
      || submittentDTO.getCompanyId().getProofStatusFabe()
      .equals(ProofStatus.WITH_KAIO.getValue())) {
      // If the submittent has no joint ventures and no subcontractors, or if the submittent has
      // proof status fabe KAIO, just return the submittent proof status fabe.
      return submittentDTO.getCompanyId().getProofStatusFabe();
    } else if (!submittentDTO.getJointVentures().isEmpty()
      || !submittentDTO.getSubcontractors().isEmpty()) {
      Integer submittentProofStatusFabe = submittentDTO.getCompanyId().getProofStatusFabe();

      if (!submittentDTO.getJointVentures().isEmpty()) {
        // If the submittent has joint ventures, check their proof status fabe.
        for (CompanyDTO jointVenture : submittentDTO.getJointVentures()) {
          if (jointVenture.getProofStatusFabe().equals(ProofStatus.WITH_KAIO.getValue())) {
            // If the joint venture has proof status fabe KAIO, just return the joint venture proof
            // status fabe.
            return jointVenture.getProofStatusFabe();
          } else if ((jointVenture.getProofStatusFabe().equals(ProofStatus.NOT_ACTIVE.getValue())
            && submittentProofStatusFabe.equals(ProofStatus.ALL_PROOF.getValue()))
            || jointVenture.getProofStatusFabe().equals(ProofStatus.WITH_FABE.getValue())) {
            // If the submittent has proof status fabe ALL_PROOF and the joint venture has proof
            // status fabe NOT_ACTIVE, or if the joint venture has proof status fabe WITH_FABE, set
            // the joint venture proof status fabe as the submittent proof status fabe.
            submittentProofStatusFabe = jointVenture.getProofStatusFabe();
          }
        }
      }

      if (!submittentDTO.getSubcontractors().isEmpty()) {
        // If the submittent has subcontractors, check their proof status fabe.
        for (CompanyDTO subcontractor : submittentDTO.getSubcontractors()) {
          if (subcontractor.getProofStatusFabe().equals(ProofStatus.WITH_KAIO.getValue())) {
            // If the subcontractor has proof status fabe KAIO, just return the subcontractor proof
            // status fabe.
            return subcontractor.getProofStatusFabe();
          } else if ((subcontractor.getProofStatusFabe().equals(ProofStatus.NOT_ACTIVE.getValue())
            && submittentProofStatusFabe.equals(ProofStatus.ALL_PROOF.getValue()))
            || subcontractor.getProofStatusFabe().equals(ProofStatus.WITH_FABE.getValue())) {
            // If the submittent has proof status fabe ALL_PROOF and the subcontractor has proof
            // status fabe NOT_ACTIVE, or if the subcontractor has proof status fabe WITH_FABE, set
            // the subcontractor proof status fabe as the submittent proof status fabe.
            submittentProofStatusFabe = subcontractor.getProofStatusFabe();
          }
        }
      }

      // Return the submittent proof status fabe.
      return submittentProofStatusFabe;
    }
    return null;
  }

  /**
   * Initialize award info.
   *
   * @param submission the submission
   */
  private void initializeAwardInfo(SubmissionDTO submission) {

    LOGGER.log(Level.CONFIG, "Executing method initializeAwardInfo, Parameters: submission: {0}",
      submission);

    // Save award info default values.
    AwardInfoDTO award = submissionCloseService.getAwardInfo(submission.getId());
    if (award == null || award.getAvailableDate() == null) {
      if (award == null) {
        award = new AwardInfoDTO();
      }
      award.setAvailableDate(new Date());
      award.setObjectNameRead(Boolean.TRUE);
      award.setProjectNameRead(Boolean.TRUE);
      award.setWorkingClassRead(Boolean.TRUE);
      award.setDescriptionRead(Boolean.TRUE);
      submissionCloseService.createAwardInfo(award);
    } else {
      // we need to update the award in case of reopening statuses and adding new Submittents
      submissionCloseService.updateAwardInfo(award);
    }
  }

  /**
   * Sets the exclusion date.
   *
   * @param submission the submission
   * @param legal the legal
   */
  private void setExclusionDate(SubmissionDTO submission, LegalHearingExclusionDTO legal) {

    LOGGER.log(Level.CONFIG, "Executing method setExclusionDate, Parameters: submission: {0}, "
        + "legal: {1}",
      new Object[]{submission, legal});

    if (legal.getLevel().equals(SelectiveLevel.FIRST_LEVEL.getValue())) {
      if (submission.getFirstLevelExclusionDate() != null && legal.getId() != null) {
        LegalHearingExclusionEntity entity =
          em.find(LegalHearingExclusionEntity.class, legal.getId());
        entity.setFirstLevelExclusionDate(submission.getFirstLevelExclusionDate());
        String userId = getUserId();
        entity.setUpdatedBy(userId);
        em.merge(entity);
      }
      legal.setExclusionDeadline(submission.getFirstLevelExclusionDate());
    } else {
      if (submission.getExclusionDeadline() != null && legal.getId() != null) {
        LegalHearingExclusionEntity entity =
          em.find(LegalHearingExclusionEntity.class, legal.getId());
        entity.setExclusionDeadline(submission.getExclusionDeadline());
        String userId = getUserId();
        entity.setUpdatedBy(userId);
        em.merge(entity);
      }
      legal.setExclusionDeadline(submission.getExclusionDeadline());
    }
  }

  /**
   * Sets the rejected excluded properties.
   *
   * @param documentDTO the document DTO
   * @param offer the offer
   * @param attributesMap the attributes map
   * @param type the type
   * @param name the name
   */
  private void setRejectedExcludedProperties(DocumentDTO documentDTO, SubmittentOfferDTO offer,
    HashMap<String, String> attributesMap, String type, String name) {

    LOGGER.log(Level.CONFIG,
      "Executing method setRejectedExcludedProperties, Parameters: documentDTO: {0}, "
        + "offer: {1}, attributesMap: {2}, type: {3}, name: {4}",
      new Object[]{documentDTO, offer, attributesMap, type, name});

    documentDTO.setFilename(name + LookupValues.UNDER_SCORE
      + offer.getSubmittent().getCompanyId().getCompanyName()
      + setFileExtension(Template.VERFUGUNGEN_DL_WETTBEWERB));
    documentDTO.setTitle(name);
    attributesMap.put(LookupValues.TYPE, type);
    attributesMap.put(LookupValues.WITH_VORBEHALT, "NO");
  }

  /**
   * Sets the award properties.
   *
   * @param documentDTO the document DTO
   * @param placeholders the placeholders
   * @param attributesMap the attributes map
   * @param offer the offer
   * @param submission the submission
   */
  private void setAwardProperties(DocumentDTO documentDTO, HashMap<String, String> placeholders,
    HashMap<String, String> attributesMap, SubmittentOfferDTO offer, SubmissionDTO submission) {

    LOGGER.log(Level.CONFIG,
      "Executing method setAwardProperties, Parameters: documentDTO: {0}, "
        + "placeholders: {1}, attributesMap: {2}, offer: {3}, submission: {4}",
      new Object[]{documentDTO, placeholders, attributesMap, offer, submission});

    attributesMap.put(LookupValues.TYPE, TemplateConstants.AWARD);

    documentDTO.setFilename(TemplateConstants.ZUSCHLAG + LookupValues.UNDER_SCORE
      + offer.getSubmittent().getCompanyId().getCompanyName()
      + setFileExtension(Template.VERFUGUNGEN_DL_WETTBEWERB));
    documentDTO.setTitle(TemplateConstants.ZUSCHLAG);

    templateBean.setCompanyNameOrArge(offer.getOffer(), placeholders,
      DocumentPlaceholders.AWARDED_COMPANY_NAME_OR_ARGE.getValue());

    if (!submission.getProcess().equals(Process.NEGOTIATED_PROCEDURE)
      && !submission.getProcess().equals(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION)
      && (StringUtils.isNotBlank(submission.getCommissionProcurementProposalReservation())
      && !submission.getCommissionProcurementProposalReservation().equalsIgnoreCase(
      CommissionProcurementProposalReservation.RESERVATION_NONE.getValue()))) {
      attributesMap.put(LookupValues.WITH_VORBEHALT, "YES");
      placeholders.put(DocumentPlaceholders.BA_RESERVATION.getValue(),
        submission.getCommissionProcurementProposalReservation());
    } else {
      attributesMap.put(LookupValues.WITH_VORBEHALT, "NO");
    }

    if (offer.getOffer().getNotes() != null) {
      placeholders.put(DocumentPlaceholders.AWARDED_NOTES.getValue(),
        "Projekts " + offer.getOffer().getNotes());
    } else {
      placeholders.put(DocumentPlaceholders.AWARDED_NOTES.getValue(), "Projekts");
    }
  }

  /**
   * Update submission award notices status.
   *
   * @param submission the submission
   */
  private void updateSubmissionAwardNoticesStatus(SubmissionDTO submission) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateSubmissionAwardNoticesStatus, Parameters: submission: {0}",
      submission);

    submissionService.updateSubmissionStatus(submission.getId(),
      TenderStatus.AWARD_NOTICES_CREATED.getValue(),
      AuditMessages.VERFUGUNGEN_DOCS_GENERATED.name(), null, LookupValues.EXTERNAL_LOG);
    // Now that the status has been set to AWARD_NOTICES_CREATED, set the isLocked submission value
    // to false.
    SubmissionEntity submissionEntity = em.find(SubmissionEntity.class, submission.getId());
    submissionEntity.setIsLocked(Boolean.FALSE);
    em.merge(submissionEntity);
    // Update security accordingly.
    security.editSubmissionLockedGroupResources(submissionEntity.getId(),
      submissionEntity.getIsLocked());
  }

  /**
   * Gets the template input stream.
   *
   * @param documentDTO the document DTO
   * @param templateList the template list
   * @param inputStream the input stream
   * @return the template input stream
   */
  private InputStream getTemplateInputStream(DocumentDTO documentDTO, List<NodeDTO> templateList,
    InputStream inputStream) {

    LOGGER.log(Level.CONFIG,
      "Executing method getTemplateInputStream, Parameters: documentDTO: {0}, "
        + "templateList: {1}, inputStream: {2}",
      new Object[]{documentDTO, templateList, inputStream});

    if (!templateList.isEmpty()) {
      inputStream =
        new ByteArrayInputStream(versionService.getBinContent(templateList.get(0).getId()));
    }

    // If template is derived from user selection then set Zertifikat and Nachweisbrief (from
    // project part) flags to false.
    documentDTO.setGenerateProofTemplate(Boolean.FALSE);
    documentDTO.setIsCompanyCertificate(Boolean.FALSE);
    return inputStream;
  }

  /**
   * Retrieve template.
   *
   * @param documentDTO the document DTO
   * @return the list
   */
  private List<NodeDTO> retrieveTemplate(DocumentDTO documentDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method retrieveTemplate, Parameters: documentDTO: {0}",
      documentDTO);

    HashMap<String, String> attributesMap = new HashMap<>();
    attributesMap.put(DocumentAttributes.TEMPLATE_ID.name(), documentDTO.getTemplateId());
    attributesMap.put(DocumentAttributes.TENANT_ID.name(), documentDTO.getTenantId());

    return documentService.getNodeByAttributes(LookupValues.TEMPLATE_FOLDER_ID, attributesMap);
  }

  /**
   * Creates the document.
   *
   * @param documentDTO the document DTO
   * @param templateByteArray the template byte array
   * @param submittentId the submittent id
   * @param companyId the company id
   * @param showInCompany the show in company
   * @return the string
   */
  /*
   * (non-Javadoc)
   *
   * @see ch.bern.submiss.services.api.administration.SubDocumentService#createDocument()
   */
  private String createDocument(DocumentDTO documentDTO, byte[] templateByteArray,
    String submittentId, String companyId, Boolean showInCompany) {

    LOGGER.log(Level.CONFIG,
      "Executing method createDocument, Parameters: documentDTO: {0}, "
        + "templateByteArray: {1}, submittentId: {2}, companyId: {3}, showInCompany: {4}",
      new Object[]{documentDTO, templateByteArray, submittentId, companyId, showInCompany});

    String folderId = null;
    VersionDTO docLatestVersion = null;
    String versionId = null;

    try {
      FolderDTO persitedFolderDTO =
        documentService.getFolderByID(documentDTO.getFolderId(), true, false);

      // Make the document file name valid.
      documentDTO.setFilename(makeFileNameValid(documentDTO.getFilename()));

      // If no directory for submission or company exists, create a new one, else fetch existing
      if (persitedFolderDTO == null) {
        FolderDTO folder = new FolderDTO();
        folder.setId(documentDTO.getFolderId());
        folder.setCreatedOn(new Date().getTime());

        folderId = documentService.createFolder(folder, getUserFullName(), null);
      } else {
        folderId = persitedFolderDTO.getId();
      }

      docLatestVersion = getDocLatestversion(documentDTO, submittentId, companyId);

      if (documentDTO.isCreateVersion()) {

        if (docLatestVersion == null) {
          // Create a new version for document
          versionId = createNewVersionedDocument(documentDTO, templateByteArray, folderId,
            submittentId, companyId, true, showInCompany);
        } else {
          versionId =
            createVersionedDocument(documentDTO, templateByteArray, docLatestVersion, true);
        }
      } else {
        if (docLatestVersion == null) {
          // Create a document in the database without a version
          versionId = createNewNonVersionedDocument(documentDTO, templateByteArray, folderId,
            submittentId, companyId, showInCompany, true);
        } else {
          versionId = docLatestVersion.getId();
          docLatestVersion.setFilename(documentDTO.getFilename());
          docLatestVersion.getAttributes().put(DocumentAttributes.TITLE.name(),
            documentDTO.getTitle());
          docLatestVersion.getAttributes().put(DocumentAttributes.DOCUMENT_CREATION_TYPE.name(),
            DocumentCreationType.GENERATED.toString());
          updateDocumentFileAttributes(documentDTO, docLatestVersion);
          versionService.updateVersion(docLatestVersion.getNodeId(), docLatestVersion,
            templateByteArray, getUserFullName(), true, null);

          versionService.replaceVersionContent(docLatestVersion.getId(), templateByteArray);

        }
      }

    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Unable to create document file: " + e);
    }
    return versionId;
  }

  /**
   * Makes the given file name valid, by replacing all forbidden characters with underscore ('_').
   *
   * @param fileName the file name
   * @return valid file name
   */
  private String makeFileNameValid(String fileName) {
    char[] fileNameChars = fileName.toCharArray();
    for (int i = 0; i < fileNameChars.length; i++) {
      if (fileNameChars[i] == '/' || fileNameChars[i] == '\\' || fileNameChars[i] == '?'
        || fileNameChars[i] == ':' || fileNameChars[i] == '>' || fileNameChars[i] == '<'
        || fileNameChars[i] == '"' || fileNameChars[i] == '|'
        || fileNameChars[i] == '*') {
        fileNameChars[i] = '_';
      }
    }
    return new String(fileNameChars);
  }

  /**
   * Update document file attributes.
   *
   * @param documentDTO the document DTO
   * @param docLatestVersion the doc latest version
   */
  private void updateDocumentFileAttributes(DocumentDTO documentDTO, VersionDTO docLatestVersion) {
    Map<String, String> attributes = new HashMap<>();
    // if the document can be generated only by Admin, then it is set to Private.
    // If Verfahren is F/FK under threshold then documents are not private. After reopen
    // Verfahren F/FK is
    // over threshold. Document should be private. So we also have to update the
    // PRIVATE_DOCUMENT attribute on FileDTO.
    if (documentDTO.getIsAdminRightsOnly() != null && documentDTO.getIsAdminRightsOnly()) {
      attributes.put(DocumentAttributes.PRIVATE_DOCUMENT.name(), Boolean.TRUE.toString());
      attributes.put(DocumentAttributes.PRIVATE_GROUP.name(), getGroupName(getUser()));
    }
    documentService.updateAttributes(docLatestVersion.getNodeId(), attributes,
      getUserFullName(), null);
    documentService.renameFile(docLatestVersion.getNodeId(), documentDTO.getFilename(),
      getUserFullName(), null);
  }

  /**
   * Creates the versioned document.
   *
   * @param documentDTO the document DTO
   * @param templateByteArray the template byte array
   * @param docLatestVersion the doc latest version
   * @param generated the generated
   * @return the string
   */
  private String createVersionedDocument(DocumentDTO documentDTO, byte[] templateByteArray,
    VersionDTO docLatestVersion, Boolean generated) {

    LOGGER.log(Level.CONFIG,
      "Executing method createVersionedDocument, Parameters: documentDTO: {0}, "
        + "templateByteArray: {1}, docLatestVersion: {2}, generated: {3}",
      new Object[]{documentDTO, templateByteArray, docLatestVersion, generated});
    // Increment version number
    Integer newVersionNumber = null;
    if (docLatestVersion.getAttributes() == null
      || docLatestVersion.getAttributes().get(DocumentAttributes.VERSION.name()) == null) {
      newVersionNumber = 1;
    } else {
      newVersionNumber =
        Integer.parseInt(docLatestVersion.getAttributes().get(DocumentAttributes.VERSION.name()))
          + 1;
    }

    VersionDTO versionDTO = new VersionDTO();
    versionDTO.setCreatedOn(new Date().getTime());
    versionDTO.setAttributes(new HashMap<String, String>());
    versionDTO.setName(newVersionNumber.toString());
    versionDTO.getAttributes().put(DocumentAttributes.VERSION.name(), newVersionNumber.toString());
    versionDTO.getAttributes().put(DocumentAttributes.ACTIVE.name(), Boolean.TRUE.toString());
    versionDTO.getAttributes().put(DocumentAttributes.TITLE.name(), documentDTO.getTitle());
    String groupName = getGroupName(getUser());
    versionDTO.getAttributes().put(DocumentAttributes.CREATORS_GROUP.name(), groupName);
    versionDTO.getAttributes().put(DocumentAttributes.IS_PROJECT_DOCUMENT.name(),
      String.valueOf(documentDTO.isProjectDocument()));
    versionDTO.getAttributes().put(DocumentAttributes.DEPARTMENT.name(),
      getDocumentDepartment(documentDTO));

    if (generated) {
      versionDTO.getAttributes().put(DocumentAttributes.DOCUMENT_CREATION_TYPE.name(),
        DocumentCreationType.GENERATED.toString());
    } else {
      versionDTO.getAttributes().put(DocumentAttributes.DOCUMENT_CREATION_TYPE.name(),
        DocumentCreationType.UPLOADED.toString());
    }

    // Mark older versions as inactive
    for (VersionDTO verDTO : versionService.getFileVersions(docLatestVersion.getNodeId())) {
      verDTO.getAttributes().put(DocumentAttributes.ACTIVE.name(), Boolean.FALSE.toString());
      versionService.updateAttributes(docLatestVersion.getNodeId(), verDTO.getAttributes(),
        verDTO.getLastModifiedBy(), null);
    }

    return versionService.createVersion(docLatestVersion.getNodeId(), versionDTO,
      documentDTO.getFilename(), templateByteArray, getUserFullName(), null);
  }

  /**
   * Creates the versioned document.
   *
   * @param documentDTO the document DTO
   * @param templateByteArray the template byte array
   * @param folderId the folder id
   * @param submittentId the submittent id
   * @param companyId the company id
   * @param generated the generated
   * @param showInCompany the show in company
   * @return the id of the version
   */
  private String createNewVersionedDocument(DocumentDTO documentDTO, byte[] templateByteArray,
    String folderId, String submittentId, String companyId, Boolean generated,
    Boolean showInCompany) {

    LOGGER.log(Level.CONFIG,
      "Executing method createNewVersionedDocument, Parameters: documentDTO: {0}, "
        + "templateByteArray: {1}, folderId: {2}, submittentId: {3}, companyId: {4}, "
        + "generated: {5}, showInCompany: {6}",
      new Object[]{documentDTO, templateByteArray, folderId, submittentId, companyId, generated,
        showInCompany});

    String groupName = getGroupName(getUser());
    FileDTO file = new FileDTO();
    file.setCreatedOn(new Date().getTime());
    file.setParentId(folderId);
    file.setName(documentDTO.getFilename());
    file.setAttributes(new HashMap<String, String>());
    file.getAttributes().put(DocumentAttributes.SHOW_IN_COMPANY.name(), showInCompany.toString());
    file.getAttributes().put(DocumentAttributes.TEMPLATE_ID.name(), documentDTO.getTemplateId());
    file.getAttributes().put(DocumentAttributes.TENANT_ID.name(), documentDTO.getTenantId());
    if (documentDTO.getLegalHearingType() != null) {
      file.getAttributes().put(DocumentAttributes.ADDITIONAL_INFO.name(),
        documentDTO.getLegalHearingType());
    }
    if (documentDTO.getNachtragId() != null) {
      file.getAttributes().put(DocumentAttributes.ADDITIONAL_INFO.name(),
        documentDTO.getNachtragId());
    }
    if (submittentId != null) {
      file.getAttributes().put(DocumentAttributes.TENDER_ID.name(), submittentId);
    }
    if (companyId != null) {
      file.getAttributes().put(DocumentAttributes.COMPANY_ID.name(), companyId);
    }
    // if the document can be generated only by Admin, then it is set to Private
    if (documentDTO.getIsAdminRightsOnly() != null && documentDTO.getIsAdminRightsOnly()) {
      file.getAttributes().put(DocumentAttributes.PRIVATE_DOCUMENT.name(), Boolean.TRUE.toString());
      file.getAttributes().put(DocumentAttributes.PRIVATE_GROUP.name(), groupName);
    }

    VersionDTO versionDTO = new VersionDTO();
    versionDTO.setCreatedOn(new Date().getTime());
    versionDTO.setAttributes(new HashMap<String, String>());
    versionDTO.setName("1");
    versionDTO.getAttributes().put(DocumentAttributes.VERSION.name(), "1");
    versionDTO.getAttributes().put(DocumentAttributes.ACTIVE.name(), Boolean.TRUE.toString());
    versionDTO.getAttributes().put(DocumentAttributes.DOCUMENT_CREATION_TYPE.name(),
      (generated ? DocumentCreationType.GENERATED.toString()
        : DocumentCreationType.UPLOADED.toString()));
    versionDTO.getAttributes().put(DocumentAttributes.TITLE.name(), documentDTO.getTitle());
    versionDTO.getAttributes().put(DocumentAttributes.CREATORS_GROUP.name(), groupName);
    versionDTO.getAttributes().put(DocumentAttributes.IS_PROJECT_DOCUMENT.name(),
      String.valueOf(documentDTO.isProjectDocument()));
    versionDTO.getAttributes().put(DocumentAttributes.DEPARTMENT.name(),
      getDocumentDepartment(documentDTO));

    CreateFileAndVersionStatusDTO createFileAndVersionStatusDTO = documentService
      .createFileAndVersion(file, versionDTO, templateByteArray, getUserFullName(), null);

    return createFileAndVersionStatusDTO.getVersionID();
  }

  /**
   * Creates the non versioned document.
   *
   * @param documentDTO the document DTO
   * @param templateByteArray the template byte array
   * @param folderId the folder id
   * @param submittentId the submittent id
   * @param companyId the company id
   * @param showInCompany the show in company
   * @param generated the generated
   * @return the version id
   */
  private String createNewNonVersionedDocument(DocumentDTO documentDTO, byte[] templateByteArray,
    String folderId, String submittentId, String companyId, Boolean showInCompany,
    Boolean generated) {

    LOGGER.log(Level.CONFIG,
      "Executing method createNewNonVersionedDocument, Parameters: documentDTO: {0}, "
        + "templateByteArray: {1}, folderId: {2}, submittentId: {3}, companyId: {4}, "
        + "showInCompany: {5}, generated: {6}",
      new Object[]{documentDTO, templateByteArray, folderId, submittentId, companyId,
        showInCompany, generated});

    String groupName = getGroupName(getUser());
    FileDTO file = new FileDTO();
    file.setCreatedOn(new Date().getTime());
    file.setParentId(folderId);
    file.setName(documentDTO.getFilename());
    file.setAttributes(new HashMap<String, String>());
    file.getAttributes().put(DocumentAttributes.SHOW_IN_COMPANY.name(), showInCompany.toString());
    if (documentDTO.getTemplateId() != null) {
      file.getAttributes().put(DocumentAttributes.TEMPLATE_ID.name(), documentDTO.getTemplateId());
    }
    if (documentDTO.getTenantId() != null) {
      file.getAttributes().put(DocumentAttributes.TENANT_ID.name(), documentDTO.getTenantId());
    }
    if (submittentId != null) {
      file.getAttributes().put(DocumentAttributes.TENDER_ID.name(), submittentId);
    }
    if (companyId != null) {
      file.getAttributes().put(DocumentAttributes.COMPANY_ID.name(), companyId);
    }
    if (documentDTO.getLegalHearingType() != null) {
      file.getAttributes().put(DocumentAttributes.ADDITIONAL_INFO.name(),
        documentDTO.getLegalHearingType());
    }
    if (documentDTO.getNachtragId() != null) {
      file.getAttributes().put(DocumentAttributes.ADDITIONAL_INFO.name(),
        documentDTO.getNachtragId());
    }
    // if the document can be generated only by Admin, then it is set to Private
    if (documentDTO.getIsAdminRightsOnly() != null && documentDTO.getIsAdminRightsOnly()) {
      file.getAttributes().put(DocumentAttributes.PRIVATE_DOCUMENT.name(), Boolean.TRUE.toString());
      file.getAttributes().put(DocumentAttributes.PRIVATE_GROUP.name(), groupName);
    }

    VersionDTO versionDTO = new VersionDTO();
    versionDTO.setCreatedOn(new Date().getTime());
    versionDTO.setAttributes(new HashMap<String, String>());
    versionDTO.setName("1");
    versionDTO.getAttributes().put(DocumentAttributes.VERSION.name(), "1");
    versionDTO.getAttributes().put(DocumentAttributes.ACTIVE.name(), Boolean.TRUE.toString());
    if (generated) {
      versionDTO.getAttributes().put(DocumentAttributes.DOCUMENT_CREATION_TYPE.name(),
        DocumentCreationType.GENERATED.toString());
    } else {
      versionDTO.getAttributes().put(DocumentAttributes.DOCUMENT_CREATION_TYPE.name(),
        DocumentCreationType.UPLOADED.toString());
    }
    versionDTO.getAttributes().put(DocumentAttributes.TITLE.name(), documentDTO.getTitle());
    versionDTO.getAttributes().put(DocumentAttributes.CREATORS_GROUP.name(), groupName);
    versionDTO.getAttributes().put(DocumentAttributes.IS_PROJECT_DOCUMENT.name(),
      String.valueOf(documentDTO.isProjectDocument()));
    versionDTO.getAttributes().put(DocumentAttributes.DEPARTMENT.name(),
      getDocumentDepartment(documentDTO));

    CreateFileAndVersionStatusDTO fileAndVersion = documentService.createFileAndVersion(file,
      versionDTO, templateByteArray, getUserFullName(), null);

    return fileAndVersion.getVersionID();
  }

  @Override
  public void uploadDocument(String folderId, String versionId, String uploadedFileId,
    Boolean replaceDocument, Boolean isProjectDocument, Set<ValidationError> errors) {

    LOGGER.log(Level.CONFIG,
      "Executing method uploadDocument, Parameters: folderId: {0}, "
        + "versionId: {1}, uploadedFileId: {2}, replaceDocument: {3}, isProjectDocument: {4}, "
        + "errors: {5}",
      new Object[]{folderId, versionId, uploadedFileId, replaceDocument, isProjectDocument,
        errors});

    // Retrieve file from database
    FileGetResponse fileGetResponse = fileUpload.getByID(uploadedFileId);
    byte[] uploadedDocument = fileGetResponse.getFile().getFileData();

    // Version is null if a new file is uploaded through the multiple upload function. In this case
    // the file is uploaded without versioning.
    if (versionId == null) {
      String title = FilenameUtils.removeExtension(fileGetResponse.getFile().getFilename());
      // the filename must be <= 100 chars
      if (title.length() > 100) {
        // pass the error message in the front end
        errors.add(new ValidationError("titleTooBig",
          ValidationMessages.TITLE_DOC_ERROR_MESSAGE));
      } else {
        DocumentDTO documentDTO = new DocumentDTO();
        documentDTO.setTitle(title);
        documentDTO.setFilename(fileGetResponse.getFile().getFilename());
        documentDTO.setProjectDocument(isProjectDocument);
        documentDTO.setFolderId(folderId);
        documentDTO.setTenantId(usersService.getUserById(getUserId()).getTenant().getId());

        FolderDTO persitedFolderDTO = documentService.getFolderByID(folderId, true, false);

        if (persitedFolderDTO == null) {
          FolderDTO folder = new FolderDTO();
          folder.setId(documentDTO.getFolderId());
          folder.setCreatedOn(new Date().getTime());

          folderId = documentService.createFolder(folder, getUserFullName(), null);
        }

        createNewNonVersionedDocument(documentDTO, uploadedDocument, folderId, null, folderId,
          !isProjectDocument, false);
      }
    } else {
      // Documents that are upload though an already existing file in order to replace or create a
      // new version of this file.
      VersionDTO docLatestVersion = versionService.getVersionById(versionId);
      docLatestVersion.getAttributes().put(DocumentAttributes.DOCUMENT_CREATION_TYPE.name(),
        DocumentCreationType.UPLOADED.toString());

      // Uploaded file must have the same filename and extention as the existing one
      if (FilenameUtils.getExtension(fileGetResponse.getFile().getFilename())
        .equals(FilenameUtils.getExtension(docLatestVersion.getFilename()))) {
        if (fileGetResponse.getFile().getFilename().equals(docLatestVersion.getFilename())) {
          if (replaceDocument) {

            // Replace existing versioned document
            versionService.updateVersion(docLatestVersion.getNodeId(), docLatestVersion,
              uploadedDocument, getUserFullName(), true, null);
            versionService.replaceVersionContent(versionId, uploadedDocument);
            addAuditLog(documentService.getNodeByID(docLatestVersion.getNodeId()));
          } else {

            DocumentDTO documentDTO = new DocumentDTO();
            documentDTO
              .setTitle(FilenameUtils.removeExtension(fileGetResponse.getFile().getFilename()));
            documentDTO.setFilename(fileGetResponse.getFile().getFilename());
            documentDTO.setProjectDocument(isProjectDocument);

            documentDTO.setFolderId(
              documentService.getNodeByID(docLatestVersion.getNodeId()).getParentId());
            createVersionedDocument(documentDTO, uploadedDocument, docLatestVersion, false);
            addAuditLog(documentService.getNodeByID(docLatestVersion.getNodeId()));
          }
        } else {
          errors.add(new ValidationError(null, ValidationMessages.FILENAMES_NOT_MATCH));
        }
      } else {
        errors.add(new ValidationError(null, ValidationMessages.FILE_TYPES_NOT_MATCH));
      }
    }
  }

  /**
   * Adds the audit log.
   *
   * @param node the node
   */
  private void addAuditLog(NodeDTO node) {

    LOGGER.log(Level.CONFIG,
      "Executing method addAuditLog, Parameters: node: {0}",
      node);

    Map<String, MasterListValueHistoryDTO> activeSD = new HashMap<>();
    activeSD = cacheBean.getActiveSD().row(CategorySD.TEMPLATE_TYPE.getValue());
    if (node.getAttributes().get(DocumentAttributes.TEMPLATE_ID.name()) != null) {
      MasterListValueHistoryDTO m =
        activeSD.get(node.getAttributes().get(DocumentAttributes.TEMPLATE_ID.name()));

      StringBuilder additionalInfo = new StringBuilder();

      if (!m.getShortCode().equals(Template.NACHWEISBRIEF_FT)) {
        SubmissionEntity submission = em.find(SubmissionEntity.class, node.getParentId());

        MasterListValueHistoryEntity objectEntity = new JPAQueryFactory(em)
          .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
          .where(qMasterListValueHistoryEntity.masterListValueId
            .eq(submission.getProject().getObjectName())
            .and(qMasterListValueHistoryEntity.toDate.isNull()))
          .fetchOne();

        additionalInfo =
          new StringBuilder(submission.getProject().getProjectName()).append("[#]")
            .append(objectEntity.getValue1()).append("[#]").append("[#]")
            .append(getUser().getAttributeData(LookupValues.USER_ATTRIBUTES.TENANT.getValue()))
            .append("[#]");
      }

      switch (m.getShortCode()) {
        case Template.RECHTLICHES_GEHOR:
          auditLog(AuditLevel.PROJECT_LEVEL.name(), AuditEvent.CREATE.name(),
            AuditMessages.RECHTLICHES_GEHOR_DOC_UPLOADED.name(), node.getParentId(),
            additionalInfo.toString());
          break;
        case Template.AUSSCHLUSS:
        case Template.VERFUGUNGEN:
        case Template.VERFUGUNGEN_DL_WETTBEWERB:
          auditLog(AuditLevel.PROJECT_LEVEL.name(), AuditEvent.CREATE.name(),
            AuditMessages.VERFUGUNGEN_DOCS_UPLOADED.name(), node.getParentId(),
            additionalInfo.toString());
          break;
        case Template.SELEKTIV_1_STUFE:
          auditLog(AuditLevel.PROJECT_LEVEL.name(), AuditEvent.CREATE.name(),
            AuditMessages.SELEKTIV_1_STUFE_DOC_UPLOADED.name(), node.getParentId(),
            additionalInfo.toString());
          break;
        case Template.VERFAHRENSABBRUCH:
          auditLog(AuditLevel.PROJECT_LEVEL.name(), AuditEvent.CREATE.name(),
            AuditMessages.VERFAHRENSABBRUCH_DOC_UPLOADED.name(), node.getParentId(),
            additionalInfo.toString());
          break;
        case Template.NACHWEISBRIEF_FT:
          CompanyEntity companyEntity = em.find(CompanyEntity.class, node.getParentId());
          StringBuilder additionalInfoCompany = new StringBuilder(companyEntity.getCompanyName())
            .append("[#]")
            .append(companyEntity.getProofStatusFabe()).append("[#]");

          auditLog(AuditLevel.COMPANY_LEVEL.name(), AuditEvent.CREATE.name(),
            AuditMessages.NACHWEISBRIEF_DOC_UPLOADED.name(), node.getParentId(),
            additionalInfoCompany.toString());
          break;
        case Template.NACHWEISBRIEF_PT:
        case Template.NACHWEISBRIEF_SUB:
          auditLog(AuditLevel.PROJECT_LEVEL.name(), AuditEvent.CREATE.name(),
            AuditMessages.NACHWEISBRIEF_DOC_UPLOADED.name(), node.getParentId(),
            additionalInfo.toString());
          break;
        default:
          break;
      }
    }
  }

  @Override
  public void deleteDocument(String versionId, String reason) {

    LOGGER.log(Level.CONFIG,
      "Executing method deleteDocument, Parameters: versionId: {0}, "
        + "reason: {1}",
      new Object[]{versionId, reason});

    try {
      VersionDTO versionDTO = versionService.getVersionById(versionId);
      NodeDTO node = documentService.getNodeByID(versionDTO.getNodeId());
      if (Boolean.parseBoolean(versionDTO.getAttributes().get(DocumentAttributes.ACTIVE.name()))
        || versionDTO.getAttributes().get(DocumentAttributes.DOCUMENT_CREATION_TYPE.name())
        .equals(DocumentCreationType.GENERATED.toString())) {

        versionService.deleteVersion(versionId, null);
        VersionDTO latestVersionDTO = versionService.getFileLatestVersion(versionDTO.getNodeId());

        if (latestVersionDTO == null) {
          documentService.deleteFile(versionDTO.getNodeId(), null);
        } else {
          latestVersionDTO.getAttributes().put(DocumentAttributes.ACTIVE.name(),
            Boolean.TRUE.toString());
          versionService.updateAttributes(latestVersionDTO.getNodeId(),
            latestVersionDTO.getAttributes(), getUserFullName(), null);
        }

        // delete also the entry in SUB_DOCUMENT_DEADLINE table, if exists
        DocumentDeadlineEntity documentDeadlineEntity =
          new JPAQueryFactory(em).select(qDocumentDeadlineEntity).from(qDocumentDeadlineEntity)
            .where(qDocumentDeadlineEntity.versionId.eq(versionId)).fetchOne();
        if (documentDeadlineEntity != null) {
          em.remove(documentDeadlineEntity);
        }

        StringBuilder additionalInfo = null;
        SubmissionEntity submissionEntity = em.find(SubmissionEntity.class, node.getParentId());
        if (submissionEntity != null) {
          MasterListValueHistoryEntity objectEntity = new JPAQueryFactory(em)
            .select(qMasterListValueHistoryEntity).from(qMasterListValueHistoryEntity)
            .where(qMasterListValueHistoryEntity.masterListValueId
              .eq(submissionEntity.getProject().getObjectName())
              .and(qMasterListValueHistoryEntity.toDate.isNull()))
            .fetchOne();

          additionalInfo = new StringBuilder(submissionEntity.getProject().getProjectName())
            .append("[#]").append(objectEntity.getValue1()).append("[#]").append("[#]")
            .append(getUser().getAttributeData(LookupValues.USER_ATTRIBUTES.TENANT.getValue()))
            .append("[#]").append(versionDTO.getAttributes().get(DocumentAttributes.TITLE.name()));
          audit.createLogAudit(AuditLevel.PROJECT_LEVEL.name(), AuditEvent.DELETE.name(),
            AuditGroupName.DOCUMENT.name(), AuditMessages.DOCUMENT_DELETED.name(),
            getUser().getId(), node.getParentId(), reason, additionalInfo.toString(),
            LookupValues.EXTERNAL_LOG);
        } else {

          CompanyEntity companyEntity = em.find(CompanyEntity.class, node.getParentId());
          additionalInfo = new StringBuilder(companyEntity.getCompanyName()).append("[#]")
            .append(companyEntity.getProofStatusFabe()).append("[#]")
            .append(versionDTO.getAttributes().get(DocumentAttributes.TITLE.name()));

          audit.createLogAudit(AuditLevel.COMPANY_LEVEL.name(), AuditEvent.DELETE.name(),
            AuditGroupName.DOCUMENT.name(), AuditMessages.DOCUMENT_DELETED.name(),
            getUser().getId(), companyEntity.getId(), reason, additionalInfo.toString(),
            LookupValues.EXTERNAL_LOG);
        }
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Unable to delete document: " + e);
    }
  }

  @Override
  public byte[] downloadDocument(String nodeId, String versionName) {

    LOGGER.log(Level.CONFIG,
      "Executing method downloadDocument, Parameters: nodeId: {0}, "
        + "versionName: {1}",
      new Object[]{nodeId, versionName});

    return versionService.getBinContent(nodeId, versionName);
  }

  @Override
  public VersionDTO getDocLatestversion(DocumentDTO documentDTO, String submittentId,
    String companyId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getDocLatestversion, Parameters: documentDTO: {0}, "
        + "submittentId: {1}, companyId: {2}",
      new Object[]{documentDTO, submittentId, companyId});

    // Retrieve latest version of node with the given template of a specific folder with the given
    // attributes
    HashMap<String, String> attributesMap = new HashMap<>();
    attributesMap.clear();
    attributesMap.put(DocumentAttributes.TEMPLATE_ID.name(), documentDTO.getTemplateId());
    attributesMap.put(DocumentAttributes.TENANT_ID.name(), documentDTO.getTenantId());
    if (submittentId != null) {
      attributesMap.put(DocumentAttributes.TENDER_ID.name(), submittentId);
    }
    if (companyId != null) {
      attributesMap.put(DocumentAttributes.COMPANY_ID.name(), companyId);
    }
    if (documentDTO.getLegalHearingType() != null) {
      attributesMap.put(DocumentAttributes.ADDITIONAL_INFO.name(),
        documentDTO.getLegalHearingType());
    }
    if (documentDTO.getNachtragId() != null) {
      attributesMap.put(DocumentAttributes.ADDITIONAL_INFO.name(), documentDTO.getNachtragId());
    }
    List<NodeDTO> peristedNodes =
      documentService.getNodeByAttributes(documentDTO.getFolderId(), attributesMap);

    if (!peristedNodes.isEmpty()) {
      return versionService.getFileLatestVersion(peristedNodes.get(0).getId());
    }

    return null;
  }


  @Override
  public VersionDTO getVersionById(String versionId) {

    LOGGER.log(Level.CONFIG,
      "Executing method getVersionById, Parameters: versionId: {0}",
      versionId);

    return versionService.getVersionById(versionId);
  }

  /**
   * This function generates docs according to submittents (Nachweise anfordern functionality).
   *
   * @param documentDTO the document DTO
   * @return the list
   */
  private List<String> generateTendererProofDocuments(DocumentDTO documentDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method generateTendererProofDocuments, Parameters: documentDTO: {0}",
      documentDTO);

    HashMap<String, String> attributesMap = new HashMap<>();
    HashMap<String, String> placeholders = new HashMap<>();
    InputStream inputStream = null;
    MasterListValueHistoryEntity template = new MasterListValueHistoryEntity();
    List<NodeDTO> templateList = new ArrayList<>();
    List<SubmittentOfferDTO> offers = new ArrayList<>();
    List<String> createdDocumentIds = new ArrayList<>();

    /** Get from User the Tenant */
    TenantDTO tenant = sDTenantService
      .getTenantById(getUser().getAttributeData(USER_ATTRIBUTES.TENANT.getValue()));

    /** Get logo */
    byte[] logo = getLogo(documentDTO.getFolderId(), placeholders);
    /** Get logo width per tenant */
    long logoWidth = getLogoWidthForTenant(tenant);

    SubmissionDTO submission = submissionService.getSubmissionById(documentDTO.getFolderId());
    submission.setCurrentState(TenderStatus
      .fromValue(submissionService.getCurrentStatusOfSubmission(documentDTO.getFolderId())));
    if (submission.getProcess().equals(Process.SELECTIVE)
      && !compareCurrentVsSpecificStatus(submission.getCurrentState(),
      TenderStatus.SUITABILITY_AUDIT_COMPLETED_S)) {
      offers = submissionService.getApplicantsBySubmission(documentDTO.getFolderId());
    } else {
      offers = submissionService.getCompaniesBySubmission(documentDTO.getFolderId());
    }

    if (!submission.getProcess().equals(Process.NEGOTIATED_PROCEDURE)
      && !submission.getProcess().equals(Process.NEGOTIATED_PROCEDURE_WITH_COMPETITION)) {
      List<String> paragraphList = new ArrayList<>();
      paragraphList.add(DocumentPlaceholders.F_GERMAN_PROOFS.getValue());
      QSubmittentEntity qSubmittentEntity = QSubmittentEntity.submittentEntity;
      QCompanyEntity qCompanyEntitySubcontractors = new QCompanyEntity("subcontractors");
      QCompanyEntity qCompanyEntityjointVentures = new QCompanyEntity("jointVentures");

      List<String> submittentIds = new ArrayList<>();
      for (SubmittentOfferDTO offer : offers) {
        if (offer.getOffer().getIsEmptyOffer() == null || !offer.getOffer().getIsEmptyOffer()) {
          // Only add submittent id if offer is not empty.
          submittentIds.add(offer.getSubmittent().getId());
        }
      }

      List<SubmittentEntity> submittents = new JPAQueryFactory(em).selectFrom(qSubmittentEntity)
        .leftJoin(qSubmittentEntity.jointVentures, qCompanyEntityjointVentures)
        .leftJoin(qSubmittentEntity.subcontractors, qCompanyEntitySubcontractors)
        .where(qSubmittentEntity.id.in(submittentIds))
        .groupBy(qSubmittentEntity.companyId, qCompanyEntitySubcontractors,
          qCompanyEntityjointVentures)
        .orderBy(qSubmittentEntity.companyId.id.asc()).fetch();
      Set<SubmittentEntity> submittentSet = new HashSet<>(submittents);
      List<SubmittentDTO> submittentDTOs =
        SubmittentDTOMapper.INSTANCE.toSubmittentDTO(Lists.newArrayList(submittentSet));

      for (SubmittentDTO offer : submittentDTOs) {

        Date deadline;
        if (submission.getProcess().equals(Process.SELECTIVE)) {
          deadline = submission.getFirstDeadline();
        } else {
          deadline = submission.getSecondDeadline();
        }

        List<CompanyProofDTO> companyProofDTOs =
          companyService.getProofByCompanyId(offer.getCompanyId().getId());

        // Read specific template for proof request documents
        template = new JPAQueryFactory(em).selectFrom(qMasterListValueHistoryEntity)
          .where(qMasterListValueHistoryEntity.shortCode.eq(Template.NACHWEISBRIEF_PT)
            .and(qMasterListValueHistoryEntity.tenant.id
              .eq(usersService.getUserById(getUser().getId()).getTenant().getId())))
          .fetchOne();

        // In the field VALUE3 of the table MasterListValueHistory is stored whether
        // the document can be created only by Admin or not
        if (template.getValue3() != null) {
          documentDTO.setIsAdminRightsOnly(
            template.getValue3().equals(LookupValues.ZERO_STRING) ? Boolean.FALSE : Boolean.TRUE);
        }

        // Retrieve template from DB
        attributesMap.put(DocumentAttributes.TEMPLATE_ID.name(),
          template.getMasterListValueId().getId());
        attributesMap.put(DocumentAttributes.TENANT_ID.name(), template.getTenant().getId());
        templateList =
          documentService.getNodeByAttributes(LookupValues.TEMPLATE_FOLDER_ID, attributesMap);
        inputStream =
          new ByteArrayInputStream(versionService.getBinContent(templateList.get(0).getId()));

        // Set template values to documentDTO in order to generate document with these attributes.
        documentDTO.setTemplateId(template.getMasterListValueId().getId());
        documentDTO.setTenantId(template.getTenant().getId());
        documentDTO.setFilename(template.getValue1() + LookupValues.UNDER_SCORE
          + offer.getCompanyId().getCompanyName() + TemplateConstants.DOCX_FILE_EXTENSION);
        // Replace Reference placeholders.
        initReferenceData(template.getShortCode(), placeholders, documentDTO);
        // Create Nachweisbrief document when company has invalid proofs.
        if (createProofDocument(deadline, companyProofDTOs)) {

          templateBean.replaceSubmissionPlaceholders(placeholders, submission, offers);

          ruleService.runProjectTemplateRules(null, offer.getCompanyId(),
            documentDTO, null, placeholders, Template.NACHWEISBRIEF_PT);

          // Update company entity with the new document values.
          updateCompanyDocumentValues(offer.getCompanyId().getId(), documentDTO,
            Template.NACHWEISBRIEF_PT);

          addProofPlaceholder(companyProofDTOs, deadline, placeholders);

          // Set main company to empty as the main company of the current submittent does not
          // exists.
          placeholders.put(DocumentPlaceholders.S_MAIN_COMPANY.getValue(),
            TemplateConstants.EMPTY_STRING);

          String versionId = createDocument(documentDTO,
            templateService.replacePlaceholdersWordDoc(inputStream,
              SubmissConverter.replaceSpecialCharactersInPlaceholders(placeholders), logo,
              logoWidth, paragraphList, setParagraphPosition(Template.NACHWEISBRIEF_PT))
              .toByteArray(),
            offer.getId(), offer.getCompanyId().getId(), true);
          createdDocumentIds.add(versionId);

          // at this point we need to inform the database about the deadline of the Nachweisbrief,
          // because we need this information in Verfügungsinformationen
          DocumentDeadlineEntity documentDeadlineEntity = new DocumentDeadlineEntity();
          documentDeadlineEntity.setVersionId(versionId);
          documentDeadlineEntity.setDeadline(documentDTO.getSubmitDate());
          em.persist(documentDeadlineEntity);
        }

        // Create document for joint ventures
        for (CompanyDTO jointVenture : offer.getJointVentures()) {
          documentDTO.setFilename(template.getValue1() + LookupValues.UNDER_SCORE
            + jointVenture.getCompanyName() + TemplateConstants.DOCX_FILE_EXTENSION);
          List<CompanyProofDTO> jointVentureProofDTOs =
            companyService.getProofByCompanyId(jointVenture.getId());

          // Create Nachweisbrief document when company has invalid proofs.
          if (createProofDocument(deadline, jointVentureProofDTOs)) {

            // Check if the s_obj_pro_worktype_descr placeholder exists. If not, add it.
            if (placeholders.get(S_OBJ_PRO_WORKTYPE_DESCR) == null) {
              ruleService.replaceSubmissionPlaceholders(submission, placeholders);
            }

            addProofPlaceholder(jointVentureProofDTOs, deadline, placeholders);

            // Update the main company of every joint venture.
            placeholders.put(DocumentPlaceholders.S_MAIN_COMPANY.getValue(),
              TemplateConstants.COPY_TO + offer.getCompanyId().getCompanyName()
                + ", " + offer.getCompanyId().getLocation());

            ruleService.runProjectTemplateRules(null, jointVenture, documentDTO, null, placeholders,
              Template.NACHWEISBRIEF_PT);

            inputStream =
              new ByteArrayInputStream(versionService.getBinContent(templateList.get(0).getId()));

            createdDocumentIds.add(createDocument(documentDTO,
              templateService.replacePlaceholdersWordDoc(inputStream,
                SubmissConverter.replaceSpecialCharactersInPlaceholders(placeholders),
                logo, logoWidth, paragraphList,
                setParagraphPosition(Template.NACHWEISBRIEF_PT)).toByteArray(),
              offer.getId(), jointVenture.getId(), true));

            // Update company entity (joint ventures) with the new document values.
            updateCompanyDocumentValues(jointVenture.getId(), documentDTO,
              Template.NACHWEISBRIEF_PT);
          }

        }
        // Create document for subcontractors
        for (CompanyDTO subcontractor : offer.getSubcontractors()) {
          documentDTO.setFilename(template.getValue1() + LookupValues.UNDER_SCORE
            + subcontractor.getCompanyName() + TemplateConstants.DOCX_FILE_EXTENSION);
          List<CompanyProofDTO> subcontractorProofDTOs =
            companyService.getProofByCompanyId(subcontractor.getId());

          // Create Nachweisbrief document when company has invalid proofs.
          if (createProofDocument(deadline, subcontractorProofDTOs)) {

            // Check if the s_obj_pro_worktype_descr placeholder exists. If not, add it.
            if (placeholders.get(S_OBJ_PRO_WORKTYPE_DESCR) == null) {
              ruleService.replaceSubmissionPlaceholders(submission, placeholders);
            }

            // Retrieve different template for subcontractors
            MasterListValueHistoryEntity subcontractorsTemplate =
              new JPAQueryFactory(em).selectFrom(qMasterListValueHistoryEntity)
                .where(qMasterListValueHistoryEntity.shortCode.eq(Template.NACHWEISBRIEF_SUB)
                  .and(qMasterListValueHistoryEntity.tenant.id
                    .eq(usersService.getUserById(getUser().getId()).getTenant().getId())))
                .fetchOne();

            // In the field VALUE3 of the table MasterListValueHistory is stored whether
            // the document can be created only by Admin or not
            if (template.getValue3() != null) {
              documentDTO.setIsAdminRightsOnly(
                template.getValue3().equals(LookupValues.ZERO_STRING) ? Boolean.FALSE
                  : Boolean.TRUE);
            }

            attributesMap.put(DocumentAttributes.TEMPLATE_ID.name(),
              subcontractorsTemplate.getMasterListValueId().getId());
            attributesMap.put(DocumentAttributes.TENANT_ID.name(),
              subcontractorsTemplate.getTenant().getId());

            documentDTO.setTemplateId(subcontractorsTemplate.getMasterListValueId().getId());
            List<NodeDTO> subcontractorsTemplates =
              documentService.getNodeByAttributes(LookupValues.TEMPLATE_FOLDER_ID, attributesMap);

            InputStream templateInputStream = new ByteArrayInputStream(
              versionService.getBinContent(subcontractorsTemplates.get(0).getId()));

            // Update the main company of every subcontractor.
            placeholders.put(DocumentPlaceholders.S_MAIN_COMPANY.getValue(),
              TemplateConstants.COPY_TO + offer.getCompanyId().getCompanyName()
                + ", " + offer.getCompanyId().getLocation());

            placeholders.put(DocumentPlaceholders.F_MAIN_COMPANY.getValue(),
              offer.getCompanyId().getCompanyName());
            addProofPlaceholder(subcontractorProofDTOs, deadline, placeholders);

            // Use the same rule with submittents and joint ventures template because it contains
            // the
            // same placeholders.
            ruleService.runProjectTemplateRules(null, subcontractor, documentDTO, null,
              placeholders, Template.NACHWEISBRIEF_PT);

            try {
              // Create document with different template with subcontractorss
              createdDocumentIds.add(createDocument(documentDTO,
                templateService.replacePlaceholdersWordDoc(templateInputStream,
                  SubmissConverter.replaceSpecialCharactersInPlaceholders(placeholders),
                  logo, logoWidth, paragraphList,
                  setParagraphPosition(Template.NACHWEISBRIEF_PT)).toByteArray(),
                offer.getId(), subcontractor.getId(), true));

              // Update company entity (subcontractors) with the new document values.
              updateCompanyDocumentValues(subcontractor.getId(), documentDTO,
                Template.NACHWEISBRIEF_PT);

            } finally {
              try {
                templateInputStream.close();
              } catch (IOException e) {
                LOGGER.log(Level.SEVERE, TemplateConstants.INPUTSTREAM_ERROR + e);
              }
            }
          }
        }
      }

      StringBuilder additionalInfo = new StringBuilder(submission.getProject().getProjectName())
        .append("[#]").append(submission.getProject().getObjectName().getValue1()).append("[#]")
        .append(submission.getWorkType().getValue1() + submission.getWorkType().getValue2())
        .append("[#]")
        .append(getUser().getAttributeData(LookupValues.USER_ATTRIBUTES.TENANT.getValue()))
        .append("[#]");
      auditLog(AuditLevel.PROJECT_LEVEL.name(), AuditEvent.CREATE.name(),
        AuditMessages.GENERATE_PROOF_DOCUMENT.name(), submission.getId(),
        additionalInfo.toString());
    } else {
      createdDocumentIds.addAll(generateProofDocumentNegotiated(documentDTO, submission,
        placeholders, logo, logoWidth));
    }

    return createdDocumentIds;
  }

  /**
   * Sets the paragraph position.
   *
   * @param template the template
   * @return the integer
   */
  private Integer setParagraphPosition(String template) {

    LOGGER.log(Level.CONFIG,
      "Executing method setParagraphPosition, Parameters: template: {0}",
      template);

    if (sDTenantService
      .getTenantById(
        getUser().getAttribute(LookupValues.USER_ATTRIBUTES.TENANT.getValue()).getData())
      .getName().equals(DocumentProperties.TENANT_STADT_BERN.getValue())) {
      return 15;
    } else if (sDTenantService
      .getTenantById(
        getUser().getAttribute(LookupValues.USER_ATTRIBUTES.TENANT.getValue()).getData())
      .getName().equals(DocumentProperties.TENANT_EWB.getValue())) {
      if (template.equals(Template.NACHWEISBRIEF_FT)) {
        return 20;
      }
      if (template.equals(Template.NACHWEISBRIEF_PT)
        || template.equals(Template.NACHWEISBRIEF_SUB)) {
        return 25;
      }
    }
    return null;
  }

  /**
   * This method updates the CompanyEntity with the submitted values after creating a Certificate or
   * Proof Request document.
   *
   * @param companyId the company id
   * @param documentDTO the document DTO
   * @param documentShortCode the document short code
   */
  private void updateCompanyDocumentValues(String companyId, DocumentDTO documentDTO,
    String documentShortCode) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateCompanyDocumentValues, Parameters: companyId: {0}, "
        + "documentDTO: {1}, documentShortCode: {2}",
      new Object[]{companyId, documentDTO, documentShortCode});

    CompanyEntity company = em.find(CompanyEntity.class, companyId);
    if (documentShortCode.equals(Template.NACHWEISBRIEF_FT)
      || documentShortCode.equals(Template.NACHWEISBRIEF_PT)) {
      company.setProofDocModOn(new Date());
      company.setProofDocModBy(em.find(TenantEntity.class, documentDTO.getTenantId()));
      company.setProofDocSubmitDate(documentDTO.getSubmitDate());
    } else {
      company.setCertificateDate(new Date());
      company.setCertDocExpirationDate(documentDTO.getExpirationDate());
    }
    em.persist(company);
    em.flush();
  }

  /**
   * This method replace the proof placeholder for selected company.
   *
   * @param companyProofDTOs the company proof DT os
   * @param date the date
   * @param placeholders the placeholders
   */
  private void addProofPlaceholder(List<CompanyProofDTO> companyProofDTOs, Date date,
    HashMap<String, String> placeholders) {

    LOGGER.log(Level.CONFIG,
      "Executing method addProofPlaceholder, Parameters:  date: {0}",
      date);

    StringBuilder germanProofs = new StringBuilder();
    for (CompanyProofDTO companyProof : companyProofDTOs) {

      // Find proofs that are not valid.
      if (companyProof.getRequired() && companyProof.getProof().getValidityPeriod() != null) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, -companyProof.getProof().getValidityPeriod());
        // Check validity of required proofs.
        if (companyProof.getProofDate() == null
          || !companyProof.getProofDate().after(cal.getTime())) {
          germanProofs.append(companyProof.getProof().getDescription());
          germanProofs.append(TemplateConstants.NEW_LINE_STRING);
        }
      }
    }

    // Replace proof placeholder with the description of non valid proofs.
    if (germanProofs.length() > 1) {
      placeholders.put(DocumentPlaceholders.F_GERMAN_PROOFS.getValue(),
        germanProofs.substring(0, germanProofs.length() - 1));
    } else {
      placeholders.put(DocumentPlaceholders.F_GERMAN_PROOFS.getValue(),
        TemplateConstants.EMPTY_STRING);
    }
  }

  /**
   * Inits the reference data.
   *
   * @param templateShortCode the template short code
   * @param placeholders the placeholders
   * @param documentDTO the document DTO
   */
  private void initReferenceData(String templateShortCode, Map<String, String> placeholders,
    DocumentDTO documentDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method initReferenceData, Parameters: templateShortCode: {0}, "
        + "placeholders: {1}, documentDTO: {2}",
      new Object[]{templateShortCode, placeholders, documentDTO});

    switch (templateShortCode) {
      // Role reference
      case Template.BEKO_ANTRAG:
      case Template.BEKO_BESCHLUSS:
        setReferenceDepartmentPlacehoders(placeholders, documentDTO.getDepartment());
        break;
      case Template.NACHWEISBRIEF_FT:
      case Template.NACHWEISBRIEF_PT:
      case Template.RECHTLICHES_GEHOR:
      case Template.ZERTIFIKAT:
      case Template.FIRMENBLATT_ERWEITERT:
      case Template.VERTRAG_PLANERVERTRAG_SGB:
        // Bug 4046 and UC 177 - Department and Direktion reference data of certain department when
        // Tenant is Stadt Bern.
        if (usersService.getUserById(getUser().getId()).getTenant().getIsMain()) {
          DepartmentHistoryDTO department = DepartmentHistoryMapper.INSTANCE.toDepartmentHistoryDTO(
            new JPAQueryFactory(em).selectFrom(qDepartmentHistoryEntity)
              .where(qDepartmentHistoryEntity.shortCode
                .eq(LookupValues.STADT_BERN_MAIN_DEPARTMENT_SHORT_NAME)
                .and(qDepartmentHistoryEntity.toDate.isNull()))
              .fetchOne(),
            cacheBean.getActiveDirectorateHistorySD());
          templateBean.setUserAttributesPlaceholders(department, department.getDirectorate(),
            placeholders, null);

        } else {
          templateBean.setReferencePlacehoders(placeholders);
        }
        break;
      // Signature reference
      case Template.VERFAHRENSABBRUCH:
      case Template.AUSSCHLUSS:
      case Template.VERFUGUNGEN:
      case Template.SELEKTIV_1_STUFE:
      case Template.VERFUGUNGEN_DL_WETTBEWERB:
        setSignature(documentDTO, placeholders);
        break;
      default:
        LOGGER.log(Level.INFO, "Short code does not exists.");
    }
  }

  /**
   * Sets the reference department placehoders.
   *
   * @param placeholders the placeholders
   * @param department the department
   */
  private void setReferenceDepartmentPlacehoders(Map<String, String> placeholders,
    DepartmentHistoryDTO department) {

    LOGGER.log(Level.CONFIG,
      "Executing method setReferenceDepartmentPlacehoders, Parameters: placeholders: {0}, "
        + "department: {1}",
      new Object[]{placeholders, department});

    if (department != null) {
      templateBean.setUserAttributesPlaceholders(department, department.getDirectorate(),
        placeholders, null);
    }
  }

  @Override
  public boolean createProofDocument(Date deadline, List<CompanyProofDTO> companyProofDTOs) {

    LOGGER.log(Level.CONFIG,
      "Executing method createProofDocument, Parameters: deadline: {0}",
      deadline);

    boolean createDoc = false;
    for (CompanyProofDTO cProof : companyProofDTOs) {
      if (cProof.getRequired()) {
        ProofHistoryDTO proofHistory =
          cacheBean.getValueProof(cProof.getProof().getProofId().getId(), null);
        if ((proofHistory.getActive() != null && proofHistory.getActive().equals(1))
          && proofHistory.getValidityPeriod() != null) {
          Calendar cal = Calendar.getInstance();
          cal.setTime(deadline);
          cal.add(Calendar.MONTH, -proofHistory.getValidityPeriod());
          if (cProof.getProofDate() == null || !cProof.getProofDate().after(cal.getTime())) {
            createDoc = true;
            break;
          }
        }
      }
    }
    return createDoc;
  }

  @Override
  public byte[] downloadMultipleDocuments(List<String> versionIds) {

    LOGGER.log(Level.CONFIG,
      "Executing method downloadMultipleDocuments, Parameters: versionIds: {0}",
      versionIds);

    VersionDTO version = null;
    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    ZipOutputStream zipFile = new ZipOutputStream(outStream);

    try {

      int i = 0;
      for (String versionId : versionIds) {

        version = versionService.getVersionById(versionId);
        byte[] content = versionService.getBinContent(version.getNodeId(), version.getName());

        // Write binary content
        ZipEntry entry = new ZipEntry(++i + LookupValues.UNDER_SCORE + version.getFilename());
        zipFile.putNextEntry(entry);
        zipFile.write(content);

      }

    } catch (Exception e) {
      if (version != null) {
        LOGGER.log(Level.SEVERE, "Unable to add document with versionId to zip. Id: "
          + version.getId() + ", Exception: " + e);
      } else {
        LOGGER.log(Level.SEVERE,
          "Unable to create zip file for Ids: " + versionIds + ", Exception: " + e);
      }

    } finally {
      try {
        zipFile.close();
        outStream.close();
      } catch (IOException e) {
        LOGGER.log(Level.SEVERE, "Unable to close document zip stream " + e);
      }
    }

    return outStream.toByteArray();
  }

  @Override
  public void updateDocumentProperties(DocumentDTO document, Boolean privateDocumentSetChanged) {

    LOGGER.log(Level.CONFIG,
      "Executing method updateDocumentProperties, Parameters: document: {0}, "
        + "privateDocumentSetChanged: {1}",
      new Object[]{document, privateDocumentSetChanged});

    VersionDTO versionDTO = getVersionById(document.getId());
    if (versionDTO != null && versionDTO.getNodeId() != null
      && versionDTO.getAttributes() != null) {

      // Check for OptimisticLockException
      if (document.getLastModifiedOn().before(new Date(versionDTO.getLastModifiedOn()))) {
        throw new OptimisticLockException();
      }

      versionDTO.getAttributes().put(DocumentAttributes.TITLE.name(), document.getTitle());

      versionService.updateAttributes(versionDTO.getNodeId(), versionDTO.getAttributes(),
        getUserFullName(), null);

      // the private property is given to the node and not the version,
      // so that it applies to all versions
      NodeDTO nodeDTO = documentService.getNodeByID(versionDTO.getNodeId());
      if (nodeDTO != null && nodeDTO.getAttributes() != null) {
        nodeDTO.getAttributes().put(DocumentAttributes.PRIVATE_DOCUMENT.name(),
          document.isPrivateDocument().toString());
        // if a document is changed to private we have to store the group and the
        // departments
        // of the user that set the document to private
        if (document.isPrivateDocument() && privateDocumentSetChanged) {
          String groupName = getGroupName(getUser());
          nodeDTO.getAttributes().put(DocumentAttributes.PRIVATE_GROUP.name(), groupName);
          // we need the departments of the user that set the document to private
          // only in case he is not Admin
          if (!groupName.equals(Group.ADMIN.getValue())) {
            nodeDTO.getAttributes().put(DocumentAttributes.PRIVATE_DEPARTMENTS.name(),
              getUserDepartments());
          }
        }

        documentService.updateAttributes(versionDTO.getNodeId(), nodeDTO.getAttributes(),
          getUserFullName(), null);
      }
    }
  }

  /**
   * if document is generated in projectPart department value is the submission department else it's
   * the department of the user that generates the document.
   *
   * @param documentDTO the document DTO
   * @return the document department
   */
  private String getDocumentDepartment(DocumentDTO documentDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method getDocumentDepartment, Parameters: documentDTO: {0}",
      documentDTO);

    if (documentDTO.isProjectDocument()) {
      return submissionService.getSubmissionById(documentDTO.getFolderId()).getProject()
        .getDepartment().getDepartmentId().getId();
    } else {
      return getUserDepartments();
    }
  }

  /**
   * This function gets all the departments and map the departmentIds and departmentHistoryDTOS in
   * order to avoid multiple calls to the database.
   *
   * @return the department history DTO
   */
  private Map<String, DepartmentHistoryDTO> getDepartmentHistoryDTO() {

    LOGGER.log(Level.CONFIG, "Executing method getDepartmentHistoryDTO");

    HashMap<String, DepartmentHistoryDTO> departmentMap = new HashMap<>();
    for (DepartmentHistoryDTO department : sDDepartmentService.readAll()) {
      departmentMap.put(department.getDepartmentId().getId(), department);
    }
    return departmentMap;
  }

  @Override
  public List<DirectorateHistoryDTO> loadDocumentDirectorate(String documentId) {

    LOGGER.log(Level.CONFIG,
      "Executing method loadDocumentDirectorate, Parameters: documentId: {0}",
      documentId);

    VersionDTO versionDTO = getVersionById(documentId);
    if (versionDTO != null && versionDTO.getAttributes() != null
      && versionDTO.getAttributes().get(DocumentAttributes.IS_PROJECT_DOCUMENT.name()) != null) {
      // for project Documents document directorate is the directorate of the Project
      if (Boolean.parseBoolean(
        versionDTO.getAttributes().get(DocumentAttributes.IS_PROJECT_DOCUMENT.name()))) {
        List<DirectorateHistoryDTO> directorateHistoryDTOs = new ArrayList<>();
        String submissionId = documentService.getNodeByID(versionDTO.getNodeId()).getParentId();
        directorateHistoryDTOs.add(submissionService.getSubmissionById(submissionId).getProject()
          .getDepartment().getDirectorate());
        return directorateHistoryDTOs;
        // for company Documents document directorate is the directorate of the
        // Ersteller
      } else {
        // If Document is from company part then load Direktion if user is in the DIR group.
        if (versionDTO.getAttributes().get(DocumentAttributes.CREATORS_GROUP.name())
          .equals(Group.DIR.getValue())) {
          QDirectorateHistoryEntity directorateHistoryEntity =
            QDirectorateHistoryEntity.directorateHistoryEntity;
          // change departmentHistory table
          List<String> departmentIds = Arrays.asList(versionDTO.getAttributes()
            .get(DocumentAttributes.DEPARTMENT.name()).split(TemplateConstants.DOC_SPLIT_STRING));

          List<DirectorateHistoryEntity> directorateHistoryEntities = new JPAQueryFactory(em)
            .select(directorateHistoryEntity).from(directorateHistoryEntity)
            .where(directorateHistoryEntity.directorateId.id.in(
              new JPAQueryFactory(em).selectDistinct(qDepartmentHistoryEntity.directorateEnity.id)
                .from(qDepartmentHistoryEntity).where(qDepartmentHistoryEntity.departmentId.id
                .in(departmentIds).and(qDepartmentHistoryEntity.toDate.isNull()))
                .fetch())).fetch();
          return DirectorateHistoryMapper.INSTANCE
            .toDirectorateHistoryDTO(directorateHistoryEntities);
        } else {
          return Collections.emptyList();
        }

      }
    } else {
      return Collections.emptyList();
    }

  }

  /**
   * This method generates documents during the Negotiated Procedure process.
   *
   * @param documentDTO the document DTO
   * @param submission the submission
   * @param placeholders the placeholders
   * @param tenantLogo the tenant logo
   * @param logoWidth the logo width
   * @return the list
   */
  private List<String> generateProofDocumentNegotiated(DocumentDTO documentDTO,
    SubmissionDTO submission, HashMap<String, String> placeholders, byte[] tenantLogo,
    long logoWidth) {

    LOGGER.log(Level.CONFIG,
      "Executing method generateProofDocumentNegotiated, Parameters: documentDTO: {0}, "
        + "submission: {1}, placeholders: {2}, tenantLogo: {3}, " + "logoWidth: {4}",
      new Object[]{documentDTO, submission, placeholders, tenantLogo, logoWidth});

    QSubmittentEntity qSubmittentEntity = QSubmittentEntity.submittentEntity;
    HashMap<String, String> attributesMap = new HashMap<>();
    Date deadline = submission.getSecondDeadline();
    List<String> createdDocumentIds = new ArrayList<>();

    // Find the submittent ids for which the Nachweisbrief should be created.
    List<String> submittentIds =
      new JPAQueryFactory(em).select(qSubmittentEntity.id).from(qSubmittentEntity)
        .where(qSubmittentEntity.submissionId.id.eq(documentDTO.getFolderId())
          .and((qSubmittentEntity.proofDocPending.isNotNull()
            .and(qSubmittentEntity.proofDocPending.isTrue()))))
        .fetch();
    if (!submittentIds.isEmpty()) {
      MasterListValueHistoryEntity template =
        new JPAQueryFactory(em).selectFrom(qMasterListValueHistoryEntity)
          .where(qMasterListValueHistoryEntity.shortCode.eq(Template.NACHWEISBRIEF_PT)
            .and(qMasterListValueHistoryEntity.tenant.id
              .eq(usersService.getUserById(getUser().getId()).getTenant().getId())))
          .fetchOne();

      // In the field VALUE3 of the table MasterListValueHistory is stored whether
      // the document can be created only by Admin or not
      if (template.getValue3() != null) {
        documentDTO.setIsAdminRightsOnly(
          template.getValue3().equals(LookupValues.ZERO_STRING) ? Boolean.FALSE : Boolean.TRUE);
      }

      // Retrieve template from DB
      attributesMap.put(DocumentAttributes.TEMPLATE_ID.name(),
        template.getMasterListValueId().getId());
      attributesMap.put(DocumentAttributes.TENANT_ID.name(), template.getTenant().getId());
      List<NodeDTO> templateList =
        documentService.getNodeByAttributes(LookupValues.TEMPLATE_FOLDER_ID, attributesMap);

      // Set template values to documentDTO in order to generate document with these attributes.
      documentDTO.setTemplateId(template.getMasterListValueId().getId());
      documentDTO.setTenantId(template.getTenant().getId());
      // Replace Reference placeholders.
      initReferenceData(template.getShortCode(), placeholders, documentDTO);

      templateBean.replaceSubmissionPlaceholders(placeholders, submission, null);

      List<SubmittentDTO> submittentDTOs =
        submissionService.getSubmittentsForNegotiatedProcedure(submittentIds);
      // Variable to indicate whether at least one proof request task has been settled.
      boolean proofRequestTaskSettled = false;
      // Variable to indicate whether at least one proof document has been created.
      boolean proofDocumentCreated = false;
      for (SubmittentDTO submittentDTO : submittentDTOs) {

        // Make sure that the templateId in the documentDTO has not been changed from previous
        // iterations (specifically from calling the subcontractosProofDocuments function for the
        // creation of the subcontractor proof documents).
        if (!documentDTO.getTemplateId().equals(template.getMasterListValueId().getId())) {
          documentDTO.setTemplateId(template.getMasterListValueId().getId());
        }

        // Only create proof document if the submittent company proof is not provided.
        if (submittentDTO.getCompanyId().getIsProofProvided() == null
          || !submittentDTO.getCompanyId().getIsProofProvided()) {
          documentDTO.setFilename(template.getValue1() + LookupValues.UNDER_SCORE
            + submittentDTO.getCompanyId().getCompanyName()
            + TemplateConstants.DOCX_FILE_EXTENSION);
          // Set main company to empty as the main company of the current submittent does not
          // exist.
          placeholders.put(DocumentPlaceholders.S_MAIN_COMPANY.getValue(),
            TemplateConstants.EMPTY_STRING);
          createdDocumentIds
            .add(proofCompanyDocumentsNegotiatedProcess(submittentDTO, documentDTO, deadline,
              placeholders, templateList, submittentDTO.getCompanyId(), tenantLogo, logoWidth));
          proofDocumentCreated = true;
        }
        if (!proofRequestTaskSettled) {
          // If no task has been settled, get the proofRequestTaskSettled value from the
          // settleProofRequestTask function.
          proofRequestTaskSettled = settleProofRequestTask(submittentDTO.getSubmissionId().getId(),
            submittentDTO.getCompanyId().getId(), documentDTO.getSubmitDate());
        } else {
          // If at least one proof request task has been settled, just call the
          // settleProofRequestTask function without assigning its returned value.
          settleProofRequestTask(submittentDTO.getSubmissionId().getId(),
            submittentDTO.getCompanyId().getId(), documentDTO.getSubmitDate());
        }

        // Generate Nachweisbrief for joint ventures.
        for (CompanyDTO jointVenture : submittentDTO.getJointVentures()) {
          // Only create proof document if the joint venture proof is not provided.
          if (jointVenture.getIsProofProvided() == null || !jointVenture.getIsProofProvided()) {
            // Update the main company of every joint venture.
            documentDTO.setFilename(template.getValue1() + LookupValues.UNDER_SCORE
              + jointVenture.getCompanyName() + TemplateConstants.DOCX_FILE_EXTENSION);
            placeholders.put(DocumentPlaceholders.S_MAIN_COMPANY.getValue(),
              TemplateConstants.COPY_TO + submittentDTO.getCompanyId().getCompanyName() + ", "
                + submittentDTO.getCompanyId().getLocation());
            createdDocumentIds
              .add(proofCompanyDocumentsNegotiatedProcess(submittentDTO, documentDTO, deadline,
                placeholders, templateList, jointVenture, tenantLogo, logoWidth));
            proofDocumentCreated = true;
          }
        }
        // Create document for subcontractors.
        for (CompanyDTO subcontractor : submittentDTO.getSubcontractors()) {
          // Only create proof document if the subcontractor proof is not provided.
          if (subcontractor.getIsProofProvided() == null || !subcontractor.getIsProofProvided()) {
            documentDTO.setFilename(template.getValue1() + LookupValues.UNDER_SCORE
              + subcontractor.getCompanyName() + TemplateConstants.DOCX_FILE_EXTENSION);
            createdDocumentIds.add(subcontractosProofDocuments(submittentDTO, documentDTO, deadline,
              placeholders, subcontractor, tenantLogo, logoWidth));
            proofDocumentCreated = true;
          }
        }
      }
      // If at least one proof request task has been settled, create an audit log.
      if (proofRequestTaskSettled) {
        taskService.createLogForSettledProofRequestTasks(submission);
      }
      StringBuilder additionalInfo = new StringBuilder(submission.getProject().getProjectName())
        .append("[#]").append(submission.getProject().getObjectName().getValue1()).append("[#]")
        .append(submission.getWorkType().getValue1() + submission.getWorkType().getValue2())
        .append("[#]")
        .append(getUser().getAttributeData(LookupValues.USER_ATTRIBUTES.TENANT.getValue()))
        .append("[#]");

      // Generate log if at least one proof document has been created.
      if (proofDocumentCreated) {
        auditLog(AuditLevel.PROJECT_LEVEL.name(), AuditEvent.CREATE.name(),
          AuditMessages.GENERATE_PROOF_DOCUMENT.name(), submission.getId(),
          additionalInfo.toString());
      }
    }

    return createdDocumentIds;
  }

  /**
   * This method generated Nachweisbrief documents for subcontractors in Negotiated Procedure
   * process.
   *
   * @param submittentDTO the submittent DTO
   * @param documentDTO the document DTO
   * @param deadline the deadline
   * @param placeholders the placeholders
   * @param subcontractor the subcontractor
   * @param tenantLogo the tenant logo
   * @param logoWidth the logo width
   * @return the string
   */
  private String subcontractosProofDocuments(SubmittentDTO submittentDTO, DocumentDTO documentDTO,
    Date deadline, HashMap<String, String> placeholders, CompanyDTO subcontractor,
    byte[] tenantLogo, long logoWidth) {

    LOGGER.log(Level.CONFIG,
      "Executing method subcontractosProofDocuments, Parameters: submittentDTO: {0}, "
        + "documentDTO: {1}, deadline: {2}, placeholders: {3}, subcontractor: {4}, "
        + "tenantLogo: {5}, logoWidth: {6}",
      new Object[]{submittentDTO, documentDTO, deadline, placeholders, subcontractor,
        tenantLogo, logoWidth});

    HashMap<String, String> attributesMap = new HashMap<>();
    QMasterListValueHistoryEntity qMasterListValueHistory =
      QMasterListValueHistoryEntity.masterListValueHistoryEntity;
    String createdDocumentId;
    List<CompanyProofDTO> subcontractorProofDTOs =
      companyService.getProofByCompanyId(subcontractor.getId());

    List<String> paragraphList = new ArrayList<>();
    paragraphList.add(DocumentPlaceholders.F_GERMAN_PROOFS.getValue());
    // Retrieve different template for subcontractors
    MasterListValueHistoryEntity subcontractorsTemplate =
      new JPAQueryFactory(em).selectFrom(qMasterListValueHistory)
        .where(qMasterListValueHistory.shortCode.eq(Template.NACHWEISBRIEF_SUB)
          .and(qMasterListValueHistory.tenant.id
            .eq(usersService.getUserById(getUser().getId()).getTenant().getId())))
        .fetchOne();

    // In the field VALUE3 of the table MasterListValueHistory is stored whether
    // the document can be created only by Admin or not
    if (subcontractorsTemplate.getValue3() != null) {
      documentDTO.setIsAdminRightsOnly(
        subcontractorsTemplate.getValue3().equals(LookupValues.ZERO_STRING) ? Boolean.FALSE
          : Boolean.TRUE);
    }

    attributesMap.put(DocumentAttributes.TEMPLATE_ID.name(),
      subcontractorsTemplate.getMasterListValueId().getId());
    attributesMap.put(DocumentAttributes.TENANT_ID.name(),
      subcontractorsTemplate.getTenant().getId());

    documentDTO.setTemplateId(subcontractorsTemplate.getMasterListValueId().getId());
    List<NodeDTO> subcontractorsTemplates =
      documentService.getNodeByAttributes(LookupValues.TEMPLATE_FOLDER_ID, attributesMap);

    InputStream templateInputStream = new ByteArrayInputStream(
      versionService.getBinContent(subcontractorsTemplates.get(0).getId()));

    // Update the main company of every subcontractor.
    placeholders.put(DocumentPlaceholders.S_MAIN_COMPANY.getValue(),
      TemplateConstants.COPY_TO + submittentDTO.getCompanyId().getCompanyName() + ", "
        + submittentDTO.getCompanyId().getLocation());
    placeholders.put(DocumentPlaceholders.F_MAIN_COMPANY.getValue(),
      submittentDTO.getCompanyId().getCompanyName());

    addProofPlaceholder(subcontractorProofDTOs, deadline, placeholders);

    // Use the same rule with submittents and joint ventures template because it contains
    // the
    // same placeholders.
    ruleService.runProjectTemplateRules(null, subcontractor, documentDTO, null, placeholders,
      Template.NACHWEISBRIEF_PT);
    try {
      // Create document with different template with subcontractorss
      createdDocumentId =
        createDocument(documentDTO,
          templateService.replacePlaceholdersWordDoc(templateInputStream,
            SubmissConverter.replaceSpecialCharactersInPlaceholders(placeholders), tenantLogo,
            logoWidth, paragraphList, setParagraphPosition(Template.NACHWEISBRIEF_PT))
            .toByteArray(),
          submittentDTO.getId(), subcontractor.getId(), true);

      // Update company entity (subcontractors) with the new document values.
      updateCompanyDocumentValues(subcontractor.getId(), documentDTO, Template.NACHWEISBRIEF_PT);

    } finally {
      try {
        templateInputStream.close();
      } catch (IOException e) {
        LOGGER.log(Level.SEVERE, TemplateConstants.INPUTSTREAM_ERROR + e);
      }
    }

    return createdDocumentId;
  }

  /**
   * This method is used to generate Nachweisbrief for Submittents and Joint Ventures of the current
   * Submittent in Negotiated Procedure process type.
   *
   * @param submittentDTO the submittent DTO
   * @param documentDTO the document DTO
   * @param deadline the deadline
   * @param placeholders the placeholders
   * @param templateList the template list
   * @param companyDTO the company DTO
   * @param tenantLogo the tenant logo
   * @param logoWidth the logo width
   * @return the string
   */
  private String proofCompanyDocumentsNegotiatedProcess(SubmittentDTO submittentDTO,
    DocumentDTO documentDTO, Date deadline, HashMap<String, String> placeholders,
    List<NodeDTO> templateList, CompanyDTO companyDTO, byte[] tenantLogo, long logoWidth) {

    LOGGER.log(Level.CONFIG,
      "Executing method proofCompanyDocumentsNegotiatedProcess, Parameters: submittentDTO: {0}, "
        + "documentDTO: {1}, deadline: {2}, placeholders: {3}, templateList: {4}, "
        + "companyDTO: {5}, tenantLogo: {6}, logoWidth: {7}",
      new Object[]{submittentDTO, documentDTO, deadline, placeholders, templateList,
        companyDTO, tenantLogo, logoWidth});

    List<CompanyProofDTO> companyProofDTOs = companyService.getProofByCompanyId(companyDTO.getId());
    String createdDocumentId;
    addProofPlaceholder(companyProofDTOs, deadline, placeholders);

    InputStream inputStream =
      new ByteArrayInputStream(versionService.getBinContent(templateList.get(0).getId()));
    ruleService.runProjectTemplateRules(null, companyDTO, documentDTO, null, placeholders,
      Template.NACHWEISBRIEF_PT);

    List<String> paragraphList = new ArrayList<>();
    paragraphList.add(DocumentPlaceholders.F_GERMAN_PROOFS.getValue());
    createdDocumentId =
      createDocument(documentDTO,
        templateService.replacePlaceholdersWordDoc(inputStream,
          SubmissConverter.replaceSpecialCharactersInPlaceholders(placeholders), tenantLogo,
          logoWidth, paragraphList, setParagraphPosition(Template.NACHWEISBRIEF_PT))
          .toByteArray(),
        submittentDTO.getId(), companyDTO.getId(), true);

    // Update company entity with the new document values.
    updateCompanyDocumentValues(companyDTO.getId(), documentDTO, Template.NACHWEISBRIEF_PT);

    return createdDocumentId;
  }

  /**
   * Gets the user departments list as a string.
   *
   * @return the user departments list as a string
   */
  private String getUserDepartments() {

    LOGGER.log(Level.CONFIG, "Executing method getUserDepartments");

    StringBuilder departmentIds = new StringBuilder();
    SubmissUserDTO user = usersService.getUserById(getUser().getId());
    if (user.getMainDepartment() != null) {
      departmentIds.append(user.getMainDepartment().getDepartmentId().getId()).append(",");
    }
    if (user.getSecondaryDepartments() != null && !user.getSecondaryDepartments().isEmpty()) {
      for (DepartmentHistoryDTO secondaryDepartment : user.getSecondaryDepartments()) {
        departmentIds.append(secondaryDepartment.getDepartmentId().getId()).append(",");
      }
    }
    if (departmentIds.length() != 0) {
      return departmentIds.substring(0, departmentIds.length() - 1);
    }
    return null;
  }

  /**
   * Creates two lists for using them as the header and the content of the company offers table.
   *
   * @param headerCompanyOffers the header company offers
   * @param contentCompanyOffers the content company offers
   * @param offers the offers
   */
  private void createCompanyOffersTable(List<String> headerCompanyOffers,
    List<LinkedHashMap<Map<String, String>, String>> contentCompanyOffers,
    List<CompanyOfferDTO> offers) {

    LOGGER.log(Level.CONFIG,
      "Executing method createCompanyOffersTable, Parameters: headerCompanyOffers: {0}, "
        + "contentCompanyOffers: {1}, offers: {2}",
      new Object[]{headerCompanyOffers, contentCompanyOffers, offers});

    /* Create a list of Strings for the header of the table */
    headerCompanyOffers.add(TemplateConstants.TEMPLATE_TABLE_HEADER.HEADER_COLUMN_DATE.getValue());
    headerCompanyOffers.add("Objekt");
    headerCompanyOffers.add("Projekt");
    headerCompanyOffers.add("Submission");
    headerCompanyOffers.add("Nettobetrag");
    headerCompanyOffers.add("Rang");
    String tenantName = getUsersTenantName();
    if (tenantName.equals(DocumentProperties.TENANT_STADT_BERN.getValue())) {
      headerCompanyOffers.add("F");
    }
    headerCompanyOffers.add("Z");
    headerCompanyOffers.add("A");

    Map<String, String> cellProperties = new HashMap<>();
    Map<String, String> cellProperties2 = new HashMap<>();
    Map<String, String> cellProperties3 = new HashMap<>();
    Map<String, String> cellProperties4 = new HashMap<>();
    Map<String, String> cellProperties5 = new HashMap<>();
    Map<String, String> cellProperties6 = new HashMap<>();
    Map<String, String> cellProperties7 = new HashMap<>();

    cellProperties.put(DocumentProperties.FONT_SIZE.getValue(),
      DocumentProperties.NUMBER_16.getValue());
    cellProperties.put(DocumentProperties.LEFT_MARGIN.getValue(),
      DocumentProperties.NUMBER_60.getValue());

    cellProperties2.put(DocumentProperties.FONT_SIZE.getValue(),
      DocumentProperties.NUMBER_16.getValue());
    cellProperties2.put(DocumentProperties.LEFT_MARGIN.getValue(),
      DocumentProperties.NUMBER_60.getValue());

    cellProperties3.put(DocumentProperties.FONT_SIZE.getValue(),
      DocumentProperties.NUMBER_16.getValue());
    cellProperties3.put(DocumentProperties.LEFT_MARGIN.getValue(),
      DocumentProperties.NUMBER_60.getValue());

    cellProperties4.put(DocumentProperties.FONT_SIZE.getValue(),
      DocumentProperties.NUMBER_16.getValue());
    cellProperties4.put(DocumentProperties.LEFT_MARGIN.getValue(),
      DocumentProperties.NUMBER_60.getValue());

    cellProperties5.put(DocumentProperties.FONT_SIZE.getValue(),
      DocumentProperties.NUMBER_16.getValue());
    cellProperties5.put(DocumentProperties.LEFT_MARGIN.getValue(),
      DocumentProperties.NUMBER_60.getValue());

    cellProperties6.put(DocumentProperties.FONT_SIZE.getValue(),
      DocumentProperties.NUMBER_16.getValue());
    cellProperties6.put(DocumentProperties.LEFT_MARGIN.getValue(),
      DocumentProperties.NUMBER_60.getValue());

    cellProperties7.put(DocumentProperties.FONT_SIZE.getValue(),
      DocumentProperties.NUMBER_16.getValue());
    cellProperties7.put(DocumentProperties.LEFT_MARGIN.getValue(),
      DocumentProperties.NUMBER_60.getValue());

    /* Loop for the worktypes and add it to LinkedHashMap (position column: according integer) */
    Collections.sort(offers, ComparatorUtil.sortCompanyOffersByDate);
    for (CompanyOfferDTO offer : offers) {
      Locale locale = new Locale("de", "CH");
      NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
      LinkedHashMap<Map<String, String>, String> subContent = new LinkedHashMap<>();
      cellProperties.put(DocumentProperties.WIDTH.getValue(), "1240"); // 2.19 cm
      cellProperties.put(DocumentProperties.VALUE.getValue(), "1");
      if (offer.getDeadline2() != null) {
        subContent.put(cellProperties, SubmissConverter.convertToSwissDate(offer.getDeadline2()));
      } else {
        subContent.put(cellProperties, TemplateConstants.EMPTY_STRING);
      }

      cellProperties2.put(DocumentProperties.WIDTH.getValue(), "1840"); // 3.25 cm
      cellProperties2.put(DocumentProperties.VALUE.getValue(), "2");
      subContent.put(cellProperties2, offer.getObjectName());

      cellProperties3.put(DocumentProperties.WIDTH.getValue(), "1700"); // 3 cm
      cellProperties3.put(DocumentProperties.VALUE.getValue(), "3");
      subContent.put(cellProperties3, offer.getProjectName());

      cellProperties4.put(DocumentProperties.WIDTH.getValue(), "2125"); // 3.75 cm
      cellProperties4.put(DocumentProperties.VALUE.getValue(), "4");
      if (offer.getSubmissionId() != null) {
        SubmissionDTO submission = submissionService.getSubmissionById(offer.getSubmissionId());
        StringBuilder submissionCol = new StringBuilder();
        if (submission.getDescription() == null || submission.getDescription().isEmpty()) {
          submissionCol.append(submission.getWorkType().getValue2());
        } else {
          submissionCol.append(submission.getWorkType().getValue2()).append(", ")
            .append(submission.getDescription());
        }
        subContent.put(cellProperties4, submissionCol.toString());
      } else {
        /*
         * if submissionId is null it means that the submission is migrated so in this case choose
         * the submission name from migrated offer
         */
        subContent.put(cellProperties4, offer.getMigratedSubmission());
      }

      cellProperties5.put(DocumentProperties.VALUE.getValue(), "5");
      cellProperties5.put(DocumentProperties.WIDTH.getValue(), "1700"); // 3 cm
      cellProperties5.put(DocumentProperties.ALIGN_RIGHT.getValue(), Boolean.TRUE.toString());
      subContent.put(cellProperties5, currencyFormatter.format(offer.getAmmount())
        .replace(TemplateConstants.DEFAULT_AMOUNT_FORMAT, TemplateConstants.EMPTY_STRING));

      cellProperties6.put(DocumentProperties.VALUE.getValue(), "6");
      cellProperties6.put(DocumentProperties.WIDTH.getValue(), "566"); // 1 cm
      subContent.put(cellProperties6,
        offer.getRank() == null ? TemplateConstants.EMPTY_STRING : offer.getRank().toString());

      cellProperties7.put(DocumentProperties.VALUE.getValue(), "7");
      cellProperties7.put(DocumentProperties.WIDTH.getValue(), "284"); // 0.5 cm
      if ((!offer.getSubcontractors().isEmpty() || !offer.getJointVentures().isEmpty())
        && (offer.getSubcontractors() != null || offer.getJointVentures() != null)
        && offer.isLeading()) {
        subContent.put(cellProperties7, "X");
      } else {
        subContent.put(cellProperties7, TemplateConstants.EMPTY_STRING);
      }
      if (tenantName.equals(DocumentProperties.TENANT_STADT_BERN.getValue())) {
        cellProperties7.put(DocumentProperties.VALUE.getValue(), "8");
      }
      if (offer.getIsAwarded() != null) {
        subContent.put(cellProperties7,
          !offer.getIsAwarded() ? TemplateConstants.EMPTY_STRING : "X");
      } else {
        subContent.put(cellProperties7, TemplateConstants.EMPTY_STRING);
      }
      cellProperties7.put(DocumentProperties.VALUE.getValue(), "9");
      if (offer.getIsAwarded() == null) {
        subContent.put(cellProperties7,
          !offer.getIsExcludedFromProcess() ? TemplateConstants.EMPTY_STRING : "X");
      } else {
        subContent.put(cellProperties7, TemplateConstants.EMPTY_STRING);
      }
      contentCompanyOffers.add(subContent);
    }
  }

  /**
   * Gets the users tenant name.
   *
   * @return the users tenant name
   */
  private String getUsersTenantName() {

    LOGGER.log(Level.CONFIG, "Executing method getUsersTenantName");

    String tenantId = getUser().getAttribute("TENANT").getData();
    return sDTenantService.getTenantById(tenantId).getName();
  }

  @Override
  public byte[] generateCompanyPartDocument(DocumentDTO documentDTO, String templateShortCode) {

    LOGGER.log(Level.CONFIG,
      "Executing method generateCompanyPartDocument, Parameters: documentDTO: {0}, "
        + "templateShortCode: {1}",
      new Object[]{documentDTO, templateShortCode});

    HashMap<String, String> placeholders = new HashMap<>();
    CompanyDTO company = null;

    List<CompanyOfferDTO> offers = new ArrayList<>();
    if (documentDTO.getFolderId() != null) {
      company = companyService.getCompanyById(documentDTO.getFolderId());
      offers = companyService.getOfferByCompanyId(company.getId(), false);
    }

    LinkedList<String> headerCompanyOffers = new LinkedList<>();
    List<LinkedHashMap<Map<String, String>, String>> contentCompanyOffers = new ArrayList<>();
    HashMap<String, String> attributesMap = new HashMap<>();
    StringBuilder worktypes = new StringBuilder();
    List<CompanyProofDTO> companyProofDTOs = null;
    QMasterListValueHistoryEntity qMasterListValueHistory =
      QMasterListValueHistoryEntity.masterListValueHistoryEntity;
    Map<String, String> tableProperties = new HashMap<>();
    MasterListValueHistoryEntity template =
      new JPAQueryFactory(em).selectFrom(qMasterListValueHistory)
        .where(qMasterListValueHistory.shortCode.eq(templateShortCode)
          .and(qMasterListValueHistory.tenant.id
            .eq(usersService.getUserById(getUser().getId()).getTenant().getId())))
        .fetchOne();

    /* Get from User the Tenant */
    TenantDTO tenant = sDTenantService
      .getTenantById(getUser().getAttributeData(USER_ATTRIBUTES.TENANT.getValue()));

    /* Get logo */
    byte[] logo = getLogo(documentDTO.getFolderId(), placeholders);
    /* Get logo width per tenant */
    long logoWidth = getLogoWidthForTenant(tenant);

    // In the field VALUE3 of the table MasterListValueHistory is stored whether
    // the document can be created only by Admin or not
    if (template.getValue3() != null) {
      documentDTO.setIsAdminRightsOnly(
        template.getValue3().equals(LookupValues.ZERO_STRING) ? Boolean.FALSE : Boolean.TRUE);
    }
    InputStream inputStream = null;
    if (template.getShortCode().equals(Template.COMPANIES_PER_TENANT)) {
      documentDTO.setFilename(template.getValue1() + TemplateConstants.XLSX_FILE_EXTENSION);
    } else {
      if (template.getShortCode().equals(Template.FIRMENBLATT_EINFACH)
        || template.getShortCode().equals(Template.FIRMENBLATT_ERWEITERT)) {
        documentDTO.setFilename(template.getValue1() + TemplateConstants.PDF_FILE_EXTENSION);
      } else {
        documentDTO.setFilename(template.getValue1() + TemplateConstants.DOCX_FILE_EXTENSION);
      }
      // Retrieve template from DB
      attributesMap.put(DocumentAttributes.TEMPLATE_ID.name(),
        template.getMasterListValueId().getId());
      attributesMap.put(DocumentAttributes.TENANT_ID.name(), template.getTenant().getId());
      List<NodeDTO> templateList =
        documentService.getNodeByAttributes(LookupValues.TEMPLATE_FOLDER_ID, attributesMap);
      inputStream =
        new ByteArrayInputStream(versionService.getBinContent(templateList.get(0).getId()));
    }

    switch (templateShortCode) {
      case Template.ZERTIFIKAT:

        // Set template values to documentDTO in order to generate document with these attributes.
        documentDTO.setTemplateId(template.getMasterListValueId().getId());
        documentDTO.setTenantId(template.getTenant().getId());

        companyProofDTOs = companyService.getProofByCompanyId(documentDTO.getFolderId());
        addGermanFrenchProofText(placeholders, companyProofDTOs);

        placeholders.put(DocumentPlaceholders.F_COMPANY_LOCATION.getValue(), company.getLocation());
        placeholders.put(DocumentPlaceholders.F_COMPANY_ADDRESS.getValue(), company.getAddress1());
        placeholders.put(DocumentPlaceholders.F_COMPANY_ADDRESS2.getValue(), company.getAddress2());

        initReferenceData(Template.ZERTIFIKAT, placeholders, documentDTO);

        ruleService.runProjectTemplateRules(null, company, documentDTO, null, placeholders,
          Template.ZERTIFIKAT);

        // Update company in DB according to documentDTO values.
        updateCompanyDocumentValues(company.getId(), documentDTO, Template.ZERTIFIKAT);

        CompanyEntity companyEntity = em.find(CompanyEntity.class, documentDTO.getFolderId());
        StringBuilder additionalInfo = new StringBuilder(companyEntity.getCompanyName())
          .append("[#]")
          .append(companyEntity.getProofStatusFabe()).append("[#]");

        audit.createLogAudit(AuditLevel.COMPANY_LEVEL.name(), AuditEvent.CREATE.name(),
          AuditGroupName.COMPANY.name(), AuditMessages.GENERATE_CERTIFICATE_DOCUMENT.name(),
          getUser().getId(), company.getId(), null, additionalInfo.toString(),
          LookupValues.EXTERNAL_LOG);

        // Initialize placeholder where the bullet list should be replaced in the docx.
        List<String> bulletList = new ArrayList<>();
        bulletList.add(DocumentPlaceholders.F_GERMAN_PROOFS.getValue());
        bulletList.add(DocumentPlaceholders.F_FRENCH_PROOFS.getValue());
        // Add the bullet list properties
        Map<String, String> bulletListProperties = getBulletListProperties();

        return templateService.replacePlaceholdersWordDoc(inputStream,
          SubmissConverter.replaceSpecialCharactersInPlaceholders(placeholders),
          documentDTO.getDeptAmountAction(), bulletList, bulletListProperties).toByteArray();

      case Template.LISTE_ARBEITSGATTUNG:
        String fileExtension = setFileExtension(Template.LISTE_ARBEITSGATTUNG);
        documentDTO.setFilename(template.getValue1() + fileExtension);
        List<MasterListValueHistoryDTO> workTypes = sDWorktypeService.readAll();

        // In order to avoid some huge white spaces created by Jasper
        // in the end of the last page,
        // we have to create some empty values
        int dummyCount = workTypes.size() % 54; // every page contains 54 workTypes
        if (dummyCount > 0) {
          dummyCount = 54 - dummyCount;
          for (int i = 0; i < dummyCount; i++) {
            MasterListValueHistoryDTO masterListValueHistoryDTO = new MasterListValueHistoryDTO();
            masterListValueHistoryDTO.setValue1(StringUtils.EMPTY);
            masterListValueHistoryDTO.setValue2(StringUtils.EMPTY);
            workTypes.add(masterListValueHistoryDTO);
          }
        }

        InputStream newInputStream =
          templateBean.getTemplateByAttributeName(TemplateConstants.LISTE_ARBEITSGATTUNG);
        if (newInputStream != null) {
          try {
            Map<String, Object> parameters = new HashMap<>();
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(workTypes);
            parameters.put(TemplateConstants.DATASOURCE, dataSource);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            JasperCompileManager.compileReportToStream(newInputStream, byteArrayOutputStream);
            InputStream stream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(stream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
              new JRBeanCollectionDataSource(workTypes));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            JRXlsxExporter exporter = new JRXlsxExporter();
            SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
            configuration.setDetectCellType(true);
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
            exporter.setConfiguration(configuration);
            exporter.exportReport();
            return outputStream.toByteArray();
          } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
          }
        }
        return new byte[0];

      case Template.FIRMENBLATT_EINFACH:
        if (company.getWorkTypes() != null) {
          List<MasterListValueHistoryDTO> worktypeList = new ArrayList<>(company.getWorkTypes());
          Collections.sort(worktypeList, ComparatorUtil.workType);
          for (MasterListValueHistoryDTO worktype : worktypeList) {
            if (worktype.getValue1().length() > 3) {
              worktypes.append(StringUtils.rightPad(worktype.getValue1(), 8))
                .append(worktype.getValue2());
            } else {
              worktypes.append(StringUtils.rightPad(worktype.getValue1(), 9))
                .append(worktype.getValue2());
            }
            worktypes.append(TemplateConstants.NEW_LINE_STRING);
          }
        }
        if (worktypes.length() > 1) {
          placeholders.put(DocumentPlaceholders.F_COMPANY_WORKTYPES.getValue(),
            worktypes.substring(0, worktypes.length() - 1));
        } else {
          placeholders.put(DocumentPlaceholders.F_COMPANY_WORKTYPES.getValue(),
            TemplateConstants.EMPTY_STRING);
        }

        placeholders.put(DocumentPlaceholders.F_COMPANY_PROOF_STATUS.getValue(),
          templateBean.setCompanyNotProvidedProofStatus(company).toString());
        String apFactor = (company.getApprenticeFactor() != null)
          ? String.format("%.2f", company.getApprenticeFactor())
          : String.format("%.2f", DEFAULT_DOUBLE);
        placeholders.put(DocumentPlaceholders.F_COMPANY_APPRENTICE_FACTOR_VALUE.getValue(),
          apFactor);

        // The fifty plus factor (F50+).
        String fiftyPlusFactor = (company.getFiftyPlusFactor() != null)
          ? String.format("%.2f", company.getFiftyPlusFactor())
          : String.format("%.2f", DEFAULT_DOUBLE);
        placeholders.put(DocumentPlaceholders.F_COMPANY_FIFTY_PLUS_FACTOR_VALUE.getValue(),
          fiftyPlusFactor);

        ruleService.runProjectTemplateRules(null, company, documentDTO, null, placeholders,
          Template.FIRMENBLATT_EINFACH);

        tableProperties.put(DocumentProperties.REMOVE_BORDER.getValue(), Boolean.TRUE.toString());
        tableProperties.put(DocumentProperties.BOLD_HEADER.getValue(), Boolean.TRUE.toString());
        tableProperties.put(DocumentProperties.FONT_SIZE.getValue(),
          DocumentProperties.NUMBER_16.getValue());
        tableProperties.put(DocumentProperties.BOTTOM_MARGIN.getValue(),
          DocumentProperties.NUMBER_60.getValue());
        tableProperties.put(DocumentProperties.BORDER_SPACE.getValue(), "0");
        tableProperties.put(DocumentProperties.LEFT_MARGIN.getValue(),
          DocumentProperties.NUMBER_60.getValue());
        tableProperties.put(DocumentProperties.SPACING.getValue(),
          DocumentProperties.NUMBER_200.getValue());
        tableProperties.put(DocumentProperties.REPEAT_HEADER.getValue(), Boolean.TRUE.toString());
        tableProperties.put(DocumentProperties.TABLE_TITLE_GRIDSPAN.getValue(),
          DocumentProperties.NUMBER_3.getValue());
        tableProperties.put(DocumentProperties.TABLE_TITLE_FONT_SIZE.getValue(),
          DocumentProperties.NUMBER_24.getValue());

        createCompanyOffersTable(headerCompanyOffers, contentCompanyOffers, offers);

        return submissPrintService.convertToPDF(templateService
          .replacePlaceholdersWordDoc(new ByteArrayInputStream(templateService
              .createTableInDocxDocument(inputStream, headerCompanyOffers,
                TemplateConstants.SUBMITTED_OFFERS, contentCompanyOffers, tableProperties, null)
              .toByteArray()),
            SubmissConverter.replaceSpecialCharactersInPlaceholders(placeholders), logo,
            logoWidth)
          .toByteArray());

      case Template.COMPANIES_PER_TENANT:
        return generateCompanyPartDocumentExcel(templateShortCode);

      case Template.FIRMENBLATT_ERWEITERT:

        // Replace company proof placeholders.
        StringBuilder proofs1 = new StringBuilder();
        StringBuilder proofs2 = new StringBuilder();
        StringBuilder proofs3 = new StringBuilder();
        StringBuilder proofs4 = new StringBuilder();
        companyProofDTOs = companyService.getProofByCompanyId(company.getId());

        // The fifty plus factor (F50+).
        String fPlusFactor = (company.getFiftyPlusFactor() != null)
          ? String.format("%.2f", company.getFiftyPlusFactor())
          : String.format("%.2f", DEFAULT_DOUBLE);
        placeholders.put(DocumentPlaceholders.F_COMPANY_FIFTY_PLUS_FACTOR_VALUE.getValue(),
          fPlusFactor);

        Collections.sort(companyProofDTOs, ComparatorUtil.sortCompanyProofDTOsAsc);
        int count = 0;
        for (CompanyProofDTO companyProof : companyProofDTOs) {
          count++;
          if ((count % 2) == 0) {
            if (companyProof != null) {
              proofs3.append(companyProof.getProof().getName());
              proofs3.append(TemplateConstants.NEW_LINE_STRING);
              if (companyProof.getProofDate() != null) {
                proofs4.append(SubmissConverter.convertToSwissDate(companyProof.getProofDate()));
                proofs4.append(TemplateConstants.NEW_LINE_STRING);
              } else {
                proofs4.append("nicht erforderlich");
                proofs4.append(TemplateConstants.NEW_LINE_STRING);
              }
            }
          } else {
            if (companyProof != null) {
              proofs1.append(companyProof.getProof().getName());
              proofs1.append(TemplateConstants.NEW_LINE_STRING);
              if (companyProof.getProofDate() != null) {
                proofs2.append(SubmissConverter.convertToSwissDate(companyProof.getProofDate()));
                proofs2.append(TemplateConstants.NEW_LINE_STRING);
              } else {
                proofs2.append("nicht erforderlich");
                proofs2.append(TemplateConstants.NEW_LINE_STRING);
              }
            }
          }
          if (proofs1.length() > 1) {
            placeholders.put(DocumentPlaceholders.F_COMPANY_PROOF_STATUS_COL_1.getValue(),
              proofs1.substring(0, proofs1.length() - 1));
            placeholders.put(DocumentPlaceholders.F_COMPANY_PROOF_STATUS_COL_2.getValue(),
              proofs2.substring(0, proofs2.length() - 1));
          } else {
            placeholders.put(DocumentPlaceholders.F_COMPANY_PROOF_STATUS_COL_1.getValue(),
              TemplateConstants.EMPTY_STRING);
            placeholders.put(DocumentPlaceholders.F_COMPANY_PROOF_STATUS_COL_2.getValue(),
              TemplateConstants.EMPTY_STRING);
          }
          if (proofs3.length() > 1) {
            placeholders.put(DocumentPlaceholders.F_COMPANY_PROOF_STATUS_COL_3.getValue(),
              proofs3.substring(0, proofs3.length() - 1));
            placeholders.put(DocumentPlaceholders.F_COMPANY_PROOF_STATUS_COL_4.getValue(),
              proofs4.substring(0, proofs4.length() - 1));
          } else {
            placeholders.put(DocumentPlaceholders.F_COMPANY_PROOF_STATUS_COL_3.getValue(),
              TemplateConstants.EMPTY_STRING);
            placeholders.put(DocumentPlaceholders.F_COMPANY_PROOF_STATUS_COL_4.getValue(),
              TemplateConstants.EMPTY_STRING);
          }
        }
        if (company.getWorkTypes() != null) {
          List<MasterListValueHistoryDTO> worktypeList = new ArrayList<>(company.getWorkTypes());
          Collections.sort(worktypeList, ComparatorUtil.workType);
          for (MasterListValueHistoryDTO worktype : worktypeList) {
            if (worktype.getValue1().length() > 3) {
              worktypes.append(StringUtils.rightPad(worktype.getValue1(), 8))
                .append(worktype.getValue2());
            } else {
              worktypes.append(StringUtils.rightPad(worktype.getValue1(), 9))
                .append(worktype.getValue2());
            }
            worktypes.append(TemplateConstants.NEW_LINE_STRING);
          }
        }
        if (worktypes.length() > 1) {
          placeholders.put(DocumentPlaceholders.F_COMPANY_WORKTYPES.getValue(),
            worktypes.substring(0, worktypes.length() - 1));
        } else {
          placeholders.put(DocumentPlaceholders.F_COMPANY_WORKTYPES.getValue(),
            TemplateConstants.EMPTY_STRING);
        }

        placeholders.put(DocumentPlaceholders.F_COMPANY_PROOF_STATUS.getValue(),
          templateBean.setCompanyNotProvidedProofStatus(company).toString());
        String appFactor = (company.getApprenticeFactor() != null)
          ? String.format("%.2f", company.getApprenticeFactor())
          : String.format("%.2f", DEFAULT_DOUBLE);
        placeholders.put(DocumentPlaceholders.F_COMPANY_APPRENTICE_FACTOR_VALUE.getValue(),
          appFactor);

        initReferenceData(Template.FIRMENBLATT_ERWEITERT, placeholders, documentDTO);
        tableProperties.put(DocumentProperties.REMOVE_BORDER.getValue(), Boolean.TRUE.toString());
        tableProperties.put(DocumentProperties.BOLD_HEADER.getValue(), Boolean.TRUE.toString());
        tableProperties.put(DocumentProperties.FONT_SIZE.getValue(),
          DocumentProperties.NUMBER_16.getValue());
        tableProperties.put(DocumentProperties.BOTTOM_MARGIN.getValue(),
          DocumentProperties.NUMBER_60.getValue());
        tableProperties.put(DocumentProperties.BORDER_SPACE.getValue(), "0");
        tableProperties.put(DocumentProperties.LEFT_MARGIN.getValue(),
          DocumentProperties.NUMBER_60.getValue());
        tableProperties.put(DocumentProperties.SPACING.getValue(),
          DocumentProperties.NUMBER_160.getValue());
        tableProperties.put(DocumentProperties.REPEAT_HEADER.getValue(), Boolean.TRUE.toString());
        tableProperties.put(DocumentProperties.TABLE_TITLE_GRIDSPAN.getValue(),
          DocumentProperties.NUMBER_3.getValue());
        tableProperties.put(DocumentProperties.TABLE_TITLE_FONT_SIZE.getValue(),
          DocumentProperties.NUMBER_24.getValue());

        ruleService.runProjectTemplateRules(null, company, documentDTO, null, placeholders,
          Template.FIRMENBLATT_ERWEITERT);

        /*
         * For only this document the text (direction name) after comma must split in a second line
         */
        String directorateName =
          placeholders.get(DocumentPlaceholders.R_DIRECTORATE_NAME.getValue());

        placeholders.put(DocumentPlaceholders.R_DIRECTORATE_NAME.getValue(),
          directorateName.replace(",", ", \n"));

        /*
         * if tenant is Kanton Bern don't create offer table for the current document and add
         * placeholder to show the doc creator: user name and surname
         */
        if (tenant.getName().equals(DocumentProperties.TENANT_KANTON_BERN.getValue())) {
          placeholders.put(DocumentPlaceholders.R_INITIALS.getValue(),
            getUser().getAttributeData(USER_ATTRIBUTES.LASTNAME.getValue()) + " "
              + getUser().getAttributeData(USER_ATTRIBUTES.FIRSTNAME.getValue()));
          return submissPrintService
            .convertToPDF(templateService.replacePlaceholdersWordDoc(inputStream,
              SubmissConverter.replaceSpecialCharactersInPlaceholders(placeholders), null,
              new ArrayList<String>(), logo, logoWidth).toByteArray());
        } else {
          createCompanyOffersTable(headerCompanyOffers, contentCompanyOffers, offers);
          return submissPrintService.convertToPDF(templateService.replacePlaceholdersWordDoc(
            new ByteArrayInputStream(templateService.createTableInDocxDocument(inputStream,
              headerCompanyOffers, TemplateConstants.SUBMITTED_OFFERS, contentCompanyOffers,
              tableProperties, null).toByteArray()),
            SubmissConverter.replaceSpecialCharactersInPlaceholders(placeholders), logo,
            logoWidth).toByteArray());
        }

      default:
        return new byte[0];
    }
  }

  /**
   * Adds the German/French proofs and text.
   *
   * @param placeholders the placeholders
   * @param companyProofDTOs the companyProofDTOs
   */
  private void addGermanFrenchProofText(HashMap<String, String> placeholders,
    List<CompanyProofDTO> companyProofDTOs) {

    LOGGER.log(Level.CONFIG,
      "Executing method addGermanFrenchProofText, Parameters: placeholders: {0}, "
        + "companyProofDTOs: {1}",
      new Object[]{placeholders, companyProofDTOs});

    // Replace company proof placeholders.
    StringBuilder germanProofs = new StringBuilder();
    StringBuilder frenchProofs = new StringBuilder();
    for (CompanyProofDTO companyProof : companyProofDTOs) {
      if (companyProof.getRequired()) {
        germanProofs.append(companyProof.getProof().getDescription());
        germanProofs.append(TemplateConstants.NEW_LINE_STRING);
        frenchProofs.append(companyProof.getProof().getDescriptionFr());
        frenchProofs.append(TemplateConstants.NEW_LINE_STRING);
      }
    }
    // Add also the german and french text to displayed before the proofs in the bullet list
    germanProofs.append(TemplateConstants.GERMAN_TEXT_ZERTIFIKAT);
    frenchProofs.append(TemplateConstants.FRENCH_TEXT_ZERTIFIKAT);
    if (germanProofs.length() > 1 && frenchProofs.length() > 1) {
      placeholders.put(DocumentPlaceholders.F_GERMAN_PROOFS.getValue(),
        germanProofs.substring(0, germanProofs.length() - 1));
      placeholders.put(DocumentPlaceholders.F_FRENCH_PROOFS.getValue(),
        frenchProofs.substring(0, frenchProofs.length() - 1));
    } else {
      placeholders.put(DocumentPlaceholders.F_GERMAN_PROOFS.getValue(),
        TemplateConstants.EMPTY_STRING);
      placeholders.put(DocumentPlaceholders.F_FRENCH_PROOFS.getValue(),
        TemplateConstants.EMPTY_STRING);
    }
  }

  /**
   * Gets the bullet list properties.
   *
   * @return the bulletListProperties
   */
  private Map<String, String> getBulletListProperties() {

    LOGGER.log(Level.CONFIG, "Executing method getBulletListProperties");

    Map<String, String> bulletListProperties = new HashMap<>();
    bulletListProperties.put("fontSize", "18"); // in docx4j: font size = (word doc font size) * 2
    bulletListProperties.put("bulletMargin", "30"); // the left margin of the bullet
    bulletListProperties.put("line", "240"); // single line spacing
    bulletListProperties.put("lineRule", "auto"); // line spacing: auto
    return bulletListProperties;
  }

  @Override
  public byte[] generateCompanySearchDocuments(DocumentDTO documentDTO, String shortCode,
    List<CompanyDTO> searchedCompanies, String sortColumn, String sortType,
    CompanySearchDTO searchDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method generateCompanySearchDocuments, Parameters: documentDTO: {0}, "
        + "shortCode: {1}, searchedCompanies: {2}, sortColumn: {3}, sortType: {4}, "
        + "searchDTO: {5}",
      new Object[]{documentDTO, shortCode, searchedCompanies, sortColumn, sortType, searchDTO});

    QMasterListValueHistoryEntity qMasterListValueHistory =
      QMasterListValueHistoryEntity.masterListValueHistoryEntity;
    MasterListValueHistoryEntity template =
      new JPAQueryFactory(em).selectFrom(qMasterListValueHistory)
        .where(qMasterListValueHistory.shortCode.eq(shortCode)
          .and(qMasterListValueHistory.tenant.id
            .eq(usersService.getUserById(getUser().getId()).getTenant().getId())))
        .fetchOne();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    switch (shortCode) {
      case Template.FIRMENLISTE_KOMPLETT:
        documentDTO.setFilename(template.getValue1() + TemplateConstants.PDF_FILE_EXTENSION);
        try (InputStream inputStream =
          templateBean.getTemplateByAttributeName(TemplateConstants.FIRMENLISTE_KOMPLETT)) {
          Map<String, Object> parameters = new HashMap<>();
          List<CompanyDTO> allCompanies = companyService.getAllCompanies();
          parameters.put(TemplateConstants.DATASOURCE,
            new JRBeanCollectionDataSource(allCompanies));
          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
          JasperCompileManager.compileReportToStream(inputStream, byteArrayOutputStream);
          InputStream stream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
          JasperReport jasperReport = (JasperReport) JRLoader.loadObject(stream);
          JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
            new JRBeanCollectionDataSource(allCompanies));

          // check if template generates extra blank page at the end of document
          // If extra page is generated that means that the page has only 2 elements (Date - Paging)
          // so we check if there are only 2 values and remove that page
          // then we fix page numbering
          for (Iterator<JRPrintPage> i = jasperPrint.getPages().iterator(); i.hasNext(); ) {
            JRPrintPage page = i.next();
            if (page.getElements().size() == 2) {
              i.remove();
              templateBean.fixTemplatePaging(jasperPrint);
            }
          }

          outputStream = new ByteArrayOutputStream();
          JRPdfExporter exporter = new JRPdfExporter();
          SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
          exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
          exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
          exporter.setConfiguration(configuration);
          exporter.exportReport();
          outputStream.toByteArray();
        } catch (Exception e) {
          LOGGER.log(Level.SEVERE, e.getMessage());
        }
        break;
      case Template.FIRMENLISTE_NACH_SUCHRESULTAT:
        documentDTO.setFilename(template.getValue1() + TemplateConstants.PDF_FILE_EXTENSION);
        try (InputStream inputStream = templateBean
          .getTemplateByAttributeName(TemplateConstants.FIRMENLISTE_NACH_SUCHRESULTAT)) {
          Map<String, Object> parameters = new HashMap<>();
          if (searchedCompanies != null && !searchedCompanies.isEmpty()) {

            List<String> searchedCompaniesIDs = new ArrayList<>();
            for (CompanyDTO searchedCompany : searchedCompanies) {
              searchedCompaniesIDs.add(searchedCompany.getId());
            }
            List<CompanyDTO> companies =
              companyService.getSearchedCompanies(searchedCompaniesIDs, sortColumn, sortType);

            parameters.put(TemplateConstants.DATASOURCE, new JRBeanCollectionDataSource(companies));
            parameters.put("SEARCHED_CRITERIA", templateBean.companySearchCriteria(searchDTO));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            JasperCompileManager.compileReportToStream(inputStream, byteArrayOutputStream);
            InputStream stream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(stream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
              new JRBeanCollectionDataSource(companies));

            // check if template generates extra blank page at the end of document
            // If extra page is generated that means that the page has only 2 elements (Date - Paging)
            // so we check if there are only 2 values and remove that page
            // then we fix page numbering
            for (Iterator<JRPrintPage> i = jasperPrint.getPages().iterator(); i.hasNext(); ) {
              JRPrintPage page = i.next();
              if (page.getElements().size() == 2) {
                i.remove();
                templateBean.fixTemplatePaging(jasperPrint);
              }
            }

            outputStream = new ByteArrayOutputStream();
            JRPdfExporter exporter = new JRPdfExporter();
            SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
            exporter.setConfiguration(configuration);
            exporter.exportReport();
            outputStream.toByteArray();
          }
        } catch (Exception e) {
          LOGGER.log(Level.SEVERE, e.getMessage());
        }
        break;
      case Template.LISTE_ARBEITSGATTUNG:
        documentDTO.setFilename(template.getValue1() + TemplateConstants.XLSX_FILE_EXTENSION);
        List<MasterListValueHistoryDTO> workTypes = sDWorktypeService.readAll();

        // In order to avoid some huge white spaces created by Jasper
        // in the end of the last page,
        // we have to create some empty values
        int dummyCount = workTypes.size() % 54; // every page contains 54 workTypes
        if (dummyCount > 0) {
          dummyCount = 54 - dummyCount;
          for (int i = 0; i < dummyCount; i++) {
            MasterListValueHistoryDTO masterListValueHistoryDTO = new MasterListValueHistoryDTO();
            masterListValueHistoryDTO.setValue1(StringUtils.EMPTY);
            masterListValueHistoryDTO.setValue2(StringUtils.EMPTY);
            workTypes.add(masterListValueHistoryDTO);
          }
        }

        InputStream newInputStream =
          templateBean.getTemplateByAttributeName(TemplateConstants.LISTE_ARBEITSGATTUNG);
        if (newInputStream != null) {
          try {
            Map<String, Object> parameters = new HashMap<>();
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(workTypes);
            parameters.put(TemplateConstants.DATASOURCE, dataSource);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            JasperCompileManager.compileReportToStream(newInputStream, byteArrayOutputStream);
            InputStream stream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(stream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
              new JRBeanCollectionDataSource(workTypes));

            outputStream = new ByteArrayOutputStream();
            JRXlsxExporter exporter = new JRXlsxExporter();
            SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
            configuration.setDetectCellType(true);
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
            exporter.setConfiguration(configuration);
            exporter.exportReport();
            outputStream.toByteArray();
          } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
          }
        }
        break;
      case Template.LISTE_WINBAU:
        documentDTO.setFilename(template.getValue1() + TemplateConstants.XLSX_FILE_EXTENSION);
        List<CompanyDTO> allCompanies = companyService.getAllCompanies();

        // In order to avoid some huge white spaces created by Jasper
        // in the end of the last page,
        // we have to create some empty values
        int compCount = allCompanies.size() % 30;
        if (compCount > 0) {
          compCount = 30 - compCount;
          for (int i = 0; i < compCount; i++) {
            CompanyDTO comp = new CompanyDTO();
            comp.setCompanyName(StringUtils.EMPTY);
            comp.setAddress1(StringUtils.EMPTY);
            comp.setPostCode(StringUtils.EMPTY);
            comp.setLocation(StringUtils.EMPTY);
            comp.setCompanyTel(StringUtils.EMPTY);
            comp.setCompanyEmail(StringUtils.EMPTY);
            allCompanies.add(comp);
          }
        }

        InputStream inputStream =
          templateBean.getTemplateByAttributeName(TemplateConstants.LISTE_WINBAU);
        if (inputStream != null) {
          try {
            Map<String, Object> parameters = new HashMap<>();
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(allCompanies);
            parameters.put(TemplateConstants.DATASOURCE, dataSource);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            JasperCompileManager.compileReportToStream(inputStream, byteArrayOutputStream);
            InputStream stream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(stream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
              new JRBeanCollectionDataSource(allCompanies));

            outputStream = new ByteArrayOutputStream();
            JRXlsxExporter exporter = new JRXlsxExporter();
            SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
            configuration.setDetectCellType(true);
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
            exporter.setConfiguration(configuration);
            exporter.exportReport();
            outputStream.toByteArray();
          } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
          }
        }
        break;
      default:
        return new byte[0];
    }
    return outputStream.toByteArray();
  }

  /**
   * Sets the width of image logo per tenant.
   *
   * @param tenant the tenant
   * @return the long
   */
  private long getLogoWidthForTenant(TenantDTO tenant) {

    LOGGER.log(Level.CONFIG,
      "Executing method getLogoWidthForTenant, Parameters: tenant: {0}",
      tenant);

    long width = 0;
    if (tenant.getName().equals(DocumentProperties.TENANT_STADT_BERN.getValue())) {
      width = TemplateConstants.SB_LOGO_WIDTH;
    }
    return width;
  }

  /**
   * Sets the file extension.
   *
   * @param templateCode the template code
   * @return the string
   */
  private String setFileExtension(String templateCode) {

    LOGGER.log(Level.CONFIG,
      "Executing method setFileExtension, Parameters: templateCode: {0}",
      templateCode);

    switch (templateCode) {
      // Word documents
      case Template.NACHWEISBRIEF_FT:
      case Template.NACHWEISBRIEF_PT:
      case Template.BEKO_ANTRAG:
      case Template.ZERTIFIKAT:
      case Template.BEKO_BESCHLUSS:
      case Template.VERFAHRENSABBRUCH:
      case Template.ANGEBOTSDECKBLATT:
      case Template.NACHWEISBRIEF_SUB:
      case Template.RECHTLICHES_GEHOR:
      case Template.VERTRAG_AUFTRAGSBESTATIGUNG_FREIHANDIG_ISB:
      case Template.VERTRAG_AUFTRAGSBESTATIGUNG_FREIHANDIG_SGB:
      case Template.VERTRAG_WERKVERTRAG_HSB:
      case Template.VERTRAG_WERKVERTRAG_ISB:
      case Template.VERTRAG_WERKVERTRAG_SGB:
      case Template.VERTRAG_DIENSTLEISTUNGSVERTRAG_LB:
      case Template.VERTRAG_KAUF_LIEFERVERTRAG_LB:
      case Template.VERTRAG_BESTELLUNG_PLANERLEISTUNGEN_SGB:
      case Template.VERTRAG_PLANERVERTRAG_SGB:
      case Template.AUSSCHLUSS:
      case Template.VERFUGUNGEN:
      case Template.SELEKTIV_1_STUFE:
      case Template.VERFUGUNGEN_DL_WETTBEWERB:
      case Template.NACHTRAG:
        return TemplateConstants.DOCX_FILE_EXTENSION;

      // Excel documents
      case Template.SUBMITTENTENLISTE_POSTLISTE:
      case Template.LISTE_ARBEITSGATTUNG:
        return TemplateConstants.XLSX_FILE_EXTENSION;

      // PDF documents
      case Template.SUBMITTENTENLISTE:
      case Template.SUBMITTENTENLISTE_ALS_ETIKETTEN:
      case Template.A_OFFERRTOFFNUNGSPROTOKOLL:
      case Template.OFFERRTOFFNUNGSPROTOKOLL:
      case Template.OFFERTVERGLEICH_FREIHANDIG:
      case Template.OFFERRTOFFNUNGSPROTOKOLL_DL_WW:
      case Template.A_OFFERRTOFFNUNGSPROTOKOLL_DL_WW:
      case Template.BEWERBER_UBERSICHT:
      case Template.SUBMISSIONSUBERSICHT:
      case Template.EIGNUNGSPRUFUNG:
      case Template.ZUSCHLAGSBEWERTUNG:
        return TemplateConstants.PDF_FILE_EXTENSION;

      default:
        return null;
    }
  }

  @Override
  public byte[] generateProjectPartDocument(DocumentDTO documentDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method generateProjectPartDocument, Parameters: documentDTO: {0}",
      new Object[]{documentDTO});

    HashMap<String, String> placeholders = new HashMap<>();

    MasterListValueHistoryEntity template = new JPAQueryFactory(em)
      .selectFrom(qMasterListValueHistoryEntity)
      .where(qMasterListValueHistoryEntity.masterListValueId.id.eq(documentDTO.getTemplateId()))
      .fetchOne();
    // submission
    SubmissionDTO submission = submissionService.getSubmissionById(documentDTO.getFolderId());

    /*
     * Retrieve the allowed templates. A document can only be generated if has been retrieved from
     * the following rule. Check the user right to in order to generate the document.
     */
    submission.setCurrentState(TenderStatus
      .fromValue(submissionService.getCurrentStatusOfSubmission(documentDTO.getFolderId())));

    // Replace Reference placeholders.
    initReferenceData(template.getShortCode(), placeholders, documentDTO);

    // list of submittents
    List<SubmittentOfferDTO> offers =
      submissionService.getCompaniesBySubmission(documentDTO.getFolderId());

    // Clear Submittent list when Selektiv process and status is not greater than
    // OFFER_OPENING_STARTED.
    if (submission.getProcess().equals(Process.SELECTIVE)
      && !compareCurrentVsSpecificStatus(submission.getCurrentState(),
      TenderStatus.SUITABILITY_AUDIT_COMPLETED_S)) {
      offers.clear();
      offers.addAll(submissionService.getApplicantsBySubmission(submission.getId()));
    }

    templateBean.replaceSubmissionPlaceholders(placeholders, submission, offers);

    List<CompanyDTO> companies = new ArrayList<>();

    for (SubmittentOfferDTO offer : offers) {
      companies.add(offer.getSubmittent().getCompanyId());
    }

    if (template.getShortCode().equals(Template.SUBMITTENTENLISTE_POSTLISTE)) {

      String fileExtension = setFileExtension(Template.SUBMITTENTENLISTE_POSTLISTE);
      documentDTO.setFilename(template.getValue1() + fileExtension);
      InputStream inputStream =
        templateBean.getTemplateByAttributeName(TemplateConstants.SUBMITTENTENLISTE_POSTLISTE);

      if (inputStream != null) {
        try {
          Map<String, Object> parameters = new HashMap<>();
          JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(companies);
          parameters.put(TemplateConstants.DATASOURCE, dataSource);
          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
          JasperCompileManager.compileReportToStream(inputStream, byteArrayOutputStream);
          InputStream stream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
          JasperReport jasperReport = (JasperReport) JRLoader.loadObject(stream);
          JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
            new JRBeanCollectionDataSource(companies));
          ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

          JRXlsxExporter exporter = new JRXlsxExporter();
          SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
          configuration.setDetectCellType(true);
          configuration.setPrintPageTopMargin(58);
          configuration.setPrintPageBottomMargin(35);
          configuration.setPrintPageLeftMargin(45);
          configuration.setPrintPageRightMargin(45);
          exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
          exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
          exporter.setConfiguration(configuration);
          exporter.exportReport();
          outputStream = templateBean.setXlsxPrintSetup(outputStream, PrintSetup.A4_PAPERSIZE);
          return outputStream.toByteArray();
        } catch (Exception e) {
          LOGGER.log(Level.SEVERE, e.getMessage());
        }
      }
    } else if (template.getShortCode().equals(Template.SUBMITTENTENLISTE_ALS_ETIKETTEN)) {
      documentDTO.setFilename(
        template.getValue1() + setFileExtension(Template.SUBMITTENTENLISTE_ALS_ETIKETTEN));
      try (InputStream inputStream = templateBean
        .getTemplateByAttributeName(TemplateConstants.SUBMITTENTENLISTE_ALS_ETIKETTEN)) {
        Map<String, Object> parameters = new HashMap<>();
        templateBean.setSubmittentenListEtikettenParameters(parameters, submission);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(companies);
        parameters.put(TemplateConstants.DATASOURCE, dataSource);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        JasperCompileManager.compileReportToStream(inputStream, byteArrayOutputStream);
        InputStream stream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(stream);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
          new JRBeanCollectionDataSource(companies));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JRPdfExporter exporter = new JRPdfExporter();
        SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
        exporter.setConfiguration(configuration);
        exporter.exportReport();
        return outputStream.toByteArray();
      } catch (Exception e) {
        LOGGER.log(Level.SEVERE, e.getMessage());
      }
    }
    return new byte[0];
  }

  /**
   * Gets the offer list.
   *
   * @param submission the submission
   * @param offers the offers
   */
  private void getOfferListForCancel(SubmissionDTO submission, List<SubmittentOfferDTO> offers) {

    LOGGER.log(Level.CONFIG,
      "Executing method getOfferList, Parameters: submission: {0}, "
        + "offers: {1}",
      new Object[]{submission, offers});

    // Clear Submittent list when Selektiv process has only Bewerber and status is not greater
    // than
    // SUITABILITY_AUDIT_COMPLETED_S.
    if (submission.getProcess().equals(Process.SELECTIVE)
      && !compareCurrentVsSpecificStatus(submission.getCurrentState(),
      TenderStatus.SUITABILITY_AUDIT_COMPLETED_S)) {
      offers.clear();
    }
    if (submission.getProcess().equals(Process.SELECTIVE)) {
      offers.addAll(submissionService.getApplicantsBySubmission(submission.getId()));
    }
    // remove empty offers or excluded applicants
    offers.removeIf(
      submittentOfferDTO -> submittentOfferDTO.getOffer().getIsEmptyOffer() ||
        Boolean.TRUE.equals(submittentOfferDTO.getOffer().getExcludedFirstLevel()));
  }

  /**
   * Gets the content of company name column.
   *
   * @param offer the offer
   * @param templateCode the template code
   * @param alphaEnum the alpha enum
   * @return the content of company name column
   */
  public String getContentOfCompanyNameColumn(OfferDTO offer, String templateCode,
    String alphaEnum) {

    LOGGER.log(Level.CONFIG,
      "Executing method getContentOfCompanyNameColumn, Parameters: offer: {0}, "
        + "templateCode: {1}, alphaEnum: {2}",
      new Object[]{offer, templateCode, alphaEnum});

    if (templateCode.equals(Template.OFFERTVERGLEICH_FREIHANDIG)
      || templateCode.equals(Template.OFFERRTOFFNUNGSPROTOKOLL_DL_WW)
      || templateCode.equals(Template.OFFERRTOFFNUNGSPROTOKOLL)
      || templateCode.equals(Template.BEWERBER_UBERSICHT)) {
      return getCompanyNameTemplatePT12(offer);
    } else if (templateCode.equals(Template.A_OFFERRTOFFNUNGSPROTOKOLL)) {
      return alphaEnum;
    } else {
      return getCompanyNameTemplatePT08(offer);
    }
  }

  /**
   * Content of company column on Documents table when template code is PT12.
   *
   * @param offer the offer
   * @return the company name template PT 12 and PT 10
   */
  private String getCompanyNameTemplatePT12(OfferDTO offer) {

    LOGGER.log(Level.CONFIG,
      "Executing method getCompanyNameTemplatePT12, Parameters: offer: {0}",
      offer);

    StringBuilder companyColumn = new StringBuilder();

    /* create a default StringBuilder for main company's name */
    StringBuilder mainCompany = new StringBuilder();
    mainCompany.append(offer.getSubmittent().getCompanyId().getCompanyName()).append(", ")
      .append(offer.getSubmittent().getCompanyId().getLocation());

    /* create a default StringBuilder for the jointVentures */
    StringBuilder jointVentures = new StringBuilder();
    for (CompanyDTO jointVenture : offer.getSubmittent().getJointVentures()) {
      jointVentures.append(" / ").append(jointVenture.getCompanyName()).append(", ")
        .append(jointVenture.getLocation());
    }
    /* create a default StringBuilder for the sub contarctors */
    StringBuilder subContractors = new StringBuilder();
    for (CompanyDTO subcontractor : offer.getSubmittent().getSubcontractors()) {
      subContractors.append(subcontractor.getCompanyName()).append(", ")
        .append(subcontractor.getLocation()).append(" / ");
    }

    /** When no joint ventures and no sub Contractors */
    if (offer.getSubmittent().getJointVentures().isEmpty()
      && offer.getSubmittent().getSubcontractors().isEmpty()) {
      companyColumn.append(new StringBuilder(mainCompany));
    }

    /* When there are Joint ventures and no Sub Contractors */
    else if (!offer.getSubmittent().getJointVentures().isEmpty()
      && offer.getSubmittent().getSubcontractors().isEmpty()) {
      companyColumn.append(TemplateConstants.ARGE).append(new StringBuilder(mainCompany));
      companyColumn.append(new StringBuilder(jointVentures));

    }

    /** When sub contractors and no Joint ventures */
    else if (offer.getSubmittent().getJointVentures().isEmpty()
      && !offer.getSubmittent().getSubcontractors().isEmpty()) {
      companyColumn.append(new StringBuilder(mainCompany));
      companyColumn.append(" (").append(new StringBuilder(subContractors));
    }

    // when both joint ventures and sub contractors
    else {
      companyColumn.append(TemplateConstants.ARGE)
        .append(new StringBuilder(mainCompany).append(new StringBuilder(jointVentures)));
      companyColumn.append(" (").append(new StringBuilder(subContractors));
    }
    if (companyColumn.toString().endsWith(" / ")) {
      companyColumn.delete(companyColumn.length() - 3, companyColumn.length());
      companyColumn.append(")");
    }

    return companyColumn.toString();
  }

  /**
   * Content of company column on Documents table when template code is PT08.
   *
   * @param offer the offer
   * @return the company name template PT 08
   */
  private String getCompanyNameTemplatePT08(OfferDTO offer) {

    LOGGER.log(Level.CONFIG,
      "Executing method getCompanyNameTemplatePT08, Parameters: offer: {0}",
      offer);

    if (offer.getSubmittent().getJointVentures().isEmpty()
      && offer.getSubmittent().getSubcontractors().isEmpty()) {
      return offer.getSubmittent().getCompanyId().getCompanyName() + ", "
        + offer.getSubmittent().getCompanyId().getLocation();
    } else {
      StringBuilder companyNameStr = new StringBuilder();
      companyNameStr.append(offer.getSubmittent().getCompanyId().getCompanyName());
      if (!offer.getSubmittent().getJointVentures().isEmpty()) {
        for (CompanyDTO jointVenture : offer.getSubmittent().getJointVentures()) {
          companyNameStr.append(", ").append(jointVenture.getCompanyName()).append(" (ARGE)");
        }
      }
      if (!offer.getSubmittent().getSubcontractors().isEmpty()) {
        for (CompanyDTO subcontractor : offer.getSubmittent().getSubcontractors()) {
          companyNameStr.append(", ").append(subcontractor.getCompanyName()).append(" (Sub U.)");
        }
      }
      return companyNameStr.toString();
    }
  }

  /**
   * Function to create xlsx document and return it in bytes using TemplateService function.
   *
   * @param shortCode the short code
   * @return the byte[]
   */
  private byte[] generateCompanyPartDocumentExcel(String shortCode) {

    LOGGER.log(Level.CONFIG,
      "Executing method generateCompanyPartDocumentExcel, Parameters: "
        + "shortCode: {0}",
      shortCode);

    if (shortCode.equals(Template.COMPANIES_PER_TENANT)) {
      List<CompanyDTO> companies = companyService.getAllNonMigratedCompanies();
      Collections.sort(companies, ComparatorUtil.sortCompaniesDTOByCompanyName);

      // Adding comma to the company name (used to separate the company name from the location in
      // the document).
      for (CompanyDTO company : companies) {
        company.setCompanyName(company.getCompanyName() + ", ");
      }

      // Creating companies with empty values (used for the page break).
      while (companies.size() % 52 != 0) {
        CompanyDTO company = new CompanyDTO();
        company.setModUserTenant(StringUtils.EMPTY);
        company.setCompanyName(StringUtils.EMPTY);
        company.setLocation(StringUtils.EMPTY);
        companies.add(company);
      }

      InputStream inputStream =
        templateBean.getTemplateByAttributeName(TemplateConstants.FIRMEN_PRO_MANDNT);
      if (inputStream != null) {
        try {
          Map<String, Object> parameters = new HashMap<>();
          JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(companies);
          parameters.put(TemplateConstants.DATASOURCE, dataSource);
          parameters.put("companiesCount", companies.size());
          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
          JasperCompileManager.compileReportToStream(inputStream, byteArrayOutputStream);
          InputStream stream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
          JasperReport jasperReport = (JasperReport) JRLoader.loadObject(stream);
          JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
            new JRBeanCollectionDataSource(companies));
          ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
          JRXlsxExporter exporter = new JRXlsxExporter();
          SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
          configuration.setDetectCellType(true);
          configuration.setPrintPageTopMargin(55);
          configuration.setPrintPageBottomMargin(55);
          configuration.setPrintPageLeftMargin(51);
          configuration.setPrintPageRightMargin(51);
          configuration.setPrintHeaderMargin(22);
          configuration.setPrintFooterMargin(22);
          exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
          exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
          exporter.setConfiguration(configuration);
          exporter.exportReport();
          return outputStream.toByteArray();
        } catch (Exception e) {
          LOGGER.log(Level.SEVERE, e.getMessage());
        }
      }
    }
    return new byte[0];
  }

  /**
   * Sets the signature.
   *
   * @param documentDTO  the document DTO
   * @param placeholders the placeholders
   */
  private void setSignature(DocumentDTO documentDTO, Map<String, String> placeholders) {

    LOGGER.log(Level.CONFIG,
      "Executing method setSignature, Parameters: documentDTO: {0}, "
        + "placeholders: {1}",
      new Object[]{documentDTO, placeholders});

    // Get all active department history dtos from cacheBean
    Map<String, DepartmentHistoryDTO> departmentHistoryDTOs =
      cacheBean.getActiveDepartmentHistorySD();

    initializeSignatureReferenceDepartment(documentDTO, placeholders, departmentHistoryDTOs);
    initializeSignatureCopy(placeholders, documentDTO.getSignatureCopies(), departmentHistoryDTOs);
    initializeSignatureReferencePerson(documentDTO.getFirstSignature(),
      documentDTO.getSecondSignature(), placeholders);
  }

  /**
   * Initialize the signature department.
   *
   * @param documentDTO           the documentDTO
   * @param placeholders          the placeholders
   * @param departmentHistoryDTOs the departmentHistoryDTOs
   */
  private void initializeSignatureReferenceDepartment(DocumentDTO documentDTO,
    Map<String, String> placeholders, Map<String, DepartmentHistoryDTO> departmentHistoryDTOs) {

    LOGGER.log(Level.CONFIG,
      "Executing method initializeSignatureReferenceDepartment, Parameters: "
        + "documentDTO: {0}, placeholders: {1}, departmentHistoryDTOs: {2}",
      new Object[]{documentDTO, placeholders, departmentHistoryDTOs});

    DepartmentHistoryDTO projectDepartment = departmentHistoryDTOs
      .get(getDocumentDepartment(documentDTO));
    DepartmentHistoryDTO signatureDepartment = null;
    // Find the signature department for the reference placeholders
    if (documentDTO.getFirstSignature() != null
      && documentDTO.getFirstSignature().getDepartment() != null) {
      signatureDepartment = departmentHistoryDTOs
        .get(documentDTO.getFirstSignature().getDepartment().getId());
    }
    templateBean
      .setUserAttributesPlaceholders(projectDepartment, projectDepartment.getDirectorate(),
        placeholders, signatureDepartment);
  }

  /**
   * Initialize signature reference person.
   *
   * @param firstSignature  the first signature
   * @param secondSignature the second signature
   * @param placeholders    the placeholders
   */
  private void initializeSignatureReferencePerson(SignatureProcessTypeEntitledDTO firstSignature,
    SignatureProcessTypeEntitledDTO secondSignature, Map<String, String> placeholders) {

    LOGGER.log(Level.CONFIG,
      "Executing method initializeSignatureReferencePerson, Parameters: "
        + "firstSignature: {0}, secondSignature: {1}, placeholders: {2}",
      new Object[]{firstSignature, secondSignature, placeholders});

    if (firstSignature != null) {
      placeholders.put(DocumentPlaceholders.FIRST_REFERENCE_NAME.getValue(),
        (firstSignature.getName() != null)
          ? firstSignature.getName()
          : TemplateConstants.EMPTY_STRING);
      placeholders.put(DocumentPlaceholders.FIRST_REFERENCE_FUNCTION.getValue(),
        (firstSignature.getFunction() != null)
          ? firstSignature.getFunction()
          : TemplateConstants.EMPTY_STRING);
    } else {
      placeholders
        .put(DocumentPlaceholders.FIRST_REFERENCE_NAME.getValue(), TemplateConstants.EMPTY_STRING);
      placeholders.put(DocumentPlaceholders.FIRST_REFERENCE_FUNCTION.getValue(),
        TemplateConstants.EMPTY_STRING);
    }

    if (secondSignature != null) {
      placeholders.put(DocumentPlaceholders.SECOND_REFERENCE_NAME.getValue(),
        (secondSignature.getName() != null)
          ? secondSignature.getName()
          : TemplateConstants.EMPTY_STRING);
      placeholders.put(DocumentPlaceholders.SECOND_REFERENCE_FUNCTION.getValue(),
        (secondSignature.getFunction() != null)
          ? secondSignature.getFunction()
          : TemplateConstants.EMPTY_STRING);
    } else {
      placeholders
        .put(DocumentPlaceholders.SECOND_REFERENCE_NAME.getValue(), TemplateConstants.EMPTY_STRING);
      placeholders.put(DocumentPlaceholders.SECOND_REFERENCE_FUNCTION.getValue(),
        TemplateConstants.EMPTY_STRING);
    }
  }

  /**
   * Initialize signature copy.
   *
   * @param placeholders          the placeholders
   * @param signatureCopies       the signature copies
   * @param departmentHistoryDTOs the departmentHistoryDTOs
   */
  private void initializeSignatureCopy(Map<String, String> placeholders,
    List<SignatureCopyDTO> signatureCopies,
    Map<String, DepartmentHistoryDTO> departmentHistoryDTOs) {

    LOGGER.log(Level.CONFIG,
      "Executing method initializeSignatureCopy, Parameters: placeholders: {0}, "
        + "signatureCopies: {1}, departmentHistoryDTOs: {2}",
      new Object[]{placeholders, signatureCopies, departmentHistoryDTOs});

    if (signatureCopies != null && !signatureCopies.isEmpty()) {
      StringBuilder copyPlaceholder = new StringBuilder();
      for (SignatureCopyDTO signatureCopy : signatureCopies) {
        // Check if copy department exists.
        if (signatureCopy.getDepartment() != null) {
          DepartmentHistoryDTO copyDepartment = departmentHistoryDTOs
            .get(signatureCopy.getDepartment().getId());
          copyPlaceholder.append(copyDepartment.getName()).append(", ");
        }
      }
      placeholders.put(DocumentPlaceholders.COPY_REFERENCE.getValue(),
        (copyPlaceholder.length() > 1)
          ? copyPlaceholder.substring(0, copyPlaceholder.length() - 2)
          : TemplateConstants.EMPTY_STRING);
    } else {
      placeholders.put(DocumentPlaceholders.COPY_REFERENCE.getValue(),
        TemplateConstants.EMPTY_STRING);
    }
  }

  /**
   * if task PROOF_REQUEST exists, Settle this Task and create the next one PROOF_REQUEST_PL_XY.
   *
   * @param submissionID the submission ID
   * @param companyID the company ID
   * @return true if task settled
   */
  private boolean settleProofRequestTask(String submissionID, String companyID, Date submitDate) {

    LOGGER.log(Level.CONFIG,
      "Executing method settleProofRequestTask, Parameters: "
        + "submissionID: {0}, companyID: {1}, submitDate: {2}",
      new Object[]{submissionID, companyID, submitDate});

    return taskService
      .settleTask(submissionID, companyID, TaskTypes.PROOF_REQUEST, null, submitDate);
  }

  @Override
  public List<DocumentDTO> getDocumentVersionsByFilename(String folderId,
    List<String> fileNameList) {

    LOGGER.log(Level.CONFIG,
      "Executing method getDocumentVersionsByFilename, Parameters: folderId: {0}, "
        + "fileNameList: {1}",
      new Object[]{folderId, fileNameList});

    List<VersionDTO> versionDTOList =
      versionService.getVersionsByFilenameForFile(folderId, fileNameList);
    List<DocumentDTO> documentDTOList = new ArrayList<>();
    UserDTO userDTO = getUser();
    String usersGroup = getGroupName(userDTO);
    List<String> permittedDeps = null;
    if (usersGroup.equals(Group.SB.getValue())) {
      permittedDeps = getDepartments(userDTO);
    } else if (!usersGroup.equals(Group.ADMIN.getValue())) {
      permittedDeps = security.getPermittedDepartments(userDTO);
    }
    for (VersionDTO versionDTO : versionDTOList) {
      NodeDTO nodeDTO = documentService.getNodeByID(versionDTO.getNodeId());
      if (userDTO.getAttribute(USER_ATTRIBUTES.TENANT.getValue()) != null
        && userDTO.getAttribute(USER_ATTRIBUTES.TENANT.getValue()).getData()
        .equals(nodeDTO.getAttributes().get(DocumentAttributes.TENANT_ID.name()))) {
        if (!usersGroup.equals(Group.ADMIN.getValue())) {
          if (nodeDTO.getAttributes().containsKey(DocumentAttributes.PRIVATE_DOCUMENT.name())
            && nodeDTO.getAttributes().get(DocumentAttributes.PRIVATE_DOCUMENT.name())
            .equals(Boolean.TRUE.toString())) {
            if (nodeDTO.getAttributes()
              .get(DocumentAttributes.PRIVATE_DEPARTMENTS.name()) != null) {
              List<String> departmentIds = Arrays
                .asList(nodeDTO.getAttributes().get(DocumentAttributes.PRIVATE_DEPARTMENTS.name())
                  .split(TemplateConstants.DOC_SPLIT_STRING));
              for (String id : departmentIds) {
                if (permittedDeps.contains(id)) {
                  documentDTOList.add(DocumentMapper.INSTANCE.toDocumentDTO(versionDTO));
                  break;
                }
              }
            }
          } else {
            documentDTOList.add(DocumentMapper.INSTANCE.toDocumentDTO(versionDTO));
          }
        } else {
          documentDTOList.add(DocumentMapper.INSTANCE.toDocumentDTO(versionDTO));
        }
      }
    }

    return documentDTOList;
  }

  @Override
  public boolean isDeletionReasonMandatory(String templateId) {

    LOGGER.log(Level.CONFIG,
      "Executing method isDeletionReasonMandatory, Parameters: templateId: {0}",
      templateId);

    String template =
      new JPAQueryFactory(em).select(qMasterListValueHistoryEntity.shortCode)
        .from(qMasterListValueHistoryEntity)
        .where(
          qMasterListValueHistoryEntity.masterListValueId.id.eq(templateId)
            .and(qMasterListValueHistoryEntity.tenant.id
              .eq(getUser().getAttributeData(USER_ATTRIBUTES.TENANT.getValue()))))
        .fetchOne();
    // Template is null when a file has been uploaded . An uploaded file can be deleted without
    // entering a reason
    if (template == null) {
      return false;
    }
    return (template.equals(Template.VERFAHRENSABBRUCH) || template.equals(Template.AUSSCHLUSS)
      || template.equals(Template.VERFUGUNGEN) || template.equals(Template.SELEKTIV_1_STUFE)
      || template.equals(Template.VERFUGUNGEN_DL_WETTBEWERB));
  }

  /**
   * Audit log.
   *
   * @param level the level
   * @param event the event
   * @param auditMessage the audit message
   * @param submissionId the submission id
   */
  private void auditLog(String level, String event, String auditMessage,
    String submissionId, String additionalInfo) {

    LOGGER.log(Level.CONFIG, "Executing method auditLog, Parameters: level: {0}, "
        + "event: {1}, auditMessage: {2}, submissionId: {3}, additionalInfo: {4}",
      new Object[]{level, event, auditMessage, submissionId, additionalInfo});

    audit.createLogAudit(level, event, AuditGroupName.DOCUMENT.name(), auditMessage,
      getUser().getId(), submissionId, null, additionalInfo, LookupValues.EXTERNAL_LOG);
  }

  /**
   * Get the departments of the user.
   *
   * @param userDTO The dto of the user
   * @return A list of the departments of the user
   */
  private List<String> getDepartments(UserDTO userDTO) {

    LOGGER.log(Level.CONFIG,
      "Executing method getDepartments, Parameters: userDTO: {0}",
      userDTO);

    List<String> departments = new ArrayList<>();
    if (userDTO.getAttribute(USER_ATTRIBUTES.SAML_DEPARTMENT.getValue()) != null) {
      String department =
        userDTO.getAttribute(USER_ATTRIBUTES.SAML_DEPARTMENT.getValue()).getData();
      departments.add(department);
    }
    // and subdepartments
    List<String> secondaryDepartmentIds = usersService
      .getAllSecondaryDepartments(userDTO);
    for (String secondaryDepartmentId : secondaryDepartmentIds) {
      departments.add(secondaryDepartmentId);
    }
    return departments;
  }

  @Override
  public boolean documentExists(String submissionId, String templateShortCode) {

    LOGGER.log(Level.CONFIG,
      "Executing method documentExists, Parameters: submissionId: {0}, "
        + "templateShortCode: {1}",
      new Object[]{submissionId, templateShortCode});

    Map<String, String> map = new HashMap<>();
    String tenantId = usersService.getTenant().getId();
    String templateId = sDService.getTemplateIdByShortCode(templateShortCode, tenantId);
    map.put(DocumentAttributes.TEMPLATE_ID.name(), templateId);
    List<NodeDTO> nodeDTOs = documentService.getNodeByAttributes(submissionId, map);
    return !nodeDTOs.isEmpty();
  }

  @Override
  public boolean legalHearingTerminateDocumentExists(String submissionId,
    String templateShortCode) {

    LOGGER.log(Level.CONFIG,
      "Executing method legalHearingTerminateDocumentExists, Parameters: submissionId: {0}, "
        + "templateShortCode: {1}",
      new Object[]{submissionId, templateShortCode});

    Map<String, String> map = new HashMap<>();
    String tenantId = usersService.getTenant().getId();
    String templateId = sDService.getTemplateIdByShortCode(templateShortCode, tenantId);
    map.put(DocumentAttributes.TEMPLATE_ID.name(), templateId);
    List<NodeDTO> nodeDTOs = documentService.getNodeByAttributes(submissionId, map);
    return nodeDTOs
      .stream()
      .anyMatch(nodeDTO -> nodeDTO.getName().contains(TEMPLATE_NAMES.RECHTLICHES_GEHOR_AB.getValue()));
  }

  /**
   * Gets the logo.
   *
   * @param submissionId the submission id
   * @param placeholders the placeholders
   * @return the logo
   */
  private byte[] getLogo(String submissionId, Map<String, String> placeholders) {

    LOGGER.log(Level.CONFIG,
      "Executing method getLogo, Parameters: submissionId: {0}, placeholders: {1} ",
      new Object[]{submissionId, placeholders});

    byte[] logo = null;
    // When creating a document in the project part, get the logo from the department (if the
    // department has its own logo). In any other case get the tenant logo.
    if (submissionId != null) {
      SubmissionEntity submissionEntity = em.find(SubmissionEntity.class, submissionId);
      if (submissionEntity != null
        && submissionEntity.getProject().getDepartmentHistory().getLogo() != null) {
        logo = submissionEntity.getProject().getDepartmentHistory().getLogo();
      } else {
        logo = sDService.getTenantLogo();
      }
    } else {
      logo = sDService.getTenantLogo();
    }
    if (logo == null) {
      placeholders.put("sb_logo", "");
    }
    return logo;
  }

  @Override
  public List<String> permittedDocumentsForUpload(String folderId, List<String> filenames,
    boolean isProjectPart) {

    LOGGER.log(Level.CONFIG,
      "Executing method permittedDocumentsForUpload, Parameters: folderId: {0}, "
        + "filenames: {1}, isProjectPart: {2} ",
      new Object[]{folderId, filenames, isProjectPart});

    // The list with the permitted templates for upload
    List<String> templatesList = new ArrayList<>();
    // permitted upload templates list
    List<String> putList = new ArrayList<>();
    // permitted delete templates list
    List<String> pdtList = new ArrayList<>();

    if (isProjectPart) {
      SubmissionDTO submissionDTO = submissionService.getSubmissionById(folderId);
      submissionDTO.setCurrentState(TenderStatus.fromValue(submissionDTO.getStatus()));

      /* get the template types that can be uploaded by the user */
      List<MasterListValueHistoryDTO> permittedUploadTemplateDTOs =
        ruleService.getProjectUploadAllowedTemplates();

      /* get the template types that can be deleted by the user */
      List<MasterListValueHistoryDTO> permittedDeleteTemplateDTOs =
        ruleService.getProjectAllowedTemplates(submissionDTO);
      permittedDeleteTemplateDTOs.addAll(ruleService.getProjectDepartmentTemplates(submissionDTO));

      /* Get all possible upload templates of system and remove those that must not be uploaded  */

      /* fill lists with templates' names removing the extension */
      for (MasterListValueHistoryDTO put : permittedUploadTemplateDTOs) {
        putList.add(FilenameUtils.removeExtension(put.getValue1()));
      }
      for (MasterListValueHistoryDTO pdt : permittedDeleteTemplateDTOs) {
        pdtList.add(FilenameUtils.removeExtension(pdt.getValue1()));
      }
      /* putList now has all not accepted templates */
      putList.removeAll(pdtList);

      for (int i = 0; i < filenames.size(); i++) {
        if (putList.contains(FilenameUtils.removeExtension(filenames.get(i)))) {
          continue;
        } else {
          templatesList.add(FilenameUtils.removeExtension(filenames.get(i)));
        }
      }
    }
    return templatesList;
  }

  /**
   * Retrieve the creation date of the Rechtliches Gehör (Abbruch) document
   *
   * @param submission the submission
   */
  private void retrieveRechtlichesDate(SubmissionDTO submission) {

    LOGGER.log(Level.CONFIG,
      "Executing method retrieveRechtlichesDate, Parameters: submission: {0}, ",
      submission);

    if (submission != null) {
      // Retrieve all created documents of this submission
      Set<NodeDTO> documents = documentService.getFolderByID(submission.getId(), false, false)
        .getChildren();

      /*
       * The placeholder r_date should contain the last_modified_on date
       * of the Rechtliches Gehör (Abbruch) document
       */
      documents.stream()
        .filter(document ->
          document.getName().contains(TEMPLATE_NAMES.RECHTLICHES_GEHOR_AB.getValue()))
        .forEach(document ->
          submission.getLegalHearingTerminate().get(0)
            .setUpdatedOn(new Timestamp(document.getLastModifiedOn())));
    }
  }
}
