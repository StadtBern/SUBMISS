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
import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

/**
 * The Class ProjectEntity.
 */
@Entity
@Table(name = "SUB_PROJECT")
public class ProjectEntity {

  /** The id. */
  @Id
  @GeneratedValue(generator = "uuid1")
  @GenericGenerator(name = "uuid1", strategy = "uuid2")
  private String id;

  /** The project name. */
  @Column(name = "NAME")
  private String projectName;

  /** The tenant. */
  @ManyToOne
  @JoinColumn(name = "FK_TENANT")
  private TenantEntity tenant;

  /** The object name. */
  @ManyToOne
  @JoinColumn(name = "FK_OBJECT")
  private MasterListValueEntity objectName;

  /** The procedure. */
  @ManyToOne
  @JoinColumn(name = "FK_PM_PROCEDURE")
  private MasterListValueEntity procedure;

  /** The gatt wto. */
  @Column(name = "GATT_WTO")
  private Boolean gattWto;

  /** The pm department name. */
  @Column(name = "PM_DEPARTMENT_NAME", length = 100)
  private String pmDepartmentName;

  /** The pm external. */
  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "FK_PM_EXTERNAL")
  private CompanyEntity pmExternal;

  /** The department. */
  @ManyToOne
  @JoinColumn(name = "FK_DEPARTMENT")
  private DepartmentEntity department;

  /** The project number. */
  @Column(name = "PROJECT_NUMBER", length = 50)
  private String projectNumber;

  /** The notes. */
  @Column(name = "NOTES", length = 100)
  private String notes;

  /** The construction permit. */
  @Column(name = "CONSTRUCTION_PERMIT", length = 4, columnDefinition = "TINYINT")
  @Enumerated
  private ConstructionPermit constructionPermit;

  /** The loan approval. */
  @Column(name = "LOAN_APPROVAL")
  @Enumerated
  private LoanApproval loanApproval;

  /** The created on. */
  @Column(name = "CREATED_ON")
  private Timestamp createdOn;


  /** The created by. */
  @Column(name = "CREATED_BY")
  private String createdBy;


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
  public TenantEntity getTenant() {
    return tenant;
  }

  /**
   * Sets the tenant.
   *
   * @param tenant the new tenant
   */
  public void setTenant(TenantEntity tenant) {
    this.tenant = tenant;
  }


  /**
   * Gets the object name.
   *
   * @return the object name
   */
  public MasterListValueEntity getObjectName() {
    return objectName;
  }

  /**
   * Sets the object name.
   *
   * @param objectName the new object name
   */
  public void setObjectName(MasterListValueEntity objectName) {
    this.objectName = objectName;
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
   * Gets the department.
   *
   * @return the department
   */
  public DepartmentEntity getDepartment() {
    return department;
  }

  /**
   * Sets the department.
   *
   * @param department the new department
   */
  public void setDepartment(DepartmentEntity department) {
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

  public MasterListValueHistoryEntity getRecentProcedureHistory() {
    for (MasterListValueHistoryEntity procedureHistoryEntity : procedure.getMasterListValueHistory()) {
      if (procedureHistoryEntity.getToDate() == null) {
        return procedureHistoryEntity;
      }
    }
    return null;
  }

  public MasterListValueHistoryEntity getRecentObjectNameHistory() {
    for (MasterListValueHistoryEntity objectNameHistoryEntity : objectName.getMasterListValueHistory()) {
      if (objectNameHistoryEntity.getToDate() == null) {
        return objectNameHistoryEntity;
      }
    }
    return null;
  }

  public DepartmentHistoryEntity getDepartmentHistory() {
    for (DepartmentHistoryEntity departmentHistoryEntity : department.getDepartment()) {
      if (departmentHistoryEntity.getToDate() == null) {
        return departmentHistoryEntity;
      }
    }
    return null;
  }
}
