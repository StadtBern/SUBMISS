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

import ch.bern.submiss.services.api.constants.ConstructionPermit;
import ch.bern.submiss.services.api.constants.LoanApproval;
import ch.bern.submiss.services.api.constants.Process;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * The Class SubmissionEntity.
 */
@Entity
@Table(name = "SUB_TENDER")
public class SubmissionEntity extends AbstractEntity {

  /**
   * The project.
   */
  @ManyToOne
  @Cascade({CascadeType.SAVE_UPDATE})
  @JoinColumn(name = "FK_PROJECT")
  private ProjectEntity project;

  /**
   * The work type.
   */
  @ManyToOne
  @JoinColumn(name = "FK_HISTORY_WORK_TYPE")
  private MasterListValueEntity workType;

  /**
   * The description.
   */
  @Column(name = "DESCRIPTION")
  private String description;

  /**
   * The process.
   */
  @Column(name = "PROCESS")
  @Enumerated
  private Process process;

  /**
   * The cost estimate.
   */
  @Column(name = "COST_ESTIMATE")
  private BigDecimal costEstimate;

  /**
   * The process type.
   */
  @ManyToOne
  @JoinColumn(name = "FK_PROCESS_TYPE")
  private MasterListValueEntity processType;

  /**
   * The gatt two.
   */
  @Column(name = "GATT_WTO")
  private Boolean gattTwo;

  /**
   * The publication date.
   */
  @Column(name = "PUBLICATION_DATE")
  private Date publicationDate;

  /**
   * The publication date direct award.
   */
  @Column(name = "PUBLICATION_DATE_DIRECT_AWARD")
  private Date publicationDateDirectAward;

  /**
   * The publication date award.
   */
  @Column(name = "PUBLICATION_DATE_AWARD")
  private Date publicationDateAward;

  /**
   * The procedure.
   */
  @ManyToOne
  @JoinColumn(name = "FK_PM_ADMIN")
  private MasterListValueEntity procedure;

  /**
   * The pm department name.
   */
  @Column(name = "PM_DEPARTMENT_NAME")
  private String pmDepartmentName;

  /**
   * The pm external.
   */
  @ManyToOne
  @JoinColumn(name = "FK_PM_EXTERNAL")
  @Cascade({CascadeType.SAVE_UPDATE})
  private CompanyEntity pmExternal;

  /**
   * The is locked.
   */
  @Column(name = "IS_LOCKED")
  private Boolean isLocked;

  /**
   * The construction permit.
   */
  @Column(name = "CONSTRUCTION_PERMIT", length = 4, columnDefinition = "TINYINT")
  @Enumerated
  private ConstructionPermit constructionPermit;

  /**
   * The loan approval.
   */
  @Column(name = "LOAN_APPROVAL")
  @Enumerated
  private LoanApproval loanApproval;

  /**
   * The first deadline.
   */
  @Column(name = "DEADLINE1")
  private Date firstDeadline;

  /**
   * The second deadline.
   */
  @Column(name = "DEADLINE2")
  private Date secondDeadline;

  /**
   * The application opening date.
   */
  @Column(name = "APPLICATION_OPENING_DATE")
  private Date applicationOpeningDate;

  /**
   * The offer opening date.
   */
  @Column(name = "OFFER_OPENING")
  private Date offerOpeningDate;

  /**
   * The first logged by.
   */
  @Column(name = "LOGGED_BY")
  private String firstLoggedBy;

  /**
   * The second logged by.
   */
  @Column(name = "LOGGED_BY_2")
  private String secondLoggedBy;

  /**
   * The notes.
   */
  @Column(name = "NOTES")
  private String notes;

  /**
   * The is service tender.
   */
  @Column(name = "IS_SERVICE_TENDER")
  private Boolean isServiceTender;

  /**
   * The is geko entry.
   */
  @Column(name = "IS_GEKO_ENTRY")
  private Boolean isGekoEntry;

  /**
   * The reason free award.
   */
  @ManyToOne
  @JoinColumn(name = "FK_REASON_NEGOTIATED_PROCEDURE")
  private MasterListValueEntity reasonFreeAward;

  /**
   * The submittents.
   */
  @OneToMany(mappedBy = "submissionId", fetch = FetchType.EAGER)
  @OrderBy(value = "SORT_ORDER ASC")
  private List<SubmittentEntity> submittents;

  /**
   * The min grade.
   */
  @Column(name = "MIN_GRADE")
  private BigDecimal minGrade;

  /**
   * The max grade.
   */
  @Column(name = "MAX_GRADE")
  private BigDecimal maxGrade;

  /**
   * The above threshold.
   */
  @Column(name = "ABOVE_THRESHOLD")
  private Boolean aboveThreshold;

  /**
   * The award min grade.
   */
  @Column(name = "AWARD_MIN_GRADE")
  private BigDecimal awardMinGrade;

  /**
   * The award max grade.
   */
  @Column(name = "AWARD_MAX_GRADE")
  private BigDecimal awardMaxGrade;

  /**
   * The operating cost formula.
   */
  @ManyToOne
  @JoinColumn(name = "FK_MASTER_LIST_VALUE_HISTORY_OPERATING_COST_FORMULA")
  private MasterListValueEntity operatingCostFormula;

  /**
   * The price formula.
   */
  @ManyToOne
  @JoinColumn(name = "FK_MASTER_LIST_VALUE_HISTORY_PRICE_FORMULA")
  private MasterListValueEntity priceFormula;

  /**
   * The added award recipients.
   */
  @Column(name = "ADDED_AWARD_RECIPIENTS")
  private BigDecimal addedAwardRecipients;

  /**
   * The evaluation through.
   */
  @Column(name = "EVALUATION_THROUGH")
  private String evaluationThrough;

  /**
   * The commission procurement proposal date.
   */
  @Column(name = "CPP_DATE")
  private Date commissionProcurementProposalDate;

  /**
   * The commission procurement proposal business.
   */
  @Column(name = "CPP_BUSINESS")
  private BigDecimal commissionProcurementProposalBusiness;

  /**
   * The commission procurement proposal object.
   */
  @Column(name = "CPP_OBJECT")
  private String commissionProcurementProposalObject;

  /**
   * The commission procurement proposal suitability audit dropdown.
   */
  @Column(name = "CPP_SUITABILITY_AUDIT_DROPDOWN")
  private String commissionProcurementProposalSuitabilityAuditDropdown;

  /**
   * The commission procurement proposal suitability audit text.
   */
  @Column(name = "CPP_SUITABILITY_AUDIT_TEXT")
  private String commissionProcurementProposalSuitabilityAuditText;

  /**
   * The commission procurement proposal pre remarks.
   */
  @Column(name = "CPP_PRE_REMARKS")
  private String commissionProcurementProposalPreRemarks;

  /**
   * The commission procurement proposal reservation.
   */
  @Column(name = "CPP_RESERVATION")
  private String commissionProcurementProposalReservation;

  /**
   * The commission procurement proposal reason given.
   */
  @Column(name = "CPP_REASON_GIVEN")
  private String commissionProcurementProposalReasonGiven;

  /**
   * The commission procurement decision recommendation.
   */
  @Column(name = "CPD_RECOMMENDATION")
  private String commissionProcurementDecisionRecommendation;

  /**
   * The submission cancel.
   */
  @OneToMany(mappedBy = "submission")
  private List<SubmissionCancelEntity> submissionCancel;

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
   * Whether field pm department name is updated.
   */
  @Column(name = "IS_PM_DEPARTMENT_NAME_UPDATED")
  private Boolean isPmDepartmentNameUpdated;

  /**
   * Whether field pm external is updated.
   */
  @Column(name = "IS_FK_PM_EXTERNAL_UPDATED")
  private Boolean isPmExternalUpdated;

  /**
   * Whether field procedure is updated.
   */
  @Column(name = "IS_FK_PM_ADMIN_UPDATED")
  private Boolean isProcedureUpdated;

  /**
   * Whether field gatt two is updated.
   */
  @Column(name = "IS_GATT_WTO_UPDATED")
  private Boolean isGattTwoUpdated;

  /**
   * The legal hearing terminate.
   */
  @OneToMany(mappedBy = "submission")
  private List<LegalHearingTerminateEntity> legalHearingTerminate;

  /**
   * The exclusion deadline.
   */
  @Column(name = "EXCLUSION_DEADLINE")
  private Date exclusionDeadline;

  /**
   * The status.
   */
  @Column(name = "STATUS")
  private String status;

  /**
   * The first level exclusion date.
   */
  @Column(name = "EXCLUSION_DEADLINE_FIRST_LEVEL")
  private Date firstLevelExclusionDate;

  /**
   * The custom price formula.
   */
  @Column(name = "CUSTOM_PRICE_FORMULA")
  private String customPriceFormula;

  /**
   * The custom operationg cost formula.
   */
  @Column(name = "CUSTOM_OPERATING_COST_FORMULA")
  private String customOperatingCostFormula;

  /**
   * The examination is locked.
   */
  @Column(name = "EXAMINATION_IS_LOCKED")
  private Boolean examinationIsLocked;

  /**
   * The award is locked.
   */
  @Column(name = "AWARD_IS_LOCKED")
  private Boolean awardIsLocked;

  /**
   * The examination locking timestamp.
   */
  @Column(name = "EXAMINATION_LOCKED_TIME")
  private Timestamp examinationLockedTime;

  /**
   * The award locking timestamp.
   */
  @Column(name = "AWARD_LOCKED_TIME")
  private Timestamp awardLockedTime;

  /**
   * The user who locked the examination.
   */
  @Column(name = "EXAMINATION_LOCKED_BY")
  private String examinationLockedBy;

  /**
   * The user who locked the award.
   */
  @Column(name = "AWARD_LOCKED_BY")
  private String awardLockedBy;

  @OneToMany(mappedBy = "tenderId")
  @OrderBy(value = "onDate DESC")
  private Set<TenderStatusHistoryEntity> tenderStatusHistory;

  /**
   * The is geko entry by Manual award.
   */
  @Column(name = "IS_GEKO_ENTRY_BY_MANUAL_AWARD")
  private Boolean isGekoEntryByManualAward;

  /**
   * The submittentListCheckedBy.
   */
  @Column(name = "SUBMITTENTLIST_CHECKED_BY")
  private String submittentListCheckedBy;

  /**
   * The submittentListCheckedOn.
   */
  @Column(name = "SUBMITTENTLIST_CHECKED_ON")
  private Long submittentListCheckedOn;

  /**
   * Gets the project.
   *
   * @return the project
   */
  public ProjectEntity getProject() {
    return project;
  }

  /**
   * Sets the project.
   *
   * @param project the new project
   */
  public void setProject(ProjectEntity project) {
    this.project = project;
  }

  /**
   * Gets the work type.
   *
   * @return the work type
   */
  public MasterListValueEntity getWorkType() {
    return workType;
  }

  /**
   * Sets the work type.
   *
   * @param workType the new work type
   */
  public void setWorkType(MasterListValueEntity workType) {
    this.workType = workType;
  }

  /**
   * Gets the description.
   *
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description.
   *
   * @param description the new description
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the process.
   *
   * @return the process
   */
  public Process getProcess() {
    return process;
  }

  /**
   * Sets the process.
   *
   * @param process the new process
   */
  public void setProcess(Process process) {
    this.process = process;
  }

  /**
   * Gets the cost estimate.
   *
   * @return the cost estimate
   */
  public BigDecimal getCostEstimate() {
    return costEstimate;
  }

  /**
   * Sets the cost estimate.
   *
   * @param costEstimate the new cost estimate
   */
  public void setCostEstimate(BigDecimal costEstimate) {
    this.costEstimate = costEstimate;
  }

  /**
   * Gets the process type.
   *
   * @return the process type
   */
  public MasterListValueEntity getProcessType() {
    return processType;
  }

  /**
   * Sets the process type.
   *
   * @param processType the new process type
   */
  public void setProcessType(MasterListValueEntity processType) {
    this.processType = processType;
  }

  /**
   * Gets the gatt two.
   *
   * @return the gatt two
   */
  public Boolean getGattTwo() {
    return gattTwo;
  }

  /**
   * Sets the gatt two.
   *
   * @param gattTwo the new gatt two
   */
  public void setGattTwo(Boolean gattTwo) {
    this.gattTwo = gattTwo;
  }

  /**
   * Gets the publication date.
   *
   * @return the publication date
   */
  public Date getPublicationDate() {
    return publicationDate;
  }

  /**
   * Sets the publication date.
   *
   * @param publicationDate the new publication date
   */
  public void setPublicationDate(Date publicationDate) {
    this.publicationDate = publicationDate;
  }

  /**
   * Gets the publication date direct award.
   *
   * @return the publication date direct award
   */
  public Date getPublicationDateDirectAward() {
    return publicationDateDirectAward;
  }

  /**
   * Sets the publication date direct award.
   *
   * @param publicationDateDirectAward the new publication date direct award
   */
  public void setPublicationDateDirectAward(Date publicationDateDirectAward) {
    this.publicationDateDirectAward = publicationDateDirectAward;
  }

  /**
   * Gets the publication date award.
   *
   * @return the publication date award
   */
  public Date getPublicationDateAward() {
    return publicationDateAward;
  }

  /**
   * Sets the publication date award.
   *
   * @param publicationDateAward the new publication date award
   */
  public void setPublicationDateAward(Date publicationDateAward) {
    this.publicationDateAward = publicationDateAward;
  }

  /**
   * Gets the procedure.
   *
   * @return the procedure
   */
  public MasterListValueEntity getProcedure() {
    return procedure;
  }

  /**
   * Sets the procedure.
   *
   * @param procedure the new procedure
   */
  public void setProcedure(MasterListValueEntity procedure) {
    this.procedure = procedure;
  }

  /**
   * Gets the pm department name.
   *
   * @return the pm department name
   */
  public String getPmDepartmentName() {
    return pmDepartmentName;
  }

  /**
   * Sets the pm department name.
   *
   * @param pmDepartmentName the new pm department name
   */
  public void setPmDepartmentName(String pmDepartmentName) {
    this.pmDepartmentName = pmDepartmentName;
  }

  /**
   * Gets the pm external.
   *
   * @return the pm external
   */
  public CompanyEntity getPmExternal() {
    return pmExternal;
  }

  /**
   * Sets the pm external.
   *
   * @param pmExternal the new pm external
   */
  public void setPmExternal(CompanyEntity pmExternal) {
    this.pmExternal = pmExternal;
  }

  /**
   * Gets the checks if is locked.
   *
   * @return the checks if is locked
   */
  public Boolean getIsLocked() {
    return isLocked;
  }

  /**
   * Sets the checks if is locked.
   *
   * @param isLocked the new checks if is locked
   */
  public void setIsLocked(Boolean isLocked) {
    this.isLocked = isLocked;
  }

  /**
   * Gets the construction permit.
   *
   * @return the construction permit
   */
  public ConstructionPermit getConstructionPermit() {
    return constructionPermit;
  }

  /**
   * Sets the construction permit.
   *
   * @param constructionPermit the new construction permit
   */
  public void setConstructionPermit(ConstructionPermit constructionPermit) {
    this.constructionPermit = constructionPermit;
  }

  /**
   * Gets the loan approval.
   *
   * @return the loan approval
   */
  public LoanApproval getLoanApproval() {
    return loanApproval;
  }

  /**
   * Sets the loan approval.
   *
   * @param loanApproval the new loan approval
   */
  public void setLoanApproval(LoanApproval loanApproval) {
    this.loanApproval = loanApproval;
  }

  /**
   * Gets the first deadline.
   *
   * @return the first deadline
   */
  public Date getFirstDeadline() {
    return firstDeadline;
  }

  /**
   * Sets the first deadline.
   *
   * @param firstDeadline the new first deadline
   */
  public void setFirstDeadline(Date firstDeadline) {
    this.firstDeadline = firstDeadline;
  }

  /**
   * Gets the second deadline.
   *
   * @return the second deadline
   */
  public Date getSecondDeadline() {
    return secondDeadline;
  }

  /**
   * Sets the second deadline.
   *
   * @param secondDeadline the new second deadline
   */
  public void setSecondDeadline(Date secondDeadline) {
    this.secondDeadline = secondDeadline;
  }

  /**
   * Gets the application opening date.
   *
   * @return the application opening date
   */
  public Date getApplicationOpeningDate() {
    return applicationOpeningDate;
  }

  /**
   * Sets the application opening date.
   *
   * @param applicationOpeningDate the new application opening date
   */
  public void setApplicationOpeningDate(Date applicationOpeningDate) {
    this.applicationOpeningDate = applicationOpeningDate;
  }

  /**
   * Gets the offer opening date.
   *
   * @return the offer opening date
   */
  public Date getOfferOpeningDate() {
    return offerOpeningDate;
  }

  /**
   * Sets the offer opening date.
   *
   * @param offerOpeningDate the new offer opening date
   */
  public void setOfferOpeningDate(Date offerOpeningDate) {
    this.offerOpeningDate = offerOpeningDate;
  }

  /**
   * Gets the first logged by.
   *
   * @return the first logged by
   */
  public String getFirstLoggedBy() {
    return firstLoggedBy;
  }

  /**
   * Sets the first logged by.
   *
   * @param firstLoggedBy the new first logged by
   */
  public void setFirstLoggedBy(String firstLoggedBy) {
    this.firstLoggedBy = firstLoggedBy;
  }

  /**
   * Gets the second logged by.
   *
   * @return the second logged by
   */
  public String getSecondLoggedBy() {
    return secondLoggedBy;
  }

  /**
   * Sets the second logged by.
   *
   * @param secondLoggedBy the new second logged by
   */
  public void setSecondLoggedBy(String secondLoggedBy) {
    this.secondLoggedBy = secondLoggedBy;
  }

  /**
   * Gets the notes.
   *
   * @return the notes
   */
  public String getNotes() {
    return notes;
  }

  /**
   * Sets the notes.
   *
   * @param notes the new notes
   */
  public void setNotes(String notes) {
    this.notes = notes;
  }

  /**
   * Gets the checks if is service tender.
   *
   * @return the checks if is service tender
   */
  public Boolean getIsServiceTender() {
    return isServiceTender;
  }

  /**
   * Sets the checks if is service tender.
   *
   * @param isServiceTender the new checks if is service tender
   */
  public void setIsServiceTender(Boolean isServiceTender) {
    this.isServiceTender = isServiceTender;
  }

  /**
   * Gets the checks if is geko entry.
   *
   * @return the checks if is geko entry
   */
  public Boolean getIsGekoEntry() {
    return isGekoEntry;
  }

  /**
   * Sets the checks if is geko entry.
   *
   * @param isGekoEntry the new checks if is geko entry
   */
  public void setIsGekoEntry(Boolean isGekoEntry) {
    this.isGekoEntry = isGekoEntry;
  }

  /**
   * Gets the reason free award.
   *
   * @return the reason free award
   */
  public MasterListValueEntity getReasonFreeAward() {
    return reasonFreeAward;
  }

  /**
   * Sets the reason free award.
   *
   * @param reasonFreeAward the new reason free award
   */
  public void setReasonFreeAward(MasterListValueEntity reasonFreeAward) {
    this.reasonFreeAward = reasonFreeAward;
  }

  /**
   * Gets the submittents.
   *
   * @return the submittents
   */
  public List<SubmittentEntity> getSubmittents() {
    return submittents;
  }

  /**
   * Sets the submittents.
   *
   * @param submittents the new submittents
   */
  public void setSubmittents(List<SubmittentEntity> submittents) {
    this.submittents = submittents;
  }

  /**
   * Gets the min grade.
   *
   * @return the min grade
   */
  public BigDecimal getMinGrade() {
    return minGrade;
  }

  /**
   * Sets the min grade.
   *
   * @param minGrade the new min grade
   */
  public void setMinGrade(BigDecimal minGrade) {
    this.minGrade = minGrade;
  }

  /**
   * Gets the max grade.
   *
   * @return the max grade
   */
  public BigDecimal getMaxGrade() {
    return maxGrade;
  }

  /**
   * Sets the max grade.
   *
   * @param maxGrade the new max grade
   */
  public void setMaxGrade(BigDecimal maxGrade) {
    this.maxGrade = maxGrade;
  }

  /**
   * Gets the above threshold.
   *
   * @return the above threshold
   */
  public Boolean getAboveThreshold() {
    return aboveThreshold;
  }

  /**
   * Sets the above threshold.
   *
   * @param aboveThreshold the new above threshold
   */
  public void setAboveThreshold(Boolean aboveThreshold) {
    this.aboveThreshold = aboveThreshold;
  }

  /**
   * Gets the award min grade.
   *
   * @return the award min grade
   */
  public BigDecimal getAwardMinGrade() {
    return awardMinGrade;
  }

  /**
   * Sets the award min grade.
   *
   * @param awardMinGrade the new award min grade
   */
  public void setAwardMinGrade(BigDecimal awardMinGrade) {
    this.awardMinGrade = awardMinGrade;
  }

  /**
   * Gets the award max grade.
   *
   * @return the award max grade
   */
  public BigDecimal getAwardMaxGrade() {
    return awardMaxGrade;
  }

  /**
   * Sets the award max grade.
   *
   * @param awardMaxGrade the new award max grade
   */
  public void setAwardMaxGrade(BigDecimal awardMaxGrade) {
    this.awardMaxGrade = awardMaxGrade;
  }

  /**
   * Gets the operating cost formula.
   *
   * @return the operating cost formula
   */
  public MasterListValueEntity getOperatingCostFormula() {
    return operatingCostFormula;
  }

  /**
   * Sets the operating cost formula.
   *
   * @param operatingCostFormula the new operating cost formula
   */
  public void setOperatingCostFormula(MasterListValueEntity operatingCostFormula) {
    this.operatingCostFormula = operatingCostFormula;
  }

  /**
   * Gets the price formula.
   *
   * @return the price formula
   */
  public MasterListValueEntity getPriceFormula() {
    return priceFormula;
  }

  /**
   * Sets the price formula.
   *
   * @param priceFormula the new price formula
   */
  public void setPriceFormula(MasterListValueEntity priceFormula) {
    this.priceFormula = priceFormula;
  }

  /**
   * Gets the added award recipients.
   *
   * @return the added award recipients
   */
  public BigDecimal getAddedAwardRecipients() {
    return addedAwardRecipients;
  }

  /**
   * Sets the added award recipients.
   *
   * @param addedAwardRecipients the new added award recipients
   */
  public void setAddedAwardRecipients(BigDecimal addedAwardRecipients) {
    this.addedAwardRecipients = addedAwardRecipients;
  }

  /**
   * Gets the evaluation through.
   *
   * @return the evaluation through
   */
  public String getEvaluationThrough() {
    return evaluationThrough;
  }

  /**
   * Sets the evaluation through.
   *
   * @param evaluationThrough the new evaluation through
   */
  public void setEvaluationThrough(String evaluationThrough) {
    this.evaluationThrough = evaluationThrough;
  }

  /**
   * Gets the commission procurement proposal date.
   *
   * @return the commission procurement proposal date
   */
  public Date getCommissionProcurementProposalDate() {
    return commissionProcurementProposalDate;
  }

  /**
   * Sets the commission procurement proposal date.
   *
   * @param commissionProcurementProposalDate the new commission procurement proposal date
   */
  public void setCommissionProcurementProposalDate(Date commissionProcurementProposalDate) {
    this.commissionProcurementProposalDate = commissionProcurementProposalDate;
  }

  /**
   * Gets the commission procurement proposal business.
   *
   * @return the commission procurement proposal business
   */
  public BigDecimal getCommissionProcurementProposalBusiness() {
    return commissionProcurementProposalBusiness;
  }

  /**
   * Sets the commission procurement proposal business.
   *
   * @param commissionProcurementProposalBusiness the new commission procurement proposal business
   */
  public void setCommissionProcurementProposalBusiness(
    BigDecimal commissionProcurementProposalBusiness) {
    this.commissionProcurementProposalBusiness = commissionProcurementProposalBusiness;
  }

  /**
   * Gets the commission procurement proposal object.
   *
   * @return the commission procurement proposal object
   */
  public String getCommissionProcurementProposalObject() {
    return commissionProcurementProposalObject;
  }

  /**
   * Sets the commission procurement proposal object.
   *
   * @param commissionProcurementProposalObject the new commission procurement proposal object
   */
  public void setCommissionProcurementProposalObject(String commissionProcurementProposalObject) {
    this.commissionProcurementProposalObject = commissionProcurementProposalObject;
  }

  /**
   * Gets the commission procurement proposal suitability audit dropdown.
   *
   * @return the commission procurement proposal suitability audit dropdown
   */
  public String getCommissionProcurementProposalSuitabilityAuditDropdown() {
    return commissionProcurementProposalSuitabilityAuditDropdown;
  }

  /**
   * Sets the commission procurement proposal suitability audit dropdown.
   *
   * @param commissionProcurementProposalSuitabilityAuditDropdown the new commission procurement
   *                                                              proposal suitability audit
   *                                                              dropdown
   */
  public void setCommissionProcurementProposalSuitabilityAuditDropdown(
    String commissionProcurementProposalSuitabilityAuditDropdown) {
    this.commissionProcurementProposalSuitabilityAuditDropdown =
      commissionProcurementProposalSuitabilityAuditDropdown;
  }

  /**
   * Gets the commission procurement proposal suitability audit text.
   *
   * @return the commission procurement proposal suitability audit text
   */
  public String getCommissionProcurementProposalSuitabilityAuditText() {
    return commissionProcurementProposalSuitabilityAuditText;
  }

  /**
   * Sets the commission procurement proposal suitability audit text.
   *
   * @param commissionProcurementProposalSuitabilityAuditText the new commission procurement
   *                                                          proposal suitability audit text
   */
  public void setCommissionProcurementProposalSuitabilityAuditText(
    String commissionProcurementProposalSuitabilityAuditText) {
    this.commissionProcurementProposalSuitabilityAuditText =
      commissionProcurementProposalSuitabilityAuditText;
  }

  /**
   * Gets the commission procurement proposal pre remarks.
   *
   * @return the commission procurement proposal pre remarks
   */
  public String getCommissionProcurementProposalPreRemarks() {
    return commissionProcurementProposalPreRemarks;
  }

  /**
   * Sets the commission procurement proposal pre remarks.
   *
   * @param commissionProcurementProposalPreRemarks the new commission procurement proposal pre
   *                                                remarks
   */
  public void setCommissionProcurementProposalPreRemarks(
    String commissionProcurementProposalPreRemarks) {
    this.commissionProcurementProposalPreRemarks = commissionProcurementProposalPreRemarks;
  }

  /**
   * Gets the commission procurement proposal reservation.
   *
   * @return the commission procurement proposal reservation
   */
  public String getCommissionProcurementProposalReservation() {
    return commissionProcurementProposalReservation;
  }

  /**
   * Sets the commission procurement proposal reservation.
   *
   * @param commissionProcurementProposalReservation the new commission procurement proposal
   *                                                 reservation
   */
  public void setCommissionProcurementProposalReservation(
    String commissionProcurementProposalReservation) {
    this.commissionProcurementProposalReservation = commissionProcurementProposalReservation;
  }

  /**
   * Gets the commission procurement proposal reason given.
   *
   * @return the commission procurement proposal reason given
   */
  public String getCommissionProcurementProposalReasonGiven() {
    return commissionProcurementProposalReasonGiven;
  }

  /**
   * Sets the commission procurement proposal reason given.
   *
   * @param commissionProcurementProposalReasonGiven the new commission procurement proposal reason
   *                                                 given
   */
  public void setCommissionProcurementProposalReasonGiven(
    String commissionProcurementProposalReasonGiven) {
    this.commissionProcurementProposalReasonGiven = commissionProcurementProposalReasonGiven;
  }

  /**
   * Gets the commission procurement decision recommendation.
   *
   * @return the commission procurement decision recommendation
   */
  public String getCommissionProcurementDecisionRecommendation() {
    return commissionProcurementDecisionRecommendation;
  }

  /**
   * Sets the commission procurement decision recommendation.
   *
   * @param commissionProcurementDecisionRecommendation the new commission procurement decision
   *                                                    recommendation
   */
  public void setCommissionProcurementDecisionRecommendation(
    String commissionProcurementDecisionRecommendation) {
    this.commissionProcurementDecisionRecommendation = commissionProcurementDecisionRecommendation;
  }

  /**
   * Gets the submission cancel.
   *
   * @return the submission cancel
   */
  public List<SubmissionCancelEntity> getSubmissionCancel() {
    return submissionCancel;
  }

  /**
   * Sets the submission cancel.
   *
   * @param submissionCancel the new submission cancel
   */
  public void setSubmissionCancel(List<SubmissionCancelEntity> submissionCancel) {
    this.submissionCancel = submissionCancel;
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
   * Gets the checks if is pm department name updated.
   *
   * @return the checks if is pm department name updated
   */
  public Boolean getIsPmDepartmentNameUpdated() {
    return isPmDepartmentNameUpdated;
  }

  /**
   * Sets the checks if is pm department name updated.
   *
   * @param isPmDepartmentNameUpdated the new checks if is pm department name updated
   */
  public void setIsPmDepartmentNameUpdated(Boolean isPmDepartmentNameUpdated) {
    this.isPmDepartmentNameUpdated = isPmDepartmentNameUpdated;
  }

  /**
   * Gets the checks if is pm external updated.
   *
   * @return the checks if is pm external updated
   */
  public Boolean getIsPmExternalUpdated() {
    return isPmExternalUpdated;
  }

  /**
   * Sets the checks if is pm external updated.
   *
   * @param isPmExternalUpdated the new checks if is pm external updated
   */
  public void setIsPmExternalUpdated(Boolean isPmExternalUpdated) {
    this.isPmExternalUpdated = isPmExternalUpdated;
  }

  /**
   * Gets the checks if is procedure updated.
   *
   * @return the checks if is procedure updated
   */
  public Boolean getIsProcedureUpdated() {
    return isProcedureUpdated;
  }

  /**
   * Sets the checks if is procedure updated.
   *
   * @param isProcedureUpdated the new checks if is procedure updated
   */
  public void setIsProcedureUpdated(Boolean isProcedureUpdated) {
    this.isProcedureUpdated = isProcedureUpdated;
  }

  /**
   * Gets the checks if is gatt two updated.
   *
   * @return the checks if is gatt two updated
   */
  public Boolean getIsGattTwoUpdated() {
    return isGattTwoUpdated;
  }

  /**
   * Sets the checks if is gatt two updated.
   *
   * @param isGattTwoUpdated the new checks if is gatt two updated
   */
  public void setIsGattTwoUpdated(Boolean isGattTwoUpdated) {
    this.isGattTwoUpdated = isGattTwoUpdated;
  }

  /**
   * Gets the work type history.
   *
   * @param fromDate the from date
   * @return the work type history
   */
  public MasterListValueHistoryEntity getWorkTypeHistory(Date fromDate) {

    for (MasterListValueHistoryEntity historyEntity : workType.getMasterListValueHistory()) {
      if (historyEntity.getFromDate().before(fromDate)) {
        return historyEntity;
      }
    }

    return null;
  }

  /**
   * Gets the process type history.
   *
   * @param fromDate the from date
   * @return the process type history
   */
  public MasterListValueHistoryEntity getProcessTypeHistory(Date fromDate) {

    for (MasterListValueHistoryEntity historyEntity : processType.getMasterListValueHistory()) {
      if (historyEntity.getFromDate().before(fromDate)) {
        return historyEntity;
      }
    }

    return null;
  }

  /**
   * Gets the procedure history.
   *
   * @param fromDate the from date
   * @return the procedure history
   */
  public MasterListValueHistoryEntity getProcedureHistory(Date fromDate) {

    for (MasterListValueHistoryEntity historyEntity : procedure.getMasterListValueHistory()) {
      if (historyEntity.getFromDate().before(fromDate)) {
        return historyEntity;
      }
    }

    return null;
  }

  /**
   * Gets the reason free award history.
   *
   * @param fromDate the from date
   * @return the reason free award history
   */
  public MasterListValueHistoryEntity getReasonFreeAwardHistory(Date fromDate) {

    for (MasterListValueHistoryEntity historyEntity : reasonFreeAward.getMasterListValueHistory()) {
      if (historyEntity.getFromDate().before(fromDate)) {
        return historyEntity;
      }
    }

    return null;
  }

  /**
   * Gets the operating cost formula history.
   *
   * @param fromDate the from date
   * @return the operating cost formula history
   */
  public MasterListValueHistoryEntity getOperatingCostFormulaHistory(Date fromDate) {

    for (MasterListValueHistoryEntity historyEntity : operatingCostFormula
      .getMasterListValueHistory()) {
      if (historyEntity.getFromDate().before(fromDate)) {
        return historyEntity;
      }
    }

    return null;
  }

  /**
   * Gets the price formula history.
   *
   * @param fromDate the from date
   * @return the price formula history
   */
  public MasterListValueHistoryEntity getPriceFormulaHistory(Date fromDate) {

    for (MasterListValueHistoryEntity historyEntity : priceFormula.getMasterListValueHistory()) {
      if (historyEntity.getFromDate().before(fromDate)) {
        return historyEntity;
      }
    }

    return null;
  }

  /**
   * Gets the legal hearing terminate.
   *
   * @return the legal hearing terminate
   */
  public List<LegalHearingTerminateEntity> getLegalHearingTerminate() {
    return legalHearingTerminate;
  }

  /**
   * Sets the legal hearing terminate.
   *
   * @param legalHearingTerminate the new legal hearing terminate
   */
  public void setLegalHearingTerminate(List<LegalHearingTerminateEntity> legalHearingTerminate) {
    this.legalHearingTerminate = legalHearingTerminate;
  }

  /**
   * Gets the recent reason free award history.
   *
   * @return the recent reason free award history
   */
  public MasterListValueHistoryEntity getRecentReasonFreeAwardHistory() {
    if (null != reasonFreeAward.getMasterListValueHistory()) {
      for (MasterListValueHistoryEntity reasonFreeAwardHistoryEntity : reasonFreeAward
        .getMasterListValueHistory()) {
        if (reasonFreeAwardHistoryEntity.getToDate() == null) {
          return reasonFreeAwardHistoryEntity;
        }
      }
    }
    return null;
  }

  /**
   * Gets the recent operating cost formula history.
   *
   * @return the recent operating cost formula history
   */
  public MasterListValueHistoryEntity getRecentOperatingCostFormulaHistory() {
    for (MasterListValueHistoryEntity operatingCostFormulaHistoryEntity : operatingCostFormula
      .getMasterListValueHistory()) {
      if (operatingCostFormulaHistoryEntity.getToDate() == null) {
        return operatingCostFormulaHistoryEntity;
      }
    }
    return null;
  }

  /**
   * Gets the recent price formula history.
   *
   * @return the recent price formula history
   */
  public MasterListValueHistoryEntity getRecentPriceFormulaHistory() {
    for (MasterListValueHistoryEntity priceFormulaHistoryEntity : priceFormula
      .getMasterListValueHistory()) {
      if (priceFormulaHistoryEntity.getToDate() == null) {
        return priceFormulaHistoryEntity;
      }
    }
    return null;
  }

  /**
   * Gets the recent work type history.
   *
   * @return the recent work type history
   */
  public MasterListValueHistoryEntity getRecentWorkTypeHistory() {
    for (MasterListValueHistoryEntity workTypeHistoryEntity : workType
      .getMasterListValueHistory()) {
      if (workTypeHistoryEntity.getToDate() == null) {
        return workTypeHistoryEntity;
      }
    }
    return null;
  }

  /**
   * Gets the recent procedure history.
   *
   * @return the recent procedure history
   */
  public MasterListValueHistoryEntity getRecentProcedureHistory() {
    if (procedure == null) {
      return new MasterListValueHistoryEntity();
    }
    for (MasterListValueHistoryEntity procedureHistoryEntity : procedure
      .getMasterListValueHistory()) {
      if (procedureHistoryEntity.getToDate() == null) {
        return procedureHistoryEntity;
      }
    }
    return null;
  }

  /**
   * Gets the recent process type history.
   *
   * @return the recent process type history
   */
  public MasterListValueHistoryEntity getRecentProcessTypeHistory() {
    for (MasterListValueHistoryEntity processTypeHistoryEntity : processType
      .getMasterListValueHistory()) {
      if (processTypeHistoryEntity.getToDate() == null) {
        return processTypeHistoryEntity;
      }
    }
    return null;
  }

  /**
   * Gets the exclusion deadline.
   *
   * @return the exclusion deadline
   */
  public Date getExclusionDeadline() {
    return exclusionDeadline;
  }

  /**
   * Sets the exclusion deadline.
   *
   * @param exclusionDeadline the new exclusion deadline
   */

  public void setExclusionDeadline(Date exclusionDeadline) {
    this.exclusionDeadline = exclusionDeadline;
  }

  /**
   * Gets the status.
   *
   * @return the status
   */
  public String getStatus() {
    return status;
  }

  /**
   * Sets the status.
   *
   * @param status the new status
   */
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * Gets the first level exclusion date.
   *
   * @return the first level exclusion date
   */
  public Date getFirstLevelExclusionDate() {
    return firstLevelExclusionDate;
  }

  /**
   * Sets the first level exclusion date.
   *
   * @param firstLevelExclusionDate the new first level exclusion date
   */
  public void setFirstLevelExclusionDate(Date firstLevelExclusionDate) {
    this.firstLevelExclusionDate = firstLevelExclusionDate;
  }

  /**
   * Gets the custom price formula.
   *
   * @return the custom price formula
   */
  public String getCustomPriceFormula() {
    return customPriceFormula;
  }

  /**
   * Sets the custom price formula.
   *
   * @param customPriceFormula the new custom price formula
   */
  public void setCustomPriceFormula(String customPriceFormula) {
    this.customPriceFormula = customPriceFormula;
  }

  /**
   * Gets the custom operating cost formula.
   *
   * @return the custom operating cost formula
   */
  public String getCustomOperatingCostFormula() {
    return customOperatingCostFormula;
  }

  /**
   * Sets the custom operating cost formula.
   *
   * @param customOperatingCostFormula the new custom operating cost formula
   */
  public void setCustomOperatingCostFormula(String customOperatingCostFormula) {
    this.customOperatingCostFormula = customOperatingCostFormula;
  }

  /**
   * Gets the examination is locked.
   *
   * @return the examination is locked
   */
  public Boolean getExaminationIsLocked() {
    return examinationIsLocked;
  }

  /**
   * Sets the examination is locked.
   *
   * @param examinationIsLocked the new examination is locked
   */
  public void setExaminationIsLocked(Boolean examinationIsLocked) {
    this.examinationIsLocked = examinationIsLocked;
  }

  /**
   * Gets the award is locked.
   *
   * @return the award is locked
   */
  public Boolean getAwardIsLocked() {
    return awardIsLocked;
  }

  /**
   * Sets the award is locked.
   *
   * @param awardIsLocked the new award is locked
   */
  public void setAwardIsLocked(Boolean awardIsLocked) {
    this.awardIsLocked = awardIsLocked;
  }

  /**
   * Gets Examination locking time.
   *
   * @return Examination locking time.
   */
  public Timestamp getExaminationLockedTime() {
    return examinationLockedTime;
  }

  /**
   * Sets the examination locking time.
   *
   * @param examinationLockedTime
   */
  public void setExaminationLockedTime(Timestamp examinationLockedTime) {
    this.examinationLockedTime = examinationLockedTime;
  }

  /**
   * Gets Award locking time.
   *
   * @return Award locking time.
   */
  public Timestamp getAwardLockedTime() {
    return awardLockedTime;
  }

  /**
   * Sets award locking time
   *
   * @param awardLockedTime
   */
  public void setAwardLockedTime(Timestamp awardLockedTime) {
    this.awardLockedTime = awardLockedTime;
  }

  /**
   * Gets the user who locked the examination.
   *
   * @return the user who locked the examination.
   */
  public String getExaminationLockedBy() {
    return examinationLockedBy;
  }

  /**
   * Sets the user who locks the examination
   *
   * @param examinationBy
   */
  public void setExaminationLockedBy(String examinationBy) {
    this.examinationLockedBy = examinationBy;
  }


  /**
   * Gets the user who locks the award.
   *
   * @return the user who locks the award.
   */
  public String getAwardLockedBy() {
    return awardLockedBy;
  }

  /**
   * Sets the user who locks the award.
   *
   * @param awardLockedBy
   */
  public void setAwardLockedBy(String awardLockedBy) {
    this.awardLockedBy = awardLockedBy;
  }

  /**
   * Gets the tender status history.
   *
   * @return the tender status history
   */
  public Set<TenderStatusHistoryEntity> getTenderStatusHistory() {
    return tenderStatusHistory;
  }

  /**
   * Sets the tender status history.
   *
   * @param tenderStatusHistory the new tender status history
   */
  public void setTenderStatusHistory(Set<TenderStatusHistoryEntity> tenderStatusHistory) {
    this.tenderStatusHistory = tenderStatusHistory;
  }


  /**
   * Gets the checks if is geko entry by manual award.
   *
   * @return the checks if is geko entry by manual award
   */
  public Boolean getIsGekoEntryByManualAward() {
    return isGekoEntryByManualAward;
  }

  /**
   * Sets the checks if is geko entry by manual award.
   *
   * @param isGekoEntryByManualAward the new checks if is geko entry by manual award
   */
  public void setIsGekoEntryByManualAward(Boolean isGekoEntryByManualAward) {
    this.isGekoEntryByManualAward = isGekoEntryByManualAward;
  }

  /**
   * Gets the user who clicked the Submittentenliste prüfen button.
   *
   * @return the submittentListCheckedBy
   */
  public String getSubmittentListCheckedBy() {
    return submittentListCheckedBy;
  }

  /**
   * Sets the user who clicked the Submittentenliste prüfen button.
   *
   * @param submittentListCheckedBy the user who clicked the button
   */
  public void setSubmittentListCheckedBy(String submittentListCheckedBy) {
    this.submittentListCheckedBy = submittentListCheckedBy;
  }

  /**
   * Gets the timestamp of the Submittentenliste prüfen button click.
   *
   * @return the submittentListCheckedOn
   */
  public Long getSubmittentListCheckedOn() {
    return submittentListCheckedOn;
  }

  /**
   * Sets the timestamp of the Submittentenliste prüfen button click.
   *
   * @param submittentListCheckedOn the timestamp
   */
  public void setSubmittentListCheckedOn(Long submittentListCheckedOn) {
    this.submittentListCheckedOn = submittentListCheckedOn;
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
    return "SubmissionEntity [id=" + super.getId() + ", version=" + super.getVersion()
      + ",  description=" + description + ",  costEstimate="
      + costEstimate + ",  gattTwo=" + gattTwo + ", publicationDate=" + publicationDate
      + ", publicationDateDirectAward=" + publicationDateDirectAward + ", publicationDateAward="
      + publicationDateAward + ",  pmDepartmentName=" + pmDepartmentName + ", isLocked="
      + isLocked + ", loanApproval=" + loanApproval + ", firstDeadline=" + firstDeadline
      + ", secondDeadline=" + secondDeadline + ", applicationOpeningDate="
      + applicationOpeningDate + ", offerOpeningDate=" + offerOpeningDate + ", firstLoggedBy="
      + firstLoggedBy + ", secondLoggedBy=" + secondLoggedBy + ", notes=" + notes
      + ", isServiceTender=" + isServiceTender + ", isGekoEntry=" + isGekoEntry + ", minGrade="
      + minGrade + ", maxGrade=" + maxGrade + ", aboveThreshold=" + aboveThreshold
      + ", awardMinGrade=" + awardMinGrade + ", awardMaxGrade=" + awardMaxGrade
      + ",  addedAwardRecipients=" + addedAwardRecipients + ", evaluationThrough="
      + evaluationThrough + ", commissionProcurementProposalDate="
      + commissionProcurementProposalDate + ", commissionProcurementProposalBusiness="
      + commissionProcurementProposalBusiness + ", commissionProcurementProposalObject="
      + commissionProcurementProposalObject
      + ", commissionProcurementProposalSuitabilityAuditDropdown="
      + commissionProcurementProposalSuitabilityAuditDropdown
      + ", commissionProcurementProposalSuitabilityAuditText="
      + commissionProcurementProposalSuitabilityAuditText
      + ", commissionProcurementProposalPreRemarks=" + commissionProcurementProposalPreRemarks
      + ", commissionProcurementProposalReservation=" + commissionProcurementProposalReservation
      + ", commissionProcurementProposalReasonGiven=" + commissionProcurementProposalReasonGiven
      + ", commissionProcurementDecisionRecommendation="
      + commissionProcurementDecisionRecommendation + ",  createdOn=" + createdOn + ", createdBy="
      + createdBy + ", isPmDepartmentNameUpdated=" + isPmDepartmentNameUpdated
      + ", isPmExternalUpdated=" + isPmExternalUpdated + ", isProcedureUpdated="
      + isProcedureUpdated + ", isGattTwoUpdated=" + isGattTwoUpdated + ",  exclusionDeadline="
      + exclusionDeadline + ", status=" + status + ", firstLevelExclusionDate="
      + firstLevelExclusionDate + ", customPriceFormula=" + customPriceFormula
      + ", customOperatingCostFormula=" + customOperatingCostFormula + ", examinationIsLocked="
      + examinationIsLocked + ", awardIsLocked=" + awardIsLocked + "]";
  }
}
