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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Form for searching tenders.
 * <p>
 * We ignore unknown properties in order to facilitate using the same model for view and update at
 * client side.
 *
 * @author European Dynamics SA
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchForm {

  private String objectId;

  private List<String> projectNames;

  private String procedureId;

  private List<String> departmentsIDs;

  private List<String> directoratesIDs;

  private Set<MasterListValueHistoryForm> workTypes;

  private String description;

  private String pmDepartmentName;

  private Boolean selective;

  private Boolean open;

  private Boolean invitation;

  private Boolean negotiatedProcedure;

  private Boolean negotiatedProcedureWithCompetition;

  private Boolean negotiatedProcedureAboveThreshold;

  private Boolean constructionIndustry;

  private Boolean relatedTrades;

  private Boolean supplyContracts;

  private Boolean dLAssignments;

  private Boolean isServiceTender;

  private String companyName;

  private Boolean running;

  private Boolean completed;

  private Date offerDateFrom;

  private Date offerDateUntil;

  private FilterForm filter;

  private String excludedProject;

  private Date tenderCreationDate;

  private String documentTitle;


  public String getObjectId() {
    return objectId;
  }

  public void setObjectId(String objectId) {
    this.objectId = objectId;
  }

  public List<String> getProjectNames() {
    return projectNames;
  }

  public void setProjectNames(List<String> projectNames) {
    this.projectNames = projectNames;
  }

  public String getProcedureId() {
    return procedureId;
  }

  public void setProcedureId(String procedureId) {
    this.procedureId = procedureId;
  }

  public List<String> getDepartmentsIDs() {
    return departmentsIDs;
  }

  public void setDepartmentsIDs(List<String> departmentsIDs) {
    this.departmentsIDs = departmentsIDs;
  }

  public List<String> getDirectoratesIDs() {
    return directoratesIDs;
  }

  public void setDirectoratesIDs(List<String> directoratesIDs) {
    this.directoratesIDs = directoratesIDs;
  }

  public FilterForm getFilter() {
    return filter;
  }

  public void setFilter(FilterForm filter) {
    this.filter = filter;
  }

  public Set<MasterListValueHistoryForm> getWorkTypes() {
    return workTypes;
  }

  public void setWorkTypes(Set<MasterListValueHistoryForm> workTypes) {
    this.workTypes = workTypes;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getPmDepartmentName() {
    return pmDepartmentName;
  }

  public void setPmDepartmentName(String pmDepartmentName) {
    this.pmDepartmentName = pmDepartmentName;
  }

  public Boolean getSelective() {
    return selective;
  }

  public void setSelective(Boolean selective) {
    this.selective = selective;
  }

  public Boolean getOpen() {
    return open;
  }

  public void setOpen(Boolean open) {
    this.open = open;
  }

  public Boolean getInvitation() {
    return invitation;
  }

  public void setInvitation(Boolean invitation) {
    this.invitation = invitation;
  }

  public Boolean getNegotiatedProcedure() {
    return negotiatedProcedure;
  }

  public void setNegotiatedProcedure(Boolean negotiatedProcedure) {
    this.negotiatedProcedure = negotiatedProcedure;
  }

  public Boolean getNegotiatedProcedureWithCompetition() {
    return negotiatedProcedureWithCompetition;
  }

  public void setNegotiatedProcedureWithCompetition(Boolean negotiatedProcedureWithCompetition) {
    this.negotiatedProcedureWithCompetition = negotiatedProcedureWithCompetition;
  }

  public Boolean getNegotiatedProcedureAboveThreshold() {
    return negotiatedProcedureAboveThreshold;
  }

  public void setNegotiatedProcedureAboveThreshold(Boolean negotiatedProcedureAboveThreshold) {
    this.negotiatedProcedureAboveThreshold = negotiatedProcedureAboveThreshold;
  }

  public Boolean getConstructionIndustry() {
    return constructionIndustry;
  }

  public void setConstructionIndustry(Boolean constructionIndustry) {
    this.constructionIndustry = constructionIndustry;
  }

  public Boolean getRelatedTrades() {
    return relatedTrades;
  }

  public void setRelatedTrades(Boolean relatedTrades) {
    this.relatedTrades = relatedTrades;
  }

  public Boolean getSupplyContracts() {
    return supplyContracts;
  }

  public void setSupplyContracts(Boolean supplyContracts) {
    this.supplyContracts = supplyContracts;
  }

  public Boolean getdLAssignments() {
    return dLAssignments;
  }

  public void setdLAssignments(Boolean dLAssignments) {
    this.dLAssignments = dLAssignments;
  }

  public Boolean getIsServiceTender() {
    return isServiceTender;
  }

  public void setIsServiceTender(Boolean isServiceTender) {
    this.isServiceTender = isServiceTender;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public Boolean getRunning() {
    return running;
  }

  public void setRunning(Boolean running) {
    this.running = running;
  }

  public Boolean getCompleted() {
    return completed;
  }

  public void setCompleted(Boolean completed) {
    this.completed = completed;
  }

  public Date getOfferDateFrom() {
    return offerDateFrom;
  }

  public void setOfferDateFrom(Date offerDateFrom) {
    this.offerDateFrom = offerDateFrom;
  }

  public Date getOfferDateUntil() {
    return offerDateUntil;
  }

  public void setOfferDateUntil(Date offerDateUntil) {
    this.offerDateUntil = offerDateUntil;
  }

  public String getExcludedProject() {
    return excludedProject;
  }

  public void setExcludedProject(String excludedProject) {
    this.excludedProject = excludedProject;
  }

  public Date getTenderCreationDate() {
    return tenderCreationDate;
  }

  public void setTenderCreationDate(Date tenderCreationDate) {
    this.tenderCreationDate = tenderCreationDate;
  }

  public String getDocumentTitle() {
    return documentTitle;
  }

  public void setDocumentTitle(String documentTitle) {
    this.documentTitle = documentTitle;
  }
}
