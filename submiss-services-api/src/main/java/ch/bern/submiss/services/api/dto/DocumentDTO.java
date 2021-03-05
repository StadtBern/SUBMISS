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

package ch.bern.submiss.services.api.dto;

import ch.bern.submiss.services.api.constants.DocumentCreationType;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * The Class DocumentDTO.
 */
public class DocumentDTO {

  /**
   * The id.
   */
  private String id;

  /**
   * The name.
   */
  private String title;

  /**
   * The tenderer.
   */
  private String tenantId;

  /**
   * The filename.
   */
  private String filename;

  /**
   * The document creation type.
   */
  private DocumentCreationType documentCreationType;

  /**
   * The created on.
   */
  private Date createdOn;

  /**
   * The last modified on.
   */
  private Date lastModifiedOn;

  /**
   * The version.
   */
  private String version;

  /**
   * The created by.
   */
  private String createdBy;

  /**
   * The active.
   */
  private Boolean active;

  /**
   * The mimetype.
   */
  private String mimetype;

  /**
   * The global.
   */
  private Boolean global;

  /**
   * The submission id.
   */
  private String folderId;

  /**
   * The template id.
   */
  private String templateId;

  /**
   * The node id.
   */
  private String nodeId;

  /**
   * The create version.
   */
  private boolean createVersion;

  /**
   * The private document.
   */
  private Boolean privateDocument;

  /**
   * The group of the user that set the document to private.
   */
  private String privateGroup;

  /**
   * The can be downloaded flag.
   */
  private Boolean canBeDownloaded;

  /**
   * The can be uploaded flag.
   */
  private Boolean canBeUploaded;

  /**
   * The can properties be edited flag.
   */
  private Boolean canPropertiesBeEdited;

  /**
   * The can be deleted flag.
   */
  private Boolean canBeDeleted;

  /**
   * The can be printed flag.
   */
  private Boolean canBePrinted;

  /**
   * The is admin rights only flag.
   */
  private Boolean isAdminRightsOnly;

  /**
   * The submit date.
   */
  private Date submitDate;

  /**
   * The generate proof template.
   */
  private Boolean generateProofTemplate;

  /**
   * The procurement.
   */
  private String procurement;

  /**
   * The department.
   */
  private DepartmentHistoryDTO department;

  /**
   * The is company certificate.
   */
  private Boolean isCompanyCertificate;

  /**
   * The dept amounts.
   */
  private BigDecimal deptAmounts;

  /**
   * The expiration date.
   */
  private Date expirationDate;

  /**
   * The submitent name.
   */
  private String submitentName;

  /**
   * The project document.
   */
  private boolean projectDocument;

  /**
   * The user departments.
   */
  private List<DepartmentHistoryDTO> userDepartments;

  /**
   * The legal hearing type.
   */
  private String legalHearingType;

  /**
   * The submittents.
   */
  private List<LegalHearingExclusionDTO> legalHearingExclusion;

  /**
   * The first signature.
   */
  private SignatureProcessTypeEntitledDTO firstSignature;

  /**
   * The second signature.
   */
  private SignatureProcessTypeEntitledDTO secondSignature;

  /**
   * The signature copies.
   */
  private List<SignatureCopyDTO> signatureCopies;

  /**
   * The dept amount action.
   */
  private String deptAmountAction;

  /**
   * The Nachtrag Id.
   */
  private String nachtragId;

  /**
   * The Nachtrag Submittent Id.
   */
  private String nachtragSubmittentId;


  /**
   * The Nachtrag Company Id.
   */
  private String nachtragCompanyId;

  /**
   * The version name as Integer.
   */
  private int versionName;

  /**
   * Gets the version name.
   *
   * @return the versionName
   */
  public int getVersionName() {
    return versionName;
  }

  /**
   * Sets the version name.
   *
   * @param versionName the new id
   */
  public void setVersionName(int versionName) {
    this.versionName = versionName;
  }

  /**
   * Gets the id.
   *
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the id.
   *
   * @param id the new id
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the title.
   *
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the title.
   *
   * @param title the new title
   */
  public void setTitle(String title) {
    this.title = title;
  }

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
   * Gets the filename.
   *
   * @return the filename
   */
  public String getFilename() {
    return filename;
  }

  /**
   * Sets the filename.
   *
   * @param filename the new filename
   */
  public void setFilename(String filename) {
    this.filename = filename;
  }

  /**
   * Gets the document creation type.
   *
   * @return the document creation type
   */
  public DocumentCreationType getDocumentCreationType() {
    return documentCreationType;
  }

  /**
   * Sets the document creation type.
   *
   * @param documentCreationType the new document creation type
   */
  public void setDocumentCreationType(DocumentCreationType documentCreationType) {
    this.documentCreationType = documentCreationType;
  }

  /**
   * Gets the created on.
   *
   * @return the created on
   */
  public Date getCreatedOn() {
    return createdOn;
  }

  /**
   * Sets the created on.
   *
   * @param createdOn the new created on
   */
  public void setCreatedOn(Date createdOn) {
    this.createdOn = createdOn;
  }

  /**
   * Gets the last modified on.
   *
   * @return the last modified on
   */
  public Date getLastModifiedOn() {
    return lastModifiedOn;
  }

  /**
   * Sets the last modified on.
   *
   * @param lastModifiedOn the new last modified on
   */
  public void setLastModifiedOn(Date lastModifiedOn) {
    this.lastModifiedOn = lastModifiedOn;
  }

  /**
   * Gets the version.
   *
   * @return the version
   */
  public String getVersion() {
    return version;
  }

  /**
   * Sets the version.
   *
   * @param version the new version
   */
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * Gets the created by.
   *
   * @return the created by
   */
  public String getCreatedBy() {
    return createdBy;
  }

  /**
   * Sets the created by.
   *
   * @param createdBy the new created by
   */
  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  /**
   * Gets the active.
   *
   * @return the active
   */
  public Boolean getActive() {
    return active;
  }

  /**
   * Sets the active.
   *
   * @param active the new active
   */
  public void setActive(Boolean active) {
    this.active = active;
  }

  /**
   * Gets the mimetype.
   *
   * @return the mimetype
   */
  public String getMimetype() {
    return mimetype;
  }

  /**
   * Sets the mimetype.
   *
   * @param mimetype the new mimetype
   */
  public void setMimetype(String mimetype) {
    this.mimetype = mimetype;
  }

  /**
   * Gets the global.
   *
   * @return the global
   */
  public Boolean getGlobal() {
    return global;
  }

  /**
   * Sets the global.
   *
   * @param global the new global
   */
  public void setGlobal(Boolean global) {
    this.global = global;
  }

  /**
   * Gets the folder id.
   *
   * @return the folder id
   */
  public String getFolderId() {
    return folderId;
  }

  /**
   * Sets the folder id.
   *
   * @param folderId the new folder id
   */
  public void setFolderId(String folderId) {
    this.folderId = folderId;
  }

  /**
   * Gets the template id.
   *
   * @return the template id
   */
  public String getTemplateId() {
    return templateId;
  }

  /**
   * Sets the template id.
   *
   * @param templateId the new template id
   */
  public void setTemplateId(String templateId) {
    this.templateId = templateId;
  }

  /**
   * Checks if is creates the version.
   *
   * @return true, if is creates the version
   */
  public boolean isCreateVersion() {
    return createVersion;
  }

  /**
   * Sets the creates the version.
   *
   * @param createVersion the new creates the version
   */
  public void setCreateVersion(boolean createVersion) {
    this.createVersion = createVersion;
  }

  /**
   * Gets the node id.
   *
   * @return the node id
   */
  public String getNodeId() {
    return nodeId;
  }

  /**
   * Sets the node id.
   *
   * @param nodeId the new node id
   */
  public void setNodeId(String nodeId) {
    this.nodeId = nodeId;
  }

  /**
   * Checks if is private document.
   *
   * @return the boolean
   */
  public Boolean isPrivateDocument() {
    return privateDocument;
  }

  /**
   * Sets the private document.
   *
   * @param privateDocument the new private document
   */
  public void setPrivateDocument(Boolean privateDocument) {
    this.privateDocument = privateDocument;
  }

  /**
   * Gets the can be downloaded.
   *
   * @return the can be downloaded
   */
  public Boolean getCanBeDownloaded() {
    return canBeDownloaded;
  }

  /**
   * Sets the can be downloaded.
   *
   * @param canBeDownloaded the new can be downloaded
   */
  public void setCanBeDownloaded(Boolean canBeDownloaded) {
    this.canBeDownloaded = canBeDownloaded;
  }

  /**
   * Gets the can be uploaded.
   *
   * @return the can be uploaded
   */
  public Boolean getCanBeUploaded() {
    return canBeUploaded;
  }

  /**
   * Sets the can be uploaded.
   *
   * @param canBeUploaded the new can be uploaded
   */
  public void setCanBeUploaded(Boolean canBeUploaded) {
    this.canBeUploaded = canBeUploaded;
  }

  /**
   * Gets the can be edited.
   *
   * @return the can be edited
   */
  public Boolean getCanPropertiesBeEdited() {
    return canPropertiesBeEdited;
  }

  /**
   * Sets the can be edited.
   *
   * @param canPropertiesBeEdited the new can properties be edited
   */
  public void setCanPropertiesBeEdited(Boolean canPropertiesBeEdited) {
    this.canPropertiesBeEdited = canPropertiesBeEdited;
  }

  /**
   * Gets the can be deleted.
   *
   * @return the can be deleted
   */
  public Boolean getCanBeDeleted() {
    return canBeDeleted;
  }

  /**
   * Sets the can be deleted.
   *
   * @param canBeDeleted the new can be deleted
   */
  public void setCanBeDeleted(Boolean canBeDeleted) {
    this.canBeDeleted = canBeDeleted;
  }


  /**
   * Gets the can be printed.
   *
   * @return the can be printed
   */
  public Boolean getCanBePrinted() {
    return canBePrinted;
  }

  /**
   * Sets the can be printed.
   *
   * @param canBePrinted the new can be printed
   */
  public void setCanBePrinted(Boolean canBePrinted) {
    this.canBePrinted = canBePrinted;
  }

  /**
   * Gets the checks if is admin rights only.
   *
   * @return the checks if is admin rights only
   */
  public Boolean getIsAdminRightsOnly() {
    return isAdminRightsOnly;
  }

  /**
   * Sets the checks if is admin rights only.
   *
   * @param isAdminRightsOnly the new checks if is admin rights only
   */
  public void setIsAdminRightsOnly(Boolean isAdminRightsOnly) {
    this.isAdminRightsOnly = isAdminRightsOnly;
  }

  /**
   * Gets the submit date.
   *
   * @return the submit date
   */
  public Date getSubmitDate() {
    return submitDate;
  }

  /**
   * Sets the submit date.
   *
   * @param submitDate the new submit date
   */
  public void setSubmitDate(Date submitDate) {
    this.submitDate = submitDate;
  }

  /**
   * Gets the generate proof template.
   *
   * @return the generate proof template
   */
  public Boolean getGenerateProofTemplate() {
    return generateProofTemplate;
  }

  /**
   * Sets the generate proof template.
   *
   * @param generateProofTemplate the new generate proof template
   */
  public void setGenerateProofTemplate(Boolean generateProofTemplate) {
    this.generateProofTemplate = generateProofTemplate;
  }

  /**
   * Gets the procurement.
   *
   * @return the procurement
   */
  public String getProcurement() {
    return procurement;
  }

  /**
   * Sets the procurement.
   *
   * @param procurement the new procurement
   */
  public void setProcurement(String procurement) {
    this.procurement = procurement;
  }

  /**
   * Gets the department.
   *
   * @return the department
   */
  public DepartmentHistoryDTO getDepartment() {
    return department;
  }

  /**
   * Sets the department.
   *
   * @param department the new department
   */
  public void setDepartment(DepartmentHistoryDTO department) {
    this.department = department;
  }

  /**
   * Gets the checks if is company certificate.
   *
   * @return the checks if is company certificate
   */
  public Boolean getIsCompanyCertificate() {
    return isCompanyCertificate;
  }

  /**
   * Sets the checks if is company certificate.
   *
   * @param isCompanyCertificate the new checks if is company certificate
   */
  public void setIsCompanyCertificate(Boolean isCompanyCertificate) {
    this.isCompanyCertificate = isCompanyCertificate;
  }


  /**
   * Gets the dept amounts.
   *
   * @return the dept amounts
   */
  public BigDecimal getDeptAmounts() {
    return deptAmounts;
  }

  /**
   * Sets the dept amounts.
   *
   * @param deptAmounts the new dept amounts
   */
  public void setDeptAmounts(BigDecimal deptAmounts) {
    this.deptAmounts = deptAmounts;
  }

  /**
   * Gets the expiration date.
   *
   * @return the expiration date
   */
  public Date getExpirationDate() {
    return expirationDate;
  }

  /**
   * Sets the expiration date.
   *
   * @param expirationDate the new expiration date
   */
  public void setExpirationDate(Date expirationDate) {
    this.expirationDate = expirationDate;
  }

  /**
   * Gets the submitent name.
   *
   * @return the submitent name
   */
  public String getSubmitentName() {
    return submitentName;
  }

  /**
   * Sets the submitent name.
   *
   * @param submitentName the new submitent name
   */
  public void setSubmitentName(String submitentName) {
    this.submitentName = submitentName;
  }

  /**
   * Checks if is project document.
   *
   * @return true, if is project document
   */
  public boolean isProjectDocument() {
    return projectDocument;
  }

  /**
   * Sets the project document.
   *
   * @param projectDocument the new project document
   */
  public void setProjectDocument(boolean projectDocument) {
    this.projectDocument = projectDocument;
  }

  /**
   * Gets the user departments.
   *
   * @return the user departments
   */
  public List<DepartmentHistoryDTO> getUserDepartments() {
    return userDepartments;
  }

  /**
   * Sets the user departments.
   *
   * @param userDepartments the new user departments
   */
  public void setUserDepartments(List<DepartmentHistoryDTO> userDepartments) {
    this.userDepartments = userDepartments;
  }

  /**
   * Gets the group of the user that set the document to private.
   *
   * @return the group of the user that set the document to private
   */
  public String getPrivateGroup() {
    return privateGroup;
  }

  /**
   * Sets the group of the user that set the document to private.
   *
   * @param privateGroup the new group of the user that set the document to private
   */
  public void setPrivateGroup(String privateGroup) {
    this.privateGroup = privateGroup;
  }

  /**
   * Gets the legal hearing type.
   *
   * @return the legal hearing type
   */
  public String getLegalHearingType() {
    return legalHearingType;
  }

  /**
   * Sets the legal hearing type.
   *
   * @param legalHearingType the new legal hearing type
   */
  public void setLegalHearingType(String legalHearingType) {
    this.legalHearingType = legalHearingType;
  }

  /**
   * Gets the legal hearing exclusion.
   *
   * @return the legal hearing exclusion
   */
  public List<LegalHearingExclusionDTO> getLegalHearingExclusion() {
    return legalHearingExclusion;
  }

  /**
   * Sets the legal hearing exclusion.
   *
   * @param legalHearingExclusion the new legal hearing exclusion
   */
  public void setLegalHearingExclusion(List<LegalHearingExclusionDTO> legalHearingExclusion) {
    this.legalHearingExclusion = legalHearingExclusion;
  }

  /**
   * Gets the first signature.
   *
   * @return the first signature
   */
  public SignatureProcessTypeEntitledDTO getFirstSignature() {
    return firstSignature;
  }

  /**
   * Sets the first signature.
   *
   * @param firstSignature the new first signature
   */
  public void setFirstSignature(SignatureProcessTypeEntitledDTO firstSignature) {
    this.firstSignature = firstSignature;
  }

  /**
   * Gets the second signature.
   *
   * @return the second signature
   */
  public SignatureProcessTypeEntitledDTO getSecondSignature() {
    return secondSignature;
  }

  /**
   * Sets the second signature.
   *
   * @param secondSignature the new second signature
   */
  public void setSecondSignature(SignatureProcessTypeEntitledDTO secondSignature) {
    this.secondSignature = secondSignature;
  }

  /**
   * Gets the dept amount action.
   *
   * @return the dept amount action
   */
  public String getDeptAmountAction() {
    return deptAmountAction;
  }

  /**
   * Sets the dept amount action.
   *
   * @param deptAmountAction the new dept amount action
   */
  public void setDeptAmountAction(String deptAmountAction) {
    this.deptAmountAction = deptAmountAction;
  }

  public List<SignatureCopyDTO> getSignatureCopies() {
    return signatureCopies;
  }

  public void setSignatureCopies(List<SignatureCopyDTO> signatureCopies) {
    this.signatureCopies = signatureCopies;
  }

  public String getNachtragId() {
    return nachtragId;
  }

  public void setNachtragId(String nachtragId) {
    this.nachtragId = nachtragId;
  }

  public String getNachtragSubmittentId() {
    return nachtragSubmittentId;
  }

  public void setNachtragSubmittentId(String nachtragSubmittentId) {
    this.nachtragSubmittentId = nachtragSubmittentId;
  }

  public String getNachtragCompanyId() {
    return nachtragCompanyId;
  }

  public void setNachtragCompanyId(String nachtragCompanyId) {
    this.nachtragCompanyId = nachtragCompanyId;
  }

  @Override
  public String toString() {
    return "DocumentDTO [id=" + id + ", title=" + title + ", tenantId=" + tenantId + ", filename="
      + filename + ", documentCreationType=" + documentCreationType + ", createdOn=" + createdOn
      + ", lastModifiedOn=" + lastModifiedOn + ", version=" + version + ", createdBy=" + createdBy
      + ", active=" + active + ", mimetype=" + mimetype + ", global=" + global + ", folderId="
      + folderId + ", templateId=" + templateId + ", nodeId=" + nodeId + ", createVersion="
      + createVersion + ", privateDocument=" + privateDocument + ", privateGroup=" + privateGroup
      + ", canBeDownloaded=" + canBeDownloaded + ", canBeUploaded=" + canBeUploaded
      + ", canPropertiesBeEdited=" + canPropertiesBeEdited + ", canBeDeleted=" + canBeDeleted
      + ", canBePrinted=" + canBePrinted + ", isAdminRightsOnly=" + isAdminRightsOnly
      + ", submitDate=" + submitDate + ", generateProofTemplate=" + generateProofTemplate
      + ", procurement=" + procurement + ", isCompanyCertificate=" + isCompanyCertificate
      + ", deptAmounts=" + deptAmounts + ", expirationDate=" + expirationDate + ", submitentName="
      + submitentName + ", projectDocument=" + projectDocument + ", legalHearingType="
      + legalHearingType + ", deptAmountAction=" + deptAmountAction + "]";
  }

}

