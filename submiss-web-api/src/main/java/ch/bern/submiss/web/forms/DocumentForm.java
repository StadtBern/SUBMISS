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

import ch.bern.submiss.services.api.dto.SignatureCopyDTO;
import ch.bern.submiss.services.api.dto.SignatureProcessTypeEntitledDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

/**
 * The Class DocumentForm.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentForm {

  /** The submission id. */
  private String id;

  /** The template. */
  private String templateId;

  /** The create version. */
  private boolean createVersion;

  /** The version. */
  private String version;

  /** The tenant id. */
  private String tenantId;

  /** The template name. */
  private String templateName;

  /** The submit date. */
  private Date submitDate;

  /** The generate proof template. */
  private Boolean generateProofTemplate;

  /** The procurement. */
  private String procurement;

  /** The department. */
  private DepartmentHistoryForm department;

  /** The is company certificate. */
  private Boolean isCompanyCertificate;

  /** The dept amounts. */
  private BigDecimal deptAmounts;

  /** The expiration date. */
  private Date expirationDate;

  /** The title. */
  private String title;

  
  /** The project document. */
  private boolean projectDocument;
  
  /** The is cancelation. */
  private String legalHearingType;
  
  /** The legal hearing exclusion. */
  private List<LegalExclusionForm> legalHearingExclusion;
  
  /** The first signature. */
  private SignatureProcessTypeEntitledDTO firstSignature;
  
  /** The second signature. */
  private SignatureProcessTypeEntitledDTO secondSignature;

  /**
   * The signature copies.
   */
  private List<SignatureCopyDTO> signatureCopies;

  /**
   * The Nachtrag Id.
   */
  private String nachtragId;

  /**
   * The Nachtrag Submittent.
   */
  private String nachtragSubmittentId;


  /**
   * The Nachtrag Company Id.
   */
  private String nachtragCompanyId;

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
   * Gets the template name.
   *
   * @return the template name
   */
  public String getTemplateName() {
    return templateName;
  }

  /**
   * Sets the template name.
   *
   * @param templateName the new template name
   */
  public void setTemplateName(String templateName) {
    this.templateName = templateName;
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
  public DepartmentHistoryForm getDepartment() {
    return department;
  }

  /**
   * Sets the department.
   *
   * @param department the new department
   */
  public void setDepartment(DepartmentHistoryForm department) {
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
  public List<LegalExclusionForm> getLegalHearingExclusion() {
    return legalHearingExclusion;
  }

  /**
   * Sets the legal hearing exclusion.
   *
   * @param legalHearingExclusion the new legal hearing exclusion
   */
  public void setLegalHearingExclusion(List<LegalExclusionForm> legalHearingExclusion) {
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
}
