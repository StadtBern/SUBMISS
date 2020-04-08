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

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * The Class SearchDTO.
 */
public class SearchDTO extends AbstractStammdatenDTO {

  /** The object id. */
  private String objectId;

  /** The project names. */
  private List<String> projectNames;

  /** The procedure id. */
  private String procedureId;

  /** The departments I ds. */
  private List<String> departmentsIDs;

  /** The directorates I ds. */
  private List<String> directoratesIDs;

  /** The work types. */
  private Set<MasterListValueHistoryDTO> workTypes;

  /** The description. */
  private String description;

  /** The pm department name. */
  private String pmDepartmentName;

  /** The selective. */
  private Boolean selective;

  /** The open. */
  private Boolean open;

  /** The invitation. */
  private Boolean invitation;

  /** The negotiated procedure. */
  private Boolean negotiatedProcedure;

  /** The negotiated procedure with competition. */
  private Boolean negotiatedProcedureWithCompetition;

  /** The negotiated procedure above threshold. */
  private Boolean negotiatedProcedureAboveThreshold;

  /** The construction industry. */
  private Boolean constructionIndustry;

  /** The related trades. */
  private Boolean relatedTrades;

  /** The supply contracts. */
  private Boolean supplyContracts;

  /** The d L assignments. */
  private Boolean dLAssignments;

  /** The is service tender. */
  private Boolean isServiceTender;

  /** The company name. */
  private String companyName;

  /** The running. */
  private Boolean running;

  /** The completed. */
  private Boolean completed;

  /** The offer date from. */
  private Date offerDateFrom;

  /** The offer date until. */
  private Date offerDateUntil;

  /** The filter. */
  private FilterDTO filter;

  /** The excluded project. */
  private String excludedProject;



  /**
   * Gets the object id.
   *
   * @return the object id
   */
  public String getObjectId() {
    return objectId;
  }

  /**
   * Sets the object id.
   *
   * @param objectId the new object id
   */
  public void setObjectId(String objectId) {
    this.objectId = objectId;
  }

  /**
   * Gets the project names.
   *
   * @return the project names
   */
  public List<String> getProjectNames() {
    return projectNames;
  }

  /**
   * Sets the project names.
   *
   * @param projectNames the new project names
   */
  public void setProjectNames(List<String> projectNames) {
    this.projectNames = projectNames;
  }

  /**
   * Gets the procedure id.
   *
   * @return the procedure id
   */
  public String getProcedureId() {
    return procedureId;
  }

  /**
   * Sets the procedure id.
   *
   * @param procedureId the new procedure id
   */
  public void setProcedureId(String procedureId) {
    this.procedureId = procedureId;
  }

  /**
   * Gets the departments I ds.
   *
   * @return the departments I ds
   */
  public List<String> getDepartmentsIDs() {
    return departmentsIDs;
  }

  /**
   * Sets the departments I ds.
   *
   * @param departmentsIDs the new departments I ds
   */
  public void setDepartmentsIDs(List<String> departmentsIDs) {
    this.departmentsIDs = departmentsIDs;
  }

  /**
   * Gets the directorates I ds.
   *
   * @return the directorates I ds
   */
  public List<String> getDirectoratesIDs() {
    return directoratesIDs;
  }

  /**
   * Sets the directorates I ds.
   *
   * @param directoratesIDs the new directorates I ds
   */
  public void setDirectoratesIDs(List<String> directoratesIDs) {
    this.directoratesIDs = directoratesIDs;
  }

  /**
   * Gets the filter.
   *
   * @return the filter
   */
  public FilterDTO getFilter() {
    return filter;
  }

  /**
   * Sets the filter.
   *
   * @param filter the new filter
   */
  public void setFilter(FilterDTO filter) {
    this.filter = filter;
  }

  /**
   * Gets the work types.
   *
   * @return the work types
   */
  public Set<MasterListValueHistoryDTO> getWorkTypes() {
    return workTypes;
  }

  /**
   * Sets the work types.
   *
   * @param workTypes the new work types
   */
  public void setWorkTypes(Set<MasterListValueHistoryDTO> workTypes) {
    this.workTypes = workTypes;
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
   * Gets the selective.
   *
   * @return the selective
   */
  public Boolean getSelective() {
    return selective;
  }

  /**
   * Sets the selective.
   *
   * @param selective the new selective
   */
  public void setSelective(Boolean selective) {
    this.selective = selective;
  }

  /**
   * Gets the open.
   *
   * @return the open
   */
  public Boolean getOpen() {
    return open;
  }

  /**
   * Sets the open.
   *
   * @param open the new open
   */
  public void setOpen(Boolean open) {
    this.open = open;
  }

  /**
   * Gets the invitation.
   *
   * @return the invitation
   */
  public Boolean getInvitation() {
    return invitation;
  }

  /**
   * Sets the invitation.
   *
   * @param invitation the new invitation
   */
  public void setInvitation(Boolean invitation) {
    this.invitation = invitation;
  }

  /**
   * Gets the negotiated procedure.
   *
   * @return the negotiated procedure
   */
  public Boolean getNegotiatedProcedure() {
    return negotiatedProcedure;
  }

  /**
   * Sets the negotiated procedure.
   *
   * @param negotiatedProcedure the new negotiated procedure
   */
  public void setNegotiatedProcedure(Boolean negotiatedProcedure) {
    this.negotiatedProcedure = negotiatedProcedure;
  }

  /**
   * Gets the negotiated procedure with competition.
   *
   * @return the negotiated procedure with competition
   */
  public Boolean getNegotiatedProcedureWithCompetition() {
    return negotiatedProcedureWithCompetition;
  }

  /**
   * Sets the negotiated procedure with competition.
   *
   * @param negotiatedProcedureWithCompetition the new negotiated procedure with competition
   */
  public void setNegotiatedProcedureWithCompetition(Boolean negotiatedProcedureWithCompetition) {
    this.negotiatedProcedureWithCompetition = negotiatedProcedureWithCompetition;
  }

  /**
   * Gets the negotiated procedure above threshold.
   *
   * @return the negotiated procedure above threshold
   */
  public Boolean getNegotiatedProcedureAboveThreshold() {
    return negotiatedProcedureAboveThreshold;
  }

  /**
   * Sets the negotiated procedure above threshold.
   *
   * @param negotiatedProcedureAboveThreshold the new negotiated procedure above threshold
   */
  public void setNegotiatedProcedureAboveThreshold(Boolean negotiatedProcedureAboveThreshold) {
    this.negotiatedProcedureAboveThreshold = negotiatedProcedureAboveThreshold;
  }

  /**
   * Gets the construction industry.
   *
   * @return the construction industry
   */
  public Boolean getConstructionIndustry() {
    return constructionIndustry;
  }

  /**
   * Sets the construction industry.
   *
   * @param constructionIndustry the new construction industry
   */
  public void setConstructionIndustry(Boolean constructionIndustry) {
    this.constructionIndustry = constructionIndustry;
  }

  /**
   * Gets the related trades.
   *
   * @return the related trades
   */
  public Boolean getRelatedTrades() {
    return relatedTrades;
  }

  /**
   * Sets the related trades.
   *
   * @param relatedTrades the new related trades
   */
  public void setRelatedTrades(Boolean relatedTrades) {
    this.relatedTrades = relatedTrades;
  }

  /**
   * Gets the supply contracts.
   *
   * @return the supply contracts
   */
  public Boolean getSupplyContracts() {
    return supplyContracts;
  }

  /**
   * Sets the supply contracts.
   *
   * @param supplyContracts the new supply contracts
   */
  public void setSupplyContracts(Boolean supplyContracts) {
    this.supplyContracts = supplyContracts;
  }

  /**
   * Gets the d L assignments.
   *
   * @return the d L assignments
   */
  public Boolean getdLAssignments() {
    return dLAssignments;
  }

  /**
   * Sets the d L assignments.
   *
   * @param dLAssignments the new d L assignments
   */
  public void setdLAssignments(Boolean dLAssignments) {
    this.dLAssignments = dLAssignments;
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
   * Gets the company name.
   *
   * @return the company name
   */
  public String getCompanyName() {
    return companyName;
  }

  /**
   * Sets the company name.
   *
   * @param companyName the new company name
   */
  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  /**
   * Gets the running.
   *
   * @return the running
   */
  public Boolean getRunning() {
    return running;
  }

  /**
   * Sets the running.
   *
   * @param running the new running
   */
  public void setRunning(Boolean running) {
    this.running = running;
  }

  /**
   * Gets the completed.
   *
   * @return the completed
   */
  public Boolean getCompleted() {
    return completed;
  }

  /**
   * Sets the completed.
   *
   * @param completed the new completed
   */
  public void setCompleted(Boolean completed) {
    this.completed = completed;
  }

  /**
   * Gets the offer date from.
   *
   * @return the offer date from
   */
  public Date getOfferDateFrom() {
    return offerDateFrom;
  }

  /**
   * Sets the offer date from.
   *
   * @param offerDateFrom the new offer date from
   */
  public void setOfferDateFrom(Date offerDateFrom) {
    this.offerDateFrom = offerDateFrom;
  }

  /**
   * Gets the offer date until.
   *
   * @return the offer date until
   */
  public Date getOfferDateUntil() {
    return offerDateUntil;
  }

  /**
   * Sets the offer date until.
   *
   * @param offerDateUntil the new offer date until
   */
  public void setOfferDateUntil(Date offerDateUntil) {
    this.offerDateUntil = offerDateUntil;
  }

  /**
   * Gets the excluded project.
   *
   * @return the excluded project
   */
  public String getExcludedProject() {
    return excludedProject;
  }

  /**
   * Sets the excluded project.
   *
   * @param excludedProject the new excluded project
   */
  public void setExcludedProject(String excludedProject) {
    this.excludedProject = excludedProject;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "SearchDTO [objectId=" + objectId + ", projectNames=" + projectNames + ", procedureId="
        + procedureId + ", departmentsIDs=" + departmentsIDs + ", directoratesIDs="
        + directoratesIDs + ", description=" + description + ", pmDepartmentName="
        + pmDepartmentName + ", selective=" + selective + ", open=" + open + ", invitation="
        + invitation + ", negotiatedProcedure=" + negotiatedProcedure
        + ", negotiatedProcedureWithCompetition=" + negotiatedProcedureWithCompetition
        + ", negotiatedProcedureAboveThreshold=" + negotiatedProcedureAboveThreshold
        + ", constructionIndustry=" + constructionIndustry + ", relatedTrades=" + relatedTrades
        + ", supplyContracts=" + supplyContracts + ", dLAssignments=" + dLAssignments
        + ", isServiceTender=" + isServiceTender + ", companyName=" + companyName + ", running="
        + running + ", completed=" + completed + ", offerDateFrom=" + offerDateFrom
        + ", offerDateUntil=" + offerDateUntil + ", filter=" + filter + ", excludedProject="
        + excludedProject + "]";
  }
}
