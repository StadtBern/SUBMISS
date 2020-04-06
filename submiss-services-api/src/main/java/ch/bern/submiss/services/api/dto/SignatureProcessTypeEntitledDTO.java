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

/**
 * The Class SignatureProcessTypeEntitledDTO.
 */
public class SignatureProcessTypeEntitledDTO {

  /** The id. */
  private String id;

  /** The created on. */
  private Timestamp createdOn;

  /** The name. */
  private String name;

  /** The function. */
  private String function;

  /** The department. */
  private DepartmentDTO department;

  /** The department history. */
  private DepartmentHistoryDTO departmentHistory;

  /** The signature process type. */
  private SignatureProcessTypeDTO signatureProcessType;

  /** The sort Number. */
  private Integer sortNumber;

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
   * Gets the name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name.
   *
   * @param name the new name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the function.
   *
   * @return the function
   */
  public String getFunction() {
    return function;
  }

  /**
   * Sets the function.
   *
   * @param function the new function
   */
  public void setFunction(String function) {
    this.function = function;
  }

  /**
   * Gets the department.
   *
   * @return the department
   */
  public DepartmentDTO getDepartment() {
    return department;
  }

  /**
   * Sets the department.
   *
   * @param department the new department
   */
  public void setDepartment(DepartmentDTO department) {
    this.department = department;
  }

  /**
   * Gets the department history.
   *
   * @return the department history
   */
  public DepartmentHistoryDTO getDepartmentHistory() {
    return departmentHistory;
  }

  /**
   * Sets the department history.
   *
   * @param departmentHistory the new department history
   */
  public void setDepartmentHistory(DepartmentHistoryDTO departmentHistory) {
    this.departmentHistory = departmentHistory;
  }

  /**
   * Gets the signature process type.
   *
   * @return the signature process type
   */
  public SignatureProcessTypeDTO getSignatureProcessType() {
    return signatureProcessType;
  }

  /**
   * Sets the signature process type.
   *
   * @param signatureProcessType the new signature process type
   */
  public void setSignatureProcessType(SignatureProcessTypeDTO signatureProcessType) {
    this.signatureProcessType = signatureProcessType;
  }

  /**
   * Gets the sort number.
   *
   * @return the sort number
   */
  public Integer getSortNumber() {
    return sortNumber;
  }

  /**
   * Sets the sort number.
   *
   * @param sortNumber the new sort number
   */
  public void setSortNumber(Integer sortNumber) {
    this.sortNumber = sortNumber;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "SignatureProcessTypeEntitledDTO [id=" + id + ", createdOn=" + createdOn + ", name="
        + name + ", function=" + function + ", sortNumber=" + sortNumber + "]";
  }

}
