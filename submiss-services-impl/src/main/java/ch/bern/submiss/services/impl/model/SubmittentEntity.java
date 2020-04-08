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

package ch.bern.submiss.services.impl.model;

import java.sql.Timestamp;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * The Class SubmittentEntity.
 */
@Entity
@Table(name = "SUB_TENDERER")
public class SubmittentEntity extends AbstractEntity {

  /**
   * The company id.
   */
  @ManyToOne
  @JoinColumn(name = "FK_COMPANY")
  private CompanyEntity companyId;

  /**
   * The submission id.
   */
  @ManyToOne
  @JoinColumn(name = "FK_TENDER")
  private SubmissionEntity submissionId;

  /**
   * The subcontractors.
   */
  @ManyToMany
  @JoinTable(name = "SUB_SUBCONTRACTOR", joinColumns = {@JoinColumn(name = "FK_TENDERER")},
    inverseJoinColumns = {@JoinColumn(name = "FK_COMPANY")})
  @Cascade({CascadeType.SAVE_UPDATE})
  private Set<CompanyEntity> subcontractors;

  /**
   * The joint ventures.
   */
  @ManyToMany
  @JoinTable(name = "SUB_JOINT_VENTURE", joinColumns = {@JoinColumn(name = "FK_TENDERER")},
    inverseJoinColumns = {@JoinColumn(name = "FK_COMPANY")})
  @Cascade({CascadeType.SAVE_UPDATE})
  private Set<CompanyEntity> jointVentures;

  /**
   * The exists exclusion reasons.
   */
  @Column(name = "EXISTS_EXCLUSION_REASONS")
  private Boolean existsExclusionReasons;

  /**
   * The formal examination fulfilled.
   */
  @Column(name = "FORMAL_EXAMINATION_FULFILLED")
  private Boolean formalExaminationFulfilled;

  /**
   * The sort order.
   */
  @Column(name = "SORT_ORDER")
  private Integer sortOrder;

  /**
   * The proof doc pending.
   */
  @Column(name = "PROOF_DOC_PENDING")
  private Boolean proofDocPending;

  /**
   * The created on.
   */
  @Column(name = "CREATED_ON")
  private Timestamp createdOn;

  /**
   * The updated on.
   */
  @UpdateTimestamp
  @Column(name = "UPDATED_ON", insertable = false)
  private Timestamp updatedOn;

  /**
   * The created by.
   */
  @Column(name = "CREATED_BY")
  private String createdBy;

  /**
   * The is applicant.
   */
  @Column(name = "IS_APPLICANT")
  private Boolean isApplicant;

  /**
   * The formal audit notes.
   */
  @Column(name = "FORMAL_AUDIT_NOTES")
  private String formalAuditNotes;

  @OneToOne(mappedBy = "submittent", fetch = FetchType.EAGER)
  private OfferEntity offer;

  /**
   * Gets the company id.
   *
   * @return the company id
   */
  public CompanyEntity getCompanyId() {
    return companyId;
  }

  /**
   * Sets the company id.
   *
   * @param companyId the new company id
   */
  public void setCompanyId(CompanyEntity companyId) {
    this.companyId = companyId;
  }

  /**
   * Gets the submission id.
   *
   * @return the submission id
   */
  public SubmissionEntity getSubmissionId() {
    return submissionId;
  }

  /**
   * Sets the submission id.
   *
   * @param submissionId the new submission id
   */
  public void setSubmissionId(SubmissionEntity submissionId) {
    this.submissionId = submissionId;
  }

  /**
   * Gets the subcontractors.
   *
   * @return the subcontractors
   */
  public Set<CompanyEntity> getSubcontractors() {
    return subcontractors;
  }

  /**
   * Sets the subcontractors.
   *
   * @param subcontractors the new subcontractors
   */
  public void setSubcontractors(Set<CompanyEntity> subcontractors) {
    this.subcontractors = subcontractors;
  }

  /**
   * Gets the joint ventures.
   *
   * @return the joint ventures
   */
  public Set<CompanyEntity> getJointVentures() {
    return jointVentures;
  }

  /**
   * Sets the joint ventures.
   *
   * @param jointVentures the new joint ventures
   */
  public void setJointVentures(Set<CompanyEntity> jointVentures) {
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
   * Gets the offer.
   *
   * @return the offer
   */
  public OfferEntity getOffer() {
    return offer;
  }

  /**
   * Sets the offer.
   *
   * @param offer the new offer
   */
  public void setOffer(OfferEntity offer) {
    this.offer = offer;
  }

  /**
   * Gets the updatedOn.
   *
   * @return the updatedOn
   */
  public Timestamp getUpdatedOn() {
    return updatedOn;
  }

  /**
   * Sets the updatedOn.
   *
   * @param updatedOn the updatedOn
   */
  public void setUpdatedOn(Timestamp updatedOn) {
    this.updatedOn = updatedOn;
  }

  @Override
  public String toString() {
    return "SubmittentEntity [id=" + super.getId() + ", version=" + super.getVersion()
      + ", companyId=" + companyId + ", submissionId="
      + submissionId + ", subcontractors=" + subcontractors + ", jointVentures=" + jointVentures
      + ", existsExclusionReasons=" + existsExclusionReasons + ", formalExaminationFulfilled="
      + formalExaminationFulfilled + ", sortOrder=" + sortOrder + ", proofDocPending="
      + proofDocPending + ", createdOn=" + createdOn + ", createdBy=" + createdBy
      + ", isApplicant=" + isApplicant + ", formalAuditNotes=" + formalAuditNotes + ", offer="
      + offer + "]";
  }

  @Override
  public int hashCode() {
    return companyId.hashCode() + jointVentures.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    SubmittentEntity other = (SubmittentEntity) obj;
    if (hashCode() == other.hashCode()) {
      // Check subcontractor
      return hashCode() + subcontractors.hashCode() == other.hashCode()
        + other.subcontractors.hashCode();
    }
    return hashCode() == other.hashCode();
  }
}
