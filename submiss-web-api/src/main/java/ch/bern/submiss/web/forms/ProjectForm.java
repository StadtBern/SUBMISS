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
import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Form for creating or updating a project.
 *
 * We ignore unknown properties in order to facilitate using the same model for view and update at
 * client side.
 *
 * @author European Dynamics SA
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectForm {

  private String id;

  private String projectName;

  private MasterListValueHistoryForm objectName;

  private MasterListValueHistoryForm procedure;

  private DepartmentHistoryForm department;

  private TenantForm tenant;

  private Boolean gattWto;

  private String pmDepartmentName;

  private CompanyForm pmExternal;

  private String projectNumber;

  private String notes;

  private ConstructionPermit constructionPermit;

  private LoanApproval loanApproval;

  private String createdBy;

  private Timestamp createdOn;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public TenantForm getTenant() {
    return tenant;
  }

  public void setTenant(TenantForm tenant) {
    this.tenant = tenant;
  }

  public MasterListValueHistoryForm getObjectName() {
    return objectName;
  }

  public void setObjectName(MasterListValueHistoryForm objectName) {
    this.objectName = objectName;
  }

  public MasterListValueHistoryForm getProcedure() {
    return procedure;
  }

  public void setProcedure(MasterListValueHistoryForm procedure) {
    this.procedure = procedure;
  }

  public Boolean getGattWto() {
    return gattWto;
  }

  public void setGattWto(Boolean gattWto) {
    this.gattWto = gattWto;
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

  public String getProjectNumber() {
    return projectNumber;
  }

  public void setProjectNumber(String projectNumber) {
    this.projectNumber = projectNumber;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
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

  public DepartmentHistoryForm getDepartment() {
    return department;
  }

  public void setDepartment(DepartmentHistoryForm department) {
    this.department = department;
  }

  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
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
}
