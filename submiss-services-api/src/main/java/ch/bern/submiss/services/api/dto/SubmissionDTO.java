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

import ch.bern.submiss.services.api.constants.ConstructionPermit;
import ch.bern.submiss.services.api.constants.LoanApproval;
import ch.bern.submiss.services.api.constants.Process;
import ch.bern.submiss.services.api.constants.TenderStatus;
import ch.bern.submiss.services.api.util.View;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * The Class SubmissionDTO.
 */
@JsonIgnoreProperties(value = {"submittents"})
public class SubmissionDTO extends AbstractDTO {

  /**
   * The project.
   */
  @JsonView(View.Public.class)
  private ProjectDTO project;

  /**
   * The work type.
   */
  @JsonView(View.Public.class)
  private MasterListValueHistoryDTO workType;

  /**
   * The description.
   */
  @JsonView(View.Internal.class)
  private String description;

  /**
   * The process.
   */
  @JsonView(View.Internal.class)
  private Process process;

  /**
   * The cost estimate.
   */
  @JsonView(View.Internal.class)
  private BigDecimal costEstimate;

  /**
   * The process type.
   */
  @JsonView(View.Internal.class)
  private MasterListValueHistoryDTO processType;

  /**
   * The gatt two.
   */
  @JsonView(View.Internal.class)
  private Boolean gattTwo;

  /**
   * The publication date.
   */
  @JsonView(View.Internal.class)
  private Date publicationDate;

  /**
   * The publication date direct award.
   */
  @JsonView(View.Internal.class)
  private Date publicationDateDirectAward;

  /**
   * The publication date award.
   */
  @JsonView(View.Internal.class)
  private Date publicationDateAward;

  /**
   * The procedure.
   */
  @JsonView(View.Internal.class)
  private MasterListValueHistoryDTO procedure;

  /**
   * The pm department name.
   */
  @JsonView(View.Internal.class)
  private String pmDepartmentName;

  /**
   * The pm external.
   */
  @JsonView(View.Internal.class)
  private CompanyDTO pmExternal;

  /**
   * The is locked.
   */
  @JsonView(View.Internal.class)
  private Boolean isLocked;

  /**
   * The construction permit.
   */
  @JsonView(View.Internal.class)
  private ConstructionPermit constructionPermit;

  /**
   * The loan approval.
   */
  @JsonView(View.Internal.class)
  private LoanApproval loanApproval;

  /**
   * The first deadline.
   */
  @JsonView(View.Internal.class)
  private Date firstDeadline;

  /**
   * The second deadline.
   */
  @JsonView(View.Internal.class)
  private Date secondDeadline;

  /**
   * The application opening date.
   */
  @JsonView(View.Internal.class)
  private Date applicationOpeningDate;

  /**
   * The offer opening date.
   */
  @JsonView(View.Internal.class)
  private Date offerOpeningDate;

  /**
   * The first logged by.
   */
  @JsonView(View.Internal.class)
  private String firstLoggedBy;

  /**
   * The second logged by.
   */
  @JsonView(View.Internal.class)
  private String secondLoggedBy;

  /**
   * The notes.
   */
  @JsonView(View.Internal.class)
  private String notes;

  /**
   * The is service tender.
   */
  @JsonView(View.Internal.class)
  private Boolean isServiceTender;

  /**
   * The is geko entry.
   */
  @JsonView(View.Internal.class)
  private Boolean isGekoEntry;

  /**
   * The reason free award.
   */
  @JsonView(View.Internal.class)
  private MasterListValueHistoryDTO reasonFreeAward;

  /**
   * The submittents.
   */
  @JsonView(View.Internal.class)
  @JsonIgnore
  private List<SubmittentDTO> submittents;

  /**
   * The min grade.
   */
  @JsonView(View.Internal.class)
  private BigDecimal minGrade;

  /**
   * The max grade.
   */
  @JsonView(View.Internal.class)
  private BigDecimal maxGrade;

  /**
   * The change for sec.
   */
  @JsonView(View.Internal.class)
  private Boolean changeForSec;

  /**
   * The is locked changed.
   */
  @JsonView(View.Internal.class)
  private Boolean isLockedChanged;

  /**
   * The above threshold.
   */
  @JsonView(View.Internal.class)
  private Boolean aboveThreshold;

  /**
   * The award min grade.
   */
  @JsonView(View.Internal.class)
  private BigDecimal awardMinGrade;

  /**
   * The award max grade.
   */
  @JsonView(View.Internal.class)
  private BigDecimal awardMaxGrade;

  /**
   * The operating cost formula.
   */
  @JsonView(View.Internal.class)
  private MasterListValueHistoryDTO operatingCostFormula;

  /**
   * The price formula.
   */
  @JsonView(View.Internal.class)
  private MasterListValueHistoryDTO priceFormula;

  /**
   * The added award recipients.
   */
  @JsonView(View.Internal.class)
  private BigDecimal addedAwardRecipients;

  /**
   * The evaluation through.
   */
  @JsonView(View.Internal.class)
  private String evaluationThrough;

  /**
   * The commission procurement proposal date.
   */
  @JsonView(View.Internal.class)
  private Date commissionProcurementProposalDate;

  /**
   * The commission procurement proposal business.
   */
  @JsonView(View.Internal.class)
  private BigDecimal commissionProcurementProposalBusiness;

  /**
   * The commission procurement proposal object.
   */
  @JsonView(View.Internal.class)
  private String commissionProcurementProposalObject;

  /**
   * The commission procurement proposal suitability audit dropdown.
   */
  @JsonView(View.Internal.class)
  private String commissionProcurementProposalSuitabilityAuditDropdown;

  /**
   * The commission procurement proposal suitability audit text.
   */
  @JsonView(View.Internal.class)
  private String commissionProcurementProposalSuitabilityAuditText;

  /**
   * The commission procurement proposal pre remarks.
   */
  @JsonView(View.Internal.class)
  private String commissionProcurementProposalPreRemarks;

  /**
   * The commission procurement proposal reservation.
   */
  @JsonView(View.Internal.class)
  private String commissionProcurementProposalReservation;

  /**
   * The commission procurement proposal reason given.
   */
  @JsonView(View.Internal.class)
  private String commissionProcurementProposalReasonGiven;

  /**
   * The commission procurement decision recommendation.
   */
  @JsonView(View.Internal.class)
  private String commissionProcurementDecisionRecommendation;

  /**
   * The old process, it is stored so that it can be checked whether an update in the security
   * resources in delete document is necessary.
   */
  @JsonView(View.Internal.class)
  private Process oldProcess;

  /**
   * The current state.
   */
  @JsonView(View.Internal.class)
  private TenderStatus currentState;

  /**
   * The current process. It is used to store the string value of Process in the rule.
   */
  @JsonView(View.Internal.class)
  private String currentProcess;

  /**
   * The submission cancel.
   */
  @JsonView(View.Internal.class)
  private List<SubmissionCancelDTO> submissionCancel;

  /**
   * Whether field pm department name is updated.
   */
  @JsonView(View.Internal.class)
  private Boolean isPmDepartmentNameUpdated;

  /**
   * Whether field pm external is updated.
   */
  @JsonView(View.Internal.class)
  private Boolean isPmExternalUpdated;

  /**
   * Whether field procedure is updated.
   */
  @JsonView(View.Internal.class)
  private Boolean isProcedureUpdated;

  /**
   * Whether field gatt two is updated.
   */
  @JsonView(View.Internal.class)
  private Boolean isGattTwoUpdated;

  /**
   * The created on.
   */
  @JsonView(View.Internal.class)
  private Timestamp createdOn;

  /**
   * The updated on.
   */
  @JsonView(View.Internal.class)
  private Timestamp updatedOn;

  /**
   * The created by.
   */
  @JsonView(View.Internal.class)
  private String createdBy;

  /*
   * Fields reportCompanies - reportAvailableDate - reportAmount - reportAmountTotal -
   * reportAwardedSubmittentName - reportAwardedSubmittentAmount are used only for the generation of
   * reports.
   */

  /**
   * The report companies.
   */
  @JsonView(View.Internal.class)
  private List<String> reportCompanies;

  /**
   * The report available Date.
   */
  @JsonView(View.Internal.class)
  private Date reportAvailableDate;

  /**
   * The exclusion deadline.
   */
  @JsonView(View.Internal.class)
  private Date exclusionDeadline;

  /**
   * The report amount.
   */
  @JsonView(View.Internal.class)
  private List<BigDecimal> reportAmount;

  /**
   * The report awarded submittent.
   */
  @JsonView(View.Internal.class)
  private String reportAwardedSubmittentName;

  /**
   * The report awarded submittent amount.
   */
  @JsonView(View.Internal.class)
  private BigDecimal reportAwardedSubmittentAmount;

  /**
   * The report amount total.
   */
  @JsonView(View.Internal.class)
  private BigDecimal reportAmountTotal;

  /**
   * The status.
   */
  @JsonView(View.Internal.class)
  private String status;

  /**
   * The first level exclusion date.
   */
  private Date firstLevelExclusionDate;

  /**
   * The custom operating cost formula.
   */
  @JsonView(View.Internal.class)
  private String customOperatingCostFormula;

  /**
   * The custom price formula.
   */
  @JsonView(View.Internal.class)
  private String customPriceFormula;

  /**
   * The examination is locked.
   */
  @JsonView(View.Internal.class)
  private Boolean examinationIsLocked;

  /**
   * The award is locked.
   */
  @JsonView(View.Internal.class)
  private Boolean awardIsLocked;

  /**
   * The examination locking timestamp.
   */
  @JsonView(View.Internal.class)
  private Timestamp examinationLockedTime;

  /**
   * The award locking timestamp.
   */
  @JsonView(View.Internal.class)
  private Timestamp awardLockedTime;

  /**
   * The user who locked the examination.
   */
  @JsonView(View.Internal.class)
  private String examinationLockedBy;

  /**
   * The user who locked the award.
   */
  @JsonView(View.Internal.class)
  private String awardLockedBy;

  /**
   * The is geko entry.
   */
  @JsonView(View.Internal.class)
  private Boolean isGekoEntryByManualAward;

  /**
   * The submittentListCheckedBy.
   */
  @JsonView(View.Internal.class)
  private String submittentListCheckedBy;

  /**
   * The submittentListCheckedOn.
   */
  @JsonView(View.Internal.class)
  private Long submittentListCheckedOn;
  /**
   * The legal hearing terminate.
   */
  private List<LegalHearingTerminateDTO> legalHearingTerminate;

  /**
   * The pageRequestedOn timestamp.
   */
  @JsonView(View.Internal.class)
  private Timestamp pageRequestedOn;

  /**
   * The noAwardTender.
   */
  @JsonView(View.Internal.class)
  private Boolean noAwardTender;

  /**
   * The number of passing applicants to the 2nd stage of Selektiv.
   */
  @JsonView(View.Internal.class)
  private Integer passingApplicants;

  /**
   * Gets the report companies.
   *
   * @return the report companies
   */
  public List<String> getReportCompanies() {
    return reportCompanies;
  }

  /**
   * Sets the report companies.
   *
   * @param reportCompanies the new report companies
   */
  public void setReportCompanies(List<String> reportCompanies) {
    this.reportCompanies = reportCompanies;
  }

  /**
   * Gets the project.
   *
   * @return the project
   */
  public ProjectDTO getProject() {
    return project;
  }

  /**
   * Sets the project.
   *
   * @param project the new project
   */
  public void setProject(ProjectDTO project) {
    this.project = project;
  }


  /**
   * Gets the work type.
   *
   * @return the work type
   */
  public MasterListValueHistoryDTO getWorkType() {
    return workType;
  }

  /**
   * Sets the work type.
   *
   * @param workType the new work type
   */
  public void setWorkType(MasterListValueHistoryDTO workType) {
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
  public MasterListValueHistoryDTO getProcessType() {
    return processType;
  }

  /**
   * Sets the process type.
   *
   * @param processType the new process type
   */
  public void setProcessType(MasterListValueHistoryDTO processType) {
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
  public MasterListValueHistoryDTO getProcedure() {
    return procedure;
  }

  /**
   * Sets the procedure.
   *
   * @param procedure the new procedure
   */
  public void setProcedure(MasterListValueHistoryDTO procedure) {
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
  public CompanyDTO getPmExternal() {
    return pmExternal;
  }

  /**
   * Sets the pm external.
   *
   * @param pmExternal the new pm external
   */
  public void setPmExternal(CompanyDTO pmExternal) {
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
  public MasterListValueHistoryDTO getReasonFreeAward() {
    return reasonFreeAward;
  }

  /**
   * Sets the reason free award.
   *
   * @param reasonFreeAward the new reason free award
   */
  public void setReasonFreeAward(MasterListValueHistoryDTO reasonFreeAward) {
    this.reasonFreeAward = reasonFreeAward;
  }

  /**
   * Gets the submittents.
   *
   * @return the submittents
   */
  @JsonProperty
  public List<SubmittentDTO> getSubmittents() {
    return submittents;
  }

  /**
   * Sets the submittents.
   *
   * @param submittents the new submittents
   */
  @JsonIgnore
  public void setSubmittents(List<SubmittentDTO> submittents) {
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
   * Gets the change for sec.
   *
   * @return the change for sec
   */
  public Boolean getChangeForSec() {
    return changeForSec;
  }

  /**
   * Sets the change for sec.
   *
   * @param changeForSec the new change for sec
   */
  public void setChangeForSec(Boolean changeForSec) {
    this.changeForSec = changeForSec;
  }

  /**
   * Gets the checks if is locked changed.
   *
   * @return the checks if is locked changed
   */
  public Boolean getIsLockedChanged() {
    return isLockedChanged;
  }

  /**
   * Sets the checks if is locked changed.
   *
   * @param isLockedChanged the new checks if is locked changed
   */
  public void setIsLockedChanged(Boolean isLockedChanged) {
    this.isLockedChanged = isLockedChanged;
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
  public MasterListValueHistoryDTO getOperatingCostFormula() {
    return operatingCostFormula;
  }

  /**
   * Sets the operating cost formula.
   *
   * @param operatingCostFormula the new operating cost formula
   */
  public void setOperatingCostFormula(MasterListValueHistoryDTO operatingCostFormula) {
    this.operatingCostFormula = operatingCostFormula;
  }

  /**
   * Gets the price formula.
   *
   * @return the price formula
   */
  public MasterListValueHistoryDTO getPriceFormula() {
    return priceFormula;
  }

  /**
   * Sets the price formula.
   *
   * @param priceFormula the new price formula
   */
  public void setPriceFormula(MasterListValueHistoryDTO priceFormula) {
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
   * Gets the old process.
   *
   * @return the old process
   */
  public Process getOldProcess() {
    return oldProcess;
  }

  /**
   * Sets the old process.
   *
   * @param oldProcess the new old process
   */
  public void setOldProcess(Process oldProcess) {
    this.oldProcess = oldProcess;
  }

  /**
   * Gets the current state.
   *
   * @return the current state
   */
  public TenderStatus getCurrentState() {
    return currentState;
  }

  /**
   * Sets the current state.
   *
   * @param currentState the new current state
   */
  public void setCurrentState(TenderStatus currentState) {
    this.currentState = currentState;
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
   * Gets the current process.
   *
   * @return the current process
   */
  public String getCurrentProcess() {
    return currentProcess;
  }

  /**
   * Sets the current process.
   *
   * @param currentProcess the new current process
   */
  public void setCurrentProcess(String currentProcess) {
    this.currentProcess = currentProcess;
  }

  /**
   * Gets the submission cancel.
   *
   * @return the submission cancel
   */
  public List<SubmissionCancelDTO> getSubmissionCancel() {
    return submissionCancel;
  }

  /**
   * Sets the submission cancel.
   *
   * @param submissionCancel the new submission cancel
   */
  public void setSubmissionCancel(List<SubmissionCancelDTO> submissionCancel) {
    this.submissionCancel = submissionCancel;
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
   * Gets the legal hearing terminate.
   *
   * @return the legal hearing terminate
   */
  public List<LegalHearingTerminateDTO> getLegalHearingTerminate() {
    return legalHearingTerminate;
  }

  /**
   * Sets the legal hearing terminate.
   *
   * @param legalHearingTerminate the new legal hearing terminate
   */
  public void setLegalHearingTerminate(List<LegalHearingTerminateDTO> legalHearingTerminate) {
    this.legalHearingTerminate = legalHearingTerminate;
  }

  /**
   * Gets the report available date.
   *
   * @return the report available date
   */
  public Date getReportAvailableDate() {
    return reportAvailableDate;
  }

  /**
   * Sets the report available date.
   *
   * @param reportAvailableDate the new report available date
   */
  public void setReportAvailableDate(Date reportAvailableDate) {
    this.reportAvailableDate = reportAvailableDate;
  }

  /**
   * Gets the report amount.
   *
   * @return the report amount
   */
  public List<BigDecimal> getReportAmount() {
    return reportAmount;
  }

  /**
   * Sets the report amount.
   *
   * @param reportAmount the new report amount
   */
  public void setReportAmount(List<BigDecimal> reportAmount) {
    this.reportAmount = reportAmount;
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
   * Gets the report awarded submittent name.
   *
   * @return the report awarded submittent name
   */
  public String getReportAwardedSubmittentName() {
    return reportAwardedSubmittentName;
  }

  /**
   * Sets the report awarded submittent name.
   *
   * @param reportAwardedSubmittentName the new report awarded submittent name
   */
  public void setReportAwardedSubmittentName(String reportAwardedSubmittentName) {
    this.reportAwardedSubmittentName = reportAwardedSubmittentName;
  }

  /**
   * Gets the report awarded submittent amount.
   *
   * @return the report awarded submittent amount
   */
  public BigDecimal getReportAwardedSubmittentAmount() {
    return reportAwardedSubmittentAmount;
  }

  /**
   * Sets the report awarded submittent amount.
   *
   * @param reportAwardedSubmittentAmount the new report awarded submittent amount
   */

  public void setReportAwardedSubmittentAmount(BigDecimal reportAwardedSubmittentAmount) {
    this.reportAwardedSubmittentAmount = reportAwardedSubmittentAmount;
  }


  /**
   * Gets the report amount total.
   *
   * @return the report amount total
   */
  public BigDecimal getReportAmountTotal() {
    return reportAmountTotal;
  }

  /**
   * Sets the report amount total.
   *
   * @param reportAmountTotal the new report amount total
   */
  public void setReportAmountTotal(BigDecimal reportAmountTotal) {
    this.reportAmountTotal = reportAmountTotal;
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
   * Gets the pageRequestedOn.
   *
   * @return the pageRequestedOn
   */
  public Timestamp getPageRequestedOn() {
    return pageRequestedOn;
  }

  /**
   * Sets pageRequestedOn as the timestamp of the GET request.
   *
   * @param pageRequestedOn the pageRequestedOn
   */
  public void setPageRequestedOn(Timestamp pageRequestedOn) {
    this.pageRequestedOn = pageRequestedOn;
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

  public Boolean getNoAwardTender() {
    return noAwardTender;
  }

  public void setNoAwardTender(Boolean noAwardTender) {
    this.noAwardTender = noAwardTender;
  }

  public Integer getPassingApplicants() {
    return passingApplicants;
  }

  public void setPassingApplicants(Integer passingApplicants) {
    this.passingApplicants = passingApplicants;
  }

  @Override
  public String toString() {
    return "SubmissionDTO [id=" + super.getId() + ", version=" + super.getVersion()
      + ", description=" + description + ", process=" + process
      + ", costEstimate=" + costEstimate + ", gattTwo=" + gattTwo + ", publicationDate="
      + publicationDate + ", publicationDateDirectAward=" + publicationDateDirectAward
      + ", publicationDateAward=" + publicationDateAward + ", pmDepartmentName="
      + pmDepartmentName + ", pmExternal=" + pmExternal + ", isLocked=" + isLocked
      + ", constructionPermit=" + constructionPermit + ", loanApproval=" + loanApproval
      + ", firstDeadline=" + firstDeadline + ", secondDeadline=" + secondDeadline
      + ", applicationOpeningDate=" + applicationOpeningDate + ", offerOpeningDate="
      + offerOpeningDate + ", firstLoggedBy=" + firstLoggedBy + ", secondLoggedBy="
      + secondLoggedBy + ", notes=" + notes + ", isServiceTender=" + isServiceTender
      + ", isGekoEntry=" + isGekoEntry + ", minGrade=" + minGrade + ", maxGrade=" + maxGrade
      + ", changeForSec=" + changeForSec + ", isLockedChanged=" + isLockedChanged
      + ", aboveThreshold=" + aboveThreshold + ", awardMinGrade=" + awardMinGrade
      + ", awardMaxGrade=" + awardMaxGrade + ", addedAwardRecipients=" + addedAwardRecipients
      + ", evaluationThrough=" + evaluationThrough + ", commissionProcurementProposalDate="
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
      + commissionProcurementDecisionRecommendation + ", oldProcess=" + oldProcess
      + ", currentState=" + currentState + ", currentProcess=" + currentProcess
      + ", isPmDepartmentNameUpdated=" + isPmDepartmentNameUpdated + ", isPmExternalUpdated="
      + isPmExternalUpdated + ", isProcedureUpdated=" + isProcedureUpdated + ", isGattTwoUpdated="
      + isGattTwoUpdated + ", createdOn=" + createdOn + ", createdBy=" + createdBy
      + ", reportCompanies=" + reportCompanies + ", reportAvailableDate=" + reportAvailableDate
      + ", exclusionDeadline=" + exclusionDeadline + ", reportAmount=" + reportAmount
      + ", reportAwardedSubmittentName=" + reportAwardedSubmittentName
      + ", reportAwardedSubmittentAmount=" + reportAwardedSubmittentAmount
      + ", reportAmountTotal=" + reportAmountTotal + ", status=" + status
      + ", firstLevelExclusionDate=" + firstLevelExclusionDate + ", customOperatingCostFormula="
      + customOperatingCostFormula + ", customPriceFormula=" + customPriceFormula
      + ", examinationIsLocked=" + examinationIsLocked + ", awardIsLocked=" + awardIsLocked
      + ", legalHearingTerminate=" + legalHearingTerminate + "]";
  }
}
