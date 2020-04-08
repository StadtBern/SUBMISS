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

import ch.bern.submiss.services.api.util.View;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import java.sql.Timestamp;
import java.util.Set;

/**
 * The Class SubmittentForm.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubmittentForm extends AbstractForm {

  /**
   * The company id.
   */
  private CompanyForm companyId;

  /**
   * The submission id.
   */
  private SubmissionForm submissionId;

  /**
   * The subcontractors.
   */
  private Set<CompanyForm> subcontractors;

  /**
   * The joint ventures.
   */
  private Set<CompanyForm> jointVentures;

  /**
   * The exists exclusion reasons.
   */
  private Boolean existsExclusionReasons;

  /**
   * The formal examination fulfilled.
   */
  private Boolean formalExaminationFulfilled;

  /**
   * The proof doc pending.
   */
  private Boolean proofDocPending;

  /**
   * The created on.
   */
  private Timestamp createdOn;

  /**
   * The updated on.
   */
  private Timestamp updatedOn;

  /**
   * The created by.
   */
  private String createdBy;

  /**
   * The is applicant.
   */
  private Boolean isApplicant;

  /**
   * The formal audit notes.
   */
  private String formalAuditNotes;

  /**
   * The formal audit version.
   */
  @JsonView(View.Public.class)
  private Long formalAuditVersion;

  /**
   * Gets the company id.
   *
   * @return the company id
   */
  public CompanyForm getCompanyId() {
    return companyId;
  }

  /**
   * Sets the company id.
   *
   * @param companyId the new company id
   */
  public void setCompanyId(CompanyForm companyId) {
    this.companyId = companyId;
  }

  /**
   * Gets the submission id.
   *
   * @return the submission id
   */
  public SubmissionForm getSubmissionId() {
    return submissionId;
  }

  /**
   * Sets the submission id.
   *
   * @param submissionId the new submission id
   */
  public void setSubmissionId(SubmissionForm submissionId) {
    this.submissionId = submissionId;
  }

  /**
   * Gets the subcontractors.
   *
   * @return the subcontractors
   */
  public Set<CompanyForm> getSubcontractors() {
    return subcontractors;
  }

  /**
   * Sets the subcontractors.
   *
   * @param subcontractors the new subcontractors
   */
  public void setSubcontractors(Set<CompanyForm> subcontractors) {
    this.subcontractors = subcontractors;
  }

  /**
   * Gets the joint ventures.
   *
   * @return the joint ventures
   */
  public Set<CompanyForm> getJointVentures() {
    return jointVentures;
  }

  /**
   * Sets the joint ventures.
   *
   * @param jointVentures the new joint ventures
   */
  public void setJointVentures(Set<CompanyForm> jointVentures) {
    this.jointVentures = jointVentures;
  }

  /**
   * Gets the exists exclusion reasons.
   *
   * @return the exists exclusion reasons
   */
  public Boolean getExistsExclusionReasons() {
    return existsExclusionReasons;
  }

  /**
   * Sets the exists exclusion reasons.
   *
   * @param existsExclusionReasons the new exists exclusion reasons
   */
  public void setExistsExclusionReasons(Boolean existsExclusionReasons) {
    this.existsExclusionReasons = existsExclusionReasons;
  }

  /**
   * Gets the formal examination fulfilled.
   *
   * @return the formal examination fulfilled
   */
  public Boolean getFormalExaminationFulfilled() {
    return formalExaminationFulfilled;
  }

  /**
   * Sets the formal examination fulfilled.
   *
   * @param formalExaminationFulfilled the new formal examination fulfilled
   */
  public void setFormalExaminationFulfilled(Boolean formalExaminationFulfilled) {
    this.formalExaminationFulfilled = formalExaminationFulfilled;
  }

  /**
   * Gets the proof doc pending.
   *
   * @return the proof doc pending
   */
  public Boolean getProofDocPending() {
    return proofDocPending;
  }

  /**
   * Sets the proof doc pending.
   *
   * @param proofDocPending the new proof doc pending
   */
  public void setProofDocPending(Boolean proofDocPending) {
    this.proofDocPending = proofDocPending;
  }

  /**
   * Gets the created on.
   *
   * @return the created on
   */
  public Timestamp getCreatedOn() {
    return createdOn;
  }

  /**
   * Sets the created on.
   *
   * @param createdOn the new created on
   */
  public void setCreatedOn(Timestamp createdOn) {
    this.createdOn = createdOn;
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
   * Gets the checks if is applicant.
   *
   * @return the checks if is applicant
   */
  public Boolean getIsApplicant() {
    return isApplicant;
  }

  /**
   * Sets the checks if is applicant.
   *
   * @param isApplicant the new checks if is applicant
   */
  public void setIsApplicant(Boolean isApplicant) {
    this.isApplicant = isApplicant;
  }

  /**
   * Gets the formal audit notes.
   *
   * @return the formal audit notes
   */
  public String getFormalAuditNotes() {
    return formalAuditNotes;
  }

  /**
   * Sets the formal audit notes.
   *
   * @param formalAuditNotes the new formal audit notes
   */
  public void setFormalAuditNotes(String formalAuditNotes) {
    this.formalAuditNotes = formalAuditNotes;
  }

  /**
   * Gets the updated on.
   *
   * @return the updatedOn
   */
  public Timestamp getUpdatedOn() {
    return updatedOn;
  }

  /**
   * Sets the updated on.
   *
   * @param updatedOn the updatedOn
   */
  public void setUpdatedOn(Timestamp updatedOn) {
    this.updatedOn = updatedOn;
  }

  public Long getFormalAuditVersion() {
    return formalAuditVersion;
  }

  public void setFormalAuditVersion(Long formalAuditVersion) {
    this.formalAuditVersion = formalAuditVersion;
  }
}
