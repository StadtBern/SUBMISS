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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonView;

import ch.bern.submiss.services.api.util.View;

/**
 * The Class SubmittentDTO.
 */
public class SubmittentDTO {

  /** The id. */
  @JsonView(View.Public.class)
  private String id;

  /** The company id. */
  @JsonView(View.Public.class)
  private CompanyDTO companyId;

  /** The submission id. */
  @JsonView(View.Internal.class)
  private SubmissionDTO submissionId;

  /** The subcontractors. */
  @JsonView(View.Public.class)
  private Set<CompanyDTO> subcontractors;

  /** The joint ventures. */
  @JsonView(View.Public.class)
  private Set<CompanyDTO> jointVentures;

  /** The exists exclusion reasons. */
  @JsonView(View.Public.class)
  private Boolean existsExclusionReasons;

  /** The formal examination fulfilled. */
  @JsonView(View.Public.class)
  private Boolean formalExaminationFulfilled;

  /** The sort order. */
  @JsonView(View.Public.class)
  private Integer sortOrder;

  /** The proof doc pending. */
  @JsonView(View.Internal.class)
  private Boolean proofDocPending;

  /** The is variant. */
  @JsonView(View.Public.class)
  private Boolean isVariant;

  /** The created on. */
  @JsonView(View.Internal.class)
  private Timestamp createdOn;

  /** The created by. */
  @JsonView(View.Internal.class)
  private String createdBy;

  /** The is applicant. */
  @JsonView(View.Public.class)
  private Boolean isApplicant;

  /** The submittent name arge sub. */
  @JsonView(View.Public.class)
  private String submittentNameArgeSub;

  /** The formal audit notes. */
  @JsonView(View.Public.class)
  private String formalAuditNotes;

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
   * Gets the company id.
   *
   * @return the company id
   */
  public CompanyDTO getCompanyId() {
    return companyId;
  }

  /**
   * Sets the company id.
   *
   * @param companyId the new company id
   */
  public void setCompanyId(CompanyDTO companyId) {
    this.companyId = companyId;
  }

  /**
   * Gets the submission id.
   *
   * @return the submission id
   */
  public SubmissionDTO getSubmissionId() {
    return submissionId;
  }

  /**
   * Sets the submission id.
   *
   * @param submissionId the new submission id
   */
  public void setSubmissionId(SubmissionDTO submissionId) {
    this.submissionId = submissionId;
  }

  /**
   * Gets the subcontractors.
   *
   * @return the subcontractors
   */
  public Set<CompanyDTO> getSubcontractors() {
    return subcontractors;
  }

  /**
   * Sets the subcontractors.
   *
   * @param subcontractors the new subcontractors
   */
  public void setSubcontractors(Set<CompanyDTO> subcontractors) {
    this.subcontractors = subcontractors;
  }

  /**
   * Gets the joint ventures.
   *
   * @return the joint ventures
   */
  public Set<CompanyDTO> getJointVentures() {
    return jointVentures;
  }

  /**
   * Sets the joint ventures.
   *
   * @param jointVentures the new joint ventures
   */
  public void setJointVentures(Set<CompanyDTO> jointVentures) {
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
   * Gets the sort order.
   *
   * @return the sort order
   */
  public Integer getSortOrder() {
    return sortOrder;
  }

  /**
   * Sets the sort order.
   *
   * @param sortOrder the new sort order
   */
  public void setSortOrder(Integer sortOrder) {
    this.sortOrder = sortOrder;
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
   * Gets the checks if is variant.
   *
   * @return the checks if is variant
   */
  public Boolean getIsVariant() {
    return isVariant;
  }

  /**
   * Sets the checks if is variant.
   *
   * @param isVariant the new checks if is variant
   */
  public void setIsVariant(Boolean isVariant) {
    this.isVariant = isVariant;
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
   * Gets the submittent name arge sub.
   *
   * @return the submittent name arge sub
   */
  public String getSubmittentNameArgeSub() {
    StringBuilder name = new StringBuilder();
    name.append(getCompanyId().getCompanyName() + ", " + getCompanyId().getLocation());
    if (getJointVentures() != null && !getJointVentures().isEmpty()) {
      name.append(" (ARGE: ");
      name.append(constructCompaniesNames(getJointVentures()));
    }
    if (getSubcontractors() != null && !getSubcontractors().isEmpty()) {
      if (getJointVentures() != null && !getJointVentures().isEmpty()) {
        name.append(", Sub. U: ");
      } else {
        name.append(" (Sub. U: ");
      }
      name.append(constructCompaniesNames(getSubcontractors()));
    }
    if ((getJointVentures() != null && !getJointVentures().isEmpty())
        || (getSubcontractors() != null && !getSubcontractors().isEmpty())) {
      name.append(")");
    }
    submittentNameArgeSub = name.toString();
    return submittentNameArgeSub;
  }

  /**
   * Construct companies names.
   *
   * @param companies the companies
   * @return the string
   */
  private String constructCompaniesNames(Set<CompanyDTO> companies) {
    StringBuilder name = new StringBuilder();
    List<CompanyDTO> companyList = new ArrayList<>(companies);
    Collections.sort(companyList, sortCompaniesByCompanyName);
    boolean isFirst = true;
    for (CompanyDTO company : companyList) {
      // add / as separator to all entries besides the first
      if (!isFirst) {
        name.append(" / ");
      } else {
        isFirst = false;
      }
      name.append(company.getCompanyName());
    }
    return name.toString();
  }

  /** The Constant sortCompaniesByCompanyName. */
  public static final Comparator<CompanyDTO> sortCompaniesByCompanyName =
    Comparator.comparing(CompanyDTO::getCompanyName); // compare by name and order alphabetically

  /**
   * Sets the submittent name arge sub.
   *
   * @param submittentNameArgeSub the new submittent name arge sub
   */
  public void setSubmittentNameArgeSub(String submittentNameArgeSub) {
    this.submittentNameArgeSub = submittentNameArgeSub;
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

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "SubmittentDTO [id=" + id + ", existsExclusionReasons=" + existsExclusionReasons
        + ", formalExaminationFulfilled=" + formalExaminationFulfilled + ", sortOrder=" + sortOrder
        + ", proofDocPending=" + proofDocPending + ", isVariant=" + isVariant + ", createdOn="
        + createdOn + ", createdBy=" + createdBy + ", isApplicant=" + isApplicant
        + ", submittentNameArgeSub=" + submittentNameArgeSub + ", formalAuditNotes="
        + formalAuditNotes + "]";
  }

}

