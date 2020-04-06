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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

/**
 * The Class SignatureProcessTypeEntitledEntity.
 */
@Entity
@Table(name = "SUB_SIGNATURE_PROCESS_TYPE_ENTITLED")
public class SignatureProcessTypeEntitledEntity {

  /** The id. */
  @Id
  @GeneratedValue(generator = "uuid1")
  @GenericGenerator(name = "uuid1", strategy = "uuid2")
  private String id;

  /** The created on. */
  @Column(name = "CREATE_ON")
  private Timestamp createdOn;

  /** The name. */
  private String name;

  /** The function. */
  private String function;


  /** The department. */
  @OneToOne
  @JoinColumn(name = "FK_DEPARTMENT")
  private DepartmentEntity department;

  /** The process type. */
  @ManyToOne
  @JoinColumn(name = "FK_SIGNATURE_PROCESS_TYPE")
  private SignatureProcessTypeEntity processType;

  /** The sort number. */
  @Column(name = "SORTNUMBER")
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
   * Gets the process type.
   *
   * @return the process type
   */
  public SignatureProcessTypeEntity getProcessType() {
    return processType;
  }

  /**
   * Sets the process type.
   *
   * @param processType the new process type
   */
  public void setProcessType(SignatureProcessTypeEntity processType) {
    this.processType = processType;
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
    return "SignatureProcessTypeEntitledEntity [id=" + id + ", createdOn=" + createdOn + ", name="
        + name + ", function=" + function + ",  sortNumber=" + sortNumber + "]";
  }

}
