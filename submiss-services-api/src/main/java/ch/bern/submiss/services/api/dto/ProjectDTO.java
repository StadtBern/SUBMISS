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
import com.fasterxml.jackson.annotation.JsonView;
import ch.bern.submiss.services.api.constants.ConstructionPermit;
import ch.bern.submiss.services.api.constants.LoanApproval;
import ch.bern.submiss.services.api.util.View;

/**
 * The Class ProjectDTO.
 */
public class ProjectDTO {

  /** The id. */
  @JsonView(View.Internal.class)
  private String id;

  /** The project name. */
  @JsonView(View.Public.class)
  private String projectName;

  /** The tenant. */
  @JsonView(View.Internal.class)
  private TenantDTO tenant;

  /** The object name. */
  @JsonView(View.Public.class)
  private MasterListValueHistoryDTO objectName;

  /** The procedure. */
  @JsonView(View.Internal.class)
  private MasterListValueHistoryDTO procedure;

  /** The gatt wto. */
  @JsonView(View.Internal.class)
  private Boolean gattWto;

  /** The pm department name. */
  @JsonView(View.Internal.class)
  private String pmDepartmentName;

  /** The pm external. */
  @JsonView(View.Internal.class)
  private CompanyDTO pmExternal;

  /** The department. */
  @JsonView(View.Internal.class)
  private DepartmentHistoryDTO department;

  /** The project number. */
  @JsonView(View.Internal.class)
  private String projectNumber;

  /** The notes. */
  @JsonView(View.Internal.class)
  private String notes;

  /** The construction permit. */
  @JsonView(View.Internal.class)
  private ConstructionPermit constructionPermit;

  /** The loan approval. */
  @JsonView(View.Internal.class)
  private LoanApproval loanApproval;

  /** The create by. */
  @JsonView(View.Internal.class)
  private String createdBy;

  /** The created on. */
  @JsonView(View.Internal.class)
  private Timestamp createdOn;

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
   * Gets the project name.
   *
   * @return the project name
   */
  public String getProjectName() {
    return projectName;
  }

  /**
   * Sets the project name.
   *
   * @param projectName the new project name
   */
  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  /**
   * Gets the tenant.
   *
   * @return the tenant
   */
  public TenantDTO getTenant() {
    return tenant;
  }

  /**
   * Sets the tenant.
   *
   * @param tenant the new tenant
   */
  public void setTenant(TenantDTO tenant) {
    this.tenant = tenant;
  }

  /**
   * Gets the object name.
   *
   * @return the object name
   */
  public MasterListValueHistoryDTO getObjectName() {
    return objectName;
  }

  /**
   * Sets the object name.
   *
   * @param objectName the new object name
   */
  public void setObjectName(MasterListValueHistoryDTO objectName) {
    this.objectName = objectName;
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
   * Gets the gatt wto.
   *
   * @return the gatt wto
   */
  public Boolean getGattWto() {
    return gattWto;
  }

  /**
   * Sets the gatt wto.
   *
   * @param gattWto the new gatt wto
   */
  public void setGattWto(Boolean gattWto) {
    this.gattWto = gattWto;
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
   * Gets the department.
   *
   * @return the department
   */
  public DepartmentHistoryDTO getDepartment() {
    return department;
  }

  /**
   * Sets the department.
   *
   * @param department the new department
   */
  public void setDepartment(DepartmentHistoryDTO department) {
    this.department = department;
  }

  /**
   * Gets the project number.
   *
   * @return the project number
   */
  public String getProjectNumber() {
    return projectNumber;
  }

  /**
   * Sets the project number.
   *
   * @param projectNumber the new project number
   */
  public void setProjectNumber(String projectNumber) {
    this.projectNumber = projectNumber;
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

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "ProjectDTO [id=" + id + ", projectName=" + projectName + ", gattWto=" + gattWto
        + ", pmDepartmentName=" + pmDepartmentName + ", projectNumber=" + projectNumber + ", notes="
        + notes + ", constructionPermit=" + ", createdBy=" + createdBy + ", createdOn=" + createdOn
        + "]";
  }



}
