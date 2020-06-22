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

import ch.bern.submiss.services.api.constants.ConstructionPermit;
import ch.bern.submiss.services.api.constants.LoanApproval;
import ch.bern.submiss.services.api.constants.Process;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SubmissionForm extends AbstractForm {

  private ProjectForm project;
  private MasterListValueHistoryForm workType;
  private String description;
  private Process process;
  private BigDecimal costEstimate;
  private MasterListValueHistoryForm processType;
  private Boolean gattTwo;
  private Date publicationDate;
  private Date publicationDateDirectAward;
  private Date publicationDateAward;
  private MasterListValueHistoryForm procedure;
  private String pmDepartmentName;
  private CompanyForm pmExternal;
  private Boolean isLocked;
  private ConstructionPermit constructionPermit;
  private LoanApproval loanApproval;
  private Date firstDeadline;
  private Date secondDeadline;
  private Date applicationOpeningDate;
  private Date offerOpeningDate;
  private String firstLoggedBy;
  private String secondLoggedBy;
  private String notes;
  private Boolean isServiceTender;
  private Boolean isGekoEntry;
  private MasterListValueHistoryForm reasonFreeAward;
  private BigDecimal minGrade;
  private BigDecimal maxGrade;
  private Boolean changeForSec;
  private Boolean isLockedChanged;
  private Boolean aboveThreshold;
  private BigDecimal awardMinGrade;
  private BigDecimal awardMaxGrade;
  private BigDecimal addedAwardRecipients;
  private String evaluationThrough;
  private Date commissionProcurementProposalDate;
  private BigDecimal commissionProcurementProposalBusiness;
  private String commissionProcurementProposalObject;
  private String commissionProcurementProposalSuitabilityAuditDropdown;
  private String commissionProcurementProposalSuitabilityAuditText;
  private String commissionProcurementProposalPreRemarks;
  private String commissionProcurementProposalReservation;
  private String commissionProcurementProposalReasonGiven;
  private String commissionProcurementDecisionRecommendation;
  private Process oldProcess;
  private Boolean isPmDepartmentNameUpdated;
  private Boolean isPmExternalUpdated;
  private Boolean isProcedureUpdated;
  private Boolean isGattTwoUpdated;
  private String createdBy;
  private Timestamp createdOn;
  private Timestamp updatedOn;
  private MasterListValueHistoryForm operatingCostFormula;
  private MasterListValueHistoryForm priceFormula;
  private String status;
  private String customPriceFormula;
  private String customOperatingCostFormula;
  private Boolean isGekoEntryByManualAward;
  private Boolean noAwardTender;

  public ProjectForm getProject() {
    return project;
  }

  public void setProject(ProjectForm project) {
    this.project = project;
  }

  public MasterListValueHistoryForm getWorkType() {
    return workType;
  }

  public void setWorkType(MasterListValueHistoryForm workType) {
    this.workType = workType;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Process getProcess() {
    return process;
  }

  public void setProcess(Process process) {
    this.process = process;
  }

  public BigDecimal getCostEstimate() {
    return costEstimate;
  }

  public void setCostEstimate(BigDecimal costEstimate) {
    this.costEstimate = costEstimate;
  }

  public MasterListValueHistoryForm getProcessType() {
    return processType;
  }

  public void setProcessType(MasterListValueHistoryForm processType) {
    this.processType = processType;
  }

  public Boolean getGattTwo() {
    return gattTwo;
  }

  public void setGattTwo(Boolean gattTwo) {
    this.gattTwo = gattTwo;
  }

  public Date getPublicationDate() {
    return publicationDate;
  }

  public void setPublicationDate(Date publicationDate) {
    this.publicationDate = publicationDate;
  }

  public Date getPublicationDateDirectAward() {
    return publicationDateDirectAward;
  }

  public void setPublicationDateDirectAward(Date publicationDateDirectAward) {
    this.publicationDateDirectAward = publicationDateDirectAward;
  }

  public Date getPublicationDateAward() {
    return publicationDateAward;
  }

  public void setPublicationDateAward(Date publicationDateAward) {
    this.publicationDateAward = publicationDateAward;
  }

  public MasterListValueHistoryForm getProcedure() {
    return procedure;
  }

  public void setProcedure(MasterListValueHistoryForm procedure) {
    this.procedure = procedure;
  }

  public String getPmDepartmentName() {
    return pmDepartmentName;
  }

  public void setPmDepartmentName(String pmDepartmentName) {
    this.pmDepartmentName = pmDepartmentName;
  }

  public CompanyForm getPmExternal() {
    return pmExternal;
  }

  public void setPmExternal(CompanyForm pmExternal) {
    this.pmExternal = pmExternal;
  }

  public Boolean getIsLocked() {
    return isLocked;
  }

  public void setIsLocked(Boolean isLocked) {
    this.isLocked = isLocked;
  }

  public ConstructionPermit getConstructionPermit() {
    return constructionPermit;
  }

  public void setConstructionPermit(ConstructionPermit constructionPermit) {
    this.constructionPermit = constructionPermit;
  }

  public LoanApproval getLoanApproval() {
    return loanApproval;
  }

  public void setLoanApproval(LoanApproval loanApproval) {
    this.loanApproval = loanApproval;
  }

  public Date getFirstDeadline() {
    return firstDeadline;
  }

  public void setFirstDeadline(Date firstDeadline) {
    this.firstDeadline = firstDeadline;
  }

  public Date getSecondDeadline() {
    return secondDeadline;
  }

  public void setSecondDeadline(Date secondDeadline) {
    this.secondDeadline = secondDeadline;
  }

  public Date getApplicationOpeningDate() {
    return applicationOpeningDate;
  }

  public void setApplicationOpeningDate(Date applicationOpeningDate) {
    this.applicationOpeningDate = applicationOpeningDate;
  }

  public Date getOfferOpeningDate() {
    return offerOpeningDate;
  }

  public void setOfferOpeningDate(Date offerOpeningDate) {
    this.offerOpeningDate = offerOpeningDate;
  }

  public String getFirstLoggedBy() {
    return firstLoggedBy;
  }

  public void setFirstLoggedBy(String firstLoggedBy) {
    this.firstLoggedBy = firstLoggedBy;
  }

  public String getSecondLoggedBy() {
    return secondLoggedBy;
  }

  public void setSecondLoggedBy(String secondLoggedBy) {
    this.secondLoggedBy = secondLoggedBy;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public Boolean getIsServiceTender() {
    return isServiceTender;
  }

  public void setIsServiceTender(Boolean isServiceTender) {
    this.isServiceTender = isServiceTender;
  }

  public Boolean getIsGekoEntry() {
    return isGekoEntry;
  }

  public void setIsGekoEntry(Boolean isGekoEntry) {
    this.isGekoEntry = isGekoEntry;
  }

  public MasterListValueHistoryForm getReasonFreeAward() {
    return reasonFreeAward;
  }

  public void setReasonFreeAward(MasterListValueHistoryForm reasonFreeAward) {
    this.reasonFreeAward = reasonFreeAward;
  }

  public Boolean getChangeForSec() {
    return changeForSec;
  }

  public void setChangeForSec(Boolean changeForSec) {
    this.changeForSec = changeForSec;
  }

  public Boolean getIsLockedChanged() {
    return isLockedChanged;
  }

  public void setIsLockedChanged(Boolean isLockedChanged) {
    this.isLockedChanged = isLockedChanged;
  }

  public Boolean getAboveThreshold() {
    return aboveThreshold;
  }

  public void setAboveThreshold(Boolean aboveThreshold) {
    this.aboveThreshold = aboveThreshold;
  }

  public Process getOldProcess() {
    return oldProcess;
  }

  public void setOldProcess(Process oldProcess) {
    this.oldProcess = oldProcess;
  }

  public BigDecimal getMinGrade() {
    return minGrade;
  }

  public void setMinGrade(BigDecimal minGrade) {
    this.minGrade = minGrade;
  }

  public BigDecimal getMaxGrade() {
    return maxGrade;
  }

  public void setMaxGrade(BigDecimal maxGrade) {
    this.maxGrade = maxGrade;
  }

  public BigDecimal getAwardMinGrade() {
    return awardMinGrade;
  }

  public void setAwardMinGrade(BigDecimal awardMinGrade) {
    this.awardMinGrade = awardMinGrade;
  }

  public BigDecimal getAwardMaxGrade() {
    return awardMaxGrade;
  }

  public void setAwardMaxGrade(BigDecimal awardMaxGrade) {
    this.awardMaxGrade = awardMaxGrade;
  }

  public BigDecimal getAddedAwardRecipients() {
    return addedAwardRecipients;
  }

  public void setAddedAwardRecipients(BigDecimal addedAwardRecipients) {
    this.addedAwardRecipients = addedAwardRecipients;
  }

  public String getEvaluationThrough() {
    return evaluationThrough;
  }

  public void setEvaluationThrough(String evaluationThrough) {
    this.evaluationThrough = evaluationThrough;
  }

  public Date getCommissionProcurementProposalDate() {
    return commissionProcurementProposalDate;
  }

  public void setCommissionProcurementProposalDate(Date commissionProcurementProposalDate) {
    this.commissionProcurementProposalDate = commissionProcurementProposalDate;
  }

  public BigDecimal getCommissionProcurementProposalBusiness() {
    return commissionProcurementProposalBusiness;
  }

  public void setCommissionProcurementProposalBusiness(
    BigDecimal commissionProcurementProposalBusiness) {
    this.commissionProcurementProposalBusiness = commissionProcurementProposalBusiness;
  }

  public String getCommissionProcurementProposalObject() {
    return commissionProcurementProposalObject;
  }

  public void setCommissionProcurementProposalObject(String commissionProcurementProposalObject) {
    this.commissionProcurementProposalObject = commissionProcurementProposalObject;
  }

  public String getCommissionProcurementProposalSuitabilityAuditDropdown() {
    return commissionProcurementProposalSuitabilityAuditDropdown;
  }

  public void setCommissionProcurementProposalSuitabilityAuditDropdown(
    String commissionProcurementProposalSuitabilityAuditDropdown) {
    this.commissionProcurementProposalSuitabilityAuditDropdown =
      commissionProcurementProposalSuitabilityAuditDropdown;
  }

  public String getCommissionProcurementProposalSuitabilityAuditText() {
    return commissionProcurementProposalSuitabilityAuditText;
  }

  public void setCommissionProcurementProposalSuitabilityAuditText(
    String commissionProcurementProposalSuitabilityAuditText) {
    this.commissionProcurementProposalSuitabilityAuditText =
      commissionProcurementProposalSuitabilityAuditText;
  }

  public String getCommissionProcurementProposalPreRemarks() {
    return commissionProcurementProposalPreRemarks;
  }

  public void setCommissionProcurementProposalPreRemarks(
    String commissionProcurementProposalPreRemarks) {
    this.commissionProcurementProposalPreRemarks = commissionProcurementProposalPreRemarks;
  }

  public String getCommissionProcurementProposalReservation() {
    return commissionProcurementProposalReservation;
  }

  public void setCommissionProcurementProposalReservation(
    String commissionProcurementProposalReservation) {
    this.commissionProcurementProposalReservation = commissionProcurementProposalReservation;
  }

  public String getCommissionProcurementProposalReasonGiven() {
    return commissionProcurementProposalReasonGiven;
  }

  public void setCommissionProcurementProposalReasonGiven(
    String commissionProcurementProposalReasonGiven) {
    this.commissionProcurementProposalReasonGiven = commissionProcurementProposalReasonGiven;
  }

  public String getCommissionProcurementDecisionRecommendation() {
    return commissionProcurementDecisionRecommendation;
  }

  public void setCommissionProcurementDecisionRecommendation(
    String commissionProcurementDecisionRecommendation) {
    this.commissionProcurementDecisionRecommendation = commissionProcurementDecisionRecommendation;
  }

  public Boolean getIsPmDepartmentNameUpdated() {
    return isPmDepartmentNameUpdated;
  }

  public void setIsPmDepartmentNameUpdated(Boolean isPmDepartmentNameUpdated) {
    this.isPmDepartmentNameUpdated = isPmDepartmentNameUpdated;
  }

  public Boolean getIsPmExternalUpdated() {
    return isPmExternalUpdated;
  }

  public void setIsPmExternalUpdated(Boolean isPmExternalUpdated) {
    this.isPmExternalUpdated = isPmExternalUpdated;
  }

  public Boolean getIsProcedureUpdated() {
    return isProcedureUpdated;
  }

  public void setIsProcedureUpdated(Boolean isProcedureUpdated) {
    this.isProcedureUpdated = isProcedureUpdated;
  }

  public Boolean getIsGattTwoUpdated() {
    return isGattTwoUpdated;
  }

  public void setIsGattTwoUpdated(Boolean isGattTwoUpdated) {
    this.isGattTwoUpdated = isGattTwoUpdated;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public Timestamp getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(Timestamp createdOn) {
    this.createdOn = createdOn;
  }

  public MasterListValueHistoryForm getOperatingCostFormula() {
    return operatingCostFormula;
  }

  public void setOperatingCostFormula(MasterListValueHistoryForm operatingCostFormula) {
    this.operatingCostFormula = operatingCostFormula;
  }

  public MasterListValueHistoryForm getPriceFormula() {
    return priceFormula;
  }

  public void setPriceFormula(MasterListValueHistoryForm priceFormula) {
    this.priceFormula = priceFormula;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getCustomPriceFormula() {
    return customPriceFormula;
  }

  public void setCustomPriceFormula(String customPriceFormula) {
    this.customPriceFormula = customPriceFormula;
  }

  public String getCustomOperatingCostFormula() {
    return customOperatingCostFormula;
  }

  public void setCustomOperatingCostFormula(String customOperatingCostFormula) {
    this.customOperatingCostFormula = customOperatingCostFormula;
  }

  public Boolean getIsGekoEntryByManualAward() {
    return isGekoEntryByManualAward;
  }

  public void setIsGekoEntryByManualAward(Boolean isGekoEntryByManualAward) {
    this.isGekoEntryByManualAward = isGekoEntryByManualAward;
  }

  public Timestamp getUpdatedOn() {
    return updatedOn;
  }

  public void setUpdatedOn(Timestamp updatedOn) {
    this.updatedOn = updatedOn;
  }

  public Boolean getNoAwardTender() {
    return noAwardTender;
  }

  public void setNoAwardTender(Boolean noAwardTender) {
    this.noAwardTender = noAwardTender;
  }

  @Override
  public String toString() {
    return "SubmissionForm [id=" + super.getId() + ", version=" + super.getVersion() + ", project="
      + project + ", workType=" + workType
      + ", description=" + description + ", process=" + process + ", costEstimate=" + costEstimate
      + ", processType=" + processType + ", gattTwo=" + gattTwo + ", publicationDate="
      + publicationDate + ", publicationDateDirectAward=" + publicationDateDirectAward
      + ", publicationDateAward=" + publicationDateAward + ", procedure=" + procedure
      + ", pmDepartmentName=" + pmDepartmentName + ", pmExternal=" + pmExternal + ", isLocked="
      + isLocked + ", constructionPermit=" + constructionPermit + ", loanApproval=" + loanApproval
      + ", firstDeadline=" + firstDeadline + ", secondDeadline=" + secondDeadline
      + ", applicationOpeningDate=" + applicationOpeningDate + ", offerOpeningDate="
      + offerOpeningDate + ", firstLoggedBy=" + firstLoggedBy + ", secondLoggedBy="
      + secondLoggedBy + ", notes=" + notes + ", isServiceTender=" + isServiceTender
      + ", isGekoEntry=" + isGekoEntry + ", reasonFreeAward=" + reasonFreeAward
      + ", changeForSec=" + changeForSec + ", isLockedChanged=" + isLockedChanged
      + ", aboveTheshold=" + aboveThreshold + "]";
  }
}
